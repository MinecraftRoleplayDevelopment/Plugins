/*     */ package com.comphenix.protocol.reflect.cloning;
/*     */ 
/*     */ import com.comphenix.protocol.utility.MinecraftReflection;
/*     */ import com.google.common.primitives.Primitives;
/*     */ import java.math.BigDecimal;
/*     */ import java.math.BigInteger;
/*     */ import java.net.Inet4Address;
/*     */ import java.net.Inet6Address;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.net.URI;
/*     */ import java.net.URL;
/*     */ import java.security.PublicKey;
/*     */ import java.util.Locale;
/*     */ import java.util.UUID;
/*     */ import javax.crypto.SecretKey;
/*     */ 
/*     */ public class ImmutableDetector
/*     */   implements Cloner
/*     */ {
/*  45 */   private static final Class<?>[] immutableClasses = { StackTraceElement.class, BigDecimal.class, BigInteger.class, Locale.class, UUID.class, URL.class, URI.class, Inet4Address.class, Inet6Address.class, InetSocketAddress.class, SecretKey.class, PublicKey.class };
/*     */ 
/*     */   public boolean canClone(Object source)
/*     */   {
/*  56 */     if (source == null) {
/*  57 */       return false;
/*     */     }
/*  59 */     return isImmutable(source.getClass());
/*     */   }
/*     */ 
/*     */   public static boolean isImmutable(Class<?> type)
/*     */   {
/*  69 */     if (type.isArray()) {
/*  70 */       return false;
/*     */     }
/*     */ 
/*  73 */     if ((Primitives.isWrapperType(type)) || (String.class.equals(type))) {
/*  74 */       return true;
/*     */     }
/*  76 */     if (isEnumWorkaround(type)) {
/*  77 */       return true;
/*     */     }
/*  79 */     for (Class clazz : immutableClasses) {
/*  80 */       if (clazz.equals(type)) {
/*  81 */         return true;
/*     */       }
/*     */     }
/*  84 */     if ((MinecraftReflection.isUsingNetty()) && 
/*  85 */       (type.equals(MinecraftReflection.getGameProfileClass()))) {
/*  86 */       return true;
/*     */     }
/*     */ 
/*  90 */     return false;
/*     */   }
/*     */ 
/*     */   private static boolean isEnumWorkaround(Class<?> enumClass)
/*     */   {
/*  95 */     while (enumClass != null) {
/*  96 */       if (enumClass.isEnum())
/*  97 */         return true;
/*  98 */       enumClass = enumClass.getSuperclass();
/*     */     }
/* 100 */     return false;
/*     */   }
/*     */ 
/*     */   public Object clone(Object source)
/*     */   {
/* 106 */     return source;
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.reflect.cloning.ImmutableDetector
 * JD-Core Version:    0.6.2
 */