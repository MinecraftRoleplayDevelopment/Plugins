/*     */ package com.comphenix.protocol.injector.packet;
/*     */ 
/*     */ import com.comphenix.net.sf.cglib.proxy.Callback;
/*     */ import com.comphenix.net.sf.cglib.proxy.CallbackFilter;
/*     */ import com.comphenix.net.sf.cglib.proxy.Enhancer;
/*     */ import com.comphenix.protocol.error.ErrorReporter;
/*     */ import com.comphenix.protocol.error.Report;
/*     */ import com.comphenix.protocol.error.Report.ReportBuilder;
/*     */ import com.comphenix.protocol.error.ReportType;
/*     */ import com.comphenix.protocol.events.NetworkMarker;
/*     */ import com.comphenix.protocol.events.PacketEvent;
/*     */ import com.comphenix.protocol.reflect.MethodInfo;
/*     */ import com.comphenix.protocol.reflect.fuzzy.FuzzyMethodContract;
/*     */ import com.comphenix.protocol.reflect.fuzzy.FuzzyMethodContract.Builder;
/*     */ import com.comphenix.protocol.utility.EnhancerFactory;
/*     */ import com.comphenix.protocol.utility.MinecraftReflection;
/*     */ import com.google.common.collect.Maps;
/*     */ import java.io.DataOutput;
/*     */ import java.lang.reflect.Method;
/*     */ import java.util.Map;
/*     */ import java.util.concurrent.ConcurrentMap;
/*     */ 
/*     */ public class InterceptWritePacket
/*     */ {
/*  27 */   public static final ReportType REPORT_CANNOT_FIND_WRITE_PACKET_METHOD = new ReportType("Cannot find write packet method in %s.");
/*  28 */   public static final ReportType REPORT_CANNOT_CONSTRUCT_WRITE_PROXY = new ReportType("Cannot construct write proxy packet %s.");
/*     */ 
/*  33 */   private static FuzzyMethodContract WRITE_PACKET = FuzzyMethodContract.newBuilder().returnTypeVoid().parameterDerivedOf(DataOutput.class).parameterCount(1).build();
/*     */   private CallbackFilter filter;
/*     */   private boolean writePacketIntercepted;
/*  42 */   private ConcurrentMap<Integer, Class<?>> proxyClasses = Maps.newConcurrentMap();
/*     */   private ErrorReporter reporter;
/*     */   private WritePacketModifier modifierWrite;
/*     */   private WritePacketModifier modifierRest;
/*     */ 
/*     */   public InterceptWritePacket(ErrorReporter reporter)
/*     */   {
/*  49 */     this.reporter = reporter;
/*     */ 
/*  52 */     this.modifierWrite = new WritePacketModifier(reporter, true);
/*  53 */     this.modifierRest = new WritePacketModifier(reporter, false);
/*     */   }
/*     */ 
/*     */   private Class<?> createProxyClass(int packetId)
/*     */   {
/*  59 */     Enhancer ex = EnhancerFactory.getInstance().createEnhancer();
/*     */ 
/*  62 */     if (this.filter == null) {
/*  63 */       this.filter = new CallbackFilter()
/*     */       {
/*     */         public int accept(Method method)
/*     */         {
/*  67 */           if (InterceptWritePacket.WRITE_PACKET.isMatch(MethodInfo.fromMethod(method), null)) {
/*  68 */             InterceptWritePacket.this.writePacketIntercepted = true;
/*  69 */             return 0;
/*     */           }
/*  71 */           return 1;
/*     */         }
/*     */ 
/*     */       };
/*     */     }
/*     */ 
/*  78 */     ex.setSuperclass(MinecraftReflection.getPacketClass());
/*  79 */     ex.setCallbackFilter(this.filter);
/*  80 */     ex.setUseCache(false);
/*     */ 
/*  82 */     ex.setCallbackTypes(new Class[] { WritePacketModifier.class, WritePacketModifier.class });
/*  83 */     Class proxyClass = ex.createClass();
/*     */ 
/*  86 */     Enhancer.registerStaticCallbacks(proxyClass, new Callback[] { this.modifierWrite, this.modifierRest });
/*     */ 
/*  88 */     if (proxyClass != null)
/*     */     {
/*  90 */       if (!this.writePacketIntercepted) {
/*  91 */         this.reporter.reportWarning(this, Report.newBuilder(REPORT_CANNOT_FIND_WRITE_PACKET_METHOD).messageParam(new Object[] { MinecraftReflection.getPacketClass() }));
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/*  96 */     return proxyClass;
/*     */   }
/*     */ 
/*     */   private Class<?> getProxyClass(int packetId)
/*     */   {
/* 101 */     Class stored = (Class)this.proxyClasses.get(Integer.valueOf(packetId));
/*     */ 
/* 104 */     if (stored == null) {
/* 105 */       Class created = createProxyClass(packetId);
/* 106 */       stored = (Class)this.proxyClasses.putIfAbsent(Integer.valueOf(packetId), created);
/*     */ 
/* 109 */       if (stored == null) {
/* 110 */         stored = created;
/* 111 */         PacketRegistry.getPacketToID().put(stored, Integer.valueOf(packetId));
/*     */       }
/*     */     }
/* 114 */     return stored;
/*     */   }
/*     */ 
/*     */   public Object constructProxy(Object proxyObject, PacketEvent event, NetworkMarker marker)
/*     */   {
/* 123 */     Class proxyClass = null;
/*     */     try
/*     */     {
/* 126 */       proxyClass = getProxyClass(event.getPacketID());
/* 127 */       Object generated = proxyClass.newInstance();
/*     */ 
/* 129 */       this.modifierWrite.register(generated, proxyObject, event, marker);
/* 130 */       this.modifierRest.register(generated, proxyObject, event, marker);
/* 131 */       return generated;
/*     */     }
/*     */     catch (Exception e) {
/* 134 */       this.reporter.reportWarning(this, Report.newBuilder(REPORT_CANNOT_CONSTRUCT_WRITE_PROXY).messageParam(new Object[] { proxyClass }));
/*     */     }
/*     */ 
/* 137 */     return null;
/*     */   }
/*     */ 
/*     */   public void cleanup()
/*     */   {
/* 147 */     for (Class stored : this.proxyClasses.values())
/* 148 */       PacketRegistry.getPacketToID().remove(stored);
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.injector.packet.InterceptWritePacket
 * JD-Core Version:    0.6.2
 */