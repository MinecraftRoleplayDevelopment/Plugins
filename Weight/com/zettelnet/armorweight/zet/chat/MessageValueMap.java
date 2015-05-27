/*    */ package com.zettelnet.armorweight.zet.chat;
/*    */ 
/*    */ import java.util.HashMap;
/*    */ import java.util.Map;
/*    */ import java.util.Map.Entry;
/*    */ import org.bukkit.command.CommandSender;
/*    */ 
/*    */ public class MessageValueMap extends HashMap<String, CharSequence>
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   private Map<CommandSenderType, Map<String, String>> messageValues;
/*    */ 
/*    */   public MessageValueMap()
/*    */   {
/* 14 */     this.messageValues = new HashMap();
/* 15 */     for (CommandSenderType type : CommandSenderType.values())
/* 16 */       this.messageValues.put(type, new HashMap());
/*    */   }
/*    */ 
/*    */   public CharSequence put(String key, CharSequence value)
/*    */   {
/* 22 */     if ((value instanceof FormatOption)) {
/* 23 */       FormatOption f = (FormatOption)value;
/* 24 */       for (CommandSenderType type : CommandSenderType.values())
/* 25 */         ((Map)this.messageValues.get(type)).put(key, f.toString(type));
/*    */     }
/*    */     else {
/* 28 */       String str = value.toString();
/* 29 */       for (CommandSenderType type : CommandSenderType.values()) {
/* 30 */         ((Map)this.messageValues.get(type)).put(key, str);
/*    */       }
/*    */     }
/* 33 */     return (CharSequence)super.put(key, value);
/*    */   }
/*    */ 
/*    */   public CharSequence remove(Object key)
/*    */   {
/* 38 */     this.messageValues.remove(key);
/* 39 */     return (CharSequence)super.remove(key);
/*    */   }
/*    */ 
/*    */   public void recalculate() {
/* 43 */     this.messageValues = new HashMap();
/* 44 */     for (CommandSenderType type : CommandSenderType.values()) {
/* 45 */       this.messageValues.put(type, new HashMap());
/*    */     }
/*    */ 
/* 48 */     for (Map.Entry entry : entrySet()) {
/* 49 */       String key = (String)entry.getKey();
/* 50 */       CharSequence charSequence = (CharSequence)entry.getValue();
/*    */ 
/* 52 */       if ((charSequence instanceof FormatOption)) {
/* 53 */         FormatOption f = (FormatOption)charSequence;
/* 54 */         for (CommandSenderType type : CommandSenderType.values())
/* 55 */           ((Map)this.messageValues.get(type)).put(key, f.toString(type));
/*    */       }
/*    */       else {
/* 58 */         String value = charSequence.toString();
/* 59 */         for (CommandSenderType type : CommandSenderType.values())
/* 60 */           ((Map)this.messageValues.get(type)).put(key, value);
/*    */       }
/*    */     }
/*    */   }
/*    */ 
/*    */   public Map<CommandSenderType, Map<String, String>> getMessageValues()
/*    */   {
/* 67 */     return this.messageValues;
/*    */   }
/*    */ 
/*    */   public Map<String, String> getMessageValues(CommandSenderType senderType) {
/* 71 */     return (Map)this.messageValues.get(senderType);
/*    */   }
/*    */ 
/*    */   public Map<String, String> getMessageValues(CommandSender sender) {
/* 75 */     return getMessageValues(CommandSenderType.valueOf(sender));
/*    */   }
/*    */ 
/*    */   public static MessageValueMap valueOf(Map<String, String> formatOptions) {
/* 79 */     MessageValueMap map = new MessageValueMap();
/* 80 */     for (Map.Entry entry : formatOptions.entrySet()) {
/* 81 */       map.put((String)entry.getKey(), (CharSequence)entry.getValue());
/*    */     }
/* 83 */     return map;
/*    */   }
/*    */ 
/*    */   public static MessageValueMap valueOf(Object[] values) {
/* 87 */     if (values.length % 2 != 0) {
/* 88 */       throw new IllegalArgumentException("Value array has to have an even number of elements.");
/*    */     }
/*    */ 
/* 91 */     Map map = new HashMap();
/*    */ 
/* 93 */     for (int i = 0; i < values.length; i += 2) {
/* 94 */       map.put(values[i].toString(), values[(i + 1)].toString());
/*    */     }
/*    */ 
/* 97 */     return valueOf(map);
/*    */   }
/*    */ }

/* Location:           D:\Github\Mechanics\ArmorWeight.jar
 * Qualified Name:     com.zettelnet.armorweight.zet.chat.MessageValueMap
 * JD-Core Version:    0.6.2
 */