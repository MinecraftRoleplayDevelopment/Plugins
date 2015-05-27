package com.comphenix.net.sf.cglib.proxy;

import com.comphenix.net.sf.cglib.core.ClassEmitter;
import com.comphenix.net.sf.cglib.core.CodeEmitter;
import com.comphenix.net.sf.cglib.core.MethodInfo;
import com.comphenix.net.sf.cglib.core.Signature;
import java.util.List;

abstract interface CallbackGenerator
{
  public abstract void generate(ClassEmitter paramClassEmitter, Context paramContext, List paramList)
    throws Exception;

  public abstract void generateStatic(CodeEmitter paramCodeEmitter, Context paramContext, List paramList)
    throws Exception;

  public static abstract interface Context
  {
    public abstract ClassLoader getClassLoader();

    public abstract CodeEmitter beginMethod(ClassEmitter paramClassEmitter, MethodInfo paramMethodInfo);

    public abstract int getOriginalModifiers(MethodInfo paramMethodInfo);

    public abstract int getIndex(MethodInfo paramMethodInfo);

    public abstract void emitCallback(CodeEmitter paramCodeEmitter, int paramInt);

    public abstract Signature getImplSignature(MethodInfo paramMethodInfo);

    public abstract void emitInvoke(CodeEmitter paramCodeEmitter, MethodInfo paramMethodInfo);
  }
}

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.net.sf.cglib.proxy.CallbackGenerator
 * JD-Core Version:    0.6.2
 */