/*    */ package com.comphenix.protocol.injector.spigot;
/*    */ 
/*    */ import com.comphenix.protocol.PacketType;
/*    */ import com.comphenix.protocol.concurrency.PacketTypeSet;
/*    */ import com.comphenix.protocol.events.NetworkMarker;
/*    */ import com.comphenix.protocol.events.PacketContainer;
/*    */ import com.comphenix.protocol.events.PacketEvent;
/*    */ import com.comphenix.protocol.injector.player.PlayerInjectionHandler.ConflictStrategy;
/*    */ import com.comphenix.protocol.utility.MinecraftProtocolVersion;
/*    */ import io.netty.channel.Channel;
/*    */ import java.io.InputStream;
/*    */ import java.lang.reflect.InvocationTargetException;
/*    */ import java.net.InetSocketAddress;
/*    */ import org.bukkit.entity.Player;
/*    */ 
/*    */ class DummyPlayerHandler extends AbstractPlayerHandler
/*    */ {
/*    */   private SpigotPacketInjector injector;
/*    */ 
/*    */   public int getProtocolVersion(Player player)
/*    */   {
/* 29 */     return MinecraftProtocolVersion.getCurrentVersion();
/*    */   }
/*    */ 
/*    */   public DummyPlayerHandler(SpigotPacketInjector injector, PacketTypeSet sendingFilters) {
/* 33 */     super(sendingFilters);
/* 34 */     this.injector = injector;
/*    */   }
/*    */ 
/*    */   public boolean uninjectPlayer(InetSocketAddress address)
/*    */   {
/* 39 */     return true;
/*    */   }
/*    */ 
/*    */   public boolean uninjectPlayer(Player player)
/*    */   {
/* 44 */     this.injector.uninjectPlayer(player);
/* 45 */     return true;
/*    */   }
/*    */ 
/*    */   public void sendServerPacket(Player receiver, PacketContainer packet, NetworkMarker marker, boolean filters) throws InvocationTargetException
/*    */   {
/* 50 */     this.injector.sendServerPacket(receiver, packet, marker, filters);
/*    */   }
/*    */ 
/*    */   public void recieveClientPacket(Player player, Object mcPacket) throws IllegalAccessException, InvocationTargetException
/*    */   {
/* 55 */     this.injector.processPacket(player, mcPacket);
/*    */   }
/*    */ 
/*    */   public void injectPlayer(Player player, PlayerInjectionHandler.ConflictStrategy strategy)
/*    */   {
/* 61 */     this.injector.injectPlayer(player);
/*    */   }
/*    */ 
/*    */   public boolean hasMainThreadListener(PacketType type)
/*    */   {
/* 66 */     return this.sendingFilters.contains(type);
/*    */   }
/*    */ 
/*    */   public void handleDisconnect(Player player)
/*    */   {
/*    */   }
/*    */ 
/*    */   public PacketEvent handlePacketRecieved(PacketContainer packet, InputStream input, byte[] buffered)
/*    */   {
/* 77 */     if (buffered != null) {
/* 78 */       this.injector.saveBuffered(packet.getHandle(), buffered);
/*    */     }
/* 80 */     return null;
/*    */   }
/*    */ 
/*    */   public void updatePlayer(Player player)
/*    */   {
/*    */   }
/*    */ 
/*    */   public Channel getChannel(Player player)
/*    */   {
/* 90 */     throw new UnsupportedOperationException();
/*    */   }
/*    */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.injector.spigot.DummyPlayerHandler
 * JD-Core Version:    0.6.2
 */