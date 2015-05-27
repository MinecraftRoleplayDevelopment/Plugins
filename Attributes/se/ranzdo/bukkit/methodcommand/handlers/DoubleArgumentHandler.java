/*    */ package se.ranzdo.bukkit.methodcommand.handlers;
/*    */ 
/*    */ import org.bukkit.command.CommandSender;
/*    */ import se.ranzdo.bukkit.methodcommand.CommandArgument;
/*    */ import se.ranzdo.bukkit.methodcommand.TransformError;
/*    */ 
/*    */ public class DoubleArgumentHandler extends NumberArgumentHandler<Double>
/*    */ {
/*    */   public DoubleArgumentHandler()
/*    */   {
/* 10 */     setMessage("parse_error", "The parameter [%p] is not a number");
/*    */   }
/*    */ 
/*    */   public Double transform(CommandSender sender, CommandArgument argument, String value) throws TransformError
/*    */   {
/*    */     try {
/* 16 */       return Double.valueOf(Double.parseDouble(value));
/*    */     } catch (NumberFormatException e) {
/*    */     }
/* 19 */     throw new TransformError(argument.getMessage("parse_error"));
/*    */   }
/*    */ }

/* Location:           D:\Github\Mechanics\ItemAttributes.jar
 * Qualified Name:     se.ranzdo.bukkit.methodcommand.handlers.DoubleArgumentHandler
 * JD-Core Version:    0.6.2
 */