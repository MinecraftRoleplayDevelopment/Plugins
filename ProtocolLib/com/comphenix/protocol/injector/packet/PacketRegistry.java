/*     */ package com.comphenix.protocol.injector.packet;
/*     */ 
/*     */ import com.comphenix.protocol.PacketType;
/*     */ import com.comphenix.protocol.PacketType.Sender;
/*     */ import com.comphenix.protocol.ProtocolLibrary;
/*     */ import com.comphenix.protocol.error.ErrorReporter;
/*     */ import com.comphenix.protocol.error.Report;
/*     */ import com.comphenix.protocol.error.Report.ReportBuilder;
/*     */ import com.comphenix.protocol.error.ReportType;
/*     */ import com.comphenix.protocol.injector.netty.NettyProtocolRegistry;
/*     */ import com.comphenix.protocol.reflect.FieldAccessException;
/*     */ import com.comphenix.protocol.utility.MinecraftReflection;
/*     */ import com.comphenix.protocol.wrappers.TroveWrapper.CannotFindTroveNoEntryValue;
/*     */ import com.google.common.base.Function;
/*     */ import com.google.common.collect.Maps;
/*     */ import com.google.common.collect.Sets;
/*     */ import java.util.Collections;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.Set;
/*     */ 
/*     */ public class PacketRegistry
/*     */ {
/*  45 */   public static final ReportType REPORT_CANNOT_CORRECT_TROVE_MAP = new ReportType("Unable to correct no entry value.");
/*     */ 
/*  47 */   public static final ReportType REPORT_INSUFFICIENT_SERVER_PACKETS = new ReportType("Too few server packets detected: %s");
/*  48 */   public static final ReportType REPORT_INSUFFICIENT_CLIENT_PACKETS = new ReportType("Too few client packets detected: %s");
/*     */   private static volatile LegacyPacketRegistry LEGACY;
/*     */   private static volatile NettyProtocolRegistry NETTY;
/*     */   private static volatile Set<PacketType> NETTY_SERVER_PACKETS;
/*     */   private static volatile Set<PacketType> NETTY_CLIENT_PACKETS;
/*     */   private static volatile Set<Integer> LEGACY_SERVER_PACKETS;
/*     */   private static volatile Set<Integer> LEGACY_CLIENT_PACKETS;
/*     */   private static volatile Map<Integer, Class> LEGACY_PREVIOUS_PACKETS;
/*     */   private static boolean INITIALIZED;
/*     */ 
/*     */   private static void initialize()
/*     */   {
/*  70 */     if (INITIALIZED)
/*     */     {
/*  72 */       if ((NETTY == null) && (LEGACY == null))
/*  73 */         throw new IllegalStateException("No initialized registry.");
/*  74 */       return;
/*     */     }
/*     */ 
/*  78 */     if (MinecraftReflection.isUsingNetty()) {
/*  79 */       if (NETTY == null)
/*  80 */         NETTY = new NettyProtocolRegistry();
/*     */     }
/*     */     else
/*  83 */       initializeLegacy();
/*     */   }
/*     */ 
/*     */   public static boolean isSupported(PacketType type)
/*     */   {
/*  93 */     initialize();
/*     */ 
/*  95 */     if (NETTY != null) {
/*  96 */       return NETTY.getPacketTypeLookup().containsKey(type);
/*     */     }
/*     */ 
/*  99 */     return type.isClient() ? LEGACY.getClientPackets().contains(Integer.valueOf(type.getLegacyId())) : LEGACY.getServerPackets().contains(Integer.valueOf(type.getLegacyId()));
/*     */   }
/*     */ 
/*     */   private static void initializeLegacy()
/*     */   {
/* 108 */     if (LEGACY == null)
/*     */       try {
/* 110 */         LEGACY = new LegacyPacketRegistry();
/* 111 */         LEGACY.initialize();
/*     */       } catch (LegacyPacketRegistry.InsufficientPacketsException e) {
/* 113 */         if (e.isClient()) {
/* 114 */           ProtocolLibrary.getErrorReporter().reportWarning(PacketRegistry.class, Report.newBuilder(REPORT_INSUFFICIENT_CLIENT_PACKETS).messageParam(new Object[] { Integer.valueOf(e.getPacketCount()) }));
/*     */         }
/*     */         else
/*     */         {
/* 118 */           ProtocolLibrary.getErrorReporter().reportWarning(PacketRegistry.class, Report.newBuilder(REPORT_INSUFFICIENT_SERVER_PACKETS).messageParam(new Object[] { Integer.valueOf(e.getPacketCount()) }));
/*     */         }
/*     */       }
/*     */       catch (TroveWrapper.CannotFindTroveNoEntryValue e)
/*     */       {
/* 123 */         ProtocolLibrary.getErrorReporter().reportWarning(PacketRegistry.class, Report.newBuilder(REPORT_CANNOT_CORRECT_TROVE_MAP).error(e.getCause()));
/*     */       }
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public static Map<Class, Integer> getPacketToID()
/*     */   {
/* 137 */     initialize();
/*     */ 
/* 139 */     if (NETTY != null)
/*     */     {
/* 141 */       Map result = Maps.transformValues(NETTY.getPacketClassLookup(), new Function() {
/*     */         public Integer apply(PacketType type) {
/* 143 */           return Integer.valueOf(type.getLegacyId());
/*     */         }
/*     */       });
/* 146 */       return result;
/*     */     }
/* 148 */     return LEGACY.getPacketToID();
/*     */   }
/*     */ 
/*     */   public static Map<Class, PacketType> getPacketToType()
/*     */   {
/* 156 */     initialize();
/*     */ 
/* 158 */     if (NETTY != null)
/*     */     {
/* 160 */       Map result = NETTY.getPacketClassLookup();
/* 161 */       return result;
/*     */     }
/* 163 */     return Maps.transformValues(LEGACY.getPacketToID(), new Function() {
/*     */       public PacketType apply(Integer packetId) {
/* 165 */         return PacketType.findLegacy(packetId.intValue());
/*     */       }
/*     */     });
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public static Map<Integer, Class> getOverwrittenPackets()
/*     */   {
/* 178 */     initialize();
/*     */ 
/* 180 */     if (LEGACY != null)
/* 181 */       return LEGACY.getOverwrittenPackets();
/* 182 */     throw new IllegalStateException("Not supported on Netty.");
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public static Map<Integer, Class> getPreviousPackets()
/*     */   {
/* 191 */     initialize();
/*     */ 
/* 193 */     if (NETTY != null)
/*     */     {
/* 195 */       if (LEGACY_PREVIOUS_PACKETS == null) {
/* 196 */         Map map = Maps.newHashMap();
/*     */ 
/* 198 */         for (Map.Entry entry : NETTY.getPacketTypeLookup().entrySet()) {
/* 199 */           map.put(Integer.valueOf(((PacketType)entry.getKey()).getLegacyId()), entry.getValue());
/*     */         }
/* 201 */         LEGACY_PREVIOUS_PACKETS = Collections.unmodifiableMap(map);
/*     */       }
/* 203 */       return LEGACY_PREVIOUS_PACKETS;
/*     */     }
/* 205 */     return LEGACY.getPreviousPackets();
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public static Set<Integer> getServerPackets()
/*     */     throws FieldAccessException
/*     */   {
/* 217 */     initialize();
/*     */ 
/* 219 */     if (NETTY != null) {
/* 220 */       if (LEGACY_SERVER_PACKETS == null) {
/* 221 */         LEGACY_SERVER_PACKETS = toLegacy(NETTY.getServerPackets());
/*     */       }
/* 223 */       return LEGACY_SERVER_PACKETS;
/*     */     }
/* 225 */     return LEGACY.getServerPackets();
/*     */   }
/*     */ 
/*     */   public static Set<PacketType> getServerPacketTypes()
/*     */   {
/* 233 */     initialize();
/*     */ 
/* 235 */     if (NETTY != null) {
/* 236 */       NETTY.synchronize();
/* 237 */       return NETTY.getServerPackets();
/*     */     }
/*     */ 
/* 241 */     if (NETTY_SERVER_PACKETS == null) {
/* 242 */       NETTY_SERVER_PACKETS = toPacketTypes(LEGACY.getServerPackets(), PacketType.Sender.SERVER);
/*     */     }
/* 244 */     return NETTY_SERVER_PACKETS;
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public static Set<Integer> getClientPackets()
/*     */     throws FieldAccessException
/*     */   {
/* 256 */     initialize();
/*     */ 
/* 258 */     if (NETTY != null) {
/* 259 */       if (LEGACY_CLIENT_PACKETS == null) {
/* 260 */         LEGACY_CLIENT_PACKETS = toLegacy(NETTY.getClientPackets());
/*     */       }
/* 262 */       return LEGACY_CLIENT_PACKETS;
/*     */     }
/* 264 */     return LEGACY.getClientPackets();
/*     */   }
/*     */ 
/*     */   public static Set<PacketType> getClientPacketTypes()
/*     */   {
/* 272 */     initialize();
/*     */ 
/* 274 */     if (NETTY != null) {
/* 275 */       NETTY.synchronize();
/* 276 */       return NETTY.getClientPackets();
/*     */     }
/*     */ 
/* 280 */     if (NETTY_CLIENT_PACKETS == null) {
/* 281 */       NETTY_CLIENT_PACKETS = toPacketTypes(LEGACY.getClientPackets(), PacketType.Sender.CLIENT);
/*     */     }
/* 283 */     return NETTY_CLIENT_PACKETS;
/*     */   }
/*     */ 
/*     */   public static Set<Integer> toLegacy(Set<PacketType> types)
/*     */   {
/* 292 */     Set result = Sets.newHashSet();
/*     */ 
/* 294 */     for (PacketType type : types)
/* 295 */       result.add(Integer.valueOf(type.getLegacyId()));
/* 296 */     return Collections.unmodifiableSet(result);
/*     */   }
/*     */ 
/*     */   public static Set<PacketType> toPacketTypes(Set<Integer> ids)
/*     */   {
/* 305 */     return toPacketTypes(ids, null);
/*     */   }
/*     */ 
/*     */   public static Set<PacketType> toPacketTypes(Set<Integer> ids, PacketType.Sender preference)
/*     */   {
/* 315 */     Set result = Sets.newHashSet();
/*     */ 
/* 317 */     for (Iterator i$ = ids.iterator(); i$.hasNext(); ) { int id = ((Integer)i$.next()).intValue();
/* 318 */       result.add(PacketType.fromLegacy(id, preference)); }
/* 319 */     return Collections.unmodifiableSet(result);
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public static Class getPacketClassFromID(int packetID)
/*     */   {
/* 331 */     initialize();
/*     */ 
/* 333 */     if (NETTY != null)
/* 334 */       return (Class)NETTY.getPacketTypeLookup().get(PacketType.findLegacy(packetID));
/* 335 */     return LEGACY.getPacketClassFromID(packetID);
/*     */   }
/*     */ 
/*     */   public static Class getPacketClassFromType(PacketType type)
/*     */   {
/* 344 */     return getPacketClassFromType(type, false);
/*     */   }
/*     */ 
/*     */   public static Class getPacketClassFromType(PacketType type, boolean forceVanilla)
/*     */   {
/* 356 */     initialize();
/*     */ 
/* 358 */     if (NETTY != null)
/* 359 */       return (Class)NETTY.getPacketTypeLookup().get(type);
/* 360 */     return LEGACY.getPacketClassFromID(type.getLegacyId(), forceVanilla);
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public static Class getPacketClassFromID(int packetID, boolean forceVanilla)
/*     */   {
/* 373 */     initialize();
/*     */ 
/* 375 */     if (LEGACY != null)
/* 376 */       return LEGACY.getPacketClassFromID(packetID, forceVanilla);
/* 377 */     return getPacketClassFromID(packetID);
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public static int getPacketID(Class<?> packet)
/*     */   {
/* 390 */     initialize();
/*     */ 
/* 392 */     if (NETTY != null)
/* 393 */       return ((PacketType)NETTY.getPacketClassLookup().get(packet)).getLegacyId();
/* 394 */     return LEGACY.getPacketID(packet);
/*     */   }
/*     */ 
/*     */   public static PacketType getPacketType(Class<?> packet)
/*     */   {
/* 403 */     return getPacketType(packet, null);
/*     */   }
/*     */ 
/*     */   public static PacketType getPacketType(Class<?> packet, PacketType.Sender sender)
/*     */   {
/* 413 */     initialize();
/*     */ 
/* 415 */     if (NETTY != null) {
/* 416 */       return (PacketType)NETTY.getPacketClassLookup().get(packet);
/*     */     }
/* 418 */     int id = LEGACY.getPacketID(packet);
/*     */ 
/* 420 */     return PacketType.hasLegacy(id) ? PacketType.fromLegacy(id, sender) : null;
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.injector.packet.PacketRegistry
 * JD-Core Version:    0.6.2
 */