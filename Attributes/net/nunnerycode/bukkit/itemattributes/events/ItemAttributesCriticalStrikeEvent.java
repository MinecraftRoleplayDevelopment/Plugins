/*    */ package net.nunnerycode.bukkit.itemattributes.events;
/*    */ 
/*    */ import net.nunnerycode.bukkit.itemattributes.api.events.ItemAttributesEvent;
/*    */ import net.nunnerycode.bukkit.itemattributes.api.events.attributes.CriticalStrikeEvent;
/*    */ import net.nunnerycode.bukkit.itemattributes.api.events.attributes.LivingEntityAttributeEvent;
/*    */ import org.bukkit.entity.LivingEntity;
/*    */ import org.bukkit.event.Cancellable;
/*    */ 
/*    */ public class ItemAttributesCriticalStrikeEvent extends ItemAttributesEvent
/*    */   implements LivingEntityAttributeEvent, CriticalStrikeEvent, Cancellable
/*    */ {
/*    */   private LivingEntity livingEntity;
/*    */   private LivingEntity target;
/*    */   private double criticalRate;
/*    */   private double criticalDamage;
/*    */   private boolean cancelled;
/*    */ 
/*    */   public ItemAttributesCriticalStrikeEvent(LivingEntity livingEntity, LivingEntity target, double criticalRate, double criticalDamage)
/*    */   {
/* 20 */     this.livingEntity = livingEntity;
/* 21 */     this.target = target;
/* 22 */     this.criticalRate = criticalRate;
/* 23 */     this.criticalDamage = criticalDamage;
/*    */   }
/*    */ 
/*    */   public double getCriticalRate()
/*    */   {
/* 28 */     return this.criticalRate;
/*    */   }
/*    */ 
/*    */   public double getCriticalDamage()
/*    */   {
/* 33 */     return this.criticalDamage;
/*    */   }
/*    */ 
/*    */   public LivingEntity getTarget()
/*    */   {
/* 38 */     return this.target;
/*    */   }
/*    */ 
/*    */   public LivingEntity getLivingEntity()
/*    */   {
/* 43 */     return this.livingEntity;
/*    */   }
/*    */ 
/*    */   public boolean isCancelled()
/*    */   {
/* 48 */     return this.cancelled;
/*    */   }
/*    */ 
/*    */   public void setCancelled(boolean b)
/*    */   {
/* 53 */     this.cancelled = b;
/*    */   }
/*    */ }

/* Location:           D:\Github\Mechanics\ItemAttributes.jar
 * Qualified Name:     net.nunnerycode.bukkit.itemattributes.events.ItemAttributesCriticalStrikeEvent
 * JD-Core Version:    0.6.2
 */