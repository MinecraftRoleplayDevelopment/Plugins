/*     */ package com.comphenix.protocol.events;
/*     */ 
/*     */ import com.comphenix.protocol.PacketType;
/*     */ import com.comphenix.protocol.PacketType.Sender;
/*     */ import com.comphenix.protocol.injector.GamePhase;
/*     */ import com.comphenix.protocol.injector.packet.PacketRegistry;
/*     */ import com.google.common.base.Preconditions;
/*     */ import com.google.common.collect.Iterables;
/*     */ import com.google.common.collect.Lists;
/*     */ import com.google.common.collect.Sets;
/*     */ import java.util.List;
/*     */ import java.util.Set;
/*     */ import javax.annotation.Nonnull;
/*     */ import org.bukkit.plugin.Plugin;
/*     */ 
/*     */ public abstract class PacketAdapter
/*     */   implements PacketListener
/*     */ {
/*     */   protected Plugin plugin;
/*     */   protected ConnectionSide connectionSide;
/*  44 */   protected ListeningWhitelist receivingWhitelist = ListeningWhitelist.EMPTY_WHITELIST;
/*  45 */   protected ListeningWhitelist sendingWhitelist = ListeningWhitelist.EMPTY_WHITELIST;
/*     */ 
/*     */   public PacketAdapter(@Nonnull AdapterParameteters params)
/*     */   {
/*  52 */     this(checkValidity(params).plugin, params.connectionSide, params.listenerPriority, params.gamePhase, params.options, params.packets);
/*     */   }
/*     */ 
/*     */   public PacketAdapter(Plugin plugin, PacketType[] types)
/*     */   {
/*  65 */     this(plugin, ListenerPriority.NORMAL, types);
/*     */   }
/*     */ 
/*     */   public PacketAdapter(Plugin plugin, Iterable<? extends PacketType> types)
/*     */   {
/*  74 */     this(params(plugin, (PacketType[])Iterables.toArray(types, PacketType.class)));
/*     */   }
/*     */ 
/*     */   public PacketAdapter(Plugin plugin, ListenerPriority listenerPriority, Iterable<? extends PacketType> types)
/*     */   {
/*  84 */     this(params(plugin, (PacketType[])Iterables.toArray(types, PacketType.class)).listenerPriority(listenerPriority));
/*     */   }
/*     */ 
/*     */   public PacketAdapter(Plugin plugin, ListenerPriority listenerPriority, Iterable<? extends PacketType> types, ListenerOptions[] options)
/*     */   {
/*  95 */     this(params(plugin, (PacketType[])Iterables.toArray(types, PacketType.class)).listenerPriority(listenerPriority).options(options));
/*     */   }
/*     */ 
/*     */   public PacketAdapter(Plugin plugin, ListenerPriority listenerPriority, PacketType[] types)
/*     */   {
/* 105 */     this(params(plugin, types).listenerPriority(listenerPriority));
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public PacketAdapter(Plugin plugin, ConnectionSide connectionSide, Integer[] packets)
/*     */   {
/* 118 */     this(plugin, connectionSide, ListenerPriority.NORMAL, packets);
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public PacketAdapter(Plugin plugin, ConnectionSide connectionSide, ListenerPriority listenerPriority, Set<Integer> packets)
/*     */   {
/* 132 */     this(plugin, connectionSide, listenerPriority, GamePhase.PLAYING, (Integer[])packets.toArray(new Integer[0]));
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public PacketAdapter(Plugin plugin, ConnectionSide connectionSide, GamePhase gamePhase, Set<Integer> packets)
/*     */   {
/* 148 */     this(plugin, connectionSide, ListenerPriority.NORMAL, gamePhase, (Integer[])packets.toArray(new Integer[0]));
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public PacketAdapter(Plugin plugin, ConnectionSide connectionSide, ListenerPriority listenerPriority, GamePhase gamePhase, Set<Integer> packets)
/*     */   {
/* 165 */     this(plugin, connectionSide, listenerPriority, gamePhase, (Integer[])packets.toArray(new Integer[0]));
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public PacketAdapter(Plugin plugin, ConnectionSide connectionSide, ListenerPriority listenerPriority, Integer[] packets)
/*     */   {
/* 179 */     this(plugin, connectionSide, listenerPriority, GamePhase.PLAYING, packets);
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public PacketAdapter(Plugin plugin, ConnectionSide connectionSide, ListenerOptions[] options, Integer[] packets)
/*     */   {
/* 193 */     this(plugin, connectionSide, ListenerPriority.NORMAL, GamePhase.PLAYING, options, packets);
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public PacketAdapter(Plugin plugin, ConnectionSide connectionSide, GamePhase gamePhase, Integer[] packets)
/*     */   {
/* 207 */     this(plugin, connectionSide, ListenerPriority.NORMAL, gamePhase, packets);
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public PacketAdapter(Plugin plugin, ConnectionSide connectionSide, ListenerPriority listenerPriority, GamePhase gamePhase, Integer[] packets)
/*     */   {
/* 224 */     this(plugin, connectionSide, listenerPriority, gamePhase, new ListenerOptions[0], packets);
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public PacketAdapter(Plugin plugin, ConnectionSide connectionSide, ListenerPriority listenerPriority, GamePhase gamePhase, ListenerOptions[] options, Integer[] packets)
/*     */   {
/* 247 */     this(plugin, connectionSide, listenerPriority, gamePhase, options, (PacketType[])PacketRegistry.toPacketTypes(Sets.newHashSet(packets), connectionSide.getSender()).toArray(new PacketType[0]));
/*     */   }
/*     */ 
/*     */   private PacketAdapter(Plugin plugin, ConnectionSide connectionSide, ListenerPriority listenerPriority, GamePhase gamePhase, ListenerOptions[] options, PacketType[] packets)
/*     */   {
/* 257 */     if (plugin == null)
/* 258 */       throw new IllegalArgumentException("plugin cannot be null");
/* 259 */     if (connectionSide == null)
/* 260 */       throw new IllegalArgumentException("connectionSide cannot be null");
/* 261 */     if (listenerPriority == null)
/* 262 */       throw new IllegalArgumentException("listenerPriority cannot be null");
/* 263 */     if (gamePhase == null)
/* 264 */       throw new IllegalArgumentException("gamePhase cannot be NULL");
/* 265 */     if (packets == null)
/* 266 */       throw new IllegalArgumentException("packets cannot be null");
/* 267 */     if (options == null) {
/* 268 */       throw new IllegalArgumentException("options cannot be null");
/*     */     }
/* 270 */     ListenerOptions[] serverOptions = options;
/* 271 */     ListenerOptions[] clientOptions = options;
/*     */ 
/* 274 */     if (connectionSide == ConnectionSide.BOTH) {
/* 275 */       serverOptions = (ListenerOptions[])except(serverOptions, new ListenerOptions[0], ListenerOptions.INTERCEPT_INPUT_BUFFER);
/*     */     }
/*     */ 
/* 280 */     if (connectionSide.isForServer()) {
/* 281 */       this.sendingWhitelist = ListeningWhitelist.newBuilder().priority(listenerPriority).types(packets).gamePhase(gamePhase).options(serverOptions).build();
/*     */     }
/*     */ 
/* 288 */     if (connectionSide.isForClient()) {
/* 289 */       this.receivingWhitelist = ListeningWhitelist.newBuilder().priority(listenerPriority).types(packets).gamePhase(gamePhase).options(clientOptions).build();
/*     */     }
/*     */ 
/* 296 */     this.plugin = plugin;
/* 297 */     this.connectionSide = connectionSide;
/*     */   }
/*     */ 
/*     */   private static <T> T[] except(T[] values, T[] buffer, T except)
/*     */   {
/* 302 */     List result = Lists.newArrayList(values);
/*     */ 
/* 304 */     result.remove(except);
/* 305 */     return result.toArray(buffer);
/*     */   }
/*     */ 
/*     */   public void onPacketReceiving(PacketEvent event)
/*     */   {
/* 311 */     throw new IllegalStateException("Override onPacketReceiving to get notifcations of received packets!");
/*     */   }
/*     */ 
/*     */   public void onPacketSending(PacketEvent event)
/*     */   {
/* 317 */     throw new IllegalStateException("Override onPacketSending to get notifcations of sent packets!");
/*     */   }
/*     */ 
/*     */   public ListeningWhitelist getReceivingWhitelist()
/*     */   {
/* 322 */     return this.receivingWhitelist;
/*     */   }
/*     */ 
/*     */   public ListeningWhitelist getSendingWhitelist()
/*     */   {
/* 327 */     return this.sendingWhitelist;
/*     */   }
/*     */ 
/*     */   public Plugin getPlugin()
/*     */   {
/* 332 */     return this.plugin;
/*     */   }
/*     */ 
/*     */   public static String getPluginName(PacketListener listener)
/*     */   {
/* 341 */     return getPluginName(listener.getPlugin());
/*     */   }
/*     */ 
/*     */   public static String getPluginName(Plugin plugin)
/*     */   {
/*     */     try
/*     */     {
/* 352 */       if (plugin == null) {
/* 353 */         return "UNKNOWN";
/*     */       }
/* 355 */       return plugin.getName();
/*     */     } catch (NoSuchMethodError e) {
/*     */     }
/* 358 */     return plugin.toString();
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 365 */     return String.format("PacketAdapter[plugin=%s, sending=%s, receiving=%s]", new Object[] { getPluginName(this), this.sendingWhitelist, this.receivingWhitelist });
/*     */   }
/*     */ 
/*     */   public static AdapterParameteters params()
/*     */   {
/* 378 */     return new AdapterParameteters();
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public static AdapterParameteters params(Plugin plugin, Integer[] packets)
/*     */   {
/* 392 */     return new AdapterParameteters().plugin(plugin).packets(packets);
/*     */   }
/*     */ 
/*     */   public static AdapterParameteters params(Plugin plugin, PacketType[] packets)
/*     */   {
/* 404 */     return new AdapterParameteters().plugin(plugin).types(packets);
/*     */   }
/*     */ 
/*     */   private static AdapterParameteters checkValidity(AdapterParameteters params)
/*     */   {
/* 629 */     if (params == null)
/* 630 */       throw new IllegalArgumentException("params cannot be NULL.");
/* 631 */     if (params.plugin == null)
/* 632 */       throw new IllegalStateException("Plugin was never set in the parameters.");
/* 633 */     if (params.connectionSide == null)
/* 634 */       throw new IllegalStateException("Connection side was never set in the parameters.");
/* 635 */     if (params.packets == null)
/* 636 */       throw new IllegalStateException("Packet IDs was never set in the parameters.");
/* 637 */     return params;
/*     */   }
/*     */ 
/*     */   public static class AdapterParameteters
/*     */   {
/*     */     private Plugin plugin;
/*     */     private ConnectionSide connectionSide;
/*     */     private PacketType[] packets;
/* 419 */     private GamePhase gamePhase = GamePhase.PLAYING;
/* 420 */     private ListenerOptions[] options = new ListenerOptions[0];
/* 421 */     private ListenerPriority listenerPriority = ListenerPriority.NORMAL;
/*     */ 
/*     */     public AdapterParameteters plugin(@Nonnull Plugin plugin)
/*     */     {
/* 429 */       this.plugin = ((Plugin)Preconditions.checkNotNull(plugin, "plugin cannot be NULL."));
/* 430 */       return this;
/*     */     }
/*     */ 
/*     */     public AdapterParameteters connectionSide(@Nonnull ConnectionSide connectionSide)
/*     */     {
/* 439 */       this.connectionSide = ((ConnectionSide)Preconditions.checkNotNull(connectionSide, "connectionside cannot be NULL."));
/* 440 */       return this;
/*     */     }
/*     */ 
/*     */     public AdapterParameteters clientSide()
/*     */     {
/* 448 */       return connectionSide(ConnectionSide.add(this.connectionSide, ConnectionSide.CLIENT_SIDE));
/*     */     }
/*     */ 
/*     */     public AdapterParameteters serverSide()
/*     */     {
/* 456 */       return connectionSide(ConnectionSide.add(this.connectionSide, ConnectionSide.SERVER_SIDE));
/*     */     }
/*     */ 
/*     */     public AdapterParameteters listenerPriority(@Nonnull ListenerPriority listenerPriority)
/*     */     {
/* 467 */       this.listenerPriority = ((ListenerPriority)Preconditions.checkNotNull(listenerPriority, "listener priority cannot be NULL."));
/* 468 */       return this;
/*     */     }
/*     */ 
/*     */     public AdapterParameteters gamePhase(@Nonnull GamePhase gamePhase)
/*     */     {
/* 479 */       this.gamePhase = ((GamePhase)Preconditions.checkNotNull(gamePhase, "gamePhase cannot be NULL."));
/* 480 */       return this;
/*     */     }
/*     */ 
/*     */     public AdapterParameteters loginPhase()
/*     */     {
/* 488 */       return gamePhase(GamePhase.LOGIN);
/*     */     }
/*     */ 
/*     */     public AdapterParameteters options(@Nonnull ListenerOptions[] options)
/*     */     {
/* 499 */       this.options = ((ListenerOptions[])Preconditions.checkNotNull(options, "options cannot be NULL."));
/* 500 */       return this;
/*     */     }
/*     */ 
/*     */     public AdapterParameteters options(@Nonnull Set<? extends ListenerOptions> options)
/*     */     {
/* 511 */       Preconditions.checkNotNull(options, "options cannot be NULL.");
/* 512 */       this.options = ((ListenerOptions[])options.toArray(new ListenerOptions[0]));
/* 513 */       return this;
/*     */     }
/*     */ 
/*     */     private AdapterParameteters addOption(ListenerOptions option)
/*     */     {
/* 522 */       if (this.options == null) {
/* 523 */         return options(new ListenerOptions[] { option });
/*     */       }
/* 525 */       Set current = Sets.newHashSet(this.options);
/* 526 */       current.add(option);
/* 527 */       return options(current);
/*     */     }
/*     */ 
/*     */     public AdapterParameteters optionIntercept()
/*     */     {
/* 536 */       return addOption(ListenerOptions.INTERCEPT_INPUT_BUFFER);
/*     */     }
/*     */ 
/*     */     public AdapterParameteters optionManualGamePhase()
/*     */     {
/* 546 */       return addOption(ListenerOptions.DISABLE_GAMEPHASE_DETECTION);
/*     */     }
/*     */ 
/*     */     public AdapterParameteters optionAsync()
/*     */     {
/* 556 */       return addOption(ListenerOptions.ASYNC);
/*     */     }
/*     */ 
/*     */     @Deprecated
/*     */     public AdapterParameteters packets(@Nonnull Integer[] packets)
/*     */     {
/* 570 */       Preconditions.checkNotNull(packets, "packets cannot be NULL");
/* 571 */       PacketType[] types = new PacketType[packets.length];
/*     */ 
/* 573 */       for (int i = 0; i < types.length; i++) {
/* 574 */         types[i] = PacketType.findLegacy(packets[i].intValue());
/*     */       }
/* 576 */       this.packets = types;
/* 577 */       return this;
/*     */     }
/*     */ 
/*     */     @Deprecated
/*     */     public AdapterParameteters packets(@Nonnull Set<Integer> packets)
/*     */     {
/* 589 */       return packets((Integer[])packets.toArray(new Integer[0]));
/*     */     }
/*     */ 
/*     */     public AdapterParameteters types(@Nonnull PacketType[] packets)
/*     */     {
/* 601 */       if (this.connectionSide == null) {
/* 602 */         for (PacketType type : packets) {
/* 603 */           this.connectionSide = ConnectionSide.add(this.connectionSide, type.getSender().toSide());
/*     */         }
/*     */       }
/* 606 */       this.packets = ((PacketType[])Preconditions.checkNotNull(packets, "packets cannot be NULL"));
/*     */ 
/* 608 */       if (packets.length == 0)
/* 609 */         throw new IllegalArgumentException("Passed an empty packet type array.");
/* 610 */       return this;
/*     */     }
/*     */ 
/*     */     public AdapterParameteters types(@Nonnull Set<PacketType> packets)
/*     */     {
/* 621 */       return types((PacketType[])packets.toArray(new PacketType[0]));
/*     */     }
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.events.PacketAdapter
 * JD-Core Version:    0.6.2
 */