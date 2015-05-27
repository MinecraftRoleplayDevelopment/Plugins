/*    */ package main.Chunks;
/*    */ 
/*    */ import java.io.File;
/*    */ import java.io.IOException;
/*    */ import java.io.PrintStream;
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;
/*    */ import org.bukkit.configuration.file.FileConfiguration;
/*    */ import org.bukkit.configuration.file.FileConfigurationOptions;
/*    */ import org.bukkit.configuration.file.YamlConfiguration;
/*    */ 
/*    */ public class Save
/*    */ {
/*    */   static final String path = "plugins/Chunks/config.yml";
/* 13 */   static File file = new File("plugins/Chunks/config.yml");
/* 14 */   static FileConfiguration config = YamlConfiguration.loadConfiguration(file);
/*    */   public static List<String> worlds;
/*    */ 
/*    */   public static void createConfig()
/*    */   {
/* 19 */     List worldList = new ArrayList();
/* 20 */     worldList.add("world");
/* 21 */     worldList.add("world_nether");
/* 23 */     config.addDefault("Worlds", worldList);
/*    */ 
/* 25 */     config.options().copyDefaults(true);
/*    */ 
/* 27 */     save();
/*    */   }
/*    */ 
/*    */   public static void readConfig() {
/* 31 */     worlds = config.getStringList("Worlds");
/*    */   }
/*    */ 
/*    */   public static void save() {
/*    */     try {
/* 36 */       config.save("plugins/Chunks/config.yml");
/*    */     } catch (IOException e) {
/* 38 */       System.out.println("[Chunks] Error 'createConfig' on plugins/NoNewChunks/config.yml");
/*    */     }
/*    */   }
/*    */ }

/* Location:           D:\Github\Mechanics\Chunks.jar
 * Qualified Name:     net.PixelizedMC.Chunks.CM
 * JD-Core Version:    0.6.2
 */