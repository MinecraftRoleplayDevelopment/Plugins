/*     */ package com.comphenix.protocol.reflect.fuzzy;
/*     */ 
/*     */ import com.google.common.base.Objects;
/*     */ import com.google.common.collect.Maps;
/*     */ import java.lang.reflect.Member;
/*     */ import java.util.Map;
/*     */ import java.util.regex.Matcher;
/*     */ import java.util.regex.Pattern;
/*     */ import javax.annotation.Nonnull;
/*     */ 
/*     */ public abstract class AbstractFuzzyMember<T extends Member> extends AbstractFuzzyMatcher<T>
/*     */ {
/*     */   protected int modifiersRequired;
/*     */   protected int modifiersBanned;
/*     */   protected Pattern nameRegex;
/*  25 */   protected AbstractFuzzyMatcher<Class<?>> declaringMatcher = ClassExactMatcher.MATCH_ALL;
/*     */   protected transient boolean sealed;
/*     */ 
/*     */   protected AbstractFuzzyMember()
/*     */   {
/*     */   }
/*     */ 
/*     */   protected void prepareBuild()
/*     */   {
/*     */   }
/*     */ 
/*     */   protected AbstractFuzzyMember(AbstractFuzzyMember<T> other)
/*     */   {
/* 170 */     this.modifiersRequired = other.modifiersRequired;
/* 171 */     this.modifiersBanned = other.modifiersBanned;
/* 172 */     this.nameRegex = other.nameRegex;
/* 173 */     this.declaringMatcher = other.declaringMatcher;
/* 174 */     this.sealed = true;
/*     */   }
/*     */ 
/*     */   public int getModifiersRequired()
/*     */   {
/* 182 */     return this.modifiersRequired;
/*     */   }
/*     */ 
/*     */   public int getModifiersBanned()
/*     */   {
/* 190 */     return this.modifiersBanned;
/*     */   }
/*     */ 
/*     */   public Pattern getNameRegex()
/*     */   {
/* 198 */     return this.nameRegex;
/*     */   }
/*     */ 
/*     */   public AbstractFuzzyMatcher<Class<?>> getDeclaringMatcher()
/*     */   {
/* 206 */     return this.declaringMatcher;
/*     */   }
/*     */ 
/*     */   public boolean isMatch(T value, Object parent)
/*     */   {
/* 211 */     int mods = value.getModifiers();
/*     */ 
/* 214 */     return ((mods & this.modifiersRequired) == this.modifiersRequired) && ((mods & this.modifiersBanned) == 0) && (this.declaringMatcher.isMatch(value.getDeclaringClass(), value)) && (isNameMatch(value.getName()));
/*     */   }
/*     */ 
/*     */   private boolean isNameMatch(String name)
/*     */   {
/* 226 */     if (this.nameRegex == null) {
/* 227 */       return true;
/*     */     }
/* 229 */     return this.nameRegex.matcher(name).matches();
/*     */   }
/*     */ 
/*     */   protected int calculateRoundNumber()
/*     */   {
/* 235 */     if (!this.sealed) {
/* 236 */       throw new IllegalStateException("Cannot calculate round number during construction.");
/*     */     }
/*     */ 
/* 239 */     return this.declaringMatcher.getRoundNumber();
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 244 */     return getKeyValueView().toString();
/*     */   }
/*     */ 
/*     */   protected Map<String, Object> getKeyValueView()
/*     */   {
/* 254 */     Map map = Maps.newLinkedHashMap();
/*     */ 
/* 257 */     if ((this.modifiersRequired != 2147483647) || (this.modifiersBanned != 0)) {
/* 258 */       map.put("modifiers", String.format("[required: %s, banned: %s]", new Object[] { getBitView(this.modifiersRequired, 16), getBitView(this.modifiersBanned, 16) }));
/*     */     }
/*     */ 
/* 263 */     if (this.nameRegex != null) {
/* 264 */       map.put("name", this.nameRegex.pattern());
/*     */     }
/* 266 */     if (this.declaringMatcher != ClassExactMatcher.MATCH_ALL) {
/* 267 */       map.put("declaring", this.declaringMatcher);
/*     */     }
/*     */ 
/* 270 */     return map;
/*     */   }
/*     */ 
/*     */   private static String getBitView(int value, int bits) {
/* 274 */     if ((bits < 0) || (bits > 31)) {
/* 275 */       throw new IllegalArgumentException("Bits must be a value between 0 and 32");
/*     */     }
/*     */ 
/* 278 */     int snipped = value & (1 << bits) - 1;
/* 279 */     return Integer.toBinaryString(snipped);
/*     */   }
/*     */ 
/*     */   public boolean equals(Object obj)
/*     */   {
/* 285 */     if (this == obj)
/* 286 */       return true;
/* 287 */     if ((obj instanceof AbstractFuzzyMember))
/*     */     {
/* 289 */       AbstractFuzzyMember other = (AbstractFuzzyMember)obj;
/*     */ 
/* 291 */       return (this.modifiersBanned == other.modifiersBanned) && (this.modifiersRequired == other.modifiersRequired) && (FuzzyMatchers.checkPattern(this.nameRegex, other.nameRegex)) && (Objects.equal(this.declaringMatcher, other.declaringMatcher));
/*     */     }
/*     */ 
/* 296 */     return false;
/*     */   }
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 301 */     return Objects.hashCode(new Object[] { Integer.valueOf(this.modifiersBanned), Integer.valueOf(this.modifiersRequired), this.nameRegex != null ? this.nameRegex.pattern() : null, this.declaringMatcher });
/*     */   }
/*     */ 
/*     */   public static abstract class Builder<T extends AbstractFuzzyMember<?>>
/*     */   {
/*  38 */     protected T member = initialMember();
/*     */ 
/*     */     public Builder<T> requireModifier(int modifier)
/*     */     {
/*  46 */       this.member.modifiersRequired |= modifier;
/*  47 */       return this;
/*     */     }
/*     */ 
/*     */     public Builder<T> requirePublic()
/*     */     {
/*  55 */       return requireModifier(1);
/*     */     }
/*     */ 
/*     */     public Builder<T> banModifier(int modifier)
/*     */     {
/*  64 */       this.member.modifiersBanned |= modifier;
/*  65 */       return this;
/*     */     }
/*     */ 
/*     */     public Builder<T> nameRegex(String regex)
/*     */     {
/*  74 */       this.member.nameRegex = Pattern.compile(regex);
/*  75 */       return this;
/*     */     }
/*     */ 
/*     */     public Builder<T> nameRegex(Pattern pattern)
/*     */     {
/*  84 */       this.member.nameRegex = pattern;
/*  85 */       return this;
/*     */     }
/*     */ 
/*     */     public Builder<T> nameExact(String name)
/*     */     {
/*  96 */       return nameRegex(Pattern.quote(name));
/*     */     }
/*     */ 
/*     */     public Builder<T> declaringClassExactType(Class<?> declaringClass)
/*     */     {
/* 105 */       this.member.declaringMatcher = FuzzyMatchers.matchExact(declaringClass);
/* 106 */       return this;
/*     */     }
/*     */ 
/*     */     public Builder<T> declaringClassSuperOf(Class<?> declaringClass)
/*     */     {
/* 115 */       this.member.declaringMatcher = FuzzyMatchers.matchSuper(declaringClass);
/* 116 */       return this;
/*     */     }
/*     */ 
/*     */     public Builder<T> declaringClassDerivedOf(Class<?> declaringClass)
/*     */     {
/* 125 */       this.member.declaringMatcher = FuzzyMatchers.matchDerived(declaringClass);
/* 126 */       return this;
/*     */     }
/*     */ 
/*     */     public Builder<T> declaringClassMatching(AbstractFuzzyMatcher<Class<?>> classMatcher)
/*     */     {
/* 135 */       this.member.declaringMatcher = classMatcher;
/* 136 */       return this;
/*     */     }
/*     */ 
/*     */     @Nonnull
/*     */     protected abstract T initialMember();
/*     */ 
/*     */     public abstract T build();
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.reflect.fuzzy.AbstractFuzzyMember
 * JD-Core Version:    0.6.2
 */