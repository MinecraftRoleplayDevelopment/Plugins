/*     */ package net.milkbowl.vault.permission.plugins;
/*     */ 
/*     */ import java.util.Collection;
/*     */ import java.util.HashSet;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.logging.Logger;
/*     */ import net.milkbowl.vault.permission.Permission;
/*     */ import org.anjocaido.groupmanager.GroupManager;
/*     */ import org.anjocaido.groupmanager.data.Group;
/*     */ import org.anjocaido.groupmanager.data.User;
/*     */ import org.anjocaido.groupmanager.dataholder.OverloadedWorldHolder;
/*     */ import org.anjocaido.groupmanager.dataholder.worlds.WorldsHolder;
/*     */ import org.anjocaido.groupmanager.permissions.AnjoPermissionsHandler;
/*     */ import org.anjocaido.groupmanager.permissions.BukkitPermissions;
/*     */ import org.bukkit.Bukkit;
/*     */ import org.bukkit.Server;
/*     */ import org.bukkit.World;
/*     */ import org.bukkit.entity.Player;
/*     */ import org.bukkit.event.EventHandler;
/*     */ import org.bukkit.event.EventPriority;
/*     */ import org.bukkit.event.Listener;
/*     */ import org.bukkit.event.server.PluginDisableEvent;
/*     */ import org.bukkit.event.server.PluginEnableEvent;
/*     */ import org.bukkit.permissions.PermissionAttachment;
/*     */ import org.bukkit.permissions.PermissionAttachmentInfo;
/*     */ import org.bukkit.plugin.Plugin;
/*     */ import org.bukkit.plugin.PluginDescriptionFile;
/*     */ import org.bukkit.plugin.PluginManager;
/*     */ 
/*     */ public class Permission_GroupManager extends Permission
/*     */ {
/*  43 */   private final String name = "GroupManager";
/*     */   private GroupManager groupManager;
/*     */ 
/*     */   public Permission_GroupManager(Plugin plugin)
/*     */   {
/*  47 */     this.plugin = plugin;
/*  48 */     Bukkit.getServer().getPluginManager().registerEvents(new PermissionServerListener(this), plugin);
/*     */ 
/*  51 */     if (this.groupManager == null) {
/*  52 */       Plugin perms = plugin.getServer().getPluginManager().getPlugin("GroupManager");
/*  53 */       if ((perms != null) && (perms.isEnabled())) {
/*  54 */         this.groupManager = ((GroupManager)perms);
/*  55 */         log.info(String.format("[%s][Permission] %s hooked.", new Object[] { plugin.getDescription().getName(), "GroupManager" }));
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public String getName()
/*     */   {
/*  91 */     getClass(); return "GroupManager";
/*     */   }
/*     */ 
/*     */   public boolean isEnabled()
/*     */   {
/*  96 */     return (this.groupManager != null) && (this.groupManager.isEnabled());
/*     */   }
/*     */ 
/*     */   public boolean playerHas(String worldName, String playerName, String permission)
/*     */   {
/*     */     AnjoPermissionsHandler handler;
/*     */     AnjoPermissionsHandler handler;
/* 102 */     if (worldName == null) {
/* 103 */       handler = this.groupManager.getWorldsHolder().getWorldPermissionsByPlayerName(playerName);
/*     */     }
/*     */     else {
/* 106 */       handler = this.groupManager.getWorldsHolder().getWorldPermissions(worldName);
/*     */     }
/* 108 */     if (handler == null) {
/* 109 */       return false;
/*     */     }
/* 111 */     return handler.permission(playerName, permission);
/*     */   }
/*     */ 
/*     */   public boolean playerAdd(String worldName, String playerName, String permission)
/*     */   {
/*     */     OverloadedWorldHolder owh;
/*     */     OverloadedWorldHolder owh;
/* 117 */     if (worldName == null)
/* 118 */       owh = this.groupManager.getWorldsHolder().getWorldDataByPlayerName(playerName);
/*     */     else {
/* 120 */       owh = this.groupManager.getWorldsHolder().getWorldData(worldName);
/*     */     }
/* 122 */     if (owh == null) {
/* 123 */       return false;
/*     */     }
/*     */ 
/* 126 */     User user = owh.getUser(playerName);
/* 127 */     if (user == null) {
/* 128 */       return false;
/*     */     }
/*     */ 
/* 131 */     user.addPermission(permission);
/* 132 */     Player p = Bukkit.getPlayer(playerName);
/* 133 */     if (p != null) {
/* 134 */       GroupManager.BukkitPermissions.updatePermissions(p);
/*     */     }
/* 136 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean playerRemove(String worldName, String playerName, String permission)
/*     */   {
/*     */     OverloadedWorldHolder owh;
/*     */     OverloadedWorldHolder owh;
/* 142 */     if (worldName == null)
/* 143 */       owh = this.groupManager.getWorldsHolder().getWorldDataByPlayerName(playerName);
/*     */     else {
/* 145 */       owh = this.groupManager.getWorldsHolder().getWorldData(worldName);
/*     */     }
/* 147 */     if (owh == null) {
/* 148 */       return false;
/*     */     }
/*     */ 
/* 151 */     User user = owh.getUser(playerName);
/* 152 */     if (user == null) {
/* 153 */       return false;
/*     */     }
/*     */ 
/* 156 */     user.removePermission(permission);
/* 157 */     Player p = Bukkit.getPlayer(playerName);
/* 158 */     if (p != null) {
/* 159 */       GroupManager.BukkitPermissions.updatePermissions(p);
/*     */     }
/* 161 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean groupHas(String worldName, String groupName, String permission)
/*     */   {
/*     */     OverloadedWorldHolder owh;
/*     */     OverloadedWorldHolder owh;
/* 167 */     if (worldName == null)
/* 168 */       owh = this.groupManager.getWorldsHolder().getDefaultWorld();
/*     */     else {
/* 170 */       owh = this.groupManager.getWorldsHolder().getWorldData(worldName);
/*     */     }
/* 172 */     if (owh == null) {
/* 173 */       return false;
/*     */     }
/*     */ 
/* 176 */     Group group = owh.getGroup(groupName);
/* 177 */     if (group == null) {
/* 178 */       return false;
/*     */     }
/*     */ 
/* 181 */     return group.hasSamePermissionNode(permission);
/*     */   }
/*     */ 
/*     */   public boolean groupAdd(String worldName, String groupName, String permission)
/*     */   {
/*     */     OverloadedWorldHolder owh;
/*     */     OverloadedWorldHolder owh;
/* 187 */     if (worldName == null)
/* 188 */       owh = this.groupManager.getWorldsHolder().getDefaultWorld();
/*     */     else {
/* 190 */       owh = this.groupManager.getWorldsHolder().getWorldData(worldName);
/*     */     }
/* 192 */     if (owh == null) {
/* 193 */       return false;
/*     */     }
/*     */ 
/* 196 */     Group group = owh.getGroup(groupName);
/* 197 */     if (group == null) {
/* 198 */       return false;
/*     */     }
/*     */ 
/* 201 */     group.addPermission(permission);
/* 202 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean groupRemove(String worldName, String groupName, String permission)
/*     */   {
/*     */     OverloadedWorldHolder owh;
/*     */     OverloadedWorldHolder owh;
/* 208 */     if (worldName == null)
/* 209 */       owh = this.groupManager.getWorldsHolder().getDefaultWorld();
/*     */     else {
/* 211 */       owh = this.groupManager.getWorldsHolder().getWorldData(worldName);
/*     */     }
/* 213 */     if (owh == null) {
/* 214 */       return false;
/*     */     }
/*     */ 
/* 217 */     Group group = owh.getGroup(groupName);
/* 218 */     if (group == null) {
/* 219 */       return false;
/*     */     }
/*     */ 
/* 222 */     group.removePermission(permission);
/* 223 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean playerInGroup(String worldName, String playerName, String groupName)
/*     */   {
/*     */     AnjoPermissionsHandler handler;
/*     */     AnjoPermissionsHandler handler;
/* 229 */     if (worldName == null)
/* 230 */       handler = this.groupManager.getWorldsHolder().getWorldPermissionsByPlayerName(playerName);
/*     */     else {
/* 232 */       handler = this.groupManager.getWorldsHolder().getWorldPermissions(worldName);
/*     */     }
/* 234 */     if (handler == null) {
/* 235 */       return false;
/*     */     }
/* 237 */     return handler.inGroup(playerName, groupName);
/*     */   }
/*     */ 
/*     */   public boolean playerAddGroup(String worldName, String playerName, String groupName)
/*     */   {
/*     */     OverloadedWorldHolder owh;
/*     */     OverloadedWorldHolder owh;
/* 243 */     if (worldName == null)
/* 244 */       owh = this.groupManager.getWorldsHolder().getWorldDataByPlayerName(playerName);
/*     */     else {
/* 246 */       owh = this.groupManager.getWorldsHolder().getWorldData(worldName);
/*     */     }
/* 248 */     if (owh == null) {
/* 249 */       return false;
/*     */     }
/* 251 */     User user = owh.getUser(playerName);
/* 252 */     if (user == null) {
/* 253 */       return false;
/*     */     }
/* 255 */     Group group = owh.getGroup(groupName);
/* 256 */     if (group == null) {
/* 257 */       return false;
/*     */     }
/* 259 */     if (user.getGroup().equals(owh.getDefaultGroup()))
/* 260 */       user.setGroup(group);
/* 261 */     else if (group.getInherits().contains(user.getGroup().getName().toLowerCase()))
/* 262 */       user.setGroup(group);
/*     */     else {
/* 264 */       user.addSubGroup(group);
/*     */     }
/* 266 */     Player p = Bukkit.getPlayer(playerName);
/* 267 */     if (p != null) {
/* 268 */       GroupManager.BukkitPermissions.updatePermissions(p);
/*     */     }
/* 270 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean playerRemoveGroup(String worldName, String playerName, String groupName)
/*     */   {
/*     */     OverloadedWorldHolder owh;
/*     */     OverloadedWorldHolder owh;
/* 276 */     if (worldName == null)
/* 277 */       owh = this.groupManager.getWorldsHolder().getWorldDataByPlayerName(playerName);
/*     */     else {
/* 279 */       owh = this.groupManager.getWorldsHolder().getWorldData(worldName);
/*     */     }
/* 281 */     if (owh == null) {
/* 282 */       return false;
/*     */     }
/* 284 */     User user = owh.getUser(playerName);
/* 285 */     if (user == null) {
/* 286 */       return false;
/*     */     }
/* 288 */     boolean success = false;
/* 289 */     if (user.getGroup().getName().equalsIgnoreCase(groupName)) {
/* 290 */       user.setGroup(owh.getDefaultGroup());
/* 291 */       success = true;
/*     */     } else {
/* 293 */       Group group = owh.getGroup(groupName);
/* 294 */       if (group != null) {
/* 295 */         success = user.removeSubGroup(group);
/*     */       }
/*     */     }
/* 298 */     if (success) {
/* 299 */       Player p = Bukkit.getPlayer(playerName);
/* 300 */       if (p != null) {
/* 301 */         GroupManager.BukkitPermissions.updatePermissions(p);
/*     */       }
/*     */     }
/* 304 */     return success;
/*     */   }
/*     */ 
/*     */   public String[] getPlayerGroups(String worldName, String playerName)
/*     */   {
/*     */     AnjoPermissionsHandler handler;
/*     */     AnjoPermissionsHandler handler;
/* 310 */     if (worldName == null)
/* 311 */       handler = this.groupManager.getWorldsHolder().getWorldPermissionsByPlayerName(playerName);
/*     */     else {
/* 313 */       handler = this.groupManager.getWorldsHolder().getWorldPermissions(worldName);
/*     */     }
/* 315 */     if (handler == null) {
/* 316 */       return null;
/*     */     }
/* 318 */     return handler.getGroups(playerName);
/*     */   }
/*     */ 
/*     */   public String getPrimaryGroup(String worldName, String playerName)
/*     */   {
/*     */     AnjoPermissionsHandler handler;
/*     */     AnjoPermissionsHandler handler;
/* 324 */     if (worldName == null)
/* 325 */       handler = this.groupManager.getWorldsHolder().getWorldPermissionsByPlayerName(playerName);
/*     */     else {
/* 327 */       handler = this.groupManager.getWorldsHolder().getWorldPermissions(worldName);
/*     */     }
/* 329 */     if (handler == null) {
/* 330 */       return null;
/*     */     }
/* 332 */     return handler.getGroup(playerName);
/*     */   }
/*     */ 
/*     */   public boolean playerAddTransient(String world, String player, String permission)
/*     */   {
/* 337 */     if (world != null) {
/* 338 */       throw new UnsupportedOperationException(getName() + " does not support World based transient permissions!");
/*     */     }
/* 340 */     Player p = this.plugin.getServer().getPlayer(player);
/* 341 */     if (p == null) {
/* 342 */       throw new UnsupportedOperationException(getName() + " does not support offline player transient permissions!");
/*     */     }
/*     */ 
/* 345 */     for (PermissionAttachmentInfo paInfo : p.getEffectivePermissions()) {
/* 346 */       if (paInfo.getAttachment().getPlugin().equals(this.plugin)) {
/* 347 */         paInfo.getAttachment().setPermission(permission, true);
/* 348 */         return true;
/*     */       }
/*     */     }
/*     */ 
/* 352 */     PermissionAttachment attach = p.addAttachment(this.plugin);
/* 353 */     attach.setPermission(permission, true);
/*     */ 
/* 355 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean playerRemoveTransient(String world, String player, String permission)
/*     */   {
/* 360 */     if (world != null) {
/* 361 */       throw new UnsupportedOperationException(getName() + " does not support World based transient permissions!");
/*     */     }
/* 363 */     Player p = this.plugin.getServer().getPlayer(player);
/* 364 */     if (p == null) {
/* 365 */       throw new UnsupportedOperationException(getName() + " does not support offline player transient permissions!");
/*     */     }
/* 367 */     for (PermissionAttachmentInfo paInfo : p.getEffectivePermissions()) {
/* 368 */       if (paInfo.getAttachment().getPlugin().equals(this.plugin)) {
/* 369 */         return ((Boolean)paInfo.getAttachment().getPermissions().remove(permission)).booleanValue();
/*     */       }
/*     */     }
/* 372 */     return false;
/*     */   }
/*     */ 
/*     */   public String[] getGroups()
/*     */   {
/* 377 */     Set groupNames = new HashSet();
/* 378 */     for (World world : Bukkit.getServer().getWorlds()) {
/* 379 */       OverloadedWorldHolder owh = this.groupManager.getWorldsHolder().getWorldData(world.getName());
/* 380 */       if (owh != null)
/*     */       {
/* 383 */         Collection groups = owh.getGroupList();
/* 384 */         if (groups != null)
/*     */         {
/* 387 */           for (Group group : groups)
/* 388 */             groupNames.add(group.getName()); 
/*     */         }
/*     */       }
/*     */     }
/* 391 */     return (String[])groupNames.toArray(new String[0]);
/*     */   }
/*     */ 
/*     */   public boolean hasSuperPermsCompat()
/*     */   {
/* 396 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean hasGroupSupport()
/*     */   {
/* 401 */     return true;
/*     */   }
/*     */ 
/*     */   public class PermissionServerListener
/*     */     implements Listener
/*     */   {
/*  61 */     Permission_GroupManager permission = null;
/*     */ 
/*     */     public PermissionServerListener(Permission_GroupManager permission) {
/*  64 */       this.permission = permission;
/*     */     }
/*     */ 
/*     */     @EventHandler(priority=EventPriority.MONITOR)
/*     */     public void onPluginEnable(PluginEnableEvent event) {
/*  69 */       if (this.permission.groupManager == null) {
/*  70 */         Plugin p = event.getPlugin();
/*  71 */         if (p.getDescription().getName().equals("GroupManager")) {
/*  72 */           this.permission.groupManager = ((GroupManager)p);
/*  73 */           Permission_GroupManager.log.info(String.format("[%s][Permission] %s hooked.", new Object[] { Permission_GroupManager.this.plugin.getDescription().getName(), "GroupManager" }));
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/*     */     @EventHandler(priority=EventPriority.MONITOR)
/*     */     public void onPluginDisable(PluginDisableEvent event) {
/*  80 */       if ((this.permission.groupManager != null) && 
/*  81 */         (event.getPlugin().getDescription().getName().equals("GroupManager"))) {
/*  82 */         this.permission.groupManager = null;
/*  83 */         Permission_GroupManager.log.info(String.format("[%s][Permission] %s un-hooked.", new Object[] { Permission_GroupManager.this.plugin.getDescription().getName(), "GroupManager" }));
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\Vault.jar
 * Qualified Name:     net.milkbowl.vault.permission.plugins.Permission_GroupManager
 * JD-Core Version:    0.6.2
 */