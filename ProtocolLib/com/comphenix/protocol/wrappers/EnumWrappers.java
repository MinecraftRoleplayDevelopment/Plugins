/*     */ package com.comphenix.protocol.wrappers;
/*     */ 
/*     */ import com.comphenix.protocol.PacketType;
/*     */ import com.comphenix.protocol.PacketType.Handshake.Client;
/*     */ import com.comphenix.protocol.PacketType.Play.Client;
/*     */ import com.comphenix.protocol.PacketType.Play.Server;
/*     */ import com.comphenix.protocol.PacketType.Protocol;
/*     */ import com.comphenix.protocol.reflect.EquivalentConverter;
/*     */ import com.comphenix.protocol.reflect.FuzzyReflection;
/*     */ import com.comphenix.protocol.utility.MinecraftReflection;
/*     */ import com.google.common.collect.Maps;
/*     */ import java.lang.reflect.Field;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import org.bukkit.GameMode;
/*     */ 
/*     */ public abstract class EnumWrappers
/*     */ {
/* 243 */   private static Class<?> PROTOCOL_CLASS = null;
/* 244 */   private static Class<?> CLIENT_COMMAND_CLASS = null;
/* 245 */   private static Class<?> CHAT_VISIBILITY_CLASS = null;
/* 246 */   private static Class<?> DIFFICULTY_CLASS = null;
/* 247 */   private static Class<?> ENTITY_USE_ACTION_CLASS = null;
/* 248 */   private static Class<?> GAMEMODE_CLASS = null;
/* 249 */   private static Class<?> RESOURCE_PACK_STATUS_CLASS = null;
/* 250 */   private static Class<?> PLAYER_INFO_ACTION_CLASS = null;
/* 251 */   private static Class<?> TITLE_ACTION_CLASS = null;
/* 252 */   private static Class<?> WORLD_BORDER_ACTION_CLASS = null;
/* 253 */   private static Class<?> COMBAT_EVENT_TYPE_CLASS = null;
/* 254 */   private static Class<?> PLAYER_DIG_TYPE_CLASS = null;
/* 255 */   private static Class<?> PLAYER_ACTION_CLASS = null;
/* 256 */   private static Class<?> SCOREBOARD_ACTION_CLASS = null;
/* 257 */   private static Class<?> PARTICLE_CLASS = null;
/*     */ 
/* 259 */   private static boolean INITIALIZED = false;
/* 260 */   private static Map<Class<?>, EquivalentConverter<?>> FROM_NATIVE = Maps.newHashMap();
/* 261 */   private static Map<Class<?>, EquivalentConverter<?>> FROM_WRAPPER = Maps.newHashMap();
/*     */ 
/*     */   private static void initialize()
/*     */   {
/* 267 */     if (!MinecraftReflection.isUsingNetty()) {
/* 268 */       throw new IllegalArgumentException("Not supported on 1.6.4 and earlier.");
/*     */     }
/* 270 */     if (INITIALIZED) {
/* 271 */       return;
/*     */     }
/* 273 */     PROTOCOL_CLASS = getEnum(PacketType.Handshake.Client.SET_PROTOCOL.getPacketClass(), 0);
/* 274 */     CLIENT_COMMAND_CLASS = getEnum(PacketType.Play.Client.CLIENT_COMMAND.getPacketClass(), 0);
/* 275 */     CHAT_VISIBILITY_CLASS = getEnum(PacketType.Play.Client.SETTINGS.getPacketClass(), 0);
/* 276 */     DIFFICULTY_CLASS = getEnum(PacketType.Play.Server.LOGIN.getPacketClass(), 1);
/* 277 */     ENTITY_USE_ACTION_CLASS = getEnum(PacketType.Play.Client.USE_ENTITY.getPacketClass(), 0);
/* 278 */     GAMEMODE_CLASS = getEnum(PacketType.Play.Server.LOGIN.getPacketClass(), 0);
/* 279 */     RESOURCE_PACK_STATUS_CLASS = getEnum(PacketType.Play.Client.RESOURCE_PACK_STATUS.getPacketClass(), 0);
/* 280 */     PLAYER_INFO_ACTION_CLASS = getEnum(PacketType.Play.Server.PLAYER_INFO.getPacketClass(), 0);
/* 281 */     TITLE_ACTION_CLASS = getEnum(PacketType.Play.Server.TITLE.getPacketClass(), 0);
/* 282 */     WORLD_BORDER_ACTION_CLASS = getEnum(PacketType.Play.Server.WORLD_BORDER.getPacketClass(), 0);
/* 283 */     COMBAT_EVENT_TYPE_CLASS = getEnum(PacketType.Play.Server.COMBAT_EVENT.getPacketClass(), 0);
/* 284 */     PLAYER_DIG_TYPE_CLASS = getEnum(PacketType.Play.Client.BLOCK_DIG.getPacketClass(), 1);
/* 285 */     PLAYER_ACTION_CLASS = getEnum(PacketType.Play.Client.ENTITY_ACTION.getPacketClass(), 0);
/* 286 */     SCOREBOARD_ACTION_CLASS = getEnum(PacketType.Play.Server.SCOREBOARD_SCORE.getPacketClass(), 0);
/* 287 */     PARTICLE_CLASS = getEnum(PacketType.Play.Server.WORLD_PARTICLES.getPacketClass(), 0);
/*     */ 
/* 289 */     associate(PROTOCOL_CLASS, PacketType.Protocol.class, getClientCommandConverter());
/* 290 */     associate(CLIENT_COMMAND_CLASS, ClientCommand.class, getClientCommandConverter());
/* 291 */     associate(CHAT_VISIBILITY_CLASS, ChatVisibility.class, getChatVisibilityConverter());
/* 292 */     associate(DIFFICULTY_CLASS, Difficulty.class, getDifficultyConverter());
/* 293 */     associate(ENTITY_USE_ACTION_CLASS, EntityUseAction.class, getEntityUseActionConverter());
/* 294 */     associate(GAMEMODE_CLASS, NativeGameMode.class, getGameModeConverter());
/* 295 */     associate(RESOURCE_PACK_STATUS_CLASS, ResourcePackStatus.class, getResourcePackStatusConverter());
/* 296 */     associate(PLAYER_INFO_ACTION_CLASS, PlayerInfoAction.class, getPlayerInfoActionConverter());
/* 297 */     associate(TITLE_ACTION_CLASS, TitleAction.class, getTitleActionConverter());
/* 298 */     associate(WORLD_BORDER_ACTION_CLASS, WorldBorderAction.class, getWorldBorderActionConverter());
/* 299 */     associate(COMBAT_EVENT_TYPE_CLASS, CombatEventType.class, getCombatEventTypeConverter());
/* 300 */     associate(PLAYER_DIG_TYPE_CLASS, PlayerDigType.class, getPlayerDiggingActionConverter());
/* 301 */     associate(PLAYER_ACTION_CLASS, PlayerAction.class, getEntityActionConverter());
/* 302 */     associate(SCOREBOARD_ACTION_CLASS, ScoreboardAction.class, getUpdateScoreActionConverter());
/* 303 */     associate(PARTICLE_CLASS, Particle.class, getParticleConverter());
/* 304 */     INITIALIZED = true;
/*     */   }
/*     */ 
/*     */   private static void associate(Class<?> nativeClass, Class<?> wrapperClass, EquivalentConverter<?> converter) {
/* 308 */     FROM_NATIVE.put(nativeClass, converter);
/* 309 */     FROM_WRAPPER.put(wrapperClass, converter);
/*     */   }
/*     */ 
/*     */   private static Class<?> getEnum(Class<?> clazz, int index)
/*     */   {
/* 319 */     return ((Field)FuzzyReflection.fromClass(clazz, true).getFieldListByType(Enum.class).get(index)).getType();
/*     */   }
/*     */ 
/*     */   public static Map<Class<?>, EquivalentConverter<?>> getFromNativeMap() {
/* 323 */     return FROM_NATIVE;
/*     */   }
/*     */ 
/*     */   public static Map<Class<?>, EquivalentConverter<?>> getFromWrapperMap() {
/* 327 */     return FROM_WRAPPER;
/*     */   }
/*     */ 
/*     */   public static Class<?> getProtocolClass()
/*     */   {
/* 332 */     initialize();
/* 333 */     return PROTOCOL_CLASS;
/*     */   }
/*     */ 
/*     */   public static Class<?> getClientCommandClass() {
/* 337 */     initialize();
/* 338 */     return CLIENT_COMMAND_CLASS;
/*     */   }
/*     */ 
/*     */   public static Class<?> getChatVisibilityClass() {
/* 342 */     initialize();
/* 343 */     return CHAT_VISIBILITY_CLASS;
/*     */   }
/*     */ 
/*     */   public static Class<?> getDifficultyClass() {
/* 347 */     initialize();
/* 348 */     return DIFFICULTY_CLASS;
/*     */   }
/*     */ 
/*     */   public static Class<?> getEntityUseActionClass() {
/* 352 */     initialize();
/* 353 */     return ENTITY_USE_ACTION_CLASS;
/*     */   }
/*     */ 
/*     */   public static Class<?> getGameModeClass() {
/* 357 */     initialize();
/* 358 */     return GAMEMODE_CLASS;
/*     */   }
/*     */ 
/*     */   public static Class<?> getResourcePackStatusClass() {
/* 362 */     initialize();
/* 363 */     return RESOURCE_PACK_STATUS_CLASS;
/*     */   }
/*     */ 
/*     */   public static Class<?> getPlayerInfoActionClass() {
/* 367 */     initialize();
/* 368 */     return PLAYER_INFO_ACTION_CLASS;
/*     */   }
/*     */ 
/*     */   public static Class<?> getTitleActionClass() {
/* 372 */     initialize();
/* 373 */     return TITLE_ACTION_CLASS;
/*     */   }
/*     */ 
/*     */   public static Class<?> getWorldBorderActionClass() {
/* 377 */     initialize();
/* 378 */     return WORLD_BORDER_ACTION_CLASS;
/*     */   }
/*     */ 
/*     */   public static Class<?> getCombatEventTypeClass() {
/* 382 */     initialize();
/* 383 */     return COMBAT_EVENT_TYPE_CLASS;
/*     */   }
/*     */ 
/*     */   public static Class<?> getPlayerDigTypeClass() {
/* 387 */     initialize();
/* 388 */     return PLAYER_DIG_TYPE_CLASS;
/*     */   }
/*     */ 
/*     */   public static Class<?> getPlayerActionClass() {
/* 392 */     initialize();
/* 393 */     return PLAYER_ACTION_CLASS;
/*     */   }
/*     */ 
/*     */   public static Class<?> getScoreboardActionClass() {
/* 397 */     initialize();
/* 398 */     return SCOREBOARD_ACTION_CLASS;
/*     */   }
/*     */ 
/*     */   public static Class<?> getParticleClass() {
/* 402 */     initialize();
/* 403 */     return PARTICLE_CLASS;
/*     */   }
/*     */ 
/*     */   public static EquivalentConverter<PacketType.Protocol> getProtocolConverter()
/*     */   {
/* 408 */     return new EnumConverter(PacketType.Protocol.class);
/*     */   }
/*     */ 
/*     */   public static EquivalentConverter<ClientCommand> getClientCommandConverter() {
/* 412 */     return new EnumConverter(ClientCommand.class);
/*     */   }
/*     */ 
/*     */   public static EquivalentConverter<ChatVisibility> getChatVisibilityConverter() {
/* 416 */     return new EnumConverter(ChatVisibility.class);
/*     */   }
/*     */ 
/*     */   public static EquivalentConverter<Difficulty> getDifficultyConverter() {
/* 420 */     return new EnumConverter(Difficulty.class);
/*     */   }
/*     */ 
/*     */   public static EquivalentConverter<EntityUseAction> getEntityUseActionConverter() {
/* 424 */     return new EnumConverter(EntityUseAction.class);
/*     */   }
/*     */ 
/*     */   public static EquivalentConverter<NativeGameMode> getGameModeConverter() {
/* 428 */     return new EnumConverter(NativeGameMode.class);
/*     */   }
/*     */ 
/*     */   public static EquivalentConverter<ResourcePackStatus> getResourcePackStatusConverter() {
/* 432 */     return new EnumConverter(ResourcePackStatus.class);
/*     */   }
/*     */ 
/*     */   public static EquivalentConverter<PlayerInfoAction> getPlayerInfoActionConverter() {
/* 436 */     return new EnumConverter(PlayerInfoAction.class);
/*     */   }
/*     */ 
/*     */   public static EquivalentConverter<TitleAction> getTitleActionConverter() {
/* 440 */     return new EnumConverter(TitleAction.class);
/*     */   }
/*     */ 
/*     */   public static EquivalentConverter<WorldBorderAction> getWorldBorderActionConverter() {
/* 444 */     return new EnumConverter(WorldBorderAction.class);
/*     */   }
/*     */ 
/*     */   public static EquivalentConverter<CombatEventType> getCombatEventTypeConverter() {
/* 448 */     return new EnumConverter(CombatEventType.class);
/*     */   }
/*     */ 
/*     */   public static EquivalentConverter<PlayerDigType> getPlayerDiggingActionConverter() {
/* 452 */     return new EnumConverter(PlayerDigType.class);
/*     */   }
/*     */ 
/*     */   public static EquivalentConverter<PlayerAction> getEntityActionConverter() {
/* 456 */     return new EnumConverter(PlayerAction.class);
/*     */   }
/*     */ 
/*     */   public static EquivalentConverter<ScoreboardAction> getUpdateScoreActionConverter() {
/* 460 */     return new EnumConverter(ScoreboardAction.class);
/*     */   }
/*     */ 
/*     */   public static EquivalentConverter<Particle> getParticleConverter() {
/* 464 */     return new EnumConverter(Particle.class);
/*     */   }
/*     */ 
/*     */   private static class EnumConverter<T extends Enum<T>> implements EquivalentConverter<T>
/*     */   {
/*     */     private Class<T> specificType;
/*     */ 
/*     */     public EnumConverter(Class<T> specificType)
/*     */     {
/* 473 */       this.specificType = specificType;
/*     */     }
/*     */ 
/*     */     public T getSpecific(Object generic)
/*     */     {
/* 479 */       return Enum.valueOf(this.specificType, ((Enum)generic).name());
/*     */     }
/*     */ 
/*     */     public Object getGeneric(Class<?> genericType, T specific)
/*     */     {
/* 484 */       return Enum.valueOf(genericType, specific.name());
/*     */     }
/*     */ 
/*     */     public Class<T> getSpecificType()
/*     */     {
/* 489 */       return this.specificType;
/*     */     }
/*     */   }
/*     */ 
/*     */   public static enum Particle
/*     */   {
/* 171 */     EXPLOSION_NORMAL(0, true), 
/* 172 */     EXPLOSION_LARGE(1, true), 
/* 173 */     EXPLOSION_HUGE(2, true), 
/* 174 */     FIREWORKS_SPARK(3, false), 
/* 175 */     WATER_BUBBLE(4, false), 
/* 176 */     WATER_SPLASH(5, false), 
/* 177 */     WATER_WAKE(6, false), 
/* 178 */     SUSPENDED(7, false), 
/* 179 */     SUSPENDED_DEPTH(8, false), 
/* 180 */     CRIT(9, false), 
/* 181 */     CRIT_MAGIC(10, false), 
/* 182 */     SMOKE_NORMAL(11, false), 
/* 183 */     SMOKE_LARGE(12, false), 
/* 184 */     SPELL(13, false), 
/* 185 */     SPELL_INSTANT(14, false), 
/* 186 */     SPELL_MOB(15, false), 
/* 187 */     SPELL_MOB_AMBIENT(16, false), 
/* 188 */     SPELL_WITCH(17, false), 
/* 189 */     DRIP_WATER(18, false), 
/* 190 */     DRIP_LAVA(19, false), 
/* 191 */     VILLAGER_ANGRY(20, false), 
/* 192 */     VILLAGER_HAPPY(21, false), 
/* 193 */     TOWN_AURA(22, false), 
/* 194 */     NOTE(23, false), 
/* 195 */     PORTAL(24, false), 
/* 196 */     ENCHANTMENT_TABLE(25, false), 
/* 197 */     FLAME(26, false), 
/* 198 */     LAVA(27, false), 
/* 199 */     FOOTSTEP(28, false), 
/* 200 */     CLOUD(29, false), 
/* 201 */     REDSTONE(30, false), 
/* 202 */     SNOWBALL(31, false), 
/* 203 */     SNOW_SHOVEL(32, false), 
/* 204 */     SLIME(33, false), 
/* 205 */     HEART(34, false), 
/* 206 */     BARRIER(35, false), 
/* 207 */     ITEM_CRACK(36, false), 
/* 208 */     BLOCK_CRACK(37, false), 
/* 209 */     BLOCK_DUST(38, false), 
/* 210 */     WATER_DROP(39, false), 
/* 211 */     ITEM_TAKE(40, false), 
/* 212 */     MOB_APPEARANCE(41, true);
/*     */ 
/*     */     private static final Map<Integer, Particle> BY_ID;
/*     */     private final int id;
/*     */     private final boolean longDistance;
/*     */ 
/*     */     private Particle(int id, boolean longDistance)
/*     */     {
/* 226 */       this.id = id;
/* 227 */       this.longDistance = longDistance;
/*     */     }
/*     */ 
/*     */     public int getId() {
/* 231 */       return this.id;
/*     */     }
/*     */ 
/*     */     public boolean isLongDistance() {
/* 235 */       return this.longDistance;
/*     */     }
/*     */ 
/*     */     public static Particle getById(int id) {
/* 239 */       return (Particle)BY_ID.get(Integer.valueOf(id));
/*     */     }
/*     */ 
/*     */     static
/*     */     {
/* 216 */       BY_ID = new HashMap();
/* 217 */       for (Particle particle : values())
/* 218 */         BY_ID.put(Integer.valueOf(particle.getId()), particle);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static enum ScoreboardAction
/*     */   {
/* 166 */     CHANGE, 
/* 167 */     REMOVE;
/*     */   }
/*     */ 
/*     */   public static enum PlayerAction
/*     */   {
/* 156 */     START_SNEAKING, 
/* 157 */     STOP_SNEAKING, 
/* 158 */     STOP_SLEEPING, 
/* 159 */     START_SPRINTING, 
/* 160 */     STOP_SPRINTING, 
/* 161 */     RIDING_JUMP, 
/* 162 */     OPEN_INVENTORY;
/*     */   }
/*     */ 
/*     */   public static enum PlayerDigType
/*     */   {
/* 147 */     START_DESTROY_BLOCK, 
/* 148 */     ABORT_DESTROY_BLOCK, 
/* 149 */     STOP_DESTROY_BLOCK, 
/* 150 */     DROP_ALL_ITEMS, 
/* 151 */     DROP_ITEM, 
/* 152 */     RELEASE_USE_ITEM;
/*     */   }
/*     */ 
/*     */   public static enum CombatEventType
/*     */   {
/* 141 */     ENTER_COMBAT, 
/* 142 */     END_COMBAT, 
/* 143 */     ENTITY_DIED;
/*     */   }
/*     */ 
/*     */   public static enum WorldBorderAction
/*     */   {
/* 132 */     SET_SIZE, 
/* 133 */     LERP_SIZE, 
/* 134 */     SET_CENTER, 
/* 135 */     INITIALIZE, 
/* 136 */     SET_WARNING_TIME, 
/* 137 */     SET_WARNING_BLOCKS;
/*     */   }
/*     */ 
/*     */   public static enum TitleAction
/*     */   {
/* 124 */     TITLE, 
/* 125 */     SUBTITLE, 
/* 126 */     TIMES, 
/* 127 */     CLEAR, 
/* 128 */     RESET;
/*     */   }
/*     */ 
/*     */   public static enum PlayerInfoAction
/*     */   {
/* 116 */     ADD_PLAYER, 
/* 117 */     UPDATE_GAME_MODE, 
/* 118 */     UPDATE_LATENCY, 
/* 119 */     UPDATE_DISPLAY_NAME, 
/* 120 */     REMOVE_PLAYER;
/*     */   }
/*     */ 
/*     */   public static enum ResourcePackStatus
/*     */   {
/* 109 */     SUCCESSFULLY_LOADED, 
/* 110 */     DECLINED, 
/* 111 */     FAILED_DOWNLOAD, 
/* 112 */     ACCEPTED;
/*     */   }
/*     */ 
/*     */   public static enum NativeGameMode
/*     */   {
/*  53 */     NOT_SET, 
/*  54 */     SURVIVAL, 
/*  55 */     CREATIVE, 
/*  56 */     ADVENTURE, 
/*  57 */     SPECTATOR, 
/*     */ 
/*  62 */     NONE;
/*     */ 
/*     */     public GameMode toBukkit()
/*     */     {
/*  72 */       switch (EnumWrappers.1.$SwitchMap$com$comphenix$protocol$wrappers$EnumWrappers$NativeGameMode[ordinal()]) {
/*     */       case 1:
/*  74 */         return GameMode.ADVENTURE;
/*     */       case 2:
/*  76 */         return GameMode.CREATIVE;
/*     */       case 3:
/*  78 */         return GameMode.SPECTATOR;
/*     */       case 4:
/*  80 */         return GameMode.SURVIVAL;
/*     */       }
/*  82 */       return null;
/*     */     }
/*     */ 
/*     */     public static NativeGameMode fromBukkit(GameMode mode)
/*     */     {
/*  93 */       switch (EnumWrappers.1.$SwitchMap$org$bukkit$GameMode[mode.ordinal()]) {
/*     */       case 1:
/*  95 */         return ADVENTURE;
/*     */       case 2:
/*  97 */         return CREATIVE;
/*     */       case 3:
/*  99 */         return SPECTATOR;
/*     */       case 4:
/* 101 */         return SURVIVAL;
/*     */       }
/* 103 */       return null;
/*     */     }
/*     */   }
/*     */ 
/*     */   public static enum EntityUseAction
/*     */   {
/*  40 */     INTERACT, 
/*  41 */     ATTACK, 
/*  42 */     INTERACT_AT;
/*     */   }
/*     */ 
/*     */   public static enum Difficulty
/*     */   {
/*  33 */     PEACEFUL, 
/*  34 */     EASY, 
/*  35 */     NORMAL, 
/*  36 */     HARD;
/*     */   }
/*     */ 
/*     */   public static enum ChatVisibility
/*     */   {
/*  27 */     FULL, 
/*  28 */     SYSTEM, 
/*  29 */     HIDDEN;
/*     */   }
/*     */ 
/*     */   public static enum ClientCommand
/*     */   {
/*  21 */     PERFORM_RESPAWN, 
/*  22 */     REQUEST_STATS, 
/*  23 */     OPEN_INVENTORY_ACHIEVEMENT;
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.wrappers.EnumWrappers
 * JD-Core Version:    0.6.2
 */