package com.comphenix.protocol.reflect.accessors;

import java.lang.reflect.Constructor;

public abstract interface ConstructorAccessor
{
  public abstract Object invoke(Object[] paramArrayOfObject);

  public abstract Constructor<?> getConstructor();
}

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.reflect.accessors.ConstructorAccessor
 * JD-Core Version:    0.6.2
 */