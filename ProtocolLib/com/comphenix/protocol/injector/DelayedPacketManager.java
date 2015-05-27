/*     */ package com.comphenix.protocol.injector;
/*     */ 
/*     */ import com.comphenix.protocol.AsynchronousManager;
/*     */ import com.comphenix.protocol.PacketType;
/*     */ import com.comphenix.protocol.PacketType.Sender;
/*     */ import com.comphenix.protocol.ProtocolManager;
/*     */ import com.comphenix.protocol.error.ErrorReporter;
/*     */ import com.comphenix.protocol.error.Report;
/*     */ import com.comphenix.protocol.error.Report.ReportBuilder;
/*     */ import com.comphenix.protocol.error.ReportType;
/*     */ import com.comphenix.protocol.events.ConnectionSide;
/*     */ import com.comphenix.protocol.events.ListeningWhitelist;
/*     */ import com.comphenix.protocol.events.NetworkMarker;
/*     */ import com.comphenix.protocol.events.PacketContainer;
/*     */ import com.comphenix.protocol.events.PacketListener;
/*     */ import com.comphenix.protocol.injector.netty.WirePacket;
/*     */ import com.comphenix.protocol.injector.packet.PacketRegistry;
/*     */ import com.comphenix.protocol.reflect.FieldAccessException;
/*     */ import com.comphenix.protocol.reflect.StructureModifier;
/*     */ import com.comphenix.protocol.utility.MinecraftVersion;
/*     */ import com.google.common.base.Objects;
/*     */ import com.google.common.base.Preconditions;
/*     */ import com.google.common.collect.ImmutableSet;
/*     */ import com.google.common.collect.Lists;
/*     */ import com.google.common.collect.Sets;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.util.Collections;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Set;
/*     */ import javax.annotation.Nonnull;
/*     */ import org.bukkit.Location;
/*     */ import org.bukkit.World;
/*     */ import org.bukkit.entity.Entity;
/*     */ import org.bukkit.entity.Player;
/*     */ import org.bukkit.plugin.Plugin;
/*     */ import org.bukkit.plugin.PluginManager;
/*     */ 
/*     */ public class DelayedPacketManager
/*     */   implements ProtocolManager, InternalManager
/*     */ {
/*  48 */   public static final ReportType REPORT_CANNOT_SEND_QUEUED_PACKET = new ReportType("Cannot send queued packet %s.");
/*  49 */   public static final ReportType REPORT_CANNOT_SEND_QUEUED_WIRE_PACKET = new ReportType("Cannot send queued wire packet %s.");
/*  50 */   public static final ReportType REPORT_CANNOT_REGISTER_QUEUED_LISTENER = new ReportType("Cannot register queued listener %s.");
/*     */   private volatile InternalManager delegate;
/*  55 */   private final List<Runnable> queuedActions = Collections.synchronizedList(Lists.newArrayList());
/*  56 */   private final List<PacketListener> queuedListeners = Collections.synchronizedList(Lists.newArrayList());
/*     */   private AsynchronousManager asyncManager;
/*     */   private ErrorReporter reporter;
/*  62 */   private PacketFilterManager.PlayerInjectHooks hook = PacketFilterManager.PlayerInjectHooks.NETWORK_SERVER_OBJECT;
/*     */   private boolean closed;
/*     */   private boolean debug;
/*     */   private PluginManager queuedManager;
/*     */   private Plugin queuedPlugin;
/*     */   private MinecraftVersion version;
/*     */ 
/*     */   public DelayedPacketManager(@Nonnull ErrorReporter reporter, @Nonnull MinecraftVersion version)
/*     */   {
/*  75 */     Preconditions.checkNotNull(reporter, "reporter cannot be NULL.");
/*  76 */     Preconditions.checkNotNull(version, "version cannot be NULL.");
/*     */ 
/*  78 */     this.reporter = reporter;
/*  79 */     this.version = version;
/*     */   }
/*     */ 
/*     */   public InternalManager getDelegate()
/*     */   {
/*  87 */     return this.delegate;
/*     */   }
/*     */ 
/*     */   public int getProtocolVersion(Player player)
/*     */   {
/*  92 */     if (this.delegate != null) {
/*  93 */       return this.delegate.getProtocolVersion(player);
/*     */     }
/*  95 */     return -2147483648;
/*     */   }
/*     */ 
/*     */   public MinecraftVersion getMinecraftVersion()
/*     */   {
/* 100 */     if (this.delegate != null) {
/* 101 */       return this.delegate.getMinecraftVersion();
/*     */     }
/* 103 */     return this.version;
/*     */   }
/*     */ 
/*     */   protected void setDelegate(InternalManager delegate)
/*     */   {
/* 114 */     this.delegate = delegate;
/*     */ 
/* 116 */     if (delegate != null)
/*     */     {
/* 118 */       if (!Objects.equal(delegate.getPlayerHook(), this.hook)) {
/* 119 */         delegate.setPlayerHook(this.hook);
/*     */       }
/*     */ 
/* 122 */       if ((this.queuedManager != null) && (this.queuedPlugin != null)) {
/* 123 */         delegate.registerEvents(this.queuedManager, this.queuedPlugin);
/*     */       }
/*     */ 
/* 126 */       delegate.setDebug(this.debug);
/*     */ 
/* 129 */       synchronized (this.queuedListeners) {
/* 130 */         for (PacketListener listener : this.queuedListeners) {
/*     */           try {
/* 132 */             delegate.addPacketListener(listener);
/*     */           }
/*     */           catch (IllegalArgumentException e) {
/* 135 */             this.reporter.reportWarning(this, Report.newBuilder(REPORT_CANNOT_REGISTER_QUEUED_LISTENER).callerParam(new Object[] { delegate }).messageParam(new Object[] { listener }).error(e));
/*     */           }
/*     */ 
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 143 */       synchronized (this.queuedActions) {
/* 144 */         for (Runnable action : this.queuedActions) {
/* 145 */           action.run();
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 150 */       this.queuedListeners.clear();
/* 151 */       this.queuedActions.clear();
/*     */     }
/*     */   }
/*     */ 
/*     */   private Runnable queuedAddPacket(final ConnectionSide side, final Player player, final PacketContainer packet, final NetworkMarker marker, final boolean filtered)
/*     */   {
/* 158 */     return new Runnable()
/*     */     {
/*     */       public void run()
/*     */       {
/*     */         try {
/* 163 */           switch (DelayedPacketManager.6.$SwitchMap$com$comphenix$protocol$events$ConnectionSide[side.ordinal()]) {
/*     */           case 1:
/* 165 */             DelayedPacketManager.this.delegate.recieveClientPacket(player, packet, marker, filtered);
/* 166 */             break;
/*     */           case 2:
/* 168 */             DelayedPacketManager.this.delegate.sendServerPacket(player, packet, marker, filtered);
/* 169 */             break;
/*     */           default:
/* 171 */             throw new IllegalArgumentException("side cannot be " + side);
/*     */           }
/*     */         }
/*     */         catch (Exception e) {
/* 175 */           DelayedPacketManager.this.reporter.reportWarning(this, Report.newBuilder(DelayedPacketManager.REPORT_CANNOT_SEND_QUEUED_PACKET).callerParam(new Object[] { DelayedPacketManager.this.delegate }).messageParam(new Object[] { packet }).error(e));
/*     */         }
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   public void setPlayerHook(PacketFilterManager.PlayerInjectHooks playerHook)
/*     */   {
/* 185 */     this.hook = playerHook;
/*     */   }
/*     */ 
/*     */   public PacketFilterManager.PlayerInjectHooks getPlayerHook()
/*     */   {
/* 190 */     return this.hook;
/*     */   }
/*     */ 
/*     */   public void sendServerPacket(Player receiver, PacketContainer packet) throws InvocationTargetException
/*     */   {
/* 195 */     sendServerPacket(receiver, packet, null, true);
/*     */   }
/*     */ 
/*     */   public void sendServerPacket(Player receiver, PacketContainer packet, boolean filters) throws InvocationTargetException
/*     */   {
/* 200 */     sendServerPacket(receiver, packet, null, filters);
/*     */   }
/*     */ 
/*     */   public void sendServerPacket(Player receiver, PacketContainer packet, NetworkMarker marker, boolean filters) throws InvocationTargetException
/*     */   {
/* 205 */     if (this.delegate != null)
/* 206 */       this.delegate.sendServerPacket(receiver, packet, marker, filters);
/*     */     else
/* 208 */       this.queuedActions.add(queuedAddPacket(ConnectionSide.SERVER_SIDE, receiver, packet, marker, filters));
/*     */   }
/*     */ 
/*     */   public void sendWirePacket(Player receiver, int id, byte[] bytes)
/*     */     throws InvocationTargetException
/*     */   {
/* 214 */     WirePacket packet = new WirePacket(id, bytes);
/* 215 */     sendWirePacket(receiver, packet);
/*     */   }
/*     */ 
/*     */   public void sendWirePacket(final Player receiver, final WirePacket packet) throws InvocationTargetException
/*     */   {
/* 220 */     if (this.delegate != null)
/* 221 */       this.delegate.sendWirePacket(receiver, packet);
/*     */     else
/* 223 */       this.queuedActions.add(new Runnable()
/*     */       {
/*     */         public void run()
/*     */         {
/*     */           try {
/* 228 */             DelayedPacketManager.this.delegate.sendWirePacket(receiver, packet);
/*     */           }
/*     */           catch (Throwable ex) {
/* 231 */             DelayedPacketManager.this.reporter.reportWarning(this, Report.newBuilder(DelayedPacketManager.REPORT_CANNOT_SEND_QUEUED_WIRE_PACKET).callerParam(new Object[] { DelayedPacketManager.this.delegate }).messageParam(new Object[] { packet }).error(ex));
/*     */           }
/*     */         }
/*     */       });
/*     */   }
/*     */ 
/*     */   public void recieveClientPacket(Player sender, PacketContainer packet)
/*     */     throws IllegalAccessException, InvocationTargetException
/*     */   {
/* 244 */     recieveClientPacket(sender, packet, null, true);
/*     */   }
/*     */ 
/*     */   public void recieveClientPacket(Player sender, PacketContainer packet, boolean filters) throws IllegalAccessException, InvocationTargetException
/*     */   {
/* 249 */     recieveClientPacket(sender, packet, null, filters);
/*     */   }
/*     */ 
/*     */   public void recieveClientPacket(Player sender, PacketContainer packet, NetworkMarker marker, boolean filters) throws IllegalAccessException, InvocationTargetException
/*     */   {
/* 254 */     if (this.delegate != null)
/* 255 */       this.delegate.recieveClientPacket(sender, packet, marker, filters);
/*     */     else
/* 257 */       this.queuedActions.add(queuedAddPacket(ConnectionSide.CLIENT_SIDE, sender, packet, marker, filters));
/*     */   }
/*     */ 
/*     */   public void broadcastServerPacket(final PacketContainer packet, final Entity entity, final boolean includeTracker)
/*     */   {
/* 263 */     if (this.delegate != null)
/* 264 */       this.delegate.broadcastServerPacket(packet, entity, includeTracker);
/*     */     else
/* 266 */       this.queuedActions.add(new Runnable()
/*     */       {
/*     */         public void run() {
/* 269 */           DelayedPacketManager.this.delegate.broadcastServerPacket(packet, entity, includeTracker);
/*     */         }
/*     */       });
/*     */   }
/*     */ 
/*     */   public void broadcastServerPacket(final PacketContainer packet, final Location origin, final int maxObserverDistance)
/*     */   {
/* 277 */     if (this.delegate != null)
/* 278 */       this.delegate.broadcastServerPacket(packet, origin, maxObserverDistance);
/*     */     else
/* 280 */       this.queuedActions.add(new Runnable()
/*     */       {
/*     */         public void run() {
/* 283 */           DelayedPacketManager.this.delegate.broadcastServerPacket(packet, origin, maxObserverDistance);
/*     */         }
/*     */       });
/*     */   }
/*     */ 
/*     */   public void broadcastServerPacket(final PacketContainer packet)
/*     */   {
/* 291 */     if (this.delegate != null)
/* 292 */       this.delegate.broadcastServerPacket(packet);
/*     */     else
/* 294 */       this.queuedActions.add(new Runnable()
/*     */       {
/*     */         public void run() {
/* 297 */           DelayedPacketManager.this.delegate.broadcastServerPacket(packet);
/*     */         }
/*     */       });
/*     */   }
/*     */ 
/*     */   public ImmutableSet<PacketListener> getPacketListeners()
/*     */   {
/* 305 */     if (this.delegate != null) {
/* 306 */       return this.delegate.getPacketListeners();
/*     */     }
/* 308 */     return ImmutableSet.copyOf(this.queuedListeners);
/*     */   }
/*     */ 
/*     */   public void addPacketListener(PacketListener listener)
/*     */   {
/* 313 */     if (this.delegate != null)
/* 314 */       this.delegate.addPacketListener(listener);
/*     */     else
/* 316 */       this.queuedListeners.add(listener);
/*     */   }
/*     */ 
/*     */   public void removePacketListener(PacketListener listener)
/*     */   {
/* 321 */     if (this.delegate != null)
/* 322 */       this.delegate.removePacketListener(listener);
/*     */     else
/* 324 */       this.queuedListeners.remove(listener);
/*     */   }
/*     */ 
/*     */   public void removePacketListeners(Plugin plugin)
/*     */   {
/*     */     Iterator it;
/* 329 */     if (this.delegate != null)
/* 330 */       this.delegate.removePacketListeners(plugin);
/*     */     else
/* 332 */       for (it = this.queuedListeners.iterator(); it.hasNext(); )
/*     */       {
/* 334 */         if (Objects.equal(((PacketListener)it.next()).getPlugin(), plugin))
/* 335 */           it.remove();
/*     */       }
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public PacketContainer createPacket(int id)
/*     */   {
/* 344 */     if (this.delegate != null)
/* 345 */       return this.delegate.createPacket(id);
/* 346 */     return createPacket(id, true);
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public PacketContainer createPacket(int id, boolean forceDefaults)
/*     */   {
/* 352 */     if (this.delegate != null) {
/* 353 */       return this.delegate.createPacket(id);
/*     */     }
/*     */ 
/* 356 */     PacketContainer packet = new PacketContainer(id);
/*     */ 
/* 359 */     if (forceDefaults) {
/*     */       try {
/* 361 */         packet.getModifier().writeDefaults();
/*     */       } catch (FieldAccessException e) {
/* 363 */         throw new RuntimeException("Security exception.", e);
/*     */       }
/*     */     }
/* 366 */     return packet;
/*     */   }
/*     */ 
/*     */   public PacketConstructor createPacketConstructor(int id, Object[] arguments)
/*     */   {
/* 373 */     if (this.delegate != null) {
/* 374 */       return this.delegate.createPacketConstructor(id, arguments);
/*     */     }
/* 376 */     return PacketConstructor.DEFAULT.withPacket(id, arguments);
/*     */   }
/*     */ 
/*     */   public PacketConstructor createPacketConstructor(PacketType type, Object[] arguments)
/*     */   {
/* 381 */     if (this.delegate != null) {
/* 382 */       return this.delegate.createPacketConstructor(type, arguments);
/*     */     }
/* 384 */     return PacketConstructor.DEFAULT.withPacket(type, arguments);
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public Set<Integer> getSendingFilters()
/*     */   {
/* 390 */     if (this.delegate != null) {
/* 391 */       return this.delegate.getSendingFilters();
/*     */     }
/*     */ 
/* 394 */     Set sending = Sets.newHashSet();
/*     */ 
/* 396 */     for (PacketListener listener : this.queuedListeners) {
/* 397 */       sending.addAll(listener.getSendingWhitelist().getWhitelist());
/*     */     }
/* 399 */     return sending;
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public Set<Integer> getReceivingFilters()
/*     */   {
/* 406 */     if (this.delegate != null) {
/* 407 */       return this.delegate.getReceivingFilters();
/*     */     }
/* 409 */     Set recieving = Sets.newHashSet();
/*     */ 
/* 411 */     for (PacketListener listener : this.queuedListeners) {
/* 412 */       recieving.addAll(listener.getReceivingWhitelist().getWhitelist());
/*     */     }
/* 414 */     return recieving;
/*     */   }
/*     */ 
/*     */   public PacketContainer createPacket(PacketType type)
/*     */   {
/* 420 */     return createPacket(type.getLegacyId());
/*     */   }
/*     */ 
/*     */   public PacketContainer createPacket(PacketType type, boolean forceDefaults)
/*     */   {
/* 425 */     return createPacket(type.getLegacyId(), forceDefaults);
/*     */   }
/*     */ 
/*     */   public Set<PacketType> getSendingFilterTypes()
/*     */   {
/* 430 */     return PacketRegistry.toPacketTypes(getSendingFilters(), PacketType.Sender.SERVER);
/*     */   }
/*     */ 
/*     */   public Set<PacketType> getReceivingFilterTypes()
/*     */   {
/* 435 */     return PacketRegistry.toPacketTypes(getReceivingFilters(), PacketType.Sender.CLIENT);
/*     */   }
/*     */ 
/*     */   public void updateEntity(Entity entity, List<Player> observers) throws FieldAccessException
/*     */   {
/* 440 */     if (this.delegate != null)
/* 441 */       this.delegate.updateEntity(entity, observers);
/*     */     else
/* 443 */       EntityUtilities.updateEntity(entity, observers);
/*     */   }
/*     */ 
/*     */   public Entity getEntityFromID(World container, int id) throws FieldAccessException
/*     */   {
/* 448 */     if (this.delegate != null) {
/* 449 */       return this.delegate.getEntityFromID(container, id);
/*     */     }
/* 451 */     return EntityUtilities.getEntityFromID(container, id);
/*     */   }
/*     */ 
/*     */   public List<Player> getEntityTrackers(Entity entity) throws FieldAccessException
/*     */   {
/* 456 */     if (this.delegate != null) {
/* 457 */       return this.delegate.getEntityTrackers(entity);
/*     */     }
/* 459 */     return EntityUtilities.getEntityTrackers(entity);
/*     */   }
/*     */ 
/*     */   public boolean isClosed()
/*     */   {
/* 464 */     return (this.closed) || ((this.delegate != null) && (this.delegate.isClosed()));
/*     */   }
/*     */ 
/*     */   public AsynchronousManager getAsynchronousManager()
/*     */   {
/* 469 */     if (this.delegate != null) {
/* 470 */       return this.delegate.getAsynchronousManager();
/*     */     }
/* 472 */     return this.asyncManager;
/*     */   }
/*     */ 
/*     */   public boolean isDebug()
/*     */   {
/* 477 */     return this.debug;
/*     */   }
/*     */ 
/*     */   public void setDebug(boolean debug)
/*     */   {
/* 482 */     this.debug = debug;
/*     */ 
/* 484 */     if (this.delegate != null)
/* 485 */       this.delegate.setDebug(debug);
/*     */   }
/*     */ 
/*     */   public void setAsynchronousManager(AsynchronousManager asyncManager)
/*     */   {
/* 494 */     this.asyncManager = asyncManager;
/*     */   }
/*     */ 
/*     */   public void registerEvents(PluginManager manager, Plugin plugin)
/*     */   {
/* 499 */     if (this.delegate != null) {
/* 500 */       this.delegate.registerEvents(manager, plugin);
/*     */     } else {
/* 502 */       this.queuedManager = manager;
/* 503 */       this.queuedPlugin = plugin;
/*     */     }
/*     */   }
/*     */ 
/*     */   public void close()
/*     */   {
/* 509 */     if (this.delegate != null)
/* 510 */       this.delegate.close();
/* 511 */     this.closed = true;
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.injector.DelayedPacketManager
 * JD-Core Version:    0.6.2
 */