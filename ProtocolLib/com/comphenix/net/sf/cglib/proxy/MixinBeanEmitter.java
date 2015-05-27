/*    */ package com.comphenix.net.sf.cglib.proxy;
/*    */ 
/*    */ import com.comphenix.net.sf.cglib.asm.ClassVisitor;
/*    */ import com.comphenix.net.sf.cglib.core.ReflectUtils;
/*    */ import java.lang.reflect.Method;
/*    */ 
/*    */ class MixinBeanEmitter extends MixinEmitter
/*    */ {
/*    */   public MixinBeanEmitter(ClassVisitor v, String className, Class[] classes)
/*    */   {
/* 28 */     super(v, className, classes, null);
/*    */   }
/*    */ 
/*    */   protected Class[] getInterfaces(Class[] classes) {
/* 32 */     return null;
/*    */   }
/*    */ 
/*    */   protected Method[] getMethods(Class type) {
/* 36 */     return ReflectUtils.getPropertyMethods(ReflectUtils.getBeanProperties(type), true, true);
/*    */   }
/*    */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.net.sf.cglib.proxy.MixinBeanEmitter
 * JD-Core Version:    0.6.2
 */