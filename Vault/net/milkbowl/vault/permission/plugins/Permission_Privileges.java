/*     */ package net.milkbowl.vault.permission.plugins;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.logging.Logger;
/*     */ import net.krinsoft.privileges.Privileges;
/*     */ import net.krinsoft.privileges.groups.Group;
/*     */ import net.krinsoft.privileges.groups.GroupManager;
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
/*     */ 
/*     */ public class Permission_Privileges extends Permission
/*     */ {
/*  21 */   private final String name = "Privileges";
/*     */   private Privileges privs;
/*     */ 
/*     */   public Permission_Privileges(Plugin plugin)
/*     */   {
/*  25 */     this.plugin = plugin;
/*  26 */     Bukkit.getServer().getPluginManager().registerEvents(new PermissionServerListener(this), plugin);
/*     */ 
/*  28 */     if (this.privs == null) {
/*  29 */       Plugin perms = plugin.getServer().getPluginManager().getPlugin("Privileges");
/*  30 */       if ((perms != null) && (perms.isEnabled())) {
/*  31 */         this.privs = ((Privileges)perms);
/*  32 */         log.info(String.format("[%s][Permission] %s hooked.", new Object[] { plugin.getDescription().getName(), "Privileges" }));
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public String getName()
/*     */   {
/*  68 */     return "Privileges";
/*     */   }
/*     */ 
/*     */   public boolean isEnabled()
/*     */   {
/*  73 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean playerHas(String world, String player, String permission)
/*     */   {
/*  78 */     Player p = this.plugin.getServer().getPlayer(player);
/*  79 */     return (p != null) && (p.hasPermission(permission));
/*     */   }
/*     */ 
/*     */   public boolean playerAdd(String world, String player, String permission)
/*     */   {
/*  84 */     return false;
/*     */   }
/*     */ 
/*     */   public boolean playerRemove(String world, String player, String permission)
/*     */   {
/*  91 */     return false;
/*     */   }
/*     */ 
/*     */   public boolean groupHas(String world, String group, String permission)
/*     */   {
/*  96 */     Group g = this.privs.getGroupManager().getGroup(group);
/*  97 */     return (g != null) && (g.hasPermission(permission, world));
/*     */   }
/*     */ 
/*     */   public boolean groupAdd(String world, String group, String permission)
/*     */   {
/* 102 */     Group g = this.privs.getGroupManager().getGroup(group);
/* 103 */     return (g != null) && (g.addPermission(world, permission));
/*     */   }
/*     */ 
/*     */   public boolean groupRemove(String world, String group, String permission)
/*     */   {
/* 108 */     Group g = this.privs.getGroupManager().getGroup(group);
/* 109 */     return (g != null) && (g.removePermission(world, permission));
/*     */   }
/*     */ 
/*     */   public boolean playerInGroup(String world, String player, String group)
/*     */   {
/* 114 */     OfflinePlayer p = Bukkit.getOfflinePlayer(player);
/* 115 */     Group g = this.privs.getGroupManager().getGroup(p);
/* 116 */     return (g != null) && (g.isMemberOf(group));
/*     */   }
/*     */ 
/*     */   public boolean playerAddGroup(String world, String player, String group)
/*     */   {
/* 121 */     Group g = this.privs.getGroupManager().setGroup(player, group);
/* 122 */     return g != null;
/*     */   }
/*     */ 
/*     */   public boolean playerRemoveGroup(String world, String player, String group)
/*     */   {
/* 127 */     Group g = this.privs.getGroupManager().getDefaultGroup();
/* 128 */     return (g != null) && (playerAddGroup(world, player, g.getName()));
/*     */   }
/*     */ 
/*     */   public String[] getPlayerGroups(String world, String player)
/*     */   {
/* 133 */     OfflinePlayer p = Bukkit.getOfflinePlayer(player);
/* 134 */     if (p == null) {
/* 135 */       throw new UnsupportedOperationException("Privileges does not support offline players.");
/*     */     }
/* 137 */     Group g = this.privs.getGroupManager().getGroup(p);
/* 138 */     return g != null ? (String[])g.getGroupTree().toArray(new String[g.getGroupTree().size()]) : null;
/*     */   }
/*     */ 
/*     */   public String getPrimaryGroup(String world, String player)
/*     */   {
/* 143 */     OfflinePlayer p = Bukkit.getOfflinePlayer(player);
/* 144 */     Group g = this.privs.getGroupManager().getGroup(p);
/* 145 */     return g != null ? g.getName() : null;
/*     */   }
/*     */ 
/*     */   public String[] getGroups()
/*     */   {
/* 150 */     List groups = new ArrayList();
/* 151 */     for (Group g : this.privs.getGroupManager().getGroups()) {
/* 152 */       groups.add(g.getName());
/*     */     }
/* 154 */     return (String[])groups.toArray(new String[groups.size()]);
/*     */   }
/*     */ 
/*     */   public boolean hasSuperPermsCompat()
/*     */   {
/* 159 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean hasGroupSupport()
/*     */   {
/* 164 */     return true;
/*     */   }
/*     */ 
/*     */   public class PermissionServerListener
/*     */     implements Listener
/*     */   {
/*  38 */     Permission_Privileges permission = null;
/*     */ 
/*     */     public PermissionServerListener(Permission_Privileges permission) {
/*  41 */       this.permission = permission;
/*     */     }
/*     */ 
/*     */     @EventHandler(priority=EventPriority.MONITOR)
/*     */     public void onPluginEnable(PluginEnableEvent event) {
/*  46 */       if (this.permission.privs == null) {
/*  47 */         Plugin perms = event.getPlugin();
/*  48 */         if (perms.getDescription().getName().equals("Privileges")) {
/*  49 */           this.permission.privs = ((Privileges)perms);
/*  50 */           Permission_Privileges.log.info(String.format("[%s][Permission] %s hooked.", new Object[] { Permission_Privileges.this.plugin.getDescription().getName(), "Privileges" }));
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/*     */     @EventHandler(priority=EventPriority.MONITOR)
/*     */     public void onPluginDisable(PluginDisableEvent event) {
/*  57 */       if ((this.permission.privs != null) && 
/*  58 */         (event.getPlugin().getDescription().getName().equals("Privileges"))) {
/*  59 */         this.permission.privs = null;
/*  60 */         Permission_Privileges.log.info(String.format("[%s][Permission] %s un-hooked.", new Object[] { Permission_Privileges.this.plugin.getDescription().getName(), "Privileges" }));
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\Vault.jar
 * Qualified Name:     net.milkbowl.vault.permission.plugins.Permission_Privileges
 * JD-Core Version:    0.6.2
 */