/*    */ package com.comphenix.protocol.injector;
/*    */ 
/*    */ import com.comphenix.protocol.PacketType;
/*    */ import com.comphenix.protocol.PacketType.Login.Client;
/*    */ import com.comphenix.protocol.PacketType.Login.Server;
/*    */ import com.comphenix.protocol.PacketType.Sender;
/*    */ import com.comphenix.protocol.PacketType.Status.Client;
/*    */ import com.comphenix.protocol.PacketType.Status.Server;
/*    */ import com.comphenix.protocol.concurrency.IntegerSet;
/*    */ import com.comphenix.protocol.events.ConnectionSide;
/*    */ import com.comphenix.protocol.utility.MinecraftReflection;
/*    */ import com.comphenix.protocol.utility.MinecraftVersion;
/*    */ import org.bukkit.Bukkit;
/*    */ import org.bukkit.Server;
/*    */ 
/*    */ class LoginPackets
/*    */ {
/* 19 */   private IntegerSet clientSide = new IntegerSet(256);
/* 20 */   private IntegerSet serverSide = new IntegerSet(256);
/*    */ 
/*    */   public LoginPackets(MinecraftVersion version)
/*    */   {
/* 25 */     this.clientSide.add(2);
/* 26 */     this.serverSide.add(253);
/* 27 */     this.clientSide.add(252);
/* 28 */     this.serverSide.add(252);
/* 29 */     this.clientSide.add(205);
/* 30 */     this.serverSide.add(1);
/*    */ 
/* 33 */     this.clientSide.add(254);
/*    */ 
/* 36 */     if (version.compareTo(MinecraftVersion.HORSE_UPDATE) >= 0) {
/* 37 */       this.clientSide.add(250);
/*    */     }
/* 39 */     this.serverSide.add(255);
/*    */ 
/* 42 */     if (isMCPC())
/* 43 */       this.clientSide.add(250);
/*    */   }
/*    */ 
/*    */   private static boolean isMCPC()
/*    */   {
/* 52 */     return Bukkit.getServer().getVersion().contains("MCPC-Plus");
/*    */   }
/*    */ 
/*    */   @Deprecated
/*    */   public boolean isLoginPacket(int packetId, ConnectionSide side)
/*    */   {
/* 63 */     switch (1.$SwitchMap$com$comphenix$protocol$events$ConnectionSide[side.ordinal()]) {
/*    */     case 1:
/* 65 */       return this.clientSide.contains(packetId);
/*    */     case 2:
/* 67 */       return this.serverSide.contains(packetId);
/*    */     case 3:
/* 69 */       return (this.clientSide.contains(packetId)) || (this.serverSide.contains(packetId));
/*    */     }
/*    */ 
/* 72 */     throw new IllegalArgumentException("Unknown connection side: " + side);
/*    */   }
/*    */ 
/*    */   public boolean isLoginPacket(PacketType type)
/*    */   {
/* 82 */     if (!MinecraftReflection.isUsingNetty()) {
/* 83 */       return isLoginPacket(type.getLegacyId(), type.getSender().toSide());
/*    */     }
/* 85 */     return (PacketType.Login.Client.getInstance().hasMember(type)) || (PacketType.Login.Server.getInstance().hasMember(type)) || (PacketType.Status.Client.getInstance().hasMember(type)) || (PacketType.Status.Server.getInstance().hasMember(type));
/*    */   }
/*    */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.injector.LoginPackets
 * JD-Core Version:    0.6.2
 */