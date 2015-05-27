/*     */ package com.comphenix.protocol.wrappers.collection;
/*     */ 
/*     */ import java.util.Collection;
/*     */ import java.util.List;
/*     */ import java.util.ListIterator;
/*     */ 
/*     */ public abstract class ConvertedList<VInner, VOuter> extends ConvertedCollection<VInner, VOuter>
/*     */   implements List<VOuter>
/*     */ {
/*     */   private List<VInner> inner;
/*     */ 
/*     */   public ConvertedList(List<VInner> inner)
/*     */   {
/*  36 */     super(inner);
/*  37 */     this.inner = inner;
/*     */   }
/*     */ 
/*     */   public void add(int index, VOuter element)
/*     */   {
/*  42 */     this.inner.add(index, toInner(element));
/*     */   }
/*     */ 
/*     */   public boolean addAll(int index, Collection<? extends VOuter> c)
/*     */   {
/*  47 */     return this.inner.addAll(index, getInnerCollection(c));
/*     */   }
/*     */ 
/*     */   public VOuter get(int index)
/*     */   {
/*  52 */     return toOuter(this.inner.get(index));
/*     */   }
/*     */ 
/*     */   public int indexOf(Object o)
/*     */   {
/*  58 */     return this.inner.indexOf(toInner(o));
/*     */   }
/*     */ 
/*     */   public int lastIndexOf(Object o)
/*     */   {
/*  64 */     return this.inner.lastIndexOf(toInner(o));
/*     */   }
/*     */ 
/*     */   public ListIterator<VOuter> listIterator()
/*     */   {
/*  69 */     return listIterator(0);
/*     */   }
/*     */ 
/*     */   public ListIterator<VOuter> listIterator(int index)
/*     */   {
/*  74 */     final ListIterator innerIterator = this.inner.listIterator(index);
/*     */ 
/*  76 */     return new ListIterator()
/*     */     {
/*     */       public void add(VOuter e) {
/*  79 */         innerIterator.add(ConvertedList.this.toInner(e));
/*     */       }
/*     */ 
/*     */       public boolean hasNext()
/*     */       {
/*  84 */         return innerIterator.hasNext();
/*     */       }
/*     */ 
/*     */       public boolean hasPrevious()
/*     */       {
/*  89 */         return innerIterator.hasPrevious();
/*     */       }
/*     */ 
/*     */       public VOuter next()
/*     */       {
/*  94 */         return ConvertedList.this.toOuter(innerIterator.next());
/*     */       }
/*     */ 
/*     */       public int nextIndex()
/*     */       {
/*  99 */         return innerIterator.nextIndex();
/*     */       }
/*     */ 
/*     */       public VOuter previous()
/*     */       {
/* 104 */         return ConvertedList.this.toOuter(innerIterator.previous());
/*     */       }
/*     */ 
/*     */       public int previousIndex()
/*     */       {
/* 109 */         return innerIterator.previousIndex();
/*     */       }
/*     */ 
/*     */       public void remove()
/*     */       {
/* 114 */         innerIterator.remove();
/*     */       }
/*     */ 
/*     */       public void set(VOuter e)
/*     */       {
/* 119 */         innerIterator.set(ConvertedList.this.toInner(e));
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   public VOuter remove(int index)
/*     */   {
/* 126 */     return toOuter(this.inner.remove(index));
/*     */   }
/*     */ 
/*     */   public VOuter set(int index, VOuter element)
/*     */   {
/* 131 */     return toOuter(this.inner.set(index, toInner(element)));
/*     */   }
/*     */ 
/*     */   public List<VOuter> subList(int fromIndex, int toIndex)
/*     */   {
/* 136 */     return new ConvertedList(this.inner.subList(fromIndex, toIndex))
/*     */     {
/*     */       protected VInner toInner(VOuter outer) {
/* 139 */         return ConvertedList.this.toInner(outer);
/*     */       }
/*     */ 
/*     */       protected VOuter toOuter(VInner inner)
/*     */       {
/* 144 */         return ConvertedList.this.toOuter(inner);
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   private ConvertedCollection<VOuter, VInner> getInnerCollection(Collection c)
/*     */   {
/* 151 */     return new ConvertedCollection(c)
/*     */     {
/*     */       protected VOuter toInner(VInner outer) {
/* 154 */         return ConvertedList.this.toOuter(outer);
/*     */       }
/*     */ 
/*     */       protected VInner toOuter(VOuter inner)
/*     */       {
/* 159 */         return ConvertedList.this.toInner(inner);
/*     */       }
/*     */     };
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.wrappers.collection.ConvertedList
 * JD-Core Version:    0.6.2
 */