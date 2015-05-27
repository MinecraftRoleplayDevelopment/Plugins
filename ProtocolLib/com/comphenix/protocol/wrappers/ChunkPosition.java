/*     */ package com.comphenix.protocol.wrappers;
/*     */ 
/*     */ import com.comphenix.protocol.reflect.EquivalentConverter;
/*     */ import com.comphenix.protocol.reflect.FieldAccessException;
/*     */ import com.comphenix.protocol.reflect.StructureModifier;
/*     */ import com.comphenix.protocol.utility.MinecraftReflection;
/*     */ import com.google.common.base.Objects;
/*     */ import java.lang.reflect.Constructor;
/*     */ import org.bukkit.util.Vector;
/*     */ 
/*     */ public class ChunkPosition
/*     */ {
/*  40 */   public static ChunkPosition ORIGIN = new ChunkPosition(0, 0, 0);
/*     */   private static Constructor<?> chunkPositionConstructor;
/*     */   protected final int x;
/*     */   protected final int y;
/*     */   protected final int z;
/*     */   private static StructureModifier<Integer> intModifier;
/*     */ 
/*     */   public ChunkPosition(int x, int y, int z)
/*     */   {
/*  56 */     this.x = x;
/*  57 */     this.y = y;
/*  58 */     this.z = z;
/*     */   }
/*     */ 
/*     */   public ChunkPosition(Vector vector)
/*     */   {
/*  66 */     if (vector == null)
/*  67 */       throw new IllegalArgumentException("Vector cannot be NULL.");
/*  68 */     this.x = vector.getBlockX();
/*  69 */     this.y = vector.getBlockY();
/*  70 */     this.z = vector.getBlockZ();
/*     */   }
/*     */ 
/*     */   public Vector toVector()
/*     */   {
/*  78 */     return new Vector(this.x, this.y, this.z);
/*     */   }
/*     */ 
/*     */   public int getX()
/*     */   {
/*  86 */     return this.x;
/*     */   }
/*     */ 
/*     */   public int getY()
/*     */   {
/*  94 */     return this.y;
/*     */   }
/*     */ 
/*     */   public int getZ()
/*     */   {
/* 102 */     return this.z;
/*     */   }
/*     */ 
/*     */   public ChunkPosition add(ChunkPosition other)
/*     */   {
/* 111 */     if (other == null)
/* 112 */       throw new IllegalArgumentException("other cannot be NULL");
/* 113 */     return new ChunkPosition(this.x + other.x, this.y + other.y, this.z + other.z);
/*     */   }
/*     */ 
/*     */   public ChunkPosition subtract(ChunkPosition other)
/*     */   {
/* 122 */     if (other == null)
/* 123 */       throw new IllegalArgumentException("other cannot be NULL");
/* 124 */     return new ChunkPosition(this.x - other.x, this.y - other.y, this.z - other.z);
/*     */   }
/*     */ 
/*     */   public ChunkPosition multiply(int factor)
/*     */   {
/* 133 */     return new ChunkPosition(this.x * factor, this.y * factor, this.z * factor);
/*     */   }
/*     */ 
/*     */   public ChunkPosition divide(int divisor)
/*     */   {
/* 142 */     if (divisor == 0)
/* 143 */       throw new IllegalArgumentException("Cannot divide by null.");
/* 144 */     return new ChunkPosition(this.x / divisor, this.y / divisor, this.z / divisor);
/*     */   }
/*     */ 
/*     */   public static EquivalentConverter<ChunkPosition> getConverter()
/*     */   {
/* 152 */     return new EquivalentConverter()
/*     */     {
/*     */       public Object getGeneric(Class<?> genericType, ChunkPosition specific) {
/* 155 */         if (ChunkPosition.chunkPositionConstructor == null) {
/*     */           try {
/* 157 */             ChunkPosition.access$002(MinecraftReflection.getChunkPositionClass().getConstructor(new Class[] { Integer.TYPE, Integer.TYPE, Integer.TYPE }));
/*     */           }
/*     */           catch (Exception e) {
/* 160 */             throw new RuntimeException("Cannot find chunk position constructor.", e);
/*     */           }
/*     */         }
/*     */ 
/*     */         try
/*     */         {
/* 166 */           return ChunkPosition.chunkPositionConstructor.newInstance(new Object[] { Integer.valueOf(specific.x), Integer.valueOf(specific.y), Integer.valueOf(specific.z) });
/*     */         }
/*     */         catch (Exception e) {
/* 169 */           throw new RuntimeException("Cannot construct ChunkPosition.", e);
/*     */         }
/*     */       }
/*     */ 
/*     */       public ChunkPosition getSpecific(Object generic)
/*     */       {
/* 175 */         if (MinecraftReflection.isChunkPosition(generic))
/*     */         {
/* 177 */           ChunkPosition.access$102(new StructureModifier(generic.getClass(), null, false).withType(Integer.TYPE));
/*     */ 
/* 180 */           if (ChunkPosition.intModifier.size() < 3) {
/* 181 */             throw new IllegalStateException("Cannot read class " + generic.getClass() + " for its integer fields.");
/*     */           }
/*     */ 
/* 184 */           if (ChunkPosition.intModifier.size() >= 3) {
/*     */             try {
/* 186 */               StructureModifier instance = ChunkPosition.intModifier.withTarget(generic);
/* 187 */               return new ChunkPosition(((Integer)instance.read(0)).intValue(), ((Integer)instance.read(1)).intValue(), ((Integer)instance.read(2)).intValue());
/*     */             }
/*     */             catch (FieldAccessException e)
/*     */             {
/* 191 */               throw new RuntimeException("Field access error.", e);
/*     */             }
/*     */           }
/*     */ 
/*     */         }
/*     */ 
/* 197 */         return null;
/*     */       }
/*     */ 
/*     */       public Class<ChunkPosition> getSpecificType()
/*     */       {
/* 203 */         return ChunkPosition.class;
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   public boolean equals(Object obj)
/*     */   {
/* 211 */     if (this == obj) return true;
/* 212 */     if (obj == null) return false;
/*     */ 
/* 215 */     if ((obj instanceof ChunkPosition)) {
/* 216 */       ChunkPosition other = (ChunkPosition)obj;
/* 217 */       return (this.x == other.x) && (this.y == other.y) && (this.z == other.z);
/*     */     }
/* 219 */     return false;
/*     */   }
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 224 */     return Objects.hashCode(new Object[] { Integer.valueOf(this.x), Integer.valueOf(this.y), Integer.valueOf(this.z) });
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 229 */     return "WrappedChunkPosition [x=" + this.x + ", y=" + this.y + ", z=" + this.z + "]";
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.wrappers.ChunkPosition
 * JD-Core Version:    0.6.2
 */