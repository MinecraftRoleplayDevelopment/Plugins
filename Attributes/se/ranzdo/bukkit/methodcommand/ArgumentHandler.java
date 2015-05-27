/*     */ package se.ranzdo.bukkit.methodcommand;
/*     */ 
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import org.bukkit.command.CommandSender;
/*     */ 
/*     */ public abstract class ArgumentHandler<T>
/*     */ {
/*     */   CommandHandler handler;
/*  21 */   private Map<String, ArgumentVerifier<T>> verifiers = new HashMap();
/*  22 */   private Map<String, ArgumentHandler<T>.Variable> vars = new HashMap();
/*  23 */   private Map<String, String> messageNodes = new HashMap();
/*     */ 
/*     */   public ArgumentHandler()
/*     */   {
/*  27 */     setMessage("include_error", "[%p] has an invalid value.");
/*  28 */     setMessage("exclude_error", "[%p] has an invalid value.");
/*  29 */     setMessage("cant_as_console", "You can't do this as console.");
/*     */ 
/*  32 */     addVerifier("include", new ArgumentVerifier()
/*     */     {
/*     */       public void verify(CommandSender sender, CommandArgument argument, String verifyName, String[] verifyArgs, T value, String valueRaw) throws VerifyError {
/*  35 */         for (String include : verifyArgs)
/*     */           try {
/*  37 */             if (ArgumentHandler.this.transform(sender, argument, include) != value)
/*  38 */               throw new VerifyError(argument.getMessage("include_error", new String[] { valueRaw }));
/*     */           } catch (TransformError e) {
/*  40 */             throw ((IllegalArgumentException)new IllegalArgumentException("Could not transform the verify argument " + include).initCause(e));
/*     */           }
/*     */       }
/*     */     });
/*  46 */     addVerifier("exclude", new ArgumentVerifier()
/*     */     {
/*     */       public void verify(CommandSender sender, CommandArgument argument, String verifyName, String[] verifyArgs, T value, String valueRaw) throws VerifyError {
/*  49 */         for (String exclude : verifyArgs)
/*     */           try {
/*  51 */             if (ArgumentHandler.this.transform(sender, argument, exclude) == value)
/*  52 */               throw new VerifyError(argument.getMessage("exclude_error", new String[] { valueRaw }));
/*     */           } catch (TransformError e) {
/*  54 */             throw ((IllegalArgumentException)new IllegalArgumentException("Could not transform the verify argument " + exclude).initCause(e));
/*     */           }
/*     */       }
/*     */     });
/*     */   }
/*     */ 
/*     */   public final void addVariable(String varName, String userFriendlyName, ArgumentVariable<T> var)
/*     */   {
/*  62 */     if (verifierExists(varName)) {
/*  63 */       throw new IllegalArgumentException(new StringBuilder().append("A variable with the name ").append(varName).append(" does already exist.").toString());
/*     */     }
/*  65 */     this.vars.put(varName, new Variable(userFriendlyName, var));
/*     */   }
/*     */ 
/*     */   public final void addVerifier(String name, ArgumentVerifier<T> verify) {
/*  69 */     if (verifierExists(name)) {
/*  70 */       throw new IllegalArgumentException(new StringBuilder().append("A verifier with the name ").append(name).append(" does already exist.").toString());
/*     */     }
/*  72 */     this.verifiers.put(name, verify);
/*     */   }
/*     */ 
/*     */   public final CommandHandler getCommandHandler() {
/*  76 */     return this.handler;
/*     */   }
/*     */ 
/*     */   public final String getMessage(String node) {
/*  80 */     return (String)this.messageNodes.get(node);
/*     */   }
/*     */ 
/*     */   public final ArgumentVariable<T> getVariable(String varName) {
/*  84 */     return this.vars.get(varName) == null ? null : ((Variable)this.vars.get(varName)).variable;
/*     */   }
/*     */ 
/*     */   public final String getVariableUserFriendlyName(String varName) {
/*  88 */     return this.vars.get(varName) == null ? null : ((Variable)this.vars.get(varName)).userFriendlyName;
/*     */   }
/*     */ 
/*     */   public final ArgumentVerifier<T> getVerifier(String argName) {
/*  92 */     return (ArgumentVerifier)this.verifiers.get(argName);
/*     */   }
/*     */ 
/*     */   final T handle(CommandSender sender, CommandArgument argument, String arg) throws CommandError {
/*  96 */     if (arg == null)
/*  97 */       return null;
/*     */     Object transformed;
/*     */     Object transformed;
/* 101 */     if (arg.startsWith("?")) {
/* 102 */       String varName = arg.substring(1, arg.length());
/* 103 */       ArgumentVariable var = getVariable(varName);
/* 104 */       if (var == null) {
/* 105 */         throw new IllegalArgumentException(new StringBuilder().append("The ArgumentVariable '").append(varName).append("' is not registered.").toString());
/*     */       }
/* 107 */       transformed = var.var(sender, argument, varName);
/*     */     }
/*     */     else
/*     */     {
/*     */       Object transformed;
/* 109 */       if (arg.matches("^\\\\+\\?.*$")) {
/* 110 */         arg = arg.substring(1, arg.length());
/* 111 */         transformed = transform(sender, argument, arg);
/*     */       }
/*     */       else {
/* 114 */         transformed = transform(sender, argument, arg);
/*     */       }
/*     */     }
/* 116 */     for (Map.Entry verifier : argument.getVerifyArguments().entrySet()) {
/* 117 */       ArgumentVerifier v = (ArgumentVerifier)this.verifiers.get(verifier.getKey());
/* 118 */       if (v == null) {
/* 119 */         throw new VerifierNotRegistered((String)verifier.getKey());
/*     */       }
/* 121 */       v.verify(sender, argument, (String)verifier.getKey(), (String[])verifier.getValue(), transformed, arg);
/*     */     }
/*     */ 
/* 124 */     return transformed;
/*     */   }
/*     */ 
/*     */   public final void setMessage(String node, String def) {
/* 128 */     this.messageNodes.put(node, def);
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 133 */     StringBuilder sb = new StringBuilder();
/* 134 */     sb.append(new StringBuilder().append("ArgumentHandler -> ").append(getClass().getName()).append("\n").toString());
/* 135 */     sb.append("Set messages: \n");
/* 136 */     for (Map.Entry entry : this.messageNodes.entrySet()) {
/* 137 */       sb.append(new StringBuilder().append((String)entry.getKey()).append(" = \"").append((String)entry.getValue()).append("\";\n").toString());
/*     */     }
/* 139 */     sb.append("\nAvailable verifiers: \n");
/* 140 */     for (Map.Entry entry : this.verifiers.entrySet()) {
/* 141 */       sb.append(new StringBuilder().append((String)entry.getKey()).append(" = \"").append(((ArgumentVerifier)entry.getValue()).getClass().getName()).append("\";\n").toString());
/*     */     }
/* 143 */     sb.append("\nAvailable variables: \n");
/* 144 */     for (Map.Entry entry : this.vars.entrySet()) {
/* 145 */       sb.append(new StringBuilder().append((String)entry.getKey()).append(" = \"").append(((Variable)entry.getValue()).userFriendlyName).append("\";\n").toString());
/*     */     }
/*     */ 
/* 148 */     return sb.toString();
/*     */   }
/*     */ 
/*     */   public abstract T transform(CommandSender paramCommandSender, CommandArgument paramCommandArgument, String paramString) throws TransformError;
/*     */ 
/*     */   public final boolean variableExists(String varName) {
/* 154 */     return this.vars.get(varName) != null;
/*     */   }
/*     */ 
/*     */   public final boolean verifierExists(String argName) {
/* 158 */     return this.verifiers.get(argName) != null;
/*     */   }
/*     */ 
/*     */   private final class Variable
/*     */   {
/*     */     String userFriendlyName;
/*     */     ArgumentVariable<T> variable;
/*     */ 
/*     */     Variable(ArgumentVariable<T> userFriendlyName)
/*     */     {
/*  14 */       this.userFriendlyName = userFriendlyName;
/*  15 */       this.variable = variable;
/*     */     }
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\ItemAttributes.jar
 * Qualified Name:     se.ranzdo.bukkit.methodcommand.ArgumentHandler
 * JD-Core Version:    0.6.2
 */