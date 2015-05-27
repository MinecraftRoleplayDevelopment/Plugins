/*     */ package com.comphenix.protocol.reflect.compiler;
/*     */ 
/*     */ import com.comphenix.net.sf.cglib.asm.MethodVisitor;
/*     */ import com.comphenix.net.sf.cglib.asm.Type;
/*     */ 
/*     */ class BoxingHelper
/*     */ {
/*  29 */   private static final Type BYTE_TYPE = Type.getObjectType("java/lang/Byte");
/*  30 */   private static final Type BOOLEAN_TYPE = Type.getObjectType("java/lang/Boolean");
/*  31 */   private static final Type SHORT_TYPE = Type.getObjectType("java/lang/Short");
/*  32 */   private static final Type CHARACTER_TYPE = Type.getObjectType("java/lang/Character");
/*  33 */   private static final Type INTEGER_TYPE = Type.getObjectType("java/lang/Integer");
/*  34 */   private static final Type FLOAT_TYPE = Type.getObjectType("java/lang/Float");
/*  35 */   private static final Type LONG_TYPE = Type.getObjectType("java/lang/Long");
/*  36 */   private static final Type DOUBLE_TYPE = Type.getObjectType("java/lang/Double");
/*  37 */   private static final Type NUMBER_TYPE = Type.getObjectType("java/lang/Number");
/*  38 */   private static final Type OBJECT_TYPE = Type.getObjectType("java/lang/Object");
/*     */ 
/*  40 */   private static final MethodDescriptor BOOLEAN_VALUE = MethodDescriptor.getMethod("boolean booleanValue()");
/*  41 */   private static final MethodDescriptor CHAR_VALUE = MethodDescriptor.getMethod("char charValue()");
/*  42 */   private static final MethodDescriptor INT_VALUE = MethodDescriptor.getMethod("int intValue()");
/*  43 */   private static final MethodDescriptor FLOAT_VALUE = MethodDescriptor.getMethod("float floatValue()");
/*  44 */   private static final MethodDescriptor LONG_VALUE = MethodDescriptor.getMethod("long longValue()");
/*  45 */   private static final MethodDescriptor DOUBLE_VALUE = MethodDescriptor.getMethod("double doubleValue()");
/*     */   private MethodVisitor mv;
/*     */ 
/*     */   public BoxingHelper(MethodVisitor mv)
/*     */   {
/*  50 */     this.mv = mv;
/*     */   }
/*     */ 
/*     */   public void box(Type type)
/*     */   {
/*  60 */     if ((type.getSort() == 10) || (type.getSort() == 9)) {
/*  61 */       return;
/*     */     }
/*     */ 
/*  64 */     if (type == Type.VOID_TYPE) {
/*  65 */       push((String)null);
/*     */     } else {
/*  67 */       Type boxed = type;
/*     */ 
/*  69 */       switch (type.getSort()) {
/*     */       case 3:
/*  71 */         boxed = BYTE_TYPE;
/*  72 */         break;
/*     */       case 1:
/*  74 */         boxed = BOOLEAN_TYPE;
/*  75 */         break;
/*     */       case 4:
/*  77 */         boxed = SHORT_TYPE;
/*  78 */         break;
/*     */       case 2:
/*  80 */         boxed = CHARACTER_TYPE;
/*  81 */         break;
/*     */       case 5:
/*  83 */         boxed = INTEGER_TYPE;
/*  84 */         break;
/*     */       case 6:
/*  86 */         boxed = FLOAT_TYPE;
/*  87 */         break;
/*     */       case 7:
/*  89 */         boxed = LONG_TYPE;
/*  90 */         break;
/*     */       case 8:
/*  92 */         boxed = DOUBLE_TYPE;
/*     */       }
/*     */ 
/*  96 */       newInstance(boxed);
/*  97 */       if (type.getSize() == 2)
/*     */       {
/*  99 */         dupX2();
/* 100 */         dupX2();
/* 101 */         pop();
/*     */       }
/*     */       else {
/* 104 */         dupX1();
/* 105 */         swap();
/*     */       }
/*     */ 
/* 108 */       invokeConstructor(boxed, new MethodDescriptor("<init>", Type.VOID_TYPE, new Type[] { type }));
/*     */     }
/*     */   }
/*     */ 
/*     */   public void invokeConstructor(Type type, MethodDescriptor method)
/*     */   {
/* 119 */     invokeInsn(183, type, method);
/*     */   }
/*     */ 
/*     */   public void dupX1()
/*     */   {
/* 126 */     this.mv.visitInsn(90);
/*     */   }
/*     */ 
/*     */   public void dupX2()
/*     */   {
/* 133 */     this.mv.visitInsn(91);
/*     */   }
/*     */ 
/*     */   public void pop()
/*     */   {
/* 140 */     this.mv.visitInsn(87);
/*     */   }
/*     */ 
/*     */   public void swap()
/*     */   {
/* 147 */     this.mv.visitInsn(95);
/*     */   }
/*     */ 
/*     */   public void push(boolean value)
/*     */   {
/* 156 */     push(value ? 1 : 0);
/*     */   }
/*     */ 
/*     */   public void push(int value)
/*     */   {
/* 165 */     if ((value >= -1) && (value <= 5))
/* 166 */       this.mv.visitInsn(3 + value);
/* 167 */     else if ((value >= -128) && (value <= 127))
/* 168 */       this.mv.visitIntInsn(16, value);
/* 169 */     else if ((value >= -32768) && (value <= 32767))
/* 170 */       this.mv.visitIntInsn(17, value);
/*     */     else
/* 172 */       this.mv.visitLdcInsn(new Integer(value));
/*     */   }
/*     */ 
/*     */   public void newInstance(Type type)
/*     */   {
/* 182 */     typeInsn(187, type);
/*     */   }
/*     */ 
/*     */   public void push(String value)
/*     */   {
/* 191 */     if (value == null)
/* 192 */       this.mv.visitInsn(1);
/*     */     else
/* 194 */       this.mv.visitLdcInsn(value);
/*     */   }
/*     */ 
/*     */   public void unbox(Type type)
/*     */   {
/* 206 */     Type t = NUMBER_TYPE;
/* 207 */     MethodDescriptor sig = null;
/*     */ 
/* 209 */     switch (type.getSort()) {
/*     */     case 0:
/* 211 */       return;
/*     */     case 2:
/* 213 */       t = CHARACTER_TYPE;
/* 214 */       sig = CHAR_VALUE;
/* 215 */       break;
/*     */     case 1:
/* 217 */       t = BOOLEAN_TYPE;
/* 218 */       sig = BOOLEAN_VALUE;
/* 219 */       break;
/*     */     case 8:
/* 221 */       sig = DOUBLE_VALUE;
/* 222 */       break;
/*     */     case 6:
/* 224 */       sig = FLOAT_VALUE;
/* 225 */       break;
/*     */     case 7:
/* 227 */       sig = LONG_VALUE;
/* 228 */       break;
/*     */     case 3:
/*     */     case 4:
/*     */     case 5:
/* 232 */       sig = INT_VALUE;
/*     */     }
/*     */ 
/* 235 */     if (sig == null) {
/* 236 */       checkCast(type);
/*     */     } else {
/* 238 */       checkCast(t);
/* 239 */       invokeVirtual(t, sig);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void checkCast(Type type)
/*     */   {
/* 250 */     if (!type.equals(OBJECT_TYPE))
/* 251 */       typeInsn(192, type);
/*     */   }
/*     */ 
/*     */   public void invokeVirtual(Type owner, MethodDescriptor method)
/*     */   {
/* 262 */     invokeInsn(182, owner, method);
/*     */   }
/*     */ 
/*     */   private void invokeInsn(int opcode, Type type, MethodDescriptor method)
/*     */   {
/* 273 */     String owner = type.getSort() == 9 ? type.getDescriptor() : type.getInternalName();
/* 274 */     this.mv.visitMethodInsn(opcode, owner, method.getName(), method.getDescriptor());
/*     */   }
/*     */ 
/*     */   private void typeInsn(int opcode, Type type)
/*     */   {
/*     */     String desc;
/*     */     String desc;
/* 286 */     if (type.getSort() == 9)
/* 287 */       desc = type.getDescriptor();
/*     */     else {
/* 289 */       desc = type.getInternalName();
/*     */     }
/*     */ 
/* 292 */     this.mv.visitTypeInsn(opcode, desc);
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.reflect.compiler.BoxingHelper
 * JD-Core Version:    0.6.2
 */