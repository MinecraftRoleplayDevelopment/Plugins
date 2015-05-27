/*    */ package com.comphenix.protocol.injector.packet;
/*    */ 
/*    */ import com.comphenix.protocol.PacketType;
/*    */ import com.comphenix.protocol.events.ConnectionSide;
/*    */ import com.comphenix.protocol.events.NetworkMarker;
/*    */ import com.google.common.io.ByteSource;
/*    */ import com.google.common.primitives.Bytes;
/*    */ import java.io.ByteArrayInputStream;
/*    */ import java.io.DataInputStream;
/*    */ import java.io.IOException;
/*    */ import java.io.InputStream;
/*    */ import java.nio.ByteBuffer;
/*    */ import javax.annotation.Nonnull;
/*    */ 
/*    */ public class LegacyNetworkMarker extends NetworkMarker
/*    */ {
/*    */   public LegacyNetworkMarker(@Nonnull ConnectionSide side, byte[] inputBuffer, PacketType type)
/*    */   {
/* 23 */     super(side, inputBuffer, type);
/*    */   }
/*    */ 
/*    */   public LegacyNetworkMarker(@Nonnull ConnectionSide side, ByteBuffer inputBuffer, PacketType type) {
/* 27 */     super(side, inputBuffer, type);
/*    */   }
/*    */ 
/*    */   protected DataInputStream skipHeader(DataInputStream input)
/*    */     throws IOException
/*    */   {
/* 33 */     return input;
/*    */   }
/*    */ 
/*    */   protected ByteBuffer addHeader(ByteBuffer buffer, PacketType type)
/*    */   {
/* 38 */     return ByteBuffer.wrap(Bytes.concat(new byte[][] { { (byte)type.getLegacyId() }, buffer.array() }));
/*    */   }
/*    */ 
/*    */   protected DataInputStream addHeader(final DataInputStream input, final PacketType type)
/*    */   {
/* 43 */     ByteSource header = new ByteSource()
/*    */     {
/*    */       public InputStream openStream() throws IOException {
/* 46 */         byte[] data = { (byte)type.getLegacyId() };
/* 47 */         return new ByteArrayInputStream(data);
/*    */       }
/*    */     };
/* 51 */     ByteSource data = new ByteSource()
/*    */     {
/*    */       public InputStream openStream() throws IOException {
/* 54 */         return input;
/*    */       }
/*    */ 
/*    */     };
/*    */     try
/*    */     {
/* 60 */       return new DataInputStream(ByteSource.concat(new ByteSource[] { header, data }).openStream());
/*    */     } catch (IOException e) {
/* 62 */       throw new RuntimeException("Cannot add header.", e);
/*    */     }
/*    */   }
/*    */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.injector.packet.LegacyNetworkMarker
 * JD-Core Version:    0.6.2
 */