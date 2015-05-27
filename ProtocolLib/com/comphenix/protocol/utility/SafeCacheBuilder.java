/*     */ package com.comphenix.protocol.utility;
/*     */ 
/*     */ import com.comphenix.protocol.reflect.FieldAccessException;
/*     */ import com.google.common.base.Ticker;
/*     */ import com.google.common.cache.CacheBuilder;
/*     */ import com.google.common.cache.CacheLoader;
/*     */ import com.google.common.cache.RemovalListener;
/*     */ import java.lang.reflect.Method;
/*     */ import java.util.concurrent.ConcurrentMap;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ 
/*     */ public class SafeCacheBuilder<K, V>
/*     */ {
/*     */   private CacheBuilder<K, V> builder;
/*     */   private static Method BUILD_METHOD;
/*     */   private static Method AS_MAP_METHOD;
/*     */ 
/*     */   private SafeCacheBuilder()
/*     */   {
/*  26 */     this.builder = CacheBuilder.newBuilder();
/*     */   }
/*     */ 
/*     */   public static <K, V> SafeCacheBuilder<K, V> newBuilder()
/*     */   {
/*  35 */     return new SafeCacheBuilder();
/*     */   }
/*     */ 
/*     */   public SafeCacheBuilder<K, V> concurrencyLevel(int concurrencyLevel)
/*     */   {
/*  62 */     this.builder.concurrencyLevel(concurrencyLevel);
/*  63 */     return this;
/*     */   }
/*     */ 
/*     */   public SafeCacheBuilder<K, V> expireAfterAccess(long duration, TimeUnit unit)
/*     */   {
/*  94 */     this.builder.expireAfterAccess(duration, unit);
/*  95 */     return this;
/*     */   }
/*     */ 
/*     */   public SafeCacheBuilder<K, V> expireAfterWrite(long duration, TimeUnit unit)
/*     */   {
/* 123 */     this.builder.expireAfterWrite(duration, unit);
/* 124 */     return this;
/*     */   }
/*     */ 
/*     */   public SafeCacheBuilder<K, V> initialCapacity(int initialCapacity)
/*     */   {
/* 139 */     this.builder.initialCapacity(initialCapacity);
/* 140 */     return this;
/*     */   }
/*     */ 
/*     */   public SafeCacheBuilder<K, V> maximumSize(int size)
/*     */   {
/* 164 */     this.builder.maximumSize(size);
/* 165 */     return this;
/*     */   }
/*     */ 
/*     */   public <K1 extends K, V1 extends V> SafeCacheBuilder<K1, V1> removalListener(RemovalListener<? super K1, ? super V1> listener)
/*     */   {
/* 203 */     this.builder.removalListener(listener);
/* 204 */     return this;
/*     */   }
/*     */ 
/*     */   public SafeCacheBuilder<K, V> ticker(Ticker ticker)
/*     */   {
/* 219 */     this.builder.ticker(ticker);
/* 220 */     return this;
/*     */   }
/*     */ 
/*     */   public SafeCacheBuilder<K, V> softValues()
/*     */   {
/* 242 */     this.builder.softValues();
/* 243 */     return this;
/*     */   }
/*     */ 
/*     */   public SafeCacheBuilder<K, V> weakKeys()
/*     */   {
/* 257 */     this.builder.weakKeys();
/* 258 */     return this;
/*     */   }
/*     */ 
/*     */   public SafeCacheBuilder<K, V> weakValues()
/*     */   {
/* 277 */     this.builder.weakValues();
/* 278 */     return this;
/*     */   }
/*     */ 
/*     */   public <K1 extends K, V1 extends V> ConcurrentMap<K1, V1> build(CacheLoader<? super K1, V1> loader)
/*     */   {
/* 289 */     Object cache = null;
/*     */ 
/* 291 */     if (BUILD_METHOD == null) {
/*     */       try {
/* 293 */         BUILD_METHOD = this.builder.getClass().getDeclaredMethod("build", new Class[] { CacheLoader.class });
/* 294 */         BUILD_METHOD.setAccessible(true);
/*     */       } catch (Exception e) {
/* 296 */         throw new FieldAccessException("Unable to find CacheBuilder.build(CacheLoader)", e);
/*     */       }
/*     */     }
/*     */ 
/*     */     try
/*     */     {
/* 302 */       cache = BUILD_METHOD.invoke(this.builder, new Object[] { loader });
/*     */     } catch (Exception e) {
/* 304 */       throw new FieldAccessException("Unable to invoke " + BUILD_METHOD + " on " + this.builder, e);
/*     */     }
/*     */ 
/* 307 */     if (AS_MAP_METHOD == null) {
/*     */       try {
/* 309 */         AS_MAP_METHOD = cache.getClass().getMethod("asMap", new Class[0]);
/* 310 */         AS_MAP_METHOD.setAccessible(true);
/*     */       } catch (Exception e) {
/* 312 */         throw new FieldAccessException("Unable to find Cache.asMap() in " + cache, e);
/*     */       }
/*     */     }
/*     */ 
/*     */     try
/*     */     {
/* 318 */       return (ConcurrentMap)AS_MAP_METHOD.invoke(cache, new Object[0]);
/*     */     } catch (Exception e) {
/* 320 */       throw new FieldAccessException("Unable to invoke " + AS_MAP_METHOD + " on " + cache, e);
/*     */     }
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.utility.SafeCacheBuilder
 * JD-Core Version:    0.6.2
 */