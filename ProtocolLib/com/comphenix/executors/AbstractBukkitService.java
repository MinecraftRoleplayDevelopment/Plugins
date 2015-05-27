/*     */ package com.comphenix.executors;
/*     */ 
/*     */ import com.google.common.base.Throwables;
/*     */ import com.google.common.util.concurrent.ListenableScheduledFuture;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */ import java.util.concurrent.Callable;
/*     */ import java.util.concurrent.Executors;
/*     */ import java.util.concurrent.Future;
/*     */ import java.util.concurrent.RejectedExecutionException;
/*     */ import java.util.concurrent.RunnableFuture;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import org.bukkit.scheduler.BukkitTask;
/*     */ 
/*     */ abstract class AbstractBukkitService extends AbstractListeningService
/*     */   implements BukkitScheduledExecutorService
/*     */ {
/*     */   private static final long MILLISECONDS_PER_TICK = 50L;
/*     */   private static final long NANOSECONDS_PER_TICK = 50000000L;
/*     */   private volatile boolean shutdown;
/*     */   private PendingTasks tasks;
/*     */ 
/*     */   public AbstractBukkitService(PendingTasks tasks)
/*     */   {
/*  27 */     this.tasks = tasks;
/*     */   }
/*     */ 
/*     */   protected <T> AbstractListeningService.RunnableAbstractFuture<T> newTaskFor(Runnable runnable, T value)
/*     */   {
/*  32 */     return newTaskFor(Executors.callable(runnable, value));
/*     */   }
/*     */ 
/*     */   protected <T> AbstractListeningService.RunnableAbstractFuture<T> newTaskFor(Callable<T> callable)
/*     */   {
/*  37 */     validateState();
/*  38 */     return new CallableTask(callable);
/*     */   }
/*     */ 
/*     */   public void execute(Runnable command)
/*     */   {
/*  43 */     validateState();
/*     */ 
/*  45 */     if ((command instanceof RunnableFuture)) {
/*  46 */       this.tasks.add(getTask(command), (Future)command);
/*     */     }
/*     */     else
/*  49 */       submit(command);
/*     */   }
/*     */ 
/*     */   protected abstract BukkitTask getTask(Runnable paramRunnable);
/*     */ 
/*     */   protected abstract BukkitTask getLaterTask(Runnable paramRunnable, long paramLong);
/*     */ 
/*     */   protected abstract BukkitTask getTimerTask(long paramLong1, long paramLong2, Runnable paramRunnable);
/*     */ 
/*     */   public List<Runnable> shutdownNow()
/*     */   {
/*  60 */     shutdown();
/*  61 */     this.tasks.cancel();
/*     */ 
/*  64 */     return Collections.emptyList();
/*     */   }
/*     */ 
/*     */   public void shutdown()
/*     */   {
/*  69 */     this.shutdown = true;
/*     */   }
/*     */ 
/*     */   private void validateState() {
/*  73 */     if (this.shutdown)
/*  74 */       throw new RejectedExecutionException("Executor service has shut down. Cannot start new tasks.");
/*     */   }
/*     */ 
/*     */   private long toTicks(long delay, TimeUnit unit)
/*     */   {
/*  79 */     return Math.round(unit.toMillis(delay) / 50.0D);
/*     */   }
/*     */ 
/*     */   public ListenableScheduledFuture<?> schedule(Runnable command, long delay, TimeUnit unit)
/*     */   {
/*  84 */     return schedule(Executors.callable(command), delay, unit);
/*     */   }
/*     */ 
/*     */   public <V> ListenableScheduledFuture<V> schedule(Callable<V> callable, long delay, TimeUnit unit)
/*     */   {
/*  89 */     long ticks = toTicks(delay, unit);
/*     */ 
/*  92 */     CallableTask task = new CallableTask(callable);
/*  93 */     BukkitTask bukkitTask = getLaterTask(task, ticks);
/*     */ 
/*  95 */     this.tasks.add(bukkitTask, task);
/*  96 */     return task.getScheduledFuture(System.nanoTime() + delay * 50000000L, 0L);
/*     */   }
/*     */ 
/*     */   public ListenableScheduledFuture<?> scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit)
/*     */   {
/* 103 */     long ticksInitial = toTicks(initialDelay, unit);
/* 104 */     long ticksDelay = toTicks(period, unit);
/*     */ 
/* 107 */     CallableTask task = new CallableTask(Executors.callable(command))
/*     */     {
/*     */       protected void compute() {
/*     */         try {
/* 111 */           this.compute.call();
/*     */         }
/*     */         catch (Exception e) {
/* 114 */           throw Throwables.propagate(e);
/*     */         }
/*     */       }
/*     */     };
/* 118 */     BukkitTask bukkitTask = getTimerTask(ticksInitial, ticksDelay, task);
/*     */ 
/* 120 */     this.tasks.add(bukkitTask, task);
/* 121 */     return task.getScheduledFuture(System.nanoTime() + ticksInitial * 50000000L, ticksDelay * 50000000L);
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public ListenableScheduledFuture<?> scheduleWithFixedDelay(Runnable command, long initialDelay, long delay, TimeUnit unit)
/*     */   {
/* 130 */     return scheduleAtFixedRate(command, initialDelay, delay, unit);
/*     */   }
/*     */ 
/*     */   public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException
/*     */   {
/* 135 */     return this.tasks.awaitTermination(timeout, unit);
/*     */   }
/*     */ 
/*     */   public boolean isShutdown()
/*     */   {
/* 140 */     return this.shutdown;
/*     */   }
/*     */ 
/*     */   public boolean isTerminated()
/*     */   {
/* 145 */     return this.tasks.isTerminated();
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.executors.AbstractBukkitService
 * JD-Core Version:    0.6.2
 */