/*    */ package com.comphenix.protocol.reflect.accessors;
/*    */ 
/*    */ import java.lang.reflect.InvocationTargetException;
/*    */ import java.lang.reflect.Method;
/*    */ 
/*    */ final class DefaultMethodAccessor
/*    */   implements MethodAccessor
/*    */ {
/*    */   private final Method method;
/*    */ 
/*    */   public DefaultMethodAccessor(Method method)
/*    */   {
/* 10 */     this.method = method;
/*    */   }
/*    */ 
/*    */   public Object invoke(Object target, Object[] args)
/*    */   {
/*    */     try {
/* 16 */       return this.method.invoke(target, args);
/*    */     } catch (IllegalAccessException e) {
/* 18 */       throw new IllegalStateException("Cannot use reflection.", e);
/*    */     } catch (InvocationTargetException e) {
/* 20 */       throw new RuntimeException("An internal error occured.", e.getCause());
/*    */     } catch (IllegalArgumentException e) {
/* 22 */       throw e;
/*    */     }
/*    */   }
/*    */ 
/*    */   public Method getMethod()
/*    */   {
/* 28 */     return this.method;
/*    */   }
/*    */ 
/*    */   public int hashCode()
/*    */   {
/* 33 */     return this.method != null ? this.method.hashCode() : 0;
/*    */   }
/*    */ 
/*    */   public boolean equals(Object obj)
/*    */   {
/* 38 */     if (this == obj) {
/* 39 */       return true;
/*    */     }
/* 41 */     if ((obj instanceof DefaultMethodAccessor)) {
/* 42 */       DefaultMethodAccessor other = (DefaultMethodAccessor)obj;
/* 43 */       return other.method == this.method;
/*    */     }
/* 45 */     return true;
/*    */   }
/*    */ 
/*    */   public String toString()
/*    */   {
/* 50 */     return "DefaultMethodAccessor [method=" + this.method + "]";
/*    */   }
/*    */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.reflect.accessors.DefaultMethodAccessor
 * JD-Core Version:    0.6.2
 */