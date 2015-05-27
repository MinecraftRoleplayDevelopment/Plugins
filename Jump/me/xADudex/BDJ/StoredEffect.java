/*    */ package me.xADudex.BDJ;
/*    */ 
/*    */ import java.util.ArrayList;
/*    */ import org.bukkit.Effect;
/*    */ import org.bukkit.Location;
/*    */ import org.bukkit.World;
/*    */ import org.bukkit.entity.Player;
/*    */ import org.bukkit.entity.Player.Spigot;
/*    */ 
/*    */ public class StoredEffect
/*    */ {
/*    */   float x;
/*    */   float y;
/*    */   float z;
/*    */   float speed_data;
/*    */   int count;
/*    */   Effect type;
/*    */   int id;
/*    */   int data;
/*    */ 
/*    */   public StoredEffect(String name, float x, float y, float z, float speed_data, int count, int id, int data)
/*    */   {
/* 18 */     this.type = Effect.valueOf(name);
/* 19 */     this.x = x;
/* 20 */     this.y = y;
/* 21 */     this.z = z;
/* 22 */     this.count = count;
/* 23 */     this.speed_data = speed_data;
/* 24 */     this.id = id;
/* 25 */     this.data = data;
/*    */   }
/*    */ 
/*    */   public void play(Location loc) {
/* 29 */     for (Player p : loc.getWorld().getPlayers())
/* 30 */       p.spigot().playEffect(loc, this.type, this.id, this.data, this.x, this.y, this.z, this.speed_data, this.count, 256);
/*    */   }
/*    */ 
/*    */   public void play(Location loc, Player[] players)
/*    */   {
/* 35 */     for (Player p : players)
/* 36 */       p.spigot().playEffect(loc, this.type, this.id, this.data, this.x, this.y, this.z, this.speed_data, this.count, 256);
/*    */   }
/*    */ 
/*    */   public void play(Location loc, ArrayList<Player> players)
/*    */   {
/* 41 */     for (Player p : players)
/* 42 */       p.spigot().playEffect(loc, this.type, this.id, this.data, this.x, this.y, this.z, this.speed_data, this.count, 256);
/*    */   }
/*    */ }

/* Location:           D:\Github\Mechanics\BetterDoubleJump.jar
 * Qualified Name:     me.xADudex.BDJ.StoredEffect
 * JD-Core Version:    0.6.2
 */