/*    */ package com.comphenix.protocol.events;
/*    */ 
/*    */ import org.bukkit.plugin.Plugin;
/*    */ 
/*    */ public abstract class PacketOutputAdapter
/*    */   implements PacketOutputHandler
/*    */ {
/*    */   private final Plugin plugin;
/*    */   private final ListenerPriority priority;
/*    */ 
/*    */   public PacketOutputAdapter(Plugin plugin, ListenerPriority priority)
/*    */   {
/* 19 */     this.priority = priority;
/* 20 */     this.plugin = plugin;
/*    */   }
/*    */ 
/*    */   public Plugin getPlugin()
/*    */   {
/* 25 */     return this.plugin;
/*    */   }
/*    */ 
/*    */   public ListenerPriority getPriority()
/*    */   {
/* 30 */     return this.priority;
/*    */   }
/*    */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.events.PacketOutputAdapter
 * JD-Core Version:    0.6.2
 */