/*     */ package com.comphenix.protocol.error;
/*     */ 
/*     */ import com.comphenix.protocol.reflect.FieldAccessException;
/*     */ import com.comphenix.protocol.reflect.FuzzyReflection;
/*     */ import com.comphenix.protocol.reflect.fuzzy.FuzzyFieldContract;
/*     */ import com.comphenix.protocol.reflect.fuzzy.FuzzyFieldContract.Builder;
/*     */ import java.lang.reflect.Field;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ 
/*     */ public class ReportType
/*     */ {
/*     */   private final String errorFormat;
/*     */   protected String reportName;
/*     */ 
/*     */   public ReportType(String errorFormat)
/*     */   {
/*  29 */     this.errorFormat = errorFormat;
/*     */   }
/*     */ 
/*     */   public String getMessage(Object[] parameters)
/*     */   {
/*  38 */     if ((parameters == null) || (parameters.length == 0)) {
/*  39 */       return toString();
/*     */     }
/*  41 */     return String.format(this.errorFormat, parameters);
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/*  46 */     return this.errorFormat;
/*     */   }
/*     */ 
/*     */   public static Class<?> getSenderClass(Object sender)
/*     */   {
/*  57 */     if (sender == null)
/*  58 */       throw new IllegalArgumentException("sender cannot be NUll.");
/*  59 */     if ((sender instanceof Class)) {
/*  60 */       return (Class)sender;
/*     */     }
/*  62 */     return sender.getClass();
/*     */   }
/*     */ 
/*     */   public static String getReportName(Object sender, ReportType type)
/*     */   {
/*  77 */     if (sender == null)
/*  78 */       throw new IllegalArgumentException("sender cannot be NUll.");
/*  79 */     return getReportName(getSenderClass(sender), type);
/*     */   }
/*     */ 
/*     */   private static String getReportName(Class<?> sender, ReportType type)
/*     */   {
/*  91 */     if (sender == null) {
/*  92 */       throw new IllegalArgumentException("sender cannot be NUll.");
/*     */     }
/*     */ 
/*  95 */     if (type.reportName == null) {
/*  96 */       for (Field field : getReportFields(sender)) {
/*     */         try {
/*  98 */           field.setAccessible(true);
/*     */ 
/* 100 */           if (field.get(null) == type)
/*     */           {
/* 102 */             return type.reportName = field.getDeclaringClass().getCanonicalName() + "#" + field.getName();
/*     */           }
/*     */         } catch (IllegalAccessException e) {
/* 105 */           throw new FieldAccessException("Unable to read field " + field, e);
/*     */         }
/*     */       }
/* 108 */       throw new IllegalArgumentException("Cannot find report name for " + type);
/*     */     }
/* 110 */     return type.reportName;
/*     */   }
/*     */ 
/*     */   public static ReportType[] getReports(Class<?> sender)
/*     */   {
/* 119 */     if (sender == null)
/* 120 */       throw new IllegalArgumentException("sender cannot be NULL.");
/* 121 */     List result = new ArrayList();
/*     */ 
/* 124 */     for (Field field : getReportFields(sender)) {
/*     */       try {
/* 126 */         field.setAccessible(true);
/* 127 */         result.add((ReportType)field.get(null));
/*     */       } catch (IllegalAccessException e) {
/* 129 */         throw new FieldAccessException("Unable to read field " + field, e);
/*     */       }
/*     */     }
/* 132 */     return (ReportType[])result.toArray(new ReportType[0]);
/*     */   }
/*     */ 
/*     */   private static List<Field> getReportFields(Class<?> clazz)
/*     */   {
/* 141 */     return FuzzyReflection.fromClass(clazz).getFieldList(FuzzyFieldContract.newBuilder().requireModifier(8).typeDerivedOf(ReportType.class).build());
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.error.ReportType
 * JD-Core Version:    0.6.2
 */