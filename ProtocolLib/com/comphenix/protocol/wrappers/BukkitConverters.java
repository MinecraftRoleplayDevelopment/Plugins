/*      */ package com.comphenix.protocol.wrappers;
/*      */ 
/*      */ import com.comphenix.protocol.ProtocolLibrary;
/*      */ import com.comphenix.protocol.ProtocolManager;
/*      */ import com.comphenix.protocol.injector.BukkitUnwrapper;
/*      */ import com.comphenix.protocol.injector.PacketConstructor;
/*      */ import com.comphenix.protocol.injector.PacketConstructor.Unwrapper;
/*      */ import com.comphenix.protocol.reflect.EquivalentConverter;
/*      */ import com.comphenix.protocol.reflect.FieldAccessException;
/*      */ import com.comphenix.protocol.reflect.FuzzyReflection;
/*      */ import com.comphenix.protocol.reflect.StructureModifier;
/*      */ import com.comphenix.protocol.reflect.accessors.Accessors;
/*      */ import com.comphenix.protocol.reflect.accessors.FieldAccessor;
/*      */ import com.comphenix.protocol.reflect.accessors.MethodAccessor;
/*      */ import com.comphenix.protocol.reflect.fuzzy.FuzzyMethodContract;
/*      */ import com.comphenix.protocol.reflect.fuzzy.FuzzyMethodContract.Builder;
/*      */ import com.comphenix.protocol.reflect.instances.DefaultInstances;
/*      */ import com.comphenix.protocol.utility.MinecraftReflection;
/*      */ import com.comphenix.protocol.wrappers.nbt.NbtBase;
/*      */ import com.comphenix.protocol.wrappers.nbt.NbtCompound;
/*      */ import com.comphenix.protocol.wrappers.nbt.NbtFactory;
/*      */ import com.comphenix.protocol.wrappers.nbt.NbtWrapper;
/*      */ import com.google.common.base.Objects;
/*      */ import com.google.common.collect.ImmutableList;
/*      */ import com.google.common.collect.ImmutableList.Builder;
/*      */ import com.google.common.collect.ImmutableMap;
/*      */ import com.google.common.collect.ImmutableMap.Builder;
/*      */ import com.google.common.collect.Lists;
/*      */ import com.google.common.collect.Maps;
/*      */ import java.lang.ref.WeakReference;
/*      */ import java.lang.reflect.Array;
/*      */ import java.lang.reflect.Constructor;
/*      */ import java.lang.reflect.Method;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Collection;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Map.Entry;
/*      */ import org.bukkit.Material;
/*      */ import org.bukkit.World;
/*      */ import org.bukkit.WorldType;
/*      */ import org.bukkit.entity.Entity;
/*      */ import org.bukkit.inventory.ItemStack;
/*      */ import org.bukkit.potion.PotionEffect;
/*      */ import org.bukkit.potion.PotionEffectType;
/*      */ import org.bukkit.util.Vector;
/*      */ 
/*      */ public class BukkitConverters
/*      */ {
/*   72 */   private static boolean hasWorldType = false;
/*   73 */   private static boolean hasAttributeSnapshot = false;
/*      */   private static Map<Class<?>, EquivalentConverter<Object>> specificConverters;
/*      */   private static Map<Class<?>, EquivalentConverter<Object>> genericConverters;
/*      */   private static List<PacketConstructor.Unwrapper> unwrappers;
/*      */   private static Method worldTypeName;
/*      */   private static Method worldTypeGetType;
/*      */   private static MethodAccessor GET_BLOCK;
/*      */   private static MethodAccessor GET_BLOCK_ID;
/*      */   private static volatile Constructor<?> mobEffectConstructor;
/*      */   private static volatile StructureModifier<Object> mobEffectModifier;
/*      */   private static FieldAccessor craftWorldField;
/*      */   private static Constructor<?> vec3dConstructor;
/*      */   private static StructureModifier<Object> vec3dModifier;
/*      */ 
/*      */   public static <T, U> EquivalentConverter<Map<T, U>> getMapConverter(final Class<?> genericKeyType, EquivalentConverter<T> keyConverter)
/*      */   {
/*  220 */     return new IgnoreNullConverter(keyConverter)
/*      */     {
/*      */       protected Map<T, U> getSpecificValue(Object generic)
/*      */       {
/*  224 */         if ((generic instanceof Map)) {
/*  225 */           Map result = Maps.newHashMap();
/*      */ 
/*  228 */           for (Map.Entry entry : ((Map)generic).entrySet()) {
/*  229 */             result.put(this.val$keyConverter.getSpecific(entry.getKey()), entry.getValue());
/*      */           }
/*      */ 
/*  234 */           return result;
/*      */         }
/*      */ 
/*  238 */         return null;
/*      */       }
/*      */ 
/*      */       protected Object getGenericValue(Class<?> genericType, Map<T, U> specific)
/*      */       {
/*  244 */         Map newContainer = (Map)DefaultInstances.DEFAULT.getDefault(genericType);
/*      */ 
/*  247 */         for (Map.Entry entry : specific.entrySet()) {
/*  248 */           newContainer.put(this.val$keyConverter.getGeneric(genericKeyType, entry.getKey()), entry.getValue());
/*      */         }
/*      */ 
/*  253 */         return newContainer;
/*      */       }
/*      */ 
/*      */       public Class<Map<T, U>> getSpecificType()
/*      */       {
/*  259 */         Class dummy = Map.class;
/*  260 */         return dummy;
/*      */       }
/*      */     };
/*      */   }
/*      */ 
/*      */   public static <T> EquivalentConverter<List<T>> getListConverter(final Class<?> genericItemType, EquivalentConverter<T> itemConverter)
/*      */   {
/*  273 */     return new IgnoreNullConverter(itemConverter)
/*      */     {
/*      */       protected List<T> getSpecificValue(Object generic)
/*      */       {
/*  277 */         if ((generic instanceof Collection)) {
/*  278 */           List items = new ArrayList();
/*      */ 
/*  281 */           for (Iterator i$ = ((Collection)generic).iterator(); i$.hasNext(); ) { Object item = i$.next();
/*  282 */             Object result = this.val$itemConverter.getSpecific(item);
/*      */ 
/*  284 */             if (item != null)
/*  285 */               items.add(result);
/*      */           }
/*  287 */           return items;
/*      */         }
/*      */ 
/*  291 */         return null;
/*      */       }
/*      */ 
/*      */       protected Object getGenericValue(Class<?> genericType, List<T> specific)
/*      */       {
/*  297 */         Collection newContainer = (Collection)DefaultInstances.DEFAULT.getDefault(genericType);
/*      */ 
/*  300 */         for (Iterator i$ = specific.iterator(); i$.hasNext(); ) { Object position = i$.next();
/*  301 */           Object converted = this.val$itemConverter.getGeneric(genericItemType, position);
/*      */ 
/*  303 */           if (position == null)
/*  304 */             newContainer.add(null);
/*  305 */           else if (converted != null)
/*  306 */             newContainer.add(converted);
/*      */         }
/*  308 */         return newContainer;
/*      */       }
/*      */ 
/*      */       public Class<List<T>> getSpecificType()
/*      */       {
/*  315 */         Class dummy = List.class;
/*  316 */         return dummy;
/*      */       }
/*      */     };
/*      */   }
/*      */ 
/*      */   public static <T> EquivalentConverter<Iterable<? extends T>> getArrayConverter(final Class<?> genericItemType, EquivalentConverter<T> itemConverter)
/*      */   {
/*  332 */     return new IgnoreNullConverter(itemConverter)
/*      */     {
/*      */       protected List<T> getSpecificValue(Object generic) {
/*  335 */         if ((generic instanceof Object[])) {
/*  336 */           ImmutableList.Builder builder = ImmutableList.builder();
/*      */ 
/*  339 */           for (Object item : (Object[])generic) {
/*  340 */             Object result = this.val$itemConverter.getSpecific(item);
/*  341 */             builder.add(result);
/*      */           }
/*  343 */           return builder.build();
/*      */         }
/*      */ 
/*  347 */         return null;
/*      */       }
/*      */ 
/*      */       protected Object getGenericValue(Class<?> genericType, Iterable<? extends T> specific)
/*      */       {
/*  352 */         List list = Lists.newArrayList(specific);
/*  353 */         Object[] output = (Object[])Array.newInstance(genericItemType, list.size());
/*      */ 
/*  356 */         for (int i = 0; i < output.length; i++) {
/*  357 */           Object converted = this.val$itemConverter.getGeneric(genericItemType, list.get(i));
/*  358 */           output[i] = converted;
/*      */         }
/*  360 */         return output;
/*      */       }
/*      */ 
/*      */       public Class<Iterable<? extends T>> getSpecificType()
/*      */       {
/*  367 */         Class dummy = Iterable.class;
/*  368 */         return dummy;
/*      */       }
/*      */     };
/*      */   }
/*      */ 
/*      */   public static EquivalentConverter<WrappedGameProfile> getWrappedGameProfileConverter()
/*      */   {
/*      */     // Byte code:
/*      */     //   0: new 47	com/comphenix/protocol/wrappers/BukkitConverters$4
/*      */     //   3: dup
/*      */     //   4: invokespecial 128	com/comphenix/protocol/wrappers/BukkitConverters$4:<init>	()V
/*      */     //   7: areturn
/*      */   }
/*      */ 
/*      */   public static EquivalentConverter<WrappedChatComponent> getWrappedChatComponentConverter()
/*      */   {
/*      */     // Byte code:
/*      */     //   0: new 45	com/comphenix/protocol/wrappers/BukkitConverters$5
/*      */     //   3: dup
/*      */     //   4: invokespecial 130	com/comphenix/protocol/wrappers/BukkitConverters$5:<init>	()V
/*      */     //   7: areturn
/*      */   }
/*      */ 
/*      */   public static EquivalentConverter<WrappedBlockData> getWrappedBlockDataConverter()
/*      */   {
/*      */     // Byte code:
/*      */     //   0: new 43	com/comphenix/protocol/wrappers/BukkitConverters$6
/*      */     //   3: dup
/*      */     //   4: invokespecial 132	com/comphenix/protocol/wrappers/BukkitConverters$6:<init>	()V
/*      */     //   7: areturn
/*      */   }
/*      */ 
/*      */   public static EquivalentConverter<WrappedAttribute> getWrappedAttributeConverter()
/*      */   {
/*      */     // Byte code:
/*      */     //   0: new 41	com/comphenix/protocol/wrappers/BukkitConverters$7
/*      */     //   3: dup
/*      */     //   4: invokespecial 134	com/comphenix/protocol/wrappers/BukkitConverters$7:<init>	()V
/*      */     //   7: areturn
/*      */   }
/*      */ 
/*      */   public static EquivalentConverter<WrappedWatchableObject> getWatchableObjectConverter()
/*      */   {
/*      */     // Byte code:
/*      */     //   0: new 39	com/comphenix/protocol/wrappers/BukkitConverters$8
/*      */     //   3: dup
/*      */     //   4: invokespecial 136	com/comphenix/protocol/wrappers/BukkitConverters$8:<init>	()V
/*      */     //   7: areturn
/*      */   }
/*      */ 
/*      */   public static EquivalentConverter<WrappedDataWatcher> getDataWatcherConverter()
/*      */   {
/*      */     // Byte code:
/*      */     //   0: new 37	com/comphenix/protocol/wrappers/BukkitConverters$9
/*      */     //   3: dup
/*      */     //   4: invokespecial 138	com/comphenix/protocol/wrappers/BukkitConverters$9:<init>	()V
/*      */     //   7: areturn
/*      */   }
/*      */ 
/*      */   public static EquivalentConverter<WorldType> getWorldTypeConverter()
/*      */   {
/*  527 */     if (!hasWorldType) {
/*  528 */       return null;
/*      */     }
/*  530 */     Class worldType = MinecraftReflection.getWorldTypeClass();
/*      */ 
/*  532 */     return new IgnoreNullConverter(worldType)
/*      */     {
/*      */       protected Object getGenericValue(Class<?> genericType, WorldType specific)
/*      */       {
/*      */         try {
/*  537 */           if (BukkitConverters.worldTypeGetType == null) {
/*  538 */             BukkitConverters.access$102(FuzzyReflection.fromClass(this.val$worldType).getMethodByParameters("getType", this.val$worldType, new Class[] { String.class }));
/*      */           }
/*      */ 
/*  543 */           return BukkitConverters.worldTypeGetType.invoke(this, new Object[] { specific.getName() });
/*      */         }
/*      */         catch (Exception e) {
/*  546 */           throw new FieldAccessException("Cannot find the WorldType.getType() method.", e);
/*      */         }
/*      */       }
/*      */ 
/*      */       protected WorldType getSpecificValue(Object generic)
/*      */       {
/*      */         try {
/*  553 */           if (BukkitConverters.worldTypeName == null) {
/*      */             try {
/*  555 */               BukkitConverters.access$202(this.val$worldType.getMethod("name", new Class[0]));
/*      */             }
/*      */             catch (Exception e) {
/*  558 */               BukkitConverters.access$202(FuzzyReflection.fromClass(this.val$worldType).getMethodByParameters("name", String.class, new Class[0]));
/*      */             }
/*      */ 
/*      */           }
/*      */ 
/*  564 */           String name = (String)BukkitConverters.worldTypeName.invoke(generic, new Object[0]);
/*  565 */           return WorldType.getByName(name);
/*      */         }
/*      */         catch (Exception e) {
/*  568 */           throw new FieldAccessException("Cannot call the name method in WorldType.", e);
/*      */         }
/*      */       }
/*      */ 
/*      */       public Class<WorldType> getSpecificType()
/*      */       {
/*  574 */         return WorldType.class;
/*      */       }
/*      */     };
/*      */   }
/*      */ 
/*      */   public static EquivalentConverter<NbtBase<?>> getNbtConverter()
/*      */   {
/*      */     // Byte code:
/*      */     //   0: new 33	com/comphenix/protocol/wrappers/BukkitConverters$11
/*      */     //   3: dup
/*      */     //   4: invokespecial 153	com/comphenix/protocol/wrappers/BukkitConverters$11:<init>	()V
/*      */     //   7: areturn
/*      */   }
/*      */ 
/*      */   public static EquivalentConverter<Entity> getEntityConverter(World world)
/*      */   {
/*  611 */     final WeakReference managerRef = new WeakReference(ProtocolLibrary.getProtocolManager());
/*      */ 
/*  614 */     return new WorldSpecificConverter(world)
/*      */     {
/*      */       public Object getGenericValue(Class<?> genericType, Entity specific)
/*      */       {
/*  618 */         return Integer.valueOf(specific.getEntityId());
/*      */       }
/*      */ 
/*      */       public Entity getSpecificValue(Object generic)
/*      */       {
/*      */         try {
/*  624 */           Integer id = (Integer)generic;
/*  625 */           ProtocolManager manager = (ProtocolManager)managerRef.get();
/*      */ 
/*  628 */           if ((id != null) && (manager != null)) {
/*  629 */             return manager.getEntityFromID(this.world, id.intValue());
/*      */           }
/*  631 */           return null;
/*      */         }
/*      */         catch (FieldAccessException e)
/*      */         {
/*  635 */           throw new RuntimeException("Cannot retrieve entity from ID.", e);
/*      */         }
/*      */       }
/*      */ 
/*      */       public Class<Entity> getSpecificType()
/*      */       {
/*  641 */         return Entity.class;
/*      */       }
/*      */     };
/*      */   }
/*      */ 
/*      */   public static EquivalentConverter<ItemStack> getItemStackConverter()
/*      */   {
/*      */     // Byte code:
/*      */     //   0: new 29	com/comphenix/protocol/wrappers/BukkitConverters$13
/*      */     //   3: dup
/*      */     //   4: invokespecial 176	com/comphenix/protocol/wrappers/BukkitConverters$13:<init>	()V
/*      */     //   7: areturn
/*      */   }
/*      */ 
/*      */   public static EquivalentConverter<WrappedServerPing> getWrappedServerPingConverter()
/*      */   {
/*      */     // Byte code:
/*      */     //   0: new 27	com/comphenix/protocol/wrappers/BukkitConverters$14
/*      */     //   3: dup
/*      */     //   4: invokespecial 178	com/comphenix/protocol/wrappers/BukkitConverters$14:<init>	()V
/*      */     //   7: areturn
/*      */   }
/*      */ 
/*      */   public static EquivalentConverter<WrappedStatistic> getWrappedStatisticConverter()
/*      */   {
/*      */     // Byte code:
/*      */     //   0: new 25	com/comphenix/protocol/wrappers/BukkitConverters$15
/*      */     //   3: dup
/*      */     //   4: invokespecial 180	com/comphenix/protocol/wrappers/BukkitConverters$15:<init>	()V
/*      */     //   7: areturn
/*      */   }
/*      */ 
/*      */   public static EquivalentConverter<Material> getBlockConverter()
/*      */   {
/*      */     // Byte code:
/*      */     //   0: getstatic 183	com/comphenix/protocol/wrappers/BukkitConverters:GET_BLOCK	Lcom/comphenix/protocol/reflect/accessors/MethodAccessor;
/*      */     //   3: ifnull +9 -> 12
/*      */     //   6: getstatic 185	com/comphenix/protocol/wrappers/BukkitConverters:GET_BLOCK_ID	Lcom/comphenix/protocol/reflect/accessors/MethodAccessor;
/*      */     //   9: ifnonnull +87 -> 96
/*      */     //   12: invokestatic 188	com/comphenix/protocol/utility/MinecraftReflection:getBlockClass	()Ljava/lang/Class;
/*      */     //   15: astore_0
/*      */     //   16: invokestatic 192	com/comphenix/protocol/reflect/fuzzy/FuzzyMethodContract:newBuilder	()Lcom/comphenix/protocol/reflect/fuzzy/FuzzyMethodContract$Builder;
/*      */     //   19: iconst_1
/*      */     //   20: anewarray 194	java/lang/Class
/*      */     //   23: dup
/*      */     //   24: iconst_0
/*      */     //   25: aload_0
/*      */     //   26: aastore
/*      */     //   27: invokevirtual 198	com/comphenix/protocol/reflect/fuzzy/FuzzyMethodContract$Builder:parameterExactArray	([Ljava/lang/Class;)Lcom/comphenix/protocol/reflect/fuzzy/FuzzyMethodContract$Builder;
/*      */     //   30: bipush 8
/*      */     //   32: invokevirtual 202	com/comphenix/protocol/reflect/fuzzy/FuzzyMethodContract$Builder:requireModifier	(I)Lcom/comphenix/protocol/reflect/fuzzy/FuzzyMethodContract$Builder;
/*      */     //   35: invokevirtual 206	com/comphenix/protocol/reflect/fuzzy/FuzzyMethodContract$Builder:build	()Lcom/comphenix/protocol/reflect/fuzzy/FuzzyMethodContract;
/*      */     //   38: astore_1
/*      */     //   39: invokestatic 192	com/comphenix/protocol/reflect/fuzzy/FuzzyMethodContract:newBuilder	()Lcom/comphenix/protocol/reflect/fuzzy/FuzzyMethodContract$Builder;
/*      */     //   42: aload_0
/*      */     //   43: invokevirtual 210	com/comphenix/protocol/reflect/fuzzy/FuzzyMethodContract$Builder:returnTypeExact	(Ljava/lang/Class;)Lcom/comphenix/protocol/reflect/fuzzy/FuzzyMethodContract$Builder;
/*      */     //   46: iconst_1
/*      */     //   47: anewarray 194	java/lang/Class
/*      */     //   50: dup
/*      */     //   51: iconst_0
/*      */     //   52: getstatic 215	java/lang/Integer:TYPE	Ljava/lang/Class;
/*      */     //   55: aastore
/*      */     //   56: invokevirtual 198	com/comphenix/protocol/reflect/fuzzy/FuzzyMethodContract$Builder:parameterExactArray	([Ljava/lang/Class;)Lcom/comphenix/protocol/reflect/fuzzy/FuzzyMethodContract$Builder;
/*      */     //   59: bipush 8
/*      */     //   61: invokevirtual 202	com/comphenix/protocol/reflect/fuzzy/FuzzyMethodContract$Builder:requireModifier	(I)Lcom/comphenix/protocol/reflect/fuzzy/FuzzyMethodContract$Builder;
/*      */     //   64: invokevirtual 206	com/comphenix/protocol/reflect/fuzzy/FuzzyMethodContract$Builder:build	()Lcom/comphenix/protocol/reflect/fuzzy/FuzzyMethodContract;
/*      */     //   67: astore_2
/*      */     //   68: aload_0
/*      */     //   69: invokestatic 221	com/comphenix/protocol/reflect/FuzzyReflection:fromClass	(Ljava/lang/Class;)Lcom/comphenix/protocol/reflect/FuzzyReflection;
/*      */     //   72: aload_2
/*      */     //   73: invokevirtual 225	com/comphenix/protocol/reflect/FuzzyReflection:getMethod	(Lcom/comphenix/protocol/reflect/fuzzy/AbstractFuzzyMatcher;)Ljava/lang/reflect/Method;
/*      */     //   76: invokestatic 231	com/comphenix/protocol/reflect/accessors/Accessors:getMethodAccessor	(Ljava/lang/reflect/Method;)Lcom/comphenix/protocol/reflect/accessors/MethodAccessor;
/*      */     //   79: putstatic 183	com/comphenix/protocol/wrappers/BukkitConverters:GET_BLOCK	Lcom/comphenix/protocol/reflect/accessors/MethodAccessor;
/*      */     //   82: aload_0
/*      */     //   83: invokestatic 221	com/comphenix/protocol/reflect/FuzzyReflection:fromClass	(Ljava/lang/Class;)Lcom/comphenix/protocol/reflect/FuzzyReflection;
/*      */     //   86: aload_1
/*      */     //   87: invokevirtual 225	com/comphenix/protocol/reflect/FuzzyReflection:getMethod	(Lcom/comphenix/protocol/reflect/fuzzy/AbstractFuzzyMatcher;)Ljava/lang/reflect/Method;
/*      */     //   90: invokestatic 231	com/comphenix/protocol/reflect/accessors/Accessors:getMethodAccessor	(Ljava/lang/reflect/Method;)Lcom/comphenix/protocol/reflect/accessors/MethodAccessor;
/*      */     //   93: putstatic 185	com/comphenix/protocol/wrappers/BukkitConverters:GET_BLOCK_ID	Lcom/comphenix/protocol/reflect/accessors/MethodAccessor;
/*      */     //   96: new 23	com/comphenix/protocol/wrappers/BukkitConverters$16
/*      */     //   99: dup
/*      */     //   100: invokespecial 232	com/comphenix/protocol/wrappers/BukkitConverters$16:<init>	()V
/*      */     //   103: areturn
/*      */   }
/*      */ 
/*      */   public static EquivalentConverter<World> getWorldConverter()
/*      */   {
/*      */     // Byte code:
/*      */     //   0: new 21	com/comphenix/protocol/wrappers/BukkitConverters$17
/*      */     //   3: dup
/*      */     //   4: invokespecial 238	com/comphenix/protocol/wrappers/BukkitConverters$17:<init>	()V
/*      */     //   7: areturn
/*      */   }
/*      */ 
/*      */   public static EquivalentConverter<PotionEffect> getPotionEffectConverter()
/*      */   {
/*      */     // Byte code:
/*      */     //   0: new 19	com/comphenix/protocol/wrappers/BukkitConverters$18
/*      */     //   3: dup
/*      */     //   4: invokespecial 240	com/comphenix/protocol/wrappers/BukkitConverters$18:<init>	()V
/*      */     //   7: areturn
/*      */   }
/*      */ 
/*      */   public static EquivalentConverter<Vector> getVectorConverter()
/*      */   {
/*      */     // Byte code:
/*      */     //   0: new 17	com/comphenix/protocol/wrappers/BukkitConverters$19
/*      */     //   3: dup
/*      */     //   4: invokespecial 242	com/comphenix/protocol/wrappers/BukkitConverters$19:<init>	()V
/*      */     //   7: areturn
/*      */   }
/*      */ 
/*      */   public static <TType> EquivalentConverter<TType> getIgnoreNull(EquivalentConverter<TType> delegate)
/*      */   {
/*  886 */     return new IgnoreNullConverter(delegate)
/*      */     {
/*      */       public Object getGenericValue(Class<?> genericType, TType specific) {
/*  889 */         return this.val$delegate.getGeneric(genericType, specific);
/*      */       }
/*      */ 
/*      */       public TType getSpecificValue(Object generic)
/*      */       {
/*  894 */         return this.val$delegate.getSpecific(generic);
/*      */       }
/*      */ 
/*      */       public Class<TType> getSpecificType()
/*      */       {
/*  899 */         return this.val$delegate.getSpecificType();
/*      */       }
/*      */     };
/*      */   }
/*      */ 
/*      */   public static PacketConstructor.Unwrapper asUnwrapper(final Class<?> nativeType, EquivalentConverter<Object> converter)
/*      */   {
/*  911 */     return new PacketConstructor.Unwrapper()
/*      */     {
/*      */       public Object unwrapItem(Object wrappedObject) {
/*  914 */         Class type = PacketConstructor.getClass(wrappedObject);
/*      */ 
/*  917 */         if (this.val$converter.getSpecificType().isAssignableFrom(type)) {
/*  918 */           if ((wrappedObject instanceof Class)) {
/*  919 */             return nativeType;
/*      */           }
/*  921 */           return this.val$converter.getGeneric(nativeType, wrappedObject);
/*      */         }
/*  923 */         return null;
/*      */       }
/*      */     };
/*      */   }
/*      */ 
/*      */   public static Map<Class<?>, EquivalentConverter<Object>> getConvertersForSpecific()
/*      */   {
/*  934 */     if (specificConverters == null)
/*      */     {
/*  936 */       ImmutableMap.Builder builder = ImmutableMap.builder().put(WrappedDataWatcher.class, getDataWatcherConverter()).put(ItemStack.class, getItemStackConverter()).put(NbtBase.class, getNbtConverter()).put(NbtCompound.class, getNbtConverter()).put(WrappedWatchableObject.class, getWatchableObjectConverter()).put(PotionEffect.class, getPotionEffectConverter()).put(World.class, getWorldConverter());
/*      */ 
/*  947 */       if (MinecraftReflection.isUsingNetty()) {
/*  948 */         builder.put(Material.class, getBlockConverter());
/*  949 */         builder.put(WrappedGameProfile.class, getWrappedGameProfileConverter());
/*  950 */         builder.put(WrappedChatComponent.class, getWrappedChatComponentConverter());
/*  951 */         builder.put(WrappedServerPing.class, getWrappedServerPingConverter());
/*  952 */         builder.put(WrappedStatistic.class, getWrappedStatisticConverter());
/*      */ 
/*  954 */         for (Map.Entry entry : EnumWrappers.getFromWrapperMap().entrySet()) {
/*  955 */           builder.put((Class)entry.getKey(), (EquivalentConverter)entry.getValue());
/*      */         }
/*      */       }
/*      */ 
/*  959 */       if (hasWorldType)
/*  960 */         builder.put(WorldType.class, getWorldTypeConverter());
/*  961 */       if (hasAttributeSnapshot)
/*  962 */         builder.put(WrappedAttribute.class, getWrappedAttributeConverter());
/*  963 */       specificConverters = builder.build();
/*      */     }
/*  965 */     return specificConverters;
/*      */   }
/*      */ 
/*      */   public static Map<Class<?>, EquivalentConverter<Object>> getConvertersForGeneric()
/*      */   {
/*  974 */     if (genericConverters == null)
/*      */     {
/*  976 */       ImmutableMap.Builder builder = ImmutableMap.builder().put(MinecraftReflection.getDataWatcherClass(), getDataWatcherConverter()).put(MinecraftReflection.getItemStackClass(), getItemStackConverter()).put(MinecraftReflection.getNBTBaseClass(), getNbtConverter()).put(MinecraftReflection.getNBTCompoundClass(), getNbtConverter()).put(MinecraftReflection.getWatchableObjectClass(), getWatchableObjectConverter()).put(MinecraftReflection.getMobEffectClass(), getPotionEffectConverter()).put(MinecraftReflection.getNmsWorldClass(), getWorldConverter());
/*      */ 
/*  986 */       if (hasWorldType)
/*  987 */         builder.put(MinecraftReflection.getWorldTypeClass(), getWorldTypeConverter());
/*  988 */       if (hasAttributeSnapshot) {
/*  989 */         builder.put(MinecraftReflection.getAttributeSnapshotClass(), getWrappedAttributeConverter());
/*      */       }
/*      */ 
/*  992 */       if (MinecraftReflection.isUsingNetty()) {
/*  993 */         builder.put(MinecraftReflection.getBlockClass(), getBlockConverter());
/*  994 */         builder.put(MinecraftReflection.getGameProfileClass(), getWrappedGameProfileConverter());
/*  995 */         builder.put(MinecraftReflection.getIChatBaseComponentClass(), getWrappedChatComponentConverter());
/*  996 */         builder.put(MinecraftReflection.getServerPingClass(), getWrappedServerPingConverter());
/*  997 */         builder.put(MinecraftReflection.getStatisticClass(), getWrappedStatisticConverter());
/*      */ 
/*  999 */         for (Map.Entry entry : EnumWrappers.getFromNativeMap().entrySet()) {
/* 1000 */           builder.put((Class)entry.getKey(), (EquivalentConverter)entry.getValue());
/*      */         }
/*      */       }
/* 1003 */       genericConverters = builder.build();
/*      */     }
/* 1005 */     return genericConverters;
/*      */   }
/*      */ 
/*      */   public static List<PacketConstructor.Unwrapper> getUnwrappers()
/*      */   {
/* 1013 */     if (unwrappers == null) {
/* 1014 */       ImmutableList.Builder builder = ImmutableList.builder();
/*      */ 
/* 1016 */       for (Map.Entry entry : getConvertersForGeneric().entrySet()) {
/* 1017 */         builder.add(asUnwrapper((Class)entry.getKey(), (EquivalentConverter)entry.getValue()));
/*      */       }
/* 1019 */       unwrappers = builder.build();
/*      */     }
/* 1021 */     return unwrappers;
/*      */   }
/*      */ 
/*      */   static
/*      */   {
/*      */     try
/*      */     {
/*   97 */       MinecraftReflection.getWorldTypeClass();
/*   98 */       hasWorldType = true;
/*      */     }
/*      */     catch (Exception e) {
/*      */     }
/*      */     try {
/*  103 */       MinecraftReflection.getAttributeSnapshotClass();
/*  104 */       hasAttributeSnapshot = true;
/*      */     }
/*      */     catch (Exception e)
/*      */     {
/*      */     }
/*      */     try {
/*  110 */       craftWorldField = Accessors.getFieldAccessor(MinecraftReflection.getNmsWorldClass(), MinecraftReflection.getCraftWorldClass(), true);
/*      */     }
/*      */     catch (Exception e)
/*      */     {
/*  114 */       e.printStackTrace();
/*      */     }
/*      */   }
/*      */ 
/*      */   private static abstract class WorldSpecificConverter<TType> extends BukkitConverters.IgnoreNullConverter<TType>
/*      */   {
/*      */     protected World world;
/*      */ 
/*      */     public WorldSpecificConverter(World world)
/*      */     {
/*  188 */       super();
/*  189 */       this.world = world;
/*      */     }
/*      */ 
/*      */     public boolean equals(Object obj)
/*      */     {
/*  195 */       if (obj == this)
/*  196 */         return true;
/*  197 */       if (obj == null) {
/*  198 */         return false;
/*      */       }
/*      */ 
/*  201 */       if (((obj instanceof WorldSpecificConverter)) && (super.equals(obj)))
/*      */       {
/*  203 */         WorldSpecificConverter other = (WorldSpecificConverter)obj;
/*      */ 
/*  205 */         return Objects.equal(this.world, other.world);
/*      */       }
/*  207 */       return false;
/*      */     }
/*      */   }
/*      */ 
/*      */   private static abstract class IgnoreNullConverter<TType>
/*      */     implements EquivalentConverter<TType>
/*      */   {
/*      */     public final Object getGeneric(Class<?> genericType, TType specific)
/*      */     {
/*  127 */       if (specific != null) {
/*  128 */         return getGenericValue(genericType, specific);
/*      */       }
/*  130 */       return null;
/*      */     }
/*      */ 
/*      */     protected abstract Object getGenericValue(Class<?> paramClass, TType paramTType);
/*      */ 
/*      */     public final TType getSpecific(Object generic)
/*      */     {
/*  143 */       if (generic != null) {
/*  144 */         return getSpecificValue(generic);
/*      */       }
/*  146 */       return null;
/*      */     }
/*      */ 
/*      */     protected abstract TType getSpecificValue(Object paramObject);
/*      */ 
/*      */     public boolean equals(Object obj)
/*      */     {
/*  159 */       if (this == obj)
/*  160 */         return true;
/*  161 */       if (obj == null) {
/*  162 */         return false;
/*      */       }
/*      */ 
/*  165 */       if ((obj instanceof EquivalentConverter))
/*      */       {
/*  167 */         EquivalentConverter other = (EquivalentConverter)obj;
/*  168 */         return Objects.equal(getSpecificType(), other.getSpecificType());
/*      */       }
/*  170 */       return false;
/*      */     }
/*      */   }
/*      */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.wrappers.BukkitConverters
 * JD-Core Version:    0.6.2
 */