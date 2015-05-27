/*     */ package com.comphenix.protocol.async;
/*     */ 
/*     */ import com.comphenix.protocol.ProtocolLibrary;
/*     */ import com.comphenix.protocol.concurrency.AbstractConcurrentListenerMultimap;
/*     */ import com.comphenix.protocol.error.ErrorReporter;
/*     */ import com.comphenix.protocol.error.Report;
/*     */ import com.comphenix.protocol.error.Report.ReportBuilder;
/*     */ import com.comphenix.protocol.error.ReportType;
/*     */ import com.comphenix.protocol.events.PacketEvent;
/*     */ import com.comphenix.protocol.injector.PrioritizedListener;
/*     */ import com.google.common.collect.MinMaxPriorityQueue;
/*     */ import com.google.common.collect.MinMaxPriorityQueue.Builder;
/*     */ import java.util.Collection;
/*     */ import java.util.Iterator;
/*     */ import java.util.PriorityQueue;
/*     */ import java.util.Queue;
/*     */ import java.util.concurrent.Semaphore;
/*     */ 
/*     */ class PacketProcessingQueue extends AbstractConcurrentListenerMultimap<AsyncListenerHandler>
/*     */ {
/*  41 */   public static final ReportType REPORT_GUAVA_CORRUPT_MISSING = new ReportType("Guava is either missing or corrupt. Reverting to PriorityQueue.");
/*     */   public static final int INITIAL_CAPACITY = 64;
/*     */   public static final int DEFAULT_MAXIMUM_CONCURRENCY = 32;
/*     */   public static final int DEFAULT_QUEUE_LIMIT = 61440;
/*     */   private final int maximumConcurrency;
/*     */   private Semaphore concurrentProcessing;
/*     */   private Queue<PacketEventHolder> processingQueue;
/*     */   private PlayerSendingHandler sendingHandler;
/*     */ 
/*     */   public PacketProcessingQueue(PlayerSendingHandler sendingHandler)
/*     */   {
/*  70 */     this(sendingHandler, 64, 61440, 32);
/*     */   }
/*     */ 
/*     */   public PacketProcessingQueue(PlayerSendingHandler sendingHandler, int initialSize, int maximumSize, int maximumConcurrency)
/*     */   {
/*     */     try
/*     */     {
/*  77 */       this.processingQueue = Synchronization.queue(MinMaxPriorityQueue.expectedSize(initialSize).maximumSize(maximumSize).create(), null);
/*     */     }
/*     */     catch (IncompatibleClassChangeError e)
/*     */     {
/*  83 */       ProtocolLibrary.getErrorReporter().reportWarning(this, Report.newBuilder(REPORT_GUAVA_CORRUPT_MISSING).error(e));
/*     */ 
/*  87 */       this.processingQueue = Synchronization.queue(new PriorityQueue(), null);
/*     */     }
/*     */ 
/*  91 */     this.maximumConcurrency = maximumConcurrency;
/*  92 */     this.concurrentProcessing = new Semaphore(maximumConcurrency);
/*  93 */     this.sendingHandler = sendingHandler;
/*     */   }
/*     */ 
/*     */   public boolean enqueue(PacketEvent packet, boolean onMainThread)
/*     */   {
/*     */     try
/*     */     {
/* 104 */       this.processingQueue.add(new PacketEventHolder(packet));
/*     */ 
/* 107 */       signalBeginProcessing(onMainThread);
/* 108 */       return true; } catch (IllegalStateException e) {
/*     */     }
/* 110 */     return false;
/*     */   }
/*     */ 
/*     */   public int size()
/*     */   {
/* 119 */     return this.processingQueue.size();
/*     */   }
/*     */ 
/*     */   public void signalBeginProcessing(boolean onMainThread)
/*     */   {
/* 127 */     while (this.concurrentProcessing.tryAcquire()) {
/* 128 */       PacketEventHolder holder = (PacketEventHolder)this.processingQueue.poll();
/*     */ 
/* 131 */       if (holder != null) {
/* 132 */         PacketEvent packet = holder.getEvent();
/* 133 */         AsyncMarker marker = packet.getAsyncMarker();
/* 134 */         Collection list = getListener(packet.getPacketType());
/*     */ 
/* 136 */         marker.incrementProcessingDelay();
/*     */ 
/* 139 */         if (list != null) {
/* 140 */           Iterator iterator = list.iterator();
/*     */ 
/* 142 */           if (iterator.hasNext()) {
/* 143 */             marker.setListenerTraversal(iterator);
/* 144 */             ((AsyncListenerHandler)((PrioritizedListener)iterator.next()).getListener()).enqueuePacket(packet);
/*     */           }
/*     */ 
/*     */         }
/*     */         else
/*     */         {
/* 150 */           if (marker.decrementProcessingDelay() == 0) {
/* 151 */             PacketSendingQueue sendingQueue = this.sendingHandler.getSendingQueue(packet, false);
/*     */ 
/* 154 */             if (sendingQueue != null)
/* 155 */               sendingQueue.signalPacketUpdate(packet, onMainThread);
/*     */           }
/* 157 */           signalProcessingDone();
/*     */         }
/*     */       }
/*     */       else {
/* 161 */         signalProcessingDone();
/* 162 */         return;
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void signalProcessingDone()
/*     */   {
/* 171 */     this.concurrentProcessing.release();
/*     */   }
/*     */ 
/*     */   public int getMaximumConcurrency()
/*     */   {
/* 179 */     return this.maximumConcurrency;
/*     */   }
/*     */ 
/*     */   public void cleanupAll()
/*     */   {
/* 184 */     for (PrioritizedListener handler : values()) {
/* 185 */       if (handler != null) {
/* 186 */         ((AsyncListenerHandler)handler.getListener()).cancel();
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 191 */     clearListeners();
/*     */ 
/* 194 */     this.processingQueue.clear();
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.async.PacketProcessingQueue
 * JD-Core Version:    0.6.2
 */