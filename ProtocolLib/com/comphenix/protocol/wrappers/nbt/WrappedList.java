/*     */ package com.comphenix.protocol.wrappers.nbt;
/*     */ 
/*     */ import com.comphenix.protocol.wrappers.collection.ConvertedList;
/*     */ import com.comphenix.protocol.wrappers.nbt.io.NbtBinarySerializer;
/*     */ import com.google.common.base.Function;
/*     */ import com.google.common.base.Joiner;
/*     */ import com.google.common.collect.Iterables;
/*     */ import java.io.DataOutput;
/*     */ import java.util.Collection;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import javax.annotation.Nullable;
/*     */ 
/*     */ class WrappedList<TType>
/*     */   implements NbtWrapper<List<NbtBase<TType>>>, Iterable<TType>, NbtList<TType>
/*     */ {
/*     */   private WrappedElement<List<Object>> container;
/*     */   private ConvertedList<Object, NbtBase<TType>> savedList;
/*  47 */   private NbtType elementType = NbtType.TAG_END;
/*     */ 
/*     */   public static <T> NbtList<T> fromName(String name)
/*     */   {
/*  56 */     return (NbtList)NbtFactory.ofWrapper(NbtType.TAG_LIST, name);
/*     */   }
/*     */ 
/*     */   public static <T> NbtList<T> fromArray(String name, T[] elements)
/*     */   {
/*  67 */     NbtList result = fromName(name);
/*     */ 
/*  69 */     for (Object element : elements) {
/*  70 */       if (element == null) {
/*  71 */         throw new IllegalArgumentException("An NBT list cannot contain a null element!");
/*     */       }
/*  73 */       if ((element instanceof NbtBase))
/*  74 */         result.add((NbtBase)element);
/*     */       else
/*  76 */         result.add(NbtFactory.ofWrapper(element.getClass(), "", element));
/*     */     }
/*  78 */     return result;
/*     */   }
/*     */ 
/*     */   public static <T> NbtList<T> fromList(String name, Collection<? extends T> elements)
/*     */   {
/*  89 */     NbtList result = fromName(name);
/*     */ 
/*  91 */     for (Iterator i$ = elements.iterator(); i$.hasNext(); ) { Object element = i$.next();
/*  92 */       if (element == null) {
/*  93 */         throw new IllegalArgumentException("An NBT list cannot contain a null element!");
/*     */       }
/*  95 */       if ((element instanceof NbtBase))
/*  96 */         result.add((NbtBase)element);
/*     */       else
/*  98 */         result.add(NbtFactory.ofWrapper(element.getClass(), "", element));
/*     */     }
/* 100 */     return result;
/*     */   }
/*     */ 
/*     */   public WrappedList(Object handle)
/*     */   {
/* 108 */     this.container = new WrappedElement(handle);
/* 109 */     this.elementType = this.container.getSubType();
/*     */   }
/*     */ 
/*     */   public WrappedList(Object handle, String name)
/*     */   {
/* 118 */     this.container = new WrappedElement(handle, name);
/* 119 */     this.elementType = this.container.getSubType();
/*     */   }
/*     */ 
/*     */   public boolean accept(NbtVisitor visitor)
/*     */   {
/* 125 */     if (visitor.visitEnter(this)) {
/* 126 */       for (NbtBase node : getValue()) {
/* 127 */         if (!node.accept(visitor)) {
/*     */           break;
/*     */         }
/*     */       }
/*     */     }
/* 132 */     return visitor.visitLeave(this);
/*     */   }
/*     */ 
/*     */   public Object getHandle()
/*     */   {
/* 137 */     return this.container.getHandle();
/*     */   }
/*     */ 
/*     */   public NbtType getType()
/*     */   {
/* 142 */     return NbtType.TAG_LIST;
/*     */   }
/*     */ 
/*     */   public NbtType getElementType()
/*     */   {
/* 147 */     return this.elementType;
/*     */   }
/*     */ 
/*     */   public void setElementType(NbtType type)
/*     */   {
/* 152 */     this.elementType = type;
/* 153 */     this.container.setSubType(type);
/*     */   }
/*     */ 
/*     */   public String getName()
/*     */   {
/* 158 */     return this.container.getName();
/*     */   }
/*     */ 
/*     */   public void setName(String name)
/*     */   {
/* 163 */     this.container.setName(name);
/*     */   }
/*     */ 
/*     */   public List<NbtBase<TType>> getValue()
/*     */   {
/* 168 */     if (this.savedList == null) {
/* 169 */       this.savedList = new ConvertedList((List)this.container.getValue())
/*     */       {
/*     */         private void verifyElement(NbtBase<TType> element) {
/* 172 */           if (element == null)
/* 173 */             throw new IllegalArgumentException("Cannot store NULL elements in list.");
/* 174 */           if (!element.getName().equals("")) {
/* 175 */             throw new IllegalArgumentException("Cannot add a the named NBT tag " + element + " to a list.");
/*     */           }
/*     */ 
/* 178 */           if (WrappedList.this.getElementType() != NbtType.TAG_END) {
/* 179 */             if (!element.getType().equals(WrappedList.this.getElementType())) {
/* 180 */               throw new IllegalArgumentException("Cannot add " + element + " of " + element.getType() + " to a list of type " + WrappedList.this.getElementType());
/*     */             }
/*     */           }
/*     */           else
/* 184 */             WrappedList.this.container.setSubType(element.getType());
/*     */         }
/*     */ 
/*     */         public boolean add(NbtBase<TType> e)
/*     */         {
/* 190 */           verifyElement(e);
/* 191 */           return super.add(e);
/*     */         }
/*     */ 
/*     */         public void add(int index, NbtBase<TType> element)
/*     */         {
/* 196 */           verifyElement(element);
/* 197 */           super.add(index, element);
/*     */         }
/*     */ 
/*     */         public boolean addAll(Collection<? extends NbtBase<TType>> c)
/*     */         {
/* 202 */           boolean result = false;
/*     */ 
/* 204 */           for (NbtBase element : c) {
/* 205 */             add(element);
/* 206 */             result = true;
/*     */           }
/* 208 */           return result;
/*     */         }
/*     */ 
/*     */         protected Object toInner(NbtBase<TType> outer)
/*     */         {
/* 213 */           if (outer == null)
/* 214 */             return null;
/* 215 */           return NbtFactory.fromBase(outer).getHandle();
/*     */         }
/*     */ 
/*     */         protected NbtBase<TType> toOuter(Object inner)
/*     */         {
/* 220 */           if (inner == null)
/* 221 */             return null;
/* 222 */           return NbtFactory.fromNMS(inner, null);
/*     */         }
/*     */ 
/*     */         public String toString()
/*     */         {
/* 227 */           return WrappedList.this.toString();
/*     */         }
/*     */       };
/*     */     }
/* 231 */     return this.savedList;
/*     */   }
/*     */ 
/*     */   public NbtBase<List<NbtBase<TType>>> deepClone()
/*     */   {
/* 237 */     return this.container.deepClone();
/*     */   }
/*     */ 
/*     */   public void addClosest(Object value)
/*     */   {
/* 243 */     if (getElementType() == NbtType.TAG_END) {
/* 244 */       throw new IllegalStateException("This list has not been typed yet.");
/*     */     }
/* 246 */     if ((value instanceof Number)) {
/* 247 */       Number number = (Number)value;
/*     */ 
/* 250 */       switch (3.$SwitchMap$com$comphenix$protocol$wrappers$nbt$NbtType[getElementType().ordinal()]) { case 1:
/* 251 */         add(number.byteValue()); break;
/*     */       case 2:
/* 252 */         add(number.shortValue()); break;
/*     */       case 3:
/* 253 */         add(number.intValue()); break;
/*     */       case 4:
/* 254 */         add(number.longValue()); break;
/*     */       case 5:
/* 255 */         add(number.floatValue()); break;
/*     */       case 6:
/* 256 */         add(number.doubleValue()); break;
/*     */       case 7:
/* 257 */         add(number.toString()); break;
/*     */       default:
/* 259 */         throw new IllegalArgumentException("Cannot convert " + value + " to " + getType());
/*     */       }
/*     */     }
/* 262 */     else if ((value instanceof NbtBase))
/*     */     {
/* 264 */       add((NbtBase)value);
/*     */     }
/*     */     else
/*     */     {
/* 268 */       add(NbtFactory.ofWrapper(getElementType(), "", value));
/*     */     }
/*     */   }
/*     */ 
/*     */   public void add(NbtBase<TType> element)
/*     */   {
/* 274 */     getValue().add(element);
/*     */   }
/*     */ 
/*     */   public void add(String value)
/*     */   {
/* 280 */     add(NbtFactory.of("", value));
/*     */   }
/*     */ 
/*     */   public void add(byte value)
/*     */   {
/* 286 */     add(NbtFactory.of("", value));
/*     */   }
/*     */ 
/*     */   public void add(short value)
/*     */   {
/* 292 */     add(NbtFactory.of("", value));
/*     */   }
/*     */ 
/*     */   public void add(int value)
/*     */   {
/* 298 */     add(NbtFactory.of("", value));
/*     */   }
/*     */ 
/*     */   public void add(long value)
/*     */   {
/* 304 */     add(NbtFactory.of("", value));
/*     */   }
/*     */ 
/*     */   public void add(double value)
/*     */   {
/* 310 */     add(NbtFactory.of("", value));
/*     */   }
/*     */ 
/*     */   public void add(byte[] value)
/*     */   {
/* 316 */     add(NbtFactory.of("", value));
/*     */   }
/*     */ 
/*     */   public void add(int[] value)
/*     */   {
/* 322 */     add(NbtFactory.of("", value));
/*     */   }
/*     */ 
/*     */   public int size()
/*     */   {
/* 327 */     return getValue().size();
/*     */   }
/*     */ 
/*     */   public TType getValue(int index)
/*     */   {
/* 332 */     return ((NbtBase)getValue().get(index)).getValue();
/*     */   }
/*     */ 
/*     */   public Collection<NbtBase<TType>> asCollection()
/*     */   {
/* 341 */     return getValue();
/*     */   }
/*     */ 
/*     */   public void setValue(List<NbtBase<TType>> newValue)
/*     */   {
/* 346 */     NbtBase lastElement = null;
/* 347 */     List list = (List)this.container.getValue();
/* 348 */     list.clear();
/*     */ 
/* 351 */     for (NbtBase type : newValue) {
/* 352 */       if (type != null) {
/* 353 */         lastElement = type;
/* 354 */         list.add(NbtFactory.fromBase(type).getHandle());
/*     */       } else {
/* 356 */         list.add(null);
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 361 */     if (lastElement != null)
/* 362 */       this.container.setSubType(lastElement.getType());
/*     */   }
/*     */ 
/*     */   public void write(DataOutput destination)
/*     */   {
/* 368 */     NbtBinarySerializer.DEFAULT.serialize(this.container, destination);
/*     */   }
/*     */ 
/*     */   public boolean equals(Object obj)
/*     */   {
/* 373 */     if ((obj instanceof WrappedList))
/*     */     {
/* 375 */       WrappedList other = (WrappedList)obj;
/* 376 */       return this.container.equals(other.container);
/*     */     }
/* 378 */     return false;
/*     */   }
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 383 */     return this.container.hashCode();
/*     */   }
/*     */ 
/*     */   public Iterator<TType> iterator()
/*     */   {
/* 388 */     return Iterables.transform(getValue(), new Function()
/*     */     {
/*     */       public TType apply(@Nullable NbtBase<TType> param) {
/* 391 */         return param.getValue();
/*     */       }
/*     */     }).iterator();
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 399 */     StringBuilder builder = new StringBuilder();
/*     */ 
/* 401 */     builder.append("{\"name\": \"" + getName() + "\", \"value\": [");
/*     */ 
/* 403 */     if (size() > 0) {
/* 404 */       if (getElementType() == NbtType.TAG_STRING)
/* 405 */         builder.append("\"" + Joiner.on("\", \"").join(this) + "\"");
/*     */       else {
/* 407 */         builder.append(Joiner.on(", ").join(this));
/*     */       }
/*     */     }
/* 410 */     builder.append("]}");
/* 411 */     return builder.toString();
/*     */   }
/*     */ 
/*     */   public void remove(Object remove)
/*     */   {
/* 416 */     getValue().remove(remove);
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.wrappers.nbt.WrappedList
 * JD-Core Version:    0.6.2
 */