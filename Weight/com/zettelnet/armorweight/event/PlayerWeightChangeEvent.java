/*    */ package com.zettelnet.armorweight.event;
/*    */ 
/*    */ import org.bukkit.entity.Player;
/*    */ import org.bukkit.event.HandlerList;
/*    */ import org.bukkit.event.player.PlayerEvent;
/*    */ 
/*    */ public class PlayerWeightChangeEvent extends PlayerEvent
/*    */ {
/*  9 */   private static final HandlerList handlers = new HandlerList();
/*    */   private double oldWeight;
/*    */   private double newWeight;
/*    */ 
/*    */   public PlayerWeightChangeEvent(Player player, double oldWeight, double newWeight)
/*    */   {
/* 14 */     super(player);
/*    */ 
/* 16 */     this.oldWeight = oldWeight;
/* 17 */     this.newWeight = newWeight;
/*    */   }
/*    */ 
/*    */   public double getOldWeight() {
/* 21 */     return this.oldWeight;
/*    */   }
/*    */ 
/*    */   public double getNewWeight() {
/* 25 */     return this.newWeight;
/*    */   }
/*    */ 
/*    */   public HandlerList getHandlers()
/*    */   {
/* 30 */     return handlers;
/*    */   }
/*    */ 
/*    */   public static HandlerList getHandlerList() {
/* 34 */     return handlers;
/*    */   }
/*    */ 
/*    */   public String toString()
/*    */   {
/* 39 */     return "PlayerWeightChangeEvent [player=" + getPlayer() + ", oldWeight=" + this.oldWeight + ", newWeight=" + this.newWeight + "]";
/*    */   }
/*    */ }

/* Location:           D:\Github\Mechanics\ArmorWeight.jar
 * Qualified Name:     com.zettelnet.armorweight.event.PlayerWeightChangeEvent
 * JD-Core Version:    0.6.2
 */