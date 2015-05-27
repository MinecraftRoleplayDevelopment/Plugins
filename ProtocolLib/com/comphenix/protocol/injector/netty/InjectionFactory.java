/*     */ package com.comphenix.protocol.injector.netty;
/*     */ 
/*     */ import com.comphenix.protocol.injector.server.SocketInjector;
/*     */ import com.comphenix.protocol.injector.server.TemporaryPlayerFactory;
/*     */ import com.comphenix.protocol.reflect.FuzzyReflection;
/*     */ import com.comphenix.protocol.utility.MinecraftFields;
/*     */ import com.comphenix.protocol.utility.MinecraftReflection;
/*     */ import com.google.common.collect.MapMaker;
/*     */ import io.netty.channel.Channel;
/*     */ import java.util.concurrent.ConcurrentMap;
/*     */ import javax.annotation.Nonnull;
/*     */ import org.bukkit.Bukkit;
/*     */ import org.bukkit.entity.Player;
/*     */ import org.bukkit.plugin.Plugin;
/*     */ 
/*     */ class InjectionFactory
/*     */ {
/*  29 */   private final ConcurrentMap<Player, Injector> playerLookup = new MapMaker().weakKeys().weakValues().makeMap();
/*  30 */   private final ConcurrentMap<String, Injector> nameLookup = new MapMaker().weakValues().makeMap();
/*     */   private volatile boolean closed;
/*     */   private final Plugin plugin;
/*     */ 
/*     */   public InjectionFactory(Plugin plugin)
/*     */   {
/*  39 */     this.plugin = plugin;
/*     */   }
/*     */ 
/*     */   public Plugin getPlugin()
/*     */   {
/*  47 */     return this.plugin;
/*     */   }
/*     */ 
/*     */   @Nonnull
/*     */   public Injector fromPlayer(Player player, ChannelListener listener)
/*     */   {
/*  58 */     if (this.closed)
/*  59 */       return new ClosedInjector(player);
/*  60 */     Injector injector = (Injector)this.playerLookup.get(player);
/*     */ 
/*  63 */     if (injector == null)
/*  64 */       injector = getTemporaryInjector(player);
/*  65 */     if ((injector != null) && (!injector.isClosed())) {
/*  66 */       return injector;
/*     */     }
/*  68 */     Object networkManager = MinecraftFields.getNetworkManager(player);
/*     */ 
/*  71 */     if (networkManager == null) {
/*  72 */       return fromName(player.getName(), player);
/*     */     }
/*  74 */     Channel channel = (Channel)FuzzyReflection.getFieldValue(networkManager, Channel.class, true);
/*     */ 
/*  77 */     injector = (ChannelInjector)ChannelInjector.findChannelHandler(channel, ChannelInjector.class);
/*     */ 
/*  79 */     if (injector != null)
/*     */     {
/*  81 */       this.playerLookup.remove(injector.getPlayer());
/*  82 */       injector.setPlayer(player);
/*     */     } else {
/*  84 */       injector = new ChannelInjector(player, networkManager, channel, listener, this);
/*     */     }
/*     */ 
/*  88 */     cacheInjector(player, injector);
/*  89 */     return injector;
/*     */   }
/*     */ 
/*     */   public Injector fromName(String name, Player player)
/*     */   {
/* 100 */     if (!this.closed) {
/* 101 */       Injector injector = (Injector)this.nameLookup.get(name);
/*     */ 
/* 104 */       if (injector != null)
/*     */       {
/* 106 */         injector.setUpdatedPlayer(player);
/* 107 */         return injector;
/*     */       }
/*     */     }
/* 110 */     return new ClosedInjector(player);
/*     */   }
/*     */ 
/*     */   @Nonnull
/*     */   public Injector fromChannel(Channel channel, ChannelListener listener, TemporaryPlayerFactory playerFactory)
/*     */   {
/* 123 */     if (this.closed) {
/* 124 */       return new ClosedInjector(null);
/*     */     }
/* 126 */     Object networkManager = findNetworkManager(channel);
/* 127 */     Player temporaryPlayer = playerFactory.createTemporaryPlayer(Bukkit.getServer());
/* 128 */     ChannelInjector injector = new ChannelInjector(temporaryPlayer, networkManager, channel, listener, this);
/*     */ 
/* 131 */     TemporaryPlayerFactory.setInjectorInPlayer(temporaryPlayer, new ChannelInjector.ChannelSocketInjector(injector));
/* 132 */     return injector;
/*     */   }
/*     */ 
/*     */   public Injector invalidate(Player player)
/*     */   {
/* 141 */     Injector injector = (Injector)this.playerLookup.remove(player);
/*     */ 
/* 143 */     this.nameLookup.remove(player.getName());
/* 144 */     return injector;
/*     */   }
/*     */ 
/*     */   public Injector cacheInjector(Player player, Injector injector)
/*     */   {
/* 154 */     this.nameLookup.put(player.getName(), injector);
/* 155 */     return (Injector)this.playerLookup.put(player, injector);
/*     */   }
/*     */ 
/*     */   public Injector cacheInjector(String name, Injector injector)
/*     */   {
/* 165 */     return (Injector)this.nameLookup.put(name, injector);
/*     */   }
/*     */ 
/*     */   private ChannelInjector getTemporaryInjector(Player player)
/*     */   {
/* 174 */     SocketInjector injector = TemporaryPlayerFactory.getInjectorFromPlayer(player);
/*     */ 
/* 176 */     if (injector != null) {
/* 177 */       return ((ChannelInjector.ChannelSocketInjector)injector).getChannelInjector();
/*     */     }
/* 179 */     return null;
/*     */   }
/*     */ 
/*     */   private Object findNetworkManager(Channel channel)
/*     */   {
/* 189 */     Object networkManager = ChannelInjector.findChannelHandler(channel, MinecraftReflection.getNetworkManagerClass());
/*     */ 
/* 191 */     if (networkManager != null)
/* 192 */       return networkManager;
/* 193 */     throw new IllegalArgumentException("Unable to find NetworkManager in " + channel);
/*     */   }
/*     */ 
/*     */   public boolean isClosed()
/*     */   {
/* 203 */     return this.closed;
/*     */   }
/*     */ 
/*     */   public synchronized void close()
/*     */   {
/* 210 */     if (!this.closed) {
/* 211 */       this.closed = true;
/*     */ 
/* 214 */       for (Injector injector : this.playerLookup.values())
/* 215 */         injector.close();
/* 216 */       for (Injector injector : this.nameLookup.values())
/* 217 */         injector.close();
/*     */     }
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.injector.netty.InjectionFactory
 * JD-Core Version:    0.6.2
 */