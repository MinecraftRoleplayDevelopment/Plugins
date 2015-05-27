package com.comphenix.protocol.wrappers.nbt;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nonnull;

public abstract interface NbtCompound extends NbtBase<Map<String, NbtBase<?>>>, Iterable<NbtBase<?>>
{
  @Deprecated
  public abstract Map<String, NbtBase<?>> getValue();

  public abstract boolean containsKey(String paramString);

  public abstract Set<String> getKeys();

  public abstract <T> NbtBase<T> getValue(String paramString);

  public abstract NbtBase<?> getValueOrDefault(String paramString, NbtType paramNbtType);

  public abstract <T> NbtCompound put(@Nonnull NbtBase<T> paramNbtBase);

  public abstract String getString(String paramString);

  public abstract String getStringOrDefault(String paramString);

  public abstract NbtCompound put(String paramString1, String paramString2);

  public abstract NbtCompound put(String paramString, NbtBase<?> paramNbtBase);

  public abstract byte getByte(String paramString);

  public abstract byte getByteOrDefault(String paramString);

  public abstract NbtCompound put(String paramString, byte paramByte);

  public abstract Short getShort(String paramString);

  public abstract short getShortOrDefault(String paramString);

  public abstract NbtCompound put(String paramString, short paramShort);

  public abstract int getInteger(String paramString);

  public abstract int getIntegerOrDefault(String paramString);

  public abstract NbtCompound put(String paramString, int paramInt);

  public abstract long getLong(String paramString);

  public abstract long getLongOrDefault(String paramString);

  public abstract NbtCompound put(String paramString, long paramLong);

  public abstract float getFloat(String paramString);

  public abstract float getFloatOrDefault(String paramString);

  public abstract NbtCompound put(String paramString, float paramFloat);

  public abstract double getDouble(String paramString);

  public abstract double getDoubleOrDefault(String paramString);

  public abstract NbtCompound put(String paramString, double paramDouble);

  public abstract byte[] getByteArray(String paramString);

  public abstract NbtCompound put(String paramString, byte[] paramArrayOfByte);

  public abstract int[] getIntegerArray(String paramString);

  public abstract NbtCompound put(String paramString, int[] paramArrayOfInt);

  public abstract NbtCompound putObject(String paramString, Object paramObject);

  public abstract Object getObject(String paramString);

  public abstract NbtCompound getCompound(String paramString);

  public abstract NbtCompound getCompoundOrDefault(String paramString);

  public abstract NbtCompound put(NbtCompound paramNbtCompound);

  public abstract <T> NbtList<T> getList(String paramString);

  public abstract <T> NbtList<T> getListOrDefault(String paramString);

  public abstract <T> NbtCompound put(NbtList<T> paramNbtList);

  public abstract <T> NbtCompound put(String paramString, Collection<? extends NbtBase<T>> paramCollection);

  public abstract <T> NbtBase<?> remove(String paramString);

  public abstract Iterator<NbtBase<?>> iterator();
}

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.wrappers.nbt.NbtCompound
 * JD-Core Version:    0.6.2
 */