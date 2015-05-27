/*     */ package com.zettelnet.armorweight;
/*     */ 
/*     */ import com.zettelnet.armorweight.lib.org.mcstats.Metrics;
/*     */ import java.io.IOException;
/*     */ import java.util.logging.Logger;
/*     */ import org.bukkit.Server;
/*     */ import org.bukkit.entity.Player;
/*     */ import org.bukkit.event.HandlerList;
/*     */ import org.bukkit.plugin.PluginDescriptionFile;
/*     */ import org.bukkit.plugin.PluginManager;
/*     */ import org.bukkit.plugin.java.JavaPlugin;
/*     */ 
/*     */ public class ArmorWeightPlugin extends JavaPlugin
/*     */ {
/*     */   private Logger log;
/*     */   private ArmorWeightConfiguration config;
/*     */   private ArmorWeightLanguage lang;
/*     */   private ArmorWeightCommands commands;
/*     */   private WeightManager weightManager;
/*     */   private WeightListener listener;
/*     */   private WeightTracker tracker;
/*     */ 
/*     */   public void onEnable()
/*     */   {
/*  41 */     this.log = getLogger();
/*  42 */     this.weightManager = new WeightManager(this, this.log);
/*     */ 
/*  44 */     this.config = new ArmorWeightConfiguration(this, "config.yml", "config.yml", this.weightManager);
/*  45 */     this.config.load();
/*  46 */     this.lang = new ArmorWeightLanguage(this, "lang.yml", "lang.yml");
/*  47 */     this.lang.loadDefaults();
/*  48 */     this.lang.load();
/*     */ 
/*  50 */     this.weightManager.setPortableHorsesEnabled(isPortableHorsesFound());
/*  51 */     this.weightManager.initialize();
/*     */ 
/*  53 */     this.listener = new WeightListener(this.weightManager);
/*  54 */     getServer().getPluginManager().registerEvents(this.listener, this);
/*     */ 
/*  56 */     this.commands = new ArmorWeightCommands(this);
/*     */ 
/*  58 */     this.tracker = new WeightTracker(this, this.weightManager);
/*  59 */     this.tracker.register();
/*     */     try
/*     */     {
/*  62 */       if (this.config.metricsEnabled()) {
/*  63 */         Metrics metrics = new Metrics(this);
/*  64 */         metrics.start();
/*     */       }
/*     */     } catch (IOException e) {
/*  67 */       this.log.warning("Failed to connect to mcstats.org. If you are furthermore keep getting this error, try disabling metrics in config.yml.");
/*     */     }
/*     */ 
/*  70 */     for (Player player : getServer().getOnlinePlayers()) {
/*  71 */       if (this.weightManager.isWorldEnabled(player.getWorld())) {
/*  72 */         this.weightManager.loadPlayer(player);
/*     */       }
/*     */     }
/*     */ 
/*  76 */     this.log.info("Enabled successfully.");
/*     */   }
/*     */ 
/*     */   private boolean isPortableHorsesFound() {
/*  80 */     return getServer().getPluginManager().getPlugin("PortableHorses") != null;
/*     */   }
/*     */ 
/*     */   public void onDisable()
/*     */   {
/*  85 */     HandlerList.unregisterAll(this);
/*     */ 
/*  87 */     for (Player player : getServer().getOnlinePlayers()) {
/*  88 */       this.weightManager.unloadPlayer(player);
/*     */     }
/*     */ 
/*  91 */     this.log.info("Disabled successfully.");
/*     */   }
/*     */ 
/*     */   public ArmorWeightConfiguration getConfiguration() {
/*  95 */     return this.config;
/*     */   }
/*     */ 
/*     */   public WeightManager getWeightManager() {
/*  99 */     return this.weightManager;
/*     */   }
/*     */ 
/*     */   public ArmorWeightCommands getCommandExecutor() {
/* 103 */     return this.commands;
/*     */   }
/*     */ 
/*     */   public ArmorWeightLanguage getLanguage() {
/* 107 */     return this.lang;
/*     */   }
/*     */ 
/*     */   public void reload() {
/* 111 */     onDisable();
/* 112 */     onEnable();
/*     */   }
/*     */ 
/*     */   public String getWebsite() {
/* 116 */     return getDescription().getWebsite();
/*     */   }
/*     */ 
/*     */   public String getVersion() {
/* 120 */     return getDescription().getVersion();
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\ArmorWeight.jar
 * Qualified Name:     com.zettelnet.armorweight.ArmorWeightPlugin
 * JD-Core Version:    0.6.2
 */