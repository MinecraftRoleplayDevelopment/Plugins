/*    */ package com.comphenix.net.sf.cglib.transform.impl;
/*    */ 
/*    */ import com.comphenix.net.sf.cglib.asm.Type;
/*    */ import com.comphenix.net.sf.cglib.core.Block;
/*    */ import com.comphenix.net.sf.cglib.core.CodeEmitter;
/*    */ import com.comphenix.net.sf.cglib.core.Constants;
/*    */ import com.comphenix.net.sf.cglib.core.EmitUtils;
/*    */ import com.comphenix.net.sf.cglib.core.Signature;
/*    */ import com.comphenix.net.sf.cglib.core.TypeUtils;
/*    */ import com.comphenix.net.sf.cglib.transform.ClassEmitterTransformer;
/*    */ import java.lang.reflect.Constructor;
/*    */ 
/*    */ public class UndeclaredThrowableTransformer extends ClassEmitterTransformer
/*    */ {
/*    */   private Type wrapper;
/*    */ 
/*    */   public UndeclaredThrowableTransformer(Class wrapper)
/*    */   {
/* 29 */     this.wrapper = Type.getType(wrapper);
/* 30 */     boolean found = false;
/* 31 */     Constructor[] cstructs = wrapper.getConstructors();
/* 32 */     for (int i = 0; i < cstructs.length; i++) {
/* 33 */       Class[] types = cstructs[i].getParameterTypes();
/* 34 */       if ((types.length == 1) && (types[0].equals(Throwable.class))) {
/* 35 */         found = true;
/* 36 */         break;
/*    */       }
/*    */     }
/* 39 */     if (!found)
/* 40 */       throw new IllegalArgumentException(wrapper + " does not have a single-arg constructor that takes a Throwable");
/*    */   }
/*    */ 
/*    */   public CodeEmitter begin_method(int access, Signature sig, Type[] exceptions) {
/* 44 */     CodeEmitter e = super.begin_method(access, sig, exceptions);
/* 45 */     if ((TypeUtils.isAbstract(access)) || (sig.equals(Constants.SIG_STATIC))) {
/* 46 */       return e;
/*    */     }
/* 48 */     return new CodeEmitter(e) {
/*    */       private Block handler;
/*    */       private final Type[] val$exceptions;
/*    */ 
/*    */       public void visitMaxs(int maxStack, int maxLocals) {
/* 54 */         this.handler.end();
/* 55 */         EmitUtils.wrap_undeclared_throwable(this, this.handler, this.val$exceptions, UndeclaredThrowableTransformer.this.wrapper);
/* 56 */         super.visitMaxs(maxStack, maxLocals);
/*    */       }
/*    */     };
/*    */   }
/*    */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.net.sf.cglib.transform.impl.UndeclaredThrowableTransformer
 * JD-Core Version:    0.6.2
 */