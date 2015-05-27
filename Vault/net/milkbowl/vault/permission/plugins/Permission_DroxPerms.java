/*     */ package net.milkbowl.vault.permission.plugins;
/*     */ 
/*     */ import de.hydrox.bukkit.DroxPerms.DroxPerms;
/*     */ import de.hydrox.bukkit.DroxPerms.DroxPermsAPI;
/*     */ import java.util.ArrayList;
/*     */ import java.util.logging.Logger;
/*     */ import net.milkbowl.vault.permission.Permission;
/*     */ import org.bukkit.Bukkit;
/*     */ import org.bukkit.Server;
/*     */ import org.bukkit.configuration.file.FileConfiguration;
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
/*     */ public class Permission_DroxPerms extends Permission
/*     */ {
/*  21 */   private final String name = "DroxPerms";
/*     */   private DroxPermsAPI API;
/*     */   private boolean useOnlySubgroups;
/*     */ 
/*     */   public Permission_DroxPerms(Plugin plugin)
/*     */   {
/*  26 */     this.plugin = plugin;
/*     */ 
/*  29 */     if (this.API == null) {
/*  30 */       DroxPerms p = (DroxPerms)plugin.getServer().getPluginManager().getPlugin("DroxPerms");
/*  31 */       if (p != null) {
/*  32 */         this.API = p.getAPI();
/*  33 */         log.info(String.format("[%s][Permission] %s hooked.", new Object[] { plugin.getDescription().getName(), "DroxPerms" }));
/*  34 */         this.useOnlySubgroups = p.getConfig().getBoolean("Vault.useOnlySubgroups", true);
/*  35 */         log.info(String.format("[%s][Permission] Vault.useOnlySubgroups: %s", new Object[] { plugin.getDescription().getName(), Boolean.valueOf(this.useOnlySubgroups) }));
/*     */       }
/*     */     }
/*     */ 
/*  39 */     Bukkit.getServer().getPluginManager().registerEvents(new PermissionServerListener(), plugin);
/*     */   }
/*     */ 
/*     */   public String getName()
/*     */   {
/*  67 */     getClass(); return "DroxPerms";
/*     */   }
/*     */ 
/*     */   public boolean isEnabled()
/*     */   {
/*  72 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean hasSuperPermsCompat()
/*     */   {
/*  77 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean playerHas(String world, String player, String permission)
/*     */   {
/*  82 */     Player p = this.plugin.getServer().getPlayer(player);
/*  83 */     return p != null ? p.hasPermission(permission) : false;
/*     */   }
/*     */ 
/*     */   public boolean playerAdd(String world, String player, String permission)
/*     */   {
/*  88 */     return this.API.addPlayerPermission(player, world, permission);
/*     */   }
/*     */ 
/*     */   public boolean playerRemove(String world, String player, String permission)
/*     */   {
/*  93 */     return this.API.removePlayerPermission(player, world, permission);
/*     */   }
/*     */ 
/*     */   public boolean groupHas(String world, String group, String permission)
/*     */   {
/*  98 */     return false;
/*     */   }
/*     */ 
/*     */   public boolean groupAdd(String world, String group, String permission)
/*     */   {
/* 103 */     return this.API.addGroupPermission(group, world, permission);
/*     */   }
/*     */ 
/*     */   public boolean groupRemove(String world, String group, String permission)
/*     */   {
/* 108 */     return this.API.removeGroupPermission(group, world, permission);
/*     */   }
/*     */ 
/*     */   public boolean playerInGroup(String world, String player, String group)
/*     */   {
/* 113 */     return (this.API.getPlayerGroup(player).equalsIgnoreCase(group)) || (this.API.getPlayerSubgroups(player).contains(group));
/*     */   }
/*     */ 
/*     */   public boolean playerAddGroup(String world, String player, String group)
/*     */   {
/* 118 */     if (this.useOnlySubgroups) {
/* 119 */       return this.API.addPlayerSubgroup(player, group);
/*     */     }
/* 121 */     if ("default".equalsIgnoreCase(this.API.getPlayerGroup(player))) {
/* 122 */       return this.API.setPlayerGroup(player, group);
/*     */     }
/* 124 */     return this.API.addPlayerSubgroup(player, group);
/*     */   }
/*     */ 
/*     */   public boolean playerRemoveGroup(String world, String player, String group)
/*     */   {
/* 131 */     if (this.useOnlySubgroups) {
/* 132 */       return this.API.removePlayerSubgroup(player, group);
/*     */     }
/* 134 */     if (group.equalsIgnoreCase(this.API.getPlayerGroup(player))) {
/* 135 */       return this.API.setPlayerGroup(player, "default");
/*     */     }
/* 137 */     return this.API.removePlayerSubgroup(player, group);
/*     */   }
/*     */ 
/*     */   public String[] getPlayerGroups(String world, String player)
/*     */   {
/* 144 */     ArrayList array = this.API.getPlayerSubgroups(player);
/* 145 */     array.add(this.API.getPlayerGroup(player));
/* 146 */     return (String[])array.toArray(new String[0]);
/*     */   }
/*     */ 
/*     */   public String getPrimaryGroup(String world, String player)
/*     */   {
/* 151 */     return this.API.getPlayerGroup(player);
/*     */   }
/*     */ 
/*     */   public String[] getGroups()
/*     */   {
/* 156 */     return this.API.getGroupNames();
/*     */   }
/*     */ 
/*     */   public boolean hasGroupSupport()
/*     */   {
/* 161 */     return true;
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
/*  45 */       if (Permission_DroxPerms.this.API == null) {
/*  46 */         Plugin permPlugin = event.getPlugin();
/*  47 */         if (permPlugin.getDescription().getName().equals("DroxPerms")) {
/*  48 */           Permission_DroxPerms.this.API = ((DroxPerms)permPlugin).getAPI();
/*  49 */           Permission_DroxPerms.log.info(String.format("[%s][Permission] %s hooked.", new Object[] { Permission_DroxPerms.this.plugin.getDescription().getName(), "DroxPerms" }));
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/*     */     @EventHandler(priority=EventPriority.MONITOR)
/*     */     public void onPluginDisable(PluginDisableEvent event) {
/*  56 */       if ((Permission_DroxPerms.this.API != null) && 
/*  57 */         (event.getPlugin().getDescription().getName().equals("DroxPerms"))) {
/*  58 */         Permission_DroxPerms.this.API = null;
/*  59 */         Permission_DroxPerms.log.info(String.format("[%s][Permission] %s un-hooked.", new Object[] { Permission_DroxPerms.this.plugin.getDescription().getName(), "DroxPerms" }));
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\Vault.jar
 * Qualified Name:     net.milkbowl.vault.permission.plugins.Permission_DroxPerms
 * JD-Core Version:    0.6.2
 */