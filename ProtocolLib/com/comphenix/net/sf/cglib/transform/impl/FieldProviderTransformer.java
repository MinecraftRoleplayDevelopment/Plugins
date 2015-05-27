/*     */ package com.comphenix.net.sf.cglib.transform.impl;
/*     */ 
/*     */ import com.comphenix.net.sf.cglib.asm.Label;
/*     */ import com.comphenix.net.sf.cglib.asm.Type;
/*     */ import com.comphenix.net.sf.cglib.core.CodeEmitter;
/*     */ import com.comphenix.net.sf.cglib.core.CodeGenerationException;
/*     */ import com.comphenix.net.sf.cglib.core.Constants;
/*     */ import com.comphenix.net.sf.cglib.core.EmitUtils;
/*     */ import com.comphenix.net.sf.cglib.core.ObjectSwitchCallback;
/*     */ import com.comphenix.net.sf.cglib.core.ProcessSwitchCallback;
/*     */ import com.comphenix.net.sf.cglib.core.Signature;
/*     */ import com.comphenix.net.sf.cglib.core.TypeUtils;
/*     */ import com.comphenix.net.sf.cglib.transform.ClassEmitterTransformer;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ 
/*     */ public class FieldProviderTransformer extends ClassEmitterTransformer
/*     */ {
/*     */   private static final String FIELD_NAMES = "CGLIB$FIELD_NAMES";
/*     */   private static final String FIELD_TYPES = "CGLIB$FIELD_TYPES";
/*  30 */   private static final Type FIELD_PROVIDER = TypeUtils.parseType("com.comphenix.net.sf.cglib.transform.impl.FieldProvider");
/*     */ 
/*  32 */   private static final Type ILLEGAL_ARGUMENT_EXCEPTION = TypeUtils.parseType("IllegalArgumentException");
/*     */ 
/*  34 */   private static final Signature PROVIDER_GET = TypeUtils.parseSignature("Object getField(String)");
/*     */ 
/*  36 */   private static final Signature PROVIDER_SET = TypeUtils.parseSignature("void setField(String, Object)");
/*     */ 
/*  38 */   private static final Signature PROVIDER_SET_BY_INDEX = TypeUtils.parseSignature("void setField(int, Object)");
/*     */ 
/*  40 */   private static final Signature PROVIDER_GET_BY_INDEX = TypeUtils.parseSignature("Object getField(int)");
/*     */ 
/*  42 */   private static final Signature PROVIDER_GET_TYPES = TypeUtils.parseSignature("Class[] getFieldTypes()");
/*     */ 
/*  44 */   private static final Signature PROVIDER_GET_NAMES = TypeUtils.parseSignature("String[] getFieldNames()");
/*     */   private int access;
/*     */   private Map fields;
/*     */ 
/*     */   public void begin_class(int version, int access, String className, Type superType, Type[] interfaces, String sourceFile)
/*     */   {
/*  51 */     if (!TypeUtils.isAbstract(access)) {
/*  52 */       interfaces = TypeUtils.add(interfaces, FIELD_PROVIDER);
/*     */     }
/*  54 */     this.access = access;
/*  55 */     this.fields = new HashMap();
/*  56 */     super.begin_class(version, access, className, superType, interfaces, sourceFile);
/*     */   }
/*     */ 
/*     */   public void declare_field(int access, String name, Type type, Object value) {
/*  60 */     super.declare_field(access, name, type, value);
/*     */ 
/*  62 */     if (!TypeUtils.isStatic(access))
/*  63 */       this.fields.put(name, type);
/*     */   }
/*     */ 
/*     */   public void end_class()
/*     */   {
/*  68 */     if (!TypeUtils.isInterface(this.access)) {
/*     */       try {
/*  70 */         generate();
/*     */       } catch (RuntimeException e) {
/*  72 */         throw e;
/*     */       } catch (Exception e) {
/*  74 */         throw new CodeGenerationException(e);
/*     */       }
/*     */     }
/*  77 */     super.end_class();
/*     */   }
/*     */ 
/*     */   private void generate() throws Exception {
/*  81 */     String[] names = (String[])this.fields.keySet().toArray(new String[this.fields.size()]);
/*     */ 
/*  83 */     int[] indexes = new int[names.length];
/*  84 */     for (int i = 0; i < indexes.length; i++) {
/*  85 */       indexes[i] = i;
/*     */     }
/*     */ 
/*  88 */     super.declare_field(26, "CGLIB$FIELD_NAMES", Constants.TYPE_STRING_ARRAY, null);
/*  89 */     super.declare_field(26, "CGLIB$FIELD_TYPES", Constants.TYPE_CLASS_ARRAY, null);
/*     */ 
/*  92 */     initFieldProvider(names);
/*  93 */     getNames();
/*  94 */     getTypes();
/*  95 */     getField(names);
/*  96 */     setField(names);
/*  97 */     setByIndex(names, indexes);
/*  98 */     getByIndex(names, indexes);
/*     */   }
/*     */ 
/*     */   private void initFieldProvider(String[] names) {
/* 102 */     CodeEmitter e = getStaticHook();
/* 103 */     EmitUtils.push_object(e, names);
/* 104 */     e.putstatic(getClassType(), "CGLIB$FIELD_NAMES", Constants.TYPE_STRING_ARRAY);
/*     */ 
/* 106 */     e.push(names.length);
/* 107 */     e.newarray(Constants.TYPE_CLASS);
/* 108 */     e.dup();
/* 109 */     for (int i = 0; i < names.length; i++) {
/* 110 */       e.dup();
/* 111 */       e.push(i);
/* 112 */       Type type = (Type)this.fields.get(names[i]);
/* 113 */       EmitUtils.load_class(e, type);
/* 114 */       e.aastore();
/*     */     }
/* 116 */     e.putstatic(getClassType(), "CGLIB$FIELD_TYPES", Constants.TYPE_CLASS_ARRAY);
/*     */   }
/*     */ 
/*     */   private void getNames() {
/* 120 */     CodeEmitter e = super.begin_method(1, PROVIDER_GET_NAMES, null);
/* 121 */     e.getstatic(getClassType(), "CGLIB$FIELD_NAMES", Constants.TYPE_STRING_ARRAY);
/* 122 */     e.return_value();
/* 123 */     e.end_method();
/*     */   }
/*     */ 
/*     */   private void getTypes() {
/* 127 */     CodeEmitter e = super.begin_method(1, PROVIDER_GET_TYPES, null);
/* 128 */     e.getstatic(getClassType(), "CGLIB$FIELD_TYPES", Constants.TYPE_CLASS_ARRAY);
/* 129 */     e.return_value();
/* 130 */     e.end_method();
/*     */   }
/*     */ 
/*     */   private void setByIndex(String[] names, int[] indexes) throws Exception {
/* 134 */     CodeEmitter e = super.begin_method(1, PROVIDER_SET_BY_INDEX, null);
/* 135 */     e.load_this();
/* 136 */     e.load_arg(1);
/* 137 */     e.load_arg(0);
/* 138 */     e.process_switch(indexes, new ProcessSwitchCallback() { private final String[] val$names;
/*     */       private final CodeEmitter val$e;
/*     */ 
/* 140 */       public void processCase(int key, Label end) throws Exception { Type type = (Type)FieldProviderTransformer.this.fields.get(this.val$names[key]);
/* 141 */         this.val$e.unbox(type);
/* 142 */         this.val$e.putfield(this.val$names[key]);
/* 143 */         this.val$e.return_value(); }
/*     */ 
/*     */       public void processDefault() throws Exception {
/* 146 */         this.val$e.throw_exception(FieldProviderTransformer.ILLEGAL_ARGUMENT_EXCEPTION, "Unknown field index");
/*     */       }
/*     */     });
/* 149 */     e.end_method();
/*     */   }
/*     */ 
/*     */   private void getByIndex(String[] names, int[] indexes) throws Exception {
/* 153 */     CodeEmitter e = super.begin_method(1, PROVIDER_GET_BY_INDEX, null);
/* 154 */     e.load_this();
/* 155 */     e.load_arg(0);
/* 156 */     e.process_switch(indexes, new ProcessSwitchCallback() { private final String[] val$names;
/*     */       private final CodeEmitter val$e;
/*     */ 
/* 158 */       public void processCase(int key, Label end) throws Exception { Type type = (Type)FieldProviderTransformer.this.fields.get(this.val$names[key]);
/* 159 */         this.val$e.getfield(this.val$names[key]);
/* 160 */         this.val$e.box(type);
/* 161 */         this.val$e.return_value(); }
/*     */ 
/*     */       public void processDefault() throws Exception {
/* 164 */         this.val$e.throw_exception(FieldProviderTransformer.ILLEGAL_ARGUMENT_EXCEPTION, "Unknown field index");
/*     */       }
/*     */     });
/* 167 */     e.end_method();
/*     */   }
/*     */ 
/*     */   private void getField(String[] names)
/*     */     throws Exception
/*     */   {
/* 173 */     CodeEmitter e = begin_method(1, PROVIDER_GET, null);
/* 174 */     e.load_this();
/* 175 */     e.load_arg(0);
/* 176 */     EmitUtils.string_switch(e, names, 1, new ObjectSwitchCallback() { private final CodeEmitter val$e;
/*     */ 
/* 178 */       public void processCase(Object key, Label end) { Type type = (Type)FieldProviderTransformer.this.fields.get(key);
/* 179 */         this.val$e.getfield((String)key);
/* 180 */         this.val$e.box(type);
/* 181 */         this.val$e.return_value(); }
/*     */ 
/*     */       public void processDefault() {
/* 184 */         this.val$e.throw_exception(FieldProviderTransformer.ILLEGAL_ARGUMENT_EXCEPTION, "Unknown field name");
/*     */       }
/*     */     });
/* 187 */     e.end_method();
/*     */   }
/*     */ 
/*     */   private void setField(String[] names) throws Exception {
/* 191 */     CodeEmitter e = begin_method(1, PROVIDER_SET, null);
/* 192 */     e.load_this();
/* 193 */     e.load_arg(1);
/* 194 */     e.load_arg(0);
/* 195 */     EmitUtils.string_switch(e, names, 1, new ObjectSwitchCallback() { private final CodeEmitter val$e;
/*     */ 
/* 197 */       public void processCase(Object key, Label end) { Type type = (Type)FieldProviderTransformer.this.fields.get(key);
/* 198 */         this.val$e.unbox(type);
/* 199 */         this.val$e.putfield((String)key);
/* 200 */         this.val$e.return_value(); }
/*     */ 
/*     */       public void processDefault() {
/* 203 */         this.val$e.throw_exception(FieldProviderTransformer.ILLEGAL_ARGUMENT_EXCEPTION, "Unknown field name");
/*     */       }
/*     */     });
/* 206 */     e.end_method();
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.net.sf.cglib.transform.impl.FieldProviderTransformer
 * JD-Core Version:    0.6.2
 */