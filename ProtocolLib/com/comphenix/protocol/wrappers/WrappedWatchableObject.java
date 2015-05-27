/*     */ package com.comphenix.protocol.wrappers;
/*     */ 
/*     */ import com.comphenix.protocol.reflect.EquivalentConverter;
/*     */ import com.comphenix.protocol.reflect.FieldAccessException;
/*     */ import com.comphenix.protocol.reflect.StructureModifier;
/*     */ import com.comphenix.protocol.reflect.instances.DefaultInstances;
/*     */ import com.comphenix.protocol.utility.MinecraftReflection;
/*     */ import com.google.common.base.Objects;
/*     */ import java.lang.reflect.Constructor;
/*     */ import org.bukkit.inventory.ItemStack;
/*     */ 
/*     */ public class WrappedWatchableObject extends AbstractWrapper
/*     */ {
/*     */   private static boolean hasInitialized;
/*     */   private static StructureModifier<Object> baseModifier;
/*     */   private static Constructor<?> watchableConstructor;
/*     */   private static Class<?> watchableObjectClass;
/*     */   protected StructureModifier<Object> modifier;
/*     */   private Class<?> typeClass;
/*     */ 
/*     */   public WrappedWatchableObject(Object handle)
/*     */   {
/*  59 */     super(MinecraftReflection.getWatchableObjectClass());
/*  60 */     load(handle);
/*     */   }
/*     */ 
/*     */   public WrappedWatchableObject(int index, Object value)
/*     */   {
/*  69 */     super(MinecraftReflection.getWatchableObjectClass());
/*     */ 
/*  71 */     if (value == null) {
/*  72 */       throw new IllegalArgumentException("Value cannot be NULL.");
/*     */     }
/*     */ 
/*  75 */     Integer typeID = WrappedDataWatcher.getTypeID(value.getClass());
/*     */ 
/*  77 */     if (typeID != null) {
/*  78 */       if (watchableConstructor == null) {
/*     */         try {
/*  80 */           watchableConstructor = MinecraftReflection.getWatchableObjectClass().getConstructor(new Class[] { Integer.TYPE, Integer.TYPE, Object.class });
/*     */         }
/*     */         catch (Exception e) {
/*  83 */           throw new RuntimeException("Cannot get the WatchableObject(int, int, Object) constructor.", e);
/*     */         }
/*     */       }
/*     */ 
/*     */       try
/*     */       {
/*  89 */         load(watchableConstructor.newInstance(new Object[] { typeID, Integer.valueOf(index), getUnwrapped(value) }));
/*     */       } catch (Exception e) {
/*  91 */         throw new RuntimeException("Cannot construct underlying WatchableObject.", e);
/*     */       }
/*     */     } else {
/*  94 */       throw new IllegalArgumentException("Cannot watch the type " + value.getClass());
/*     */     }
/*     */   }
/*     */ 
/*     */   private void load(Object handle)
/*     */   {
/* 111 */     initialize();
/* 112 */     this.handle = handle;
/* 113 */     this.modifier = baseModifier.withTarget(handle);
/*     */ 
/* 116 */     if (!watchableObjectClass.isAssignableFrom(handle.getClass()))
/* 117 */       throw new ClassCastException("Cannot cast the class " + handle.getClass().getName() + " to " + watchableObjectClass.getName());
/*     */   }
/*     */ 
/*     */   private static void initialize()
/*     */   {
/* 126 */     if (!hasInitialized) {
/* 127 */       hasInitialized = true;
/* 128 */       watchableObjectClass = MinecraftReflection.getWatchableObjectClass();
/* 129 */       baseModifier = new StructureModifier(watchableObjectClass, null, false);
/*     */     }
/*     */   }
/*     */ 
/*     */   public Class<?> getType()
/*     */     throws FieldAccessException
/*     */   {
/* 147 */     return getWrappedType(getTypeRaw());
/*     */   }
/*     */ 
/*     */   private Class<?> getTypeRaw()
/*     */     throws FieldAccessException
/*     */   {
/* 156 */     if (this.typeClass == null) {
/* 157 */       this.typeClass = WrappedDataWatcher.getTypeClass(getTypeID());
/*     */ 
/* 159 */       if (this.typeClass == null) {
/* 160 */         throw new IllegalStateException("Unrecognized data type: " + getTypeID());
/*     */       }
/*     */     }
/*     */ 
/* 164 */     return this.typeClass;
/*     */   }
/*     */ 
/*     */   public int getIndex()
/*     */     throws FieldAccessException
/*     */   {
/* 173 */     return ((Integer)this.modifier.withType(Integer.TYPE).read(1)).intValue();
/*     */   }
/*     */ 
/*     */   public void setIndex(int index)
/*     */     throws FieldAccessException
/*     */   {
/* 182 */     this.modifier.withType(Integer.TYPE).write(1, Integer.valueOf(index));
/*     */   }
/*     */ 
/*     */   public int getTypeID()
/*     */     throws FieldAccessException
/*     */   {
/* 228 */     return ((Integer)this.modifier.withType(Integer.TYPE).read(0)).intValue();
/*     */   }
/*     */ 
/*     */   public void setTypeID(int id)
/*     */     throws FieldAccessException
/*     */   {
/* 238 */     this.modifier.withType(Integer.TYPE).write(0, Integer.valueOf(id));
/*     */   }
/*     */ 
/*     */   public void setValue(Object newValue)
/*     */     throws FieldAccessException
/*     */   {
/* 247 */     setValue(newValue, true);
/*     */   }
/*     */ 
/*     */   public void setValue(Object newValue, boolean updateClient)
/*     */     throws FieldAccessException
/*     */   {
/* 258 */     if (newValue == null)
/* 259 */       throw new IllegalArgumentException("Cannot watch a NULL value.");
/* 260 */     if (!getType().isAssignableFrom(newValue.getClass())) {
/* 261 */       throw new IllegalArgumentException("Object " + newValue + " must be of type " + getType().getName());
/*     */     }
/*     */ 
/* 264 */     if (updateClient) {
/* 265 */       setDirtyState(true);
/*     */     }
/*     */ 
/* 268 */     this.modifier.withType(Object.class).write(0, getUnwrapped(newValue));
/*     */   }
/*     */ 
/*     */   private Object getNMSValue()
/*     */   {
/* 276 */     return this.modifier.withType(Object.class).read(0);
/*     */   }
/*     */ 
/*     */   public Object getValue()
/*     */     throws FieldAccessException
/*     */   {
/* 285 */     return getWrapped(this.modifier.withType(Object.class).read(0));
/*     */   }
/*     */ 
/*     */   public void setDirtyState(boolean dirty)
/*     */     throws FieldAccessException
/*     */   {
/* 319 */     this.modifier.withType(Boolean.TYPE).write(0, Boolean.valueOf(dirty));
/*     */   }
/*     */ 
/*     */   public boolean getDirtyState()
/*     */     throws FieldAccessException
/*     */   {
/* 328 */     return ((Boolean)this.modifier.withType(Boolean.TYPE).read(0)).booleanValue();
/*     */   }
/*     */ 
/*     */   static Object getWrapped(Object value)
/*     */   {
/* 339 */     if (MinecraftReflection.isItemStack(value))
/* 340 */       return BukkitConverters.getItemStackConverter().getSpecific(value);
/* 341 */     if (MinecraftReflection.isChunkCoordinates(value)) {
/* 342 */       return new WrappedChunkCoordinate((Comparable)value);
/*     */     }
/* 344 */     return value;
/*     */   }
/*     */ 
/*     */   static Class<?> getWrappedType(Class<?> unwrapped)
/*     */   {
/* 354 */     if (unwrapped.equals(MinecraftReflection.getChunkPositionClass()))
/* 355 */       return ChunkPosition.class;
/* 356 */     if (unwrapped.equals(MinecraftReflection.getBlockPositionClass()))
/* 357 */       return BlockPosition.class;
/* 358 */     if (unwrapped.equals(MinecraftReflection.getChunkCoordinatesClass()))
/* 359 */       return WrappedChunkCoordinate.class;
/* 360 */     if (unwrapped.equals(MinecraftReflection.getItemStackClass())) {
/* 361 */       return ItemStack.class;
/*     */     }
/* 363 */     return unwrapped;
/*     */   }
/*     */ 
/*     */   static Object getUnwrapped(Object wrapped)
/*     */   {
/* 373 */     if ((wrapped instanceof ChunkPosition))
/* 374 */       return ChunkPosition.getConverter().getGeneric(MinecraftReflection.getChunkPositionClass(), (ChunkPosition)wrapped);
/* 375 */     if ((wrapped instanceof BlockPosition))
/* 376 */       return BlockPosition.getConverter().getGeneric(MinecraftReflection.getBlockPositionClass(), (BlockPosition)wrapped);
/* 377 */     if ((wrapped instanceof WrappedChunkCoordinate))
/* 378 */       return ((WrappedChunkCoordinate)wrapped).getHandle();
/* 379 */     if ((wrapped instanceof ItemStack)) {
/* 380 */       return BukkitConverters.getItemStackConverter().getGeneric(MinecraftReflection.getItemStackClass(), (ItemStack)wrapped);
/*     */     }
/* 382 */     return wrapped;
/*     */   }
/*     */ 
/*     */   static Class<?> getUnwrappedType(Class<?> wrapped)
/*     */   {
/* 391 */     if (wrapped.equals(ChunkPosition.class))
/* 392 */       return MinecraftReflection.getChunkPositionClass();
/* 393 */     if (wrapped.equals(BlockPosition.class))
/* 394 */       return MinecraftReflection.getBlockPositionClass();
/* 395 */     if (wrapped.equals(WrappedChunkCoordinate.class))
/* 396 */       return MinecraftReflection.getChunkCoordinatesClass();
/* 397 */     if (ItemStack.class.isAssignableFrom(wrapped)) {
/* 398 */       return MinecraftReflection.getItemStackClass();
/*     */     }
/* 400 */     return wrapped;
/*     */   }
/*     */ 
/*     */   public WrappedWatchableObject deepClone()
/*     */     throws FieldAccessException
/*     */   {
/* 409 */     WrappedWatchableObject clone = new WrappedWatchableObject(DefaultInstances.DEFAULT.getDefault(MinecraftReflection.getWatchableObjectClass()));
/*     */ 
/* 412 */     clone.setDirtyState(getDirtyState());
/* 413 */     clone.setIndex(getIndex());
/* 414 */     clone.setTypeID(getTypeID());
/* 415 */     clone.setValue(getClonedValue(), false);
/* 416 */     return clone;
/*     */   }
/*     */ 
/*     */   Object getClonedValue() throws FieldAccessException
/*     */   {
/* 421 */     Object value = getNMSValue();
/*     */ 
/* 424 */     if (MinecraftReflection.isBlockPosition(value)) {
/* 425 */       EquivalentConverter converter = BlockPosition.getConverter();
/* 426 */       return converter.getGeneric(MinecraftReflection.getBlockPositionClass(), converter.getSpecific(value));
/* 427 */     }if (MinecraftReflection.isChunkPosition(value)) {
/* 428 */       EquivalentConverter converter = ChunkPosition.getConverter();
/* 429 */       return converter.getGeneric(MinecraftReflection.getChunkPositionClass(), converter.getSpecific(value));
/* 430 */     }if (MinecraftReflection.isItemStack(value)) {
/* 431 */       return MinecraftReflection.getMinecraftItemStack(MinecraftReflection.getBukkitItemStack(value).clone());
/*     */     }
/*     */ 
/* 434 */     return value;
/*     */   }
/*     */ 
/*     */   public boolean equals(Object obj)
/*     */   {
/* 441 */     if (obj == this)
/* 442 */       return true;
/* 443 */     if (obj == null) {
/* 444 */       return false;
/*     */     }
/* 446 */     if ((obj instanceof WrappedWatchableObject)) {
/* 447 */       WrappedWatchableObject other = (WrappedWatchableObject)obj;
/*     */ 
/* 449 */       return (Objects.equal(Integer.valueOf(getIndex()), Integer.valueOf(other.getIndex()))) && (Objects.equal(Integer.valueOf(getTypeID()), Integer.valueOf(other.getTypeID()))) && (Objects.equal(getValue(), other.getValue()));
/*     */     }
/*     */ 
/* 455 */     return false;
/*     */   }
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 460 */     return Objects.hashCode(new Object[] { Integer.valueOf(getIndex()), Integer.valueOf(getTypeID()), getValue() });
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 465 */     return String.format("[%s: %s (%s)]", new Object[] { Integer.valueOf(getIndex()), getValue(), getType().getSimpleName() });
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.wrappers.WrappedWatchableObject
 * JD-Core Version:    0.6.2
 */