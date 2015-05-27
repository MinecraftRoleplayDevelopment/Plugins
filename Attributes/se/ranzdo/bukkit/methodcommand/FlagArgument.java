/*    */ package se.ranzdo.bukkit.methodcommand;
/*    */ 
/*    */ import org.bukkit.command.CommandSender;
/*    */ 
/*    */ public class FlagArgument extends CommandArgument
/*    */ {
/*    */   private final Flag flag;
/*    */ 
/*    */   public FlagArgument(Arg commandArgAnnotation, Class<?> argumentClass, ArgumentHandler<?> argumentHandler, Flag flag)
/*    */   {
/* 10 */     super(commandArgAnnotation, argumentClass, argumentHandler);
/* 11 */     this.flag = flag;
/*    */   }
/*    */ 
/*    */   public FlagArgument(String name, String description, String def, String verifiers, Class<?> argumentClass, ArgumentHandler<?> handler, Flag flag) {
/* 15 */     super(name, description, def, verifiers, argumentClass, handler);
/* 16 */     this.flag = flag;
/*    */   }
/*    */ 
/*    */   public Object execute(CommandSender sender, Arguments args)
/*    */     throws CommandError
/*    */   {
/*    */     String arg;
/*    */     String arg;
/* 22 */     if (!args.flagExists(this.flag)) {
/* 23 */       arg = getDefault(); } else {
/* 24 */       if (!args.hasNext(this.flag)) {
/* 25 */         throw new CommandError("The argument s [" + getName() + "] to the flag -" + this.flag.getIdentifier() + " is not defined");
/*    */       }
/* 27 */       arg = CommandUtil.escapeArgumentVariable(args.nextFlagArgument(this.flag));
/*    */     }
/* 29 */     return getHandler().handle(sender, this, arg);
/*    */   }
/*    */ 
/*    */   public Flag getFlag() {
/* 33 */     return this.flag;
/*    */   }
/*    */ }

/* Location:           D:\Github\Mechanics\ItemAttributes.jar
 * Qualified Name:     se.ranzdo.bukkit.methodcommand.FlagArgument
 * JD-Core Version:    0.6.2
 */