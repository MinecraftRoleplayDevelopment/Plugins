/*     */ package com.zettelnet.armorweight.zet.chat;
/*     */ 
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import org.apache.commons.lang.text.StrSubstitutor;
/*     */ import org.bukkit.ChatColor;
/*     */ import org.bukkit.command.CommandSender;
/*     */ 
/*     */ public class FormattedChatMessage extends ChatMessage
/*     */ {
/*     */   private final String defaultMessage;
/*     */   private final MessageValueMap formatOptions;
/*     */   private final Map<CommandSenderType, String> formatted;
/*     */ 
/*     */   public FormattedChatMessage(String rawMessage, String defaultRawMessage, Object[] formatOptions)
/*     */   {
/*  19 */     this(rawMessage, defaultRawMessage, MessageValueMap.valueOf(formatOptions));
/*     */   }
/*     */ 
/*     */   public FormattedChatMessage(String rawMessage, String defaultRawMessage, MessageValueMap formatOptions) {
/*  23 */     super(rawMessage);
/*     */ 
/*  25 */     this.formatOptions = formatOptions;
/*  26 */     this.formatted = new HashMap();
/*     */ 
/*  28 */     if (isNullMessage()) {
/*  29 */       for (CommandSenderType type : CommandSenderType.values()) {
/*  30 */         this.formatted.put(type, null);
/*     */       }
/*  32 */       this.defaultMessage = null;
/*  33 */       return;
/*     */     }
/*     */ 
/*  36 */     this.defaultMessage = getFormattedMessage(defaultRawMessage, formatOptions.getMessageValues(CommandSenderType.Console));
/*     */ 
/*  38 */     Map values = formatOptions.getMessageValues();
/*     */ 
/*  40 */     for (Map.Entry entry : values.entrySet()) {
/*  41 */       CommandSenderType type = (CommandSenderType)entry.getKey();
/*  42 */       Map formats = (Map)entry.getValue();
/*     */ 
/*  44 */       this.formatted.put(type, getFormattedMessage(rawMessage, formats));
/*     */     }
/*     */   }
/*     */ 
/*     */   private String getFormattedMessage(String rawMessage, Map<String, String> formats)
/*     */   {
/*  50 */     StrSubstitutor sub = new StrSubstitutor(formats, "&(", ")", '\\');
/*  51 */     String format = sub.replace(rawMessage);
/*  52 */     format = ChatColor.translateAlternateColorCodes('&', format);
/*  53 */     format = format.replaceAll("[ ]{2,}", " ");
/*  54 */     return format;
/*     */   }
/*     */ 
/*     */   public String format(CommandSenderType senderType, MessageValueMap values)
/*     */   {
/*  59 */     if (isNullMessage()) {
/*  60 */       return null;
/*     */     }
/*     */ 
/*  67 */     StrSubstitutor sub = new StrSubstitutor(values.getMessageValues(senderType), "%(", ")", '\\');
/*  68 */     String format = sub.replace(getRawMessage(senderType));
/*     */ 
/*  70 */     if (senderType != CommandSenderType.Player) {
/*  71 */       format = ChatColor.stripColor(format);
/*     */     }
/*     */ 
/*  74 */     return format;
/*     */   }
/*     */ 
/*     */   public final String getRawMessage(CommandSenderType senderType)
/*     */   {
/*  79 */     return senderType == CommandSenderType.Console ? this.defaultMessage : (String)this.formatted.get(senderType);
/*     */   }
/*     */ 
/*     */   public final String getRawMessage(CommandSender sender) {
/*  83 */     return getRawMessage(CommandSenderType.valueOf(sender));
/*     */   }
/*     */ 
/*     */   public final String getRawMessageDefault() {
/*  87 */     return this.defaultMessage;
/*     */   }
/*     */ 
/*     */   public final MessageValueMap getFormatOptions() {
/*  91 */     return this.formatOptions;
/*     */   }
/*     */ 
/*     */   public FormattedChatMessage mergeWith(ChatMessage other)
/*     */   {
/*  96 */     String raw = getRawMessage() + "\n" + other.getRawMessage();
/*  97 */     String rawDefault = getRawMessageDefault() + "\n" + other.getRawMessage();
/*  98 */     MessageValueMap formatOptions = (MessageValueMap)getFormatOptions().clone();
/*  99 */     if ((other instanceof FormattedChatMessage)) {
/* 100 */       FormattedChatMessage otherF = (FormattedChatMessage)other;
/* 101 */       formatOptions.putAll(otherF.getFormatOptions());
/* 102 */       rawDefault = getRawMessageDefault() + "\n" + otherF.getRawMessageDefault();
/*     */     }
/* 104 */     return new FormattedChatMessage(raw, rawDefault, formatOptions);
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\ArmorWeight.jar
 * Qualified Name:     com.zettelnet.armorweight.zet.chat.FormattedChatMessage
 * JD-Core Version:    0.6.2
 */