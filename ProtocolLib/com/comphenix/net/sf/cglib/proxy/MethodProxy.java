/*     */ package com.comphenix.net.sf.cglib.proxy;
/*     */ 
/*     */ import com.comphenix.net.sf.cglib.core.AbstractClassGenerator;
/*     */ import com.comphenix.net.sf.cglib.core.CodeGenerationException;
/*     */ import com.comphenix.net.sf.cglib.core.GeneratorStrategy;
/*     */ import com.comphenix.net.sf.cglib.core.NamingPolicy;
/*     */ import com.comphenix.net.sf.cglib.core.Signature;
/*     */ import com.comphenix.net.sf.cglib.reflect.FastClass;
/*     */ import com.comphenix.net.sf.cglib.reflect.FastClass.Generator;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.lang.reflect.Method;
/*     */ 
/*     */ public class MethodProxy
/*     */ {
/*     */   private Signature sig1;
/*     */   private Signature sig2;
/*     */   private CreateInfo createInfo;
/*  40 */   private final Object initLock = new Object();
/*     */   private volatile FastClassInfo fastClassInfo;
/*     */ 
/*     */   public static MethodProxy create(Class c1, Class c2, String desc, String name1, String name2)
/*     */   {
/*  48 */     MethodProxy proxy = new MethodProxy();
/*  49 */     proxy.sig1 = new Signature(name1, desc);
/*  50 */     proxy.sig2 = new Signature(name2, desc);
/*  51 */     proxy.createInfo = new CreateInfo(c1, c2);
/*  52 */     return proxy;
/*     */   }
/*     */ 
/*     */   private void init()
/*     */   {
/*  65 */     if (this.fastClassInfo == null)
/*     */     {
/*  67 */       synchronized (this.initLock)
/*     */       {
/*  69 */         if (this.fastClassInfo == null)
/*     */         {
/*  71 */           CreateInfo ci = this.createInfo;
/*     */ 
/*  73 */           FastClassInfo fci = new FastClassInfo(null);
/*  74 */           fci.f1 = helper(ci, ci.c1);
/*  75 */           fci.f2 = helper(ci, ci.c2);
/*  76 */           fci.i1 = fci.f1.getIndex(this.sig1);
/*  77 */           fci.i2 = fci.f2.getIndex(this.sig2);
/*  78 */           this.fastClassInfo = fci;
/*  79 */           this.createInfo = null;
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private static FastClass helper(CreateInfo ci, Class type)
/*     */   {
/* 115 */     FastClass.Generator g = new FastClass.Generator();
/* 116 */     g.setType(type);
/* 117 */     g.setClassLoader(ci.c2.getClassLoader());
/* 118 */     g.setNamingPolicy(ci.namingPolicy);
/* 119 */     g.setStrategy(ci.strategy);
/* 120 */     g.setAttemptLoad(ci.attemptLoad);
/* 121 */     return g.create();
/*     */   }
/*     */ 
/*     */   public Signature getSignature()
/*     */   {
/* 131 */     return this.sig1;
/*     */   }
/*     */ 
/*     */   public String getSuperName()
/*     */   {
/* 141 */     return this.sig2.getName();
/*     */   }
/*     */ 
/*     */   public int getSuperIndex()
/*     */   {
/* 152 */     init();
/* 153 */     return this.fastClassInfo.i2;
/*     */   }
/*     */ 
/*     */   FastClass getFastClass()
/*     */   {
/* 158 */     init();
/* 159 */     return this.fastClassInfo.f1;
/*     */   }
/*     */ 
/*     */   FastClass getSuperFastClass()
/*     */   {
/* 164 */     init();
/* 165 */     return this.fastClassInfo.f2;
/*     */   }
/*     */ 
/*     */   public static MethodProxy find(Class type, Signature sig)
/*     */   {
/*     */     try
/*     */     {
/* 178 */       Method m = type.getDeclaredMethod("CGLIB$findMethodProxy", MethodInterceptorGenerator.FIND_PROXY_TYPES);
/*     */ 
/* 180 */       return (MethodProxy)m.invoke(null, new Object[] { sig });
/*     */     } catch (NoSuchMethodException e) {
/* 182 */       throw new IllegalArgumentException("Class " + type + " does not use a MethodInterceptor");
/*     */     } catch (IllegalAccessException e) {
/* 184 */       throw new CodeGenerationException(e);
/*     */     } catch (InvocationTargetException e) {
/* 186 */       throw new CodeGenerationException(e);
/*     */     }
/*     */   }
/*     */ 
/*     */   public Object invoke(Object obj, Object[] args)
/*     */     throws Throwable
/*     */   {
/*     */     try
/*     */     {
/* 202 */       init();
/* 203 */       FastClassInfo fci = this.fastClassInfo;
/* 204 */       return fci.f1.invoke(fci.i1, obj, args);
/*     */     } catch (InvocationTargetException e) {
/* 206 */       throw e.getTargetException();
/*     */     } catch (IllegalArgumentException e) {
/* 208 */       if (this.fastClassInfo.i1 < 0)
/* 209 */         throw new IllegalArgumentException("Protected method: " + this.sig1);
/* 210 */       throw e;
/*     */     }
/*     */   }
/*     */ 
/*     */   public Object invokeSuper(Object obj, Object[] args)
/*     */     throws Throwable
/*     */   {
/*     */     try
/*     */     {
/* 226 */       init();
/* 227 */       FastClassInfo fci = this.fastClassInfo;
/* 228 */       return fci.f2.invoke(fci.i2, obj, args);
/*     */     } catch (InvocationTargetException e) {
/* 230 */       throw e.getTargetException();
/*     */     }
/*     */   }
/*     */ 
/*     */   private static class CreateInfo
/*     */   {
/*     */     Class c1;
/*     */     Class c2;
/*     */     NamingPolicy namingPolicy;
/*     */     GeneratorStrategy strategy;
/*     */     boolean attemptLoad;
/*     */ 
/*     */     public CreateInfo(Class c1, Class c2)
/*     */     {
/* 103 */       this.c1 = c1;
/* 104 */       this.c2 = c2;
/* 105 */       AbstractClassGenerator fromEnhancer = AbstractClassGenerator.getCurrent();
/* 106 */       if (fromEnhancer != null) {
/* 107 */         this.namingPolicy = fromEnhancer.getNamingPolicy();
/* 108 */         this.strategy = fromEnhancer.getStrategy();
/* 109 */         this.attemptLoad = fromEnhancer.getAttemptLoad();
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private static class FastClassInfo
/*     */   {
/*     */     FastClass f1;
/*     */     FastClass f2;
/*     */     int i1;
/*     */     int i2;
/*     */ 
/*     */     private FastClassInfo()
/*     */     {
/*     */     }
/*     */ 
/*     */     FastClassInfo(MethodProxy.1 x0)
/*     */     {
/*  85 */       this();
/*     */     }
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.net.sf.cglib.proxy.MethodProxy
 * JD-Core Version:    0.6.2
 */