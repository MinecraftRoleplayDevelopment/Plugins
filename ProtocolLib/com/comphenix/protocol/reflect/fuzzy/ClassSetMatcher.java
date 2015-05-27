/*    */ package com.comphenix.protocol.reflect.fuzzy;
/*    */ 
/*    */ import com.google.common.base.Objects;
/*    */ import java.util.Set;
/*    */ 
/*    */ class ClassSetMatcher extends AbstractFuzzyMatcher<Class<?>>
/*    */ {
/*    */   private final Set<Class<?>> classes;
/*    */ 
/*    */   public ClassSetMatcher(Set<Class<?>> classes)
/*    */   {
/* 16 */     if (classes == null)
/* 17 */       throw new IllegalArgumentException("Set of classes cannot be NULL.");
/* 18 */     this.classes = classes;
/*    */   }
/*    */ 
/*    */   public boolean isMatch(Class<?> value, Object parent)
/*    */   {
/* 23 */     return this.classes.contains(value);
/*    */   }
/*    */ 
/*    */   protected int calculateRoundNumber()
/*    */   {
/* 28 */     int roundNumber = 0;
/*    */ 
/* 31 */     for (Class clazz : this.classes) {
/* 32 */       roundNumber = combineRounds(roundNumber, -ClassExactMatcher.getClassNumber(clazz));
/*    */     }
/* 34 */     return roundNumber;
/*    */   }
/*    */ 
/*    */   public String toString()
/*    */   {
/* 39 */     return "match any: " + this.classes;
/*    */   }
/*    */ 
/*    */   public int hashCode()
/*    */   {
/* 44 */     return this.classes.hashCode();
/*    */   }
/*    */ 
/*    */   public boolean equals(Object obj)
/*    */   {
/* 49 */     if (this == obj)
/* 50 */       return true;
/* 51 */     if ((obj instanceof ClassSetMatcher))
/*    */     {
/* 53 */       return Objects.equal(this.classes, ((ClassSetMatcher)obj).classes);
/*    */     }
/* 55 */     return true;
/*    */   }
/*    */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.reflect.fuzzy.ClassSetMatcher
 * JD-Core Version:    0.6.2
 */