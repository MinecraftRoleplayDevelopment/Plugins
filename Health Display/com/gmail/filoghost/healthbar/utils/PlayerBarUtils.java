/*     */ package com.gmail.filoghost.healthbar.utils;
/*     */ 
/*     */ import java.util.Set;
/*     */ import org.bukkit.configuration.file.FileConfiguration;
/*     */ import org.bukkit.scoreboard.Scoreboard;
/*     */ import org.bukkit.scoreboard.Team;
/*     */ 
/*     */ public class PlayerBarUtils
/*     */ {
/*     */   public static void create10DefaultTeams(Scoreboard sb, int style)
/*     */   {
/*  16 */     if (style == 2) {
/*  17 */       sb.registerNewTeam("hbr1").setSuffix(" §c▌");
/*  18 */       sb.registerNewTeam("hbr2").setSuffix(" §c█");
/*  19 */       sb.registerNewTeam("hbr3").setSuffix(" §e█▌");
/*  20 */       sb.registerNewTeam("hbr4").setSuffix(" §e██");
/*  21 */       sb.registerNewTeam("hbr5").setSuffix(" §e██▌");
/*  22 */       sb.registerNewTeam("hbr6").setSuffix(" §a███");
/*  23 */       sb.registerNewTeam("hbr7").setSuffix(" §a███▌");
/*  24 */       sb.registerNewTeam("hbr8").setSuffix(" §a████");
/*  25 */       sb.registerNewTeam("hbr9").setSuffix(" §a████▌");
/*  26 */       sb.registerNewTeam("hbr10").setSuffix(" §a█████");
/*  27 */       return;
/*  28 */     }if (style == 3) {
/*  29 */       sb.registerNewTeam("hbr1").setSuffix(" §cI§8IIIIIIIII");
/*  30 */       sb.registerNewTeam("hbr2").setSuffix(" §cII§8IIIIIIII");
/*  31 */       sb.registerNewTeam("hbr3").setSuffix(" §eIII§8IIIIIII");
/*  32 */       sb.registerNewTeam("hbr4").setSuffix(" §eIIII§8IIIIII");
/*  33 */       sb.registerNewTeam("hbr5").setSuffix(" §eIIIII§8IIIII");
/*  34 */       sb.registerNewTeam("hbr6").setSuffix(" §aIIIIII§8IIII");
/*  35 */       sb.registerNewTeam("hbr7").setSuffix(" §aIIIIIII§8III");
/*  36 */       sb.registerNewTeam("hbr8").setSuffix(" §aIIIIIIII§8II");
/*  37 */       sb.registerNewTeam("hbr9").setSuffix(" §aIIIIIIIII§8I");
/*  38 */       sb.registerNewTeam("hbr10").setSuffix(" §aIIIIIIIIII");
/*  39 */       return;
/*  40 */     }if (style == 4) {
/*  41 */       sb.registerNewTeam("hbr1").setSuffix(" §c1❤");
/*  42 */       sb.registerNewTeam("hbr2").setSuffix(" §c2❤");
/*  43 */       sb.registerNewTeam("hbr3").setSuffix(" §e3❤");
/*  44 */       sb.registerNewTeam("hbr4").setSuffix(" §e4❤");
/*  45 */       sb.registerNewTeam("hbr5").setSuffix(" §e5❤");
/*  46 */       sb.registerNewTeam("hbr6").setSuffix(" §a6❤");
/*  47 */       sb.registerNewTeam("hbr7").setSuffix(" §a7❤");
/*  48 */       sb.registerNewTeam("hbr8").setSuffix(" §a8❤");
/*  49 */       sb.registerNewTeam("hbr9").setSuffix(" §a9❤");
/*  50 */       sb.registerNewTeam("hbr10").setSuffix(" §a10❤");
/*  51 */       return;
/*  52 */     }if (style == 5) {
/*  53 */       sb.registerNewTeam("hbr1").setSuffix(" §c♦§7♦♦♦♦ ");
/*  54 */       sb.registerNewTeam("hbr2").setSuffix(" §c♦§7♦♦♦♦ ");
/*  55 */       sb.registerNewTeam("hbr3").setSuffix(" §e♦♦§7♦♦♦ ");
/*  56 */       sb.registerNewTeam("hbr4").setSuffix(" §e♦♦§7♦♦♦ ");
/*  57 */       sb.registerNewTeam("hbr5").setSuffix(" §a♦♦♦§7♦♦ ");
/*  58 */       sb.registerNewTeam("hbr6").setSuffix(" §a♦♦♦§7♦♦ ");
/*  59 */       sb.registerNewTeam("hbr7").setSuffix(" §a♦♦♦♦§7♦ ");
/*  60 */       sb.registerNewTeam("hbr8").setSuffix(" §a♦♦♦♦§7♦ ");
/*  61 */       sb.registerNewTeam("hbr9").setSuffix(" §a♦♦♦♦♦ ");
/*  62 */       sb.registerNewTeam("hbr10").setSuffix(" §a♦♦♦♦♦ ");
/*  63 */       return;
/*  64 */     }if (style == 6) {
/*  65 */       sb.registerNewTeam("hbr1").setSuffix(" §c❤§7❤❤❤❤");
/*  66 */       sb.registerNewTeam("hbr2").setSuffix(" §c❤§7❤❤❤❤");
/*  67 */       sb.registerNewTeam("hbr3").setSuffix(" §c❤❤§7❤❤❤");
/*  68 */       sb.registerNewTeam("hbr4").setSuffix(" §c❤❤§7❤❤❤");
/*  69 */       sb.registerNewTeam("hbr5").setSuffix(" §c❤❤❤§7❤❤");
/*  70 */       sb.registerNewTeam("hbr6").setSuffix(" §c❤❤❤§7❤❤");
/*  71 */       sb.registerNewTeam("hbr7").setSuffix(" §c❤❤❤❤§7❤");
/*  72 */       sb.registerNewTeam("hbr8").setSuffix(" §c❤❤❤❤§7❤");
/*  73 */       sb.registerNewTeam("hbr9").setSuffix(" §c❤❤❤❤❤");
/*  74 */       sb.registerNewTeam("hbr10").setSuffix(" §c❤❤❤❤❤");
/*  75 */       return;
/*  76 */     }if (style == 7) {
/*  77 */       sb.registerNewTeam("hbr1").setSuffix(" §c▌§8▌▌▌▌▌▌▌▌▌");
/*  78 */       sb.registerNewTeam("hbr2").setSuffix(" §c▌▌§8▌▌▌▌▌▌▌▌");
/*  79 */       sb.registerNewTeam("hbr3").setSuffix(" §e▌▌▌§8▌▌▌▌▌▌▌");
/*  80 */       sb.registerNewTeam("hbr4").setSuffix(" §e▌▌▌▌§8▌▌▌▌▌▌");
/*  81 */       sb.registerNewTeam("hbr5").setSuffix(" §e▌▌▌▌▌§8▌▌▌▌▌");
/*  82 */       sb.registerNewTeam("hbr6").setSuffix(" §a▌▌▌▌▌▌§8▌▌▌▌");
/*  83 */       sb.registerNewTeam("hbr7").setSuffix(" §a▌▌▌▌▌▌▌§8▌▌▌");
/*  84 */       sb.registerNewTeam("hbr8").setSuffix(" §a▌▌▌▌▌▌▌▌§8▌▌");
/*  85 */       sb.registerNewTeam("hbr9").setSuffix(" §a▌▌▌▌▌▌▌▌▌§8▌");
/*  86 */       sb.registerNewTeam("hbr10").setSuffix(" §a▌▌▌▌▌▌▌▌▌▌");
/*  87 */       return;
/*     */     }
/*     */ 
/*  90 */     sb.registerNewTeam("hbr1").setSuffix(" §c|§8|||||||||");
/*  91 */     sb.registerNewTeam("hbr2").setSuffix(" §c||§8||||||||");
/*  92 */     sb.registerNewTeam("hbr3").setSuffix(" §e|||§8|||||||");
/*  93 */     sb.registerNewTeam("hbr4").setSuffix(" §e||||§8||||||");
/*  94 */     sb.registerNewTeam("hbr5").setSuffix(" §e|||||§8|||||");
/*  95 */     sb.registerNewTeam("hbr6").setSuffix(" §a||||||§8||||");
/*  96 */     sb.registerNewTeam("hbr7").setSuffix(" §a|||||||§8|||");
/*  97 */     sb.registerNewTeam("hbr8").setSuffix(" §a||||||||§8||");
/*  98 */     sb.registerNewTeam("hbr9").setSuffix(" §a|||||||||§8|");
/*  99 */     sb.registerNewTeam("hbr10").setSuffix(" §a||||||||||");
/*     */   }
/*     */ 
/*     */   public static void create10CustomTeams(Scoreboard sb, FileConfiguration c)
/*     */   {
/* 108 */     for (int i = 1; i < 11; i++)
/*     */       try
/*     */       {
/* 111 */         Team t = sb.registerNewTeam("hbr" + i);
/* 112 */         if (!c.isSet(i + "0" + "-percent.prefix")) {
/* 113 */           c.set(i + "0" + "-percent.prefix", "");
/*     */         }
/* 115 */         if (!c.isSet(i + "0" + "-percent.suffix")) {
/* 116 */           c.set(i + "0" + "-percent.suffix", "");
/*     */         }
/* 118 */         String prefix = c.getString(i + "0" + "-percent.prefix");
/* 119 */         String suffix = c.getString(i + "0" + "-percent.suffix");
/*     */ 
/* 121 */         if ((prefix != null) && (!prefix.equals("")))
/* 122 */           t.setPrefix(Utils.replaceSymbols(prefix));
/* 123 */         if ((suffix != null) && (!suffix.equals("")))
/* 124 */           t.setSuffix(Utils.replaceSymbols(suffix));
/*     */       }
/*     */       catch (Exception e) {
/* 127 */         e.printStackTrace();
/*     */       }
/*     */   }
/*     */ 
/*     */   public static void setAllTeamsInvisibility(Scoreboard sb)
/*     */   {
/* 138 */     Set teamList = sb.getTeams();
/* 139 */     for (Team team : teamList)
/*     */     {
/* 141 */       if (team.getName().contains("hbr"))
/* 142 */         team.setCanSeeFriendlyInvisibles(false);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static void removeAllHealthbarTeams(Scoreboard sb)
/*     */   {
/* 152 */     Set teamList = sb.getTeams();
/* 153 */     for (Team team : teamList)
/* 154 */       if (team.getName().contains("hbr"))
/* 155 */         team.unregister();
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\HealthBar.jar
 * Qualified Name:     com.gmail.filoghost.healthbar.utils.PlayerBarUtils
 * JD-Core Version:    0.6.2
 */