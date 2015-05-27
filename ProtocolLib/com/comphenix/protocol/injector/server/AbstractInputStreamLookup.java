/*    */ package com.comphenix.protocol.injector.server;
/*    */ 
/*    */ import com.comphenix.protocol.error.ErrorReporter;
/*    */ import java.io.InputStream;
/*    */ import java.net.Socket;
/*    */ import java.net.SocketAddress;
/*    */ import org.bukkit.Server;
/*    */ import org.bukkit.entity.Player;
/*    */ 
/*    */ public abstract class AbstractInputStreamLookup
/*    */ {
/*    */   protected final ErrorReporter reporter;
/*    */   protected final Server server;
/*    */ 
/*    */   protected AbstractInputStreamLookup(ErrorReporter reporter, Server server)
/*    */   {
/* 20 */     this.reporter = reporter;
/* 21 */     this.server = server;
/*    */   }
/*    */ 
/*    */   public abstract void inject(Object paramObject);
/*    */ 
/*    */   public abstract SocketInjector waitSocketInjector(InputStream paramInputStream);
/*    */ 
/*    */   public abstract SocketInjector waitSocketInjector(Socket paramSocket);
/*    */ 
/*    */   public abstract SocketInjector waitSocketInjector(SocketAddress paramSocketAddress);
/*    */ 
/*    */   public abstract SocketInjector peekSocketInjector(SocketAddress paramSocketAddress);
/*    */ 
/*    */   public abstract void setSocketInjector(SocketAddress paramSocketAddress, SocketInjector paramSocketInjector);
/*    */ 
/*    */   protected void onPreviousSocketOverwritten(SocketInjector previous, SocketInjector current)
/*    */   {
/* 71 */     Player player = previous.getPlayer();
/*    */ 
/* 74 */     if ((player instanceof InjectorContainer))
/* 75 */       TemporaryPlayerFactory.setInjectorInPlayer(player, current);
/*    */   }
/*    */ 
/*    */   public abstract void cleanupAll();
/*    */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.injector.server.AbstractInputStreamLookup
 * JD-Core Version:    0.6.2
 */