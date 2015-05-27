/*     */ package com.comphenix.net.sf.cglib.transform;
/*     */ 
/*     */ import com.comphenix.net.sf.cglib.asm.AnnotationVisitor;
/*     */ import com.comphenix.net.sf.cglib.asm.Attribute;
/*     */ import com.comphenix.net.sf.cglib.asm.Label;
/*     */ import com.comphenix.net.sf.cglib.asm.MethodVisitor;
/*     */ 
/*     */ public class MethodVisitorTee
/*     */   implements MethodVisitor
/*     */ {
/*     */   private final MethodVisitor mv1;
/*     */   private final MethodVisitor mv2;
/*     */ 
/*     */   public MethodVisitorTee(MethodVisitor mv1, MethodVisitor mv2)
/*     */   {
/*  25 */     this.mv1 = mv1;
/*  26 */     this.mv2 = mv2;
/*     */   }
/*     */ 
/*     */   public void visitFrame(int type, int nLocal, Object[] local, int nStack, Object[] stack) {
/*  30 */     this.mv1.visitFrame(type, nLocal, local, nStack, stack);
/*  31 */     this.mv2.visitFrame(type, nLocal, local, nStack, stack);
/*     */   }
/*     */ 
/*     */   public AnnotationVisitor visitAnnotationDefault() {
/*  35 */     return AnnotationVisitorTee.getInstance(this.mv1.visitAnnotationDefault(), this.mv2.visitAnnotationDefault());
/*     */   }
/*     */ 
/*     */   public AnnotationVisitor visitAnnotation(String desc, boolean visible)
/*     */   {
/*  40 */     return AnnotationVisitorTee.getInstance(this.mv1.visitAnnotation(desc, visible), this.mv2.visitAnnotation(desc, visible));
/*     */   }
/*     */ 
/*     */   public AnnotationVisitor visitParameterAnnotation(int parameter, String desc, boolean visible)
/*     */   {
/*  47 */     return AnnotationVisitorTee.getInstance(this.mv1.visitParameterAnnotation(parameter, desc, visible), this.mv2.visitParameterAnnotation(parameter, desc, visible));
/*     */   }
/*     */ 
/*     */   public void visitAttribute(Attribute attr)
/*     */   {
/*  52 */     this.mv1.visitAttribute(attr);
/*  53 */     this.mv2.visitAttribute(attr);
/*     */   }
/*     */ 
/*     */   public void visitCode() {
/*  57 */     this.mv1.visitCode();
/*  58 */     this.mv2.visitCode();
/*     */   }
/*     */ 
/*     */   public void visitInsn(int opcode) {
/*  62 */     this.mv1.visitInsn(opcode);
/*  63 */     this.mv2.visitInsn(opcode);
/*     */   }
/*     */ 
/*     */   public void visitIntInsn(int opcode, int operand) {
/*  67 */     this.mv1.visitIntInsn(opcode, operand);
/*  68 */     this.mv2.visitIntInsn(opcode, operand);
/*     */   }
/*     */ 
/*     */   public void visitVarInsn(int opcode, int var) {
/*  72 */     this.mv1.visitVarInsn(opcode, var);
/*  73 */     this.mv2.visitVarInsn(opcode, var);
/*     */   }
/*     */ 
/*     */   public void visitTypeInsn(int opcode, String desc) {
/*  77 */     this.mv1.visitTypeInsn(opcode, desc);
/*  78 */     this.mv2.visitTypeInsn(opcode, desc);
/*     */   }
/*     */ 
/*     */   public void visitFieldInsn(int opcode, String owner, String name, String desc) {
/*  82 */     this.mv1.visitFieldInsn(opcode, owner, name, desc);
/*  83 */     this.mv2.visitFieldInsn(opcode, owner, name, desc);
/*     */   }
/*     */ 
/*     */   public void visitMethodInsn(int opcode, String owner, String name, String desc) {
/*  87 */     this.mv1.visitMethodInsn(opcode, owner, name, desc);
/*  88 */     this.mv2.visitMethodInsn(opcode, owner, name, desc);
/*     */   }
/*     */ 
/*     */   public void visitJumpInsn(int opcode, Label label) {
/*  92 */     this.mv1.visitJumpInsn(opcode, label);
/*  93 */     this.mv2.visitJumpInsn(opcode, label);
/*     */   }
/*     */ 
/*     */   public void visitLabel(Label label) {
/*  97 */     this.mv1.visitLabel(label);
/*  98 */     this.mv2.visitLabel(label);
/*     */   }
/*     */ 
/*     */   public void visitLdcInsn(Object cst) {
/* 102 */     this.mv1.visitLdcInsn(cst);
/* 103 */     this.mv2.visitLdcInsn(cst);
/*     */   }
/*     */ 
/*     */   public void visitIincInsn(int var, int increment) {
/* 107 */     this.mv1.visitIincInsn(var, increment);
/* 108 */     this.mv2.visitIincInsn(var, increment);
/*     */   }
/*     */ 
/*     */   public void visitTableSwitchInsn(int min, int max, Label dflt, Label[] labels) {
/* 112 */     this.mv1.visitTableSwitchInsn(min, max, dflt, labels);
/* 113 */     this.mv2.visitTableSwitchInsn(min, max, dflt, labels);
/*     */   }
/*     */ 
/*     */   public void visitLookupSwitchInsn(Label dflt, int[] keys, Label[] labels) {
/* 117 */     this.mv1.visitLookupSwitchInsn(dflt, keys, labels);
/* 118 */     this.mv2.visitLookupSwitchInsn(dflt, keys, labels);
/*     */   }
/*     */ 
/*     */   public void visitMultiANewArrayInsn(String desc, int dims) {
/* 122 */     this.mv1.visitMultiANewArrayInsn(desc, dims);
/* 123 */     this.mv2.visitMultiANewArrayInsn(desc, dims);
/*     */   }
/*     */ 
/*     */   public void visitTryCatchBlock(Label start, Label end, Label handler, String type) {
/* 127 */     this.mv1.visitTryCatchBlock(start, end, handler, type);
/* 128 */     this.mv2.visitTryCatchBlock(start, end, handler, type);
/*     */   }
/*     */ 
/*     */   public void visitLocalVariable(String name, String desc, String signature, Label start, Label end, int index) {
/* 132 */     this.mv1.visitLocalVariable(name, desc, signature, start, end, index);
/* 133 */     this.mv2.visitLocalVariable(name, desc, signature, start, end, index);
/*     */   }
/*     */ 
/*     */   public void visitLineNumber(int line, Label start) {
/* 137 */     this.mv1.visitLineNumber(line, start);
/* 138 */     this.mv2.visitLineNumber(line, start);
/*     */   }
/*     */ 
/*     */   public void visitMaxs(int maxStack, int maxLocals) {
/* 142 */     this.mv1.visitMaxs(maxStack, maxLocals);
/* 143 */     this.mv2.visitMaxs(maxStack, maxLocals);
/*     */   }
/*     */ 
/*     */   public void visitEnd() {
/* 147 */     this.mv1.visitEnd();
/* 148 */     this.mv2.visitEnd();
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.net.sf.cglib.transform.MethodVisitorTee
 * JD-Core Version:    0.6.2
 */