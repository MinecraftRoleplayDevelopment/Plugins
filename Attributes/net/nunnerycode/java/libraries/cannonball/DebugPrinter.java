/*    */ package net.nunnerycode.java.libraries.cannonball;
/*    */ 
/*    */ import java.io.File;
/*    */ import java.io.FileWriter;
/*    */ import java.io.IOException;
/*    */ import java.io.PrintWriter;
/*    */ import java.util.Calendar;
/*    */ import java.util.Date;
/*    */ import java.util.logging.Level;
/*    */ 
/*    */ public class DebugPrinter
/*    */ {
/*    */   private File debugFolder;
/*    */   private File debugFile;
/*    */ 
/*    */   public DebugPrinter(String folderPath, String fileName)
/*    */   {
/* 16 */     this(new File(folderPath), new File(folderPath, fileName));
/*    */   }
/*    */ 
/*    */   public DebugPrinter(File folder, File file) {
/* 20 */     if (((!folder.exists()) && (!folder.mkdirs())) || (!folder.isDirectory())) {
/* 21 */       return;
/*    */     }
/* 23 */     this.debugFolder = folder;
/* 24 */     this.debugFile = file;
/*    */   }
/*    */ 
/*    */   public static void debug(File file, Level level, String[] messages) {
/* 28 */     if (file == null) {
/* 29 */       throw new IllegalArgumentException("file cannot be null");
/*    */     }
/* 31 */     if (level == null)
/* 32 */       throw new IllegalArgumentException("level cannot be null");
/*    */     try
/*    */     {
/* 35 */       if ((!file.exists()) && (!file.createNewFile())) {
/* 36 */         return;
/*    */       }
/* 38 */       FileWriter fw = new FileWriter(file.getPath(), true);
/* 39 */       PrintWriter pw = new PrintWriter(fw);
/* 40 */       for (String message : messages) {
/* 41 */         pw.println("[" + level.getName() + "] " + Calendar.getInstance().getTime().toString() + " | " + message);
/*    */       }
/*    */ 
/* 44 */       pw.flush();
/* 45 */       pw.close();
/*    */     } catch (IOException e) {
/* 47 */       e.printStackTrace();
/*    */     }
/*    */   }
/*    */ 
/*    */   public void debug(Level level, String[] messages) {
/*    */     try {
/* 53 */       if ((!getDebugFolder().exists()) && (!getDebugFolder().mkdirs())) {
/* 54 */         return;
/*    */       }
/* 56 */       File saveTo = getDebugFile();
/* 57 */       if ((!saveTo.exists()) && (!saveTo.createNewFile())) {
/* 58 */         return;
/*    */       }
/* 60 */       FileWriter fw = new FileWriter(saveTo.getPath(), true);
/* 61 */       PrintWriter pw = new PrintWriter(fw);
/* 62 */       for (String message : messages) {
/* 63 */         pw.println("[" + level.getName() + "] " + Calendar.getInstance().getTime().toString() + " | " + message);
/*    */       }
/*    */ 
/* 66 */       pw.flush();
/* 67 */       pw.close();
/*    */     } catch (IOException e) {
/* 69 */       e.printStackTrace();
/*    */     }
/*    */   }
/*    */ 
/*    */   public File getDebugFolder() {
/* 74 */     return this.debugFolder;
/*    */   }
/*    */ 
/*    */   public File getDebugFile() {
/* 78 */     return this.debugFile;
/*    */   }
/*    */ }

/* Location:           D:\Github\Mechanics\ItemAttributes.jar
 * Qualified Name:     net.nunnerycode.java.libraries.cannonball.DebugPrinter
 * JD-Core Version:    0.6.2
 */