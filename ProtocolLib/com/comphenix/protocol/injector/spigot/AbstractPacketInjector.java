/*    */ package com.comphenix.protocol.injector.spigot;
/*    */ 
/*    */ import com.comphenix.protocol.PacketType;
/*    */ import com.comphenix.protocol.concurrency.PacketTypeSet;
/*    */ import com.comphenix.protocol.events.ListenerOptions;
/*    */ import com.comphenix.protocol.injector.packet.PacketInjector;
/*    */ import java.util.Set;
/*    */ 
/*    */ public abstract class AbstractPacketInjector
/*    */   implements PacketInjector
/*    */ {
/*    */   private PacketTypeSet reveivedFilters;
/*    */ 
/*    */   public AbstractPacketInjector(PacketTypeSet reveivedFilters)
/*    */   {
/* 14 */     this.reveivedFilters = reveivedFilters;
/*    */   }
/*    */ 
/*    */   public boolean isCancelled(Object packet)
/*    */   {
/* 20 */     return false;
/*    */   }
/*    */ 
/*    */   public void setCancelled(Object packet, boolean cancelled)
/*    */   {
/* 25 */     throw new UnsupportedOperationException();
/*    */   }
/*    */ 
/*    */   public boolean addPacketHandler(PacketType type, Set<ListenerOptions> options)
/*    */   {
/* 30 */     this.reveivedFilters.addType(type);
/* 31 */     return true;
/*    */   }
/*    */ 
/*    */   public boolean removePacketHandler(PacketType type)
/*    */   {
/* 36 */     this.reveivedFilters.removeType(type);
/* 37 */     return true;
/*    */   }
/*    */ 
/*    */   public boolean hasPacketHandler(PacketType type)
/*    */   {
/* 42 */     return this.reveivedFilters.contains(type);
/*    */   }
/*    */ 
/*    */   public Set<PacketType> getPacketHandlers()
/*    */   {
/* 47 */     return this.reveivedFilters.values();
/*    */   }
/*    */ 
/*    */   public void cleanupAll()
/*    */   {
/* 52 */     this.reveivedFilters.clear();
/*    */   }
/*    */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.injector.spigot.AbstractPacketInjector
 * JD-Core Version:    0.6.2
 */