/*    */ package com.comphenix.net.sf.cglib.core;
/*    */ 
/*    */ import java.lang.reflect.Member;
/*    */ 
/*    */ public class RejectModifierPredicate
/*    */   implements Predicate
/*    */ {
/*    */   private int rejectMask;
/*    */ 
/*    */   public RejectModifierPredicate(int rejectMask)
/*    */   {
/* 24 */     this.rejectMask = rejectMask;
/*    */   }
/*    */ 
/*    */   public boolean evaluate(Object arg) {
/* 28 */     return (((Member)arg).getModifiers() & this.rejectMask) == 0;
/*    */   }
/*    */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.net.sf.cglib.core.RejectModifierPredicate
 * JD-Core Version:    0.6.2
 */