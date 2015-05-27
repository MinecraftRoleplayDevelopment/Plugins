/*     */ package com.gmail.filoghost.healthbar;
/*     */ 
/*     */ import com.gmail.filoghost.healthbar.metrics.MetricsLite;
/*     */ import com.gmail.filoghost.healthbar.utils.Debug;
/*     */ import com.gmail.filoghost.healthbar.utils.PlayerBarUtils;
/*     */ import com.gmail.filoghost.healthbar.utils.Utils;
/*     */ import java.io.File;
/*     */ import java.io.PrintStream;
/*     */ import java.util.logging.Logger;
/*     */ import org.bukkit.Bukkit;
/*     */ import org.bukkit.ChatColor;
/*     */ import org.bukkit.Server;
/*     */ import org.bukkit.command.PluginCommand;
/*     */ import org.bukkit.configuration.file.FileConfiguration;
/*     */ import org.bukkit.plugin.PluginManager;
/*     */ import org.bukkit.plugin.java.JavaPlugin;
/*     */ import org.bukkit.scoreboard.ScoreboardManager;
/*     */ 
/*     */ public class Main extends JavaPlugin
/*     */ {
/*     */   public static Main plugin;
/*     */   public static Logger logger;
/*     */   private static DamageListener damageListener;
/*     */   private static DeathListener deathListener;
/*     */   private static MiscListeners miscListeners;
/*     */ 
/*     */   public void onEnable()
/*     */   {
/*  29 */     plugin = this;
/*  30 */     logger = getLogger();
/*     */     try
/*     */     {
/*  34 */       String build = Utils.getBukkitBuild();
/*  35 */       if ((build != null) && 
/*  36 */         (Integer.parseInt(build) < 2811)) {
/*  37 */         logger.warning("------------------------------------------");
/*  38 */         logger.warning("Your bukkit build (#" + build + ") is old.");
/*  39 */         logger.warning("HealthBar cannot work properly,");
/*  40 */         logger.warning("please update CraftBukkit.");
/*  41 */         logger.warning("------------------------------------------");
/*  42 */         setEnabled(false);
/*  43 */         return;
/*     */       }
/*     */ 
/*     */     }
/*     */     catch (Exception localException)
/*     */     {
/*  49 */       damageListener = new DamageListener();
/*  50 */       deathListener = new DeathListener();
/*  51 */       miscListeners = new MiscListeners();
/*     */ 
/*  54 */       Debug.color("§c[HealthBar] Debug ON");
/*     */ 
/*  57 */       if (getDataFolder().exists()) {
/*  58 */         getDataFolder().mkdir();
/*     */       }
/*  60 */       Utils.loadFile("config.yml", this);
/*     */ 
/*  63 */       getServer().getPluginManager().registerEvents(damageListener, this);
/*  64 */       getServer().getPluginManager().registerEvents(deathListener, this);
/*  65 */       getServer().getPluginManager().registerEvents(miscListeners, this);
/*     */ 
/*  68 */       reloadConfigFromDisk();
/*  69 */       FileConfiguration config = getConfig();
/*     */ 
/*  73 */       Updater.UpdaterHandler.setup(this, 54447, "§2[§aHealthBar§2] ", super.getFile(), ChatColor.GREEN, "/hbr update", "health-bar");
/*     */ 
/*  75 */       if (config.getBoolean("update-notification")) {
/*  76 */         Thread updaterThread = new Thread(new Runnable() {
/*  77 */           public void run() { Updater.UpdaterHandler.startupUpdateCheck(); }
/*     */ 
/*     */         });
/*  80 */         updaterThread.start();
/*     */       }
/*     */ 
/*  85 */       getCommand("healthbar").setExecutor(new Commands(this));
/*     */       try
/*     */       {
/*  89 */         MetricsLite metrics = new MetricsLite(this);
/*  90 */         metrics.start();
/*     */       }
/*     */       catch (Exception localException1)
/*     */       {
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void onDisable() {
/*  99 */     PlayerBarUtils.removeAllHealthbarTeams(Bukkit.getScoreboardManager().getMainScoreboard());
/* 100 */     PlayerBar.removeBelowObj();
/* 101 */     DamageListener.removeAllMobHealthBars();
/* 102 */     System.out.println("HealthBar disabled, all the health bars have been removed.");
/*     */   }
/*     */ 
/*     */   public void reloadConfigFromDisk()
/*     */   {
/* 108 */     reloadConfig();
/*     */ 
/* 110 */     Configuration.checkConfigYML();
/*     */ 
/* 112 */     Utils.loadFile("custom-mob-bar.yml", this);
/* 113 */     Utils.loadFile("custom-player-bar.yml", this);
/* 114 */     Utils.loadFile("locale.yml", this);
/* 115 */     Utils.loadFile("config.yml", this);
/*     */ 
/* 118 */     Utils.getTranslationMap(this);
/*     */ 
/* 120 */     DamageListener.loadConfiguration();
/* 121 */     DeathListener.loadConfiguration();
/* 122 */     PlayerBar.loadConfiguration();
/* 123 */     MiscListeners.loadConfiguration();
/*     */   }
/*     */ 
/*     */   public static MiscListeners getLoginListenerInstance() {
/* 127 */     return miscListeners;
/*     */   }
/*     */ 
/*     */   public static File getPluginFile() {
/* 131 */     return plugin.getFile();
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\HealthBar.jar
 * Qualified Name:     com.gmail.filoghost.healthbar.Main
 * JD-Core Version:    0.6.2
 */