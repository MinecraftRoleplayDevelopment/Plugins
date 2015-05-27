/*    */ package com.zettelnet.armorweight;
/*    */ 
/*    */ import com.zettelnet.armorweight.event.PlayerWeightChangeEvent;
/*    */ import com.zettelnet.armorweight.zet.chat.ChatMessage;
/*    */ import java.util.HashMap;
/*    */ import java.util.Map;
/*    */ import org.bukkit.Server;
/*    */ import org.bukkit.entity.Player;
/*    */ import org.bukkit.event.EventHandler;
/*    */ import org.bukkit.event.EventPriority;
/*    */ import org.bukkit.event.Listener;
/*    */ import org.bukkit.event.player.PlayerQuitEvent;
/*    */ import org.bukkit.plugin.PluginManager;
/*    */ 
/*    */ public class WeightTracker
/*    */   implements Listener
/*    */ {
/*    */   private final ArmorWeightPlugin plugin;
/*    */   private final WeightManager manager;
/*    */   private final ArmorWeightLanguage lang;
/*    */   private final ArmorWeightConfiguration config;
/*    */   private final Map<Player, Long> cooldownMap;
/*    */   private long cooldown;
/*    */ 
/*    */   public WeightTracker(ArmorWeightPlugin plugin, WeightManager manager)
/*    */   {
/* 26 */     this.plugin = plugin;
/* 27 */     this.manager = manager;
/* 28 */     this.lang = plugin.getLanguage();
/* 29 */     this.config = plugin.getConfiguration();
/*    */ 
/* 31 */     this.cooldownMap = new HashMap();
/* 32 */     this.cooldown = this.config.weightWarningCooldown();
/*    */   }
/*    */ 
/*    */   public void register() {
/* 36 */     this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
/*    */   }
/*    */ 
/*    */   @EventHandler(priority=EventPriority.MONITOR)
/*    */   public void onPlayerQuit(PlayerQuitEvent event) {
/* 41 */     this.cooldownMap.remove(event.getPlayer());
/*    */   }
/*    */ 
/*    */   @EventHandler(priority=EventPriority.MONITOR)
/*    */   public void onPlayerWeightChange(PlayerWeightChangeEvent event) {
/* 46 */     if (event.getNewWeight() <= event.getOldWeight()) {
/* 47 */       return;
/*    */     }
/* 49 */     if (!this.config.weightWarningEnabled()) {
/* 50 */       return;
/*    */     }
/*    */ 
/* 53 */     Player player = event.getPlayer();
/* 54 */     long time = System.currentTimeMillis();
/* 55 */     boolean canSendMessage = time - ((Long)this.cooldownMap.get(player)).longValue() >= this.cooldown;
/*    */ 
/* 57 */     if (canSendMessage) {
/* 58 */       this.lang.weightWarning.send(player, new Object[] { "weight", this.manager.formatWeight(event.getNewWeight()), "oldWeight", this.manager.formatWeight(event.getOldWeight()) });
/* 59 */       this.cooldownMap.put(player, Long.valueOf(time));
/*    */     }
/*    */   }
/*    */ }

/* Location:           D:\Github\Mechanics\ArmorWeight.jar
 * Qualified Name:     com.zettelnet.armorweight.WeightTracker
 * JD-Core Version:    0.6.2
 */