/*    */ package com.comphenix.protocol.utility;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.io.OutputStream;
/*    */ import java.nio.ByteBuffer;
/*    */ 
/*    */ public class ByteBufferOutputStream extends OutputStream
/*    */ {
/*    */   ByteBuffer buf;
/*    */ 
/*    */   public ByteBufferOutputStream(ByteBuffer buf)
/*    */   {
/* 15 */     this.buf = buf;
/*    */   }
/*    */ 
/*    */   public void write(int b) throws IOException {
/* 19 */     this.buf.put((byte)b);
/*    */   }
/*    */ 
/*    */   public void write(byte[] bytes, int off, int len) throws IOException
/*    */   {
/* 24 */     this.buf.put(bytes, off, len);
/*    */   }
/*    */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.utility.ByteBufferOutputStream
 * JD-Core Version:    0.6.2
 */