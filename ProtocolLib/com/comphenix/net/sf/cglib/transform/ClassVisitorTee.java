/*    */ package com.comphenix.net.sf.cglib.transform;
/*    */ 
/*    */ import com.comphenix.net.sf.cglib.asm.AnnotationVisitor;
/*    */ import com.comphenix.net.sf.cglib.asm.Attribute;
/*    */ import com.comphenix.net.sf.cglib.asm.ClassVisitor;
/*    */ import com.comphenix.net.sf.cglib.asm.FieldVisitor;
/*    */ import com.comphenix.net.sf.cglib.asm.MethodVisitor;
/*    */ 
/*    */ public class ClassVisitorTee
/*    */   implements ClassVisitor
/*    */ {
/*    */   private ClassVisitor cv1;
/*    */   private ClassVisitor cv2;
/*    */ 
/*    */   public ClassVisitorTee(ClassVisitor cv1, ClassVisitor cv2)
/*    */   {
/* 24 */     this.cv1 = cv1;
/* 25 */     this.cv2 = cv2;
/*    */   }
/*    */ 
/*    */   public void visit(int version, int access, String name, String signature, String superName, String[] interfaces)
/*    */   {
/* 34 */     this.cv1.visit(version, access, name, signature, superName, interfaces);
/* 35 */     this.cv2.visit(version, access, name, signature, superName, interfaces);
/*    */   }
/*    */ 
/*    */   public void visitEnd() {
/* 39 */     this.cv1.visitEnd();
/* 40 */     this.cv2.visitEnd();
/* 41 */     this.cv1 = (this.cv2 = null);
/*    */   }
/*    */ 
/*    */   public void visitInnerClass(String name, String outerName, String innerName, int access) {
/* 45 */     this.cv1.visitInnerClass(name, outerName, innerName, access);
/* 46 */     this.cv2.visitInnerClass(name, outerName, innerName, access);
/*    */   }
/*    */ 
/*    */   public FieldVisitor visitField(int access, String name, String desc, String signature, Object value)
/*    */   {
/* 54 */     FieldVisitor fv1 = this.cv1.visitField(access, name, desc, signature, value);
/* 55 */     FieldVisitor fv2 = this.cv2.visitField(access, name, desc, signature, value);
/* 56 */     if (fv1 == null)
/* 57 */       return fv2;
/* 58 */     if (fv2 == null)
/* 59 */       return fv1;
/* 60 */     return new FieldVisitorTee(fv1, fv2);
/*    */   }
/*    */ 
/*    */   public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions)
/*    */   {
/* 69 */     MethodVisitor mv1 = this.cv1.visitMethod(access, name, desc, signature, exceptions);
/* 70 */     MethodVisitor mv2 = this.cv2.visitMethod(access, name, desc, signature, exceptions);
/* 71 */     if (mv1 == null)
/* 72 */       return mv2;
/* 73 */     if (mv2 == null)
/* 74 */       return mv1;
/* 75 */     return new MethodVisitorTee(mv1, mv2);
/*    */   }
/*    */ 
/*    */   public void visitSource(String source, String debug) {
/* 79 */     this.cv1.visitSource(source, debug);
/* 80 */     this.cv2.visitSource(source, debug);
/*    */   }
/*    */ 
/*    */   public void visitOuterClass(String owner, String name, String desc) {
/* 84 */     this.cv1.visitOuterClass(owner, name, desc);
/* 85 */     this.cv2.visitOuterClass(owner, name, desc);
/*    */   }
/*    */ 
/*    */   public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
/* 89 */     return AnnotationVisitorTee.getInstance(this.cv1.visitAnnotation(desc, visible), this.cv2.visitAnnotation(desc, visible));
/*    */   }
/*    */ 
/*    */   public void visitAttribute(Attribute attrs)
/*    */   {
/* 94 */     this.cv1.visitAttribute(attrs);
/* 95 */     this.cv2.visitAttribute(attrs);
/*    */   }
/*    */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.net.sf.cglib.transform.ClassVisitorTee
 * JD-Core Version:    0.6.2
 */