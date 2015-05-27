/*     */ package com.comphenix.protocol;
/*     */ 
/*     */ import com.comphenix.protocol.error.ErrorReporter;
/*     */ import com.comphenix.protocol.error.Report;
/*     */ import com.comphenix.protocol.error.Report.ReportBuilder;
/*     */ import com.comphenix.protocol.error.ReportType;
/*     */ import java.util.ArrayDeque;
/*     */ import java.util.Arrays;
/*     */ import java.util.Deque;
/*     */ import java.util.List;
/*     */ import org.bukkit.ChatColor;
/*     */ import org.bukkit.command.Command;
/*     */ import org.bukkit.command.CommandExecutor;
/*     */ import org.bukkit.command.CommandSender;
/*     */ 
/*     */ abstract class CommandBase
/*     */   implements CommandExecutor
/*     */ {
/*  39 */   public static final ReportType REPORT_COMMAND_ERROR = new ReportType("Cannot execute command %s.");
/*  40 */   public static final ReportType REPORT_UNEXPECTED_COMMAND = new ReportType("Incorrect command assigned to %s.");
/*     */   public static final String PERMISSION_ADMIN = "protocol.admin";
/*     */   private String permission;
/*     */   private String name;
/*     */   private int minimumArgumentCount;
/*     */   protected ErrorReporter reporter;
/*     */ 
/*     */   public CommandBase(ErrorReporter reporter, String permission, String name)
/*     */   {
/*  51 */     this(reporter, permission, name, 0);
/*     */   }
/*     */ 
/*     */   public CommandBase(ErrorReporter reporter, String permission, String name, int minimumArgumentCount) {
/*  55 */     this.reporter = reporter;
/*  56 */     this.name = name;
/*  57 */     this.permission = permission;
/*  58 */     this.minimumArgumentCount = minimumArgumentCount;
/*     */   }
/*     */ 
/*     */   public final boolean onCommand(CommandSender sender, Command command, String label, String[] args)
/*     */   {
/*     */     try
/*     */     {
/*  65 */       if (!command.getName().equalsIgnoreCase(this.name)) {
/*  66 */         this.reporter.reportWarning(this, Report.newBuilder(REPORT_UNEXPECTED_COMMAND).messageParam(new Object[] { this }));
/*  67 */         return false;
/*     */       }
/*     */ 
/*  70 */       if ((this.permission != null) && (!sender.hasPermission(this.permission))) {
/*  71 */         sender.sendMessage(ChatColor.RED + "You haven't got permission to run this command.");
/*  72 */         return true;
/*     */       }
/*     */ 
/*  76 */       if ((args != null) && (args.length >= this.minimumArgumentCount)) {
/*  77 */         return handleCommand(sender, args);
/*     */       }
/*  79 */       sender.sendMessage(ChatColor.RED + "Insufficient arguments. You need at least " + this.minimumArgumentCount);
/*  80 */       return false;
/*     */     }
/*     */     catch (Throwable ex) {
/*  83 */       this.reporter.reportDetailed(this, Report.newBuilder(REPORT_COMMAND_ERROR).error(ex).messageParam(new Object[] { this.name }).callerParam(new Object[] { sender, label, args }));
/*     */     }
/*     */ 
/*  86 */     return true;
/*     */   }
/*     */ 
/*     */   protected Boolean parseBoolean(Deque<String> arguments, String parameterName)
/*     */   {
/*  97 */     Boolean result = null;
/*     */ 
/*  99 */     if (!arguments.isEmpty()) {
/* 100 */       String arg = (String)arguments.peek();
/*     */ 
/* 102 */       if ((arg.equalsIgnoreCase("true")) || (arg.equalsIgnoreCase("on")))
/* 103 */         result = Boolean.valueOf(true);
/* 104 */       else if (arg.equalsIgnoreCase(parameterName))
/* 105 */         result = Boolean.valueOf(true);
/* 106 */       else if ((arg.equalsIgnoreCase("false")) || (arg.equalsIgnoreCase("off"))) {
/* 107 */         result = Boolean.valueOf(false);
/*     */       }
/*     */     }
/* 110 */     if (result != null)
/* 111 */       arguments.poll();
/* 112 */     return result;
/*     */   }
/*     */ 
/*     */   protected Deque<String> toQueue(String[] args, int start)
/*     */   {
/* 122 */     return new ArrayDeque(Arrays.asList(args).subList(start, args.length));
/*     */   }
/*     */ 
/*     */   public String getPermission()
/*     */   {
/* 130 */     return this.permission;
/*     */   }
/*     */ 
/*     */   public String getName()
/*     */   {
/* 138 */     return this.name;
/*     */   }
/*     */ 
/*     */   protected ErrorReporter getReporter()
/*     */   {
/* 146 */     return this.reporter;
/*     */   }
/*     */ 
/*     */   protected abstract boolean handleCommand(CommandSender paramCommandSender, String[] paramArrayOfString);
/*     */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.CommandBase
 * JD-Core Version:    0.6.2
 */