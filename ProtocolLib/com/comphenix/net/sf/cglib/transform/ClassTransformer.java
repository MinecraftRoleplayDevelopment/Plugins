package com.comphenix.net.sf.cglib.transform;

import com.comphenix.net.sf.cglib.asm.ClassVisitor;

public abstract interface ClassTransformer extends ClassVisitor
{
  public abstract void setTarget(ClassVisitor paramClassVisitor);
}

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.net.sf.cglib.transform.ClassTransformer
 * JD-Core Version:    0.6.2
 */