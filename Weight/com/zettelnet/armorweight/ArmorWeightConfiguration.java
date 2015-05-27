/*     */ package com.zettelnet.armorweight;
/*     */ 
/*     */ import com.google.common.base.CaseFormat;
/*     */ import com.zettelnet.armorweight.zet.configuration.PluginConfigurationFile;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.logging.Logger;
/*     */ import org.bukkit.Material;
/*     */ import org.bukkit.configuration.ConfigurationSection;
/*     */ import org.bukkit.configuration.file.FileConfiguration;
/*     */ import org.bukkit.enchantments.Enchantment;
/*     */ import org.bukkit.plugin.Plugin;
/*     */ 
/*     */ public class ArmorWeightConfiguration extends PluginConfigurationFile
/*     */ {
/*     */   private final Plugin plugin;
/*     */   private final WeightManager manager;
/*     */   private boolean metricsEnabled;
/*     */   private String chatLanguage;
/*     */   private boolean weightWarningEnabled;
/*     */   private long weightWarningCooldown;
/*     */   private final Map<String, Enchantment> enchantmentMappings;
/*     */ 
/*     */   public ArmorWeightConfiguration(Plugin plugin, String file, String resource, WeightManager manager)
/*     */   {
/*  32 */     super(plugin, file, resource);
/*  33 */     this.plugin = plugin;
/*  34 */     this.manager = manager;
/*  35 */     this.enchantmentMappings = new HashMap();
/*     */     try
/*     */     {
/*  41 */       this.enchantmentMappings.put("protection", Enchantment.PROTECTION_ENVIRONMENTAL);
/*  42 */       this.enchantmentMappings.put("fireProtection", Enchantment.PROTECTION_FIRE);
/*  43 */       this.enchantmentMappings.put("featherFalling", Enchantment.PROTECTION_FALL);
/*  44 */       this.enchantmentMappings.put("blastProtection", Enchantment.PROTECTION_EXPLOSIONS);
/*  45 */       this.enchantmentMappings.put("projectileProtection", Enchantment.PROTECTION_PROJECTILE);
/*  46 */       this.enchantmentMappings.put("respiration", Enchantment.OXYGEN);
/*  47 */       this.enchantmentMappings.put("aquaAffinity", Enchantment.WATER_WORKER);
/*  48 */       this.enchantmentMappings.put("sharpness", Enchantment.DAMAGE_ALL);
/*  49 */       this.enchantmentMappings.put("smite", Enchantment.DAMAGE_UNDEAD);
/*  50 */       this.enchantmentMappings.put("baneOfArthropods", Enchantment.DAMAGE_ARTHROPODS);
/*  51 */       this.enchantmentMappings.put("knockback", Enchantment.KNOCKBACK);
/*  52 */       this.enchantmentMappings.put("fireAspect", Enchantment.FIRE_ASPECT);
/*  53 */       this.enchantmentMappings.put("looting", Enchantment.LOOT_BONUS_MOBS);
/*  54 */       this.enchantmentMappings.put("efficiency", Enchantment.DIG_SPEED);
/*  55 */       this.enchantmentMappings.put("silkTouch", Enchantment.SILK_TOUCH);
/*  56 */       this.enchantmentMappings.put("unbreaking", Enchantment.DURABILITY);
/*  57 */       this.enchantmentMappings.put("fortune", Enchantment.LOOT_BONUS_BLOCKS);
/*  58 */       this.enchantmentMappings.put("power", Enchantment.ARROW_DAMAGE);
/*  59 */       this.enchantmentMappings.put("punch", Enchantment.ARROW_KNOCKBACK);
/*  60 */       this.enchantmentMappings.put("flame", Enchantment.ARROW_FIRE);
/*  61 */       this.enchantmentMappings.put("infinity", Enchantment.ARROW_INFINITE);
/*  62 */       this.enchantmentMappings.put("thorns", Enchantment.THORNS);
/*  63 */       this.enchantmentMappings.put("luckOfTheSea", Enchantment.LUCK);
/*  64 */       this.enchantmentMappings.put("lure", Enchantment.LURE);
/*  65 */       this.enchantmentMappings.put("depthStrider", Enchantment.DEPTH_STRIDER);
/*     */     }
/*     */     catch (NoSuchFieldError e) {
/*     */     }
/*     */   }
/*     */ 
/*     */   protected synchronized void loadValues(FileConfiguration config) {
/*  72 */     if (config.getBoolean("config.autoUpdate", true)) {
/*  73 */       update(config);
/*     */     }
/*     */ 
/*  76 */     this.metricsEnabled = config.getBoolean("metricsEnabled", false);
/*     */ 
/*  78 */     WeightManager.PLAYER_WEIGHT = config.getDouble("playerWeight", 90.0D) / 100.0D;
/*  79 */     WeightManager.HORSE_WEIGHT = config.getDouble("horseWeight", 400.0D) / 100.0D;
/*     */ 
/*  81 */     this.manager.setPlayerArmorWeightEnabled(config.getBoolean("weightEnabled.armor.player", true));
/*  82 */     this.manager.setHorseArmorWeightEnabled(config.getBoolean("weightEnabled.armor.horse", true));
/*  83 */     this.manager.setHorsePassengerWeightEnabled(config.getBoolean("weightEnabled.horseRider", true));
/*  84 */     this.manager.setEnchantmentWeightEnabled(config.getBoolean("weightEnabled.enchantment", false));
/*     */ 
/*  86 */     this.manager.setPlayerSpeedEffectEnabled(config.getBoolean("effectEnabled.speed.player", true));
/*  87 */     this.manager.setPlayerCreativeSpeedEffectEnabled(config.getBoolean("effectEnabled.speed.playerCreative", false));
/*     */ 
/*  90 */     this.manager.setHorseSpeedEffectEnabled(config.getBoolean("effectEnabled.speed.horse", false));
/*     */ 
/*  92 */     this.manager.setEnabledWorlds(new HashSet());
/*  93 */     this.manager.setAllWorldsEnabled(false);
/*     */     Iterator i$;
/*  94 */     if (config.isList("enabledWorlds")) {
/*  95 */       for (i$ = config.getList("enabledWorlds", new ArrayList()).iterator(); i$.hasNext(); ) { Object obj = i$.next();
/*  96 */         if ((obj instanceof String)) {
/*  97 */           String worldName = (String)obj;
/*  98 */           if (worldName.equals("*"))
/*  99 */             this.manager.setAllWorldsEnabled(true);
/*     */           else
/* 101 */             this.manager.enableWorld((String)obj);
/*     */         }
/*     */         else {
/* 104 */           this.plugin.getLogger().warning("Could not enable world " + obj + "; not a String as world name");
/*     */         } }
/*     */     }
/*     */     else {
/* 108 */       this.plugin.getLogger().warning("Invalid configuration for \"enabledWorlds\" (should be a list); ENABLING ALL WORLDS");
/* 109 */       this.manager.setAllWorldsEnabled(true);
/*     */     }
/*     */ 
/* 112 */     ArmorType.reset();
/* 113 */     if (config.isConfigurationSection("armor.weight")) {
/* 114 */       for (Map.Entry entry : config.getConfigurationSection("armor.weight").getValues(false).entrySet())
/* 115 */         loadArmorTypeWeight((String)entry.getKey(), entry.getValue());
/*     */     }
/*     */     else {
/* 118 */       this.plugin.getLogger().warning("Invalid configuration for \"armor.weight\" (should be a section); not loading armor weights");
/*     */     }
/*     */ 
/* 121 */     double helmetShare = config.getDouble("armor.share.helmet", 17.0D);
/* 122 */     double chestplateShare = config.getDouble("armor.share.chestplate", 45.0D);
/* 123 */     double leggingsShare = config.getDouble("armor.share.leggings", 25.0D);
/* 124 */     double bootsShare = config.getDouble("armor.share.boots", 13.0D);
/* 125 */     double shareSum = helmetShare + chestplateShare + leggingsShare + bootsShare;
/* 126 */     ArmorPart.CHESTPLATE.setWeightShare(chestplateShare / shareSum);
/* 127 */     ArmorPart.LEGGINGS.setWeightShare(leggingsShare / shareSum);
/* 128 */     ArmorPart.HELMET.setWeightShare(helmetShare / shareSum);
/* 129 */     ArmorPart.BOOTS.setWeightShare(bootsShare / shareSum);
/*     */ 
/* 131 */     this.chatLanguage = config.getString("chat.language", "enUS");
/*     */ 
/* 133 */     WeightManager.DEFAULT_ENCHANTMENT_WEIGHT = 0.0D;
/* 134 */     WeightManager.ENCHANTMENT_WEIGHTS.clear();
/* 135 */     if (this.manager.isEnchantmentWeightEnabled()) {
/* 136 */       if (config.isConfigurationSection("enchantment.weight")) {
/* 137 */         for (Map.Entry entry : config.getConfigurationSection("enchantment.weight").getValues(false).entrySet())
/* 138 */           loadEnchantmentWeight((String)entry.getKey(), entry.getValue());
/*     */       }
/*     */       else {
/* 141 */         this.manager.setEnchantmentWeightEnabled(false);
/* 142 */         this.plugin.getLogger().warning("Invalid configuration for \"enchantment.weight\" (should be a section); not using enchantments weights");
/*     */       }
/*     */     }
/*     */ 
/* 146 */     this.weightWarningEnabled = config.getBoolean("weightWarning.enabled", true);
/* 147 */     this.weightWarningCooldown = (()(config.getDouble("weightWarning.cooldown", 10.0D) * 1000.0D));
/*     */   }
/*     */ 
/*     */   private boolean loadArmorTypeWeight(String name, Object value) {
/* 151 */     if (ArmorType.contains(name)) {
/*     */       try {
/* 153 */         ArmorType.valueOf(name).setWeight(Double.valueOf(value.toString()).doubleValue() / 100.0D);
/* 154 */         return true;
/*     */       } catch (NumberFormatException e) {
/* 156 */         this.plugin.getLogger().warning("Armor weight value for " + name + " is an invalid number; ignoring it");
/* 157 */         return false;
/*     */       }
/*     */     }
/* 160 */     Material material = Material.matchMaterial(name);
/* 161 */     if (material == null) {
/* 162 */       this.plugin.getLogger().warning("Armor weight key for " + name + " is not a material or armor type; ignoring it");
/* 163 */       return false;
/*     */     }
/*     */     try {
/* 166 */       ArmorType type = new ArmorType(material);
/* 167 */       type.setWeight(Double.valueOf(value.toString()).doubleValue() / 100.0D);
/* 168 */       ArmorType.register(type);
/* 169 */       return true;
/*     */     } catch (NumberFormatException e) {
/* 171 */       this.plugin.getLogger().warning("Armor weight value for " + name + " is an invalid number; ignoring it");
/* 172 */     }return false;
/*     */   }
/*     */ 
/*     */   private boolean loadEnchantmentWeight(String name, Object value)
/*     */   {
/* 177 */     if (name.equalsIgnoreCase("general")) {
/*     */       try {
/* 179 */         WeightManager.DEFAULT_ENCHANTMENT_WEIGHT = Double.valueOf(value.toString()).doubleValue() / 100.0D;
/* 180 */         return true;
/*     */       } catch (NumberFormatException e) {
/* 182 */         this.plugin.getLogger().warning("Enchantment weight value for " + name + " is an invalid number; ignoring it");
/* 183 */         return false;
/*     */       }
/*     */     }
/*     */ 
/* 187 */     Enchantment ench = Enchantment.getByName(CaseFormat.UPPER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE, name));
/* 188 */     if (ench == null) {
/* 189 */       ench = (Enchantment)this.enchantmentMappings.get(name);
/* 190 */       if (ench == null) {
/* 191 */         this.plugin.getLogger().warning("Unknown enchantment type \"" + name + "\"; ignoring it");
/* 192 */         return false;
/*     */       }
/*     */     }
/*     */     try
/*     */     {
/* 197 */       WeightManager.ENCHANTMENT_WEIGHTS.put(ench, Double.valueOf(Double.valueOf(value.toString()).doubleValue() / 100.0D));
/* 198 */       return true;
/*     */     } catch (NumberFormatException e) {
/* 200 */       this.plugin.getLogger().warning("Enchantment weight value for " + name + " is an invalid number; ignoring it");
/* 201 */     }return false;
/*     */   }
/*     */ 
/*     */   protected void update(FileConfiguration config)
/*     */   {
/* 207 */     switch (getVersion()) {
/*     */     default:
/* 209 */       this.plugin.getLogger().warning("Unknown version of configuration file \"config.yml\". Will not update file");
/* 210 */       break;
/*     */     case "unknown":
/*     */     case "0.1.0":
/* 213 */       setIfNotExists("horseWeight", Integer.valueOf(400));
/*     */     case "0.2.0":
/* 215 */       updateStart("0.2.0");
/* 216 */       setIfNotExists("enchantment.enabled", Boolean.valueOf(false));
/* 217 */       setIfNotExists("enchantment.weight.general", Integer.valueOf(1));
/* 218 */       setIfNotExists("enchantment.weight.protection", Integer.valueOf(2));
/* 219 */       setIfNotExists("enchantment.weight.unbreaking", Integer.valueOf(2));
/*     */     case "0.2.1":
/* 221 */       updateStart("0.2.1");
/* 222 */       setIfNotExists("weightEnabled.armor.player", Boolean.valueOf(true));
/* 223 */       setIfNotExists("weightEnabled.armor.horse", Boolean.valueOf(true));
/* 224 */       setIfNotExists("weightEnabled.horseRider", Boolean.valueOf(true));
/* 225 */       boolean enchantmentEnabled = config.getBoolean("enchantment.enabled", true);
/* 226 */       setIfNotExists("weightEnabled.enchantment", Boolean.valueOf(enchantmentEnabled));
/* 227 */       if (config.contains("enchantment.enabled")) {
/* 228 */         config.set("enchantment.enabled", null);
/*     */       }
/*     */ 
/* 231 */       setIfNotExists("effectEnabled.speed.player", Boolean.valueOf(true));
/* 232 */       setIfNotExists("effectEnabled.speed.horse", Boolean.valueOf(true));
/*     */ 
/* 234 */       setIfNotExists("enabledWorlds", Arrays.asList(new String[] { "world", "world_nether", "world_the_end", "*" }));
/*     */     case "0.3.0":
/*     */     case "0.3.1":
/* 237 */       updateStart("0.3.1");
/* 238 */       setIfNotExists("effectEnabled.speed.playerCreative", Boolean.valueOf(false));
/*     */     case "0.3.2":
/* 240 */       updateStart("0.3.2");
/* 241 */       setIfNotExists("weightWarning.enabled", Boolean.valueOf(true));
/* 242 */       setIfNotExists("weightWarning.cooldown", Integer.valueOf(10));
/*     */     case "0.3.3":
/*     */     case "0.3.4":
/*     */     case "0.3.5":
/* 246 */       updateDone("0.3.5");
/*     */     }
/*     */   }
/*     */ 
/*     */   public String chatLanguage() {
/* 251 */     return this.chatLanguage;
/*     */   }
/*     */ 
/*     */   public boolean metricsEnabled() {
/* 255 */     return this.metricsEnabled;
/*     */   }
/*     */ 
/*     */   public boolean weightWarningEnabled() {
/* 259 */     return this.weightWarningEnabled;
/*     */   }
/*     */ 
/*     */   public long weightWarningCooldown() {
/* 263 */     return this.weightWarningCooldown;
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\ArmorWeight.jar
 * Qualified Name:     com.zettelnet.armorweight.ArmorWeightConfiguration
 * JD-Core Version:    0.6.2
 */