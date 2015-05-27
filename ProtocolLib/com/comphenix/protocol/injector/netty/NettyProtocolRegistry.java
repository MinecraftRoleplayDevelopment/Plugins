/*     */ package com.comphenix.protocol.injector.netty;
/*     */ 
/*     */ import com.comphenix.protocol.PacketType;
/*     */ import com.comphenix.protocol.PacketType.Protocol;
/*     */ import com.comphenix.protocol.PacketType.Sender;
/*     */ import com.comphenix.protocol.injector.packet.MapContainer;
/*     */ import com.comphenix.protocol.reflect.StructureModifier;
/*     */ import com.comphenix.protocol.utility.MinecraftReflection;
/*     */ import com.comphenix.protocol.utility.MinecraftVersion;
/*     */ import com.google.common.collect.BiMap;
/*     */ import com.google.common.collect.HashBiMap;
/*     */ import com.google.common.collect.Iterables;
/*     */ import com.google.common.collect.Lists;
/*     */ import com.google.common.collect.Maps;
/*     */ import com.google.common.collect.Sets;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.Set;
/*     */ 
/*     */ public class NettyProtocolRegistry
/*     */ {
/*     */   private Class<?> enumProtocol;
/*     */   private volatile Register register;
/*     */ 
/*     */   public NettyProtocolRegistry()
/*     */   {
/*  60 */     this.enumProtocol = MinecraftReflection.getEnumProtocolClass();
/*  61 */     initialize();
/*     */   }
/*     */ 
/*     */   public Map<PacketType, Class<?>> getPacketTypeLookup()
/*     */   {
/*  69 */     return Collections.unmodifiableMap(this.register.typeToClass);
/*     */   }
/*     */ 
/*     */   public Map<Class<?>, PacketType> getPacketClassLookup()
/*     */   {
/*  77 */     return Collections.unmodifiableMap(this.register.typeToClass.inverse());
/*     */   }
/*     */ 
/*     */   public Set<PacketType> getClientPackets()
/*     */   {
/*  85 */     return Collections.unmodifiableSet(this.register.clientPackets);
/*     */   }
/*     */ 
/*     */   public Set<PacketType> getServerPackets()
/*     */   {
/*  93 */     return Collections.unmodifiableSet(this.register.serverPackets);
/*     */   }
/*     */ 
/*     */   public synchronized void synchronize()
/*     */   {
/* 103 */     if (this.register.isOutdated())
/* 104 */       initialize();
/*     */   }
/*     */ 
/*     */   private synchronized void initialize()
/*     */   {
/* 112 */     Object[] protocols = this.enumProtocol.getEnumConstants();
/*     */ 
/* 115 */     if (MinecraftVersion.getCurrentVersion().compareTo(MinecraftVersion.BOUNTIFUL_UPDATE) < 0) {
/* 116 */       initialize17();
/* 117 */       return;
/*     */     }
/*     */ 
/* 121 */     Map serverMaps = Maps.newLinkedHashMap();
/* 122 */     Map clientMaps = Maps.newLinkedHashMap();
/*     */ 
/* 124 */     Register result = new Register(null);
/* 125 */     StructureModifier modifier = null;
/*     */     Object protocol;
/* 128 */     for (protocol : protocols) {
/* 129 */       if (modifier == null)
/* 130 */         modifier = new StructureModifier(protocol.getClass().getSuperclass(), false);
/* 131 */       StructureModifier maps = modifier.withTarget(protocol).withType(Map.class);
/* 132 */       for (Map.Entry entry : ((Map)maps.read(0)).entrySet()) {
/* 133 */         String direction = entry.getKey().toString();
/* 134 */         if (direction.contains("CLIENTBOUND"))
/* 135 */           serverMaps.put(protocol, entry.getValue());
/* 136 */         else if (direction.contains("SERVERBOUND")) {
/* 137 */           clientMaps.put(protocol, entry.getValue());
/*     */         }
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 143 */     for (Map map : serverMaps.values()) {
/* 144 */       result.containers.add(new MapContainer(map));
/*     */     }
/*     */ 
/* 147 */     for (Map map : clientMaps.values()) {
/* 148 */       result.containers.add(new MapContainer(map));
/*     */     }
/*     */ 
/* 159 */     for (int i = 0; i < protocols.length; i++) {
/* 160 */       Object protocol = protocols[i];
/* 161 */       Enum enumProtocol = (Enum)protocol;
/* 162 */       PacketType.Protocol equivalent = PacketType.Protocol.fromVanilla(enumProtocol);
/*     */ 
/* 165 */       if (serverMaps.containsKey(protocol))
/* 166 */         associatePackets(result, (Map)serverMaps.get(protocol), equivalent, PacketType.Sender.SERVER);
/* 167 */       if (clientMaps.containsKey(protocol)) {
/* 168 */         associatePackets(result, (Map)clientMaps.get(protocol), equivalent, PacketType.Sender.CLIENT);
/*     */       }
/*     */     }
/*     */ 
/* 172 */     this.register = result;
/*     */   }
/*     */ 
/*     */   private synchronized void initialize17() {
/* 176 */     Object[] protocols = this.enumProtocol.getEnumConstants();
/* 177 */     List serverMaps = Lists.newArrayList();
/* 178 */     List clientMaps = Lists.newArrayList();
/* 179 */     StructureModifier modifier = null;
/*     */ 
/* 182 */     Register result = new Register(null);
/*     */ 
/* 184 */     for (Object protocol : protocols) {
/* 185 */       if (modifier == null)
/* 186 */         modifier = new StructureModifier(protocol.getClass().getSuperclass(), false);
/* 187 */       StructureModifier maps = modifier.withTarget(protocol).withType(Map.class);
/*     */ 
/* 189 */       serverMaps.add(maps.read(0));
/* 190 */       clientMaps.add(maps.read(1));
/*     */     }
/*     */ 
/* 193 */     for (Map map : Iterables.concat(serverMaps, clientMaps)) {
/* 194 */       result.containers.add(new MapContainer(map));
/*     */     }
/*     */ 
/* 198 */     if (sum(clientMaps) > sum(serverMaps))
/*     */     {
/* 200 */       List temp = serverMaps;
/* 201 */       serverMaps = clientMaps;
/* 202 */       clientMaps = temp;
/*     */     }
/*     */ 
/* 205 */     for (int i = 0; i < protocols.length; i++) {
/* 206 */       Enum enumProtocol = (Enum)protocols[i];
/* 207 */       PacketType.Protocol equivalent = PacketType.Protocol.fromVanilla(enumProtocol);
/*     */ 
/* 210 */       associatePackets(result, (Map)serverMaps.get(i), equivalent, PacketType.Sender.SERVER);
/* 211 */       associatePackets(result, (Map)clientMaps.get(i), equivalent, PacketType.Sender.CLIENT);
/*     */     }
/*     */ 
/* 215 */     this.register = result;
/*     */   }
/*     */ 
/*     */   private void associatePackets(Register register, Map<Integer, Class<?>> lookup, PacketType.Protocol protocol, PacketType.Sender sender) {
/* 219 */     for (Map.Entry entry : lookup.entrySet()) {
/* 220 */       PacketType type = PacketType.fromCurrent(protocol, sender, ((Integer)entry.getKey()).intValue(), -1);
/* 221 */       register.typeToClass.put(type, entry.getValue());
/*     */ 
/* 223 */       if (sender == PacketType.Sender.SERVER)
/* 224 */         register.serverPackets.add(type);
/* 225 */       if (sender == PacketType.Sender.CLIENT)
/* 226 */         register.clientPackets.add(type);
/*     */     }
/*     */   }
/*     */ 
/*     */   private int sum(Iterable<? extends Map<Integer, Class<?>>> maps)
/*     */   {
/* 236 */     int count = 0;
/*     */ 
/* 238 */     for (Map map : maps)
/* 239 */       count += map.size();
/* 240 */     return count;
/*     */   }
/*     */ 
/*     */   private static class Register
/*     */   {
/*  35 */     public BiMap<PacketType, Class<?>> typeToClass = HashBiMap.create();
/*  36 */     public volatile Set<PacketType> serverPackets = Sets.newHashSet();
/*  37 */     public volatile Set<PacketType> clientPackets = Sets.newHashSet();
/*  38 */     public List<MapContainer> containers = Lists.newArrayList();
/*     */ 
/*     */     public boolean isOutdated()
/*     */     {
/*  45 */       for (MapContainer container : this.containers) {
/*  46 */         if (container.hasChanged()) {
/*  47 */           return true;
/*     */         }
/*     */       }
/*  50 */       return false;
/*     */     }
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.injector.netty.NettyProtocolRegistry
 * JD-Core Version:    0.6.2
 */