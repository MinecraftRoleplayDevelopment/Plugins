/*     */ package net.nunnerycode.bukkit.itemattributes.utils;
/*     */ 
/*     */ import java.util.Collection;
/*     */ import net.nunnerycode.bukkit.itemattributes.api.attributes.Attribute;
/*     */ import org.apache.commons.lang.math.RandomUtils;
/*     */ import org.apache.commons.lang3.math.NumberUtils;
/*     */ import org.bukkit.ChatColor;
/*     */ 
/*     */ public final class ItemAttributesParseUtil
/*     */ {
/*     */   public static double getValue(Collection<String> collection, Attribute attribute)
/*     */   {
/*  15 */     if ((collection == null) || (attribute == null)) {
/*  16 */       return 0.0D;
/*     */     }
/*  18 */     if (!attribute.isEnabled()) {
/*  19 */       return 0.0D;
/*     */     }
/*  21 */     if (attribute.isPercentage()) {
/*  22 */       return getDoublePercentage(collection, attribute, attribute.getMaxValue());
/*     */     }
/*  24 */     return getDouble(collection, attribute);
/*     */   }
/*     */ 
/*     */   public static double getDoublePercentage(Collection<String> collection, Attribute attribute, double base) {
/*  28 */     double d = 0.0D;
/*  29 */     if ((collection == null) || (collection.isEmpty()) || (attribute == null)) {
/*  30 */       return d;
/*     */     }
/*  32 */     for (String s : collection) {
/*  33 */       String stripped = ChatColor.stripColor(s);
/*  34 */       String withoutNumbers = stripped.replaceAll("[0-9\\+%\\-]", "").trim();
/*  35 */       String withoutLetters = stripped.replaceAll("[a-zA-Z\\+%:]", "").trim();
/*  36 */       String withoutVariables = attribute.getFormat().replaceAll("%(?s)(.*?)%", "").trim();
/*  37 */       if (withoutNumbers.equals(withoutVariables))
/*     */       {
/*  40 */         if (!s.contains("%")) {
/*  41 */           if (withoutLetters.contains(" - ")) {
/*  42 */             String[] split = withoutLetters.split(" - ");
/*  43 */             double first = NumberUtils.toDouble(split[0], 0.0D);
/*  44 */             double second = NumberUtils.toDouble(split[1], 0.0D);
/*  45 */             d += (RandomUtils.nextDouble() * (Math.max(first, second) - Math.min(first, second)) + Math.min(first, second)) / (base != 0.0D ? base : 100.0D);
/*     */           }
/*     */           else {
/*  48 */             d += NumberUtils.toDouble(withoutLetters, 0.0D) / (base != 0.0D ? base : 100.0D);
/*     */           }
/*     */         }
/*  51 */         else if (withoutLetters.contains(" - ")) {
/*  52 */           String[] split = withoutLetters.split(" - ");
/*  53 */           if (split.length > 1) {
/*  54 */             double first = NumberUtils.toDouble(split[0], 0.0D);
/*  55 */             double second = NumberUtils.toDouble(split[1], 0.0D);
/*  56 */             d += (RandomUtils.nextDouble() * (Math.max(first, second) - Math.min(first, second)) + Math.min(first, second)) / 100.0D;
/*     */           }
/*     */         }
/*     */         else {
/*  60 */           d += NumberUtils.toDouble(withoutLetters, 0.0D) / 100.0D;
/*     */         }
/*     */       }
/*     */     }
/*  64 */     return d;
/*     */   }
/*     */ 
/*     */   public static double getDouble(Collection<String> collection, Attribute attribute) {
/*  68 */     double d = 0.0D;
/*  69 */     if ((collection == null) || (collection.isEmpty()) || (attribute == null)) {
/*  70 */       return d;
/*     */     }
/*  72 */     for (String s : collection) {
/*  73 */       String stripped = ChatColor.stripColor(s);
/*  74 */       String withoutNumbers = stripped.replaceAll("[0-9\\+%\\-]", "").trim();
/*  75 */       String withoutLetters = stripped.replaceAll("[a-zA-Z\\+%:]", "").trim();
/*  76 */       String withoutVariables = attribute.getFormat().replaceAll("%(?s)(.*?)%", "").trim();
/*  77 */       if (withoutNumbers.equals(withoutVariables))
/*     */       {
/*  80 */         if (withoutLetters.contains(" - ")) {
/*  81 */           String[] split = withoutLetters.split(" - ");
/*  82 */           if (split.length > 1) {
/*  83 */             double first = NumberUtils.toDouble(split[0], 0.0D);
/*  84 */             double second = NumberUtils.toDouble(split[1], 0.0D);
/*  85 */             d += RandomUtils.nextDouble() * (Math.max(first, second) - Math.min(first, second)) + Math.min(first, second);
/*     */           }
/*     */         }
/*     */         else {
/*  89 */           d += NumberUtils.toDouble(withoutLetters, 0.0D);
/*     */         }
/*     */       }
/*     */     }
/*  92 */     return d;
/*     */   }
/*     */ 
/*     */   public static double getDoublePercentage(Collection<String> collection, Attribute attribute) {
/*  96 */     return getDoublePercentage(collection, attribute, 100.0D);
/*     */   }
/*     */ 
/*     */   public static int getInt(Collection<String> collection, Attribute attribute) {
/* 100 */     int i = 0;
/* 101 */     if ((collection == null) || (collection.isEmpty()) || (attribute == null)) {
/* 102 */       return i;
/*     */     }
/* 104 */     for (String s : collection) {
/* 105 */       String stripped = ChatColor.stripColor(s);
/* 106 */       String withoutNumbers = stripped.replaceAll("[0-9\\+%\\-]", "").trim();
/* 107 */       String withoutLetters = stripped.replaceAll("[a-zA-Z\\+%:]", "").trim();
/* 108 */       String withoutVariables = attribute.getFormat().replaceAll("%(?s)(.*?)%", "").trim();
/* 109 */       if (withoutNumbers.equals(withoutVariables))
/*     */       {
/* 112 */         if (withoutLetters.contains(" - ")) {
/* 113 */           String[] split = withoutLetters.split(" - ");
/* 114 */           if (split.length > 1) {
/* 115 */             int first = NumberUtils.toInt(split[0], 0);
/* 116 */             int second = NumberUtils.toInt(split[1], 0);
/* 117 */             i = (int)(i + (RandomUtils.nextDouble() * (Math.max(first, second) - Math.min(first, second)) + Math.min(first, second)));
/*     */           }
/*     */         }
/*     */         else {
/* 121 */           i += NumberUtils.toInt(withoutLetters, 0);
/*     */         }
/*     */       }
/*     */     }
/* 124 */     return i;
/*     */   }
/*     */ 
/*     */   public static boolean hasFormatInCollection(Collection<String> collection, String format) {
/* 128 */     boolean b = false;
/* 129 */     if ((collection == null) || (collection.isEmpty()) || (format == null) || (format.isEmpty())) {
/* 130 */       return b;
/*     */     }
/* 132 */     for (String s : collection) {
/* 133 */       if (ChatColor.stripColor(s).equalsIgnoreCase(ChatColor.stripColor(format))) {
/* 134 */         b = true;
/*     */       }
/*     */     }
/* 137 */     return b;
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\ItemAttributes.jar
 * Qualified Name:     net.nunnerycode.bukkit.itemattributes.utils.ItemAttributesParseUtil
 * JD-Core Version:    0.6.2
 */