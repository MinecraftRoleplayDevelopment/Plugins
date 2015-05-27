/*     */ package net.nunnerycode.bukkit.itemattributes.attributes;
/*     */ 
/*     */ import net.nunnerycode.bukkit.itemattributes.api.attributes.Attribute;
/*     */ import org.bukkit.Sound;
/*     */ 
/*     */ public class ItemAttribute
/*     */   implements Attribute
/*     */ {
/*     */   private final String name;
/*     */   private boolean enabled;
/*     */   private double maxValue;
/*     */   private boolean percentage;
/*     */   private String format;
/*     */   private Sound sound;
/*     */   private double baseValue;
/*     */ 
/*     */   public ItemAttribute(String name, boolean enabled, double maxValue, boolean percentage, String format, Sound sound, double baseValue)
/*     */   {
/*  18 */     this.name = name;
/*  19 */     this.enabled = enabled;
/*  20 */     this.maxValue = maxValue;
/*  21 */     this.percentage = percentage;
/*  22 */     this.format = format;
/*  23 */     this.sound = sound;
/*  24 */     this.baseValue = baseValue;
/*     */   }
/*     */ 
/*     */   public boolean isEnabled()
/*     */   {
/*  29 */     return this.enabled;
/*     */   }
/*     */ 
/*     */   public void setEnabled(boolean b)
/*     */   {
/*  34 */     this.enabled = b;
/*     */   }
/*     */ 
/*     */   public double getMaxValue()
/*     */   {
/*  39 */     return this.maxValue;
/*     */   }
/*     */ 
/*     */   public void setMaxValue(double d)
/*     */   {
/*  44 */     this.maxValue = d;
/*     */   }
/*     */ 
/*     */   public boolean isPercentage()
/*     */   {
/*  49 */     return this.percentage;
/*     */   }
/*     */ 
/*     */   public void setPercentage(boolean b)
/*     */   {
/*  54 */     this.percentage = b;
/*     */   }
/*     */ 
/*     */   public String getFormat()
/*     */   {
/*  59 */     return this.format;
/*     */   }
/*     */ 
/*     */   public void setFormat(String s)
/*     */   {
/*  64 */     this.format = s;
/*     */   }
/*     */ 
/*     */   public String getName()
/*     */   {
/*  69 */     return this.name;
/*     */   }
/*     */ 
/*     */   public Sound getSound() {
/*  73 */     return this.sound;
/*     */   }
/*     */ 
/*     */   public void setSound(Sound s) {
/*  77 */     this.sound = s;
/*     */   }
/*     */ 
/*     */   public double getBaseValue()
/*     */   {
/*  82 */     return this.baseValue;
/*     */   }
/*     */ 
/*     */   public void setBaseValue(double d)
/*     */   {
/*  87 */     this.baseValue = d;
/*     */   }
/*     */ 
/*     */   public boolean equals(Object o)
/*     */   {
/*  92 */     if (this == o) return true;
/*  93 */     if (!(o instanceof ItemAttribute)) return false;
/*     */ 
/*  95 */     ItemAttribute that = (ItemAttribute)o;
/*     */ 
/*  97 */     if (Double.compare(that.baseValue, this.baseValue) != 0) return false;
/*  98 */     if (this.enabled != that.enabled) return false;
/*  99 */     if (Double.compare(that.maxValue, this.maxValue) != 0) return false;
/* 100 */     if (this.percentage != that.percentage) return false;
/* 101 */     if (this.format != null ? !this.format.equals(that.format) : that.format != null) return false;
/* 102 */     if (this.name != null ? !this.name.equals(that.name) : that.name != null) return false;
/* 103 */     if (this.sound != that.sound) return false;
/*     */ 
/* 105 */     return true;
/*     */   }
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 112 */     int result = this.name != null ? this.name.hashCode() : 0;
/* 113 */     result = 31 * result + (this.enabled ? 1 : 0);
/* 114 */     long temp = Double.doubleToLongBits(this.maxValue);
/* 115 */     result = 31 * result + (int)(temp ^ temp >>> 32);
/* 116 */     result = 31 * result + (this.percentage ? 1 : 0);
/* 117 */     result = 31 * result + (this.format != null ? this.format.hashCode() : 0);
/* 118 */     result = 31 * result + (this.sound != null ? this.sound.hashCode() : 0);
/* 119 */     temp = Double.doubleToLongBits(this.baseValue);
/* 120 */     result = 31 * result + (int)(temp ^ temp >>> 32);
/* 121 */     return result;
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\ItemAttributes.jar
 * Qualified Name:     net.nunnerycode.bukkit.itemattributes.attributes.ItemAttribute
 * JD-Core Version:    0.6.2
 */