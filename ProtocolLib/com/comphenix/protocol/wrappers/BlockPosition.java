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
/*     */ public class BlockPosition
/*     */ {
/*  39 */   public static BlockPosition ORIGIN = new BlockPosition(0, 0, 0);
/*     */   private static Constructor<?> blockPositionConstructor;
/*     */   protected final int x;
/*     */   protected final int y;
/*     */   protected final int z;
/*     */   private static StructureModifier<Integer> intModifier;
/*     */ 
/*     */   public BlockPosition(int x, int y, int z)
/*     */   {
/*  55 */     this.x = x;
/*  56 */     this.y = y;
/*  57 */     this.z = z;
/*     */   }
/*     */ 
/*     */   public BlockPosition(Vector vector)
/*     */   {
/*  65 */     if (vector == null)
/*  66 */       throw new IllegalArgumentException("Vector cannot be NULL.");
/*  67 */     this.x = vector.getBlockX();
/*  68 */     this.y = vector.getBlockY();
/*  69 */     this.z = vector.getBlockZ();
/*     */   }
/*     */ 
/*     */   public Vector toVector()
/*     */   {
/*  77 */     return new Vector(this.x, this.y, this.z);
/*     */   }
/*     */ 
/*     */   public int getX()
/*     */   {
/*  85 */     return this.x;
/*     */   }
/*     */ 
/*     */   public int getY()
/*     */   {
/*  93 */     return this.y;
/*     */   }
/*     */ 
/*     */   public int getZ()
/*     */   {
/* 101 */     return this.z;
/*     */   }
/*     */ 
/*     */   public BlockPosition add(BlockPosition other)
/*     */   {
/* 110 */     if (other == null)
/* 111 */       throw new IllegalArgumentException("other cannot be NULL");
/* 112 */     return new BlockPosition(this.x + other.x, this.y + other.y, this.z + other.z);
/*     */   }
/*     */ 
/*     */   public BlockPosition subtract(BlockPosition other)
/*     */   {
/* 121 */     if (other == null)
/* 122 */       throw new IllegalArgumentException("other cannot be NULL");
/* 123 */     return new BlockPosition(this.x - other.x, this.y - other.y, this.z - other.z);
/*     */   }
/*     */ 
/*     */   public BlockPosition multiply(int factor)
/*     */   {
/* 132 */     return new BlockPosition(this.x * factor, this.y * factor, this.z * factor);
/*     */   }
/*     */ 
/*     */   public BlockPosition divide(int divisor)
/*     */   {
/* 141 */     if (divisor == 0)
/* 142 */       throw new IllegalArgumentException("Cannot divide by null.");
/* 143 */     return new BlockPosition(this.x / divisor, this.y / divisor, this.z / divisor);
/*     */   }
/*     */ 
/*     */   public static EquivalentConverter<BlockPosition> getConverter()
/*     */   {
/* 151 */     return new EquivalentConverter()
/*     */     {
/*     */       public Object getGeneric(Class<?> genericType, BlockPosition specific) {
/* 154 */         if (BlockPosition.blockPositionConstructor == null) {
/*     */           try {
/* 156 */             BlockPosition.access$002(MinecraftReflection.getBlockPositionClass().getConstructor(new Class[] { Integer.TYPE, Integer.TYPE, Integer.TYPE }));
/*     */           }
/*     */           catch (Exception e) {
/* 159 */             throw new RuntimeException("Cannot find block position constructor.", e);
/*     */           }
/*     */         }
/*     */ 
/*     */         try
/*     */         {
/* 165 */           return BlockPosition.blockPositionConstructor.newInstance(new Object[] { Integer.valueOf(specific.x), Integer.valueOf(specific.y), Integer.valueOf(specific.z) });
/*     */         }
/*     */         catch (Exception e) {
/* 168 */           throw new RuntimeException("Cannot construct BlockPosition.", e);
/*     */         }
/*     */       }
/*     */ 
/*     */       public BlockPosition getSpecific(Object generic)
/*     */       {
/* 174 */         if (MinecraftReflection.isBlockPosition(generic))
/*     */         {
/* 176 */           BlockPosition.access$102(new StructureModifier(generic.getClass(), null, false).withType(Integer.TYPE));
/*     */ 
/* 179 */           if (BlockPosition.intModifier.size() < 3) {
/* 180 */             throw new IllegalStateException("Cannot read class " + generic.getClass() + " for its integer fields.");
/*     */           }
/*     */ 
/* 183 */           if (BlockPosition.intModifier.size() >= 3) {
/*     */             try {
/* 185 */               StructureModifier instance = BlockPosition.intModifier.withTarget(generic);
/* 186 */               return new BlockPosition(((Integer)instance.read(0)).intValue(), ((Integer)instance.read(1)).intValue(), ((Integer)instance.read(2)).intValue());
/*     */             }
/*     */             catch (FieldAccessException e)
/*     */             {
/* 190 */               throw new RuntimeException("Field access error.", e);
/*     */             }
/*     */           }
/*     */ 
/*     */         }
/*     */ 
/* 196 */         return null;
/*     */       }
/*     */ 
/*     */       public Class<BlockPosition> getSpecificType()
/*     */       {
/* 202 */         return BlockPosition.class;
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   public boolean equals(Object obj)
/*     */   {
/* 210 */     if (this == obj) return true;
/* 211 */     if (obj == null) return false;
/*     */ 
/* 214 */     if ((obj instanceof BlockPosition)) {
/* 215 */       BlockPosition other = (BlockPosition)obj;
/* 216 */       return (this.x == other.x) && (this.y == other.y) && (this.z == other.z);
/*     */     }
/* 218 */     return false;
/*     */   }
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 223 */     return Objects.hashCode(new Object[] { Integer.valueOf(this.x), Integer.valueOf(this.y), Integer.valueOf(this.z) });
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 228 */     return "BlockPosition [x=" + this.x + ", y=" + this.y + ", z=" + this.z + "]";
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.wrappers.BlockPosition
 * JD-Core Version:    0.6.2
 */