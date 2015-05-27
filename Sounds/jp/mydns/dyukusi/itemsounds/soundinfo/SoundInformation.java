/*    */ package jp.mydns.dyukusi.itemsounds.soundinfo;
/*    */ 
/*    */ import org.bukkit.Sound;
/*    */ 
/*    */ public class SoundInformation
/*    */ {
/*    */   private Sound sound;
/*    */   private float pitch;
/*    */   private float volume;
/*    */ 
/*    */   public SoundInformation(Sound s, Float v, Float p)
/*    */   {
/* 11 */     this.sound = s;
/* 12 */     this.volume = v.floatValue();
/* 13 */     this.pitch = p.floatValue();
/*    */   }
/*    */ 
/*    */   public Sound get_sound() {
/* 17 */     return this.sound;
/*    */   }
/*    */ 
/*    */   public float get_pitch() {
/* 21 */     return this.pitch;
/*    */   }
/*    */ 
/*    */   public float get_volume() {
/* 25 */     return this.volume;
/*    */   }
/*    */ }

/* Location:           D:\Github\Mechanics\ItemSounds.jar
 * Qualified Name:     jp.mydns.dyukusi.itemsounds.soundinfo.SoundInformation
 * JD-Core Version:    0.6.2
 */