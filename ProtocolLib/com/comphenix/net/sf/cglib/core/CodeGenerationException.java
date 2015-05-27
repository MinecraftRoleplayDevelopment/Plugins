/*    */ package com.comphenix.net.sf.cglib.core;
/*    */ 
/*    */ public class CodeGenerationException extends RuntimeException
/*    */ {
/*    */   private Throwable cause;
/*    */ 
/*    */   public CodeGenerationException(Throwable cause)
/*    */   {
/* 25 */     super(cause.getClass().getName() + "-->" + cause.getMessage());
/* 26 */     this.cause = cause;
/*    */   }
/*    */ 
/*    */   public Throwable getCause() {
/* 30 */     return this.cause;
/*    */   }
/*    */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.net.sf.cglib.core.CodeGenerationException
 * JD-Core Version:    0.6.2
 */