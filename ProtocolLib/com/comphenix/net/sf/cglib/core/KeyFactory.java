/*     */ package com.comphenix.net.sf.cglib.core;
/*     */ 
/*     */ import com.comphenix.net.sf.cglib.asm.ClassVisitor;
/*     */ import com.comphenix.net.sf.cglib.asm.Label;
/*     */ import com.comphenix.net.sf.cglib.asm.Type;
/*     */ import java.lang.reflect.Method;
/*     */ 
/*     */ public abstract class KeyFactory
/*     */ {
/*  54 */   private static final Signature GET_NAME = TypeUtils.parseSignature("String getName()");
/*     */ 
/*  56 */   private static final Signature GET_CLASS = TypeUtils.parseSignature("Class getClass()");
/*     */ 
/*  58 */   private static final Signature HASH_CODE = TypeUtils.parseSignature("int hashCode()");
/*     */ 
/*  60 */   private static final Signature EQUALS = TypeUtils.parseSignature("boolean equals(Object)");
/*     */ 
/*  62 */   private static final Signature TO_STRING = TypeUtils.parseSignature("String toString()");
/*     */ 
/*  64 */   private static final Signature APPEND_STRING = TypeUtils.parseSignature("StringBuffer append(String)");
/*     */ 
/*  66 */   private static final Type KEY_FACTORY = TypeUtils.parseType("com.comphenix.net.sf.cglib.core.KeyFactory");
/*     */ 
/*  70 */   private static final int[] PRIMES = { 11, 73, 179, 331, 521, 787, 1213, 1823, 2609, 3691, 5189, 7247, 10037, 13931, 19289, 26627, 36683, 50441, 69403, 95401, 131129, 180179, 247501, 340057, 467063, 641371, 880603, 1209107, 1660097, 2279161, 3129011, 4295723, 5897291, 8095873, 11114263, 15257791, 20946017, 28754629, 39474179, 54189869, 74391461, 102123817, 140194277, 192456917, 264202273, 362693231, 497900099, 683510293, 938313161, 1288102441, 1768288259 };
/*     */ 
/*  86 */   public static final Customizer CLASS_BY_NAME = new Customizer() {
/*     */     public void customize(CodeEmitter e, Type type) {
/*  88 */       if (type.equals(Constants.TYPE_CLASS))
/*  89 */         e.invoke_virtual(Constants.TYPE_CLASS, KeyFactory.GET_NAME);
/*     */     }
/*  86 */   };
/*     */ 
/*  94 */   public static final Customizer OBJECT_BY_CLASS = new Customizer() {
/*     */     public void customize(CodeEmitter e, Type type) {
/*  96 */       e.invoke_virtual(Constants.TYPE_OBJECT, KeyFactory.GET_CLASS);
/*     */     }
/*  94 */   };
/*     */ 
/*     */   public static KeyFactory create(Class keyInterface)
/*     */   {
/* 104 */     return create(keyInterface, null);
/*     */   }
/*     */ 
/*     */   public static KeyFactory create(Class keyInterface, Customizer customizer) {
/* 108 */     return create(keyInterface.getClassLoader(), keyInterface, customizer);
/*     */   }
/*     */ 
/*     */   public static KeyFactory create(ClassLoader loader, Class keyInterface, Customizer customizer) {
/* 112 */     Generator gen = new Generator();
/* 113 */     gen.setInterface(keyInterface);
/* 114 */     gen.setCustomizer(customizer);
/* 115 */     gen.setClassLoader(loader);
/* 116 */     return gen.create(); } 
/* 120 */   public static class Generator extends AbstractClassGenerator { private static final AbstractClassGenerator.Source SOURCE = new AbstractClassGenerator.Source(KeyFactory.class.getName());
/*     */     private Class keyInterface;
/*     */     private Customizer customizer;
/*     */     private int constant;
/*     */     private int multiplier;
/*     */ 
/* 127 */     public Generator() { super(); }
/*     */ 
/*     */     protected ClassLoader getDefaultClassLoader()
/*     */     {
/* 131 */       return this.keyInterface.getClassLoader();
/*     */     }
/*     */ 
/*     */     public void setCustomizer(Customizer customizer) {
/* 135 */       this.customizer = customizer;
/*     */     }
/*     */ 
/*     */     public void setInterface(Class keyInterface) {
/* 139 */       this.keyInterface = keyInterface;
/*     */     }
/*     */ 
/*     */     public KeyFactory create() {
/* 143 */       setNamePrefix(this.keyInterface.getName());
/* 144 */       return (KeyFactory)super.create(this.keyInterface.getName());
/*     */     }
/*     */ 
/*     */     public void setHashConstant(int constant) {
/* 148 */       this.constant = constant;
/*     */     }
/*     */ 
/*     */     public void setHashMultiplier(int multiplier) {
/* 152 */       this.multiplier = multiplier;
/*     */     }
/*     */ 
/*     */     protected Object firstInstance(Class type) {
/* 156 */       return ReflectUtils.newInstance(type);
/*     */     }
/*     */ 
/*     */     protected Object nextInstance(Object instance) {
/* 160 */       return instance;
/*     */     }
/*     */ 
/*     */     public void generateClass(ClassVisitor v) {
/* 164 */       ClassEmitter ce = new ClassEmitter(v);
/*     */ 
/* 166 */       Method newInstance = ReflectUtils.findNewInstance(this.keyInterface);
/* 167 */       if (!newInstance.getReturnType().equals(Object.class)) {
/* 168 */         throw new IllegalArgumentException("newInstance method must return Object");
/*     */       }
/*     */ 
/* 171 */       Type[] parameterTypes = TypeUtils.getTypes(newInstance.getParameterTypes());
/* 172 */       ce.begin_class(46, 1, getClassName(), KeyFactory.KEY_FACTORY, new Type[] { Type.getType(this.keyInterface) }, "<generated>");
/*     */ 
/* 178 */       EmitUtils.null_constructor(ce);
/* 179 */       EmitUtils.factory_method(ce, ReflectUtils.getSignature(newInstance));
/*     */ 
/* 181 */       int seed = 0;
/* 182 */       CodeEmitter e = ce.begin_method(1, TypeUtils.parseConstructor(parameterTypes), null);
/*     */ 
/* 185 */       e.load_this();
/* 186 */       e.super_invoke_constructor();
/* 187 */       e.load_this();
/* 188 */       for (int i = 0; i < parameterTypes.length; i++) {
/* 189 */         seed += parameterTypes[i].hashCode();
/* 190 */         ce.declare_field(18, getFieldName(i), parameterTypes[i], null);
/*     */ 
/* 194 */         e.dup();
/* 195 */         e.load_arg(i);
/* 196 */         e.putfield(getFieldName(i));
/*     */       }
/* 198 */       e.return_value();
/* 199 */       e.end_method();
/*     */ 
/* 202 */       e = ce.begin_method(1, KeyFactory.HASH_CODE, null);
/* 203 */       int hc = this.constant != 0 ? this.constant : KeyFactory.PRIMES[(java.lang.Math.abs(seed) % KeyFactory.PRIMES.length)];
/* 204 */       int hm = this.multiplier != 0 ? this.multiplier : KeyFactory.PRIMES[(java.lang.Math.abs(seed * 13) % KeyFactory.PRIMES.length)];
/* 205 */       e.push(hc);
/* 206 */       for (int i = 0; i < parameterTypes.length; i++) {
/* 207 */         e.load_this();
/* 208 */         e.getfield(getFieldName(i));
/* 209 */         EmitUtils.hash_code(e, parameterTypes[i], hm, this.customizer);
/*     */       }
/* 211 */       e.return_value();
/* 212 */       e.end_method();
/*     */ 
/* 215 */       e = ce.begin_method(1, KeyFactory.EQUALS, null);
/* 216 */       Label fail = e.make_label();
/* 217 */       e.load_arg(0);
/* 218 */       e.instance_of_this();
/* 219 */       e.if_jump(153, fail);
/* 220 */       for (int i = 0; i < parameterTypes.length; i++) {
/* 221 */         e.load_this();
/* 222 */         e.getfield(getFieldName(i));
/* 223 */         e.load_arg(0);
/* 224 */         e.checkcast_this();
/* 225 */         e.getfield(getFieldName(i));
/* 226 */         EmitUtils.not_equals(e, parameterTypes[i], fail, this.customizer);
/*     */       }
/* 228 */       e.push(1);
/* 229 */       e.return_value();
/* 230 */       e.mark(fail);
/* 231 */       e.push(0);
/* 232 */       e.return_value();
/* 233 */       e.end_method();
/*     */ 
/* 236 */       e = ce.begin_method(1, KeyFactory.TO_STRING, null);
/* 237 */       e.new_instance(Constants.TYPE_STRING_BUFFER);
/* 238 */       e.dup();
/* 239 */       e.invoke_constructor(Constants.TYPE_STRING_BUFFER);
/* 240 */       for (int i = 0; i < parameterTypes.length; i++) {
/* 241 */         if (i > 0) {
/* 242 */           e.push(", ");
/* 243 */           e.invoke_virtual(Constants.TYPE_STRING_BUFFER, KeyFactory.APPEND_STRING);
/*     */         }
/* 245 */         e.load_this();
/* 246 */         e.getfield(getFieldName(i));
/* 247 */         EmitUtils.append_string(e, parameterTypes[i], EmitUtils.DEFAULT_DELIMITERS, this.customizer);
/*     */       }
/* 249 */       e.invoke_virtual(Constants.TYPE_STRING_BUFFER, KeyFactory.TO_STRING);
/* 250 */       e.return_value();
/* 251 */       e.end_method();
/*     */ 
/* 253 */       ce.end_class();
/*     */     }
/*     */ 
/*     */     private String getFieldName(int arg) {
/* 257 */       return "FIELD_" + arg;
/*     */     }
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.net.sf.cglib.core.KeyFactory
 * JD-Core Version:    0.6.2
 */