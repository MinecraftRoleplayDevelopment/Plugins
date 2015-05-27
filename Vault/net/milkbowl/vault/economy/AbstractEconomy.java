/*    */ package net.milkbowl.vault.economy;
/*    */ 
/*    */ import org.bukkit.OfflinePlayer;
/*    */ 
/*    */ public abstract class AbstractEconomy
/*    */   implements Economy
/*    */ {
/*    */   public boolean hasAccount(OfflinePlayer player)
/*    */   {
/* 10 */     return hasAccount(player.getName());
/*    */   }
/*    */ 
/*    */   public boolean hasAccount(OfflinePlayer player, String worldName)
/*    */   {
/* 15 */     return hasAccount(player.getName(), worldName);
/*    */   }
/*    */ 
/*    */   public double getBalance(OfflinePlayer player)
/*    */   {
/* 20 */     return getBalance(player.getName());
/*    */   }
/*    */ 
/*    */   public double getBalance(OfflinePlayer player, String world)
/*    */   {
/* 25 */     return getBalance(player.getName(), world);
/*    */   }
/*    */ 
/*    */   public boolean has(OfflinePlayer player, double amount)
/*    */   {
/* 30 */     return has(player.getName(), amount);
/*    */   }
/*    */ 
/*    */   public boolean has(OfflinePlayer player, String worldName, double amount)
/*    */   {
/* 35 */     return has(player.getName(), worldName, amount);
/*    */   }
/*    */ 
/*    */   public EconomyResponse withdrawPlayer(OfflinePlayer player, double amount)
/*    */   {
/* 40 */     return withdrawPlayer(player.getName(), amount);
/*    */   }
/*    */ 
/*    */   public EconomyResponse withdrawPlayer(OfflinePlayer player, String worldName, double amount)
/*    */   {
/* 45 */     return withdrawPlayer(player.getName(), worldName, amount);
/*    */   }
/*    */ 
/*    */   public EconomyResponse depositPlayer(OfflinePlayer player, double amount)
/*    */   {
/* 50 */     return depositPlayer(player.getName(), amount);
/*    */   }
/*    */ 
/*    */   public EconomyResponse depositPlayer(OfflinePlayer player, String worldName, double amount)
/*    */   {
/* 55 */     return depositPlayer(player.getName(), worldName, amount);
/*    */   }
/*    */ 
/*    */   public EconomyResponse createBank(String name, OfflinePlayer player)
/*    */   {
/* 60 */     return createBank(name, player.getName());
/*    */   }
/*    */ 
/*    */   public EconomyResponse isBankOwner(String name, OfflinePlayer player)
/*    */   {
/* 65 */     return isBankOwner(name, player.getName());
/*    */   }
/*    */ 
/*    */   public EconomyResponse isBankMember(String name, OfflinePlayer player)
/*    */   {
/* 70 */     return isBankMember(name, player.getName());
/*    */   }
/*    */ 
/*    */   public boolean createPlayerAccount(OfflinePlayer player)
/*    */   {
/* 75 */     return createPlayerAccount(player.getName());
/*    */   }
/*    */ 
/*    */   public boolean createPlayerAccount(OfflinePlayer player, String worldName)
/*    */   {
/* 80 */     return createPlayerAccount(player.getName(), worldName);
/*    */   }
/*    */ }

/* Location:           D:\Github\Mechanics\Vault.jar
 * Qualified Name:     net.milkbowl.vault.economy.AbstractEconomy
 * JD-Core Version:    0.6.2
 */