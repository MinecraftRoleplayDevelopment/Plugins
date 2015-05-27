/*     */ package fr.tomcraft.unlimitedrecipes;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.FileNotFoundException;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.List;
/*     */ import java.util.Set;
/*     */ import java.util.logging.Logger;
/*     */ import org.bukkit.ChatColor;
/*     */ import org.bukkit.Color;
/*     */ import org.bukkit.Material;
/*     */ import org.bukkit.configuration.ConfigurationSection;
/*     */ import org.bukkit.configuration.file.FileConfiguration;
/*     */ import org.bukkit.configuration.file.YamlConfiguration;
/*     */ import org.bukkit.enchantments.Enchantment;
/*     */ import org.bukkit.inventory.FurnaceRecipe;
/*     */ import org.bukkit.inventory.ItemStack;
/*     */ import org.bukkit.inventory.Recipe;
/*     */ import org.bukkit.inventory.ShapedRecipe;
/*     */ import org.bukkit.inventory.ShapelessRecipe;
/*     */ import org.bukkit.inventory.meta.ItemMeta;
/*     */ import org.bukkit.inventory.meta.LeatherArmorMeta;
/*     */ import org.bukkit.inventory.meta.SkullMeta;
/*     */ 
/*     */ public class Config
/*     */ {
/*  35 */   private static Logger LOG = Logger.getLogger("Minecraft.UnlimitedRecipes");
/*     */ 
/*  37 */   public static boolean debug = false;
/*     */   public static File craftingFile;
/*     */   public static File furnaceFile;
/*     */   public static FileConfiguration defaultConfig;
/*     */   public static FileConfiguration crafting;
/*     */   public static FileConfiguration furnace;
/*     */ 
/*     */   public static void load()
/*     */   {
/*  48 */     init();
/*  49 */     loadBlackListedRecipes();
/*  50 */     loadCraftingRecipes();
/*  51 */     loadFurnaceRecipes();
/*     */   }
/*     */ 
/*     */   private static void init()
/*     */   {
/*  56 */     URPlugin plugin = URPlugin.instance;
/*  57 */     defaultConfig = plugin.getConfig();
/*     */ 
/*  59 */     if (!plugin.getDataFolder().exists())
/*     */     {
/*  61 */       plugin.getDataFolder().mkdirs();
/*     */     }
/*     */ 
/*  64 */     if (!new File(plugin.getDataFolder(), "config.yml").exists())
/*     */     {
/*  66 */       defaultConfig.set("enableUpdateChecking", Boolean.valueOf(true));
/*  67 */       defaultConfig.set("enableUpdateDownloading", Boolean.valueOf(false));
/*  68 */       defaultConfig.set("blacklisted_items", Arrays.asList(new String[] { "STONE:0", "WORKBENCH", "61" }));
/*  69 */       defaultConfig.set("debug", Boolean.valueOf(false));
/*  70 */       plugin.saveConfig();
/*     */     }
/*     */ 
/*  73 */     debug = defaultConfig.getBoolean("debug");
/*     */ 
/*  75 */     UpdateThread.updateChecking = defaultConfig.getBoolean("enableUpdateChecking");
/*  76 */     UpdateThread.updateDownloading = defaultConfig.getBoolean("enableUpdateDownloading");
/*     */ 
/*  78 */     craftingFile = new File(plugin.getDataFolder(), "crafting.yml");
/*  79 */     furnaceFile = new File(plugin.getDataFolder(), "furnace.yml");
/*     */ 
/*  81 */     if (!craftingFile.exists())
/*     */     {
/*  83 */       extractFile("crafting.yml");
/*     */     }
/*  85 */     if (!furnaceFile.exists())
/*     */     {
/*  87 */       extractFile("furnace.yml");
/*     */     }
/*  89 */     crafting = YamlConfiguration.loadConfiguration(craftingFile);
/*  90 */     furnace = YamlConfiguration.loadConfiguration(furnaceFile);
/*     */   }
/*     */ 
/*     */   private static boolean isInt(String obj)
/*     */   {
/*     */     try
/*     */     {
/*  97 */       Integer.parseInt(obj);
/*  98 */       return true;
/*     */     } catch (Exception e) {
/*     */     }
/* 101 */     return false;
/*     */   }
/*     */ 
/*     */   private static Material getMaterial(String obj)
/*     */   {
/* 107 */     if (isInt(obj))
/*     */     {
/* 109 */       return Material.getMaterial(Integer.parseInt(obj));
/*     */     }
/*     */ 
/* 113 */     return Material.getMaterial(obj);
/*     */   }
/*     */ 
/*     */   public static void loadBlackListedRecipes()
/*     */   {
/* 119 */     if (defaultConfig.getStringList("blacklisted_items") != null)
/*     */     {
/* 121 */       List blackListedItems = defaultConfig.getStringList("blacklisted_items");
/*     */ 
/* 123 */       if (blackListedItems.isEmpty())
/*     */       {
/* 125 */         return;
/*     */       }
/*     */ 
/* 128 */       for (String item : blackListedItems)
/*     */       {
/* 130 */         Material mat = null;
/* 131 */         byte data = -1;
/*     */ 
/* 133 */         if (item.contains(":"))
/*     */         {
/* 135 */           mat = getMaterial(item.split(":")[0]);
/* 136 */           data = Byte.parseByte(item.split(":")[1]);
/* 137 */           RecipesManager.unloadBukkitRecipes(mat, data);
/* 138 */           if (debug)
/*     */           {
/* 140 */             LOG.info("[UnlimitedRecipes] All recipes for " + mat.name() + ":" + data + " were deleted !");
/*     */           }
/*     */         }
/*     */         else
/*     */         {
/* 145 */           mat = getMaterial(item);
/* 146 */           RecipesManager.unloadBukkitRecipes(mat);
/* 147 */           if (debug)
/*     */           {
/* 149 */             LOG.info("[UnlimitedRecipes] All recipes for " + mat.name() + " were deleted !");
/*     */           }
/*     */         }
/*     */       }
/*     */ 
/* 154 */       LOG.info("[UnlimitedRecipes] Recipes were deleted ! (" + blackListedItems.size() + " items)");
/*     */     }
/*     */   }
/*     */ 
/*     */   public static void loadCraftingRecipes()
/*     */   {
/* 160 */     if (crafting.getConfigurationSection("config.crafts") != null)
/*     */     {
/* 162 */       Set keys = crafting.getConfigurationSection("config.crafts").getKeys(false);
/*     */ 
/* 164 */       if ((keys == null) || (keys.isEmpty()))
/*     */       {
/* 166 */         return;
/*     */       }
/*     */ 
/* 169 */       for (String name : keys)
/*     */       {
/* 171 */         String key = "config.crafts." + name;
/* 172 */         Material toCraft = getMaterial(crafting.getString(key + ".itemID"));
/* 173 */         Object metad = crafting.get(key + ".metadata");
/* 174 */         int quantity = crafting.getInt(key + ".quantity");
/* 175 */         List enchants = crafting.getStringList(key + ".enchantments");
/* 176 */         List lores = crafting.getStringList(key + ".lores");
/* 177 */         CustomRecipe.RecipeType recipeType = crafting.getBoolean(key + ".shapelessRecipe") ? CustomRecipe.RecipeType.SHAPELESS_RECIPE : CustomRecipe.RecipeType.SHAPED_RECIPE;
/* 178 */         boolean deleteOthers = crafting.getBoolean(key + ".deleteOthers");
/* 179 */         boolean transferDurability = crafting.getBoolean(key + ".transferDurability");
/* 180 */         boolean usePermission = crafting.getBoolean(key + ".usePermission");
/* 181 */         String customName = color(crafting.getString(key + ".customName"));
/*     */ 
/* 183 */         short metadata = 0;
/* 184 */         String permission = "ur.craft." + name;
/*     */         ItemStack shpedre;
/* 187 */         if (((metad instanceof String)) && (toCraft == Material.SKULL_ITEM))
/*     */         {
/* 189 */           ItemStack shpedre = new ItemStack(toCraft, quantity, (short)3);
/* 190 */           SkullMeta meta = (SkullMeta)shpedre.getItemMeta();
/* 191 */           meta.setOwner(String.valueOf(metad));
/* 192 */           shpedre.setItemMeta(meta);
/* 193 */           metadata = 3;
/*     */         }
/* 195 */         else if (((metad instanceof String)) && (toCraft.name().contains("LEATHER_")))
/*     */         {
/* 197 */           ItemStack shpedre = new ItemStack(toCraft, quantity);
/* 198 */           LeatherArmorMeta meta = (LeatherArmorMeta)shpedre.getItemMeta();
/* 199 */           meta.setColor(Color.fromRGB(Integer.parseInt(((String)metad).split("r:")[1].split(";")[0].trim()), Integer.parseInt(((String)metad).split("g:")[1].split(";")[0].trim()), Integer.parseInt(((String)metad).split("b:")[1].split(";")[0].trim())));
/* 200 */           shpedre.setItemMeta(meta);
/*     */         }
/*     */         else
/*     */         {
/* 204 */           metadata = (short)crafting.getInt(key + ".metadata");
/* 205 */           shpedre = new ItemStack(toCraft, quantity, metadata);
/*     */         }
/*     */ 
/* 208 */         applyCustomName(shpedre, customName);
/*     */ 
/* 210 */         applyLores(shpedre, lores);
/*     */ 
/* 212 */         applyEnchants(shpedre, enchants);
/*     */ 
/* 214 */         Recipe recipe = new ShapedRecipe(shpedre);
/* 215 */         if (recipeType == CustomRecipe.RecipeType.SHAPELESS_RECIPE)
/*     */         {
/* 217 */           recipe = new ShapelessRecipe(shpedre);
/*     */         }
/* 219 */         CustomRecipe custRecipe = new CustomRecipe();
/* 220 */         custRecipe.type = recipeType;
/* 221 */         custRecipe.name = name;
/* 222 */         custRecipe.usePermission = usePermission;
/* 223 */         custRecipe.permission = permission;
/* 224 */         custRecipe.deleteOthers = deleteOthers;
/* 225 */         custRecipe.transferDurability = transferDurability;
/* 226 */         if (recipeType == CustomRecipe.RecipeType.SHAPED_RECIPE)
/*     */         {
/* 228 */           List shape_list = crafting.getStringList(key + ".recipe");
/* 229 */           String[] shape = new String[shape_list.size()];
/* 230 */           for (int i = 0; i < shape.length; i++)
/*     */           {
/* 232 */             shape[i] = ((String)shape_list.get(i));
/*     */           }
/* 234 */           ((ShapedRecipe)recipe).shape(shape);
/*     */         }
/*     */ 
/* 237 */         Set keys2 = crafting.getConfigurationSection(key + ".ingredientsID").getKeys(false);
/* 238 */         for (String key2 : keys2)
/*     */         {
/* 240 */           ConfigurationSection section2 = crafting.getConfigurationSection(key + ".ingredientsID");
/* 241 */           char c = key2.charAt(0);
/* 242 */           byte metaIng = -1;
/* 243 */           int quantityIng = 1;
/*     */ 
/* 245 */           String readed = section2.getString(key2);
/* 246 */           if (RecipesManager.getCustomRecipeByName(readed) != null)
/*     */           {
/*     */             try
/*     */             {
/* 250 */               if (recipeType == CustomRecipe.RecipeType.SHAPED_RECIPE)
/*     */               {
/* 252 */                 ((ShapedRecipe)recipe).setIngredient(c, RecipesManager.getCustomRecipeByName(readed).bukkitRecipe.getResult().getData());
/*     */               }
/*     */               else
/*     */               {
/* 256 */                 ((ShapelessRecipe)recipe).addIngredient(RecipesManager.getCustomRecipeByName(readed).bukkitRecipe.getResult().getData());
/*     */               }
/*     */             }
/*     */             catch (Exception e)
/*     */             {
/* 261 */               LOG.severe("[UnlimitedRecipes] Error while adding bukkitRecipe for: " + toCraft.name() + ":" + metadata);
/* 262 */               e.printStackTrace();
/*     */             }
/*     */           }
/*     */           else
/*     */           {
/*     */             Material materialIng;
/* 267 */             if ((readed.contains(":")) && (readed.contains("x")))
/*     */             {
/* 269 */               metaIng = Byte.parseByte(readed.split(":")[1].split("x")[0]);
/* 270 */               Material materialIng = getMaterial(readed.split(":")[0]);
/* 271 */               quantityIng = Short.parseShort(readed.split("x")[1]);
/*     */             }
/*     */             else
/*     */             {
/*     */               Material materialIng;
/* 273 */               if (readed.contains(":"))
/*     */               {
/* 275 */                 metaIng = Byte.parseByte(readed.split(":")[1]);
/* 276 */                 materialIng = getMaterial(readed.split(":")[0]);
/*     */               }
/* 278 */               else if (readed.contains("x"))
/*     */               {
/* 280 */                 metaIng = Byte.parseByte(readed.split("x")[0]);
/* 281 */                 Material materialIng = getMaterial(readed.split(":")[0]);
/* 282 */                 quantityIng = Integer.parseInt(readed.split("x")[1]);
/*     */               }
/*     */               else
/*     */               {
/* 286 */                 materialIng = getMaterial(readed);
/*     */               }
/*     */             }
/*     */             try {
/* 290 */               if (recipeType == CustomRecipe.RecipeType.SHAPED_RECIPE)
/*     */               {
/* 292 */                 ((ShapedRecipe)recipe).setIngredient(c, materialIng, metaIng);
/*     */               }
/*     */               else
/*     */               {
/* 296 */                 ((ShapelessRecipe)recipe).addIngredient(quantityIng, materialIng, metaIng);
/*     */               }
/*     */             }
/*     */             catch (Exception e)
/*     */             {
/* 301 */               LOG.severe("[UnlimitedRecipes] Error while adding bukkitRecipe for: " + toCraft.name() + ":" + metadata);
/* 302 */               e.printStackTrace();
/*     */             }
/*     */           }
/*     */         }
/* 306 */         custRecipe.bukkitRecipe = recipe;
/* 307 */         RecipesManager.registerRecipe(custRecipe);
/* 308 */         if (debug)
/*     */         {
/* 310 */           LOG.info("[UnlimitedRecipes] Crafting Recipe for: " + toCraft.name() + ":" + metadata + " added !");
/*     */         }
/*     */       }
/* 313 */       LOG.info("[UnlimitedRecipes] All craft recipes loaded ! (" + keys.size() + " recipes)");
/*     */     }
/*     */   }
/*     */ 
/*     */   public static void loadFurnaceRecipes()
/*     */   {
/* 319 */     if (furnace.getConfigurationSection("config.smelts") != null)
/*     */     {
/* 321 */       Set keys = furnace.getConfigurationSection("config.smelts").getKeys(false);
/*     */ 
/* 323 */       if ((keys == null) || (keys.isEmpty()))
/*     */       {
/* 325 */         return;
/*     */       }
/*     */ 
/* 328 */       for (String name : keys)
/*     */       {
/* 330 */         String key = "config.smelts." + name;
/* 331 */         Material material = getMaterial(furnace.getString(key + ".resultID"));
/* 332 */         byte metaResult = (byte)furnace.getInt(key + ".result_MetaData");
/* 333 */         String customName = furnace.getString(key + ".result_customName");
/* 334 */         List lores = furnace.getStringList(key + ".result_lores");
/* 335 */         Material ingredient = getMaterial(furnace.getString(key + ".ingredientID"));
/* 336 */         furnace.getInt(key + ".ingredient_MetaData");
/* 337 */         ItemStack shpedre = new ItemStack(material, 1, (short)metaResult);
/* 338 */         applyCustomName(shpedre, customName);
/* 339 */         applyLores(shpedre, lores);
/* 340 */         FurnaceRecipe recipe = new FurnaceRecipe(shpedre, ingredient.getNewData(metaResult));
/* 341 */         RecipesManager.registerRecipe(new CustomRecipe(CustomRecipe.RecipeType.FURNACE_RECIPE, name, recipe, false, null, false, false));
/* 342 */         if (debug)
/*     */         {
/* 344 */           LOG.info("[UnlimitedRecipes] Furnace Recipe for: " + material.name() + ":" + metaResult + " added !");
/*     */         }
/*     */       }
/* 347 */       LOG.info("[UnlimitedRecipes] All smelt recipes loaded ! (" + keys.size() + " recipes)");
/*     */     }
/*     */   }
/*     */ 
/*     */   public static String color(String string)
/*     */   {
/* 353 */     return string == null ? null : ChatColor.translateAlternateColorCodes('&', string);
/*     */   }
/*     */ 
/*     */   private static void applyEnchants(ItemStack its, List<String> enchants)
/*     */   {
/* 358 */     if ((enchants != null) && (!enchants.isEmpty()))
/*     */     {
/* 360 */       for (String str : enchants)
/*     */       {
/* 362 */         its.addUnsafeEnchantment(getEnchantment(str.split(":")[0]), Integer.valueOf(str.split(":")[1]).intValue());
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private static void applyLores(ItemStack its, List<String> lores)
/*     */   {
/* 369 */     if ((lores != null) && (!lores.isEmpty()))
/*     */     {
/* 371 */       List lstmp = new ArrayList();
/* 372 */       for (String s : lores)
/*     */       {
/* 374 */         lstmp.add(ChatColor.RESET + color(s));
/*     */       }
/* 376 */       ItemMeta tmp = its.getItemMeta();
/* 377 */       tmp.setLore(lstmp);
/* 378 */       its.setItemMeta(tmp);
/*     */     }
/*     */   }
/*     */ 
/*     */   private static void applyCustomName(ItemStack its, String name)
/*     */   {
/* 384 */     if (name != null)
/*     */     {
/* 386 */       ItemMeta tmp = its.getItemMeta();
/* 387 */       tmp.setDisplayName(ChatColor.RESET + color(name));
/* 388 */       its.setItemMeta(tmp);
/*     */     }
/*     */   }
/*     */ 
/*     */   private static Enchantment getEnchantment(String obj)
/*     */   {
/* 394 */     if (isInt(obj))
/*     */     {
/* 396 */       return Enchantment.getById(Integer.valueOf(obj).intValue());
/*     */     }
/*     */ 
/* 400 */     return Enchantment.getByName(obj);
/*     */   }
/*     */ 
/*     */   public void saveCraftingConfig()
/*     */   {
/*     */     try
/*     */     {
/* 408 */       URPlugin.instance.getDataFolder().mkdirs();
/* 409 */       crafting.save(craftingFile);
/*     */     }
/*     */     catch (IOException e)
/*     */     {
/* 413 */       e.printStackTrace();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void saveFurnaceConfig()
/*     */   {
/*     */     try
/*     */     {
/* 421 */       URPlugin.instance.getDataFolder().mkdirs();
/* 422 */       furnace.save(furnaceFile);
/*     */     }
/*     */     catch (IOException e)
/*     */     {
/* 426 */       e.printStackTrace();
/*     */     }
/*     */   }
/*     */ 
/*     */   private static void extractFile(String file)
/*     */   {
/* 432 */     InputStream is = URPlugin.instance.getResource(file);
/*     */     try
/*     */     {
/* 435 */       OutputStream out = new FileOutputStream(new File(URPlugin.instance.getDataFolder(), file));
/*     */       try
/*     */       {
/* 438 */         byte[] buf = new byte[8192];
/*     */         int len;
/* 440 */         while ((len = is.read(buf)) >= 0)
/*     */         {
/* 442 */           out.write(buf, 0, len);
/*     */         }
/*     */       }
/*     */       catch (IOException e)
/*     */       {
/* 447 */         e.printStackTrace();
/*     */       }
/*     */       finally
/*     */       {
/*     */       }
/*     */ 
/*     */     }
/*     */     catch (FileNotFoundException e)
/*     */     {
/* 463 */       e.printStackTrace();
/*     */     }
/*     */     finally
/*     */     {
/*     */       try
/*     */       {
/* 469 */         is.close();
/*     */       }
/*     */       catch (IOException e)
/*     */       {
/* 473 */         e.printStackTrace();
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\UnlimitedRecipes.jar
 * Qualified Name:     fr.tomcraft.unlimitedrecipes.Config
 * JD-Core Version:    0.6.2
 */