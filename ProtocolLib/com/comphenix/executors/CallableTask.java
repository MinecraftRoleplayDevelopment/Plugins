/*     */ package com.comphenix.executors;
/*     */ 
/*     */ import com.google.common.base.Preconditions;
/*     */ import com.google.common.util.concurrent.ListenableScheduledFuture;
/*     */ import java.util.concurrent.Callable;
/*     */ import java.util.concurrent.Delayed;
/*     */ import java.util.concurrent.ExecutionException;
/*     */ import java.util.concurrent.Executor;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import java.util.concurrent.TimeoutException;
/*     */ 
/*     */ class CallableTask<T> extends AbstractListeningService.RunnableAbstractFuture<T>
/*     */ {
/*     */   protected final Callable<T> compute;
/*     */ 
/*     */   public CallableTask(Callable<T> compute)
/*     */   {
/*  18 */     Preconditions.checkNotNull(compute, "compute cannot be NULL");
/*     */ 
/*  20 */     this.compute = compute;
/*     */   }
/*     */ 
/*     */   public ListenableScheduledFuture<T> getScheduledFuture(final long startTime, long nextDelay) {
/*  24 */     return new ListenableScheduledFuture()
/*     */     {
/*     */       public boolean cancel(boolean mayInterruptIfRunning) {
/*  27 */         return CallableTask.this.cancel(mayInterruptIfRunning);
/*     */       }
/*     */ 
/*     */       public T get() throws InterruptedException, ExecutionException
/*     */       {
/*  32 */         return CallableTask.this.get();
/*     */       }
/*     */ 
/*     */       public T get(long timeout, TimeUnit unit)
/*     */         throws InterruptedException, ExecutionException, TimeoutException
/*     */       {
/*  38 */         return CallableTask.this.get(timeout, unit);
/*     */       }
/*     */ 
/*     */       public boolean isCancelled()
/*     */       {
/*  43 */         return CallableTask.this.isCancelled();
/*     */       }
/*     */ 
/*     */       public boolean isDone()
/*     */       {
/*  48 */         return CallableTask.this.isDone();
/*     */       }
/*     */ 
/*     */       public void addListener(Runnable listener, Executor executor)
/*     */       {
/*  53 */         CallableTask.this.addListener(listener, executor);
/*     */       }
/*     */ 
/*     */       public int compareTo(Delayed o)
/*     */       {
/*  58 */         return Long.valueOf(getDelay(TimeUnit.NANOSECONDS)).compareTo(Long.valueOf(o.getDelay(TimeUnit.NANOSECONDS)));
/*     */       }
/*     */ 
/*     */       public long getDelay(TimeUnit unit)
/*     */       {
/*  64 */         long current = System.nanoTime();
/*     */ 
/*  67 */         if ((current < startTime) || (!isPeriodic())) {
/*  68 */           return unit.convert(startTime - current, TimeUnit.NANOSECONDS);
/*     */         }
/*  70 */         return unit.convert((current - startTime) % this.val$nextDelay, TimeUnit.NANOSECONDS);
/*     */       }
/*     */ 
/*     */       public boolean isPeriodic()
/*     */       {
/*  75 */         return this.val$nextDelay > 0L;
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   protected void compute()
/*     */   {
/*     */     try
/*     */     {
/*  91 */       if (!isCancelled())
/*  92 */         set(this.compute.call());
/*     */     }
/*     */     catch (Throwable e) {
/*  95 */       setException(e);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void run()
/*     */   {
/* 101 */     compute();
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.executors.CallableTask
 * JD-Core Version:    0.6.2
 */