/*    */ package net.nunnerycode.bukkit.itemattributes.managers;
/*    */ 
/*    */ import com.conventnunnery.libraries.config.CommentedConventYamlConfiguration;
/*    */ import java.util.ArrayList;
/*    */ import java.util.Collections;
/*    */ import java.util.List;
/*    */ import net.nunnerycode.bukkit.itemattributes.api.ItemAttributes;
/*    */ import net.nunnerycode.bukkit.itemattributes.api.managers.PermissionsManager;
/*    */ import org.bukkit.Bukkit;
/*    */ import org.bukkit.entity.Player;
/*    */ import org.bukkit.plugin.PluginManager;
/*    */ 
/*    */ public class ItemAttributesPermissionsManager
/*    */   implements PermissionsManager
/*    */ {
/*    */   private final ItemAttributes plugin;
/*    */   private final List<String> permissions;
/*    */ 
/*    */   public ItemAttributesPermissionsManager(ItemAttributes plugin)
/*    */   {
/* 17 */     this.plugin = plugin;
/* 18 */     this.permissions = new ArrayList();
/*    */   }
/*    */ 
/*    */   public void load() {
/* 22 */     getPlugin().getPermissionsYAML().load();
/* 23 */     this.permissions.clear();
/* 24 */     this.permissions.addAll(getPlugin().getPermissionsYAML().getStringList("permissions"));
/*    */   }
/*    */ 
/*    */   public void save() {
/* 28 */     getPlugin().getPermissionsYAML().load();
/* 29 */     getPlugin().getPermissionsYAML().set("permissions", this.permissions);
/* 30 */     getPlugin().getPermissionsYAML().save();
/*    */   }
/*    */ 
/*    */   public ItemAttributes getPlugin()
/*    */   {
/* 35 */     return this.plugin;
/*    */   }
/*    */ 
/*    */   public List<String> getPermissions()
/*    */   {
/* 40 */     return Collections.unmodifiableList(this.permissions);
/*    */   }
/*    */ 
/*    */   public void addPermissions(String[] permissions)
/*    */   {
/* 45 */     Collections.addAll(this.permissions, permissions);
/*    */   }
/*    */ 
/*    */   public void removePermissions(String[] permissions)
/*    */   {
/* 50 */     for (String s : permissions)
/* 51 */       if (this.permissions.contains(s))
/* 52 */         this.permissions.remove(s);
/*    */   }
/*    */ 
/*    */   public boolean hasPermission(Player player, String permission)
/*    */   {
/* 59 */     return (player.hasPermission(permission)) && ((this.permissions.contains(permission)) || (Bukkit.getPluginManager().getPermission(permission) != null));
/*    */   }
/*    */ }

/* Location:           D:\Github\Mechanics\ItemAttributes.jar
 * Qualified Name:     net.nunnerycode.bukkit.itemattributes.managers.ItemAttributesPermissionsManager
 * JD-Core Version:    0.6.2
 */