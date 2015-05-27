package com.comphenix.net.sf.cglib.core;

import com.comphenix.net.sf.cglib.asm.ClassVisitor;

public abstract interface ClassGenerator
{
  public abstract void generateClass(ClassVisitor paramClassVisitor)
    throws Exception;
}

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.net.sf.cglib.core.ClassGenerator
 * JD-Core Version:    0.6.2
 */