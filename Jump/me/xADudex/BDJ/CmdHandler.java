/*     */ package me.xADudex.BDJ;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import org.bukkit.Bukkit;
/*     */ import org.bukkit.ChatColor;
/*     */ import org.bukkit.command.Command;
/*     */ import org.bukkit.command.CommandExecutor;
/*     */ import org.bukkit.command.CommandSender;
/*     */ import org.bukkit.entity.Player;
/*     */ import org.bukkit.plugin.PluginDescriptionFile;
/*     */ 
/*     */ public class CmdHandler
/*     */   implements CommandExecutor
/*     */ {
/*  12 */   static String a = ChatColor.AQUA;
/*  13 */   static String g = ChatColor.GOLD;
/*  14 */   static String pref = g + "[" + a + "BetterDoubleJump" + g + "] " + a;
/*  15 */   static String noPermMSG = pref + ChatColor.DARK_RED + "You don't have permission to use that command!";
/*  16 */   static String notPlayer = pref + "You must be a player to use that command";
/*     */ 
/*     */   public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
/*  19 */     if (args.length == 0) {
/*  20 */       boolean hasPermForCommand = false;
/*  21 */       sender.sendMessage(g + ChatColor.STRIKETHROUGH + "                          " + 
/*  22 */         a + " BetterDoubleJump " + g + ChatColor.STRIKETHROUGH + "                          ");
/*  23 */       sender.sendMessage(g + "Author: " + a + "xADudex aka xGamingDudex");
/*  24 */       sender.sendMessage(g + "Version: " + a + Main.pl.getDescription().getVersion());
/*  25 */       sender.sendMessage(g + "Avalible Commands:");
/*  26 */       if (Main.hasAdminPerm(sender)) {
/*  27 */         sender.sendMessage(g + " - " + a + "reload " + g + "- " + a + "Reload the config file");
/*  28 */         hasPermForCommand = true;
/*     */       }
/*  30 */       if (Main.hasDisableOtherPerm(sender)) {
/*  31 */         sender.sendMessage(g + " - " + a + "disable [player] " + g + "- " + a + "Disable double jump");
/*  32 */         hasPermForCommand = true;
/*  33 */       } else if (Main.hasDisablePerm(sender)) {
/*  34 */         sender.sendMessage(g + " - " + a + "disable " + g + "- " + a + "Disable double jump");
/*  35 */         hasPermForCommand = true;
/*     */       }
/*  37 */       if (!hasPermForCommand)
/*  38 */         sender.sendMessage(g + " -" + a + " none");
/*     */     }
/*  40 */     else if ((args[0].equalsIgnoreCase("reload")) || (args[0].equalsIgnoreCase("rl"))) {
/*  41 */       if (Main.hasAdminPerm(sender)) {
/*  42 */         Main.pl.reloadConfig();
/*  43 */         Main.pl.reloadConfigFile();
/*  44 */         sender.sendMessage(pref + "Config successfully reloaded");
/*     */       } else {
/*  46 */         sender.sendMessage(noPermMSG);
/*     */       }
/*  48 */     } else if (args[0].equalsIgnoreCase("disable")) {
/*  49 */       if ((args.length >= 2) && (Main.hasDisableOtherPerm(sender))) {
/*  50 */         Player p = getPlayer(args[1]);
/*  51 */         if (p != null) {
/*  52 */           String state = toggleDisable(p) ? "Disabled" : "Enabled";
/*  53 */           sender.sendMessage(pref + "Double jump has been " + g + state + a + " for " + g + p.getName());
/*     */         } else {
/*  55 */           sender.sendMessage(pref + "Could not find any players by that name!");
/*     */         }
/*  57 */       } else if (Main.hasDisablePerm(sender)) {
/*  58 */         if ((sender instanceof Player)) {
/*  59 */           Player p = (Player)sender;
/*  60 */           String state = toggleDisable(p) ? "Disabled" : "Enabled";
/*  61 */           sender.sendMessage(pref + "You have " + g + state + a + " double jump");
/*     */         } else {
/*  63 */           sender.sendMessage(notPlayer);
/*     */         }
/*     */       } else {
/*  66 */         sender.sendMessage(noPermMSG);
/*     */       }
/*     */     } else {
/*  69 */       sender.sendMessage(pref + "Unknown command");
/*     */     }
/*  71 */     return false;
/*     */   }
/*     */ 
/*     */   static boolean toggleDisable(Player p) {
/*  75 */     if (!Main.disabled.remove(p.getUniqueId())) {
/*  76 */       Main.disabled.add(p.getUniqueId());
/*  77 */       p.setAllowFlight(false);
/*  78 */       return true;
/*     */     }
/*  80 */     p.setAllowFlight(true);
/*  81 */     return false;
/*     */   }
/*     */ 
/*     */   static Player getPlayer(String name) {
/*  85 */     name = name.toLowerCase();
/*  86 */     int off = -1;
/*  87 */     Player found = null;
/*  88 */     for (Player p : Bukkit.getOnlinePlayers()) {
/*  89 */       String pn = p.getName().toLowerCase();
/*  90 */       if (pn.equals(name))
/*  91 */         return p;
/*  92 */       if (pn.startsWith(name)) {
/*  93 */         int poff = name.length() - pn.length();
/*  94 */         if ((poff < off) || (off == -1)) {
/*  95 */           found = p;
/*  96 */           off = poff;
/*     */         }
/*     */       }
/*     */     }
/* 100 */     return found;
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\BetterDoubleJump.jar
 * Qualified Name:     me.xADudex.BDJ.CmdHandler
 * JD-Core Version:    0.6.2
 */