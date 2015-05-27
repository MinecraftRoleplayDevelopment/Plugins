/*     */ package com.gmail.filoghost.healthbar.metrics;
/*     */ 
/*     */ import java.io.BufferedReader;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStreamReader;
/*     */ import java.io.OutputStream;
/*     */ import java.io.PrintStream;
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.net.Proxy;
/*     */ import java.net.URL;
/*     */ import java.net.URLConnection;
/*     */ import java.net.URLEncoder;
/*     */ import java.util.UUID;
/*     */ import java.util.logging.Level;
/*     */ import java.util.logging.Logger;
/*     */ import java.util.zip.GZIPOutputStream;
/*     */ import org.bukkit.Bukkit;
/*     */ import org.bukkit.Server;
/*     */ import org.bukkit.configuration.InvalidConfigurationException;
/*     */ import org.bukkit.configuration.file.YamlConfiguration;
/*     */ import org.bukkit.configuration.file.YamlConfigurationOptions;
/*     */ import org.bukkit.plugin.Plugin;
/*     */ import org.bukkit.plugin.PluginDescriptionFile;
/*     */ import org.bukkit.scheduler.BukkitScheduler;
/*     */ import org.bukkit.scheduler.BukkitTask;
/*     */ 
/*     */ public class MetricsLite
/*     */ {
/*     */   private static final int REVISION = 7;
/*     */   private static final String BASE_URL = "http://report.mcstats.org";
/*     */   private static final String REPORT_URL = "/plugin/%s";
/*     */   private static final int PING_INTERVAL = 15;
/*     */   private final Plugin plugin;
/*     */   private final YamlConfiguration configuration;
/*     */   private final File configurationFile;
/*     */   private final String guid;
/*     */   private final boolean debug;
/* 103 */   private final Object optOutLock = new Object();
/*     */ 
/* 108 */   private volatile BukkitTask task = null;
/*     */ 
/*     */   public MetricsLite(Plugin plugin) throws IOException {
/* 111 */     if (plugin == null) {
/* 112 */       throw new IllegalArgumentException("Plugin cannot be null");
/*     */     }
/*     */ 
/* 115 */     this.plugin = plugin;
/*     */ 
/* 118 */     this.configurationFile = getConfigFile();
/* 119 */     this.configuration = YamlConfiguration.loadConfiguration(this.configurationFile);
/*     */ 
/* 122 */     this.configuration.addDefault("opt-out", Boolean.valueOf(false));
/* 123 */     this.configuration.addDefault("guid", UUID.randomUUID().toString());
/* 124 */     this.configuration.addDefault("debug", Boolean.valueOf(false));
/*     */ 
/* 127 */     if (this.configuration.get("guid", null) == null) {
/* 128 */       this.configuration.options().header("http://mcstats.org").copyDefaults(true);
/* 129 */       this.configuration.save(this.configurationFile);
/*     */     }
/*     */ 
/* 133 */     this.guid = this.configuration.getString("guid");
/* 134 */     this.debug = this.configuration.getBoolean("debug", false);
/*     */   }
/*     */ 
/*     */   public boolean start()
/*     */   {
/* 145 */     synchronized (this.optOutLock)
/*     */     {
/* 147 */       if (isOptOut()) {
/* 148 */         return false;
/*     */       }
/*     */ 
/* 152 */       if (this.task != null) {
/* 153 */         return true;
/*     */       }
/*     */ 
/* 157 */       this.task = this.plugin.getServer().getScheduler().runTaskTimerAsynchronously(this.plugin, new Runnable()
/*     */       {
/* 159 */         private boolean firstPost = true;
/*     */ 
/*     */         public void run()
/*     */         {
/*     */           try {
/* 164 */             synchronized (MetricsLite.this.optOutLock)
/*     */             {
/* 166 */               if ((MetricsLite.this.isOptOut()) && (MetricsLite.this.task != null)) {
/* 167 */                 MetricsLite.this.task.cancel();
/* 168 */                 MetricsLite.this.task = null;
/*     */               }
/*     */ 
/*     */             }
/*     */ 
/* 175 */             MetricsLite.this.postPlugin(!this.firstPost);
/*     */ 
/* 179 */             this.firstPost = false;
/*     */           } catch (IOException e) {
/* 181 */             if (MetricsLite.this.debug)
/* 182 */               Bukkit.getLogger().log(Level.INFO, "[Metrics] " + e.getMessage());
/*     */           }
/*     */         }
/*     */       }
/*     */       , 0L, 18000L);
/*     */ 
/* 188 */       return true;
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean isOptOut()
/*     */   {
/* 198 */     synchronized (this.optOutLock)
/*     */     {
/*     */       try {
/* 201 */         this.configuration.load(getConfigFile());
/*     */       } catch (IOException ex) {
/* 203 */         if (this.debug) {
/* 204 */           Bukkit.getLogger().log(Level.INFO, "[Metrics] " + ex.getMessage());
/*     */         }
/* 206 */         return true;
/*     */       } catch (InvalidConfigurationException ex) {
/* 208 */         if (this.debug) {
/* 209 */           Bukkit.getLogger().log(Level.INFO, "[Metrics] " + ex.getMessage());
/*     */         }
/* 211 */         return true;
/*     */       }
/* 213 */       return this.configuration.getBoolean("opt-out", false);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void enable()
/*     */     throws IOException
/*     */   {
/* 224 */     synchronized (this.optOutLock)
/*     */     {
/* 226 */       if (isOptOut()) {
/* 227 */         this.configuration.set("opt-out", Boolean.valueOf(false));
/* 228 */         this.configuration.save(this.configurationFile);
/*     */       }
/*     */ 
/* 232 */       if (this.task == null)
/* 233 */         start();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void disable()
/*     */     throws IOException
/*     */   {
/* 245 */     synchronized (this.optOutLock)
/*     */     {
/* 247 */       if (!isOptOut()) {
/* 248 */         this.configuration.set("opt-out", Boolean.valueOf(true));
/* 249 */         this.configuration.save(this.configurationFile);
/*     */       }
/*     */ 
/* 253 */       if (this.task != null) {
/* 254 */         this.task.cancel();
/* 255 */         this.task = null;
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public File getConfigFile()
/*     */   {
/* 271 */     File pluginsFolder = this.plugin.getDataFolder().getParentFile();
/*     */ 
/* 274 */     return new File(new File(pluginsFolder, "PluginMetrics"), "config.yml");
/*     */   }
/*     */ 
/*     */   private void postPlugin(boolean isPing)
/*     */     throws IOException
/*     */   {
/* 282 */     PluginDescriptionFile description = this.plugin.getDescription();
/* 283 */     String pluginName = description.getName();
/* 284 */     boolean onlineMode = Bukkit.getServer().getOnlineMode();
/* 285 */     String pluginVersion = description.getVersion();
/* 286 */     String serverVersion = Bukkit.getVersion();
/* 287 */     int playersOnline = Bukkit.getServer().getOnlinePlayers().length;
/*     */ 
/* 292 */     StringBuilder json = new StringBuilder(1024);
/* 293 */     json.append('{');
/*     */ 
/* 296 */     appendJSONPair(json, "guid", this.guid);
/* 297 */     appendJSONPair(json, "plugin_version", pluginVersion);
/* 298 */     appendJSONPair(json, "server_version", serverVersion);
/* 299 */     appendJSONPair(json, "players_online", Integer.toString(playersOnline));
/*     */ 
/* 302 */     String osname = System.getProperty("os.name");
/* 303 */     String osarch = System.getProperty("os.arch");
/* 304 */     String osversion = System.getProperty("os.version");
/* 305 */     String java_version = System.getProperty("java.version");
/* 306 */     int coreCount = Runtime.getRuntime().availableProcessors();
/*     */ 
/* 309 */     if (osarch.equals("amd64")) {
/* 310 */       osarch = "x86_64";
/*     */     }
/*     */ 
/* 313 */     appendJSONPair(json, "osname", osname);
/* 314 */     appendJSONPair(json, "osarch", osarch);
/* 315 */     appendJSONPair(json, "osversion", osversion);
/* 316 */     appendJSONPair(json, "cores", Integer.toString(coreCount));
/* 317 */     appendJSONPair(json, "auth_mode", onlineMode ? "1" : "0");
/* 318 */     appendJSONPair(json, "java_version", java_version);
/*     */ 
/* 321 */     if (isPing) {
/* 322 */       appendJSONPair(json, "ping", "1");
/*     */     }
/*     */ 
/* 326 */     json.append('}');
/*     */ 
/* 329 */     URL url = new URL("http://report.mcstats.org" + String.format("/plugin/%s", new Object[] { urlEncode(pluginName) }));
/*     */     URLConnection connection;
/*     */     URLConnection connection;
/* 336 */     if (isMineshafterPresent())
/* 337 */       connection = url.openConnection(Proxy.NO_PROXY);
/*     */     else {
/* 339 */       connection = url.openConnection();
/*     */     }
/*     */ 
/* 343 */     byte[] uncompressed = json.toString().getBytes();
/* 344 */     byte[] compressed = gzip(json.toString());
/*     */ 
/* 347 */     connection.addRequestProperty("User-Agent", "MCStats/7");
/* 348 */     connection.addRequestProperty("Content-Type", "application/json");
/* 349 */     connection.addRequestProperty("Content-Encoding", "gzip");
/* 350 */     connection.addRequestProperty("Content-Length", Integer.toString(compressed.length));
/* 351 */     connection.addRequestProperty("Accept", "application/json");
/* 352 */     connection.addRequestProperty("Connection", "close");
/*     */ 
/* 354 */     connection.setDoOutput(true);
/*     */ 
/* 356 */     if (this.debug) {
/* 357 */       System.out.println("[Metrics] Prepared request for " + pluginName + " uncompressed=" + uncompressed.length + " compressed=" + compressed.length);
/*     */     }
/*     */ 
/* 361 */     OutputStream os = connection.getOutputStream();
/* 362 */     os.write(compressed);
/* 363 */     os.flush();
/*     */ 
/* 366 */     BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
/* 367 */     String response = reader.readLine();
/*     */ 
/* 370 */     os.close();
/* 371 */     reader.close();
/*     */ 
/* 373 */     if ((response == null) || (response.startsWith("ERR")) || (response.startsWith("7"))) {
/* 374 */       if (response == null)
/* 375 */         response = "null";
/* 376 */       else if (response.startsWith("7")) {
/* 377 */         response = response.substring(response.startsWith("7,") ? 2 : 1);
/*     */       }
/*     */ 
/* 380 */       throw new IOException(response);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static byte[] gzip(String input)
/*     */   {
/* 391 */     ByteArrayOutputStream baos = new ByteArrayOutputStream();
/* 392 */     GZIPOutputStream gzos = null;
/*     */     try
/*     */     {
/* 395 */       gzos = new GZIPOutputStream(baos);
/* 396 */       gzos.write(input.getBytes("UTF-8"));
/*     */     } catch (IOException e) {
/* 398 */       e.printStackTrace();
/*     */ 
/* 400 */       if (gzos != null) try {
/* 401 */           gzos.close();
/*     */         }
/*     */         catch (IOException localIOException1)
/*     */         {
/*     */         }
/*     */     }
/*     */     finally
/*     */     {
/* 400 */       if (gzos != null) try {
/* 401 */           gzos.close();
/*     */         }
/*     */         catch (IOException localIOException2)
/*     */         {
/*     */         } 
/*     */     }
/* 406 */     return baos.toByteArray();
/*     */   }
/*     */ 
/*     */   private boolean isMineshafterPresent()
/*     */   {
/*     */     try
/*     */     {
/* 416 */       Class.forName("mineshafter.MineServer");
/* 417 */       return true; } catch (Exception e) {
/*     */     }
/* 419 */     return false;
/*     */   }
/*     */ 
/*     */   private static void appendJSONPair(StringBuilder json, String key, String value)
/*     */     throws UnsupportedEncodingException
/*     */   {
/* 432 */     boolean isValueNumeric = false;
/*     */     try
/*     */     {
/* 435 */       if ((value.equals("0")) || (!value.endsWith("0"))) {
/* 436 */         Double.parseDouble(value);
/* 437 */         isValueNumeric = true;
/*     */       }
/*     */     } catch (NumberFormatException e) {
/* 440 */       isValueNumeric = false;
/*     */     }
/*     */ 
/* 443 */     if (json.charAt(json.length() - 1) != '{') {
/* 444 */       json.append(',');
/*     */     }
/*     */ 
/* 447 */     json.append(escapeJSON(key));
/* 448 */     json.append(':');
/*     */ 
/* 450 */     if (isValueNumeric)
/* 451 */       json.append(value);
/*     */     else
/* 453 */       json.append(escapeJSON(value));
/*     */   }
/*     */ 
/*     */   private static String escapeJSON(String text)
/*     */   {
/* 464 */     StringBuilder builder = new StringBuilder();
/*     */ 
/* 466 */     builder.append('"');
/* 467 */     for (int index = 0; index < text.length(); index++) {
/* 468 */       char chr = text.charAt(index);
/*     */ 
/* 470 */       switch (chr) {
/*     */       case '"':
/*     */       case '\\':
/* 473 */         builder.append('\\');
/* 474 */         builder.append(chr);
/* 475 */         break;
/*     */       case '\b':
/* 477 */         builder.append("\\b");
/* 478 */         break;
/*     */       case '\t':
/* 480 */         builder.append("\\t");
/* 481 */         break;
/*     */       case '\n':
/* 483 */         builder.append("\\n");
/* 484 */         break;
/*     */       case '\r':
/* 486 */         builder.append("\\r");
/* 487 */         break;
/*     */       default:
/* 489 */         if (chr < ' ') {
/* 490 */           String t = "000" + Integer.toHexString(chr);
/* 491 */           builder.append("\\u" + t.substring(t.length() - 4));
/*     */         } else {
/* 493 */           builder.append(chr);
/*     */         }
/*     */         break;
/*     */       }
/*     */     }
/* 498 */     builder.append('"');
/*     */ 
/* 500 */     return builder.toString();
/*     */   }
/*     */ 
/*     */   private static String urlEncode(String text)
/*     */     throws UnsupportedEncodingException
/*     */   {
/* 510 */     return URLEncoder.encode(text, "UTF-8");
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\HealthBar.jar
 * Qualified Name:     com.gmail.filoghost.healthbar.metrics.MetricsLite
 * JD-Core Version:    0.6.2
 */