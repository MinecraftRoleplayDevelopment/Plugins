/*    */ package com.comphenix.net.sf.cglib.transform;
/*    */ 
/*    */ import com.comphenix.net.sf.cglib.asm.ClassAdapter;
/*    */ import com.comphenix.net.sf.cglib.asm.ClassVisitor;
/*    */ 
/*    */ public abstract class AbstractClassTransformer extends ClassAdapter
/*    */   implements ClassTransformer
/*    */ {
/*    */   protected AbstractClassTransformer()
/*    */   {
/* 24 */     super(null);
/*    */   }
/*    */ 
/*    */   public void setTarget(ClassVisitor target) {
/* 28 */     this.cv = target;
/*    */   }
/*    */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.net.sf.cglib.transform.AbstractClassTransformer
 * JD-Core Version:    0.6.2
 */