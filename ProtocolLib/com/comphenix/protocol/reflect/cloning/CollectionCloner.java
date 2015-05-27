/*     */ package com.comphenix.protocol.reflect.cloning;
/*     */ 
/*     */ import java.io.Serializable;
/*     */ import java.lang.reflect.Array;
/*     */ import java.lang.reflect.Constructor;
/*     */ import java.lang.reflect.Method;
/*     */ import java.util.Collection;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ 
/*     */ public class CollectionCloner
/*     */   implements Cloner
/*     */ {
/*     */   private final Cloner defaultCloner;
/*     */ 
/*     */   public CollectionCloner(Cloner defaultCloner)
/*     */   {
/*  40 */     this.defaultCloner = defaultCloner;
/*     */   }
/*     */ 
/*     */   public boolean canClone(Object source)
/*     */   {
/*  45 */     if (source == null) {
/*  46 */       return false;
/*     */     }
/*  48 */     Class clazz = source.getClass();
/*  49 */     return (Collection.class.isAssignableFrom(clazz)) || (Map.class.isAssignableFrom(clazz)) || (clazz.isArray());
/*     */   }
/*     */ 
/*     */   public Object clone(Object source)
/*     */   {
/*  55 */     if (source == null) {
/*  56 */       throw new IllegalArgumentException("source cannot be NULL.");
/*     */     }
/*  58 */     Class clazz = source.getClass();
/*     */ 
/*  60 */     if ((source instanceof Collection)) {
/*  61 */       Collection copy = (Collection)cloneConstructor(Collection.class, clazz, source);
/*     */       Iterator i$;
/*     */       try {
/*  65 */         copy.clear();
/*     */ 
/*  67 */         for (i$ = ((Collection)source).iterator(); i$.hasNext(); ) { Object element = i$.next();
/*  68 */           copy.add(getClone(element, source)); }
/*     */       }
/*     */       catch (UnsupportedOperationException e)
/*     */       {
/*     */       }
/*  73 */       return copy;
/*     */     }
/*  75 */     if ((source instanceof Map)) {
/*  76 */       Map copy = (Map)cloneConstructor(Map.class, clazz, source);
/*     */       try
/*     */       {
/*  80 */         copy.clear();
/*     */ 
/*  82 */         for (Map.Entry element : ((Map)source).entrySet()) {
/*  83 */           Object key = getClone(element.getKey(), source);
/*  84 */           Object value = getClone(element.getValue(), source);
/*  85 */           copy.put(key, value);
/*     */         }
/*     */       }
/*     */       catch (UnsupportedOperationException e) {
/*     */       }
/*  90 */       return copy;
/*     */     }
/*  92 */     if (clazz.isArray())
/*     */     {
/*  94 */       int lenght = Array.getLength(source);
/*  95 */       Class component = clazz.getComponentType();
/*     */ 
/*  98 */       if (ImmutableDetector.isImmutable(component)) {
/*  99 */         return clonePrimitive(component, source);
/*     */       }
/*     */ 
/* 103 */       Object copy = Array.newInstance(clazz.getComponentType(), lenght);
/*     */ 
/* 106 */       for (int i = 0; i < lenght; i++) {
/* 107 */         Object element = Array.get(source, i);
/*     */ 
/* 109 */         if (this.defaultCloner.canClone(element))
/* 110 */           Array.set(copy, i, this.defaultCloner.clone(element));
/*     */         else {
/* 112 */           throw new IllegalArgumentException("Cannot clone " + element + " in array " + source);
/*     */         }
/*     */       }
/*     */ 
/* 116 */       return copy;
/*     */     }
/*     */ 
/* 119 */     throw new IllegalArgumentException(source + " is not an array nor a Collection.");
/*     */   }
/*     */ 
/*     */   private Object getClone(Object element, Object container)
/*     */   {
/* 129 */     if (this.defaultCloner.canClone(element)) {
/* 130 */       return this.defaultCloner.clone(element);
/*     */     }
/* 132 */     throw new IllegalArgumentException("Cannot clone " + element + " in container " + container);
/*     */   }
/*     */ 
/*     */   private Object clonePrimitive(Class<?> component, Object source)
/*     */   {
/* 143 */     if (Byte.TYPE.equals(component))
/* 144 */       return ((byte[])source).clone();
/* 145 */     if (Short.TYPE.equals(component))
/* 146 */       return ((short[])source).clone();
/* 147 */     if (Integer.TYPE.equals(component))
/* 148 */       return ((int[])source).clone();
/* 149 */     if (Long.TYPE.equals(component))
/* 150 */       return ((long[])source).clone();
/* 151 */     if (Float.TYPE.equals(component))
/* 152 */       return ((float[])source).clone();
/* 153 */     if (Double.TYPE.equals(component))
/* 154 */       return ((double[])source).clone();
/* 155 */     if (Character.TYPE.equals(component))
/* 156 */       return ((char[])source).clone();
/* 157 */     if (Boolean.TYPE.equals(component)) {
/* 158 */       return ((boolean[])source).clone();
/*     */     }
/* 160 */     return ((Object[])source).clone();
/*     */   }
/*     */ 
/*     */   private <T> T cloneConstructor(Class<?> superclass, Class<?> clazz, Object source)
/*     */   {
/*     */     try
/*     */     {
/* 174 */       Constructor constructCopy = clazz.getConstructor(new Class[] { Collection.class });
/* 175 */       return constructCopy.newInstance(new Object[] { source });
/*     */     } catch (NoSuchMethodException e) {
/* 177 */       if ((source instanceof Serializable)) {
/* 178 */         return new SerializableCloner().clone(source);
/*     */       }
/* 180 */       return cloneObject(clazz, source);
/*     */     } catch (Exception e) {
/* 182 */       throw new RuntimeException("Cannot construct collection.", e);
/*     */     }
/*     */   }
/*     */ 
/*     */   private Object cloneObject(Class<?> clazz, Object source)
/*     */   {
/*     */     try
/*     */     {
/* 195 */       return clazz.getMethod("clone", new Class[0]).invoke(source, new Object[0]);
/*     */     } catch (Exception e1) {
/* 197 */       throw new RuntimeException("Cannot copy " + source + " (" + clazz + ")", e1);
/*     */     }
/*     */   }
/*     */ 
/*     */   public Cloner getDefaultCloner()
/*     */   {
/* 206 */     return this.defaultCloner;
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.reflect.cloning.CollectionCloner
 * JD-Core Version:    0.6.2
 */