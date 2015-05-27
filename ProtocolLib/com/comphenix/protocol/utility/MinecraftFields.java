/*    */ package com.comphenix.protocol.utility;
/*    */ 
/*    */ import com.comphenix.protocol.injector.BukkitUnwrapper;
/*    */ import com.comphenix.protocol.reflect.accessors.Accessors;
/*    */ import com.comphenix.protocol.reflect.accessors.FieldAccessor;
/*    */ import org.bukkit.entity.Player;
/*    */ 
/*    */ public class MinecraftFields
/*    */ {
/*    */   private static volatile FieldAccessor CONNECTION_ACCESSOR;
/*    */   private static volatile FieldAccessor NETWORK_ACCESSOR;
/*    */ 
/*    */   public static Object getNetworkManager(Player player)
/*    */   {
/* 28 */     Object nmsPlayer = BukkitUnwrapper.getInstance().unwrapItem(player);
/*    */ 
/* 30 */     if (NETWORK_ACCESSOR == null) {
/* 31 */       Class networkClass = MinecraftReflection.getNetworkManagerClass();
/* 32 */       Class connectionClass = MinecraftReflection.getNetServerHandlerClass();
/* 33 */       NETWORK_ACCESSOR = Accessors.getFieldAccessor(connectionClass, networkClass, true);
/*    */     }
/*    */ 
/* 36 */     Object playerConnection = getPlayerConnection(nmsPlayer);
/*    */ 
/* 38 */     if (playerConnection != null)
/* 39 */       return NETWORK_ACCESSOR.get(playerConnection);
/* 40 */     return null;
/*    */   }
/*    */ 
/*    */   public static Object getPlayerConnection(Player player)
/*    */   {
/* 49 */     return getPlayerConnection(BukkitUnwrapper.getInstance().unwrapItem(player));
/*    */   }
/*    */ 
/*    */   private static Object getPlayerConnection(Object nmsPlayer)
/*    */   {
/* 54 */     if (CONNECTION_ACCESSOR == null) {
/* 55 */       Class connectionClass = MinecraftReflection.getNetServerHandlerClass();
/* 56 */       CONNECTION_ACCESSOR = Accessors.getFieldAccessor(nmsPlayer.getClass(), connectionClass, true);
/*    */     }
/* 58 */     return CONNECTION_ACCESSOR.get(nmsPlayer);
/*    */   }
/*    */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.utility.MinecraftFields
 * JD-Core Version:    0.6.2
 */