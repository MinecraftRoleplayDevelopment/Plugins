/*     */ package net.nunnerycode.bukkit.itemattributes.tasks;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import net.nunnerycode.bukkit.itemattributes.ItemAttributesPlugin;
/*     */ import net.nunnerycode.bukkit.itemattributes.api.ItemAttributes;
/*     */ import net.nunnerycode.bukkit.itemattributes.api.attributes.Attribute;
/*     */ import net.nunnerycode.bukkit.itemattributes.api.managers.SettingsManager;
/*     */ import net.nunnerycode.bukkit.itemattributes.api.tasks.HealthUpdateTask;
/*     */ import net.nunnerycode.bukkit.itemattributes.events.ItemAttributesHealthUpdateEvent;
/*     */ import net.nunnerycode.bukkit.itemattributes.utils.ItemAttributesParseUtil;
/*     */ import org.bukkit.Bukkit;
/*     */ import org.bukkit.Location;
/*     */ import org.bukkit.World;
/*     */ import org.bukkit.entity.Entity;
/*     */ import org.bukkit.entity.LivingEntity;
/*     */ import org.bukkit.entity.Player;
/*     */ import org.bukkit.inventory.EntityEquipment;
/*     */ import org.bukkit.inventory.ItemStack;
/*     */ import org.bukkit.inventory.meta.ItemMeta;
/*     */ import org.bukkit.metadata.MetadataValue;
/*     */ import org.bukkit.plugin.PluginManager;
/*     */ import org.bukkit.scheduler.BukkitRunnable;
/*     */ 
/*     */ public final class ItemAttributesHealthUpdateTask extends BukkitRunnable
/*     */   implements HealthUpdateTask
/*     */ {
/*     */   private final ItemAttributesPlugin plugin;
/*     */ 
/*     */   public ItemAttributesHealthUpdateTask(ItemAttributesPlugin plugin)
/*     */   {
/*  26 */     this.plugin = plugin;
/*  27 */     runTaskTimer(plugin, 20L * getPlugin().getSettingsManager().getSecondsBetweenHealthUpdates(), 20L * getPlugin().getSettingsManager().getSecondsBetweenHealthUpdates());
/*     */   }
/*     */ 
/*     */   public void run()
/*     */   {
/*  33 */     Attribute healthAttribute = getPlugin().getSettingsManager().getAttribute("HEALTH");
/*  34 */     for (World w : Bukkit.getWorlds())
/*  35 */       for (Entity e : w.getEntities())
/*  36 */         if ((e instanceof Player)) {
/*  37 */           Player player = (Player)e;
/*  38 */           ItemStack[] armorContents = player.getEquipment().getArmorContents();
/*  39 */           double d = 0.0D;
/*  40 */           for (ItemStack is : armorContents) {
/*  41 */             d += ItemAttributesParseUtil.getValue(getItemStackLore(is), healthAttribute);
/*     */           }
/*  43 */           d += ItemAttributesParseUtil.getValue(getItemStackLore(player.getItemInHand()), healthAttribute);
/*  44 */           double currentHealth = player.getHealth();
/*  45 */           double baseMaxHealth = getPlugin().getSettingsManager().getBasePlayerHealth();
/*     */ 
/*  47 */           ItemAttributesHealthUpdateEvent healthUpdateEvent = new ItemAttributesHealthUpdateEvent(player, player.getMaxHealth(), baseMaxHealth, d);
/*     */ 
/*  49 */           Bukkit.getPluginManager().callEvent(healthUpdateEvent);
/*     */ 
/*  51 */           if (healthUpdateEvent.isCancelled()) {
/*  52 */             return;
/*     */           }
/*     */ 
/*  55 */           player.setHealth(Math.min(Math.max((healthUpdateEvent.getBaseHealth() + healthUpdateEvent.getChangeInHealth()) / 2.0D, 1.0D), player.getMaxHealth()));
/*     */ 
/*  57 */           player.setMaxHealth(Math.max(healthUpdateEvent.getBaseHealth() + healthUpdateEvent.getChangeInHealth(), 1.0D));
/*     */ 
/*  59 */           player.setHealth(Math.min(Math.max(currentHealth, 0.0D), player.getMaxHealth()));
/*  60 */           player.setHealthScale(player.getMaxHealth());
/*  61 */           playAttributeSounds(player.getEyeLocation(), new Attribute[] { healthAttribute });
/*  62 */         } else if ((e instanceof LivingEntity)) {
/*  63 */           LivingEntity entity = (LivingEntity)e;
/*  64 */           ItemStack[] armorContents = entity.getEquipment().getArmorContents();
/*  65 */           double d = 0.0D;
/*  66 */           for (ItemStack is : armorContents) {
/*  67 */             d += ItemAttributesParseUtil.getValue(getItemStackLore(is), healthAttribute);
/*     */           }
/*  69 */           d += ItemAttributesParseUtil.getValue(getItemStackLore(entity.getEquipment().getItemInHand()), healthAttribute);
/*     */ 
/*  71 */           double currentHealth = entity.getHealth();
/*  72 */           entity.resetMaxHealth();
/*  73 */           double baseMaxHealth = entity.getMaxHealth();
/*  74 */           if (entity.hasMetadata("itemattributes.basehealth")) {
/*  75 */             List metadataValueList = entity.getMetadata("itemattributes.basehealth");
/*  76 */             for (MetadataValue mv : metadataValueList) {
/*  77 */               if (mv.getOwningPlugin().equals(getPlugin())) {
/*  78 */                 baseMaxHealth = mv.asDouble();
/*  79 */                 break;
/*     */               }
/*     */             }
/*     */           }
/*     */ 
/*  84 */           ItemAttributesHealthUpdateEvent healthUpdateEvent = new ItemAttributesHealthUpdateEvent(entity, entity.getMaxHealth(), baseMaxHealth, d);
/*     */ 
/*  86 */           Bukkit.getPluginManager().callEvent(healthUpdateEvent);
/*     */ 
/*  88 */           if (healthUpdateEvent.isCancelled()) {
/*  89 */             return;
/*     */           }
/*     */ 
/*  92 */           entity.setHealth(Math.min(Math.max((healthUpdateEvent.getBaseHealth() + healthUpdateEvent.getChangeInHealth()) / 2.0D, 1.0D), entity.getMaxHealth()));
/*     */ 
/*  94 */           entity.setMaxHealth(Math.max(healthUpdateEvent.getBaseHealth() + healthUpdateEvent.getChangeInHealth(), 1.0D));
/*     */ 
/*  96 */           entity.setHealth(Math.min(Math.max(currentHealth, 0.0D), entity.getMaxHealth()));
/*  97 */           playAttributeSounds(entity.getEyeLocation(), new Attribute[] { healthAttribute });
/*     */         }
/*     */   }
/*     */ 
/*     */   public ItemAttributes getPlugin()
/*     */   {
/* 105 */     return this.plugin;
/*     */   }
/*     */ 
/*     */   public List<String> getItemStackLore(ItemStack itemStack) {
/* 109 */     List lore = new ArrayList();
/* 110 */     if ((itemStack != null) && (itemStack.hasItemMeta()) && (itemStack.getItemMeta().hasLore())) {
/* 111 */       lore.addAll(itemStack.getItemMeta().getLore());
/*     */     }
/* 113 */     return lore;
/*     */   }
/*     */ 
/*     */   private void playAttributeSounds(Location location, Attribute[] attributes) {
/* 117 */     for (Attribute attribute : attributes)
/* 118 */       location.getWorld().playSound(location, attribute.getSound(), 1.0F, 1.0F);
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\ItemAttributes.jar
 * Qualified Name:     net.nunnerycode.bukkit.itemattributes.tasks.ItemAttributesHealthUpdateTask
 * JD-Core Version:    0.6.2
 */