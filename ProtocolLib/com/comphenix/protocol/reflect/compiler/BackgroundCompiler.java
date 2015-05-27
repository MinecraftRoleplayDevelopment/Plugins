/*     */ package com.comphenix.protocol.reflect.compiler;
/*     */ 
/*     */ import com.comphenix.protocol.error.ErrorReporter;
/*     */ import com.comphenix.protocol.error.Report;
/*     */ import com.comphenix.protocol.error.Report.ReportBuilder;
/*     */ import com.comphenix.protocol.error.ReportType;
/*     */ import com.comphenix.protocol.reflect.StructureModifier;
/*     */ import com.google.common.collect.Lists;
/*     */ import com.google.common.collect.Maps;
/*     */ import com.google.common.util.concurrent.ThreadFactoryBuilder;
/*     */ import java.lang.management.ManagementFactory;
/*     */ import java.lang.management.MemoryPoolMXBean;
/*     */ import java.lang.management.MemoryUsage;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.concurrent.Callable;
/*     */ import java.util.concurrent.ExecutorService;
/*     */ import java.util.concurrent.Executors;
/*     */ import java.util.concurrent.RejectedExecutionException;
/*     */ import java.util.concurrent.ThreadFactory;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ 
/*     */ public class BackgroundCompiler
/*     */ {
/*  49 */   public static final ReportType REPORT_CANNOT_COMPILE_STRUCTURE_MODIFIER = new ReportType("Cannot compile structure. Disabing compiler.");
/*  50 */   public static final ReportType REPORT_CANNOT_SCHEDULE_COMPILATION = new ReportType("Unable to schedule compilation task.");
/*     */   public static final String THREAD_FORMAT = "ProtocolLib-StructureCompiler %s";
/*     */   public static final int SHUTDOWN_DELAY_MS = 2000;
/*     */   public static final double DEFAULT_DISABLE_AT_PERM_GEN = 0.65D;
/*     */   private static BackgroundCompiler backgroundCompiler;
/*  69 */   private Map<StructureCompiler.StructureKey, List<CompileListener<?>>> listeners = Maps.newHashMap();
/*  70 */   private Object listenerLock = new Object();
/*     */   private StructureCompiler compiler;
/*     */   private boolean enabled;
/*     */   private boolean shuttingDown;
/*     */   private ExecutorService executor;
/*     */   private ErrorReporter reporter;
/*  79 */   private double disablePermGenFraction = 0.65D;
/*     */ 
/*     */   public static BackgroundCompiler getInstance()
/*     */   {
/*  86 */     return backgroundCompiler;
/*     */   }
/*     */ 
/*     */   public static void setInstance(BackgroundCompiler backgroundCompiler)
/*     */   {
/*  94 */     backgroundCompiler = backgroundCompiler;
/*     */   }
/*     */ 
/*     */   public BackgroundCompiler(ClassLoader loader, ErrorReporter reporter)
/*     */   {
/* 105 */     ThreadFactory factory = new ThreadFactoryBuilder().setDaemon(true).setNameFormat("ProtocolLib-StructureCompiler %s").build();
/*     */ 
/* 109 */     initializeCompiler(loader, reporter, Executors.newSingleThreadExecutor(factory));
/*     */   }
/*     */ 
/*     */   public BackgroundCompiler(ClassLoader loader, ErrorReporter reporter, ExecutorService executor)
/*     */   {
/* 119 */     initializeCompiler(loader, reporter, executor);
/*     */   }
/*     */ 
/*     */   private void initializeCompiler(ClassLoader loader, ErrorReporter reporter, ExecutorService executor)
/*     */   {
/* 124 */     if (loader == null)
/* 125 */       throw new IllegalArgumentException("loader cannot be NULL");
/* 126 */     if (executor == null)
/* 127 */       throw new IllegalArgumentException("executor cannot be NULL");
/* 128 */     if (reporter == null) {
/* 129 */       throw new IllegalArgumentException("reporter cannot be NULL.");
/*     */     }
/* 131 */     this.compiler = new StructureCompiler(loader);
/* 132 */     this.reporter = reporter;
/* 133 */     this.executor = executor;
/* 134 */     this.enabled = true;
/*     */   }
/*     */ 
/*     */   public void scheduleCompilation(final Map<Class, StructureModifier> cache, final Class key)
/*     */   {
/* 146 */     StructureModifier uncompiled = (StructureModifier)cache.get(key);
/*     */ 
/* 148 */     if (uncompiled != null)
/* 149 */       scheduleCompilation(uncompiled, new CompileListener()
/*     */       {
/*     */         public void onCompiled(StructureModifier<Object> compiledModifier)
/*     */         {
/* 153 */           cache.put(key, compiledModifier);
/*     */         }
/*     */       });
/*     */   }
/*     */ 
/*     */   public <TKey> void scheduleCompilation(final StructureModifier<TKey> uncompiled, CompileListener<TKey> listener)
/*     */   {
/* 167 */     if ((this.enabled) && (!this.shuttingDown))
/*     */     {
/* 169 */       if (getPermGenUsage() > this.disablePermGenFraction) {
/* 170 */         return;
/*     */       }
/*     */ 
/* 173 */       if ((this.executor == null) || (this.executor.isShutdown())) {
/* 174 */         return;
/*     */       }
/*     */ 
/* 177 */       final StructureCompiler.StructureKey key = new StructureCompiler.StructureKey(uncompiled);
/*     */ 
/* 180 */       synchronized (this.listenerLock) {
/* 181 */         List list = (List)this.listeners.get(key);
/*     */ 
/* 183 */         if (!this.listeners.containsKey(key)) {
/* 184 */           this.listeners.put(key, Lists.newArrayList(new CompileListener[] { listener }));
/*     */         }
/*     */         else {
/* 187 */           list.add(listener);
/* 188 */           return;
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 193 */       Callable worker = new Callable()
/*     */       {
/*     */         public Object call() throws Exception {
/* 196 */           StructureModifier modifier = uncompiled;
/* 197 */           List list = null;
/*     */           try
/*     */           {
/* 201 */             modifier = BackgroundCompiler.this.compiler.compile(modifier);
/*     */ 
/* 203 */             synchronized (BackgroundCompiler.this.listenerLock) {
/* 204 */               list = (List)BackgroundCompiler.this.listeners.get(key);
/*     */ 
/* 207 */               if (list != null) {
/* 208 */                 list = Lists.newArrayList(list);
/*     */               }
/*     */ 
/*     */             }
/*     */ 
/* 213 */             if (list != null) {
/* 214 */               for (Iterator i$ = list.iterator(); i$.hasNext(); ) { Object compileListener = i$.next();
/* 215 */                 ((CompileListener)compileListener).onCompiled(modifier);
/*     */               }
/*     */ 
/* 219 */               synchronized (BackgroundCompiler.this.listenerLock) {
/* 220 */                 list = (List)BackgroundCompiler.this.listeners.remove(key);
/*     */               }
/*     */             }
/*     */           }
/*     */           catch (OutOfMemoryError e) {
/* 225 */             BackgroundCompiler.this.setEnabled(false);
/* 226 */             throw e;
/*     */           } catch (ThreadDeath e) {
/* 228 */             BackgroundCompiler.this.setEnabled(false);
/* 229 */             throw e;
/*     */           }
/*     */           catch (Throwable e) {
/* 232 */             BackgroundCompiler.this.setEnabled(false);
/*     */ 
/* 235 */             BackgroundCompiler.this.reporter.reportDetailed(BackgroundCompiler.this, Report.newBuilder(BackgroundCompiler.REPORT_CANNOT_COMPILE_STRUCTURE_MODIFIER).callerParam(new Object[] { uncompiled }).error(e));
/*     */           }
/*     */ 
/* 241 */           return modifier;
/*     */         }
/*     */ 
/*     */       };
/*     */       try
/*     */       {
/* 249 */         if (this.compiler.lookupClassLoader(uncompiled)) {
/*     */           try {
/* 251 */             worker.call();
/*     */           }
/*     */           catch (Exception e) {
/* 254 */             e.printStackTrace();
/*     */           }
/*     */ 
/*     */         }
/*     */         else
/*     */         {
/* 260 */           this.executor.submit(worker);
/*     */         }
/*     */ 
/*     */       }
/*     */       catch (RejectedExecutionException e)
/*     */       {
/* 267 */         this.reporter.reportWarning(this, Report.newBuilder(REPORT_CANNOT_SCHEDULE_COMPILATION).error(e));
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public <TKey> void addListener(StructureModifier<TKey> uncompiled, CompileListener<TKey> listener)
/*     */   {
/* 279 */     synchronized (this.listenerLock) {
/* 280 */       StructureCompiler.StructureKey key = new StructureCompiler.StructureKey(uncompiled);
/*     */ 
/* 283 */       List list = (List)this.listeners.get(key);
/*     */ 
/* 285 */       if (list != null)
/* 286 */         list.add(listener);
/*     */     }
/*     */   }
/*     */ 
/*     */   private double getPermGenUsage()
/*     */   {
/* 296 */     for (MemoryPoolMXBean item : ManagementFactory.getMemoryPoolMXBeans()) {
/* 297 */       if (item.getName().contains("Perm Gen")) {
/* 298 */         MemoryUsage usage = item.getUsage();
/* 299 */         return usage.getUsed() / usage.getCommitted();
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 304 */     return 0.0D;
/*     */   }
/*     */ 
/*     */   public void shutdownAll()
/*     */   {
/* 311 */     shutdownAll(2000L, TimeUnit.MILLISECONDS);
/*     */   }
/*     */ 
/*     */   public void shutdownAll(long timeout, TimeUnit unit)
/*     */   {
/* 320 */     setEnabled(false);
/* 321 */     this.shuttingDown = true;
/* 322 */     this.executor.shutdown();
/*     */     try
/*     */     {
/* 325 */       this.executor.awaitTermination(timeout, unit);
/*     */     }
/*     */     catch (InterruptedException e) {
/* 328 */       e.printStackTrace();
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean isEnabled()
/*     */   {
/* 337 */     return this.enabled;
/*     */   }
/*     */ 
/*     */   public void setEnabled(boolean enabled)
/*     */   {
/* 345 */     this.enabled = enabled;
/*     */   }
/*     */ 
/*     */   public double getDisablePermGenFraction()
/*     */   {
/* 353 */     return this.disablePermGenFraction;
/*     */   }
/*     */ 
/*     */   public void setDisablePermGenFraction(double fraction)
/*     */   {
/* 361 */     this.disablePermGenFraction = fraction;
/*     */   }
/*     */ 
/*     */   public StructureCompiler getCompiler()
/*     */   {
/* 369 */     return this.compiler;
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.reflect.compiler.BackgroundCompiler
 * JD-Core Version:    0.6.2
 */