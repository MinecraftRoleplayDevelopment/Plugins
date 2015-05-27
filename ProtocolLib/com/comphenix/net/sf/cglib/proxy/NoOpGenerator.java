/*    */ package com.comphenix.net.sf.cglib.proxy;
/*    */ 
/*    */ import com.comphenix.net.sf.cglib.core.ClassEmitter;
/*    */ import com.comphenix.net.sf.cglib.core.CodeEmitter;
/*    */ import com.comphenix.net.sf.cglib.core.EmitUtils;
/*    */ import com.comphenix.net.sf.cglib.core.MethodInfo;
/*    */ import com.comphenix.net.sf.cglib.core.TypeUtils;
/*    */ import java.util.Iterator;
/*    */ import java.util.List;
/*    */ 
/*    */ class NoOpGenerator
/*    */   implements CallbackGenerator
/*    */ {
/* 25 */   public static final NoOpGenerator INSTANCE = new NoOpGenerator();
/*    */ 
/*    */   public void generate(ClassEmitter ce, CallbackGenerator.Context context, List methods) {
/* 28 */     for (Iterator it = methods.iterator(); it.hasNext(); ) {
/* 29 */       MethodInfo method = (MethodInfo)it.next();
/* 30 */       if ((TypeUtils.isBridge(method.getModifiers())) || ((TypeUtils.isProtected(context.getOriginalModifiers(method))) && (TypeUtils.isPublic(method.getModifiers()))))
/*    */       {
/* 33 */         CodeEmitter e = EmitUtils.begin_method(ce, method);
/* 34 */         e.load_this();
/* 35 */         e.load_args();
/* 36 */         context.emitInvoke(e, method);
/* 37 */         e.return_value();
/* 38 */         e.end_method();
/*    */       }
/*    */     }
/*    */   }
/*    */ 
/*    */   public void generateStatic(CodeEmitter e, CallbackGenerator.Context context, List methods)
/*    */   {
/*    */   }
/*    */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.net.sf.cglib.proxy.NoOpGenerator
 * JD-Core Version:    0.6.2
 */