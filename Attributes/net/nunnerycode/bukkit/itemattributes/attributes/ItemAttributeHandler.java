/*     */ package net.nunnerycode.bukkit.itemattributes.attributes;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Set;
/*     */ import net.nunnerycode.bukkit.itemattributes.api.ItemAttributes;
/*     */ import net.nunnerycode.bukkit.itemattributes.api.attributes.Attribute;
/*     */ import net.nunnerycode.bukkit.itemattributes.api.attributes.AttributeHandler;
/*     */ import net.nunnerycode.bukkit.itemattributes.api.managers.SettingsManager;
/*     */ import org.apache.commons.lang.math.RandomUtils;
/*     */ import org.apache.commons.lang3.math.NumberUtils;
/*     */ import org.bukkit.ChatColor;
/*     */ import org.bukkit.entity.LivingEntity;
/*     */ import org.bukkit.inventory.EntityEquipment;
/*     */ import org.bukkit.inventory.ItemStack;
/*     */ import org.bukkit.inventory.meta.ItemMeta;
/*     */ 
/*     */ public class ItemAttributeHandler
/*     */   implements AttributeHandler
/*     */ {
/*     */   private final ItemAttributes plugin;
/*     */ 
/*     */   public ItemAttributeHandler(ItemAttributes plugin)
/*     */   {
/*  22 */     this.plugin = plugin;
/*     */   }
/*     */ 
/*     */   private List<String> getStrings(Collection<String> collection, Attribute attribute) {
/*  26 */     List list = new ArrayList();
/*  27 */     if ((collection == null) || (collection.isEmpty()) || (attribute == null) || (!attribute.isEnabled())) {
/*  28 */       return list;
/*     */     }
/*  30 */     for (String s : collection) {
/*  31 */       String stripped = ChatColor.stripColor(s);
/*  32 */       String withoutVariables = attribute.getFormat().replaceAll("%(?s)(.*?)%", "").trim();
/*  33 */       if (stripped.contains(withoutVariables)) {
/*  34 */         list.add(stripped.replace(withoutVariables, "").trim());
/*     */       }
/*     */     }
/*  37 */     return list;
/*     */   }
/*     */ 
/*     */   private double getDouble(Collection<String> collection, Attribute attribute) {
/*  41 */     double d = 0.0D;
/*  42 */     if ((collection == null) || (collection.isEmpty()) || (attribute == null) || (!attribute.isEnabled())) {
/*  43 */       return d;
/*     */     }
/*  45 */     for (String s : collection) {
/*  46 */       String stripped = ChatColor.stripColor(s);
/*  47 */       String withoutNumbers = stripped.replaceAll("[0-9\\+%\\-]", "").trim();
/*  48 */       String withoutLetters = stripped.replaceAll("[a-zA-Z\\+%:]", "").trim();
/*  49 */       String withoutVariables = attribute.getFormat().replaceAll("%(?s)(.*?)%", "").trim();
/*  50 */       if (withoutNumbers.equals(withoutVariables))
/*     */       {
/*  53 */         if (withoutLetters.contains(" - ")) {
/*  54 */           String[] split = withoutLetters.split(" - ");
/*  55 */           if (split.length > 1) {
/*  56 */             double first = NumberUtils.toDouble(split[0], 0.0D);
/*  57 */             double second = NumberUtils.toDouble(split[1], 0.0D);
/*  58 */             d += RandomUtils.nextDouble() * (Math.max(first, second) - Math.min(first, second)) + Math.min(first, second);
/*     */           }
/*     */         }
/*     */         else {
/*  62 */           d += NumberUtils.toDouble(withoutLetters, 0.0D);
/*     */         }
/*     */       }
/*     */     }
/*  65 */     return d;
/*     */   }
/*     */ 
/*     */   private double getValue(Collection<String> collection, Attribute attribute) {
/*  69 */     if ((collection == null) || (attribute == null) || (!attribute.isEnabled())) {
/*  70 */       return 0.0D;
/*     */     }
/*  72 */     if (!attribute.isEnabled()) {
/*  73 */       return 0.0D;
/*     */     }
/*  75 */     if (attribute.isPercentage()) {
/*  76 */       return getDoublePercentage(collection, attribute);
/*     */     }
/*  78 */     return getDouble(collection, attribute);
/*     */   }
/*     */ 
/*     */   public double getAttributeValueFromItemStack(ItemStack itemStack, Attribute attribute)
/*     */   {
/*  83 */     if ((itemStack == null) || (attribute == null) || (!attribute.isEnabled())) {
/*  84 */       return 0.0D;
/*     */     }
/*  86 */     List lore = new ArrayList();
/*  87 */     if ((itemStack.hasItemMeta()) && (itemStack.getItemMeta().hasLore())) {
/*  88 */       lore = itemStack.getItemMeta().getLore();
/*     */     }
/*  90 */     return getValue(lore, attribute);
/*     */   }
/*     */ 
/*     */   private double getDoublePercentage(Collection<String> collection, Attribute attribute) {
/*  94 */     double d = 0.0D;
/*  95 */     if ((collection == null) || (collection.isEmpty()) || (attribute == null) || (!attribute.isEnabled())) {
/*  96 */       return d;
/*     */     }
/*  98 */     for (String s : collection) {
/*  99 */       String stripped = ChatColor.stripColor(s);
/* 100 */       String withoutNumbers = stripped.replaceAll("[0-9\\+%\\-]", "").trim();
/* 101 */       String withoutLetters = stripped.replaceAll("[a-zA-Z\\+%:]", "").trim();
/* 102 */       String withoutVariables = attribute.getFormat().replaceAll("%(?s)(.*?)%", "").trim();
/* 103 */       if (withoutNumbers.equals(withoutVariables))
/*     */       {
/* 106 */         if (!s.contains("%")) {
/* 107 */           if (withoutLetters.contains(" - ")) {
/* 108 */             String[] split = withoutLetters.split(" - ");
/* 109 */             double first = NumberUtils.toDouble(split[0], 0.0D);
/* 110 */             double second = NumberUtils.toDouble(split[1], 0.0D);
/* 111 */             d += (RandomUtils.nextDouble() * (Math.max(first, second) - Math.min(first, second)) + Math.min(first, second)) / (attribute.getMaxValue() != 0.0D ? attribute.getMaxValue() : 100.0D);
/*     */           }
/*     */           else
/*     */           {
/* 115 */             d += NumberUtils.toDouble(withoutLetters, 0.0D) / (attribute.getMaxValue() != 0.0D ? attribute.getMaxValue() : 100.0D);
/*     */           }
/*     */ 
/*     */         }
/* 119 */         else if (withoutLetters.contains(" - ")) {
/* 120 */           String[] split = withoutLetters.split(" - ");
/* 121 */           if (split.length > 1) {
/* 122 */             double first = NumberUtils.toDouble(split[0], 0.0D);
/* 123 */             double second = NumberUtils.toDouble(split[1], 0.0D);
/* 124 */             d += (RandomUtils.nextDouble() * (Math.max(first, second) - Math.min(first, second)) + Math.min(first, second)) / 100.0D;
/*     */           }
/*     */         }
/*     */         else {
/* 128 */           d += NumberUtils.toDouble(withoutLetters, 0.0D) / 100.0D;
/*     */         }
/*     */       }
/*     */     }
/* 132 */     return d;
/*     */   }
/*     */ 
/*     */   public double getAttributeValueFromEntity(LivingEntity livingEntity, Attribute attribute)
/*     */   {
/* 137 */     double d = 0.0D;
/* 138 */     if ((livingEntity == null) || (attribute == null) || (!attribute.isEnabled())) {
/* 139 */       return d;
/*     */     }
/* 141 */     for (ItemStack itemStack : livingEntity.getEquipment().getArmorContents()) {
/* 142 */       d += getAttributeValueFromItemStack(itemStack, attribute);
/*     */     }
/* 144 */     d += getAttributeValueFromItemStack(livingEntity.getEquipment().getItemInHand(), attribute);
/* 145 */     return d;
/*     */   }
/*     */ 
/*     */   public Set<Attribute> getAttributesPresentOnItemStack(ItemStack itemStack)
/*     */   {
/* 150 */     Set attributes = new HashSet();
/* 151 */     if (itemStack == null) {
/* 152 */       return attributes;
/*     */     }
/* 154 */     List lore = new ArrayList();
/* 155 */     if ((itemStack.hasItemMeta()) && (itemStack.getItemMeta().hasLore())) {
/* 156 */       lore = itemStack.getItemMeta().getLore();
/*     */     }
/* 158 */     for (Iterator i$ = lore.iterator(); i$.hasNext(); ) { s = (String)i$.next();
/* 159 */       for (Attribute attribute : getPlugin().getSettingsManager().getLoadedAttributes())
/* 160 */         if (attribute.isEnabled())
/*     */         {
/* 163 */           if ((s.contains(attribute.getFormat().replaceAll("%(?s)(.*?)%", "").trim())) && (!attributes.contains(attribute)))
/*     */           {
/* 165 */             attributes.add(attribute);
/*     */           }
/*     */         }
/*     */     }
/*     */     String s;
/* 169 */     return attributes;
/*     */   }
/*     */ 
/*     */   public boolean hasAttributeOnItemStack(ItemStack itemStack, Attribute attribute)
/*     */   {
/* 174 */     if ((itemStack == null) || (attribute == null) || (!attribute.isEnabled())) {
/* 175 */       return false;
/*     */     }
/* 177 */     List lore = new ArrayList();
/* 178 */     if ((itemStack.hasItemMeta()) && (itemStack.getItemMeta().hasLore())) {
/* 179 */       lore = itemStack.getItemMeta().getLore();
/*     */     }
/* 181 */     for (String s : lore) {
/* 182 */       if (ChatColor.stripColor(s).contains(ChatColor.stripColor(attribute.getFormat().replaceAll("%(?s)(.*?)%", "").trim())))
/*     */       {
/* 184 */         return true;
/*     */       }
/*     */     }
/* 187 */     return false;
/*     */   }
/*     */ 
/*     */   public boolean hasAttributeOnEntity(LivingEntity livingEntity, Attribute attribute)
/*     */   {
/* 192 */     if ((livingEntity == null) || (attribute == null) || (!attribute.isEnabled())) {
/* 193 */       return false;
/*     */     }
/* 195 */     for (ItemStack itemStack : livingEntity.getEquipment().getArmorContents()) {
/* 196 */       if (hasAttributeOnItemStack(itemStack, attribute)) {
/* 197 */         return true;
/*     */       }
/*     */     }
/* 200 */     return hasAttributeOnItemStack(livingEntity.getEquipment().getItemInHand(), attribute);
/*     */   }
/*     */ 
/*     */   public ItemAttributes getPlugin()
/*     */   {
/* 205 */     return this.plugin;
/*     */   }
/*     */ 
/*     */   public List<String> getAttributeStringsFromItemStack(ItemStack itemStack, Attribute attribute)
/*     */   {
/* 211 */     List list = new ArrayList();
/* 212 */     if ((itemStack == null) || (attribute == null) || (!attribute.isEnabled())) {
/* 213 */       return list;
/*     */     }
/* 215 */     List lore = new ArrayList();
/* 216 */     if ((itemStack.hasItemMeta()) && (itemStack.getItemMeta().hasLore())) {
/* 217 */       lore = itemStack.getItemMeta().getLore();
/*     */     }
/* 219 */     list = getStrings(lore, attribute);
/* 220 */     return list;
/*     */   }
/*     */ 
/*     */   public List<String> getAttributeStringsFromEntity(LivingEntity livingEntity, Attribute attribute)
/*     */   {
/* 225 */     List list = new ArrayList();
/* 226 */     if ((livingEntity == null) || (attribute == null) || (!attribute.isEnabled())) {
/* 227 */       return list;
/*     */     }
/* 229 */     for (ItemStack itemStack : livingEntity.getEquipment().getArmorContents()) {
/* 230 */       list.addAll(getAttributeStringsFromItemStack(itemStack, attribute));
/*     */     }
/* 232 */     list.addAll(getAttributeStringsFromItemStack(livingEntity.getEquipment().getItemInHand(), attribute));
/* 233 */     return list;
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\ItemAttributes.jar
 * Qualified Name:     net.nunnerycode.bukkit.itemattributes.attributes.ItemAttributeHandler
 * JD-Core Version:    0.6.2
 */