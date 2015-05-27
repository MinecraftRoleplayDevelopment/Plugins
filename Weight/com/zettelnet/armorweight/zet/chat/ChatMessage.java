/*     */ package com.zettelnet.armorweight.zet.chat;
/*     */ 
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import org.apache.commons.lang.text.StrSubstitutor;
/*     */ import org.bukkit.ChatColor;
/*     */ import org.bukkit.command.CommandSender;
/*     */ 
/*     */ public class ChatMessage
/*     */ {
/*  13 */   public static final ChatMessage NONE = new ChatMessage(null);
/*     */   private final String rawMessage;
/*     */   private final boolean nullMessage;
/*     */ 
/*     */   public ChatMessage(String rawMessage)
/*     */   {
/*  19 */     this.rawMessage = escapeCharacters(rawMessage);
/*  20 */     this.nullMessage = checkNullMessage();
/*     */   }
/*     */ 
/*     */   protected boolean checkNullMessage() {
/*  24 */     return (this.rawMessage == null) || (this.rawMessage.equalsIgnoreCase("null")) || (this.rawMessage.equalsIgnoreCase("none"));
/*     */   }
/*     */ 
/*     */   public String format(CommandSenderType senderType, MessageValueMap values) {
/*  28 */     if (this.nullMessage) {
/*  29 */       return null;
/*     */     }
/*     */ 
/*  34 */     StrSubstitutor sub = new StrSubstitutor(values.getMessageValues(senderType), "%(", ")", '\\');
/*  35 */     String format = sub.replace(this.rawMessage);
/*     */ 
/*  37 */     if (senderType != CommandSenderType.Player) {
/*  38 */       format = ChatColor.stripColor(format);
/*     */     }
/*     */ 
/*  41 */     return format;
/*     */   }
/*     */ 
/*     */   public String format(CommandSenderType senderType, Object[] values) {
/*  45 */     return format(senderType, MessageValueMap.valueOf(values));
/*     */   }
/*     */ 
/*     */   public String format(CommandSender sender, MessageValueMap values) {
/*  49 */     return format(CommandSenderType.valueOf(sender), values);
/*     */   }
/*     */ 
/*     */   public String format(CommandSender sender, Object[] values) {
/*  53 */     return format(sender, MessageValueMap.valueOf(values));
/*     */   }
/*     */ 
/*     */   public final String getRawMessage() {
/*  57 */     return this.rawMessage;
/*     */   }
/*     */ 
/*     */   public final boolean isNullMessage() {
/*  61 */     return this.nullMessage;
/*     */   }
/*     */ 
/*     */   public String getMessage(CommandSender sender, MessageValueMap values) {
/*  65 */     return instance(sender, values).getMessage();
/*     */   }
/*     */ 
/*     */   public String getMessage(CommandSender sender, Object[] values) {
/*  69 */     return getMessage(sender, MessageValueMap.valueOf(values));
/*     */   }
/*     */ 
/*     */   public ChatMessageInstance instance(CommandSender sender, MessageValueMap values) {
/*  73 */     return new ChatMessageInstance(this, sender, values);
/*     */   }
/*     */ 
/*     */   public ChatMessageInstance instance(CommandSender sender, Object[] values) {
/*  77 */     return instance(sender, MessageValueMap.valueOf(values));
/*     */   }
/*     */ 
/*     */   public void send(CommandSender sender, MessageValueMap values) {
/*  81 */     new ChatMessageInstance(this, sender, values).send();
/*     */   }
/*     */ 
/*     */   public void send(CommandSender sender, Object[] values) {
/*  85 */     send(sender, MessageValueMap.valueOf(values));
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/*  90 */     return this.rawMessage;
/*     */   }
/*     */ 
/*     */   public FormatOption toFormatOption(Object[] values) {
/*  94 */     return toFormatOption(MessageValueMap.valueOf(values));
/*     */   }
/*     */ 
/*     */   public FormatOption toFormatOption(MessageValueMap values) {
/*  98 */     Map formatTypes = new HashMap();
/*  99 */     for (Map.Entry entry : values.getMessageValues().entrySet()) {
/* 100 */       CommandSenderType senderType = (CommandSenderType)entry.getKey();
/* 101 */       formatTypes.put(senderType, format(senderType, values));
/*     */     }
/* 103 */     return new FormatOption(formatTypes);
/*     */   }
/*     */ 
/*     */   public ChatMessage mergeWith(ChatMessage other) {
/* 107 */     return new ChatMessage(getRawMessage() + "\n" + other.getRawMessage());
/*     */   }
/*     */ 
/*     */   private String escapeCharacters(String str) {
/* 111 */     if (str == null) {
/* 112 */       return null;
/*     */     }
/* 114 */     char[] cArray = str.toCharArray();
/* 115 */     for (int i = 0; i < cArray.length; i++)
/*     */     {
/* 117 */       char c = cArray[i];
/* 118 */       if ((c >= 'А') && (c < 'Ϭ'))
/*     */       {
/*     */         int tmp42_41 = i;
/*     */         char[] tmp42_40 = cArray; tmp42_40[tmp42_41] = ((char)(tmp42_40[tmp42_41] - '͐'));
/* 120 */       } else if (c == 'ё') {
/* 121 */         cArray[i] = '¸';
/*     */       }
/*     */     }
/* 124 */     return new String(cArray);
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\ArmorWeight.jar
 * Qualified Name:     com.zettelnet.armorweight.zet.chat.ChatMessage
 * JD-Core Version:    0.6.2
 */