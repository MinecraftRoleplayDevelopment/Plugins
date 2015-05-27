/*    */ package com.comphenix.net.sf.cglib.core;
/*    */ 
/*    */ public class ClassesKey
/*    */ {
/* 19 */   private static final Key FACTORY = (Key)KeyFactory.create(Key.class, KeyFactory.OBJECT_BY_CLASS);
/*    */ 
/*    */   public static Object create(Object[] array)
/*    */   {
/* 29 */     return FACTORY.newInstance(array);
/*    */   }
/*    */ 
/*    */   static abstract interface Key
/*    */   {
/*    */     public abstract Object newInstance(Object[] paramArrayOfObject);
/*    */   }
/*    */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.net.sf.cglib.core.ClassesKey
 * JD-Core Version:    0.6.2
 */