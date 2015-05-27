/*    */ package com.comphenix.net.sf.cglib.reflect;
/*    */ 
/*    */ import java.lang.reflect.Constructor;
/*    */ import java.lang.reflect.InvocationTargetException;
/*    */ 
/*    */ public class FastConstructor extends FastMember
/*    */ {
/*    */   FastConstructor(FastClass fc, Constructor constructor)
/*    */   {
/* 24 */     super(fc, constructor, fc.getIndex(constructor.getParameterTypes()));
/*    */   }
/*    */ 
/*    */   public Class[] getParameterTypes() {
/* 28 */     return ((Constructor)this.member).getParameterTypes();
/*    */   }
/*    */ 
/*    */   public Class[] getExceptionTypes() {
/* 32 */     return ((Constructor)this.member).getExceptionTypes();
/*    */   }
/*    */ 
/*    */   public Object newInstance() throws InvocationTargetException {
/* 36 */     return this.fc.newInstance(this.index, null);
/*    */   }
/*    */ 
/*    */   public Object newInstance(Object[] args) throws InvocationTargetException {
/* 40 */     return this.fc.newInstance(this.index, args);
/*    */   }
/*    */ 
/*    */   public Constructor getJavaConstructor() {
/* 44 */     return (Constructor)this.member;
/*    */   }
/*    */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.net.sf.cglib.reflect.FastConstructor
 * JD-Core Version:    0.6.2
 */