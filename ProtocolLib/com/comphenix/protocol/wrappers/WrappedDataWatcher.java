/*     */ package com.comphenix.protocol.wrappers;
/*     */ 
/*     */ import com.comphenix.protocol.injector.BukkitUnwrapper;
/*     */ import com.comphenix.protocol.reflect.FieldAccessException;
/*     */ import com.comphenix.protocol.reflect.FieldUtils;
/*     */ import com.comphenix.protocol.reflect.FuzzyReflection;
/*     */ import com.comphenix.protocol.reflect.accessors.Accessors;
/*     */ import com.comphenix.protocol.reflect.accessors.ConstructorAccessor;
/*     */ import com.comphenix.protocol.reflect.accessors.FieldAccessor;
/*     */ import com.comphenix.protocol.reflect.accessors.ReadOnlyFieldAccessor;
/*     */ import com.comphenix.protocol.utility.MinecraftReflection;
/*     */ import com.comphenix.protocol.wrappers.collection.ConvertedMap;
/*     */ import com.google.common.base.Function;
/*     */ import com.google.common.base.Objects;
/*     */ import com.google.common.base.Preconditions;
/*     */ import com.google.common.collect.Iterators;
/*     */ import com.google.common.collect.Maps;
/*     */ import java.lang.reflect.Constructor;
/*     */ import java.lang.reflect.Field;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.lang.reflect.Method;
/*     */ import java.lang.reflect.Modifier;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.Set;
/*     */ import java.util.concurrent.locks.Lock;
/*     */ import java.util.concurrent.locks.ReadWriteLock;
/*     */ import java.util.concurrent.locks.ReentrantReadWriteLock;
/*     */ import javax.annotation.Nullable;
/*     */ import org.bukkit.entity.Entity;
/*     */ import org.bukkit.inventory.ItemStack;
/*     */ 
/*     */ public class WrappedDataWatcher extends AbstractWrapper
/*     */   implements Iterable<WrappedWatchableObject>
/*     */ {
/*     */   private static Map<Class<?>, Integer> TYPE_MAP;
/*     */   private static Map<Class<?>, Integer> CUSTOM_MAP;
/*     */   private static FieldAccessor TYPE_MAP_ACCESSOR;
/*     */   private static FieldAccessor VALUE_MAP_ACCESSOR;
/*     */   private static Field READ_WRITE_LOCK_FIELD;
/*     */   private static Field ENTITY_FIELD;
/*     */   private static Method CREATE_KEY_VALUE_METHOD;
/*     */   private static Method UPDATE_KEY_VALUE_METHOD;
/*     */   private static Method GET_KEY_VALUE_METHOD;
/*     */   private static Constructor<?> CREATE_DATA_WATCHER_CONSTRUCTOR;
/*     */   private static volatile Field ENTITY_DATA_FIELD;
/*     */   private static boolean HAS_INITIALIZED;
/*     */   private ReadWriteLock readWriteLock;
/*     */   private Map<Integer, Object> watchableObjects;
/*     */   private Map<Integer, WrappedWatchableObject> mapView;
/*     */ 
/*     */   public WrappedDataWatcher()
/*     */   {
/* 231 */     super(MinecraftReflection.getDataWatcherClass());
/*     */     try
/*     */     {
/* 235 */       if (MinecraftReflection.isUsingNetty())
/* 236 */         setHandle(newEntityHandle(null));
/*     */       else {
/* 238 */         setHandle(getHandleType().newInstance());
/*     */       }
/* 240 */       initialize();
/*     */     }
/*     */     catch (Exception e) {
/* 243 */       throw new RuntimeException("Unable to construct DataWatcher.", e);
/*     */     }
/*     */   }
/*     */ 
/*     */   public WrappedDataWatcher(Object handle)
/*     */   {
/* 253 */     super(MinecraftReflection.getDataWatcherClass());
/* 254 */     setHandle(handle);
/* 255 */     initialize();
/*     */   }
/*     */ 
/*     */   public static WrappedDataWatcher newWithEntity(Entity entity)
/*     */   {
/* 267 */     if (!MinecraftReflection.isUsingNetty())
/* 268 */       return new WrappedDataWatcher();
/* 269 */     return new WrappedDataWatcher(newEntityHandle(entity));
/*     */   }
/*     */ 
/*     */   private static Object newEntityHandle(Entity entity)
/*     */   {
/* 280 */     Class dataWatcher = MinecraftReflection.getDataWatcherClass();
/*     */     try
/*     */     {
/* 283 */       if (CREATE_DATA_WATCHER_CONSTRUCTOR == null) {
/* 284 */         CREATE_DATA_WATCHER_CONSTRUCTOR = dataWatcher.getConstructor(new Class[] { MinecraftReflection.getEntityClass() });
/*     */       }
/* 286 */       return CREATE_DATA_WATCHER_CONSTRUCTOR.newInstance(new Object[] { BukkitUnwrapper.getInstance().unwrapItem(entity) });
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/* 290 */       throw new RuntimeException("Cannot construct data watcher.", e);
/*     */     }
/*     */   }
/*     */ 
/*     */   public WrappedDataWatcher(List<WrappedWatchableObject> watchableObjects)
/*     */     throws FieldAccessException
/*     */   {
/* 306 */     this();
/*     */ 
/* 308 */     Lock writeLock = getReadWriteLock().writeLock();
/* 309 */     Map map = getWatchableObjectMap();
/*     */ 
/* 311 */     writeLock.lock();
/*     */     try
/*     */     {
/* 315 */       for (WrappedWatchableObject watched : watchableObjects)
/* 316 */         map.put(Integer.valueOf(watched.getIndex()), watched.handle);
/*     */     }
/*     */     finally {
/* 319 */       writeLock.unlock();
/*     */     }
/*     */   }
/*     */ 
/*     */   public static Integer getTypeID(Class<?> clazz)
/*     */     throws FieldAccessException
/*     */   {
/* 329 */     initialize();
/* 330 */     Integer result = (Integer)TYPE_MAP.get(WrappedWatchableObject.getUnwrappedType(clazz));
/*     */ 
/* 332 */     if (result == null) {
/* 333 */       result = (Integer)CUSTOM_MAP.get(clazz);
/*     */     }
/* 335 */     return result;
/*     */   }
/*     */ 
/*     */   public static Class<?> getTypeClass(int id)
/*     */     throws FieldAccessException
/*     */   {
/* 344 */     initialize();
/*     */ 
/* 346 */     for (Map.Entry entry : TYPE_MAP.entrySet()) {
/* 347 */       if (Objects.equal(entry.getValue(), Integer.valueOf(id))) {
/* 348 */         return (Class)entry.getKey();
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 353 */     return null;
/*     */   }
/*     */ 
/*     */   public Byte getByte(int index)
/*     */     throws FieldAccessException
/*     */   {
/* 363 */     return (Byte)getObject(index);
/*     */   }
/*     */ 
/*     */   public Short getShort(int index)
/*     */     throws FieldAccessException
/*     */   {
/* 373 */     return (Short)getObject(index);
/*     */   }
/*     */ 
/*     */   public Integer getInteger(int index)
/*     */     throws FieldAccessException
/*     */   {
/* 383 */     return (Integer)getObject(index);
/*     */   }
/*     */ 
/*     */   public Float getFloat(int index)
/*     */     throws FieldAccessException
/*     */   {
/* 393 */     return (Float)getObject(index);
/*     */   }
/*     */ 
/*     */   public String getString(int index)
/*     */     throws FieldAccessException
/*     */   {
/* 403 */     return (String)getObject(index);
/*     */   }
/*     */ 
/*     */   public ItemStack getItemStack(int index)
/*     */     throws FieldAccessException
/*     */   {
/* 413 */     return (ItemStack)getObject(index);
/*     */   }
/*     */ 
/*     */   public WrappedChunkCoordinate getChunkCoordinate(int index)
/*     */     throws FieldAccessException
/*     */   {
/* 423 */     return (WrappedChunkCoordinate)getObject(index);
/*     */   }
/*     */ 
/*     */   public Object getObject(int index)
/*     */     throws FieldAccessException
/*     */   {
/* 433 */     Object watchable = getWatchedObject(index);
/*     */ 
/* 435 */     if (watchable != null) {
/* 436 */       return new WrappedWatchableObject(watchable).getValue();
/*     */     }
/* 438 */     return null;
/*     */   }
/*     */ 
/*     */   public List<WrappedWatchableObject> getWatchableObjects()
/*     */     throws FieldAccessException
/*     */   {
/* 448 */     Lock readLock = getReadWriteLock().readLock();
/* 449 */     readLock.lock();
/*     */     try
/*     */     {
/* 452 */       List result = new ArrayList();
/*     */ 
/* 455 */       for (Iterator i$ = getWatchableObjectMap().values().iterator(); i$.hasNext(); ) { Object watchable = i$.next();
/* 456 */         if (watchable != null)
/* 457 */           result.add(new WrappedWatchableObject(watchable));
/*     */         else {
/* 459 */           result.add(null);
/*     */         }
/*     */       }
/* 462 */       return result;
/*     */     }
/*     */     finally {
/* 465 */       readLock.unlock();
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean equals(Object obj)
/*     */   {
/* 472 */     if (obj == this)
/* 473 */       return true;
/* 474 */     if (obj == null) {
/* 475 */       return false;
/*     */     }
/* 477 */     if ((obj instanceof WrappedDataWatcher)) {
/* 478 */       WrappedDataWatcher other = (WrappedDataWatcher)obj;
/* 479 */       Iterator first = iterator(); Iterator second = other.iterator();
/*     */ 
/* 482 */       if (size() != other.size()) {
/* 483 */         return false;
/*     */       }
/* 485 */       while ((first.hasNext()) && (second.hasNext()))
/*     */       {
/* 487 */         if (!((WrappedWatchableObject)first.next()).equals(second.next()))
/* 488 */           return false;
/*     */       }
/* 490 */       return true;
/*     */     }
/* 492 */     return false;
/*     */   }
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 497 */     return getWatchableObjects().hashCode();
/*     */   }
/*     */ 
/*     */   public Set<Integer> indexSet()
/*     */     throws FieldAccessException
/*     */   {
/* 506 */     Lock readLock = getReadWriteLock().readLock();
/* 507 */     readLock.lock();
/*     */     try
/*     */     {
/* 510 */       return new HashSet(getWatchableObjectMap().keySet());
/*     */     } finally {
/* 512 */       readLock.unlock();
/*     */     }
/*     */   }
/*     */ 
/*     */   public WrappedDataWatcher deepClone()
/*     */   {
/* 521 */     WrappedDataWatcher clone = new WrappedDataWatcher();
/*     */ 
/* 524 */     for (WrappedWatchableObject watchable : this) {
/* 525 */       clone.setObject(watchable.getIndex(), watchable.getClonedValue());
/*     */     }
/* 527 */     return clone;
/*     */   }
/*     */ 
/*     */   public int size()
/*     */     throws FieldAccessException
/*     */   {
/* 536 */     Lock readLock = getReadWriteLock().readLock();
/* 537 */     readLock.lock();
/*     */     try
/*     */     {
/* 540 */       return getWatchableObjectMap().size();
/*     */     } finally {
/* 542 */       readLock.unlock();
/*     */     }
/*     */   }
/*     */ 
/*     */   public WrappedWatchableObject removeObject(int index)
/*     */   {
/* 552 */     Lock writeLock = getReadWriteLock().writeLock();
/* 553 */     writeLock.lock();
/*     */     try
/*     */     {
/* 556 */       Object removed = getWatchableObjectMap().remove(Integer.valueOf(index));
/* 557 */       return removed != null ? new WrappedWatchableObject(removed) : null;
/*     */     } finally {
/* 559 */       writeLock.unlock();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setObject(int index, Object newValue)
/*     */     throws FieldAccessException
/*     */   {
/* 570 */     setObject(index, newValue, true);
/*     */   }
/*     */ 
/*     */   public void setObject(int index, Object newValue, boolean update)
/*     */     throws FieldAccessException
/*     */   {
/* 582 */     Lock writeLock = getReadWriteLock().writeLock();
/* 583 */     writeLock.lock();
/*     */     try
/*     */     {
/* 586 */       Object watchable = getWatchedObject(index);
/*     */ 
/* 588 */       if (watchable != null)
/* 589 */         new WrappedWatchableObject(watchable).setValue(newValue, update);
/*     */       else {
/* 591 */         CREATE_KEY_VALUE_METHOD.invoke(this.handle, new Object[] { Integer.valueOf(index), WrappedWatchableObject.getUnwrapped(newValue) });
/*     */       }
/*     */     }
/*     */     catch (IllegalArgumentException e)
/*     */     {
/* 596 */       throw new FieldAccessException("Cannot convert arguments.", e);
/*     */     } catch (IllegalAccessException e) {
/* 598 */       throw new FieldAccessException("Illegal access.", e);
/*     */     } catch (InvocationTargetException e) {
/* 600 */       throw new FieldAccessException("Checked exception in Minecraft.", e);
/*     */     } finally {
/* 602 */       writeLock.unlock();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setObject(int index, Object newValue, Object secondary, boolean update, CustomType type)
/*     */     throws FieldAccessException
/*     */   {
/* 615 */     Object created = type.newInstance(newValue, secondary);
/*     */ 
/* 618 */     setObject(index, created, update);
/*     */   }
/*     */ 
/*     */   private Object getWatchedObject(int index) throws FieldAccessException
/*     */   {
/* 623 */     if (GET_KEY_VALUE_METHOD != null)
/*     */       try {
/* 625 */         return GET_KEY_VALUE_METHOD.invoke(this.handle, new Object[] { Integer.valueOf(index) });
/*     */       } catch (Exception e) {
/* 627 */         throw new FieldAccessException("Cannot invoke get key method for index " + index, e);
/*     */       }
/*     */     try
/*     */     {
/* 631 */       getReadWriteLock().readLock().lock();
/* 632 */       return getWatchableObjectMap().get(Integer.valueOf(index));
/*     */     }
/*     */     finally {
/* 635 */       getReadWriteLock().readLock().unlock();
/*     */     }
/*     */   }
/*     */ 
/*     */   protected ReadWriteLock getReadWriteLock()
/*     */     throws FieldAccessException
/*     */   {
/*     */     try
/*     */     {
/* 648 */       if (this.readWriteLock != null)
/* 649 */         return this.readWriteLock;
/* 650 */       if (READ_WRITE_LOCK_FIELD != null) {
/* 651 */         return this.readWriteLock = (ReadWriteLock)FieldUtils.readField(READ_WRITE_LOCK_FIELD, this.handle, true);
/*     */       }
/* 653 */       return this.readWriteLock = new ReentrantReadWriteLock();
/*     */     } catch (IllegalAccessException e) {
/* 655 */       throw new FieldAccessException("Unable to read lock field.", e);
/*     */     }
/*     */   }
/*     */ 
/*     */   protected Map<Integer, Object> getWatchableObjectMap()
/*     */     throws FieldAccessException
/*     */   {
/* 666 */     if (this.watchableObjects == null)
/* 667 */       this.watchableObjects = ((Map)VALUE_MAP_ACCESSOR.get(this.handle));
/* 668 */     return this.watchableObjects;
/*     */   }
/*     */ 
/*     */   public static WrappedDataWatcher getEntityWatcher(Entity entity)
/*     */     throws FieldAccessException
/*     */   {
/* 678 */     if (ENTITY_DATA_FIELD == null) {
/* 679 */       ENTITY_DATA_FIELD = FuzzyReflection.fromClass(MinecraftReflection.getEntityClass(), true).getFieldByType("datawatcher", MinecraftReflection.getDataWatcherClass());
/*     */     }
/*     */ 
/* 682 */     BukkitUnwrapper unwrapper = new BukkitUnwrapper();
/*     */     try
/*     */     {
/* 685 */       Object nsmWatcher = FieldUtils.readField(ENTITY_DATA_FIELD, unwrapper.unwrapItem(entity), true);
/*     */ 
/* 687 */       if (nsmWatcher != null) {
/* 688 */         return new WrappedDataWatcher(nsmWatcher);
/*     */       }
/* 690 */       return null;
/*     */     }
/*     */     catch (IllegalAccessException e) {
/* 693 */       throw new FieldAccessException("Cannot access DataWatcher field.", e);
/*     */     }
/*     */   }
/*     */ 
/*     */   private static void initialize()
/*     */     throws FieldAccessException
/*     */   {
/* 703 */     if (!HAS_INITIALIZED)
/* 704 */       HAS_INITIALIZED = true;
/*     */     else {
/* 706 */       return;
/*     */     }
/* 708 */     FuzzyReflection fuzzy = FuzzyReflection.fromClass(MinecraftReflection.getDataWatcherClass(), true);
/*     */ 
/* 710 */     for (Field lookup : fuzzy.getFieldListByType(Map.class)) {
/* 711 */       if (Modifier.isStatic(lookup.getModifiers()))
/*     */       {
/* 713 */         TYPE_MAP_ACCESSOR = Accessors.getFieldAccessor(lookup, true);
/*     */       }
/*     */       else {
/* 716 */         VALUE_MAP_ACCESSOR = Accessors.getFieldAccessor(lookup, true);
/*     */       }
/*     */     }
/*     */ 
/* 720 */     initializeSpigot(fuzzy);
/*     */ 
/* 723 */     CUSTOM_MAP = initializeCustom();
/*     */ 
/* 726 */     TYPE_MAP = (Map)TYPE_MAP_ACCESSOR.get(null);
/*     */     try
/*     */     {
/* 729 */       READ_WRITE_LOCK_FIELD = fuzzy.getFieldByType("readWriteLock", ReadWriteLock.class);
/*     */     }
/*     */     catch (IllegalArgumentException e)
/*     */     {
/*     */     }
/*     */ 
/* 735 */     if (MinecraftReflection.isUsingNetty()) {
/* 736 */       ENTITY_FIELD = fuzzy.getFieldByType("entity", MinecraftReflection.getEntityClass());
/* 737 */       ENTITY_FIELD.setAccessible(true);
/*     */     }
/* 739 */     initializeMethods(fuzzy);
/*     */   }
/*     */ 
/*     */   private static Map<Class<?>, Integer> initializeCustom()
/*     */   {
/* 744 */     Map map = Maps.newHashMap();
/*     */ 
/* 746 */     for (CustomType type : CustomType.values()) {
/* 747 */       if (type.getSpigotClass() != null) {
/* 748 */         map.put(type.getSpigotClass(), Integer.valueOf(type.getTypeId()));
/*     */       }
/*     */     }
/* 751 */     return map;
/*     */   }
/*     */ 
/*     */   private static void initializeSpigot(FuzzyReflection fuzzy)
/*     */   {
/* 757 */     if ((TYPE_MAP_ACCESSOR != null) && (VALUE_MAP_ACCESSOR != null)) {
/* 758 */       return;
/*     */     }
/* 760 */     for (Field lookup : fuzzy.getFields()) {
/* 761 */       Class type = lookup.getType();
/*     */ 
/* 763 */       if (TroveWrapper.isTroveClass(type))
/*     */       {
/* 765 */         ReadOnlyFieldAccessor accessor = TroveWrapper.wrapMapField(Accessors.getFieldAccessor(lookup, true), new Function()
/*     */         {
/*     */           public Integer apply(@Nullable Integer value)
/*     */           {
/* 770 */             if (value.intValue() == 0)
/* 771 */               return Integer.valueOf(-1);
/* 772 */             return value;
/*     */           }
/*     */         });
/* 776 */         if (Modifier.isStatic(lookup.getModifiers()))
/* 777 */           TYPE_MAP_ACCESSOR = accessor;
/*     */         else {
/* 779 */           VALUE_MAP_ACCESSOR = accessor;
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 784 */     if (TYPE_MAP_ACCESSOR == null)
/* 785 */       throw new IllegalArgumentException("Unable to find static type map.");
/* 786 */     if (VALUE_MAP_ACCESSOR == null)
/* 787 */       throw new IllegalArgumentException("Unable to find static value map.");
/*     */   }
/*     */ 
/*     */   private static void initializeMethods(FuzzyReflection fuzzy) {
/* 791 */     List candidates = fuzzy.getMethodListByParameters(Void.TYPE, new Class[] { Integer.TYPE, Object.class });
/*     */     try
/*     */     {
/* 796 */       GET_KEY_VALUE_METHOD = fuzzy.getMethodByParameters("getWatchableObject", MinecraftReflection.getWatchableObjectClass(), new Class[] { Integer.TYPE });
/*     */ 
/* 798 */       GET_KEY_VALUE_METHOD.setAccessible(true);
/*     */     }
/*     */     catch (IllegalArgumentException e)
/*     */     {
/*     */     }
/*     */ 
/* 804 */     for (Method method : candidates) {
/* 805 */       if (!method.getName().startsWith("watch"))
/* 806 */         CREATE_KEY_VALUE_METHOD = method;
/*     */       else {
/* 808 */         UPDATE_KEY_VALUE_METHOD = method;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 813 */     if ((UPDATE_KEY_VALUE_METHOD == null) || (CREATE_KEY_VALUE_METHOD == null))
/*     */     {
/* 815 */       if (candidates.size() > 1) {
/* 816 */         CREATE_KEY_VALUE_METHOD = (Method)candidates.get(0);
/* 817 */         UPDATE_KEY_VALUE_METHOD = (Method)candidates.get(1);
/*     */       } else {
/* 819 */         throw new IllegalStateException("Unable to find create and update watchable object. Update ProtocolLib.");
/*     */       }
/*     */ 
/*     */       try
/*     */       {
/* 824 */         WrappedDataWatcher watcher = new WrappedDataWatcher();
/* 825 */         watcher.setObject(0, Integer.valueOf(0));
/* 826 */         watcher.setObject(0, Integer.valueOf(1));
/*     */ 
/* 828 */         if (watcher.getInteger(0).intValue() != 1)
/* 829 */           throw new IllegalStateException("This cannot be!");
/*     */       }
/*     */       catch (Exception e)
/*     */       {
/* 833 */         UPDATE_KEY_VALUE_METHOD = (Method)candidates.get(0);
/* 834 */         CREATE_KEY_VALUE_METHOD = (Method)candidates.get(1);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public Iterator<WrappedWatchableObject> iterator()
/*     */   {
/* 842 */     return Iterators.transform(getWatchableObjectMap().values().iterator(), new Function()
/*     */     {
/*     */       public WrappedWatchableObject apply(@Nullable Object item)
/*     */       {
/* 847 */         if (item != null) {
/* 848 */           return new WrappedWatchableObject(item);
/*     */         }
/* 850 */         return null;
/*     */       }
/*     */     });
/*     */   }
/*     */ 
/*     */   public Map<Integer, WrappedWatchableObject> asMap()
/*     */   {
/* 863 */     if (this.mapView == null) {
/* 864 */       this.mapView = new ConvertedMap(getWatchableObjectMap())
/*     */       {
/*     */         protected Object toInner(WrappedWatchableObject outer) {
/* 867 */           if (outer == null)
/* 868 */             return null;
/* 869 */           return outer.getHandle();
/*     */         }
/*     */ 
/*     */         protected WrappedWatchableObject toOuter(Object inner)
/*     */         {
/* 874 */           if (inner == null)
/* 875 */             return null;
/* 876 */           return new WrappedWatchableObject(inner);
/*     */         }
/*     */       };
/*     */     }
/* 880 */     return this.mapView;
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 885 */     return asMap().toString();
/*     */   }
/*     */ 
/*     */   public Entity getEntity()
/*     */   {
/* 895 */     if (!MinecraftReflection.isUsingNetty())
/* 896 */       throw new IllegalStateException("This method is only supported on 1.7.2 and above.");
/*     */     try
/*     */     {
/* 899 */       return (Entity)MinecraftReflection.getBukkitEntity(ENTITY_FIELD.get(this.handle));
/*     */     } catch (Exception e) {
/* 901 */       throw new RuntimeException("Unable to retrieve entity.", e);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setEntity(Entity entity)
/*     */   {
/* 912 */     if (!MinecraftReflection.isUsingNetty())
/* 913 */       throw new IllegalStateException("This method is only supported on 1.7.2 and above.");
/*     */     try
/*     */     {
/* 916 */       ENTITY_FIELD.set(this.handle, BukkitUnwrapper.getInstance().unwrapItem(entity));
/*     */     } catch (Exception e) {
/* 918 */       throw new RuntimeException("Unable to set entity.", e);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static enum CustomType
/*     */   {
/*  66 */     BYTE_SHORT("org.spigotmc.ProtocolData$ByteShort", 0, new Class[] { Short.TYPE }), 
/*  67 */     DUAL_BYTE("org.spigotmc.ProtocolData$DualByte", 0, new Class[] { Byte.TYPE, Byte.TYPE }), 
/*  68 */     HIDDEN_BYTE("org.spigotmc.ProtocolData$HiddenByte", 0, new Class[] { Byte.TYPE }), 
/*  69 */     INT_BYTE("org.spigotmc.ProtocolData$IntByte", 2, new Class[] { Integer.TYPE, Byte.TYPE }), 
/*  70 */     DUAL_INT("org.spigotmc.ProtocolData$DualInt", 2, new Class[] { Integer.TYPE, Integer.TYPE });
/*     */ 
/*     */     private Class<?> spigotClass;
/*     */     private ConstructorAccessor constructor;
/*     */     private FieldAccessor secondaryValue;
/*     */     private int typeId;
/*     */ 
/*     */     private CustomType(String className, int typeId, Class<?>[] parameters) { try { this.spigotClass = Class.forName(className);
/*  80 */         this.constructor = Accessors.getConstructorAccessor(this.spigotClass, parameters);
/*  81 */         this.secondaryValue = (parameters.length > 1 ? Accessors.getFieldAccessor(this.spigotClass, "value2", true) : null);
/*     */       } catch (ClassNotFoundException e)
/*     */       {
/*  84 */         this.spigotClass = null;
/*     */       }
/*     */ 
/*  87 */       this.typeId = typeId;
/*     */     }
/*     */ 
/*     */     Object newInstance(Object value)
/*     */     {
/*  96 */       return newInstance(value, null);
/*     */     }
/*     */ 
/*     */     Object newInstance(Object value, Object secondary)
/*     */     {
/* 108 */       Preconditions.checkNotNull(value, "value cannot be NULL.");
/*     */ 
/* 110 */       if (hasSecondary()) {
/* 111 */         return this.constructor.invoke(new Object[] { value, secondary });
/*     */       }
/* 113 */       if (secondary != null) {
/* 114 */         throw new IllegalArgumentException("Cannot construct " + this + " with a secondary value");
/*     */       }
/* 116 */       return this.constructor.invoke(new Object[] { value });
/*     */     }
/*     */ 
/*     */     void setSecondary(Object instance, Object secondary)
/*     */     {
/* 126 */       if (!hasSecondary()) {
/* 127 */         throw new IllegalArgumentException(this + " does not have a secondary value.");
/*     */       }
/* 129 */       this.secondaryValue.set(instance, secondary);
/*     */     }
/*     */ 
/*     */     Object getSecondary(Object instance)
/*     */     {
/* 138 */       if (!hasSecondary()) {
/* 139 */         throw new IllegalArgumentException(this + " does not have a secondary value.");
/*     */       }
/* 141 */       return this.secondaryValue.get(instance);
/*     */     }
/*     */ 
/*     */     public boolean hasSecondary()
/*     */     {
/* 149 */       return this.secondaryValue != null;
/*     */     }
/*     */ 
/*     */     public Class<?> getSpigotClass()
/*     */     {
/* 157 */       return this.spigotClass;
/*     */     }
/*     */ 
/*     */     public int getTypeId()
/*     */     {
/* 165 */       return this.typeId;
/*     */     }
/*     */ 
/*     */     public static CustomType fromValue(Object value)
/*     */     {
/* 174 */       for (CustomType type : values()) {
/* 175 */         if (type.getSpigotClass().isInstance(value)) {
/* 176 */           return type;
/*     */         }
/*     */       }
/* 179 */       return null;
/*     */     }
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.wrappers.WrappedDataWatcher
 * JD-Core Version:    0.6.2
 */