/*    */ package com.zettelnet.armorweight;
/*    */ 
/*    */ import org.bukkit.Material;
/*    */ import org.bukkit.inventory.ItemStack;
/*    */ 
/*    */ public class LeatherArmorType extends ArmorType
/*    */ {
/*    */   public LeatherArmorType(Material material)
/*    */   {
/*  9 */     super(material);
/*    */   }
/*    */ 
/*    */   public LeatherArmorType(String name, Material[] contentMaterials) {
/* 13 */     super(name, contentMaterials);
/*    */   }
/*    */ 
/*    */   public double getWeight(ItemStack item)
/*    */   {
/* 19 */     return super.getWeight(item);
/*    */   }
/*    */ }

/* Location:           D:\Github\Mechanics\ArmorWeight.jar
 * Qualified Name:     com.zettelnet.armorweight.LeatherArmorType
 * JD-Core Version:    0.6.2
 */