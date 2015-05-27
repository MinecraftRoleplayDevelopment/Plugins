/*     */ package com.comphenix.protocol.injector.player;
/*     */ 
/*     */ import com.comphenix.net.sf.cglib.proxy.Callback;
/*     */ import com.comphenix.net.sf.cglib.proxy.Enhancer;
/*     */ import com.comphenix.net.sf.cglib.proxy.MethodInterceptor;
/*     */ import com.comphenix.net.sf.cglib.proxy.MethodProxy;
/*     */ import com.comphenix.protocol.PacketType;
/*     */ import com.comphenix.protocol.ProtocolLibrary;
/*     */ import com.comphenix.protocol.error.ErrorReporter;
/*     */ import com.comphenix.protocol.error.Report;
/*     */ import com.comphenix.protocol.error.Report.ReportBuilder;
/*     */ import com.comphenix.protocol.error.ReportType;
/*     */ import com.comphenix.protocol.injector.ListenerInvoker;
/*     */ import com.comphenix.protocol.utility.EnhancerFactory;
/*     */ import com.comphenix.protocol.utility.MinecraftReflection;
/*     */ import com.google.common.collect.MapMaker;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.lang.reflect.Method;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Set;
/*     */ import java.util.concurrent.ConcurrentMap;
/*     */ 
/*     */ class InjectedArrayList extends ArrayList<Object>
/*     */ {
/*  47 */   public static final ReportType REPORT_CANNOT_REVERT_CANCELLED_PACKET = new ReportType("Reverting cancelled packet failed.");
/*     */   private static final long serialVersionUID = -1173865905404280990L;
/*  55 */   private static ConcurrentMap<Object, Object> delegateLookup = new MapMaker().weakKeys().makeMap();
/*     */   private transient PlayerInjector injector;
/*     */   private transient Set<Object> ignoredPackets;
/*     */   private transient InvertedIntegerCallback callback;
/*     */ 
/*     */   public InjectedArrayList(PlayerInjector injector, Set<Object> ignoredPackets)
/*     */   {
/*  63 */     this.injector = injector;
/*  64 */     this.ignoredPackets = ignoredPackets;
/*  65 */     this.callback = new InvertedIntegerCallback(null);
/*     */   }
/*     */ 
/*     */   public boolean add(Object packet)
/*     */   {
/*  71 */     Object result = null;
/*     */ 
/*  74 */     if ((packet instanceof NetworkFieldInjector.FakePacket))
/*  75 */       return true;
/*  76 */     if (this.ignoredPackets.contains(packet))
/*     */     {
/*  78 */       result = Boolean.valueOf(this.ignoredPackets.remove(packet));
/*     */     }
/*  80 */     else result = this.injector.handlePacketSending(packet);
/*     */ 
/*     */     try
/*     */     {
/*  85 */       if (result != null) {
/*  86 */         super.add(result);
/*     */       }
/*     */       else {
/*  89 */         this.injector.sendServerPacket(createNegativePacket(packet), null, true);
/*     */       }
/*     */ 
/*  93 */       return true;
/*     */     }
/*     */     catch (InvocationTargetException e)
/*     */     {
/*  97 */       ProtocolLibrary.getErrorReporter().reportDetailed(this, Report.newBuilder(REPORT_CANNOT_REVERT_CANCELLED_PACKET).error(e).callerParam(new Object[] { packet }));
/*     */     }
/*     */ 
/* 102 */     return false;
/*     */   }
/*     */ 
/*     */   Object createNegativePacket(Object source)
/*     */   {
/* 115 */     ListenerInvoker invoker = this.injector.getInvoker();
/*     */ 
/* 117 */     PacketType type = invoker.getPacketType(source);
/*     */ 
/* 137 */     Enhancer ex = EnhancerFactory.getInstance().createEnhancer();
/* 138 */     ex.setSuperclass(MinecraftReflection.getPacketClass());
/* 139 */     ex.setInterfaces(new Class[] { NetworkFieldInjector.FakePacket.class });
/* 140 */     ex.setUseCache(true);
/* 141 */     ex.setCallbackType(InvertedIntegerCallback.class);
/*     */ 
/* 143 */     Class proxyClass = ex.createClass();
/* 144 */     Enhancer.registerCallbacks(proxyClass, new Callback[] { this.callback });
/*     */     try
/*     */     {
/* 148 */       invoker.registerPacketClass(proxyClass, type.getLegacyId());
/* 149 */       Object proxy = proxyClass.newInstance();
/*     */ 
/* 151 */       registerDelegate(proxy, source);
/* 152 */       return proxy;
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/* 156 */       throw new RuntimeException("Cannot create fake class.", e);
/*     */     }
/*     */     finally {
/* 159 */       invoker.unregisterPacketClass(proxyClass);
/*     */     }
/*     */   }
/*     */ 
/*     */   private static void registerDelegate(Object proxy, Object source)
/*     */   {
/* 169 */     delegateLookup.put(proxy, source);
/*     */   }
/*     */ 
/*     */   private class InvertedIntegerCallback implements MethodInterceptor
/*     */   {
/*     */     private InvertedIntegerCallback()
/*     */     {
/*     */     }
/*     */ 
/*     */     public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
/* 179 */       Object delegate = InjectedArrayList.delegateLookup.get(obj);
/*     */ 
/* 181 */       if (delegate == null) {
/* 182 */         throw new IllegalStateException("Unable to find delegate source for " + obj);
/*     */       }
/*     */ 
/* 185 */       if ((method.getReturnType().equals(Integer.TYPE)) && (args.length == 0)) {
/* 186 */         Integer result = (Integer)proxy.invoke(delegate, args);
/* 187 */         return Integer.valueOf(-result.intValue());
/*     */       }
/* 189 */       return proxy.invoke(delegate, args);
/*     */     }
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.injector.player.InjectedArrayList
 * JD-Core Version:    0.6.2
 */