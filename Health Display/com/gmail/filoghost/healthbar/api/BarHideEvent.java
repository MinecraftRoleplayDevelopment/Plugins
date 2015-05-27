/*    */ package com.gmail.filoghost.healthbar.api;
/*    */ 
/*    */ import org.bukkit.OfflinePlayer;
/*    */ import org.bukkit.event.Event;
/*    */ import org.bukkit.event.HandlerList;
/*    */ 
/*    */ public class BarHideEvent extends Event
/*    */ {
/*  8 */   private static final HandlerList handlers = new HandlerList();
/*    */   private OfflinePlayer player;
/*    */ 
/*    */   public BarHideEvent(OfflinePlayer player)
/*    */   {
/* 12 */     this.player = player;
/*    */   }
/*    */ 
/*    */   public OfflinePlayer getOfflinePlayer() {
/* 16 */     return this.player;
/*    */   }
/*    */ 
/*    */   public HandlerList getHandlers()
/*    */   {
/* 21 */     return handlers;
/*    */   }
/*    */ 
/*    */   public static HandlerList getHandlerList() {
/* 25 */     return handlers;
/*    */   }
/*    */ }

/* Location:           D:\Github\Mechanics\HealthBar.jar
 * Qualified Name:     com.gmail.filoghost.healthbar.api.BarHideEvent
 * JD-Core Version:    0.6.2
 */