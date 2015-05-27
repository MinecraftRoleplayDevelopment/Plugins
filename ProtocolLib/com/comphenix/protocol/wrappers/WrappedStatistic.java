/*    */ package com.comphenix.protocol.wrappers;
/*    */ 
/*    */ import com.comphenix.protocol.reflect.FuzzyReflection;
/*    */ import com.comphenix.protocol.reflect.accessors.Accessors;
/*    */ import com.comphenix.protocol.reflect.accessors.FieldAccessor;
/*    */ import com.comphenix.protocol.reflect.accessors.MethodAccessor;
/*    */ import com.comphenix.protocol.utility.MinecraftReflection;
/*    */ import com.google.common.base.Function;
/*    */ import com.google.common.collect.Iterables;
/*    */ import java.util.Map;
/*    */ 
/*    */ public class WrappedStatistic extends AbstractWrapper
/*    */ {
/* 18 */   private static final Class<?> STATISTIC = MinecraftReflection.getStatisticClass();
/* 19 */   private static final Class<?> STATISTIC_LIST = MinecraftReflection.getStatisticListClass();
/*    */ 
/* 21 */   private static final MethodAccessor FIND_STATISTICS = Accessors.getMethodAccessor(FuzzyReflection.fromClass(STATISTIC_LIST).getMethodByParameters("findStatistic", STATISTIC, new Class[] { String.class }));
/*    */ 
/* 26 */   private static final FieldAccessor MAP_ACCESSOR = Accessors.getFieldAccessor(STATISTIC_LIST, Map.class, true);
/* 27 */   private static final FieldAccessor GET_NAME = Accessors.getFieldAccessor(STATISTIC, String.class, true);
/*    */   private final String name;
/*    */ 
/*    */   private WrappedStatistic(Object handle)
/*    */   {
/* 32 */     super(STATISTIC);
/* 33 */     setHandle(handle);
/*    */ 
/* 35 */     this.name = ((String)GET_NAME.get(handle));
/*    */   }
/*    */ 
/*    */   public static WrappedStatistic fromHandle(Object handle)
/*    */   {
/* 44 */     return new WrappedStatistic(handle);
/*    */   }
/*    */ 
/*    */   public static WrappedStatistic fromName(String name)
/*    */   {
/* 53 */     Object handle = FIND_STATISTICS.invoke(null, new Object[] { name });
/* 54 */     return handle != null ? fromHandle(handle) : null;
/*    */   }
/*    */ 
/*    */   public static Iterable<WrappedStatistic> values()
/*    */   {
/* 63 */     Map map = (Map)MAP_ACCESSOR.get(null);
/*    */ 
/* 65 */     return Iterables.transform(map.values(), new Function() {
/*    */       public WrappedStatistic apply(Object handle) {
/* 67 */         return WrappedStatistic.fromHandle(handle);
/*    */       }
/*    */     });
/*    */   }
/*    */ 
/*    */   public String getName()
/*    */   {
/* 77 */     return this.name;
/*    */   }
/*    */ 
/*    */   public String toString()
/*    */   {
/* 82 */     return String.valueOf(this.handle);
/*    */   }
/*    */ 
/*    */   public int hashCode()
/*    */   {
/* 87 */     return this.handle.hashCode();
/*    */   }
/*    */ 
/*    */   public boolean equals(Object obj)
/*    */   {
/* 92 */     if (obj == this) {
/* 93 */       return true;
/*    */     }
/* 95 */     if ((obj instanceof WrappedGameProfile)) {
/* 96 */       WrappedStatistic other = (WrappedStatistic)obj;
/* 97 */       return this.handle.equals(other.handle);
/*    */     }
/* 99 */     return false;
/*    */   }
/*    */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.wrappers.WrappedStatistic
 * JD-Core Version:    0.6.2
 */