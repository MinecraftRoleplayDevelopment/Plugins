/*     */ package net.milkbowl.vault.permission.plugins;
/*     */ 
/*     */ import de.bananaco.bpermissions.api.ApiLayer;
/*     */ import de.bananaco.bpermissions.api.Calculable;
/*     */ import de.bananaco.bpermissions.api.CalculableType;
/*     */ import de.bananaco.bpermissions.api.WorldManager;
/*     */ import java.util.HashSet;
/*     */ import java.util.Set;
/*     */ import java.util.logging.Logger;
/*     */ import org.bukkit.Bukkit;
/*     */ import org.bukkit.Server;
/*     */ import org.bukkit.command.CommandSender;
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
/*     */ public class Permission_bPermissions2 extends net.milkbowl.vault.permission.Permission
/*     */ {
/*  41 */   private final String name = "bPermissions2";
/*  42 */   private boolean hooked = false;
/*     */ 
/*     */   public Permission_bPermissions2(Plugin plugin) {
/*  45 */     this.plugin = plugin;
/*     */ 
/*  47 */     Bukkit.getServer().getPluginManager().registerEvents(new PermissionServerListener(), plugin);
/*     */ 
/*  50 */     if (!this.hooked) {
/*  51 */       Plugin p = plugin.getServer().getPluginManager().getPlugin("bPermissions");
/*  52 */       if (p != null) {
/*  53 */         this.hooked = true;
/*  54 */         log.info(String.format("[%s][Permission] %s hooked.", new Object[] { plugin.getDescription().getName(), "bPermissions2" }));
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public String getName()
/*     */   {
/*  85 */     return "bPermissions2";
/*     */   }
/*     */ 
/*     */   public boolean isEnabled()
/*     */   {
/*  90 */     return this.hooked;
/*     */   }
/*     */ 
/*     */   public boolean has(Player player, String permission)
/*     */   {
/*  95 */     return playerHas(player.getWorld().getName(), player.getName(), permission);
/*     */   }
/*     */ 
/*     */   public boolean has(String world, String player, String permission)
/*     */   {
/* 100 */     return playerHas(world, player, permission);
/*     */   }
/*     */ 
/*     */   public boolean has(CommandSender sender, String permission)
/*     */   {
/* 105 */     if ((sender instanceof Player)) {
/* 106 */       Player player = (Player)sender;
/* 107 */       return has(player, permission);
/*     */     }
/* 109 */     return sender.hasPermission(permission);
/*     */   }
/*     */ 
/*     */   public boolean has(org.bukkit.World world, String player, String permission)
/*     */   {
/* 114 */     return playerHas(world.getName(), player, permission);
/*     */   }
/*     */ 
/*     */   public boolean playerHas(String world, String player, String permission)
/*     */   {
/* 119 */     return ApiLayer.hasPermission(world, CalculableType.USER, player, permission);
/*     */   }
/*     */ 
/*     */   public boolean playerAdd(String world, String player, String permission)
/*     */   {
/* 124 */     ApiLayer.addPermission(world, CalculableType.USER, player, de.bananaco.bpermissions.api.Permission.loadFromString(permission));
/* 125 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean playerRemove(String world, String player, String permission)
/*     */   {
/* 130 */     ApiLayer.removePermission(world, CalculableType.USER, player, permission);
/* 131 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean groupHas(String world, String group, String permission)
/*     */   {
/* 138 */     return ApiLayer.hasPermission(world, CalculableType.GROUP, group, permission);
/*     */   }
/*     */ 
/*     */   public boolean groupAdd(String world, String group, String permission)
/*     */   {
/* 143 */     ApiLayer.addPermission(world, CalculableType.GROUP, group, de.bananaco.bpermissions.api.Permission.loadFromString(permission));
/* 144 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean groupRemove(String world, String group, String permission)
/*     */   {
/* 149 */     ApiLayer.removePermission(world, CalculableType.GROUP, group, permission);
/* 150 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean playerInGroup(String world, String player, String group)
/*     */   {
/* 155 */     return ApiLayer.hasGroup(world, CalculableType.USER, player, group);
/*     */   }
/*     */ 
/*     */   public boolean playerAddGroup(String world, String player, String group)
/*     */   {
/* 160 */     ApiLayer.addGroup(world, CalculableType.USER, player, group);
/* 161 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean playerRemoveGroup(String world, String player, String group)
/*     */   {
/* 166 */     ApiLayer.removeGroup(world, CalculableType.USER, player, group);
/* 167 */     return true;
/*     */   }
/*     */ 
/*     */   public String[] getPlayerGroups(String world, String player)
/*     */   {
/* 172 */     return ApiLayer.getGroups(world, CalculableType.USER, player);
/*     */   }
/*     */ 
/*     */   public String getPrimaryGroup(String world, String player)
/*     */   {
/* 177 */     String[] groups = getPlayerGroups(world, player);
/* 178 */     return (groups != null) && (groups.length > 0) ? groups[0] : null;
/*     */   }
/*     */ 
/*     */   public String[] getGroups()
/*     */   {
/* 183 */     String[] groups = null;
/* 184 */     Set gSet = new HashSet();
/* 185 */     for (de.bananaco.bpermissions.api.World world : WorldManager.getInstance().getAllWorlds()) {
/* 186 */       Set gr = world.getAll(CalculableType.GROUP);
/* 187 */       for (Calculable c : gr) {
/* 188 */         gSet.add(c.getNameLowerCase());
/*     */       }
/*     */     }
/*     */ 
/* 192 */     groups = (String[])gSet.toArray(new String[gSet.size()]);
/* 193 */     return groups;
/*     */   }
/*     */ 
/*     */   public boolean hasSuperPermsCompat()
/*     */   {
/* 198 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean hasGroupSupport()
/*     */   {
/* 203 */     return true;
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
/*  63 */       if (!Permission_bPermissions2.this.hooked) {
/*  64 */         Plugin p = event.getPlugin();
/*  65 */         if (p.getDescription().getName().equals("bPermissions")) {
/*  66 */           Permission_bPermissions2.this.hooked = true;
/*  67 */           Permission_bPermissions2.log.info(String.format("[%s][Permission] %s hooked.", new Object[] { Permission_bPermissions2.this.plugin.getDescription().getName(), "bPermissions2" }));
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/*     */     @EventHandler(priority=EventPriority.MONITOR)
/*     */     public void onPluginDisable(PluginDisableEvent event) {
/*  74 */       if ((Permission_bPermissions2.this.hooked) && 
/*  75 */         (event.getPlugin().getDescription().getName().equals("bPermissions"))) {
/*  76 */         Permission_bPermissions2.this.hooked = false;
/*  77 */         Permission_bPermissions2.log.info(String.format("[%s][Permission] %s un-hooked.", new Object[] { Permission_bPermissions2.this.plugin.getDescription().getName(), "bPermissions2" }));
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\Vault.jar
 * Qualified Name:     net.milkbowl.vault.permission.plugins.Permission_bPermissions2
 * JD-Core Version:    0.6.2
 */