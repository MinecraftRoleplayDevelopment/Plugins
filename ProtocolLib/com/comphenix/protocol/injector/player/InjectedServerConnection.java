/*     */ package com.comphenix.protocol.injector.player;
/*     */ 
/*     */ import com.comphenix.net.sf.cglib.proxy.Factory;
/*     */ import com.comphenix.protocol.error.ErrorReporter;
/*     */ import com.comphenix.protocol.error.Report;
/*     */ import com.comphenix.protocol.error.Report.ReportBuilder;
/*     */ import com.comphenix.protocol.error.ReportType;
/*     */ import com.comphenix.protocol.injector.server.AbstractInputStreamLookup;
/*     */ import com.comphenix.protocol.reflect.FieldAccessException;
/*     */ import com.comphenix.protocol.reflect.FieldUtils;
/*     */ import com.comphenix.protocol.reflect.FuzzyReflection;
/*     */ import com.comphenix.protocol.reflect.ObjectWriter;
/*     */ import com.comphenix.protocol.reflect.VolatileField;
/*     */ import com.comphenix.protocol.utility.MinecraftReflection;
/*     */ import java.lang.reflect.Field;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.lang.reflect.Method;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import org.bukkit.Server;
/*     */ 
/*     */ public class InjectedServerConnection
/*     */ {
/*  48 */   public static final ReportType REPORT_CANNOT_FIND_MINECRAFT_SERVER = new ReportType("Cannot extract minecraft server from Bukkit.");
/*  49 */   public static final ReportType REPORT_CANNOT_INJECT_SERVER_CONNECTION = new ReportType("Cannot inject into server connection. Bad things will happen.");
/*     */ 
/*  51 */   public static final ReportType REPORT_CANNOT_FIND_LISTENER_THREAD = new ReportType("Cannot find listener thread in MinecraftServer.");
/*  52 */   public static final ReportType REPORT_CANNOT_READ_LISTENER_THREAD = new ReportType("Unable to read the listener thread.");
/*     */ 
/*  54 */   public static final ReportType REPORT_CANNOT_FIND_SERVER_CONNECTION = new ReportType("Unable to retrieve server connection");
/*  55 */   public static final ReportType REPORT_UNEXPECTED_THREAD_COUNT = new ReportType("Unexpected number of threads in %s: %s");
/*  56 */   public static final ReportType REPORT_CANNOT_FIND_NET_HANDLER_THREAD = new ReportType("Unable to retrieve net handler thread.");
/*  57 */   public static final ReportType REPORT_INSUFFICENT_THREAD_COUNT = new ReportType("Unable to inject %s lists in %s.");
/*     */ 
/*  59 */   public static final ReportType REPORT_CANNOT_COPY_OLD_TO_NEW = new ReportType("Cannot copy old %s to new.");
/*     */   private static Field listenerThreadField;
/*     */   private static Field minecraftServerField;
/*     */   private static Field listField;
/*     */   private static Field dedicatedThreadField;
/*     */   private static Method serverConnectionMethod;
/*     */   private List<VolatileField> listFields;
/*     */   private List<ReplacedArrayList<Object>> replacedLists;
/*     */   private NetLoginInjector netLoginInjector;
/*     */   private AbstractInputStreamLookup socketInjector;
/*     */   private ServerSocketType socketType;
/*     */   private Server server;
/*     */   private ErrorReporter reporter;
/*     */   private boolean hasAttempted;
/*     */   private boolean hasSuccess;
/*  91 */   private Object minecraftServer = null;
/*     */ 
/*     */   public InjectedServerConnection(ErrorReporter reporter, AbstractInputStreamLookup socketInjector, Server server, NetLoginInjector netLoginInjector) {
/*  94 */     this.listFields = new ArrayList();
/*  95 */     this.replacedLists = new ArrayList();
/*  96 */     this.reporter = reporter;
/*  97 */     this.server = server;
/*  98 */     this.socketInjector = socketInjector;
/*  99 */     this.netLoginInjector = netLoginInjector;
/*     */   }
/*     */ 
/*     */   public static Object getServerConnection(ErrorReporter reporter, Server server)
/*     */   {
/*     */     try
/*     */     {
/* 112 */       InjectedServerConnection inspector = new InjectedServerConnection(reporter, null, server, null);
/* 113 */       return inspector.getServerConnection();
/*     */     } catch (IllegalAccessException e) {
/* 115 */       throw new FieldAccessException("Reflection error.", e);
/*     */     } catch (IllegalArgumentException e) {
/* 117 */       throw new FieldAccessException("Corrupt data.", e);
/*     */     } catch (InvocationTargetException e) {
/* 119 */       throw new FieldAccessException("Minecraft error.", e);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void initialize()
/*     */   {
/* 128 */     if (!this.hasAttempted)
/* 129 */       this.hasAttempted = true;
/*     */     else {
/* 131 */       return;
/*     */     }
/* 133 */     if (minecraftServerField == null) {
/* 134 */       minecraftServerField = FuzzyReflection.fromObject(this.server, true).getFieldByType("MinecraftServer", MinecraftReflection.getMinecraftServerClass());
/*     */     }
/*     */     try
/*     */     {
/* 138 */       this.minecraftServer = FieldUtils.readField(minecraftServerField, this.server, true);
/*     */     } catch (IllegalAccessException e1) {
/* 140 */       this.reporter.reportWarning(this, Report.newBuilder(REPORT_CANNOT_FIND_MINECRAFT_SERVER));
/* 141 */       return;
/*     */     }
/*     */     try
/*     */     {
/* 145 */       if (serverConnectionMethod == null) {
/* 146 */         serverConnectionMethod = FuzzyReflection.fromClass(minecraftServerField.getType()).getMethodByParameters("getServerConnection", MinecraftReflection.getServerConnectionClass(), new Class[0]);
/*     */       }
/*     */ 
/* 150 */       this.socketType = ServerSocketType.SERVER_CONNECTION;
/*     */     }
/*     */     catch (IllegalArgumentException e)
/*     */     {
/* 154 */       this.socketType = ServerSocketType.LISTENER_THREAD;
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/* 158 */       this.reporter.reportDetailed(this, Report.newBuilder(REPORT_CANNOT_INJECT_SERVER_CONNECTION).error(e));
/*     */     }
/*     */   }
/*     */ 
/*     */   public ServerSocketType getServerSocketType()
/*     */   {
/* 169 */     return this.socketType;
/*     */   }
/*     */ 
/*     */   public void injectList()
/*     */   {
/* 176 */     initialize();
/*     */ 
/* 178 */     if (this.socketType == ServerSocketType.SERVER_CONNECTION)
/* 179 */       injectServerConnection();
/* 180 */     else if (this.socketType == ServerSocketType.LISTENER_THREAD) {
/* 181 */       injectListenerThread();
/*     */     }
/*     */     else
/* 184 */       throw new IllegalStateException("Unable to detected server connection.");
/*     */   }
/*     */ 
/*     */   private void initializeListenerField()
/*     */   {
/* 192 */     if (listenerThreadField == null)
/* 193 */       listenerThreadField = FuzzyReflection.fromObject(this.minecraftServer).getFieldByType("networkListenThread", MinecraftReflection.getNetworkListenThreadClass());
/*     */   }
/*     */ 
/*     */   public Object getListenerThread()
/*     */     throws RuntimeException, IllegalAccessException
/*     */   {
/* 204 */     initialize();
/*     */ 
/* 206 */     if (this.socketType == ServerSocketType.LISTENER_THREAD) {
/* 207 */       initializeListenerField();
/* 208 */       return listenerThreadField.get(this.minecraftServer);
/*     */     }
/* 210 */     return null;
/*     */   }
/*     */ 
/*     */   public Object getServerConnection()
/*     */     throws IllegalAccessException, IllegalArgumentException, InvocationTargetException
/*     */   {
/* 222 */     initialize();
/*     */ 
/* 224 */     if (this.socketType == ServerSocketType.SERVER_CONNECTION) {
/* 225 */       return serverConnectionMethod.invoke(this.minecraftServer, new Object[0]);
/*     */     }
/* 227 */     return null;
/*     */   }
/*     */ 
/*     */   private void injectListenerThread() {
/*     */     try {
/* 232 */       initializeListenerField();
/*     */     } catch (RuntimeException e) {
/* 234 */       this.reporter.reportDetailed(this, Report.newBuilder(REPORT_CANNOT_FIND_LISTENER_THREAD).callerParam(new Object[] { this.minecraftServer }).error(e));
/*     */ 
/* 237 */       return;
/*     */     }
/*     */ 
/* 240 */     Object listenerThread = null;
/*     */     try
/*     */     {
/* 244 */       listenerThread = getListenerThread();
/*     */     } catch (Exception e) {
/* 246 */       this.reporter.reportWarning(this, Report.newBuilder(REPORT_CANNOT_READ_LISTENER_THREAD).error(e));
/* 247 */       return;
/*     */     }
/*     */ 
/* 251 */     injectServerSocket(listenerThread);
/*     */ 
/* 254 */     injectEveryListField(listenerThread, 1);
/* 255 */     this.hasSuccess = true;
/*     */   }
/*     */ 
/*     */   private void injectServerConnection() {
/* 259 */     Object serverConnection = null;
/*     */     try
/*     */     {
/* 263 */       serverConnection = getServerConnection();
/*     */     } catch (Exception e) {
/* 265 */       this.reporter.reportDetailed(this, Report.newBuilder(REPORT_CANNOT_FIND_SERVER_CONNECTION).callerParam(new Object[] { this.minecraftServer }).error(e));
/*     */ 
/* 268 */       return;
/*     */     }
/*     */ 
/* 271 */     if (listField == null) {
/* 272 */       listField = FuzzyReflection.fromClass(serverConnectionMethod.getReturnType(), true).getFieldByType("netServerHandlerList", List.class);
/*     */     }
/* 274 */     if (dedicatedThreadField == null) {
/* 275 */       List matches = FuzzyReflection.fromObject(serverConnection, true).getFieldListByType(Thread.class);
/*     */ 
/* 279 */       if (matches.size() != 1) {
/* 280 */         this.reporter.reportWarning(this, Report.newBuilder(REPORT_UNEXPECTED_THREAD_COUNT).messageParam(new Object[] { serverConnection.getClass(), Integer.valueOf(matches.size()) }));
/*     */       }
/*     */       else
/*     */       {
/* 284 */         dedicatedThreadField = (Field)matches.get(0);
/*     */       }
/*     */     }
/*     */     try
/*     */     {
/* 289 */       if (dedicatedThreadField != null) {
/* 290 */         Object dedicatedThread = FieldUtils.readField(dedicatedThreadField, serverConnection, true);
/*     */ 
/* 293 */         injectServerSocket(dedicatedThread);
/* 294 */         injectEveryListField(dedicatedThread, 1);
/*     */       }
/*     */     } catch (IllegalAccessException e) {
/* 297 */       this.reporter.reportWarning(this, Report.newBuilder(REPORT_CANNOT_FIND_NET_HANDLER_THREAD).error(e));
/*     */     }
/*     */ 
/* 300 */     injectIntoList(serverConnection, listField);
/* 301 */     this.hasSuccess = true;
/*     */   }
/*     */ 
/*     */   private void injectServerSocket(Object container) {
/* 305 */     this.socketInjector.inject(container);
/*     */   }
/*     */ 
/*     */   private void injectEveryListField(Object container, int minimum)
/*     */   {
/* 315 */     List lists = FuzzyReflection.fromObject(container, true).getFieldListByType(List.class);
/*     */ 
/* 317 */     for (Field list : lists) {
/* 318 */       injectIntoList(container, list);
/*     */     }
/*     */ 
/* 322 */     if (lists.size() < minimum)
/* 323 */       this.reporter.reportWarning(this, Report.newBuilder(REPORT_INSUFFICENT_THREAD_COUNT).messageParam(new Object[] { Integer.valueOf(minimum), container.getClass() }));
/*     */   }
/*     */ 
/*     */   private void injectIntoList(Object instance, Field field)
/*     */   {
/* 329 */     VolatileField listFieldRef = new VolatileField(field, instance, true);
/* 330 */     List list = (List)listFieldRef.getValue();
/*     */ 
/* 333 */     if ((list instanceof ReplacedArrayList)) {
/* 334 */       this.replacedLists.add((ReplacedArrayList)list);
/*     */     } else {
/* 336 */       ReplacedArrayList injectedList = createReplacement(list);
/*     */ 
/* 338 */       this.replacedLists.add(injectedList);
/* 339 */       listFieldRef.setValue(injectedList);
/* 340 */       this.listFields.add(listFieldRef);
/*     */     }
/*     */   }
/*     */ 
/*     */   private ReplacedArrayList<Object> createReplacement(List<Object> list)
/*     */   {
/* 346 */     return new ReplacedArrayList(list)
/*     */     {
/*     */       private static final long serialVersionUID = 2070481080950500367L;
/* 353 */       private final ObjectWriter writer = new ObjectWriter();
/*     */ 
/*     */       protected void onReplacing(Object inserting, Object replacement)
/*     */       {
/* 358 */         if (!(inserting instanceof Factory))
/*     */           try
/*     */           {
/* 361 */             this.writer.copyTo(inserting, replacement, inserting.getClass());
/*     */           } catch (OutOfMemoryError e) {
/* 363 */             throw e;
/*     */           } catch (ThreadDeath e) {
/* 365 */             throw e;
/*     */           } catch (Throwable e) {
/* 367 */             InjectedServerConnection.this.reporter.reportDetailed(InjectedServerConnection.this, Report.newBuilder(InjectedServerConnection.REPORT_CANNOT_COPY_OLD_TO_NEW).messageParam(new Object[] { inserting }).callerParam(new Object[] { inserting, replacement }).error(e));
/*     */           }
/*     */       }
/*     */ 
/*     */       protected void onInserting(Object inserting)
/*     */       {
/* 377 */         if (MinecraftReflection.isLoginHandler(inserting)) {
/* 378 */           Object replaced = InjectedServerConnection.this.netLoginInjector.onNetLoginCreated(inserting);
/*     */ 
/* 381 */           if (inserting != replaced)
/* 382 */             addMapping(inserting, replaced, true);
/*     */         }
/*     */       }
/*     */ 
/*     */       protected void onRemoved(Object removing)
/*     */       {
/* 389 */         if (MinecraftReflection.isLoginHandler(removing))
/* 390 */           InjectedServerConnection.this.netLoginInjector.cleanup(removing);
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   public void replaceServerHandler(Object oldHandler, Object newHandler)
/*     */   {
/* 402 */     if (!this.hasAttempted) {
/* 403 */       injectList();
/*     */     }
/*     */ 
/* 406 */     if (this.hasSuccess)
/* 407 */       for (ReplacedArrayList replacedList : this.replacedLists)
/* 408 */         replacedList.addMapping(oldHandler, newHandler);
/*     */   }
/*     */ 
/*     */   public void revertServerHandler(Object oldHandler)
/*     */   {
/* 418 */     if (this.hasSuccess)
/* 419 */       for (ReplacedArrayList replacedList : this.replacedLists)
/* 420 */         replacedList.removeMapping(oldHandler);
/*     */   }
/*     */ 
/*     */   public void cleanupAll()
/*     */   {
/* 429 */     if (this.replacedLists.size() > 0)
/*     */     {
/* 431 */       for (ReplacedArrayList replacedList : this.replacedLists) {
/* 432 */         replacedList.revertAll();
/*     */       }
/* 434 */       for (VolatileField field : this.listFields) {
/* 435 */         field.revertValue();
/*     */       }
/*     */ 
/* 438 */       this.listFields.clear();
/* 439 */       this.replacedLists.clear();
/*     */     }
/*     */   }
/*     */ 
/*     */   public static enum ServerSocketType
/*     */   {
/*  73 */     SERVER_CONNECTION, 
/*  74 */     LISTENER_THREAD;
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.injector.player.InjectedServerConnection
 * JD-Core Version:    0.6.2
 */