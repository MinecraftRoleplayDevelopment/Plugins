/*    */ package com.comphenix.protocol;
/*    */ 
/*    */ import java.io.PrintStream;
/*    */ import org.bukkit.Bukkit;
/*    */ 
/*    */ public class Application
/*    */ {
/*    */   private static Thread mainThread;
/* 29 */   private static boolean primaryMethod = true;
/*    */ 
/*    */   public static void main(String[] args)
/*    */   {
/* 33 */     System.out.println("This is a Bukkit library. Place it in the plugin-folder and restart the server!");
/*    */   }
/*    */ 
/*    */   public static boolean isPrimaryThread()
/*    */   {
/* 41 */     if (primaryMethod) {
/*    */       try {
/* 43 */         return Bukkit.isPrimaryThread();
/*    */       } catch (LinkageError e) {
/* 45 */         primaryMethod = false;
/*    */       }
/*    */     }
/*    */ 
/* 49 */     return Thread.currentThread().equals(mainThread);
/*    */   }
/*    */ 
/*    */   static void registerPrimaryThread()
/*    */   {
/* 56 */     mainThread = Thread.currentThread();
/*    */   }
/*    */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.Application
 * JD-Core Version:    0.6.2
 */