package com.comphenix.protocol.reflect;

public abstract interface EquivalentConverter<TType>
{
  public abstract TType getSpecific(Object paramObject);

  public abstract Object getGeneric(Class<?> paramClass, TType paramTType);

  public abstract Class<TType> getSpecificType();
}

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.reflect.EquivalentConverter
 * JD-Core Version:    0.6.2
 */