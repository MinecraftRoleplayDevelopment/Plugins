/*    */ package com.comphenix.protocol.injector.netty;
/*    */ 
/*    */ import com.comphenix.protocol.PacketType.Protocol;
/*    */ import com.comphenix.protocol.events.NetworkMarker;
/*    */ import org.bukkit.entity.Player;
/*    */ 
/*    */ class ClosedInjector
/*    */   implements Injector
/*    */ {
/*    */   private Player player;
/*    */ 
/*    */   public ClosedInjector(Player player)
/*    */   {
/* 20 */     this.player = player;
/*    */   }
/*    */ 
/*    */   public boolean inject()
/*    */   {
/* 25 */     return false;
/*    */   }
/*    */ 
/*    */   public void close()
/*    */   {
/*    */   }
/*    */ 
/*    */   public void sendServerPacket(Object packet, NetworkMarker marker, boolean filtered)
/*    */   {
/*    */   }
/*    */ 
/*    */   public void recieveClientPacket(Object packet)
/*    */   {
/*    */   }
/*    */ 
/*    */   public PacketType.Protocol getCurrentProtocol()
/*    */   {
/* 45 */     return PacketType.Protocol.HANDSHAKING;
/*    */   }
/*    */ 
/*    */   public NetworkMarker getMarker(Object packet)
/*    */   {
/* 50 */     return null;
/*    */   }
/*    */ 
/*    */   public void saveMarker(Object packet, NetworkMarker marker)
/*    */   {
/*    */   }
/*    */ 
/*    */   public void setUpdatedPlayer(Player player)
/*    */   {
/*    */   }
/*    */ 
/*    */   public Player getPlayer()
/*    */   {
/* 65 */     return this.player;
/*    */   }
/*    */ 
/*    */   public void setPlayer(Player player)
/*    */   {
/* 70 */     this.player = player;
/*    */   }
/*    */ 
/*    */   public boolean isInjected()
/*    */   {
/* 75 */     return false;
/*    */   }
/*    */ 
/*    */   public boolean isClosed()
/*    */   {
/* 80 */     return true;
/*    */   }
/*    */ 
/*    */   public int getProtocolVersion()
/*    */   {
/* 85 */     return -2147483648;
/*    */   }
/*    */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.injector.netty.ClosedInjector
 * JD-Core Version:    0.6.2
 */