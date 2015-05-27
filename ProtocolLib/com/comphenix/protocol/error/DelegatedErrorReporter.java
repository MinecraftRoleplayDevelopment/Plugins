/*    */ package com.comphenix.protocol.error;
/*    */ 
/*    */ import org.bukkit.plugin.Plugin;
/*    */ 
/*    */ public class DelegatedErrorReporter
/*    */   implements ErrorReporter
/*    */ {
/*    */   private final ErrorReporter delegated;
/*    */ 
/*    */   public DelegatedErrorReporter(ErrorReporter delegated)
/*    */   {
/* 19 */     this.delegated = delegated;
/*    */   }
/*    */ 
/*    */   public ErrorReporter getDelegated()
/*    */   {
/* 27 */     return this.delegated;
/*    */   }
/*    */ 
/*    */   public void reportMinimal(Plugin sender, String methodName, Throwable error)
/*    */   {
/* 32 */     this.delegated.reportMinimal(sender, methodName, error);
/*    */   }
/*    */ 
/*    */   public void reportMinimal(Plugin sender, String methodName, Throwable error, Object[] parameters)
/*    */   {
/* 37 */     this.delegated.reportMinimal(sender, methodName, error, parameters);
/*    */   }
/*    */ 
/*    */   public void reportDebug(Object sender, Report report)
/*    */   {
/* 42 */     Report transformed = filterReport(sender, report, false);
/*    */ 
/* 44 */     if (transformed != null)
/* 45 */       this.delegated.reportDebug(sender, transformed);
/*    */   }
/*    */ 
/*    */   public void reportWarning(Object sender, Report report)
/*    */   {
/* 51 */     Report transformed = filterReport(sender, report, false);
/*    */ 
/* 53 */     if (transformed != null)
/* 54 */       this.delegated.reportWarning(sender, transformed);
/*    */   }
/*    */ 
/*    */   public void reportDetailed(Object sender, Report report)
/*    */   {
/* 60 */     Report transformed = filterReport(sender, report, true);
/*    */ 
/* 62 */     if (transformed != null)
/* 63 */       this.delegated.reportDetailed(sender, transformed);
/*    */   }
/*    */ 
/*    */   protected Report filterReport(Object sender, Report report, boolean detailed)
/*    */   {
/* 77 */     return report;
/*    */   }
/*    */ 
/*    */   public void reportWarning(Object sender, Report.ReportBuilder reportBuilder)
/*    */   {
/* 82 */     reportWarning(sender, reportBuilder.build());
/*    */   }
/*    */ 
/*    */   public void reportDetailed(Object sender, Report.ReportBuilder reportBuilder)
/*    */   {
/* 87 */     reportDetailed(sender, reportBuilder.build());
/*    */   }
/*    */ 
/*    */   public void reportDebug(Object sender, Report.ReportBuilder builder)
/*    */   {
/* 92 */     reportDebug(sender, builder.build());
/*    */   }
/*    */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.error.DelegatedErrorReporter
 * JD-Core Version:    0.6.2
 */