/*     */ package com.comphenix.protocol.concurrency;
/*     */ 
/*     */ import java.util.Arrays;
/*     */ import java.util.Collection;
/*     */ import java.util.HashSet;
/*     */ import java.util.Set;
/*     */ 
/*     */ public class IntegerSet
/*     */ {
/*     */   private final boolean[] array;
/*     */ 
/*     */   public IntegerSet(int maximumCount)
/*     */   {
/*  43 */     this.array = new boolean[maximumCount];
/*     */   }
/*     */ 
/*     */   public IntegerSet(int maximumCount, Collection<Integer> values)
/*     */   {
/*  54 */     this.array = new boolean[maximumCount];
/*  55 */     addAll(values);
/*     */   }
/*     */ 
/*     */   public boolean contains(int element)
/*     */   {
/*  64 */     return this.array[element];
/*     */   }
/*     */ 
/*     */   public void add(int element)
/*     */   {
/*  73 */     this.array[element] = true;
/*     */   }
/*     */ 
/*     */   public void addAll(Collection<Integer> packets)
/*     */   {
/*  81 */     for (Integer id : packets)
/*  82 */       add(id.intValue());
/*     */   }
/*     */ 
/*     */   public void remove(int element)
/*     */   {
/*  92 */     if ((element >= 0) && (element < this.array.length))
/*  93 */       this.array[element] = false;
/*     */   }
/*     */ 
/*     */   public void clear()
/*     */   {
/* 100 */     Arrays.fill(this.array, false);
/*     */   }
/*     */ 
/*     */   public Set<Integer> toSet()
/*     */   {
/* 108 */     Set elements = new HashSet();
/*     */ 
/* 110 */     for (int i = 0; i < this.array.length; i++) {
/* 111 */       if (this.array[i] != 0)
/* 112 */         elements.add(Integer.valueOf(i));
/*     */     }
/* 114 */     return elements;
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.concurrency.IntegerSet
 * JD-Core Version:    0.6.2
 */