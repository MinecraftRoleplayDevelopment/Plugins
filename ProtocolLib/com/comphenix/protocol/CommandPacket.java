/*     */ package com.comphenix.protocol;
/*     */ 
/*     */ import com.comphenix.net.sf.cglib.proxy.Factory;
/*     */ import com.comphenix.protocol.concurrency.PacketTypeSet;
/*     */ import com.comphenix.protocol.error.ErrorReporter;
/*     */ import com.comphenix.protocol.error.Report;
/*     */ import com.comphenix.protocol.error.Report.ReportBuilder;
/*     */ import com.comphenix.protocol.error.ReportType;
/*     */ import com.comphenix.protocol.events.ListeningWhitelist;
/*     */ import com.comphenix.protocol.events.ListeningWhitelist.Builder;
/*     */ import com.comphenix.protocol.events.PacketContainer;
/*     */ import com.comphenix.protocol.events.PacketEvent;
/*     */ import com.comphenix.protocol.events.PacketListener;
/*     */ import com.comphenix.protocol.reflect.EquivalentConverter;
/*     */ import com.comphenix.protocol.reflect.PrettyPrinter;
/*     */ import com.comphenix.protocol.reflect.PrettyPrinter.ObjectPrinter;
/*     */ import com.comphenix.protocol.utility.ChatExtensions;
/*     */ import com.comphenix.protocol.utility.HexDumper;
/*     */ import com.comphenix.protocol.utility.MinecraftReflection;
/*     */ import com.comphenix.protocol.wrappers.BukkitConverters;
/*     */ import com.google.common.collect.MapMaker;
/*     */ import com.google.common.collect.Sets;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.util.ArrayDeque;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Deque;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.WeakHashMap;
/*     */ import java.util.logging.Level;
/*     */ import java.util.logging.Logger;
/*     */ import org.bukkit.ChatColor;
/*     */ import org.bukkit.command.CommandSender;
/*     */ import org.bukkit.entity.Player;
/*     */ import org.bukkit.plugin.Plugin;
/*     */ 
/*     */ class CommandPacket extends CommandBase
/*     */ {
/*  64 */   public static final ReportType REPORT_CANNOT_SEND_MESSAGE = new ReportType("Cannot send chat message.");
/*     */   public static final String NAME = "packet";
/*     */   public static final int PAGE_LINE_COUNT = 9;
/*     */   private static final int HEX_DUMP_THRESHOLD = 256;
/*     */   private Plugin plugin;
/*     */   private Logger logger;
/*     */   private ProtocolManager manager;
/*     */   private ChatExtensions chatter;
/*  92 */   private PacketTypeParser typeParser = new PacketTypeParser();
/*     */ 
/*  95 */   private Map<CommandSender, List<String>> pagedMessage = new WeakHashMap();
/*     */ 
/*  98 */   private PacketTypeSet packetTypes = new PacketTypeSet();
/*  99 */   private PacketTypeSet extendedTypes = new PacketTypeSet();
/*     */ 
/* 102 */   private PacketTypeSet compareTypes = new PacketTypeSet();
/* 103 */   private Map<PacketEvent, String> originalPackets = new MapMaker().weakKeys().makeMap();
/*     */   private PacketListener listener;
/*     */   private PacketListener compareListener;
/*     */   private CommandFilter filter;
/*     */ 
/*     */   public CommandPacket(ErrorReporter reporter, Plugin plugin, Logger logger, CommandFilter filter, ProtocolManager manager)
/*     */   {
/* 115 */     super(reporter, "protocol.admin", "packet", 1);
/* 116 */     this.plugin = plugin;
/* 117 */     this.logger = logger;
/* 118 */     this.manager = manager;
/* 119 */     this.filter = filter;
/* 120 */     this.chatter = new ChatExtensions(manager);
/*     */   }
/*     */ 
/*     */   public void sendMessageSilently(CommandSender receiver, String message)
/*     */   {
/*     */     try
/*     */     {
/* 131 */       this.chatter.sendMessageSilently(receiver, message);
/*     */     } catch (InvocationTargetException e) {
/* 133 */       this.reporter.reportDetailed(this, Report.newBuilder(REPORT_CANNOT_SEND_MESSAGE).error(e).callerParam(new Object[] { receiver, message }));
/*     */     }
/*     */   }
/*     */ 
/*     */   public void broadcastMessageSilently(String message, String permission)
/*     */   {
/*     */     try
/*     */     {
/* 146 */       this.chatter.broadcastMessageSilently(message, permission);
/*     */     } catch (InvocationTargetException e) {
/* 148 */       this.reporter.reportDetailed(this, Report.newBuilder(REPORT_CANNOT_SEND_MESSAGE).error(e).callerParam(new Object[] { message, permission }));
/*     */     }
/*     */   }
/*     */ 
/*     */   private void printPage(CommandSender sender, int pageIndex)
/*     */   {
/* 155 */     List paged = (List)this.pagedMessage.get(sender);
/*     */ 
/* 158 */     if (paged != null) {
/* 159 */       int lastPage = (paged.size() - 1) / 9 + 1;
/*     */ 
/* 161 */       for (int i = 9 * (pageIndex - 1); i < 9 * pageIndex; i++) {
/* 162 */         if (i < paged.size()) {
/* 163 */           sendMessageSilently(sender, " " + (String)paged.get(i));
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 168 */       if (pageIndex < lastPage)
/* 169 */         sendMessageSilently(sender, "Send /packet page " + (pageIndex + 1) + " for the next page.");
/*     */     }
/*     */     else
/*     */     {
/* 173 */       sendMessageSilently(sender, ChatColor.RED + "No pages found.");
/*     */     }
/*     */   }
/*     */ 
/*     */   protected boolean handleCommand(CommandSender sender, String[] args)
/*     */   {
/*     */     try
/*     */     {
/* 184 */       Deque arguments = new ArrayDeque(Arrays.asList(args));
/* 185 */       SubCommand subCommand = parseCommand(arguments);
/*     */ 
/* 188 */       if (subCommand == SubCommand.PAGE) {
/* 189 */         if (args.length <= 1) {
/* 190 */           sendMessageSilently(sender, ChatColor.RED + "Must specify a page index.");
/* 191 */           return true;
/*     */         }
/*     */ 
/* 194 */         int page = Integer.parseInt(args[1]);
/*     */ 
/* 196 */         if (page > 0)
/* 197 */           printPage(sender, page);
/*     */         else
/* 199 */           sendMessageSilently(sender, ChatColor.RED + "Page index must be greater than zero.");
/* 200 */         return true;
/*     */       }
/*     */ 
/* 203 */       Set types = this.typeParser.parseTypes(arguments, PacketTypeParser.DEFAULT_MAX_RANGE);
/* 204 */       Boolean detailed = parseBoolean(arguments, "detailed");
/* 205 */       Boolean compare = parseBoolean(arguments, "compare");
/*     */ 
/* 208 */       if (this.typeParser.getLastProtocol() == null) {
/* 209 */         sender.sendMessage(ChatColor.YELLOW + "Warning: Missing protocol (PLAY, etc) - assuming legacy IDs.");
/*     */       }
/* 211 */       if (arguments.size() > 0) {
/* 212 */         throw new IllegalArgumentException("Cannot parse " + arguments);
/*     */       }
/*     */ 
/* 216 */       if (detailed == null) {
/* 217 */         detailed = Boolean.valueOf(false);
/*     */       }
/* 219 */       if (compare == null) {
/* 220 */         compare = Boolean.valueOf(false);
/*     */       }
/*     */       else {
/* 223 */         detailed = Boolean.valueOf(true);
/*     */       }
/*     */ 
/* 227 */       if (subCommand == SubCommand.ADD)
/*     */       {
/* 229 */         if (args.length == 1) {
/* 230 */           sender.sendMessage(ChatColor.RED + "Please specify a connection side.");
/* 231 */           return false;
/*     */         }
/*     */ 
/* 234 */         executeAddCommand(sender, types, detailed.booleanValue(), compare.booleanValue());
/* 235 */       } else if (subCommand == SubCommand.REMOVE) {
/* 236 */         executeRemoveCommand(sender, types);
/* 237 */       } else if (subCommand == SubCommand.NAMES) {
/* 238 */         executeNamesCommand(sender, types);
/*     */       }
/*     */     }
/*     */     catch (NumberFormatException e) {
/* 242 */       sendMessageSilently(sender, ChatColor.RED + "Cannot parse number: " + e.getMessage());
/*     */     } catch (IllegalArgumentException e) {
/* 244 */       sendMessageSilently(sender, ChatColor.RED + e.getMessage());
/*     */     }
/*     */ 
/* 247 */     return true;
/*     */   }
/*     */ 
/*     */   private void executeAddCommand(CommandSender sender, Set<PacketType> addition, boolean detailed, boolean compare) {
/* 251 */     this.packetTypes.addAll(addition);
/*     */ 
/* 254 */     if (detailed) {
/* 255 */       this.extendedTypes.addAll(addition);
/*     */     }
/*     */ 
/* 258 */     if (compare) {
/* 259 */       this.compareTypes.addAll(addition);
/*     */     }
/*     */ 
/* 262 */     updatePacketListener();
/* 263 */     sendMessageSilently(sender, ChatColor.YELLOW + "Added listener " + getWhitelistInfo(this.listener));
/*     */   }
/*     */ 
/*     */   private void executeRemoveCommand(CommandSender sender, Set<PacketType> removal) {
/* 267 */     this.packetTypes.removeAll(removal);
/* 268 */     this.extendedTypes.removeAll(removal);
/* 269 */     this.compareTypes.removeAll(removal);
/* 270 */     updatePacketListener();
/* 271 */     sendMessageSilently(sender, ChatColor.YELLOW + "Removing packet types.");
/*     */   }
/*     */ 
/*     */   private void executeNamesCommand(CommandSender sender, Set<PacketType> types) {
/* 275 */     List messages = new ArrayList();
/*     */ 
/* 278 */     for (PacketType type : types) {
/* 279 */       messages.add(ChatColor.YELLOW + type.toString());
/*     */     }
/*     */ 
/* 282 */     if (((sender instanceof Player)) && (messages.size() > 0) && (messages.size() > 9))
/*     */     {
/* 284 */       this.pagedMessage.put(sender, messages);
/* 285 */       printPage(sender, 1);
/*     */     }
/*     */     else
/*     */     {
/* 289 */       for (String message : messages)
/* 290 */         sendMessageSilently(sender, message);
/*     */     }
/*     */   }
/*     */ 
/*     */   private String getWhitelistInfo(PacketListener listener)
/*     */   {
/* 301 */     boolean sendingEmpty = ListeningWhitelist.isEmpty(listener.getSendingWhitelist());
/* 302 */     boolean receivingEmpty = ListeningWhitelist.isEmpty(listener.getReceivingWhitelist());
/*     */ 
/* 304 */     if ((!sendingEmpty) && (!receivingEmpty))
/* 305 */       return String.format("Sending: %s, Receiving: %s", new Object[] { listener.getSendingWhitelist(), listener.getReceivingWhitelist() });
/* 306 */     if (!sendingEmpty)
/* 307 */       return listener.getSendingWhitelist().toString();
/* 308 */     if (!receivingEmpty) {
/* 309 */       return listener.getReceivingWhitelist().toString();
/*     */     }
/* 311 */     return "[None]";
/*     */   }
/*     */ 
/*     */   private Set<PacketType> filterTypes(Set<PacketType> types, PacketType.Sender sender) {
/* 315 */     Set result = Sets.newHashSet();
/*     */ 
/* 317 */     for (PacketType type : types) {
/* 318 */       if (type.getSender() == sender) {
/* 319 */         result.add(type);
/*     */       }
/*     */     }
/* 322 */     return result;
/*     */   }
/*     */ 
/*     */   public PacketListener createPacketListener(Set<PacketType> type) {
/* 326 */     final ListeningWhitelist serverList = ListeningWhitelist.newBuilder().types(filterTypes(type, PacketType.Sender.SERVER)).gamePhaseBoth().monitor().build();
/*     */ 
/* 332 */     final ListeningWhitelist clientList = ListeningWhitelist.newBuilder(serverList).types(filterTypes(type, PacketType.Sender.CLIENT)).monitor().build();
/*     */ 
/* 337 */     return new PacketListener()
/*     */     {
/*     */       public void onPacketSending(PacketEvent event) {
/* 340 */         if (CommandPacket.this.filter.filterEvent(event))
/* 341 */           printInformation(event);
/*     */       }
/*     */ 
/*     */       public void onPacketReceiving(PacketEvent event)
/*     */       {
/* 347 */         if (CommandPacket.this.filter.filterEvent(event))
/* 348 */           printInformation(event);
/*     */       }
/*     */ 
/*     */       private void printInformation(PacketEvent event)
/*     */       {
/* 353 */         String verb = event.isServerPacket() ? "Sent" : "Received";
/* 354 */         String format = event.isServerPacket() ? "%s %s to %s" : "%s %s from %s";
/*     */ 
/* 358 */         String shortDescription = String.format(format, new Object[] { event.isCancelled() ? "Cancelled" : verb, event.getPacketType(), event.getPlayer().getName() });
/*     */ 
/* 365 */         if (CommandPacket.this.extendedTypes.contains(event.getPacketType()))
/*     */           try {
/* 367 */             String original = (String)CommandPacket.this.originalPackets.remove(event);
/*     */ 
/* 370 */             if (original != null) {
/* 371 */               CommandPacket.this.logger.info("Initial packet:\n" + original + " -> ");
/*     */             }
/*     */ 
/* 374 */             CommandPacket.this.logger.info(shortDescription + ":\n" + CommandPacket.this.getPacketDescription(event.getPacket()));
/*     */           }
/*     */           catch (IllegalAccessException e)
/*     */           {
/* 378 */             CommandPacket.this.logger.log(Level.WARNING, "Unable to use reflection.", e);
/*     */           }
/*     */         else
/* 381 */           CommandPacket.this.logger.info(shortDescription + ".");
/*     */       }
/*     */ 
/*     */       public ListeningWhitelist getSendingWhitelist()
/*     */       {
/* 387 */         return serverList;
/*     */       }
/*     */ 
/*     */       public ListeningWhitelist getReceivingWhitelist()
/*     */       {
/* 392 */         return clientList;
/*     */       }
/*     */ 
/*     */       public Plugin getPlugin()
/*     */       {
/* 397 */         return CommandPacket.this.plugin;
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   public PacketListener createCompareListener(Set<PacketType> type) {
/* 403 */     final ListeningWhitelist serverList = ListeningWhitelist.newBuilder().types(filterTypes(type, PacketType.Sender.SERVER)).gamePhaseBoth().lowest().build();
/*     */ 
/* 409 */     final ListeningWhitelist clientList = ListeningWhitelist.newBuilder(serverList).types(filterTypes(type, PacketType.Sender.CLIENT)).lowest().build();
/*     */ 
/* 414 */     return new PacketListener()
/*     */     {
/*     */       public void onPacketSending(PacketEvent event) {
/* 417 */         savePacketState(event);
/*     */       }
/*     */ 
/*     */       public void onPacketReceiving(PacketEvent event)
/*     */       {
/* 422 */         savePacketState(event);
/*     */       }
/*     */ 
/*     */       private void savePacketState(PacketEvent event)
/*     */       {
/*     */         try
/*     */         {
/* 431 */           CommandPacket.this.originalPackets.put(event, CommandPacket.this.getPacketDescription(event.getPacket()));
/*     */         } catch (IllegalAccessException e) {
/* 433 */           throw new RuntimeException("Cannot read packet.", e);
/*     */         }
/*     */       }
/*     */ 
/*     */       public ListeningWhitelist getSendingWhitelist()
/*     */       {
/* 439 */         return serverList;
/*     */       }
/*     */ 
/*     */       public ListeningWhitelist getReceivingWhitelist()
/*     */       {
/* 444 */         return clientList;
/*     */       }
/*     */ 
/*     */       public Plugin getPlugin()
/*     */       {
/* 449 */         return CommandPacket.this.plugin;
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   public String getPacketDescription(PacketContainer packetContainer)
/*     */     throws IllegalAccessException
/*     */   {
/* 461 */     Object packet = packetContainer.getHandle();
/* 462 */     Class clazz = packet.getClass();
/*     */ 
/* 465 */     while ((clazz != null) && (clazz != Object.class) && ((!MinecraftReflection.isMinecraftClass(clazz)) || (Factory.class.isAssignableFrom(clazz))))
/*     */     {
/* 468 */       clazz = clazz.getSuperclass();
/*     */     }
/*     */ 
/* 471 */     return PrettyPrinter.printObject(packet, clazz, MinecraftReflection.getPacketClass(), 3, new PrettyPrinter.ObjectPrinter()
/*     */     {
/*     */       public boolean print(StringBuilder output, Object value)
/*     */       {
/* 475 */         if ((value instanceof byte[])) {
/* 476 */           byte[] data = (byte[])value;
/*     */ 
/* 478 */           if (data.length > 256) {
/* 479 */             output.append("[");
/* 480 */             HexDumper.defaultDumper().appendTo(output, data);
/* 481 */             output.append("]");
/* 482 */             return true;
/*     */           }
/* 484 */         } else if (value != null) {
/* 485 */           EquivalentConverter converter = CommandPacket.this.findConverter(value.getClass());
/*     */ 
/* 487 */           if (converter != null) {
/* 488 */             output.append(converter.getSpecific(value));
/* 489 */             return true;
/*     */           }
/*     */         }
/* 492 */         return false;
/*     */       }
/*     */     });
/*     */   }
/*     */ 
/*     */   private EquivalentConverter<Object> findConverter(Class<?> clazz)
/*     */   {
/* 503 */     Map converters = BukkitConverters.getConvertersForGeneric();
/*     */ 
/* 505 */     while (clazz != null) {
/* 506 */       EquivalentConverter result = (EquivalentConverter)converters.get(clazz);
/*     */ 
/* 508 */       if (result != null) {
/* 509 */         return result;
/*     */       }
/* 511 */       clazz = clazz.getSuperclass();
/*     */     }
/* 513 */     return null;
/*     */   }
/*     */ 
/*     */   public PacketListener updatePacketListener() {
/* 517 */     if (this.listener != null) {
/* 518 */       this.manager.removePacketListener(this.listener);
/*     */     }
/* 520 */     if (this.compareListener != null) {
/* 521 */       this.manager.removePacketListener(this.compareListener);
/*     */     }
/*     */ 
/* 525 */     this.listener = createPacketListener(this.packetTypes.values());
/* 526 */     this.compareListener = createCompareListener(this.compareTypes.values());
/* 527 */     this.manager.addPacketListener(this.listener);
/* 528 */     this.manager.addPacketListener(this.compareListener);
/* 529 */     return this.listener;
/*     */   }
/*     */ 
/*     */   private SubCommand parseCommand(Deque<String> arguments) {
/* 533 */     String text = ((String)arguments.poll()).toLowerCase();
/*     */ 
/* 536 */     if ("add".startsWith(text))
/* 537 */       return SubCommand.ADD;
/* 538 */     if ("remove".startsWith(text))
/* 539 */       return SubCommand.REMOVE;
/* 540 */     if ("names".startsWith(text))
/* 541 */       return SubCommand.NAMES;
/* 542 */     if ("page".startsWith(text)) {
/* 543 */       return SubCommand.PAGE;
/*     */     }
/* 545 */     throw new IllegalArgumentException(text + " is not a valid sub command. Must be add or remove.");
/*     */   }
/*     */ 
/*     */   private static enum SubCommand
/*     */   {
/*  67 */     ADD, REMOVE, NAMES, PAGE;
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.CommandPacket
 * JD-Core Version:    0.6.2
 */