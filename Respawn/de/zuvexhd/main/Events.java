/*    */ package de.zuvexhd.main;
/*    */ 
/*    */ import org.bukkit.Bukkit;
/*    */ import org.bukkit.entity.Player;
/*    */ import org.bukkit.entity.Player.Spigot;
/*    */ import org.bukkit.event.EventHandler;
/*    */ import org.bukkit.event.Listener;
/*    */ import org.bukkit.event.entity.PlayerDeathEvent;
/*    */ import org.bukkit.plugin.Plugin;
/*    */ import org.bukkit.scheduler.BukkitScheduler;
/*    */ 
/*    */ public class Events
/*    */   implements Listener
/*    */ {
/*    */   public static Plugin plugin;
/*    */ 
/*    */   public Events(Plugin plugin)
/*    */   {
/* 19 */     plugin = plugin;
/*    */   }
/*    */ 
/*    */   public void Respawn(final Player player, int Time) {
/* 23 */     Bukkit.getScheduler().runTaskLater(plugin, new Runnable()
/*    */     {
/*    */       public void run()
/*    */       {
/* 27 */         player.spigot().respawn();
/*    */       }
/*    */     }
/*    */     , Time);
/*    */   }
/*    */ 
/*    */   @EventHandler
/*    */   public void onDeath(PlayerDeathEvent event) {
/* 35 */     Player player = event.getEntity();
/* 36 */     Respawn(player, 6);
/*    */   }
/*    */ }

/* Location:           D:\Github\Mechanics\AutoRespawn.jar
 * Qualified Name:     de.zuvexhd.main.Events
 * JD-Core Version:    0.6.2
 */