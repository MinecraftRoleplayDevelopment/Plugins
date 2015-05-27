/*    */ package com.comphenix.protocol.reflect.instances;
/*    */ 
/*    */ import com.google.common.base.Defaults;
/*    */ import com.google.common.primitives.Primitives;
/*    */ import java.lang.reflect.Array;
/*    */ import javax.annotation.Nullable;
/*    */ 
/*    */ public class PrimitiveGenerator
/*    */   implements InstanceProvider
/*    */ {
/*    */   public static final String STRING_DEFAULT = "";
/* 41 */   public static PrimitiveGenerator INSTANCE = new PrimitiveGenerator("");
/*    */   private String stringDefault;
/*    */ 
/*    */   public PrimitiveGenerator(String stringDefault)
/*    */   {
/* 47 */     this.stringDefault = stringDefault;
/*    */   }
/*    */ 
/*    */   public String getStringDefault()
/*    */   {
/* 55 */     return this.stringDefault;
/*    */   }
/*    */ 
/*    */   public Object create(@Nullable Class<?> type)
/*    */   {
/* 60 */     if (type == null)
/* 61 */       return null;
/* 62 */     if (type.isPrimitive())
/* 63 */       return Defaults.defaultValue(type);
/* 64 */     if (Primitives.isWrapperType(type))
/* 65 */       return Defaults.defaultValue(Primitives.unwrap(type));
/* 66 */     if (type.isArray()) {
/* 67 */       Class arrayType = type.getComponentType();
/* 68 */       return Array.newInstance(arrayType, 0);
/* 69 */     }if (type.isEnum()) {
/* 70 */       Object[] values = type.getEnumConstants();
/* 71 */       if ((values != null) && (values.length > 0))
/* 72 */         return values[0];
/* 73 */     } else if (type.equals(String.class)) {
/* 74 */       return this.stringDefault;
/*    */     }
/*    */ 
/* 78 */     return null;
/*    */   }
/*    */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.reflect.instances.PrimitiveGenerator
 * JD-Core Version:    0.6.2
 */