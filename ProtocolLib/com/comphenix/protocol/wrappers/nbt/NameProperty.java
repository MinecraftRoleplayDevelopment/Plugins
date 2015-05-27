/*    */ package com.comphenix.protocol.wrappers.nbt;
/*    */ 
/*    */ import com.comphenix.protocol.reflect.StructureModifier;
/*    */ import com.google.common.collect.Maps;
/*    */ import java.util.Map;
/*    */ 
/*    */ public abstract class NameProperty
/*    */ {
/*  9 */   private static final Map<Class<?>, StructureModifier<String>> MODIFIERS = Maps.newConcurrentMap();
/*    */ 
/*    */   public abstract String getName();
/*    */ 
/*    */   public abstract void setName(String paramString);
/*    */ 
/*    */   private static StructureModifier<String> getModifier(Class<?> baseClass)
/*    */   {
/* 29 */     StructureModifier modifier = (StructureModifier)MODIFIERS.get(baseClass);
/*    */ 
/* 32 */     if (modifier == null) {
/* 33 */       modifier = new StructureModifier(baseClass, Object.class, false).withType(String.class);
/* 34 */       MODIFIERS.put(baseClass, modifier);
/*    */     }
/* 36 */     return modifier;
/*    */   }
/*    */ 
/*    */   public static boolean hasStringIndex(Class<?> baseClass, int index)
/*    */   {
/* 46 */     if (index < 0)
/* 47 */       return false;
/* 48 */     return index < getModifier(baseClass).size();
/*    */   }
/*    */ 
/*    */   public static NameProperty fromStringIndex(Class<?> baseClass, Object target, final int index)
/*    */   {
/* 59 */     StructureModifier modifier = getModifier(baseClass).withTarget(target);
/*    */ 
/* 61 */     return new NameProperty()
/*    */     {
/*    */       public String getName() {
/* 64 */         return (String)this.val$modifier.read(index);
/*    */       }
/*    */ 
/*    */       public void setName(String name)
/*    */       {
/* 69 */         this.val$modifier.write(index, name);
/*    */       }
/*    */     };
/*    */   }
/*    */ 
/*    */   public static NameProperty fromBean()
/*    */   {
/* 79 */     return new NameProperty()
/*    */     {
/*    */       private String name;
/*    */ 
/*    */       public void setName(String name) {
/* 84 */         this.name = name;
/*    */       }
/*    */ 
/*    */       public String getName()
/*    */       {
/* 89 */         return this.name;
/*    */       }
/*    */     };
/*    */   }
/*    */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.wrappers.nbt.NameProperty
 * JD-Core Version:    0.6.2
 */