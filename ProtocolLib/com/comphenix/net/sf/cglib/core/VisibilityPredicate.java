/*    */ package com.comphenix.net.sf.cglib.core;
/*    */ 
/*    */ import com.comphenix.net.sf.cglib.asm.Type;
/*    */ import java.lang.reflect.Member;
/*    */ import java.lang.reflect.Modifier;
/*    */ 
/*    */ public class VisibilityPredicate
/*    */   implements Predicate
/*    */ {
/*    */   private boolean protectedOk;
/*    */   private String pkg;
/*    */ 
/*    */   public VisibilityPredicate(Class source, boolean protectedOk)
/*    */   {
/* 26 */     this.protectedOk = protectedOk;
/* 27 */     this.pkg = TypeUtils.getPackageName(Type.getType(source));
/*    */   }
/*    */ 
/*    */   public boolean evaluate(Object arg) {
/* 31 */     int mod = (arg instanceof Member) ? ((Member)arg).getModifiers() : ((Integer)arg).intValue();
/* 32 */     if (Modifier.isPrivate(mod))
/* 33 */       return false;
/* 34 */     if (Modifier.isPublic(mod))
/* 35 */       return true;
/* 36 */     if (Modifier.isProtected(mod)) {
/* 37 */       return this.protectedOk;
/*    */     }
/* 39 */     return this.pkg.equals(TypeUtils.getPackageName(Type.getType(((Member)arg).getDeclaringClass())));
/*    */   }
/*    */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.net.sf.cglib.core.VisibilityPredicate
 * JD-Core Version:    0.6.2
 */