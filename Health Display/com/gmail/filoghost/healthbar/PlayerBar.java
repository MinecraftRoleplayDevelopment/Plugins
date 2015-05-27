/*     */ package com.gmail.filoghost.healthbar;
/*     */ 
/*     */ import com.gmail.filoghost.healthbar.api.BarHideEvent;
/*     */ import com.gmail.filoghost.healthbar.utils.PlayerBarUtils;
/*     */ import com.gmail.filoghost.healthbar.utils.Utils;
/*     */ import org.bukkit.OfflinePlayer;
/*     */ import org.bukkit.Server;
/*     */ import org.bukkit.configuration.file.FileConfiguration;
/*     */ import org.bukkit.entity.Player;
/*     */ import org.bukkit.plugin.Plugin;
/*     */ import org.bukkit.plugin.PluginManager;
/*     */ import org.bukkit.scoreboard.DisplaySlot;
/*     */ import org.bukkit.scoreboard.Objective;
/*     */ import org.bukkit.scoreboard.Score;
/*     */ import org.bukkit.scoreboard.Scoreboard;
/*     */ import org.bukkit.scoreboard.ScoreboardManager;
/*     */ import org.bukkit.scoreboard.Team;
/*     */ 
/*     */ public class PlayerBar
/*     */ {
/*  17 */   private static final Plugin instance = Main.plugin;
/*  18 */   private static Scoreboard sb = instance.getServer().getScoreboardManager().getMainScoreboard();
/*     */   private static boolean playerEnabled;
/*     */   private static boolean textMode;
/*     */   private static boolean useBelow;
/*     */   private static boolean belowUseProportion;
/*     */   private static int belowNameProportion;
/*     */   private static boolean belowUseRawAmountOfHearts;
/*     */   private static Objective belowObj;
/*     */   private static boolean useCustomBar;
/*     */ 
/*     */   public static void setupBelow()
/*     */   {
/*  36 */     removeBelowObj();
/*     */ 
/*  38 */     if ((playerEnabled) && (useBelow))
/*     */     {
/*  40 */       belowObj = sb.registerNewObjective("healthbarbelow", "dummy");
/*  41 */       belowObj.setDisplayName(Utils.replaceSymbols(instance.getConfig().getString(Configuration.Nodes.PLAYERS_BELOW_TEXT.getNode())));
/*  42 */       belowObj.setDisplaySlot(DisplaySlot.BELOW_NAME);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static void removeBelowObj()
/*     */   {
/*  48 */     if (sb.getObjective(DisplaySlot.BELOW_NAME) != null)
/*  49 */       sb.getObjective(DisplaySlot.BELOW_NAME).unregister();
/*  50 */     if (sb.getObjective("healthbarbelow") != null)
/*  51 */       sb.getObjective("healthbarbelow").unregister();
/*     */   }
/*     */ 
/*     */   public static boolean hasHealthDisplayed(Player player) {
/*  55 */     Team team = sb.getPlayerTeam(player);
/*  56 */     if (team == null) {
/*  57 */       return false;
/*     */     }
/*  59 */     if (sb.getPlayerTeam(player).getName().contains("hbr")) return true;
/*  60 */     return false;
/*     */   }
/*     */ 
/*     */   public static void hideHealthBar(Player player) {
/*  64 */     Team team = sb.getTeam("hbr0");
/*  65 */     if (team == null) {
/*  66 */       team = sb.registerNewTeam("hbr0");
/*  67 */       team.setCanSeeFriendlyInvisibles(false);
/*     */     }
/*  69 */     OfflinePlayer offPlayer = player;
/*  70 */     team.addPlayer(offPlayer);
/*     */ 
/*  73 */     instance.getServer().getPluginManager().callEvent(new BarHideEvent(offPlayer));
/*     */   }
/*     */ 
/*     */   public static void updateHealthBelow(Player player) {
/*  77 */     if ((useBelow) && (playerEnabled)) {
/*  78 */       int score = 0;
/*     */ 
/*  81 */       if (belowUseRawAmountOfHearts)
/*  82 */         score = getRawAmountOfHearts(player);
/*  83 */       else if (belowUseProportion)
/*  84 */         score = Utils.roundUpPositive(player.getHealth() * belowNameProportion / player.getMaxHealth());
/*     */       else {
/*  86 */         score = Utils.roundUpPositive(player.getHealth());
/*     */       }
/*     */ 
/*  89 */       belowObj.getScore(player).setScore(score);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static void setHealthSuffix(Player player, double health, double max)
/*     */   {
/*  95 */     OfflinePlayer op = player;
/*     */ 
/*  97 */     if ((useCustomBar) || (!textMode)) {
/*  98 */       int healthOn10 = Utils.roundUpPositiveWithMax(health * 10.0D / max, 10);
/*  99 */       sb.getTeam("hbr" + Integer.toString(healthOn10)).addPlayer(op);
/* 100 */       return;
/*     */     }
/*     */ 
/* 103 */     int intHealth = Utils.roundUpPositive(health);
/* 104 */     int intMax = Utils.roundUpPositive(max);
/*     */ 
/* 106 */     String color = getColor(health, max);
/* 107 */     Team team = sb.getTeam("hbr" + intHealth + "-" + intMax);
/* 108 */     if (team == null) {
/* 109 */       team = sb.registerNewTeam("hbr" + intHealth + "-" + intMax);
/* 110 */       team.setSuffix(" - " + color + intHealth + "§7/§a" + intMax);
/* 111 */       team.setCanSeeFriendlyInvisibles(false);
/*     */     }
/* 113 */     team.addPlayer(op);
/*     */   }
/*     */ 
/*     */   public static String getColor(double health, double max)
/*     */   {
/* 119 */     double ratio = health / max;
/* 120 */     if (ratio > 0.5D) return "§a";
/* 121 */     if (ratio > 0.25D) return "§e";
/* 122 */     return "§c";
/*     */   }
/*     */ 
/*     */   public static void loadConfiguration()
/*     */   {
/* 128 */     sb = instance.getServer().getScoreboardManager().getMainScoreboard();
/* 129 */     PlayerBarUtils.removeAllHealthbarTeams(sb);
/*     */ 
/* 131 */     FileConfiguration config = instance.getConfig();
/*     */ 
/* 133 */     playerEnabled = config.getBoolean(Configuration.Nodes.PLAYERS_ENABLE.getNode());
/* 134 */     textMode = config.getBoolean(Configuration.Nodes.PLAYERS_AFTER_TEXT_MODE.getNode());
/* 135 */     useCustomBar = config.getBoolean(Configuration.Nodes.PLAYERS_AFTER_USE_CUSTOM.getNode());
/* 136 */     useBelow = config.getBoolean(Configuration.Nodes.PLAYERS_BELOW_ENABLE.getNode());
/* 137 */     belowUseProportion = config.getBoolean(Configuration.Nodes.PLAYERS_BELOW_USE_PROPORTION.getNode());
/* 138 */     belowNameProportion = config.getInt(Configuration.Nodes.PLAYERS_BELOW_PROPORTIONAL_TO.getNode());
/* 139 */     belowUseRawAmountOfHearts = config.getBoolean(Configuration.Nodes.PLAYERS_BELOW_DISPLAY_RAW_HEARTS.getNode());
/*     */ 
/* 141 */     setupBelow();
/*     */ 
/* 143 */     if (useCustomBar)
/* 144 */       PlayerBarUtils.create10CustomTeams(sb, Utils.loadFile("custom-player-bar.yml", instance));
/* 145 */     else if (!textMode) {
/* 146 */       PlayerBarUtils.create10DefaultTeams(sb, config.getInt(Configuration.Nodes.PLAYERS_AFTER_STYLE.getNode()));
/*     */     }
/*     */ 
/* 150 */     PlayerBarUtils.setAllTeamsInvisibility(sb);
/*     */   }
/*     */ 
/*     */   public static int getRawAmountOfHearts(Player player) {
/* 154 */     if (player.isHealthScaled()) {
/* 155 */       return Utils.round(player.getHealth() * 10.0D / player.getMaxHealth());
/*     */     }
/* 157 */     return Utils.round(player.getHealth() / 2.0D);
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\HealthBar.jar
 * Qualified Name:     com.gmail.filoghost.healthbar.PlayerBar
 * JD-Core Version:    0.6.2
 */