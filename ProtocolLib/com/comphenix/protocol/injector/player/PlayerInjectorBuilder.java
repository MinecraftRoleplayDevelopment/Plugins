/*     */ package com.comphenix.protocol.injector.player;
/*     */ 
/*     */ import com.comphenix.protocol.ProtocolLibrary;
/*     */ import com.comphenix.protocol.ProtocolManager;
/*     */ import com.comphenix.protocol.error.ErrorReporter;
/*     */ import com.comphenix.protocol.events.PacketListener;
/*     */ import com.comphenix.protocol.injector.GamePhase;
/*     */ import com.comphenix.protocol.injector.ListenerInvoker;
/*     */ import com.comphenix.protocol.injector.PacketFilterManager;
/*     */ import com.comphenix.protocol.utility.MinecraftVersion;
/*     */ import com.google.common.base.Preconditions;
/*     */ import com.google.common.base.Predicate;
/*     */ import java.util.Set;
/*     */ import javax.annotation.Nonnull;
/*     */ import org.bukkit.Bukkit;
/*     */ import org.bukkit.Server;
/*     */ 
/*     */ public class PlayerInjectorBuilder
/*     */ {
/*     */   protected ErrorReporter reporter;
/*     */   protected Predicate<GamePhase> injectionFilter;
/*     */   protected ListenerInvoker invoker;
/*     */   protected Set<PacketListener> packetListeners;
/*     */   protected Server server;
/*     */   protected MinecraftVersion version;
/*     */ 
/*     */   public static PlayerInjectorBuilder newBuilder()
/*     */   {
/*  28 */     return new PlayerInjectorBuilder();
/*     */   }
/*     */ 
/*     */   public PlayerInjectorBuilder reporter(@Nonnull ErrorReporter reporter)
/*     */   {
/*  48 */     Preconditions.checkNotNull(reporter, "reporter cannot be NULL");
/*  49 */     this.reporter = reporter;
/*  50 */     return this;
/*     */   }
/*     */ 
/*     */   @Nonnull
/*     */   public PlayerInjectorBuilder injectionFilter(@Nonnull Predicate<GamePhase> injectionFilter)
/*     */   {
/*  61 */     Preconditions.checkNotNull(injectionFilter, "injectionFilter cannot be NULL");
/*  62 */     this.injectionFilter = injectionFilter;
/*  63 */     return this;
/*     */   }
/*     */ 
/*     */   public PlayerInjectorBuilder invoker(@Nonnull ListenerInvoker invoker)
/*     */   {
/*  72 */     Preconditions.checkNotNull(invoker, "invoker cannot be NULL");
/*  73 */     this.invoker = invoker;
/*  74 */     return this;
/*     */   }
/*     */ 
/*     */   @Nonnull
/*     */   public PlayerInjectorBuilder packetListeners(@Nonnull Set<PacketListener> packetListeners)
/*     */   {
/*  84 */     Preconditions.checkNotNull(packetListeners, "packetListeners cannot be NULL");
/*  85 */     this.packetListeners = packetListeners;
/*  86 */     return this;
/*     */   }
/*     */ 
/*     */   public PlayerInjectorBuilder server(@Nonnull Server server)
/*     */   {
/*  95 */     Preconditions.checkNotNull(server, "server cannot be NULL");
/*  96 */     this.server = server;
/*  97 */     return this;
/*     */   }
/*     */ 
/*     */   public PlayerInjectorBuilder version(MinecraftVersion version)
/*     */   {
/* 106 */     this.version = version;
/* 107 */     return this;
/*     */   }
/*     */ 
/*     */   private void initializeDefaults()
/*     */   {
/* 114 */     ProtocolManager manager = ProtocolLibrary.getProtocolManager();
/*     */ 
/* 117 */     if (this.reporter == null)
/* 118 */       this.reporter = ProtocolLibrary.getErrorReporter();
/* 119 */     if (this.invoker == null)
/* 120 */       this.invoker = ((PacketFilterManager)manager);
/* 121 */     if (this.server == null)
/* 122 */       this.server = Bukkit.getServer();
/* 123 */     if (this.injectionFilter == null)
/* 124 */       throw new IllegalStateException("injectionFilter must be initialized.");
/* 125 */     if (this.packetListeners == null)
/* 126 */       throw new IllegalStateException("packetListeners must be initialized.");
/*     */   }
/*     */ 
/*     */   public PlayerInjectionHandler buildHandler()
/*     */   {
/* 137 */     initializeDefaults();
/*     */ 
/* 139 */     return new ProxyPlayerInjectionHandler(this.reporter, this.injectionFilter, this.invoker, this.packetListeners, this.server, this.version);
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.injector.player.PlayerInjectorBuilder
 * JD-Core Version:    0.6.2
 */