/*     */ package com.comphenix.protocol.events;
/*     */ 
/*     */ import com.comphenix.protocol.PacketType;
/*     */ import com.comphenix.protocol.utility.ByteBufferInputStream;
/*     */ import com.comphenix.protocol.utility.MinecraftReflection;
/*     */ import com.comphenix.protocol.utility.StreamSerializer;
/*     */ import com.google.common.base.Preconditions;
/*     */ import com.google.common.collect.Lists;
/*     */ import com.google.common.primitives.Ints;
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.DataInputStream;
/*     */ import java.io.IOException;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.Comparator;
/*     */ import java.util.List;
/*     */ import java.util.PriorityQueue;
/*     */ import javax.annotation.Nonnull;
/*     */ 
/*     */ public abstract class NetworkMarker
/*     */ {
/*     */   private PriorityQueue<PacketOutputHandler> outputHandlers;
/*     */   private List<PacketPostListener> postListeners;
/*     */   private List<ScheduledPacket> scheduledPackets;
/*     */   private ByteBuffer inputBuffer;
/*     */   private final ConnectionSide side;
/*     */   private final PacketType type;
/*     */   private StreamSerializer serializer;
/*     */ 
/*     */   public NetworkMarker(@Nonnull ConnectionSide side, ByteBuffer inputBuffer, PacketType type)
/*     */   {
/*  75 */     this.side = ((ConnectionSide)Preconditions.checkNotNull(side, "side cannot be NULL."));
/*  76 */     this.inputBuffer = inputBuffer;
/*  77 */     this.type = type;
/*     */   }
/*     */ 
/*     */   public NetworkMarker(@Nonnull ConnectionSide side, byte[] inputBuffer, PacketType type)
/*     */   {
/*  89 */     this.side = ((ConnectionSide)Preconditions.checkNotNull(side, "side cannot be NULL."));
/*  90 */     this.type = type;
/*     */ 
/*  92 */     if (inputBuffer != null)
/*  93 */       this.inputBuffer = ByteBuffer.wrap(inputBuffer);
/*     */   }
/*     */ 
/*     */   public ConnectionSide getSide()
/*     */   {
/* 102 */     return this.side;
/*     */   }
/*     */ 
/*     */   public StreamSerializer getSerializer()
/*     */   {
/* 110 */     if (this.serializer == null)
/* 111 */       this.serializer = new StreamSerializer();
/* 112 */     return this.serializer;
/*     */   }
/*     */ 
/*     */   public ByteBuffer getInputBuffer()
/*     */   {
/* 125 */     return getInputBuffer(true);
/*     */   }
/*     */ 
/*     */   public ByteBuffer getInputBuffer(boolean excludeHeader)
/*     */   {
/* 139 */     if (this.side.isForServer()) {
/* 140 */       throw new IllegalStateException("Server-side packets have no input buffer.");
/*     */     }
/* 142 */     if (this.inputBuffer != null) {
/* 143 */       ByteBuffer result = this.inputBuffer.asReadOnlyBuffer();
/*     */       try
/*     */       {
/* 146 */         if (excludeHeader)
/* 147 */           result = skipHeader(result);
/*     */         else
/* 149 */           result = addHeader(result, this.type);
/*     */       } catch (IOException e) {
/* 151 */         throw new RuntimeException("Cannot skip packet header.", e);
/*     */       }
/* 153 */       return result;
/*     */     }
/* 155 */     return null;
/*     */   }
/*     */ 
/*     */   public DataInputStream getInputStream()
/*     */   {
/* 166 */     return getInputStream(true);
/*     */   }
/*     */ 
/*     */   public DataInputStream getInputStream(boolean excludeHeader)
/*     */   {
/* 179 */     if (this.side.isForServer())
/* 180 */       throw new IllegalStateException("Server-side packets have no input buffer.");
/* 181 */     if (this.inputBuffer == null) {
/* 182 */       return null;
/*     */     }
/* 184 */     DataInputStream input = new DataInputStream(new ByteArrayInputStream(this.inputBuffer.array()));
/*     */     try
/*     */     {
/* 189 */       if (excludeHeader)
/* 190 */         input = skipHeader(input);
/*     */       else
/* 192 */         input = addHeader(input, this.type);
/*     */     } catch (IOException e) {
/* 194 */       throw new RuntimeException("Cannot skip packet header.", e);
/*     */     }
/* 196 */     return input;
/*     */   }
/*     */ 
/*     */   public boolean requireOutputHeader()
/*     */   {
/* 204 */     return MinecraftReflection.isUsingNetty();
/*     */   }
/*     */ 
/*     */   public boolean addOutputHandler(@Nonnull PacketOutputHandler handler)
/*     */   {
/* 218 */     checkServerSide();
/* 219 */     Preconditions.checkNotNull(handler, "handler cannot be NULL.");
/*     */ 
/* 222 */     if (this.outputHandlers == null) {
/* 223 */       this.outputHandlers = new PriorityQueue(10, new Comparator()
/*     */       {
/*     */         public int compare(PacketOutputHandler o1, PacketOutputHandler o2) {
/* 226 */           return Ints.compare(o1.getPriority().getSlot(), o2.getPriority().getSlot());
/*     */         }
/*     */       });
/*     */     }
/* 230 */     return this.outputHandlers.add(handler);
/*     */   }
/*     */ 
/*     */   public boolean removeOutputHandler(@Nonnull PacketOutputHandler handler)
/*     */   {
/* 241 */     checkServerSide();
/* 242 */     Preconditions.checkNotNull(handler, "handler cannot be NULL.");
/*     */ 
/* 244 */     if (this.outputHandlers != null) {
/* 245 */       return this.outputHandlers.remove(handler);
/*     */     }
/* 247 */     return false;
/*     */   }
/*     */ 
/*     */   @Nonnull
/*     */   public Collection<PacketOutputHandler> getOutputHandlers()
/*     */   {
/* 256 */     if (this.outputHandlers != null) {
/* 257 */       return this.outputHandlers;
/*     */     }
/* 259 */     return Collections.emptyList();
/*     */   }
/*     */ 
/*     */   public boolean addPostListener(PacketPostListener listener)
/*     */   {
/* 277 */     if (this.postListeners == null)
/* 278 */       this.postListeners = Lists.newArrayList();
/* 279 */     return this.postListeners.add(listener);
/*     */   }
/*     */ 
/*     */   public boolean removePostListener(PacketPostListener listener)
/*     */   {
/* 288 */     if (this.postListeners != null) {
/* 289 */       return this.postListeners.remove(listener);
/*     */     }
/* 291 */     return false;
/*     */   }
/*     */ 
/*     */   public List<PacketPostListener> getPostListeners()
/*     */   {
/* 299 */     return this.postListeners != null ? Collections.unmodifiableList(this.postListeners) : Collections.emptyList();
/*     */   }
/*     */ 
/*     */   public List<ScheduledPacket> getScheduledPackets()
/*     */   {
/* 309 */     if (this.scheduledPackets == null)
/* 310 */       this.scheduledPackets = Lists.newArrayList();
/* 311 */     return this.scheduledPackets;
/*     */   }
/*     */ 
/*     */   private void checkServerSide()
/*     */   {
/* 318 */     if (this.side.isForClient())
/* 319 */       throw new IllegalStateException("Must be a server side packet.");
/*     */   }
/*     */ 
/*     */   protected ByteBuffer skipHeader(ByteBuffer buffer)
/*     */     throws IOException
/*     */   {
/* 330 */     skipHeader(new DataInputStream(new ByteBufferInputStream(buffer)));
/* 331 */     return buffer;
/*     */   }
/*     */ 
/*     */   protected abstract DataInputStream skipHeader(DataInputStream paramDataInputStream)
/*     */     throws IOException;
/*     */ 
/*     */   protected abstract ByteBuffer addHeader(ByteBuffer paramByteBuffer, PacketType paramPacketType);
/*     */ 
/*     */   protected abstract DataInputStream addHeader(DataInputStream paramDataInputStream, PacketType paramPacketType);
/*     */ 
/*     */   public static boolean hasOutputHandlers(NetworkMarker marker)
/*     */   {
/* 363 */     return (marker != null) && (!marker.getOutputHandlers().isEmpty());
/*     */   }
/*     */ 
/*     */   public static boolean hasPostListeners(NetworkMarker marker)
/*     */   {
/* 372 */     return (marker != null) && (!marker.getPostListeners().isEmpty());
/*     */   }
/*     */ 
/*     */   public static byte[] getByteBuffer(NetworkMarker marker)
/*     */   {
/* 381 */     if (marker != null) {
/* 382 */       ByteBuffer buffer = marker.getInputBuffer();
/*     */ 
/* 384 */       if (buffer != null) {
/* 385 */         byte[] data = new byte[buffer.remaining()];
/*     */ 
/* 387 */         buffer.get(data, 0, data.length);
/* 388 */         return data;
/*     */       }
/*     */     }
/* 391 */     return null;
/*     */   }
/*     */ 
/*     */   public static NetworkMarker getNetworkMarker(PacketEvent event)
/*     */   {
/* 402 */     return event.networkMarker;
/*     */   }
/*     */ 
/*     */   public static List<ScheduledPacket> readScheduledPackets(NetworkMarker marker)
/*     */   {
/* 413 */     return marker.scheduledPackets;
/*     */   }
/*     */ 
/*     */   public static class EmptyBufferMarker extends NetworkMarker
/*     */   {
/*     */     public EmptyBufferMarker(@Nonnull ConnectionSide side)
/*     */     {
/*  35 */       super((byte[])null, null);
/*     */     }
/*     */ 
/*     */     protected DataInputStream skipHeader(DataInputStream input) throws IOException
/*     */     {
/*  40 */       throw new IllegalStateException("Buffer is empty.");
/*     */     }
/*     */ 
/*     */     protected ByteBuffer addHeader(ByteBuffer buffer, PacketType type)
/*     */     {
/*  45 */       throw new IllegalStateException("Buffer is empty.");
/*     */     }
/*     */ 
/*     */     protected DataInputStream addHeader(DataInputStream input, PacketType type)
/*     */     {
/*  50 */       throw new IllegalStateException("Buffer is empty.");
/*     */     }
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.events.NetworkMarker
 * JD-Core Version:    0.6.2
 */