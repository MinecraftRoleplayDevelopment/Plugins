/*     */ package com.zettelnet.armorweight.lib.org.mcstats;
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
/* 157 */     if (name == null) {
/* 158 */       throw new IllegalArgumentException("Graph name cannot be null");
/*     */     }
/*     */ 
/* 162 */     Graph graph = new Graph(name, null);
/*     */ 
/* 165 */     this.graphs.add(graph);
/*     */ 
/* 168 */     return graph;
/*     */   }
/*     */ 
/*     */   public void addGraph(Graph graph)
/*     */   {
/* 179 */     if (graph == null) {
/* 180 */       throw new IllegalArgumentException("Graph cannot be null");
/*     */     }
/*     */ 
/* 183 */     this.graphs.add(graph);
/*     */   }
/*     */ 
/*     */   public boolean start()
/*     */   {
/* 195 */     synchronized (this.optOutLock)
/*     */     {
/* 197 */       if (isOptOut()) {
/* 198 */         return false;
/*     */       }
/*     */ 
/* 202 */       if (this.task != null) {
/* 203 */         return true;
/*     */       }
/*     */ 
/* 207 */       this.task = this.plugin.getServer().getScheduler().runTaskTimerAsynchronously(this.plugin, new Object()
/*     */       {
/* 209 */         private boolean firstPost = true;
/*     */ 
/*     */         public void run()
/*     */         {
/*     */           try
/*     */           {
/* 216 */             synchronized (Metrics.this.optOutLock)
/*     */             {
/* 219 */               if ((Metrics.this.isOptOut()) && (Metrics.this.task != null)) {
/* 220 */                 Metrics.this.task.cancel();
/* 221 */                 Metrics.this.task = null;
/*     */ 
/* 224 */                 for (Metrics.Graph graph : Metrics.this.graphs) {
/* 225 */                   graph.onOptOut();
/*     */                 }
/*     */ 
/*     */               }
/*     */ 
/*     */             }
/*     */ 
/* 235 */             Metrics.this.postPlugin(!this.firstPost);
/*     */ 
/* 239 */             this.firstPost = false;
/*     */           } catch (IOException e) {
/* 241 */             if (Metrics.this.debug)
/* 242 */               Bukkit.getLogger().log(Level.INFO, "[Metrics] " + e.getMessage());
/*     */           }
/*     */         }
/*     */       }
/*     */       , 0L, 18000L);
/*     */ 
/* 248 */       return true;
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean isOptOut()
/*     */   {
/* 258 */     synchronized (this.optOutLock)
/*     */     {
/*     */       try {
/* 261 */         this.configuration.load(getConfigFile());
/*     */       } catch (IOException ex) {
/* 263 */         if (this.debug) {
/* 264 */           Bukkit.getLogger().log(Level.INFO, new StringBuilder().append("[Metrics] ").append(ex.getMessage()).toString());
/*     */         }
/* 266 */         return true;
/*     */       } catch (InvalidConfigurationException ex) {
/* 268 */         if (this.debug) {
/* 269 */           Bukkit.getLogger().log(Level.INFO, new StringBuilder().append("[Metrics] ").append(ex.getMessage()).toString());
/*     */         }
/* 271 */         return true;
/*     */       }
/* 273 */       return this.configuration.getBoolean("opt-out", false);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void enable()
/*     */     throws IOException
/*     */   {
/* 286 */     synchronized (this.optOutLock)
/*     */     {
/* 289 */       if (isOptOut()) {
/* 290 */         this.configuration.set("opt-out", Boolean.valueOf(false));
/* 291 */         this.configuration.save(this.configurationFile);
/*     */       }
/*     */ 
/* 295 */       if (this.task == null)
/* 296 */         start();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void disable()
/*     */     throws IOException
/*     */   {
/* 310 */     synchronized (this.optOutLock)
/*     */     {
/* 313 */       if (!isOptOut()) {
/* 314 */         this.configuration.set("opt-out", Boolean.valueOf(true));
/* 315 */         this.configuration.save(this.configurationFile);
/*     */       }
/*     */ 
/* 319 */       if (this.task != null) {
/* 320 */         this.task.cancel();
/* 321 */         this.task = null;
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public File getConfigFile()
/*     */   {
/* 339 */     File pluginsFolder = this.plugin.getDataFolder().getParentFile();
/*     */ 
/* 342 */     return new File(new File(pluginsFolder, "PluginMetrics"), "config.yml");
/*     */   }
/*     */ 
/*     */   private void postPlugin(boolean isPing)
/*     */     throws IOException
/*     */   {
/* 350 */     PluginDescriptionFile description = this.plugin.getDescription();
/* 351 */     String pluginName = description.getName();
/* 352 */     boolean onlineMode = Bukkit.getServer().getOnlineMode();
/*     */ 
/* 356 */     String pluginVersion = description.getVersion();
/* 357 */     String serverVersion = Bukkit.getVersion();
/* 358 */     int playersOnline = Bukkit.getServer().getOnlinePlayers().size();
/*     */ 
/* 364 */     StringBuilder json = new StringBuilder(1024);
/* 365 */     json.append('{');
/*     */ 
/* 369 */     appendJSONPair(json, "guid", this.guid);
/* 370 */     appendJSONPair(json, "plugin_version", pluginVersion);
/* 371 */     appendJSONPair(json, "server_version", serverVersion);
/* 372 */     appendJSONPair(json, "players_online", Integer.toString(playersOnline));
/*     */ 
/* 375 */     String osname = System.getProperty("os.name");
/* 376 */     String osarch = System.getProperty("os.arch");
/* 377 */     String osversion = System.getProperty("os.version");
/* 378 */     String java_version = System.getProperty("java.version");
/* 379 */     int coreCount = Runtime.getRuntime().availableProcessors();
/*     */ 
/* 382 */     if (osarch.equals("amd64")) {
/* 383 */       osarch = "x86_64";
/*     */     }
/*     */ 
/* 386 */     appendJSONPair(json, "osname", osname);
/* 387 */     appendJSONPair(json, "osarch", osarch);
/* 388 */     appendJSONPair(json, "osversion", osversion);
/* 389 */     appendJSONPair(json, "cores", Integer.toString(coreCount));
/* 390 */     appendJSONPair(json, "auth_mode", onlineMode ? "1" : "0");
/* 391 */     appendJSONPair(json, "java_version", java_version);
/*     */ 
/* 394 */     if (isPing) {
/* 395 */       appendJSONPair(json, "ping", "1");
/*     */     }
/*     */ 
/* 398 */     if (this.graphs.size() > 0) {
/* 399 */       synchronized (this.graphs) {
/* 400 */         json.append(',');
/* 401 */         json.append('"');
/* 402 */         json.append("graphs");
/* 403 */         json.append('"');
/* 404 */         json.append(':');
/* 405 */         json.append('{');
/*     */ 
/* 407 */         boolean firstGraph = true;
/*     */ 
/* 409 */         Iterator iter = this.graphs.iterator();
/*     */ 
/* 411 */         while (iter.hasNext()) {
/* 412 */           Graph graph = (Graph)iter.next();
/*     */ 
/* 414 */           StringBuilder graphJson = new StringBuilder();
/* 415 */           graphJson.append('{');
/*     */ 
/* 417 */           for (Plotter plotter : graph.getPlotters()) {
/* 418 */             appendJSONPair(graphJson, plotter.getColumnName(), Integer.toString(plotter.getValue()));
/*     */           }
/*     */ 
/* 421 */           graphJson.append('}');
/*     */ 
/* 423 */           if (!firstGraph) {
/* 424 */             json.append(',');
/*     */           }
/*     */ 
/* 427 */           json.append(escapeJSON(graph.getName()));
/* 428 */           json.append(':');
/* 429 */           json.append(graphJson);
/*     */ 
/* 431 */           firstGraph = false;
/*     */         }
/*     */ 
/* 434 */         json.append('}');
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 439 */     json.append('}');
/*     */ 
/* 442 */     URL url = new URL(new StringBuilder().append("http://report.mcstats.org").append(String.format("/plugin/%s", new Object[] { urlEncode(pluginName) })).toString());
/*     */     URLConnection connection;
/*     */     URLConnection connection;
/* 449 */     if (isMineshafterPresent())
/* 450 */       connection = url.openConnection(Proxy.NO_PROXY);
/*     */     else {
/* 452 */       connection = url.openConnection();
/*     */     }
/*     */ 
/* 455 */     byte[] uncompressed = json.toString().getBytes();
/* 456 */     byte[] compressed = gzip(json.toString());
/*     */ 
/* 459 */     connection.addRequestProperty("User-Agent", "MCStats/7");
/* 460 */     connection.addRequestProperty("Content-Type", "application/json");
/* 461 */     connection.addRequestProperty("Content-Encoding", "gzip");
/* 462 */     connection.addRequestProperty("Content-Length", Integer.toString(compressed.length));
/* 463 */     connection.addRequestProperty("Accept", "application/json");
/* 464 */     connection.addRequestProperty("Connection", "close");
/*     */ 
/* 466 */     connection.setDoOutput(true);
/*     */ 
/* 468 */     if (this.debug) {
/* 469 */       System.out.println(new StringBuilder().append("[Metrics] Prepared request for ").append(pluginName).append(" uncompressed=").append(uncompressed.length).append(" compressed=").append(compressed.length).toString());
/*     */     }
/*     */ 
/* 473 */     OutputStream os = connection.getOutputStream();
/* 474 */     os.write(compressed);
/* 475 */     os.flush();
/*     */ 
/* 478 */     BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
/* 479 */     String response = reader.readLine();
/*     */ 
/* 482 */     os.close();
/* 483 */     reader.close();
/*     */ 
/* 485 */     if ((response == null) || (response.startsWith("ERR")) || (response.startsWith("7"))) {
/* 486 */       if (response == null)
/* 487 */         response = "null";
/* 488 */       else if (response.startsWith("7")) {
/* 489 */         response = response.substring(response.startsWith("7,") ? 2 : 1);
/*     */       }
/*     */ 
/* 492 */       throw new IOException(response);
/*     */     }
/*     */ 
/* 495 */     if ((response.equals("1")) || (response.contains("This is your first update this hour")))
/* 496 */       synchronized (this.graphs) {
/* 497 */         Iterator iter = this.graphs.iterator();
/*     */ 
/* 499 */         while (iter.hasNext()) {
/* 500 */           Graph graph = (Graph)iter.next();
/*     */ 
/* 502 */           for (Plotter plotter : graph.getPlotters())
/* 503 */             plotter.reset();
/*     */         }
/*     */       }
/*     */   }
/*     */ 
/*     */   public static byte[] gzip(String input)
/*     */   {
/* 518 */     ByteArrayOutputStream baos = new ByteArrayOutputStream();
/* 519 */     GZIPOutputStream gzos = null;
/*     */     try
/*     */     {
/* 522 */       gzos = new GZIPOutputStream(baos);
/* 523 */       gzos.write(input.getBytes("UTF-8"));
/*     */     } catch (IOException e) {
/* 525 */       e.printStackTrace();
/*     */     } finally {
/* 527 */       if (gzos != null)
/*     */         try {
/* 529 */           gzos.close();
/*     */         }
/*     */         catch (IOException ignore) {
/*     */         }
/*     */     }
/* 534 */     return baos.toByteArray();
/*     */   }
/*     */ 
/*     */   private boolean isMineshafterPresent()
/*     */   {
/*     */     try
/*     */     {
/* 545 */       Class.forName("mineshafter.MineServer");
/* 546 */       return true; } catch (Exception e) {
/*     */     }
/* 548 */     return false;
/*     */   }
/*     */ 
/*     */   private static void appendJSONPair(StringBuilder json, String key, String value)
/*     */     throws UnsupportedEncodingException
/*     */   {
/* 561 */     boolean isValueNumeric = false;
/*     */     try
/*     */     {
/* 564 */       if ((value.equals("0")) || (!value.endsWith("0"))) {
/* 565 */         Double.parseDouble(value);
/* 566 */         isValueNumeric = true;
/*     */       }
/*     */     } catch (NumberFormatException e) {
/* 569 */       isValueNumeric = false;
/*     */     }
/*     */ 
/* 572 */     if (json.charAt(json.length() - 1) != '{') {
/* 573 */       json.append(',');
/*     */     }
/*     */ 
/* 576 */     json.append(escapeJSON(key));
/* 577 */     json.append(':');
/*     */ 
/* 579 */     if (isValueNumeric)
/* 580 */       json.append(value);
/*     */     else
/* 582 */       json.append(escapeJSON(value));
/*     */   }
/*     */ 
/*     */   private static String escapeJSON(String text)
/*     */   {
/* 593 */     StringBuilder builder = new StringBuilder();
/*     */ 
/* 595 */     builder.append('"');
/* 596 */     for (int index = 0; index < text.length(); index++) {
/* 597 */       char chr = text.charAt(index);
/*     */ 
/* 599 */       switch (chr) {
/*     */       case '"':
/*     */       case '\\':
/* 602 */         builder.append('\\');
/* 603 */         builder.append(chr);
/* 604 */         break;
/*     */       case '\b':
/* 606 */         builder.append("\\b");
/* 607 */         break;
/*     */       case '\t':
/* 609 */         builder.append("\\t");
/* 610 */         break;
/*     */       case '\n':
/* 612 */         builder.append("\\n");
/* 613 */         break;
/*     */       case '\r':
/* 615 */         builder.append("\\r");
/* 616 */         break;
/*     */       default:
/* 618 */         if (chr < ' ') {
/* 619 */           String t = new StringBuilder().append("000").append(Integer.toHexString(chr)).toString();
/* 620 */           builder.append(new StringBuilder().append("\\u").append(t.substring(t.length() - 4)).toString());
/*     */         } else {
/* 622 */           builder.append(chr);
/*     */         }
/*     */         break;
/*     */       }
/*     */     }
/* 627 */     builder.append('"');
/*     */ 
/* 629 */     return builder.toString();
/*     */   }
/*     */ 
/*     */   private static String urlEncode(String text)
/*     */     throws UnsupportedEncodingException
/*     */   {
/* 640 */     return URLEncoder.encode(text, "UTF-8");
/*     */   }
/*     */ 
/*     */   public static abstract class Plotter
/*     */   {
/*     */     private final String name;
/*     */ 
/*     */     public Plotter()
/*     */     {
/* 738 */       this("Default");
/*     */     }
/*     */ 
/*     */     public Plotter(String name)
/*     */     {
/* 749 */       this.name = name;
/*     */     }
/*     */ 
/*     */     public abstract int getValue();
/*     */ 
/*     */     public String getColumnName()
/*     */     {
/* 769 */       return this.name;
/*     */     }
/*     */ 
/*     */     public void reset()
/*     */     {
/*     */     }
/*     */ 
/*     */     public int hashCode()
/*     */     {
/* 780 */       return getColumnName().hashCode();
/*     */     }
/*     */ 
/*     */     public boolean equals(Object object)
/*     */     {
/* 785 */       if (!(object instanceof Plotter)) {
/* 786 */         return false;
/*     */       }
/*     */ 
/* 789 */       Plotter plotter = (Plotter)object;
/* 790 */       return (plotter.name.equals(this.name)) && (plotter.getValue() == getValue());
/*     */     }
/*     */   }
/*     */ 
/*     */   public static class Graph
/*     */   {
/*     */     private final String name;
/* 657 */     private final Set<Metrics.Plotter> plotters = new LinkedHashSet();
/*     */ 
/*     */     private Graph(String name) {
/* 660 */       this.name = name;
/*     */     }
/*     */ 
/*     */     public String getName()
/*     */     {
/* 669 */       return this.name;
/*     */     }
/*     */ 
/*     */     public void addPlotter(Metrics.Plotter plotter)
/*     */     {
/* 679 */       this.plotters.add(plotter);
/*     */     }
/*     */ 
/*     */     public void removePlotter(Metrics.Plotter plotter)
/*     */     {
/* 689 */       this.plotters.remove(plotter);
/*     */     }
/*     */ 
/*     */     public Set<Metrics.Plotter> getPlotters()
/*     */     {
/* 698 */       return Collections.unmodifiableSet(this.plotters);
/*     */     }
/*     */ 
/*     */     public int hashCode()
/*     */     {
/* 703 */       return this.name.hashCode();
/*     */     }
/*     */ 
/*     */     public boolean equals(Object object)
/*     */     {
/* 708 */       if (!(object instanceof Graph)) {
/* 709 */         return false;
/*     */       }
/*     */ 
/* 712 */       Graph graph = (Graph)object;
/* 713 */       return graph.name.equals(this.name);
/*     */     }
/*     */ 
/*     */     protected void onOptOut()
/*     */     {
/*     */     }
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\ArmorWeight.jar
 * Qualified Name:     com.zettelnet.armorweight.lib.org.mcstats.Metrics
 * JD-Core Version:    0.6.2
 */