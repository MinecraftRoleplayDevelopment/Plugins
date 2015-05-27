/*    */ package com.comphenix.protocol.injector.server;
/*    */ 
/*    */ class InjectorContainer
/*    */ {
/*    */   private volatile SocketInjector injector;
/*    */ 
/*    */   public SocketInjector getInjector()
/*    */   {
/* 13 */     return this.injector;
/*    */   }
/*    */ 
/*    */   public void setInjector(SocketInjector injector) {
/* 17 */     if (injector == null)
/* 18 */       throw new IllegalArgumentException("Injector cannot be NULL.");
/* 19 */     this.injector = injector;
/*    */   }
/*    */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.injector.server.InjectorContainer
 * JD-Core Version:    0.6.2
 */