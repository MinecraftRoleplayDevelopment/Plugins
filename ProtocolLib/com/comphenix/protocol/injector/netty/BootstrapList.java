/*     */ package com.comphenix.protocol.injector.netty;
/*     */ 
/*     */ import com.google.common.collect.Lists;
/*     */ import io.netty.channel.Channel;
/*     */ import io.netty.channel.ChannelFuture;
/*     */ import io.netty.channel.ChannelHandler;
/*     */ import io.netty.channel.ChannelPipeline;
/*     */ import io.netty.channel.EventLoop;
/*     */ import java.util.Collection;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.ListIterator;
/*     */ import java.util.concurrent.Callable;
/*     */ 
/*     */ class BootstrapList
/*     */   implements List<Object>
/*     */ {
/*     */   private List<Object> delegate;
/*     */   private ChannelHandler handler;
/*     */ 
/*     */   public BootstrapList(List<Object> delegate, ChannelHandler handler)
/*     */   {
/*  26 */     this.delegate = delegate;
/*  27 */     this.handler = handler;
/*     */ 
/*  30 */     for (Iterator i$ = iterator(); i$.hasNext(); ) { Object item = i$.next();
/*  31 */       processElement(item);
/*     */     }
/*     */   }
/*     */ 
/*     */   public synchronized boolean add(Object element)
/*     */   {
/*  37 */     processElement(element);
/*  38 */     return this.delegate.add(element);
/*     */   }
/*     */ 
/*     */   public synchronized boolean addAll(Collection<? extends Object> collection)
/*     */   {
/*  43 */     List copy = Lists.newArrayList(collection);
/*     */ 
/*  46 */     for (Iterator i$ = copy.iterator(); i$.hasNext(); ) { Object element = i$.next();
/*  47 */       processElement(element);
/*     */     }
/*  49 */     return this.delegate.addAll(copy);
/*     */   }
/*     */ 
/*     */   public synchronized Object set(int index, Object element)
/*     */   {
/*  54 */     Object old = this.delegate.set(index, element);
/*     */ 
/*  57 */     if (old != element) {
/*  58 */       unprocessElement(old);
/*  59 */       processElement(element);
/*     */     }
/*  61 */     return old;
/*     */   }
/*     */ 
/*     */   protected void processElement(Object element)
/*     */   {
/*  69 */     if ((element instanceof ChannelFuture))
/*  70 */       processBootstrap((ChannelFuture)element);
/*     */   }
/*     */ 
/*     */   protected void unprocessElement(Object element)
/*     */   {
/*  79 */     if ((element instanceof ChannelFuture))
/*  80 */       unprocessBootstrap((ChannelFuture)element);
/*     */   }
/*     */ 
/*     */   protected void processBootstrap(ChannelFuture future)
/*     */   {
/*  90 */     future.channel().pipeline().addFirst(new ChannelHandler[] { this.handler });
/*     */   }
/*     */ 
/*     */   protected void unprocessBootstrap(ChannelFuture future)
/*     */   {
/*  98 */     final Channel channel = future.channel();
/*     */ 
/* 101 */     channel.eventLoop().submit(new Callable()
/*     */     {
/*     */       public Object call() throws Exception {
/* 104 */         channel.pipeline().remove(BootstrapList.this.handler);
/* 105 */         return null;
/*     */       }
/*     */     });
/*     */   }
/*     */ 
/*     */   public synchronized void close()
/*     */   {
/* 114 */     for (Iterator i$ = iterator(); i$.hasNext(); ) { Object element = i$.next();
/* 115 */       unprocessElement(element);
/*     */     }
/*     */   }
/*     */ 
/*     */   public synchronized int size()
/*     */   {
/* 121 */     return this.delegate.size();
/*     */   }
/*     */ 
/*     */   public synchronized boolean isEmpty()
/*     */   {
/* 126 */     return this.delegate.isEmpty();
/*     */   }
/*     */ 
/*     */   public boolean contains(Object o)
/*     */   {
/* 131 */     return this.delegate.contains(o);
/*     */   }
/*     */ 
/*     */   public synchronized Iterator<Object> iterator()
/*     */   {
/* 136 */     return this.delegate.iterator();
/*     */   }
/*     */ 
/*     */   public synchronized Object[] toArray()
/*     */   {
/* 141 */     return this.delegate.toArray();
/*     */   }
/*     */ 
/*     */   public synchronized <T> T[] toArray(T[] a)
/*     */   {
/* 146 */     return this.delegate.toArray(a);
/*     */   }
/*     */ 
/*     */   public synchronized boolean remove(Object o)
/*     */   {
/* 151 */     return this.delegate.remove(o);
/*     */   }
/*     */ 
/*     */   public synchronized boolean containsAll(Collection<?> c)
/*     */   {
/* 156 */     return this.delegate.containsAll(c);
/*     */   }
/*     */ 
/*     */   public synchronized boolean addAll(int index, Collection<? extends Object> c)
/*     */   {
/* 161 */     return this.delegate.addAll(index, c);
/*     */   }
/*     */ 
/*     */   public synchronized boolean removeAll(Collection<?> c)
/*     */   {
/* 166 */     return this.delegate.removeAll(c);
/*     */   }
/*     */ 
/*     */   public synchronized boolean retainAll(Collection<?> c)
/*     */   {
/* 171 */     return this.delegate.retainAll(c);
/*     */   }
/*     */ 
/*     */   public synchronized void clear()
/*     */   {
/* 176 */     this.delegate.clear();
/*     */   }
/*     */ 
/*     */   public synchronized Object get(int index)
/*     */   {
/* 181 */     return this.delegate.get(index);
/*     */   }
/*     */ 
/*     */   public synchronized void add(int index, Object element)
/*     */   {
/* 186 */     this.delegate.add(index, element);
/*     */   }
/*     */ 
/*     */   public synchronized Object remove(int index)
/*     */   {
/* 191 */     return this.delegate.remove(index);
/*     */   }
/*     */ 
/*     */   public synchronized int indexOf(Object o)
/*     */   {
/* 196 */     return this.delegate.indexOf(o);
/*     */   }
/*     */ 
/*     */   public synchronized int lastIndexOf(Object o)
/*     */   {
/* 201 */     return this.delegate.lastIndexOf(o);
/*     */   }
/*     */ 
/*     */   public synchronized ListIterator<Object> listIterator()
/*     */   {
/* 206 */     return this.delegate.listIterator();
/*     */   }
/*     */ 
/*     */   public synchronized ListIterator<Object> listIterator(int index)
/*     */   {
/* 211 */     return this.delegate.listIterator(index);
/*     */   }
/*     */ 
/*     */   public synchronized List<Object> subList(int fromIndex, int toIndex)
/*     */   {
/* 216 */     return this.delegate.subList(fromIndex, toIndex);
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.injector.netty.BootstrapList
 * JD-Core Version:    0.6.2
 */