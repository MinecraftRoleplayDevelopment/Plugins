/*     */ package com.comphenix.protocol.injector.player;
/*     */ 
/*     */ import com.comphenix.net.sf.cglib.proxy.Factory;
/*     */ import com.comphenix.protocol.PacketType;
/*     */ import com.comphenix.protocol.PacketType.Sender;
/*     */ import com.comphenix.protocol.error.ErrorReporter;
/*     */ import com.comphenix.protocol.error.Report;
/*     */ import com.comphenix.protocol.error.Report.ReportBuilder;
/*     */ import com.comphenix.protocol.error.ReportType;
/*     */ import com.comphenix.protocol.events.NetworkMarker;
/*     */ import com.comphenix.protocol.events.PacketContainer;
/*     */ import com.comphenix.protocol.events.PacketEvent;
/*     */ import com.comphenix.protocol.events.PacketListener;
/*     */ import com.comphenix.protocol.injector.BukkitUnwrapper;
/*     */ import com.comphenix.protocol.injector.GamePhase;
/*     */ import com.comphenix.protocol.injector.ListenerInvoker;
/*     */ import com.comphenix.protocol.injector.PacketFilterManager.PlayerInjectHooks;
/*     */ import com.comphenix.protocol.injector.packet.InterceptWritePacket;
/*     */ import com.comphenix.protocol.injector.server.SocketInjector;
/*     */ import com.comphenix.protocol.reflect.FieldUtils;
/*     */ import com.comphenix.protocol.reflect.FuzzyReflection;
/*     */ import com.comphenix.protocol.reflect.StructureModifier;
/*     */ import com.comphenix.protocol.reflect.VolatileField;
/*     */ import com.comphenix.protocol.utility.MinecraftReflection;
/*     */ import com.comphenix.protocol.utility.MinecraftVersion;
/*     */ import com.google.common.collect.MapMaker;
/*     */ import java.io.DataInputStream;
/*     */ import java.io.IOException;
/*     */ import java.lang.reflect.Field;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.lang.reflect.Method;
/*     */ import java.net.Socket;
/*     */ import java.net.SocketAddress;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import org.bukkit.entity.Player;
/*     */ 
/*     */ public abstract class PlayerInjector
/*     */   implements SocketInjector
/*     */ {
/*  58 */   public static final ReportType REPORT_ASSUME_DISCONNECT_METHOD = new ReportType("Cannot find disconnect method by name. Assuming %s.");
/*  59 */   public static final ReportType REPORT_INVALID_ARGUMENT_DISCONNECT = new ReportType("Invalid argument passed to disconnect method: %s");
/*  60 */   public static final ReportType REPORT_CANNOT_ACCESS_DISCONNECT = new ReportType("Unable to access disconnect method.");
/*     */ 
/*  62 */   public static final ReportType REPORT_CANNOT_CLOSE_SOCKET = new ReportType("Unable to close socket.");
/*  63 */   public static final ReportType REPORT_ACCESS_DENIED_CLOSE_SOCKET = new ReportType("Insufficient permissions. Cannot close socket.");
/*     */ 
/*  65 */   public static final ReportType REPORT_DETECTED_CUSTOM_SERVER_HANDLER = new ReportType("Detected server handler proxy type by another plugin. Conflict may occur!");
/*     */ 
/*  67 */   public static final ReportType REPORT_CANNOT_PROXY_SERVER_HANDLER = new ReportType("Unable to load server handler from proxy type.");
/*     */ 
/*  69 */   public static final ReportType REPORT_CANNOT_UPDATE_PLAYER = new ReportType("Cannot update player in PlayerEvent.");
/*  70 */   public static final ReportType REPORT_CANNOT_HANDLE_PACKET = new ReportType("Cannot handle server packet.");
/*     */ 
/*  72 */   public static final ReportType REPORT_INVALID_NETWORK_MANAGER = new ReportType("NetworkManager doesn't appear to be valid.");
/*     */   private static Field netLoginNetworkField;
/*     */   private static Method loginDisconnect;
/*     */   private static Method serverDisconnect;
/*     */   protected static Field serverHandlerField;
/*     */   protected static Field proxyServerField;
/*     */   protected static Field networkManagerField;
/*     */   protected static Field netHandlerField;
/*     */   protected static Field socketField;
/*     */   protected static Field socketAddressField;
/*     */   private static Field inputField;
/*     */   private static Field entityPlayerField;
/*     */   private static boolean hasProxyType;
/*     */   protected static StructureModifier<Object> networkModifier;
/*     */   protected static Method queueMethod;
/*     */   protected static Method processMethod;
/*     */   protected volatile Player player;
/*     */   protected boolean hasInitialized;
/*     */   protected VolatileField networkManagerRef;
/*     */   protected VolatileField serverHandlerRef;
/*     */   protected Object networkManager;
/*     */   protected Object loginHandler;
/*     */   protected Object serverHandler;
/*     */   protected Object netHandler;
/*     */   protected Socket socket;
/*     */   protected SocketAddress socketAddress;
/*     */   protected ListenerInvoker invoker;
/*     */   protected DataInputStream cachedInput;
/*     */   protected ErrorReporter reporter;
/* 130 */   protected Map<Object, NetworkMarker> queuedMarkers = new MapMaker().weakKeys().makeMap();
/*     */   protected InterceptWritePacket writePacketInterceptor;
/*     */   private boolean clean;
/*     */   boolean updateOnLogin;
/*     */   volatile Player updatedPlayer;
/*     */ 
/*     */   public PlayerInjector(ErrorReporter reporter, Player player, ListenerInvoker invoker)
/*     */   {
/* 141 */     this.reporter = reporter;
/* 142 */     this.player = player;
/* 143 */     this.invoker = invoker;
/* 144 */     this.writePacketInterceptor = invoker.getInterceptWritePacket();
/*     */   }
/*     */ 
/*     */   protected Object getEntityPlayer(Player player)
/*     */   {
/* 153 */     BukkitUnwrapper unwrapper = new BukkitUnwrapper();
/* 154 */     return unwrapper.unwrapItem(player);
/*     */   }
/*     */ 
/*     */   public void initialize(Object injectionSource)
/*     */     throws IllegalAccessException
/*     */   {
/* 162 */     if (injectionSource == null) {
/* 163 */       throw new IllegalArgumentException("injectionSource cannot be NULL");
/*     */     }
/*     */ 
/* 166 */     if ((injectionSource instanceof Player))
/* 167 */       initializePlayer((Player)injectionSource);
/* 168 */     else if (MinecraftReflection.isLoginHandler(injectionSource))
/* 169 */       initializeLogin(injectionSource);
/*     */     else
/* 171 */       throw new IllegalArgumentException("Cannot initialize a player hook using a " + injectionSource.getClass().getName());
/*     */   }
/*     */ 
/*     */   public void initializePlayer(Player player)
/*     */   {
/* 179 */     Object notchEntity = getEntityPlayer(player);
/*     */ 
/* 182 */     this.player = player;
/*     */ 
/* 184 */     if (!this.hasInitialized)
/*     */     {
/* 186 */       this.hasInitialized = true;
/*     */ 
/* 189 */       if (serverHandlerField == null) {
/* 190 */         serverHandlerField = FuzzyReflection.fromObject(notchEntity).getFieldByType("NetServerHandler", MinecraftReflection.getNetServerHandlerClass());
/*     */ 
/* 192 */         proxyServerField = getProxyField(notchEntity, serverHandlerField);
/*     */       }
/*     */ 
/* 196 */       this.serverHandlerRef = new VolatileField(serverHandlerField, notchEntity);
/* 197 */       this.serverHandler = this.serverHandlerRef.getValue();
/*     */ 
/* 200 */       if (networkManagerField == null) {
/* 201 */         networkManagerField = FuzzyReflection.fromObject(this.serverHandler).getFieldByType("networkManager", MinecraftReflection.getNetworkManagerClass());
/*     */       }
/* 203 */       initializeNetworkManager(networkManagerField, this.serverHandler);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void initializeLogin(Object netLoginHandler)
/*     */   {
/* 212 */     if (!this.hasInitialized)
/*     */     {
/* 214 */       if (!MinecraftReflection.isLoginHandler(netLoginHandler)) {
/* 215 */         throw new IllegalArgumentException("netLoginHandler (" + netLoginHandler + ") is not a " + MinecraftReflection.getNetLoginHandlerName());
/*     */       }
/*     */ 
/* 218 */       this.hasInitialized = true;
/* 219 */       this.loginHandler = netLoginHandler;
/*     */ 
/* 221 */       if (netLoginNetworkField == null) {
/* 222 */         netLoginNetworkField = FuzzyReflection.fromObject(netLoginHandler).getFieldByType("networkManager", MinecraftReflection.getNetworkManagerClass());
/*     */       }
/* 224 */       initializeNetworkManager(netLoginNetworkField, netLoginHandler);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void initializeNetworkManager(Field reference, Object container) {
/* 229 */     this.networkManagerRef = new VolatileField(reference, container);
/* 230 */     this.networkManager = this.networkManagerRef.getValue();
/*     */ 
/* 233 */     if ((this.networkManager instanceof Factory)) {
/* 234 */       return;
/*     */     }
/*     */ 
/* 238 */     if ((this.networkManager != null) && (networkModifier == null)) {
/* 239 */       networkModifier = new StructureModifier(this.networkManager.getClass(), null, false);
/*     */     }
/*     */ 
/* 242 */     if (queueMethod == null)
/* 243 */       queueMethod = FuzzyReflection.fromClass(reference.getType()).getMethodByParameters("queue", new Class[] { MinecraftReflection.getPacketClass() });
/*     */   }
/*     */ 
/*     */   protected boolean hasProxyServerHandler()
/*     */   {
/* 252 */     return hasProxyType;
/*     */   }
/*     */ 
/*     */   public Object getNetworkManager()
/*     */   {
/* 260 */     return this.networkManagerRef.getValue();
/*     */   }
/*     */ 
/*     */   public Object getServerHandler()
/*     */   {
/* 268 */     return this.serverHandlerRef.getValue();
/*     */   }
/*     */ 
/*     */   public void setNetworkManager(Object value, boolean force)
/*     */   {
/* 277 */     this.networkManagerRef.setValue(value);
/*     */ 
/* 279 */     if (force)
/* 280 */       this.networkManagerRef.saveValue();
/* 281 */     initializeNetworkManager(networkManagerField, this.serverHandler);
/*     */   }
/*     */ 
/*     */   public Socket getSocket()
/*     */     throws IllegalAccessException
/*     */   {
/*     */     try
/*     */     {
/* 292 */       if (socketField == null) {
/* 293 */         socketField = (Field)FuzzyReflection.fromObject(this.networkManager, true).getFieldListByType(Socket.class).get(0);
/*     */       }
/* 295 */       if (this.socket == null)
/* 296 */         this.socket = ((Socket)FieldUtils.readField(socketField, this.networkManager, true));
/* 297 */       return this.socket;
/*     */     } catch (IndexOutOfBoundsException e) {
/*     */     }
/* 300 */     throw new IllegalAccessException("Unable to read the socket field.");
/*     */   }
/*     */ 
/*     */   public SocketAddress getAddress()
/*     */     throws IllegalAccessException
/*     */   {
/*     */     try
/*     */     {
/* 312 */       if (socketAddressField == null) {
/* 313 */         socketAddressField = (Field)FuzzyReflection.fromObject(this.networkManager, true).getFieldListByType(SocketAddress.class).get(0);
/*     */       }
/* 315 */       if (this.socketAddress == null)
/* 316 */         this.socketAddress = ((SocketAddress)FieldUtils.readField(socketAddressField, this.networkManager, true));
/* 317 */       return this.socketAddress;
/*     */     }
/*     */     catch (IndexOutOfBoundsException e)
/*     */     {
/* 321 */       this.reporter.reportWarning(this, Report.newBuilder(REPORT_INVALID_NETWORK_MANAGER).callerParam(new Object[] { this.networkManager }).build());
/*     */     }
/* 323 */     throw new IllegalAccessException("Unable to read the socket address field.");
/*     */   }
/*     */ 
/*     */   public void disconnect(String message)
/*     */     throws InvocationTargetException
/*     */   {
/* 335 */     boolean usingNetServer = this.serverHandler != null;
/*     */ 
/* 337 */     Object handler = usingNetServer ? this.serverHandler : this.loginHandler;
/* 338 */     Method disconnect = usingNetServer ? serverDisconnect : loginDisconnect;
/*     */ 
/* 341 */     if (handler != null) {
/* 342 */       if (disconnect == null) {
/*     */         try {
/* 344 */           disconnect = FuzzyReflection.fromObject(handler).getMethodByName("disconnect.*");
/*     */         }
/*     */         catch (IllegalArgumentException e) {
/* 347 */           disconnect = FuzzyReflection.fromObject(handler).getMethodByParameters("disconnect", new Class[] { String.class });
/* 348 */           this.reporter.reportWarning(this, Report.newBuilder(REPORT_ASSUME_DISCONNECT_METHOD).messageParam(new Object[] { disconnect }));
/*     */         }
/*     */ 
/* 352 */         if (usingNetServer)
/* 353 */           serverDisconnect = disconnect;
/*     */         else
/* 355 */           loginDisconnect = disconnect;
/*     */       }
/*     */       try
/*     */       {
/* 359 */         disconnect.invoke(handler, new Object[] { message });
/* 360 */         return;
/*     */       } catch (IllegalArgumentException e) {
/* 362 */         this.reporter.reportDetailed(this, Report.newBuilder(REPORT_INVALID_ARGUMENT_DISCONNECT).error(e).messageParam(new Object[] { message }).callerParam(new Object[] { handler }));
/*     */       } catch (IllegalAccessException e) {
/* 364 */         this.reporter.reportWarning(this, Report.newBuilder(REPORT_CANNOT_ACCESS_DISCONNECT).error(e));
/*     */       }
/*     */     }
/*     */ 
/*     */     try
/*     */     {
/* 370 */       Socket socket = getSocket();
/*     */       try
/*     */       {
/* 373 */         socket.close();
/*     */       } catch (IOException e) {
/* 375 */         this.reporter.reportDetailed(this, Report.newBuilder(REPORT_CANNOT_CLOSE_SOCKET).error(e).callerParam(new Object[] { socket }));
/*     */       }
/*     */     }
/*     */     catch (IllegalAccessException e) {
/* 379 */       this.reporter.reportWarning(this, Report.newBuilder(REPORT_ACCESS_DENIED_CLOSE_SOCKET).error(e));
/*     */     }
/*     */   }
/*     */ 
/*     */   private Field getProxyField(Object notchEntity, Field serverField) {
/*     */     try {
/* 385 */       Object currentHandler = FieldUtils.readField(serverHandlerField, notchEntity, true);
/*     */ 
/* 388 */       if (currentHandler == null) {
/* 389 */         throw new ServerHandlerNull();
/*     */       }
/*     */ 
/* 392 */       if (!isStandardMinecraftNetHandler(currentHandler))
/*     */       {
/* 394 */         if ((currentHandler instanceof Factory)) {
/* 395 */           return null;
/*     */         }
/* 397 */         hasProxyType = true;
/* 398 */         this.reporter.reportWarning(this, Report.newBuilder(REPORT_DETECTED_CUSTOM_SERVER_HANDLER).callerParam(new Object[] { serverField }));
/*     */         try
/*     */         {
/* 402 */           FuzzyReflection reflection = FuzzyReflection.fromObject(currentHandler, true);
/*     */ 
/* 405 */           return reflection.getFieldByType("NetServerHandler", MinecraftReflection.getNetServerHandlerClass());
/*     */         }
/*     */         catch (RuntimeException e)
/*     */         {
/*     */         }
/*     */       }
/*     */     }
/*     */     catch (IllegalAccessException e) {
/* 413 */       this.reporter.reportWarning(this, Report.newBuilder(REPORT_CANNOT_PROXY_SERVER_HANDLER).error(e).callerParam(new Object[] { notchEntity, serverField }));
/*     */     }
/*     */ 
/* 417 */     return null;
/*     */   }
/*     */ 
/*     */   private boolean isStandardMinecraftNetHandler(Object obj)
/*     */   {
/* 426 */     if (obj == null)
/* 427 */       return false;
/* 428 */     Class clazz = obj.getClass();
/*     */ 
/* 430 */     return (MinecraftReflection.getNetLoginHandlerClass().equals(clazz)) || (MinecraftReflection.getNetServerHandlerClass().equals(clazz));
/*     */   }
/*     */ 
/*     */   protected Object getNetHandler()
/*     */     throws IllegalAccessException
/*     */   {
/* 440 */     return getNetHandler(false);
/*     */   }
/*     */ 
/*     */   protected Object getNetHandler(boolean refresh)
/*     */     throws IllegalAccessException
/*     */   {
/*     */     try
/*     */     {
/* 451 */       if (netHandlerField == null) {
/* 452 */         netHandlerField = FuzzyReflection.fromClass(this.networkManager.getClass(), true).getFieldByType("NetHandler", MinecraftReflection.getNetHandlerClass());
/*     */       }
/*     */     }
/*     */     catch (RuntimeException e1)
/*     */     {
/*     */     }
/*     */ 
/* 459 */     if (netHandlerField == null) {
/*     */       try
/*     */       {
/* 462 */         netHandlerField = FuzzyReflection.fromClass(this.networkManager.getClass(), true).getFieldByType(MinecraftReflection.getMinecraftObjectRegex());
/*     */       }
/*     */       catch (RuntimeException e2)
/*     */       {
/* 466 */         throw new IllegalAccessException("Cannot locate net handler. " + e2.getMessage());
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 471 */     if ((this.netHandler == null) || (refresh))
/* 472 */       this.netHandler = FieldUtils.readField(netHandlerField, this.networkManager, true);
/* 473 */     return this.netHandler;
/*     */   }
/*     */ 
/*     */   private Object getEntityPlayer(Object netHandler)
/*     */     throws IllegalAccessException
/*     */   {
/* 483 */     if (entityPlayerField == null) {
/* 484 */       entityPlayerField = FuzzyReflection.fromObject(netHandler).getFieldByType("EntityPlayer", MinecraftReflection.getEntityPlayerClass());
/*     */     }
/* 486 */     return FieldUtils.readField(entityPlayerField, netHandler);
/*     */   }
/*     */ 
/*     */   public void processPacket(Object packet)
/*     */     throws IllegalAccessException, InvocationTargetException
/*     */   {
/* 497 */     Object netHandler = getNetHandler();
/*     */ 
/* 500 */     if (processMethod == null) {
/*     */       try {
/* 502 */         processMethod = FuzzyReflection.fromClass(MinecraftReflection.getPacketClass()).getMethodByParameters("processPacket", new Class[] { netHandlerField.getType() });
/*     */       }
/*     */       catch (RuntimeException e) {
/* 505 */         throw new IllegalArgumentException("Cannot locate process packet method: " + e.getMessage());
/*     */       }
/*     */     }
/*     */ 
/*     */     try
/*     */     {
/* 511 */       processMethod.invoke(packet, new Object[] { netHandler });
/*     */     } catch (IllegalArgumentException e) {
/* 513 */       throw new IllegalArgumentException("Method " + processMethod.getName() + " is not compatible.");
/*     */     } catch (InvocationTargetException e) {
/* 515 */       throw e;
/*     */     }
/*     */   }
/*     */ 
/*     */   public abstract void sendServerPacket(Object paramObject, NetworkMarker paramNetworkMarker, boolean paramBoolean)
/*     */     throws InvocationTargetException;
/*     */ 
/*     */   public abstract void injectManager();
/*     */ 
/*     */   public final void cleanupAll()
/*     */   {
/* 538 */     if (!this.clean) {
/* 539 */       cleanHook();
/* 540 */       this.writePacketInterceptor.cleanup();
/*     */     }
/* 542 */     this.clean = true;
/*     */   }
/*     */ 
/*     */   public abstract void handleDisconnect();
/*     */ 
/*     */   protected abstract void cleanHook();
/*     */ 
/*     */   public boolean isClean()
/*     */   {
/* 560 */     return this.clean;
/*     */   }
/*     */ 
/*     */   public abstract boolean canInject(GamePhase paramGamePhase);
/*     */ 
/*     */   public abstract PacketFilterManager.PlayerInjectHooks getHookType();
/*     */ 
/*     */   public abstract UnsupportedListener checkListener(MinecraftVersion paramMinecraftVersion, PacketListener paramPacketListener);
/*     */ 
/*     */   public Object handlePacketSending(Object packet)
/*     */   {
/*     */     try
/*     */     {
/* 594 */       Integer id = Integer.valueOf(this.invoker.getPacketID(packet));
/* 595 */       Player currentPlayer = this.player;
/*     */ 
/* 598 */       if (this.updateOnLogin) {
/* 599 */         if (this.updatedPlayer == null) {
/*     */           try {
/* 601 */             Object handler = getNetHandler(true);
/*     */ 
/* 604 */             if (MinecraftReflection.getNetServerHandlerClass().isAssignableFrom(handler.getClass())) {
/* 605 */               setUpdatedPlayer((Player)MinecraftReflection.getBukkitEntity(getEntityPlayer(handler)));
/*     */             }
/*     */           }
/*     */           catch (IllegalAccessException e)
/*     */           {
/* 610 */             this.reporter.reportDetailed(this, Report.newBuilder(REPORT_CANNOT_UPDATE_PLAYER).error(e).callerParam(new Object[] { packet }));
/*     */           }
/*     */ 
/*     */         }
/*     */ 
/* 615 */         if (this.updatedPlayer != null) {
/* 616 */           currentPlayer = this.updatedPlayer;
/* 617 */           this.updateOnLogin = false;
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 622 */       if ((id != null) && (hasListener(id.intValue()))) {
/* 623 */         NetworkMarker marker = (NetworkMarker)this.queuedMarkers.remove(packet);
/*     */ 
/* 626 */         PacketType type = PacketType.findLegacy(id.intValue(), PacketType.Sender.SERVER);
/* 627 */         PacketContainer container = new PacketContainer(type, packet);
/* 628 */         PacketEvent event = PacketEvent.fromServer(this.invoker, container, marker, currentPlayer);
/* 629 */         this.invoker.invokePacketSending(event);
/*     */ 
/* 632 */         if (event.isCancelled()) {
/* 633 */           return null;
/*     */         }
/*     */ 
/* 636 */         Object result = event.getPacket().getHandle();
/* 637 */         marker = NetworkMarker.getNetworkMarker(event);
/*     */ 
/* 640 */         if ((result != null) && ((NetworkMarker.hasOutputHandlers(marker)) || (NetworkMarker.hasPostListeners(marker))));
/* 641 */         return this.writePacketInterceptor.constructProxy(result, event, marker);
/*     */       }
/*     */ 
/*     */     }
/*     */     catch (OutOfMemoryError e)
/*     */     {
/* 647 */       throw e;
/*     */     } catch (ThreadDeath e) {
/* 649 */       throw e;
/*     */     } catch (Throwable e) {
/* 651 */       this.reporter.reportDetailed(this, Report.newBuilder(REPORT_CANNOT_HANDLE_PACKET).error(e).callerParam(new Object[] { packet }));
/*     */     }
/*     */ 
/* 654 */     return packet;
/*     */   }
/*     */ 
/*     */   protected abstract boolean hasListener(int paramInt);
/*     */ 
/*     */   public DataInputStream getInputStream(boolean cache)
/*     */   {
/* 671 */     if (this.networkManager == null)
/* 672 */       throw new IllegalStateException("Network manager is NULL.");
/* 673 */     if (inputField == null) {
/* 674 */       inputField = FuzzyReflection.fromObject(this.networkManager, true).getFieldByType("java\\.io\\.DataInputStream");
/*     */     }
/*     */ 
/*     */     try
/*     */     {
/* 679 */       if ((cache) && (this.cachedInput != null)) {
/* 680 */         return this.cachedInput;
/*     */       }
/*     */ 
/* 683 */       this.cachedInput = ((DataInputStream)FieldUtils.readField(inputField, this.networkManager, true));
/* 684 */       return this.cachedInput;
/*     */     }
/*     */     catch (IllegalAccessException e) {
/* 687 */       throw new RuntimeException("Unable to read input stream.", e);
/*     */     }
/*     */   }
/*     */ 
/*     */   public Player getPlayer()
/*     */   {
/* 696 */     return this.player;
/*     */   }
/*     */ 
/*     */   public void setPlayer(Player player)
/*     */   {
/* 706 */     this.player = player;
/*     */   }
/*     */ 
/*     */   public ListenerInvoker getInvoker()
/*     */   {
/* 714 */     return this.invoker;
/*     */   }
/*     */ 
/*     */   public Player getUpdatedPlayer()
/*     */   {
/* 723 */     if (this.updatedPlayer != null) {
/* 724 */       return this.updatedPlayer;
/*     */     }
/* 726 */     return this.player;
/*     */   }
/*     */ 
/*     */   public void transferState(SocketInjector delegate)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void setUpdatedPlayer(Player updatedPlayer)
/*     */   {
/* 736 */     this.updatedPlayer = updatedPlayer;
/*     */   }
/*     */ 
/*     */   public static class ServerHandlerNull extends IllegalAccessError
/*     */   {
/*     */     private static final long serialVersionUID = 1L;
/*     */ 
/*     */     public ServerHandlerNull()
/*     */     {
/* 749 */       super();
/*     */     }
/*     */ 
/*     */     public ServerHandlerNull(String s) {
/* 753 */       super();
/*     */     }
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.injector.player.PlayerInjector
 * JD-Core Version:    0.6.2
 */