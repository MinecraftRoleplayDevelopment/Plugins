/*     */ package com.comphenix.protocol.wrappers.nbt;
/*     */ 
/*     */ import com.comphenix.protocol.wrappers.collection.ConvertedMap;
/*     */ import com.comphenix.protocol.wrappers.nbt.io.NbtBinarySerializer;
/*     */ import java.io.DataOutput;
/*     */ import java.util.Collection;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.Set;
/*     */ 
/*     */ class WrappedCompound
/*     */   implements NbtWrapper<Map<String, NbtBase<?>>>, Iterable<NbtBase<?>>, NbtCompound
/*     */ {
/*     */   private WrappedElement<Map<String, Object>> container;
/*     */   private ConvertedMap<String, Object, NbtBase<?>> savedMap;
/*     */ 
/*     */   public static WrappedCompound fromName(String name)
/*     */   {
/*  48 */     return (WrappedCompound)NbtFactory.ofWrapper(NbtType.TAG_COMPOUND, name);
/*     */   }
/*     */ 
/*     */   public static NbtCompound fromList(String name, Collection<? extends NbtBase<?>> list)
/*     */   {
/*  58 */     WrappedCompound copy = fromName(name);
/*     */ 
/*  60 */     for (NbtBase base : list)
/*  61 */       copy.getValue().put(base.getName(), base);
/*  62 */     return copy;
/*     */   }
/*     */ 
/*     */   public WrappedCompound(Object handle)
/*     */   {
/*  70 */     this.container = new WrappedElement(handle);
/*     */   }
/*     */ 
/*     */   public WrappedCompound(Object handle, String name)
/*     */   {
/*  79 */     this.container = new WrappedElement(handle, name);
/*     */   }
/*     */ 
/*     */   public boolean accept(NbtVisitor visitor)
/*     */   {
/*  85 */     if (visitor.visitEnter(this)) {
/*  86 */       for (NbtBase node : this) {
/*  87 */         if (!node.accept(visitor)) {
/*     */           break;
/*     */         }
/*     */       }
/*     */     }
/*  92 */     return visitor.visitLeave(this);
/*     */   }
/*     */ 
/*     */   public Object getHandle()
/*     */   {
/*  97 */     return this.container.getHandle();
/*     */   }
/*     */ 
/*     */   public NbtType getType()
/*     */   {
/* 102 */     return NbtType.TAG_COMPOUND;
/*     */   }
/*     */ 
/*     */   public String getName()
/*     */   {
/* 107 */     return this.container.getName();
/*     */   }
/*     */ 
/*     */   public void setName(String name)
/*     */   {
/* 112 */     this.container.setName(name);
/*     */   }
/*     */ 
/*     */   public boolean containsKey(String key)
/*     */   {
/* 121 */     return getValue().containsKey(key);
/*     */   }
/*     */ 
/*     */   public Set<String> getKeys()
/*     */   {
/* 130 */     return getValue().keySet();
/*     */   }
/*     */ 
/*     */   public Map<String, NbtBase<?>> getValue()
/*     */   {
/* 136 */     if (this.savedMap == null) {
/* 137 */       this.savedMap = new ConvertedMap((Map)this.container.getValue())
/*     */       {
/*     */         protected Object toInner(NbtBase<?> outer) {
/* 140 */           if (outer == null)
/* 141 */             return null;
/* 142 */           return NbtFactory.fromBase(outer).getHandle();
/*     */         }
/*     */ 
/*     */         protected NbtBase<?> toOuter(Object inner)
/*     */         {
/* 147 */           if (inner == null)
/* 148 */             return null;
/* 149 */           return NbtFactory.fromNMS(inner);
/*     */         }
/*     */ 
/*     */         protected NbtBase<?> toOuter(String key, Object inner)
/*     */         {
/* 154 */           if (inner == null)
/* 155 */             return null;
/* 156 */           return NbtFactory.fromNMS(inner, key);
/*     */         }
/*     */ 
/*     */         public String toString()
/*     */         {
/* 161 */           return WrappedCompound.this.toString();
/*     */         }
/*     */       };
/*     */     }
/* 165 */     return this.savedMap;
/*     */   }
/*     */ 
/*     */   public void setValue(Map<String, NbtBase<?>> newValue)
/*     */   {
/* 171 */     for (Map.Entry entry : newValue.entrySet()) {
/* 172 */       Object value = entry.getValue();
/*     */ 
/* 175 */       if ((value instanceof NbtBase))
/* 176 */         put((NbtBase)entry.getValue());
/*     */       else
/* 178 */         putObject((String)entry.getKey(), entry.getValue());
/*     */     }
/*     */   }
/*     */ 
/*     */   public <T> NbtBase<T> getValue(String key)
/*     */   {
/* 190 */     return (NbtBase)getValue().get(key);
/*     */   }
/*     */ 
/*     */   public NbtBase<?> getValueOrDefault(String key, NbtType type)
/*     */   {
/* 201 */     NbtBase nbt = getValue(key);
/*     */ 
/* 204 */     if (nbt == null)
/* 205 */       put(nbt = NbtFactory.ofWrapper(type, key));
/* 206 */     else if (nbt.getType() != type) {
/* 207 */       throw new IllegalArgumentException("Cannot get tag " + nbt + ": Not a " + type);
/*     */     }
/* 209 */     return nbt;
/*     */   }
/*     */ 
/*     */   private <T> NbtBase<T> getValueExact(String key)
/*     */   {
/* 219 */     NbtBase value = getValue(key);
/*     */ 
/* 222 */     if (value != null) {
/* 223 */       return value;
/*     */     }
/* 225 */     throw new IllegalArgumentException("Cannot find key " + key);
/*     */   }
/*     */ 
/*     */   public NbtBase<Map<String, NbtBase<?>>> deepClone()
/*     */   {
/* 231 */     return this.container.deepClone();
/*     */   }
/*     */ 
/*     */   public <T> NbtCompound put(NbtBase<T> entry)
/*     */   {
/* 241 */     if (entry == null) {
/* 242 */       throw new IllegalArgumentException("Entry cannot be NULL.");
/*     */     }
/* 244 */     getValue().put(entry.getName(), entry);
/* 245 */     return this;
/*     */   }
/*     */ 
/*     */   public String getString(String key)
/*     */   {
/* 256 */     return (String)getValueExact(key).getValue();
/*     */   }
/*     */ 
/*     */   public String getStringOrDefault(String key)
/*     */   {
/* 266 */     return (String)getValueOrDefault(key, NbtType.TAG_STRING).getValue();
/*     */   }
/*     */ 
/*     */   public NbtCompound put(String key, String value)
/*     */   {
/* 277 */     getValue().put(key, NbtFactory.of(key, value));
/* 278 */     return this;
/*     */   }
/*     */ 
/*     */   public NbtCompound putObject(String key, Object value)
/*     */   {
/* 283 */     if (value == null) {
/* 284 */       remove(key);
/* 285 */     } else if ((value instanceof NbtBase)) {
/* 286 */       put(key, (NbtBase)value);
/*     */     } else {
/* 288 */       NbtBase base = new MemoryElement(key, value);
/* 289 */       put(base);
/*     */     }
/* 291 */     return this;
/*     */   }
/*     */ 
/*     */   public Object getObject(String key)
/*     */   {
/* 296 */     NbtBase base = getValue(key);
/*     */ 
/* 298 */     if ((base != null) && (base.getType() != NbtType.TAG_LIST) && (base.getType() != NbtType.TAG_COMPOUND)) {
/* 299 */       return base.getValue();
/*     */     }
/* 301 */     return base;
/*     */   }
/*     */ 
/*     */   public byte getByte(String key)
/*     */   {
/* 312 */     return ((Byte)getValueExact(key).getValue()).byteValue();
/*     */   }
/*     */ 
/*     */   public byte getByteOrDefault(String key)
/*     */   {
/* 322 */     return ((Byte)getValueOrDefault(key, NbtType.TAG_BYTE).getValue()).byteValue();
/*     */   }
/*     */ 
/*     */   public NbtCompound put(String key, byte value)
/*     */   {
/* 333 */     getValue().put(key, NbtFactory.of(key, value));
/* 334 */     return this;
/*     */   }
/*     */ 
/*     */   public Short getShort(String key)
/*     */   {
/* 345 */     return (Short)getValueExact(key).getValue();
/*     */   }
/*     */ 
/*     */   public short getShortOrDefault(String key)
/*     */   {
/* 355 */     return ((Short)getValueOrDefault(key, NbtType.TAG_SHORT).getValue()).shortValue();
/*     */   }
/*     */ 
/*     */   public NbtCompound put(String key, short value)
/*     */   {
/* 366 */     getValue().put(key, NbtFactory.of(key, value));
/* 367 */     return this;
/*     */   }
/*     */ 
/*     */   public int getInteger(String key)
/*     */   {
/* 378 */     return ((Integer)getValueExact(key).getValue()).intValue();
/*     */   }
/*     */ 
/*     */   public int getIntegerOrDefault(String key)
/*     */   {
/* 388 */     return ((Integer)getValueOrDefault(key, NbtType.TAG_INT).getValue()).intValue();
/*     */   }
/*     */ 
/*     */   public NbtCompound put(String key, int value)
/*     */   {
/* 399 */     getValue().put(key, NbtFactory.of(key, value));
/* 400 */     return this;
/*     */   }
/*     */ 
/*     */   public long getLong(String key)
/*     */   {
/* 411 */     return ((Long)getValueExact(key).getValue()).longValue();
/*     */   }
/*     */ 
/*     */   public long getLongOrDefault(String key)
/*     */   {
/* 421 */     return ((Long)getValueOrDefault(key, NbtType.TAG_LONG).getValue()).longValue();
/*     */   }
/*     */ 
/*     */   public NbtCompound put(String key, long value)
/*     */   {
/* 432 */     getValue().put(key, NbtFactory.of(key, value));
/* 433 */     return this;
/*     */   }
/*     */ 
/*     */   public float getFloat(String key)
/*     */   {
/* 444 */     return ((Float)getValueExact(key).getValue()).floatValue();
/*     */   }
/*     */ 
/*     */   public float getFloatOrDefault(String key)
/*     */   {
/* 454 */     return ((Float)getValueOrDefault(key, NbtType.TAG_FLOAT).getValue()).floatValue();
/*     */   }
/*     */ 
/*     */   public NbtCompound put(String key, float value)
/*     */   {
/* 465 */     getValue().put(key, NbtFactory.of(key, value));
/* 466 */     return this;
/*     */   }
/*     */ 
/*     */   public double getDouble(String key)
/*     */   {
/* 477 */     return ((Double)getValueExact(key).getValue()).doubleValue();
/*     */   }
/*     */ 
/*     */   public double getDoubleOrDefault(String key)
/*     */   {
/* 487 */     return ((Double)getValueOrDefault(key, NbtType.TAG_DOUBLE).getValue()).doubleValue();
/*     */   }
/*     */ 
/*     */   public NbtCompound put(String key, double value)
/*     */   {
/* 498 */     getValue().put(key, NbtFactory.of(key, value));
/* 499 */     return this;
/*     */   }
/*     */ 
/*     */   public byte[] getByteArray(String key)
/*     */   {
/* 510 */     return (byte[])getValueExact(key).getValue();
/*     */   }
/*     */ 
/*     */   public NbtCompound put(String key, byte[] value)
/*     */   {
/* 521 */     getValue().put(key, NbtFactory.of(key, value));
/* 522 */     return this;
/*     */   }
/*     */ 
/*     */   public int[] getIntegerArray(String key)
/*     */   {
/* 533 */     return (int[])getValueExact(key).getValue();
/*     */   }
/*     */ 
/*     */   public NbtCompound put(String key, int[] value)
/*     */   {
/* 544 */     getValue().put(key, NbtFactory.of(key, value));
/* 545 */     return this;
/*     */   }
/*     */ 
/*     */   public NbtCompound getCompound(String key)
/*     */   {
/* 557 */     return (NbtCompound)getValueExact(key);
/*     */   }
/*     */ 
/*     */   public NbtCompound getCompoundOrDefault(String key)
/*     */   {
/* 567 */     return (NbtCompound)getValueOrDefault(key, NbtType.TAG_COMPOUND);
/*     */   }
/*     */ 
/*     */   public NbtCompound put(NbtCompound compound)
/*     */   {
/* 577 */     getValue().put(compound.getName(), compound);
/* 578 */     return this;
/*     */   }
/*     */ 
/*     */   public <T> NbtList<T> getList(String key)
/*     */   {
/* 590 */     return (NbtList)getValueExact(key);
/*     */   }
/*     */ 
/*     */   public <T> NbtList<T> getListOrDefault(String key)
/*     */   {
/* 601 */     return (NbtList)getValueOrDefault(key, NbtType.TAG_LIST);
/*     */   }
/*     */ 
/*     */   public <T> NbtCompound put(NbtList<T> list)
/*     */   {
/* 611 */     getValue().put(list.getName(), list);
/* 612 */     return this;
/*     */   }
/*     */ 
/*     */   public NbtCompound put(String key, NbtBase<?> entry)
/*     */   {
/* 617 */     if (entry == null) {
/* 618 */       throw new IllegalArgumentException("Entry cannot be NULL.");
/*     */     }
/*     */ 
/* 621 */     NbtBase clone = entry.deepClone();
/*     */ 
/* 623 */     clone.setName(key);
/* 624 */     return put(clone);
/*     */   }
/*     */ 
/*     */   public <T> NbtCompound put(String key, Collection<? extends NbtBase<T>> list)
/*     */   {
/* 635 */     return put(WrappedList.fromList(key, list));
/*     */   }
/*     */ 
/*     */   public <T> NbtBase<?> remove(String key)
/*     */   {
/* 640 */     return (NbtBase)getValue().remove(key);
/*     */   }
/*     */ 
/*     */   public void write(DataOutput destination)
/*     */   {
/* 645 */     NbtBinarySerializer.DEFAULT.serialize(this.container, destination);
/*     */   }
/*     */ 
/*     */   public boolean equals(Object obj)
/*     */   {
/* 650 */     if ((obj instanceof WrappedCompound)) {
/* 651 */       WrappedCompound other = (WrappedCompound)obj;
/* 652 */       return this.container.equals(other.container);
/*     */     }
/* 654 */     return false;
/*     */   }
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 659 */     return this.container.hashCode();
/*     */   }
/*     */ 
/*     */   public Iterator<NbtBase<?>> iterator()
/*     */   {
/* 664 */     return getValue().values().iterator();
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 669 */     StringBuilder builder = new StringBuilder();
/*     */ 
/* 671 */     builder.append("{");
/* 672 */     builder.append("\"name\": \"" + getName() + "\"");
/*     */ 
/* 674 */     for (NbtBase element : this) {
/* 675 */       builder.append(", ");
/*     */ 
/* 678 */       if (element.getType() == NbtType.TAG_STRING)
/* 679 */         builder.append("\"" + element.getName() + "\": \"" + element.getValue() + "\"");
/*     */       else {
/* 681 */         builder.append("\"" + element.getName() + "\": " + element.getValue());
/*     */       }
/*     */     }
/* 684 */     builder.append("}");
/* 685 */     return builder.toString();
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.wrappers.nbt.WrappedCompound
 * JD-Core Version:    0.6.2
 */