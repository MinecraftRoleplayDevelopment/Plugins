/*     */ package com.comphenix.protocol.injector;
/*     */ 
/*     */ import org.bukkit.Server;
/*     */ import org.bukkit.plugin.Plugin;
/*     */ import org.bukkit.scheduler.BukkitScheduler;
/*     */ 
/*     */ public class DelayedSingleTask
/*     */ {
/*  30 */   protected int taskID = -1;
/*     */   protected Plugin plugin;
/*     */   protected BukkitScheduler scheduler;
/*     */   protected boolean closed;
/*     */ 
/*     */   public DelayedSingleTask(Plugin plugin)
/*     */   {
/*  40 */     this.plugin = plugin;
/*  41 */     this.scheduler = plugin.getServer().getScheduler();
/*     */   }
/*     */ 
/*     */   public DelayedSingleTask(Plugin plugin, BukkitScheduler scheduler)
/*     */   {
/*  50 */     this.plugin = plugin;
/*  51 */     this.scheduler = scheduler;
/*     */   }
/*     */ 
/*     */   public boolean schedule(long ticksDelay, Runnable task)
/*     */   {
/*  66 */     if (ticksDelay < 0L)
/*  67 */       throw new IllegalArgumentException("Tick delay cannot be negative.");
/*  68 */     if (task == null)
/*  69 */       throw new IllegalArgumentException("task cannot be NULL");
/*  70 */     if (this.closed) {
/*  71 */       return false;
/*     */     }
/*     */ 
/*  74 */     if (ticksDelay == 0L) {
/*  75 */       task.run();
/*  76 */       return true;
/*     */     }
/*     */ 
/*  80 */     final Runnable dispatch = task;
/*     */ 
/*  83 */     cancel();
/*  84 */     this.taskID = this.scheduler.scheduleSyncDelayedTask(this.plugin, new Runnable()
/*     */     {
/*     */       public void run() {
/*  87 */         dispatch.run();
/*  88 */         DelayedSingleTask.this.taskID = -1;
/*     */       }
/*     */     }
/*     */     , ticksDelay);
/*     */ 
/*  92 */     return isRunning();
/*     */   }
/*     */ 
/*     */   public boolean isRunning()
/*     */   {
/* 100 */     return this.taskID >= 0;
/*     */   }
/*     */ 
/*     */   public boolean cancel()
/*     */   {
/* 108 */     if (isRunning()) {
/* 109 */       this.scheduler.cancelTask(this.taskID);
/* 110 */       this.taskID = -1;
/* 111 */       return true;
/*     */     }
/* 113 */     return false;
/*     */   }
/*     */ 
/*     */   public int getTaskID()
/*     */   {
/* 122 */     return this.taskID;
/*     */   }
/*     */ 
/*     */   public Plugin getPlugin()
/*     */   {
/* 130 */     return this.plugin;
/*     */   }
/*     */ 
/*     */   public synchronized void close()
/*     */   {
/* 137 */     if (!this.closed) {
/* 138 */       cancel();
/* 139 */       this.plugin = null;
/* 140 */       this.scheduler = null;
/* 141 */       this.closed = true;
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void finalize() throws Throwable
/*     */   {
/* 147 */     close();
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.injector.DelayedSingleTask
 * JD-Core Version:    0.6.2
 */