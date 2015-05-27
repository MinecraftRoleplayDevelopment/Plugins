/*    */ package se.ranzdo.bukkit.methodcommand.handlers;
/*    */ 
/*    */ import org.bukkit.Bukkit;
/*    */ import org.bukkit.World;
/*    */ import org.bukkit.command.CommandSender;
/*    */ import org.bukkit.entity.Player;
/*    */ import se.ranzdo.bukkit.methodcommand.ArgumentHandler;
/*    */ import se.ranzdo.bukkit.methodcommand.ArgumentVariable;
/*    */ import se.ranzdo.bukkit.methodcommand.CommandArgument;
/*    */ import se.ranzdo.bukkit.methodcommand.CommandError;
/*    */ import se.ranzdo.bukkit.methodcommand.TransformError;
/*    */ 
/*    */ public class WorldArgumentHandler extends ArgumentHandler<World>
/*    */ {
/*    */   public WorldArgumentHandler()
/*    */   {
/* 17 */     setMessage("world_not_found", "The world \"%1\" was not found");
/*    */ 
/* 19 */     addVariable("sender", "The command executor", new ArgumentVariable()
/*    */     {
/*    */       public World var(CommandSender sender, CommandArgument argument, String varName) throws CommandError {
/* 22 */         if (!(sender instanceof Player)) {
/* 23 */           throw new CommandError(argument.getMessage("cant_as_console"));
/*    */         }
/* 25 */         return ((Player)sender).getWorld();
/*    */       }
/*    */     });
/*    */   }
/*    */ 
/*    */   public World transform(CommandSender sender, CommandArgument argument, String value) throws TransformError
/*    */   {
/* 32 */     World world = Bukkit.getWorld(value);
/* 33 */     if (world == null)
/* 34 */       throw new TransformError(argument.getMessage("world_not_found", new String[] { value }));
/* 35 */     return world;
/*    */   }
/*    */ }

/* Location:           D:\Github\Mechanics\ItemAttributes.jar
 * Qualified Name:     se.ranzdo.bukkit.methodcommand.handlers.WorldArgumentHandler
 * JD-Core Version:    0.6.2
 */