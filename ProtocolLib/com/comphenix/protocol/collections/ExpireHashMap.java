/*     */ package com.comphenix.protocol.collections;
/*     */ 
/*     */ import com.google.common.base.Function;
/*     */ import com.google.common.base.Objects;
/*     */ import com.google.common.base.Preconditions;
/*     */ import com.google.common.base.Ticker;
/*     */ import com.google.common.collect.Maps;
/*     */ import com.google.common.primitives.Longs;
/*     */ import java.util.Collection;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.PriorityQueue;
/*     */ import java.util.Set;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ 
/*     */ public class ExpireHashMap<K, V>
/*     */ {
/*  52 */   private Map<K, ExpireHashMap<K, V>.ExpireEntry> keyLookup = new HashMap();
/*  53 */   private PriorityQueue<ExpireHashMap<K, V>.ExpireEntry> expireQueue = new PriorityQueue();
/*     */ 
/*  56 */   private Map<K, V> valueView = Maps.transformValues(this.keyLookup, new Function()
/*     */   {
/*     */     public V apply(ExpireHashMap<K, V>.ExpireEntry entry) {
/*  59 */       return entry.expireValue;
/*     */     }
/*     */   });
/*     */   private Ticker ticker;
/*     */ 
/*     */   public ExpireHashMap()
/*     */   {
/*  70 */     this(Ticker.systemTicker());
/*     */   }
/*     */ 
/*     */   public ExpireHashMap(Ticker ticker)
/*     */   {
/*  78 */     this.ticker = ticker;
/*     */   }
/*     */ 
/*     */   public V get(K key)
/*     */   {
/*  87 */     evictExpired();
/*     */ 
/*  89 */     ExpireEntry entry = (ExpireEntry)this.keyLookup.get(key);
/*  90 */     return entry != null ? entry.expireValue : null;
/*     */   }
/*     */ 
/*     */   public V put(K key, V value, long expireDelay, TimeUnit expireUnit)
/*     */   {
/* 102 */     Preconditions.checkNotNull(expireUnit, "expireUnit cannot be NULL");
/* 103 */     Preconditions.checkState(expireDelay > 0L, "expireDelay cannot be equal or less than zero.");
/* 104 */     evictExpired();
/*     */ 
/* 106 */     ExpireEntry entry = new ExpireEntry(this.ticker.read() + TimeUnit.NANOSECONDS.convert(expireDelay, expireUnit), key, value);
/*     */ 
/* 110 */     ExpireEntry previous = (ExpireEntry)this.keyLookup.put(key, entry);
/*     */ 
/* 113 */     this.expireQueue.add(entry);
/* 114 */     return previous != null ? previous.expireValue : null;
/*     */   }
/*     */ 
/*     */   public boolean containsKey(K key)
/*     */   {
/* 123 */     evictExpired();
/* 124 */     return this.keyLookup.containsKey(key);
/*     */   }
/*     */ 
/*     */   public boolean containsValue(V value)
/*     */   {
/* 133 */     evictExpired();
/*     */ 
/* 136 */     for (ExpireEntry entry : this.keyLookup.values()) {
/* 137 */       if (Objects.equal(value, entry.expireValue)) {
/* 138 */         return true;
/*     */       }
/*     */     }
/* 141 */     return false;
/*     */   }
/*     */ 
/*     */   public V removeKey(K key)
/*     */   {
/* 150 */     evictExpired();
/*     */ 
/* 152 */     ExpireEntry entry = (ExpireEntry)this.keyLookup.remove(key);
/* 153 */     return entry != null ? entry.expireValue : null;
/*     */   }
/*     */ 
/*     */   public int size()
/*     */   {
/* 161 */     evictExpired();
/* 162 */     return this.keyLookup.size();
/*     */   }
/*     */ 
/*     */   public Set<K> keySet()
/*     */   {
/* 170 */     evictExpired();
/* 171 */     return this.keyLookup.keySet();
/*     */   }
/*     */ 
/*     */   public Collection<V> values()
/*     */   {
/* 179 */     evictExpired();
/* 180 */     return this.valueView.values();
/*     */   }
/*     */ 
/*     */   public Set<Map.Entry<K, V>> entrySet()
/*     */   {
/* 188 */     evictExpired();
/* 189 */     return this.valueView.entrySet();
/*     */   }
/*     */ 
/*     */   public Map<K, V> asMap()
/*     */   {
/* 197 */     evictExpired();
/* 198 */     return this.valueView;
/*     */   }
/*     */ 
/*     */   public void collect()
/*     */   {
/* 208 */     evictExpired();
/*     */ 
/* 211 */     this.expireQueue.clear();
/* 212 */     this.expireQueue.addAll(this.keyLookup.values());
/*     */   }
/*     */ 
/*     */   public void clear()
/*     */   {
/* 219 */     this.keyLookup.clear();
/* 220 */     this.expireQueue.clear();
/*     */   }
/*     */ 
/*     */   protected void evictExpired()
/*     */   {
/* 229 */     long currentTime = this.ticker.read();
/*     */ 
/* 232 */     while ((this.expireQueue.size() > 0) && (((ExpireEntry)this.expireQueue.peek()).expireTime <= currentTime)) {
/* 233 */       ExpireEntry entry = (ExpireEntry)this.expireQueue.poll();
/*     */ 
/* 235 */       if (entry == this.keyLookup.get(entry.expireKey))
/* 236 */         this.keyLookup.remove(entry.expireKey);
/*     */     }
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 243 */     return this.valueView.toString();
/*     */   }
/*     */ 
/*     */   private class ExpireEntry
/*     */     implements Comparable<ExpireHashMap<K, V>.ExpireEntry>
/*     */   {
/*     */     public final long expireTime;
/*     */     public final K expireKey;
/*     */     public final V expireValue;
/*     */ 
/*     */     public ExpireEntry(K arg3, V expireKey)
/*     */     {
/*  35 */       this.expireTime = expireTime;
/*  36 */       this.expireKey = expireKey;
/*  37 */       this.expireValue = expireValue;
/*     */     }
/*     */ 
/*     */     public int compareTo(ExpireHashMap<K, V>.ExpireEntry o)
/*     */     {
/*  42 */       return Longs.compare(this.expireTime, o.expireTime);
/*     */     }
/*     */ 
/*     */     public String toString()
/*     */     {
/*  47 */       return "ExpireEntry [expireTime=" + this.expireTime + ", expireKey=" + this.expireKey + ", expireValue=" + this.expireValue + "]";
/*     */     }
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.collections.ExpireHashMap
 * JD-Core Version:    0.6.2
 */