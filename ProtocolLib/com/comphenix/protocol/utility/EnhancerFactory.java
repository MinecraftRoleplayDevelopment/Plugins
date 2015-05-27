/*    */ package com.comphenix.protocol.utility;
/*    */ 
/*    */ import com.comphenix.net.sf.cglib.proxy.Enhancer;
/*    */ 
/*    */ public class EnhancerFactory
/*    */ {
/* 10 */   private static EnhancerFactory INSTANCE = new EnhancerFactory();
/*    */ 
/* 13 */   private ClassLoader loader = EnhancerFactory.class.getClassLoader();
/*    */ 
/*    */   public static EnhancerFactory getInstance() {
/* 16 */     return INSTANCE;
/*    */   }
/*    */ 
/*    */   public Enhancer createEnhancer()
/*    */   {
/* 24 */     Enhancer enhancer = new Enhancer();
/* 25 */     enhancer.setClassLoader(this.loader);
/* 26 */     return enhancer;
/*    */   }
/*    */ 
/*    */   public void setClassLoader(ClassLoader loader)
/*    */   {
/* 34 */     this.loader = loader;
/*    */   }
/*    */ 
/*    */   public ClassLoader getClassLoader()
/*    */   {
/* 42 */     return this.loader;
/*    */   }
/*    */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.utility.EnhancerFactory
 * JD-Core Version:    0.6.2
 */