/*     */ package com.comphenix.protocol.wrappers;
/*     */ 
/*     */ import com.comphenix.protocol.reflect.EquivalentConverter;
/*     */ import com.comphenix.protocol.reflect.accessors.Accessors;
/*     */ import com.comphenix.protocol.reflect.accessors.ConstructorAccessor;
/*     */ import com.comphenix.protocol.reflect.accessors.FieldAccessor;
/*     */ import com.comphenix.protocol.utility.MinecraftReflection;
/*     */ import com.google.common.base.Objects;
/*     */ 
/*     */ public class ChunkCoordIntPair
/*     */ {
/*  15 */   private static Class<?> COORD_PAIR_CLASS = MinecraftReflection.getChunkCoordIntPair();
/*     */   private static ConstructorAccessor COORD_CONSTRUCTOR;
/*     */   private static FieldAccessor COORD_X;
/*     */   private static FieldAccessor COORD_Z;
/*     */   protected final int chunkX;
/*     */   protected final int chunkZ;
/*     */ 
/*     */   public ChunkCoordIntPair(int x, int z)
/*     */   {
/*  30 */     this.chunkX = x;
/*  31 */     this.chunkZ = z;
/*     */   }
/*     */ 
/*     */   public ChunkPosition getPosition(int y)
/*     */   {
/*  40 */     return new ChunkPosition((this.chunkX << 4) + 8, y, (this.chunkZ << 4) + 8);
/*     */   }
/*     */ 
/*     */   public int getChunkX()
/*     */   {
/*  50 */     return this.chunkX;
/*     */   }
/*     */ 
/*     */   public int getChunkZ()
/*     */   {
/*  60 */     return this.chunkZ;
/*     */   }
/*     */ 
/*     */   public static EquivalentConverter<ChunkCoordIntPair> getConverter()
/*     */   {
/*  68 */     return new EquivalentConverter()
/*     */     {
/*     */       public Object getGeneric(Class<?> genericType, ChunkCoordIntPair specific) {
/*  71 */         if (ChunkCoordIntPair.COORD_CONSTRUCTOR == null) {
/*  72 */           ChunkCoordIntPair.access$002(Accessors.getConstructorAccessor(ChunkCoordIntPair.COORD_PAIR_CLASS, new Class[] { Integer.TYPE, Integer.TYPE }));
/*     */         }
/*     */ 
/*  75 */         return ChunkCoordIntPair.COORD_CONSTRUCTOR.invoke(new Object[] { Integer.valueOf(specific.chunkX), Integer.valueOf(specific.chunkZ) });
/*     */       }
/*     */ 
/*     */       public ChunkCoordIntPair getSpecific(Object generic)
/*     */       {
/*  80 */         if (MinecraftReflection.isChunkCoordIntPair(generic)) {
/*  81 */           if ((ChunkCoordIntPair.COORD_X == null) || (ChunkCoordIntPair.COORD_Z == null)) {
/*  82 */             FieldAccessor[] ints = Accessors.getFieldAccessorArray(ChunkCoordIntPair.COORD_PAIR_CLASS, Integer.TYPE, true);
/*  83 */             ChunkCoordIntPair.access$202(ints[0]);
/*  84 */             ChunkCoordIntPair.access$302(ints[1]);
/*     */           }
/*  86 */           return new ChunkCoordIntPair(((Integer)ChunkCoordIntPair.COORD_X.get(generic)).intValue(), ((Integer)ChunkCoordIntPair.COORD_Z.get(generic)).intValue());
/*     */         }
/*     */ 
/*  90 */         return null;
/*     */       }
/*     */ 
/*     */       public Class<ChunkCoordIntPair> getSpecificType()
/*     */       {
/*  95 */         return ChunkCoordIntPair.class;
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   public boolean equals(Object obj)
/*     */   {
/* 102 */     if (this == obj) return true;
/*     */ 
/* 105 */     if ((obj instanceof ChunkCoordIntPair)) {
/* 106 */       ChunkCoordIntPair other = (ChunkCoordIntPair)obj;
/* 107 */       return (this.chunkX == other.chunkX) && (this.chunkZ == other.chunkZ);
/*     */     }
/* 109 */     return false;
/*     */   }
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 114 */     return Objects.hashCode(new Object[] { Integer.valueOf(this.chunkX), Integer.valueOf(this.chunkZ) });
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 119 */     return "ChunkCoordIntPair [x=" + this.chunkX + ", z=" + this.chunkZ + "]";
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.wrappers.ChunkCoordIntPair
 * JD-Core Version:    0.6.2
 */