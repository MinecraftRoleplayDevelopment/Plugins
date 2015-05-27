/*    */ package com.comphenix.protocol.error;
/*    */ 
/*    */ import com.comphenix.protocol.reflect.PrettyPrinter;
/*    */ import java.io.PrintStream;
/*    */ import org.bukkit.plugin.Plugin;
/*    */ 
/*    */ public class BasicErrorReporter
/*    */   implements ErrorReporter
/*    */ {
/*    */   private final PrintStream output;
/*    */ 
/*    */   public BasicErrorReporter()
/*    */   {
/* 24 */     this(System.err);
/*    */   }
/*    */ 
/*    */   public BasicErrorReporter(PrintStream output)
/*    */   {
/* 32 */     this.output = output;
/*    */   }
/*    */ 
/*    */   public void reportMinimal(Plugin sender, String methodName, Throwable error)
/*    */   {
/* 37 */     this.output.println("Unhandled exception occured in " + methodName + " for " + sender.getName());
/* 38 */     error.printStackTrace(this.output);
/*    */   }
/*    */ 
/*    */   public void reportMinimal(Plugin sender, String methodName, Throwable error, Object[] parameters)
/*    */   {
/* 43 */     reportMinimal(sender, methodName, error);
/*    */ 
/* 46 */     printParameters(parameters);
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
/*    */   public void reportWarning(Object sender, Report report)
/*    */   {
/* 62 */     this.output.println("[" + sender.getClass().getSimpleName() + "] " + report.getReportMessage());
/*    */ 
/* 64 */     if (report.getException() != null) {
/* 65 */       report.getException().printStackTrace(this.output);
/*    */     }
/* 67 */     printParameters(report.getCallerParameters());
/*    */   }
/*    */ 
/*    */   public void reportWarning(Object sender, Report.ReportBuilder reportBuilder)
/*    */   {
/* 72 */     reportWarning(sender, reportBuilder.build());
/*    */   }
/*    */ 
/*    */   public void reportDetailed(Object sender, Report report)
/*    */   {
/* 78 */     reportWarning(sender, report);
/*    */   }
/*    */ 
/*    */   public void reportDetailed(Object sender, Report.ReportBuilder reportBuilder)
/*    */   {
/* 83 */     reportWarning(sender, reportBuilder);
/*    */   }
/*    */ 
/*    */   private void printParameters(Object[] parameters)
/*    */   {
/* 91 */     if ((parameters != null) && (parameters.length > 0)) {
/* 92 */       this.output.println("Parameters: ");
/*    */       try
/*    */       {
/* 95 */         for (Object parameter : parameters)
/* 96 */           if (parameter == null)
/* 97 */             this.output.println("[NULL]");
/*    */           else
/* 99 */             this.output.println(PrettyPrinter.printObject(parameter));
/*    */       }
/*    */       catch (IllegalAccessException e)
/*    */       {
/* 103 */         e.printStackTrace();
/*    */       }
/*    */     }
/*    */   }
/*    */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.error.BasicErrorReporter
 * JD-Core Version:    0.6.2
 */