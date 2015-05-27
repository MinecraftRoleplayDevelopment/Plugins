/*    */ package fr.tomcraft.unlimitedrecipes;
/*    */ 
/*    */ import org.bukkit.ChatColor;
/*    */ import org.bukkit.Material;
/*    */ import org.bukkit.entity.HumanEntity;
/*    */ import org.bukkit.entity.Player;
/*    */ import org.bukkit.event.EventHandler;
/*    */ import org.bukkit.event.EventPriority;
/*    */ import org.bukkit.event.Listener;
/*    */ import org.bukkit.event.inventory.PrepareItemCraftEvent;
/*    */ import org.bukkit.event.player.PlayerJoinEvent;
/*    */ import org.bukkit.inventory.CraftingInventory;
/*    */ import org.bukkit.inventory.InventoryView;
/*    */ import org.bukkit.inventory.ItemStack;
/*    */ import org.bukkit.inventory.Recipe;
/*    */ import org.bukkit.inventory.meta.SkullMeta;
/*    */ 
/*    */ public class RecipesListener
/*    */   implements Listener
/*    */ {
/*    */   @EventHandler(priority=EventPriority.MONITOR)
/*    */   public void onPlayerJoin(PlayerJoinEvent e)
/*    */   {
/* 20 */     if ((UpdateThread.updateAvailable) && (URPlugin.hasPermission(e.getPlayer(), "ur.update")))
/*    */     {
/* 22 */       e.getPlayer().sendMessage(ChatColor.RED + "[UnlimitedRecipes] An update is available," + (UpdateThread.updateDownloading ? " it will be applied on next restart." : " you can get it here: "));
/* 23 */       e.getPlayer().sendMessage(ChatColor.RED + "http://dev.bukkit.org/bukkit-plugins/unlimitedrecipes/ (click)");
/*    */     }
/*    */   }
/*    */ 
/*    */   @EventHandler
/*    */   public void onPlayerCraftEvent(PrepareItemCraftEvent e)
/*    */   {
/* 30 */     Recipe recipe = e.getRecipe();
/* 31 */     ItemStack result = recipe.getResult();
/* 32 */     CustomRecipe custRecipe = null;
/*    */ 
/* 34 */     if ((custRecipe = RecipesManager.getCustomRecipeByRecipe(recipe)) != null)
/*    */     {
/* 36 */       if ((custRecipe.usePermission) && (!URPlugin.hasPermission(e.getView().getPlayer(), custRecipe.permission)))
/*    */       {
/* 38 */         e.getInventory().setResult(null);
/* 39 */         return;
/*    */       }
/*    */ 
/* 42 */       if ((recipe.getResult().getType() == Material.SKULL_ITEM) && (((SkullMeta)result.getItemMeta()).getOwner().equalsIgnoreCase("--CrafterHead")))
/*    */       {
/* 44 */         SkullMeta meta = (SkullMeta)result.getItemMeta();
/* 45 */         meta.setOwner(e.getView().getPlayer().getName());
/* 46 */         result.setItemMeta(meta);
/* 47 */         e.getInventory().setResult(result);
/*    */       }
/*    */ 
/* 50 */       if (custRecipe.transferDurability)
/*    */       {
/* 52 */         float rendment = 1.0F;
/*    */ 
/* 54 */         for (ItemStack its : e.getInventory().getMatrix())
/*    */         {
/* 56 */           if ((its != null) && (its != e.getInventory().getResult()) && (!its.getType().isBlock()) && (its.getType() != Material.INK_SACK) && (its.getType().getMaxDurability() != 0))
/*    */           {
/* 58 */             float displayDura = its.getType().getMaxDurability() - its.getDurability();
/* 59 */             rendment *= displayDura / its.getType().getMaxDurability();
/*    */           }
/*    */         }
/*    */ 
/* 63 */         short newDurability = (short)(int)(rendment * result.getType().getMaxDurability());
/* 64 */         ItemStack newResult = result.clone();
/*    */ 
/* 66 */         newResult.setDurability((short)(result.getType().getMaxDurability() - newDurability));
/*    */ 
/* 68 */         e.getInventory().setResult(newResult);
/*    */       }
/*    */     }
/*    */   }
/*    */ }

/* Location:           D:\Github\Mechanics\UnlimitedRecipes.jar
 * Qualified Name:     fr.tomcraft.unlimitedrecipes.RecipesListener
 * JD-Core Version:    0.6.2
 */