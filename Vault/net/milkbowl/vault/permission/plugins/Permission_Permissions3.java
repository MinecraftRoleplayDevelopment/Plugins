/*     */ package net.milkbowl.vault.permission.plugins;
/*     */ 
/*     */ import com.nijiko.permissions.Group;
/*     */ import com.nijiko.permissions.ModularControl;
/*     */ import com.nijiko.permissions.PermissionHandler;
/*     */ import com.nijiko.permissions.User;
/*     */ import com.nijikokun.bukkit.Permissions.Permissions;
/*     */ import java.util.HashSet;
/*     */ import java.util.Set;
/*     */ import java.util.logging.Logger;
/*     */ import net.milkbowl.vault.permission.Permission;
/*     */ import org.bukkit.Bukkit;
/*     */ import org.bukkit.Server;
/*     */ import org.bukkit.World;
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
/*     */ public class Permission_Permissions3 extends Permission
/*     */ {
/*  40 */   private String name = "Permissions3";
/*     */   private ModularControl perms;
/*  42 */   private Permissions permission = null;
/*     */ 
/*     */   public Permission_Permissions3(Plugin plugin) {
/*  45 */     this.plugin = plugin;
/*  46 */     Bukkit.getServer().getPluginManager().registerEvents(new PermissionServerListener(), plugin);
/*     */ 
/*  49 */     if (this.permission == null) {
/*  50 */       Plugin perms = plugin.getServer().getPluginManager().getPlugin("Permissions");
/*  51 */       if (perms == null) {
/*  52 */         plugin.getServer().getPluginManager().getPlugin("vPerms");
/*  53 */         this.name = "vPerms";
/*     */       }
/*  55 */       if ((perms != null) && 
/*  56 */         (perms.isEnabled()) && (perms.getDescription().getVersion().startsWith("3"))) {
/*  57 */         this.permission = ((Permissions)perms);
/*  58 */         this.perms = ((ModularControl)this.permission.getHandler());
/*  59 */         log.severe("Your permission system is outdated and no longer fully supported! It is highly advised to update!");
/*  60 */         log.info(String.format("[%s][Permission] %s hooked.", new Object[] { plugin.getDescription().getName(), this.name }));
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean isEnabled()
/*     */   {
/*  68 */     if (this.permission == null) {
/*  69 */       return false;
/*     */     }
/*  71 */     return this.permission.isEnabled();
/*     */   }
/*     */ 
/*     */   public boolean playerInGroup(String worldName, String playerName, String groupName)
/*     */   {
/*  77 */     return this.permission.getHandler().inGroup(worldName, playerName, groupName);
/*     */   }
/*     */ 
/*     */   public String getName()
/*     */   {
/* 110 */     return this.name;
/*     */   }
/*     */ 
/*     */   public boolean has(CommandSender sender, String permission)
/*     */   {
/* 115 */     if ((sender.isOp()) || (!(sender instanceof Player))) {
/* 116 */       return true;
/*     */     }
/* 118 */     return has(((Player)sender).getWorld().getName(), sender.getName(), permission);
/*     */   }
/*     */ 
/*     */   public boolean has(Player player, String permission)
/*     */   {
/* 124 */     return has(player.getWorld().getName(), player.getName(), permission);
/*     */   }
/*     */ 
/*     */   public boolean playerAddGroup(String worldName, String playerName, String groupName) {
/* 128 */     if (worldName == null) {
/* 129 */       worldName = "*";
/*     */     }
/*     */ 
/* 132 */     Group g = this.perms.getGroupObject(worldName, groupName);
/* 133 */     if (g == null)
/* 134 */       return false;
/*     */     try
/*     */     {
/* 137 */       this.perms.safeGetUser(worldName, playerName).addParent(g);
/*     */     } catch (Exception e) {
/* 139 */       e.printStackTrace();
/* 140 */       return false;
/*     */     }
/* 142 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean playerRemoveGroup(String worldName, String playerName, String groupName)
/*     */   {
/* 147 */     if (worldName == null) {
/* 148 */       worldName = "*";
/*     */     }
/*     */ 
/* 151 */     Group g = this.perms.getGroupObject(worldName, groupName);
/* 152 */     if (g == null)
/* 153 */       return false;
/*     */     try
/*     */     {
/* 156 */       this.perms.safeGetUser(worldName, playerName).removeParent(g);
/*     */     } catch (Exception e) {
/* 158 */       e.printStackTrace();
/* 159 */       return false;
/*     */     }
/* 161 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean playerAdd(String worldName, String playerName, String permission)
/*     */   {
/* 166 */     this.perms.addUserPermission(worldName, playerName, permission);
/* 167 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean playerRemove(String worldName, String playerName, String permission)
/*     */   {
/* 172 */     this.perms.removeUserPermission(worldName, playerName, permission);
/* 173 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean groupAdd(String worldName, String groupName, String permission)
/*     */   {
/* 178 */     if (worldName == null) {
/* 179 */       worldName = "*";
/*     */     }
/*     */ 
/* 182 */     this.perms.addGroupPermission(worldName, groupName, permission);
/* 183 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean groupRemove(String worldName, String groupName, String permission)
/*     */   {
/* 188 */     if (worldName == null) {
/* 189 */       worldName = "*";
/*     */     }
/* 191 */     this.perms.removeGroupPermission(worldName, groupName, permission);
/* 192 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean groupHas(String worldName, String groupName, String permission)
/*     */   {
/* 197 */     if (worldName == null)
/* 198 */       worldName = "*";
/*     */     try
/*     */     {
/* 201 */       return this.perms.safeGetGroup(worldName, groupName).hasPermission(permission);
/*     */     } catch (Exception e) {
/* 203 */       e.printStackTrace();
/* 204 */     }return false;
/*     */   }
/*     */ 
/*     */   public String[] getPlayerGroups(String world, String playerName)
/*     */   {
/* 210 */     return this.perms.getGroups(world, playerName);
/*     */   }
/*     */ 
/*     */   public String getPrimaryGroup(String world, String playerName) {
/* 214 */     return getPlayerGroups(world, playerName)[0];
/*     */   }
/*     */ 
/*     */   public boolean playerHas(String worldName, String playerName, String permission)
/*     */   {
/* 219 */     Player p = this.plugin.getServer().getPlayer(playerName);
/* 220 */     if ((p != null) && 
/* 221 */       (p.hasPermission(permission))) {
/* 222 */       return true;
/*     */     }
/* 224 */     return this.perms.has(worldName, playerName, permission);
/*     */   }
/*     */ 
/*     */   public boolean playerAddTransient(String player, String permission)
/*     */   {
/* 230 */     return playerAddTransient(null, player, permission);
/*     */   }
/*     */ 
/*     */   public boolean playerAddTransient(Player player, String permission)
/*     */   {
/* 235 */     return playerAddTransient(null, player.getName(), permission);
/*     */   }
/*     */ 
/*     */   public boolean playerAddTransient(String worldName, Player player, String permission)
/*     */   {
/* 240 */     return playerAddTransient(worldName, player.getName(), permission);
/*     */   }
/*     */ 
/*     */   public boolean playerAddTransient(String worldName, String player, String permission)
/*     */   {
/* 245 */     if (worldName == null)
/* 246 */       worldName = "*";
/*     */     try
/*     */     {
/* 249 */       this.perms.safeGetUser(worldName, player).addTransientPermission(permission);
/* 250 */       return true; } catch (Exception e) {
/*     */     }
/* 252 */     return false;
/*     */   }
/*     */ 
/*     */   public boolean playerRemoveTransient(String player, String permission)
/*     */   {
/* 258 */     return playerRemoveTransient(null, player, permission);
/*     */   }
/*     */ 
/*     */   public boolean playerRemoveTransient(Player player, String permission)
/*     */   {
/* 263 */     return playerRemoveTransient(null, player.getName(), permission);
/*     */   }
/*     */ 
/*     */   public boolean playerRemoveTransient(String worldName, Player player, String permission)
/*     */   {
/* 268 */     return playerRemoveTransient(worldName, player.getName(), permission);
/*     */   }
/*     */ 
/*     */   public boolean playerRemoveTransient(String worldName, String player, String permission)
/*     */   {
/* 273 */     if (worldName == null) {
/* 274 */       worldName = "*";
/*     */     }
/*     */     try
/*     */     {
/* 278 */       this.perms.safeGetUser(worldName, player).removeTransientPermission(permission);
/* 279 */       return true; } catch (Exception e) {
/*     */     }
/* 281 */     return false;
/*     */   }
/*     */ 
/*     */   public String[] getGroups()
/*     */   {
/* 288 */     Set groupNames = new HashSet();
/* 289 */     for (World world : Bukkit.getServer().getWorlds()) {
/* 290 */       for (Group group : this.perms.getGroups(world.getName())) {
/* 291 */         groupNames.add(group.getName());
/*     */       }
/*     */     }
/* 294 */     return (String[])groupNames.toArray(new String[0]);
/*     */   }
/*     */ 
/*     */   public boolean hasSuperPermsCompat()
/*     */   {
/* 299 */     return false;
/*     */   }
/*     */ 
/*     */   public boolean hasGroupSupport()
/*     */   {
/* 304 */     return true;
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
/*  84 */       if (Permission_Permissions3.this.permission == null) {
/*  85 */         Plugin permi = event.getPlugin();
/*  86 */         if (((permi.getDescription().getName().equals("Permissions")) || (permi.getDescription().getName().equals("vPerms"))) && (permi.getDescription().getVersion().startsWith("3")) && 
/*  87 */           (permi.isEnabled())) {
/*  88 */           Permission_Permissions3.this.permission = ((Permissions)permi);
/*  89 */           Permission_Permissions3.this.perms = ((ModularControl)Permission_Permissions3.this.permission.getHandler());
/*  90 */           Permission_Permissions3.log.info(String.format("[%s][Permission] %s hooked.", new Object[] { Permission_Permissions3.this.plugin.getDescription().getName(), Permission_Permissions3.this.name }));
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/*     */     @EventHandler(priority=EventPriority.MONITOR)
/*     */     public void onPluginDisable(PluginDisableEvent event)
/*     */     {
/*  98 */       if ((Permission_Permissions3.this.permission != null) && (
/*  99 */         (event.getPlugin().getDescription().getName().equals("Permissions")) || (event.getPlugin().getDescription().getName().equals("vPerms")))) {
/* 100 */         Permission_Permissions3.this.permission = null;
/* 101 */         Permission_Permissions3.this.perms = null;
/* 102 */         Permission_Permissions3.log.info(String.format("[%s][Permission] %s un-hooked.", new Object[] { Permission_Permissions3.this.plugin.getDescription().getName(), Permission_Permissions3.this.name }));
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\Vault.jar
 * Qualified Name:     net.milkbowl.vault.permission.plugins.Permission_Permissions3
 * JD-Core Version:    0.6.2
 */