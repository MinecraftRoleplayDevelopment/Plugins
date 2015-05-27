/*    */ package com.comphenix.protocol.reflect.instances;
/*    */ 
/*    */ import com.comphenix.protocol.reflect.fuzzy.AbstractFuzzyMatcher;
/*    */ import com.comphenix.protocol.reflect.fuzzy.FuzzyMatchers;
/*    */ import javax.annotation.Nullable;
/*    */ 
/*    */ public class BannedGenerator
/*    */   implements InstanceProvider
/*    */ {
/*    */   private AbstractFuzzyMatcher<Class<?>> classMatcher;
/*    */ 
/*    */   public BannedGenerator(AbstractFuzzyMatcher<Class<?>> classMatcher)
/*    */   {
/* 21 */     this.classMatcher = classMatcher;
/*    */   }
/*    */ 
/*    */   public BannedGenerator(Class<?>[] classes) {
/* 25 */     this.classMatcher = FuzzyMatchers.matchAnyOf(classes);
/*    */   }
/*    */ 
/*    */   public Object create(@Nullable Class<?> type)
/*    */   {
/* 31 */     if (this.classMatcher.isMatch(type, null)) {
/* 32 */       throw new NotConstructableException();
/*    */     }
/* 34 */     return null;
/*    */   }
/*    */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.reflect.instances.BannedGenerator
 * JD-Core Version:    0.6.2
 */