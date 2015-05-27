/*     */ package com.comphenix.protocol.concurrency;
/*     */ 
/*     */ import com.comphenix.protocol.PacketType;
/*     */ import com.comphenix.protocol.injector.packet.PacketRegistry;
/*     */ import com.google.common.base.Preconditions;
/*     */ import com.google.common.collect.Maps;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.Set;
/*     */ 
/*     */ public class PacketTypeSet
/*     */ {
/*  17 */   private Set<PacketType> types = Collections.newSetFromMap(Maps.newConcurrentMap());
/*  18 */   private Set<Class<?>> classes = Collections.newSetFromMap(Maps.newConcurrentMap());
/*     */ 
/*     */   public PacketTypeSet()
/*     */   {
/*     */   }
/*     */ 
/*     */   public PacketTypeSet(Collection<? extends PacketType> values) {
/*  25 */     for (PacketType type : values)
/*  26 */       addType(type);
/*     */   }
/*     */ 
/*     */   public synchronized void addType(PacketType type)
/*     */   {
/*  35 */     Class packetClass = getPacketClass(type);
/*  36 */     this.types.add(Preconditions.checkNotNull(type, "type cannot be NULL."));
/*     */ 
/*  38 */     if (packetClass != null)
/*  39 */       this.classes.add(getPacketClass(type));
/*     */   }
/*     */ 
/*     */   public synchronized void addAll(Iterable<? extends PacketType> types)
/*     */   {
/*  48 */     for (PacketType type : types)
/*  49 */       addType(type);
/*     */   }
/*     */ 
/*     */   public synchronized void removeType(PacketType type)
/*     */   {
/*  58 */     Class packetClass = getPacketClass(type);
/*  59 */     this.types.remove(Preconditions.checkNotNull(type, "type cannot be NULL."));
/*     */ 
/*  61 */     if (packetClass != null)
/*  62 */       this.classes.remove(getPacketClass(type));
/*     */   }
/*     */ 
/*     */   public synchronized void removeAll(Iterable<? extends PacketType> types)
/*     */   {
/*  71 */     for (PacketType type : types)
/*  72 */       removeType(type);
/*     */   }
/*     */ 
/*     */   protected Class<?> getPacketClass(PacketType type)
/*     */   {
/*  82 */     return PacketRegistry.getPacketClassFromType(type);
/*     */   }
/*     */ 
/*     */   public boolean contains(PacketType type)
/*     */   {
/*  91 */     return this.types.contains(type);
/*     */   }
/*     */ 
/*     */   public boolean contains(Class<?> packetClass)
/*     */   {
/* 100 */     return this.classes.contains(packetClass);
/*     */   }
/*     */ 
/*     */   public boolean containsPacket(Object packet)
/*     */   {
/* 109 */     if (packet == null)
/* 110 */       return false;
/* 111 */     return this.classes.contains(packet.getClass());
/*     */   }
/*     */ 
/*     */   public Set<PacketType> values()
/*     */   {
/* 119 */     return this.types;
/*     */   }
/*     */ 
/*     */   public int size()
/*     */   {
/* 127 */     return this.types.size();
/*     */   }
/*     */ 
/*     */   public synchronized void clear() {
/* 131 */     this.types.clear();
/* 132 */     this.classes.clear();
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.concurrency.PacketTypeSet
 * JD-Core Version:    0.6.2
 */