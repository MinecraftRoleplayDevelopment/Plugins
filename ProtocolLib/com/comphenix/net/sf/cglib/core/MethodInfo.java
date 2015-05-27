/*    */ package com.comphenix.net.sf.cglib.core;
/*    */ 
/*    */ import com.comphenix.net.sf.cglib.asm.Type;
/*    */ 
/*    */ public abstract class MethodInfo
/*    */ {
/*    */   public abstract ClassInfo getClassInfo();
/*    */ 
/*    */   public abstract int getModifiers();
/*    */ 
/*    */   public abstract Signature getSignature();
/*    */ 
/*    */   public abstract Type[] getExceptionTypes();
/*    */ 
/*    */   public boolean equals(Object o)
/*    */   {
/* 32 */     if (o == null)
/* 33 */       return false;
/* 34 */     if (!(o instanceof MethodInfo))
/* 35 */       return false;
/* 36 */     return getSignature().equals(((MethodInfo)o).getSignature());
/*    */   }
/*    */ 
/*    */   public int hashCode() {
/* 40 */     return getSignature().hashCode();
/*    */   }
/*    */ 
/*    */   public String toString()
/*    */   {
/* 45 */     return getSignature().toString();
/*    */   }
/*    */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.net.sf.cglib.core.MethodInfo
 * JD-Core Version:    0.6.2
 */