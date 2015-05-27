/*     */ package com.comphenix.protocol.wrappers.nbt;
/*     */ 
/*     */ import com.comphenix.protocol.reflect.FieldAccessException;
/*     */ import com.comphenix.protocol.reflect.FuzzyReflection;
/*     */ import com.comphenix.protocol.reflect.StructureModifier;
/*     */ import com.comphenix.protocol.utility.MinecraftReflection;
/*     */ import com.comphenix.protocol.wrappers.BukkitConverters;
/*     */ import com.comphenix.protocol.wrappers.nbt.io.NbtBinarySerializer;
/*     */ import com.google.common.base.Preconditions;
/*     */ import com.google.common.io.Closeables;
/*     */ import java.io.DataInputStream;
/*     */ import java.io.DataOutputStream;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.lang.reflect.Method;
/*     */ import java.util.Collection;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.zip.GZIPInputStream;
/*     */ import java.util.zip.GZIPOutputStream;
/*     */ import javax.annotation.Nonnull;
/*     */ import org.bukkit.Material;
/*     */ import org.bukkit.block.Block;
/*     */ import org.bukkit.block.BlockState;
/*     */ import org.bukkit.inventory.ItemStack;
/*     */ 
/*     */ public class NbtFactory
/*     */ {
/*     */   private static Method methodCreateTag;
/*     */   private static boolean methodCreateWithName;
/*     */   private static StructureModifier<Object> itemStackModifier;
/*     */ 
/*     */   public static NbtCompound asCompound(NbtBase<?> tag)
/*     */   {
/*  68 */     if ((tag instanceof NbtCompound))
/*  69 */       return (NbtCompound)tag;
/*  70 */     if (tag != null) {
/*  71 */       throw new UnsupportedOperationException("Cannot cast a " + tag.getClass() + "( " + tag.getType() + ") to TAG_COMPUND.");
/*     */     }
/*     */ 
/*  74 */     throw new IllegalArgumentException("Tag cannot be NULL.");
/*     */   }
/*     */ 
/*     */   public static NbtList<?> asList(NbtBase<?> tag)
/*     */   {
/*  84 */     if ((tag instanceof NbtList))
/*  85 */       return (NbtList)tag;
/*  86 */     if (tag != null) {
/*  87 */       throw new UnsupportedOperationException("Cannot cast a " + tag.getClass() + "( " + tag.getType() + ") to TAG_LIST.");
/*     */     }
/*     */ 
/*  90 */     throw new IllegalArgumentException("Tag cannot be NULL.");
/*     */   }
/*     */ 
/*     */   public static <T> NbtWrapper<T> fromBase(NbtBase<T> base)
/*     */   {
/* 102 */     if ((base instanceof NbtWrapper)) {
/* 103 */       return (NbtWrapper)base;
/*     */     }
/* 105 */     if (base.getType() == NbtType.TAG_COMPOUND)
/*     */     {
/* 107 */       WrappedCompound copy = WrappedCompound.fromName(base.getName());
/* 108 */       Object value = base.getValue();
/*     */ 
/* 110 */       copy.setValue((Map)value);
/* 111 */       return copy;
/*     */     }
/* 113 */     if (base.getType() == NbtType.TAG_LIST)
/*     */     {
/* 115 */       NbtList copy = WrappedList.fromName(base.getName());
/*     */ 
/* 117 */       copy.setValue((List)base.getValue());
/* 118 */       return (NbtWrapper)copy;
/*     */     }
/*     */ 
/* 122 */     NbtWrapper copy = ofWrapper(base.getType(), base.getName());
/*     */ 
/* 124 */     copy.setValue(base.getValue());
/* 125 */     return copy;
/*     */   }
/*     */ 
/*     */   public static void setItemTag(ItemStack stack, NbtCompound compound)
/*     */   {
/* 140 */     checkItemStack(stack);
/*     */ 
/* 142 */     StructureModifier modifier = getStackModifier(stack);
/* 143 */     modifier.write(0, compound);
/*     */   }
/*     */ 
/*     */   public static NbtWrapper<?> fromItemTag(ItemStack stack)
/*     */   {
/* 157 */     checkItemStack(stack);
/*     */ 
/* 159 */     StructureModifier modifier = getStackModifier(stack);
/* 160 */     NbtBase result = (NbtBase)modifier.read(0);
/*     */ 
/* 163 */     if (result == null) {
/* 164 */       result = ofCompound("tag");
/* 165 */       modifier.write(0, result);
/*     */     }
/* 167 */     return fromBase(result);
/*     */   }
/*     */ 
/*     */   public static NbtCompound fromFile(String file)
/*     */     throws IOException
/*     */   {
/* 177 */     Preconditions.checkNotNull(file, "file cannot be NULL");
/* 178 */     FileInputStream stream = null;
/* 179 */     DataInputStream input = null;
/* 180 */     boolean swallow = true;
/*     */     try
/*     */     {
/* 183 */       stream = new FileInputStream(file);
/* 184 */       NbtCompound result = NbtBinarySerializer.DEFAULT.deserializeCompound(input = new DataInputStream(new GZIPInputStream(stream)));
/*     */ 
/* 186 */       swallow = false;
/* 187 */       return result;
/*     */     }
/*     */     finally {
/* 190 */       if (input != null) Closeables.close(input, swallow);
/* 191 */       else if (stream != null) Closeables.close(stream, swallow);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static void toFile(NbtCompound compound, String file)
/*     */     throws IOException
/*     */   {
/* 202 */     Preconditions.checkNotNull(compound, "compound cannot be NULL");
/* 203 */     Preconditions.checkNotNull(file, "file cannot be NULL");
/* 204 */     FileOutputStream stream = null;
/* 205 */     DataOutputStream output = null;
/* 206 */     boolean swallow = true;
/*     */     try
/*     */     {
/* 209 */       stream = new FileOutputStream(file);
/* 210 */       NbtBinarySerializer.DEFAULT.serialize(compound, output = new DataOutputStream(new GZIPOutputStream(stream)));
/*     */ 
/* 212 */       swallow = false;
/*     */     }
/*     */     finally {
/* 215 */       if (output != null) Closeables.close(output, swallow);
/* 216 */       else if (stream != null) Closeables.close(stream, swallow);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static NbtCompound readBlockState(Block block)
/*     */   {
/* 226 */     BlockState state = block.getState();
/* 227 */     TileEntityAccessor accessor = TileEntityAccessor.getAccessor(state);
/*     */ 
/* 229 */     return accessor != null ? accessor.readBlockState(state) : null;
/*     */   }
/*     */ 
/*     */   public static void writeBlockState(Block target, NbtCompound blockState)
/*     */   {
/* 239 */     BlockState state = target.getState();
/* 240 */     TileEntityAccessor accessor = TileEntityAccessor.getAccessor(state);
/*     */ 
/* 242 */     if (accessor != null)
/* 243 */       accessor.writeBlockState(state, blockState);
/*     */     else
/* 245 */       throw new IllegalArgumentException("Unable to find tile entity in " + target);
/*     */   }
/*     */ 
/*     */   private static void checkItemStack(ItemStack stack)
/*     */   {
/* 254 */     if (stack == null)
/* 255 */       throw new IllegalArgumentException("Stack cannot be NULL.");
/* 256 */     if (!MinecraftReflection.isCraftItemStack(stack))
/* 257 */       throw new IllegalArgumentException("Stack must be a CraftItemStack.");
/* 258 */     if (stack.getType() == Material.AIR)
/* 259 */       throw new IllegalArgumentException("ItemStacks representing air cannot store NMS information.");
/*     */   }
/*     */ 
/*     */   private static StructureModifier<NbtBase<?>> getStackModifier(ItemStack stack)
/*     */   {
/* 268 */     Object nmsStack = MinecraftReflection.getMinecraftItemStack(stack);
/*     */ 
/* 270 */     if (itemStackModifier == null) {
/* 271 */       itemStackModifier = new StructureModifier(nmsStack.getClass(), Object.class, false);
/*     */     }
/*     */ 
/* 275 */     return itemStackModifier.withTarget(nmsStack).withType(MinecraftReflection.getNBTBaseClass(), BukkitConverters.getNbtConverter());
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public static <T> NbtWrapper<T> fromNMS(Object handle)
/*     */   {
/* 291 */     WrappedElement partial = new WrappedElement(handle);
/*     */ 
/* 294 */     if (partial.getType() == NbtType.TAG_COMPOUND)
/* 295 */       return new WrappedCompound(handle);
/* 296 */     if (partial.getType() == NbtType.TAG_LIST) {
/* 297 */       return new WrappedList(handle);
/*     */     }
/* 299 */     return partial;
/*     */   }
/*     */ 
/*     */   public static <T> NbtWrapper<T> fromNMS(Object handle, String name)
/*     */   {
/* 310 */     WrappedElement partial = new WrappedElement(handle, name);
/*     */ 
/* 313 */     if (partial.getType() == NbtType.TAG_COMPOUND)
/* 314 */       return new WrappedCompound(handle, name);
/* 315 */     if (partial.getType() == NbtType.TAG_LIST) {
/* 316 */       return new WrappedList(handle, name);
/*     */     }
/* 318 */     return partial;
/*     */   }
/*     */ 
/*     */   public static NbtCompound fromNMSCompound(@Nonnull Object handle)
/*     */   {
/* 327 */     if (handle == null)
/* 328 */       throw new IllegalArgumentException("handle cannot be NULL.");
/* 329 */     return (NbtCompound)fromNMS(handle);
/*     */   }
/*     */ 
/*     */   public static NbtBase<String> of(String name, String value)
/*     */   {
/* 339 */     return ofWrapper(NbtType.TAG_STRING, name, value);
/*     */   }
/*     */ 
/*     */   public static NbtBase<Byte> of(String name, byte value)
/*     */   {
/* 349 */     return ofWrapper(NbtType.TAG_BYTE, name, Byte.valueOf(value));
/*     */   }
/*     */ 
/*     */   public static NbtBase<Short> of(String name, short value)
/*     */   {
/* 359 */     return ofWrapper(NbtType.TAG_SHORT, name, Short.valueOf(value));
/*     */   }
/*     */ 
/*     */   public static NbtBase<Integer> of(String name, int value)
/*     */   {
/* 369 */     return ofWrapper(NbtType.TAG_INT, name, Integer.valueOf(value));
/*     */   }
/*     */ 
/*     */   public static NbtBase<Long> of(String name, long value)
/*     */   {
/* 379 */     return ofWrapper(NbtType.TAG_LONG, name, Long.valueOf(value));
/*     */   }
/*     */ 
/*     */   public static NbtBase<Float> of(String name, float value)
/*     */   {
/* 389 */     return ofWrapper(NbtType.TAG_FLOAT, name, Float.valueOf(value));
/*     */   }
/*     */ 
/*     */   public static NbtBase<Double> of(String name, double value)
/*     */   {
/* 399 */     return ofWrapper(NbtType.TAG_DOUBLE, name, Double.valueOf(value));
/*     */   }
/*     */ 
/*     */   public static NbtBase<byte[]> of(String name, byte[] value)
/*     */   {
/* 409 */     return ofWrapper(NbtType.TAG_BYTE_ARRAY, name, value);
/*     */   }
/*     */ 
/*     */   public static NbtBase<int[]> of(String name, int[] value)
/*     */   {
/* 419 */     return ofWrapper(NbtType.TAG_INT_ARRAY, name, value);
/*     */   }
/*     */ 
/*     */   public static NbtCompound ofCompound(String name, Collection<? extends NbtBase<?>> list)
/*     */   {
/* 429 */     return WrappedCompound.fromList(name, list);
/*     */   }
/*     */ 
/*     */   public static NbtCompound ofCompound(String name)
/*     */   {
/* 438 */     return WrappedCompound.fromName(name);
/*     */   }
/*     */ 
/*     */   public static <T> NbtList<T> ofList(String name, T[] elements)
/*     */   {
/* 448 */     return WrappedList.fromArray(name, elements);
/*     */   }
/*     */ 
/*     */   public static <T> NbtList<T> ofList(String name, Collection<? extends T> elements)
/*     */   {
/* 458 */     return WrappedList.fromList(name, elements);
/*     */   }
/*     */ 
/*     */   public static <T> NbtWrapper<T> ofWrapper(NbtType type, String name)
/*     */   {
/* 469 */     if (type == null)
/* 470 */       throw new IllegalArgumentException("type cannot be NULL.");
/* 471 */     if (type == NbtType.TAG_END) {
/* 472 */       throw new IllegalArgumentException("Cannot create a TAG_END.");
/*     */     }
/* 474 */     if (methodCreateTag == null) {
/* 475 */       Class base = MinecraftReflection.getNBTBaseClass();
/*     */       try
/*     */       {
/* 479 */         methodCreateTag = findCreateMethod(base, new Class[] { Byte.TYPE, String.class });
/* 480 */         methodCreateWithName = true;
/*     */       }
/*     */       catch (Exception e) {
/* 483 */         methodCreateTag = findCreateMethod(base, new Class[] { Byte.TYPE });
/* 484 */         methodCreateWithName = false;
/*     */       }
/*     */     }
/*     */ 
/*     */     try
/*     */     {
/* 490 */       if (methodCreateWithName) {
/* 491 */         return createTagWithName(type, name);
/*     */       }
/* 493 */       return createTagSetName(type, name);
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/* 497 */       throw new FieldAccessException(String.format("Cannot create NBT element %s (type: %s)", new Object[] { name, type }), e);
/*     */     }
/*     */   }
/*     */ 
/*     */   private static Method findCreateMethod(Class<?> base, Class<?>[] params)
/*     */   {
/* 509 */     Method method = FuzzyReflection.fromClass(base, true).getMethodByParameters("createTag", base, params);
/* 510 */     method.setAccessible(true);
/* 511 */     return method;
/*     */   }
/*     */ 
/*     */   private static <T> NbtWrapper<T> createTagWithName(NbtType type, String name)
/*     */     throws Exception
/*     */   {
/* 517 */     Object handle = methodCreateTag.invoke(null, new Object[] { Byte.valueOf((byte)type.getRawID()), name });
/*     */ 
/* 519 */     if (type == NbtType.TAG_COMPOUND)
/* 520 */       return new WrappedCompound(handle);
/* 521 */     if (type == NbtType.TAG_LIST) {
/* 522 */       return new WrappedList(handle);
/*     */     }
/* 524 */     return new WrappedElement(handle);
/*     */   }
/*     */ 
/*     */   private static <T> NbtWrapper<T> createTagSetName(NbtType type, String name)
/*     */     throws Exception
/*     */   {
/* 530 */     Object handle = methodCreateTag.invoke(null, new Object[] { Byte.valueOf((byte)type.getRawID()) });
/*     */ 
/* 532 */     if (type == NbtType.TAG_COMPOUND)
/* 533 */       return new WrappedCompound(handle, name);
/* 534 */     if (type == NbtType.TAG_LIST) {
/* 535 */       return new WrappedList(handle, name);
/*     */     }
/* 537 */     return new WrappedElement(handle, name);
/*     */   }
/*     */ 
/*     */   public static <T> NbtWrapper<T> ofWrapper(NbtType type, String name, T value)
/*     */   {
/* 549 */     NbtWrapper created = ofWrapper(type, name);
/*     */ 
/* 552 */     created.setValue(value);
/* 553 */     return created;
/*     */   }
/*     */ 
/*     */   public static <T> NbtWrapper<T> ofWrapper(Class<?> type, String name, T value)
/*     */   {
/* 566 */     return ofWrapper(NbtType.getTypeFromClass(type), name, value);
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.wrappers.nbt.NbtFactory
 * JD-Core Version:    0.6.2
 */