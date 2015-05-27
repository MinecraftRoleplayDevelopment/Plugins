/*     */ package net.milkbowl.vault.permission.plugins;
/*     */ 
/*     */ import com.platymuus.bukkit.permissions.Group;
/*     */ import com.platymuus.bukkit.permissions.PermissionInfo;
/*     */ import com.platymuus.bukkit.permissions.PermissionsPlugin;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.logging.Logger;
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
/*     */ public class Permission_PermissionsBukkit extends Permission
/*     */ {
/*  37 */   private final String name = "PermissionsBukkit";
/*  38 */   private PermissionsPlugin perms = null;
/*     */ 
/*     */   public Permission_PermissionsBukkit(Plugin plugin) {
/*  41 */     this.plugin = plugin;
/*  42 */     Bukkit.getServer().getPluginManager().registerEvents(new PermissionServerListener(this), plugin);
/*     */ 
/*  45 */     if (this.perms == null) {
/*  46 */       Plugin perms = plugin.getServer().getPluginManager().getPlugin("PermissionsBukkit");
/*  47 */       if (perms != null) {
/*  48 */         this.perms = ((PermissionsPlugin)perms);
/*  49 */         log.info(String.format("[%s][Permission] %s hooked.", new Object[] { plugin.getDescription().getName(), "PermissionsBukkit" }));
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public String getName()
/*     */   {
/*  85 */     return "PermissionsBukkit";
/*     */   }
/*     */ 
/*     */   public boolean isEnabled()
/*     */   {
/*  90 */     if (this.perms == null) {
/*  91 */       return false;
/*     */     }
/*  93 */     return this.perms.isEnabled();
/*     */   }
/*     */ 
/*     */   public boolean playerHas(String world, String player, String permission)
/*     */   {
/*  99 */     if (Bukkit.getPlayer(player) != null) {
/* 100 */       return Bukkit.getPlayer(player).hasPermission(permission);
/*     */     }
/* 102 */     return false;
/*     */   }
/*     */ 
/*     */   public boolean playerAdd(String world, String player, String permission)
/*     */   {
/* 108 */     if (world != null) {
/* 109 */       permission = world + ":" + permission;
/*     */     }
/* 111 */     return this.plugin.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "permissions player setperm " + player + " " + permission + " true");
/*     */   }
/*     */ 
/*     */   public boolean playerRemove(String world, String player, String permission)
/*     */   {
/* 116 */     if (world != null) {
/* 117 */       permission = world + ":" + permission;
/*     */     }
/* 119 */     return this.plugin.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "permissions player unsetperm " + player + " " + permission);
/*     */   }
/*     */ 
/*     */   public boolean groupHas(String world, String group, String permission)
/*     */   {
/* 126 */     if ((world != null) && (!world.isEmpty())) {
/* 127 */       return this.perms.getGroup(group).getInfo().getWorldPermissions(world).get(permission) == null ? false : ((Boolean)this.perms.getGroup(group).getInfo().getWorldPermissions(world).get(permission)).booleanValue();
/*     */     }
/* 129 */     if (this.perms.getGroup(group) == null)
/* 130 */       return false;
/* 131 */     if (this.perms.getGroup(group).getInfo() == null)
/* 132 */       return false;
/* 133 */     if (this.perms.getGroup(group).getInfo().getPermissions() == null) {
/* 134 */       return false;
/*     */     }
/* 136 */     return ((Boolean)this.perms.getGroup(group).getInfo().getPermissions().get(permission)).booleanValue();
/*     */   }
/*     */ 
/*     */   public boolean groupAdd(String world, String group, String permission)
/*     */   {
/* 141 */     if (world != null) {
/* 142 */       permission = world + ":" + permission;
/*     */     }
/* 144 */     return this.plugin.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "permissions group setperm " + group + " " + permission + " true");
/*     */   }
/*     */ 
/*     */   public boolean groupRemove(String world, String group, String permission)
/*     */   {
/* 149 */     if (world != null) {
/* 150 */       permission = world + ":" + permission;
/*     */     }
/* 152 */     return this.plugin.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "permissions group unsetperm " + group + " " + permission);
/*     */   }
/*     */ 
/*     */   public boolean playerInGroup(String world, String player, String group)
/*     */   {
/* 157 */     if (world != null) {
/* 158 */       for (Group g : this.perms.getPlayerInfo(player).getGroups()) {
/* 159 */         if (g.getName().equals(group)) {
/* 160 */           return g.getInfo().getWorlds().contains(world);
/*     */         }
/*     */       }
/* 163 */       return false;
/*     */     }
/* 165 */     Group g = this.perms.getGroup(group);
/* 166 */     if (g == null) {
/* 167 */       return false;
/*     */     }
/* 169 */     return g.getPlayers().contains(player);
/*     */   }
/*     */ 
/*     */   public boolean playerAddGroup(String world, String player, String group)
/*     */   {
/* 174 */     if (world != null) {
/* 175 */       return false;
/*     */     }
/* 177 */     return this.plugin.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "permissions player addgroup " + player + " " + group);
/*     */   }
/*     */ 
/*     */   public boolean playerRemoveGroup(String world, String player, String group)
/*     */   {
/* 182 */     if (world != null) {
/* 183 */       return false;
/*     */     }
/* 185 */     return this.plugin.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "permissions player removegroup " + player + " " + group);
/*     */   }
/*     */ 
/*     */   public String[] getPlayerGroups(String world, String player)
/*     */   {
/* 190 */     List groupList = new ArrayList();
/* 191 */     PermissionInfo info = this.perms.getPlayerInfo(player);
/* 192 */     if ((world != null) && (info != null)) {
/* 193 */       for (Group group : this.perms.getPlayerInfo(player).getGroups()) {
/* 194 */         if (group.getInfo().getWorlds().contains(world)) {
/* 195 */           groupList.add(group.getName());
/*     */         }
/*     */       }
/* 198 */       return (String[])groupList.toArray(new String[0]);
/*     */     }
/* 200 */     if (info != null) {
/* 201 */       for (Group group : info.getGroups()) {
/* 202 */         groupList.add(group.getName());
/*     */       }
/*     */     }
/* 205 */     return (String[])groupList.toArray(new String[0]);
/*     */   }
/*     */ 
/*     */   public String getPrimaryGroup(String world, String player)
/*     */   {
/* 210 */     if (this.perms.getPlayerInfo(player) == null)
/* 211 */       return null;
/* 212 */     if ((this.perms.getPlayerInfo(player).getGroups() != null) && (!this.perms.getPlayerInfo(player).getGroups().isEmpty())) {
/* 213 */       return ((Group)this.perms.getPlayerInfo(player).getGroups().get(0)).getName();
/*     */     }
/* 215 */     return null;
/*     */   }
/*     */ 
/*     */   public String[] getGroups()
/*     */   {
/* 220 */     List groupNames = new ArrayList();
/* 221 */     for (Group group : this.perms.getAllGroups()) {
/* 222 */       groupNames.add(group.getName());
/*     */     }
/*     */ 
/* 225 */     return (String[])groupNames.toArray(new String[0]);
/*     */   }
/*     */ 
/*     */   public boolean hasSuperPermsCompat()
/*     */   {
/* 230 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean hasGroupSupport()
/*     */   {
/* 235 */     return true;
/*     */   }
/*     */ 
/*     */   public class PermissionServerListener
/*     */     implements Listener
/*     */   {
/*  55 */     Permission_PermissionsBukkit permission = null;
/*     */ 
/*     */     public PermissionServerListener(Permission_PermissionsBukkit permission) {
/*  58 */       this.permission = permission;
/*     */     }
/*     */ 
/*     */     @EventHandler(priority=EventPriority.MONITOR)
/*     */     public void onPluginEnable(PluginEnableEvent event) {
/*  63 */       if (this.permission.perms == null) {
/*  64 */         Plugin perms = event.getPlugin();
/*  65 */         if (perms.getDescription().getName().equals("PermissionsBukkit")) {
/*  66 */           this.permission.perms = ((PermissionsPlugin)perms);
/*  67 */           Permission_PermissionsBukkit.log.info(String.format("[%s][Permission] %s hooked.", new Object[] { Permission_PermissionsBukkit.this.plugin.getDescription().getName(), "PermissionsBukkit" }));
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/*     */     @EventHandler(priority=EventPriority.MONITOR)
/*     */     public void onPluginDisable(PluginDisableEvent event) {
/*  74 */       if ((this.permission.perms != null) && 
/*  75 */         (event.getPlugin().getDescription().getName().equals("PermissionsBukkit"))) {
/*  76 */         this.permission.perms = null;
/*  77 */         Permission_PermissionsBukkit.log.info(String.format("[%s][Permission] %s un-hooked.", new Object[] { Permission_PermissionsBukkit.this.plugin.getDescription().getName(), "PermissionsBukkit" }));
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\Vault.jar
 * Qualified Name:     net.milkbowl.vault.permission.plugins.Permission_PermissionsBukkit
 * JD-Core Version:    0.6.2
 */