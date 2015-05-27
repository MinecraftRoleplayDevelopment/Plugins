/*     */ package com.comphenix.protocol.wrappers;
/*     */ 
/*     */ import com.comphenix.protocol.wrappers.collection.ConvertedSet;
/*     */ import com.google.common.collect.Multimap;
/*     */ import com.google.common.collect.Multiset;
/*     */ import com.google.common.collect.Multiset.Entry;
/*     */ import java.util.Collection;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.Set;
/*     */ 
/*     */ class GuavaWrappers
/*     */ {
/*  18 */   private static volatile boolean USE_REFLECTION_FALLBACK = false;
/*     */ 
/*     */   public static <TKey, TValue> Multimap<TKey, TValue> getBukkitMultimap(Multimap<TKey, TValue> multimap)
/*     */   {
/*  28 */     if (USE_REFLECTION_FALLBACK) {
/*  29 */       return GuavaReflection.getBukkitMultimap(multimap);
/*     */     }
/*     */ 
/*  32 */     Multimap result = new Multimap()
/*     */     {
/*     */       public Map<TKey, Collection<TValue>> asMap() {
/*  35 */         return this.val$multimap.asMap();
/*     */       }
/*     */ 
/*     */       public void clear()
/*     */       {
/*  40 */         this.val$multimap.clear();
/*     */       }
/*     */ 
/*     */       public boolean containsEntry(Object arg0, Object arg1)
/*     */       {
/*  45 */         return this.val$multimap.containsEntry(arg0, arg1);
/*     */       }
/*     */ 
/*     */       public boolean containsKey(Object arg0)
/*     */       {
/*  50 */         return this.val$multimap.containsKey(arg0);
/*     */       }
/*     */ 
/*     */       public boolean containsValue(Object arg0)
/*     */       {
/*  55 */         return this.val$multimap.containsValue(arg0);
/*     */       }
/*     */ 
/*     */       public Collection<Map.Entry<TKey, TValue>> entries()
/*     */       {
/*  60 */         return this.val$multimap.entries();
/*     */       }
/*     */ 
/*     */       public boolean equals(Object arg0)
/*     */       {
/*  65 */         return this.val$multimap.equals(arg0);
/*     */       }
/*     */ 
/*     */       public Collection<TValue> get(TKey arg0)
/*     */       {
/*  70 */         return this.val$multimap.get(arg0);
/*     */       }
/*     */ 
/*     */       public int hashCode()
/*     */       {
/*  75 */         return this.val$multimap.hashCode();
/*     */       }
/*     */ 
/*     */       public boolean isEmpty()
/*     */       {
/*  80 */         return this.val$multimap.isEmpty();
/*     */       }
/*     */ 
/*     */       public Set<TKey> keySet()
/*     */       {
/*  85 */         return this.val$multimap.keySet();
/*     */       }
/*     */ 
/*     */       public Multiset<TKey> keys()
/*     */       {
/*  90 */         return GuavaWrappers.getBukkitMultiset(this.val$multimap.keys());
/*     */       }
/*     */ 
/*     */       public boolean put(TKey arg0, TValue arg1)
/*     */       {
/*  95 */         return this.val$multimap.put(arg0, arg1);
/*     */       }
/*     */ 
/*     */       public boolean putAll(Multimap<? extends TKey, ? extends TValue> arg0)
/*     */       {
/* 100 */         boolean result = false;
/*     */ 
/* 103 */         for (Map.Entry entry : arg0.entries()) {
/* 104 */           result |= this.val$multimap.put(entry.getKey(), entry.getValue());
/*     */         }
/* 106 */         return result;
/*     */       }
/*     */ 
/*     */       public boolean putAll(TKey arg0, Iterable<? extends TValue> arg1)
/*     */       {
/* 111 */         return this.val$multimap.putAll(arg0, arg1);
/*     */       }
/*     */ 
/*     */       public boolean remove(Object arg0, Object arg1)
/*     */       {
/* 116 */         return this.val$multimap.remove(arg0, arg1);
/*     */       }
/*     */ 
/*     */       public Collection<TValue> removeAll(Object arg0)
/*     */       {
/* 121 */         return this.val$multimap.removeAll(arg0);
/*     */       }
/*     */ 
/*     */       public Collection<TValue> replaceValues(TKey arg0, Iterable<? extends TValue> arg1)
/*     */       {
/* 126 */         return this.val$multimap.replaceValues(arg0, arg1);
/*     */       }
/*     */ 
/*     */       public int size()
/*     */       {
/* 131 */         return this.val$multimap.size();
/*     */       }
/*     */ 
/*     */       public Collection<TValue> values()
/*     */       {
/* 136 */         return this.val$multimap.values();
/*     */       }
/*     */     };
/*     */     try
/*     */     {
/* 141 */       result.size();
/* 142 */       return result;
/*     */     }
/*     */     catch (LinkageError e) {
/* 145 */       USE_REFLECTION_FALLBACK = true;
/* 146 */     }return GuavaReflection.getBukkitMultimap(multimap);
/*     */   }
/*     */ 
/*     */   public static <TValue> Multiset<TValue> getBukkitMultiset(Multiset<TValue> multiset)
/*     */   {
/* 151 */     if (USE_REFLECTION_FALLBACK) {
/* 152 */       return GuavaReflection.getBukkitMultiset(multiset);
/*     */     }
/*     */ 
/* 155 */     Multiset result = new Multiset()
/*     */     {
/*     */       public int add(TValue arg0, int arg1) {
/* 158 */         return this.val$multiset.add(arg0, arg1);
/*     */       }
/*     */ 
/*     */       public boolean add(TValue arg0)
/*     */       {
/* 163 */         return this.val$multiset.add(arg0);
/*     */       }
/*     */ 
/*     */       public boolean addAll(Collection<? extends TValue> c)
/*     */       {
/* 168 */         return this.val$multiset.addAll(c);
/*     */       }
/*     */ 
/*     */       public void clear()
/*     */       {
/* 173 */         this.val$multiset.clear();
/*     */       }
/*     */ 
/*     */       public boolean contains(Object arg0)
/*     */       {
/* 178 */         return this.val$multiset.contains(arg0);
/*     */       }
/*     */ 
/*     */       public boolean containsAll(Collection<?> arg0)
/*     */       {
/* 183 */         return this.val$multiset.containsAll(arg0);
/*     */       }
/*     */ 
/*     */       public int count(Object arg0)
/*     */       {
/* 188 */         return this.val$multiset.count(arg0);
/*     */       }
/*     */ 
/*     */       public Set<TValue> elementSet()
/*     */       {
/* 193 */         return this.val$multiset.elementSet();
/*     */       }
/*     */ 
/*     */       public Set<Multiset.Entry<TValue>> entrySet()
/*     */       {
/* 198 */         return new ConvertedSet(this.val$multiset.entrySet())
/*     */         {
/*     */           protected Multiset.Entry<TValue> toOuter(Multiset.Entry<TValue> inner)
/*     */           {
/* 206 */             return GuavaWrappers.getBukkitEntry(inner);
/*     */           }
/*     */ 
/*     */           protected Multiset.Entry<TValue> toInner(Multiset.Entry<TValue> outer)
/*     */           {
/* 212 */             throw new UnsupportedOperationException("Cannot convert " + outer);
/*     */           }
/*     */         };
/*     */       }
/*     */ 
/*     */       public boolean equals(Object arg0)
/*     */       {
/* 219 */         return this.val$multiset.equals(arg0);
/*     */       }
/*     */ 
/*     */       public int hashCode()
/*     */       {
/* 224 */         return this.val$multiset.hashCode();
/*     */       }
/*     */ 
/*     */       public boolean isEmpty()
/*     */       {
/* 229 */         return this.val$multiset.isEmpty();
/*     */       }
/*     */ 
/*     */       public Iterator<TValue> iterator()
/*     */       {
/* 234 */         return this.val$multiset.iterator();
/*     */       }
/*     */ 
/*     */       public int remove(Object arg0, int arg1)
/*     */       {
/* 239 */         return this.val$multiset.remove(arg0, arg1);
/*     */       }
/*     */ 
/*     */       public boolean remove(Object arg0)
/*     */       {
/* 244 */         return this.val$multiset.remove(arg0);
/*     */       }
/*     */ 
/*     */       public boolean removeAll(Collection<?> arg0)
/*     */       {
/* 249 */         return this.val$multiset.removeAll(arg0);
/*     */       }
/*     */ 
/*     */       public boolean retainAll(Collection<?> arg0)
/*     */       {
/* 254 */         return this.val$multiset.retainAll(arg0);
/*     */       }
/*     */ 
/*     */       public boolean setCount(TValue arg0, int arg1, int arg2)
/*     */       {
/* 259 */         return this.val$multiset.setCount(arg0, arg1, arg2);
/*     */       }
/*     */ 
/*     */       public int setCount(TValue arg0, int arg1)
/*     */       {
/* 264 */         return this.val$multiset.setCount(arg0, arg1);
/*     */       }
/*     */ 
/*     */       public int size()
/*     */       {
/* 269 */         return this.val$multiset.size();
/*     */       }
/*     */ 
/*     */       public Object[] toArray()
/*     */       {
/* 274 */         return this.val$multiset.toArray();
/*     */       }
/*     */ 
/*     */       public <T> T[] toArray(T[] a)
/*     */       {
/* 279 */         return this.val$multiset.toArray(a);
/*     */       }
/*     */ 
/*     */       public String toString()
/*     */       {
/* 284 */         return this.val$multiset.toString();
/*     */       }
/*     */     };
/*     */     try
/*     */     {
/* 289 */       result.size();
/* 290 */       return result;
/*     */     } catch (LinkageError e) {
/* 292 */       USE_REFLECTION_FALLBACK = true;
/* 293 */     }return GuavaReflection.getBukkitMultiset(multiset);
/*     */   }
/*     */ 
/*     */   private static <TValue> Multiset.Entry<TValue> getBukkitEntry(Multiset.Entry<TValue> entry)
/*     */   {
/* 298 */     return new Multiset.Entry()
/*     */     {
/*     */       public boolean equals(Object arg0) {
/* 301 */         return this.val$entry.equals(arg0);
/*     */       }
/*     */ 
/*     */       public int getCount()
/*     */       {
/* 306 */         return this.val$entry.getCount();
/*     */       }
/*     */ 
/*     */       public TValue getElement()
/*     */       {
/* 311 */         return this.val$entry.getElement();
/*     */       }
/*     */ 
/*     */       public int hashCode()
/*     */       {
/* 316 */         return this.val$entry.hashCode();
/*     */       }
/*     */ 
/*     */       public String toString()
/*     */       {
/* 321 */         return this.val$entry.toString();
/*     */       }
/*     */     };
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.wrappers.GuavaWrappers
 * JD-Core Version:    0.6.2
 */