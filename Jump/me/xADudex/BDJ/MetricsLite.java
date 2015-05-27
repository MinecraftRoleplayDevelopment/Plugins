/*     */ package me.xADudex.BDJ;
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
/*     */ import java.util.Collection;
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
/*  75 */   private final Object optOutLock = new Object();
/*     */ 
/*  80 */   private volatile BukkitTask task = null;
/*     */ 
/*     */   public MetricsLite(Plugin plugin) throws IOException {
/*  83 */     if (plugin == null) {
/*  84 */       throw new IllegalArgumentException("Plugin cannot be null");
/*     */     }
/*     */ 
/*  87 */     this.plugin = plugin;
/*     */ 
/*  90 */     this.configurationFile = getConfigFile();
/*  91 */     this.configuration = YamlConfiguration.loadConfiguration(this.configurationFile);
/*     */ 
/*  94 */     this.configuration.addDefault("opt-out", Boolean.valueOf(false));
/*  95 */     this.configuration.addDefault("guid", UUID.randomUUID().toString());
/*  96 */     this.configuration.addDefault("debug", Boolean.valueOf(false));
/*     */ 
/*  99 */     if (this.configuration.get("guid", null) == null) {
/* 100 */       this.configuration.options().header("http://mcstats.org").copyDefaults(true);
/* 101 */       this.configuration.save(this.configurationFile);
/*     */     }
/*     */ 
/* 105 */     this.guid = this.configuration.getString("guid");
/* 106 */     this.debug = this.configuration.getBoolean("debug", false);
/*     */   }
/*     */ 
/*     */   public boolean start()
/*     */   {
/* 117 */     synchronized (this.optOutLock)
/*     */     {
/* 119 */       if (isOptOut()) {
/* 120 */         return false;
/*     */       }
/*     */ 
/* 124 */       if (this.task != null) {
/* 125 */         return true;
/*     */       }
/*     */ 
/* 129 */       this.task = this.plugin.getServer().getScheduler().runTaskTimerAsynchronously(this.plugin, new Runnable()
/*     */       {
/* 131 */         private boolean firstPost = true;
/*     */ 
/*     */         public void run()
/*     */         {
/*     */           try {
/* 136 */             synchronized (MetricsLite.this.optOutLock)
/*     */             {
/* 138 */               if ((MetricsLite.this.isOptOut()) && (MetricsLite.this.task != null)) {
/* 139 */                 MetricsLite.this.task.cancel();
/* 140 */                 MetricsLite.this.task = null;
/*     */               }
/*     */ 
/*     */             }
/*     */ 
/* 147 */             MetricsLite.this.postPlugin(!this.firstPost);
/*     */ 
/* 151 */             this.firstPost = false;
/*     */           } catch (IOException e) {
/* 153 */             if (MetricsLite.this.debug)
/* 154 */               Bukkit.getLogger().log(Level.INFO, "[Metrics] " + e.getMessage());
/*     */           }
/*     */         }
/*     */       }
/*     */       , 0L, 18000L);
/*     */ 
/* 160 */       return true;
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean isOptOut()
/*     */   {
/* 170 */     synchronized (this.optOutLock)
/*     */     {
/*     */       try {
/* 173 */         this.configuration.load(getConfigFile());
/*     */       } catch (IOException ex) {
/* 175 */         if (this.debug) {
/* 176 */           Bukkit.getLogger().log(Level.INFO, "[Metrics] " + ex.getMessage());
/*     */         }
/* 178 */         return true;
/*     */       } catch (InvalidConfigurationException ex) {
/* 180 */         if (this.debug) {
/* 181 */           Bukkit.getLogger().log(Level.INFO, "[Metrics] " + ex.getMessage());
/*     */         }
/* 183 */         return true;
/*     */       }
/* 185 */       return this.configuration.getBoolean("opt-out", false);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void enable()
/*     */     throws IOException
/*     */   {
/* 196 */     synchronized (this.optOutLock)
/*     */     {
/* 198 */       if (isOptOut()) {
/* 199 */         this.configuration.set("opt-out", Boolean.valueOf(false));
/* 200 */         this.configuration.save(this.configurationFile);
/*     */       }
/*     */ 
/* 204 */       if (this.task == null)
/* 205 */         start();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void disable()
/*     */     throws IOException
/*     */   {
/* 217 */     synchronized (this.optOutLock)
/*     */     {
/* 219 */       if (!isOptOut()) {
/* 220 */         this.configuration.set("opt-out", Boolean.valueOf(true));
/* 221 */         this.configuration.save(this.configurationFile);
/*     */       }
/*     */ 
/* 225 */       if (this.task != null) {
/* 226 */         this.task.cancel();
/* 227 */         this.task = null;
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public File getConfigFile()
/*     */   {
/* 243 */     File pluginsFolder = this.plugin.getDataFolder().getParentFile();
/*     */ 
/* 246 */     return new File(new File(pluginsFolder, "PluginMetrics"), "config.yml");
/*     */   }
/*     */ 
/*     */   private void postPlugin(boolean isPing)
/*     */     throws IOException
/*     */   {
/* 254 */     PluginDescriptionFile description = this.plugin.getDescription();
/* 255 */     String pluginName = description.getName();
/* 256 */     boolean onlineMode = Bukkit.getServer().getOnlineMode();
/* 257 */     String pluginVersion = description.getVersion();
/* 258 */     String serverVersion = Bukkit.getVersion();
/* 259 */     int playersOnline = Bukkit.getServer().getOnlinePlayers().size();
/*     */ 
/* 264 */     StringBuilder json = new StringBuilder(1024);
/* 265 */     json.append('{');
/*     */ 
/* 268 */     appendJSONPair(json, "guid", this.guid);
/* 269 */     appendJSONPair(json, "plugin_version", pluginVersion);
/* 270 */     appendJSONPair(json, "server_version", serverVersion);
/* 271 */     appendJSONPair(json, "players_online", Integer.toString(playersOnline));
/*     */ 
/* 274 */     String osname = System.getProperty("os.name");
/* 275 */     String osarch = System.getProperty("os.arch");
/* 276 */     String osversion = System.getProperty("os.version");
/* 277 */     String java_version = System.getProperty("java.version");
/* 278 */     int coreCount = Runtime.getRuntime().availableProcessors();
/*     */ 
/* 281 */     if (osarch.equals("amd64")) {
/* 282 */       osarch = "x86_64";
/*     */     }
/*     */ 
/* 285 */     appendJSONPair(json, "osname", osname);
/* 286 */     appendJSONPair(json, "osarch", osarch);
/* 287 */     appendJSONPair(json, "osversion", osversion);
/* 288 */     appendJSONPair(json, "cores", Integer.toString(coreCount));
/* 289 */     appendJSONPair(json, "auth_mode", onlineMode ? "1" : "0");
/* 290 */     appendJSONPair(json, "java_version", java_version);
/*     */ 
/* 293 */     if (isPing) {
/* 294 */       appendJSONPair(json, "ping", "1");
/*     */     }
/*     */ 
/* 298 */     json.append('}');
/*     */ 
/* 301 */     URL url = new URL("http://report.mcstats.org" + String.format("/plugin/%s", new Object[] { urlEncode(pluginName) }));
/*     */     URLConnection connection;
/*     */     URLConnection connection;
/* 308 */     if (isMineshafterPresent())
/* 309 */       connection = url.openConnection(Proxy.NO_PROXY);
/*     */     else {
/* 311 */       connection = url.openConnection();
/*     */     }
/*     */ 
/* 315 */     byte[] uncompressed = json.toString().getBytes();
/* 316 */     byte[] compressed = gzip(json.toString());
/*     */ 
/* 319 */     connection.addRequestProperty("User-Agent", "MCStats/7");
/* 320 */     connection.addRequestProperty("Content-Type", "application/json");
/* 321 */     connection.addRequestProperty("Content-Encoding", "gzip");
/* 322 */     connection.addRequestProperty("Content-Length", Integer.toString(compressed.length));
/* 323 */     connection.addRequestProperty("Accept", "application/json");
/* 324 */     connection.addRequestProperty("Connection", "close");
/*     */ 
/* 326 */     connection.setDoOutput(true);
/*     */ 
/* 328 */     if (this.debug) {
/* 329 */       System.out.println("[Metrics] Prepared request for " + pluginName + " uncompressed=" + uncompressed.length + " compressed=" + compressed.length);
/*     */     }
/*     */ 
/* 333 */     OutputStream os = connection.getOutputStream();
/* 334 */     os.write(compressed);
/* 335 */     os.flush();
/*     */ 
/* 338 */     BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
/* 339 */     String response = reader.readLine();
/*     */ 
/* 342 */     os.close();
/* 343 */     reader.close();
/*     */ 
/* 345 */     if ((response == null) || (response.startsWith("ERR")) || (response.startsWith("7"))) {
/* 346 */       if (response == null)
/* 347 */         response = "null";
/* 348 */       else if (response.startsWith("7")) {
/* 349 */         response = response.substring(response.startsWith("7,") ? 2 : 1);
/*     */       }
/*     */ 
/* 352 */       throw new IOException(response);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static byte[] gzip(String input)
/*     */   {
/* 363 */     ByteArrayOutputStream baos = new ByteArrayOutputStream();
/* 364 */     GZIPOutputStream gzos = null;
/*     */     try
/*     */     {
/* 367 */       gzos = new GZIPOutputStream(baos);
/* 368 */       gzos.write(input.getBytes("UTF-8"));
/*     */     } catch (IOException e) {
/* 370 */       e.printStackTrace();
/*     */ 
/* 372 */       if (gzos != null) try {
/* 373 */           gzos.close();
/*     */         }
/*     */         catch (IOException localIOException1)
/*     */         {
/*     */         }
/*     */     }
/*     */     finally
/*     */     {
/* 372 */       if (gzos != null) try {
/* 373 */           gzos.close();
/*     */         }
/*     */         catch (IOException localIOException2)
/*     */         {
/*     */         } 
/*     */     }
/* 378 */     return baos.toByteArray();
/*     */   }
/*     */ 
/*     */   private boolean isMineshafterPresent()
/*     */   {
/*     */     try
/*     */     {
/* 388 */       Class.forName("mineshafter.MineServer");
/* 389 */       return true; } catch (Exception e) {
/*     */     }
/* 391 */     return false;
/*     */   }
/*     */ 
/*     */   private static void appendJSONPair(StringBuilder json, String key, String value)
/*     */     throws UnsupportedEncodingException
/*     */   {
/* 404 */     boolean isValueNumeric = false;
/*     */     try
/*     */     {
/* 407 */       if ((value.equals("0")) || (!value.endsWith("0"))) {
/* 408 */         Double.parseDouble(value);
/* 409 */         isValueNumeric = true;
/*     */       }
/*     */     } catch (NumberFormatException e) {
/* 412 */       isValueNumeric = false;
/*     */     }
/*     */ 
/* 415 */     if (json.charAt(json.length() - 1) != '{') {
/* 416 */       json.append(',');
/*     */     }
/*     */ 
/* 419 */     json.append(escapeJSON(key));
/* 420 */     json.append(':');
/*     */ 
/* 422 */     if (isValueNumeric)
/* 423 */       json.append(value);
/*     */     else
/* 425 */       json.append(escapeJSON(value));
/*     */   }
/*     */ 
/*     */   private static String escapeJSON(String text)
/*     */   {
/* 436 */     StringBuilder builder = new StringBuilder();
/*     */ 
/* 438 */     builder.append('"');
/* 439 */     for (int index = 0; index < text.length(); index++) {
/* 440 */       char chr = text.charAt(index);
/*     */ 
/* 442 */       switch (chr) {
/*     */       case '"':
/*     */       case '\\':
/* 445 */         builder.append('\\');
/* 446 */         builder.append(chr);
/* 447 */         break;
/*     */       case '\b':
/* 449 */         builder.append("\\b");
/* 450 */         break;
/*     */       case '\t':
/* 452 */         builder.append("\\t");
/* 453 */         break;
/*     */       case '\n':
/* 455 */         builder.append("\\n");
/* 456 */         break;
/*     */       case '\r':
/* 458 */         builder.append("\\r");
/* 459 */         break;
/*     */       default:
/* 461 */         if (chr < ' ') {
/* 462 */           String t = "000" + Integer.toHexString(chr);
/* 463 */           builder.append("\\u" + t.substring(t.length() - 4));
/*     */         } else {
/* 465 */           builder.append(chr);
/*     */         }
/*     */         break;
/*     */       }
/*     */     }
/* 470 */     builder.append('"');
/*     */ 
/* 472 */     return builder.toString();
/*     */   }
/*     */ 
/*     */   private static String urlEncode(String text)
/*     */     throws UnsupportedEncodingException
/*     */   {
/* 482 */     return URLEncoder.encode(text, "UTF-8");
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\BetterDoubleJump.jar
 * Qualified Name:     me.xADudex.BDJ.MetricsLite
 * JD-Core Version:    0.6.2
 */