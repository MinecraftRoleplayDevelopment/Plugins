/*    */ package com.zettelnet.armorweight.zet.chat;
/*    */ 
/*    */ import org.bukkit.command.BlockCommandSender;
/*    */ import org.bukkit.command.CommandSender;
/*    */ import org.bukkit.command.ConsoleCommandSender;
/*    */ import org.bukkit.entity.Player;
/*    */ 
/*    */ public enum CommandSenderType
/*    */ {
/* 10 */   Player, Console, Block;
/*    */ 
/*    */   public static CommandSenderType valueOf(CommandSender sender) {
/* 13 */     if ((sender instanceof Player)) {
/* 14 */       return Player;
/*    */     }
/* 16 */     if ((sender instanceof ConsoleCommandSender)) {
/* 17 */       return Console;
/*    */     }
/* 19 */     if ((sender instanceof BlockCommandSender)) {
/* 20 */       return Block;
/*    */     }
/* 22 */     return null;
/*    */   }
/*    */ }

/* Location:           D:\Github\Mechanics\ArmorWeight.jar
 * Qualified Name:     com.zettelnet.armorweight.zet.chat.CommandSenderType
 * JD-Core Version:    0.6.2
 */