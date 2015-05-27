/*    */ package com.zettelnet.armorweight.zet.chat;
/*    */ 
/*    */ import org.bukkit.command.CommandSender;
/*    */ 
/*    */ public final class ChatMessageInstance
/*    */ {
/*    */   private final ChatMessage type;
/*    */   private final CommandSender sender;
/*    */   private final MessageValueMap values;
/*    */   private final String message;
/*    */ 
/*    */   public ChatMessageInstance(ChatMessage type, CommandSender sender, Object[] values)
/*    */   {
/* 13 */     this(type, sender, MessageValueMap.valueOf(values));
/*    */   }
/*    */ 
/*    */   public ChatMessageInstance(ChatMessage type, CommandSender sender, MessageValueMap values) {
/* 17 */     this.type = type;
/* 18 */     this.sender = sender;
/* 19 */     this.values = values;
/* 20 */     this.message = type.format(sender, values);
/*    */   }
/*    */ 
/*    */   public String toString()
/*    */   {
/* 25 */     return this.message;
/*    */   }
/*    */ 
/*    */   public final ChatMessage getType() {
/* 29 */     return this.type;
/*    */   }
/*    */ 
/*    */   public final CommandSender getSender() {
/* 33 */     return this.sender;
/*    */   }
/*    */ 
/*    */   public final MessageValueMap getValues() {
/* 37 */     return this.values;
/*    */   }
/*    */ 
/*    */   public final String getMessage() {
/* 41 */     return this.message;
/*    */   }
/*    */ 
/*    */   public final boolean hasMessage() {
/* 45 */     return this.message != null;
/*    */   }
/*    */ 
/*    */   public void send() {
/* 49 */     if (hasMessage())
/* 50 */       this.sender.sendMessage(this.message);
/*    */   }
/*    */ }

/* Location:           D:\Github\Mechanics\ArmorWeight.jar
 * Qualified Name:     com.zettelnet.armorweight.zet.chat.ChatMessageInstance
 * JD-Core Version:    0.6.2
 */