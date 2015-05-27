/*     */ package com.comphenix.protocol.collections;
/*     */ 
/*     */ import com.google.common.base.Preconditions;
/*     */ import com.google.common.collect.Maps;
/*     */ import java.util.Arrays;
/*     */ import java.util.Map;
/*     */ 
/*     */ public class IntegerMap<T>
/*     */ {
/*     */   private T[] array;
/*     */   private int size;
/*     */ 
/*     */   public static <T> IntegerMap<T> newMap()
/*     */   {
/*  24 */     return new IntegerMap();
/*     */   }
/*     */ 
/*     */   public IntegerMap()
/*     */   {
/*  31 */     this(8);
/*     */   }
/*     */ 
/*     */   public IntegerMap(int initialCapacity)
/*     */   {
/*  40 */     Object[] backingArray = (Object[])new Object[initialCapacity];
/*  41 */     this.array = backingArray;
/*  42 */     this.size = 0;
/*     */   }
/*     */ 
/*     */   public T put(int key, T value)
/*     */   {
/*  52 */     ensureCapacity(key);
/*     */ 
/*  54 */     Object old = this.array[key];
/*  55 */     this.array[key] = Preconditions.checkNotNull(value, "value cannot be NULL");
/*     */ 
/*  57 */     if (old == null)
/*  58 */       this.size += 1;
/*  59 */     return old;
/*     */   }
/*     */ 
/*     */   public T remove(int key)
/*     */   {
/*  68 */     Object old = this.array[key];
/*  69 */     this.array[key] = null;
/*     */ 
/*  71 */     if (old != null)
/*  72 */       this.size -= 1;
/*  73 */     return old;
/*     */   }
/*     */ 
/*     */   protected void ensureCapacity(int key)
/*     */   {
/*  81 */     int newLength = this.array.length;
/*     */ 
/*  84 */     if (key < 0)
/*  85 */       throw new IllegalArgumentException("Negative key values are not permitted.");
/*  86 */     if (key < newLength) {
/*  87 */       return;
/*     */     }
/*  89 */     while (newLength <= key) {
/*  90 */       int next = newLength * 2;
/*     */ 
/*  92 */       newLength = next > newLength ? next : 2147483647;
/*     */     }
/*  94 */     this.array = Arrays.copyOf(this.array, newLength);
/*     */   }
/*     */ 
/*     */   public int size()
/*     */   {
/* 102 */     return this.size;
/*     */   }
/*     */ 
/*     */   public T get(int key)
/*     */   {
/* 111 */     if ((key >= 0) && (key < this.array.length))
/* 112 */       return this.array[key];
/* 113 */     return null;
/*     */   }
/*     */ 
/*     */   public boolean containsKey(int key)
/*     */   {
/* 122 */     return get(key) != null;
/*     */   }
/*     */ 
/*     */   public Map<Integer, Object> toMap()
/*     */   {
/* 130 */     Map map = Maps.newHashMap();
/*     */ 
/* 132 */     for (int i = 0; i < this.array.length; i++) {
/* 133 */       if (this.array[i] != null) {
/* 134 */         map.put(Integer.valueOf(i), this.array[i]);
/*     */       }
/*     */     }
/* 137 */     return map;
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.collections.IntegerMap
 * JD-Core Version:    0.6.2
 */