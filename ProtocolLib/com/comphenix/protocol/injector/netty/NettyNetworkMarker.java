/*    */ package com.comphenix.protocol.injector.netty;
/*    */ 
/*    */ import com.comphenix.protocol.PacketType;
/*    */ import com.comphenix.protocol.events.ConnectionSide;
/*    */ import com.comphenix.protocol.events.NetworkMarker;
/*    */ import com.comphenix.protocol.utility.StreamSerializer;
/*    */ import java.io.DataInputStream;
/*    */ import java.io.IOException;
/*    */ import java.nio.ByteBuffer;
/*    */ import javax.annotation.Nonnull;
/*    */ 
/*    */ class NettyNetworkMarker extends NetworkMarker
/*    */ {
/*    */   public NettyNetworkMarker(@Nonnull ConnectionSide side, byte[] inputBuffer)
/*    */   {
/* 15 */     super(side, inputBuffer, null);
/*    */   }
/*    */ 
/*    */   public NettyNetworkMarker(@Nonnull ConnectionSide side, ByteBuffer inputBuffer) {
/* 19 */     super(side, inputBuffer, null);
/*    */   }
/*    */ 
/*    */   protected DataInputStream skipHeader(DataInputStream input)
/*    */     throws IOException
/*    */   {
/* 25 */     getSerializer().deserializeVarInt(input);
/* 26 */     return input;
/*    */   }
/*    */ 
/*    */   protected ByteBuffer addHeader(ByteBuffer buffer, PacketType type)
/*    */   {
/* 32 */     return buffer;
/*    */   }
/*    */ 
/*    */   protected DataInputStream addHeader(DataInputStream input, PacketType type)
/*    */   {
/* 38 */     return input;
/*    */   }
/*    */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.injector.netty.NettyNetworkMarker
 * JD-Core Version:    0.6.2
 */