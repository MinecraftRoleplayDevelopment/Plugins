/*     */ package com.comphenix.protocol.concurrency;
/*     */ 
/*     */ import com.google.common.base.Objects;
/*     */ import com.google.common.collect.Iterables;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ 
/*     */ public class SortedCopyOnWriteArray<T extends Comparable<T>>
/*     */   implements Iterable<T>, Collection<T>
/*     */ {
/*     */   private volatile List<T> list;
/*     */ 
/*     */   public SortedCopyOnWriteArray()
/*     */   {
/*  41 */     this.list = new ArrayList();
/*     */   }
/*     */ 
/*     */   public SortedCopyOnWriteArray(Collection<T> wrapped)
/*     */   {
/*  49 */     this.list = new ArrayList(wrapped);
/*     */   }
/*     */ 
/*     */   public SortedCopyOnWriteArray(Collection<T> wrapped, boolean sort)
/*     */   {
/*  58 */     this.list = new ArrayList(wrapped);
/*     */ 
/*  60 */     if (sort)
/*  61 */       Collections.sort(this.list);
/*     */   }
/*     */ 
/*     */   public synchronized boolean add(T value)
/*     */   {
/*  72 */     if (value == null) {
/*  73 */       throw new IllegalArgumentException("value cannot be NULL");
/*     */     }
/*  75 */     List copy = new ArrayList();
/*     */ 
/*  77 */     for (Comparable element : this.list)
/*     */     {
/*  79 */       if ((value != null) && (value.compareTo(element) < 0)) {
/*  80 */         copy.add(value);
/*  81 */         value = null;
/*     */       }
/*  83 */       copy.add(element);
/*     */     }
/*     */ 
/*  87 */     if (value != null) {
/*  88 */       copy.add(value);
/*     */     }
/*  90 */     this.list = copy;
/*  91 */     return true;
/*     */   }
/*     */ 
/*     */   public synchronized boolean addAll(Collection<? extends T> values)
/*     */   {
/*  96 */     if (values == null)
/*  97 */       throw new IllegalArgumentException("values cannot be NULL");
/*  98 */     if (values.size() == 0) {
/*  99 */       return false;
/*     */     }
/* 101 */     List copy = new ArrayList();
/*     */ 
/* 104 */     copy.addAll(this.list);
/* 105 */     copy.addAll(values);
/* 106 */     Collections.sort(copy);
/*     */ 
/* 108 */     this.list = copy;
/* 109 */     return true;
/*     */   }
/*     */ 
/*     */   public synchronized boolean remove(Object value)
/*     */   {
/* 120 */     List copy = new ArrayList();
/* 121 */     boolean result = false;
/*     */ 
/* 127 */     for (Comparable element : this.list) {
/* 128 */       if (!Objects.equal(value, element))
/* 129 */         copy.add(element);
/*     */       else {
/* 131 */         result = true;
/*     */       }
/*     */     }
/*     */ 
/* 135 */     this.list = copy;
/* 136 */     return result;
/*     */   }
/*     */ 
/*     */   public boolean removeAll(Collection<?> values)
/*     */   {
/* 142 */     if (values == null)
/* 143 */       throw new IllegalArgumentException("values cannot be NULL");
/* 144 */     if (values.size() == 0) {
/* 145 */       return false;
/*     */     }
/* 147 */     List copy = new ArrayList();
/*     */ 
/* 149 */     copy.addAll(this.list);
/* 150 */     copy.removeAll(values);
/*     */ 
/* 152 */     this.list = copy;
/* 153 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean retainAll(Collection<?> values)
/*     */   {
/* 159 */     if (values == null)
/* 160 */       throw new IllegalArgumentException("values cannot be NULL");
/* 161 */     if (values.size() == 0) {
/* 162 */       return false;
/*     */     }
/* 164 */     List copy = new ArrayList();
/*     */ 
/* 166 */     copy.addAll(this.list);
/* 167 */     copy.removeAll(values);
/*     */ 
/* 169 */     this.list = copy;
/* 170 */     return true;
/*     */   }
/*     */ 
/*     */   public synchronized void remove(int index)
/*     */   {
/* 178 */     List copy = new ArrayList(this.list);
/*     */ 
/* 180 */     copy.remove(index);
/* 181 */     this.list = copy;
/*     */   }
/*     */ 
/*     */   public T get(int index)
/*     */   {
/* 190 */     return (Comparable)this.list.get(index);
/*     */   }
/*     */ 
/*     */   public int size()
/*     */   {
/* 198 */     return this.list.size();
/*     */   }
/*     */ 
/*     */   public Iterator<T> iterator()
/*     */   {
/* 206 */     return Iterables.unmodifiableIterable(this.list).iterator();
/*     */   }
/*     */ 
/*     */   public void clear()
/*     */   {
/* 211 */     this.list = new ArrayList();
/*     */   }
/*     */ 
/*     */   public boolean contains(Object value)
/*     */   {
/* 216 */     return this.list.contains(value);
/*     */   }
/*     */ 
/*     */   public boolean containsAll(Collection<?> values)
/*     */   {
/* 221 */     return this.list.containsAll(values);
/*     */   }
/*     */ 
/*     */   public boolean isEmpty()
/*     */   {
/* 226 */     return this.list.isEmpty();
/*     */   }
/*     */ 
/*     */   public Object[] toArray()
/*     */   {
/* 231 */     return this.list.toArray();
/*     */   }
/*     */ 
/*     */   public <T> T[] toArray(T[] a)
/*     */   {
/* 237 */     return this.list.toArray(a);
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 242 */     return this.list.toString();
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.concurrency.SortedCopyOnWriteArray
 * JD-Core Version:    0.6.2
 */