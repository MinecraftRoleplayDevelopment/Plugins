/*     */ package com.comphenix.protocol.reflect;
/*     */ 
/*     */ import com.google.common.collect.BiMap;
/*     */ import com.google.common.collect.HashBiMap;
/*     */ import java.lang.reflect.Field;
/*     */ import java.lang.reflect.Modifier;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.Set;
/*     */ 
/*     */ public class ObjectEnum<T>
/*     */   implements Iterable<T>
/*     */ {
/*  38 */   protected BiMap<T, String> members = HashBiMap.create();
/*     */ 
/*     */   public ObjectEnum(Class<T> fieldType)
/*     */   {
/*  44 */     registerAll(fieldType);
/*     */   }
/*     */ 
/*     */   protected void registerAll(Class<T> fieldType)
/*     */   {
/*     */     try
/*     */     {
/*  54 */       for (Field entry : getClass().getFields())
/*  55 */         if ((Modifier.isStatic(entry.getModifiers())) && (fieldType.isAssignableFrom(entry.getType()))) {
/*  56 */           Object value = entry.get(null);
/*     */ 
/*  58 */           if (value == null) {
/*  59 */             throw new IllegalArgumentException("Field " + entry + " was NULL. Remember to " + "construct the object after the field has been declared.");
/*     */           }
/*  61 */           registerMember(value, entry.getName());
/*     */         }
/*     */     }
/*     */     catch (IllegalArgumentException e)
/*     */     {
/*  66 */       e.printStackTrace();
/*     */     } catch (IllegalAccessException e) {
/*  68 */       e.printStackTrace();
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean registerMember(T instance, String name)
/*     */   {
/*  79 */     if (!this.members.containsKey(instance)) {
/*  80 */       this.members.put(instance, name);
/*  81 */       return true;
/*     */     }
/*  83 */     return false;
/*     */   }
/*     */ 
/*     */   public boolean hasMember(T member)
/*     */   {
/*  92 */     return this.members.containsKey(member);
/*     */   }
/*     */ 
/*     */   public T valueOf(String name)
/*     */   {
/* 101 */     return this.members.inverse().get(name);
/*     */   }
/*     */ 
/*     */   public String getDeclaredName(T member)
/*     */   {
/* 110 */     return (String)this.members.get(member);
/*     */   }
/*     */ 
/*     */   public Set<T> values()
/*     */   {
/* 118 */     return new HashSet(this.members.keySet());
/*     */   }
/*     */ 
/*     */   public Iterator<T> iterator()
/*     */   {
/* 123 */     return this.members.keySet().iterator();
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.reflect.ObjectEnum
 * JD-Core Version:    0.6.2
 */