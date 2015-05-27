/*     */ package com.comphenix.net.sf.cglib.transform.impl;
/*     */ 
/*     */ import com.comphenix.net.sf.cglib.asm.Type;
/*     */ import com.comphenix.net.sf.cglib.core.CodeEmitter;
/*     */ import com.comphenix.net.sf.cglib.core.CodeGenerationException;
/*     */ import com.comphenix.net.sf.cglib.core.ReflectUtils;
/*     */ import com.comphenix.net.sf.cglib.core.Signature;
/*     */ import com.comphenix.net.sf.cglib.core.TypeUtils;
/*     */ import com.comphenix.net.sf.cglib.transform.ClassEmitterTransformer;
/*     */ import java.lang.reflect.Method;
/*     */ import java.lang.reflect.Modifier;
/*     */ 
/*     */ public class AddDelegateTransformer extends ClassEmitterTransformer
/*     */ {
/*     */   private static final String DELEGATE = "$CGLIB_DELEGATE";
/*  30 */   private static final Signature CSTRUCT_OBJECT = TypeUtils.parseSignature("void <init>(Object)");
/*     */   private Class[] delegateIf;
/*     */   private Class delegateImpl;
/*     */   private Type delegateType;
/*     */ 
/*     */   public AddDelegateTransformer(Class[] delegateIf, Class delegateImpl)
/*     */   {
/*     */     try
/*     */     {
/*  40 */       delegateImpl.getConstructor(new Class[] { Object.class });
/*  41 */       this.delegateIf = delegateIf;
/*  42 */       this.delegateImpl = delegateImpl;
/*  43 */       this.delegateType = Type.getType(delegateImpl);
/*     */     } catch (NoSuchMethodException e) {
/*  45 */       throw new CodeGenerationException(e);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void begin_class(int version, int access, String className, Type superType, Type[] interfaces, String sourceFile)
/*     */   {
/*  51 */     if (!TypeUtils.isInterface(access))
/*     */     {
/*  53 */       Type[] all = TypeUtils.add(interfaces, TypeUtils.getTypes(this.delegateIf));
/*  54 */       super.begin_class(version, access, className, superType, all, sourceFile);
/*     */ 
/*  56 */       declare_field(130, "$CGLIB_DELEGATE", this.delegateType, null);
/*     */ 
/*  60 */       for (int i = 0; i < this.delegateIf.length; i++) {
/*  61 */         Method[] methods = this.delegateIf[i].getMethods();
/*  62 */         for (int j = 0; j < methods.length; j++)
/*  63 */           if (Modifier.isAbstract(methods[j].getModifiers()))
/*  64 */             addDelegate(methods[j]);
/*     */       }
/*     */     }
/*     */     else
/*     */     {
/*  69 */       super.begin_class(version, access, className, superType, interfaces, sourceFile);
/*     */     }
/*     */   }
/*     */ 
/*     */   public CodeEmitter begin_method(int access, Signature sig, Type[] exceptions) {
/*  74 */     CodeEmitter e = super.begin_method(access, sig, exceptions);
/*  75 */     if (sig.getName().equals("<init>")) {
/*  76 */       return new CodeEmitter(e) {
/*  77 */         private boolean transformInit = true;
/*     */ 
/*  79 */         public void visitMethodInsn(int opcode, String owner, String name, String desc) { super.visitMethodInsn(opcode, owner, name, desc);
/*  80 */           if ((this.transformInit) && (opcode == 183)) {
/*  81 */             load_this();
/*  82 */             new_instance(AddDelegateTransformer.this.delegateType);
/*  83 */             dup();
/*  84 */             load_this();
/*  85 */             invoke_constructor(AddDelegateTransformer.this.delegateType, AddDelegateTransformer.CSTRUCT_OBJECT);
/*  86 */             putfield("$CGLIB_DELEGATE");
/*  87 */             this.transformInit = false;
/*     */           }
/*     */         }
/*     */       };
/*     */     }
/*  92 */     return e;
/*     */   }
/*     */ 
/*     */   private void addDelegate(Method m)
/*     */   {
/*     */     try {
/*  98 */       Method delegate = this.delegateImpl.getMethod(m.getName(), m.getParameterTypes());
/*  99 */       if (!delegate.getReturnType().getName().equals(m.getReturnType().getName()))
/* 100 */         throw new IllegalArgumentException("Invalid delegate signature " + delegate);
/*     */     }
/*     */     catch (NoSuchMethodException e) {
/* 103 */       throw new CodeGenerationException(e);
/*     */     }
/*     */ 
/* 106 */     Signature sig = ReflectUtils.getSignature(m);
/* 107 */     Type[] exceptions = TypeUtils.getTypes(m.getExceptionTypes());
/* 108 */     CodeEmitter e = super.begin_method(1, sig, exceptions);
/* 109 */     e.load_this();
/* 110 */     e.getfield("$CGLIB_DELEGATE");
/* 111 */     e.load_args();
/* 112 */     e.invoke_virtual(this.delegateType, sig);
/* 113 */     e.return_value();
/* 114 */     e.end_method();
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.net.sf.cglib.transform.impl.AddDelegateTransformer
 * JD-Core Version:    0.6.2
 */