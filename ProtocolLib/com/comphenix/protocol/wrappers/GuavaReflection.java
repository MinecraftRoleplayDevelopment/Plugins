/*     */ package com.comphenix.protocol.wrappers;
/*     */ 
/*     */ import com.comphenix.protocol.reflect.accessors.Accessors;
/*     */ import com.comphenix.protocol.reflect.accessors.MethodAccessor;
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
/*     */ class GuavaReflection
/*     */ {
/*     */   public static <TKey, TValue> Multimap<TKey, TValue> getBukkitMultimap(Object multimap)
/*     */   {
/*  29 */     return new Multimap() {
/*  30 */       private Class<?> multimapClass = this.val$multimap.getClass();
/*  31 */       private MethodAccessor methodAsMap = Accessors.getMethodAccessor(this.multimapClass, "asMap", new Class[0]);
/*  32 */       private MethodAccessor methodClear = Accessors.getMethodAccessor(this.multimapClass, "clear", new Class[0]);
/*  33 */       private MethodAccessor methodContainsEntry = Accessors.getMethodAccessor(this.multimapClass, "containsEntry", new Class[] { Object.class, Object.class });
/*  34 */       private MethodAccessor methodContainsKey = Accessors.getMethodAccessor(this.multimapClass, "containsKey", new Class[] { Object.class });
/*  35 */       private MethodAccessor methodContainsValue = Accessors.getMethodAccessor(this.multimapClass, "containsValue", new Class[] { Object.class });
/*  36 */       private MethodAccessor methodEntries = Accessors.getMethodAccessor(this.multimapClass, "entries", new Class[0]);
/*  37 */       private MethodAccessor methodGet = Accessors.getMethodAccessor(this.multimapClass, "get", new Class[] { Object.class });
/*  38 */       private MethodAccessor methodIsEmpty = Accessors.getMethodAccessor(this.multimapClass, "isEmpty", new Class[0]);
/*  39 */       private MethodAccessor methodKeySet = Accessors.getMethodAccessor(this.multimapClass, "keySet", new Class[0]);
/*  40 */       private MethodAccessor methodKeys = Accessors.getMethodAccessor(this.multimapClass, "keys", new Class[0]);
/*  41 */       private MethodAccessor methodPut = Accessors.getMethodAccessor(this.multimapClass, "put", new Class[] { Object.class, Object.class });
/*  42 */       private MethodAccessor methodPutAll = Accessors.getMethodAccessor(this.multimapClass, "putAll", new Class[] { Object.class, Iterable.class });
/*  43 */       private MethodAccessor methodRemove = Accessors.getMethodAccessor(this.multimapClass, "remove", new Class[] { Object.class, Object.class });
/*  44 */       private MethodAccessor methodRemoveAll = Accessors.getMethodAccessor(this.multimapClass, "removeAll", new Class[] { Object.class });
/*  45 */       private MethodAccessor methodReplaceValues = Accessors.getMethodAccessor(this.multimapClass, "replaceValues", new Class[] { Object.class, Iterable.class });
/*  46 */       private MethodAccessor methodSize = Accessors.getMethodAccessor(this.multimapClass, "size", new Class[0]);
/*  47 */       private MethodAccessor methodValues = Accessors.getMethodAccessor(this.multimapClass, "values", new Class[0]);
/*     */ 
/*     */       public Map<TKey, Collection<TValue>> asMap()
/*     */       {
/*  51 */         return (Map)this.methodAsMap.invoke(this.val$multimap, new Object[0]);
/*     */       }
/*     */ 
/*     */       public void clear() {
/*  55 */         this.methodClear.invoke(this.val$multimap, new Object[0]);
/*     */       }
/*     */ 
/*     */       public boolean containsEntry(Object arg0, Object arg1) {
/*  59 */         return ((Boolean)this.methodContainsEntry.invoke(this.val$multimap, new Object[] { arg0, arg1 })).booleanValue();
/*     */       }
/*     */ 
/*     */       public boolean containsKey(Object arg0) {
/*  63 */         return ((Boolean)this.methodContainsKey.invoke(this.val$multimap, new Object[] { arg0 })).booleanValue();
/*     */       }
/*     */ 
/*     */       public boolean containsValue(Object arg0) {
/*  67 */         return ((Boolean)this.methodContainsValue.invoke(this.val$multimap, new Object[] { arg0 })).booleanValue();
/*     */       }
/*     */ 
/*     */       public Collection<Map.Entry<TKey, TValue>> entries()
/*     */       {
/*  72 */         return (Collection)this.methodEntries.invoke(this.val$multimap, new Object[0]);
/*     */       }
/*     */ 
/*     */       public boolean equals(Object arg0) {
/*  76 */         return this.val$multimap.equals(arg0);
/*     */       }
/*     */ 
/*     */       public int hashCode() {
/*  80 */         return this.val$multimap.hashCode();
/*     */       }
/*     */ 
/*     */       public String toString()
/*     */       {
/*  85 */         return this.val$multimap.toString();
/*     */       }
/*     */ 
/*     */       public Collection<TValue> get(TKey arg0)
/*     */       {
/*  90 */         return (Collection)this.methodGet.invoke(this.val$multimap, new Object[] { arg0 });
/*     */       }
/*     */ 
/*     */       public boolean isEmpty() {
/*  94 */         return ((Boolean)this.methodIsEmpty.invoke(this.val$multimap, new Object[0])).booleanValue();
/*     */       }
/*     */ 
/*     */       public Set<TKey> keySet()
/*     */       {
/*  99 */         return (Set)this.methodKeySet.invoke(this.val$multimap, new Object[0]);
/*     */       }
/*     */ 
/*     */       public Multiset<TKey> keys() {
/* 103 */         return GuavaReflection.getBukkitMultiset(this.methodKeys.invoke(this.val$multimap, new Object[0]));
/*     */       }
/*     */ 
/*     */       public boolean put(TKey arg0, TValue arg1) {
/* 107 */         return ((Boolean)this.methodPut.invoke(this.val$multimap, new Object[] { arg0, arg1 })).booleanValue();
/*     */       }
/*     */ 
/*     */       public boolean putAll(Multimap<? extends TKey, ? extends TValue> arg0) {
/* 111 */         boolean result = false;
/*     */ 
/* 114 */         for (Map.Entry entry : arg0.entries()) {
/* 115 */           result |= ((Boolean)this.methodPut.invoke(this.val$multimap, new Object[] { entry.getKey(), entry.getValue() })).booleanValue();
/*     */         }
/* 117 */         return result;
/*     */       }
/*     */ 
/*     */       public boolean putAll(TKey arg0, Iterable<? extends TValue> arg1) {
/* 121 */         return ((Boolean)this.methodPutAll.invoke(arg0, new Object[] { arg1 })).booleanValue();
/*     */       }
/*     */ 
/*     */       public boolean remove(Object arg0, Object arg1) {
/* 125 */         return ((Boolean)this.methodRemove.invoke(this.val$multimap, new Object[] { arg0, arg1 })).booleanValue();
/*     */       }
/*     */ 
/*     */       public Collection<TValue> removeAll(Object arg0)
/*     */       {
/* 130 */         return (Collection)this.methodRemoveAll.invoke(this.val$multimap, new Object[] { arg0 });
/*     */       }
/*     */ 
/*     */       public Collection<TValue> replaceValues(TKey arg0, Iterable<? extends TValue> arg1)
/*     */       {
/* 135 */         return (Collection)this.methodReplaceValues.invoke(this.val$multimap, new Object[] { arg0, arg1 });
/*     */       }
/*     */ 
/*     */       public int size() {
/* 139 */         return ((Integer)this.methodSize.invoke(this.val$multimap, new Object[0])).intValue();
/*     */       }
/*     */ 
/*     */       public Collection<TValue> values()
/*     */       {
/* 144 */         return (Collection)this.methodValues.invoke(this.val$multimap, new Object[0]);
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   public static <TValue> Multiset<TValue> getBukkitMultiset(Object multiset) {
/* 150 */     return new Multiset() {
/* 151 */       private Class<?> multisetClass = this.val$multiset.getClass();
/* 152 */       private MethodAccessor methodAddMany = Accessors.getMethodAccessor(this.multisetClass, "add", new Class[] { Object.class, Integer.TYPE });
/* 153 */       private MethodAccessor methodAddOne = Accessors.getMethodAccessor(this.multisetClass, "add", new Class[] { Object.class });
/* 154 */       private MethodAccessor methodAddAll = Accessors.getMethodAccessor(this.multisetClass, "addAll", new Class[] { Collection.class });
/* 155 */       private MethodAccessor methodClear = Accessors.getMethodAccessor(this.multisetClass, "clear", new Class[0]);
/* 156 */       private MethodAccessor methodContains = Accessors.getMethodAccessor(this.multisetClass, "contains", new Class[] { Object.class });
/* 157 */       private MethodAccessor methodContainsAll = Accessors.getMethodAccessor(this.multisetClass, "containsAll", new Class[] { Collection.class });
/* 158 */       private MethodAccessor methodCount = Accessors.getMethodAccessor(this.multisetClass, "count", new Class[] { Object.class });
/* 159 */       private MethodAccessor methodElementSet = Accessors.getMethodAccessor(this.multisetClass, "elementSet", new Class[0]);
/* 160 */       private MethodAccessor methodEntrySet = Accessors.getMethodAccessor(this.multisetClass, "entrySet", new Class[0]);
/* 161 */       private MethodAccessor methodIsEmpty = Accessors.getMethodAccessor(this.multisetClass, "isEmpty", new Class[0]);
/* 162 */       private MethodAccessor methodIterator = Accessors.getMethodAccessor(this.multisetClass, "iterator", new Class[0]);
/* 163 */       private MethodAccessor methodRemoveCount = Accessors.getMethodAccessor(this.multisetClass, "remove", new Class[] { Object.class, Integer.TYPE });
/* 164 */       private MethodAccessor methodRemoveOne = Accessors.getMethodAccessor(this.multisetClass, "remove", new Class[] { Object.class });
/* 165 */       private MethodAccessor methodRemoveAll = Accessors.getMethodAccessor(this.multisetClass, "removeAll", new Class[] { Collection.class });
/* 166 */       private MethodAccessor methodRetainAll = Accessors.getMethodAccessor(this.multisetClass, "retainAll", new Class[] { Collection.class });
/* 167 */       private MethodAccessor methodSetCountOldNew = Accessors.getMethodAccessor(this.multisetClass, "setCount", new Class[] { Object.class, Integer.TYPE, Integer.TYPE });
/* 168 */       private MethodAccessor methodSetCountNew = Accessors.getMethodAccessor(this.multisetClass, "setCount", new Class[] { Object.class, Integer.TYPE });
/* 169 */       private MethodAccessor methodSize = Accessors.getMethodAccessor(this.multisetClass, "size", new Class[0]);
/* 170 */       private MethodAccessor methodToArray = Accessors.getMethodAccessor(this.multisetClass, "toArray", new Class[0]);
/* 171 */       private MethodAccessor methodToArrayBuffer = Accessors.getMethodAccessor(this.multisetClass, "toArray", new Class[] { [Ljava.lang.Object.class });
/*     */ 
/*     */       public int add(TValue arg0, int arg1) {
/* 174 */         return ((Integer)this.methodAddMany.invoke(this.val$multiset, new Object[] { arg0, Integer.valueOf(arg1) })).intValue();
/*     */       }
/*     */ 
/*     */       public boolean add(TValue arg0) {
/* 178 */         return ((Boolean)this.methodAddOne.invoke(this.val$multiset, new Object[] { arg0 })).booleanValue();
/*     */       }
/*     */ 
/*     */       public boolean addAll(Collection<? extends TValue> c) {
/* 182 */         return ((Boolean)this.methodAddAll.invoke(this.val$multiset, new Object[] { c })).booleanValue();
/*     */       }
/*     */ 
/*     */       public void clear() {
/* 186 */         this.methodClear.invoke(this.val$multiset, new Object[0]);
/*     */       }
/*     */ 
/*     */       public boolean contains(Object arg0) {
/* 190 */         return ((Boolean)this.methodContains.invoke(this.val$multiset, new Object[] { arg0 })).booleanValue();
/*     */       }
/*     */ 
/*     */       public boolean containsAll(Collection<?> arg0) {
/* 194 */         return ((Boolean)this.methodContainsAll.invoke(this.val$multiset, new Object[] { arg0 })).booleanValue();
/*     */       }
/*     */ 
/*     */       public int count(Object arg0) {
/* 198 */         return ((Integer)this.methodCount.invoke(this.val$multiset, new Object[] { arg0 })).intValue();
/*     */       }
/*     */ 
/*     */       public Set<TValue> elementSet()
/*     */       {
/* 203 */         return (Set)this.methodElementSet.invoke(this.val$multiset, new Object[0]);
/*     */       }
/*     */ 
/*     */       public Set<Multiset.Entry<TValue>> entrySet()
/*     */       {
/* 208 */         return new ConvertedSet((Set)this.methodEntrySet.invoke(this.val$multiset, new Object[0]))
/*     */         {
/*     */           protected Multiset.Entry<TValue> toOuter(Object inner)
/*     */           {
/* 216 */             return GuavaReflection.getBukkitEntry(inner);
/*     */           }
/*     */ 
/*     */           protected Object toInner(Multiset.Entry<TValue> outer)
/*     */           {
/* 222 */             throw new UnsupportedOperationException("Cannot convert " + outer);
/*     */           }
/*     */         };
/*     */       }
/*     */ 
/*     */       public boolean equals(Object arg0) {
/* 228 */         return this.val$multiset.equals(arg0);
/*     */       }
/*     */ 
/*     */       public int hashCode() {
/* 232 */         return this.val$multiset.hashCode();
/*     */       }
/*     */ 
/*     */       public boolean isEmpty() {
/* 236 */         return ((Boolean)this.methodIsEmpty.invoke(this.val$multiset, new Object[0])).booleanValue();
/*     */       }
/*     */ 
/*     */       public Iterator<TValue> iterator()
/*     */       {
/* 241 */         return (Iterator)this.methodIterator.invoke(this.val$multiset, new Object[0]);
/*     */       }
/*     */ 
/*     */       public int remove(Object arg0, int arg1) {
/* 245 */         return ((Integer)this.methodRemoveCount.invoke(this.val$multiset, new Object[] { arg0, Integer.valueOf(arg1) })).intValue();
/*     */       }
/*     */ 
/*     */       public boolean remove(Object arg0) {
/* 249 */         return ((Boolean)this.methodRemoveOne.invoke(this.val$multiset, new Object[] { arg0 })).booleanValue();
/*     */       }
/*     */ 
/*     */       public boolean removeAll(Collection<?> arg0) {
/* 253 */         return ((Boolean)this.methodRemoveAll.invoke(this.val$multiset, new Object[] { arg0 })).booleanValue();
/*     */       }
/*     */ 
/*     */       public boolean retainAll(Collection<?> arg0) {
/* 257 */         return ((Boolean)this.methodRetainAll.invoke(this.val$multiset, new Object[] { arg0 })).booleanValue();
/*     */       }
/*     */ 
/*     */       public boolean setCount(TValue arg0, int arg1, int arg2) {
/* 261 */         return ((Boolean)this.methodSetCountOldNew.invoke(this.val$multiset, new Object[] { arg0, Integer.valueOf(arg1), Integer.valueOf(arg2) })).booleanValue();
/*     */       }
/*     */ 
/*     */       public int setCount(TValue arg0, int arg1) {
/* 265 */         return ((Integer)this.methodSetCountNew.invoke(this.val$multiset, new Object[] { arg0, Integer.valueOf(arg1) })).intValue();
/*     */       }
/*     */ 
/*     */       public int size() {
/* 269 */         return ((Integer)this.methodSize.invoke(this.val$multiset, new Object[0])).intValue();
/*     */       }
/*     */ 
/*     */       public Object[] toArray() {
/* 273 */         return (Object[])this.methodToArray.invoke(this.val$multiset, new Object[0]);
/*     */       }
/*     */ 
/*     */       public <T> T[] toArray(T[] a)
/*     */       {
/* 278 */         return (Object[])this.methodToArrayBuffer.invoke(this.val$multiset, a);
/*     */       }
/*     */ 
/*     */       public String toString() {
/* 282 */         return this.val$multiset.toString();
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   private static <TValue> Multiset.Entry<TValue> getBukkitEntry(Object entry) {
/* 288 */     return new Multiset.Entry() {
/* 289 */       private Class<?> entryClass = this.val$entry.getClass();
/* 290 */       private MethodAccessor methodEquals = Accessors.getMethodAccessor(this.entryClass, "equals", new Class[] { Object.class });
/* 291 */       private MethodAccessor methodGetCount = Accessors.getMethodAccessor(this.entryClass, "getCount", new Class[0]);
/* 292 */       private MethodAccessor methodGetElement = Accessors.getMethodAccessor(this.entryClass, "getElement", new Class[0]);
/*     */ 
/*     */       public boolean equals(Object arg0) {
/* 295 */         return ((Boolean)this.methodEquals.invoke(this.val$entry, new Object[] { arg0 })).booleanValue();
/*     */       }
/*     */ 
/*     */       public int getCount() {
/* 299 */         return ((Integer)this.methodGetCount.invoke(this.val$entry, new Object[0])).intValue();
/*     */       }
/*     */ 
/*     */       public TValue getElement()
/*     */       {
/* 304 */         return this.methodGetElement.invoke(this.val$entry, new Object[0]);
/*     */       }
/*     */ 
/*     */       public int hashCode() {
/* 308 */         return this.val$entry.hashCode();
/*     */       }
/*     */ 
/*     */       public String toString() {
/* 312 */         return this.val$entry.toString();
/*     */       }
/*     */     };
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.wrappers.GuavaReflection
 * JD-Core Version:    0.6.2
 */