/*    */ package com.comphenix.net.sf.cglib.proxy;
/*    */ 
/*    */ import com.comphenix.net.sf.cglib.asm.Type;
/*    */ import com.comphenix.net.sf.cglib.core.Block;
/*    */ import com.comphenix.net.sf.cglib.core.ClassEmitter;
/*    */ import com.comphenix.net.sf.cglib.core.CodeEmitter;
/*    */ import com.comphenix.net.sf.cglib.core.EmitUtils;
/*    */ import com.comphenix.net.sf.cglib.core.MethodInfo;
/*    */ import com.comphenix.net.sf.cglib.core.Signature;
/*    */ import com.comphenix.net.sf.cglib.core.TypeUtils;
/*    */ import java.util.Iterator;
/*    */ import java.util.List;
/*    */ 
/*    */ class InvocationHandlerGenerator
/*    */   implements CallbackGenerator
/*    */ {
/* 25 */   public static final InvocationHandlerGenerator INSTANCE = new InvocationHandlerGenerator();
/*    */ 
/* 27 */   private static final Type INVOCATION_HANDLER = TypeUtils.parseType("com.comphenix.net.sf.cglib.proxy.InvocationHandler");
/*    */ 
/* 29 */   private static final Type UNDECLARED_THROWABLE_EXCEPTION = TypeUtils.parseType("com.comphenix.net.sf.cglib.proxy.UndeclaredThrowableException");
/*    */ 
/* 31 */   private static final Type METHOD = TypeUtils.parseType("java.lang.reflect.Method");
/*    */ 
/* 33 */   private static final Signature INVOKE = TypeUtils.parseSignature("Object invoke(Object, java.lang.reflect.Method, Object[])");
/*    */ 
/*    */   public void generate(ClassEmitter ce, CallbackGenerator.Context context, List methods)
/*    */   {
/* 37 */     for (Iterator it = methods.iterator(); it.hasNext(); ) {
/* 38 */       MethodInfo method = (MethodInfo)it.next();
/* 39 */       Signature impl = context.getImplSignature(method);
/* 40 */       ce.declare_field(26, impl.getName(), METHOD, null);
/*    */ 
/* 42 */       CodeEmitter e = context.beginMethod(ce, method);
/* 43 */       Block handler = e.begin_block();
/* 44 */       context.emitCallback(e, context.getIndex(method));
/* 45 */       e.load_this();
/* 46 */       e.getfield(impl.getName());
/* 47 */       e.create_arg_array();
/* 48 */       e.invoke_interface(INVOCATION_HANDLER, INVOKE);
/* 49 */       e.unbox(method.getSignature().getReturnType());
/* 50 */       e.return_value();
/* 51 */       handler.end();
/* 52 */       EmitUtils.wrap_undeclared_throwable(e, handler, method.getExceptionTypes(), UNDECLARED_THROWABLE_EXCEPTION);
/* 53 */       e.end_method();
/*    */     }
/*    */   }
/*    */ 
/*    */   public void generateStatic(CodeEmitter e, CallbackGenerator.Context context, List methods) {
/* 58 */     for (Iterator it = methods.iterator(); it.hasNext(); ) {
/* 59 */       MethodInfo method = (MethodInfo)it.next();
/* 60 */       EmitUtils.load_method(e, method);
/* 61 */       e.putfield(context.getImplSignature(method).getName());
/*    */     }
/*    */   }
/*    */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.net.sf.cglib.proxy.InvocationHandlerGenerator
 * JD-Core Version:    0.6.2
 */