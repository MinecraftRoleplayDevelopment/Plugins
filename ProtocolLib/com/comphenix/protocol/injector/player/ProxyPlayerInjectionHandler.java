/*     */ package com.comphenix.protocol.injector.player;
/*     */ 
/*     */ import com.comphenix.net.sf.cglib.proxy.Factory;
/*     */ import com.comphenix.protocol.PacketType;
/*     */ import com.comphenix.protocol.PacketType.Sender;
/*     */ import com.comphenix.protocol.concurrency.BlockingHashMap;
/*     */ import com.comphenix.protocol.concurrency.IntegerSet;
/*     */ import com.comphenix.protocol.error.ErrorReporter;
/*     */ import com.comphenix.protocol.error.Report;
/*     */ import com.comphenix.protocol.error.Report.ReportBuilder;
/*     */ import com.comphenix.protocol.error.ReportType;
/*     */ import com.comphenix.protocol.events.ListenerOptions;
/*     */ import com.comphenix.protocol.events.NetworkMarker;
/*     */ import com.comphenix.protocol.events.PacketAdapter;
/*     */ import com.comphenix.protocol.events.PacketContainer;
/*     */ import com.comphenix.protocol.events.PacketEvent;
/*     */ import com.comphenix.protocol.events.PacketListener;
/*     */ import com.comphenix.protocol.injector.GamePhase;
/*     */ import com.comphenix.protocol.injector.ListenerInvoker;
/*     */ import com.comphenix.protocol.injector.PacketFilterManager.PlayerInjectHooks;
/*     */ import com.comphenix.protocol.injector.PlayerLoggedOutException;
/*     */ import com.comphenix.protocol.injector.packet.PacketRegistry;
/*     */ import com.comphenix.protocol.injector.server.AbstractInputStreamLookup;
/*     */ import com.comphenix.protocol.injector.server.BukkitSocketInjector;
/*     */ import com.comphenix.protocol.injector.server.InputStreamLookupBuilder;
/*     */ import com.comphenix.protocol.injector.server.SocketInjector;
/*     */ import com.comphenix.protocol.utility.MinecraftProtocolVersion;
/*     */ import com.comphenix.protocol.utility.MinecraftReflection;
/*     */ import com.comphenix.protocol.utility.MinecraftVersion;
/*     */ import com.comphenix.protocol.utility.SafeCacheBuilder;
/*     */ import com.google.common.base.Objects;
/*     */ import com.google.common.base.Predicate;
/*     */ import com.google.common.collect.Maps;
/*     */ import io.netty.channel.Channel;
/*     */ import java.io.DataInputStream;
/*     */ import java.io.InputStream;
/*     */ import java.lang.ref.WeakReference;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.net.Socket;
/*     */ import java.net.SocketAddress;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.concurrent.ConcurrentMap;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import org.bukkit.Server;
/*     */ import org.bukkit.entity.Player;
/*     */ 
/*     */ class ProxyPlayerInjectionHandler
/*     */   implements PlayerInjectionHandler
/*     */ {
/*  77 */   public static final ReportType REPORT_UNSUPPPORTED_LISTENER = new ReportType("Cannot fully register listener for %s: %s");
/*     */ 
/*  80 */   public static final ReportType REPORT_PLAYER_HOOK_FAILED = new ReportType("Player hook %s failed.");
/*  81 */   public static final ReportType REPORT_SWITCHED_PLAYER_HOOK = new ReportType("Switching to %s instead.");
/*     */ 
/*  83 */   public static final ReportType REPORT_HOOK_CLEANUP_FAILED = new ReportType("Cleaing up after player hook failed.");
/*  84 */   public static final ReportType REPORT_CANNOT_REVERT_HOOK = new ReportType("Unable to fully revert old injector. May cause conflicts.");
/*     */   private InjectedServerConnection serverInjection;
/*     */   private AbstractInputStreamLookup inputStreamLookup;
/*     */   private NetLoginInjector netLoginInjector;
/*     */   private WeakReference<PlayerInjector> lastSuccessfulHook;
/*  99 */   private ConcurrentMap<Player, PlayerInjector> dummyInjectors = SafeCacheBuilder.newBuilder().expireAfterWrite(30L, TimeUnit.SECONDS).build(BlockingHashMap.newInvalidCacheLoader());
/*     */ 
/* 105 */   private Map<Player, PlayerInjector> playerInjection = Maps.newConcurrentMap();
/*     */ 
/* 108 */   private volatile PacketFilterManager.PlayerInjectHooks loginPlayerHook = PacketFilterManager.PlayerInjectHooks.NETWORK_SERVER_OBJECT;
/* 109 */   private volatile PacketFilterManager.PlayerInjectHooks playingPlayerHook = PacketFilterManager.PlayerInjectHooks.NETWORK_SERVER_OBJECT;
/*     */   private ErrorReporter reporter;
/*     */   private boolean hasClosed;
/*     */   private ListenerInvoker invoker;
/*     */   private MinecraftVersion version;
/* 124 */   private IntegerSet sendingFilters = new IntegerSet(256);
/*     */   private Set<PacketListener> packetListeners;
/*     */   private Predicate<GamePhase> injectionFilter;
/*     */ 
/*     */   public ProxyPlayerInjectionHandler(ErrorReporter reporter, Predicate<GamePhase> injectionFilter, ListenerInvoker invoker, Set<PacketListener> packetListeners, Server server, MinecraftVersion version)
/*     */   {
/* 136 */     this.reporter = reporter;
/* 137 */     this.invoker = invoker;
/* 138 */     this.injectionFilter = injectionFilter;
/* 139 */     this.packetListeners = packetListeners;
/* 140 */     this.version = version;
/*     */ 
/* 142 */     this.inputStreamLookup = InputStreamLookupBuilder.newBuilder().server(server).reporter(reporter).build();
/*     */ 
/* 148 */     this.netLoginInjector = new NetLoginInjector(reporter, server, this);
/* 149 */     this.serverInjection = new InjectedServerConnection(reporter, this.inputStreamLookup, server, this.netLoginInjector);
/* 150 */     this.serverInjection.injectList();
/*     */   }
/*     */ 
/*     */   public int getProtocolVersion(Player player)
/*     */   {
/* 156 */     return MinecraftProtocolVersion.getCurrentVersion();
/*     */   }
/*     */ 
/*     */   public PacketFilterManager.PlayerInjectHooks getPlayerHook()
/*     */   {
/* 165 */     return getPlayerHook(GamePhase.PLAYING);
/*     */   }
/*     */ 
/*     */   public PacketFilterManager.PlayerInjectHooks getPlayerHook(GamePhase phase)
/*     */   {
/* 175 */     switch (phase) {
/*     */     case LOGIN:
/* 177 */       return this.loginPlayerHook;
/*     */     case PLAYING:
/* 179 */       return this.playingPlayerHook;
/*     */     }
/* 181 */     throw new IllegalArgumentException("Cannot retrieve injection hook for both phases at the same time.");
/*     */   }
/*     */ 
/*     */   public boolean hasMainThreadListener(PacketType type)
/*     */   {
/* 187 */     return this.sendingFilters.contains(type.getLegacyId());
/*     */   }
/*     */ 
/*     */   public void setPlayerHook(PacketFilterManager.PlayerInjectHooks playerHook)
/*     */   {
/* 196 */     setPlayerHook(GamePhase.PLAYING, playerHook);
/*     */   }
/*     */ 
/*     */   public void setPlayerHook(GamePhase phase, PacketFilterManager.PlayerInjectHooks playerHook)
/*     */   {
/* 206 */     if (phase.hasLogin())
/* 207 */       this.loginPlayerHook = playerHook;
/* 208 */     if (phase.hasPlaying()) {
/* 209 */       this.playingPlayerHook = playerHook;
/*     */     }
/*     */ 
/* 212 */     checkListener(this.packetListeners);
/*     */   }
/*     */ 
/*     */   public void addPacketHandler(PacketType type, Set<ListenerOptions> options)
/*     */   {
/* 217 */     this.sendingFilters.add(type.getLegacyId());
/*     */   }
/*     */ 
/*     */   public void removePacketHandler(PacketType type)
/*     */   {
/* 222 */     this.sendingFilters.remove(type.getLegacyId());
/*     */   }
/*     */ 
/*     */   private PlayerInjector getHookInstance(Player player, PacketFilterManager.PlayerInjectHooks hook)
/*     */     throws IllegalAccessException
/*     */   {
/* 234 */     switch (1.$SwitchMap$com$comphenix$protocol$injector$PacketFilterManager$PlayerInjectHooks[hook.ordinal()]) {
/*     */     case 1:
/* 236 */       return new NetworkFieldInjector(this.reporter, player, this.invoker, this.sendingFilters);
/*     */     case 2:
/* 238 */       return new NetworkObjectInjector(this.reporter, player, this.invoker, this.sendingFilters);
/*     */     case 3:
/* 240 */       return new NetworkServerInjector(this.reporter, player, this.invoker, this.sendingFilters, this.serverInjection);
/*     */     }
/* 242 */     throw new IllegalArgumentException("Cannot construct a player injector.");
/*     */   }
/*     */ 
/*     */   public Player getPlayerByConnection(DataInputStream inputStream)
/*     */   {
/* 254 */     SocketInjector injector = this.inputStreamLookup.waitSocketInjector(inputStream);
/*     */ 
/* 256 */     if (injector != null) {
/* 257 */       return injector.getPlayer();
/*     */     }
/* 259 */     return null;
/*     */   }
/*     */ 
/*     */   private PacketFilterManager.PlayerInjectHooks getInjectorType(PlayerInjector injector)
/*     */   {
/* 269 */     return injector != null ? injector.getHookType() : PacketFilterManager.PlayerInjectHooks.NONE;
/*     */   }
/*     */ 
/*     */   public void injectPlayer(Player player, PlayerInjectionHandler.ConflictStrategy strategy)
/*     */   {
/* 282 */     if (isInjectionNecessary(GamePhase.PLAYING))
/* 283 */       injectPlayer(player, player, strategy, GamePhase.PLAYING);
/*     */   }
/*     */ 
/*     */   public boolean isInjectionNecessary(GamePhase phase)
/*     */   {
/* 293 */     return this.injectionFilter.apply(phase);
/*     */   }
/*     */ 
/*     */   PlayerInjector injectPlayer(Player player, Object injectionPoint, PlayerInjectionHandler.ConflictStrategy stategy, GamePhase phase)
/*     */   {
/* 307 */     if (player == null)
/* 308 */       throw new IllegalArgumentException("Player cannot be NULL.");
/* 309 */     if (injectionPoint == null)
/* 310 */       throw new IllegalArgumentException("injectionPoint cannot be NULL.");
/* 311 */     if (phase == null) {
/* 312 */       throw new IllegalArgumentException("phase cannot be NULL.");
/*     */     }
/*     */ 
/* 315 */     synchronized (player) {
/* 316 */       return injectPlayerInternal(player, injectionPoint, stategy, phase);
/*     */     }
/*     */   }
/*     */ 
/*     */   private PlayerInjector injectPlayerInternal(Player player, Object injectionPoint, PlayerInjectionHandler.ConflictStrategy stategy, GamePhase phase)
/*     */   {
/* 322 */     PlayerInjector injector = (PlayerInjector)this.playerInjection.get(player);
/* 323 */     PacketFilterManager.PlayerInjectHooks tempHook = getPlayerHook(phase);
/* 324 */     PacketFilterManager.PlayerInjectHooks permanentHook = tempHook;
/*     */ 
/* 329 */     boolean invalidInjector = !injector.canInject(phase);
/*     */ 
/* 332 */     if ((!this.hasClosed) && ((tempHook != getInjectorType(injector)) || (invalidInjector))) {
/* 333 */       while (tempHook != PacketFilterManager.PlayerInjectHooks.NONE)
/*     */       {
/* 335 */         boolean hookFailed = false;
/*     */ 
/* 338 */         cleanupHook(injector);
/*     */         try
/*     */         {
/* 341 */           injector = getHookInstance(player, tempHook);
/*     */ 
/* 344 */           if (injector.canInject(phase)) {
/* 345 */             injector.initialize(injectionPoint);
/*     */ 
/* 348 */             SocketAddress address = injector.getAddress();
/*     */ 
/* 351 */             if (address == null) {
/* 352 */               return null;
/*     */             }
/* 354 */             SocketInjector previous = this.inputStreamLookup.peekSocketInjector(address);
/* 355 */             Socket socket = injector.getSocket();
/*     */ 
/* 358 */             if ((previous != null) && (!(player instanceof Factory))) {
/* 359 */               switch (stategy) {
/*     */               case OVERRIDE:
/* 361 */                 uninjectPlayer(previous.getPlayer(), true);
/* 362 */                 break;
/*     */               case BAIL_OUT:
/* 364 */                 return null;
/*     */               }
/*     */             }
/* 367 */             injector.injectManager();
/*     */ 
/* 369 */             saveAddressLookup(address, socket, injector);
/* 370 */             break;
/*     */           }
/*     */         }
/*     */         catch (PlayerLoggedOutException e) {
/* 374 */           throw e;
/*     */         }
/*     */         catch (Exception e)
/*     */         {
/* 378 */           this.reporter.reportDetailed(this, Report.newBuilder(REPORT_PLAYER_HOOK_FAILED).messageParam(new Object[] { tempHook }).callerParam(new Object[] { player, injectionPoint, phase }).error(e));
/*     */ 
/* 381 */           hookFailed = true;
/*     */         }
/*     */ 
/* 385 */         tempHook = PacketFilterManager.PlayerInjectHooks.values()[(tempHook.ordinal() - 1)];
/*     */ 
/* 387 */         if (hookFailed) {
/* 388 */           this.reporter.reportWarning(this, Report.newBuilder(REPORT_SWITCHED_PLAYER_HOOK).messageParam(new Object[] { tempHook }));
/*     */         }
/*     */ 
/* 391 */         if (tempHook == PacketFilterManager.PlayerInjectHooks.NONE) {
/* 392 */           cleanupHook(injector);
/* 393 */           injector = null;
/* 394 */           hookFailed = true;
/*     */         }
/*     */ 
/* 398 */         if (hookFailed) {
/* 399 */           permanentHook = tempHook;
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 404 */       if (injector != null)
/* 405 */         this.lastSuccessfulHook = new WeakReference(injector);
/* 406 */       if (permanentHook != getPlayerHook(phase)) {
/* 407 */         setPlayerHook(phase, tempHook);
/*     */       }
/*     */ 
/* 410 */       if (injector != null) {
/* 411 */         this.playerInjection.put(player, injector);
/*     */       }
/*     */     }
/*     */ 
/* 415 */     return injector;
/*     */   }
/*     */ 
/*     */   private void saveAddressLookup(SocketAddress address, Socket socket, SocketInjector injector) {
/* 419 */     SocketAddress socketAddress = socket != null ? socket.getRemoteSocketAddress() : null;
/*     */ 
/* 421 */     if ((socketAddress != null) && (!Objects.equal(socketAddress, address)))
/*     */     {
/* 423 */       this.inputStreamLookup.setSocketInjector(socketAddress, injector);
/*     */     }
/*     */ 
/* 426 */     this.inputStreamLookup.setSocketInjector(address, injector);
/*     */   }
/*     */ 
/*     */   private void cleanupHook(PlayerInjector injector)
/*     */   {
/*     */     try {
/* 432 */       if (injector != null)
/* 433 */         injector.cleanupAll();
/*     */     } catch (Exception ex) {
/* 435 */       this.reporter.reportDetailed(this, Report.newBuilder(REPORT_HOOK_CLEANUP_FAILED).callerParam(new Object[] { injector }).error(ex));
/*     */     }
/*     */   }
/*     */ 
/*     */   public void handleDisconnect(Player player)
/*     */   {
/* 445 */     PlayerInjector injector = getInjector(player);
/*     */ 
/* 447 */     if (injector != null)
/* 448 */       injector.handleDisconnect();
/*     */   }
/*     */ 
/*     */   public void updatePlayer(Player player)
/*     */   {
/* 454 */     SocketAddress address = player.getAddress();
/*     */ 
/* 457 */     if (address != null) {
/* 458 */       SocketInjector injector = this.inputStreamLookup.peekSocketInjector(address);
/*     */ 
/* 460 */       if (injector != null)
/* 461 */         injector.setUpdatedPlayer(player);
/*     */       else
/* 463 */         this.inputStreamLookup.setSocketInjector(player.getAddress(), new BukkitSocketInjector(player));
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean uninjectPlayer(Player player)
/*     */   {
/* 476 */     return uninjectPlayer(player, false);
/*     */   }
/*     */ 
/*     */   private boolean uninjectPlayer(Player player, boolean prepareNextHook)
/*     */   {
/* 486 */     if ((!this.hasClosed) && (player != null))
/*     */     {
/* 488 */       PlayerInjector injector = (PlayerInjector)this.playerInjection.remove(player);
/*     */ 
/* 490 */       if (injector != null) {
/* 491 */         injector.cleanupAll();
/*     */ 
/* 494 */         if ((prepareNextHook) && ((injector instanceof NetworkObjectInjector))) {
/*     */           try {
/* 496 */             PlayerInjector dummyInjector = getHookInstance(player, PacketFilterManager.PlayerInjectHooks.NETWORK_SERVER_OBJECT);
/* 497 */             dummyInjector.initializePlayer(player);
/* 498 */             dummyInjector.setNetworkManager(injector.getNetworkManager(), true);
/*     */           }
/*     */           catch (IllegalAccessException e)
/*     */           {
/* 502 */             this.reporter.reportWarning(this, Report.newBuilder(REPORT_CANNOT_REVERT_HOOK).error(e));
/*     */           }
/*     */         }
/*     */ 
/* 506 */         return true;
/*     */       }
/*     */     }
/*     */ 
/* 510 */     return false;
/*     */   }
/*     */ 
/*     */   public boolean uninjectPlayer(InetSocketAddress address)
/*     */   {
/* 524 */     if ((!this.hasClosed) && (address != null)) {
/* 525 */       SocketInjector injector = this.inputStreamLookup.peekSocketInjector(address);
/*     */ 
/* 528 */       if (injector != null)
/* 529 */         uninjectPlayer(injector.getPlayer(), true);
/* 530 */       return true;
/*     */     }
/*     */ 
/* 533 */     return false;
/*     */   }
/*     */ 
/*     */   public void sendServerPacket(Player receiver, PacketContainer packet, NetworkMarker marker, boolean filters)
/*     */     throws InvocationTargetException
/*     */   {
/* 545 */     SocketInjector injector = getInjector(receiver);
/*     */ 
/* 548 */     if (injector != null)
/* 549 */       injector.sendServerPacket(packet.getHandle(), marker, filters);
/*     */     else
/* 551 */       throw new PlayerLoggedOutException(String.format("Unable to send packet %s (%s): Player %s has logged out.", new Object[] { packet.getType(), packet, receiver }));
/*     */   }
/*     */ 
/*     */   public void recieveClientPacket(Player player, Object mcPacket)
/*     */     throws IllegalAccessException, InvocationTargetException
/*     */   {
/* 567 */     PlayerInjector injector = getInjector(player);
/*     */ 
/* 570 */     if (injector != null)
/* 571 */       injector.processPacket(mcPacket);
/*     */     else
/* 573 */       throw new PlayerLoggedOutException(String.format("Unable to receieve packet %s. Player %s has logged out.", new Object[] { mcPacket, player }));
/*     */   }
/*     */ 
/*     */   private PlayerInjector getInjector(Player player)
/*     */   {
/* 585 */     PlayerInjector injector = (PlayerInjector)this.playerInjection.get(player);
/*     */ 
/* 587 */     if (injector == null)
/*     */     {
/* 589 */       SocketAddress address = player.getAddress();
/*     */ 
/* 592 */       if (address == null) {
/* 593 */         return null;
/*     */       }
/*     */ 
/* 596 */       SocketInjector result = this.inputStreamLookup.peekSocketInjector(address);
/*     */ 
/* 599 */       if ((result instanceof PlayerInjector)) {
/* 600 */         return (PlayerInjector)result;
/*     */       }
/*     */ 
/* 603 */       return createDummyInjector(player);
/*     */     }
/*     */ 
/* 606 */     return injector;
/*     */   }
/*     */ 
/*     */   private PlayerInjector createDummyInjector(Player player)
/*     */   {
/* 616 */     if (!MinecraftReflection.getCraftPlayerClass().isAssignableFrom(player.getClass()))
/*     */     {
/* 618 */       return null;
/*     */     }
/*     */     try
/*     */     {
/* 622 */       PlayerInjector dummyInjector = getHookInstance(player, PacketFilterManager.PlayerInjectHooks.NETWORK_SERVER_OBJECT);
/* 623 */       dummyInjector.initializePlayer(player);
/*     */ 
/* 626 */       if (dummyInjector.getSocket() == null) {
/* 627 */         return null;
/*     */       }
/*     */ 
/* 630 */       this.inputStreamLookup.setSocketInjector(dummyInjector.getAddress(), dummyInjector);
/* 631 */       this.dummyInjectors.put(player, dummyInjector);
/* 632 */       return dummyInjector;
/*     */     }
/*     */     catch (IllegalAccessException e) {
/* 635 */       throw new RuntimeException("Cannot access fields.", e);
/*     */     }
/*     */   }
/*     */ 
/*     */   PlayerInjector getInjectorByNetworkHandler(Object networkManager)
/*     */   {
/* 646 */     if (networkManager == null) {
/* 647 */       return null;
/*     */     }
/*     */ 
/* 650 */     for (PlayerInjector injector : this.playerInjection.values()) {
/* 651 */       if (injector.getNetworkManager() == networkManager) {
/* 652 */         return injector;
/*     */       }
/*     */     }
/*     */ 
/* 656 */     return null;
/*     */   }
/*     */ 
/*     */   public boolean canRecievePackets()
/*     */   {
/* 661 */     return false;
/*     */   }
/*     */ 
/*     */   public PacketEvent handlePacketRecieved(PacketContainer packet, InputStream input, byte[] buffered)
/*     */   {
/* 666 */     throw new UnsupportedOperationException("Proxy injection cannot handle received packets.");
/*     */   }
/*     */ 
/*     */   public void checkListener(Set<PacketListener> listeners)
/*     */   {
/* 676 */     if (getLastSuccessfulHook() != null)
/* 677 */       for (PacketListener listener : listeners)
/* 678 */         checkListener(listener);
/*     */   }
/*     */ 
/*     */   private PlayerInjector getLastSuccessfulHook()
/*     */   {
/* 690 */     return this.lastSuccessfulHook != null ? (PlayerInjector)this.lastSuccessfulHook.get() : null;
/*     */   }
/*     */ 
/*     */   public void checkListener(PacketListener listener)
/*     */   {
/* 701 */     PlayerInjector last = getLastSuccessfulHook();
/*     */ 
/* 703 */     if (last != null) {
/* 704 */       UnsupportedListener result = last.checkListener(this.version, listener);
/*     */ 
/* 707 */       if (result != null) {
/* 708 */         this.reporter.reportWarning(this, Report.newBuilder(REPORT_UNSUPPPORTED_LISTENER).messageParam(new Object[] { PacketAdapter.getPluginName(listener), result }));
/*     */ 
/* 713 */         for (int packetID : result.getPackets()) {
/* 714 */           removePacketHandler(PacketType.findLegacy(packetID, PacketType.Sender.CLIENT));
/* 715 */           removePacketHandler(PacketType.findLegacy(packetID, PacketType.Sender.SERVER));
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public Set<PacketType> getSendingFilters()
/*     */   {
/* 727 */     return PacketRegistry.toPacketTypes(this.sendingFilters.toSet(), PacketType.Sender.SERVER);
/*     */   }
/*     */ 
/*     */   public void close()
/*     */   {
/* 733 */     if ((this.hasClosed) || (this.playerInjection == null)) {
/* 734 */       return;
/*     */     }
/*     */ 
/* 737 */     for (PlayerInjector injection : this.playerInjection.values()) {
/* 738 */       if (injection != null) {
/* 739 */         injection.cleanupAll();
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 744 */     if (this.inputStreamLookup != null)
/* 745 */       this.inputStreamLookup.cleanupAll();
/* 746 */     if (this.serverInjection != null)
/* 747 */       this.serverInjection.cleanupAll();
/* 748 */     if (this.netLoginInjector != null)
/* 749 */       this.netLoginInjector.cleanupAll();
/* 750 */     this.inputStreamLookup = null;
/* 751 */     this.serverInjection = null;
/* 752 */     this.netLoginInjector = null;
/* 753 */     this.hasClosed = true;
/*     */ 
/* 755 */     this.playerInjection.clear();
/* 756 */     this.invoker = null;
/*     */   }
/*     */ 
/*     */   public Channel getChannel(Player player)
/*     */   {
/* 761 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.injector.player.ProxyPlayerInjectionHandler
 * JD-Core Version:    0.6.2
 */