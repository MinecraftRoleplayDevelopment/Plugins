/*   */ package se.ranzdo.bukkit.methodcommand;
/*   */ 
/*   */ import java.lang.reflect.Method;
/*   */ 
/*   */ public class RegisterCommandMethodException extends RuntimeException
/*   */ {
/*   */   private static final long serialVersionUID = 1L;
/*   */ 
/*   */   public RegisterCommandMethodException(Method method, String msg)
/*   */   {
/* 9 */     super("Could not register the command method " + method.getName() + " in the class " + method.getDeclaringClass().getName() + ". Reason: " + msg);
/*   */   }
/*   */ }

/* Location:           D:\Github\Mechanics\ItemAttributes.jar
 * Qualified Name:     se.ranzdo.bukkit.methodcommand.RegisterCommandMethodException
 * JD-Core Version:    0.6.2
 */