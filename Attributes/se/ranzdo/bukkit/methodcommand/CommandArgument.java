/*    */ package se.ranzdo.bukkit.methodcommand;
/*    */ 
/*    */ import java.util.HashMap;
/*    */ import java.util.Map;
/*    */ import org.bukkit.command.CommandSender;
/*    */ 
/*    */ public class CommandArgument
/*    */   implements ExecutableArgument
/*    */ {
/*    */   private final String name;
/*    */   private final String description;
/*    */   private final String def;
/*    */   private final Map<String, String[]> verifyArguments;
/*    */   private final ArgumentHandler<?> handler;
/*    */   private final Class<?> argumentClass;
/* 17 */   private Map<String, String> overrideMessages = new HashMap();
/*    */ 
/*    */   public CommandArgument(Arg commandArgAnnotation, Class<?> argumentClass, ArgumentHandler<?> argumentHandler) {
/* 20 */     this(commandArgAnnotation.name(), commandArgAnnotation.description(), commandArgAnnotation.def(), commandArgAnnotation.verifiers(), argumentClass, argumentHandler);
/*    */   }
/*    */ 
/*    */   public CommandArgument(String name, String description, String def, String verifiers, Class<?> argumentClass, ArgumentHandler<?> handler) {
/* 24 */     this.name = name;
/* 25 */     this.description = description;
/* 26 */     this.def = def;
/* 27 */     this.verifyArguments = CommandUtil.parseVerifiers(verifiers);
/* 28 */     this.handler = handler;
/* 29 */     this.argumentClass = argumentClass;
/*    */   }
/*    */ 
/*    */   public Object execute(CommandSender sender, Arguments args)
/*    */     throws CommandError
/*    */   {
/*    */     String arg;
/*    */     String arg;
/* 35 */     if (!args.hasNext()) {
/* 36 */       if (this.def.equals(" ")) {
/* 37 */         throw new CommandError("The argument [" + this.name + "] is not defined (it has no default value)", true);
/*    */       }
/* 39 */       arg = this.def;
/*    */     }
/*    */     else {
/* 42 */       arg = CommandUtil.escapeArgumentVariable(args.nextArgument());
/*    */     }
/* 44 */     return this.handler.handle(sender, this, arg);
/*    */   }
/*    */ 
/*    */   private String formatMessage(String msg, String[] vars) {
/* 48 */     msg = msg.replace("%p", this.name);
/*    */ 
/* 50 */     for (int i = 1; i <= vars.length; i++) {
/* 51 */       msg = msg.replace("%" + i, vars[(i - 1)]);
/*    */     }
/*    */ 
/* 54 */     return msg.replaceAll("%\\d+", "<variable not available>");
/*    */   }
/*    */ 
/*    */   public Class<?> getArgumentClass() {
/* 58 */     return this.argumentClass;
/*    */   }
/*    */ 
/*    */   public String getDefault() {
/* 62 */     return this.def;
/*    */   }
/*    */ 
/*    */   public String getDescription() {
/* 66 */     return this.description;
/*    */   }
/*    */ 
/*    */   public ArgumentHandler<?> getHandler() {
/* 70 */     return this.handler;
/*    */   }
/*    */ 
/*    */   public String getMessage(String node) {
/* 74 */     return getMessage(node, new String[0]);
/*    */   }
/*    */ 
/*    */   public String getMessage(String node, String[] vars) {
/* 78 */     String msg = (String)this.overrideMessages.get(node);
/*    */ 
/* 80 */     if (msg != null) {
/* 81 */       return formatMessage(msg, vars);
/*    */     }
/* 83 */     msg = this.handler.getMessage(node);
/*    */ 
/* 85 */     if (msg != null) {
/* 86 */       return formatMessage(msg, vars);
/*    */     }
/* 88 */     throw new IllegalArgumentException("The node \"" + node + "\" is not available.");
/*    */   }
/*    */ 
/*    */   public String getName() {
/* 92 */     return this.name;
/*    */   }
/*    */ 
/*    */   public Map<String, String[]> getVerifyArguments() {
/* 96 */     return this.verifyArguments;
/*    */   }
/*    */ }

/* Location:           D:\Github\Mechanics\ItemAttributes.jar
 * Qualified Name:     se.ranzdo.bukkit.methodcommand.CommandArgument
 * JD-Core Version:    0.6.2
 */