/*     */ package jp.mydns.dyukusi;
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
/*     */ import java.util.Collections;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.LinkedHashSet;
/*     */ import java.util.Set;
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
/*     */ public class Metrics
/*     */ {
/*     */   private static final int REVISION = 7;
/*     */   private static final String BASE_URL = "http://report.mcstats.org";
/*     */   private static final String REPORT_URL = "/plugin/%s";
/*     */   private static final int PING_INTERVAL = 15;
/*     */   private final Plugin plugin;
/*  87 */   private final Set<Graph> graphs = Collections.synchronizedSet(new HashSet());
/*     */   private final YamlConfiguration configuration;
/*     */   private final File configurationFile;
/*     */   private final String guid;
/*     */   private final boolean debug;
/* 112 */   private final Object optOutLock = new Object();
/*     */ 
/* 117 */   private volatile BukkitTask task = null;
/*     */ 
/*     */   public Metrics(Plugin plugin) throws IOException {
/* 120 */     if (plugin == null) {
/* 121 */       throw new IllegalArgumentException("Plugin cannot be null");
/*     */     }
/*     */ 
/* 124 */     this.plugin = plugin;
/*     */ 
/* 127 */     this.configurationFile = getConfigFile();
/* 128 */     this.configuration = YamlConfiguration.loadConfiguration(this.configurationFile);
/*     */ 
/* 131 */     this.configuration.addDefault("opt-out", Boolean.valueOf(false));
/* 132 */     this.configuration.addDefault("guid", UUID.randomUUID().toString());
/* 133 */     this.configuration.addDefault("debug", Boolean.valueOf(false));
/*     */ 
/* 136 */     if (this.configuration.get("guid", null) == null) {
/* 137 */       this.configuration.options().header("http://mcstats.org").copyDefaults(true);
/* 138 */       this.configuration.save(this.configurationFile);
/*     */     }
/*     */ 
/* 142 */     this.guid = this.configuration.getString("guid");
/* 143 */     this.debug = this.configuration.getBoolean("debug", false);
/*     */   }
/*     */ 
/*     */   public Graph createGraph(String name)
/*     */   {
/* 154 */     if (name == null) {
/* 155 */       throw new IllegalArgumentException("Graph name cannot be null");
/*     */     }
/*     */ 
/* 159 */     Graph graph = new Graph(name, null);
/*     */ 
/* 162 */     this.graphs.add(graph);
/*     */ 
/* 165 */     return graph;
/*     */   }
/*     */ 
/*     */   public void addGraph(Graph graph)
/*     */   {
/* 174 */     if (graph == null) {
/* 175 */       throw new IllegalArgumentException("Graph cannot be null");
/*     */     }
/*     */ 
/* 178 */     this.graphs.add(graph);
/*     */   }
/*     */ 
/*     */   public boolean start()
/*     */   {
/* 189 */     synchronized (this.optOutLock)
/*     */     {
/* 191 */       if (isOptOut()) {
/* 192 */         return false;
/*     */       }
/*     */ 
/* 196 */       if (this.task != null) {
/* 197 */         return true;
/*     */       }
/*     */ 
/* 201 */       this.task = this.plugin.getServer().getScheduler().runTaskTimerAsynchronously(this.plugin, new Runnable()
/*     */       {
/* 203 */         private boolean firstPost = true;
/*     */ 
/*     */         public void run()
/*     */         {
/*     */           try {
/* 208 */             synchronized (Metrics.this.optOutLock)
/*     */             {
/* 210 */               if ((Metrics.this.isOptOut()) && (Metrics.this.task != null)) {
/* 211 */                 Metrics.this.task.cancel();
/* 212 */                 Metrics.this.task = null;
/*     */ 
/* 214 */                 for (Metrics.Graph graph : Metrics.this.graphs) {
/* 215 */                   graph.onOptOut();
/*     */                 }
/*     */ 
/*     */               }
/*     */ 
/*     */             }
/*     */ 
/* 223 */             Metrics.this.postPlugin(!this.firstPost);
/*     */ 
/* 227 */             this.firstPost = false;
/*     */           } catch (IOException e) {
/* 229 */             if (Metrics.this.debug)
/* 230 */               Bukkit.getLogger().log(Level.INFO, "[Metrics] " + e.getMessage());
/*     */           }
/*     */         }
/*     */       }
/*     */       , 0L, 18000L);
/*     */ 
/* 236 */       return true;
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean isOptOut()
/*     */   {
/* 246 */     synchronized (this.optOutLock)
/*     */     {
/*     */       try {
/* 249 */         this.configuration.load(getConfigFile());
/*     */       } catch (IOException ex) {
/* 251 */         if (this.debug) {
/* 252 */           Bukkit.getLogger().log(Level.INFO, "[Metrics] " + ex.getMessage());
/*     */         }
/* 254 */         return true;
/*     */       } catch (InvalidConfigurationException ex) {
/* 256 */         if (this.debug) {
/* 257 */           Bukkit.getLogger().log(Level.INFO, "[Metrics] " + ex.getMessage());
/*     */         }
/* 259 */         return true;
/*     */       }
/* 261 */       return this.configuration.getBoolean("opt-out", false);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void enable()
/*     */     throws IOException
/*     */   {
/* 272 */     synchronized (this.optOutLock)
/*     */     {
/* 274 */       if (isOptOut()) {
/* 275 */         this.configuration.set("opt-out", Boolean.valueOf(false));
/* 276 */         this.configuration.save(this.configurationFile);
/*     */       }
/*     */ 
/* 280 */       if (this.task == null)
/* 281 */         start();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void disable()
/*     */     throws IOException
/*     */   {
/* 293 */     synchronized (this.optOutLock)
/*     */     {
/* 295 */       if (!isOptOut()) {
/* 296 */         this.configuration.set("opt-out", Boolean.valueOf(true));
/* 297 */         this.configuration.save(this.configurationFile);
/*     */       }
/*     */ 
/* 301 */       if (this.task != null) {
/* 302 */         this.task.cancel();
/* 303 */         this.task = null;
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public File getConfigFile()
/*     */   {
/* 319 */     File pluginsFolder = this.plugin.getDataFolder().getParentFile();
/*     */ 
/* 322 */     return new File(new File(pluginsFolder, "PluginMetrics"), "config.yml");
/*     */   }
/*     */ 
/*     */   private void postPlugin(boolean isPing)
/*     */     throws IOException
/*     */   {
/* 330 */     PluginDescriptionFile description = this.plugin.getDescription();
/* 331 */     String pluginName = description.getName();
/* 332 */     boolean onlineMode = Bukkit.getServer().getOnlineMode();
/* 333 */     String pluginVersion = description.getVersion();
/* 334 */     String serverVersion = Bukkit.getVersion();
/* 335 */     int playersOnline = Bukkit.getServer().getOnlinePlayers().length;
/*     */ 
/* 340 */     StringBuilder json = new StringBuilder(1024);
/* 341 */     json.append('{');
/*     */ 
/* 344 */     appendJSONPair(json, "guid", this.guid);
/* 345 */     appendJSONPair(json, "plugin_version", pluginVersion);
/* 346 */     appendJSONPair(json, "server_version", serverVersion);
/* 347 */     appendJSONPair(json, "players_online", Integer.toString(playersOnline));
/*     */ 
/* 350 */     String osname = System.getProperty("os.name");
/* 351 */     String osarch = System.getProperty("os.arch");
/* 352 */     String osversion = System.getProperty("os.version");
/* 353 */     String java_version = System.getProperty("java.version");
/* 354 */     int coreCount = Runtime.getRuntime().availableProcessors();
/*     */ 
/* 357 */     if (osarch.equals("amd64")) {
/* 358 */       osarch = "x86_64";
/*     */     }
/*     */ 
/* 361 */     appendJSONPair(json, "osname", osname);
/* 362 */     appendJSONPair(json, "osarch", osarch);
/* 363 */     appendJSONPair(json, "osversion", osversion);
/* 364 */     appendJSONPair(json, "cores", Integer.toString(coreCount));
/* 365 */     appendJSONPair(json, "auth_mode", onlineMode ? "1" : "0");
/* 366 */     appendJSONPair(json, "java_version", java_version);
/*     */ 
/* 369 */     if (isPing) {
/* 370 */       appendJSONPair(json, "ping", "1");
/*     */     }
/*     */ 
/* 373 */     if (this.graphs.size() > 0) {
/* 374 */       synchronized (this.graphs) {
/* 375 */         json.append(',');
/* 376 */         json.append('"');
/* 377 */         json.append("graphs");
/* 378 */         json.append('"');
/* 379 */         json.append(':');
/* 380 */         json.append('{');
/*     */ 
/* 382 */         boolean firstGraph = true;
/*     */ 
/* 384 */         Iterator iter = this.graphs.iterator();
/*     */ 
/* 386 */         while (iter.hasNext()) {
/* 387 */           Graph graph = (Graph)iter.next();
/*     */ 
/* 389 */           StringBuilder graphJson = new StringBuilder();
/* 390 */           graphJson.append('{');
/*     */ 
/* 392 */           for (Plotter plotter : graph.getPlotters()) {
/* 393 */             appendJSONPair(graphJson, plotter.getColumnName(), Integer.toString(plotter.getValue()));
/*     */           }
/*     */ 
/* 396 */           graphJson.append('}');
/*     */ 
/* 398 */           if (!firstGraph) {
/* 399 */             json.append(',');
/*     */           }
/*     */ 
/* 402 */           json.append(escapeJSON(graph.getName()));
/* 403 */           json.append(':');
/* 404 */           json.append(graphJson);
/*     */ 
/* 406 */           firstGraph = false;
/*     */         }
/*     */ 
/* 409 */         json.append('}');
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 414 */     json.append('}');
/*     */ 
/* 417 */     URL url = new URL("http://report.mcstats.org" + String.format("/plugin/%s", new Object[] { urlEncode(pluginName) }));
/*     */     URLConnection connection;
/*     */     URLConnection connection;
/* 424 */     if (isMineshafterPresent())
/* 425 */       connection = url.openConnection(Proxy.NO_PROXY);
/*     */     else {
/* 427 */       connection = url.openConnection();
/*     */     }
/*     */ 
/* 431 */     byte[] uncompressed = json.toString().getBytes();
/* 432 */     byte[] compressed = gzip(json.toString());
/*     */ 
/* 435 */     connection.addRequestProperty("User-Agent", "MCStats/7");
/* 436 */     connection.addRequestProperty("Content-Type", "application/json");
/* 437 */     connection.addRequestProperty("Content-Encoding", "gzip");
/* 438 */     connection.addRequestProperty("Content-Length", Integer.toString(compressed.length));
/* 439 */     connection.addRequestProperty("Accept", "application/json");
/* 440 */     connection.addRequestProperty("Connection", "close");
/*     */ 
/* 442 */     connection.setDoOutput(true);
/*     */ 
/* 444 */     if (this.debug) {
/* 445 */       System.out.println("[Metrics] Prepared request for " + pluginName + " uncompressed=" + uncompressed.length + " compressed=" + compressed.length);
/*     */     }
/*     */ 
/* 449 */     OutputStream os = connection.getOutputStream();
/* 450 */     os.write(compressed);
/* 451 */     os.flush();
/*     */ 
/* 454 */     BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
/* 455 */     String response = reader.readLine();
/*     */ 
/* 458 */     os.close();
/* 459 */     reader.close();
/*     */ 
/* 461 */     if ((response == null) || (response.startsWith("ERR")) || (response.startsWith("7"))) {
/* 462 */       if (response == null)
/* 463 */         response = "null";
/* 464 */       else if (response.startsWith("7")) {
/* 465 */         response = response.substring(response.startsWith("7,") ? 2 : 1);
/*     */       }
/*     */ 
/* 468 */       throw new IOException(response);
/*     */     }
/*     */ 
/* 471 */     if ((response.equals("1")) || (response.contains("This is your first update this hour")))
/* 472 */       synchronized (this.graphs) {
/* 473 */         Iterator iter = this.graphs.iterator();
/*     */ 
/* 475 */         while (iter.hasNext()) {
/* 476 */           Graph graph = (Graph)iter.next();
/*     */ 
/* 478 */           for (Plotter plotter : graph.getPlotters())
/* 479 */             plotter.reset();
/*     */         }
/*     */       }
/*     */   }
/*     */ 
/*     */   public static byte[] gzip(String input)
/*     */   {
/* 494 */     ByteArrayOutputStream baos = new ByteArrayOutputStream();
/* 495 */     GZIPOutputStream gzos = null;
/*     */     try
/*     */     {
/* 498 */       gzos = new GZIPOutputStream(baos);
/* 499 */       gzos.write(input.getBytes("UTF-8"));
/*     */     } catch (IOException e) {
/* 501 */       e.printStackTrace();
/*     */     } finally {
/* 503 */       if (gzos != null) try {
/* 504 */           gzos.close();
/*     */         }
/*     */         catch (IOException ignore)
/*     */         {
/*     */         } 
/*     */     }
/* 509 */     return baos.toByteArray();
/*     */   }
/*     */ 
/*     */   private boolean isMineshafterPresent()
/*     */   {
/*     */     try
/*     */     {
/* 519 */       Class.forName("mineshafter.MineServer");
/* 520 */       return true; } catch (Exception e) {
/*     */     }
/* 522 */     return false;
/*     */   }
/*     */ 
/*     */   private static void appendJSONPair(StringBuilder json, String key, String value)
/*     */     throws UnsupportedEncodingException
/*     */   {
/* 535 */     boolean isValueNumeric = false;
/*     */     try
/*     */     {
/* 538 */       if ((value.equals("0")) || (!value.endsWith("0"))) {
/* 539 */         Double.parseDouble(value);
/* 540 */         isValueNumeric = true;
/*     */       }
/*     */     } catch (NumberFormatException e) {
/* 543 */       isValueNumeric = false;
/*     */     }
/*     */ 
/* 546 */     if (json.charAt(json.length() - 1) != '{') {
/* 547 */       json.append(',');
/*     */     }
/*     */ 
/* 550 */     json.append(escapeJSON(key));
/* 551 */     json.append(':');
/*     */ 
/* 553 */     if (isValueNumeric)
/* 554 */       json.append(value);
/*     */     else
/* 556 */       json.append(escapeJSON(value));
/*     */   }
/*     */ 
/*     */   private static String escapeJSON(String text)
/*     */   {
/* 567 */     StringBuilder builder = new StringBuilder();
/*     */ 
/* 569 */     builder.append('"');
/* 570 */     for (int index = 0; index < text.length(); index++) {
/* 571 */       char chr = text.charAt(index);
/*     */ 
/* 573 */       switch (chr) {
/*     */       case '"':
/*     */       case '\\':
/* 576 */         builder.append('\\');
/* 577 */         builder.append(chr);
/* 578 */         break;
/*     */       case '\b':
/* 580 */         builder.append("\\b");
/* 581 */         break;
/*     */       case '\t':
/* 583 */         builder.append("\\t");
/* 584 */         break;
/*     */       case '\n':
/* 586 */         builder.append("\\n");
/* 587 */         break;
/*     */       case '\r':
/* 589 */         builder.append("\\r");
/* 590 */         break;
/*     */       default:
/* 592 */         if (chr < ' ') {
/* 593 */           String t = "000" + Integer.toHexString(chr);
/* 594 */           builder.append("\\u" + t.substring(t.length() - 4));
/*     */         } else {
/* 596 */           builder.append(chr);
/*     */         }
/*     */         break;
/*     */       }
/*     */     }
/* 601 */     builder.append('"');
/*     */ 
/* 603 */     return builder.toString();
/*     */   }
/*     */ 
/*     */   private static String urlEncode(String text)
/*     */     throws UnsupportedEncodingException
/*     */   {
/* 613 */     return URLEncoder.encode(text, "UTF-8");
/*     */   }
/*     */ 
/*     */   public static abstract class Plotter
/*     */   {
/*     */     private final String name;
/*     */ 
/*     */     public Plotter()
/*     */     {
/* 708 */       this("Default");
/*     */     }
/*     */ 
/*     */     public Plotter(String name)
/*     */     {
/* 717 */       this.name = name;
/*     */     }
/*     */ 
/*     */     public abstract int getValue();
/*     */ 
/*     */     public String getColumnName()
/*     */     {
/* 735 */       return this.name;
/*     */     }
/*     */ 
/*     */     public void reset()
/*     */     {
/*     */     }
/*     */ 
/*     */     public int hashCode()
/*     */     {
/* 746 */       return getColumnName().hashCode();
/*     */     }
/*     */ 
/*     */     public boolean equals(Object object)
/*     */     {
/* 751 */       if (!(object instanceof Plotter)) {
/* 752 */         return false;
/*     */       }
/*     */ 
/* 755 */       Plotter plotter = (Plotter)object;
/* 756 */       return (plotter.name.equals(this.name)) && (plotter.getValue() == getValue());
/*     */     }
/*     */   }
/*     */ 
/*     */   public static class Graph
/*     */   {
/*     */     private final String name;
/* 630 */     private final Set<Metrics.Plotter> plotters = new LinkedHashSet();
/*     */ 
/*     */     private Graph(String name) {
/* 633 */       this.name = name;
/*     */     }
/*     */ 
/*     */     public String getName()
/*     */     {
/* 642 */       return this.name;
/*     */     }
/*     */ 
/*     */     public void addPlotter(Metrics.Plotter plotter)
/*     */     {
/* 651 */       this.plotters.add(plotter);
/*     */     }
/*     */ 
/*     */     public void removePlotter(Metrics.Plotter plotter)
/*     */     {
/* 660 */       this.plotters.remove(plotter);
/*     */     }
/*     */ 
/*     */     public Set<Metrics.Plotter> getPlotters()
/*     */     {
/* 669 */       return Collections.unmodifiableSet(this.plotters);
/*     */     }
/*     */ 
/*     */     public int hashCode()
/*     */     {
/* 674 */       return this.name.hashCode();
/*     */     }
/*     */ 
/*     */     public boolean equals(Object object)
/*     */     {
/* 679 */       if (!(object instanceof Graph)) {
/* 680 */         return false;
/*     */       }
/*     */ 
/* 683 */       Graph graph = (Graph)object;
/* 684 */       return graph.name.equals(this.name);
/*     */     }
/*     */ 
/*     */     protected void onOptOut()
/*     */     {
/*     */     }
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\ItemSounds.jar
 * Qualified Name:     jp.mydns.dyukusi.Metrics
 * JD-Core Version:    0.6.2
 */