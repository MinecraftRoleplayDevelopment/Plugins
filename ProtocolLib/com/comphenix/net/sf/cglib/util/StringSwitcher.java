/*     */ package com.comphenix.net.sf.cglib.util;
/*     */ 
/*     */ import com.comphenix.net.sf.cglib.asm.ClassVisitor;
/*     */ import com.comphenix.net.sf.cglib.asm.Label;
/*     */ import com.comphenix.net.sf.cglib.asm.Type;
/*     */ import com.comphenix.net.sf.cglib.core.AbstractClassGenerator;
/*     */ import com.comphenix.net.sf.cglib.core.AbstractClassGenerator.Source;
/*     */ import com.comphenix.net.sf.cglib.core.ClassEmitter;
/*     */ import com.comphenix.net.sf.cglib.core.CodeEmitter;
/*     */ import com.comphenix.net.sf.cglib.core.EmitUtils;
/*     */ import com.comphenix.net.sf.cglib.core.KeyFactory;
/*     */ import com.comphenix.net.sf.cglib.core.ObjectSwitchCallback;
/*     */ import com.comphenix.net.sf.cglib.core.ReflectUtils;
/*     */ import com.comphenix.net.sf.cglib.core.Signature;
/*     */ import com.comphenix.net.sf.cglib.core.TypeUtils;
/*     */ import java.util.Arrays;
/*     */ import java.util.List;
/*     */ 
/*     */ public abstract class StringSwitcher
/*     */ {
/*  28 */   private static final Type STRING_SWITCHER = TypeUtils.parseType("com.comphenix.net.sf.cglib.util.StringSwitcher");
/*     */ 
/*  30 */   private static final Signature INT_VALUE = TypeUtils.parseSignature("int intValue(String)");
/*     */ 
/*  32 */   private static final StringSwitcherKey KEY_FACTORY = (StringSwitcherKey)KeyFactory.create(StringSwitcherKey.class);
/*     */ 
/*     */   public static StringSwitcher create(String[] strings, int[] ints, boolean fixedInput)
/*     */   {
/*  49 */     Generator gen = new Generator();
/*  50 */     gen.setStrings(strings);
/*  51 */     gen.setInts(ints);
/*  52 */     gen.setFixedInput(fixedInput);
/*  53 */     return gen.create();
/*     */   }
/*     */ 
/*     */   public abstract int intValue(String paramString);
/*     */ 
/*     */   public static class Generator extends AbstractClassGenerator
/*     */   {
/*  69 */     private static final AbstractClassGenerator.Source SOURCE = new AbstractClassGenerator.Source(StringSwitcher.class.getName());
/*     */     private String[] strings;
/*     */     private int[] ints;
/*     */     private boolean fixedInput;
/*     */ 
/*     */     public Generator()
/*     */     {
/*  76 */       super();
/*     */     }
/*     */ 
/*     */     public void setStrings(String[] strings)
/*     */     {
/*  85 */       this.strings = strings;
/*     */     }
/*     */ 
/*     */     public void setInts(int[] ints)
/*     */     {
/*  94 */       this.ints = ints;
/*     */     }
/*     */ 
/*     */     public void setFixedInput(boolean fixedInput)
/*     */     {
/* 103 */       this.fixedInput = fixedInput;
/*     */     }
/*     */ 
/*     */     protected ClassLoader getDefaultClassLoader() {
/* 107 */       return getClass().getClassLoader();
/*     */     }
/*     */ 
/*     */     public StringSwitcher create()
/*     */     {
/* 114 */       setNamePrefix(StringSwitcher.class.getName());
/* 115 */       Object key = StringSwitcher.KEY_FACTORY.newInstance(this.strings, this.ints, this.fixedInput);
/* 116 */       return (StringSwitcher)super.create(key);
/*     */     }
/*     */ 
/*     */     public void generateClass(ClassVisitor v) throws Exception {
/* 120 */       ClassEmitter ce = new ClassEmitter(v);
/* 121 */       ce.begin_class(46, 1, getClassName(), StringSwitcher.STRING_SWITCHER, null, "<generated>");
/*     */ 
/* 127 */       EmitUtils.null_constructor(ce);
/* 128 */       CodeEmitter e = ce.begin_method(1, StringSwitcher.INT_VALUE, null);
/* 129 */       e.load_arg(0);
/* 130 */       List stringList = Arrays.asList(this.strings);
/* 131 */       int style = this.fixedInput ? 2 : 1;
/* 132 */       EmitUtils.string_switch(e, this.strings, style, new ObjectSwitchCallback() { private final CodeEmitter val$e;
/*     */         private final List val$stringList;
/*     */ 
/* 134 */         public void processCase(Object key, Label end) { this.val$e.push(StringSwitcher.Generator.this.ints[this.val$stringList.indexOf(key)]);
/* 135 */           this.val$e.return_value(); }
/*     */ 
/*     */         public void processDefault() {
/* 138 */           this.val$e.push(-1);
/* 139 */           this.val$e.return_value();
/*     */         }
/*     */       });
/* 142 */       e.end_method();
/* 143 */       ce.end_class();
/*     */     }
/*     */ 
/*     */     protected Object firstInstance(Class type) {
/* 147 */       return (StringSwitcher)ReflectUtils.newInstance(type);
/*     */     }
/*     */ 
/*     */     protected Object nextInstance(Object instance) {
/* 151 */       return instance;
/*     */     }
/*     */   }
/*     */ 
/*     */   static abstract interface StringSwitcherKey
/*     */   {
/*     */     public abstract Object newInstance(String[] paramArrayOfString, int[] paramArrayOfInt, boolean paramBoolean);
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.net.sf.cglib.util.StringSwitcher
 * JD-Core Version:    0.6.2
 */