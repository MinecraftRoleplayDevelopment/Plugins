/*    */ package com.comphenix.net.sf.cglib.transform.impl;
/*    */ 
/*    */ import com.comphenix.net.sf.cglib.asm.Type;
/*    */ import com.comphenix.net.sf.cglib.core.CodeEmitter;
/*    */ import com.comphenix.net.sf.cglib.core.Constants;
/*    */ import com.comphenix.net.sf.cglib.core.MethodInfo;
/*    */ import com.comphenix.net.sf.cglib.core.ReflectUtils;
/*    */ import com.comphenix.net.sf.cglib.core.Signature;
/*    */ import com.comphenix.net.sf.cglib.transform.ClassEmitterTransformer;
/*    */ import java.lang.reflect.Method;
/*    */ 
/*    */ public class AddInitTransformer extends ClassEmitterTransformer
/*    */ {
/*    */   private MethodInfo info;
/*    */ 
/*    */   public AddInitTransformer(Method method)
/*    */   {
/* 37 */     this.info = ReflectUtils.getMethodInfo(method);
/*    */ 
/* 39 */     Type[] types = this.info.getSignature().getArgumentTypes();
/* 40 */     if ((types.length != 1) || (!types[0].equals(Constants.TYPE_OBJECT)) || (!this.info.getSignature().getReturnType().equals(Type.VOID_TYPE)))
/*    */     {
/* 43 */       throw new IllegalArgumentException(method + " illegal signature");
/*    */     }
/*    */   }
/*    */ 
/*    */   public CodeEmitter begin_method(int access, Signature sig, Type[] exceptions) {
/* 48 */     CodeEmitter emitter = super.begin_method(access, sig, exceptions);
/* 49 */     if (sig.getName().equals("<init>")) {
/* 50 */       return new CodeEmitter(emitter) {
/*    */         public void visitInsn(int opcode) {
/* 52 */           if (opcode == 177) {
/* 53 */             load_this();
/* 54 */             invoke(AddInitTransformer.this.info);
/*    */           }
/* 56 */           super.visitInsn(opcode);
/*    */         }
/*    */       };
/*    */     }
/* 60 */     return emitter;
/*    */   }
/*    */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.net.sf.cglib.transform.impl.AddInitTransformer
 * JD-Core Version:    0.6.2
 */