/*    */ package com.comphenix.protocol.reflect.cloning;
/*    */ 
/*    */ import java.io.ByteArrayInputStream;
/*    */ import java.io.ByteArrayOutputStream;
/*    */ import java.io.ObjectInputStream;
/*    */ import java.io.ObjectOutputStream;
/*    */ import java.io.Serializable;
/*    */ 
/*    */ public class SerializableCloner
/*    */   implements Cloner
/*    */ {
/*    */   public boolean canClone(Object source)
/*    */   {
/* 17 */     if (source == null)
/* 18 */       return false;
/* 19 */     return source instanceof Serializable;
/*    */   }
/*    */ 
/*    */   public Object clone(Object source)
/*    */   {
/* 24 */     return clone((Serializable)source);
/*    */   }
/*    */ 
/*    */   public static <T extends Serializable> T clone(T obj)
/*    */   {
/*    */     try
/*    */     {
/* 36 */       if ((obj instanceof Serializable)) {
/* 37 */         ByteArrayOutputStream out = new ByteArrayOutputStream();
/* 38 */         ObjectOutputStream oout = new ObjectOutputStream(out);
/*    */ 
/* 40 */         oout.writeObject(obj);
/* 41 */         ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(out.toByteArray()));
/* 42 */         return (Serializable)in.readObject();
/*    */       }
/* 44 */       throw new RuntimeException("Object " + obj + " is not serializable!");
/*    */     }
/*    */     catch (Exception e) {
/* 47 */       throw new RuntimeException("Unable to clone object " + obj + " (" + obj.getClass().getName() + ")", e);
/*    */     }
/*    */   }
/*    */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.reflect.cloning.SerializableCloner
 * JD-Core Version:    0.6.2
 */