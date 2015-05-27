/*    */ package com.comphenix.net.sf.cglib.proxy;
/*    */ 
/*    */ import com.comphenix.net.sf.cglib.asm.Type;
/*    */ import com.comphenix.net.sf.cglib.core.ClassEmitter;
/*    */ import com.comphenix.net.sf.cglib.core.CodeEmitter;
/*    */ import com.comphenix.net.sf.cglib.core.MethodInfo;
/*    */ import com.comphenix.net.sf.cglib.core.Signature;
/*    */ import com.comphenix.net.sf.cglib.core.TypeUtils;
/*    */ import java.util.Iterator;
/*    */ import java.util.List;
/*    */ 
/*    */ class FixedValueGenerator
/*    */   implements CallbackGenerator
/*    */ {
/* 23 */   public static final FixedValueGenerator INSTANCE = new FixedValueGenerator();
/* 24 */   private static final Type FIXED_VALUE = TypeUtils.parseType("com.comphenix.net.sf.cglib.proxy.FixedValue");
/*    */ 
/* 26 */   private static final Signature LOAD_OBJECT = TypeUtils.parseSignature("Object loadObject()");
/*    */ 
/*    */   public void generate(ClassEmitter ce, CallbackGenerator.Context context, List methods)
/*    */   {
/* 30 */     for (Iterator it = methods.iterator(); it.hasNext(); ) {
/* 31 */       MethodInfo method = (MethodInfo)it.next();
/* 32 */       CodeEmitter e = context.beginMethod(ce, method);
/* 33 */       context.emitCallback(e, context.getIndex(method));
/* 34 */       e.invoke_interface(FIXED_VALUE, LOAD_OBJECT);
/* 35 */       e.unbox_or_zero(e.getReturnType());
/* 36 */       e.return_value();
/* 37 */       e.end_method();
/*    */     }
/*    */   }
/*    */ 
/*    */   public void generateStatic(CodeEmitter e, CallbackGenerator.Context context, List methods)
/*    */   {
/*    */   }
/*    */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.net.sf.cglib.proxy.FixedValueGenerator
 * JD-Core Version:    0.6.2
 */