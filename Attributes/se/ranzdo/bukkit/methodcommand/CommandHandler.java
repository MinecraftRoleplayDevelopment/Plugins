/*     */ package se.ranzdo.bukkit.methodcommand;
/*     */ 
/*     */ import java.lang.reflect.Method;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import org.bukkit.ChatColor;
/*     */ import org.bukkit.Material;
/*     */ import org.bukkit.World;
/*     */ import org.bukkit.command.CommandExecutor;
/*     */ import org.bukkit.command.CommandSender;
/*     */ import org.bukkit.command.PluginCommand;
/*     */ import org.bukkit.entity.EntityType;
/*     */ import org.bukkit.entity.Player;
/*     */ import org.bukkit.plugin.java.JavaPlugin;
/*     */ import se.ranzdo.bukkit.methodcommand.handlers.DoubleArgumentHandler;
/*     */ import se.ranzdo.bukkit.methodcommand.handlers.EntityTypeArgumentHandler;
/*     */ import se.ranzdo.bukkit.methodcommand.handlers.IntegerArgumentHandler;
/*     */ import se.ranzdo.bukkit.methodcommand.handlers.MaterialArgumentHandler;
/*     */ import se.ranzdo.bukkit.methodcommand.handlers.PlayerArgumentHandler;
/*     */ import se.ranzdo.bukkit.methodcommand.handlers.StringArgumentHandler;
/*     */ import se.ranzdo.bukkit.methodcommand.handlers.WorldArgumentHandler;
/*     */ 
/*     */ public class CommandHandler
/*     */   implements CommandExecutor
/*     */ {
/*     */   private JavaPlugin plugin;
/*  25 */   private Map<Class<?>, ArgumentHandler<?>> argumentHandlers = new HashMap();
/*  26 */   private Map<org.bukkit.command.Command, RootCommand> rootCommands = new HashMap();
/*     */ 
/*  28 */   private PermissionHandler permissionHandler = new PermissionHandler()
/*     */   {
/*     */     public boolean hasPermission(CommandSender sender, String[] permissions) {
/*  31 */       for (String perm : permissions) {
/*  32 */         if (!sender.hasPermission(perm))
/*  33 */           return false;
/*     */       }
/*  35 */       return true;
/*     */     }
/*  28 */   };
/*     */ 
/*  39 */   private HelpHandler helpHandler = new HelpHandler() {
/*     */     private String formatArgument(CommandArgument argument) {
/*  41 */       String def = argument.getDefault();
/*  42 */       if (def.equals(" ")) {
/*  43 */         def = "";
/*     */       }
/*  45 */       else if (def.startsWith("?")) {
/*  46 */         String varName = def.substring(1);
/*  47 */         def = argument.getHandler().getVariableUserFriendlyName(varName);
/*  48 */         if (def == null)
/*  49 */           throw new IllegalArgumentException(new StringBuilder().append("The ArgumentVariable '").append(varName).append("' is not registered.").toString());
/*  50 */         def = new StringBuilder().append(ChatColor.GOLD).append(" | ").append(ChatColor.WHITE).append(def).toString();
/*     */       }
/*     */       else {
/*  53 */         def = new StringBuilder().append(ChatColor.GOLD).append(" | ").append(ChatColor.WHITE).append(def).toString();
/*     */       }
/*     */ 
/*  56 */       return new StringBuilder().append(ChatColor.AQUA).append("[").append(argument.getName()).append(def).append(ChatColor.AQUA).append("] ").append(ChatColor.DARK_AQUA).append(argument.getDescription()).toString();
/*     */     }
/*     */ 
/*     */     public String[] getHelpMessage(RegisteredCommand command)
/*     */     {
/*  61 */       ArrayList message = new ArrayList();
/*     */ 
/*  63 */       if (command.isSet()) {
/*  64 */         message.add(new StringBuilder().append(ChatColor.AQUA).append(command.getDescription()).toString());
/*     */       }
/*     */ 
/*  67 */       message.add(getUsage(command));
/*     */ 
/*  69 */       if (command.isSet()) {
/*  70 */         for (CommandArgument argument : command.getArguments()) {
/*  71 */           message.add(formatArgument(argument));
/*     */         }
/*  73 */         if (command.getWildcard() != null) {
/*  74 */           message.add(formatArgument(command.getWildcard()));
/*     */         }
/*  76 */         List flags = command.getFlags();
/*  77 */         if (flags.size() > 0) {
/*  78 */           message.add(new StringBuilder().append(ChatColor.GOLD).append("Flags:").toString());
/*  79 */           for (Flag flag : flags) {
/*  80 */             StringBuilder args = new StringBuilder();
/*  81 */             for (FlagArgument argument : flag.getArguments()) {
/*  82 */               args.append(new StringBuilder().append(" [").append(argument.getName()).append("]").toString());
/*     */             }
/*  84 */             message.add(new StringBuilder().append("-").append(flag.getIdentifier()).append(ChatColor.AQUA).append(args.toString()).toString());
/*  85 */             for (FlagArgument argument : flag.getArguments()) {
/*  86 */               message.add(formatArgument(argument));
/*     */             }
/*     */           }
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/*  93 */       List subcommands = command.getSuffixes();
/*  94 */       if (subcommands.size() > 0) {
/*  95 */         message.add(new StringBuilder().append(ChatColor.GOLD).append("Subcommands:").toString());
/*  96 */         for (RegisteredCommand scommand : subcommands) {
/*  97 */           message.add(scommand.getUsage());
/*     */         }
/*     */       }
/*     */ 
/* 101 */       return (String[])message.toArray(new String[0]);
/*     */     }
/*     */ 
/*     */     public String getUsage(RegisteredCommand command)
/*     */     {
/* 106 */       StringBuilder usage = new StringBuilder();
/* 107 */       usage.append(command.getLabel());
/*     */ 
/* 109 */       RegisteredCommand parent = command.getParent();
/* 110 */       while (parent != null) {
/* 111 */         usage.insert(0, new StringBuilder().append(parent.getLabel()).append(" ").toString());
/* 112 */         parent = parent.getParent();
/*     */       }
/*     */ 
/* 115 */       usage.insert(0, "/");
/*     */ 
/* 117 */       if (!command.isSet()) {
/* 118 */         return usage.toString();
/*     */       }
/* 120 */       usage.append(ChatColor.AQUA);
/*     */ 
/* 122 */       for (CommandArgument argument : command.getArguments()) {
/* 123 */         usage.append(new StringBuilder().append(" [").append(argument.getName()).append("]").toString());
/*     */       }
/*     */ 
/* 126 */       usage.append(ChatColor.WHITE);
/*     */ 
/* 128 */       for (Flag flag : command.getFlags()) {
/* 129 */         usage.append(new StringBuilder().append(" (-").append(flag.getIdentifier()).append(ChatColor.AQUA).toString());
/* 130 */         for (FlagArgument arg : flag.getArguments()) {
/* 131 */           usage.append(new StringBuilder().append(" [").append(arg.getName()).append("]").toString());
/*     */         }
/* 133 */         usage.append(new StringBuilder().append(ChatColor.WHITE).append(")").toString());
/*     */       }
/*     */ 
/* 136 */       if (command.getWildcard() != null) {
/* 137 */         usage.append(new StringBuilder().append(ChatColor.AQUA).append(" [").append(command.getWildcard().getName()).append("]").toString());
/*     */       }
/*     */ 
/* 140 */       return usage.toString();
/*     */     }
/*  39 */   };
/*     */ 
/* 144 */   private String helpSuffix = "help";
/*     */ 
/*     */   public CommandHandler(JavaPlugin plugin) {
/* 147 */     this.plugin = plugin;
/*     */ 
/* 149 */     registerArgumentHandler(String.class, new StringArgumentHandler());
/* 150 */     registerArgumentHandler(Integer.TYPE, new IntegerArgumentHandler());
/* 151 */     registerArgumentHandler(Double.TYPE, new DoubleArgumentHandler());
/* 152 */     registerArgumentHandler(Player.class, new PlayerArgumentHandler());
/* 153 */     registerArgumentHandler(World.class, new WorldArgumentHandler());
/* 154 */     registerArgumentHandler(EntityType.class, new EntityTypeArgumentHandler());
/* 155 */     registerArgumentHandler(Material.class, new MaterialArgumentHandler());
/*     */   }
/*     */ 
/*     */   public <T> ArgumentHandler<? extends T> getArgumentHandler(Class<T> clazz)
/*     */   {
/* 160 */     return (ArgumentHandler)this.argumentHandlers.get(clazz);
/*     */   }
/*     */ 
/*     */   public HelpHandler getHelpHandler() {
/* 164 */     return this.helpHandler;
/*     */   }
/*     */ 
/*     */   public PermissionHandler getPermissionHandler() {
/* 168 */     return this.permissionHandler;
/*     */   }
/*     */ 
/*     */   public <T> void registerArgumentHandler(Class<? extends T> clazz, ArgumentHandler<T> argHandler) {
/* 172 */     if (this.argumentHandlers.get(clazz) != null) {
/* 173 */       throw new IllegalArgumentException("The is already a ArgumentHandler bound to the class " + clazz.getName() + ".");
/*     */     }
/* 175 */     argHandler.handler = this;
/* 176 */     this.argumentHandlers.put(clazz, argHandler);
/*     */   }
/*     */ 
/*     */   public void registerCommands(Object commands) {
/* 180 */     for (Method method : commands.getClass().getDeclaredMethods()) {
/* 181 */       Command commandAnno = (Command)method.getAnnotation(Command.class);
/* 182 */       if (commandAnno != null)
/*     */       {
/* 185 */         String[] identifiers = commandAnno.identifier().split(" ");
/* 186 */         if (identifiers.length == 0) {
/* 187 */           throw new RegisterCommandMethodException(method, "Invalid identifiers");
/*     */         }
/* 189 */         PluginCommand rootPcommand = this.plugin.getCommand(identifiers[0]);
/*     */ 
/* 191 */         if (rootPcommand == null) {
/* 192 */           throw new RegisterCommandMethodException(method, "The rootcommand (the first identifier) is not registerd in the plugin.yml");
/*     */         }
/* 194 */         if (rootPcommand.getExecutor() != this) {
/* 195 */           rootPcommand.setExecutor(this);
/*     */         }
/* 197 */         RootCommand rootCommand = (RootCommand)this.rootCommands.get(rootPcommand);
/*     */ 
/* 199 */         if (rootCommand == null) {
/* 200 */           rootCommand = new RootCommand(rootPcommand, this);
/* 201 */           this.rootCommands.put(rootPcommand, rootCommand);
/*     */         }
/*     */ 
/* 204 */         RegisteredCommand mainCommand = rootCommand;
/*     */ 
/* 206 */         for (int i = 1; i < identifiers.length; i++) {
/* 207 */           String suffix = identifiers[i];
/* 208 */           if (mainCommand.doesSuffixCommandExist(suffix)) {
/* 209 */             mainCommand = mainCommand.getSuffixCommand(suffix);
/*     */           }
/*     */           else {
/* 212 */             RegisteredCommand newCommand = new RegisteredCommand(suffix, this, mainCommand);
/* 213 */             mainCommand.addSuffixCommand(suffix, newCommand);
/* 214 */             mainCommand = newCommand;
/*     */           }
/*     */         }
/*     */ 
/* 218 */         mainCommand.set(commands, method);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/* 223 */   public void setHelpHandler(HelpHandler helpHandler) { this.helpHandler = helpHandler; }
/*     */ 
/*     */   public void setPermissionHandler(PermissionHandler permissionHandler)
/*     */   {
/* 227 */     this.permissionHandler = permissionHandler;
/*     */   }
/*     */ 
/*     */   public String getHelpSuffix() {
/* 231 */     return this.helpSuffix;
/*     */   }
/*     */ 
/*     */   public void setHelpSuffix(String suffix) {
/* 235 */     this.helpSuffix = suffix;
/*     */   }
/*     */ 
/*     */   public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args)
/*     */   {
/* 240 */     RootCommand rootCommand = (RootCommand)this.rootCommands.get(command);
/* 241 */     if (rootCommand == null) {
/* 242 */       return false;
/*     */     }
/*     */ 
/* 245 */     if ((rootCommand.onlyPlayers()) && (!(sender instanceof Player))) {
/* 246 */       sender.sendMessage(ChatColor.RED + "Sorry, but only players can execute this command.");
/* 247 */       return true;
/*     */     }
/*     */ 
/* 250 */     rootCommand.execute(sender, args);
/*     */ 
/* 252 */     return true;
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\ItemAttributes.jar
 * Qualified Name:     se.ranzdo.bukkit.methodcommand.CommandHandler
 * JD-Core Version:    0.6.2
 */