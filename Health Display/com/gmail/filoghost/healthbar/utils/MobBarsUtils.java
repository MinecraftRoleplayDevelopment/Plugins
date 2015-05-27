/*     */ package com.gmail.filoghost.healthbar.utils;
/*     */ 
/*     */ import org.bukkit.configuration.file.FileConfiguration;
/*     */ 
/*     */ public class MobBarsUtils
/*     */ {
/*     */   public static String[] getDefaultsBars(FileConfiguration config)
/*     */   {
/*  15 */     String[] barArray = new String[21];
/*     */ 
/*  17 */     int mobBarStyle = config.getInt("mob-bars.display-style");
/*     */ 
/*  19 */     if (mobBarStyle == 2)
/*     */     {
/*  21 */       barArray[0] = "§c|§7|||||||||||||||||||"; barArray[1] = "§c|§7|||||||||||||||||||";
/*  22 */       barArray[2] = "§c||§7||||||||||||||||||"; barArray[3] = "§c|||§7|||||||||||||||||";
/*  23 */       barArray[4] = "§c||||§7||||||||||||||||"; barArray[5] = "§e|||||§7|||||||||||||||";
/*  24 */       barArray[6] = "§e||||||§7||||||||||||||"; barArray[7] = "§e|||||||§7|||||||||||||";
/*  25 */       barArray[8] = "§e||||||||§7||||||||||||"; barArray[9] = "§e|||||||||§7|||||||||||";
/*  26 */       barArray[10] = "§e||||||||||§7||||||||||"; barArray[11] = "§a|||||||||||§7|||||||||";
/*  27 */       barArray[12] = "§a||||||||||||§7||||||||"; barArray[13] = "§a|||||||||||||§7|||||||";
/*  28 */       barArray[14] = "§a||||||||||||||§7||||||"; barArray[15] = "§a|||||||||||||||§7|||||";
/*  29 */       barArray[16] = "§a||||||||||||||||§7||||"; barArray[17] = "§a|||||||||||||||||§7|||";
/*  30 */       barArray[18] = "§a||||||||||||||||||§7||"; barArray[19] = "§a|||||||||||||||||||§7|";
/*  31 */       barArray[20] = "§a||||||||||||||||||||";
/*     */     }
/*  33 */     else if (mobBarStyle == 3)
/*     */     {
/*  35 */       barArray[0] = "§c❤§7❤❤❤❤❤❤❤❤❤"; barArray[1] = "§c❤§7❤❤❤❤❤❤❤❤❤";
/*  36 */       barArray[2] = "§c❤§7❤❤❤❤❤❤❤❤❤"; barArray[3] = "§c❤❤§7❤❤❤❤❤❤❤❤";
/*  37 */       barArray[4] = "§c❤❤§7❤❤❤❤❤❤❤❤"; barArray[5] = "§e❤❤❤§7❤❤❤❤❤❤❤";
/*  38 */       barArray[6] = "§e❤❤❤§7❤❤❤❤❤❤❤"; barArray[7] = "§e❤❤❤❤§7❤❤❤❤❤❤";
/*  39 */       barArray[8] = "§e❤❤❤❤§7❤❤❤❤❤❤"; barArray[9] = "§e❤❤❤❤❤§7❤❤❤❤❤";
/*  40 */       barArray[10] = "§e❤❤❤❤❤§7❤❤❤❤❤"; barArray[11] = "§a❤❤❤❤❤❤§7❤❤❤❤";
/*  41 */       barArray[12] = "§a❤❤❤❤❤❤§7❤❤❤❤"; barArray[13] = "§a❤❤❤❤❤❤❤§7❤❤❤";
/*  42 */       barArray[14] = "§a❤❤❤❤❤❤❤§7❤❤❤"; barArray[15] = "§a❤❤❤❤❤❤❤❤§7❤❤";
/*  43 */       barArray[16] = "§a❤❤❤❤❤❤❤❤§7❤❤"; barArray[17] = "§a❤❤❤❤❤❤❤❤❤§7❤";
/*  44 */       barArray[18] = "§a❤❤❤❤❤❤❤❤❤§7❤"; barArray[19] = "§a❤❤❤❤❤❤❤❤❤❤";
/*  45 */       barArray[20] = "§a❤❤❤❤❤❤❤❤❤❤";
/*     */     }
/*  47 */     else if (mobBarStyle == 4)
/*     */     {
/*  49 */       barArray[0] = "§a▌§8▌▌▌▌▌▌▌▌▌▌▌▌▌▌▌▌▌▌▌"; barArray[1] = "§a▌§8▌▌▌▌▌▌▌▌▌▌▌▌▌▌▌▌▌▌▌";
/*  50 */       barArray[2] = "§a▌▌§8▌▌▌▌▌▌▌▌▌▌▌▌▌▌▌▌▌▌"; barArray[3] = "§a▌▌▌§8▌▌▌▌▌▌▌▌▌▌▌▌▌▌▌▌▌";
/*  51 */       barArray[4] = "§a▌▌▌▌§8▌▌▌▌▌▌▌▌▌▌▌▌▌▌▌▌"; barArray[5] = "§a▌▌▌▌▌§8▌▌▌▌▌▌▌▌▌▌▌▌▌▌▌";
/*  52 */       barArray[6] = "§a▌▌▌▌▌▌§8▌▌▌▌▌▌▌▌▌▌▌▌▌▌"; barArray[7] = "§a▌▌▌▌▌▌▌§8▌▌▌▌▌▌▌▌▌▌▌▌▌";
/*  53 */       barArray[8] = "§a▌▌▌▌▌▌▌▌§8▌▌▌▌▌▌▌▌▌▌▌▌"; barArray[9] = "§a▌▌▌▌▌▌▌▌▌§8▌▌▌▌▌▌▌▌▌▌▌";
/*  54 */       barArray[10] = "§a▌▌▌▌▌▌▌▌▌▌§8▌▌▌▌▌▌▌▌▌▌"; barArray[11] = "§a▌▌▌▌▌▌▌▌▌▌▌§8▌▌▌▌▌▌▌▌▌";
/*  55 */       barArray[12] = "§a▌▌▌▌▌▌▌▌▌▌▌▌§8▌▌▌▌▌▌▌▌"; barArray[13] = "§a▌▌▌▌▌▌▌▌▌▌▌▌▌§8▌▌▌▌▌▌▌";
/*  56 */       barArray[14] = "§a▌▌▌▌▌▌▌▌▌▌▌▌▌▌§8▌▌▌▌▌▌"; barArray[15] = "§a▌▌▌▌▌▌▌▌▌▌▌▌▌▌▌§8▌▌▌▌▌";
/*  57 */       barArray[16] = "§a▌▌▌▌▌▌▌▌▌▌▌▌▌▌▌▌§8▌▌▌▌"; barArray[17] = "§a▌▌▌▌▌▌▌▌▌▌▌▌▌▌▌▌▌§8▌▌▌";
/*  58 */       barArray[18] = "§a▌▌▌▌▌▌▌▌▌▌▌▌▌▌▌▌▌▌§8▌▌"; barArray[19] = "§a▌▌▌▌▌▌▌▌▌▌▌▌▌▌▌▌▌▌▌§8▌";
/*  59 */       barArray[20] = "§a▌▌▌▌▌▌▌▌▌▌▌▌▌▌▌▌▌▌▌▌";
/*     */     }
/*  61 */     else if (mobBarStyle == 5)
/*     */     {
/*  63 */       barArray[0] = "§c█§0█████████"; barArray[1] = "§c█§0█████████";
/*  64 */       barArray[2] = "§c█§0█████████"; barArray[3] = "§c██§0████████";
/*  65 */       barArray[4] = "§c██§0████████"; barArray[5] = "§e███§0███████";
/*  66 */       barArray[6] = "§e███§0███████"; barArray[7] = "§e████§0██████";
/*  67 */       barArray[8] = "§e████§0██████"; barArray[9] = "§e█████§0█████";
/*  68 */       barArray[10] = "§e█████§0█████"; barArray[11] = "§a██████§0████";
/*  69 */       barArray[12] = "§a██████§0████"; barArray[13] = "§a███████§0███";
/*  70 */       barArray[14] = "§a███████§0███"; barArray[15] = "§a████████§0██";
/*  71 */       barArray[16] = "§a████████§0██"; barArray[17] = "§a█████████§0█";
/*  72 */       barArray[18] = "§a█████████§0█"; barArray[19] = "§a██████████";
/*  73 */       barArray[20] = "§a██████████";
/*     */     }
/*     */     else
/*     */     {
/*  78 */       barArray[0] = "§c▌                   "; barArray[1] = "§c▌                   ";
/*  79 */       barArray[2] = "§c█                  "; barArray[3] = "§c█▌                 ";
/*  80 */       barArray[4] = "§c██                "; barArray[5] = "§e██▌               ";
/*  81 */       barArray[6] = "§e███              "; barArray[7] = "§e███▌             ";
/*  82 */       barArray[8] = "§e████            "; barArray[9] = "§e████▌           ";
/*  83 */       barArray[10] = "§e█████          "; barArray[11] = "§a█████▌         ";
/*  84 */       barArray[12] = "§a██████        "; barArray[13] = "§a██████▌       ";
/*  85 */       barArray[14] = "§a███████      "; barArray[15] = "§a███████▌     ";
/*  86 */       barArray[16] = "§a████████    "; barArray[17] = "§a████████▌   ";
/*  87 */       barArray[18] = "§a█████████  "; barArray[19] = "§a█████████▌ ";
/*  88 */       barArray[20] = "§a██████████";
/*     */     }
/*     */ 
/*  91 */     return barArray;
/*     */   }
/*     */ 
/*     */   public static String[] getCustomBars(FileConfiguration config)
/*     */   {
/* 100 */     String[] barArray = new String[21];
/*     */ 
/* 102 */     barArray[0] = "";
/*     */ 
/* 104 */     for (int i = 1; i < 21; i++)
/*     */     {
/* 106 */       barArray[i] = "";
/*     */       try
/*     */       {
/* 110 */         String cname = config.getString(i * 5 + "-percent-bar");
/*     */ 
/* 112 */         if (cname == null) cname = "";
/*     */ 
/* 114 */         barArray[i] = Utils.replaceSymbols(cname);
/*     */       }
/*     */       catch (Exception e) {
/* 117 */         e.printStackTrace();
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 122 */     return barArray;
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\HealthBar.jar
 * Qualified Name:     com.gmail.filoghost.healthbar.utils.MobBarsUtils
 * JD-Core Version:    0.6.2
 */