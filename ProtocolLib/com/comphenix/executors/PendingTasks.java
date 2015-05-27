/*     */ package com.comphenix.executors;
/*     */ 
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.Set;
/*     */ import java.util.concurrent.Future;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import org.bukkit.plugin.Plugin;
/*     */ import org.bukkit.scheduler.BukkitScheduler;
/*     */ import org.bukkit.scheduler.BukkitTask;
/*     */ 
/*     */ class PendingTasks
/*     */ {
/*  25 */   private Set<CancelableFuture> pending = new HashSet();
/*  26 */   private final Object pendingLock = new Object();
/*     */   private final Plugin plugin;
/*     */   private final BukkitScheduler scheduler;
/*     */   private BukkitTask cancellationTask;
/*     */ 
/*     */   public PendingTasks(Plugin plugin, BukkitScheduler scheduler)
/*     */   {
/*  34 */     this.plugin = plugin;
/*  35 */     this.scheduler = scheduler;
/*     */   }
/*     */ 
/*     */   public void add(final BukkitTask task, final Future<?> future) {
/*  39 */     add(new CancelableFuture()
/*     */     {
/*     */       public boolean isTaskCancelled()
/*     */       {
/*  43 */         if (future.isDone()) {
/*  44 */           return future.isCancelled();
/*     */         }
/*  46 */         return (!PendingTasks.this.scheduler.isCurrentlyRunning(task.getTaskId())) && (!PendingTasks.this.scheduler.isQueued(task.getTaskId()));
/*     */       }
/*     */ 
/*     */       public void cancel()
/*     */       {
/*  53 */         task.cancel();
/*  54 */         future.cancel(true);
/*     */       }
/*     */     });
/*     */   }
/*     */ 
/*     */   private CancelableFuture add(CancelableFuture task) {
/*  60 */     synchronized (this.pendingLock) {
/*  61 */       this.pending.add(task);
/*  62 */       this.pendingLock.notifyAll();
/*  63 */       beginCancellationTask();
/*  64 */       return task;
/*     */     }
/*     */   }
/*     */ 
/*     */   private void beginCancellationTask() {
/*  69 */     if (this.cancellationTask == null)
/*  70 */       this.cancellationTask = this.scheduler.runTaskTimer(this.plugin, new Runnable()
/*     */       {
/*     */         public void run()
/*     */         {
/*  74 */           synchronized (PendingTasks.this.pendingLock) {
/*  75 */             boolean changed = false;
/*     */ 
/*  77 */             for (Iterator it = PendingTasks.this.pending.iterator(); it.hasNext(); ) {
/*  78 */               PendingTasks.CancelableFuture future = (PendingTasks.CancelableFuture)it.next();
/*     */ 
/*  81 */               if (future.isTaskCancelled()) {
/*  82 */                 future.cancel();
/*  83 */                 it.remove();
/*  84 */                 changed = true;
/*     */               }
/*     */ 
/*     */             }
/*     */ 
/*  89 */             if (changed) {
/*  90 */               PendingTasks.this.pendingLock.notifyAll();
/*     */             }
/*     */ 
/*     */           }
/*     */ 
/*  95 */           if (PendingTasks.this.isTerminated()) {
/*  96 */             PendingTasks.this.cancellationTask.cancel();
/*  97 */             PendingTasks.this.cancellationTask = null;
/*     */           }
/*     */         }
/*     */       }
/*     */       , 1L, 1L);
/*     */   }
/*     */ 
/*     */   public void cancel()
/*     */   {
/* 108 */     for (CancelableFuture task : this.pending)
/* 109 */       task.cancel();
/*     */   }
/*     */ 
/*     */   public boolean awaitTermination(long timeout, TimeUnit unit)
/*     */     throws InterruptedException
/*     */   {
/* 121 */     long expire = System.nanoTime() + unit.toNanos(timeout);
/*     */ 
/* 123 */     synchronized (this.pendingLock)
/*     */     {
/* 125 */       while (!isTerminated())
/*     */       {
/* 127 */         if (expire < System.nanoTime())
/* 128 */           return false;
/* 129 */         unit.timedWait(this.pendingLock, timeout);
/*     */       }
/*     */     }
/*     */ 
/* 133 */     return false;
/*     */   }
/*     */ 
/*     */   public boolean isTerminated()
/*     */   {
/* 141 */     return this.pending.isEmpty();
/*     */   }
/*     */ 
/*     */   private static abstract interface CancelableFuture
/*     */   {
/*     */     public abstract void cancel();
/*     */ 
/*     */     public abstract boolean isTaskCancelled();
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.executors.PendingTasks
 * JD-Core Version:    0.6.2
 */