/*     */ package com.conventnunnery.libraries.config;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import org.bukkit.configuration.file.FileConfiguration;
/*     */ import org.bukkit.configuration.file.YamlConfiguration;
/*     */ 
/*     */ public class ConventYamlConfiguration extends YamlConfiguration
/*     */   implements ConventConfiguration
/*     */ {
/*     */   private final File file;
/*     */   private final String version;
/*     */ 
/*     */   public ConventYamlConfiguration(File file)
/*     */   {
/*  20 */     this(file, null);
/*     */   }
/*     */ 
/*     */   public ConventYamlConfiguration(File file, String version)
/*     */   {
/*  32 */     this.file = file;
/*  33 */     this.version = version;
/*     */   }
/*     */ 
/*     */   public String getName()
/*     */   {
/*  38 */     return this.file.getName();
/*     */   }
/*     */ 
/*     */   public boolean load()
/*     */   {
/*  48 */     return load(options().updateOnLoad(), options().createDefaultFile());
/*     */   }
/*     */ 
/*     */   public boolean load(boolean update, boolean createDefaultFile)
/*     */   {
/*     */     try
/*     */     {
/*  62 */       if (this.file.exists())
/*     */       {
/*  64 */         load(this.file);
/*     */ 
/*  66 */         if ((needToUpdate()) && (update)) update();
/*     */       }
/*  68 */       else if (createDefaultFile)
/*     */       {
/*  70 */         options().copyDefaults(true);
/*     */ 
/*  72 */         return save();
/*     */       }
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/*  77 */       e.printStackTrace();
/*  78 */       return false;
/*     */     }
/*     */ 
/*  81 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean update()
/*     */   {
/*  92 */     if ((options().backupOnUpdate()) && 
/*  93 */       (!backup())) return false;
/*     */ 
/*  95 */     options().copyDefaults(true);
/*     */ 
/*  97 */     return save();
/*     */   }
/*     */ 
/*     */   public ConventYamlConfigurationOptions options()
/*     */   {
/* 103 */     if (this.options == null) {
/* 104 */       this.options = new ConventYamlConfigurationOptions(this);
/*     */     }
/* 106 */     return (ConventYamlConfigurationOptions)this.options;
/*     */   }
/*     */ 
/*     */   public boolean save()
/*     */   {
/*     */     try
/*     */     {
/* 117 */       this.file.getParentFile().mkdirs();
/* 118 */       save(this.file);
/* 119 */       return true; } catch (IOException e) {
/*     */     }
/* 121 */     return false;
/*     */   }
/*     */ 
/*     */   public void setDefaults(InputStream inputStream)
/*     */   {
/* 127 */     super.setDefaults(YamlConfiguration.loadConfiguration(inputStream));
/*     */   }
/*     */ 
/*     */   public void saveDefaults(InputStream inputStream)
/*     */   {
/*     */   }
/*     */ 
/*     */   public boolean needToUpdate()
/*     */   {
/* 137 */     return (getString("version") == null) || ((this.version != null) && (!this.version.equalsIgnoreCase(getString("version"))));
/*     */   }
/*     */ 
/*     */   public boolean backup()
/*     */   {
/* 143 */     File backup = new File(this.file.getParent(), this.file.getName().replace(".yml", "_old.yml"));
/*     */     try
/*     */     {
/* 147 */       backup.getParentFile().mkdirs();
/*     */ 
/* 149 */       save(backup);
/*     */     }
/*     */     catch (IOException e) {
/* 152 */       e.printStackTrace();
/* 153 */       return false;
/*     */     }
/*     */ 
/* 156 */     return true;
/*     */   }
/*     */ 
/*     */   public FileConfiguration getFileConfiguration()
/*     */   {
/* 162 */     return this;
/*     */   }
/*     */ 
/*     */   public String getVersion()
/*     */   {
/* 167 */     return this.version;
/*     */   }
/*     */ 
/*     */   public boolean load(boolean update)
/*     */   {
/* 177 */     return load(update, options().createDefaultFile());
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\ItemAttributes.jar
 * Qualified Name:     com.conventnunnery.libraries.config.ConventYamlConfiguration
 * JD-Core Version:    0.6.2
 */