/*     */ package com.comphenix.protocol.utility;
/*     */ 
/*     */ import com.comphenix.protocol.reflect.FieldUtils;
/*     */ import com.comphenix.protocol.reflect.MethodUtils;
/*     */ import java.lang.reflect.Method;
/*     */ import org.bukkit.Bukkit;
/*     */ import org.bukkit.Server;
/*     */ 
/*     */ class RemappedClassSource extends ClassSource
/*     */ {
/*     */   private Object classRemapper;
/*     */   private Method mapType;
/*     */   private ClassLoader loader;
/*     */ 
/*     */   public RemappedClassSource()
/*     */   {
/*  41 */     this(RemappedClassSource.class.getClassLoader());
/*     */   }
/*     */ 
/*     */   public RemappedClassSource(ClassLoader loader)
/*     */   {
/*  49 */     this.loader = loader;
/*     */   }
/*     */ 
/*     */   public RemappedClassSource initialize()
/*     */   {
/*     */     try
/*     */     {
/*  59 */       if ((Bukkit.getServer() == null) || (!Bukkit.getServer().getVersion().contains("MCPC-Plus"))) {
/*  60 */         throw new RemapperUnavaibleException(RemappedClassSource.RemapperUnavaibleException.Reason.MCPC_NOT_PRESENT);
/*     */       }
/*     */ 
/*  64 */       this.classRemapper = FieldUtils.readField(getClass().getClassLoader(), "remapper", true);
/*     */ 
/*  66 */       if (this.classRemapper == null) {
/*  67 */         throw new RemapperUnavaibleException(RemappedClassSource.RemapperUnavaibleException.Reason.REMAPPER_DISABLED);
/*     */       }
/*     */ 
/*  71 */       Class renamerClazz = this.classRemapper.getClass();
/*     */ 
/*  73 */       this.mapType = MethodUtils.getAccessibleMethod(renamerClazz, "map", new Class[] { String.class });
/*     */ 
/*  76 */       return this;
/*     */     }
/*     */     catch (RemapperUnavaibleException e) {
/*  79 */       throw e;
/*     */     }
/*     */     catch (Exception e) {
/*  82 */       throw new RuntimeException("Cannot access MCPC remapper.", e);
/*     */     }
/*     */   }
/*     */ 
/*     */   public Class<?> loadClass(String canonicalName) throws ClassNotFoundException
/*     */   {
/*  88 */     String remapped = getClassName(canonicalName);
/*     */     try
/*     */     {
/*  91 */       return this.loader.loadClass(remapped); } catch (ClassNotFoundException e) {
/*     */     }
/*  93 */     throw new ClassNotFoundException("Cannot find " + canonicalName + "(Remapped: " + remapped + ")");
/*     */   }
/*     */ 
/*     */   public String getClassName(String path)
/*     */   {
/*     */     try
/*     */     {
/* 104 */       String remapped = (String)this.mapType.invoke(this.classRemapper, new Object[] { path.replace('.', '/') });
/* 105 */       return remapped.replace('/', '.');
/*     */     } catch (Exception e) {
/* 107 */       throw new RuntimeException("Cannot remap class name.", e);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static class RemapperUnavaibleException extends RuntimeException
/*     */   {
/*     */     private static final long serialVersionUID = 1L;
/*     */     private final Reason reason;
/*     */ 
/*     */     public RemapperUnavaibleException(Reason reason)
/*     */     {
/* 136 */       super();
/* 137 */       this.reason = reason;
/*     */     }
/*     */ 
/*     */     public Reason getReason()
/*     */     {
/* 145 */       return this.reason;
/*     */     }
/*     */ 
/*     */     public static enum Reason
/*     */     {
/* 115 */       MCPC_NOT_PRESENT("The server is not running MCPC+"), 
/* 116 */       REMAPPER_DISABLED("Running an MCPC+ server but the remapper is unavailable. Please turn it on!");
/*     */ 
/*     */       private final String message;
/*     */ 
/*     */       private Reason(String message) {
/* 121 */         this.message = message;
/*     */       }
/*     */ 
/*     */       public String getMessage()
/*     */       {
/* 129 */         return this.message;
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.utility.RemappedClassSource
 * JD-Core Version:    0.6.2
 */