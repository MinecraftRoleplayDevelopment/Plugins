/*    */ package net.nunnerycode.bukkit.itemattributes.api.events;
/*    */ 
/*    */ import org.bukkit.event.Event;
/*    */ import org.bukkit.event.HandlerList;
/*    */ 
/*    */ public class ItemAttributesEvent extends Event
/*    */ {
/*  8 */   private static HandlerList handlerList = new HandlerList();
/*    */ 
/*    */   public static HandlerList getHandlerList() {
/* 11 */     return handlerList;
/*    */   }
/*    */ 
/*    */   public HandlerList getHandlers()
/*    */   {
/* 16 */     return handlerList;
/*    */   }
/*    */ }

/* Location:           D:\Github\Mechanics\ItemAttributes.jar
 * Qualified Name:     net.nunnerycode.bukkit.itemattributes.api.events.ItemAttributesEvent
 * JD-Core Version:    0.6.2
 */