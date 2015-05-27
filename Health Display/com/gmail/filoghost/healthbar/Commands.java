/*    */ package com.gmail.filoghost.healthbar;
/*    */ 
/*    */ import org.bukkit.command.Command;
/*    */ import org.bukkit.command.CommandExecutor;
/*    */ import org.bukkit.command.CommandSender;
/*    */ import org.bukkit.plugin.PluginDescriptionFile;
/*    */ 
/*    */ public class Commands
/*    */   implements CommandExecutor
/*    */ {
/*    */   public Main instance;
/*    */   private static final String PREFIX = "§2[§aHealthBar§2] ";
/*    */ 
/*    */   public Commands(Main main)
/*    */   {
/* 13 */     this.instance = main;
/*    */   }
/*    */ 
/*    */   public boolean onCommand(final CommandSender sender, Command cmd, String label, String[] args)
/*    */   {
/* 18 */     if (args.length == 0) {
/* 19 */       sendInfo(sender);
/* 20 */       return true;
/*    */     }
/*    */ 
/* 23 */     if (args[0].equalsIgnoreCase("help")) {
/* 24 */       sendCommandList(sender);
/* 25 */       return true;
/*    */     }
/*    */ 
/* 28 */     if (args[0].equalsIgnoreCase("reload")) {
/* 29 */       reloadConfigs(sender);
/* 30 */       return true;
/*    */     }
/*    */ 
/* 33 */     if (args[0].equalsIgnoreCase("update"))
/*    */     {
/* 35 */       if (!sender.hasPermission("healthbar.update")) {
/* 36 */         noPermissionMessage(sender);
/* 37 */         return true;
/*    */       }
/*    */ 
/* 40 */       Thread updaterThread = new Thread(new Runnable() {
/* 41 */         public void run() { Updater.UpdaterHandler.manuallyCheckUpdates(sender); }
/*    */ 
/*    */       });
/* 43 */       updaterThread.start();
/*    */ 
/* 45 */       return true;
/*    */     }
/*    */ 
/* 48 */     sender.sendMessage("§2[§aHealthBar§2] §eUnknown command. Type §a" + label + " §efor help.");
/* 49 */     return true;
/*    */   }
/*    */ 
/*    */   private void reloadConfigs(CommandSender sender)
/*    */   {
/* 54 */     if (!sender.hasPermission("healthbar.reload")) {
/* 55 */       noPermissionMessage(sender);
/* 56 */       return;
/*    */     }
/*    */     try {
/* 59 */       this.instance.reloadConfigFromDisk();
/* 60 */       sender.sendMessage("§e>>§6 HealthBar reloaded");
/*    */     }
/*    */     catch (Exception e) {
/* 63 */       e.printStackTrace();
/* 64 */       sender.sendMessage("§cFailed to reload configs, take a look at the console!");
/*    */     }
/*    */   }
/*    */ 
/*    */   private void sendInfo(CommandSender sender)
/*    */   {
/* 70 */     sender.sendMessage("§2[§aHealthBar§2] ");
/* 71 */     sender.sendMessage("§aVersion: §7" + this.instance.getDescription().getVersion());
/* 72 */     sender.sendMessage("§aDeveloper: §7filoghost");
/* 73 */     sender.sendMessage("§aCommands: §7/hbr help");
/*    */   }
/*    */ 
/*    */   private void sendCommandList(CommandSender sender) {
/* 77 */     if (!sender.hasPermission("healthbar.help")) {
/* 78 */       noPermissionMessage(sender);
/* 79 */       return;
/*    */     }
/* 81 */     sender.sendMessage("§e>>§6 HealthBar commands: ");
/* 82 */     sender.sendMessage("§2/hbr §7- §aDisplays general plugin info");
/* 83 */     sender.sendMessage("§2/hbr reload §7- §aReloads the configs");
/* 84 */     sender.sendMessage("§2/hbr update §7- §aChecks for updates");
/*    */   }
/*    */ 
/*    */   private void noPermissionMessage(CommandSender sender) {
/* 88 */     sender.sendMessage("§cYou don't have permission.");
/*    */   }
/*    */ }

/* Location:           D:\Github\Mechanics\HealthBar.jar
 * Qualified Name:     com.gmail.filoghost.healthbar.Commands
 * JD-Core Version:    0.6.2
 */