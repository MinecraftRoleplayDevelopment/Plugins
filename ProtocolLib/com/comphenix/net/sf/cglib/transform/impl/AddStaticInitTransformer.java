/*    */ package com.comphenix.net.sf.cglib.transform.impl;
/*    */ 
/*    */ import com.comphenix.net.sf.cglib.asm.Type;
/*    */ import com.comphenix.net.sf.cglib.core.CodeEmitter;
/*    */ import com.comphenix.net.sf.cglib.core.Constants;
/*    */ import com.comphenix.net.sf.cglib.core.EmitUtils;
/*    */ import com.comphenix.net.sf.cglib.core.MethodInfo;
/*    */ import com.comphenix.net.sf.cglib.core.ReflectUtils;
/*    */ import com.comphenix.net.sf.cglib.core.Signature;
/*    */ import com.comphenix.net.sf.cglib.core.TypeUtils;
/*    */ import com.comphenix.net.sf.cglib.transform.ClassEmitterTransformer;
/*    */ import java.lang.reflect.Method;
/*    */ 
/*    */ public class AddStaticInitTransformer extends ClassEmitterTransformer
/*    */ {
/*    */   private MethodInfo info;
/*    */ 
/*    */   public AddStaticInitTransformer(Method classInit)
/*    */   {
/* 30 */     this.info = ReflectUtils.getMethodInfo(classInit);
/* 31 */     if (!TypeUtils.isStatic(this.info.getModifiers())) {
/* 32 */       throw new IllegalArgumentException(classInit + " is not static");
/*    */     }
/* 34 */     Type[] types = this.info.getSignature().getArgumentTypes();
/* 35 */     if ((types.length != 1) || (!types[0].equals(Constants.TYPE_CLASS)) || (!this.info.getSignature().getReturnType().equals(Type.VOID_TYPE)))
/*    */     {
/* 38 */       throw new IllegalArgumentException(classInit + " illegal signature");
/*    */     }
/*    */   }
/*    */ 
/*    */   protected void init() {
/* 43 */     if (!TypeUtils.isInterface(getAccess())) {
/* 44 */       CodeEmitter e = getStaticHook();
/* 45 */       EmitUtils.load_class_this(e);
/* 46 */       e.invoke(this.info);
/*    */     }
/*    */   }
/*    */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.net.sf.cglib.transform.impl.AddStaticInitTransformer
 * JD-Core Version:    0.6.2
 */