/*    */ package com.comphenix.protocol.events;
/*    */ 
/*    */ import com.comphenix.protocol.Packets.Client;
/*    */ import com.comphenix.protocol.Packets.Server;
/*    */ import com.comphenix.protocol.injector.GamePhase;
/*    */ import com.comphenix.protocol.injector.packet.PacketRegistry;
/*    */ import com.comphenix.protocol.reflect.FieldAccessException;
/*    */ import java.util.logging.Level;
/*    */ import java.util.logging.Logger;
/*    */ import org.bukkit.plugin.Plugin;
/*    */ 
/*    */ public abstract class MonitorAdapter
/*    */   implements PacketListener
/*    */ {
/*    */   private Plugin plugin;
/* 38 */   private ListeningWhitelist sending = ListeningWhitelist.EMPTY_WHITELIST;
/* 39 */   private ListeningWhitelist receiving = ListeningWhitelist.EMPTY_WHITELIST;
/*    */ 
/*    */   public MonitorAdapter(Plugin plugin, ConnectionSide side) {
/* 42 */     initialize(plugin, side, getLogger(plugin));
/*    */   }
/*    */ 
/*    */   public MonitorAdapter(Plugin plugin, ConnectionSide side, Logger logger) {
/* 46 */     initialize(plugin, side, logger);
/*    */   }
/*    */ 
/*    */   private void initialize(Plugin plugin, ConnectionSide side, Logger logger)
/*    */   {
/* 51 */     this.plugin = plugin;
/*    */     try
/*    */     {
/* 55 */       if (side.isForServer())
/* 56 */         this.sending = ListeningWhitelist.newBuilder().monitor().types(PacketRegistry.getServerPacketTypes()).gamePhaseBoth().build();
/* 57 */       if (side.isForClient())
/* 58 */         this.receiving = ListeningWhitelist.newBuilder().monitor().types(PacketRegistry.getClientPacketTypes()).gamePhaseBoth().build();
/*    */     } catch (FieldAccessException e) {
/* 60 */       if (side.isForServer())
/* 61 */         this.sending = new ListeningWhitelist(ListenerPriority.MONITOR, Packets.Server.getRegistry().values(), GamePhase.BOTH);
/* 62 */       if (side.isForClient())
/* 63 */         this.receiving = new ListeningWhitelist(ListenerPriority.MONITOR, Packets.Client.getRegistry().values(), GamePhase.BOTH);
/* 64 */       logger.log(Level.WARNING, "Defaulting to 1.3 packets.", e);
/*    */     }
/*    */   }
/*    */ 
/*    */   private Logger getLogger(Plugin plugin)
/*    */   {
/*    */     try
/*    */     {
/* 75 */       return plugin.getLogger(); } catch (NoSuchMethodError e) {
/*    */     }
/* 77 */     return Logger.getLogger("Minecraft");
/*    */   }
/*    */ 
/*    */   public ListeningWhitelist getSendingWhitelist()
/*    */   {
/* 83 */     return this.sending;
/*    */   }
/*    */ 
/*    */   public ListeningWhitelist getReceivingWhitelist()
/*    */   {
/* 88 */     return this.receiving;
/*    */   }
/*    */ 
/*    */   public Plugin getPlugin()
/*    */   {
/* 93 */     return this.plugin;
/*    */   }
/*    */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.events.MonitorAdapter
 * JD-Core Version:    0.6.2
 */