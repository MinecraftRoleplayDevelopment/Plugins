/*     */ package com.comphenix.protocol.wrappers.collection;
/*     */ 
/*     */ import com.google.common.base.Preconditions;
/*     */ import com.google.common.collect.Iterators;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collection;
/*     */ import java.util.Iterator;
/*     */ import java.util.Set;
/*     */ 
/*     */ public class CachedCollection<T>
/*     */   implements Collection<T>
/*     */ {
/*     */   protected Set<T> delegate;
/*     */   protected Object[] cache;
/*     */ 
/*     */   public CachedCollection(Set<T> delegate)
/*     */   {
/*  31 */     this.delegate = ((Set)Preconditions.checkNotNull(delegate, "delegate cannot be NULL."));
/*     */   }
/*     */ 
/*     */   private void initializeCache()
/*     */   {
/*  38 */     if (this.cache == null)
/*  39 */       this.cache = new Object[this.delegate.size()];
/*     */   }
/*     */ 
/*     */   private void growCache()
/*     */   {
/*  48 */     if (this.cache == null)
/*  49 */       return;
/*  50 */     int newLength = this.cache.length;
/*     */ 
/*  53 */     while (newLength < this.delegate.size()) {
/*  54 */       newLength *= 2;
/*     */     }
/*  56 */     if (newLength != this.cache.length)
/*  57 */       this.cache = Arrays.copyOf(this.cache, newLength);
/*     */   }
/*     */ 
/*     */   public int size()
/*     */   {
/*  63 */     return this.delegate.size();
/*     */   }
/*     */ 
/*     */   public boolean isEmpty()
/*     */   {
/*  68 */     return this.delegate.isEmpty();
/*     */   }
/*     */ 
/*     */   public boolean contains(Object o)
/*     */   {
/*  73 */     return this.delegate.contains(o);
/*     */   }
/*     */ 
/*     */   public Iterator<T> iterator()
/*     */   {
/*  78 */     final Iterator source = this.delegate.iterator();
/*  79 */     initializeCache();
/*     */ 
/*  81 */     return new Iterator() {
/*  82 */       int currentIndex = -1;
/*  83 */       int iteratorIndex = -1;
/*     */ 
/*     */       public boolean hasNext()
/*     */       {
/*  87 */         return this.currentIndex < CachedCollection.this.delegate.size() - 1;
/*     */       }
/*     */ 
/*     */       public T next()
/*     */       {
/*  93 */         this.currentIndex += 1;
/*     */ 
/*  95 */         if (CachedCollection.this.cache[this.currentIndex] == null) {
/*  96 */           CachedCollection.this.cache[this.currentIndex] = getSourceValue();
/*     */         }
/*  98 */         return CachedCollection.this.cache[this.currentIndex];
/*     */       }
/*     */ 
/*     */       public void remove()
/*     */       {
/* 104 */         getSourceValue();
/* 105 */         source.remove();
/*     */       }
/*     */ 
/*     */       private T getSourceValue()
/*     */       {
/* 112 */         Object last = null;
/*     */ 
/* 114 */         while (this.iteratorIndex < this.currentIndex) {
/* 115 */           this.iteratorIndex += 1;
/* 116 */           last = source.next();
/*     */         }
/* 118 */         return last;
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   public Object[] toArray()
/*     */   {
/* 125 */     Iterators.size(iterator());
/* 126 */     return (Object[])this.cache.clone();
/*     */   }
/*     */ 
/*     */   public <T> T[] toArray(T[] a)
/*     */   {
/* 132 */     Iterators.size(iterator());
/* 133 */     return (Object[])Arrays.copyOf(this.cache, size(), a.getClass().getComponentType());
/*     */   }
/*     */ 
/*     */   public boolean add(T e)
/*     */   {
/* 138 */     boolean result = this.delegate.add(e);
/*     */ 
/* 140 */     growCache();
/* 141 */     return result;
/*     */   }
/*     */ 
/*     */   public boolean addAll(Collection<? extends T> c)
/*     */   {
/* 146 */     boolean result = this.delegate.addAll(c);
/*     */ 
/* 148 */     growCache();
/* 149 */     return result;
/*     */   }
/*     */ 
/*     */   public boolean containsAll(Collection<?> c)
/*     */   {
/* 154 */     return this.delegate.containsAll(c);
/*     */   }
/*     */ 
/*     */   public boolean remove(Object o)
/*     */   {
/* 159 */     this.cache = null;
/* 160 */     return this.delegate.remove(o);
/*     */   }
/*     */ 
/*     */   public boolean removeAll(Collection<?> c)
/*     */   {
/* 165 */     this.cache = null;
/* 166 */     return this.delegate.removeAll(c);
/*     */   }
/*     */ 
/*     */   public boolean retainAll(Collection<?> c)
/*     */   {
/* 171 */     this.cache = null;
/* 172 */     return this.delegate.retainAll(c);
/*     */   }
/*     */ 
/*     */   public void clear()
/*     */   {
/* 177 */     this.cache = null;
/* 178 */     this.delegate.clear();
/*     */   }
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 183 */     int result = 1;
/*     */ 
/* 186 */     for (Iterator i$ = iterator(); i$.hasNext(); ) { Object element = i$.next();
/* 187 */       result = 31 * result + (element == null ? 0 : element.hashCode()); }
/* 188 */     return result;
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 193 */     Iterators.size(iterator());
/* 194 */     StringBuilder result = new StringBuilder("[");
/*     */ 
/* 196 */     for (Iterator i$ = iterator(); i$.hasNext(); ) { Object element = i$.next();
/* 197 */       if (result.length() > 1)
/* 198 */         result.append(", ");
/* 199 */       result.append(element);
/*     */     }
/* 201 */     return "]";
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.wrappers.collection.CachedCollection
 * JD-Core Version:    0.6.2
 */