/*    */ package com.comphenix.protocol.injector;
/*    */ 
/*    */ public enum GamePhase
/*    */ {
/* 29 */   LOGIN, 
/*    */ 
/* 34 */   PLAYING, 
/*    */ 
/* 39 */   BOTH;
/*    */ 
/*    */   public boolean hasLogin()
/*    */   {
/* 46 */     return (this == LOGIN) || (this == BOTH);
/*    */   }
/*    */ 
/*    */   public boolean hasPlaying()
/*    */   {
/* 54 */     return (this == PLAYING) || (this == BOTH);
/*    */   }
/*    */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.injector.GamePhase
 * JD-Core Version:    0.6.2
 */