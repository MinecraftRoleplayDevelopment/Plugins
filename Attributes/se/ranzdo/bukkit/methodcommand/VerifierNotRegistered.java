/*   */ package se.ranzdo.bukkit.methodcommand;
/*   */ 
/*   */ public class VerifierNotRegistered extends RuntimeException
/*   */ {
/*   */   private static final long serialVersionUID = 1L;
/*   */ 
/*   */   public VerifierNotRegistered(String verifierName)
/*   */   {
/* 7 */     super("The verify method named " + verifierName + " is not registered");
/*   */   }
/*   */ }

/* Location:           D:\Github\Mechanics\ItemAttributes.jar
 * Qualified Name:     se.ranzdo.bukkit.methodcommand.VerifierNotRegistered
 * JD-Core Version:    0.6.2
 */