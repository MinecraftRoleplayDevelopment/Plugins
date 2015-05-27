/*    */ package com.comphenix.protocol.utility;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.io.InputStream;
/*    */ import java.nio.ByteBuffer;
/*    */ 
/*    */ public class ByteBufferInputStream extends InputStream
/*    */ {
/*    */   private ByteBuffer buf;
/*    */ 
/*    */   public ByteBufferInputStream(ByteBuffer buf)
/*    */   {
/* 15 */     this.buf = buf;
/*    */   }
/*    */ 
/*    */   public int read() throws IOException {
/* 19 */     if (!this.buf.hasRemaining()) {
/* 20 */       return -1;
/*    */     }
/* 22 */     return this.buf.get() & 0xFF;
/*    */   }
/*    */ 
/*    */   public int read(byte[] bytes, int off, int len) throws IOException
/*    */   {
/* 27 */     if (!this.buf.hasRemaining()) {
/* 28 */       return -1;
/*    */     }
/*    */ 
/* 31 */     len = Math.min(len, this.buf.remaining());
/* 32 */     this.buf.get(bytes, off, len);
/* 33 */     return len;
/*    */   }
/*    */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.utility.ByteBufferInputStream
 * JD-Core Version:    0.6.2
 */