/*      */ package com.comphenix.protocol.reflect;
/*      */ 
/*      */ import java.lang.ref.Reference;
/*      */ import java.lang.ref.WeakReference;
/*      */ import java.lang.reflect.InvocationTargetException;
/*      */ import java.lang.reflect.Method;
/*      */ import java.lang.reflect.Modifier;
/*      */ import java.util.Arrays;
/*      */ import java.util.Collections;
/*      */ import java.util.Map;
/*      */ import java.util.WeakHashMap;
/*      */ import java.util.logging.Logger;
/*      */ import org.bukkit.Bukkit;
/*      */ 
/*      */ public class MethodUtils
/*      */ {
/*   72 */   private static boolean loggedAccessibleWarning = false;
/*      */ 
/*   82 */   private static boolean CACHE_METHODS = true;
/*      */ 
/*   85 */   private static final Class[] EMPTY_CLASS_PARAMETERS = new Class[0];
/*      */ 
/*   87 */   private static final Object[] EMPTY_OBJECT_ARRAY = new Object[0];
/*      */ 
/*  110 */   private static final Map cache = Collections.synchronizedMap(new WeakHashMap());
/*      */ 
/*      */   public static synchronized void setCacheMethods(boolean cacheMethods)
/*      */   {
/*  123 */     CACHE_METHODS = cacheMethods;
/*  124 */     if (!CACHE_METHODS)
/*  125 */       clearCache();
/*      */   }
/*      */ 
/*      */   public static synchronized int clearCache()
/*      */   {
/*  135 */     int size = cache.size();
/*  136 */     cache.clear();
/*  137 */     return size;
/*      */   }
/*      */ 
/*      */   public static Object invokeMethod(Object object, String methodName, Object arg)
/*      */     throws NoSuchMethodException, IllegalAccessException, InvocationTargetException
/*      */   {
/*  176 */     Object[] args = { arg };
/*  177 */     return invokeMethod(object, methodName, args);
/*      */   }
/*      */ 
/*      */   public static Object invokeMethod(Object object, String methodName, Object[] args)
/*      */     throws NoSuchMethodException, IllegalAccessException, InvocationTargetException
/*      */   {
/*  218 */     if (args == null) {
/*  219 */       args = EMPTY_OBJECT_ARRAY;
/*      */     }
/*  221 */     int arguments = args.length;
/*  222 */     Class[] parameterTypes = new Class[arguments];
/*  223 */     for (int i = 0; i < arguments; i++) {
/*  224 */       parameterTypes[i] = args[i].getClass();
/*      */     }
/*  226 */     return invokeMethod(object, methodName, args, parameterTypes);
/*      */   }
/*      */ 
/*      */   public static Object invokeMethod(Object object, String methodName, Object[] args, Class[] parameterTypes)
/*      */     throws NoSuchMethodException, IllegalAccessException, InvocationTargetException
/*      */   {
/*  267 */     if (parameterTypes == null) {
/*  268 */       parameterTypes = EMPTY_CLASS_PARAMETERS;
/*      */     }
/*  270 */     if (args == null) {
/*  271 */       args = EMPTY_OBJECT_ARRAY;
/*      */     }
/*      */ 
/*  274 */     Method method = getMatchingAccessibleMethod(object.getClass(), methodName, parameterTypes);
/*      */ 
/*  278 */     if (method == null) {
/*  279 */       throw new NoSuchMethodException("No such accessible method: " + methodName + "() on object: " + object.getClass().getName());
/*      */     }
/*      */ 
/*  282 */     return method.invoke(object, args);
/*      */   }
/*      */ 
/*      */   public static Object invokeExactMethod(Object object, String methodName, Object arg)
/*      */     throws NoSuchMethodException, IllegalAccessException, InvocationTargetException
/*      */   {
/*  314 */     Object[] args = { arg };
/*  315 */     return invokeExactMethod(object, methodName, args);
/*      */   }
/*      */ 
/*      */   public static Object invokeExactMethod(Object object, String methodName, Object[] args)
/*      */     throws NoSuchMethodException, IllegalAccessException, InvocationTargetException
/*      */   {
/*  346 */     if (args == null) {
/*  347 */       args = EMPTY_OBJECT_ARRAY;
/*      */     }
/*  349 */     int arguments = args.length;
/*  350 */     Class[] parameterTypes = new Class[arguments];
/*  351 */     for (int i = 0; i < arguments; i++) {
/*  352 */       parameterTypes[i] = args[i].getClass();
/*      */     }
/*  354 */     return invokeExactMethod(object, methodName, args, parameterTypes);
/*      */   }
/*      */ 
/*      */   public static Object invokeExactMethod(Object object, String methodName, Object[] args, Class[] parameterTypes)
/*      */     throws NoSuchMethodException, IllegalAccessException, InvocationTargetException
/*      */   {
/*  388 */     if (args == null) {
/*  389 */       args = EMPTY_OBJECT_ARRAY;
/*      */     }
/*      */ 
/*  392 */     if (parameterTypes == null) {
/*  393 */       parameterTypes = EMPTY_CLASS_PARAMETERS;
/*      */     }
/*      */ 
/*  396 */     Method method = getAccessibleMethod(object.getClass(), methodName, parameterTypes);
/*      */ 
/*  400 */     if (method == null) {
/*  401 */       throw new NoSuchMethodException("No such accessible method: " + methodName + "() on object: " + object.getClass().getName());
/*      */     }
/*      */ 
/*  404 */     return method.invoke(object, args);
/*      */   }
/*      */ 
/*      */   public static Object invokeExactStaticMethod(Class objectClass, String methodName, Object[] args, Class[] parameterTypes)
/*      */     throws NoSuchMethodException, IllegalAccessException, InvocationTargetException
/*      */   {
/*  438 */     if (args == null) {
/*  439 */       args = EMPTY_OBJECT_ARRAY;
/*      */     }
/*      */ 
/*  442 */     if (parameterTypes == null) {
/*  443 */       parameterTypes = EMPTY_CLASS_PARAMETERS;
/*      */     }
/*      */ 
/*  446 */     Method method = getAccessibleMethod(objectClass, methodName, parameterTypes);
/*      */ 
/*  450 */     if (method == null) {
/*  451 */       throw new NoSuchMethodException("No such accessible method: " + methodName + "() on class: " + objectClass.getName());
/*      */     }
/*      */ 
/*  454 */     return method.invoke(null, args);
/*      */   }
/*      */ 
/*      */   public static Object invokeStaticMethod(Class objectClass, String methodName, Object arg)
/*      */     throws NoSuchMethodException, IllegalAccessException, InvocationTargetException
/*      */   {
/*  495 */     Object[] args = { arg };
/*  496 */     return invokeStaticMethod(objectClass, methodName, args);
/*      */   }
/*      */ 
/*      */   public static Object invokeStaticMethod(Class objectClass, String methodName, Object[] args)
/*      */     throws NoSuchMethodException, IllegalAccessException, InvocationTargetException
/*      */   {
/*  538 */     if (args == null) {
/*  539 */       args = EMPTY_OBJECT_ARRAY;
/*      */     }
/*  541 */     int arguments = args.length;
/*  542 */     Class[] parameterTypes = new Class[arguments];
/*  543 */     for (int i = 0; i < arguments; i++) {
/*  544 */       parameterTypes[i] = args[i].getClass();
/*      */     }
/*  546 */     return invokeStaticMethod(objectClass, methodName, args, parameterTypes);
/*      */   }
/*      */ 
/*      */   public static Object invokeStaticMethod(Class objectClass, String methodName, Object[] args, Class[] parameterTypes)
/*      */     throws NoSuchMethodException, IllegalAccessException, InvocationTargetException
/*      */   {
/*  588 */     if (parameterTypes == null) {
/*  589 */       parameterTypes = EMPTY_CLASS_PARAMETERS;
/*      */     }
/*  591 */     if (args == null) {
/*  592 */       args = EMPTY_OBJECT_ARRAY;
/*      */     }
/*      */ 
/*  595 */     Method method = getMatchingAccessibleMethod(objectClass, methodName, parameterTypes);
/*      */ 
/*  599 */     if (method == null) {
/*  600 */       throw new NoSuchMethodException("No such accessible method: " + methodName + "() on class: " + objectClass.getName());
/*      */     }
/*      */ 
/*  603 */     return method.invoke(null, args);
/*      */   }
/*      */ 
/*      */   public static Object invokeExactStaticMethod(Class objectClass, String methodName, Object arg)
/*      */     throws NoSuchMethodException, IllegalAccessException, InvocationTargetException
/*      */   {
/*  636 */     Object[] args = { arg };
/*  637 */     return invokeExactStaticMethod(objectClass, methodName, args);
/*      */   }
/*      */ 
/*      */   public static Object invokeExactStaticMethod(Class objectClass, String methodName, Object[] args)
/*      */     throws NoSuchMethodException, IllegalAccessException, InvocationTargetException
/*      */   {
/*  669 */     if (args == null) {
/*  670 */       args = EMPTY_OBJECT_ARRAY;
/*      */     }
/*  672 */     int arguments = args.length;
/*  673 */     Class[] parameterTypes = new Class[arguments];
/*  674 */     for (int i = 0; i < arguments; i++) {
/*  675 */       parameterTypes[i] = args[i].getClass();
/*      */     }
/*  677 */     return invokeExactStaticMethod(objectClass, methodName, args, parameterTypes);
/*      */   }
/*      */ 
/*      */   public static Method getAccessibleMethod(Class clazz, String methodName, Class[] parameterTypes)
/*      */   {
/*      */     try
/*      */     {
/*  700 */       MethodDescriptor md = new MethodDescriptor(clazz, methodName, parameterTypes, true);
/*      */ 
/*  702 */       Method method = getCachedMethod(md);
/*  703 */       if (method != null) {
/*  704 */         return method;
/*      */       }
/*      */ 
/*  707 */       method = getAccessibleMethod(clazz, clazz.getMethod(methodName, parameterTypes));
/*      */ 
/*  709 */       cacheMethod(md, method);
/*  710 */       return method; } catch (NoSuchMethodException e) {
/*      */     }
/*  712 */     return null;
/*      */   }
/*      */ 
/*      */   public static Method getAccessibleMethod(Method method)
/*      */   {
/*  729 */     if (method == null) {
/*  730 */       return null;
/*      */     }
/*      */ 
/*  733 */     return getAccessibleMethod(method.getDeclaringClass(), method);
/*      */   }
/*      */ 
/*      */   public static Method getAccessibleMethod(Class clazz, Method method)
/*      */   {
/*  750 */     if (method == null) {
/*  751 */       return null;
/*      */     }
/*      */ 
/*  755 */     if (!Modifier.isPublic(method.getModifiers())) {
/*  756 */       return null;
/*      */     }
/*      */ 
/*  759 */     boolean sameClass = true;
/*  760 */     if (clazz == null) {
/*  761 */       clazz = method.getDeclaringClass();
/*      */     } else {
/*  763 */       sameClass = clazz.equals(method.getDeclaringClass());
/*  764 */       if (!method.getDeclaringClass().isAssignableFrom(clazz)) {
/*  765 */         throw new IllegalArgumentException(clazz.getName() + " is not assignable from " + method.getDeclaringClass().getName());
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  771 */     if (Modifier.isPublic(clazz.getModifiers())) {
/*  772 */       if ((!sameClass) && (!Modifier.isPublic(method.getDeclaringClass().getModifiers()))) {
/*  773 */         setMethodAccessible(method);
/*      */       }
/*  775 */       return method;
/*      */     }
/*      */ 
/*  778 */     String methodName = method.getName();
/*  779 */     Class[] parameterTypes = method.getParameterTypes();
/*      */ 
/*  782 */     method = getAccessibleMethodFromInterfaceNest(clazz, methodName, parameterTypes);
/*      */ 
/*  788 */     if (method == null) {
/*  789 */       method = getAccessibleMethodFromSuperclass(clazz, methodName, parameterTypes);
/*      */     }
/*      */ 
/*  794 */     return method;
/*      */   }
/*      */ 
/*      */   private static Method getAccessibleMethodFromSuperclass(Class clazz, String methodName, Class[] parameterTypes)
/*      */   {
/*  814 */     Class parentClazz = clazz.getSuperclass();
/*  815 */     while (parentClazz != null) {
/*  816 */       if (Modifier.isPublic(parentClazz.getModifiers())) {
/*      */         try {
/*  818 */           return parentClazz.getMethod(methodName, parameterTypes);
/*      */         } catch (NoSuchMethodException e) {
/*  820 */           return null;
/*      */         }
/*      */       }
/*  823 */       parentClazz = parentClazz.getSuperclass();
/*      */     }
/*  825 */     return null;
/*      */   }
/*      */ 
/*      */   private static Method getAccessibleMethodFromInterfaceNest(Class clazz, String methodName, Class[] parameterTypes)
/*      */   {
/*  846 */     Method method = null;
/*      */ 
/*  849 */     for (; clazz != null; clazz = clazz.getSuperclass())
/*      */     {
/*  852 */       Class[] interfaces = clazz.getInterfaces();
/*  853 */       for (int i = 0; i < interfaces.length; i++)
/*      */       {
/*  856 */         if (Modifier.isPublic(interfaces[i].getModifiers()))
/*      */         {
/*      */           try
/*      */           {
/*  862 */             method = interfaces[i].getDeclaredMethod(methodName, parameterTypes);
/*      */           }
/*      */           catch (NoSuchMethodException e)
/*      */           {
/*      */           }
/*      */ 
/*  869 */           if (method != null) {
/*  870 */             return method;
/*      */           }
/*      */ 
/*  874 */           method = getAccessibleMethodFromInterfaceNest(interfaces[i], methodName, parameterTypes);
/*      */ 
/*  878 */           if (method != null) {
/*  879 */             return method;
/*      */           }
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  887 */     return null;
/*      */   }
/*      */ 
/*      */   public static Method getMatchingAccessibleMethod(Class clazz, String methodName, Class[] parameterTypes)
/*      */   {
/*  920 */     MethodDescriptor md = new MethodDescriptor(clazz, methodName, parameterTypes, false);
/*  921 */     Logger log = tryGetLogger();
/*      */     try
/*      */     {
/*  927 */       Method method = getCachedMethod(md);
/*  928 */       if (method != null) {
/*  929 */         return method;
/*      */       }
/*      */ 
/*  932 */       method = clazz.getMethod(methodName, parameterTypes);
/*      */ 
/*  934 */       setMethodAccessible(method);
/*      */ 
/*  936 */       cacheMethod(md, method);
/*  937 */       return method;
/*      */     }
/*      */     catch (NoSuchMethodException e)
/*      */     {
/*  942 */       int paramSize = parameterTypes.length;
/*  943 */       Method bestMatch = null;
/*  944 */       Method[] methods = clazz.getMethods();
/*  945 */       float bestMatchCost = 3.4028235E+38F;
/*  946 */       float myCost = 3.4028235E+38F;
/*  947 */       int i = 0; for (int size = methods.length; i < size; i++) {
/*  948 */         if (methods[i].getName().equals(methodName))
/*      */         {
/*  951 */           Class[] methodsParams = methods[i].getParameterTypes();
/*  952 */           int methodParamSize = methodsParams.length;
/*  953 */           if (methodParamSize == paramSize) {
/*  954 */             boolean match = true;
/*  955 */             for (int n = 0; n < methodParamSize; n++) {
/*  956 */               if (!isAssignmentCompatible(methodsParams[n], parameterTypes[n])) {
/*  957 */                 match = false;
/*  958 */                 break;
/*      */               }
/*      */             }
/*      */ 
/*  962 */             if (match)
/*      */             {
/*  964 */               Method method = getAccessibleMethod(clazz, methods[i]);
/*  965 */               if (method != null) {
/*  966 */                 setMethodAccessible(method);
/*  967 */                 myCost = getTotalTransformationCost(parameterTypes, method.getParameterTypes());
/*  968 */                 if (myCost < bestMatchCost) {
/*  969 */                   bestMatch = method;
/*  970 */                   bestMatchCost = myCost;
/*      */                 }
/*      */               }
/*      */ 
/*  974 */               if (log == null);
/*      */             }
/*      */           }
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*  981 */       if (bestMatch != null) {
/*  982 */         cacheMethod(md, bestMatch);
/*      */       }
/*  984 */       else if (log != null) {
/*  985 */         log.severe("No match found.");
/*      */       }
/*      */ 
/*  989 */       return bestMatch;
/*      */     }
/*      */   }
/*      */ 
/*      */   private static Logger tryGetLogger()
/*      */   {
/*      */     try
/*      */     {
/*  998 */       return Bukkit.getLogger(); } catch (Exception e) {
/*      */     }
/* 1000 */     return null;
/*      */   }
/*      */ 
/*      */   private static void setMethodAccessible(Method method)
/*      */   {
/*      */     try
/*      */     {
/* 1025 */       if (!method.isAccessible()) {
/* 1026 */         method.setAccessible(true);
/*      */       }
/*      */     }
/*      */     catch (SecurityException se)
/*      */     {
/* 1031 */       if (!loggedAccessibleWarning) {
/* 1032 */         boolean vulnerableJVM = false;
/*      */         try {
/* 1034 */           String specVersion = System.getProperty("java.specification.version");
/* 1035 */           if ((specVersion.charAt(0) == '1') && ((specVersion.charAt(2) == '0') || (specVersion.charAt(2) == '1') || (specVersion.charAt(2) == '2') || (specVersion.charAt(2) == '3')))
/*      */           {
/* 1041 */             vulnerableJVM = true;
/*      */           }
/*      */         }
/*      */         catch (SecurityException e) {
/* 1045 */           vulnerableJVM = true;
/*      */         }
/* 1047 */         if ((vulnerableJVM) && (tryGetLogger() != null)) {
/* 1048 */           tryGetLogger().info("Vulnerable JVM!");
/*      */         }
/*      */ 
/* 1051 */         loggedAccessibleWarning = true;
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private static float getTotalTransformationCost(Class[] srcArgs, Class[] destArgs)
/*      */   {
/* 1065 */     float totalCost = 0.0F;
/* 1066 */     for (int i = 0; i < srcArgs.length; i++)
/*      */     {
/* 1068 */       Class srcClass = srcArgs[i];
/* 1069 */       Class destClass = destArgs[i];
/* 1070 */       totalCost += getObjectTransformationCost(srcClass, destClass);
/*      */     }
/*      */ 
/* 1073 */     return totalCost;
/*      */   }
/*      */ 
/*      */   private static float getObjectTransformationCost(Class srcClass, Class destClass)
/*      */   {
/* 1085 */     float cost = 0.0F;
/* 1086 */     while ((destClass != null) && (!destClass.equals(srcClass))) {
/* 1087 */       if ((destClass.isInterface()) && (isAssignmentCompatible(destClass, srcClass)))
/*      */       {
/* 1092 */         cost += 0.25F;
/* 1093 */         break;
/*      */       }
/* 1095 */       cost += 1.0F;
/* 1096 */       destClass = destClass.getSuperclass();
/*      */     }
/*      */ 
/* 1103 */     if (destClass == null) {
/* 1104 */       cost += 1.5F;
/*      */     }
/*      */ 
/* 1107 */     return cost;
/*      */   }
/*      */ 
/*      */   public static final boolean isAssignmentCompatible(Class parameterType, Class parameterization)
/*      */   {
/* 1131 */     if (parameterType.isAssignableFrom(parameterization)) {
/* 1132 */       return true;
/*      */     }
/*      */ 
/* 1135 */     if (parameterType.isPrimitive())
/*      */     {
/* 1138 */       Class parameterWrapperClazz = getPrimitiveWrapper(parameterType);
/* 1139 */       if (parameterWrapperClazz != null) {
/* 1140 */         return parameterWrapperClazz.equals(parameterization);
/*      */       }
/*      */     }
/*      */ 
/* 1144 */     return false;
/*      */   }
/*      */ 
/*      */   public static Class getPrimitiveWrapper(Class primitiveType)
/*      */   {
/* 1156 */     if (Boolean.TYPE.equals(primitiveType))
/* 1157 */       return Boolean.class;
/* 1158 */     if (Float.TYPE.equals(primitiveType))
/* 1159 */       return Float.class;
/* 1160 */     if (Long.TYPE.equals(primitiveType))
/* 1161 */       return Long.class;
/* 1162 */     if (Integer.TYPE.equals(primitiveType))
/* 1163 */       return Integer.class;
/* 1164 */     if (Short.TYPE.equals(primitiveType))
/* 1165 */       return Short.class;
/* 1166 */     if (Byte.TYPE.equals(primitiveType))
/* 1167 */       return Byte.class;
/* 1168 */     if (Double.TYPE.equals(primitiveType))
/* 1169 */       return Double.class;
/* 1170 */     if (Character.TYPE.equals(primitiveType)) {
/* 1171 */       return Character.class;
/*      */     }
/*      */ 
/* 1174 */     return null;
/*      */   }
/*      */ 
/*      */   public static Class getPrimitiveType(Class wrapperType)
/*      */   {
/* 1187 */     if (Boolean.class.equals(wrapperType))
/* 1188 */       return Boolean.TYPE;
/* 1189 */     if (Float.class.equals(wrapperType))
/* 1190 */       return Float.TYPE;
/* 1191 */     if (Long.class.equals(wrapperType))
/* 1192 */       return Long.TYPE;
/* 1193 */     if (Integer.class.equals(wrapperType))
/* 1194 */       return Integer.TYPE;
/* 1195 */     if (Short.class.equals(wrapperType))
/* 1196 */       return Short.TYPE;
/* 1197 */     if (Byte.class.equals(wrapperType))
/* 1198 */       return Byte.TYPE;
/* 1199 */     if (Double.class.equals(wrapperType))
/* 1200 */       return Double.TYPE;
/* 1201 */     if (Character.class.equals(wrapperType)) {
/* 1202 */       return Character.TYPE;
/*      */     }
/* 1204 */     return null;
/*      */   }
/*      */ 
/*      */   public static Class toNonPrimitiveClass(Class clazz)
/*      */   {
/* 1215 */     if (clazz.isPrimitive()) {
/* 1216 */       Class primitiveClazz = getPrimitiveWrapper(clazz);
/*      */ 
/* 1218 */       if (primitiveClazz != null) {
/* 1219 */         return primitiveClazz;
/*      */       }
/* 1221 */       return clazz;
/*      */     }
/*      */ 
/* 1224 */     return clazz;
/*      */   }
/*      */ 
/*      */   private static Method getCachedMethod(MethodDescriptor md)
/*      */   {
/* 1236 */     if (CACHE_METHODS) {
/* 1237 */       Reference methodRef = (Reference)cache.get(md);
/* 1238 */       if (methodRef != null) {
/* 1239 */         return (Method)methodRef.get();
/*      */       }
/*      */     }
/* 1242 */     return null;
/*      */   }
/*      */ 
/*      */   private static void cacheMethod(MethodDescriptor md, Method method)
/*      */   {
/* 1253 */     if ((CACHE_METHODS) && 
/* 1254 */       (method != null))
/* 1255 */       cache.put(md, new WeakReference(method));
/*      */   }
/*      */ 
/*      */   private static class MethodDescriptor
/*      */   {
/*      */     private Class cls;
/*      */     private String methodName;
/*      */     private Class[] paramTypes;
/*      */     private boolean exact;
/*      */     private int hashCode;
/*      */ 
/*      */     public MethodDescriptor(Class cls, String methodName, Class[] paramTypes, boolean exact)
/*      */     {
/* 1279 */       if (cls == null) {
/* 1280 */         throw new IllegalArgumentException("Class cannot be null");
/*      */       }
/* 1282 */       if (methodName == null) {
/* 1283 */         throw new IllegalArgumentException("Method Name cannot be null");
/*      */       }
/* 1285 */       if (paramTypes == null) {
/* 1286 */         paramTypes = MethodUtils.EMPTY_CLASS_PARAMETERS;
/*      */       }
/*      */ 
/* 1289 */       this.cls = cls;
/* 1290 */       this.methodName = methodName;
/* 1291 */       this.paramTypes = paramTypes;
/* 1292 */       this.exact = exact;
/*      */ 
/* 1294 */       this.hashCode = methodName.length();
/*      */     }
/*      */ 
/*      */     public boolean equals(Object obj)
/*      */     {
/* 1302 */       if (!(obj instanceof MethodDescriptor)) {
/* 1303 */         return false;
/*      */       }
/* 1305 */       MethodDescriptor md = (MethodDescriptor)obj;
/*      */ 
/* 1307 */       return (this.exact == md.exact) && (this.methodName.equals(md.methodName)) && (this.cls.equals(md.cls)) && (Arrays.equals(this.paramTypes, md.paramTypes));
/*      */     }
/*      */ 
/*      */     public int hashCode()
/*      */     {
/* 1322 */       return this.hashCode;
/*      */     }
/*      */   }
/*      */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.reflect.MethodUtils
 * JD-Core Version:    0.6.2
 */