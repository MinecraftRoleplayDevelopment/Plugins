/*     */ package com.comphenix.protocol.wrappers;
/*     */ 
/*     */ import com.comphenix.protocol.reflect.StructureModifier;
/*     */ import com.comphenix.protocol.utility.MinecraftReflection;
/*     */ import com.google.common.base.Objects;
/*     */ 
/*     */ public class WrappedChunkCoordinate extends AbstractWrapper
/*     */   implements Comparable<WrappedChunkCoordinate>
/*     */ {
/*     */   private static final boolean LARGER_THAN_NULL = true;
/*     */   private static StructureModifier<Integer> SHARED_MODIFIER;
/*     */   private StructureModifier<Integer> handleModifier;
/*     */ 
/*     */   public WrappedChunkCoordinate()
/*     */   {
/*  45 */     super(MinecraftReflection.getChunkCoordinatesClass());
/*     */     try
/*     */     {
/*  48 */       setHandle(getHandleType().newInstance());
/*     */     } catch (Exception e) {
/*  50 */       throw new RuntimeException("Cannot construct chunk coordinate.");
/*     */     }
/*     */   }
/*     */ 
/*     */   public WrappedChunkCoordinate(Comparable handle)
/*     */   {
/*  60 */     super(MinecraftReflection.getChunkCoordinatesClass());
/*  61 */     setHandle(handle);
/*     */   }
/*     */ 
/*     */   private StructureModifier<Integer> getModifier()
/*     */   {
/*  66 */     if (SHARED_MODIFIER == null)
/*  67 */       SHARED_MODIFIER = new StructureModifier(this.handle.getClass(), null, false).withType(Integer.TYPE);
/*  68 */     if (this.handleModifier == null)
/*  69 */       this.handleModifier = SHARED_MODIFIER.withTarget(this.handle);
/*  70 */     return this.handleModifier;
/*     */   }
/*     */ 
/*     */   public WrappedChunkCoordinate(int x, int y, int z)
/*     */   {
/*  80 */     this();
/*  81 */     setX(x);
/*  82 */     setY(y);
/*  83 */     setZ(z);
/*     */   }
/*     */ 
/*     */   public WrappedChunkCoordinate(ChunkPosition position)
/*     */   {
/*  91 */     this(position.getX(), position.getY(), position.getZ());
/*     */   }
/*     */ 
/*     */   public Object getHandle() {
/*  95 */     return this.handle;
/*     */   }
/*     */ 
/*     */   public int getX()
/*     */   {
/* 103 */     return ((Integer)getModifier().read(0)).intValue();
/*     */   }
/*     */ 
/*     */   public void setX(int newX)
/*     */   {
/* 111 */     getModifier().write(0, Integer.valueOf(newX));
/*     */   }
/*     */ 
/*     */   public int getY()
/*     */   {
/* 119 */     return ((Integer)getModifier().read(1)).intValue();
/*     */   }
/*     */ 
/*     */   public void setY(int newY)
/*     */   {
/* 127 */     getModifier().write(1, Integer.valueOf(newY));
/*     */   }
/*     */ 
/*     */   public int getZ()
/*     */   {
/* 135 */     return ((Integer)getModifier().read(2)).intValue();
/*     */   }
/*     */ 
/*     */   public void setZ(int newZ)
/*     */   {
/* 143 */     getModifier().write(2, Integer.valueOf(newZ));
/*     */   }
/*     */ 
/*     */   public ChunkPosition toPosition()
/*     */   {
/* 151 */     return new ChunkPosition(getX(), getY(), getZ());
/*     */   }
/*     */ 
/*     */   public int compareTo(WrappedChunkCoordinate other)
/*     */   {
/* 158 */     if (other.handle == null) {
/* 159 */       return -1;
/*     */     }
/* 161 */     return ((Comparable)this.handle).compareTo(other.handle);
/*     */   }
/*     */ 
/*     */   public boolean equals(Object other)
/*     */   {
/* 166 */     if ((other instanceof WrappedChunkCoordinate)) {
/* 167 */       WrappedChunkCoordinate wrapper = (WrappedChunkCoordinate)other;
/* 168 */       return Objects.equal(this.handle, wrapper.handle);
/*     */     }
/*     */ 
/* 174 */     return false;
/*     */   }
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 179 */     return this.handle.hashCode();
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 184 */     return String.format("ChunkCoordinate [x: %s, y: %s, z: %s]", new Object[] { Integer.valueOf(getX()), Integer.valueOf(getY()), Integer.valueOf(getZ()) });
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.wrappers.WrappedChunkCoordinate
 * JD-Core Version:    0.6.2
 */