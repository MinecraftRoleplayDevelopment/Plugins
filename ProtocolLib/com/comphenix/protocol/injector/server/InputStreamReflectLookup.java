/*     */ package com.comphenix.protocol.injector.server;
/*     */ 
/*     */ import com.comphenix.protocol.concurrency.BlockingHashMap;
/*     */ import com.comphenix.protocol.error.ErrorReporter;
/*     */ import com.comphenix.protocol.reflect.FieldAccessException;
/*     */ import com.comphenix.protocol.reflect.FieldUtils;
/*     */ import com.comphenix.protocol.reflect.FuzzyReflection;
/*     */ import com.google.common.collect.MapMaker;
/*     */ import java.io.FilterInputStream;
/*     */ import java.io.InputStream;
/*     */ import java.lang.reflect.Field;
/*     */ import java.net.Socket;
/*     */ import java.net.SocketAddress;
/*     */ import java.util.concurrent.ConcurrentMap;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import org.bukkit.Server;
/*     */ 
/*     */ class InputStreamReflectLookup extends AbstractInputStreamLookup
/*     */ {
/*     */   private static Field filteredInputField;
/*     */   private static final long DEFAULT_TIMEOUT = 2000L;
/*  28 */   protected BlockingHashMap<SocketAddress, SocketInjector> addressLookup = new BlockingHashMap();
/*  29 */   protected ConcurrentMap<InputStream, SocketAddress> inputLookup = new MapMaker().weakValues().makeMap();
/*     */   private final long injectorTimeout;
/*     */ 
/*     */   public InputStreamReflectLookup(ErrorReporter reporter, Server server)
/*     */   {
/*  35 */     this(reporter, server, 2000L);
/*     */   }
/*     */ 
/*     */   public InputStreamReflectLookup(ErrorReporter reporter, Server server, long injectorTimeout)
/*     */   {
/*  47 */     super(reporter, server);
/*  48 */     this.injectorTimeout = injectorTimeout;
/*     */   }
/*     */ 
/*     */   public void inject(Object container)
/*     */   {
/*     */   }
/*     */ 
/*     */   public SocketInjector peekSocketInjector(SocketAddress address)
/*     */   {
/*     */     try
/*     */     {
/*  59 */       return (SocketInjector)this.addressLookup.get(address, 0L, TimeUnit.MILLISECONDS);
/*     */     } catch (InterruptedException e) {
/*     */     }
/*  62 */     return null;
/*     */   }
/*     */ 
/*     */   public SocketInjector waitSocketInjector(SocketAddress address)
/*     */   {
/*     */     try
/*     */     {
/*  74 */       return (SocketInjector)this.addressLookup.get(address, this.injectorTimeout, TimeUnit.MILLISECONDS, true);
/*     */     }
/*     */     catch (InterruptedException e) {
/*  77 */       throw new IllegalStateException("Impossible exception occured!", e);
/*     */     }
/*     */   }
/*     */ 
/*     */   public SocketInjector waitSocketInjector(Socket socket)
/*     */   {
/*  83 */     return waitSocketInjector(socket.getRemoteSocketAddress());
/*     */   }
/*     */ 
/*     */   public SocketInjector waitSocketInjector(InputStream input)
/*     */   {
/*     */     try {
/*  89 */       SocketAddress address = waitSocketAddress(input);
/*     */ 
/*  92 */       if (address != null) {
/*  93 */         return waitSocketInjector(address);
/*     */       }
/*  95 */       return null;
/*     */     } catch (IllegalAccessException e) {
/*  97 */       throw new FieldAccessException("Cannot find or access socket field for " + input, e);
/*     */     }
/*     */   }
/*     */ 
/*     */   private SocketAddress waitSocketAddress(InputStream stream)
/*     */     throws IllegalAccessException
/*     */   {
/* 109 */     if ((stream instanceof FilterInputStream)) {
/* 110 */       return waitSocketAddress(getInputStream((FilterInputStream)stream));
/*     */     }
/* 112 */     SocketAddress result = (SocketAddress)this.inputLookup.get(stream);
/*     */ 
/* 114 */     if (result == null) {
/* 115 */       Socket socket = lookupSocket(stream);
/*     */ 
/* 118 */       result = socket.getRemoteSocketAddress();
/* 119 */       this.inputLookup.put(stream, result);
/*     */     }
/* 121 */     return result;
/*     */   }
/*     */ 
/*     */   protected static InputStream getInputStream(FilterInputStream filtered)
/*     */   {
/* 131 */     if (filteredInputField == null) {
/* 132 */       filteredInputField = FuzzyReflection.fromClass(FilterInputStream.class, true).getFieldByType("in", InputStream.class);
/*     */     }
/*     */ 
/* 135 */     InputStream current = filtered;
/*     */     try
/*     */     {
/* 139 */       while ((current instanceof FilterInputStream)) {
/* 140 */         current = (InputStream)FieldUtils.readField(filteredInputField, current, true);
/*     */       }
/* 142 */       return current;
/*     */     } catch (IllegalAccessException e) {
/* 144 */       throw new FieldAccessException("Cannot access filtered input field.", e);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setSocketInjector(SocketAddress address, SocketInjector injector)
/*     */   {
/* 150 */     if (address == null)
/* 151 */       throw new IllegalArgumentException("address cannot be NULL");
/* 152 */     if (injector == null) {
/* 153 */       throw new IllegalArgumentException("injector cannot be NULL.");
/*     */     }
/* 155 */     SocketInjector previous = (SocketInjector)this.addressLookup.put(address, injector);
/*     */ 
/* 158 */     if (previous != null)
/*     */     {
/* 160 */       onPreviousSocketOverwritten(previous, injector);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void cleanupAll()
/*     */   {
/*     */   }
/*     */ 
/*     */   private static Socket lookupSocket(InputStream stream)
/*     */     throws IllegalAccessException
/*     */   {
/* 176 */     if ((stream instanceof FilterInputStream)) {
/* 177 */       return lookupSocket(getInputStream((FilterInputStream)stream));
/*     */     }
/*     */ 
/* 180 */     Field socketField = FuzzyReflection.fromObject(stream, true).getFieldByType("socket", Socket.class);
/*     */ 
/* 183 */     return (Socket)FieldUtils.readField(socketField, stream, true);
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.injector.server.InputStreamReflectLookup
 * JD-Core Version:    0.6.2
 */