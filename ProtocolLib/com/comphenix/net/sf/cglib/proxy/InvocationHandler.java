package com.comphenix.net.sf.cglib.proxy;

import java.lang.reflect.Method;

public abstract interface InvocationHandler extends Callback
{
  public abstract Object invoke(Object paramObject, Method paramMethod, Object[] paramArrayOfObject)
    throws Throwable;
}

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.net.sf.cglib.proxy.InvocationHandler
 * JD-Core Version:    0.6.2
 */