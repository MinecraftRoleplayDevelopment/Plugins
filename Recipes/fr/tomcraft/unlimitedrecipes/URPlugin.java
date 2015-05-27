/*    */ package fr.tomcraft.unlimitedrecipes;
/*    */ 
/*    */ import org.bukkit.Bukkit;
/*    */ import org.bukkit.ChatColor;
/*    */ import org.bukkit.command.Command;
/*    */ import org.bukkit.command.CommandSender;
/*    */ import org.bukkit.configuration.file.FileConfiguration;
/*    */ import org.bukkit.plugin.PluginManager;
/*    */ import org.bukkit.plugin.java.JavaPlugin;
/*    */ 
/*    */ public class URPlugin extends JavaPlugin
/*    */ {
/*    */   public static URPlugin instance;
/*    */   public static Updater updater;
/*    */ 
/*    */   public void onEnable()
/*    */   {
/* 22 */     instance = this;
/* 23 */     reloadConfig();
/* 24 */     Bukkit.getPluginManager().registerEvents(new RecipesListener(), this);
/*    */   }
/*    */ 
/*    */   public FileConfiguration getCraftingConfig()
/*    */   {
/* 29 */     return Config.crafting;
/*    */   }
/*    */ 
/*    */   public FileConfiguration getFurnaceConfig()
/*    */   {
/* 34 */     return Config.furnace;
/*    */   }
/*    */ 
/*    */   public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
/*    */   {
/* 40 */     if (hasPermission(sender, "ur.reload"))
/*    */     {
/* 42 */       if (label.equalsIgnoreCase("ur"))
/*    */       {
/* 44 */         if ((args.length == 0) || (!args[0].equalsIgnoreCase("reload")))
/*    */         {
/* 46 */           sender.sendMessage(ChatColor.RED + "Usage: /ur reload");
/* 47 */           return false;
/*    */         }
/* 49 */         reloadConfig();
/*    */       }
/*    */     }
/* 52 */     return true;
/*    */   }
/*    */ 
/*    */   public void reloadConfig()
/*    */   {
/* 57 */     super.reloadConfig();
/* 58 */     RecipesManager.reset();
/* 59 */     Config.load();
/* 60 */     UpdateThread.restart();
/*    */   }
/*    */ 
/*    */   public static boolean hasPermission(CommandSender sender, String perm)
/*    */   {
/* 65 */     return (sender.hasPermission(perm)) || (sender.isOp());
/*    */   }
/*    */ 
/*    */   public static void renewUpdater()
/*    */   {
/* 70 */     updater = new Updater(instance, 52907, instance.getFile(), UpdateThread.updateDownloading ? Updater.UpdateType.DEFAULT : Updater.UpdateType.NO_DOWNLOAD, false);
/*    */   }
/*    */ }

/* Location:           D:\Github\Mechanics\UnlimitedRecipes.jar
 * Qualified Name:     fr.tomcraft.unlimitedrecipes.URPlugin
 * JD-Core Version:    0.6.2
 */