package com.comphenix.net.sf.cglib.proxy;

import java.lang.reflect.Method;

public abstract interface MethodInterceptor extends Callback
{
  public abstract Object intercept(Object paramObject, Method paramMethod, Object[] paramArrayOfObject, MethodProxy paramMethodProxy)
    throws Throwable;
}

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.net.sf.cglib.proxy.MethodInterceptor
 * JD-Core Version:    0.6.2
 */