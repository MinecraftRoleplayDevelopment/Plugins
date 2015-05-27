/*    */ package com.comphenix.protocol.injector.server;
/*    */ 
/*    */ import com.comphenix.protocol.events.NetworkMarker;
/*    */ 
/*    */ class QueuedSendPacket
/*    */ {
/*    */   private final Object packet;
/*    */   private final NetworkMarker marker;
/*    */   private final boolean filtered;
/*    */ 
/*    */   public QueuedSendPacket(Object packet, NetworkMarker marker, boolean filtered)
/*    */   {
/* 15 */     this.packet = packet;
/* 16 */     this.marker = marker;
/* 17 */     this.filtered = filtered;
/*    */   }
/*    */ 
/*    */   public NetworkMarker getMarker()
/*    */   {
/* 25 */     return this.marker;
/*    */   }
/*    */ 
/*    */   public Object getPacket()
/*    */   {
/* 33 */     return this.packet;
/*    */   }
/*    */ 
/*    */   public boolean isFiltered()
/*    */   {
/* 41 */     return this.filtered;
/*    */   }
/*    */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.injector.server.QueuedSendPacket
 * JD-Core Version:    0.6.2
 */