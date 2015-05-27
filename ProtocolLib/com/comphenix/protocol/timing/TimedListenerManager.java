/*     */ package com.comphenix.protocol.timing;
/*     */ 
/*     */ import com.comphenix.protocol.events.PacketListener;
/*     */ import com.google.common.collect.ImmutableMap;
/*     */ import com.google.common.collect.ImmutableMap.Builder;
/*     */ import com.google.common.collect.Maps;
/*     */ import java.util.Calendar;
/*     */ import java.util.Date;
/*     */ import java.util.Set;
/*     */ import java.util.concurrent.ConcurrentMap;
/*     */ import java.util.concurrent.atomic.AtomicBoolean;
/*     */ import org.bukkit.plugin.Plugin;
/*     */ 
/*     */ public class TimedListenerManager
/*     */ {
/*  28 */   private static final TimedListenerManager INSTANCE = new TimedListenerManager();
/*     */ 
/*  30 */   private static final AtomicBoolean timing = new AtomicBoolean();
/*     */   private volatile Date started;
/*     */   private volatile Date stopped;
/*  36 */   private ConcurrentMap<String, ImmutableMap<ListenerType, TimedTracker>> map = Maps.newConcurrentMap();
/*     */ 
/*     */   public static TimedListenerManager getInstance()
/*     */   {
/*  45 */     return INSTANCE;
/*     */   }
/*     */ 
/*     */   public boolean startTiming()
/*     */   {
/*  53 */     if (setTiming(true)) {
/*  54 */       this.started = Calendar.getInstance().getTime();
/*  55 */       return true;
/*     */     }
/*  57 */     return false;
/*     */   }
/*     */ 
/*     */   public boolean stopTiming()
/*     */   {
/*  65 */     if (setTiming(false)) {
/*  66 */       this.stopped = Calendar.getInstance().getTime();
/*  67 */       return true;
/*     */     }
/*  69 */     return false;
/*     */   }
/*     */ 
/*     */   public Date getStarted()
/*     */   {
/*  77 */     return this.started;
/*     */   }
/*     */ 
/*     */   public Date getStopped()
/*     */   {
/*  85 */     return this.stopped;
/*     */   }
/*     */ 
/*     */   private boolean setTiming(boolean value)
/*     */   {
/*  94 */     return timing.compareAndSet(!value, value);
/*     */   }
/*     */ 
/*     */   public boolean isTiming()
/*     */   {
/* 102 */     return timing.get();
/*     */   }
/*     */ 
/*     */   public void clear()
/*     */   {
/* 109 */     this.map.clear();
/*     */   }
/*     */ 
/*     */   public Set<String> getTrackedPlugins()
/*     */   {
/* 117 */     return this.map.keySet();
/*     */   }
/*     */ 
/*     */   public TimedTracker getTracker(Plugin plugin, ListenerType type)
/*     */   {
/* 127 */     return getTracker(plugin.getName(), type);
/*     */   }
/*     */ 
/*     */   public TimedTracker getTracker(PacketListener listener, ListenerType type)
/*     */   {
/* 137 */     return getTracker(listener.getPlugin().getName(), type);
/*     */   }
/*     */ 
/*     */   public TimedTracker getTracker(String pluginName, ListenerType type)
/*     */   {
/* 147 */     return (TimedTracker)getTrackers(pluginName).get(type);
/*     */   }
/*     */ 
/*     */   private ImmutableMap<ListenerType, TimedTracker> getTrackers(String pluginName)
/*     */   {
/* 156 */     ImmutableMap trackers = (ImmutableMap)this.map.get(pluginName);
/*     */ 
/* 159 */     if (trackers == null) {
/* 160 */       ImmutableMap created = newTrackerMap();
/* 161 */       trackers = (ImmutableMap)this.map.putIfAbsent(pluginName, created);
/*     */ 
/* 164 */       if (trackers == null) {
/* 165 */         trackers = created;
/*     */       }
/*     */     }
/* 168 */     return trackers;
/*     */   }
/*     */ 
/*     */   private ImmutableMap<ListenerType, TimedTracker> newTrackerMap()
/*     */   {
/* 176 */     ImmutableMap.Builder builder = ImmutableMap.builder();
/*     */ 
/* 179 */     for (ListenerType type : ListenerType.values()) {
/* 180 */       builder.put(type, new TimedTracker());
/*     */     }
/* 182 */     return builder.build();
/*     */   }
/*     */ 
/*     */   public static enum ListenerType
/*     */   {
/*  21 */     ASYNC_SERVER_SIDE, 
/*  22 */     ASYNC_CLIENT_SIDE, 
/*  23 */     SYNC_SERVER_SIDE, 
/*  24 */     SYNC_CLIENT_SIDE;
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.timing.TimedListenerManager
 * JD-Core Version:    0.6.2
 */