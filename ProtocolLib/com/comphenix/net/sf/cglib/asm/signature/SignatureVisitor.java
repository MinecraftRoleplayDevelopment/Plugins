package com.comphenix.net.sf.cglib.asm.signature;

public abstract interface SignatureVisitor
{
  public static final char EXTENDS = '+';
  public static final char SUPER = '-';
  public static final char INSTANCEOF = '=';

  public abstract void visitFormalTypeParameter(String paramString);

  public abstract SignatureVisitor visitClassBound();

  public abstract SignatureVisitor visitInterfaceBound();

  public abstract SignatureVisitor visitSuperclass();

  public abstract SignatureVisitor visitInterface();

  public abstract SignatureVisitor visitParameterType();

  public abstract SignatureVisitor visitReturnType();

  public abstract SignatureVisitor visitExceptionType();

  public abstract void visitBaseType(char paramChar);

  public abstract void visitTypeVariable(String paramString);

  public abstract SignatureVisitor visitArrayType();

  public abstract void visitClassType(String paramString);

  public abstract void visitInnerClassType(String paramString);

  public abstract void visitTypeArgument();

  public abstract SignatureVisitor visitTypeArgument(char paramChar);

  public abstract void visitEnd();
}

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.net.sf.cglib.asm.signature.SignatureVisitor
 * JD-Core Version:    0.6.2
 */