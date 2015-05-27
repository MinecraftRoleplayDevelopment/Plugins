/*     */ package com.comphenix.protocol.wrappers;
/*     */ 
/*     */ import com.comphenix.protocol.PacketType.Play.Server;
/*     */ import com.comphenix.protocol.events.PacketContainer;
/*     */ import com.comphenix.protocol.reflect.FuzzyReflection;
/*     */ import com.comphenix.protocol.reflect.StructureModifier;
/*     */ import com.comphenix.protocol.reflect.fuzzy.FuzzyMethodContract;
/*     */ import com.comphenix.protocol.reflect.fuzzy.FuzzyMethodContract.Builder;
/*     */ import com.comphenix.protocol.utility.MinecraftReflection;
/*     */ import com.comphenix.protocol.wrappers.collection.CachedSet;
/*     */ import com.comphenix.protocol.wrappers.collection.ConvertedSet;
/*     */ import com.google.common.base.Objects;
/*     */ import com.google.common.base.Objects.ToStringHelper;
/*     */ import com.google.common.base.Preconditions;
/*     */ import com.google.common.collect.Sets;
/*     */ import com.google.common.collect.Sets.SetView;
/*     */ import java.lang.reflect.Constructor;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.Set;
/*     */ import java.util.UUID;
/*     */ import javax.annotation.Nonnull;
/*     */ 
/*     */ public class WrappedAttribute extends AbstractWrapper
/*     */ {
/*     */   private static StructureModifier<Object> ATTRIBUTE_MODIFIER;
/*     */   private static Constructor<?> ATTRIBUTE_CONSTRUCTOR;
/*     */   protected Object handle;
/*     */   protected StructureModifier<Object> modifier;
/*  41 */   private double computedValue = (0.0D / 0.0D);
/*     */   private Set<WrappedAttributeModifier> attributeModifiers;
/*     */ 
/*     */   private WrappedAttribute(@Nonnull Object handle)
/*     */   {
/*  51 */     super(MinecraftReflection.getAttributeSnapshotClass());
/*  52 */     setHandle(handle);
/*     */ 
/*  55 */     if (ATTRIBUTE_MODIFIER == null) {
/*  56 */       ATTRIBUTE_MODIFIER = new StructureModifier(MinecraftReflection.getAttributeSnapshotClass());
/*     */     }
/*  58 */     this.modifier = ATTRIBUTE_MODIFIER.withTarget(handle);
/*     */   }
/*     */ 
/*     */   public static WrappedAttribute fromHandle(@Nonnull Object handle)
/*     */   {
/*  69 */     return new WrappedAttribute(handle);
/*     */   }
/*     */ 
/*     */   public static Builder newBuilder()
/*     */   {
/*  77 */     return new Builder(null, null);
/*     */   }
/*     */ 
/*     */   public static Builder newBuilder(@Nonnull WrappedAttribute template)
/*     */   {
/*  86 */     return new Builder((WrappedAttribute)Preconditions.checkNotNull(template, "template cannot be NULL."), null);
/*     */   }
/*     */ 
/*     */   public String getAttributeKey()
/*     */   {
/*  96 */     return (String)this.modifier.withType(String.class).read(0);
/*     */   }
/*     */ 
/*     */   public double getBaseValue()
/*     */   {
/* 104 */     return ((Double)this.modifier.withType(Double.TYPE).read(0)).doubleValue();
/*     */   }
/*     */ 
/*     */   public double getFinalValue()
/*     */   {
/* 112 */     if (Double.isNaN(this.computedValue)) {
/* 113 */       this.computedValue = computeValue();
/*     */     }
/* 115 */     return this.computedValue;
/*     */   }
/*     */ 
/*     */   public PacketContainer getParentPacket()
/*     */   {
/* 123 */     return new PacketContainer(PacketType.Play.Server.UPDATE_ATTRIBUTES, this.modifier.withType(MinecraftReflection.getPacketClass()).read(0));
/*     */   }
/*     */ 
/*     */   public boolean hasModifier(UUID id)
/*     */   {
/* 134 */     return getModifiers().contains(WrappedAttributeModifier.newBuilder(id).build());
/*     */   }
/*     */ 
/*     */   public WrappedAttributeModifier getModifierByUUID(UUID id)
/*     */   {
/* 143 */     if (hasModifier(id)) {
/* 144 */       for (WrappedAttributeModifier modifier : getModifiers()) {
/* 145 */         if (Objects.equal(modifier.getUUID(), id)) {
/* 146 */           return modifier;
/*     */         }
/*     */       }
/*     */     }
/* 150 */     return null;
/*     */   }
/*     */ 
/*     */   public Set<WrappedAttributeModifier> getModifiers()
/*     */   {
/* 158 */     if (this.attributeModifiers == null)
/*     */     {
/* 160 */       Collection collection = (Collection)this.modifier.withType(Collection.class).read(0);
/*     */ 
/* 163 */       ConvertedSet converted = new ConvertedSet(getSetSafely(collection))
/*     */       {
/*     */         protected Object toInner(WrappedAttributeModifier outer)
/*     */         {
/* 167 */           return outer.getHandle();
/*     */         }
/*     */ 
/*     */         protected WrappedAttributeModifier toOuter(Object inner)
/*     */         {
/* 172 */           return WrappedAttributeModifier.fromHandle(inner);
/*     */         }
/*     */       };
/* 176 */       this.attributeModifiers = new CachedSet(converted);
/*     */     }
/* 178 */     return Collections.unmodifiableSet(this.attributeModifiers);
/*     */   }
/*     */ 
/*     */   public WrappedAttribute withModifiers(Collection<WrappedAttributeModifier> modifiers)
/*     */   {
/* 187 */     return newBuilder(this).modifiers(modifiers).build();
/*     */   }
/*     */ 
/*     */   public boolean equals(Object obj)
/*     */   {
/* 192 */     if (this == obj)
/* 193 */       return true;
/* 194 */     if ((obj instanceof WrappedAttribute)) {
/* 195 */       WrappedAttribute other = (WrappedAttribute)obj;
/*     */ 
/* 197 */       return (getBaseValue() == other.getBaseValue()) && (Objects.equal(getAttributeKey(), other.getAttributeKey())) && (Sets.symmetricDifference(getModifiers(), other.getModifiers()).isEmpty());
/*     */     }
/*     */ 
/* 204 */     return false;
/*     */   }
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 209 */     if (this.attributeModifiers == null)
/* 210 */       getModifiers();
/* 211 */     return Objects.hashCode(new Object[] { getAttributeKey(), Double.valueOf(getBaseValue()), this.attributeModifiers });
/*     */   }
/*     */ 
/*     */   private double computeValue()
/*     */   {
/* 219 */     Collection modifiers = getModifiers();
/* 220 */     double x = getBaseValue();
/* 221 */     double y = 0.0D;
/*     */ 
/* 224 */     for (int phase = 0; phase < 3; phase++) {
/* 225 */       for (WrappedAttributeModifier modifier : modifiers) {
/* 226 */         if (modifier.getOperation().getId() == phase) {
/* 227 */           switch (phase) {
/*     */           case 0:
/* 229 */             x += modifier.getAmount();
/* 230 */             break;
/*     */           case 1:
/* 232 */             y += x * modifier.getAmount();
/* 233 */             break;
/*     */           case 2:
/* 235 */             y *= (1.0D + modifier.getAmount());
/* 236 */             break;
/*     */           default:
/* 238 */             throw new IllegalStateException("Unknown phase: " + phase);
/*     */           }
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 244 */       if (phase == 0) {
/* 245 */         y = x;
/*     */       }
/*     */     }
/* 248 */     return y;
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 253 */     return Objects.toStringHelper("WrappedAttribute").add("key", getAttributeKey()).add("baseValue", getBaseValue()).add("finalValue", getFinalValue()).add("modifiers", getModifiers()).toString();
/*     */   }
/*     */ 
/*     */   private static <U> Set<U> getSetSafely(Collection<U> collection)
/*     */   {
/* 267 */     return (collection instanceof Set) ? (Set)collection : Sets.newHashSet(collection);
/*     */   }
/*     */ 
/*     */   static double checkDouble(double value)
/*     */   {
/* 275 */     if (Double.isInfinite(value))
/* 276 */       throw new IllegalArgumentException("value cannot be infinite.");
/* 277 */     if (Double.isNaN(value))
/* 278 */       throw new IllegalArgumentException("value cannot be NaN.");
/* 279 */     return value;
/*     */   }
/*     */ 
/*     */   public static class Builder
/*     */   {
/* 289 */     private double baseValue = (0.0D / 0.0D);
/*     */     private String attributeKey;
/*     */     private PacketContainer packet;
/* 292 */     private Collection<WrappedAttributeModifier> modifiers = Collections.emptyList();
/*     */ 
/*     */     private Builder(WrappedAttribute template) {
/* 295 */       if (template != null) {
/* 296 */         this.baseValue = template.getBaseValue();
/* 297 */         this.attributeKey = template.getAttributeKey();
/* 298 */         this.packet = template.getParentPacket();
/* 299 */         this.modifiers = template.getModifiers();
/*     */       }
/*     */     }
/*     */ 
/*     */     public Builder baseValue(double baseValue)
/*     */     {
/* 311 */       this.baseValue = WrappedAttribute.checkDouble(baseValue);
/* 312 */       return this;
/*     */     }
/*     */ 
/*     */     public Builder attributeKey(String attributeKey)
/*     */     {
/* 323 */       this.attributeKey = ((String)Preconditions.checkNotNull(attributeKey, "attributeKey cannot be NULL."));
/* 324 */       return this;
/*     */     }
/*     */ 
/*     */     public Builder modifiers(Collection<WrappedAttributeModifier> modifiers)
/*     */     {
/* 333 */       this.modifiers = ((Collection)Preconditions.checkNotNull(modifiers, "modifiers cannot be NULL - use an empty list instead."));
/* 334 */       return this;
/*     */     }
/*     */ 
/*     */     public Builder packet(PacketContainer packet)
/*     */     {
/* 343 */       if (((PacketContainer)Preconditions.checkNotNull(packet, "packet cannot be NULL")).getType() != PacketType.Play.Server.UPDATE_ATTRIBUTES) {
/* 344 */         throw new IllegalArgumentException("Packet must be UPDATE_ATTRIBUTES (44)");
/*     */       }
/* 346 */       this.packet = packet;
/* 347 */       return this;
/*     */     }
/*     */ 
/*     */     private Set<Object> getUnwrappedModifiers()
/*     */     {
/* 355 */       Set output = Sets.newHashSet();
/*     */ 
/* 357 */       for (WrappedAttributeModifier modifier : this.modifiers) {
/* 358 */         output.add(modifier.getHandle());
/*     */       }
/* 360 */       return output;
/*     */     }
/*     */ 
/*     */     public WrappedAttribute build()
/*     */     {
/* 369 */       Preconditions.checkNotNull(this.packet, "packet cannot be NULL.");
/* 370 */       Preconditions.checkNotNull(this.attributeKey, "attributeKey cannot be NULL.");
/*     */ 
/* 373 */       if (Double.isNaN(this.baseValue)) {
/* 374 */         throw new IllegalStateException("Base value has not been set.");
/*     */       }
/*     */ 
/* 378 */       if (WrappedAttribute.ATTRIBUTE_CONSTRUCTOR == null) {
/* 379 */         WrappedAttribute.access$102(FuzzyReflection.fromClass(MinecraftReflection.getAttributeSnapshotClass(), true).getConstructor(FuzzyMethodContract.newBuilder().parameterCount(4).parameterDerivedOf(MinecraftReflection.getPacketClass(), 0).parameterExactType(String.class, 1).parameterExactType(Double.TYPE, 2).parameterDerivedOf(Collection.class, 3).build()));
/*     */ 
/* 388 */         WrappedAttribute.ATTRIBUTE_CONSTRUCTOR.setAccessible(true);
/*     */       }
/*     */       try
/*     */       {
/* 392 */         Object handle = WrappedAttribute.ATTRIBUTE_CONSTRUCTOR.newInstance(new Object[] { this.packet.getHandle(), this.attributeKey, Double.valueOf(this.baseValue), getUnwrappedModifiers() });
/*     */ 
/* 399 */         return new WrappedAttribute(handle, null);
/*     */       }
/*     */       catch (Exception e) {
/* 402 */         throw new RuntimeException("Cannot construct AttributeSnapshot.", e);
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.wrappers.WrappedAttribute
 * JD-Core Version:    0.6.2
 */