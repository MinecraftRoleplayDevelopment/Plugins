/*    */ package com.comphenix.protocol.events;
/*    */ 
/*    */ public enum ListenerPriority
/*    */ {
/* 30 */   LOWEST(0), 
/*    */ 
/* 34 */   LOW(1), 
/*    */ 
/* 38 */   NORMAL(2), 
/*    */ 
/* 42 */   HIGH(3), 
/*    */ 
/* 47 */   HIGHEST(4), 
/*    */ 
/* 53 */   MONITOR(5);
/*    */ 
/*    */   private final int slot;
/*    */ 
/*    */   private ListenerPriority(int slot) {
/* 58 */     this.slot = slot;
/*    */   }
/*    */ 
/*    */   public int getSlot()
/*    */   {
/* 66 */     return this.slot;
/*    */   }
/*    */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.events.ListenerPriority
 * JD-Core Version:    0.6.2
 */