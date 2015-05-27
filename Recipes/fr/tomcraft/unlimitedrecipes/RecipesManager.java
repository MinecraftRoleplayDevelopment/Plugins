/*     */ package fr.tomcraft.unlimitedrecipes;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import org.bukkit.Bukkit;
/*     */ import org.bukkit.Material;
/*     */ import org.bukkit.inventory.FurnaceRecipe;
/*     */ import org.bukkit.inventory.ItemStack;
/*     */ import org.bukkit.inventory.Recipe;
/*     */ import org.bukkit.inventory.ShapedRecipe;
/*     */ import org.bukkit.inventory.ShapelessRecipe;
/*     */ import org.bukkit.material.MaterialData;
/*     */ 
/*     */ public class RecipesManager
/*     */ {
/*  19 */   public static ArrayList<CustomRecipe> customRecipes = new ArrayList();
/*     */ 
/*     */   public static void reset()
/*     */   {
/*  23 */     Bukkit.resetRecipes();
/*  24 */     customRecipes = new ArrayList();
/*     */   }
/*     */ 
/*     */   public static void registerRecipe(CustomRecipe recipe)
/*     */   {
/*  29 */     if (recipe.type == CustomRecipe.RecipeType.SHAPED_RECIPE)
/*     */     {
/*  31 */       recipe.ingredients = ((ShapedRecipe)recipe.bukkitRecipe).getIngredientMap();
/*     */     }
/*  33 */     customRecipes.add(recipe);
/*     */ 
/*  35 */     if (recipe.deleteOthers)
/*     */     {
/*  37 */       unloadBukkitRecipes(recipe.bukkitRecipe.getResult().getType(), recipe.bukkitRecipe.getResult().getData().getData());
/*     */     }
/*     */ 
/*  40 */     Bukkit.addRecipe(recipe.bukkitRecipe);
/*     */   }
/*     */ 
/*     */   public static void unloadBukkitRecipe(Recipe toUnload)
/*     */   {
/*  45 */     Iterator it = Bukkit.recipeIterator();
/*     */ 
/*  47 */     while (it.hasNext())
/*     */     {
/*  49 */       Recipe recipe = (Recipe)it.next();
/*  50 */       if ((recipe != null) && (!isCustomRecipe(recipe)) && (recipe.equals(toUnload)))
/*     */       {
/*  52 */         it.remove();
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public static void unloadBukkitRecipes(Material toUnload)
/*     */   {
/*  59 */     Iterator it = Bukkit.recipeIterator();
/*     */ 
/*  61 */     while (it.hasNext())
/*     */     {
/*  63 */       Recipe recipe = (Recipe)it.next();
/*  64 */       if ((recipe != null) && (!isCustomRecipe(recipe)) && (recipe.getResult().getType().equals(toUnload)))
/*     */       {
/*  66 */         it.remove();
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public static void unloadBukkitRecipes(Material toUnload, byte data)
/*     */   {
/*  73 */     Iterator it = Bukkit.recipeIterator();
/*     */ 
/*  75 */     while (it.hasNext())
/*     */     {
/*  77 */       Recipe recipe = (Recipe)it.next();
/*  78 */       if ((recipe != null) && (!isCustomRecipe(recipe)) && (recipe.getResult().getType().equals(toUnload)) && (recipe.getResult().getData().getData() == data))
/*     */       {
/*  80 */         it.remove();
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public static CustomRecipe getCustomRecipeByRecipe(Recipe recipe)
/*     */   {
/*  87 */     for (CustomRecipe cust : customRecipes)
/*     */     {
/*  90 */       if ((recipe.getResult().getData().equals(cust.bukkitRecipe.getResult().getData())) && (recipe.getResult().getAmount() == cust.bukkitRecipe.getResult().getAmount()))
/*     */       {
/*  95 */         if (((recipe instanceof ShapedRecipe)) && (cust.type == CustomRecipe.RecipeType.SHAPED_RECIPE))
/*     */         {
/*  97 */           return cust;
/*     */         }
/*  99 */         if (((recipe instanceof ShapelessRecipe)) && (cust.type == CustomRecipe.RecipeType.SHAPELESS_RECIPE))
/*     */         {
/* 101 */           ShapelessRecipe custRecipe = (ShapelessRecipe)cust.bukkitRecipe;
/* 102 */           ShapelessRecipe bukkitRecipe = (ShapelessRecipe)recipe;
/*     */ 
/* 104 */           if ((custRecipe.getIngredientList().size() == bukkitRecipe.getIngredientList().size()) && (custRecipe.getIngredientList().containsAll(bukkitRecipe.getIngredientList())))
/*     */           {
/* 106 */             return cust;
/*     */           }
/*     */         }
/* 109 */         else if (((recipe instanceof FurnaceRecipe)) && (cust.type == CustomRecipe.RecipeType.FURNACE_RECIPE))
/*     */         {
/* 111 */           if (((FurnaceRecipe)cust.bukkitRecipe).getInput().equals(((FurnaceRecipe)recipe).getInput()))
/*     */           {
/* 113 */             return cust;
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/* 117 */     return null;
/*     */   }
/*     */ 
/*     */   public static CustomRecipe getCustomRecipeByResult(String result)
/*     */   {
/* 122 */     for (CustomRecipe cust : customRecipes)
/*     */     {
/* 124 */       ItemStack its = cust.bukkitRecipe.getResult();
/* 125 */       if ((its.getType().name() + ":" + its.getDurability()).equals(result))
/*     */       {
/* 127 */         return cust;
/*     */       }
/*     */     }
/* 130 */     return null;
/*     */   }
/*     */ 
/*     */   public static CustomRecipe getCustomRecipeByName(String name)
/*     */   {
/* 135 */     for (CustomRecipe cust : customRecipes)
/*     */     {
/* 137 */       if (cust.name.equalsIgnoreCase(name))
/*     */       {
/* 139 */         return cust;
/*     */       }
/*     */     }
/* 142 */     return null;
/*     */   }
/*     */ 
/*     */   public static boolean isCustomRecipe(Recipe recipe)
/*     */   {
/* 147 */     return getCustomRecipeByRecipe(recipe) != null;
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\UnlimitedRecipes.jar
 * Qualified Name:     fr.tomcraft.unlimitedrecipes.RecipesManager
 * JD-Core Version:    0.6.2
 */