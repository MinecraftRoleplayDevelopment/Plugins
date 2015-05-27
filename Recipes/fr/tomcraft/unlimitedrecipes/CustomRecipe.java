/*    */ package fr.tomcraft.unlimitedrecipes;
/*    */ 
/*    */ import java.util.Map;
/*    */ import org.bukkit.inventory.ItemStack;
/*    */ import org.bukkit.inventory.Recipe;
/*    */ 
/*    */ public class CustomRecipe
/*    */ {
/*    */   public RecipeType type;
/*    */   public String name;
/*    */   public Recipe bukkitRecipe;
/*    */   public boolean usePermission;
/*    */   public String permission;
/*    */   public boolean deleteOthers;
/*    */   public boolean transferDurability;
/*    */   public Map<Character, ItemStack> ingredients;
/*    */ 
/*    */   public CustomRecipe()
/*    */   {
/*    */   }
/*    */ 
/*    */   public CustomRecipe(RecipeType type, String name, Recipe recipe, boolean usePermission, String permission, boolean override, boolean deleteOthers)
/*    */   {
/* 26 */     this.type = type;
/* 27 */     this.name = name;
/* 28 */     this.bukkitRecipe = recipe;
/* 29 */     this.usePermission = usePermission;
/* 30 */     this.permission = permission;
/* 31 */     this.deleteOthers = deleteOthers;
/*    */   }
/*    */ 
/*    */   public static enum RecipeType
/*    */   {
/* 36 */     SHAPED_RECIPE, SHAPELESS_RECIPE, FURNACE_RECIPE;
/*    */   }
/*    */ }

/* Location:           D:\Github\Mechanics\UnlimitedRecipes.jar
 * Qualified Name:     fr.tomcraft.unlimitedrecipes.CustomRecipe
 * JD-Core Version:    0.6.2
 */