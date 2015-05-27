package com.comphenix.protocol.wrappers.nbt;

import java.io.DataOutput;

public abstract interface NbtWrapper<TType> extends NbtBase<TType>
{
  public abstract Object getHandle();

  public abstract void write(DataOutput paramDataOutput);
}

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.wrappers.nbt.NbtWrapper
 * JD-Core Version:    0.6.2
 */