/*     */ package com.comphenix.net.sf.cglib.proxy;
/*     */ 
/*     */ import com.comphenix.net.sf.cglib.asm.ClassVisitor;
/*     */ import com.comphenix.net.sf.cglib.asm.Type;
/*     */ import com.comphenix.net.sf.cglib.core.AbstractClassGenerator;
/*     */ import com.comphenix.net.sf.cglib.core.AbstractClassGenerator.Source;
/*     */ import com.comphenix.net.sf.cglib.core.ClassEmitter;
/*     */ import com.comphenix.net.sf.cglib.core.CodeEmitter;
/*     */ import com.comphenix.net.sf.cglib.core.ReflectUtils;
/*     */ import com.comphenix.net.sf.cglib.core.Signature;
/*     */ import java.lang.reflect.Method;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ 
/*     */ public class InterfaceMaker extends AbstractClassGenerator
/*     */ {
/*  34 */   private static final AbstractClassGenerator.Source SOURCE = new AbstractClassGenerator.Source(InterfaceMaker.class.getName());
/*  35 */   private Map signatures = new HashMap();
/*     */ 
/*     */   public InterfaceMaker()
/*     */   {
/*  43 */     super(SOURCE);
/*     */   }
/*     */ 
/*     */   public void add(Signature sig, Type[] exceptions)
/*     */   {
/*  52 */     this.signatures.put(sig, exceptions);
/*     */   }
/*     */ 
/*     */   public void add(Method method)
/*     */   {
/*  61 */     add(ReflectUtils.getSignature(method), ReflectUtils.getExceptionTypes(method));
/*     */   }
/*     */ 
/*     */   public void add(Class clazz)
/*     */   {
/*  72 */     Method[] methods = clazz.getMethods();
/*  73 */     for (int i = 0; i < methods.length; i++) {
/*  74 */       Method m = methods[i];
/*  75 */       if (!m.getDeclaringClass().getName().equals("java.lang.Object"))
/*  76 */         add(m);
/*     */     }
/*     */   }
/*     */ 
/*     */   public Class create()
/*     */   {
/*  85 */     setUseCache(false);
/*  86 */     return (Class)super.create(this);
/*     */   }
/*     */ 
/*     */   protected ClassLoader getDefaultClassLoader() {
/*  90 */     return null;
/*     */   }
/*     */ 
/*     */   protected Object firstInstance(Class type) {
/*  94 */     return type;
/*     */   }
/*     */ 
/*     */   protected Object nextInstance(Object instance) {
/*  98 */     throw new IllegalStateException("InterfaceMaker does not cache");
/*     */   }
/*     */ 
/*     */   public void generateClass(ClassVisitor v) throws Exception {
/* 102 */     ClassEmitter ce = new ClassEmitter(v);
/* 103 */     ce.begin_class(46, 513, getClassName(), null, null, "<generated>");
/*     */ 
/* 109 */     for (Iterator it = this.signatures.keySet().iterator(); it.hasNext(); ) {
/* 110 */       Signature sig = (Signature)it.next();
/* 111 */       Type[] exceptions = (Type[])this.signatures.get(sig);
/* 112 */       ce.begin_method(1025, sig, exceptions).end_method();
/*     */     }
/*     */ 
/* 116 */     ce.end_class();
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.net.sf.cglib.proxy.InterfaceMaker
 * JD-Core Version:    0.6.2
 */