/*     */ package com.comphenix.protocol.injector.server;
/*     */ 
/*     */ import com.comphenix.net.sf.cglib.proxy.Callback;
/*     */ import com.comphenix.net.sf.cglib.proxy.CallbackFilter;
/*     */ import com.comphenix.net.sf.cglib.proxy.Enhancer;
/*     */ import com.comphenix.net.sf.cglib.proxy.MethodInterceptor;
/*     */ import com.comphenix.net.sf.cglib.proxy.MethodProxy;
/*     */ import com.comphenix.net.sf.cglib.proxy.NoOp;
/*     */ import com.comphenix.protocol.events.PacketContainer;
/*     */ import com.comphenix.protocol.reflect.FieldAccessException;
/*     */ import com.comphenix.protocol.utility.ChatExtensions;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.lang.reflect.Method;
/*     */ import java.net.Socket;
/*     */ import org.bukkit.Server;
/*     */ import org.bukkit.entity.Player;
/*     */ 
/*     */ public class TemporaryPlayerFactory
/*     */ {
/*     */   private static CallbackFilter callbackFilter;
/*     */ 
/*     */   public static SocketInjector getInjectorFromPlayer(Player player)
/*     */   {
/*  50 */     if ((player instanceof InjectorContainer)) {
/*  51 */       return ((InjectorContainer)player).getInjector();
/*     */     }
/*  53 */     return null;
/*     */   }
/*     */ 
/*     */   public static void setInjectorInPlayer(Player player, SocketInjector injector)
/*     */   {
/*  62 */     ((InjectorContainer)player).setInjector(injector);
/*     */   }
/*     */ 
/*     */   public Player createTemporaryPlayer(final Server server)
/*     */   {
/*  87 */     Callback implementation = new MethodInterceptor()
/*     */     {
/*     */       public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
/*  90 */         String methodName = method.getName();
/*  91 */         SocketInjector injector = ((InjectorContainer)obj).getInjector();
/*     */ 
/*  93 */         if (injector == null) {
/*  94 */           throw new IllegalStateException("Unable to find injector.");
/*     */         }
/*     */ 
/*  97 */         if (methodName.equals("getPlayer"))
/*  98 */           return injector.getUpdatedPlayer();
/*  99 */         if (methodName.equals("getAddress"))
/* 100 */           return injector.getAddress();
/* 101 */         if (methodName.equals("getServer")) {
/* 102 */           return server;
/*     */         }
/*     */ 
/* 105 */         if ((methodName.equals("chat")) || (methodName.equals("sendMessage"))) {
/*     */           try {
/* 107 */             Object argument = args[0];
/*     */ 
/* 110 */             if ((argument instanceof String))
/* 111 */               return TemporaryPlayerFactory.this.sendMessage(injector, (String)argument);
/* 112 */             if ((argument instanceof String[])) {
/* 113 */               for (String message : (String[])argument) {
/* 114 */                 TemporaryPlayerFactory.this.sendMessage(injector, message);
/*     */               }
/* 116 */               return null;
/*     */             }
/*     */           } catch (InvocationTargetException e) {
/* 119 */             throw e.getCause();
/*     */           }
/*     */ 
/*     */         }
/*     */ 
/* 124 */         if (methodName.equals("kickPlayer")) {
/* 125 */           injector.disconnect((String)args[0]);
/* 126 */           return null;
/*     */         }
/*     */ 
/* 130 */         Player updated = injector.getUpdatedPlayer();
/*     */ 
/* 132 */         if ((updated != obj) && (updated != null)) {
/* 133 */           return proxy.invoke(updated, args);
/*     */         }
/*     */ 
/* 137 */         if (methodName.equals("isOnline"))
/* 138 */           return Boolean.valueOf((injector.getSocket() != null) && (injector.getSocket().isConnected()));
/* 139 */         if (methodName.equals("getName")) {
/* 140 */           return "UNKNOWN[" + injector.getSocket().getRemoteSocketAddress() + "]";
/*     */         }
/*     */ 
/* 143 */         throw new UnsupportedOperationException("The method " + method.getName() + " is not supported for temporary players.");
/*     */       }
/*     */     };
/* 149 */     if (callbackFilter == null) {
/* 150 */       callbackFilter = new CallbackFilter()
/*     */       {
/*     */         public int accept(Method method)
/*     */         {
/* 154 */           if ((method.getDeclaringClass().equals(Object.class)) || (method.getDeclaringClass().equals(InjectorContainer.class)))
/*     */           {
/* 156 */             return 0;
/*     */           }
/* 158 */           return 1;
/*     */         }
/*     */ 
/*     */       };
/*     */     }
/*     */ 
/* 164 */     Enhancer ex = new Enhancer();
/* 165 */     ex.setSuperclass(InjectorContainer.class);
/* 166 */     ex.setInterfaces(new Class[] { Player.class });
/* 167 */     ex.setCallbacks(new Callback[] { NoOp.INSTANCE, implementation });
/* 168 */     ex.setCallbackFilter(callbackFilter);
/*     */ 
/* 170 */     return (Player)ex.create();
/*     */   }
/*     */ 
/*     */   public Player createTemporaryPlayer(Server server, SocketInjector injector)
/*     */   {
/* 180 */     Player temporary = createTemporaryPlayer(server);
/*     */ 
/* 182 */     ((InjectorContainer)temporary).setInjector(injector);
/* 183 */     return temporary;
/*     */   }
/*     */ 
/*     */   private Object sendMessage(SocketInjector injector, String message)
/*     */     throws InvocationTargetException, FieldAccessException
/*     */   {
/* 195 */     for (PacketContainer packet : ChatExtensions.createChatPackets(message)) {
/* 196 */       injector.sendServerPacket(packet.getHandle(), null, false);
/*     */     }
/* 198 */     return null;
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.injector.server.TemporaryPlayerFactory
 * JD-Core Version:    0.6.2
 */