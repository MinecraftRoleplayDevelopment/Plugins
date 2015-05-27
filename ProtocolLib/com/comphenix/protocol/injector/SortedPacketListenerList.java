/*     */ package com.comphenix.protocol.injector;
/*     */ 
/*     */ import com.comphenix.protocol.concurrency.AbstractConcurrentListenerMultimap;
/*     */ import com.comphenix.protocol.error.ErrorReporter;
/*     */ import com.comphenix.protocol.events.ListenerPriority;
/*     */ import com.comphenix.protocol.events.PacketContainer;
/*     */ import com.comphenix.protocol.events.PacketEvent;
/*     */ import com.comphenix.protocol.events.PacketListener;
/*     */ import com.comphenix.protocol.timing.TimedListenerManager;
/*     */ import com.comphenix.protocol.timing.TimedListenerManager.ListenerType;
/*     */ import com.comphenix.protocol.timing.TimedTracker;
/*     */ import java.util.Collection;
/*     */ 
/*     */ public final class SortedPacketListenerList extends AbstractConcurrentListenerMultimap<PacketListener>
/*     */ {
/*  38 */   private TimedListenerManager timedManager = TimedListenerManager.getInstance();
/*     */ 
/*     */   public void invokePacketRecieving(ErrorReporter reporter, PacketEvent event)
/*     */   {
/*  50 */     Collection list = getListener(event.getPacketType());
/*     */ 
/*  52 */     if (list == null) {
/*  53 */       return;
/*     */     }
/*     */ 
/*  56 */     if (this.timedManager.isTiming())
/*  57 */       for (PrioritizedListener element : list) {
/*  58 */         TimedTracker tracker = this.timedManager.getTracker((PacketListener)element.getListener(), TimedListenerManager.ListenerType.SYNC_CLIENT_SIDE);
/*  59 */         long token = tracker.beginTracking();
/*     */ 
/*  62 */         invokeReceivingListener(reporter, event, element);
/*  63 */         tracker.endTracking(token, event.getPacketType());
/*     */       }
/*     */     else
/*  66 */       for (PrioritizedListener element : list)
/*  67 */         invokeReceivingListener(reporter, event, element);
/*     */   }
/*     */ 
/*     */   public void invokePacketRecieving(ErrorReporter reporter, PacketEvent event, ListenerPriority priorityFilter)
/*     */   {
/*  79 */     Collection list = getListener(event.getPacketType());
/*     */ 
/*  81 */     if (list == null) {
/*  82 */       return;
/*     */     }
/*     */ 
/*  85 */     if (this.timedManager.isTiming()) {
/*  86 */       for (PrioritizedListener element : list)
/*  87 */         if (element.getPriority() == priorityFilter) {
/*  88 */           TimedTracker tracker = this.timedManager.getTracker((PacketListener)element.getListener(), TimedListenerManager.ListenerType.SYNC_CLIENT_SIDE);
/*  89 */           long token = tracker.beginTracking();
/*     */ 
/*  92 */           invokeReceivingListener(reporter, event, element);
/*  93 */           tracker.endTracking(token, event.getPacketType());
/*     */         }
/*     */     }
/*     */     else
/*  97 */       for (PrioritizedListener element : list)
/*  98 */         if (element.getPriority() == priorityFilter)
/*  99 */           invokeReceivingListener(reporter, event, element);
/*     */   }
/*     */ 
/*     */   private final void invokeReceivingListener(ErrorReporter reporter, PacketEvent event, PrioritizedListener<PacketListener> element)
/*     */   {
/*     */     try
/*     */     {
/* 113 */       event.setReadOnly(element.getPriority() == ListenerPriority.MONITOR);
/* 114 */       ((PacketListener)element.getListener()).onPacketReceiving(event);
/*     */     }
/*     */     catch (OutOfMemoryError e) {
/* 117 */       throw e;
/*     */     } catch (ThreadDeath e) {
/* 119 */       throw e;
/*     */     }
/*     */     catch (Throwable e) {
/* 122 */       reporter.reportMinimal(((PacketListener)element.getListener()).getPlugin(), "onPacketReceiving(PacketEvent)", e, new Object[] { event.getPacket().getHandle() });
/*     */     }
/*     */   }
/*     */ 
/*     */   public void invokePacketSending(ErrorReporter reporter, PacketEvent event)
/*     */   {
/* 133 */     Collection list = getListener(event.getPacketType());
/*     */ 
/* 135 */     if (list == null) {
/* 136 */       return;
/*     */     }
/* 138 */     if (this.timedManager.isTiming())
/* 139 */       for (PrioritizedListener element : list) {
/* 140 */         TimedTracker tracker = this.timedManager.getTracker((PacketListener)element.getListener(), TimedListenerManager.ListenerType.SYNC_SERVER_SIDE);
/* 141 */         long token = tracker.beginTracking();
/*     */ 
/* 144 */         invokeSendingListener(reporter, event, element);
/* 145 */         tracker.endTracking(token, event.getPacketType());
/*     */       }
/*     */     else
/* 148 */       for (PrioritizedListener element : list)
/* 149 */         invokeSendingListener(reporter, event, element);
/*     */   }
/*     */ 
/*     */   public void invokePacketSending(ErrorReporter reporter, PacketEvent event, ListenerPriority priorityFilter)
/*     */   {
/* 161 */     Collection list = getListener(event.getPacketType());
/*     */ 
/* 163 */     if (list == null) {
/* 164 */       return;
/*     */     }
/* 166 */     if (this.timedManager.isTiming()) {
/* 167 */       for (PrioritizedListener element : list)
/* 168 */         if (element.getPriority() == priorityFilter) {
/* 169 */           TimedTracker tracker = this.timedManager.getTracker((PacketListener)element.getListener(), TimedListenerManager.ListenerType.SYNC_SERVER_SIDE);
/* 170 */           long token = tracker.beginTracking();
/*     */ 
/* 173 */           invokeSendingListener(reporter, event, element);
/* 174 */           tracker.endTracking(token, event.getPacketType());
/*     */         }
/*     */     }
/*     */     else
/* 178 */       for (PrioritizedListener element : list)
/* 179 */         if (element.getPriority() == priorityFilter)
/* 180 */           invokeSendingListener(reporter, event, element);
/*     */   }
/*     */ 
/*     */   private final void invokeSendingListener(ErrorReporter reporter, PacketEvent event, PrioritizedListener<PacketListener> element)
/*     */   {
/*     */     try
/*     */     {
/* 194 */       event.setReadOnly(element.getPriority() == ListenerPriority.MONITOR);
/* 195 */       ((PacketListener)element.getListener()).onPacketSending(event);
/*     */     }
/*     */     catch (OutOfMemoryError e) {
/* 198 */       throw e;
/*     */     } catch (ThreadDeath e) {
/* 200 */       throw e;
/*     */     }
/*     */     catch (Throwable e) {
/* 203 */       reporter.reportMinimal(((PacketListener)element.getListener()).getPlugin(), "onPacketSending(PacketEvent)", e, new Object[] { event.getPacket().getHandle() });
/*     */     }
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.injector.SortedPacketListenerList
 * JD-Core Version:    0.6.2
 */