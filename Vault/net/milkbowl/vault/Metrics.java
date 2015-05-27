/*     */ package net.milkbowl.vault;
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
/*     */ import net.milkbowl.vault.chat.Chat;
/*     */ import net.milkbowl.vault.economy.Economy;
/*     */ import net.milkbowl.vault.permission.Permission;
/*     */ import org.bukkit.Bukkit;
/*     */ import org.bukkit.Server;
/*     */ import org.bukkit.configuration.InvalidConfigurationException;
/*     */ import org.bukkit.configuration.file.YamlConfiguration;
/*     */ import org.bukkit.configuration.file.YamlConfigurationOptions;
/*     */ import org.bukkit.plugin.Plugin;
/*     */ import org.bukkit.plugin.PluginDescriptionFile;
/*     */ import org.bukkit.plugin.RegisteredServiceProvider;
/*     */ import org.bukkit.plugin.ServicesManager;
/*     */ import org.bukkit.scheduler.BukkitScheduler;
/*     */ import org.bukkit.scheduler.BukkitTask;
/*     */ 
/*     */ public class Metrics
/*     */ {
/*     */   private static final int REVISION = 8;
/*     */   private static final String BASE_URL = "http://report.mcstats.org";
/*     */   private static final String REPORT_URL = "/plugin/%s";
/*     */   private static final int PING_INTERVAL = 15;
/*     */   private final Plugin plugin;
/*  93 */   private final Set<Graph> graphs = Collections.synchronizedSet(new HashSet());
/*     */   private final YamlConfiguration configuration;
/*     */   private final File configurationFile;
/*     */   private final String guid;
/*     */   private final boolean debug;
/* 118 */   private final Object optOutLock = new Object();
/*     */ 
/* 123 */   private volatile BukkitTask task = null;
/*     */ 
/*     */   public Metrics(Plugin plugin) throws IOException {
/* 126 */     if (plugin == null) {
/* 127 */       throw new IllegalArgumentException("Plugin cannot be null");
/*     */     }
/*     */ 
/* 130 */     this.plugin = plugin;
/*     */ 
/* 133 */     this.configurationFile = getConfigFile();
/* 134 */     this.configuration = YamlConfiguration.loadConfiguration(this.configurationFile);
/*     */ 
/* 137 */     this.configuration.addDefault("opt-out", Boolean.valueOf(false));
/* 138 */     this.configuration.addDefault("guid", UUID.randomUUID().toString());
/* 139 */     this.configuration.addDefault("debug", Boolean.valueOf(false));
/*     */ 
/* 142 */     if (this.configuration.get("guid", null) == null) {
/* 143 */       this.configuration.options().header("http://mcstats.org").copyDefaults(true);
/* 144 */       this.configuration.save(this.configurationFile);
/*     */     }
/*     */ 
/* 148 */     this.guid = this.configuration.getString("guid");
/* 149 */     this.debug = this.configuration.getBoolean("debug", false);
/*     */   }
/*     */ 
/*     */   public void findCustomData()
/*     */   {
/* 154 */     Graph econGraph = createGraph("Economy");
/* 155 */     RegisteredServiceProvider rspEcon = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
/* 156 */     Economy econ = null;
/* 157 */     if (rspEcon != null) {
/* 158 */       econ = (Economy)rspEcon.getProvider();
/*     */     }
/* 160 */     String econName = econ != null ? econ.getName() : "No Economy";
/* 161 */     econGraph.addPlotter(new Plotter(econName)
/*     */     {
/*     */       public int getValue()
/*     */       {
/* 165 */         return 1;
/*     */       }
/*     */     });
/* 170 */     Graph permGraph = createGraph("Permission");
/* 171 */     String permName = ((Permission)Bukkit.getServer().getServicesManager().getRegistration(Permission.class).getProvider()).getName();
/* 172 */     permGraph.addPlotter(new Plotter(permName)
/*     */     {
/*     */       public int getValue()
/*     */       {
/* 176 */         return 1;
/*     */       }
/*     */     });
/* 181 */     Graph chatGraph = createGraph("Chat");
/* 182 */     RegisteredServiceProvider rspChat = Bukkit.getServer().getServicesManager().getRegistration(Chat.class);
/* 183 */     Chat chat = null;
/* 184 */     if (rspChat != null) {
/* 185 */       chat = (Chat)rspChat.getProvider();
/*     */     }
/* 187 */     String chatName = chat != null ? chat.getName() : "No Chat";
/*     */ 
/* 189 */     chatGraph.addPlotter(new Plotter(chatName)
/*     */     {
/*     */       public int getValue()
/*     */       {
/* 193 */         return 1;
/*     */       }
/*     */     });
/*     */   }
/*     */ 
/*     */   public Graph createGraph(String name)
/*     */   {
/* 206 */     if (name == null) {
/* 207 */       throw new IllegalArgumentException("Graph name cannot be null");
/*     */     }
/*     */ 
/* 211 */     Graph graph = new Graph(name, null);
/*     */ 
/* 214 */     this.graphs.add(graph);
/*     */ 
/* 217 */     return graph;
/*     */   }
/*     */ 
/*     */   public void addGraph(Graph graph)
/*     */   {
/* 226 */     if (graph == null) {
/* 227 */       throw new IllegalArgumentException("Graph cannot be null");
/*     */     }
/*     */ 
/* 230 */     this.graphs.add(graph);
/*     */   }
/*     */ 
/*     */   public boolean start()
/*     */   {
/* 241 */     synchronized (this.optOutLock)
/*     */     {
/* 243 */       if (isOptOut()) {
/* 244 */         return false;
/*     */       }
/*     */ 
/* 248 */       if (this.task != null) {
/* 249 */         return true;
/*     */       }
/*     */ 
/* 253 */       this.task = this.plugin.getServer().getScheduler().runTaskTimerAsynchronously(this.plugin, new Object()
/*     */       {
/* 255 */         private boolean firstPost = true;
/*     */ 
/*     */         public void run()
/*     */         {
/*     */           try {
/* 260 */             synchronized (Metrics.this.optOutLock)
/*     */             {
/* 262 */               if ((Metrics.this.isOptOut()) && (Metrics.this.task != null)) {
/* 263 */                 Metrics.this.task.cancel();
/* 264 */                 Metrics.this.task = null;
/*     */ 
/* 266 */                 for (Metrics.Graph graph : Metrics.this.graphs) {
/* 267 */                   graph.onOptOut();
/*     */                 }
/*     */ 
/*     */               }
/*     */ 
/*     */             }
/*     */ 
/* 275 */             Metrics.this.postPlugin(!this.firstPost);
/*     */ 
/* 279 */             this.firstPost = false;
/*     */           } catch (IOException e) {
/* 281 */             if (Metrics.this.debug)
/* 282 */               Bukkit.getLogger().log(Level.INFO, "[Metrics] " + e.getMessage());
/*     */           }
/*     */         }
/*     */       }
/*     */       , 0L, 18000L);
/*     */ 
/* 288 */       return true;
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean isOptOut()
/*     */   {
/* 298 */     synchronized (this.optOutLock)
/*     */     {
/*     */       try {
/* 301 */         this.configuration.load(getConfigFile());
/*     */       } catch (IOException ex) {
/* 303 */         if (this.debug) {
/* 304 */           Bukkit.getLogger().log(Level.INFO, new StringBuilder().append("[Metrics] ").append(ex.getMessage()).toString());
/*     */         }
/* 306 */         return true;
/*     */       } catch (InvalidConfigurationException ex) {
/* 308 */         if (this.debug) {
/* 309 */           Bukkit.getLogger().log(Level.INFO, new StringBuilder().append("[Metrics] ").append(ex.getMessage()).toString());
/*     */         }
/* 311 */         return true;
/*     */       }
/* 313 */       return this.configuration.getBoolean("opt-out", false);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void enable()
/*     */     throws IOException
/*     */   {
/* 324 */     synchronized (this.optOutLock)
/*     */     {
/* 326 */       if (isOptOut()) {
/* 327 */         this.configuration.set("opt-out", Boolean.valueOf(false));
/* 328 */         this.configuration.save(this.configurationFile);
/*     */       }
/*     */ 
/* 332 */       if (this.task == null)
/* 333 */         start();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void disable()
/*     */     throws IOException
/*     */   {
/* 345 */     synchronized (this.optOutLock)
/*     */     {
/* 347 */       if (!isOptOut()) {
/* 348 */         this.configuration.set("opt-out", Boolean.valueOf(true));
/* 349 */         this.configuration.save(this.configurationFile);
/*     */       }
/*     */ 
/* 353 */       if (this.task != null) {
/* 354 */         this.task.cancel();
/* 355 */         this.task = null;
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public File getConfigFile()
/*     */   {
/* 371 */     File pluginsFolder = this.plugin.getDataFolder().getParentFile();
/*     */ 
/* 374 */     return new File(new File(pluginsFolder, "PluginMetrics"), "config.yml");
/*     */   }
/*     */ 
/*     */   private void postPlugin(boolean isPing)
/*     */     throws IOException
/*     */   {
/* 382 */     PluginDescriptionFile description = this.plugin.getDescription();
/* 383 */     String pluginName = description.getName();
/* 384 */     boolean onlineMode = Bukkit.getServer().getOnlineMode();
/* 385 */     String pluginVersion = description.getVersion();
/* 386 */     String serverVersion = Bukkit.getVersion();
/* 387 */     int playersOnline = Bukkit.getServer().getOnlinePlayers().size();
/*     */ 
/* 392 */     StringBuilder json = new StringBuilder(1024);
/* 393 */     json.append('{');
/*     */ 
/* 396 */     appendJSONPair(json, "guid", this.guid);
/* 397 */     appendJSONPair(json, "plugin_version", pluginVersion);
/* 398 */     appendJSONPair(json, "server_version", serverVersion);
/* 399 */     appendJSONPair(json, "players_online", Integer.toString(playersOnline));
/*     */ 
/* 402 */     String osname = System.getProperty("os.name");
/* 403 */     String osarch = System.getProperty("os.arch");
/* 404 */     String osversion = System.getProperty("os.version");
/* 405 */     String java_version = System.getProperty("java.version");
/* 406 */     int coreCount = Runtime.getRuntime().availableProcessors();
/*     */ 
/* 409 */     if (osarch.equals("amd64")) {
/* 410 */       osarch = "x86_64";
/*     */     }
/*     */ 
/* 413 */     appendJSONPair(json, "osname", osname);
/* 414 */     appendJSONPair(json, "osarch", osarch);
/* 415 */     appendJSONPair(json, "osversion", osversion);
/* 416 */     appendJSONPair(json, "cores", Integer.toString(coreCount));
/* 417 */     appendJSONPair(json, "auth_mode", onlineMode ? "1" : "0");
/* 418 */     appendJSONPair(json, "java_version", java_version);
/*     */ 
/* 421 */     if (isPing) {
/* 422 */       appendJSONPair(json, "ping", "1");
/*     */     }
/*     */ 
/* 425 */     if (this.graphs.size() > 0) {
/* 426 */       synchronized (this.graphs) {
/* 427 */         json.append(',');
/* 428 */         json.append('"');
/* 429 */         json.append("graphs");
/* 430 */         json.append('"');
/* 431 */         json.append(':');
/* 432 */         json.append('{');
/*     */ 
/* 434 */         boolean firstGraph = true;
/*     */ 
/* 436 */         Iterator iter = this.graphs.iterator();
/*     */ 
/* 438 */         while (iter.hasNext()) {
/* 439 */           Graph graph = (Graph)iter.next();
/*     */ 
/* 441 */           StringBuilder graphJson = new StringBuilder();
/* 442 */           graphJson.append('{');
/*     */ 
/* 444 */           for (Plotter plotter : graph.getPlotters()) {
/* 445 */             appendJSONPair(graphJson, plotter.getColumnName(), Integer.toString(plotter.getValue()));
/*     */           }
/*     */ 
/* 448 */           graphJson.append('}');
/*     */ 
/* 450 */           if (!firstGraph) {
/* 451 */             json.append(',');
/*     */           }
/*     */ 
/* 454 */           json.append(escapeJSON(graph.getName()));
/* 455 */           json.append(':');
/* 456 */           json.append(graphJson);
/*     */ 
/* 458 */           firstGraph = false;
/*     */         }
/*     */ 
/* 461 */         json.append('}');
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 466 */     json.append('}');
/*     */ 
/* 469 */     URL url = new URL(new StringBuilder().append("http://report.mcstats.org").append(String.format("/plugin/%s", new Object[] { urlEncode(pluginName) })).toString());
/*     */     URLConnection connection;
/*     */     URLConnection connection;
/* 476 */     if (isMineshafterPresent())
/* 477 */       connection = url.openConnection(Proxy.NO_PROXY);
/*     */     else {
/* 479 */       connection = url.openConnection();
/*     */     }
/*     */ 
/* 483 */     byte[] uncompressed = json.toString().getBytes();
/* 484 */     byte[] compressed = gzip(json.toString());
/*     */ 
/* 487 */     connection.addRequestProperty("User-Agent", "MCStats/8");
/* 488 */     connection.addRequestProperty("Content-Type", "application/json");
/* 489 */     connection.addRequestProperty("Content-Encoding", "gzip");
/* 490 */     connection.addRequestProperty("Content-Length", Integer.toString(compressed.length));
/* 491 */     connection.addRequestProperty("Accept", "application/json");
/* 492 */     connection.addRequestProperty("Connection", "close");
/*     */ 
/* 494 */     connection.setDoOutput(true);
/*     */ 
/* 496 */     if (this.debug) {
/* 497 */       System.out.println(new StringBuilder().append("[Metrics] Prepared request for ").append(pluginName).append(" uncompressed=").append(uncompressed.length).append(" compressed=").append(compressed.length).toString());
/*     */     }
/*     */ 
/* 501 */     OutputStream os = connection.getOutputStream();
/* 502 */     os.write(compressed);
/* 503 */     os.flush();
/*     */ 
/* 506 */     BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
/* 507 */     String response = reader.readLine();
/*     */ 
/* 510 */     os.close();
/* 511 */     reader.close();
/*     */ 
/* 513 */     if ((response == null) || (response.startsWith("ERR")) || (response.startsWith("7"))) {
/* 514 */       if (response == null)
/* 515 */         response = "null";
/* 516 */       else if (response.startsWith("7")) {
/* 517 */         response = response.substring(response.startsWith("7,") ? 2 : 1);
/*     */       }
/*     */ 
/* 520 */       throw new IOException(response);
/*     */     }
/*     */ 
/* 523 */     if ((response.equals("1")) || (response.contains("This is your first update this hour")))
/* 524 */       synchronized (this.graphs) {
/* 525 */         Iterator iter = this.graphs.iterator();
/*     */ 
/* 527 */         while (iter.hasNext()) {
/* 528 */           Graph graph = (Graph)iter.next();
/*     */ 
/* 530 */           for (Plotter plotter : graph.getPlotters())
/* 531 */             plotter.reset();
/*     */         }
/*     */       }
/*     */   }
/*     */ 
/*     */   public static byte[] gzip(String input)
/*     */   {
/* 546 */     ByteArrayOutputStream baos = new ByteArrayOutputStream();
/* 547 */     GZIPOutputStream gzos = null;
/*     */     try
/*     */     {
/* 550 */       gzos = new GZIPOutputStream(baos);
/* 551 */       gzos.write(input.getBytes("UTF-8"));
/*     */     } catch (IOException e) {
/* 553 */       e.printStackTrace();
/*     */     } finally {
/* 555 */       if (gzos != null) try {
/* 556 */           gzos.close();
/*     */         }
/*     */         catch (IOException ignore)
/*     */         {
/*     */         } 
/*     */     }
/* 561 */     return baos.toByteArray();
/*     */   }
/*     */ 
/*     */   private boolean isMineshafterPresent()
/*     */   {
/*     */     try
/*     */     {
/* 571 */       Class.forName("mineshafter.MineServer");
/* 572 */       return true; } catch (Exception e) {
/*     */     }
/* 574 */     return false;
/*     */   }
/*     */ 
/*     */   private static void appendJSONPair(StringBuilder json, String key, String value)
/*     */     throws UnsupportedEncodingException
/*     */   {
/*     */     boolean isValueNumeric;
/*     */     try
/*     */     {
/* 590 */       Double.parseDouble(value);
/* 591 */       isValueNumeric = true;
/*     */     } catch (NumberFormatException e) {
/* 593 */       isValueNumeric = false;
/*     */     }
/*     */ 
/* 596 */     if (json.charAt(json.length() - 1) != '{') {
/* 597 */       json.append(',');
/*     */     }
/*     */ 
/* 600 */     json.append(escapeJSON(key));
/* 601 */     json.append(':');
/*     */ 
/* 603 */     if (isValueNumeric)
/* 604 */       json.append(value);
/*     */     else
/* 606 */       json.append(escapeJSON(value));
/*     */   }
/*     */ 
/*     */   private static String escapeJSON(String text)
/*     */   {
/* 617 */     StringBuilder builder = new StringBuilder();
/*     */ 
/* 619 */     builder.append('"');
/* 620 */     for (int index = 0; index < text.length(); index++) {
/* 621 */       char chr = text.charAt(index);
/*     */ 
/* 623 */       switch (chr) {
/*     */       case '"':
/*     */       case '\\':
/* 626 */         builder.append('\\');
/* 627 */         builder.append(chr);
/* 628 */         break;
/*     */       case '\b':
/* 630 */         builder.append("\\b");
/* 631 */         break;
/*     */       case '\t':
/* 633 */         builder.append("\\t");
/* 634 */         break;
/*     */       case '\n':
/* 636 */         builder.append("\\n");
/* 637 */         break;
/*     */       case '\r':
/* 639 */         builder.append("\\r");
/* 640 */         break;
/*     */       default:
/* 642 */         if (chr < ' ') {
/* 643 */           String t = new StringBuilder().append("000").append(Integer.toHexString(chr)).toString();
/* 644 */           builder.append(new StringBuilder().append("\\u").append(t.substring(t.length() - 4)).toString());
/*     */         } else {
/* 646 */           builder.append(chr);
/*     */         }
/*     */         break;
/*     */       }
/*     */     }
/* 651 */     builder.append('"');
/*     */ 
/* 653 */     return builder.toString();
/*     */   }
/*     */ 
/*     */   private static String urlEncode(String text)
/*     */     throws UnsupportedEncodingException
/*     */   {
/* 663 */     return URLEncoder.encode(text, "UTF-8");
/*     */   }
/*     */ 
/*     */   public static abstract class Plotter
/*     */   {
/*     */     private final String name;
/*     */ 
/*     */     public Plotter()
/*     */     {
/* 758 */       this("Default");
/*     */     }
/*     */ 
/*     */     public Plotter(String name)
/*     */     {
/* 767 */       this.name = name;
/*     */     }
/*     */ 
/*     */     public abstract int getValue();
/*     */ 
/*     */     public String getColumnName()
/*     */     {
/* 785 */       return this.name;
/*     */     }
/*     */ 
/*     */     public void reset()
/*     */     {
/*     */     }
/*     */ 
/*     */     public int hashCode()
/*     */     {
/* 796 */       return getColumnName().hashCode();
/*     */     }
/*     */ 
/*     */     public boolean equals(Object object)
/*     */     {
/* 801 */       if (!(object instanceof Plotter)) {
/* 802 */         return false;
/*     */       }
/*     */ 
/* 805 */       Plotter plotter = (Plotter)object;
/* 806 */       return (plotter.name.equals(this.name)) && (plotter.getValue() == getValue());
/*     */     }
/*     */   }
/*     */ 
/*     */   public static class Graph
/*     */   {
/*     */     private final String name;
/* 680 */     private final Set<Metrics.Plotter> plotters = new LinkedHashSet();
/*     */ 
/*     */     private Graph(String name) {
/* 683 */       this.name = name;
/*     */     }
/*     */ 
/*     */     public String getName()
/*     */     {
/* 692 */       return this.name;
/*     */     }
/*     */ 
/*     */     public void addPlotter(Metrics.Plotter plotter)
/*     */     {
/* 701 */       this.plotters.add(plotter);
/*     */     }
/*     */ 
/*     */     public void removePlotter(Metrics.Plotter plotter)
/*     */     {
/* 710 */       this.plotters.remove(plotter);
/*     */     }
/*     */ 
/*     */     public Set<Metrics.Plotter> getPlotters()
/*     */     {
/* 719 */       return Collections.unmodifiableSet(this.plotters);
/*     */     }
/*     */ 
/*     */     public int hashCode()
/*     */     {
/* 724 */       return this.name.hashCode();
/*     */     }
/*     */ 
/*     */     public boolean equals(Object object)
/*     */     {
/* 729 */       if (!(object instanceof Graph)) {
/* 730 */         return false;
/*     */       }
/*     */ 
/* 733 */       Graph graph = (Graph)object;
/* 734 */       return graph.name.equals(this.name);
/*     */     }
/*     */ 
/*     */     protected void onOptOut()
/*     */     {
/*     */     }
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\Vault.jar
 * Qualified Name:     net.milkbowl.vault.Metrics
 * JD-Core Version:    0.6.2
 */