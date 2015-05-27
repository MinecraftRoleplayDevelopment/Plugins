/*     */ package net.milkbowl.vault.chat.plugins;
/*     */ 
/*     */ import java.util.logging.Logger;
/*     */ import net.ae97.totalpermissions.PermissionManager;
/*     */ import net.ae97.totalpermissions.TotalPermissions;
/*     */ import net.ae97.totalpermissions.permission.PermissionBase;
/*     */ import net.ae97.totalpermissions.permission.PermissionGroup;
/*     */ import net.ae97.totalpermissions.permission.PermissionUser;
/*     */ import net.milkbowl.vault.chat.Chat;
/*     */ import net.milkbowl.vault.permission.Permission;
/*     */ import org.bukkit.Bukkit;
/*     */ import org.bukkit.Server;
/*     */ import org.bukkit.event.EventHandler;
/*     */ import org.bukkit.event.EventPriority;
/*     */ import org.bukkit.event.Listener;
/*     */ import org.bukkit.event.server.PluginDisableEvent;
/*     */ import org.bukkit.event.server.PluginEnableEvent;
/*     */ import org.bukkit.plugin.Plugin;
/*     */ import org.bukkit.plugin.PluginDescriptionFile;
/*     */ import org.bukkit.plugin.PluginManager;
/*     */ 
/*     */ public class Chat_TotalPermissions extends Chat
/*     */ {
/*     */   private final Plugin plugin;
/*     */   private TotalPermissions totalPermissions;
/*  41 */   private final String name = "TotalPermissions-Chat";
/*     */ 
/*     */   public Chat_TotalPermissions(Plugin plugin, Permission perms) {
/*  44 */     super(perms);
/*  45 */     this.plugin = plugin;
/*  46 */     Bukkit.getServer().getPluginManager().registerEvents(new PermissionServerListener(this), plugin);
/*     */ 
/*  48 */     if (this.totalPermissions == null) {
/*  49 */       Plugin chat = plugin.getServer().getPluginManager().getPlugin("TotalPermissions");
/*  50 */       if ((chat != null) && 
/*  51 */         (chat.isEnabled())) {
/*  52 */         this.totalPermissions = ((TotalPermissions)chat);
/*  53 */         plugin.getLogger().info(String.format("[Chat] %s hooked.", new Object[] { "TotalPermissions-Chat" }));
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public String getName()
/*     */   {
/*  96 */     return "TotalPermissions-Chat";
/*     */   }
/*     */ 
/*     */   public boolean isEnabled()
/*     */   {
/* 101 */     return this.totalPermissions == null ? false : this.totalPermissions.isEnabled();
/*     */   }
/*     */ 
/*     */   public String getPlayerPrefix(String world, String player)
/*     */   {
/* 106 */     return getPlayerInfoString(world, player, "prefix", null);
/*     */   }
/*     */ 
/*     */   public void setPlayerPrefix(String world, String player, String prefix)
/*     */   {
/* 111 */     setPlayerInfoString(world, player, "prefix", prefix);
/*     */   }
/*     */ 
/*     */   public String getPlayerSuffix(String world, String player)
/*     */   {
/* 116 */     return getPlayerInfoString(world, player, "suffix", null);
/*     */   }
/*     */ 
/*     */   public void setPlayerSuffix(String world, String player, String suffix)
/*     */   {
/* 121 */     setPlayerInfoString(world, player, "suffix", suffix);
/*     */   }
/*     */ 
/*     */   public String getGroupPrefix(String world, String group)
/*     */   {
/* 126 */     return getGroupInfoString(world, group, "prefix", null);
/*     */   }
/*     */ 
/*     */   public void setGroupPrefix(String world, String group, String prefix)
/*     */   {
/* 131 */     setGroupInfoString(world, group, "prefix", prefix);
/*     */   }
/*     */ 
/*     */   public String getGroupSuffix(String world, String group)
/*     */   {
/* 136 */     return getGroupInfoString(world, group, "suffix", null);
/*     */   }
/*     */ 
/*     */   public void setGroupSuffix(String world, String group, String suffix)
/*     */   {
/* 141 */     setGroupInfoString(world, group, "suffix", suffix);
/*     */   }
/*     */ 
/*     */   public int getPlayerInfoInteger(String world, String player, String node, int defaultValue)
/*     */   {
/* 146 */     Object pre = getPlayerInfo(world, player, node);
/* 147 */     if ((pre instanceof Integer)) {
/* 148 */       return ((Integer)pre).intValue();
/*     */     }
/* 150 */     return defaultValue;
/*     */   }
/*     */ 
/*     */   public void setPlayerInfoInteger(String world, String player, String node, int value)
/*     */   {
/* 155 */     setPlayerInfo(world, player, node, Integer.valueOf(value));
/*     */   }
/*     */ 
/*     */   public int getGroupInfoInteger(String world, String group, String node, int defaultValue)
/*     */   {
/* 160 */     Object pre = getGroupInfo(world, group, node);
/* 161 */     if ((pre instanceof Integer)) {
/* 162 */       return ((Integer)pre).intValue();
/*     */     }
/* 164 */     return defaultValue;
/*     */   }
/*     */ 
/*     */   public void setGroupInfoInteger(String world, String group, String node, int value)
/*     */   {
/* 169 */     setGroupInfo(world, group, node, Integer.valueOf(value));
/*     */   }
/*     */ 
/*     */   public double getPlayerInfoDouble(String world, String player, String node, double defaultValue)
/*     */   {
/* 174 */     Object pre = getPlayerInfo(world, player, node);
/* 175 */     if ((pre instanceof Double)) {
/* 176 */       return ((Double)pre).doubleValue();
/*     */     }
/* 178 */     return defaultValue;
/*     */   }
/*     */ 
/*     */   public void setPlayerInfoDouble(String world, String player, String node, double value)
/*     */   {
/* 183 */     setPlayerInfo(world, player, node, Double.valueOf(value));
/*     */   }
/*     */ 
/*     */   public double getGroupInfoDouble(String world, String group, String node, double defaultValue)
/*     */   {
/* 188 */     Object pre = getGroupInfo(world, group, node);
/* 189 */     if ((pre instanceof Double)) {
/* 190 */       return ((Double)pre).doubleValue();
/*     */     }
/* 192 */     return defaultValue;
/*     */   }
/*     */ 
/*     */   public void setGroupInfoDouble(String world, String group, String node, double value)
/*     */   {
/* 197 */     setGroupInfo(world, group, node, Double.valueOf(value));
/*     */   }
/*     */ 
/*     */   public boolean getPlayerInfoBoolean(String world, String player, String node, boolean defaultValue)
/*     */   {
/* 202 */     Object pre = getPlayerInfo(world, player, node);
/* 203 */     if ((pre instanceof Boolean)) {
/* 204 */       return ((Boolean)pre).booleanValue();
/*     */     }
/* 206 */     return defaultValue;
/*     */   }
/*     */ 
/*     */   public void setPlayerInfoBoolean(String world, String player, String node, boolean value)
/*     */   {
/* 211 */     setPlayerInfo(world, player, node, Boolean.valueOf(value));
/*     */   }
/*     */ 
/*     */   public boolean getGroupInfoBoolean(String world, String group, String node, boolean defaultValue)
/*     */   {
/* 216 */     Object pre = getGroupInfo(world, group, node);
/* 217 */     if ((pre instanceof Boolean)) {
/* 218 */       return ((Boolean)pre).booleanValue();
/*     */     }
/* 220 */     return defaultValue;
/*     */   }
/*     */ 
/*     */   public void setGroupInfoBoolean(String world, String group, String node, boolean value)
/*     */   {
/* 225 */     setGroupInfo(world, group, node, Boolean.valueOf(value));
/*     */   }
/*     */ 
/*     */   public String getPlayerInfoString(String world, String player, String node, String defaultValue)
/*     */   {
/* 230 */     Object pre = getPlayerInfo(world, player, node);
/* 231 */     if ((pre instanceof String)) {
/* 232 */       return (String)pre;
/*     */     }
/* 234 */     return defaultValue;
/*     */   }
/*     */ 
/*     */   public void setPlayerInfoString(String world, String player, String node, String value)
/*     */   {
/* 239 */     setPlayerInfo(world, player, node, value);
/*     */   }
/*     */ 
/*     */   public String getGroupInfoString(String world, String group, String node, String defaultValue)
/*     */   {
/* 244 */     Object pre = getGroupInfo(world, group, node);
/* 245 */     if ((pre instanceof String)) {
/* 246 */       return (String)pre;
/*     */     }
/* 248 */     return defaultValue;
/*     */   }
/*     */ 
/*     */   public void setGroupInfoString(String world, String group, String node, String value)
/*     */   {
/* 253 */     setGroupInfo(world, group, node, value);
/*     */   }
/*     */ 
/*     */   private PermissionUser getUser(String name) {
/* 257 */     PermissionManager manager = this.totalPermissions.getManager();
/* 258 */     PermissionUser user = manager.getUser(name);
/* 259 */     return user;
/*     */   }
/*     */ 
/*     */   private PermissionGroup getGroup(String name) {
/* 263 */     PermissionManager manager = this.totalPermissions.getManager();
/* 264 */     PermissionGroup group = manager.getGroup(name);
/* 265 */     return group;
/*     */   }
/*     */ 
/*     */   private void setPlayerInfo(String world, String player, String node, Object value) {
/* 269 */     PermissionBase base = getUser(player);
/* 270 */     base.setOption(node, value, world);
/*     */   }
/*     */ 
/*     */   private void setGroupInfo(String world, String group, String node, Object value) {
/* 274 */     PermissionBase base = getGroup(group);
/* 275 */     base.setOption(node, value, world);
/*     */   }
/*     */ 
/*     */   private Object getPlayerInfo(String world, String player, String node) {
/* 279 */     PermissionBase base = getUser(player);
/* 280 */     return base.getOption(node);
/*     */   }
/*     */ 
/*     */   private Object getGroupInfo(String world, String group, String node) {
/* 284 */     PermissionBase base = getUser(group);
/* 285 */     return base.getOption(node);
/*     */   }
/*     */ 
/*     */   public class PermissionServerListener
/*     */     implements Listener
/*     */   {
/*  61 */     Chat_TotalPermissions chat = null;
/*     */ 
/*     */     public PermissionServerListener(Chat_TotalPermissions chat) {
/*  64 */       this.chat = chat;
/*     */     }
/*     */ 
/*     */     @EventHandler(priority=EventPriority.MONITOR)
/*     */     public void onPluginEnable(PluginEnableEvent event) {
/*  69 */       if (this.chat.totalPermissions == null) {
/*  70 */         Plugin perms = event.getPlugin();
/*     */ 
/*  72 */         if ((perms != null) && 
/*  73 */           (perms.getDescription().getName().equals("TotalPermissions")) && 
/*  74 */           (perms.isEnabled())) {
/*  75 */           this.chat.totalPermissions = ((TotalPermissions)perms);
/*  76 */           Chat_TotalPermissions.this.plugin.getLogger().info(String.format("[Chat] %s hooked.", new Object[] { this.chat.getName() }));
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/*     */     @EventHandler(priority=EventPriority.MONITOR)
/*     */     public void onPluginDisable(PluginDisableEvent event)
/*     */     {
/*  85 */       if ((this.chat.totalPermissions != null) && 
/*  86 */         (event.getPlugin().getDescription().getName().equals("TotalPermissions"))) {
/*  87 */         this.chat.totalPermissions = null;
/*  88 */         Chat_TotalPermissions.this.plugin.getLogger().info(String.format("[Chat] %s un-hooked.", new Object[] { "TotalPermissions-Chat" }));
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\Vault.jar
 * Qualified Name:     net.milkbowl.vault.chat.plugins.Chat_TotalPermissions
 * JD-Core Version:    0.6.2
 */