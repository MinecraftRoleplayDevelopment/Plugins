/*    */ package com.comphenix.protocol.wrappers.collection;
/*    */ 
/*    */ import com.google.common.base.Function;
/*    */ import javax.annotation.Nullable;
/*    */ 
/*    */ public abstract class AbstractConverted<VInner, VOuter>
/*    */ {
/* 34 */   private Function<VOuter, VInner> innerConverter = new Function()
/*    */   {
/*    */     public VInner apply(@Nullable VOuter param) {
/* 37 */       return AbstractConverted.this.toInner(param);
/*    */     }
/* 34 */   };
/*    */ 
/* 42 */   private Function<VInner, VOuter> outerConverter = new Function()
/*    */   {
/*    */     public VOuter apply(@Nullable VInner param) {
/* 45 */       return AbstractConverted.this.toOuter(param);
/*    */     }
/* 42 */   };
/*    */ 
/*    */   protected abstract VOuter toOuter(VInner paramVInner);
/*    */ 
/*    */   protected abstract VInner toInner(VOuter paramVOuter);
/*    */ 
/*    */   protected Function<VOuter, VInner> getInnerConverter()
/*    */   {
/* 68 */     return this.innerConverter;
/*    */   }
/*    */ 
/*    */   protected Function<VInner, VOuter> getOuterConverter()
/*    */   {
/* 76 */     return this.outerConverter;
/*    */   }
/*    */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.wrappers.collection.AbstractConverted
 * JD-Core Version:    0.6.2
 */