/*     */ package com.comphenix.protocol.reflect.fuzzy;
/*     */ 
/*     */ import com.google.common.base.Objects;
/*     */ 
/*     */ class ClassExactMatcher extends AbstractFuzzyMatcher<Class<?>>
/*     */ {
/*  34 */   public static final ClassExactMatcher MATCH_ALL = new ClassExactMatcher(null, Options.MATCH_SUPER);
/*     */   private final Class<?> matcher;
/*     */   private final Options option;
/*     */ 
/*     */   ClassExactMatcher(Class<?> matcher, Options option)
/*     */   {
/*  45 */     this.matcher = matcher;
/*  46 */     this.option = option;
/*     */   }
/*     */ 
/*     */   public boolean isMatch(Class<?> input, Object parent)
/*     */   {
/*  59 */     if (input == null) {
/*  60 */       throw new IllegalArgumentException("Input class cannot be NULL.");
/*     */     }
/*     */ 
/*  63 */     if (this.matcher == null)
/*  64 */       return this.option != Options.MATCH_EXACT;
/*  65 */     if (this.option == Options.MATCH_SUPER)
/*  66 */       return input.isAssignableFrom(this.matcher);
/*  67 */     if (this.option == Options.MATCH_DERIVED) {
/*  68 */       return this.matcher.isAssignableFrom(input);
/*     */     }
/*  70 */     return input.equals(this.matcher);
/*     */   }
/*     */ 
/*     */   protected int calculateRoundNumber()
/*     */   {
/*  75 */     return -getClassNumber(this.matcher);
/*     */   }
/*     */ 
/*     */   public static int getClassNumber(Class<?> clazz)
/*     */   {
/*  86 */     int count = 0;
/*     */ 
/*  89 */     while (clazz != null) {
/*  90 */       count++;
/*  91 */       clazz = clazz.getSuperclass();
/*     */     }
/*  93 */     return count;
/*     */   }
/*     */ 
/*     */   public Class<?> getMatcher()
/*     */   {
/* 101 */     return this.matcher;
/*     */   }
/*     */ 
/*     */   public Options getOptions()
/*     */   {
/* 109 */     return this.option;
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 114 */     if (this.option == Options.MATCH_SUPER)
/* 115 */       return this.matcher + " instanceof input";
/* 116 */     if (this.option == Options.MATCH_DERIVED) {
/* 117 */       return "input instanceof " + this.matcher;
/*     */     }
/* 119 */     return "Exact " + this.matcher;
/*     */   }
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 124 */     return Objects.hashCode(new Object[] { this.matcher, this.option });
/*     */   }
/*     */ 
/*     */   public boolean equals(Object obj)
/*     */   {
/* 129 */     if (this == obj)
/* 130 */       return true;
/* 131 */     if ((obj instanceof ClassExactMatcher)) {
/* 132 */       ClassExactMatcher other = (ClassExactMatcher)obj;
/*     */ 
/* 134 */       return (Objects.equal(this.matcher, other.matcher)) && (Objects.equal(this.option, other.option));
/*     */     }
/*     */ 
/* 137 */     return false;
/*     */   }
/*     */ 
/*     */   static enum Options
/*     */   {
/*  18 */     MATCH_EXACT, 
/*     */ 
/*  23 */     MATCH_SUPER, 
/*     */ 
/*  28 */     MATCH_DERIVED;
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.reflect.fuzzy.ClassExactMatcher
 * JD-Core Version:    0.6.2
 */