/*    */ package com.comphenix.net.sf.cglib.transform;
/*    */ 
/*    */ import com.comphenix.net.sf.cglib.asm.ClassVisitor;
/*    */ import com.comphenix.net.sf.cglib.asm.MethodVisitor;
/*    */ 
/*    */ public class MethodFilterTransformer extends AbstractClassTransformer
/*    */ {
/*    */   private MethodFilter filter;
/*    */   private ClassTransformer pass;
/*    */   private ClassVisitor direct;
/*    */ 
/*    */   public MethodFilterTransformer(MethodFilter filter, ClassTransformer pass)
/*    */   {
/* 26 */     this.filter = filter;
/* 27 */     this.pass = pass;
/* 28 */     super.setTarget(pass);
/*    */   }
/*    */ 
/*    */   public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions)
/*    */   {
/* 36 */     return (this.filter.accept(access, name, desc, signature, exceptions) ? this.pass : this.direct).visitMethod(access, name, desc, signature, exceptions);
/*    */   }
/*    */ 
/*    */   public void setTarget(ClassVisitor target) {
/* 40 */     this.pass.setTarget(target);
/* 41 */     this.direct = target;
/*    */   }
/*    */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.net.sf.cglib.transform.MethodFilterTransformer
 * JD-Core Version:    0.6.2
 */