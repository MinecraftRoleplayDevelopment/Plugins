/*     */ package com.comphenix.protocol.reflect.fuzzy;
/*     */ 
/*     */ import com.google.common.base.Preconditions;
/*     */ import com.google.common.collect.Sets;
/*     */ import java.lang.reflect.Member;
/*     */ import java.util.Set;
/*     */ import java.util.regex.Pattern;
/*     */ import javax.annotation.Nonnull;
/*     */ 
/*     */ public class FuzzyMatchers
/*     */ {
/*  19 */   private static AbstractFuzzyMatcher<Class<?>> MATCH_ALL = new AbstractFuzzyMatcher()
/*     */   {
/*     */     public boolean isMatch(Class<?> value, Object parent) {
/*  22 */       return true;
/*     */     }
/*     */ 
/*     */     protected int calculateRoundNumber()
/*     */     {
/*  27 */       return 0;
/*     */     }
/*  19 */   };
/*     */ 
/*     */   public static AbstractFuzzyMatcher<Class<?>> matchArray(@Nonnull AbstractFuzzyMatcher<Class<?>> componentMatcher)
/*     */   {
/*  41 */     Preconditions.checkNotNull(componentMatcher, "componentMatcher cannot be NULL.");
/*  42 */     return new AbstractFuzzyMatcher()
/*     */     {
/*     */       public boolean isMatch(Class<?> value, Object parent) {
/*  45 */         return (value.isArray()) && (this.val$componentMatcher.isMatch(value.getComponentType(), parent));
/*     */       }
/*     */ 
/*     */       protected int calculateRoundNumber()
/*     */       {
/*  51 */         return -1;
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   public static AbstractFuzzyMatcher<Class<?>> matchAll()
/*     */   {
/*  61 */     return MATCH_ALL;
/*     */   }
/*     */ 
/*     */   public static AbstractFuzzyMatcher<Class<?>> matchExact(Class<?> matcher)
/*     */   {
/*  70 */     return new ClassExactMatcher(matcher, ClassExactMatcher.Options.MATCH_EXACT);
/*     */   }
/*     */ 
/*     */   public static AbstractFuzzyMatcher<Class<?>> matchAnyOf(Class<?>[] classes)
/*     */   {
/*  79 */     return matchAnyOf(Sets.newHashSet(classes));
/*     */   }
/*     */ 
/*     */   public static AbstractFuzzyMatcher<Class<?>> matchAnyOf(Set<Class<?>> classes)
/*     */   {
/*  88 */     return new ClassSetMatcher(classes);
/*     */   }
/*     */ 
/*     */   public static AbstractFuzzyMatcher<Class<?>> matchSuper(Class<?> matcher)
/*     */   {
/*  97 */     return new ClassExactMatcher(matcher, ClassExactMatcher.Options.MATCH_SUPER);
/*     */   }
/*     */ 
/*     */   public static AbstractFuzzyMatcher<Class<?>> matchDerived(Class<?> matcher)
/*     */   {
/* 106 */     return new ClassExactMatcher(matcher, ClassExactMatcher.Options.MATCH_DERIVED);
/*     */   }
/*     */ 
/*     */   public static AbstractFuzzyMatcher<Class<?>> matchRegex(Pattern regex, int priority)
/*     */   {
/* 116 */     return new ClassRegexMatcher(regex, priority);
/*     */   }
/*     */ 
/*     */   public static AbstractFuzzyMatcher<Class<?>> matchRegex(String regex, int priority)
/*     */   {
/* 126 */     return matchRegex(Pattern.compile(regex), priority);
/*     */   }
/*     */ 
/*     */   public static AbstractFuzzyMatcher<Class<?>> matchParent()
/*     */   {
/* 134 */     return new AbstractFuzzyMatcher()
/*     */     {
/*     */       public boolean isMatch(Class<?> value, Object parent) {
/* 137 */         if ((parent instanceof Member))
/* 138 */           return ((Member)parent).getDeclaringClass().equals(value);
/* 139 */         if ((parent instanceof Class)) {
/* 140 */           return parent.equals(value);
/*     */         }
/*     */ 
/* 143 */         return false;
/*     */       }
/*     */ 
/*     */       protected int calculateRoundNumber()
/*     */       {
/* 150 */         return -100;
/*     */       }
/*     */ 
/*     */       public String toString()
/*     */       {
/* 155 */         return "match parent class";
/*     */       }
/*     */ 
/*     */       public int hashCode()
/*     */       {
/* 160 */         return 0;
/*     */       }
/*     */ 
/*     */       public boolean equals(Object obj)
/*     */       {
/* 166 */         return (obj != null) && (obj.getClass() == getClass());
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   static boolean checkPattern(Pattern a, Pattern b)
/*     */   {
/* 180 */     if (a == null)
/* 181 */       return b == null;
/* 182 */     if (b == null) {
/* 183 */       return false;
/*     */     }
/* 185 */     return a.pattern().equals(b.pattern());
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.reflect.fuzzy.FuzzyMatchers
 * JD-Core Version:    0.6.2
 */