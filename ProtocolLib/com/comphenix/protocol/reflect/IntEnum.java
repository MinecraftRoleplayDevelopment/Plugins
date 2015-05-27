/*     */ package com.comphenix.protocol.reflect;
/*     */ 
/*     */ import com.google.common.collect.BiMap;
/*     */ import com.google.common.collect.HashBiMap;
/*     */ import java.lang.reflect.Field;
/*     */ import java.util.HashSet;
/*     */ import java.util.Set;
/*     */ 
/*     */ public class IntEnum
/*     */ {
/*  35 */   protected BiMap<Integer, String> members = HashBiMap.create();
/*     */ 
/*     */   public IntEnum()
/*     */   {
/*  41 */     registerAll();
/*     */   }
/*     */ 
/*     */   protected void registerAll()
/*     */   {
/*     */     try
/*     */     {
/*  50 */       for (Field entry : getClass().getFields()) {
/*  51 */         if (entry.getType().equals(Integer.TYPE))
/*  52 */           registerMember(entry.getInt(this), entry.getName());
/*     */       }
/*     */     }
/*     */     catch (IllegalArgumentException e)
/*     */     {
/*  57 */       e.printStackTrace();
/*     */     } catch (IllegalAccessException e) {
/*  59 */       e.printStackTrace();
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void registerMember(int id, String name)
/*     */   {
/*  69 */     this.members.put(Integer.valueOf(id), name);
/*     */   }
/*     */ 
/*     */   public boolean hasMember(int id)
/*     */   {
/*  78 */     return this.members.containsKey(Integer.valueOf(id));
/*     */   }
/*     */ 
/*     */   public Integer valueOf(String name)
/*     */   {
/*  87 */     return (Integer)this.members.inverse().get(name);
/*     */   }
/*     */ 
/*     */   public String getDeclaredName(Integer id)
/*     */   {
/*  96 */     return (String)this.members.get(id);
/*     */   }
/*     */ 
/*     */   public Set<Integer> values()
/*     */   {
/* 104 */     return new HashSet(this.members.keySet());
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.reflect.IntEnum
 * JD-Core Version:    0.6.2
 */