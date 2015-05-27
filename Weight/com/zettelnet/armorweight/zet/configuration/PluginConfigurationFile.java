/*     */ package com.zettelnet.armorweight.zet.configuration;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.FileNotFoundException;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.Reader;
/*     */ import java.nio.charset.Charset;
/*     */ import java.util.logging.Logger;
/*     */ import org.bukkit.Bukkit;
/*     */ import org.bukkit.configuration.InvalidConfigurationException;
/*     */ import org.bukkit.configuration.file.FileConfiguration;
/*     */ import org.bukkit.plugin.Plugin;
/*     */ 
/*     */ public abstract class PluginConfigurationFile extends UpdatableConfigurationFile
/*     */ {
/*  28 */   private static Logger log = Bukkit.getLogger();
/*     */   private boolean updateSilently;
/*     */ 
/*     */   public PluginConfigurationFile(Plugin plugin, String file, String resource)
/*     */   {
/*  33 */     this(plugin, file, resource, defaultConfiguration());
/*     */   }
/*     */ 
/*     */   public PluginConfigurationFile(Plugin plugin, String file, String resource, FileConfiguration config) {
/*  37 */     this(new File(plugin.getDataFolder(), file), plugin.getResource(resource), config);
/*     */   }
/*     */ 
/*     */   public PluginConfigurationFile(File file, InputStream resource) {
/*  41 */     this(file, resource, defaultConfiguration());
/*     */   }
/*     */ 
/*     */   public PluginConfigurationFile(File file, InputStream resource, FileConfiguration config) {
/*  45 */     super(file, resource, config);
/*  46 */     this.updateSilently = false;
/*     */   }
/*     */ 
/*     */   public static FileConfiguration loadConfiguration(FileConfiguration config, Reader reader) {
/*     */     try {
/*  51 */       return ConfigurationFile.loadConfiguration(config, reader);
/*     */     } catch (IOException e) {
/*  53 */       log.warning(String.format("Failed to load configuration file using reader:", new Object[0]));
/*  54 */       e.printStackTrace();
/*     */     } catch (InvalidConfigurationException e) {
/*  56 */       log.warning(String.format("Failed to load configuration file using reader. Configuration invalid:", new Object[0]));
/*  57 */       e.printStackTrace();
/*     */     }
/*  59 */     return config;
/*     */   }
/*     */ 
/*     */   public static FileConfiguration loadConfiguration(FileConfiguration config, File file, Charset charset) {
/*     */     try {
/*  64 */       return ConfigurationFile.loadConfiguration(config, file, charset);
/*     */     } catch (FileNotFoundException e) {
/*  66 */       log.warning(String.format("Failed to load configuration file using reader. File not found:", new Object[0]));
/*  67 */       e.printStackTrace();
/*     */     } catch (IOException e) {
/*  69 */       log.warning(String.format("Failed to load configuration file %s:", new Object[] { file.getName() }));
/*  70 */       e.printStackTrace();
/*     */     } catch (InvalidConfigurationException e) {
/*  72 */       log.warning(String.format("Failed to load configuration file %s. Configuration invalid:", new Object[] { file.getName() }));
/*  73 */       e.printStackTrace();
/*     */     }
/*  75 */     return config;
/*     */   }
/*     */ 
/*     */   public static FileConfiguration loadConfiguration(FileConfiguration config, InputStream input, Charset charset) {
/*     */     try {
/*  80 */       return ConfigurationFile.loadConfiguration(config, input, charset);
/*     */     } catch (IOException e) {
/*  82 */       log.warning(String.format("Failed to load configuration file using stream:", new Object[0]));
/*  83 */       e.printStackTrace();
/*     */     } catch (InvalidConfigurationException e) {
/*  85 */       log.warning(String.format("Failed to load configuration file using stream. Configuration invalid:", new Object[0]));
/*  86 */       e.printStackTrace();
/*     */     }
/*  88 */     return config;
/*     */   }
/*     */ 
/*     */   public void load()
/*     */   {
/*     */     try {
/*  94 */       super.load();
/*     */     } catch (FileNotFoundException e) {
/*  96 */       log.warning(String.format("Failed to load configuration file %s! File not found:", new Object[] { getFileName() }));
/*  97 */       e.printStackTrace();
/*     */     } catch (IOException e) {
/*  99 */       log.warning(String.format("Failed to load configuration file %s:", new Object[] { getFileName() }));
/* 100 */       e.printStackTrace();
/*     */     } catch (InvalidConfigurationException e) {
/* 102 */       log.warning(String.format("Failed to load configuration file %s! The configuration is invalid:", new Object[] { getFileName() }));
/* 103 */       e.printStackTrace();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void save()
/*     */   {
/*     */     try {
/* 110 */       super.save();
/*     */     } catch (IOException e) {
/* 112 */       log.warning(String.format("Failed to save configuration file %s:", new Object[] { getFileName() }));
/* 113 */       e.printStackTrace();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void create()
/*     */   {
/*     */     try {
/* 120 */       super.create();
/*     */     } catch (IOException e) {
/* 122 */       log.warning(String.format("Failed to create configuration file %s:", new Object[] { getFileName() }));
/* 123 */       e.printStackTrace();
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void onFileCreate()
/*     */   {
/* 132 */     log.info(String.format("Created new configuration file %s from defaults.", new Object[] { getFileName() }));
/*     */   }
/*     */ 
/*     */   public void reset()
/*     */   {
/*     */     try {
/* 138 */       super.reset();
/*     */     } catch (IOException e) {
/* 140 */       log.warning(String.format("Failed to reset configuration file %s:", new Object[] { getFileName() }));
/* 141 */       e.printStackTrace();
/*     */     } catch (InvalidConfigurationException e) {
/* 143 */       log.warning(String.format("Failed to reset configuration file %s! The configuration is invalid:", new Object[] { getFileName() }));
/* 144 */       e.printStackTrace();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void move(File newFile)
/*     */   {
/*     */     try {
/* 151 */       super.move(newFile);
/*     */     } catch (IOException e) {
/* 153 */       log.warning(String.format("Failed to move file %s to %s:", new Object[] { getFileName(), newFile.getName() }));
/* 154 */       e.printStackTrace();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void copy(File newFile)
/*     */   {
/*     */     try {
/* 161 */       super.copy(newFile);
/*     */     } catch (IOException e) {
/* 163 */       log.warning(String.format("Failed to copy file %s to %s:", new Object[] { getFileName(), newFile.getName() }));
/* 164 */       e.printStackTrace();
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void onUpdateStart(String startVersion)
/*     */   {
/* 170 */     if (!this.updateSilently) {
/* 171 */       log.warning(String.format("Configuration file %s is outdatet (v%s)!", new Object[] { getFileName(), startVersion }));
/* 172 */       log.info(String.format("Attempting to update %s ...", new Object[] { getFileName() }));
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void updateDone(String toVersion)
/*     */   {
/*     */     try {
/* 179 */       super.updateDone(toVersion);
/*     */     } catch (IOException e) {
/* 181 */       log.warning(String.format("Failed to save updated configuration file %s to version %s:", new Object[] { getFileName(), toVersion }));
/* 182 */       e.printStackTrace();
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void onUpdateDone(String startVersion, String endVersion)
/*     */   {
/* 188 */     if (!this.updateSilently) {
/* 189 */       log.info(String.format("Configuration file %s has been updated automatically from v%s to v%s!", new Object[] { getFileName(), startVersion, endVersion }));
/* 190 */       log.info(String.format("Please check if the update was successful.", new Object[0]));
/*     */     }
/*     */   }
/*     */ 
/*     */   public static void setLogger(Logger logger) {
/* 195 */     log = logger;
/*     */   }
/*     */ 
/*     */   protected void setUpdateSilently(boolean updateSilently) {
/* 199 */     this.updateSilently = updateSilently;
/*     */   }
/*     */ 
/*     */   public boolean isUpdateSilently() {
/* 203 */     return this.updateSilently;
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\ArmorWeight.jar
 * Qualified Name:     com.zettelnet.armorweight.zet.configuration.PluginConfigurationFile
 * JD-Core Version:    0.6.2
 */