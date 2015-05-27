/*    */ package de.zuvexhd.main;
/*    */ 
/*    */ import java.io.PrintStream;
/*    */ import org.bukkit.Bukkit;
/*    */ import org.bukkit.plugin.PluginManager;
/*    */ import org.bukkit.plugin.java.JavaPlugin;
/*    */ 
/*    */ public class main extends JavaPlugin
/*    */ {
/*    */   public void onEnable()
/*    */   {
/* 12 */     System.out.println("");
/* 13 */     Bukkit.getPluginManager().registerEvents(new Events(this), this);
/*    */   }
/*    */ 
/*    */   public void onDisable()
/*    */   {
/* 19 */     System.out.println("");
/*    */   }
/*    */ }

/* Location:           D:\Github\Mechanics\AutoRespawn.jar
 * Qualified Name:     de.zuvexhd.main.main
 * JD-Core Version:    0.6.2
 */