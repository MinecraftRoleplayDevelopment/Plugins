/*    */ package com.comphenix.protocol.async;
/*    */ 
/*    */ import com.comphenix.protocol.events.PacketEvent;
/*    */ import com.google.common.base.Preconditions;
/*    */ import com.google.common.collect.ComparisonChain;
/*    */ import com.google.common.primitives.Longs;
/*    */ 
/*    */ class PacketEventHolder
/*    */   implements Comparable<PacketEventHolder>
/*    */ {
/*    */   private PacketEvent event;
/* 33 */   private long sendingIndex = 0L;
/*    */ 
/*    */   public PacketEventHolder(PacketEvent event)
/*    */   {
/* 40 */     this.event = ((PacketEvent)Preconditions.checkNotNull(event, "Event must be non-null"));
/*    */ 
/* 42 */     if (event.getAsyncMarker() != null)
/* 43 */       this.sendingIndex = event.getAsyncMarker().getNewSendingIndex();
/*    */   }
/*    */ 
/*    */   public PacketEvent getEvent()
/*    */   {
/* 51 */     return this.event;
/*    */   }
/*    */ 
/*    */   public int compareTo(PacketEventHolder other)
/*    */   {
/* 56 */     return ComparisonChain.start().compare(this.sendingIndex, other.sendingIndex).result();
/*    */   }
/*    */ 
/*    */   public boolean equals(Object other)
/*    */   {
/* 64 */     if (other == this)
/* 65 */       return true;
/* 66 */     if ((other instanceof PacketEventHolder)) {
/* 67 */       return this.sendingIndex == ((PacketEventHolder)other).sendingIndex;
/*    */     }
/* 69 */     return false;
/*    */   }
/*    */ 
/*    */   public int hashCode()
/*    */   {
/* 74 */     return Longs.hashCode(this.sendingIndex);
/*    */   }
/*    */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.async.PacketEventHolder
 * JD-Core Version:    0.6.2
 */