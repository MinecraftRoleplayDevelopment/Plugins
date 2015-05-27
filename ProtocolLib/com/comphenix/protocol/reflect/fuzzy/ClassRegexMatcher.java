/*    */ package com.comphenix.protocol.reflect.fuzzy;
/*    */ 
/*    */ import com.google.common.base.Objects;
/*    */ import java.util.regex.Matcher;
/*    */ import java.util.regex.Pattern;
/*    */ 
/*    */ class ClassRegexMatcher extends AbstractFuzzyMatcher<Class<?>>
/*    */ {
/*    */   private final Pattern regex;
/*    */   private final int priority;
/*    */ 
/*    */   public ClassRegexMatcher(Pattern regex, int priority)
/*    */   {
/* 17 */     if (regex == null)
/* 18 */       throw new IllegalArgumentException("Regular expression pattern cannot be NULL.");
/* 19 */     this.regex = regex;
/* 20 */     this.priority = priority;
/*    */   }
/*    */ 
/*    */   public boolean isMatch(Class<?> value, Object parent)
/*    */   {
/* 25 */     if (value != null) {
/* 26 */       return this.regex.matcher(value.getCanonicalName()).matches();
/*    */     }
/* 28 */     return false;
/*    */   }
/*    */ 
/*    */   protected int calculateRoundNumber()
/*    */   {
/* 33 */     return -this.priority;
/*    */   }
/*    */ 
/*    */   public String toString()
/*    */   {
/* 38 */     return "class name of " + this.regex.toString();
/*    */   }
/*    */ 
/*    */   public int hashCode()
/*    */   {
/* 43 */     return Objects.hashCode(new Object[] { this.regex, Integer.valueOf(this.priority) });
/*    */   }
/*    */ 
/*    */   public boolean equals(Object obj)
/*    */   {
/* 48 */     if (this == obj)
/* 49 */       return true;
/* 50 */     if ((obj instanceof ClassRegexMatcher)) {
/* 51 */       ClassRegexMatcher other = (ClassRegexMatcher)obj;
/*    */ 
/* 53 */       return (this.priority == other.priority) && (FuzzyMatchers.checkPattern(this.regex, other.regex));
/*    */     }
/*    */ 
/* 56 */     return false;
/*    */   }
/*    */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.reflect.fuzzy.ClassRegexMatcher
 * JD-Core Version:    0.6.2
 */