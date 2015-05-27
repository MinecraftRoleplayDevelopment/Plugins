/*     */ package com.zettelnet.armorweight;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import org.apache.commons.lang.Validate;
/*     */ import org.bukkit.Material;
/*     */ import org.bukkit.inventory.ItemStack;
/*     */ 
/*     */ public class ArmorType
/*     */ {
/*  17 */   private static final Map<String, ArmorType> registeredNames = new HashMap();
/*  18 */   private static final Map<Material, ArmorType> registeredMaterials = new HashMap();
/*     */ 
/* 175 */   public static final ArmorType AIR = new ArmorType("none", new Material[] { Material.AIR });
/*     */   public static final ArmorType LEATHER;
/*     */   public static final ArmorType GOLD;
/*     */   public static final ArmorType CHAINMAIL;
/*     */   public static final ArmorType IRON;
/*     */   public static final ArmorType DIAMOND;
/*     */   private final String name;
/*     */   private final Material[] materials;
/*     */   private double weight;
/*     */ 
/*     */   public static Collection<ArmorType> values()
/*     */   {
/*  21 */     return Collections.unmodifiableCollection(registeredNames.values());
/*     */   }
/*     */ 
/*     */   public static ArmorType valueOf(String name) {
/*  25 */     return (ArmorType)registeredNames.get(name.toLowerCase());
/*     */   }
/*     */ 
/*     */   public static boolean contains(String name) {
/*  29 */     return registeredNames.containsKey(name);
/*     */   }
/*     */ 
/*     */   public static ArmorType valueOf(Material material) {
/*  33 */     ArmorType type = (ArmorType)registeredMaterials.get(material);
/*  34 */     return type == null ? AIR : type;
/*     */   }
/*     */ 
/*     */   public static boolean contains(Material material) {
/*  38 */     return registeredMaterials.containsKey(material);
/*     */   }
/*     */ 
/*     */   public static void register(ArmorType type) {
/*  42 */     Validate.notNull(type, "Type cannot be null");
/*  43 */     Validate.isTrue(!registeredMaterials.containsKey(type.getName().toLowerCase()), "Type name already registered");
/*  44 */     registeredNames.put(type.getName().toLowerCase(), type);
/*  45 */     for (Material mat : type.getMaterial()) {
/*  46 */       if (registeredMaterials.containsKey(mat));
/*  49 */       registeredMaterials.put(mat, type);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static boolean registerIfAvailable(String name, String[] materialNames) {
/*  54 */     Validate.notNull(name, "Type name cannot be null");
/*  55 */     Validate.notNull(materialNames, "Material names cannot be null");
/*     */ 
/*  57 */     List materials = new ArrayList(materialNames.length);
/*  58 */     for (String materialName : materialNames)
/*     */       try {
/*  60 */         Material material = Material.valueOf(materialName);
/*  61 */         materials.add(material);
/*     */       }
/*     */       catch (IllegalArgumentException e) {
/*     */       }
/*  65 */     register(new ArmorType(name, (Material[])materials.toArray(new Material[materials.size()])));
/*  66 */     return true;
/*     */   }
/*     */ 
/*     */   public static void reset() {
/*  70 */     registeredNames.clear();
/*  71 */     registeredMaterials.clear();
/*     */ 
/*  74 */     register(AIR);
/*  75 */     register(LEATHER);
/*  76 */     register(GOLD);
/*  77 */     register(CHAINMAIL);
/*  78 */     register(IRON);
/*  79 */     register(DIAMOND);
/*     */ 
/*  85 */     registerIfAvailable("metallurgy_copper", new String[] { "METALLURGY_METALLURGY_COPPER_HELMET", "METALLURGY_METALLURGY_COPPER_CHESTPLATE", "METALLURGY_METALLURGY_COPPER_LEGGINGS", "METALLURGY_METALLURGY_COPPER_BOOTS" });
/*  86 */     registerIfAvailable("metallurgy_astral_silver", new String[] { "METALLURGY_METALLURGY_ASTRAL_SILVER_HELMET", "METALLURGY_METALLURGY_ASTRAL_SILVER_CHESTPLATE", "METALLURGY_METALLURGY_ASTRAL_SILVER_LEGGINGS", "METALLURGY_METALLURGY_ASTRAL_SILVER_BOOTS" });
/*  87 */     registerIfAvailable("metallurgy_steel", new String[] { "METALLURGY_METALLURGY_STEEL_HELMET", "METALLURGY_METALLURGY_STEEL_CHESTPLATE", "METALLURGY_METALLURGY_STEEL_LEGGINGS", "METALLURGY_METALLURGY_STEEL_BOOTS" });
/*  88 */     registerIfAvailable("metallurgy_haderoth", new String[] { "METALLURGY_METALLURGY_HADEROTH_HELMET", "METALLURGY_METALLURGY_HADEROTH_CHESTPLATE", "METALLURGY_METALLURGY_HADEROTH_LEGGINGS", "METALLURGY_METALLURGY_HADEROTH_BOOTS" });
/*  89 */     registerIfAvailable("metallurgy_quicksilver", new String[] { "METALLURGY_METALLURGY_QUICKSILVER_HELMET", "METALLURGY_METALLURGY_QUICKSILVER_CHESTPLATE", "METALLURGY_METALLURGY_QUICKSILVER_LEGGINGS", "METALLURGY_METALLURGY_QUICKSILVER_BOOTS" });
/*  90 */     registerIfAvailable("metallurgy_bronze", new String[] { "METALLURGY_METALLURGY_BRONZE_HELMET", "METALLURGY_METALLURGY_BRONZE_CHESTPLATE", "METALLURGY_METALLURGY_BRONZE_LEGGINGS", "METALLURGY_METALLURGY_BRONZE_BOOTS" });
/*  91 */     registerIfAvailable("metallurgy_angmallen", new String[] { "METALLURGY_METALLURGY_ANGMALLEN_HELMET", "METALLURGY_METALLURGY_ANGMALLEN_CHESTPLATE", "METALLURGY_METALLURGY_ANGMALLEN_LEGGINGS", "METALLURGY_METALLURGY_ANGMALLEN_BOOTS" });
/*  92 */     registerIfAvailable("metallurgy_deep_iron", new String[] { "METALLURGY_METALLURGY_DEEP_IRON_HELMET", "METALLURGY_METALLURGY_DEEP_IRON_CHESTPLATE", "METALLURGY_METALLURGY_DEEP_IRON_LEGGINGS", "METALLURGY_METALLURGY_DEEP_IRON_BOOTS" });
/*  93 */     registerIfAvailable("metallurgy_black_steel", new String[] { "METALLURGY_METALLURGY_BLACK_STEEL_HELMET", "METALLURGY_METALLURGY_BLACK_STEEL_CHESTPLATE", "METALLURGY_METALLURGY_BLACK_STEEL_LEGGINGS", "METALLURGY_METALLURGY_BLACK_STEEL_BOOTS" });
/*  94 */     registerIfAvailable("metallurgy_prometheum", new String[] { "METALLURGY_METALLURGY_PROMETHEUM_HELMET", "METALLURGY_METALLURGY_PROMETHEUM_CHESTPLATE", "METALLURGY_METALLURGY_PROMETHEUM_LEGGINGS", "METALLURGY_METALLURGY_PROMETHEUM_BOOTS" });
/*  95 */     registerIfAvailable("metallurgy_oureclase", new String[] { "METALLURGY_METALLURGY_OURECLASE_HELMET", "METALLURGY_METALLURGY_OURECLASE_CHESTPLATE", "METALLURGY_METALLURGY_OURECLASE_LEGGINGS", "METALLURGY_METALLURGY_OURECLASE_BOOTS" });
/*  96 */     registerIfAvailable("metallurgy_carmot", new String[] { "METALLURGY_METALLURGY_CARMOT_HELMET", "METALLURGY_METALLURGY_CARMOT_CHESTPLATE", "METALLURGY_METALLURGY_CARMOT_LEGGINGS", "METALLURGY_METALLURGY_CARMOT_BOOTS" });
/*  97 */     registerIfAvailable("metallurgy_orichalcum", new String[] { "METALLURGY_METALLURGY_ORICHALCUM_HELMET", "METALLURGY_METALLURGY_ORICHALCUM_CHESTPLATE", "METALLURGY_METALLURGY_ORICHALCUM_LEGGINGS", "METALLURGY_METALLURGY_ORICHALCUM_BOOTS" });
/*  98 */     registerIfAvailable("metallurgy_damascus_steel", new String[] { "METALLURGY_METALLURGY_DAMASCUS_STEEL_HELMET", "METALLURGY_METALLURGY_DAMASCUS_STEEL_CHESTPLATE", "METALLURGY_METALLURGY_DAMASCUS_STEEL_LEGGINGS", "METALLURGY_METALLURGY_DAMASCUS_STEEL_BOOTS" });
/*  99 */     registerIfAvailable("metallurgy_mithril", new String[] { "METALLURGY_METALLURGY_MITHRIL_HELMET", "METALLURGY_METALLURGY_MITHRIL_CHESTPLATE", "METALLURGY_METALLURGY_MITHRIL_LEGGINGS", "METALLURGY_METALLURGY_MITHRIL_BOOTS" });
/* 100 */     registerIfAvailable("metallurgy_hepatizon", new String[] { "METALLURGY_METALLURGY_HEPATIZON_HELMET", "METALLURGY_METALLURGY_HEPATIZON_CHESTPLATE", "METALLURGY_METALLURGY_HEPATIZON_LEGGINGS", "METALLURGY_METALLURGY_HEPATIZON_BOOTS" });
/* 101 */     registerIfAvailable("metallurgy_desichalkos", new String[] { "METALLURGY_METALLURGY_DESICHALKOS_HELMET", "METALLURGY_METALLURGY_DESICHALKOS_CHESTPLATE", "METALLURGY_METALLURGY_DESICHALKOS_LEGGINGS", "METALLURGY_METALLURGY_DESICHALKOS_BOOTS" });
/* 102 */     registerIfAvailable("metallurgy_celenegil", new String[] { "METALLURGY_METALLURGY_CELENEGIL_HELMET", "METALLURGY_METALLURGY_CELENEGIL_CHESTPLATE", "METALLURGY_METALLURGY_CELENEGIL_LEGGINGS", "METALLURGY_METALLURGY_CELENEGIL_BOOTS" });
/* 103 */     registerIfAvailable("metallurgy_eximite", new String[] { "METALLURGY_METALLURGY_EXIMITE_HELMET", "METALLURGY_METALLURGY_EXIMITE_CHESTPLATE", "METALLURGY_METALLURGY_EXIMITE_LEGGINGS", "METALLURGY_METALLURGY_EXIMITE_BOOTS" });
/* 104 */     registerIfAvailable("metallurgy_kalendrite", new String[] { "METALLURGY_METALLURGY_KALENDRITE_HELMET", "METALLURGY_METALLURGY_KALENDRITE_CHESTPLATE", "METALLURGY_METALLURGY_KALENDRITE_LEGGINGS", "METALLURGY_METALLURGY_KALENDRITE_BOOTS" });
/* 105 */     registerIfAvailable("metallurgy_electrum", new String[] { "METALLURGY_METALLURGY_ELECTRUM_HELMET", "METALLURGY_METALLURGY_ELECTRUM_CHESTPLATE", "METALLURGY_METALLURGY_ELECTRUM_LEGGINGS", "METALLURGY_METALLURGY_ELECTRUM_BOOTS" });
/* 106 */     registerIfAvailable("metallurgy_vulcanite", new String[] { "METALLURGY_METALLURGY_VULCANITE_HELMET", "METALLURGY_METALLURGY_VULCANITE_CHESTPLATE", "METALLURGY_METALLURGY_VULCANITE_LEGGINGS", "METALLURGY_METALLURGY_VULCANITE_BOOTS" });
/* 107 */     registerIfAvailable("metallurgy_sanguinite", new String[] { "METALLURGY_METALLURGY_SANGUINITE_HELMET", "METALLURGY_METALLURGY_SANGUINITE_CHESTPLATE", "METALLURGY_METALLURGY_SANGUINITE_LEGGINGS", "METALLURGY_METALLURGY_SANGUINITE_BOOTS" });
/* 108 */     registerIfAvailable("metallurgy_inolashite", new String[] { "METALLURGY_METALLURGY_INOLASHITE_HELMET", "METALLURGY_METALLURGY_INOLASHITE_CHESTPLATE", "METALLURGY_METALLURGY_INOLASHITE_LEGGINGS", "METALLURGY_METALLURGY_INOLASHITE_BOOTS" });
/* 109 */     registerIfAvailable("metallurgy_amordrine", new String[] { "METALLURGY_METALLURGY_AMORDRINE_HELMET", "METALLURGY_METALLURGY_AMORDRINE_CHESTPLATE", "METALLURGY_METALLURGY_AMORDRINE_LEGGINGS", "METALLURGY_METALLURGY_AMORDRINE_BOOTS" });
/* 110 */     registerIfAvailable("metallurgy_shadow_steel", new String[] { "METALLURGY_METALLURGY_SHADOW_STEEL_HELMET", "METALLURGY_METALLURGY_SHADOW_STEEL_CHESTPLATE", "METALLURGY_METALLURGY_SHADOW_STEEL_LEGGINGS", "METALLURGY_METALLURGY_SHADOW_STEEL_BOOTS" });
/* 111 */     registerIfAvailable("metallurgy_silver", new String[] { "METALLURGY_METALLURGY_SILVER_HELMET", "METALLURGY_METALLURGY_SILVER_CHESTPLATE", "METALLURGY_METALLURGY_SILVER_LEGGINGS", "METALLURGY_METALLURGY_SILVER_BOOTS" });
/* 112 */     registerIfAvailable("metallurgy_brass", new String[] { "METALLURGY_METALLURGY_BRASS_HELMET", "METALLURGY_METALLURGY_BRASS_CHESTPLATE", "METALLURGY_METALLURGY_BRASS_LEGGINGS", "METALLURGY_METALLURGY_BRASS_BOOTS" });
/* 113 */     registerIfAvailable("metallurgy_platinum", new String[] { "METALLURGY_METALLURGY_PLATINUM_HELMET", "METALLURGY_METALLURGY_PLATINUM_CHESTPLATE", "METALLURGY_METALLURGY_PLATINUM_LEGGINGS", "METALLURGY_METALLURGY_PLATINUM_BOOTS" });
/* 114 */     registerIfAvailable("metallurgy_ceruclase", new String[] { "METALLURGY_METALLURGY_CERUCLASE_HELMET", "METALLURGY_METALLURGY_CERUCLASE_CHESTPLATE", "METALLURGY_METALLURGY_CERUCLASE_LEGGINGS", "METALLURGY_METALLURGY_CERUCLASE_BOOTS" });
/* 115 */     registerIfAvailable("metallurgy_midasium", new String[] { "METALLURGY_METALLURGY_MIDASIUM_HELMET", "METALLURGY_METALLURGY_MIDASIUM_CHESTPLATE", "METALLURGY_METALLURGY_MIDASIUM_LEGGINGS", "METALLURGY_METALLURGY_MIDASIUM_BOOTS" });
/* 116 */     registerIfAvailable("metallurgy_shadow_iron", new String[] { "METALLURGY_METALLURGY_SHADOW_IRON_HELMET", "METALLURGY_METALLURGY_SHADOW_IRON_CHESTPLATE", "METALLURGY_METALLURGY_SHADOW_IRON_LEGGINGS", "METALLURGY_METALLURGY_SHADOW_IRON_BOOTS" });
/* 117 */     registerIfAvailable("metallurgy_ignatius", new String[] { "METALLURGY_METALLURGY_IGNATIUS_HELMET", "METALLURGY_METALLURGY_IGNATIUS_CHESTPLATE", "METALLURGY_METALLURGY_IGNATIUS_LEGGINGS", "METALLURGY_METALLURGY_IGNATIUS_BOOTS" });
/* 118 */     registerIfAvailable("metallurgy_atlarus", new String[] { "METALLURGY_METALLURGY_ATLARUS_HELMET", "METALLURGY_METALLURGY_ATLARUS_CHESTPLATE", "METALLURGY_METALLURGY_ATLARUS_LEGGINGS", "METALLURGY_METALLURGY_ATLARUS_BOOTS" });
/* 119 */     registerIfAvailable("metallurgy_adamantine", new String[] { "METALLURGY_METALLURGY_ADAMANTINE_HELMET", "METALLURGY_METALLURGY_ADAMANTINE_CHESTPLATE", "METALLURGY_METALLURGY_ADAMANTINE_LEGGINGS", "METALLURGY_METALLURGY_ADAMANTINE_BOOTS" });
/* 120 */     registerIfAvailable("metallurgy_vyroxeres", new String[] { "METALLURGY_METALLURGY_VYROXERES_HELMET", "METALLURGY_METALLURGY_VYROXERES_CHESTPLATE", "METALLURGY_METALLURGY_VYROXERES_LEGGINGS", "METALLURGY_METALLURGY_VYROXERES_BOOTS" });
/* 121 */     registerIfAvailable("metallurgy_tartarite", new String[] { "METALLURGY_METALLURGY_TARTARITE_HELMET", "METALLURGY_METALLURGY_TARTARITE_CHESTPLATE", "METALLURGY_METALLURGY_TARTARITE_LEGGINGS", "METALLURGY_METALLURGY_TARTARITE_BOOTS" });
/*     */ 
/* 125 */     registerIfAvailable("ic2_bronze", new String[] { "IC2_ITEMARMORBRONZEHELMET", "IC2_ITEMARMORBRONZECHESTPLATE", "IC2_ITEMARMORBRONZELEGS", "IC2_ITEMARMORBRONZEBOOTS" });
/* 126 */     registerIfAvailable("ic2_nano", new String[] { "IC2_ITEMARMORNANOHELMET", "IC2_ITEMARMORNANOCHESTPLATE", "IC2_ITEMARMORNANOLEGS", "ITEMARMORNANOBOOTS" });
/* 127 */     registerIfAvailable("ic2_quantum", new String[] { "IC2_ITEMARMORQUANTUMHELMET", "IC2_ITEMARMORQUANTUMCHESTPLATE", "IC2_ITEMARMORQUANTUMLEGS", "IC2_ITEMARMORQUANTUMBOOTS" });
/* 128 */     registerIfAvailable("ic2_hazmat", new String[] { "IC2_ITEMARMORHAZMATHELMET", "IC2_ITEMARMORHAZMATCHSTPLATE", "IC2_ITEMARMORHAZMATLEGS", "IC2_ITEMARMORHAZMATBOOTS" });
/*     */ 
/* 132 */     registerIfAvailable("harvestcraft_hardened_leather", new String[] { "HARVESTCRAFT_HARDENEDLEATHERHELMITEM", "HARVESTCRAFT_HARDENEDLEATHERCHESTITEM", "HARVESTCRAFT_HARDENEDLEATHERLEGGINGSITEM", "HARVESTCRAFT_HARDENEDLEATHERBOOTSITEM" });
/*     */ 
/* 136 */     registerIfAvailable("desertcraft_cactus", new String[] { "DESERTCRAFT_CACTUS_HELM", "DESERTCRAFT_CACTUS_CHEST", "DESERTCRAFT_CACTUS_LEGS", "DESERTCRAFT_CACTUS_BOOTS" });
/*     */ 
/* 140 */     registerIfAvailable("tconstruct_wood", new String[] { "TCONSTRUCT_HELMETWOOD", "TCONSTRUCT_CHESTPLATEWOOD", "TCONSTRUCT_LEGGINGSWOOD", "TCONSTRUCT_BOOTSWOOD" });
/* 141 */     registerIfAvailable("tconstruct_travel", new String[] { "TCONSTRUCT_TRAVELGOGGLES", "TCONSTRUCT_TRAVELVEST", "TCONSTRUCT_TRAVELWINGS", "TCONSTRUCT_TRAVELBOOTS" });
/*     */ 
/* 144 */     registerIfAvailable("customnpcs_cowleather", new String[] { "CUSTOMNPCS_NPCCOWLEATHERHEAD", "CUSTOMNPCS_NPCCOWLEATHERCHEST", "CUSTOMNPCS_NPCCOWLEATHERLEGS", "CUSTOMNPCS_NPCCOWLEATHERBOOTS" });
/* 145 */     registerIfAvailable("customnpcs_nanorum", new String[] { "CUSTOMNPCS_NPCNANORUMHEAD", "CUSTOMNPCS_NPCNANORUMCHEST", "CUSTOMNPCS_NPCNANORUMLEGS", "CUSTOMNPCS_NPCNANORUMBOOTS" });
/* 146 */     registerIfAvailable("customnpcs_tactical", new String[] { "CUSTOMNPCS_NPCTACTICALHEAD", "CUSTOMNPCS_NPCTACTICALCHEST" });
/* 147 */     registerIfAvailable("customnpcs_full_leather", new String[] { "CUSTOMNPCS_NPCFULLLEATHERHEAD", "CUSTOMNPCS_NPCFULLLEATHERCHEST" });
/* 148 */     registerIfAvailable("customnpcs_full_gold", new String[] { "CUSTOMNPCS_NPCFULLGOLDHEAD", "CUSTOMNPCS_NPCFULLGOLDCHEST" });
/* 149 */     registerIfAvailable("customnpcs_full_iron", new String[] { "CUSTOMNPCS_NPCFULLIRONHEAD", "CUSTOMNPCS_NPCFULLIRONCHEST" });
/* 150 */     registerIfAvailable("customnpcs_full_diamond", new String[] { "CUSTOMNPCS_NPCFULLDIAMONDHEAD", "CUSTOMNPCS_NPCFULLDIAMONDCHEST" });
/* 151 */     registerIfAvailable("customnpcs_full_bronze", new String[] { "CUSTOMNPCS_NPCFULLBRONZEHEAD", "CUSTOMNPCS_NPCFULLBRONZECHEST", "CUSTOMNPCS_NPCFULLBRONZELEGGINGS", "CUSTOMNPCS_NPCFULLBRONZEBOOTS" });
/* 152 */     registerIfAvailable("customnpcs_full_emerald", new String[] { "CUSTOMNPCS_NPCFULLEMERALDHEAD", "CUSTOMNPCS_NPCFULLEMERALDCHEST", "CUSTOMNPCS_NPCFULLEMERALDLEGGINGS", "CUSTOMNPCS_NPCFULLEMERALDBOOTS" });
/* 153 */     registerIfAvailable("customnpcs_full_wooden", new String[] { "CUSTOMNPCS_NPCFULLWOODENHEAD", "CUSTOMNPCS_NPCFULLWOODENCHEST", "CUSTOMNPCS_NPCFULLWOODENLEGGINGS", "CUSTOMNPCS_NPCFULLWOODENBOOTS" });
/* 154 */     registerIfAvailable("customnpcs_tuxedo", new String[] { "CUSTOMNPCS_NPCTUXEDOCHEST", "CUSTOMNPCS_NPCTUXEDOPANTS", "CUSTOMNPCS_NPCTUXEDOBOTTOM" });
/* 155 */     registerIfAvailable("customnpcs_wizard", new String[] { "CUSTOMNPCS_NPCWIZARDCHEST", "CUSTOMNPCS_NPCWIZARDCHEST", "CUSTOMNPCS_NPCWIZARDPANTS" });
/* 156 */     registerIfAvailable("customnpcs_assassin", new String[] { "CUSTOMNPCS_NPCASSASSINHEAD", "CUSTOMNPCS_NPCASSASSINCHEST", "CUSTOMNPCS_NPCASSASSINLEGGINGS", "CUSTOMNPCS_NPCASSASSINBOOTS" });
/* 157 */     registerIfAvailable("customnpcs_soldier", new String[] { "CUSTOMNPCS_NPCSOLDIERHEAD", "CUSTOMNPCS_NPCSOLDIERCHEST", "CUSTOMNPCS_NPCSOLDIERLEGS", "CUSTOMNPCS_NPCSOLDIERBOTTOM" });
/* 158 */     registerIfAvailable("customnpcs_x407", new String[] { "CUSTOMNPCS_NPCX407HEAD", "CUSTOMNPCS_NPCX407CHEST", "CUSTOMNPCS_NPCX407LEGS", "CUSTOMNPCS_NPCX407BOOTS" });
/* 159 */     registerIfAvailable("customnpcs_commissar", new String[] { "CUSTOMNPCS_NPCCOMMISSARHEAD", "CUSTOMNPCS_NPCCOMMISSARCHEST", "CUSTOMNPCS_NPCCOMMISSARLEGS", "CUSTOMNPCS_NPCCOMMISSARBOTTOM" });
/* 160 */     registerIfAvailable("customnpcs_infantry", new String[] { "CUSTOMNPCS_NPCINFANTRYHELMET" });
/* 161 */     registerIfAvailable("customnpcs_ninja", new String[] { "CUSTOMNPCS_NPCNINJAHEAD", "CUSTOMNPCS_NPCNINJACHEST", "CUSTOMNPCS_NPCNINJAPANTS" });
/* 162 */     registerIfAvailable("customnpcs_officer", new String[] { "CUSTOMNPCS_NPCOFFICERCHEST" });
/* 163 */     registerIfAvailable("customnpcs_bandit", new String[] { "CUSTOMNPCS_NPCBANDITMASK" });
/* 164 */     registerIfAvailable("customnpcs_crown", new String[] { "CUSTOMNPCS_NPCCROWN" });
/* 165 */     registerIfAvailable("customnpcs_crown2", new String[] { "CUSTOMNPCS_NPCCROWN2" });
/* 166 */     registerIfAvailable("customnpcs_papercrown", new String[] { "CUSTOMNPCS_NPCPAPERCROWN" });
/* 167 */     registerIfAvailable("customnpcs_iron_skirt", new String[] { "CUSTOMNPCS_NPCIRONSKIRT" });
/* 168 */     registerIfAvailable("customnpcs_chain_skirt", new String[] { "CUSTOMNPCS_NPCCHAINSKIRT" });
/* 169 */     registerIfAvailable("customnpcs_leather_skirt", new String[] { "CUSTOMNPCS_NPCLEATHERSKIRT" });
/* 170 */     registerIfAvailable("customnpcs_diamond_skirt", new String[] { "CUSTOMNPCS_NPCDIAMONDSKIRT" });
/* 171 */     registerIfAvailable("customnpcs_gold_skirt", new String[] { "CUSTOMNPCS_NPCGOLDSKIRT" });
/*     */   }
/*     */ 
/*     */   public ArmorType(Material material)
/*     */   {
/* 194 */     this(material.toString().toLowerCase(), new Material[] { material });
/*     */   }
/*     */ 
/*     */   public ArmorType(String name, Material[] contentMaterials) {
/* 198 */     this.name = name;
/* 199 */     this.materials = contentMaterials;
/* 200 */     this.weight = 1.0D;
/*     */   }
/*     */ 
/*     */   public String getName() {
/* 204 */     return this.name;
/*     */   }
/*     */ 
/*     */   public Material[] getMaterial() {
/* 208 */     return this.materials;
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public double getWeight() {
/* 213 */     return this.weight;
/*     */   }
/*     */ 
/*     */   public void setWeight(double weight) {
/* 217 */     this.weight = weight;
/*     */   }
/*     */ 
/*     */   public double getWeight(ItemStack item) {
/* 221 */     return getWeight();
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 226 */     return "ArmorType [name=" + this.name + ",materials=" + Arrays.toString(this.materials) + "]";
/*     */   }
/*     */ 
/*     */   public static double getItemWeight(ItemStack item) {
/* 230 */     return valueOf(item.getType()).getWeight(item);
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/* 177 */     AIR.setWeight(0.0D);
/*     */ 
/* 179 */     LEATHER = new ArmorType("leather", new Material[] { Material.LEATHER_HELMET, Material.LEATHER_CHESTPLATE, Material.LEATHER_LEGGINGS, Material.LEATHER_BOOTS });
/* 180 */     GOLD = new ArmorType("gold", new Material[] { Material.GOLD_HELMET, Material.GOLD_CHESTPLATE, Material.GOLD_LEGGINGS, Material.GOLD_BOOTS, Material.GOLD_BARDING });
/* 181 */     CHAINMAIL = new ArmorType("chainmail", new Material[] { Material.CHAINMAIL_HELMET, Material.CHAINMAIL_CHESTPLATE, Material.CHAINMAIL_LEGGINGS, Material.CHAINMAIL_BOOTS });
/* 182 */     IRON = new ArmorType("iron", new Material[] { Material.IRON_HELMET, Material.IRON_CHESTPLATE, Material.IRON_LEGGINGS, Material.IRON_BOOTS, Material.IRON_BARDING });
/* 183 */     DIAMOND = new ArmorType("diamond", new Material[] { Material.DIAMOND_HELMET, Material.DIAMOND_CHESTPLATE, Material.DIAMOND_LEGGINGS, Material.DIAMOND_BOOTS, Material.DIAMOND_BARDING });
/*     */ 
/* 185 */     reset();
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\ArmorWeight.jar
 * Qualified Name:     com.zettelnet.armorweight.ArmorType
 * JD-Core Version:    0.6.2
 */