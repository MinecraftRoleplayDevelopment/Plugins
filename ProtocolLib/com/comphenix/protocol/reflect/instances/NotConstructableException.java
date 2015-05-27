/*    */ package com.comphenix.protocol.reflect.instances;
/*    */ 
/*    */ public class NotConstructableException extends IllegalArgumentException
/*    */ {
/*    */   private static final long serialVersionUID = -1144171604744845463L;
/*    */ 
/*    */   public NotConstructableException()
/*    */   {
/* 19 */     super("This object should never be constructed.");
/*    */   }
/*    */ 
/*    */   public NotConstructableException(String message)
/*    */   {
/* 26 */     super(message);
/*    */   }
/*    */ 
/*    */   public NotConstructableException(String message, Throwable cause)
/*    */   {
/* 33 */     super(message, cause);
/*    */   }
/*    */ 
/*    */   public NotConstructableException(Throwable cause)
/*    */   {
/* 40 */     super(cause);
/*    */   }
/*    */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.reflect.instances.NotConstructableException
 * JD-Core Version:    0.6.2
 */