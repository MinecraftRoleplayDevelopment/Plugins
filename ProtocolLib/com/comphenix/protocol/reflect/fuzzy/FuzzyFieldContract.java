/*     */ package com.comphenix.protocol.reflect.fuzzy;
/*     */ 
/*     */ import com.google.common.base.Objects;
/*     */ import java.lang.reflect.Field;
/*     */ import java.util.Map;
/*     */ import java.util.regex.Pattern;
/*     */ import javax.annotation.Nonnull;
/*     */ 
/*     */ public class FuzzyFieldContract extends AbstractFuzzyMember<Field>
/*     */ {
/*  17 */   private AbstractFuzzyMatcher<Class<?>> typeMatcher = ClassExactMatcher.MATCH_ALL;
/*     */ 
/*     */   public static FuzzyFieldContract matchType(AbstractFuzzyMatcher<Class<?>> matcher)
/*     */   {
/* 123 */     return newBuilder().typeMatches(matcher).build();
/*     */   }
/*     */ 
/*     */   public static Builder newBuilder()
/*     */   {
/* 131 */     return new Builder();
/*     */   }
/*     */ 
/*     */   private FuzzyFieldContract()
/*     */   {
/*     */   }
/*     */ 
/*     */   public AbstractFuzzyMatcher<Class<?>> getTypeMatcher()
/*     */   {
/* 144 */     return this.typeMatcher;
/*     */   }
/*     */ 
/*     */   private FuzzyFieldContract(FuzzyFieldContract other)
/*     */   {
/* 152 */     super(other);
/* 153 */     this.typeMatcher = other.typeMatcher;
/*     */   }
/*     */ 
/*     */   public boolean isMatch(Field value, Object parent)
/*     */   {
/* 158 */     if (super.isMatch(value, parent)) {
/* 159 */       return this.typeMatcher.isMatch(value.getType(), value);
/*     */     }
/*     */ 
/* 162 */     return false;
/*     */   }
/*     */ 
/*     */   protected int calculateRoundNumber()
/*     */   {
/* 168 */     return combineRounds(super.calculateRoundNumber(), this.typeMatcher.calculateRoundNumber());
/*     */   }
/*     */ 
/*     */   protected Map<String, Object> getKeyValueView()
/*     */   {
/* 174 */     Map member = super.getKeyValueView();
/*     */ 
/* 176 */     if (this.typeMatcher != ClassExactMatcher.MATCH_ALL) {
/* 177 */       member.put("type", this.typeMatcher);
/*     */     }
/* 179 */     return member;
/*     */   }
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 184 */     return Objects.hashCode(new Object[] { this.typeMatcher, Integer.valueOf(super.hashCode()) });
/*     */   }
/*     */ 
/*     */   public boolean equals(Object obj)
/*     */   {
/* 190 */     if (this == obj)
/* 191 */       return true;
/* 192 */     if (((obj instanceof FuzzyFieldContract)) && (super.equals(obj))) {
/* 193 */       return Objects.equal(this.typeMatcher, ((FuzzyFieldContract)obj).typeMatcher);
/*     */     }
/* 195 */     return true;
/*     */   }
/*     */ 
/*     */   public static class Builder extends AbstractFuzzyMember.Builder<FuzzyFieldContract>
/*     */   {
/*     */     public Builder requireModifier(int modifier)
/*     */     {
/*  27 */       super.requireModifier(modifier);
/*  28 */       return this;
/*     */     }
/*     */ 
/*     */     public Builder banModifier(int modifier)
/*     */     {
/*  33 */       super.banModifier(modifier);
/*  34 */       return this;
/*     */     }
/*     */ 
/*     */     public Builder requirePublic()
/*     */     {
/*  39 */       super.requirePublic();
/*  40 */       return this;
/*     */     }
/*     */ 
/*     */     public Builder nameRegex(String regex)
/*     */     {
/*  45 */       super.nameRegex(regex);
/*  46 */       return this;
/*     */     }
/*     */ 
/*     */     public Builder nameRegex(Pattern pattern)
/*     */     {
/*  51 */       super.nameRegex(pattern);
/*  52 */       return this;
/*     */     }
/*     */ 
/*     */     public Builder nameExact(String name)
/*     */     {
/*  57 */       super.nameExact(name);
/*  58 */       return this;
/*     */     }
/*     */ 
/*     */     public Builder declaringClassExactType(Class<?> declaringClass) {
/*  62 */       super.declaringClassExactType(declaringClass);
/*  63 */       return this;
/*     */     }
/*     */ 
/*     */     public Builder declaringClassSuperOf(Class<?> declaringClass)
/*     */     {
/*  68 */       super.declaringClassSuperOf(declaringClass);
/*  69 */       return this;
/*     */     }
/*     */ 
/*     */     public Builder declaringClassDerivedOf(Class<?> declaringClass)
/*     */     {
/*  74 */       super.declaringClassDerivedOf(declaringClass);
/*  75 */       return this;
/*     */     }
/*     */ 
/*     */     public Builder declaringClassMatching(AbstractFuzzyMatcher<Class<?>> classMatcher)
/*     */     {
/*  80 */       super.declaringClassMatching(classMatcher);
/*  81 */       return this;
/*     */     }
/*     */ 
/*     */     @Nonnull
/*     */     protected FuzzyFieldContract initialMember()
/*     */     {
/*  87 */       return new FuzzyFieldContract(null);
/*     */     }
/*     */ 
/*     */     public Builder typeExact(Class<?> type) {
/*  91 */       ((FuzzyFieldContract)this.member).typeMatcher = FuzzyMatchers.matchExact(type);
/*  92 */       return this;
/*     */     }
/*     */ 
/*     */     public Builder typeSuperOf(Class<?> type) {
/*  96 */       ((FuzzyFieldContract)this.member).typeMatcher = FuzzyMatchers.matchSuper(type);
/*  97 */       return this;
/*     */     }
/*     */ 
/*     */     public Builder typeDerivedOf(Class<?> type) {
/* 101 */       ((FuzzyFieldContract)this.member).typeMatcher = FuzzyMatchers.matchDerived(type);
/* 102 */       return this;
/*     */     }
/*     */ 
/*     */     public Builder typeMatches(AbstractFuzzyMatcher<Class<?>> matcher) {
/* 106 */       ((FuzzyFieldContract)this.member).typeMatcher = matcher;
/* 107 */       return this;
/*     */     }
/*     */ 
/*     */     public FuzzyFieldContract build()
/*     */     {
/* 112 */       ((FuzzyFieldContract)this.member).prepareBuild();
/* 113 */       return new FuzzyFieldContract((FuzzyFieldContract)this.member, null);
/*     */     }
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.reflect.fuzzy.FuzzyFieldContract
 * JD-Core Version:    0.6.2
 */