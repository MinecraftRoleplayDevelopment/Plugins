package com.comphenix.protocol.async;

public abstract interface AsyncRunnable extends Runnable
{
  public abstract int getID();

  public abstract boolean stop()
    throws InterruptedException;

  public abstract boolean isRunning();

  public abstract boolean isFinished();
}

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.async.AsyncRunnable
 * JD-Core Version:    0.6.2
 */