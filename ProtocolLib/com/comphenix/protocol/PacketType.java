/*     */ package com.comphenix.protocol;
/*     */ 
/*     */ import com.comphenix.protocol.events.ConnectionSide;
/*     */ import com.comphenix.protocol.injector.packet.PacketRegistry;
/*     */ import com.comphenix.protocol.reflect.ObjectEnum;
/*     */ import com.comphenix.protocol.utility.MinecraftReflection;
/*     */ import com.comphenix.protocol.utility.MinecraftVersion;
/*     */ import com.google.common.base.Objects;
/*     */ import com.google.common.base.Preconditions;
/*     */ import com.google.common.collect.ComparisonChain;
/*     */ import com.google.common.collect.Iterables;
/*     */ import com.google.common.collect.Lists;
/*     */ import com.google.common.util.concurrent.Futures;
/*     */ import com.google.common.util.concurrent.ListeningScheduledExecutorService;
/*     */ import java.io.Serializable;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collection;
/*     */ import java.util.List;
/*     */ import java.util.UUID;
/*     */ import java.util.concurrent.Callable;
/*     */ import java.util.concurrent.Future;
/*     */ import org.bukkit.Bukkit;
/*     */ 
/*     */ public class PacketType
/*     */   implements Serializable, Comparable<PacketType>
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   public static final int UNKNOWN_PACKET = -1;
/*     */   private static PacketTypeLookup LOOKUP;
/* 519 */   private static final MinecraftVersion PROTOCOL_VERSION = MinecraftVersion.BOUNTIFUL_UPDATE;
/*     */   private final Protocol protocol;
/*     */   private final Sender sender;
/*     */   private final int currentId;
/*     */   private final int legacyId;
/*     */   private final MinecraftVersion version;
/*     */ 
/*     */   private static PacketTypeLookup getLookup()
/*     */   {
/* 532 */     if (LOOKUP == null) {
/* 533 */       LOOKUP = new PacketTypeLookup().addPacketTypes(PacketType.Handshake.Client.getInstance()).addPacketTypes(PacketType.Handshake.Server.getInstance()).addPacketTypes(PacketType.Play.Client.getInstance()).addPacketTypes(PacketType.Play.Server.getInstance()).addPacketTypes(PacketType.Status.Client.getInstance()).addPacketTypes(PacketType.Status.Server.getInstance()).addPacketTypes(PacketType.Login.Client.getInstance()).addPacketTypes(PacketType.Login.Server.getInstance()).addPacketTypes(PacketType.Legacy.Client.getInstance()).addPacketTypes(PacketType.Legacy.Server.getInstance());
/*     */     }
/*     */ 
/* 545 */     return LOOKUP;
/*     */   }
/*     */ 
/*     */   public static Iterable<PacketType> values()
/*     */   {
/* 553 */     List sources = Lists.newArrayList();
/* 554 */     sources.add(PacketType.Handshake.Client.getInstance());
/* 555 */     sources.add(PacketType.Handshake.Server.getInstance());
/* 556 */     sources.add(PacketType.Play.Client.getInstance());
/* 557 */     sources.add(PacketType.Play.Server.getInstance());
/* 558 */     sources.add(PacketType.Status.Client.getInstance());
/* 559 */     sources.add(PacketType.Status.Server.getInstance());
/* 560 */     sources.add(PacketType.Login.Client.getInstance());
/* 561 */     sources.add(PacketType.Login.Server.getInstance());
/*     */ 
/* 564 */     if (!MinecraftReflection.isUsingNetty()) {
/* 565 */       sources.add(PacketType.Legacy.Client.getInstance());
/* 566 */       sources.add(PacketType.Legacy.Server.getInstance());
/*     */     }
/* 568 */     return Iterables.concat(sources);
/*     */   }
/*     */ 
/*     */   public static PacketType findLegacy(int packetId)
/*     */   {
/* 578 */     PacketType type = getLookup().getFromLegacy(packetId);
/*     */ 
/* 580 */     if (type != null)
/* 581 */       return type;
/* 582 */     throw new IllegalArgumentException("Cannot find legacy packet " + packetId);
/*     */   }
/*     */ 
/*     */   public static PacketType findLegacy(int packetId, Sender preference)
/*     */   {
/* 593 */     if (preference == null)
/* 594 */       return findLegacy(packetId);
/* 595 */     PacketType type = getLookup().getFromLegacy(packetId, preference);
/*     */ 
/* 597 */     if (type != null)
/* 598 */       return type;
/* 599 */     throw new IllegalArgumentException("Cannot find legacy packet " + packetId);
/*     */   }
/*     */ 
/*     */   public static boolean hasLegacy(int packetId)
/*     */   {
/* 609 */     return getLookup().getFromLegacy(packetId) != null;
/*     */   }
/*     */ 
/*     */   public static PacketType findCurrent(Protocol protocol, Sender sender, int packetId)
/*     */   {
/* 626 */     PacketType type = getLookup().getFromCurrent(protocol, sender, packetId);
/*     */ 
/* 628 */     if (type != null)
/* 629 */       return type;
/* 630 */     throw new IllegalArgumentException("Cannot find packet " + packetId + "(Protocol: " + protocol + ", Sender: " + sender + ")");
/*     */   }
/*     */ 
/*     */   public static boolean hasCurrent(Protocol protocol, Sender sender, int packetId)
/*     */   {
/* 642 */     return getLookup().getFromCurrent(protocol, sender, packetId) != null;
/*     */   }
/*     */ 
/*     */   public static PacketType fromLegacy(int id, Sender sender)
/*     */   {
/* 655 */     PacketType type = getLookup().getFromLegacy(id, sender);
/*     */ 
/* 657 */     if (type == null) {
/* 658 */       if (sender == null)
/* 659 */         throw new IllegalArgumentException("Cannot find legacy packet " + id);
/* 660 */       type = newLegacy(sender, id);
/*     */ 
/* 663 */       scheduleRegister(type, "Dynamic-" + UUID.randomUUID().toString());
/*     */     }
/* 665 */     return type;
/*     */   }
/*     */ 
/*     */   public static PacketType fromCurrent(Protocol protocol, Sender sender, int packetId, int legacyId)
/*     */   {
/* 679 */     PacketType type = getLookup().getFromCurrent(protocol, sender, packetId);
/*     */ 
/* 681 */     if (type == null) {
/* 682 */       type = new PacketType(protocol, sender, packetId, legacyId);
/*     */ 
/* 685 */       scheduleRegister(type, "Dynamic-" + UUID.randomUUID().toString());
/*     */     }
/* 687 */     return type;
/*     */   }
/*     */ 
/*     */   public static PacketType fromClass(Class<?> packetClass)
/*     */   {
/* 696 */     PacketType type = PacketRegistry.getPacketType(packetClass);
/*     */ 
/* 698 */     if (type != null)
/* 699 */       return type;
/* 700 */     throw new IllegalArgumentException("Class " + packetClass + " is not a registered packet.");
/*     */   }
/*     */ 
/*     */   public static Collection<PacketType> fromName(String name)
/*     */   {
/* 711 */     return getLookup().getFromName(name);
/*     */   }
/*     */ 
/*     */   public static boolean hasClass(Class<?> packetClass)
/*     */   {
/* 720 */     return PacketRegistry.getPacketType(packetClass) != null;
/*     */   }
/*     */ 
/*     */   public static Future<Boolean> scheduleRegister(PacketType type, final String name)
/*     */   {
/* 732 */     Callable callable = new Callable()
/*     */     {
/*     */       public Boolean call()
/*     */         throws Exception
/*     */       {
/* 738 */         ObjectEnum objEnum = PacketType.getObjectEnum(this.val$type);
/*     */ 
/* 740 */         if (objEnum.registerMember(this.val$type, name)) {
/* 741 */           PacketType.access$400().addPacketTypes(Arrays.asList(new PacketType[] { this.val$type }));
/* 742 */           return Boolean.valueOf(true);
/*     */         }
/* 744 */         return Boolean.valueOf(false);
/*     */       }
/*     */     };
/* 749 */     if ((Bukkit.getServer() == null) || (Application.isPrimaryThread())) {
/*     */       try {
/* 751 */         return Futures.immediateFuture(callable.call());
/*     */       } catch (Exception e) {
/* 753 */         return Futures.immediateFailedFuture(e);
/*     */       }
/*     */     }
/* 756 */     return ProtocolLibrary.getExecutorSync().submit(callable);
/*     */   }
/*     */ 
/*     */   public static ObjectEnum<PacketType> getObjectEnum(PacketType type)
/*     */   {
/* 765 */     switch (2.$SwitchMap$com$comphenix$protocol$PacketType$Protocol[type.getProtocol().ordinal()]) {
/*     */     case 1:
/* 767 */       return type.isClient() ? PacketType.Handshake.Client.getInstance() : PacketType.Handshake.Server.getInstance();
/*     */     case 2:
/* 769 */       return type.isClient() ? PacketType.Play.Client.getInstance() : PacketType.Play.Server.getInstance();
/*     */     case 3:
/* 771 */       return type.isClient() ? PacketType.Status.Client.getInstance() : PacketType.Status.Server.getInstance();
/*     */     case 4:
/* 773 */       return type.isClient() ? PacketType.Login.Client.getInstance() : PacketType.Login.Server.getInstance();
/*     */     case 5:
/* 775 */       return type.isClient() ? PacketType.Legacy.Client.getInstance() : PacketType.Legacy.Server.getInstance();
/*     */     }
/* 777 */     throw new IllegalStateException("Unexpected protocol: " + type.getProtocol());
/*     */   }
/*     */ 
/*     */   public PacketType(Protocol protocol, Sender sender, int currentId, int legacyId)
/*     */   {
/* 789 */     this(protocol, sender, currentId, legacyId, PROTOCOL_VERSION);
/*     */   }
/*     */ 
/*     */   public PacketType(Protocol protocol, Sender sender, int currentId, int legacyId, MinecraftVersion version)
/*     */   {
/* 801 */     this.protocol = ((Protocol)Preconditions.checkNotNull(protocol, "protocol cannot be NULL"));
/* 802 */     this.sender = ((Sender)Preconditions.checkNotNull(sender, "sender cannot be NULL"));
/* 803 */     this.currentId = currentId;
/* 804 */     this.legacyId = legacyId;
/* 805 */     this.version = version;
/*     */   }
/*     */ 
/*     */   public static PacketType newLegacy(Sender sender, int legacyId)
/*     */   {
/* 814 */     return new PacketType(Protocol.LEGACY, sender, -1, legacyId, MinecraftVersion.WORLD_UPDATE);
/*     */   }
/*     */ 
/*     */   public boolean isSupported()
/*     */   {
/* 822 */     return PacketRegistry.isSupported(this);
/*     */   }
/*     */ 
/*     */   public Protocol getProtocol()
/*     */   {
/* 830 */     return this.protocol;
/*     */   }
/*     */ 
/*     */   public Sender getSender()
/*     */   {
/* 838 */     return this.sender;
/*     */   }
/*     */ 
/*     */   public boolean isClient()
/*     */   {
/* 846 */     return this.sender == Sender.CLIENT;
/*     */   }
/*     */ 
/*     */   public boolean isServer()
/*     */   {
/* 854 */     return this.sender == Sender.SERVER;
/*     */   }
/*     */ 
/*     */   public int getCurrentId()
/*     */   {
/* 866 */     return this.currentId;
/*     */   }
/*     */ 
/*     */   public Class<?> getPacketClass()
/*     */   {
/*     */     try
/*     */     {
/* 875 */       return PacketRegistry.getPacketClassFromType(this); } catch (Exception e) {
/*     */     }
/* 877 */     return null;
/*     */   }
/*     */ 
/*     */   public String name()
/*     */   {
/* 886 */     return getObjectEnum(this).getDeclaredName(this);
/*     */   }
/*     */ 
/*     */   public MinecraftVersion getCurrentVersion()
/*     */   {
/* 894 */     return this.version;
/*     */   }
/*     */ 
/*     */   public int getLegacyId()
/*     */   {
/* 904 */     return this.legacyId;
/*     */   }
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 909 */     return Objects.hashCode(new Object[] { this.protocol, this.sender, Integer.valueOf(this.currentId), Integer.valueOf(this.legacyId) });
/*     */   }
/*     */ 
/*     */   public boolean equals(Object obj)
/*     */   {
/* 914 */     if (obj == this) {
/* 915 */       return true;
/*     */     }
/* 917 */     if ((obj instanceof PacketType)) {
/* 918 */       PacketType other = (PacketType)obj;
/* 919 */       return (this.protocol == other.protocol) && (this.sender == other.sender) && (this.currentId == other.currentId) && (this.legacyId == other.legacyId);
/*     */     }
/*     */ 
/* 924 */     return false;
/*     */   }
/*     */ 
/*     */   public int compareTo(PacketType other)
/*     */   {
/* 929 */     return ComparisonChain.start().compare(this.protocol, other.getProtocol()).compare(this.sender, other.getSender()).compare(this.currentId, other.getCurrentId()).compare(this.legacyId, other.getLegacyId()).result();
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 939 */     Class clazz = getPacketClass();
/*     */ 
/* 941 */     if (clazz == null) {
/* 942 */       return "UNREGISTERED [" + this.protocol + ", " + this.sender + ", " + this.currentId + ", legacy: " + this.legacyId + "]";
/*     */     }
/* 944 */     return clazz.getSimpleName() + "[" + this.currentId + ", legacy: " + this.legacyId + "]";
/*     */   }
/*     */ 
/*     */   public static enum Sender
/*     */   {
/* 497 */     CLIENT, 
/*     */ 
/* 502 */     SERVER;
/*     */ 
/*     */     public ConnectionSide toSide()
/*     */     {
/* 509 */       return this == CLIENT ? ConnectionSide.CLIENT_SIDE : ConnectionSide.SERVER_SIDE;
/*     */     }
/*     */   }
/*     */ 
/*     */   public static enum Protocol
/*     */   {
/* 458 */     HANDSHAKING, 
/* 459 */     PLAY, 
/* 460 */     STATUS, 
/* 461 */     LOGIN, 
/*     */ 
/* 466 */     LEGACY;
/*     */ 
/*     */     public static Protocol fromVanilla(Enum<?> vanilla)
/*     */     {
/* 474 */       String name = vanilla.name();
/*     */ 
/* 476 */       if ("HANDSHAKING".equals(name))
/* 477 */         return HANDSHAKING;
/* 478 */       if ("PLAY".equals(name))
/* 479 */         return PLAY;
/* 480 */       if ("STATUS".equals(name))
/* 481 */         return STATUS;
/* 482 */       if ("LOGIN".equals(name))
/* 483 */         return LOGIN;
/* 484 */       throw new IllegalArgumentException("Unrecognized vanilla enum " + vanilla);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static class Legacy
/*     */   {
/* 381 */     private static final PacketType.Protocol PROTOCOL = PacketType.Protocol.LEGACY;
/*     */ 
/*     */     public static PacketType.Protocol getProtocol()
/*     */     {
/* 449 */       return PROTOCOL;
/*     */     }
/*     */ 
/*     */     public static class Client extends ObjectEnum<PacketType>
/*     */     {
/* 429 */       private static final PacketType.Sender SENDER = PacketType.Sender.CLIENT;
/*     */ 
/* 431 */       public static final PacketType LOGIN = PacketType.newLegacy(SENDER, 1);
/* 432 */       public static final PacketType RESPAWN = PacketType.newLegacy(SENDER, 9);
/* 433 */       public static final PacketType DISCONNECT = PacketType.newLegacy(SENDER, 255);
/*     */ 
/* 435 */       private static final Client INSTANCE = new Client();
/*     */ 
/*     */       private Client() {
/* 438 */         super();
/*     */       }
/*     */       public static PacketType.Sender getSender() {
/* 441 */         return SENDER;
/*     */       }
/*     */       public static Client getInstance() {
/* 444 */         return INSTANCE;
/*     */       }
/*     */     }
/*     */ 
/*     */     public static class Server extends ObjectEnum<PacketType>
/*     */     {
/* 389 */       private static final PacketType.Sender SENDER = PacketType.Sender.SERVER;
/*     */ 
/* 391 */       public static final PacketType PLAYER_FLYING = PacketType.newLegacy(SENDER, 10);
/* 392 */       public static final PacketType PLAYER_POSITION = PacketType.newLegacy(SENDER, 11);
/* 393 */       public static final PacketType PLAYER_POSITON_LOOK = PacketType.newLegacy(SENDER, 12);
/*     */ 
/* 397 */       public static final PacketType PICKUP_SPAWN = PacketType.newLegacy(SENDER, 21);
/*     */ 
/* 401 */       public static final PacketType SET_CREATIVE_SLOT = PacketType.newLegacy(SENDER, 107);
/*     */ 
/* 406 */       public static final PacketType KEY_RESPONSE = PacketType.newLegacy(SENDER, 252);
/*     */ 
/* 408 */       private static final Server INSTANCE = new Server();
/*     */ 
/*     */       private Server()
/*     */       {
/* 412 */         super();
/*     */       }
/*     */ 
/*     */       public static PacketType.Sender getSender() {
/* 416 */         return SENDER;
/*     */       }
/*     */       public static Server getInstance() {
/* 419 */         return INSTANCE;
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public static class Login
/*     */   {
/* 321 */     private static final PacketType.Protocol PROTOCOL = PacketType.Protocol.LOGIN;
/*     */ 
/*     */     public static PacketType.Protocol getProtocol()
/*     */     {
/* 372 */       return PROTOCOL;
/*     */     }
/*     */ 
/*     */     public static class Client extends ObjectEnum<PacketType>
/*     */     {
/* 353 */       private static final PacketType.Sender SENDER = PacketType.Sender.CLIENT;
/*     */ 
/* 355 */       public static final PacketType START = new PacketType(PacketType.Login.PROTOCOL, SENDER, 0, 231);
/* 356 */       public static final PacketType ENCRYPTION_BEGIN = new PacketType(PacketType.Login.PROTOCOL, SENDER, 1, 252);
/*     */ 
/* 358 */       private static final Client INSTANCE = new Client();
/*     */ 
/*     */       private Client() {
/* 361 */         super();
/*     */       }
/*     */       public static PacketType.Sender getSender() {
/* 364 */         return SENDER;
/*     */       }
/*     */       public static Client getInstance() {
/* 367 */         return INSTANCE;
/*     */       }
/*     */     }
/*     */ 
/*     */     public static class Server extends ObjectEnum<PacketType>
/*     */     {
/* 328 */       private static final PacketType.Sender SENDER = PacketType.Sender.SERVER;
/*     */ 
/* 330 */       public static final PacketType DISCONNECT = new PacketType(PacketType.Login.PROTOCOL, SENDER, 0, 255);
/* 331 */       public static final PacketType ENCRYPTION_BEGIN = new PacketType(PacketType.Login.PROTOCOL, SENDER, 1, 253);
/* 332 */       public static final PacketType SUCCESS = new PacketType(PacketType.Login.PROTOCOL, SENDER, 2, 232);
/* 333 */       public static final PacketType SET_COMPRESSION = new PacketType(PacketType.Login.PROTOCOL, SENDER, 3, -1);
/*     */ 
/* 335 */       private static final Server INSTANCE = new Server();
/*     */ 
/*     */       private Server() {
/* 338 */         super();
/*     */       }
/*     */       public static PacketType.Sender getSender() {
/* 341 */         return SENDER;
/*     */       }
/*     */       public static Server getInstance() {
/* 344 */         return INSTANCE;
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public static class Status
/*     */   {
/* 263 */     private static final PacketType.Protocol PROTOCOL = PacketType.Protocol.STATUS;
/*     */ 
/*     */     public static PacketType.Protocol getProtocol()
/*     */     {
/* 312 */       return PROTOCOL;
/*     */     }
/*     */ 
/*     */     public static class Client extends ObjectEnum<PacketType>
/*     */     {
/* 293 */       private static final PacketType.Sender SENDER = PacketType.Sender.CLIENT;
/*     */ 
/* 295 */       public static final PacketType IN_START = new PacketType(PacketType.Status.PROTOCOL, SENDER, 0, 254);
/* 296 */       public static final PacketType IN_PING = new PacketType(PacketType.Status.PROTOCOL, SENDER, 1, 230);
/*     */ 
/* 298 */       private static final Client INSTANCE = new Client();
/*     */ 
/*     */       private Client() {
/* 301 */         super();
/*     */       }
/*     */       public static PacketType.Sender getSender() {
/* 304 */         return SENDER;
/*     */       }
/*     */       public static Client getInstance() {
/* 307 */         return INSTANCE;
/*     */       }
/*     */     }
/*     */ 
/*     */     public static class Server extends ObjectEnum<PacketType>
/*     */     {
/* 270 */       private static final PacketType.Sender SENDER = PacketType.Sender.SERVER;
/*     */ 
/* 272 */       public static final PacketType OUT_SERVER_INFO = new PacketType(PacketType.Status.PROTOCOL, SENDER, 0, 255);
/* 273 */       public static final PacketType OUT_PING = new PacketType(PacketType.Status.PROTOCOL, SENDER, 1, 230);
/*     */ 
/* 275 */       private static final Server INSTANCE = new Server();
/*     */ 
/*     */       private Server() {
/* 278 */         super();
/*     */       }
/*     */       public static PacketType.Sender getSender() {
/* 281 */         return SENDER;
/*     */       }
/*     */       public static Server getInstance() {
/* 284 */         return INSTANCE;
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public static class Play
/*     */   {
/*  99 */     private static final PacketType.Protocol PROTOCOL = PacketType.Protocol.PLAY;
/*     */ 
/*     */     public static PacketType.Protocol getProtocol()
/*     */     {
/* 254 */       return PROTOCOL;
/*     */     }
/*     */ 
/*     */     public static class Client extends ObjectEnum<PacketType>
/*     */     {
/* 211 */       private static final PacketType.Sender SENDER = PacketType.Sender.CLIENT;
/*     */ 
/* 213 */       public static final PacketType KEEP_ALIVE = new PacketType(PacketType.Play.PROTOCOL, SENDER, 0, 0);
/* 214 */       public static final PacketType CHAT = new PacketType(PacketType.Play.PROTOCOL, SENDER, 1, 3);
/* 215 */       public static final PacketType USE_ENTITY = new PacketType(PacketType.Play.PROTOCOL, SENDER, 2, 7);
/* 216 */       public static final PacketType FLYING = new PacketType(PacketType.Play.PROTOCOL, SENDER, 3, 10);
/* 217 */       public static final PacketType POSITION = new PacketType(PacketType.Play.PROTOCOL, SENDER, 4, 11);
/* 218 */       public static final PacketType LOOK = new PacketType(PacketType.Play.PROTOCOL, SENDER, 5, 12);
/* 219 */       public static final PacketType POSITION_LOOK = new PacketType(PacketType.Play.PROTOCOL, SENDER, 6, 13);
/* 220 */       public static final PacketType BLOCK_DIG = new PacketType(PacketType.Play.PROTOCOL, SENDER, 7, 14);
/* 221 */       public static final PacketType BLOCK_PLACE = new PacketType(PacketType.Play.PROTOCOL, SENDER, 8, 15);
/* 222 */       public static final PacketType HELD_ITEM_SLOT = new PacketType(PacketType.Play.PROTOCOL, SENDER, 9, 16);
/* 223 */       public static final PacketType ARM_ANIMATION = new PacketType(PacketType.Play.PROTOCOL, SENDER, 10, 18);
/* 224 */       public static final PacketType ENTITY_ACTION = new PacketType(PacketType.Play.PROTOCOL, SENDER, 11, 19);
/* 225 */       public static final PacketType STEER_VEHICLE = new PacketType(PacketType.Play.PROTOCOL, SENDER, 12, 27);
/* 226 */       public static final PacketType CLOSE_WINDOW = new PacketType(PacketType.Play.PROTOCOL, SENDER, 13, 101);
/* 227 */       public static final PacketType WINDOW_CLICK = new PacketType(PacketType.Play.PROTOCOL, SENDER, 14, 102);
/* 228 */       public static final PacketType TRANSACTION = new PacketType(PacketType.Play.PROTOCOL, SENDER, 15, 106);
/* 229 */       public static final PacketType SET_CREATIVE_SLOT = new PacketType(PacketType.Play.PROTOCOL, SENDER, 16, 107);
/* 230 */       public static final PacketType ENCHANT_ITEM = new PacketType(PacketType.Play.PROTOCOL, SENDER, 17, 108);
/* 231 */       public static final PacketType UPDATE_SIGN = new PacketType(PacketType.Play.PROTOCOL, SENDER, 18, 130);
/* 232 */       public static final PacketType ABILITIES = new PacketType(PacketType.Play.PROTOCOL, SENDER, 19, 202);
/* 233 */       public static final PacketType TAB_COMPLETE = new PacketType(PacketType.Play.PROTOCOL, SENDER, 20, 203);
/* 234 */       public static final PacketType SETTINGS = new PacketType(PacketType.Play.PROTOCOL, SENDER, 21, 204);
/* 235 */       public static final PacketType CLIENT_COMMAND = new PacketType(PacketType.Play.PROTOCOL, SENDER, 22, 205);
/* 236 */       public static final PacketType CUSTOM_PAYLOAD = new PacketType(PacketType.Play.PROTOCOL, SENDER, 23, 250);
/* 237 */       public static final PacketType SPECTATE = new PacketType(PacketType.Play.PROTOCOL, SENDER, 24, -1);
/* 238 */       public static final PacketType RESOURCE_PACK_STATUS = new PacketType(PacketType.Play.PROTOCOL, SENDER, 25, -1);
/*     */ 
/* 240 */       private static final Client INSTANCE = new Client();
/*     */ 
/*     */       private Client() {
/* 243 */         super();
/*     */       }
/*     */       public static PacketType.Sender getSender() {
/* 246 */         return SENDER;
/*     */       }
/*     */       public static Client getInstance() {
/* 249 */         return INSTANCE;
/*     */       }
/*     */     }
/*     */ 
/*     */     public static class Server extends ObjectEnum<PacketType>
/*     */     {
/* 106 */       private static final PacketType.Sender SENDER = PacketType.Sender.SERVER;
/*     */ 
/* 108 */       public static final PacketType KEEP_ALIVE = new PacketType(PacketType.Play.PROTOCOL, SENDER, 0, 0);
/* 109 */       public static final PacketType LOGIN = new PacketType(PacketType.Play.PROTOCOL, SENDER, 1, 1);
/* 110 */       public static final PacketType CHAT = new PacketType(PacketType.Play.PROTOCOL, SENDER, 2, 3);
/* 111 */       public static final PacketType UPDATE_TIME = new PacketType(PacketType.Play.PROTOCOL, SENDER, 3, 4);
/* 112 */       public static final PacketType ENTITY_EQUIPMENT = new PacketType(PacketType.Play.PROTOCOL, SENDER, 4, 5);
/* 113 */       public static final PacketType SPAWN_POSITION = new PacketType(PacketType.Play.PROTOCOL, SENDER, 5, 6);
/* 114 */       public static final PacketType UPDATE_HEALTH = new PacketType(PacketType.Play.PROTOCOL, SENDER, 6, 8);
/* 115 */       public static final PacketType RESPAWN = new PacketType(PacketType.Play.PROTOCOL, SENDER, 7, 9);
/* 116 */       public static final PacketType POSITION = new PacketType(PacketType.Play.PROTOCOL, SENDER, 8, 13);
/* 117 */       public static final PacketType HELD_ITEM_SLOT = new PacketType(PacketType.Play.PROTOCOL, SENDER, 9, 16);
/*     */ 
/* 121 */       public static final PacketType BED = new PacketType(PacketType.Play.PROTOCOL, SENDER, 10, 17);
/* 122 */       public static final PacketType ANIMATION = new PacketType(PacketType.Play.PROTOCOL, SENDER, 11, 18);
/* 123 */       public static final PacketType NAMED_ENTITY_SPAWN = new PacketType(PacketType.Play.PROTOCOL, SENDER, 12, 20);
/* 124 */       public static final PacketType COLLECT = new PacketType(PacketType.Play.PROTOCOL, SENDER, 13, 22);
/* 125 */       public static final PacketType SPAWN_ENTITY = new PacketType(PacketType.Play.PROTOCOL, SENDER, 14, 23);
/* 126 */       public static final PacketType SPAWN_ENTITY_LIVING = new PacketType(PacketType.Play.PROTOCOL, SENDER, 15, 24);
/* 127 */       public static final PacketType SPAWN_ENTITY_PAINTING = new PacketType(PacketType.Play.PROTOCOL, SENDER, 16, 25);
/* 128 */       public static final PacketType SPAWN_ENTITY_EXPERIENCE_ORB = new PacketType(PacketType.Play.PROTOCOL, SENDER, 17, 26);
/*     */ 
/* 130 */       public static final PacketType ENTITY_VELOCITY = new PacketType(PacketType.Play.PROTOCOL, SENDER, 18, 28);
/* 131 */       public static final PacketType ENTITY_DESTROY = new PacketType(PacketType.Play.PROTOCOL, SENDER, 19, 29);
/* 132 */       public static final PacketType ENTITY = new PacketType(PacketType.Play.PROTOCOL, SENDER, 20, 30);
/* 133 */       public static final PacketType REL_ENTITY_MOVE = new PacketType(PacketType.Play.PROTOCOL, SENDER, 21, 31);
/* 134 */       public static final PacketType ENTITY_LOOK = new PacketType(PacketType.Play.PROTOCOL, SENDER, 22, 32);
/* 135 */       public static final PacketType ENTITY_MOVE_LOOK = new PacketType(PacketType.Play.PROTOCOL, SENDER, 23, 33);
/* 136 */       public static final PacketType ENTITY_TELEPORT = new PacketType(PacketType.Play.PROTOCOL, SENDER, 24, 34);
/* 137 */       public static final PacketType ENTITY_HEAD_ROTATION = new PacketType(PacketType.Play.PROTOCOL, SENDER, 25, 35);
/* 138 */       public static final PacketType ENTITY_STATUS = new PacketType(PacketType.Play.PROTOCOL, SENDER, 26, 38);
/* 139 */       public static final PacketType ATTACH_ENTITY = new PacketType(PacketType.Play.PROTOCOL, SENDER, 27, 39);
/* 140 */       public static final PacketType ENTITY_METADATA = new PacketType(PacketType.Play.PROTOCOL, SENDER, 28, 40);
/* 141 */       public static final PacketType ENTITY_EFFECT = new PacketType(PacketType.Play.PROTOCOL, SENDER, 29, 41);
/* 142 */       public static final PacketType REMOVE_ENTITY_EFFECT = new PacketType(PacketType.Play.PROTOCOL, SENDER, 30, 42);
/* 143 */       public static final PacketType EXPERIENCE = new PacketType(PacketType.Play.PROTOCOL, SENDER, 31, 43);
/* 144 */       public static final PacketType UPDATE_ATTRIBUTES = new PacketType(PacketType.Play.PROTOCOL, SENDER, 32, 44);
/* 145 */       public static final PacketType MAP_CHUNK = new PacketType(PacketType.Play.PROTOCOL, SENDER, 33, 51);
/* 146 */       public static final PacketType MULTI_BLOCK_CHANGE = new PacketType(PacketType.Play.PROTOCOL, SENDER, 34, 52);
/* 147 */       public static final PacketType BLOCK_CHANGE = new PacketType(PacketType.Play.PROTOCOL, SENDER, 35, 53);
/* 148 */       public static final PacketType BLOCK_ACTION = new PacketType(PacketType.Play.PROTOCOL, SENDER, 36, 54);
/* 149 */       public static final PacketType BLOCK_BREAK_ANIMATION = new PacketType(PacketType.Play.PROTOCOL, SENDER, 37, 55);
/* 150 */       public static final PacketType MAP_CHUNK_BULK = new PacketType(PacketType.Play.PROTOCOL, SENDER, 38, 56);
/* 151 */       public static final PacketType EXPLOSION = new PacketType(PacketType.Play.PROTOCOL, SENDER, 39, 60);
/* 152 */       public static final PacketType WORLD_EVENT = new PacketType(PacketType.Play.PROTOCOL, SENDER, 40, 61);
/* 153 */       public static final PacketType NAMED_SOUND_EFFECT = new PacketType(PacketType.Play.PROTOCOL, SENDER, 41, 62);
/* 154 */       public static final PacketType WORLD_PARTICLES = new PacketType(PacketType.Play.PROTOCOL, SENDER, 42, 63);
/*     */ 
/* 158 */       public static final PacketType GAME_STATE_CHANGE = new PacketType(PacketType.Play.PROTOCOL, SENDER, 43, 70);
/* 159 */       public static final PacketType SPAWN_ENTITY_WEATHER = new PacketType(PacketType.Play.PROTOCOL, SENDER, 44, 71);
/* 160 */       public static final PacketType OPEN_WINDOW = new PacketType(PacketType.Play.PROTOCOL, SENDER, 45, 100);
/* 161 */       public static final PacketType CLOSE_WINDOW = new PacketType(PacketType.Play.PROTOCOL, SENDER, 46, 101);
/* 162 */       public static final PacketType SET_SLOT = new PacketType(PacketType.Play.PROTOCOL, SENDER, 47, 103);
/* 163 */       public static final PacketType WINDOW_ITEMS = new PacketType(PacketType.Play.PROTOCOL, SENDER, 48, 104);
/* 164 */       public static final PacketType CRAFT_PROGRESS_BAR = new PacketType(PacketType.Play.PROTOCOL, SENDER, 49, 105);
/* 165 */       public static final PacketType TRANSACTION = new PacketType(PacketType.Play.PROTOCOL, SENDER, 50, 106);
/* 166 */       public static final PacketType UPDATE_SIGN = new PacketType(PacketType.Play.PROTOCOL, SENDER, 51, 130);
/* 167 */       public static final PacketType MAP = new PacketType(PacketType.Play.PROTOCOL, SENDER, 52, 131);
/* 168 */       public static final PacketType TILE_ENTITY_DATA = new PacketType(PacketType.Play.PROTOCOL, SENDER, 53, 132);
/* 169 */       public static final PacketType OPEN_SIGN_ENTITY = new PacketType(PacketType.Play.PROTOCOL, SENDER, 54, 133);
/* 170 */       public static final PacketType STATISTICS = new PacketType(PacketType.Play.PROTOCOL, SENDER, 55, 200);
/* 171 */       public static final PacketType PLAYER_INFO = new PacketType(PacketType.Play.PROTOCOL, SENDER, 56, 201);
/* 172 */       public static final PacketType ABILITIES = new PacketType(PacketType.Play.PROTOCOL, SENDER, 57, 202);
/* 173 */       public static final PacketType TAB_COMPLETE = new PacketType(PacketType.Play.PROTOCOL, SENDER, 58, 203);
/* 174 */       public static final PacketType SCOREBOARD_OBJECTIVE = new PacketType(PacketType.Play.PROTOCOL, SENDER, 59, 206);
/* 175 */       public static final PacketType SCOREBOARD_SCORE = new PacketType(PacketType.Play.PROTOCOL, SENDER, 60, 207);
/* 176 */       public static final PacketType SCOREBOARD_DISPLAY_OBJECTIVE = new PacketType(PacketType.Play.PROTOCOL, SENDER, 61, 208);
/*     */ 
/* 178 */       public static final PacketType SCOREBOARD_TEAM = new PacketType(PacketType.Play.PROTOCOL, SENDER, 62, 209);
/* 179 */       public static final PacketType CUSTOM_PAYLOAD = new PacketType(PacketType.Play.PROTOCOL, SENDER, 63, 250);
/* 180 */       public static final PacketType KICK_DISCONNECT = new PacketType(PacketType.Play.PROTOCOL, SENDER, 64, 255);
/* 181 */       public static final PacketType SERVER_DIFFICULTY = new PacketType(PacketType.Play.PROTOCOL, SENDER, 65, -1);
/* 182 */       public static final PacketType COMBAT_EVENT = new PacketType(PacketType.Play.PROTOCOL, SENDER, 66, -1);
/* 183 */       public static final PacketType CAMERA = new PacketType(PacketType.Play.PROTOCOL, SENDER, 67, -1);
/* 184 */       public static final PacketType WORLD_BORDER = new PacketType(PacketType.Play.PROTOCOL, SENDER, 68, -1);
/* 185 */       public static final PacketType TITLE = new PacketType(PacketType.Play.PROTOCOL, SENDER, 69, -1);
/* 186 */       public static final PacketType SET_COMPRESSION = new PacketType(PacketType.Play.PROTOCOL, SENDER, 70, -1);
/* 187 */       public static final PacketType PLAYER_LIST_HEADER_FOOTER = new PacketType(PacketType.Play.PROTOCOL, SENDER, 71, -1);
/*     */ 
/* 189 */       public static final PacketType RESOURCE_PACK_SEND = new PacketType(PacketType.Play.PROTOCOL, SENDER, 72, -1);
/* 190 */       public static final PacketType UPDATE_ENTITY_NBT = new PacketType(PacketType.Play.PROTOCOL, SENDER, 73, -1);
/*     */ 
/* 193 */       private static final Server INSTANCE = new Server();
/*     */ 
/*     */       private Server() {
/* 196 */         super();
/*     */       }
/*     */       public static PacketType.Sender getSender() {
/* 199 */         return SENDER;
/*     */       }
/*     */       public static Server getInstance() {
/* 202 */         return INSTANCE;
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public static class Handshake
/*     */   {
/*  46 */     private static final PacketType.Protocol PROTOCOL = PacketType.Protocol.HANDSHAKING;
/*     */ 
/*     */     public static PacketType.Protocol getProtocol()
/*     */     {
/*  90 */       return PROTOCOL;
/*     */     }
/*     */ 
/*     */     public static class Server extends ObjectEnum<PacketType>
/*     */     {
/*  77 */       private static final PacketType.Sender SENDER = PacketType.Sender.CLIENT;
/*  78 */       private static final Server INSTANCE = new Server();
/*     */ 
/*  79 */       private Server() { super(); }
/*     */ 
/*     */       public static Server getInstance() {
/*  82 */         return INSTANCE;
/*     */       }
/*     */       public static PacketType.Sender getSender() {
/*  85 */         return SENDER;
/*     */       }
/*     */     }
/*     */ 
/*     */     public static class Client extends ObjectEnum<PacketType>
/*     */     {
/*  53 */       private static final PacketType.Sender SENDER = PacketType.Sender.CLIENT;
/*     */ 
/*  57 */       public static final PacketType SET_PROTOCOL = new PacketType(PacketType.Handshake.PROTOCOL, SENDER, 0, 2);
/*     */ 
/*  59 */       private static final Client INSTANCE = new Client();
/*     */ 
/*     */       private Client() {
/*  62 */         super();
/*     */       }
/*     */       public static Client getInstance() {
/*  65 */         return INSTANCE;
/*     */       }
/*     */       public static PacketType.Sender getSender() {
/*  68 */         return SENDER;
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.PacketType
 * JD-Core Version:    0.6.2
 */