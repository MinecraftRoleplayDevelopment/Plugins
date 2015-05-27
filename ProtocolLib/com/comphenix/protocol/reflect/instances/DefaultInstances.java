/*     */ package com.comphenix.protocol.reflect.instances;
/*     */ 
/*     */ import com.comphenix.net.sf.cglib.proxy.Enhancer;
/*     */ import com.comphenix.protocol.ProtocolLibrary;
/*     */ import com.google.common.base.Objects;
/*     */ import com.google.common.collect.ImmutableList;
/*     */ import java.lang.reflect.Constructor;
/*     */ import java.util.Collection;
/*     */ import java.util.List;
/*     */ import java.util.logging.Level;
/*     */ import javax.annotation.Nullable;
/*     */ 
/*     */ public class DefaultInstances
/*     */   implements InstanceProvider
/*     */ {
/*  43 */   public static final DefaultInstances DEFAULT = fromArray(new InstanceProvider[] { PrimitiveGenerator.INSTANCE, CollectionGenerator.INSTANCE });
/*     */ 
/*  49 */   private int maximumRecursion = 20;
/*     */   private ImmutableList<InstanceProvider> registered;
/*     */   private boolean nonNull;
/*     */ 
/*     */   public DefaultInstances(ImmutableList<InstanceProvider> registered)
/*     */   {
/*  66 */     this.registered = registered;
/*     */   }
/*     */ 
/*     */   public DefaultInstances(DefaultInstances other)
/*     */   {
/*  74 */     this.nonNull = other.nonNull;
/*  75 */     this.maximumRecursion = other.maximumRecursion;
/*  76 */     this.registered = other.registered;
/*     */   }
/*     */ 
/*     */   public DefaultInstances(InstanceProvider[] instaceProviders)
/*     */   {
/*  84 */     this(ImmutableList.copyOf(instaceProviders));
/*     */   }
/*     */ 
/*     */   public static DefaultInstances fromArray(InstanceProvider[] instanceProviders)
/*     */   {
/*  93 */     return new DefaultInstances(ImmutableList.copyOf(instanceProviders));
/*     */   }
/*     */ 
/*     */   public static DefaultInstances fromCollection(Collection<InstanceProvider> instanceProviders)
/*     */   {
/* 102 */     return new DefaultInstances(ImmutableList.copyOf(instanceProviders));
/*     */   }
/*     */ 
/*     */   public ImmutableList<InstanceProvider> getRegistered()
/*     */   {
/* 110 */     return this.registered;
/*     */   }
/*     */ 
/*     */   public boolean isNonNull()
/*     */   {
/* 118 */     return this.nonNull;
/*     */   }
/*     */ 
/*     */   public void setNonNull(boolean nonNull)
/*     */   {
/* 126 */     this.nonNull = nonNull;
/*     */   }
/*     */ 
/*     */   public int getMaximumRecursion()
/*     */   {
/* 134 */     return this.maximumRecursion;
/*     */   }
/*     */ 
/*     */   public void setMaximumRecursion(int maximumRecursion)
/*     */   {
/* 142 */     if (maximumRecursion < 1)
/* 143 */       throw new IllegalArgumentException("Maxmimum recursion height must be one or higher.");
/* 144 */     this.maximumRecursion = maximumRecursion;
/*     */   }
/*     */ 
/*     */   public <T> T getDefault(Class<T> type)
/*     */   {
/* 165 */     return getDefaultInternal(type, this.registered, 0);
/*     */   }
/*     */ 
/*     */   public <T> Constructor<T> getMinimumConstructor(Class<T> type)
/*     */   {
/* 174 */     return getMinimumConstructor(type, this.registered, 0);
/*     */   }
/*     */ 
/*     */   private <T> Constructor<T> getMinimumConstructor(Class<T> type, List<InstanceProvider> providers, int recursionLevel)
/*     */   {
/* 179 */     Constructor minimum = null;
/* 180 */     int lastCount = 2147483647;
/*     */ 
/* 183 */     for (Constructor candidate : type.getConstructors()) {
/* 184 */       Class[] types = candidate.getParameterTypes();
/*     */ 
/* 188 */       if ((types.length < lastCount) && 
/* 189 */         (!contains(types, type)) && (
/* 190 */         (!this.nonNull) || 
/* 192 */         (!isAnyNull(types, providers, recursionLevel))))
/*     */       {
/* 197 */         minimum = candidate;
/* 198 */         lastCount = types.length;
/*     */ 
/* 201 */         if (lastCount == 0)
/*     */         {
/*     */           break;
/*     */         }
/*     */       }
/*     */     }
/* 207 */     return minimum;
/*     */   }
/*     */ 
/*     */   private boolean isAnyNull(Class<?>[] types, List<InstanceProvider> providers, int recursionLevel)
/*     */   {
/* 221 */     for (Class type : types) {
/* 222 */       if (getDefaultInternal(type, providers, recursionLevel) == null) {
/* 223 */         return true;
/*     */       }
/*     */     }
/*     */ 
/* 227 */     return false;
/*     */   }
/*     */ 
/*     */   public <T> T getDefault(Class<T> type, List<InstanceProvider> providers)
/*     */   {
/* 249 */     return getDefaultInternal(type, providers, 0);
/*     */   }
/*     */ 
/*     */   private <T> T getDefaultInternal(Class<T> type, List<InstanceProvider> providers, int recursionLevel)
/*     */   {
/*     */     try
/*     */     {
/* 256 */       for (InstanceProvider generator : providers) {
/* 257 */         Object value = generator.create(type);
/*     */ 
/* 259 */         if (value != null)
/* 260 */           return value;
/*     */       }
/*     */     } catch (NotConstructableException e) {
/* 263 */       return null;
/*     */     }
/*     */ 
/* 267 */     if (recursionLevel >= this.maximumRecursion) {
/* 268 */       return null;
/*     */     }
/*     */ 
/* 271 */     Constructor minimum = getMinimumConstructor(type, providers, recursionLevel + 1);
/*     */     try
/*     */     {
/* 275 */       if (minimum != null) {
/* 276 */         int parameterCount = minimum.getParameterTypes().length;
/* 277 */         Object[] params = new Object[parameterCount];
/* 278 */         Class[] types = minimum.getParameterTypes();
/*     */ 
/* 281 */         for (int i = 0; i < parameterCount; i++) {
/* 282 */           params[i] = getDefaultInternal(types[i], providers, recursionLevel + 1);
/*     */ 
/* 285 */           if ((params[i] == null) && (this.nonNull)) {
/* 286 */             ProtocolLibrary.log(Level.WARNING, "Nonnull contract broken.", new Object[0]);
/* 287 */             return null;
/*     */           }
/*     */         }
/*     */ 
/* 291 */         return createInstance(type, minimum, types, params);
/*     */       }
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/*     */     }
/*     */ 
/* 298 */     return null;
/*     */   }
/*     */ 
/*     */   public DefaultInstances forEnhancer(Enhancer enhancer)
/*     */   {
/* 307 */     final Enhancer ex = enhancer;
/*     */ 
/* 309 */     return new DefaultInstances(this)
/*     */     {
/*     */       protected <T> T createInstance(Class<T> type, Constructor<T> constructor, Class<?>[] types, Object[] params)
/*     */       {
/* 314 */         return ex.create(types, params);
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   protected <T> T createInstance(Class<T> type, Constructor<T> constructor, Class<?>[] types, Object[] params)
/*     */   {
/*     */     try
/*     */     {
/* 330 */       return constructor.newInstance(params); } catch (Exception e) {
/*     */     }
/* 332 */     return null;
/*     */   }
/*     */ 
/*     */   protected <T> boolean contains(T[] elements, T elementToFind)
/*     */   {
/* 339 */     for (Object element : elements) {
/* 340 */       if (Objects.equal(elementToFind, element))
/* 341 */         return true;
/*     */     }
/* 343 */     return false;
/*     */   }
/*     */ 
/*     */   public Object create(@Nullable Class<?> type)
/*     */   {
/* 348 */     return getDefault(type);
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.reflect.instances.DefaultInstances
 * JD-Core Version:    0.6.2
 */