/*    */ package com.zettelnet.armorweight.zet.chat;
/*    */ 
/*    */ import java.util.HashMap;
/*    */ import java.util.Map;
/*    */ import org.bukkit.command.CommandSender;
/*    */ 
/*    */ public class FormatOption
/*    */   implements CharSequence
/*    */ {
/*    */   private final Map<CommandSenderType, String> values;
/*    */   private final String defaultValue;
/*    */ 
/*    */   public FormatOption(String playerValue)
/*    */   {
/* 14 */     this.values = new HashMap();
/* 15 */     this.values.put(CommandSenderType.Player, playerValue);
/*    */ 
/* 17 */     this.defaultValue = playerValue;
/*    */   }
/*    */ 
/*    */   public FormatOption(String playerValue, String consoleValue) {
/* 21 */     this(playerValue);
/* 22 */     this.values.put(CommandSenderType.Console, consoleValue);
/*    */   }
/*    */ 
/*    */   public FormatOption(String playerValue, String consoleValue, String blockValue) {
/* 26 */     this(playerValue);
/* 27 */     this.values.put(CommandSenderType.Console, consoleValue);
/* 28 */     this.values.put(CommandSenderType.Block, blockValue);
/*    */   }
/*    */ 
/*    */   public FormatOption(Map<CommandSenderType, String> values) {
/* 32 */     this.defaultValue = ((String)values.get(CommandSenderType.Player));
/* 33 */     this.values = values;
/*    */   }
/*    */ 
/*    */   public char charAt(int index)
/*    */   {
/* 38 */     return this.defaultValue.charAt(index);
/*    */   }
/*    */ 
/*    */   public int length()
/*    */   {
/* 43 */     return this.defaultValue.length();
/*    */   }
/*    */ 
/*    */   public CharSequence subSequence(int beginIndex, int endIndex)
/*    */   {
/* 48 */     return this.defaultValue.subSequence(beginIndex, endIndex);
/*    */   }
/*    */ 
/*    */   public boolean equals(Object other)
/*    */   {
/* 53 */     if (other == this) {
/* 54 */       return true;
/*    */     }
/* 56 */     if (other == null) {
/* 57 */       return false;
/*    */     }
/* 59 */     return other.toString().equals(toString());
/*    */   }
/*    */ 
/*    */   public String toString()
/*    */   {
/* 64 */     return this.defaultValue;
/*    */   }
/*    */ 
/*    */   public String toString(CommandSenderType senderType) {
/* 68 */     return this.values.containsKey(senderType) ? (String)this.values.get(senderType) : this.defaultValue;
/*    */   }
/*    */ 
/*    */   public String toString(CommandSender sender) {
/* 72 */     return toString(CommandSenderType.valueOf(sender));
/*    */   }
/*    */ }

/* Location:           D:\Github\Mechanics\ArmorWeight.jar
 * Qualified Name:     com.zettelnet.armorweight.zet.chat.FormatOption
 * JD-Core Version:    0.6.2
 */