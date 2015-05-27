/*     */ package net.milkbowl.vault.chat.plugins;
/*     */ 
/*     */ import java.util.logging.Logger;
/*     */ import net.milkbowl.vault.chat.Chat;
/*     */ import net.milkbowl.vault.permission.Permission;
/*     */ import org.anjocaido.groupmanager.GroupManager;
/*     */ import org.anjocaido.groupmanager.data.Group;
/*     */ import org.anjocaido.groupmanager.data.GroupVariables;
/*     */ import org.anjocaido.groupmanager.data.User;
/*     */ import org.anjocaido.groupmanager.data.UserVariables;
/*     */ import org.anjocaido.groupmanager.dataholder.OverloadedWorldHolder;
/*     */ import org.anjocaido.groupmanager.dataholder.worlds.WorldsHolder;
/*     */ import org.anjocaido.groupmanager.permissions.AnjoPermissionsHandler;
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
/*     */ public class Chat_GroupManager extends Chat
/*     */ {
/*  38 */   private static final Logger log = Logger.getLogger("Minecraft");
/*  39 */   private final String name = "GroupManager - Chat";
/*  40 */   private Plugin plugin = null;
/*     */   private GroupManager groupManager;
/*     */ 
/*     */   public Chat_GroupManager(Plugin plugin, Permission perms)
/*     */   {
/*  44 */     super(perms);
/*  45 */     this.plugin = plugin;
/*  46 */     Bukkit.getServer().getPluginManager().registerEvents(new PermissionServerListener(this), plugin);
/*     */ 
/*  49 */     if (this.groupManager == null) {
/*  50 */       Plugin chat = plugin.getServer().getPluginManager().getPlugin("GroupManager");
/*  51 */       if ((chat != null) && 
/*  52 */         (chat.isEnabled())) {
/*  53 */         this.groupManager = ((GroupManager)chat);
/*  54 */         log.info(String.format("[%s][Chat] %s hooked.", new Object[] { plugin.getDescription().getName(), "GroupManager - Chat" }));
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public String getName()
/*     */   {
/*  93 */     getClass(); return "GroupManager - Chat";
/*     */   }
/*     */ 
/*     */   public boolean isEnabled()
/*     */   {
/*  98 */     if (this.groupManager == null) {
/*  99 */       return false;
/*     */     }
/* 101 */     return this.groupManager.isEnabled();
/*     */   }
/*     */ 
/*     */   public int getPlayerInfoInteger(String worldName, String playerName, String node, int defaultValue)
/*     */   {
/*     */     AnjoPermissionsHandler handler;
/*     */     AnjoPermissionsHandler handler;
/* 108 */     if (worldName == null)
/* 109 */       handler = this.groupManager.getWorldsHolder().getWorldPermissionsByPlayerName(playerName);
/*     */     else {
/* 111 */       handler = this.groupManager.getWorldsHolder().getWorldPermissions(worldName);
/*     */     }
/* 113 */     if (handler == null) {
/* 114 */       return defaultValue;
/*     */     }
/* 116 */     Integer val = Integer.valueOf(handler.getUserPermissionInteger(playerName, node));
/* 117 */     return val != null ? val.intValue() : defaultValue;
/*     */   }
/*     */ 
/*     */   public void setPlayerInfoInteger(String worldName, String playerName, String node, int value)
/*     */   {
/* 122 */     setPlayerValue(worldName, playerName, node, Integer.valueOf(value));
/*     */   }
/*     */ 
/*     */   public int getGroupInfoInteger(String worldName, String groupName, String node, int defaultValue)
/*     */   {
/*     */     AnjoPermissionsHandler handler;
/*     */     AnjoPermissionsHandler handler;
/* 128 */     if (worldName == null)
/* 129 */       handler = this.groupManager.getWorldsHolder().getDefaultWorld().getPermissionsHandler();
/*     */     else {
/* 131 */       handler = this.groupManager.getWorldsHolder().getWorldPermissions(worldName);
/*     */     }
/* 133 */     if (handler == null) {
/* 134 */       return defaultValue;
/*     */     }
/* 136 */     Integer val = Integer.valueOf(handler.getGroupPermissionInteger(groupName, node));
/* 137 */     return val != null ? val.intValue() : defaultValue;
/*     */   }
/*     */ 
/*     */   public void setGroupInfoInteger(String worldName, String groupName, String node, int value)
/*     */   {
/* 142 */     setGroupValue(worldName, groupName, node, Integer.valueOf(value));
/*     */   }
/*     */ 
/*     */   public double getPlayerInfoDouble(String worldName, String playerName, String node, double defaultValue)
/*     */   {
/*     */     AnjoPermissionsHandler handler;
/*     */     AnjoPermissionsHandler handler;
/* 148 */     if (worldName == null)
/* 149 */       handler = this.groupManager.getWorldsHolder().getWorldPermissionsByPlayerName(playerName);
/*     */     else {
/* 151 */       handler = this.groupManager.getWorldsHolder().getWorldPermissions(worldName);
/*     */     }
/* 153 */     if (handler == null) {
/* 154 */       return defaultValue;
/*     */     }
/* 156 */     Double val = Double.valueOf(handler.getUserPermissionDouble(playerName, node));
/* 157 */     return val != null ? val.doubleValue() : defaultValue;
/*     */   }
/*     */ 
/*     */   public void setPlayerInfoDouble(String worldName, String playerName, String node, double value)
/*     */   {
/* 162 */     setPlayerValue(worldName, playerName, node, Double.valueOf(value));
/*     */   }
/*     */ 
/*     */   public double getGroupInfoDouble(String worldName, String groupName, String node, double defaultValue)
/*     */   {
/*     */     AnjoPermissionsHandler handler;
/*     */     AnjoPermissionsHandler handler;
/* 168 */     if (worldName == null)
/* 169 */       handler = this.groupManager.getWorldsHolder().getDefaultWorld().getPermissionsHandler();
/*     */     else {
/* 171 */       handler = this.groupManager.getWorldsHolder().getWorldPermissions(worldName);
/*     */     }
/* 173 */     if (handler == null) {
/* 174 */       return defaultValue;
/*     */     }
/* 176 */     Double val = Double.valueOf(handler.getGroupPermissionDouble(groupName, node));
/* 177 */     return val != null ? val.doubleValue() : defaultValue;
/*     */   }
/*     */ 
/*     */   public void setGroupInfoDouble(String worldName, String groupName, String node, double value)
/*     */   {
/* 182 */     setGroupValue(worldName, groupName, node, Double.valueOf(value));
/*     */   }
/*     */ 
/*     */   public boolean getPlayerInfoBoolean(String worldName, String playerName, String node, boolean defaultValue)
/*     */   {
/*     */     AnjoPermissionsHandler handler;
/*     */     AnjoPermissionsHandler handler;
/* 188 */     if (worldName == null)
/* 189 */       handler = this.groupManager.getWorldsHolder().getWorldPermissionsByPlayerName(playerName);
/*     */     else {
/* 191 */       handler = this.groupManager.getWorldsHolder().getWorldPermissions(worldName);
/*     */     }
/* 193 */     if (handler == null) {
/* 194 */       return defaultValue;
/*     */     }
/* 196 */     Boolean val = Boolean.valueOf(handler.getUserPermissionBoolean(playerName, node));
/* 197 */     return val != null ? val.booleanValue() : defaultValue;
/*     */   }
/*     */ 
/*     */   public void setPlayerInfoBoolean(String worldName, String playerName, String node, boolean value)
/*     */   {
/* 202 */     setPlayerValue(worldName, playerName, node, Boolean.valueOf(value));
/*     */   }
/*     */ 
/*     */   public boolean getGroupInfoBoolean(String worldName, String groupName, String node, boolean defaultValue)
/*     */   {
/*     */     AnjoPermissionsHandler handler;
/*     */     AnjoPermissionsHandler handler;
/* 208 */     if (worldName == null)
/* 209 */       handler = this.groupManager.getWorldsHolder().getDefaultWorld().getPermissionsHandler();
/*     */     else {
/* 211 */       handler = this.groupManager.getWorldsHolder().getWorldPermissions(worldName);
/*     */     }
/* 213 */     if (handler == null) {
/* 214 */       return defaultValue;
/*     */     }
/* 216 */     Boolean val = Boolean.valueOf(handler.getGroupPermissionBoolean(groupName, node));
/* 217 */     return val != null ? val.booleanValue() : defaultValue;
/*     */   }
/*     */ 
/*     */   public void setGroupInfoBoolean(String worldName, String groupName, String node, boolean value)
/*     */   {
/* 222 */     setGroupValue(worldName, groupName, node, Boolean.valueOf(value));
/*     */   }
/*     */ 
/*     */   public String getPlayerInfoString(String worldName, String playerName, String node, String defaultValue)
/*     */   {
/*     */     AnjoPermissionsHandler handler;
/*     */     AnjoPermissionsHandler handler;
/* 228 */     if (worldName == null)
/* 229 */       handler = this.groupManager.getWorldsHolder().getWorldPermissionsByPlayerName(playerName);
/*     */     else {
/* 231 */       handler = this.groupManager.getWorldsHolder().getWorldPermissions(worldName);
/*     */     }
/* 233 */     if (handler == null) {
/* 234 */       return defaultValue;
/*     */     }
/* 236 */     String val = handler.getUserPermissionString(playerName, node);
/* 237 */     return val != null ? val : defaultValue;
/*     */   }
/*     */ 
/*     */   public void setPlayerInfoString(String worldName, String playerName, String node, String value)
/*     */   {
/* 242 */     setPlayerValue(worldName, playerName, node, value);
/*     */   }
/*     */ 
/*     */   public String getGroupInfoString(String worldName, String groupName, String node, String defaultValue)
/*     */   {
/*     */     AnjoPermissionsHandler handler;
/*     */     AnjoPermissionsHandler handler;
/* 248 */     if (worldName == null)
/* 249 */       handler = this.groupManager.getWorldsHolder().getDefaultWorld().getPermissionsHandler();
/*     */     else {
/* 251 */       handler = this.groupManager.getWorldsHolder().getWorldPermissions(worldName);
/*     */     }
/* 253 */     if (handler == null) {
/* 254 */       return defaultValue;
/*     */     }
/* 256 */     String val = handler.getGroupPermissionString(groupName, node);
/* 257 */     return val != null ? val : defaultValue;
/*     */   }
/*     */ 
/*     */   public void setGroupInfoString(String worldName, String groupName, String node, String value)
/*     */   {
/* 262 */     setGroupValue(worldName, groupName, node, value);
/*     */   }
/*     */ 
/*     */   public String getPlayerPrefix(String worldName, String playerName)
/*     */   {
/*     */     AnjoPermissionsHandler handler;
/*     */     AnjoPermissionsHandler handler;
/* 268 */     if (worldName == null)
/* 269 */       handler = this.groupManager.getWorldsHolder().getWorldPermissionsByPlayerName(playerName);
/*     */     else {
/* 271 */       handler = this.groupManager.getWorldsHolder().getWorldPermissions(worldName);
/*     */     }
/* 273 */     if (handler == null) {
/* 274 */       return "";
/*     */     }
/* 276 */     return handler.getUserPrefix(playerName);
/*     */   }
/*     */ 
/*     */   public String getPlayerSuffix(String worldName, String playerName)
/*     */   {
/*     */     AnjoPermissionsHandler handler;
/*     */     AnjoPermissionsHandler handler;
/* 282 */     if (worldName == null)
/* 283 */       handler = this.groupManager.getWorldsHolder().getWorldPermissionsByPlayerName(playerName);
/*     */     else {
/* 285 */       handler = this.groupManager.getWorldsHolder().getWorldPermissions(worldName);
/*     */     }
/* 287 */     if (handler == null) {
/* 288 */       return "";
/*     */     }
/* 290 */     return handler.getUserSuffix(playerName);
/*     */   }
/*     */ 
/*     */   public void setPlayerSuffix(String worldName, String player, String suffix)
/*     */   {
/* 295 */     setPlayerInfoString(worldName, player, "suffix", suffix);
/*     */   }
/*     */ 
/*     */   public void setPlayerPrefix(String worldName, String player, String prefix)
/*     */   {
/* 300 */     setPlayerInfoString(worldName, player, "prefix", prefix);
/*     */   }
/*     */ 
/*     */   public String getGroupPrefix(String worldName, String group)
/*     */   {
/* 305 */     return getGroupInfoString(worldName, group, "prefix", "");
/*     */   }
/*     */ 
/*     */   public void setGroupPrefix(String worldName, String group, String prefix)
/*     */   {
/* 310 */     setGroupInfoString(worldName, group, "prefix", prefix);
/*     */   }
/*     */ 
/*     */   public String getGroupSuffix(String worldName, String group)
/*     */   {
/* 315 */     return getGroupInfoString(worldName, group, "suffix", "");
/*     */   }
/*     */ 
/*     */   public void setGroupSuffix(String worldName, String group, String suffix)
/*     */   {
/* 320 */     setGroupInfoString(worldName, group, "suffix", suffix);
/*     */   }
/*     */ 
/*     */   public String getPrimaryGroup(String worldName, String playerName)
/*     */   {
/*     */     AnjoPermissionsHandler handler;
/*     */     AnjoPermissionsHandler handler;
/* 326 */     if (worldName == null)
/* 327 */       handler = this.groupManager.getWorldsHolder().getWorldPermissionsByPlayerName(playerName);
/*     */     else {
/* 329 */       handler = this.groupManager.getWorldsHolder().getWorldPermissions(worldName);
/*     */     }
/* 331 */     return handler.getGroup(playerName);
/*     */   }
/*     */ 
/*     */   private void setPlayerValue(String worldName, String playerName, String node, Object value)
/*     */   {
/*     */     OverloadedWorldHolder owh;
/*     */     OverloadedWorldHolder owh;
/* 336 */     if (worldName == null)
/* 337 */       owh = this.groupManager.getWorldsHolder().getWorldDataByPlayerName(playerName);
/*     */     else {
/* 339 */       owh = this.groupManager.getWorldsHolder().getWorldData(worldName);
/*     */     }
/* 341 */     if (owh == null) {
/* 342 */       return;
/*     */     }
/* 344 */     User user = owh.getUser(playerName);
/* 345 */     if (user == null) {
/* 346 */       return;
/*     */     }
/* 348 */     user.getVariables().addVar(node, value);
/*     */   }
/*     */ 
/*     */   private void setGroupValue(String worldName, String groupName, String node, Object value)
/*     */   {
/*     */     OverloadedWorldHolder owh;
/*     */     OverloadedWorldHolder owh;
/* 353 */     if (worldName == null)
/* 354 */       owh = this.groupManager.getWorldsHolder().getDefaultWorld();
/*     */     else {
/* 356 */       owh = this.groupManager.getWorldsHolder().getWorldData(worldName);
/*     */     }
/* 358 */     if (owh == null) {
/* 359 */       return;
/*     */     }
/* 361 */     Group group = owh.getGroup(groupName);
/* 362 */     if (group == null) {
/* 363 */       return;
/*     */     }
/* 365 */     group.getVariables().addVar(node, value);
/*     */   }
/*     */ 
/*     */   public class PermissionServerListener
/*     */     implements Listener
/*     */   {
/*  62 */     Chat_GroupManager chat = null;
/*     */ 
/*     */     public PermissionServerListener(Chat_GroupManager chat) {
/*  65 */       this.chat = chat;
/*     */     }
/*     */ 
/*     */     @EventHandler(priority=EventPriority.MONITOR)
/*     */     public void onPluginEnable(PluginEnableEvent event) {
/*  70 */       if (this.chat.groupManager == null) {
/*  71 */         Plugin perms = event.getPlugin();
/*     */ 
/*  73 */         if (perms.getDescription().getName().equals("GroupManager")) {
/*  74 */           this.chat.groupManager = ((GroupManager)perms);
/*  75 */           Chat_GroupManager.log.info(String.format("[%s][Chat] %s hooked.", new Object[] { Chat_GroupManager.this.plugin.getDescription().getName(), "GroupManager - Chat" }));
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/*     */     @EventHandler(priority=EventPriority.MONITOR)
/*     */     public void onPluginDisable(PluginDisableEvent event) {
/*  82 */       if ((this.chat.groupManager != null) && 
/*  83 */         (event.getPlugin().getDescription().getName().equals("GroupManager"))) {
/*  84 */         this.chat.groupManager = null;
/*  85 */         Chat_GroupManager.log.info(String.format("[%s][Chat] %s un-hooked.", new Object[] { Chat_GroupManager.this.plugin.getDescription().getName(), "GroupManager - Chat" }));
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\Vault.jar
 * Qualified Name:     net.milkbowl.vault.chat.plugins.Chat_GroupManager
 * JD-Core Version:    0.6.2
 */