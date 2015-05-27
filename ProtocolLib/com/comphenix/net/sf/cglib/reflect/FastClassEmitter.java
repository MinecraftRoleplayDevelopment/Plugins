/*     */ package com.comphenix.net.sf.cglib.reflect;
/*     */ 
/*     */ import com.comphenix.net.sf.cglib.asm.ClassVisitor;
/*     */ import com.comphenix.net.sf.cglib.asm.Label;
/*     */ import com.comphenix.net.sf.cglib.asm.Type;
/*     */ import com.comphenix.net.sf.cglib.core.Block;
/*     */ import com.comphenix.net.sf.cglib.core.ClassEmitter;
/*     */ import com.comphenix.net.sf.cglib.core.CodeEmitter;
/*     */ import com.comphenix.net.sf.cglib.core.CollectionUtils;
/*     */ import com.comphenix.net.sf.cglib.core.Constants;
/*     */ import com.comphenix.net.sf.cglib.core.DuplicatesPredicate;
/*     */ import com.comphenix.net.sf.cglib.core.EmitUtils;
/*     */ import com.comphenix.net.sf.cglib.core.MethodInfo;
/*     */ import com.comphenix.net.sf.cglib.core.MethodInfoTransformer;
/*     */ import com.comphenix.net.sf.cglib.core.ObjectSwitchCallback;
/*     */ import com.comphenix.net.sf.cglib.core.ProcessSwitchCallback;
/*     */ import com.comphenix.net.sf.cglib.core.ReflectUtils;
/*     */ import com.comphenix.net.sf.cglib.core.Signature;
/*     */ import com.comphenix.net.sf.cglib.core.Transformer;
/*     */ import com.comphenix.net.sf.cglib.core.TypeUtils;
/*     */ import com.comphenix.net.sf.cglib.core.VisibilityPredicate;
/*     */ import java.lang.reflect.Method;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ 
/*     */ class FastClassEmitter extends ClassEmitter
/*     */ {
/*  26 */   private static final Signature CSTRUCT_CLASS = TypeUtils.parseConstructor("Class");
/*     */ 
/*  28 */   private static final Signature METHOD_GET_INDEX = TypeUtils.parseSignature("int getIndex(String, Class[])");
/*     */ 
/*  30 */   private static final Signature SIGNATURE_GET_INDEX = new Signature("getIndex", Type.INT_TYPE, new Type[] { Constants.TYPE_SIGNATURE });
/*     */ 
/*  32 */   private static final Signature TO_STRING = TypeUtils.parseSignature("String toString()");
/*     */ 
/*  34 */   private static final Signature CONSTRUCTOR_GET_INDEX = TypeUtils.parseSignature("int getIndex(Class[])");
/*     */ 
/*  36 */   private static final Signature INVOKE = TypeUtils.parseSignature("Object invoke(int, Object, Object[])");
/*     */ 
/*  38 */   private static final Signature NEW_INSTANCE = TypeUtils.parseSignature("Object newInstance(int, Object[])");
/*     */ 
/*  40 */   private static final Signature GET_MAX_INDEX = TypeUtils.parseSignature("int getMaxIndex()");
/*     */ 
/*  42 */   private static final Signature GET_SIGNATURE_WITHOUT_RETURN_TYPE = TypeUtils.parseSignature("String getSignatureWithoutReturnType(String, Class[])");
/*     */ 
/*  44 */   private static final Type FAST_CLASS = TypeUtils.parseType("com.comphenix.net.sf.cglib.reflect.FastClass");
/*     */ 
/*  46 */   private static final Type ILLEGAL_ARGUMENT_EXCEPTION = TypeUtils.parseType("IllegalArgumentException");
/*     */ 
/*  48 */   private static final Type INVOCATION_TARGET_EXCEPTION = TypeUtils.parseType("java.lang.reflect.InvocationTargetException");
/*     */ 
/*  50 */   private static final Type[] INVOCATION_TARGET_EXCEPTION_ARRAY = { INVOCATION_TARGET_EXCEPTION };
/*     */   private static final int TOO_MANY_METHODS = 100;
/*     */ 
/*     */   public FastClassEmitter(ClassVisitor v, String className, Class type)
/*     */   {
/*  53 */     super(v);
/*     */ 
/*  55 */     Type base = Type.getType(type);
/*  56 */     begin_class(46, 1, className, FAST_CLASS, null, "<generated>");
/*     */ 
/*  59 */     CodeEmitter e = begin_method(1, CSTRUCT_CLASS, null);
/*  60 */     e.load_this();
/*  61 */     e.load_args();
/*  62 */     e.super_invoke_constructor(CSTRUCT_CLASS);
/*  63 */     e.return_value();
/*  64 */     e.end_method();
/*     */ 
/*  66 */     VisibilityPredicate vp = new VisibilityPredicate(type, false);
/*  67 */     List methods = ReflectUtils.addAllMethods(type, new ArrayList());
/*  68 */     CollectionUtils.filter(methods, vp);
/*  69 */     CollectionUtils.filter(methods, new DuplicatesPredicate());
/*  70 */     List constructors = new ArrayList(Arrays.asList(type.getDeclaredConstructors()));
/*  71 */     CollectionUtils.filter(constructors, vp);
/*     */ 
/*  74 */     emitIndexBySignature(methods);
/*     */ 
/*  77 */     emitIndexByClassArray(methods);
/*     */ 
/*  80 */     e = begin_method(1, CONSTRUCTOR_GET_INDEX, null);
/*  81 */     e.load_args();
/*  82 */     List info = CollectionUtils.transform(constructors, MethodInfoTransformer.getInstance());
/*  83 */     EmitUtils.constructor_switch(e, info, new GetIndexCallback(e, info));
/*  84 */     e.end_method();
/*     */ 
/*  87 */     e = begin_method(1, INVOKE, INVOCATION_TARGET_EXCEPTION_ARRAY);
/*  88 */     e.load_arg(1);
/*  89 */     e.checkcast(base);
/*  90 */     e.load_arg(0);
/*  91 */     invokeSwitchHelper(e, methods, 2, base);
/*  92 */     e.end_method();
/*     */ 
/*  95 */     e = begin_method(1, NEW_INSTANCE, INVOCATION_TARGET_EXCEPTION_ARRAY);
/*  96 */     e.new_instance(base);
/*  97 */     e.dup();
/*  98 */     e.load_arg(0);
/*  99 */     invokeSwitchHelper(e, constructors, 1, base);
/* 100 */     e.end_method();
/*     */ 
/* 103 */     e = begin_method(1, GET_MAX_INDEX, null);
/* 104 */     e.push(methods.size() - 1);
/* 105 */     e.return_value();
/* 106 */     e.end_method();
/*     */ 
/* 108 */     end_class();
/*     */   }
/*     */ 
/*     */   private void emitIndexBySignature(List methods)
/*     */   {
/* 113 */     CodeEmitter e = begin_method(1, SIGNATURE_GET_INDEX, null);
/* 114 */     List signatures = CollectionUtils.transform(methods, new Transformer() {
/*     */       public Object transform(Object obj) {
/* 116 */         return ReflectUtils.getSignature((Method)obj).toString();
/*     */       }
/*     */     });
/* 119 */     e.load_arg(0);
/* 120 */     e.invoke_virtual(Constants.TYPE_OBJECT, TO_STRING);
/* 121 */     signatureSwitchHelper(e, signatures);
/* 122 */     e.end_method();
/*     */   }
/*     */ 
/*     */   private void emitIndexByClassArray(List methods)
/*     */   {
/* 127 */     CodeEmitter e = begin_method(1, METHOD_GET_INDEX, null);
/* 128 */     if (methods.size() > 100)
/*     */     {
/* 130 */       List signatures = CollectionUtils.transform(methods, new Transformer() {
/*     */         public Object transform(Object obj) {
/* 132 */           String s = ReflectUtils.getSignature((Method)obj).toString();
/* 133 */           return s.substring(0, s.lastIndexOf(')') + 1);
/*     */         }
/*     */       });
/* 136 */       e.load_args();
/* 137 */       e.invoke_static(FAST_CLASS, GET_SIGNATURE_WITHOUT_RETURN_TYPE);
/* 138 */       signatureSwitchHelper(e, signatures);
/*     */     } else {
/* 140 */       e.load_args();
/* 141 */       List info = CollectionUtils.transform(methods, MethodInfoTransformer.getInstance());
/* 142 */       EmitUtils.method_switch(e, info, new GetIndexCallback(e, info));
/*     */     }
/* 144 */     e.end_method();
/*     */   }
/*     */ 
/*     */   private void signatureSwitchHelper(CodeEmitter e, List signatures) {
/* 148 */     ObjectSwitchCallback callback = new ObjectSwitchCallback() { private final CodeEmitter val$e;
/*     */       private final List val$signatures;
/*     */ 
/* 151 */       public void processCase(Object key, Label end) { this.val$e.push(this.val$signatures.indexOf(key));
/* 152 */         this.val$e.return_value(); }
/*     */ 
/*     */       public void processDefault() {
/* 155 */         this.val$e.push(-1);
/* 156 */         this.val$e.return_value();
/*     */       }
/*     */     };
/* 159 */     EmitUtils.string_switch(e, (String[])signatures.toArray(new String[signatures.size()]), 1, callback);
/*     */   }
/*     */ 
/*     */   private static void invokeSwitchHelper(CodeEmitter e, List members, int arg, Type base)
/*     */   {
/* 166 */     List info = CollectionUtils.transform(members, MethodInfoTransformer.getInstance());
/* 167 */     Label illegalArg = e.make_label();
/* 168 */     Block block = e.begin_block();
/* 169 */     e.process_switch(getIntRange(info.size()), new ProcessSwitchCallback() { private final List val$info;
/*     */       private final CodeEmitter val$e;
/*     */       private final int val$arg;
/*     */       private final Type val$base;
/*     */       private final Label val$illegalArg;
/*     */ 
/* 171 */       public void processCase(int key, Label end) { MethodInfo method = (MethodInfo)this.val$info.get(key);
/* 172 */         Type[] types = method.getSignature().getArgumentTypes();
/* 173 */         for (int i = 0; i < types.length; i++) {
/* 174 */           this.val$e.load_arg(this.val$arg);
/* 175 */           this.val$e.aaload(i);
/* 176 */           this.val$e.unbox(types[i]);
/*     */         }
/*     */ 
/* 180 */         this.val$e.invoke(method, this.val$base);
/* 181 */         if (!TypeUtils.isConstructor(method)) {
/* 182 */           this.val$e.box(method.getSignature().getReturnType());
/*     */         }
/* 184 */         this.val$e.return_value(); }
/*     */ 
/*     */       public void processDefault() {
/* 187 */         this.val$e.goTo(this.val$illegalArg);
/*     */       }
/*     */     });
/* 190 */     block.end();
/* 191 */     EmitUtils.wrap_throwable(block, INVOCATION_TARGET_EXCEPTION);
/* 192 */     e.mark(illegalArg);
/* 193 */     e.throw_exception(ILLEGAL_ARGUMENT_EXCEPTION, "Cannot find matching method/constructor");
/*     */   }
/*     */ 
/*     */   private static int[] getIntRange(int length)
/*     */   {
/* 220 */     int[] range = new int[length];
/* 221 */     for (int i = 0; i < length; i++) {
/* 222 */       range[i] = i;
/*     */     }
/* 224 */     return range;
/*     */   }
/*     */ 
/*     */   private static class GetIndexCallback
/*     */     implements ObjectSwitchCallback
/*     */   {
/*     */     private CodeEmitter e;
/* 198 */     private Map indexes = new HashMap();
/*     */ 
/*     */     public GetIndexCallback(CodeEmitter e, List methods) {
/* 201 */       this.e = e;
/* 202 */       int index = 0;
/* 203 */       for (Iterator it = methods.iterator(); it.hasNext(); )
/* 204 */         this.indexes.put(it.next(), new Integer(index++));
/*     */     }
/*     */ 
/*     */     public void processCase(Object key, Label end)
/*     */     {
/* 209 */       this.e.push(((Integer)this.indexes.get(key)).intValue());
/* 210 */       this.e.return_value();
/*     */     }
/*     */ 
/*     */     public void processDefault() {
/* 214 */       this.e.push(-1);
/* 215 */       this.e.return_value();
/*     */     }
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.net.sf.cglib.reflect.FastClassEmitter
 * JD-Core Version:    0.6.2
 */