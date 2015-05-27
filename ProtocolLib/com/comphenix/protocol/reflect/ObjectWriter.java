/*     */ package com.comphenix.protocol.reflect;
/*     */ 
/*     */ import com.comphenix.protocol.injector.StructureCache;
/*     */ import com.comphenix.protocol.utility.MinecraftReflection;
/*     */ import java.lang.reflect.Field;
/*     */ import java.lang.reflect.Modifier;
/*     */ import java.util.concurrent.ConcurrentHashMap;
/*     */ import java.util.concurrent.ConcurrentMap;
/*     */ 
/*     */ public class ObjectWriter
/*     */ {
/*  36 */   private static ConcurrentMap<Class, StructureModifier<Object>> cache = new ConcurrentHashMap();
/*     */ 
/*     */   private StructureModifier<Object> getModifier(Class<?> type)
/*     */   {
/*  47 */     Class packetClass = MinecraftReflection.getPacketClass();
/*     */ 
/*  50 */     if ((!type.equals(packetClass)) && (packetClass.isAssignableFrom(type)))
/*     */     {
/*  52 */       return StructureCache.getStructure(type);
/*     */     }
/*     */ 
/*  55 */     StructureModifier modifier = (StructureModifier)cache.get(type);
/*     */ 
/*  58 */     if (modifier == null) {
/*  59 */       StructureModifier value = new StructureModifier(type, null, false);
/*  60 */       modifier = (StructureModifier)cache.putIfAbsent(type, value);
/*     */ 
/*  62 */       if (modifier == null) {
/*  63 */         modifier = value;
/*     */       }
/*     */     }
/*     */ 
/*  67 */     return modifier;
/*     */   }
/*     */ 
/*     */   public void copyTo(Object source, Object destination, Class<?> commonType)
/*     */   {
/*  80 */     copyToInternal(source, destination, commonType, true);
/*     */   }
/*     */ 
/*     */   protected void transformField(StructureModifier<Object> modifierSource, StructureModifier<Object> modifierDest, int fieldIndex)
/*     */   {
/*  90 */     Object value = modifierSource.read(fieldIndex);
/*  91 */     modifierDest.write(fieldIndex, value);
/*     */   }
/*     */ 
/*     */   private void copyToInternal(Object source, Object destination, Class<?> commonType, boolean copyPublic)
/*     */   {
/*  96 */     if (source == null)
/*  97 */       throw new IllegalArgumentException("Source cannot be NULL");
/*  98 */     if (destination == null) {
/*  99 */       throw new IllegalArgumentException("Destination cannot be NULL");
/*     */     }
/* 101 */     StructureModifier modifier = getModifier(commonType);
/*     */ 
/* 104 */     StructureModifier modifierSource = modifier.withTarget(source);
/* 105 */     StructureModifier modifierDest = modifier.withTarget(destination);
/*     */     try
/*     */     {
/* 109 */       for (int i = 0; i < modifierSource.size(); i++) {
/* 110 */         Field field = modifierSource.getField(i);
/* 111 */         int mod = field.getModifiers();
/*     */ 
/* 114 */         if ((!Modifier.isStatic(mod)) && ((!Modifier.isPublic(mod)) || (copyPublic))) {
/* 115 */           transformField(modifierSource, modifierDest, i);
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 120 */       Class superclass = commonType.getSuperclass();
/*     */ 
/* 122 */       if ((superclass != null) && (!superclass.equals(Object.class)))
/* 123 */         copyToInternal(source, destination, superclass, false);
/*     */     }
/*     */     catch (FieldAccessException e)
/*     */     {
/* 127 */       throw new RuntimeException("Unable to copy fields from " + commonType.getName(), e);
/*     */     }
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.reflect.ObjectWriter
 * JD-Core Version:    0.6.2
 */