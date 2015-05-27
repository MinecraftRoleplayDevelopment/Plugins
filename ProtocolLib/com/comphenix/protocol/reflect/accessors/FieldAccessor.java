package com.comphenix.protocol.reflect.accessors;

import java.lang.reflect.Field;

public abstract interface FieldAccessor
{
  public abstract Object get(Object paramObject);

  public abstract void set(Object paramObject1, Object paramObject2);

  public abstract Field getField();
}

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.reflect.accessors.FieldAccessor
 * JD-Core Version:    0.6.2
 */