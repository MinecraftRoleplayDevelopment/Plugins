/*   */ package com.comphenix.protocol.reflect.accessors;
/*   */ 
/*   */ public abstract class ReadOnlyFieldAccessor
/*   */   implements FieldAccessor
/*   */ {
/*   */   public final void set(Object instance, Object value)
/*   */   {
/* 6 */     throw new UnsupportedOperationException("Cannot update the content of a read-only field accessor.");
/*   */   }
/*   */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.reflect.accessors.ReadOnlyFieldAccessor
 * JD-Core Version:    0.6.2
 */