/*     */ package com.comphenix.protocol.utility;
/*     */ 
/*     */ import com.comphenix.net.sf.cglib.proxy.Enhancer;
/*     */ import com.comphenix.net.sf.cglib.proxy.MethodInterceptor;
/*     */ import com.comphenix.net.sf.cglib.proxy.MethodProxy;
/*     */ import com.comphenix.protocol.PacketType.Play.Client;
/*     */ import com.comphenix.protocol.events.PacketContainer;
/*     */ import com.comphenix.protocol.reflect.FuzzyReflection;
/*     */ import io.netty.buffer.ByteBuf;
/*     */ import io.netty.buffer.UnpooledByteBufAllocator;
/*     */ import io.netty.channel.ChannelHandlerContext;
/*     */ import io.netty.util.concurrent.GenericFutureListener;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.lang.reflect.Method;
/*     */ import java.util.Collection;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ 
/*     */ public class MinecraftMethods
/*     */ {
/*     */   private static volatile Method sendPacketMethod;
/*     */   private static volatile Method networkManagerHandle;
/*     */   private static volatile Method networkManagerPacketRead;
/*     */   private static volatile Method packetReadByteBuf;
/*     */   private static volatile Method packetWriteByteBuf;
/*     */ 
/*     */   public static Method getSendPacketMethod()
/*     */   {
/*  43 */     if (sendPacketMethod == null) {
/*  44 */       Class serverHandlerClass = MinecraftReflection.getNetServerHandlerClass();
/*     */       try
/*     */       {
/*  47 */         sendPacketMethod = FuzzyReflection.fromClass(serverHandlerClass).getMethodByName("sendPacket.*");
/*     */       }
/*     */       catch (IllegalArgumentException e) {
/*  50 */         if (MinecraftReflection.isUsingNetty()) {
/*  51 */           sendPacketMethod = FuzzyReflection.fromClass(serverHandlerClass).getMethodByParameters("sendPacket", new Class[] { MinecraftReflection.getPacketClass() });
/*     */ 
/*  53 */           return sendPacketMethod;
/*     */         }
/*     */ 
/*  56 */         Map netServer = getMethodList(serverHandlerClass, new Class[] { MinecraftReflection.getPacketClass() });
/*     */ 
/*  58 */         Map netHandler = getMethodList(MinecraftReflection.getNetHandlerClass(), new Class[] { MinecraftReflection.getPacketClass() });
/*     */ 
/*  62 */         for (String methodName : netHandler.keySet()) {
/*  63 */           netServer.remove(methodName);
/*     */         }
/*     */ 
/*  67 */         if (netServer.size() == 1) {
/*  68 */           Method[] methods = (Method[])netServer.values().toArray(new Method[0]);
/*  69 */           sendPacketMethod = methods[0];
/*     */         } else {
/*  71 */           throw new IllegalArgumentException("Unable to find the sendPacket method in NetServerHandler/PlayerConnection.");
/*     */         }
/*     */       }
/*     */     }
/*  75 */     return sendPacketMethod; } 
/*     */   public static Method getDisconnectMethod(Class<? extends Object> playerConnection) { // Byte code:
/*     */     //   0: aload_0
/*     */     //   1: invokestatic 51	com/comphenix/protocol/reflect/FuzzyReflection:fromClass	(Ljava/lang/Class;)Lcom/comphenix/protocol/reflect/FuzzyReflection;
/*     */     //   4: ldc 147
/*     */     //   6: invokevirtual 57	com/comphenix/protocol/reflect/FuzzyReflection:getMethodByName	(Ljava/lang/String;)Ljava/lang/reflect/Method;
/*     */     //   9: areturn
/*     */     //   10: astore_1
/*     */     //   11: aload_0
/*     */     //   12: invokestatic 151	com/comphenix/protocol/reflect/FuzzyReflection:fromObject	(Ljava/lang/Object;)Lcom/comphenix/protocol/reflect/FuzzyReflection;
/*     */     //   15: ldc 153
/*     */     //   17: iconst_1
/*     */     //   18: anewarray 59	java/lang/Class
/*     */     //   21: dup
/*     */     //   22: iconst_0
/*     */     //   23: ldc 102
/*     */     //   25: aastore
/*     */     //   26: invokevirtual 72	com/comphenix/protocol/reflect/FuzzyReflection:getMethodByParameters	(Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;
/*     */     //   29: areturn
/*     */     //
/*     */     // Exception table:
/*     */     //   from	to	target	type
/*     */     //   0	9	10	java/lang/IllegalArgumentException } 
/*  99 */   public static Method getNetworkManagerHandleMethod() { if (networkManagerHandle == null) {
/* 100 */       networkManagerHandle = FuzzyReflection.fromClass(MinecraftReflection.getNetworkManagerClass(), true).getMethodByParameters("handle", new Class[] { MinecraftReflection.getPacketClass(), [Lio.netty.util.concurrent.GenericFutureListener.class });
/*     */ 
/* 102 */       networkManagerHandle.setAccessible(true);
/*     */     }
/* 104 */     return networkManagerHandle;
/*     */   }
/*     */ 
/*     */   public static Method getNetworkManagerReadPacketMethod()
/*     */   {
/* 114 */     if (networkManagerPacketRead == null) {
/* 115 */       networkManagerPacketRead = FuzzyReflection.fromClass(MinecraftReflection.getNetworkManagerClass(), true).getMethodByParameters("packetRead", new Class[] { ChannelHandlerContext.class, MinecraftReflection.getPacketClass() });
/*     */ 
/* 117 */       networkManagerPacketRead.setAccessible(true);
/*     */     }
/* 119 */     return networkManagerPacketRead;
/*     */   }
/*     */ 
/*     */   private static Map<String, Method> getMethodList(Class<?> source, Class<?>[] params)
/*     */   {
/* 129 */     FuzzyReflection reflect = FuzzyReflection.fromClass(source, true);
/*     */ 
/* 131 */     return reflect.getMappedMethods(reflect.getMethodListByParameters(Void.TYPE, params));
/*     */   }
/*     */ 
/*     */   public static Method getPacketReadByteBufMethod()
/*     */   {
/* 143 */     initializePacket();
/* 144 */     return packetReadByteBuf;
/*     */   }
/*     */ 
/*     */   public static Method getPacketWriteByteBufMethod()
/*     */   {
/* 154 */     initializePacket();
/* 155 */     return packetWriteByteBuf;
/*     */   }
/*     */ 
/*     */   private static void initializePacket()
/*     */   {
/* 163 */     if ((packetReadByteBuf == null) || (packetWriteByteBuf == null))
/*     */     {
/* 165 */       Enhancer enhancer = EnhancerFactory.getInstance().createEnhancer();
/* 166 */       enhancer.setSuperclass(MinecraftReflection.getPacketDataSerializerClass());
/* 167 */       enhancer.setCallback(new MethodInterceptor()
/*     */       {
/*     */         public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
/* 170 */           if (method.getName().contains("read"))
/* 171 */             throw new MinecraftMethods.ReadMethodException();
/* 172 */           if (method.getName().contains("write"))
/* 173 */             throw new MinecraftMethods.WriteMethodException();
/* 174 */           return proxy.invokeSuper(obj, args);
/*     */         }
/*     */       });
/* 179 */       Object javaProxy = enhancer.create(new Class[] { ByteBuf.class }, new Object[] { UnpooledByteBufAllocator.DEFAULT.buffer() });
/*     */ 
/* 184 */       Object lookPacket = new PacketContainer(PacketType.Play.Client.CLOSE_WINDOW).getHandle();
/* 185 */       List candidates = FuzzyReflection.fromClass(MinecraftReflection.getPacketClass()).getMethodListByParameters(Void.TYPE, new Class[] { MinecraftReflection.getPacketDataSerializerClass() });
/*     */ 
/* 189 */       for (Method method : candidates) {
/*     */         try {
/* 191 */           method.invoke(lookPacket, new Object[] { javaProxy });
/*     */         } catch (InvocationTargetException e) {
/* 193 */           if ((e.getCause() instanceof ReadMethodException))
/*     */           {
/* 195 */             packetReadByteBuf = method;
/* 196 */           } else if ((e.getCause() instanceof WriteMethodException)) {
/* 197 */             packetWriteByteBuf = method;
/*     */           }
/*     */         }
/*     */         catch (Exception e)
/*     */         {
/* 202 */           throw new RuntimeException("Generic reflection error.", e);
/*     */         }
/*     */       }
/*     */ 
/* 206 */       if (packetReadByteBuf == null)
/* 207 */         throw new IllegalStateException("Unable to find Packet.read(PacketDataSerializer)");
/* 208 */       if (packetWriteByteBuf == null)
/* 209 */         throw new IllegalStateException("Unable to find Packet.write(PacketDataSerializer)");
/*     */     }
/*     */   }
/*     */ 
/*     */   private static class WriteMethodException extends RuntimeException
/*     */   {
/*     */     private static final long serialVersionUID = 1L;
/*     */ 
/*     */     public WriteMethodException()
/*     */     {
/* 233 */       super();
/*     */     }
/*     */   }
/*     */ 
/*     */   private static class ReadMethodException extends RuntimeException
/*     */   {
/*     */     private static final long serialVersionUID = 1L;
/*     */ 
/*     */     public ReadMethodException()
/*     */     {
/* 221 */       super();
/*     */     }
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.utility.MinecraftMethods
 * JD-Core Version:    0.6.2
 */