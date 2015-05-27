/*     */ package com.comphenix.protocol.async;
/*     */ 
/*     */ import com.google.common.base.Preconditions;
/*     */ import java.io.Serializable;
/*     */ import java.util.Collection;
/*     */ import java.util.Iterator;
/*     */ import java.util.Queue;
/*     */ import javax.annotation.Nullable;
/*     */ 
/*     */ class Synchronization
/*     */ {
/*     */   public static <E> Queue<E> queue(Queue<E> queue, @Nullable Object mutex)
/*     */   {
/*  46 */     return (queue instanceof SynchronizedQueue) ? queue : new SynchronizedQueue(queue, mutex);
/*     */   }
/*     */ 
/*     */   private static class SynchronizedQueue<E> extends Synchronization.SynchronizedCollection<E>
/*     */     implements Queue<E>
/*     */   {
/*     */     private static final long serialVersionUID = 1961791630386791902L;
/*     */ 
/*     */     SynchronizedQueue(Queue<E> delegate, @Nullable Object mutex)
/*     */     {
/* 185 */       super(mutex, null);
/*     */     }
/*     */ 
/*     */     Queue<E> delegate()
/*     */     {
/* 190 */       return (Queue)super.delegate();
/*     */     }
/*     */ 
/*     */     public E element()
/*     */     {
/* 195 */       synchronized (this.mutex) {
/* 196 */         return delegate().element();
/*     */       }
/*     */     }
/*     */ 
/*     */     public boolean offer(E e)
/*     */     {
/* 202 */       synchronized (this.mutex) {
/* 203 */         return delegate().offer(e);
/*     */       }
/*     */     }
/*     */ 
/*     */     public E peek()
/*     */     {
/* 209 */       synchronized (this.mutex) {
/* 210 */         return delegate().peek();
/*     */       }
/*     */     }
/*     */ 
/*     */     public E poll()
/*     */     {
/* 216 */       synchronized (this.mutex) {
/* 217 */         return delegate().poll();
/*     */       }
/*     */     }
/*     */ 
/*     */     public E remove()
/*     */     {
/* 223 */       synchronized (this.mutex) {
/* 224 */         return delegate().remove();
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private static class SynchronizedCollection<E> extends Synchronization.SynchronizedObject
/*     */     implements Collection<E>
/*     */   {
/*     */     private static final long serialVersionUID = 5440572373531285692L;
/*     */ 
/*     */     private SynchronizedCollection(Collection<E> delegate, @Nullable Object mutex)
/*     */     {
/*  81 */       super(mutex);
/*     */     }
/*     */ 
/*     */     Collection<E> delegate()
/*     */     {
/*  87 */       return (Collection)super.delegate();
/*     */     }
/*     */ 
/*     */     public boolean add(E e)
/*     */     {
/*  92 */       synchronized (this.mutex) {
/*  93 */         return delegate().add(e);
/*     */       }
/*     */     }
/*     */ 
/*     */     public boolean addAll(Collection<? extends E> c)
/*     */     {
/*  99 */       synchronized (this.mutex) {
/* 100 */         return delegate().addAll(c);
/*     */       }
/*     */     }
/*     */ 
/*     */     public void clear()
/*     */     {
/* 106 */       synchronized (this.mutex) {
/* 107 */         delegate().clear();
/*     */       }
/*     */     }
/*     */ 
/*     */     public boolean contains(Object o)
/*     */     {
/* 113 */       synchronized (this.mutex) {
/* 114 */         return delegate().contains(o);
/*     */       }
/*     */     }
/*     */ 
/*     */     public boolean containsAll(Collection<?> c)
/*     */     {
/* 120 */       synchronized (this.mutex) {
/* 121 */         return delegate().containsAll(c);
/*     */       }
/*     */     }
/*     */ 
/*     */     public boolean isEmpty()
/*     */     {
/* 127 */       synchronized (this.mutex) {
/* 128 */         return delegate().isEmpty();
/*     */       }
/*     */     }
/*     */ 
/*     */     public Iterator<E> iterator()
/*     */     {
/* 134 */       return delegate().iterator();
/*     */     }
/*     */ 
/*     */     public boolean remove(Object o)
/*     */     {
/* 139 */       synchronized (this.mutex) {
/* 140 */         return delegate().remove(o);
/*     */       }
/*     */     }
/*     */ 
/*     */     public boolean removeAll(Collection<?> c)
/*     */     {
/* 146 */       synchronized (this.mutex) {
/* 147 */         return delegate().removeAll(c);
/*     */       }
/*     */     }
/*     */ 
/*     */     public boolean retainAll(Collection<?> c)
/*     */     {
/* 153 */       synchronized (this.mutex) {
/* 154 */         return delegate().retainAll(c);
/*     */       }
/*     */     }
/*     */ 
/*     */     public int size()
/*     */     {
/* 160 */       synchronized (this.mutex) {
/* 161 */         return delegate().size();
/*     */       }
/*     */     }
/*     */ 
/*     */     public Object[] toArray()
/*     */     {
/* 167 */       synchronized (this.mutex) {
/* 168 */         return delegate().toArray();
/*     */       }
/*     */     }
/*     */ 
/*     */     public <T> T[] toArray(T[] a)
/*     */     {
/* 174 */       synchronized (this.mutex) {
/* 175 */         return delegate().toArray(a);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private static class SynchronizedObject
/*     */     implements Serializable
/*     */   {
/*     */     private static final long serialVersionUID = -4408866092364554628L;
/*     */     final Object delegate;
/*     */     final Object mutex;
/*     */ 
/*     */     SynchronizedObject(Object delegate, @Nullable Object mutex)
/*     */     {
/*  58 */       this.delegate = Preconditions.checkNotNull(delegate);
/*  59 */       this.mutex = (mutex == null ? this : mutex);
/*     */     }
/*     */ 
/*     */     Object delegate() {
/*  63 */       return this.delegate;
/*     */     }
/*     */ 
/*     */     public String toString()
/*     */     {
/*  70 */       synchronized (this.mutex) {
/*  71 */         return this.delegate.toString();
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.async.Synchronization
 * JD-Core Version:    0.6.2
 */