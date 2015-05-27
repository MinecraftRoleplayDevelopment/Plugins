package com.comphenix.executors;

import com.google.common.util.concurrent.ListenableFuture;
import java.util.concurrent.RunnableScheduledFuture;

public abstract interface ListenableScheduledFuture<V> extends RunnableScheduledFuture<V>, ListenableFuture<V>
{
}

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.executors.ListenableScheduledFuture
 * JD-Core Version:    0.6.2
 */