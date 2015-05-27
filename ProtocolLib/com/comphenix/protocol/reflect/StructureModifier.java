/*     */ package com.comphenix.protocol.reflect;
/*     */ 
/*     */ import com.comphenix.protocol.ProtocolLibrary;
/*     */ import com.comphenix.protocol.error.PluginContext;
/*     */ import com.comphenix.protocol.reflect.compiler.BackgroundCompiler;
/*     */ import com.comphenix.protocol.reflect.instances.BannedGenerator;
/*     */ import com.comphenix.protocol.reflect.instances.DefaultInstances;
/*     */ import com.comphenix.protocol.utility.MinecraftReflection;
/*     */ import com.comphenix.protocol.utility.Util;
/*     */ import com.google.common.base.Function;
/*     */ import com.google.common.base.Objects;
/*     */ import com.google.common.collect.ImmutableList;
/*     */ import com.google.common.collect.Lists;
/*     */ import java.lang.reflect.Field;
/*     */ import java.lang.reflect.Modifier;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.concurrent.ConcurrentHashMap;
/*     */ import java.util.logging.Level;
/*     */ 
/*     */ public class StructureModifier<TField>
/*     */ {
/*     */   protected Class targetType;
/*     */   protected Object target;
/*     */   protected EquivalentConverter<TField> converter;
/*     */   protected Class fieldType;
/*  62 */   protected List<Field> data = new ArrayList();
/*     */   protected Map<Field, Integer> defaultFields;
/*     */   protected Map<Class, StructureModifier> subtypeCache;
/*     */   protected boolean customConvertHandling;
/*     */   protected boolean useStructureCompiler;
/*  77 */   private static DefaultInstances DEFAULT_GENERATOR = getDefaultGenerator();
/*     */ 
/* 186 */   private static final List<String> BROKEN_PLUGINS = Util.asList(new String[] { "TagAPI" });
/*     */ 
/*     */   private static DefaultInstances getDefaultGenerator()
/*     */   {
/*  80 */     List providers = Lists.newArrayList();
/*     */ 
/*  83 */     providers.add(new BannedGenerator(new Class[] { MinecraftReflection.getItemStackClass(), MinecraftReflection.getBlockClass() }));
/*  84 */     providers.addAll(DefaultInstances.DEFAULT.getRegistered());
/*  85 */     return DefaultInstances.fromCollection(providers);
/*     */   }
/*     */ 
/*     */   public StructureModifier(Class targetType)
/*     */   {
/*  93 */     this(targetType, null, true);
/*     */   }
/*     */ 
/*     */   public StructureModifier(Class targetType, boolean useStructureCompiler)
/*     */   {
/* 102 */     this(targetType, null, true, useStructureCompiler);
/*     */   }
/*     */ 
/*     */   public StructureModifier(Class targetType, Class superclassExclude, boolean requireDefault)
/*     */   {
/* 112 */     this(targetType, superclassExclude, requireDefault, true);
/*     */   }
/*     */ 
/*     */   public StructureModifier(Class targetType, Class superclassExclude, boolean requireDefault, boolean useStructureCompiler)
/*     */   {
/* 123 */     List fields = getFields(targetType, superclassExclude);
/* 124 */     Map defaults = requireDefault ? generateDefaultFields(fields) : new HashMap();
/*     */ 
/* 126 */     initialize(targetType, Object.class, fields, defaults, null, new ConcurrentHashMap(), useStructureCompiler);
/*     */   }
/*     */ 
/*     */   protected StructureModifier()
/*     */   {
/*     */   }
/*     */ 
/*     */   protected void initialize(StructureModifier<TField> other)
/*     */   {
/* 142 */     initialize(other.targetType, other.fieldType, other.data, other.defaultFields, other.converter, other.subtypeCache, other.useStructureCompiler);
/*     */   }
/*     */ 
/*     */   protected void initialize(Class targetType, Class fieldType, List<Field> data, Map<Field, Integer> defaultFields, EquivalentConverter<TField> converter, Map<Class, StructureModifier> subTypeCache)
/*     */   {
/* 159 */     initialize(targetType, fieldType, data, defaultFields, converter, subTypeCache, true);
/*     */   }
/*     */ 
/*     */   protected void initialize(Class targetType, Class fieldType, List<Field> data, Map<Field, Integer> defaultFields, EquivalentConverter<TField> converter, Map<Class, StructureModifier> subTypeCache, boolean useStructureCompiler)
/*     */   {
/* 177 */     this.targetType = targetType;
/* 178 */     this.fieldType = fieldType;
/* 179 */     this.data = data;
/* 180 */     this.defaultFields = defaultFields;
/* 181 */     this.converter = converter;
/* 182 */     this.subtypeCache = subTypeCache;
/* 183 */     this.useStructureCompiler = useStructureCompiler;
/*     */   }
/*     */ 
/*     */   public TField read(int fieldIndex)
/*     */     throws FieldAccessException
/*     */   {
/*     */     try
/*     */     {
/* 196 */       return readInternal(fieldIndex);
/*     */     } catch (FieldAccessException ex) {
/* 198 */       String plugin = PluginContext.getPluginCaller(ex);
/* 199 */       if (BROKEN_PLUGINS.contains(plugin)) {
/* 200 */         ProtocolLibrary.log(Level.WARNING, "Encountered an exception caused by broken plugin {0}.", new Object[] { plugin });
/* 201 */         ProtocolLibrary.log(Level.WARNING, "It is advised that you remove it.", new Object[0]);
/*     */       }
/*     */ 
/* 204 */       throw ex;
/*     */     }
/*     */   }
/*     */ 
/*     */   private TField readInternal(int fieldIndex) throws FieldAccessException
/*     */   {
/* 210 */     if (this.target == null) {
/* 211 */       throw new IllegalStateException("Cannot read from a null target!");
/*     */     }
/* 213 */     if (fieldIndex < 0) {
/* 214 */       throw new FieldAccessException(String.format("Field index (%s) cannot be negative.", new Object[] { Integer.valueOf(fieldIndex) }));
/*     */     }
/* 216 */     if (this.data.size() == 0) {
/* 217 */       throw new FieldAccessException(String.format("No field with type %s exists in class %s.", new Object[] { this.fieldType.getName(), this.target.getClass().getSimpleName() }));
/*     */     }
/*     */ 
/* 220 */     if (fieldIndex >= this.data.size())
/* 221 */       throw new FieldAccessException(String.format("Field index out of bounds. (Index: %s, Size: %s)", new Object[] { Integer.valueOf(fieldIndex), Integer.valueOf(this.data.size()) }));
/*     */     try
/*     */     {
/* 224 */       Object result = FieldUtils.readField((Field)this.data.get(fieldIndex), this.target, true);
/*     */ 
/* 227 */       if (needConversion()) {
/* 228 */         return this.converter.getSpecific(result);
/*     */       }
/* 230 */       return result;
/*     */     }
/*     */     catch (IllegalAccessException e) {
/* 233 */       throw new FieldAccessException("Cannot read field due to a security limitation.", e);
/*     */     }
/*     */   }
/*     */ 
/*     */   public TField readSafely(int fieldIndex)
/*     */     throws FieldAccessException
/*     */   {
/* 244 */     if ((fieldIndex >= 0) && (fieldIndex < this.data.size())) {
/* 245 */       return read(fieldIndex);
/*     */     }
/* 247 */     return null;
/*     */   }
/*     */ 
/*     */   public boolean isReadOnly(int fieldIndex)
/*     */   {
/* 257 */     return Modifier.isFinal(getField(fieldIndex).getModifiers());
/*     */   }
/*     */ 
/*     */   public boolean isPublic(int fieldIndex)
/*     */   {
/* 266 */     return Modifier.isPublic(getField(fieldIndex).getModifiers());
/*     */   }
/*     */ 
/*     */   public void setReadOnly(int fieldIndex, boolean value)
/*     */     throws FieldAccessException
/*     */   {
/* 280 */     if ((fieldIndex < 0) || (fieldIndex >= this.data.size()))
/* 281 */       throw new IllegalArgumentException("Index parameter is not within [0 - " + this.data.size() + ")");
/*     */     try
/*     */     {
/* 284 */       setFinalState((Field)this.data.get(fieldIndex), value);
/*     */     } catch (IllegalAccessException e) {
/* 286 */       throw new FieldAccessException("Cannot write read only status due to a security limitation.", e);
/*     */     }
/*     */   }
/*     */ 
/*     */   protected static void setFinalState(Field field, boolean isReadOnly)
/*     */     throws IllegalAccessException
/*     */   {
/* 297 */     if (isReadOnly)
/* 298 */       FieldUtils.writeField(field, "modifiers", Integer.valueOf(field.getModifiers() | 0x10), true);
/*     */     else
/* 300 */       FieldUtils.writeField(field, "modifiers", Integer.valueOf(field.getModifiers() & 0xFFFFFFEF), true);
/*     */   }
/*     */ 
/*     */   public StructureModifier<TField> write(int fieldIndex, TField value)
/*     */     throws FieldAccessException
/*     */   {
/*     */     try
/*     */     {
/* 312 */       return writeInternal(fieldIndex, value);
/*     */     } catch (FieldAccessException ex) {
/* 314 */       String plugin = PluginContext.getPluginCaller(ex);
/* 315 */       if (BROKEN_PLUGINS.contains(plugin)) {
/* 316 */         ProtocolLibrary.log(Level.WARNING, "Encountered an exception caused by broken plugin {0}.", new Object[] { plugin });
/* 317 */         ProtocolLibrary.log(Level.WARNING, "It is advised that you remove it.", new Object[0]);
/*     */       }
/*     */ 
/* 320 */       throw ex;
/*     */     }
/*     */   }
/*     */ 
/*     */   private StructureModifier<TField> writeInternal(int fieldIndex, TField value) throws FieldAccessException {
/* 325 */     if (this.target == null) {
/* 326 */       throw new IllegalStateException("Cannot read from a null target!");
/*     */     }
/* 328 */     if (fieldIndex < 0) {
/* 329 */       throw new FieldAccessException(String.format("Field index (%s) cannot be negative.", new Object[] { Integer.valueOf(fieldIndex) }));
/*     */     }
/* 331 */     if (this.data.size() == 0) {
/* 332 */       throw new FieldAccessException(String.format("No field with type %s exists in class %s.", new Object[] { this.fieldType.getName(), this.target.getClass().getSimpleName() }));
/*     */     }
/*     */ 
/* 335 */     if (fieldIndex >= this.data.size()) {
/* 336 */       throw new FieldAccessException(String.format("Field index out of bounds. (Index: %s, Size: %s)", new Object[] { Integer.valueOf(fieldIndex), Integer.valueOf(this.data.size()) }));
/*     */     }
/*     */ 
/* 339 */     Object obj = needConversion() ? this.converter.getGeneric(getFieldType(fieldIndex), value) : value;
/*     */     try
/*     */     {
/* 342 */       FieldUtils.writeField((Field)this.data.get(fieldIndex), this.target, obj, true);
/*     */     } catch (IllegalAccessException e) {
/* 344 */       throw new FieldAccessException("Cannot read field due to a security limitation.", e);
/*     */     }
/*     */ 
/* 348 */     return this;
/*     */   }
/*     */ 
/*     */   protected Class<?> getFieldType(int index)
/*     */   {
/* 357 */     return ((Field)this.data.get(index)).getType();
/*     */   }
/*     */ 
/*     */   private final boolean needConversion()
/*     */   {
/* 365 */     return (this.converter != null) && (!this.customConvertHandling);
/*     */   }
/*     */ 
/*     */   public StructureModifier<TField> writeSafely(int fieldIndex, TField value)
/*     */     throws FieldAccessException
/*     */   {
/* 376 */     if ((fieldIndex >= 0) && (fieldIndex < this.data.size())) {
/* 377 */       write(fieldIndex, value);
/*     */     }
/* 379 */     return this;
/*     */   }
/*     */ 
/*     */   public StructureModifier<TField> modify(int fieldIndex, Function<TField, TField> select)
/*     */     throws FieldAccessException
/*     */   {
/* 390 */     Object value = read(fieldIndex);
/* 391 */     return write(fieldIndex, select.apply(value));
/*     */   }
/*     */ 
/*     */   public <T> StructureModifier<T> withType(Class fieldType)
/*     */   {
/* 400 */     return withType(fieldType, null);
/*     */   }
/*     */ 
/*     */   public StructureModifier<TField> writeDefaults()
/*     */     throws FieldAccessException
/*     */   {
/* 410 */     DefaultInstances generator = DefaultInstances.DEFAULT;
/*     */ 
/* 413 */     for (Field field : this.defaultFields.keySet()) {
/*     */       try {
/* 415 */         FieldUtils.writeField(field, this.target, generator.getDefault(field.getType()), true);
/*     */       }
/*     */       catch (IllegalAccessException e) {
/* 418 */         throw new FieldAccessException("Cannot write to field due to a security limitation.", e);
/*     */       }
/*     */     }
/*     */ 
/* 422 */     return this;
/*     */   }
/*     */ 
/*     */   public <T> StructureModifier<T> withType(Class fieldType, EquivalentConverter<T> converter)
/*     */   {
/* 433 */     StructureModifier result = (StructureModifier)this.subtypeCache.get(fieldType);
/*     */ 
/* 436 */     if (result == null) {
/* 437 */       List filtered = new ArrayList();
/* 438 */       Map defaults = new HashMap();
/* 439 */       int index = 0;
/*     */ 
/* 441 */       for (Field field : this.data) {
/* 442 */         if ((fieldType != null) && (fieldType.isAssignableFrom(field.getType()))) {
/* 443 */           filtered.add(field);
/*     */ 
/* 446 */           if (this.defaultFields.containsKey(field)) {
/* 447 */             defaults.put(field, Integer.valueOf(index));
/*     */           }
/*     */         }
/*     */ 
/* 451 */         index++;
/*     */       }
/*     */ 
/* 455 */       result = withFieldType(fieldType, filtered, defaults);
/*     */ 
/* 457 */       if (fieldType != null) {
/* 458 */         this.subtypeCache.put(fieldType, result);
/*     */ 
/* 461 */         if ((this.useStructureCompiler) && (BackgroundCompiler.getInstance() != null)) {
/* 462 */           BackgroundCompiler.getInstance().scheduleCompilation(this.subtypeCache, fieldType);
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 467 */     result = result.withTarget(this.target);
/*     */ 
/* 470 */     if (!Objects.equal(result.converter, converter)) {
/* 471 */       result = result.withConverter(converter);
/*     */     }
/* 473 */     return result;
/*     */   }
/*     */ 
/*     */   public Class getFieldType()
/*     */   {
/* 481 */     return this.fieldType;
/*     */   }
/*     */ 
/*     */   public Class getTargetType()
/*     */   {
/* 489 */     return this.targetType;
/*     */   }
/*     */ 
/*     */   public Object getTarget()
/*     */   {
/* 497 */     return this.target;
/*     */   }
/*     */ 
/*     */   public int size()
/*     */   {
/* 505 */     return this.data.size();
/*     */   }
/*     */ 
/*     */   protected <T> StructureModifier<T> withFieldType(Class fieldType, List<Field> filtered, Map<Field, Integer> defaults)
/*     */   {
/* 518 */     return withFieldType(fieldType, filtered, defaults, null);
/*     */   }
/*     */ 
/*     */   protected <T> StructureModifier<T> withFieldType(Class fieldType, List<Field> filtered, Map<Field, Integer> defaults, EquivalentConverter<T> converter)
/*     */   {
/* 533 */     StructureModifier result = new StructureModifier();
/* 534 */     result.initialize(this.targetType, fieldType, filtered, defaults, converter, new ConcurrentHashMap(), this.useStructureCompiler);
/*     */ 
/* 537 */     return result;
/*     */   }
/*     */ 
/*     */   public StructureModifier<TField> withTarget(Object target)
/*     */   {
/* 546 */     StructureModifier copy = new StructureModifier();
/*     */ 
/* 549 */     copy.initialize(this);
/* 550 */     copy.target = target;
/* 551 */     return copy;
/*     */   }
/*     */ 
/*     */   private <T> StructureModifier<T> withConverter(EquivalentConverter<T> converter)
/*     */   {
/* 561 */     StructureModifier copy = withTarget(this.target);
/*     */ 
/* 563 */     copy.setConverter(converter);
/* 564 */     return copy;
/*     */   }
/*     */ 
/*     */   protected void setConverter(EquivalentConverter<TField> converter)
/*     */   {
/* 572 */     this.converter = converter;
/*     */   }
/*     */ 
/*     */   public List<Field> getFields()
/*     */   {
/* 580 */     return ImmutableList.copyOf(this.data);
/*     */   }
/*     */ 
/*     */   public Field getField(int fieldIndex)
/*     */   {
/* 590 */     if ((fieldIndex < 0) || (fieldIndex >= this.data.size())) {
/* 591 */       throw new IllegalArgumentException("Index parameter is not within [0 - " + this.data.size() + ")");
/*     */     }
/* 593 */     return (Field)this.data.get(fieldIndex);
/*     */   }
/*     */ 
/*     */   public List<TField> getValues()
/*     */     throws FieldAccessException
/*     */   {
/* 602 */     List values = new ArrayList();
/*     */ 
/* 604 */     for (int i = 0; i < size(); i++) {
/* 605 */       values.add(read(i));
/*     */     }
/*     */ 
/* 608 */     return values;
/*     */   }
/*     */ 
/*     */   private static Map<Field, Integer> generateDefaultFields(List<Field> fields)
/*     */   {
/* 614 */     Map requireDefaults = new HashMap();
/* 615 */     DefaultInstances generator = DEFAULT_GENERATOR;
/* 616 */     int index = 0;
/*     */ 
/* 618 */     for (Field field : fields) {
/* 619 */       Class type = field.getType();
/* 620 */       int modifier = field.getModifiers();
/*     */ 
/* 623 */       if ((!type.isPrimitive()) && (!Modifier.isFinal(modifier)))
/*     */       {
/* 625 */         if (generator.getDefault(type) != null)
/*     */         {
/* 627 */           requireDefaults.put(field, Integer.valueOf(index));
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 632 */       index++;
/*     */     }
/*     */ 
/* 635 */     return requireDefaults;
/*     */   }
/*     */ 
/*     */   private static List<Field> getFields(Class type, Class superclassExclude)
/*     */   {
/* 640 */     List result = new ArrayList();
/*     */ 
/* 643 */     for (Field field : FuzzyReflection.fromClass(type, true).getDeclaredFields(superclassExclude)) {
/* 644 */       int mod = field.getModifiers();
/*     */ 
/* 647 */       if ((!Modifier.isStatic(mod)) && ((superclassExclude == null) || (!field.getDeclaringClass().equals(superclassExclude))))
/*     */       {
/* 651 */         result.add(field);
/*     */       }
/*     */     }
/* 654 */     return result;
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.reflect.StructureModifier
 * JD-Core Version:    0.6.2
 */