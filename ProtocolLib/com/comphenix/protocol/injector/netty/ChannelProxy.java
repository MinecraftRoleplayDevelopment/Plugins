/*     */ package com.comphenix.protocol.injector.netty;
/*     */ 
/*     */ import com.comphenix.protocol.reflect.accessors.Accessors;
/*     */ import com.comphenix.protocol.reflect.accessors.FieldAccessor;
/*     */ import com.google.common.collect.Maps;
/*     */ import io.netty.buffer.ByteBufAllocator;
/*     */ import io.netty.channel.Channel;
/*     */ import io.netty.channel.Channel.Unsafe;
/*     */ import io.netty.channel.ChannelConfig;
/*     */ import io.netty.channel.ChannelFuture;
/*     */ import io.netty.channel.ChannelMetadata;
/*     */ import io.netty.channel.ChannelPipeline;
/*     */ import io.netty.channel.ChannelProgressivePromise;
/*     */ import io.netty.channel.ChannelPromise;
/*     */ import io.netty.channel.EventLoop;
/*     */ import io.netty.util.Attribute;
/*     */ import io.netty.util.AttributeKey;
/*     */ import java.lang.reflect.Field;
/*     */ import java.net.SocketAddress;
/*     */ import java.util.Map;
/*     */ import java.util.concurrent.Callable;
/*     */ 
/*     */ abstract class ChannelProxy
/*     */   implements Channel
/*     */ {
/*  26 */   private static final FieldAccessor MARK_NO_MESSAGE = new FieldAccessor() {
/*     */     public void set(Object instance, Object value) {
/*     */     }
/*     */     public Object get(Object instance) {
/*  30 */       return null;
/*     */     }
/*  32 */     public Field getField() { return null; }
/*  26 */   };
/*     */ 
/*  36 */   private static Map<Class<?>, FieldAccessor> MESSAGE_LOOKUP = Maps.newConcurrentMap();
/*     */   protected Channel delegate;
/*     */   protected Class<?> messageClass;
/*     */   private transient EventLoopProxy loopProxy;
/*     */ 
/*     */   public ChannelProxy(Channel delegate, Class<?> messageClass)
/*     */   {
/*  46 */     this.delegate = delegate;
/*  47 */     this.messageClass = messageClass;
/*     */   }
/*     */ 
/*     */   protected abstract <T> Callable<T> onMessageScheduled(Callable<T> paramCallable, FieldAccessor paramFieldAccessor);
/*     */ 
/*     */   protected abstract Runnable onMessageScheduled(Runnable paramRunnable, FieldAccessor paramFieldAccessor);
/*     */ 
/*     */   public <T> Attribute<T> attr(AttributeKey<T> paramAttributeKey)
/*     */   {
/*  68 */     return this.delegate.attr(paramAttributeKey);
/*     */   }
/*     */ 
/*     */   public ChannelFuture bind(SocketAddress paramSocketAddress)
/*     */   {
/*  73 */     return this.delegate.bind(paramSocketAddress);
/*     */   }
/*     */ 
/*     */   public ChannelPipeline pipeline()
/*     */   {
/*  78 */     return this.delegate.pipeline();
/*     */   }
/*     */ 
/*     */   public ChannelFuture connect(SocketAddress paramSocketAddress)
/*     */   {
/*  83 */     return this.delegate.connect(paramSocketAddress);
/*     */   }
/*     */ 
/*     */   public ByteBufAllocator alloc()
/*     */   {
/*  88 */     return this.delegate.alloc();
/*     */   }
/*     */ 
/*     */   public ChannelPromise newPromise()
/*     */   {
/*  93 */     return this.delegate.newPromise();
/*     */   }
/*     */ 
/*     */   public EventLoop eventLoop()
/*     */   {
/*  98 */     if (this.loopProxy == null) {
/*  99 */       this.loopProxy = new EventLoopProxy()
/*     */       {
/*     */         protected EventLoop getDelegate() {
/* 102 */           return ChannelProxy.this.delegate.eventLoop();
/*     */         }
/*     */ 
/*     */         protected Runnable schedulingRunnable(Runnable runnable)
/*     */         {
/* 107 */           FieldAccessor accessor = ChannelProxy.this.getMessageAccessor(runnable);
/*     */ 
/* 109 */           if (accessor != null) {
/* 110 */             Runnable result = ChannelProxy.this.onMessageScheduled(runnable, accessor);
/* 111 */             return result != null ? result : getEmptyRunnable();
/*     */           }
/* 113 */           return runnable;
/*     */         }
/*     */ 
/*     */         protected <T> Callable<T> schedulingCallable(Callable<T> callable)
/*     */         {
/* 118 */           FieldAccessor accessor = ChannelProxy.this.getMessageAccessor(callable);
/*     */ 
/* 120 */           if (accessor != null) {
/* 121 */             Callable result = ChannelProxy.this.onMessageScheduled(callable, accessor);
/* 122 */             return result != null ? result : EventLoopProxy.getEmptyCallable();
/*     */           }
/* 124 */           return callable;
/*     */         }
/*     */       };
/*     */     }
/* 128 */     return this.loopProxy;
/*     */   }
/*     */ 
/*     */   private FieldAccessor getMessageAccessor(Object value)
/*     */   {
/* 137 */     Class clazz = value.getClass();
/* 138 */     FieldAccessor accessor = (FieldAccessor)MESSAGE_LOOKUP.get(clazz);
/*     */ 
/* 140 */     if (accessor == null) {
/*     */       try {
/* 142 */         accessor = Accessors.getFieldAccessor(clazz, this.messageClass, true);
/*     */       } catch (IllegalArgumentException e) {
/* 144 */         accessor = MARK_NO_MESSAGE;
/*     */       }
/*     */ 
/* 147 */       MESSAGE_LOOKUP.put(clazz, accessor);
/*     */     }
/* 149 */     return accessor != MARK_NO_MESSAGE ? accessor : null;
/*     */   }
/*     */ 
/*     */   public ChannelFuture connect(SocketAddress paramSocketAddress1, SocketAddress paramSocketAddress2)
/*     */   {
/* 155 */     return this.delegate.connect(paramSocketAddress1, paramSocketAddress2);
/*     */   }
/*     */ 
/*     */   public ChannelProgressivePromise newProgressivePromise()
/*     */   {
/* 160 */     return this.delegate.newProgressivePromise();
/*     */   }
/*     */ 
/*     */   public Channel parent()
/*     */   {
/* 165 */     return this.delegate.parent();
/*     */   }
/*     */ 
/*     */   public ChannelConfig config()
/*     */   {
/* 170 */     return this.delegate.config();
/*     */   }
/*     */ 
/*     */   public ChannelFuture newSucceededFuture()
/*     */   {
/* 175 */     return this.delegate.newSucceededFuture();
/*     */   }
/*     */ 
/*     */   public boolean isOpen()
/*     */   {
/* 180 */     return this.delegate.isOpen();
/*     */   }
/*     */ 
/*     */   public ChannelFuture disconnect()
/*     */   {
/* 185 */     return this.delegate.disconnect();
/*     */   }
/*     */ 
/*     */   public boolean isRegistered()
/*     */   {
/* 190 */     return this.delegate.isRegistered();
/*     */   }
/*     */ 
/*     */   public ChannelFuture newFailedFuture(Throwable paramThrowable)
/*     */   {
/* 195 */     return this.delegate.newFailedFuture(paramThrowable);
/*     */   }
/*     */ 
/*     */   public ChannelFuture close()
/*     */   {
/* 200 */     return this.delegate.close();
/*     */   }
/*     */ 
/*     */   public boolean isActive()
/*     */   {
/* 205 */     return this.delegate.isActive();
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public ChannelFuture deregister()
/*     */   {
/* 211 */     return this.delegate.deregister();
/*     */   }
/*     */ 
/*     */   public ChannelPromise voidPromise()
/*     */   {
/* 216 */     return this.delegate.voidPromise();
/*     */   }
/*     */ 
/*     */   public ChannelMetadata metadata()
/*     */   {
/* 221 */     return this.delegate.metadata();
/*     */   }
/*     */ 
/*     */   public ChannelFuture bind(SocketAddress paramSocketAddress, ChannelPromise paramChannelPromise)
/*     */   {
/* 227 */     return this.delegate.bind(paramSocketAddress, paramChannelPromise);
/*     */   }
/*     */ 
/*     */   public SocketAddress localAddress()
/*     */   {
/* 232 */     return this.delegate.localAddress();
/*     */   }
/*     */ 
/*     */   public SocketAddress remoteAddress()
/*     */   {
/* 237 */     return this.delegate.remoteAddress();
/*     */   }
/*     */ 
/*     */   public ChannelFuture connect(SocketAddress paramSocketAddress, ChannelPromise paramChannelPromise)
/*     */   {
/* 243 */     return this.delegate.connect(paramSocketAddress, paramChannelPromise);
/*     */   }
/*     */ 
/*     */   public ChannelFuture closeFuture()
/*     */   {
/* 248 */     return this.delegate.closeFuture();
/*     */   }
/*     */ 
/*     */   public boolean isWritable()
/*     */   {
/* 253 */     return this.delegate.isWritable();
/*     */   }
/*     */ 
/*     */   public Channel flush()
/*     */   {
/* 258 */     return this.delegate.flush();
/*     */   }
/*     */ 
/*     */   public ChannelFuture connect(SocketAddress paramSocketAddress1, SocketAddress paramSocketAddress2, ChannelPromise paramChannelPromise)
/*     */   {
/* 264 */     return this.delegate.connect(paramSocketAddress1, paramSocketAddress2, paramChannelPromise);
/*     */   }
/*     */ 
/*     */   public Channel read()
/*     */   {
/* 269 */     return this.delegate.read();
/*     */   }
/*     */ 
/*     */   public Channel.Unsafe unsafe()
/*     */   {
/* 274 */     return this.delegate.unsafe();
/*     */   }
/*     */ 
/*     */   public ChannelFuture disconnect(ChannelPromise paramChannelPromise)
/*     */   {
/* 279 */     return this.delegate.disconnect(paramChannelPromise);
/*     */   }
/*     */ 
/*     */   public ChannelFuture close(ChannelPromise paramChannelPromise)
/*     */   {
/* 284 */     return this.delegate.close(paramChannelPromise);
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public ChannelFuture deregister(ChannelPromise paramChannelPromise)
/*     */   {
/* 290 */     return this.delegate.deregister(paramChannelPromise);
/*     */   }
/*     */ 
/*     */   public ChannelFuture write(Object paramObject)
/*     */   {
/* 295 */     return this.delegate.write(paramObject);
/*     */   }
/*     */ 
/*     */   public ChannelFuture write(Object paramObject, ChannelPromise paramChannelPromise)
/*     */   {
/* 300 */     return this.delegate.write(paramObject, paramChannelPromise);
/*     */   }
/*     */ 
/*     */   public ChannelFuture writeAndFlush(Object paramObject, ChannelPromise paramChannelPromise)
/*     */   {
/* 305 */     return this.delegate.writeAndFlush(paramObject, paramChannelPromise);
/*     */   }
/*     */ 
/*     */   public ChannelFuture writeAndFlush(Object paramObject)
/*     */   {
/* 310 */     return this.delegate.writeAndFlush(paramObject);
/*     */   }
/*     */ 
/*     */   public int compareTo(Channel o)
/*     */   {
/* 315 */     return this.delegate.compareTo(o);
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.injector.netty.ChannelProxy
 * JD-Core Version:    0.6.2
 */