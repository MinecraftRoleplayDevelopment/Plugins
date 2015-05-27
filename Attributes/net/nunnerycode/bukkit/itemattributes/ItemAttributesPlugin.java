/*     */ package net.nunnerycode.bukkit.itemattributes;
/*     */ 
/*     */ import com.conventnunnery.libraries.config.CommentedConventYamlConfiguration;
/*     */ import com.conventnunnery.libraries.config.ConventYamlConfigurationOptions;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.logging.Level;
/*     */ import java.util.logging.Logger;
/*     */ import net.nunnerycode.bukkit.itemattributes.api.ItemAttributes;
/*     */ import net.nunnerycode.bukkit.itemattributes.api.attributes.Attribute;
/*     */ import net.nunnerycode.bukkit.itemattributes.api.attributes.AttributeHandler;
/*     */ import net.nunnerycode.bukkit.itemattributes.api.commands.ItemAttributesCommand;
/*     */ import net.nunnerycode.bukkit.itemattributes.api.managers.LanguageManager;
/*     */ import net.nunnerycode.bukkit.itemattributes.api.managers.PermissionsManager;
/*     */ import net.nunnerycode.bukkit.itemattributes.api.managers.SettingsManager;
/*     */ import net.nunnerycode.bukkit.itemattributes.api.tasks.HealthUpdateTask;
/*     */ import net.nunnerycode.bukkit.itemattributes.attributes.ItemAttributeHandler;
/*     */ import net.nunnerycode.bukkit.itemattributes.commands.ItemAttributesCommands;
/*     */ import net.nunnerycode.bukkit.itemattributes.listeners.ItemAttributesCoreListener;
/*     */ import net.nunnerycode.bukkit.itemattributes.managers.ItemAttributesLanguageManager;
/*     */ import net.nunnerycode.bukkit.itemattributes.managers.ItemAttributesPermissionsManager;
/*     */ import net.nunnerycode.bukkit.itemattributes.managers.ItemAttributesSettingsManager;
/*     */ import net.nunnerycode.bukkit.itemattributes.tasks.ItemAttributesHealthUpdateTask;
/*     */ import net.nunnerycode.java.libraries.cannonball.DebugPrinter;
/*     */ import org.bukkit.Bukkit;
/*     */ import org.bukkit.Server;
/*     */ import org.bukkit.configuration.file.YamlConfiguration;
/*     */ import org.bukkit.plugin.PluginDescriptionFile;
/*     */ import org.bukkit.plugin.PluginManager;
/*     */ import org.bukkit.plugin.java.JavaPlugin;
/*     */ 
/*     */ public final class ItemAttributesPlugin extends JavaPlugin
/*     */   implements ItemAttributes
/*     */ {
/*     */   private DebugPrinter debugPrinter;
/*     */   private CommentedConventYamlConfiguration configYAML;
/*     */   private CommentedConventYamlConfiguration languageYAML;
/*     */   private CommentedConventYamlConfiguration permissionsYAML;
/*     */   private ItemAttributesLanguageManager itemAttributesLanguageManager;
/*     */   private ItemAttributesSettingsManager itemAttributesSettingsManager;
/*     */   private ItemAttributesPermissionsManager itemAttributesPermissionsManager;
/*     */   private ItemAttributesCoreListener itemAttributesCoreListener;
/*     */   private HealthUpdateTask itemAttributesHealthUpdateTask;
/*     */   private ItemAttributesCommand itemAttributesCommands;
/*     */   private ItemAttributeHandler itemAttributeHandler;
/*     */ 
/*     */   public ItemAttributesCoreListener getCoreListener()
/*     */   {
/*  45 */     return this.itemAttributesCoreListener;
/*     */   }
/*     */ 
/*     */   public DebugPrinter getDebugPrinter()
/*     */   {
/*  50 */     return this.debugPrinter;
/*     */   }
/*     */ 
/*     */   public CommentedConventYamlConfiguration getConfigYAML()
/*     */   {
/*  55 */     return this.configYAML;
/*     */   }
/*     */ 
/*     */   public CommentedConventYamlConfiguration getLanguageYAML()
/*     */   {
/*  60 */     return this.languageYAML;
/*     */   }
/*     */ 
/*     */   public LanguageManager getLanguageManager()
/*     */   {
/*  65 */     return this.itemAttributesLanguageManager;
/*     */   }
/*     */ 
/*     */   public SettingsManager getSettingsManager()
/*     */   {
/*  70 */     return this.itemAttributesSettingsManager;
/*     */   }
/*     */ 
/*     */   public HealthUpdateTask getHealthUpdateTask()
/*     */   {
/*  75 */     return this.itemAttributesHealthUpdateTask;
/*     */   }
/*     */ 
/*     */   public ItemAttributesCommand getItemAttributesCommand()
/*     */   {
/*  80 */     return this.itemAttributesCommands;
/*     */   }
/*     */ 
/*     */   public AttributeHandler getAttributeHandler()
/*     */   {
/*  85 */     return this.itemAttributeHandler;
/*     */   }
/*     */ 
/*     */   public void onDisable()
/*     */   {
/*  90 */     this.itemAttributesSettingsManager.save();
/*  91 */     this.debugPrinter.debug(Level.INFO, new String[] { "v" + getDescription().getVersion() + " disabled" });
/*     */   }
/*     */ 
/*     */   public void onEnable()
/*     */   {
/*  96 */     unpackConfigurationFiles(new String[] { "config.yml", "language.yml", "permissions.yml" }, false);
/*     */ 
/*  98 */     this.configYAML = new CommentedConventYamlConfiguration(new File(getDataFolder(), "config.yml"), YamlConfiguration.loadConfiguration(getResource("config.yml")).getString("version"));
/*     */ 
/* 100 */     this.configYAML.options().updateOnLoad(true);
/* 101 */     this.configYAML.options().backupOnUpdate(true);
/* 102 */     this.languageYAML = new CommentedConventYamlConfiguration(new File(getDataFolder(), "language.yml"), YamlConfiguration.loadConfiguration(getResource("language.yml")).getString("version"));
/*     */ 
/* 104 */     this.languageYAML.options().updateOnLoad(true);
/* 105 */     this.languageYAML.options().backupOnUpdate(true);
/* 106 */     this.permissionsYAML = new CommentedConventYamlConfiguration(new File(getDataFolder(), "permissions.yml"), YamlConfiguration.loadConfiguration(getResource("permissions.yml")).getString("version"));
/*     */ 
/* 108 */     this.permissionsYAML.options().updateOnLoad(true);
/* 109 */     this.permissionsYAML.options().backupOnUpdate(true);
/*     */ 
/* 111 */     this.debugPrinter = new DebugPrinter(getDataFolder().getPath() + "/log/", "debug.log");
/*     */ 
/* 113 */     this.itemAttributesSettingsManager = new ItemAttributesSettingsManager(this);
/* 114 */     this.itemAttributesSettingsManager.load();
/*     */ 
/* 116 */     this.itemAttributesLanguageManager = new ItemAttributesLanguageManager(this);
/* 117 */     this.itemAttributesLanguageManager.load();
/*     */ 
/* 119 */     this.itemAttributesPermissionsManager = new ItemAttributesPermissionsManager(this);
/* 120 */     this.itemAttributesPermissionsManager.load();
/*     */ 
/* 122 */     this.itemAttributeHandler = new ItemAttributeHandler(this);
/*     */ 
/* 124 */     this.itemAttributesCoreListener = new ItemAttributesCoreListener(this);
/*     */ 
/* 126 */     if (this.itemAttributesSettingsManager.getAttribute("HEALTH").isEnabled()) {
/* 127 */       this.itemAttributesHealthUpdateTask = new ItemAttributesHealthUpdateTask(this);
/*     */     }
/*     */ 
/* 130 */     Bukkit.getServer().getPluginManager().registerEvents(this.itemAttributesCoreListener, this);
/*     */ 
/* 132 */     this.itemAttributesCommands = new ItemAttributesCommands(this);
/*     */ 
/* 134 */     List loadedAttributes = new ArrayList();
/* 135 */     for (Attribute attribute : this.itemAttributesSettingsManager.getLoadedAttributes()) {
/* 136 */       loadedAttributes.add(attribute.getName());
/*     */     }
/*     */ 
/* 139 */     this.debugPrinter.debug(Level.INFO, new String[] { "Loaded attributes: " + loadedAttributes.toString() });
/* 140 */     this.debugPrinter.debug(Level.INFO, new String[] { "v" + getDescription().getVersion() + " enabled" });
/*     */   }
/*     */ 
/*     */   private void unpackConfigurationFiles(String[] configurationFiles, boolean overwrite) {
/* 144 */     for (String s : configurationFiles)
/* 145 */       if (getResource(s) != null)
/*     */       {
/* 148 */         YamlConfiguration yc = YamlConfiguration.loadConfiguration(getResource(s));
/*     */         try {
/* 150 */           File f = new File(getDataFolder(), s);
/* 151 */           if (!f.exists()) {
/* 152 */             yc.save(new File(getDataFolder(), s));
/*     */           }
/* 155 */           else if (overwrite)
/* 156 */             yc.save(new File(getDataFolder(), s));
/*     */         }
/*     */         catch (IOException e) {
/* 159 */           getLogger().warning("Could not unpack " + s);
/*     */         }
/*     */       }
/*     */   }
/*     */ 
/*     */   public CommentedConventYamlConfiguration getPermissionsYAML()
/*     */   {
/* 166 */     return this.permissionsYAML;
/*     */   }
/*     */ 
/*     */   public PermissionsManager getPermissionsManager()
/*     */   {
/* 171 */     return this.itemAttributesPermissionsManager;
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\ItemAttributes.jar
 * Qualified Name:     net.nunnerycode.bukkit.itemattributes.ItemAttributesPlugin
 * JD-Core Version:    0.6.2
 */