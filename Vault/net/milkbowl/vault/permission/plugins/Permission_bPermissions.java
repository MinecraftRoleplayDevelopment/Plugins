/*     */ package net.milkbowl.vault.permission.plugins;
/*     */ 
/*     */ import de.bananaco.permissions.Permissions;
/*     */ import de.bananaco.permissions.interfaces.PermissionSet;
/*     */ import de.bananaco.permissions.worlds.HasPermission;
/*     */ import de.bananaco.permissions.worlds.WorldPermissionsManager;
/*     */ import java.util.List;
/*     */ import java.util.logging.Logger;
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
/*     */ public class Permission_bPermissions extends Permission
/*     */ {
/*  37 */   private final String name = "bPermissions";
/*     */   private WorldPermissionsManager perms;
/*     */ 
/*     */   public Permission_bPermissions(Plugin plugin)
/*     */   {
/*  41 */     this.plugin = plugin;
/*  42 */     Bukkit.getServer().getPluginManager().registerEvents(new PermissionServerListener(), plugin);
/*     */ 
/*  45 */     if (this.perms == null) {
/*  46 */       Plugin p = plugin.getServer().getPluginManager().getPlugin("bPermissions");
/*  47 */       if (p != null) {
/*  48 */         this.perms = Permissions.getWorldPermissionsManager();
/*  49 */         log.info(String.format("[%s][Permission] %s hooked.", new Object[] { plugin.getDescription().getName(), "bPermissions" }));
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public String getName()
/*     */   {
/*  80 */     return "bPermissions";
/*     */   }
/*     */ 
/*     */   public boolean isEnabled()
/*     */   {
/*  85 */     return this.perms != null;
/*     */   }
/*     */ 
/*     */   public boolean playerHas(String world, String player, String permission)
/*     */   {
/*  90 */     return HasPermission.has(player, world, permission);
/*     */   }
/*     */ 
/*     */   public boolean playerAdd(String world, String player, String permission)
/*     */   {
/*  95 */     if (world == null) {
/*  96 */       return false;
/*     */     }
/*     */ 
/*  99 */     PermissionSet set = this.perms.getPermissionSet(world);
/* 100 */     if (set == null) {
/* 101 */       return false;
/*     */     }
/*     */ 
/* 104 */     set.addPlayerNode(permission, player);
/* 105 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean playerRemove(String world, String player, String permission)
/*     */   {
/* 110 */     if (world == null) {
/* 111 */       return false;
/*     */     }
/*     */ 
/* 114 */     PermissionSet set = this.perms.getPermissionSet(world);
/* 115 */     if (set == null) {
/* 116 */       return false;
/*     */     }
/*     */ 
/* 119 */     set.removePlayerNode(permission, player);
/* 120 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean groupHas(String world, String group, String permission)
/*     */   {
/* 127 */     if (world == null) {
/* 128 */       return false;
/*     */     }
/*     */ 
/* 131 */     PermissionSet set = this.perms.getPermissionSet(world);
/* 132 */     if (set == null) {
/* 133 */       return false;
/*     */     }
/*     */ 
/* 136 */     if (set.getGroupNodes(group) == null) {
/* 137 */       return false;
/*     */     }
/*     */ 
/* 140 */     return set.getGroupNodes(group).contains(permission);
/*     */   }
/*     */ 
/*     */   public boolean groupAdd(String world, String group, String permission)
/*     */   {
/* 145 */     if (world == null) {
/* 146 */       return false;
/*     */     }
/*     */ 
/* 149 */     PermissionSet set = this.perms.getPermissionSet(world);
/* 150 */     if (set == null) {
/* 151 */       return false;
/*     */     }
/*     */ 
/* 154 */     if (set.getGroupNodes(group) == null) {
/* 155 */       return false;
/*     */     }
/*     */ 
/* 158 */     set.addNode(permission, group);
/* 159 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean groupRemove(String world, String group, String permission)
/*     */   {
/* 164 */     if (world == null) {
/* 165 */       return false;
/*     */     }
/*     */ 
/* 168 */     PermissionSet set = this.perms.getPermissionSet(world);
/* 169 */     if (set == null) {
/* 170 */       return false;
/*     */     }
/*     */ 
/* 173 */     if (set.getGroupNodes(group) == null) {
/* 174 */       return false;
/*     */     }
/*     */ 
/* 177 */     set.removeNode(permission, group);
/* 178 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean playerInGroup(String world, String player, String group)
/*     */   {
/* 183 */     if (world == null) {
/* 184 */       return false;
/*     */     }
/*     */ 
/* 187 */     PermissionSet set = this.perms.getPermissionSet(world);
/* 188 */     if (set == null) {
/* 189 */       return false;
/*     */     }
/*     */ 
/* 192 */     if (set.getGroups(player) == null) {
/* 193 */       return false;
/*     */     }
/*     */ 
/* 196 */     return set.getGroups(player).contains(group);
/*     */   }
/*     */ 
/*     */   public boolean playerAddGroup(String world, String player, String group)
/*     */   {
/* 201 */     if (world == null) {
/* 202 */       return false;
/*     */     }
/*     */ 
/* 205 */     PermissionSet set = this.perms.getPermissionSet(world);
/* 206 */     if (set == null) {
/* 207 */       return false;
/*     */     }
/*     */ 
/* 210 */     if (set.getGroupNodes(group) == null) {
/* 211 */       return false;
/*     */     }
/*     */ 
/* 214 */     set.addGroup(player, group);
/* 215 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean playerRemoveGroup(String world, String player, String group)
/*     */   {
/* 220 */     if (world == null) {
/* 221 */       return false;
/*     */     }
/*     */ 
/* 224 */     PermissionSet set = this.perms.getPermissionSet(world);
/* 225 */     if (set == null) {
/* 226 */       return false;
/*     */     }
/*     */ 
/* 229 */     set.removeGroup(player, group);
/* 230 */     return true;
/*     */   }
/*     */ 
/*     */   public String[] getPlayerGroups(String world, String player)
/*     */   {
/* 235 */     if (world == null) {
/* 236 */       return null;
/*     */     }
/*     */ 
/* 239 */     PermissionSet set = this.perms.getPermissionSet(world);
/* 240 */     if (set == null) {
/* 241 */       return null;
/*     */     }
/*     */ 
/* 244 */     List groups = set.getGroups(player);
/* 245 */     return groups == null ? null : (String[])groups.toArray(new String[0]);
/*     */   }
/*     */ 
/*     */   public String getPrimaryGroup(String world, String player)
/*     */   {
/* 250 */     if (world == null) {
/* 251 */       return null;
/*     */     }
/*     */ 
/* 254 */     PermissionSet set = this.perms.getPermissionSet(world);
/* 255 */     if (set == null) {
/* 256 */       return null;
/*     */     }
/*     */ 
/* 259 */     List groups = set.getGroups(player);
/* 260 */     if ((groups == null) || (groups.isEmpty())) {
/* 261 */       return null;
/*     */     }
/* 263 */     return (String)groups.get(0);
/*     */   }
/*     */ 
/*     */   public String[] getGroups()
/*     */   {
/* 269 */     throw new UnsupportedOperationException("bPermissions does not support server-wide groups");
/*     */   }
/*     */ 
/*     */   public boolean hasSuperPermsCompat()
/*     */   {
/* 274 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean hasGroupSupport()
/*     */   {
/* 279 */     return true;
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
/*  58 */       if (Permission_bPermissions.this.perms == null) {
/*  59 */         Plugin p = event.getPlugin();
/*  60 */         if ((p.getDescription().getName().equals("bPermissions")) && (p.isEnabled())) {
/*  61 */           Permission_bPermissions.this.perms = Permissions.getWorldPermissionsManager();
/*  62 */           Permission_bPermissions.log.info(String.format("[%s][Permission] %s hooked.", new Object[] { Permission_bPermissions.this.plugin.getDescription().getName(), "bPermissions" }));
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/*     */     @EventHandler(priority=EventPriority.MONITOR)
/*     */     public void onPluginDisable(PluginDisableEvent event) {
/*  69 */       if ((Permission_bPermissions.this.perms != null) && 
/*  70 */         (event.getPlugin().getDescription().getName().equals("bPermissions"))) {
/*  71 */         Permission_bPermissions.this.perms = null;
/*  72 */         Permission_bPermissions.log.info(String.format("[%s][Permission] %s un-hooked.", new Object[] { Permission_bPermissions.this.plugin.getDescription().getName(), "bPermissions" }));
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\Vault.jar
 * Qualified Name:     net.milkbowl.vault.permission.plugins.Permission_bPermissions
 * JD-Core Version:    0.6.2
 */