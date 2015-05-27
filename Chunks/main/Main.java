/*    */ package main.Chunks;
/*    */ 
/*    */ import org.bukkit.Bukkit;
/*    */ import org.bukkit.command.Command;
/*    */ import org.bukkit.command.CommandSender;
/*    */ import org.bukkit.plugin.PluginManager;
/*    */ import org.bukkit.plugin.java.JavaPlugin;
/*    */ 
/*    */ public class Main extends JavaPlugin
/*    */ {
/* 10 */   static Main instance = null;
/*    */ 
/*    */   public void onEnable() {
/* 13 */     instance = this;
/* 14 */     Bukkit.getPluginManager().registerEvents(new ChunkListener(), this);
/*    */ 
/* 17 */     CM.createConfig();
/* 18 */     CM.readConfig();
/*    */   }
/*    */ 
/*    */   public void onDisable()
/*    */   {
/*    */   }
/*    */ 
/*    */   public boolean onCommand(CommandSender a, Command b, String c, String[] d) {
/* 26 */     return Commands.command(a, b, c, d);
/*    */   }
/*    */ 
/*    */   public static Main getInstance() {
/* 30 */     return instance;
/*    */   }
/*    */ }

/* Location:           D:\Github\Mechanics\NoNewChunks.jar
 * Qualified Name:     net.PixelizedMC.NoNewChunks.Main
 * JD-Core Version:    0.6.2
 */