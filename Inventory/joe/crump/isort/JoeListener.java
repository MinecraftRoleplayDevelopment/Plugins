/*      */ package joe.crump.isort;
/*      */ 
/*      */ import java.util.ArrayList;
/*      */ import java.util.Collections;
/*      */ import java.util.Comparator;
/*      */ import java.util.HashMap;
/*      */ import java.util.Map;
/*      */ import org.bukkit.Material;
/*      */ import org.bukkit.block.Block;
/*      */ import org.bukkit.block.Chest;
/*      */ import org.bukkit.block.ContainerBlock;
/*      */ import org.bukkit.enchantments.Enchantment;
/*      */ import org.bukkit.entity.Player;
/*      */ import org.bukkit.event.EventHandler;
/*      */ import org.bukkit.event.EventPriority;
/*      */ import org.bukkit.event.Listener;
/*      */ import org.bukkit.event.block.Action;
/*      */ import org.bukkit.event.player.PlayerInteractEvent;
/*      */ import org.bukkit.inventory.Inventory;
/*      */ import org.bukkit.inventory.ItemStack;
/*      */ import org.bukkit.inventory.meta.EnchantmentStorageMeta;
/*      */ import org.bukkit.inventory.meta.ItemMeta;
/*      */ import org.bukkit.potion.Potion;
/*      */ import org.bukkit.potion.PotionType;
/*      */ 
/*      */ public class JoeListener
/*      */   implements Listener
/*      */ {
/*   68 */   public static Material[] MaterialSortPreference = { 
/*   69 */     Material.COBBLESTONE, 
/*   70 */     Material.DIRT, 
/*   71 */     Material.SAND, 
/*   72 */     Material.SANDSTONE, 
/*   73 */     Material.GRAVEL, 
/*   74 */     Material.GRASS, 
/*   75 */     Material.STONE, 
/*   76 */     Material.LOG, 
/*   77 */     Material.WOOD, 
/*   78 */     Material.GLASS, 
/*   79 */     Material.THIN_GLASS, 
/*   80 */     Material.GLOWSTONE, 
/*   81 */     Material.GLOWSTONE_DUST, 
/*   82 */     Material.STICK, 
/*   83 */     Material.TORCH, 
/*   84 */     Material.SIGN, 
/*   85 */     Material.SIGN_POST, 
/*   86 */     Material.CHEST, 
/*   87 */     Material.OBSIDIAN, 
/*   88 */     Material.IRON_INGOT, 
/*   89 */     Material.IRON_BLOCK, 
/*   90 */     Material.GOLD_NUGGET, 
/*   91 */     Material.GOLD_INGOT, 
/*   92 */     Material.GOLD_BLOCK, 
/*   93 */     Material.DIAMOND, 
/*   94 */     Material.DIAMOND_BLOCK, 
/*   95 */     Material.EMERALD, 
/*   96 */     Material.EMERALD_BLOCK, 
/*   97 */     Material.REDSTONE, 
/*   98 */     Material.REDSTONE_BLOCK, 
/*  100 */     Material.REDSTONE_ORE, 
/*  101 */     Material.GLOWING_REDSTONE_ORE, 
/*  102 */     Material.IRON_ORE, 
/*  103 */     Material.GOLD_ORE, 
/*  104 */     Material.DIAMOND_ORE, 
/*  105 */     Material.EMERALD_ORE, 
/*  106 */     Material.COAL_ORE, 
/*  107 */     Material.LAPIS_ORE, 
/*  109 */     Material.ENDER_STONE, 
/*  110 */     Material.SOUL_SAND, 
/*  111 */     Material.BRICK, 
/*  112 */     Material.CLAY_BRICK, 
/*  113 */     Material.CLAY, 
/*  114 */     Material.CLAY_BALL, 
/*  116 */     Material.COAL, 
/*  117 */     Material.LEATHER, 
/*  118 */     Material.STRING, 
/*  119 */     Material.BONE, 
/*  120 */     Material.FEATHER, 
/*  121 */     Material.INK_SACK, 
/*  122 */     Material.SULPHUR, 
/*  123 */     Material.WEB, 
/*  124 */     Material.SPIDER_EYE, 
/*  125 */     Material.SLIME_BALL, 
/*  127 */     Material.FENCE_GATE, 
/*  128 */     Material.FENCE, 
/*  129 */     Material.NETHER_FENCE, 
/*  131 */     Material.COOKED_BEEF, 
/*  132 */     Material.COOKED_CHICKEN, 
/*  133 */     Material.COOKED_FISH, 
/*  134 */     Material.POTATO, 
/*  135 */     Material.COOKIE, 
/*  136 */     Material.APPLE, 
/*  137 */     Material.MELON, 
/*  138 */     Material.BREAD, 
/*  139 */     Material.MUSHROOM_SOUP, 
/*  140 */     Material.BOWL, 
/*  142 */     Material.RAW_BEEF, 
/*  143 */     Material.RAW_CHICKEN, 
/*  144 */     Material.RAW_FISH, 
/*  145 */     Material.PORK, 
/*  147 */     Material.WHEAT, 
/*  148 */     Material.SUGAR, 
/*  149 */     Material.SUGAR_CANE, 
/*  150 */     Material.SUGAR_CANE_BLOCK, 
/*  151 */     Material.MELON_BLOCK, 
/*  152 */     Material.PUMPKIN, 
/*  153 */     Material.PUMPKIN_PIE, 
/*  154 */     Material.CAKE, 
/*  155 */     Material.CAKE_BLOCK, 
/*  157 */     Material.JACK_O_LANTERN, 
/*  159 */     Material.WOOD_SPADE, 
/*  160 */     Material.STONE_SPADE, 
/*  161 */     Material.IRON_SPADE, 
/*  162 */     Material.GOLD_SPADE, 
/*  163 */     Material.DIAMOND_SPADE, 
/*  164 */     Material.WOOD_PICKAXE, 
/*  165 */     Material.STONE_PICKAXE, 
/*  166 */     Material.IRON_PICKAXE, 
/*  167 */     Material.GOLD_PICKAXE, 
/*  168 */     Material.DIAMOND_PICKAXE, 
/*  169 */     Material.WOOD_AXE, 
/*  170 */     Material.STONE_AXE, 
/*  171 */     Material.IRON_AXE, 
/*  172 */     Material.GOLD_AXE, 
/*  173 */     Material.DIAMOND_AXE, 
/*  174 */     Material.WOOD_HOE, 
/*  175 */     Material.STONE_HOE, 
/*  176 */     Material.IRON_HOE, 
/*  177 */     Material.GOLD_HOE, 
/*  178 */     Material.DIAMOND_HOE, 
/*  179 */     Material.FISHING_ROD, 
/*  180 */     Material.BOW, 
/*  181 */     Material.ARROW, 
/*  182 */     Material.YELLOW_FLOWER, 
/*  183 */     Material.RED_ROSE, 
/*  184 */     Material.FLOWER_POT, 
/*  185 */     Material.ENCHANTED_BOOK, 
/*  186 */     Material.BOOK, 
/*  187 */     Material.BOOK_AND_QUILL, 
/*  188 */     Material.PAPER, 
/*  189 */     Material.MAP, 
/*  190 */     Material.COMPASS };
/*      */ 
/*  227 */   static HashMap<String, Integer> ItemGroupCounts = new HashMap();
/*  228 */   static boolean useSmallGroups = false;
/*      */ 
/*      */   @EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled=true)
/*      */   public void onChestOpen(PlayerInteractEvent event)
/*      */   {
/*   46 */     Player p = event.getPlayer();
/*   47 */     if (p == null) return;
/*      */ 
/*   50 */     if ((p.hasPermission("joe.invsort.noaccess")) && (!p.hasPermission("joe.invsort.access"))) return;
/*      */ 
/*   53 */     if (!JoeUtils.DoesPlayerWantSorting(p)) return;
/*      */ 
/*   55 */     if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK))
/*      */     {
/*   57 */       Block block = event.getClickedBlock();
/*   58 */       Material mat = block.getType();
/*   59 */       if ((mat == Material.CHEST) || (mat == Material.TRAPPED_CHEST))
/*      */       {
/*   62 */         SortChest(block);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public static int GetStringSortValue(String str, boolean alphaOnly)
/*      */   {
/*  211 */     String lwr = str.toLowerCase();
/*  212 */     if (lwr.length() < 4) lwr = String.format("%-4s", new Object[] { lwr });
/*      */ 
/*  214 */     int base = 96;
/*  215 */     int num = 0;
/*  216 */     int curBase = 1;
/*  217 */     for (int i = 3; i >= 0; i--)
/*      */     {
/*  219 */       num += (lwr.charAt(i) - ' ') * curBase;
/*  220 */       curBase *= base;
/*      */     }
/*      */ 
/*  224 */     return num;
/*      */   }
/*      */ 
/*      */   public static long GetOrganizeOrder(ItemStack is)
/*      */   {
/*  231 */     if (is == null) return 9999L;
/*      */ 
/*  233 */     Integer iOffset = (Integer)ItemGroupCounts.get(GetItemGroupType(is));
/*  234 */     Long offset = Long.valueOf(0L);
/*  235 */     if (iOffset != null) offset = Long.valueOf(iOffset.intValue() * -1000000000L);
/*      */ 
/*  238 */     Material mat = is.getType();
/*  239 */     String matName = mat.toString().toLowerCase();
/*  240 */     int baseVal = 0;
/*  241 */     int dur = is.getDurability();
/*      */ 
/*  276 */     String subname = GetItemGroupType(is);
/*      */ 
/*  285 */     if (mat == Material.SKULL_ITEM)
/*      */     {
/*  287 */       String owner = JoeUtils.GetSkullOwnerFromItemStack(is);
/*  288 */       if ((owner != null) && (!owner.isEmpty()))
/*      */       {
/*  290 */         long val = 10L * GetStringSortValue(owner, true);
/*      */ 
/*  292 */         return baseVal + val + offset.longValue();
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  300 */     if (mat == Material.FIREWORK)
/*      */     {
/*  302 */       return baseVal + 1020 + (64 - is.getAmount()) + offset.longValue();
/*      */     }
/*      */ 
/*  307 */     int num = GetStringSortValue(subname, false);
/*      */ 
/*  310 */     if (matName.startsWith("leather")) num += 6000;
/*  311 */     if (matName.startsWith("stone")) num += 5000;
/*  312 */     if (matName.startsWith("iron")) num += 4000;
/*  313 */     if (matName.startsWith("gold")) num += 3000;
/*  314 */     if (matName.startsWith("chain")) num += 2000;
/*  315 */     if (matName.startsWith("diamond")) num += 1000;
/*      */ 
/*  318 */     if (mat == Material.RAW_BEEF) num++;
/*  319 */     if (mat == Material.RAW_CHICKEN) num++;
/*  320 */     if (mat == Material.RAW_FISH) num++;
/*      */ 
/*  323 */     if (mat == Material.MOSSY_COBBLESTONE) num++;
/*  324 */     if (mat == Material.SANDSTONE) num++;
/*  325 */     if (mat == Material.ENDER_CHEST) num++;
/*      */ 
/*      */     try
/*      */     {
/*  330 */       if (mat == Material.STAINED_CLAY) num++;
/*      */     }
/*      */     catch (Throwable localThrowable)
/*      */     {
/*      */     }
/*      */ 
/*  336 */     if (mat == Material.THIN_GLASS) num++;
/*  337 */     if (mat == Material.NETHER_BRICK) num++;
/*  338 */     if (mat == Material.REDSTONE_BLOCK) num++;
/*  339 */     if (mat == Material.STORAGE_MINECART) num++;
/*  340 */     if (mat == Material.POWERED_RAIL) num++;
/*  341 */     if (mat == Material.DETECTOR_RAIL) num += 2;
/*  342 */     if (mat == Material.ACTIVATOR_RAIL) num += 3;
/*  343 */     if (mat == Material.IRON_INGOT) num++;
/*      */ 
/*  345 */     if (mat == Material.CLAY) num++;
/*  346 */     if (mat == Material.SUGAR_CANE) num++;
/*  347 */     if (mat == Material.JACK_O_LANTERN) num++;
/*  348 */     if (mat == Material.MELON_BLOCK) num += 2;
/*  349 */     if (mat == Material.HUGE_MUSHROOM_1) num++;
/*  350 */     if (mat == Material.HUGE_MUSHROOM_2) num += 2;
/*  351 */     if (mat == Material.LEAVES) num++;
/*  352 */     if (mat == Material.SAPLING) num++;
/*      */ 
/*  357 */     if (mat == Material.ENCHANTED_BOOK)
/*      */     {
/*  359 */       int bookDelta = 0;
/*  360 */       ItemMeta meta = is.getItemMeta();
/*  361 */       if ((meta != null) && ((meta instanceof EnchantmentStorageMeta)))
/*      */       {
/*  363 */         EnchantmentStorageMeta bookmeta = (EnchantmentStorageMeta)meta;
/*  364 */         Map enchantments = bookmeta.getStoredEnchants();
/*  365 */         if (enchantments != null)
/*      */         {
/*  367 */           for (Enchantment ekey : enchantments.keySet())
/*      */           {
/*  369 */             bookDelta += ekey.getId() * 10;
/*  370 */             bookDelta += ((Integer)enchantments.get(ekey)).intValue() % 10;
/*      */           }
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*  376 */       return num + bookDelta % 1000 + offset.longValue();
/*      */     }
/*  378 */     if (mat == Material.WRITTEN_BOOK) return num + 1000 + offset.longValue();
/*  379 */     if (mat == Material.BOOK) return num + 1100 + offset.longValue();
/*  380 */     if (mat == Material.BOOK_AND_QUILL) return num + 1200 + offset.longValue();
/*      */ 
/*  383 */     if (mat.toString().contains("RECORD")) num += mat.getId() - 2256;
/*  384 */     if (mat.toString().contains("STAIRS")) num += mat.getId();
/*      */ 
/*  386 */     if (mat == Material.POTION)
/*      */     {
/*      */       try
/*      */       {
/*  393 */         Potion pot = Potion.fromItemStack(is);
/*  394 */         num += pot.getType().ordinal() * 2;
/*  395 */         if (!pot.isSplash()) break label861; num++;
/*      */       }
/*      */       catch (Throwable localThrowable1)
/*      */       {
/*      */       }
/*      */ 
/*      */     }
/*      */     else
/*      */     {
/*  421 */       Map enchMap = is.getEnchantments();
/*  422 */       if (enchMap != null) num += 100 * enchMap.size();
/*      */ 
/*  430 */       if (mat.getMaxDurability() > 0)
/*      */       {
/*  432 */         if (dur != 0) num += 1 + dur * 99 / mat.getMaxDurability();
/*      */       }
/*      */       else
/*      */       {
/*  436 */         num += dur % 100;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  442 */     label861: return num + offset.longValue();
/*      */   }
/*      */ 
/*      */   public static String GetItemGroupType(ItemStack is)
/*      */   {
/*  449 */     Material mat = is.getType();
/*  450 */     int dur = is.getDurability();
/*  451 */     if (useSmallGroups)
/*      */     {
/*  453 */       if ((mat == Material.PUMPKIN) || (mat == Material.JACK_O_LANTERN) || (mat == Material.MELON_BLOCK)) return "PKIN";
/*  454 */       if ((mat == Material.BOOK) || (mat == Material.WRITTEN_BOOK) || (mat == Material.ENCHANTED_BOOK) || (mat == Material.BOOK_AND_QUILL)) return "B(0K";
/*      */ 
/*  457 */       if ((mat == Material.HUGE_MUSHROOM_1) || (mat == Material.HUGE_MUSHROOM_2) || 
/*  458 */         (mat == Material.BROWN_MUSHROOM) || (mat == Material.RED_MUSHROOM))
/*      */       {
/*  461 */         return "MSRM";
/*      */       }
/*      */ 
/*  464 */       if (((mat == Material.LONG_GRASS) && (dur != 0)) || (mat == Material.LEAVES) || (mat == Material.SAPLING)) return "X(Z1";
/*      */ 
/*      */     }
/*  469 */     else if ((mat == Material.PUMPKIN) || (mat == Material.JACK_O_LANTERN)) { return "PKIN"; }
/*      */ 
/*  471 */     if ((mat == Material.DEAD_BUSH) || ((mat == Material.LONG_GRASS) && (dur == 0))) return "XYZ1";
/*      */ 
/*  474 */     String matName = mat.toString().toLowerCase();
/*  475 */     matName = matName.replace("_item", "");
/*  476 */     matName = matName.replace("_1", "");
/*  477 */     matName = matName.replace("_block", "");
/*      */ 
/*  483 */     if (!useSmallGroups)
/*      */     {
/*  485 */       if (mat == Material.WOOD_STEP) return String.format("#V%02d", new Object[] { Integer.valueOf(dur) });
/*      */ 
/*  487 */       if (mat == Material.WOOD) return String.format("#W%02d", new Object[] { Integer.valueOf(dur + 16) });
/*  488 */       if (mat == Material.LOG) return String.format("#X%02d", new Object[] { Integer.valueOf(dur + 32) });
/*  489 */       if (mat == Material.WOOL) return String.format("#Y%02d", new Object[] { Integer.valueOf(dur + 48) });
/*      */ 
/*      */       try
/*      */       {
/*  494 */         if ((mat == Material.CLAY) || (mat == Material.STAINED_CLAY)) return String.format("#Z%02d", new Object[] { Integer.valueOf(dur + 48) });
/*      */ 
/*      */       }
/*      */       catch (Throwable localThrowable)
/*      */       {
/*  501 */         if (mat == Material.MOSSY_COBBLESTONE) return "AAMC";
/*      */       }
/*      */     }
/*  504 */     if ((mat == Material.YELLOW_FLOWER) || (mat == Material.RED_ROSE)) return "#ZFW";
/*      */ 
/*  506 */     int idx = matName.lastIndexOf("_");
/*  507 */     String subname = matName;
/*  508 */     if (idx >= 0) subname = matName.substring(idx + 1);
/*      */ 
/*  511 */     if ((!useSmallGroups) && (subname.equals("stairs"))) return "JS" + matName.toString().substring(0, 2);
/*      */ 
/*  520 */     if (subname.equals("sword")) return "#A01";
/*  521 */     if (subname.equals("spade")) return "#B01";
/*  522 */     if (subname.equals("pickaxe")) return "#C01";
/*  523 */     if (subname.equals("axe")) return "#D01";
/*  524 */     if (subname.equals("hoe")) return "#D02";
/*  525 */     if (subname.equals("shears")) return "#D03";
/*      */ 
/*  527 */     if (mat == Material.FLINT_AND_STEEL) return "#E01";
/*      */ 
/*  529 */     if (subname.equals("helmet")) return "#L01";
/*  530 */     if (subname.equals("chestplate")) return "#M01";
/*  531 */     if (subname.equals("leggings")) return "#O01";
/*  532 */     if (subname.equals("boots")) return "#P01";
/*      */ 
/*  535 */     if (mat == Material.CARROT_STICK) return "#Q01";
/*  536 */     if (mat == Material.FISHING_ROD) return "#Q02";
/*      */ 
/*  542 */     if (!useSmallGroups)
/*      */     {
/*  544 */       if (mat == Material.CHEST) return "NN01";
/*  545 */       if (mat == Material.ENDER_CHEST) return "NN02";
/*  546 */       if (mat == Material.GOLD_INGOT) return "AC01";
/*  547 */       if (mat == Material.IRON_INGOT) return "AC02";
/*      */ 
/*      */     }
/*      */ 
/*  559 */     if (subname.equals("dropper")) return "MM01";
/*  560 */     if (subname.equals("workbench")) return "MM02";
/*  561 */     if (subname.equals("furnace")) return "MM03";
/*  562 */     if (subname.equals("note")) return "MM04";
/*  563 */     if (subname.equals("jukebox")) return "MM05";
/*  564 */     if (subname.equals("firework")) return "ZZFW";
/*  565 */     if (subname.equals("flesh")) return "ZZGW";
/*  566 */     if (subname.equals("sign")) return "ABS1";
/*  567 */     if (subname.equals("sapling")) return "ZZAW";
/*      */ 
/*  570 */     if ((mat == Material.COOKED_CHICKEN) || (mat == Material.RAW_CHICKEN)) return "002)";
/*  571 */     if ((mat == Material.COOKED_FISH) || (mat == Material.RAW_FISH)) return "003)";
/*  572 */     if (useSmallGroups)
/*      */     {
/*  574 */       if ((mat == Material.COOKED_BEEF) || (mat == Material.RAW_BEEF) || (mat == Material.GRILLED_PORK) || (mat == Material.PORK)) return "004)";
/*      */     }
/*      */     else
/*      */     {
/*  578 */       if ((mat == Material.COOKED_BEEF) || (mat == Material.RAW_BEEF)) return "001)";
/*  579 */       if ((mat == Material.GRILLED_PORK) || (mat == Material.PORK)) return "004)";
/*      */     }
/*      */ 
/*  582 */     if (mat == Material.MELON) return "005)";
/*  583 */     if (mat == Material.COOKIE) return "006)";
/*  584 */     if (mat == Material.APPLE) return "007)";
/*  585 */     if (mat == Material.MUSHROOM_SOUP) return "008)";
/*  586 */     if (mat == Material.POTATO_ITEM) return "009)";
/*  587 */     if ((mat == Material.CAKE) || (mat == Material.CAKE_BLOCK)) return "00A)";
/*  588 */     if (mat == Material.PUMPKIN_PIE) return "00B)";
/*  589 */     if ((mat == Material.CARROT_ITEM) || (mat == Material.GOLDEN_CARROT)) return "00C)";
/*  590 */     if (mat == Material.BREAD) return "00D)";
/*  591 */     if ((mat == Material.SUGAR) || (mat == Material.SUGAR_CANE) || (mat == Material.SUGAR_CANE_BLOCK)) return "00E)";
/*  592 */     if (mat == Material.WHEAT) return "00F)";
/*  593 */     if (mat == Material.EGG) return "00G)";
/*  594 */     if (mat == Material.BREAD) return "00H)";
/*  595 */     if (mat == Material.CARROT) return "00I)";
/*      */ 
/*  601 */     if (useSmallGroups)
/*      */     {
/*  603 */       if ((mat == Material.DIAMOND) || (mat == Material.DIAMOND_BLOCK)) return "(L01";
/*  604 */       if ((mat == Material.EMERALD) || (mat == Material.EMERALD_BLOCK)) return "(L02";
/*      */ 
/*      */     }
/*      */ 
/*  608 */     if (mat == Material.DIAMOND_BLOCK) return "ML01";
/*  609 */     if (mat == Material.EMERALD_BLOCK) return "ML02";
/*  610 */     if (mat == Material.IRON_BLOCK) return "ML03";
/*  611 */     if (mat == Material.GLASS) return "ML04";
/*  612 */     if (mat == Material.ICE) return "ML05";
/*  613 */     if (mat == Material.SNOW_BLOCK) return "ML06";
/*  614 */     if (mat == Material.MYCEL) return "ML07";
/*      */ 
/*  616 */     if ((mat == Material.CLAY) || (mat == Material.CLAY_BALL)) return "ML09";
/*      */     try
/*      */     {
/*  619 */       if (mat == Material.STAINED_CLAY) return "ML09";
/*      */     }
/*      */     catch (Throwable localThrowable1)
/*      */     {
/*  623 */       if (mat == Material.DIRT) return "ML0A";
/*  624 */       if (mat == Material.GRAVEL) return "ML0B";
/*  625 */       if (mat == Material.STONE) return "ML0C";
/*  626 */       if (mat == Material.SAND) return "ML0D";
/*  627 */       if (mat == Material.SANDSTONE) return "ML0E";
/*      */ 
/*  629 */       if (mat == Material.FEATHER) return "MQ01";
/*  630 */       if (mat == Material.FLINT) return "MQ02";
/*      */ 
/*  639 */       if ((useSmallGroups) && (mat.toString().contains("RECORD"))) return "RCRD";
/*      */ 
/*      */     }
/*      */ 
/*  643 */     return subname;
/*      */   }
/*      */ 
/*      */   public static int GetFirstEmpty(Inventory inv)
/*      */   {
/*  649 */     int roomSize = inv.getSize();
/*  650 */     int height = roomSize / 9;
/*  651 */     for (int x = 8; x >= 0; x--)
/*      */     {
/*  653 */       for (int y = height - 1; y >= 0; y--)
/*      */       {
/*  655 */         int pos = y * 9 + x;
/*  656 */         if (inv.getItem(pos) == null) return pos;
/*      */       }
/*      */     }
/*      */ 
/*  660 */     return inv.firstEmpty();
/*      */   }
/*      */ 
/*      */   public static void SortChest(Block tgt)
/*      */   {
/*      */     try
/*      */     {
/*  669 */       if (tgt == null) return;
/*      */ 
/*  676 */       Chest tgtChest = (Chest)tgt.getState();
/*      */ 
/*  678 */       if (tgtChest == null) return;
/*      */ 
/*  684 */       ArrayList chests = new ArrayList();
/*  685 */       chests.add(tgtChest);
/*      */ 
/*  713 */       ArrayList newContents = new ArrayList();
/*      */ 
/*  715 */       for (ContainerBlock chest : chests)
/*      */       {
/*  717 */         Inventory inv = chest.getInventory();
/*  718 */         if (inv != null) {
/*  719 */           ItemStack[] items = inv.getContents();
/*  720 */           if (items != null)
/*      */           {
/*  722 */             for (ItemStack is : items)
/*      */             {
/*  724 */               StackAdd(newContents, is);
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */ 
/*  730 */       if (newContents.size() <= 0)
/*      */       {
/*  733 */         return;
/*      */       }
/*      */ 
/*  736 */       ItemGroupCounts = new HashMap();
/*  737 */       useSmallGroups = false;
/*  738 */       for (ItemStack is : newContents)
/*      */       {
/*  740 */         String lastType = GetItemGroupType(is);
/*  741 */         Integer cur = (Integer)ItemGroupCounts.get(lastType);
/*  742 */         if (cur == null) cur = Integer.valueOf(1); else
/*  743 */           cur = Integer.valueOf(cur.intValue() + 1);
/*  744 */         ItemGroupCounts.put(lastType, cur);
/*      */       }
/*      */ 
/*  747 */       Inventory tmpInv = ((ContainerBlock)chests.get(0)).getInventory();
/*  748 */       int roomWidth = 9;
/*  749 */       int roomSize = tmpInv.getSize();
/*  750 */       int nRows = roomSize / 9;
/*      */ 
/*  754 */       if (ItemGroupCounts.size() > nRows)
/*      */       {
/*  756 */         useSmallGroups = true;
/*  757 */         ItemGroupCounts = new HashMap();
/*  758 */         for (ItemStack is : newContents)
/*      */         {
/*  760 */           String lastType = GetItemGroupType(is);
/*  761 */           Integer cur = (Integer)ItemGroupCounts.get(lastType);
/*  762 */           if (cur == null) cur = Integer.valueOf(1); else
/*  763 */             cur = Integer.valueOf(cur.intValue() + 1);
/*  764 */           ItemGroupCounts.put(lastType, cur);
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*  772 */       Collections.sort(newContents, new Comparator()
/*      */       {
/*      */         public int compare(ItemStack o1, ItemStack o2) {
/*  775 */           Long off1 = Long.valueOf(JoeListener.GetOrganizeOrder(o1));
/*  776 */           Long off2 = Long.valueOf(JoeListener.GetOrganizeOrder(o2));
/*  777 */           if (off1.longValue() > off2.longValue()) return 1;
/*  778 */           if (off1.longValue() < off2.longValue()) return -1;
/*  779 */           return 0;
/*      */         }
/*      */       });
/*  791 */       for (ContainerBlock chest : chests)
/*      */       {
/*  793 */         Inventory inv = chest.getInventory();
/*  794 */         if (inv != null) {
/*  795 */           inv.clear();
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*  803 */       Inventory curInv = ((ContainerBlock)chests.get(0)).getInventory();
/*  804 */       int forcePos = 0;
/*      */ 
/*  806 */       int maxGroup = 0;
/*  807 */       int nLinesUsingByRow = 0;
/*      */       Integer amt;
/*  808 */       for (String key : ItemGroupCounts.keySet())
/*      */       {
/*  810 */         amt = (Integer)ItemGroupCounts.get(key);
/*  811 */         if (amt.intValue() > maxGroup) maxGroup = amt.intValue();
/*  812 */         nLinesUsingByRow++;
/*  813 */         nLinesUsingByRow += (amt.intValue() - 1) / 9;
/*      */       }
/*      */ 
/*  818 */       if (nLinesUsingByRow <= nRows)
/*      */       {
/*  822 */         String lastType = GetItemGroupType((ItemStack)newContents.get(0));
/*  823 */         for (ItemStack is : newContents)
/*      */         {
/*  825 */           int idxEmpty = 99999;
/*  826 */           String thisGroupType = GetItemGroupType(is);
/*  827 */           if (lastType.equals(thisGroupType))
/*      */           {
/*  829 */             idxEmpty = forcePos;
/*  830 */             forcePos++;
/*      */           }
/*      */           else
/*      */           {
/*  834 */             lastType = thisGroupType;
/*  835 */             if (forcePos % roomWidth != 0)
/*      */             {
/*  837 */               forcePos -= forcePos % roomWidth;
/*  838 */               forcePos += roomWidth;
/*      */             }
/*  840 */             idxEmpty = forcePos;
/*  841 */             forcePos++;
/*      */           }
/*  843 */           curInv.setItem(idxEmpty, is);
/*      */         }
/*  845 */         return;
/*      */       }
/*      */ 
/*  851 */       boolean useFullRow = false;
/*  852 */       if (ItemGroupCounts.size() == 1) useFullRow = true;
/*      */ 
/*  855 */       if (maxGroup <= 1) useFullRow = true;
/*      */ 
/*  858 */       if (useFullRow)
/*      */       {
/*  860 */         for (ItemStack is : newContents)
/*      */         {
/*  862 */           curInv.addItem(new ItemStack[] { is });
/*      */         }
/*  864 */         return;
/*      */       }
/*      */ 
/*  870 */       String lastType = GetItemGroupType((ItemStack)newContents.get(0));
/*  871 */       int splitPos = ((Integer)ItemGroupCounts.get(lastType)).intValue() + 1;
/*  872 */       if (splitPos > 5) splitPos = 5;
/*      */ 
/*  875 */       int forcePass = 1;
/*  876 */       for (ItemStack is : newContents)
/*      */       {
/*  878 */         int idxEmpty = 99999;
/*  879 */         String thisGroupType = GetItemGroupType(is);
/*  880 */         if (forcePass < 3)
/*      */         {
/*  883 */           if (forcePass == 1)
/*      */           {
/*  885 */             int nThisType = ((Integer)ItemGroupCounts.get(thisGroupType)).intValue();
/*  886 */             if (nThisType <= 1)
/*      */             {
/*  889 */               idxEmpty = 9999;
/*      */             }
/*  895 */             else if ((lastType.equals(thisGroupType)) && (forcePos % roomWidth < 5) && (forcePos < roomSize))
/*      */             {
/*  897 */               idxEmpty = forcePos;
/*  898 */               forcePos++;
/*      */             }
/*      */             else
/*      */             {
/*  902 */               lastType = thisGroupType;
/*      */ 
/*  904 */               forcePos -= forcePos % roomWidth;
/*  905 */               forcePos += roomWidth;
/*      */ 
/*  918 */               idxEmpty = forcePos;
/*  919 */               forcePos++;
/*      */             }
/*      */ 
/*      */           }
/*      */           else
/*      */           {
/*  927 */             int nThisType = ((Integer)ItemGroupCounts.get(thisGroupType)).intValue();
/*  928 */             if (nThisType <= 1)
/*      */             {
/*  931 */               idxEmpty = 9999;
/*      */             }
/*      */             else
/*      */             {
/*  935 */               int segIdx = forcePos % roomWidth;
/*  936 */               if ((lastType.equals(thisGroupType)) && (segIdx >= splitPos) && (segIdx < 8) && (forcePos < roomSize))
/*      */               {
/*  938 */                 idxEmpty = forcePos;
/*  939 */                 forcePos++;
/*      */               }
/*      */               else
/*      */               {
/*  943 */                 lastType = thisGroupType;
/*  944 */                 if (forcePos % roomWidth == 0) forcePos--;
/*  945 */                 if (forcePos % roomWidth != splitPos)
/*      */                 {
/*  947 */                   forcePos -= forcePos % roomWidth;
/*  948 */                   forcePos += roomWidth + splitPos;
/*      */                 }
/*  950 */                 idxEmpty = forcePos;
/*  951 */                 forcePos++;
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */ 
/*  957 */         if ((forcePass == 1) && (idxEmpty >= roomSize))
/*      */         {
/*  959 */           lastType = thisGroupType;
/*      */ 
/*  962 */           idxEmpty = splitPos;
/*  963 */           forcePos = splitPos + 1;
/*  964 */           forcePass = 2;
/*      */ 
/*  966 */           int nThisType = ((Integer)ItemGroupCounts.get(thisGroupType)).intValue();
/*  967 */           if (nThisType <= 1) idxEmpty = 9999;
/*      */ 
/*      */         }
/*      */ 
/*  971 */         if ((idxEmpty < 0) || (idxEmpty >= roomSize))
/*      */         {
/*  973 */           forcePass = 3;
/*      */ 
/*  975 */           idxEmpty = GetFirstEmpty(curInv);
/*      */         }
/*      */ 
/*  992 */         curInv.setItem(idxEmpty, is);
/*      */       }
/*      */ 
/*      */     }
/*      */     catch (Throwable exc)
/*      */     {
/*  999 */       JoeUtils.ReportError(exc, "Gracefully handled but please report so Joe will fix this annoying message!");
/* 1000 */       if (tgt != null) JoeUtils.ConsoleMsg(String.format("Error Details: BlockType=%s, Location=%s, Details=%s", new Object[] { tgt.getType(), JoeUtils.LocString(tgt.getLocation()), exc.toString() }));
/*      */     }
/*      */   }
/*      */ 
/*      */   public static boolean ItemsAreSame(ItemStack i1, ItemStack i2)
/*      */   {
/* 1008 */     if ((i1 == null) && (i2 == null)) return true;
/* 1009 */     if (i1 == null) return false;
/* 1010 */     if (i2 == null) return false;
/*      */ 
/* 1013 */     if (i1.getType() != i2.getType()) return false;
/* 1014 */     if (i1.getDurability() != i2.getDurability()) return false;
/*      */ 
/* 1017 */     int i1Amt = i1.getAmount();
/* 1018 */     i1.setAmount(1);
/* 1019 */     int i2Amt = i2.getAmount();
/* 1020 */     i2.setAmount(1);
/* 1021 */     boolean res = i1.toString().equals(i2.toString());
/* 1022 */     i1.setAmount(i1Amt);
/* 1023 */     i2.setAmount(i2Amt);
/* 1024 */     return res;
/*      */   }
/*      */ 
/*      */   public static void StackAdd(ArrayList<ItemStack> stack, ItemStack item)
/*      */   {
/* 1115 */     if (item == null) return;
/* 1116 */     Material mat = item.getType();
/* 1117 */     if (mat == Material.AIR) return;
/*      */ 
/* 1119 */     int maxStackSize = item.getMaxStackSize();
/* 1120 */     if (maxStackSize <= 0) maxStackSize = 1;
/*      */ 
/* 1123 */     if (maxStackSize < item.getAmount()) maxStackSize = item.getAmount();
/*      */ 
/* 1125 */     int remaining = item.getAmount();
/* 1126 */     if (remaining <= 0) return;
/* 1127 */     for (int idx = 0; idx < stack.size(); idx++)
/*      */     {
/* 1129 */       ItemStack is = (ItemStack)stack.get(idx);
/* 1130 */       if (is.getType() == mat)
/*      */       {
/* 1132 */         if (ItemsAreSame(is, item))
/*      */         {
/* 1134 */           int curAmt = is.getAmount();
/* 1135 */           if (curAmt > 0)
/*      */           {
/* 1137 */             if (curAmt < maxStackSize)
/*      */             {
/* 1139 */               int possibleToAdd = maxStackSize - curAmt;
/*      */ 
/* 1141 */               if (remaining < possibleToAdd)
/*      */               {
/* 1143 */                 is.setAmount(curAmt + remaining);
/* 1144 */                 return;
/*      */               }
/* 1146 */               is.setAmount(maxStackSize);
/* 1147 */               remaining -= possibleToAdd;
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/* 1151 */     while (remaining > 0)
/*      */     {
/* 1153 */       if (remaining <= maxStackSize)
/*      */       {
/* 1155 */         ItemStack newItem = item.clone();
/* 1156 */         newItem.setAmount(remaining);
/* 1157 */         stack.add(newItem);
/* 1158 */         return;
/*      */       }
/* 1160 */       ItemStack newItem = item.clone();
/* 1161 */       newItem.setAmount(maxStackSize);
/* 1162 */       stack.add(newItem);
/* 1163 */       remaining -= maxStackSize;
/*      */     }
/*      */   }
/*      */ }

/* Location:           D:\Github\Mechanics\JoeInventorySort.jar
 * Qualified Name:     joe.crump.isort.JoeListener
 * JD-Core Version:    0.6.2
 */