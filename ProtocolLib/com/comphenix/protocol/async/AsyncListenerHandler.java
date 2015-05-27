/*     */ package com.comphenix.protocol.async;
/*     */ 
/*     */ import com.comphenix.protocol.ProtocolLibrary;
/*     */ import com.comphenix.protocol.error.ErrorReporter;
/*     */ import com.comphenix.protocol.error.Report;
/*     */ import com.comphenix.protocol.error.Report.ReportBuilder;
/*     */ import com.comphenix.protocol.error.ReportType;
/*     */ import com.comphenix.protocol.events.ListeningWhitelist;
/*     */ import com.comphenix.protocol.events.PacketAdapter;
/*     */ import com.comphenix.protocol.events.PacketEvent;
/*     */ import com.comphenix.protocol.events.PacketListener;
/*     */ import com.comphenix.protocol.injector.PrioritizedListener;
/*     */ import com.comphenix.protocol.timing.TimedListenerManager;
/*     */ import com.comphenix.protocol.timing.TimedListenerManager.ListenerType;
/*     */ import com.comphenix.protocol.timing.TimedTracker;
/*     */ import com.comphenix.protocol.utility.WrappedScheduler;
/*     */ import com.google.common.base.Function;
/*     */ import com.google.common.base.Joiner;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.Set;
/*     */ import java.util.concurrent.ArrayBlockingQueue;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import java.util.concurrent.atomic.AtomicBoolean;
/*     */ import java.util.concurrent.atomic.AtomicInteger;
/*     */ import org.bukkit.plugin.Plugin;
/*     */ import org.bukkit.scheduler.BukkitScheduler;
/*     */ 
/*     */ public class AsyncListenerHandler
/*     */ {
/*  50 */   public static final ReportType REPORT_HANDLER_NOT_STARTED = new ReportType("Plugin %s did not start the asynchronous handler %s by calling start() or syncStart().");
/*     */ 
/*  56 */   private static final PacketEvent INTERUPT_PACKET = new PacketEvent(new Object());
/*     */ 
/*  61 */   private static final PacketEvent WAKEUP_PACKET = new PacketEvent(new Object());
/*     */   private static final int TICKS_PER_SECOND = 20;
/*  69 */   private static final AtomicInteger nextID = new AtomicInteger();
/*     */   private static final int DEFAULT_CAPACITY = 1024;
/*     */   private volatile boolean cancelled;
/*  78 */   private final AtomicInteger started = new AtomicInteger();
/*     */   private PacketListener listener;
/*     */   private AsyncFilterManager filterManager;
/*     */   private NullPacketListener nullPacketListener;
/*  88 */   private ArrayBlockingQueue<PacketEvent> queuedPackets = new ArrayBlockingQueue(1024);
/*     */ 
/*  91 */   private final Set<Integer> stoppedTasks = new HashSet();
/*  92 */   private final Object stopLock = new Object();
/*     */ 
/*  95 */   private int syncTask = -1;
/*     */   private Thread mainThread;
/*     */   private int warningTask;
/* 104 */   private TimedListenerManager timedManager = TimedListenerManager.getInstance();
/*     */ 
/*     */   AsyncListenerHandler(Thread mainThread, AsyncFilterManager filterManager, PacketListener listener)
/*     */   {
/* 113 */     if (filterManager == null)
/* 114 */       throw new IllegalArgumentException("filterManager cannot be NULL");
/* 115 */     if (listener == null) {
/* 116 */       throw new IllegalArgumentException("listener cannot be NULL");
/*     */     }
/* 118 */     this.mainThread = mainThread;
/* 119 */     this.filterManager = filterManager;
/* 120 */     this.listener = listener;
/* 121 */     startWarningTask();
/*     */   }
/*     */ 
/*     */   private void startWarningTask() {
/* 125 */     this.warningTask = this.filterManager.getScheduler().scheduleSyncDelayedTask(getPlugin(), new Runnable()
/*     */     {
/*     */       public void run() {
/* 128 */         ProtocolLibrary.getErrorReporter().reportWarning(AsyncListenerHandler.this, Report.newBuilder(AsyncListenerHandler.REPORT_HANDLER_NOT_STARTED).messageParam(new Object[] { AsyncListenerHandler.this.listener.getPlugin(), AsyncListenerHandler.this }).build());
/*     */       }
/*     */     }
/*     */     , 40L);
/*     */   }
/*     */ 
/*     */   private void stopWarningTask()
/*     */   {
/* 138 */     int taskId = this.warningTask;
/*     */ 
/* 141 */     if (this.warningTask >= 0) {
/* 142 */       this.filterManager.getScheduler().cancelTask(taskId);
/* 143 */       this.warningTask = -1;
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean isCancelled()
/*     */   {
/* 152 */     return this.cancelled;
/*     */   }
/*     */ 
/*     */   public PacketListener getAsyncListener()
/*     */   {
/* 160 */     return this.listener;
/*     */   }
/*     */ 
/*     */   void setNullPacketListener(NullPacketListener nullPacketListener)
/*     */   {
/* 168 */     this.nullPacketListener = nullPacketListener;
/*     */   }
/*     */ 
/*     */   PacketListener getNullPacketListener()
/*     */   {
/* 176 */     return this.nullPacketListener;
/*     */   }
/*     */ 
/*     */   public Plugin getPlugin()
/*     */   {
/* 184 */     return this.listener != null ? this.listener.getPlugin() : null;
/*     */   }
/*     */ 
/*     */   public void cancel()
/*     */   {
/* 192 */     close();
/*     */   }
/*     */ 
/*     */   public void enqueuePacket(PacketEvent packet)
/*     */   {
/* 201 */     if (packet == null) {
/* 202 */       throw new IllegalArgumentException("packet is NULL");
/*     */     }
/* 204 */     this.queuedPackets.add(packet);
/*     */   }
/*     */ 
/*     */   public AsyncRunnable getListenerLoop()
/*     */   {
/* 214 */     return new AsyncRunnable()
/*     */     {
/* 216 */       private final AtomicBoolean firstRun = new AtomicBoolean();
/* 217 */       private final AtomicBoolean finished = new AtomicBoolean();
/* 218 */       private final int id = AsyncListenerHandler.nextID.incrementAndGet();
/*     */ 
/*     */       public int getID()
/*     */       {
/* 222 */         return this.id;
/*     */       }
/*     */ 
/*     */       public void run()
/*     */       {
/* 228 */         if (this.firstRun.compareAndSet(false, true)) {
/* 229 */           AsyncListenerHandler.this.listenerLoop(this.id);
/*     */ 
/* 231 */           synchronized (AsyncListenerHandler.this.stopLock) {
/* 232 */             AsyncListenerHandler.this.stoppedTasks.remove(Integer.valueOf(this.id));
/* 233 */             AsyncListenerHandler.this.stopLock.notifyAll();
/* 234 */             this.finished.set(true);
/*     */           }
/*     */         }
/*     */         else {
/* 238 */           if (this.finished.get()) {
/* 239 */             throw new IllegalStateException("This listener has already been run. Create a new instead.");
/*     */           }
/*     */ 
/* 242 */           throw new IllegalStateException("This listener loop has already been started. Create a new instead.");
/*     */         }
/*     */       }
/*     */ 
/*     */       public boolean stop()
/*     */         throws InterruptedException
/*     */       {
/* 249 */         synchronized (AsyncListenerHandler.this.stopLock) {
/* 250 */           if (!isRunning()) {
/* 251 */             return false;
/*     */           }
/* 253 */           AsyncListenerHandler.this.stoppedTasks.add(Integer.valueOf(this.id));
/*     */ 
/* 256 */           for (int i = 0; i < AsyncListenerHandler.this.getWorkers(); i++) {
/* 257 */             AsyncListenerHandler.this.queuedPackets.offer(AsyncListenerHandler.WAKEUP_PACKET);
/*     */           }
/*     */ 
/* 260 */           this.finished.set(true);
/* 261 */           AsyncListenerHandler.this.waitForStops();
/* 262 */           return true;
/*     */         }
/*     */       }
/*     */ 
/*     */       public boolean isRunning()
/*     */       {
/* 268 */         return (this.firstRun.get()) && (!this.finished.get());
/*     */       }
/*     */ 
/*     */       public boolean isFinished()
/*     */       {
/* 273 */         return this.finished.get();
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   public synchronized void start()
/*     */   {
/* 282 */     if (this.listener.getPlugin() == null)
/* 283 */       throw new IllegalArgumentException("Cannot start task without a valid plugin.");
/* 284 */     if (this.cancelled) {
/* 285 */       throw new IllegalStateException("Cannot start a worker when the listener is closing.");
/*     */     }
/* 287 */     final AsyncRunnable listenerLoop = getListenerLoop();
/*     */ 
/* 289 */     stopWarningTask();
/* 290 */     scheduleAsync(new Runnable()
/*     */     {
/*     */       public void run() {
/* 293 */         Thread thread = Thread.currentThread();
/*     */ 
/* 295 */         String previousName = thread.getName();
/* 296 */         String workerName = AsyncListenerHandler.this.getFriendlyWorkerName(listenerLoop.getID());
/*     */ 
/* 299 */         thread.setName(workerName);
/* 300 */         listenerLoop.run();
/* 301 */         thread.setName(previousName);
/*     */       }
/*     */     });
/*     */   }
/*     */ 
/*     */   public synchronized void start(Function<AsyncRunnable, Void> executor)
/*     */   {
/* 330 */     if (this.listener.getPlugin() == null)
/* 331 */       throw new IllegalArgumentException("Cannot start task without a valid plugin.");
/* 332 */     if (this.cancelled) {
/* 333 */       throw new IllegalStateException("Cannot start a worker when the listener is closing.");
/*     */     }
/* 335 */     final AsyncRunnable listenerLoop = getListenerLoop();
/* 336 */     final Function delegateCopy = executor;
/*     */ 
/* 338 */     scheduleAsync(new Runnable()
/*     */     {
/*     */       public void run() {
/* 341 */         delegateCopy.apply(listenerLoop);
/*     */       }
/*     */     });
/*     */   }
/*     */ 
/*     */   private void scheduleAsync(Runnable runnable)
/*     */   {
/* 348 */     WrappedScheduler.runAsynchronouslyRepeat(this.listener.getPlugin(), this.filterManager.getScheduler(), runnable, 0L, -1L);
/*     */   }
/*     */ 
/*     */   public String getFriendlyWorkerName(int id)
/*     */   {
/* 360 */     return String.format("Protocol Worker #%s - %s - [recv: %s, send: %s]", new Object[] { Integer.valueOf(id), PacketAdapter.getPluginName(this.listener), fromWhitelist(this.listener.getReceivingWhitelist()), fromWhitelist(this.listener.getSendingWhitelist()) });
/*     */   }
/*     */ 
/*     */   private String fromWhitelist(ListeningWhitelist whitelist)
/*     */   {
/* 374 */     if (whitelist == null) {
/* 375 */       return "";
/*     */     }
/* 377 */     return Joiner.on(", ").join(whitelist.getTypes());
/*     */   }
/*     */ 
/*     */   public synchronized boolean syncStart()
/*     */   {
/* 393 */     return syncStart(500L, TimeUnit.MICROSECONDS);
/*     */   }
/*     */ 
/*     */   public synchronized boolean syncStart(final long time, final TimeUnit unit)
/*     */   {
/* 416 */     if (time <= 0L)
/* 417 */       throw new IllegalArgumentException("Time must be greater than zero.");
/* 418 */     if (unit == null) {
/* 419 */       throw new IllegalArgumentException("TimeUnit cannot be NULL.");
/*     */     }
/* 421 */     long tickDelay = 1L;
/* 422 */     int workerID = nextID.incrementAndGet();
/*     */ 
/* 424 */     if (this.syncTask < 0) {
/* 425 */       stopWarningTask();
/*     */ 
/* 427 */       this.syncTask = this.filterManager.getScheduler().scheduleSyncRepeatingTask(getPlugin(), new Runnable()
/*     */       {
/*     */         public void run() {
/* 430 */           long stopTime = System.nanoTime() + unit.convert(time, TimeUnit.NANOSECONDS);
/*     */ 
/* 432 */           while (!AsyncListenerHandler.this.cancelled) {
/* 433 */             PacketEvent packet = (PacketEvent)AsyncListenerHandler.this.queuedPackets.poll();
/*     */ 
/* 435 */             if ((packet == AsyncListenerHandler.INTERUPT_PACKET) || (packet == AsyncListenerHandler.WAKEUP_PACKET))
/*     */             {
/* 437 */               AsyncListenerHandler.this.queuedPackets.add(packet);
/*     */             }
/* 441 */             else if ((packet != null) && (packet.getAsyncMarker() != null)) {
/* 442 */               AsyncListenerHandler.this.processPacket(this.val$workerID, packet, "onSyncPacket()");
/*     */ 
/* 449 */               if (System.nanoTime() < stopTime)
/*     */                 break;
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */       , 1L, 1L);
/*     */ 
/* 456 */       if (this.syncTask < 0) {
/* 457 */         throw new IllegalStateException("Cannot start synchronous task.");
/*     */       }
/* 459 */       return true;
/*     */     }
/* 461 */     return false;
/*     */   }
/*     */ 
/*     */   public synchronized boolean syncStop()
/*     */   {
/* 470 */     if (this.syncTask > 0) {
/* 471 */       this.filterManager.getScheduler().cancelTask(this.syncTask);
/*     */ 
/* 473 */       this.syncTask = -1;
/* 474 */       return true;
/*     */     }
/* 476 */     return false;
/*     */   }
/*     */ 
/*     */   public synchronized void start(int count)
/*     */   {
/* 485 */     for (int i = 0; i < count; i++)
/* 486 */       start();
/*     */   }
/*     */ 
/*     */   public synchronized void stop()
/*     */   {
/* 493 */     this.queuedPackets.add(INTERUPT_PACKET);
/*     */   }
/*     */ 
/*     */   public synchronized void stop(int count)
/*     */   {
/* 501 */     for (int i = 0; i < count; i++)
/* 502 */       stop();
/*     */   }
/*     */ 
/*     */   public synchronized void setWorkers(int count)
/*     */   {
/* 512 */     if (count < 0)
/* 513 */       throw new IllegalArgumentException("Number of workers cannot be less than zero.");
/* 514 */     if (count > 1024)
/* 515 */       throw new IllegalArgumentException("Cannot initiate more than 1024 workers");
/* 516 */     if ((this.cancelled) && (count > 0)) {
/* 517 */       throw new IllegalArgumentException("Cannot add workers when the listener is closing.");
/*     */     }
/* 519 */     long time = System.currentTimeMillis();
/*     */ 
/* 522 */     while (this.started.get() != count) {
/* 523 */       if (this.started.get() < count)
/* 524 */         start();
/*     */       else {
/* 526 */         stop();
/*     */       }
/*     */ 
/* 529 */       if (System.currentTimeMillis() - time > 50L)
/* 530 */         throw new RuntimeException("Failed to set worker count.");
/*     */     }
/*     */   }
/*     */ 
/*     */   public synchronized int getWorkers()
/*     */   {
/* 541 */     return this.started.get();
/*     */   }
/*     */ 
/*     */   private boolean waitForStops()
/*     */     throws InterruptedException
/*     */   {
/* 550 */     synchronized (this.stopLock) {
/* 551 */       while ((this.stoppedTasks.size() > 0) && (!this.cancelled)) {
/* 552 */         this.stopLock.wait();
/*     */       }
/* 554 */       return this.cancelled;
/*     */     }
/*     */   }
/*     */ 
/*     */   private void listenerLoop(int workerID)
/*     */   {
/* 566 */     if (Thread.currentThread().getId() == this.mainThread.getId())
/* 567 */       throw new IllegalStateException("Do not call this method from the main thread.");
/* 568 */     if (this.cancelled) {
/* 569 */       throw new IllegalStateException("Listener has been cancelled. Create a new listener instead.");
/*     */     }
/*     */     try
/*     */     {
/* 573 */       if (waitForStops())
/*     */       {
/*     */         return;
/*     */       }
/* 577 */       this.started.incrementAndGet();
/*     */ 
/* 579 */       while (!this.cancelled) {
/* 580 */         PacketEvent packet = (PacketEvent)this.queuedPackets.take();
/*     */ 
/* 583 */         if (packet == WAKEUP_PACKET)
/*     */         {
/* 585 */           synchronized (this.stopLock)
/*     */           {
/* 587 */             if (this.stoppedTasks.contains(Integer.valueOf(workerID)))
/*     */               return;
/* 589 */             if (waitForStops()) return;
/*     */           }
/*     */         }
/* 592 */         else if (packet == INTERUPT_PACKET)
/*     */         {
/*     */           return;
/*     */         }
/* 596 */         if ((packet != null) && (packet.getAsyncMarker() != null))
/* 597 */           processPacket(workerID, packet, "onAsyncPacket()");
/*     */       }
/*     */     }
/*     */     catch (InterruptedException e)
/*     */     {
/*     */     }
/*     */     finally
/*     */     {
/* 605 */       this.started.decrementAndGet();
/*     */     }
/*     */   }
/*     */ 
/*     */   private void processPacket(int workerID, PacketEvent packet, String methodName)
/*     */   {
/* 616 */     AsyncMarker marker = packet.getAsyncMarker();
/*     */     try
/*     */     {
/* 620 */       synchronized (marker.getProcessingLock()) {
/* 621 */         marker.setListenerHandler(this);
/* 622 */         marker.setWorkerID(workerID);
/*     */ 
/* 625 */         if (this.timedManager.isTiming())
/*     */         {
/* 627 */           TimedTracker tracker = this.timedManager.getTracker(this.listener, packet.isServerPacket() ? TimedListenerManager.ListenerType.ASYNC_SERVER_SIDE : TimedListenerManager.ListenerType.ASYNC_CLIENT_SIDE);
/*     */ 
/* 629 */           long token = tracker.beginTracking();
/*     */ 
/* 631 */           if (packet.isServerPacket())
/* 632 */             this.listener.onPacketSending(packet);
/*     */           else {
/* 634 */             this.listener.onPacketReceiving(packet);
/*     */           }
/*     */ 
/* 637 */           tracker.endTracking(token, packet.getPacketType());
/*     */         }
/* 640 */         else if (packet.isServerPacket()) {
/* 641 */           this.listener.onPacketSending(packet);
/*     */         } else {
/* 643 */           this.listener.onPacketReceiving(packet);
/*     */         }
/*     */       }
/*     */     }
/*     */     catch (OutOfMemoryError e) {
/* 648 */       throw e;
/*     */     } catch (ThreadDeath e) {
/* 650 */       throw e;
/*     */     }
/*     */     catch (Throwable e) {
/* 653 */       this.filterManager.getErrorReporter().reportMinimal(this.listener.getPlugin(), methodName, e);
/*     */     }
/*     */ 
/* 657 */     if (!marker.hasExpired()) {
/* 658 */       while (marker.getListenerTraversal().hasNext()) {
/* 659 */         AsyncListenerHandler handler = (AsyncListenerHandler)((PrioritizedListener)marker.getListenerTraversal().next()).getListener();
/*     */ 
/* 661 */         if (!handler.isCancelled()) {
/* 662 */           handler.enqueuePacket(packet);
/* 663 */           return;
/*     */         }
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 669 */     this.filterManager.signalFreeProcessingSlot(packet);
/*     */ 
/* 672 */     this.filterManager.signalPacketTransmission(packet);
/*     */   }
/*     */ 
/*     */   private synchronized void close()
/*     */   {
/* 680 */     if (!this.cancelled) {
/* 681 */       this.filterManager.unregisterAsyncHandlerInternal(this);
/* 682 */       this.cancelled = true;
/*     */ 
/* 685 */       syncStop();
/*     */ 
/* 688 */       stopThreads();
/*     */     }
/*     */   }
/*     */ 
/*     */   private void stopThreads()
/*     */   {
/* 697 */     this.queuedPackets.clear();
/* 698 */     stop(this.started.get());
/*     */ 
/* 701 */     synchronized (this.stopLock) {
/* 702 */       this.stopLock.notifyAll();
/*     */     }
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.async.AsyncListenerHandler
 * JD-Core Version:    0.6.2
 */