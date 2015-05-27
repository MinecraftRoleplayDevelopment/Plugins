/*     */ package com.comphenix.protocol.reflect.compiler;
/*     */ 
/*     */ import com.comphenix.net.sf.cglib.asm.Type;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ 
/*     */ class MethodDescriptor
/*     */ {
/*     */   private final String name;
/*     */   private final String desc;
/*  46 */   private static final Map<String, String> DESCRIPTORS = new HashMap();
/*     */ 
/*     */   public MethodDescriptor(String name, String desc)
/*     */   {
/*  65 */     this.name = name;
/*  66 */     this.desc = desc;
/*     */   }
/*     */ 
/*     */   public MethodDescriptor(String name, Type returnType, Type[] argumentTypes)
/*     */   {
/*  81 */     this(name, Type.getMethodDescriptor(returnType, argumentTypes));
/*     */   }
/*     */ 
/*     */   public static MethodDescriptor getMethod(String method)
/*     */     throws IllegalArgumentException
/*     */   {
/* 102 */     return getMethod(method, false);
/*     */   }
/*     */ 
/*     */   public static MethodDescriptor getMethod(String method, boolean defaultPackage)
/*     */     throws IllegalArgumentException
/*     */   {
/* 129 */     int space = method.indexOf(' ');
/* 130 */     int start = method.indexOf('(', space) + 1;
/* 131 */     int end = method.indexOf(')', start);
/* 132 */     if ((space == -1) || (start == -1) || (end == -1)) {
/* 133 */       throw new IllegalArgumentException(); } String returnType = method.substring(0, space);
/* 136 */     String methodName = method.substring(space + 1, start - 1).trim();
/* 137 */     StringBuffer sb = new StringBuffer();
/* 138 */     sb.append('(');
/*     */     int p;
/*     */     do { p = method.indexOf(',', start);
/*     */       String s;
/*     */       String s;
/* 143 */       if (p == -1) {
/* 144 */         s = map(method.substring(start, end).trim(), defaultPackage);
/*     */       } else {
/* 146 */         s = map(method.substring(start, p).trim(), defaultPackage);
/* 147 */         start = p + 1;
/*     */       }
/* 149 */       sb.append(s); }
/* 150 */     while (p != -1);
/* 151 */     sb.append(')');
/* 152 */     sb.append(map(returnType, defaultPackage));
/* 153 */     return new MethodDescriptor(methodName, sb.toString());
/*     */   }
/*     */ 
/*     */   private static String map(String type, boolean defaultPackage) {
/* 157 */     if ("".equals(type)) {
/* 158 */       return type;
/*     */     }
/*     */ 
/* 161 */     StringBuffer sb = new StringBuffer();
/* 162 */     int index = 0;
/* 163 */     while ((index = type.indexOf("[]", index) + 1) > 0) {
/* 164 */       sb.append('[');
/*     */     }
/*     */ 
/* 167 */     String t = type.substring(0, type.length() - sb.length() * 2);
/* 168 */     String desc = (String)DESCRIPTORS.get(t);
/* 169 */     if (desc != null) {
/* 170 */       sb.append(desc);
/*     */     } else {
/* 172 */       sb.append('L');
/* 173 */       if (t.indexOf('.') < 0) {
/* 174 */         if (!defaultPackage) {
/* 175 */           sb.append("java/lang/");
/*     */         }
/* 177 */         sb.append(t);
/*     */       } else {
/* 179 */         sb.append(t.replace('.', '/'));
/*     */       }
/* 181 */       sb.append(';');
/*     */     }
/* 183 */     return sb.toString();
/*     */   }
/*     */ 
/*     */   public String getName()
/*     */   {
/* 192 */     return this.name;
/*     */   }
/*     */ 
/*     */   public String getDescriptor()
/*     */   {
/* 201 */     return this.desc;
/*     */   }
/*     */ 
/*     */   public Type getReturnType()
/*     */   {
/* 210 */     return Type.getReturnType(this.desc);
/*     */   }
/*     */ 
/*     */   public Type[] getArgumentTypes()
/*     */   {
/* 219 */     return Type.getArgumentTypes(this.desc);
/*     */   }
/*     */ 
/*     */   public String toString() {
/* 223 */     return this.name + this.desc;
/*     */   }
/*     */ 
/*     */   public boolean equals(Object o) {
/* 227 */     if (!(o instanceof MethodDescriptor)) {
/* 228 */       return false;
/*     */     }
/* 230 */     MethodDescriptor other = (MethodDescriptor)o;
/* 231 */     return (this.name.equals(other.name)) && (this.desc.equals(other.desc));
/*     */   }
/*     */ 
/*     */   public int hashCode() {
/* 235 */     return this.name.hashCode() ^ this.desc.hashCode();
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  47 */     DESCRIPTORS.put("void", "V");
/*  48 */     DESCRIPTORS.put("byte", "B");
/*  49 */     DESCRIPTORS.put("char", "C");
/*  50 */     DESCRIPTORS.put("double", "D");
/*  51 */     DESCRIPTORS.put("float", "F");
/*  52 */     DESCRIPTORS.put("int", "I");
/*  53 */     DESCRIPTORS.put("long", "J");
/*  54 */     DESCRIPTORS.put("short", "S");
/*  55 */     DESCRIPTORS.put("boolean", "Z");
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.reflect.compiler.MethodDescriptor
 * JD-Core Version:    0.6.2
 */