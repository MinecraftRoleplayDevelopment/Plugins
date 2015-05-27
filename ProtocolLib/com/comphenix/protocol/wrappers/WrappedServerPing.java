/*     */ package com.comphenix.protocol.wrappers;
/*     */ 
/*     */ import com.comphenix.protocol.PacketType;
/*     */ import com.comphenix.protocol.PacketType.Status.Server;
/*     */ import com.comphenix.protocol.ProtocolLibrary;
/*     */ import com.comphenix.protocol.ProtocolManager;
/*     */ import com.comphenix.protocol.injector.BukkitUnwrapper;
/*     */ import com.comphenix.protocol.reflect.EquivalentConverter;
/*     */ import com.comphenix.protocol.reflect.accessors.Accessors;
/*     */ import com.comphenix.protocol.reflect.accessors.ConstructorAccessor;
/*     */ import com.comphenix.protocol.reflect.accessors.FieldAccessor;
/*     */ import com.comphenix.protocol.reflect.accessors.MethodAccessor;
/*     */ import com.comphenix.protocol.utility.MinecraftReflection;
/*     */ import com.comphenix.protocol.utility.MinecraftVersion;
/*     */ import com.comphenix.protocol.utility.Util;
/*     */ import com.google.common.base.Charsets;
/*     */ import com.google.common.base.Preconditions;
/*     */ import com.google.common.base.Splitter;
/*     */ import com.google.common.collect.ImmutableList;
/*     */ import com.google.common.collect.ImmutableMap;
/*     */ import com.google.common.collect.ImmutableMap.Builder;
/*     */ import com.google.common.collect.Lists;
/*     */ import com.google.common.io.ByteStreams;
/*     */ import com.mojang.authlib.GameProfile;
/*     */ import io.netty.buffer.ByteBuf;
/*     */ import io.netty.buffer.Unpooled;
/*     */ import io.netty.handler.codec.base64.Base64;
/*     */ import io.netty.util.IllegalReferenceCountException;
/*     */ import java.awt.image.BufferedImage;
/*     */ import java.awt.image.RenderedImage;
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.util.List;
/*     */ import javax.imageio.ImageIO;
/*     */ import org.bukkit.Bukkit;
/*     */ import org.bukkit.Server;
/*     */ import org.bukkit.entity.Player;
/*     */ 
/*     */ public class WrappedServerPing extends AbstractWrapper
/*     */ {
/*  51 */   private static ImmutableMap<MinecraftVersion, Integer> VERSION_NUMBERS = ImmutableMap.builder().put(MinecraftVersion.WORLD_UPDATE, Integer.valueOf(4)).put(MinecraftVersion.SKIN_UPDATE, Integer.valueOf(5)).put(MinecraftVersion.BOUNTIFUL_UPDATE, Integer.valueOf(47)).build();
/*     */ 
/*  56 */   private static MinecraftVersion LAST_VERSION = MinecraftVersion.BOUNTIFUL_UPDATE;
/*     */ 
/*  59 */   private static Class<?> SERVER_PING = MinecraftReflection.getServerPingClass();
/*  60 */   private static ConstructorAccessor SERVER_PING_CONSTRUCTOR = Accessors.getConstructorAccessor(SERVER_PING, new Class[0]);
/*  61 */   private static FieldAccessor DESCRIPTION = Accessors.getFieldAccessor(SERVER_PING, MinecraftReflection.getIChatBaseComponentClass(), true);
/*  62 */   private static FieldAccessor PLAYERS = Accessors.getFieldAccessor(SERVER_PING, MinecraftReflection.getServerPingPlayerSampleClass(), true);
/*  63 */   private static FieldAccessor VERSION = Accessors.getFieldAccessor(SERVER_PING, MinecraftReflection.getServerPingServerDataClass(), true);
/*  64 */   private static FieldAccessor FAVICON = Accessors.getFieldAccessor(SERVER_PING, String.class, true);
/*     */ 
/*  67 */   private static EquivalentConverter<Iterable<? extends WrappedGameProfile>> PROFILE_CONVERT = BukkitConverters.getArrayConverter(GameProfile.class, BukkitConverters.getWrappedGameProfileConverter());
/*     */ 
/*  71 */   private static Class<?> PLAYERS_CLASS = MinecraftReflection.getServerPingPlayerSampleClass();
/*  72 */   private static ConstructorAccessor PLAYERS_CONSTRUCTOR = Accessors.getConstructorAccessor(PLAYERS_CLASS, new Class[] { Integer.TYPE, Integer.TYPE });
/*  73 */   private static FieldAccessor[] PLAYERS_INTS = Accessors.getFieldAccessorArray(PLAYERS_CLASS, Integer.TYPE, true);
/*  74 */   private static FieldAccessor PLAYERS_PROFILES = Accessors.getFieldAccessor(PLAYERS_CLASS, [Lcom.mojang.authlib.GameProfile.class, true);
/*  75 */   private static FieldAccessor PLAYERS_MAXIMUM = PLAYERS_INTS[0];
/*  76 */   private static FieldAccessor PLAYERS_ONLINE = PLAYERS_INTS[1];
/*     */ 
/*  79 */   private static Class<?> GSON_CLASS = MinecraftReflection.getMinecraftGsonClass();
/*  80 */   private static MethodAccessor GSON_TO_JSON = Accessors.getMethodAccessor(GSON_CLASS, "toJson", new Class[] { Object.class });
/*  81 */   private static MethodAccessor GSON_FROM_JSON = Accessors.getMethodAccessor(GSON_CLASS, "fromJson", new Class[] { String.class, Class.class });
/*  82 */   private static FieldAccessor PING_GSON = Accessors.getCached(Accessors.getFieldAccessor(PacketType.Status.Server.OUT_SERVER_INFO.getPacketClass(), GSON_CLASS, true));
/*     */ 
/*  87 */   private static Class<?> VERSION_CLASS = MinecraftReflection.getServerPingServerDataClass();
/*  88 */   private static ConstructorAccessor VERSION_CONSTRUCTOR = Accessors.getConstructorAccessor(VERSION_CLASS, new Class[] { String.class, Integer.TYPE });
/*  89 */   private static FieldAccessor VERSION_NAME = Accessors.getFieldAccessor(VERSION_CLASS, String.class, true);
/*  90 */   private static FieldAccessor VERSION_PROTOCOL = Accessors.getFieldAccessor(VERSION_CLASS, Integer.TYPE, true);
/*     */ 
/*  93 */   private static FieldAccessor ENTITY_HUMAN_PROFILE = Accessors.getFieldAccessor(MinecraftReflection.getEntityPlayerClass().getSuperclass(), GameProfile.class, true);
/*     */   private Object players;
/*     */   private Object version;
/*     */ 
/*     */   public WrappedServerPing()
/*     */   {
/* 106 */     super(MinecraftReflection.getServerPingClass());
/* 107 */     setHandle(SERVER_PING_CONSTRUCTOR.invoke(new Object[0]));
/* 108 */     resetPlayers();
/* 109 */     resetVersion();
/*     */   }
/*     */ 
/*     */   private WrappedServerPing(Object handle) {
/* 113 */     super(MinecraftReflection.getServerPingClass());
/* 114 */     setHandle(handle);
/* 115 */     this.players = PLAYERS.get(handle);
/* 116 */     this.version = VERSION.get(handle);
/*     */   }
/*     */ 
/*     */   protected void resetPlayers()
/*     */   {
/* 123 */     this.players = PLAYERS_CONSTRUCTOR.invoke(new Object[] { Integer.valueOf(0), Integer.valueOf(0) });
/* 124 */     PLAYERS.set(this.handle, this.players);
/*     */   }
/*     */ 
/*     */   protected void resetVersion()
/*     */   {
/* 131 */     ProtocolManager manager = ProtocolLibrary.getProtocolManager();
/* 132 */     MinecraftVersion minecraftVersion = LAST_VERSION;
/*     */ 
/* 135 */     if (manager != null) {
/* 136 */       minecraftVersion = manager.getMinecraftVersion();
/*     */     }
/* 138 */     this.version = VERSION_CONSTRUCTOR.invoke(new Object[] { minecraftVersion.toString(), VERSION_NUMBERS.get(minecraftVersion) });
/* 139 */     VERSION.set(this.handle, this.version);
/*     */   }
/*     */ 
/*     */   public static WrappedServerPing fromHandle(Object handle)
/*     */   {
/* 148 */     return new WrappedServerPing(handle);
/*     */   }
/*     */ 
/*     */   public static WrappedServerPing fromJson(String json)
/*     */   {
/* 157 */     return fromHandle(GSON_FROM_JSON.invoke(PING_GSON.get(null), new Object[] { json, SERVER_PING }));
/*     */   }
/*     */ 
/*     */   public WrappedChatComponent getMotD()
/*     */   {
/* 165 */     return WrappedChatComponent.fromHandle(DESCRIPTION.get(this.handle));
/*     */   }
/*     */ 
/*     */   public void setMotD(WrappedChatComponent description)
/*     */   {
/* 173 */     DESCRIPTION.set(this.handle, description.getHandle());
/*     */   }
/*     */ 
/*     */   public void setMotD(String message)
/*     */   {
/* 181 */     setMotD(WrappedChatComponent.fromText(message));
/*     */   }
/*     */ 
/*     */   public CompressedImage getFavicon()
/*     */   {
/* 189 */     String favicon = (String)FAVICON.get(this.handle);
/* 190 */     return favicon != null ? CompressedImage.fromEncodedText(favicon) : null;
/*     */   }
/*     */ 
/*     */   public void setFavicon(CompressedImage image)
/*     */   {
/* 198 */     FAVICON.set(this.handle, image != null ? image.toEncodedText() : null);
/*     */   }
/*     */ 
/*     */   public int getPlayersOnline()
/*     */   {
/* 208 */     if (this.players == null)
/* 209 */       throw new IllegalStateException("The player count has been hidden.");
/* 210 */     return ((Integer)PLAYERS_ONLINE.get(this.players)).intValue();
/*     */   }
/*     */ 
/*     */   public void setPlayersOnline(int online)
/*     */   {
/* 221 */     if (this.players == null)
/* 222 */       resetPlayers();
/* 223 */     PLAYERS_ONLINE.set(this.players, Integer.valueOf(online));
/*     */   }
/*     */ 
/*     */   public int getPlayersMaximum()
/*     */   {
/* 233 */     if (this.players == null)
/* 234 */       throw new IllegalStateException("The player maximum has been hidden.");
/* 235 */     return ((Integer)PLAYERS_MAXIMUM.get(this.players)).intValue();
/*     */   }
/*     */ 
/*     */   public void setPlayersMaximum(int maximum)
/*     */   {
/* 246 */     if (this.players == null)
/* 247 */       resetPlayers();
/* 248 */     PLAYERS_MAXIMUM.set(this.players, Integer.valueOf(maximum));
/*     */   }
/*     */ 
/*     */   public void setPlayersVisible(boolean visible)
/*     */   {
/* 258 */     if (isPlayersVisible() != visible)
/* 259 */       if (visible)
/*     */       {
/* 261 */         Server server = Bukkit.getServer();
/* 262 */         setPlayersMaximum(server.getMaxPlayers());
/* 263 */         setPlayersOnline(Util.getOnlinePlayers().size());
/*     */       } else {
/* 265 */         PLAYERS.set(this.handle, this.players = null);
/*     */       }
/*     */   }
/*     */ 
/*     */   public boolean isPlayersVisible()
/*     */   {
/* 277 */     return this.players != null;
/*     */   }
/*     */ 
/*     */   public ImmutableList<WrappedGameProfile> getPlayers()
/*     */   {
/* 285 */     if (this.players == null)
/* 286 */       return ImmutableList.of();
/* 287 */     Object playerProfiles = PLAYERS_PROFILES.get(this.players);
/* 288 */     if (playerProfiles == null)
/* 289 */       return ImmutableList.of();
/* 290 */     return ImmutableList.copyOf((Iterable)PROFILE_CONVERT.getSpecific(playerProfiles));
/*     */   }
/*     */ 
/*     */   public void setPlayers(Iterable<? extends WrappedGameProfile> profile)
/*     */   {
/* 298 */     if (this.players == null)
/* 299 */       resetPlayers();
/* 300 */     PLAYERS_PROFILES.set(this.players, profile != null ? PROFILE_CONVERT.getGeneric([Lcom.mojang.authlib.GameProfile.class, profile) : null);
/*     */   }
/*     */ 
/*     */   public void setBukkitPlayers(Iterable<? extends Player> players)
/*     */   {
/* 308 */     List profiles = Lists.newArrayList();
/*     */ 
/* 310 */     for (Player player : players) {
/* 311 */       GameProfile profile = (GameProfile)ENTITY_HUMAN_PROFILE.get(BukkitUnwrapper.getInstance().unwrapItem(player));
/* 312 */       profiles.add(WrappedGameProfile.fromHandle(profile));
/*     */     }
/* 314 */     setPlayers(profiles);
/*     */   }
/*     */ 
/*     */   public String getVersionName()
/*     */   {
/* 322 */     return (String)VERSION_NAME.get(this.version);
/*     */   }
/*     */ 
/*     */   public void setVersionName(String name)
/*     */   {
/* 330 */     VERSION_NAME.set(this.version, name);
/*     */   }
/*     */ 
/*     */   public int getVersionProtocol()
/*     */   {
/* 338 */     return ((Integer)VERSION_PROTOCOL.get(this.version)).intValue();
/*     */   }
/*     */ 
/*     */   public void setVersionProtocol(int protocol)
/*     */   {
/* 346 */     VERSION_PROTOCOL.set(this.version, Integer.valueOf(protocol));
/*     */   }
/*     */ 
/*     */   public WrappedServerPing deepClone()
/*     */   {
/* 354 */     WrappedServerPing copy = new WrappedServerPing();
/* 355 */     WrappedChatComponent motd = getMotD();
/*     */ 
/* 357 */     copy.setPlayers(getPlayers());
/* 358 */     copy.setFavicon(getFavicon());
/* 359 */     copy.setMotD(motd != null ? motd.deepClone() : null);
/* 360 */     copy.setVersionName(getVersionName());
/* 361 */     copy.setVersionProtocol(getVersionProtocol());
/*     */ 
/* 363 */     if (isPlayersVisible()) {
/* 364 */       copy.setPlayersMaximum(getPlayersMaximum());
/* 365 */       copy.setPlayersOnline(getPlayersOnline());
/*     */     } else {
/* 367 */       copy.setPlayersVisible(false);
/*     */     }
/* 369 */     return copy;
/*     */   }
/*     */ 
/*     */   public String toJson()
/*     */   {
/* 377 */     return (String)GSON_TO_JSON.invoke(PING_GSON.get(null), new Object[] { this.handle });
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 382 */     return "WrappedServerPing< " + toJson() + ">";
/*     */   }
/*     */ 
/*     */   private static class EncodedCompressedImage extends WrappedServerPing.CompressedImage
/*     */   {
/*     */     public EncodedCompressedImage(String encoded)
/*     */     {
/* 522 */       this.encoded = ((String)Preconditions.checkNotNull(encoded, "encoded favicon cannot be NULL"));
/*     */     }
/*     */ 
/*     */     protected void initialize()
/*     */     {
/* 529 */       if ((this.mime == null) || (this.data == null))
/* 530 */         decode();
/*     */     }
/*     */ 
/*     */     protected void decode()
/*     */     {
/* 538 */       for (String segment : Splitter.on(";").split(this.encoded))
/* 539 */         if (segment.startsWith("data:")) {
/* 540 */           this.mime = segment.substring(5);
/* 541 */         } else if (segment.startsWith("base64,")) {
/* 542 */           byte[] encoded = segment.substring(7).getBytes(Charsets.UTF_8);
/* 543 */           ByteBuf decoded = Base64.decode(Unpooled.wrappedBuffer(encoded));
/*     */ 
/* 546 */           byte[] data = new byte[decoded.readableBytes()];
/* 547 */           decoded.readBytes(data);
/* 548 */           this.data = data;
/*     */         }
/*     */     }
/*     */ 
/*     */     protected byte[] getData()
/*     */     {
/* 557 */       initialize();
/* 558 */       return super.getData();
/*     */     }
/*     */ 
/*     */     public String getMime()
/*     */     {
/* 563 */       initialize();
/* 564 */       return super.getMime();
/*     */     }
/*     */ 
/*     */     public String toEncodedText()
/*     */     {
/* 569 */       return this.encoded;
/*     */     }
/*     */   }
/*     */ 
/*     */   public static class CompressedImage
/*     */   {
/*     */     protected volatile String mime;
/*     */     protected volatile byte[] data;
/*     */     protected volatile String encoded;
/*     */ 
/*     */     protected CompressedImage()
/*     */     {
/*     */     }
/*     */ 
/*     */     public CompressedImage(String mime, byte[] data)
/*     */     {
/* 408 */       this.mime = ((String)Preconditions.checkNotNull(mime, "mime cannot be NULL"));
/* 409 */       this.data = ((byte[])Preconditions.checkNotNull(data, "data cannot be NULL"));
/*     */     }
/*     */ 
/*     */     public static CompressedImage fromPng(InputStream input)
/*     */       throws IOException
/*     */     {
/* 419 */       return new CompressedImage("image/png", ByteStreams.toByteArray(input));
/*     */     }
/*     */ 
/*     */     public static CompressedImage fromPng(byte[] data)
/*     */     {
/* 428 */       return new CompressedImage("image/png", data);
/*     */     }
/*     */ 
/*     */     public static CompressedImage fromBase64Png(String base64)
/*     */     {
/*     */       try
/*     */       {
/* 438 */         return new WrappedServerPing.EncodedCompressedImage("data:image/png;base64," + base64);
/*     */       }
/*     */       catch (IllegalArgumentException e) {
/* 441 */         throw new IllegalReferenceCountException("Must be a pure base64 encoded string. Cannot be an encoded text.", e);
/*     */       }
/*     */     }
/*     */ 
/*     */     public static CompressedImage fromPng(RenderedImage image)
/*     */       throws IOException
/*     */     {
/* 451 */       ByteArrayOutputStream output = new ByteArrayOutputStream();
/* 452 */       ImageIO.write(image, "png", output);
/* 453 */       return new CompressedImage("image/png", output.toByteArray());
/*     */     }
/*     */ 
/*     */     public static CompressedImage fromEncodedText(String text)
/*     */     {
/* 462 */       return new WrappedServerPing.EncodedCompressedImage(text);
/*     */     }
/*     */ 
/*     */     public String getMime()
/*     */     {
/* 472 */       return this.mime;
/*     */     }
/*     */ 
/*     */     public byte[] getDataCopy()
/*     */     {
/* 480 */       return (byte[])getData().clone();
/*     */     }
/*     */ 
/*     */     protected byte[] getData()
/*     */     {
/* 488 */       return this.data;
/*     */     }
/*     */ 
/*     */     public BufferedImage getImage()
/*     */       throws IOException
/*     */     {
/* 497 */       return ImageIO.read(new ByteArrayInputStream(getData()));
/*     */     }
/*     */ 
/*     */     public String toEncodedText()
/*     */     {
/* 505 */       if (this.encoded == null) {
/* 506 */         ByteBuf buffer = Unpooled.wrappedBuffer(getData());
/* 507 */         String computed = "data:" + this.mime + ";base64," + Base64.encode(buffer).toString(Charsets.UTF_8);
/*     */ 
/* 510 */         this.encoded = computed;
/*     */       }
/* 512 */       return this.encoded;
/*     */     }
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.wrappers.WrappedServerPing
 * JD-Core Version:    0.6.2
 */