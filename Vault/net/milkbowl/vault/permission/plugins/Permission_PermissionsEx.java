/*     */ package net.milkbowl.vault.permission.plugins;
/*     */ 
/*     */ import java.util.List;
/*     */ import java.util.logging.Logger;
/*     */ import net.milkbowl.vault.permission.Permission;
/*     */ import org.bukkit.Bukkit;
/*     */ import org.bukkit.OfflinePlayer;
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
/*     */ import ru.tehkode.permissions.PermissionGroup;
/*     */ import ru.tehkode.permissions.PermissionManager;
/*     */ import ru.tehkode.permissions.PermissionUser;
/*     */ import ru.tehkode.permissions.bukkit.PermissionsEx;
/*     */ 
/*     */ public class Permission_PermissionsEx extends Permission
/*     */ {
/*  38 */   private final String name = "PermissionsEx";
/*  39 */   private PermissionsEx permission = null;
/*     */ 
/*     */   public Permission_PermissionsEx(Plugin plugin) {
/*  42 */     this.plugin = plugin;
/*  43 */     Bukkit.getServer().getPluginManager().registerEvents(new PermissionServerListener(this), plugin);
/*     */ 
/*  46 */     if (this.permission == null) {
/*  47 */       Plugin perms = plugin.getServer().getPluginManager().getPlugin("PermissionsEx");
/*  48 */       if ((perms != null) && 
/*  49 */         (perms.isEnabled())) {
/*     */         try {
/*  51 */           if (Double.valueOf(perms.getDescription().getVersion()).doubleValue() < 1.16D)
/*  52 */             log.info(String.format("[%s][Permission] %s below 1.16 is not compatible with Vault! Falling back to SuperPerms only mode. PLEASE UPDATE!", new Object[] { plugin.getDescription().getName(), "PermissionsEx" }));
/*     */         }
/*     */         catch (NumberFormatException e)
/*     */         {
/*     */         }
/*  57 */         this.permission = ((PermissionsEx)perms);
/*  58 */         log.info(String.format("[%s][Permission] %s hooked.", new Object[] { plugin.getDescription().getName(), "PermissionsEx" }));
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean isEnabled()
/*     */   {
/*  66 */     if (this.permission == null) {
/*  67 */       return false;
/*     */     }
/*  69 */     return this.permission.isEnabled();
/*     */   }
/*     */ 
/*     */   public String getName()
/*     */   {
/* 112 */     return "PermissionsEx";
/*     */   }
/*     */ 
/*     */   public boolean playerInGroup(String worldName, OfflinePlayer op, String groupName)
/*     */   {
/* 117 */     PermissionUser user = getUser(op);
/* 118 */     if (user == null) {
/* 119 */       return false;
/*     */     }
/* 121 */     return user.inGroup(groupName, worldName);
/*     */   }
/*     */ 
/*     */   public boolean playerInGroup(String worldName, String playerName, String groupName)
/*     */   {
/* 126 */     return PermissionsEx.getPermissionManager().getUser(playerName).inGroup(groupName, worldName);
/*     */   }
/*     */ 
/*     */   public boolean playerAddGroup(String worldName, OfflinePlayer op, String groupName)
/*     */   {
/* 131 */     PermissionGroup group = PermissionsEx.getPermissionManager().getGroup(groupName);
/* 132 */     PermissionUser user = getUser(op);
/* 133 */     if ((group == null) || (user == null)) {
/* 134 */       return false;
/*     */     }
/* 136 */     user.addGroup(groupName, worldName);
/* 137 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean playerAddGroup(String worldName, String playerName, String groupName)
/*     */   {
/* 143 */     PermissionGroup group = PermissionsEx.getPermissionManager().getGroup(groupName);
/* 144 */     PermissionUser user = PermissionsEx.getPermissionManager().getUser(playerName);
/* 145 */     if ((group == null) || (user == null)) {
/* 146 */       return false;
/*     */     }
/* 148 */     user.addGroup(groupName, worldName);
/* 149 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean playerRemoveGroup(String worldName, OfflinePlayer op, String groupName)
/*     */   {
/* 155 */     PermissionUser user = getUser(op);
/* 156 */     user.removeGroup(groupName, worldName);
/* 157 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean playerRemoveGroup(String worldName, String playerName, String groupName)
/*     */   {
/* 162 */     PermissionsEx.getPermissionManager().getUser(playerName).removeGroup(groupName, worldName);
/* 163 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean playerAdd(String worldName, OfflinePlayer op, String permission)
/*     */   {
/* 168 */     PermissionUser user = getUser(op);
/* 169 */     if (user == null) {
/* 170 */       return false;
/*     */     }
/* 172 */     user.addPermission(permission, worldName);
/* 173 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean playerAdd(String worldName, String playerName, String permission)
/*     */   {
/* 179 */     PermissionUser user = getUser(playerName);
/* 180 */     if (user == null) {
/* 181 */       return false;
/*     */     }
/* 183 */     user.addPermission(permission, worldName);
/* 184 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean playerRemove(String worldName, OfflinePlayer op, String permission)
/*     */   {
/* 190 */     PermissionUser user = getUser(op);
/* 191 */     if (user == null) {
/* 192 */       return false;
/*     */     }
/* 194 */     user.removePermission(permission, worldName);
/* 195 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean playerRemove(String worldName, String playerName, String permission)
/*     */   {
/* 201 */     PermissionUser user = getUser(playerName);
/* 202 */     if (user == null) {
/* 203 */       return false;
/*     */     }
/* 205 */     user.removePermission(permission, worldName);
/* 206 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean groupAdd(String worldName, String groupName, String permission)
/*     */   {
/* 212 */     PermissionGroup group = PermissionsEx.getPermissionManager().getGroup(groupName);
/* 213 */     if (group == null) {
/* 214 */       return false;
/*     */     }
/* 216 */     group.addPermission(permission, worldName);
/* 217 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean groupRemove(String worldName, String groupName, String permission)
/*     */   {
/* 223 */     PermissionGroup group = PermissionsEx.getPermissionManager().getGroup(groupName);
/* 224 */     if (group == null) {
/* 225 */       return false;
/*     */     }
/* 227 */     group.removePermission(permission, worldName);
/* 228 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean groupHas(String worldName, String groupName, String permission)
/*     */   {
/* 234 */     PermissionGroup group = PermissionsEx.getPermissionManager().getGroup(groupName);
/* 235 */     if (group == null) {
/* 236 */       return false;
/*     */     }
/* 238 */     return group.has(permission, worldName);
/*     */   }
/*     */ 
/*     */   private PermissionUser getUser(OfflinePlayer op)
/*     */   {
/* 243 */     return PermissionsEx.getPermissionManager().getUser(op.getUniqueId());
/*     */   }
/*     */ 
/*     */   private PermissionUser getUser(String playerName) {
/* 247 */     return PermissionsEx.getPermissionManager().getUser(playerName);
/*     */   }
/*     */ 
/*     */   public String[] getPlayerGroups(String world, OfflinePlayer op)
/*     */   {
/* 252 */     PermissionUser user = getUser(op);
/* 253 */     return user == null ? null : (String[])user.getParentIdentifiers(world).toArray(new String[0]);
/*     */   }
/*     */ 
/*     */   public String[] getPlayerGroups(String world, String playerName)
/*     */   {
/* 258 */     PermissionUser user = getUser(playerName);
/* 259 */     return user == null ? null : (String[])user.getParentIdentifiers(world).toArray(new String[0]);
/*     */   }
/*     */ 
/*     */   public String getPrimaryGroup(String world, OfflinePlayer op)
/*     */   {
/* 264 */     PermissionUser user = getUser(op);
/* 265 */     if (user == null)
/* 266 */       return null;
/* 267 */     if (user.getParentIdentifiers(world).size() > 0) {
/* 268 */       return (String)user.getParentIdentifiers(world).get(0);
/*     */     }
/* 270 */     return null;
/*     */   }
/*     */ 
/*     */   public String getPrimaryGroup(String world, String playerName)
/*     */   {
/* 276 */     PermissionUser user = PermissionsEx.getPermissionManager().getUser(playerName);
/* 277 */     if (user == null)
/* 278 */       return null;
/* 279 */     if (user.getParentIdentifiers(world).size() > 0) {
/* 280 */       return (String)user.getParentIdentifiers(world).get(0);
/*     */     }
/* 282 */     return null;
/*     */   }
/*     */ 
/*     */   public boolean playerHas(String worldName, OfflinePlayer op, String permission)
/*     */   {
/* 288 */     PermissionUser user = getUser(op);
/* 289 */     if (user != null) {
/* 290 */       return user.has(permission, worldName);
/*     */     }
/* 292 */     return false;
/*     */   }
/*     */ 
/*     */   public boolean playerHas(String worldName, String playerName, String permission)
/*     */   {
/* 298 */     PermissionUser user = getUser(playerName);
/* 299 */     if (user != null) {
/* 300 */       return user.has(permission, worldName);
/*     */     }
/* 302 */     return false;
/*     */   }
/*     */ 
/*     */   public boolean playerAddTransient(String worldName, String player, String permission)
/*     */   {
/* 308 */     PermissionUser pPlayer = getUser(player);
/* 309 */     if (pPlayer != null) {
/* 310 */       pPlayer.addTimedPermission(permission, worldName, 0);
/* 311 */       return true;
/*     */     }
/* 313 */     return false;
/*     */   }
/*     */ 
/*     */   public boolean playerAddTransient(String worldName, Player player, String permission)
/*     */   {
/* 319 */     PermissionUser pPlayer = getUser(player);
/* 320 */     if (pPlayer != null) {
/* 321 */       pPlayer.addTimedPermission(permission, worldName, 0);
/* 322 */       return true;
/*     */     }
/* 324 */     return false;
/*     */   }
/*     */ 
/*     */   public boolean playerAddTransient(String player, String permission)
/*     */   {
/* 330 */     return playerAddTransient(null, player, permission);
/*     */   }
/*     */ 
/*     */   public boolean playerAddTransient(Player player, String permission)
/*     */   {
/* 335 */     return playerAddTransient(null, player, permission);
/*     */   }
/*     */ 
/*     */   public boolean playerRemoveTransient(String worldName, String player, String permission)
/*     */   {
/* 340 */     PermissionUser pPlayer = getUser(player);
/* 341 */     if (pPlayer != null) {
/* 342 */       pPlayer.removeTimedPermission(permission, worldName);
/* 343 */       return true;
/*     */     }
/* 345 */     return false;
/*     */   }
/*     */ 
/*     */   public boolean playerRemoveTransient(Player player, String permission)
/*     */   {
/* 351 */     return playerRemoveTransient(null, player, permission);
/*     */   }
/*     */ 
/*     */   public boolean playerRemoveTransient(String worldName, Player player, String permission)
/*     */   {
/* 356 */     PermissionUser pPlayer = getUser(player);
/* 357 */     if (pPlayer != null) {
/* 358 */       pPlayer.removeTimedPermission(permission, worldName);
/* 359 */       return true;
/*     */     }
/* 361 */     return false;
/*     */   }
/*     */ 
/*     */   public boolean playerRemoveTransient(String player, String permission)
/*     */   {
/* 367 */     return playerRemoveTransient(null, player, permission);
/*     */   }
/*     */ 
/*     */   public String[] getGroups()
/*     */   {
/* 372 */     List groups = PermissionsEx.getPermissionManager().getGroupList();
/* 373 */     if ((groups == null) || (groups.isEmpty())) {
/* 374 */       return null;
/*     */     }
/* 376 */     String[] groupNames = new String[groups.size()];
/* 377 */     for (int i = 0; i < groups.size(); i++) {
/* 378 */       groupNames[i] = ((PermissionGroup)groups.get(i)).getName();
/*     */     }
/* 380 */     return groupNames;
/*     */   }
/*     */ 
/*     */   public boolean hasSuperPermsCompat()
/*     */   {
/* 385 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean hasGroupSupport()
/*     */   {
/* 390 */     return true;
/*     */   }
/*     */ 
/*     */   public class PermissionServerListener
/*     */     implements Listener
/*     */   {
/*  74 */     Permission_PermissionsEx permission = null;
/*     */ 
/*     */     public PermissionServerListener(Permission_PermissionsEx permission) {
/*  77 */       this.permission = permission;
/*     */     }
/*     */ 
/*     */     @EventHandler(priority=EventPriority.MONITOR)
/*     */     public void onPluginEnable(PluginEnableEvent event) {
/*  82 */       if (this.permission.permission == null) {
/*  83 */         Plugin perms = event.getPlugin();
/*  84 */         if (perms.getDescription().getName().equals("PermissionsEx")) {
/*     */           try {
/*  86 */             if (Double.valueOf(perms.getDescription().getVersion()).doubleValue() < 1.16D) {
/*  87 */               Permission_PermissionsEx.log.info(String.format("[%s][Permission] %s below 1.16 is not compatible with Vault! Falling back to SuperPerms only mode. PLEASE UPDATE!", new Object[] { Permission_PermissionsEx.this.plugin.getDescription().getName(), "PermissionsEx" }));
/*  88 */               return;
/*     */             }
/*     */           }
/*     */           catch (NumberFormatException e) {
/*     */           }
/*  93 */           this.permission.permission = ((PermissionsEx)perms);
/*  94 */           Permission_PermissionsEx.log.info(String.format("[%s][Permission] %s hooked.", new Object[] { Permission_PermissionsEx.this.plugin.getDescription().getName(), "PermissionsEx" }));
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/*     */     @EventHandler(priority=EventPriority.MONITOR)
/*     */     public void onPluginDisable(PluginDisableEvent event) {
/* 101 */       if ((this.permission.permission != null) && 
/* 102 */         (event.getPlugin().getDescription().getName().equals("PermissionsEx"))) {
/* 103 */         this.permission.permission = null;
/* 104 */         Permission_PermissionsEx.log.info(String.format("[%s][Permission] %s un-hooked.", new Object[] { Permission_PermissionsEx.this.plugin.getDescription().getName(), "PermissionsEx" }));
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\Vault.jar
 * Qualified Name:     net.milkbowl.vault.permission.plugins.Permission_PermissionsEx
 * JD-Core Version:    0.6.2
 */