/*     */ package com.comphenix.protocol.injector.spigot;
/*     */ 
/*     */ import com.comphenix.net.sf.cglib.proxy.Callback;
/*     */ import com.comphenix.net.sf.cglib.proxy.CallbackFilter;
/*     */ import com.comphenix.net.sf.cglib.proxy.Enhancer;
/*     */ import com.comphenix.net.sf.cglib.proxy.Factory;
/*     */ import com.comphenix.net.sf.cglib.proxy.MethodInterceptor;
/*     */ import com.comphenix.net.sf.cglib.proxy.MethodProxy;
/*     */ import com.comphenix.net.sf.cglib.proxy.NoOp;
/*     */ import com.comphenix.protocol.PacketType;
/*     */ import com.comphenix.protocol.PacketType.Sender;
/*     */ import com.comphenix.protocol.concurrency.PacketTypeSet;
/*     */ import com.comphenix.protocol.error.DelegatedErrorReporter;
/*     */ import com.comphenix.protocol.error.ErrorReporter;
/*     */ import com.comphenix.protocol.error.Report;
/*     */ import com.comphenix.protocol.error.Report.ReportBuilder;
/*     */ import com.comphenix.protocol.error.ReportType;
/*     */ import com.comphenix.protocol.events.ConnectionSide;
/*     */ import com.comphenix.protocol.events.NetworkMarker;
/*     */ import com.comphenix.protocol.events.PacketContainer;
/*     */ import com.comphenix.protocol.events.PacketEvent;
/*     */ import com.comphenix.protocol.injector.ListenerInvoker;
/*     */ import com.comphenix.protocol.injector.PlayerLoggedOutException;
/*     */ import com.comphenix.protocol.injector.packet.LegacyNetworkMarker;
/*     */ import com.comphenix.protocol.injector.packet.PacketInjector;
/*     */ import com.comphenix.protocol.injector.player.NetworkObjectInjector;
/*     */ import com.comphenix.protocol.injector.player.PlayerInjectionHandler;
/*     */ import com.comphenix.protocol.reflect.FieldUtils;
/*     */ import com.comphenix.protocol.reflect.FuzzyReflection;
/*     */ import com.comphenix.protocol.reflect.MethodInfo;
/*     */ import com.comphenix.protocol.reflect.fuzzy.FuzzyMethodContract;
/*     */ import com.comphenix.protocol.reflect.fuzzy.FuzzyMethodContract.Builder;
/*     */ import com.comphenix.protocol.utility.EnhancerFactory;
/*     */ import com.comphenix.protocol.utility.MinecraftReflection;
/*     */ import com.google.common.collect.Iterables;
/*     */ import com.google.common.collect.Lists;
/*     */ import com.google.common.collect.MapMaker;
/*     */ import com.google.common.collect.Maps;
/*     */ import java.lang.reflect.Field;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.lang.reflect.Method;
/*     */ import java.net.Socket;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.concurrent.ConcurrentMap;
/*     */ import org.bukkit.Bukkit;
/*     */ import org.bukkit.Server;
/*     */ import org.bukkit.entity.Player;
/*     */ import org.bukkit.plugin.Plugin;
/*     */ import org.bukkit.scheduler.BukkitScheduler;
/*     */ 
/*     */ public class SpigotPacketInjector
/*     */   implements SpigotPacketListener
/*     */ {
/*  60 */   public static final ReportType REPORT_CANNOT_CLEANUP_SPIGOT = new ReportType("Cannot cleanup Spigot listener.");
/*     */   private static volatile Class<?> spigotListenerClass;
/*     */   private static volatile boolean classChecked;
/*     */   private static volatile Field playerConnectionPlayer;
/*  70 */   private Set<Object> ignoredPackets = Collections.newSetFromMap(new MapMaker().weakKeys().makeMap());
/*     */   private static final int CLEANUP_DELAY = 100;
/*     */   private Object dynamicListener;
/*     */   private Plugin plugin;
/*     */   private PacketTypeSet queuedFilters;
/*     */   private PacketTypeSet reveivedFilters;
/*  90 */   private ConcurrentMap<Object, NetworkObjectInjector> networkManagerInjector = Maps.newConcurrentMap();
/*     */ 
/*  93 */   private ConcurrentMap<Player, NetworkObjectInjector> playerInjector = Maps.newConcurrentMap();
/*     */ 
/*  96 */   private Map<Object, byte[]> readBufferedPackets = new MapMaker().weakKeys().makeMap();
/*     */   private ListenerInvoker invoker;
/*     */   private ErrorReporter reporter;
/*     */   private Server server;
/*     */   private PacketInjector proxyPacketInjector;
/*     */   private static final int BACKGROUND_DELAY = 600;
/*     */   private int backgroundId;
/*     */ 
/*     */   public SpigotPacketInjector(ErrorReporter reporter, ListenerInvoker invoker, Server server)
/*     */   {
/* 114 */     this.reporter = reporter;
/* 115 */     this.invoker = invoker;
/* 116 */     this.server = server;
/* 117 */     this.queuedFilters = new PacketTypeSet();
/* 118 */     this.reveivedFilters = new PacketTypeSet();
/*     */   }
/*     */ 
/*     */   public ListenerInvoker getInvoker()
/*     */   {
/* 126 */     return this.invoker;
/*     */   }
/*     */ 
/*     */   public void setProxyPacketInjector(PacketInjector proxyPacketInjector)
/*     */   {
/* 134 */     this.proxyPacketInjector = proxyPacketInjector;
/*     */   }
/*     */ 
/*     */   public PacketInjector getProxyPacketInjector()
/*     */   {
/* 142 */     return this.proxyPacketInjector;
/*     */   }
/*     */ 
/*     */   private static Class<?> getSpigotListenerClass()
/*     */   {
/* 150 */     if (!classChecked) {
/*     */       try {
/* 152 */         spigotListenerClass = SpigotPacketInjector.class.getClassLoader().loadClass("org.spigotmc.netty.PacketListener");
/*     */       } catch (ClassNotFoundException e) {
/* 154 */         return null;
/*     */       }
/*     */       finally {
/* 157 */         classChecked = true;
/*     */       }
/*     */     }
/* 160 */     return spigotListenerClass;
/*     */   }
/*     */ 
/*     */   private static Method getRegisterMethod()
/*     */   {
/* 168 */     Class clazz = getSpigotListenerClass();
/*     */ 
/* 170 */     if (clazz != null) {
/*     */       try {
/* 172 */         return clazz.getMethod("register", new Class[] { clazz, Plugin.class });
/*     */       }
/*     */       catch (SecurityException e) {
/* 175 */         throw new RuntimeException("Reflection is not allowed.", e);
/*     */       } catch (NoSuchMethodException e) {
/* 177 */         throw new IllegalStateException("Cannot find register() method in " + clazz, e);
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 182 */     throw new IllegalStateException("Spigot could not be found!");
/*     */   }
/*     */ 
/*     */   public static boolean canUseSpigotListener()
/*     */   {
/* 190 */     return getSpigotListenerClass() != null;
/*     */   }
/*     */ 
/*     */   public boolean register(Plugin plugin)
/*     */   {
/* 199 */     if (hasRegistered()) {
/* 200 */       return false;
/*     */     }
/*     */ 
/* 203 */     this.plugin = plugin;
/*     */ 
/* 205 */     Callback[] callbacks = new Callback[3];
/* 206 */     final boolean[] found = new boolean[3];
/*     */ 
/* 209 */     callbacks[0] = new MethodInterceptor()
/*     */     {
/*     */       public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
/* 212 */         return SpigotPacketInjector.this.packetReceived(args[0], args[1], args[2]);
/*     */       }
/*     */     };
/* 216 */     callbacks[1] = new MethodInterceptor()
/*     */     {
/*     */       public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
/* 219 */         return SpigotPacketInjector.this.packetQueued(args[0], args[1], args[2]);
/*     */       }
/*     */     };
/* 224 */     callbacks[2] = NoOp.INSTANCE;
/*     */ 
/* 226 */     Enhancer enhancer = EnhancerFactory.getInstance().createEnhancer();
/* 227 */     enhancer.setSuperclass(getSpigotListenerClass());
/* 228 */     enhancer.setCallbacks(callbacks);
/* 229 */     enhancer.setCallbackFilter(new CallbackFilter()
/*     */     {
/*     */       public int accept(Method method)
/*     */       {
/* 233 */         if (SpigotPacketInjector.this.matchMethod("packetReceived", method)) {
/* 234 */           found[0] = true;
/* 235 */           return 0;
/* 236 */         }if (SpigotPacketInjector.this.matchMethod("packetQueued", method)) {
/* 237 */           found[1] = true;
/* 238 */           return 1;
/*     */         }
/* 240 */         found[2] = true;
/* 241 */         return 2;
/*     */       }
/*     */     });
/* 245 */     this.dynamicListener = enhancer.create();
/*     */ 
/* 248 */     if (found[0] == 0)
/* 249 */       throw new IllegalStateException("Unable to find a valid packet receiver in Spigot.");
/* 250 */     if (found[1] == 0) {
/* 251 */       throw new IllegalStateException("Unable to find a valid packet queue in Spigot.");
/*     */     }
/*     */     try
/*     */     {
/* 255 */       getRegisterMethod().invoke(null, new Object[] { this.dynamicListener, plugin });
/*     */     } catch (Exception e) {
/* 257 */       throw new RuntimeException("Cannot register Spigot packet listener.", e);
/*     */     }
/*     */ 
/* 261 */     this.backgroundId = createBackgroundTask();
/*     */ 
/* 264 */     return true;
/*     */   }
/*     */ 
/*     */   private int createBackgroundTask()
/*     */   {
/* 272 */     return Bukkit.getScheduler().scheduleSyncRepeatingTask(this.plugin, new Runnable()
/*     */     {
/*     */       public void run() {
/* 275 */         SpigotPacketInjector.this.cleanupInjectors();
/*     */       }
/*     */     }
/*     */     , 600L, 600L);
/*     */   }
/*     */ 
/*     */   private void cleanupInjectors()
/*     */   {
/* 284 */     for (NetworkObjectInjector injector : this.networkManagerInjector.values())
/*     */       try {
/* 286 */         if ((injector.getSocket() != null) && (injector.getSocket().isClosed()))
/* 287 */           cleanupInjector(injector);
/*     */       }
/*     */       catch (Exception e) {
/* 290 */         this.reporter.reportMinimal(this.plugin, "cleanupInjectors", e);
/*     */ 
/* 293 */         cleanupInjector(injector);
/*     */       }
/*     */   }
/*     */ 
/*     */   private void cleanupInjector(NetworkObjectInjector injector)
/*     */   {
/* 304 */     this.playerInjector.remove(injector.getPlayer());
/* 305 */     this.playerInjector.remove(injector.getUpdatedPlayer());
/* 306 */     this.networkManagerInjector.remove(injector.getNetworkManager());
/*     */   }
/*     */ 
/*     */   private boolean matchMethod(String methodName, Method method)
/*     */   {
/* 316 */     return FuzzyMethodContract.newBuilder().nameExact(methodName).parameterCount(3).parameterSuperOf(MinecraftReflection.getNetHandlerClass(), 1).parameterSuperOf(MinecraftReflection.getPacketClass(), 2).returnTypeExact(MinecraftReflection.getPacketClass()).build().isMatch(MethodInfo.fromMethod(method), null);
/*     */   }
/*     */ 
/*     */   public boolean hasRegistered()
/*     */   {
/* 331 */     return this.dynamicListener != null;
/*     */   }
/*     */ 
/*     */   public PlayerInjectionHandler getPlayerHandler()
/*     */   {
/* 339 */     return new DummyPlayerHandler(this, this.queuedFilters);
/*     */   }
/*     */ 
/*     */   public PacketInjector getPacketInjector()
/*     */   {
/* 347 */     return new DummyPacketInjector(this, this.reveivedFilters);
/*     */   }
/*     */ 
/*     */   NetworkObjectInjector getInjector(Player player, boolean createNew)
/*     */   {
/* 357 */     NetworkObjectInjector injector = (NetworkObjectInjector)this.playerInjector.get(player);
/*     */ 
/* 359 */     if ((injector == null) && (createNew))
/*     */     {
/* 361 */       if ((player instanceof Factory))
/* 362 */         throw new IllegalArgumentException("Cannot inject tempoary player " + player);
/*     */       try
/*     */       {
/* 365 */         NetworkObjectInjector created = new NetworkObjectInjector(filterImpossibleWarnings(this.reporter), null, this.invoker, null);
/*     */ 
/* 368 */         created.initializePlayer(player);
/*     */ 
/* 370 */         if (created.getNetworkManager() == null)
/* 371 */           throw new PlayerLoggedOutException("Player " + player + " has logged out.");
/* 372 */         injector = saveInjector(created.getNetworkManager(), created);
/*     */       }
/*     */       catch (IllegalAccessException e) {
/* 375 */         throw new RuntimeException("Cannot create dummy injector.", e);
/*     */       }
/*     */     }
/* 378 */     return injector;
/*     */   }
/*     */ 
/*     */   NetworkObjectInjector getInjector(Object networkManager, Object connection)
/*     */   {
/* 388 */     NetworkObjectInjector dummyInjector = (NetworkObjectInjector)this.networkManagerInjector.get(networkManager);
/*     */ 
/* 390 */     if (dummyInjector == null) {
/*     */       try
/*     */       {
/* 393 */         NetworkObjectInjector created = new NetworkObjectInjector(filterImpossibleWarnings(this.reporter), null, this.invoker, null);
/*     */ 
/* 396 */         if (MinecraftReflection.isLoginHandler(connection)) {
/* 397 */           created.initialize(connection);
/* 398 */           created.setPlayer(created.createTemporaryPlayer(this.server));
/* 399 */         } else if (MinecraftReflection.isServerHandler(connection))
/*     */         {
/* 401 */           if (playerConnectionPlayer == null) {
/* 402 */             playerConnectionPlayer = FuzzyReflection.fromObject(connection).getFieldByType("player", MinecraftReflection.getEntityPlayerClass());
/*     */           }
/* 404 */           Object entityPlayer = playerConnectionPlayer.get(connection);
/*     */ 
/* 406 */           created.initialize(MinecraftReflection.getBukkitEntity(entityPlayer));
/*     */         }
/*     */         else {
/* 409 */           throw new IllegalArgumentException("Unregonized connection in NetworkManager.");
/*     */         }
/*     */ 
/* 412 */         dummyInjector = saveInjector(networkManager, created);
/*     */       }
/*     */       catch (IllegalAccessException e) {
/* 415 */         throw new RuntimeException("Cannot create dummy injector.", e);
/*     */       }
/*     */     }
/*     */ 
/* 419 */     return dummyInjector;
/*     */   }
/*     */ 
/*     */   private ErrorReporter filterImpossibleWarnings(ErrorReporter reporter)
/*     */   {
/* 428 */     return new DelegatedErrorReporter(reporter)
/*     */     {
/*     */       protected Report filterReport(Object sender, Report report, boolean detailed)
/*     */       {
/* 432 */         if (report.getType() == NetworkObjectInjector.REPORT_DETECTED_CUSTOM_SERVER_HANDLER)
/* 433 */           return null;
/* 434 */         return report;
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   private NetworkObjectInjector saveInjector(Object networkManager, NetworkObjectInjector created)
/*     */   {
/* 447 */     NetworkObjectInjector result = (NetworkObjectInjector)this.networkManagerInjector.putIfAbsent(networkManager, created);
/*     */ 
/* 449 */     if (result == null) {
/* 450 */       result = created;
/*     */     }
/*     */ 
/* 454 */     this.playerInjector.put(created.getPlayer(), created);
/* 455 */     return result;
/*     */   }
/*     */ 
/*     */   public void saveBuffered(Object handle, byte[] buffered)
/*     */   {
/* 464 */     this.readBufferedPackets.put(handle, buffered);
/*     */   }
/*     */ 
/*     */   public Object packetReceived(Object networkManager, Object connection, Object packet)
/*     */   {
/* 469 */     if (this.reveivedFilters.contains(packet.getClass()))
/*     */     {
/* 471 */       Integer id = Integer.valueOf(this.invoker.getPacketID(packet));
/*     */ 
/* 474 */       if (this.ignoredPackets.remove(packet)) {
/* 475 */         return packet;
/*     */       }
/*     */ 
/* 478 */       Player sender = getInjector(networkManager, connection).getUpdatedPlayer();
/* 479 */       PacketType type = PacketType.findLegacy(id.intValue(), PacketType.Sender.CLIENT);
/* 480 */       PacketContainer container = new PacketContainer(type, packet);
/* 481 */       PacketEvent event = packetReceived(container, sender, (byte[])this.readBufferedPackets.get(packet));
/*     */ 
/* 483 */       if (!event.isCancelled()) {
/* 484 */         return event.getPacket().getHandle();
/*     */       }
/* 486 */       return null;
/*     */     }
/*     */ 
/* 489 */     return packet;
/*     */   }
/*     */ 
/*     */   public Object packetQueued(Object networkManager, Object connection, Object packet)
/*     */   {
/* 494 */     if (this.queuedFilters.contains(packet.getClass()))
/*     */     {
/* 496 */       Integer id = Integer.valueOf(this.invoker.getPacketID(packet));
/*     */ 
/* 499 */       if (this.ignoredPackets.remove(packet)) {
/* 500 */         return packet;
/*     */       }
/*     */ 
/* 503 */       Player reciever = getInjector(networkManager, connection).getUpdatedPlayer();
/* 504 */       PacketType type = PacketType.findLegacy(id.intValue(), PacketType.Sender.SERVER);
/* 505 */       PacketContainer container = new PacketContainer(type, packet);
/* 506 */       PacketEvent event = packetQueued(container, reciever);
/*     */ 
/* 508 */       if (!event.isCancelled()) {
/* 509 */         return event.getPacket().getHandle();
/*     */       }
/* 511 */       return null;
/*     */     }
/*     */ 
/* 514 */     return packet;
/*     */   }
/*     */ 
/*     */   PacketEvent packetQueued(PacketContainer packet, Player receiver)
/*     */   {
/* 524 */     PacketEvent event = PacketEvent.fromServer(this, packet, receiver);
/*     */ 
/* 526 */     this.invoker.invokePacketSending(event);
/* 527 */     return event;
/*     */   }
/*     */ 
/*     */   PacketEvent packetReceived(PacketContainer packet, Player sender, byte[] buffered)
/*     */   {
/* 537 */     NetworkMarker marker = buffered != null ? new LegacyNetworkMarker(ConnectionSide.CLIENT_SIDE, buffered, packet.getType()) : null;
/* 538 */     PacketEvent event = PacketEvent.fromClient(this, packet, marker, sender);
/*     */ 
/* 540 */     this.invoker.invokePacketRecieving(event);
/* 541 */     return event;
/*     */   }
/*     */ 
/*     */   void injectPlayer(Player player)
/*     */   {
/*     */     try
/*     */     {
/* 550 */       NetworkObjectInjector dummy = new NetworkObjectInjector(filterImpossibleWarnings(this.reporter), player, this.invoker, null);
/*     */ 
/* 552 */       dummy.initializePlayer(player);
/*     */ 
/* 555 */       NetworkObjectInjector realInjector = (NetworkObjectInjector)this.networkManagerInjector.get(dummy.getNetworkManager());
/*     */ 
/* 557 */       if (realInjector != null)
/*     */       {
/* 559 */         realInjector.setUpdatedPlayer(player);
/* 560 */         this.playerInjector.put(player, realInjector);
/*     */       }
/*     */       else {
/* 563 */         saveInjector(dummy.getNetworkManager(), dummy);
/*     */       }
/*     */     }
/*     */     catch (IllegalAccessException e) {
/* 567 */       throw new RuntimeException("Cannot inject " + player);
/*     */     }
/*     */   }
/*     */ 
/*     */   void uninjectPlayer(Player player)
/*     */   {
/* 576 */     final NetworkObjectInjector injector = getInjector(player, false);
/*     */ 
/* 578 */     if ((player != null) && (injector != null))
/* 579 */       Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin, new Runnable()
/*     */       {
/*     */         public void run() {
/* 582 */           SpigotPacketInjector.this.cleanupInjector(injector);
/*     */         }
/*     */       }
/*     */       , 100L);
/*     */   }
/*     */ 
/*     */   void sendServerPacket(Player receiver, PacketContainer packet, NetworkMarker marker, boolean filters)
/*     */     throws InvocationTargetException
/*     */   {
/* 597 */     NetworkObjectInjector networkObject = getInjector(receiver, true);
/*     */ 
/* 600 */     if (filters)
/* 601 */       this.ignoredPackets.remove(packet.getHandle());
/*     */     else {
/* 603 */       this.ignoredPackets.add(packet.getHandle());
/*     */     }
/* 605 */     networkObject.sendServerPacket(packet.getHandle(), marker, filters);
/*     */   }
/*     */ 
/*     */   void processPacket(Player player, Object mcPacket)
/*     */     throws IllegalAccessException, InvocationTargetException
/*     */   {
/* 616 */     NetworkObjectInjector networkObject = getInjector(player, true);
/*     */ 
/* 619 */     this.ignoredPackets.add(mcPacket);
/* 620 */     networkObject.processPacket(mcPacket);
/*     */   }
/*     */ 
/*     */   private void cleanupListener()
/*     */   {
/* 625 */     Class listenerClass = getSpigotListenerClass();
/*     */ 
/* 628 */     synchronized (listenerClass) {
/*     */       try {
/* 630 */         Field listenersField = FieldUtils.getField(listenerClass, "listeners", true);
/* 631 */         Field bakedField = FieldUtils.getField(listenerClass, "baked", true);
/*     */ 
/* 633 */         Map listenerMap = (Map)listenersField.get(null);
/* 634 */         List listenerArray = Lists.newArrayList((Object[])bakedField.get(null));
/*     */ 
/* 636 */         listenerMap.remove(this.dynamicListener);
/* 637 */         listenerArray.remove(this.dynamicListener);
/*     */ 
/* 640 */         bakedField.set(null, Iterables.toArray(listenerArray, listenerClass));
/*     */ 
/* 643 */         this.dynamicListener = null;
/*     */       } catch (Exception e) {
/* 645 */         this.reporter.reportWarning(this, Report.newBuilder(REPORT_CANNOT_CLEANUP_SPIGOT).callerParam(new Object[] { this.dynamicListener }).error(e));
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void cleanupAll()
/*     */   {
/* 656 */     if (this.dynamicListener != null) {
/* 657 */       cleanupListener();
/*     */     }
/* 659 */     if (this.backgroundId >= 0) {
/* 660 */       Bukkit.getScheduler().cancelTask(this.backgroundId);
/* 661 */       this.backgroundId = -1;
/*     */     }
/*     */ 
/* 665 */     if (this.proxyPacketInjector != null)
/* 666 */       this.proxyPacketInjector.cleanupAll();
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.injector.spigot.SpigotPacketInjector
 * JD-Core Version:    0.6.2
 */