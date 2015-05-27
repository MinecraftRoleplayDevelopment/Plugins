/*     */ package com.gmail.filoghost.healthbar;
/*     */ 
/*     */ import com.gmail.filoghost.healthbar.api.HealthBarAPI;
/*     */ import java.util.Arrays;
/*     */ import java.util.List;
/*     */ import org.bukkit.Bukkit;
/*     */ import org.bukkit.Chunk;
/*     */ import org.bukkit.Location;
/*     */ import org.bukkit.Material;
/*     */ import org.bukkit.Server;
/*     */ import org.bukkit.World;
/*     */ import org.bukkit.configuration.file.FileConfiguration;
/*     */ import org.bukkit.entity.Entity;
/*     */ import org.bukkit.entity.EntityType;
/*     */ import org.bukkit.entity.Horse;
/*     */ import org.bukkit.entity.Horse.Variant;
/*     */ import org.bukkit.entity.LivingEntity;
/*     */ import org.bukkit.entity.Player;
/*     */ import org.bukkit.entity.Vehicle;
/*     */ import org.bukkit.entity.Villager;
/*     */ import org.bukkit.event.EventHandler;
/*     */ import org.bukkit.event.EventPriority;
/*     */ import org.bukkit.event.Listener;
/*     */ import org.bukkit.event.player.PlayerInteractEntityEvent;
/*     */ import org.bukkit.event.player.PlayerJoinEvent;
/*     */ import org.bukkit.event.player.PlayerRespawnEvent;
/*     */ import org.bukkit.event.player.PlayerTeleportEvent;
/*     */ import org.bukkit.event.vehicle.VehicleEnterEvent;
/*     */ import org.bukkit.event.vehicle.VehicleExitEvent;
/*     */ import org.bukkit.event.world.ChunkLoadEvent;
/*     */ import org.bukkit.event.world.ChunkUnloadEvent;
/*     */ import org.bukkit.inventory.ItemStack;
/*     */ import org.bukkit.plugin.Plugin;
/*     */ import org.bukkit.scheduler.BukkitScheduler;
/*     */ import org.bukkit.scoreboard.Scoreboard;
/*     */ import org.bukkit.scoreboard.ScoreboardManager;
/*     */ 
/*     */ public class MiscListeners
/*     */   implements Listener
/*     */ {
/*  36 */   private static final Plugin instance = Main.plugin;
/*     */   private static boolean fixTabNames;
/*     */   private static boolean usePlayerPermissions;
/*  39 */   private static Scoreboard fakeSb = instance.getServer().getScoreboardManager().getNewScoreboard();
/*  40 */   private static Scoreboard mainSb = instance.getServer().getScoreboardManager().getMainScoreboard();
/*     */   private static boolean playerEnabled;
/*     */   private static int playerHideDelay;
/*     */   private static boolean playerUseAfter;
/*  45 */   private static boolean pluginDisabledWhiteTabNames = false;
/*     */ 
/*  47 */   private static BukkitScheduler scheduler = Bukkit.getScheduler();
/*     */   private static boolean overrideOtherScoreboards;
/*     */   private static boolean playerUseDisabledWorlds;
/*     */   private static List<String> playerDisabledWorlds;
/*     */ 
/*     */   @EventHandler(ignoreCancelled=true, priority=EventPriority.HIGHEST)
/*     */   public void onInteract(PlayerInteractEntityEvent event)
/*     */   {
/*  60 */     Entity entity = event.getRightClicked();
/*     */ 
/*  62 */     if ((event.getPlayer().getItemInHand().getType() == Material.NAME_TAG) && ((entity instanceof LivingEntity)))
/*     */     {
/*  64 */       final LivingEntity mob = (LivingEntity)entity;
/*     */ 
/*  66 */       if ((DamageListener.mobHideDelay == 0L) && (HealthBarAPI.mobHasBar(mob))) {
/*  67 */         scheduler.scheduleSyncDelayedTask(instance, new Runnable() {
/*  68 */           public void run() { DamageListener.parseMobHit(mob, true); }
/*     */ 
/*     */         }
/*     */         , 20L);
/*     */       }
/*     */ 
/*  72 */       DamageListener.hideBar(mob);
/*  73 */       mob.setCustomNameVisible(false);
/*  74 */       return;
/*     */     }
/*     */ 
/*  77 */     if ((entity instanceof Villager)) {
/*  78 */       Villager villager = (Villager)entity;
/*  79 */       if ((villager.isAdult()) && (HealthBarAPI.mobHasBar(villager)))
/*     */       {
/*  81 */         DamageListener.hideBar(villager);
/*     */ 
/*  83 */         if (DamageListener.mobHideDelay == 0L) {
/*  84 */           DamageListener.parseMobHit(villager, true);
/*     */         }
/*     */       }
/*     */     }
/*  88 */     else if ((entity instanceof Horse)) {
/*  89 */       final Horse horse = (Horse)entity;
/*     */ 
/*  91 */       if ((horse.getVariant() == Horse.Variant.DONKEY) || (horse.getVariant() == Horse.Variant.MULE))
/*     */       {
/*  93 */         if (HealthBarAPI.mobHasBar(horse)) {
/*  94 */           DamageListener.hideBar(horse);
/*  95 */           if (DamageListener.mobHideDelay == 0L)
/*  96 */             scheduler.scheduleSyncDelayedTask(Main.plugin, new Runnable() {
/*  97 */               public void run() { DamageListener.parseMobHit(horse, true); }
/*     */ 
/*     */             }
/*     */             , 1L);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   @EventHandler(ignoreCancelled=true, priority=EventPriority.HIGHEST)
/*     */   public void onVehicleEnter(VehicleEnterEvent event)
/*     */   {
/* 108 */     Vehicle vehicle = event.getVehicle();
/* 109 */     if ((vehicle instanceof Horse)) {
/* 110 */       Horse horse = (Horse)vehicle;
/*     */ 
/* 112 */       if (HealthBarAPI.mobHasBar(horse))
/* 113 */         DamageListener.hideBar(horse);
/*     */     }
/*     */   }
/*     */ 
/*     */   @EventHandler(ignoreCancelled=true, priority=EventPriority.HIGHEST)
/*     */   public void onVehicleLeave(VehicleExitEvent event)
/*     */   {
/* 120 */     Vehicle vehicle = event.getVehicle();
/* 121 */     if ((vehicle instanceof Horse)) {
/* 122 */       final Horse horse = (Horse)vehicle;
/*     */ 
/* 124 */       if (DamageListener.mobHideDelay == 0L)
/* 125 */         scheduler.scheduleSyncDelayedTask(instance, new Runnable() {
/* 126 */           public void run() { DamageListener.parseMobHit(horse, true); }
/*     */ 
/*     */         }
/*     */         , 1L);
/*     */     }
/*     */   }
/*     */ 
/*     */   @EventHandler
/*     */   public void onChunkLoad(ChunkLoadEvent event)
/*     */   {
/* 134 */     if ((DamageListener.mobHideDelay == 0L) && (DamageListener.mobEnabled))
/* 135 */       for (Entity entity : event.getChunk().getEntities())
/* 136 */         if (((entity instanceof LivingEntity)) && (entity.getType() != EntityType.PLAYER))
/* 137 */           DamageListener.parseMobHit((LivingEntity)entity, true);
/*     */   }
/*     */ 
/*     */   @EventHandler
/*     */   public void onChunkUnload(ChunkUnloadEvent event)
/*     */   {
/* 145 */     if (!DamageListener.mobEnabled) return;
/* 146 */     for (Entity entity : event.getChunk().getEntities())
/* 147 */       if (((entity instanceof LivingEntity)) && (entity.getType() != EntityType.PLAYER))
/* 148 */         DamageListener.hideBar((LivingEntity)entity);
/*     */   }
/*     */ 
/*     */   @EventHandler(priority=EventPriority.LOWEST)
/*     */   public void joinLowest(PlayerJoinEvent event)
/*     */   {
/* 158 */     if (!playerEnabled) return;
/*     */ 
/*     */     try
/*     */     {
/* 162 */       fixTabName(event.getPlayer());
/*     */     }
/*     */     catch (Exception localException)
/*     */     {
/*     */     }
/*     */   }
/*     */ 
/*     */   @EventHandler(priority=EventPriority.HIGHEST)
/*     */   public void joinHighest(PlayerJoinEvent event) {
/* 171 */     if (!playerEnabled) return;
/*     */ 
/* 173 */     final Player p = event.getPlayer();
/*     */ 
/* 176 */     updateScoreboard(p, p.getWorld().getName().toLowerCase());
/*     */ 
/* 179 */     scheduler.scheduleSyncDelayedTask(instance, new Runnable() {
/* 180 */       public void run() { MiscListeners.updatePlayer(p); }
/*     */ 
/*     */     }
/*     */     , 1L);
/*     */ 
/* 184 */     Updater.UpdaterHandler.notifyIfUpdateWasFound(p, "healthbar.update");
/*     */   }
/*     */ 
/*     */   @EventHandler(ignoreCancelled=true, priority=EventPriority.HIGH)
/*     */   public void playerTeleport(final PlayerTeleportEvent event)
/*     */   {
/* 191 */     if (!playerEnabled) return;
/*     */ 
/* 193 */     final Player player = event.getPlayer();
/*     */ 
/* 196 */     if (event.getFrom().getWorld() == event.getTo().getWorld())
/*     */     {
/* 198 */       scheduler.scheduleSyncDelayedTask(instance, new Runnable() {
/*     */         public void run() {
/* 200 */           if (MiscListeners.overrideOtherScoreboards) {
/* 201 */             MiscListeners.updateScoreboard(player, player.getWorld().getName().toLowerCase());
/*     */           }
/*     */ 
/* 204 */           MiscListeners.updatePlayer(player);
/*     */         }
/*     */       }
/*     */       , 1L);
/*     */     }
/*     */     else
/*     */     {
/* 211 */       scheduler.scheduleSyncDelayedTask(instance, new Runnable() {
/*     */         public void run() {
/* 213 */           MiscListeners.updatePlayer(player);
/*     */         }
/*     */       }
/*     */       , 1L);
/*     */ 
/* 216 */       if (overrideOtherScoreboards)
/*     */       {
/* 219 */         scheduler.scheduleSyncDelayedTask(instance, new Runnable() {
/*     */           public void run() {
/* 221 */             MiscListeners.updateScoreboard(player, event.getTo().getWorld().getName().toLowerCase());
/*     */           }
/*     */         }
/*     */         , 1L);
/*     */       }
/*     */       else
/* 225 */         updateScoreboard(player, event.getTo().getWorld().getName().toLowerCase());
/*     */     }
/*     */   }
/*     */ 
/*     */   @EventHandler(priority=EventPriority.HIGHEST)
/*     */   public void playerRespawn(PlayerRespawnEvent event)
/*     */   {
/* 238 */     if (!playerEnabled) return;
/*     */ 
/* 240 */     final Player player = event.getPlayer();
/*     */ 
/* 242 */     updateScoreboard(player, player.getWorld().getName().toLowerCase());
/*     */ 
/* 244 */     scheduler.scheduleSyncDelayedTask(instance, new Runnable() {
/* 245 */       public void run() { MiscListeners.updatePlayer(player); }
/*     */ 
/*     */     }
/*     */     , 1L);
/*     */   }
/*     */ 
/*     */   private static void updateScoreboard(Player p, String worldName)
/*     */   {
/* 253 */     if (!p.isOnline()) return;
/*     */ 
/* 256 */     if ((usePlayerPermissions) && 
/* 257 */       (!p.hasPermission("healthbar.see.onplayer"))) {
/*     */       try { p.setScoreboard(fakeSb); } catch (Exception localException) {
/* 259 */       }return;
/*     */     }
/*     */ 
/* 264 */     if ((playerUseDisabledWorlds) && 
/* 265 */       (playerDisabledWorlds.contains(worldName))) {
/*     */       try { p.setScoreboard(fakeSb); } catch (Exception localException1) {
/* 267 */       }return;
/*     */     }
/*     */ 
/*     */     try
/*     */     {
/* 272 */       p.setScoreboard(mainSb);
/*     */     }
/*     */     catch (Exception localException2) {
/*     */     }
/*     */   }
/*     */ 
/*     */   private static void updatePlayer(Player p) {
/* 279 */     PlayerBar.updateHealthBelow(p);
/*     */ 
/* 282 */     if ((playerUseAfter) && (playerHideDelay == 0))
/* 283 */       PlayerBar.setHealthSuffix(p, p.getHealth(), p.getMaxHealth());
/*     */   }
/*     */ 
/*     */   private static void fixTabName(Player p)
/*     */   {
/* 289 */     if ((fixTabNames) && (!pluginDisabledWhiteTabNames)) {
/* 290 */       if (p.getPlayerListName().startsWith("§")) return;
/*     */ 
/* 292 */       if (p.getName().length() > 14) {
/* 293 */         p.setPlayerListName(p.getName().substring(0, 14));
/* 294 */         p.setPlayerListName(p.getName());
/*     */       }
/*     */       else {
/* 297 */         p.setPlayerListName("§f" + p.getName());
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public static void loadConfiguration()
/*     */   {
/* 304 */     FileConfiguration config = instance.getConfig();
/*     */ 
/* 306 */     usePlayerPermissions = config.getBoolean(Configuration.Nodes.USE_PLAYER_PERMISSIONS.getNode());
/* 307 */     fixTabNames = config.getBoolean(Configuration.Nodes.FIX_TAB_NAMES.getNode());
/* 308 */     playerHideDelay = config.getInt(Configuration.Nodes.PLAYERS_AFTER_DELAY.getNode());
/* 309 */     playerEnabled = config.getBoolean(Configuration.Nodes.PLAYERS_ENABLE.getNode());
/* 310 */     playerUseAfter = config.getBoolean(Configuration.Nodes.PLAYERS_AFTER_ENABLE.getNode());
/*     */ 
/* 312 */     playerUseDisabledWorlds = config.getBoolean(Configuration.Nodes.PLAYERS_WORLD_DISABLING.getNode());
/*     */ 
/* 314 */     overrideOtherScoreboards = config.getBoolean(Configuration.Nodes.OVERRIDE_OTHER_SCOREBOARD.getNode());
/* 315 */     if (playerUseDisabledWorlds) {
/* 316 */       playerDisabledWorlds = Arrays.asList(
/* 317 */         instance.getConfig()
/* 318 */         .getString(Configuration.Nodes.PLAYERS_DISABLED_WORLDS.getNode())
/* 319 */         .toLowerCase()
/* 320 */         .replace(" ", "")
/* 321 */         .split(","));
/*     */     }
/*     */ 
/* 324 */     Player[] playerlist = Bukkit.getOnlinePlayers();
/* 325 */     if (playerlist.length != 0)
/* 326 */       for (Player p : playerlist) {
/* 327 */         updatePlayer(p);
/* 328 */         updateScoreboard(p, p.getWorld().getName().toLowerCase());
/* 329 */         fixTabName(p);
/*     */       }
/*     */   }
/*     */ 
/*     */   public static void disableTabNamesFix()
/*     */   {
/* 337 */     pluginDisabledWhiteTabNames = true;
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\HealthBar.jar
 * Qualified Name:     com.gmail.filoghost.healthbar.MiscListeners
 * JD-Core Version:    0.6.2
 */