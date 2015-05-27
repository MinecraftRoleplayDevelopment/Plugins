package com.comphenix.net.sf.cglib.core;

public abstract interface GeneratorStrategy
{
  public abstract byte[] generate(ClassGenerator paramClassGenerator)
    throws Exception;

  public abstract boolean equals(Object paramObject);
}

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.net.sf.cglib.core.GeneratorStrategy
 * JD-Core Version:    0.6.2
 */