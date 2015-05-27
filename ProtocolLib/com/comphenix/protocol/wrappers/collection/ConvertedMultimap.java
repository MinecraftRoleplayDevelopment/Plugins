/*     */ package com.comphenix.protocol.wrappers.collection;
/*     */ 
/*     */ import com.google.common.base.Joiner;
/*     */ import com.google.common.base.Preconditions;
/*     */ import com.google.common.collect.Iterables;
/*     */ import com.google.common.collect.Multimap;
/*     */ import com.google.common.collect.Multiset;
/*     */ import java.util.Collection;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.Set;
/*     */ import javax.annotation.Nullable;
/*     */ 
/*     */ public abstract class ConvertedMultimap<Key, VInner, VOuter> extends AbstractConverted<VInner, VOuter>
/*     */   implements Multimap<Key, VOuter>
/*     */ {
/*     */   private Multimap<Key, VInner> inner;
/*     */ 
/*     */   public ConvertedMultimap(Multimap<Key, VInner> inner)
/*     */   {
/*  30 */     this.inner = ((Multimap)Preconditions.checkNotNull(inner, "inner map cannot be NULL."));
/*     */   }
/*     */ 
/*     */   protected Collection<VOuter> toOuterCollection(Collection<VInner> inner)
/*     */   {
/*  39 */     return new ConvertedCollection(inner)
/*     */     {
/*     */       protected VInner toInner(VOuter outer) {
/*  42 */         return ConvertedMultimap.this.toInner(outer);
/*     */       }
/*     */ 
/*     */       protected VOuter toOuter(VInner inner)
/*     */       {
/*  47 */         return ConvertedMultimap.this.toOuter(inner);
/*     */       }
/*     */ 
/*     */       public String toString()
/*     */       {
/*  52 */         return "[" + Joiner.on(", ").join(this) + "]";
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   protected Collection<VInner> toInnerCollection(Collection<VOuter> outer)
/*     */   {
/*  63 */     return new ConvertedCollection(outer)
/*     */     {
/*     */       protected VOuter toInner(VInner outer) {
/*  66 */         return ConvertedMultimap.this.toOuter(outer);
/*     */       }
/*     */ 
/*     */       protected VInner toOuter(VOuter inner)
/*     */       {
/*  71 */         return ConvertedMultimap.this.toInner(inner);
/*     */       }
/*     */ 
/*     */       public String toString()
/*     */       {
/*  76 */         return "[" + Joiner.on(", ").join(this) + "]";
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   protected Object toInnerObject(Object outer)
/*     */   {
/*  88 */     return toInner(outer);
/*     */   }
/*     */ 
/*     */   public int size()
/*     */   {
/*  93 */     return this.inner.size();
/*     */   }
/*     */ 
/*     */   public boolean isEmpty()
/*     */   {
/*  98 */     return this.inner.isEmpty();
/*     */   }
/*     */ 
/*     */   public boolean containsKey(@Nullable Object key)
/*     */   {
/* 103 */     return this.inner.containsKey(key);
/*     */   }
/*     */ 
/*     */   public boolean containsValue(@Nullable Object value)
/*     */   {
/* 108 */     return this.inner.containsValue(toInnerObject(value));
/*     */   }
/*     */ 
/*     */   public boolean containsEntry(@Nullable Object key, @Nullable Object value)
/*     */   {
/* 113 */     return this.inner.containsEntry(key, toInnerObject(value));
/*     */   }
/*     */ 
/*     */   public boolean put(@Nullable Key key, @Nullable VOuter value)
/*     */   {
/* 118 */     return this.inner.put(key, toInner(value));
/*     */   }
/*     */ 
/*     */   public boolean remove(@Nullable Object key, @Nullable Object value)
/*     */   {
/* 123 */     return this.inner.remove(key, toInnerObject(value));
/*     */   }
/*     */ 
/*     */   public boolean putAll(@Nullable Key key, Iterable<? extends VOuter> values)
/*     */   {
/* 128 */     return this.inner.putAll(key, Iterables.transform(values, getInnerConverter()));
/*     */   }
/*     */ 
/*     */   public boolean putAll(Multimap<? extends Key, ? extends VOuter> multimap)
/*     */   {
/* 134 */     return this.inner.putAll(new ConvertedMultimap(multimap)
/*     */     {
/*     */       protected VOuter toInner(VInner outer) {
/* 137 */         return ConvertedMultimap.this.toOuter(outer);
/*     */       }
/*     */ 
/*     */       protected VInner toOuter(VOuter inner)
/*     */       {
/* 142 */         return ConvertedMultimap.this.toInner(inner);
/*     */       }
/*     */     });
/*     */   }
/*     */ 
/*     */   public Collection<VOuter> replaceValues(@Nullable Key key, Iterable<? extends VOuter> values)
/*     */   {
/* 149 */     return toOuterCollection(this.inner.replaceValues(key, Iterables.transform(values, getInnerConverter())));
/*     */   }
/*     */ 
/*     */   public Collection<VOuter> removeAll(@Nullable Object key)
/*     */   {
/* 156 */     return toOuterCollection(this.inner.removeAll(key));
/*     */   }
/*     */ 
/*     */   public void clear()
/*     */   {
/* 161 */     this.inner.clear();
/*     */   }
/*     */ 
/*     */   public Collection<VOuter> get(@Nullable Key key)
/*     */   {
/* 166 */     return toOuterCollection(this.inner.get(key));
/*     */   }
/*     */ 
/*     */   public Set<Key> keySet()
/*     */   {
/* 171 */     return this.inner.keySet();
/*     */   }
/*     */ 
/*     */   public Multiset<Key> keys()
/*     */   {
/* 176 */     return this.inner.keys();
/*     */   }
/*     */ 
/*     */   public Collection<VOuter> values()
/*     */   {
/* 181 */     return toOuterCollection(this.inner.values());
/*     */   }
/*     */ 
/*     */   public Collection<Map.Entry<Key, VOuter>> entries()
/*     */   {
/* 186 */     return ConvertedMap.convertedEntrySet(this.inner.entries(), new BiFunction()
/*     */     {
/*     */       public VInner apply(Key key, VOuter outer) {
/* 189 */         return ConvertedMultimap.this.toInner(outer);
/*     */       }
/*     */     }
/*     */     , new BiFunction()
/*     */     {
/*     */       public VOuter apply(Key key, VInner inner)
/*     */       {
/* 194 */         return ConvertedMultimap.this.toOuter(inner);
/*     */       }
/*     */     });
/*     */   }
/*     */ 
/*     */   public Map<Key, Collection<VOuter>> asMap()
/*     */   {
/* 202 */     return new ConvertedMap(this.inner.asMap())
/*     */     {
/*     */       protected Collection<VInner> toInner(Collection<VOuter> outer) {
/* 205 */         return ConvertedMultimap.this.toInnerCollection(outer);
/*     */       }
/*     */ 
/*     */       protected Collection<VOuter> toOuter(Collection<VInner> inner)
/*     */       {
/* 210 */         return ConvertedMultimap.this.toOuterCollection(inner);
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 228 */     Iterator i = entries().iterator();
/* 229 */     if (!i.hasNext()) {
/* 230 */       return "{}";
/*     */     }
/* 232 */     StringBuilder sb = new StringBuilder();
/* 233 */     sb.append('{');
/*     */     while (true) {
/* 235 */       Map.Entry e = (Map.Entry)i.next();
/* 236 */       Object key = e.getKey();
/* 237 */       Object value = e.getValue();
/* 238 */       sb.append(key == this ? "(this Map)" : key);
/* 239 */       sb.append('=');
/* 240 */       sb.append(value == this ? "(this Map)" : value);
/* 241 */       if (!i.hasNext())
/* 242 */         return '}';
/* 243 */       sb.append(", ");
/*     */     }
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.wrappers.collection.ConvertedMultimap
 * JD-Core Version:    0.6.2
 */