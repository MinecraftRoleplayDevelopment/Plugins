package com.comphenix.protocol.wrappers.nbt;

public abstract interface NbtVisitor
{
  public abstract boolean visit(NbtBase<?> paramNbtBase);

  public abstract boolean visitEnter(NbtList<?> paramNbtList);

  public abstract boolean visitEnter(NbtCompound paramNbtCompound);

  public abstract boolean visitLeave(NbtList<?> paramNbtList);

  public abstract boolean visitLeave(NbtCompound paramNbtCompound);
}

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.wrappers.nbt.NbtVisitor
 * JD-Core Version:    0.6.2
 */