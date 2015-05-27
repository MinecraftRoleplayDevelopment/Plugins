/*   */ package se.ranzdo.bukkit.methodcommand;
/*   */ 
/*   */ public class InvalidVerifyArgument extends RuntimeException
/*   */ {
/*   */   private static final long serialVersionUID = 1L;
/*   */ 
/*   */   public InvalidVerifyArgument(String name)
/*   */   {
/* 7 */     super("The verifier " + name + " is not valid.");
/*   */   }
/*   */ }

/* Location:           D:\Github\Mechanics\ItemAttributes.jar
 * Qualified Name:     se.ranzdo.bukkit.methodcommand.InvalidVerifyArgument
 * JD-Core Version:    0.6.2
 */