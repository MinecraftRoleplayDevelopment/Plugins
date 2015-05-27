/*      */ package com.zettelnet.armorweight;
/*      */ 
/*      */ import com.zettelnet.armorweight.event.HorseWeightChangeEvent;
/*      */ import com.zettelnet.armorweight.event.PlayerWeightChangeEvent;
/*      */ import com.zettelnet.armorweight.lib.darkblade12.ReflectionHandler;
/*      */ import com.zettelnet.armorweight.lib.darkblade12.ReflectionHandler.PackageType;
/*      */ import com.zettelnet.armorweight.lib.darkblade12.ReflectionHandler.SubPackageType;
/*      */ import java.lang.reflect.Field;
/*      */ import java.lang.reflect.Method;
/*      */ import java.util.HashMap;
/*      */ import java.util.HashSet;
/*      */ import java.util.Map;
/*      */ import java.util.Map.Entry;
/*      */ import java.util.Set;
/*      */ import java.util.logging.Logger;
/*      */ import org.apache.commons.lang.Validate;
/*      */ import org.bukkit.Bukkit;
/*      */ import org.bukkit.GameMode;
/*      */ import org.bukkit.Server;
/*      */ import org.bukkit.World;
/*      */ import org.bukkit.enchantments.Enchantment;
/*      */ import org.bukkit.entity.Entity;
/*      */ import org.bukkit.entity.Horse;
/*      */ import org.bukkit.entity.Player;
/*      */ import org.bukkit.event.Event;
/*      */ import org.bukkit.inventory.HorseInventory;
/*      */ import org.bukkit.inventory.ItemStack;
/*      */ import org.bukkit.inventory.PlayerInventory;
/*      */ import org.bukkit.plugin.Plugin;
/*      */ import org.bukkit.plugin.PluginManager;
/*      */ import org.bukkit.scheduler.BukkitScheduler;
/*      */ 
/*      */ public class WeightManager
/*      */ {
/*      */   public static final double DEFAULT_PLAYER_WEIGHT = 1.0D;
/*  162 */   public static double PLAYER_WEIGHT = 1.0D;
/*      */   public static final double DEFAULT_HORSE_WEIGHT = 5.0D;
/*  173 */   public static double HORSE_WEIGHT = 5.0D;
/*      */   public static final float DEFAULT_WALK_SPEED = 0.2F;
/*      */   public static final float DEFAULT_FLY_SPEED = 0.1F;
/*  196 */   public static double DEFAULT_ENCHANTMENT_WEIGHT = 0.01D;
/*      */ 
/*  206 */   public static final Map<Enchantment, Double> ENCHANTMENT_WEIGHTS = new HashMap();
/*      */   private final Plugin plugin;
/*      */   private final Logger logger;
/*      */   private Set<String> enabledWorlds;
/*      */   private boolean allWorldsEnabled;
/*      */   private boolean playerArmorWeightEnabled;
/*      */   private boolean horseArmorWeightEnabled;
/*      */   private boolean horsePassengerWeightEnabled;
/*      */   private boolean enchantmentWeightEnabled;
/*      */   private boolean playerSpeedEffectEnabled;
/*      */   private boolean playerCreativeSpeedEffectEnabled;
/*      */   private boolean horseSpeedEffectEnabled;
/*      */   private boolean portableHorsesEnabled;
/*      */   private final Map<Player, PlayerData> playerData;
/*      */   private final Map<Horse, HorseData> horseData;
/*      */   private Class<?> craftHorseClass;
/*      */   private Class<?> entityHorseClass;
/*      */   private Class<?> iAttributeClass;
/*      */   private Class<?> genericAttributesClass;
/*      */   private Class<?> attributeInstanceClass;
/*      */   private Class<?> portableHorsesClass;
/*      */   private Method portableHorsesClassIsPortableHorseSaddleMethod;
/*      */   private Method portableHorsesClassCanUseHorseMethod;
/*      */ 
/*      */   public WeightManager(Plugin plugin, Logger logger)
/*      */   {
/*  249 */     Validate.notNull(logger, "Logger cannot be null");
/*  250 */     this.plugin = plugin;
/*  251 */     this.logger = logger;
/*      */ 
/*  253 */     this.enabledWorlds = new HashSet();
/*  254 */     this.allWorldsEnabled = false;
/*      */ 
/*  256 */     this.playerArmorWeightEnabled = true;
/*  257 */     this.horseArmorWeightEnabled = true;
/*  258 */     this.horsePassengerWeightEnabled = true;
/*  259 */     this.enchantmentWeightEnabled = false;
/*      */ 
/*  261 */     this.playerSpeedEffectEnabled = true;
/*  262 */     this.horseSpeedEffectEnabled = false;
/*      */ 
/*  264 */     this.portableHorsesEnabled = false;
/*      */ 
/*  266 */     this.playerData = new HashMap();
/*  267 */     this.horseData = new HashMap();
/*      */   }
/*      */ 
/*      */   protected void initialize()
/*      */   {
/*  280 */     if (isHorseSpeedEffectEnabled()) {
/*      */       try {
/*  282 */         this.craftHorseClass = ReflectionHandler.getClass("CraftHorse", ReflectionHandler.SubPackageType.ENTITY);
/*  283 */         this.entityHorseClass = ReflectionHandler.getClass("EntityHorse", ReflectionHandler.PackageType.MINECRAFT_SERVER);
/*  284 */         this.iAttributeClass = ReflectionHandler.getClass("IAttribute", ReflectionHandler.PackageType.MINECRAFT_SERVER);
/*  285 */         this.genericAttributesClass = ReflectionHandler.getClass("GenericAttributes", ReflectionHandler.PackageType.MINECRAFT_SERVER);
/*  286 */         this.attributeInstanceClass = ReflectionHandler.getClass("AttributeInstance", ReflectionHandler.PackageType.MINECRAFT_SERVER);
/*      */       } catch (Exception e) {
/*  288 */         setHorseSpeedEffectEnabled(false);
/*  289 */         this.logger.warning("Failed to get NMS classes for modifing horse speeds. You are probably using the wrong version of the plugin. Install the latest version and reload the server.");
/*  290 */         this.logger.warning("The horse speed effect has been disabled.");
/*  291 */         this.logger.warning("Error log:");
/*  292 */         e.printStackTrace();
/*      */       }
/*      */     }
/*  295 */     if (isPortableHorsesEnabled())
/*      */       try {
/*  297 */         this.portableHorsesClass = Class.forName("com.norcode.bukkit.portablehorses.PortableHorses");
/*  298 */         this.portableHorsesClassIsPortableHorseSaddleMethod = this.portableHorsesClass.getMethod("isPortableHorseSaddle", new Class[] { ItemStack.class });
/*  299 */         this.portableHorsesClassCanUseHorseMethod = this.portableHorsesClass.getMethod("canUseHorse", new Class[] { Player.class, Horse.class });
/*      */       } catch (Exception e) {
/*  301 */         setPortableHorsesEnabled(false);
/*  302 */         this.logger.warning("Failed to get PortableHorses classes to create compatibility with PortableHorses. You are probably using the wrong version of the plugin. Install the latest version and reload the server.");
/*  303 */         this.logger.warning("It is recommended to not use PortableHorses or disable the horse speed effect. Otherwise, horse speeds may be messed up.");
/*  304 */         this.logger.warning("Error log:");
/*  305 */         e.printStackTrace();
/*      */       }
/*      */   }
/*      */ 
/*      */   public Plugin getPlugin()
/*      */   {
/*  316 */     return this.plugin;
/*      */   }
/*      */ 
/*      */   public Logger getLogger()
/*      */   {
/*  325 */     return this.logger;
/*      */   }
/*      */ 
/*      */   public Set<String> getEnabledWorlds()
/*      */   {
/*  336 */     return this.enabledWorlds;
/*      */   }
/*      */ 
/*      */   public boolean isWorldEnabled(World world)
/*      */   {
/*  349 */     if (isAllWorldsEnabled()) {
/*  350 */       return true;
/*      */     }
/*  352 */     if (world == null) {
/*  353 */       return false;
/*      */     }
/*  355 */     return this.enabledWorlds.contains(world.getName());
/*      */   }
/*      */ 
/*      */   public boolean isWorldEnabled(String worldName)
/*      */   {
/*  367 */     if (isAllWorldsEnabled()) {
/*  368 */       return true;
/*      */     }
/*  370 */     return this.enabledWorlds.contains(worldName);
/*      */   }
/*      */ 
/*      */   public void setEnabledWorlds(Set<String> enabledWorldNames)
/*      */   {
/*  384 */     Validate.notNull(enabledWorldNames, "Enabled worlds cannot be null");
/*  385 */     this.enabledWorlds = enabledWorldNames;
/*      */   }
/*      */ 
/*      */   public boolean enableWorld(World world)
/*      */   {
/*  398 */     Validate.notNull(world, "World cannot be null");
/*  399 */     return enableWorld(world.getName());
/*      */   }
/*      */ 
/*      */   public boolean enableWorld(String worldName)
/*      */   {
/*  412 */     Validate.notNull(worldName, "World name cannot be null");
/*  413 */     return this.enabledWorlds.add(worldName);
/*      */   }
/*      */ 
/*      */   public boolean disableWorld(World world)
/*      */   {
/*  426 */     Validate.notNull(world, "World cannot be null");
/*  427 */     return disableWorld(world.getName());
/*      */   }
/*      */ 
/*      */   public boolean disableWorld(String worldName)
/*      */   {
/*  441 */     Validate.notNull(worldName, "World name cannot be null");
/*  442 */     return this.enabledWorlds.remove(worldName);
/*      */   }
/*      */ 
/*      */   public boolean isAllWorldsEnabled()
/*      */   {
/*  453 */     return this.allWorldsEnabled;
/*      */   }
/*      */ 
/*      */   public void setAllWorldsEnabled(boolean enabled)
/*      */   {
/*  464 */     this.allWorldsEnabled = enabled;
/*      */   }
/*      */ 
/*      */   public boolean isPortableHorsesEnabled()
/*      */   {
/*  475 */     return this.portableHorsesEnabled;
/*      */   }
/*      */ 
/*      */   public void setPortableHorsesEnabled(boolean enabled)
/*      */   {
/*  486 */     this.portableHorsesEnabled = enabled;
/*      */   }
/*      */ 
/*      */   public boolean isPlayerArmorWeightEnabled()
/*      */   {
/*  496 */     return this.playerArmorWeightEnabled;
/*      */   }
/*      */ 
/*      */   public void setPlayerArmorWeightEnabled(boolean enabled)
/*      */   {
/*  509 */     this.playerArmorWeightEnabled = enabled;
/*      */   }
/*      */ 
/*      */   public boolean isHorseArmorWeightEnabled()
/*      */   {
/*  520 */     return this.horseArmorWeightEnabled;
/*      */   }
/*      */ 
/*      */   public void setHorseArmorWeightEnabled(boolean enabled)
/*      */   {
/*  532 */     this.horseArmorWeightEnabled = enabled;
/*      */   }
/*      */ 
/*      */   public boolean isHorsePassengerWeightEnabled()
/*      */   {
/*  543 */     return this.horsePassengerWeightEnabled;
/*      */   }
/*      */ 
/*      */   public void setHorsePassengerWeightEnabled(boolean horsePassengerWeightEnabled)
/*      */   {
/*  555 */     this.horsePassengerWeightEnabled = horsePassengerWeightEnabled;
/*      */   }
/*      */ 
/*      */   public boolean isEnchantmentWeightEnabled()
/*      */   {
/*  565 */     return this.enchantmentWeightEnabled;
/*      */   }
/*      */ 
/*      */   public void setEnchantmentWeightEnabled(boolean enabled)
/*      */   {
/*  577 */     this.enchantmentWeightEnabled = enabled;
/*      */   }
/*      */ 
/*      */   public boolean isPlayerSpeedEffectEnabled()
/*      */   {
/*  589 */     return this.playerSpeedEffectEnabled;
/*      */   }
/*      */ 
/*      */   public void setPlayerSpeedEffectEnabled(boolean enabled)
/*      */   {
/*  602 */     this.playerSpeedEffectEnabled = enabled;
/*      */   }
/*      */ 
/*      */   public boolean isPlayerCreativeSpeedEffectEnabled()
/*      */   {
/*  615 */     return this.playerCreativeSpeedEffectEnabled;
/*      */   }
/*      */ 
/*      */   public void setPlayerCreativeSpeedEffectEnabled(boolean enabled)
/*      */   {
/*  629 */     this.playerCreativeSpeedEffectEnabled = enabled;
/*      */   }
/*      */ 
/*      */   public boolean isHorseSpeedEffectEnabled()
/*      */   {
/*  639 */     return this.horseSpeedEffectEnabled;
/*      */   }
/*      */ 
/*      */   public void setHorseSpeedEffectEnabled(boolean enabled)
/*      */   {
/*  650 */     this.horseSpeedEffectEnabled = enabled;
/*      */   }
/*      */ 
/*      */   public String formatWeight(double weight)
/*      */   {
/*  663 */     return String.valueOf(Math.round(weight * 100.0D));
/*      */   }
/*      */ 
/*      */   public boolean loadPlayer(Player player)
/*      */   {
/*  687 */     Validate.notNull(player, "Player cannot be null");
/*  688 */     if (isPlayerLoaded(player)) {
/*  689 */       return false;
/*      */     }
/*  691 */     double weight = calculateWeight(player);
/*  692 */     PlayerData data = new PlayerData(player, weight);
/*  693 */     this.playerData.put(player, data);
/*  694 */     updateEffects(player, weight);
/*  695 */     if ((player.getVehicle() instanceof Horse)) {
/*  696 */       loadHorse((Horse)player.getVehicle(), player);
/*      */     }
/*  698 */     return true;
/*      */   }
/*      */ 
/*      */   public boolean unloadPlayer(Player player)
/*      */   {
/*  720 */     if (!isPlayerLoaded(player)) {
/*  721 */       return false;
/*      */     }
/*  723 */     resetEffects(player);
/*  724 */     this.playerData.remove(player);
/*  725 */     if ((player.getVehicle() instanceof Horse)) {
/*  726 */       unloadHorse((Horse)player.getVehicle(), null);
/*      */     }
/*  728 */     return true;
/*      */   }
/*      */ 
/*      */   public boolean isPlayerLoaded(Player player)
/*      */   {
/*  743 */     return this.playerData.containsKey(player);
/*      */   }
/*      */ 
/*      */   public double getWeight(Player player)
/*      */   {
/*  761 */     if (!isPlayerLoaded(player)) {
/*  762 */       return calculateWeight(player);
/*      */     }
/*  764 */     return ((PlayerData)this.playerData.get(player)).getStoredWeight();
/*      */   }
/*      */ 
/*      */   public double calculateWeight(Player player)
/*      */   {
/*  780 */     return getPlayerWeight(player) + getArmorWeight(player);
/*      */   }
/*      */ 
/*      */   public double getPlayerWeight(Player player)
/*      */   {
/*  791 */     return PLAYER_WEIGHT;
/*      */   }
/*      */ 
/*      */   public double getArmorWeight(Player player)
/*      */   {
/*  811 */     Validate.notNull(player, "Player cannot be null");
/*  812 */     if (!player.hasPermission("armorweight.weight.armor")) {
/*  813 */       return 0.0D;
/*      */     }
/*  815 */     return getArmorWeight(player.getInventory().getArmorContents());
/*      */   }
/*      */ 
/*      */   public double getArmorWeight(ItemStack[] armorContents)
/*      */   {
/*  838 */     Validate.notNull(armorContents, "Armor contents cannot be null");
/*  839 */     if (!isPlayerArmorWeightEnabled()) {
/*  840 */       return 0.0D;
/*      */     }
/*  842 */     double weight = 0.0D;
/*  843 */     for (int i = 0; i < armorContents.length; i++) {
/*  844 */       ItemStack stack = armorContents[i];
/*  845 */       ArmorPart piece = ArmorPart.valueOf(i);
/*  846 */       weight += (ArmorType.getItemWeight(stack) + getEnchantmentsWeight(stack)) * piece.getWeightShare();
/*      */     }
/*  848 */     return weight;
/*      */   }
/*      */ 
/*      */   public boolean updateWeight(Player player)
/*      */   {
/*  869 */     return updateWeight(player, calculateWeight(player));
/*      */   }
/*      */ 
/*      */   public boolean updateWeight(Player player, double weight)
/*      */   {
/*  890 */     Validate.notNull(player, "Player cannot be null");
/*  891 */     if (!isPlayerLoaded(player)) {
/*  892 */       return false;
/*      */     }
/*  894 */     if (((PlayerData)this.playerData.get(player)).updateWeight(weight)) {
/*  895 */       updateEffects(player, weight);
/*  896 */       return true;
/*      */     }
/*  898 */     return false;
/*      */   }
/*      */ 
/*      */   public void updateWeightLater(final Player player)
/*      */   {
/*  922 */     Validate.notNull(player, "Player cannot be null");
/*  923 */     this.plugin.getServer().getScheduler().runTask(this.plugin, new Runnable()
/*      */     {
/*      */       public void run() {
/*  926 */         WeightManager.this.updateWeight(player);
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   public boolean updateEffects(Player player)
/*      */   {
/*  956 */     return updateEffects(player, getWeight(player));
/*      */   }
/*      */ 
/*      */   public boolean updateEffects(Player player, double weight)
/*      */   {
/*  982 */     Validate.notNull(player, "Player cannot be null");
/*  983 */     if (!isPlayerLoaded(player)) {
/*  984 */       return false;
/*      */     }
/*  986 */     boolean speedEffect = player.hasPermission("armorweight.effect.speed");
/*  987 */     speedEffect = (speedEffect) && (player.getGameMode() != GameMode.CREATIVE ? isPlayerSpeedEffectEnabled() : isPlayerCreativeSpeedEffectEnabled());
/*      */ 
/*  989 */     if (((PlayerData)this.playerData.get(player)).hadSpeedEffect() != speedEffect) {
/*  990 */       if (!speedEffect) {
/*  991 */         player.setWalkSpeed(0.2F);
/*  992 */         player.setFlySpeed(0.1F);
/*      */       }
/*  994 */       ((PlayerData)this.playerData.get(player)).setHadSpeedEffect(speedEffect);
/*      */     }
/*  996 */     if (speedEffect) {
/*  997 */       player.setWalkSpeed(getPlayerSpeed(weight, 0.2000000029802322D));
/*  998 */       player.setFlySpeed(getPlayerSpeed(weight, 0.1000000014901161D));
/*      */     }
/* 1000 */     ((PlayerData)this.playerData.get(player)).setDirty(false);
/* 1001 */     return true;
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public void resetWeight(Player player)
/*      */   {
/* 1015 */     resetEffects(player);
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public void resetEffects(Player player)
/*      */   {
/* 1034 */     updateEffects(player, 1.0D);
/*      */   }
/*      */ 
/*      */   public float getPlayerSpeed(double weight, double defaultSpeed)
/*      */   {
/* 1053 */     float speed = (float)(1.0D / weight * defaultSpeed);
/* 1054 */     if (speed > 1.0F) {
/* 1055 */       speed = 1.0F;
/*      */     }
/* 1057 */     if (speed < -1.0F) {
/* 1058 */       speed = -1.0F;
/*      */     }
/* 1060 */     return speed;
/*      */   }
/*      */ 
/*      */   public double getWeight(Enchantment enchantment)
/*      */   {
/* 1075 */     Double d = Double.valueOf(ENCHANTMENT_WEIGHTS.containsKey(enchantment) ? ((Double)ENCHANTMENT_WEIGHTS.get(enchantment)).doubleValue() : DEFAULT_ENCHANTMENT_WEIGHT);
/* 1076 */     return d == null ? 0.0D : d.doubleValue();
/*      */   }
/*      */ 
/*      */   public double getWeight(Enchantment enchantment, int level)
/*      */   {
/* 1091 */     Validate.isTrue(level >= 0, "Enchantment level has to be greater or equal than 0 (" + level + " < 0)");
/* 1092 */     return getWeight(enchantment) * level;
/*      */   }
/*      */ 
/*      */   public double getEnchantmentsWeight(ItemStack item)
/*      */   {
/* 1105 */     if (!isEnchantmentWeightEnabled()) {
/* 1106 */       return 0.0D;
/*      */     }
/* 1108 */     if (item == null) {
/* 1109 */       return 0.0D;
/*      */     }
/* 1111 */     double weight = 0.0D;
/* 1112 */     for (Map.Entry entry : item.getEnchantments().entrySet()) {
/* 1113 */       weight += getWeight((Enchantment)entry.getKey(), ((Integer)entry.getValue()).intValue());
/*      */     }
/* 1115 */     return weight;
/*      */   }
/*      */ 
/*      */   public void loadHorse(Horse horse)
/*      */   {
/* 1142 */     Validate.notNull(horse, "Horse cannot be null");
/* 1143 */     loadHorse(horse, horse.getPassenger());
/*      */   }
/*      */ 
/*      */   public boolean loadHorse(Horse horse, Entity passenger)
/*      */   {
/* 1172 */     Validate.notNull(horse, "Horse cannot be null");
/* 1173 */     if (isHorseLoaded(horse))
/* 1174 */       return false;
/*      */     try
/*      */     {
/* 1177 */       double weight = calculateWeight(horse, passenger);
/* 1178 */       HorseData data = new HorseData(horse, weight);
/* 1179 */       if (isHorseSpeedEffectEnabled()) {
/* 1180 */         data.setDefaultSpeed(getHorseSpeed(horse));
/*      */       }
/* 1182 */       this.horseData.put(horse, data);
/* 1183 */       updateEffects(horse, weight);
/* 1184 */       return true;
/*      */     } catch (Exception e) {
/* 1186 */       this.logger.warning("Failed to load horse " + horse + " and get its speed (probaly an NMS issue):");
/* 1187 */       e.printStackTrace();
/* 1188 */     }return false;
/*      */   }
/*      */ 
/*      */   public boolean unloadHorse(Horse horse)
/*      */   {
/* 1207 */     Validate.notNull(horse, "Horse cannot be null");
/* 1208 */     return unloadHorse(horse, horse.getPassenger());
/*      */   }
/*      */ 
/*      */   public boolean unloadHorse(Horse horse, Entity passenger)
/*      */   {
/* 1229 */     if (!isHorseLoaded(horse)) {
/* 1230 */       return false;
/*      */     }
/* 1232 */     if (!resetEffects(horse)) {
/* 1233 */       return false;
/*      */     }
/* 1235 */     this.horseData.remove(horse);
/* 1236 */     return true;
/*      */   }
/*      */ 
/*      */   public boolean isHorseLoaded(Horse horse)
/*      */   {
/* 1251 */     return this.horseData.containsKey(horse);
/*      */   }
/*      */ 
/*      */   public double getWeight(Horse horse)
/*      */   {
/* 1269 */     Validate.notNull(horse, "Horse cannot be null");
/* 1270 */     return getWeight(horse, horse.getPassenger());
/*      */   }
/*      */ 
/*      */   public double getWeight(Horse horse, Entity passenger)
/*      */   {
/* 1292 */     if (!isHorseLoaded(horse)) {
/* 1293 */       return calculateWeight(horse, passenger);
/*      */     }
/* 1295 */     return ((HorseData)this.horseData.get(horse)).getStoredWeight();
/*      */   }
/*      */ 
/*      */   public double calculateWeight(Horse horse)
/*      */   {
/* 1310 */     Validate.notNull(horse, "Horse cannot be null");
/* 1311 */     return calculateWeight(horse, horse.getPassenger());
/*      */   }
/*      */ 
/*      */   public double calculateWeight(Horse horse, Entity passenger)
/*      */   {
/* 1330 */     Validate.notNull(horse, "Horse cannot be null");
/*      */ 
/* 1332 */     double weight = getHorseWeight(horse);
/* 1333 */     if ((isHorsePassengerWeightEnabled()) && ((passenger instanceof Player))) {
/* 1334 */       weight += getWeight((Player)passenger);
/*      */     }
/* 1336 */     weight += getArmorWeight(horse.getInventory());
/* 1337 */     return weight;
/*      */   }
/*      */ 
/*      */   public double getHorseWeight(Horse horse)
/*      */   {
/* 1348 */     return HORSE_WEIGHT;
/*      */   }
/*      */ 
/*      */   public double getArmorWeight(Horse horse)
/*      */   {
/* 1364 */     Validate.notNull(horse, "Horse cannot be null");
/* 1365 */     return getArmorWeight(horse.getInventory());
/*      */   }
/*      */ 
/*      */   public double getArmorWeight(HorseInventory inventory)
/*      */   {
/* 1384 */     Validate.notNull(inventory, "Horse inventory cannot be null");
/* 1385 */     if (!isHorseArmorWeightEnabled()) {
/* 1386 */       return 0.0D;
/*      */     }
/* 1388 */     ItemStack armor = inventory.getArmor();
/* 1389 */     if (armor == null) {
/* 1390 */       return 0.0D;
/*      */     }
/* 1392 */     double weight = ArmorType.getItemWeight(armor);
/* 1393 */     weight += getEnchantmentsWeight(armor);
/*      */ 
/* 1395 */     return weight;
/*      */   }
/*      */ 
/*      */   public boolean updateWeight(Horse horse)
/*      */   {
/* 1415 */     return updateWeight(horse, calculateWeight(horse));
/*      */   }
/*      */ 
/*      */   public boolean updateWeight(Horse horse, Entity passenger)
/*      */   {
/* 1438 */     return updateWeight(horse, calculateWeight(horse, passenger));
/*      */   }
/*      */ 
/*      */   public boolean updateWeight(Horse horse, double weight)
/*      */   {
/* 1458 */     Validate.notNull(horse, "Horse cannot be null");
/* 1459 */     if (!isHorseLoaded(horse)) {
/* 1460 */       return false;
/*      */     }
/* 1462 */     if (((HorseData)this.horseData.get(horse)).updateWeight(weight)) {
/* 1463 */       updateEffects(horse, weight);
/* 1464 */       return true;
/*      */     }
/* 1466 */     return false;
/*      */   }
/*      */ 
/*      */   public void updateWeightLater(final Horse horse)
/*      */   {
/* 1489 */     Validate.notNull(horse, "Horse cannot be null");
/* 1490 */     this.plugin.getServer().getScheduler().runTask(this.plugin, new Runnable()
/*      */     {
/*      */       public void run() {
/* 1493 */         WeightManager.this.updateWeight(horse);
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   public void updateWeightLater(final Horse horse, final Entity passenger)
/*      */   {
/* 1521 */     Validate.notNull(horse, "Horse cannot be null");
/* 1522 */     this.plugin.getServer().getScheduler().runTask(this.plugin, new Runnable()
/*      */     {
/*      */       public void run() {
/* 1525 */         WeightManager.this.updateWeight(horse, passenger);
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   public boolean updateEffects(Horse horse)
/*      */   {
/* 1551 */     return updateEffects(horse, getWeight(horse));
/*      */   }
/*      */ 
/*      */   public boolean updateEffects(Horse horse, Entity passenger)
/*      */   {
/* 1578 */     return updateEffects(horse, getWeight(horse, passenger));
/*      */   }
/*      */ 
/*      */   public boolean updateEffects(Horse horse, double weight)
/*      */   {
/* 1602 */     Validate.notNull(horse, "Horse cannot be null");
/* 1603 */     if (!isHorseLoaded(horse)) {
/* 1604 */       return false;
/*      */     }
/*      */ 
/* 1607 */     if (isHorseSpeedEffectEnabled()) {
/*      */       try {
/* 1609 */         setHorseSpeed(horse, getHorseSpeed(weight, getDefaultHorseSpeed(horse)));
/*      */       } catch (Exception e) {
/* 1611 */         this.logger.warning("Failed to update speed of horse " + horse + " (probaly an NMS issue):");
/* 1612 */         e.printStackTrace();
/* 1613 */         return false;
/*      */       }
/*      */     }
/*      */ 
/* 1617 */     ((HorseData)this.horseData.get(horse)).setDirty(false);
/* 1618 */     return true;
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public void resetWeight(Horse horse)
/*      */   {
/* 1639 */     resetEffects(horse);
/*      */   }
/*      */ 
/*      */   /** @deprecated */
/*      */   public boolean resetEffects(Horse horse)
/*      */   {
/* 1662 */     return updateEffects(horse, 5.0D);
/*      */   }
/*      */ 
/*      */   public double getHorseSpeed(double weight, double defaultSpeed)
/*      */   {
/* 1679 */     double speed = 5.0D / weight * defaultSpeed;
/* 1680 */     if (speed > 1.0D) {
/* 1681 */       speed = 1.0D;
/*      */     }
/* 1683 */     if (speed < -1.0D) {
/* 1684 */       speed = -1.0D;
/*      */     }
/* 1686 */     return speed;
/*      */   }
/*      */ 
/*      */   public double getDefaultHorseSpeed(Horse horse)
/*      */     throws IllegalStateException
/*      */   {
/* 1701 */     Validate.notNull(horse, "Horse cannot be null");
/* 1702 */     if (!isHorseLoaded(horse)) {
/* 1703 */       throw new IllegalStateException("Horse has to be loaded");
/*      */     }
/* 1705 */     return ((HorseData)this.horseData.get(horse)).getDefaultSpeed();
/*      */   }
/*      */ 
/*      */   protected synchronized double getHorseSpeed(Horse horse) throws Exception {
/* 1709 */     Validate.notNull(horse, "Horse cannot be null");
/*      */ 
/* 1711 */     Object craftHorse = this.craftHorseClass.cast(horse);
/* 1712 */     Object entityHorse = this.craftHorseClass.getMethod("getHandle", new Class[0]).invoke(craftHorse, new Object[0]);
/*      */ 
/* 1714 */     Object genericAttribute = this.genericAttributesClass.getField("d").get(null);
/* 1715 */     Object attributeInstance = this.entityHorseClass.getMethod("getAttributeInstance", new Class[] { this.iAttributeClass }).invoke(entityHorse, new Object[] { genericAttribute });
/*      */ 
/* 1717 */     return ((Double)this.attributeInstanceClass.getMethod("getValue", new Class[0]).invoke(attributeInstance, new Object[0])).doubleValue();
/*      */   }
/*      */ 
/*      */   protected synchronized void setHorseSpeed(Horse horse, double speed) throws Exception {
/* 1721 */     Validate.notNull(horse, "Horse cannot be null");
/*      */ 
/* 1723 */     Object craftHorse = this.craftHorseClass.cast(horse);
/* 1724 */     Object entityHorse = this.craftHorseClass.getMethod("getHandle", new Class[0]).invoke(craftHorse, new Object[0]);
/*      */ 
/* 1726 */     Object genericAttribute = this.genericAttributesClass.getField("d").get(null);
/* 1727 */     Object attributeInstance = this.entityHorseClass.getMethod("getAttributeInstance", new Class[] { this.iAttributeClass }).invoke(entityHorse, new Object[] { genericAttribute });
/*      */ 
/* 1729 */     this.attributeInstanceClass.getMethod("setValue", new Class[] { Double.TYPE }).invoke(attributeInstance, new Object[] { Double.valueOf(speed) });
/*      */   }
/*      */ 
/*      */   protected boolean isKickedOffHorse(Player player, Horse horse, ItemStack saddle) throws Exception {
/* 1733 */     Plugin ph = this.plugin.getServer().getPluginManager().getPlugin("PortableHorses");
/* 1734 */     if (((Boolean)this.portableHorsesClassIsPortableHorseSaddleMethod.invoke(ph, new Object[] { saddle })).booleanValue());
/* 1734 */     return ((Boolean)this.portableHorsesClassCanUseHorseMethod.invoke(ph, new Object[] { player, horse })).booleanValue();
/*      */   }
/*      */ 
/*      */   protected class PlayerData extends WeightManager.WeightData
/*      */   {
/*      */     private final Player player;
/*      */     private boolean hadSpeed;
/*      */ 
/*      */     public PlayerData(Player player, double weight)
/*      */     {
/*  126 */       this(player, weight, true);
/*      */     }
/*      */ 
/*      */     public PlayerData(Player player, double weight, boolean dirty) {
/*  130 */       super(weight, dirty);
/*  131 */       this.player = player;
/*  132 */       this.hadSpeed = true;
/*      */     }
/*      */ 
/*      */     public Player getPlayer() {
/*  136 */       return this.player;
/*      */     }
/*      */ 
/*      */     protected Event makeEvent(double oldWeight, double newWeight)
/*      */     {
/*  141 */       return new PlayerWeightChangeEvent(this.player, oldWeight, newWeight);
/*      */     }
/*      */ 
/*      */     public boolean hadSpeedEffect() {
/*  145 */       return this.hadSpeed;
/*      */     }
/*      */ 
/*      */     public void setHadSpeedEffect(boolean hadSpeedEffect) {
/*  149 */       this.hadSpeed = hadSpeedEffect;
/*      */     }
/*      */   }
/*      */ 
/*      */   protected abstract class WeightData
/*      */   {
/*      */     private double weight;
/*      */     private boolean dirty;
/*      */ 
/*      */     public WeightData(double weight)
/*      */     {
/*   88 */       this(weight, true);
/*      */     }
/*      */ 
/*      */     public WeightData(double weight, boolean dirty) {
/*   92 */       this.weight = weight;
/*   93 */       this.dirty = dirty;
/*      */     }
/*      */ 
/*      */     public double getStoredWeight() {
/*   97 */       return this.weight;
/*      */     }
/*      */ 
/*      */     public boolean updateWeight(double weight) {
/*  101 */       if (this.weight != weight) {
/*  102 */         Bukkit.getPluginManager().callEvent(makeEvent(this.weight, weight));
/*  103 */         this.weight = weight;
/*  104 */         setDirty(true);
/*      */       }
/*  106 */       return isDirty();
/*      */     }
/*      */ 
/*      */     protected abstract Event makeEvent(double paramDouble1, double paramDouble2);
/*      */ 
/*      */     public boolean isDirty() {
/*  112 */       return this.dirty;
/*      */     }
/*      */ 
/*      */     public void setDirty(boolean dirty) {
/*  116 */       this.dirty = dirty;
/*      */     }
/*      */   }
/*      */ 
/*      */   protected class HorseData extends WeightManager.WeightData
/*      */   {
/*      */     private final Horse horse;
/*      */     private double defaultSpeed;
/*      */ 
/*      */     public HorseData(Horse horse, double weight)
/*      */     {
/*   54 */       this(horse, weight, true);
/*      */     }
/*      */ 
/*      */     public HorseData(Horse horse, double weight, boolean dirty) {
/*   58 */       super(weight, dirty);
/*      */ 
/*   60 */       this.horse = horse;
/*   61 */       this.defaultSpeed = 0.0D;
/*      */     }
/*      */ 
/*      */     public Horse getHorse() {
/*   65 */       return this.horse;
/*      */     }
/*      */ 
/*      */     public double getDefaultSpeed() {
/*   69 */       return this.defaultSpeed;
/*      */     }
/*      */ 
/*      */     public void setDefaultSpeed(double speed) {
/*   73 */       this.defaultSpeed = speed;
/*      */     }
/*      */ 
/*      */     protected Event makeEvent(double oldWeight, double newWeight)
/*      */     {
/*   78 */       return new HorseWeightChangeEvent(this.horse, oldWeight, newWeight);
/*      */     }
/*      */   }
/*      */ }

/* Location:           D:\Github\Mechanics\ArmorWeight.jar
 * Qualified Name:     com.zettelnet.armorweight.WeightManager
 * JD-Core Version:    0.6.2
 */