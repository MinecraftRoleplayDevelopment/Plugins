/*     */ package com.comphenix.protocol.injector.packet;
/*     */ 
/*     */ import com.comphenix.net.sf.cglib.proxy.MethodInterceptor;
/*     */ import com.comphenix.net.sf.cglib.proxy.MethodProxy;
/*     */ import com.comphenix.protocol.error.ErrorReporter;
/*     */ import com.comphenix.protocol.error.Report;
/*     */ import com.comphenix.protocol.error.Report.ReportBuilder;
/*     */ import com.comphenix.protocol.error.ReportType;
/*     */ import com.comphenix.protocol.events.NetworkMarker;
/*     */ import com.comphenix.protocol.events.PacketEvent;
/*     */ import com.comphenix.protocol.injector.NetworkProcessor;
/*     */ import com.google.common.collect.MapMaker;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.DataOutput;
/*     */ import java.io.DataOutputStream;
/*     */ import java.lang.reflect.Method;
/*     */ import java.util.Collection;
/*     */ import java.util.Map;
/*     */ 
/*     */ public class WritePacketModifier
/*     */   implements MethodInterceptor
/*     */ {
/*  38 */   public static final ReportType REPORT_CANNOT_WRITE_SERVER_PACKET = new ReportType("Cannot write server packet.");
/*     */ 
/*  53 */   private Map<Object, ProxyInformation> proxyLookup = new MapMaker().weakKeys().makeMap();
/*     */   private final ErrorReporter reporter;
/*     */   private final NetworkProcessor processor;
/*     */   private boolean isWriteMethod;
/*     */ 
/*     */   public WritePacketModifier(ErrorReporter reporter, boolean isWriteMethod)
/*     */   {
/*  63 */     this.reporter = reporter;
/*  64 */     this.processor = new NetworkProcessor(reporter);
/*  65 */     this.isWriteMethod = isWriteMethod;
/*     */   }
/*     */ 
/*     */   public void register(Object generatedClass, Object proxyObject, PacketEvent event, NetworkMarker marker)
/*     */   {
/*  76 */     this.proxyLookup.put(generatedClass, new ProxyInformation(proxyObject, event, marker));
/*     */   }
/*     */ 
/*     */   public Object intercept(Object thisObj, Method method, Object[] args, MethodProxy proxy) throws Throwable
/*     */   {
/*  81 */     ProxyInformation information = (ProxyInformation)this.proxyLookup.get(thisObj);
/*     */ 
/*  83 */     if (information == null)
/*     */     {
/*  85 */       throw new RuntimeException("Cannot find proxy information for " + thisObj);
/*     */     }
/*     */ 
/*  88 */     if (this.isWriteMethod)
/*     */     {
/*  90 */       if (!information.marker.getOutputHandlers().isEmpty()) {
/*     */         try {
/*  92 */           DataOutput output = (DataOutput)args[0];
/*     */ 
/*  95 */           ByteArrayOutputStream outputBufferStream = new ByteArrayOutputStream();
/*  96 */           proxy.invoke(information.proxyObject, new Object[] { new DataOutputStream(outputBufferStream) });
/*     */ 
/*  99 */           byte[] outputBuffer = this.processor.processOutput(information.event, information.marker, outputBufferStream.toByteArray());
/*     */ 
/* 103 */           output.write(outputBuffer);
/*     */ 
/* 106 */           this.processor.invokePostEvent(information.event, information.marker);
/* 107 */           return null;
/*     */         }
/*     */         catch (OutOfMemoryError e) {
/* 110 */           throw e;
/*     */         } catch (ThreadDeath e) {
/* 112 */           throw e;
/*     */         }
/*     */         catch (Throwable e) {
/* 115 */           this.reporter.reportDetailed(this, Report.newBuilder(REPORT_CANNOT_WRITE_SERVER_PACKET).callerParam(new Object[] { args[0] }).error(e));
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 122 */       proxy.invoke(information.proxyObject, args);
/* 123 */       this.processor.invokePostEvent(information.event, information.marker);
/* 124 */       return null;
/*     */     }
/*     */ 
/* 128 */     return proxy.invoke(information.proxyObject, args);
/*     */   }
/*     */ 
/*     */   private static class ProxyInformation
/*     */   {
/*     */     public final Object proxyObject;
/*     */     public final PacketEvent event;
/*     */     public final NetworkMarker marker;
/*     */ 
/*     */     public ProxyInformation(Object proxyObject, PacketEvent event, NetworkMarker marker)
/*     */     {
/*  47 */       this.proxyObject = proxyObject;
/*  48 */       this.event = event;
/*  49 */       this.marker = marker;
/*     */     }
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.injector.packet.WritePacketModifier
 * JD-Core Version:    0.6.2
 */