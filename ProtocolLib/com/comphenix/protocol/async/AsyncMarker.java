/*     */ package com.comphenix.protocol.async;
/*     */ 
/*     */ import com.comphenix.protocol.PacketStream;
/*     */ import com.comphenix.protocol.PacketType.Play.Client;
/*     */ import com.comphenix.protocol.ProtocolLibrary;
/*     */ import com.comphenix.protocol.events.NetworkMarker;
/*     */ import com.comphenix.protocol.events.PacketContainer;
/*     */ import com.comphenix.protocol.events.PacketEvent;
/*     */ import com.comphenix.protocol.injector.PrioritizedListener;
/*     */ import com.comphenix.protocol.reflect.FieldAccessException;
/*     */ import com.comphenix.protocol.reflect.FuzzyReflection;
/*     */ import com.comphenix.protocol.reflect.StructureModifier;
/*     */ import com.comphenix.protocol.utility.MinecraftReflection;
/*     */ import com.comphenix.protocol.utility.MinecraftVersion;
/*     */ import com.google.common.primitives.Longs;
/*     */ import java.io.IOException;
/*     */ import java.io.Serializable;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.lang.reflect.Method;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.concurrent.atomic.AtomicInteger;
/*     */ import java.util.logging.Level;
/*     */ 
/*     */ public class AsyncMarker
/*     */   implements Serializable, Comparable<AsyncMarker>
/*     */ {
/*     */   private static final long serialVersionUID = -2621498096616187384L;
/*     */   public static final int DEFAULT_TIMEOUT_DELTA = 1800000;
/*     */   public static final int DEFAULT_SENDING_DELTA = 0;
/*     */   private transient PacketStream packetStream;
/*     */   private transient Iterator<PrioritizedListener<AsyncListenerHandler>> listenerTraversal;
/*     */   private long initialTime;
/*     */   private long timeout;
/*     */   private long originalSendingIndex;
/*     */   private long newSendingIndex;
/*     */   private Long queuedSendingIndex;
/*     */   private volatile boolean processed;
/*     */   private volatile boolean transmitted;
/*     */   private volatile boolean asyncCancelled;
/*  96 */   private AtomicInteger processingDelay = new AtomicInteger();
/*     */ 
/*  99 */   private Object processingLock = new Object();
/*     */   private transient AsyncListenerHandler listenerHandler;
/*     */   private transient int workerID;
/*     */   private static volatile Method isMinecraftAsync;
/*     */   private static volatile boolean alwaysSync;
/*     */ 
/*     */   AsyncMarker(PacketStream packetStream, long sendingIndex, long initialTime, long timeoutDelta)
/*     */   {
/* 114 */     if (packetStream == null) {
/* 115 */       throw new IllegalArgumentException("packetStream cannot be NULL");
/*     */     }
/* 117 */     this.packetStream = packetStream;
/*     */ 
/* 120 */     this.initialTime = initialTime;
/* 121 */     this.timeout = (initialTime + timeoutDelta);
/*     */ 
/* 124 */     this.originalSendingIndex = sendingIndex;
/* 125 */     this.newSendingIndex = sendingIndex;
/*     */   }
/*     */ 
/*     */   public long getInitialTime()
/*     */   {
/* 133 */     return this.initialTime;
/*     */   }
/*     */ 
/*     */   public long getTimeout()
/*     */   {
/* 141 */     return this.timeout;
/*     */   }
/*     */ 
/*     */   public void setTimeout(long timeout)
/*     */   {
/* 149 */     this.timeout = timeout;
/*     */   }
/*     */ 
/*     */   public long getOriginalSendingIndex()
/*     */   {
/* 157 */     return this.originalSendingIndex;
/*     */   }
/*     */ 
/*     */   public long getNewSendingIndex()
/*     */   {
/* 167 */     return this.newSendingIndex;
/*     */   }
/*     */ 
/*     */   public void setNewSendingIndex(long newSendingIndex)
/*     */   {
/* 177 */     this.newSendingIndex = newSendingIndex;
/*     */   }
/*     */ 
/*     */   public PacketStream getPacketStream()
/*     */   {
/* 185 */     return this.packetStream;
/*     */   }
/*     */ 
/*     */   public void setPacketStream(PacketStream packetStream)
/*     */   {
/* 193 */     this.packetStream = packetStream;
/*     */   }
/*     */ 
/*     */   public boolean isProcessed()
/*     */   {
/* 201 */     return this.processed;
/*     */   }
/*     */ 
/*     */   void setProcessed(boolean processed)
/*     */   {
/* 209 */     this.processed = processed;
/*     */   }
/*     */ 
/*     */   public int incrementProcessingDelay()
/*     */   {
/* 226 */     return this.processingDelay.incrementAndGet();
/*     */   }
/*     */ 
/*     */   int decrementProcessingDelay()
/*     */   {
/* 234 */     return this.processingDelay.decrementAndGet();
/*     */   }
/*     */ 
/*     */   public int getProcessingDelay()
/*     */   {
/* 242 */     return this.processingDelay.get();
/*     */   }
/*     */ 
/*     */   public boolean isQueued()
/*     */   {
/* 250 */     return this.queuedSendingIndex != null;
/*     */   }
/*     */ 
/*     */   public long getQueuedSendingIndex()
/*     */   {
/* 258 */     return this.queuedSendingIndex != null ? this.queuedSendingIndex.longValue() : 0L;
/*     */   }
/*     */ 
/*     */   void setQueuedSendingIndex(Long queuedSendingIndex)
/*     */   {
/* 266 */     this.queuedSendingIndex = queuedSendingIndex;
/*     */   }
/*     */ 
/*     */   public Object getProcessingLock()
/*     */   {
/* 277 */     return this.processingLock;
/*     */   }
/*     */ 
/*     */   public void setProcessingLock(Object processingLock) {
/* 281 */     this.processingLock = processingLock;
/*     */   }
/*     */ 
/*     */   public boolean isTransmitted()
/*     */   {
/* 289 */     return this.transmitted;
/*     */   }
/*     */ 
/*     */   public boolean hasExpired()
/*     */   {
/* 297 */     return hasExpired(System.currentTimeMillis());
/*     */   }
/*     */ 
/*     */   public boolean hasExpired(long currentTime)
/*     */   {
/* 306 */     return this.timeout < currentTime;
/*     */   }
/*     */ 
/*     */   public boolean isAsyncCancelled()
/*     */   {
/* 314 */     return this.asyncCancelled;
/*     */   }
/*     */ 
/*     */   public void setAsyncCancelled(boolean asyncCancelled)
/*     */   {
/* 326 */     this.asyncCancelled = asyncCancelled;
/*     */   }
/*     */ 
/*     */   public AsyncListenerHandler getListenerHandler()
/*     */   {
/* 334 */     return this.listenerHandler;
/*     */   }
/*     */ 
/*     */   void setListenerHandler(AsyncListenerHandler listenerHandler)
/*     */   {
/* 344 */     this.listenerHandler = listenerHandler;
/*     */   }
/*     */ 
/*     */   public int getWorkerID()
/*     */   {
/* 352 */     return this.workerID;
/*     */   }
/*     */ 
/*     */   void setWorkerID(int workerID)
/*     */   {
/* 362 */     this.workerID = workerID;
/*     */   }
/*     */ 
/*     */   Iterator<PrioritizedListener<AsyncListenerHandler>> getListenerTraversal()
/*     */   {
/* 370 */     return this.listenerTraversal;
/*     */   }
/*     */ 
/*     */   void setListenerTraversal(Iterator<PrioritizedListener<AsyncListenerHandler>> listenerTraversal)
/*     */   {
/* 378 */     this.listenerTraversal = listenerTraversal;
/*     */   }
/*     */ 
/*     */   void sendPacket(PacketEvent event)
/*     */     throws IOException
/*     */   {
/*     */     try
/*     */     {
/* 388 */       if (event.isServerPacket())
/* 389 */         this.packetStream.sendServerPacket(event.getPlayer(), event.getPacket(), NetworkMarker.getNetworkMarker(event), false);
/*     */       else {
/* 391 */         this.packetStream.recieveClientPacket(event.getPlayer(), event.getPacket(), NetworkMarker.getNetworkMarker(event), false);
/*     */       }
/* 393 */       this.transmitted = true;
/*     */     }
/*     */     catch (InvocationTargetException e) {
/* 396 */       throw new IOException("Cannot send packet", e);
/*     */     } catch (IllegalAccessException e) {
/* 398 */       throw new IOException("Cannot send packet", e);
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean isMinecraftAsync(PacketEvent event)
/*     */     throws FieldAccessException
/*     */   {
/* 407 */     if ((isMinecraftAsync == null) && (!alwaysSync)) {
/*     */       try {
/* 409 */         isMinecraftAsync = FuzzyReflection.fromClass(MinecraftReflection.getPacketClass()).getMethodByName("a_.*");
/*     */       }
/*     */       catch (RuntimeException e) {
/* 412 */         List methods = FuzzyReflection.fromClass(MinecraftReflection.getPacketClass()).getMethodListByParameters(Boolean.TYPE, new Class[0]);
/*     */ 
/* 416 */         if (methods.size() == 2) {
/* 417 */           isMinecraftAsync = (Method)methods.get(1);
/* 418 */         } else if (methods.size() == 1)
/*     */         {
/* 420 */           alwaysSync = true;
/* 421 */         } else if (MinecraftVersion.getCurrentVersion().isAtLeast(MinecraftVersion.BOUNTIFUL_UPDATE))
/*     */         {
/* 424 */           if (event.getPacketType() == PacketType.Play.Client.CHAT) {
/* 425 */             String content = (String)event.getPacket().getStrings().readSafely(0);
/* 426 */             if (content != null)
/*     */             {
/* 428 */               return !content.startsWith("/");
/*     */             }
/* 430 */             ProtocolLibrary.log(Level.WARNING, "Failed to determine contents of incoming chat packet!", new Object[0]);
/* 431 */             alwaysSync = true;
/*     */           }
/*     */           else
/*     */           {
/* 435 */             return false;
/*     */           }
/*     */         } else {
/* 438 */           ProtocolLibrary.log(Level.INFO, "Could not determine asynchronous state of packets.", new Object[0]);
/* 439 */           ProtocolLibrary.log(Level.INFO, "This can probably be ignored.", new Object[0]);
/* 440 */           alwaysSync = true;
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 445 */     if (alwaysSync) {
/* 446 */       return false;
/*     */     }
/*     */     try
/*     */     {
/* 450 */       return ((Boolean)isMinecraftAsync.invoke(event.getPacket().getHandle(), new Object[0])).booleanValue();
/*     */     } catch (IllegalArgumentException e) {
/* 452 */       throw new FieldAccessException("Illegal argument", e);
/*     */     } catch (IllegalAccessException e) {
/* 454 */       throw new FieldAccessException("Unable to reflect method call 'a_', or: isAsyncPacket.", e);
/*     */     } catch (InvocationTargetException e) {
/* 456 */       throw new FieldAccessException("Minecraft error", e);
/*     */     }
/*     */   }
/*     */ 
/*     */   public int compareTo(AsyncMarker o)
/*     */   {
/* 463 */     if (o == null) {
/* 464 */       return 1;
/*     */     }
/* 466 */     return Longs.compare(getNewSendingIndex(), o.getNewSendingIndex());
/*     */   }
/*     */ 
/*     */   public boolean equals(Object other)
/*     */   {
/* 472 */     if (other == this)
/* 473 */       return true;
/* 474 */     if ((other instanceof AsyncMarker)) {
/* 475 */       return getNewSendingIndex() == ((AsyncMarker)other).getNewSendingIndex();
/*     */     }
/* 477 */     return false;
/*     */   }
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 482 */     return Longs.hashCode(getNewSendingIndex());
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.async.AsyncMarker
 * JD-Core Version:    0.6.2
 */