/*     */ package com.comphenix.protocol.reflect.accessors;
/*     */ 
/*     */ import com.comphenix.protocol.reflect.ExactReflection;
/*     */ import com.comphenix.protocol.reflect.FuzzyReflection;
/*     */ import com.google.common.base.Joiner;
/*     */ import java.lang.reflect.Constructor;
/*     */ import java.lang.reflect.Field;
/*     */ import java.lang.reflect.Method;
/*     */ import java.util.List;
/*     */ 
/*     */ public final class Accessors
/*     */ {
/*     */   public static FieldAccessor getFieldAccessor(Class<?> instanceClass, Class<?> fieldClass, boolean forceAccess)
/*     */   {
/*  57 */     Field field = FuzzyReflection.fromClass(instanceClass, forceAccess).getFieldByType(null, fieldClass);
/*  58 */     return getFieldAccessor(field);
/*     */   }
/*     */ 
/*     */   public static FieldAccessor[] getFieldAccessorArray(Class<?> instanceClass, Class<?> fieldClass, boolean forceAccess)
/*     */   {
/*  69 */     List fields = FuzzyReflection.fromClass(instanceClass, forceAccess).getFieldListByType(fieldClass);
/*  70 */     FieldAccessor[] accessors = new FieldAccessor[fields.size()];
/*     */ 
/*  72 */     for (int i = 0; i < accessors.length; i++) {
/*  73 */       accessors[i] = getFieldAccessor((Field)fields.get(i));
/*     */     }
/*  75 */     return accessors;
/*     */   }
/*     */ 
/*     */   public static FieldAccessor getFieldAccessor(Class<?> instanceClass, String fieldName, boolean forceAccess)
/*     */   {
/*  87 */     return getFieldAccessor(ExactReflection.fromClass(instanceClass, true).getField(fieldName));
/*     */   }
/*     */ 
/*     */   public static FieldAccessor getFieldAccessor(Field field)
/*     */   {
/*  96 */     return getFieldAccessor(field, true);
/*     */   }
/*     */ 
/*     */   public static FieldAccessor getFieldAccessor(Field field, boolean forceAccess)
/*     */   {
/* 106 */     field.setAccessible(true);
/* 107 */     return new DefaultFieldAccessor(field);
/*     */   }
/*     */ 
/*     */   public static FieldAccessor getFieldAcccessorOrNull(Class<?> clazz, String fieldName, Class<?> fieldType)
/*     */   {
/*     */     try
/*     */     {
/* 119 */       FieldAccessor accessor = getFieldAccessor(clazz, fieldName, true);
/*     */ 
/* 122 */       if (fieldType.isAssignableFrom(accessor.getField().getType())) {
/* 123 */         return accessor;
/*     */       }
/* 125 */       return null; } catch (IllegalArgumentException e) {
/*     */     }
/* 127 */     return null;
/*     */   }
/*     */ 
/*     */   public static MethodAccessor getMethodAcccessorOrNull(Class<?> clazz, String methodName)
/*     */   {
/*     */     try
/*     */     {
/* 139 */       return getMethodAccessor(clazz, methodName, new Class[0]); } catch (IllegalArgumentException e) {
/*     */     }
/* 141 */     return null;
/*     */   }
/*     */ 
/*     */   public static ConstructorAccessor getConstructorAccessorOrNull(Class<?> clazz, Class<?>[] parameters)
/*     */   {
/*     */     try
/*     */     {
/* 153 */       return getConstructorAccessor(clazz, parameters); } catch (IllegalArgumentException e) {
/*     */     }
/* 155 */     return null;
/*     */   }
/*     */ 
/*     */   public static FieldAccessor getCached(FieldAccessor inner)
/*     */   {
/* 168 */     return new FieldAccessor() {
/* 169 */       private final Object EMPTY = new Object();
/* 170 */       private volatile Object value = this.EMPTY;
/*     */ 
/*     */       public void set(Object instance, Object value)
/*     */       {
/* 174 */         this.val$inner.set(instance, value);
/* 175 */         update(value);
/*     */       }
/*     */ 
/*     */       public Object get(Object instance)
/*     */       {
/* 180 */         Object cache = this.value;
/*     */ 
/* 182 */         if (cache != this.EMPTY)
/* 183 */           return cache;
/* 184 */         return update(this.val$inner.get(instance));
/*     */       }
/*     */ 
/*     */       private Object update(Object value)
/*     */       {
/* 193 */         return this.value = value;
/*     */       }
/*     */ 
/*     */       public Field getField()
/*     */       {
/* 198 */         return this.val$inner.getField();
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   public static FieldAccessor getSynchronized(FieldAccessor accessor)
/*     */   {
/* 210 */     if ((accessor instanceof SynchronizedFieldAccessor))
/* 211 */       return accessor;
/* 212 */     return new SynchronizedFieldAccessor(accessor, null);
/*     */   }
/*     */ 
/*     */   public static MethodAccessor getConstantAccessor(Object returnValue, final Method method)
/*     */   {
/* 222 */     return new MethodAccessor()
/*     */     {
/*     */       public Object invoke(Object target, Object[] args) {
/* 225 */         return this.val$returnValue;
/*     */       }
/*     */ 
/*     */       public Method getMethod()
/*     */       {
/* 230 */         return method;
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   public static MethodAccessor getMethodAccessor(Class<?> instanceClass, String methodName, Class<?>[] parameters)
/*     */   {
/* 243 */     return new DefaultMethodAccessor(ExactReflection.fromClass(instanceClass, true).getMethod(methodName, parameters));
/*     */   }
/*     */ 
/*     */   public static MethodAccessor getMethodAccessor(Method method)
/*     */   {
/* 252 */     return getMethodAccessor(method, true);
/*     */   }
/*     */ 
/*     */   public static MethodAccessor getMethodAccessor(Method method, boolean forceAccess)
/*     */   {
/* 262 */     method.setAccessible(forceAccess);
/* 263 */     return new DefaultMethodAccessor(method);
/*     */   }
/*     */ 
/*     */   public static ConstructorAccessor getConstructorAccessor(Class<?> instanceClass, Class<?>[] parameters)
/*     */   {
/*     */     try
/*     */     {
/* 276 */       return getConstructorAccessor(instanceClass.getDeclaredConstructor(parameters));
/*     */     } catch (NoSuchMethodException e) {
/* 278 */       throw new IllegalArgumentException(String.format("Unable to find constructor %s(%s).", new Object[] { instanceClass, Joiner.on(",").join(parameters) }));
/*     */     }
/*     */     catch (SecurityException e)
/*     */     {
/* 282 */       throw new IllegalStateException("Cannot access constructors.", e);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static ConstructorAccessor getConstructorAccessor(Constructor<?> constructor)
/*     */   {
/* 292 */     return new DefaultConstrutorAccessor(constructor);
/*     */   }
/*     */ 
/*     */   public static final class SynchronizedFieldAccessor
/*     */     implements FieldAccessor
/*     */   {
/*     */     private final FieldAccessor accessor;
/*     */ 
/*     */     private SynchronizedFieldAccessor(FieldAccessor accessor)
/*     */     {
/*  20 */       this.accessor = accessor;
/*     */     }
/*     */ 
/*     */     public void set(Object instance, Object value)
/*     */     {
/*  25 */       Object lock = this.accessor.get(instance);
/*     */ 
/*  27 */       if (lock != null)
/*  28 */         synchronized (lock) {
/*  29 */           this.accessor.set(instance, value);
/*     */         }
/*     */       else
/*  32 */         this.accessor.set(instance, value);
/*     */     }
/*     */ 
/*     */     public Object get(Object instance)
/*     */     {
/*  38 */       return this.accessor.get(instance);
/*     */     }
/*     */ 
/*     */     public Field getField()
/*     */     {
/*  43 */       return this.accessor.getField();
/*     */     }
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.reflect.accessors.Accessors
 * JD-Core Version:    0.6.2
 */