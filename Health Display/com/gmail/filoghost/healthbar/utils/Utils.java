/*     */ package com.gmail.filoghost.healthbar.utils;
/*     */ 
/*     */ import com.gmail.filoghost.healthbar.Main;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.io.PrintStream;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.logging.Logger;
/*     */ import java.util.regex.Matcher;
/*     */ import java.util.regex.Pattern;
/*     */ import org.apache.commons.lang.WordUtils;
/*     */ import org.bukkit.Bukkit;
/*     */ import org.bukkit.ChatColor;
/*     */ import org.bukkit.configuration.file.FileConfiguration;
/*     */ import org.bukkit.configuration.file.YamlConfiguration;
/*     */ import org.bukkit.entity.EntityType;
/*     */ import org.bukkit.plugin.Plugin;
/*     */ 
/*     */ public class Utils
/*     */ {
/*     */   public static String colorize(String input)
/*     */   {
/*  29 */     if (input == null) return "";
/*  30 */     return ChatColor.translateAlternateColorCodes('&', input);
/*     */   }
/*     */ 
/*     */   public static String replaceSymbols(String input)
/*     */   {
/*  38 */     if ((input == null) || (input.length() == 0)) return input;
/*     */ 
/*  41 */     return ChatColor.translateAlternateColorCodes('&', input)
/*  42 */       .replace("<3", "❤")
/*  43 */       .replace("[x]", "█")
/*  44 */       .replace("[/]", "█")
/*  45 */       .replace("[*]", "★")
/*  46 */       .replace("[p]", "●")
/*  47 */       .replace("[+]", "◆")
/*  48 */       .replace("[++]", "✦");
/*     */   }
/*     */ 
/*     */   public static FileConfiguration loadFile(String path, Plugin plugin)
/*     */   {
/*  57 */     if (!path.endsWith(".yml")) path = path + ".yml";
/*     */ 
/*  59 */     File file = new File(plugin.getDataFolder(), path);
/*     */ 
/*  61 */     if (!file.exists()) {
/*     */       try {
/*  63 */         plugin.saveResource(path, false);
/*     */       } catch (Exception e) {
/*  65 */         e.printStackTrace();
/*  66 */         System.out.println("-------------------------------------------------");
/*  67 */         System.out.println("[HealthBar] Cannot save " + path + " to disk!");
/*  68 */         System.out.println("-------------------------------------------------");
/*  69 */         return null;
/*     */       }
/*     */     }
/*     */ 
/*  73 */     YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
/*  74 */     return config;
/*     */   }
/*     */ 
/*     */   public static Map<String, String> getTranslationMap(Plugin plugin)
/*     */   {
/*  82 */     FileConfiguration config = loadFile("locale.yml", plugin);
/*     */ 
/*  84 */     Map localeMap = new HashMap();
/*     */ 
/*  86 */     for (EntityType entityType : EntityType.values())
/*     */     {
/*  88 */       if ((entityType.isAlive()) && (!entityType.equals(EntityType.PLAYER)))
/*     */       {
/*  90 */         String name = entityType.toString();
/*     */ 
/*  92 */         if (config.isSet(name)) {
/*  93 */           localeMap.put(name, config.getString(name));
/*     */         } else {
/*  95 */           config.set(name, WordUtils.capitalizeFully(name.replace("_", " ")));
/*  96 */           localeMap.put(name, WordUtils.capitalizeFully(name.replace("_", " ")));
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/*     */     try
/*     */     {
/* 103 */       config.save(new File(plugin.getDataFolder(), "locale.yml"));
/*     */     } catch (IOException e) {
/* 105 */       e.printStackTrace();
/* 106 */       System.out.println("-------------------------------------------------");
/* 107 */       System.out.println("[HealthBar] Cannot save locale.yml to disk!");
/* 108 */       System.out.println("-------------------------------------------------");
/*     */     }
/*     */ 
/* 111 */     return localeMap;
/*     */   }
/*     */ 
/*     */   public static List<EntityType> getTypesFromString(String input) {
/* 115 */     List list = new ArrayList();
/* 116 */     if ((input == null) || (input.length() == 0)) return list;
/* 117 */     String[] split = input.split(",");
/*     */ 
/* 119 */     for (String s : split) {
/* 120 */       EntityType type = getTypeFromString(s);
/* 121 */       if (type == null)
/* 122 */         Main.logger.warning("Cannot find entity type: '" + s + "'. Valid types are listed in locale.yml (The uppercase names, with the underscore)");
/*     */       else {
/* 124 */         list.add(type);
/*     */       }
/*     */     }
/*     */ 
/* 128 */     return list;
/*     */   }
/*     */ 
/*     */   public static int round(double d)
/*     */   {
/* 133 */     double remainder = d - (int)d;
/* 134 */     if (remainder <= 0.5D) {
/* 135 */       return (int)d;
/*     */     }
/* 137 */     return (int)d + 1;
/*     */   }
/*     */ 
/*     */   public static int roundUpPositive(double d)
/*     */   {
/* 142 */     int i = (int)d;
/* 143 */     double remainder = d - i;
/* 144 */     if (remainder > 0.0D) {
/* 145 */       i++;
/*     */     }
/* 147 */     if (i < 0) return 0;
/* 148 */     return i;
/*     */   }
/*     */ 
/*     */   public static int roundUpPositiveWithMax(double d, int max) {
/* 152 */     int result = roundUpPositive(d);
/* 153 */     if (d > max) return max;
/* 154 */     return result;
/*     */   }
/*     */ 
/*     */   public static EntityType getTypeFromString(String s)
/*     */   {
/* 159 */     for (EntityType type : EntityType.values()) {
/* 160 */       if (s.replace(" ", "").replace("_", "").equalsIgnoreCase(type.toString().replace("_", ""))) {
/* 161 */         return type;
/*     */       }
/*     */     }
/* 164 */     return null;
/*     */   }
/*     */ 
/*     */   public static String getBukkitBuild()
/*     */   {
/* 169 */     String version = Bukkit.getVersion();
/* 170 */     Pattern pattern = Pattern.compile("(b)([0-9]+)(jnks)");
/* 171 */     Matcher matcher = pattern.matcher(version);
/*     */ 
/* 173 */     if (matcher.find()) {
/* 174 */       return matcher.group(2);
/*     */     }
/*     */ 
/* 177 */     return null;
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\HealthBar.jar
 * Qualified Name:     com.gmail.filoghost.healthbar.utils.Utils
 * JD-Core Version:    0.6.2
 */