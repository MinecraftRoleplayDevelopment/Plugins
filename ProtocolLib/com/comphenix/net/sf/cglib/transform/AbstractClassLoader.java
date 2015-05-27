/*     */ package com.comphenix.net.sf.cglib.transform;
/*     */ 
/*     */ import com.comphenix.net.sf.cglib.asm.Attribute;
/*     */ import com.comphenix.net.sf.cglib.asm.ClassReader;
/*     */ import com.comphenix.net.sf.cglib.asm.ClassWriter;
/*     */ import com.comphenix.net.sf.cglib.core.ClassGenerator;
/*     */ import com.comphenix.net.sf.cglib.core.CodeGenerationException;
/*     */ import com.comphenix.net.sf.cglib.core.DebuggingClassWriter;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedAction;
/*     */ import java.security.ProtectionDomain;
/*     */ 
/*     */ public abstract class AbstractClassLoader extends ClassLoader
/*     */ {
/*     */   private ClassFilter filter;
/*     */   private ClassLoader classPath;
/*  35 */   private static ProtectionDomain DOMAIN = (ProtectionDomain)AccessController.doPrivileged(new PrivilegedAction()
/*     */   {
/*     */     public Object run()
/*     */     {
/*  39 */       return AbstractClassLoader.class.getProtectionDomain();
/*     */     }
/*     */   });
/*     */ 
/*     */   protected AbstractClassLoader(ClassLoader parent, ClassLoader classPath, ClassFilter filter)
/*     */   {
/*  45 */     super(parent);
/*  46 */     this.filter = filter;
/*  47 */     this.classPath = classPath;
/*     */   }
/*     */ 
/*     */   public Class loadClass(String name) throws ClassNotFoundException
/*     */   {
/*  52 */     Class loaded = findLoadedClass(name);
/*     */ 
/*  54 */     if ((loaded != null) && 
/*  55 */       (loaded.getClassLoader() == this)) {
/*  56 */       return loaded;
/*     */     }
/*     */ 
/*  60 */     if (!this.filter.accept(name)) {
/*  61 */       return super.loadClass(name);
/*     */     }
/*     */     ClassReader r;
/*     */     try
/*     */     {
/*  66 */       InputStream is = this.classPath.getResourceAsStream(name.replace('.', '/') + ".class");
/*     */ 
/*  70 */       if (is == null)
/*     */       {
/*  72 */         throw new ClassNotFoundException(name);
/*     */       }
/*     */ 
/*     */       try
/*     */       {
/*  77 */         r = new ClassReader(is);
/*     */       }
/*     */       finally
/*     */       {
/*  81 */         is.close();
/*     */       }
/*     */     }
/*     */     catch (IOException e) {
/*  85 */       throw new ClassNotFoundException(name + ":" + e.getMessage());
/*     */     }
/*     */     try
/*     */     {
/*  89 */       ClassWriter w = new DebuggingClassWriter(1);
/*  90 */       getGenerator(r).generateClass(w);
/*  91 */       byte[] b = w.toByteArray();
/*  92 */       Class c = super.defineClass(name, b, 0, b.length, DOMAIN);
/*  93 */       postProcess(c);
/*  94 */       return c;
/*     */     } catch (RuntimeException e) {
/*  96 */       throw e;
/*     */     } catch (Error e) {
/*  98 */       throw e;
/*     */     } catch (Exception e) {
/* 100 */       throw new CodeGenerationException(e);
/*     */     }
/*     */   }
/*     */ 
/*     */   protected ClassGenerator getGenerator(ClassReader r) {
/* 105 */     return new ClassReaderGenerator(r, attributes(), getFlags());
/*     */   }
/*     */ 
/*     */   protected int getFlags() {
/* 109 */     return 0;
/*     */   }
/*     */ 
/*     */   protected Attribute[] attributes() {
/* 113 */     return null;
/*     */   }
/*     */ 
/*     */   protected void postProcess(Class c)
/*     */   {
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.net.sf.cglib.transform.AbstractClassLoader
 * JD-Core Version:    0.6.2
 */