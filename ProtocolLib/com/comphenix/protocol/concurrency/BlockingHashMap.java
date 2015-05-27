/*     */ package com.comphenix.protocol.concurrency;
/*     */ 
/*     */ import com.comphenix.protocol.utility.SafeCacheBuilder;
/*     */ import com.google.common.cache.CacheLoader;
/*     */ import com.google.common.cache.RemovalCause;
/*     */ import com.google.common.cache.RemovalListener;
/*     */ import com.google.common.cache.RemovalNotification;
/*     */ import java.util.Collection;
/*     */ import java.util.Set;
/*     */ import java.util.concurrent.ConcurrentHashMap;
/*     */ import java.util.concurrent.ConcurrentMap;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ 
/*     */ public class BlockingHashMap<TKey, TValue>
/*     */ {
/*     */   private final ConcurrentMap<TKey, TValue> backingMap;
/*     */   private final ConcurrentMap<TKey, Object> locks;
/*     */ 
/*     */   public static <TKey, TValue> CacheLoader<TKey, TValue> newInvalidCacheLoader()
/*     */   {
/*  54 */     return new CacheLoader()
/*     */     {
/*     */       public TValue load(TKey key) throws Exception {
/*  57 */         throw new IllegalStateException("Illegal use. Access the map directly instead.");
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   public BlockingHashMap()
/*     */   {
/*  66 */     this.backingMap = SafeCacheBuilder.newBuilder().weakValues().removalListener(new RemovalListener()
/*     */     {
/*     */       public void onRemoval(RemovalNotification<TKey, TValue> entry)
/*     */       {
/*  73 */         if (entry.getCause() != RemovalCause.REPLACED)
/*  74 */           BlockingHashMap.this.locks.remove(entry.getKey());
/*     */       }
/*     */     }).build(newInvalidCacheLoader());
/*     */ 
/*  81 */     this.locks = new ConcurrentHashMap();
/*     */   }
/*     */ 
/*     */   public static <TKey, TValue> BlockingHashMap<TKey, TValue> create()
/*     */   {
/*  89 */     return new BlockingHashMap();
/*     */   }
/*     */ 
/*     */   public TValue get(TKey key)
/*     */     throws InterruptedException
/*     */   {
/*  99 */     if (key == null) {
/* 100 */       throw new IllegalArgumentException("key cannot be NULL.");
/*     */     }
/* 102 */     Object value = this.backingMap.get(key);
/*     */ 
/* 105 */     if (value == null) {
/* 106 */       Object lock = getLock(key);
/*     */ 
/* 108 */       synchronized (lock) {
/* 109 */         while (value == null) {
/* 110 */           lock.wait();
/* 111 */           value = this.backingMap.get(key);
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 116 */     return value;
/*     */   }
/*     */ 
/*     */   public TValue get(TKey key, long timeout, TimeUnit unit)
/*     */     throws InterruptedException
/*     */   {
/* 128 */     return get(key, timeout, unit, false);
/*     */   }
/*     */ 
/*     */   public TValue get(TKey key, long timeout, TimeUnit unit, boolean ignoreInterrupted)
/*     */     throws InterruptedException
/*     */   {
/* 144 */     if (key == null)
/* 145 */       throw new IllegalArgumentException("key cannot be NULL.");
/* 146 */     if (unit == null)
/* 147 */       throw new IllegalArgumentException("Unit cannot be NULL.");
/* 148 */     if (timeout < 0L) {
/* 149 */       throw new IllegalArgumentException("Timeout cannot be less than zero.");
/*     */     }
/* 151 */     Object value = this.backingMap.get(key);
/*     */ 
/* 154 */     if ((value == null) && (timeout > 0L)) {
/* 155 */       Object lock = getLock(key);
/* 156 */       long stopTimeNS = System.nanoTime() + unit.toNanos(timeout);
/*     */ 
/* 159 */       synchronized (lock) {
/* 160 */         while (value == null) {
/*     */           try {
/* 162 */             long remainingTime = stopTimeNS - System.nanoTime();
/*     */ 
/* 164 */             if (remainingTime > 0L) {
/* 165 */               TimeUnit.NANOSECONDS.timedWait(lock, remainingTime);
/* 166 */               value = this.backingMap.get(key);
/*     */             }
/*     */             else {
/* 169 */               break;
/*     */             }
/*     */           }
/*     */           catch (InterruptedException e) {
/* 173 */             if (!ignoreInterrupted)
/* 174 */               throw e;
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/* 179 */     return value;
/*     */   }
/*     */ 
/*     */   public TValue put(TKey key, TValue value)
/*     */   {
/* 192 */     if (value == null) {
/* 193 */       throw new IllegalArgumentException("This map doesn't support NULL values.");
/*     */     }
/* 195 */     Object previous = this.backingMap.put(key, value);
/* 196 */     Object lock = getLock(key);
/*     */ 
/* 199 */     synchronized (lock) {
/* 200 */       lock.notifyAll();
/* 201 */       return previous;
/*     */     }
/*     */   }
/*     */ 
/*     */   public TValue putIfAbsent(TKey key, TValue value)
/*     */   {
/* 212 */     if (value == null) {
/* 213 */       throw new IllegalArgumentException("This map doesn't support NULL values.");
/*     */     }
/* 215 */     Object previous = this.backingMap.putIfAbsent(key, value);
/*     */ 
/* 218 */     if (previous == null) {
/* 219 */       Object lock = getLock(key);
/*     */ 
/* 221 */       synchronized (lock) {
/* 222 */         lock.notifyAll();
/*     */       }
/*     */     }
/* 225 */     return previous;
/*     */   }
/*     */ 
/*     */   public int size() {
/* 229 */     return this.backingMap.size();
/*     */   }
/*     */ 
/*     */   public Collection<TValue> values() {
/* 233 */     return this.backingMap.values();
/*     */   }
/*     */ 
/*     */   public Set<TKey> keys() {
/* 237 */     return this.backingMap.keySet();
/*     */   }
/*     */ 
/*     */   private Object getLock(TKey key)
/*     */   {
/* 246 */     Object lock = this.locks.get(key);
/*     */ 
/* 248 */     if (lock == null) {
/* 249 */       Object created = new Object();
/*     */ 
/* 252 */       lock = this.locks.putIfAbsent(key, created);
/*     */ 
/* 255 */       if (lock == null) {
/* 256 */         lock = created;
/*     */       }
/*     */     }
/*     */ 
/* 260 */     return lock;
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.concurrency.BlockingHashMap
 * JD-Core Version:    0.6.2
 */