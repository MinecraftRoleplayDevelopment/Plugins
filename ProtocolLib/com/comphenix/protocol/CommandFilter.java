/*     */ package com.comphenix.protocol;
/*     */ 
/*     */ import com.comphenix.protocol.error.ErrorReporter;
/*     */ import com.comphenix.protocol.error.Report;
/*     */ import com.comphenix.protocol.error.Report.ReportBuilder;
/*     */ import com.comphenix.protocol.error.ReportType;
/*     */ import com.comphenix.protocol.events.PacketContainer;
/*     */ import com.comphenix.protocol.events.PacketEvent;
/*     */ import com.google.common.collect.Sets;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Deque;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Set;
/*     */ import java.util.logging.Logger;
/*     */ import javax.script.Invocable;
/*     */ import javax.script.ScriptEngine;
/*     */ import javax.script.ScriptEngineManager;
/*     */ import javax.script.ScriptException;
/*     */ import org.bukkit.ChatColor;
/*     */ import org.bukkit.command.CommandSender;
/*     */ import org.bukkit.conversations.Conversable;
/*     */ import org.bukkit.conversations.Conversation;
/*     */ import org.bukkit.conversations.ConversationAbandonedEvent;
/*     */ import org.bukkit.conversations.ConversationAbandonedListener;
/*     */ import org.bukkit.conversations.ConversationContext;
/*     */ import org.bukkit.conversations.ConversationFactory;
/*     */ import org.bukkit.plugin.Plugin;
/*     */ 
/*     */ public class CommandFilter extends CommandBase
/*     */ {
/*  37 */   public static final ReportType REPORT_FALLBACK_ENGINE = new ReportType("Falling back to the Rhino engine.");
/*  38 */   public static final ReportType REPORT_CANNOT_LOAD_FALLBACK_ENGINE = new ReportType("Could not load Rhino either. Please upgrade your JVM or OS.");
/*  39 */   public static final ReportType REPORT_PACKAGES_UNSUPPORTED_IN_ENGINE = new ReportType("Unable to initialize packages for JavaScript engine.");
/*  40 */   public static final ReportType REPORT_FILTER_REMOVED_FOR_ERROR = new ReportType("Removing filter %s for causing %s.");
/*  41 */   public static final ReportType REPORT_CANNOT_HANDLE_CONVERSATION = new ReportType("Cannot handle conversation.");
/*     */   public static final String NAME = "filter";
/*     */   private FilterFailedHandler defaultFailedHandler;
/* 209 */   private List<Filter> filters = new ArrayList();
/*     */   private final Plugin plugin;
/*     */   private ProtocolConfig config;
/*     */   private ScriptEngine engine;
/*     */   private boolean uninitialized;
/*     */ 
/*     */   public CommandFilter(ErrorReporter reporter, Plugin plugin, ProtocolConfig config)
/*     */   {
/* 222 */     super(reporter, "protocol.admin", "filter", 2);
/* 223 */     this.plugin = plugin;
/* 224 */     this.config = config;
/*     */ 
/* 227 */     this.uninitialized = true;
/*     */   }
/*     */ 
/*     */   private void initalizeScript()
/*     */   {
/*     */     try {
/* 233 */       initializeEngine();
/*     */ 
/* 236 */       if (!isInitialized()) {
/* 237 */         throw new ScriptException("A JavaScript engine could not be found.");
/*     */       }
/* 239 */       this.plugin.getLogger().info("Loaded command filter engine.");
/*     */     }
/*     */     catch (ScriptException e1)
/*     */     {
/* 243 */       printPackageWarning(e1);
/*     */ 
/* 245 */       if (!this.config.getScriptEngineName().equals("rhino")) {
/* 246 */         this.reporter.reportWarning(this, Report.newBuilder(REPORT_FALLBACK_ENGINE));
/* 247 */         this.config.setScriptEngineName("rhino");
/* 248 */         this.config.saveAll();
/*     */         try
/*     */         {
/* 251 */           initializeEngine();
/*     */ 
/* 253 */           if (!isInitialized())
/* 254 */             this.reporter.reportWarning(this, Report.newBuilder(REPORT_CANNOT_LOAD_FALLBACK_ENGINE));
/*     */         }
/*     */         catch (ScriptException e2)
/*     */         {
/* 258 */           printPackageWarning(e2);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private void printPackageWarning(ScriptException e) {
/* 265 */     this.reporter.reportWarning(this, Report.newBuilder(REPORT_PACKAGES_UNSUPPORTED_IN_ENGINE).error(e));
/*     */   }
/*     */ 
/*     */   private void initializeEngine()
/*     */     throws ScriptException
/*     */   {
/* 273 */     ScriptEngineManager manager = new ScriptEngineManager();
/* 274 */     this.engine = manager.getEngineByName(this.config.getScriptEngineName());
/*     */ 
/* 277 */     if (this.engine != null) {
/* 278 */       this.engine.eval("importPackage(org.bukkit);");
/* 279 */       this.engine.eval("importPackage(com.comphenix.protocol.reflect);");
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean isInitialized()
/*     */   {
/* 288 */     return this.engine != null;
/*     */   }
/*     */ 
/*     */   private FilterFailedHandler getDefaultErrorHandler()
/*     */   {
/* 293 */     if (this.defaultFailedHandler == null) {
/* 294 */       this.defaultFailedHandler = new FilterFailedHandler()
/*     */       {
/*     */         public boolean handle(PacketEvent event, CommandFilter.Filter filter, Exception ex) {
/* 297 */           CommandFilter.this.reporter.reportMinimal(CommandFilter.this.plugin, "filterEvent(PacketEvent)", ex, new Object[] { event });
/* 298 */           CommandFilter.this.reporter.reportWarning(this, Report.newBuilder(CommandFilter.REPORT_FILTER_REMOVED_FOR_ERROR).messageParam(new Object[] { filter.getName(), ex.getClass().getSimpleName() }));
/*     */ 
/* 301 */           return false;
/*     */         }
/*     */       };
/*     */     }
/* 305 */     return this.defaultFailedHandler;
/*     */   }
/*     */ 
/*     */   public boolean filterEvent(PacketEvent event)
/*     */   {
/* 316 */     return filterEvent(event, getDefaultErrorHandler());
/*     */   }
/*     */ 
/*     */   public boolean filterEvent(PacketEvent event, FilterFailedHandler handler)
/*     */   {
/* 327 */     for (Iterator it = this.filters.iterator(); it.hasNext(); ) {
/* 328 */       Filter filter = (Filter)it.next();
/*     */       try
/*     */       {
/* 331 */         if (!filter.evaluate(this.engine, event))
/* 332 */           return false;
/*     */       }
/*     */       catch (Exception ex) {
/* 335 */         if (!handler.handle(event, filter, ex)) {
/* 336 */           it.remove();
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 341 */     return true;
/*     */   }
/*     */ 
/*     */   private void checkScriptStatus()
/*     */   {
/* 349 */     if (this.uninitialized) {
/* 350 */       this.uninitialized = false;
/* 351 */       initalizeScript();
/*     */     }
/*     */   }
/*     */ 
/*     */   protected boolean handleCommand(CommandSender sender, String[] args)
/*     */   {
/* 361 */     checkScriptStatus();
/*     */ 
/* 363 */     if (!this.config.isDebug()) {
/* 364 */       sender.sendMessage(ChatColor.RED + "Debug mode must be enabled in the configuration first!");
/* 365 */       return true;
/*     */     }
/* 367 */     if (!isInitialized()) {
/* 368 */       sender.sendMessage(ChatColor.RED + "JavaScript engine was not present. Filter system is disabled.");
/* 369 */       return true;
/*     */     }
/*     */ 
/* 372 */     SubCommand command = parseCommand(args, 0);
/* 373 */     final String name = args[1];
/*     */ 
/* 375 */     switch (3.$SwitchMap$com$comphenix$protocol$CommandFilter$SubCommand[command.ordinal()])
/*     */     {
/*     */     case 1:
/* 378 */       if (findFilter(name) != null) {
/* 379 */         sender.sendMessage(ChatColor.RED + "Filter " + name + " already exists. Remove it first.");
/* 380 */         return true;
/*     */       }
/*     */ 
/* 384 */       Deque rangeArguments = toQueue(args, 2);
/*     */ 
/* 386 */       PacketTypeParser parser = new PacketTypeParser();
/* 387 */       final Set packets = parser.parseTypes(rangeArguments, PacketTypeParser.DEFAULT_MAX_RANGE);
/* 388 */       sender.sendMessage("Enter filter program ('}' to complete or CANCEL):");
/*     */ 
/* 391 */       if ((sender instanceof Conversable)) {
/* 392 */         final MultipleLinesPrompt prompt = new MultipleLinesPrompt(new CompilationSuccessCanceller(null), "function(event, packet) {");
/*     */ 
/* 395 */         new ConversationFactory(this.plugin).withFirstPrompt(prompt).withEscapeSequence("CANCEL").withLocalEcho(false).addConversationAbandonedListener(new ConversationAbandonedListener()
/*     */         {
/*     */           public void conversationAbandoned(ConversationAbandonedEvent event)
/*     */           {
/*     */             try
/*     */             {
/* 403 */               Conversable whom = event.getContext().getForWhom();
/*     */ 
/* 405 */               if (event.gracefulExit()) {
/* 406 */                 String predicate = prompt.removeAccumulatedInput(event.getContext());
/* 407 */                 CommandFilter.Filter filter = new CommandFilter.Filter(name, predicate, packets);
/*     */ 
/* 410 */                 whom.sendRawMessage(prompt.getPromptText(event.getContext()));
/*     */                 try
/*     */                 {
/* 414 */                   filter.compile(CommandFilter.this.engine);
/*     */ 
/* 416 */                   CommandFilter.this.filters.add(filter);
/* 417 */                   whom.sendRawMessage(ChatColor.GOLD + "Added filter " + name);
/*     */                 } catch (ScriptException e) {
/* 419 */                   e.printStackTrace();
/* 420 */                   whom.sendRawMessage(ChatColor.GOLD + "Compilation error: " + e.getMessage());
/*     */                 }
/*     */               }
/*     */               else {
/* 424 */                 whom.sendRawMessage(ChatColor.RED + "Cancelled filter.");
/*     */               }
/*     */             } catch (Exception e) {
/* 427 */               CommandFilter.this.reporter.reportDetailed(this, Report.newBuilder(CommandFilter.REPORT_CANNOT_HANDLE_CONVERSATION).error(e).callerParam(new Object[] { event }));
/*     */             }
/*     */           }
/*     */         }).buildConversation((Conversable)sender).begin();
/*     */       }
/*     */       else
/*     */       {
/* 436 */         sender.sendMessage(ChatColor.RED + "Only console and players are supported!");
/*     */       }
/*     */ 
/* 439 */       break;
/*     */     case 2:
/* 442 */       Filter filter = findFilter(name);
/*     */ 
/* 445 */       if (filter != null) {
/* 446 */         filter.close(this.engine);
/* 447 */         this.filters.remove(filter);
/* 448 */         sender.sendMessage(ChatColor.GOLD + "Removed filter " + name);
/*     */       } else {
/* 450 */         sender.sendMessage(ChatColor.RED + "Unable to find a filter by the name " + name);
/*     */       }
/*     */       break;
/*     */     }
/*     */ 
/* 455 */     return true;
/*     */   }
/*     */ 
/*     */   private Filter findFilter(String name)
/*     */   {
/* 465 */     for (Filter filter : this.filters) {
/* 466 */       if (filter.getName().equalsIgnoreCase(name)) {
/* 467 */         return filter;
/*     */       }
/*     */     }
/* 470 */     return null;
/*     */   }
/*     */ 
/*     */   private SubCommand parseCommand(String[] args, int index) {
/* 474 */     String text = args[index].toUpperCase();
/*     */     try
/*     */     {
/* 477 */       return SubCommand.valueOf(text);
/*     */     } catch (IllegalArgumentException e) {
/* 479 */       throw new IllegalArgumentException(text + " is not a valid sub command. Must be add or remove.", e);
/*     */     }
/*     */   }
/*     */ 
/*     */   private class CompilationSuccessCanceller
/*     */     implements MultipleLinesPrompt.MultipleConversationCanceller
/*     */   {
/*     */     private CompilationSuccessCanceller()
/*     */     {
/*     */     }
/*     */ 
/*     */     public boolean cancelBasedOnInput(ConversationContext context, String in)
/*     */     {
/* 170 */       throw new UnsupportedOperationException("Cannot cancel on the last line alone.");
/*     */     }
/*     */ 
/*     */     public void setConversation(Conversation conversation)
/*     */     {
/*     */     }
/*     */ 
/*     */     public boolean cancelBasedOnInput(ConversationContext context, String currentLine, StringBuilder lines, int lineCount)
/*     */     {
/*     */       try
/*     */       {
/* 181 */         CommandFilter.this.engine.eval("function(event, packet) {\n" + lines.toString());
/*     */ 
/* 184 */         return true;
/*     */       }
/*     */       catch (ScriptException e) {
/* 187 */         int realLineCount = lineCount + 1;
/*     */ 
/* 190 */         if (e.getLineNumber() < realLineCount) tmpTernaryOp = true;  } return false;
/*     */     }
/*     */ 
/*     */     public CompilationSuccessCanceller clone()
/*     */     {
/* 196 */       return new CompilationSuccessCanceller(CommandFilter.this);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static class Filter
/*     */   {
/*     */     private final String name;
/*     */     private final String predicate;
/*     */     private final Set<PacketType> packets;
/*     */ 
/*     */     public Filter(String name, String predicate, Set<PacketType> packets)
/*     */     {
/*  80 */       this.name = name;
/*  81 */       this.predicate = predicate;
/*  82 */       this.packets = Sets.newHashSet(packets);
/*     */     }
/*     */ 
/*     */     public String getName()
/*     */     {
/*  90 */       return this.name;
/*     */     }
/*     */ 
/*     */     public String getPredicate()
/*     */     {
/*  98 */       return this.predicate;
/*     */     }
/*     */ 
/*     */     public Set<PacketType> getRanges()
/*     */     {
/* 106 */       return Sets.newHashSet(this.packets);
/*     */     }
/*     */ 
/*     */     private boolean isApplicable(PacketEvent event)
/*     */     {
/* 115 */       return this.packets.contains(event.getPacketType());
/*     */     }
/*     */ 
/*     */     public boolean evaluate(ScriptEngine context, PacketEvent event)
/*     */       throws ScriptException
/*     */     {
/* 128 */       if (!isApplicable(event)) {
/* 129 */         return true;
/*     */       }
/* 131 */       compile(context);
/*     */       try
/*     */       {
/* 134 */         Object result = ((Invocable)context).invokeFunction(this.name, new Object[] { event, event.getPacket().getHandle() });
/*     */ 
/* 136 */         if ((result instanceof Boolean)) {
/* 137 */           return ((Boolean)result).booleanValue();
/*     */         }
/* 139 */         throw new ScriptException("Filter result wasn't a boolean: " + result);
/*     */       }
/*     */       catch (NoSuchMethodException e)
/*     */       {
/* 143 */         throw new IllegalStateException("Unable to compile " + this.name + " into current script engine.", e);
/*     */       }
/*     */     }
/*     */ 
/*     */     public void compile(ScriptEngine context)
/*     */       throws ScriptException
/*     */     {
/* 153 */       if (context.get(this.name) == null)
/* 154 */         context.eval("var " + this.name + " = function(event, packet) {\n" + this.predicate);
/*     */     }
/*     */ 
/*     */     public void close(ScriptEngine context)
/*     */     {
/* 163 */       context.put(this.name, null);
/*     */     }
/*     */   }
/*     */ 
/*     */   private static enum SubCommand
/*     */   {
/*  60 */     ADD, REMOVE;
/*     */   }
/*     */ 
/*     */   public static abstract interface FilterFailedHandler
/*     */   {
/*     */     public abstract boolean handle(PacketEvent paramPacketEvent, CommandFilter.Filter paramFilter, Exception paramException);
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.CommandFilter
 * JD-Core Version:    0.6.2
 */