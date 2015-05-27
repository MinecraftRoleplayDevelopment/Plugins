/*     */ package jp.mydns.dyukusi.itemsounds;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.logging.Logger;
/*     */ import jp.mydns.dyukusi.Metrics;
/*     */ import jp.mydns.dyukusi.itemsounds.listener.InventoryListener;
/*     */ import jp.mydns.dyukusi.itemsounds.soundinfo.SoundInformation;
/*     */ import org.bukkit.ChatColor;
/*     */ import org.bukkit.Material;
/*     */ import org.bukkit.Server;
/*     */ import org.bukkit.Sound;
/*     */ import org.bukkit.configuration.file.FileConfiguration;
/*     */ import org.bukkit.plugin.PluginManager;
/*     */ import org.bukkit.plugin.java.JavaPlugin;
/*     */ 
/*     */ public class ItemSounds extends JavaPlugin
/*     */ {
/*     */   String item_sound_list_path;
/*     */   private HashMap<Material, SoundInformation> equip_armor_map;
/*     */   private HashMap<Material, SoundInformation> itemsound_map;
/*     */   private HashMap<Material, SoundInformation> handitem_sound_map;
/*     */   private SoundInformation default_equip_armor_sound;
/*     */   private SoundInformation default_handitem_sound;
/*     */   private SoundInformation default_item_sound;
/*     */ 
/*     */   public void onEnable()
/*     */   {
/*     */     try
/*     */     {
/*  31 */       Metrics metrics = new Metrics(this);
/*  32 */       metrics.start();
/*     */     } catch (IOException e) {
/*  34 */       getLogger().info(ChatColor.RED + "Failed to submit the stats");
/*     */     }
/*     */ 
/*  39 */     if (!new File(getDataFolder().getAbsolutePath() + "/config.yml").exists()) {
/*  40 */       getLogger().info("Creating config.yml ...");
/*  41 */       saveDefaultConfig();
/*     */     }
/*     */ 
/*  45 */     String[] armor = getConfig().getString("default_equip_armor_sound").split(",");
/*  46 */     this.default_equip_armor_sound = new SoundInformation(Sound.valueOf(armor[0]), Float.valueOf(Float.parseFloat(armor[1])), 
/*  47 */       Float.valueOf(Float.parseFloat(armor[2])));
/*     */ 
/*  50 */     List setting_list = getConfig().getStringList("equip_armor_sounds");
/*  51 */     this.equip_armor_map = new HashMap();
/*     */     String[] array;
/*  53 */     for (String str : setting_list) {
/*  54 */       array = str.split(",");
/*  55 */       Material material = Material.valueOf(array[0]);
/*  56 */       Sound sound = Sound.valueOf(array[1]);
/*  57 */       float volume = Float.parseFloat(array[2]);
/*  58 */       float pitch = Float.parseFloat(array[3]);
/*     */ 
/*  60 */       this.equip_armor_map.put(material, new SoundInformation(sound, Float.valueOf(volume), Float.valueOf(pitch)));
/*     */     }
/*     */ 
/*  64 */     String[] equip = getConfig().getString("default_equip_handitem_sound").split(",");
/*  65 */     this.default_handitem_sound = new SoundInformation(Sound.valueOf(equip[0]), Float.valueOf(Float.parseFloat(equip[1])), 
/*  66 */       Float.valueOf(Float.parseFloat(equip[2])));
/*     */ 
/*  69 */     setting_list = getConfig().getStringList("equip_handitem_sounds");
/*  70 */     this.handitem_sound_map = new HashMap();
/*     */     String[] array;
/*  72 */     for (String str : setting_list) {
/*  73 */       array = str.split(",");
/*  74 */       Material material = Material.valueOf(array[0]);
/*  75 */       Sound sound = Sound.valueOf(array[1]);
/*  76 */       float volume = Float.parseFloat(array[2]);
/*  77 */       float pitch = Float.parseFloat(array[3]);
/*     */ 
/*  79 */       this.handitem_sound_map.put(material, new SoundInformation(sound, Float.valueOf(volume), Float.valueOf(pitch)));
/*     */     }
/*     */ 
/*  83 */     String[] undefined = getConfig().getString("default_put_item_sound").split(",");
/*  84 */     this.default_item_sound = new SoundInformation(Sound.valueOf(undefined[0]), Float.valueOf(Float.parseFloat(undefined[1])), 
/*  85 */       Float.valueOf(Float.parseFloat(undefined[2])));
/*     */ 
/*  88 */     setting_list = getConfig().getStringList("put_item_sounds");
/*  89 */     this.itemsound_map = new HashMap();
/*     */ 
/*  91 */     for (String str : setting_list) {
/*  92 */       String[] array = str.split(",");
/*  93 */       Material material = Material.valueOf(array[0]);
/*  94 */       Sound sound = Sound.valueOf(array[1]);
/*  95 */       float volume = Float.parseFloat(array[2]);
/*  96 */       float pitch = Float.parseFloat(array[3]);
/*     */ 
/*  98 */       this.itemsound_map.put(material, new SoundInformation(sound, Float.valueOf(volume), Float.valueOf(pitch)));
/*     */     }
/*     */ 
/* 101 */     getServer().getPluginManager().registerEvents(new InventoryListener(this, this.itemsound_map), this);
/*     */   }
/*     */ 
/*     */   public void onDisable()
/*     */   {
/*     */   }
/*     */ 
/*     */   public boolean iscontain_put_sound(Material material)
/*     */   {
/* 110 */     return this.itemsound_map.containsKey(material);
/*     */   }
/*     */ 
/*     */   public boolean iscontain_handitem_sound(Material material) {
/* 114 */     return this.handitem_sound_map.containsKey(material);
/*     */   }
/*     */ 
/*     */   public boolean iscontain_equip_armor_sound(Material material) {
/* 118 */     return this.equip_armor_map.containsKey(material);
/*     */   }
/*     */ 
/*     */   public SoundInformation get_put_sound_inf(Material material) {
/* 122 */     return (SoundInformation)this.itemsound_map.get(material);
/*     */   }
/*     */ 
/*     */   public SoundInformation get_handitem_sound_inf(Material material) {
/* 126 */     return (SoundInformation)this.handitem_sound_map.get(material);
/*     */   }
/*     */ 
/*     */   public SoundInformation get_equip_armor_sound_inf(Material material) {
/* 130 */     return (SoundInformation)this.equip_armor_map.get(material);
/*     */   }
/*     */ 
/*     */   public SoundInformation get_default_equip_armor_sound_inf() {
/* 134 */     return this.default_equip_armor_sound;
/*     */   }
/*     */ 
/*     */   public SoundInformation get_default_item_sound_inf() {
/* 138 */     return this.default_item_sound;
/*     */   }
/*     */ 
/*     */   public SoundInformation get_default_handitem_sound_inf() {
/* 142 */     return this.default_handitem_sound;
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\ItemSounds.jar
 * Qualified Name:     jp.mydns.dyukusi.itemsounds.ItemSounds
 * JD-Core Version:    0.6.2
 */