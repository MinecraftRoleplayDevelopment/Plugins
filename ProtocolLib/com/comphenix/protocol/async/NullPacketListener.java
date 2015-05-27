/*    */ package com.comphenix.protocol.async;
/*    */ 
/*    */ import com.comphenix.protocol.events.ListenerOptions;
/*    */ import com.comphenix.protocol.events.ListenerPriority;
/*    */ import com.comphenix.protocol.events.ListeningWhitelist;
/*    */ import com.comphenix.protocol.events.ListeningWhitelist.Builder;
/*    */ import com.comphenix.protocol.events.PacketEvent;
/*    */ import com.comphenix.protocol.events.PacketListener;
/*    */ import org.bukkit.plugin.Plugin;
/*    */ 
/*    */ class NullPacketListener
/*    */   implements PacketListener
/*    */ {
/*    */   private ListeningWhitelist sendingWhitelist;
/*    */   private ListeningWhitelist receivingWhitelist;
/*    */   private Plugin plugin;
/*    */ 
/*    */   public NullPacketListener(PacketListener original)
/*    */   {
/* 44 */     this.sendingWhitelist = cloneWhitelist(ListenerPriority.LOW, original.getSendingWhitelist());
/* 45 */     this.receivingWhitelist = cloneWhitelist(ListenerPriority.LOW, original.getReceivingWhitelist());
/* 46 */     this.plugin = original.getPlugin();
/*    */   }
/*    */ 
/*    */   public void onPacketSending(PacketEvent event)
/*    */   {
/*    */   }
/*    */ 
/*    */   public void onPacketReceiving(PacketEvent event)
/*    */   {
/*    */   }
/*    */ 
/*    */   public ListeningWhitelist getSendingWhitelist()
/*    */   {
/* 61 */     return this.sendingWhitelist;
/*    */   }
/*    */ 
/*    */   public ListeningWhitelist getReceivingWhitelist()
/*    */   {
/* 66 */     return this.receivingWhitelist;
/*    */   }
/*    */ 
/*    */   private ListeningWhitelist cloneWhitelist(ListenerPriority priority, ListeningWhitelist whitelist) {
/* 70 */     if (whitelist != null)
/*    */     {
/* 72 */       return ListeningWhitelist.newBuilder(whitelist).priority(priority).mergeOptions(new ListenerOptions[] { ListenerOptions.ASYNC }).build();
/*    */     }
/* 74 */     return null;
/*    */   }
/*    */ 
/*    */   public Plugin getPlugin()
/*    */   {
/* 79 */     return this.plugin;
/*    */   }
/*    */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.async.NullPacketListener
 * JD-Core Version:    0.6.2
 */