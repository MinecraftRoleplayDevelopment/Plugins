/*     */ package net.milkbowl.vault.item;
/*     */ 
/*     */ import org.bukkit.Material;
/*     */ import org.bukkit.inventory.ItemStack;
/*     */ 
/*     */ public class ItemInfo
/*     */ {
/*     */   public final Material material;
/*     */   public final short subTypeId;
/*     */   public final String name;
/*     */   public final String[][] search;
/*     */ 
/*     */   public ItemInfo(String name, String[][] search, Material material)
/*     */   {
/*  29 */     this.material = material;
/*  30 */     this.name = name;
/*  31 */     this.subTypeId = 0;
/*  32 */     this.search = ((String[][])search.clone());
/*     */   }
/*     */ 
/*     */   public ItemInfo(String name, String[][] search, Material material, short subTypeId) {
/*  36 */     this.name = name;
/*  37 */     this.material = material;
/*  38 */     this.subTypeId = subTypeId;
/*  39 */     this.search = ((String[][])search.clone());
/*     */   }
/*     */ 
/*     */   public Material getType() {
/*  43 */     return this.material;
/*     */   }
/*     */ 
/*     */   public short getSubTypeId() {
/*  47 */     return this.subTypeId;
/*     */   }
/*     */ 
/*     */   public int getStackSize() {
/*  51 */     return this.material.getMaxStackSize();
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public int getId() {
/*  56 */     return this.material.getId();
/*     */   }
/*     */ 
/*     */   public boolean isEdible() {
/*  60 */     return this.material.isEdible();
/*     */   }
/*     */ 
/*     */   public boolean isBlock() {
/*  64 */     return this.material.isBlock();
/*     */   }
/*     */ 
/*     */   public String getName() {
/*  68 */     return this.name;
/*     */   }
/*     */ 
/*     */   public int hashCode()
/*     */   {
/*  73 */     int hash = 7;
/*  74 */     hash = 17 * hash + getId();
/*  75 */     hash = 17 * hash + this.subTypeId;
/*  76 */     return hash;
/*     */   }
/*     */ 
/*     */   public boolean isDurable() {
/*  80 */     return this.material.getMaxDurability() > 0;
/*     */   }
/*     */ 
/*     */   public ItemStack toStack() {
/*  84 */     return new ItemStack(this.material, 1, this.subTypeId);
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/*  90 */     return String.format("%s[%d:%d]", new Object[] { this.name, Integer.valueOf(this.material.getId()), Short.valueOf(this.subTypeId) });
/*     */   }
/*     */ 
/*     */   public boolean equals(Object obj)
/*     */   {
/*  95 */     if (obj == null)
/*  96 */       return false;
/*  97 */     if (this == obj)
/*  98 */       return true;
/*  99 */     if (!(obj instanceof ItemInfo)) {
/* 100 */       return false;
/*     */     }
/* 102 */     return (((ItemInfo)obj).material == this.material) && (((ItemInfo)obj).subTypeId == this.subTypeId);
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\Vault.jar
 * Qualified Name:     net.milkbowl.vault.item.ItemInfo
 * JD-Core Version:    0.6.2
 */