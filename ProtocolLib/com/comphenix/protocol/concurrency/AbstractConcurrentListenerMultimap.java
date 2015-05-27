/*     */ package com.comphenix.protocol.concurrency;
/*     */ 
/*     */ import com.comphenix.protocol.PacketType;
/*     */ import com.comphenix.protocol.events.ListeningWhitelist;
/*     */ import com.comphenix.protocol.injector.PrioritizedListener;
/*     */ import com.google.common.collect.Iterables;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.List;
/*     */ import java.util.Set;
/*     */ import java.util.concurrent.ConcurrentHashMap;
/*     */ import java.util.concurrent.ConcurrentMap;
/*     */ 
/*     */ public abstract class AbstractConcurrentListenerMultimap<TListener>
/*     */ {
/*     */   private ConcurrentMap<PacketType, SortedCopyOnWriteArray<PrioritizedListener<TListener>>> mapListeners;
/*     */ 
/*     */   public AbstractConcurrentListenerMultimap()
/*     */   {
/*  42 */     this.mapListeners = new ConcurrentHashMap();
/*     */   }
/*     */ 
/*     */   public void addListener(TListener listener, ListeningWhitelist whitelist)
/*     */   {
/*  51 */     PrioritizedListener prioritized = new PrioritizedListener(listener, whitelist.getPriority());
/*     */ 
/*  53 */     for (PacketType type : whitelist.getTypes())
/*  54 */       addListener(type, prioritized);
/*     */   }
/*     */ 
/*     */   private void addListener(PacketType type, PrioritizedListener<TListener> listener)
/*     */   {
/*  60 */     SortedCopyOnWriteArray list = (SortedCopyOnWriteArray)this.mapListeners.get(type);
/*     */ 
/*  63 */     if (list == null)
/*     */     {
/*  66 */       SortedCopyOnWriteArray value = new SortedCopyOnWriteArray();
/*     */ 
/*  69 */       list = (SortedCopyOnWriteArray)this.mapListeners.putIfAbsent(type, value);
/*     */ 
/*  71 */       if (list == null) {
/*  72 */         list = value;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/*  77 */     list.add(listener);
/*     */   }
/*     */ 
/*     */   public List<PacketType> removeListener(TListener listener, ListeningWhitelist whitelist)
/*     */   {
/*  87 */     List removedPackets = new ArrayList();
/*     */ 
/*  90 */     for (PacketType type : whitelist.getTypes()) {
/*  91 */       SortedCopyOnWriteArray list = (SortedCopyOnWriteArray)this.mapListeners.get(type);
/*     */ 
/*  94 */       if (list != null)
/*     */       {
/*  96 */         if (list.size() > 0)
/*     */         {
/*  98 */           list.remove(new PrioritizedListener(listener, whitelist.getPriority()));
/*     */ 
/* 100 */           if (list.size() == 0) {
/* 101 */             this.mapListeners.remove(type);
/* 102 */             removedPackets.add(type);
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 108 */     return removedPackets;
/*     */   }
/*     */ 
/*     */   public Collection<PrioritizedListener<TListener>> getListener(PacketType type)
/*     */   {
/* 119 */     return (Collection)this.mapListeners.get(type);
/*     */   }
/*     */ 
/*     */   public Iterable<PrioritizedListener<TListener>> values()
/*     */   {
/* 127 */     return Iterables.concat(this.mapListeners.values());
/*     */   }
/*     */ 
/*     */   public Set<PacketType> keySet()
/*     */   {
/* 135 */     return this.mapListeners.keySet();
/*     */   }
/*     */ 
/*     */   protected void clearListeners()
/*     */   {
/* 142 */     this.mapListeners.clear();
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.concurrency.AbstractConcurrentListenerMultimap
 * JD-Core Version:    0.6.2
 */