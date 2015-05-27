/*     */ package com.comphenix.protocol.error;
/*     */ 
/*     */ import com.comphenix.protocol.ProtocolLibrary;
/*     */ import com.comphenix.protocol.collections.ExpireHashMap;
/*     */ import com.comphenix.protocol.events.PacketAdapter;
/*     */ import com.comphenix.protocol.reflect.PrettyPrinter;
/*     */ import com.google.common.base.Preconditions;
/*     */ import com.google.common.primitives.Primitives;
/*     */ import java.io.PrintWriter;
/*     */ import java.io.StringWriter;
/*     */ import java.lang.ref.WeakReference;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.concurrent.ConcurrentHashMap;
/*     */ import java.util.concurrent.ConcurrentMap;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import java.util.concurrent.atomic.AtomicInteger;
/*     */ import java.util.logging.Level;
/*     */ import java.util.logging.Logger;
/*     */ import org.apache.commons.lang.builder.ToStringBuilder;
/*     */ import org.apache.commons.lang.builder.ToStringStyle;
/*     */ import org.bukkit.Bukkit;
/*     */ import org.bukkit.Server;
/*     */ import org.bukkit.plugin.Plugin;
/*     */ 
/*     */ public class DetailedErrorReporter
/*     */   implements ErrorReporter
/*     */ {
/*  55 */   public static final ReportType REPORT_EXCEPTION_COUNT = new ReportType("Internal exception count: %s!");
/*     */   public static final String SECOND_LEVEL_PREFIX = "  ";
/*     */   public static final String DEFAULT_PREFIX = "  ";
/*     */   public static final String DEFAULT_SUPPORT_URL = "https://github.com/dmulloy2/ProtocolLib/issues";
/*     */   public static final String ERROR_PERMISSION = "protocol.info";
/*     */   public static final int DEFAULT_MAX_ERROR_COUNT = 20;
/*  68 */   private ConcurrentMap<String, AtomicInteger> warningCount = new ConcurrentHashMap();
/*     */   protected String prefix;
/*     */   protected String supportURL;
/*  73 */   protected AtomicInteger internalErrorCount = new AtomicInteger();
/*     */   protected int maxErrorCount;
/*     */   protected Logger logger;
/*     */   protected WeakReference<Plugin> pluginReference;
/*     */   protected String pluginName;
/*     */   protected boolean apacheCommonsMissing;
/*     */   protected boolean detailedReporting;
/*  88 */   protected Map<String, Object> globalParameters = new HashMap();
/*     */ 
/*  91 */   private ExpireHashMap<Report, Boolean> rateLimited = new ExpireHashMap();
/*  92 */   private Object rateLock = new Object();
/*     */ 
/*     */   public DetailedErrorReporter(Plugin plugin)
/*     */   {
/*  98 */     this(plugin, "  ", "https://github.com/dmulloy2/ProtocolLib/issues");
/*     */   }
/*     */ 
/*     */   public DetailedErrorReporter(Plugin plugin, String prefix, String supportURL)
/*     */   {
/* 108 */     this(plugin, prefix, supportURL, 20, getBukkitLogger());
/*     */   }
/*     */ 
/*     */   public DetailedErrorReporter(Plugin plugin, String prefix, String supportURL, int maxErrorCount, Logger logger)
/*     */   {
/* 120 */     if (plugin == null) {
/* 121 */       throw new IllegalArgumentException("Plugin cannot be NULL.");
/*     */     }
/* 123 */     this.pluginReference = new WeakReference(plugin);
/* 124 */     this.pluginName = plugin.getName();
/* 125 */     this.prefix = prefix;
/* 126 */     this.supportURL = supportURL;
/* 127 */     this.maxErrorCount = maxErrorCount;
/* 128 */     this.logger = logger;
/*     */   }
/*     */ 
/*     */   private static Logger getBukkitLogger()
/*     */   {
/*     */     try {
/* 134 */       return Bukkit.getLogger(); } catch (LinkageError e) {
/*     */     }
/* 136 */     return Logger.getLogger("Minecraft");
/*     */   }
/*     */ 
/*     */   public boolean isDetailedReporting()
/*     */   {
/* 145 */     return this.detailedReporting;
/*     */   }
/*     */ 
/*     */   public void setDetailedReporting(boolean detailedReporting)
/*     */   {
/* 153 */     this.detailedReporting = detailedReporting;
/*     */   }
/*     */ 
/*     */   public void reportMinimal(Plugin sender, String methodName, Throwable error, Object[] parameters)
/*     */   {
/* 158 */     if (reportMinimalNoSpam(sender, methodName, error))
/*     */     {
/* 160 */       if ((parameters != null) && (parameters.length > 0))
/* 161 */         this.logger.log(Level.SEVERE, printParameters(parameters));
/*     */     }
/*     */   }
/*     */ 
/*     */   public void reportMinimal(Plugin sender, String methodName, Throwable error)
/*     */   {
/* 168 */     reportMinimalNoSpam(sender, methodName, error);
/*     */   }
/*     */ 
/*     */   public boolean reportMinimalNoSpam(Plugin sender, String methodName, Throwable error)
/*     */   {
/* 179 */     String pluginName = PacketAdapter.getPluginName(sender);
/* 180 */     AtomicInteger counter = (AtomicInteger)this.warningCount.get(pluginName);
/*     */ 
/* 183 */     if (counter == null) {
/* 184 */       AtomicInteger created = new AtomicInteger();
/* 185 */       counter = (AtomicInteger)this.warningCount.putIfAbsent(pluginName, created);
/*     */ 
/* 187 */       if (counter == null) {
/* 188 */         counter = created;
/*     */       }
/*     */     }
/*     */ 
/* 192 */     int errorCount = counter.incrementAndGet();
/*     */ 
/* 195 */     if (errorCount < getMaxErrorCount()) {
/* 196 */       this.logger.log(Level.SEVERE, "[" + pluginName + "] Unhandled exception occured in " + methodName + " for " + pluginName, error);
/*     */ 
/* 198 */       return true;
/*     */     }
/*     */ 
/* 202 */     if (isPowerOfTwo(errorCount)) {
/* 203 */       this.logger.log(Level.SEVERE, "[" + pluginName + "] Unhandled exception number " + errorCount + " occured in " + methodName + " for " + pluginName, error);
/*     */     }
/*     */ 
/* 206 */     return false;
/*     */   }
/*     */ 
/*     */   private boolean isPowerOfTwo(int number)
/*     */   {
/* 218 */     return (number & number - 1) == 0;
/*     */   }
/*     */ 
/*     */   public void reportDebug(Object sender, Report.ReportBuilder builder)
/*     */   {
/* 223 */     reportDebug(sender, ((Report.ReportBuilder)Preconditions.checkNotNull(builder, "builder cannot be NULL")).build());
/*     */   }
/*     */ 
/*     */   public void reportDebug(Object sender, Report report)
/*     */   {
/* 228 */     if ((this.logger.isLoggable(Level.FINE)) && (canReport(report)))
/* 229 */       reportLevel(Level.FINE, sender, report);
/*     */   }
/*     */ 
/*     */   public void reportWarning(Object sender, Report.ReportBuilder reportBuilder)
/*     */   {
/* 235 */     if (reportBuilder == null) {
/* 236 */       throw new IllegalArgumentException("reportBuilder cannot be NULL.");
/*     */     }
/* 238 */     reportWarning(sender, reportBuilder.build());
/*     */   }
/*     */ 
/*     */   public void reportWarning(Object sender, Report report)
/*     */   {
/* 243 */     if ((this.logger.isLoggable(Level.WARNING)) && (canReport(report)))
/* 244 */       reportLevel(Level.WARNING, sender, report);
/*     */   }
/*     */ 
/*     */   protected boolean canReport(Report report)
/*     */   {
/* 256 */     long rateLimit = report.getRateLimit();
/*     */ 
/* 259 */     if (rateLimit > 0L) {
/* 260 */       synchronized (this.rateLock) {
/* 261 */         if (this.rateLimited.containsKey(report)) {
/* 262 */           return false;
/*     */         }
/* 264 */         this.rateLimited.put(report, Boolean.valueOf(true), rateLimit, TimeUnit.NANOSECONDS);
/*     */       }
/*     */     }
/* 267 */     return true;
/*     */   }
/*     */ 
/*     */   private void reportLevel(Level level, Object sender, Report report) {
/* 271 */     String message = "[" + this.pluginName + "] [" + getSenderName(sender) + "] " + report.getReportMessage();
/*     */ 
/* 274 */     if (report.getException() != null) {
/* 275 */       this.logger.log(level, message, report.getException());
/*     */     } else {
/* 277 */       this.logger.log(level, message);
/*     */ 
/* 280 */       if (this.detailedReporting) {
/* 281 */         printCallStack(level, this.logger);
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 286 */     if (report.hasCallerParameters())
/*     */     {
/* 288 */       this.logger.log(level, printParameters(report.getCallerParameters()));
/*     */     }
/*     */   }
/*     */ 
/*     */   private String getSenderName(Object sender)
/*     */   {
/* 298 */     if (sender != null) {
/* 299 */       return ReportType.getSenderClass(sender).getSimpleName();
/*     */     }
/* 301 */     return "NULL";
/*     */   }
/*     */ 
/*     */   public void reportDetailed(Object sender, Report.ReportBuilder reportBuilder)
/*     */   {
/* 306 */     reportDetailed(sender, reportBuilder.build());
/*     */   }
/*     */ 
/*     */   public void reportDetailed(Object sender, Report report)
/*     */   {
/* 311 */     Plugin plugin = (Plugin)this.pluginReference.get();
/* 312 */     int errorCount = this.internalErrorCount.incrementAndGet();
/*     */ 
/* 315 */     if (errorCount > getMaxErrorCount())
/*     */     {
/* 317 */       if (isPowerOfTwo(errorCount))
/*     */       {
/* 319 */         reportWarning(this, Report.newBuilder(REPORT_EXCEPTION_COUNT).messageParam(new Object[] { Integer.valueOf(errorCount) }).build());
/*     */       }
/*     */       else {
/* 322 */         return;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 327 */     if (!canReport(report)) {
/* 328 */       return;
/*     */     }
/*     */ 
/* 331 */     StringWriter text = new StringWriter();
/* 332 */     PrintWriter writer = new PrintWriter(text);
/*     */ 
/* 335 */     writer.println("[" + this.pluginName + "] INTERNAL ERROR: " + report.getReportMessage());
/* 336 */     writer.println("If this problem hasn't already been reported, please open a ticket");
/* 337 */     writer.println("at " + this.supportURL + " with the following data:");
/*     */ 
/* 340 */     writer.println("Stack Trace:");
/*     */ 
/* 342 */     if (report.getException() != null) {
/* 343 */       report.getException().printStackTrace(writer);
/*     */     }
/* 345 */     else if (this.detailedReporting) {
/* 346 */       printCallStack(writer);
/*     */     }
/*     */ 
/* 350 */     writer.println("Dump:");
/*     */ 
/* 353 */     if (report.hasCallerParameters()) {
/* 354 */       printParameters(writer, report.getCallerParameters());
/*     */     }
/*     */ 
/* 358 */     for (String param : globalParameters()) {
/* 359 */       writer.println("  " + param + ":");
/* 360 */       writer.println(addPrefix(getStringDescription(getGlobalParameter(param)), "    "));
/*     */     }
/*     */ 
/* 365 */     writer.println("Sender:");
/* 366 */     writer.println(addPrefix(getStringDescription(sender), "  "));
/*     */ 
/* 369 */     if (plugin != null) {
/* 370 */       writer.println("Version:");
/* 371 */       writer.println(addPrefix(plugin.toString(), "  "));
/*     */     }
/*     */ 
/* 375 */     if (Bukkit.getServer() != null) {
/* 376 */       writer.println("Server:");
/* 377 */       writer.println(addPrefix(Bukkit.getServer().getVersion(), "  "));
/*     */ 
/* 380 */       if ("protocol.info" != null) {
/* 381 */         Bukkit.getServer().broadcast(String.format("Error %s (%s) occured in %s.", new Object[] { report.getReportMessage(), report.getException(), sender }), "protocol.info");
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 389 */     this.logger.severe(addPrefix(text.toString(), this.prefix));
/*     */   }
/*     */ 
/*     */   private void printCallStack(Level level, Logger logger)
/*     */   {
/* 397 */     StringWriter text = new StringWriter();
/* 398 */     printCallStack(new PrintWriter(text));
/*     */ 
/* 401 */     logger.log(level, text.toString());
/*     */   }
/*     */ 
/*     */   private void printCallStack(PrintWriter writer)
/*     */   {
/* 409 */     Exception current = new Exception("Not an error! This is the call stack.");
/* 410 */     current.printStackTrace(writer);
/*     */   }
/*     */ 
/*     */   private String printParameters(Object[] parameters) {
/* 414 */     StringWriter writer = new StringWriter();
/*     */ 
/* 417 */     printParameters(new PrintWriter(writer), parameters);
/* 418 */     return writer.toString();
/*     */   }
/*     */ 
/*     */   private void printParameters(PrintWriter writer, Object[] parameters) {
/* 422 */     writer.println("Parameters: ");
/*     */ 
/* 425 */     for (Object param : parameters)
/* 426 */       writer.println(addPrefix(getStringDescription(param), "  "));
/*     */   }
/*     */ 
/*     */   protected String addPrefix(String text, String prefix)
/*     */   {
/* 437 */     return text.replaceAll("(?m)^", prefix);
/*     */   }
/*     */ 
/*     */   protected String getStringDescription(Object value)
/*     */   {
/* 447 */     if (value == null)
/* 448 */       return "[NULL]";
/* 449 */     if ((isSimpleType(value)) || ((value instanceof Class)))
/* 450 */       return value.toString();
/*     */     try
/*     */     {
/* 453 */       if (!this.apacheCommonsMissing)
/* 454 */         return ToStringBuilder.reflectionToString(value, ToStringStyle.MULTI_LINE_STYLE, false, null);
/*     */     }
/*     */     catch (LinkageError ex) {
/* 457 */       this.apacheCommonsMissing = true;
/*     */     }
/*     */     catch (Exception e) {
/* 460 */       ProtocolLibrary.log(Level.WARNING, "Cannot convert to a String with Apache: " + e.getMessage(), new Object[0]);
/*     */     }
/*     */ 
/*     */     try
/*     */     {
/* 465 */       return PrettyPrinter.printObject(value, value.getClass(), Object.class);
/*     */     } catch (IllegalAccessException e) {
/* 467 */       return "[Error: " + e.getMessage() + "]";
/*     */     }
/*     */   }
/*     */ 
/*     */   protected boolean isSimpleType(Object test)
/*     */   {
/* 478 */     return ((test instanceof String)) || (Primitives.isWrapperType(test.getClass()));
/*     */   }
/*     */ 
/*     */   public int getErrorCount()
/*     */   {
/* 486 */     return this.internalErrorCount.get();
/*     */   }
/*     */ 
/*     */   public void setErrorCount(int errorCount)
/*     */   {
/* 494 */     this.internalErrorCount.set(errorCount);
/*     */   }
/*     */ 
/*     */   public int getMaxErrorCount()
/*     */   {
/* 502 */     return this.maxErrorCount;
/*     */   }
/*     */ 
/*     */   public void setMaxErrorCount(int maxErrorCount)
/*     */   {
/* 510 */     this.maxErrorCount = maxErrorCount;
/*     */   }
/*     */ 
/*     */   public void addGlobalParameter(String key, Object value)
/*     */   {
/* 521 */     if (key == null)
/* 522 */       throw new IllegalArgumentException("key cannot be NULL.");
/* 523 */     if (value == null) {
/* 524 */       throw new IllegalArgumentException("value cannot be NULL.");
/*     */     }
/* 526 */     this.globalParameters.put(key, value);
/*     */   }
/*     */ 
/*     */   public Object getGlobalParameter(String key)
/*     */   {
/* 535 */     if (key == null) {
/* 536 */       throw new IllegalArgumentException("key cannot be NULL.");
/*     */     }
/* 538 */     return this.globalParameters.get(key);
/*     */   }
/*     */ 
/*     */   public void clearGlobalParameters()
/*     */   {
/* 545 */     this.globalParameters.clear();
/*     */   }
/*     */ 
/*     */   public Set<String> globalParameters()
/*     */   {
/* 553 */     return this.globalParameters.keySet();
/*     */   }
/*     */ 
/*     */   public String getSupportURL()
/*     */   {
/* 561 */     return this.supportURL;
/*     */   }
/*     */ 
/*     */   public void setSupportURL(String supportURL)
/*     */   {
/* 569 */     this.supportURL = supportURL;
/*     */   }
/*     */ 
/*     */   public String getPrefix()
/*     */   {
/* 577 */     return this.prefix;
/*     */   }
/*     */ 
/*     */   public void setPrefix(String prefix)
/*     */   {
/* 585 */     this.prefix = prefix;
/*     */   }
/*     */ 
/*     */   public Logger getLogger()
/*     */   {
/* 593 */     return this.logger;
/*     */   }
/*     */ 
/*     */   public void setLogger(Logger logger)
/*     */   {
/* 601 */     this.logger = logger;
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.error.DetailedErrorReporter
 * JD-Core Version:    0.6.2
 */