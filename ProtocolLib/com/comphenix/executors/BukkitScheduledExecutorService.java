package com.comphenix.executors;

import com.google.common.util.concurrent.ListenableScheduledFuture;
import com.google.common.util.concurrent.ListeningScheduledExecutorService;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

public abstract interface BukkitScheduledExecutorService extends ListeningScheduledExecutorService
{
  public abstract ListenableScheduledFuture<?> schedule(Runnable paramRunnable, long paramLong, TimeUnit paramTimeUnit);

  public abstract <V> ListenableScheduledFuture<V> schedule(Callable<V> paramCallable, long paramLong, TimeUnit paramTimeUnit);

  public abstract ListenableScheduledFuture<?> scheduleAtFixedRate(Runnable paramRunnable, long paramLong1, long paramLong2, TimeUnit paramTimeUnit);

  @Deprecated
  public abstract ListenableScheduledFuture<?> scheduleWithFixedDelay(Runnable paramRunnable, long paramLong1, long paramLong2, TimeUnit paramTimeUnit);
}

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.executors.BukkitScheduledExecutorService
 * JD-Core Version:    0.6.2
 */