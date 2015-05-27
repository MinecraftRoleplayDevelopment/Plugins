/*    */ package com.comphenix.net.sf.cglib.transform;
/*    */ 
/*    */ import com.comphenix.net.sf.cglib.asm.ClassVisitor;
/*    */ import com.comphenix.net.sf.cglib.core.ClassGenerator;
/*    */ 
/*    */ public class TransformingClassGenerator
/*    */   implements ClassGenerator
/*    */ {
/*    */   private ClassGenerator gen;
/*    */   private ClassTransformer t;
/*    */ 
/*    */   public TransformingClassGenerator(ClassGenerator gen, ClassTransformer t)
/*    */   {
/* 27 */     this.gen = gen;
/* 28 */     this.t = t;
/*    */   }
/*    */ 
/*    */   public void generateClass(ClassVisitor v) throws Exception {
/* 32 */     this.t.setTarget(v);
/* 33 */     this.gen.generateClass(this.t);
/*    */   }
/*    */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.net.sf.cglib.transform.TransformingClassGenerator
 * JD-Core Version:    0.6.2
 */