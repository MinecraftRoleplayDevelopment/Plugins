/*    */ package com.comphenix.protocol.injector.player;
/*    */ 
/*    */ import com.comphenix.protocol.PacketType;
/*    */ import com.comphenix.protocol.events.ListenerOptions;
/*    */ import com.comphenix.protocol.events.NetworkMarker;
/*    */ import com.comphenix.protocol.events.PacketContainer;
/*    */ import com.comphenix.protocol.events.PacketEvent;
/*    */ import com.comphenix.protocol.events.PacketListener;
/*    */ import com.comphenix.protocol.injector.GamePhase;
/*    */ import com.comphenix.protocol.injector.PacketFilterManager.PlayerInjectHooks;
/*    */ import io.netty.channel.Channel;
/*    */ import java.io.DataInputStream;
/*    */ import java.io.InputStream;
/*    */ import java.lang.reflect.InvocationTargetException;
/*    */ import java.net.InetSocketAddress;
/*    */ import java.util.Set;
/*    */ import org.bukkit.entity.Player;
/*    */ 
/*    */ public abstract interface PlayerInjectionHandler
/*    */ {
/*    */   public abstract int getProtocolVersion(Player paramPlayer);
/*    */ 
/*    */   public abstract PacketFilterManager.PlayerInjectHooks getPlayerHook();
/*    */ 
/*    */   public abstract PacketFilterManager.PlayerInjectHooks getPlayerHook(GamePhase paramGamePhase);
/*    */ 
/*    */   public abstract void setPlayerHook(PacketFilterManager.PlayerInjectHooks paramPlayerInjectHooks);
/*    */ 
/*    */   public abstract void setPlayerHook(GamePhase paramGamePhase, PacketFilterManager.PlayerInjectHooks paramPlayerInjectHooks);
/*    */ 
/*    */   public abstract void addPacketHandler(PacketType paramPacketType, Set<ListenerOptions> paramSet);
/*    */ 
/*    */   public abstract void removePacketHandler(PacketType paramPacketType);
/*    */ 
/*    */   public abstract Player getPlayerByConnection(DataInputStream paramDataInputStream)
/*    */     throws InterruptedException;
/*    */ 
/*    */   public abstract void injectPlayer(Player paramPlayer, ConflictStrategy paramConflictStrategy);
/*    */ 
/*    */   public abstract void handleDisconnect(Player paramPlayer);
/*    */ 
/*    */   public abstract boolean uninjectPlayer(Player paramPlayer);
/*    */ 
/*    */   public abstract boolean uninjectPlayer(InetSocketAddress paramInetSocketAddress);
/*    */ 
/*    */   public abstract void sendServerPacket(Player paramPlayer, PacketContainer paramPacketContainer, NetworkMarker paramNetworkMarker, boolean paramBoolean)
/*    */     throws InvocationTargetException;
/*    */ 
/*    */   public abstract void recieveClientPacket(Player paramPlayer, Object paramObject)
/*    */     throws IllegalAccessException, InvocationTargetException;
/*    */ 
/*    */   public abstract void updatePlayer(Player paramPlayer);
/*    */ 
/*    */   public abstract void checkListener(Set<PacketListener> paramSet);
/*    */ 
/*    */   public abstract void checkListener(PacketListener paramPacketListener);
/*    */ 
/*    */   public abstract Set<PacketType> getSendingFilters();
/*    */ 
/*    */   public abstract boolean canRecievePackets();
/*    */ 
/*    */   public abstract PacketEvent handlePacketRecieved(PacketContainer paramPacketContainer, InputStream paramInputStream, byte[] paramArrayOfByte);
/*    */ 
/*    */   public abstract void close();
/*    */ 
/*    */   public abstract boolean hasMainThreadListener(PacketType paramPacketType);
/*    */ 
/*    */   public abstract Channel getChannel(Player paramPlayer);
/*    */ 
/*    */   public static enum ConflictStrategy
/*    */   {
/* 32 */     OVERRIDE, 
/*    */ 
/* 37 */     BAIL_OUT;
/*    */   }
/*    */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.injector.player.PlayerInjectionHandler
 * JD-Core Version:    0.6.2
 */