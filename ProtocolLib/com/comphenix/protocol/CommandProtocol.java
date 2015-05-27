/*     */ package com.comphenix.protocol;
/*     */ 
/*     */ import com.comphenix.protocol.error.ErrorReporter;
/*     */ import com.comphenix.protocol.events.PacketListener;
/*     */ import com.comphenix.protocol.timing.TimedListenerManager;
/*     */ import com.comphenix.protocol.timing.TimingReportGenerator;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import org.bukkit.ChatColor;
/*     */ import org.bukkit.command.CommandSender;
/*     */ import org.bukkit.plugin.Plugin;
/*     */ import org.bukkit.plugin.PluginDescriptionFile;
/*     */ 
/*     */ class CommandProtocol extends CommandBase
/*     */ {
/*     */   public static final String NAME = "protocol";
/*     */   private Plugin plugin;
/*     */   private ProtocolConfig config;
/*     */ 
/*     */   public CommandProtocol(ErrorReporter reporter, Plugin plugin, ProtocolConfig config)
/*     */   {
/*  48 */     super(reporter, "protocol.admin", "protocol", 1);
/*  49 */     this.plugin = plugin;
/*  50 */     this.config = config;
/*     */   }
/*     */ 
/*     */   protected boolean handleCommand(CommandSender sender, String[] args)
/*     */   {
/*  55 */     String subCommand = args[0];
/*     */ 
/*  58 */     if ((subCommand.equalsIgnoreCase("config")) || (subCommand.equalsIgnoreCase("reload")))
/*  59 */       reloadConfiguration(sender);
/*  60 */     else if (subCommand.equalsIgnoreCase("timings"))
/*  61 */       toggleTimings(sender, args);
/*  62 */     else if (subCommand.equalsIgnoreCase("listeners"))
/*  63 */       printListeners(sender);
/*  64 */     else if (subCommand.equalsIgnoreCase("version"))
/*  65 */       printVersion(sender);
/*     */     else
/*  67 */       return false;
/*  68 */     return true;
/*     */   }
/*     */ 
/*     */   private void printListeners(CommandSender sender)
/*     */   {
/*  73 */     ProtocolManager manager = ProtocolLibrary.getProtocolManager();
/*     */ 
/*  75 */     sender.sendMessage(ChatColor.GOLD + "Packet listeners:");
/*  76 */     for (PacketListener listener : manager.getPacketListeners()) {
/*  77 */       sender.sendMessage(ChatColor.GOLD + " - " + listener);
/*     */     }
/*     */ 
/*  81 */     sender.sendMessage(ChatColor.GOLD + "Asynchronous listeners:");
/*  82 */     for (PacketListener listener : manager.getAsynchronousManager().getAsyncHandlers())
/*  83 */       sender.sendMessage(ChatColor.GOLD + " - " + listener);
/*     */   }
/*     */ 
/*     */   private void toggleTimings(CommandSender sender, String[] args)
/*     */   {
/*  88 */     TimedListenerManager manager = TimedListenerManager.getInstance();
/*  89 */     boolean state = !manager.isTiming();
/*     */ 
/*  92 */     if (args.length == 2) {
/*  93 */       Boolean parsed = parseBoolean(toQueue(args, 2), "start");
/*     */ 
/*  95 */       if (parsed != null) {
/*  96 */         state = parsed.booleanValue();
/*     */       } else {
/*  98 */         sender.sendMessage(ChatColor.RED + "Specify a state: ON or OFF.");
/*  99 */         return;
/*     */       }
/* 101 */     } else if (args.length > 2) {
/* 102 */       sender.sendMessage(ChatColor.RED + "Too many parameters.");
/* 103 */       return;
/*     */     }
/*     */ 
/* 107 */     if (state) {
/* 108 */       if (manager.startTiming())
/* 109 */         sender.sendMessage(ChatColor.GOLD + "Started timing packet listeners.");
/*     */       else
/* 111 */         sender.sendMessage(ChatColor.RED + "Packet timing already started.");
/*     */     }
/* 113 */     else if (manager.stopTiming()) {
/* 114 */       saveTimings(manager);
/* 115 */       sender.sendMessage(ChatColor.GOLD + "Stopped and saved result in plugin folder.");
/*     */     } else {
/* 117 */       sender.sendMessage(ChatColor.RED + "Packet timing already stopped.");
/*     */     }
/*     */   }
/*     */ 
/*     */   private void saveTimings(TimedListenerManager manager)
/*     */   {
/*     */     try {
/* 124 */       File destination = new File(this.plugin.getDataFolder(), "Timings - " + System.currentTimeMillis() + ".txt");
/* 125 */       TimingReportGenerator generator = new TimingReportGenerator();
/*     */ 
/* 128 */       generator.saveTo(destination, manager);
/* 129 */       manager.clear();
/*     */     } catch (IOException e) {
/* 131 */       this.reporter.reportMinimal(this.plugin, "saveTimings()", e);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void printVersion(CommandSender sender) {
/* 136 */     PluginDescriptionFile desc = this.plugin.getDescription();
/*     */ 
/* 138 */     sender.sendMessage(ChatColor.GREEN + desc.getName() + ChatColor.WHITE + " v" + ChatColor.GREEN + desc.getVersion());
/* 139 */     sender.sendMessage(ChatColor.WHITE + "Authors: " + ChatColor.GREEN + "dmulloy2" + ChatColor.WHITE + " and " + ChatColor.GREEN + "Comphenix");
/* 140 */     sender.sendMessage(ChatColor.WHITE + "Issues: " + ChatColor.GREEN + "https://github.com/dmulloy2/ProtocolLib/issues");
/*     */   }
/*     */ 
/*     */   public void updateFinished()
/*     */   {
/* 147 */     long currentTime = System.currentTimeMillis() / 1000L;
/*     */ 
/* 149 */     this.config.setAutoLastTime(currentTime);
/* 150 */     this.config.saveAll();
/*     */   }
/*     */ 
/*     */   public void reloadConfiguration(CommandSender sender) {
/* 154 */     this.plugin.reloadConfig();
/* 155 */     sender.sendMessage(ChatColor.YELLOW + "Reloaded configuration!");
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.CommandProtocol
 * JD-Core Version:    0.6.2
 */