/*    */ package com.comphenix.protocol.utility;
/*    */ 
/*    */ import org.bukkit.Server;
/*    */ import org.bukkit.plugin.Plugin;
/*    */ import org.bukkit.scheduler.BukkitScheduler;
/*    */ import org.bukkit.scheduler.BukkitTask;
/*    */ 
/*    */ public class WrappedScheduler
/*    */ {
/*    */   public static TaskWrapper runAsynchronouslyOnce(Plugin plugin, Runnable runnable, long firstDelay)
/*    */   {
/* 31 */     return runAsynchronouslyRepeat(plugin, plugin.getServer().getScheduler(), runnable, firstDelay, -1L);
/*    */   }
/*    */ 
/*    */   public static TaskWrapper runAsynchronouslyRepeat(Plugin plugin, Runnable runnable, long firstDelay, long repeatDelay)
/*    */   {
/* 43 */     return runAsynchronouslyRepeat(plugin, plugin.getServer().getScheduler(), runnable, firstDelay, repeatDelay);
/*    */   }
/*    */ 
/*    */   public static TaskWrapper runAsynchronouslyRepeat(Plugin plugin, BukkitScheduler scheduler, Runnable runnable, long firstDelay, long repeatDelay)
/*    */   {
/*    */     try
/*    */     {
/* 58 */       final int taskID = scheduler.scheduleAsyncRepeatingTask(plugin, runnable, firstDelay, repeatDelay);
/*    */ 
/* 61 */       return new TaskWrapper()
/*    */       {
/*    */         public void cancel() {
/* 64 */           this.val$scheduler.cancelTask(taskID);
/*    */         } } ;
/*    */     }
/*    */     catch (NoSuchMethodError e) {
/*    */     }
/* 69 */     return tryUpdatedVersion(plugin, scheduler, runnable, firstDelay, repeatDelay);
/*    */   }
/*    */ 
/*    */   private static TaskWrapper tryUpdatedVersion(Plugin plugin, BukkitScheduler scheduler, Runnable runnable, long firstDelay, long repeatDelay)
/*    */   {
/* 83 */     BukkitTask task = scheduler.runTaskTimerAsynchronously(plugin, runnable, firstDelay, repeatDelay);
/*    */ 
/* 85 */     return new TaskWrapper()
/*    */     {
/*    */       public void cancel() {
/* 88 */         this.val$task.cancel();
/*    */       }
/*    */     };
/*    */   }
/*    */ 
/*    */   public static abstract interface TaskWrapper
/*    */   {
/*    */     public abstract void cancel();
/*    */   }
/*    */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.utility.WrappedScheduler
 * JD-Core Version:    0.6.2
 */