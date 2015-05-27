/*    */ package se.ranzdo.bukkit.methodcommand;
/*    */ 
/*    */ import org.bukkit.ChatColor;
/*    */ 
/*    */ public class CommandError extends Exception
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   private boolean showUsage;
/*    */ 
/*    */   public CommandError(String msg)
/*    */   {
/* 11 */     this(msg, false);
/*    */   }
/*    */ 
/*    */   public CommandError(String msg, boolean showUsage) {
/* 15 */     super(msg);
/* 16 */     this.showUsage = showUsage;
/*    */   }
/*    */ 
/*    */   public String getColorizedMessage() {
/* 20 */     String msg = getMessage();
/* 21 */     msg = msg.replaceAll("\\[", ChatColor.AQUA + "[");
/* 22 */     msg = msg.replaceAll("\\]", "]" + ChatColor.RED);
/* 23 */     return ChatColor.RED + msg;
/*    */   }
/*    */ 
/*    */   public boolean showUsage() {
/* 27 */     return this.showUsage;
/*    */   }
/*    */ }

/* Location:           D:\Github\Mechanics\ItemAttributes.jar
 * Qualified Name:     se.ranzdo.bukkit.methodcommand.CommandError
 * JD-Core Version:    0.6.2
 */