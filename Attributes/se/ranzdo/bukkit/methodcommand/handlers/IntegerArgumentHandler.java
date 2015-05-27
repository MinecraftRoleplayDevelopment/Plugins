/*    */ package se.ranzdo.bukkit.methodcommand.handlers;
/*    */ 
/*    */ import org.bukkit.command.CommandSender;
/*    */ import se.ranzdo.bukkit.methodcommand.CommandArgument;
/*    */ import se.ranzdo.bukkit.methodcommand.TransformError;
/*    */ 
/*    */ public class IntegerArgumentHandler extends NumberArgumentHandler<Integer>
/*    */ {
/*    */   public IntegerArgumentHandler()
/*    */   {
/* 10 */     setMessage("parse_error", "The parameter [%p] is not an integer");
/*    */   }
/*    */ 
/*    */   public Integer transform(CommandSender sender, CommandArgument argument, String value) throws TransformError
/*    */   {
/*    */     try {
/* 16 */       return Integer.valueOf(Integer.parseInt(value));
/*    */     } catch (NumberFormatException e) {
/*    */     }
/* 19 */     throw new TransformError(argument.getMessage("parse_error"));
/*    */   }
/*    */ }

/* Location:           D:\Github\Mechanics\ItemAttributes.jar
 * Qualified Name:     se.ranzdo.bukkit.methodcommand.handlers.IntegerArgumentHandler
 * JD-Core Version:    0.6.2
 */