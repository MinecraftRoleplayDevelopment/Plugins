/*    */ package com.comphenix.protocol.injector.server;
/*    */ 
/*    */ import com.comphenix.protocol.events.NetworkMarker;
/*    */ import java.lang.reflect.InvocationTargetException;
/*    */ import java.net.Socket;
/*    */ import java.net.SocketAddress;
/*    */ import java.util.ArrayList;
/*    */ import java.util.Collections;
/*    */ import java.util.List;
/*    */ import org.bukkit.entity.Player;
/*    */ 
/*    */ public class BukkitSocketInjector
/*    */   implements SocketInjector
/*    */ {
/*    */   private Player player;
/* 18 */   private List<QueuedSendPacket> syncronizedQueue = Collections.synchronizedList(new ArrayList());
/*    */ 
/*    */   public BukkitSocketInjector(Player player)
/*    */   {
/* 25 */     if (player == null)
/* 26 */       throw new IllegalArgumentException("Player cannot be NULL.");
/* 27 */     this.player = player;
/*    */   }
/*    */ 
/*    */   public Socket getSocket() throws IllegalAccessException
/*    */   {
/* 32 */     throw new UnsupportedOperationException("Cannot get socket from Bukkit player.");
/*    */   }
/*    */ 
/*    */   public SocketAddress getAddress() throws IllegalAccessException
/*    */   {
/* 37 */     return this.player.getAddress();
/*    */   }
/*    */ 
/*    */   public void disconnect(String message) throws InvocationTargetException
/*    */   {
/* 42 */     this.player.kickPlayer(message);
/*    */   }
/*    */ 
/*    */   public void sendServerPacket(Object packet, NetworkMarker marker, boolean filtered)
/*    */     throws InvocationTargetException
/*    */   {
/* 48 */     QueuedSendPacket command = new QueuedSendPacket(packet, marker, filtered);
/*    */ 
/* 51 */     this.syncronizedQueue.add(command);
/*    */   }
/*    */ 
/*    */   public Player getPlayer()
/*    */   {
/* 56 */     return this.player;
/*    */   }
/*    */ 
/*    */   public Player getUpdatedPlayer()
/*    */   {
/* 61 */     return this.player;
/*    */   }
/*    */ 
/*    */   public void transferState(SocketInjector delegate)
/*    */   {
/*    */     try
/*    */     {
/* 68 */       synchronized (this.syncronizedQueue) {
/* 69 */         for (QueuedSendPacket command : this.syncronizedQueue) {
/* 70 */           delegate.sendServerPacket(command.getPacket(), command.getMarker(), command.isFiltered());
/*    */         }
/* 72 */         this.syncronizedQueue.clear();
/*    */       }
/*    */     } catch (InvocationTargetException e) {
/* 75 */       throw new RuntimeException("Unable to transmit packets to " + delegate + " from old injector.", e);
/*    */     }
/*    */   }
/*    */ 
/*    */   public void setUpdatedPlayer(Player updatedPlayer)
/*    */   {
/* 81 */     this.player = updatedPlayer;
/*    */   }
/*    */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.injector.server.BukkitSocketInjector
 * JD-Core Version:    0.6.2
 */