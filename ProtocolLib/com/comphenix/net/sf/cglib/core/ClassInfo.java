/*    */ package com.comphenix.net.sf.cglib.core;
/*    */ 
/*    */ import com.comphenix.net.sf.cglib.asm.Type;
/*    */ 
/*    */ public abstract class ClassInfo
/*    */ {
/*    */   public abstract Type getType();
/*    */ 
/*    */   public abstract Type getSuperType();
/*    */ 
/*    */   public abstract Type[] getInterfaces();
/*    */ 
/*    */   public abstract int getModifiers();
/*    */ 
/*    */   public boolean equals(Object o)
/*    */   {
/* 32 */     if (o == null)
/* 33 */       return false;
/* 34 */     if (!(o instanceof ClassInfo))
/* 35 */       return false;
/* 36 */     return getType().equals(((ClassInfo)o).getType());
/*    */   }
/*    */ 
/*    */   public int hashCode() {
/* 40 */     return getType().hashCode();
/*    */   }
/*    */ 
/*    */   public String toString()
/*    */   {
/* 45 */     return getType().getClassName();
/*    */   }
/*    */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.net.sf.cglib.core.ClassInfo
 * JD-Core Version:    0.6.2
 */