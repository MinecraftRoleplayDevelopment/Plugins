/*    */ package com.comphenix.net.sf.cglib.transform.impl;
/*    */ 
/*    */ import com.comphenix.net.sf.cglib.asm.Type;
/*    */ import com.comphenix.net.sf.cglib.core.EmitUtils;
/*    */ import com.comphenix.net.sf.cglib.core.TypeUtils;
/*    */ import com.comphenix.net.sf.cglib.transform.ClassEmitterTransformer;
/*    */ import java.util.Map;
/*    */ import java.util.Set;
/*    */ 
/*    */ public class AddPropertyTransformer extends ClassEmitterTransformer
/*    */ {
/*    */   private final String[] names;
/*    */   private final Type[] types;
/*    */ 
/*    */   public AddPropertyTransformer(Map props)
/*    */   {
/* 28 */     int size = props.size();
/* 29 */     this.names = ((String[])props.keySet().toArray(new String[size]));
/* 30 */     this.types = new Type[size];
/* 31 */     for (int i = 0; i < size; i++)
/* 32 */       this.types[i] = ((Type)props.get(this.names[i]));
/*    */   }
/*    */ 
/*    */   public AddPropertyTransformer(String[] names, Type[] types)
/*    */   {
/* 37 */     this.names = names;
/* 38 */     this.types = types;
/*    */   }
/*    */ 
/*    */   public void end_class() {
/* 42 */     if (!TypeUtils.isAbstract(getAccess())) {
/* 43 */       EmitUtils.add_properties(this, this.names, this.types);
/*    */     }
/* 45 */     super.end_class();
/*    */   }
/*    */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.net.sf.cglib.transform.impl.AddPropertyTransformer
 * JD-Core Version:    0.6.2
 */