/*    */ package com.comphenix.protocol.reflect.accessors;
/*    */ 
/*    */ import java.lang.reflect.Constructor;
/*    */ import java.lang.reflect.InvocationTargetException;
/*    */ 
/*    */ final class DefaultConstrutorAccessor
/*    */   implements ConstructorAccessor
/*    */ {
/*    */   private final Constructor<?> constructor;
/*    */ 
/*    */   public DefaultConstrutorAccessor(Constructor<?> method)
/*    */   {
/* 10 */     this.constructor = method;
/*    */   }
/*    */ 
/*    */   public Object invoke(Object[] args)
/*    */   {
/*    */     try {
/* 16 */       return this.constructor.newInstance(args);
/*    */     } catch (IllegalAccessException e) {
/* 18 */       throw new IllegalStateException("Cannot use reflection.", e);
/*    */     } catch (IllegalArgumentException e) {
/* 20 */       throw e;
/*    */     } catch (InvocationTargetException e) {
/* 22 */       throw new RuntimeException("An internal error occured.", e.getCause());
/*    */     } catch (InstantiationException e) {
/* 24 */       throw new RuntimeException("Cannot instantiate object.", e);
/*    */     }
/*    */   }
/*    */ 
/*    */   public Constructor<?> getConstructor()
/*    */   {
/* 30 */     return this.constructor;
/*    */   }
/*    */ 
/*    */   public int hashCode()
/*    */   {
/* 35 */     return this.constructor != null ? this.constructor.hashCode() : 0;
/*    */   }
/*    */ 
/*    */   public boolean equals(Object obj)
/*    */   {
/* 40 */     if (this == obj) {
/* 41 */       return true;
/*    */     }
/* 43 */     if ((obj instanceof DefaultConstrutorAccessor)) {
/* 44 */       DefaultConstrutorAccessor other = (DefaultConstrutorAccessor)obj;
/* 45 */       return other.constructor == this.constructor;
/*    */     }
/* 47 */     return true;
/*    */   }
/*    */ 
/*    */   public String toString()
/*    */   {
/* 52 */     return "DefaultConstrutorAccessor [constructor=" + this.constructor + "]";
/*    */   }
/*    */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.reflect.accessors.DefaultConstrutorAccessor
 * JD-Core Version:    0.6.2
 */