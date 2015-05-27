/*     */ package com.zettelnet.armorweight;
/*     */ 
/*     */ import com.zettelnet.armorweight.zet.chat.ChatMessage;
/*     */ import com.zettelnet.armorweight.zet.configuration.LanguageConfigurationFile;
/*     */ import java.util.logging.Logger;
/*     */ import org.bukkit.configuration.file.FileConfiguration;
/*     */ 
/*     */ public class ArmorWeightLanguage extends LanguageConfigurationFile
/*     */ {
/*     */   private final ArmorWeightPlugin plugin;
/*     */   public ChatMessage commandErrorSyntax;
/*     */   public ChatMessage commandErrorInvalidArgument;
/*     */   public ChatMessage commandErrorInvalidArgumentType;
/*     */   public ChatMessage commandErrorMissingArgument;
/*     */   public ChatMessage commandErrorNotPlayer;
/*     */   public ChatMessage commandErrorNoPermission;
/*     */   public ChatMessage commandErrorDisabled;
/*     */   public ChatMessage commandArgumentAmount;
/*     */   public ChatMessage commandArgumentPlayer;
/*     */   public ChatMessage commandHelpCommand;
/*     */   public ChatMessage weightGetSelf;
/*     */   public ChatMessage weightGetSelfHorse;
/*     */   public ChatMessage weightGetOther;
/*     */   public ChatMessage weightGetOtherHorse;
/*     */   public ChatMessage weightHelpGet;
/*     */   public ChatMessage weightWarning;
/*     */   public ChatMessage pluginPrefix;
/*     */   public ChatMessage pluginInfo;
/*     */   public ChatMessage pluginWebsite;
/*     */   public ChatMessage pluginReload;
/*     */   public ChatMessage pluginUpdateAvailable;
/*     */   public ChatMessage pluginHelpReload;
/*     */ 
/*     */   public ArmorWeightLanguage(ArmorWeightPlugin plugin, String file, String resource)
/*     */   {
/*  40 */     super(plugin, file, resource);
/*  41 */     this.plugin = plugin;
/*     */   }
/*     */ 
/*     */   public String getLanguage()
/*     */   {
/*  46 */     return this.plugin.getConfiguration().chatLanguage();
/*     */   }
/*     */ 
/*     */   public void loadValues(FileConfiguration config, FileConfiguration defaults)
/*     */   {
/*  51 */     if (config.getBoolean("config.autoUpdate", true)) {
/*  52 */       update(config);
/*     */     }
/*     */ 
/*  55 */     this.pluginPrefix = load("plugin.prefix", "&(darkGray)[&(gray)ArmorWeight&(gray)] ");
/*  56 */     addFormatOption("prefix", this.pluginPrefix.toFormatOption(new Object[0]));
/*     */ 
/*  58 */     this.commandErrorSyntax = load("command.error.syntax", "&(red)Syntax: &(gray)/%(syntax)");
/*  59 */     this.commandErrorInvalidArgument = load("command.error.invalidArgument", "&(red)Argument \"%(arg)\" invalid");
/*  60 */     this.commandErrorInvalidArgumentType = load("command.error.invalidArgumentType", "&(red)Argument %(argType) \"%(arg)\" invalid");
/*  61 */     this.commandErrorMissingArgument = load("command.error.missingArgument", "&(red)Missing argument %(argType)");
/*  62 */     this.commandErrorNotPlayer = load("command.error.notPlayer", "&(red)Missing argument %(argType)");
/*  63 */     this.commandErrorNoPermission = load("command.error.noPermission", "&(red)The player %(player) is currently not online");
/*  64 */     this.commandErrorDisabled = load("command.error.disabled", "&(red)You don't have permission to do this");
/*     */ 
/*  66 */     this.commandArgumentAmount = load("command.argument.amount", "amount");
/*  67 */     this.commandArgumentPlayer = load("command.argument.player", "player");
/*     */ 
/*  69 */     this.commandHelpCommand = load("command.help.command", "&(white)/%(syntax)&(white) - &(gray)%(description)");
/*     */ 
/*  71 */     this.weightGetSelf = load("weight.get.self", "&(prefix)&(gray)You weigh &(gold)%(weight) &(yellow)(%(playerWeight) + %(armorWeight))&(gray)!");
/*  72 */     this.weightGetSelfHorse = load("weight.get.selfHorse", "&(prefix)&(gray)Your &(darkGray)horse &(gray)weighs &(gold)%(weight) &(yellow)(%(horseWeight) + %(armorWeight) + %(passengerWeight))&(gray)!");
/*  73 */     this.weightGetOther = load("weight.get.other", "&(prefix)&(gray)%(player) weighs &(gold)%(weight) &(yellow)(%(playerWeight) + %(armorWeight))&(gray)!");
/*  74 */     this.weightGetOtherHorse = load("weight.get.otherHorse", "&(prefix)&(gray)%(player)'s &(darkGray)horse &(gray)weighs &(gold)%(weight) &(yellow)(%(horseWeight) + %(armorWeight) + %(passengerWeight))&(gray)!");
/*  75 */     this.weightHelpGet = load("weight.help.get", "Gets the weight of a player");
/*  76 */     this.weightWarning = load("weight.warning", "&(prefix)&(gray)Equipping heavy armor weighs you down");
/*     */ 
/*  78 */     this.pluginInfo = load("plugin.info", "&(prefix)&(gray)This server is running &(darkGray)ArmorWeight&(gray) v%(version) by %(creator)!");
/*  79 */     this.pluginWebsite = load("plugin.website", "&(darkGray)Website: &(gray)%(website)");
/*  80 */     this.pluginReload = load("plugin.reload", "&(prefix)&(white)Reloaded configurations &(...)");
/*  81 */     this.pluginUpdateAvailable = load("plugin.updateAvailable", "&(prefix)&(gray)An update is available &(white)(%(updateName) for %(updateGameVersion))&(gray)!&(br)&(white)Download it at &(gray)%(updateLink)&(white).");
/*  82 */     this.pluginHelpReload = load("plugin.help.reload", "Reloads the plugin configurations");
/*     */   }
/*     */ 
/*     */   public void update(FileConfiguration config)
/*     */   {
/*  87 */     switch (getVersion()) {
/*     */     default:
/*  89 */       this.plugin.getLogger().warning("Unknown version of configuration file \"lang.yml\". Will not update file");
/*  90 */       break;
/*     */     case "unknown":
/*     */     case "0.1.0":
/*  93 */       updateStart("0.1.0");
/*  94 */       setIfNotExists("enUS.weight.get.selfHorse", "&(prefix)&(gray)Your &(darkGray)horse &(gray)weighs &(gold)%(weight) &(yellow)(%(horseWeight) + %(armorWeight) + %(passengerWeight))&(gray)!");
/*  95 */       setIfNotExists("enUS.weight.get.otherHorse", "&(prefix)&(gray)%(player)'s &(darkGray)horse &(gray)weighs &(gold)%(weight) &(yellow)(%(horseWeight) + %(armorWeight) + %(passengerWeight))&(gray)!");
/*     */     case "0.2.0":
/*     */     case "0.2.1":
/*     */     case "0.3.0":
/*     */     case "0.3.1":
/*     */     case "0.3.2":
/* 101 */       updateStart("0.3.2");
/* 102 */       setIfNotExists("enUS.weight.warning", "&(prefix)&(gray)Wearing heavy armor will cause you to slow down!");
/*     */     case "0.3.3":
/*     */     case "0.3.4":
/*     */     case "0.3.5":
/* 106 */       updateDone("0.3.5");
/*     */     }
/*     */   }
/*     */ 
/*     */   public ChatMessage pluginPrefix() {
/* 111 */     return this.pluginPrefix;
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\ArmorWeight.jar
 * Qualified Name:     com.zettelnet.armorweight.ArmorWeightLanguage
 * JD-Core Version:    0.6.2
 */