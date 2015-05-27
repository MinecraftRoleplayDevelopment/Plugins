/*     */ package com.comphenix.protocol.injector.netty;
/*     */ 
/*     */ import io.netty.channel.ChannelFuture;
/*     */ import io.netty.channel.ChannelOption;
/*     */ import io.netty.channel.socket.SocketChannelConfig;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.net.InetAddress;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.net.Socket;
/*     */ import java.net.SocketAddress;
/*     */ import java.net.SocketException;
/*     */ 
/*     */ class NettySocketAdaptor extends Socket
/*     */ {
/*     */   private final io.netty.channel.socket.SocketChannel ch;
/*     */ 
/*     */   private NettySocketAdaptor(io.netty.channel.socket.SocketChannel ch)
/*     */   {
/*  27 */     this.ch = ch;
/*     */   }
/*     */ 
/*     */   public static NettySocketAdaptor adapt(io.netty.channel.socket.SocketChannel ch) {
/*  31 */     return new NettySocketAdaptor(ch);
/*     */   }
/*     */ 
/*     */   public void bind(SocketAddress bindpoint) throws IOException
/*     */   {
/*  36 */     this.ch.bind(bindpoint).syncUninterruptibly();
/*     */   }
/*     */ 
/*     */   public synchronized void close() throws IOException
/*     */   {
/*  41 */     this.ch.close().syncUninterruptibly();
/*     */   }
/*     */ 
/*     */   public void connect(SocketAddress endpoint) throws IOException
/*     */   {
/*  46 */     this.ch.connect(endpoint).syncUninterruptibly();
/*     */   }
/*     */ 
/*     */   public void connect(SocketAddress endpoint, int timeout) throws IOException
/*     */   {
/*  51 */     this.ch.config().setConnectTimeoutMillis(timeout);
/*  52 */     this.ch.connect(endpoint).syncUninterruptibly();
/*     */   }
/*     */ 
/*     */   public boolean equals(Object obj)
/*     */   {
/*  57 */     return ((obj instanceof NettySocketAdaptor)) && (this.ch.equals(((NettySocketAdaptor)obj).ch));
/*     */   }
/*     */ 
/*     */   public java.nio.channels.SocketChannel getChannel()
/*     */   {
/*  62 */     throw new UnsupportedOperationException("Operation not supported on Channel wrapper.");
/*     */   }
/*     */ 
/*     */   public InetAddress getInetAddress()
/*     */   {
/*  67 */     return this.ch.remoteAddress().getAddress();
/*     */   }
/*     */ 
/*     */   public InputStream getInputStream() throws IOException
/*     */   {
/*  72 */     throw new UnsupportedOperationException("Operation not supported on Channel wrapper.");
/*     */   }
/*     */ 
/*     */   public boolean getKeepAlive() throws SocketException
/*     */   {
/*  77 */     return ((Boolean)this.ch.config().getOption(ChannelOption.SO_KEEPALIVE)).booleanValue();
/*     */   }
/*     */ 
/*     */   public InetAddress getLocalAddress()
/*     */   {
/*  82 */     return this.ch.localAddress().getAddress();
/*     */   }
/*     */ 
/*     */   public int getLocalPort()
/*     */   {
/*  87 */     return this.ch.localAddress().getPort();
/*     */   }
/*     */ 
/*     */   public SocketAddress getLocalSocketAddress()
/*     */   {
/*  92 */     return this.ch.localAddress();
/*     */   }
/*     */ 
/*     */   public boolean getOOBInline() throws SocketException
/*     */   {
/*  97 */     throw new UnsupportedOperationException("Operation not supported on Channel wrapper.");
/*     */   }
/*     */ 
/*     */   public OutputStream getOutputStream() throws IOException
/*     */   {
/* 102 */     throw new UnsupportedOperationException("Operation not supported on Channel wrapper.");
/*     */   }
/*     */ 
/*     */   public int getPort()
/*     */   {
/* 107 */     return this.ch.remoteAddress().getPort();
/*     */   }
/*     */ 
/*     */   public synchronized int getReceiveBufferSize() throws SocketException
/*     */   {
/* 112 */     return ((Integer)this.ch.config().getOption(ChannelOption.SO_RCVBUF)).intValue();
/*     */   }
/*     */ 
/*     */   public SocketAddress getRemoteSocketAddress()
/*     */   {
/* 117 */     return this.ch.remoteAddress();
/*     */   }
/*     */ 
/*     */   public boolean getReuseAddress() throws SocketException
/*     */   {
/* 122 */     return ((Boolean)this.ch.config().getOption(ChannelOption.SO_REUSEADDR)).booleanValue();
/*     */   }
/*     */ 
/*     */   public synchronized int getSendBufferSize() throws SocketException
/*     */   {
/* 127 */     return ((Integer)this.ch.config().getOption(ChannelOption.SO_SNDBUF)).intValue();
/*     */   }
/*     */ 
/*     */   public int getSoLinger() throws SocketException
/*     */   {
/* 132 */     return ((Integer)this.ch.config().getOption(ChannelOption.SO_LINGER)).intValue();
/*     */   }
/*     */ 
/*     */   public synchronized int getSoTimeout() throws SocketException
/*     */   {
/* 137 */     throw new UnsupportedOperationException("Operation not supported on Channel wrapper.");
/*     */   }
/*     */ 
/*     */   public boolean getTcpNoDelay() throws SocketException
/*     */   {
/* 142 */     return ((Boolean)this.ch.config().getOption(ChannelOption.TCP_NODELAY)).booleanValue();
/*     */   }
/*     */ 
/*     */   public int getTrafficClass() throws SocketException
/*     */   {
/* 147 */     return ((Integer)this.ch.config().getOption(ChannelOption.IP_TOS)).intValue();
/*     */   }
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 152 */     return this.ch.hashCode();
/*     */   }
/*     */ 
/*     */   public boolean isBound()
/*     */   {
/* 157 */     return this.ch.localAddress() != null;
/*     */   }
/*     */ 
/*     */   public boolean isClosed()
/*     */   {
/* 162 */     return !this.ch.isOpen();
/*     */   }
/*     */ 
/*     */   public boolean isConnected()
/*     */   {
/* 167 */     return this.ch.isActive();
/*     */   }
/*     */ 
/*     */   public boolean isInputShutdown()
/*     */   {
/* 172 */     return this.ch.isInputShutdown();
/*     */   }
/*     */ 
/*     */   public boolean isOutputShutdown()
/*     */   {
/* 177 */     return this.ch.isOutputShutdown();
/*     */   }
/*     */ 
/*     */   public void sendUrgentData(int data) throws IOException
/*     */   {
/* 182 */     throw new UnsupportedOperationException("Operation not supported on Channel wrapper.");
/*     */   }
/*     */ 
/*     */   public void setKeepAlive(boolean on) throws SocketException
/*     */   {
/* 187 */     this.ch.config().setOption(ChannelOption.SO_KEEPALIVE, Boolean.valueOf(on));
/*     */   }
/*     */ 
/*     */   public void setOOBInline(boolean on) throws SocketException
/*     */   {
/* 192 */     throw new UnsupportedOperationException("Operation not supported on Channel wrapper.");
/*     */   }
/*     */ 
/*     */   public void setPerformancePreferences(int connectionTime, int latency, int bandwidth)
/*     */   {
/* 197 */     throw new UnsupportedOperationException("Operation not supported on Channel wrapper.");
/*     */   }
/*     */ 
/*     */   public synchronized void setReceiveBufferSize(int size) throws SocketException
/*     */   {
/* 202 */     this.ch.config().setOption(ChannelOption.SO_RCVBUF, Integer.valueOf(size));
/*     */   }
/*     */ 
/*     */   public void setReuseAddress(boolean on) throws SocketException
/*     */   {
/* 207 */     this.ch.config().setOption(ChannelOption.SO_REUSEADDR, Boolean.valueOf(on));
/*     */   }
/*     */ 
/*     */   public synchronized void setSendBufferSize(int size) throws SocketException
/*     */   {
/* 212 */     this.ch.config().setOption(ChannelOption.SO_SNDBUF, Integer.valueOf(size));
/*     */   }
/*     */ 
/*     */   public void setSoLinger(boolean on, int linger) throws SocketException
/*     */   {
/* 217 */     this.ch.config().setOption(ChannelOption.SO_LINGER, Integer.valueOf(linger));
/*     */   }
/*     */ 
/*     */   public synchronized void setSoTimeout(int timeout) throws SocketException
/*     */   {
/* 222 */     throw new UnsupportedOperationException("Operation not supported on Channel wrapper.");
/*     */   }
/*     */ 
/*     */   public void setTcpNoDelay(boolean on) throws SocketException
/*     */   {
/* 227 */     this.ch.config().setOption(ChannelOption.TCP_NODELAY, Boolean.valueOf(on));
/*     */   }
/*     */ 
/*     */   public void setTrafficClass(int tc) throws SocketException
/*     */   {
/* 232 */     this.ch.config().setOption(ChannelOption.IP_TOS, Integer.valueOf(tc));
/*     */   }
/*     */ 
/*     */   public void shutdownInput() throws IOException
/*     */   {
/* 237 */     throw new UnsupportedOperationException("Operation not supported on Channel wrapper.");
/*     */   }
/*     */ 
/*     */   public void shutdownOutput() throws IOException
/*     */   {
/* 242 */     this.ch.shutdownOutput().syncUninterruptibly();
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 247 */     return this.ch.toString();
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.injector.netty.NettySocketAdaptor
 * JD-Core Version:    0.6.2
 */