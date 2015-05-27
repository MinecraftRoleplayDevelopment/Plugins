/*     */ package com.comphenix.protocol.wrappers;
/*     */ 
/*     */ import com.comphenix.protocol.reflect.FuzzyReflection;
/*     */ import com.comphenix.protocol.reflect.fuzzy.FuzzyMethodContract;
/*     */ import com.comphenix.protocol.reflect.fuzzy.FuzzyMethodContract.Builder;
/*     */ import com.comphenix.protocol.utility.MinecraftReflection;
/*     */ import com.google.common.base.Preconditions;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.lang.reflect.Method;
/*     */ import java.lang.reflect.Modifier;
/*     */ import javax.annotation.Nonnull;
/*     */ 
/*     */ public class WrappedIntHashMap extends AbstractWrapper
/*     */ {
/*     */   private static Method PUT_METHOD;
/*     */   private static Method GET_METHOD;
/*     */   private static Method REMOVE_METHOD;
/*     */ 
/*     */   private WrappedIntHashMap(Object handle)
/*     */   {
/*  28 */     super(MinecraftReflection.getIntHashMapClass());
/*  29 */     setHandle(handle);
/*     */   }
/*     */ 
/*     */   public static WrappedIntHashMap newMap()
/*     */   {
/*     */     try
/*     */     {
/*  38 */       return new WrappedIntHashMap(MinecraftReflection.getIntHashMapClass().newInstance());
/*     */     } catch (Exception e) {
/*  40 */       throw new RuntimeException("Unable to construct IntHashMap.", e);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static WrappedIntHashMap fromHandle(@Nonnull Object handle)
/*     */   {
/*  51 */     return new WrappedIntHashMap(handle);
/*     */   }
/*     */ 
/*     */   public void put(int key, Object value)
/*     */   {
/*  63 */     Preconditions.checkNotNull(value, "value cannot be NULL.");
/*     */ 
/*  65 */     initializePutMethod();
/*  66 */     putInternal(key, value);
/*     */   }
/*     */ 
/*     */   private void putInternal(int key, Object value)
/*     */   {
/*  75 */     invokeMethod(PUT_METHOD, new Object[] { Integer.valueOf(key), value });
/*     */   }
/*     */ 
/*     */   public Object get(int key)
/*     */   {
/*  84 */     initializeGetMethod();
/*  85 */     return invokeMethod(GET_METHOD, new Object[] { Integer.valueOf(key) });
/*     */   }
/*     */ 
/*     */   public Object remove(int key)
/*     */   {
/*  94 */     initializeGetMethod();
/*     */ 
/*  96 */     if (REMOVE_METHOD == null)
/*  97 */       return removeFallback(key);
/*  98 */     return invokeMethod(REMOVE_METHOD, new Object[] { Integer.valueOf(key) });
/*     */   }
/*     */ 
/*     */   private Object removeFallback(int key)
/*     */   {
/* 107 */     Object old = get(key);
/*     */ 
/* 109 */     invokeMethod(PUT_METHOD, new Object[] { Integer.valueOf(key), null });
/* 110 */     return old;
/*     */   }
/*     */ 
/*     */   private Object invokeMethod(Method method, Object[] params)
/*     */   {
/*     */     try
/*     */     {
/* 121 */       return method.invoke(this.handle, params);
/*     */     } catch (IllegalArgumentException e) {
/* 123 */       throw new RuntimeException("Illegal argument.", e);
/*     */     } catch (IllegalAccessException e) {
/* 125 */       throw new RuntimeException("Cannot access method.", e);
/*     */     } catch (InvocationTargetException e) {
/* 127 */       throw new RuntimeException("Unable to invoke " + method + " on " + this.handle, e);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void initializePutMethod() {
/* 132 */     if (PUT_METHOD == null)
/*     */     {
/* 134 */       PUT_METHOD = FuzzyReflection.fromClass(MinecraftReflection.getIntHashMapClass()).getMethod(FuzzyMethodContract.newBuilder().banModifier(8).parameterCount(2).parameterExactType(Integer.TYPE).parameterExactType(Object.class).build());
/*     */     }
/*     */   }
/*     */ 
/*     */   private void initializeGetMethod()
/*     */   {
/* 145 */     if (GET_METHOD == null) {
/* 146 */       WrappedIntHashMap temp = newMap();
/* 147 */       String expected = "hello";
/*     */ 
/* 150 */       for (Method method : FuzzyReflection.fromClass(MinecraftReflection.getIntHashMapClass()).getMethodListByParameters(Object.class, new Class[] { Integer.TYPE }))
/*     */       {
/* 154 */         temp.put(1, expected);
/*     */ 
/* 157 */         if (!Modifier.isStatic(method.getModifiers()))
/*     */         {
/*     */           try
/*     */           {
/* 161 */             boolean first = expected.equals(method.invoke(temp.getHandle(), new Object[] { Integer.valueOf(1) }));
/* 162 */             boolean second = expected.equals(method.invoke(temp.getHandle(), new Object[] { Integer.valueOf(1) }));
/*     */ 
/* 165 */             if ((first) && (!second))
/* 166 */               REMOVE_METHOD = method;
/* 167 */             else if ((first) && (second))
/* 168 */               GET_METHOD = method;
/*     */           }
/*     */           catch (Exception e)
/*     */           {
/*     */           }
/*     */         }
/*     */       }
/* 175 */       if (GET_METHOD == null)
/* 176 */         throw new IllegalStateException("Unable to find appropriate GET_METHOD for IntHashMap.");
/*     */     }
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.wrappers.WrappedIntHashMap
 * JD-Core Version:    0.6.2
 */