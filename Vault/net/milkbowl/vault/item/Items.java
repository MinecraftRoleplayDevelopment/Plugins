/*      */ package net.milkbowl.vault.item;
/*      */ 
/*      */ import java.util.ArrayList;
/*      */ import java.util.Collections;
/*      */ import java.util.List;
/*      */ import java.util.concurrent.CopyOnWriteArrayList;
/*      */ import java.util.regex.Matcher;
/*      */ import java.util.regex.Pattern;
/*      */ import org.bukkit.Material;
/*      */ import org.bukkit.inventory.ItemStack;
/*      */ 
/*      */ public class Items
/*      */ {
/*   30 */   private static final List<ItemInfo> items = new CopyOnWriteArrayList();
/*      */ 
/*      */   public static List<ItemInfo> getItemList()
/*      */   {
/*   37 */     return Collections.unmodifiableList(items);
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public static ItemInfo itemById(int typeId)
/*      */   {
/*  719 */     return itemByType(Material.getMaterial(typeId), (short)0);
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public static ItemInfo itemById(int typeId, short subType) {
/*  724 */     return itemByType(Material.getMaterial(typeId), subType);
/*      */   }
/*      */ 
/*      */   public static ItemInfo itemByStack(ItemStack itemStack)
/*      */   {
/*  733 */     if (itemStack == null) {
/*  734 */       return null;
/*      */     }
/*      */ 
/*  737 */     for (ItemInfo item : items) {
/*  738 */       if ((itemStack.getType().equals(item.getType())) && (item.isDurable()))
/*  739 */         return item;
/*  740 */       if ((itemStack.getType().equals(item.getType())) && (item.getSubTypeId() == itemStack.getDurability())) {
/*  741 */         return item;
/*      */       }
/*      */     }
/*      */ 
/*  745 */     return null;
/*      */   }
/*      */ 
/*      */   public static ItemInfo itemByItem(ItemInfo item) {
/*  749 */     for (ItemInfo i : items) {
/*  750 */       if (item.equals(i)) {
/*  751 */         return i;
/*      */       }
/*      */     }
/*  754 */     return null;
/*      */   }
/*      */ 
/*      */   public static ItemInfo itemByType(Material type)
/*      */   {
/*  763 */     return itemByType(type, (short)0);
/*      */   }
/*      */ 
/*      */   public static ItemInfo itemByType(Material type, short subType)
/*      */   {
/*  773 */     for (ItemInfo item : items) {
/*  774 */       if ((item.getType() == type) && (item.getSubTypeId() == subType)) {
/*  775 */         return item;
/*      */       }
/*      */     }
/*  778 */     return null;
/*      */   }
/*      */ 
/*      */   public static ItemInfo itemByString(String string)
/*      */   {
/*  793 */     Pattern pattern = Pattern.compile("(?i)^(\\d+)$");
/*  794 */     Matcher matcher = pattern.matcher(string);
/*  795 */     if (matcher.find()) {
/*  796 */       int id = Integer.parseInt(matcher.group(1));
/*  797 */       return itemById(id);
/*      */     }
/*      */ 
/*  801 */     matcher.reset();
/*  802 */     pattern = Pattern.compile("(?i)^(\\d+):(\\d+)$");
/*  803 */     matcher = pattern.matcher(string);
/*  804 */     if (matcher.find()) {
/*  805 */       int id = Integer.parseInt(matcher.group(1));
/*  806 */       short type = Short.parseShort(matcher.group(2));
/*  807 */       return itemById(id, type);
/*      */     }
/*      */ 
/*  811 */     matcher.reset();
/*  812 */     pattern = Pattern.compile("(?i)^(.*)$");
/*  813 */     matcher = pattern.matcher(string);
/*  814 */     if (matcher.find()) {
/*  815 */       String name = matcher.group(1);
/*  816 */       return itemByName(name);
/*      */     }
/*      */ 
/*  819 */     return null;
/*      */   }
/*      */ 
/*      */   public static ItemInfo itemByName(ArrayList<String> search) {
/*  823 */     String searchString = join(search, " ");
/*  824 */     return itemByName(searchString);
/*      */   }
/*      */ 
/*      */   public static ItemInfo[] itemByNames(ArrayList<String> search, boolean multi) {
/*  828 */     String searchString = join(search, " ");
/*  829 */     return itemsByName(searchString, multi);
/*      */   }
/*      */ 
/*      */   public static ItemInfo[] itemsByName(String searchString, boolean multi)
/*      */   {
/*  841 */     if (!multi) {
/*  842 */       return new ItemInfo[] { itemByName(searchString) };
/*      */     }
/*      */ 
/*  845 */     ItemInfo[] itemList = new ItemInfo[0];
/*      */     int typeId;
/*      */     short subTypeId;
/*      */     int i;
/*  847 */     if (searchString.matches("\\d+:\\d+"))
/*      */     {
/*  851 */       String[] params = searchString.split(":");
/*  852 */       typeId = Integer.parseInt(params[0]);
/*  853 */       subTypeId = Short.parseShort(params[1]);
/*      */ 
/*  856 */       for (ItemInfo item : items)
/*      */       {
/*  858 */         if ((item.getId() == typeId) && (item.getSubTypeId() == subTypeId)) {
/*  859 */           itemList[0] = item;
/*  860 */           break;
/*      */         }
/*      */       }
/*      */     }
/*      */     else
/*      */     {
/*      */       int typeId;
/*      */       int i;
/*  863 */       if (searchString.matches("\\d+"))
/*      */       {
/*  866 */         typeId = Integer.parseInt(searchString);
/*      */ 
/*  869 */         i = 0;
/*  870 */         for (ItemInfo item : items)
/*      */         {
/*  872 */           if (item.getId() == typeId) {
/*  873 */             itemList[i] = item;
/*  874 */             i++;
/*      */           }
/*      */         }
/*      */ 
/*      */       }
/*      */       else
/*      */       {
/*  881 */         i = 0;
/*  882 */         for (ItemInfo item : items)
/*      */         {
/*  884 */           for (String[] attributes : item.search) {
/*  885 */             boolean match = false;
/*      */ 
/*  887 */             for (String attribute : attributes) {
/*  888 */               if (searchString.toLowerCase().contains(attribute)) {
/*  889 */                 match = true;
/*  890 */                 break;
/*      */               }
/*      */             }
/*      */ 
/*  894 */             if (match) {
/*  895 */               itemList[i] = item;
/*  896 */               i++;
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*  902 */     return itemList;
/*      */   }
/*      */ 
/*      */   public static ItemInfo itemByName(String searchString)
/*      */   {
/*  912 */     ItemInfo matchedItem = null;
/*  913 */     int matchedItemStrength = 0;
/*  914 */     int matchedValue = 0;
/*      */     int typeId;
/*      */     short subTypeId;
/*  916 */     if (searchString.matches("\\d+:\\d+"))
/*      */     {
/*  920 */       String[] params = searchString.split(":");
/*  921 */       typeId = Integer.parseInt(params[0]);
/*  922 */       subTypeId = Short.parseShort(params[1]);
/*      */ 
/*  925 */       for (ItemInfo item : items)
/*      */       {
/*  927 */         if ((item.getId() == typeId) && (item.getSubTypeId() == subTypeId)) {
/*  928 */           matchedItem = item;
/*  929 */           break;
/*      */         }
/*      */       }
/*      */     }
/*      */     else
/*      */     {
/*      */       int typeId;
/*      */       short subTypeId;
/*  932 */       if (searchString.matches("\\d+"))
/*      */       {
/*  936 */         typeId = Integer.parseInt(searchString);
/*  937 */         subTypeId = 0;
/*      */ 
/*  940 */         for (ItemInfo item : items)
/*      */         {
/*  942 */           if ((item.getId() == typeId) && (item.getSubTypeId() == subTypeId)) {
/*  943 */             matchedItem = item;
/*  944 */             break;
/*      */           }
/*      */         }
/*      */ 
/*      */       }
/*      */       else
/*      */       {
/*  951 */         for (ItemInfo item : items)
/*      */         {
/*  953 */           for (String[] attributes : item.search) {
/*  954 */             int val = 0;
/*  955 */             boolean match = false;
/*      */ 
/*  957 */             for (String attribute : attributes) {
/*  958 */               if (searchString.toLowerCase().contains(attribute)) {
/*  959 */                 val += attribute.length();
/*  960 */                 match = true;
/*      */               } else {
/*  962 */                 match = false;
/*  963 */                 break;
/*      */               }
/*      */ 
/*      */             }
/*      */ 
/*  968 */             if ((match) && (
/*  969 */               (matchedItem == null) || (val > matchedValue) || (attributes.length > matchedItemStrength))) {
/*  970 */               matchedItem = item;
/*  971 */               matchedValue = val;
/*  972 */               matchedItemStrength = attributes.length;
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/*  979 */     return matchedItem;
/*      */   }
/*      */ 
/*      */   public static String join(String[] array, String glue)
/*      */   {
/*  989 */     String joined = null;
/*  990 */     for (String element : array) {
/*  991 */       if (joined == null)
/*  992 */         joined = element;
/*      */       else {
/*  994 */         joined = joined + glue + element;
/*      */       }
/*      */     }
/*      */ 
/*  998 */     if (joined == null) {
/*  999 */       return "";
/*      */     }
/* 1001 */     return joined;
/*      */   }
/*      */ 
/*      */   public static String join(List<String> list, String glue)
/*      */   {
/* 1012 */     String joined = null;
/* 1013 */     for (String element : list) {
/* 1014 */       if (joined == null)
/* 1015 */         joined = element;
/*      */       else {
/* 1017 */         joined = joined + glue + element;
/*      */       }
/*      */     }
/*      */ 
/* 1021 */     if (joined == null) {
/* 1022 */       return "";
/*      */     }
/* 1024 */     return joined;
/*      */   }
/*      */ 
/*      */   static
/*      */   {
/*   41 */     items.add(new ItemInfo("Air", new String[][] { { "air" } }, Material.AIR));
/*   42 */     items.add(new ItemInfo("Stone", new String[][] { { "ston" }, { "smoo", "sto" } }, Material.STONE));
/*   43 */     items.add(new ItemInfo("Grass", new String[][] { { "gras" } }, Material.GRASS));
/*   44 */     items.add(new ItemInfo("Dirt", new String[][] { { "dirt" } }, Material.DIRT));
/*   45 */     items.add(new ItemInfo("Cobblestone", new String[][] { { "cobb", "sto" }, { "cobb" } }, Material.COBBLESTONE));
/*   46 */     items.add(new ItemInfo("Oak Plank", new String[][] { { "wood" }, { "oak", "plank" }, { "oak", "wood" } }, Material.WOOD));
/*   47 */     items.add(new ItemInfo("Spruce Plank", new String[][] { { "spru", "plank" }, { "spruc", "wood" } }, Material.WOOD, (short)1));
/*   48 */     items.add(new ItemInfo("Birch Plank", new String[][] { { "birch", "plank" }, { "birch", "wood" } }, Material.WOOD, (short)2));
/*   49 */     items.add(new ItemInfo("Jungle Plank", new String[][] { { "jung", "plank" }, { "jung", "wood" } }, Material.WOOD, (short)3));
/*   50 */     items.add(new ItemInfo("Oak Sapling", new String[][] { { "sapl" }, { "sapl", "oak" } }, Material.SAPLING));
/*   51 */     items.add(new ItemInfo("Spruce Sapling", new String[][] { { "sapl", "spruc" } }, Material.SAPLING, (short)1));
/*   52 */     items.add(new ItemInfo("Birch Sapling", new String[][] { { "sapl", "birch" } }, Material.SAPLING, (short)2));
/*   53 */     items.add(new ItemInfo("Jungle Sapling", new String[][] { { "sapl", "jungle" } }, Material.SAPLING, (short)3));
/*   54 */     items.add(new ItemInfo("Bedrock", new String[][] { { "rock" } }, Material.BEDROCK));
/*   55 */     items.add(new ItemInfo("Water", new String[][] { { "water" } }, Material.WATER));
/*   56 */     items.add(new ItemInfo("Lava", new String[][] { { "lava" } }, Material.LAVA));
/*   57 */     items.add(new ItemInfo("Sand", new String[][] { { "sand" } }, Material.SAND));
/*   58 */     items.add(new ItemInfo("Gold Ore", new String[][] { { "ore", "gold" } }, Material.GOLD_ORE));
/*   59 */     items.add(new ItemInfo("Iron Ore", new String[][] { { "ore", "iron" } }, Material.IRON_ORE));
/*   60 */     items.add(new ItemInfo("Coal Ore", new String[][] { { "ore", "coal" } }, Material.COAL_ORE));
/*   61 */     items.add(new ItemInfo("Gravel", new String[][] { { "grav" } }, Material.GRAVEL));
/*   62 */     items.add(new ItemInfo("Oak Log", new String[][] { { "oak" }, { "log" }, { "oak", "log" } }, Material.LOG));
/*   63 */     items.add(new ItemInfo("Spruce Log", new String[][] { { "spruc" }, { "spruc", "log" } }, Material.LOG, (short)1));
/*   64 */     items.add(new ItemInfo("Birch Log", new String[][] { { "birch" }, { "birch", "log" } }, Material.LOG, (short)2));
/*   65 */     items.add(new ItemInfo("Jungle Log", new String[][] { { "jung", "log" } }, Material.LOG, (short)3));
/*   66 */     items.add(new ItemInfo("Leaves Block", new String[][] { { "blo", "leaf" }, { "blo", "leaves" } }, Material.LEAVES));
/*   67 */     items.add(new ItemInfo("Spruce Leaves Block", new String[][] { { "blo", "lea", "spruc" } }, Material.LEAVES, (short)1));
/*   68 */     items.add(new ItemInfo("Birch Leaves Block", new String[][] { { "blo", "lea", "birch" } }, Material.LEAVES, (short)2));
/*   69 */     items.add(new ItemInfo("Jungle Leaves Block", new String[][] { { "blo", "lea", "jung" } }, Material.LEAVES, (short)3));
/*   70 */     items.add(new ItemInfo("Leaves", new String[][] { { "leaf" }, { "leaves" } }, Material.LEAVES, (short)4));
/*   71 */     items.add(new ItemInfo("Spruce Leaves", new String[][] { { "lea", "spruce" } }, Material.LEAVES, (short)5));
/*   72 */     items.add(new ItemInfo("Birch Leaves", new String[][] { { "lea", "birch" } }, Material.LEAVES, (short)6));
/*   73 */     items.add(new ItemInfo("Jungle Leaves", new String[][] { { "lea", "jung" } }, Material.LEAVES, (short)7));
/*   74 */     items.add(new ItemInfo("Sponge", new String[][] { { "sponge" } }, Material.SPONGE));
/*   75 */     items.add(new ItemInfo("Glass", new String[][] { { "glas" }, { "sili" } }, Material.GLASS));
/*   76 */     items.add(new ItemInfo("Lapis Lazuli Ore", new String[][] { { "lap", "laz", "ore" }, { "lazul", "ore" }, { "ore", "lapiz" } }, Material.LAPIS_ORE));
/*   77 */     items.add(new ItemInfo("Lapis Lazuli Block", new String[][] { { "lap", "laz", "bloc" }, { "lazu", "bloc" }, { "blo", "lapi" } }, Material.LAPIS_BLOCK));
/*   78 */     items.add(new ItemInfo("Dispenser", new String[][] { { "dispen" } }, Material.DISPENSER));
/*   79 */     items.add(new ItemInfo("Sandstone", new String[][] { { "sand", "st" } }, Material.SANDSTONE));
/*   80 */     items.add(new ItemInfo("Chiseled Sandstone", new String[][] { { "chis", "sand", "sto" } }, Material.SANDSTONE, (short)1));
/*   81 */     items.add(new ItemInfo("Smooth Sandstone", new String[][] { { "smoo", "sand", "sto" } }, Material.SANDSTONE, (short)2));
/*   82 */     items.add(new ItemInfo("Note Block", new String[][] { { "note" } }, Material.NOTE_BLOCK));
/*   83 */     items.add(new ItemInfo("Bed Block", new String[][] { { "block", "bed" } }, Material.BED_BLOCK));
/*   84 */     items.add(new ItemInfo("Powered Rail", new String[][] { { "rail", "pow" }, { "trac", "pow" }, { "boost" } }, Material.POWERED_RAIL));
/*   85 */     items.add(new ItemInfo("Detector Rail", new String[][] { { "rail", "det" }, { "trac", "det" }, { "detec" } }, Material.DETECTOR_RAIL));
/*   86 */     items.add(new ItemInfo("Sticky Piston", new String[][] { { "stic", "pis" } }, Material.PISTON_STICKY_BASE));
/*   87 */     items.add(new ItemInfo("Web", new String[][] { { "web" }, { "cobw" } }, Material.WEB));
/*   88 */     items.add(new ItemInfo("Dead Shrub", new String[][] { { "dead", "shru" }, { "dese", "shru" }, { "shrub" } }, Material.LONG_GRASS, (short)0));
/*   89 */     items.add(new ItemInfo("Tall Grass", new String[][] { { "tall", "gras" }, { "long", "gras" } }, Material.LONG_GRASS, (short)1));
/*   90 */     items.add(new ItemInfo("Fern", new String[][] { { "fern" } }, Material.LONG_GRASS, (short)2));
/*   91 */     items.add(new ItemInfo("Piston", new String[][] { { "pisto" } }, Material.PISTON_BASE));
/*   92 */     items.add(new ItemInfo("White Wool", new String[][] { { "wool", "whit" }, { "wool" } }, Material.WOOL));
/*   93 */     items.add(new ItemInfo("Orange Wool", new String[][] { { "wool", "ora" } }, Material.WOOL, (short)1));
/*   94 */     items.add(new ItemInfo("Magenta Wool", new String[][] { { "wool", "mag" } }, Material.WOOL, (short)2));
/*   95 */     items.add(new ItemInfo("Light Blue Wool", new String[][] { { "wool", "lig", "blue" } }, Material.WOOL, (short)3));
/*   96 */     items.add(new ItemInfo("Yellow Wool", new String[][] { { "wool", "yell" } }, Material.WOOL, (short)4));
/*   97 */     items.add(new ItemInfo("Light Green Wool", new String[][] { { "wool", "lig", "gree" }, { "wool", "gree" } }, Material.WOOL, (short)5));
/*   98 */     items.add(new ItemInfo("Pink Wool", new String[][] { { "wool", "pink" } }, Material.WOOL, (short)6));
/*   99 */     items.add(new ItemInfo("Gray Wool", new String[][] { { "wool", "gray" }, { "wool", "grey" } }, Material.WOOL, (short)7));
/*  100 */     items.add(new ItemInfo("Light Gray Wool", new String[][] { { "lig", "wool", "gra" }, { "lig", "wool", "gre" } }, Material.WOOL, (short)8));
/*  101 */     items.add(new ItemInfo("Cyan Wool", new String[][] { { "wool", "cya" } }, Material.WOOL, (short)9));
/*  102 */     items.add(new ItemInfo("Purple Wool", new String[][] { { "wool", "pur" } }, Material.WOOL, (short)10));
/*  103 */     items.add(new ItemInfo("Blue Wool", new String[][] { { "wool", "blue" } }, Material.WOOL, (short)11));
/*  104 */     items.add(new ItemInfo("Brown Wool", new String[][] { { "wool", "brow" } }, Material.WOOL, (short)12));
/*  105 */     items.add(new ItemInfo("Dark Green Wool", new String[][] { { "wool", "dar", "gree" }, { "wool", "gree" } }, Material.WOOL, (short)13));
/*  106 */     items.add(new ItemInfo("Red Wool", new String[][] { { "wool", "red" } }, Material.WOOL, (short)14));
/*  107 */     items.add(new ItemInfo("Black Wool", new String[][] { { "wool", "bla" } }, Material.WOOL, (short)15));
/*  108 */     items.add(new ItemInfo("Dandelion", new String[][] { { "flow", "yell" }, { "dande" } }, Material.YELLOW_FLOWER));
/*  109 */     items.add(new ItemInfo("Brown Mushroom", new String[][] { { "mush", "bro" } }, Material.BROWN_MUSHROOM));
/*  110 */     items.add(new ItemInfo("Red Mushroom", new String[][] { { "mush", "red" } }, Material.RED_MUSHROOM));
/*  111 */     items.add(new ItemInfo("Gold Block", new String[][] { { "gold", "bl" } }, Material.GOLD_BLOCK));
/*  112 */     items.add(new ItemInfo("Iron Block", new String[][] { { "iron", "bl" } }, Material.IRON_BLOCK));
/*      */ 
/*  123 */     items.add(new ItemInfo("Stone Slab", new String[][] { { "slab", "sto" }, { "slab" }, { "step", "ston" } }, Material.STEP));
/*  124 */     items.add(new ItemInfo("Sandstone Slab", new String[][] { { "slab", "sand", "sto" }, { "step", "sand", "sto" } }, Material.STEP, (short)1));
/*  125 */     items.add(new ItemInfo("Wooden Slab", new String[][] { { "slab", "woo" }, { "step", "woo" } }, Material.STEP, (short)2));
/*  126 */     items.add(new ItemInfo("Cobblestone Slab", new String[][] { { "slab", "cob", "sto" }, { "slab", "cob" } }, Material.STEP, (short)3));
/*  127 */     items.add(new ItemInfo("Brick Slab", new String[][] { { "slab", "bri" } }, Material.STEP, (short)4));
/*  128 */     items.add(new ItemInfo("Stone Brick Slab", new String[][] { { "slab", "sto", "bri" } }, Material.STEP, (short)5));
/*  129 */     items.add(new ItemInfo("Brick", new String[][] { { "bric" } }, Material.BRICK));
/*  130 */     items.add(new ItemInfo("TNT", new String[][] { { "tnt" }, { "boom" } }, Material.TNT));
/*  131 */     items.add(new ItemInfo("Bookshelf", new String[][] { { "bookshe" }, { "book", "she" } }, Material.BOOKSHELF));
/*  132 */     items.add(new ItemInfo("Moss Stone", new String[][] { { "moss", "sto" }, { "moss" } }, Material.MOSSY_COBBLESTONE));
/*  133 */     items.add(new ItemInfo("Obsidian", new String[][] { { "obsi" } }, Material.OBSIDIAN));
/*  134 */     items.add(new ItemInfo("Torch", new String[][] { { "torc" } }, Material.TORCH));
/*  135 */     items.add(new ItemInfo("Fire", new String[][] { { "fire" } }, Material.FIRE));
/*  136 */     items.add(new ItemInfo("Monster Spawner", new String[][] { { "spawn" } }, Material.MOB_SPAWNER));
/*  137 */     items.add(new ItemInfo("Oak Wood Stairs", new String[][] { { "stair", "wood" }, { "oak", "stair" } }, Material.WOOD_STAIRS));
/*  138 */     items.add(new ItemInfo("Jungle Wood Stairs", new String[][] { { "jungle", "stair" }, { "jung", "stair", "woo" } }, Material.JUNGLE_WOOD_STAIRS));
/*  139 */     items.add(new ItemInfo("Spruce Wood Stairs", new String[][] { { "spruce", "stai" }, { "spru", "stair", "woo" } }, Material.SPRUCE_WOOD_STAIRS));
/*  140 */     items.add(new ItemInfo("Birch Wood Stairs", new String[][] { { "birch", "stair" }, { "birc", "stai", "woo" } }, Material.BIRCH_WOOD_STAIRS));
/*  141 */     items.add(new ItemInfo("Chest", new String[][] { { "chest" } }, Material.CHEST));
/*  142 */     items.add(new ItemInfo("Diamond Ore", new String[][] { { "ore", "diam" } }, Material.DIAMOND_ORE));
/*  143 */     items.add(new ItemInfo("Diamond Block", new String[][] { { "diam", "bl" } }, Material.DIAMOND_BLOCK));
/*  144 */     items.add(new ItemInfo("Crafting Table", new String[][] { { "benc" }, { "squa" }, { "craft" } }, Material.WORKBENCH));
/*  145 */     items.add(new ItemInfo("Farmland", new String[][] { { "soil" }, { "farm" } }, Material.SOIL));
/*  146 */     items.add(new ItemInfo("Furnace", new String[][] { { "furna" }, { "cooke" } }, Material.FURNACE));
/*  147 */     items.add(new ItemInfo("Ladder", new String[][] { { "ladd" } }, Material.LADDER));
/*  148 */     items.add(new ItemInfo("Rails", new String[][] { { "rail" }, { "trac" } }, Material.RAILS));
/*  149 */     items.add(new ItemInfo("Cobblestone Stairs", new String[][] { { "stair", "cob", "sto" }, { "stair", "cob" } }, Material.COBBLESTONE_STAIRS));
/*  150 */     items.add(new ItemInfo("Lever", new String[][] { { "lever" }, { "switc" } }, Material.LEVER));
/*  151 */     items.add(new ItemInfo("Stone Pressure Plate", new String[][] { { "pres", "plat", "ston" } }, Material.STONE_PLATE));
/*  152 */     items.add(new ItemInfo("Wooden Pressure Plate", new String[][] { { "pres", "plat", "wood" } }, Material.WOOD_PLATE));
/*  153 */     items.add(new ItemInfo("Redstone Ore", new String[][] { { "redst", "ore" } }, Material.REDSTONE_ORE));
/*  154 */     items.add(new ItemInfo("Redstone Torch", new String[][] { { "torc", "red" }, { "torc", "rs" } }, Material.REDSTONE_TORCH_ON));
/*  155 */     items.add(new ItemInfo("Stone Button", new String[][] { { "stone", "button" }, { "button" } }, Material.STONE_BUTTON));
/*  156 */     items.add(new ItemInfo("Snow", new String[][] { { "tile", "snow" }, { "snow", "slab" }, { "snow" } }, Material.SNOW));
/*  157 */     items.add(new ItemInfo("Ice", new String[][] { { "ice" } }, Material.ICE));
/*  158 */     items.add(new ItemInfo("Snow Block", new String[][] { { "blo", "snow" } }, Material.SNOW_BLOCK));
/*  159 */     items.add(new ItemInfo("Cactus", new String[][] { { "cact" } }, Material.CACTUS));
/*  160 */     items.add(new ItemInfo("Clay Block", new String[][] { { "clay", "blo" } }, Material.CLAY));
/*  161 */     items.add(new ItemInfo("Jukebox", new String[][] { { "jukeb" } }, Material.JUKEBOX));
/*  162 */     items.add(new ItemInfo("Oak Fence", new String[][] { { "oak", "fence" }, { "fence" } }, Material.FENCE));
/*  163 */     items.add(new ItemInfo("Pumpkin", new String[][] { { "pump" } }, Material.PUMPKIN));
/*  164 */     items.add(new ItemInfo("Netherrack", new String[][] { { "netherr" }, { "netherst" }, { "hellst" } }, Material.NETHERRACK));
/*  165 */     items.add(new ItemInfo("Soul Sand", new String[][] { { "soul", "sand" }, { "soul" }, { "slowsa" }, { "nether", "mud" }, { "slow", "sand" }, { "quick", "sand" }, { "mud" } }, Material.SOUL_SAND));
/*  166 */     items.add(new ItemInfo("Glowstone", new String[][] { { "glow", "stone" }, { "light", "stone" } }, Material.GLOWSTONE));
/*  167 */     items.add(new ItemInfo("Portal", new String[][] { { "port" } }, Material.PORTAL));
/*  168 */     items.add(new ItemInfo("Jack-O-Lantern", new String[][] { { "jack" }, { "lante" } }, Material.JACK_O_LANTERN));
/*  169 */     items.add(new ItemInfo("Wooden Trapdoor", new String[][] { { "trap", "doo" }, { "woo", "hatc" }, { "woo", "trap", "door" } }, Material.TRAP_DOOR));
/*  170 */     items.add(new ItemInfo("Stone Monster Egg", new String[][] { { "mons", "egg" }, { "sto", "mons", "egg" }, { "hid", "silver" } }, Material.MONSTER_EGGS));
/*  171 */     items.add(new ItemInfo("Stone Brick Monster Egg", new String[][] { { "sto", "bri", "mons", "egg" }, { "hid", "silver", "sto", "bri" } }, Material.MONSTER_EGGS, (short)2));
/*  172 */     items.add(new ItemInfo("Mossy Stone Brick Monster Egg", new String[][] { { "moss", "sto", "bri", "mons", "egg" }, { "hid", "silver", "mos", "sto", "bri" } }, Material.MONSTER_EGGS, (short)3));
/*  173 */     items.add(new ItemInfo("Huge Brown Mushroom", new String[][] { { "bro", "huge", "mush" } }, Material.HUGE_MUSHROOM_1));
/*  174 */     items.add(new ItemInfo("Huge Red Mushroom", new String[][] { { "red", "huge", "mush" } }, Material.HUGE_MUSHROOM_2));
/*  175 */     items.add(new ItemInfo("Stone Brick", new String[][] { { "sto", "bric" }, { "smoo", "bric" } }, Material.SMOOTH_BRICK, (short)0));
/*  176 */     items.add(new ItemInfo("Iron Fence", new String[][] { { "bars", "iron" }, { "fence", "iron" } }, Material.IRON_FENCE));
/*  177 */     items.add(new ItemInfo("Glass Pane", new String[][] { { "thin", "gla" }, { "pane" }, { "gla", "pane" } }, Material.THIN_GLASS));
/*  178 */     items.add(new ItemInfo("Melon Block", new String[][] { { "melon" } }, Material.MELON_BLOCK));
/*  179 */     items.add(new ItemInfo("Mossy Stone Brick", new String[][] { { "moss", "sto", "bri" }, { "moss", "smoo", "bri" }, { "moss", "smoo" }, { "moss", "sto" } }, Material.SMOOTH_BRICK, (short)1));
/*  180 */     items.add(new ItemInfo("Cracked Stone Brick", new String[][] { { "cra", "sto", "bri" }, { "cra", "sto" }, { "cra", "smoo", "bri" }, { "cra", "smoo" } }, Material.SMOOTH_BRICK, (short)2));
/*  181 */     items.add(new ItemInfo("Chiseled Stone Brick", new String[][] { { "chis", "sto", "bri" }, { "chis", "sto" }, { "chis", "smoo", "bri" } }, Material.SMOOTH_BRICK, (short)3));
/*  182 */     items.add(new ItemInfo("Brick Stairs", new String[][] { { "stair", "bri" } }, Material.BRICK_STAIRS));
/*  183 */     items.add(new ItemInfo("Fence Gate", new String[][] { { "gate", "fen" }, { "gate" } }, Material.FENCE_GATE));
/*  184 */     items.add(new ItemInfo("Vines", new String[][] { { "vine" }, { "ivy" } }, Material.VINE));
/*  185 */     items.add(new ItemInfo("Stone Brick Stairs", new String[][] { { "stair", "sto", "bri" }, { "stair", "sto" }, { "stair", "smoo", "bri" }, { "stair", "smoo" } }, Material.SMOOTH_STAIRS));
/*  186 */     items.add(new ItemInfo("Iron Shovel", new String[][] { { "shov", "ir" }, { "spad", "ir" } }, Material.IRON_SPADE));
/*  187 */     items.add(new ItemInfo("Iron Pickaxe", new String[][] { { "pick", "ir" } }, Material.IRON_PICKAXE));
/*  188 */     items.add(new ItemInfo("Iron Axe", new String[][] { { "axe", "ir" } }, Material.IRON_AXE));
/*  189 */     items.add(new ItemInfo("Flint and Steel", new String[][] { { "steel" }, { "lighter" }, { "flin", "ste" } }, Material.FLINT_AND_STEEL));
/*  190 */     items.add(new ItemInfo("Apple", new String[][] { { "appl" } }, Material.APPLE));
/*  191 */     items.add(new ItemInfo("Bow", new String[][] { { "bow" } }, Material.BOW));
/*  192 */     items.add(new ItemInfo("Arrow", new String[][] { { "arro" } }, Material.ARROW));
/*  193 */     items.add(new ItemInfo("Coal", new String[][] { { "coal" } }, Material.COAL));
/*  194 */     items.add(new ItemInfo("Charcoal", new String[][] { { "char", "coal" }, { "char" } }, Material.COAL, (short)1));
/*  195 */     items.add(new ItemInfo("Diamond", new String[][] { { "diamo" } }, Material.DIAMOND));
/*  196 */     items.add(new ItemInfo("Iron Ingot", new String[][] { { "ingo", "ir" }, { "iron" } }, Material.IRON_INGOT));
/*  197 */     items.add(new ItemInfo("Gold Ingot", new String[][] { { "ingo", "go" }, { "gold" } }, Material.GOLD_INGOT));
/*  198 */     items.add(new ItemInfo("Iron Sword", new String[][] { { "swor", "ir" } }, Material.IRON_SWORD));
/*  199 */     items.add(new ItemInfo("Wooden Sword", new String[][] { { "swor", "woo" } }, Material.WOOD_SWORD));
/*  200 */     items.add(new ItemInfo("Wooden Shovel", new String[][] { { "shov", "wo" }, { "spad", "wo" } }, Material.WOOD_SPADE));
/*  201 */     items.add(new ItemInfo("Wooden Pickaxe", new String[][] { { "pick", "woo" } }, Material.WOOD_PICKAXE));
/*  202 */     items.add(new ItemInfo("Wooden Axe", new String[][] { { "axe", "woo" } }, Material.WOOD_AXE));
/*  203 */     items.add(new ItemInfo("Stone Sword", new String[][] { { "swor", "sto" } }, Material.STONE_SWORD));
/*  204 */     items.add(new ItemInfo("Stone Shovel", new String[][] { { "shov", "sto" }, { "spad", "sto" } }, Material.STONE_SPADE));
/*  205 */     items.add(new ItemInfo("Stone Pickaxe", new String[][] { { "pick", "sto" } }, Material.STONE_PICKAXE));
/*  206 */     items.add(new ItemInfo("Stone Axe", new String[][] { { "axe", "sto" } }, Material.STONE_AXE));
/*  207 */     items.add(new ItemInfo("Diamond Sword", new String[][] { { "swor", "dia" } }, Material.DIAMOND_SWORD));
/*  208 */     items.add(new ItemInfo("Diamond Shovel", new String[][] { { "shov", "dia" }, { "spad", "dia" } }, Material.DIAMOND_SPADE));
/*  209 */     items.add(new ItemInfo("Diamond Pickaxe", new String[][] { { "pick", "dia" } }, Material.DIAMOND_PICKAXE));
/*  210 */     items.add(new ItemInfo("Diamond Axe", new String[][] { { "axe", "dia" } }, Material.DIAMOND_AXE));
/*  211 */     items.add(new ItemInfo("Stick", new String[][] { { "stic" } }, Material.STICK));
/*  212 */     items.add(new ItemInfo("Bowl", new String[][] { { "bo", "wl" } }, Material.BOWL));
/*  213 */     items.add(new ItemInfo("Mushroom Soup", new String[][] { { "soup" } }, Material.MUSHROOM_SOUP));
/*  214 */     items.add(new ItemInfo("Gold Sword", new String[][] { { "swor", "gol" } }, Material.GOLD_SWORD));
/*  215 */     items.add(new ItemInfo("Gold Shovel", new String[][] { { "shov", "gol" }, { "spad", "gol" } }, Material.GOLD_SPADE));
/*  216 */     items.add(new ItemInfo("Gold Pickaxe", new String[][] { { "pick", "gol" } }, Material.GOLD_PICKAXE));
/*  217 */     items.add(new ItemInfo("Gold Axe", new String[][] { { "axe", "gol" } }, Material.GOLD_AXE));
/*  218 */     items.add(new ItemInfo("String", new String[][] { { "stri" } }, Material.STRING));
/*  219 */     items.add(new ItemInfo("Feather", new String[][] { { "feat" } }, Material.FEATHER));
/*  220 */     items.add(new ItemInfo("Gunpowder", new String[][] { { "gun" }, { "sulph" } }, Material.SULPHUR));
/*  221 */     items.add(new ItemInfo("Wooden Hoe", new String[][] { { "hoe", "wo" } }, Material.WOOD_HOE));
/*  222 */     items.add(new ItemInfo("Stone Hoe", new String[][] { { "hoe", "sto" } }, Material.STONE_HOE));
/*  223 */     items.add(new ItemInfo("Iron Hoe", new String[][] { { "hoe", "iro" } }, Material.IRON_HOE));
/*  224 */     items.add(new ItemInfo("Diamond Hoe", new String[][] { { "hoe", "dia" } }, Material.DIAMOND_HOE));
/*  225 */     items.add(new ItemInfo("Gold Hoe", new String[][] { { "hoe", "go" } }, Material.GOLD_HOE));
/*  226 */     items.add(new ItemInfo("Seeds", new String[][] { { "seed" } }, Material.SEEDS));
/*  227 */     items.add(new ItemInfo("Wheat", new String[][] { { "whea" } }, Material.WHEAT));
/*  228 */     items.add(new ItemInfo("Bread", new String[][] { { "brea" } }, Material.BREAD));
/*  229 */     items.add(new ItemInfo("Leather Cap", new String[][] { { "cap", "lea" }, { "hat", "lea" }, { "helm", "lea" } }, Material.LEATHER_HELMET));
/*  230 */     items.add(new ItemInfo("Leather Tunic", new String[][] { { "tun", "lea" }, { "ches", "lea" } }, Material.LEATHER_CHESTPLATE));
/*  231 */     items.add(new ItemInfo("Leather Pants", new String[][] { { "pan", "lea" }, { "trou", "lea" }, { "leg", "lea" } }, Material.LEATHER_LEGGINGS));
/*  232 */     items.add(new ItemInfo("Leather Boots", new String[][] { { "boo", "lea" } }, Material.LEATHER_BOOTS));
/*  233 */     items.add(new ItemInfo("Chainmail Helmet", new String[][] { { "cap", "cha" }, { "hat", "cha" }, { "helm", "cha" } }, Material.CHAINMAIL_HELMET));
/*  234 */     items.add(new ItemInfo("Chainmail Chestplate", new String[][] { { "tun", "cha" }, { "ches", "cha" } }, Material.CHAINMAIL_CHESTPLATE));
/*  235 */     items.add(new ItemInfo("Chainmail Leggings", new String[][] { { "pan", "cha" }, { "trou", "cha" }, { "leg", "cha" } }, Material.CHAINMAIL_LEGGINGS));
/*  236 */     items.add(new ItemInfo("Chainmail Boots", new String[][] { { "boo", "cha" } }, Material.CHAINMAIL_BOOTS));
/*  237 */     items.add(new ItemInfo("Iron Helmet", new String[][] { { "cap", "ir" }, { "hat", "ir" }, { "helm", "ir" } }, Material.IRON_HELMET));
/*  238 */     items.add(new ItemInfo("Iron Chestplate", new String[][] { { "tun", "ir" }, { "ches", "ir" } }, Material.IRON_CHESTPLATE));
/*  239 */     items.add(new ItemInfo("Iron Leggings", new String[][] { { "pan", "ir" }, { "trou", "ir" }, { "leg", "ir" } }, Material.IRON_LEGGINGS));
/*  240 */     items.add(new ItemInfo("Iron Boots", new String[][] { { "boo", "ir" } }, Material.IRON_BOOTS));
/*  241 */     items.add(new ItemInfo("Diamond Helmet", new String[][] { { "cap", "dia" }, { "hat", "dia" }, { "helm", "dia" } }, Material.DIAMOND_HELMET));
/*  242 */     items.add(new ItemInfo("Diamond Chestplate", new String[][] { { "tun", "dia" }, { "ches", "dia" } }, Material.DIAMOND_CHESTPLATE));
/*  243 */     items.add(new ItemInfo("Diamond Leggings", new String[][] { { "pan", "dia" }, { "trou", "dia" }, { "leg", "dia" } }, Material.DIAMOND_LEGGINGS));
/*  244 */     items.add(new ItemInfo("Diamond Boots", new String[][] { { "boo", "dia" } }, Material.DIAMOND_BOOTS));
/*  245 */     items.add(new ItemInfo("Gold Helmet", new String[][] { { "cap", "go" }, { "hat", "go" }, { "helm", "go" } }, Material.GOLD_HELMET));
/*  246 */     items.add(new ItemInfo("Gold Chestplate", new String[][] { { "tun", "go" }, { "ches", "go" } }, Material.GOLD_CHESTPLATE));
/*  247 */     items.add(new ItemInfo("Gold Leggings", new String[][] { { "pan", "go" }, { "trou", "go" }, { "leg", "go" } }, Material.GOLD_LEGGINGS));
/*  248 */     items.add(new ItemInfo("Gold Boots", new String[][] { { "boo", "go" } }, Material.GOLD_BOOTS));
/*  249 */     items.add(new ItemInfo("Flint", new String[][] { { "flin" } }, Material.FLINT));
/*  250 */     items.add(new ItemInfo("Raw Porkchop", new String[][] { { "pork" }, { "ham" } }, Material.PORK));
/*  251 */     items.add(new ItemInfo("Cooked Porkchop", new String[][] { { "pork", "cook" }, { "baco" } }, Material.GRILLED_PORK));
/*  252 */     items.add(new ItemInfo("Paintings", new String[][] { { "paint" } }, Material.PAINTING));
/*  253 */     items.add(new ItemInfo("Golden Apple", new String[][] { { "appl", "go" } }, Material.GOLDEN_APPLE));
/*  254 */     items.add(new ItemInfo("Enchanted Golden Apple", new String[][] { { "appl", "go", "ench" } }, Material.GOLDEN_APPLE, (short)1));
/*  255 */     items.add(new ItemInfo("Sign", new String[][] { { "sign" } }, Material.SIGN));
/*  256 */     items.add(new ItemInfo("Wooden Door", new String[][] { { "door", "wood" }, { "door" } }, Material.WOOD_DOOR));
/*  257 */     items.add(new ItemInfo("Bucket", new String[][] { { "buck" }, { "bukk" } }, Material.BUCKET));
/*  258 */     items.add(new ItemInfo("Water Bucket", new String[][] { { "water", "buck" } }, Material.WATER_BUCKET));
/*  259 */     items.add(new ItemInfo("Lava Bucket", new String[][] { { "lava", "buck" } }, Material.LAVA_BUCKET));
/*  260 */     items.add(new ItemInfo("Minecart", new String[][] { { "cart" } }, Material.MINECART));
/*  261 */     items.add(new ItemInfo("Saddle", new String[][] { { "sad" }, { "pig" } }, Material.SADDLE));
/*  262 */     items.add(new ItemInfo("Iron Door", new String[][] { { "door", "iron" } }, Material.IRON_DOOR));
/*  263 */     items.add(new ItemInfo("Redstone Dust", new String[][] { { "red", "ston", "dust" }, { "dust", "rs" }, { "dust", "red" }, { "reds" } }, Material.REDSTONE));
/*  264 */     items.add(new ItemInfo("Snowball", new String[][] { { "snow", "ball" } }, Material.SNOW_BALL));
/*  265 */     items.add(new ItemInfo("Boat", new String[][] { { "boat" } }, Material.BOAT));
/*  266 */     items.add(new ItemInfo("Leather", new String[][] { { "lea" }, { "hide" } }, Material.LEATHER));
/*  267 */     items.add(new ItemInfo("Milk Bucket", new String[][] { { "buck", "mil" }, { "milk" } }, Material.MILK_BUCKET));
/*  268 */     items.add(new ItemInfo("Clay Brick", new String[][] { { "bric", "cl" }, { "sin", "bric" } }, Material.CLAY_BRICK));
/*  269 */     items.add(new ItemInfo("Clay", new String[][] { { "clay" } }, Material.CLAY_BALL));
/*  270 */     items.add(new ItemInfo("Sugar Cane", new String[][] { { "reed" }, { "cane" } }, Material.SUGAR_CANE));
/*  271 */     items.add(new ItemInfo("Paper", new String[][] { { "pape" } }, Material.PAPER));
/*  272 */     items.add(new ItemInfo("Book", new String[][] { { "book" } }, Material.BOOK));
/*  273 */     items.add(new ItemInfo("Slimeball", new String[][] { { "slime" } }, Material.SLIME_BALL));
/*  274 */     items.add(new ItemInfo("Storage Minecart", new String[][] { { "cart", "sto" }, { "cart", "che" }, { "cargo" } }, Material.STORAGE_MINECART));
/*  275 */     items.add(new ItemInfo("Powered Minecart", new String[][] { { "cart", "pow" }, { "engine" } }, Material.POWERED_MINECART));
/*  276 */     items.add(new ItemInfo("Egg", new String[][] { { "egg" } }, Material.EGG));
/*  277 */     items.add(new ItemInfo("Compass", new String[][] { { "comp" } }, Material.COMPASS));
/*  278 */     items.add(new ItemInfo("Fishing Rod", new String[][] { { "rod" }, { "rod", "fish" }, { "pole", "fish" } }, Material.FISHING_ROD));
/*  279 */     items.add(new ItemInfo("Clock", new String[][] { { "cloc" }, { "watc" } }, Material.WATCH));
/*  280 */     items.add(new ItemInfo("Glowstone Dust", new String[][] { { "glow", "sto", "dus" }, { "glow", "dus" }, { "ligh", "dust" } }, Material.GLOWSTONE_DUST));
/*  281 */     items.add(new ItemInfo("Raw Fish", new String[][] { { "fish" }, { "fish", "raw" } }, Material.RAW_FISH));
/*  282 */     items.add(new ItemInfo("Cooked Fish", new String[][] { { "fish", "coo" }, { "kipper" } }, Material.COOKED_FISH));
/*  283 */     items.add(new ItemInfo("Ink Sac", new String[][] { { "ink" }, { "dye", "bla" } }, Material.INK_SACK));
/*  284 */     items.add(new ItemInfo("Red Dye", new String[][] { { "dye", "red" }, { "pain", "red" }, { "pet", "ros" }, { "pet", "red" } }, Material.INK_SACK, (short)1));
/*  285 */     items.add(new ItemInfo("Cactus Green", new String[][] { { "cact", "gree" }, { "dye", "gree" }, { "pain", "gree" } }, Material.INK_SACK, (short)2));
/*  286 */     items.add(new ItemInfo("Cocoa Beans", new String[][] { { "bean" }, { "choco" }, { "cocoa" }, { "dye", "bro" }, { "pain", "bro" } }, Material.INK_SACK, (short)3));
/*  287 */     items.add(new ItemInfo("Lapis Lazuli", new String[][] { { "lapi", "lazu" }, { "dye", "lapi" }, { "dye", "blu" }, { "pain", "blu" } }, Material.INK_SACK, (short)4));
/*  288 */     items.add(new ItemInfo("Purple Dye", new String[][] { { "dye", "pur" }, { "pain", "pur" } }, Material.INK_SACK, (short)5));
/*  289 */     items.add(new ItemInfo("Cyan Dye", new String[][] { { "dye", "cya" }, { "pain", "cya" } }, Material.INK_SACK, (short)6));
/*  290 */     items.add(new ItemInfo("Light Gray Dye", new String[][] { { "dye", "lig", "gra" }, { "dye", "lig", "grey" }, { "pain", "lig", "grey" }, { "pain", "lig", "grey" } }, Material.INK_SACK, (short)7));
/*  291 */     items.add(new ItemInfo("Gray Dye", new String[][] { { "dye", "gra" }, { "dye", "grey" }, { "pain", "grey" }, { "pain", "grey" } }, Material.INK_SACK, (short)8));
/*  292 */     items.add(new ItemInfo("Pink Dye", new String[][] { { "dye", "pin" }, { "pain", "pin" } }, Material.INK_SACK, (short)9));
/*  293 */     items.add(new ItemInfo("Lime Dye", new String[][] { { "dye", "lim" }, { "pain", "lim" }, { "dye", "lig", "gree" }, { "pain", "lig", "gree" } }, Material.INK_SACK, (short)10));
/*  294 */     items.add(new ItemInfo("Dandelion Yellow", new String[][] { { "dye", "yel" }, { "yel", "dan" }, { "pet", "dan" }, { "pet", "yel" } }, Material.INK_SACK, (short)11));
/*  295 */     items.add(new ItemInfo("Light Blue Dye", new String[][] { { "dye", "lig", "blu" }, { "pain", "lig", "blu" } }, Material.INK_SACK, (short)12));
/*  296 */     items.add(new ItemInfo("Magenta Dye", new String[][] { { "dye", "mag" }, { "pain", "mag" } }, Material.INK_SACK, (short)13));
/*  297 */     items.add(new ItemInfo("Orange Dye", new String[][] { { "dye", "ora" }, { "pain", "ora" } }, Material.INK_SACK, (short)14));
/*  298 */     items.add(new ItemInfo("Bone Meal", new String[][] { { "bonem" }, { "bone", "me" }, { "dye", "whi" }, { "pain", "whi" } }, Material.INK_SACK, (short)15));
/*  299 */     items.add(new ItemInfo("Bone", new String[][] { { "bone" }, { "femur" } }, Material.BONE));
/*  300 */     items.add(new ItemInfo("Sugar", new String[][] { { "suga" } }, Material.SUGAR));
/*  301 */     items.add(new ItemInfo("Cake", new String[][] { { "cake" } }, Material.CAKE));
/*  302 */     items.add(new ItemInfo("Melon Slice", new String[][] { { "sli", "melo" } }, Material.MELON));
/*  303 */     items.add(new ItemInfo("Pumpkin Seed", new String[][] { { "seed", "pump" } }, Material.PUMPKIN_SEEDS));
/*  304 */     items.add(new ItemInfo("Melon Seed", new String[][] { { "seed", "melo" } }, Material.MELON_SEEDS));
/*  305 */     items.add(new ItemInfo("Raw Beef", new String[][] { { "beef", "raw" } }, Material.RAW_BEEF));
/*  306 */     items.add(new ItemInfo("Steak", new String[][] { { "steak" }, { "beef", "coo" } }, Material.COOKED_BEEF));
/*  307 */     items.add(new ItemInfo("Raw Chicken", new String[][] { { "chi", "raw" } }, Material.RAW_CHICKEN));
/*  308 */     items.add(new ItemInfo("Cooked Chicken", new String[][] { { "chi", "coo" } }, Material.COOKED_CHICKEN));
/*  309 */     items.add(new ItemInfo("Rotten Flesh", new String[][] { { "flesh" }, { "rott" } }, Material.ROTTEN_FLESH));
/*  310 */     items.add(new ItemInfo("Bed", new String[][] { { "bed" } }, Material.BED));
/*  311 */     items.add(new ItemInfo("Redstone Repeater", new String[][] { { "repe", "reds" }, { "diod" }, { "repeat" } }, Material.DIODE));
/*  312 */     items.add(new ItemInfo("Cookie", new String[][] { { "cooki" } }, Material.COOKIE));
/*  313 */     items.add(new ItemInfo("Map", new String[][] { { "map" } }, Material.MAP));
/*  314 */     items.add(new ItemInfo("Empty Map", new String[][] { { "empt", "ma" } }, Material.EMPTY_MAP));
/*  315 */     items.add(new ItemInfo("Shears", new String[][] { { "shea" } }, Material.SHEARS));
/*  316 */     items.add(new ItemInfo("Ender Pearl", new String[][] { { "end", "pear" }, { "pearl" } }, Material.ENDER_PEARL));
/*  317 */     items.add(new ItemInfo("Mycelium", new String[][] { { "myc" } }, Material.MYCEL));
/*  318 */     items.add(new ItemInfo("Lily Pad", new String[][] { { "lil", "pad" }, { "lil", "wat" } }, Material.WATER_LILY));
/*  319 */     items.add(new ItemInfo("Cauldron Block", new String[][] { { "bloc", "cauld" } }, Material.CAULDRON));
/*  320 */     items.add(new ItemInfo("Cauldron", new String[][] { { "cauld" } }, Material.CAULDRON_ITEM));
/*  321 */     items.add(new ItemInfo("Enchantment Table", new String[][] { { "ench", "tab" } }, Material.ENCHANTMENT_TABLE));
/*  322 */     items.add(new ItemInfo("Brewing Stand Block", new String[][] { { "bloc", "brew", "stan" }, { "alch", "bloc" } }, Material.BREWING_STAND));
/*  323 */     items.add(new ItemInfo("Brewing Stand", new String[][] { { "brew", "stan" }, { "alch", "stand" }, { "alch", "tab" } }, Material.BREWING_STAND_ITEM));
/*  324 */     items.add(new ItemInfo("Nether Brick", new String[][] { { "neth", "bric" } }, Material.NETHER_BRICK));
/*  325 */     items.add(new ItemInfo("Nether Brick Stairs", new String[][] { { "neth", "stair" }, { "neth", "stai", "bric" } }, Material.NETHER_BRICK_STAIRS));
/*  326 */     items.add(new ItemInfo("Nether Brick Fence", new String[][] { { "neth", "fence" }, { "neth", "fence", "bric" } }, Material.NETHER_FENCE));
/*  327 */     items.add(new ItemInfo("Netherwarts", new String[][] { { "wart" }, { "neth", "war" } }, Material.NETHER_WARTS));
/*  328 */     items.add(new ItemInfo("Netherstalk", new String[][] { { "neth", "stalk" } }, Material.NETHER_STALK));
/*  329 */     items.add(new ItemInfo("End Portal", new String[][] { { "end", "port" } }, Material.ENDER_PORTAL));
/*  330 */     items.add(new ItemInfo("End Portal Frame", new String[][] { { "fram", "end", "port" } }, Material.ENDER_PORTAL_FRAME));
/*  331 */     items.add(new ItemInfo("End Stone", new String[][] { { "end", "ston" } }, Material.ENDER_STONE));
/*  332 */     items.add(new ItemInfo("Dragon Egg", new String[][] { { "drag", "egg" } }, Material.DRAGON_EGG));
/*  333 */     items.add(new ItemInfo("Blaze Rod", new String[][] { { "rod", "blaz" } }, Material.BLAZE_ROD));
/*  334 */     items.add(new ItemInfo("Ghast Tear", new String[][] { { "ghas", "tear" } }, Material.GHAST_TEAR));
/*  335 */     items.add(new ItemInfo("Gold Nugget", new String[][] { { "nugg", "gold" } }, Material.GOLD_NUGGET));
/*  336 */     items.add(new ItemInfo("Glass Bottle", new String[][] { { "bottl" }, { "glas", "bott" }, { "empt", "bott" } }, Material.GLASS_BOTTLE));
/*  337 */     items.add(new ItemInfo("Potion", new String[][] { { "potio" } }, Material.POTION));
/*  338 */     items.add(new ItemInfo("Water Bottle", new String[][] { { "wat", "bot" } }, Material.POTION, (short)0));
/*  339 */     items.add(new ItemInfo("Awkward Potion", new String[][] { { "poti", "awk" } }, Material.POTION, (short)16));
/*  340 */     items.add(new ItemInfo("Thick Potion", new String[][] { { "poti", "thic" } }, Material.POTION, (short)32));
/*  341 */     items.add(new ItemInfo("Mundane Potion (Extended)", new String[][] { { "poti", "mund", "ext" } }, Material.POTION, (short)64));
/*  342 */     items.add(new ItemInfo("Mundane Potion", new String[][] { { "poti", "mund" } }, Material.POTION, (short)8192));
/*  343 */     items.add(new ItemInfo("Potion of Regeneration", new String[][] { { "poti", "rege" } }, Material.POTION, (short)8193));
/*  344 */     items.add(new ItemInfo("Potion of Regeneration (Extended)", new String[][] { { "poti", "rege", "ext" } }, Material.POTION, (short)8257));
/*  345 */     items.add(new ItemInfo("Potion of Regeneration II", new String[][] { { "poti", "rege", "2" }, { "poti", "rege", "ii" } }, Material.POTION, (short)8225));
/*  346 */     items.add(new ItemInfo("Potion of Swiftness", new String[][] { { "poti", "swif" }, { "poti", "speed" } }, Material.POTION, (short)8194));
/*  347 */     items.add(new ItemInfo("Potion of Swiftness (Extended)", new String[][] { { "poti", "swif", "ext" }, { "poti", "speed", "ext" } }, Material.POTION, (short)8258));
/*  348 */     items.add(new ItemInfo("Potion of Swiftness II", new String[][] { { "poti", "swif", "2" }, { "poti", "swif", "ii" }, { "poti", "speed", "2" }, { "poti", "speed", "ii" } }, Material.POTION, (short)8226));
/*  349 */     items.add(new ItemInfo("Potion of Fire Resistance", new String[][] { { "poti", "fire" } }, Material.POTION, (short)8195));
/*  350 */     items.add(new ItemInfo("Potion of Fire Resistance (Extended)", new String[][] { { "poti", "fire", "ext" } }, Material.POTION, (short)8259));
/*  351 */     items.add(new ItemInfo("Potion of Fire Resistance (Reverted)", new String[][] { { "poti", "fire", "rev" } }, Material.POTION, (short)8227));
/*  352 */     items.add(new ItemInfo("Potion of Healing", new String[][] { { "poti", "heal" } }, Material.POTION, (short)8197));
/*  353 */     items.add(new ItemInfo("Potion of Healing (Reverted)", new String[][] { { "poti", "heal", "rev" } }, Material.POTION, (short)8261));
/*  354 */     items.add(new ItemInfo("Potion of Healing II", new String[][] { { "poti", "heal", "2" }, { "poti", "heal", "ii" } }, Material.POTION, (short)8229));
/*  355 */     items.add(new ItemInfo("Potion of Strength", new String[][] { { "poti", "str" } }, Material.POTION, (short)8201));
/*  356 */     items.add(new ItemInfo("Potion of Strength (Extended)", new String[][] { { "poti", "str", "ext" } }, Material.POTION, (short)8265));
/*  357 */     items.add(new ItemInfo("Potion of Strength II", new String[][] { { "poti", "str", "2" }, { "poti", "str", "ii" } }, Material.POTION, (short)8233));
/*  358 */     items.add(new ItemInfo("Potion of Poison", new String[][] { { "poti", "pois" } }, Material.POTION, (short)8196));
/*  359 */     items.add(new ItemInfo("Potion of Poison (Extended)", new String[][] { { "poti", "pois", "ext" } }, Material.POTION, (short)8260));
/*  360 */     items.add(new ItemInfo("Potion of Poison II", new String[][] { { "poti", "pois", "2" }, { "poti", "pois", "ii" } }, Material.POTION, (short)8228));
/*  361 */     items.add(new ItemInfo("Potion of Weakness", new String[][] { { "poti", "weak" } }, Material.POTION, (short)8200));
/*  362 */     items.add(new ItemInfo("Potion of Weakness (Extended)", new String[][] { { "poti", "weak", "ext" } }, Material.POTION, (short)8264));
/*  363 */     items.add(new ItemInfo("Potion of Weakness (Reverted)", new String[][] { { "poti", "weak", "rev" } }, Material.POTION, (short)8232));
/*  364 */     items.add(new ItemInfo("Potion of Slowness", new String[][] { { "poti", "slow" } }, Material.POTION, (short)8202));
/*  365 */     items.add(new ItemInfo("Potion of Slowness (Extended)", new String[][] { { "poti", "slow", "ext" } }, Material.POTION, (short)8266));
/*  366 */     items.add(new ItemInfo("Potion of Slowness (Reverted)", new String[][] { { "poti", "slow", "rev" } }, Material.POTION, (short)8234));
/*  367 */     items.add(new ItemInfo("Potion of Harming", new String[][] { { "poti", "harm" } }, Material.POTION, (short)8204));
/*  368 */     items.add(new ItemInfo("Potion of Harming (Reverted)", new String[][] { { "poti", "harm", "rev" } }, Material.POTION, (short)8268));
/*  369 */     items.add(new ItemInfo("Potion of Harming II", new String[][] { { "poti", "harm", "2" }, { "poti", "harm", "ii" } }, Material.POTION, (short)8236));
/*  370 */     items.add(new ItemInfo("Splash Mundane Potion", new String[][] { { "poti", "mund", "spl" } }, Material.POTION, (short)16384));
/*  371 */     items.add(new ItemInfo("Splash Potion of Regeneration", new String[][] { { "poti", "rege", "spl" } }, Material.POTION, (short)16385));
/*  372 */     items.add(new ItemInfo("Splash Potion of Regeneration (Extended)", new String[][] { { "poti", "rege", "spl", "ext" } }, Material.POTION, (short)16449));
/*  373 */     items.add(new ItemInfo("Splash Potion of Regeneration II", new String[][] { { "poti", "rege", "spl", "2" }, { "poti", "rege", "spl", "ii" } }, Material.POTION, (short)16417));
/*  374 */     items.add(new ItemInfo("Splash Potion of Swiftness", new String[][] { { "poti", "swif", "spl" }, { "poti", "speed", "spl" } }, Material.POTION, (short)16386));
/*  375 */     items.add(new ItemInfo("Splash Potion of Swiftness (Extended)", new String[][] { { "poti", "swif", "spl", "ext" }, { "poti", "speed", "spl", "ext" } }, Material.POTION, (short)16450));
/*  376 */     items.add(new ItemInfo("Splash Potion of Swiftness II", new String[][] { { "poti", "swif", "spl", "2" }, { "poti", "swif", "spl", "ii" }, { "poti", "speed", "spl", "2" }, { "poti", "speed", "spl", "ii" } }, Material.POTION, (short)16418));
/*  377 */     items.add(new ItemInfo("Splash Potion of Fire Resistance", new String[][] { { "poti", "fire", "spl" } }, Material.POTION, (short)16387));
/*  378 */     items.add(new ItemInfo("Splash Potion of Fire Resistance (Extended)", new String[][] { { "poti", "fire", "spl", "ext" } }, Material.POTION, (short)16451));
/*  379 */     items.add(new ItemInfo("Splash Potion of Fire Resistance (Reverted)", new String[][] { { "poti", "fire", "spl", "rev" } }, Material.POTION, (short)16419));
/*  380 */     items.add(new ItemInfo("Splash Potion of Healing", new String[][] { { "poti", "heal", "spl" } }, Material.POTION, (short)16389));
/*  381 */     items.add(new ItemInfo("Splash Potion of Healing (Reverted)", new String[][] { { "poti", "heal", "spl", "rev" } }, Material.POTION, (short)16453));
/*  382 */     items.add(new ItemInfo("Splash Potion of Healing II", new String[][] { { "poti", "heal", "spl", "2" }, { "poti", "heal", "spl", "ii" } }, Material.POTION, (short)16421));
/*  383 */     items.add(new ItemInfo("Splash Potion of Strength", new String[][] { { "poti", "str", "spl" } }, Material.POTION, (short)16393));
/*  384 */     items.add(new ItemInfo("Splash Potion of Strength (Extended)", new String[][] { { "poti", "str", "spl", "ext" } }, Material.POTION, (short)16457));
/*  385 */     items.add(new ItemInfo("Splash Potion of Strength II", new String[][] { { "poti", "str", "spl", "2" }, { "poti", "str", "spl", "ii" } }, Material.POTION, (short)16425));
/*  386 */     items.add(new ItemInfo("Splash Potion of Poison", new String[][] { { "poti", "pois", "spl" } }, Material.POTION, (short)16388));
/*  387 */     items.add(new ItemInfo("Splash Potion of Poison (Extended)", new String[][] { { "poti", "pois", "spl", "ext" } }, Material.POTION, (short)16452));
/*  388 */     items.add(new ItemInfo("Splash Potion of Poison II", new String[][] { { "poti", "pois", "spl", "2" }, { "poti", "pois", "spl", "ii" } }, Material.POTION, (short)16420));
/*  389 */     items.add(new ItemInfo("Splash Potion of Weakness", new String[][] { { "poti", "weak", "spl" } }, Material.POTION, (short)16392));
/*  390 */     items.add(new ItemInfo("Splash Potion of Weakness (Extended)", new String[][] { { "poti", "weak", "spl", "ext" } }, Material.POTION, (short)16456));
/*  391 */     items.add(new ItemInfo("Splash Potion of Weakness (Reverted)", new String[][] { { "poti", "weak", "spl", "rev" } }, Material.POTION, (short)16424));
/*  392 */     items.add(new ItemInfo("Splash Potion of Slowness", new String[][] { { "poti", "slow", "spl" } }, Material.POTION, (short)16394));
/*  393 */     items.add(new ItemInfo("Splash Potion of Slowness (Extended)", new String[][] { { "poti", "slow", "spl", "ext" } }, Material.POTION, (short)16458));
/*  394 */     items.add(new ItemInfo("Splash Potion of Slowness (Reverted)", new String[][] { { "poti", "slow", "spl", "rev" } }, Material.POTION, (short)16426));
/*  395 */     items.add(new ItemInfo("Splash Potion of Harming", new String[][] { { "poti", "harm", "spl" } }, Material.POTION, (short)16396));
/*  396 */     items.add(new ItemInfo("Splash Potion of Harming (Reverted)", new String[][] { { "poti", "harm", "spl", "rev" } }, Material.POTION, (short)16460));
/*  397 */     items.add(new ItemInfo("Splash Potion of Harming II", new String[][] { { "poti", "harm", "spl", "2" }, { "poti", "harm", "spl", "ii" } }, Material.POTION, (short)16428));
/*  398 */     items.add(new ItemInfo("Spider Eye", new String[][] { { "spid", "eye" } }, Material.SPIDER_EYE));
/*  399 */     items.add(new ItemInfo("Fermented Spider Eye", new String[][] { { "ferm", "spid", "eye" } }, Material.FERMENTED_SPIDER_EYE));
/*  400 */     items.add(new ItemInfo("Blaze Powder", new String[][] { { "powd", "blaz" } }, Material.BLAZE_POWDER));
/*  401 */     items.add(new ItemInfo("Magma Cream", new String[][] { { "crea", "magm" } }, Material.MAGMA_CREAM));
/*  402 */     items.add(new ItemInfo("Eye of Ender", new String[][] { { "end", "ey" } }, Material.EYE_OF_ENDER));
/*  403 */     items.add(new ItemInfo("Glistering Melon", new String[][] { { "melo", "glis" } }, Material.SPECKLED_MELON));
/*  404 */     items.add(new ItemInfo("Spawn Egg", new String[][] { { "spaw", "egg" } }, Material.MONSTER_EGG));
/*  405 */     items.add(new ItemInfo("Creeper Spawn Egg", new String[][] { { "creep", "egg" } }, Material.MONSTER_EGG, (short)50));
/*  406 */     items.add(new ItemInfo("Skeleton Spawn Egg", new String[][] { { "skele", "egg" } }, Material.MONSTER_EGG, (short)51));
/*  407 */     items.add(new ItemInfo("Spider Spawn Egg", new String[][] { { "spider", "egg" } }, Material.MONSTER_EGG, (short)52));
/*  408 */     items.add(new ItemInfo("Zombie Spawn Egg", new String[][] { { "zombie", "egg" } }, Material.MONSTER_EGG, (short)54));
/*  409 */     items.add(new ItemInfo("Slime Spawn Egg", new String[][] { { "slime", "egg" } }, Material.MONSTER_EGG, (short)55));
/*  410 */     items.add(new ItemInfo("Ghast Spawn Egg", new String[][] { { "ghast", "egg" } }, Material.MONSTER_EGG, (short)56));
/*  411 */     items.add(new ItemInfo("Zombie Pigman Spawn Egg", new String[][] { { "zomb", "pig", "egg" } }, Material.MONSTER_EGG, (short)57));
/*  412 */     items.add(new ItemInfo("Enderman Spawn Egg", new String[][] { { "end", "man", "egg" } }, Material.MONSTER_EGG, (short)58));
/*  413 */     items.add(new ItemInfo("Cave Spider Spawn Egg", new String[][] { { "cav", "spid", "egg" } }, Material.MONSTER_EGG, (short)59));
/*  414 */     items.add(new ItemInfo("Silverfish Spawn Egg", new String[][] { { "silv", "fish", "egg" } }, Material.MONSTER_EGG, (short)60));
/*  415 */     items.add(new ItemInfo("Blaze Spawn Egg", new String[][] { { "blaze", "egg" } }, Material.MONSTER_EGG, (short)61));
/*  416 */     items.add(new ItemInfo("Magma Cube Spawn Egg", new String[][] { { "mag", "cub", "egg" }, { "neth", "slim", "egg" } }, Material.MONSTER_EGG, (short)62));
/*  417 */     items.add(new ItemInfo("Pig Spawn Egg", new String[][] { { "pig", "spa", "egg" }, { "pig", "egg" } }, Material.MONSTER_EGG, (short)90));
/*  418 */     items.add(new ItemInfo("Sheep Spawn Egg", new String[][] { { "sheep", "egg" } }, Material.MONSTER_EGG, (short)91));
/*  419 */     items.add(new ItemInfo("Cow Spawn Egg", new String[][] { { "cow", "spa", "egg" }, { "cow", "egg" } }, Material.MONSTER_EGG, (short)92));
/*  420 */     items.add(new ItemInfo("Chicken Spawn Egg", new String[][] { { "chick", "egg" } }, Material.MONSTER_EGG, (short)93));
/*  421 */     items.add(new ItemInfo("Squid Spawn Egg", new String[][] { { "squi", "spa", "egg" }, { "squi", "egg" } }, Material.MONSTER_EGG, (short)94));
/*  422 */     items.add(new ItemInfo("Wolf Spawn Egg", new String[][] { { "wolf", "spa", "egg" }, { "wolf", "egg" } }, Material.MONSTER_EGG, (short)95));
/*  423 */     items.add(new ItemInfo("Mooshroom Spawn Egg", new String[][] { { "moo", "room", "egg" }, { "mush", "cow", "egg" } }, Material.MONSTER_EGG, (short)96));
/*  424 */     items.add(new ItemInfo("Ocelot Spawn Egg", new String[][] { { "ocelo", "egg" }, { "ozelo", "egg" } }, Material.MONSTER_EGG, (short)98));
/*  425 */     items.add(new ItemInfo("Villager Spawn Egg", new String[][] { { "villa", "egg" } }, Material.MONSTER_EGG, (short)120));
/*  426 */     items.add(new ItemInfo("Bottle 'o Enchanting", new String[][] { { "bot", "ench" }, { "bot", "xp" } }, Material.EXP_BOTTLE));
/*  427 */     items.add(new ItemInfo("Fire Charge", new String[][] { { "fir", "char" } }, Material.FIREBALL));
/*  428 */     items.add(new ItemInfo("13 Disc", new String[][] { { "dis", "gol" }, { "rec", "gol" }, { "13", "disc" }, { "13", "reco" } }, Material.GOLD_RECORD));
/*  429 */     items.add(new ItemInfo("cat Disc", new String[][] { { "dis", "gre" }, { "rec", "gre" }, { "cat", "disc" }, { "cat", "reco" } }, Material.GREEN_RECORD));
/*  430 */     items.add(new ItemInfo("blocks Disc", new String[][] { { "block", "disc" }, { "block", "reco" }, { "3", "disc" }, { "3", "reco" } }, Material.RECORD_3));
/*  431 */     items.add(new ItemInfo("chirp Disc", new String[][] { { "chirp", "disc" }, { "chirp", "reco" }, { "4", "disc" }, { "4", "reco" } }, Material.RECORD_4));
/*  432 */     items.add(new ItemInfo("far Disc", new String[][] { { "far", "disc" }, { "far", "reco" }, { "5", "disc" }, { "5", "reco" } }, Material.RECORD_5));
/*  433 */     items.add(new ItemInfo("mall Disc", new String[][] { { "mall", "disc" }, { "mall", "reco" }, { "6", "disc" }, { "6", "reco" } }, Material.RECORD_6));
/*  434 */     items.add(new ItemInfo("mellohi Disc", new String[][] { { "mello", "disc" }, { "mello", "reco" }, { "7", "disc" }, { "7", "reco" } }, Material.RECORD_7));
/*  435 */     items.add(new ItemInfo("stahl Disc", new String[][] { { "stahl", "disc" }, { "stahl", "reco" }, { "8", "disc" }, { "8", "reco" } }, Material.RECORD_8));
/*  436 */     items.add(new ItemInfo("strad Disc", new String[][] { { "strad", "disc" }, { "strad", "reco" }, { "9", "disc" }, { "9", "reco" } }, Material.RECORD_9));
/*  437 */     items.add(new ItemInfo("ward Disc", new String[][] { { "ward", "disc" }, { "ward", "reco" }, { "10", "disc" }, { "10", "reco" } }, Material.RECORD_10));
/*  438 */     items.add(new ItemInfo("11 Disc", new String[][] { { "11", "disc" }, { "11", "reco" } }, Material.RECORD_11));
/*  439 */     items.add(new ItemInfo("wait Disc", new String[][] { { "12", "disc" }, { "wait", "disc" }, { "12", "reco" }, { "wait", "reco" } }, Material.RECORD_12));
/*  440 */     items.add(new ItemInfo("Redstone Lamp", new String[][] { { "lamp" }, { "lamp", "redst" } }, Material.REDSTONE_LAMP_OFF));
/*  441 */     items.add(new ItemInfo("Redstone Torch Off", new String[][] { { "off", "red", "sto", "tor" } }, Material.REDSTONE_TORCH_OFF));
/*      */ 
/*  443 */     items.add(new ItemInfo("Emerald Ore", new String[][] { { "emer", "ore" } }, Material.EMERALD_ORE));
/*  444 */     items.add(new ItemInfo("Emerald", new String[][] { { "emer" } }, Material.EMERALD));
/*  445 */     items.add(new ItemInfo("Emerald Block", new String[][] { { "emer", "blo" } }, Material.EMERALD_BLOCK));
/*  446 */     items.add(new ItemInfo("Ender Chest", new String[][] { { "end", "ches" } }, Material.ENDER_CHEST));
/*  447 */     items.add(new ItemInfo("Tripwire Hook", new String[][] { { "hoo", "trip" } }, Material.TRIPWIRE_HOOK));
/*  448 */     items.add(new ItemInfo("Tripwire", new String[][] { { "trip" } }, Material.TRIPWIRE));
/*  449 */     items.add(new ItemInfo("Sandstone Stair", new String[][] { { "stair", "sand", "sto" }, { "stair", "sand" } }, Material.SANDSTONE_STAIRS));
/*      */ 
/*  458 */     items.add(new ItemInfo("Oak Slab", new String[][] { { "slab", "oak" }, { "step", "oak" } }, Material.WOOD_STEP));
/*  459 */     items.add(new ItemInfo("Spruce Slab", new String[][] { { "slab", "spru" }, { "step", "spru" } }, Material.WOOD_STEP, (short)1));
/*  460 */     items.add(new ItemInfo("Birch Slab", new String[][] { { "slab", "birc" }, { "step", "birc" } }, Material.WOOD_STEP, (short)2));
/*  461 */     items.add(new ItemInfo("Jungle Wood Slab", new String[][] { { "jung", "wood", "sla" }, { "slab", "jung" }, { "step", "jung" } }, Material.WOOD_STEP, (short)3));
/*  462 */     items.add(new ItemInfo("Book and Quill", new String[][] { { "qui", "book" } }, Material.BOOK_AND_QUILL));
/*  463 */     items.add(new ItemInfo("Written Book", new String[][] { { "wri", "book" } }, Material.WRITTEN_BOOK));
/*  464 */     items.add(new ItemInfo("Cocoa Pod", new String[][] { { "coco" }, { "coc", "pod" } }, Material.COCOA));
/*      */ 
/*  466 */     items.add(new ItemInfo("Command Block", new String[][] { { "comm" } }, Material.COMMAND));
/*  467 */     items.add(new ItemInfo("Beacon Block", new String[][] { { "beac" } }, Material.BEACON));
/*  468 */     items.add(new ItemInfo("Anvil", new String[][] { { "anv" } }, Material.ANVIL));
/*  469 */     items.add(new ItemInfo("Slightly Damaged Anvil", new String[][] { { "dam", "anv" }, { "sli", "anv" } }, Material.ANVIL, (short)1));
/*  470 */     items.add(new ItemInfo("Very Damaged Anvil", new String[][] { { "ver", "dam", "anv" }, { "ver", "anv" } }, Material.ANVIL, (short)2));
/*  471 */     items.add(new ItemInfo("Flower Pot Block", new String[][] { { "blo", "flow", "pot" } }, Material.FLOWER_POT));
/*  472 */     items.add(new ItemInfo("Flower Pot", new String[][] { { "flow", "pot" } }, Material.FLOWER_POT_ITEM));
/*  473 */     items.add(new ItemInfo("Cobblestone Wall", new String[][] { { "cobble", "wall" } }, Material.COBBLE_WALL));
/*  474 */     items.add(new ItemInfo("Mossy Cobblestone Wall", new String[][] { { "mos", "cob", "wall" } }, Material.COBBLE_WALL, (short)1));
/*  475 */     items.add(new ItemInfo("Item Frame", new String[][] { { "fram" } }, Material.ITEM_FRAME));
/*  476 */     items.add(new ItemInfo("Skeleton Skull", new String[][] { { "skel", "skul" }, { "skel", "hea" } }, Material.SKULL_ITEM));
/*  477 */     items.add(new ItemInfo("Wither Skeleton Skull", new String[][] { { "wither", "skul" }, { "with", "hea" } }, Material.SKULL_ITEM, (short)1));
/*  478 */     items.add(new ItemInfo("Zombie Head", new String[][] { { "zomb", "hea" }, { "zomb", "skul" } }, Material.SKULL_ITEM, (short)2));
/*  479 */     items.add(new ItemInfo("Human Head", new String[][] { { "huma", "skul" }, { "huma", "hea" } }, Material.SKULL_ITEM, (short)3));
/*  480 */     items.add(new ItemInfo("Creeper Head", new String[][] { { "cree", "skul" }, { "cree", "hea" } }, Material.SKULL_ITEM, (short)4));
/*  481 */     items.add(new ItemInfo("Carrot", new String[][] { { "carro" } }, Material.CARROT_ITEM));
/*  482 */     items.add(new ItemInfo("Golden Carrot", new String[][] { { "carr", "gol" } }, Material.GOLDEN_CARROT));
/*  483 */     items.add(new ItemInfo("Carrot Block", new String[][] { { "blo", "carr" } }, Material.CARROT));
/*  484 */     items.add(new ItemInfo("Carrot on a Stick", new String[][] { { "sti", "carr" } }, Material.CARROT_STICK));
/*  485 */     items.add(new ItemInfo("Potato", new String[][] { { "pota" } }, Material.POTATO_ITEM));
/*  486 */     items.add(new ItemInfo("Potato Block", new String[][] { { "blo", "pota" } }, Material.POTATO));
/*  487 */     items.add(new ItemInfo("Baked Potato", new String[][] { { "pota", "bak" } }, Material.BAKED_POTATO));
/*  488 */     items.add(new ItemInfo("Poisonous Potato", new String[][] { { "pota", "poi" } }, Material.POISONOUS_POTATO));
/*  489 */     items.add(new ItemInfo("Wood Button", new String[][] { { "woo", "butto" } }, Material.WOOD_BUTTON));
/*  490 */     items.add(new ItemInfo("Pumpkin Pie", new String[][] { { "pie" }, { "pumpk", "pie" } }, Material.PUMPKIN_PIE));
/*  491 */     items.add(new ItemInfo("Potion of Invisibility", new String[][] { { "poti", "invi" } }, Material.POTION, (short)8206));
/*  492 */     items.add(new ItemInfo("Potion of Invisibility (Extended)", new String[][] { { "poti", "invi", "ext" } }, Material.POTION, (short)8270));
/*  493 */     items.add(new ItemInfo("Potion of Night Vision", new String[][] { { "poti", "nigh", "visi" }, { "poti", "visio" } }, Material.POTION, (short)8198));
/*  494 */     items.add(new ItemInfo("Potion of Night Vision (Extended)", new String[][] { { "poti", "nigh", "visi", "ext" }, { "poti", "visio", "ext" } }, Material.POTION, (short)8262));
/*  495 */     items.add(new ItemInfo("Enchanted Book", new String[][] { { "ench", "boo" } }, Material.ENCHANTED_BOOK));
/*  496 */     items.add(new ItemInfo("Nether Star", new String[][] { { "star", "neth" } }, Material.NETHER_STAR));
/*  497 */     items.add(new ItemInfo("Firework Star", new String[][] { { "fire", "star" } }, Material.FIREWORK_CHARGE));
/*  498 */     items.add(new ItemInfo("Firework Rocket", new String[][] { { "rocket" }, { "firework" } }, Material.FIREWORK));
/*  499 */     items.add(new ItemInfo("White Firework Star", new String[][] { { "whi", "fire", "star" } }, Material.FIREWORK_CHARGE, (short)1));
/*  500 */     items.add(new ItemInfo("Orange Firework Star", new String[][] { { "ora", "fire", "star" } }, Material.FIREWORK_CHARGE, (short)2));
/*  501 */     items.add(new ItemInfo("Magenta Firework Star", new String[][] { { "mag", "fire", "star" } }, Material.FIREWORK_CHARGE, (short)3));
/*  502 */     items.add(new ItemInfo("Light Blue Firework Star", new String[][] { { "blu", "lig", "fire", "star" } }, Material.FIREWORK_CHARGE, (short)4));
/*  503 */     items.add(new ItemInfo("Yellow Firework Star", new String[][] { { "yell", "fire", "star" } }, Material.FIREWORK_CHARGE, (short)5));
/*  504 */     items.add(new ItemInfo("Lime Firework Star", new String[][] { { "lim", "fire", "star" } }, Material.FIREWORK_CHARGE, (short)6));
/*  505 */     items.add(new ItemInfo("Pink Firework Star", new String[][] { { "pin", "fire", "star" } }, Material.FIREWORK_CHARGE, (short)7));
/*  506 */     items.add(new ItemInfo("Gray Firework Star", new String[][] { { "gra", "fire", "star" } }, Material.FIREWORK_CHARGE, (short)8));
/*  507 */     items.add(new ItemInfo("Light Gray Firework Star", new String[][] { { "lig", "gra", "fire", "star" } }, Material.FIREWORK_CHARGE, (short)9));
/*  508 */     items.add(new ItemInfo("Cyan Firework Star", new String[][] { { "cya", "fire", "star" } }, Material.FIREWORK_CHARGE, (short)10));
/*  509 */     items.add(new ItemInfo("Purple Firework Star", new String[][] { { "pur", "fire", "star" } }, Material.FIREWORK_CHARGE, (short)11));
/*  510 */     items.add(new ItemInfo("Blue Firework Star", new String[][] { { "blue", "fire", "star" } }, Material.FIREWORK_CHARGE, (short)12));
/*  511 */     items.add(new ItemInfo("Brown Firework Star", new String[][] { { "bro", "fire", "star" } }, Material.FIREWORK_CHARGE, (short)13));
/*  512 */     items.add(new ItemInfo("Green Firework Star", new String[][] { { "gre", "fire", "star" } }, Material.FIREWORK_CHARGE, (short)14));
/*  513 */     items.add(new ItemInfo("Red Firework Star", new String[][] { { "red", "fire", "star" } }, Material.FIREWORK_CHARGE, (short)15));
/*  514 */     items.add(new ItemInfo("Black Firework Star", new String[][] { { "bla", "fire", "star" } }, Material.FIREWORK_CHARGE, (short)16));
/*  515 */     items.add(new ItemInfo("Dead Bush", new String[][] { { "dea", "bush" } }, Material.DEAD_BUSH));
/*  516 */     items.add(new ItemInfo("Nether Brick Slab", new String[][] { { "sla", "net", "bri" }, { "step", "net", "bri" } }, Material.STEP, (short)6));
/*      */ 
/*  518 */     items.add(new ItemInfo("Activator Rail", new String[][] { { "rail", "acti" }, { "trac", "acti" }, { "activ" } }, Material.ACTIVATOR_RAIL));
/*  519 */     items.add(new ItemInfo("Block of Redstone", new String[][] { { "block", "red" }, { "block", "rs" } }, Material.REDSTONE_BLOCK));
/*  520 */     items.add(new ItemInfo("Daylight Sensor", new String[][] { { "day", "sen" }, { "ligh", "sen" } }, Material.DAYLIGHT_DETECTOR));
/*  521 */     items.add(new ItemInfo("Dropper", new String[][] { { "drop" } }, Material.DROPPER));
/*  522 */     items.add(new ItemInfo("Hopper", new String[][] { { "hop", "item" }, { "hop" } }, Material.HOPPER));
/*  523 */     items.add(new ItemInfo("Explosive Minecart", new String[][] { { "cart", "tnt" }, { "cart", "exp" } }, Material.EXPLOSIVE_MINECART));
/*  524 */     items.add(new ItemInfo("Hopper Minecart", new String[][] { { "cart", "hop" }, { "hop" } }, Material.HOPPER_MINECART));
/*  525 */     items.add(new ItemInfo("Redstone Comparator", new String[][] { { "rs", "compara" }, { "red", "comparat" }, { "comparat" } }, Material.REDSTONE_COMPARATOR));
/*  526 */     items.add(new ItemInfo("Trapped Chest", new String[][] { { "tra", "ches" } }, Material.TRAPPED_CHEST));
/*  527 */     items.add(new ItemInfo("Nether Brick Item", new String[][] { { "neth", "bric", "it" } }, Material.NETHER_BRICK_ITEM));
/*  528 */     items.add(new ItemInfo("Nether Quartz", new String[][] { { "neth", "qua" }, { "qua" } }, Material.QUARTZ));
/*  529 */     items.add(new ItemInfo("Nether Quartz Ore", new String[][] { { "neth", "qua", "ore" }, { "qua", "ore" } }, Material.QUARTZ_ORE));
/*  530 */     items.add(new ItemInfo("Quartz Block", new String[][] { { "qua", "blo" } }, Material.QUARTZ_BLOCK));
/*  531 */     items.add(new ItemInfo("Quartz Slab", new String[][] { { "qua", "slab" }, { "qua", "step" } }, Material.STEP, (short)7));
/*  532 */     items.add(new ItemInfo("Quartz Double Slab", new String[][] { { "qua", "dou", "sla" }, { "qua", "dou", "step" } }, Material.DOUBLE_STEP, (short)7));
/*  533 */     items.add(new ItemInfo("Quartz Stairs", new String[][] { { "qua", "stair" } }, Material.QUARTZ_STAIRS));
/*  534 */     items.add(new ItemInfo("Chiseled Quartz", new String[][] { { "qua", "chis" } }, Material.QUARTZ_BLOCK, (short)1));
/*  535 */     items.add(new ItemInfo("Quartz Pillar", new String[][] { { "qua", "pil" } }, Material.QUARTZ_BLOCK, (short)2));
/*  536 */     items.add(new ItemInfo("Weighted Gold Plate", new String[][] { { "wei", "plat", "gol" }, { "pres", "plat", "gol" } }, Material.GOLD_PLATE));
/*  537 */     items.add(new ItemInfo("Weighted Iron Plate", new String[][] { { "wei", "plat", "iro" }, { "pres", "plat", "iro" } }, Material.IRON_PLATE));
/*      */ 
/*  539 */     items.add(new ItemInfo("Horse Spawn Egg", new String[][] { { "horse", "egg" } }, Material.MONSTER_EGG, (short)100));
/*  540 */     items.add(new ItemInfo("Diamond Horse Armor", new String[][] { { "dia", "horse", "arm" }, { "dia", "bard" } }, Material.DIAMOND_BARDING));
/*  541 */     items.add(new ItemInfo("Gold Horse Armor", new String[][] { { "gold", "horse", "arm" }, { "gold", "bard" } }, Material.GOLD_BARDING));
/*  542 */     items.add(new ItemInfo("Iron Horse Armor", new String[][] { { "iron", "horse", "arm" }, { "iron", "bard" } }, Material.IRON_BARDING));
/*  543 */     items.add(new ItemInfo("Leash", new String[][] { { "leas" }, { "lead" } }, Material.LEASH));
/*  544 */     items.add(new ItemInfo("Hay Bale", new String[][] { { "hay", "bale" }, { "hay", "block" } }, Material.HAY_BLOCK));
/*  545 */     items.add(new ItemInfo("Name Tag", new String[][] { { "name", "tag" } }, Material.NAME_TAG));
/*  546 */     items.add(new ItemInfo("Hardened Clay", new String[][] { { "hard", "clay" } }, Material.HARD_CLAY));
/*  547 */     items.add(new ItemInfo("Block of Coal", new String[][] { { "coal", "block" } }, Material.COAL_BLOCK));
/*  548 */     items.add(new ItemInfo("White Stained Clay", new String[][] { { "clay", "whit" }, { "stai", "clay" }, { "whi", "stain", "cla" } }, Material.STAINED_CLAY));
/*  549 */     items.add(new ItemInfo("Orange Stained Clay", new String[][] { { "clay", "ora" }, { "ora", "stain", "cla" } }, Material.STAINED_CLAY, (short)1));
/*  550 */     items.add(new ItemInfo("Magenta Stained Clay", new String[][] { { "clay", "mag" }, { "mag", "stain", "cla" } }, Material.STAINED_CLAY, (short)2));
/*  551 */     items.add(new ItemInfo("Light Blue Stained Clay", new String[][] { { "clay", "lig", "blue" }, { "lig", "blu", "stain", "cla" } }, Material.STAINED_CLAY, (short)3));
/*  552 */     items.add(new ItemInfo("Yellow Stained Clay", new String[][] { { "clay", "yell" }, { "yell", "stain", "cla" } }, Material.STAINED_CLAY, (short)4));
/*  553 */     items.add(new ItemInfo("Lime Stained Clay", new String[][] { { "clay", "lig", "gree" }, { "clay", "lime" }, { "lime", "stain", "cla" } }, Material.STAINED_CLAY, (short)5));
/*  554 */     items.add(new ItemInfo("Pink Stained Clay", new String[][] { { "clay", "pink" }, { "pink", "stain", "cla" } }, Material.STAINED_CLAY, (short)6));
/*  555 */     items.add(new ItemInfo("Gray Stained Clay", new String[][] { { "clay", "gray" }, { "clay", "grey" }, { "gra", "stain", "cla" }, { "gre", "stain", "cla" } }, Material.STAINED_CLAY, (short)7));
/*  556 */     items.add(new ItemInfo("Light Gray Stained Clay", new String[][] { { "lig", "clay", "gra" }, { "lig", "clay", "gre" }, { "lig", "gra", "stain", "cla" } }, Material.STAINED_CLAY, (short)8));
/*  557 */     items.add(new ItemInfo("Cyan Stained Clay", new String[][] { { "clay", "cya" }, { "cya", "stain", "cla" } }, Material.STAINED_CLAY, (short)9));
/*  558 */     items.add(new ItemInfo("Purple Stained Clay", new String[][] { { "clay", "pur" }, { "pur", "stain", "cla" } }, Material.STAINED_CLAY, (short)10));
/*  559 */     items.add(new ItemInfo("Blue Stained Clay", new String[][] { { "clay", "blue" }, { "blue", "stain", "cla" } }, Material.STAINED_CLAY, (short)11));
/*  560 */     items.add(new ItemInfo("Brown Stained Clay", new String[][] { { "clay", "brown" }, { "brown", "stain", "cla" } }, Material.STAINED_CLAY, (short)12));
/*  561 */     items.add(new ItemInfo("Green Stained Clay", new String[][] { { "clay", "gree" }, { "gree", "stain", "cla" } }, Material.STAINED_CLAY, (short)13));
/*  562 */     items.add(new ItemInfo("Red Stained Clay", new String[][] { { "clay", "red" }, { "red", "stain", "cla" } }, Material.STAINED_CLAY, (short)14));
/*  563 */     items.add(new ItemInfo("Black Stained Clay", new String[][] { { "clay", "bla" }, { "bla", "stain", "cla" } }, Material.STAINED_CLAY, (short)15));
/*  564 */     items.add(new ItemInfo("White Carpet", new String[][] { { "carpet", "whit" }, { "carpet" } }, Material.CARPET));
/*  565 */     items.add(new ItemInfo("Orange Carpet", new String[][] { { "carpet", "ora" } }, Material.CARPET, (short)1));
/*  566 */     items.add(new ItemInfo("Magenta Carpet", new String[][] { { "carpet", "mag" } }, Material.CARPET, (short)2));
/*  567 */     items.add(new ItemInfo("Light Blue Carpet", new String[][] { { "carpet", "lig", "blue" } }, Material.CARPET, (short)3));
/*  568 */     items.add(new ItemInfo("Yellow Carpet", new String[][] { { "carpet", "yell" } }, Material.CARPET, (short)4));
/*  569 */     items.add(new ItemInfo("Light Green Carpet", new String[][] { { "carpet", "lig", "gree" }, { "carpet", "gree" } }, Material.CARPET, (short)5));
/*  570 */     items.add(new ItemInfo("Pink Carpet", new String[][] { { "carpet", "pink" } }, Material.CARPET, (short)6));
/*  571 */     items.add(new ItemInfo("Gray Carpet", new String[][] { { "carpet", "gray" }, { "carpet", "grey" } }, Material.CARPET, (short)7));
/*  572 */     items.add(new ItemInfo("Light Gray Carpet", new String[][] { { "lig", "carpet", "gra" }, { "lig", "carpet", "gre" } }, Material.CARPET, (short)8));
/*  573 */     items.add(new ItemInfo("Cyan Carpet", new String[][] { { "carpet", "cya" } }, Material.CARPET, (short)9));
/*  574 */     items.add(new ItemInfo("Purple Carpet", new String[][] { { "carpet", "pur" } }, Material.CARPET, (short)10));
/*  575 */     items.add(new ItemInfo("Blue Carpet", new String[][] { { "carpet", "blue" } }, Material.CARPET, (short)11));
/*  576 */     items.add(new ItemInfo("Brown Carpet", new String[][] { { "carpet", "brow" } }, Material.CARPET, (short)12));
/*  577 */     items.add(new ItemInfo("Dark Green Carpet", new String[][] { { "carpet", "dar", "gree" }, { "carpet", "gree" } }, Material.CARPET, (short)13));
/*  578 */     items.add(new ItemInfo("Red Carpet", new String[][] { { "carpet", "red" } }, Material.CARPET, (short)14));
/*  579 */     items.add(new ItemInfo("Black Carpet", new String[][] { { "carpet", "bla" } }, Material.CARPET, (short)15));
/*      */ 
/*  581 */     items.add(new ItemInfo("Packed Ice", new String[][] { { "pack", "ice" } }, Material.PACKED_ICE));
/*      */ 
/*  583 */     items.add(new ItemInfo("Coarse Dirt", new String[][] { { "coar", "dirt" }, { "less", "dirt" } }, Material.DIRT, (short)1));
/*  584 */     items.add(new ItemInfo("Acacia Log", new String[][] { { "acac" }, { "log", "acac" } }, Material.LOG_2));
/*  585 */     items.add(new ItemInfo("Dark Oak Log", new String[][] { { "oak", "dar" }, { "log", "oak", "dar" } }, Material.LOG_2, (short)1));
/*  586 */     items.add(new ItemInfo("Acacia Plank", new String[][] { { "acac", "plank" }, { "acac", "wood" } }, Material.WOOD, (short)4));
/*  587 */     items.add(new ItemInfo("Dark Oak Plank", new String[][] { { "dar", "oak", "plank" }, { "dar", "oak", "wood" } }, Material.WOOD, (short)5));
/*  588 */     items.add(new ItemInfo("Acacia Wood Stairs", new String[][] { { "stair", "wood", "acac" }, { "acac", "stair" } }, Material.ACACIA_STAIRS));
/*  589 */     items.add(new ItemInfo("Dark Oak Wood Stairs", new String[][] { { "stair", "wood", "dar", "oak" }, { "dar", "oak", "stair" } }, Material.DARK_OAK_STAIRS));
/*  590 */     items.add(new ItemInfo("Acacia Sapling", new String[][] { { "sapl", "acac" } }, Material.SAPLING, (short)4));
/*  591 */     items.add(new ItemInfo("Dark Oak Sapling", new String[][] { { "sapl", "oak", "dar" } }, Material.SAPLING, (short)5));
/*  592 */     items.add(new ItemInfo("Acacia Leaves", new String[][] { { "lea", "acac" } }, Material.LEAVES_2));
/*  593 */     items.add(new ItemInfo("Dark Oak Leaves", new String[][] { { "lea", "oak", "dar" } }, Material.LEAVES_2, (short)1));
/*  594 */     items.add(new ItemInfo("Packed Ice", new String[][] { { "ice", "pac" }, { "ice", "opaq" } }, Material.PACKED_ICE));
/*  595 */     items.add(new ItemInfo("Podzol", new String[][] { { "podz" }, { "dirt", "pod" } }, Material.DIRT, (short)2));
/*  596 */     items.add(new ItemInfo("Red Sand", new String[][] { { "red", "sand" } }, Material.SAND, (short)1));
/*  597 */     items.add(new ItemInfo("Cobblestone Monster Egg", new String[][] { { "cobb", "sto", "mons", "egg" }, { "cobb", "mons", "egg" }, { "hid", "silver", "cob" } }, Material.MONSTER_EGGS, (short)1));
/*  598 */     items.add(new ItemInfo("Cracked Stone Brick Monster Egg", new String[][] { { "cra", "sto", "bri", "mons", "egg" }, { "hid", "silver", "cra", "sto", "bri" } }, Material.MONSTER_EGGS, (short)4));
/*  599 */     items.add(new ItemInfo("Chiseled Stone Brick Monster Egg", new String[][] { { "chi", "stone", "bri", "mons", "egg" }, { "hid", "silver", "chi", "sto", "bri" } }, Material.MONSTER_EGGS, (short)5));
/*  600 */     items.add(new ItemInfo("White Stained Glass", new String[][] { { "stai", "glas", "whit" }, { "stai", "glas" } }, Material.STAINED_GLASS));
/*  601 */     items.add(new ItemInfo("Orange Stained Glass", new String[][] { { "stai", "glas", "ora" } }, Material.STAINED_GLASS, (short)1));
/*  602 */     items.add(new ItemInfo("Magenta Stained Glass", new String[][] { { "stai", "glas", "mag" } }, Material.STAINED_GLASS, (short)2));
/*  603 */     items.add(new ItemInfo("Light Blue Stained Glass", new String[][] { { "stai", "glas", "lig", "blue" } }, Material.STAINED_GLASS, (short)3));
/*  604 */     items.add(new ItemInfo("Yellow Stained Glass", new String[][] { { "stai", "glas", "yell" } }, Material.STAINED_GLASS, (short)4));
/*  605 */     items.add(new ItemInfo("Light Green Stained Glass", new String[][] { { "stai", "glas", "lig", "gree" }, { "stai", "glas", "gree" } }, Material.STAINED_GLASS, (short)5));
/*  606 */     items.add(new ItemInfo("Pink Stained Glass", new String[][] { { "stai", "glas", "pink" } }, Material.STAINED_GLASS, (short)6));
/*  607 */     items.add(new ItemInfo("Gray Stained Glass", new String[][] { { "stai", "glas", "gra" }, { "stai", "glas", "gre" } }, Material.STAINED_GLASS, (short)7));
/*  608 */     items.add(new ItemInfo("Light Gray Stained Glass", new String[][] { { "lig", "stai", "glas", "gra" }, { "lig", "stai", "glas", "gre" } }, Material.STAINED_GLASS, (short)8));
/*  609 */     items.add(new ItemInfo("Cyan Stained Glass", new String[][] { { "stai", "glas", "cya" } }, Material.STAINED_GLASS, (short)9));
/*  610 */     items.add(new ItemInfo("Purple Stained Glass", new String[][] { { "stai", "glas", "pur" } }, Material.STAINED_GLASS, (short)10));
/*  611 */     items.add(new ItemInfo("Blue Stained Glass", new String[][] { { "stai", "glas", "blue" } }, Material.STAINED_GLASS, (short)11));
/*  612 */     items.add(new ItemInfo("Brown Stained Glass", new String[][] { { "stai", "glas", "brow" } }, Material.STAINED_GLASS, (short)12));
/*  613 */     items.add(new ItemInfo("Dark Green Stained Glass", new String[][] { { "stai", "glas", "dar", "gree" }, { "stai", "glas", "gree" } }, Material.STAINED_GLASS, (short)13));
/*  614 */     items.add(new ItemInfo("Red Stained Glass", new String[][] { { "stai", "glas", "red" } }, Material.STAINED_GLASS, (short)14));
/*  615 */     items.add(new ItemInfo("Black Stained Glass", new String[][] { { "stai", "glas", "bla" } }, Material.STAINED_GLASS, (short)15));
/*  616 */     items.add(new ItemInfo("White Stained Glass Pane", new String[][] { { "stai", "glas", "pane", "whit" }, { "stai", "glas", "pane" } }, Material.STAINED_GLASS_PANE));
/*  617 */     items.add(new ItemInfo("Orange Stained Glass Pane", new String[][] { { "stai", "glas", "pane", "ora" } }, Material.STAINED_GLASS_PANE, (short)1));
/*  618 */     items.add(new ItemInfo("Magenta Stained Glass Pane", new String[][] { { "stai", "glas", "pane", "mag" } }, Material.STAINED_GLASS_PANE, (short)2));
/*  619 */     items.add(new ItemInfo("Light Blue Stained Glass Pane", new String[][] { { "stai", "glas", "pane", "lig", "blue" } }, Material.STAINED_GLASS_PANE, (short)3));
/*  620 */     items.add(new ItemInfo("Yellow Stained Glass Pane", new String[][] { { "stai", "glas", "pane", "yell" } }, Material.STAINED_GLASS_PANE, (short)4));
/*  621 */     items.add(new ItemInfo("Light Green Stained Glass Pane", new String[][] { { "stai", "glas", "pane", "lig", "gree" }, { "stai", "glas", "pane", "gree" } }, Material.STAINED_GLASS_PANE, (short)5));
/*  622 */     items.add(new ItemInfo("Pink Stained Glass Pane", new String[][] { { "stai", "glas", "pane", "pink" } }, Material.STAINED_GLASS_PANE, (short)6));
/*  623 */     items.add(new ItemInfo("Gray Stained Glass Pane", new String[][] { { "stai", "glas", "pane", "gra" }, { "stai", "glas", "pane", "gre" } }, Material.STAINED_GLASS_PANE, (short)7));
/*  624 */     items.add(new ItemInfo("Light Gray Stained Glass Pane", new String[][] { { "lig", "stai", "glas", "pane", "gra" }, { "lig", "stai", "glas", "pane", "gre" } }, Material.STAINED_GLASS_PANE, (short)8));
/*  625 */     items.add(new ItemInfo("Cyan Stained Glass Pane", new String[][] { { "stai", "glas", "pane", "cya" } }, Material.STAINED_GLASS_PANE, (short)9));
/*  626 */     items.add(new ItemInfo("Purple Stained Glass Pane", new String[][] { { "stai", "glas", "pane", "pur" } }, Material.STAINED_GLASS_PANE, (short)10));
/*  627 */     items.add(new ItemInfo("Blue Stained Glass Pane", new String[][] { { "stai", "glas", "pane", "blue" } }, Material.STAINED_GLASS_PANE, (short)11));
/*  628 */     items.add(new ItemInfo("Brown Stained Glass Pane", new String[][] { { "stai", "glas", "pane", "brow" } }, Material.STAINED_GLASS_PANE, (short)12));
/*  629 */     items.add(new ItemInfo("Dark Green Stained Glass Pane", new String[][] { { "stai", "glas", "pane", "dar", "gree" }, { "stai", "glas", "pane", "gree" } }, Material.STAINED_GLASS_PANE, (short)13));
/*  630 */     items.add(new ItemInfo("Red Stained Glass Pane", new String[][] { { "stai", "glas", "pane", "red" } }, Material.STAINED_GLASS_PANE, (short)14));
/*  631 */     items.add(new ItemInfo("Black Stained Glass Pane", new String[][] { { "stai", "glas", "pane", "bla" } }, Material.STAINED_GLASS_PANE, (short)15));
/*  632 */     items.add(new ItemInfo("Poppy", new String[][] { { "flow", "red" }, { "rose" }, { "poppy" } }, Material.RED_ROSE));
/*  633 */     items.add(new ItemInfo("Blue Orchid", new String[][] { { "flow", "blue" }, { "orch", "blue" } }, Material.RED_ROSE, (short)1));
/*  634 */     items.add(new ItemInfo("Allium", new String[][] { { "flow", "mag" }, { "alli" } }, Material.RED_ROSE, (short)2));
/*  635 */     items.add(new ItemInfo("Azure Bluet", new String[][] { { "flow", "whit" }, { "azu", "blue" } }, Material.RED_ROSE, (short)3));
/*  636 */     items.add(new ItemInfo("Red Tulip", new String[][] { { "tul", "red" } }, Material.RED_ROSE, (short)4));
/*  637 */     items.add(new ItemInfo("Orange Tulip", new String[][] { { "tul", "ora" } }, Material.RED_ROSE, (short)5));
/*  638 */     items.add(new ItemInfo("White Tulip", new String[][] { { "tul", "whit" } }, Material.RED_ROSE, (short)6));
/*  639 */     items.add(new ItemInfo("Pink Tulip", new String[][] { { "tul", "pin" } }, Material.RED_ROSE, (short)7));
/*  640 */     items.add(new ItemInfo("Oxeye Daisy", new String[][] { { "dais" }, { "oxe", "dais" } }, Material.RED_ROSE, (short)8));
/*  641 */     items.add(new ItemInfo("Sunflower", new String[][] { { "flow", "sun" } }, Material.DOUBLE_PLANT, (short)0));
/*  642 */     items.add(new ItemInfo("Lilac", new String[][] { { "flow", "lila" }, { "lila" } }, Material.DOUBLE_PLANT, (short)1));
/*  643 */     items.add(new ItemInfo("Double Tallgrass", new String[][] { { "doub", "tall", "gras" }, { "doub", "long", "gras" } }, Material.DOUBLE_PLANT, (short)2));
/*  644 */     items.add(new ItemInfo("Large Fern", new String[][] { { "larg", "fern" }, { "doub", "fern" } }, Material.DOUBLE_PLANT, (short)3));
/*  645 */     items.add(new ItemInfo("Rose Bush", new String[][] { { "bush", "rose" } }, Material.DOUBLE_PLANT, (short)4));
/*  646 */     items.add(new ItemInfo("Peony", new String[][] { { "flow", "peon" }, { "peon" } }, Material.DOUBLE_PLANT, (short)5));
/*  647 */     items.add(new ItemInfo("Command Minecart", new String[][] { { "cart", "comm" } }, Material.COMMAND_MINECART));
/*  648 */     items.add(new ItemInfo("Potion of Water Breathing", new String[][] { { "poti", "wate", "breat" } }, Material.POTION, (short)8205));
/*  649 */     items.add(new ItemInfo("Potion of Water Breathing (Reverted)", new String[][] { { "poti", "wate", "breat", "rev" } }, Material.POTION, (short)8237));
/*  650 */     items.add(new ItemInfo("Potion of Water Breathing (Extended)", new String[][] { { "poti", "wate", "breat", "ext" } }, Material.POTION, (short)8269));
/*  651 */     items.add(new ItemInfo("Splash Potion of Water Breathing", new String[][] { { "poti", "wate", "breat", "spl" } }, Material.POTION, (short)16397));
/*  652 */     items.add(new ItemInfo("Splash Potion of Water Breathing (Reverted)", new String[][] { { "poti", "wate", "breat", "rev", "spl" } }, Material.POTION, (short)16429));
/*  653 */     items.add(new ItemInfo("Splash Potion of Water Breathing (Extended)", new String[][] { { "poti", "wate", "breat", "ext", "spl" } }, Material.POTION, (short)16461));
/*  654 */     items.add(new ItemInfo("Raw Salmon", new String[][] { { "salm" }, { "raw", "salm" } }, Material.RAW_FISH, (short)1));
/*  655 */     items.add(new ItemInfo("Cooked Salmon", new String[][] { { "salm", "cook" } }, Material.COOKED_FISH, (short)1));
/*  656 */     items.add(new ItemInfo("Clownfish", new String[][] { { "fish", "clow" } }, Material.RAW_FISH, (short)2));
/*  657 */     items.add(new ItemInfo("Pufferfish", new String[][] { { "fish", "puff" }, { "fish", "blo" }, { "fish", "glob" } }, Material.RAW_FISH, (short)3));
/*  658 */     items.add(new ItemInfo("Acacia Slab", new String[][] { { "slab", "aca" }, { "step", "aca" } }, Material.WOOD_STEP, (short)4));
/*  659 */     items.add(new ItemInfo("Dark Oak Slab", new String[][] { { "slab", "dar", "oak" }, { "step", "dar", "oak" } }, Material.WOOD_STEP, (short)5));
/*      */ 
/*  661 */     items.add(new ItemInfo("Granite", new String[][] { { "gran" } }, Material.STONE, (short)1));
/*  662 */     items.add(new ItemInfo("Polished Granite", new String[][] { { "pol", "gran" }, { "smoo", "gran" } }, Material.STONE, (short)2));
/*  663 */     items.add(new ItemInfo("Diorite", new String[][] { { "dior" } }, Material.STONE, (short)3));
/*  664 */     items.add(new ItemInfo("Polished Diorite", new String[][] { { "pol", "dior" }, { "smoo", "dior" } }, Material.STONE, (short)4));
/*  665 */     items.add(new ItemInfo("Andesite", new String[][] { { "ande" } }, Material.STONE, (short)5));
/*  666 */     items.add(new ItemInfo("Polished Andesite", new String[][] { { "pol", "ande" }, { "smoo", "ande" } }, Material.STONE, (short)6));
/*  667 */     items.add(new ItemInfo("Slime Block", new String[][] { { "sli", "blo" } }, Material.SLIME_BLOCK));
/*  668 */     items.add(new ItemInfo("Wet Sponge", new String[][] { { "wet", "spon" } }, Material.SPONGE, (short)1));
/*  669 */     items.add(new ItemInfo("Barrier", new String[][] { { "barri" } }, Material.BARRIER));
/*  670 */     items.add(new ItemInfo("Iron Trapdoor", new String[][] { { "tra", "doo", "iron" }, { "iron", "hatc" } }, Material.IRON_TRAPDOOR));
/*  671 */     items.add(new ItemInfo("Prismarine", new String[][] { { "pris", "mar" } }, Material.PRISMARINE));
/*  672 */     items.add(new ItemInfo("Prismarine Bricks", new String[][] { { "bri", "pris", "mar" } }, Material.PRISMARINE, (short)1));
/*  673 */     items.add(new ItemInfo("Dark Prismarine", new String[][] { { "dar", "pris", "mar" } }, Material.PRISMARINE, (short)2));
/*  674 */     items.add(new ItemInfo("Sea Lantern", new String[][] { { "sea", "lan" } }, Material.SEA_LANTERN));
/*  675 */     items.add(new ItemInfo("Red Sandstone", new String[][] { { "red", "san", "sto" } }, Material.RED_SANDSTONE));
/*  676 */     items.add(new ItemInfo("Chiseled Red Sandstone", new String[][] { { "red", "chi", "san", "sto" } }, Material.RED_SANDSTONE, (short)1));
/*  677 */     items.add(new ItemInfo("Smooth Red Sandstone", new String[][] { { "red", "smoo", "san", "sto" } }, Material.RED_SANDSTONE, (short)2));
/*  678 */     items.add(new ItemInfo("Red Sandstone Stairs", new String[][] { { "red", "san", "ston", "stai" }, { "red", "san", "ston", "step" } }, Material.RED_SANDSTONE_STAIRS));
/*  679 */     items.add(new ItemInfo("Red Sandstone Slab", new String[][] { { "red", "san", "ston", "slab" }, { "red", "san", "ston", "step" } }, Material.STONE_SLAB2));
/*  680 */     items.add(new ItemInfo("Spruce Fence Gate", new String[][] { { "gate", "spru", "fence" } }, Material.SPRUCE_FENCE_GATE));
/*  681 */     items.add(new ItemInfo("Birch Fence Gate", new String[][] { { "gate", "birc", "fence" } }, Material.BIRCH_FENCE_GATE));
/*  682 */     items.add(new ItemInfo("Jungle Fence Gate", new String[][] { { "gate", "jung", "fence" } }, Material.JUNGLE_FENCE_GATE));
/*  683 */     items.add(new ItemInfo("Dark Oak Fence Gate", new String[][] { { "gate", "dark", "oak", "fence" } }, Material.DARK_OAK_FENCE_GATE));
/*  684 */     items.add(new ItemInfo("Acacia Fence Gate", new String[][] { { "gate", "acac", "fence" } }, Material.ACACIA_FENCE_GATE));
/*  685 */     items.add(new ItemInfo("Spruce Fence", new String[][] { { "spru", "fence" } }, Material.SPRUCE_FENCE));
/*  686 */     items.add(new ItemInfo("Birch Fence", new String[][] { { "birc", "fence" } }, Material.BIRCH_FENCE));
/*  687 */     items.add(new ItemInfo("Jungle Fence", new String[][] { { "jung", "fence" } }, Material.JUNGLE_FENCE));
/*  688 */     items.add(new ItemInfo("Dark Oak Fence", new String[][] { { "dark", "oak", "fence" } }, Material.DARK_OAK_FENCE));
/*  689 */     items.add(new ItemInfo("Acacia Fence", new String[][] { { "acac", "fence" } }, Material.ACACIA_FENCE));
/*  690 */     items.add(new ItemInfo("Spruce Door", new String[][] { { "spru", "door" } }, Material.SPRUCE_DOOR_ITEM));
/*  691 */     items.add(new ItemInfo("Birch Door", new String[][] { { "birc", "door" } }, Material.BIRCH_DOOR_ITEM));
/*  692 */     items.add(new ItemInfo("Jungle Door", new String[][] { { "jung", "door" } }, Material.JUNGLE_DOOR_ITEM));
/*  693 */     items.add(new ItemInfo("Dark Oak Door", new String[][] { { "dark", "oak", "door" } }, Material.DARK_OAK_DOOR_ITEM));
/*  694 */     items.add(new ItemInfo("Acacia Door", new String[][] { { "acac", "door" } }, Material.ACACIA_DOOR_ITEM));
/*  695 */     items.add(new ItemInfo("Prismarine Shard", new String[][] { { "shar", "pris" } }, Material.PRISMARINE_SHARD));
/*  696 */     items.add(new ItemInfo("Prismarine Crystal", new String[][] { { "pris", "crys" } }, Material.PRISMARINE_CRYSTALS));
/*  697 */     items.add(new ItemInfo("Raw Rabbit", new String[][] { { "raw", "rabb" }, { "rabb" } }, Material.RABBIT));
/*  698 */     items.add(new ItemInfo("Cooked Rabbit", new String[][] { { "cook", "rabb" } }, Material.COOKED_RABBIT));
/*  699 */     items.add(new ItemInfo("Rabbit Stew", new String[][] { { "rabb", "stew" } }, Material.RABBIT_STEW));
/*  700 */     items.add(new ItemInfo("Rabbit Foot", new String[][] { { "rabb", "foot" } }, Material.RABBIT_FOOT));
/*  701 */     items.add(new ItemInfo("Rabbit Hide", new String[][] { { "hide", "rab" } }, Material.RABBIT_HIDE));
/*  702 */     items.add(new ItemInfo("Armor Stand", new String[][] { { "armo", "stan" } }, Material.ARMOR_STAND));
/*  703 */     items.add(new ItemInfo("Raw Mutton", new String[][] { { "mutt" }, { "raw", "mutt" } }, Material.MUTTON));
/*  704 */     items.add(new ItemInfo("Cooked Mutton", new String[][] { { "cook", "mutt" } }, Material.COOKED_MUTTON));
/*  705 */     items.add(new ItemInfo("Banner", new String[][] { { "banner" } }, Material.BANNER));
/*  706 */     items.add(new ItemInfo("Potion of Leaping", new String[][] { { "poti", "leap" } }, Material.POTION, (short)8203));
/*  707 */     items.add(new ItemInfo("Potion of Leaping (Extended)", new String[][] { { "poti", "leap", "ext" } }, Material.POTION, (short)8267));
/*  708 */     items.add(new ItemInfo("Potion of Leaping II", new String[][] { { "poti", "leap", "ii" }, { "poti", "leap", "2" } }, Material.POTION, (short)8235));
/*  709 */     items.add(new ItemInfo("Splash Potion of Leaping", new String[][] { { "spl", "poti", "leap" } }, Material.POTION, (short)16395));
/*  710 */     items.add(new ItemInfo("Splash Potion of Leaping (Extended)", new String[][] { { "poti", "leap", "spl", "ext" } }, Material.POTION, (short)16459));
/*  711 */     items.add(new ItemInfo("Splash Potion of Leaping II", new String[][] { { "poti", "leap", "spl", "2" }, { "poti", "leap", "spl", "ii" } }, Material.POTION, (short)16427));
/*  712 */     items.add(new ItemInfo("Guardian Spawn Egg", new String[][] { { "guard", "spaw", "egg" } }, Material.MONSTER_EGG, (short)68));
/*  713 */     items.add(new ItemInfo("Endermite Spawn Egg", new String[][] { { "mite", "end", "spaw", "egg" } }, Material.MONSTER_EGG, (short)67));
/*  714 */     items.add(new ItemInfo("Rabbit Spawn Egg", new String[][] { { "rabb", "spaw", "egg" } }, Material.MONSTER_EGG, (short)101));
/*      */   }
/*      */ }

/* Location:           D:\Github\Mechanics\Vault.jar
 * Qualified Name:     net.milkbowl.vault.item.Items
 * JD-Core Version:    0.6.2
 */