/*    */ package com.comphenix.net.sf.cglib.transform.impl;
/*    */ 
/*    */ import com.comphenix.net.sf.cglib.core.ClassGenerator;
/*    */ import com.comphenix.net.sf.cglib.core.DefaultGeneratorStrategy;
/*    */ import com.comphenix.net.sf.cglib.core.TypeUtils;
/*    */ import com.comphenix.net.sf.cglib.transform.ClassTransformer;
/*    */ import com.comphenix.net.sf.cglib.transform.MethodFilter;
/*    */ import com.comphenix.net.sf.cglib.transform.MethodFilterTransformer;
/*    */ import com.comphenix.net.sf.cglib.transform.TransformingClassGenerator;
/*    */ 
/*    */ public class UndeclaredThrowableStrategy extends DefaultGeneratorStrategy
/*    */ {
/*    */   private ClassTransformer t;
/* 43 */   private static final MethodFilter TRANSFORM_FILTER = new MethodFilter() {
/*    */     public boolean accept(int access, String name, String desc, String signature, String[] exceptions) {
/* 45 */       return (!TypeUtils.isPrivate(access)) && (name.indexOf('$') < 0);
/*    */     }
/* 43 */   };
/*    */ 
/*    */   public UndeclaredThrowableStrategy(Class wrapper)
/*    */   {
/* 39 */     this.t = new UndeclaredThrowableTransformer(wrapper);
/* 40 */     this.t = new MethodFilterTransformer(TRANSFORM_FILTER, this.t);
/*    */   }
/*    */ 
/*    */   protected ClassGenerator transform(ClassGenerator cg)
/*    */     throws Exception
/*    */   {
/* 50 */     return new TransformingClassGenerator(cg, this.t);
/*    */   }
/*    */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.net.sf.cglib.transform.impl.UndeclaredThrowableStrategy
 * JD-Core Version:    0.6.2
 */