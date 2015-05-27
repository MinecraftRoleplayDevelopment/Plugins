/*    */ package com.comphenix.protocol.wrappers.collection;
/*    */ 
/*    */ import java.util.Set;
/*    */ 
/*    */ public class CachedSet<T> extends CachedCollection<T>
/*    */   implements Set<T>
/*    */ {
/*    */   public CachedSet(Set<T> delegate)
/*    */   {
/* 17 */     super(delegate);
/*    */   }
/*    */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.wrappers.collection.CachedSet
 * JD-Core Version:    0.6.2
 */