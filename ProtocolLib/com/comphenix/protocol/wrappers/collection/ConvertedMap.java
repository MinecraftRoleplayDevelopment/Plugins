/*     */ package com.comphenix.protocol.wrappers.collection;
/*     */ 
/*     */ import com.google.common.base.Function;
/*     */ import com.google.common.collect.Collections2;
/*     */ import java.util.Collection;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.Set;
/*     */ import javax.annotation.Nullable;
/*     */ 
/*     */ public abstract class ConvertedMap<Key, VInner, VOuter> extends AbstractConverted<VInner, VOuter>
/*     */   implements Map<Key, VOuter>
/*     */ {
/*     */   private Map<Key, VInner> inner;
/*  43 */   private BiFunction<Key, VOuter, VInner> innerConverter = new BiFunction()
/*     */   {
/*     */     public VInner apply(Key key, VOuter outer) {
/*  46 */       return ConvertedMap.this.toInner(key, outer);
/*     */     }
/*  43 */   };
/*     */ 
/*  51 */   private BiFunction<Key, VInner, VOuter> outerConverter = new BiFunction()
/*     */   {
/*     */     public VOuter apply(Key key, VInner inner) {
/*  54 */       return ConvertedMap.this.toOuter(key, inner);
/*     */     }
/*  51 */   };
/*     */ 
/*     */   public ConvertedMap(Map<Key, VInner> inner)
/*     */   {
/*  59 */     if (inner == null)
/*  60 */       throw new IllegalArgumentException("Inner map cannot be NULL.");
/*  61 */     this.inner = inner;
/*     */   }
/*     */ 
/*     */   public void clear()
/*     */   {
/*  66 */     this.inner.clear();
/*     */   }
/*     */ 
/*     */   public boolean containsKey(Object key)
/*     */   {
/*  71 */     return this.inner.containsKey(key);
/*     */   }
/*     */ 
/*     */   public boolean containsValue(Object value)
/*     */   {
/*  77 */     return this.inner.containsValue(toInner(value));
/*     */   }
/*     */ 
/*     */   public Set<Map.Entry<Key, VOuter>> entrySet()
/*     */   {
/*  82 */     return convertedEntrySet(this.inner.entrySet(), this.innerConverter, this.outerConverter);
/*     */   }
/*     */ 
/*     */   protected VOuter toOuter(Key key, VInner inner)
/*     */   {
/*  91 */     return toOuter(inner);
/*     */   }
/*     */ 
/*     */   protected VInner toInner(Key key, VOuter outer)
/*     */   {
/* 100 */     return toInner(outer);
/*     */   }
/*     */ 
/*     */   public VOuter get(Object key)
/*     */   {
/* 106 */     return toOuter(key, this.inner.get(key));
/*     */   }
/*     */ 
/*     */   public boolean isEmpty()
/*     */   {
/* 111 */     return this.inner.isEmpty();
/*     */   }
/*     */ 
/*     */   public Set<Key> keySet()
/*     */   {
/* 116 */     return this.inner.keySet();
/*     */   }
/*     */ 
/*     */   public VOuter put(Key key, VOuter value)
/*     */   {
/* 121 */     return toOuter(key, this.inner.put(key, toInner(key, value)));
/*     */   }
/*     */ 
/*     */   public void putAll(Map<? extends Key, ? extends VOuter> m)
/*     */   {
/* 126 */     for (Map.Entry entry : m.entrySet())
/* 127 */       put(entry.getKey(), entry.getValue());
/*     */   }
/*     */ 
/*     */   public VOuter remove(Object key)
/*     */   {
/* 134 */     return toOuter(key, this.inner.remove(key));
/*     */   }
/*     */ 
/*     */   public int size()
/*     */   {
/* 139 */     return this.inner.size();
/*     */   }
/*     */ 
/*     */   public Collection<VOuter> values()
/*     */   {
/* 144 */     return Collections2.transform(entrySet(), new Function()
/*     */     {
/*     */       public VOuter apply(@Nullable Map.Entry<Key, VOuter> entry) {
/* 147 */         return entry.getValue();
/*     */       }
/*     */     });
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 165 */     Iterator i = entrySet().iterator();
/* 166 */     if (!i.hasNext()) {
/* 167 */       return "{}";
/*     */     }
/* 169 */     StringBuilder sb = new StringBuilder();
/* 170 */     sb.append('{');
/*     */     while (true) {
/* 172 */       Map.Entry e = (Map.Entry)i.next();
/* 173 */       Object key = e.getKey();
/* 174 */       Object value = e.getValue();
/* 175 */       sb.append(key == this ? "(this Map)" : key);
/* 176 */       sb.append('=');
/* 177 */       sb.append(value == this ? "(this Map)" : value);
/* 178 */       if (!i.hasNext())
/* 179 */         return '}';
/* 180 */       sb.append(", ");
/*     */     }
/*     */   }
/*     */ 
/*     */   static <Key, VInner, VOuter> Set<Map.Entry<Key, VOuter>> convertedEntrySet(Collection<Map.Entry<Key, VInner>> entries, final BiFunction<Key, VOuter, VInner> innerFunction, final BiFunction<Key, VInner, VOuter> outerFunction)
/*     */   {
/* 196 */     return new ConvertedSet(entries)
/*     */     {
/*     */       protected Map.Entry<Key, VInner> toInner(final Map.Entry<Key, VOuter> outer) {
/* 199 */         return new Map.Entry()
/*     */         {
/*     */           public Key getKey() {
/* 202 */             return outer.getKey();
/*     */           }
/*     */ 
/*     */           public VInner getValue()
/*     */           {
/* 207 */             return ConvertedMap.4.this.val$innerFunction.apply(getKey(), outer.getValue());
/*     */           }
/*     */ 
/*     */           public VInner setValue(VInner value)
/*     */           {
/* 212 */             return ConvertedMap.4.this.val$innerFunction.apply(getKey(), outer.setValue(ConvertedMap.4.this.val$outerFunction.apply(getKey(), value)));
/*     */           }
/*     */ 
/*     */           public String toString()
/*     */           {
/* 217 */             return String.format("\"%s\": %s", new Object[] { getKey(), getValue() });
/*     */           }
/*     */         };
/*     */       }
/*     */ 
/*     */       protected Map.Entry<Key, VOuter> toOuter(final Map.Entry<Key, VInner> inner)
/*     */       {
/* 224 */         return new Map.Entry()
/*     */         {
/*     */           public Key getKey() {
/* 227 */             return inner.getKey();
/*     */           }
/*     */ 
/*     */           public VOuter getValue()
/*     */           {
/* 232 */             return ConvertedMap.4.this.val$outerFunction.apply(getKey(), inner.getValue());
/*     */           }
/*     */ 
/*     */           public VOuter setValue(VOuter value)
/*     */           {
/* 237 */             Object converted = ConvertedMap.4.this.val$innerFunction.apply(getKey(), value);
/* 238 */             return ConvertedMap.4.this.val$outerFunction.apply(getKey(), inner.setValue(converted));
/*     */           }
/*     */ 
/*     */           public String toString()
/*     */           {
/* 243 */             return String.format("\"%s\": %s", new Object[] { getKey(), getValue() });
/*     */           }
/*     */         };
/*     */       }
/*     */     };
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.wrappers.collection.ConvertedMap
 * JD-Core Version:    0.6.2
 */