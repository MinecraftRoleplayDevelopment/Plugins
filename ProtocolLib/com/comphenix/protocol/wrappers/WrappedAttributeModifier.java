/*     */ package com.comphenix.protocol.wrappers;
/*     */ 
/*     */ import com.comphenix.protocol.reflect.FuzzyReflection;
/*     */ import com.comphenix.protocol.reflect.StructureModifier;
/*     */ import com.comphenix.protocol.reflect.fuzzy.FuzzyMethodContract;
/*     */ import com.comphenix.protocol.reflect.fuzzy.FuzzyMethodContract.Builder;
/*     */ import com.comphenix.protocol.utility.MinecraftReflection;
/*     */ import com.google.common.base.Objects;
/*     */ import com.google.common.base.Preconditions;
/*     */ import java.lang.reflect.Constructor;
/*     */ import java.util.UUID;
/*     */ import javax.annotation.Nonnull;
/*     */ 
/*     */ public class WrappedAttributeModifier extends AbstractWrapper
/*     */ {
/*     */   private static StructureModifier<Object> BASE_MODIFIER;
/*     */   private static Constructor<?> ATTRIBUTE_MODIFIER_CONSTRUCTOR;
/*     */   protected StructureModifier<Object> modifier;
/*     */   private final UUID uuid;
/*     */   private final String name;
/*     */   private final Operation operation;
/*     */   private final double amount;
/*     */ 
/*     */   protected WrappedAttributeModifier(UUID uuid, String name, double amount, Operation operation)
/*     */   {
/* 108 */     super(MinecraftReflection.getAttributeModifierClass());
/*     */ 
/* 111 */     this.uuid = uuid;
/* 112 */     this.name = name;
/* 113 */     this.amount = amount;
/* 114 */     this.operation = operation;
/*     */   }
/*     */ 
/*     */   protected WrappedAttributeModifier(@Nonnull Object handle)
/*     */   {
/* 123 */     super(MinecraftReflection.getAttributeModifierClass());
/* 124 */     setHandle(handle);
/* 125 */     initializeModifier(handle);
/*     */ 
/* 128 */     this.uuid = ((UUID)this.modifier.withType(UUID.class).read(0));
/* 129 */     this.name = ((String)this.modifier.withType(String.class).read(0));
/* 130 */     this.amount = ((Double)this.modifier.withType(Double.TYPE).read(0)).doubleValue();
/* 131 */     this.operation = Operation.fromId(((Integer)this.modifier.withType(Integer.TYPE).read(0)).intValue());
/*     */   }
/*     */ 
/*     */   protected WrappedAttributeModifier(@Nonnull Object handle, UUID uuid, String name, double amount, Operation operation)
/*     */   {
/* 143 */     this(uuid, name, amount, operation);
/*     */ 
/* 146 */     setHandle(handle);
/* 147 */     initializeModifier(handle);
/*     */   }
/*     */ 
/*     */   public static Builder newBuilder()
/*     */   {
/* 157 */     return new Builder(null, null).uuid(UUID.randomUUID());
/*     */   }
/*     */ 
/*     */   public static Builder newBuilder(UUID id)
/*     */   {
/* 166 */     return new Builder(null, null).uuid(id);
/*     */   }
/*     */ 
/*     */   public static Builder newBuilder(@Nonnull WrappedAttributeModifier template)
/*     */   {
/* 175 */     return new Builder((WrappedAttributeModifier)Preconditions.checkNotNull(template, "template cannot be NULL."), null);
/*     */   }
/*     */ 
/*     */   public static WrappedAttributeModifier fromHandle(@Nonnull Object handle)
/*     */   {
/* 185 */     return new WrappedAttributeModifier(handle);
/*     */   }
/*     */ 
/*     */   private void initializeModifier(@Nonnull Object handle)
/*     */   {
/* 195 */     if (BASE_MODIFIER == null) {
/* 196 */       BASE_MODIFIER = new StructureModifier(MinecraftReflection.getAttributeModifierClass());
/*     */     }
/* 198 */     this.modifier = BASE_MODIFIER.withTarget(handle);
/*     */   }
/*     */ 
/*     */   public UUID getUUID()
/*     */   {
/* 206 */     return this.uuid;
/*     */   }
/*     */ 
/*     */   public String getName()
/*     */   {
/* 216 */     return this.name;
/*     */   }
/*     */ 
/*     */   public Operation getOperation()
/*     */   {
/* 224 */     return this.operation;
/*     */   }
/*     */ 
/*     */   public double getAmount()
/*     */   {
/* 232 */     return this.amount;
/*     */   }
/*     */ 
/*     */   public Object getHandle()
/*     */   {
/* 240 */     return this.handle;
/*     */   }
/*     */ 
/*     */   public void setPendingSynchronization(boolean pending)
/*     */   {
/* 250 */     this.modifier.withType(Boolean.TYPE).write(0, Boolean.valueOf(pending));
/*     */   }
/*     */ 
/*     */   public boolean isPendingSynchronization()
/*     */   {
/* 258 */     return ((Boolean)this.modifier.withType(Boolean.TYPE).read(0)).booleanValue();
/*     */   }
/*     */ 
/*     */   public boolean equals(Object obj)
/*     */   {
/* 269 */     if (obj == this)
/* 270 */       return true;
/* 271 */     if ((obj instanceof WrappedAttributeModifier)) {
/* 272 */       WrappedAttributeModifier other = (WrappedAttributeModifier)obj;
/*     */ 
/* 275 */       return Objects.equal(this.uuid, other.getUUID());
/*     */     }
/* 277 */     return false;
/*     */   }
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 282 */     return this.uuid != null ? this.uuid.hashCode() : 0;
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 287 */     return "[amount=" + this.amount + ", operation=" + this.operation + ", name='" + this.name + "', id=" + this.uuid + ", serialize=" + isPendingSynchronization() + "]";
/*     */   }
/*     */ 
/*     */   public static class Builder
/*     */   {
/* 297 */     private WrappedAttributeModifier.Operation operation = WrappedAttributeModifier.Operation.ADD_NUMBER;
/* 298 */     private String name = "Unknown";
/*     */     private double amount;
/*     */     private UUID uuid;
/*     */ 
/*     */     private Builder(WrappedAttributeModifier template)
/*     */     {
/* 303 */       if (template != null) {
/* 304 */         this.operation = template.getOperation();
/* 305 */         this.name = template.getName();
/* 306 */         this.amount = template.getAmount();
/* 307 */         this.uuid = template.getUUID();
/*     */       }
/*     */     }
/*     */ 
/*     */     public Builder uuid(@Nonnull UUID uuid)
/*     */     {
/* 321 */       this.uuid = ((UUID)Preconditions.checkNotNull(uuid, "uuid cannot be NULL."));
/* 322 */       return this;
/*     */     }
/*     */ 
/*     */     public Builder operation(@Nonnull WrappedAttributeModifier.Operation operation)
/*     */     {
/* 332 */       this.operation = ((WrappedAttributeModifier.Operation)Preconditions.checkNotNull(operation, "operation cannot be NULL."));
/* 333 */       return this;
/*     */     }
/*     */ 
/*     */     public Builder name(@Nonnull String name)
/*     */     {
/* 342 */       this.name = ((String)Preconditions.checkNotNull(name, "name cannot be NULL."));
/* 343 */       return this;
/*     */     }
/*     */ 
/*     */     public Builder amount(double amount)
/*     */     {
/* 353 */       this.amount = WrappedAttribute.checkDouble(amount);
/* 354 */       return this;
/*     */     }
/*     */ 
/*     */     public WrappedAttributeModifier build()
/*     */     {
/* 364 */       Preconditions.checkNotNull(this.uuid, "uuid cannot be NULL.");
/*     */ 
/* 367 */       if (WrappedAttributeModifier.ATTRIBUTE_MODIFIER_CONSTRUCTOR == null) {
/* 368 */         WrappedAttributeModifier.access$102(FuzzyReflection.fromClass(MinecraftReflection.getAttributeModifierClass(), true).getConstructor(FuzzyMethodContract.newBuilder().parameterCount(4).parameterDerivedOf(UUID.class, 0).parameterExactType(String.class, 1).parameterExactType(Double.TYPE, 2).parameterExactType(Integer.TYPE, 3).build()));
/*     */ 
/* 377 */         WrappedAttributeModifier.ATTRIBUTE_MODIFIER_CONSTRUCTOR.setAccessible(true);
/*     */       }
/*     */ 
/*     */       try
/*     */       {
/* 383 */         return new WrappedAttributeModifier(WrappedAttributeModifier.ATTRIBUTE_MODIFIER_CONSTRUCTOR.newInstance(new Object[] { this.uuid, this.name, Double.valueOf(this.amount), Integer.valueOf(this.operation.getId()) }), this.uuid, this.name, this.amount, this.operation);
/*     */       }
/*     */       catch (Exception e)
/*     */       {
/* 389 */         throw new RuntimeException("Cannot construct AttributeModifier.", e);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public static enum Operation
/*     */   {
/*  41 */     ADD_NUMBER(0), 
/*     */ 
/*  46 */     MULTIPLY_PERCENTAGE(1), 
/*     */ 
/*  51 */     ADD_PERCENTAGE(2);
/*     */ 
/*     */     private int id;
/*     */ 
/*     */     private Operation(int id) {
/*  56 */       this.id = id;
/*     */     }
/*     */ 
/*     */     public int getId()
/*     */     {
/*  64 */       return this.id;
/*     */     }
/*     */ 
/*     */     public static Operation fromId(int id)
/*     */     {
/*  74 */       for (Operation op : values()) {
/*  75 */         if (op.getId() == id) {
/*  76 */           return op;
/*     */         }
/*     */       }
/*  79 */       throw new IllegalArgumentException("Corrupt operation ID " + id + " detected.");
/*     */     }
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.wrappers.WrappedAttributeModifier
 * JD-Core Version:    0.6.2
 */