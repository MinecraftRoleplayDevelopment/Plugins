/*    */ package com.zettelnet.armorweight.event;
/*    */ 
/*    */ import org.bukkit.entity.Horse;
/*    */ import org.bukkit.event.HandlerList;
/*    */ import org.bukkit.event.entity.EntityEvent;
/*    */ 
/*    */ public class HorseWeightChangeEvent extends EntityEvent
/*    */ {
/*  9 */   private static final HandlerList handlers = new HandlerList();
/*    */   private double oldWeight;
/*    */   private double newWeight;
/*    */ 
/*    */   public HorseWeightChangeEvent(Horse horse, double oldWeight, double newWeight)
/*    */   {
/* 14 */     super(horse);
/*    */ 
/* 16 */     this.oldWeight = oldWeight;
/* 17 */     this.newWeight = newWeight;
/*    */   }
/*    */ 
/*    */   public Horse getEntity()
/*    */   {
/* 22 */     return (Horse)super.getEntity();
/*    */   }
/*    */ 
/*    */   public double getOldWeight() {
/* 26 */     return this.oldWeight;
/*    */   }
/*    */ 
/*    */   public double getNewWeight() {
/* 30 */     return this.newWeight;
/*    */   }
/*    */ 
/*    */   public HandlerList getHandlers()
/*    */   {
/* 35 */     return handlers;
/*    */   }
/*    */ 
/*    */   public static HandlerList getHandlerList() {
/* 39 */     return handlers;
/*    */   }
/*    */ 
/*    */   public String toString()
/*    */   {
/* 44 */     return "HorseWeightChangeEvent [horse=" + getEntity() + ", oldWeight=" + this.oldWeight + ", newWeight=" + this.newWeight + "]";
/*    */   }
/*    */ }

/* Location:           D:\Github\Mechanics\ArmorWeight.jar
 * Qualified Name:     com.zettelnet.armorweight.event.HorseWeightChangeEvent
 * JD-Core Version:    0.6.2
 */