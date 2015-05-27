/*    */ package com.comphenix.protocol.wrappers;
/*    */ 
/*    */ import com.google.common.base.Preconditions;
/*    */ 
/*    */ public abstract class AbstractWrapper
/*    */ {
/*    */   protected Object handle;
/*    */   protected Class<?> handleType;
/*    */ 
/*    */   public AbstractWrapper(Class<?> handleType)
/*    */   {
/* 18 */     this.handleType = ((Class)Preconditions.checkNotNull(handleType, "handleType cannot be NULL"));
/*    */   }
/*    */ 
/*    */   protected void setHandle(Object handle)
/*    */   {
/* 28 */     if (handle == null)
/* 29 */       throw new IllegalArgumentException("handle cannot be NULL.");
/* 30 */     if (!this.handleType.isAssignableFrom(handle.getClass()))
/* 31 */       throw new IllegalArgumentException("handle (" + handle + ") is not a " + this.handleType + ", but " + handle.getClass());
/* 32 */     this.handle = handle;
/*    */   }
/*    */ 
/*    */   public Object getHandle()
/*    */   {
/* 40 */     return this.handle;
/*    */   }
/*    */ 
/*    */   public Class<?> getHandleType()
/*    */   {
/* 48 */     return this.handleType;
/*    */   }
/*    */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.wrappers.AbstractWrapper
 * JD-Core Version:    0.6.2
 */