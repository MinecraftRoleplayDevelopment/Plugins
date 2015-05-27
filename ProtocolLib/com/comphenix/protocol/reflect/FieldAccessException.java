/*    */ package com.comphenix.protocol.reflect;
/*    */ 
/*    */ public class FieldAccessException extends RuntimeException
/*    */ {
/*    */   private static final long serialVersionUID = 1911011681494034617L;
/*    */ 
/*    */   public FieldAccessException()
/*    */   {
/*    */   }
/*    */ 
/*    */   public FieldAccessException(String message, Throwable cause)
/*    */   {
/* 37 */     super(message, cause);
/*    */   }
/*    */ 
/*    */   public FieldAccessException(String message) {
/* 41 */     super(message);
/*    */   }
/*    */ 
/*    */   public FieldAccessException(Throwable cause) {
/* 45 */     super(cause);
/*    */   }
/*    */ 
/*    */   public static FieldAccessException fromFormat(String message, Object[] params) {
/* 49 */     return new FieldAccessException(String.format(message, params));
/*    */   }
/*    */ 
/*    */   public String toString()
/*    */   {
/* 54 */     String message = getMessage();
/* 55 */     return "FieldAccessException" + (message != null ? ": " + message : "");
/*    */   }
/*    */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.reflect.FieldAccessException
 * JD-Core Version:    0.6.2
 */