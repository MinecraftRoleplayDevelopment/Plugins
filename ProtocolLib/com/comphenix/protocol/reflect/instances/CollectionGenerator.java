/*    */ package com.comphenix.protocol.reflect.instances;
/*    */ 
/*    */ import java.util.ArrayList;
/*    */ import java.util.Collection;
/*    */ import java.util.HashMap;
/*    */ import java.util.HashSet;
/*    */ import java.util.LinkedList;
/*    */ import java.util.List;
/*    */ import java.util.Map;
/*    */ import java.util.Queue;
/*    */ import java.util.Set;
/*    */ import java.util.SortedMap;
/*    */ import java.util.SortedSet;
/*    */ import java.util.TreeMap;
/*    */ import java.util.TreeSet;
/*    */ import javax.annotation.Nullable;
/*    */ 
/*    */ public class CollectionGenerator
/*    */   implements InstanceProvider
/*    */ {
/* 45 */   public static final CollectionGenerator INSTANCE = new CollectionGenerator();
/*    */ 
/*    */   public Object create(@Nullable Class<?> type)
/*    */   {
/* 50 */     if ((type != null) && (type.isInterface())) {
/* 51 */       if ((type.equals(Collection.class)) || (type.equals(List.class)))
/* 52 */         return new ArrayList();
/* 53 */       if (type.equals(Set.class))
/* 54 */         return new HashSet();
/* 55 */       if (type.equals(Map.class))
/* 56 */         return new HashMap();
/* 57 */       if (type.equals(SortedSet.class))
/* 58 */         return new TreeSet();
/* 59 */       if (type.equals(SortedMap.class))
/* 60 */         return new TreeMap();
/* 61 */       if (type.equals(Queue.class)) {
/* 62 */         return new LinkedList();
/*    */       }
/*    */     }
/*    */ 
/* 66 */     return null;
/*    */   }
/*    */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.reflect.instances.CollectionGenerator
 * JD-Core Version:    0.6.2
 */