/*     */ package net.milkbowl.vault.permission.plugins;
/*     */ 
/*     */ import com.github.sebc722.xperms.core.Main;
/*     */ import com.github.sebc722.xperms.permissions.Xgroup;
/*     */ import com.github.sebc722.xperms.permissions.Xplayer;
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
/*     */ public class Permission_Xperms extends Permission
/*     */ {
/*  32 */   private final String name = "Xperms";
/*  33 */   private Main perms = null;
/*     */ 
/*     */   public Permission_Xperms(Plugin plugin) {
/*  36 */     this.plugin = plugin;
/*  37 */     Bukkit.getServer().getPluginManager().registerEvents(new PermissionServerListener(this), plugin);
/*     */ 
/*  39 */     if (this.perms == null) {
/*  40 */       Plugin perms = plugin.getServer().getPluginManager().getPlugin("Xperms");
/*  41 */       if (this.perms != null) {
/*  42 */         if (perms.isEnabled()) {
/*     */           try {
/*  44 */             if (Double.valueOf(perms.getDescription().getVersion()).doubleValue() < 1.1D)
/*  45 */               log.info(String.format("[%s] [Permission] %s Current version is not compatible with vault! Please Update!", new Object[] { plugin.getDescription().getName(), "Xperms" }));
/*     */           }
/*     */           catch (NumberFormatException e)
/*     */           {
/*  49 */             log.info(String.format("[%s] [Permission] %s Current version is not compatibe with vault! Please Update!", new Object[] { plugin.getDescription().getName(), "Xperms" }));
/*     */           }
/*     */         }
/*  52 */         this.perms = ((Main)perms);
/*  53 */         log.info(String.format("[%s][Permission] %s hooked.", new Object[] { plugin.getDescription().getName(), "Xperms" }));
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public String getName()
/*     */   {
/*  97 */     return "Xperms";
/*     */   }
/*     */ 
/*     */   public boolean isEnabled()
/*     */   {
/* 102 */     return this.perms.isEnabled();
/*     */   }
/*     */ 
/*     */   public boolean hasSuperPermsCompat()
/*     */   {
/* 107 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean playerHas(String world, String player, String permission)
/*     */   {
/* 112 */     return this.perms.getXplayer().hasPerm(world, player, permission);
/*     */   }
/*     */ 
/*     */   public boolean playerAdd(String world, String player, String permission)
/*     */   {
/* 117 */     return this.perms.getXplayer().addNode(world, player, permission);
/*     */   }
/*     */ 
/*     */   public boolean playerRemove(String world, String player, String permission)
/*     */   {
/* 122 */     return this.perms.getXplayer().removeNode(world, player, permission);
/*     */   }
/*     */ 
/*     */   public boolean groupHas(String world, String group, String permission)
/*     */   {
/* 127 */     return this.perms.getXgroup().hasPerm(group, permission);
/*     */   }
/*     */ 
/*     */   public boolean groupAdd(String world, String group, String permission)
/*     */   {
/* 132 */     this.perms.getXgroup().addNode(group, permission);
/* 133 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean groupRemove(String world, String group, String permission)
/*     */   {
/* 138 */     return this.perms.getXgroup().removeNode(group, permission);
/*     */   }
/*     */ 
/*     */   public boolean playerInGroup(String world, String player, String group)
/*     */   {
/* 143 */     String groupForWorld = this.perms.getXplayer().getGroupForWorld(player, world);
/* 144 */     if (groupForWorld.equals(group)) {
/* 145 */       return true;
/*     */     }
/* 147 */     return false;
/*     */   }
/*     */ 
/*     */   public boolean playerAddGroup(String world, String player, String group)
/*     */   {
/* 152 */     return this.perms.getXplayer().setPlayerGroup(world, player, group);
/*     */   }
/*     */ 
/*     */   public boolean playerRemoveGroup(String world, String player, String group)
/*     */   {
/* 157 */     return this.perms.getXplayer().setPlayerDefault(world, player);
/*     */   }
/*     */ 
/*     */   public String[] getPlayerGroups(String world, String player)
/*     */   {
/* 162 */     return this.perms.getXplayer().getPlayerGroups(player);
/*     */   }
/*     */ 
/*     */   public String getPrimaryGroup(String world, String player)
/*     */   {
/* 167 */     return this.perms.getXplayer().getGroupForWorld(player, world);
/*     */   }
/*     */ 
/*     */   public String[] getGroups()
/*     */   {
/* 172 */     return this.perms.getXgroup().getGroups();
/*     */   }
/*     */ 
/*     */   public boolean hasGroupSupport()
/*     */   {
/* 177 */     return true;
/*     */   }
/*     */ 
/*     */   public class PermissionServerListener
/*     */     implements Listener
/*     */   {
/*  59 */     Permission_Xperms permission = null;
/*     */ 
/*     */     public PermissionServerListener(Permission_Xperms permission) {
/*  62 */       this.permission = permission;
/*     */     }
/*     */ 
/*     */     @EventHandler(priority=EventPriority.MONITOR)
/*     */     public void onPluginEnable(PluginEnableEvent event) {
/*  67 */       if (this.permission.perms == null) {
/*  68 */         Plugin perms = event.getPlugin();
/*  69 */         if (perms.getDescription().getName().equals("Xperms")) {
/*     */           try {
/*  71 */             if (Double.valueOf(perms.getDescription().getVersion()).doubleValue() < 1.1D)
/*  72 */               Permission_Xperms.log.info(String.format("[%s] [Permission] %s Current version is not compatible with vault! Please Update!", new Object[] { Permission_Xperms.this.plugin.getDescription().getName(), "Xperms" }));
/*     */           }
/*     */           catch (NumberFormatException e)
/*     */           {
/*  76 */             Permission_Xperms.log.info(String.format("[%s] [Permission] %s Current version is not compatibe with vault! Please Update!", new Object[] { Permission_Xperms.this.plugin.getDescription().getName(), "Xperms" }));
/*     */           }
/*  78 */           this.permission.perms = ((Main)perms);
/*  79 */           Permission_Xperms.log.info(String.format("[%s][Permission] %s hooked.", new Object[] { Permission_Xperms.this.plugin.getDescription().getName(), "Xperms" }));
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/*     */     @EventHandler(priority=EventPriority.MONITOR)
/*     */     public void onPluginDisable(PluginDisableEvent event) {
/*  86 */       if ((this.permission.perms != null) && 
/*  87 */         (event.getPlugin().getName().equals("Xperms"))) {
/*  88 */         this.permission.perms = null;
/*  89 */         Permission_Xperms.log.info(String.format("[%s][Permission] %s un-hooked.", new Object[] { Permission_Xperms.this.plugin.getDescription().getName(), "Xperms" }));
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\Vault.jar
 * Qualified Name:     net.milkbowl.vault.permission.plugins.Permission_Xperms
 * JD-Core Version:    0.6.2
 */