/*     */ package com.comphenix.protocol.injector.player;
/*     */ 
/*     */ import com.comphenix.net.sf.cglib.proxy.Callback;
/*     */ import com.comphenix.net.sf.cglib.proxy.CallbackFilter;
/*     */ import com.comphenix.net.sf.cglib.proxy.Enhancer;
/*     */ import com.comphenix.net.sf.cglib.proxy.LazyLoader;
/*     */ import com.comphenix.net.sf.cglib.proxy.MethodInterceptor;
/*     */ import com.comphenix.net.sf.cglib.proxy.MethodProxy;
/*     */ import com.comphenix.protocol.concurrency.IntegerSet;
/*     */ import com.comphenix.protocol.error.ErrorReporter;
/*     */ import com.comphenix.protocol.events.ListeningWhitelist;
/*     */ import com.comphenix.protocol.events.NetworkMarker;
/*     */ import com.comphenix.protocol.events.PacketListener;
/*     */ import com.comphenix.protocol.injector.GamePhase;
/*     */ import com.comphenix.protocol.injector.ListenerInvoker;
/*     */ import com.comphenix.protocol.injector.PacketFilterManager.PlayerInjectHooks;
/*     */ import com.comphenix.protocol.injector.server.TemporaryPlayerFactory;
/*     */ import com.comphenix.protocol.reflect.VolatileField;
/*     */ import com.comphenix.protocol.utility.EnhancerFactory;
/*     */ import com.comphenix.protocol.utility.MinecraftVersion;
/*     */ import java.lang.reflect.Field;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.lang.reflect.Method;
/*     */ import java.util.Map;
/*     */ import org.bukkit.Server;
/*     */ import org.bukkit.entity.Player;
/*     */ 
/*     */ public class NetworkObjectInjector extends PlayerInjector
/*     */ {
/*     */   private IntegerSet sendingFilters;
/*  59 */   private MinecraftVersion safeVersion = new MinecraftVersion("1.4.4");
/*     */   private static volatile CallbackFilter callbackFilter;
/*     */   private static volatile TemporaryPlayerFactory tempPlayerFactory;
/*     */ 
/*     */   public NetworkObjectInjector(ErrorReporter reporter, Player player, ListenerInvoker invoker, IntegerSet sendingFilters)
/*     */     throws IllegalAccessException
/*     */   {
/*  80 */     super(reporter, player, invoker);
/*  81 */     this.sendingFilters = sendingFilters;
/*     */   }
/*     */ 
/*     */   protected boolean hasListener(int packetID)
/*     */   {
/*  86 */     return this.sendingFilters.contains(packetID);
/*     */   }
/*     */ 
/*     */   public Player createTemporaryPlayer(Server server)
/*     */   {
/*  95 */     if (tempPlayerFactory == null) {
/*  96 */       tempPlayerFactory = new TemporaryPlayerFactory();
/*     */     }
/*     */ 
/*  99 */     return tempPlayerFactory.createTemporaryPlayer(server, this);
/*     */   }
/*     */ 
/*     */   public void sendServerPacket(Object packet, NetworkMarker marker, boolean filtered) throws InvocationTargetException
/*     */   {
/* 104 */     Object networkDelegate = filtered ? this.networkManagerRef.getValue() : this.networkManagerRef.getOldValue();
/*     */ 
/* 106 */     if (networkDelegate != null)
/*     */       try {
/* 108 */         if (marker != null) {
/* 109 */           this.queuedMarkers.put(packet, marker);
/*     */         }
/*     */ 
/* 113 */         queueMethod.invoke(networkDelegate, new Object[] { packet });
/*     */       }
/*     */       catch (IllegalArgumentException e) {
/* 116 */         throw e;
/*     */       } catch (InvocationTargetException e) {
/* 118 */         throw e;
/*     */       } catch (IllegalAccessException e) {
/* 120 */         throw new IllegalStateException("Unable to access queue method.", e);
/*     */       }
/*     */     else
/* 123 */       throw new IllegalStateException("Unable to load network mananager. Cannot send packet.");
/*     */   }
/*     */ 
/*     */   public UnsupportedListener checkListener(MinecraftVersion version, PacketListener listener)
/*     */   {
/* 129 */     if ((version != null) && (version.compareTo(this.safeVersion) > 0)) {
/* 130 */       return null;
/*     */     }
/*     */ 
/* 134 */     int[] unsupported = { 51, 56 };
/*     */ 
/* 137 */     if (ListeningWhitelist.containsAny(listener.getSendingWhitelist(), unsupported)) {
/* 138 */       return new UnsupportedListener("The NETWORK_OBJECT_INJECTOR hook doesn't support map chunk listeners.", unsupported);
/*     */     }
/* 140 */     return null;
/*     */   }
/*     */ 
/*     */   public void injectManager()
/*     */   {
/* 148 */     if (this.networkManager != null) {
/* 149 */       Class networkInterface = this.networkManagerRef.getField().getType();
/* 150 */       final Object networkDelegate = this.networkManagerRef.getOldValue();
/*     */ 
/* 152 */       if (!networkInterface.isInterface()) {
/* 153 */         throw new UnsupportedOperationException("Must use CraftBukkit 1.3.0 or later to inject into into NetworkMananger.");
/*     */       }
/*     */ 
/* 157 */       Callback queueFilter = new MethodInterceptor()
/*     */       {
/*     */         public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
/* 160 */           Object packet = args[0];
/*     */ 
/* 162 */           if (packet != null) {
/* 163 */             packet = NetworkObjectInjector.this.handlePacketSending(packet);
/*     */ 
/* 166 */             if (packet != null)
/* 167 */               args[0] = packet;
/*     */             else {
/* 169 */               return null;
/*     */             }
/*     */           }
/*     */ 
/* 173 */           return proxy.invokeSuper(networkDelegate, args);
/*     */         }
/*     */       };
/* 176 */       Callback dispatch = new LazyLoader()
/*     */       {
/*     */         public Object loadObject() throws Exception {
/* 179 */           return networkDelegate;
/*     */         }
/*     */       };
/* 184 */       if (callbackFilter == null) {
/* 185 */         callbackFilter = new CallbackFilter()
/*     */         {
/*     */           public int accept(Method method) {
/* 188 */             if (method.equals(PlayerInjector.queueMethod)) {
/* 189 */               return 0;
/*     */             }
/* 191 */             return 1;
/*     */           }
/*     */ 
/*     */         };
/*     */       }
/*     */ 
/* 197 */       Enhancer ex = EnhancerFactory.getInstance().createEnhancer();
/* 198 */       ex.setSuperclass(networkInterface);
/* 199 */       ex.setCallbacks(new Callback[] { queueFilter, dispatch });
/* 200 */       ex.setCallbackFilter(callbackFilter);
/*     */ 
/* 203 */       this.networkManagerRef.setValue(ex.create());
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void cleanHook()
/*     */   {
/* 210 */     if ((this.networkManagerRef != null) && (this.networkManagerRef.isCurrentSet()))
/* 211 */       this.networkManagerRef.revertValue();
/*     */   }
/*     */ 
/*     */   public void handleDisconnect()
/*     */   {
/*     */   }
/*     */ 
/*     */   public boolean canInject(GamePhase phase)
/*     */   {
/* 223 */     return true;
/*     */   }
/*     */ 
/*     */   public PacketFilterManager.PlayerInjectHooks getHookType()
/*     */   {
/* 228 */     return PacketFilterManager.PlayerInjectHooks.NETWORK_MANAGER_OBJECT;
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.injector.player.NetworkObjectInjector
 * JD-Core Version:    0.6.2
 */