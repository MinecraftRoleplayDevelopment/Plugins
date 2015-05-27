/*    */ package com.comphenix.protocol.events;
/*    */ 
/*    */ import com.google.common.base.Preconditions;
/*    */ import org.bukkit.plugin.Plugin;
/*    */ 
/*    */ public abstract class PacketPostAdapter
/*    */   implements PacketPostListener
/*    */ {
/*    */   private Plugin plugin;
/*    */ 
/*    */   public PacketPostAdapter(Plugin plugin)
/*    */   {
/* 15 */     this.plugin = ((Plugin)Preconditions.checkNotNull(plugin, "plugin cannot be NULL"));
/*    */   }
/*    */ 
/*    */   public Plugin getPlugin()
/*    */   {
/* 20 */     return this.plugin;
/*    */   }
/*    */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.events.PacketPostAdapter
 * JD-Core Version:    0.6.2
 */