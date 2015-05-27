/*     */ package net.nunnerycode.bukkit.itemattributes.managers;
/*     */ 
/*     */ import com.conventnunnery.libraries.config.ConventConfiguration;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import net.nunnerycode.bukkit.itemattributes.ItemAttributesPlugin;
/*     */ import net.nunnerycode.bukkit.itemattributes.api.managers.LanguageManager;
/*     */ import org.bukkit.ChatColor;
/*     */ import org.bukkit.command.CommandSender;
/*     */ import org.bukkit.configuration.ConfigurationSection;
/*     */ import org.bukkit.configuration.file.FileConfiguration;
/*     */ 
/*     */ public final class ItemAttributesLanguageManager
/*     */   implements LanguageManager
/*     */ {
/*     */   private final ItemAttributesPlugin plugin;
/*     */   private final Map<String, String> messages;
/*     */ 
/*     */   public ItemAttributesLanguageManager(ItemAttributesPlugin plugin)
/*     */   {
/*  20 */     this.plugin = plugin;
/*  21 */     this.messages = new HashMap();
/*     */   }
/*     */ 
/*     */   public Map<String, String> getMessages()
/*     */   {
/*  26 */     return this.messages;
/*     */   }
/*     */ 
/*     */   public void sendMessage(CommandSender reciever, String path)
/*     */   {
/*  31 */     String message = getMessage(path);
/*  32 */     if (message == null) {
/*  33 */       return;
/*     */     }
/*  35 */     reciever.sendMessage(message);
/*     */   }
/*     */ 
/*     */   public String getMessage(String path)
/*     */   {
/*  40 */     String message = (String)this.messages.get(path);
/*  41 */     if (message == null) {
/*  42 */       return null;
/*     */     }
/*  44 */     return ChatColor.translateAlternateColorCodes('&', message);
/*     */   }
/*     */ 
/*     */   public void load() {
/*  48 */     ConventConfiguration c = getPlugin().getLanguageYAML();
/*  49 */     c.load();
/*  50 */     FileConfiguration fc = c.getFileConfiguration();
/*  51 */     if (fc.isConfigurationSection("messages"))
/*  52 */       for (String key : fc.getConfigurationSection("messages").getKeys(true))
/*  53 */         if (!fc.getConfigurationSection("messages").isConfigurationSection(key))
/*     */         {
/*  56 */           this.messages.put(key, fc.getConfigurationSection("messages").getString(key, key));
/*     */         }
/*     */   }
/*     */ 
/*     */   public ItemAttributesPlugin getPlugin()
/*     */   {
/*  63 */     return this.plugin;
/*     */   }
/*     */ 
/*     */   public void sendMessage(CommandSender reciever, String path, String[][] arguments)
/*     */   {
/*  69 */     String message = getMessage(path, arguments);
/*  70 */     if (message == null) {
/*  71 */       return;
/*     */     }
/*  73 */     reciever.sendMessage(message);
/*     */   }
/*     */ 
/*     */   public String getMessage(String path, String[][] arguments)
/*     */   {
/*  78 */     String message = (String)this.messages.get(path);
/*  79 */     if (message == null) {
/*  80 */       return null;
/*     */     }
/*  82 */     message = ChatColor.translateAlternateColorCodes('&', message);
/*  83 */     for (String[] argument : arguments) {
/*  84 */       message = message.replaceAll(argument[0], argument[1]);
/*     */     }
/*  86 */     return message;
/*     */   }
/*     */ 
/*     */   public List<String> getStringList(String path)
/*     */   {
/*  91 */     List message = Arrays.asList(((String)this.messages.get(path)).split("^"));
/*  92 */     List strings = new ArrayList();
/*  93 */     for (String s : message) {
/*  94 */       strings.add(ChatColor.translateAlternateColorCodes('&', s));
/*     */     }
/*  96 */     return strings;
/*     */   }
/*     */ 
/*     */   public List<String> getStringList(String path, String[][] arguments)
/*     */   {
/* 101 */     List message = Arrays.asList(((String)this.messages.get(path)).split("^"));
/* 102 */     List strings = new ArrayList();
/* 103 */     for (String s : message) {
/* 104 */       for (String[] argument : arguments) {
/* 105 */         strings.add(ChatColor.translateAlternateColorCodes('&', s.replace(argument[0], argument[1])));
/*     */       }
/*     */     }
/* 108 */     return strings;
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\ItemAttributes.jar
 * Qualified Name:     net.nunnerycode.bukkit.itemattributes.managers.ItemAttributesLanguageManager
 * JD-Core Version:    0.6.2
 */