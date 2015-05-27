/*    */ package se.ranzdo.bukkit.methodcommand;
/*    */ 
/*    */ import java.lang.reflect.Array;
/*    */ import org.bukkit.command.CommandSender;
/*    */ 
/*    */ public class WildcardArgument extends CommandArgument
/*    */ {
/*    */   private boolean join;
/*    */ 
/*    */   public WildcardArgument(Arg commandArgAnnotation, Class<?> argumentClass, ArgumentHandler<?> argumentHandler, boolean join)
/*    */   {
/* 12 */     super(commandArgAnnotation, argumentClass, argumentHandler);
/* 13 */     this.join = join;
/*    */   }
/*    */ 
/*    */   public WildcardArgument(String name, String description, String def, String verifiers, Class<?> argumentClass, ArgumentHandler<?> handler, boolean join) {
/* 17 */     super(name, description, def, verifiers, argumentClass, handler);
/* 18 */     this.join = join;
/*    */   }
/*    */ 
/*    */   public Object execute(CommandSender sender, Arguments args) throws CommandError
/*    */   {
/* 23 */     if (!args.hasNext()) {
/* 24 */       Object o = getHandler().handle(sender, this, getDefault().equals(" ") ? "" : getDefault());
/* 25 */       if (this.join) {
/* 26 */         return o;
/*    */       }
/* 28 */       Object array = Array.newInstance(getArgumentClass(), 1);
/* 29 */       Array.set(array, 0, o);
/* 30 */       return array;
/*    */     }
/*    */ 
/* 34 */     if (this.join) {
/* 35 */       StringBuilder sb = new StringBuilder();
/*    */ 
/* 37 */       while (args.hasNext()) {
/* 38 */         sb.append(args.nextArgument()).append(" ");
/*    */       }
/*    */ 
/* 41 */       return getHandler().handle(sender, this, CommandUtil.escapeArgumentVariable(sb.toString().trim()));
/*    */     }
/*    */ 
/* 44 */     Object array = Array.newInstance(getArgumentClass(), args.over());
/*    */ 
/* 46 */     for (int i = 0; i < args.over(); i++) {
/* 47 */       Array.set(array, i, getHandler().handle(sender, this, CommandUtil.escapeArgumentVariable(args.nextArgument())));
/*    */     }
/* 49 */     return array;
/*    */   }
/*    */ 
/*    */   public boolean willJoin()
/*    */   {
/* 54 */     return this.join;
/*    */   }
/*    */ }

/* Location:           D:\Github\Mechanics\ItemAttributes.jar
 * Qualified Name:     se.ranzdo.bukkit.methodcommand.WildcardArgument
 * JD-Core Version:    0.6.2
 */