/*     */ package com.comphenix.protocol.async;
/*     */ 
/*     */ import com.comphenix.protocol.PacketType;
/*     */ import com.comphenix.protocol.ProtocolLibrary;
/*     */ import com.comphenix.protocol.error.ErrorReporter;
/*     */ import com.comphenix.protocol.error.Report;
/*     */ import com.comphenix.protocol.error.Report.ReportBuilder;
/*     */ import com.comphenix.protocol.error.ReportType;
/*     */ import com.comphenix.protocol.events.PacketEvent;
/*     */ import com.comphenix.protocol.injector.PlayerLoggedOutException;
/*     */ import com.comphenix.protocol.reflect.FieldAccessException;
/*     */ import java.io.IOException;
/*     */ import java.util.HashSet;
/*     */ import java.util.List;
/*     */ import java.util.Set;
/*     */ import java.util.concurrent.Executor;
/*     */ import java.util.concurrent.PriorityBlockingQueue;
/*     */ import org.bukkit.entity.Player;
/*     */ 
/*     */ abstract class PacketSendingQueue
/*     */ {
/*  42 */   public static final ReportType REPORT_DROPPED_PACKET = new ReportType("Warning: Dropped packet index %s of type %s.");
/*     */   public static final int INITIAL_CAPACITY = 10;
/*     */   private PriorityBlockingQueue<PacketEventHolder> sendingQueue;
/*     */   private Executor asynchronousSender;
/*     */   private final boolean notThreadSafe;
/*  53 */   private boolean cleanedUp = false;
/*     */ 
/*     */   public PacketSendingQueue(boolean notThreadSafe, Executor asynchronousSender)
/*     */   {
/*  60 */     this.sendingQueue = new PriorityBlockingQueue(10);
/*  61 */     this.notThreadSafe = notThreadSafe;
/*  62 */     this.asynchronousSender = asynchronousSender;
/*     */   }
/*     */ 
/*     */   public int size()
/*     */   {
/*  70 */     return this.sendingQueue.size();
/*     */   }
/*     */ 
/*     */   public void enqueue(PacketEvent packet)
/*     */   {
/*  78 */     this.sendingQueue.add(new PacketEventHolder(packet));
/*     */   }
/*     */ 
/*     */   public synchronized void signalPacketUpdate(PacketEvent packetUpdated, boolean onMainThread)
/*     */   {
/*  88 */     AsyncMarker marker = packetUpdated.getAsyncMarker();
/*     */ 
/*  91 */     if ((marker.getQueuedSendingIndex() != marker.getNewSendingIndex()) && (!marker.hasExpired())) {
/*  92 */       PacketEvent copy = PacketEvent.fromSynchronous(packetUpdated, marker);
/*     */ 
/*  95 */       packetUpdated.setReadOnly(false);
/*  96 */       packetUpdated.setCancelled(true);
/*     */ 
/*  99 */       enqueue(copy);
/*     */     }
/*     */ 
/* 103 */     marker.setProcessed(true);
/* 104 */     trySendPackets(onMainThread);
/*     */   }
/*     */ 
/*     */   public synchronized void signalPacketUpdate(List<PacketType> packetsRemoved, boolean onMainThread)
/*     */   {
/* 113 */     Set lookup = new HashSet(packetsRemoved);
/*     */ 
/* 116 */     for (PacketEventHolder holder : this.sendingQueue) {
/* 117 */       PacketEvent event = holder.getEvent();
/*     */ 
/* 119 */       if (lookup.contains(event.getPacketType())) {
/* 120 */         event.getAsyncMarker().setProcessed(true);
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 125 */     trySendPackets(onMainThread);
/*     */   }
/*     */ 
/*     */   public void trySendPackets(boolean onMainThread)
/*     */   {
/* 134 */     boolean sending = true;
/*     */ 
/* 137 */     while (sending) {
/* 138 */       PacketEventHolder holder = (PacketEventHolder)this.sendingQueue.poll();
/*     */ 
/* 140 */       if (holder != null) {
/* 141 */         sending = processPacketHolder(onMainThread, holder);
/*     */ 
/* 143 */         if (!sending)
/*     */         {
/* 145 */           this.sendingQueue.add(holder);
/*     */         }
/*     */       }
/*     */       else
/*     */       {
/* 150 */         sending = false;
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private boolean processPacketHolder(boolean onMainThread, final PacketEventHolder holder)
/*     */   {
/* 162 */     PacketEvent current = holder.getEvent();
/* 163 */     AsyncMarker marker = current.getAsyncMarker();
/* 164 */     boolean hasExpired = marker.hasExpired();
/*     */ 
/* 167 */     if (this.cleanedUp) {
/* 168 */       return true;
/*     */     }
/*     */ 
/* 172 */     if ((marker.isProcessed()) || (hasExpired)) {
/* 173 */       if (hasExpired)
/*     */       {
/* 175 */         onPacketTimeout(current);
/*     */ 
/* 178 */         marker = current.getAsyncMarker();
/* 179 */         hasExpired = marker.hasExpired();
/*     */ 
/* 182 */         if ((!marker.isProcessed()) && (!hasExpired)) {
/* 183 */           return false;
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 188 */       if ((!current.isCancelled()) && (!hasExpired))
/*     */       {
/* 190 */         if (this.notThreadSafe) {
/*     */           try {
/* 192 */             boolean wantAsync = marker.isMinecraftAsync(current);
/* 193 */             boolean wantSync = !wantAsync;
/*     */ 
/* 196 */             if ((!onMainThread) && (wantSync)) {
/* 197 */               return false;
/*     */             }
/*     */ 
/* 201 */             if ((onMainThread) && (wantAsync)) {
/* 202 */               this.asynchronousSender.execute(new Runnable()
/*     */               {
/*     */                 public void run()
/*     */                 {
/* 206 */                   PacketSendingQueue.this.processPacketHolder(false, holder);
/*     */                 }
/*     */               });
/* 211 */               return true;
/*     */             }
/*     */           }
/*     */           catch (FieldAccessException e) {
/* 215 */             e.printStackTrace();
/*     */ 
/* 218 */             return true;
/*     */           }
/*     */ 
/*     */         }
/*     */ 
/* 223 */         if (isOnline(current.getPlayer())) {
/* 224 */           sendPacket(current);
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 229 */       return true;
/*     */     }
/*     */ 
/* 233 */     return false;
/*     */   }
/*     */ 
/*     */   protected abstract void onPacketTimeout(PacketEvent paramPacketEvent);
/*     */ 
/*     */   private boolean isOnline(Player player)
/*     */   {
/* 243 */     return (player != null) && (player.isOnline());
/*     */   }
/*     */ 
/*     */   private void forceSend()
/*     */   {
/*     */     while (true)
/*     */     {
/* 251 */       PacketEventHolder holder = (PacketEventHolder)this.sendingQueue.poll();
/*     */ 
/* 253 */       if (holder == null) break;
/* 254 */       sendPacket(holder.getEvent());
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean isSynchronizeMain()
/*     */   {
/* 266 */     return this.notThreadSafe;
/*     */   }
/*     */ 
/*     */   private void sendPacket(PacketEvent event)
/*     */   {
/* 275 */     AsyncMarker marker = event.getAsyncMarker();
/*     */     try
/*     */     {
/* 279 */       if ((marker != null) && (!marker.isTransmitted()))
/* 280 */         marker.sendPacket(event);
/*     */     }
/*     */     catch (PlayerLoggedOutException e)
/*     */     {
/* 284 */       ProtocolLibrary.getErrorReporter().reportDebug(this, Report.newBuilder(REPORT_DROPPED_PACKET).messageParam(new Object[] { Long.valueOf(marker.getOriginalSendingIndex()), event.getPacketType() }).callerParam(new Object[] { event }));
/*     */     }
/*     */     catch (IOException e)
/*     */     {
/* 291 */       e.printStackTrace();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void cleanupAll()
/*     */   {
/* 299 */     if (!this.cleanedUp)
/*     */     {
/* 301 */       forceSend();
/*     */ 
/* 304 */       this.cleanedUp = true;
/*     */     }
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.async.PacketSendingQueue
 * JD-Core Version:    0.6.2
 */