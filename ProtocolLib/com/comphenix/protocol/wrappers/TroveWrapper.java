/*     */ package com.comphenix.protocol.wrappers;
/*     */ 
/*     */ import com.comphenix.protocol.reflect.FieldAccessException;
/*     */ import com.comphenix.protocol.reflect.FieldUtils;
/*     */ import com.comphenix.protocol.reflect.accessors.FieldAccessor;
/*     */ import com.comphenix.protocol.reflect.accessors.ReadOnlyFieldAccessor;
/*     */ import com.comphenix.protocol.reflect.fuzzy.AbstractFuzzyMatcher;
/*     */ import com.comphenix.protocol.reflect.fuzzy.FuzzyMatchers;
/*     */ import com.comphenix.protocol.utility.ClassSource;
/*     */ import com.google.common.base.Function;
/*     */ import java.lang.reflect.Field;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.lang.reflect.Method;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import javax.annotation.Nonnull;
/*     */ 
/*     */ public class TroveWrapper
/*     */ {
/*  26 */   private static final String[] TROVE_LOCATIONS = { "net.minecraft.util.gnu.trove", "gnu.trove" };
/*     */ 
/*  32 */   private static final ClassSource[] TROVE_SOURCES = { ClassSource.fromPackage(TROVE_LOCATIONS[0]), ClassSource.fromPackage(TROVE_LOCATIONS[1]) };
/*     */ 
/*     */   public static ReadOnlyFieldAccessor wrapMapField(FieldAccessor accessor)
/*     */   {
/*  43 */     return wrapMapField(accessor, null);
/*     */   }
/*     */ 
/*     */   public static ReadOnlyFieldAccessor wrapMapField(FieldAccessor accessor, final Function<Integer, Integer> noEntryTransform)
/*     */   {
/*  53 */     return new ReadOnlyFieldAccessor() {
/*     */       public Object get(Object instance) {
/*  55 */         Object troveMap = this.val$accessor.get(instance);
/*     */ 
/*  58 */         if (noEntryTransform != null)
/*  59 */           TroveWrapper.transformNoEntryValue(troveMap, noEntryTransform);
/*  60 */         return TroveWrapper.getDecoratedMap(troveMap);
/*     */       }
/*     */       public Field getField() {
/*  63 */         return this.val$accessor.getField();
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   public static ReadOnlyFieldAccessor wrapSetField(FieldAccessor accessor)
/*     */   {
/*  74 */     return new ReadOnlyFieldAccessor() {
/*     */       public Object get(Object instance) {
/*  76 */         return TroveWrapper.getDecoratedSet(this.val$accessor.get(instance));
/*     */       }
/*     */       public Field getField() {
/*  79 */         return this.val$accessor.getField();
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   public static ReadOnlyFieldAccessor wrapListField(FieldAccessor accessor)
/*     */   {
/*  90 */     return new ReadOnlyFieldAccessor() {
/*     */       public Object get(Object instance) {
/*  92 */         return TroveWrapper.getDecoratedList(this.val$accessor.get(instance));
/*     */       }
/*     */       public Field getField() {
/*  95 */         return this.val$accessor.getField();
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   public static <TKey, TValue> Map<TKey, TValue> getDecoratedMap(@Nonnull Object troveMap)
/*     */   {
/* 110 */     Map result = (Map)getDecorated(troveMap);
/* 111 */     return result;
/*     */   }
/*     */ 
/*     */   public static <TValue> Set<TValue> getDecoratedSet(@Nonnull Object troveSet)
/*     */   {
/* 124 */     Set result = (Set)getDecorated(troveSet);
/* 125 */     return result;
/*     */   }
/*     */ 
/*     */   public static <TValue> List<TValue> getDecoratedList(@Nonnull Object troveList)
/*     */   {
/* 138 */     List result = (List)getDecorated(troveList);
/* 139 */     return result;
/*     */   }
/*     */ 
/*     */   public static boolean isTroveClass(Class<?> clazz)
/*     */   {
/* 148 */     return getClassSource(clazz) != null;
/*     */   }
/*     */ 
/*     */   public static void transformNoEntryValue(Object troveMap, Function<Integer, Integer> transform)
/*     */   {
/*     */     try
/*     */     {
/* 159 */       Field field = FieldUtils.getField(troveMap.getClass(), "no_entry_value", true);
/* 160 */       int current = ((Integer)FieldUtils.readField(field, troveMap, true)).intValue();
/* 161 */       int transformed = ((Integer)transform.apply(Integer.valueOf(current))).intValue();
/*     */ 
/* 163 */       if (current != transformed)
/* 164 */         FieldUtils.writeField(field, troveMap, Integer.valueOf(transformed));
/*     */     }
/*     */     catch (IllegalArgumentException e) {
/* 167 */       throw new CannotFindTroveNoEntryValue(e, null);
/*     */     } catch (IllegalAccessException e) {
/* 169 */       throw new IllegalStateException("Cannot access reflection.", e);
/*     */     }
/*     */   }
/*     */ 
/*     */   private static ClassSource getClassSource(Class<?> clazz)
/*     */   {
/* 179 */     for (int i = 0; i < TROVE_LOCATIONS.length; i++) {
/* 180 */       if (clazz.getCanonicalName().startsWith(TROVE_LOCATIONS[i])) {
/* 181 */         return TROVE_SOURCES[i];
/*     */       }
/*     */     }
/* 184 */     return null;
/*     */   }
/*     */ 
/*     */   private static Object getDecorated(@Nonnull Object trove)
/*     */   {
/* 193 */     if (trove == null) {
/* 194 */       throw new IllegalArgumentException("trove instance cannot be non-null.");
/*     */     }
/* 196 */     AbstractFuzzyMatcher match = FuzzyMatchers.matchSuper(trove.getClass());
/* 197 */     Class decorators = null;
/*     */     try
/*     */     {
/* 201 */       decorators = getClassSource(trove.getClass()).loadClass("TDecorators");
/*     */     } catch (ClassNotFoundException e) {
/* 203 */       throw new IllegalStateException(e.getMessage(), e);
/*     */     }
/*     */ 
/* 207 */     for (Method method : decorators.getMethods()) {
/* 208 */       Class[] types = method.getParameterTypes();
/*     */ 
/* 210 */       if ((types.length == 1) && (match.isMatch(types[0], null))) {
/*     */         try {
/* 212 */           Object result = method.invoke(null, new Object[] { trove });
/*     */ 
/* 214 */           if (result == null) {
/* 215 */             throw new FieldAccessException("Wrapper returned NULL.");
/*     */           }
/* 217 */           return result;
/*     */         }
/*     */         catch (IllegalArgumentException e) {
/* 220 */           throw new FieldAccessException("Cannot invoke wrapper method.", e);
/*     */         } catch (IllegalAccessException e) {
/* 222 */           throw new FieldAccessException("Illegal access.", e);
/*     */         } catch (InvocationTargetException e) {
/* 224 */           throw new FieldAccessException("Error in invocation.", e);
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 229 */     throw new IllegalArgumentException("Cannot find decorator for " + trove + " (" + trove.getClass() + ")");
/*     */   }
/*     */ 
/*     */   public static class CannotFindTroveNoEntryValue extends RuntimeException {
/*     */     private static final long serialVersionUID = 1L;
/*     */ 
/*     */     private CannotFindTroveNoEntryValue(Throwable inner) {
/* 236 */       super(inner);
/*     */     }
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.wrappers.TroveWrapper
 * JD-Core Version:    0.6.2
 */