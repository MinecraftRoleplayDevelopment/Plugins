/*     */ package net.milkbowl.vault.permission.plugins;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.logging.Level;
/*     */ import java.util.logging.Logger;
/*     */ import net.ae97.totalpermissions.PermissionManager;
/*     */ import net.ae97.totalpermissions.TotalPermissions;
/*     */ import net.ae97.totalpermissions.permission.PermissionBase;
/*     */ import net.ae97.totalpermissions.permission.PermissionUser;
/*     */ import net.milkbowl.vault.permission.Permission;
/*     */ import org.bukkit.event.EventHandler;
/*     */ import org.bukkit.event.EventPriority;
/*     */ import org.bukkit.event.Listener;
/*     */ import org.bukkit.event.server.PluginDisableEvent;
/*     */ import org.bukkit.event.server.PluginEnableEvent;
/*     */ import org.bukkit.plugin.Plugin;
/*     */ import org.bukkit.plugin.PluginDescriptionFile;
/*     */ 
/*     */ public class Permission_TotalPermissions extends Permission
/*     */ {
/*  36 */   private final String name = "TotalPermissions";
/*     */   private PermissionManager manager;
/*     */   private TotalPermissions totalperms;
/*     */ 
/*     */   public Permission_TotalPermissions(Plugin pl)
/*     */   {
/*  41 */     this.plugin = pl;
/*     */   }
/*     */ 
/*     */   public String getName()
/*     */   {
/*  72 */     return "TotalPermissions";
/*     */   }
/*     */ 
/*     */   public boolean isEnabled()
/*     */   {
/*  77 */     return (this.plugin != null) && (this.plugin.isEnabled()) && (this.totalperms != null) && (this.totalperms.isEnabled());
/*     */   }
/*     */ 
/*     */   public boolean hasSuperPermsCompat()
/*     */   {
/*  82 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean hasGroupSupport()
/*     */   {
/*  87 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean playerHas(String world, String player, String permission)
/*     */   {
/*  92 */     PermissionBase user = this.manager.getUser(player);
/*  93 */     return user.has(permission, world);
/*     */   }
/*     */ 
/*     */   public boolean playerAdd(String world, String player, String permission)
/*     */   {
/*     */     try {
/*  99 */       PermissionBase user = this.manager.getUser(player);
/* 100 */       user.addPerm(permission, world);
/* 101 */       return true;
/*     */     } catch (IOException ex) {
/* 103 */       this.plugin.getLogger().log(Level.SEVERE, String.format("[%s] An error occured while saving perms", new Object[] { this.totalperms.getDescription().getName() }), ex);
/*     */     }
/* 105 */     return false;
/*     */   }
/*     */ 
/*     */   public boolean playerRemove(String world, String player, String permission)
/*     */   {
/*     */     try
/*     */     {
/* 112 */       PermissionBase user = this.manager.getUser(player);
/* 113 */       user.remPerm(permission, world);
/* 114 */       return true;
/*     */     } catch (IOException ex) {
/* 116 */       this.plugin.getLogger().log(Level.SEVERE, String.format("[%s] An error occured while saving perms", new Object[] { this.totalperms.getDescription().getName() }), ex);
/*     */     }
/* 118 */     return false;
/*     */   }
/*     */ 
/*     */   public boolean groupHas(String world, String group, String permission)
/*     */   {
/* 124 */     PermissionBase permGroup = this.manager.getGroup(group);
/* 125 */     return permGroup.has(permission, world);
/*     */   }
/*     */ 
/*     */   public boolean groupAdd(String world, String group, String permission)
/*     */   {
/*     */     try {
/* 131 */       PermissionBase permGroup = this.manager.getGroup(group);
/* 132 */       permGroup.addPerm(permission, world);
/* 133 */       return true;
/*     */     } catch (IOException ex) {
/* 135 */       this.plugin.getLogger().log(Level.SEVERE, String.format("[%s] An error occured while saving perms", new Object[] { this.totalperms.getDescription().getName() }), ex);
/*     */     }
/* 137 */     return false;
/*     */   }
/*     */ 
/*     */   public boolean groupRemove(String world, String group, String permission)
/*     */   {
/*     */     try
/*     */     {
/* 144 */       PermissionBase permGroup = this.manager.getGroup(group);
/* 145 */       permGroup.remPerm(permission, world);
/* 146 */       return true;
/*     */     } catch (IOException ex) {
/* 148 */       this.plugin.getLogger().log(Level.SEVERE, String.format("[%s] An error occured while saving perms", new Object[] { this.totalperms.getDescription().getName() }), ex);
/*     */     }
/* 150 */     return false;
/*     */   }
/*     */ 
/*     */   public boolean playerInGroup(String world, String player, String group)
/*     */   {
/* 156 */     PermissionUser user = this.manager.getUser(player);
/* 157 */     List groups = user.getGroups(world);
/* 158 */     return groups.contains(group);
/*     */   }
/*     */ 
/*     */   public boolean playerAddGroup(String world, String player, String group)
/*     */   {
/*     */     try {
/* 164 */       PermissionUser user = this.manager.getUser(player);
/* 165 */       user.addGroup(group, world);
/* 166 */       return true;
/*     */     } catch (IOException ex) {
/* 168 */       this.plugin.getLogger().log(Level.SEVERE, String.format("[%s] An error occured while saving perms", new Object[] { this.totalperms.getDescription().getName() }), ex);
/*     */     }
/* 170 */     return false;
/*     */   }
/*     */ 
/*     */   public boolean playerRemoveGroup(String world, String player, String group)
/*     */   {
/*     */     try
/*     */     {
/* 177 */       PermissionUser user = this.manager.getUser(player);
/* 178 */       user.remGroup(group, world);
/* 179 */       return true;
/*     */     } catch (IOException ex) {
/* 181 */       this.plugin.getLogger().log(Level.SEVERE, String.format("[%s] An error occured while saving perms", new Object[] { this.totalperms.getDescription().getName() }), ex);
/*     */     }
/* 183 */     return false;
/*     */   }
/*     */ 
/*     */   public String[] getPlayerGroups(String world, String player)
/*     */   {
/* 189 */     PermissionUser user = this.manager.getUser(player);
/* 190 */     List groups = user.getGroups(world);
/* 191 */     if (groups == null) {
/* 192 */       groups = new ArrayList();
/*     */     }
/* 194 */     return (String[])groups.toArray(new String[groups.size()]);
/*     */   }
/*     */ 
/*     */   public String getPrimaryGroup(String world, String player)
/*     */   {
/* 199 */     String[] groups = getPlayerGroups(world, player);
/* 200 */     if (groups.length == 0) {
/* 201 */       return "";
/*     */     }
/* 203 */     return groups[0];
/*     */   }
/*     */ 
/*     */   public String[] getGroups()
/*     */   {
/* 209 */     return this.manager.getGroups();
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
/*  48 */       if ((Permission_TotalPermissions.this.manager == null) || (Permission_TotalPermissions.this.totalperms == null)) {
/*  49 */         Plugin permPlugin = event.getPlugin();
/*  50 */         if (permPlugin.getDescription().getName().equals("TotalPermissions")) {
/*  51 */           Permission_TotalPermissions.this.totalperms = ((TotalPermissions)permPlugin);
/*  52 */           Permission_TotalPermissions.this.manager = Permission_TotalPermissions.this.totalperms.getManager();
/*  53 */           Permission_TotalPermissions.log.info(String.format("[%s][Permission] %s hooked.", new Object[] { Permission_TotalPermissions.this.plugin.getDescription().getName(), "TotalPermissions" }));
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/*     */     @EventHandler(priority=EventPriority.MONITOR)
/*     */     public void onPluginDisable(PluginDisableEvent event) {
/*  60 */       if ((Permission_TotalPermissions.this.manager != null) && 
/*  61 */         (event.getPlugin().getDescription().getName().equals("TotalPermissions"))) {
/*  62 */         Permission_TotalPermissions.this.totalperms = null;
/*  63 */         Permission_TotalPermissions.this.manager = null;
/*  64 */         Permission_TotalPermissions.log.info(String.format("[%s][Permission] %s un-hooked.", new Object[] { Permission_TotalPermissions.this.plugin.getDescription().getName(), "TotalPermissions" }));
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\Vault.jar
 * Qualified Name:     net.milkbowl.vault.permission.plugins.Permission_TotalPermissions
 * JD-Core Version:    0.6.2
 */