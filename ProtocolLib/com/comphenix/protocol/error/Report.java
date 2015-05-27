/*     */ package com.comphenix.protocol.error;
/*     */ 
/*     */ import java.util.Arrays;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import javax.annotation.Nullable;
/*     */ 
/*     */ public class Report
/*     */ {
/*     */   private final ReportType type;
/*     */   private final Throwable exception;
/*     */   private final Object[] messageParameters;
/*     */   private final Object[] callerParameters;
/*     */   private final long rateLimit;
/*     */ 
/*     */   public static ReportBuilder newBuilder(ReportType type)
/*     */   {
/* 116 */     return new ReportBuilder(null).type(type);
/*     */   }
/*     */ 
/*     */   protected Report(ReportType type, @Nullable Throwable exception, @Nullable Object[] messageParameters, @Nullable Object[] callerParameters)
/*     */   {
/* 128 */     this(type, exception, messageParameters, callerParameters, 0L);
/*     */   }
/*     */ 
/*     */   protected Report(ReportType type, @Nullable Throwable exception, @Nullable Object[] messageParameters, @Nullable Object[] callerParameters, long rateLimit)
/*     */   {
/* 141 */     if (type == null)
/* 142 */       throw new IllegalArgumentException("type cannot be NULL.");
/* 143 */     this.type = type;
/* 144 */     this.exception = exception;
/* 145 */     this.messageParameters = messageParameters;
/* 146 */     this.callerParameters = callerParameters;
/* 147 */     this.rateLimit = rateLimit;
/*     */   }
/*     */ 
/*     */   public String getReportMessage()
/*     */   {
/* 155 */     return this.type.getMessage(this.messageParameters);
/*     */   }
/*     */ 
/*     */   public Object[] getMessageParameters()
/*     */   {
/* 165 */     return this.messageParameters;
/*     */   }
/*     */ 
/*     */   public Object[] getCallerParameters()
/*     */   {
/* 173 */     return this.callerParameters;
/*     */   }
/*     */ 
/*     */   public ReportType getType()
/*     */   {
/* 181 */     return this.type;
/*     */   }
/*     */ 
/*     */   public Throwable getException()
/*     */   {
/* 189 */     return this.exception;
/*     */   }
/*     */ 
/*     */   public boolean hasMessageParameters()
/*     */   {
/* 197 */     return (this.messageParameters != null) && (this.messageParameters.length > 0);
/*     */   }
/*     */ 
/*     */   public boolean hasCallerParameters()
/*     */   {
/* 205 */     return (this.callerParameters != null) && (this.callerParameters.length > 0);
/*     */   }
/*     */ 
/*     */   public long getRateLimit()
/*     */   {
/* 215 */     return this.rateLimit;
/*     */   }
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 220 */     int prime = 31;
/* 221 */     int result = 1;
/* 222 */     result = 31 * result + Arrays.hashCode(this.callerParameters);
/* 223 */     result = 31 * result + Arrays.hashCode(this.messageParameters);
/* 224 */     result = 31 * result + (this.type == null ? 0 : this.type.hashCode());
/* 225 */     return result;
/*     */   }
/*     */ 
/*     */   public boolean equals(Object obj)
/*     */   {
/* 230 */     if (this == obj)
/* 231 */       return true;
/* 232 */     if ((obj instanceof Report)) {
/* 233 */       Report other = (Report)obj;
/* 234 */       return (this.type == other.type) && (Arrays.equals(this.callerParameters, other.callerParameters)) && (Arrays.equals(this.messageParameters, other.messageParameters));
/*     */     }
/*     */ 
/* 238 */     return false;
/*     */   }
/*     */ 
/*     */   public static class ReportBuilder
/*     */   {
/*     */     private ReportType type;
/*     */     private Throwable exception;
/*     */     private Object[] messageParameters;
/*     */     private Object[] callerParameters;
/*     */     private long rateLimit;
/*     */ 
/*     */     public ReportBuilder type(ReportType type)
/*     */     {
/*  42 */       if (type == null)
/*  43 */         throw new IllegalArgumentException("Report type cannot be set to NULL.");
/*  44 */       this.type = type;
/*  45 */       return this;
/*     */     }
/*     */ 
/*     */     public ReportBuilder error(@Nullable Throwable exception)
/*     */     {
/*  54 */       this.exception = exception;
/*  55 */       return this;
/*     */     }
/*     */ 
/*     */     public ReportBuilder messageParam(@Nullable Object[] messageParameters)
/*     */     {
/*  64 */       this.messageParameters = messageParameters;
/*  65 */       return this;
/*     */     }
/*     */ 
/*     */     public ReportBuilder callerParam(@Nullable Object[] callerParameters)
/*     */     {
/*  74 */       this.callerParameters = callerParameters;
/*  75 */       return this;
/*     */     }
/*     */ 
/*     */     public ReportBuilder rateLimit(long rateLimit)
/*     */     {
/*  85 */       if (rateLimit < 0L)
/*  86 */         throw new IllegalArgumentException("Rate limit cannot be less than zero.");
/*  87 */       this.rateLimit = rateLimit;
/*  88 */       return this;
/*     */     }
/*     */ 
/*     */     public ReportBuilder rateLimit(long rateLimit, TimeUnit rateUnit)
/*     */     {
/*  98 */       return rateLimit(TimeUnit.NANOSECONDS.convert(rateLimit, rateUnit));
/*     */     }
/*     */ 
/*     */     public Report build()
/*     */     {
/* 106 */       return new Report(this.type, this.exception, this.messageParameters, this.callerParameters, this.rateLimit);
/*     */     }
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.error.Report
 * JD-Core Version:    0.6.2
 */