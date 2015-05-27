/*     */ package com.comphenix.protocol.injector.packet;
/*     */ 
/*     */ import com.comphenix.net.sf.cglib.proxy.Callback;
/*     */ import com.comphenix.net.sf.cglib.proxy.CallbackFilter;
/*     */ import com.comphenix.net.sf.cglib.proxy.Enhancer;
/*     */ import com.comphenix.net.sf.cglib.proxy.Factory;
/*     */ import com.comphenix.net.sf.cglib.proxy.NoOp;
/*     */ import com.comphenix.protocol.PacketType;
/*     */ import com.comphenix.protocol.PacketType.Sender;
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
/*     */ import com.comphenix.protocol.injector.player.PlayerInjectionHandler;
/*     */ import com.comphenix.protocol.reflect.FieldAccessException;
/*     */ import com.comphenix.protocol.reflect.FieldUtils;
/*     */ import com.comphenix.protocol.reflect.FuzzyReflection;
/*     */ import com.comphenix.protocol.reflect.MethodInfo;
/*     */ import com.comphenix.protocol.reflect.fuzzy.FuzzyMethodContract;
/*     */ import com.comphenix.protocol.reflect.fuzzy.FuzzyMethodContract.Builder;
/*     */ import com.comphenix.protocol.utility.EnhancerFactory;
/*     */ import com.comphenix.protocol.utility.MinecraftReflection;
/*     */ import com.comphenix.protocol.wrappers.WrappedIntHashMap;
/*     */ import java.io.DataInput;
/*     */ import java.io.DataInputStream;
/*     */ import java.io.InputStream;
/*     */ import java.lang.reflect.Field;
/*     */ import java.lang.reflect.Method;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import org.bukkit.entity.Player;
/*     */ 
/*     */ class ProxyPacketInjector
/*     */   implements PacketInjector
/*     */ {
/*  63 */   public static final ReportType REPORT_CANNOT_FIND_READ_PACKET_METHOD = new ReportType("Cannot find read packet method for ID %s.");
/*  64 */   public static final ReportType REPORT_UNKNOWN_ORIGIN_FOR_PACKET = new ReportType("Timeout: Unknown origin %s for packet %s. Are you using GamePhase.LOGIN?");
/*     */ 
/* 135 */   private static FuzzyMethodContract READ_PACKET = FuzzyMethodContract.newBuilder().returnTypeVoid().parameterDerivedOf(DataInput.class).parameterCount(1).build();
/*     */   private static PacketClassLookup lookup;
/*     */   private ListenerInvoker manager;
/*     */   private ErrorReporter reporter;
/*     */   private PlayerInjectionHandler playerInjection;
/*     */   private CallbackFilter filter;
/* 156 */   private boolean readPacketIntercepted = false;
/*     */ 
/*     */   public ProxyPacketInjector(ListenerInvoker manager, PlayerInjectionHandler playerInjection, ErrorReporter reporter)
/*     */     throws FieldAccessException
/*     */   {
/* 161 */     this.manager = manager;
/* 162 */     this.playerInjection = playerInjection;
/* 163 */     this.reporter = reporter;
/* 164 */     initialize();
/*     */   }
/*     */ 
/*     */   public boolean isCancelled(Object packet)
/*     */   {
/* 169 */     return ReadPacketModifier.isCancelled(packet);
/*     */   }
/*     */ 
/*     */   public void setCancelled(Object packet, boolean cancelled)
/*     */   {
/* 174 */     if (cancelled)
/* 175 */       ReadPacketModifier.setOverride(packet, null);
/*     */     else
/* 177 */       ReadPacketModifier.removeOverride(packet);
/*     */   }
/*     */ 
/*     */   private void initialize() throws FieldAccessException
/*     */   {
/* 182 */     if (lookup == null)
/*     */       try {
/* 184 */         lookup = new IntHashMapLookup();
/*     */       }
/*     */       catch (Exception e1) {
/*     */         try {
/* 188 */           lookup = new ArrayLookup();
/*     */         }
/*     */         catch (Exception e2) {
/* 191 */           throw new FieldAccessException(e1.getMessage() + ". Workaround failed too.", e2);
/*     */         }
/*     */       }
/*     */   }
/*     */ 
/*     */   public void inputBuffersChanged(Set<PacketType> set)
/*     */   {
/*     */   }
/*     */ 
/*     */   public boolean addPacketHandler(PacketType type, Set<ListenerOptions> options)
/*     */   {
/* 207 */     int packetID = type.getLegacyId();
/*     */ 
/* 209 */     if (hasPacketHandler(type)) {
/* 210 */       return false;
/*     */     }
/* 212 */     Enhancer ex = EnhancerFactory.getInstance().createEnhancer();
/*     */ 
/* 219 */     Map overwritten = PacketRegistry.getOverwrittenPackets();
/* 220 */     Map previous = PacketRegistry.getPreviousPackets();
/* 221 */     Map registry = PacketRegistry.getPacketToID();
/* 222 */     Class old = PacketRegistry.getPacketClassFromType(type);
/*     */ 
/* 225 */     if (old == null) {
/* 226 */       throw new IllegalStateException("Packet ID " + type + " is not a valid packet type in this version.");
/*     */     }
/*     */ 
/* 229 */     if (Factory.class.isAssignableFrom(old)) {
/* 230 */       throw new IllegalStateException("Packet " + type + " has already been injected.");
/*     */     }
/*     */ 
/* 233 */     if (this.filter == null) {
/* 234 */       this.readPacketIntercepted = false;
/*     */ 
/* 236 */       this.filter = new CallbackFilter()
/*     */       {
/*     */         public int accept(Method method)
/*     */         {
/* 240 */           if (method.getDeclaringClass().equals(Object.class))
/* 241 */             return 0;
/* 242 */           if (ProxyPacketInjector.READ_PACKET.isMatch(MethodInfo.fromMethod(method), null)) {
/* 243 */             ProxyPacketInjector.this.readPacketIntercepted = true;
/* 244 */             return 1;
/*     */           }
/* 246 */           return 2;
/*     */         }
/*     */ 
/*     */       };
/*     */     }
/*     */ 
/* 253 */     ex.setSuperclass(old);
/* 254 */     ex.setCallbackFilter(this.filter);
/* 255 */     ex.setCallbackTypes(new Class[] { NoOp.class, ReadPacketModifier.class, ReadPacketModifier.class });
/* 256 */     Class proxy = ex.createClass();
/*     */ 
/* 259 */     ReadPacketModifier modifierReadPacket = new ReadPacketModifier(packetID, this, this.reporter, true);
/* 260 */     ReadPacketModifier modifierRest = new ReadPacketModifier(packetID, this, this.reporter, false);
/*     */ 
/* 263 */     Enhancer.registerStaticCallbacks(proxy, new Callback[] { NoOp.INSTANCE, modifierReadPacket, modifierRest });
/*     */ 
/* 266 */     if (!this.readPacketIntercepted) {
/* 267 */       this.reporter.reportWarning(this, Report.newBuilder(REPORT_CANNOT_FIND_READ_PACKET_METHOD).messageParam(new Object[] { Integer.valueOf(packetID) }));
/*     */     }
/*     */ 
/* 272 */     previous.put(Integer.valueOf(packetID), old);
/* 273 */     registry.put(proxy, Integer.valueOf(packetID));
/* 274 */     overwritten.put(Integer.valueOf(packetID), proxy);
/* 275 */     lookup.setLookup(packetID, proxy);
/* 276 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean removePacketHandler(PacketType type)
/*     */   {
/* 282 */     int packetID = type.getLegacyId();
/*     */ 
/* 284 */     if (!hasPacketHandler(type)) {
/* 285 */       return false;
/*     */     }
/* 287 */     Map registry = PacketRegistry.getPacketToID();
/* 288 */     Map previous = PacketRegistry.getPreviousPackets();
/* 289 */     Map overwritten = PacketRegistry.getOverwrittenPackets();
/*     */ 
/* 291 */     Class old = (Class)previous.get(Integer.valueOf(packetID));
/* 292 */     Class proxy = PacketRegistry.getPacketClassFromType(type);
/*     */ 
/* 294 */     lookup.setLookup(packetID, old);
/* 295 */     previous.remove(Integer.valueOf(packetID));
/* 296 */     registry.remove(proxy);
/* 297 */     overwritten.remove(Integer.valueOf(packetID));
/* 298 */     return true;
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public boolean requireInputBuffers(int packetId)
/*     */   {
/* 308 */     return this.manager.requireInputBuffer(packetId);
/*     */   }
/*     */ 
/*     */   public boolean hasPacketHandler(PacketType type)
/*     */   {
/* 314 */     return PacketRegistry.getPreviousPackets().containsKey(Integer.valueOf(type.getLegacyId()));
/*     */   }
/*     */ 
/*     */   public Set<PacketType> getPacketHandlers()
/*     */   {
/* 320 */     return PacketRegistry.toPacketTypes(PacketRegistry.getPreviousPackets().keySet(), PacketType.Sender.CLIENT);
/*     */   }
/*     */ 
/*     */   public PacketEvent packetRecieved(PacketContainer packet, InputStream input, byte[] buffered)
/*     */   {
/* 325 */     if (this.playerInjection.canRecievePackets()) {
/* 326 */       return this.playerInjection.handlePacketRecieved(packet, input, buffered);
/*     */     }
/*     */     try
/*     */     {
/* 330 */       Player client = this.playerInjection.getPlayerByConnection((DataInputStream)input);
/*     */ 
/* 333 */       if (client != null) {
/* 334 */         return packetRecieved(packet, client, buffered);
/*     */       }
/*     */ 
/* 337 */       this.reporter.reportWarning(this, Report.newBuilder(REPORT_UNKNOWN_ORIGIN_FOR_PACKET).messageParam(new Object[] { input, packet.getType() }));
/* 338 */       return null;
/*     */     }
/*     */     catch (InterruptedException e)
/*     */     {
/*     */     }
/*     */ 
/* 344 */     return null;
/*     */   }
/*     */ 
/*     */   public PacketEvent packetRecieved(PacketContainer packet, Player client, byte[] buffered)
/*     */   {
/* 350 */     NetworkMarker marker = buffered != null ? new LegacyNetworkMarker(ConnectionSide.CLIENT_SIDE, buffered, packet.getType()) : null;
/* 351 */     PacketEvent event = PacketEvent.fromClient(this.manager, packet, marker, client);
/*     */ 
/* 353 */     this.manager.invokePacketRecieving(event);
/* 354 */     return event;
/*     */   }
/*     */ 
/*     */   public synchronized void cleanupAll()
/*     */   {
/* 360 */     Map overwritten = PacketRegistry.getOverwrittenPackets();
/* 361 */     Map previous = PacketRegistry.getPreviousPackets();
/*     */ 
/* 364 */     for (Integer id : (Integer[])previous.keySet().toArray(new Integer[0])) {
/* 365 */       removePacketHandler(PacketType.findLegacy(id.intValue(), PacketType.Sender.CLIENT));
/* 366 */       removePacketHandler(PacketType.findLegacy(id.intValue(), PacketType.Sender.SERVER));
/*     */     }
/*     */ 
/* 369 */     overwritten.clear();
/* 370 */     previous.clear();
/*     */   }
/*     */ 
/*     */   private static class ArrayLookup
/*     */     implements ProxyPacketInjector.PacketClassLookup
/*     */   {
/*     */     private Class<?>[] array;
/*     */ 
/*     */     public ArrayLookup()
/*     */       throws IllegalAccessException
/*     */     {
/* 106 */       initialize();
/*     */     }
/*     */ 
/*     */     public void setLookup(int packetID, Class<?> clazz)
/*     */     {
/* 111 */       this.array[packetID] = clazz;
/*     */     }
/*     */ 
/*     */     private void initialize() throws IllegalAccessException {
/* 115 */       FuzzyReflection reflection = FuzzyReflection.fromClass(MinecraftReflection.getPacketClass());
/*     */ 
/* 118 */       for (Field field : reflection.getFieldListByType([Ljava.lang.Class.class)) {
/* 119 */         Class[] test = (Class[])FieldUtils.readField(field, (Object)null);
/*     */ 
/* 121 */         if (test.length == 256) {
/* 122 */           this.array = test;
/* 123 */           return;
/*     */         }
/*     */       }
/* 126 */       throw new IllegalArgumentException("Unable to find an array with the type " + [Ljava.lang.Class.class + " in " + MinecraftReflection.getPacketClass());
/*     */     }
/*     */   }
/*     */ 
/*     */   private static class IntHashMapLookup
/*     */     implements ProxyPacketInjector.PacketClassLookup
/*     */   {
/*     */     private WrappedIntHashMap intHashMap;
/*     */ 
/*     */     public IntHashMapLookup()
/*     */       throws IllegalAccessException
/*     */     {
/*  78 */       initialize();
/*     */     }
/*     */ 
/*     */     public void setLookup(int packetID, Class<?> clazz)
/*     */     {
/*  83 */       this.intHashMap.put(packetID, clazz);
/*     */     }
/*     */ 
/*     */     private void initialize() throws IllegalAccessException {
/*  87 */       if (this.intHashMap == null)
/*     */       {
/*  89 */         Field intHashMapField = FuzzyReflection.fromClass(MinecraftReflection.getPacketClass(), true).getFieldByType("packetIdMap", MinecraftReflection.getIntHashMapClass());
/*     */         try
/*     */         {
/*  93 */           this.intHashMap = WrappedIntHashMap.fromHandle(FieldUtils.readField(intHashMapField, (Object)null, true));
/*     */         }
/*     */         catch (IllegalArgumentException e) {
/*  96 */           throw new RuntimeException("Minecraft is incompatible.", e);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private static abstract interface PacketClassLookup
/*     */   {
/*     */     public abstract void setLookup(int paramInt, Class<?> paramClass);
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.injector.packet.ProxyPacketInjector
 * JD-Core Version:    0.6.2
 */