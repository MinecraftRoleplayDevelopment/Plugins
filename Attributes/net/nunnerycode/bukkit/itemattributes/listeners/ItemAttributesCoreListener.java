/*      */ package net.nunnerycode.bukkit.itemattributes.listeners;
/*      */ 
/*      */ import java.text.DecimalFormat;
/*      */ import java.util.ArrayList;
/*      */ import java.util.List;
/*      */ import net.nunnerycode.bukkit.itemattributes.ItemAttributesPlugin;
/*      */ import net.nunnerycode.bukkit.itemattributes.api.ItemAttributes;
/*      */ import net.nunnerycode.bukkit.itemattributes.api.attributes.Attribute;
/*      */ import net.nunnerycode.bukkit.itemattributes.api.attributes.AttributeHandler;
/*      */ import net.nunnerycode.bukkit.itemattributes.api.listeners.CoreListener;
/*      */ import net.nunnerycode.bukkit.itemattributes.api.managers.LanguageManager;
/*      */ import net.nunnerycode.bukkit.itemattributes.api.managers.PermissionsManager;
/*      */ import net.nunnerycode.bukkit.itemattributes.api.managers.SettingsManager;
/*      */ import net.nunnerycode.bukkit.itemattributes.events.ItemAttributesCriticalStrikeEvent;
/*      */ import net.nunnerycode.bukkit.itemattributes.events.ItemAttributesHealthUpdateEvent;
/*      */ import net.nunnerycode.bukkit.itemattributes.events.ItemAttributesStunStrikeEvent;
/*      */ import net.nunnerycode.bukkit.itemattributes.utils.ItemAttributesParseUtil;
/*      */ import org.apache.commons.lang.math.RandomUtils;
/*      */ import org.apache.commons.lang3.text.WordUtils;
/*      */ import org.bukkit.Bukkit;
/*      */ import org.bukkit.ChatColor;
/*      */ import org.bukkit.Location;
/*      */ import org.bukkit.Material;
/*      */ import org.bukkit.World;
/*      */ import org.bukkit.entity.Entity;
/*      */ import org.bukkit.entity.EntityType;
/*      */ import org.bukkit.entity.HumanEntity;
/*      */ import org.bukkit.entity.LivingEntity;
/*      */ import org.bukkit.entity.Player;
/*      */ import org.bukkit.entity.Projectile;
/*      */ import org.bukkit.event.EventHandler;
/*      */ import org.bukkit.event.EventPriority;
/*      */ import org.bukkit.event.Listener;
/*      */ import org.bukkit.event.entity.CreatureSpawnEvent;
/*      */ import org.bukkit.event.entity.EntityDamageByEntityEvent;
/*      */ import org.bukkit.event.entity.EntityDamageEvent;
/*      */ import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
/*      */ import org.bukkit.event.entity.EntityRegainHealthEvent;
/*      */ import org.bukkit.event.entity.EntityTargetEvent;
/*      */ import org.bukkit.event.entity.ProjectileLaunchEvent;
/*      */ import org.bukkit.event.inventory.InventoryCloseEvent;
/*      */ import org.bukkit.event.player.PlayerItemBreakEvent;
/*      */ import org.bukkit.event.player.PlayerItemHeldEvent;
/*      */ import org.bukkit.event.player.PlayerJoinEvent;
/*      */ import org.bukkit.event.player.PlayerRespawnEvent;
/*      */ import org.bukkit.inventory.EntityEquipment;
/*      */ import org.bukkit.inventory.ItemStack;
/*      */ import org.bukkit.inventory.PlayerInventory;
/*      */ import org.bukkit.inventory.meta.ItemMeta;
/*      */ import org.bukkit.metadata.FixedMetadataValue;
/*      */ import org.bukkit.metadata.MetadataValue;
/*      */ import org.bukkit.plugin.PluginManager;
/*      */ import org.bukkit.potion.PotionEffect;
/*      */ import org.bukkit.potion.PotionEffectType;
/*      */ 
/*      */ public final class ItemAttributesCoreListener
/*      */   implements Listener, CoreListener
/*      */ {
/*      */   private final ItemAttributesPlugin plugin;
/*      */   private final DecimalFormat decimalFormat;
/*      */ 
/*      */   public ItemAttributesCoreListener(ItemAttributesPlugin plugin)
/*      */   {
/*   51 */     this.plugin = plugin;
/*   52 */     this.decimalFormat = new DecimalFormat("#.##");
/*      */   }
/*      */ 
/*      */   @EventHandler(priority=EventPriority.MONITOR)
/*      */   public void onCreatureSpawnEvent(CreatureSpawnEvent event) {
/*   57 */     if (event.isCancelled()) {
/*   58 */       return;
/*      */     }
/*   60 */     double maxHealth = event.getEntity().getMaxHealth();
/*   61 */     MetadataValue metadataValue = new FixedMetadataValue(this.plugin, Double.valueOf(maxHealth));
/*   62 */     event.getEntity().setMetadata("itemattributes.basehealth", metadataValue);
/*      */   }
/*      */ 
/*      */   @EventHandler(priority=EventPriority.LOWEST)
/*      */   public void onInventoryCloseEventLowest(InventoryCloseEvent event) {
/*   67 */     for (HumanEntity he : event.getViewers())
/*   68 */       if (((he instanceof Player)) && (!he.isDead()))
/*      */       {
/*   71 */         Player player = (Player)he;
/*   72 */         handleLevelRequirementCheck(player);
/*   73 */         handlePermissionCheck(player);
/*      */       }
/*      */   }
/*      */ 
/*      */   private boolean handleLevelRequirementCheck(Player player) {
/*   78 */     if (player.hasPermission("itemattributes.admin.ignorelevels")) {
/*   79 */       return false;
/*      */     }
/*      */ 
/*   82 */     boolean b = false;
/*      */ 
/*   84 */     ItemStack itemInHand = player.getEquipment().getItemInHand();
/*   85 */     ItemStack helmet = player.getEquipment().getHelmet();
/*   86 */     ItemStack chestplate = player.getEquipment().getChestplate();
/*   87 */     ItemStack leggings = player.getEquipment().getLeggings();
/*   88 */     ItemStack boots = player.getEquipment().getBoots();
/*      */ 
/*   90 */     Attribute levelRequirementAttribute = getPlugin().getSettingsManager().getAttribute("LEVEL REQUIREMENT");
/*      */ 
/*   93 */     int level = (int)ItemAttributesParseUtil.getValue(getItemStackLore(itemInHand), levelRequirementAttribute);
/*   94 */     if (player.getLevel() < level) {
/*   95 */       if (player.getInventory().firstEmpty() >= 0)
/*   96 */         player.getInventory().addItem(new ItemStack[] { itemInHand });
/*      */       else {
/*   98 */         player.getWorld().dropItem(player.getLocation(), itemInHand);
/*      */       }
/*  100 */       player.getEquipment().setItemInHand(new ItemStack(Material.AIR));
/*  101 */       getPlugin().getLanguageManager().sendMessage(player, "events.unable-to-use-level", new String[][] { { "%itemname%", getItemName(itemInHand) }, { "%level%", String.valueOf(level) } });
/*      */ 
/*  103 */       b = true;
/*      */     }
/*      */ 
/*  107 */     level = (int)ItemAttributesParseUtil.getValue(getItemStackLore(helmet), levelRequirementAttribute);
/*  108 */     if (player.getLevel() < level) {
/*  109 */       if (player.getInventory().firstEmpty() >= 0)
/*  110 */         player.getInventory().addItem(new ItemStack[] { helmet });
/*      */       else {
/*  112 */         player.getWorld().dropItem(player.getLocation(), helmet);
/*      */       }
/*  114 */       player.getEquipment().setHelmet(new ItemStack(Material.AIR));
/*  115 */       getPlugin().getLanguageManager().sendMessage(player, "events.unable-to-use-level", new String[][] { { "%itemname%", getItemName(helmet) }, { "%level%", String.valueOf(level) } });
/*      */ 
/*  117 */       b = true;
/*      */     }
/*      */ 
/*  121 */     level = (int)ItemAttributesParseUtil.getValue(getItemStackLore(chestplate), levelRequirementAttribute);
/*  122 */     if (player.getLevel() < level) {
/*  123 */       if (player.getInventory().firstEmpty() >= 0)
/*  124 */         player.getInventory().addItem(new ItemStack[] { chestplate });
/*      */       else {
/*  126 */         player.getWorld().dropItem(player.getLocation(), chestplate);
/*      */       }
/*  128 */       player.getEquipment().setChestplate(new ItemStack(Material.AIR));
/*  129 */       getPlugin().getLanguageManager().sendMessage(player, "events.unable-to-use-level", new String[][] { { "%itemname%", getItemName(chestplate) }, { "%level%", String.valueOf(level) } });
/*      */ 
/*  131 */       b = true;
/*      */     }
/*      */ 
/*  135 */     level = (int)ItemAttributesParseUtil.getValue(getItemStackLore(leggings), levelRequirementAttribute);
/*  136 */     if (player.getLevel() < level) {
/*  137 */       if (player.getInventory().firstEmpty() >= 0)
/*  138 */         player.getInventory().addItem(new ItemStack[] { leggings });
/*      */       else {
/*  140 */         player.getWorld().dropItem(player.getLocation(), leggings);
/*      */       }
/*  142 */       player.getEquipment().setLeggings(new ItemStack(Material.AIR));
/*  143 */       getPlugin().getLanguageManager().sendMessage(player, "events.unable-to-use-level", new String[][] { { "%itemname%", getItemName(leggings) }, { "%level%", String.valueOf(level) } });
/*      */ 
/*  145 */       b = true;
/*      */     }
/*      */ 
/*  149 */     level = (int)ItemAttributesParseUtil.getValue(getItemStackLore(boots), levelRequirementAttribute);
/*  150 */     if (player.getLevel() < level) {
/*  151 */       if (player.getInventory().firstEmpty() >= 0)
/*  152 */         player.getInventory().addItem(new ItemStack[] { boots });
/*      */       else {
/*  154 */         player.getWorld().dropItem(player.getLocation(), boots);
/*      */       }
/*  156 */       player.getEquipment().setBoots(new ItemStack(Material.AIR));
/*  157 */       getPlugin().getLanguageManager().sendMessage(player, "events.unable-to-use-level", new String[][] { { "%itemname%", getItemName(boots) }, { "%level%", String.valueOf(level) } });
/*      */ 
/*  159 */       b = true;
/*      */     }
/*      */ 
/*  162 */     if (b) {
/*  163 */       playAttributeSounds(player.getEyeLocation(), new Attribute[] { levelRequirementAttribute });
/*      */     }
/*      */ 
/*  166 */     return b;
/*      */   }
/*      */ 
/*      */   private void playAttributeSounds(Location location, Attribute[] attributes) {
/*  170 */     for (Attribute attribute : attributes)
/*  171 */       location.getWorld().playSound(location, attribute.getSound(), 1.0F, 1.0F);
/*      */   }
/*      */ 
/*      */   private String getItemName(ItemStack itemStack)
/*      */   {
/*  176 */     String name = "";
/*  177 */     if ((itemStack.hasItemMeta()) && (itemStack.getItemMeta().hasDisplayName())) {
/*  178 */       name = ChatColor.stripColor(itemStack.getItemMeta().getDisplayName());
/*      */     } else {
/*  180 */       String matName = itemStack.getType().name();
/*  181 */       String[] splitMatName = matName.split("_");
/*  182 */       for (int i = 0; i < splitMatName.length; i++) {
/*  183 */         if (i < splitMatName.length - 1)
/*  184 */           name = name.concat(WordUtils.capitalizeFully(splitMatName[i]).concat(" "));
/*      */         else {
/*  186 */           name = name.concat(WordUtils.capitalizeFully(splitMatName[i]));
/*      */         }
/*      */       }
/*      */     }
/*  190 */     return name;
/*      */   }
/*      */ 
/*      */   private List<String> getItemStackLore(ItemStack itemStack) {
/*  194 */     List lore = new ArrayList();
/*  195 */     if ((itemStack != null) && (itemStack.hasItemMeta()) && (itemStack.getItemMeta().hasLore())) {
/*  196 */       lore.addAll(itemStack.getItemMeta().getLore());
/*      */     }
/*  198 */     return lore;
/*      */   }
/*      */ 
/*      */   public ItemAttributes getPlugin()
/*      */   {
/*  203 */     return this.plugin;
/*      */   }
/*      */ 
/*      */   private boolean handlePermissionCheck(Player player) {
/*  207 */     if (player.hasPermission("itemattributes.admin.ignorepermissions")) {
/*  208 */       return false;
/*      */     }
/*      */ 
/*  211 */     boolean b = false;
/*      */ 
/*  213 */     ItemStack itemInHand = player.getEquipment().getItemInHand();
/*  214 */     ItemStack helmet = player.getEquipment().getHelmet();
/*  215 */     ItemStack chestplate = player.getEquipment().getChestplate();
/*  216 */     ItemStack leggings = player.getEquipment().getLeggings();
/*  217 */     ItemStack boots = player.getEquipment().getBoots();
/*      */ 
/*  219 */     Attribute permissionRequirementAttribute = getPlugin().getSettingsManager().getAttribute("PERMISSION REQUIREMENT");
/*      */ 
/*  222 */     List perms = getPlugin().getAttributeHandler().getAttributeStringsFromItemStack(itemInHand, permissionRequirementAttribute);
/*      */ 
/*  224 */     for (String s : perms) {
/*  225 */       if (!getPlugin().getPermissionsManager().hasPermission(player, s)) {
/*  226 */         if (player.getInventory().firstEmpty() >= 0)
/*  227 */           player.getInventory().addItem(new ItemStack[] { itemInHand });
/*      */         else {
/*  229 */           player.getWorld().dropItem(player.getLocation(), itemInHand);
/*      */         }
/*  231 */         player.getEquipment().setItemInHand(new ItemStack(Material.AIR));
/*  232 */         getPlugin().getLanguageManager().sendMessage(player, "events.unable-to-use-permission", new String[][] { { "%itemname%", getItemName(itemInHand) } });
/*      */ 
/*  234 */         b = true;
/*  235 */         break;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  240 */     perms = getPlugin().getAttributeHandler().getAttributeStringsFromItemStack(helmet, permissionRequirementAttribute);
/*      */ 
/*  242 */     for (String s : perms) {
/*  243 */       if (!getPlugin().getPermissionsManager().hasPermission(player, s)) {
/*  244 */         if (player.getInventory().firstEmpty() >= 0)
/*  245 */           player.getInventory().addItem(new ItemStack[] { helmet });
/*      */         else {
/*  247 */           player.getWorld().dropItem(player.getLocation(), helmet);
/*      */         }
/*  249 */         player.getEquipment().setHelmet(new ItemStack(Material.AIR));
/*  250 */         getPlugin().getLanguageManager().sendMessage(player, "events.unable-to-use-permission", new String[][] { { "%itemname%", getItemName(helmet) } });
/*      */ 
/*  252 */         b = true;
/*  253 */         break;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  258 */     perms = getPlugin().getAttributeHandler().getAttributeStringsFromItemStack(chestplate, permissionRequirementAttribute);
/*      */ 
/*  260 */     for (String s : perms) {
/*  261 */       if (!getPlugin().getPermissionsManager().hasPermission(player, s)) {
/*  262 */         if (player.getInventory().firstEmpty() >= 0)
/*  263 */           player.getInventory().addItem(new ItemStack[] { chestplate });
/*      */         else {
/*  265 */           player.getWorld().dropItem(player.getLocation(), chestplate);
/*      */         }
/*  267 */         player.getEquipment().setChestplate(new ItemStack(Material.AIR));
/*  268 */         getPlugin().getLanguageManager().sendMessage(player, "events.unable-to-use-permission", new String[][] { { "%itemname%", getItemName(chestplate) } });
/*      */ 
/*  270 */         b = true;
/*  271 */         break;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  276 */     perms = getPlugin().getAttributeHandler().getAttributeStringsFromItemStack(leggings, permissionRequirementAttribute);
/*      */ 
/*  278 */     for (String s : perms) {
/*  279 */       if (!getPlugin().getPermissionsManager().hasPermission(player, s)) {
/*  280 */         if (player.getInventory().firstEmpty() >= 0)
/*  281 */           player.getInventory().addItem(new ItemStack[] { leggings });
/*      */         else {
/*  283 */           player.getWorld().dropItem(player.getLocation(), leggings);
/*      */         }
/*  285 */         player.getEquipment().setLeggings(new ItemStack(Material.AIR));
/*  286 */         getPlugin().getLanguageManager().sendMessage(player, "events.unable-to-use-permission", new String[][] { { "%itemname%", getItemName(leggings) } });
/*      */ 
/*  288 */         b = true;
/*  289 */         break;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  294 */     perms = getPlugin().getAttributeHandler().getAttributeStringsFromItemStack(boots, permissionRequirementAttribute);
/*      */ 
/*  296 */     for (String s : perms) {
/*  297 */       if (!getPlugin().getPermissionsManager().hasPermission(player, s)) {
/*  298 */         if (player.getInventory().firstEmpty() >= 0)
/*  299 */           player.getInventory().addItem(new ItemStack[] { boots });
/*      */         else {
/*  301 */           player.getWorld().dropItem(player.getLocation(), boots);
/*      */         }
/*  303 */         player.getEquipment().setBoots(new ItemStack(Material.AIR));
/*  304 */         getPlugin().getLanguageManager().sendMessage(player, "events.unable-to-use-permission", new String[][] { { "%itemname%", getItemName(boots) } });
/*      */ 
/*  306 */         b = true;
/*  307 */         break;
/*      */       }
/*      */     }
/*      */ 
/*  311 */     if (b) {
/*  312 */       playAttributeSounds(player.getEyeLocation(), new Attribute[] { permissionRequirementAttribute });
/*      */     }
/*      */ 
/*  315 */     return b;
/*      */   }
/*      */ 
/*      */   @EventHandler(priority=EventPriority.LOW)
/*      */   public void onInventoryCloseEventLow(InventoryCloseEvent event) {
/*  320 */     Attribute healthAttribute = getPlugin().getSettingsManager().getAttribute("HEALTH");
/*  321 */     if (!healthAttribute.isEnabled()) {
/*  322 */       return;
/*      */     }
/*  324 */     for (HumanEntity he : event.getViewers())
/*  325 */       if (!he.isDead())
/*      */       {
/*  328 */         ItemStack[] armorContents = he.getEquipment().getArmorContents();
/*  329 */         double d = 0.0D;
/*  330 */         for (ItemStack is : armorContents) {
/*  331 */           d += ItemAttributesParseUtil.getValue(getItemStackLore(is), healthAttribute);
/*      */         }
/*  333 */         d += ItemAttributesParseUtil.getValue(getItemStackLore(he.getEquipment().getItemInHand()), healthAttribute);
/*      */ 
/*  336 */         double currentHealth = he.getHealth();
/*  337 */         double baseMaxHealth = getPlugin().getSettingsManager().getBasePlayerHealth();
/*      */ 
/*  339 */         ItemAttributesHealthUpdateEvent healthUpdateEvent = new ItemAttributesHealthUpdateEvent(he, he.getMaxHealth(), baseMaxHealth, d);
/*      */ 
/*  341 */         Bukkit.getPluginManager().callEvent(healthUpdateEvent);
/*      */ 
/*  343 */         if (!healthUpdateEvent.isCancelled())
/*      */         {
/*  347 */           he.setMaxHealth(Math.max(healthUpdateEvent.getBaseHealth() + healthUpdateEvent.getChangeInHealth(), 1.0D));
/*  348 */           he.setHealth(Math.min(Math.max(currentHealth, 0.0D), he.getMaxHealth()));
/*  349 */           if ((he instanceof Player)) {
/*  350 */             ((Player)he).setHealthScale(he.getMaxHealth());
/*      */           }
/*      */ 
/*  353 */           playAttributeSounds(he.getEyeLocation(), new Attribute[] { healthAttribute });
/*      */         }
/*      */       }
/*      */   }
/*      */ 
/*  359 */   @EventHandler(priority=EventPriority.LOWEST)
/*      */   public void onPlayerJoinEventLowest(PlayerJoinEvent event) { Player player = event.getPlayer();
/*  360 */     handleLevelRequirementCheck(player);
/*  361 */     handlePermissionCheck(player); }
/*      */ 
/*      */   @EventHandler(priority=EventPriority.LOW)
/*      */   public void onPlayerJoinEventLow(PlayerJoinEvent event)
/*      */   {
/*  366 */     Attribute healthAttribute = getPlugin().getSettingsManager().getAttribute("HEALTH");
/*  367 */     if (!healthAttribute.isEnabled()) {
/*  368 */       return;
/*      */     }
/*  370 */     ItemStack[] armorContents = event.getPlayer().getEquipment().getArmorContents();
/*  371 */     double d = 0.0D;
/*  372 */     for (ItemStack is : armorContents) {
/*  373 */       d += ItemAttributesParseUtil.getValue(getItemStackLore(is), healthAttribute);
/*      */     }
/*  375 */     d += ItemAttributesParseUtil.getValue(getItemStackLore(event.getPlayer().getItemInHand()), healthAttribute);
/*      */ 
/*  377 */     double currentHealth = event.getPlayer().getHealth();
/*  378 */     double baseMaxHealth = getPlugin().getSettingsManager().getBasePlayerHealth();
/*      */ 
/*  380 */     ItemAttributesHealthUpdateEvent healthUpdateEvent = new ItemAttributesHealthUpdateEvent(event.getPlayer(), event.getPlayer().getMaxHealth(), baseMaxHealth, d);
/*      */ 
/*  382 */     Bukkit.getPluginManager().callEvent(healthUpdateEvent);
/*      */ 
/*  384 */     if (healthUpdateEvent.isCancelled()) {
/*  385 */       return;
/*      */     }
/*      */ 
/*  388 */     event.getPlayer().setMaxHealth(Math.max(healthUpdateEvent.getBaseHealth() + healthUpdateEvent.getChangeInHealth(), 1.0D));
/*      */ 
/*  390 */     event.getPlayer().setHealth(Math.min(Math.max(currentHealth, 0.0D), event.getPlayer().getMaxHealth()));
/*  391 */     event.getPlayer().setHealthScale(event.getPlayer().getMaxHealth());
/*  392 */     playAttributeSounds(event.getPlayer().getEyeLocation(), new Attribute[] { healthAttribute });
/*      */   }
/*      */ 
/*      */   @EventHandler(priority=EventPriority.LOWEST)
/*      */   public void onEntityTargetEventLowest(EntityTargetEvent event) {
/*  397 */     Attribute healthAttribute = getPlugin().getSettingsManager().getAttribute("HEALTH");
/*  398 */     if ((event.isCancelled()) || (!(event.getEntity() instanceof LivingEntity)) || ((event.getEntity() instanceof Player)) || (!healthAttribute.isEnabled()))
/*      */     {
/*  400 */       return;
/*      */     }
/*  402 */     LivingEntity entity = (LivingEntity)event.getEntity();
/*  403 */     ItemStack[] armorContents = entity.getEquipment().getArmorContents();
/*  404 */     double d = 0.0D;
/*  405 */     for (ItemStack is : armorContents) {
/*  406 */       d += ItemAttributesParseUtil.getValue(getItemStackLore(is), healthAttribute);
/*      */     }
/*  408 */     d += ItemAttributesParseUtil.getValue(getItemStackLore(entity.getEquipment().getItemInHand()), healthAttribute);
/*      */ 
/*  410 */     double currentHealth = entity.getHealth();
/*  411 */     entity.resetMaxHealth();
/*  412 */     double baseMaxHealth = entity.getMaxHealth();
/*  413 */     if (entity.hasMetadata("itemattributes.basehealth")) {
/*  414 */       List metadataValueList = entity.getMetadata("itemattributes.basehealth");
/*  415 */       for (MetadataValue mv : metadataValueList) {
/*  416 */         if (mv.getOwningPlugin().equals(getPlugin())) {
/*  417 */           baseMaxHealth = mv.asDouble();
/*  418 */           break;
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/*  423 */     ItemAttributesHealthUpdateEvent healthUpdateEvent = new ItemAttributesHealthUpdateEvent(entity, entity.getMaxHealth(), baseMaxHealth, d);
/*      */ 
/*  425 */     Bukkit.getPluginManager().callEvent(healthUpdateEvent);
/*      */ 
/*  427 */     if (healthUpdateEvent.isCancelled()) {
/*  428 */       return;
/*      */     }
/*      */ 
/*  431 */     entity.setMaxHealth(Math.max(baseMaxHealth + d, 1.0D));
/*  432 */     entity.setHealth(Math.min(Math.max(currentHealth, 0.0D), entity.getMaxHealth()));
/*  433 */     playAttributeSounds(event.getEntity().getLocation().add(0.0D, 1.0D, 0.0D), new Attribute[] { healthAttribute });
/*      */   }
/*      */ 
/*      */   @EventHandler(priority=EventPriority.LOWEST)
/*      */   public void onPlayerRespawnEventLowest(PlayerRespawnEvent event) {
/*  438 */     Player player = event.getPlayer();
/*  439 */     handleLevelRequirementCheck(player);
/*  440 */     handlePermissionCheck(player);
/*      */   }
/*      */ 
/*      */   @EventHandler(priority=EventPriority.LOW)
/*      */   public void onPlayerRespawnEventLow(PlayerRespawnEvent event) {
/*  445 */     Attribute healthAttribute = getPlugin().getSettingsManager().getAttribute("HEALTH");
/*  446 */     if (!healthAttribute.isEnabled()) {
/*  447 */       return;
/*      */     }
/*  449 */     ItemStack[] armorContents = event.getPlayer().getEquipment().getArmorContents();
/*  450 */     double d = 0.0D;
/*  451 */     for (ItemStack is : armorContents) {
/*  452 */       d += ItemAttributesParseUtil.getValue(getItemStackLore(is), healthAttribute);
/*      */     }
/*  454 */     d += ItemAttributesParseUtil.getValue(getItemStackLore(event.getPlayer().getItemInHand()), healthAttribute);
/*      */ 
/*  456 */     double currentHealth = event.getPlayer().getHealth();
/*  457 */     double baseMaxHealth = getPlugin().getSettingsManager().getBasePlayerHealth();
/*      */ 
/*  459 */     ItemAttributesHealthUpdateEvent healthUpdateEvent = new ItemAttributesHealthUpdateEvent(event.getPlayer(), event.getPlayer().getMaxHealth(), baseMaxHealth, d);
/*      */ 
/*  461 */     Bukkit.getPluginManager().callEvent(healthUpdateEvent);
/*      */ 
/*  463 */     if (healthUpdateEvent.isCancelled()) {
/*  464 */       return;
/*      */     }
/*      */ 
/*  467 */     event.getPlayer().setMaxHealth(Math.max(healthUpdateEvent.getBaseHealth() + healthUpdateEvent.getChangeInHealth(), 1.0D));
/*      */ 
/*  469 */     event.getPlayer().setHealth(Math.min(Math.max(currentHealth, 0.0D), event.getPlayer().getMaxHealth()));
/*  470 */     event.getPlayer().setHealthScale(event.getPlayer().getMaxHealth());
/*  471 */     playAttributeSounds(event.getPlayer().getEyeLocation(), new Attribute[] { healthAttribute });
/*      */   }
/*      */ 
/*      */   @EventHandler(priority=EventPriority.LOWEST)
/*      */   public void onItemBreakEventLowest(PlayerItemBreakEvent event) {
/*  476 */     Attribute healthAttribute = getPlugin().getSettingsManager().getAttribute("HEALTH");
/*  477 */     if (!healthAttribute.isEnabled()) {
/*  478 */       return;
/*      */     }
/*  480 */     ItemStack[] armorContents = event.getPlayer().getEquipment().getArmorContents();
/*  481 */     double d = 0.0D;
/*  482 */     for (ItemStack is : armorContents) {
/*  483 */       d += ItemAttributesParseUtil.getValue(getItemStackLore(is), healthAttribute);
/*      */     }
/*  485 */     d += ItemAttributesParseUtil.getValue(getItemStackLore(event.getPlayer().getItemInHand()), healthAttribute);
/*      */ 
/*  487 */     double currentHealth = event.getPlayer().getHealth();
/*  488 */     double baseMaxHealth = getPlugin().getSettingsManager().getBasePlayerHealth();
/*      */ 
/*  490 */     ItemAttributesHealthUpdateEvent healthUpdateEvent = new ItemAttributesHealthUpdateEvent(event.getPlayer(), event.getPlayer().getMaxHealth(), baseMaxHealth, d);
/*      */ 
/*  492 */     Bukkit.getPluginManager().callEvent(healthUpdateEvent);
/*      */ 
/*  494 */     if (healthUpdateEvent.isCancelled()) {
/*  495 */       return;
/*      */     }
/*      */ 
/*  498 */     event.getPlayer().setMaxHealth(Math.max(healthUpdateEvent.getBaseHealth() + healthUpdateEvent.getChangeInHealth(), 1.0D));
/*      */ 
/*  500 */     event.getPlayer().setHealth(Math.min(Math.max(currentHealth, 0.0D), event.getPlayer().getMaxHealth()));
/*  501 */     event.getPlayer().setHealthScale(event.getPlayer().getMaxHealth());
/*  502 */     playAttributeSounds(event.getPlayer().getEyeLocation(), new Attribute[] { healthAttribute });
/*      */   }
/*      */ 
/*      */   @EventHandler(priority=EventPriority.LOWEST)
/*      */   public void onItemHeldEventLowest(PlayerItemHeldEvent event) {
/*  507 */     Player player = event.getPlayer();
/*  508 */     handleLevelRequirementCheckSlot(player, event.getNewSlot());
/*  509 */     handlePermissionCheck(player, event.getNewSlot());
/*      */   }
/*      */ 
/*      */   private boolean handleLevelRequirementCheckSlot(Player player, int i) {
/*  513 */     if (player.hasPermission("itemattributes.admin.ignorelevels")) {
/*  514 */       return false;
/*      */     }
/*      */ 
/*  517 */     boolean b = false;
/*      */ 
/*  519 */     ItemStack itemInHand = player.getInventory().getItem(i);
/*  520 */     ItemStack helmet = player.getEquipment().getHelmet();
/*  521 */     ItemStack chestplate = player.getEquipment().getChestplate();
/*  522 */     ItemStack leggings = player.getEquipment().getLeggings();
/*  523 */     ItemStack boots = player.getEquipment().getBoots();
/*      */ 
/*  525 */     Attribute levelRequirementAttribute = getPlugin().getSettingsManager().getAttribute("LEVEL REQUIREMENT");
/*      */ 
/*  528 */     int level = (int)ItemAttributesParseUtil.getValue(getItemStackLore(itemInHand), levelRequirementAttribute);
/*  529 */     if (player.getLevel() < level) {
/*  530 */       if (player.getInventory().firstEmpty() >= 0)
/*  531 */         player.getInventory().addItem(new ItemStack[] { itemInHand });
/*      */       else {
/*  533 */         player.getWorld().dropItem(player.getLocation(), itemInHand);
/*      */       }
/*  535 */       player.getInventory().setItem(i, new ItemStack(Material.AIR));
/*  536 */       getPlugin().getLanguageManager().sendMessage(player, "events.unable-to-use-level", new String[][] { { "%itemname%", getItemName(itemInHand) }, { "%level%", String.valueOf(level) } });
/*      */ 
/*  538 */       b = true;
/*      */     }
/*      */ 
/*  542 */     level = (int)ItemAttributesParseUtil.getValue(getItemStackLore(helmet), levelRequirementAttribute);
/*  543 */     if (player.getLevel() < level) {
/*  544 */       if (player.getInventory().firstEmpty() >= 0)
/*  545 */         player.getInventory().addItem(new ItemStack[] { helmet });
/*      */       else {
/*  547 */         player.getWorld().dropItem(player.getLocation(), helmet);
/*      */       }
/*  549 */       player.getEquipment().setHelmet(new ItemStack(Material.AIR));
/*  550 */       getPlugin().getLanguageManager().sendMessage(player, "events.unable-to-use-level", new String[][] { { "%itemname%", getItemName(helmet) }, { "%level%", String.valueOf(level) } });
/*      */ 
/*  552 */       b = true;
/*      */     }
/*      */ 
/*  556 */     level = (int)ItemAttributesParseUtil.getValue(getItemStackLore(chestplate), levelRequirementAttribute);
/*  557 */     if (player.getLevel() < level) {
/*  558 */       if (player.getInventory().firstEmpty() >= 0)
/*  559 */         player.getInventory().addItem(new ItemStack[] { chestplate });
/*      */       else {
/*  561 */         player.getWorld().dropItem(player.getLocation(), chestplate);
/*      */       }
/*  563 */       player.getEquipment().setChestplate(new ItemStack(Material.AIR));
/*  564 */       getPlugin().getLanguageManager().sendMessage(player, "events.unable-to-use-level", new String[][] { { "%itemname%", getItemName(chestplate) }, { "%level%", String.valueOf(level) } });
/*      */ 
/*  566 */       b = true;
/*      */     }
/*      */ 
/*  570 */     level = (int)ItemAttributesParseUtil.getValue(getItemStackLore(leggings), levelRequirementAttribute);
/*  571 */     if (player.getLevel() < level) {
/*  572 */       if (player.getInventory().firstEmpty() >= 0)
/*  573 */         player.getInventory().addItem(new ItemStack[] { leggings });
/*      */       else {
/*  575 */         player.getWorld().dropItem(player.getLocation(), leggings);
/*      */       }
/*  577 */       player.getEquipment().setLeggings(new ItemStack(Material.AIR));
/*  578 */       getPlugin().getLanguageManager().sendMessage(player, "events.unable-to-use-level", new String[][] { { "%itemname%", getItemName(leggings) }, { "%level%", String.valueOf(level) } });
/*      */ 
/*  580 */       b = true;
/*      */     }
/*      */ 
/*  584 */     level = (int)ItemAttributesParseUtil.getValue(getItemStackLore(boots), levelRequirementAttribute);
/*  585 */     if (player.getLevel() < level) {
/*  586 */       if (player.getInventory().firstEmpty() >= 0)
/*  587 */         player.getInventory().addItem(new ItemStack[] { boots });
/*      */       else {
/*  589 */         player.getWorld().dropItem(player.getLocation(), boots);
/*      */       }
/*  591 */       player.getEquipment().setBoots(new ItemStack(Material.AIR));
/*  592 */       getPlugin().getLanguageManager().sendMessage(player, "events.unable-to-use-level", new String[][] { { "%itemname%", getItemName(boots) }, { "%level%", String.valueOf(level) } });
/*      */ 
/*  594 */       b = true;
/*      */     }
/*      */ 
/*  597 */     if (b) {
/*  598 */       playAttributeSounds(player.getEyeLocation(), new Attribute[] { levelRequirementAttribute });
/*      */     }
/*      */ 
/*  601 */     return b;
/*      */   }
/*      */ 
/*      */   private boolean handlePermissionCheck(Player player, int slot) {
/*  605 */     if (player.hasPermission("itemattributes.admin.ignorepermissions")) {
/*  606 */       return false;
/*      */     }
/*      */ 
/*  609 */     boolean b = false;
/*      */ 
/*  611 */     ItemStack itemInHand = player.getInventory().getItem(slot);
/*  612 */     ItemStack helmet = player.getEquipment().getHelmet();
/*  613 */     ItemStack chestplate = player.getEquipment().getChestplate();
/*  614 */     ItemStack leggings = player.getEquipment().getLeggings();
/*  615 */     ItemStack boots = player.getEquipment().getBoots();
/*      */ 
/*  617 */     Attribute permissionRequirementAttribute = getPlugin().getSettingsManager().getAttribute("PERMISSION REQUIREMENT");
/*      */ 
/*  621 */     List perms = getPlugin().getAttributeHandler().getAttributeStringsFromItemStack(itemInHand, permissionRequirementAttribute);
/*      */ 
/*  623 */     for (String s : perms) {
/*  624 */       if (!getPlugin().getPermissionsManager().hasPermission(player, s)) {
/*  625 */         if (player.getInventory().firstEmpty() >= 0)
/*  626 */           player.getInventory().addItem(new ItemStack[] { itemInHand });
/*      */         else {
/*  628 */           player.getWorld().dropItem(player.getLocation(), itemInHand);
/*      */         }
/*  630 */         player.getInventory().setItem(slot, new ItemStack(Material.AIR));
/*  631 */         getPlugin().getLanguageManager().sendMessage(player, "events.unable-to-use-permission", new String[][] { { "%itemname%", getItemName(itemInHand) } });
/*      */ 
/*  633 */         b = true;
/*  634 */         break;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  639 */     perms = getPlugin().getAttributeHandler().getAttributeStringsFromItemStack(helmet, permissionRequirementAttribute);
/*      */ 
/*  641 */     for (String s : perms) {
/*  642 */       if (!getPlugin().getPermissionsManager().hasPermission(player, s)) {
/*  643 */         if (player.getInventory().firstEmpty() >= 0)
/*  644 */           player.getInventory().addItem(new ItemStack[] { helmet });
/*      */         else {
/*  646 */           player.getWorld().dropItem(player.getLocation(), helmet);
/*      */         }
/*  648 */         player.getEquipment().setHelmet(new ItemStack(Material.AIR));
/*  649 */         getPlugin().getLanguageManager().sendMessage(player, "events.unable-to-use-permission", new String[][] { { "%itemname%", getItemName(helmet) } });
/*      */ 
/*  651 */         b = true;
/*  652 */         break;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  657 */     perms = getPlugin().getAttributeHandler().getAttributeStringsFromItemStack(chestplate, permissionRequirementAttribute);
/*      */ 
/*  659 */     for (String s : perms) {
/*  660 */       if (!getPlugin().getPermissionsManager().hasPermission(player, s)) {
/*  661 */         if (player.getInventory().firstEmpty() >= 0)
/*  662 */           player.getInventory().addItem(new ItemStack[] { chestplate });
/*      */         else {
/*  664 */           player.getWorld().dropItem(player.getLocation(), chestplate);
/*      */         }
/*  666 */         player.getEquipment().setChestplate(new ItemStack(Material.AIR));
/*  667 */         getPlugin().getLanguageManager().sendMessage(player, "events.unable-to-use-permission", new String[][] { { "%itemname%", getItemName(chestplate) } });
/*      */ 
/*  669 */         b = true;
/*  670 */         break;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  675 */     perms = getPlugin().getAttributeHandler().getAttributeStringsFromItemStack(leggings, permissionRequirementAttribute);
/*      */ 
/*  677 */     for (String s : perms) {
/*  678 */       if (!getPlugin().getPermissionsManager().hasPermission(player, s)) {
/*  679 */         if (player.getInventory().firstEmpty() >= 0)
/*  680 */           player.getInventory().addItem(new ItemStack[] { leggings });
/*      */         else {
/*  682 */           player.getWorld().dropItem(player.getLocation(), leggings);
/*      */         }
/*  684 */         player.getEquipment().setLeggings(new ItemStack(Material.AIR));
/*  685 */         getPlugin().getLanguageManager().sendMessage(player, "events.unable-to-use-permission", new String[][] { { "%itemname%", getItemName(leggings) } });
/*      */ 
/*  687 */         b = true;
/*  688 */         break;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  693 */     perms = getPlugin().getAttributeHandler().getAttributeStringsFromItemStack(boots, permissionRequirementAttribute);
/*      */ 
/*  695 */     for (String s : perms) {
/*  696 */       if (!getPlugin().getPermissionsManager().hasPermission(player, s)) {
/*  697 */         if (player.getInventory().firstEmpty() >= 0)
/*  698 */           player.getInventory().addItem(new ItemStack[] { boots });
/*      */         else {
/*  700 */           player.getWorld().dropItem(player.getLocation(), boots);
/*      */         }
/*  702 */         player.getEquipment().setBoots(new ItemStack(Material.AIR));
/*  703 */         getPlugin().getLanguageManager().sendMessage(player, "events.unable-to-use-permission", new String[][] { { "%itemname%", getItemName(boots) } });
/*      */ 
/*  705 */         b = true;
/*  706 */         break;
/*      */       }
/*      */     }
/*      */ 
/*  710 */     if (b) {
/*  711 */       playAttributeSounds(player.getEyeLocation(), new Attribute[] { permissionRequirementAttribute });
/*      */     }
/*      */ 
/*  714 */     return b;
/*      */   }
/*      */ 
/*      */   @EventHandler(priority=EventPriority.LOW)
/*      */   public void onItemHeldEventLow(PlayerItemHeldEvent event) {
/*  719 */     Attribute healthAttribute = getPlugin().getSettingsManager().getAttribute("HEALTH");
/*  720 */     if (!healthAttribute.isEnabled()) {
/*  721 */       return;
/*      */     }
/*  723 */     ItemStack[] armorContents = event.getPlayer().getEquipment().getArmorContents();
/*  724 */     double d = 0.0D;
/*  725 */     for (ItemStack is : armorContents) {
/*  726 */       d += ItemAttributesParseUtil.getValue(getItemStackLore(is), healthAttribute);
/*      */     }
/*  728 */     d += ItemAttributesParseUtil.getValue(getItemStackLore(event.getPlayer().getInventory().getItem(event.getNewSlot())), healthAttribute);
/*      */ 
/*  730 */     double currentHealth = event.getPlayer().getHealth();
/*  731 */     double baseMaxHealth = getPlugin().getSettingsManager().getBasePlayerHealth();
/*      */ 
/*  733 */     ItemAttributesHealthUpdateEvent healthUpdateEvent = new ItemAttributesHealthUpdateEvent(event.getPlayer(), event.getPlayer().getMaxHealth(), baseMaxHealth, d);
/*      */ 
/*  735 */     Bukkit.getPluginManager().callEvent(healthUpdateEvent);
/*      */ 
/*  737 */     if (healthUpdateEvent.isCancelled()) {
/*  738 */       return;
/*      */     }
/*      */ 
/*  741 */     event.getPlayer().setMaxHealth(Math.max(healthUpdateEvent.getBaseHealth() + healthUpdateEvent.getChangeInHealth(), 1.0D));
/*      */ 
/*  743 */     event.getPlayer().setHealth(Math.min(Math.max(currentHealth, 0.0D), event.getPlayer().getMaxHealth()));
/*  744 */     event.getPlayer().setHealthScale(event.getPlayer().getMaxHealth());
/*  745 */     playAttributeSounds(event.getPlayer().getEyeLocation(), new Attribute[] { healthAttribute });
/*      */   }
/*      */ 
/*      */   @EventHandler(priority=EventPriority.LOWEST)
/*      */   public void onEntityRegainHealthEventLowest(EntityRegainHealthEvent event) {
/*  750 */     if (!(event.getEntity() instanceof Player)) {
/*  751 */       return;
/*      */     }
/*  753 */     Player player = (Player)event.getEntity();
/*  754 */     handleLevelRequirementCheck(player);
/*  755 */     handlePermissionCheck(player);
/*      */   }
/*      */ 
/*      */   @EventHandler(priority=EventPriority.LOW)
/*      */   public void onEntityRegainHealthEventLow(EntityRegainHealthEvent event) {
/*  760 */     double amount = event.getAmount();
/*  761 */     Attribute regenerationAttribute = getPlugin().getSettingsManager().getAttribute("REGENERATION");
/*  762 */     if ((event.getEntity() instanceof LivingEntity)) {
/*  763 */       LivingEntity le = (LivingEntity)event.getEntity();
/*  764 */       ItemStack[] armorContents = le.getEquipment().getArmorContents();
/*  765 */       for (ItemStack is : armorContents) {
/*  766 */         amount += ItemAttributesParseUtil.getValue(getItemStackLore(is), regenerationAttribute);
/*      */       }
/*  768 */       amount += ItemAttributesParseUtil.getValue(getItemStackLore(le.getEquipment().getItemInHand()), regenerationAttribute);
/*      */     }
/*      */ 
/*  771 */     event.setAmount(amount);
/*      */   }
/*      */ 
/*      */   @EventHandler(priority=EventPriority.MONITOR)
/*      */   public void onProjectileLaunchEventMonitor(ProjectileLaunchEvent event) {
/*  776 */     Projectile projectile = event.getEntity();
/*  777 */     if (projectile.getShooter() == null) {
/*  778 */       return;
/*      */     }
/*  780 */     LivingEntity le = projectile.getShooter();
/*  781 */     Material shotItemMaterial = getMaterialFromEntityType(projectile.getType());
/*  782 */     ItemStack shotItem = new ItemStack(shotItemMaterial);
/*  783 */     ItemStack shootingItem = null;
/*      */ 
/*  785 */     if ((le.getEquipment() != null) && (le.getEquipment().getItemInHand() != null)) {
/*  786 */       shootingItem = le.getEquipment().getItemInHand();
/*      */     }
/*      */ 
/*  789 */     if ((le instanceof Player)) {
/*  790 */       ItemStack[] contents = ((Player)le).getInventory().getContents();
/*  791 */       for (ItemStack is : contents) {
/*  792 */         if ((is != null) && (is.getType() != null))
/*      */         {
/*  795 */           if (is.getType() == shotItemMaterial) {
/*  796 */             shotItem = is;
/*  797 */             break;
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*  802 */     double criticalRate = 0.0D;
/*  803 */     double criticalDamage = 0.0D;
/*  804 */     double arrowDamage = 0.0D;
/*  805 */     double bowDamage = 0.0D;
/*  806 */     double armorDamage = 0.0D;
/*  807 */     double armorPenetration = 0.0D;
/*  808 */     double stunRate = 0.0D;
/*  809 */     int stunLength = 0;
/*      */ 
/*  811 */     Attribute damageAttribute = getPlugin().getSettingsManager().getAttribute("DAMAGE");
/*  812 */     Attribute rangedDamageAttribute = getPlugin().getSettingsManager().getAttribute("RANGED DAMAGE");
/*  813 */     Attribute criticalRateAttribute = getPlugin().getSettingsManager().getAttribute("CRITICAL RATE");
/*  814 */     Attribute criticalDamageAttribute = getPlugin().getSettingsManager().getAttribute("CRITICAL DAMAGE");
/*  815 */     Attribute stunRateAttribute = getPlugin().getSettingsManager().getAttribute("STUN RATE");
/*  816 */     Attribute stunLengthAttribute = getPlugin().getSettingsManager().getAttribute("STUN LENGTH");
/*  817 */     Attribute armorPenetrationAttribute = getPlugin().getSettingsManager().getAttribute("ARMOR PENETRATION");
/*      */ 
/*  819 */     arrowDamage += ItemAttributesParseUtil.getValue(getItemStackLore(shotItem), damageAttribute);
/*  820 */     arrowDamage += ItemAttributesParseUtil.getValue(getItemStackLore(shotItem), rangedDamageAttribute);
/*  821 */     criticalRate += ItemAttributesParseUtil.getValue(getItemStackLore(shotItem), criticalRateAttribute);
/*  822 */     criticalDamage += ItemAttributesParseUtil.getValue(getItemStackLore(shotItem), criticalDamageAttribute);
/*  823 */     armorPenetration += ItemAttributesParseUtil.getValue(getItemStackLore(shotItem), armorPenetrationAttribute);
/*  824 */     stunRate += ItemAttributesParseUtil.getValue(getItemStackLore(shotItem), stunRateAttribute);
/*  825 */     stunLength = (int)(stunLength + ItemAttributesParseUtil.getValue(getItemStackLore(shotItem), stunLengthAttribute));
/*      */ 
/*  827 */     if (shootingItem != null) {
/*  828 */       bowDamage += ItemAttributesParseUtil.getValue(getItemStackLore(shootingItem), damageAttribute);
/*      */ 
/*  830 */       bowDamage += ItemAttributesParseUtil.getValue(getItemStackLore(shootingItem), rangedDamageAttribute);
/*      */ 
/*  832 */       criticalRate += ItemAttributesParseUtil.getValue(getItemStackLore(shootingItem), criticalRateAttribute);
/*      */ 
/*  834 */       criticalDamage += ItemAttributesParseUtil.getValue(getItemStackLore(shootingItem), criticalDamageAttribute);
/*      */ 
/*  836 */       armorPenetration += ItemAttributesParseUtil.getValue(getItemStackLore(shootingItem), armorPenetrationAttribute);
/*      */ 
/*  838 */       stunRate += ItemAttributesParseUtil.getValue(getItemStackLore(shootingItem), stunRateAttribute);
/*      */ 
/*  840 */       stunLength = (int)(stunLength + ItemAttributesParseUtil.getValue(getItemStackLore(shootingItem), stunLengthAttribute));
/*      */     }
/*      */ 
/*  843 */     for (ItemStack is : le.getEquipment().getArmorContents()) {
/*  844 */       armorDamage += ItemAttributesParseUtil.getValue(getItemStackLore(is), damageAttribute);
/*  845 */       armorDamage += ItemAttributesParseUtil.getValue(getItemStackLore(is), rangedDamageAttribute);
/*  846 */       criticalRate += ItemAttributesParseUtil.getValue(getItemStackLore(is), criticalRateAttribute);
/*      */ 
/*  848 */       criticalDamage += ItemAttributesParseUtil.getValue(getItemStackLore(is), criticalDamageAttribute);
/*      */ 
/*  850 */       armorPenetration += ItemAttributesParseUtil.getValue(getItemStackLore(is), armorPenetrationAttribute);
/*  851 */       stunRate += ItemAttributesParseUtil.getValue(getItemStackLore(is), stunRateAttribute);
/*  852 */       stunLength = (int)(stunLength + ItemAttributesParseUtil.getValue(getItemStackLore(is), stunLengthAttribute));
/*      */     }
/*      */ 
/*  855 */     double totalDamage = arrowDamage + bowDamage + armorDamage;
/*      */ 
/*  857 */     event.getEntity().setMetadata("itemattributes.damage", new FixedMetadataValue(this.plugin, Double.valueOf(totalDamage)));
/*  858 */     event.getEntity().setMetadata("itemattributes.criticalrate", new FixedMetadataValue(this.plugin, Double.valueOf(criticalRate)));
/*  859 */     event.getEntity().setMetadata("itemattributes.criticaldamage", new FixedMetadataValue(this.plugin, Double.valueOf(criticalDamage)));
/*  860 */     event.getEntity().setMetadata("itemattributes.armorpenetration", new FixedMetadataValue(this.plugin, Double.valueOf(armorPenetration)));
/*      */ 
/*  862 */     event.getEntity().setMetadata("itemattributes.stunrate", new FixedMetadataValue(this.plugin, Double.valueOf(stunRate)));
/*  863 */     event.getEntity().setMetadata("itemattributes.stunlength", new FixedMetadataValue(this.plugin, Integer.valueOf(stunLength)));
/*      */   }
/*      */ 
/*      */   private Material getMaterialFromEntityType(EntityType entityType) {
/*  867 */     switch (1.$SwitchMap$org$bukkit$entity$EntityType[entityType.ordinal()]) {
/*      */     case 1:
/*  869 */       return Material.ARROW;
/*      */     case 2:
/*  871 */       return Material.SNOW_BALL;
/*      */     case 3:
/*  873 */       return Material.FIREBALL;
/*      */     case 4:
/*  875 */       return Material.FIREBALL;
/*      */     case 5:
/*  877 */       return Material.ENDER_PEARL;
/*      */     case 6:
/*  879 */       return Material.EXP_BOTTLE;
/*      */     case 7:
/*  881 */       return Material.FIREBALL;
/*      */     case 8:
/*  883 */       return Material.POTION;
/*      */     case 9:
/*  885 */       return Material.EGG;
/*      */     case 10:
/*  887 */       return Material.FISHING_ROD;
/*      */     }
/*  889 */     return null;
/*      */   }
/*      */ 
/*      */   @EventHandler(priority=EventPriority.LOWEST)
/*      */   public void onEntityDamageByEntityEventLowest(EntityDamageByEntityEvent event)
/*      */   {
/*  895 */     if (event.isCancelled()) {
/*  896 */       return;
/*      */     }
/*      */ 
/*  899 */     boolean b = false;
/*      */ 
/*  901 */     if ((event.getEntity() instanceof Player)) {
/*  902 */       b = (handleLevelRequirementCheck((Player)event.getEntity())) || (handlePermissionCheck((Player)event.getEntity()));
/*      */     }
/*      */ 
/*  906 */     if ((event.getDamager() instanceof Player)) {
/*  907 */       b = (handleLevelRequirementCheck((Player)event.getDamager())) || (handlePermissionCheck((Player)event.getDamager()));
/*      */     }
/*      */ 
/*  911 */     if (((event.getDamager() instanceof Projectile)) && ((((Projectile)event.getDamager()).getShooter() instanceof Player))) {
/*  912 */       b = (handleLevelRequirementCheck((Player)((Projectile)event.getDamager()).getShooter())) || (handlePermissionCheck((Player)((Projectile)event.getDamager()).getShooter()));
/*      */     }
/*      */ 
/*  916 */     event.setCancelled(b);
/*      */ 
/*  918 */     if (b)
/*  919 */       event.setDamage(0.0D);
/*      */   }
/*      */ 
/*      */   @EventHandler(priority=EventPriority.LOW)
/*      */   public void onEntityDamageByEntityEventLow(EntityDamageByEntityEvent event)
/*      */   {
/*  925 */     if (event.isCancelled()) {
/*  926 */       return;
/*      */     }
/*      */ 
/*  929 */     double originalDamage = event.getDamage();
/*  930 */     if ((event.getDamager() instanceof Player)) {
/*  931 */       originalDamage = getPlugin().getSettingsManager().isItemOnlyDamageSystemEnabled() ? getPlugin().getSettingsManager().getItemOnlyDamageSystemBaseDamage() : event.getDamage();
/*      */     }
/*      */ 
/*  937 */     double damagerEquipmentDamage = 0.0D;
/*  938 */     double damagerCriticalChance = getPlugin().getSettingsManager().getBaseCriticalRate();
/*  939 */     double damagerCriticalDamage = getPlugin().getSettingsManager().getBaseCriticalDamage();
/*  940 */     double armorPenetration = 0.0D;
/*  941 */     double stunRate = getPlugin().getSettingsManager().getBaseStunRate();
/*  942 */     int stunLength = getPlugin().getSettingsManager().getBaseStunLength();
/*  943 */     double dodgeRate = getPlugin().getSettingsManager().getBaseDodgeRate();
/*      */ 
/*  945 */     Attribute damageAttribute = getPlugin().getSettingsManager().getAttribute("DAMAGE");
/*  946 */     Attribute meleeDamageAttribute = getPlugin().getSettingsManager().getAttribute("MELEE DAMAGE");
/*  947 */     Attribute criticalRateAttribute = getPlugin().getSettingsManager().getAttribute("CRITICAL RATE");
/*  948 */     Attribute criticalDamageAttribute = getPlugin().getSettingsManager().getAttribute("CRITICAL DAMAGE");
/*  949 */     Attribute stunRateAttribute = getPlugin().getSettingsManager().getAttribute("STUN RATE");
/*  950 */     Attribute stunLengthAttribute = getPlugin().getSettingsManager().getAttribute("STUN LENGTH");
/*  951 */     Attribute dodgeRateAttribute = getPlugin().getSettingsManager().getAttribute("DODGE RATE");
/*  952 */     Attribute armorAttribute = getPlugin().getSettingsManager().getAttribute("ARMOR");
/*  953 */     Attribute armorPenetrationAttribute = getPlugin().getSettingsManager().getAttribute("ARMOR PENETRATION");
/*      */ 
/*  955 */     if ((event.getDamager() instanceof Projectile)) {
/*  956 */       Projectile projectile = (Projectile)event.getDamager();
/*  957 */       LivingEntity shooter = projectile.getShooter();
/*  958 */       if (shooter != null) {
/*  959 */         if (projectile.hasMetadata("itemattributes.damage")) {
/*  960 */           List metadataValueList = projectile.getMetadata("itemattributes.damage");
/*  961 */           for (MetadataValue mv : metadataValueList) {
/*  962 */             if (mv.getOwningPlugin().equals(getPlugin())) {
/*  963 */               damagerEquipmentDamage += mv.asDouble();
/*  964 */               break;
/*      */             }
/*      */           }
/*      */         }
/*  968 */         if (projectile.hasMetadata("itemattributes.criticalrate")) {
/*  969 */           List metadataValueList = projectile.getMetadata("itemattributes.criticalrate");
/*  970 */           for (MetadataValue mv : metadataValueList) {
/*  971 */             if (mv.getOwningPlugin().equals(getPlugin())) {
/*  972 */               damagerCriticalChance += mv.asDouble();
/*  973 */               break;
/*      */             }
/*      */           }
/*      */         }
/*  977 */         if (projectile.hasMetadata("itemattributes.criticaldamage")) {
/*  978 */           List metadataValueList = projectile.getMetadata("itemattributes.criticaldamage");
/*  979 */           for (MetadataValue mv : metadataValueList) {
/*  980 */             if (mv.getOwningPlugin().equals(getPlugin())) {
/*  981 */               damagerCriticalDamage += mv.asDouble();
/*  982 */               break;
/*      */             }
/*      */           }
/*      */         }
/*  986 */         if (projectile.hasMetadata("itemattributes.armorpenetration")) {
/*  987 */           List metadataValueList = projectile.getMetadata("itemattributes.armorpenetration");
/*  988 */           for (MetadataValue mv : metadataValueList) {
/*  989 */             if (mv.getOwningPlugin().equals(getPlugin())) {
/*  990 */               armorPenetration += mv.asDouble();
/*  991 */               break;
/*      */             }
/*      */           }
/*      */         }
/*  995 */         if (projectile.hasMetadata("itemattributes.stunrate")) {
/*  996 */           List metadataValueList = projectile.getMetadata("itemattributes.stunrate");
/*  997 */           for (MetadataValue mv : metadataValueList) {
/*  998 */             if (mv.getOwningPlugin().equals(getPlugin())) {
/*  999 */               stunRate += mv.asDouble();
/* 1000 */               break;
/*      */             }
/*      */           }
/*      */         }
/* 1004 */         if (projectile.hasMetadata("itemattributes.stunlength")) {
/* 1005 */           List metadataValueList = projectile.getMetadata("itemattributes.stunlength");
/* 1006 */           for (MetadataValue mv : metadataValueList)
/* 1007 */             if (mv.getOwningPlugin().equals(getPlugin())) {
/* 1008 */               stunLength += mv.asInt();
/* 1009 */               break;
/*      */             }
/*      */         }
/*      */       }
/*      */     }
/* 1014 */     else if ((event.getDamager() instanceof LivingEntity)) {
/* 1015 */       LivingEntity damager = (LivingEntity)event.getDamager();
/* 1016 */       damagerEquipmentDamage += getPlugin().getAttributeHandler().getAttributeValueFromEntity(damager, meleeDamageAttribute);
/*      */ 
/* 1018 */       damagerEquipmentDamage += getPlugin().getAttributeHandler().getAttributeValueFromEntity(damager, damageAttribute);
/*      */ 
/* 1020 */       damagerCriticalChance += getPlugin().getAttributeHandler().getAttributeValueFromEntity(damager, criticalRateAttribute);
/*      */ 
/* 1022 */       damagerCriticalDamage += getPlugin().getAttributeHandler().getAttributeValueFromEntity(damager, criticalDamageAttribute);
/*      */ 
/* 1024 */       stunRate += getPlugin().getAttributeHandler().getAttributeValueFromEntity(damager, stunRateAttribute);
/* 1025 */       stunLength = (int)(stunLength + getPlugin().getAttributeHandler().getAttributeValueFromEntity(damager, stunLengthAttribute));
/* 1026 */       armorPenetration += getPlugin().getAttributeHandler().getAttributeValueFromEntity(damager, armorPenetrationAttribute);
/*      */     }
/*      */ 
/* 1030 */     if ((event.getEntity() instanceof LivingEntity)) {
/* 1031 */       LivingEntity entity = (LivingEntity)event.getEntity();
/* 1032 */       dodgeRate += getPlugin().getAttributeHandler().getAttributeValueFromEntity(entity, dodgeRateAttribute);
/*      */     }
/*      */ 
/* 1035 */     double damagedEquipmentReduction = 0.0D;
/* 1036 */     if ((event.getEntity() instanceof LivingEntity)) {
/* 1037 */       LivingEntity entity = (LivingEntity)event.getEntity();
/* 1038 */       damagedEquipmentReduction += getPlugin().getAttributeHandler().getAttributeValueFromEntity(entity, armorAttribute);
/*      */     }
/*      */ 
/* 1042 */     boolean dodged = RandomUtils.nextDouble() < dodgeRate;
/*      */ 
/* 1044 */     if (dodged) {
/* 1045 */       if ((event.getEntity() instanceof Player)) {
/* 1046 */         getPlugin().getLanguageManager().sendMessage((Player)event.getEntity(), "events.dodge");
/*      */       }
/* 1048 */       event.setDamage(0.0D);
/* 1049 */       event.setCancelled(true);
/* 1050 */       playAttributeSounds(event.getEntity().getLocation().add(0.0D, 1.0D, 0.0D), new Attribute[] { dodgeRateAttribute });
/* 1051 */       return;
/*      */     }
/*      */ 
/* 1054 */     double equipmentDamage = damagerEquipmentDamage - (damagedEquipmentReduction - armorPenetration);
/* 1055 */     double damage = originalDamage + equipmentDamage;
/*      */ 
/* 1057 */     if (damagedEquipmentReduction != 0.0D) {
/* 1058 */       playAttributeSounds(event.getEntity().getLocation().add(0.0D, 1.0D, 0.0D), new Attribute[] { armorAttribute });
/*      */     }
/* 1060 */     if (armorPenetration != 0.0D) {
/* 1061 */       playAttributeSounds(event.getEntity().getLocation().add(0.0D, 1.0D, 0.0D), new Attribute[] { armorPenetrationAttribute });
/*      */     }
/*      */ 
/* 1064 */     if (RandomUtils.nextDouble() < damagerCriticalChance)
/*      */     {
/* 1066 */       ItemAttributesCriticalStrikeEvent criticalStrikeEvent = null;
/*      */ 
/* 1068 */       if (((event.getDamager() instanceof LivingEntity)) && ((event.getEntity() instanceof LivingEntity))) {
/* 1069 */         criticalStrikeEvent = new ItemAttributesCriticalStrikeEvent((LivingEntity)event.getDamager(), (LivingEntity)event.getEntity(), damagerCriticalChance, damagerCriticalDamage);
/*      */ 
/* 1071 */         Bukkit.getPluginManager().callEvent(criticalStrikeEvent);
/*      */       }
/*      */ 
/* 1074 */       if (criticalStrikeEvent == null) {
/* 1075 */         double critPercentage = 1.0D + damagerCriticalDamage;
/* 1076 */         damage *= critPercentage;
/* 1077 */         if ((event.getDamager() instanceof Player)) {
/* 1078 */           getPlugin().getLanguageManager().sendMessage((Player)event.getDamager(), "events.critical-hit", new String[][] { { "%percentage%", this.decimalFormat.format(critPercentage * 100.0D) } });
/*      */         }
/* 1080 */         else if (((event.getDamager() instanceof Projectile)) && ((((Projectile)event.getDamager()).getShooter() instanceof Player)))
/*      */         {
/* 1082 */           getPlugin().getLanguageManager().sendMessage((Player)((Projectile)event.getDamager()).getShooter(), "events.critical-hit", new String[][] { { "%percentage%", this.decimalFormat.format(critPercentage * 100.0D) } });
/*      */         }
/*      */ 
/* 1086 */         playAttributeSounds(event.getDamager().getLocation().add(0.0D, 1.0D, 0.0D), new Attribute[] { criticalRateAttribute, criticalDamageAttribute });
/* 1087 */       } else if ((criticalStrikeEvent != null) && (!criticalStrikeEvent.isCancelled())) {
/* 1088 */         double critPercentage = 1.0D + criticalStrikeEvent.getCriticalDamage();
/* 1089 */         damage *= critPercentage;
/* 1090 */         if ((event.getDamager() instanceof Player)) {
/* 1091 */           getPlugin().getLanguageManager().sendMessage((Player)event.getDamager(), "events.critical-hit", new String[][] { { "%percentage%", this.decimalFormat.format(critPercentage * 100.0D) } });
/*      */         }
/* 1093 */         else if (((event.getDamager() instanceof Projectile)) && ((((Projectile)event.getDamager()).getShooter() instanceof Player)))
/*      */         {
/* 1095 */           getPlugin().getLanguageManager().sendMessage((Player)((Projectile)event.getDamager()).getShooter(), "events.critical-hit", new String[][] { { "%percentage%", this.decimalFormat.format(critPercentage * 100.0D) } });
/*      */         }
/*      */ 
/* 1099 */         playAttributeSounds(event.getDamager().getLocation().add(0.0D, 1.0D, 0.0D), new Attribute[] { criticalRateAttribute, criticalDamageAttribute });
/*      */       }
/*      */     }
/*      */ 
/* 1103 */     if (RandomUtils.nextDouble() < stunRate) {
/* 1104 */       if ((event.getEntity() instanceof LivingEntity)) {
/* 1105 */         LivingEntity defender = (LivingEntity)event.getEntity();
/* 1106 */         LivingEntity attacker = null;
/* 1107 */         if ((event.getDamager() instanceof Player)) {
/* 1108 */           getPlugin().getLanguageManager().sendMessage((Player)event.getDamager(), "events.stun");
/* 1109 */           attacker = (LivingEntity)event.getDamager();
/* 1110 */         } else if (((event.getDamager() instanceof Projectile)) && ((((Projectile)event.getDamager()).getShooter() instanceof Player)))
/*      */         {
/* 1112 */           getPlugin().getLanguageManager().sendMessage((Player)((Projectile)event.getDamager()).getShooter(), "events.stun");
/*      */ 
/* 1114 */           attacker = ((Projectile)event.getDamager()).getShooter();
/*      */         }
/* 1116 */         if (attacker == null) {
/* 1117 */           defender.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, stunLength * 20, 7));
/*      */ 
/* 1119 */           defender.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, stunLength * 20, 7));
/*      */         }
/*      */         else {
/* 1122 */           ItemAttributesStunStrikeEvent stunStrikeEvent = new ItemAttributesStunStrikeEvent(attacker, defender, stunRate, stunLength);
/*      */ 
/* 1124 */           Bukkit.getPluginManager().callEvent(stunStrikeEvent);
/*      */ 
/* 1126 */           if (!stunStrikeEvent.isCancelled()) {
/* 1127 */             defender.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, stunLength * 20, 7));
/*      */ 
/* 1129 */             defender.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, stunLength * 20, 7));
/*      */           }
/*      */         }
/*      */       }
/*      */ 
/* 1134 */       playAttributeSounds(event.getEntity().getLocation().add(0.0D, 1.0D, 0.0D), new Attribute[] { stunLengthAttribute, stunRateAttribute });
/*      */     }
/*      */ 
/* 1138 */     event.setDamage(damage);
/*      */   }
/*      */ 
/*      */   @EventHandler(priority=EventPriority.LOWEST)
/*      */   public void onEntityDamageEventLowest(EntityDamageEvent event) {
/* 1143 */     if ((event.isCancelled()) || ((event instanceof EntityDamageByEntityEvent)) || (!(event.getEntity() instanceof Player)))
/*      */     {
/* 1145 */       return;
/*      */     }
/* 1147 */     Player player = (Player)event.getEntity();
/* 1148 */     handleLevelRequirementCheck(player);
/* 1149 */     handlePermissionCheck(player);
/*      */   }
/*      */ 
/*      */   @EventHandler(priority=EventPriority.LOW)
/*      */   public void onEntityDamageEventLow(EntityDamageEvent event) {
/* 1154 */     if ((event.isCancelled()) || ((event instanceof EntityDamageByEntityEvent)) || (!(event.getEntity() instanceof LivingEntity)))
/*      */     {
/* 1156 */       return;
/*      */     }
/* 1158 */     EntityDamageEvent.DamageCause damageCause = event.getCause();
/* 1159 */     if ((damageCause == EntityDamageEvent.DamageCause.DROWNING) || (damageCause == EntityDamageEvent.DamageCause.STARVATION) || (damageCause == EntityDamageEvent.DamageCause.SUFFOCATION) || (damageCause == EntityDamageEvent.DamageCause.SUICIDE) || (damageCause == EntityDamageEvent.DamageCause.THORNS))
/*      */     {
/* 1162 */       return;
/*      */     }
/*      */ 
/* 1165 */     if ((damageCause == EntityDamageEvent.DamageCause.FIRE) || (damageCause == EntityDamageEvent.DamageCause.FIRE_TICK))
/*      */     {
/* 1167 */       boolean b = false;
/* 1168 */       for (ItemStack is : ((LivingEntity)event.getEntity()).getEquipment().getArmorContents()) {
/* 1169 */         if (!b) {
/* 1170 */           b = ItemAttributesParseUtil.hasFormatInCollection(getItemStackLore(is), getPlugin().getSettingsManager().getAttribute("FIRE IMMUNITY").getFormat());
/*      */         }
/*      */       }
/*      */ 
/* 1174 */       if (!b) {
/* 1175 */         b = ItemAttributesParseUtil.hasFormatInCollection(getItemStackLore(((LivingEntity)event.getEntity()).getEquipment().getItemInHand()), getPlugin().getSettingsManager().getAttribute("FIRE IMMUNITY").getFormat());
/*      */       }
/*      */ 
/* 1178 */       if (b) {
/* 1179 */         event.setDamage(0.0D);
/* 1180 */         event.setCancelled(true);
/*      */       }
/* 1182 */     } else if (damageCause == EntityDamageEvent.DamageCause.POISON) {
/* 1183 */       boolean b = false;
/* 1184 */       for (ItemStack is : ((LivingEntity)event.getEntity()).getEquipment().getArmorContents()) {
/* 1185 */         if (!b) {
/* 1186 */           b = ItemAttributesParseUtil.hasFormatInCollection(getItemStackLore(is), getPlugin().getSettingsManager().getAttribute("POISON IMMUNITY").getFormat());
/*      */         }
/*      */       }
/*      */ 
/* 1190 */       if (!b) {
/* 1191 */         b = ItemAttributesParseUtil.hasFormatInCollection(getItemStackLore(((LivingEntity)event.getEntity()).getEquipment().getItemInHand()), getPlugin().getSettingsManager().getAttribute("POISON IMMUNITY").getFormat());
/*      */       }
/*      */ 
/* 1195 */       if (b) {
/* 1196 */         event.setDamage(0.0D);
/* 1197 */         event.setCancelled(true);
/*      */       }
/* 1199 */     } else if (damageCause == EntityDamageEvent.DamageCause.WITHER) {
/* 1200 */       boolean b = false;
/* 1201 */       for (ItemStack is : ((LivingEntity)event.getEntity()).getEquipment().getArmorContents()) {
/* 1202 */         if (!b) {
/* 1203 */           b = ItemAttributesParseUtil.hasFormatInCollection(getItemStackLore(is), getPlugin().getSettingsManager().getAttribute("WITHER IMMUNITY").getFormat());
/*      */         }
/*      */       }
/*      */ 
/* 1207 */       if (!b) {
/* 1208 */         b = ItemAttributesParseUtil.hasFormatInCollection(getItemStackLore(((LivingEntity)event.getEntity()).getEquipment().getItemInHand()), getPlugin().getSettingsManager().getAttribute("WITHER IMMUNITY").getFormat());
/*      */       }
/*      */ 
/* 1211 */       if (b) {
/* 1212 */         event.setDamage(0.0D);
/* 1213 */         event.setCancelled(true);
/*      */       }
/*      */     }
/*      */ 
/* 1217 */     if (event.isCancelled())
/* 1218 */       playAttributeSounds(((LivingEntity)event.getEntity()).getEyeLocation(), new Attribute[] { getPlugin().getSettingsManager().getAttribute("FIRE IMMUNITY"), getPlugin().getSettingsManager().getAttribute("POISON IMMUNITY"), getPlugin().getSettingsManager().getAttribute("WITHER IMMUNITY") });
/*      */   }
/*      */ }

/* Location:           D:\Github\Mechanics\ItemAttributes.jar
 * Qualified Name:     net.nunnerycode.bukkit.itemattributes.listeners.ItemAttributesCoreListener
 * JD-Core Version:    0.6.2
 */