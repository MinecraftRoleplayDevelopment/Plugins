/*    */ package com.comphenix.net.sf.cglib.transform;
/*    */ 
/*    */ import com.comphenix.net.sf.cglib.asm.AnnotationVisitor;
/*    */ import com.comphenix.net.sf.cglib.asm.Attribute;
/*    */ import com.comphenix.net.sf.cglib.asm.FieldVisitor;
/*    */ 
/*    */ public class FieldVisitorTee
/*    */   implements FieldVisitor
/*    */ {
/*    */   private FieldVisitor fv1;
/*    */   private FieldVisitor fv2;
/*    */ 
/*    */   public FieldVisitorTee(FieldVisitor fv1, FieldVisitor fv2)
/*    */   {
/* 26 */     this.fv1 = fv1;
/* 27 */     this.fv2 = fv2;
/*    */   }
/*    */ 
/*    */   public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
/* 31 */     return AnnotationVisitorTee.getInstance(this.fv1.visitAnnotation(desc, visible), this.fv2.visitAnnotation(desc, visible));
/*    */   }
/*    */ 
/*    */   public void visitAttribute(Attribute attr)
/*    */   {
/* 36 */     this.fv1.visitAttribute(attr);
/* 37 */     this.fv2.visitAttribute(attr);
/*    */   }
/*    */ 
/*    */   public void visitEnd() {
/* 41 */     this.fv1.visitEnd();
/* 42 */     this.fv2.visitEnd();
/*    */   }
/*    */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.net.sf.cglib.transform.FieldVisitorTee
 * JD-Core Version:    0.6.2
 */