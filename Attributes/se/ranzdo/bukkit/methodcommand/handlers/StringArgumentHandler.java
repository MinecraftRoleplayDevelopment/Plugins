/*    */ package se.ranzdo.bukkit.methodcommand.handlers;
/*    */ 
/*    */ import org.bukkit.command.CommandSender;
/*    */ import se.ranzdo.bukkit.methodcommand.ArgumentHandler;
/*    */ import se.ranzdo.bukkit.methodcommand.ArgumentVerifier;
/*    */ import se.ranzdo.bukkit.methodcommand.CommandArgument;
/*    */ import se.ranzdo.bukkit.methodcommand.InvalidVerifyArgument;
/*    */ import se.ranzdo.bukkit.methodcommand.TransformError;
/*    */ import se.ranzdo.bukkit.methodcommand.VerifyError;
/*    */ 
/*    */ public class StringArgumentHandler extends ArgumentHandler<String>
/*    */ {
/*    */   public StringArgumentHandler()
/*    */   {
/* 15 */     setMessage("min_error", "The parameter [%p] must be more than %1 characters.");
/* 16 */     setMessage("max_error", "The parameter [%p] can't be more than %1 characters.");
/*    */ 
/* 18 */     addVerifier("min", new ArgumentVerifier()
/*    */     {
/*    */       public void verify(CommandSender sender, CommandArgument argument, String verifyName, String[] verifyArgs, String value, String valueRaw) throws VerifyError {
/* 21 */         if (verifyArgs.length != 1)
/* 22 */           throw new InvalidVerifyArgument(argument.getName());
/*    */         try
/*    */         {
/* 25 */           int min = Integer.parseInt(verifyArgs[0]);
/* 26 */           if (value.length() < min)
/* 27 */             throw new VerifyError(argument.getMessage("min_error", new String[] { verifyArgs[0] }));
/*    */         }
/*    */         catch (NumberFormatException e) {
/* 30 */           throw new InvalidVerifyArgument(argument.getName());
/*    */         }
/*    */       }
/*    */     });
/* 36 */     addVerifier("max", new ArgumentVerifier()
/*    */     {
/*    */       public void verify(CommandSender sender, CommandArgument argument, String verifyName, String[] verifyArgs, String value, String valueRaw) throws VerifyError {
/* 39 */         if (verifyArgs.length != 1)
/* 40 */           throw new InvalidVerifyArgument(argument.getName());
/*    */         try
/*    */         {
/* 43 */           int max = Integer.parseInt(verifyArgs[0]);
/* 44 */           if (value.length() > max)
/* 45 */             throw new VerifyError(argument.getMessage("max_error", new String[] { verifyArgs[0] }));
/*    */         }
/*    */         catch (NumberFormatException e) {
/* 48 */           throw new InvalidVerifyArgument(argument.getName());
/*    */         }
/*    */       }
/*    */     });
/*    */   }
/*    */ 
/*    */   public String transform(CommandSender sender, CommandArgument argument, String value) throws TransformError
/*    */   {
/* 56 */     return value;
/*    */   }
/*    */ }

/* Location:           D:\Github\Mechanics\ItemAttributes.jar
 * Qualified Name:     se.ranzdo.bukkit.methodcommand.handlers.StringArgumentHandler
 * JD-Core Version:    0.6.2
 */