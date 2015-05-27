/*    */ package com.comphenix.protocol.events;
/*    */ 
/*    */ import com.comphenix.protocol.PacketType.Sender;
/*    */ 
/*    */ public enum ConnectionSide
/*    */ {
/* 31 */   SERVER_SIDE, 
/*    */ 
/* 36 */   CLIENT_SIDE, 
/*    */ 
/* 41 */   BOTH;
/*    */ 
/*    */   public boolean isForClient() {
/* 44 */     return (this == CLIENT_SIDE) || (this == BOTH);
/*    */   }
/*    */ 
/*    */   public boolean isForServer() {
/* 48 */     return (this == SERVER_SIDE) || (this == BOTH);
/*    */   }
/*    */ 
/*    */   public PacketType.Sender getSender()
/*    */   {
/* 58 */     if (this == SERVER_SIDE)
/* 59 */       return PacketType.Sender.SERVER;
/* 60 */     if (this == CLIENT_SIDE)
/* 61 */       return PacketType.Sender.CLIENT;
/* 62 */     return null;
/*    */   }
/*    */ 
/*    */   public static ConnectionSide add(ConnectionSide a, ConnectionSide b)
/*    */   {
/* 74 */     if (a == null)
/* 75 */       return b;
/* 76 */     if (b == null) {
/* 77 */       return a;
/*    */     }
/*    */ 
/* 80 */     boolean client = (a.isForClient()) || (b.isForClient());
/* 81 */     boolean server = (a.isForServer()) || (b.isForServer());
/*    */ 
/* 83 */     if ((client) && (server))
/* 84 */       return BOTH;
/* 85 */     if (client) {
/* 86 */       return CLIENT_SIDE;
/*    */     }
/* 88 */     return SERVER_SIDE;
/*    */   }
/*    */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.events.ConnectionSide
 * JD-Core Version:    0.6.2
 */