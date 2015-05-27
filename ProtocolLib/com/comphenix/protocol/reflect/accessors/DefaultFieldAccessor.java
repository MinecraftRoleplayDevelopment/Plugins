/*    */ package com.comphenix.protocol.reflect.accessors;
/*    */ 
/*    */ import java.lang.reflect.Field;
/*    */ 
/*    */ final class DefaultFieldAccessor
/*    */   implements FieldAccessor
/*    */ {
/*    */   private final Field field;
/*    */ 
/*    */   public DefaultFieldAccessor(Field field)
/*    */   {
/*  9 */     this.field = field;
/*    */   }
/*    */ 
/*    */   public Object get(Object instance)
/*    */   {
/*    */     try {
/* 15 */       return this.field.get(instance);
/*    */     } catch (IllegalArgumentException e) {
/* 17 */       throw new RuntimeException("Cannot read  " + this.field, e);
/*    */     } catch (IllegalAccessException e) {
/* 19 */       throw new IllegalStateException("Cannot use reflection.", e);
/*    */     }
/*    */   }
/*    */ 
/*    */   public void set(Object instance, Object value)
/*    */   {
/*    */     try {
/* 26 */       this.field.set(instance, value);
/*    */     } catch (IllegalArgumentException e) {
/* 28 */       throw new RuntimeException("Cannot set field " + this.field + " to value " + value, e);
/*    */     } catch (IllegalAccessException e) {
/* 30 */       throw new IllegalStateException("Cannot use reflection.", e);
/*    */     }
/*    */   }
/*    */ 
/*    */   public Field getField()
/*    */   {
/* 36 */     return this.field;
/*    */   }
/*    */ 
/*    */   public int hashCode()
/*    */   {
/* 41 */     return this.field != null ? this.field.hashCode() : 0;
/*    */   }
/*    */ 
/*    */   public boolean equals(Object obj)
/*    */   {
/* 46 */     if (this == obj) {
/* 47 */       return true;
/*    */     }
/* 49 */     if ((obj instanceof DefaultFieldAccessor)) {
/* 50 */       DefaultFieldAccessor other = (DefaultFieldAccessor)obj;
/* 51 */       return other.field == this.field;
/*    */     }
/* 53 */     return true;
/*    */   }
/*    */ 
/*    */   public String toString()
/*    */   {
/* 58 */     return "DefaultFieldAccessor [field=" + this.field + "]";
/*    */   }
/*    */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.reflect.accessors.DefaultFieldAccessor
 * JD-Core Version:    0.6.2
 */