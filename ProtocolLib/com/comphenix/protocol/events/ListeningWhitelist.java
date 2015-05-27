/*     */ package com.comphenix.protocol.events;
/*     */ 
/*     */ import com.comphenix.protocol.PacketType;
/*     */ import com.comphenix.protocol.injector.GamePhase;
/*     */ import com.comphenix.protocol.injector.packet.PacketRegistry;
/*     */ import com.google.common.base.Objects;
/*     */ import com.google.common.base.Objects.ToStringHelper;
/*     */ import com.google.common.collect.Sets;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.EnumSet;
/*     */ import java.util.Set;
/*     */ 
/*     */ public class ListeningWhitelist
/*     */ {
/*  41 */   public static final ListeningWhitelist EMPTY_WHITELIST = new ListeningWhitelist(ListenerPriority.LOW, new Integer[0]);
/*     */   private final ListenerPriority priority;
/*     */   private final GamePhase gamePhase;
/*     */   private final Set<ListenerOptions> options;
/*     */   private final Set<PacketType> types;
/*     */   private transient Set<Integer> intWhitelist;
/*     */ 
/*     */   private ListeningWhitelist(Builder builder)
/*     */   {
/*  52 */     this.priority = builder.priority;
/*  53 */     this.types = builder.types;
/*  54 */     this.gamePhase = builder.gamePhase;
/*  55 */     this.options = builder.options;
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public ListeningWhitelist(ListenerPriority priority, Set<Integer> whitelist)
/*     */   {
/*  67 */     this(priority, whitelist, GamePhase.PLAYING);
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public ListeningWhitelist(ListenerPriority priority, Set<Integer> whitelist, GamePhase gamePhase)
/*     */   {
/*  80 */     this.priority = priority;
/*  81 */     this.types = PacketRegistry.toPacketTypes(safeSet(whitelist));
/*  82 */     this.gamePhase = gamePhase;
/*  83 */     this.options = EnumSet.noneOf(ListenerOptions.class);
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public ListeningWhitelist(ListenerPriority priority, Integer[] whitelist)
/*     */   {
/*  95 */     this.priority = priority;
/*  96 */     this.types = PacketRegistry.toPacketTypes(Sets.newHashSet(whitelist));
/*  97 */     this.gamePhase = GamePhase.PLAYING;
/*  98 */     this.options = EnumSet.noneOf(ListenerOptions.class);
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public ListeningWhitelist(ListenerPriority priority, Integer[] whitelist, GamePhase gamePhase)
/*     */   {
/* 111 */     this.priority = priority;
/* 112 */     this.types = PacketRegistry.toPacketTypes(Sets.newHashSet(whitelist));
/* 113 */     this.gamePhase = gamePhase;
/* 114 */     this.options = EnumSet.noneOf(ListenerOptions.class);
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public ListeningWhitelist(ListenerPriority priority, Integer[] whitelist, GamePhase gamePhase, ListenerOptions[] options)
/*     */   {
/* 128 */     this.priority = priority;
/* 129 */     this.types = PacketRegistry.toPacketTypes(Sets.newHashSet(whitelist));
/* 130 */     this.gamePhase = gamePhase;
/* 131 */     this.options = safeEnumSet(Arrays.asList(options), ListenerOptions.class);
/*     */   }
/*     */ 
/*     */   public boolean isEnabled()
/*     */   {
/* 139 */     return (this.types != null) && (this.types.size() > 0);
/*     */   }
/*     */ 
/*     */   public ListenerPriority getPriority()
/*     */   {
/* 147 */     return this.priority;
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public Set<Integer> getWhitelist()
/*     */   {
/* 158 */     if (this.intWhitelist == null)
/* 159 */       this.intWhitelist = PacketRegistry.toLegacy(this.types);
/* 160 */     return this.intWhitelist;
/*     */   }
/*     */ 
/*     */   public Set<PacketType> getTypes()
/*     */   {
/* 168 */     return this.types;
/*     */   }
/*     */ 
/*     */   public GamePhase getGamePhase()
/*     */   {
/* 176 */     return this.gamePhase;
/*     */   }
/*     */ 
/*     */   public Set<ListenerOptions> getOptions()
/*     */   {
/* 184 */     return Collections.unmodifiableSet(this.options);
/*     */   }
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 189 */     return Objects.hashCode(new Object[] { this.priority, this.types, this.gamePhase, this.options });
/*     */   }
/*     */ 
/*     */   public static boolean containsAny(ListeningWhitelist whitelist, int[] idList)
/*     */   {
/* 199 */     if (whitelist != null) {
/* 200 */       for (int i = 0; i < idList.length; i++) {
/* 201 */         if (whitelist.getWhitelist().contains(Integer.valueOf(idList[i]))) {
/* 202 */           return true;
/*     */         }
/*     */       }
/*     */     }
/* 206 */     return false;
/*     */   }
/*     */ 
/*     */   public static boolean isEmpty(ListeningWhitelist whitelist)
/*     */   {
/* 215 */     if (whitelist == EMPTY_WHITELIST)
/* 216 */       return true;
/* 217 */     if (whitelist == null) {
/* 218 */       return true;
/*     */     }
/* 220 */     return whitelist.getTypes().isEmpty();
/*     */   }
/*     */ 
/*     */   public boolean equals(Object obj)
/*     */   {
/* 225 */     if ((obj instanceof ListeningWhitelist)) {
/* 226 */       ListeningWhitelist other = (ListeningWhitelist)obj;
/* 227 */       return (Objects.equal(this.priority, other.priority)) && (Objects.equal(this.types, other.types)) && (Objects.equal(this.gamePhase, other.gamePhase)) && (Objects.equal(this.options, other.options));
/*     */     }
/*     */ 
/* 232 */     return false;
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 238 */     if (this == EMPTY_WHITELIST) {
/* 239 */       return "EMPTY_WHITELIST";
/*     */     }
/* 241 */     return Objects.toStringHelper(this).add("priority", this.priority).add("packets", this.types).add("gamephase", this.gamePhase).add("options", this.options).toString();
/*     */   }
/*     */ 
/*     */   public static Builder newBuilder()
/*     */   {
/* 254 */     return new Builder(null, null);
/*     */   }
/*     */ 
/*     */   public static Builder newBuilder(ListeningWhitelist template)
/*     */   {
/* 263 */     return new Builder(template, null);
/*     */   }
/*     */ 
/*     */   private static <T extends Enum<T>> EnumSet<T> safeEnumSet(Collection<T> options, Class<T> enumClass)
/*     */   {
/* 272 */     if ((options != null) && (!options.isEmpty())) {
/* 273 */       return EnumSet.copyOf(options);
/*     */     }
/* 275 */     return EnumSet.noneOf(enumClass);
/*     */   }
/*     */ 
/*     */   private static <T> Set<T> safeSet(Collection<T> set)
/*     */   {
/* 285 */     if (set != null) {
/* 286 */       return Sets.newHashSet(set);
/*     */     }
/* 288 */     return Collections.emptySet();
/*     */   }
/*     */ 
/*     */   public static class Builder
/*     */   {
/* 297 */     private ListenerPriority priority = ListenerPriority.NORMAL;
/* 298 */     private Set<PacketType> types = Sets.newHashSet();
/* 299 */     private GamePhase gamePhase = GamePhase.PLAYING;
/* 300 */     private Set<ListenerOptions> options = Sets.newHashSet();
/*     */ 
/*     */     private Builder(ListeningWhitelist template)
/*     */     {
/* 307 */       if (template != null) {
/* 308 */         priority(template.getPriority());
/* 309 */         gamePhase(template.getGamePhase());
/* 310 */         types(template.getTypes());
/* 311 */         options(template.getOptions());
/*     */       }
/*     */     }
/*     */ 
/*     */     public Builder priority(ListenerPriority priority)
/*     */     {
/* 321 */       this.priority = priority;
/* 322 */       return this;
/*     */     }
/*     */ 
/*     */     public Builder monitor()
/*     */     {
/* 330 */       return priority(ListenerPriority.MONITOR);
/*     */     }
/*     */ 
/*     */     public Builder normal()
/*     */     {
/* 338 */       return priority(ListenerPriority.NORMAL);
/*     */     }
/*     */ 
/*     */     public Builder lowest()
/*     */     {
/* 346 */       return priority(ListenerPriority.LOWEST);
/*     */     }
/*     */ 
/*     */     public Builder low()
/*     */     {
/* 354 */       return priority(ListenerPriority.LOW);
/*     */     }
/*     */ 
/*     */     public Builder highest()
/*     */     {
/* 362 */       return priority(ListenerPriority.HIGHEST);
/*     */     }
/*     */ 
/*     */     public Builder high()
/*     */     {
/* 370 */       return priority(ListenerPriority.HIGH);
/*     */     }
/*     */ 
/*     */     @Deprecated
/*     */     public Builder whitelist(Collection<Integer> whitelist)
/*     */     {
/* 382 */       this.types = PacketRegistry.toPacketTypes(ListeningWhitelist.safeSet(whitelist));
/* 383 */       return this;
/*     */     }
/*     */ 
/*     */     public Builder types(PacketType[] types)
/*     */     {
/* 392 */       this.types = ListeningWhitelist.safeSet(Sets.newHashSet(types));
/* 393 */       return this;
/*     */     }
/*     */ 
/*     */     public Builder types(Collection<PacketType> types)
/*     */     {
/* 402 */       this.types = ListeningWhitelist.safeSet(types);
/* 403 */       return this;
/*     */     }
/*     */ 
/*     */     public Builder gamePhase(GamePhase gamePhase)
/*     */     {
/* 412 */       this.gamePhase = gamePhase;
/* 413 */       return this;
/*     */     }
/*     */ 
/*     */     public Builder gamePhaseBoth()
/*     */     {
/* 421 */       return gamePhase(GamePhase.BOTH);
/*     */     }
/*     */ 
/*     */     public Builder options(Set<ListenerOptions> options)
/*     */     {
/* 429 */       this.options = ListeningWhitelist.safeSet(options);
/* 430 */       return this;
/*     */     }
/*     */ 
/*     */     public Builder options(Collection<ListenerOptions> options)
/*     */     {
/* 439 */       this.options = ListeningWhitelist.safeSet(options);
/* 440 */       return this;
/*     */     }
/*     */ 
/*     */     public Builder options(ListenerOptions[] serverOptions)
/*     */     {
/* 449 */       this.options = ListeningWhitelist.safeSet(Sets.newHashSet(serverOptions));
/* 450 */       return this;
/*     */     }
/*     */ 
/*     */     public Builder mergeOptions(ListenerOptions[] serverOptions)
/*     */     {
/* 459 */       return mergeOptions(Arrays.asList(serverOptions));
/*     */     }
/*     */ 
/*     */     public Builder mergeOptions(Collection<ListenerOptions> serverOptions)
/*     */     {
/* 468 */       if (this.options == null) {
/* 469 */         return options(serverOptions);
/*     */       }
/*     */ 
/* 472 */       this.options.addAll(serverOptions);
/* 473 */       return this;
/*     */     }
/*     */ 
/*     */     public ListeningWhitelist build()
/*     */     {
/* 481 */       return new ListeningWhitelist(this, null);
/*     */     }
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.events.ListeningWhitelist
 * JD-Core Version:    0.6.2
 */