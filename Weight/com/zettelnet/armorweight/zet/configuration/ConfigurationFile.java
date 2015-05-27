/*     */ package com.zettelnet.armorweight.zet.configuration;
/*     */ 
/*     */ import java.io.BufferedReader;
/*     */ import java.io.BufferedWriter;
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.FileNotFoundException;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.InputStreamReader;
/*     */ import java.io.OutputStream;
/*     */ import java.io.OutputStreamWriter;
/*     */ import java.io.Reader;
/*     */ import java.io.Writer;
/*     */ import java.nio.charset.Charset;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import org.bukkit.configuration.InvalidConfigurationException;
/*     */ import org.bukkit.configuration.file.FileConfiguration;
/*     */ import org.bukkit.configuration.file.YamlConfiguration;
/*     */ 
/*     */ public abstract class ConfigurationFile
/*     */ {
/*     */   private final FileConfiguration config;
/*     */   private final File file;
/*     */   private Charset charset;
/*     */ 
/*     */   public ConfigurationFile(File file)
/*     */   {
/*  59 */     this(file, defaultConfiguration());
/*     */   }
/*     */ 
/*     */   public ConfigurationFile(File file, FileConfiguration config)
/*     */   {
/*  75 */     this.file = file;
/*  76 */     this.config = config;
/*     */ 
/*  78 */     this.charset = defaultCharset();
/*     */   }
/*     */ 
/*     */   public final FileConfiguration getConfig()
/*     */   {
/*  87 */     return this.config;
/*     */   }
/*     */ 
/*     */   public final File getFile()
/*     */   {
/*  96 */     return this.file;
/*     */   }
/*     */ 
/*     */   public String getFileName()
/*     */   {
/* 107 */     return this.file.getName();
/*     */   }
/*     */ 
/*     */   public Charset getCharset()
/*     */   {
/* 120 */     return this.charset;
/*     */   }
/*     */ 
/*     */   public void setCharset(Charset charset)
/*     */   {
/* 133 */     this.charset = charset;
/*     */   }
/*     */ 
/*     */   public void setCharset(String charsetName)
/*     */   {
/* 146 */     setCharset(Charset.forName(charsetName));
/*     */   }
/*     */ 
/*     */   public static FileConfiguration loadConfiguration(FileConfiguration config, Reader reader)
/*     */     throws IOException, InvalidConfigurationException
/*     */   {
/* 170 */     for (Map.Entry entry : config.getValues(false).entrySet()) {
/* 171 */       config.set((String)entry.getKey(), null);
/*     */     }
/*     */ 
/* 174 */     BufferedReader input = (reader instanceof BufferedReader) ? (BufferedReader)reader : new BufferedReader(reader);
/*     */ 
/* 176 */     StringBuilder builder = new StringBuilder();
/*     */     try
/*     */     {
/*     */       String line;
/* 179 */       while ((line = input.readLine()) != null) {
/* 180 */         builder.append(line);
/* 181 */         builder.append('\n');
/*     */       }
/*     */     } finally {
/* 184 */       input.close();
/*     */     }
/* 186 */     config.loadFromString(builder.toString());
/* 187 */     return config;
/*     */   }
/*     */ 
/*     */   public static FileConfiguration loadConfiguration(FileConfiguration config, File file, Charset charset)
/*     */     throws FileNotFoundException, IOException, InvalidConfigurationException
/*     */   {
/* 215 */     return loadConfiguration(config, new InputStreamReader(new FileInputStream(file), charset));
/*     */   }
/*     */ 
/*     */   public static FileConfiguration loadConfiguration(FileConfiguration config, InputStream input, Charset charset)
/*     */     throws IOException, InvalidConfigurationException
/*     */   {
/* 244 */     return loadConfiguration(config, new InputStreamReader(input, charset));
/*     */   }
/*     */ 
/*     */   public void load()
/*     */     throws FileNotFoundException, IOException, InvalidConfigurationException
/*     */   {
/* 263 */     loadConfiguration(this.config, this.file, this.charset);
/* 264 */     loadValues(this.config);
/*     */   }
/*     */ 
/*     */   protected abstract void loadValues(FileConfiguration paramFileConfiguration);
/*     */ 
/*     */   public void save()
/*     */     throws IOException
/*     */   {
/* 286 */     Writer fileWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(this.file), this.charset));
/*     */ 
/* 288 */     fileWriter.write(this.config.saveToString());
/* 289 */     fileWriter.close();
/*     */   }
/*     */ 
/*     */   public void move(File toFile)
/*     */     throws IOException
/*     */   {
/* 313 */     if (toFile.exists()) {
/* 314 */       toFile.delete();
/*     */     }
/* 316 */     if (!this.file.renameTo(toFile))
/* 317 */       throw new IOException("File not moved!");
/*     */   }
/*     */ 
/*     */   public void copy(File toFile)
/*     */     throws IOException
/*     */   {
/* 337 */     if (toFile.exists()) {
/* 338 */       toFile.delete();
/*     */     }
/* 340 */     InputStream is = null;
/* 341 */     OutputStream os = null;
/* 342 */     is = new FileInputStream(this.file);
/* 343 */     os = new FileOutputStream(toFile);
/* 344 */     byte[] buffer = new byte[1024];
/*     */     int length;
/* 346 */     while ((length = is.read(buffer)) > 0) {
/* 347 */       os.write(buffer, 0, length);
/*     */     }
/* 349 */     is.close();
/* 350 */     os.close();
/*     */   }
/*     */ 
/*     */   protected boolean setIfNotExists(String path, Object value)
/*     */   {
/* 366 */     if (!this.config.contains(path)) {
/* 367 */       this.config.set(path, value);
/* 368 */       return true;
/*     */     }
/* 370 */     return false;
/*     */   }
/*     */ 
/*     */   public static FileConfiguration defaultConfiguration()
/*     */   {
/* 380 */     return new YamlConfiguration();
/*     */   }
/*     */ 
/*     */   public static Charset defaultCharset()
/*     */   {
/* 390 */     return Charset.forName("UTF-8");
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\ArmorWeight.jar
 * Qualified Name:     com.zettelnet.armorweight.zet.configuration.ConfigurationFile
 * JD-Core Version:    0.6.2
 */