/*     */ package net.milkbowl.vault.permission.plugins;
/*     */ 
/*     */ import com.dthielke.starburst.Group;
/*     */ import com.dthielke.starburst.GroupManager;
/*     */ import com.dthielke.starburst.GroupSet;
/*     */ import com.dthielke.starburst.StarburstPlugin;
/*     */ import com.dthielke.starburst.User;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Set;
/*     */ import java.util.logging.Logger;
/*     */ import net.milkbowl.vault.permission.Permission;
/*     */ import org.bukkit.Bukkit;
/*     */ import org.bukkit.OfflinePlayer;
/*     */ import org.bukkit.Server;
/*     */ import org.bukkit.World;
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
/*     */ public class Permission_Starburst extends Permission
/*     */ {
/*     */   private StarburstPlugin perms;
/*  42 */   private final String name = "Starburst";
/*     */ 
/*     */   public Permission_Starburst(Plugin plugin) {
/*  45 */     this.plugin = plugin;
/*  46 */     Bukkit.getServer().getPluginManager().registerEvents(new PermissionServerListener(), plugin);
/*     */ 
/*  49 */     if (this.perms == null) {
/*  50 */       Plugin p = plugin.getServer().getPluginManager().getPlugin("Starburst");
/*  51 */       if (p != null) {
/*  52 */         this.perms = ((StarburstPlugin)p);
/*  53 */         log.info(String.format("[%s][Permission] %s hooked.", new Object[] { plugin.getDescription().getName(), "Starburst" }));
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public String[] getGroups()
/*     */   {
/*  84 */     String[] s = new String[this.perms.getGroupManager().getDefaultGroupSet().getGroups().size()];
/*  85 */     int i = 0;
/*  86 */     for (Group g : this.perms.getGroupManager().getDefaultGroupSet().getGroups()) {
/*  87 */       s[i] = g.getName();
/*  88 */       i++;
/*     */     }
/*  90 */     return s;
/*     */   }
/*     */ 
/*     */   public String getName()
/*     */   {
/*  95 */     return "Starburst";
/*     */   }
/*     */ 
/*     */   public String[] getPlayerGroups(String world, String player)
/*     */   {
/* 100 */     OfflinePlayer op = Bukkit.getOfflinePlayer(player);
/* 101 */     GroupSet set = this.perms.getGroupManager().getWorldSet(Bukkit.getWorld(world));
/* 102 */     User user = set.getUser(op);
/*     */ 
/* 104 */     Set children = user.getChildren(true);
/* 105 */     List groups = new ArrayList();
/* 106 */     for (Group child : children) {
/* 107 */       groups.add(child.getName());
/*     */     }
/* 109 */     return (String[])groups.toArray(new String[groups.size()]);
/*     */   }
/*     */ 
/*     */   public String getPrimaryGroup(String world, String player)
/*     */   {
/* 114 */     OfflinePlayer op = Bukkit.getOfflinePlayer(player);
/* 115 */     GroupSet set = this.perms.getGroupManager().getWorldSet(Bukkit.getWorld(world));
/* 116 */     User user = set.getUser(op);
/*     */ 
/* 118 */     Set children = user.getChildren(false);
/* 119 */     if (!children.isEmpty()) {
/* 120 */       return ((Group)children.iterator().next()).getName();
/*     */     }
/* 122 */     return null;
/*     */   }
/*     */ 
/*     */   public boolean groupAdd(String world, String group, String permission)
/*     */   {
/* 128 */     GroupManager gm = this.perms.getGroupManager();
/* 129 */     GroupSet set = gm.getWorldSet(Bukkit.getWorld(world));
/* 130 */     if (set.hasGroup(group)) {
/* 131 */       Group g = set.getGroup(group);
/*     */ 
/* 133 */       boolean value = !permission.startsWith("^");
/* 134 */       permission = value ? permission : permission.substring(1);
/* 135 */       g.addPermission(permission, value, true, true);
/*     */ 
/* 137 */       for (User user : gm.getAffectedUsers(g)) {
/* 138 */         user.applyPermissions(gm.getFactory());
/*     */       }
/* 140 */       return true;
/*     */     }
/* 142 */     return false;
/*     */   }
/*     */ 
/*     */   public boolean groupHas(String world, String group, String permission)
/*     */   {
/* 148 */     GroupSet set = this.perms.getGroupManager().getWorldSet(Bukkit.getWorld(world));
/* 149 */     if (set.hasGroup(group)) {
/* 150 */       Group g = set.getGroup(group);
/* 151 */       return g.hasPermission(permission, true);
/*     */     }
/* 153 */     return false;
/*     */   }
/*     */ 
/*     */   public boolean groupRemove(String world, String group, String permission)
/*     */   {
/* 159 */     GroupManager gm = this.perms.getGroupManager();
/* 160 */     GroupSet set = gm.getWorldSet(Bukkit.getWorld(world));
/* 161 */     if (set.hasGroup(group)) {
/* 162 */       Group g = set.getGroup(group);
/*     */ 
/* 164 */       boolean value = !permission.startsWith("^");
/* 165 */       permission = value ? permission : permission.substring(1);
/*     */ 
/* 167 */       if (g.hasPermission(permission, false)) {
/* 168 */         g.removePermission(permission, true);
/*     */ 
/* 170 */         for (User user : gm.getAffectedUsers(g)) {
/* 171 */           user.applyPermissions(gm.getFactory());
/*     */         }
/* 173 */         return true;
/*     */       }
/* 175 */       return false;
/*     */     }
/*     */ 
/* 178 */     return false;
/*     */   }
/*     */ 
/*     */   public boolean hasSuperPermsCompat()
/*     */   {
/* 184 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean isEnabled()
/*     */   {
/* 189 */     return (this.perms != null) && (this.perms.isEnabled());
/*     */   }
/*     */ 
/*     */   public boolean playerAdd(String world, String player, String permission)
/*     */   {
/* 194 */     OfflinePlayer op = Bukkit.getOfflinePlayer(player);
/* 195 */     GroupSet set = this.perms.getGroupManager().getWorldSet(Bukkit.getWorld(world));
/* 196 */     User user = set.getUser(op);
/*     */ 
/* 198 */     boolean value = !permission.startsWith("^");
/* 199 */     permission = value ? permission : permission.substring(1);
/* 200 */     user.addPermission(permission, value, true, true);
/*     */ 
/* 202 */     if (user.isActive()) {
/* 203 */       user.applyPermissions(this.perms.getGroupManager().getFactory());
/*     */     }
/* 205 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean playerAddGroup(String world, String player, String group)
/*     */   {
/* 210 */     OfflinePlayer op = Bukkit.getOfflinePlayer(player);
/* 211 */     GroupSet set = this.perms.getGroupManager().getWorldSet(Bukkit.getWorld(world));
/* 212 */     User user = set.getUser(op);
/*     */ 
/* 214 */     if (set.hasGroup(group)) {
/* 215 */       Group g = set.getGroup(group);
/* 216 */       if (!user.hasChild(g, false)) {
/* 217 */         user.addChild(g, true);
/*     */ 
/* 219 */         if (user.isActive()) {
/* 220 */           user.applyPermissions(this.perms.getGroupManager().getFactory());
/*     */         }
/* 222 */         return true;
/*     */       }
/* 224 */       return false;
/*     */     }
/*     */ 
/* 227 */     return false;
/*     */   }
/*     */ 
/*     */   public boolean playerHas(String world, String player, String permission)
/*     */   {
/* 233 */     OfflinePlayer op = Bukkit.getOfflinePlayer(player);
/*     */ 
/* 235 */     if (op.isOnline()) {
/* 236 */       Player p = (Player)op;
/* 237 */       if (p.getWorld().getName().equalsIgnoreCase(world)) {
/* 238 */         return p.hasPermission(permission);
/*     */       }
/*     */     }
/*     */ 
/* 242 */     GroupSet set = this.perms.getGroupManager().getWorldSet(Bukkit.getWorld(world));
/* 243 */     Group user = set.getUser(op);
/* 244 */     return user.hasPermission(permission, true);
/*     */   }
/*     */ 
/*     */   public boolean playerInGroup(String world, String player, String group)
/*     */   {
/* 249 */     OfflinePlayer op = Bukkit.getOfflinePlayer(player);
/* 250 */     GroupSet set = this.perms.getGroupManager().getWorldSet(Bukkit.getWorld(world));
/* 251 */     User user = set.getUser(op);
/*     */ 
/* 253 */     if (set.hasGroup(group)) {
/* 254 */       Group g = set.getGroup(group);
/* 255 */       return user.hasChild(g, true);
/*     */     }
/* 257 */     return false;
/*     */   }
/*     */ 
/*     */   public boolean playerRemove(String world, String player, String permission)
/*     */   {
/* 263 */     OfflinePlayer op = Bukkit.getOfflinePlayer(player);
/* 264 */     GroupSet set = this.perms.getGroupManager().getWorldSet(Bukkit.getWorld(world));
/* 265 */     User user = set.getUser(op);
/*     */ 
/* 267 */     boolean value = !permission.startsWith("^");
/* 268 */     permission = value ? permission : permission.substring(1);
/* 269 */     if (user.hasPermission(permission, false)) {
/* 270 */       user.removePermission(permission, true);
/* 271 */       if (user.isActive()) {
/* 272 */         user.applyPermissions(this.perms.getGroupManager().getFactory());
/*     */       }
/* 274 */       return true;
/*     */     }
/* 276 */     return false;
/*     */   }
/*     */ 
/*     */   public boolean playerRemoveGroup(String world, String player, String group)
/*     */   {
/* 282 */     OfflinePlayer op = Bukkit.getOfflinePlayer(player);
/* 283 */     GroupSet set = this.perms.getGroupManager().getWorldSet(Bukkit.getWorld(world));
/* 284 */     User user = set.getUser(op);
/*     */ 
/* 286 */     if (set.hasGroup(group)) {
/* 287 */       Group g = set.getGroup(group);
/* 288 */       if (user.hasChild(g, false)) {
/* 289 */         user.removeChild(g, true);
/*     */ 
/* 291 */         if (user.isActive()) {
/* 292 */           user.applyPermissions(this.perms.getGroupManager().getFactory());
/*     */         }
/* 294 */         return true;
/*     */       }
/* 296 */       return false;
/*     */     }
/*     */ 
/* 299 */     return false;
/*     */   }
/*     */ 
/*     */   public boolean hasGroupSupport()
/*     */   {
/* 305 */     return true;
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
/*  62 */       if (Permission_Starburst.this.perms == null) {
/*  63 */         Plugin p = event.getPlugin();
/*  64 */         if (p.getDescription().getName().equals("Starburst")) {
/*  65 */           Permission_Starburst.this.perms = ((StarburstPlugin)p);
/*  66 */           Permission_Starburst.log.info(String.format("[%s][Permission] %s hooked.", new Object[] { Permission_Starburst.this.plugin.getDescription().getName(), "Starburst" }));
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/*     */     @EventHandler(priority=EventPriority.MONITOR)
/*     */     public void onPluginDisable(PluginDisableEvent event) {
/*  73 */       if ((Permission_Starburst.this.perms != null) && 
/*  74 */         (event.getPlugin().getDescription().getName().equals("Starburst"))) {
/*  75 */         Permission_Starburst.this.perms = null;
/*  76 */         Permission_Starburst.log.info(String.format("[%s][Permission] %s un-hooked.", new Object[] { Permission_Starburst.this.plugin.getDescription().getName(), "Starburst" }));
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\Vault.jar
 * Qualified Name:     net.milkbowl.vault.permission.plugins.Permission_Starburst
 * JD-Core Version:    0.6.2
 */