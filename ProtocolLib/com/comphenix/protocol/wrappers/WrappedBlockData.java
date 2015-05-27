/*     */ package com.comphenix.protocol.wrappers;
/*     */ 
/*     */ import com.comphenix.protocol.reflect.EquivalentConverter;
/*     */ import com.comphenix.protocol.reflect.FuzzyReflection;
/*     */ import com.comphenix.protocol.reflect.accessors.Accessors;
/*     */ import com.comphenix.protocol.reflect.accessors.MethodAccessor;
/*     */ import com.comphenix.protocol.reflect.fuzzy.FuzzyMethodContract;
/*     */ import com.comphenix.protocol.reflect.fuzzy.FuzzyMethodContract.Builder;
/*     */ import com.comphenix.protocol.utility.MinecraftReflection;
/*     */ import org.bukkit.Material;
/*     */ 
/*     */ public class WrappedBlockData extends AbstractWrapper
/*     */ {
/*  36 */   private static final Class<?> MAGIC_NUMBERS = MinecraftReflection.getCraftBukkitClass("util.CraftMagicNumbers");
/*  37 */   private static final Class<?> IBLOCK_DATA = MinecraftReflection.getIBlockDataClass();
/*  38 */   private static final Class<?> BLOCK = MinecraftReflection.getBlockClass();
/*     */ 
/*  40 */   private static MethodAccessor FROM_LEGACY_DATA = null;
/*  41 */   private static MethodAccessor GET_NMS_BLOCK = null;
/*     */ 
/*  58 */   private static MethodAccessor GET_BLOCK = Accessors.getMethodAccessor(fuzzy.getMethodByParameters("getBlock", BLOCK, new Class[0]));
/*     */ 
/*     */   public WrappedBlockData(Object handle)
/*     */   {
/*  63 */     super(IBLOCK_DATA);
/*  64 */     setHandle(handle);
/*     */   }
/*     */ 
/*     */   public Material getType()
/*     */   {
/*  72 */     Object block = GET_BLOCK.invoke(this.handle, new Object[0]);
/*  73 */     return (Material)BukkitConverters.getBlockConverter().getSpecific(block);
/*     */   }
/*     */ 
/*     */   public void setType(Material type)
/*     */   {
/*  81 */     setTypeAndData(type, 0);
/*     */   }
/*     */ 
/*     */   public void setTypeAndData(Material type, int data)
/*     */   {
/*  90 */     Object nmsBlock = GET_NMS_BLOCK.invoke(null, new Object[] { type });
/*  91 */     Object blockData = FROM_LEGACY_DATA.invoke(nmsBlock, new Object[] { Integer.valueOf(data) });
/*  92 */     setHandle(blockData);
/*     */   }
/*     */ 
/*     */   public static WrappedBlockData createData(Material type)
/*     */   {
/* 101 */     return createData(type, 0);
/*     */   }
/*     */ 
/*     */   public static WrappedBlockData createData(Material type, int data)
/*     */   {
/* 111 */     Object nmsBlock = GET_NMS_BLOCK.invoke(null, new Object[] { type });
/* 112 */     Object blockData = FROM_LEGACY_DATA.invoke(nmsBlock, new Object[] { Integer.valueOf(data) });
/* 113 */     return new WrappedBlockData(blockData);
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 118 */     return "WrappedBlockData[handle=" + this.handle + "]";
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  45 */     FuzzyReflection fuzzy = FuzzyReflection.fromClass(BLOCK);
/*  46 */     FuzzyMethodContract contract = FuzzyMethodContract.newBuilder().banModifier(8).parameterExactArray(new Class[] { Integer.TYPE }).returnTypeExact(IBLOCK_DATA).build();
/*     */ 
/*  51 */     FROM_LEGACY_DATA = Accessors.getMethodAccessor(fuzzy.getMethod(contract));
/*     */ 
/*  53 */     fuzzy = FuzzyReflection.fromClass(MAGIC_NUMBERS);
/*  54 */     GET_NMS_BLOCK = Accessors.getMethodAccessor(fuzzy.getMethodByParameters("getBlock", BLOCK, new Class[] { Material.class }));
/*     */ 
/*  57 */     fuzzy = FuzzyReflection.fromClass(IBLOCK_DATA);
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.wrappers.WrappedBlockData
 * JD-Core Version:    0.6.2
 */