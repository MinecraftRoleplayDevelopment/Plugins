/*     */ package com.comphenix.net.sf.cglib.transform.impl;
/*     */ 
/*     */ import com.comphenix.net.sf.cglib.asm.Label;
/*     */ import com.comphenix.net.sf.cglib.asm.Type;
/*     */ import com.comphenix.net.sf.cglib.core.CodeEmitter;
/*     */ import com.comphenix.net.sf.cglib.core.Constants;
/*     */ import com.comphenix.net.sf.cglib.core.Local;
/*     */ import com.comphenix.net.sf.cglib.core.Signature;
/*     */ import com.comphenix.net.sf.cglib.core.TypeUtils;
/*     */ import com.comphenix.net.sf.cglib.transform.ClassEmitterTransformer;
/*     */ 
/*     */ public class InterceptFieldTransformer extends ClassEmitterTransformer
/*     */ {
/*     */   private static final String CALLBACK_FIELD = "$CGLIB_READ_WRITE_CALLBACK";
/*  32 */   private static final Type CALLBACK = TypeUtils.parseType("com.comphenix.net.sf.cglib.transform.impl.InterceptFieldCallback");
/*     */ 
/*  34 */   private static final Type ENABLED = TypeUtils.parseType("com.comphenix.net.sf.cglib.transform.impl.InterceptFieldEnabled");
/*     */ 
/*  36 */   private static final Signature ENABLED_SET = new Signature("setInterceptFieldCallback", Type.VOID_TYPE, new Type[] { CALLBACK });
/*     */ 
/*  38 */   private static final Signature ENABLED_GET = new Signature("getInterceptFieldCallback", CALLBACK, new Type[0]);
/*     */   private InterceptFieldFilter filter;
/*     */ 
/*     */   public InterceptFieldTransformer(InterceptFieldFilter filter)
/*     */   {
/*  44 */     this.filter = filter;
/*     */   }
/*     */ 
/*     */   public void begin_class(int version, int access, String className, Type superType, Type[] interfaces, String sourceFile) {
/*  48 */     if (!TypeUtils.isInterface(access)) {
/*  49 */       super.begin_class(version, access, className, superType, TypeUtils.add(interfaces, ENABLED), sourceFile);
/*     */ 
/*  51 */       super.declare_field(130, "$CGLIB_READ_WRITE_CALLBACK", CALLBACK, null);
/*     */ 
/*  57 */       CodeEmitter e = super.begin_method(1, ENABLED_GET, null);
/*  58 */       e.load_this();
/*  59 */       e.getfield("$CGLIB_READ_WRITE_CALLBACK");
/*  60 */       e.return_value();
/*  61 */       e.end_method();
/*     */ 
/*  63 */       e = super.begin_method(1, ENABLED_SET, null);
/*  64 */       e.load_this();
/*  65 */       e.load_arg(0);
/*  66 */       e.putfield("$CGLIB_READ_WRITE_CALLBACK");
/*  67 */       e.return_value();
/*  68 */       e.end_method();
/*     */     } else {
/*  70 */       super.begin_class(version, access, className, superType, interfaces, sourceFile);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void declare_field(int access, String name, Type type, Object value) {
/*  75 */     super.declare_field(access, name, type, value);
/*  76 */     if (!TypeUtils.isStatic(access)) {
/*  77 */       if (this.filter.acceptRead(getClassType(), name)) {
/*  78 */         addReadMethod(name, type);
/*     */       }
/*  80 */       if (this.filter.acceptWrite(getClassType(), name))
/*  81 */         addWriteMethod(name, type);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void addReadMethod(String name, Type type)
/*     */   {
/*  87 */     CodeEmitter e = super.begin_method(1, readMethodSig(name, type.getDescriptor()), null);
/*     */ 
/*  90 */     e.load_this();
/*  91 */     e.getfield(name);
/*  92 */     e.load_this();
/*  93 */     e.invoke_interface(ENABLED, ENABLED_GET);
/*  94 */     Label intercept = e.make_label();
/*  95 */     e.ifnonnull(intercept);
/*  96 */     e.return_value();
/*     */ 
/*  98 */     e.mark(intercept);
/*  99 */     Local result = e.make_local(type);
/* 100 */     e.store_local(result);
/* 101 */     e.load_this();
/* 102 */     e.invoke_interface(ENABLED, ENABLED_GET);
/* 103 */     e.load_this();
/* 104 */     e.push(name);
/* 105 */     e.load_local(result);
/* 106 */     e.invoke_interface(CALLBACK, readCallbackSig(type));
/* 107 */     if (!TypeUtils.isPrimitive(type)) {
/* 108 */       e.checkcast(type);
/*     */     }
/* 110 */     e.return_value();
/* 111 */     e.end_method();
/*     */   }
/*     */ 
/*     */   private void addWriteMethod(String name, Type type) {
/* 115 */     CodeEmitter e = super.begin_method(1, writeMethodSig(name, type.getDescriptor()), null);
/*     */ 
/* 118 */     e.load_this();
/* 119 */     e.dup();
/* 120 */     e.invoke_interface(ENABLED, ENABLED_GET);
/* 121 */     Label skip = e.make_label();
/* 122 */     e.ifnull(skip);
/*     */ 
/* 124 */     e.load_this();
/* 125 */     e.invoke_interface(ENABLED, ENABLED_GET);
/* 126 */     e.load_this();
/* 127 */     e.push(name);
/* 128 */     e.load_this();
/* 129 */     e.getfield(name);
/* 130 */     e.load_arg(0);
/* 131 */     e.invoke_interface(CALLBACK, writeCallbackSig(type));
/* 132 */     if (!TypeUtils.isPrimitive(type)) {
/* 133 */       e.checkcast(type);
/*     */     }
/* 135 */     Label go = e.make_label();
/* 136 */     e.goTo(go);
/* 137 */     e.mark(skip);
/* 138 */     e.load_arg(0);
/* 139 */     e.mark(go);
/* 140 */     e.putfield(name);
/* 141 */     e.return_value();
/* 142 */     e.end_method();
/*     */   }
/*     */ 
/*     */   public CodeEmitter begin_method(int access, Signature sig, Type[] exceptions) {
/* 146 */     return new CodeEmitter(super.begin_method(access, sig, exceptions)) {
/*     */       public void visitFieldInsn(int opcode, String owner, String name, String desc) {
/* 148 */         Type towner = TypeUtils.fromInternalName(owner);
/* 149 */         switch (opcode) {
/*     */         case 180:
/* 151 */           if (InterceptFieldTransformer.this.filter.acceptRead(towner, name)) { helper(towner, InterceptFieldTransformer.readMethodSig(name, desc));
/*     */             return;
/*     */           }
/*     */           break;
/*     */         case 181:
/* 157 */           if (InterceptFieldTransformer.this.filter.acceptWrite(towner, name)) { helper(towner, InterceptFieldTransformer.writeMethodSig(name, desc));
/*     */             return;
/*     */           }
/*     */           break;
/*     */         }
/* 163 */         super.visitFieldInsn(opcode, owner, name, desc);
/*     */       }
/*     */ 
/*     */       private void helper(Type owner, Signature sig) {
/* 167 */         invoke_virtual(owner, sig);
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   private static Signature readMethodSig(String name, String desc) {
/* 173 */     return new Signature("$cglib_read_" + name, "()" + desc);
/*     */   }
/*     */ 
/*     */   private static Signature writeMethodSig(String name, String desc) {
/* 177 */     return new Signature("$cglib_write_" + name, "(" + desc + ")V");
/*     */   }
/*     */ 
/*     */   private static Signature readCallbackSig(Type type) {
/* 181 */     Type remap = remap(type);
/* 182 */     return new Signature("read" + callbackName(remap), remap, new Type[] { Constants.TYPE_OBJECT, Constants.TYPE_STRING, remap });
/*     */   }
/*     */ 
/*     */   private static Signature writeCallbackSig(Type type)
/*     */   {
/* 190 */     Type remap = remap(type);
/* 191 */     return new Signature("write" + callbackName(remap), remap, new Type[] { Constants.TYPE_OBJECT, Constants.TYPE_STRING, remap, remap });
/*     */   }
/*     */ 
/*     */   private static Type remap(Type type)
/*     */   {
/* 200 */     switch (type.getSort()) {
/*     */     case 9:
/*     */     case 10:
/* 203 */       return Constants.TYPE_OBJECT;
/*     */     }
/* 205 */     return type;
/*     */   }
/*     */ 
/*     */   private static String callbackName(Type type)
/*     */   {
/* 210 */     return type == Constants.TYPE_OBJECT ? "Object" : TypeUtils.upperFirst(TypeUtils.getClassName(type));
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.net.sf.cglib.transform.impl.InterceptFieldTransformer
 * JD-Core Version:    0.6.2
 */