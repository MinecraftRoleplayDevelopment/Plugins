/*     */ package net.milkbowl.vault.permission.plugins;
/*     */ 
/*     */ import com.overmc.overpermissions.OverPermissions;
/*     */ import com.overmc.overpermissions.OverPermissionsAPI;
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
/*     */ public class Permission_OverPermissions extends Permission
/*     */ {
/*  33 */   private final String name = "OverPermissions";
/*     */   private OverPermissions overPerms;
/*     */   private OverPermissionsAPI api;
/*     */ 
/*     */   public Permission_OverPermissions(Plugin plugin)
/*     */   {
/*  38 */     this.plugin = plugin;
/*  39 */     Bukkit.getServer().getPluginManager().registerEvents(new PermissionServerListener(this), plugin);
/*     */ 
/*  41 */     if (this.overPerms == null) {
/*  42 */       Plugin perms = plugin.getServer().getPluginManager().getPlugin("OverPermissions");
/*  43 */       if ((perms != null) && (perms.isEnabled())) {
/*  44 */         this.overPerms = ((OverPermissions)perms);
/*  45 */         log.info(String.format("[%s][Permission] %s hooked.", new Object[] { plugin.getDescription().getName(), "OverPermissions" }));
/*     */       }
/*     */     }
/*     */ 
/*  49 */     if ((this.api == null) && (this.overPerms != null))
/*  50 */       this.api = this.overPerms.getAPI();
/*     */   }
/*     */ 
/*     */   public String getName()
/*     */   {
/*  56 */     return "OverPermissions";
/*     */   }
/*     */ 
/*     */   public boolean isEnabled()
/*     */   {
/*  61 */     return (this.overPerms != null) && (this.overPerms.isEnabled());
/*     */   }
/*     */ 
/*     */   public boolean playerHas(String worldName, String playerName, String permission)
/*     */   {
/*  66 */     return this.api.playerHas(worldName, playerName, permission);
/*     */   }
/*     */ 
/*     */   public boolean playerAdd(String worldName, String playerName, String permission)
/*     */   {
/*  71 */     return this.api.playerAdd(worldName, playerName, permission);
/*     */   }
/*     */ 
/*     */   public boolean playerRemove(String worldName, String playerName, String permission)
/*     */   {
/*  76 */     return this.api.playerRemove(worldName, playerName, permission);
/*     */   }
/*     */ 
/*     */   public boolean groupHas(String worldName, String groupName, String permission)
/*     */   {
/*  81 */     return this.api.groupHas(groupName, permission);
/*     */   }
/*     */ 
/*     */   public boolean groupAdd(String worldName, String groupName, String permission)
/*     */   {
/*  86 */     return this.api.groupAdd(groupName, permission);
/*     */   }
/*     */ 
/*     */   public boolean groupRemove(String worldName, String groupName, String permission)
/*     */   {
/*  91 */     return this.api.groupRemove(groupName, permission);
/*     */   }
/*     */ 
/*     */   public boolean playerInGroup(String worldName, String playerName, String groupName)
/*     */   {
/*  96 */     return this.api.groupHasPlayer(playerName, groupName);
/*     */   }
/*     */ 
/*     */   public boolean playerAddGroup(String worldName, String playerName, String groupName)
/*     */   {
/* 101 */     return this.api.playerAddGroup(playerName, groupName);
/*     */   }
/*     */ 
/*     */   public boolean playerRemoveGroup(String worldName, String playerName, String groupName)
/*     */   {
/* 106 */     return this.api.playerRemoveGroup(playerName, groupName);
/*     */   }
/*     */ 
/*     */   public String[] getPlayerGroups(String worldName, String playerName)
/*     */   {
/* 111 */     return this.api.getPlayerGroups(worldName, playerName);
/*     */   }
/*     */ 
/*     */   public String getPrimaryGroup(String worldName, String playerName)
/*     */   {
/* 116 */     String[] playerGroups = getPlayerGroups(worldName, playerName);
/* 117 */     if (playerGroups.length == 0) {
/* 118 */       return null;
/*     */     }
/* 120 */     return playerGroups[0];
/*     */   }
/*     */ 
/*     */   public boolean playerAddTransient(String world, String player, String permission)
/*     */   {
/* 125 */     return this.api.playerAddTransient(world, player, permission);
/*     */   }
/*     */ 
/*     */   public boolean playerRemoveTransient(String world, String player, String permission)
/*     */   {
/* 130 */     return this.api.playerRemoveTransient(world, player, permission);
/*     */   }
/*     */ 
/*     */   public String[] getGroups()
/*     */   {
/* 135 */     return this.api.getGroupsArray();
/*     */   }
/*     */ 
/*     */   public boolean hasSuperPermsCompat()
/*     */   {
/* 140 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean hasGroupSupport()
/*     */   {
/* 145 */     return true;
/*     */   }
/*     */ 
/*     */   public class PermissionServerListener implements Listener {
/* 149 */     Permission_OverPermissions permission = null;
/*     */ 
/*     */     public PermissionServerListener(Permission_OverPermissions permission) {
/* 152 */       this.permission = permission;
/*     */     }
/*     */ 
/*     */     @EventHandler(priority=EventPriority.MONITOR)
/*     */     public void onPluginEnable(PluginEnableEvent event) {
/* 157 */       if (this.permission.overPerms == null) {
/* 158 */         Plugin perms = Permission_OverPermissions.this.plugin.getServer().getPluginManager().getPlugin("OverPermissions");
/* 159 */         if (perms != null) {
/* 160 */           this.permission.overPerms = ((OverPermissions)perms);
/* 161 */           Permission_OverPermissions.log.info(String.format("[%s][Permission] %s hooked.", new Object[] { Permission_OverPermissions.this.plugin.getDescription().getName(), "OverPermissions" }));
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/*     */     @EventHandler(priority=EventPriority.MONITOR)
/*     */     public void onPluginDisable(PluginDisableEvent event) {
/* 168 */       if ((this.permission.overPerms != null) && (event.getPlugin().getDescription().getName().equals("OverPermissions")))
/*     */       {
/* 170 */         this.permission.overPerms = null;
/* 171 */         Permission_OverPermissions.log.info(String.format("[%s][Permission] %s un-hooked.", new Object[] { Permission_OverPermissions.this.plugin.getDescription().getName(), "OverPermissions" }));
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\Vault.jar
 * Qualified Name:     net.milkbowl.vault.permission.plugins.Permission_OverPermissions
 * JD-Core Version:    0.6.2
 */