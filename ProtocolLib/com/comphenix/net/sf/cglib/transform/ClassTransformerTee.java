/*    */ package com.comphenix.net.sf.cglib.transform;
/*    */ 
/*    */ import com.comphenix.net.sf.cglib.asm.ClassAdapter;
/*    */ import com.comphenix.net.sf.cglib.asm.ClassVisitor;
/*    */ 
/*    */ public class ClassTransformerTee extends ClassAdapter
/*    */   implements ClassTransformer
/*    */ {
/*    */   private ClassVisitor branch;
/*    */ 
/*    */   public ClassTransformerTee(ClassVisitor branch)
/*    */   {
/* 25 */     super(null);
/* 26 */     this.branch = branch;
/*    */   }
/*    */ 
/*    */   public void setTarget(ClassVisitor target) {
/* 30 */     this.cv = new ClassVisitorTee(this.branch, target);
/*    */   }
/*    */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.net.sf.cglib.transform.ClassTransformerTee
 * JD-Core Version:    0.6.2
 */