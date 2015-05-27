/*     */ package com.comphenix.protocol;
/*     */ 
/*     */ import com.comphenix.protocol.collections.IntegerMap;
/*     */ import com.google.common.base.Preconditions;
/*     */ import com.google.common.collect.HashMultimap;
/*     */ import com.google.common.collect.Multimap;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ 
/*     */ class PacketTypeLookup
/*     */ {
/*  52 */   private final IntegerMap<PacketType> legacyLookup = new IntegerMap();
/*  53 */   private final IntegerMap<PacketType> serverLookup = new IntegerMap();
/*  54 */   private final IntegerMap<PacketType> clientLookup = new IntegerMap();
/*     */ 
/*  57 */   private final ProtocolSenderLookup currentLookup = new ProtocolSenderLookup(null);
/*     */ 
/*  60 */   private final Multimap<String, PacketType> nameLookup = HashMultimap.create();
/*     */ 
/*     */   public PacketTypeLookup addPacketTypes(Iterable<? extends PacketType> types)
/*     */   {
/*  67 */     Preconditions.checkNotNull(types, "types cannot be NULL");
/*     */ 
/*  69 */     for (PacketType type : types) {
/*  70 */       int legacy = type.getLegacyId();
/*     */ 
/*  73 */       if (legacy != -1) {
/*  74 */         if (type.isServer())
/*  75 */           this.serverLookup.put(type.getLegacyId(), type);
/*  76 */         if (type.isClient())
/*  77 */           this.clientLookup.put(type.getLegacyId(), type);
/*  78 */         this.legacyLookup.put(type.getLegacyId(), type);
/*     */       }
/*     */ 
/*  81 */       if (type.getCurrentId() != -1) {
/*  82 */         this.currentLookup.getMap(type.getProtocol(), type.getSender()).put(type.getCurrentId(), type);
/*     */       }
/*     */ 
/*  85 */       this.nameLookup.put(type.name(), type);
/*     */     }
/*  87 */     return this;
/*     */   }
/*     */ 
/*     */   public PacketType getFromLegacy(int packetId)
/*     */   {
/*  96 */     return (PacketType)this.legacyLookup.get(packetId);
/*     */   }
/*     */ 
/*     */   public Collection<PacketType> getFromName(String name)
/*     */   {
/* 105 */     return Collections.unmodifiableCollection(this.nameLookup.get(name));
/*     */   }
/*     */ 
/*     */   public PacketType getFromLegacy(int packetId, PacketType.Sender preference)
/*     */   {
/* 115 */     if (preference == PacketType.Sender.CLIENT) {
/* 116 */       return (PacketType)getFirst(packetId, this.clientLookup, this.serverLookup);
/*     */     }
/* 118 */     return (PacketType)getFirst(packetId, this.serverLookup, this.clientLookup);
/*     */   }
/*     */ 
/*     */   private <T> T getFirst(int packetId, IntegerMap<T> first, IntegerMap<T> second)
/*     */   {
/* 123 */     if (first.containsKey(packetId)) {
/* 124 */       return first.get(packetId);
/*     */     }
/* 126 */     return second.get(packetId);
/*     */   }
/*     */ 
/*     */   public PacketType getFromCurrent(PacketType.Protocol protocol, PacketType.Sender sender, int packetId)
/*     */   {
/* 137 */     return (PacketType)this.currentLookup.getMap(protocol, sender).get(packetId);
/*     */   }
/*     */ 
/*     */   private static class ProtocolSenderLookup
/*     */   {
/*  20 */     public final IntegerMap<PacketType> HANDSHAKE_CLIENT = IntegerMap.newMap();
/*  21 */     public final IntegerMap<PacketType> HANDSHAKE_SERVER = IntegerMap.newMap();
/*  22 */     public final IntegerMap<PacketType> GAME_CLIENT = IntegerMap.newMap();
/*  23 */     public final IntegerMap<PacketType> GAME_SERVER = IntegerMap.newMap();
/*  24 */     public final IntegerMap<PacketType> STATUS_CLIENT = IntegerMap.newMap();
/*  25 */     public final IntegerMap<PacketType> STATUS_SERVER = IntegerMap.newMap();
/*  26 */     public final IntegerMap<PacketType> LOGIN_CLIENT = IntegerMap.newMap();
/*  27 */     public final IntegerMap<PacketType> LOGIN_SERVER = IntegerMap.newMap();
/*     */ 
/*     */     public IntegerMap<PacketType> getMap(PacketType.Protocol protocol, PacketType.Sender sender)
/*     */     {
/*  36 */       switch (PacketTypeLookup.1.$SwitchMap$com$comphenix$protocol$PacketType$Protocol[protocol.ordinal()]) {
/*     */       case 1:
/*  38 */         return sender == PacketType.Sender.CLIENT ? this.HANDSHAKE_CLIENT : this.HANDSHAKE_SERVER;
/*     */       case 2:
/*  40 */         return sender == PacketType.Sender.CLIENT ? this.GAME_CLIENT : this.GAME_SERVER;
/*     */       case 3:
/*  42 */         return sender == PacketType.Sender.CLIENT ? this.STATUS_CLIENT : this.STATUS_SERVER;
/*     */       case 4:
/*  44 */         return sender == PacketType.Sender.CLIENT ? this.LOGIN_CLIENT : this.LOGIN_SERVER;
/*     */       }
/*  46 */       throw new IllegalArgumentException("Unable to find protocol " + protocol);
/*     */     }
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.PacketTypeLookup
 * JD-Core Version:    0.6.2
 */