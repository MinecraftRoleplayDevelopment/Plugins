/*     */ package com.comphenix.protocol.reflect;
/*     */ 
/*     */ import com.comphenix.net.sf.cglib.asm.ClassReader;
/*     */ import com.comphenix.net.sf.cglib.asm.MethodVisitor;
/*     */ import com.comphenix.net.sf.cglib.asm.Type;
/*     */ import com.comphenix.protocol.reflect.compiler.EmptyClassVisitor;
/*     */ import com.comphenix.protocol.reflect.compiler.EmptyMethodVisitor;
/*     */ import com.google.common.collect.Lists;
/*     */ import java.io.IOException;
/*     */ import java.lang.reflect.Method;
/*     */ import java.util.List;
/*     */ 
/*     */ public class ClassAnalyser
/*     */ {
/*  85 */   private static final ClassAnalyser DEFAULT = new ClassAnalyser();
/*     */ 
/*     */   public static ClassAnalyser getDefault()
/*     */   {
/*  92 */     return DEFAULT;
/*     */   }
/*     */ 
/*     */   public List<AsmMethod> getMethodCalls(Method method)
/*     */     throws IOException
/*     */   {
/* 102 */     return getMethodCalls(method.getDeclaringClass(), method);
/*     */   }
/*     */ 
/*     */   public List<AsmMethod> getMethodCalls(Class<?> clazz, Method method)
/*     */     throws IOException
/*     */   {
/* 113 */     ClassReader reader = new ClassReader(clazz.getCanonicalName());
/* 114 */     final List output = Lists.newArrayList();
/*     */ 
/* 117 */     final String methodName = method.getName();
/* 118 */     final String methodDescription = Type.getMethodDescriptor(method);
/*     */ 
/* 120 */     reader.accept(new EmptyClassVisitor()
/*     */     {
/*     */       public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions)
/*     */       {
/* 124 */         if ((methodName.equals(name)) && (methodDescription.equals(desc))) {
/* 125 */           return new EmptyMethodVisitor()
/*     */           {
/*     */             public void visitMethodInsn(int opcode, String owner, String name, String desc) {
/* 128 */               ClassAnalyser.1.this.val$output.add(new ClassAnalyser.AsmMethod(ClassAnalyser.AsmMethod.AsmOpcodes.fromIntOpcode(opcode), owner, ClassAnalyser.1.this.val$methodName, desc));
/*     */             }
/*     */           };
/*     */         }
/* 132 */         return null;
/*     */       }
/*     */     }
/*     */     , 8);
/*     */ 
/* 136 */     return output;
/*     */   }
/*     */ 
/*     */   public static class AsmMethod
/*     */   {
/*     */     private final AsmOpcodes opcode;
/*     */     private final String ownerClass;
/*     */     private final String methodName;
/*     */     private final String signature;
/*     */ 
/*     */     public AsmMethod(AsmOpcodes opcode, String ownerClass, String methodName, String signature)
/*     */     {
/*  50 */       this.opcode = opcode;
/*  51 */       this.ownerClass = ownerClass;
/*  52 */       this.methodName = methodName;
/*  53 */       this.signature = signature;
/*     */     }
/*     */ 
/*     */     public String getOwnerName() {
/*  57 */       return this.ownerClass;
/*     */     }
/*     */ 
/*     */     public AsmOpcodes getOpcode()
/*     */     {
/*  65 */       return this.opcode;
/*     */     }
/*     */ 
/*     */     public Class<?> getOwnerClass()
/*     */       throws ClassNotFoundException
/*     */     {
/*  74 */       return AsmMethod.class.getClassLoader().loadClass(getOwnerName().replace('/', '.'));
/*     */     }
/*     */ 
/*     */     public String getMethodName() {
/*  78 */       return this.methodName;
/*     */     }
/*     */ 
/*     */     public String getSignature() {
/*  82 */       return this.signature;
/*     */     }
/*     */ 
/*     */     public static enum AsmOpcodes
/*     */     {
/*  26 */       INVOKE_VIRTUAL, 
/*  27 */       INVOKE_SPECIAL, 
/*  28 */       INVOKE_STATIC, 
/*  29 */       INVOKE_INTERFACE, 
/*  30 */       INVOKE_DYNAMIC;
/*     */ 
/*     */       public static AsmOpcodes fromIntOpcode(int opcode) {
/*  33 */         switch (opcode) { case 182:
/*  34 */           return INVOKE_VIRTUAL;
/*     */         case 183:
/*  35 */           return INVOKE_SPECIAL;
/*     */         case 184:
/*  36 */           return INVOKE_STATIC;
/*     */         case 185:
/*  37 */           return INVOKE_INTERFACE;
/*     */         case 186:
/*  38 */           return INVOKE_DYNAMIC; }
/*  39 */         throw new IllegalArgumentException("Unknown opcode: " + opcode);
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.reflect.ClassAnalyser
 * JD-Core Version:    0.6.2
 */