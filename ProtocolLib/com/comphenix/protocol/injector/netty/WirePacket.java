/*    */ package com.comphenix.protocol.injector.netty;
/*    */ 
/*    */ import io.netty.buffer.ByteBuf;
/*    */ 
/*    */ public class WirePacket
/*    */ {
/*    */   private final int id;
/*    */   private final byte[] bytes;
/*    */ 
/*    */   public WirePacket(int id, byte[] bytes)
/*    */   {
/* 30 */     this.id = id;
/* 31 */     this.bytes = bytes;
/*    */   }
/*    */ 
/*    */   public int getId() {
/* 35 */     return this.id;
/*    */   }
/*    */ 
/*    */   public byte[] getBytes() {
/* 39 */     return this.bytes;
/*    */   }
/*    */ 
/*    */   public void writeId(ByteBuf output) {
/* 43 */     int i = this.id;
/* 44 */     while ((i & 0xFFFFFF80) != 0) {
/* 45 */       output.writeByte(i & 0x7F | 0x80);
/* 46 */       i >>>= 7;
/*    */     }
/*    */ 
/* 49 */     output.writeByte(i);
/*    */   }
/*    */ 
/*    */   public void writeBytes(ByteBuf output) {
/* 53 */     output.writeBytes(this.bytes);
/*    */   }
/*    */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.injector.netty.WirePacket
 * JD-Core Version:    0.6.2
 */