/*     */ package com.comphenix.protocol.reflect;
/*     */ 
/*     */ import com.google.common.collect.Lists;
/*     */ import java.lang.annotation.Annotation;
/*     */ import java.lang.reflect.Constructor;
/*     */ import java.lang.reflect.GenericDeclaration;
/*     */ import java.lang.reflect.Member;
/*     */ import java.lang.reflect.Method;
/*     */ import java.lang.reflect.TypeVariable;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collection;
/*     */ import java.util.List;
/*     */ 
/*     */ public abstract class MethodInfo
/*     */   implements GenericDeclaration, Member
/*     */ {
/*     */   public static MethodInfo fromMethod(Method method)
/*     */   {
/*  28 */     return new MethodInfo()
/*     */     {
/*     */       public <T extends Annotation> T getAnnotation(Class<T> annotationClass) {
/*  31 */         return this.val$method.getAnnotation(annotationClass);
/*     */       }
/*     */ 
/*     */       public Annotation[] getAnnotations() {
/*  35 */         return this.val$method.getAnnotations();
/*     */       }
/*     */ 
/*     */       public Annotation[] getDeclaredAnnotations() {
/*  39 */         return this.val$method.getDeclaredAnnotations();
/*     */       }
/*     */ 
/*     */       public String getName() {
/*  43 */         return this.val$method.getName();
/*     */       }
/*     */ 
/*     */       public Class<?>[] getParameterTypes() {
/*  47 */         return this.val$method.getParameterTypes();
/*     */       }
/*     */ 
/*     */       public Class<?> getDeclaringClass() {
/*  51 */         return this.val$method.getDeclaringClass();
/*     */       }
/*     */ 
/*     */       public Class<?> getReturnType() {
/*  55 */         return this.val$method.getReturnType();
/*     */       }
/*     */ 
/*     */       public int getModifiers() {
/*  59 */         return this.val$method.getModifiers();
/*     */       }
/*     */ 
/*     */       public Class<?>[] getExceptionTypes() {
/*  63 */         return this.val$method.getExceptionTypes();
/*     */       }
/*     */ 
/*     */       public TypeVariable<?>[] getTypeParameters() {
/*  67 */         return this.val$method.getTypeParameters();
/*     */       }
/*     */ 
/*     */       public String toGenericString() {
/*  71 */         return this.val$method.toGenericString();
/*     */       }
/*     */ 
/*     */       public String toString() {
/*  75 */         return this.val$method.toString();
/*     */       }
/*     */ 
/*     */       public boolean isSynthetic() {
/*  79 */         return this.val$method.isSynthetic();
/*     */       }
/*     */ 
/*     */       public int hashCode() {
/*  83 */         return this.val$method.hashCode();
/*     */       }
/*     */ 
/*     */       public boolean isConstructor() {
/*  87 */         return false;
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   public static Collection<MethodInfo> fromMethods(Method[] methods)
/*     */   {
/*  98 */     return fromMethods(Arrays.asList(methods));
/*     */   }
/*     */ 
/*     */   public static List<MethodInfo> fromMethods(Collection<Method> methods)
/*     */   {
/* 107 */     List infos = Lists.newArrayList();
/*     */ 
/* 109 */     for (Method method : methods)
/* 110 */       infos.add(fromMethod(method));
/* 111 */     return infos;
/*     */   }
/*     */ 
/*     */   public static MethodInfo fromConstructor(Constructor<?> constructor)
/*     */   {
/* 120 */     return new MethodInfo()
/*     */     {
/*     */       public <T extends Annotation> T getAnnotation(Class<T> annotationClass) {
/* 123 */         return this.val$constructor.getAnnotation(annotationClass);
/*     */       }
/*     */ 
/*     */       public Annotation[] getAnnotations() {
/* 127 */         return this.val$constructor.getAnnotations();
/*     */       }
/*     */ 
/*     */       public Annotation[] getDeclaredAnnotations() {
/* 131 */         return this.val$constructor.getDeclaredAnnotations();
/*     */       }
/*     */ 
/*     */       public String getName() {
/* 135 */         return this.val$constructor.getName();
/*     */       }
/*     */ 
/*     */       public Class<?>[] getParameterTypes() {
/* 139 */         return this.val$constructor.getParameterTypes();
/*     */       }
/*     */ 
/*     */       public Class<?> getDeclaringClass() {
/* 143 */         return this.val$constructor.getDeclaringClass();
/*     */       }
/*     */ 
/*     */       public Class<?> getReturnType() {
/* 147 */         return Void.class;
/*     */       }
/*     */ 
/*     */       public int getModifiers() {
/* 151 */         return this.val$constructor.getModifiers();
/*     */       }
/*     */ 
/*     */       public Class<?>[] getExceptionTypes() {
/* 155 */         return this.val$constructor.getExceptionTypes();
/*     */       }
/*     */ 
/*     */       public TypeVariable<?>[] getTypeParameters() {
/* 159 */         return this.val$constructor.getTypeParameters();
/*     */       }
/*     */ 
/*     */       public String toGenericString() {
/* 163 */         return this.val$constructor.toGenericString();
/*     */       }
/*     */ 
/*     */       public String toString() {
/* 167 */         return this.val$constructor.toString();
/*     */       }
/*     */ 
/*     */       public boolean isSynthetic() {
/* 171 */         return this.val$constructor.isSynthetic();
/*     */       }
/*     */ 
/*     */       public int hashCode() {
/* 175 */         return this.val$constructor.hashCode();
/*     */       }
/*     */ 
/*     */       public boolean isConstructor() {
/* 179 */         return true;
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   public static Collection<MethodInfo> fromConstructors(Constructor<?>[] constructors)
/*     */   {
/* 190 */     return fromConstructors(Arrays.asList(constructors));
/*     */   }
/*     */ 
/*     */   public static List<MethodInfo> fromConstructors(Collection<Constructor<?>> constructors)
/*     */   {
/* 199 */     List infos = Lists.newArrayList();
/*     */ 
/* 201 */     for (Constructor constructor : constructors)
/* 202 */       infos.add(fromConstructor(constructor));
/* 203 */     return infos;
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 214 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   public abstract String toGenericString();
/*     */ 
/*     */   public abstract Class<?>[] getExceptionTypes();
/*     */ 
/*     */   public abstract Class<?> getReturnType();
/*     */ 
/*     */   public abstract Class<?>[] getParameterTypes();
/*     */ 
/*     */   public abstract boolean isConstructor();
/*     */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.reflect.MethodInfo
 * JD-Core Version:    0.6.2
 */