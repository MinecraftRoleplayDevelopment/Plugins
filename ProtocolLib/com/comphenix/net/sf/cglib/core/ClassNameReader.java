/*    */ package com.comphenix.net.sf.cglib.core;
/*    */ 
/*    */ import com.comphenix.net.sf.cglib.asm.ClassAdapter;
/*    */ import com.comphenix.net.sf.cglib.asm.ClassReader;
/*    */ import com.comphenix.net.sf.cglib.asm.ClassVisitor;
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;
/*    */ 
/*    */ public class ClassNameReader
/*    */ {
/* 27 */   private static final EarlyExitException EARLY_EXIT = new EarlyExitException(null);
/*    */ 
/*    */   public static String getClassName(ClassReader r)
/*    */   {
/* 32 */     return getClassInfo(r)[0];
/*    */   }
/*    */ 
/*    */   public static String[] getClassInfo(ClassReader r)
/*    */   {
/* 37 */     List array = new ArrayList();
/*    */     try {
/* 39 */       r.accept(new ClassAdapter(null)
/*    */       {
/*    */         private final List val$array;
/*    */ 
/*    */         public void visit(int version, int access, String name, String signature, String superName, String[] interfaces)
/*    */         {
/* 46 */           this.val$array.add(name.replace('/', '.'));
/* 47 */           if (superName != null) {
/* 48 */             this.val$array.add(superName.replace('/', '.'));
/*    */           }
/* 50 */           for (int i = 0; i < interfaces.length; i++) {
/* 51 */             this.val$array.add(interfaces[i].replace('/', '.'));
/*    */           }
/*    */ 
/* 54 */           throw ClassNameReader.EARLY_EXIT;
/*    */         }
/*    */       }
/*    */       , 6);
/*    */     }
/*    */     catch (EarlyExitException e)
/*    */     {
/*    */     }
/*    */ 
/* 59 */     return (String[])array.toArray(new String[0]);
/*    */   }
/*    */ 
/*    */   private static class EarlyExitException extends RuntimeException
/*    */   {
/*    */     private EarlyExitException()
/*    */     {
/*    */     }
/*    */ 
/*    */     EarlyExitException(ClassNameReader.1 x0)
/*    */     {
/* 28 */       this();
/*    */     }
/*    */   }
/*    */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.net.sf.cglib.core.ClassNameReader
 * JD-Core Version:    0.6.2
 */