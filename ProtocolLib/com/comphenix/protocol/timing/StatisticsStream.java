/*     */ package com.comphenix.protocol.timing;
/*     */ 
/*     */ public class StatisticsStream extends OnlineComputation
/*     */ {
/*  12 */   private int count = 0;
/*  13 */   private double mean = 0.0D;
/*  14 */   private double m2 = 0.0D;
/*     */ 
/*  17 */   private double minimum = 1.7976931348623157E+308D;
/*  18 */   private double maximum = 0.0D;
/*     */ 
/*     */   public StatisticsStream()
/*     */   {
/*     */   }
/*     */ 
/*     */   public StatisticsStream(StatisticsStream other)
/*     */   {
/*  31 */     this.count = other.count;
/*  32 */     this.mean = other.mean;
/*  33 */     this.m2 = other.m2;
/*  34 */     this.minimum = other.minimum;
/*  35 */     this.maximum = other.maximum;
/*     */   }
/*     */ 
/*     */   public StatisticsStream copy()
/*     */   {
/*  40 */     return new StatisticsStream(this);
/*     */   }
/*     */ 
/*     */   public void observe(double value)
/*     */   {
/*  49 */     double delta = value - this.mean;
/*     */ 
/*  52 */     this.count += 1;
/*  53 */     this.mean += delta / this.count;
/*  54 */     this.m2 += delta * (value - this.mean);
/*     */ 
/*  57 */     if (value < this.minimum)
/*  58 */       this.minimum = value;
/*  59 */     if (value > this.maximum)
/*  60 */       this.maximum = value;
/*     */   }
/*     */ 
/*     */   public double getMean()
/*     */   {
/*  68 */     checkCount();
/*  69 */     return this.mean;
/*     */   }
/*     */ 
/*     */   public double getVariance()
/*     */   {
/*  77 */     checkCount();
/*  78 */     return this.m2 / (this.count - 1);
/*     */   }
/*     */ 
/*     */   public double getStandardDeviation()
/*     */   {
/*  86 */     return Math.sqrt(getVariance());
/*     */   }
/*     */ 
/*     */   public double getMinimum()
/*     */   {
/*  94 */     checkCount();
/*  95 */     return this.minimum;
/*     */   }
/*     */ 
/*     */   public double getMaximum()
/*     */   {
/* 103 */     checkCount();
/* 104 */     return this.maximum;
/*     */   }
/*     */ 
/*     */   public StatisticsStream add(StatisticsStream other)
/*     */   {
/* 113 */     if (this.count == 0)
/* 114 */       return other;
/* 115 */     if (other.count == 0) {
/* 116 */       return this;
/*     */     }
/* 118 */     StatisticsStream stream = new StatisticsStream();
/* 119 */     double delta = other.mean - this.mean;
/* 120 */     double n = this.count + other.count;
/*     */ 
/* 122 */     stream.count = ((int)n);
/* 123 */     this.mean += delta * (other.count / n);
/* 124 */     stream.m2 = (this.m2 + other.m2 + delta * delta * (this.count * other.count) / n);
/* 125 */     stream.minimum = Math.min(this.minimum, other.minimum);
/* 126 */     stream.maximum = Math.max(this.maximum, other.maximum);
/* 127 */     return stream;
/*     */   }
/*     */ 
/*     */   public int getCount()
/*     */   {
/* 136 */     return this.count;
/*     */   }
/*     */ 
/*     */   private void checkCount() {
/* 140 */     if (this.count == 0)
/* 141 */       throw new IllegalStateException("No observations in stream.");
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 147 */     if (this.count == 0) {
/* 148 */       return "StatisticsStream [Nothing recorded]";
/*     */     }
/* 150 */     return String.format("StatisticsStream [Average: %.3f, SD: %.3f, Min: %.3f, Max: %.3f, Count: %s]", new Object[] { Double.valueOf(getMean()), Double.valueOf(getStandardDeviation()), Double.valueOf(getMinimum()), Double.valueOf(getMaximum()), Integer.valueOf(getCount()) });
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.timing.StatisticsStream
 * JD-Core Version:    0.6.2
 */