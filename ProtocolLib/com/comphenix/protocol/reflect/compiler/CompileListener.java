package com.comphenix.protocol.reflect.compiler;

import com.comphenix.protocol.reflect.StructureModifier;

public abstract interface CompileListener<TKey>
{
  public abstract void onCompiled(StructureModifier<TKey> paramStructureModifier);
}

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.reflect.compiler.CompileListener
 * JD-Core Version:    0.6.2
 */