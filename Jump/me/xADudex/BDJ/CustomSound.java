/*    */ package me.xADudex.BDJ;
/*    */ 
/*    */ import org.bukkit.Location;
/*    */ import org.bukkit.Sound;
/*    */ import org.bukkit.World;
/*    */ 
/*    */ public class CustomSound
/*    */ {
/*    */   Sound sound;
/*    */   float volume;
/*    */   float speed;
/*    */ 
/*    */   CustomSound(Sound sound, float volume, float speed)
/*    */   {
/* 13 */     this.sound = sound;
/* 14 */     this.volume = volume;
/* 15 */     this.speed = speed;
/*    */   }
/*    */ 
/*    */   void play(Location loc) {
/* 19 */     if (loc != null)
/* 20 */       loc.getWorld().playSound(loc, this.sound, this.volume, this.speed);
/*    */   }
/*    */ }

/* Location:           D:\Github\Mechanics\BetterDoubleJump.jar
 * Qualified Name:     me.xADudex.BDJ.CustomSound
 * JD-Core Version:    0.6.2
 */