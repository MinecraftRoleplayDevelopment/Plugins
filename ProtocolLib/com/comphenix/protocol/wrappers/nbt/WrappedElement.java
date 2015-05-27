/*     */ package com.comphenix.protocol.wrappers.nbt;
/*     */ 
/*     */ import com.comphenix.protocol.reflect.FieldAccessException;
/*     */ import com.comphenix.protocol.reflect.FuzzyReflection;
/*     */ import com.comphenix.protocol.reflect.StructureModifier;
/*     */ import com.comphenix.protocol.utility.MinecraftReflection;
/*     */ import com.comphenix.protocol.wrappers.nbt.io.NbtBinarySerializer;
/*     */ import com.google.common.base.Objects;
/*     */ import java.io.DataOutput;
/*     */ import java.lang.reflect.Method;
/*     */ 
/*     */ class WrappedElement<TType>
/*     */   implements NbtWrapper<TType>
/*     */ {
/*     */   private static volatile Method methodGetTypeID;
/*     */   private static volatile Method methodClone;
/*     */   private static volatile Boolean hasNbtName;
/*  46 */   private static StructureModifier<?>[] modifiers = new StructureModifier[NbtType.values().length];
/*     */   private Object handle;
/*     */   private NbtType type;
/*     */   private NameProperty nameProperty;
/*     */ 
/*     */   public WrappedElement(Object handle)
/*     */   {
/*  62 */     this.handle = handle;
/*  63 */     initializeProperty();
/*     */   }
/*     */ 
/*     */   public WrappedElement(Object handle, String name)
/*     */   {
/*  71 */     this.handle = handle;
/*  72 */     initializeProperty();
/*  73 */     setName(name);
/*     */   }
/*     */ 
/*     */   private void initializeProperty() {
/*  77 */     if (this.nameProperty == null) {
/*  78 */       Class base = MinecraftReflection.getNBTBaseClass();
/*     */ 
/*  81 */       if (hasNbtName == null) {
/*  82 */         hasNbtName = Boolean.valueOf(NameProperty.hasStringIndex(base, 0));
/*     */       }
/*     */ 
/*  86 */       if (hasNbtName.booleanValue())
/*  87 */         this.nameProperty = NameProperty.fromStringIndex(base, this.handle, 0);
/*     */       else
/*  89 */         this.nameProperty = NameProperty.fromBean();
/*     */     }
/*     */   }
/*     */ 
/*     */   protected StructureModifier<TType> getCurrentModifier()
/*     */   {
/*  98 */     NbtType type = getType();
/*     */ 
/* 100 */     return getCurrentBaseModifier().withType(type.getValueType());
/*     */   }
/*     */ 
/*     */   protected StructureModifier<Object> getCurrentBaseModifier()
/*     */   {
/* 109 */     int index = getType().ordinal();
/* 110 */     StructureModifier modifier = modifiers[index];
/*     */ 
/* 113 */     if (modifier == null) {
/* 114 */       synchronized (this) {
/* 115 */         if (modifiers[index] == null) {
/* 116 */           modifiers[index] = new StructureModifier(this.handle.getClass(), MinecraftReflection.getNBTBaseClass(), false);
/*     */         }
/* 118 */         modifier = modifiers[index];
/*     */       }
/*     */     }
/*     */ 
/* 122 */     return modifier;
/*     */   }
/*     */ 
/*     */   public boolean accept(NbtVisitor visitor)
/*     */   {
/* 127 */     return visitor.visit(this);
/*     */   }
/*     */ 
/*     */   public Object getHandle()
/*     */   {
/* 136 */     return this.handle;
/*     */   }
/*     */ 
/*     */   public NbtType getType()
/*     */   {
/* 141 */     if (methodGetTypeID == null)
/*     */     {
/* 143 */       methodGetTypeID = FuzzyReflection.fromClass(MinecraftReflection.getNBTBaseClass()).getMethodByParameters("getTypeID", Byte.TYPE, new Class[0]);
/*     */     }
/*     */ 
/* 146 */     if (this.type == null) {
/*     */       try {
/* 148 */         this.type = NbtType.getTypeFromID(((Byte)methodGetTypeID.invoke(this.handle, new Object[0])).byteValue());
/*     */       } catch (Exception e) {
/* 150 */         throw new FieldAccessException("Cannot get NBT type of " + this.handle, e);
/*     */       }
/*     */     }
/*     */ 
/* 154 */     return this.type;
/*     */   }
/*     */ 
/*     */   public NbtType getSubType()
/*     */   {
/* 162 */     int subID = ((Byte)getCurrentBaseModifier().withType(Byte.TYPE).withTarget(this.handle).read(0)).byteValue();
/* 163 */     return NbtType.getTypeFromID(subID);
/*     */   }
/*     */ 
/*     */   public void setSubType(NbtType type)
/*     */   {
/* 171 */     byte subID = (byte)type.getRawID();
/* 172 */     getCurrentBaseModifier().withType(Byte.TYPE).withTarget(this.handle).write(0, Byte.valueOf(subID));
/*     */   }
/*     */ 
/*     */   public String getName()
/*     */   {
/* 177 */     return this.nameProperty.getName();
/*     */   }
/*     */ 
/*     */   public void setName(String name)
/*     */   {
/* 182 */     this.nameProperty.setName(name);
/*     */   }
/*     */ 
/*     */   public TType getValue()
/*     */   {
/* 187 */     return getCurrentModifier().withTarget(this.handle).read(0);
/*     */   }
/*     */ 
/*     */   public void setValue(TType newValue)
/*     */   {
/* 192 */     getCurrentModifier().withTarget(this.handle).write(0, newValue);
/*     */   }
/*     */ 
/*     */   public void write(DataOutput destination)
/*     */   {
/* 198 */     NbtBinarySerializer.DEFAULT.serialize(this, destination);
/*     */   }
/*     */ 
/*     */   public NbtBase<TType> deepClone()
/*     */   {
/* 203 */     if (methodClone == null) {
/* 204 */       Class base = MinecraftReflection.getNBTBaseClass();
/*     */ 
/* 207 */       methodClone = FuzzyReflection.fromClass(base).getMethodByParameters("clone", base, new Class[0]);
/*     */     }
/*     */ 
/*     */     try
/*     */     {
/* 212 */       return NbtFactory.fromNMS(methodClone.invoke(this.handle, new Object[0]), getName());
/*     */     } catch (Exception e) {
/* 214 */       throw new FieldAccessException("Unable to clone " + this.handle, e);
/*     */     }
/*     */   }
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 220 */     return Objects.hashCode(new Object[] { getName(), getType(), getValue() });
/*     */   }
/*     */ 
/*     */   public boolean equals(Object obj)
/*     */   {
/* 225 */     if ((obj instanceof NbtBase)) {
/* 226 */       NbtBase other = (NbtBase)obj;
/*     */ 
/* 229 */       if (other.getType().equals(getType())) {
/* 230 */         return Objects.equal(getValue(), other.getValue());
/*     */       }
/*     */     }
/* 233 */     return false;
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 238 */     StringBuilder result = new StringBuilder();
/* 239 */     String name = getName();
/*     */ 
/* 241 */     result.append("{");
/*     */ 
/* 243 */     if ((name != null) && (name.length() > 0)) {
/* 244 */       result.append("name: '" + name + "', ");
/*     */     }
/* 246 */     result.append("value: ");
/*     */ 
/* 249 */     if (getType() == NbtType.TAG_STRING)
/* 250 */       result.append("'" + getValue() + "'");
/*     */     else {
/* 252 */       result.append(getValue());
/*     */     }
/* 254 */     result.append("}");
/* 255 */     return result.toString();
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.wrappers.nbt.WrappedElement
 * JD-Core Version:    0.6.2
 */