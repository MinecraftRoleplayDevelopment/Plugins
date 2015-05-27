/*     */ package com.comphenix.executors;
/*     */ 
/*     */ import com.google.common.collect.MapMaker;
/*     */ import com.google.common.util.concurrent.FutureCallback;
/*     */ import com.google.common.util.concurrent.Futures;
/*     */ import com.google.common.util.concurrent.ListenableFuture;
/*     */ import java.util.Collections;
/*     */ import java.util.Set;
/*     */ import java.util.WeakHashMap;
/*     */ import java.util.concurrent.ConcurrentMap;
/*     */ import java.util.concurrent.ExecutorService;
/*     */ import java.util.concurrent.Future;
/*     */ import org.bukkit.event.Event;
/*     */ import org.bukkit.event.EventException;
/*     */ import org.bukkit.event.EventPriority;
/*     */ import org.bukkit.event.Listener;
/*     */ import org.bukkit.event.server.PluginDisableEvent;
/*     */ import org.bukkit.plugin.EventExecutor;
/*     */ import org.bukkit.plugin.Plugin;
/*     */ 
/*     */ class PluginDisabledListener
/*     */   implements Listener
/*     */ {
/*  24 */   private static ConcurrentMap<Plugin, PluginDisabledListener> listeners = new MapMaker().weakKeys().makeMap();
/*     */ 
/*  27 */   private Set<Future<?>> futures = Collections.newSetFromMap(new WeakHashMap());
/*  28 */   private Set<ExecutorService> services = Collections.newSetFromMap(new WeakHashMap());
/*  29 */   private Object setLock = new Object();
/*     */   private final Plugin plugin;
/*     */   private boolean disabled;
/*     */ 
/*     */   private PluginDisabledListener(Plugin plugin)
/*     */   {
/*  36 */     this.plugin = plugin;
/*     */   }
/*     */ 
/*     */   public static PluginDisabledListener getListener(Plugin plugin)
/*     */   {
/*  45 */     PluginDisabledListener result = (PluginDisabledListener)listeners.get(plugin);
/*     */ 
/*  47 */     if (result == null) {
/*  48 */       PluginDisabledListener created = new PluginDisabledListener(plugin);
/*  49 */       result = (PluginDisabledListener)listeners.putIfAbsent(plugin, created);
/*     */ 
/*  51 */       if (result == null)
/*     */       {
/*  53 */         BukkitFutures.registerEventExecutor(plugin, PluginDisableEvent.class, EventPriority.NORMAL, new EventExecutor()
/*     */         {
/*     */           public void execute(Listener listener, Event event) throws EventException {
/*  56 */             if ((event instanceof PluginDisableEvent))
/*  57 */               this.val$created.onPluginDisabled((PluginDisableEvent)event);
/*     */           }
/*     */         });
/*  62 */         result = created;
/*     */       }
/*     */     }
/*  65 */     return result;
/*     */   }
/*     */ 
/*     */   public void addFuture(final ListenableFuture<?> future)
/*     */   {
/*  73 */     synchronized (this.setLock) {
/*  74 */       if (this.disabled)
/*  75 */         processFuture(future);
/*     */       else {
/*  77 */         this.futures.add(future);
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/*  82 */     Futures.addCallback(future, new FutureCallback()
/*     */     {
/*     */       public void onSuccess(Object value) {
/*  85 */         synchronized (PluginDisabledListener.this.setLock) {
/*  86 */           PluginDisabledListener.this.futures.remove(future);
/*     */         }
/*     */       }
/*     */ 
/*     */       public void onFailure(Throwable ex)
/*     */       {
/*  92 */         synchronized (PluginDisabledListener.this.setLock) {
/*  93 */           PluginDisabledListener.this.futures.remove(future);
/*     */         }
/*     */       }
/*     */     });
/*     */   }
/*     */ 
/*     */   public void addService(ExecutorService service)
/*     */   {
/* 104 */     synchronized (this.setLock) {
/* 105 */       if (this.disabled)
/* 106 */         processService(service);
/*     */       else
/* 108 */         this.services.add(service);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void onPluginDisabled(PluginDisableEvent e)
/*     */   {
/* 115 */     if (e.getPlugin().equals(this.plugin))
/* 116 */       synchronized (this.setLock) {
/* 117 */         this.disabled = true;
/*     */ 
/* 120 */         for (Future future : this.futures) {
/* 121 */           processFuture(future);
/*     */         }
/* 123 */         for (ExecutorService service : this.services)
/* 124 */           processService(service);
/*     */       }
/*     */   }
/*     */ 
/*     */   private void processFuture(Future<?> future)
/*     */   {
/* 131 */     if (!future.isDone())
/* 132 */       future.cancel(true);
/*     */   }
/*     */ 
/*     */   private void processService(ExecutorService service)
/*     */   {
/* 137 */     service.shutdownNow();
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.executors.PluginDisabledListener
 * JD-Core Version:    0.6.2
 */