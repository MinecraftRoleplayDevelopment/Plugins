/*    */ package se.ranzdo.bukkit.methodcommand.handlers;
/*    */ 
/*    */ import org.bukkit.command.CommandSender;
/*    */ import se.ranzdo.bukkit.methodcommand.ArgumentHandler;
/*    */ import se.ranzdo.bukkit.methodcommand.ArgumentVerifier;
/*    */ import se.ranzdo.bukkit.methodcommand.CommandArgument;
/*    */ import se.ranzdo.bukkit.methodcommand.InvalidVerifyArgument;
/*    */ import se.ranzdo.bukkit.methodcommand.VerifyError;
/*    */ 
/*    */ public abstract class NumberArgumentHandler<T extends Number> extends ArgumentHandler<T>
/*    */ {
/*    */   public NumberArgumentHandler()
/*    */   {
/* 15 */     setMessage("min_error", "The parameter [%p] must be equal or greater than %1");
/* 16 */     setMessage("max_error", "The parameter [%p] must be equal or less than %1");
/* 17 */     setMessage("range_error", "The parameter [%p] must be equal or greater than %1 and less than or equal to %2");
/*    */ 
/* 19 */     addVerifier("min", new ArgumentVerifier()
/*    */     {
/*    */       public void verify(CommandSender sender, CommandArgument argument, String verifyName, String[] verifyArgs, T value, String valueRaw) throws VerifyError {
/* 22 */         if (verifyArgs.length != 1)
/* 23 */           throw new InvalidVerifyArgument(argument.getName());
/*    */         try
/*    */         {
/* 26 */           double min = Double.parseDouble(verifyArgs[0]);
/* 27 */           if (value.doubleValue() < min)
/* 28 */             throw new VerifyError(argument.getMessage("min_error", new String[] { verifyArgs[0] }));
/*    */         }
/*    */         catch (NumberFormatException e) {
/* 31 */           throw new InvalidVerifyArgument(argument.getName());
/*    */         }
/*    */       }
/*    */     });
/* 36 */     addVerifier("max", new ArgumentVerifier()
/*    */     {
/*    */       public void verify(CommandSender sender, CommandArgument argument, String verifyName, String[] verifyArgs, T value, String valueRaw) throws VerifyError {
/* 39 */         if (verifyArgs.length != 1)
/* 40 */           throw new InvalidVerifyArgument(argument.getName());
/*    */         try
/*    */         {
/* 43 */           double max = Double.parseDouble(verifyArgs[0]);
/* 44 */           if (value.doubleValue() > max)
/* 45 */             throw new VerifyError(argument.getMessage("max_error", new String[] { verifyArgs[0] }));
/*    */         }
/*    */         catch (NumberFormatException e) {
/* 48 */           throw new InvalidVerifyArgument(argument.getName());
/*    */         }
/*    */       }
/*    */     });
/* 53 */     addVerifier("range", new ArgumentVerifier()
/*    */     {
/*    */       public void verify(CommandSender sender, CommandArgument argument, String verifyName, String[] verifyArgs, T value, String valueRaw) throws VerifyError {
/* 56 */         if (verifyArgs.length != 2)
/* 57 */           throw new InvalidVerifyArgument(argument.getName());
/*    */         try
/*    */         {
/* 60 */           double min = Double.parseDouble(verifyArgs[0]);
/* 61 */           double max = Double.parseDouble(verifyArgs[1]);
/* 62 */           double dvalue = value.doubleValue();
/* 63 */           if ((dvalue < min) || (dvalue > max))
/* 64 */             throw new VerifyError(argument.getMessage("range_error", new String[] { verifyArgs[0], verifyArgs[1] }));
/*    */         }
/*    */         catch (NumberFormatException e) {
/* 67 */           throw new InvalidVerifyArgument(argument.getName());
/*    */         }
/*    */       }
/*    */     });
/*    */   }
/*    */ }

/* Location:           D:\Github\Mechanics\ItemAttributes.jar
 * Qualified Name:     se.ranzdo.bukkit.methodcommand.handlers.NumberArgumentHandler
 * JD-Core Version:    0.6.2
 */