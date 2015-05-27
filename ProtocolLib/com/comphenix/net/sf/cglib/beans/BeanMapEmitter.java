/*     */ package com.comphenix.net.sf.cglib.beans;
/*     */ 
/*     */ import com.comphenix.net.sf.cglib.asm.ClassVisitor;
/*     */ import com.comphenix.net.sf.cglib.asm.Label;
/*     */ import com.comphenix.net.sf.cglib.asm.Type;
/*     */ import com.comphenix.net.sf.cglib.core.ClassEmitter;
/*     */ import com.comphenix.net.sf.cglib.core.CodeEmitter;
/*     */ import com.comphenix.net.sf.cglib.core.Constants;
/*     */ import com.comphenix.net.sf.cglib.core.EmitUtils;
/*     */ import com.comphenix.net.sf.cglib.core.MethodInfo;
/*     */ import com.comphenix.net.sf.cglib.core.ObjectSwitchCallback;
/*     */ import com.comphenix.net.sf.cglib.core.ReflectUtils;
/*     */ import com.comphenix.net.sf.cglib.core.Signature;
/*     */ import com.comphenix.net.sf.cglib.core.TypeUtils;
/*     */ import java.beans.PropertyDescriptor;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ 
/*     */ class BeanMapEmitter extends ClassEmitter
/*     */ {
/*  26 */   private static final Type BEAN_MAP = TypeUtils.parseType("com.comphenix.net.sf.cglib.beans.BeanMap");
/*     */ 
/*  28 */   private static final Type FIXED_KEY_SET = TypeUtils.parseType("com.comphenix.net.sf.cglib.beans.FixedKeySet");
/*     */ 
/*  30 */   private static final Signature CSTRUCT_OBJECT = TypeUtils.parseConstructor("Object");
/*     */ 
/*  32 */   private static final Signature CSTRUCT_STRING_ARRAY = TypeUtils.parseConstructor("String[]");
/*     */ 
/*  34 */   private static final Signature BEAN_MAP_GET = TypeUtils.parseSignature("Object get(Object, Object)");
/*     */ 
/*  36 */   private static final Signature BEAN_MAP_PUT = TypeUtils.parseSignature("Object put(Object, Object, Object)");
/*     */ 
/*  38 */   private static final Signature KEY_SET = TypeUtils.parseSignature("java.util.Set keySet()");
/*     */ 
/*  40 */   private static final Signature NEW_INSTANCE = new Signature("newInstance", BEAN_MAP, new Type[] { Constants.TYPE_OBJECT });
/*     */ 
/*  42 */   private static final Signature GET_PROPERTY_TYPE = TypeUtils.parseSignature("Class getPropertyType(String)");
/*     */ 
/*     */   public BeanMapEmitter(ClassVisitor v, String className, Class type, int require)
/*     */   {
/*  46 */     super(v);
/*     */ 
/*  48 */     begin_class(46, 1, className, BEAN_MAP, null, "<generated>");
/*  49 */     EmitUtils.null_constructor(this);
/*  50 */     EmitUtils.factory_method(this, NEW_INSTANCE);
/*  51 */     generateConstructor();
/*     */ 
/*  53 */     Map getters = makePropertyMap(ReflectUtils.getBeanGetters(type));
/*  54 */     Map setters = makePropertyMap(ReflectUtils.getBeanSetters(type));
/*  55 */     Map allProps = new HashMap();
/*  56 */     allProps.putAll(getters);
/*  57 */     allProps.putAll(setters);
/*     */     Iterator it;
/*  59 */     if (require != 0) {
/*  60 */       for (it = allProps.keySet().iterator(); it.hasNext(); ) {
/*  61 */         String name = (String)it.next();
/*  62 */         if ((((require & 0x1) != 0) && (!getters.containsKey(name))) || (((require & 0x2) != 0) && (!setters.containsKey(name))))
/*     */         {
/*  64 */           it.remove();
/*  65 */           getters.remove(name);
/*  66 */           setters.remove(name);
/*     */         }
/*     */       }
/*     */     }
/*  70 */     generateGet(type, getters);
/*  71 */     generatePut(type, setters);
/*     */ 
/*  73 */     String[] allNames = getNames(allProps);
/*  74 */     generateKeySet(allNames);
/*  75 */     generateGetPropertyType(allProps, allNames);
/*  76 */     end_class();
/*     */   }
/*     */ 
/*     */   private Map makePropertyMap(PropertyDescriptor[] props) {
/*  80 */     Map names = new HashMap();
/*  81 */     for (int i = 0; i < props.length; i++) {
/*  82 */       names.put(props[i].getName(), props[i]);
/*     */     }
/*  84 */     return names;
/*     */   }
/*     */ 
/*     */   private String[] getNames(Map propertyMap) {
/*  88 */     return (String[])propertyMap.keySet().toArray(new String[propertyMap.size()]);
/*     */   }
/*     */ 
/*     */   private void generateConstructor() {
/*  92 */     CodeEmitter e = begin_method(1, CSTRUCT_OBJECT, null);
/*  93 */     e.load_this();
/*  94 */     e.load_arg(0);
/*  95 */     e.super_invoke_constructor(CSTRUCT_OBJECT);
/*  96 */     e.return_value();
/*  97 */     e.end_method();
/*     */   }
/*     */ 
/*     */   private void generateGet(Class type, Map getters) {
/* 101 */     CodeEmitter e = begin_method(1, BEAN_MAP_GET, null);
/* 102 */     e.load_arg(0);
/* 103 */     e.checkcast(Type.getType(type));
/* 104 */     e.load_arg(1);
/* 105 */     e.checkcast(Constants.TYPE_STRING);
/* 106 */     EmitUtils.string_switch(e, getNames(getters), 1, new ObjectSwitchCallback() { private final Map val$getters;
/*     */       private final CodeEmitter val$e;
/*     */ 
/* 108 */       public void processCase(Object key, Label end) { PropertyDescriptor pd = (PropertyDescriptor)this.val$getters.get(key);
/* 109 */         MethodInfo method = ReflectUtils.getMethodInfo(pd.getReadMethod());
/* 110 */         this.val$e.invoke(method);
/* 111 */         this.val$e.box(method.getSignature().getReturnType());
/* 112 */         this.val$e.return_value(); }
/*     */ 
/*     */       public void processDefault() {
/* 115 */         this.val$e.aconst_null();
/* 116 */         this.val$e.return_value();
/*     */       }
/*     */     });
/* 119 */     e.end_method();
/*     */   }
/*     */ 
/*     */   private void generatePut(Class type, Map setters) {
/* 123 */     CodeEmitter e = begin_method(1, BEAN_MAP_PUT, null);
/* 124 */     e.load_arg(0);
/* 125 */     e.checkcast(Type.getType(type));
/* 126 */     e.load_arg(1);
/* 127 */     e.checkcast(Constants.TYPE_STRING);
/* 128 */     EmitUtils.string_switch(e, getNames(setters), 1, new ObjectSwitchCallback() { private final Map val$setters;
/*     */       private final CodeEmitter val$e;
/*     */ 
/* 130 */       public void processCase(Object key, Label end) { PropertyDescriptor pd = (PropertyDescriptor)this.val$setters.get(key);
/* 131 */         if (pd.getReadMethod() == null) {
/* 132 */           this.val$e.aconst_null();
/*     */         } else {
/* 134 */           MethodInfo read = ReflectUtils.getMethodInfo(pd.getReadMethod());
/* 135 */           this.val$e.dup();
/* 136 */           this.val$e.invoke(read);
/* 137 */           this.val$e.box(read.getSignature().getReturnType());
/*     */         }
/* 139 */         this.val$e.swap();
/* 140 */         this.val$e.load_arg(2);
/* 141 */         MethodInfo write = ReflectUtils.getMethodInfo(pd.getWriteMethod());
/* 142 */         this.val$e.unbox(write.getSignature().getArgumentTypes()[0]);
/* 143 */         this.val$e.invoke(write);
/* 144 */         this.val$e.return_value();
/*     */       }
/*     */ 
/*     */       public void processDefault()
/*     */       {
/*     */       }
/*     */     });
/* 150 */     e.aconst_null();
/* 151 */     e.return_value();
/* 152 */     e.end_method();
/*     */   }
/*     */ 
/*     */   private void generateKeySet(String[] allNames)
/*     */   {
/* 157 */     declare_field(10, "keys", FIXED_KEY_SET, null);
/*     */ 
/* 159 */     CodeEmitter e = begin_static();
/* 160 */     e.new_instance(FIXED_KEY_SET);
/* 161 */     e.dup();
/* 162 */     EmitUtils.push_array(e, allNames);
/* 163 */     e.invoke_constructor(FIXED_KEY_SET, CSTRUCT_STRING_ARRAY);
/* 164 */     e.putfield("keys");
/* 165 */     e.return_value();
/* 166 */     e.end_method();
/*     */ 
/* 169 */     e = begin_method(1, KEY_SET, null);
/* 170 */     e.load_this();
/* 171 */     e.getfield("keys");
/* 172 */     e.return_value();
/* 173 */     e.end_method();
/*     */   }
/*     */ 
/*     */   private void generateGetPropertyType(Map allProps, String[] allNames) {
/* 177 */     CodeEmitter e = begin_method(1, GET_PROPERTY_TYPE, null);
/* 178 */     e.load_arg(0);
/* 179 */     EmitUtils.string_switch(e, allNames, 1, new ObjectSwitchCallback() { private final Map val$allProps;
/*     */       private final CodeEmitter val$e;
/*     */ 
/* 181 */       public void processCase(Object key, Label end) { PropertyDescriptor pd = (PropertyDescriptor)this.val$allProps.get(key);
/* 182 */         EmitUtils.load_class(this.val$e, Type.getType(pd.getPropertyType()));
/* 183 */         this.val$e.return_value(); }
/*     */ 
/*     */       public void processDefault() {
/* 186 */         this.val$e.aconst_null();
/* 187 */         this.val$e.return_value();
/*     */       }
/*     */     });
/* 190 */     e.end_method();
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.net.sf.cglib.beans.BeanMapEmitter
 * JD-Core Version:    0.6.2
 */