/*    */ package com.comphenix.net.sf.cglib.core;
/*    */ 
/*    */ import java.lang.reflect.Method;
/*    */ import java.util.HashSet;
/*    */ import java.util.Set;
/*    */ 
/*    */ public class DuplicatesPredicate
/*    */   implements Predicate
/*    */ {
/* 22 */   private Set unique = new HashSet();
/*    */ 
/*    */   public boolean evaluate(Object arg) {
/* 25 */     return this.unique.add(MethodWrapper.create((Method)arg));
/*    */   }
/*    */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.net.sf.cglib.core.DuplicatesPredicate
 * JD-Core Version:    0.6.2
 */