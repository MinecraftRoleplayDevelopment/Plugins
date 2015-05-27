/*     */ package net.milkbowl.vault.permission.plugins;
/*     */ 
/*     */ import com.lightniinja.kperms.KGroup;
/*     */ import com.lightniinja.kperms.KPermsPlugin;
/*     */ import com.lightniinja.kperms.KPlayer;
/*     */ import com.lightniinja.kperms.Utilities;
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
/*     */ public class Permission_KPerms extends Permission
/*     */ {
/*     */   private final Plugin vault;
/*  38 */   private KPermsPlugin kperms = null;
/*     */ 
/*     */   public Permission_KPerms(Plugin plugin)
/*     */   {
/*  42 */     this.vault = plugin;
/*  43 */     Bukkit.getServer().getPluginManager().registerEvents(new PermissionServerListener(this), this.vault);
/*  44 */     if (this.kperms == null) {
/*  45 */       Plugin perms = plugin.getServer().getPluginManager().getPlugin("KPerms");
/*  46 */       if ((perms != null) && (perms.isEnabled())) {
/*  47 */         this.kperms = ((KPermsPlugin)perms);
/*  48 */         plugin.getLogger().info(String.format("[%s][Permission] %s hooked.", new Object[] { plugin.getDescription().getName(), "KPerms" }));
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public String getName()
/*     */   {
/*  84 */     return "KPerms";
/*     */   }
/*     */ 
/*     */   public boolean isEnabled()
/*     */   {
/*  89 */     return this.kperms.isEnabled();
/*     */   }
/*     */ 
/*     */   public boolean hasSuperPermsCompat()
/*     */   {
/*  94 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean hasGroupSupport()
/*     */   {
/*  99 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean playerHas(String world, String player, String permission)
/*     */   {
/* 104 */     return new KPlayer(player, this.kperms).hasPermission(permission);
/*     */   }
/*     */ 
/*     */   public boolean playerAdd(String world, String player, String permission)
/*     */   {
/* 109 */     return new KPlayer(player, this.kperms).addPermission(permission);
/*     */   }
/*     */ 
/*     */   public boolean playerRemove(String world, String player, String permission)
/*     */   {
/* 114 */     return new KPlayer(player, this.kperms).removePermission(permission);
/*     */   }
/*     */ 
/*     */   public boolean groupHas(String world, String group, String permission)
/*     */   {
/* 119 */     return new KGroup(group, this.kperms).hasPermission(permission);
/*     */   }
/*     */ 
/*     */   public boolean groupAdd(String world, String group, String permission)
/*     */   {
/* 124 */     return new KGroup(group, this.kperms).addPermission(permission);
/*     */   }
/*     */ 
/*     */   public boolean groupRemove(String world, String group, String permission)
/*     */   {
/* 129 */     return new KGroup(group, this.kperms).removePermission(permission);
/*     */   }
/*     */ 
/*     */   public boolean playerInGroup(String world, String player, String group)
/*     */   {
/* 134 */     return new KPlayer(player, this.kperms).isMemberOfGroup(group);
/*     */   }
/*     */ 
/*     */   public boolean playerAddGroup(String world, String player, String group)
/*     */   {
/* 139 */     return new KPlayer(player, this.kperms).addGroup(group);
/*     */   }
/*     */ 
/*     */   public boolean playerRemoveGroup(String world, String player, String group)
/*     */   {
/* 144 */     return new KPlayer(player, this.kperms).removeGroup(group);
/*     */   }
/*     */ 
/*     */   public String[] getPlayerGroups(String world, String player)
/*     */   {
/* 149 */     List groups = new KPlayer(player, this.kperms).getGroups();
/* 150 */     String[] gr = new String[groups.size()];
/* 151 */     gr = (String[])groups.toArray(gr);
/* 152 */     return gr;
/*     */   }
/*     */ 
/*     */   public String getPrimaryGroup(String world, String player)
/*     */   {
/* 157 */     return new KPlayer(player, this.kperms).getPrimaryGroup();
/*     */   }
/*     */ 
/*     */   public String[] getGroups()
/*     */   {
/* 162 */     return new Utilities(this.kperms).getGroups();
/*     */   }
/*     */ 
/*     */   private class PermissionServerListener
/*     */     implements Listener
/*     */   {
/*     */     private final Permission_KPerms bridge;
/*     */ 
/*     */     public PermissionServerListener(Permission_KPerms bridge)
/*     */     {
/*  57 */       this.bridge = bridge;
/*     */     }
/*     */ 
/*     */     @EventHandler(priority=EventPriority.MONITOR)
/*     */     public void onPluginEnable(PluginEnableEvent event) {
/*  62 */       if (this.bridge.kperms == null) {
/*  63 */         Plugin plugin = event.getPlugin();
/*  64 */         if (plugin.getDescription().getName().equals("KPerms")) {
/*  65 */           this.bridge.kperms = ((KPermsPlugin)plugin);
/*  66 */           Permission_KPerms.log.info(String.format("[%s][Permission] %s hooked.", new Object[] { Permission_KPerms.this.vault.getDescription().getName(), "KPerms" }));
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/*     */     @EventHandler(priority=EventPriority.MONITOR)
/*     */     public void onPluginDisable(PluginDisableEvent event) {
/*  73 */       if ((this.bridge.kperms != null) && 
/*  74 */         (event.getPlugin().getDescription().getName().equals(this.bridge.kperms.getName()))) {
/*  75 */         this.bridge.kperms = null;
/*  76 */         Permission_KPerms.log.info(String.format("[%s][Permission] %s un-hooked.", new Object[] { Permission_KPerms.this.vault.getDescription().getName(), "KPerms" }));
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\Vault.jar
 * Qualified Name:     net.milkbowl.vault.permission.plugins.Permission_KPerms
 * JD-Core Version:    0.6.2
 */