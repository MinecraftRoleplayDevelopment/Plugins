/*    */ package com.comphenix.protocol.injector.spigot;
/*    */ 
/*    */ import com.comphenix.protocol.PacketType;
/*    */ import com.comphenix.protocol.concurrency.PacketTypeSet;
/*    */ import com.comphenix.protocol.events.ListenerOptions;
/*    */ import com.comphenix.protocol.events.PacketListener;
/*    */ import com.comphenix.protocol.injector.GamePhase;
/*    */ import com.comphenix.protocol.injector.PacketFilterManager.PlayerInjectHooks;
/*    */ import com.comphenix.protocol.injector.player.PlayerInjectionHandler;
/*    */ import java.io.DataInputStream;
/*    */ import java.util.Set;
/*    */ import org.bukkit.entity.Player;
/*    */ 
/*    */ public abstract class AbstractPlayerHandler
/*    */   implements PlayerInjectionHandler
/*    */ {
/*    */   protected PacketTypeSet sendingFilters;
/*    */ 
/*    */   public AbstractPlayerHandler(PacketTypeSet sendingFilters)
/*    */   {
/* 20 */     this.sendingFilters = sendingFilters;
/*    */   }
/*    */ 
/*    */   public void setPlayerHook(GamePhase phase, PacketFilterManager.PlayerInjectHooks playerHook)
/*    */   {
/* 25 */     throw new UnsupportedOperationException("This is not needed in Spigot.");
/*    */   }
/*    */ 
/*    */   public void setPlayerHook(PacketFilterManager.PlayerInjectHooks playerHook)
/*    */   {
/* 30 */     throw new UnsupportedOperationException("This is not needed in Spigot.");
/*    */   }
/*    */ 
/*    */   public void addPacketHandler(PacketType type, Set<ListenerOptions> options)
/*    */   {
/* 35 */     this.sendingFilters.addType(type);
/*    */   }
/*    */ 
/*    */   public void removePacketHandler(PacketType type)
/*    */   {
/* 40 */     this.sendingFilters.removeType(type);
/*    */   }
/*    */ 
/*    */   public Set<PacketType> getSendingFilters()
/*    */   {
/* 45 */     return this.sendingFilters.values();
/*    */   }
/*    */ 
/*    */   public void close()
/*    */   {
/* 50 */     this.sendingFilters.clear();
/*    */   }
/*    */ 
/*    */   public PacketFilterManager.PlayerInjectHooks getPlayerHook(GamePhase phase)
/*    */   {
/* 55 */     return PacketFilterManager.PlayerInjectHooks.NETWORK_SERVER_OBJECT;
/*    */   }
/*    */ 
/*    */   public boolean canRecievePackets()
/*    */   {
/* 60 */     return true;
/*    */   }
/*    */ 
/*    */   public PacketFilterManager.PlayerInjectHooks getPlayerHook()
/*    */   {
/* 66 */     return PacketFilterManager.PlayerInjectHooks.NETWORK_SERVER_OBJECT;
/*    */   }
/*    */ 
/*    */   public Player getPlayerByConnection(DataInputStream inputStream) throws InterruptedException
/*    */   {
/* 71 */     throw new UnsupportedOperationException("This is not needed in Spigot.");
/*    */   }
/*    */ 
/*    */   public void checkListener(PacketListener listener)
/*    */   {
/*    */   }
/*    */ 
/*    */   public void checkListener(Set<PacketListener> listeners)
/*    */   {
/*    */   }
/*    */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.injector.spigot.AbstractPlayerHandler
 * JD-Core Version:    0.6.2
 */