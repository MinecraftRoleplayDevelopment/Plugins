package com.comphenix.net.sf.cglib.asm;

public abstract interface FieldVisitor
{
  public abstract AnnotationVisitor visitAnnotation(String paramString, boolean paramBoolean);

  public abstract void visitAttribute(Attribute paramAttribute);

  public abstract void visitEnd();
}

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.net.sf.cglib.asm.FieldVisitor
 * JD-Core Version:    0.6.2
 */