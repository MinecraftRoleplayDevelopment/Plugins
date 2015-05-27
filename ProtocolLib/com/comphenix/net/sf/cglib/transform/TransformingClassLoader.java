/*    */ package com.comphenix.net.sf.cglib.transform;
/*    */ 
/*    */ import com.comphenix.net.sf.cglib.asm.ClassReader;
/*    */ import com.comphenix.net.sf.cglib.core.ClassGenerator;
/*    */ 
/*    */ public class TransformingClassLoader extends AbstractClassLoader
/*    */ {
/*    */   private ClassTransformerFactory t;
/*    */ 
/*    */   public TransformingClassLoader(ClassLoader parent, ClassFilter filter, ClassTransformerFactory t)
/*    */   {
/* 26 */     super(parent, parent, filter);
/* 27 */     this.t = t;
/*    */   }
/*    */ 
/*    */   protected ClassGenerator getGenerator(ClassReader r) {
/* 31 */     ClassTransformer t2 = this.t.newInstance();
/* 32 */     return new TransformingClassGenerator(super.getGenerator(r), t2);
/*    */   }
/*    */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.net.sf.cglib.transform.TransformingClassLoader
 * JD-Core Version:    0.6.2
 */