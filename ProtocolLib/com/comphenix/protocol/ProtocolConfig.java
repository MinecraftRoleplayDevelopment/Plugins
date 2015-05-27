/*     */ package com.comphenix.protocol;
/*     */ 
/*     */ import com.comphenix.protocol.injector.PacketFilterManager.PlayerInjectHooks;
/*     */ import com.google.common.base.Charsets;
/*     */ import com.google.common.collect.ImmutableList;
/*     */ import com.google.common.collect.Lists;
/*     */ import com.google.common.io.Files;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.util.List;
/*     */ import java.util.logging.Logger;
/*     */ import org.bukkit.configuration.Configuration;
/*     */ import org.bukkit.configuration.ConfigurationOptions;
/*     */ import org.bukkit.configuration.ConfigurationSection;
/*     */ import org.bukkit.plugin.Plugin;
/*     */ 
/*     */ public class ProtocolConfig
/*     */ {
/*     */   private static final String LAST_UPDATE_FILE = "lastupdate";
/*     */   private static final String SECTION_GLOBAL = "global";
/*     */   private static final String SECTION_AUTOUPDATER = "auto updater";
/*     */   private static final String METRICS_ENABLED = "metrics";
/*     */   private static final String IGNORE_VERSION_CHECK = "ignore version check";
/*     */   private static final String BACKGROUND_COMPILER_ENABLED = "background compiler";
/*     */   private static final String DEBUG_MODE_ENABLED = "debug";
/*     */   private static final String DETAILED_ERROR = "detailed error";
/*     */   private static final String INJECTION_METHOD = "injection method";
/*     */   private static final String SCRIPT_ENGINE_NAME = "script engine";
/*     */   private static final String SUPPRESSED_REPORTS = "suppressed reports";
/*     */   private static final String UPDATER_NOTIFY = "notify";
/*     */   private static final String UPDATER_DOWNLAD = "download";
/*     */   private static final String UPDATER_DELAY = "delay";
/*     */   private static final long DEFAULT_UPDATER_DELAY = 43200L;
/*     */   private Plugin plugin;
/*     */   private Configuration config;
/*     */   private boolean loadingSections;
/*     */   private ConfigurationSection global;
/*     */   private ConfigurationSection updater;
/*     */   private long lastUpdateTime;
/*     */   private boolean configChanged;
/*     */   private boolean valuesChanged;
/*     */   private int modCount;
/*     */ 
/*     */   public ProtocolConfig(Plugin plugin)
/*     */   {
/*  80 */     this.plugin = plugin;
/*  81 */     reloadConfig();
/*     */   }
/*     */ 
/*     */   public void reloadConfig()
/*     */   {
/*  89 */     this.configChanged = false;
/*  90 */     this.valuesChanged = false;
/*  91 */     this.modCount += 1;
/*     */ 
/*  93 */     this.config = this.plugin.getConfig();
/*  94 */     this.lastUpdateTime = loadLastUpdate();
/*  95 */     loadSections(!this.loadingSections);
/*     */   }
/*     */ 
/*     */   private long loadLastUpdate()
/*     */   {
/* 103 */     File dataFile = getLastUpdateFile();
/*     */ 
/* 105 */     if (dataFile.exists()) {
/*     */       try {
/* 107 */         return Long.parseLong(Files.toString(dataFile, Charsets.UTF_8));
/*     */       } catch (NumberFormatException e) {
/* 109 */         this.plugin.getLogger().warning("Cannot parse " + dataFile + " as a number.");
/*     */       } catch (IOException e) {
/* 111 */         this.plugin.getLogger().warning("Cannot read " + dataFile);
/*     */       }
/*     */     }
/*     */ 
/* 115 */     return 0L;
/*     */   }
/*     */ 
/*     */   private void saveLastUpdate(long value)
/*     */   {
/* 123 */     File dataFile = getLastUpdateFile();
/*     */ 
/* 126 */     dataFile.getParentFile().mkdirs();
/*     */ 
/* 128 */     if (dataFile.exists())
/* 129 */       dataFile.delete();
/*     */     try
/*     */     {
/* 132 */       Files.write(Long.toString(value), dataFile, Charsets.UTF_8);
/*     */     } catch (IOException e) {
/* 134 */       throw new RuntimeException("Cannot write " + dataFile, e);
/*     */     }
/*     */   }
/*     */ 
/*     */   private File getLastUpdateFile()
/*     */   {
/* 143 */     return new File(this.plugin.getDataFolder(), "lastupdate");
/*     */   }
/*     */ 
/*     */   private void loadSections(boolean copyDefaults)
/*     */   {
/* 151 */     if (this.config != null) {
/* 152 */       this.global = this.config.getConfigurationSection("global");
/*     */     }
/* 154 */     if (this.global != null) {
/* 155 */       this.updater = this.global.getConfigurationSection("auto updater");
/*     */     }
/*     */ 
/* 159 */     if ((copyDefaults) && ((!getFile().exists()) || (this.global == null) || (this.updater == null))) {
/* 160 */       this.loadingSections = true;
/*     */ 
/* 162 */       if (this.config != null)
/* 163 */         this.config.options().copyDefaults(true);
/* 164 */       this.plugin.saveDefaultConfig();
/* 165 */       this.plugin.reloadConfig();
/* 166 */       this.loadingSections = false;
/*     */ 
/* 169 */       ProtocolLibrary.log("Created default configuration.", new Object[0]);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void setConfig(ConfigurationSection section, String path, Object value)
/*     */   {
/* 180 */     this.configChanged = true;
/* 181 */     section.set(path, value);
/*     */   }
/*     */ 
/*     */   public File getFile()
/*     */   {
/* 189 */     return new File(this.plugin.getDataFolder(), "config.yml");
/*     */   }
/*     */ 
/*     */   public boolean isDetailedErrorReporting()
/*     */   {
/* 197 */     return this.global.getBoolean("detailed error", false);
/*     */   }
/*     */ 
/*     */   public void setDetailedErrorReporting(boolean value)
/*     */   {
/* 205 */     this.global.set("detailed error", Boolean.valueOf(value));
/*     */   }
/*     */ 
/*     */   public boolean isAutoNotify()
/*     */   {
/* 213 */     return this.updater.getBoolean("notify", true);
/*     */   }
/*     */ 
/*     */   public void setAutoNotify(boolean value)
/*     */   {
/* 221 */     setConfig(this.updater, "notify", Boolean.valueOf(value));
/* 222 */     this.modCount += 1;
/*     */   }
/*     */ 
/*     */   public boolean isAutoDownload()
/*     */   {
/* 230 */     return (this.updater != null) && (this.updater.getBoolean("download", true));
/*     */   }
/*     */ 
/*     */   public void setAutoDownload(boolean value)
/*     */   {
/* 238 */     setConfig(this.updater, "download", Boolean.valueOf(value));
/* 239 */     this.modCount += 1;
/*     */   }
/*     */ 
/*     */   public boolean isDebug()
/*     */   {
/* 249 */     return this.global.getBoolean("debug", false);
/*     */   }
/*     */ 
/*     */   public void setDebug(boolean value)
/*     */   {
/* 257 */     setConfig(this.global, "debug", Boolean.valueOf(value));
/* 258 */     this.modCount += 1;
/*     */   }
/*     */ 
/*     */   public ImmutableList<String> getSuppressedReports()
/*     */   {
/* 266 */     return ImmutableList.copyOf(this.global.getStringList("suppressed reports"));
/*     */   }
/*     */ 
/*     */   public void setSuppressedReports(List<String> reports)
/*     */   {
/* 274 */     this.global.set("suppressed reports", Lists.newArrayList(reports));
/* 275 */     this.modCount += 1;
/*     */   }
/*     */ 
/*     */   public long getAutoDelay()
/*     */   {
/* 284 */     return Math.max(this.updater.getInt("delay", 0), 43200L);
/*     */   }
/*     */ 
/*     */   public void setAutoDelay(long delaySeconds)
/*     */   {
/* 295 */     if (delaySeconds < 43200L)
/* 296 */       delaySeconds = 43200L;
/* 297 */     setConfig(this.updater, "delay", Long.valueOf(delaySeconds));
/* 298 */     this.modCount += 1;
/*     */   }
/*     */ 
/*     */   public String getIgnoreVersionCheck()
/*     */   {
/* 306 */     return this.global.getString("ignore version check", "");
/*     */   }
/*     */ 
/*     */   public void setIgnoreVersionCheck(String ignoreVersion)
/*     */   {
/* 318 */     setConfig(this.global, "ignore version check", ignoreVersion);
/* 319 */     this.modCount += 1;
/*     */   }
/*     */ 
/*     */   public boolean isMetricsEnabled()
/*     */   {
/* 327 */     return this.global.getBoolean("metrics", true);
/*     */   }
/*     */ 
/*     */   public void setMetricsEnabled(boolean enabled)
/*     */   {
/* 338 */     setConfig(this.global, "metrics", Boolean.valueOf(enabled));
/* 339 */     this.modCount += 1;
/*     */   }
/*     */ 
/*     */   public boolean isBackgroundCompilerEnabled()
/*     */   {
/* 347 */     return this.global.getBoolean("background compiler", true);
/*     */   }
/*     */ 
/*     */   public void setBackgroundCompilerEnabled(boolean enabled)
/*     */   {
/* 357 */     setConfig(this.global, "background compiler", Boolean.valueOf(enabled));
/* 358 */     this.modCount += 1;
/*     */   }
/*     */ 
/*     */   public long getAutoLastTime()
/*     */   {
/* 366 */     return this.lastUpdateTime;
/*     */   }
/*     */ 
/*     */   public void setAutoLastTime(long lastTimeSeconds)
/*     */   {
/* 377 */     this.valuesChanged = true;
/* 378 */     this.lastUpdateTime = lastTimeSeconds;
/*     */   }
/*     */ 
/*     */   public String getScriptEngineName()
/*     */   {
/* 386 */     return this.global.getString("script engine", "JavaScript");
/*     */   }
/*     */ 
/*     */   public void setScriptEngineName(String name)
/*     */   {
/* 396 */     setConfig(this.global, "script engine", name);
/* 397 */     this.modCount += 1;
/*     */   }
/*     */ 
/*     */   public PacketFilterManager.PlayerInjectHooks getDefaultMethod()
/*     */   {
/* 405 */     return PacketFilterManager.PlayerInjectHooks.NETWORK_SERVER_OBJECT;
/*     */   }
/*     */ 
/*     */   public PacketFilterManager.PlayerInjectHooks getInjectionMethod()
/*     */     throws IllegalArgumentException
/*     */   {
/* 414 */     String text = this.global.getString("injection method");
/*     */ 
/* 417 */     PacketFilterManager.PlayerInjectHooks hook = getDefaultMethod();
/*     */ 
/* 419 */     if (text != null)
/* 420 */       hook = PacketFilterManager.PlayerInjectHooks.valueOf(text.toUpperCase().replace(" ", "_"));
/* 421 */     return hook;
/*     */   }
/*     */ 
/*     */   public void setInjectionMethod(PacketFilterManager.PlayerInjectHooks hook)
/*     */   {
/* 429 */     setConfig(this.global, "injection method", hook.name());
/* 430 */     this.modCount += 1;
/*     */   }
/*     */ 
/*     */   public int getModificationCount()
/*     */   {
/* 438 */     return this.modCount;
/*     */   }
/*     */ 
/*     */   public void saveAll()
/*     */   {
/* 445 */     if (this.valuesChanged)
/* 446 */       saveLastUpdate(this.lastUpdateTime);
/* 447 */     if (this.configChanged) {
/* 448 */       this.plugin.saveConfig();
/*     */     }
/*     */ 
/* 451 */     this.valuesChanged = false;
/* 452 */     this.configChanged = false;
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.ProtocolConfig
 * JD-Core Version:    0.6.2
 */