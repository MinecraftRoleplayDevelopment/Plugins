/*    */ package com.comphenix.protocol.utility;
/*    */ 
/*    */ import com.google.common.base.Preconditions;
/*    */ import com.google.common.base.Strings;
/*    */ import com.google.common.collect.Maps;
/*    */ import java.util.Map;
/*    */ 
/*    */ class CachedPackage
/*    */ {
/*    */   private final Map<String, Class<?>> cache;
/*    */   private final String packageName;
/*    */   private final ClassSource source;
/*    */ 
/*    */   public CachedPackage(String packageName, ClassSource source)
/*    */   {
/* 42 */     this.packageName = packageName;
/* 43 */     this.cache = Maps.newConcurrentMap();
/* 44 */     this.source = source;
/*    */   }
/*    */ 
/*    */   public void setPackageClass(String className, Class<?> clazz)
/*    */   {
/* 53 */     this.cache.put(className, clazz);
/*    */   }
/*    */ 
/*    */   public Class<?> getPackageClass(String className)
/*    */   {
/*    */     try
/*    */     {
/* 64 */       Class result = (Class)this.cache.get(Preconditions.checkNotNull(className, "className cannot be NULL"));
/*    */ 
/* 67 */       if (result == null)
/*    */       {
/* 69 */         result = this.source.loadClass(combine(this.packageName, className));
/* 70 */         if (result == null) {
/* 71 */           throw new IllegalArgumentException("Source " + this.source + " returned NULL for " + className);
/*    */         }
/* 73 */         this.cache.put(className, result);
/*    */       }
/*    */ 
/* 76 */       return result;
/*    */     } catch (ClassNotFoundException e) {
/* 78 */       throw new RuntimeException("Cannot find class " + className, e);
/*    */     }
/*    */   }
/*    */ 
/*    */   public static String combine(String packageName, String className)
/*    */   {
/* 89 */     if (Strings.isNullOrEmpty(packageName))
/* 90 */       return className;
/* 91 */     if (Strings.isNullOrEmpty(className))
/* 92 */       return packageName;
/* 93 */     return packageName + "." + className;
/*    */   }
/*    */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.utility.CachedPackage
 * JD-Core Version:    0.6.2
 */