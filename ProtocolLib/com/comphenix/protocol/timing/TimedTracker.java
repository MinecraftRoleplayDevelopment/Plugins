/*    */ package com.comphenix.protocol.timing;
/*    */ 
/*    */ import com.comphenix.protocol.PacketType;
/*    */ import com.google.common.collect.Maps;
/*    */ import java.util.Map;
/*    */ import java.util.Map.Entry;
/*    */ 
/*    */ public class TimedTracker
/*    */ {
/* 15 */   private Map<PacketType, StatisticsStream> packets = Maps.newHashMap();
/*    */   private int observations;
/*    */ 
/*    */   public long beginTracking()
/*    */   {
/* 23 */     return System.nanoTime();
/*    */   }
/*    */ 
/*    */   public synchronized void endTracking(long trackingToken, PacketType type)
/*    */   {
/* 32 */     StatisticsStream stream = (StatisticsStream)this.packets.get(type);
/*    */ 
/* 35 */     if (stream == null) {
/* 36 */       this.packets.put(type, stream = new StatisticsStream());
/*    */     }
/*    */ 
/* 39 */     stream.observe(System.nanoTime() - trackingToken);
/* 40 */     this.observations += 1;
/*    */   }
/*    */ 
/*    */   public int getObservations()
/*    */   {
/* 48 */     return this.observations;
/*    */   }
/*    */ 
/*    */   public synchronized Map<PacketType, StatisticsStream> getStatistics()
/*    */   {
/* 56 */     Map clone = Maps.newHashMap();
/*    */ 
/* 58 */     for (Map.Entry entry : this.packets.entrySet()) {
/* 59 */       clone.put(entry.getKey(), new StatisticsStream((StatisticsStream)entry.getValue()));
/*    */     }
/*    */ 
/* 64 */     return clone;
/*    */   }
/*    */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.timing.TimedTracker
 * JD-Core Version:    0.6.2
 */