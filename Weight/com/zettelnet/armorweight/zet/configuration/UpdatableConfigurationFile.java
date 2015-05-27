/*     */ package com.zettelnet.armorweight.zet.configuration;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import org.bukkit.configuration.file.FileConfiguration;
/*     */ import org.bukkit.plugin.Plugin;
/*     */ 
/*     */ public abstract class UpdatableConfigurationFile extends ResourcedConfigurationFile
/*     */ {
/*     */   private String updateFromVersion;
/*     */   private String versionKey;
/*     */ 
/*     */   public UpdatableConfigurationFile(Plugin plugin, String file, String resource)
/*     */   {
/*  26 */     this(plugin, file, resource, defaultConfiguration());
/*     */   }
/*     */ 
/*     */   public UpdatableConfigurationFile(Plugin plugin, String file, String resource, FileConfiguration config)
/*     */   {
/*  31 */     this(new File(plugin.getDataFolder(), file), plugin.getResource(resource), config);
/*     */   }
/*     */ 
/*     */   public UpdatableConfigurationFile(File file, InputStream resource)
/*     */   {
/*  36 */     this(file, resource, defaultConfiguration());
/*     */   }
/*     */ 
/*     */   public UpdatableConfigurationFile(File file, InputStream resource, FileConfiguration config)
/*     */   {
/*  41 */     super(file, resource, config);
/*  42 */     this.updateFromVersion = null;
/*  43 */     this.versionKey = defaultVersionKey();
/*     */   }
/*     */ 
/*     */   protected void update(FileConfiguration config)
/*     */   {
/*     */   }
/*     */ 
/*     */   protected void updateStart(String fromVersion)
/*     */   {
/*  99 */     if (this.updateFromVersion == null) {
/* 100 */       this.updateFromVersion = fromVersion;
/* 101 */       onUpdateStart(fromVersion);
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void onUpdateStart(String fromVersion)
/*     */   {
/*     */   }
/*     */ 
/*     */   protected void updateDone(String toVersion)
/*     */     throws IOException
/*     */   {
/* 134 */     if (this.updateFromVersion != null) {
/* 135 */       getConfig().set(this.versionKey, toVersion);
/* 136 */       save();
/* 137 */       onUpdateDone(this.updateFromVersion, toVersion);
/* 138 */       this.updateFromVersion = null;
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void onUpdateDone(String fromVersion, String toVersion)
/*     */   {
/*     */   }
/*     */ 
/*     */   public String getVersion()
/*     */   {
/* 164 */     return getConfig().getString(this.versionKey, "unknown");
/*     */   }
/*     */ 
/*     */   protected String getVersionKey()
/*     */   {
/* 176 */     return this.versionKey;
/*     */   }
/*     */ 
/*     */   protected void setVersionKey(String key)
/*     */   {
/* 188 */     this.versionKey = key;
/*     */   }
/*     */ 
/*     */   public static String defaultVersionKey()
/*     */   {
/* 201 */     return "config.version";
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\ArmorWeight.jar
 * Qualified Name:     com.zettelnet.armorweight.zet.configuration.UpdatableConfigurationFile
 * JD-Core Version:    0.6.2
 */