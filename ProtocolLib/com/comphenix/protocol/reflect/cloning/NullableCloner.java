/*    */ package com.comphenix.protocol.reflect.cloning;
/*    */ 
/*    */ public class NullableCloner
/*    */   implements Cloner
/*    */ {
/*    */   protected Cloner wrapped;
/*    */ 
/*    */   public NullableCloner(Cloner wrapped)
/*    */   {
/* 29 */     this.wrapped = wrapped;
/*    */   }
/*    */ 
/*    */   public boolean canClone(Object source)
/*    */   {
/* 34 */     return true;
/*    */   }
/*    */ 
/*    */   public Object clone(Object source)
/*    */   {
/* 40 */     if (source == null) {
/* 41 */       return null;
/*    */     }
/* 43 */     return this.wrapped.clone(source);
/*    */   }
/*    */ 
/*    */   public Cloner getWrapped() {
/* 47 */     return this.wrapped;
/*    */   }
/*    */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.reflect.cloning.NullableCloner
 * JD-Core Version:    0.6.2
 */