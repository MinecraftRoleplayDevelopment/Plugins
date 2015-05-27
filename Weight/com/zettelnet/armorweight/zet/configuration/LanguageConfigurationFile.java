/*     */ package com.zettelnet.armorweight.zet.configuration;
/*     */ 
/*     */ import com.zettelnet.armorweight.zet.chat.ChatMessage;
/*     */ import com.zettelnet.armorweight.zet.chat.FormatOption;
/*     */ import com.zettelnet.armorweight.zet.chat.FormattedChatMessage;
/*     */ import com.zettelnet.armorweight.zet.chat.MessageValueMap;
/*     */ import java.io.File;
/*     */ import java.io.InputStream;
/*     */ import org.bukkit.ChatColor;
/*     */ import org.bukkit.configuration.file.FileConfiguration;
/*     */ import org.bukkit.plugin.Plugin;
/*     */ 
/*     */ public abstract class LanguageConfigurationFile extends PluginConfigurationFile
/*     */ {
/*     */   private final MessageValueMap formatOptions;
/*     */   private final FileConfiguration defaultConfiguration;
/*     */ 
/*     */   public LanguageConfigurationFile(Plugin plugin, String file, String resource)
/*     */   {
/*  20 */     this(plugin, file, resource, defaultConfiguration());
/*     */   }
/*     */ 
/*     */   public LanguageConfigurationFile(Plugin plugin, String file, String resource, FileConfiguration config) {
/*  24 */     this(new File(plugin.getDataFolder(), file), plugin.getResource(resource), config);
/*     */   }
/*     */ 
/*     */   public LanguageConfigurationFile(File file, InputStream resource) {
/*  28 */     this(file, resource, defaultConfiguration());
/*     */   }
/*     */ 
/*     */   public LanguageConfigurationFile(File file, InputStream resource, FileConfiguration config) {
/*  32 */     super(file, resource, config);
/*     */ 
/*  34 */     this.formatOptions = new MessageValueMap();
/*  35 */     this.formatOptions.put("aqua", ChatColor.AQUA.toString());
/*  36 */     this.formatOptions.put("black", ChatColor.BLACK.toString());
/*  37 */     this.formatOptions.put("blue", ChatColor.BLUE.toString());
/*  38 */     this.formatOptions.put("bold", ChatColor.BOLD.toString());
/*  39 */     this.formatOptions.put("darkAqua", ChatColor.DARK_AQUA.toString());
/*  40 */     this.formatOptions.put("darkBlue", ChatColor.DARK_BLUE.toString());
/*  41 */     this.formatOptions.put("darkGray", ChatColor.DARK_GRAY.toString());
/*  42 */     this.formatOptions.put("darkGreen", ChatColor.DARK_GREEN.toString());
/*  43 */     this.formatOptions.put("darkPurple", ChatColor.DARK_PURPLE.toString());
/*  44 */     this.formatOptions.put("darkRed", ChatColor.DARK_RED.toString());
/*  45 */     this.formatOptions.put("gold", ChatColor.GOLD.toString());
/*  46 */     this.formatOptions.put("gray", ChatColor.GRAY.toString());
/*  47 */     this.formatOptions.put("green", ChatColor.GREEN.toString());
/*  48 */     this.formatOptions.put("italic", ChatColor.ITALIC.toString());
/*  49 */     this.formatOptions.put("lightPurple", ChatColor.LIGHT_PURPLE.toString());
/*  50 */     this.formatOptions.put("magic", ChatColor.MAGIC.toString());
/*  51 */     this.formatOptions.put("red", ChatColor.RED.toString());
/*  52 */     this.formatOptions.put("reset", ChatColor.RESET.toString());
/*  53 */     this.formatOptions.put("strikethrough", ChatColor.STRIKETHROUGH.toString());
/*  54 */     this.formatOptions.put("underline", ChatColor.UNDERLINE.toString());
/*  55 */     this.formatOptions.put("white", ChatColor.WHITE.toString());
/*  56 */     this.formatOptions.put("yellow", ChatColor.YELLOW.toString());
/*     */ 
/*  58 */     this.formatOptions.put("heart", new FormatOption("❤", "", ""));
/*  59 */     this.formatOptions.put("heartSmall", new FormatOption("♥", "", ""));
/*  60 */     this.formatOptions.put("heartWhite", new FormatOption("♡", "", ""));
/*  61 */     this.formatOptions.put("heartRotated", new FormatOption("❥", "", ""));
/*  62 */     this.formatOptions.put("heartExclamation", new FormatOption("❣", "", ""));
/*     */ 
/*  64 */     this.formatOptions.put("br", new FormatOption("\n", System.lineSeparator(), System.lineSeparator()));
/*  65 */     this.formatOptions.put("...", new FormatOption("…", "...", "..."));
/*  66 */     this.formatOptions.put(" ", new FormatOption("  "));
/*     */ 
/*  68 */     this.defaultConfiguration = defaultConfiguration();
/*     */   }
/*     */ 
/*     */   public abstract String getLanguage();
/*     */ 
/*     */   public void loadDefaults() {
/*  74 */     loadConfiguration(this.defaultConfiguration, getResource(), getCharset());
/*     */   }
/*     */ 
/*     */   public FileConfiguration getDefaultConfig() {
/*  78 */     return this.defaultConfiguration;
/*     */   }
/*     */ 
/*     */   public void loadValues(FileConfiguration config)
/*     */   {
/*  83 */     loadValues(config, this.defaultConfiguration);
/*     */   }
/*     */ 
/*     */   public abstract void loadValues(FileConfiguration paramFileConfiguration1, FileConfiguration paramFileConfiguration2);
/*     */ 
/*     */   protected ChatMessage load(String path, String defaultValue) {
/*  89 */     path = getLanguage() + "." + path;
/*  90 */     String value = getConfig().getString(path);
/*  91 */     if (value == null) {
/*  92 */       value = getDefaultConfig().getString(path, defaultValue);
/*     */     }
/*  94 */     return new FormattedChatMessage(value, defaultValue, this.formatOptions);
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   protected ChatMessage load(String path) {
/*  99 */     path = getLanguage() + "." + path;
/* 100 */     String value = getConfig().getString(path);
/* 101 */     if (value == null) {
/* 102 */       value = getDefaultConfig().getString(path);
/*     */     }
/* 104 */     return new FormattedChatMessage(value, value, this.formatOptions);
/*     */   }
/*     */ 
/*     */   protected void addFormatOption(String name, CharSequence option) {
/* 108 */     this.formatOptions.put(name, option);
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\ArmorWeight.jar
 * Qualified Name:     com.zettelnet.armorweight.zet.configuration.LanguageConfigurationFile
 * JD-Core Version:    0.6.2
 */