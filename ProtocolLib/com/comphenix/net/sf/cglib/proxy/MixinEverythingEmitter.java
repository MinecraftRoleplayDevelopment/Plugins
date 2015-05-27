/*    */ package com.comphenix.net.sf.cglib.proxy;
/*    */ 
/*    */ import com.comphenix.net.sf.cglib.asm.ClassVisitor;
/*    */ import com.comphenix.net.sf.cglib.core.CollectionUtils;
/*    */ import com.comphenix.net.sf.cglib.core.ReflectUtils;
/*    */ import com.comphenix.net.sf.cglib.core.RejectModifierPredicate;
/*    */ import java.lang.reflect.Method;
/*    */ import java.util.ArrayList;
/*    */ import java.util.Arrays;
/*    */ import java.util.List;
/*    */ 
/*    */ class MixinEverythingEmitter extends MixinEmitter
/*    */ {
/*    */   public MixinEverythingEmitter(ClassVisitor v, String className, Class[] classes)
/*    */   {
/* 33 */     super(v, className, classes, null);
/*    */   }
/*    */ 
/*    */   protected Class[] getInterfaces(Class[] classes) {
/* 37 */     List list = new ArrayList();
/* 38 */     for (int i = 0; i < classes.length; i++) {
/* 39 */       ReflectUtils.addAllInterfaces(classes[i], list);
/*    */     }
/* 41 */     return (Class[])list.toArray(new Class[list.size()]);
/*    */   }
/*    */ 
/*    */   protected Method[] getMethods(Class type) {
/* 45 */     List methods = new ArrayList(Arrays.asList(type.getMethods()));
/* 46 */     CollectionUtils.filter(methods, new RejectModifierPredicate(24));
/* 47 */     return (Method[])methods.toArray(new Method[methods.size()]);
/*    */   }
/*    */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.net.sf.cglib.proxy.MixinEverythingEmitter
 * JD-Core Version:    0.6.2
 */