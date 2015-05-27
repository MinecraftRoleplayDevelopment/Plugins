/*     */ package com.comphenix.protocol.reflect;
/*     */ 
/*     */ import com.google.common.primitives.Primitives;
/*     */ import java.lang.reflect.Array;
/*     */ import java.lang.reflect.Field;
/*     */ import java.lang.reflect.Modifier;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.Set;
/*     */ 
/*     */ public class PrettyPrinter
/*     */ {
/*     */   public static final int RECURSE_DEPTH = 3;
/*     */ 
/*     */   public static String printObject(Object object)
/*     */     throws IllegalAccessException
/*     */   {
/*  71 */     if (object == null) {
/*  72 */       throw new IllegalArgumentException("object cannot be NULL.");
/*     */     }
/*  74 */     return printObject(object, object.getClass(), Object.class);
/*     */   }
/*     */ 
/*     */   public static String printObject(Object object, Class<?> start, Class<?> stop)
/*     */     throws IllegalAccessException
/*     */   {
/*  85 */     if (object == null) {
/*  86 */       throw new IllegalArgumentException("object cannot be NULL.");
/*     */     }
/*  88 */     return printObject(object, start, stop, 3);
/*     */   }
/*     */ 
/*     */   public static String printObject(Object object, Class<?> start, Class<?> stop, int hierachyDepth)
/*     */     throws IllegalAccessException
/*     */   {
/*  99 */     return printObject(object, start, stop, hierachyDepth, ObjectPrinter.DEFAULT);
/*     */   }
/*     */ 
/*     */   public static String printObject(Object object, Class<?> start, Class<?> stop, int hierachyDepth, ObjectPrinter printer)
/*     */     throws IllegalAccessException
/*     */   {
/* 112 */     if (object == null) {
/* 113 */       throw new IllegalArgumentException("object cannot be NULL.");
/*     */     }
/* 115 */     StringBuilder output = new StringBuilder();
/* 116 */     Set previous = new HashSet();
/*     */ 
/* 119 */     output.append("{ ");
/* 120 */     printObject(output, object, start, stop, previous, hierachyDepth, true, printer);
/* 121 */     output.append(" }");
/*     */ 
/* 123 */     return output.toString();
/*     */   }
/*     */ 
/*     */   private static void printIterables(StringBuilder output, Iterable iterable, Class<?> stop, Set<Object> previous, int hierachyIndex, ObjectPrinter printer)
/*     */     throws IllegalAccessException
/*     */   {
/* 130 */     boolean first = true;
/* 131 */     output.append("(");
/*     */ 
/* 133 */     for (Iterator i$ = iterable.iterator(); i$.hasNext(); ) { Object value = i$.next();
/* 134 */       if (first)
/* 135 */         first = false;
/*     */       else {
/* 137 */         output.append(", ");
/*     */       }
/*     */ 
/* 140 */       printValue(output, value, stop, previous, hierachyIndex - 1, printer);
/*     */     }
/*     */ 
/* 143 */     output.append(")");
/*     */   }
/*     */ 
/*     */   private static void printMap(StringBuilder output, Map<Object, Object> map, Class<?> current, Class<?> stop, Set<Object> previous, int hierachyIndex, ObjectPrinter printer)
/*     */     throws IllegalAccessException
/*     */   {
/* 159 */     boolean first = true;
/* 160 */     output.append("[");
/*     */ 
/* 162 */     for (Map.Entry entry : map.entrySet()) {
/* 163 */       if (first)
/* 164 */         first = false;
/*     */       else {
/* 166 */         output.append(", ");
/*     */       }
/* 168 */       printValue(output, entry.getKey(), stop, previous, hierachyIndex - 1, printer);
/* 169 */       output.append(": ");
/* 170 */       printValue(output, entry.getValue(), stop, previous, hierachyIndex - 1, printer);
/*     */     }
/*     */ 
/* 173 */     output.append("]");
/*     */   }
/*     */ 
/*     */   private static void printArray(StringBuilder output, Object array, Class<?> current, Class<?> stop, Set<Object> previous, int hierachyIndex, ObjectPrinter printer)
/*     */     throws IllegalAccessException
/*     */   {
/* 179 */     Class component = current.getComponentType();
/* 180 */     boolean first = true;
/*     */ 
/* 182 */     if (!component.isArray())
/* 183 */       output.append(component.getName());
/* 184 */     output.append("[");
/*     */ 
/* 186 */     for (int i = 0; i < Array.getLength(array); i++) {
/* 187 */       if (first)
/* 188 */         first = false;
/*     */       else {
/* 190 */         output.append(", ");
/*     */       }
/*     */       try
/*     */       {
/* 194 */         printValue(output, Array.get(array, i), component, stop, previous, hierachyIndex - 1, printer);
/*     */       } catch (ArrayIndexOutOfBoundsException e) {
/* 196 */         e.printStackTrace();
/* 197 */         break;
/*     */       } catch (IllegalArgumentException e) {
/* 199 */         e.printStackTrace();
/* 200 */         break;
/*     */       }
/*     */     }
/*     */ 
/* 204 */     output.append("]");
/*     */   }
/*     */ 
/*     */   private static void printObject(StringBuilder output, Object object, Class<?> current, Class<?> stop, Set<Object> previous, int hierachyIndex, boolean first, ObjectPrinter printer)
/*     */     throws IllegalAccessException
/*     */   {
/* 213 */     if ((current == null) || (current == Object.class) || ((stop != null) && (current.equals(stop)))) {
/* 214 */       return;
/*     */     }
/*     */ 
/* 218 */     previous.add(object);
/*     */ 
/* 221 */     if (hierachyIndex < 0) {
/* 222 */       output.append("...");
/* 223 */       return;
/*     */     }
/*     */ 
/* 226 */     for (Field field : current.getDeclaredFields()) {
/* 227 */       int mod = field.getModifiers();
/*     */ 
/* 230 */       if ((!Modifier.isTransient(mod)) && (!Modifier.isStatic(mod))) {
/* 231 */         Class type = field.getType();
/* 232 */         Object value = FieldUtils.readField(field, object, true);
/*     */ 
/* 234 */         if (first)
/* 235 */           first = false;
/*     */         else {
/* 237 */           output.append(", ");
/*     */         }
/*     */ 
/* 240 */         output.append(field.getName());
/* 241 */         output.append(" = ");
/* 242 */         printValue(output, value, type, stop, previous, hierachyIndex - 1, printer);
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 247 */     printObject(output, object, current.getSuperclass(), stop, previous, hierachyIndex, first, printer);
/*     */   }
/*     */ 
/*     */   private static void printValue(StringBuilder output, Object value, Class<?> stop, Set<Object> previous, int hierachyIndex, ObjectPrinter printer)
/*     */     throws IllegalAccessException
/*     */   {
/* 253 */     printValue(output, value, value != null ? value.getClass() : null, stop, previous, hierachyIndex, printer);
/*     */   }
/*     */ 
/*     */   private static void printValue(StringBuilder output, Object value, Class<?> type, Class<?> stop, Set<Object> previous, int hierachyIndex, ObjectPrinter printer)
/*     */     throws IllegalAccessException
/*     */   {
/* 262 */     if (printer.print(output, value))
/* 263 */       return;
/* 264 */     if (value == null) {
/* 265 */       output.append("NULL");
/* 266 */     } else if ((type.isPrimitive()) || (Primitives.isWrapperType(type))) {
/* 267 */       output.append(value);
/* 268 */     } else if ((type == String.class) || (hierachyIndex <= 0)) {
/* 269 */       output.append("\"" + value + "\"");
/* 270 */     } else if (type.isArray()) {
/* 271 */       printArray(output, value, type, stop, previous, hierachyIndex, printer);
/* 272 */     } else if (Iterable.class.isAssignableFrom(type)) {
/* 273 */       printIterables(output, (Iterable)value, stop, previous, hierachyIndex, printer);
/* 274 */     } else if (Map.class.isAssignableFrom(type)) {
/* 275 */       printMap(output, (Map)value, type, stop, previous, hierachyIndex, printer);
/* 276 */     } else if ((ClassLoader.class.isAssignableFrom(type)) || (previous.contains(value)))
/*     */     {
/* 278 */       output.append("\"" + value + "\"");
/*     */     } else {
/* 280 */       output.append("{ ");
/* 281 */       printObject(output, value, value.getClass(), stop, previous, hierachyIndex, true, printer);
/* 282 */       output.append(" }");
/*     */     }
/*     */   }
/*     */ 
/*     */   public static abstract interface ObjectPrinter
/*     */   {
/*  41 */     public static final ObjectPrinter DEFAULT = new ObjectPrinter()
/*     */     {
/*     */       public boolean print(StringBuilder output, Object value) {
/*  44 */         return false;
/*     */       }
/*  41 */     };
/*     */ 
/*     */     public abstract boolean print(StringBuilder paramStringBuilder, Object paramObject);
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.reflect.PrettyPrinter
 * JD-Core Version:    0.6.2
 */