/*    */ package com.comphenix.protocol.injector.spigot;
/*    */ 
/*    */ import com.comphenix.protocol.PacketType;
/*    */ import com.comphenix.protocol.concurrency.PacketTypeSet;
/*    */ import com.comphenix.protocol.events.PacketContainer;
/*    */ import com.comphenix.protocol.events.PacketEvent;
/*    */ import com.comphenix.protocol.injector.packet.PacketInjector;
/*    */ import com.google.common.collect.Sets;
/*    */ import java.util.Set;
/*    */ import org.bukkit.entity.Player;
/*    */ 
/*    */ class DummyPacketInjector extends AbstractPacketInjector
/*    */   implements PacketInjector
/*    */ {
/*    */   private SpigotPacketInjector injector;
/* 21 */   private PacketTypeSet lastBufferedPackets = new PacketTypeSet();
/*    */ 
/*    */   public DummyPacketInjector(SpigotPacketInjector injector, PacketTypeSet reveivedFilters) {
/* 24 */     super(reveivedFilters);
/* 25 */     this.injector = injector;
/*    */   }
/*    */ 
/*    */   public void inputBuffersChanged(Set<PacketType> set)
/*    */   {
/* 30 */     Set removed = Sets.difference(this.lastBufferedPackets.values(), set);
/* 31 */     Set added = Sets.difference(set, this.lastBufferedPackets.values());
/*    */ 
/* 34 */     for (PacketType packet : removed) {
/* 35 */       this.injector.getProxyPacketInjector().removePacketHandler(packet);
/*    */     }
/* 37 */     for (PacketType packet : added)
/* 38 */       this.injector.getProxyPacketInjector().addPacketHandler(packet, null);
/*    */   }
/*    */ 
/*    */   public PacketEvent packetRecieved(PacketContainer packet, Player client, byte[] buffered)
/*    */   {
/* 44 */     return this.injector.packetReceived(packet, client, buffered);
/*    */   }
/*    */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.injector.spigot.DummyPacketInjector
 * JD-Core Version:    0.6.2
 */