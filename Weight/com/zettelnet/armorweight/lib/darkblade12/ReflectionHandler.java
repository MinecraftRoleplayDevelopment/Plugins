/*     */ package com.zettelnet.armorweight.lib.darkblade12;
/*     */ 
/*     */ import java.lang.reflect.Constructor;
/*     */ import java.lang.reflect.Field;
/*     */ import java.lang.reflect.Method;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import org.bukkit.Bukkit;
/*     */ 
/*     */ public final class ReflectionHandler
/*     */ {
/*     */   public static Class<?> getClass(String name, PackageType type)
/*     */     throws Exception
/*     */   {
/*  28 */     return Class.forName(type + "." + name);
/*     */   }
/*     */ 
/*     */   public static Class<?> getClass(String name, SubPackageType type) throws Exception {
/*  32 */     return Class.forName(type + "." + name);
/*     */   }
/*     */ 
/*     */   public static Constructor<?> getConstructor(Class<?> clazz, Class<?>[] parameterTypes) {
/*  36 */     Class[] p = DataType.convertToPrimitive(parameterTypes);
/*  37 */     for (Constructor c : clazz.getConstructors())
/*  38 */       if (DataType.equalsArray(DataType.convertToPrimitive(c.getParameterTypes()), p))
/*  39 */         return c;
/*  40 */     return null;
/*     */   }
/*     */ 
/*     */   public static Constructor<?> getConstructor(String className, PackageType type, Class<?>[] parameterTypes) throws Exception {
/*  44 */     return getConstructor(getClass(className, type), parameterTypes);
/*     */   }
/*     */ 
/*     */   public static Constructor<?> getConstructor(String className, SubPackageType type, Class<?>[] parameterTypes) throws Exception {
/*  48 */     return getConstructor(getClass(className, type), parameterTypes);
/*     */   }
/*     */ 
/*     */   public static Object newInstance(Class<?> clazz, Object[] args) throws Exception {
/*  52 */     return getConstructor(clazz, DataType.convertToPrimitive(args)).newInstance(args);
/*     */   }
/*     */ 
/*     */   public static Object newInstance(String className, PackageType type, Object[] args) throws Exception {
/*  56 */     return newInstance(getClass(className, type), args);
/*     */   }
/*     */ 
/*     */   public static Object newInstance(String className, SubPackageType type, Object[] args) throws Exception {
/*  60 */     return newInstance(getClass(className, type), args);
/*     */   }
/*     */ 
/*     */   public static Method getMethod(Class<?> clazz, String name, Class<?>[] parameterTypes) {
/*  64 */     Class[] p = DataType.convertToPrimitive(parameterTypes);
/*  65 */     for (Method m : clazz.getMethods())
/*  66 */       if ((m.getName().equals(name)) && (DataType.equalsArray(DataType.convertToPrimitive(m.getParameterTypes()), p)))
/*  67 */         return m;
/*  68 */     return null;
/*     */   }
/*     */ 
/*     */   public static Method getMethod(String className, PackageType type, String name, Class<?>[] parameterTypes) throws Exception {
/*  72 */     return getMethod(getClass(className, type), name, parameterTypes);
/*     */   }
/*     */ 
/*     */   public static Method getMethod(String className, SubPackageType type, String name, Class<?>[] parameterTypes) throws Exception {
/*  76 */     return getMethod(getClass(className, type), name, parameterTypes);
/*     */   }
/*     */ 
/*     */   public static Object invokeMethod(String name, Object instance, Object[] args) throws Exception {
/*  80 */     return getMethod(instance.getClass(), name, DataType.convertToPrimitive(args)).invoke(instance, args);
/*     */   }
/*     */ 
/*     */   public static Object invokeMethod(Class<?> clazz, String name, Object instance, Object[] args) throws Exception {
/*  84 */     return getMethod(clazz, name, DataType.convertToPrimitive(args)).invoke(instance, args);
/*     */   }
/*     */ 
/*     */   public static Object invokeMethod(String className, PackageType type, String name, Object instance, Object[] args) throws Exception {
/*  88 */     return invokeMethod(getClass(className, type), name, instance, args);
/*     */   }
/*     */ 
/*     */   public static Object invokeMethod(String className, SubPackageType type, String name, Object instance, Object[] args) throws Exception {
/*  92 */     return invokeMethod(getClass(className, type), name, instance, args);
/*     */   }
/*     */ 
/*     */   public static Field getField(Class<?> clazz, String name) throws Exception {
/*  96 */     Field f = clazz.getField(name);
/*  97 */     f.setAccessible(true);
/*  98 */     return f;
/*     */   }
/*     */ 
/*     */   public static Field getField(String className, PackageType type, String name) throws Exception {
/* 102 */     return getField(getClass(className, type), name);
/*     */   }
/*     */ 
/*     */   public static Field getField(String className, SubPackageType type, String name) throws Exception {
/* 106 */     return getField(getClass(className, type), name);
/*     */   }
/*     */ 
/*     */   public static Field getDeclaredField(Class<?> clazz, String name) throws Exception {
/* 110 */     Field f = clazz.getDeclaredField(name);
/* 111 */     f.setAccessible(true);
/* 112 */     return f;
/*     */   }
/*     */ 
/*     */   public static Field getDeclaredField(String className, PackageType type, String name) throws Exception {
/* 116 */     return getDeclaredField(getClass(className, type), name);
/*     */   }
/*     */ 
/*     */   public static Field getDeclaredField(String className, SubPackageType type, String name) throws Exception {
/* 120 */     return getDeclaredField(getClass(className, type), name);
/*     */   }
/*     */ 
/*     */   public static Object getValue(Object instance, String fieldName) throws Exception {
/* 124 */     return getField(instance.getClass(), fieldName).get(instance);
/*     */   }
/*     */ 
/*     */   public static Object getValue(Class<?> clazz, Object instance, String fieldName) throws Exception {
/* 128 */     return getField(clazz, fieldName).get(instance);
/*     */   }
/*     */ 
/*     */   public static Object getValue(String className, PackageType type, Object instance, String fieldName) throws Exception {
/* 132 */     return getValue(getClass(className, type), instance, fieldName);
/*     */   }
/*     */ 
/*     */   public static Object getValue(String className, SubPackageType type, Object instance, String fieldName) throws Exception {
/* 136 */     return getValue(getClass(className, type), instance, fieldName);
/*     */   }
/*     */ 
/*     */   public static Object getDeclaredValue(Object instance, String fieldName) throws Exception {
/* 140 */     return getDeclaredField(instance.getClass(), fieldName).get(instance);
/*     */   }
/*     */ 
/*     */   public static Object getDeclaredValue(Class<?> clazz, Object instance, String fieldName) throws Exception {
/* 144 */     return getDeclaredField(clazz, fieldName).get(instance);
/*     */   }
/*     */ 
/*     */   public static Object getDeclaredValue(String className, PackageType type, Object instance, String fieldName) throws Exception {
/* 148 */     return getDeclaredValue(getClass(className, type), instance, fieldName);
/*     */   }
/*     */ 
/*     */   public static Object getDeclaredValue(String className, SubPackageType type, Object instance, String fieldName) throws Exception {
/* 152 */     return getDeclaredValue(getClass(className, type), instance, fieldName);
/*     */   }
/*     */ 
/*     */   public static void setValue(Object instance, String fieldName, Object fieldValue) throws Exception {
/* 156 */     Field f = getField(instance.getClass(), fieldName);
/* 157 */     f.set(instance, fieldValue);
/*     */   }
/*     */ 
/*     */   public static void setValue(Object instance, FieldPair pair) throws Exception {
/* 161 */     setValue(instance, pair.getName(), pair.getValue());
/*     */   }
/*     */ 
/*     */   public static void setValue(Class<?> clazz, Object instance, String fieldName, Object fieldValue) throws Exception {
/* 165 */     Field f = getField(clazz, fieldName);
/* 166 */     f.set(instance, fieldValue);
/*     */   }
/*     */ 
/*     */   public static void setValue(Class<?> clazz, Object instance, FieldPair pair) throws Exception {
/* 170 */     setValue(clazz, instance, pair.getName(), pair.getValue());
/*     */   }
/*     */ 
/*     */   public static void setValue(String className, PackageType type, Object instance, String fieldName, Object fieldValue) throws Exception {
/* 174 */     setValue(getClass(className, type), instance, fieldName, fieldValue);
/*     */   }
/*     */ 
/*     */   public static void setValue(String className, PackageType type, Object instance, FieldPair pair) throws Exception {
/* 178 */     setValue(className, type, instance, pair.getName(), pair.getValue());
/*     */   }
/*     */ 
/*     */   public static void setValue(String className, SubPackageType type, Object instance, String fieldName, Object fieldValue) throws Exception {
/* 182 */     setValue(getClass(className, type), instance, fieldName, fieldValue);
/*     */   }
/*     */ 
/*     */   public static void setValue(String className, SubPackageType type, Object instance, FieldPair pair) throws Exception {
/* 186 */     setValue(className, type, instance, pair.getName(), pair.getValue());
/*     */   }
/*     */ 
/*     */   public static void setValues(Object instance, FieldPair[] pairs) throws Exception {
/* 190 */     for (FieldPair pair : pairs)
/* 191 */       setValue(instance, pair);
/*     */   }
/*     */ 
/*     */   public static void setValues(Class<?> clazz, Object instance, FieldPair[] pairs) throws Exception {
/* 195 */     for (FieldPair pair : pairs)
/* 196 */       setValue(clazz, instance, pair);
/*     */   }
/*     */ 
/*     */   public static void setValues(String className, PackageType type, Object instance, FieldPair[] pairs) throws Exception {
/* 200 */     setValues(getClass(className, type), instance, pairs);
/*     */   }
/*     */ 
/*     */   public static void setValues(String className, SubPackageType type, Object instance, FieldPair[] pairs) throws Exception {
/* 204 */     setValues(getClass(className, type), instance, pairs);
/*     */   }
/*     */ 
/*     */   public static void setDeclaredValue(Object instance, String fieldName, Object fieldValue) throws Exception {
/* 208 */     Field f = getDeclaredField(instance.getClass(), fieldName);
/* 209 */     f.set(instance, fieldValue);
/*     */   }
/*     */ 
/*     */   public static void setDeclaredValue(Object instance, FieldPair pair) throws Exception {
/* 213 */     setDeclaredValue(instance, pair.getName(), pair.getValue());
/*     */   }
/*     */ 
/*     */   public static void setDeclaredValue(Class<?> clazz, Object instance, String fieldName, Object fieldValue) throws Exception {
/* 217 */     Field f = getDeclaredField(clazz, fieldName);
/* 218 */     f.set(instance, fieldValue);
/*     */   }
/*     */ 
/*     */   public static void setDeclaredValue(Class<?> clazz, Object instance, FieldPair pair) throws Exception {
/* 222 */     setDeclaredValue(clazz, instance, pair.getName(), pair.getValue());
/*     */   }
/*     */ 
/*     */   public static void setDeclaredValue(String className, PackageType type, Object instance, String fieldName, Object fieldValue) throws Exception {
/* 226 */     setDeclaredValue(getClass(className, type), instance, fieldName, fieldValue);
/*     */   }
/*     */ 
/*     */   public static void setDeclaredValue(String className, PackageType type, Object instance, FieldPair pair) throws Exception {
/* 230 */     setDeclaredValue(className, type, instance, pair.getName(), pair.getValue());
/*     */   }
/*     */ 
/*     */   public static void setDeclaredValue(String className, SubPackageType type, Object instance, String fieldName, Object fieldValue) throws Exception {
/* 234 */     setDeclaredValue(getClass(className, type), instance, fieldName, fieldValue);
/*     */   }
/*     */ 
/*     */   public static void setDeclaredValue(String className, SubPackageType type, Object instance, FieldPair pair) throws Exception {
/* 238 */     setDeclaredValue(className, type, instance, pair.getName(), pair.getValue());
/*     */   }
/*     */ 
/*     */   public static void setDeclaredValues(Object instance, FieldPair[] pairs) throws Exception {
/* 242 */     for (FieldPair pair : pairs)
/* 243 */       setDeclaredValue(instance, pair);
/*     */   }
/*     */ 
/*     */   public static void setDeclaredValues(Class<?> clazz, Object instance, FieldPair[] pairs) throws Exception {
/* 247 */     for (FieldPair pair : pairs)
/* 248 */       setDeclaredValue(clazz, instance, pair);
/*     */   }
/*     */ 
/*     */   public static void setDeclaredValues(String className, PackageType type, Object instance, FieldPair[] pairs) throws Exception {
/* 252 */     setDeclaredValues(getClass(className, type), instance, pairs);
/*     */   }
/*     */ 
/*     */   public static void setDeclaredValues(String className, SubPackageType type, Object instance, FieldPair[] pairs) throws Exception {
/* 256 */     setDeclaredValues(getClass(className, type), instance, pairs);
/*     */   }
/*     */ 
/*     */   public static enum PacketType
/*     */   {
/* 433 */     HANDSHAKING_IN_SET_PROTOCOL("PacketHandshakingInSetProtocol"), 
/* 434 */     LOGIN_IN_ENCRYPTION_BEGIN("PacketLoginInEncryptionBegin"), 
/* 435 */     LOGIN_IN_START("PacketLoginInStart"), 
/* 436 */     LOGIN_OUT_DISCONNECT("PacketLoginOutDisconnect"), 
/* 437 */     LOGIN_OUT_ENCRYPTION_BEGIN("PacketLoginOutEncryptionBegin"), 
/* 438 */     LOGIN_OUT_SUCCESS("PacketLoginOutSuccess"), 
/* 439 */     PLAY_IN_ABILITIES("PacketPlayInAbilities"), 
/* 440 */     PLAY_IN_ARM_ANIMATION("PacketPlayInArmAnimation"), 
/* 441 */     PLAY_IN_BLOCK_DIG("PacketPlayInBlockDig"), 
/* 442 */     PLAY_IN_BLOCK_PLACE("PacketPlayInBlockPlace"), 
/* 443 */     PLAY_IN_CHAT("PacketPlayInChat"), 
/* 444 */     PLAY_IN_CLIENT_COMMAND("PacketPlayInClientCommand"), 
/* 445 */     PLAY_IN_CLOSE_WINDOW("PacketPlayInCloseWindow"), 
/* 446 */     PLAY_IN_CUSTOM_PAYLOAD("PacketPlayInCustomPayload"), 
/* 447 */     PLAY_IN_ENCHANT_ITEM("PacketPlayInEnchantItem"), 
/* 448 */     PLAY_IN_ENTITY_ACTION("PacketPlayInEntityAction"), 
/* 449 */     PLAY_IN_FLYING("PacketPlayInFlying"), 
/* 450 */     PLAY_IN_HELD_ITEM_SLOT("PacketPlayInHeldItemSlot"), 
/* 451 */     PLAY_IN_KEEP_ALIVE("PacketPlayInKeepAlive"), 
/* 452 */     PLAY_IN_LOOK("PacketPlayInLook"), 
/* 453 */     PLAY_IN_POSITION("PacketPlayInPosition"), 
/* 454 */     PLAY_IN_POSITION_LOOK("PacketPlayInPositionLook"), 
/* 455 */     PLAY_IN_SET_CREATIVE_SLOT("PacketPlayInSetCreativeSlot "), 
/* 456 */     PLAY_IN_SETTINGS("PacketPlayInSettings"), 
/* 457 */     PLAY_IN_STEER_VEHICLE("PacketPlayInSteerVehicle"), 
/* 458 */     PLAY_IN_TAB_COMPLETE("PacketPlayInTabComplete"), 
/* 459 */     PLAY_IN_TRANSACTION("PacketPlayInTransaction"), 
/* 460 */     PLAY_IN_UPDATE_SIGN("PacketPlayInUpdateSign"), 
/* 461 */     PLAY_IN_USE_ENTITY("PacketPlayInUseEntity"), 
/* 462 */     PLAY_IN_WINDOW_CLICK("PacketPlayInWindowClick"), 
/* 463 */     PLAY_OUT_ABILITIES("PacketPlayOutAbilities"), 
/* 464 */     PLAY_OUT_ANIMATION("PacketPlayOutAnimation"), 
/* 465 */     PLAY_OUT_ATTACH_ENTITY("PacketPlayOutAttachEntity"), 
/* 466 */     PLAY_OUT_BED("PacketPlayOutBed"), 
/* 467 */     PLAY_OUT_BLOCK_ACTION("PacketPlayOutBlockAction"), 
/* 468 */     PLAY_OUT_BLOCK_BREAK_ANIMATION("PacketPlayOutBlockBreakAnimation"), 
/* 469 */     PLAY_OUT_BLOCK_CHANGE("PacketPlayOutBlockChange"), 
/* 470 */     PLAY_OUT_CHAT("PacketPlayOutChat"), 
/* 471 */     PLAY_OUT_CLOSE_WINDOW("PacketPlayOutCloseWindow"), 
/* 472 */     PLAY_OUT_COLLECT("PacketPlayOutCollect"), 
/* 473 */     PLAY_OUT_CRAFT_PROGRESS_BAR("PacketPlayOutCraftProgressBar"), 
/* 474 */     PLAY_OUT_CUSTOM_PAYLOAD("PacketPlayOutCustomPayload"), 
/* 475 */     PLAY_OUT_ENTITY("PacketPlayOutEntity"), 
/* 476 */     PLAY_OUT_ENTITY_DESTROY("PacketPlayOutEntityDestroy"), 
/* 477 */     PLAY_OUT_ENTITY_EFFECT("PacketPlayOutEntityEffect"), 
/* 478 */     PLAY_OUT_ENTITY_EQUIPMENT("PacketPlayOutEntityEquipment"), 
/* 479 */     PLAY_OUT_ENTITY_HEAD_ROTATION("PacketPlayOutEntityHeadRotation"), 
/* 480 */     PLAY_OUT_ENTITY_LOOK("PacketPlayOutEntityLook"), 
/* 481 */     PLAY_OUT_ENTITY_METADATA("PacketPlayOutEntityMetadata"), 
/* 482 */     PLAY_OUT_ENTITY_STATUS("PacketPlayOutEntityStatus"), 
/* 483 */     PLAY_OUT_ENTITY_TELEPORT("PacketPlayOutEntityTeleport"), 
/* 484 */     PLAY_OUT_ENTITY_VELOCITY("PacketPlayOutEntityVelocity"), 
/* 485 */     PLAY_OUT_EXPERIENCE("PacketPlayOutExperience"), 
/* 486 */     PLAY_OUT_EXPLOSION("PacketPlayOutExplosion"), 
/* 487 */     PLAY_OUT_GAME_STATE_CHANGE("PacketPlayOutGameStateChange"), 
/* 488 */     PLAY_OUT_HELD_ITEM_SLOT("PacketPlayOutHeldItemSlot"), 
/* 489 */     PLAY_OUT_KEEP_ALIVE("PacketPlayOutKeepAlive"), 
/* 490 */     PLAY_OUT_KICK_DISCONNECT("PacketPlayOutKickDisconnect"), 
/* 491 */     PLAY_OUT_LOGIN("PacketPlayOutLogin"), 
/* 492 */     PLAY_OUT_MAP("PacketPlayOutMap"), 
/* 493 */     PLAY_OUT_MAP_CHUNK("PacketPlayOutMapChunk"), 
/* 494 */     PLAY_OUT_MAP_CHUNK_BULK("PacketPlayOutMapChunkBulk"), 
/* 495 */     PLAY_OUT_MULTI_BLOCK_CHANGE("PacketPlayOutMultiBlockChange"), 
/* 496 */     PLAY_OUT_NAMED_ENTITY_SPAWN("PacketPlayOutNamedEntitySpawn"), 
/* 497 */     PLAY_OUT_NAMED_SOUND_EFFECT("PacketPlayOutNamedSoundEffect"), 
/* 498 */     PLAY_OUT_OPEN_SIGN_EDITOR("PacketPlayOutOpenSignEditor"), 
/* 499 */     PLAY_OUT_OPEN_WINDOW("PacketPlayOutOpenWindow"), 
/* 500 */     PLAY_OUT_PLAYER_INFO("PacketPlayOutPlayerInfo"), 
/* 501 */     PLAY_OUT_POSITION("PacketPlayOutPosition"), 
/* 502 */     PLAY_OUT_REL_ENTITY_MOVE("PacketPlayOutRelEntityMove"), 
/* 503 */     PLAY_OUT_REL_ENTITY_MOVE_LOOK("PacketPlayOutRelEntityMoveLook"), 
/* 504 */     PLAY_OUT_REMOVE_ENTITY_EFFECT("PacketPlayOutRemoveEntityEffect"), 
/* 505 */     PLAY_OUT_RESPAWN("PacketPlayOutRespawn"), 
/* 506 */     PLAY_OUT_SCOREBOARD_DISPLAY_OBJECTIVE("PacketPlayOutScoreboardDisplayObjective"), 
/* 507 */     PLAY_OUT_SCOREBOARD_OBJECTIVE("PacketPlayOutScoreboardObjective"), 
/* 508 */     PLAY_OUT_SCOREBOARD_SCORE("PacketPlayOutScoreboardScore"), 
/* 509 */     PLAY_OUT_SCOREBOARD_TEAM("PacketPlayOutScoreboardTeam"), 
/* 510 */     PLAY_OUT_SET_SLOT("PacketPlayOutSetSlot"), 
/* 511 */     PLAY_OUT_SPAWN_ENTITY("PacketPlayOutSpawnEntity"), 
/* 512 */     PLAY_OUT_SPAWN_ENTITY_EXPERIENCE_ORB("PacketPlayOutSpawnEntityExperienceOrb"), 
/* 513 */     PLAY_OUT_SPAWN_ENTITY_LIVING("PacketPlayOutSpawnEntityLiving"), 
/* 514 */     PLAY_OUT_SPAWN_ENTITY_PAINTING("PacketPlayOutSpawnEntityPainting"), 
/* 515 */     PLAY_OUT_SPAWN_ENTITY_WEATHER("PacketPlayOutSpawnEntityWeather"), 
/* 516 */     PLAY_OUT_SPAWN_POSITION("PacketPlayOutSpawnPosition"), 
/* 517 */     PLAY_OUT_STATISTIC("PacketPlayOutStatistic"), 
/* 518 */     PLAY_OUT_TAB_COMPLETE("PacketPlayOutTabComplete"), 
/* 519 */     PLAY_OUT_TILE_ENTITY_DATA("PacketPlayOutTileEntityData"), 
/* 520 */     PLAY_OUT_TRANSACTION("PacketPlayOutTransaction"), 
/* 521 */     PLAY_OUT_UPDATE_ATTRIBUTES("PacketPlayOutUpdateAttributes"), 
/* 522 */     PLAY_OUT_UPDATE_HEALTH("PacketPlayOutUpdateHealth"), 
/* 523 */     PLAY_OUT_UPDATE_SIGN("PacketPlayOutUpdateSign"), 
/* 524 */     PLAY_OUT_UPDATE_TIME("PacketPlayOutUpdateTime"), 
/* 525 */     PLAY_OUT_WINDOW_ITEMS("PacketPlayOutWindowItems"), 
/* 526 */     PLAY_OUT_WORLD_EVENT("PacketPlayOutWorldEvent"), 
/* 527 */     PLAY_OUT_WORLD_PARTICLES("PacketPlayOutWorldParticles"), 
/* 528 */     STATUS_IN_PING("PacketStatusInPing"), 
/* 529 */     STATUS_IN_START("PacketStatusInStart"), 
/* 530 */     STATUS_OUT_PONG("PacketStatusOutPong"), 
/* 531 */     STATUS_OUT_SERVER_INFO("PacketStatusOutServerInfo");
/*     */ 
/*     */     private final String name;
/*     */     private Class<?> packet;
/*     */ 
/* 537 */     private PacketType(String name) { this.name = name; }
/*     */ 
/*     */     public String getName()
/*     */     {
/* 541 */       return getName();
/*     */     }
/*     */ 
/*     */     public Class<?> getPacket() throws Exception {
/* 545 */       return this.packet == null ? (this.packet = ReflectionHandler.getClass(this.name, ReflectionHandler.PackageType.MINECRAFT_SERVER)) : this.packet;
/*     */     }
/*     */   }
/*     */ 
/*     */   public static enum SubPackageType
/*     */   {
/* 392 */     BLOCK, 
/* 393 */     CHUNKIO, 
/* 394 */     COMMAND, 
/* 395 */     CONVERSATIONS, 
/* 396 */     ENCHANTMENS, 
/* 397 */     ENTITY, 
/* 398 */     EVENT, 
/* 399 */     GENERATOR, 
/* 400 */     HELP, 
/* 401 */     INVENTORY, 
/* 402 */     MAP, 
/* 403 */     METADATA, 
/* 404 */     POTION, 
/* 405 */     PROJECTILES, 
/* 406 */     SCHEDULER, 
/* 407 */     SCOREBOARD, 
/* 408 */     UPDATER, 
/* 409 */     UTIL;
/*     */ 
/*     */     private final String name;
/*     */ 
/*     */     private SubPackageType() {
/* 414 */       this.name = (ReflectionHandler.PackageType.CRAFTBUKKIT + "." + name().toLowerCase());
/*     */     }
/*     */ 
/*     */     public String getName() {
/* 418 */       return this.name;
/*     */     }
/*     */ 
/*     */     public String toString()
/*     */     {
/* 423 */       return this.name;
/*     */     }
/*     */   }
/*     */ 
/*     */   public static enum PackageType
/*     */   {
/* 367 */     MINECRAFT_SERVER("net.minecraft.server." + Bukkit.getServer().getClass().getPackage().getName().substring(23)), 
/* 368 */     CRAFTBUKKIT(Bukkit.getServer().getClass().getPackage().getName());
/*     */ 
/*     */     private final String name;
/*     */ 
/*     */     private PackageType(String name) {
/* 373 */       this.name = name;
/*     */     }
/*     */ 
/*     */     public String getName() {
/* 377 */       return this.name;
/*     */     }
/*     */ 
/*     */     public String toString()
/*     */     {
/* 382 */       return this.name;
/*     */     }
/*     */   }
/*     */ 
/*     */   public final class FieldPair
/*     */   {
/*     */     private final String name;
/*     */     private final Object value;
/*     */ 
/*     */     public FieldPair(String name, Object value)
/*     */     {
/* 348 */       this.name = name;
/* 349 */       this.value = value;
/*     */     }
/*     */ 
/*     */     public String getName() {
/* 353 */       return this.name;
/*     */     }
/*     */ 
/*     */     public Object getValue() {
/* 357 */       return this.value;
/*     */     }
/*     */   }
/*     */ 
/*     */   public static enum DataType
/*     */   {
/* 265 */     BYTE(Byte.TYPE, Byte.class), 
/* 266 */     SHORT(Short.TYPE, Short.class), 
/* 267 */     INTEGER(Integer.TYPE, Integer.class), 
/* 268 */     LONG(Long.TYPE, Long.class), 
/* 269 */     CHARACTER(Character.TYPE, Character.class), 
/* 270 */     FLOAT(Float.TYPE, Float.class), 
/* 271 */     DOUBLE(Double.TYPE, Double.class), 
/* 272 */     BOOLEAN(Boolean.TYPE, Boolean.class);
/*     */ 
/*     */     private static final Map<Class<?>, DataType> CLASS_MAP;
/*     */     private final Class<?> primitive;
/*     */     private final Class<?> reference;
/*     */ 
/*     */     private DataType(Class<?> primitive, Class<?> reference)
/*     */     {
/* 286 */       this.primitive = primitive;
/* 287 */       this.reference = reference;
/*     */     }
/*     */ 
/*     */     public Class<?> getPrimitive() {
/* 291 */       return this.primitive;
/*     */     }
/*     */ 
/*     */     public Class<?> getReference() {
/* 295 */       return this.reference;
/*     */     }
/*     */ 
/*     */     public static DataType fromClass(Class<?> c) {
/* 299 */       return (DataType)CLASS_MAP.get(c);
/*     */     }
/*     */ 
/*     */     public static Class<?> getPrimitive(Class<?> c) {
/* 303 */       DataType t = fromClass(c);
/* 304 */       return t == null ? c : t.getPrimitive();
/*     */     }
/*     */ 
/*     */     public static Class<?> getReference(Class<?> c) {
/* 308 */       DataType t = fromClass(c);
/* 309 */       return t == null ? c : t.getReference();
/*     */     }
/*     */ 
/*     */     public static Class<?>[] convertToPrimitive(Class<?>[] classes) {
/* 313 */       int length = classes == null ? 0 : classes.length;
/* 314 */       Class[] types = new Class[length];
/* 315 */       for (int i = 0; i < length; i++)
/* 316 */         types[i] = getPrimitive(classes[i]);
/* 317 */       return types;
/*     */     }
/*     */ 
/*     */     public static Class<?>[] convertToPrimitive(Object[] objects) {
/* 321 */       int length = objects == null ? 0 : objects.length;
/* 322 */       Class[] types = new Class[length];
/* 323 */       for (int i = 0; i < length; i++)
/* 324 */         types[i] = getPrimitive(objects[i].getClass());
/* 325 */       return types;
/*     */     }
/*     */ 
/*     */     public static boolean equalsArray(Class<?>[] a1, Class<?>[] a2) {
/* 329 */       if ((a1 == null) || (a2 == null) || (a1.length != a2.length))
/* 330 */         return false;
/* 331 */       for (int i = 0; i < a1.length; i++)
/* 332 */         if ((!a1[i].equals(a2[i])) && (!a1[i].isAssignableFrom(a2[i])))
/* 333 */           return false;
/* 334 */       return true;
/*     */     }
/*     */ 
/*     */     static
/*     */     {
/* 274 */       CLASS_MAP = new HashMap();
/*     */ 
/* 279 */       for (DataType t : values()) {
/* 280 */         CLASS_MAP.put(t.primitive, t);
/* 281 */         CLASS_MAP.put(t.reference, t);
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\ArmorWeight.jar
 * Qualified Name:     com.zettelnet.armorweight.lib.darkblade12.ReflectionHandler
 * JD-Core Version:    0.6.2
 */