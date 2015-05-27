/*     */ package com.comphenix.protocol.reflect.fuzzy;
/*     */ 
/*     */ import com.comphenix.protocol.reflect.FuzzyReflection;
/*     */ import com.comphenix.protocol.reflect.MethodInfo;
/*     */ import com.google.common.base.Joiner;
/*     */ import com.google.common.collect.ImmutableList;
/*     */ import com.google.common.collect.Lists;
/*     */ import com.google.common.collect.Maps;
/*     */ import java.lang.reflect.Field;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ 
/*     */ public class FuzzyClassContract extends AbstractFuzzyMatcher<Class<?>>
/*     */ {
/*     */   private final ImmutableList<AbstractFuzzyMatcher<Field>> fieldContracts;
/*     */   private final ImmutableList<AbstractFuzzyMatcher<MethodInfo>> methodContracts;
/*     */   private final ImmutableList<AbstractFuzzyMatcher<MethodInfo>> constructorContracts;
/*     */   private final ImmutableList<AbstractFuzzyMatcher<Class<?>>> baseclassContracts;
/*     */   private final ImmutableList<AbstractFuzzyMatcher<Class<?>>> interfaceContracts;
/*     */ 
/*     */   public static Builder newBuilder()
/*     */   {
/* 155 */     return new Builder();
/*     */   }
/*     */ 
/*     */   private FuzzyClassContract(Builder builder)
/*     */   {
/* 164 */     this.fieldContracts = ImmutableList.copyOf(builder.fieldContracts);
/* 165 */     this.methodContracts = ImmutableList.copyOf(builder.methodContracts);
/* 166 */     this.constructorContracts = ImmutableList.copyOf(builder.constructorContracts);
/* 167 */     this.baseclassContracts = ImmutableList.copyOf(builder.baseclassContracts);
/* 168 */     this.interfaceContracts = ImmutableList.copyOf(builder.interfaceContracts);
/*     */   }
/*     */ 
/*     */   public ImmutableList<AbstractFuzzyMatcher<Field>> getFieldContracts()
/*     */   {
/* 178 */     return this.fieldContracts;
/*     */   }
/*     */ 
/*     */   public ImmutableList<AbstractFuzzyMatcher<MethodInfo>> getMethodContracts()
/*     */   {
/* 188 */     return this.methodContracts;
/*     */   }
/*     */ 
/*     */   public ImmutableList<AbstractFuzzyMatcher<MethodInfo>> getConstructorContracts()
/*     */   {
/* 198 */     return this.constructorContracts;
/*     */   }
/*     */ 
/*     */   public ImmutableList<AbstractFuzzyMatcher<Class<?>>> getBaseclassContracts()
/*     */   {
/* 208 */     return this.baseclassContracts;
/*     */   }
/*     */ 
/*     */   public ImmutableList<AbstractFuzzyMatcher<Class<?>>> getInterfaceContracts()
/*     */   {
/* 218 */     return this.interfaceContracts;
/*     */   }
/*     */ 
/*     */   protected int calculateRoundNumber()
/*     */   {
/* 224 */     return combineRounds(new Integer[] { Integer.valueOf(findHighestRound(this.fieldContracts)), Integer.valueOf(findHighestRound(this.methodContracts)), Integer.valueOf(findHighestRound(this.constructorContracts)), Integer.valueOf(findHighestRound(this.interfaceContracts)), Integer.valueOf(findHighestRound(this.baseclassContracts)) });
/*     */   }
/*     */ 
/*     */   private <T> int findHighestRound(Collection<AbstractFuzzyMatcher<T>> list)
/*     */   {
/* 232 */     int highest = 0;
/*     */ 
/* 235 */     for (AbstractFuzzyMatcher matcher : list)
/* 236 */       highest = combineRounds(highest, matcher.getRoundNumber());
/* 237 */     return highest;
/*     */   }
/*     */ 
/*     */   public boolean isMatch(Class<?> value, Object parent)
/*     */   {
/* 242 */     FuzzyReflection reflection = FuzzyReflection.fromClass(value, true);
/*     */ 
/* 245 */     return ((this.fieldContracts.size() == 0) || (processContracts(reflection.getFields(), value, this.fieldContracts))) && ((this.methodContracts.size() == 0) || (processContracts(MethodInfo.fromMethods(reflection.getMethods()), value, this.methodContracts))) && ((this.constructorContracts.size() == 0) || (processContracts(MethodInfo.fromConstructors(value.getDeclaredConstructors()), value, this.constructorContracts))) && ((this.baseclassContracts.size() == 0) || (processValue(value.getSuperclass(), parent, this.baseclassContracts))) && ((this.interfaceContracts.size() == 0) || (processContracts(Arrays.asList(value.getInterfaces()), (Class)parent, this.interfaceContracts)));
/*     */   }
/*     */ 
/*     */   private <T> boolean processContracts(Collection<T> values, Object parent, List<AbstractFuzzyMatcher<T>> matchers)
/*     */   {
/* 258 */     boolean[] accepted = new boolean[matchers.size()];
/* 259 */     int count = accepted.length;
/*     */ 
/* 262 */     for (Iterator i$ = values.iterator(); i$.hasNext(); ) { Object value = i$.next();
/* 263 */       int index = processValue(value, parent, accepted, matchers);
/*     */ 
/* 266 */       if (index >= 0) {
/* 267 */         accepted[index] = true;
/* 268 */         count--;
/*     */       }
/*     */ 
/* 272 */       if (count == 0)
/* 273 */         return true;
/*     */     }
/* 275 */     return count == 0;
/*     */   }
/*     */ 
/*     */   private <T> boolean processValue(T value, Object parent, List<AbstractFuzzyMatcher<T>> matchers) {
/* 279 */     for (int i = 0; i < matchers.size(); i++) {
/* 280 */       if (((AbstractFuzzyMatcher)matchers.get(i)).isMatch(value, parent)) {
/* 281 */         return true;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 286 */     return false;
/*     */   }
/*     */ 
/*     */   private <T> int processValue(T value, Object parent, boolean[] accepted, List<AbstractFuzzyMatcher<T>> matchers)
/*     */   {
/* 291 */     for (int i = 0; i < matchers.size(); i++) {
/* 292 */       if (accepted[i] == 0) {
/* 293 */         AbstractFuzzyMatcher matcher = (AbstractFuzzyMatcher)matchers.get(i);
/*     */ 
/* 296 */         if (matcher.isMatch(value, parent)) {
/* 297 */           return i;
/*     */         }
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 303 */     return -1;
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 308 */     Map params = Maps.newLinkedHashMap();
/*     */ 
/* 310 */     if (this.fieldContracts.size() > 0) {
/* 311 */       params.put("fields", this.fieldContracts);
/*     */     }
/* 313 */     if (this.methodContracts.size() > 0) {
/* 314 */       params.put("methods", this.methodContracts);
/*     */     }
/* 316 */     if (this.constructorContracts.size() > 0) {
/* 317 */       params.put("constructors", this.constructorContracts);
/*     */     }
/* 319 */     if (this.baseclassContracts.size() > 0) {
/* 320 */       params.put("baseclasses", this.baseclassContracts);
/*     */     }
/* 322 */     if (this.interfaceContracts.size() > 0) {
/* 323 */       params.put("interfaces", this.interfaceContracts);
/*     */     }
/* 325 */     return "{\n  " + Joiner.on(", \n  ").join(params.entrySet()) + "\n}";
/*     */   }
/*     */ 
/*     */   public static class Builder
/*     */   {
/*  36 */     private List<AbstractFuzzyMatcher<Field>> fieldContracts = Lists.newArrayList();
/*  37 */     private List<AbstractFuzzyMatcher<MethodInfo>> methodContracts = Lists.newArrayList();
/*  38 */     private List<AbstractFuzzyMatcher<MethodInfo>> constructorContracts = Lists.newArrayList();
/*     */ 
/*  40 */     private List<AbstractFuzzyMatcher<Class<?>>> baseclassContracts = Lists.newArrayList();
/*  41 */     private List<AbstractFuzzyMatcher<Class<?>>> interfaceContracts = Lists.newArrayList();
/*     */ 
/*     */     public Builder field(AbstractFuzzyMatcher<Field> matcher)
/*     */     {
/*  49 */       this.fieldContracts.add(matcher);
/*  50 */       return this;
/*     */     }
/*     */ 
/*     */     public Builder field(FuzzyFieldContract.Builder builder)
/*     */     {
/*  59 */       return field(builder.build());
/*     */     }
/*     */ 
/*     */     public Builder method(AbstractFuzzyMatcher<MethodInfo> matcher)
/*     */     {
/*  68 */       this.methodContracts.add(matcher);
/*  69 */       return this;
/*     */     }
/*     */ 
/*     */     public Builder method(FuzzyMethodContract.Builder builder)
/*     */     {
/*  78 */       return method(builder.build());
/*     */     }
/*     */ 
/*     */     public Builder constructor(AbstractFuzzyMatcher<MethodInfo> matcher)
/*     */     {
/*  87 */       this.constructorContracts.add(matcher);
/*  88 */       return this;
/*     */     }
/*     */ 
/*     */     public Builder constructor(FuzzyMethodContract.Builder builder)
/*     */     {
/*  97 */       return constructor(builder.build());
/*     */     }
/*     */ 
/*     */     public Builder baseclass(AbstractFuzzyMatcher<Class<?>> matcher)
/*     */     {
/* 106 */       this.baseclassContracts.add(matcher);
/* 107 */       return this;
/*     */     }
/*     */ 
/*     */     public Builder baseclass(Builder builder)
/*     */     {
/* 116 */       return baseclass(builder.build());
/*     */     }
/*     */ 
/*     */     public Builder interfaces(AbstractFuzzyMatcher<Class<?>> matcher)
/*     */     {
/* 125 */       this.interfaceContracts.add(matcher);
/* 126 */       return this;
/*     */     }
/*     */ 
/*     */     public Builder interfaces(Builder builder)
/*     */     {
/* 135 */       return interfaces(builder.build());
/*     */     }
/*     */ 
/*     */     public FuzzyClassContract build() {
/* 139 */       Collections.sort(this.fieldContracts);
/* 140 */       Collections.sort(this.methodContracts);
/* 141 */       Collections.sort(this.constructorContracts);
/* 142 */       Collections.sort(this.baseclassContracts);
/* 143 */       Collections.sort(this.interfaceContracts);
/*     */ 
/* 146 */       return new FuzzyClassContract(this, null);
/*     */     }
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.reflect.fuzzy.FuzzyClassContract
 * JD-Core Version:    0.6.2
 */