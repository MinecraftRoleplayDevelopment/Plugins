/*    */ package com.zettelnet.armorweight;
/*    */ 
/*    */ import org.apache.commons.lang.Validate;
/*    */ 
/*    */ public enum ArmorPart
/*    */ {
/*  7 */   BOOTS(0), LEGGINGS(1), CHESTPLATE(2), HELMET(3);
/*    */ 
/*    */   private final int id;
/*    */   private double weightShare;
/*    */ 
/*    */   private ArmorPart(int id) {
/* 14 */     this.id = id;
/* 15 */     this.weightShare = 0.25D;
/*    */   }
/*    */ 
/*    */   public int getId() {
/* 19 */     return this.id;
/*    */   }
/*    */ 
/*    */   public static ArmorPart valueOf(int id) {
/* 23 */     switch (id) {
/*    */     case 0:
/* 25 */       return BOOTS;
/*    */     case 1:
/* 27 */       return LEGGINGS;
/*    */     case 2:
/* 29 */       return CHESTPLATE;
/*    */     case 3:
/* 31 */       return HELMET;
/*    */     }
/* 33 */     return null;
/*    */   }
/*    */ 
/*    */   public double getWeightShare()
/*    */   {
/* 38 */     return this.weightShare;
/*    */   }
/*    */ 
/*    */   public void setWeightShare(double weightShare) {
/* 42 */     this.weightShare = weightShare;
/*    */   }
/*    */ 
/*    */   public static ArmorPart matchPart(String name) {
/* 46 */     Validate.notNull(name, "Name cannot be null");
/* 47 */     return valueOf(name.toUpperCase().replaceAll("\\s+", "_").replaceAll("\\W", ""));
/*    */   }
/*    */ }

/* Location:           D:\Github\Mechanics\ArmorWeight.jar
 * Qualified Name:     com.zettelnet.armorweight.ArmorPart
 * JD-Core Version:    0.6.2
 */