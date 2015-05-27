package com.comphenix.protocol.reflect.instances;

import javax.annotation.Nullable;

public abstract interface InstanceProvider
{
  public abstract Object create(@Nullable Class<?> paramClass);
}

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.reflect.instances.InstanceProvider
 * JD-Core Version:    0.6.2
 */