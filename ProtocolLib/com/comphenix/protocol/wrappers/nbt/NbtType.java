/*     */ package com.comphenix.protocol.wrappers.nbt;
/*     */ 
/*     */ import com.google.common.primitives.Primitives;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ 
/*     */ public enum NbtType
/*     */ {
/*  35 */   TAG_END(0, Void.class), 
/*     */ 
/*  40 */   TAG_BYTE(1, Byte.TYPE), 
/*     */ 
/*  45 */   TAG_SHORT(2, Short.TYPE), 
/*     */ 
/*  50 */   TAG_INT(3, Integer.TYPE), 
/*     */ 
/*  55 */   TAG_LONG(4, Long.TYPE), 
/*     */ 
/*  60 */   TAG_FLOAT(5, Float.TYPE), 
/*     */ 
/*  65 */   TAG_DOUBLE(6, Double.TYPE), 
/*     */ 
/*  70 */   TAG_BYTE_ARRAY(7, [B.class), 
/*     */ 
/*  75 */   TAG_INT_ARRAY(11, [I.class), 
/*     */ 
/*  80 */   TAG_STRING(8, String.class), 
/*     */ 
/*  85 */   TAG_LIST(9, List.class), 
/*     */ 
/*  90 */   TAG_COMPOUND(10, Map.class);
/*     */ 
/*     */   private int rawID;
/*     */   private Class<?> valueType;
/*     */   private static NbtType[] lookup;
/*     */   private static Map<Class<?>, NbtType> classLookup;
/*     */ 
/*     */   private NbtType(int rawID, Class<?> valueType)
/*     */   {
/* 123 */     this.rawID = rawID;
/* 124 */     this.valueType = valueType;
/*     */   }
/*     */ 
/*     */   public boolean isComposite()
/*     */   {
/* 132 */     return (this == TAG_COMPOUND) || (this == TAG_LIST);
/*     */   }
/*     */ 
/*     */   public int getRawID()
/*     */   {
/* 140 */     return this.rawID;
/*     */   }
/*     */ 
/*     */   public Class<?> getValueType()
/*     */   {
/* 148 */     return this.valueType;
/*     */   }
/*     */ 
/*     */   public static NbtType getTypeFromID(int rawID)
/*     */   {
/* 157 */     if ((rawID < 0) || (rawID >= lookup.length))
/* 158 */       throw new IllegalArgumentException("Unrecognized raw ID " + rawID);
/* 159 */     return lookup[rawID];
/*     */   }
/*     */ 
/*     */   public static NbtType getTypeFromClass(Class<?> clazz)
/*     */   {
/* 169 */     NbtType result = (NbtType)classLookup.get(clazz);
/*     */ 
/* 172 */     if (result != null) {
/* 173 */       return result;
/*     */     }
/*     */ 
/* 176 */     for (Class implemented : clazz.getInterfaces()) {
/* 177 */       if (classLookup.containsKey(implemented)) {
/* 178 */         return (NbtType)classLookup.get(implemented);
/*     */       }
/*     */     }
/* 181 */     throw new IllegalArgumentException("No NBT tag can represent a " + clazz);
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/* 102 */     NbtType[] values = values();
/* 103 */     lookup = new NbtType[values.length];
/* 104 */     classLookup = new HashMap();
/*     */ 
/* 107 */     for (NbtType type : values) {
/* 108 */       lookup[type.getRawID()] = type;
/* 109 */       classLookup.put(type.getValueType(), type);
/*     */ 
/* 112 */       if (type.getValueType().isPrimitive()) {
/* 113 */         classLookup.put(Primitives.wrap(type.getValueType()), type);
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 118 */     classLookup.put(NbtList.class, TAG_LIST);
/* 119 */     classLookup.put(NbtCompound.class, TAG_COMPOUND);
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.wrappers.nbt.NbtType
 * JD-Core Version:    0.6.2
 */