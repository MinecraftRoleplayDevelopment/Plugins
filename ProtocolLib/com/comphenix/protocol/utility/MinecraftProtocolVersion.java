/*    */ package com.comphenix.protocol.utility;
/*    */ 
/*    */ import com.comphenix.protocol.ProtocolLibrary;
/*    */ import com.comphenix.protocol.ProtocolManager;
/*    */ import com.google.common.collect.Maps;
/*    */ import java.util.Map.Entry;
/*    */ import java.util.NavigableMap;
/*    */ import java.util.TreeMap;
/*    */ 
/*    */ public class MinecraftProtocolVersion
/*    */ {
/* 15 */   private static final NavigableMap<MinecraftVersion, Integer> lookup = createLookup();
/*    */ 
/*    */   private static NavigableMap<MinecraftVersion, Integer> createLookup() {
/* 18 */     TreeMap map = Maps.newTreeMap();
/*    */ 
/* 22 */     map.put(new MinecraftVersion(1, 0, 0), Integer.valueOf(22));
/* 23 */     map.put(new MinecraftVersion(1, 1, 0), Integer.valueOf(23));
/* 24 */     map.put(new MinecraftVersion(1, 2, 2), Integer.valueOf(28));
/* 25 */     map.put(new MinecraftVersion(1, 2, 4), Integer.valueOf(29));
/* 26 */     map.put(new MinecraftVersion(1, 3, 1), Integer.valueOf(39));
/* 27 */     map.put(new MinecraftVersion(1, 4, 2), Integer.valueOf(47));
/* 28 */     map.put(new MinecraftVersion(1, 4, 3), Integer.valueOf(48));
/* 29 */     map.put(new MinecraftVersion(1, 4, 4), Integer.valueOf(49));
/* 30 */     map.put(new MinecraftVersion(1, 4, 6), Integer.valueOf(51));
/* 31 */     map.put(new MinecraftVersion(1, 5, 0), Integer.valueOf(60));
/* 32 */     map.put(new MinecraftVersion(1, 5, 2), Integer.valueOf(61));
/* 33 */     map.put(new MinecraftVersion(1, 6, 0), Integer.valueOf(72));
/* 34 */     map.put(new MinecraftVersion(1, 6, 1), Integer.valueOf(73));
/* 35 */     map.put(new MinecraftVersion(1, 6, 2), Integer.valueOf(74));
/* 36 */     map.put(new MinecraftVersion(1, 6, 4), Integer.valueOf(78));
/*    */ 
/* 39 */     map.put(new MinecraftVersion(1, 7, 1), Integer.valueOf(4));
/* 40 */     map.put(new MinecraftVersion(1, 7, 6), Integer.valueOf(5));
/* 41 */     map.put(new MinecraftVersion(1, 8, 0), Integer.valueOf(47));
/* 42 */     return map;
/*    */   }
/*    */ 
/*    */   public static int getCurrentVersion()
/*    */   {
/* 50 */     return getVersion(ProtocolLibrary.getProtocolManager().getMinecraftVersion());
/*    */   }
/*    */ 
/*    */   public static int getVersion(MinecraftVersion version)
/*    */   {
/* 59 */     Map.Entry result = lookup.floorEntry(version);
/* 60 */     return result != null ? ((Integer)result.getValue()).intValue() : -2147483648;
/*    */   }
/*    */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.utility.MinecraftProtocolVersion
 * JD-Core Version:    0.6.2
 */