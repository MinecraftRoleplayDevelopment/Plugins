/*     */ package com.comphenix.protocol.concurrency;
/*     */ 
/*     */ import com.google.common.base.Objects;
/*     */ import com.google.common.collect.Range;
/*     */ import java.util.HashSet;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.NavigableMap;
/*     */ import java.util.Set;
/*     */ import java.util.TreeMap;
/*     */ 
/*     */ public abstract class AbstractIntervalTree<TKey extends Comparable<TKey>, TValue>
/*     */ {
/* 136 */   protected NavigableMap<TKey, AbstractIntervalTree<TKey, TValue>.EndPoint> bounds = new TreeMap();
/*     */ 
/*     */   public Set<AbstractIntervalTree<TKey, TValue>.Entry> remove(TKey lowerBound, TKey upperBound)
/*     */   {
/* 144 */     return remove(lowerBound, upperBound, false);
/*     */   }
/*     */ 
/*     */   public Set<AbstractIntervalTree<TKey, TValue>.Entry> remove(TKey lowerBound, TKey upperBound, boolean preserveDifference)
/*     */   {
/* 154 */     checkBounds(lowerBound, upperBound);
/* 155 */     NavigableMap range = this.bounds.subMap(lowerBound, true, upperBound, true);
/*     */ 
/* 157 */     EndPoint first = getNextEndPoint(lowerBound, true);
/* 158 */     EndPoint last = getPreviousEndPoint(upperBound, true);
/*     */ 
/* 161 */     EndPoint previous = null;
/* 162 */     EndPoint next = null;
/*     */ 
/* 164 */     Set resized = new HashSet();
/* 165 */     Set removed = new HashSet();
/*     */ 
/* 168 */     if ((first != null) && (first.state == State.CLOSE)) {
/* 169 */       previous = getPreviousEndPoint(first.key, false);
/*     */ 
/* 172 */       if (previous != null) {
/* 173 */         removed.add(getEntry(previous, first));
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 178 */     if ((last != null) && (last.state == State.OPEN)) {
/* 179 */       next = getNextEndPoint(last.key, false);
/*     */ 
/* 181 */       if (next != null) {
/* 182 */         removed.add(getEntry(last, next));
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 187 */     removeEntrySafely(previous, first);
/* 188 */     removeEntrySafely(last, next);
/*     */ 
/* 191 */     if (preserveDifference) {
/* 192 */       if (previous != null) {
/* 193 */         resized.add(putUnsafe(previous.key, decrementKey(lowerBound), previous.value));
/*     */       }
/* 195 */       if (next != null) {
/* 196 */         resized.add(putUnsafe(incrementKey(upperBound), next.key, next.value));
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 201 */     getEntries(removed, range);
/* 202 */     invokeEntryRemoved(removed);
/*     */ 
/* 204 */     if (preserveDifference) {
/* 205 */       invokeEntryAdded(resized);
/*     */     }
/*     */ 
/* 209 */     range.clear();
/* 210 */     return removed;
/*     */   }
/*     */ 
/*     */   protected AbstractIntervalTree<TKey, TValue>.Entry getEntry(AbstractIntervalTree<TKey, TValue>.EndPoint left, AbstractIntervalTree<TKey, TValue>.EndPoint right)
/*     */   {
/* 220 */     if (left == null)
/* 221 */       throw new IllegalArgumentException("left endpoint cannot be NULL.");
/* 222 */     if (right == null) {
/* 223 */       throw new IllegalArgumentException("right endpoint cannot be NULL.");
/*     */     }
/*     */ 
/* 226 */     if (right.key.compareTo(left.key) < 0) {
/* 227 */       return getEntry(right, left);
/*     */     }
/* 229 */     return new Entry(left, right);
/*     */   }
/*     */ 
/*     */   private void removeEntrySafely(AbstractIntervalTree<TKey, TValue>.EndPoint left, AbstractIntervalTree<TKey, TValue>.EndPoint right)
/*     */   {
/* 234 */     if ((left != null) && (right != null)) {
/* 235 */       this.bounds.remove(left.key);
/* 236 */       this.bounds.remove(right.key);
/*     */     }
/*     */   }
/*     */ 
/*     */   protected AbstractIntervalTree<TKey, TValue>.EndPoint addEndPoint(TKey key, TValue value, State state)
/*     */   {
/* 242 */     EndPoint endPoint = (EndPoint)this.bounds.get(key);
/*     */ 
/* 244 */     if (endPoint != null) {
/* 245 */       endPoint.state = State.BOTH;
/*     */     } else {
/* 247 */       endPoint = new EndPoint(state, key, value);
/* 248 */       this.bounds.put(key, endPoint);
/*     */     }
/* 250 */     return endPoint;
/*     */   }
/*     */ 
/*     */   public void put(TKey lowerBound, TKey upperBound, TValue value)
/*     */   {
/* 265 */     remove(lowerBound, upperBound, true);
/* 266 */     invokeEntryAdded(putUnsafe(lowerBound, upperBound, value));
/*     */   }
/*     */ 
/*     */   private AbstractIntervalTree<TKey, TValue>.Entry putUnsafe(TKey lowerBound, TKey upperBound, TValue value)
/*     */   {
/* 277 */     if (value != null) {
/* 278 */       EndPoint left = addEndPoint(lowerBound, value, State.OPEN);
/* 279 */       EndPoint right = addEndPoint(upperBound, value, State.CLOSE);
/*     */ 
/* 281 */       return new Entry(left, right);
/*     */     }
/* 283 */     return null;
/*     */   }
/*     */ 
/*     */   private void checkBounds(TKey lowerBound, TKey upperBound)
/*     */   {
/* 293 */     if (lowerBound == null)
/* 294 */       throw new IllegalAccessError("lowerbound cannot be NULL.");
/* 295 */     if (upperBound == null)
/* 296 */       throw new IllegalAccessError("upperBound cannot be NULL.");
/* 297 */     if (upperBound.compareTo(lowerBound) < 0)
/* 298 */       throw new IllegalArgumentException("upperBound cannot be less than lowerBound.");
/*     */   }
/*     */ 
/*     */   public boolean containsKey(TKey key)
/*     */   {
/* 307 */     return getEndPoint(key) != null;
/*     */   }
/*     */ 
/*     */   public Set<AbstractIntervalTree<TKey, TValue>.Entry> entrySet()
/*     */   {
/* 316 */     Set result = new HashSet();
/* 317 */     getEntries(result, this.bounds);
/* 318 */     return result;
/*     */   }
/*     */ 
/*     */   public void clear()
/*     */   {
/* 325 */     if (!this.bounds.isEmpty())
/* 326 */       remove((Comparable)this.bounds.firstKey(), (Comparable)this.bounds.lastKey());
/*     */   }
/*     */ 
/*     */   private void getEntries(Set<AbstractIntervalTree<TKey, TValue>.Entry> destination, NavigableMap<TKey, AbstractIntervalTree<TKey, TValue>.EndPoint> map)
/*     */   {
/* 336 */     Map.Entry last = null;
/*     */ 
/* 338 */     for (Map.Entry entry : map.entrySet())
/* 339 */       switch (1.$SwitchMap$com$comphenix$protocol$concurrency$AbstractIntervalTree$State[((EndPoint)entry.getValue()).state.ordinal()]) {
/*     */       case 1:
/* 341 */         EndPoint point = (EndPoint)entry.getValue();
/* 342 */         destination.add(new Entry(point, point));
/* 343 */         break;
/*     */       case 2:
/* 345 */         if (last != null)
/* 346 */           destination.add(new Entry((EndPoint)last.getValue(), (EndPoint)entry.getValue())); break;
/*     */       case 3:
/* 351 */         last = entry;
/* 352 */         break;
/*     */       default:
/* 354 */         throw new IllegalStateException("Illegal open/close state detected.");
/*     */       }
/*     */   }
/*     */ 
/*     */   public void putAll(AbstractIntervalTree<TKey, TValue> other)
/*     */   {
/* 365 */     for (Entry entry : other.entrySet())
/* 366 */       put(entry.left.key, entry.right.key, entry.getValue());
/*     */   }
/*     */ 
/*     */   public TValue get(TKey key)
/*     */   {
/* 376 */     EndPoint point = getEndPoint(key);
/*     */ 
/* 378 */     if (point != null) {
/* 379 */       return point.value;
/*     */     }
/* 381 */     return null;
/*     */   }
/*     */ 
/*     */   protected AbstractIntervalTree<TKey, TValue>.EndPoint getEndPoint(TKey key)
/*     */   {
/* 390 */     EndPoint ends = (EndPoint)this.bounds.get(key);
/*     */ 
/* 392 */     if (ends != null)
/*     */     {
/* 394 */       if (ends.state == State.CLOSE) {
/* 395 */         Map.Entry left = this.bounds.floorEntry(decrementKey(key));
/* 396 */         return left != null ? (EndPoint)left.getValue() : null;
/*     */       }
/*     */ 
/* 399 */       return ends;
/*     */     }
/*     */ 
/* 404 */     Map.Entry left = this.bounds.floorEntry(key);
/*     */ 
/* 407 */     if ((left != null) && (((EndPoint)left.getValue()).state == State.OPEN)) {
/* 408 */       return (EndPoint)left.getValue();
/*     */     }
/* 410 */     return null;
/*     */   }
/*     */ 
/*     */   protected AbstractIntervalTree<TKey, TValue>.EndPoint getPreviousEndPoint(TKey point, boolean inclusive)
/*     */   {
/* 422 */     if (point != null) {
/* 423 */       Map.Entry previous = this.bounds.floorEntry(inclusive ? point : decrementKey(point));
/*     */ 
/* 425 */       if (previous != null)
/* 426 */         return (EndPoint)previous.getValue();
/*     */     }
/* 428 */     return null;
/*     */   }
/*     */ 
/*     */   protected AbstractIntervalTree<TKey, TValue>.EndPoint getNextEndPoint(TKey point, boolean inclusive)
/*     */   {
/* 438 */     if (point != null) {
/* 439 */       Map.Entry next = this.bounds.ceilingEntry(inclusive ? point : incrementKey(point));
/*     */ 
/* 441 */       if (next != null)
/* 442 */         return (EndPoint)next.getValue();
/*     */     }
/* 444 */     return null;
/*     */   }
/*     */ 
/*     */   private void invokeEntryAdded(AbstractIntervalTree<TKey, TValue>.Entry added) {
/* 448 */     if (added != null)
/* 449 */       onEntryAdded(added);
/*     */   }
/*     */ 
/*     */   private void invokeEntryAdded(Set<AbstractIntervalTree<TKey, TValue>.Entry> added)
/*     */   {
/* 454 */     for (Entry entry : added)
/* 455 */       onEntryAdded(entry);
/*     */   }
/*     */ 
/*     */   private void invokeEntryRemoved(Set<AbstractIntervalTree<TKey, TValue>.Entry> removed)
/*     */   {
/* 460 */     for (Entry entry : removed)
/* 461 */       onEntryRemoved(entry);
/*     */   }
/*     */ 
/*     */   protected void onEntryAdded(AbstractIntervalTree<TKey, TValue>.Entry added)
/*     */   {
/*     */   }
/*     */ 
/*     */   protected void onEntryRemoved(AbstractIntervalTree<TKey, TValue>.Entry removed)
/*     */   {
/*     */   }
/*     */ 
/*     */   protected abstract TKey decrementKey(TKey paramTKey);
/*     */ 
/*     */   protected abstract TKey incrementKey(TKey paramTKey);
/*     */ 
/*     */   protected class EndPoint
/*     */   {
/*     */     public AbstractIntervalTree.State state;
/*     */     public TValue value;
/*     */     public TKey key;
/*     */ 
/*     */     public EndPoint(TKey state, TValue key)
/*     */     {
/* 129 */       this.state = state;
/* 130 */       this.key = key;
/* 131 */       this.value = value;
/*     */     }
/*     */   }
/*     */ 
/*     */   public class Entry
/*     */     implements Map.Entry<Range<TKey>, TValue>
/*     */   {
/*     */     private AbstractIntervalTree<TKey, TValue>.EndPoint left;
/*     */     private AbstractIntervalTree<TKey, TValue>.EndPoint right;
/*     */ 
/*     */     Entry(AbstractIntervalTree<TKey, TValue>.EndPoint left)
/*     */     {
/*  56 */       if (left == null)
/*  57 */         throw new IllegalAccessError("left cannot be NUll");
/*  58 */       if (right == null)
/*  59 */         throw new IllegalAccessError("right cannot be NUll");
/*  60 */       if (left.key.compareTo(right.key) > 0) {
/*  61 */         throw new IllegalArgumentException("Left key (" + left.key + ") cannot be greater than the right key (" + right.key + ")");
/*     */       }
/*     */ 
/*  64 */       this.left = left;
/*  65 */       this.right = right;
/*     */     }
/*     */ 
/*     */     public Range<TKey> getKey()
/*     */     {
/*  70 */       return Range.closed(this.left.key, this.right.key);
/*     */     }
/*     */ 
/*     */     public TValue getValue()
/*     */     {
/*  75 */       return this.left.value;
/*     */     }
/*     */ 
/*     */     public TValue setValue(TValue value)
/*     */     {
/*  80 */       Object old = this.left.value;
/*     */ 
/*  83 */       this.left.value = value;
/*  84 */       this.right.value = value;
/*  85 */       return old;
/*     */     }
/*     */ 
/*     */     public boolean equals(Object obj)
/*     */     {
/*  92 */       if (obj == this)
/*  93 */         return true;
/*  94 */       if ((obj instanceof Entry)) {
/*  95 */         return (Objects.equal(this.left.key, ((Entry)obj).left.key)) && (Objects.equal(this.right.key, ((Entry)obj).right.key)) && (Objects.equal(this.left.value, ((Entry)obj).left.value));
/*     */       }
/*     */ 
/*  99 */       return false;
/*     */     }
/*     */ 
/*     */     public int hashCode()
/*     */     {
/* 105 */       return Objects.hashCode(new Object[] { this.left.key, this.right.key, this.left.value });
/*     */     }
/*     */ 
/*     */     public String toString()
/*     */     {
/* 110 */       return String.format("Value %s at [%s, %s]", new Object[] { this.left.value, this.left.key, this.right.key });
/*     */     }
/*     */   }
/*     */ 
/*     */   protected static enum State
/*     */   {
/*  43 */     OPEN, 
/*  44 */     CLOSE, 
/*  45 */     BOTH;
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.concurrency.AbstractIntervalTree
 * JD-Core Version:    0.6.2
 */