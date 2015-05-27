/*     */ package net.milkbowl.vault.permission.plugins;
/*     */ 
/*     */ import net.milkbowl.vault.permission.Permission;
/*     */ import org.bukkit.Server;
/*     */ import org.bukkit.entity.Player;
/*     */ import org.bukkit.plugin.Plugin;
/*     */ 
/*     */ public class Permission_SuperPerms extends Permission
/*     */ {
/*  25 */   private final String name = "SuperPerms";
/*     */ 
/*     */   public Permission_SuperPerms(Plugin plugin) {
/*  28 */     this.plugin = plugin;
/*     */   }
/*     */ 
/*     */   public String getName()
/*     */   {
/*  33 */     return "SuperPerms";
/*     */   }
/*     */ 
/*     */   public boolean isEnabled()
/*     */   {
/*  38 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean playerHas(String world, String player, String permission)
/*     */   {
/*  43 */     Player p = this.plugin.getServer().getPlayer(player);
/*  44 */     return p != null ? p.hasPermission(permission) : false;
/*     */   }
/*     */ 
/*     */   public boolean playerAdd(String world, String player, String permission)
/*     */   {
/*  49 */     return false;
/*     */   }
/*     */ 
/*     */   public boolean playerRemove(String world, String player, String permission)
/*     */   {
/*  56 */     return false;
/*     */   }
/*     */ 
/*     */   public boolean groupHas(String world, String group, String permission)
/*     */   {
/*  61 */     throw new UnsupportedOperationException(getName() + " no group permissions.");
/*     */   }
/*     */ 
/*     */   public boolean groupAdd(String world, String group, String permission)
/*     */   {
/*  66 */     throw new UnsupportedOperationException(getName() + " no group permissions.");
/*     */   }
/*     */ 
/*     */   public boolean groupRemove(String world, String group, String permission)
/*     */   {
/*  71 */     throw new UnsupportedOperationException(getName() + " no group permissions.");
/*     */   }
/*     */ 
/*     */   public boolean playerInGroup(String world, String player, String group)
/*     */   {
/*  76 */     return playerHas(world, player, "groups." + group);
/*     */   }
/*     */ 
/*     */   public boolean playerAddGroup(String world, String player, String group)
/*     */   {
/*  81 */     throw new UnsupportedOperationException(getName() + " no group permissions.");
/*     */   }
/*     */ 
/*     */   public boolean playerRemoveGroup(String world, String player, String group)
/*     */   {
/*  86 */     throw new UnsupportedOperationException(getName() + " no group permissions.");
/*     */   }
/*     */ 
/*     */   public String[] getPlayerGroups(String world, String player)
/*     */   {
/*  91 */     throw new UnsupportedOperationException(getName() + " no group permissions.");
/*     */   }
/*     */ 
/*     */   public String getPrimaryGroup(String world, String player)
/*     */   {
/*  96 */     throw new UnsupportedOperationException(getName() + " no group permissions.");
/*     */   }
/*     */ 
/*     */   public String[] getGroups()
/*     */   {
/* 101 */     return new String[0];
/*     */   }
/*     */ 
/*     */   public boolean hasSuperPermsCompat()
/*     */   {
/* 106 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean hasGroupSupport()
/*     */   {
/* 111 */     return false;
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\Vault.jar
 * Qualified Name:     net.milkbowl.vault.permission.plugins.Permission_SuperPerms
 * JD-Core Version:    0.6.2
 */