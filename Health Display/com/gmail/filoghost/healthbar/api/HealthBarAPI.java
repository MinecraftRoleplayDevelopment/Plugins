/*    */ package com.gmail.filoghost.healthbar.api;
/*    */ 
/*    */ import com.gmail.filoghost.healthbar.DamageListener;
/*    */ import com.gmail.filoghost.healthbar.MiscListeners;
/*    */ import org.bukkit.entity.LivingEntity;
/*    */ 
/*    */ public class HealthBarAPI
/*    */ {
/*    */   public static boolean mobHasBar(LivingEntity mob)
/*    */   {
/* 15 */     String tagName = mob.getCustomName();
/*    */ 
/* 17 */     if ((tagName != null) && 
/* 18 */       (tagName.startsWith("§r"))) {
/* 19 */       return true;
/*    */     }
/*    */ 
/* 23 */     return false;
/*    */   }
/*    */ 
/*    */   public static void mobHideBar(LivingEntity mob)
/*    */   {
/* 30 */     DamageListener.hideBar(mob);
/*    */   }
/*    */ 
/*    */   public static String getMobName(LivingEntity mob)
/*    */   {
/* 37 */     return DamageListener.getNameWhileHavingBar(mob);
/*    */   }
/*    */ 
/*    */   public static void disableWhiteTabNames()
/*    */   {
/* 45 */     MiscListeners.disableTabNamesFix();
/*    */   }
/*    */ }

/* Location:           D:\Github\Mechanics\HealthBar.jar
 * Qualified Name:     com.gmail.filoghost.healthbar.api.HealthBarAPI
 * JD-Core Version:    0.6.2
 */