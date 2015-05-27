/*     */ package se.ranzdo.bukkit.methodcommand;
/*     */ 
/*     */ import java.lang.annotation.Annotation;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.lang.reflect.Method;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.LinkedHashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import org.bukkit.ChatColor;
/*     */ import org.bukkit.command.CommandSender;
/*     */ 
/*     */ public class RegisteredCommand
/*     */ {
/*     */   private String label;
/*     */   private RegisteredCommand parent;
/*     */   private String description;
/*     */   private String[] permissions;
/*     */   private boolean onlyPlayers;
/*     */   private Method method;
/*     */   private Object methodInstance;
/*     */   private CommandHandler handler;
/*  25 */   private boolean set = false;
/*     */ 
/*  27 */   private ArrayList<ExecutableArgument> methodArguments = new ArrayList();
/*  28 */   private ArrayList<CommandArgument> arguments = new ArrayList();
/*  29 */   private ArrayList<RegisteredCommand> suffixes = new ArrayList();
/*  30 */   private ArrayList<Flag> flags = new ArrayList();
/*     */   private WildcardArgument wildcard;
/*  32 */   private Map<String, Flag> flagsByName = new LinkedHashMap();
/*  33 */   private Map<String, RegisteredCommand> suffixesByName = new HashMap();
/*     */ 
/*     */   RegisteredCommand(String label, CommandHandler handler, RegisteredCommand parent) {
/*  36 */     this.label = label;
/*  37 */     this.handler = handler;
/*  38 */     this.parent = parent;
/*     */   }
/*     */ 
/*     */   void addSuffixCommand(String suffix, RegisteredCommand command) {
/*  42 */     this.suffixesByName.put(suffix.toLowerCase(), command);
/*  43 */     this.suffixes.add(command);
/*     */   }
/*     */ 
/*     */   boolean doesSuffixCommandExist(String suffix) {
/*  47 */     return this.suffixesByName.get(suffix) != null;
/*     */   }
/*     */ 
/*     */   void execute(CommandSender sender, String[] args) {
/*  51 */     if (!testPermission(sender)) {
/*  52 */       sender.sendMessage(ChatColor.RED + "You do not have permission to do this!");
/*  53 */       return;
/*     */     }
/*     */ 
/*  56 */     if (args.length > 0) {
/*  57 */       String suffixLabel = args[0].toLowerCase();
/*  58 */       if (suffixLabel.equals(this.handler.getHelpSuffix())) {
/*  59 */         sendHelpMessage(sender);
/*  60 */         return;
/*     */       }
/*     */ 
/*  63 */       RegisteredCommand command = (RegisteredCommand)this.suffixesByName.get(suffixLabel);
/*  64 */       if (command == null) {
/*  65 */         executeMethod(sender, args);
/*     */       }
/*     */       else {
/*  68 */         String[] nargs = new String[args.length - 1];
/*  69 */         System.arraycopy(args, 1, nargs, 0, args.length - 1);
/*  70 */         command.execute(sender, nargs);
/*     */       }
/*     */     }
/*     */     else {
/*  74 */       executeMethod(sender, args);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void executeMethod(CommandSender sender, String[] args) {
/*  79 */     if (!this.set) {
/*  80 */       sendHelpMessage(sender);
/*  81 */       return;
/*  84 */     }
/*     */ ArrayList resultArgs = new ArrayList();
/*  85 */     resultArgs.add(sender);
/*     */     Arguments arguments;
/*     */     try {
/*  89 */       arguments = new Arguments(args, this.flagsByName);
/*     */     } catch (CommandError e) {
/*  91 */       sender.sendMessage(e.getColorizedMessage());
/*  92 */       return;
/*     */     }
/*     */ 
/*  95 */     for (ExecutableArgument ea : this.methodArguments)
/*     */       try {
/*  97 */         resultArgs.add(ea.execute(sender, arguments));
/*     */       } catch (CommandError e) {
/*  99 */         sender.sendMessage(e.getColorizedMessage());
/* 100 */         if (e.showUsage())
/* 101 */           sender.sendMessage(getUsage());
/* 102 */         return;
/*     */       }
/*     */     try
/*     */     {
/*     */       try
/*     */       {
/* 108 */         this.method.invoke(this.methodInstance, resultArgs.toArray());
/*     */       }
/*     */       catch (InvocationTargetException e) {
/* 111 */         if ((e.getCause() instanceof CommandError)) {
/* 112 */           CommandError ce = (CommandError)e.getCause();
/* 113 */           sender.sendMessage(ce.getColorizedMessage());
/* 114 */           if (ce.showUsage())
/* 115 */             sender.sendMessage(getUsage());
/*     */         }
/*     */         else {
/* 118 */           throw e;
/*     */         }
/*     */       }
/*     */     } catch (Exception e) { sender.sendMessage(ChatColor.RED + "An internal error occurred while attempting to perform this command.");
/* 122 */       e.printStackTrace(); }
/*     */   }
/*     */ 
/*     */   private ArgumentHandler<?> getArgumenHandler(Class<?> argumentClass)
/*     */   {
/* 127 */     ArgumentHandler argumentHandler = this.handler.getArgumentHandler(argumentClass);
/*     */ 
/* 129 */     if (argumentHandler == null) {
/* 130 */       throw new RegisterCommandMethodException(this.method, "Could not find a ArgumentHandler for (" + argumentClass.getName() + ")");
/*     */     }
/* 132 */     return argumentHandler;
/*     */   }
/*     */ 
/*     */   public List<CommandArgument> getArguments() {
/* 136 */     return this.arguments;
/*     */   }
/*     */ 
/*     */   public String getDescription() {
/* 140 */     return this.description;
/*     */   }
/*     */ 
/*     */   public List<Flag> getFlags() {
/* 144 */     return this.flags;
/*     */   }
/*     */ 
/*     */   public String[] getHelpMessage() {
/* 148 */     return this.handler.getHelpHandler().getHelpMessage(this);
/*     */   }
/*     */ 
/*     */   public String getLabel() {
/* 152 */     return this.label;
/*     */   }
/*     */ 
/*     */   public RegisteredCommand getParent() {
/* 156 */     return this.parent;
/*     */   }
/*     */ 
/*     */   public String[] getPermissions() {
/* 160 */     return this.permissions;
/*     */   }
/*     */ 
/*     */   public RegisteredCommand getSuffixCommand(String suffix) {
/* 164 */     return (RegisteredCommand)this.suffixesByName.get(suffix);
/*     */   }
/*     */ 
/*     */   public List<RegisteredCommand> getSuffixes() {
/* 168 */     return this.suffixes;
/*     */   }
/*     */ 
/*     */   public String getUsage() {
/* 172 */     return this.handler.getHelpHandler().getUsage(this);
/*     */   }
/*     */ 
/*     */   public WildcardArgument getWildcard() {
/* 176 */     return this.wildcard;
/*     */   }
/*     */ 
/*     */   public boolean isOnlyPlayers() {
/* 180 */     return this.onlyPlayers;
/*     */   }
/*     */ 
/*     */   public boolean isSet() {
/* 184 */     return this.set;
/*     */   }
/*     */ 
/*     */   public boolean onlyPlayers() {
/* 188 */     return this.onlyPlayers;
/*     */   }
/*     */ 
/*     */   public void sendHelpMessage(CommandSender sender) {
/* 192 */     sender.sendMessage(getHelpMessage());
/*     */   }
/*     */ 
/*     */   void set(Object methodInstance, Method method) {
/* 196 */     this.methodInstance = methodInstance;
/* 197 */     this.method = method;
/* 198 */     method.setAccessible(true);
/* 199 */     Command command = (Command)method.getAnnotation(Command.class);
/* 200 */     Flags flagsAnnotation = (Flags)method.getAnnotation(Flags.class);
/* 201 */     this.description = command.description();
/* 202 */     this.permissions = command.permissions();
/* 203 */     this.onlyPlayers = command.onlyPlayers();
/*     */ 
/* 205 */     Class[] methodParameters = method.getParameterTypes();
/*     */ 
/* 207 */     if ((methodParameters.length == 0) || (!CommandSender.class.isAssignableFrom(methodParameters[0]))) {
/* 208 */       throw new RegisterCommandMethodException(method, "The first parameter in the command method must be assignable to the CommandSender interface.");
/*     */     }
/* 210 */     if (flagsAnnotation != null) {
/* 211 */       String[] flags = flagsAnnotation.identifier();
/* 212 */       String[] flagdescriptions = flagsAnnotation.description();
/*     */ 
/* 214 */       for (int i = 0; i < flags.length; i++) {
/* 215 */         Flag flag = new Flag(flags[i], i < flagdescriptions.length ? flagdescriptions[i] : "");
/* 216 */         this.flagsByName.put(flags[i], flag);
/* 217 */         this.flags.add(flag);
/*     */       }
/*     */     }
/*     */ 
/* 221 */     Annotation[][] parameterAnnotations = method.getParameterAnnotations();
/*     */ 
/* 223 */     for (int i = 1; i < methodParameters.length; i++)
/*     */     {
/* 226 */       Arg commandArgAnnotation = null;
/* 227 */       for (Annotation annotation : parameterAnnotations[i]) {
/* 228 */         if (annotation.annotationType() == Arg.class) {
/* 229 */           commandArgAnnotation = (Arg)annotation;
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 234 */       FlagArg flagArgAnnotation = null;
/* 235 */       for (Annotation annotation : parameterAnnotations[i]) {
/* 236 */         if (annotation.annotationType() == FlagArg.class) {
/* 237 */           flagArgAnnotation = (FlagArg)annotation;
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 242 */       if ((commandArgAnnotation == null) && (flagArgAnnotation == null)) {
/* 243 */         throw new RegisterCommandMethodException(method, "The command annonation is present on a method, however one of the parameters is not annotated.");
/*     */       }
/* 245 */       Flag flag = null;
/*     */ 
/* 247 */       if (flagArgAnnotation != null) {
/* 248 */         flag = (Flag)this.flagsByName.get(flagArgAnnotation.value());
/* 249 */         if (flag == null) {
/* 250 */           throw new RegisterCommandMethodException(method, "The flag annonation is present on a parameter, however the flag is not defined in the flags annonation.");
/*     */         }
/*     */       }
/* 253 */       Class argumentClass = methodParameters[i];
/*     */ 
/* 255 */       if (commandArgAnnotation == null) {
/* 256 */         if ((argumentClass != Boolean.TYPE) && (argumentClass != Boolean.class)) {
/* 257 */           throw new RegisterCommandMethodException(method, "The flag annonation is present on a parameter without the arg annonation, however the parameter type is not an boolean.");
/*     */         }
/* 259 */         this.methodArguments.add(flag);
/*     */       }
/* 265 */       else if (flagArgAnnotation == null)
/*     */       {
/*     */         CommandArgument argument;
/* 267 */         if (i == methodParameters.length - 1)
/*     */         {
/* 269 */           Wildcard wildcard = null;
/* 270 */           for (Annotation annotation : parameterAnnotations[i])
/* 271 */             if (annotation.annotationType() == Wildcard.class)
/* 272 */               wildcard = (Wildcard)annotation;
/*     */           CommandArgument argument;
/* 276 */           if (wildcard != null) {
/* 277 */             boolean join = wildcard.join();
/* 278 */             if (!join) {
/* 279 */               argumentClass = argumentClass.getComponentType();
/* 280 */               if (argumentClass == null)
/* 281 */                 throw new RegisterCommandMethodException(method, "The wildcard argument needs to be an array if join is false.");
/*     */             }
/* 283 */             this.wildcard = new WildcardArgument(commandArgAnnotation, argumentClass, getArgumenHandler(argumentClass), join);
/* 284 */             argument = this.wildcard;
/*     */           }
/*     */           else
/*     */           {
/* 288 */             CommandArgument argument = new CommandArgument(commandArgAnnotation, argumentClass, getArgumenHandler(argumentClass));
/* 289 */             this.arguments.add(argument);
/*     */           }
/*     */         }
/*     */         else {
/* 293 */           argument = new CommandArgument(commandArgAnnotation, argumentClass, getArgumenHandler(argumentClass));
/* 294 */           this.arguments.add(argument);
/*     */         }
/*     */ 
/* 297 */         this.methodArguments.add(argument);
/*     */       }
/*     */       else {
/* 300 */         FlagArgument argument = new FlagArgument(commandArgAnnotation, argumentClass, getArgumenHandler(argumentClass), flag);
/* 301 */         this.methodArguments.add(argument);
/* 302 */         flag.addArgument(argument);
/*     */       }
/*     */     }
/*     */ 
/* 306 */     this.set = true;
/*     */   }
/*     */ 
/*     */   public boolean testPermission(CommandSender sender) {
/* 310 */     if (!this.set) {
/* 311 */       return true;
/*     */     }
/* 313 */     return this.handler.getPermissionHandler().hasPermission(sender, this.permissions);
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\ItemAttributes.jar
 * Qualified Name:     se.ranzdo.bukkit.methodcommand.RegisteredCommand
 * JD-Core Version:    0.6.2
 */