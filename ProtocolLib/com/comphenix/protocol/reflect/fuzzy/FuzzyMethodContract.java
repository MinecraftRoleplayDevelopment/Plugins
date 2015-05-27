/*     */ package com.comphenix.protocol.reflect.fuzzy;
/*     */ 
/*     */ import com.comphenix.protocol.reflect.MethodInfo;
/*     */ import com.google.common.base.Objects;
/*     */ import com.google.common.collect.ImmutableList;
/*     */ import com.google.common.collect.Lists;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.regex.Pattern;
/*     */ import javax.annotation.Nonnull;
/*     */ 
/*     */ public class FuzzyMethodContract extends AbstractFuzzyMember<MethodInfo>
/*     */ {
/*  81 */   private AbstractFuzzyMatcher<Class<?>> returnMatcher = ClassExactMatcher.MATCH_ALL;
/*     */   private List<ParameterClassMatcher> paramMatchers;
/*     */   private List<ParameterClassMatcher> exceptionMatchers;
/*     */   private Integer paramCount;
/*     */ 
/*     */   public static Builder newBuilder()
/*     */   {
/* 391 */     return new Builder();
/*     */   }
/*     */ 
/*     */   private FuzzyMethodContract()
/*     */   {
/* 396 */     this.paramMatchers = Lists.newArrayList();
/* 397 */     this.exceptionMatchers = Lists.newArrayList();
/*     */   }
/*     */ 
/*     */   private FuzzyMethodContract(FuzzyMethodContract other) {
/* 401 */     super(other);
/* 402 */     this.returnMatcher = other.returnMatcher;
/* 403 */     this.paramMatchers = other.paramMatchers;
/* 404 */     this.exceptionMatchers = other.exceptionMatchers;
/* 405 */     this.paramCount = other.paramCount;
/*     */   }
/*     */ 
/*     */   private static FuzzyMethodContract immutableCopy(FuzzyMethodContract other)
/*     */   {
/* 414 */     FuzzyMethodContract copy = new FuzzyMethodContract(other);
/*     */ 
/* 417 */     copy.paramMatchers = ImmutableList.copyOf(copy.paramMatchers);
/* 418 */     copy.exceptionMatchers = ImmutableList.copyOf(copy.exceptionMatchers);
/* 419 */     return copy;
/*     */   }
/*     */ 
/*     */   public AbstractFuzzyMatcher<Class<?>> getReturnMatcher()
/*     */   {
/* 427 */     return this.returnMatcher;
/*     */   }
/*     */ 
/*     */   public ImmutableList<ParameterClassMatcher> getParamMatchers()
/*     */   {
/* 435 */     if ((this.paramMatchers instanceof ImmutableList)) {
/* 436 */       return (ImmutableList)this.paramMatchers;
/*     */     }
/* 438 */     throw new IllegalStateException("Lists haven't been sealed yet.");
/*     */   }
/*     */ 
/*     */   public List<ParameterClassMatcher> getExceptionMatchers()
/*     */   {
/* 446 */     if ((this.exceptionMatchers instanceof ImmutableList)) {
/* 447 */       return this.exceptionMatchers;
/*     */     }
/* 449 */     throw new IllegalStateException("Lists haven't been sealed yet.");
/*     */   }
/*     */ 
/*     */   public Integer getParamCount()
/*     */   {
/* 457 */     return this.paramCount;
/*     */   }
/*     */ 
/*     */   protected void prepareBuild()
/*     */   {
/* 462 */     super.prepareBuild();
/*     */ 
/* 465 */     Collections.sort(this.paramMatchers);
/* 466 */     Collections.sort(this.exceptionMatchers);
/*     */   }
/*     */ 
/*     */   public boolean isMatch(MethodInfo value, Object parent)
/*     */   {
/* 471 */     if (super.isMatch(value, parent)) {
/* 472 */       Class[] params = value.getParameterTypes();
/* 473 */       Class[] exceptions = value.getExceptionTypes();
/*     */ 
/* 475 */       if (!this.returnMatcher.isMatch(value.getReturnType(), value))
/* 476 */         return false;
/* 477 */       if ((this.paramCount != null) && (this.paramCount.intValue() != value.getParameterTypes().length)) {
/* 478 */         return false;
/*     */       }
/*     */ 
/* 481 */       return (matchParameters(params, value, this.paramMatchers)) && (matchParameters(exceptions, value, this.exceptionMatchers));
/*     */     }
/*     */ 
/* 485 */     return false;
/*     */   }
/*     */ 
/*     */   private boolean matchParameters(Class<?>[] types, MethodInfo parent, List<ParameterClassMatcher> matchers) {
/* 489 */     boolean[] accepted = new boolean[matchers.size()];
/* 490 */     int count = accepted.length;
/*     */ 
/* 493 */     for (int i = 0; i < types.length; i++) {
/* 494 */       int matcherIndex = processValue(types[i], parent, i, accepted, matchers);
/*     */ 
/* 496 */       if (matcherIndex >= 0) {
/* 497 */         accepted[matcherIndex] = true;
/* 498 */         count--;
/*     */       }
/*     */ 
/* 502 */       if (count == 0)
/* 503 */         return true;
/*     */     }
/* 505 */     return count == 0;
/*     */   }
/*     */ 
/*     */   private int processValue(Class<?> value, MethodInfo parent, int index, boolean[] accepted, List<ParameterClassMatcher> matchers)
/*     */   {
/* 510 */     for (int i = 0; i < matchers.size(); i++) {
/* 511 */       if (accepted[i] == 0)
/*     */       {
/* 513 */         if (((ParameterClassMatcher)matchers.get(i)).isParameterMatch(value, parent, index)) {
/* 514 */           return i;
/*     */         }
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 520 */     return -1;
/*     */   }
/*     */ 
/*     */   protected int calculateRoundNumber()
/*     */   {
/* 525 */     int current = 0;
/*     */ 
/* 528 */     current = this.returnMatcher.getRoundNumber();
/*     */ 
/* 531 */     for (ParameterClassMatcher matcher : this.paramMatchers) {
/* 532 */       current = combineRounds(current, matcher.calculateRoundNumber());
/*     */     }
/*     */ 
/* 535 */     for (ParameterClassMatcher matcher : this.exceptionMatchers) {
/* 536 */       current = combineRounds(current, matcher.calculateRoundNumber());
/*     */     }
/*     */ 
/* 539 */     return combineRounds(super.calculateRoundNumber(), current);
/*     */   }
/*     */ 
/*     */   protected Map<String, Object> getKeyValueView()
/*     */   {
/* 544 */     Map member = super.getKeyValueView();
/*     */ 
/* 547 */     if (this.returnMatcher != ClassExactMatcher.MATCH_ALL) {
/* 548 */       member.put("return", this.returnMatcher);
/*     */     }
/* 550 */     if (this.paramMatchers.size() > 0) {
/* 551 */       member.put("params", this.paramMatchers);
/*     */     }
/* 553 */     if (this.exceptionMatchers.size() > 0) {
/* 554 */       member.put("exceptions", this.exceptionMatchers);
/*     */     }
/* 556 */     if (this.paramCount != null) {
/* 557 */       member.put("paramCount", this.paramCount);
/*     */     }
/* 559 */     return member;
/*     */   }
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 564 */     return Objects.hashCode(new Object[] { this.returnMatcher, this.paramMatchers, this.exceptionMatchers, this.paramCount, Integer.valueOf(super.hashCode()) });
/*     */   }
/*     */ 
/*     */   public boolean equals(Object obj)
/*     */   {
/* 570 */     if (this == obj)
/* 571 */       return true;
/* 572 */     if (((obj instanceof FuzzyMethodContract)) && (super.equals(obj))) {
/* 573 */       FuzzyMethodContract other = (FuzzyMethodContract)obj;
/*     */ 
/* 575 */       return (Objects.equal(this.paramCount, other.paramCount)) && (Objects.equal(this.returnMatcher, other.returnMatcher)) && (Objects.equal(this.paramMatchers, other.paramMatchers)) && (Objects.equal(this.exceptionMatchers, other.exceptionMatchers));
/*     */     }
/*     */ 
/* 580 */     return true;
/*     */   }
/*     */ 
/*     */   public static class Builder extends AbstractFuzzyMember.Builder<FuzzyMethodContract>
/*     */   {
/*     */     public Builder requireModifier(int modifier)
/*     */     {
/*  97 */       super.requireModifier(modifier);
/*  98 */       return this;
/*     */     }
/*     */ 
/*     */     public Builder requirePublic()
/*     */     {
/* 103 */       super.requirePublic();
/* 104 */       return this;
/*     */     }
/*     */ 
/*     */     public Builder banModifier(int modifier)
/*     */     {
/* 109 */       super.banModifier(modifier);
/* 110 */       return this;
/*     */     }
/*     */ 
/*     */     public Builder nameRegex(String regex)
/*     */     {
/* 115 */       super.nameRegex(regex);
/* 116 */       return this;
/*     */     }
/*     */ 
/*     */     public Builder nameRegex(Pattern pattern)
/*     */     {
/* 121 */       super.nameRegex(pattern);
/* 122 */       return this;
/*     */     }
/*     */ 
/*     */     public Builder nameExact(String name)
/*     */     {
/* 127 */       super.nameExact(name);
/* 128 */       return this;
/*     */     }
/*     */ 
/*     */     public Builder declaringClassExactType(Class<?> declaringClass)
/*     */     {
/* 133 */       super.declaringClassExactType(declaringClass);
/* 134 */       return this;
/*     */     }
/*     */ 
/*     */     public Builder declaringClassSuperOf(Class<?> declaringClass)
/*     */     {
/* 139 */       super.declaringClassSuperOf(declaringClass);
/* 140 */       return this;
/*     */     }
/*     */ 
/*     */     public Builder declaringClassDerivedOf(Class<?> declaringClass)
/*     */     {
/* 145 */       super.declaringClassDerivedOf(declaringClass);
/* 146 */       return this;
/*     */     }
/*     */ 
/*     */     public Builder declaringClassMatching(AbstractFuzzyMatcher<Class<?>> classMatcher)
/*     */     {
/* 151 */       super.declaringClassMatching(classMatcher);
/* 152 */       return this;
/*     */     }
/*     */ 
/*     */     public Builder parameterExactType(Class<?> type)
/*     */     {
/* 161 */       ((FuzzyMethodContract)this.member).paramMatchers.add(new FuzzyMethodContract.ParameterClassMatcher(FuzzyMatchers.matchExact(type)));
/* 162 */       return this;
/*     */     }
/*     */ 
/*     */     public Builder parameterSuperOf(Class<?> type)
/*     */     {
/* 173 */       ((FuzzyMethodContract)this.member).paramMatchers.add(new FuzzyMethodContract.ParameterClassMatcher(FuzzyMatchers.matchSuper(type)));
/* 174 */       return this;
/*     */     }
/*     */ 
/*     */     public Builder parameterDerivedOf(Class<?> type)
/*     */     {
/* 185 */       ((FuzzyMethodContract)this.member).paramMatchers.add(new FuzzyMethodContract.ParameterClassMatcher(FuzzyMatchers.matchDerived(type)));
/* 186 */       return this;
/*     */     }
/*     */ 
/*     */     public Builder parameterMatches(AbstractFuzzyMatcher<Class<?>> classMatcher)
/*     */     {
/* 195 */       ((FuzzyMethodContract)this.member).paramMatchers.add(new FuzzyMethodContract.ParameterClassMatcher(classMatcher));
/* 196 */       return this;
/*     */     }
/*     */ 
/*     */     public Builder parameterExactType(Class<?> type, int index)
/*     */     {
/* 206 */       ((FuzzyMethodContract)this.member).paramMatchers.add(new FuzzyMethodContract.ParameterClassMatcher(FuzzyMatchers.matchExact(type), Integer.valueOf(index)));
/* 207 */       return this;
/*     */     }
/*     */ 
/*     */     public Builder parameterExactArray(Class<?>[] types)
/*     */     {
/* 216 */       parameterCount(types.length);
/*     */ 
/* 218 */       for (int i = 0; i < types.length; i++) {
/* 219 */         parameterExactType(types[i], i);
/*     */       }
/* 221 */       return this;
/*     */     }
/*     */ 
/*     */     public Builder parameterSuperOf(Class<?> type, int index)
/*     */     {
/* 233 */       ((FuzzyMethodContract)this.member).paramMatchers.add(new FuzzyMethodContract.ParameterClassMatcher(FuzzyMatchers.matchSuper(type), Integer.valueOf(index)));
/* 234 */       return this;
/*     */     }
/*     */ 
/*     */     public Builder parameterDerivedOf(Class<?> type, int index)
/*     */     {
/* 246 */       ((FuzzyMethodContract)this.member).paramMatchers.add(new FuzzyMethodContract.ParameterClassMatcher(FuzzyMatchers.matchDerived(type), Integer.valueOf(index)));
/* 247 */       return this;
/*     */     }
/*     */ 
/*     */     public Builder parameterMatches(AbstractFuzzyMatcher<Class<?>> classMatcher, int index)
/*     */     {
/* 257 */       ((FuzzyMethodContract)this.member).paramMatchers.add(new FuzzyMethodContract.ParameterClassMatcher(classMatcher, Integer.valueOf(index)));
/* 258 */       return this;
/*     */     }
/*     */ 
/*     */     public Builder parameterCount(int expectedCount)
/*     */     {
/* 267 */       ((FuzzyMethodContract)this.member).paramCount = Integer.valueOf(expectedCount);
/* 268 */       return this;
/*     */     }
/*     */ 
/*     */     public Builder returnTypeVoid()
/*     */     {
/* 276 */       return returnTypeExact(Void.TYPE);
/*     */     }
/*     */ 
/*     */     public Builder returnTypeExact(Class<?> type)
/*     */     {
/* 285 */       ((FuzzyMethodContract)this.member).returnMatcher = FuzzyMatchers.matchExact(type);
/* 286 */       return this;
/*     */     }
/*     */ 
/*     */     public Builder returnDerivedOf(Class<?> type)
/*     */     {
/* 295 */       ((FuzzyMethodContract)this.member).returnMatcher = FuzzyMatchers.matchDerived(type);
/* 296 */       return this;
/*     */     }
/*     */ 
/*     */     public Builder returnTypeMatches(AbstractFuzzyMatcher<Class<?>> classMatcher)
/*     */     {
/* 305 */       ((FuzzyMethodContract)this.member).returnMatcher = classMatcher;
/* 306 */       return this;
/*     */     }
/*     */ 
/*     */     public Builder exceptionExactType(Class<?> type)
/*     */     {
/* 315 */       ((FuzzyMethodContract)this.member).exceptionMatchers.add(new FuzzyMethodContract.ParameterClassMatcher(FuzzyMatchers.matchExact(type)));
/* 316 */       return this;
/*     */     }
/*     */ 
/*     */     public Builder exceptionSuperOf(Class<?> type)
/*     */     {
/* 325 */       ((FuzzyMethodContract)this.member).exceptionMatchers.add(new FuzzyMethodContract.ParameterClassMatcher(FuzzyMatchers.matchSuper(type)));
/* 326 */       return this;
/*     */     }
/*     */ 
/*     */     public Builder exceptionMatches(AbstractFuzzyMatcher<Class<?>> classMatcher)
/*     */     {
/* 335 */       ((FuzzyMethodContract)this.member).exceptionMatchers.add(new FuzzyMethodContract.ParameterClassMatcher(classMatcher));
/* 336 */       return this;
/*     */     }
/*     */ 
/*     */     public Builder exceptionExactType(Class<?> type, int index)
/*     */     {
/* 346 */       ((FuzzyMethodContract)this.member).exceptionMatchers.add(new FuzzyMethodContract.ParameterClassMatcher(FuzzyMatchers.matchExact(type), Integer.valueOf(index)));
/* 347 */       return this;
/*     */     }
/*     */ 
/*     */     public Builder exceptionSuperOf(Class<?> type, int index)
/*     */     {
/* 357 */       ((FuzzyMethodContract)this.member).exceptionMatchers.add(new FuzzyMethodContract.ParameterClassMatcher(FuzzyMatchers.matchSuper(type), Integer.valueOf(index)));
/* 358 */       return this;
/*     */     }
/*     */ 
/*     */     public Builder exceptionMatches(AbstractFuzzyMatcher<Class<?>> classMatcher, int index)
/*     */     {
/* 368 */       ((FuzzyMethodContract)this.member).exceptionMatchers.add(new FuzzyMethodContract.ParameterClassMatcher(classMatcher, Integer.valueOf(index)));
/* 369 */       return this;
/*     */     }
/*     */ 
/*     */     @Nonnull
/*     */     protected FuzzyMethodContract initialMember()
/*     */     {
/* 376 */       return new FuzzyMethodContract(null);
/*     */     }
/*     */ 
/*     */     public FuzzyMethodContract build()
/*     */     {
/* 381 */       ((FuzzyMethodContract)this.member).prepareBuild();
/* 382 */       return FuzzyMethodContract.immutableCopy((FuzzyMethodContract)this.member);
/*     */     }
/*     */   }
/*     */ 
/*     */   private static class ParameterClassMatcher extends AbstractFuzzyMatcher<Class<?>[]>
/*     */   {
/*     */     private final AbstractFuzzyMatcher<Class<?>> typeMatcher;
/*     */     private final Integer indexMatch;
/*     */ 
/*     */     public ParameterClassMatcher(@Nonnull AbstractFuzzyMatcher<Class<?>> typeMatcher)
/*     */     {
/*  33 */       this(typeMatcher, null);
/*     */     }
/*     */ 
/*     */     public ParameterClassMatcher(@Nonnull AbstractFuzzyMatcher<Class<?>> typeMatcher, Integer indexMatch)
/*     */     {
/*  42 */       if (typeMatcher == null) {
/*  43 */         throw new IllegalArgumentException("Type matcher cannot be NULL.");
/*     */       }
/*  45 */       this.typeMatcher = typeMatcher;
/*  46 */       this.indexMatch = indexMatch;
/*     */     }
/*     */ 
/*     */     public boolean isParameterMatch(Class<?> param, MethodInfo parent, int index)
/*     */     {
/*  58 */       if ((this.indexMatch == null) || (this.indexMatch.intValue() == index)) {
/*  59 */         return this.typeMatcher.isMatch(param, parent);
/*     */       }
/*  61 */       return false;
/*     */     }
/*     */ 
/*     */     public boolean isMatch(Class<?>[] value, Object parent)
/*     */     {
/*  66 */       throw new UnsupportedOperationException("Use the parameter match instead.");
/*     */     }
/*     */ 
/*     */     protected int calculateRoundNumber()
/*     */     {
/*  71 */       return this.typeMatcher.getRoundNumber();
/*     */     }
/*     */ 
/*     */     public String toString()
/*     */     {
/*  76 */       return String.format("{Type: %s, Index: %s}", new Object[] { this.typeMatcher, this.indexMatch });
/*     */     }
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.reflect.fuzzy.FuzzyMethodContract
 * JD-Core Version:    0.6.2
 */