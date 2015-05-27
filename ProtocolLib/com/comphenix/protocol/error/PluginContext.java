/*     */ package com.comphenix.protocol.error;
/*     */ 
/*     */ import com.google.common.base.Preconditions;
/*     */ import java.io.File;
/*     */ import java.net.URL;
/*     */ import java.net.URLDecoder;
/*     */ import java.security.CodeSource;
/*     */ import java.security.ProtectionDomain;
/*     */ import org.bukkit.Bukkit;
/*     */ import org.bukkit.plugin.Plugin;
/*     */ import org.bukkit.plugin.PluginManager;
/*     */ 
/*     */ public final class PluginContext
/*     */ {
/*     */   private static File pluginFolder;
/*     */ 
/*     */   public static String getPluginCaller(Exception ex)
/*     */   {
/*  26 */     StackTraceElement[] elements = ex.getStackTrace();
/*  27 */     String current = getPluginName(elements[0]);
/*     */ 
/*  29 */     for (int i = 1; i < elements.length; i++) {
/*  30 */       String caller = getPluginName(elements[i]);
/*  31 */       if ((caller != null) && (!caller.equals(current))) {
/*  32 */         return caller;
/*     */       }
/*     */     }
/*     */ 
/*  36 */     return null;
/*     */   }
/*     */ 
/*     */   public static String getPluginName(StackTraceElement element)
/*     */   {
/*     */     try
/*     */     {
/*  46 */       if (Bukkit.getServer() == null) {
/*  47 */         return null;
/*     */       }
/*     */ 
/*  50 */       CodeSource codeSource = Class.forName(element.getClassName()).getProtectionDomain().getCodeSource();
/*  51 */       if (codeSource != null) {
/*  52 */         String encoding = codeSource.getLocation().getPath();
/*  53 */         File path = new File(URLDecoder.decode(encoding, "UTF-8"));
/*  54 */         File plugins = getPluginFolder();
/*     */ 
/*  56 */         if ((plugins != null) && (folderContains(plugins, path))) {
/*  57 */           return path.getName().replaceAll(".jar", "");
/*     */         }
/*     */       }
/*     */     }
/*     */     catch (Throwable ex)
/*     */     {
/*     */     }
/*  64 */     return null;
/*     */   }
/*     */ 
/*     */   private static boolean folderContains(File folder, File file)
/*     */   {
/*  74 */     Preconditions.checkNotNull(folder, "folder cannot be NULL");
/*  75 */     Preconditions.checkNotNull(file, "file cannot be NULL");
/*     */ 
/*  78 */     folder = folder.getAbsoluteFile();
/*  79 */     file = file.getAbsoluteFile();
/*     */ 
/*  81 */     while (file != null) {
/*  82 */       if (folder.equals(file))
/*  83 */         return true;
/*  84 */       file = file.getParentFile();
/*     */     }
/*     */ 
/*  87 */     return false;
/*     */   }
/*     */ 
/*     */   private static File getPluginFolder()
/*     */   {
/*  95 */     File folder = pluginFolder;
/*     */ 
/*  97 */     if ((folder == null) && (Bukkit.getServer() != null)) {
/*  98 */       Plugin[] plugins = Bukkit.getPluginManager().getPlugins();
/*     */ 
/* 100 */       if (plugins.length > 0) {
/* 101 */         folder = plugins[0].getDataFolder().getParentFile();
/* 102 */         pluginFolder = folder;
/*     */       }
/*     */     }
/*     */ 
/* 106 */     return folder;
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.error.PluginContext
 * JD-Core Version:    0.6.2
 */