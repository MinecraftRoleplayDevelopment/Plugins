/*     */ package com.comphenix.protocol.metrics;
/*     */ 
/*     */ import com.comphenix.protocol.utility.Util;
/*     */ import com.comphenix.protocol.utility.WrappedScheduler;
/*     */ import com.comphenix.protocol.utility.WrappedScheduler.TaskWrapper;
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
/*     */ import java.util.List;
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
/*     */ 
/*     */ public class Metrics
/*     */ {
/*     */   private static final int REVISION = 7;
/*     */   private static final String BASE_URL = "http://report.mcstats.org";
/*     */   private static final String REPORT_URL = "/plugin/%s";
/*     */   private static final int PING_INTERVAL = 15;
/*     */   private final Plugin plugin;
/*  89 */   private final Set<Graph> graphs = Collections.synchronizedSet(new HashSet());
/*     */   private final YamlConfiguration configuration;
/*     */   private final File configurationFile;
/*     */   private final String guid;
/*     */   private final boolean debug;
/* 114 */   private final Object optOutLock = new Object();
/*     */ 
/* 119 */   private volatile WrappedScheduler.TaskWrapper task = null;
/*     */ 
/*     */   public Metrics(Plugin plugin) throws IOException {
/* 122 */     if (plugin == null) {
/* 123 */       throw new IllegalArgumentException("Plugin cannot be null");
/*     */     }
/*     */ 
/* 126 */     this.plugin = plugin;
/*     */ 
/* 129 */     this.configurationFile = getConfigFile();
/* 130 */     this.configuration = YamlConfiguration.loadConfiguration(this.configurationFile);
/*     */ 
/* 133 */     this.configuration.addDefault("opt-out", Boolean.valueOf(false));
/* 134 */     this.configuration.addDefault("guid", UUID.randomUUID().toString());
/* 135 */     this.configuration.addDefault("debug", Boolean.valueOf(false));
/*     */ 
/* 138 */     if (this.configuration.get("guid", null) == null) {
/* 139 */       this.configuration.options().header("http://mcstats.org").copyDefaults(true);
/* 140 */       this.configuration.save(this.configurationFile);
/*     */     }
/*     */ 
/* 144 */     this.guid = this.configuration.getString("guid");
/* 145 */     this.debug = this.configuration.getBoolean("debug", false);
/*     */   }
/*     */ 
/*     */   public Graph createGraph(String name)
/*     */   {
/* 156 */     if (name == null) {
/* 157 */       throw new IllegalArgumentException("Graph name cannot be null");
/*     */     }
/*     */ 
/* 161 */     Graph graph = new Graph(name, null);
/*     */ 
/* 164 */     this.graphs.add(graph);
/*     */ 
/* 167 */     return graph;
/*     */   }
/*     */ 
/*     */   public void addGraph(Graph graph)
/*     */   {
/* 176 */     if (graph == null) {
/* 177 */       throw new IllegalArgumentException("Graph cannot be null");
/*     */     }
/*     */ 
/* 180 */     this.graphs.add(graph);
/*     */   }
/*     */ 
/*     */   public boolean start()
/*     */   {
/* 191 */     synchronized (this.optOutLock)
/*     */     {
/* 193 */       if (isOptOut()) {
/* 194 */         return false;
/*     */       }
/*     */ 
/* 198 */       if (this.task != null) {
/* 199 */         return true;
/*     */       }
/*     */ 
/* 203 */       this.task = WrappedScheduler.runAsynchronouslyRepeat(this.plugin, new Runnable()
/*     */       {
/* 205 */         private boolean firstPost = true;
/*     */ 
/*     */         public void run()
/*     */         {
/*     */           try
/*     */           {
/* 211 */             synchronized (Metrics.this.optOutLock)
/*     */             {
/* 213 */               if ((Metrics.this.isOptOut()) && (Metrics.this.task != null)) {
/* 214 */                 Metrics.this.task.cancel();
/* 215 */                 Metrics.this.task = null;
/*     */ 
/* 217 */                 for (Metrics.Graph graph : Metrics.this.graphs) {
/* 218 */                   graph.onOptOut();
/*     */                 }
/*     */ 
/*     */               }
/*     */ 
/*     */             }
/*     */ 
/* 226 */             Metrics.this.postPlugin(!this.firstPost);
/*     */ 
/* 230 */             this.firstPost = false;
/*     */           } catch (IOException e) {
/* 232 */             if (Metrics.this.debug)
/* 233 */               Bukkit.getLogger().log(Level.INFO, "[Metrics] " + e.getMessage());
/*     */           }
/*     */         }
/*     */       }
/*     */       , 0L, 18000L);
/*     */ 
/* 239 */       return true;
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean isOptOut()
/*     */   {
/* 249 */     synchronized (this.optOutLock)
/*     */     {
/*     */       try {
/* 252 */         this.configuration.load(getConfigFile());
/*     */       } catch (IOException ex) {
/* 254 */         if (this.debug) {
/* 255 */           Bukkit.getLogger().log(Level.INFO, "[Metrics] " + ex.getMessage());
/*     */         }
/* 257 */         return true;
/*     */       } catch (InvalidConfigurationException ex) {
/* 259 */         if (this.debug) {
/* 260 */           Bukkit.getLogger().log(Level.INFO, "[Metrics] " + ex.getMessage());
/*     */         }
/* 262 */         return true;
/*     */       }
/* 264 */       return this.configuration.getBoolean("opt-out", false);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void enable()
/*     */     throws IOException
/*     */   {
/* 275 */     synchronized (this.optOutLock)
/*     */     {
/* 277 */       if (isOptOut()) {
/* 278 */         this.configuration.set("opt-out", Boolean.valueOf(false));
/* 279 */         this.configuration.save(this.configurationFile);
/*     */       }
/*     */ 
/* 283 */       if (this.task == null)
/* 284 */         start();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void disable()
/*     */     throws IOException
/*     */   {
/* 296 */     synchronized (this.optOutLock)
/*     */     {
/* 298 */       if (!isOptOut()) {
/* 299 */         this.configuration.set("opt-out", Boolean.valueOf(true));
/* 300 */         this.configuration.save(this.configurationFile);
/*     */       }
/*     */ 
/* 304 */       if (this.task != null) {
/* 305 */         this.task.cancel();
/* 306 */         this.task = null;
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public File getConfigFile()
/*     */   {
/* 322 */     File pluginsFolder = this.plugin.getDataFolder().getParentFile();
/*     */ 
/* 325 */     return new File(new File(pluginsFolder, "PluginMetrics"), "config.yml");
/*     */   }
/*     */ 
/*     */   private void postPlugin(boolean isPing)
/*     */     throws IOException
/*     */   {
/* 333 */     PluginDescriptionFile description = this.plugin.getDescription();
/* 334 */     String pluginName = description.getName();
/* 335 */     boolean onlineMode = Bukkit.getServer().getOnlineMode();
/* 336 */     String pluginVersion = trimVersion(description.getVersion());
/* 337 */     String serverVersion = Bukkit.getVersion();
/* 338 */     int playersOnline = Util.getOnlinePlayers().size();
/*     */ 
/* 343 */     StringBuilder json = new StringBuilder(1024);
/* 344 */     json.append('{');
/*     */ 
/* 347 */     appendJSONPair(json, "guid", this.guid);
/* 348 */     appendJSONPair(json, "plugin_version", pluginVersion);
/* 349 */     appendJSONPair(json, "server_version", serverVersion);
/* 350 */     appendJSONPair(json, "players_online", Integer.toString(playersOnline));
/*     */ 
/* 353 */     String osname = System.getProperty("os.name");
/* 354 */     String osarch = System.getProperty("os.arch");
/* 355 */     String osversion = System.getProperty("os.version");
/* 356 */     String java_version = System.getProperty("java.version");
/* 357 */     int coreCount = Runtime.getRuntime().availableProcessors();
/*     */ 
/* 360 */     if (osarch.equals("amd64")) {
/* 361 */       osarch = "x86_64";
/*     */     }
/*     */ 
/* 364 */     appendJSONPair(json, "osname", osname);
/* 365 */     appendJSONPair(json, "osarch", osarch);
/* 366 */     appendJSONPair(json, "osversion", osversion);
/* 367 */     appendJSONPair(json, "cores", Integer.toString(coreCount));
/* 368 */     appendJSONPair(json, "auth_mode", onlineMode ? "1" : "0");
/* 369 */     appendJSONPair(json, "java_version", java_version);
/*     */ 
/* 372 */     if (isPing) {
/* 373 */       appendJSONPair(json, "ping", "1");
/*     */     }
/*     */ 
/* 376 */     if (this.graphs.size() > 0) {
/* 377 */       synchronized (this.graphs) {
/* 378 */         json.append(',');
/* 379 */         json.append('"');
/* 380 */         json.append("graphs");
/* 381 */         json.append('"');
/* 382 */         json.append(':');
/* 383 */         json.append('{');
/*     */ 
/* 385 */         boolean firstGraph = true;
/*     */ 
/* 387 */         Iterator iter = this.graphs.iterator();
/*     */ 
/* 389 */         while (iter.hasNext()) {
/* 390 */           Graph graph = (Graph)iter.next();
/*     */ 
/* 392 */           StringBuilder graphJson = new StringBuilder();
/* 393 */           graphJson.append('{');
/*     */ 
/* 395 */           for (Plotter plotter : graph.getPlotters()) {
/* 396 */             appendJSONPair(graphJson, plotter.getColumnName(), Integer.toString(plotter.getValue()));
/*     */           }
/*     */ 
/* 399 */           graphJson.append('}');
/*     */ 
/* 401 */           if (!firstGraph) {
/* 402 */             json.append(',');
/*     */           }
/*     */ 
/* 405 */           json.append(escapeJSON(graph.getName()));
/* 406 */           json.append(':');
/* 407 */           json.append(graphJson);
/*     */ 
/* 409 */           firstGraph = false;
/*     */         }
/*     */ 
/* 412 */         json.append('}');
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 417 */     json.append('}');
/*     */ 
/* 420 */     URL url = new URL("http://report.mcstats.org" + String.format("/plugin/%s", new Object[] { urlEncode(pluginName) }));
/*     */     URLConnection connection;
/*     */     URLConnection connection;
/* 427 */     if (isMineshafterPresent())
/* 428 */       connection = url.openConnection(Proxy.NO_PROXY);
/*     */     else {
/* 430 */       connection = url.openConnection();
/*     */     }
/*     */ 
/* 434 */     byte[] uncompressed = json.toString().getBytes();
/* 435 */     byte[] compressed = gzip(json.toString());
/*     */ 
/* 438 */     connection.addRequestProperty("User-Agent", "MCStats/7");
/* 439 */     connection.addRequestProperty("Content-Type", "application/json");
/* 440 */     connection.addRequestProperty("Content-Encoding", "gzip");
/* 441 */     connection.addRequestProperty("Content-Length", Integer.toString(compressed.length));
/* 442 */     connection.addRequestProperty("Accept", "application/json");
/* 443 */     connection.addRequestProperty("Connection", "close");
/*     */ 
/* 445 */     connection.setDoOutput(true);
/*     */ 
/* 447 */     if (this.debug) {
/* 448 */       System.out.println("[Metrics] Prepared request for " + pluginName + " uncompressed=" + uncompressed.length + " compressed=" + compressed.length);
/*     */     }
/*     */ 
/* 452 */     OutputStream os = connection.getOutputStream();
/* 453 */     os.write(compressed);
/* 454 */     os.flush();
/*     */ 
/* 457 */     BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
/* 458 */     String response = reader.readLine();
/*     */ 
/* 461 */     os.close();
/* 462 */     reader.close();
/*     */ 
/* 464 */     if ((response == null) || (response.startsWith("ERR")) || (response.startsWith("7"))) {
/* 465 */       if (response == null)
/* 466 */         response = "null";
/* 467 */       else if (response.startsWith("7")) {
/* 468 */         response = response.substring(response.startsWith("7,") ? 2 : 1);
/*     */       }
/*     */ 
/* 471 */       throw new IOException(response);
/*     */     }
/*     */ 
/* 474 */     if ((response.equals("1")) || (response.contains("This is your first update this hour")))
/* 475 */       synchronized (this.graphs) {
/* 476 */         Iterator iter = this.graphs.iterator();
/*     */ 
/* 478 */         while (iter.hasNext()) {
/* 479 */           Graph graph = (Graph)iter.next();
/*     */ 
/* 481 */           for (Plotter plotter : graph.getPlotters())
/* 482 */             plotter.reset();
/*     */         }
/*     */       }
/*     */   }
/*     */ 
/*     */   private String trimVersion(String version)
/*     */   {
/* 493 */     return version.contains("-b") ? version.substring(0, version.indexOf("-b")) : version;
/*     */   }
/*     */ 
/*     */   public static byte[] gzip(String input)
/*     */   {
/* 503 */     ByteArrayOutputStream baos = new ByteArrayOutputStream();
/* 504 */     GZIPOutputStream gzos = null;
/*     */     try
/*     */     {
/* 507 */       gzos = new GZIPOutputStream(baos);
/* 508 */       gzos.write(input.getBytes("UTF-8"));
/*     */     } catch (IOException e) {
/* 510 */       e.printStackTrace();
/*     */     } finally {
/* 512 */       if (gzos != null) try {
/* 513 */           gzos.close();
/*     */         }
/*     */         catch (IOException ignore)
/*     */         {
/*     */         } 
/*     */     }
/* 518 */     return baos.toByteArray();
/*     */   }
/*     */ 
/*     */   private boolean isMineshafterPresent()
/*     */   {
/*     */     try
/*     */     {
/* 528 */       Class.forName("mineshafter.MineServer");
/* 529 */       return true; } catch (Exception e) {
/*     */     }
/* 531 */     return false;
/*     */   }
/*     */ 
/*     */   private static void appendJSONPair(StringBuilder json, String key, String value)
/*     */     throws UnsupportedEncodingException
/*     */   {
/* 544 */     boolean isValueNumeric = false;
/*     */     try
/*     */     {
/* 547 */       if ((value.equals("0")) || (!value.endsWith("0"))) {
/* 548 */         Double.parseDouble(value);
/* 549 */         isValueNumeric = true;
/*     */       }
/*     */     } catch (NumberFormatException e) {
/* 552 */       isValueNumeric = false;
/*     */     }
/*     */ 
/* 555 */     if (json.charAt(json.length() - 1) != '{') {
/* 556 */       json.append(',');
/*     */     }
/*     */ 
/* 559 */     json.append(escapeJSON(key));
/* 560 */     json.append(':');
/*     */ 
/* 562 */     if (isValueNumeric)
/* 563 */       json.append(value);
/*     */     else
/* 565 */       json.append(escapeJSON(value));
/*     */   }
/*     */ 
/*     */   private static String escapeJSON(String text)
/*     */   {
/* 576 */     StringBuilder builder = new StringBuilder();
/*     */ 
/* 578 */     builder.append('"');
/* 579 */     for (int index = 0; index < text.length(); index++) {
/* 580 */       char chr = text.charAt(index);
/*     */ 
/* 582 */       switch (chr) {
/*     */       case '"':
/*     */       case '\\':
/* 585 */         builder.append('\\');
/* 586 */         builder.append(chr);
/* 587 */         break;
/*     */       case '\b':
/* 589 */         builder.append("\\b");
/* 590 */         break;
/*     */       case '\t':
/* 592 */         builder.append("\\t");
/* 593 */         break;
/*     */       case '\n':
/* 595 */         builder.append("\\n");
/* 596 */         break;
/*     */       case '\r':
/* 598 */         builder.append("\\r");
/* 599 */         break;
/*     */       default:
/* 601 */         if (chr < ' ') {
/* 602 */           String t = "000" + Integer.toHexString(chr);
/* 603 */           builder.append("\\u" + t.substring(t.length() - 4));
/*     */         } else {
/* 605 */           builder.append(chr);
/*     */         }
/*     */         break;
/*     */       }
/*     */     }
/* 610 */     builder.append('"');
/*     */ 
/* 612 */     return builder.toString();
/*     */   }
/*     */ 
/*     */   private static String urlEncode(String text)
/*     */     throws UnsupportedEncodingException
/*     */   {
/* 622 */     return URLEncoder.encode(text, "UTF-8");
/*     */   }
/*     */ 
/*     */   public static abstract class Plotter
/*     */   {
/*     */     private final String name;
/*     */ 
/*     */     public Plotter()
/*     */     {
/* 717 */       this("Default");
/*     */     }
/*     */ 
/*     */     public Plotter(String name)
/*     */     {
/* 726 */       this.name = name;
/*     */     }
/*     */ 
/*     */     public abstract int getValue();
/*     */ 
/*     */     public String getColumnName()
/*     */     {
/* 744 */       return this.name;
/*     */     }
/*     */ 
/*     */     public void reset()
/*     */     {
/*     */     }
/*     */ 
/*     */     public int hashCode()
/*     */     {
/* 755 */       return getColumnName().hashCode();
/*     */     }
/*     */ 
/*     */     public boolean equals(Object object)
/*     */     {
/* 760 */       if (!(object instanceof Plotter)) {
/* 761 */         return false;
/*     */       }
/*     */ 
/* 764 */       Plotter plotter = (Plotter)object;
/* 765 */       return (plotter.name.equals(this.name)) && (plotter.getValue() == getValue());
/*     */     }
/*     */   }
/*     */ 
/*     */   public static class Graph
/*     */   {
/*     */     private final String name;
/* 639 */     private final Set<Metrics.Plotter> plotters = new LinkedHashSet();
/*     */ 
/*     */     private Graph(String name) {
/* 642 */       this.name = name;
/*     */     }
/*     */ 
/*     */     public String getName()
/*     */     {
/* 651 */       return this.name;
/*     */     }
/*     */ 
/*     */     public void addPlotter(Metrics.Plotter plotter)
/*     */     {
/* 660 */       this.plotters.add(plotter);
/*     */     }
/*     */ 
/*     */     public void removePlotter(Metrics.Plotter plotter)
/*     */     {
/* 669 */       this.plotters.remove(plotter);
/*     */     }
/*     */ 
/*     */     public Set<Metrics.Plotter> getPlotters()
/*     */     {
/* 678 */       return Collections.unmodifiableSet(this.plotters);
/*     */     }
/*     */ 
/*     */     public int hashCode()
/*     */     {
/* 683 */       return this.name.hashCode();
/*     */     }
/*     */ 
/*     */     public boolean equals(Object object)
/*     */     {
/* 688 */       if (!(object instanceof Graph)) {
/* 689 */         return false;
/*     */       }
/*     */ 
/* 692 */       Graph graph = (Graph)object;
/* 693 */       return graph.name.equals(this.name);
/*     */     }
/*     */ 
/*     */     protected void onOptOut()
/*     */     {
/*     */     }
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.metrics.Metrics
 * JD-Core Version:    0.6.2
 */