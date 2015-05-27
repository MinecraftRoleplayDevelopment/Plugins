/*     */ package com.comphenix.protocol.utility;
/*     */ 
/*     */ import java.util.Collections;
/*     */ import java.util.Map;
/*     */ 
/*     */ public abstract class ClassSource
/*     */ {
/*     */   public static ClassSource fromClassLoader()
/*     */   {
/*  16 */     return fromClassLoader(ClassSource.class.getClassLoader());
/*     */   }
/*     */ 
/*     */   public static ClassSource fromPackage(String packageName)
/*     */   {
/*  25 */     return fromClassLoader().usingPackage(packageName);
/*     */   }
/*     */ 
/*     */   public static ClassSource fromClassLoader(ClassLoader loader)
/*     */   {
/*  34 */     return new ClassSource()
/*     */     {
/*     */       public Class<?> loadClass(String canonicalName) throws ClassNotFoundException {
/*  37 */         return this.val$loader.loadClass(canonicalName);
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   public static ClassSource fromMap(Map<String, Class<?>> map)
/*     */   {
/*  49 */     return new ClassSource()
/*     */     {
/*     */       public Class<?> loadClass(String canonicalName) throws ClassNotFoundException {
/*  52 */         Class loaded = this.val$map == null ? null : (Class)this.val$map.get(canonicalName);
/*  53 */         if (loaded == null)
/*     */         {
/*  55 */           throw new ClassNotFoundException("The specified class could not be found by this ClassLoader.");
/*     */         }
/*  57 */         return loaded;
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   public static ClassSource empty()
/*     */   {
/*  66 */     return fromMap(Collections.emptyMap());
/*     */   }
/*     */ 
/*     */   public static ClassSource attemptLoadFrom(ClassSource[] sources)
/*     */   {
/*  76 */     if (sources.length == 0) {
/*  77 */       return empty();
/*     */     }
/*     */ 
/*  80 */     ClassSource source = null;
/*  81 */     for (int i = 0; i < sources.length; i++) {
/*  82 */       if (sources[i] == null) {
/*  83 */         throw new IllegalArgumentException("Null values are not permitted as ClassSources.");
/*     */       }
/*     */ 
/*  86 */       source = source == null ? sources[i] : source.retry(sources[i]);
/*     */     }
/*  88 */     return source;
/*     */   }
/*     */ 
/*     */   public ClassSource retry(final ClassSource other)
/*     */   {
/*  97 */     return new ClassSource()
/*     */     {
/*     */       public Class<?> loadClass(String canonicalName) throws ClassNotFoundException {
/*     */         try {
/* 101 */           return ClassSource.this.loadClass(canonicalName); } catch (ClassNotFoundException e) {
/*     */         }
/* 103 */         return other.loadClass(canonicalName);
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   public ClassSource usingPackage(final String packageName)
/*     */   {
/* 115 */     return new ClassSource()
/*     */     {
/*     */       public Class<?> loadClass(String canonicalName) throws ClassNotFoundException {
/* 118 */         return ClassSource.this.loadClass(append(packageName, canonicalName));
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   protected static String append(String a, String b)
/*     */   {
/* 130 */     boolean left = a.endsWith(".");
/* 131 */     boolean right = b.endsWith(".");
/*     */ 
/* 134 */     if ((left) && (right))
/* 135 */       return a.substring(0, a.length() - 1) + b;
/* 136 */     if (left != right) {
/* 137 */       return a + b;
/*     */     }
/* 139 */     return a + "." + b;
/*     */   }
/*     */ 
/*     */   public abstract Class<?> loadClass(String paramString)
/*     */     throws ClassNotFoundException;
/*     */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.utility.ClassSource
 * JD-Core Version:    0.6.2
 */