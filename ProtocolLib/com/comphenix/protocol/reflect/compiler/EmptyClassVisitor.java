/*    */ package com.comphenix.protocol.reflect.compiler;
/*    */ 
/*    */ import com.comphenix.net.sf.cglib.asm.AnnotationVisitor;
/*    */ import com.comphenix.net.sf.cglib.asm.Attribute;
/*    */ import com.comphenix.net.sf.cglib.asm.ClassVisitor;
/*    */ import com.comphenix.net.sf.cglib.asm.FieldVisitor;
/*    */ import com.comphenix.net.sf.cglib.asm.MethodVisitor;
/*    */ 
/*    */ public abstract class EmptyClassVisitor
/*    */   implements ClassVisitor
/*    */ {
/*    */   public void visit(int version, int access, String name, String signature, String superName, String[] interfaces)
/*    */   {
/*    */   }
/*    */ 
/*    */   public AnnotationVisitor visitAnnotation(String desc, boolean visible)
/*    */   {
/* 18 */     return null;
/*    */   }
/*    */ 
/*    */   public void visitAttribute(Attribute attr)
/*    */   {
/*    */   }
/*    */ 
/*    */   public void visitEnd()
/*    */   {
/*    */   }
/*    */ 
/*    */   public FieldVisitor visitField(int access, String name, String desc, String signature, Object value)
/*    */   {
/* 34 */     return null;
/*    */   }
/*    */ 
/*    */   public void visitInnerClass(String name, String outerName, String innerName, int access)
/*    */   {
/*    */   }
/*    */ 
/*    */   public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions)
/*    */   {
/* 45 */     return null;
/*    */   }
/*    */ 
/*    */   public void visitOuterClass(String owner, String name, String desc)
/*    */   {
/*    */   }
/*    */ 
/*    */   public void visitSource(String source, String debug)
/*    */   {
/*    */   }
/*    */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.reflect.compiler.EmptyClassVisitor
 * JD-Core Version:    0.6.2
 */