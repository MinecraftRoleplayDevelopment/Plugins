/*     */ package com.comphenix.protocol.utility;
/*     */ 
/*     */ import com.comphenix.protocol.reflect.accessors.Accessors;
/*     */ import com.comphenix.protocol.reflect.accessors.FieldAccessor;
/*     */ import com.google.common.io.ByteStreams;
/*     */ import io.netty.buffer.AbstractByteBuf;
/*     */ import io.netty.buffer.ByteBuf;
/*     */ import io.netty.buffer.ByteBufAllocator;
/*     */ import java.io.DataInputStream;
/*     */ import java.io.DataOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.ByteOrder;
/*     */ import java.nio.channels.Channels;
/*     */ import java.nio.channels.GatheringByteChannel;
/*     */ import java.nio.channels.ScatteringByteChannel;
/*     */ import java.nio.channels.WritableByteChannel;
/*     */ 
/*     */ class ByteBufAdapter extends AbstractByteBuf
/*     */ {
/*     */   private DataInputStream input;
/*     */   private DataOutputStream output;
/*     */   private static FieldAccessor READER_INDEX;
/*     */   private static FieldAccessor WRITER_INDEX;
/*     */   private static final int CAPACITY = 32767;
/*     */ 
/*     */   private ByteBufAdapter(DataInputStream input, DataOutputStream output)
/*     */   {
/*  42 */     super(32767);
/*  43 */     this.input = input;
/*  44 */     this.output = output;
/*     */     try
/*     */     {
/*  48 */       if (READER_INDEX == null) {
/*  49 */         READER_INDEX = Accessors.getFieldAccessor(AbstractByteBuf.class.getDeclaredField("readerIndex"));
/*     */       }
/*  51 */       if (WRITER_INDEX == null)
/*  52 */         WRITER_INDEX = Accessors.getFieldAccessor(AbstractByteBuf.class.getDeclaredField("writerIndex"));
/*     */     }
/*     */     catch (Exception e) {
/*  55 */       throw new RuntimeException("Cannot initialize ByteBufAdapter.", e);
/*     */     }
/*     */ 
/*  59 */     if (input == null)
/*  60 */       READER_INDEX.set(this, Integer.valueOf(2147483647));
/*  61 */     if (output == null)
/*  62 */       WRITER_INDEX.set(this, Integer.valueOf(2147483647));
/*     */   }
/*     */ 
/*     */   public static ByteBuf packetReader(DataInputStream input)
/*     */   {
/*  71 */     return MinecraftReflection.getPacketDataSerializer(new ByteBufAdapter(input, null));
/*     */   }
/*     */ 
/*     */   public static ByteBuf packetWriter(DataOutputStream output)
/*     */   {
/*  80 */     return MinecraftReflection.getPacketDataSerializer(new ByteBufAdapter(null, output));
/*     */   }
/*     */ 
/*     */   public int refCnt()
/*     */   {
/*  85 */     return 1;
/*     */   }
/*     */ 
/*     */   public boolean release()
/*     */   {
/*  90 */     return false;
/*     */   }
/*     */ 
/*     */   public boolean release(int paramInt)
/*     */   {
/*  95 */     return false;
/*     */   }
/*     */ 
/*     */   protected byte _getByte(int paramInt)
/*     */   {
/*     */     try {
/* 101 */       return this.input.readByte();
/*     */     } catch (IOException e) {
/* 103 */       throw new RuntimeException("Cannot read input.", e);
/*     */     }
/*     */   }
/*     */ 
/*     */   protected short _getShort(int paramInt)
/*     */   {
/*     */     try {
/* 110 */       return this.input.readShort();
/*     */     } catch (IOException e) {
/* 112 */       throw new RuntimeException("Cannot read input.", e);
/*     */     }
/*     */   }
/*     */ 
/*     */   protected int _getUnsignedMedium(int paramInt)
/*     */   {
/*     */     try {
/* 119 */       return this.input.readUnsignedShort();
/*     */     } catch (IOException e) {
/* 121 */       throw new RuntimeException("Cannot read input.", e);
/*     */     }
/*     */   }
/*     */ 
/*     */   protected int _getInt(int paramInt)
/*     */   {
/*     */     try {
/* 128 */       return this.input.readInt();
/*     */     } catch (IOException e) {
/* 130 */       throw new RuntimeException("Cannot read input.", e);
/*     */     }
/*     */   }
/*     */ 
/*     */   protected long _getLong(int paramInt)
/*     */   {
/*     */     try {
/* 137 */       return this.input.readLong();
/*     */     } catch (IOException e) {
/* 139 */       throw new RuntimeException("Cannot read input.", e);
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void _setByte(int index, int value)
/*     */   {
/*     */     try {
/* 146 */       this.output.writeByte(value);
/*     */     } catch (IOException e) {
/* 148 */       throw new RuntimeException("Cannot write output.", e);
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void _setShort(int index, int value)
/*     */   {
/*     */     try {
/* 155 */       this.output.writeShort(value);
/*     */     } catch (IOException e) {
/* 157 */       throw new RuntimeException("Cannot write output.", e);
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void _setMedium(int index, int value)
/*     */   {
/*     */     try {
/* 164 */       this.output.writeShort(value);
/*     */     } catch (IOException e) {
/* 166 */       throw new RuntimeException("Cannot write output.", e);
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void _setInt(int index, int value)
/*     */   {
/*     */     try {
/* 173 */       this.output.writeInt(value);
/*     */     } catch (IOException e) {
/* 175 */       throw new RuntimeException("Cannot write output.", e);
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void _setLong(int index, long value)
/*     */   {
/*     */     try {
/* 182 */       this.output.writeLong(value);
/*     */     } catch (IOException e) {
/* 184 */       throw new RuntimeException("Cannot write output.", e);
/*     */     }
/*     */   }
/*     */ 
/*     */   public int capacity()
/*     */   {
/* 190 */     return 32767;
/*     */   }
/*     */ 
/*     */   public ByteBuf capacity(int paramInt)
/*     */   {
/* 195 */     return this;
/*     */   }
/*     */ 
/*     */   public ByteBufAllocator alloc()
/*     */   {
/* 200 */     return null;
/*     */   }
/*     */ 
/*     */   public ByteOrder order()
/*     */   {
/* 205 */     return ByteOrder.LITTLE_ENDIAN;
/*     */   }
/*     */ 
/*     */   public ByteBuf unwrap()
/*     */   {
/* 210 */     return null;
/*     */   }
/*     */ 
/*     */   public boolean isDirect()
/*     */   {
/* 215 */     return false;
/*     */   }
/*     */ 
/*     */   public ByteBuf getBytes(int index, ByteBuf dst, int dstIndex, int length)
/*     */   {
/*     */     try {
/* 221 */       for (int i = 0; i < length; i++)
/* 222 */         dst.setByte(dstIndex + i, this.input.read());
/*     */     }
/*     */     catch (IOException e) {
/* 225 */       throw new RuntimeException("Cannot read input.", e);
/*     */     }
/* 227 */     return this;
/*     */   }
/*     */ 
/*     */   public ByteBuf getBytes(int index, byte[] dst, int dstIndex, int length)
/*     */   {
/*     */     try {
/* 233 */       this.input.read(dst, dstIndex, length);
/*     */     } catch (IOException e) {
/* 235 */       throw new RuntimeException("Cannot read input.", e);
/*     */     }
/* 237 */     return this;
/*     */   }
/*     */ 
/*     */   public ByteBuf getBytes(int index, ByteBuffer dst)
/*     */   {
/*     */     try {
/* 243 */       dst.put(ByteStreams.toByteArray(this.input));
/*     */     } catch (IOException e) {
/* 245 */       throw new RuntimeException("Cannot read input.", e);
/*     */     }
/* 247 */     return this;
/*     */   }
/*     */ 
/*     */   public ByteBuf getBytes(int index, OutputStream dst, int length) throws IOException
/*     */   {
/* 252 */     ByteStreams.copy(ByteStreams.limit(this.input, length), dst);
/* 253 */     return this;
/*     */   }
/*     */ 
/*     */   public int getBytes(int index, GatheringByteChannel out, int length) throws IOException
/*     */   {
/* 258 */     byte[] data = ByteStreams.toByteArray(ByteStreams.limit(this.input, length));
/*     */ 
/* 260 */     out.write(ByteBuffer.wrap(data));
/* 261 */     return data.length;
/*     */   }
/*     */ 
/*     */   public ByteBuf setBytes(int index, ByteBuf src, int srcIndex, int length)
/*     */   {
/* 266 */     byte[] buffer = new byte[length];
/* 267 */     src.getBytes(srcIndex, buffer);
/*     */     try
/*     */     {
/* 270 */       this.output.write(buffer);
/* 271 */       return this;
/*     */     } catch (IOException e) {
/* 273 */       throw new RuntimeException("Cannot write output.", e);
/*     */     }
/*     */   }
/*     */ 
/*     */   public ByteBuf setBytes(int index, byte[] src, int srcIndex, int length)
/*     */   {
/*     */     try {
/* 280 */       this.output.write(src, srcIndex, length);
/* 281 */       return this;
/*     */     } catch (IOException e) {
/* 283 */       throw new RuntimeException("Cannot write output.", e);
/*     */     }
/*     */   }
/*     */ 
/*     */   public ByteBuf setBytes(int index, ByteBuffer src)
/*     */   {
/*     */     try {
/* 290 */       WritableByteChannel channel = Channels.newChannel(this.output);
/*     */ 
/* 292 */       channel.write(src);
/* 293 */       return this;
/*     */     } catch (IOException e) {
/* 295 */       throw new RuntimeException("Cannot write output.", e);
/*     */     }
/*     */   }
/*     */ 
/*     */   public int setBytes(int index, InputStream in, int length) throws IOException
/*     */   {
/* 301 */     InputStream limit = ByteStreams.limit(in, length);
/* 302 */     ByteStreams.copy(limit, this.output);
/* 303 */     return length - limit.available();
/*     */   }
/*     */ 
/*     */   public int setBytes(int index, ScatteringByteChannel in, int length) throws IOException
/*     */   {
/* 308 */     ByteBuffer buffer = ByteBuffer.allocate(length);
/* 309 */     WritableByteChannel channel = Channels.newChannel(this.output);
/*     */ 
/* 311 */     int count = in.read(buffer);
/* 312 */     channel.write(buffer);
/* 313 */     return count;
/*     */   }
/*     */ 
/*     */   public ByteBuf copy(int index, int length)
/*     */   {
/* 318 */     throw new UnsupportedOperationException("Cannot seek in input stream.");
/*     */   }
/*     */ 
/*     */   public int nioBufferCount()
/*     */   {
/* 323 */     return 0;
/*     */   }
/*     */ 
/*     */   public ByteBuffer nioBuffer(int paramInt1, int paramInt2)
/*     */   {
/* 328 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   public ByteBuffer internalNioBuffer(int paramInt1, int paramInt2)
/*     */   {
/* 333 */     return null;
/*     */   }
/*     */ 
/*     */   public ByteBuffer[] nioBuffers(int paramInt1, int paramInt2)
/*     */   {
/* 338 */     return null;
/*     */   }
/*     */ 
/*     */   public boolean hasArray()
/*     */   {
/* 343 */     return false;
/*     */   }
/*     */ 
/*     */   public byte[] array()
/*     */   {
/* 348 */     return null;
/*     */   }
/*     */ 
/*     */   public int arrayOffset()
/*     */   {
/* 353 */     return 0;
/*     */   }
/*     */ 
/*     */   public boolean hasMemoryAddress()
/*     */   {
/* 358 */     return false;
/*     */   }
/*     */ 
/*     */   public long memoryAddress()
/*     */   {
/* 363 */     return 0L;
/*     */   }
/*     */ 
/*     */   public ByteBuf retain(int paramInt)
/*     */   {
/* 368 */     return this;
/*     */   }
/*     */ 
/*     */   public ByteBuf retain()
/*     */   {
/* 373 */     return this;
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.utility.ByteBufAdapter
 * JD-Core Version:    0.6.2
 */