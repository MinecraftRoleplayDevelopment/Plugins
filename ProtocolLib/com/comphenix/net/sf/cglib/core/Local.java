/*    */ package com.comphenix.net.sf.cglib.core;
/*    */ 
/*    */ import com.comphenix.net.sf.cglib.asm.Type;
/*    */ 
/*    */ public class Local
/*    */ {
/*    */   private Type type;
/*    */   private int index;
/*    */ 
/*    */   public Local(int index, Type type)
/*    */   {
/* 26 */     this.type = type;
/* 27 */     this.index = index;
/*    */   }
/*    */ 
/*    */   public int getIndex() {
/* 31 */     return this.index;
/*    */   }
/*    */ 
/*    */   public Type getType() {
/* 35 */     return this.type;
/*    */   }
/*    */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.net.sf.cglib.core.Local
 * JD-Core Version:    0.6.2
 */