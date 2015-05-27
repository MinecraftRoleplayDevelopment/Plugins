package com.comphenix.net.sf.cglib.core;

import com.comphenix.net.sf.cglib.asm.Label;

public abstract interface ObjectSwitchCallback
{
  public abstract void processCase(Object paramObject, Label paramLabel)
    throws Exception;

  public abstract void processDefault()
    throws Exception;
}

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.net.sf.cglib.core.ObjectSwitchCallback
 * JD-Core Version:    0.6.2
 */