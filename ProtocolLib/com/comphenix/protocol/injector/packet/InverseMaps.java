/*    */ package com.comphenix.protocol.injector.packet;
/*    */ 
/*    */ import com.google.common.base.Predicate;
/*    */ import com.google.common.collect.ForwardingMap;
/*    */ import com.google.common.collect.ForwardingMultimap;
/*    */ import com.google.common.collect.HashMultimap;
/*    */ import com.google.common.collect.Maps;
/*    */ import com.google.common.collect.Multimap;
/*    */ import java.util.Map;
/*    */ import java.util.Map.Entry;
/*    */ 
/*    */ public class InverseMaps
/*    */ {
/*    */   public static <K, V> Multimap<K, V> inverseMultimap(final Map<V, K> map, final Predicate<Map.Entry<V, K>> filter)
/*    */   {
/* 18 */     MapContainer container = new MapContainer(map);
/*    */ 
/* 20 */     return new ForwardingMultimap()
/*    */     {
/*    */       private Multimap<K, V> inverseMultimap;
/*    */ 
/*    */       protected Multimap<K, V> delegate()
/*    */       {
/* 26 */         if (this.val$container.hasChanged()) {
/* 27 */           this.inverseMultimap = HashMultimap.create();
/*    */ 
/* 30 */           for (Map.Entry entry : map.entrySet()) {
/* 31 */             if (filter.apply(entry)) {
/* 32 */               this.inverseMultimap.put(entry.getValue(), entry.getKey());
/*    */             }
/*    */           }
/* 35 */           this.val$container.setChanged(false);
/*    */         }
/* 37 */         return this.inverseMultimap;
/*    */       }
/*    */     };
/*    */   }
/*    */ 
/*    */   public static <K, V> Map<K, V> inverseMap(final Map<V, K> map, final Predicate<Map.Entry<V, K>> filter) {
/* 43 */     MapContainer container = new MapContainer(map);
/*    */ 
/* 45 */     return new ForwardingMap()
/*    */     {
/*    */       private Map<K, V> inverseMap;
/*    */ 
/*    */       protected Map<K, V> delegate()
/*    */       {
/* 51 */         if (this.val$container.hasChanged()) {
/* 52 */           this.inverseMap = Maps.newHashMap();
/*    */ 
/* 55 */           for (Map.Entry entry : map.entrySet()) {
/* 56 */             if (filter.apply(entry)) {
/* 57 */               this.inverseMap.put(entry.getValue(), entry.getKey());
/*    */             }
/*    */           }
/* 60 */           this.val$container.setChanged(false);
/*    */         }
/* 62 */         return this.inverseMap;
/*    */       }
/*    */     };
/*    */   }
/*    */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.injector.packet.InverseMaps
 * JD-Core Version:    0.6.2
 */