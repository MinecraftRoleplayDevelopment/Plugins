/*     */ package com.comphenix.protocol.injector.packet;
/*     */ 
/*     */ import com.comphenix.net.sf.cglib.proxy.MethodInterceptor;
/*     */ import com.comphenix.net.sf.cglib.proxy.MethodProxy;
/*     */ import com.comphenix.protocol.PacketType;
/*     */ import com.comphenix.protocol.PacketType.Sender;
/*     */ import com.comphenix.protocol.error.ErrorReporter;
/*     */ import com.comphenix.protocol.error.Report;
/*     */ import com.comphenix.protocol.error.Report.ReportBuilder;
/*     */ import com.comphenix.protocol.error.ReportType;
/*     */ import com.comphenix.protocol.events.NetworkMarker;
/*     */ import com.comphenix.protocol.events.PacketContainer;
/*     */ import com.comphenix.protocol.events.PacketEvent;
/*     */ import com.comphenix.protocol.injector.NetworkProcessor;
/*     */ import com.google.common.collect.MapMaker;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.DataInputStream;
/*     */ import java.io.InputStream;
/*     */ import java.lang.reflect.Method;
/*     */ import java.util.Map;
/*     */ 
/*     */ class ReadPacketModifier
/*     */   implements MethodInterceptor
/*     */ {
/*  41 */   public static final ReportType REPORT_CANNOT_HANDLE_CLIENT_PACKET = new ReportType("Cannot handle client packet.");
/*     */ 
/*  44 */   private static final Object CANCEL_MARKER = new Object();
/*     */   private ProxyPacketInjector packetInjector;
/*     */   private int packetID;
/*     */   private ErrorReporter reporter;
/*     */   private NetworkProcessor processor;
/*     */   private boolean isReadPacketDataMethod;
/*  58 */   private static Map<Object, Object> override = new MapMaker().weakKeys().makeMap();
/*     */ 
/*     */   public ReadPacketModifier(int packetID, ProxyPacketInjector packetInjector, ErrorReporter reporter, boolean isReadPacketDataMethod) {
/*  61 */     this.packetID = packetID;
/*  62 */     this.packetInjector = packetInjector;
/*  63 */     this.reporter = reporter;
/*  64 */     this.processor = new NetworkProcessor(reporter);
/*  65 */     this.isReadPacketDataMethod = isReadPacketDataMethod;
/*     */   }
/*     */ 
/*     */   public static void removeOverride(Object packet)
/*     */   {
/*  73 */     override.remove(packet);
/*     */   }
/*     */ 
/*     */   public static Object getOverride(Object packet)
/*     */   {
/*  82 */     return override.get(packet);
/*     */   }
/*     */ 
/*     */   public static void setOverride(Object packet, Object overridePacket)
/*     */   {
/*  93 */     override.put(packet, overridePacket != null ? overridePacket : CANCEL_MARKER);
/*     */   }
/*     */ 
/*     */   public static boolean isCancelled(Object packet)
/*     */   {
/* 102 */     return getOverride(packet) == CANCEL_MARKER;
/*     */   }
/*     */ 
/*     */   public Object intercept(Object thisObj, Method method, Object[] args, MethodProxy proxy)
/*     */     throws Throwable
/*     */   {
/* 109 */     Object overridenObject = override.get(thisObj);
/* 110 */     Object returnValue = null;
/*     */ 
/* 113 */     InputStream input = this.isReadPacketDataMethod ? (InputStream)args[0] : null;
/* 114 */     ByteArrayOutputStream bufferStream = null;
/*     */ 
/* 117 */     if ((this.isReadPacketDataMethod) && (this.packetInjector.requireInputBuffers(this.packetID))) {
/* 118 */       CaptureInputStream captured = new CaptureInputStream(input, bufferStream = new ByteArrayOutputStream());
/*     */ 
/* 122 */       args[0] = new DataInputStream(captured);
/*     */     }
/*     */ 
/* 125 */     if (overridenObject != null)
/*     */     {
/* 127 */       if (overridenObject == CANCEL_MARKER)
/*     */       {
/* 129 */         if (method.getReturnType().equals(Void.TYPE)) {
/* 130 */           return null;
/*     */         }
/* 132 */         overridenObject = thisObj;
/*     */       }
/*     */ 
/* 135 */       returnValue = proxy.invokeSuper(overridenObject, args);
/*     */     } else {
/* 137 */       returnValue = proxy.invokeSuper(thisObj, args);
/*     */     }
/*     */ 
/* 141 */     if (this.isReadPacketDataMethod)
/*     */     {
/* 143 */       args[0] = input;
/*     */       try
/*     */       {
/* 146 */         byte[] buffer = bufferStream != null ? bufferStream.toByteArray() : null;
/*     */ 
/* 149 */         PacketType type = PacketType.findLegacy(this.packetID, PacketType.Sender.CLIENT);
/* 150 */         PacketContainer container = new PacketContainer(type, thisObj);
/* 151 */         PacketEvent event = this.packetInjector.packetRecieved(container, input, buffer);
/*     */ 
/* 154 */         if (event != null) {
/* 155 */           Object result = event.getPacket().getHandle();
/*     */ 
/* 157 */           if (event.isCancelled()) {
/* 158 */             override.put(thisObj, CANCEL_MARKER);
/* 159 */             return returnValue;
/* 160 */           }if (!objectEquals(thisObj, result)) {
/* 161 */             override.put(thisObj, result);
/*     */           }
/*     */ 
/* 165 */           NetworkMarker marker = NetworkMarker.getNetworkMarker(event);
/* 166 */           this.processor.invokePostEvent(event, marker);
/*     */         }
/*     */       }
/*     */       catch (OutOfMemoryError e) {
/* 170 */         throw e;
/*     */       } catch (ThreadDeath e) {
/* 172 */         throw e;
/*     */       }
/*     */       catch (Throwable e) {
/* 175 */         this.reporter.reportDetailed(this, Report.newBuilder(REPORT_CANNOT_HANDLE_CLIENT_PACKET).callerParam(new Object[] { args[0] }).error(e));
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 180 */     return returnValue;
/*     */   }
/*     */ 
/*     */   private boolean objectEquals(Object a, Object b) {
/* 184 */     return System.identityHashCode(a) != System.identityHashCode(b);
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.injector.packet.ReadPacketModifier
 * JD-Core Version:    0.6.2
 */