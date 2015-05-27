/*     */ package net.milkbowl.vault.permission.plugins;
/*     */ 
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
/*     */ import ru.simsonic.rscPermissions.MainPluginClass;
/*     */ import ru.simsonic.rscPermissions.rscpAPI;
/*     */ 
/*     */ public class Permission_rscPermissions extends Permission
/*     */ {
/*     */   private final Plugin vault;
/*  33 */   private MainPluginClass rscp = null;
/*  34 */   private rscpAPI rscpAPI = null;
/*     */ 
/*     */   public Permission_rscPermissions(Plugin plugin)
/*     */   {
/*  38 */     this.vault = plugin;
/*  39 */     Bukkit.getServer().getPluginManager().registerEvents(new PermissionServerListener(this), this.vault);
/*  40 */     if (this.rscp == null) {
/*  41 */       Plugin perms = plugin.getServer().getPluginManager().getPlugin("rscPermissions");
/*  42 */       if ((perms != null) && (perms.isEnabled())) {
/*  43 */         this.rscp = ((MainPluginClass)perms);
/*  44 */         this.rscpAPI = this.rscp.API;
/*  45 */         plugin.getLogger().info(String.format("[%s][Permission] %s hooked.", new Object[] { plugin.getDescription().getName(), "rscPermissions" }));
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public String getName()
/*     */   {
/*  83 */     return "rscPermissions";
/*     */   }
/*     */ 
/*     */   public boolean isEnabled()
/*     */   {
/*  88 */     return (this.rscpAPI != null) && (this.rscpAPI.isEnabled());
/*     */   }
/*     */ 
/*     */   public boolean hasSuperPermsCompat()
/*     */   {
/*  93 */     return this.rscpAPI.hasSuperPermsCompat();
/*     */   }
/*     */ 
/*     */   public boolean hasGroupSupport()
/*     */   {
/*  98 */     return this.rscpAPI.hasGroupSupport();
/*     */   }
/*     */ 
/*     */   public boolean playerHas(String world, String player, String permission)
/*     */   {
/* 103 */     return this.rscpAPI.playerHas(world, player, permission);
/*     */   }
/*     */ 
/*     */   public boolean playerAdd(String world, String player, String permission)
/*     */   {
/* 108 */     return this.rscpAPI.playerAdd(world, player, permission);
/*     */   }
/*     */ 
/*     */   public boolean playerRemove(String world, String player, String permission)
/*     */   {
/* 113 */     return this.rscpAPI.playerRemove(world, player, permission);
/*     */   }
/*     */ 
/*     */   public boolean groupHas(String world, String group, String permission)
/*     */   {
/* 118 */     return this.rscpAPI.groupHas(world, group, permission);
/*     */   }
/*     */ 
/*     */   public boolean groupAdd(String world, String group, String permission)
/*     */   {
/* 123 */     return this.rscpAPI.groupAdd(world, group, permission);
/*     */   }
/*     */ 
/*     */   public boolean groupRemove(String world, String group, String permission)
/*     */   {
/* 128 */     return this.rscpAPI.groupRemove(world, group, permission);
/*     */   }
/*     */ 
/*     */   public boolean playerInGroup(String world, String player, String group)
/*     */   {
/* 133 */     return this.rscpAPI.playerInGroup(world, player, group);
/*     */   }
/*     */ 
/*     */   public boolean playerAddGroup(String world, String player, String group)
/*     */   {
/* 138 */     return this.rscpAPI.playerAddGroup(world, player, group);
/*     */   }
/*     */ 
/*     */   public boolean playerRemoveGroup(String world, String player, String group)
/*     */   {
/* 143 */     return this.rscpAPI.playerRemoveGroup(world, player, group);
/*     */   }
/*     */ 
/*     */   public String[] getPlayerGroups(String world, String player)
/*     */   {
/* 148 */     return this.rscpAPI.getPlayerGroups(world, player);
/*     */   }
/*     */ 
/*     */   public String getPrimaryGroup(String world, String player)
/*     */   {
/* 153 */     return this.rscpAPI.getPrimaryGroup(world, player);
/*     */   }
/*     */ 
/*     */   public String[] getGroups()
/*     */   {
/* 158 */     return this.rscpAPI.getGroups();
/*     */   }
/*     */ 
/*     */   private class PermissionServerListener
/*     */     implements Listener
/*     */   {
/*     */     private final Permission_rscPermissions bridge;
/*     */ 
/*     */     public PermissionServerListener(Permission_rscPermissions bridge)
/*     */     {
/*  54 */       this.bridge = bridge;
/*     */     }
/*     */ 
/*     */     @EventHandler(priority=EventPriority.MONITOR)
/*     */     public void onPluginEnable(PluginEnableEvent event) {
/*  59 */       if (this.bridge.rscp == null) {
/*  60 */         Plugin plugin = event.getPlugin();
/*  61 */         if (plugin.getDescription().getName().equals("rscPermissions")) {
/*  62 */           this.bridge.rscp = ((MainPluginClass)plugin);
/*  63 */           this.bridge.rscpAPI = this.bridge.rscp.API;
/*  64 */           Permission_rscPermissions.log.info(String.format("[%s][Permission] %s hooked.", new Object[] { Permission_rscPermissions.this.vault.getDescription().getName(), "rscPermissions" }));
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/*     */     @EventHandler(priority=EventPriority.MONITOR)
/*     */     public void onPluginDisable(PluginDisableEvent event) {
/*  71 */       if ((this.bridge.rscpAPI != null) && 
/*  72 */         (event.getPlugin().getDescription().getName().equals(this.bridge.rscpAPI.getName()))) {
/*  73 */         this.bridge.rscpAPI = null;
/*  74 */         this.bridge.rscp = null;
/*  75 */         Permission_rscPermissions.log.info(String.format("[%s][Permission] %s un-hooked.", new Object[] { Permission_rscPermissions.this.vault.getDescription().getName(), "rscPermissions" }));
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\Vault.jar
 * Qualified Name:     net.milkbowl.vault.permission.plugins.Permission_rscPermissions
 * JD-Core Version:    0.6.2
 */