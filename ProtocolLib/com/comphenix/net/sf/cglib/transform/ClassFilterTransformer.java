/*    */ package com.comphenix.net.sf.cglib.transform;
/*    */ 
/*    */ public class ClassFilterTransformer extends AbstractClassFilterTransformer
/*    */ {
/*    */   private ClassFilter filter;
/*    */ 
/*    */   public ClassFilterTransformer(ClassFilter filter, ClassTransformer pass)
/*    */   {
/* 24 */     super(pass);
/* 25 */     this.filter = filter;
/*    */   }
/*    */ 
/*    */   protected boolean accept(int version, int access, String name, String signature, String superName, String[] interfaces) {
/* 29 */     return this.filter.accept(name.replace('/', '.'));
/*    */   }
/*    */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.net.sf.cglib.transform.ClassFilterTransformer
 * JD-Core Version:    0.6.2
 */