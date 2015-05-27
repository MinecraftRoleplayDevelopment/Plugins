/*     */ package com.comphenix.protocol.injector.netty;
/*     */ 
/*     */ import com.comphenix.protocol.PacketType;
/*     */ import com.comphenix.protocol.concurrency.PacketTypeSet;
/*     */ import com.comphenix.protocol.error.ErrorReporter;
/*     */ import com.comphenix.protocol.error.Report;
/*     */ import com.comphenix.protocol.error.Report.ReportBuilder;
/*     */ import com.comphenix.protocol.error.ReportType;
/*     */ import com.comphenix.protocol.events.ConnectionSide;
/*     */ import com.comphenix.protocol.events.ListenerOptions;
/*     */ import com.comphenix.protocol.events.NetworkMarker;
/*     */ import com.comphenix.protocol.events.PacketContainer;
/*     */ import com.comphenix.protocol.events.PacketEvent;
/*     */ import com.comphenix.protocol.injector.ListenerInvoker;
/*     */ import com.comphenix.protocol.injector.packet.PacketInjector;
/*     */ import com.comphenix.protocol.injector.packet.PacketRegistry;
/*     */ import com.comphenix.protocol.injector.player.PlayerInjectionHandler;
/*     */ import com.comphenix.protocol.injector.player.PlayerInjectionHandler.ConflictStrategy;
/*     */ import com.comphenix.protocol.injector.server.TemporaryPlayerFactory;
/*     */ import com.comphenix.protocol.injector.spigot.AbstractPacketInjector;
/*     */ import com.comphenix.protocol.injector.spigot.AbstractPlayerHandler;
/*     */ import com.comphenix.protocol.reflect.FuzzyReflection;
/*     */ import com.comphenix.protocol.reflect.VolatileField;
/*     */ import com.comphenix.protocol.utility.MinecraftReflection;
/*     */ import com.google.common.collect.Lists;
/*     */ import io.netty.channel.Channel;
/*     */ import io.netty.channel.ChannelFuture;
/*     */ import io.netty.channel.ChannelHandler;
/*     */ import io.netty.channel.ChannelHandlerContext;
/*     */ import io.netty.channel.ChannelInboundHandler;
/*     */ import io.netty.channel.ChannelInboundHandlerAdapter;
/*     */ import io.netty.channel.ChannelInitializer;
/*     */ import io.netty.channel.ChannelPipeline;
/*     */ import java.io.InputStream;
/*     */ import java.lang.reflect.Field;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.lang.reflect.Method;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.util.List;
/*     */ import java.util.Set;
/*     */ import org.bukkit.entity.Player;
/*     */ import org.bukkit.plugin.Plugin;
/*     */ 
/*     */ public class NettyProtocolInjector
/*     */   implements ChannelListener
/*     */ {
/*  46 */   public static final ReportType REPORT_CANNOT_INJECT_INCOMING_CHANNEL = new ReportType("Unable to inject incoming channel %s.");
/*     */   private volatile boolean injected;
/*     */   private volatile boolean closed;
/*  52 */   private TemporaryPlayerFactory playerFactory = new TemporaryPlayerFactory();
/*  53 */   private List<VolatileField> bootstrapFields = Lists.newArrayList();
/*     */   private InjectionFactory injectionFactory;
/*     */   private volatile List<Object> networkManagers;
/*  62 */   private PacketTypeSet sendingFilters = new PacketTypeSet();
/*  63 */   private PacketTypeSet reveivedFilters = new PacketTypeSet();
/*     */ 
/*  66 */   private PacketTypeSet mainThreadFilters = new PacketTypeSet();
/*     */ 
/*  69 */   private PacketTypeSet bufferedPackets = new PacketTypeSet();
/*     */   private ListenerInvoker invoker;
/*     */   private ErrorReporter reporter;
/*     */   private boolean debug;
/*     */ 
/*     */   public NettyProtocolInjector(Plugin plugin, ListenerInvoker invoker, ErrorReporter reporter)
/*     */   {
/*  77 */     this.injectionFactory = new InjectionFactory(plugin);
/*  78 */     this.invoker = invoker;
/*  79 */     this.reporter = reporter;
/*     */   }
/*     */ 
/*     */   public boolean isDebug()
/*     */   {
/*  84 */     return this.debug;
/*     */   }
/*     */ 
/*     */   public void setDebug(boolean debug)
/*     */   {
/*  92 */     this.debug = debug;
/*     */   }
/*     */ 
/*     */   public synchronized void inject()
/*     */   {
/* 100 */     if (this.injected)
/* 101 */       throw new IllegalStateException("Cannot inject twice.");
/*     */     try {
/* 103 */       FuzzyReflection fuzzyServer = FuzzyReflection.fromClass(MinecraftReflection.getMinecraftServerClass());
/* 104 */       List serverConnectionMethods = fuzzyServer.getMethodListByParameters(MinecraftReflection.getServerConnectionClass(), new Class[0]);
/*     */ 
/* 107 */       Object server = fuzzyServer.getSingleton();
/* 108 */       Object serverConnection = null;
/*     */ 
/* 110 */       for (Method method : serverConnectionMethods) {
/*     */         try {
/* 112 */           serverConnection = method.invoke(server, new Object[0]);
/*     */ 
/* 115 */           if (serverConnection != null)
/* 116 */             break;
/*     */         }
/*     */         catch (Exception e)
/*     */         {
/* 120 */           e.printStackTrace();
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 125 */       final ChannelInboundHandler endInitProtocol = new ChannelInitializer()
/*     */       {
/*     */         protected void initChannel(Channel channel) throws Exception
/*     */         {
/*     */           try {
/* 130 */             synchronized (NettyProtocolInjector.this.networkManagers) {
/* 131 */               NettyProtocolInjector.this.injectionFactory.fromChannel(channel, NettyProtocolInjector.this, NettyProtocolInjector.this.playerFactory).inject();
/*     */             }
/*     */           } catch (Exception e) {
/* 134 */             NettyProtocolInjector.this.reporter.reportDetailed(NettyProtocolInjector.this, Report.newBuilder(NettyProtocolInjector.REPORT_CANNOT_INJECT_INCOMING_CHANNEL).messageParam(new Object[] { channel }).error(e));
/*     */           }
/*     */         }
/*     */       };
/* 141 */       final ChannelInboundHandler beginInitProtocol = new ChannelInitializer()
/*     */       {
/*     */         protected void initChannel(Channel channel) throws Exception
/*     */         {
/* 145 */           channel.pipeline().addLast(new ChannelHandler[] { endInitProtocol });
/*     */         }
/*     */       };
/* 150 */       ChannelHandler connectionHandler = new ChannelInboundHandlerAdapter()
/*     */       {
/*     */         public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
/* 153 */           Channel channel = (Channel)msg;
/*     */ 
/* 156 */           channel.pipeline().addFirst(new ChannelHandler[] { beginInitProtocol });
/* 157 */           ctx.fireChannelRead(msg);
/*     */         }
/*     */       };
/* 162 */       this.networkManagers = ((List)FuzzyReflection.fromObject(serverConnection, true).invokeMethod(null, "getNetworkManagers", List.class, new Object[] { serverConnection }));
/*     */ 
/* 166 */       this.bootstrapFields = getBootstrapFields(serverConnection);
/*     */ 
/* 168 */       for (VolatileField field : this.bootstrapFields) {
/* 169 */         List list = (List)field.getValue();
/*     */ 
/* 172 */         if (list != this.networkManagers)
/*     */         {
/* 177 */           field.setValue(new BootstrapList(list, connectionHandler));
/*     */         }
/*     */       }
/* 180 */       this.injected = true;
/*     */     }
/*     */     catch (Exception e) {
/* 183 */       throw new RuntimeException("Unable to inject channel futures.", e);
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean hasListener(Class<?> packetClass)
/*     */   {
/* 189 */     return (this.reveivedFilters.contains(packetClass)) || (this.sendingFilters.contains(packetClass));
/*     */   }
/*     */ 
/*     */   public boolean hasMainThreadListener(Class<?> packetClass)
/*     */   {
/* 194 */     return this.mainThreadFilters.contains(packetClass);
/*     */   }
/*     */ 
/*     */   public ErrorReporter getReporter()
/*     */   {
/* 199 */     return this.reporter;
/*     */   }
/*     */ 
/*     */   public void injectPlayer(Player player)
/*     */   {
/* 207 */     this.injectionFactory.fromPlayer(player, this).inject();
/*     */   }
/*     */ 
/*     */   private List<VolatileField> getBootstrapFields(Object serverConnection) {
/* 211 */     List result = Lists.newArrayList();
/*     */ 
/* 214 */     for (Field field : FuzzyReflection.fromObject(serverConnection, true).getFieldListByType(List.class)) {
/* 215 */       VolatileField volatileField = new VolatileField(field, serverConnection, true).toSynchronized();
/*     */ 
/* 218 */       List list = (List)volatileField.getValue();
/*     */ 
/* 220 */       if ((list.size() == 0) || ((list.get(0) instanceof ChannelFuture))) {
/* 221 */         result.add(volatileField);
/*     */       }
/*     */     }
/* 224 */     return result;
/*     */   }
/*     */ 
/*     */   public synchronized void close()
/*     */   {
/* 231 */     if (!this.closed) {
/* 232 */       this.closed = true;
/*     */ 
/* 234 */       for (VolatileField field : this.bootstrapFields) {
/* 235 */         Object value = field.getValue();
/*     */ 
/* 238 */         if ((value instanceof BootstrapList)) {
/* 239 */           ((BootstrapList)value).close();
/*     */         }
/* 241 */         field.revertValue();
/*     */       }
/*     */ 
/* 244 */       this.injectionFactory.close();
/*     */     }
/*     */   }
/*     */ 
/*     */   public PacketEvent onPacketSending(Injector injector, Object packet, NetworkMarker marker)
/*     */   {
/* 250 */     Class clazz = packet.getClass();
/*     */ 
/* 252 */     if ((this.sendingFilters.contains(clazz)) || (marker != null)) {
/* 253 */       PacketContainer container = new PacketContainer(PacketRegistry.getPacketType(clazz), packet);
/* 254 */       return packetQueued(container, injector.getPlayer(), marker);
/*     */     }
/*     */ 
/* 258 */     return null;
/*     */   }
/*     */ 
/*     */   public PacketEvent onPacketReceiving(Injector injector, Object packet, NetworkMarker marker)
/*     */   {
/* 263 */     Class clazz = packet.getClass();
/*     */ 
/* 265 */     if ((this.reveivedFilters.contains(clazz)) || (marker != null)) {
/* 266 */       PacketContainer container = new PacketContainer(PacketRegistry.getPacketType(clazz), packet);
/* 267 */       return packetReceived(container, injector.getPlayer(), marker);
/*     */     }
/*     */ 
/* 271 */     return null;
/*     */   }
/*     */ 
/*     */   public boolean includeBuffer(Class<?> packetClass)
/*     */   {
/* 276 */     return this.bufferedPackets.contains(packetClass);
/*     */   }
/*     */ 
/*     */   private PacketEvent packetQueued(PacketContainer packet, Player receiver, NetworkMarker marker)
/*     */   {
/* 286 */     PacketEvent event = PacketEvent.fromServer(this, packet, marker, receiver);
/*     */ 
/* 288 */     this.invoker.invokePacketSending(event);
/* 289 */     return event;
/*     */   }
/*     */ 
/*     */   private PacketEvent packetReceived(PacketContainer packet, Player sender, NetworkMarker marker)
/*     */   {
/* 300 */     PacketEvent event = PacketEvent.fromClient(this, packet, marker, sender);
/*     */ 
/* 302 */     this.invoker.invokePacketRecieving(event);
/* 303 */     return event;
/*     */   }
/*     */ 
/*     */   public PlayerInjectionHandler getPlayerInjector()
/*     */   {
/* 308 */     return new AbstractPlayerHandler(this.sendingFilters) {
/* 309 */       private ChannelListener listener = NettyProtocolInjector.this;
/*     */ 
/*     */       public int getProtocolVersion(Player player)
/*     */       {
/* 313 */         return NettyProtocolInjector.this.injectionFactory.fromPlayer(player, this.listener).getProtocolVersion();
/*     */       }
/*     */ 
/*     */       public void updatePlayer(Player player)
/*     */       {
/* 318 */         NettyProtocolInjector.this.injectionFactory.fromPlayer(player, this.listener).inject();
/*     */       }
/*     */ 
/*     */       public void injectPlayer(Player player, PlayerInjectionHandler.ConflictStrategy strategy)
/*     */       {
/* 323 */         NettyProtocolInjector.this.injectionFactory.fromPlayer(player, this.listener).inject();
/*     */       }
/*     */ 
/*     */       public boolean uninjectPlayer(InetSocketAddress address)
/*     */       {
/* 329 */         return true;
/*     */       }
/*     */ 
/*     */       public void addPacketHandler(PacketType type, Set<ListenerOptions> options)
/*     */       {
/* 334 */         if ((options != null) && (!options.contains(ListenerOptions.ASYNC)))
/* 335 */           NettyProtocolInjector.this.mainThreadFilters.addType(type);
/* 336 */         super.addPacketHandler(type, options);
/*     */       }
/*     */ 
/*     */       public void removePacketHandler(PacketType type)
/*     */       {
/* 341 */         NettyProtocolInjector.this.mainThreadFilters.removeType(type);
/* 342 */         super.removePacketHandler(type);
/*     */       }
/*     */ 
/*     */       public boolean uninjectPlayer(Player player)
/*     */       {
/* 348 */         return true;
/*     */       }
/*     */ 
/*     */       public void sendServerPacket(Player receiver, PacketContainer packet, NetworkMarker marker, boolean filters) throws InvocationTargetException
/*     */       {
/* 353 */         NettyProtocolInjector.this.injectionFactory.fromPlayer(receiver, this.listener).sendServerPacket(packet.getHandle(), marker, filters);
/*     */       }
/*     */ 
/*     */       public boolean hasMainThreadListener(PacketType type)
/*     */       {
/* 359 */         return NettyProtocolInjector.this.mainThreadFilters.contains(type);
/*     */       }
/*     */ 
/*     */       public void recieveClientPacket(Player player, Object mcPacket) throws IllegalAccessException, InvocationTargetException
/*     */       {
/* 364 */         NettyProtocolInjector.this.injectionFactory.fromPlayer(player, this.listener).recieveClientPacket(mcPacket);
/*     */       }
/*     */ 
/*     */       public PacketEvent handlePacketRecieved(PacketContainer packet, InputStream input, byte[] buffered)
/*     */       {
/* 371 */         return null;
/*     */       }
/*     */ 
/*     */       public void handleDisconnect(Player player)
/*     */       {
/* 376 */         NettyProtocolInjector.this.injectionFactory.fromPlayer(player, this.listener).close();
/*     */       }
/*     */ 
/*     */       public Channel getChannel(Player player)
/*     */       {
/* 381 */         Injector injector = NettyProtocolInjector.this.injectionFactory.fromPlayer(player, this.listener);
/* 382 */         if ((injector instanceof ChannelInjector)) {
/* 383 */           return ((ChannelInjector)injector).getChannel();
/*     */         }
/*     */ 
/* 386 */         return null;
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   public PacketInjector getPacketInjector()
/*     */   {
/* 397 */     return new AbstractPacketInjector(this.reveivedFilters)
/*     */     {
/*     */       public PacketEvent packetRecieved(PacketContainer packet, Player client, byte[] buffered) {
/* 400 */         NetworkMarker marker = buffered != null ? new NettyNetworkMarker(ConnectionSide.CLIENT_SIDE, buffered) : null;
/* 401 */         NettyProtocolInjector.this.injectionFactory.fromPlayer(client, NettyProtocolInjector.this).saveMarker(packet.getHandle(), marker);
/*     */ 
/* 403 */         return NettyProtocolInjector.this.packetReceived(packet, client, marker);
/*     */       }
/*     */ 
/*     */       public void inputBuffersChanged(Set<PacketType> set)
/*     */       {
/* 408 */         NettyProtocolInjector.this.bufferedPackets = new PacketTypeSet(set);
/*     */       }
/*     */     };
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.injector.netty.NettyProtocolInjector
 * JD-Core Version:    0.6.2
 */