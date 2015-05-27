/*     */ package com.comphenix.protocol.timing;
/*     */ 
/*     */ import com.google.common.base.Preconditions;
/*     */ import com.google.common.collect.ImmutableList;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ 
/*     */ public class HistogramStream extends OnlineComputation
/*     */ {
/*     */   protected List<StatisticsStream> bins;
/*     */   protected StatisticsStream current;
/*     */   protected int binWidth;
/*     */   protected int count;
/*     */ 
/*     */   public HistogramStream(int binWidth)
/*     */   {
/*  39 */     this(new ArrayList(), new StatisticsStream(), binWidth);
/*     */   }
/*     */ 
/*     */   public HistogramStream(HistogramStream other)
/*     */   {
/*  48 */     for (StatisticsStream stream : other.bins) {
/*  49 */       StatisticsStream copy = stream.copy();
/*     */ 
/*  52 */       if (stream == other.current)
/*  53 */         this.current = copy;
/*  54 */       this.bins.add(copy);
/*     */     }
/*  56 */     this.binWidth = other.binWidth;
/*     */   }
/*     */ 
/*     */   protected HistogramStream(List<StatisticsStream> bins, StatisticsStream current, int binWidth)
/*     */   {
/*  66 */     if (binWidth < 1)
/*  67 */       throw new IllegalArgumentException("binWidth cannot be less than 1");
/*  68 */     this.bins = ((List)Preconditions.checkNotNull(bins, "bins cannot be NULL"));
/*  69 */     this.current = ((StatisticsStream)Preconditions.checkNotNull(current, "current cannot be NULL"));
/*  70 */     this.binWidth = binWidth;
/*     */ 
/*  72 */     if (!this.bins.contains(current))
/*  73 */       this.bins.add(current);
/*     */   }
/*     */ 
/*     */   public HistogramStream copy()
/*     */   {
/*  79 */     return new HistogramStream(this);
/*     */   }
/*     */ 
/*     */   public ImmutableList<StatisticsStream> getBins()
/*     */   {
/*  87 */     return ImmutableList.copyOf(this.bins);
/*     */   }
/*     */ 
/*     */   public void observe(double value)
/*     */   {
/*  92 */     checkOverflow();
/*  93 */     this.count += 1;
/*  94 */     this.current.observe(value);
/*     */   }
/*     */ 
/*     */   protected void checkOverflow()
/*     */   {
/* 101 */     if (this.current.getCount() >= this.binWidth)
/* 102 */       this.bins.add(this.current = new StatisticsStream());
/*     */   }
/*     */ 
/*     */   public StatisticsStream getTotal()
/*     */   {
/* 113 */     StatisticsStream sum = null;
/*     */ 
/* 115 */     for (StatisticsStream stream : this.bins) {
/* 116 */       sum = sum != null ? stream.add(sum) : stream;
/*     */     }
/* 118 */     return sum;
/*     */   }
/*     */ 
/*     */   public int getCount()
/*     */   {
/* 123 */     return this.count;
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.timing.HistogramStream
 * JD-Core Version:    0.6.2
 */