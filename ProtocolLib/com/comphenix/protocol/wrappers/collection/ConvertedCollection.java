/*     */ package com.comphenix.protocol.wrappers.collection;
/*     */ 
/*     */ import com.google.common.collect.Iterators;
/*     */ import com.google.common.collect.Lists;
/*     */ import java.lang.reflect.Array;
/*     */ import java.util.Collection;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ 
/*     */ public abstract class ConvertedCollection<VInner, VOuter> extends AbstractConverted<VInner, VOuter>
/*     */   implements Collection<VOuter>
/*     */ {
/*     */   private Collection<VInner> inner;
/*     */ 
/*     */   public ConvertedCollection(Collection<VInner> inner)
/*     */   {
/*  41 */     this.inner = inner;
/*     */   }
/*     */ 
/*     */   public boolean add(VOuter e)
/*     */   {
/*  46 */     return this.inner.add(toInner(e));
/*     */   }
/*     */ 
/*     */   public boolean addAll(Collection<? extends VOuter> c)
/*     */   {
/*  51 */     boolean modified = false;
/*     */ 
/*  53 */     for (Iterator i$ = c.iterator(); i$.hasNext(); ) { Object outer = i$.next();
/*  54 */       modified |= add(outer); }
/*  55 */     return modified;
/*     */   }
/*     */ 
/*     */   public void clear()
/*     */   {
/*  60 */     this.inner.clear();
/*     */   }
/*     */ 
/*     */   public boolean contains(Object o)
/*     */   {
/*  66 */     return this.inner.contains(toInner(o));
/*     */   }
/*     */ 
/*     */   public boolean containsAll(Collection<?> c)
/*     */   {
/*  71 */     for (Iterator i$ = c.iterator(); i$.hasNext(); ) { Object outer = i$.next();
/*  72 */       if (!contains(outer))
/*  73 */         return false;
/*     */     }
/*  75 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean isEmpty()
/*     */   {
/*  80 */     return this.inner.isEmpty();
/*     */   }
/*     */ 
/*     */   public Iterator<VOuter> iterator()
/*     */   {
/*  85 */     return Iterators.transform(this.inner.iterator(), getOuterConverter());
/*     */   }
/*     */ 
/*     */   public boolean remove(Object o)
/*     */   {
/*  91 */     return this.inner.remove(toInner(o));
/*     */   }
/*     */ 
/*     */   public boolean removeAll(Collection<?> c)
/*     */   {
/*  96 */     boolean modified = false;
/*     */ 
/*  98 */     for (Iterator i$ = c.iterator(); i$.hasNext(); ) { Object outer = i$.next();
/*  99 */       modified |= remove(outer); }
/* 100 */     return modified;
/*     */   }
/*     */ 
/*     */   public boolean retainAll(Collection<?> c)
/*     */   {
/* 106 */     List innerCopy = Lists.newArrayList();
/*     */ 
/* 109 */     for (Iterator i$ = c.iterator(); i$.hasNext(); ) { Object outer = i$.next();
/* 110 */       innerCopy.add(toInner(outer)); }
/* 111 */     return this.inner.retainAll(innerCopy);
/*     */   }
/*     */ 
/*     */   public int size()
/*     */   {
/* 116 */     return this.inner.size();
/*     */   }
/*     */ 
/*     */   public Object[] toArray()
/*     */   {
/* 122 */     Object[] array = this.inner.toArray();
/*     */ 
/* 124 */     for (int i = 0; i < array.length; i++)
/* 125 */       array[i] = toOuter(array[i]);
/* 126 */     return array;
/*     */   }
/*     */ 
/*     */   public <T> T[] toArray(T[] a)
/*     */   {
/* 132 */     Object[] array = a;
/* 133 */     int index = 0;
/*     */ 
/* 135 */     if (array.length < size()) {
/* 136 */       array = (Object[])Array.newInstance(a.getClass().getComponentType(), size());
/*     */     }
/*     */ 
/* 140 */     for (Iterator i$ = this.inner.iterator(); i$.hasNext(); ) { Object innerValue = i$.next();
/* 141 */       array[(index++)] = toOuter(innerValue); }
/* 142 */     return array;
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.wrappers.collection.ConvertedCollection
 * JD-Core Version:    0.6.2
 */