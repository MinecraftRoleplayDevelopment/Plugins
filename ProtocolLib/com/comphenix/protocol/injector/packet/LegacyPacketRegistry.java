/*     */ package com.comphenix.protocol.injector.packet;
/*     */ 
/*     */ import com.comphenix.net.sf.cglib.proxy.Factory;
/*     */ import com.comphenix.protocol.reflect.FieldAccessException;
/*     */ import com.comphenix.protocol.reflect.FieldUtils;
/*     */ import com.comphenix.protocol.reflect.FuzzyReflection;
/*     */ import com.comphenix.protocol.reflect.fuzzy.FuzzyClassContract;
/*     */ import com.comphenix.protocol.reflect.fuzzy.FuzzyClassContract.Builder;
/*     */ import com.comphenix.protocol.reflect.fuzzy.FuzzyFieldContract;
/*     */ import com.comphenix.protocol.reflect.fuzzy.FuzzyFieldContract.Builder;
/*     */ import com.comphenix.protocol.reflect.fuzzy.FuzzyMethodContract;
/*     */ import com.comphenix.protocol.reflect.fuzzy.FuzzyMethodContract.Builder;
/*     */ import com.comphenix.protocol.utility.MinecraftReflection;
/*     */ import com.comphenix.protocol.wrappers.TroveWrapper;
/*     */ import com.google.common.base.Function;
/*     */ import com.google.common.base.Predicate;
/*     */ import com.google.common.collect.ImmutableSet;
/*     */ import com.google.common.collect.Iterables;
/*     */ import com.google.common.collect.Multimap;
/*     */ import java.lang.reflect.Field;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.Set;
/*     */ import javax.annotation.Nullable;
/*     */ 
/*     */ class LegacyPacketRegistry
/*     */ {
/*     */   private static final int MIN_SERVER_PACKETS = 5;
/*     */   private static final int MIN_CLIENT_PACKETS = 5;
/*     */   private FuzzyReflection packetRegistry;
/*     */   private Map<Class, Integer> packetToID;
/*     */   private Multimap<Integer, Class> customIdToPacket;
/*     */   private Map<Integer, Class> vanillaIdToPacket;
/*     */   private ImmutableSet<Integer> serverPackets;
/*     */   private ImmutableSet<Integer> clientPackets;
/*     */   private Set<Integer> serverPacketsRef;
/*     */   private Set<Integer> clientPacketsRef;
/*     */   private Map<Integer, Class> overwrittenPackets;
/*     */   private Map<Integer, Class> previousValues;
/*     */ 
/*     */   LegacyPacketRegistry()
/*     */   {
/*  52 */     this.overwrittenPackets = new HashMap();
/*     */ 
/*  55 */     this.previousValues = new HashMap();
/*     */   }
/*     */ 
/*     */   public void initialize()
/*     */   {
/*  62 */     if (this.packetToID == null) {
/*     */       try {
/*  64 */         Field packetsField = getPacketRegistry().getFieldByType("packetsField", Map.class);
/*  65 */         this.packetToID = ((Map)FieldUtils.readStaticField(packetsField, true));
/*     */       }
/*     */       catch (IllegalArgumentException e) {
/*     */         try {
/*  69 */           this.packetToID = getSpigotWrapper();
/*     */         }
/*     */         catch (Exception e2) {
/*  72 */           throw new IllegalArgumentException(e.getMessage() + "; Spigot workaround failed.", e2);
/*     */         }
/*     */       }
/*     */       catch (IllegalAccessException e) {
/*  76 */         throw new RuntimeException("Unable to retrieve the packetClassToIdMap", e);
/*     */       }
/*     */ 
/*  80 */       this.customIdToPacket = InverseMaps.inverseMultimap(this.packetToID, new Predicate()
/*     */       {
/*     */         public boolean apply(@Nullable Map.Entry<Class, Integer> entry) {
/*  83 */           return !MinecraftReflection.isMinecraftClass((Class)entry.getKey());
/*     */         }
/*     */       });
/*  88 */       this.vanillaIdToPacket = InverseMaps.inverseMap(this.packetToID, new Predicate()
/*     */       {
/*     */         public boolean apply(@Nullable Map.Entry<Class, Integer> entry) {
/*  91 */           return MinecraftReflection.isMinecraftClass((Class)entry.getKey());
/*     */         }
/*     */       });
/*     */     }
/*  95 */     initializeSets();
/*     */   }
/*     */ 
/*     */   private void initializeSets() throws FieldAccessException
/*     */   {
/* 100 */     if ((this.serverPacketsRef == null) || (this.clientPacketsRef == null)) {
/* 101 */       List sets = getPacketRegistry().getFieldListByType(Set.class);
/*     */       try
/*     */       {
/* 104 */         if (sets.size() > 1) {
/* 105 */           this.serverPacketsRef = ((Set)FieldUtils.readStaticField((Field)sets.get(0), true));
/* 106 */           this.clientPacketsRef = ((Set)FieldUtils.readStaticField((Field)sets.get(1), true));
/*     */ 
/* 109 */           if ((this.serverPacketsRef == null) || (this.clientPacketsRef == null)) {
/* 110 */             throw new FieldAccessException("Packet sets are in an illegal state.");
/*     */           }
/*     */ 
/* 113 */           this.serverPackets = ImmutableSet.copyOf(this.serverPacketsRef);
/* 114 */           this.clientPackets = ImmutableSet.copyOf(this.clientPacketsRef);
/*     */ 
/* 117 */           if (this.serverPackets.size() < 5)
/* 118 */             throw new InsufficientPacketsException("Insufficient server packets.", false, this.serverPackets.size(), null);
/* 119 */           if (this.clientPackets.size() < 5)
/* 120 */             throw new InsufficientPacketsException("Insufficient client packets.", true, this.clientPackets.size(), null);
/*     */         }
/*     */         else {
/* 123 */           throw new FieldAccessException("Cannot retrieve packet client/server sets.");
/*     */         }
/*     */       }
/*     */       catch (IllegalAccessException e) {
/* 127 */         throw new FieldAccessException("Cannot access field.", e);
/*     */       }
/*     */     }
/*     */     else
/*     */     {
/* 132 */       if ((this.serverPacketsRef != null) && (this.serverPacketsRef.size() != this.serverPackets.size()))
/* 133 */         this.serverPackets = ImmutableSet.copyOf(this.serverPacketsRef);
/* 134 */       if ((this.clientPacketsRef != null) && (this.clientPacketsRef.size() != this.clientPackets.size()))
/* 135 */         this.clientPackets = ImmutableSet.copyOf(this.clientPacketsRef);
/*     */     }
/*     */   }
/*     */ 
/*     */   public Map<Class, Integer> getPacketToID()
/*     */   {
/* 145 */     if (this.packetToID == null) {
/* 146 */       initialize();
/*     */     }
/* 148 */     return this.packetToID;
/*     */   }
/*     */ 
/*     */   private Map<Class, Integer> getSpigotWrapper()
/*     */     throws IllegalAccessException
/*     */   {
/* 154 */     FuzzyClassContract mapLike = FuzzyClassContract.newBuilder().method(FuzzyMethodContract.newBuilder().nameExact("size").returnTypeExact(Integer.TYPE)).method(FuzzyMethodContract.newBuilder().nameExact("put").parameterCount(2)).method(FuzzyMethodContract.newBuilder().nameExact("get").parameterCount(1)).build();
/*     */ 
/* 163 */     Field packetsField = getPacketRegistry().getField(FuzzyFieldContract.newBuilder().typeMatches(mapLike).build());
/*     */ 
/* 165 */     Object troveMap = FieldUtils.readStaticField(packetsField, true);
/*     */ 
/* 168 */     TroveWrapper.transformNoEntryValue(troveMap, new Function() {
/*     */       public Integer apply(Integer value) {
/* 170 */         if ((value.intValue() >= 0) && (value.intValue() < 256))
/*     */         {
/* 172 */           return Integer.valueOf(-1);
/*     */         }
/* 174 */         return value;
/*     */       }
/*     */     });
/* 179 */     return TroveWrapper.getDecoratedMap(troveMap);
/*     */   }
/*     */ 
/*     */   private FuzzyReflection getPacketRegistry()
/*     */   {
/* 187 */     if (this.packetRegistry == null)
/* 188 */       this.packetRegistry = FuzzyReflection.fromClass(MinecraftReflection.getPacketClass(), true);
/* 189 */     return this.packetRegistry;
/*     */   }
/*     */ 
/*     */   public Map<Integer, Class> getOverwrittenPackets()
/*     */   {
/* 197 */     return this.overwrittenPackets;
/*     */   }
/*     */ 
/*     */   public Map<Integer, Class> getPreviousPackets()
/*     */   {
/* 205 */     return this.previousValues;
/*     */   }
/*     */ 
/*     */   public Set<Integer> getServerPackets()
/*     */     throws FieldAccessException
/*     */   {
/* 214 */     initializeSets();
/*     */ 
/* 217 */     if ((this.serverPackets != null) && (this.serverPackets.size() < 5))
/* 218 */       throw new FieldAccessException("Server packet list is empty. Seems to be unsupported");
/* 219 */     return this.serverPackets;
/*     */   }
/*     */ 
/*     */   public Set<Integer> getClientPackets()
/*     */     throws FieldAccessException
/*     */   {
/* 228 */     initializeSets();
/*     */ 
/* 231 */     if ((this.clientPackets != null) && (this.clientPackets.size() < 5))
/* 232 */       throw new FieldAccessException("Client packet list is empty. Seems to be unsupported");
/* 233 */     return this.clientPackets;
/*     */   }
/*     */ 
/*     */   public Class getPacketClassFromID(int packetID)
/*     */   {
/* 242 */     return getPacketClassFromID(packetID, false);
/*     */   }
/*     */ 
/*     */   public Class getPacketClassFromID(int packetID, boolean forceVanilla)
/*     */   {
/* 252 */     Map lookup = forceVanilla ? this.previousValues : this.overwrittenPackets;
/* 253 */     Class result = null;
/*     */ 
/* 256 */     if (lookup.containsKey(Integer.valueOf(packetID))) {
/* 257 */       return removeEnhancer((Class)lookup.get(Integer.valueOf(packetID)), forceVanilla);
/*     */     }
/*     */ 
/* 261 */     getPacketToID();
/*     */ 
/* 264 */     if (!forceVanilla) {
/* 265 */       result = (Class)Iterables.getFirst(this.customIdToPacket.get(Integer.valueOf(packetID)), null);
/*     */     }
/* 267 */     if (result == null) {
/* 268 */       result = (Class)this.vanillaIdToPacket.get(Integer.valueOf(packetID));
/*     */     }
/*     */ 
/* 272 */     if (result != null) {
/* 273 */       return result;
/*     */     }
/* 275 */     throw new IllegalArgumentException("The packet ID " + packetID + " is not registered.");
/*     */   }
/*     */ 
/*     */   public int getPacketID(Class<?> packet)
/*     */   {
/* 285 */     if (packet == null)
/* 286 */       throw new IllegalArgumentException("Packet type class cannot be NULL.");
/* 287 */     if (!MinecraftReflection.getPacketClass().isAssignableFrom(packet)) {
/* 288 */       throw new IllegalArgumentException("Type must be a packet.");
/*     */     }
/*     */ 
/* 291 */     return ((Integer)getPacketToID().get(packet)).intValue();
/*     */   }
/*     */ 
/*     */   private static Class removeEnhancer(Class clazz, boolean remove)
/*     */   {
/* 301 */     if (remove)
/*     */     {
/* 303 */       while ((Factory.class.isAssignableFrom(clazz)) && (!clazz.equals(Object.class))) {
/* 304 */         clazz = clazz.getSuperclass();
/*     */       }
/*     */     }
/* 307 */     return clazz;
/*     */   }
/*     */ 
/*     */   public static class InsufficientPacketsException extends RuntimeException
/*     */   {
/*     */     private static final long serialVersionUID = 1L;
/*     */     private final boolean client;
/*     */     private final int packetCount;
/*     */ 
/*     */     private InsufficientPacketsException(String message, boolean client, int packetCount)
/*     */     {
/* 321 */       super();
/* 322 */       this.client = client;
/* 323 */       this.packetCount = packetCount;
/*     */     }
/*     */ 
/*     */     public boolean isClient() {
/* 327 */       return this.client;
/*     */     }
/*     */ 
/*     */     public int getPacketCount() {
/* 331 */       return this.packetCount;
/*     */     }
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.injector.packet.LegacyPacketRegistry
 * JD-Core Version:    0.6.2
 */