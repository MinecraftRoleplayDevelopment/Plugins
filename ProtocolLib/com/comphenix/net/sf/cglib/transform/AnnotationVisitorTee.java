/*    */ package com.comphenix.net.sf.cglib.transform;
/*    */ 
/*    */ import com.comphenix.net.sf.cglib.asm.AnnotationVisitor;
/*    */ 
/*    */ public class AnnotationVisitorTee
/*    */   implements AnnotationVisitor
/*    */ {
/*    */   private AnnotationVisitor av1;
/*    */   private AnnotationVisitor av2;
/*    */ 
/*    */   public static AnnotationVisitor getInstance(AnnotationVisitor av1, AnnotationVisitor av2)
/*    */   {
/* 24 */     if (av1 == null)
/* 25 */       return av2;
/* 26 */     if (av2 == null)
/* 27 */       return av1;
/* 28 */     return new AnnotationVisitorTee(av1, av2);
/*    */   }
/*    */ 
/*    */   public AnnotationVisitorTee(AnnotationVisitor av1, AnnotationVisitor av2) {
/* 32 */     this.av1 = av1;
/* 33 */     this.av2 = av2;
/*    */   }
/*    */ 
/*    */   public void visit(String name, Object value) {
/* 37 */     this.av2.visit(name, value);
/* 38 */     this.av2.visit(name, value);
/*    */   }
/*    */ 
/*    */   public void visitEnum(String name, String desc, String value) {
/* 42 */     this.av1.visitEnum(name, desc, value);
/* 43 */     this.av2.visitEnum(name, desc, value);
/*    */   }
/*    */ 
/*    */   public AnnotationVisitor visitAnnotation(String name, String desc) {
/* 47 */     return getInstance(this.av1.visitAnnotation(name, desc), this.av2.visitAnnotation(name, desc));
/*    */   }
/*    */ 
/*    */   public AnnotationVisitor visitArray(String name)
/*    */   {
/* 52 */     return getInstance(this.av1.visitArray(name), this.av2.visitArray(name));
/*    */   }
/*    */ 
/*    */   public void visitEnd() {
/* 56 */     this.av1.visitEnd();
/* 57 */     this.av2.visitEnd();
/*    */   }
/*    */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.net.sf.cglib.transform.AnnotationVisitorTee
 * JD-Core Version:    0.6.2
 */