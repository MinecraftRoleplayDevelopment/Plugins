/*    */ package com.comphenix.net.sf.cglib.core;
/*    */ 
/*    */ public class DefaultNamingPolicy
/*    */   implements NamingPolicy
/*    */ {
/* 31 */   public static final DefaultNamingPolicy INSTANCE = new DefaultNamingPolicy();
/*    */ 
/*    */   public String getClassName(String prefix, String source, Object key, Predicate names) {
/* 34 */     if (prefix == null)
/* 35 */       prefix = "com.comphenix.net.sf.cglib.empty.Object";
/* 36 */     else if (prefix.startsWith("java")) {
/* 37 */       prefix = "$" + prefix;
/*    */     }
/* 39 */     String base = prefix + "$$" + source.substring(source.lastIndexOf('.') + 1) + getTag() + "$$" + Integer.toHexString(key.hashCode());
/*    */ 
/* 44 */     String attempt = base;
/* 45 */     int index = 2;
/* 46 */     while (names.evaluate(attempt))
/* 47 */       attempt = base + "_" + index++;
/* 48 */     return attempt;
/*    */   }
/*    */ 
/*    */   protected String getTag()
/*    */   {
/* 56 */     return "ByCGLIB";
/*    */   }
/*    */ 
/*    */   public int hashCode() {
/* 60 */     return getTag().hashCode();
/*    */   }
/*    */ 
/*    */   public boolean equals(Object o) {
/* 64 */     return ((o instanceof DefaultNamingPolicy)) && (((DefaultNamingPolicy)o).getTag().equals(getTag()));
/*    */   }
/*    */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.net.sf.cglib.core.DefaultNamingPolicy
 * JD-Core Version:    0.6.2
 */