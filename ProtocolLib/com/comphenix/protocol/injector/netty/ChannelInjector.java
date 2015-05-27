/*     */ package com.comphenix.protocol.injector.netty;
/*     */ 
/*     */ import com.comphenix.net.sf.cglib.proxy.Factory;
/*     */ import com.comphenix.protocol.PacketType;
/*     */ import com.comphenix.protocol.PacketType.Login.Client;
/*     */ import com.comphenix.protocol.PacketType.Protocol;
/*     */ import com.comphenix.protocol.ProtocolLibrary;
/*     */ import com.comphenix.protocol.error.ErrorReporter;
/*     */ import com.comphenix.protocol.error.Report;
/*     */ import com.comphenix.protocol.error.Report.ReportBuilder;
/*     */ import com.comphenix.protocol.error.ReportType;
/*     */ import com.comphenix.protocol.events.ConnectionSide;
/*     */ import com.comphenix.protocol.events.NetworkMarker;
/*     */ import com.comphenix.protocol.events.PacketContainer;
/*     */ import com.comphenix.protocol.events.PacketEvent;
/*     */ import com.comphenix.protocol.injector.NetworkProcessor;
/*     */ import com.comphenix.protocol.injector.server.SocketInjector;
/*     */ import com.comphenix.protocol.reflect.FuzzyReflection;
/*     */ import com.comphenix.protocol.reflect.VolatileField;
/*     */ import com.comphenix.protocol.reflect.accessors.Accessors;
/*     */ import com.comphenix.protocol.reflect.accessors.FieldAccessor;
/*     */ import com.comphenix.protocol.reflect.accessors.MethodAccessor;
/*     */ import com.comphenix.protocol.utility.MinecraftFields;
/*     */ import com.comphenix.protocol.utility.MinecraftMethods;
/*     */ import com.comphenix.protocol.utility.MinecraftProtocolVersion;
/*     */ import com.comphenix.protocol.utility.MinecraftReflection;
/*     */ import com.comphenix.protocol.wrappers.WrappedGameProfile;
/*     */ import com.google.common.base.Preconditions;
/*     */ import com.google.common.collect.MapMaker;
/*     */ import io.netty.buffer.ByteBuf;
/*     */ import io.netty.buffer.ByteBufAllocator;
/*     */ import io.netty.channel.Channel;
/*     */ import io.netty.channel.ChannelHandler;
/*     */ import io.netty.channel.ChannelHandlerAdapter;
/*     */ import io.netty.channel.ChannelHandlerContext;
/*     */ import io.netty.channel.ChannelInboundHandlerAdapter;
/*     */ import io.netty.channel.ChannelPipeline;
/*     */ import io.netty.channel.ChannelPromise;
/*     */ import io.netty.channel.EventLoop;
/*     */ import io.netty.channel.socket.SocketChannel;
/*     */ import io.netty.handler.codec.ByteToMessageDecoder;
/*     */ import io.netty.handler.codec.MessageToByteEncoder;
/*     */ import io.netty.util.concurrent.GenericFutureListener;
/*     */ import io.netty.util.internal.TypeParameterMatcher;
/*     */ import java.io.PrintStream;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.lang.reflect.Method;
/*     */ import java.net.Socket;
/*     */ import java.net.SocketAddress;
/*     */ import java.nio.channels.ClosedChannelException;
/*     */ import java.util.ArrayDeque;
/*     */ import java.util.Deque;
/*     */ import java.util.List;
/*     */ import java.util.ListIterator;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.NoSuchElementException;
/*     */ import java.util.concurrent.Callable;
/*     */ import java.util.concurrent.ConcurrentMap;
/*     */ import org.bukkit.Bukkit;
/*     */ import org.bukkit.entity.Player;
/*     */ import org.bukkit.scheduler.BukkitScheduler;
/*     */ 
/*     */ class ChannelInjector extends ByteToMessageDecoder
/*     */   implements Injector
/*     */ {
/*  63 */   public static final ReportType REPORT_CANNOT_INTERCEPT_SERVER_PACKET = new ReportType("Unable to intercept a written server packet.");
/*  64 */   public static final ReportType REPORT_CANNOT_INTERCEPT_CLIENT_PACKET = new ReportType("Unable to intercept a read client packet.");
/*  65 */   public static final ReportType REPORT_CANNOT_EXECUTE_IN_CHANNEL_THREAD = new ReportType("Cannot execute code in channel thread.");
/*  66 */   public static final ReportType REPORT_CANNOT_FIND_GET_VERSION = new ReportType("Cannot find getVersion() in NetworkMananger");
/*     */ 
/*  71 */   private static final PacketEvent BYPASSED_PACKET = new PacketEvent(ChannelInjector.class);
/*     */ 
/*  74 */   private static Class<?> PACKET_LOGIN_CLIENT = null;
/*  75 */   private static FieldAccessor LOGIN_GAME_PROFILE = null;
/*     */   private static MethodAccessor DECODE_BUFFER;
/*     */   private static MethodAccessor ENCODE_BUFFER;
/*     */   private static FieldAccessor ENCODER_TYPE_MATCHER;
/*     */   private static FieldAccessor PROTOCOL_ACCESSOR;
/*     */   private static MethodAccessor PROTOCOL_VERSION;
/*     */   private InjectionFactory factory;
/*     */   private Player player;
/*     */   private Player updated;
/*     */   private String playerName;
/*     */   private Object playerConnection;
/*     */   private final Object networkManager;
/*     */   private final Channel originalChannel;
/*     */   private VolatileField channelField;
/* 105 */   private ConcurrentMap<Object, NetworkMarker> packetMarker = new MapMaker().weakKeys().makeMap();
/*     */   private PacketEvent currentEvent;
/*     */   private PacketEvent finalEvent;
/* 122 */   private final ThreadLocal<Boolean> scheduleProcessPackets = new ThreadLocal()
/*     */   {
/*     */     protected Boolean initialValue() {
/* 125 */       return Boolean.valueOf(true);
/*     */     }
/* 122 */   };
/*     */   private ByteToMessageDecoder vanillaDecoder;
/*     */   private MessageToByteEncoder<Object> vanillaEncoder;
/* 133 */   private Deque<PacketEvent> finishQueue = new ArrayDeque();
/*     */   private ChannelListener channelListener;
/*     */   private NetworkProcessor processor;
/*     */   private boolean injected;
/*     */   private boolean closed;
/*     */ 
/*     */   public ChannelInjector(Player player, Object networkManager, Channel channel, ChannelListener channelListener, InjectionFactory factory)
/*     */   {
/* 154 */     this.player = ((Player)Preconditions.checkNotNull(player, "player cannot be NULL"));
/* 155 */     this.networkManager = Preconditions.checkNotNull(networkManager, "networkMananger cannot be NULL");
/* 156 */     this.originalChannel = ((Channel)Preconditions.checkNotNull(channel, "channel cannot be NULL"));
/* 157 */     this.channelListener = ((ChannelListener)Preconditions.checkNotNull(channelListener, "channelListener cannot be NULL"));
/* 158 */     this.factory = ((InjectionFactory)Preconditions.checkNotNull(factory, "factory cannot be NULL"));
/* 159 */     this.processor = new NetworkProcessor(ProtocolLibrary.getErrorReporter());
/*     */ 
/* 162 */     this.channelField = new VolatileField(FuzzyReflection.fromObject(networkManager, true).getFieldByType("channel", Channel.class), networkManager, true);
/*     */   }
/*     */ 
/*     */   public int getProtocolVersion()
/*     */   {
/* 172 */     MethodAccessor accessor = PROTOCOL_VERSION;
/* 173 */     if (accessor == null)
/*     */       try {
/* 175 */         accessor = Accessors.getMethodAccessor(this.networkManager.getClass(), "getVersion", new Class[0]);
/*     */       }
/*     */       catch (Throwable ex)
/*     */       {
/*     */       }
/* 180 */     if (accessor != null) {
/* 181 */       return ((Integer)accessor.invoke(this.networkManager, new Object[0])).intValue();
/*     */     }
/* 183 */     return MinecraftProtocolVersion.getCurrentVersion();
/*     */   }
/*     */ 
/*     */   public boolean inject()
/*     */   {
/* 190 */     synchronized (this.networkManager) {
/* 191 */       if (this.closed)
/* 192 */         return false;
/* 193 */       if ((this.originalChannel instanceof Factory))
/* 194 */         return false;
/* 195 */       if (!this.originalChannel.isActive()) {
/* 196 */         return false;
/*     */       }
/*     */ 
/* 200 */       if (Bukkit.isPrimaryThread())
/*     */       {
/* 202 */         executeInChannelThread(new Runnable()
/*     */         {
/*     */           public void run() {
/* 205 */             ChannelInjector.this.inject();
/*     */           }
/*     */         });
/* 208 */         return false;
/*     */       }
/*     */ 
/* 212 */       if (findChannelHandler(this.originalChannel, ChannelInjector.class) != null) {
/* 213 */         return false;
/*     */       }
/*     */ 
/* 217 */       this.vanillaDecoder = ((ByteToMessageDecoder)this.originalChannel.pipeline().get("decoder"));
/* 218 */       this.vanillaEncoder = ((MessageToByteEncoder)this.originalChannel.pipeline().get("encoder"));
/*     */ 
/* 220 */       if (this.vanillaDecoder == null)
/* 221 */         throw new IllegalArgumentException("Unable to find vanilla decoder in " + this.originalChannel.pipeline());
/* 222 */       if (this.vanillaEncoder == null)
/* 223 */         throw new IllegalArgumentException("Unable to find vanilla encoder in " + this.originalChannel.pipeline());
/* 224 */       patchEncoder(this.vanillaEncoder);
/*     */ 
/* 226 */       if (DECODE_BUFFER == null) {
/* 227 */         DECODE_BUFFER = Accessors.getMethodAccessor(this.vanillaDecoder.getClass(), "decode", new Class[] { ChannelHandlerContext.class, ByteBuf.class, List.class });
/*     */       }
/* 229 */       if (ENCODE_BUFFER == null) {
/* 230 */         ENCODE_BUFFER = Accessors.getMethodAccessor(this.vanillaEncoder.getClass(), "encode", new Class[] { ChannelHandlerContext.class, Object.class, ByteBuf.class });
/*     */       }
/*     */ 
/* 234 */       MessageToByteEncoder protocolEncoder = new MessageToByteEncoder()
/*     */       {
/*     */         protected void encode(ChannelHandlerContext ctx, Object packet, ByteBuf output) throws Exception {
/* 237 */           if ((packet instanceof WirePacket))
/*     */           {
/* 239 */             ChannelInjector.this.encodeWirePacket((WirePacket)packet, output);
/*     */           }
/* 241 */           else ChannelInjector.this.encode(ctx, packet, output);
/*     */         }
/*     */ 
/*     */         public void write(ChannelHandlerContext ctx, Object packet, ChannelPromise promise)
/*     */           throws Exception
/*     */         {
/* 247 */           super.write(ctx, packet, promise);
/* 248 */           ChannelInjector.this.finalWrite(ctx, packet, promise);
/*     */         }
/*     */       };
/* 253 */       ChannelInboundHandlerAdapter finishHandler = new ChannelInboundHandlerAdapter()
/*     */       {
/*     */         public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception
/*     */         {
/* 257 */           ctx.fireChannelRead(msg);
/* 258 */           ChannelInjector.this.finishRead(ctx, msg);
/*     */         }
/*     */       };
/* 262 */       ChannelHandlerAdapter exceptionHandler = new ChannelHandlerAdapter()
/*     */       {
/*     */         public void exceptionCaught(ChannelHandlerContext context, Throwable ex) throws Exception {
/* 265 */           if (!(ex instanceof ClosedChannelException))
/*     */           {
/* 269 */             System.err.println("[ProtocolLib] Encountered an uncaught exception in the channel pipeline:");
/* 270 */             ex.printStackTrace();
/*     */           }
/*     */         }
/*     */       };
/* 276 */       this.originalChannel.pipeline().addBefore("decoder", "protocol_lib_decoder", this);
/* 277 */       this.originalChannel.pipeline().addBefore("protocol_lib_decoder", "protocol_lib_finish", finishHandler);
/* 278 */       this.originalChannel.pipeline().addAfter("encoder", "protocol_lib_encoder", protocolEncoder);
/* 279 */       this.originalChannel.pipeline().addLast("protocol_lib_exception_handler", exceptionHandler);
/*     */ 
/* 282 */       this.channelField.setValue(new ChannelProxy(this.originalChannel, MinecraftReflection.getPacketClass())
/*     */       {
/* 284 */         private final PipelineProxy pipelineProxy = new PipelineProxy(ChannelInjector.this.originalChannel.pipeline(), this)
/*     */         {
/*     */           public ChannelPipeline addBefore(String baseName, String name, ChannelHandler handler)
/*     */           {
/* 288 */             if (("decoder".equals(baseName)) && 
/* 289 */               (super.get("protocol_lib_decoder") != null) && (ChannelInjector.this.guessCompression(handler))) {
/* 290 */               super.addBefore("protocol_lib_decoder", name, handler);
/* 291 */               return this;
/*     */             }
/*     */ 
/* 295 */             return super.addBefore(baseName, name, handler);
/*     */           }
/* 284 */         };
/*     */ 
/*     */         public ChannelPipeline pipeline()
/*     */         {
/* 301 */           return this.pipelineProxy;
/*     */         }
/*     */ 
/*     */         protected <T> Callable<T> onMessageScheduled(final Callable<T> callable, FieldAccessor packetAccessor)
/*     */         {
/* 306 */           final PacketEvent event = handleScheduled(callable, packetAccessor);
/*     */ 
/* 309 */           if ((event != null) && (event.isCancelled())) {
/* 310 */             return null;
/*     */           }
/* 312 */           return new Callable()
/*     */           {
/*     */             public T call() throws Exception {
/* 315 */               Object result = null;
/*     */ 
/* 318 */               ChannelInjector.this.currentEvent = event;
/* 319 */               result = callable.call();
/* 320 */               ChannelInjector.this.currentEvent = null;
/* 321 */               return result;
/*     */             }
/*     */           };
/*     */         }
/*     */ 
/*     */         protected Runnable onMessageScheduled(final Runnable runnable, FieldAccessor packetAccessor)
/*     */         {
/* 328 */           final PacketEvent event = handleScheduled(runnable, packetAccessor);
/*     */ 
/* 331 */           if ((event != null) && (event.isCancelled())) {
/* 332 */             return null;
/*     */           }
/* 334 */           return new Runnable()
/*     */           {
/*     */             public void run() {
/* 337 */               ChannelInjector.this.currentEvent = event;
/* 338 */               runnable.run();
/* 339 */               ChannelInjector.this.currentEvent = null;
/*     */             }
/*     */           };
/*     */         }
/*     */ 
/*     */         protected PacketEvent handleScheduled(Object instance, FieldAccessor accessor)
/*     */         {
/* 346 */           Object original = accessor.get(instance);
/*     */ 
/* 349 */           if (!((Boolean)ChannelInjector.this.scheduleProcessPackets.get()).booleanValue()) {
/* 350 */             NetworkMarker marker = ChannelInjector.this.getMarker(original);
/*     */ 
/* 352 */             if (marker != null) {
/* 353 */               PacketEvent result = new PacketEvent(ChannelInjector.class);
/* 354 */               result.setNetworkMarker(marker);
/* 355 */               return result;
/*     */             }
/* 357 */             return ChannelInjector.BYPASSED_PACKET;
/*     */           }
/*     */ 
/* 360 */           PacketEvent event = ChannelInjector.this.processSending(original);
/*     */ 
/* 362 */           if ((event != null) && (!event.isCancelled())) {
/* 363 */             Object changed = event.getPacket().getHandle();
/*     */ 
/* 366 */             if (original != changed)
/* 367 */               accessor.set(instance, changed);
/*     */           }
/* 369 */           return event != null ? event : ChannelInjector.BYPASSED_PACKET;
/*     */         }
/*     */       });
/* 373 */       this.injected = true;
/* 374 */       return true;
/*     */     }
/*     */   }
/*     */ 
/*     */   private boolean guessCompression(ChannelHandler handler)
/*     */   {
/* 384 */     String className = handler != null ? handler.getClass().getCanonicalName() : null;
/* 385 */     return (className.contains("Compressor")) || (className.contains("Decompressor"));
/*     */   }
/*     */ 
/*     */   private PacketEvent processSending(Object message)
/*     */   {
/* 394 */     return this.channelListener.onPacketSending(this, message, getMarker(message));
/*     */   }
/*     */ 
/*     */   private void patchEncoder(MessageToByteEncoder<Object> encoder)
/*     */   {
/* 402 */     if (ENCODER_TYPE_MATCHER == null) {
/* 403 */       ENCODER_TYPE_MATCHER = Accessors.getFieldAccessor(encoder.getClass(), "matcher", true);
/*     */     }
/* 405 */     ENCODER_TYPE_MATCHER.set(encoder, TypeParameterMatcher.get(MinecraftReflection.getPacketClass()));
/*     */   }
/*     */ 
/*     */   public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception
/*     */   {
/* 410 */     if (this.channelListener.isDebug())
/* 411 */       cause.printStackTrace();
/* 412 */     super.exceptionCaught(ctx, cause);
/*     */   }
/*     */ 
/*     */   protected void encodeWirePacket(WirePacket packet, ByteBuf output) throws Exception {
/* 416 */     packet.writeId(output);
/* 417 */     packet.writeBytes(output);
/*     */   }
/*     */ 
/*     */   protected void encode(ChannelHandlerContext ctx, Object packet, ByteBuf output)
/*     */     throws Exception
/*     */   {
/* 428 */     NetworkMarker marker = null;
/* 429 */     PacketEvent event = this.currentEvent;
/*     */     try
/*     */     {
/* 433 */       if (!((Boolean)this.scheduleProcessPackets.get()).booleanValue())
/*     */       {
/*     */         return;
/*     */       }
/*     */ 
/* 438 */       if (event == null) {
/* 439 */         Class clazz = packet.getClass();
/*     */ 
/* 442 */         if (this.channelListener.hasMainThreadListener(clazz))
/*     */         {
/* 444 */           scheduleMainThread(packet);
/* 445 */           packet = null;
/*     */         }
/*     */         else {
/* 448 */           event = processSending(packet);
/*     */ 
/* 451 */           if (event != null) {
/* 452 */             packet = !event.isCancelled() ? event.getPacket().getHandle() : null;
/*     */           }
/*     */         }
/*     */       }
/* 456 */       if (event != null)
/*     */       {
/* 458 */         marker = NetworkMarker.getNetworkMarker(event);
/*     */       }
/*     */ 
/* 462 */       if ((packet != null) && (event != null) && (NetworkMarker.hasOutputHandlers(marker))) {
/* 463 */         ByteBuf packetBuffer = ctx.alloc().buffer();
/* 464 */         ENCODE_BUFFER.invoke(this.vanillaEncoder, new Object[] { ctx, packet, packetBuffer });
/*     */ 
/* 467 */         byte[] data = this.processor.processOutput(event, marker, getBytes(packetBuffer));
/*     */ 
/* 470 */         output.writeBytes(data);
/* 471 */         packet = null;
/*     */ 
/* 474 */         this.finalEvent = event;
/*     */       }
/*     */     }
/*     */     catch (Exception e) {
/* 478 */       this.channelListener.getReporter().reportDetailed(this, Report.newBuilder(REPORT_CANNOT_INTERCEPT_SERVER_PACKET).callerParam(new Object[] { packet }).error(e).build());
/*     */     }
/*     */     finally
/*     */     {
/* 482 */       if (packet != null) {
/* 483 */         ENCODE_BUFFER.invoke(this.vanillaEncoder, new Object[] { ctx, packet, output });
/* 484 */         this.finalEvent = event;
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void finalWrite(ChannelHandlerContext ctx, Object packet, ChannelPromise promise)
/*     */   {
/* 496 */     PacketEvent event = this.finalEvent;
/*     */ 
/* 498 */     if (event != null)
/*     */     {
/* 500 */       this.finalEvent = null;
/* 501 */       this.currentEvent = null;
/*     */ 
/* 503 */       this.processor.invokePostEvent(event, NetworkMarker.getNetworkMarker(event));
/*     */     }
/*     */   }
/*     */ 
/*     */   private void scheduleMainThread(final Object packetCopy)
/*     */   {
/* 509 */     Bukkit.getScheduler().scheduleSyncDelayedTask(this.factory.getPlugin(), new Runnable()
/*     */     {
/*     */       public void run() {
/* 512 */         ChannelInjector.this.invokeSendPacket(packetCopy);
/*     */       }
/*     */     });
/*     */   }
/*     */ 
/*     */   protected void decode(ChannelHandlerContext ctx, ByteBuf byteBuffer, List<Object> packets) throws Exception
/*     */   {
/* 519 */     byteBuffer.markReaderIndex();
/* 520 */     DECODE_BUFFER.invoke(this.vanillaDecoder, new Object[] { ctx, byteBuffer, packets });
/*     */     try
/*     */     {
/* 524 */       this.finishQueue.clear();
/*     */ 
/* 526 */       for (it = packets.listIterator(); it.hasNext(); ) {
/* 527 */         Object input = it.next();
/* 528 */         Class packetClass = input.getClass();
/* 529 */         NetworkMarker marker = null;
/*     */ 
/* 532 */         handleLogin(packetClass, input);
/*     */ 
/* 534 */         if (this.channelListener.includeBuffer(packetClass)) {
/* 535 */           byteBuffer.resetReaderIndex();
/* 536 */           marker = new NettyNetworkMarker(ConnectionSide.CLIENT_SIDE, getBytes(byteBuffer));
/*     */         }
/*     */ 
/* 539 */         PacketEvent output = this.channelListener.onPacketReceiving(this, input, marker);
/*     */ 
/* 542 */         if (output != null)
/* 543 */           if (output.isCancelled()) {
/* 544 */             it.remove();
/*     */           } else {
/* 546 */             if (output.getPacket().getHandle() != input) {
/* 547 */               it.set(output.getPacket().getHandle());
/*     */             }
/*     */ 
/* 550 */             this.finishQueue.addLast(output);
/*     */           }
/*     */       }
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/*     */       ListIterator it;
/* 554 */       this.channelListener.getReporter().reportDetailed(this, Report.newBuilder(REPORT_CANNOT_INTERCEPT_CLIENT_PACKET).callerParam(new Object[] { byteBuffer }).error(e).build());
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void finishRead(ChannelHandlerContext ctx, Object msg)
/*     */   {
/* 566 */     PacketEvent event = (PacketEvent)this.finishQueue.pollFirst();
/*     */ 
/* 568 */     if (event != null) {
/* 569 */       NetworkMarker marker = NetworkMarker.getNetworkMarker(event);
/*     */ 
/* 571 */       if (marker != null)
/* 572 */         this.processor.invokePostEvent(event, marker);
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void handleLogin(Class<?> packetClass, Object packet)
/*     */   {
/* 583 */     Class loginClass = PACKET_LOGIN_CLIENT;
/* 584 */     FieldAccessor loginClient = LOGIN_GAME_PROFILE;
/*     */ 
/* 587 */     if (loginClass == null) {
/* 588 */       loginClass = PacketType.Login.Client.START.getPacketClass();
/* 589 */       PACKET_LOGIN_CLIENT = loginClass;
/*     */     }
/* 591 */     if (loginClient == null) {
/* 592 */       loginClient = Accessors.getFieldAccessor(PACKET_LOGIN_CLIENT, MinecraftReflection.getGameProfileClass(), true);
/* 593 */       LOGIN_GAME_PROFILE = loginClient;
/*     */     }
/*     */ 
/* 597 */     if (loginClass.equals(packetClass))
/*     */     {
/* 599 */       WrappedGameProfile profile = WrappedGameProfile.fromHandle(loginClient.get(packet));
/*     */ 
/* 602 */       this.factory.cacheInjector(profile.getName(), this);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void channelActive(ChannelHandlerContext ctx) throws Exception
/*     */   {
/* 608 */     super.channelActive(ctx);
/*     */ 
/* 611 */     if (this.channelField != null)
/* 612 */       this.channelField.refreshValue();
/*     */   }
/*     */ 
/*     */   private byte[] getBytes(ByteBuf buffer)
/*     */   {
/* 622 */     byte[] data = new byte[buffer.readableBytes()];
/*     */ 
/* 624 */     buffer.readBytes(data);
/* 625 */     return data;
/*     */   }
/*     */ 
/*     */   private void disconnect(String message)
/*     */   {
/* 634 */     if ((this.playerConnection == null) || ((this.player instanceof Factory)))
/* 635 */       this.originalChannel.disconnect();
/*     */     else
/*     */       try
/*     */       {
/* 639 */         MinecraftMethods.getDisconnectMethod(this.playerConnection.getClass()).invoke(this.playerConnection, new Object[] { message });
/*     */       }
/*     */       catch (Exception e) {
/* 642 */         throw new IllegalArgumentException("Unable to invoke disconnect method.", e);
/*     */       }
/*     */   }
/*     */ 
/*     */   public void sendServerPacket(Object packet, NetworkMarker marker, boolean filtered)
/*     */   {
/* 649 */     saveMarker(packet, marker);
/*     */     try
/*     */     {
/* 652 */       this.scheduleProcessPackets.set(Boolean.valueOf(filtered));
/* 653 */       invokeSendPacket(packet);
/*     */     } finally {
/* 655 */       this.scheduleProcessPackets.set(Boolean.valueOf(true));
/*     */     }
/*     */   }
/*     */ 
/*     */   private void invokeSendPacket(Object packet)
/*     */   {
/*     */     try
/*     */     {
/* 666 */       if ((this.player instanceof Factory))
/* 667 */         MinecraftMethods.getNetworkManagerHandleMethod().invoke(this.networkManager, new Object[] { packet, new GenericFutureListener[0] });
/*     */       else
/* 669 */         MinecraftMethods.getSendPacketMethod().invoke(getPlayerConnection(), new Object[] { packet });
/*     */     }
/*     */     catch (Exception e) {
/* 672 */       throw new RuntimeException("Unable to send server packet " + packet, e);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void recieveClientPacket(final Object packet)
/*     */   {
/* 681 */     Runnable action = new Runnable()
/*     */     {
/*     */       public void run() {
/*     */         try {
/* 685 */           MinecraftMethods.getNetworkManagerReadPacketMethod().invoke(ChannelInjector.this.networkManager, new Object[] { null, packet });
/*     */         }
/*     */         catch (Exception e) {
/* 688 */           ProtocolLibrary.getErrorReporter().reportMinimal(ChannelInjector.this.factory.getPlugin(), "recieveClientPacket", e);
/*     */         }
/*     */       }
/*     */     };
/* 694 */     if (this.originalChannel.eventLoop().inEventLoop())
/* 695 */       action.run();
/*     */     else
/* 697 */       this.originalChannel.eventLoop().execute(action);
/*     */   }
/*     */ 
/*     */   public PacketType.Protocol getCurrentProtocol()
/*     */   {
/* 703 */     if (PROTOCOL_ACCESSOR == null) {
/* 704 */       PROTOCOL_ACCESSOR = Accessors.getFieldAccessor(this.networkManager.getClass(), MinecraftReflection.getEnumProtocolClass(), true);
/*     */     }
/*     */ 
/* 707 */     return PacketType.Protocol.fromVanilla((Enum)PROTOCOL_ACCESSOR.get(this.networkManager));
/*     */   }
/*     */ 
/*     */   private Object getPlayerConnection()
/*     */   {
/* 715 */     if (this.playerConnection == null) {
/* 716 */       this.playerConnection = MinecraftFields.getPlayerConnection(this.player);
/*     */     }
/* 718 */     return this.playerConnection;
/*     */   }
/*     */ 
/*     */   public NetworkMarker getMarker(Object packet)
/*     */   {
/* 723 */     return (NetworkMarker)this.packetMarker.get(packet);
/*     */   }
/*     */ 
/*     */   public void saveMarker(Object packet, NetworkMarker marker)
/*     */   {
/* 728 */     if (marker != null)
/* 729 */       this.packetMarker.put(packet, marker);
/*     */   }
/*     */ 
/*     */   public Player getPlayer()
/*     */   {
/* 735 */     if ((this.player == null) && (this.playerName != null)) {
/* 736 */       return Bukkit.getPlayer(this.playerName);
/*     */     }
/*     */ 
/* 739 */     return this.player;
/*     */   }
/*     */ 
/*     */   public void setPlayer(Player player)
/*     */   {
/* 748 */     this.player = player;
/* 749 */     this.playerName = player.getName();
/*     */   }
/*     */ 
/*     */   public void setUpdatedPlayer(Player updated)
/*     */   {
/* 758 */     this.updated = updated;
/* 759 */     this.playerName = updated.getName();
/*     */   }
/*     */ 
/*     */   public boolean isInjected()
/*     */   {
/* 764 */     return this.injected;
/*     */   }
/*     */ 
/*     */   public boolean isClosed()
/*     */   {
/* 773 */     return this.closed;
/*     */   }
/*     */ 
/*     */   public void close()
/*     */   {
/* 778 */     if (!this.closed) {
/* 779 */       this.closed = true;
/*     */ 
/* 781 */       if (this.injected) {
/* 782 */         this.channelField.revertValue();
/*     */ 
/* 801 */         executeInChannelThread(new Runnable()
/*     */         {
/*     */           public void run() {
/* 804 */             String[] handlers = { "protocol_lib_decoder", "protocol_lib_finish", "protocol_lib_encoder", "protocol_lib_exception_handler" };
/*     */ 
/* 808 */             for (String handler : handlers)
/*     */               try {
/* 810 */                 ChannelInjector.this.originalChannel.pipeline().remove(handler);
/*     */               }
/*     */               catch (NoSuchElementException e)
/*     */               {
/*     */               }
/*     */           }
/*     */         });
/* 819 */         this.factory.invalidate(this.player);
/*     */ 
/* 823 */         this.player = null;
/* 824 */         this.updated = null;
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private void executeInChannelThread(final Runnable command)
/*     */   {
/* 836 */     this.originalChannel.eventLoop().execute(new Runnable()
/*     */     {
/*     */       public void run() {
/*     */         try {
/* 840 */           command.run();
/*     */         } catch (Exception e) {
/* 842 */           ProtocolLibrary.getErrorReporter().reportDetailed(ChannelInjector.this, Report.newBuilder(ChannelInjector.REPORT_CANNOT_EXECUTE_IN_CHANNEL_THREAD).error(e).build());
/*     */         }
/*     */       }
/*     */     });
/*     */   }
/*     */ 
/*     */   public static ChannelHandler findChannelHandler(Channel channel, Class<?> clazz)
/*     */   {
/* 856 */     for (Map.Entry entry : channel.pipeline()) {
/* 857 */       if (clazz.isAssignableFrom(((ChannelHandler)entry.getValue()).getClass())) {
/* 858 */         return (ChannelHandler)entry.getValue();
/*     */       }
/*     */     }
/* 861 */     return null;
/*     */   }
/*     */ 
/*     */   public Channel getChannel()
/*     */   {
/* 921 */     return this.originalChannel;
/*     */   }
/*     */ 
/*     */   static class ChannelSocketInjector
/*     */     implements SocketInjector
/*     */   {
/*     */     private final ChannelInjector injector;
/*     */ 
/*     */     public ChannelSocketInjector(ChannelInjector injector)
/*     */     {
/* 872 */       this.injector = ((ChannelInjector)Preconditions.checkNotNull(injector, "injector cannot be NULL"));
/*     */     }
/*     */ 
/*     */     public Socket getSocket() throws IllegalAccessException
/*     */     {
/* 877 */       return NettySocketAdaptor.adapt((SocketChannel)this.injector.originalChannel);
/*     */     }
/*     */ 
/*     */     public SocketAddress getAddress() throws IllegalAccessException
/*     */     {
/* 882 */       return this.injector.originalChannel.remoteAddress();
/*     */     }
/*     */ 
/*     */     public void disconnect(String message) throws InvocationTargetException
/*     */     {
/* 887 */       this.injector.disconnect(message);
/*     */     }
/*     */ 
/*     */     public void sendServerPacket(Object packet, NetworkMarker marker, boolean filtered) throws InvocationTargetException
/*     */     {
/* 892 */       this.injector.sendServerPacket(packet, marker, filtered);
/*     */     }
/*     */ 
/*     */     public Player getPlayer()
/*     */     {
/* 897 */       return this.injector.getPlayer();
/*     */     }
/*     */ 
/*     */     public Player getUpdatedPlayer()
/*     */     {
/* 902 */       return this.injector.updated;
/*     */     }
/*     */ 
/*     */     public void transferState(SocketInjector delegate)
/*     */     {
/*     */     }
/*     */ 
/*     */     public void setUpdatedPlayer(Player updatedPlayer)
/*     */     {
/* 912 */       this.injector.setPlayer(updatedPlayer);
/*     */     }
/*     */ 
/*     */     public ChannelInjector getChannelInjector() {
/* 916 */       return this.injector;
/*     */     }
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.injector.netty.ChannelInjector
 * JD-Core Version:    0.6.2
 */