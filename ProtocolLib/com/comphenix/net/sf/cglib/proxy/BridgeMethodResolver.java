/*     */ package com.comphenix.net.sf.cglib.proxy;
/*     */ 
/*     */ import com.comphenix.net.sf.cglib.asm.AnnotationVisitor;
/*     */ import com.comphenix.net.sf.cglib.asm.Attribute;
/*     */ import com.comphenix.net.sf.cglib.asm.ClassReader;
/*     */ import com.comphenix.net.sf.cglib.asm.ClassVisitor;
/*     */ import com.comphenix.net.sf.cglib.asm.FieldVisitor;
/*     */ import com.comphenix.net.sf.cglib.asm.Label;
/*     */ import com.comphenix.net.sf.cglib.asm.MethodVisitor;
/*     */ import com.comphenix.net.sf.cglib.core.Signature;
/*     */ import java.io.IOException;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.Set;
/*     */ 
/*     */ class BridgeMethodResolver
/*     */ {
/*     */   private final Map declToBridge;
/*     */ 
/*     */   public BridgeMethodResolver(Map declToBridge)
/*     */   {
/*  47 */     this.declToBridge = declToBridge;
/*     */   }
/*     */ 
/*     */   public Map resolveAll()
/*     */   {
/*  55 */     Map resolved = new HashMap();
/*  56 */     for (Iterator entryIter = this.declToBridge.entrySet().iterator(); entryIter.hasNext(); ) {
/*  57 */       Map.Entry entry = (Map.Entry)entryIter.next();
/*  58 */       Class owner = (Class)entry.getKey();
/*  59 */       Set bridges = (Set)entry.getValue();
/*     */       try {
/*  61 */         new ClassReader(owner.getName()).accept(new BridgedFinder(bridges, resolved), 6);
/*     */       }
/*     */       catch (IOException ignored) {
/*     */       }
/*     */     }
/*  66 */     return resolved;
/*     */   }
/*     */ 
/*     */   private static class BridgedFinder
/*     */     implements ClassVisitor, MethodVisitor
/*     */   {
/*     */     private Map resolved;
/*     */     private Set eligableMethods;
/*  73 */     private Signature currentMethod = null;
/*     */ 
/*     */     BridgedFinder(Set eligableMethods, Map resolved) {
/*  76 */       this.resolved = resolved;
/*  77 */       this.eligableMethods = eligableMethods;
/*     */     }
/*     */ 
/*     */     public void visit(int version, int access, String name, String signature, String superName, String[] interfaces)
/*     */     {
/*     */     }
/*     */ 
/*     */     public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions)
/*     */     {
/*  86 */       Signature sig = new Signature(name, desc);
/*  87 */       if (this.eligableMethods.remove(sig)) {
/*  88 */         this.currentMethod = sig;
/*  89 */         return this;
/*     */       }
/*  91 */       return null;
/*     */     }
/*     */ 
/*     */     public void visitSource(String source, String debug)
/*     */     {
/*     */     }
/*     */ 
/*     */     public void visitLineNumber(int line, Label start)
/*     */     {
/*     */     }
/*     */ 
/*     */     public void visitFieldInsn(int opcode, String owner, String name, String desc)
/*     */     {
/*     */     }
/*     */ 
/*     */     public void visitEnd()
/*     */     {
/*     */     }
/*     */ 
/*     */     public void visitInnerClass(String name, String outerName, String innerName, int access) {
/*     */     }
/*     */ 
/*     */     public void visitOuterClass(String owner, String name, String desc) {
/*     */     }
/*     */ 
/*     */     public void visitAttribute(Attribute attr) {
/*     */     }
/*     */ 
/*     */     public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
/* 120 */       return null;
/*     */     }
/*     */ 
/*     */     public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
/* 124 */       return null;
/*     */     }
/*     */ 
/*     */     public AnnotationVisitor visitAnnotationDefault() {
/* 128 */       return null;
/*     */     }
/*     */ 
/*     */     public AnnotationVisitor visitParameterAnnotation(int parameter, String desc, boolean visible)
/*     */     {
/* 133 */       return null;
/*     */     }
/*     */ 
/*     */     public void visitCode()
/*     */     {
/*     */     }
/*     */ 
/*     */     public void visitFrame(int type, int nLocal, Object[] local, int nStack, Object[] stack)
/*     */     {
/*     */     }
/*     */ 
/*     */     public void visitIincInsn(int var, int increment)
/*     */     {
/*     */     }
/*     */ 
/*     */     public void visitInsn(int opcode) {
/*     */     }
/*     */ 
/*     */     public void visitIntInsn(int opcode, int operand) {
/*     */     }
/*     */ 
/*     */     public void visitJumpInsn(int opcode, Label label) {
/*     */     }
/*     */ 
/*     */     public void visitLabel(Label label) {
/*     */     }
/*     */ 
/*     */     public void visitLdcInsn(Object cst) {
/*     */     }
/*     */ 
/*     */     public void visitLocalVariable(String name, String desc, String signature, Label start, Label end, int index) {
/*     */     }
/*     */ 
/*     */     public void visitLookupSwitchInsn(Label dflt, int[] keys, Label[] labels) {
/*     */     }
/*     */ 
/*     */     public void visitMaxs(int maxStack, int maxLocals) {
/*     */     }
/*     */ 
/*     */     public void visitMethodInsn(int opcode, String owner, String name, String desc) {
/* 173 */       if ((opcode == 183) && (this.currentMethod != null)) {
/* 174 */         Signature target = new Signature(name, desc);
/*     */ 
/* 181 */         if (!target.equals(this.currentMethod)) {
/* 182 */           this.resolved.put(this.currentMethod, target);
/*     */         }
/* 184 */         this.currentMethod = null;
/*     */       }
/*     */     }
/*     */ 
/*     */     public void visitMultiANewArrayInsn(String desc, int dims)
/*     */     {
/*     */     }
/*     */ 
/*     */     public void visitTableSwitchInsn(int min, int max, Label dflt, Label[] labels)
/*     */     {
/*     */     }
/*     */ 
/*     */     public void visitTryCatchBlock(Label start, Label end, Label handler, String type)
/*     */     {
/*     */     }
/*     */ 
/*     */     public void visitTypeInsn(int opcode, String desc)
/*     */     {
/*     */     }
/*     */ 
/*     */     public void visitVarInsn(int opcode, int var)
/*     */     {
/*     */     }
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.net.sf.cglib.proxy.BridgeMethodResolver
 * JD-Core Version:    0.6.2
 */