/*      */ package com.comphenix.protocol.injector;
/*      */ 
/*      */ import com.comphenix.net.sf.cglib.proxy.Enhancer;
/*      */ import com.comphenix.net.sf.cglib.proxy.MethodInterceptor;
/*      */ import com.comphenix.net.sf.cglib.proxy.MethodProxy;
/*      */ import com.comphenix.protocol.AsynchronousManager;
/*      */ import com.comphenix.protocol.PacketType;
/*      */ import com.comphenix.protocol.PacketType.Sender;
/*      */ import com.comphenix.protocol.ProtocolManager;
/*      */ import com.comphenix.protocol.async.AsyncFilterManager;
/*      */ import com.comphenix.protocol.async.AsyncMarker;
/*      */ import com.comphenix.protocol.error.ErrorReporter;
/*      */ import com.comphenix.protocol.error.Report;
/*      */ import com.comphenix.protocol.error.Report.ReportBuilder;
/*      */ import com.comphenix.protocol.error.ReportType;
/*      */ import com.comphenix.protocol.events.ConnectionSide;
/*      */ import com.comphenix.protocol.events.ListenerOptions;
/*      */ import com.comphenix.protocol.events.ListenerPriority;
/*      */ import com.comphenix.protocol.events.ListeningWhitelist;
/*      */ import com.comphenix.protocol.events.NetworkMarker;
/*      */ import com.comphenix.protocol.events.PacketAdapter;
/*      */ import com.comphenix.protocol.events.PacketContainer;
/*      */ import com.comphenix.protocol.events.PacketEvent;
/*      */ import com.comphenix.protocol.events.PacketListener;
/*      */ import com.comphenix.protocol.injector.netty.NettyProtocolInjector;
/*      */ import com.comphenix.protocol.injector.netty.WirePacket;
/*      */ import com.comphenix.protocol.injector.packet.InterceptWritePacket;
/*      */ import com.comphenix.protocol.injector.packet.PacketInjector;
/*      */ import com.comphenix.protocol.injector.packet.PacketInjectorBuilder;
/*      */ import com.comphenix.protocol.injector.packet.PacketRegistry;
/*      */ import com.comphenix.protocol.injector.player.PlayerInjectionHandler;
/*      */ import com.comphenix.protocol.injector.player.PlayerInjectionHandler.ConflictStrategy;
/*      */ import com.comphenix.protocol.injector.player.PlayerInjector.ServerHandlerNull;
/*      */ import com.comphenix.protocol.injector.player.PlayerInjectorBuilder;
/*      */ import com.comphenix.protocol.injector.spigot.SpigotPacketInjector;
/*      */ import com.comphenix.protocol.reflect.FieldAccessException;
/*      */ import com.comphenix.protocol.reflect.FuzzyReflection;
/*      */ import com.comphenix.protocol.reflect.StructureModifier;
/*      */ import com.comphenix.protocol.utility.EnhancerFactory;
/*      */ import com.comphenix.protocol.utility.MinecraftReflection;
/*      */ import com.comphenix.protocol.utility.MinecraftVersion;
/*      */ import com.comphenix.protocol.utility.Util;
/*      */ import com.google.common.base.Objects;
/*      */ import com.google.common.base.Preconditions;
/*      */ import com.google.common.base.Predicate;
/*      */ import com.google.common.collect.ImmutableSet;
/*      */ import com.google.common.collect.Sets;
/*      */ import io.netty.channel.Channel;
/*      */ import java.lang.reflect.InvocationTargetException;
/*      */ import java.lang.reflect.Method;
/*      */ import java.util.Collections;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Set;
/*      */ import java.util.concurrent.ConcurrentHashMap;
/*      */ import java.util.concurrent.atomic.AtomicBoolean;
/*      */ import java.util.concurrent.atomic.AtomicInteger;
/*      */ import javax.annotation.Nullable;
/*      */ import org.bukkit.Bukkit;
/*      */ import org.bukkit.Location;
/*      */ import org.bukkit.Server;
/*      */ import org.bukkit.World;
/*      */ import org.bukkit.entity.Entity;
/*      */ import org.bukkit.entity.Player;
/*      */ import org.bukkit.event.EventHandler;
/*      */ import org.bukkit.event.EventPriority;
/*      */ import org.bukkit.event.Listener;
/*      */ import org.bukkit.event.player.PlayerJoinEvent;
/*      */ import org.bukkit.event.player.PlayerLoginEvent;
/*      */ import org.bukkit.event.player.PlayerQuitEvent;
/*      */ import org.bukkit.event.server.PluginDisableEvent;
/*      */ import org.bukkit.plugin.Plugin;
/*      */ import org.bukkit.plugin.PluginManager;
/*      */ import org.bukkit.scheduler.BukkitScheduler;
/*      */ 
/*      */ public final class PacketFilterManager
/*      */   implements ProtocolManager, ListenerInvoker, InternalManager
/*      */ {
/*   96 */   public static final ReportType REPORT_CANNOT_LOAD_PACKET_LIST = new ReportType("Cannot load server and client packet list.");
/*   97 */   public static final ReportType REPORT_CANNOT_INITIALIZE_PACKET_INJECTOR = new ReportType("Unable to initialize packet injector");
/*      */ 
/*   99 */   public static final ReportType REPORT_PLUGIN_DEPEND_MISSING = new ReportType("%s doesn't depend on ProtocolLib. Check that its plugin.yml has a 'depend' directive.");
/*      */ 
/*  103 */   public static final ReportType REPORT_UNSUPPORTED_SERVER_PACKET_ID = new ReportType("[%s] Unsupported server packet ID in current Minecraft version: %s");
/*  104 */   public static final ReportType REPORT_UNSUPPORTED_CLIENT_PACKET_ID = new ReportType("[%s] Unsupported client packet ID in current Minecraft version: %s");
/*      */ 
/*  107 */   public static final ReportType REPORT_CANNOT_UNINJECT_PLAYER = new ReportType("Unable to uninject net handler for player.");
/*  108 */   public static final ReportType REPORT_CANNOT_UNINJECT_OFFLINE_PLAYER = new ReportType("Unable to uninject logged off player.");
/*  109 */   public static final ReportType REPORT_CANNOT_INJECT_PLAYER = new ReportType("Unable to inject player.");
/*      */ 
/*  111 */   public static final ReportType REPORT_CANNOT_UNREGISTER_PLUGIN = new ReportType("Unable to handle disabled plugin.");
/*  112 */   public static final ReportType REPORT_PLUGIN_VERIFIER_ERROR = new ReportType("Verifier error: %s");
/*      */   public static final int TICKS_PER_SECOND = 20;
/*      */   private static final int UNHOOK_DELAY = 100;
/*      */   private DelayedSingleTask unhookTask;
/*  156 */   private Set<PacketListener> packetListeners = Collections.newSetFromMap(new ConcurrentHashMap());
/*      */   private PacketInjector packetInjector;
/*      */   private PlayerInjectionHandler playerInjection;
/*      */   private InterceptWritePacket interceptWritePacket;
/*  169 */   private volatile Set<PacketType> inputBufferedPackets = Sets.newHashSet();
/*      */   private SortedPacketListenerList recievedListeners;
/*      */   private SortedPacketListenerList sendingListeners;
/*      */   private volatile boolean hasClosed;
/*      */   private ClassLoader classLoader;
/*      */   private ErrorReporter reporter;
/*      */   private Server server;
/*      */   private Plugin library;
/*      */   private AsyncFilterManager asyncFilterManager;
/*      */   private boolean knowsServerPackets;
/*      */   private boolean knowsClientPackets;
/*  198 */   private AtomicInteger phaseLoginCount = new AtomicInteger(0);
/*  199 */   private AtomicInteger phasePlayingCount = new AtomicInteger(0);
/*      */ 
/*  202 */   private AtomicBoolean packetCreation = new AtomicBoolean();
/*      */   private SpigotPacketInjector spigotInjector;
/*      */   private NettyProtocolInjector nettyInjector;
/*      */   private PluginVerifier pluginVerifier;
/*  214 */   private boolean hasRecycleDistance = true;
/*      */   private MinecraftVersion minecraftVersion;
/*      */   private LoginPackets loginPackets;
/*      */   private boolean debug;
/*      */ 
/*      */   public PacketFilterManager(PacketFilterBuilder builder)
/*      */   {
/*  230 */     Predicate isInjectionNecessary = new Predicate()
/*      */     {
/*      */       public boolean apply(@Nullable GamePhase phase) {
/*  233 */         boolean result = true;
/*      */ 
/*  235 */         if (phase.hasLogin()) {
/*  236 */           result &= PacketFilterManager.this.getPhaseLoginCount() > 0;
/*      */         }
/*  238 */         if (phase.hasPlaying())
/*  239 */           result &= ((PacketFilterManager.this.getPhasePlayingCount() > 0) || (PacketFilterManager.this.unhookTask.isRunning()));
/*  240 */         return result;
/*      */       }
/*      */     };
/*  245 */     this.recievedListeners = new SortedPacketListenerList();
/*  246 */     this.sendingListeners = new SortedPacketListenerList();
/*      */ 
/*  249 */     this.unhookTask = builder.getUnhookTask();
/*  250 */     this.server = builder.getServer();
/*  251 */     this.classLoader = builder.getClassLoader();
/*  252 */     this.reporter = builder.getReporter();
/*      */     try
/*      */     {
/*  256 */       this.pluginVerifier = new PluginVerifier(builder.getLibrary());
/*      */     } catch (OutOfMemoryError e) {
/*  258 */       throw e;
/*      */     } catch (ThreadDeath e) {
/*  260 */       throw e;
/*      */     } catch (Throwable e) {
/*  262 */       this.reporter.reportWarning(this, Report.newBuilder(REPORT_PLUGIN_VERIFIER_ERROR).messageParam(new Object[] { e.getMessage() }).error(e));
/*      */     }
/*      */ 
/*  267 */     this.minecraftVersion = builder.getMinecraftVersion();
/*  268 */     this.loginPackets = new LoginPackets(this.minecraftVersion);
/*      */ 
/*  271 */     this.interceptWritePacket = new InterceptWritePacket(this.reporter);
/*      */ 
/*  274 */     if (MinecraftReflection.isUsingNetty()) {
/*  275 */       this.nettyInjector = new NettyProtocolInjector(builder.getLibrary(), this, this.reporter);
/*  276 */       this.playerInjection = this.nettyInjector.getPlayerInjector();
/*  277 */       this.packetInjector = this.nettyInjector.getPacketInjector();
/*      */     }
/*  279 */     else if (builder.isNettyEnabled()) {
/*  280 */       this.spigotInjector = new SpigotPacketInjector(this.reporter, this, this.server);
/*  281 */       this.playerInjection = this.spigotInjector.getPlayerHandler();
/*  282 */       this.packetInjector = this.spigotInjector.getPacketInjector();
/*      */ 
/*  285 */       this.spigotInjector.setProxyPacketInjector(PacketInjectorBuilder.newBuilder().invoker(this).reporter(this.reporter).playerInjection(this.playerInjection).buildInjector());
/*      */     }
/*      */     else
/*      */     {
/*  294 */       this.playerInjection = PlayerInjectorBuilder.newBuilder().invoker(this).server(this.server).reporter(this.reporter).packetListeners(this.packetListeners).injectionFilter(isInjectionNecessary).version(builder.getMinecraftVersion()).buildHandler();
/*      */ 
/*  303 */       this.packetInjector = PacketInjectorBuilder.newBuilder().invoker(this).reporter(this.reporter).playerInjection(this.playerInjection).buildInjector();
/*      */     }
/*      */ 
/*  309 */     this.asyncFilterManager = builder.getAsyncManager();
/*  310 */     this.library = builder.getLibrary();
/*      */     try
/*      */     {
/*  314 */       this.knowsServerPackets = (PacketRegistry.getClientPacketTypes() != null);
/*  315 */       this.knowsClientPackets = (PacketRegistry.getServerPacketTypes() != null);
/*      */     } catch (FieldAccessException e) {
/*  317 */       this.reporter.reportWarning(this, Report.newBuilder(REPORT_CANNOT_LOAD_PACKET_LIST).error(e));
/*      */     }
/*      */   }
/*      */ 
/*      */   public static PacketFilterBuilder newBuilder()
/*      */   {
/*  326 */     return new PacketFilterBuilder();
/*      */   }
/*      */ 
/*      */   public int getProtocolVersion(Player player)
/*      */   {
/*  331 */     return this.playerInjection.getProtocolVersion(player);
/*      */   }
/*      */ 
/*      */   public MinecraftVersion getMinecraftVersion()
/*      */   {
/*  336 */     return this.minecraftVersion;
/*      */   }
/*      */ 
/*      */   public AsynchronousManager getAsynchronousManager()
/*      */   {
/*  341 */     return this.asyncFilterManager;
/*      */   }
/*      */ 
/*      */   public boolean isDebug()
/*      */   {
/*  346 */     return this.debug;
/*      */   }
/*      */ 
/*      */   public void setDebug(boolean debug)
/*      */   {
/*  351 */     this.debug = debug;
/*      */ 
/*  354 */     if (this.nettyInjector != null)
/*  355 */       this.nettyInjector.setDebug(debug);
/*      */   }
/*      */ 
/*      */   public PlayerInjectHooks getPlayerHook()
/*      */   {
/*  365 */     return this.playerInjection.getPlayerHook();
/*      */   }
/*      */ 
/*      */   public void setPlayerHook(PlayerInjectHooks playerHook)
/*      */   {
/*  374 */     this.playerInjection.setPlayerHook(playerHook);
/*      */   }
/*      */ 
/*      */   public ImmutableSet<PacketListener> getPacketListeners()
/*      */   {
/*  379 */     return ImmutableSet.copyOf(this.packetListeners);
/*      */   }
/*      */ 
/*      */   public InterceptWritePacket getInterceptWritePacket()
/*      */   {
/*  384 */     return this.interceptWritePacket;
/*      */   }
/*      */ 
/*      */   private void printPluginWarnings(Plugin plugin)
/*      */   {
/*  392 */     if (this.pluginVerifier == null)
/*  393 */       return;
/*      */     try
/*      */     {
/*  396 */       switch (8.$SwitchMap$com$comphenix$protocol$injector$PluginVerifier$VerificationResult[this.pluginVerifier.verify(plugin).ordinal()]) {
/*      */       case 1:
/*  398 */         this.reporter.reportWarning(this, Report.newBuilder(REPORT_PLUGIN_DEPEND_MISSING).messageParam(new Object[] { plugin.getName() }));
/*      */       case 2:
/*      */       }
/*      */     }
/*      */     catch (Exception e)
/*      */     {
/*  404 */       this.reporter.reportWarning(this, Report.newBuilder(REPORT_PLUGIN_VERIFIER_ERROR).messageParam(new Object[] { e.getMessage() }));
/*      */     }
/*      */   }
/*      */ 
/*      */   public void addPacketListener(PacketListener listener)
/*      */   {
/*  410 */     if (listener == null) {
/*  411 */       throw new IllegalArgumentException("listener cannot be NULL.");
/*      */     }
/*      */ 
/*  414 */     if (this.packetListeners.contains(listener))
/*  415 */       return;
/*  416 */     ListeningWhitelist sending = listener.getSendingWhitelist();
/*  417 */     ListeningWhitelist receiving = listener.getReceivingWhitelist();
/*  418 */     boolean hasSending = (sending != null) && (sending.isEnabled());
/*  419 */     boolean hasReceiving = (receiving != null) && (receiving.isEnabled());
/*      */ 
/*  422 */     if (((!hasSending) || (!sending.getOptions().contains(ListenerOptions.SKIP_PLUGIN_VERIFIER))) && ((!hasReceiving) || (!receiving.getOptions().contains(ListenerOptions.SKIP_PLUGIN_VERIFIER))))
/*      */     {
/*  425 */       printPluginWarnings(listener.getPlugin());
/*      */     }
/*      */ 
/*  428 */     if ((hasSending) || (hasReceiving))
/*      */     {
/*  430 */       if (hasSending)
/*      */       {
/*  432 */         if (sending.getOptions().contains(ListenerOptions.INTERCEPT_INPUT_BUFFER)) {
/*  433 */           throw new IllegalArgumentException("Sending whitelist cannot require input bufferes to be intercepted.");
/*      */         }
/*      */ 
/*  436 */         verifyWhitelist(listener, sending);
/*  437 */         this.sendingListeners.addListener(listener, sending);
/*  438 */         enablePacketFilters(listener, sending.getTypes());
/*      */ 
/*  441 */         this.playerInjection.checkListener(listener);
/*      */       }
/*  443 */       if (hasSending) {
/*  444 */         incrementPhases(processPhase(sending));
/*      */       }
/*      */ 
/*  447 */       if (hasReceiving) {
/*  448 */         verifyWhitelist(listener, receiving);
/*  449 */         this.recievedListeners.addListener(listener, receiving);
/*  450 */         enablePacketFilters(listener, receiving.getTypes());
/*      */       }
/*  452 */       if (hasReceiving) {
/*  453 */         incrementPhases(processPhase(receiving));
/*      */       }
/*      */ 
/*  456 */       this.packetListeners.add(listener);
/*  457 */       updateRequireInputBuffers();
/*      */     }
/*      */   }
/*      */ 
/*      */   private GamePhase processPhase(ListeningWhitelist whitelist)
/*      */   {
/*  463 */     if ((!whitelist.getGamePhase().hasLogin()) && (!whitelist.getOptions().contains(ListenerOptions.DISABLE_GAMEPHASE_DETECTION)))
/*      */     {
/*  466 */       for (PacketType type : whitelist.getTypes()) {
/*  467 */         if (this.loginPackets.isLoginPacket(type)) {
/*  468 */           return GamePhase.BOTH;
/*      */         }
/*      */       }
/*      */     }
/*  472 */     return whitelist.getGamePhase();
/*      */   }
/*      */ 
/*      */   private void updateRequireInputBuffers()
/*      */   {
/*  479 */     Set updated = Sets.newHashSet();
/*      */ 
/*  481 */     for (PacketListener listener : this.packetListeners) {
/*  482 */       ListeningWhitelist whitelist = listener.getReceivingWhitelist();
/*      */ 
/*  485 */       if (whitelist.getOptions().contains(ListenerOptions.INTERCEPT_INPUT_BUFFER)) {
/*  486 */         for (PacketType type : whitelist.getTypes()) {
/*  487 */           updated.add(type);
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/*  492 */     this.inputBufferedPackets = updated;
/*  493 */     this.packetInjector.inputBuffersChanged(updated);
/*      */   }
/*      */ 
/*      */   private void incrementPhases(GamePhase phase)
/*      */   {
/*  501 */     if (phase.hasLogin()) {
/*  502 */       this.phaseLoginCount.incrementAndGet();
/*      */     }
/*      */ 
/*  505 */     if ((phase.hasPlaying()) && 
/*  506 */       (this.phasePlayingCount.incrementAndGet() == 1))
/*      */     {
/*  508 */       if (this.unhookTask.isRunning()) {
/*  509 */         this.unhookTask.cancel();
/*      */       }
/*      */       else
/*  512 */         initializePlayers(Util.getOnlinePlayers());
/*      */     }
/*      */   }
/*      */ 
/*      */   private void decrementPhases(GamePhase phase)
/*      */   {
/*  522 */     if (phase.hasLogin()) {
/*  523 */       this.phaseLoginCount.decrementAndGet();
/*      */     }
/*      */ 
/*  526 */     if ((phase.hasPlaying()) && 
/*  527 */       (this.phasePlayingCount.decrementAndGet() == 0))
/*      */     {
/*  529 */       this.unhookTask.schedule(100L, new Runnable()
/*      */       {
/*      */         public void run()
/*      */         {
/*  533 */           PacketFilterManager.this.uninitializePlayers(Util.getOnlinePlayers());
/*      */         }
/*      */       });
/*      */     }
/*      */   }
/*      */ 
/*      */   public static void verifyWhitelist(PacketListener listener, ListeningWhitelist whitelist)
/*      */   {
/*  547 */     for (PacketType type : whitelist.getTypes())
/*  548 */       if (type == null)
/*  549 */         throw new IllegalArgumentException(String.format("Packet type in in listener %s was NULL.", new Object[] { PacketAdapter.getPluginName(listener) }));
/*      */   }
/*      */ 
/*      */   public void removePacketListener(PacketListener listener)
/*      */   {
/*  558 */     if (listener == null) {
/*  559 */       throw new IllegalArgumentException("listener cannot be NULL");
/*      */     }
/*  561 */     List sendingRemoved = null;
/*  562 */     List receivingRemoved = null;
/*      */ 
/*  564 */     ListeningWhitelist sending = listener.getSendingWhitelist();
/*  565 */     ListeningWhitelist receiving = listener.getReceivingWhitelist();
/*      */ 
/*  568 */     if (!this.packetListeners.remove(listener)) {
/*  569 */       return;
/*      */     }
/*      */ 
/*  572 */     if ((sending != null) && (sending.isEnabled())) {
/*  573 */       sendingRemoved = this.sendingListeners.removeListener(listener, sending);
/*  574 */       decrementPhases(processPhase(sending));
/*      */     }
/*  576 */     if ((receiving != null) && (receiving.isEnabled())) {
/*  577 */       receivingRemoved = this.recievedListeners.removeListener(listener, receiving);
/*  578 */       decrementPhases(processPhase(receiving));
/*      */     }
/*      */ 
/*  582 */     if ((sendingRemoved != null) && (sendingRemoved.size() > 0))
/*  583 */       disablePacketFilters(ConnectionSide.SERVER_SIDE, sendingRemoved);
/*  584 */     if ((receivingRemoved != null) && (receivingRemoved.size() > 0))
/*  585 */       disablePacketFilters(ConnectionSide.CLIENT_SIDE, receivingRemoved);
/*  586 */     updateRequireInputBuffers();
/*      */   }
/*      */ 
/*      */   public void removePacketListeners(Plugin plugin)
/*      */   {
/*  592 */     for (PacketListener listener : this.packetListeners)
/*      */     {
/*  594 */       if (Objects.equal(listener.getPlugin(), plugin)) {
/*  595 */         removePacketListener(listener);
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  600 */     this.asyncFilterManager.unregisterAsyncHandlers(plugin);
/*      */   }
/*      */ 
/*      */   public void invokePacketRecieving(PacketEvent event)
/*      */   {
/*  605 */     if (!this.hasClosed)
/*  606 */       handlePacket(this.recievedListeners, event, false);
/*      */   }
/*      */ 
/*      */   public void invokePacketSending(PacketEvent event)
/*      */   {
/*  612 */     if (!this.hasClosed)
/*  613 */       handlePacket(this.sendingListeners, event, true);
/*      */   }
/*      */ 
/*      */   public boolean requireInputBuffer(int packetId)
/*      */   {
/*  619 */     return this.inputBufferedPackets.contains(PacketType.findLegacy(packetId, PacketType.Sender.CLIENT));
/*      */   }
/*      */ 
/*      */   private void handlePacket(SortedPacketListenerList packetListeners, PacketEvent event, boolean sending)
/*      */   {
/*  631 */     if (this.asyncFilterManager.hasAsynchronousListeners(event)) {
/*  632 */       event.setAsyncMarker(this.asyncFilterManager.createAsyncMarker());
/*      */     }
/*      */ 
/*  636 */     if (sending)
/*  637 */       packetListeners.invokePacketSending(this.reporter, event);
/*      */     else {
/*  639 */       packetListeners.invokePacketRecieving(this.reporter, event);
/*      */     }
/*      */ 
/*  642 */     if ((!event.isCancelled()) && (!hasAsyncCancelled(event.getAsyncMarker()))) {
/*  643 */       this.asyncFilterManager.enqueueSyncPacket(event, event.getAsyncMarker());
/*      */ 
/*  646 */       event.setReadOnly(false);
/*  647 */       event.setCancelled(true);
/*      */     }
/*      */   }
/*      */ 
/*      */   private boolean hasAsyncCancelled(AsyncMarker marker)
/*      */   {
/*  653 */     return (marker == null) || (marker.isAsyncCancelled());
/*      */   }
/*      */ 
/*      */   private void enablePacketFilters(PacketListener listener, Iterable<PacketType> packets)
/*      */   {
/*  669 */     for (PacketType type : packets)
/*      */     {
/*  671 */       if (type.getSender() == PacketType.Sender.SERVER)
/*      */       {
/*  673 */         if ((!this.knowsServerPackets) || (PacketRegistry.getServerPacketTypes().contains(type)))
/*  674 */           this.playerInjection.addPacketHandler(type, listener.getSendingWhitelist().getOptions());
/*      */         else {
/*  676 */           this.reporter.reportWarning(this, Report.newBuilder(REPORT_UNSUPPORTED_SERVER_PACKET_ID).messageParam(new Object[] { PacketAdapter.getPluginName(listener), type }));
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*  682 */       if ((type.getSender() == PacketType.Sender.CLIENT) && (this.packetInjector != null))
/*  683 */         if ((!this.knowsClientPackets) || (PacketRegistry.getClientPacketTypes().contains(type)))
/*  684 */           this.packetInjector.addPacketHandler(type, listener.getReceivingWhitelist().getOptions());
/*      */         else
/*  686 */           this.reporter.reportWarning(this, Report.newBuilder(REPORT_UNSUPPORTED_CLIENT_PACKET_ID).messageParam(new Object[] { PacketAdapter.getPluginName(listener), type }));
/*      */     }
/*      */   }
/*      */ 
/*      */   private void disablePacketFilters(ConnectionSide side, Iterable<PacketType> packets)
/*      */   {
/*  699 */     if (side == null) {
/*  700 */       throw new IllegalArgumentException("side cannot be NULL.");
/*      */     }
/*  702 */     for (PacketType type : packets) {
/*  703 */       if (side.isForServer())
/*  704 */         this.playerInjection.removePacketHandler(type);
/*  705 */       if ((side.isForClient()) && (this.packetInjector != null))
/*  706 */         this.packetInjector.removePacketHandler(type);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void broadcastServerPacket(PacketContainer packet)
/*      */   {
/*  712 */     Preconditions.checkNotNull(packet, "packet cannot be NULL.");
/*  713 */     broadcastServerPacket(packet, Util.getOnlinePlayers());
/*      */   }
/*      */ 
/*      */   public void broadcastServerPacket(PacketContainer packet, Entity entity, boolean includeTracker)
/*      */   {
/*  718 */     Preconditions.checkNotNull(packet, "packet cannot be NULL.");
/*  719 */     Preconditions.checkNotNull(entity, "entity cannot be NULL.");
/*  720 */     List trackers = getEntityTrackers(entity);
/*      */ 
/*  723 */     if ((includeTracker) && ((entity instanceof Player))) {
/*  724 */       trackers.add((Player)entity);
/*      */     }
/*  726 */     broadcastServerPacket(packet, trackers);
/*      */   }
/*      */ 
/*      */   public void broadcastServerPacket(PacketContainer packet, Location origin, int maxObserverDistance)
/*      */   {
/*      */     try
/*      */     {
/*  733 */       maxDistance = maxObserverDistance * maxObserverDistance;
/*      */ 
/*  735 */       world = origin.getWorld();
/*  736 */       recycle = origin.clone();
/*      */ 
/*  739 */       for (Player player : this.server.getOnlinePlayers())
/*  740 */         if ((world.equals(player.getWorld())) && (getDistanceSquared(origin, recycle, player) <= maxDistance))
/*      */         {
/*  743 */           sendServerPacket(player, packet);
/*      */         }
/*      */     }
/*      */     catch (InvocationTargetException e)
/*      */     {
/*      */       int maxDistance;
/*      */       World world;
/*      */       Location recycle;
/*  748 */       throw new FieldAccessException("Unable to send server packet.", e);
/*      */     }
/*      */   }
/*      */ 
/*      */   private double getDistanceSquared(Location origin, Location recycle, Player player)
/*      */   {
/*  760 */     if (this.hasRecycleDistance) {
/*      */       try {
/*  762 */         return player.getLocation(recycle).distanceSquared(origin);
/*      */       }
/*      */       catch (Error e) {
/*  765 */         this.hasRecycleDistance = false;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  770 */     return player.getLocation().distanceSquared(origin);
/*      */   }
/*      */ 
/*      */   private void broadcastServerPacket(PacketContainer packet, Iterable<Player> players)
/*      */   {
/*      */     try
/*      */     {
/*  780 */       for (Player player : players)
/*  781 */         sendServerPacket(player, packet);
/*      */     }
/*      */     catch (InvocationTargetException e) {
/*  784 */       throw new FieldAccessException("Unable to send server packet.", e);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void sendServerPacket(Player reciever, PacketContainer packet) throws InvocationTargetException
/*      */   {
/*  790 */     sendServerPacket(reciever, packet, null, true);
/*      */   }
/*      */ 
/*      */   public void sendServerPacket(Player reciever, PacketContainer packet, boolean filters) throws InvocationTargetException
/*      */   {
/*  795 */     sendServerPacket(reciever, packet, null, filters);
/*      */   }
/*      */ 
/*      */   public void sendServerPacket(final Player receiver, final PacketContainer packet, NetworkMarker marker, final boolean filters) throws InvocationTargetException
/*      */   {
/*  800 */     if (receiver == null)
/*  801 */       throw new IllegalArgumentException("receiver cannot be NULL.");
/*  802 */     if (packet == null)
/*  803 */       throw new IllegalArgumentException("packet cannot be NULL.");
/*  804 */     if (packet.getType().getSender() == PacketType.Sender.CLIENT) {
/*  805 */       throw new IllegalArgumentException("Packet of sender CLIENT cannot be sent to a client.");
/*      */     }
/*      */ 
/*  808 */     if (this.packetCreation.compareAndSet(false, true)) {
/*  809 */       incrementPhases(GamePhase.PLAYING);
/*      */     }
/*  811 */     if (!filters)
/*      */     {
/*  813 */       if ((!filters) && (!Bukkit.isPrimaryThread()) && (this.playerInjection.hasMainThreadListener(packet.getType()))) {
/*  814 */         final NetworkMarker copy = marker;
/*      */ 
/*  816 */         this.server.getScheduler().scheduleSyncDelayedTask(this.library, new Runnable()
/*      */         {
/*      */           public void run()
/*      */           {
/*      */             try {
/*  821 */               if (!Bukkit.isPrimaryThread())
/*  822 */                 throw new IllegalStateException("Scheduled task was not executed on the main thread!");
/*  823 */               PacketFilterManager.this.sendServerPacket(receiver, packet, copy, filters);
/*      */             } catch (Exception e) {
/*  825 */               PacketFilterManager.this.reporter.reportMinimal(PacketFilterManager.this.library, "sendServerPacket-run()", e);
/*      */             }
/*      */           }
/*      */         });
/*  829 */         return;
/*      */       }
/*      */ 
/*  832 */       PacketEvent event = PacketEvent.fromServer(this, packet, marker, receiver, false);
/*  833 */       this.sendingListeners.invokePacketSending(this.reporter, event, ListenerPriority.MONITOR);
/*  834 */       marker = NetworkMarker.getNetworkMarker(event);
/*      */     }
/*  836 */     this.playerInjection.sendServerPacket(receiver, packet, marker, filters);
/*      */   }
/*      */ 
/*      */   public void sendWirePacket(Player receiver, int id, byte[] bytes) throws InvocationTargetException
/*      */   {
/*  841 */     WirePacket packet = new WirePacket(id, bytes);
/*  842 */     sendWirePacket(receiver, packet);
/*      */   }
/*      */ 
/*      */   public void sendWirePacket(Player receiver, WirePacket packet) throws InvocationTargetException
/*      */   {
/*  847 */     Channel channel = this.playerInjection.getChannel(receiver);
/*  848 */     if (channel == null) {
/*  849 */       throw new InvocationTargetException(new NullPointerException(), "Failed to obtain channel for " + receiver.getName());
/*      */     }
/*      */ 
/*  852 */     channel.writeAndFlush(packet);
/*      */   }
/*      */ 
/*      */   public void recieveClientPacket(Player sender, PacketContainer packet) throws IllegalAccessException, InvocationTargetException
/*      */   {
/*  857 */     recieveClientPacket(sender, packet, null, true);
/*      */   }
/*      */ 
/*      */   public void recieveClientPacket(Player sender, PacketContainer packet, boolean filters) throws IllegalAccessException, InvocationTargetException
/*      */   {
/*  862 */     recieveClientPacket(sender, packet, null, filters);
/*      */   }
/*      */ 
/*      */   public void recieveClientPacket(Player sender, PacketContainer packet, NetworkMarker marker, boolean filters) throws IllegalAccessException, InvocationTargetException
/*      */   {
/*  867 */     if (sender == null)
/*  868 */       throw new IllegalArgumentException("sender cannot be NULL.");
/*  869 */     if (packet == null)
/*  870 */       throw new IllegalArgumentException("packet cannot be NULL.");
/*  871 */     if (packet.getType().getSender() == PacketType.Sender.SERVER) {
/*  872 */       throw new IllegalArgumentException("Packet of sender SERVER cannot be sent to the server.");
/*      */     }
/*      */ 
/*  875 */     if (this.packetCreation.compareAndSet(false, true)) {
/*  876 */       incrementPhases(GamePhase.PLAYING);
/*      */     }
/*  878 */     Object mcPacket = packet.getHandle();
/*  879 */     boolean cancelled = this.packetInjector.isCancelled(mcPacket);
/*      */ 
/*  882 */     if (cancelled) {
/*  883 */       this.packetInjector.setCancelled(mcPacket, false);
/*      */     }
/*      */ 
/*  886 */     if (filters) {
/*  887 */       byte[] data = NetworkMarker.getByteBuffer(marker);
/*  888 */       PacketEvent event = this.packetInjector.packetRecieved(packet, sender, data);
/*      */ 
/*  890 */       if (!event.isCancelled())
/*  891 */         mcPacket = event.getPacket().getHandle();
/*      */       else
/*  893 */         return;
/*      */     }
/*      */     else
/*      */     {
/*  897 */       this.recievedListeners.invokePacketSending(this.reporter, PacketEvent.fromClient(this, packet, marker, sender, false), ListenerPriority.MONITOR);
/*      */     }
/*      */ 
/*  903 */     this.playerInjection.recieveClientPacket(sender, mcPacket);
/*      */ 
/*  906 */     if (cancelled)
/*  907 */       this.packetInjector.setCancelled(mcPacket, true);
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public PacketContainer createPacket(int id)
/*      */   {
/*  914 */     return createPacket(PacketType.findLegacy(id), true);
/*      */   }
/*      */ 
/*      */   public PacketContainer createPacket(PacketType type)
/*      */   {
/*  919 */     return createPacket(type, true);
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public PacketContainer createPacket(int id, boolean forceDefaults)
/*      */   {
/*  925 */     return createPacket(PacketType.findLegacy(id), forceDefaults);
/*      */   }
/*      */ 
/*      */   public PacketContainer createPacket(PacketType type, boolean forceDefaults)
/*      */   {
/*  930 */     PacketContainer packet = new PacketContainer(type);
/*      */ 
/*  933 */     if (forceDefaults) {
/*      */       try {
/*  935 */         packet.getModifier().writeDefaults();
/*      */       } catch (FieldAccessException e) {
/*  937 */         throw new RuntimeException("Security exception.", e);
/*      */       }
/*      */     }
/*      */ 
/*  941 */     return packet;
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public PacketConstructor createPacketConstructor(int id, Object[] arguments)
/*      */   {
/*  947 */     return PacketConstructor.DEFAULT.withPacket(id, arguments);
/*      */   }
/*      */ 
/*      */   public PacketConstructor createPacketConstructor(PacketType type, Object[] arguments)
/*      */   {
/*  952 */     return PacketConstructor.DEFAULT.withPacket(type, arguments);
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public Set<Integer> getSendingFilters()
/*      */   {
/*  958 */     return PacketRegistry.toLegacy(this.playerInjection.getSendingFilters());
/*      */   }
/*      */ 
/*      */   public Set<Integer> getReceivingFilters()
/*      */   {
/*  963 */     return PacketRegistry.toLegacy(this.packetInjector.getPacketHandlers());
/*      */   }
/*      */ 
/*      */   public Set<PacketType> getSendingFilterTypes() {
/*  967 */     return Collections.unmodifiableSet(this.playerInjection.getSendingFilters());
/*      */   }
/*      */ 
/*      */   public Set<PacketType> getReceivingFilterTypes()
/*      */   {
/*  972 */     return Collections.unmodifiableSet(this.packetInjector.getPacketHandlers());
/*      */   }
/*      */ 
/*      */   public void updateEntity(Entity entity, List<Player> observers) throws FieldAccessException
/*      */   {
/*  977 */     EntityUtilities.updateEntity(entity, observers);
/*      */   }
/*      */ 
/*      */   public Entity getEntityFromID(World container, int id) throws FieldAccessException
/*      */   {
/*  982 */     return EntityUtilities.getEntityFromID(container, id);
/*      */   }
/*      */ 
/*      */   public List<Player> getEntityTrackers(Entity entity) throws FieldAccessException
/*      */   {
/*  987 */     return EntityUtilities.getEntityTrackers(entity);
/*      */   }
/*      */ 
/*      */   public void initializePlayers(List<Player> players)
/*      */   {
/*  995 */     for (Player player : players)
/*  996 */       this.playerInjection.injectPlayer(player, PlayerInjectionHandler.ConflictStrategy.OVERRIDE);
/*      */   }
/*      */ 
/*      */   public void uninitializePlayers(List<Player> players)
/*      */   {
/* 1004 */     for (Player player : players)
/* 1005 */       this.playerInjection.uninjectPlayer(player);
/*      */   }
/*      */ 
/*      */   public void registerEvents(PluginManager manager, final Plugin plugin)
/*      */   {
/* 1017 */     if ((this.spigotInjector != null) && (!this.spigotInjector.register(plugin)))
/* 1018 */       throw new IllegalArgumentException("Spigot has already been registered.");
/* 1019 */     if (this.nettyInjector != null)
/* 1020 */       this.nettyInjector.inject();
/*      */     try
/*      */     {
/* 1023 */       manager.registerEvents(new Listener()
/*      */       {
/*      */         @EventHandler(priority=EventPriority.LOWEST)
/*      */         public void onPlayerLogin(PlayerLoginEvent event) {
/* 1027 */           PacketFilterManager.this.onPlayerLogin(event);
/*      */         }
/*      */ 
/*      */         @EventHandler(priority=EventPriority.LOWEST)
/*      */         public void onPrePlayerJoin(PlayerJoinEvent event) {
/* 1032 */           PacketFilterManager.this.onPrePlayerJoin(event);
/*      */         }
/*      */ 
/*      */         @EventHandler(priority=EventPriority.MONITOR)
/*      */         public void onPlayerJoin(PlayerJoinEvent event) {
/* 1037 */           PacketFilterManager.this.onPlayerJoin(event);
/*      */         }
/*      */ 
/*      */         @EventHandler(priority=EventPriority.MONITOR)
/*      */         public void onPlayerQuit(PlayerQuitEvent event) {
/* 1042 */           PacketFilterManager.this.onPlayerQuit(event);
/*      */         }
/*      */ 
/*      */         @EventHandler(priority=EventPriority.MONITOR)
/*      */         public void onPluginDisabled(PluginDisableEvent event) {
/* 1047 */           PacketFilterManager.this.onPluginDisabled(event, plugin);
/*      */         }
/*      */       }
/*      */       , plugin);
/*      */     }
/*      */     catch (NoSuchMethodError e)
/*      */     {
/* 1054 */       registerOld(manager, plugin);
/*      */     }
/*      */   }
/*      */ 
/*      */   private void onPlayerLogin(PlayerLoginEvent event) {
/* 1059 */     this.playerInjection.updatePlayer(event.getPlayer());
/*      */   }
/*      */ 
/*      */   private void onPrePlayerJoin(PlayerJoinEvent event) {
/* 1063 */     this.playerInjection.updatePlayer(event.getPlayer());
/*      */   }
/*      */ 
/*      */   private void onPlayerJoin(PlayerJoinEvent event)
/*      */   {
/*      */     try {
/* 1069 */       this.playerInjection.uninjectPlayer(event.getPlayer().getAddress());
/* 1070 */       this.playerInjection.injectPlayer(event.getPlayer(), PlayerInjectionHandler.ConflictStrategy.OVERRIDE);
/*      */     } catch (PlayerInjector.ServerHandlerNull e) {
/*      */     }
/*      */     catch (Exception e) {
/* 1074 */       this.reporter.reportDetailed(this, Report.newBuilder(REPORT_CANNOT_INJECT_PLAYER).callerParam(new Object[] { event }).error(e));
/*      */     }
/*      */   }
/*      */ 
/*      */   private void onPlayerQuit(PlayerQuitEvent event)
/*      */   {
/*      */     try
/*      */     {
/* 1082 */       Player player = event.getPlayer();
/*      */ 
/* 1084 */       this.asyncFilterManager.removePlayer(player);
/* 1085 */       this.playerInjection.handleDisconnect(player);
/* 1086 */       this.playerInjection.uninjectPlayer(player);
/*      */     } catch (Exception e) {
/* 1088 */       this.reporter.reportDetailed(this, Report.newBuilder(REPORT_CANNOT_UNINJECT_OFFLINE_PLAYER).callerParam(new Object[] { event }).error(e));
/*      */     }
/*      */   }
/*      */ 
/*      */   private void onPluginDisabled(PluginDisableEvent event, Plugin protocolLibrary)
/*      */   {
/*      */     try
/*      */     {
/* 1097 */       if (event.getPlugin() != protocolLibrary)
/* 1098 */         removePacketListeners(event.getPlugin());
/*      */     }
/*      */     catch (Exception e) {
/* 1101 */       this.reporter.reportDetailed(this, Report.newBuilder(REPORT_CANNOT_UNREGISTER_PLUGIN).callerParam(new Object[] { event }).error(e));
/*      */     }
/*      */   }
/*      */ 
/*      */   private int getPhasePlayingCount()
/*      */   {
/* 1112 */     return this.phasePlayingCount.get();
/*      */   }
/*      */ 
/*      */   private int getPhaseLoginCount()
/*      */   {
/* 1120 */     return this.phaseLoginCount.get();
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public int getPacketID(Object packet)
/*      */   {
/* 1126 */     return PacketRegistry.getPacketID(packet.getClass());
/*      */   }
/*      */ 
/*      */   public PacketType getPacketType(Object packet)
/*      */   {
/* 1131 */     if (packet == null)
/* 1132 */       throw new IllegalArgumentException("Packet cannot be NULL.");
/* 1133 */     if (!MinecraftReflection.isPacketClass(packet)) {
/* 1134 */       throw new IllegalArgumentException("The given object " + packet + " is not a packet.");
/*      */     }
/* 1136 */     PacketType type = PacketRegistry.getPacketType(packet.getClass());
/*      */ 
/* 1138 */     if (type != null) {
/* 1139 */       return type;
/*      */     }
/* 1141 */     throw new IllegalArgumentException("Unable to find associated packet of " + packet + ": Lookup returned NULL.");
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public void registerPacketClass(Class<?> clazz, int packetID)
/*      */   {
/* 1149 */     PacketRegistry.getPacketToID().put(clazz, Integer.valueOf(packetID));
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public void unregisterPacketClass(Class<?> clazz)
/*      */   {
/* 1155 */     PacketRegistry.getPacketToID().remove(clazz);
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public Class<?> getPacketClassFromID(int packetID, boolean forceVanilla)
/*      */   {
/* 1161 */     return PacketRegistry.getPacketClassFromID(packetID, forceVanilla);
/*      */   }
/*      */ 
/*      */   private void registerOld(PluginManager manager, final Plugin plugin)
/*      */   {
/*      */     try
/*      */     {
/* 1168 */       ClassLoader loader = manager.getClass().getClassLoader();
/*      */ 
/* 1171 */       Class eventTypes = loader.loadClass("org.bukkit.event.Event$Type");
/* 1172 */       Class eventPriority = loader.loadClass("org.bukkit.event.Event$Priority");
/*      */ 
/* 1175 */       Object priorityLowest = Enum.valueOf(eventPriority, "Lowest");
/* 1176 */       Object priorityMonitor = Enum.valueOf(eventPriority, "Monitor");
/*      */ 
/* 1179 */       Object playerJoinType = Enum.valueOf(eventTypes, "PLAYER_JOIN");
/* 1180 */       Object playerQuitType = Enum.valueOf(eventTypes, "PLAYER_QUIT");
/* 1181 */       Object pluginDisabledType = Enum.valueOf(eventTypes, "PLUGIN_DISABLE");
/*      */ 
/* 1184 */       Class playerListener = loader.loadClass("org.bukkit.event.player.PlayerListener");
/* 1185 */       Class serverListener = loader.loadClass("org.bukkit.event.server.ServerListener");
/*      */ 
/* 1188 */       Method registerEvent = FuzzyReflection.fromObject(manager).getMethodByParameters("registerEvent", new Class[] { eventTypes, Listener.class, eventPriority, Plugin.class });
/*      */ 
/* 1191 */       Enhancer playerLow = EnhancerFactory.getInstance().createEnhancer();
/* 1192 */       Enhancer playerEx = EnhancerFactory.getInstance().createEnhancer();
/* 1193 */       Enhancer serverEx = EnhancerFactory.getInstance().createEnhancer();
/*      */ 
/* 1195 */       playerLow.setSuperclass(playerListener);
/* 1196 */       playerLow.setClassLoader(this.classLoader);
/* 1197 */       playerLow.setCallback(new MethodInterceptor()
/*      */       {
/*      */         public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy)
/*      */           throws Throwable
/*      */         {
/* 1202 */           if (args.length == 1) {
/* 1203 */             Object event = args[0];
/*      */ 
/* 1205 */             if ((event instanceof PlayerJoinEvent)) {
/* 1206 */               PacketFilterManager.this.onPrePlayerJoin((PlayerJoinEvent)event);
/*      */             }
/*      */           }
/* 1209 */           return null;
/*      */         }
/*      */       });
/* 1213 */       playerEx.setSuperclass(playerListener);
/* 1214 */       playerEx.setClassLoader(this.classLoader);
/* 1215 */       playerEx.setCallback(new MethodInterceptor()
/*      */       {
/*      */         public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
/* 1218 */           if (args.length == 1) {
/* 1219 */             Object event = args[0];
/*      */ 
/* 1222 */             if ((event instanceof PlayerJoinEvent))
/* 1223 */               PacketFilterManager.this.onPlayerJoin((PlayerJoinEvent)event);
/* 1224 */             else if ((event instanceof PlayerQuitEvent)) {
/* 1225 */               PacketFilterManager.this.onPlayerQuit((PlayerQuitEvent)event);
/*      */             }
/*      */           }
/* 1228 */           return null;
/*      */         }
/*      */       });
/* 1232 */       serverEx.setSuperclass(serverListener);
/* 1233 */       serverEx.setClassLoader(this.classLoader);
/* 1234 */       serverEx.setCallback(new MethodInterceptor()
/*      */       {
/*      */         public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy)
/*      */           throws Throwable
/*      */         {
/* 1239 */           if (args.length == 1) {
/* 1240 */             Object event = args[0];
/*      */ 
/* 1242 */             if ((event instanceof PluginDisableEvent))
/* 1243 */               PacketFilterManager.this.onPluginDisabled((PluginDisableEvent)event, plugin);
/*      */           }
/* 1245 */           return null;
/*      */         }
/*      */       });
/* 1250 */       Object playerProxyLow = playerLow.create();
/* 1251 */       Object playerProxy = playerEx.create();
/* 1252 */       Object serverProxy = serverEx.create();
/*      */ 
/* 1254 */       registerEvent.invoke(manager, new Object[] { playerJoinType, playerProxyLow, priorityLowest, plugin });
/* 1255 */       registerEvent.invoke(manager, new Object[] { playerJoinType, playerProxy, priorityMonitor, plugin });
/* 1256 */       registerEvent.invoke(manager, new Object[] { playerQuitType, playerProxy, priorityMonitor, plugin });
/* 1257 */       registerEvent.invoke(manager, new Object[] { pluginDisabledType, serverProxy, priorityMonitor, plugin });
/*      */     }
/*      */     catch (ClassNotFoundException e1)
/*      */     {
/* 1261 */       e1.printStackTrace();
/*      */     } catch (IllegalArgumentException e) {
/* 1263 */       e.printStackTrace();
/*      */     } catch (IllegalAccessException e) {
/* 1265 */       e.printStackTrace();
/*      */     } catch (InvocationTargetException e) {
/* 1267 */       e.printStackTrace();
/*      */     }
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public static Set<Integer> getServerPackets()
/*      */     throws FieldAccessException
/*      */   {
/* 1278 */     return PacketRegistry.getServerPackets();
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public static Set<Integer> getClientPackets()
/*      */     throws FieldAccessException
/*      */   {
/* 1288 */     return PacketRegistry.getClientPackets();
/*      */   }
/*      */ 
/*      */   public ClassLoader getClassLoader()
/*      */   {
/* 1296 */     return this.classLoader;
/*      */   }
/*      */ 
/*      */   public boolean isClosed()
/*      */   {
/* 1301 */     return this.hasClosed;
/*      */   }
/*      */ 
/*      */   public void close()
/*      */   {
/* 1307 */     if (this.hasClosed) {
/* 1308 */       return;
/*      */     }
/*      */ 
/* 1311 */     if (this.packetInjector != null)
/* 1312 */       this.packetInjector.cleanupAll();
/* 1313 */     if (this.spigotInjector != null)
/* 1314 */       this.spigotInjector.cleanupAll();
/* 1315 */     if (this.nettyInjector != null) {
/* 1316 */       this.nettyInjector.close();
/*      */     }
/*      */ 
/* 1319 */     this.playerInjection.close();
/* 1320 */     this.hasClosed = true;
/*      */ 
/* 1323 */     this.packetListeners.clear();
/* 1324 */     this.recievedListeners = null;
/* 1325 */     this.sendingListeners = null;
/*      */ 
/* 1328 */     this.interceptWritePacket.cleanup();
/*      */ 
/* 1331 */     this.asyncFilterManager.cleanupAll();
/*      */   }
/*      */ 
/*      */   protected void finalize() throws Throwable
/*      */   {
/* 1336 */     close();
/*      */   }
/*      */ 
/*      */   public static enum PlayerInjectHooks
/*      */   {
/*  127 */     NONE, 
/*      */ 
/*  134 */     NETWORK_MANAGER_OBJECT, 
/*      */ 
/*  141 */     NETWORK_HANDLER_FIELDS, 
/*      */ 
/*  146 */     NETWORK_SERVER_OBJECT;
/*      */   }
/*      */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.injector.PacketFilterManager
 * JD-Core Version:    0.6.2
 */