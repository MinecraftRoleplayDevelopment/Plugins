package com.comphenix.protocol.wrappers.nbt;

public abstract interface NbtBase<TType>
{
  public abstract boolean accept(NbtVisitor paramNbtVisitor);

  public abstract NbtType getType();

  public abstract String getName();

  public abstract void setName(String paramString);

  public abstract TType getValue();

  public abstract void setValue(TType paramTType);

  public abstract NbtBase<TType> deepClone();
}

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.wrappers.nbt.NbtBase
 * JD-Core Version:    0.6.2
 */