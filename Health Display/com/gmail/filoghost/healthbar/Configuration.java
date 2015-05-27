/*    */ package com.gmail.filoghost.healthbar;
/*    */ 
/*    */ import org.bukkit.configuration.file.FileConfiguration;
/*    */ 
/*    */ public class Configuration
/*    */ {
/*    */   public static void checkConfigYML()
/*    */   {
/*  8 */     FileConfiguration config = Main.plugin.getConfig();
/*  9 */     Nodes[] nodes = Nodes.values();
/*    */ 
/* 11 */     for (Nodes node : nodes) {
/* 12 */       if (!config.isSet(node.getNode())) {
/* 13 */         config.set(node.getNode(), node.getValue());
/*    */       }
/*    */     }
/*    */ 
/* 17 */     Main.plugin.saveConfig();
/* 18 */     Main.plugin.reloadConfig();
/*    */   }
/*    */ 
/*    */   public static enum Nodes
/*    */   {
/* 23 */     PLAYERS_ENABLE("player-bars.enable", Boolean.valueOf(true)), 
/* 24 */     PLAYERS_AFTER_ENABLE("player-bars.after-name.enable", Boolean.valueOf(true)), 
/* 25 */     PLAYERS_AFTER_STYLE("player-bars.after-name.display-style", Integer.valueOf(1)), 
/* 26 */     PLAYERS_AFTER_ALWAYS_SHOWN("player-bars.after-name.always-shown", Boolean.valueOf(false)), 
/* 27 */     PLAYERS_AFTER_TEXT_MODE("player-bars.after-name.text-mode", Boolean.valueOf(false)), 
/* 28 */     PLAYERS_AFTER_DELAY("player-bars.after-name.hide-delay-seconds", Integer.valueOf(5)), 
/* 29 */     PLAYERS_AFTER_USE_CUSTOM("player-bars.after-name.use-custom-file", Boolean.valueOf(false)), 
/*    */ 
/* 31 */     PLAYERS_BELOW_ENABLE("player-bars.below-name.enable", Boolean.valueOf(true)), 
/* 32 */     PLAYERS_BELOW_TEXT("player-bars.below-name.text", "% &cHealth"), 
/* 33 */     PLAYERS_BELOW_DISPLAY_RAW_HEARTS("player-bars.below-name.display-raw-hearts", Boolean.valueOf(false)), 
/* 34 */     PLAYERS_BELOW_USE_PROPORTION("player-bars.below-name.use-proportion", Boolean.valueOf(true)), 
/* 35 */     PLAYERS_BELOW_PROPORTIONAL_TO("player-bars.below-name.proportional-to", Integer.valueOf(100)), 
/*    */ 
/* 37 */     PLAYERS_WORLD_DISABLING("player-bars.world-disabling", Boolean.valueOf(false)), 
/* 38 */     PLAYERS_DISABLED_WORLDS("player-bars.disabled-worlds", "world_nether,world_the_end"), 
/*    */ 
/* 40 */     MOB_ENABLE("mob-bars.enable", Boolean.valueOf(true)), 
/* 41 */     MOB_SHOW_ON_NAMED("mob-bars.show-on-named-mobs", Boolean.valueOf(true)), 
/* 42 */     MOB_STYLE("mob-bars.display-style", Integer.valueOf(1)), 
/* 43 */     MOB_ALWAYS_SHOWN("mob-bars.always-shown", Boolean.valueOf(false)), 
/* 44 */     MOB_TEXT_MODE("mob-bars.text-mode", Boolean.valueOf(false)), 
/* 45 */     MOB_CUSTOM_TEXT_ENABLE("mob-bars.custom-text-enable", Boolean.valueOf(false)), 
/* 46 */     MOB_CUSTOM_TEXT("mob-bars.custom-text", "{name} - &a{health}/{max}"), 
/* 47 */     MOB_DELAY("mob-bars.hide-delay-seconds", Integer.valueOf(5)), 
/* 48 */     MOB_SHOW_IF_LOOKING("mob-bars.show-only-if-looking", Boolean.valueOf(false)), 
/* 49 */     MOB_USE_CUSTOM("mob-bars.use-custom-file", Boolean.valueOf(false)), 
/* 50 */     MOB_WORLD_DISABLING("mob-bars.world-disabling", Boolean.valueOf(false)), 
/* 51 */     MOB_DISABLED_WORLDS("mob-bars.disabled-worlds", "world_nether,world_the_end"), 
/* 52 */     MOB_TYPE_DISABLING("mob-bars.type-disabling", Boolean.valueOf(false)), 
/* 53 */     MOB_DISABLED_TYPES("mob-bars.disabled-types", "creeper,zombie,skeleton,iron_golem"), 
/*    */ 
/* 55 */     HOOKS_EPIBOSS("hooks.epicboss", Boolean.valueOf(false)), 
/*    */ 
/* 57 */     FIX_TAB_NAMES("fix-tab-names", Boolean.valueOf(true)), 
/* 58 */     FIX_DEATH_MESSAGES("fix-death-messages", Boolean.valueOf(true)), 
/* 59 */     UPDATE_NOTIFICATION("update-notification", Boolean.valueOf(true)), 
/* 60 */     USE_PLAYER_PERMISSIONS("use-player-bar-permissions", Boolean.valueOf(false)), 
/* 61 */     OVERRIDE_OTHER_SCOREBOARD("override-other-scoreboard", Boolean.valueOf(false));
/*    */ 
/*    */     private String node;
/*    */     private Object value;
/*    */ 
/* 67 */     private Nodes(String node, Object defaultValue) { this.node = node;
/* 68 */       this.value = defaultValue; }
/*    */ 
/*    */     public String getNode()
/*    */     {
/* 72 */       return this.node;
/*    */     }
/*    */ 
/*    */     public Object getValue() {
/* 76 */       return this.value;
/*    */     }
/*    */   }
/*    */ }

/* Location:           D:\Github\Mechanics\HealthBar.jar
 * Qualified Name:     com.gmail.filoghost.healthbar.Configuration
 * JD-Core Version:    0.6.2
 */