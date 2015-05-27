/*     */ package net.milkbowl.vault.permission;
/*     */ 
/*     */ import java.util.logging.Logger;
/*     */ import org.bukkit.OfflinePlayer;
/*     */ import org.bukkit.Server;
/*     */ import org.bukkit.World;
/*     */ import org.bukkit.command.CommandSender;
/*     */ import org.bukkit.entity.Player;
/*     */ import org.bukkit.permissions.PermissionAttachment;
/*     */ import org.bukkit.permissions.PermissionAttachmentInfo;
/*     */ import org.bukkit.plugin.Plugin;
/*     */ 
/*     */ public abstract class Permission
/*     */ {
/*  34 */   protected static final Logger log = Logger.getLogger("Minecraft");
/*  35 */   protected Plugin plugin = null;
/*     */ 
/*     */   public abstract String getName();
/*     */ 
/*     */   public abstract boolean isEnabled();
/*     */ 
/*     */   public abstract boolean hasSuperPermsCompat();
/*     */ 
/*     */   @Deprecated
/*     */   public boolean has(String world, String player, String permission)
/*     */   {
/*  60 */     if (world == null) {
/*  61 */       return playerHas((String)null, player, permission);
/*     */     }
/*  63 */     return playerHas(world, player, permission);
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public boolean has(World world, String player, String permission)
/*     */   {
/*  71 */     if (world == null) {
/*  72 */       return playerHas((String)null, player, permission);
/*     */     }
/*  74 */     return playerHas(world.getName(), player, permission);
/*     */   }
/*     */ 
/*     */   public boolean has(CommandSender sender, String permission)
/*     */   {
/*  88 */     return sender.hasPermission(permission);
/*     */   }
/*     */ 
/*     */   public boolean has(Player player, String permission)
/*     */   {
/*  98 */     return player.hasPermission(permission);
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public abstract boolean playerHas(String paramString1, String paramString2, String paramString3);
/*     */ 
/*     */   @Deprecated
/*     */   public boolean playerHas(World world, String player, String permission)
/*     */   {
/* 112 */     if (world == null) {
/* 113 */       return playerHas((String)null, player, permission);
/*     */     }
/* 115 */     return playerHas(world.getName(), player, permission);
/*     */   }
/*     */ 
/*     */   public boolean playerHas(String world, OfflinePlayer player, String permission)
/*     */   {
/* 129 */     if (world == null) {
/* 130 */       return has((String)null, player.getName(), permission);
/*     */     }
/* 132 */     return has(world, player.getName(), permission);
/*     */   }
/*     */ 
/*     */   public boolean playerHas(Player player, String permission)
/*     */   {
/* 145 */     return has(player, permission);
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public abstract boolean playerAdd(String paramString1, String paramString2, String paramString3);
/*     */ 
/*     */   @Deprecated
/*     */   public boolean playerAdd(World world, String player, String permission)
/*     */   {
/* 167 */     if (world == null) {
/* 168 */       return playerAdd((String)null, player, permission);
/*     */     }
/* 170 */     return playerAdd(world.getName(), player, permission);
/*     */   }
/*     */ 
/*     */   public boolean playerAdd(String world, OfflinePlayer player, String permission)
/*     */   {
/* 184 */     if (world == null) {
/* 185 */       return playerAdd((String)null, player.getName(), permission);
/*     */     }
/* 187 */     return playerAdd(world, player.getName(), permission);
/*     */   }
/*     */ 
/*     */   public boolean playerAdd(Player player, String permission)
/*     */   {
/* 200 */     return playerAdd(player.getWorld().getName(), player, permission);
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public boolean playerAddTransient(String player, String permission)
/*     */     throws UnsupportedOperationException
/*     */   {
/* 208 */     Player p = this.plugin.getServer().getPlayer(player);
/* 209 */     if (p == null) {
/* 210 */       throw new UnsupportedOperationException(getName() + " does not support offline player transient permissions!");
/*     */     }
/* 212 */     return playerAddTransient(p, permission);
/*     */   }
/*     */ 
/*     */   public boolean playerAddTransient(OfflinePlayer player, String permission)
/*     */     throws UnsupportedOperationException
/*     */   {
/* 225 */     if (player.isOnline()) {
/* 226 */       return playerAddTransient((Player)player, permission);
/*     */     }
/* 228 */     throw new UnsupportedOperationException(getName() + " does not support offline player transient permissions!");
/*     */   }
/*     */ 
/*     */   public boolean playerAddTransient(Player player, String permission)
/*     */   {
/* 240 */     for (PermissionAttachmentInfo paInfo : player.getEffectivePermissions()) {
/* 241 */       if ((paInfo.getAttachment() != null) && (paInfo.getAttachment().getPlugin().equals(this.plugin))) {
/* 242 */         paInfo.getAttachment().setPermission(permission, true);
/* 243 */         return true;
/*     */       }
/*     */     }
/*     */ 
/* 247 */     PermissionAttachment attach = player.addAttachment(this.plugin);
/* 248 */     attach.setPermission(permission, true);
/*     */ 
/* 250 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean playerAddTransient(String worldName, OfflinePlayer player, String permission)
/*     */   {
/* 263 */     return playerAddTransient(worldName, player.getName(), permission);
/*     */   }
/*     */ 
/*     */   public boolean playerAddTransient(String worldName, Player player, String permission)
/*     */   {
/* 276 */     return playerAddTransient(player, permission);
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public boolean playerAddTransient(String worldName, String player, String permission)
/*     */   {
/* 284 */     Player p = this.plugin.getServer().getPlayer(player);
/* 285 */     if (p == null) {
/* 286 */       throw new UnsupportedOperationException(getName() + " does not support offline player transient permissions!");
/*     */     }
/* 288 */     return playerAddTransient(p, permission);
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public boolean playerRemoveTransient(String worldName, String player, String permission)
/*     */   {
/* 296 */     Player p = this.plugin.getServer().getPlayer(player);
/* 297 */     if (p == null) {
/* 298 */       return false;
/*     */     }
/* 300 */     return playerRemoveTransient(p, permission);
/*     */   }
/*     */ 
/*     */   public boolean playerRemoveTransient(String worldName, OfflinePlayer player, String permission)
/*     */   {
/* 313 */     return playerRemoveTransient(worldName, player.getName(), permission);
/*     */   }
/*     */ 
/*     */   public boolean playerRemoveTransient(String worldName, Player player, String permission)
/*     */   {
/* 326 */     return playerRemoveTransient(worldName, player, permission);
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public abstract boolean playerRemove(String paramString1, String paramString2, String paramString3);
/*     */ 
/*     */   public boolean playerRemove(String world, OfflinePlayer player, String permission)
/*     */   {
/* 346 */     if (world == null) {
/* 347 */       return playerRemove((String)null, player.getName(), permission);
/*     */     }
/* 349 */     return playerRemove(world, player.getName(), permission);
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public boolean playerRemove(World world, String player, String permission)
/*     */   {
/* 364 */     if (world == null) {
/* 365 */       return playerRemove((String)null, player, permission);
/*     */     }
/* 367 */     return playerRemove(world.getName(), player, permission);
/*     */   }
/*     */ 
/*     */   public boolean playerRemove(Player player, String permission)
/*     */   {
/* 379 */     return playerRemove(player.getWorld().getName(), player, permission);
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public boolean playerRemoveTransient(String player, String permission)
/*     */   {
/* 387 */     Player p = this.plugin.getServer().getPlayer(player);
/* 388 */     if (p == null) {
/* 389 */       return false;
/*     */     }
/* 391 */     return playerRemoveTransient(p, permission);
/*     */   }
/*     */ 
/*     */   public boolean playerRemoveTransient(OfflinePlayer player, String permission)
/*     */   {
/* 405 */     if (player.isOnline()) {
/* 406 */       return playerRemoveTransient((Player)player, permission);
/*     */     }
/* 408 */     return false;
/*     */   }
/*     */ 
/*     */   public boolean playerRemoveTransient(Player player, String permission)
/*     */   {
/* 420 */     for (PermissionAttachmentInfo paInfo : player.getEffectivePermissions()) {
/* 421 */       if ((paInfo.getAttachment() != null) && (paInfo.getAttachment().getPlugin().equals(this.plugin))) {
/* 422 */         paInfo.getAttachment().unsetPermission(permission);
/* 423 */         return true;
/*     */       }
/*     */     }
/* 426 */     return false;
/*     */   }
/*     */ 
/*     */   public abstract boolean groupHas(String paramString1, String paramString2, String paramString3);
/*     */ 
/*     */   public boolean groupHas(World world, String group, String permission)
/*     */   {
/* 452 */     if (world == null) {
/* 453 */       return groupHas((String)null, group, permission);
/*     */     }
/* 455 */     return groupHas(world.getName(), group, permission);
/*     */   }
/*     */ 
/*     */   public abstract boolean groupAdd(String paramString1, String paramString2, String paramString3);
/*     */ 
/*     */   public boolean groupAdd(World world, String group, String permission)
/*     */   {
/* 481 */     if (world == null) {
/* 482 */       return groupAdd((String)null, group, permission);
/*     */     }
/* 484 */     return groupAdd(world.getName(), group, permission);
/*     */   }
/*     */ 
/*     */   public abstract boolean groupRemove(String paramString1, String paramString2, String paramString3);
/*     */ 
/*     */   public boolean groupRemove(World world, String group, String permission)
/*     */   {
/* 510 */     if (world == null) {
/* 511 */       return groupRemove((String)null, group, permission);
/*     */     }
/* 513 */     return groupRemove(world.getName(), group, permission);
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public abstract boolean playerInGroup(String paramString1, String paramString2, String paramString3);
/*     */ 
/*     */   @Deprecated
/*     */   public boolean playerInGroup(World world, String player, String group)
/*     */   {
/* 527 */     if (world == null) {
/* 528 */       return playerInGroup((String)null, player, group);
/*     */     }
/* 530 */     return playerInGroup(world.getName(), player, group);
/*     */   }
/*     */ 
/*     */   public boolean playerInGroup(String world, OfflinePlayer player, String group)
/*     */   {
/* 544 */     if (world == null) {
/* 545 */       return playerInGroup((String)null, player.getName(), group);
/*     */     }
/* 547 */     return playerInGroup(world, player.getName(), group);
/*     */   }
/*     */ 
/*     */   public boolean playerInGroup(Player player, String group)
/*     */   {
/* 560 */     return playerInGroup(player.getWorld().getName(), player, group);
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public abstract boolean playerAddGroup(String paramString1, String paramString2, String paramString3);
/*     */ 
/*     */   @Deprecated
/*     */   public boolean playerAddGroup(World world, String player, String group)
/*     */   {
/* 574 */     if (world == null) {
/* 575 */       return playerAddGroup((String)null, player, group);
/*     */     }
/* 577 */     return playerAddGroup(world.getName(), player, group);
/*     */   }
/*     */ 
/*     */   public boolean playerAddGroup(String world, OfflinePlayer player, String group)
/*     */   {
/* 591 */     if (world == null) {
/* 592 */       return playerAddGroup((String)null, player.getName(), group);
/*     */     }
/* 594 */     return playerAddGroup(world, player.getName(), group);
/*     */   }
/*     */ 
/*     */   public boolean playerAddGroup(Player player, String group)
/*     */   {
/* 607 */     return playerAddGroup(player.getWorld().getName(), player, group);
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public abstract boolean playerRemoveGroup(String paramString1, String paramString2, String paramString3);
/*     */ 
/*     */   @Deprecated
/*     */   public boolean playerRemoveGroup(World world, String player, String group)
/*     */   {
/* 621 */     if (world == null) {
/* 622 */       return playerRemoveGroup((String)null, player, group);
/*     */     }
/* 624 */     return playerRemoveGroup(world.getName(), player, group);
/*     */   }
/*     */ 
/*     */   public boolean playerRemoveGroup(String world, OfflinePlayer player, String group)
/*     */   {
/* 638 */     if (world == null) {
/* 639 */       return playerRemoveGroup((String)null, player.getName(), group);
/*     */     }
/* 641 */     return playerRemoveGroup(world, player.getName(), group);
/*     */   }
/*     */ 
/*     */   public boolean playerRemoveGroup(Player player, String group)
/*     */   {
/* 654 */     return playerRemoveGroup(player.getWorld().getName(), player, group);
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public abstract String[] getPlayerGroups(String paramString1, String paramString2);
/*     */ 
/*     */   @Deprecated
/*     */   public String[] getPlayerGroups(World world, String player)
/*     */   {
/* 668 */     if (world == null) {
/* 669 */       return getPlayerGroups((String)null, player);
/*     */     }
/* 671 */     return getPlayerGroups(world.getName(), player);
/*     */   }
/*     */ 
/*     */   public String[] getPlayerGroups(String world, OfflinePlayer player)
/*     */   {
/* 684 */     return getPlayerGroups(world, player.getName());
/*     */   }
/*     */ 
/*     */   public String[] getPlayerGroups(Player player)
/*     */   {
/* 696 */     return getPlayerGroups(player.getWorld().getName(), player);
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public abstract String getPrimaryGroup(String paramString1, String paramString2);
/*     */ 
/*     */   @Deprecated
/*     */   public String getPrimaryGroup(World world, String player)
/*     */   {
/* 710 */     if (world == null) {
/* 711 */       return getPrimaryGroup((String)null, player);
/*     */     }
/* 713 */     return getPrimaryGroup(world.getName(), player);
/*     */   }
/*     */ 
/*     */   public String getPrimaryGroup(String world, OfflinePlayer player)
/*     */   {
/* 726 */     return getPrimaryGroup(world, player.getName());
/*     */   }
/*     */ 
/*     */   public String getPrimaryGroup(Player player)
/*     */   {
/* 738 */     return getPrimaryGroup(player.getWorld().getName(), player);
/*     */   }
/*     */ 
/*     */   public abstract String[] getGroups();
/*     */ 
/*     */   public abstract boolean hasGroupSupport();
/*     */ }

/* Location:           D:\Github\Mechanics\Vault.jar
 * Qualified Name:     net.milkbowl.vault.permission.Permission
 * JD-Core Version:    0.6.2
 */