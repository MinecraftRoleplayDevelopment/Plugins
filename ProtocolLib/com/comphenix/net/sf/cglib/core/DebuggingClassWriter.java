/*     */ package com.comphenix.net.sf.cglib.core;
/*     */ 
/*     */ import com.comphenix.net.sf.cglib.asm.ClassReader;
/*     */ import com.comphenix.net.sf.cglib.asm.ClassWriter;
/*     */ import com.comphenix.net.sf.cglib.asm.util.TraceClassVisitor;
/*     */ import java.io.BufferedOutputStream;
/*     */ import java.io.File;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStream;
/*     */ import java.io.OutputStreamWriter;
/*     */ import java.io.PrintStream;
/*     */ import java.io.PrintWriter;
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedAction;
/*     */ 
/*     */ public class DebuggingClassWriter extends ClassWriter
/*     */ {
/*     */   public static final String DEBUG_LOCATION_PROPERTY = "cglib.debugLocation";
/*  35 */   private static String debugLocation = System.getProperty("cglib.debugLocation");
/*     */   private static boolean traceEnabled;
/*     */   private String className;
/*     */   private String superName;
/*     */ 
/*     */   public DebuggingClassWriter(int flags)
/*     */   {
/*  47 */     super(flags);
/*     */   }
/*     */ 
/*     */   public void visit(int version, int access, String name, String signature, String superName, String[] interfaces)
/*     */   {
/*  56 */     this.className = name.replace('/', '.');
/*  57 */     this.superName = superName.replace('/', '.');
/*  58 */     super.visit(version, access, name, signature, superName, interfaces);
/*     */   }
/*     */ 
/*     */   public String getClassName() {
/*  62 */     return this.className;
/*     */   }
/*     */ 
/*     */   public String getSuperName() {
/*  66 */     return this.superName;
/*     */   }
/*     */ 
/*     */   public byte[] toByteArray()
/*     */   {
/*  71 */     return (byte[])AccessController.doPrivileged(new PrivilegedAction()
/*     */     {
/*     */       public Object run()
/*     */       {
/*  76 */         byte[] b = DebuggingClassWriter.this.toByteArray();
/*  77 */         if (DebuggingClassWriter.debugLocation != null) {
/*  78 */           String dirs = DebuggingClassWriter.this.className.replace('.', File.separatorChar);
/*     */           try {
/*  80 */             new File(DebuggingClassWriter.debugLocation + File.separatorChar + dirs).getParentFile().mkdirs();
/*     */ 
/*  82 */             File file = new File(new File(DebuggingClassWriter.debugLocation), dirs + ".class");
/*  83 */             OutputStream out = new BufferedOutputStream(new FileOutputStream(file));
/*     */             try {
/*  85 */               out.write(b);
/*     */             } finally {
/*  87 */               out.close();
/*     */             }
/*     */ 
/*  90 */             if (DebuggingClassWriter.traceEnabled) {
/*  91 */               file = new File(new File(DebuggingClassWriter.debugLocation), dirs + ".asm");
/*  92 */               out = new BufferedOutputStream(new FileOutputStream(file));
/*     */               try {
/*  94 */                 ClassReader cr = new ClassReader(b);
/*  95 */                 PrintWriter pw = new PrintWriter(new OutputStreamWriter(out));
/*  96 */                 TraceClassVisitor tcv = new TraceClassVisitor(null, pw);
/*  97 */                 cr.accept(tcv, 0);
/*  98 */                 pw.flush();
/*     */               } finally {
/* 100 */                 out.close();
/*     */               }
/*     */             }
/*     */           } catch (IOException e) {
/* 104 */             throw new CodeGenerationException(e);
/*     */           }
/*     */         }
/* 107 */         return b;
/*     */       }
/*     */     });
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  36 */     if (debugLocation != null) {
/*  37 */       System.err.println("CGLIB debugging enabled, writing to '" + debugLocation + "'");
/*     */       try {
/*  39 */         Class.forName("com.comphenix.net.sf.cglib.asm.util.TraceClassVisitor");
/*  40 */         traceEnabled = true;
/*     */       }
/*     */       catch (Throwable ignore)
/*     */       {
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.net.sf.cglib.core.DebuggingClassWriter
 * JD-Core Version:    0.6.2
 */