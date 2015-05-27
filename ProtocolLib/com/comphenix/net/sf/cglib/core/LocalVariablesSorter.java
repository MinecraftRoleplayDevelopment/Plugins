/*     */ package com.comphenix.net.sf.cglib.core;
/*     */ 
/*     */ import com.comphenix.net.sf.cglib.asm.Label;
/*     */ import com.comphenix.net.sf.cglib.asm.MethodAdapter;
/*     */ import com.comphenix.net.sf.cglib.asm.MethodVisitor;
/*     */ import com.comphenix.net.sf.cglib.asm.Type;
/*     */ 
/*     */ public class LocalVariablesSorter extends MethodAdapter
/*     */ {
/*     */   protected final int firstLocal;
/*     */   private final State state;
/*     */ 
/*     */   public LocalVariablesSorter(int access, String desc, MethodVisitor mv)
/*     */   {
/*  67 */     super(mv);
/*  68 */     this.state = new State(null);
/*  69 */     Type[] args = Type.getArgumentTypes(desc);
/*  70 */     this.state.nextLocal = ((0x8 & access) != 0 ? 0 : 1);
/*  71 */     for (int i = 0; i < args.length; i++) {
/*  72 */       this.state.nextLocal += args[i].getSize();
/*     */     }
/*  74 */     this.firstLocal = this.state.nextLocal;
/*     */   }
/*     */ 
/*     */   public LocalVariablesSorter(LocalVariablesSorter lvs) {
/*  78 */     super(lvs.mv);
/*  79 */     this.state = lvs.state;
/*  80 */     this.firstLocal = lvs.firstLocal;
/*     */   }
/*     */ 
/*     */   public void visitVarInsn(int opcode, int var)
/*     */   {
/*     */     int size;
/*  85 */     switch (opcode) {
/*     */     case 22:
/*     */     case 24:
/*     */     case 55:
/*     */     case 57:
/*  90 */       size = 2;
/*  91 */       break;
/*     */     default:
/*  93 */       size = 1;
/*     */     }
/*  95 */     this.mv.visitVarInsn(opcode, remap(var, size));
/*     */   }
/*     */ 
/*     */   public void visitIincInsn(int var, int increment) {
/*  99 */     this.mv.visitIincInsn(remap(var, 1), increment);
/*     */   }
/*     */ 
/*     */   public void visitMaxs(int maxStack, int maxLocals) {
/* 103 */     this.mv.visitMaxs(maxStack, this.state.nextLocal);
/*     */   }
/*     */ 
/*     */   public void visitLocalVariable(String name, String desc, String signature, Label start, Label end, int index)
/*     */   {
/* 114 */     this.mv.visitLocalVariable(name, desc, signature, start, end, remap(index));
/*     */   }
/*     */ 
/*     */   protected int newLocal(int size)
/*     */   {
/* 120 */     int var = this.state.nextLocal;
/* 121 */     this.state.nextLocal += size;
/* 122 */     return var;
/*     */   }
/*     */ 
/*     */   private int remap(int var, int size) {
/* 126 */     if (var < this.firstLocal) {
/* 127 */       return var;
/*     */     }
/* 129 */     int key = 2 * var + size - 1;
/* 130 */     int length = this.state.mapping.length;
/* 131 */     if (key >= length) {
/* 132 */       int[] newMapping = new int[Math.max(2 * length, key + 1)];
/* 133 */       System.arraycopy(this.state.mapping, 0, newMapping, 0, length);
/* 134 */       this.state.mapping = newMapping;
/*     */     }
/* 136 */     int value = this.state.mapping[key];
/* 137 */     if (value == 0) {
/* 138 */       value = this.state.nextLocal + 1;
/* 139 */       this.state.mapping[key] = value;
/* 140 */       this.state.nextLocal += size;
/*     */     }
/* 142 */     return value - 1;
/*     */   }
/*     */ 
/*     */   private int remap(int var) {
/* 146 */     if (var < this.firstLocal) {
/* 147 */       return var;
/*     */     }
/* 149 */     int key = 2 * var;
/* 150 */     int value = key < this.state.mapping.length ? this.state.mapping[key] : 0;
/* 151 */     if (value == 0) {
/* 152 */       value = key + 1 < this.state.mapping.length ? this.state.mapping[(key + 1)] : 0;
/*     */     }
/* 154 */     if (value == 0) {
/* 155 */       throw new IllegalStateException("Unknown local variable " + var);
/*     */     }
/* 157 */     return value - 1;
/*     */   }
/*     */ 
/*     */   private static class State
/*     */   {
/*  55 */     int[] mapping = new int[40];
/*     */     int nextLocal;
/*     */ 
/*     */     private State()
/*     */     {
/*     */     }
/*     */ 
/*     */     State(LocalVariablesSorter.1 x0)
/*     */     {
/*  53 */       this();
/*     */     }
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.net.sf.cglib.core.LocalVariablesSorter
 * JD-Core Version:    0.6.2
 */