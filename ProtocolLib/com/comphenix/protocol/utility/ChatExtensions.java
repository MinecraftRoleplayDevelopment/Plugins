/*     */ package com.comphenix.protocol.utility;
/*     */ 
/*     */ import com.comphenix.protocol.PacketType.Play.Server;
/*     */ import com.comphenix.protocol.ProtocolManager;
/*     */ import com.comphenix.protocol.events.PacketContainer;
/*     */ import com.comphenix.protocol.injector.PacketConstructor;
/*     */ import com.comphenix.protocol.injector.packet.PacketRegistry;
/*     */ import com.comphenix.protocol.reflect.FieldAccessException;
/*     */ import com.comphenix.protocol.reflect.FuzzyReflection;
/*     */ import com.comphenix.protocol.reflect.accessors.Accessors;
/*     */ import com.comphenix.protocol.reflect.accessors.MethodAccessor;
/*     */ import com.comphenix.protocol.reflect.fuzzy.FuzzyMatchers;
/*     */ import com.comphenix.protocol.reflect.fuzzy.FuzzyMethodContract;
/*     */ import com.comphenix.protocol.reflect.fuzzy.FuzzyMethodContract.Builder;
/*     */ import com.google.common.base.Strings;
/*     */ import com.google.common.collect.Iterables;
/*     */ import java.lang.reflect.Constructor;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.util.List;
/*     */ import org.bukkit.Bukkit;
/*     */ import org.bukkit.Server;
/*     */ import org.bukkit.command.CommandSender;
/*     */ import org.bukkit.entity.Player;
/*     */ 
/*     */ public class ChatExtensions
/*     */ {
/*     */   private ProtocolManager manager;
/*     */   private static volatile PacketConstructor chatConstructor;
/*  56 */   private static volatile Constructor<?> jsonConstructor = getJsonFormatConstructor();
/*     */   private static volatile MethodAccessor messageFactory;
/*     */ 
/*     */   public ChatExtensions(ProtocolManager manager)
/*     */   {
/*  60 */     this.manager = manager;
/*     */   }
/*     */ 
/*     */   public void sendMessageSilently(CommandSender receiver, String message)
/*     */     throws InvocationTargetException
/*     */   {
/*  70 */     if (receiver == null)
/*  71 */       throw new IllegalArgumentException("receiver cannot be NULL.");
/*  72 */     if (message == null) {
/*  73 */       throw new IllegalArgumentException("message cannot be NULL.");
/*     */     }
/*     */ 
/*  76 */     if ((receiver instanceof Player))
/*  77 */       sendMessageSilently((Player)receiver, message);
/*     */     else
/*  79 */       receiver.sendMessage(message);
/*     */   }
/*     */ 
/*     */   private void sendMessageSilently(Player player, String message)
/*     */     throws InvocationTargetException
/*     */   {
/*     */     try
/*     */     {
/*  91 */       for (PacketContainer packet : createChatPackets(message))
/*  92 */         this.manager.sendServerPacket(player, packet, false);
/*     */     }
/*     */     catch (FieldAccessException e) {
/*  95 */       throw new InvocationTargetException(e);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static PacketContainer[] createChatPackets(String message)
/*     */   {
/* 105 */     if (jsonConstructor != null) {
/* 106 */       if (chatConstructor == null) {
/* 107 */         Class messageClass = jsonConstructor.getParameterTypes()[0];
/* 108 */         chatConstructor = PacketConstructor.DEFAULT.withPacket(PacketType.Play.Server.CHAT, new Object[] { messageClass });
/*     */ 
/* 111 */         if (MinecraftReflection.isUsingNetty()) {
/* 112 */           messageFactory = Accessors.getMethodAccessor(MinecraftReflection.getCraftMessageClass(), "fromString", new Class[] { String.class });
/*     */         }
/*     */         else {
/* 115 */           messageFactory = Accessors.getMethodAccessor(FuzzyReflection.fromClass(messageClass).getMethod(FuzzyMethodContract.newBuilder().requireModifier(8).parameterCount(1).parameterExactType(String.class).returnTypeMatches(FuzzyMatchers.matchParent()).build()));
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 128 */       if (MinecraftReflection.isUsingNetty()) {
/* 129 */         Object[] components = (Object[])messageFactory.invoke(null, new Object[] { message });
/* 130 */         PacketContainer[] packets = new PacketContainer[components.length];
/*     */ 
/* 132 */         for (int i = 0; i < components.length; i++) {
/* 133 */           packets[i] = chatConstructor.createPacket(new Object[] { components[i] });
/*     */         }
/* 135 */         return packets;
/*     */       }
/*     */ 
/* 139 */       return new PacketContainer[] { chatConstructor.createPacket(new Object[] { messageFactory.invoke(null, new Object[] { message }) }) };
/*     */     }
/*     */ 
/* 143 */     if (chatConstructor == null) {
/* 144 */       chatConstructor = PacketConstructor.DEFAULT.withPacket(PacketType.Play.Server.CHAT, new Object[] { message });
/*     */     }
/*     */ 
/* 147 */     return new PacketContainer[] { chatConstructor.createPacket(new Object[] { message }) };
/*     */   }
/*     */ 
/*     */   public void broadcastMessageSilently(String message, String permission)
/*     */     throws InvocationTargetException
/*     */   {
/* 158 */     if (message == null) {
/* 159 */       throw new IllegalArgumentException("message cannot be NULL.");
/*     */     }
/*     */ 
/* 162 */     for (Player player : Bukkit.getServer().getOnlinePlayers())
/* 163 */       if ((permission == null) || (player.hasPermission(permission)))
/* 164 */         sendMessageSilently(player, message);
/*     */   }
/*     */ 
/*     */   public static String[] toFlowerBox(String[] message, String marginChar, int marginWidth, int marginHeight)
/*     */   {
/* 177 */     String[] output = new String[message.length + marginHeight * 2];
/* 178 */     int width = getMaximumLength(message);
/*     */ 
/* 181 */     String topButtomMargin = Strings.repeat(marginChar, width + marginWidth * 2);
/* 182 */     String leftRightMargin = Strings.repeat(marginChar, marginWidth);
/*     */ 
/* 185 */     for (int i = 0; i < message.length; i++) {
/* 186 */       output[(i + marginHeight)] = (leftRightMargin + Strings.padEnd(message[i], width, ' ') + leftRightMargin);
/*     */     }
/*     */ 
/* 190 */     for (int i = 0; i < marginHeight; i++) {
/* 191 */       output[i] = topButtomMargin;
/* 192 */       output[(output.length - i - 1)] = topButtomMargin;
/*     */     }
/* 194 */     return output;
/*     */   }
/*     */ 
/*     */   private static int getMaximumLength(String[] lines)
/*     */   {
/* 203 */     int current = 0;
/*     */ 
/* 206 */     for (int i = 0; i < lines.length; i++) {
/* 207 */       if (current < lines[i].length())
/* 208 */         current = lines[i].length();
/*     */     }
/* 210 */     return current;
/*     */   }
/*     */ 
/*     */   static Constructor<?> getJsonFormatConstructor()
/*     */   {
/* 218 */     Class chatPacket = PacketRegistry.getPacketClassFromType(PacketType.Play.Server.CHAT, true);
/* 219 */     List list = FuzzyReflection.fromClass(chatPacket).getConstructorList(FuzzyMethodContract.newBuilder().parameterCount(1).parameterMatches(MinecraftReflection.getMinecraftObjectMatcher()).build());
/*     */ 
/* 227 */     return (Constructor)Iterables.getFirst(list, null);
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.utility.ChatExtensions
 * JD-Core Version:    0.6.2
 */