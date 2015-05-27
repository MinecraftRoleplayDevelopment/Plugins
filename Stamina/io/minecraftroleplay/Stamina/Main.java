/*    */ package io.minecraftroleplay.Stamina;
/*    */ 
/*    */ import java.io.File;
/*    */ import java.util.logging.Logger;
/*    */ import org.bukkit.Bukkit;
/*    */ import org.bukkit.Server;
/*    */ import org.bukkit.configuration.file.FileConfiguration;
/*    */ import org.bukkit.entity.Player;
/*    */ import org.bukkit.plugin.PluginManager;
/*    */ import org.bukkit.scheduler.BukkitScheduler;
/*    */ 
/*    */ public class Main extends org.bukkit.plugin.java.JavaPlugin implements org.bukkit.event.Listener
/*    */ {
/*    */   public static Main plugin;
/* 15 */   public final Logger logger = Logger.getLogger("Minecraft");
/*    */   
/*    */   public void onEnable()
/*    */   {
/* 19 */     getServer().getPluginManager().registerEvents(this, this);
/* 20 */     File configFile = new File(getDataFolder() + "/config.yml");
/* 21 */     if (!configFile.exists())
/*    */     {
/* 23 */       saveDefaultConfig();
/*    */     }
/* 25 */     Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
/*    */       public void run() { Player[] arrayOfPlayer;
/* 27 */         int j = (arrayOfPlayer = Bukkit.getOnlinePlayers()).length; for (int i = 0; i < j; i++) { Player player = arrayOfPlayer[i];
/* 28 */           if (player.isSprinting()) {
/* 29 */             player.setFoodLevel(player.getFoodLevel() - Main.this.getConfig().getInt("Hunger-value"));
/*    */           }
/*    */         }
/*    */       }
/* 33 */     }, 0L, getConfig().getInt("Hunger-interval"));
/* 34 */     Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
/*    */       public void run() { Player[] arrayOfPlayer;
/* 36 */         int j = (arrayOfPlayer = Bukkit.getOnlinePlayers()).length; for (int i = 0; i < j; i++) { Player player = arrayOfPlayer[i];
/* 37 */           if ((!player.isSprinting()) && (player.getFoodLevel() != 20)) {
/* 38 */             player.setFoodLevel(player.getFoodLevel() + Main.this.getConfig().getInt("Saturation-interval"));
/*    */           }
/*    */         }
/*    */       }
/* 42 */     }, 0L, getConfig().getInt("Saturation-interval"));
/*    */   }
/*    */ }


/* Location:              C:\Users\Harry\Documents\Eclipse Workspace\SprintBar.jar!\me\Alcatraz\SprintBar\Main.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */