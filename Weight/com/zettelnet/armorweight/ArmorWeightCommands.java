/*     */ package com.zettelnet.armorweight;
/*     */ 
/*     */ import com.zettelnet.armorweight.zet.chat.ChatMessage;
/*     */ import org.bukkit.Bukkit;
/*     */ import org.bukkit.command.Command;
/*     */ import org.bukkit.command.CommandExecutor;
/*     */ import org.bukkit.command.CommandSender;
/*     */ import org.bukkit.command.PluginCommand;
/*     */ import org.bukkit.entity.Horse;
/*     */ import org.bukkit.entity.Player;
/*     */ 
/*     */ public class ArmorWeightCommands
/*     */   implements CommandExecutor
/*     */ {
/*     */   private final ArmorWeightPlugin plugin;
/*     */ 
/*     */   public ArmorWeightCommands(ArmorWeightPlugin plugin)
/*     */   {
/*  15 */     this.plugin = plugin;
/*     */ 
/*  17 */     plugin.getCommand("weight").setExecutor(this);
/*  18 */     plugin.getCommand("armorweight").setExecutor(this);
/*     */   }
/*     */ 
/*     */   public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
/*     */   {
/*  23 */     ArmorWeightLanguage lang = this.plugin.getLanguage();
/*     */ 
/*  25 */     switch (command.getName().toLowerCase()) {
/*     */     case "weight":
/*  27 */       if (!sender.hasPermission("armorweight.command.weight.getown")) {
/*  28 */         lang.commandErrorNoPermission.send(sender, new Object[0]);
/*  29 */         return true;
/*     */       }
/*  31 */       if (args.length == 0) {
/*  32 */         if (!(sender instanceof Player)) {
/*  33 */           lang.commandErrorMissingArgument.send(sender, new Object[] { "argType", lang.commandArgumentPlayer });
/*  34 */           lang.commandErrorSyntax.send(sender, new Object[] { "syntax", "weight <" + lang.commandArgumentPlayer + ">" });
/*  35 */           return true;
/*     */         }
/*  37 */         Player p = (Player)sender;
/*  38 */         WeightManager wm = this.plugin.getWeightManager();
/*  39 */         if (!(p.getVehicle() instanceof Horse)) {
/*  40 */           lang.weightGetSelf.send(sender, new Object[] { "player", p.getName(), "weight", wm.formatWeight(wm.getWeight(p)), "playerWeight", wm.formatWeight(wm.getPlayerWeight(p)), "armorWeight", wm.formatWeight(wm.getArmorWeight(p)) });
/*     */         } else {
/*  42 */           Horse h = (Horse)p.getVehicle();
/*  43 */           lang.weightGetSelfHorse.send(sender, new Object[] { "player", p.getName(), "weight", wm.formatWeight(wm.getWeight(h)), "horseWeight", wm.formatWeight(wm.getHorseWeight(h)), "passengerWeight", wm.formatWeight(wm.isHorsePassengerWeightEnabled() ? wm.getWeight(p) : 0.0D), "armorWeight", wm.formatWeight(wm.getArmorWeight(h)) });
/*     */         }
/*  45 */         return true;
/*     */       }
/*  47 */       if (args[0].equalsIgnoreCase("help")) {
/*  48 */         if ((sender instanceof Player))
/*  49 */           lang.commandHelpCommand.send(sender, new Object[] { "syntax", "weight [" + lang.commandArgumentPlayer + "]", "description", lang.weightHelpGet });
/*     */         else {
/*  51 */           lang.commandHelpCommand.send(sender, new Object[] { "syntax", "weight <" + lang.commandArgumentPlayer + ">", "description", lang.weightHelpGet });
/*     */         }
/*  53 */         return true;
/*     */       }
/*  55 */       if (!sender.hasPermission("armorweight.command.weight.getothers")) {
/*  56 */         lang.commandErrorNoPermission.send(sender, new Object[0]);
/*  57 */         return true;
/*     */       }
/*  59 */       Player p = Bukkit.getPlayer(args[0]);
/*  60 */       if (p == null) {
/*  61 */         lang.commandErrorNotPlayer.send(sender, new Object[] { "player", args[0] });
/*  62 */         if ((sender instanceof Player))
/*  63 */           lang.commandErrorSyntax.send(sender, new Object[] { "syntax", "weight [" + lang.commandArgumentPlayer + "]" });
/*     */         else {
/*  65 */           lang.commandErrorSyntax.send(sender, new Object[] { "syntax", "weight <" + lang.commandArgumentPlayer + ">" });
/*     */         }
/*  67 */         return true;
/*     */       }
/*  69 */       WeightManager wm = this.plugin.getWeightManager();
/*  70 */       if (!(p.getVehicle() instanceof Horse)) {
/*  71 */         lang.weightGetOther.send(sender, new Object[] { "player", p.getName(), "weight", wm.formatWeight(wm.getWeight(p)), "playerWeight", wm.formatWeight(wm.getPlayerWeight(p)), "armorWeight", wm.formatWeight(wm.getArmorWeight(p)) });
/*     */       } else {
/*  73 */         ??? = (Horse)p.getVehicle();
/*  74 */         lang.weightGetOtherHorse.send(sender, new Object[] { "player", p.getName(), "weight", wm.formatWeight(wm.getWeight(???)), "horseWeight", wm.formatWeight(wm.getHorseWeight(???)), "passengerWeight", wm.formatWeight(wm.isHorsePassengerWeightEnabled() ? wm.getWeight(p) : 0.0D), "armorWeight", wm.formatWeight(wm.getArmorWeight(???)) });
/*     */       }
/*  76 */       return true;
/*     */     case "armorweight":
/*  78 */       if (!sender.hasPermission("armorweight.command.plugin.info")) {
/*  79 */         lang.commandErrorNoPermission.send(sender, new Object[0]);
/*  80 */         return true;
/*     */       }
/*  82 */       if (args.length == 0) {
/*  83 */         lang.pluginInfo.send(sender, new Object[] { "version", this.plugin.getVersion(), "creator", "Zettelkasten" });
/*  84 */         lang.pluginWebsite.send(sender, new Object[] { "website", this.plugin.getWebsite() });
/*  85 */         return true;
/*     */       }
/*  87 */       switch (args[0].toLowerCase()) {
/*     */       case "help":
/*  89 */         if (sender.hasPermission("armorweight.command.weight.getown")) {
/*  90 */           if ((sender instanceof Player))
/*  91 */             lang.commandHelpCommand.send(sender, new Object[] { "syntax", "weight [" + lang.commandArgumentPlayer + "]", "description", lang.weightHelpGet });
/*     */           else {
/*  93 */             lang.commandHelpCommand.send(sender, new Object[] { "syntax", "weight <" + lang.commandArgumentPlayer + ">", "description", lang.weightHelpGet });
/*     */           }
/*     */         }
/*  96 */         if (sender.hasPermission("armorweight.command.plugin.reload")) {
/*  97 */           lang.commandHelpCommand.send(sender, new Object[] { "syntax", "armorweight reload", "description", lang.pluginHelpReload });
/*     */         }
/*  99 */         return true;
/*     */       case "reload":
/*     */       case "rl":
/* 102 */         if (!sender.hasPermission("armorweight.command.plugin.reload")) {
/* 103 */           lang.commandErrorNoPermission.send(sender, new Object[0]);
/* 104 */           return true;
/*     */         }
/* 106 */         this.plugin.reload();
/* 107 */         lang.pluginReload.send(sender, new Object[0]);
/* 108 */         return true;
/*     */       }
/* 110 */       lang.commandErrorInvalidArgument.send(sender, new Object[] { "arg", args[0] });
/* 111 */       lang.commandErrorSyntax.send(sender, new Object[] { "syntax", "armorweight help" });
/* 112 */       return true;
/*     */     }
/*     */ 
/* 115 */     return true;
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\ArmorWeight.jar
 * Qualified Name:     com.zettelnet.armorweight.ArmorWeightCommands
 * JD-Core Version:    0.6.2
 */