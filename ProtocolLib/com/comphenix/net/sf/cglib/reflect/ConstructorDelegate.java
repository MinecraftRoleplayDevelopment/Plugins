/*     */ package com.comphenix.net.sf.cglib.reflect;
/*     */ 
/*     */ import com.comphenix.net.sf.cglib.asm.ClassVisitor;
/*     */ import com.comphenix.net.sf.cglib.asm.Type;
/*     */ import com.comphenix.net.sf.cglib.core.AbstractClassGenerator;
/*     */ import com.comphenix.net.sf.cglib.core.AbstractClassGenerator.Source;
/*     */ import com.comphenix.net.sf.cglib.core.ClassEmitter;
/*     */ import com.comphenix.net.sf.cglib.core.CodeEmitter;
/*     */ import com.comphenix.net.sf.cglib.core.EmitUtils;
/*     */ import com.comphenix.net.sf.cglib.core.KeyFactory;
/*     */ import com.comphenix.net.sf.cglib.core.ReflectUtils;
/*     */ import com.comphenix.net.sf.cglib.core.TypeUtils;
/*     */ import java.lang.reflect.Constructor;
/*     */ import java.lang.reflect.Method;
/*     */ 
/*     */ public abstract class ConstructorDelegate
/*     */ {
/*  28 */   private static final ConstructorKey KEY_FACTORY = (ConstructorKey)KeyFactory.create(ConstructorKey.class, KeyFactory.CLASS_BY_NAME);
/*     */ 
/*     */   public static ConstructorDelegate create(Class targetClass, Class iface)
/*     */   {
/*  39 */     Generator gen = new Generator();
/*  40 */     gen.setTargetClass(targetClass);
/*  41 */     gen.setInterface(iface);
/*  42 */     return gen.create();
/*     */   }
/*     */ 
/*  46 */   public static class Generator extends AbstractClassGenerator { private static final AbstractClassGenerator.Source SOURCE = new AbstractClassGenerator.Source(ConstructorDelegate.class.getName());
/*  47 */     private static final Type CONSTRUCTOR_DELEGATE = TypeUtils.parseType("com.comphenix.net.sf.cglib.reflect.ConstructorDelegate");
/*     */     private Class iface;
/*     */     private Class targetClass;
/*     */ 
/*     */     public Generator() {
/*  54 */       super();
/*     */     }
/*     */ 
/*     */     public void setInterface(Class iface) {
/*  58 */       this.iface = iface;
/*     */     }
/*     */ 
/*     */     public void setTargetClass(Class targetClass) {
/*  62 */       this.targetClass = targetClass;
/*     */     }
/*     */ 
/*     */     public ConstructorDelegate create() {
/*  66 */       setNamePrefix(this.targetClass.getName());
/*  67 */       Object key = ConstructorDelegate.KEY_FACTORY.newInstance(this.iface.getName(), this.targetClass.getName());
/*  68 */       return (ConstructorDelegate)super.create(key);
/*     */     }
/*     */ 
/*     */     protected ClassLoader getDefaultClassLoader() {
/*  72 */       return this.targetClass.getClassLoader();
/*     */     }
/*     */ 
/*     */     public void generateClass(ClassVisitor v) {
/*  76 */       setNamePrefix(this.targetClass.getName());
/*     */ 
/*  78 */       Method newInstance = ReflectUtils.findNewInstance(this.iface);
/*  79 */       if (!newInstance.getReturnType().isAssignableFrom(this.targetClass))
/*  80 */         throw new IllegalArgumentException("incompatible return type");
/*     */       Constructor constructor;
/*     */       try
/*     */       {
/*  84 */         constructor = this.targetClass.getDeclaredConstructor(newInstance.getParameterTypes());
/*     */       } catch (NoSuchMethodException e) {
/*  86 */         throw new IllegalArgumentException("interface does not match any known constructor");
/*     */       }
/*     */ 
/*  89 */       ClassEmitter ce = new ClassEmitter(v);
/*  90 */       ce.begin_class(46, 1, getClassName(), CONSTRUCTOR_DELEGATE, new Type[] { Type.getType(this.iface) }, "<generated>");
/*     */ 
/*  96 */       Type declaring = Type.getType(constructor.getDeclaringClass());
/*  97 */       EmitUtils.null_constructor(ce);
/*  98 */       CodeEmitter e = ce.begin_method(1, ReflectUtils.getSignature(newInstance), ReflectUtils.getExceptionTypes(newInstance));
/*     */ 
/* 101 */       e.new_instance(declaring);
/* 102 */       e.dup();
/* 103 */       e.load_args();
/* 104 */       e.invoke_constructor(declaring, ReflectUtils.getSignature(constructor));
/* 105 */       e.return_value();
/* 106 */       e.end_method();
/* 107 */       ce.end_class();
/*     */     }
/*     */ 
/*     */     protected Object firstInstance(Class type) {
/* 111 */       return ReflectUtils.newInstance(type);
/*     */     }
/*     */ 
/*     */     protected Object nextInstance(Object instance) {
/* 115 */       return instance;
/*     */     }
/*     */   }
/*     */ 
/*     */   static abstract interface ConstructorKey
/*     */   {
/*     */     public abstract Object newInstance(String paramString1, String paramString2);
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.net.sf.cglib.reflect.ConstructorDelegate
 * JD-Core Version:    0.6.2
 */