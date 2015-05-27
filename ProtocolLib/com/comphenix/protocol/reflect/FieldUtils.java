/*     */ package com.comphenix.protocol.reflect;
/*     */ 
/*     */ import java.lang.reflect.AccessibleObject;
/*     */ import java.lang.reflect.Field;
/*     */ import java.lang.reflect.Member;
/*     */ import java.lang.reflect.Modifier;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ 
/*     */ public class FieldUtils
/*     */ {
/*     */   public static Field getField(Class cls, String fieldName)
/*     */   {
/*  64 */     Field field = getField(cls, fieldName, false);
/*  65 */     MemberUtils.setAccessibleWorkaround(field);
/*  66 */     return field;
/*     */   }
/*     */ 
/*     */   public static Field getField(Class cls, String fieldName, boolean forceAccess)
/*     */   {
/*  82 */     if (cls == null) {
/*  83 */       throw new IllegalArgumentException("The class must not be null");
/*     */     }
/*  85 */     if (fieldName == null) {
/*  86 */       throw new IllegalArgumentException("The field name must not be null");
/*     */     }
/*     */ 
/* 104 */     for (Class acls = cls; acls != null; acls = acls.getSuperclass()) {
/*     */       try {
/* 106 */         Field field = acls.getDeclaredField(fieldName);
/*     */ 
/* 109 */         if (!Modifier.isPublic(field.getModifiers())) {
/* 110 */           if (forceAccess)
/* 111 */             field.setAccessible(true);
/*     */           else {
/* 113 */             continue;
/*     */           }
/*     */         }
/* 116 */         return field;
/*     */       }
/*     */       catch (NoSuchFieldException ex)
/*     */       {
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 125 */     Field match = null;
/* 126 */     for (Iterator intf = getAllInterfaces(cls).iterator(); intf.hasNext(); )
/*     */       try {
/* 128 */         Field test = ((Class)intf.next()).getField(fieldName);
/* 129 */         if (match != null) {
/* 130 */           throw new IllegalArgumentException("Reference to field " + fieldName + " is ambiguous relative to " + cls + "; a matching field exists on two or more implemented interfaces.");
/*     */         }
/*     */ 
/* 134 */         match = test;
/*     */       }
/*     */       catch (NoSuchFieldException ex)
/*     */       {
/*     */       }
/* 139 */     return match;
/*     */   }
/*     */ 
/*     */   private static List getAllInterfaces(Class cls)
/*     */   {
/* 156 */     if (cls == null) {
/* 157 */       return null;
/*     */     }
/* 159 */     List list = new ArrayList();
/*     */ 
/* 161 */     while (cls != null) {
/* 162 */       Class[] interfaces = cls.getInterfaces();
/*     */       Iterator it;
/* 163 */       for (int i = 0; i < interfaces.length; i++) {
/* 164 */         if (!list.contains(interfaces[i])) {
/* 165 */           list.add(interfaces[i]);
/*     */         }
/* 167 */         List superInterfaces = getAllInterfaces(interfaces[i]);
/* 168 */         for (it = superInterfaces.iterator(); it.hasNext(); ) {
/* 169 */           Class intface = (Class)it.next();
/* 170 */           if (!list.contains(intface)) {
/* 171 */             list.add(intface);
/*     */           }
/*     */         }
/*     */       }
/* 175 */       cls = cls.getSuperclass();
/*     */     }
/* 177 */     return list;
/*     */   }
/*     */ 
/*     */   public static Object readStaticField(Field field)
/*     */     throws IllegalAccessException
/*     */   {
/* 189 */     return readStaticField(field, false);
/*     */   }
/*     */ 
/*     */   public static Object readStaticField(Field field, boolean forceAccess)
/*     */     throws IllegalAccessException
/*     */   {
/* 204 */     if (field == null) {
/* 205 */       throw new IllegalArgumentException("The field must not be null");
/*     */     }
/* 207 */     if (!Modifier.isStatic(field.getModifiers())) {
/* 208 */       throw new IllegalArgumentException("The field '" + field.getName() + "' is not static");
/*     */     }
/* 210 */     return readField(field, (Object)null, forceAccess);
/*     */   }
/*     */ 
/*     */   public static Object readStaticField(Class cls, String fieldName)
/*     */     throws IllegalAccessException
/*     */   {
/* 223 */     return readStaticField(cls, fieldName, false);
/*     */   }
/*     */ 
/*     */   public static Object readStaticField(Class cls, String fieldName, boolean forceAccess)
/*     */     throws IllegalAccessException
/*     */   {
/* 240 */     Field field = getField(cls, fieldName, forceAccess);
/* 241 */     if (field == null) {
/* 242 */       throw new IllegalArgumentException("Cannot locate field " + fieldName + " on " + cls);
/*     */     }
/*     */ 
/* 245 */     return readStaticField(field, false);
/*     */   }
/*     */ 
/*     */   public static Object readField(Field field, Object target)
/*     */     throws IllegalAccessException
/*     */   {
/* 258 */     return readField(field, target, false);
/*     */   }
/*     */ 
/*     */   public static Object readField(Field field, Object target, boolean forceAccess)
/*     */     throws IllegalAccessException
/*     */   {
/* 273 */     if (field == null) {
/* 274 */       throw new IllegalArgumentException("The field must not be null");
/*     */     }
/* 276 */     if ((forceAccess) && (!field.isAccessible()))
/* 277 */       field.setAccessible(true);
/*     */     else {
/* 279 */       MemberUtils.setAccessibleWorkaround(field);
/*     */     }
/* 281 */     return field.get(target);
/*     */   }
/*     */ 
/*     */   public static Object readField(Object target, String fieldName)
/*     */     throws IllegalAccessException
/*     */   {
/* 294 */     return readField(target, fieldName, false);
/*     */   }
/*     */ 
/*     */   public static Object readField(Object target, String fieldName, boolean forceAccess)
/*     */     throws IllegalAccessException
/*     */   {
/* 311 */     if (target == null) {
/* 312 */       throw new IllegalArgumentException("target object must not be null");
/*     */     }
/* 314 */     Class cls = target.getClass();
/* 315 */     Field field = getField(cls, fieldName, forceAccess);
/* 316 */     if (field == null) {
/* 317 */       throw new IllegalArgumentException("Cannot locate field " + fieldName + " on " + cls);
/*     */     }
/*     */ 
/* 320 */     return readField(field, target);
/*     */   }
/*     */ 
/*     */   public static void writeStaticField(Field field, Object value)
/*     */     throws IllegalAccessException
/*     */   {
/* 332 */     writeStaticField(field, value, false);
/*     */   }
/*     */ 
/*     */   public static void writeStaticField(Field field, Object value, boolean forceAccess)
/*     */     throws IllegalAccessException
/*     */   {
/* 349 */     if (field == null) {
/* 350 */       throw new IllegalArgumentException("The field must not be null");
/*     */     }
/* 352 */     if (!Modifier.isStatic(field.getModifiers())) {
/* 353 */       throw new IllegalArgumentException("The field '" + field.getName() + "' is not static");
/*     */     }
/* 355 */     writeField(field, (Object)null, value, forceAccess);
/*     */   }
/*     */ 
/*     */   public static void writeStaticField(Class cls, String fieldName, Object value)
/*     */     throws IllegalAccessException
/*     */   {
/* 370 */     writeStaticField(cls, fieldName, value, false);
/*     */   }
/*     */ 
/*     */   public static void writeStaticField(Class cls, String fieldName, Object value, boolean forceAccess)
/*     */     throws IllegalAccessException
/*     */   {
/* 389 */     Field field = getField(cls, fieldName, forceAccess);
/* 390 */     if (field == null) {
/* 391 */       throw new IllegalArgumentException("Cannot locate field " + fieldName + " on " + cls);
/*     */     }
/*     */ 
/* 394 */     writeStaticField(field, value);
/*     */   }
/*     */ 
/*     */   public static void writeStaticFinalField(Class<?> clazz, String fieldName, Object value, boolean forceAccess) throws Exception {
/* 398 */     Field field = getField(clazz, fieldName, forceAccess);
/* 399 */     if (field == null) {
/* 400 */       throw new IllegalArgumentException("Cannot locate field " + fieldName + " in " + clazz);
/*     */     }
/*     */ 
/* 403 */     field.setAccessible(true);
/*     */ 
/* 405 */     Field modifiersField = Field.class.getDeclaredField("modifiers");
/* 406 */     modifiersField.setAccessible(true);
/* 407 */     modifiersField.setInt(field, field.getModifiers() & 0xFFFFFFEF);
/*     */ 
/* 409 */     field.setAccessible(true);
/* 410 */     field.set(null, value);
/*     */   }
/*     */ 
/*     */   public static void writeField(Field field, Object target, Object value)
/*     */     throws IllegalAccessException
/*     */   {
/* 424 */     writeField(field, target, value, false);
/*     */   }
/*     */ 
/*     */   public static void writeField(Field field, Object target, Object value, boolean forceAccess)
/*     */     throws IllegalAccessException
/*     */   {
/* 442 */     if (field == null) {
/* 443 */       throw new IllegalArgumentException("The field must not be null");
/*     */     }
/* 445 */     if ((forceAccess) && (!field.isAccessible()))
/* 446 */       field.setAccessible(true);
/*     */     else {
/* 448 */       MemberUtils.setAccessibleWorkaround(field);
/*     */     }
/* 450 */     field.set(target, value);
/*     */   }
/*     */ 
/*     */   public static void writeField(Object target, String fieldName, Object value)
/*     */     throws IllegalAccessException
/*     */   {
/* 465 */     writeField(target, fieldName, value, false);
/*     */   }
/*     */ 
/*     */   public static void writeField(Object target, String fieldName, Object value, boolean forceAccess)
/*     */     throws IllegalAccessException
/*     */   {
/* 483 */     if (target == null) {
/* 484 */       throw new IllegalArgumentException("target object must not be null");
/*     */     }
/* 486 */     Class cls = target.getClass();
/* 487 */     Field field = getField(cls, fieldName, forceAccess);
/* 488 */     if (field == null) {
/* 489 */       throw new IllegalArgumentException("Cannot locate declared field " + cls.getName() + "." + fieldName);
/*     */     }
/*     */ 
/* 493 */     writeField(field, target, value);
/*     */   }
/*     */ 
/*     */   private static class MemberUtils
/*     */   {
/*     */     private static final int ACCESS_TEST = 7;
/*     */ 
/*     */     public static void setAccessibleWorkaround(AccessibleObject o)
/*     */     {
/* 503 */       if ((o == null) || (o.isAccessible())) {
/* 504 */         return;
/*     */       }
/* 506 */       Member m = (Member)o;
/* 507 */       if ((Modifier.isPublic(m.getModifiers())) && (isPackageAccess(m.getDeclaringClass().getModifiers())))
/*     */         try
/*     */         {
/* 510 */           o.setAccessible(true);
/*     */         }
/*     */         catch (SecurityException e)
/*     */         {
/*     */         }
/*     */     }
/*     */ 
/*     */     public static boolean isPackageAccess(int modifiers)
/*     */     {
/* 524 */       return (modifiers & 0x7) == 0;
/*     */     }
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.reflect.FieldUtils
 * JD-Core Version:    0.6.2
 */