/*     */ package net.milkbowl.vault.chat.plugins;
/*     */ 
/*     */ import de.hydrox.bukkit.DroxPerms.DroxPerms;
/*     */ import de.hydrox.bukkit.DroxPerms.DroxPermsAPI;
/*     */ import java.util.logging.Logger;
/*     */ import net.milkbowl.vault.chat.Chat;
/*     */ import net.milkbowl.vault.permission.Permission;
/*     */ import org.bukkit.Bukkit;
/*     */ import org.bukkit.Server;
/*     */ import org.bukkit.event.EventHandler;
/*     */ import org.bukkit.event.EventPriority;
/*     */ import org.bukkit.event.Listener;
/*     */ import org.bukkit.event.server.PluginEnableEvent;
/*     */ import org.bukkit.plugin.Plugin;
/*     */ import org.bukkit.plugin.PluginDescriptionFile;
/*     */ import org.bukkit.plugin.PluginManager;
/*     */ 
/*     */ public class Chat_DroxPerms extends Chat
/*     */ {
/*  19 */   private static final Logger log = Logger.getLogger("Minecraft");
/*     */ 
/*  21 */   private final String name = "DroxPerms";
/*     */   private Plugin plugin;
/*     */   private DroxPermsAPI API;
/*     */ 
/*     */   public Chat_DroxPerms(Plugin plugin, Permission perms)
/*     */   {
/*  26 */     super(perms);
/*  27 */     this.plugin = plugin;
/*     */ 
/*  30 */     if (this.API == null) {
/*  31 */       DroxPerms p = (DroxPerms)plugin.getServer().getPluginManager().getPlugin("DroxPerms");
/*  32 */       if (p != null) {
/*  33 */         this.API = p.getAPI();
/*  34 */         log.info(String.format("[%s][Chat] %s hooked.", new Object[] { plugin.getDescription().getName(), "DroxPerms" }));
/*     */       }
/*     */     }
/*  37 */     Bukkit.getServer().getPluginManager().registerEvents(new PermissionServerListener(), plugin);
/*     */   }
/*     */ 
/*     */   public String getName()
/*     */   {
/*  55 */     getClass(); return "DroxPerms";
/*     */   }
/*     */ 
/*     */   public boolean isEnabled()
/*     */   {
/*  60 */     return true;
/*     */   }
/*     */ 
/*     */   public String getPlayerPrefix(String world, String player)
/*     */   {
/*  65 */     String prefix = this.API.getPlayerInfo(player, "prefix");
/*  66 */     if (prefix == null) {
/*  67 */       String prigroup = this.API.getPlayerGroup(player);
/*  68 */       prefix = this.API.getGroupInfo(prigroup, "prefix");
/*     */     }
/*  70 */     return prefix;
/*     */   }
/*     */ 
/*     */   public void setPlayerPrefix(String world, String player, String prefix)
/*     */   {
/*  75 */     this.API.setPlayerInfo(player, "prefix", prefix);
/*     */   }
/*     */ 
/*     */   public String getPlayerSuffix(String world, String player)
/*     */   {
/*  80 */     return this.API.getPlayerInfo(player, "suffix");
/*     */   }
/*     */ 
/*     */   public void setPlayerSuffix(String world, String player, String suffix)
/*     */   {
/*  85 */     this.API.setPlayerInfo(player, "suffix", suffix);
/*     */   }
/*     */ 
/*     */   public String getGroupPrefix(String world, String group)
/*     */   {
/*  90 */     return this.API.getGroupInfo(group, "prefix");
/*     */   }
/*     */ 
/*     */   public void setGroupPrefix(String world, String group, String prefix)
/*     */   {
/*  95 */     this.API.setGroupInfo(group, "prefix", prefix);
/*     */   }
/*     */ 
/*     */   public String getGroupSuffix(String world, String group)
/*     */   {
/* 100 */     return this.API.getGroupInfo(group, "suffix");
/*     */   }
/*     */ 
/*     */   public void setGroupSuffix(String world, String group, String suffix)
/*     */   {
/* 105 */     this.API.setGroupInfo(group, "suffix", suffix);
/*     */   }
/*     */ 
/*     */   public int getPlayerInfoInteger(String world, String player, String node, int defaultValue)
/*     */   {
/* 110 */     String s = getPlayerInfoString(world, player, node, null);
/* 111 */     if (s == null) {
/* 112 */       return defaultValue;
/*     */     }
/*     */     try
/*     */     {
/* 116 */       return Integer.valueOf(s).intValue(); } catch (NumberFormatException e) {
/*     */     }
/* 118 */     return defaultValue;
/*     */   }
/*     */ 
/*     */   public void setPlayerInfoInteger(String world, String player, String node, int value)
/*     */   {
/* 124 */     this.API.setPlayerInfo(player, node, String.valueOf(value));
/*     */   }
/*     */ 
/*     */   public int getGroupInfoInteger(String world, String group, String node, int defaultValue)
/*     */   {
/* 129 */     String s = getGroupInfoString(world, group, node, null);
/* 130 */     if (s == null) {
/* 131 */       return defaultValue;
/*     */     }
/*     */     try
/*     */     {
/* 135 */       return Integer.valueOf(s).intValue(); } catch (NumberFormatException e) {
/*     */     }
/* 137 */     return defaultValue;
/*     */   }
/*     */ 
/*     */   public void setGroupInfoInteger(String world, String group, String node, int value)
/*     */   {
/* 143 */     this.API.setGroupInfo(group, node, String.valueOf(value));
/*     */   }
/*     */ 
/*     */   public double getPlayerInfoDouble(String world, String player, String node, double defaultValue)
/*     */   {
/* 148 */     String s = getPlayerInfoString(world, player, node, null);
/* 149 */     if (s == null) {
/* 150 */       return defaultValue;
/*     */     }
/*     */     try
/*     */     {
/* 154 */       return Double.valueOf(s).doubleValue(); } catch (NumberFormatException e) {
/*     */     }
/* 156 */     return defaultValue;
/*     */   }
/*     */ 
/*     */   public void setPlayerInfoDouble(String world, String player, String node, double value)
/*     */   {
/* 162 */     this.API.setPlayerInfo(player, node, String.valueOf(value));
/*     */   }
/*     */ 
/*     */   public double getGroupInfoDouble(String world, String group, String node, double defaultValue)
/*     */   {
/* 167 */     String s = getGroupInfoString(world, group, node, null);
/* 168 */     if (s == null) {
/* 169 */       return defaultValue;
/*     */     }
/*     */     try
/*     */     {
/* 173 */       return Double.valueOf(s).doubleValue(); } catch (NumberFormatException e) {
/*     */     }
/* 175 */     return defaultValue;
/*     */   }
/*     */ 
/*     */   public void setGroupInfoDouble(String world, String group, String node, double value)
/*     */   {
/* 181 */     this.API.setGroupInfo(group, node, String.valueOf(value));
/*     */   }
/*     */ 
/*     */   public boolean getPlayerInfoBoolean(String world, String player, String node, boolean defaultValue)
/*     */   {
/* 186 */     String s = getPlayerInfoString(world, player, node, null);
/* 187 */     if (s == null) {
/* 188 */       return defaultValue;
/*     */     }
/* 190 */     Boolean val = Boolean.valueOf(s);
/* 191 */     return val != null ? val.booleanValue() : defaultValue;
/*     */   }
/*     */ 
/*     */   public void setPlayerInfoBoolean(String world, String player, String node, boolean value)
/*     */   {
/* 197 */     this.API.setPlayerInfo(player, node, String.valueOf(value));
/*     */   }
/*     */ 
/*     */   public boolean getGroupInfoBoolean(String world, String group, String node, boolean defaultValue)
/*     */   {
/* 202 */     String s = getGroupInfoString(world, group, node, null);
/* 203 */     if (s == null) {
/* 204 */       return defaultValue;
/*     */     }
/* 206 */     Boolean val = Boolean.valueOf(s);
/* 207 */     return val != null ? val.booleanValue() : defaultValue;
/*     */   }
/*     */ 
/*     */   public void setGroupInfoBoolean(String world, String group, String node, boolean value)
/*     */   {
/* 213 */     this.API.setGroupInfo(group, node, String.valueOf(value));
/*     */   }
/*     */ 
/*     */   public String getPlayerInfoString(String world, String player, String node, String defaultValue)
/*     */   {
/* 218 */     String val = this.API.getPlayerInfo(player, node);
/* 219 */     return val != null ? val : defaultValue;
/*     */   }
/*     */ 
/*     */   public void setPlayerInfoString(String world, String player, String node, String value)
/*     */   {
/* 224 */     this.API.setPlayerInfo(player, node, value);
/*     */   }
/*     */ 
/*     */   public String getGroupInfoString(String world, String group, String node, String defaultValue)
/*     */   {
/* 229 */     String val = this.API.getGroupInfo(group, node);
/* 230 */     return val != null ? val : defaultValue;
/*     */   }
/*     */ 
/*     */   public void setGroupInfoString(String world, String group, String node, String value)
/*     */   {
/* 235 */     this.API.setGroupInfo(group, node, value);
/*     */   }
/*     */ 
/*     */   public class PermissionServerListener
/*     */     implements Listener
/*     */   {
/*     */     public PermissionServerListener()
/*     */     {
/*     */     }
/*     */ 
/*     */     @EventHandler(priority=EventPriority.MONITOR)
/*     */     public void onPluginEnable(PluginEnableEvent event)
/*     */     {
/*  43 */       if (Chat_DroxPerms.this.API == null) {
/*  44 */         Plugin permPlugin = event.getPlugin();
/*  45 */         if (permPlugin.getDescription().getName().equals("DroxPerms")) {
/*  46 */           Chat_DroxPerms.this.API = ((DroxPerms)permPlugin).getAPI();
/*  47 */           Chat_DroxPerms.log.info(String.format("[%s][Chat] %s hooked.", new Object[] { Chat_DroxPerms.this.plugin.getDescription().getName(), "DroxPerms" }));
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\Vault.jar
 * Qualified Name:     net.milkbowl.vault.chat.plugins.Chat_DroxPerms
 * JD-Core Version:    0.6.2
 */