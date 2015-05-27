/*     */ package com.gmail.filoghost.healthbar;
/*     */ 
/*     */ import com.gmail.filoghost.healthbar.utils.MobBarsUtils;
/*     */ import com.gmail.filoghost.healthbar.utils.Utils;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.logging.Logger;
/*     */ import me.ThaH3lper.com.API.EpicBossAPI;
/*     */ import org.bukkit.Bukkit;
/*     */ import org.bukkit.Server;
/*     */ import org.bukkit.World;
/*     */ import org.bukkit.command.ConsoleCommandSender;
/*     */ import org.bukkit.configuration.file.FileConfiguration;
/*     */ import org.bukkit.entity.Entity;
/*     */ import org.bukkit.entity.EntityType;
/*     */ import org.bukkit.entity.LivingEntity;
/*     */ import org.bukkit.entity.Player;
/*     */ import org.bukkit.event.EventHandler;
/*     */ import org.bukkit.event.EventPriority;
/*     */ import org.bukkit.event.Listener;
/*     */ import org.bukkit.event.entity.CreatureSpawnEvent;
/*     */ import org.bukkit.event.entity.EntityDamageByEntityEvent;
/*     */ import org.bukkit.event.entity.EntityDamageEvent;
/*     */ import org.bukkit.event.entity.EntityDeathEvent;
/*     */ import org.bukkit.event.entity.EntityRegainHealthEvent;
/*     */ import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;
/*     */ import org.bukkit.plugin.Plugin;
/*     */ import org.bukkit.plugin.PluginManager;
/*     */ import org.bukkit.scheduler.BukkitScheduler;
/*     */ 
/*     */ public class DamageListener
/*     */   implements Listener
/*     */ {
/*  35 */   private static final Plugin plugin = Main.plugin;
/*  36 */   private static BukkitScheduler scheduler = Bukkit.getScheduler();
/*     */   public static boolean mobEnabled;
/*     */   private static String[] barArray;
/*     */   private static boolean mobUseText;
/*     */   private static boolean mobUseCustomText;
/*     */   private static String mobCustomText;
/*     */   private static boolean customTextContains_Name;
/*     */   private static boolean mobSemiHidden;
/*     */   protected static long mobHideDelay;
/*     */   private static boolean mobUseCustomBar;
/*     */   private static boolean showOnCustomNames;
/*     */   private static BarType barStyle;
/*     */   private static boolean playerEnabled;
/*     */   private static long playerHideDelay;
/*     */   private static boolean playerUseAfter;
/*     */   private static boolean hookEpicboss;
/*  59 */   private static Map<String, String> localeMap = new HashMap();
/*  60 */   private static Map<String, Integer> playerTable = new HashMap();
/*  61 */   private static Map<Integer, Integer> mobTable = new HashMap();
/*  62 */   private static Map<Integer, StringBoolean> namesTable = new HashMap();
/*     */   private static boolean mobUseDisabledWorlds;
/*  66 */   private static List<String> mobDisabledWorlds = new ArrayList();
/*     */   private static boolean mobTypeDisabling;
/*  70 */   private static List<EntityType> mobDisabledTypes = new ArrayList();
/*     */ 
/*     */   @EventHandler(ignoreCancelled=true, priority=EventPriority.LOW)
/*     */   public void onEntityDeath(EntityDeathEvent event)
/*     */   {
/*  76 */     if ((event.getEntity() instanceof LivingEntity))
/*  77 */       hideBar(event.getEntity());
/*     */   }
/*     */ 
/*     */   @EventHandler(ignoreCancelled=true, priority=EventPriority.HIGHEST)
/*     */   public void onEntityDamageEvent(EntityDamageEvent event)
/*     */   {
/*  84 */     Entity entity = event.getEntity();
/*     */ 
/*  86 */     if (!(entity instanceof LivingEntity)) return;
/*  87 */     LivingEntity living = (LivingEntity)entity;
/*  88 */     if (living.getNoDamageTicks() > living.getMaximumNoDamageTicks() / 2.0F) return;
/*     */ 
/*  91 */     if (((entity instanceof Player)) && 
/*  92 */       (playerEnabled)) {
/*  93 */       parsePlayerHit((Player)entity, event instanceof EntityDamageByEntityEvent);
/*  94 */       return;
/*     */     }
/*     */ 
/*  98 */     if (mobEnabled)
/*  99 */       parseMobHit(living, event instanceof EntityDamageByEntityEvent);
/*     */   }
/*     */ 
/*     */   @EventHandler(ignoreCancelled=true, priority=EventPriority.HIGHEST)
/*     */   public void onEntityRegain(EntityRegainHealthEvent event)
/*     */   {
/* 106 */     Entity entity = event.getEntity();
/*     */ 
/* 108 */     if ((playerEnabled) && 
/* 109 */       ((entity instanceof Player))) {
/* 110 */       parsePlayerHit((Player)entity, (event.getRegainReason() != EntityRegainHealthEvent.RegainReason.SATIATED) && (event.getAmount() > 0.0D));
/* 111 */       return;
/*     */     }
/*     */ 
/* 114 */     if ((mobEnabled) && 
/* 115 */       ((entity instanceof LivingEntity)))
/* 116 */       parseMobHit((LivingEntity)entity, true);
/*     */   }
/*     */ 
/*     */   @EventHandler(ignoreCancelled=true, priority=EventPriority.HIGHEST)
/*     */   public void onEntitySpawn(CreatureSpawnEvent event)
/*     */   {
/* 124 */     if ((mobHideDelay == 0L) && (mobEnabled))
/*     */     {
/* 126 */       final LivingEntity mob = event.getEntity();
/* 127 */       scheduler.scheduleSyncDelayedTask(plugin, new Runnable() {
/* 128 */         public void run() { DamageListener.parseMobHit(mob, true); }
/*     */ 
/*     */       }
/*     */       , 1L);
/*     */     }
/*     */   }
/*     */ 
/*     */   private static void parsePlayerHit(Player player, boolean damagedByEntity)
/*     */   {
/* 143 */     String pname = player.getName();
/*     */ 
/* 146 */     scheduler.scheduleSyncDelayedTask(plugin, new Runnable() {
/* 147 */       public void run() { PlayerBar.updateHealthBelow(DamageListener.this); }
/*     */ 
/*     */     });
/* 151 */     if (!playerUseAfter) return;
/*     */ 
/* 154 */     if (playerHideDelay == 0L) {
/* 155 */       showPlayerHealthBar(player);
/* 156 */       return;
/*     */     }
/*     */ 
/* 159 */     if (damagedByEntity)
/*     */     {
/* 162 */       Integer eventualTaskID = (Integer)playerTable.remove(pname);
/*     */ 
/* 164 */       if (eventualTaskID != null)
/*     */       {
/* 166 */         scheduler.cancelTask(eventualTaskID.intValue());
/*     */       }
/*     */ 
/* 169 */       showPlayerHealthBar(player);
/* 170 */       hidePlayerBarLater(player);
/* 171 */       return;
/*     */     }
/*     */ 
/* 175 */     if (playerTable.containsKey(pname))
/* 176 */       showPlayerHealthBar(player);
/*     */   }
/*     */ 
/*     */   protected static void parseMobHit(LivingEntity mob, boolean damagedByEntity)
/*     */   {
/* 188 */     EntityType type = mob.getType();
/* 189 */     if ((mobTypeDisabling) && (mobDisabledTypes.contains(type))) return;
/* 190 */     if ((type == EntityType.WITHER) || (type == EntityType.ENDER_DRAGON)) return;
/* 191 */     if ((type == EntityType.HORSE) && (!mob.isEmpty()))
/*     */     {
/* 194 */       return;
/*     */     }
/*     */ 
/* 200 */     if ((mobUseDisabledWorlds) && 
/* 201 */       (mobDisabledWorlds.contains(mob.getWorld().getName().toLowerCase())))
/*     */     {
/* 203 */       return;
/*     */     }
/*     */ 
/* 210 */     String customName = mob.getCustomName();
/* 211 */     if ((customName != null) && 
/* 212 */       (!customName.startsWith("§r"))) {
/* 213 */       if (showOnCustomNames)
/* 214 */         namesTable.put(Integer.valueOf(mob.getEntityId()), new StringBoolean(customName, Boolean.valueOf(mob.isCustomNameVisible())));
/* 215 */       else return;
/*     */ 
/*     */     }
/*     */ 
/* 220 */     if (mobHideDelay == 0L) {
/* 221 */       showMobHealthBar(mob);
/* 222 */       return;
/*     */     }
/*     */ 
/* 227 */     if (damagedByEntity)
/*     */     {
/* 231 */       Integer eventualTaskID = (Integer)mobTable.remove(Integer.valueOf(mob.getEntityId()));
/*     */ 
/* 233 */       if (eventualTaskID != null)
/*     */       {
/* 235 */         scheduler.cancelTask(eventualTaskID.intValue());
/*     */       }
/* 237 */       showMobHealthBar(mob);
/* 238 */       hideMobBarLater(mob);
/* 239 */       return;
/*     */     }
/*     */ 
/* 244 */     if (mobTable.containsKey(Integer.valueOf(mob.getEntityId())))
/* 245 */       showMobHealthBar(mob);
/*     */   }
/*     */ 
/*     */   private static void showMobHealthBar(LivingEntity mob)
/*     */   {
/* 253 */     scheduler.scheduleSyncDelayedTask(plugin, new Runnable()
/*     */     {
/*     */       public void run()
/*     */       {
/* 257 */         double health = DamageListener.this.getHealth();
/* 258 */         double max = DamageListener.this.getMaxHealth();
/*     */ 
/* 262 */         if (health <= 0.0D) {
/* 263 */           return;
/*     */         }
/*     */ 
/* 267 */         if (DamageListener.barStyle == BarType.BAR)
/*     */         {
/* 269 */           DamageListener.this.setCustomName("§r" + DamageListener.barArray[Utils.roundUpPositiveWithMax(health / max * 20.0D, 20)]);
/*     */         }
/* 272 */         else if (DamageListener.barStyle == BarType.CUSTOM_TEXT)
/*     */         {
/* 274 */           String displayString = DamageListener.mobCustomText.replace("{h}", String.valueOf(Utils.roundUpPositive(health)));
/* 275 */           displayString = displayString.replace("{m}", String.valueOf(Utils.roundUpPositive(max)));
/*     */ 
/* 278 */           if (DamageListener.customTextContains_Name) {
/* 279 */             displayString = displayString.replace("{n}", DamageListener.getName(DamageListener.this, DamageListener.this.getType().toString()));
/*     */           }
/* 281 */           DamageListener.this.setCustomName("§r" + displayString);
/*     */         }
/* 284 */         else if (DamageListener.barStyle == BarType.DEFAULT_TEXT)
/*     */         {
/* 286 */           StringBuilder sb = new StringBuilder("§rHealth: ");
/* 287 */           sb.append(Utils.roundUpPositive(health));
/* 288 */           sb.append("/");
/* 289 */           sb.append(Utils.roundUpPositive(max));
/* 290 */           DamageListener.this.setCustomName(sb.toString());
/*     */         }
/*     */ 
/* 294 */         if (!DamageListener.mobSemiHidden) DamageListener.this.setCustomNameVisible(true); 
/*     */       }
/*     */     });
/*     */   }
/*     */ 
/*     */   private static void hideMobBarLater(LivingEntity mob)
/*     */   {
/* 300 */     int id = mob.getEntityId();
/* 301 */     mobTable.put(Integer.valueOf(id), Integer.valueOf(scheduler.scheduleSyncDelayedTask(plugin, new Runnable() {
/*     */       public void run() {
/* 303 */         DamageListener.hideBar(DamageListener.this);
/*     */       }
/*     */     }
/*     */     , mobHideDelay)));
/*     */   }
/*     */ 
/*     */   public static void hidePlayerBarLater(Player player)
/*     */   {
/* 310 */     playerTable.put(player.getName(), Integer.valueOf(scheduler.scheduleSyncDelayedTask(plugin, new Runnable() {
/*     */       public void run() {
/* 312 */         DamageListener.playerTable.remove(DamageListener.this.getName());
/* 313 */         PlayerBar.hideHealthBar(DamageListener.this);
/*     */       }
/*     */     }
/*     */     , playerHideDelay)));
/*     */   }
/*     */ 
/*     */   public static void hideBar(LivingEntity mob)
/*     */   {
/* 322 */     String cname = mob.getCustomName();
/* 323 */     if ((cname != null) && (!cname.startsWith("§r")))
/*     */     {
/* 325 */       return;
/*     */     }
/*     */ 
/* 329 */     Integer id = (Integer)mobTable.remove(Integer.valueOf(mob.getEntityId()));
/* 330 */     if (id != null) {
/* 331 */       scheduler.cancelTask(id.intValue());
/*     */     }
/*     */ 
/* 334 */     if (showOnCustomNames) {
/* 335 */       int idForName = mob.getEntityId();
/* 336 */       StringBoolean sb = (StringBoolean)namesTable.remove(Integer.valueOf(idForName));
/* 337 */       if (sb != null)
/*     */       {
/* 339 */         mob.setCustomName(sb.getString());
/* 340 */         mob.setCustomNameVisible(sb.getBoolean().booleanValue());
/* 341 */         return;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 346 */     mob.setCustomName("");
/* 347 */     mob.setCustomNameVisible(false);
/*     */   }
/*     */ 
/*     */   public static String getNameWhileHavingBar(LivingEntity mob)
/*     */   {
/* 352 */     String cname = mob.getCustomName();
/* 353 */     if (cname == null) return null;
/*     */ 
/* 355 */     if (cname.startsWith("§r")) {
/* 356 */       if (showOnCustomNames) {
/* 357 */         int id = mob.getEntityId();
/* 358 */         StringBoolean sb = (StringBoolean)namesTable.get(Integer.valueOf(id));
/* 359 */         if (sb != null) {
/* 360 */           return sb.getString();
/*     */         }
/*     */       }
/* 363 */       return null;
/*     */     }
/*     */ 
/* 366 */     return cname;
/*     */   }
/*     */ 
/*     */   private static void showPlayerHealthBar(Player p)
/*     */   {
/* 372 */     scheduler.scheduleSyncDelayedTask(plugin, new Runnable()
/*     */     {
/*     */       public void run()
/*     */       {
/* 376 */         double health = DamageListener.this.getHealth();
/* 377 */         double max = DamageListener.this.getMaxHealth();
/*     */ 
/* 381 */         if (health == 0.0D) {
/* 382 */           PlayerBar.hideHealthBar(DamageListener.this);
/* 383 */           return;
/*     */         }
/*     */ 
/* 386 */         PlayerBar.setHealthSuffix(DamageListener.this, health, max);
/*     */       }
/*     */     });
/*     */   }
/*     */ 
/*     */   public static void removeAllMobHealthBars() {
/* 392 */     scheduler.cancelTasks(plugin);
/* 393 */     mobTable.clear();
/* 394 */     List worldsList = plugin.getServer().getWorlds();
/*     */     Iterator localIterator2;
/* 395 */     for (Iterator localIterator1 = worldsList.iterator(); localIterator1.hasNext(); 
/* 397 */       localIterator2.hasNext())
/*     */     {
/* 395 */       World w = (World)localIterator1.next();
/* 396 */       List entityList = w.getLivingEntities();
/* 397 */       localIterator2 = entityList.iterator(); continue; LivingEntity e = (LivingEntity)localIterator2.next();
/* 398 */       if (e.getType() != EntityType.PLAYER)
/* 399 */         hideBar(e);
/*     */     }
/*     */   }
/*     */ 
/*     */   private static String getName(LivingEntity mob, String mobType)
/*     */   {
/* 409 */     if (hookEpicboss) {
/*     */       try {
/* 411 */         if (EpicBossAPI.isBoss(mob))
/* 412 */           return Utils.colorize(EpicBossAPI.getBossDisplayName(mob));
/*     */       }
/*     */       catch (Exception ex) {
/* 415 */         ex.printStackTrace();
/* 416 */         Main.logger.warning("Could not get boss name from EpicBoss. Hook disabled. Is it updated?");
/* 417 */         hookEpicboss = false;
/*     */       }
/*     */     }
/*     */ 
/* 421 */     String customName = mob.getCustomName();
/* 422 */     if ((customName != null) && (!customName.startsWith("§r"))) {
/* 423 */       return customName;
/*     */     }
/*     */ 
/* 426 */     StringBoolean sb = (StringBoolean)namesTable.get(Integer.valueOf(mob.getEntityId()));
/* 427 */     if (sb != null)
/*     */     {
/* 429 */       return sb.getString();
/*     */     }
/*     */ 
/* 432 */     String name = (String)localeMap.get(mobType);
/*     */ 
/* 434 */     if (name != null) {
/* 435 */       return name;
/*     */     }
/* 437 */     return "";
/*     */   }
/*     */ 
/*     */   public static void loadConfiguration()
/*     */   {
/* 444 */     removeAllMobHealthBars();
/*     */ 
/* 446 */     FileConfiguration config = plugin.getConfig();
/*     */ 
/* 449 */     mobEnabled = config.getBoolean(Configuration.Nodes.MOB_ENABLE.getNode());
/* 450 */     mobUseText = config.getBoolean(Configuration.Nodes.MOB_TEXT_MODE.getNode());
/* 451 */     mobUseCustomText = config.getBoolean(Configuration.Nodes.MOB_CUSTOM_TEXT_ENABLE.getNode());
/* 452 */     mobCustomText = Utils.replaceSymbols(config.getString(Configuration.Nodes.MOB_CUSTOM_TEXT.getNode()));
/* 453 */     mobSemiHidden = config.getBoolean(Configuration.Nodes.MOB_SHOW_IF_LOOKING.getNode());
/*     */ 
/* 455 */     mobHideDelay = config.getInt(Configuration.Nodes.MOB_DELAY.getNode()) * 20L;
/* 456 */     if (config.getBoolean(Configuration.Nodes.MOB_ALWAYS_SHOWN.getNode(), false)) {
/* 457 */       mobHideDelay = 0L;
/*     */     }
/*     */ 
/* 460 */     mobUseCustomBar = config.getBoolean(Configuration.Nodes.MOB_USE_CUSTOM.getNode());
/* 461 */     showOnCustomNames = config.getBoolean(Configuration.Nodes.MOB_SHOW_ON_NAMED.getNode());
/* 462 */     mobUseDisabledWorlds = config.getBoolean(Configuration.Nodes.MOB_WORLD_DISABLING.getNode());
/*     */ 
/* 464 */     if (mobUseDisabledWorlds) {
/* 465 */       mobDisabledWorlds = Arrays.asList(plugin.getConfig()
/* 466 */         .getString(Configuration.Nodes.MOB_DISABLED_WORLDS.getNode())
/* 467 */         .toLowerCase()
/* 468 */         .replace(" ", "")
/* 469 */         .split(","));
/*     */     }
/*     */ 
/* 472 */     mobTypeDisabling = config.getBoolean(Configuration.Nodes.MOB_TYPE_DISABLING.getNode());
/*     */ 
/* 477 */     playerEnabled = config.getBoolean(Configuration.Nodes.PLAYERS_ENABLE.getNode());
/*     */ 
/* 479 */     playerHideDelay = config.getInt("player-bars.after-name.hide-delay-seconds") * 20L;
/* 480 */     if (config.getBoolean(Configuration.Nodes.PLAYERS_AFTER_ALWAYS_SHOWN.getNode(), false)) {
/* 481 */       playerHideDelay = 0L;
/*     */     }
/*     */ 
/* 485 */     playerUseAfter = config.getBoolean(Configuration.Nodes.PLAYERS_AFTER_ENABLE.getNode());
/*     */ 
/* 488 */     hookEpicboss = config.getBoolean(Configuration.Nodes.HOOKS_EPIBOSS.getNode());
/*     */ 
/* 490 */     if (hookEpicboss) {
/* 491 */       if (!Bukkit.getPluginManager().isPluginEnabled("EpicBoss Gold Edition"))
/*     */       {
/* 493 */         hookEpicboss = false;
/* 494 */         Bukkit.getConsoleSender().sendMessage("§a[HealthBar] §fCould not find plugin EpicBoss Gold Edition, check that you have installed it and it's correctly loaded. If not, set 'hooks, epicboss: false' in the configs. If you think that is an error, contact the developer.");
/*     */       }
/*     */       else
/*     */       {
/* 498 */         Main.logger.info("Hooked plugin EpicBoss Gold Edition.");
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 503 */     if (mobCustomText.contains("{name}")) {
/* 504 */       customTextContains_Name = true;
/* 505 */       mobCustomText = mobCustomText.replace("{name}", "{n}"); } else {
/* 506 */       customTextContains_Name = false;
/*     */     }
/*     */ 
/* 509 */     barArray = new String[21];
/*     */ 
/* 512 */     if (mobUseCustomBar) {
/* 513 */       barStyle = BarType.BAR;
/*     */     }
/* 516 */     else if (mobUseText) {
/* 517 */       if (mobUseCustomText) {
/* 518 */         mobCustomText = mobCustomText.replace("{health}", "{h}");
/* 519 */         mobCustomText = mobCustomText.replace("{max}", "{m}");
/* 520 */         barStyle = BarType.CUSTOM_TEXT;
/*     */       } else {
/* 522 */         barStyle = BarType.DEFAULT_TEXT;
/*     */       }
/*     */     }
/*     */     else
/*     */     {
/* 527 */       barStyle = BarType.BAR;
/*     */     }
/*     */ 
/* 530 */     if (barStyle == BarType.BAR) {
/* 531 */       if (mobUseCustomBar)
/* 532 */         barArray = MobBarsUtils.getCustomBars(Utils.loadFile("custom-mob-bar.yml", plugin));
/*     */       else {
/* 534 */         barArray = MobBarsUtils.getDefaultsBars(config);
/*     */       }
/*     */     }
/*     */ 
/* 538 */     if ((mobUseCustomText) && (customTextContains_Name))
/*     */     {
/* 540 */       localeMap = Utils.getTranslationMap(plugin);
/*     */     }
/*     */ 
/* 543 */     if (mobTypeDisabling) {
/* 544 */       mobDisabledTypes = Utils.getTypesFromString(config.getString(Configuration.Nodes.MOB_DISABLED_TYPES.getNode()));
/*     */     }
/*     */ 
/* 547 */     if (mobHideDelay == 0L)
/*     */     {
/*     */       Iterator localIterator2;
/* 548 */       for (Iterator localIterator1 = Bukkit.getWorlds().iterator(); localIterator1.hasNext(); 
/* 549 */         localIterator2.hasNext())
/*     */       {
/* 548 */         World world = (World)localIterator1.next();
/* 549 */         localIterator2 = world.getLivingEntities().iterator(); continue; LivingEntity mob = (LivingEntity)localIterator2.next();
/* 550 */         if (mob.getType() != EntityType.PLAYER)
/* 551 */           parseMobHit(mob, true);
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\HealthBar.jar
 * Qualified Name:     com.gmail.filoghost.healthbar.DamageListener
 * JD-Core Version:    0.6.2
 */