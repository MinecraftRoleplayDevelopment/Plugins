/*    */ package com.comphenix.net.sf.cglib.proxy;
/*    */ 
/*    */ import com.comphenix.net.sf.cglib.core.CodeGenerationException;
/*    */ 
/*    */ public class UndeclaredThrowableException extends CodeGenerationException
/*    */ {
/*    */   public UndeclaredThrowableException(Throwable t)
/*    */   {
/* 30 */     super(t);
/*    */   }
/*    */ 
/*    */   public Throwable getUndeclaredThrowable() {
/* 34 */     return getCause();
/*    */   }
/*    */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.net.sf.cglib.proxy.UndeclaredThrowableException
 * JD-Core Version:    0.6.2
 */