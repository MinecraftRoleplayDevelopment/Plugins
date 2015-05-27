/*     */ package com.comphenix.protocol.async;
/*     */ 
/*     */ import com.comphenix.protocol.PacketType;
/*     */ import com.comphenix.protocol.concurrency.ConcurrentPlayerMap;
/*     */ import com.comphenix.protocol.error.ErrorReporter;
/*     */ import com.comphenix.protocol.events.PacketEvent;
/*     */ import com.comphenix.protocol.injector.SortedPacketListenerList;
/*     */ import com.google.common.util.concurrent.ThreadFactoryBuilder;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.concurrent.ConcurrentMap;
/*     */ import java.util.concurrent.Executor;
/*     */ import java.util.concurrent.Executors;
/*     */ import java.util.concurrent.ThreadFactory;
/*     */ import org.bukkit.entity.Player;
/*     */ 
/*     */ class PlayerSendingHandler
/*     */ {
/*     */   private ErrorReporter reporter;
/*     */   private ConcurrentMap<Player, QueueContainer> playerSendingQueues;
/*     */   private SortedPacketListenerList serverTimeoutListeners;
/*     */   private SortedPacketListenerList clientTimeoutListeners;
/*     */   private Executor asynchronousSender;
/*     */   private volatile boolean cleaningUp;
/*     */ 
/*     */   public PlayerSendingHandler(ErrorReporter reporter, SortedPacketListenerList serverTimeoutListeners, SortedPacketListenerList clientTimeoutListeners)
/*     */   {
/* 104 */     this.reporter = reporter;
/* 105 */     this.serverTimeoutListeners = serverTimeoutListeners;
/* 106 */     this.clientTimeoutListeners = clientTimeoutListeners;
/*     */ 
/* 109 */     this.playerSendingQueues = ConcurrentPlayerMap.usingAddress();
/*     */   }
/*     */ 
/*     */   public synchronized void initializeScheduler()
/*     */   {
/* 116 */     if (this.asynchronousSender == null) {
/* 117 */       ThreadFactory factory = new ThreadFactoryBuilder().setDaemon(true).setNameFormat("ProtocolLib-AsyncSender %s").build();
/*     */ 
/* 121 */       this.asynchronousSender = Executors.newSingleThreadExecutor(factory);
/*     */     }
/*     */   }
/*     */ 
/*     */   public PacketSendingQueue getSendingQueue(PacketEvent packet)
/*     */   {
/* 131 */     return getSendingQueue(packet, true);
/*     */   }
/*     */ 
/*     */   public PacketSendingQueue getSendingQueue(PacketEvent packet, boolean createNew)
/*     */   {
/* 141 */     QueueContainer queues = (QueueContainer)this.playerSendingQueues.get(packet.getPlayer());
/*     */ 
/* 144 */     if ((queues == null) && (createNew)) {
/* 145 */       QueueContainer newContainer = new QueueContainer();
/*     */ 
/* 148 */       queues = (QueueContainer)this.playerSendingQueues.putIfAbsent(packet.getPlayer(), newContainer);
/*     */ 
/* 150 */       if (queues == null) {
/* 151 */         queues = newContainer;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 156 */     if (queues != null) {
/* 157 */       return packet.isServerPacket() ? queues.getServerQueue() : queues.getClientQueue();
/*     */     }
/* 159 */     return null;
/*     */   }
/*     */ 
/*     */   public void sendAllPackets()
/*     */   {
/* 166 */     if (!this.cleaningUp)
/* 167 */       for (QueueContainer queues : this.playerSendingQueues.values()) {
/* 168 */         queues.getClientQueue().cleanupAll();
/* 169 */         queues.getServerQueue().cleanupAll();
/*     */       }
/*     */   }
/*     */ 
/*     */   public void sendServerPackets(List<PacketType> types, boolean synchronusOK)
/*     */   {
/* 180 */     if (!this.cleaningUp)
/* 181 */       for (QueueContainer queue : this.playerSendingQueues.values())
/* 182 */         queue.getServerQueue().signalPacketUpdate(types, synchronusOK);
/*     */   }
/*     */ 
/*     */   public void sendClientPackets(List<PacketType> types, boolean synchronusOK)
/*     */   {
/* 193 */     if (!this.cleaningUp)
/* 194 */       for (QueueContainer queue : this.playerSendingQueues.values())
/* 195 */         queue.getClientQueue().signalPacketUpdate(types, synchronusOK);
/*     */   }
/*     */ 
/*     */   public void trySendServerPackets(boolean onMainThread)
/*     */   {
/* 205 */     for (QueueContainer queue : this.playerSendingQueues.values())
/* 206 */       queue.getServerQueue().trySendPackets(onMainThread);
/*     */   }
/*     */ 
/*     */   public void trySendClientPackets(boolean onMainThread)
/*     */   {
/* 215 */     for (QueueContainer queue : this.playerSendingQueues.values())
/* 216 */       queue.getClientQueue().trySendPackets(onMainThread);
/*     */   }
/*     */ 
/*     */   public List<PacketSendingQueue> getServerQueues()
/*     */   {
/* 225 */     List result = new ArrayList();
/*     */ 
/* 227 */     for (QueueContainer queue : this.playerSendingQueues.values())
/* 228 */       result.add(queue.getServerQueue());
/* 229 */     return result;
/*     */   }
/*     */ 
/*     */   public List<PacketSendingQueue> getClientQueues()
/*     */   {
/* 237 */     List result = new ArrayList();
/*     */ 
/* 239 */     for (QueueContainer queue : this.playerSendingQueues.values())
/* 240 */       result.add(queue.getClientQueue());
/* 241 */     return result;
/*     */   }
/*     */ 
/*     */   public void cleanupAll()
/*     */   {
/* 248 */     if (!this.cleaningUp) {
/* 249 */       this.cleaningUp = true;
/*     */ 
/* 251 */       sendAllPackets();
/* 252 */       this.playerSendingQueues.clear();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void removePlayer(Player player)
/*     */   {
/* 262 */     this.playerSendingQueues.remove(player);
/*     */   }
/*     */ 
/*     */   private class QueueContainer
/*     */   {
/*     */     private PacketSendingQueue serverQueue;
/*     */     private PacketSendingQueue clientQueue;
/*     */ 
/*     */     public QueueContainer()
/*     */     {
/*  66 */       this.serverQueue = new PacketSendingQueue(false, PlayerSendingHandler.this.asynchronousSender)
/*     */       {
/*     */         protected void onPacketTimeout(PacketEvent event) {
/*  69 */           if (!PlayerSendingHandler.this.cleaningUp)
/*  70 */             PlayerSendingHandler.this.serverTimeoutListeners.invokePacketSending(PlayerSendingHandler.this.reporter, event);
/*     */         }
/*     */       };
/*  76 */       this.clientQueue = new PacketSendingQueue(true, PlayerSendingHandler.this.asynchronousSender)
/*     */       {
/*     */         protected void onPacketTimeout(PacketEvent event) {
/*  79 */           if (!PlayerSendingHandler.this.cleaningUp)
/*  80 */             PlayerSendingHandler.this.clientTimeoutListeners.invokePacketSending(PlayerSendingHandler.this.reporter, event);
/*     */         }
/*     */       };
/*     */     }
/*     */ 
/*     */     public PacketSendingQueue getServerQueue()
/*     */     {
/*  87 */       return this.serverQueue;
/*     */     }
/*     */ 
/*     */     public PacketSendingQueue getClientQueue() {
/*  91 */       return this.clientQueue;
/*     */     }
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.async.PlayerSendingHandler
 * JD-Core Version:    0.6.2
 */