package com.comphenix.net.sf.cglib.transform.impl;

public abstract interface FieldProvider
{
  public abstract String[] getFieldNames();

  public abstract Class[] getFieldTypes();

  public abstract void setField(int paramInt, Object paramObject);

  public abstract Object getField(int paramInt);

  public abstract void setField(String paramString, Object paramObject);

  public abstract Object getField(String paramString);
}

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.net.sf.cglib.transform.impl.FieldProvider
 * JD-Core Version:    0.6.2
 */