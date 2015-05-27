/*     */ package com.comphenix.protocol.reflect.cloning;
/*     */ 
/*     */ import com.comphenix.protocol.reflect.EquivalentConverter;
/*     */ import com.comphenix.protocol.utility.MinecraftReflection;
/*     */ import com.comphenix.protocol.wrappers.BlockPosition;
/*     */ import com.comphenix.protocol.wrappers.BukkitConverters;
/*     */ import com.comphenix.protocol.wrappers.ChunkPosition;
/*     */ import com.comphenix.protocol.wrappers.WrappedDataWatcher;
/*     */ import com.comphenix.protocol.wrappers.WrappedServerPing;
/*     */ import com.google.common.collect.Maps;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import org.bukkit.inventory.ItemStack;
/*     */ 
/*     */ public class BukkitCloner
/*     */   implements Cloner
/*     */ {
/*  38 */   private final Map<Integer, Class<?>> clonableClasses = Maps.newConcurrentMap();
/*     */ 
/*     */   public BukkitCloner() {
/*  41 */     addClass(0, MinecraftReflection.getItemStackClass());
/*  42 */     addClass(1, MinecraftReflection.getDataWatcherClass());
/*     */     try
/*     */     {
/*  46 */       addClass(2, MinecraftReflection.getBlockPositionClass());
/*     */     }
/*     */     catch (Throwable ex) {
/*     */     }
/*     */     try {
/*  51 */       addClass(3, MinecraftReflection.getChunkPositionClass());
/*     */     }
/*     */     catch (Throwable ex) {
/*     */     }
/*  55 */     if (MinecraftReflection.isUsingNetty())
/*  56 */       addClass(4, MinecraftReflection.getServerPingClass());
/*     */   }
/*     */ 
/*     */   private void addClass(int id, Class<?> clazz)
/*     */   {
/*  61 */     if (clazz != null)
/*  62 */       this.clonableClasses.put(Integer.valueOf(id), clazz);
/*     */   }
/*     */ 
/*     */   private int findMatchingClass(Class<?> type)
/*     */   {
/*  67 */     for (Map.Entry entry : this.clonableClasses.entrySet()) {
/*  68 */       if (((Class)entry.getValue()).isAssignableFrom(type)) {
/*  69 */         return ((Integer)entry.getKey()).intValue();
/*     */       }
/*     */     }
/*     */ 
/*  73 */     return -1;
/*     */   }
/*     */ 
/*     */   public boolean canClone(Object source)
/*     */   {
/*  78 */     if (source == null) {
/*  79 */       return false;
/*     */     }
/*  81 */     return findMatchingClass(source.getClass()) >= 0;
/*     */   }
/*     */ 
/*     */   public Object clone(Object source)
/*     */   {
/*  86 */     if (source == null) {
/*  87 */       throw new IllegalArgumentException("source cannot be NULL.");
/*     */     }
/*     */ 
/*  90 */     switch (findMatchingClass(source.getClass())) {
/*     */     case 0:
/*  92 */       return MinecraftReflection.getMinecraftItemStack(MinecraftReflection.getBukkitItemStack(source).clone());
/*     */     case 1:
/*  94 */       EquivalentConverter dataConverter = BukkitConverters.getDataWatcherConverter();
/*  95 */       return dataConverter.getGeneric((Class)this.clonableClasses.get(Integer.valueOf(1)), ((WrappedDataWatcher)dataConverter.getSpecific(source)).deepClone());
/*     */     case 2:
/*  97 */       EquivalentConverter blockConverter = BlockPosition.getConverter();
/*  98 */       return blockConverter.getGeneric((Class)this.clonableClasses.get(Integer.valueOf(2)), blockConverter.getSpecific(source));
/*     */     case 3:
/* 100 */       EquivalentConverter chunkConverter = ChunkPosition.getConverter();
/* 101 */       return chunkConverter.getGeneric((Class)this.clonableClasses.get(Integer.valueOf(3)), chunkConverter.getSpecific(source));
/*     */     case 4:
/* 103 */       EquivalentConverter serverConverter = BukkitConverters.getWrappedServerPingConverter();
/* 104 */       return serverConverter.getGeneric((Class)this.clonableClasses.get(Integer.valueOf(4)), ((WrappedServerPing)serverConverter.getSpecific(source)).deepClone());
/*     */     }
/* 106 */     throw new IllegalArgumentException("Cannot clone objects of type " + source.getClass());
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.reflect.cloning.BukkitCloner
 * JD-Core Version:    0.6.2
 */