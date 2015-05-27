/*     */ package com.comphenix.protocol.injector.netty;
/*     */ 
/*     */ import io.netty.channel.Channel;
/*     */ import io.netty.channel.ChannelFuture;
/*     */ import io.netty.channel.ChannelHandler;
/*     */ import io.netty.channel.ChannelHandlerContext;
/*     */ import io.netty.channel.ChannelPipeline;
/*     */ import io.netty.channel.ChannelPromise;
/*     */ import io.netty.util.concurrent.EventExecutorGroup;
/*     */ import java.net.SocketAddress;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ 
/*     */ public class PipelineProxy
/*     */   implements ChannelPipeline
/*     */ {
/*     */   protected final ChannelPipeline pipeline;
/*     */   protected final Channel channel;
/*     */ 
/*     */   public PipelineProxy(ChannelPipeline pipeline, Channel channel)
/*     */   {
/*  26 */     this.pipeline = pipeline;
/*  27 */     this.channel = channel;
/*     */   }
/*     */ 
/*     */   public ChannelPipeline addAfter(EventExecutorGroup arg0, String arg1, String arg2, ChannelHandler arg3)
/*     */   {
/*  32 */     this.pipeline.addAfter(arg0, arg1, arg2, arg3);
/*  33 */     return this;
/*     */   }
/*     */ 
/*     */   public ChannelPipeline addAfter(String arg0, String arg1, ChannelHandler arg2)
/*     */   {
/*  38 */     this.pipeline.addAfter(arg0, arg1, arg2);
/*  39 */     return this;
/*     */   }
/*     */ 
/*     */   public ChannelPipeline addBefore(EventExecutorGroup arg0, String arg1, String arg2, ChannelHandler arg3)
/*     */   {
/*  44 */     this.pipeline.addBefore(arg0, arg1, arg2, arg3);
/*  45 */     return this;
/*     */   }
/*     */ 
/*     */   public ChannelPipeline addBefore(String arg0, String arg1, ChannelHandler arg2)
/*     */   {
/*  50 */     this.pipeline.addBefore(arg0, arg1, arg2);
/*  51 */     return this;
/*     */   }
/*     */ 
/*     */   public ChannelPipeline addFirst(ChannelHandler[] arg0)
/*     */   {
/*  56 */     this.pipeline.addFirst(arg0);
/*  57 */     return this;
/*     */   }
/*     */ 
/*     */   public ChannelPipeline addFirst(EventExecutorGroup arg0, ChannelHandler[] arg1)
/*     */   {
/*  62 */     this.pipeline.addFirst(arg0, arg1);
/*  63 */     return this;
/*     */   }
/*     */ 
/*     */   public ChannelPipeline addFirst(EventExecutorGroup arg0, String arg1, ChannelHandler arg2)
/*     */   {
/*  68 */     this.pipeline.addFirst(arg0, arg1, arg2);
/*  69 */     return this;
/*     */   }
/*     */ 
/*     */   public ChannelPipeline addFirst(String arg0, ChannelHandler arg1)
/*     */   {
/*  74 */     this.pipeline.addFirst(arg0, arg1);
/*  75 */     return this;
/*     */   }
/*     */ 
/*     */   public ChannelPipeline addLast(ChannelHandler[] arg0)
/*     */   {
/*  80 */     this.pipeline.addLast(arg0);
/*  81 */     return this;
/*     */   }
/*     */ 
/*     */   public ChannelPipeline addLast(EventExecutorGroup arg0, ChannelHandler[] arg1)
/*     */   {
/*  86 */     this.pipeline.addLast(arg0, arg1);
/*  87 */     return this;
/*     */   }
/*     */ 
/*     */   public ChannelPipeline addLast(EventExecutorGroup arg0, String arg1, ChannelHandler arg2)
/*     */   {
/*  92 */     this.pipeline.addLast(arg0, arg1, arg2);
/*  93 */     return this;
/*     */   }
/*     */ 
/*     */   public ChannelPipeline addLast(String arg0, ChannelHandler arg1)
/*     */   {
/*  98 */     this.pipeline.addLast(arg0, arg1);
/*  99 */     return this;
/*     */   }
/*     */ 
/*     */   public ChannelFuture bind(SocketAddress arg0, ChannelPromise arg1)
/*     */   {
/* 104 */     return this.pipeline.bind(arg0, arg1);
/*     */   }
/*     */ 
/*     */   public ChannelFuture bind(SocketAddress arg0)
/*     */   {
/* 109 */     return this.pipeline.bind(arg0);
/*     */   }
/*     */ 
/*     */   public Channel channel()
/*     */   {
/* 114 */     return this.channel;
/*     */   }
/*     */ 
/*     */   public ChannelFuture close()
/*     */   {
/* 119 */     return this.pipeline.close();
/*     */   }
/*     */ 
/*     */   public ChannelFuture close(ChannelPromise arg0)
/*     */   {
/* 124 */     return this.pipeline.close(arg0);
/*     */   }
/*     */ 
/*     */   public ChannelFuture connect(SocketAddress arg0, ChannelPromise arg1)
/*     */   {
/* 129 */     return this.pipeline.connect(arg0, arg1);
/*     */   }
/*     */ 
/*     */   public ChannelFuture connect(SocketAddress arg0, SocketAddress arg1, ChannelPromise arg2)
/*     */   {
/* 134 */     return this.pipeline.connect(arg0, arg1, arg2);
/*     */   }
/*     */ 
/*     */   public ChannelFuture connect(SocketAddress arg0, SocketAddress arg1)
/*     */   {
/* 139 */     return this.pipeline.connect(arg0, arg1);
/*     */   }
/*     */ 
/*     */   public ChannelFuture connect(SocketAddress arg0)
/*     */   {
/* 144 */     return this.pipeline.connect(arg0);
/*     */   }
/*     */ 
/*     */   public ChannelHandlerContext context(ChannelHandler arg0)
/*     */   {
/* 149 */     return this.pipeline.context(arg0);
/*     */   }
/*     */ 
/*     */   public ChannelHandlerContext context(Class<? extends ChannelHandler> arg0)
/*     */   {
/* 154 */     return this.pipeline.context(arg0);
/*     */   }
/*     */ 
/*     */   public ChannelHandlerContext context(String arg0)
/*     */   {
/* 159 */     return this.pipeline.context(arg0);
/*     */   }
/*     */ 
/*     */   public ChannelFuture deregister()
/*     */   {
/* 165 */     return this.pipeline.deregister();
/*     */   }
/*     */ 
/*     */   public ChannelFuture deregister(ChannelPromise arg0)
/*     */   {
/* 170 */     return this.pipeline.deregister(arg0);
/*     */   }
/*     */ 
/*     */   public ChannelPipeline fireChannelUnregistered()
/*     */   {
/* 175 */     this.pipeline.fireChannelUnregistered();
/* 176 */     return this;
/*     */   }
/*     */ 
/*     */   public ChannelFuture disconnect()
/*     */   {
/* 181 */     return this.pipeline.disconnect();
/*     */   }
/*     */ 
/*     */   public ChannelFuture disconnect(ChannelPromise arg0)
/*     */   {
/* 186 */     return this.pipeline.disconnect(arg0);
/*     */   }
/*     */ 
/*     */   public ChannelPipeline fireChannelActive()
/*     */   {
/* 191 */     this.pipeline.fireChannelActive();
/* 192 */     return this;
/*     */   }
/*     */ 
/*     */   public ChannelPipeline fireChannelInactive()
/*     */   {
/* 197 */     this.pipeline.fireChannelInactive();
/* 198 */     return this;
/*     */   }
/*     */ 
/*     */   public ChannelPipeline fireChannelRead(Object arg0)
/*     */   {
/* 203 */     this.pipeline.fireChannelRead(arg0);
/* 204 */     return this;
/*     */   }
/*     */ 
/*     */   public ChannelPipeline fireChannelReadComplete()
/*     */   {
/* 209 */     this.pipeline.fireChannelReadComplete();
/* 210 */     return this;
/*     */   }
/*     */ 
/*     */   public ChannelPipeline fireChannelRegistered()
/*     */   {
/* 215 */     this.pipeline.fireChannelRegistered();
/* 216 */     return this;
/*     */   }
/*     */ 
/*     */   public ChannelPipeline fireChannelWritabilityChanged()
/*     */   {
/* 221 */     this.pipeline.fireChannelWritabilityChanged();
/* 222 */     return this;
/*     */   }
/*     */ 
/*     */   public ChannelPipeline fireExceptionCaught(Throwable arg0)
/*     */   {
/* 227 */     this.pipeline.fireExceptionCaught(arg0);
/* 228 */     return this;
/*     */   }
/*     */ 
/*     */   public ChannelPipeline fireUserEventTriggered(Object arg0)
/*     */   {
/* 233 */     this.pipeline.fireUserEventTriggered(arg0);
/* 234 */     return this;
/*     */   }
/*     */ 
/*     */   public ChannelHandler first()
/*     */   {
/* 239 */     return this.pipeline.first();
/*     */   }
/*     */ 
/*     */   public ChannelHandlerContext firstContext()
/*     */   {
/* 244 */     return this.pipeline.firstContext();
/*     */   }
/*     */ 
/*     */   public ChannelPipeline flush()
/*     */   {
/* 249 */     this.pipeline.flush();
/* 250 */     return this;
/*     */   }
/*     */ 
/*     */   public <T extends ChannelHandler> T get(Class<T> arg0)
/*     */   {
/* 255 */     return this.pipeline.get(arg0);
/*     */   }
/*     */ 
/*     */   public ChannelHandler get(String arg0)
/*     */   {
/* 260 */     return this.pipeline.get(arg0);
/*     */   }
/*     */ 
/*     */   public Iterator<Map.Entry<String, ChannelHandler>> iterator()
/*     */   {
/* 265 */     return this.pipeline.iterator();
/*     */   }
/*     */ 
/*     */   public ChannelHandler last()
/*     */   {
/* 270 */     return this.pipeline.last();
/*     */   }
/*     */ 
/*     */   public ChannelHandlerContext lastContext()
/*     */   {
/* 275 */     return this.pipeline.lastContext();
/*     */   }
/*     */ 
/*     */   public List<String> names()
/*     */   {
/* 280 */     return this.pipeline.names();
/*     */   }
/*     */ 
/*     */   public ChannelPipeline read()
/*     */   {
/* 285 */     this.pipeline.read();
/* 286 */     return this;
/*     */   }
/*     */ 
/*     */   public ChannelPipeline remove(ChannelHandler arg0)
/*     */   {
/* 291 */     this.pipeline.remove(arg0);
/* 292 */     return this;
/*     */   }
/*     */ 
/*     */   public <T extends ChannelHandler> T remove(Class<T> arg0)
/*     */   {
/* 297 */     return this.pipeline.remove(arg0);
/*     */   }
/*     */ 
/*     */   public ChannelHandler remove(String arg0)
/*     */   {
/* 302 */     return this.pipeline.remove(arg0);
/*     */   }
/*     */ 
/*     */   public ChannelHandler removeFirst()
/*     */   {
/* 307 */     return this.pipeline.removeFirst();
/*     */   }
/*     */ 
/*     */   public ChannelHandler removeLast()
/*     */   {
/* 312 */     return this.pipeline.removeLast();
/*     */   }
/*     */ 
/*     */   public ChannelPipeline replace(ChannelHandler arg0, String arg1, ChannelHandler arg2)
/*     */   {
/* 317 */     this.pipeline.replace(arg0, arg1, arg2);
/* 318 */     return this;
/*     */   }
/*     */ 
/*     */   public <T extends ChannelHandler> T replace(Class<T> arg0, String arg1, ChannelHandler arg2)
/*     */   {
/* 323 */     return this.pipeline.replace(arg0, arg1, arg2);
/*     */   }
/*     */ 
/*     */   public ChannelHandler replace(String arg0, String arg1, ChannelHandler arg2)
/*     */   {
/* 328 */     return this.pipeline.replace(arg0, arg1, arg2);
/*     */   }
/*     */ 
/*     */   public Map<String, ChannelHandler> toMap()
/*     */   {
/* 333 */     return this.pipeline.toMap();
/*     */   }
/*     */ 
/*     */   public ChannelFuture write(Object arg0, ChannelPromise arg1)
/*     */   {
/* 338 */     return this.pipeline.write(arg0, arg1);
/*     */   }
/*     */ 
/*     */   public ChannelFuture write(Object arg0)
/*     */   {
/* 343 */     return this.pipeline.write(arg0);
/*     */   }
/*     */ 
/*     */   public ChannelFuture writeAndFlush(Object arg0, ChannelPromise arg1)
/*     */   {
/* 348 */     return this.pipeline.writeAndFlush(arg0, arg1);
/*     */   }
/*     */ 
/*     */   public ChannelFuture writeAndFlush(Object arg0)
/*     */   {
/* 353 */     return this.pipeline.writeAndFlush(arg0);
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.injector.netty.PipelineProxy
 * JD-Core Version:    0.6.2
 */