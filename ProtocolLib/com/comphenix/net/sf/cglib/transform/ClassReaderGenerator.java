/*    */ package com.comphenix.net.sf.cglib.transform;
/*    */ 
/*    */ import com.comphenix.net.sf.cglib.asm.Attribute;
/*    */ import com.comphenix.net.sf.cglib.asm.ClassReader;
/*    */ import com.comphenix.net.sf.cglib.asm.ClassVisitor;
/*    */ import com.comphenix.net.sf.cglib.core.ClassGenerator;
/*    */ 
/*    */ public class ClassReaderGenerator
/*    */   implements ClassGenerator
/*    */ {
/*    */   private final ClassReader r;
/*    */   private final Attribute[] attrs;
/*    */   private final int flags;
/*    */ 
/*    */   public ClassReaderGenerator(ClassReader r, int flags)
/*    */   {
/* 29 */     this(r, null, flags);
/*    */   }
/*    */ 
/*    */   public ClassReaderGenerator(ClassReader r, Attribute[] attrs, int flags) {
/* 33 */     this.r = r;
/* 34 */     this.attrs = (attrs != null ? attrs : new Attribute[0]);
/* 35 */     this.flags = flags;
/*    */   }
/*    */ 
/*    */   public void generateClass(ClassVisitor v) {
/* 39 */     this.r.accept(v, this.attrs, this.flags);
/*    */   }
/*    */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.net.sf.cglib.transform.ClassReaderGenerator
 * JD-Core Version:    0.6.2
 */