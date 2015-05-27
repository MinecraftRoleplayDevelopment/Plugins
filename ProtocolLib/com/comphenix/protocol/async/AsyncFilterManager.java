/*     */ package com.comphenix.protocol.async;
/*     */ 
/*     */ import com.comphenix.protocol.AsynchronousManager;
/*     */ import com.comphenix.protocol.PacketStream;
/*     */ import com.comphenix.protocol.PacketType;
/*     */ import com.comphenix.protocol.ProtocolManager;
/*     */ import com.comphenix.protocol.error.ErrorReporter;
/*     */ import com.comphenix.protocol.events.ListeningWhitelist;
/*     */ import com.comphenix.protocol.events.PacketEvent;
/*     */ import com.comphenix.protocol.events.PacketListener;
/*     */ import com.comphenix.protocol.injector.PacketFilterManager;
/*     */ import com.comphenix.protocol.injector.PrioritizedListener;
/*     */ import com.comphenix.protocol.injector.SortedPacketListenerList;
/*     */ import com.comphenix.protocol.injector.packet.PacketRegistry;
/*     */ import com.google.common.base.Objects;
/*     */ import com.google.common.collect.ImmutableSet;
/*     */ import com.google.common.collect.ImmutableSet.Builder;
/*     */ import com.google.common.collect.Iterables;
/*     */ import com.google.common.collect.Sets;
/*     */ import java.util.Collection;
/*     */ import java.util.List;
/*     */ import java.util.Set;
/*     */ import java.util.concurrent.ConcurrentHashMap;
/*     */ import java.util.concurrent.atomic.AtomicInteger;
/*     */ import org.bukkit.entity.Player;
/*     */ import org.bukkit.plugin.Plugin;
/*     */ import org.bukkit.scheduler.BukkitScheduler;
/*     */ 
/*     */ public class AsyncFilterManager
/*     */   implements AsynchronousManager
/*     */ {
/*     */   private SortedPacketListenerList serverTimeoutListeners;
/*     */   private SortedPacketListenerList clientTimeoutListeners;
/*     */   private Set<PacketListener> timeoutListeners;
/*     */   private PacketProcessingQueue serverProcessingQueue;
/*     */   private PacketProcessingQueue clientProcessingQueue;
/*     */   private final PlayerSendingHandler playerSendingHandler;
/*     */   private final ErrorReporter reporter;
/*     */   private final Thread mainThread;
/*     */   private final BukkitScheduler scheduler;
/*  76 */   private final AtomicInteger currentSendingIndex = new AtomicInteger();
/*     */   private ProtocolManager manager;
/*     */ 
/*     */   public AsyncFilterManager(ErrorReporter reporter, BukkitScheduler scheduler)
/*     */   {
/*  90 */     this.serverTimeoutListeners = new SortedPacketListenerList();
/*  91 */     this.clientTimeoutListeners = new SortedPacketListenerList();
/*  92 */     this.timeoutListeners = Sets.newSetFromMap(new ConcurrentHashMap());
/*     */ 
/*  94 */     this.playerSendingHandler = new PlayerSendingHandler(reporter, this.serverTimeoutListeners, this.clientTimeoutListeners);
/*  95 */     this.serverProcessingQueue = new PacketProcessingQueue(this.playerSendingHandler);
/*  96 */     this.clientProcessingQueue = new PacketProcessingQueue(this.playerSendingHandler);
/*  97 */     this.playerSendingHandler.initializeScheduler();
/*     */ 
/*  99 */     this.scheduler = scheduler;
/* 100 */     this.reporter = reporter;
/* 101 */     this.mainThread = Thread.currentThread();
/*     */   }
/*     */ 
/*     */   public ProtocolManager getManager()
/*     */   {
/* 109 */     return this.manager;
/*     */   }
/*     */ 
/*     */   public void setManager(ProtocolManager manager)
/*     */   {
/* 117 */     this.manager = manager;
/*     */   }
/*     */ 
/*     */   public AsyncListenerHandler registerAsyncHandler(PacketListener listener)
/*     */   {
/* 122 */     return registerAsyncHandler(listener, true);
/*     */   }
/*     */ 
/*     */   public void registerTimeoutHandler(PacketListener listener)
/*     */   {
/* 127 */     if (listener == null)
/* 128 */       throw new IllegalArgumentException("listener cannot be NULL.");
/* 129 */     if (!this.timeoutListeners.add(listener)) {
/* 130 */       return;
/*     */     }
/* 132 */     ListeningWhitelist sending = listener.getSendingWhitelist();
/* 133 */     ListeningWhitelist receiving = listener.getReceivingWhitelist();
/*     */ 
/* 135 */     if (!ListeningWhitelist.isEmpty(sending))
/* 136 */       this.serverTimeoutListeners.addListener(listener, sending);
/* 137 */     if (!ListeningWhitelist.isEmpty(receiving))
/* 138 */       this.serverTimeoutListeners.addListener(listener, receiving);
/*     */   }
/*     */ 
/*     */   public Set<PacketListener> getTimeoutHandlers()
/*     */   {
/* 143 */     return ImmutableSet.copyOf(this.timeoutListeners);
/*     */   }
/*     */ 
/*     */   public Set<PacketListener> getAsyncHandlers()
/*     */   {
/* 148 */     ImmutableSet.Builder builder = ImmutableSet.builder();
/*     */ 
/* 152 */     for (PrioritizedListener handler : Iterables.concat(this.serverProcessingQueue.values(), this.clientProcessingQueue.values())) {
/* 153 */       builder.add(((AsyncListenerHandler)handler.getListener()).getAsyncListener());
/*     */     }
/* 155 */     return builder.build();
/*     */   }
/*     */ 
/*     */   public AsyncListenerHandler registerAsyncHandler(PacketListener listener, boolean autoInject)
/*     */   {
/* 173 */     AsyncListenerHandler handler = new AsyncListenerHandler(this.mainThread, this, listener);
/*     */ 
/* 175 */     ListeningWhitelist sendingWhitelist = listener.getSendingWhitelist();
/* 176 */     ListeningWhitelist receivingWhitelist = listener.getReceivingWhitelist();
/*     */ 
/* 178 */     if ((!hasValidWhitelist(sendingWhitelist)) && (!hasValidWhitelist(receivingWhitelist))) {
/* 179 */       throw new IllegalArgumentException("Listener has an empty sending and receiving whitelist.");
/*     */     }
/*     */ 
/* 183 */     if (hasValidWhitelist(sendingWhitelist)) {
/* 184 */       PacketFilterManager.verifyWhitelist(listener, sendingWhitelist);
/* 185 */       this.serverProcessingQueue.addListener(handler, sendingWhitelist);
/*     */     }
/* 187 */     if (hasValidWhitelist(receivingWhitelist)) {
/* 188 */       PacketFilterManager.verifyWhitelist(listener, receivingWhitelist);
/* 189 */       this.clientProcessingQueue.addListener(handler, receivingWhitelist);
/*     */     }
/*     */ 
/* 193 */     if (autoInject) {
/* 194 */       handler.setNullPacketListener(new NullPacketListener(listener));
/* 195 */       this.manager.addPacketListener(handler.getNullPacketListener());
/*     */     }
/* 197 */     return handler;
/*     */   }
/*     */ 
/*     */   private boolean hasValidWhitelist(ListeningWhitelist whitelist) {
/* 201 */     return (whitelist != null) && (whitelist.getTypes().size() > 0);
/*     */   }
/*     */ 
/*     */   public void unregisterTimeoutHandler(PacketListener listener)
/*     */   {
/* 206 */     if (listener == null) {
/* 207 */       throw new IllegalArgumentException("listener cannot be NULL.");
/*     */     }
/* 209 */     ListeningWhitelist sending = listener.getSendingWhitelist();
/* 210 */     ListeningWhitelist receiving = listener.getReceivingWhitelist();
/*     */ 
/* 213 */     if ((this.serverTimeoutListeners.removeListener(listener, sending).size() > 0) || (this.clientTimeoutListeners.removeListener(listener, receiving).size() > 0))
/*     */     {
/* 215 */       this.timeoutListeners.remove(listener);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void unregisterAsyncHandler(PacketListener listener)
/*     */   {
/* 221 */     if (listener == null) {
/* 222 */       throw new IllegalArgumentException("listener cannot be NULL.");
/*     */     }
/* 224 */     AsyncListenerHandler handler = findHandler(this.serverProcessingQueue, listener.getSendingWhitelist(), listener);
/*     */ 
/* 227 */     if (handler == null) {
/* 228 */       handler = findHandler(this.clientProcessingQueue, listener.getReceivingWhitelist(), listener);
/*     */     }
/* 230 */     unregisterAsyncHandler(handler);
/*     */   }
/*     */ 
/*     */   private AsyncListenerHandler findHandler(PacketProcessingQueue queue, ListeningWhitelist search, PacketListener target)
/*     */   {
/* 235 */     if (ListeningWhitelist.isEmpty(search)) {
/* 236 */       return null;
/*     */     }
/* 238 */     for (PacketType type : search.getTypes()) {
/* 239 */       for (PrioritizedListener element : queue.getListener(type)) {
/* 240 */         if (((AsyncListenerHandler)element.getListener()).getAsyncListener() == target) {
/* 241 */           return (AsyncListenerHandler)element.getListener();
/*     */         }
/*     */       }
/*     */     }
/* 245 */     return null;
/*     */   }
/*     */ 
/*     */   public void unregisterAsyncHandler(AsyncListenerHandler handler)
/*     */   {
/* 250 */     if (handler == null) {
/* 251 */       throw new IllegalArgumentException("listenerToken cannot be NULL");
/*     */     }
/* 253 */     handler.cancel();
/*     */   }
/*     */ 
/*     */   void unregisterAsyncHandlerInternal(AsyncListenerHandler handler)
/*     */   {
/* 259 */     PacketListener listener = handler.getAsyncListener();
/* 260 */     boolean synchronusOK = onMainThread();
/*     */ 
/* 263 */     if (handler.getNullPacketListener() != null) {
/* 264 */       this.manager.removePacketListener(handler.getNullPacketListener());
/*     */     }
/*     */ 
/* 268 */     if (hasValidWhitelist(listener.getSendingWhitelist())) {
/* 269 */       List removed = this.serverProcessingQueue.removeListener(handler, listener.getSendingWhitelist());
/*     */ 
/* 272 */       this.playerSendingHandler.sendServerPackets(removed, synchronusOK);
/*     */     }
/*     */ 
/* 275 */     if (hasValidWhitelist(listener.getReceivingWhitelist())) {
/* 276 */       List removed = this.clientProcessingQueue.removeListener(handler, listener.getReceivingWhitelist());
/* 277 */       this.playerSendingHandler.sendClientPackets(removed, synchronusOK);
/*     */     }
/*     */   }
/*     */ 
/*     */   private boolean onMainThread()
/*     */   {
/* 286 */     return Thread.currentThread().getId() == this.mainThread.getId();
/*     */   }
/*     */ 
/*     */   public void unregisterAsyncHandlers(Plugin plugin)
/*     */   {
/* 291 */     unregisterAsyncHandlers(this.serverProcessingQueue, plugin);
/* 292 */     unregisterAsyncHandlers(this.clientProcessingQueue, plugin);
/*     */   }
/*     */ 
/*     */   private void unregisterAsyncHandlers(PacketProcessingQueue processingQueue, Plugin plugin)
/*     */   {
/* 298 */     for (PrioritizedListener listener : processingQueue.values())
/*     */     {
/* 300 */       if (Objects.equal(((AsyncListenerHandler)listener.getListener()).getPlugin(), plugin))
/* 301 */         unregisterAsyncHandler((AsyncListenerHandler)listener.getListener());
/*     */     }
/*     */   }
/*     */ 
/*     */   public synchronized void enqueueSyncPacket(PacketEvent syncPacket, AsyncMarker asyncMarker)
/*     */   {
/* 313 */     PacketEvent newEvent = PacketEvent.fromSynchronous(syncPacket, asyncMarker);
/*     */ 
/* 315 */     if ((asyncMarker.isQueued()) || (asyncMarker.isTransmitted())) {
/* 316 */       throw new IllegalArgumentException("Cannot queue a packet that has already been queued.");
/*     */     }
/* 318 */     asyncMarker.setQueuedSendingIndex(Long.valueOf(asyncMarker.getNewSendingIndex()));
/*     */ 
/* 322 */     Player player = newEvent.getPlayer();
/* 323 */     if (player != null)
/*     */     {
/* 325 */       getSendingQueue(syncPacket).enqueue(newEvent);
/*     */ 
/* 328 */       getProcessingQueue(syncPacket).enqueue(newEvent, true);
/*     */     }
/*     */   }
/*     */ 
/*     */   public Set<Integer> getSendingFilters()
/*     */   {
/* 334 */     return PacketRegistry.toLegacy(this.serverProcessingQueue.keySet());
/*     */   }
/*     */ 
/*     */   public Set<PacketType> getReceivingTypes()
/*     */   {
/* 339 */     return this.serverProcessingQueue.keySet();
/*     */   }
/*     */ 
/*     */   public Set<Integer> getReceivingFilters()
/*     */   {
/* 344 */     return PacketRegistry.toLegacy(this.clientProcessingQueue.keySet());
/*     */   }
/*     */ 
/*     */   public Set<PacketType> getSendingTypes()
/*     */   {
/* 349 */     return this.clientProcessingQueue.keySet();
/*     */   }
/*     */ 
/*     */   public BukkitScheduler getScheduler()
/*     */   {
/* 357 */     return this.scheduler;
/*     */   }
/*     */ 
/*     */   public boolean hasAsynchronousListeners(PacketEvent packet)
/*     */   {
/* 362 */     Collection list = getProcessingQueue(packet).getListener(packet.getPacketType());
/* 363 */     return (list != null) && (list.size() > 0);
/*     */   }
/*     */ 
/*     */   public AsyncMarker createAsyncMarker()
/*     */   {
/* 371 */     return createAsyncMarker(1800000L);
/*     */   }
/*     */ 
/*     */   public AsyncMarker createAsyncMarker(long timeoutDelta)
/*     */   {
/* 381 */     return createAsyncMarker(timeoutDelta, this.currentSendingIndex.incrementAndGet());
/*     */   }
/*     */ 
/*     */   private AsyncMarker createAsyncMarker(long timeoutDelta, long sendingIndex)
/*     */   {
/* 386 */     return new AsyncMarker(this.manager, sendingIndex, System.currentTimeMillis(), timeoutDelta);
/*     */   }
/*     */ 
/*     */   public PacketStream getPacketStream()
/*     */   {
/* 391 */     return this.manager;
/*     */   }
/*     */ 
/*     */   public ErrorReporter getErrorReporter()
/*     */   {
/* 396 */     return this.reporter;
/*     */   }
/*     */ 
/*     */   public void cleanupAll()
/*     */   {
/* 401 */     this.serverProcessingQueue.cleanupAll();
/* 402 */     this.playerSendingHandler.cleanupAll();
/* 403 */     this.timeoutListeners.clear();
/*     */ 
/* 405 */     this.serverTimeoutListeners = null;
/* 406 */     this.clientTimeoutListeners = null;
/*     */   }
/*     */ 
/*     */   public void signalPacketTransmission(PacketEvent packet)
/*     */   {
/* 411 */     signalPacketTransmission(packet, onMainThread());
/*     */   }
/*     */ 
/*     */   private void signalPacketTransmission(PacketEvent packet, boolean onMainThread)
/*     */   {
/* 420 */     AsyncMarker marker = packet.getAsyncMarker();
/* 421 */     if (marker == null) {
/* 422 */       throw new IllegalArgumentException("A sync packet cannot be transmitted by the asynchronous manager.");
/*     */     }
/* 424 */     if (!marker.isQueued()) {
/* 425 */       throw new IllegalArgumentException("A packet must have been queued before it can be transmitted.");
/*     */     }
/*     */ 
/* 429 */     if (marker.decrementProcessingDelay() == 0) {
/* 430 */       PacketSendingQueue queue = getSendingQueue(packet, false);
/*     */ 
/* 433 */       if (queue != null)
/* 434 */         queue.signalPacketUpdate(packet, onMainThread);
/*     */     }
/*     */   }
/*     */ 
/*     */   public PacketSendingQueue getSendingQueue(PacketEvent packet)
/*     */   {
/* 444 */     return this.playerSendingHandler.getSendingQueue(packet);
/*     */   }
/*     */ 
/*     */   public PacketSendingQueue getSendingQueue(PacketEvent packet, boolean createNew)
/*     */   {
/* 454 */     return this.playerSendingHandler.getSendingQueue(packet, createNew);
/*     */   }
/*     */ 
/*     */   public PacketProcessingQueue getProcessingQueue(PacketEvent packet)
/*     */   {
/* 463 */     return packet.isServerPacket() ? this.serverProcessingQueue : this.clientProcessingQueue;
/*     */   }
/*     */ 
/*     */   public void signalFreeProcessingSlot(PacketEvent packet)
/*     */   {
/* 471 */     getProcessingQueue(packet).signalProcessingDone();
/*     */   }
/*     */ 
/*     */   public void sendProcessedPackets(int tickCounter, boolean onMainThread)
/*     */   {
/* 479 */     if (tickCounter % 10 == 0) {
/* 480 */       this.playerSendingHandler.trySendServerPackets(onMainThread);
/*     */     }
/*     */ 
/* 483 */     this.playerSendingHandler.trySendClientPackets(onMainThread);
/*     */   }
/*     */ 
/*     */   public void removePlayer(Player player)
/*     */   {
/* 491 */     this.playerSendingHandler.removePlayer(player);
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.async.AsyncFilterManager
 * JD-Core Version:    0.6.2
 */