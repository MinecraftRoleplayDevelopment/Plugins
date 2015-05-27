/*    */ package net.nunnerycode.bukkit.itemattributes.events;
/*    */ 
/*    */ import net.nunnerycode.bukkit.itemattributes.api.events.ItemAttributesEvent;
/*    */ import net.nunnerycode.bukkit.itemattributes.api.events.attributes.HealthUpdateEvent;
/*    */ import net.nunnerycode.bukkit.itemattributes.api.events.attributes.LivingEntityAttributeEvent;
/*    */ import org.bukkit.entity.LivingEntity;
/*    */ import org.bukkit.event.Cancellable;
/*    */ 
/*    */ public class ItemAttributesHealthUpdateEvent extends ItemAttributesEvent
/*    */   implements LivingEntityAttributeEvent, HealthUpdateEvent, Cancellable
/*    */ {
/*    */   private LivingEntity livingEntity;
/*    */   private double previousHealth;
/*    */   private double baseHealth;
/*    */   private double changeInHealth;
/*    */   private boolean cancelled;
/*    */ 
/*    */   public ItemAttributesHealthUpdateEvent(LivingEntity livingEntity, double previousHealth, double baseHealth, double changeInHealth)
/*    */   {
/* 21 */     this.livingEntity = livingEntity;
/* 22 */     this.previousHealth = previousHealth;
/* 23 */     this.baseHealth = baseHealth;
/* 24 */     this.changeInHealth = changeInHealth;
/*    */   }
/*    */ 
/*    */   public boolean isCancelled()
/*    */   {
/* 29 */     return this.cancelled;
/*    */   }
/*    */ 
/*    */   public void setCancelled(boolean b)
/*    */   {
/* 34 */     this.cancelled = b;
/*    */   }
/*    */ 
/*    */   public double getPreviousHealth()
/*    */   {
/* 39 */     return this.previousHealth;
/*    */   }
/*    */ 
/*    */   public double getBaseHealth()
/*    */   {
/* 44 */     return this.baseHealth;
/*    */   }
/*    */ 
/*    */   public double getChangeInHealth()
/*    */   {
/* 49 */     return this.changeInHealth;
/*    */   }
/*    */ 
/*    */   public void setChangeInHealth(double changeInHealth)
/*    */   {
/* 54 */     this.changeInHealth = changeInHealth;
/*    */   }
/*    */ 
/*    */   public LivingEntity getLivingEntity()
/*    */   {
/* 59 */     return this.livingEntity;
/*    */   }
/*    */ }

/* Location:           D:\Github\Mechanics\ItemAttributes.jar
 * Qualified Name:     net.nunnerycode.bukkit.itemattributes.events.ItemAttributesHealthUpdateEvent
 * JD-Core Version:    0.6.2
 */