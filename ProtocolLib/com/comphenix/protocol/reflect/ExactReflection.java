/*     */ package com.comphenix.protocol.reflect;
/*     */ 
/*     */ import com.google.common.base.Preconditions;
/*     */ import java.lang.reflect.Field;
/*     */ import java.lang.reflect.Method;
/*     */ import java.lang.reflect.Modifier;
/*     */ import java.util.Arrays;
/*     */ import javax.annotation.Nonnull;
/*     */ 
/*     */ public class ExactReflection
/*     */ {
/*     */   private Class<?> source;
/*     */   private boolean forceAccess;
/*     */ 
/*     */   private ExactReflection(Class<?> source, boolean forceAccess)
/*     */   {
/*  18 */     this.source = ((Class)Preconditions.checkNotNull(source, "source class cannot be NULL"));
/*  19 */     this.forceAccess = forceAccess;
/*     */   }
/*     */ 
/*     */   public static ExactReflection fromClass(Class<?> source)
/*     */   {
/*  28 */     return fromClass(source, false);
/*     */   }
/*     */ 
/*     */   public static ExactReflection fromClass(Class<?> source, boolean forceAccess)
/*     */   {
/*  38 */     return new ExactReflection(source, forceAccess);
/*     */   }
/*     */ 
/*     */   public static ExactReflection fromObject(Object reference)
/*     */   {
/*  47 */     return new ExactReflection(reference.getClass(), false);
/*     */   }
/*     */ 
/*     */   public static ExactReflection fromObject(Object reference, boolean forceAccess)
/*     */   {
/*  57 */     return new ExactReflection(reference.getClass(), forceAccess);
/*     */   }
/*     */ 
/*     */   public Method getMethod(String methodName, Class<?>[] parameters)
/*     */   {
/*  70 */     return getMethod(this.source, methodName, parameters);
/*     */   }
/*     */ 
/*     */   private Method getMethod(Class<?> instanceClass, String methodName, Class<?>[] parameters)
/*     */   {
/*  75 */     for (Method method : instanceClass.getDeclaredMethods()) {
/*  76 */       if (((this.forceAccess) || (Modifier.isPublic(method.getModifiers()))) && ((methodName == null) || (method.getName().equals(methodName))) && (Arrays.equals(method.getParameterTypes(), parameters)))
/*     */       {
/*  80 */         method.setAccessible(true);
/*  81 */         return method;
/*     */       }
/*     */     }
/*     */ 
/*  85 */     if (instanceClass.getSuperclass() != null)
/*  86 */       return getMethod(instanceClass.getSuperclass(), methodName, parameters);
/*  87 */     throw new IllegalArgumentException(String.format("Unable to find method %s (%s) in %s.", new Object[] { methodName, Arrays.asList(parameters), this.source }));
/*     */   }
/*     */ 
/*     */   public Field getField(String fieldName)
/*     */   {
/*  99 */     return getField(this.source, fieldName);
/*     */   }
/*     */ 
/*     */   private Field getField(Class<?> instanceClass, @Nonnull String fieldName)
/*     */   {
/* 105 */     for (Field field : instanceClass.getDeclaredFields()) {
/* 106 */       if (field.getName().equals(fieldName)) {
/* 107 */         field.setAccessible(true);
/* 108 */         return field;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 113 */     if (instanceClass.getSuperclass() != null)
/* 114 */       return getField(instanceClass.getSuperclass(), fieldName);
/* 115 */     throw new IllegalArgumentException(String.format("Unable to find field %s in %s.", new Object[] { fieldName, this.source }));
/*     */   }
/*     */ 
/*     */   public ExactReflection forceAccess()
/*     */   {
/* 124 */     return new ExactReflection(this.source, true);
/*     */   }
/*     */ 
/*     */   public boolean isForceAccess()
/*     */   {
/* 133 */     return this.forceAccess;
/*     */   }
/*     */ 
/*     */   public Class<?> getSource()
/*     */   {
/* 141 */     return this.source;
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.reflect.ExactReflection
 * JD-Core Version:    0.6.2
 */