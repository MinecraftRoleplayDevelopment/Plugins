/*    */ package com.comphenix.protocol.metrics;
/*    */ 
/*    */ import com.comphenix.protocol.ProtocolLibrary;
/*    */ import com.comphenix.protocol.ProtocolManager;
/*    */ import com.comphenix.protocol.events.PacketAdapter;
/*    */ import com.comphenix.protocol.events.PacketListener;
/*    */ import java.io.IOException;
/*    */ import java.util.HashMap;
/*    */ import java.util.Map;
/*    */ import java.util.Map.Entry;
/*    */ import org.bukkit.plugin.Plugin;
/*    */ 
/*    */ public class Statistics
/*    */ {
/*    */   private Metrics metrics;
/*    */ 
/*    */   public Statistics(Plugin plugin)
/*    */     throws IOException
/*    */   {
/* 38 */     this.metrics = new Metrics(plugin);
/*    */ 
/* 41 */     addPluginUserGraph(this.metrics);
/*    */ 
/* 43 */     this.metrics.start();
/*    */   }
/*    */ 
/*    */   private void addPluginUserGraph(Metrics metrics)
/*    */   {
/* 48 */     Metrics.Graph pluginUsers = metrics.createGraph("Plugin Users");
/*    */ 
/* 50 */     for (Map.Entry entry : getPluginUsers(ProtocolLibrary.getProtocolManager()).entrySet()) {
/* 51 */       final int count = ((Integer)entry.getValue()).intValue();
/*    */ 
/* 54 */       pluginUsers.addPlotter(new Metrics.Plotter((String)entry.getKey())
/*    */       {
/*    */         public int getValue() {
/* 57 */           return count;
/*    */         }
/*    */       });
/*    */     }
/*    */   }
/*    */ 
/*    */   private Map<String, Integer> getPluginUsers(ProtocolManager manager)
/*    */   {
/* 66 */     Map users = new HashMap();
/*    */ 
/* 68 */     for (PacketListener listener : manager.getPacketListeners())
/*    */     {
/* 70 */       String name = PacketAdapter.getPluginName(listener);
/*    */ 
/* 73 */       if (!users.containsKey(name))
/* 74 */         users.put(name, Integer.valueOf(1));
/*    */       else {
/* 76 */         users.put(name, Integer.valueOf(((Integer)users.get(name)).intValue() + 1));
/*    */       }
/*    */     }
/*    */ 
/* 80 */     return users;
/*    */   }
/*    */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.metrics.Statistics
 * JD-Core Version:    0.6.2
 */