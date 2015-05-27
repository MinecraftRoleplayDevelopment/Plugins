/*     */ package com.comphenix.protocol.injector;
/*     */ 
/*     */ import com.comphenix.executors.BukkitFutures;
/*     */ import com.comphenix.protocol.async.AsyncFilterManager;
/*     */ import com.comphenix.protocol.error.ErrorReporter;
/*     */ import com.comphenix.protocol.error.Report;
/*     */ import com.comphenix.protocol.error.Report.ReportBuilder;
/*     */ import com.comphenix.protocol.error.ReportType;
/*     */ import com.comphenix.protocol.injector.player.InjectedServerConnection;
/*     */ import com.comphenix.protocol.injector.spigot.SpigotPacketInjector;
/*     */ import com.comphenix.protocol.utility.MinecraftReflection;
/*     */ import com.comphenix.protocol.utility.MinecraftVersion;
/*     */ import com.google.common.util.concurrent.FutureCallback;
/*     */ import com.google.common.util.concurrent.Futures;
/*     */ import javax.annotation.Nonnull;
/*     */ import org.bukkit.Server;
/*     */ import org.bukkit.event.world.WorldInitEvent;
/*     */ import org.bukkit.plugin.Plugin;
/*     */ 
/*     */ public class PacketFilterBuilder
/*     */ {
/*  22 */   public static final ReportType REPORT_TEMPORARY_EVENT_ERROR = new ReportType("Unable to register or handle temporary event.");
/*  23 */   public static final ReportType REPORT_SPIGOT_IS_DELAYING_INJECTOR = new ReportType("Delaying due to Spigot.");
/*     */   private ClassLoader classLoader;
/*     */   private Server server;
/*     */   private Plugin library;
/*     */   private MinecraftVersion mcVersion;
/*     */   private DelayedSingleTask unhookTask;
/*     */   private ErrorReporter reporter;
/*     */   private AsyncFilterManager asyncManager;
/*     */   private boolean nettyEnabled;
/*     */ 
/*     */   public PacketFilterBuilder classLoader(@Nonnull ClassLoader classLoader)
/*     */   {
/*  42 */     if (classLoader == null)
/*  43 */       throw new IllegalArgumentException("classLoader cannot be NULL.");
/*  44 */     this.classLoader = classLoader;
/*  45 */     return this;
/*     */   }
/*     */ 
/*     */   public PacketFilterBuilder server(@Nonnull Server server)
/*     */   {
/*  54 */     if (server == null)
/*  55 */       throw new IllegalArgumentException("server cannot be NULL.");
/*  56 */     this.server = server;
/*  57 */     return this;
/*     */   }
/*     */ 
/*     */   public PacketFilterBuilder library(@Nonnull Plugin library)
/*     */   {
/*  66 */     if (library == null)
/*  67 */       throw new IllegalArgumentException("library cannot be NULL.");
/*  68 */     this.library = library;
/*  69 */     return this;
/*     */   }
/*     */ 
/*     */   public PacketFilterBuilder minecraftVersion(@Nonnull MinecraftVersion mcVersion)
/*     */   {
/*  78 */     if (mcVersion == null)
/*  79 */       throw new IllegalArgumentException("minecraftVersion cannot be NULL.");
/*  80 */     this.mcVersion = mcVersion;
/*  81 */     return this;
/*     */   }
/*     */ 
/*     */   public PacketFilterBuilder unhookTask(@Nonnull DelayedSingleTask unhookTask)
/*     */   {
/*  90 */     if (unhookTask == null)
/*  91 */       throw new IllegalArgumentException("unhookTask cannot be NULL.");
/*  92 */     this.unhookTask = unhookTask;
/*  93 */     return this;
/*     */   }
/*     */ 
/*     */   public PacketFilterBuilder reporter(@Nonnull ErrorReporter reporter)
/*     */   {
/* 102 */     if (reporter == null)
/* 103 */       throw new IllegalArgumentException("reporter cannot be NULL.");
/* 104 */     this.reporter = reporter;
/* 105 */     return this;
/*     */   }
/*     */ 
/*     */   public boolean isNettyEnabled()
/*     */   {
/* 115 */     return this.nettyEnabled;
/*     */   }
/*     */ 
/*     */   public ClassLoader getClassLoader()
/*     */   {
/* 123 */     return this.classLoader;
/*     */   }
/*     */ 
/*     */   public Server getServer()
/*     */   {
/* 131 */     return this.server;
/*     */   }
/*     */ 
/*     */   public Plugin getLibrary()
/*     */   {
/* 139 */     return this.library;
/*     */   }
/*     */ 
/*     */   public MinecraftVersion getMinecraftVersion()
/*     */   {
/* 147 */     return this.mcVersion;
/*     */   }
/*     */ 
/*     */   public DelayedSingleTask getUnhookTask()
/*     */   {
/* 155 */     return this.unhookTask;
/*     */   }
/*     */ 
/*     */   public ErrorReporter getReporter()
/*     */   {
/* 163 */     return this.reporter;
/*     */   }
/*     */ 
/*     */   public AsyncFilterManager getAsyncManager()
/*     */   {
/* 173 */     return this.asyncManager;
/*     */   }
/*     */ 
/*     */   public InternalManager build()
/*     */   {
/* 181 */     if (this.reporter == null)
/* 182 */       throw new IllegalArgumentException("reporter cannot be NULL.");
/* 183 */     if (this.classLoader == null) {
/* 184 */       throw new IllegalArgumentException("classLoader cannot be NULL.");
/*     */     }
/* 186 */     this.asyncManager = new AsyncFilterManager(this.reporter, this.server.getScheduler());
/* 187 */     this.nettyEnabled = false;
/*     */ 
/* 190 */     if (SpigotPacketInjector.canUseSpigotListener())
/*     */     {
/* 192 */       if (InjectedServerConnection.getServerConnection(this.reporter, this.server) == null)
/*     */       {
/* 194 */         final DelayedPacketManager delayed = new DelayedPacketManager(this.reporter, this.mcVersion);
/*     */ 
/* 197 */         delayed.setAsynchronousManager(this.asyncManager);
/* 198 */         this.asyncManager.setManager(delayed);
/*     */ 
/* 200 */         Futures.addCallback(BukkitFutures.nextEvent(this.library, WorldInitEvent.class), new FutureCallback()
/*     */         {
/*     */           public void onSuccess(WorldInitEvent event)
/*     */           {
/* 205 */             if (delayed.isClosed())
/* 206 */               return;
/*     */             try
/*     */             {
/* 209 */               PacketFilterBuilder.this.registerSpigot(delayed);
/*     */             } catch (Exception e) {
/* 211 */               onFailure(e);
/*     */             }
/*     */           }
/*     */ 
/*     */           public void onFailure(Throwable error)
/*     */           {
/* 217 */             PacketFilterBuilder.this.reporter.reportWarning(PacketFilterBuilder.this, Report.newBuilder(PacketFilterBuilder.REPORT_TEMPORARY_EVENT_ERROR).error(error));
/*     */           }
/*     */         });
/* 222 */         this.reporter.reportWarning(this, Report.newBuilder(REPORT_SPIGOT_IS_DELAYING_INJECTOR));
/*     */ 
/* 225 */         return delayed;
/*     */       }
/* 227 */       this.nettyEnabled = (!MinecraftReflection.isMinecraftObject(InjectedServerConnection.getServerConnection(this.reporter, this.server)));
/*     */     }
/*     */ 
/* 233 */     return buildInternal();
/*     */   }
/*     */ 
/*     */   private void registerSpigot(DelayedPacketManager delayed)
/*     */   {
/* 238 */     this.nettyEnabled = (!MinecraftReflection.isMinecraftObject(InjectedServerConnection.getServerConnection(this.reporter, this.server)));
/*     */ 
/* 242 */     delayed.setDelegate(buildInternal());
/*     */   }
/*     */ 
/*     */   private PacketFilterManager buildInternal()
/*     */   {
/* 250 */     PacketFilterManager manager = new PacketFilterManager(this);
/*     */ 
/* 253 */     this.asyncManager.setManager(manager);
/* 254 */     return manager;
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.injector.PacketFilterBuilder
 * JD-Core Version:    0.6.2
 */