/*     */ package com.comphenix.net.sf.cglib.transform;
/*     */ 
/*     */ import com.comphenix.net.sf.cglib.asm.Attribute;
/*     */ import com.comphenix.net.sf.cglib.asm.ClassReader;
/*     */ import com.comphenix.net.sf.cglib.asm.ClassWriter;
/*     */ import com.comphenix.net.sf.cglib.core.ClassNameReader;
/*     */ import com.comphenix.net.sf.cglib.core.DebuggingClassWriter;
/*     */ import java.io.BufferedInputStream;
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.DataInputStream;
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.FileNotFoundException;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.net.MalformedURLException;
/*     */ import java.util.zip.CRC32;
/*     */ import java.util.zip.ZipEntry;
/*     */ import java.util.zip.ZipInputStream;
/*     */ import java.util.zip.ZipOutputStream;
/*     */ 
/*     */ public abstract class AbstractTransformTask extends AbstractProcessTask
/*     */ {
/*     */   private static final int ZIP_MAGIC = 1347093252;
/*     */   private static final int CLASS_MAGIC = -889275714;
/*     */   private boolean verbose;
/*     */ 
/*     */   public void setVerbose(boolean verbose)
/*     */   {
/*  39 */     this.verbose = verbose;
/*     */   }
/*     */ 
/*     */   protected abstract ClassTransformer getClassTransformer(String[] paramArrayOfString);
/*     */ 
/*     */   protected Attribute[] attributes()
/*     */   {
/*  54 */     return null;
/*     */   }
/*     */ 
/*     */   protected void processFile(File file) throws Exception
/*     */   {
/*  59 */     if (isClassFile(file))
/*     */     {
/*  61 */       processClassFile(file);
/*     */     }
/*  63 */     else if (isJarFile(file))
/*     */     {
/*  65 */       processJarFile(file);
/*     */     }
/*     */     else
/*     */     {
/*  69 */       log("ignoring " + file.toURL(), 1);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void processClassFile(File file)
/*     */     throws Exception, FileNotFoundException, IOException, MalformedURLException
/*     */   {
/*  84 */     ClassReader reader = getClassReader(file);
/*  85 */     String[] name = ClassNameReader.getClassInfo(reader);
/*  86 */     ClassWriter w = new DebuggingClassWriter(1);
/*  87 */     ClassTransformer t = getClassTransformer(name);
/*  88 */     if (t != null)
/*     */     {
/*  90 */       if (this.verbose) {
/*  91 */         log("processing " + file.toURL());
/*     */       }
/*  93 */       new TransformingClassGenerator(new ClassReaderGenerator(getClassReader(file), attributes(), getFlags()), t).generateClass(w);
/*     */ 
/*  96 */       FileOutputStream fos = new FileOutputStream(file);
/*     */       try {
/*  98 */         fos.write(w.toByteArray());
/*     */       } finally {
/* 100 */         fos.close();
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   protected int getFlags()
/*     */   {
/* 108 */     return 0;
/*     */   }
/*     */ 
/*     */   private static ClassReader getClassReader(File file) throws Exception {
/* 112 */     InputStream in = new BufferedInputStream(new FileInputStream(file));
/*     */     try {
/* 114 */       ClassReader r = new ClassReader(in);
/* 115 */       return r;
/*     */     } finally {
/* 117 */       in.close();
/*     */     }
/*     */   }
/*     */ 
/*     */   protected boolean isClassFile(File file)
/*     */     throws IOException
/*     */   {
/* 124 */     return checkMagic(file, -889275714L);
/*     */   }
/*     */ 
/*     */   protected void processJarFile(File file)
/*     */     throws Exception
/*     */   {
/* 130 */     if (this.verbose) {
/* 131 */       log("processing " + file.toURL());
/*     */     }
/*     */ 
/* 134 */     File tempFile = File.createTempFile(file.getName(), null, new File(file.getAbsoluteFile().getParent()));
/*     */     try
/*     */     {
/* 138 */       ZipInputStream zip = new ZipInputStream(new FileInputStream(file));
/*     */       try { FileOutputStream fout = new FileOutputStream(tempFile);
/*     */         ZipOutputStream out;
/*     */         try { out = new ZipOutputStream(fout);
/*     */           ZipEntry entry;
/* 145 */           while ((entry = zip.getNextEntry()) != null)
/*     */           {
/* 148 */             byte[] bytes = getBytes(zip);
/*     */ 
/* 150 */             if (!entry.isDirectory())
/*     */             {
/* 152 */               DataInputStream din = new DataInputStream(new ByteArrayInputStream(bytes));
/*     */ 
/* 156 */               if (din.readInt() == -889275714)
/*     */               {
/* 158 */                 bytes = process(bytes);
/*     */               }
/* 161 */               else if (this.verbose) {
/* 162 */                 log("ignoring " + entry.toString());
/*     */               }
/*     */ 
/*     */             }
/*     */ 
/* 167 */             ZipEntry outEntry = new ZipEntry(entry.getName());
/* 168 */             outEntry.setMethod(entry.getMethod());
/* 169 */             outEntry.setComment(entry.getComment());
/* 170 */             outEntry.setSize(bytes.length);
/*     */ 
/* 173 */             if (outEntry.getMethod() == 0) {
/* 174 */               CRC32 crc = new CRC32();
/* 175 */               crc.update(bytes);
/* 176 */               outEntry.setCrc(crc.getValue());
/* 177 */               outEntry.setCompressedSize(bytes.length);
/*     */             }
/* 179 */             out.putNextEntry(outEntry);
/* 180 */             out.write(bytes);
/* 181 */             out.closeEntry();
/* 182 */             zip.closeEntry();
/*     */           }
/*     */         } finally
/*     */         {
/*     */         }
/*     */       }
/*     */       finally
/*     */       {
/* 190 */         zip.close();
/*     */       }
/*     */ 
/* 194 */       if (file.delete())
/*     */       {
/* 196 */         File newFile = new File(tempFile.getAbsolutePath());
/*     */ 
/* 198 */         if (!newFile.renameTo(file))
/* 199 */           throw new IOException("can not rename " + tempFile + " to " + file);
/*     */       }
/*     */       else
/*     */       {
/* 203 */         throw new IOException("can not delete " + file);
/*     */       }
/*     */     }
/*     */     finally
/*     */     {
/* 208 */       tempFile.delete();
/*     */     }
/*     */   }
/*     */ 
/*     */   private byte[] process(byte[] bytes)
/*     */     throws Exception
/*     */   {
/* 222 */     ClassReader reader = new ClassReader(new ByteArrayInputStream(bytes));
/* 223 */     String[] name = ClassNameReader.getClassInfo(reader);
/* 224 */     ClassWriter w = new DebuggingClassWriter(1);
/* 225 */     ClassTransformer t = getClassTransformer(name);
/* 226 */     if (t != null) {
/* 227 */       if (this.verbose) {
/* 228 */         log("processing " + name[0]);
/*     */       }
/* 230 */       new TransformingClassGenerator(new ClassReaderGenerator(new ClassReader(new ByteArrayInputStream(bytes)), attributes(), getFlags()), t).generateClass(w);
/*     */ 
/* 233 */       ByteArrayOutputStream out = new ByteArrayOutputStream();
/* 234 */       out.write(w.toByteArray());
/* 235 */       return out.toByteArray();
/*     */     }
/* 237 */     return bytes;
/*     */   }
/*     */ 
/*     */   private byte[] getBytes(ZipInputStream zip)
/*     */     throws IOException
/*     */   {
/* 247 */     ByteArrayOutputStream bout = new ByteArrayOutputStream();
/* 248 */     InputStream in = new BufferedInputStream(zip);
/*     */     int b;
/* 250 */     while ((b = in.read()) != -1) {
/* 251 */       bout.write(b);
/*     */     }
/* 253 */     return bout.toByteArray();
/*     */   }
/*     */ 
/*     */   private boolean checkMagic(File file, long magic) throws IOException {
/* 257 */     DataInputStream in = new DataInputStream(new FileInputStream(file));
/*     */     try {
/* 259 */       int m = in.readInt();
/* 260 */       return magic == m;
/*     */     } finally {
/* 262 */       in.close();
/*     */     }
/*     */   }
/*     */ 
/*     */   protected boolean isJarFile(File file) throws IOException {
/* 267 */     return checkMagic(file, 1347093252L);
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.net.sf.cglib.transform.AbstractTransformTask
 * JD-Core Version:    0.6.2
 */