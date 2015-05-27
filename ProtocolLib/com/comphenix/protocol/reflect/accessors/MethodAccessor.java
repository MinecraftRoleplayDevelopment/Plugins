package com.comphenix.protocol.reflect.accessors;

import java.lang.reflect.Method;

public abstract interface MethodAccessor
{
  public abstract Object invoke(Object paramObject, Object[] paramArrayOfObject);

  public abstract Method getMethod();
}

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.reflect.accessors.MethodAccessor
 * JD-Core Version:    0.6.2
 */