/*    */ package se.ranzdo.bukkit.methodcommand;
/*    */ 
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;
/*    */ import org.bukkit.command.CommandSender;
/*    */ 
/*    */ public class Flag
/*    */   implements ExecutableArgument
/*    */ {
/*    */   private final String identifier;
/*    */   private final String description;
/* 11 */   private List<FlagArgument> arguments = new ArrayList();
/*    */ 
/*    */   public Flag(String identifier, String description) {
/* 14 */     this.identifier = identifier;
/* 15 */     this.description = description;
/*    */   }
/*    */ 
/*    */   public void addArgument(FlagArgument argument) {
/* 19 */     this.arguments.add(argument);
/*    */   }
/*    */ 
/*    */   public Object execute(CommandSender sender, Arguments args)
/*    */   {
/* 24 */     return Boolean.valueOf(args.flagExists(this));
/*    */   }
/*    */ 
/*    */   public List<FlagArgument> getArguments() {
/* 28 */     return this.arguments;
/*    */   }
/*    */ 
/*    */   public String getDescription() {
/* 32 */     return this.description;
/*    */   }
/*    */ 
/*    */   public String getIdentifier() {
/* 36 */     return this.identifier;
/*    */   }
/*    */ 
/*    */   public int hashCode()
/*    */   {
/* 41 */     return this.identifier.hashCode();
/*    */   }
/*    */ }

/* Location:           D:\Github\Mechanics\ItemAttributes.jar
 * Qualified Name:     se.ranzdo.bukkit.methodcommand.Flag
 * JD-Core Version:    0.6.2
 */