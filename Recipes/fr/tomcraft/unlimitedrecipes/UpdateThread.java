/*    */ package fr.tomcraft.unlimitedrecipes;
/*    */ 
/*    */ import org.bukkit.Bukkit;
/*    */ import org.bukkit.ChatColor;
/*    */ import org.bukkit.command.ConsoleCommandSender;
/*    */ import org.bukkit.scheduler.BukkitScheduler;
/*    */ import org.bukkit.scheduler.BukkitTask;
/*    */ 
/*    */ public class UpdateThread
/*    */   implements Runnable
/*    */ {
/* 12 */   public static boolean updateChecking = true;
/* 13 */   public static boolean updateDownloading = false;
/* 14 */   public static boolean updateAvailable = false;
/*    */   private static BukkitTask task;
/*    */ 
/*    */   public static void start()
/*    */   {
/* 19 */     if ((updateChecking) && (task == null))
/*    */     {
/* 21 */       task = Bukkit.getScheduler().runTaskTimerAsynchronously(URPlugin.instance, new UpdateThread(), 0L, 864000L);
/*    */     }
/*    */   }
/*    */ 
/*    */   public static void stop()
/*    */   {
/* 27 */     if (task != null)
/*    */     {
/* 29 */       task.cancel();
/*    */     }
/*    */   }
/*    */ 
/*    */   public static void restart()
/*    */   {
/* 35 */     stop();
/* 36 */     start();
/*    */   }
/*    */ 
/*    */   public static boolean checkUpdate()
/*    */   {
/* 41 */     URPlugin.renewUpdater();
/* 42 */     if (URPlugin.updater.getResult() == Updater.UpdateResult.UPDATE_AVAILABLE)
/*    */     {
/* 44 */       Bukkit.getConsoleSender().sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "[UnlimitedRecipes] " + ChatColor.RESET + ChatColor.RED + "An update is available," + (updateDownloading ? " it will be applied on next restart." : " you can get it here: "));
/* 45 */       Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "http://dev.bukkit.org/bukkit-plugins/unlimitedrecipes/");
/* 46 */       return true;
/*    */     }
/* 48 */     return false;
/*    */   }
/*    */ 
/*    */   public void run()
/*    */   {
/* 54 */     updateAvailable = checkUpdate();
/*    */   }
/*    */ }

/* Location:           D:\Github\Mechanics\UnlimitedRecipes.jar
 * Qualified Name:     fr.tomcraft.unlimitedrecipes.UpdateThread
 * JD-Core Version:    0.6.2
 */