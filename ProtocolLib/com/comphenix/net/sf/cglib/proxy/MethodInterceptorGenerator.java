/*     */ package com.comphenix.net.sf.cglib.proxy;
/*     */ 
/*     */ import com.comphenix.net.sf.cglib.asm.Label;
/*     */ import com.comphenix.net.sf.cglib.asm.Type;
/*     */ import com.comphenix.net.sf.cglib.core.ClassEmitter;
/*     */ import com.comphenix.net.sf.cglib.core.ClassInfo;
/*     */ import com.comphenix.net.sf.cglib.core.CodeEmitter;
/*     */ import com.comphenix.net.sf.cglib.core.CollectionUtils;
/*     */ import com.comphenix.net.sf.cglib.core.Constants;
/*     */ import com.comphenix.net.sf.cglib.core.EmitUtils;
/*     */ import com.comphenix.net.sf.cglib.core.Local;
/*     */ import com.comphenix.net.sf.cglib.core.MethodInfo;
/*     */ import com.comphenix.net.sf.cglib.core.ObjectSwitchCallback;
/*     */ import com.comphenix.net.sf.cglib.core.Signature;
/*     */ import com.comphenix.net.sf.cglib.core.Transformer;
/*     */ import com.comphenix.net.sf.cglib.core.TypeUtils;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ 
/*     */ class MethodInterceptorGenerator
/*     */   implements CallbackGenerator
/*     */ {
/*  27 */   public static final MethodInterceptorGenerator INSTANCE = new MethodInterceptorGenerator();
/*     */   static final String EMPTY_ARGS_NAME = "CGLIB$emptyArgs";
/*     */   static final String FIND_PROXY_NAME = "CGLIB$findMethodProxy";
/*  31 */   static final Class[] FIND_PROXY_TYPES = { Signature.class };
/*     */ 
/*  33 */   private static final Type ABSTRACT_METHOD_ERROR = TypeUtils.parseType("AbstractMethodError");
/*     */ 
/*  35 */   private static final Type METHOD = TypeUtils.parseType("java.lang.reflect.Method");
/*     */ 
/*  37 */   private static final Type REFLECT_UTILS = TypeUtils.parseType("com.comphenix.net.sf.cglib.core.ReflectUtils");
/*     */ 
/*  39 */   private static final Type METHOD_PROXY = TypeUtils.parseType("com.comphenix.net.sf.cglib.proxy.MethodProxy");
/*     */ 
/*  41 */   private static final Type METHOD_INTERCEPTOR = TypeUtils.parseType("com.comphenix.net.sf.cglib.proxy.MethodInterceptor");
/*     */ 
/*  43 */   private static final Signature GET_DECLARED_METHODS = TypeUtils.parseSignature("java.lang.reflect.Method[] getDeclaredMethods()");
/*     */ 
/*  45 */   private static final Signature GET_DECLARING_CLASS = TypeUtils.parseSignature("Class getDeclaringClass()");
/*     */ 
/*  47 */   private static final Signature FIND_METHODS = TypeUtils.parseSignature("java.lang.reflect.Method[] findMethods(String[], java.lang.reflect.Method[])");
/*     */ 
/*  49 */   private static final Signature MAKE_PROXY = new Signature("create", METHOD_PROXY, new Type[] { Constants.TYPE_CLASS, Constants.TYPE_CLASS, Constants.TYPE_STRING, Constants.TYPE_STRING, Constants.TYPE_STRING });
/*     */ 
/*  57 */   private static final Signature INTERCEPT = new Signature("intercept", Constants.TYPE_OBJECT, new Type[] { Constants.TYPE_OBJECT, METHOD, Constants.TYPE_OBJECT_ARRAY, METHOD_PROXY });
/*     */ 
/*  64 */   private static final Signature FIND_PROXY = new Signature("CGLIB$findMethodProxy", METHOD_PROXY, new Type[] { Constants.TYPE_SIGNATURE });
/*     */ 
/*  66 */   private static final Signature TO_STRING = TypeUtils.parseSignature("String toString()");
/*     */ 
/*  68 */   private static final Transformer METHOD_TO_CLASS = new Transformer() {
/*     */     public Object transform(Object value) {
/*  70 */       return ((MethodInfo)value).getClassInfo();
/*     */     }
/*  68 */   };
/*     */ 
/*  73 */   private static final Signature CSTRUCT_SIGNATURE = TypeUtils.parseConstructor("String, String");
/*     */ 
/*     */   private String getMethodField(Signature impl)
/*     */   {
/*  77 */     return impl.getName() + "$Method";
/*     */   }
/*     */   private String getMethodProxyField(Signature impl) {
/*  80 */     return impl.getName() + "$Proxy";
/*     */   }
/*     */ 
/*     */   public void generate(ClassEmitter ce, CallbackGenerator.Context context, List methods) {
/*  84 */     Map sigMap = new HashMap();
/*  85 */     for (Iterator it = methods.iterator(); it.hasNext(); ) {
/*  86 */       MethodInfo method = (MethodInfo)it.next();
/*  87 */       Signature sig = method.getSignature();
/*  88 */       Signature impl = context.getImplSignature(method);
/*     */ 
/*  90 */       String methodField = getMethodField(impl);
/*  91 */       String methodProxyField = getMethodProxyField(impl);
/*     */ 
/*  93 */       sigMap.put(sig.toString(), methodProxyField);
/*  94 */       ce.declare_field(26, methodField, METHOD, null);
/*  95 */       ce.declare_field(26, methodProxyField, METHOD_PROXY, null);
/*  96 */       ce.declare_field(26, "CGLIB$emptyArgs", Constants.TYPE_OBJECT_ARRAY, null);
/*     */ 
/* 100 */       CodeEmitter e = ce.begin_method(16, impl, method.getExceptionTypes());
/*     */ 
/* 103 */       superHelper(e, method, context);
/* 104 */       e.return_value();
/* 105 */       e.end_method();
/*     */ 
/* 108 */       e = context.beginMethod(ce, method);
/* 109 */       Label nullInterceptor = e.make_label();
/* 110 */       context.emitCallback(e, context.getIndex(method));
/* 111 */       e.dup();
/* 112 */       e.ifnull(nullInterceptor);
/*     */ 
/* 114 */       e.load_this();
/* 115 */       e.getfield(methodField);
/*     */ 
/* 117 */       if (sig.getArgumentTypes().length == 0)
/* 118 */         e.getfield("CGLIB$emptyArgs");
/*     */       else {
/* 120 */         e.create_arg_array();
/*     */       }
/*     */ 
/* 123 */       e.getfield(methodProxyField);
/* 124 */       e.invoke_interface(METHOD_INTERCEPTOR, INTERCEPT);
/* 125 */       e.unbox_or_zero(sig.getReturnType());
/* 126 */       e.return_value();
/*     */ 
/* 128 */       e.mark(nullInterceptor);
/* 129 */       superHelper(e, method, context);
/* 130 */       e.return_value();
/* 131 */       e.end_method();
/*     */     }
/* 133 */     generateFindProxy(ce, sigMap);
/*     */   }
/*     */ 
/*     */   private static void superHelper(CodeEmitter e, MethodInfo method, CallbackGenerator.Context context)
/*     */   {
/* 138 */     if (TypeUtils.isAbstract(method.getModifiers())) {
/* 139 */       e.throw_exception(ABSTRACT_METHOD_ERROR, method.toString() + " is abstract");
/*     */     } else {
/* 141 */       e.load_this();
/* 142 */       e.load_args();
/* 143 */       context.emitInvoke(e, method);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void generateStatic(CodeEmitter e, CallbackGenerator.Context context, List methods)
/*     */     throws Exception
/*     */   {
/* 161 */     e.push(0);
/* 162 */     e.newarray();
/* 163 */     e.putfield("CGLIB$emptyArgs");
/*     */ 
/* 165 */     Local thisclass = e.make_local();
/* 166 */     Local declaringclass = e.make_local();
/* 167 */     EmitUtils.load_class_this(e);
/* 168 */     e.store_local(thisclass);
/*     */ 
/* 170 */     Map methodsByClass = CollectionUtils.bucket(methods, METHOD_TO_CLASS);
/* 171 */     for (Iterator i = methodsByClass.keySet().iterator(); i.hasNext(); ) {
/* 172 */       ClassInfo classInfo = (ClassInfo)i.next();
/*     */ 
/* 174 */       List classMethods = (List)methodsByClass.get(classInfo);
/* 175 */       e.push(2 * classMethods.size());
/* 176 */       e.newarray(Constants.TYPE_STRING);
/* 177 */       for (int index = 0; index < classMethods.size(); index++) {
/* 178 */         MethodInfo method = (MethodInfo)classMethods.get(index);
/* 179 */         Signature sig = method.getSignature();
/* 180 */         e.dup();
/* 181 */         e.push(2 * index);
/* 182 */         e.push(sig.getName());
/* 183 */         e.aastore();
/* 184 */         e.dup();
/* 185 */         e.push(2 * index + 1);
/* 186 */         e.push(sig.getDescriptor());
/* 187 */         e.aastore();
/*     */       }
/*     */ 
/* 190 */       EmitUtils.load_class(e, classInfo.getType());
/* 191 */       e.dup();
/* 192 */       e.store_local(declaringclass);
/* 193 */       e.invoke_virtual(Constants.TYPE_CLASS, GET_DECLARED_METHODS);
/* 194 */       e.invoke_static(REFLECT_UTILS, FIND_METHODS);
/*     */ 
/* 196 */       for (int index = 0; index < classMethods.size(); index++) {
/* 197 */         MethodInfo method = (MethodInfo)classMethods.get(index);
/* 198 */         Signature sig = method.getSignature();
/* 199 */         Signature impl = context.getImplSignature(method);
/* 200 */         e.dup();
/* 201 */         e.push(index);
/* 202 */         e.array_load(METHOD);
/* 203 */         e.putfield(getMethodField(impl));
/*     */ 
/* 205 */         e.load_local(declaringclass);
/* 206 */         e.load_local(thisclass);
/* 207 */         e.push(sig.getDescriptor());
/* 208 */         e.push(sig.getName());
/* 209 */         e.push(impl.getName());
/* 210 */         e.invoke_static(METHOD_PROXY, MAKE_PROXY);
/* 211 */         e.putfield(getMethodProxyField(impl));
/*     */       }
/* 213 */       e.pop();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void generateFindProxy(ClassEmitter ce, Map sigMap) {
/* 218 */     CodeEmitter e = ce.begin_method(9, FIND_PROXY, null);
/*     */ 
/* 221 */     e.load_arg(0);
/* 222 */     e.invoke_virtual(Constants.TYPE_OBJECT, TO_STRING);
/* 223 */     ObjectSwitchCallback callback = new ObjectSwitchCallback() { private final CodeEmitter val$e;
/*     */       private final Map val$sigMap;
/*     */ 
/* 225 */       public void processCase(Object key, Label end) { this.val$e.getfield((String)this.val$sigMap.get(key));
/* 226 */         this.val$e.return_value(); }
/*     */ 
/*     */       public void processDefault() {
/* 229 */         this.val$e.aconst_null();
/* 230 */         this.val$e.return_value();
/*     */       }
/*     */     };
/* 233 */     EmitUtils.string_switch(e, (String[])sigMap.keySet().toArray(new String[0]), 1, callback);
/*     */ 
/* 237 */     e.end_method();
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.net.sf.cglib.proxy.MethodInterceptorGenerator
 * JD-Core Version:    0.6.2
 */