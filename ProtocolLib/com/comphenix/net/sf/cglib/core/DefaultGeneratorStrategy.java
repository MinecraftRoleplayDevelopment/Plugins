/*    */ package com.comphenix.net.sf.cglib.core;
/*    */ 
/*    */ import com.comphenix.net.sf.cglib.asm.ClassWriter;
/*    */ 
/*    */ public class DefaultGeneratorStrategy
/*    */   implements GeneratorStrategy
/*    */ {
/* 21 */   public static final DefaultGeneratorStrategy INSTANCE = new DefaultGeneratorStrategy();
/*    */ 
/*    */   public byte[] generate(ClassGenerator cg) throws Exception {
/* 24 */     ClassWriter cw = getClassWriter();
/* 25 */     transform(cg).generateClass(cw);
/* 26 */     return transform(cw.toByteArray());
/*    */   }
/*    */ 
/*    */   protected ClassWriter getClassWriter() throws Exception {
/* 30 */     return new DebuggingClassWriter(1);
/*    */   }
/*    */ 
/*    */   protected byte[] transform(byte[] b) throws Exception {
/* 34 */     return b;
/*    */   }
/*    */ 
/*    */   protected ClassGenerator transform(ClassGenerator cg) throws Exception {
/* 38 */     return cg;
/*    */   }
/*    */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.net.sf.cglib.core.DefaultGeneratorStrategy
 * JD-Core Version:    0.6.2
 */