/*     */ package com.zettelnet.armorweight.zet.configuration;
/*     */ 
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.File;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import org.bukkit.configuration.InvalidConfigurationException;
/*     */ import org.bukkit.configuration.file.FileConfiguration;
/*     */ import org.bukkit.plugin.Plugin;
/*     */ 
/*     */ public abstract class ResourcedConfigurationFile extends ConfigurationFile
/*     */ {
/*     */   private final ByteArrayOutputStream resourceBytes;
/*     */ 
/*     */   public ResourcedConfigurationFile(Plugin plugin, String file, String resource)
/*     */   {
/*  43 */     this(plugin, file, resource, defaultConfiguration());
/*     */   }
/*     */ 
/*     */   public ResourcedConfigurationFile(Plugin plugin, String file, String resource, FileConfiguration config)
/*     */   {
/*  63 */     this(new File(plugin.getDataFolder(), file), plugin.getResource(resource), config);
/*     */   }
/*     */ 
/*     */   public ResourcedConfigurationFile(File file, InputStream resource)
/*     */   {
/*  78 */     this(file, resource, defaultConfiguration());
/*     */   }
/*     */ 
/*     */   public ResourcedConfigurationFile(File file, InputStream resource, FileConfiguration config)
/*     */   {
/*  95 */     super(file, config);
/*  96 */     this.resourceBytes = new ByteArrayOutputStream();
/*     */     try {
/*  98 */       byte[] buf = new byte[1024];
/*     */       int len;
/* 100 */       while ((len = resource.read(buf)) > 0) {
/* 101 */         this.resourceBytes.write(buf, 0, len);
/*     */       }
/* 103 */       this.resourceBytes.flush();
/*     */     } catch (IOException e) {
/* 105 */       e.printStackTrace();
/*     */     }
/*     */   }
/*     */ 
/*     */   public final InputStream getResource()
/*     */   {
/* 115 */     return new ByteArrayInputStream(this.resourceBytes.toByteArray());
/*     */   }
/*     */ 
/*     */   public void load()
/*     */     throws IOException, InvalidConfigurationException
/*     */   {
/* 130 */     if (!getFile().exists()) {
/* 131 */       create();
/* 132 */       onFileCreate();
/*     */     }
/* 134 */     super.load();
/*     */   }
/*     */ 
/*     */   protected void onFileCreate()
/*     */   {
/*     */   }
/*     */ 
/*     */   public void create()
/*     */     throws IOException
/*     */   {
/* 157 */     getFile().getParentFile().mkdirs();
/* 158 */     InputStream in = getResource();
/* 159 */     OutputStream out = new FileOutputStream(getFile());
/* 160 */     byte[] buf = new byte[1024];
/*     */     int len;
/* 162 */     while ((len = in.read(buf)) > 0) {
/* 163 */       out.write(buf, 0, len);
/*     */     }
/* 165 */     in.close();
/* 166 */     out.close();
/*     */   }
/*     */ 
/*     */   public void reset()
/*     */     throws IOException, InvalidConfigurationException
/*     */   {
/* 179 */     getFile().delete();
/* 180 */     load();
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\ArmorWeight.jar
 * Qualified Name:     com.zettelnet.armorweight.zet.configuration.ResourcedConfigurationFile
 * JD-Core Version:    0.6.2
 */