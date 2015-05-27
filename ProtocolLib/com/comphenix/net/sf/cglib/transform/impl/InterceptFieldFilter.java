package com.comphenix.net.sf.cglib.transform.impl;

import com.comphenix.net.sf.cglib.asm.Type;

public abstract interface InterceptFieldFilter
{
  public abstract boolean acceptRead(Type paramType, String paramString);

  public abstract boolean acceptWrite(Type paramType, String paramString);
}

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.net.sf.cglib.transform.impl.InterceptFieldFilter
 * JD-Core Version:    0.6.2
 */