/*    */ package com.comphenix.protocol.timing;
/*    */ 
/*    */ public abstract class OnlineComputation
/*    */ {
/*    */   public abstract int getCount();
/*    */ 
/*    */   public abstract void observe(double paramDouble);
/*    */ 
/*    */   public abstract OnlineComputation copy();
/*    */ 
/*    */   public static OnlineComputation synchronizedComputation(OnlineComputation computation)
/*    */   {
/* 32 */     return new OnlineComputation()
/*    */     {
/*    */       public synchronized void observe(double value) {
/* 35 */         this.val$computation.observe(value);
/*    */       }
/*    */ 
/*    */       public synchronized int getCount()
/*    */       {
/* 40 */         return this.val$computation.getCount();
/*    */       }
/*    */ 
/*    */       public synchronized OnlineComputation copy()
/*    */       {
/* 45 */         return this.val$computation.copy();
/*    */       }
/*    */     };
/*    */   }
/*    */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.timing.OnlineComputation
 * JD-Core Version:    0.6.2
 */