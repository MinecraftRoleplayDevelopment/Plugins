package com.comphenix.net.sf.cglib.proxy;

import java.lang.reflect.Method;

public abstract interface CallbackFilter
{
  public abstract int accept(Method paramMethod);

  public abstract boolean equals(Object paramObject);
}

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.net.sf.cglib.proxy.CallbackFilter
 * JD-Core Version:    0.6.2
 */