/*     */ package com.comphenix.protocol.injector;
/*     */ 
/*     */ import com.comphenix.protocol.ProtocolLibrary;
/*     */ import com.comphenix.protocol.error.ErrorReporter;
/*     */ import com.comphenix.protocol.error.Report;
/*     */ import com.comphenix.protocol.error.Report.ReportBuilder;
/*     */ import com.comphenix.protocol.error.ReportType;
/*     */ import com.comphenix.protocol.reflect.FieldUtils;
/*     */ import com.comphenix.protocol.reflect.instances.DefaultInstances;
/*     */ import com.google.common.primitives.Primitives;
/*     */ import java.lang.reflect.Field;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.lang.reflect.Method;
/*     */ import java.util.Collection;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
/*     */ import java.util.concurrent.ConcurrentHashMap;
/*     */ 
/*     */ public class BukkitUnwrapper
/*     */   implements PacketConstructor.Unwrapper
/*     */ {
/*     */   private static BukkitUnwrapper DEFAULT;
/*  50 */   public static final ReportType REPORT_ILLEGAL_ARGUMENT = new ReportType("Illegal argument.");
/*  51 */   public static final ReportType REPORT_SECURITY_LIMITATION = new ReportType("Security limitation.");
/*  52 */   public static final ReportType REPORT_CANNOT_FIND_UNWRAP_METHOD = new ReportType("Cannot find method.");
/*     */ 
/*  54 */   public static final ReportType REPORT_CANNOT_READ_FIELD_HANDLE = new ReportType("Cannot read field 'handle'.");
/*     */ 
/*  56 */   private static Map<Class<?>, PacketConstructor.Unwrapper> unwrapperCache = new ConcurrentHashMap();
/*     */   private final ErrorReporter reporter;
/*     */ 
/*     */   public static BukkitUnwrapper getInstance()
/*     */   {
/*  66 */     ErrorReporter currentReporter = ProtocolLibrary.getErrorReporter();
/*     */ 
/*  69 */     if ((DEFAULT == null) || (DEFAULT.reporter != currentReporter)) {
/*  70 */       DEFAULT = new BukkitUnwrapper(currentReporter);
/*     */     }
/*  72 */     return DEFAULT;
/*     */   }
/*     */ 
/*     */   public BukkitUnwrapper()
/*     */   {
/*  79 */     this(ProtocolLibrary.getErrorReporter());
/*     */   }
/*     */ 
/*     */   public BukkitUnwrapper(ErrorReporter reporter)
/*     */   {
/*  87 */     this.reporter = reporter;
/*     */   }
/*     */ 
/*     */   public Object unwrapItem(Object wrappedObject)
/*     */   {
/*  94 */     if (wrappedObject == null)
/*  95 */       return null;
/*  96 */     Class currentClass = PacketConstructor.getClass(wrappedObject);
/*     */ 
/*  99 */     if ((currentClass.isPrimitive()) || (currentClass.equals(String.class))) {
/* 100 */       return null;
/*     */     }
/*     */ 
/* 103 */     if ((wrappedObject instanceof Collection))
/* 104 */       return handleCollection((Collection)wrappedObject);
/* 105 */     if ((Primitives.isWrapperType(currentClass)) || ((wrappedObject instanceof String))) {
/* 106 */       return null;
/*     */     }
/*     */ 
/* 109 */     PacketConstructor.Unwrapper specificUnwrapper = getSpecificUnwrapper(currentClass);
/*     */ 
/* 112 */     if (specificUnwrapper != null) {
/* 113 */       return specificUnwrapper.unwrapItem(wrappedObject);
/*     */     }
/* 115 */     return null;
/*     */   }
/*     */ 
/*     */   private Object handleCollection(Collection<Object> wrappedObject)
/*     */   {
/* 122 */     Collection copy = (Collection)DefaultInstances.DEFAULT.getDefault(wrappedObject.getClass());
/*     */ 
/* 124 */     if (copy != null)
/*     */     {
/* 126 */       for (Iterator i$ = wrappedObject.iterator(); i$.hasNext(); ) { Object element = i$.next();
/* 127 */         copy.add(unwrapItem(element));
/*     */       }
/* 129 */       return copy;
/*     */     }
/*     */ 
/* 133 */     return null;
/*     */   }
/*     */ 
/*     */   private PacketConstructor.Unwrapper getSpecificUnwrapper(final Class<?> type)
/*     */   {
/* 144 */     if (unwrapperCache.containsKey(type))
/*     */     {
/* 146 */       return (PacketConstructor.Unwrapper)unwrapperCache.get(type);
/*     */     }
/*     */     try
/*     */     {
/* 150 */       final Method find = type.getMethod("getHandle", new Class[0]);
/*     */ 
/* 153 */       PacketConstructor.Unwrapper methodUnwrapper = new PacketConstructor.Unwrapper()
/*     */       {
/*     */         public Object unwrapItem(Object wrappedObject) {
/*     */           try {
/* 157 */             if ((wrappedObject instanceof Class))
/* 158 */               return BukkitUnwrapper.checkClass((Class)wrappedObject, type, find.getReturnType());
/* 159 */             return find.invoke(wrappedObject, new Object[0]);
/*     */           }
/*     */           catch (IllegalArgumentException e) {
/* 162 */             BukkitUnwrapper.this.reporter.reportDetailed(this, Report.newBuilder(BukkitUnwrapper.REPORT_ILLEGAL_ARGUMENT).error(e).callerParam(new Object[] { wrappedObject, find }));
/*     */           }
/*     */           catch (IllegalAccessException e)
/*     */           {
/* 167 */             return null;
/*     */           }
/*     */           catch (InvocationTargetException e) {
/* 170 */             throw new RuntimeException("Minecraft error.", e);
/*     */           }
/*     */ 
/* 173 */           return null;
/*     */         }
/*     */       };
/* 177 */       unwrapperCache.put(type, methodUnwrapper);
/* 178 */       return methodUnwrapper;
/*     */     }
/*     */     catch (SecurityException e) {
/* 181 */       this.reporter.reportDetailed(this, Report.newBuilder(REPORT_SECURITY_LIMITATION).error(e).callerParam(new Object[] { type }));
/*     */     }
/*     */     catch (NoSuchMethodException e)
/*     */     {
/* 186 */       PacketConstructor.Unwrapper fieldUnwrapper = getFieldUnwrapper(type);
/*     */ 
/* 188 */       if (fieldUnwrapper != null) {
/* 189 */         return fieldUnwrapper;
/*     */       }
/* 191 */       this.reporter.reportDetailed(this, Report.newBuilder(REPORT_CANNOT_FIND_UNWRAP_METHOD).error(e).callerParam(new Object[] { type }));
/*     */     }
/*     */ 
/* 196 */     return null;
/*     */   }
/*     */ 
/*     */   private PacketConstructor.Unwrapper getFieldUnwrapper(final Class<?> type)
/*     */   {
/* 205 */     final Field find = FieldUtils.getField(type, "handle", true);
/*     */ 
/* 208 */     if (find != null) {
/* 209 */       PacketConstructor.Unwrapper fieldUnwrapper = new PacketConstructor.Unwrapper()
/*     */       {
/*     */         public Object unwrapItem(Object wrappedObject) {
/*     */           try {
/* 213 */             if ((wrappedObject instanceof Class))
/* 214 */               return BukkitUnwrapper.checkClass((Class)wrappedObject, type, find.getType());
/* 215 */             return FieldUtils.readField(find, wrappedObject, true);
/*     */           } catch (IllegalAccessException e) {
/* 217 */             BukkitUnwrapper.this.reporter.reportDetailed(this, Report.newBuilder(BukkitUnwrapper.REPORT_CANNOT_READ_FIELD_HANDLE).error(e).callerParam(new Object[] { wrappedObject, find }));
/*     */           }
/*     */ 
/* 220 */           return null;
/*     */         }
/*     */       };
/* 225 */       unwrapperCache.put(type, fieldUnwrapper);
/* 226 */       return fieldUnwrapper;
/*     */     }
/*     */ 
/* 230 */     this.reporter.reportDetailed(this, Report.newBuilder(REPORT_CANNOT_READ_FIELD_HANDLE).callerParam(new Object[] { find }));
/*     */ 
/* 233 */     return null;
/*     */   }
/*     */ 
/*     */   private static Class<?> checkClass(Class<?> input, Class<?> expected, Class<?> result)
/*     */   {
/* 238 */     if (expected.isAssignableFrom(input)) {
/* 239 */       return result;
/*     */     }
/* 241 */     return null;
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.injector.BukkitUnwrapper
 * JD-Core Version:    0.6.2
 */