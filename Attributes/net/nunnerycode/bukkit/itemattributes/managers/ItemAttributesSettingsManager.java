/*     */ package net.nunnerycode.bukkit.itemattributes.managers;
/*     */ 
/*     */ import com.conventnunnery.libraries.config.CommentedConventYamlConfiguration;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.Set;
/*     */ import net.nunnerycode.bukkit.itemattributes.ItemAttributesPlugin;
/*     */ import net.nunnerycode.bukkit.itemattributes.api.ItemAttributes;
/*     */ import net.nunnerycode.bukkit.itemattributes.api.attributes.Attribute;
/*     */ import net.nunnerycode.bukkit.itemattributes.api.managers.SettingsManager;
/*     */ import net.nunnerycode.bukkit.itemattributes.attributes.ItemAttribute;
/*     */ import org.bukkit.Sound;
/*     */ import org.bukkit.configuration.ConfigurationSection;
/*     */ 
/*     */ public final class ItemAttributesSettingsManager
/*     */   implements SettingsManager
/*     */ {
/*     */   private ItemAttributesPlugin plugin;
/*     */   private double basePlayerHealth;
/*     */   private double baseCriticalRate;
/*     */   private double baseCriticalDamage;
/*     */   private double baseStunRate;
/*     */   private double baseDodgeRate;
/*     */   private int baseStunLength;
/*     */   private int secondsBetweenHealthUpdates;
/*     */   private Map<String, Attribute> attributeMap;
/*     */   private boolean itemOnlyDamageSystemEnabled;
/*     */   private double itemOnlyDamageSystemBaseDamage;
/*     */   private boolean pluginCompatible;
/*     */ 
/*     */   public ItemAttributesSettingsManager(ItemAttributesPlugin plugin)
/*     */   {
/*  31 */     this.plugin = plugin;
/*  32 */     this.attributeMap = new HashMap();
/*     */   }
/*     */ 
/*     */   public void load() {
/*  36 */     getPlugin().getConfigYAML().load();
/*  37 */     this.basePlayerHealth = getPlugin().getConfigYAML().getDouble("options.base-player-health", 20.0D);
/*  38 */     this.baseCriticalRate = getPlugin().getConfigYAML().getDouble("options.base-critical-rate", 0.05D);
/*  39 */     this.baseCriticalDamage = getPlugin().getConfigYAML().getDouble("options.base-critical-damage", 0.2D);
/*  40 */     this.baseStunRate = getPlugin().getConfigYAML().getDouble("options.base-stun-rate", 0.05D);
/*  41 */     this.baseStunLength = getPlugin().getConfigYAML().getInt("options.base-stun-length", 1);
/*  42 */     this.baseDodgeRate = getPlugin().getConfigYAML().getDouble("options.base-dodge-rate", 0.0D);
/*  43 */     this.secondsBetweenHealthUpdates = getPlugin().getConfigYAML().getInt("options.seconds-between-health-updates", 10);
/*     */ 
/*  45 */     this.itemOnlyDamageSystemEnabled = getPlugin().getConfigYAML().getBoolean("options.item-only-damage-system.enabled", false);
/*     */ 
/*  47 */     this.itemOnlyDamageSystemBaseDamage = getPlugin().getConfigYAML().getDouble("options.item-only-damage-system.base-damage", 1.0D);
/*     */ 
/*  49 */     this.pluginCompatible = getPlugin().getConfigYAML().getBoolean("options.enable-plugin-compatibility", true);
/*  50 */     this.attributeMap.put("HEALTH", new ItemAttribute("Health", true, 100.0D, false, "%value% Health", null, 0.0D));
/*  51 */     this.attributeMap.put("ARMOR", new ItemAttribute("Armor", true, 100.0D, false, "%value% Armor", null, 0.0D));
/*  52 */     this.attributeMap.put("DAMAGE", new ItemAttribute("Damage", true, 100.0D, false, "%value% Damage", null, 1.0D));
/*  53 */     this.attributeMap.put("MELEE DAMAGE", new ItemAttribute("Melee Damage", true, 100.0D, false, "%value% Melee Damage", null, 0.0D));
/*     */ 
/*  55 */     this.attributeMap.put("RANGED DAMAGE", new ItemAttribute("Ranged Damage", true, 100.0D, false, "%value% Ranged Damage", null, 0.0D));
/*     */ 
/*  57 */     this.attributeMap.put("REGENERATION", new ItemAttribute("Regeneration", true, 100.0D, false, "%value% Regeneration", null, 0.0D));
/*     */ 
/*  59 */     this.attributeMap.put("CRITICAL RATE", new ItemAttribute("Critical Rate", true, 100.0D, true, "%value% Critical Rate", null, 0.05D));
/*     */ 
/*  61 */     this.attributeMap.put("CRITICAL DAMAGE", new ItemAttribute("Critical Damage", true, 100.0D, true, "%value% Critical Damage", null, 0.2D));
/*     */ 
/*  63 */     this.attributeMap.put("LEVEL REQUIREMENT", new ItemAttribute("Level Requirement", true, 100.0D, false, "Level Requirement: %value%", null, 0.0D));
/*     */ 
/*  65 */     this.attributeMap.put("ARMOR PENETRATION", new ItemAttribute("Armor Penetration", true, 100.0D, false, "%value% Armor Penetration", null, 0.0D));
/*     */ 
/*  67 */     this.attributeMap.put("STUN RATE", new ItemAttribute("Stun Rate", true, 100.0D, true, "%value% Stun Rate", null, 0.0D));
/*  68 */     this.attributeMap.put("STUN LENGTH", new ItemAttribute("Stun Length", true, 100.0D, false, "%value% Stun Length", null, 1.0D));
/*     */ 
/*  70 */     this.attributeMap.put("DODGE RATE", new ItemAttribute("Dodge Rate", true, 100.0D, true, "%value% Dodge Rate", null, 0.0D));
/*     */ 
/*  72 */     this.attributeMap.put("FIRE IMMUNITY", new ItemAttribute("Fire Immunity", true, -1.0D, false, "Fire Immunity", null, -1.0D));
/*     */ 
/*  74 */     this.attributeMap.put("WITHER IMMUNITY", new ItemAttribute("Wither Immunity", true, -1.0D, false, "Wither Immunity", null, -1.0D));
/*     */ 
/*  76 */     this.attributeMap.put("POISON IMMUNITY", new ItemAttribute("Poison Immunity", true, -1.0D, false, "Poison Immunity", null, -1.0D));
/*     */ 
/*  78 */     this.attributeMap.put("PERMISSION REQUIREMENT", new ItemAttribute("Permission Requirement", true, -1.0D, false, "Permission Requirement: %value%", null, -1.0D));
/*     */     ConfigurationSection section;
/*  81 */     if (getPlugin().getConfigYAML().isConfigurationSection("core-stats")) {
/*  82 */       section = getPlugin().getConfigYAML().getConfigurationSection("core-stats");
/*  83 */       for (Map.Entry entry : this.attributeMap.entrySet()) {
/*  84 */         ((Attribute)entry.getValue()).setEnabled(section.getBoolean(((String)entry.getKey()).toLowerCase().replace(" ", "-") + ".enabled", ((Attribute)entry.getValue()).isEnabled()));
/*     */ 
/*  86 */         ((Attribute)entry.getValue()).setFormat(section.getString(((String)entry.getKey()).toLowerCase().replace(" ", "-") + ".format", ((Attribute)entry.getValue()).getFormat()));
/*     */ 
/*  88 */         ((Attribute)entry.getValue()).setMaxValue(section.getDouble(((String)entry.getKey()).toLowerCase().replace(" ", "-") + ".max-value", ((Attribute)entry.getValue()).getMaxValue()));
/*     */ 
/*  90 */         ((Attribute)entry.getValue()).setPercentage(section.getBoolean(((String)entry.getKey()).toLowerCase().replace(" ", "-") + ".percentage", ((Attribute)entry.getValue()).isPercentage()));
/*     */ 
/*  92 */         ((Attribute)entry.getValue()).setBaseValue(section.getDouble(((String)entry.getKey()).toLowerCase().replace(" ", "-") + ".base-value", ((Attribute)entry.getValue()).getBaseValue()));
/*     */         try
/*     */         {
/*  95 */           ((Attribute)entry.getValue()).setSound(Sound.valueOf(section.getString(((String)entry.getKey()).toLowerCase().replace(" ", "-") + ".sound", ((Attribute)entry.getValue()).getSound() != null ? ((Attribute)entry.getValue()).getSound().name() : "")));
/*     */         }
/*     */         catch (Exception e)
/*     */         {
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public ItemAttributes getPlugin()
/*     */   {
/* 107 */     return this.plugin;
/*     */   }
/*     */ 
/*     */   public double getBasePlayerHealth()
/*     */   {
/* 112 */     return this.basePlayerHealth;
/*     */   }
/*     */ 
/*     */   public int getSecondsBetweenHealthUpdates()
/*     */   {
/* 117 */     return this.secondsBetweenHealthUpdates;
/*     */   }
/*     */ 
/*     */   public double getBaseCriticalRate()
/*     */   {
/* 122 */     return this.baseCriticalRate;
/*     */   }
/*     */ 
/*     */   public double getBaseCriticalDamage()
/*     */   {
/* 127 */     return this.baseCriticalDamage;
/*     */   }
/*     */ 
/*     */   public double getBaseStunRate()
/*     */   {
/* 132 */     return this.baseStunRate;
/*     */   }
/*     */ 
/*     */   public int getBaseStunLength()
/*     */   {
/* 137 */     return this.baseStunLength;
/*     */   }
/*     */ 
/*     */   public double getBaseDodgeRate()
/*     */   {
/* 142 */     return this.baseDodgeRate;
/*     */   }
/*     */ 
/*     */   public Attribute getAttribute(String name) {
/* 146 */     if (this.attributeMap.containsKey(name.toUpperCase())) {
/* 147 */       return (Attribute)this.attributeMap.get(name.toUpperCase());
/*     */     }
/* 149 */     return null;
/*     */   }
/*     */ 
/*     */   public boolean isItemOnlyDamageSystemEnabled()
/*     */   {
/* 154 */     return this.itemOnlyDamageSystemEnabled;
/*     */   }
/*     */ 
/*     */   public void save() {
/* 158 */     getPlugin().getConfigYAML().load();
/* 159 */     if (!getPlugin().getConfigYAML().isSet("version")) {
/* 160 */       getPlugin().getConfigYAML().set("version", getPlugin().getConfigYAML().getVersion());
/* 161 */       getPlugin().getConfigYAML().set("options.base-player-health", Double.valueOf(this.basePlayerHealth));
/* 162 */       getPlugin().getConfigYAML().set("options.base-critical-rate", Double.valueOf(this.baseCriticalRate));
/* 163 */       getPlugin().getConfigYAML().set("options.base-critical-damage", Double.valueOf(this.baseCriticalDamage));
/* 164 */       getPlugin().getConfigYAML().set("options.base-stun-rate", Double.valueOf(this.baseStunRate));
/* 165 */       getPlugin().getConfigYAML().set("options.base-stun-length", Integer.valueOf(this.baseStunLength));
/* 166 */       getPlugin().getConfigYAML().set("options.seconds-between-health-updates", Integer.valueOf(this.secondsBetweenHealthUpdates));
/* 167 */       getPlugin().getConfigYAML().set("options.item-only-damage-system.enabled", Boolean.valueOf(this.itemOnlyDamageSystemEnabled));
/* 168 */       getPlugin().getConfigYAML().set("options.item-only-damage-system.base-damage", Double.valueOf(this.itemOnlyDamageSystemBaseDamage));
/* 169 */       for (Map.Entry entry : this.attributeMap.entrySet()) {
/* 170 */         getPlugin().getConfigYAML().set("core-stats." + ((String)entry.getKey()).toLowerCase().replace(" ", "-") + ".enabled", Boolean.valueOf(((Attribute)entry.getValue()).isEnabled()));
/*     */ 
/* 172 */         getPlugin().getConfigYAML().set("core-stats." + ((String)entry.getKey()).toLowerCase().replace(" ", "-") + ".format", ((Attribute)entry.getValue()).getFormat());
/*     */ 
/* 174 */         getPlugin().getConfigYAML().set("core-stats." + ((String)entry.getKey()).toLowerCase().replace(" ", "-") + ".percentage", Boolean.valueOf(((Attribute)entry.getValue()).isPercentage()));
/*     */ 
/* 176 */         getPlugin().getConfigYAML().set("core-stats." + ((String)entry.getKey()).toLowerCase().replace(" ", "-") + ".max-value", Double.valueOf(((Attribute)entry.getValue()).getMaxValue()));
/*     */ 
/* 178 */         getPlugin().getConfigYAML().set("core-stats." + ((String)entry.getKey()).toLowerCase().replace(" ", "-") + ".base-value", Double.valueOf(((Attribute)entry.getValue()).getBaseValue()));
/*     */         try
/*     */         {
/* 181 */           getPlugin().getConfigYAML().set("core-stats." + ((String)entry.getKey()).toLowerCase().replace(" ", "-") + ".sound", ((Attribute)entry.getValue()).getSound().name());
/*     */         }
/*     */         catch (Exception e)
/*     */         {
/*     */         }
/*     */       }
/*     */     }
/* 188 */     getPlugin().getConfigYAML().save();
/*     */   }
/*     */ 
/*     */   public double getItemOnlyDamageSystemBaseDamage()
/*     */   {
/* 193 */     return this.itemOnlyDamageSystemBaseDamage;
/*     */   }
/*     */ 
/*     */   public boolean addAttribute(String name, Attribute attribute)
/*     */   {
/* 198 */     boolean b = false;
/* 199 */     if (isPluginCompatible()) {
/* 200 */       this.attributeMap.put(name.toUpperCase(), attribute);
/* 201 */       b = this.attributeMap.containsKey(name.toUpperCase());
/*     */     }
/* 203 */     return b;
/*     */   }
/*     */ 
/*     */   public boolean removeAttribute(String name, Attribute attribute)
/*     */   {
/* 208 */     boolean b = false;
/* 209 */     if (isPluginCompatible()) {
/* 210 */       this.attributeMap.remove(name.toUpperCase());
/* 211 */       b = !this.attributeMap.containsKey(name.toUpperCase());
/*     */     }
/* 213 */     return b;
/*     */   }
/*     */ 
/*     */   public Set<Attribute> getLoadedAttributes()
/*     */   {
/* 218 */     return new HashSet(this.attributeMap.values());
/*     */   }
/*     */ 
/*     */   public boolean isPluginCompatible()
/*     */   {
/* 223 */     return this.pluginCompatible;
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\ItemAttributes.jar
 * Qualified Name:     net.nunnerycode.bukkit.itemattributes.managers.ItemAttributesSettingsManager
 * JD-Core Version:    0.6.2
 */