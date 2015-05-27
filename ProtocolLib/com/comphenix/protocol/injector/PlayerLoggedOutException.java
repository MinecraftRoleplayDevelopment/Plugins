/*    */ package com.comphenix.protocol.injector;
/*    */ 
/*    */ public class PlayerLoggedOutException extends RuntimeException
/*    */ {
/*    */   private static final long serialVersionUID = 4889257862160145234L;
/*    */ 
/*    */   public PlayerLoggedOutException()
/*    */   {
/* 34 */     super("Cannot inject a player that has already logged out.");
/*    */   }
/*    */ 
/*    */   public PlayerLoggedOutException(String message, Throwable cause) {
/* 38 */     super(message, cause);
/*    */   }
/*    */ 
/*    */   public PlayerLoggedOutException(String message) {
/* 42 */     super(message);
/*    */   }
/*    */ 
/*    */   public PlayerLoggedOutException(Throwable cause) {
/* 46 */     super(cause);
/*    */   }
/*    */ 
/*    */   public static PlayerLoggedOutException fromFormat(String message, Object[] params)
/*    */   {
/* 56 */     return new PlayerLoggedOutException(String.format(message, params));
/*    */   }
/*    */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.injector.PlayerLoggedOutException
 * JD-Core Version:    0.6.2
 */