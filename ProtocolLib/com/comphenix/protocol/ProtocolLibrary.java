/*     */ package com.comphenix.protocol;
/*     */ 
/*     */ import com.comphenix.executors.BukkitExecutors;
/*     */ import com.comphenix.protocol.async.AsyncFilterManager;
/*     */ import com.comphenix.protocol.error.BasicErrorReporter;
/*     */ import com.comphenix.protocol.error.DelegatedErrorReporter;
/*     */ import com.comphenix.protocol.error.DetailedErrorReporter;
/*     */ import com.comphenix.protocol.error.ErrorReporter;
/*     */ import com.comphenix.protocol.error.Report;
/*     */ import com.comphenix.protocol.error.Report.ReportBuilder;
/*     */ import com.comphenix.protocol.error.ReportType;
/*     */ import com.comphenix.protocol.injector.DelayedSingleTask;
/*     */ import com.comphenix.protocol.injector.InternalManager;
/*     */ import com.comphenix.protocol.injector.PacketFilterBuilder;
/*     */ import com.comphenix.protocol.injector.PacketFilterManager;
/*     */ import com.comphenix.protocol.injector.PacketFilterManager.PlayerInjectHooks;
/*     */ import com.comphenix.protocol.metrics.Statistics;
/*     */ import com.comphenix.protocol.reflect.compiler.BackgroundCompiler;
/*     */ import com.comphenix.protocol.utility.ChatExtensions;
/*     */ import com.comphenix.protocol.utility.EnhancerFactory;
/*     */ import com.comphenix.protocol.utility.MinecraftVersion;
/*     */ import com.google.common.base.Splitter;
/*     */ import com.google.common.collect.Iterables;
/*     */ import com.google.common.collect.Sets;
/*     */ import com.google.common.util.concurrent.ListeningScheduledExecutorService;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.text.MessageFormat;
/*     */ import java.util.Set;
/*     */ import java.util.logging.Handler;
/*     */ import java.util.logging.Level;
/*     */ import java.util.logging.LogRecord;
/*     */ import java.util.logging.Logger;
/*     */ import java.util.regex.Matcher;
/*     */ import java.util.regex.Pattern;
/*     */ import org.bukkit.Server;
/*     */ import org.bukkit.command.CommandExecutor;
/*     */ import org.bukkit.command.PluginCommand;
/*     */ import org.bukkit.plugin.Plugin;
/*     */ import org.bukkit.plugin.PluginDescriptionFile;
/*     */ import org.bukkit.plugin.PluginManager;
/*     */ import org.bukkit.plugin.java.JavaPlugin;
/*     */ import org.bukkit.scheduler.BukkitScheduler;
/*     */ 
/*     */ public class ProtocolLibrary extends JavaPlugin
/*     */ {
/*  66 */   public static final ReportType REPORT_CANNOT_LOAD_CONFIG = new ReportType("Cannot load configuration");
/*  67 */   public static final ReportType REPORT_CANNOT_DELETE_CONFIG = new ReportType("Cannot delete old ProtocolLib configuration.");
/*  68 */   public static final ReportType REPORT_CANNOT_PARSE_INJECTION_METHOD = new ReportType("Cannot parse injection method. Using default.");
/*     */ 
/*  70 */   public static final ReportType REPORT_PLUGIN_LOAD_ERROR = new ReportType("Cannot load ProtocolLib.");
/*  71 */   public static final ReportType REPORT_PLUGIN_ENABLE_ERROR = new ReportType("Cannot enable ProtocolLib.");
/*     */ 
/*  73 */   public static final ReportType REPORT_METRICS_IO_ERROR = new ReportType("Unable to enable metrics due to network problems.");
/*  74 */   public static final ReportType REPORT_METRICS_GENERIC_ERROR = new ReportType("Unable to enable metrics due to network problems.");
/*     */ 
/*  76 */   public static final ReportType REPORT_CANNOT_PARSE_MINECRAFT_VERSION = new ReportType("Unable to retrieve current Minecraft version. Assuming %s");
/*  77 */   public static final ReportType REPORT_CANNOT_DETECT_CONFLICTING_PLUGINS = new ReportType("Unable to detect conflicting plugin versions.");
/*  78 */   public static final ReportType REPORT_CANNOT_REGISTER_COMMAND = new ReportType("Cannot register command %s: %s");
/*     */ 
/*  80 */   public static final ReportType REPORT_CANNOT_CREATE_TIMEOUT_TASK = new ReportType("Unable to create packet timeout task.");
/*  81 */   public static final ReportType REPORT_CANNOT_UPDATE_PLUGIN = new ReportType("Cannot perform automatic updates.");
/*     */   public static final String MINIMUM_MINECRAFT_VERSION = "1.0";
/*     */   public static final String MAXIMUM_MINECRAFT_VERSION = "1.8.3";
/*     */   public static final String MINECRAFT_LAST_RELEASE_DATE = "2015-02-20";
/*     */   static final long MILLI_PER_SECOND = 1000L;
/*     */   private static final String PERMISSION_INFO = "protocol.info";
/*     */   private static InternalManager protocolManager;
/* 116 */   private static ErrorReporter reporter = new BasicErrorReporter();
/*     */   private static ProtocolConfig config;
/*     */   private Statistics statistics;
/*     */   private static ListeningScheduledExecutorService executorAsync;
/*     */   private static ListeningScheduledExecutorService executorSync;
/*     */   private BackgroundCompiler backgroundCompiler;
/* 133 */   private int packetTask = -1;
/* 134 */   private int tickCounter = 0;
/*     */   private static final int ASYNC_MANAGER_DELAY = 1;
/*     */   private DelayedSingleTask unhookTask;
/* 141 */   private int configExpectedMod = -1;
/*     */   private static Logger logger;
/*     */   private Handler redirectHandler;
/*     */   private CommandProtocol commandProtocol;
/*     */   private CommandPacket commandPacket;
/*     */   private CommandFilter commandFilter;
/*     */   private boolean skipDisable;
/*     */ 
/*     */   public void onLoad()
/*     */   {
/* 158 */     logger = getLoggerSafely();
/* 159 */     Application.registerPrimaryThread();
/*     */ 
/* 162 */     EnhancerFactory.getInstance().setClassLoader(getClassLoader());
/*     */ 
/* 165 */     executorAsync = BukkitExecutors.newAsynchronous(this);
/* 166 */     executorSync = BukkitExecutors.newSynchronous(this);
/*     */ 
/* 169 */     DetailedErrorReporter detailedReporter = new DetailedErrorReporter(this);
/* 170 */     reporter = getFilteredReporter(detailedReporter);
/*     */     try
/*     */     {
/* 173 */       config = new ProtocolConfig(this);
/*     */     } catch (Exception e) {
/* 175 */       reporter.reportWarning(this, Report.newBuilder(REPORT_CANNOT_LOAD_CONFIG).error(e));
/*     */ 
/* 178 */       if (deleteConfig())
/* 179 */         config = new ProtocolConfig(this);
/*     */       else {
/* 181 */         reporter.reportWarning(this, Report.newBuilder(REPORT_CANNOT_DELETE_CONFIG));
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 186 */     if (config.isDebug()) {
/* 187 */       logger.warning("Debug mode is enabled!");
/*     */     }
/*     */ 
/* 190 */     if (config.isDetailedErrorReporting()) {
/* 191 */       detailedReporter.setDetailedReporting(true);
/* 192 */       logger.warning("Detailed error reporting enabled!");
/*     */     }
/*     */ 
/*     */     try
/*     */     {
/* 197 */       checkConflictingVersions();
/*     */ 
/* 200 */       MinecraftVersion version = verifyMinecraftVersion();
/*     */ 
/* 202 */       this.unhookTask = new DelayedSingleTask(this);
/* 203 */       protocolManager = PacketFilterManager.newBuilder().classLoader(getClassLoader()).server(getServer()).library(this).minecraftVersion(version).unhookTask(this.unhookTask).reporter(reporter).build();
/*     */ 
/* 213 */       detailedReporter.addGlobalParameter("manager", protocolManager);
/*     */       try
/*     */       {
/* 217 */         PacketFilterManager.PlayerInjectHooks hook = config.getInjectionMethod();
/*     */ 
/* 220 */         if (!protocolManager.getPlayerHook().equals(hook)) {
/* 221 */           logger.info("Changing player hook from " + protocolManager.getPlayerHook() + " to " + hook);
/* 222 */           protocolManager.setPlayerHook(hook);
/*     */         }
/*     */       } catch (IllegalArgumentException e) {
/* 225 */         reporter.reportWarning(config, Report.newBuilder(REPORT_CANNOT_PARSE_INJECTION_METHOD).error(e));
/*     */       }
/*     */ 
/* 229 */       initializeCommands();
/* 230 */       setupBroadcastUsers("protocol.info");
/*     */     }
/*     */     catch (OutOfMemoryError e) {
/* 233 */       throw e;
/*     */     } catch (ThreadDeath e) {
/* 235 */       throw e;
/*     */     } catch (Throwable e) {
/* 237 */       reporter.reportDetailed(this, Report.newBuilder(REPORT_PLUGIN_LOAD_ERROR).error(e).callerParam(new Object[] { protocolManager }));
/* 238 */       disablePlugin();
/*     */     }
/*     */   }
/*     */ 
/*     */   private void initializeCommands()
/*     */   {
/* 247 */     for (ProtocolCommand command : ProtocolCommand.values())
/*     */       try {
/* 249 */         switch (4.$SwitchMap$com$comphenix$protocol$ProtocolLibrary$ProtocolCommand[command.ordinal()]) {
/*     */         case 1:
/* 251 */           this.commandProtocol = new CommandProtocol(reporter, this, config);
/* 252 */           break;
/*     */         case 2:
/* 254 */           this.commandFilter = new CommandFilter(reporter, this, config);
/* 255 */           break;
/*     */         case 3:
/* 257 */           this.commandPacket = new CommandPacket(reporter, this, logger, this.commandFilter, protocolManager);
/*     */         }
/*     */       }
/*     */       catch (OutOfMemoryError e) {
/* 261 */         throw e;
/*     */       } catch (ThreadDeath e) {
/* 263 */         throw e;
/*     */       } catch (Throwable e) {
/* 265 */         reporter.reportWarning(this, Report.newBuilder(REPORT_CANNOT_REGISTER_COMMAND).messageParam(new Object[] { command.name(), e.getMessage() }).error(e));
/*     */       }
/*     */   }
/*     */ 
/*     */   private ErrorReporter getFilteredReporter(ErrorReporter reporter)
/*     */   {
/* 276 */     return new DelegatedErrorReporter(reporter) {
/* 277 */       private int lastModCount = -1;
/* 278 */       private Set<String> reports = Sets.newHashSet();
/*     */ 
/*     */       protected Report filterReport(Object sender, Report report, boolean detailed)
/*     */       {
/*     */         try {
/* 283 */           String canonicalName = ReportType.getReportName(sender, report.getType());
/* 284 */           String reportName = ((String)Iterables.getLast(Splitter.on("#").split(canonicalName))).toUpperCase();
/*     */ 
/* 286 */           if ((ProtocolLibrary.config != null) && (ProtocolLibrary.config.getModificationCount() != this.lastModCount))
/*     */           {
/* 288 */             this.reports = Sets.newHashSet(ProtocolLibrary.config.getSuppressedReports());
/* 289 */             this.lastModCount = ProtocolLibrary.config.getModificationCount();
/*     */           }
/*     */ 
/* 293 */           if ((this.reports.contains(canonicalName)) || (this.reports.contains(reportName)))
/* 294 */             return null;
/*     */         }
/*     */         catch (Exception e)
/*     */         {
/* 298 */           ProtocolLibrary.logger.warning("Error filtering reports: " + e.toString());
/*     */         }
/*     */ 
/* 301 */         return report;
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   private boolean deleteConfig() {
/* 307 */     return config.getFile().delete();
/*     */   }
/*     */ 
/*     */   public void reloadConfig()
/*     */   {
/* 312 */     super.reloadConfig();
/*     */ 
/* 315 */     if (config != null)
/* 316 */       config.reloadConfig();
/*     */   }
/*     */ 
/*     */   private void setupBroadcastUsers(final String permission)
/*     */   {
/* 322 */     if (this.redirectHandler != null) {
/* 323 */       return;
/*     */     }
/*     */ 
/* 326 */     this.redirectHandler = new Handler()
/*     */     {
/*     */       public void publish(LogRecord record)
/*     */       {
/* 330 */         if (record.getLevel().intValue() >= Level.WARNING.intValue())
/* 331 */           ProtocolLibrary.this.commandPacket.broadcastMessageSilently(record.getMessage(), permission);
/*     */       }
/*     */ 
/*     */       public void flush()
/*     */       {
/*     */       }
/*     */ 
/*     */       public void close()
/*     */         throws SecurityException
/*     */       {
/*     */       }
/*     */     };
/* 346 */     logger.addHandler(this.redirectHandler);
/*     */   }
/*     */ 
/*     */   public void onEnable()
/*     */   {
/*     */     try {
/* 352 */       Server server = getServer();
/* 353 */       PluginManager manager = server.getPluginManager();
/*     */ 
/* 356 */       if (manager == null) {
/* 357 */         return;
/*     */       }
/*     */ 
/* 360 */       if (protocolManager == null) {
/* 361 */         Logger directLogging = Logger.getLogger("Minecraft");
/* 362 */         String[] message = { " ProtocolLib does not support plugin reloaders! ", " Please use the built-in reload command! " };
/*     */ 
/* 367 */         for (String line : ChatExtensions.toFlowerBox(message, "*", 3, 1)) {
/* 368 */           directLogging.severe(line);
/*     */         }
/*     */ 
/* 371 */         disablePlugin();
/* 372 */         return;
/*     */       }
/*     */ 
/* 376 */       checkForIncompatibility(manager);
/*     */ 
/* 379 */       if ((this.backgroundCompiler == null) && (config.isBackgroundCompilerEnabled())) {
/* 380 */         this.backgroundCompiler = new BackgroundCompiler(getClassLoader(), reporter);
/* 381 */         BackgroundCompiler.setInstance(this.backgroundCompiler);
/*     */ 
/* 383 */         logger.info("Started structure compiler thread.");
/*     */       } else {
/* 385 */         logger.info("Structure compiler thread has been disabled.");
/*     */       }
/*     */ 
/* 389 */       registerCommand("protocol", this.commandProtocol);
/* 390 */       registerCommand("packet", this.commandPacket);
/* 391 */       registerCommand("filter", this.commandFilter);
/*     */ 
/* 394 */       protocolManager.registerEvents(manager, this);
/*     */ 
/* 398 */       createPacketTask(server);
/*     */     } catch (OutOfMemoryError e) {
/* 400 */       throw e;
/*     */     } catch (ThreadDeath e) {
/* 402 */       throw e;
/*     */     } catch (Throwable e) {
/* 404 */       reporter.reportDetailed(this, Report.newBuilder(REPORT_PLUGIN_ENABLE_ERROR).error(e));
/* 405 */       disablePlugin();
/* 406 */       return;
/*     */     }
/*     */ 
/*     */     try
/*     */     {
/* 411 */       if (config.isMetricsEnabled())
/* 412 */         this.statistics = new Statistics(this);
/*     */     }
/*     */     catch (OutOfMemoryError e) {
/* 415 */       throw e;
/*     */     } catch (ThreadDeath e) {
/* 417 */       throw e;
/*     */     } catch (IOException e) {
/* 419 */       reporter.reportDetailed(this, Report.newBuilder(REPORT_METRICS_IO_ERROR).error(e).callerParam(new Object[] { this.statistics }));
/*     */     } catch (Throwable e) {
/* 421 */       reporter.reportDetailed(this, Report.newBuilder(REPORT_METRICS_GENERIC_ERROR).error(e).callerParam(new Object[] { this.statistics }));
/*     */     }
/*     */   }
/*     */ 
/*     */   private void checkForIncompatibility(PluginManager manager)
/*     */   {
/* 427 */     String[] incompatible = new String[0];
/*     */ 
/* 429 */     for (String plugin : incompatible) {
/* 430 */       if (manager.getPlugin(plugin) != null)
/*     */       {
/* 432 */         logger.severe("Detected incompatible plugin: " + plugin);
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 437 */     if (manager.getPlugin("TagAPI") != null) {
/* 438 */       Plugin iTag = manager.getPlugin("iTag");
/* 439 */       if ((iTag == null) || (iTag.getDescription().getVersion().startsWith("1.0")))
/* 440 */         logger.severe("Detected incompatible plugin: TagAPI");
/*     */     }
/*     */   }
/*     */ 
/*     */   private MinecraftVersion verifyMinecraftVersion()
/*     */   {
/* 447 */     MinecraftVersion minimum = new MinecraftVersion("1.0");
/* 448 */     MinecraftVersion maximum = new MinecraftVersion("1.8.3");
/*     */     try
/*     */     {
/* 451 */       MinecraftVersion current = new MinecraftVersion(getServer());
/*     */ 
/* 454 */       if (!config.getIgnoreVersionCheck().equals(current.getVersion()))
/*     */       {
/* 456 */         if (current.compareTo(minimum) < 0)
/* 457 */           logger.warning("Version " + current + " is lower than the minimum " + minimum);
/* 458 */         if (current.compareTo(maximum) > 0) {
/* 459 */           logger.warning("Version " + current + " has not yet been tested! Proceed with caution.");
/*     */         }
/*     */       }
/* 462 */       return current;
/*     */     } catch (Exception e) {
/* 464 */       reporter.reportWarning(this, Report.newBuilder(REPORT_CANNOT_PARSE_MINECRAFT_VERSION).error(e).messageParam(new Object[] { maximum }));
/*     */     }
/*     */ 
/* 467 */     return maximum;
/*     */   }
/*     */ 
/*     */   private void checkConflictingVersions()
/*     */   {
/* 472 */     Pattern ourPlugin = Pattern.compile("ProtocolLib-(.*)\\.jar");
/* 473 */     MinecraftVersion currentVersion = new MinecraftVersion(getDescription().getVersion());
/* 474 */     MinecraftVersion newestVersion = null;
/*     */ 
/* 477 */     File loadedFile = getFile();
/*     */     try
/*     */     {
/* 481 */       File pluginFolder = new File("plugins/");
/*     */ 
/* 483 */       for (File candidate : pluginFolder.listFiles())
/* 484 */         if ((candidate.isFile()) && (!candidate.equals(loadedFile))) {
/* 485 */           Matcher match = ourPlugin.matcher(candidate.getName());
/* 486 */           if (match.matches()) {
/* 487 */             MinecraftVersion version = new MinecraftVersion(match.group(1));
/*     */ 
/* 489 */             if (candidate.length() == 0L)
/*     */             {
/* 491 */               logger.info((candidate.delete() ? "Deleted " : "Could not delete ") + candidate);
/* 492 */             } else if ((newestVersion == null) || (newestVersion.compareTo(version) < 0))
/* 493 */               newestVersion = version;
/*     */           }
/*     */         }
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/* 499 */       reporter.reportWarning(this, Report.newBuilder(REPORT_CANNOT_DETECT_CONFLICTING_PLUGINS).error(e));
/*     */     }
/*     */ 
/* 503 */     if ((newestVersion != null) && (currentVersion.compareTo(newestVersion) < 0))
/*     */     {
/* 505 */       this.skipDisable = true;
/*     */ 
/* 507 */       throw new IllegalStateException(String.format("Detected a newer version of ProtocolLib (%s) in plugin folder than the current (%s). Disabling.", new Object[] { newestVersion.getVersion(), currentVersion.getVersion() }));
/*     */     }
/*     */   }
/*     */ 
/*     */   private void registerCommand(String name, CommandExecutor executor)
/*     */   {
/*     */     try
/*     */     {
/* 516 */       if (executor == null) {
/* 517 */         return;
/*     */       }
/* 519 */       PluginCommand command = getCommand(name);
/*     */ 
/* 522 */       if (command != null)
/* 523 */         command.setExecutor(executor);
/*     */       else
/* 525 */         throw new RuntimeException("plugin.yml might be corrupt.");
/*     */     }
/*     */     catch (RuntimeException e) {
/* 528 */       reporter.reportWarning(this, Report.newBuilder(REPORT_CANNOT_REGISTER_COMMAND).messageParam(new Object[] { name, e.getMessage() }).error(e));
/*     */     }
/*     */   }
/*     */ 
/*     */   private void disablePlugin()
/*     */   {
/* 536 */     getServer().getPluginManager().disablePlugin(this);
/*     */   }
/*     */ 
/*     */   private void createPacketTask(Server server) {
/*     */     try {
/* 541 */       if (this.packetTask >= 0) {
/* 542 */         throw new IllegalStateException("Packet task has already been created");
/*     */       }
/*     */ 
/* 545 */       this.packetTask = server.getScheduler().scheduleSyncRepeatingTask(this, new Runnable()
/*     */       {
/*     */         public void run() {
/* 548 */           AsyncFilterManager manager = (AsyncFilterManager)ProtocolLibrary.protocolManager.getAsynchronousManager();
/*     */ 
/* 551 */           manager.sendProcessedPackets(ProtocolLibrary.access$408(ProtocolLibrary.this), true);
/*     */ 
/* 554 */           ProtocolLibrary.this.updateConfiguration();
/*     */         }
/*     */       }
/*     */       , 1L, 1L);
/*     */     }
/*     */     catch (OutOfMemoryError e)
/*     */     {
/* 558 */       throw e;
/*     */     } catch (ThreadDeath e) {
/* 560 */       throw e;
/*     */     } catch (Throwable e) {
/* 562 */       if (this.packetTask == -1)
/* 563 */         reporter.reportDetailed(this, Report.newBuilder(REPORT_CANNOT_CREATE_TIMEOUT_TASK).error(e));
/*     */     }
/*     */   }
/*     */ 
/*     */   private void updateConfiguration()
/*     */   {
/* 569 */     if ((config != null) && (config.getModificationCount() != this.configExpectedMod)) {
/* 570 */       this.configExpectedMod = config.getModificationCount();
/*     */ 
/* 573 */       protocolManager.setDebug(config.isDebug());
/*     */     }
/*     */   }
/*     */ 
/*     */   public void onDisable()
/*     */   {
/* 579 */     if (this.skipDisable) {
/* 580 */       return;
/*     */     }
/*     */ 
/* 584 */     if (this.backgroundCompiler != null) {
/* 585 */       this.backgroundCompiler.shutdownAll();
/* 586 */       this.backgroundCompiler = null;
/* 587 */       BackgroundCompiler.setInstance(null);
/*     */     }
/*     */ 
/* 591 */     if (this.packetTask >= 0) {
/* 592 */       getServer().getScheduler().cancelTask(this.packetTask);
/* 593 */       this.packetTask = -1;
/*     */     }
/*     */ 
/* 597 */     if (this.redirectHandler != null) {
/* 598 */       logger.removeHandler(this.redirectHandler);
/*     */     }
/* 600 */     if (protocolManager != null)
/* 601 */       protocolManager.close();
/*     */     else {
/* 603 */       return;
/*     */     }
/* 605 */     if (this.unhookTask != null)
/* 606 */       this.unhookTask.close();
/* 607 */     protocolManager = null;
/* 608 */     this.statistics = null;
/*     */ 
/* 611 */     reporter = new BasicErrorReporter();
/*     */   }
/*     */ 
/*     */   private Logger getLoggerSafely()
/*     */   {
/* 616 */     Logger log = null;
/*     */     try
/*     */     {
/* 619 */       log = getLogger();
/*     */     } catch (OutOfMemoryError e) {
/* 621 */       throw e;
/*     */     } catch (ThreadDeath e) {
/* 623 */       throw e;
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/*     */     }
/*     */ 
/* 629 */     if (log == null)
/* 630 */       log = Logger.getLogger("Minecraft");
/* 631 */     return log;
/*     */   }
/*     */ 
/*     */   public static ErrorReporter getErrorReporter()
/*     */   {
/* 641 */     return reporter;
/*     */   }
/*     */ 
/*     */   public static ProtocolConfig getConfiguration()
/*     */   {
/* 649 */     return config;
/*     */   }
/*     */ 
/*     */   public static ProtocolManager getProtocolManager()
/*     */   {
/* 657 */     return protocolManager;
/*     */   }
/*     */ 
/*     */   public Statistics getStatistics()
/*     */   {
/* 668 */     return this.statistics;
/*     */   }
/*     */ 
/*     */   public static ListeningScheduledExecutorService getExecutorAsync()
/*     */   {
/* 678 */     return executorAsync;
/*     */   }
/*     */ 
/*     */   public static ListeningScheduledExecutorService getExecutorSync()
/*     */   {
/* 688 */     return executorSync;
/*     */   }
/*     */ 
/*     */   public static void log(Level level, String message, Object[] args)
/*     */   {
/* 694 */     logger.log(level, MessageFormat.format(message, args));
/*     */   }
/*     */ 
/*     */   public static void log(String message, Object[] args) {
/* 698 */     log(Level.INFO, message, args);
/*     */   }
/*     */ 
/*     */   private static enum ProtocolCommand
/*     */   {
/* 100 */     FILTER, 
/* 101 */     PACKET, 
/* 102 */     PROTOCOL;
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.ProtocolLibrary
 * JD-Core Version:    0.6.2
 */