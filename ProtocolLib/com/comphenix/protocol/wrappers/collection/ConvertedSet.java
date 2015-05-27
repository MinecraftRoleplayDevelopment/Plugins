/*    */ package com.comphenix.protocol.wrappers.collection;
/*    */ 
/*    */ import java.util.Collection;
/*    */ import java.util.Set;
/*    */ 
/*    */ public abstract class ConvertedSet<VInner, VOuter> extends ConvertedCollection<VInner, VOuter>
/*    */   implements Set<VOuter>
/*    */ {
/*    */   public ConvertedSet(Collection<VInner> inner)
/*    */   {
/* 33 */     super(inner);
/*    */   }
/*    */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.wrappers.collection.ConvertedSet
 * JD-Core Version:    0.6.2
 */