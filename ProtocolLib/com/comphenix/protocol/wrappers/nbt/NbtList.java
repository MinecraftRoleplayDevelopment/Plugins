package com.comphenix.protocol.wrappers.nbt;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public abstract interface NbtList<TType> extends NbtBase<List<NbtBase<TType>>>, Iterable<TType>
{
  public static final String EMPTY_NAME = "";

  public abstract NbtType getElementType();

  public abstract void setElementType(NbtType paramNbtType);

  public abstract void addClosest(Object paramObject);

  public abstract void add(NbtBase<TType> paramNbtBase);

  public abstract void add(String paramString);

  public abstract void add(byte paramByte);

  public abstract void add(short paramShort);

  public abstract void add(int paramInt);

  public abstract void add(long paramLong);

  public abstract void add(double paramDouble);

  public abstract void add(byte[] paramArrayOfByte);

  public abstract void add(int[] paramArrayOfInt);

  public abstract void remove(Object paramObject);

  public abstract TType getValue(int paramInt);

  public abstract int size();

  public abstract Collection<NbtBase<TType>> asCollection();

  public abstract Iterator<TType> iterator();
}

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.wrappers.nbt.NbtList
 * JD-Core Version:    0.6.2
 */