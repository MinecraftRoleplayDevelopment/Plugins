/*     */ package com.comphenix.protocol.reflect.fuzzy;
/*     */ 
/*     */ import com.google.common.primitives.Ints;
/*     */ 
/*     */ public abstract class AbstractFuzzyMatcher<T>
/*     */   implements Comparable<AbstractFuzzyMatcher<T>>
/*     */ {
/*     */   private Integer roundNumber;
/*     */ 
/*     */   public abstract boolean isMatch(T paramT, Object paramObject);
/*     */ 
/*     */   protected abstract int calculateRoundNumber();
/*     */ 
/*     */   public final int getRoundNumber()
/*     */   {
/*  44 */     if (this.roundNumber == null) {
/*  45 */       return (this.roundNumber = Integer.valueOf(calculateRoundNumber())).intValue();
/*     */     }
/*  47 */     return this.roundNumber.intValue();
/*     */   }
/*     */ 
/*     */   protected final int combineRounds(int roundA, int roundB)
/*     */   {
/*  58 */     if (roundA == 0)
/*  59 */       return roundB;
/*  60 */     if (roundB == 0) {
/*  61 */       return roundA;
/*     */     }
/*  63 */     return Math.max(roundA, roundB);
/*     */   }
/*     */ 
/*     */   protected final int combineRounds(Integer[] rounds)
/*     */   {
/*  72 */     if (rounds.length < 2) {
/*  73 */       throw new IllegalArgumentException("Must supply at least two arguments.");
/*     */     }
/*     */ 
/*  76 */     int reduced = combineRounds(rounds[0].intValue(), rounds[1].intValue());
/*     */ 
/*  79 */     for (int i = 2; i < rounds.length; i++) {
/*  80 */       reduced = combineRounds(reduced, rounds[i].intValue());
/*     */     }
/*  82 */     return reduced;
/*     */   }
/*     */ 
/*     */   public int compareTo(AbstractFuzzyMatcher<T> obj)
/*     */   {
/*  87 */     if ((obj instanceof AbstractFuzzyMatcher)) {
/*  88 */       AbstractFuzzyMatcher matcher = obj;
/*  89 */       return Ints.compare(getRoundNumber(), matcher.getRoundNumber());
/*     */     }
/*     */ 
/*  92 */     return -1;
/*     */   }
/*     */ 
/*     */   public AbstractFuzzyMatcher<T> inverted()
/*     */   {
/* 100 */     return new AbstractFuzzyMatcher()
/*     */     {
/*     */       public boolean isMatch(T value, Object parent) {
/* 103 */         return !AbstractFuzzyMatcher.this.isMatch(value, parent);
/*     */       }
/*     */ 
/*     */       protected int calculateRoundNumber()
/*     */       {
/* 108 */         return -2;
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   public AbstractFuzzyMatcher<T> and(final AbstractFuzzyMatcher<T> other)
/*     */   {
/* 119 */     return new AbstractFuzzyMatcher()
/*     */     {
/*     */       public boolean isMatch(T value, Object parent)
/*     */       {
/* 123 */         return (AbstractFuzzyMatcher.this.isMatch(value, parent)) && (other.isMatch(value, parent));
/*     */       }
/*     */ 
/*     */       protected int calculateRoundNumber()
/*     */       {
/* 129 */         return combineRounds(AbstractFuzzyMatcher.this.getRoundNumber(), other.getRoundNumber());
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   public AbstractFuzzyMatcher<T> or(final AbstractFuzzyMatcher<T> other)
/*     */   {
/* 140 */     return new AbstractFuzzyMatcher()
/*     */     {
/*     */       public boolean isMatch(T value, Object parent)
/*     */       {
/* 144 */         return (AbstractFuzzyMatcher.this.isMatch(value, parent)) || (other.isMatch(value, parent));
/*     */       }
/*     */ 
/*     */       protected int calculateRoundNumber()
/*     */       {
/* 150 */         return combineRounds(AbstractFuzzyMatcher.this.getRoundNumber(), other.getRoundNumber());
/*     */       }
/*     */     };
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.reflect.fuzzy.AbstractFuzzyMatcher
 * JD-Core Version:    0.6.2
 */