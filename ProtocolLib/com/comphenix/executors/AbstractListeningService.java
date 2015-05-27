/*     */ package com.comphenix.executors;
/*     */ 
/*     */ import com.google.common.util.concurrent.AbstractFuture;
/*     */ import com.google.common.util.concurrent.ListenableFuture;
/*     */ import com.google.common.util.concurrent.ListeningExecutorService;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.concurrent.Callable;
/*     */ import java.util.concurrent.CancellationException;
/*     */ import java.util.concurrent.ExecutionException;
/*     */ import java.util.concurrent.ExecutorCompletionService;
/*     */ import java.util.concurrent.Future;
/*     */ import java.util.concurrent.RunnableFuture;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import java.util.concurrent.TimeoutException;
/*     */ 
/*     */ abstract class AbstractListeningService
/*     */   implements ListeningExecutorService
/*     */ {
/*     */   protected abstract <T> RunnableAbstractFuture<T> newTaskFor(Runnable paramRunnable, T paramT);
/*     */ 
/*     */   protected abstract <T> RunnableAbstractFuture<T> newTaskFor(Callable<T> paramCallable);
/*     */ 
/*     */   public ListenableFuture<?> submit(Runnable task)
/*     */   {
/*  83 */     if (task == null) {
/*  84 */       throw new NullPointerException();
/*     */     }
/*  86 */     RunnableAbstractFuture ftask = newTaskFor(task, null);
/*  87 */     execute(ftask);
/*  88 */     return ftask;
/*     */   }
/*     */ 
/*     */   public <T> ListenableFuture<T> submit(Runnable task, T result)
/*     */   {
/*  93 */     if (task == null) {
/*  94 */       throw new NullPointerException();
/*     */     }
/*  96 */     RunnableAbstractFuture ftask = newTaskFor(task, result);
/*  97 */     execute(ftask);
/*  98 */     return ftask;
/*     */   }
/*     */ 
/*     */   public <T> ListenableFuture<T> submit(Callable<T> task)
/*     */   {
/* 103 */     if (task == null) {
/* 104 */       throw new NullPointerException();
/*     */     }
/* 106 */     RunnableAbstractFuture ftask = newTaskFor(task);
/* 107 */     execute(ftask);
/* 108 */     return ftask;
/*     */   }
/*     */ 
/*     */   private <T> T doInvokeAny(Collection<? extends Callable<T>> tasks, boolean timed, long nanos)
/*     */     throws InterruptedException, ExecutionException, TimeoutException
/*     */   {
/* 116 */     if (tasks == null) {
/* 117 */       throw new NullPointerException();
/*     */     }
/* 119 */     int ntasks = tasks.size();
/* 120 */     if (ntasks == 0) {
/* 121 */       throw new IllegalArgumentException();
/*     */     }
/* 123 */     List futures = new ArrayList(ntasks);
/* 124 */     ExecutorCompletionService ecs = new ExecutorCompletionService(this);
/*     */     try
/*     */     {
/* 135 */       ExecutionException ee = null;
/* 136 */       long lastTime = timed ? System.nanoTime() : 0L;
/* 137 */       Iterator it = tasks.iterator();
/*     */ 
/* 140 */       futures.add(ecs.submit((Callable)it.next()));
/* 141 */       ntasks--;
/* 142 */       int active = 1;
/*     */       while (true)
/*     */       {
/* 145 */         Future f = ecs.poll();
/* 146 */         if (f == null)
/* 147 */           if (ntasks > 0) {
/* 148 */             ntasks--;
/* 149 */             futures.add(ecs.submit((Callable)it.next()));
/* 150 */             active++; } else {
/* 151 */             if (active == 0)
/*     */               break;
/* 153 */             if (timed) {
/* 154 */               f = ecs.poll(nanos, TimeUnit.NANOSECONDS);
/* 155 */               if (f == null) {
/* 156 */                 throw new TimeoutException();
/*     */               }
/* 158 */               long now = System.nanoTime();
/* 159 */               nanos -= now - lastTime;
/* 160 */               lastTime = now;
/*     */             } else {
/* 162 */               f = ecs.take();
/*     */             }
/*     */           }
/* 165 */         if (f != null) {
/* 166 */           active--;
/*     */           try
/*     */           {
/*     */             Iterator i$;
/*     */             Future f;
/* 168 */             return f.get();
/*     */           } catch (ExecutionException eex) {
/* 170 */             ee = eex;
/*     */           } catch (RuntimeException rex) {
/* 172 */             ee = new ExecutionException(rex);
/*     */           }
/*     */         }
/*     */       }
/*     */ 
/* 177 */       if (ee == null) {
/* 178 */         ee = new ExecutionException(null);
/*     */       }
/* 180 */       throw ee;
/*     */     }
/*     */     finally {
/* 183 */       for (Future f : futures)
/* 184 */         f.cancel(true);
/*     */     }
/*     */   }
/*     */ 
/*     */   public <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException
/*     */   {
/*     */     try
/*     */     {
/* 192 */       return doInvokeAny(tasks, false, 0L);
/*     */     } catch (TimeoutException cannotHappen) {
/*     */     }
/* 195 */     return null;
/*     */   }
/*     */ 
/*     */   public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit)
/*     */     throws InterruptedException, ExecutionException, TimeoutException
/*     */   {
/* 202 */     return doInvokeAny(tasks, true, unit.toNanos(timeout));
/*     */   }
/*     */ 
/*     */   public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks)
/*     */     throws InterruptedException
/*     */   {
/* 208 */     if (tasks == null) {
/* 209 */       throw new NullPointerException();
/*     */     }
/* 211 */     List futures = new ArrayList(tasks.size());
/* 212 */     boolean done = false;
/*     */     try {
/* 214 */       for (Callable t : tasks) {
/* 215 */         RunnableAbstractFuture f = newTaskFor(t);
/* 216 */         futures.add(f);
/* 217 */         execute(f);
/*     */       }
/* 219 */       for (Future f : futures)
/* 220 */         if (!f.isDone())
/*     */           try {
/* 222 */             f.get();
/*     */           }
/*     */           catch (CancellationException ignore) {
/*     */           }
/*     */           catch (ExecutionException ignore) {
/*     */           }
/* 228 */       done = true;
/*     */       Iterator i$;
/*     */       Future f;
/* 229 */       return futures;
/*     */     } finally {
/* 231 */       if (!done)
/* 232 */         for (Future f : futures)
/* 233 */           f.cancel(true);
/*     */     }
/*     */   }
/*     */ 
/*     */   public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit)
/*     */     throws InterruptedException
/*     */   {
/* 241 */     if ((tasks == null) || (unit == null)) {
/* 242 */       throw new NullPointerException();
/*     */     }
/* 244 */     long nanos = unit.toNanos(timeout);
/* 245 */     List futures = new ArrayList(tasks.size());
/* 246 */     boolean done = false;
/*     */     try {
/* 248 */       for (Callable t : tasks) {
/* 249 */         futures.add(newTaskFor(t));
/*     */       }
/* 251 */       long lastTime = System.nanoTime();
/*     */ 
/* 255 */       Iterator it = futures.iterator();
/*     */       List localList1;
/* 256 */       while (it.hasNext()) {
/* 257 */         execute((Runnable)it.next());
/* 258 */         long now = System.nanoTime();
/* 259 */         nanos -= now - lastTime;
/* 260 */         lastTime = now;
/* 261 */         if (nanos <= 0L)
/*     */         {
/*     */           Iterator i$;
/*     */           Future f;
/* 262 */           return futures;
/*     */         }
/*     */       }
/*     */ 
/* 266 */       for (Future f : futures) {
/* 267 */         if (!f.isDone())
/*     */         {
/*     */           Iterator i$;
/* 268 */           if (nanos <= 0L)
/*     */           {
/*     */             Future f;
/* 269 */             return futures;
/*     */           }
/*     */           try {
/* 272 */             f.get(nanos, TimeUnit.NANOSECONDS);
/*     */           }
/*     */           catch (CancellationException ignore)
/*     */           {
/*     */           }
/*     */           catch (ExecutionException ignore)
/*     */           {
/*     */           }
/*     */           catch (TimeoutException toe)
/*     */           {
/*     */             Iterator i$;
/*     */             Future f;
/* 276 */             return futures;
/*     */           }
/* 278 */           long now = System.nanoTime();
/* 279 */           nanos -= now - lastTime;
/* 280 */           lastTime = now;
/*     */         }
/*     */       }
/* 283 */       done = true;
/*     */       Iterator i$;
/*     */       Future f;
/* 284 */       return futures;
/*     */     } finally {
/* 286 */       if (!done)
/* 287 */         for (Future f : futures)
/* 288 */           f.cancel(true);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static abstract class RunnableAbstractFuture<T> extends AbstractFuture<T>
/*     */     implements RunnableFuture<T>
/*     */   {
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.executors.AbstractListeningService
 * JD-Core Version:    0.6.2
 */