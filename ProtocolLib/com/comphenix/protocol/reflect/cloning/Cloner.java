package com.comphenix.protocol.reflect.cloning;

public abstract interface Cloner
{
  public abstract boolean canClone(Object paramObject);

  public abstract Object clone(Object paramObject);
}

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.reflect.cloning.Cloner
 * JD-Core Version:    0.6.2
 */