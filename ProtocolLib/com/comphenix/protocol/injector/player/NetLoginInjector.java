/*     */ package com.comphenix.protocol.injector.player;
/*     */ 
/*     */ import com.comphenix.protocol.error.ErrorReporter;
/*     */ import com.comphenix.protocol.error.Report;
/*     */ import com.comphenix.protocol.error.Report.ReportBuilder;
/*     */ import com.comphenix.protocol.error.ReportType;
/*     */ import com.comphenix.protocol.injector.GamePhase;
/*     */ import com.comphenix.protocol.injector.server.TemporaryPlayerFactory;
/*     */ import com.comphenix.protocol.utility.MinecraftReflection;
/*     */ import com.google.common.collect.Maps;
/*     */ import java.util.concurrent.ConcurrentMap;
/*     */ import org.bukkit.Server;
/*     */ import org.bukkit.entity.Player;
/*     */ 
/*     */ class NetLoginInjector
/*     */ {
/*  40 */   public static final ReportType REPORT_CANNOT_HOOK_LOGIN_HANDLER = new ReportType("Unable to hook %s.");
/*  41 */   public static final ReportType REPORT_CANNOT_CLEANUP_LOGIN_HANDLER = new ReportType("Cannot cleanup %s.");
/*     */ 
/*  43 */   private ConcurrentMap<Object, PlayerInjector> injectedLogins = Maps.newConcurrentMap();
/*     */   private ProxyPlayerInjectionHandler injectionHandler;
/*  49 */   private TemporaryPlayerFactory playerFactory = new TemporaryPlayerFactory();
/*     */   private ErrorReporter reporter;
/*     */   private Server server;
/*     */ 
/*     */   public NetLoginInjector(ErrorReporter reporter, Server server, ProxyPlayerInjectionHandler injectionHandler)
/*     */   {
/*  56 */     this.reporter = reporter;
/*  57 */     this.server = server;
/*  58 */     this.injectionHandler = injectionHandler;
/*     */   }
/*     */ 
/*     */   public Object onNetLoginCreated(Object inserting)
/*     */   {
/*     */     try
/*     */     {
/*  69 */       if (!this.injectionHandler.isInjectionNecessary(GamePhase.LOGIN)) {
/*  70 */         return inserting;
/*     */       }
/*  72 */       Player temporary = this.playerFactory.createTemporaryPlayer(this.server);
/*     */ 
/*  74 */       PlayerInjector injector = this.injectionHandler.injectPlayer(temporary, inserting, PlayerInjectionHandler.ConflictStrategy.BAIL_OUT, GamePhase.LOGIN);
/*     */ 
/*  77 */       if (injector != null)
/*     */       {
/*  79 */         TemporaryPlayerFactory.setInjectorInPlayer(temporary, injector);
/*  80 */         injector.updateOnLogin = true;
/*     */ 
/*  83 */         this.injectedLogins.putIfAbsent(inserting, injector);
/*     */       }
/*     */ 
/*  87 */       return inserting;
/*     */     }
/*     */     catch (OutOfMemoryError e) {
/*  90 */       throw e;
/*     */     } catch (ThreadDeath e) {
/*  92 */       throw e;
/*     */     }
/*     */     catch (Throwable e) {
/*  95 */       this.reporter.reportDetailed(this, Report.newBuilder(REPORT_CANNOT_HOOK_LOGIN_HANDLER).messageParam(new Object[] { MinecraftReflection.getNetLoginHandlerName() }).callerParam(new Object[] { inserting, this.injectionHandler }).error(e));
/*     */     }
/*     */ 
/* 101 */     return inserting;
/*     */   }
/*     */ 
/*     */   public synchronized void cleanup(Object removing)
/*     */   {
/* 111 */     PlayerInjector injected = (PlayerInjector)this.injectedLogins.get(removing);
/*     */ 
/* 113 */     if (injected != null)
/*     */       try {
/* 115 */         PlayerInjector newInjector = null;
/* 116 */         Player player = injected.getPlayer();
/*     */ 
/* 119 */         this.injectedLogins.remove(removing);
/*     */ 
/* 122 */         if (injected.isClean()) {
/* 123 */           return;
/*     */         }
/*     */ 
/* 126 */         newInjector = this.injectionHandler.getInjectorByNetworkHandler(injected.getNetworkManager());
/* 127 */         this.injectionHandler.uninjectPlayer(player);
/*     */ 
/* 130 */         if ((newInjector != null) && 
/* 131 */           ((injected instanceof NetworkObjectInjector))) {
/* 132 */           newInjector.setNetworkManager(injected.getNetworkManager(), true);
/*     */         }
/*     */       }
/*     */       catch (OutOfMemoryError e)
/*     */       {
/* 137 */         throw e;
/*     */       } catch (ThreadDeath e) {
/* 139 */         throw e;
/*     */       }
/*     */       catch (Throwable e) {
/* 142 */         this.reporter.reportDetailed(this, Report.newBuilder(REPORT_CANNOT_CLEANUP_LOGIN_HANDLER).messageParam(new Object[] { MinecraftReflection.getNetLoginHandlerName() }).callerParam(new Object[] { removing }).error(e));
/*     */       }
/*     */   }
/*     */ 
/*     */   public void cleanupAll()
/*     */   {
/* 156 */     for (PlayerInjector injector : this.injectedLogins.values()) {
/* 157 */       injector.cleanupAll();
/*     */     }
/*     */ 
/* 160 */     this.injectedLogins.clear();
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.injector.player.NetLoginInjector
 * JD-Core Version:    0.6.2
 */