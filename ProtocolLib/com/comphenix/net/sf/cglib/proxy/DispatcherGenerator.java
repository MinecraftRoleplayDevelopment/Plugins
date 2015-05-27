/*    */ package com.comphenix.net.sf.cglib.proxy;
/*    */ 
/*    */ import com.comphenix.net.sf.cglib.asm.Type;
/*    */ import com.comphenix.net.sf.cglib.core.ClassEmitter;
/*    */ import com.comphenix.net.sf.cglib.core.ClassInfo;
/*    */ import com.comphenix.net.sf.cglib.core.CodeEmitter;
/*    */ import com.comphenix.net.sf.cglib.core.MethodInfo;
/*    */ import com.comphenix.net.sf.cglib.core.Signature;
/*    */ import com.comphenix.net.sf.cglib.core.TypeUtils;
/*    */ import java.util.Iterator;
/*    */ import java.util.List;
/*    */ 
/*    */ class DispatcherGenerator
/*    */   implements CallbackGenerator
/*    */ {
/* 23 */   public static final DispatcherGenerator INSTANCE = new DispatcherGenerator(false);
/*    */ 
/* 25 */   public static final DispatcherGenerator PROXY_REF_INSTANCE = new DispatcherGenerator(true);
/*    */ 
/* 28 */   private static final Type DISPATCHER = TypeUtils.parseType("com.comphenix.net.sf.cglib.proxy.Dispatcher");
/*    */ 
/* 30 */   private static final Type PROXY_REF_DISPATCHER = TypeUtils.parseType("com.comphenix.net.sf.cglib.proxy.ProxyRefDispatcher");
/*    */ 
/* 32 */   private static final Signature LOAD_OBJECT = TypeUtils.parseSignature("Object loadObject()");
/*    */ 
/* 34 */   private static final Signature PROXY_REF_LOAD_OBJECT = TypeUtils.parseSignature("Object loadObject(Object)");
/*    */   private boolean proxyRef;
/*    */ 
/*    */   private DispatcherGenerator(boolean proxyRef)
/*    */   {
/* 40 */     this.proxyRef = proxyRef;
/*    */   }
/*    */ 
/*    */   public void generate(ClassEmitter ce, CallbackGenerator.Context context, List methods) {
/* 44 */     for (Iterator it = methods.iterator(); it.hasNext(); ) {
/* 45 */       MethodInfo method = (MethodInfo)it.next();
/* 46 */       if (!TypeUtils.isProtected(method.getModifiers())) {
/* 47 */         CodeEmitter e = context.beginMethod(ce, method);
/* 48 */         context.emitCallback(e, context.getIndex(method));
/* 49 */         if (this.proxyRef) {
/* 50 */           e.load_this();
/* 51 */           e.invoke_interface(PROXY_REF_DISPATCHER, PROXY_REF_LOAD_OBJECT);
/*    */         } else {
/* 53 */           e.invoke_interface(DISPATCHER, LOAD_OBJECT);
/*    */         }
/* 55 */         e.checkcast(method.getClassInfo().getType());
/* 56 */         e.load_args();
/* 57 */         e.invoke(method);
/* 58 */         e.return_value();
/* 59 */         e.end_method();
/*    */       }
/*    */     }
/*    */   }
/*    */ 
/*    */   public void generateStatic(CodeEmitter e, CallbackGenerator.Context context, List methods)
/*    */   {
/*    */   }
/*    */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.net.sf.cglib.proxy.DispatcherGenerator
 * JD-Core Version:    0.6.2
 */