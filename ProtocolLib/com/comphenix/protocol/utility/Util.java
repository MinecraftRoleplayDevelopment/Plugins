/*    */ package com.comphenix.protocol.utility;
/*    */ 
/*    */ import com.comphenix.protocol.reflect.accessors.Accessors;
/*    */ import com.comphenix.protocol.reflect.accessors.MethodAccessor;
/*    */ import java.lang.reflect.Method;
/*    */ import java.util.ArrayList;
/*    */ import java.util.Arrays;
/*    */ import java.util.Collection;
/*    */ import java.util.List;
/*    */ import org.bukkit.Bukkit;
/*    */ import org.bukkit.entity.Player;
/*    */ 
/*    */ public class Util
/*    */ {
/*    */   private static MethodAccessor getOnlinePlayers;
/*    */   private static boolean reflectionRequired;
/*    */ 
/*    */   public static List<Player> getOnlinePlayers()
/*    */   {
/* 59 */     if (reflectionRequired) {
/* 60 */       return Arrays.asList((Player[])getOnlinePlayers.invoke(null, new Object[0]));
/*    */     }
/*    */ 
/* 63 */     return (List)Bukkit.getOnlinePlayers();
/*    */   }
/*    */ 
/*    */   public static <E> List<E> asList(E[] elements) {
/* 67 */     List list = new ArrayList(elements.length);
/* 68 */     for (Object element : elements) {
/* 69 */       list.add(element);
/*    */     }
/*    */ 
/* 72 */     return list;
/*    */   }
/*    */ 
/*    */   static
/*    */   {
/*    */     try
/*    */     {
/* 43 */       Method method = Bukkit.class.getMethod("getOnlinePlayers", new Class[0]);
/* 44 */       getOnlinePlayers = Accessors.getMethodAccessor(method);
/* 45 */       reflectionRequired = !method.getReturnType().isAssignableFrom(Collection.class);
/*    */     } catch (Throwable ex) {
/* 47 */       throw new RuntimeException("Failed to obtain getOnlinePlayers method.", ex);
/*    */     }
/*    */   }
/*    */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.utility.Util
 * JD-Core Version:    0.6.2
 */