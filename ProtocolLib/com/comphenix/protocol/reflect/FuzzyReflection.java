/*     */ package com.comphenix.protocol.reflect;
/*     */ 
/*     */ import com.comphenix.protocol.reflect.accessors.Accessors;
/*     */ import com.comphenix.protocol.reflect.accessors.FieldAccessor;
/*     */ import com.comphenix.protocol.reflect.accessors.MethodAccessor;
/*     */ import com.comphenix.protocol.reflect.fuzzy.AbstractFuzzyMatcher;
/*     */ import com.comphenix.protocol.reflect.fuzzy.FuzzyMethodContract;
/*     */ import com.comphenix.protocol.reflect.fuzzy.FuzzyMethodContract.Builder;
/*     */ import com.google.common.collect.Lists;
/*     */ import com.google.common.collect.Maps;
/*     */ import com.google.common.collect.Sets;
/*     */ import java.lang.reflect.Constructor;
/*     */ import java.lang.reflect.Field;
/*     */ import java.lang.reflect.Method;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.LinkedHashSet;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.regex.Matcher;
/*     */ import java.util.regex.Pattern;
/*     */ 
/*     */ public class FuzzyReflection
/*     */ {
/*     */   private Class<?> source;
/*     */   private boolean forceAccess;
/*     */ 
/*     */   public FuzzyReflection(Class<?> source, boolean forceAccess)
/*     */   {
/*  52 */     this.source = source;
/*  53 */     this.forceAccess = forceAccess;
/*     */   }
/*     */ 
/*     */   public static FuzzyReflection fromClass(Class<?> source)
/*     */   {
/*  62 */     return fromClass(source, false);
/*     */   }
/*     */ 
/*     */   public static FuzzyReflection fromClass(Class<?> source, boolean forceAccess)
/*     */   {
/*  72 */     return new FuzzyReflection(source, forceAccess);
/*     */   }
/*     */ 
/*     */   public static FuzzyReflection fromObject(Object reference)
/*     */   {
/*  81 */     return new FuzzyReflection(reference.getClass(), false);
/*     */   }
/*     */ 
/*     */   public static FuzzyReflection fromObject(Object reference, boolean forceAccess)
/*     */   {
/*  91 */     return new FuzzyReflection(reference.getClass(), forceAccess);
/*     */   }
/*     */ 
/*     */   public static <T> T getFieldValue(Object instance, Class<T> fieldClass, boolean forceAccess)
/*     */   {
/* 104 */     Object result = Accessors.getFieldAccessor(instance.getClass(), fieldClass, forceAccess).get(instance);
/* 105 */     return result;
/*     */   }
/*     */ 
/*     */   public Class<?> getSource()
/*     */   {
/* 112 */     return this.source;
/*     */   }
/*     */ 
/*     */   public Object getSingleton()
/*     */   {
/* 121 */     Method method = null;
/* 122 */     Field field = null;
/*     */     try
/*     */     {
/* 125 */       method = getMethod(FuzzyMethodContract.newBuilder().parameterCount(0).returnDerivedOf(this.source).requireModifier(8).build());
/*     */     }
/*     */     catch (IllegalArgumentException e)
/*     */     {
/* 135 */       field = getFieldByType("instance", this.source);
/*     */     }
/*     */ 
/* 139 */     if (method != null) {
/*     */       try {
/* 141 */         method.setAccessible(true);
/* 142 */         return method.invoke(null, new Object[0]);
/*     */       } catch (Exception e) {
/* 144 */         throw new RuntimeException("Cannot invoke singleton method " + method, e);
/*     */       }
/*     */     }
/* 147 */     if (field != null) {
/*     */       try {
/* 149 */         field.setAccessible(true);
/* 150 */         return field.get(null);
/*     */       } catch (Exception e) {
/* 152 */         throw new IllegalArgumentException("Cannot get content of singleton field " + field, e);
/*     */       }
/*     */     }
/*     */ 
/* 156 */     throw new IllegalStateException("Impossible.");
/*     */   }
/*     */ 
/*     */   public Method getMethod(AbstractFuzzyMatcher<MethodInfo> matcher)
/*     */   {
/* 168 */     List result = getMethodList(matcher);
/*     */ 
/* 170 */     if (result.size() > 0) {
/* 171 */       return (Method)result.get(0);
/*     */     }
/* 173 */     throw new IllegalArgumentException("Unable to find a method that matches " + matcher);
/*     */   }
/*     */ 
/*     */   public List<Method> getMethodList(AbstractFuzzyMatcher<MethodInfo> matcher)
/*     */   {
/* 184 */     List methods = Lists.newArrayList();
/*     */ 
/* 187 */     for (Method method : getMethods()) {
/* 188 */       if (matcher.isMatch(MethodInfo.fromMethod(method), this.source)) {
/* 189 */         methods.add(method);
/*     */       }
/*     */     }
/* 192 */     return methods;
/*     */   }
/*     */ 
/*     */   public Method getMethodByName(String nameRegex)
/*     */   {
/* 202 */     Pattern match = Pattern.compile(nameRegex);
/*     */ 
/* 204 */     for (Method method : getMethods()) {
/* 205 */       if (match.matcher(method.getName()).matches())
/*     */       {
/* 207 */         return method;
/*     */       }
/*     */     }
/*     */ 
/* 211 */     throw new IllegalArgumentException("Unable to find a method with the pattern " + nameRegex + " in " + this.source.getName());
/*     */   }
/*     */ 
/*     */   public Method getMethodByParameters(String name, Class<?>[] args)
/*     */   {
/* 224 */     for (Method method : getMethods()) {
/* 225 */       if (Arrays.equals(method.getParameterTypes(), args)) {
/* 226 */         return method;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 231 */     throw new IllegalArgumentException("Unable to find " + name + " in " + this.source.getName());
/*     */   }
/*     */ 
/*     */   public Method getMethodByParameters(String name, Class<?> returnType, Class<?>[] args)
/*     */   {
/* 244 */     List methods = getMethodListByParameters(returnType, args);
/*     */ 
/* 246 */     if (methods.size() > 0) {
/* 247 */       return (Method)methods.get(0);
/*     */     }
/*     */ 
/* 250 */     throw new IllegalArgumentException("Unable to find " + name + " in " + this.source.getName());
/*     */   }
/*     */ 
/*     */   public Method getMethodByParameters(String name, String returnTypeRegex, String[] argsRegex)
/*     */   {
/* 263 */     Pattern match = Pattern.compile(returnTypeRegex);
/* 264 */     Pattern[] argMatch = new Pattern[argsRegex.length];
/*     */ 
/* 266 */     for (int i = 0; i < argsRegex.length; i++) {
/* 267 */       argMatch[i] = Pattern.compile(argsRegex[i]);
/*     */     }
/*     */ 
/* 271 */     for (Method method : getMethods()) {
/* 272 */       if ((match.matcher(method.getReturnType().getName()).matches()) && 
/* 273 */         (matchParameters(argMatch, method.getParameterTypes()))) {
/* 274 */         return method;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 279 */     throw new IllegalArgumentException("Unable to find " + name + " in " + this.source.getName());
/*     */   }
/*     */ 
/*     */   public Object invokeMethod(Object target, String name, Class<?> returnType, Object[] parameters)
/*     */   {
/* 293 */     Class[] types = new Class[parameters.length];
/*     */ 
/* 295 */     for (int i = 0; i < types.length; i++) {
/* 296 */       types[i] = parameters[i].getClass();
/*     */     }
/* 298 */     return Accessors.getMethodAccessor(getMethodByParameters(name, returnType, types)).invoke(target, parameters);
/*     */   }
/*     */ 
/*     */   private boolean matchParameters(Pattern[] parameterMatchers, Class<?>[] argTypes)
/*     */   {
/* 303 */     if (parameterMatchers.length != argTypes.length) {
/* 304 */       throw new IllegalArgumentException("Arrays must have the same cardinality.");
/*     */     }
/*     */ 
/* 307 */     for (int i = 0; i < argTypes.length; i++) {
/* 308 */       if (!parameterMatchers[i].matcher(argTypes[i].getName()).matches()) {
/* 309 */         return false;
/*     */       }
/*     */     }
/* 312 */     return true;
/*     */   }
/*     */ 
/*     */   public List<Method> getMethodListByParameters(Class<?> returnType, Class<?>[] args)
/*     */   {
/* 322 */     List methods = new ArrayList();
/*     */ 
/* 325 */     for (Method method : getMethods()) {
/* 326 */       if ((method.getReturnType().equals(returnType)) && (Arrays.equals(method.getParameterTypes(), args))) {
/* 327 */         methods.add(method);
/*     */       }
/*     */     }
/* 330 */     return methods;
/*     */   }
/*     */ 
/*     */   public Field getFieldByName(String nameRegex)
/*     */   {
/* 340 */     Pattern match = Pattern.compile(nameRegex);
/*     */ 
/* 342 */     for (Field field : getFields()) {
/* 343 */       if (match.matcher(field.getName()).matches())
/*     */       {
/* 345 */         return field;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 350 */     throw new IllegalArgumentException("Unable to find a field with the pattern " + nameRegex + " in " + this.source.getName());
/*     */   }
/*     */ 
/*     */   public Field getFieldByType(String name, Class<?> type)
/*     */   {
/* 361 */     List fields = getFieldListByType(type);
/*     */ 
/* 363 */     if (fields.size() > 0) {
/* 364 */       return (Field)fields.get(0);
/*     */     }
/*     */ 
/* 367 */     throw new IllegalArgumentException(String.format("Unable to find a field %s with the type %s in %s", new Object[] { name, type.getName(), this.source.getName() }));
/*     */   }
/*     */ 
/*     */   public List<Field> getFieldListByType(Class<?> type)
/*     */   {
/* 379 */     List fields = new ArrayList();
/*     */ 
/* 382 */     for (Field field : getFields())
/*     */     {
/* 384 */       if (type.isAssignableFrom(field.getType())) {
/* 385 */         fields.add(field);
/*     */       }
/*     */     }
/*     */ 
/* 389 */     return fields;
/*     */   }
/*     */ 
/*     */   public Field getField(AbstractFuzzyMatcher<Field> matcher)
/*     */   {
/* 401 */     List result = getFieldList(matcher);
/*     */ 
/* 403 */     if (result.size() > 0) {
/* 404 */       return (Field)result.get(0);
/*     */     }
/* 406 */     throw new IllegalArgumentException("Unable to find a field that matches " + matcher);
/*     */   }
/*     */ 
/*     */   public List<Field> getFieldList(AbstractFuzzyMatcher<Field> matcher)
/*     */   {
/* 417 */     List fields = Lists.newArrayList();
/*     */ 
/* 420 */     for (Field field : getFields()) {
/* 421 */       if (matcher.isMatch(field, this.source)) {
/* 422 */         fields.add(field);
/*     */       }
/*     */     }
/* 425 */     return fields;
/*     */   }
/*     */ 
/*     */   public Field getFieldByType(String typeRegex)
/*     */   {
/* 442 */     Pattern match = Pattern.compile(typeRegex);
/*     */ 
/* 445 */     for (Field field : getFields()) {
/* 446 */       String name = field.getType().getName();
/*     */ 
/* 448 */       if (match.matcher(name).matches()) {
/* 449 */         return field;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 454 */     throw new IllegalArgumentException("Unable to find a field with the type " + typeRegex + " in " + this.source.getName());
/*     */   }
/*     */ 
/*     */   public Field getFieldByType(String typeRegex, Set<Class> ignored)
/*     */   {
/* 474 */     Pattern match = Pattern.compile(typeRegex);
/*     */ 
/* 477 */     for (Field field : getFields()) {
/* 478 */       Class type = field.getType();
/*     */ 
/* 480 */       if ((!ignored.contains(type)) && (match.matcher(type.getName()).matches())) {
/* 481 */         return field;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 486 */     throw new IllegalArgumentException("Unable to find a field with the type " + typeRegex + " in " + this.source.getName());
/*     */   }
/*     */ 
/*     */   public Constructor<?> getConstructor(AbstractFuzzyMatcher<MethodInfo> matcher)
/*     */   {
/* 499 */     List result = getConstructorList(matcher);
/*     */ 
/* 501 */     if (result.size() > 0) {
/* 502 */       return (Constructor)result.get(0);
/*     */     }
/* 504 */     throw new IllegalArgumentException("Unable to find a method that matches " + matcher);
/*     */   }
/*     */ 
/*     */   public Map<String, Method> getMappedMethods(List<Method> methods)
/*     */   {
/* 515 */     Map map = Maps.newHashMap();
/*     */ 
/* 517 */     for (Method method : methods) {
/* 518 */       map.put(method.getName(), method);
/*     */     }
/* 520 */     return map;
/*     */   }
/*     */ 
/*     */   public List<Constructor<?>> getConstructorList(AbstractFuzzyMatcher<MethodInfo> matcher)
/*     */   {
/* 531 */     List constructors = Lists.newArrayList();
/*     */ 
/* 534 */     for (Constructor constructor : getConstructors()) {
/* 535 */       if (matcher.isMatch(MethodInfo.fromConstructor(constructor), this.source)) {
/* 536 */         constructors.add(constructor);
/*     */       }
/*     */     }
/* 539 */     return constructors;
/*     */   }
/*     */ 
/*     */   public Set<Field> getFields()
/*     */   {
/* 550 */     if (this.forceAccess) {
/* 551 */       return setUnion(new Field[][] { this.source.getDeclaredFields(), this.source.getFields() });
/*     */     }
/* 553 */     return setUnion(new Field[][] { this.source.getFields() });
/*     */   }
/*     */ 
/*     */   public Set<Field> getDeclaredFields(Class<?> excludeClass)
/*     */   {
/* 562 */     if (this.forceAccess) {
/* 563 */       Class current = this.source;
/* 564 */       Set fields = Sets.newLinkedHashSet();
/*     */ 
/* 566 */       while ((current != null) && (current != excludeClass)) {
/* 567 */         fields.addAll(Arrays.asList(current.getDeclaredFields()));
/* 568 */         current = current.getSuperclass();
/*     */       }
/* 570 */       return fields;
/*     */     }
/* 572 */     return getFields();
/*     */   }
/*     */ 
/*     */   public Set<Method> getMethods()
/*     */   {
/* 583 */     if (this.forceAccess) {
/* 584 */       return setUnion(new Method[][] { this.source.getDeclaredMethods(), this.source.getMethods() });
/*     */     }
/* 586 */     return setUnion(new Method[][] { this.source.getMethods() });
/*     */   }
/*     */ 
/*     */   public Set<Constructor<?>> getConstructors()
/*     */   {
/* 596 */     if (this.forceAccess) {
/* 597 */       return setUnion(new Constructor[][] { this.source.getDeclaredConstructors() });
/*     */     }
/* 599 */     return setUnion(new Constructor[][] { this.source.getConstructors() });
/*     */   }
/*     */ 
/*     */   private static <T> Set<T> setUnion(T[][] array)
/*     */   {
/* 604 */     Set result = new LinkedHashSet();
/*     */ 
/* 606 */     for (Object[] elements : array) {
/* 607 */       for (Object element : elements) {
/* 608 */         result.add(element);
/*     */       }
/*     */     }
/* 611 */     return result;
/*     */   }
/*     */ 
/*     */   public boolean isForceAccess()
/*     */   {
/* 619 */     return this.forceAccess;
/*     */   }
/*     */ 
/*     */   public void setForceAccess(boolean forceAccess)
/*     */   {
/* 627 */     this.forceAccess = forceAccess;
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.reflect.FuzzyReflection
 * JD-Core Version:    0.6.2
 */