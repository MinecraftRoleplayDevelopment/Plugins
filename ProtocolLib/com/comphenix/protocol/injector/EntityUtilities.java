/*     */ package com.comphenix.protocol.injector;
/*     */ 
/*     */ import com.comphenix.protocol.reflect.FieldAccessException;
/*     */ import com.comphenix.protocol.reflect.FieldUtils;
/*     */ import com.comphenix.protocol.reflect.FuzzyReflection;
/*     */ import com.comphenix.protocol.utility.MinecraftReflection;
/*     */ import com.comphenix.protocol.wrappers.WrappedIntHashMap;
/*     */ import com.google.common.collect.Lists;
/*     */ import java.lang.reflect.Constructor;
/*     */ import java.lang.reflect.Field;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.lang.reflect.Method;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Set;
/*     */ import org.bukkit.World;
/*     */ import org.bukkit.entity.Entity;
/*     */ import org.bukkit.entity.Player;
/*     */ 
/*     */ class EntityUtilities
/*     */ {
/*     */   private static Field entityTrackerField;
/*     */   private static Field trackedEntitiesField;
/*     */   private static Field trackedPlayersField;
/*     */   private static Field trackerField;
/*     */   private static Method scanPlayersMethod;
/*     */ 
/*     */   public static void updateEntity(Entity entity, List<Player> observers)
/*     */     throws FieldAccessException
/*     */   {
/*     */     try
/*     */     {
/*  89 */       Object trackerEntry = getEntityTrackerEntry(entity.getWorld(), entity.getEntityId());
/*     */ 
/*  91 */       if (trackedPlayersField == null)
/*     */       {
/*  93 */         trackedPlayersField = FuzzyReflection.fromObject(trackerEntry).getFieldByType("java\\.util\\..*");
/*     */       }
/*     */ 
/*  97 */       Collection trackedPlayers = (Collection)FieldUtils.readField(trackedPlayersField, trackerEntry, false);
/*  98 */       List nmsPlayers = unwrapBukkit(observers);
/*     */ 
/* 101 */       trackedPlayers.removeAll(nmsPlayers);
/*     */ 
/* 104 */       if (scanPlayersMethod == null) {
/* 105 */         scanPlayersMethod = trackerEntry.getClass().getMethod("scanPlayers", new Class[] { List.class });
/*     */       }
/*     */ 
/* 109 */       scanPlayersMethod.invoke(trackerEntry, new Object[] { nmsPlayers });
/*     */     }
/*     */     catch (IllegalArgumentException e) {
/* 112 */       throw e;
/*     */     } catch (IllegalAccessException e) {
/* 114 */       throw new FieldAccessException("Security limitation prevents access to 'get' method in IntHashMap", e);
/*     */     } catch (InvocationTargetException e) {
/* 116 */       throw new RuntimeException("Exception occurred in Minecraft.", e);
/*     */     } catch (SecurityException e) {
/* 118 */       throw new FieldAccessException("Security limitation prevents access to 'scanPlayers' method in trackerEntry.", e);
/*     */     } catch (NoSuchMethodException e) {
/* 120 */       throw new FieldAccessException("Cannot find 'scanPlayers' method. Is ProtocolLib up to date?", e);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static List<Player> getEntityTrackers(Entity entity)
/*     */   {
/*     */     try
/*     */     {
/* 132 */       List result = new ArrayList();
/* 133 */       Object trackerEntry = getEntityTrackerEntry(entity.getWorld(), entity.getEntityId());
/*     */ 
/* 135 */       if (trackerEntry == null) {
/* 136 */         throw new IllegalArgumentException("Cannot find entity trackers for " + entity + (entity.isDead() ? " - entity is dead." : "."));
/*     */       }
/*     */ 
/* 139 */       if (trackedPlayersField == null) {
/* 140 */         trackedPlayersField = FuzzyReflection.fromObject(trackerEntry).getFieldByType("java\\.util\\..*");
/*     */       }
/*     */ 
/* 143 */       Collection trackedPlayers = (Collection)FieldUtils.readField(trackedPlayersField, trackerEntry, false);
/*     */ 
/* 146 */       for (Iterator i$ = trackedPlayers.iterator(); i$.hasNext(); ) { Object tracker = i$.next();
/* 147 */         if (MinecraftReflection.isMinecraftPlayer(tracker)) {
/* 148 */           result.add((Player)MinecraftReflection.getBukkitEntity(tracker));
/*     */         }
/*     */       }
/* 151 */       return result;
/*     */     }
/*     */     catch (IllegalAccessException e) {
/* 154 */       throw new FieldAccessException("Security limitation prevented access to the list of tracked players.", e);
/*     */     }
/*     */   }
/*     */ 
/*     */   private static Object getEntityTrackerEntry(World world, int entityID)
/*     */     throws FieldAccessException, IllegalArgumentException
/*     */   {
/* 166 */     BukkitUnwrapper unwrapper = new BukkitUnwrapper();
/* 167 */     Object worldServer = unwrapper.unwrapItem(world);
/*     */ 
/* 169 */     if (entityTrackerField == null) {
/* 170 */       entityTrackerField = FuzzyReflection.fromObject(worldServer).getFieldByType("tracker", MinecraftReflection.getEntityTrackerClass());
/*     */     }
/*     */ 
/* 174 */     Object tracker = null;
/*     */     try
/*     */     {
/* 177 */       tracker = FieldUtils.readField(entityTrackerField, worldServer, false);
/*     */     } catch (IllegalAccessException e) {
/* 179 */       throw new FieldAccessException("Cannot access 'tracker' field due to security limitations.", e);
/*     */     }
/*     */ 
/* 182 */     if (trackedEntitiesField == null)
/*     */     {
/* 184 */       Set ignoredTypes = new HashSet();
/*     */ 
/* 188 */       for (Constructor constructor : tracker.getClass().getConstructors()) {
/* 189 */         for (Class type : constructor.getParameterTypes()) {
/* 190 */           ignoredTypes.add(type);
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 195 */       trackedEntitiesField = FuzzyReflection.fromObject(tracker, true).getFieldByType(MinecraftReflection.getMinecraftObjectRegex(), ignoredTypes);
/*     */     }
/*     */ 
/* 200 */     Object trackedEntities = null;
/*     */     try
/*     */     {
/* 203 */       trackedEntities = FieldUtils.readField(trackedEntitiesField, tracker, true);
/*     */     } catch (IllegalAccessException e) {
/* 205 */       throw new FieldAccessException("Cannot access 'trackedEntities' field due to security limitations.", e);
/*     */     }
/*     */ 
/* 208 */     return WrappedIntHashMap.fromHandle(trackedEntities).get(entityID);
/*     */   }
/*     */ 
/*     */   public static Entity getEntityFromID(World world, int entityID)
/*     */     throws FieldAccessException
/*     */   {
/*     */     try
/*     */     {
/* 218 */       Object trackerEntry = getEntityTrackerEntry(world, entityID);
/* 219 */       Object tracker = null;
/*     */ 
/* 222 */       if (trackerEntry != null) {
/* 223 */         if (trackerField == null) {
/*     */           try {
/* 225 */             trackerField = trackerEntry.getClass().getField("tracker");
/*     */           }
/*     */           catch (NoSuchFieldException e) {
/* 228 */             trackerField = FuzzyReflection.fromObject(trackerEntry).getFieldByType("tracker", MinecraftReflection.getEntityClass());
/*     */           }
/*     */ 
/*     */         }
/*     */ 
/* 233 */         tracker = FieldUtils.readField(trackerField, trackerEntry, true);
/*     */       }
/*     */ 
/* 237 */       if (tracker != null) {
/* 238 */         return (Entity)MinecraftReflection.getBukkitEntity(tracker);
/*     */       }
/* 240 */       return null;
/*     */     }
/*     */     catch (Exception e) {
/* 243 */       throw new FieldAccessException("Cannot find entity from ID " + entityID + ".", e);
/*     */     }
/*     */   }
/*     */ 
/*     */   private static List<Object> unwrapBukkit(List<Player> players)
/*     */   {
/* 249 */     List output = Lists.newArrayList();
/* 250 */     BukkitUnwrapper unwrapper = new BukkitUnwrapper();
/*     */ 
/* 253 */     for (Player player : players) {
/* 254 */       Object result = unwrapper.unwrapItem(player);
/*     */ 
/* 256 */       if (result != null)
/* 257 */         output.add(result);
/*     */       else {
/* 259 */         throw new IllegalArgumentException("Cannot unwrap item " + player);
/*     */       }
/*     */     }
/* 262 */     return output;
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.injector.EntityUtilities
 * JD-Core Version:    0.6.2
 */