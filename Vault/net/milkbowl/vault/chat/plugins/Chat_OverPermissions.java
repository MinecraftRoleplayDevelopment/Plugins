/*     */ package net.milkbowl.vault.chat.plugins;
/*     */ 
/*     */ import com.overmc.overpermissions.Group;
/*     */ import com.overmc.overpermissions.GroupManager;
/*     */ import com.overmc.overpermissions.OverPermissions;
/*     */ import com.overmc.overpermissions.OverPermissionsAPI;
/*     */ import com.overmc.overpermissions.PlayerPermissionData;
/*     */ import com.overmc.overpermissions.SQLManager;
/*     */ import java.util.logging.Logger;
/*     */ import net.milkbowl.vault.chat.Chat;
/*     */ import net.milkbowl.vault.permission.Permission;
/*     */ import org.bukkit.Bukkit;
/*     */ import org.bukkit.Server;
/*     */ import org.bukkit.entity.Player;
/*     */ import org.bukkit.event.EventHandler;
/*     */ import org.bukkit.event.EventPriority;
/*     */ import org.bukkit.event.Listener;
/*     */ import org.bukkit.event.server.PluginDisableEvent;
/*     */ import org.bukkit.event.server.PluginEnableEvent;
/*     */ import org.bukkit.plugin.Plugin;
/*     */ import org.bukkit.plugin.PluginDescriptionFile;
/*     */ import org.bukkit.plugin.PluginManager;
/*     */ 
/*     */ public class Chat_OverPermissions extends Chat
/*     */ {
/*     */   private static final String name = "OverPermissions_Chat";
/*     */   private Plugin plugin;
/*     */   private OverPermissions overPerms;
/*     */   private OverPermissionsAPI api;
/*     */ 
/*     */   public Chat_OverPermissions(Plugin plugin, Permission perms)
/*     */   {
/*  43 */     super(perms);
/*  44 */     this.plugin = plugin;
/*     */ 
/*  46 */     plugin.getServer().getPluginManager().registerEvents(new PermissionServerListener(this), plugin);
/*     */ 
/*  48 */     if (this.overPerms == null) {
/*  49 */       Plugin p = plugin.getServer().getPluginManager().getPlugin("OverPermissions");
/*  50 */       if (p != null) {
/*  51 */         this.overPerms = ((OverPermissions)p);
/*  52 */         plugin.getLogger().info(String.format("[%s][Chat] %s hooked.", new Object[] { plugin.getDescription().getName(), "OverPermissions" }));
/*     */       }
/*     */     }
/*  55 */     if ((this.api == null) && (this.overPerms != null))
/*  56 */       this.api = this.overPerms.getAPI();
/*     */   }
/*     */ 
/*     */   public String getName()
/*     */   {
/*  62 */     return "OverPermissions_Chat";
/*     */   }
/*     */ 
/*     */   public boolean isEnabled()
/*     */   {
/*  67 */     return this.overPerms != null;
/*     */   }
/*     */ 
/*     */   public String getPlayerPrefix(String world, String player)
/*     */   {
/*  72 */     return getPlayerInfoString(world, player, "prefix", "");
/*     */   }
/*     */ 
/*     */   public void setPlayerPrefix(String world, String player, String prefix)
/*     */   {
/*  77 */     setPlayerInfoString(world, player, "prefix", prefix);
/*     */   }
/*     */ 
/*     */   public String getPlayerSuffix(String world, String player)
/*     */   {
/*  82 */     return getPlayerInfoString(world, player, "suffix", "");
/*     */   }
/*     */ 
/*     */   public void setPlayerSuffix(String world, String player, String suffix)
/*     */   {
/*  87 */     setPlayerInfoString(world, player, "suffix", suffix);
/*     */   }
/*     */ 
/*     */   public String getGroupPrefix(String world, String group)
/*     */   {
/*  92 */     return getGroupInfoString(world, group, "prefix", "");
/*     */   }
/*     */ 
/*     */   public void setGroupPrefix(String world, String group, String prefix)
/*     */   {
/*  97 */     setGroupInfoString(world, group, "prefix", prefix);
/*     */   }
/*     */ 
/*     */   public String getGroupSuffix(String world, String group)
/*     */   {
/* 102 */     return getGroupInfoString(world, group, "suffix", "");
/*     */   }
/*     */ 
/*     */   public void setGroupSuffix(String world, String group, String suffix)
/*     */   {
/* 107 */     setGroupInfoString(world, group, "prefix", suffix);
/*     */   }
/*     */ 
/*     */   public int getPlayerInfoInteger(String world, String player, String node, int defaultValue)
/*     */   {
/* 112 */     String s = getPlayerInfoString(world, player, node, null);
/* 113 */     if (s == null)
/* 114 */       return defaultValue;
/*     */     try
/*     */     {
/* 117 */       return Integer.valueOf(s).intValue();
/*     */     } catch (NumberFormatException e) {
/*     */     }
/* 120 */     return defaultValue;
/*     */   }
/*     */ 
/*     */   public void setPlayerInfoInteger(String world, String player, String node, int value)
/*     */   {
/* 125 */     setPlayerInfoString(world, player, node, String.valueOf(value));
/*     */   }
/*     */ 
/*     */   public int getGroupInfoInteger(String world, String group, String node, int defaultValue)
/*     */   {
/* 130 */     String s = getGroupInfoString(world, group, node, null);
/* 131 */     if (s == null)
/* 132 */       return defaultValue;
/*     */     try
/*     */     {
/* 135 */       return Integer.valueOf(s).intValue();
/*     */     } catch (NumberFormatException e) {
/*     */     }
/* 138 */     return defaultValue;
/*     */   }
/*     */ 
/*     */   public void setGroupInfoInteger(String world, String group, String node, int value)
/*     */   {
/* 143 */     setGroupInfoString(world, group, node, String.valueOf(value));
/*     */   }
/*     */ 
/*     */   public double getPlayerInfoDouble(String world, String player, String node, double defaultValue)
/*     */   {
/* 148 */     String s = getPlayerInfoString(world, player, node, null);
/* 149 */     if (s == null)
/* 150 */       return defaultValue;
/*     */     try
/*     */     {
/* 153 */       return Double.valueOf(s).doubleValue();
/*     */     } catch (NumberFormatException e) {
/*     */     }
/* 156 */     return defaultValue;
/*     */   }
/*     */ 
/*     */   public void setPlayerInfoDouble(String world, String player, String node, double value)
/*     */   {
/* 161 */     setPlayerInfoString(world, player, node, String.valueOf(value));
/*     */   }
/*     */ 
/*     */   public double getGroupInfoDouble(String world, String group, String node, double defaultValue)
/*     */   {
/* 166 */     String s = getGroupInfoString(world, group, node, null);
/* 167 */     if (s == null)
/* 168 */       return defaultValue;
/*     */     try
/*     */     {
/* 171 */       return Double.valueOf(s).doubleValue();
/*     */     } catch (NumberFormatException e) {
/*     */     }
/* 174 */     return defaultValue;
/*     */   }
/*     */ 
/*     */   public void setGroupInfoDouble(String world, String group, String node, double value)
/*     */   {
/* 179 */     setGroupInfoString(world, group, node, String.valueOf(value));
/*     */   }
/*     */ 
/*     */   public boolean getPlayerInfoBoolean(String world, String player, String node, boolean defaultValue)
/*     */   {
/* 184 */     String s = getPlayerInfoString(world, player, node, null);
/* 185 */     if (s == null) {
/* 186 */       return defaultValue;
/*     */     }
/* 188 */     Boolean val = Boolean.valueOf(s);
/* 189 */     return val != null ? val.booleanValue() : defaultValue;
/*     */   }
/*     */ 
/*     */   public void setPlayerInfoBoolean(String world, String player, String node, boolean value)
/*     */   {
/* 194 */     setPlayerInfoString(world, player, node, String.valueOf(value));
/*     */   }
/*     */ 
/*     */   public boolean getGroupInfoBoolean(String world, String group, String node, boolean defaultValue)
/*     */   {
/* 199 */     String s = getGroupInfoString(world, group, node, null);
/* 200 */     if (s == null) {
/* 201 */       return defaultValue;
/*     */     }
/* 203 */     return Boolean.valueOf(s).booleanValue();
/*     */   }
/*     */ 
/*     */   public void setGroupInfoBoolean(String world, String group, String node, boolean value)
/*     */   {
/* 208 */     setGroupInfoString(world, group, node, String.valueOf(value));
/*     */   }
/*     */ 
/*     */   public String getPlayerInfoString(String world, String playerName, String node, String defaultValue)
/*     */   {
/* 213 */     Player p = Bukkit.getPlayerExact(playerName);
/* 214 */     String ret = null;
/* 215 */     if (p != null) {
/* 216 */       ret = this.overPerms.getPlayerPermissions(p).getStringMeta(node, defaultValue);
/*     */     } else {
/* 218 */       int playerId = this.overPerms.getSQLManager().getPlayerId(playerName);
/* 219 */       int worldId = this.overPerms.getSQLManager().getWorldId(world);
/* 220 */       ret = this.overPerms.getSQLManager().getPlayerMetaValue(playerId, worldId, node);
/*     */     }
/* 222 */     if (ret == null) {
/* 223 */       return defaultValue;
/*     */     }
/* 225 */     return ret;
/*     */   }
/*     */ 
/*     */   public void setPlayerInfoString(String world, String player, String node, String value)
/*     */   {
/* 230 */     Player p = Bukkit.getPlayerExact(player);
/* 231 */     int playerId = this.overPerms.getSQLManager().getPlayerId(player, true);
/* 232 */     int worldId = this.overPerms.getSQLManager().getWorldId(player, false);
/* 233 */     if (worldId < 0)
/* 234 */       this.overPerms.getSQLManager().setGlobalPlayerMeta(playerId, node, value);
/*     */     else {
/* 236 */       this.overPerms.getSQLManager().setPlayerMeta(playerId, worldId, node, value);
/*     */     }
/* 238 */     if (p != null)
/* 239 */       this.overPerms.getPlayerPermissions(p).recalculateMeta();
/*     */   }
/*     */ 
/*     */   public String getGroupInfoString(String world, String groupName, String node, String defaultValue)
/*     */   {
/* 245 */     Group group = this.overPerms.getGroupManager().getGroup(groupName);
/* 246 */     if (group == null) {
/* 247 */       return defaultValue;
/*     */     }
/* 249 */     String value = group.getMeta(node);
/* 250 */     if (value == null) {
/* 251 */       return defaultValue;
/*     */     }
/* 253 */     return value;
/*     */   }
/*     */ 
/*     */   public void setGroupInfoString(String world, String groupName, String node, String value)
/*     */   {
/* 258 */     Group group = this.overPerms.getGroupManager().getGroup(groupName);
/* 259 */     if (group == null) {
/* 260 */       return;
/*     */     }
/* 262 */     group.setMeta(node, value);
/* 263 */     group.recalculatePermissions();
/*     */   }
/*     */ 
/*     */   public class PermissionServerListener implements Listener {
/* 267 */     Chat_OverPermissions chat = null;
/*     */ 
/*     */     public PermissionServerListener(Chat_OverPermissions chat) {
/* 270 */       this.chat = chat;
/*     */     }
/*     */ 
/*     */     @EventHandler(priority=EventPriority.MONITOR)
/*     */     public void onPluginEnable(PluginEnableEvent event) {
/* 275 */       if (this.chat.overPerms == null) {
/* 276 */         Plugin chat = Chat_OverPermissions.this.plugin.getServer().getPluginManager().getPlugin("OverPermissions");
/* 277 */         if (chat != null) {
/* 278 */           this.chat.overPerms = ((OverPermissions)chat);
/* 279 */           Chat_OverPermissions.this.plugin.getLogger().info(String.format("[%s][Chat] %s hooked.", new Object[] { Chat_OverPermissions.this.plugin.getDescription().getName(), Chat_OverPermissions.this.getName() }));
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/*     */     @EventHandler(priority=EventPriority.MONITOR)
/*     */     public void onPluginDisable(PluginDisableEvent event) {
/* 286 */       if ((this.chat.overPerms != null) && (event.getPlugin().getDescription().getName().equals("OverPermissions")))
/*     */       {
/* 288 */         this.chat.overPerms = null;
/* 289 */         Chat_OverPermissions.this.plugin.getLogger().info(String.format("[%s][Chat] %s un-hooked.", new Object[] { Chat_OverPermissions.this.plugin.getDescription().getName(), Chat_OverPermissions.this.getName() }));
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\Vault.jar
 * Qualified Name:     net.milkbowl.vault.chat.plugins.Chat_OverPermissions
 * JD-Core Version:    0.6.2
 */