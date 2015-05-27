/*    */ package com.comphenix.protocol.injector.packet;
/*    */ 
/*    */ import com.comphenix.protocol.ProtocolLibrary;
/*    */ import com.comphenix.protocol.ProtocolManager;
/*    */ import com.comphenix.protocol.error.ErrorReporter;
/*    */ import com.comphenix.protocol.injector.ListenerInvoker;
/*    */ import com.comphenix.protocol.injector.PacketFilterManager;
/*    */ import com.comphenix.protocol.injector.player.PlayerInjectionHandler;
/*    */ import com.comphenix.protocol.reflect.FieldAccessException;
/*    */ import com.google.common.base.Preconditions;
/*    */ import javax.annotation.Nonnull;
/*    */ 
/*    */ public class PacketInjectorBuilder
/*    */ {
/*    */   protected ListenerInvoker invoker;
/*    */   protected ErrorReporter reporter;
/*    */   protected PlayerInjectionHandler playerInjection;
/*    */ 
/*    */   public static PacketInjectorBuilder newBuilder()
/*    */   {
/* 29 */     return new PacketInjectorBuilder();
/*    */   }
/*    */ 
/*    */   public PacketInjectorBuilder reporter(@Nonnull ErrorReporter reporter)
/*    */   {
/* 42 */     Preconditions.checkNotNull(reporter, "reporter cannot be NULL");
/* 43 */     this.reporter = reporter;
/* 44 */     return this;
/*    */   }
/*    */ 
/*    */   public PacketInjectorBuilder invoker(@Nonnull ListenerInvoker invoker)
/*    */   {
/* 53 */     Preconditions.checkNotNull(invoker, "invoker cannot be NULL");
/* 54 */     this.invoker = invoker;
/* 55 */     return this;
/*    */   }
/*    */ 
/*    */   @Nonnull
/*    */   public PacketInjectorBuilder playerInjection(@Nonnull PlayerInjectionHandler playerInjection)
/*    */   {
/* 65 */     Preconditions.checkNotNull(playerInjection, "playerInjection cannot be NULL");
/* 66 */     this.playerInjection = playerInjection;
/* 67 */     return this;
/*    */   }
/*    */ 
/*    */   private void initializeDefaults()
/*    */   {
/* 74 */     ProtocolManager manager = ProtocolLibrary.getProtocolManager();
/*    */ 
/* 77 */     if (this.reporter == null)
/* 78 */       this.reporter = ProtocolLibrary.getErrorReporter();
/* 79 */     if (this.invoker == null)
/* 80 */       this.invoker = ((PacketFilterManager)manager);
/* 81 */     if (this.playerInjection == null)
/* 82 */       throw new IllegalStateException("Player injection parameter must be initialized.");
/*    */   }
/*    */ 
/*    */   public PacketInjector buildInjector()
/*    */     throws FieldAccessException
/*    */   {
/* 93 */     initializeDefaults();
/* 94 */     return new ProxyPacketInjector(this.invoker, this.playerInjection, this.reporter);
/*    */   }
/*    */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.injector.packet.PacketInjectorBuilder
 * JD-Core Version:    0.6.2
 */