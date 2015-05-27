/*     */ package com.comphenix.protocol.injector.netty;
/*     */ 
/*     */ import io.netty.channel.Channel;
/*     */ import io.netty.channel.ChannelFuture;
/*     */ import io.netty.channel.ChannelPromise;
/*     */ import io.netty.channel.EventLoop;
/*     */ import io.netty.channel.EventLoopGroup;
/*     */ import io.netty.util.concurrent.EventExecutor;
/*     */ import io.netty.util.concurrent.ProgressivePromise;
/*     */ import io.netty.util.concurrent.Promise;
/*     */ import io.netty.util.concurrent.ScheduledFuture;
/*     */ import java.util.Collection;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.concurrent.Callable;
/*     */ import java.util.concurrent.ExecutionException;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import java.util.concurrent.TimeoutException;
/*     */ 
/*     */ abstract class EventLoopProxy
/*     */   implements EventLoop
/*     */ {
/*  27 */   private static final Runnable EMPTY_RUNNABLE = new Runnable() {
/*  27 */     public void run() {  }  } ;
/*     */ 
/*  33 */   private static final Callable<?> EMPTY_CALLABLE = new Callable()
/*     */   {
/*     */     public Object call() throws Exception {
/*  36 */       return null;
/*     */     }
/*  33 */   };
/*     */ 
/*     */   protected abstract EventLoop getDelegate();
/*     */ 
/*     */   public static <T> Callable<T> getEmptyCallable()
/*     */   {
/*  52 */     return EMPTY_CALLABLE;
/*     */   }
/*     */ 
/*     */   public static Runnable getEmptyRunnable()
/*     */   {
/*  60 */     return EMPTY_RUNNABLE;
/*     */   }
/*     */ 
/*     */   protected abstract Runnable schedulingRunnable(Runnable paramRunnable);
/*     */ 
/*     */   protected abstract <T> Callable<T> schedulingCallable(Callable<T> paramCallable);
/*     */ 
/*     */   public void execute(Runnable command)
/*     */   {
/*  79 */     getDelegate().execute(schedulingRunnable(command));
/*     */   }
/*     */ 
/*     */   public <T> io.netty.util.concurrent.Future<T> submit(Callable<T> action)
/*     */   {
/*  84 */     return getDelegate().submit(schedulingCallable(action));
/*     */   }
/*     */ 
/*     */   public <T> io.netty.util.concurrent.Future<T> submit(Runnable action, T arg1)
/*     */   {
/*  89 */     return getDelegate().submit(schedulingRunnable(action), arg1);
/*     */   }
/*     */ 
/*     */   public io.netty.util.concurrent.Future<?> submit(Runnable action)
/*     */   {
/*  94 */     return getDelegate().submit(schedulingRunnable(action));
/*     */   }
/*     */ 
/*     */   public <V> ScheduledFuture<V> schedule(Callable<V> action, long arg1, TimeUnit arg2)
/*     */   {
/*  99 */     return getDelegate().schedule(schedulingCallable(action), arg1, arg2);
/*     */   }
/*     */ 
/*     */   public ScheduledFuture<?> schedule(Runnable action, long arg1, TimeUnit arg2)
/*     */   {
/* 104 */     return getDelegate().schedule(schedulingRunnable(action), arg1, arg2);
/*     */   }
/*     */ 
/*     */   public ScheduledFuture<?> scheduleAtFixedRate(Runnable action, long arg1, long arg2, TimeUnit arg3)
/*     */   {
/* 109 */     return getDelegate().scheduleAtFixedRate(schedulingRunnable(action), arg1, arg2, arg3);
/*     */   }
/*     */ 
/*     */   public ScheduledFuture<?> scheduleWithFixedDelay(Runnable action, long arg1, long arg2, TimeUnit arg3)
/*     */   {
/* 114 */     return getDelegate().scheduleWithFixedDelay(schedulingRunnable(action), arg1, arg2, arg3);
/*     */   }
/*     */ 
/*     */   public boolean awaitTermination(long timeout, TimeUnit unit)
/*     */     throws InterruptedException
/*     */   {
/* 120 */     return getDelegate().awaitTermination(timeout, unit);
/*     */   }
/*     */ 
/*     */   public boolean inEventLoop()
/*     */   {
/* 125 */     return getDelegate().inEventLoop();
/*     */   }
/*     */ 
/*     */   public boolean inEventLoop(Thread arg0)
/*     */   {
/* 130 */     return getDelegate().inEventLoop(arg0);
/*     */   }
/*     */ 
/*     */   public boolean isShutdown()
/*     */   {
/* 135 */     return getDelegate().isShutdown();
/*     */   }
/*     */ 
/*     */   public boolean isTerminated()
/*     */   {
/* 140 */     return getDelegate().isTerminated();
/*     */   }
/*     */ 
/*     */   public <T> List<java.util.concurrent.Future<T>> invokeAll(Collection<? extends Callable<T>> tasks)
/*     */     throws InterruptedException
/*     */   {
/* 146 */     return getDelegate().invokeAll(tasks);
/*     */   }
/*     */ 
/*     */   public <T> List<java.util.concurrent.Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit)
/*     */     throws InterruptedException
/*     */   {
/* 152 */     return getDelegate().invokeAll(tasks, timeout, unit);
/*     */   }
/*     */ 
/*     */   public <T> T invokeAny(Collection<? extends Callable<T>> tasks)
/*     */     throws InterruptedException, ExecutionException
/*     */   {
/* 158 */     return getDelegate().invokeAny(tasks);
/*     */   }
/*     */ 
/*     */   public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit)
/*     */     throws InterruptedException, ExecutionException, TimeoutException
/*     */   {
/* 164 */     return getDelegate().invokeAny(tasks, timeout, unit);
/*     */   }
/*     */ 
/*     */   public boolean isShuttingDown()
/*     */   {
/* 169 */     return getDelegate().isShuttingDown();
/*     */   }
/*     */ 
/*     */   public Iterator<EventExecutor> iterator()
/*     */   {
/* 174 */     return getDelegate().iterator();
/*     */   }
/*     */ 
/*     */   public <V> io.netty.util.concurrent.Future<V> newFailedFuture(Throwable arg0)
/*     */   {
/* 179 */     return getDelegate().newFailedFuture(arg0);
/*     */   }
/*     */ 
/*     */   public EventLoop next()
/*     */   {
/* 184 */     return getDelegate().next();
/*     */   }
/*     */ 
/*     */   public <V> ProgressivePromise<V> newProgressivePromise()
/*     */   {
/* 189 */     return getDelegate().newProgressivePromise();
/*     */   }
/*     */ 
/*     */   public <V> Promise<V> newPromise()
/*     */   {
/* 194 */     return getDelegate().newPromise();
/*     */   }
/*     */ 
/*     */   public <V> io.netty.util.concurrent.Future<V> newSucceededFuture(V arg0)
/*     */   {
/* 199 */     return getDelegate().newSucceededFuture(arg0);
/*     */   }
/*     */ 
/*     */   public EventLoopGroup parent()
/*     */   {
/* 204 */     return getDelegate().parent();
/*     */   }
/*     */ 
/*     */   public ChannelFuture register(Channel arg0, ChannelPromise arg1)
/*     */   {
/* 209 */     return getDelegate().register(arg0, arg1);
/*     */   }
/*     */ 
/*     */   public ChannelFuture register(Channel arg0)
/*     */   {
/* 214 */     return getDelegate().register(arg0);
/*     */   }
/*     */ 
/*     */   public io.netty.util.concurrent.Future<?> shutdownGracefully()
/*     */   {
/* 219 */     return getDelegate().shutdownGracefully();
/*     */   }
/*     */ 
/*     */   public io.netty.util.concurrent.Future<?> shutdownGracefully(long arg0, long arg1, TimeUnit arg2)
/*     */   {
/* 224 */     return getDelegate().shutdownGracefully(arg0, arg1, arg2);
/*     */   }
/*     */ 
/*     */   public io.netty.util.concurrent.Future<?> terminationFuture()
/*     */   {
/* 229 */     return getDelegate().terminationFuture();
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public void shutdown()
/*     */   {
/* 235 */     getDelegate().shutdown();
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public List<Runnable> shutdownNow()
/*     */   {
/* 241 */     return getDelegate().shutdownNow();
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.injector.netty.EventLoopProxy
 * JD-Core Version:    0.6.2
 */