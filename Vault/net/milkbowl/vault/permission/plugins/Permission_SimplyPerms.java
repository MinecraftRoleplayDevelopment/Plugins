/*     */ package net.milkbowl.vault.permission.plugins;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.logging.Logger;
/*     */ import net.crystalyx.bukkit.simplyperms.SimplyAPI;
/*     */ import net.crystalyx.bukkit.simplyperms.SimplyPlugin;
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
/*     */ public class Permission_SimplyPerms extends Permission
/*     */ {
/*  37 */   private final String name = "SimplyPerms";
/*     */   private SimplyAPI perms;
/*     */ 
/*     */   public Permission_SimplyPerms(Plugin plugin)
/*     */   {
/*  41 */     this.plugin = plugin;
/*  42 */     Bukkit.getServer().getPluginManager().registerEvents(new PermissionServerListener(this), plugin);
/*     */ 
/*  44 */     if (this.perms == null) {
/*  45 */       Plugin perms = plugin.getServer().getPluginManager().getPlugin("SimplyPerms");
/*  46 */       if ((perms != null) && (perms.isEnabled())) {
/*  47 */         this.perms = ((SimplyPlugin)perms).getAPI();
/*  48 */         log.info(String.format("[%s][Permission] %s hooked.", new Object[] { plugin.getDescription().getName(), "SimplyPerms" }));
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public String getName()
/*     */   {
/*  84 */     return "SimplyPerms";
/*     */   }
/*     */ 
/*     */   public boolean isEnabled()
/*     */   {
/*  89 */     return this.perms != null;
/*     */   }
/*     */ 
/*     */   public boolean hasSuperPermsCompat()
/*     */   {
/*  94 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean playerHas(String world, String player, String permission)
/*     */   {
/*  99 */     permission = permission.toLowerCase();
/* 100 */     Map playerPermissions = this.perms.getPlayerPermissions(player, world);
/* 101 */     return (playerPermissions.containsKey(permission)) && (((Boolean)playerPermissions.get(permission)).booleanValue());
/*     */   }
/*     */ 
/*     */   public boolean playerAdd(String world, String player, String permission)
/*     */   {
/* 106 */     permission = permission.toLowerCase();
/* 107 */     if (world != null)
/* 108 */       this.perms.addPlayerPermission(player, world, permission, true);
/*     */     else {
/* 110 */       this.perms.addPlayerPermission(player, permission, true);
/*     */     }
/* 112 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean playerRemove(String world, String player, String permission)
/*     */   {
/* 117 */     permission = permission.toLowerCase();
/* 118 */     if (world != null)
/* 119 */       this.perms.removePlayerPermission(player, world, permission);
/*     */     else {
/* 121 */       this.perms.removePlayerPermission(player, permission);
/*     */     }
/* 123 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean groupHas(String world, String group, String permission)
/*     */   {
/* 128 */     permission = permission.toLowerCase();
/* 129 */     Map groupPermissions = this.perms.getGroupPermissions(group, world);
/* 130 */     return (groupPermissions.containsKey(permission)) && (((Boolean)groupPermissions.get(permission)).booleanValue());
/*     */   }
/*     */ 
/*     */   public boolean groupAdd(String world, String group, String permission)
/*     */   {
/* 135 */     permission = permission.toLowerCase();
/* 136 */     if (world != null)
/* 137 */       this.perms.addGroupPermission(group, world, permission, true);
/*     */     else {
/* 139 */       this.perms.addGroupPermission(group, permission, true);
/*     */     }
/* 141 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean groupRemove(String world, String group, String permission)
/*     */   {
/* 146 */     permission = permission.toLowerCase();
/* 147 */     if (world != null) {
/* 148 */       permission = world + ":" + permission;
/* 149 */       this.perms.removeGroupPermission(group, world, permission);
/*     */     } else {
/* 151 */       this.perms.removeGroupPermission(group, permission);
/*     */     }
/* 153 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean playerInGroup(String world, String player, String group)
/*     */   {
/* 158 */     if (world != null) {
/* 159 */       for (String g : this.perms.getPlayerGroups(player)) {
/* 160 */         if (g.equals(group)) {
/* 161 */           return this.perms.getGroupWorlds(group).contains(world);
/*     */         }
/*     */       }
/* 164 */       return false;
/*     */     }
/*     */ 
/* 167 */     if (!this.perms.getAllGroups().contains(group)) {
/* 168 */       return false;
/*     */     }
/* 170 */     return this.perms.getPlayerGroups(player).contains(group);
/*     */   }
/*     */ 
/*     */   public boolean playerAddGroup(String world, String player, String group)
/*     */   {
/* 175 */     group = group.toLowerCase();
/* 176 */     this.perms.addPlayerGroup(player, group);
/* 177 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean playerRemoveGroup(String world, String player, String group)
/*     */   {
/* 182 */     group = group.toLowerCase();
/* 183 */     this.perms.removePlayerGroup(player, group);
/* 184 */     return true;
/*     */   }
/*     */ 
/*     */   public String[] getPlayerGroups(String world, String player)
/*     */   {
/* 189 */     List groupList = new ArrayList();
/* 190 */     if ((world != null) && (this.perms.isPlayerInDB(player))) {
/* 191 */       for (String group : this.perms.getPlayerGroups(player)) {
/* 192 */         if (this.perms.getGroupWorlds(group).contains(world)) {
/* 193 */           groupList.add(group);
/*     */         }
/*     */       }
/* 196 */       return (String[])groupList.toArray(new String[0]);
/*     */     }
/* 198 */     if (this.perms.isPlayerInDB(player)) {
/* 199 */       for (String group : this.perms.getPlayerGroups(player)) {
/* 200 */         groupList.add(group);
/*     */       }
/*     */     }
/* 203 */     return (String[])groupList.toArray(new String[0]);
/*     */   }
/*     */ 
/*     */   public String getPrimaryGroup(String world, String player)
/*     */   {
/* 208 */     if (!this.perms.isPlayerInDB(player))
/* 209 */       return null;
/* 210 */     if ((this.perms.getPlayerGroups(player) != null) && (!this.perms.getPlayerGroups(player).isEmpty())) {
/* 211 */       return (String)this.perms.getPlayerGroups(player).get(0);
/*     */     }
/* 213 */     return null;
/*     */   }
/*     */ 
/*     */   public String[] getGroups()
/*     */   {
/* 218 */     return (String[])this.perms.getAllGroups().toArray(new String[0]);
/*     */   }
/*     */ 
/*     */   public boolean hasGroupSupport()
/*     */   {
/* 223 */     return true;
/*     */   }
/*     */ 
/*     */   public class PermissionServerListener
/*     */     implements Listener
/*     */   {
/*  54 */     Permission_SimplyPerms permission = null;
/*     */ 
/*     */     public PermissionServerListener(Permission_SimplyPerms permission) {
/*  57 */       this.permission = permission;
/*     */     }
/*     */ 
/*     */     @EventHandler(priority=EventPriority.MONITOR)
/*     */     public void onPluginEnable(PluginEnableEvent event) {
/*  62 */       if (this.permission.perms == null) {
/*  63 */         Plugin perms = event.getPlugin();
/*  64 */         if (perms.getDescription().getName().equals("SimplyPerms")) {
/*  65 */           this.permission.perms = ((SimplyPlugin)perms).getAPI();
/*  66 */           Permission_SimplyPerms.log.info(String.format("[%s][Permission] %s hooked.", new Object[] { Permission_SimplyPerms.this.plugin.getDescription().getName(), "SimplyPerms" }));
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/*     */     @EventHandler(priority=EventPriority.MONITOR)
/*     */     public void onPluginDisable(PluginDisableEvent event) {
/*  73 */       if ((this.permission.perms != null) && 
/*  74 */         (event.getPlugin().getDescription().getName().equals("SimplyPerms"))) {
/*  75 */         this.permission.perms = null;
/*  76 */         Permission_SimplyPerms.log.info(String.format("[%s][Permission] %s un-hooked.", new Object[] { Permission_SimplyPerms.this.plugin.getDescription().getName(), "SimplyPerms" }));
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\Vault.jar
 * Qualified Name:     net.milkbowl.vault.permission.plugins.Permission_SimplyPerms
 * JD-Core Version:    0.6.2
 */