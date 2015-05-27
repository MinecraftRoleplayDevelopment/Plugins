/*     */ package net.milkbowl.vault.economy.plugins;
/*     */ 
/*     */ import com.gravypod.Dosh.Dosh;
/*     */ import com.gravypod.Dosh.MoneyUtils;
/*     */ import com.gravypod.Dosh.Settings;
/*     */ import java.util.List;
/*     */ import net.milkbowl.vault.economy.AbstractEconomy;
/*     */ import net.milkbowl.vault.economy.EconomyResponse;
/*     */ import net.milkbowl.vault.economy.EconomyResponse.ResponseType;
/*     */ import org.bukkit.Server;
/*     */ import org.bukkit.plugin.Plugin;
/*     */ import org.bukkit.plugin.PluginManager;
/*     */ 
/*     */ public class Economy_Dosh extends AbstractEconomy
/*     */ {
/*     */   Plugin plugin;
/*     */   Dosh doshPlugin;
/*     */   DoshAPIHandler apiHandle;
/*     */ 
/*     */   public Economy_Dosh(Plugin _plugin)
/*     */   {
/*  37 */     this.plugin = _plugin;
/*     */ 
/*  39 */     if (this.plugin.getServer().getPluginManager().isPluginEnabled("Dosh")) {
/*  40 */       this.doshPlugin = ((Dosh)this.plugin.getServer().getPluginManager().getPlugin("Dosh"));
/*  41 */       this.apiHandle = new DoshAPIHandler();
/*     */     }
/*     */     else;
/*     */   }
/*     */ 
/*     */   public boolean isEnabled()
/*     */   {
/*  49 */     return this.apiHandle != null;
/*     */   }
/*     */ 
/*     */   public String getName()
/*     */   {
/*  54 */     return "Dosh";
/*     */   }
/*     */ 
/*     */   public boolean hasBankSupport()
/*     */   {
/*  59 */     return false;
/*     */   }
/*     */ 
/*     */   public int fractionalDigits()
/*     */   {
/*  64 */     return 0;
/*     */   }
/*     */ 
/*     */   public String format(double amount)
/*     */   {
/*  69 */     return null;
/*     */   }
/*     */ 
/*     */   public String currencyNamePlural()
/*     */   {
/*  74 */     return Dosh.getSettings().moneyName + "s";
/*     */   }
/*     */ 
/*     */   public String currencyNameSingular()
/*     */   {
/*  79 */     return Dosh.getSettings().moneyName;
/*     */   }
/*     */ 
/*     */   public boolean hasAccount(String playerName)
/*     */   {
/*  84 */     return true;
/*     */   }
/*     */ 
/*     */   public double getBalance(String playerName)
/*     */   {
/*  89 */     return DoshAPIHandler.getUserBal(playerName).doubleValue();
/*     */   }
/*     */ 
/*     */   public boolean has(String playerName, double amount)
/*     */   {
/*  94 */     return getBalance(playerName) - amount > 0.0D;
/*     */   }
/*     */ 
/*     */   public EconomyResponse withdrawPlayer(String playerName, double amount)
/*     */   {
/* 100 */     if (DoshAPIHandler.subtractMoney(playerName, Double.valueOf(amount))) {
/* 101 */       return new EconomyResponse(amount, getBalance(playerName), EconomyResponse.ResponseType.SUCCESS, "Worked!");
/*     */     }
/*     */ 
/* 104 */     return new EconomyResponse(amount, getBalance(playerName), EconomyResponse.ResponseType.FAILURE, "Didnt work!");
/*     */   }
/*     */ 
/*     */   public EconomyResponse depositPlayer(String playerName, double amount)
/*     */   {
/* 110 */     DoshAPIHandler.addUserBal(playerName, Double.valueOf(amount));
/* 111 */     return new EconomyResponse(amount, getBalance(playerName), EconomyResponse.ResponseType.SUCCESS, "It worked!");
/*     */   }
/*     */ 
/*     */   public EconomyResponse createBank(String name, String player)
/*     */   {
/* 116 */     return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "We do not use banks!");
/*     */   }
/*     */ 
/*     */   public EconomyResponse deleteBank(String name)
/*     */   {
/* 121 */     return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "We do not use banks!");
/*     */   }
/*     */ 
/*     */   public EconomyResponse bankBalance(String name)
/*     */   {
/* 126 */     return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "We do not use banks!");
/*     */   }
/*     */ 
/*     */   public EconomyResponse bankHas(String name, double amount)
/*     */   {
/* 131 */     return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "We do not use banks!");
/*     */   }
/*     */ 
/*     */   public EconomyResponse bankWithdraw(String name, double amount)
/*     */   {
/* 136 */     return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "We do not use banks!");
/*     */   }
/*     */ 
/*     */   public EconomyResponse bankDeposit(String name, double amount)
/*     */   {
/* 141 */     return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "We do not use banks!");
/*     */   }
/*     */ 
/*     */   public EconomyResponse isBankOwner(String name, String playerName)
/*     */   {
/* 146 */     return null;
/*     */   }
/*     */ 
/*     */   public EconomyResponse isBankMember(String name, String playerName)
/*     */   {
/* 151 */     return null;
/*     */   }
/*     */ 
/*     */   public List<String> getBanks()
/*     */   {
/* 156 */     return null;
/*     */   }
/*     */ 
/*     */   public boolean createPlayerAccount(String playerName)
/*     */   {
/* 161 */     return false;
/*     */   }
/*     */ 
/*     */   public boolean hasAccount(String playerName, String worldName)
/*     */   {
/* 168 */     return hasAccount(playerName);
/*     */   }
/*     */ 
/*     */   public double getBalance(String playerName, String world)
/*     */   {
/* 173 */     return getBalance(playerName);
/*     */   }
/*     */ 
/*     */   public boolean has(String playerName, String worldName, double amount)
/*     */   {
/* 178 */     return has(playerName, amount);
/*     */   }
/*     */ 
/*     */   public EconomyResponse withdrawPlayer(String playerName, String worldName, double amount)
/*     */   {
/* 183 */     return withdrawPlayer(playerName, amount);
/*     */   }
/*     */ 
/*     */   public EconomyResponse depositPlayer(String playerName, String worldName, double amount)
/*     */   {
/* 188 */     return depositPlayer(playerName, amount);
/*     */   }
/*     */ 
/*     */   public boolean createPlayerAccount(String playerName, String worldName)
/*     */   {
/* 193 */     return createPlayerAccount(playerName);
/*     */   }
/*     */ 
/*     */   public class DoshAPIHandler extends MoneyUtils
/*     */   {
/*     */     public DoshAPIHandler()
/*     */     {
/*     */     }
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\Vault.jar
 * Qualified Name:     net.milkbowl.vault.economy.plugins.Economy_Dosh
 * JD-Core Version:    0.6.2
 */