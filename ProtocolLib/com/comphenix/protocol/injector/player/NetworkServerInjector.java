/*     */ package com.comphenix.protocol.injector.player;
/*     */ 
/*     */ import com.comphenix.net.sf.cglib.proxy.Callback;
/*     */ import com.comphenix.net.sf.cglib.proxy.CallbackFilter;
/*     */ import com.comphenix.net.sf.cglib.proxy.Enhancer;
/*     */ import com.comphenix.net.sf.cglib.proxy.Factory;
/*     */ import com.comphenix.net.sf.cglib.proxy.MethodInterceptor;
/*     */ import com.comphenix.net.sf.cglib.proxy.MethodProxy;
/*     */ import com.comphenix.net.sf.cglib.proxy.NoOp;
/*     */ import com.comphenix.protocol.concurrency.IntegerSet;
/*     */ import com.comphenix.protocol.error.ErrorReporter;
/*     */ import com.comphenix.protocol.error.Report;
/*     */ import com.comphenix.protocol.error.Report.ReportBuilder;
/*     */ import com.comphenix.protocol.error.ReportType;
/*     */ import com.comphenix.protocol.events.NetworkMarker;
/*     */ import com.comphenix.protocol.events.PacketListener;
/*     */ import com.comphenix.protocol.injector.GamePhase;
/*     */ import com.comphenix.protocol.injector.ListenerInvoker;
/*     */ import com.comphenix.protocol.injector.PacketFilterManager.PlayerInjectHooks;
/*     */ import com.comphenix.protocol.reflect.FieldUtils;
/*     */ import com.comphenix.protocol.reflect.FuzzyReflection;
/*     */ import com.comphenix.protocol.reflect.ObjectWriter;
/*     */ import com.comphenix.protocol.reflect.VolatileField;
/*     */ import com.comphenix.protocol.reflect.instances.DefaultInstances;
/*     */ import com.comphenix.protocol.reflect.instances.ExistingGenerator;
/*     */ import com.comphenix.protocol.reflect.instances.InstanceProvider;
/*     */ import com.comphenix.protocol.utility.EnhancerFactory;
/*     */ import com.comphenix.protocol.utility.MinecraftMethods;
/*     */ import com.comphenix.protocol.utility.MinecraftReflection;
/*     */ import com.comphenix.protocol.utility.MinecraftVersion;
/*     */ import java.lang.reflect.Field;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.lang.reflect.Method;
/*     */ import java.util.Arrays;
/*     */ import java.util.Map;
/*     */ import org.bukkit.entity.Player;
/*     */ 
/*     */ class NetworkServerInjector extends PlayerInjector
/*     */ {
/*  62 */   public static final ReportType REPORT_ASSUMING_DISCONNECT_FIELD = new ReportType("Unable to find 'disconnected' field. Assuming %s.");
/*  63 */   public static final ReportType REPORT_DISCONNECT_FIELD_MISSING = new ReportType("Cannot find disconnected field. Is ProtocolLib up to date?");
/*  64 */   public static final ReportType REPORT_DISCONNECT_FIELD_FAILURE = new ReportType("Unable to update disconnected field. Player quit event may be sent twice.");
/*     */   private static volatile CallbackFilter callbackFilter;
/*     */   private static volatile boolean foundSendPacket;
/*     */   private static volatile Field disconnectField;
/*     */   private InjectedServerConnection serverInjection;
/*     */   private IntegerSet sendingFilters;
/*     */   private boolean hasDisconnected;
/*  79 */   private final ObjectWriter writer = new ObjectWriter();
/*     */ 
/*     */   public NetworkServerInjector(ErrorReporter reporter, Player player, ListenerInvoker invoker, IntegerSet sendingFilters, InjectedServerConnection serverInjection)
/*     */   {
/*  85 */     super(reporter, player, invoker);
/*  86 */     this.sendingFilters = sendingFilters;
/*  87 */     this.serverInjection = serverInjection;
/*     */   }
/*     */ 
/*     */   protected boolean hasListener(int packetID)
/*     */   {
/*  92 */     return this.sendingFilters.contains(packetID);
/*     */   }
/*     */ 
/*     */   public void sendServerPacket(Object packet, NetworkMarker marker, boolean filtered) throws InvocationTargetException
/*     */   {
/*  97 */     Object serverDelegate = filtered ? this.serverHandlerRef.getValue() : this.serverHandlerRef.getOldValue();
/*     */ 
/*  99 */     if (serverDelegate != null)
/*     */       try {
/* 101 */         if (marker != null) {
/* 102 */           this.queuedMarkers.put(packet, marker);
/*     */         }
/*     */ 
/* 106 */         MinecraftMethods.getSendPacketMethod().invoke(serverDelegate, new Object[] { packet });
/*     */       }
/*     */       catch (IllegalArgumentException e) {
/* 109 */         throw e;
/*     */       } catch (InvocationTargetException e) {
/* 111 */         throw e;
/*     */       } catch (IllegalAccessException e) {
/* 113 */         throw new IllegalStateException("Unable to access send packet method.", e);
/*     */       }
/*     */     else
/* 116 */       throw new IllegalStateException("Unable to load server handler. Cannot send packet.");
/*     */   }
/*     */ 
/*     */   public void injectManager()
/*     */   {
/* 122 */     if (this.serverHandlerRef == null) {
/* 123 */       throw new IllegalStateException("Cannot find server handler.");
/*     */     }
/* 125 */     if ((this.serverHandlerRef.getValue() instanceof Factory)) {
/* 126 */       return;
/*     */     }
/* 128 */     if (!tryInjectManager()) {
/* 129 */       Class serverHandlerClass = MinecraftReflection.getNetServerHandlerClass();
/*     */ 
/* 132 */       if (proxyServerField != null) {
/* 133 */         this.serverHandlerRef = new VolatileField(proxyServerField, this.serverHandler, true);
/* 134 */         this.serverHandler = this.serverHandlerRef.getValue();
/*     */ 
/* 136 */         if (this.serverHandler == null) {
/* 137 */           throw new RuntimeException("Cannot hook player: Inner proxy object is NULL.");
/*     */         }
/* 139 */         serverHandlerClass = this.serverHandler.getClass();
/*     */ 
/* 142 */         if (tryInjectManager())
/*     */         {
/* 144 */           return;
/*     */         }
/*     */       }
/*     */ 
/* 148 */       throw new RuntimeException("Cannot hook player: Unable to find a valid constructor for the " + serverHandlerClass.getName() + " object.");
/*     */     }
/*     */   }
/*     */ 
/*     */   private boolean tryInjectManager()
/*     */   {
/* 155 */     Class serverClass = this.serverHandler.getClass();
/*     */ 
/* 157 */     Enhancer ex = EnhancerFactory.getInstance().createEnhancer();
/* 158 */     Callback sendPacketCallback = new MethodInterceptor()
/*     */     {
/*     */       public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
/* 161 */         Object packet = args[0];
/*     */ 
/* 163 */         if (packet != null) {
/* 164 */           packet = NetworkServerInjector.this.handlePacketSending(packet);
/*     */ 
/* 167 */           if (packet != null)
/* 168 */             args[0] = packet;
/*     */           else {
/* 170 */             return null;
/*     */           }
/*     */         }
/*     */ 
/* 174 */         return proxy.invokeSuper(obj, args);
/*     */       }
/*     */     };
/* 177 */     Callback noOpCallback = NoOp.INSTANCE;
/*     */ 
/* 181 */     if (callbackFilter == null) {
/* 182 */       callbackFilter = new SendMethodFilter(null);
/*     */     }
/*     */ 
/* 185 */     ex.setSuperclass(serverClass);
/* 186 */     ex.setCallbacks(new Callback[] { sendPacketCallback, noOpCallback });
/* 187 */     ex.setCallbackFilter(callbackFilter);
/*     */ 
/* 190 */     Class minecraftSuperClass = getFirstMinecraftSuperClass(this.serverHandler.getClass());
/* 191 */     ExistingGenerator generator = ExistingGenerator.fromObjectFields(this.serverHandler, minecraftSuperClass);
/* 192 */     DefaultInstances serverInstances = null;
/*     */ 
/* 195 */     Object proxyInstance = getProxyServerHandler();
/*     */ 
/* 198 */     if ((proxyInstance != null) && (proxyInstance != this.serverHandler)) {
/* 199 */       serverInstances = DefaultInstances.fromArray(new InstanceProvider[] { generator, ExistingGenerator.fromObjectArray(new Object[] { proxyInstance }) });
/*     */     }
/*     */     else {
/* 202 */       serverInstances = DefaultInstances.fromArray(new InstanceProvider[] { generator });
/*     */     }
/*     */ 
/* 205 */     serverInstances.setNonNull(true);
/* 206 */     serverInstances.setMaximumRecursion(1);
/*     */ 
/* 208 */     Object proxyObject = serverInstances.forEnhancer(ex).getDefault(serverClass);
/*     */ 
/* 211 */     if (proxyObject != null)
/*     */     {
/* 213 */       if (!foundSendPacket) {
/* 214 */         throw new IllegalArgumentException("Unable to find a sendPacket method in " + serverClass);
/*     */       }
/*     */ 
/* 217 */       this.serverInjection.replaceServerHandler(this.serverHandler, proxyObject);
/* 218 */       this.serverHandlerRef.setValue(proxyObject);
/* 219 */       return true;
/*     */     }
/* 221 */     return false;
/*     */   }
/*     */ 
/*     */   private Object getProxyServerHandler()
/*     */   {
/* 226 */     if ((proxyServerField != null) && (!proxyServerField.equals(this.serverHandlerRef.getField()))) {
/*     */       try {
/* 228 */         return FieldUtils.readField(proxyServerField, this.serverHandler, true);
/*     */       } catch (OutOfMemoryError e) {
/* 230 */         throw e;
/*     */       } catch (ThreadDeath e) {
/* 232 */         throw e;
/*     */       }
/*     */       catch (Throwable e)
/*     */       {
/*     */       }
/*     */     }
/* 238 */     return null;
/*     */   }
/*     */ 
/*     */   private Class<?> getFirstMinecraftSuperClass(Class<?> clazz) {
/* 242 */     if (MinecraftReflection.isMinecraftClass(clazz))
/* 243 */       return clazz;
/* 244 */     if (clazz.equals(Object.class)) {
/* 245 */       return clazz;
/*     */     }
/* 247 */     return getFirstMinecraftSuperClass(clazz.getSuperclass());
/*     */   }
/*     */ 
/*     */   protected void cleanHook()
/*     */   {
/* 252 */     if ((this.serverHandlerRef != null) && (this.serverHandlerRef.isCurrentSet())) {
/* 253 */       this.writer.copyTo(this.serverHandlerRef.getValue(), this.serverHandlerRef.getOldValue(), this.serverHandler.getClass());
/* 254 */       this.serverHandlerRef.revertValue();
/*     */       try
/*     */       {
/* 257 */         if (getNetHandler() != null)
/*     */           try
/*     */           {
/* 260 */             FieldUtils.writeField(netHandlerField, this.networkManager, this.serverHandlerRef.getOldValue(), true);
/*     */           }
/*     */           catch (IllegalAccessException e) {
/* 263 */             e.printStackTrace();
/*     */           }
/*     */       }
/*     */       catch (IllegalAccessException e) {
/* 267 */         e.printStackTrace();
/*     */       }
/*     */ 
/* 271 */       if (this.hasDisconnected) {
/* 272 */         setDisconnect(this.serverHandlerRef.getValue(), true);
/*     */       }
/*     */     }
/*     */ 
/* 276 */     this.serverInjection.revertServerHandler(this.serverHandler);
/*     */   }
/*     */ 
/*     */   public void handleDisconnect()
/*     */   {
/* 281 */     this.hasDisconnected = true;
/*     */   }
/*     */ 
/*     */   private void setDisconnect(Object handler, boolean value)
/*     */   {
/*     */     try
/*     */     {
/* 293 */       if (disconnectField == null) {
/* 294 */         disconnectField = FuzzyReflection.fromObject(handler).getFieldByName("disconnected.*");
/*     */       }
/* 296 */       FieldUtils.writeField(disconnectField, handler, Boolean.valueOf(value));
/*     */     }
/*     */     catch (IllegalArgumentException e)
/*     */     {
/* 300 */       if (disconnectField == null) {
/* 301 */         disconnectField = FuzzyReflection.fromObject(handler).getFieldByType("disconnected", Boolean.TYPE);
/* 302 */         this.reporter.reportWarning(this, Report.newBuilder(REPORT_ASSUMING_DISCONNECT_FIELD).messageParam(new Object[] { disconnectField }));
/*     */ 
/* 305 */         if (disconnectField != null) {
/* 306 */           setDisconnect(handler, value);
/* 307 */           return;
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 312 */       this.reporter.reportDetailed(this, Report.newBuilder(REPORT_DISCONNECT_FIELD_MISSING).error(e));
/*     */     }
/*     */     catch (IllegalAccessException e) {
/* 315 */       this.reporter.reportWarning(this, Report.newBuilder(REPORT_DISCONNECT_FIELD_FAILURE).error(e));
/*     */     }
/*     */   }
/*     */ 
/*     */   public UnsupportedListener checkListener(MinecraftVersion version, PacketListener listener)
/*     */   {
/* 322 */     return null;
/*     */   }
/*     */ 
/*     */   public boolean canInject(GamePhase phase)
/*     */   {
/* 328 */     return phase == GamePhase.PLAYING;
/*     */   }
/*     */ 
/*     */   public PacketFilterManager.PlayerInjectHooks getHookType()
/*     */   {
/* 333 */     return PacketFilterManager.PlayerInjectHooks.NETWORK_SERVER_OBJECT;
/*     */   }
/*     */ 
/*     */   private static class SendMethodFilter
/*     */     implements CallbackFilter
/*     */   {
/* 341 */     private Method sendPacket = MinecraftMethods.getSendPacketMethod();
/*     */ 
/*     */     public int accept(Method method)
/*     */     {
/* 345 */       if (isCallableEqual(this.sendPacket, method)) {
/* 346 */         NetworkServerInjector.access$102(true);
/* 347 */         return 0;
/*     */       }
/* 349 */       return 1;
/*     */     }
/*     */ 
/*     */     private boolean isCallableEqual(Method first, Method second)
/*     */     {
/* 362 */       return (first.getName().equals(second.getName())) && (first.getReturnType().equals(second.getReturnType())) && (Arrays.equals(first.getParameterTypes(), second.getParameterTypes()));
/*     */     }
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.injector.player.NetworkServerInjector
 * JD-Core Version:    0.6.2
 */