/*    */ package com.comphenix.net.sf.cglib.core;
/*    */ 
/*    */ import com.comphenix.net.sf.cglib.asm.Label;
/*    */ 
/*    */ public class Block
/*    */ {
/*    */   private CodeEmitter e;
/*    */   private Label start;
/*    */   private Label end;
/*    */ 
/*    */   public Block(CodeEmitter e)
/*    */   {
/* 27 */     this.e = e;
/* 28 */     this.start = e.mark();
/*    */   }
/*    */ 
/*    */   public CodeEmitter getCodeEmitter() {
/* 32 */     return this.e;
/*    */   }
/*    */ 
/*    */   public void end() {
/* 36 */     if (this.end != null) {
/* 37 */       throw new IllegalStateException("end of label already set");
/*    */     }
/* 39 */     this.end = this.e.mark();
/*    */   }
/*    */ 
/*    */   public Label getStart() {
/* 43 */     return this.start;
/*    */   }
/*    */ 
/*    */   public Label getEnd() {
/* 47 */     return this.end;
/*    */   }
/*    */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.net.sf.cglib.core.Block
 * JD-Core Version:    0.6.2
 */