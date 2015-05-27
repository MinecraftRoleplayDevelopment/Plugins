/*    */ package net.nunnerycode.bukkit.itemattributes.events;
/*    */ 
/*    */ import net.nunnerycode.bukkit.itemattributes.api.events.ItemAttributesEvent;
/*    */ import net.nunnerycode.bukkit.itemattributes.api.events.attributes.LivingEntityAttributeEvent;
/*    */ import net.nunnerycode.bukkit.itemattributes.api.events.attributes.StunStrikeEvent;
/*    */ import org.bukkit.entity.LivingEntity;
/*    */ import org.bukkit.event.Cancellable;
/*    */ 
/*    */ public class ItemAttributesStunStrikeEvent extends ItemAttributesEvent
/*    */   implements LivingEntityAttributeEvent, StunStrikeEvent, Cancellable
/*    */ {
/*    */   private LivingEntity livingEntity;
/*    */   private LivingEntity target;
/*    */   private double stunRate;
/*    */   private int stunLength;
/*    */   private boolean cancelled;
/*    */ 
/*    */   public ItemAttributesStunStrikeEvent(LivingEntity livingEntity, LivingEntity target, double stunRate, int stunLength)
/*    */   {
/* 20 */     this.livingEntity = livingEntity;
/* 21 */     this.target = target;
/* 22 */     this.stunRate = stunRate;
/* 23 */     this.stunLength = stunLength;
/*    */   }
/*    */ 
/*    */   public double getStunRate()
/*    */   {
/* 28 */     return this.stunRate;
/*    */   }
/*    */ 
/*    */   public int getStunLength()
/*    */   {
/* 33 */     return this.stunLength;
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
 * Qualified Name:     net.nunnerycode.bukkit.itemattributes.events.ItemAttributesStunStrikeEvent
 * JD-Core Version:    0.6.2
 */