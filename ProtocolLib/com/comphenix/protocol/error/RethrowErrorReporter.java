/*    */ package com.comphenix.protocol.error;
/*    */ 
/*    */ import com.google.common.base.Joiner;
/*    */ import org.bukkit.plugin.Plugin;
/*    */ 
/*    */ public class RethrowErrorReporter
/*    */   implements ErrorReporter
/*    */ {
/*    */   public void reportMinimal(Plugin sender, String methodName, Throwable error)
/*    */   {
/* 15 */     throw new RuntimeException("Minimal error by " + sender + " in " + methodName, error);
/*    */   }
/*    */ 
/*    */   public void reportMinimal(Plugin sender, String methodName, Throwable error, Object[] parameters)
/*    */   {
/* 20 */     throw new RuntimeException("Minimal error by " + sender + " in " + methodName + " with " + Joiner.on(",").join(parameters), error);
/*    */   }
/*    */ 
/*    */   public void reportDebug(Object sender, Report report)
/*    */   {
/*    */   }
/*    */ 
/*    */   public void reportDebug(Object sender, Report.ReportBuilder builder)
/*    */   {
/*    */   }
/*    */ 
/*    */   public void reportWarning(Object sender, Report.ReportBuilder reportBuilder)
/*    */   {
/* 36 */     reportWarning(sender, reportBuilder.build());
/*    */   }
/*    */ 
/*    */   public void reportWarning(Object sender, Report report)
/*    */   {
/* 41 */     throw new RuntimeException("Warning by " + sender + ": " + report);
/*    */   }
/*    */ 
/*    */   public void reportDetailed(Object sender, Report.ReportBuilder reportBuilder)
/*    */   {
/* 46 */     reportDetailed(sender, reportBuilder.build());
/*    */   }
/*    */ 
/*    */   public void reportDetailed(Object sender, Report report)
/*    */   {
/* 51 */     throw new RuntimeException("Detailed error " + sender + ": " + report, report.getException());
/*    */   }
/*    */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.error.RethrowErrorReporter
 * JD-Core Version:    0.6.2
 */