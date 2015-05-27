/*    */ package com.comphenix.net.sf.cglib.core;
/*    */ 
/*    */ import java.lang.reflect.Method;
/*    */ import java.util.Collection;
/*    */ import java.util.HashSet;
/*    */ import java.util.Iterator;
/*    */ import java.util.Set;
/*    */ 
/*    */ public class MethodWrapper
/*    */ {
/* 22 */   private static final MethodWrapperKey KEY_FACTORY = (MethodWrapperKey)KeyFactory.create(MethodWrapperKey.class);
/*    */ 
/*    */   public static Object create(Method method)
/*    */   {
/* 34 */     return KEY_FACTORY.newInstance(method.getName(), ReflectUtils.getNames(method.getParameterTypes()), method.getReturnType().getName());
/*    */   }
/*    */ 
/*    */   public static Set createSet(Collection methods)
/*    */   {
/* 40 */     Set set = new HashSet();
/* 41 */     for (Iterator it = methods.iterator(); it.hasNext(); ) {
/* 42 */       set.add(create((Method)it.next()));
/*    */     }
/* 44 */     return set;
/*    */   }
/*    */ 
/*    */   public static abstract interface MethodWrapperKey
/*    */   {
/*    */     public abstract Object newInstance(String paramString1, String[] paramArrayOfString, String paramString2);
/*    */   }
/*    */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.net.sf.cglib.core.MethodWrapper
 * JD-Core Version:    0.6.2
 */