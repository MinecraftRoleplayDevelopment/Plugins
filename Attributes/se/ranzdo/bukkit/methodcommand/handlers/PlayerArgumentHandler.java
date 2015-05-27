/*    */ package se.ranzdo.bukkit.methodcommand.handlers;
/*    */ 
/*    */ import org.bukkit.Bukkit;
/*    */ import org.bukkit.command.CommandSender;
/*    */ import org.bukkit.entity.Player;
/*    */ import se.ranzdo.bukkit.methodcommand.ArgumentHandler;
/*    */ import se.ranzdo.bukkit.methodcommand.ArgumentVariable;
/*    */ import se.ranzdo.bukkit.methodcommand.CommandArgument;
/*    */ import se.ranzdo.bukkit.methodcommand.CommandError;
/*    */ import se.ranzdo.bukkit.methodcommand.TransformError;
/*    */ 
/*    */ public class PlayerArgumentHandler extends ArgumentHandler<Player>
/*    */ {
/*    */   public PlayerArgumentHandler()
/*    */   {
/* 16 */     setMessage("player_not_online", "The player %1 is not online");
/*    */ 
/* 18 */     addVariable("sender", "The command executor", new ArgumentVariable()
/*    */     {
/*    */       public Player var(CommandSender sender, CommandArgument argument, String varName) throws CommandError {
/* 21 */         if (!(sender instanceof Player)) {
/* 22 */           throw new CommandError(argument.getMessage("cant_as_console"));
/*    */         }
/* 24 */         return (Player)sender;
/*    */       }
/*    */     });
/*    */   }
/*    */ 
/*    */   public Player transform(CommandSender sender, CommandArgument argument, String value) throws TransformError
/*    */   {
/* 31 */     Player p = Bukkit.getPlayer(value);
/* 32 */     if (p == null) {
/* 33 */       throw new TransformError(argument.getMessage("player_not_online", new String[] { value }));
/*    */     }
/* 35 */     return p;
/*    */   }
/*    */ }

/* Location:           D:\Github\Mechanics\ItemAttributes.jar
 * Qualified Name:     se.ranzdo.bukkit.methodcommand.handlers.PlayerArgumentHandler
 * JD-Core Version:    0.6.2
 */