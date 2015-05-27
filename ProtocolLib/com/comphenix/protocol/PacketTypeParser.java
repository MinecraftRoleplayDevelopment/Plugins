/*     */ package com.comphenix.protocol;
/*     */ 
/*     */ import com.comphenix.protocol.events.ConnectionSide;
/*     */ import com.google.common.collect.ContiguousSet;
/*     */ import com.google.common.collect.DiscreteDomain;
/*     */ import com.google.common.collect.Lists;
/*     */ import com.google.common.collect.Range;
/*     */ import com.google.common.collect.Sets;
/*     */ import java.util.Collection;
/*     */ import java.util.Deque;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Set;
/*     */ 
/*     */ class PacketTypeParser
/*     */ {
/*  19 */   public static final Range<Integer> DEFAULT_MAX_RANGE = Range.closed(Integer.valueOf(0), Integer.valueOf(255));
/*     */ 
/*  21 */   private PacketType.Sender side = null;
/*  22 */   private PacketType.Protocol protocol = null;
/*     */ 
/*     */   public Set<PacketType> parseTypes(Deque<String> arguments, Range<Integer> defaultRange) {
/*  25 */     Set result = Sets.newHashSet();
/*  26 */     this.side = null;
/*  27 */     this.protocol = null;
/*     */ 
/*  30 */     while (this.side == null) {
/*  31 */       String arg = (String)arguments.poll();
/*     */ 
/*  34 */       if (this.side == null) {
/*  35 */         ConnectionSide connection = parseSide(arg);
/*     */ 
/*  37 */         if (connection != null) {
/*  38 */           this.side = connection.getSender();
/*     */         }
/*     */ 
/*     */       }
/*  42 */       else if ((this.protocol != null) || 
/*  43 */         ((this.protocol = parseProtocol(arg)) == null))
/*     */       {
/*  47 */         throw new IllegalArgumentException("Specify connection side (CLIENT or SERVER).");
/*     */       }
/*     */     }
/*     */ 
/*  51 */     List ranges = RangeParser.getRanges(arguments, DEFAULT_MAX_RANGE);
/*     */     Iterator it;
/*  54 */     if (this.protocol != null) {
/*  55 */       for (it = arguments.iterator(); it.hasNext(); ) {
/*  56 */         String name = ((String)it.next()).toUpperCase();
/*  57 */         Collection names = PacketType.fromName(name);
/*     */ 
/*  59 */         for (PacketType type : names) {
/*  60 */           if ((type.getProtocol() == this.protocol) && (type.getSender() == this.side)) {
/*  61 */             result.add(type);
/*  62 */             it.remove();
/*     */           }
/*     */         }
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/*  69 */     if ((ranges.isEmpty()) && (result.isEmpty())) {
/*  70 */       ranges = Lists.newArrayList();
/*  71 */       ranges.add(defaultRange);
/*     */     }
/*     */ 
/*  74 */     for (Range range : ranges) {
/*  75 */       for (Integer id : ContiguousSet.create(range, DiscreteDomain.integers()))
/*     */       {
/*  77 */         if (this.protocol == null) {
/*  78 */           if (PacketType.hasLegacy(id.intValue())) {
/*  79 */             result.add(PacketType.findLegacy(id.intValue(), this.side));
/*     */           }
/*     */         }
/*  82 */         else if (PacketType.hasCurrent(this.protocol, this.side, id.intValue())) {
/*  83 */           result.add(PacketType.findCurrent(this.protocol, this.side, id.intValue()));
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/*  88 */     return result;
/*     */   }
/*     */ 
/*     */   public PacketType.Protocol getLastProtocol()
/*     */   {
/*  96 */     return this.protocol;
/*     */   }
/*     */ 
/*     */   public PacketType.Sender getLastSide()
/*     */   {
/* 104 */     return this.side;
/*     */   }
/*     */ 
/*     */   public ConnectionSide parseSide(String text)
/*     */   {
/* 113 */     if (text == null)
/* 114 */       return null;
/* 115 */     String candidate = text.toLowerCase();
/*     */ 
/* 118 */     if ("client".startsWith(candidate))
/* 119 */       return ConnectionSide.CLIENT_SIDE;
/* 120 */     if ("server".startsWith(candidate)) {
/* 121 */       return ConnectionSide.SERVER_SIDE;
/*     */     }
/* 123 */     return null;
/*     */   }
/*     */ 
/*     */   public PacketType.Protocol parseProtocol(String text)
/*     */   {
/* 132 */     if (text == null)
/* 133 */       return null;
/* 134 */     String candidate = text.toLowerCase();
/*     */ 
/* 136 */     if (("handshake".equals(candidate)) || ("handshaking".equals(candidate)))
/* 137 */       return PacketType.Protocol.HANDSHAKING;
/* 138 */     if ("login".equals(candidate))
/* 139 */       return PacketType.Protocol.LOGIN;
/* 140 */     if (("play".equals(candidate)) || ("game".equals(candidate)))
/* 141 */       return PacketType.Protocol.PLAY;
/* 142 */     if ("status".equals(candidate)) {
/* 143 */       return PacketType.Protocol.STATUS;
/*     */     }
/* 145 */     return null;
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.PacketTypeParser
 * JD-Core Version:    0.6.2
 */