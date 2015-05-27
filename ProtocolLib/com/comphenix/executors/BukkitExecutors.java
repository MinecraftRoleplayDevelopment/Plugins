/*    */ package com.comphenix.executors;
/*    */ 
/*    */ import com.google.common.base.Preconditions;
/*    */ import org.bukkit.Server;
/*    */ import org.bukkit.plugin.Plugin;
/*    */ import org.bukkit.scheduler.BukkitScheduler;
/*    */ import org.bukkit.scheduler.BukkitTask;
/*    */ 
/*    */ public class BukkitExecutors
/*    */ {
/*    */   public static BukkitScheduledExecutorService newSynchronous(final Plugin plugin)
/*    */   {
/* 21 */     final BukkitScheduler scheduler = getScheduler(plugin);
/* 22 */     Preconditions.checkNotNull(plugin, "plugin cannot be NULL");
/*    */ 
/* 24 */     BukkitScheduledExecutorService service = new AbstractBukkitService(new PendingTasks(plugin, scheduler))
/*    */     {
/*    */       protected BukkitTask getTask(Runnable command) {
/* 27 */         return scheduler.runTask(plugin, command);
/*    */       }
/*    */ 
/*    */       protected BukkitTask getLaterTask(Runnable task, long ticks)
/*    */       {
/* 32 */         return scheduler.runTaskLater(plugin, task, ticks);
/*    */       }
/*    */ 
/*    */       protected BukkitTask getTimerTask(long ticksInitial, long ticksDelay, Runnable task)
/*    */       {
/* 37 */         return scheduler.runTaskTimer(plugin, task, ticksInitial, ticksDelay);
/*    */       }
/*    */     };
/* 41 */     PluginDisabledListener.getListener(plugin).addService(service);
/* 42 */     return service;
/*    */   }
/*    */ 
/*    */   public static BukkitScheduledExecutorService newAsynchronous(final Plugin plugin)
/*    */   {
/* 52 */     final BukkitScheduler scheduler = getScheduler(plugin);
/* 53 */     Preconditions.checkNotNull(plugin, "plugin cannot be NULL");
/*    */ 
/* 55 */     BukkitScheduledExecutorService service = new AbstractBukkitService(new PendingTasks(plugin, scheduler))
/*    */     {
/*    */       protected BukkitTask getTask(Runnable command) {
/* 58 */         return scheduler.runTaskAsynchronously(plugin, command);
/*    */       }
/*    */ 
/*    */       protected BukkitTask getLaterTask(Runnable task, long ticks)
/*    */       {
/* 63 */         return scheduler.runTaskLaterAsynchronously(plugin, task, ticks);
/*    */       }
/*    */ 
/*    */       protected BukkitTask getTimerTask(long ticksInitial, long ticksDelay, Runnable task)
/*    */       {
/* 68 */         return scheduler.runTaskTimerAsynchronously(plugin, task, ticksInitial, ticksDelay);
/*    */       }
/*    */     };
/* 72 */     PluginDisabledListener.getListener(plugin).addService(service);
/* 73 */     return service;
/*    */   }
/*    */ 
/*    */   private static BukkitScheduler getScheduler(Plugin plugin)
/*    */   {
/* 81 */     BukkitScheduler scheduler = plugin.getServer().getScheduler();
/*    */ 
/* 83 */     if (scheduler != null) {
/* 84 */       return scheduler;
/*    */     }
/* 86 */     throw new IllegalStateException("Unable to retrieve scheduler.");
/*    */   }
/*    */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.executors.BukkitExecutors
 * JD-Core Version:    0.6.2
 */