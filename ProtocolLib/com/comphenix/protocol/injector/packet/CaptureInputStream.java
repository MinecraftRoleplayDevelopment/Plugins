/*    */ package com.comphenix.protocol.injector.packet;
/*    */ 
/*    */ import java.io.FilterInputStream;
/*    */ import java.io.IOException;
/*    */ import java.io.InputStream;
/*    */ import java.io.OutputStream;
/*    */ 
/*    */ class CaptureInputStream extends FilterInputStream
/*    */ {
/*    */   protected OutputStream out;
/*    */ 
/*    */   public CaptureInputStream(InputStream in, OutputStream out)
/*    */   {
/* 17 */     super(in);
/* 18 */     this.out = out;
/*    */   }
/*    */ 
/*    */   public int read() throws IOException
/*    */   {
/* 23 */     int value = super.read();
/*    */ 
/* 26 */     if (value >= 0)
/* 27 */       this.out.write(value);
/* 28 */     return value;
/*    */   }
/*    */ 
/*    */   public void close() throws IOException
/*    */   {
/* 33 */     super.close();
/* 34 */     this.out.close();
/*    */   }
/*    */ 
/*    */   public int read(byte[] b) throws IOException
/*    */   {
/* 39 */     int count = super.read(b);
/*    */ 
/* 41 */     if (count > 0) {
/* 42 */       this.out.write(b, 0, count);
/*    */     }
/* 44 */     return count;
/*    */   }
/*    */ 
/*    */   public int read(byte[] b, int off, int len) throws IOException
/*    */   {
/* 49 */     int count = super.read(b, off, len);
/*    */ 
/* 51 */     if (count > 0) {
/* 52 */       this.out.write(b, off, count);
/*    */     }
/* 54 */     return count;
/*    */   }
/*    */ 
/*    */   public OutputStream getOutputStream()
/*    */   {
/* 62 */     return this.out;
/*    */   }
/*    */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.injector.packet.CaptureInputStream
 * JD-Core Version:    0.6.2
 */