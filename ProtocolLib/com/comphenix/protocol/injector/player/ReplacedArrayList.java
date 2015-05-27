/*     */ package com.comphenix.protocol.injector.player;
/*     */ 
/*     */ import com.google.common.base.Objects;
/*     */ import com.google.common.collect.BiMap;
/*     */ import com.google.common.collect.HashBiMap;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.ListIterator;
/*     */ 
/*     */ class ReplacedArrayList<TKey> extends ArrayList<TKey>
/*     */ {
/*     */   private static final long serialVersionUID = 1008492765999744804L;
/*  44 */   private BiMap<TKey, TKey> replaceMap = HashBiMap.create();
/*     */   private List<TKey> underlyingList;
/*     */ 
/*     */   public ReplacedArrayList(List<TKey> underlyingList)
/*     */   {
/*  48 */     this.underlyingList = underlyingList;
/*     */   }
/*     */ 
/*     */   protected void onReplacing(TKey inserting, TKey replacement)
/*     */   {
/*     */   }
/*     */ 
/*     */   protected void onInserting(TKey inserting)
/*     */   {
/*     */   }
/*     */ 
/*     */   protected void onRemoved(TKey removing)
/*     */   {
/*     */   }
/*     */ 
/*     */   public boolean add(TKey element)
/*     */   {
/*  80 */     onInserting(element);
/*     */ 
/*  82 */     if (this.replaceMap.containsKey(element)) {
/*  83 */       Object replacement = this.replaceMap.get(element);
/*  84 */       onReplacing(element, replacement);
/*  85 */       return delegate().add(replacement);
/*     */     }
/*  87 */     return delegate().add(element);
/*     */   }
/*     */ 
/*     */   public void add(int index, TKey element)
/*     */   {
/*  93 */     onInserting(element);
/*     */ 
/*  95 */     if (this.replaceMap.containsKey(element)) {
/*  96 */       Object replacement = this.replaceMap.get(element);
/*  97 */       onReplacing(element, replacement);
/*  98 */       delegate().add(index, replacement);
/*     */     } else {
/* 100 */       delegate().add(index, element);
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean addAll(Collection<? extends TKey> collection)
/*     */   {
/* 106 */     int oldSize = size();
/*     */ 
/* 108 */     for (Iterator i$ = collection.iterator(); i$.hasNext(); ) { Object element = i$.next();
/* 109 */       add(element); }
/* 110 */     return size() != oldSize;
/*     */   }
/*     */ 
/*     */   public boolean addAll(int index, Collection<? extends TKey> elements)
/*     */   {
/* 115 */     int oldSize = size();
/*     */ 
/* 117 */     for (Iterator i$ = elements.iterator(); i$.hasNext(); ) { Object element = i$.next();
/* 118 */       add(index++, element); }
/* 119 */     return size() != oldSize;
/*     */   }
/*     */ 
/*     */   public boolean remove(Object object)
/*     */   {
/* 125 */     boolean success = delegate().remove(object);
/*     */ 
/* 127 */     if (success)
/* 128 */       onRemoved(object);
/* 129 */     return success;
/*     */   }
/*     */ 
/*     */   public TKey remove(int index)
/*     */   {
/* 134 */     Object removed = delegate().remove(index);
/*     */ 
/* 136 */     if (removed != null)
/* 137 */       onRemoved(removed);
/* 138 */     return removed;
/*     */   }
/*     */ 
/*     */   public boolean removeAll(Collection<?> collection)
/*     */   {
/* 143 */     int oldSize = size();
/*     */ 
/* 146 */     for (Iterator i$ = collection.iterator(); i$.hasNext(); ) { Object element = i$.next();
/* 147 */       remove(element); }
/* 148 */     return size() != oldSize;
/*     */   }
/*     */ 
/*     */   protected List<TKey> delegate() {
/* 152 */     return this.underlyingList;
/*     */   }
/*     */ 
/*     */   public void clear()
/*     */   {
/* 157 */     for (Iterator i$ = delegate().iterator(); i$.hasNext(); ) { Object element = i$.next();
/* 158 */       onRemoved(element);
/*     */     }
/* 160 */     delegate().clear();
/*     */   }
/*     */ 
/*     */   public boolean contains(Object o)
/*     */   {
/* 165 */     return delegate().contains(o);
/*     */   }
/*     */ 
/*     */   public boolean containsAll(Collection<?> c)
/*     */   {
/* 170 */     return delegate().containsAll(c);
/*     */   }
/*     */ 
/*     */   public TKey get(int index)
/*     */   {
/* 175 */     return delegate().get(index);
/*     */   }
/*     */ 
/*     */   public int indexOf(Object o)
/*     */   {
/* 180 */     return delegate().indexOf(o);
/*     */   }
/*     */ 
/*     */   public boolean isEmpty()
/*     */   {
/* 185 */     return delegate().isEmpty();
/*     */   }
/*     */ 
/*     */   public Iterator<TKey> iterator()
/*     */   {
/* 190 */     return delegate().iterator();
/*     */   }
/*     */ 
/*     */   public int lastIndexOf(Object o)
/*     */   {
/* 195 */     return delegate().lastIndexOf(o);
/*     */   }
/*     */ 
/*     */   public ListIterator<TKey> listIterator()
/*     */   {
/* 200 */     return delegate().listIterator();
/*     */   }
/*     */ 
/*     */   public ListIterator<TKey> listIterator(int index)
/*     */   {
/* 205 */     return delegate().listIterator(index);
/*     */   }
/*     */ 
/*     */   public boolean retainAll(Collection<?> c)
/*     */   {
/* 210 */     int oldSize = size();
/*     */ 
/* 212 */     for (Iterator it = delegate().iterator(); it.hasNext(); ) {
/* 213 */       Object current = it.next();
/*     */ 
/* 216 */       if (!c.contains(current)) {
/* 217 */         it.remove();
/* 218 */         onRemoved(current);
/*     */       }
/*     */     }
/* 221 */     return size() != oldSize;
/*     */   }
/*     */ 
/*     */   public TKey set(int index, TKey element)
/*     */   {
/* 227 */     if (this.replaceMap.containsKey(element)) {
/* 228 */       Object replacement = this.replaceMap.get(element);
/* 229 */       onReplacing(element, replacement);
/* 230 */       return delegate().set(index, replacement);
/*     */     }
/* 232 */     return delegate().set(index, element);
/*     */   }
/*     */ 
/*     */   public int size()
/*     */   {
/* 238 */     return delegate().size();
/*     */   }
/*     */ 
/*     */   public List<TKey> subList(int fromIndex, int toIndex)
/*     */   {
/* 243 */     return delegate().subList(fromIndex, toIndex);
/*     */   }
/*     */ 
/*     */   public Object[] toArray()
/*     */   {
/* 248 */     return delegate().toArray();
/*     */   }
/*     */ 
/*     */   public <T> T[] toArray(T[] a)
/*     */   {
/* 253 */     return delegate().toArray(a);
/*     */   }
/*     */ 
/*     */   public synchronized void addMapping(TKey target, TKey replacement)
/*     */   {
/* 264 */     addMapping(target, replacement, false);
/*     */   }
/*     */ 
/*     */   public TKey getMapping(TKey target)
/*     */   {
/* 273 */     return this.replaceMap.get(target);
/*     */   }
/*     */ 
/*     */   public synchronized void addMapping(TKey target, TKey replacement, boolean ignoreExisting)
/*     */   {
/* 285 */     this.replaceMap.put(target, replacement);
/*     */ 
/* 288 */     if (!ignoreExisting)
/* 289 */       replaceAll(target, replacement);
/*     */   }
/*     */ 
/*     */   public synchronized TKey removeMapping(TKey target)
/*     */   {
/* 300 */     if (this.replaceMap.containsKey(target)) {
/* 301 */       Object replacement = this.replaceMap.get(target);
/* 302 */       this.replaceMap.remove(target);
/*     */ 
/* 305 */       replaceAll(replacement, target);
/* 306 */       return replacement;
/*     */     }
/* 308 */     return null;
/*     */   }
/*     */ 
/*     */   public synchronized TKey swapMapping(TKey target)
/*     */   {
/* 318 */     Object replacement = removeMapping(target);
/*     */ 
/* 321 */     if (replacement != null) {
/* 322 */       this.replaceMap.put(replacement, target);
/*     */     }
/* 324 */     return replacement;
/*     */   }
/*     */ 
/*     */   public synchronized void replaceAll(TKey find, TKey replace)
/*     */   {
/* 333 */     for (int i = 0; i < this.underlyingList.size(); i++)
/* 334 */       if (Objects.equal(this.underlyingList.get(i), find)) {
/* 335 */         onReplacing(find, replace);
/* 336 */         this.underlyingList.set(i, replace);
/*     */       }
/*     */   }
/*     */ 
/*     */   public synchronized void revertAll()
/*     */   {
/* 347 */     if (this.replaceMap.size() < 1) {
/* 348 */       return;
/*     */     }
/* 350 */     BiMap inverse = this.replaceMap.inverse();
/*     */ 
/* 352 */     for (int i = 0; i < this.underlyingList.size(); i++) {
/* 353 */       Object replaced = this.underlyingList.get(i);
/*     */ 
/* 355 */       if (inverse.containsKey(replaced)) {
/* 356 */         Object original = inverse.get(replaced);
/* 357 */         onReplacing(replaced, original);
/* 358 */         this.underlyingList.set(i, original);
/*     */       }
/*     */     }
/*     */ 
/* 362 */     this.replaceMap.clear();
/*     */   }
/*     */ 
/*     */   protected void finalize() throws Throwable
/*     */   {
/* 367 */     revertAll();
/* 368 */     super.finalize();
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.injector.player.ReplacedArrayList
 * JD-Core Version:    0.6.2
 */