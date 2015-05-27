/*     */ package net.milkbowl.vault.economy.plugins;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.logging.Logger;
/*     */ import me.ashtheking.currency.Currency;
/*     */ import me.ashtheking.currency.CurrencyList;
/*     */ import net.milkbowl.vault.economy.AbstractEconomy;
/*     */ import net.milkbowl.vault.economy.EconomyResponse;
/*     */ import net.milkbowl.vault.economy.EconomyResponse.ResponseType;
/*     */ import org.bukkit.Bukkit;
/*     */ import org.bukkit.Server;
/*     */ import org.bukkit.event.EventHandler;
/*     */ import org.bukkit.event.EventPriority;
/*     */ import org.bukkit.event.Listener;
/*     */ import org.bukkit.event.server.PluginDisableEvent;
/*     */ import org.bukkit.event.server.PluginEnableEvent;
/*     */ import org.bukkit.plugin.Plugin;
/*     */ import org.bukkit.plugin.PluginDescriptionFile;
/*     */ import org.bukkit.plugin.PluginManager;
/*     */ 
/*     */ public class Economy_MultiCurrency extends AbstractEconomy
/*     */ {
/*  37 */   private static final Logger log = Logger.getLogger("Minecraft");
/*     */ 
/*  39 */   private final String name = "MultiCurrency";
/*  40 */   private Plugin plugin = null;
/*  41 */   private Currency economy = null;
/*     */ 
/*     */   public Economy_MultiCurrency(Plugin plugin) {
/*  44 */     this.plugin = plugin;
/*  45 */     Bukkit.getServer().getPluginManager().registerEvents(new EconomyServerListener(this), plugin);
/*     */ 
/*  48 */     if (this.economy == null) {
/*  49 */       Plugin multiCurrency = plugin.getServer().getPluginManager().getPlugin("MultiCurrency");
/*  50 */       if ((multiCurrency != null) && (multiCurrency.isEnabled())) {
/*  51 */         this.economy = ((Currency)multiCurrency);
/*  52 */         log.info(String.format("[%s][Economy] %s hooked.", new Object[] { plugin.getDescription().getName(), "MultiCurrency" }));
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public String getName()
/*     */   {
/*  59 */     return "MultiCurrency";
/*     */   }
/*     */ 
/*     */   public boolean isEnabled()
/*     */   {
/*  64 */     if (this.economy == null) {
/*  65 */       return false;
/*     */     }
/*  67 */     return this.economy.isEnabled();
/*     */   }
/*     */ 
/*     */   public double getBalance(String playerName)
/*     */   {
/*  75 */     double balance = CurrencyList.getValue((String)CurrencyList.maxCurrency(playerName)[0], playerName);
/*     */ 
/*  77 */     double fBalance = balance;
/*  78 */     return fBalance;
/*     */   }
/*     */ 
/*     */   public EconomyResponse withdrawPlayer(String playerName, double amount)
/*     */   {
/*  85 */     String errorMessage = null;
/*     */ 
/*  87 */     if (amount < 0.0D) {
/*  88 */       errorMessage = "Cannot withdraw negative funds";
/*  89 */       EconomyResponse.ResponseType type = EconomyResponse.ResponseType.FAILURE;
/*  90 */       amount = 0.0D;
/*  91 */       double balance = CurrencyList.getValue((String)CurrencyList.maxCurrency(playerName)[0], playerName);
/*     */ 
/*  93 */       return new EconomyResponse(amount, balance, type, errorMessage);
/*     */     }
/*     */ 
/*  96 */     if (!CurrencyList.hasEnough(playerName, amount)) {
/*  97 */       errorMessage = "Insufficient funds";
/*  98 */       EconomyResponse.ResponseType type = EconomyResponse.ResponseType.FAILURE;
/*  99 */       amount = 0.0D;
/* 100 */       double balance = CurrencyList.getValue((String)CurrencyList.maxCurrency(playerName)[0], playerName);
/*     */ 
/* 102 */       return new EconomyResponse(amount, balance, type, errorMessage);
/*     */     }
/*     */ 
/* 105 */     if (CurrencyList.subtract(playerName, amount)) {
/* 106 */       EconomyResponse.ResponseType type = EconomyResponse.ResponseType.SUCCESS;
/* 107 */       double balance = CurrencyList.getValue((String)CurrencyList.maxCurrency(playerName)[0], playerName);
/*     */ 
/* 109 */       return new EconomyResponse(amount, balance, type, errorMessage);
/*     */     }
/* 111 */     errorMessage = "Error withdrawing funds";
/* 112 */     EconomyResponse.ResponseType type = EconomyResponse.ResponseType.FAILURE;
/* 113 */     amount = 0.0D;
/* 114 */     double balance = CurrencyList.getValue((String)CurrencyList.maxCurrency(playerName)[0], playerName);
/*     */ 
/* 116 */     return new EconomyResponse(amount, balance, type, errorMessage);
/*     */   }
/*     */ 
/*     */   public EconomyResponse depositPlayer(String playerName, double amount)
/*     */   {
/* 124 */     String errorMessage = null;
/*     */ 
/* 126 */     if (amount < 0.0D) {
/* 127 */       errorMessage = "Cannot deposit negative funds";
/* 128 */       EconomyResponse.ResponseType type = EconomyResponse.ResponseType.FAILURE;
/* 129 */       amount = 0.0D;
/* 130 */       double balance = CurrencyList.getValue((String)CurrencyList.maxCurrency(playerName)[0], playerName);
/*     */ 
/* 132 */       return new EconomyResponse(amount, balance, type, errorMessage);
/*     */     }
/*     */ 
/* 135 */     if (CurrencyList.add(playerName, amount)) {
/* 136 */       EconomyResponse.ResponseType type = EconomyResponse.ResponseType.SUCCESS;
/* 137 */       double balance = CurrencyList.getValue((String)CurrencyList.maxCurrency(playerName)[0], playerName);
/*     */ 
/* 139 */       return new EconomyResponse(amount, balance, type, errorMessage);
/*     */     }
/* 141 */     errorMessage = "Error withdrawing funds";
/* 142 */     EconomyResponse.ResponseType type = EconomyResponse.ResponseType.FAILURE;
/* 143 */     amount = 0.0D;
/* 144 */     double balance = CurrencyList.getValue((String)CurrencyList.maxCurrency(playerName)[0], playerName);
/*     */ 
/* 146 */     return new EconomyResponse(amount, balance, type, errorMessage);
/*     */   }
/*     */ 
/*     */   public String format(double amount)
/*     */   {
/* 182 */     return String.format("%.2f %s", new Object[] { Double.valueOf(amount), "currency" });
/*     */   }
/*     */ 
/*     */   public String currencyNameSingular()
/*     */   {
/* 187 */     return "currency";
/*     */   }
/*     */ 
/*     */   public String currencyNamePlural()
/*     */   {
/* 192 */     return "currency";
/*     */   }
/*     */ 
/*     */   public boolean has(String playerName, double amount)
/*     */   {
/* 197 */     return getBalance(playerName) >= amount;
/*     */   }
/*     */ 
/*     */   public EconomyResponse createBank(String name, String player)
/*     */   {
/* 202 */     return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "MultiCurrency does not support bank accounts");
/*     */   }
/*     */ 
/*     */   public EconomyResponse deleteBank(String name)
/*     */   {
/* 207 */     return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "MultiCurrency does not support bank accounts!");
/*     */   }
/*     */ 
/*     */   public EconomyResponse bankHas(String name, double amount)
/*     */   {
/* 212 */     return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "MultiCurrency does not support bank accounts");
/*     */   }
/*     */ 
/*     */   public EconomyResponse bankWithdraw(String name, double amount)
/*     */   {
/* 217 */     return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "MultiCurrency does not support bank accounts");
/*     */   }
/*     */ 
/*     */   public EconomyResponse bankDeposit(String name, double amount)
/*     */   {
/* 222 */     return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "MultiCurrency does not support bank accounts");
/*     */   }
/*     */ 
/*     */   public EconomyResponse isBankOwner(String name, String playerName)
/*     */   {
/* 227 */     return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "MultiCurrency does not support bank accounts");
/*     */   }
/*     */ 
/*     */   public EconomyResponse isBankMember(String name, String playerName)
/*     */   {
/* 232 */     return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "MultiCurrency does not support bank accounts");
/*     */   }
/*     */ 
/*     */   public EconomyResponse bankBalance(String name)
/*     */   {
/* 237 */     return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "MultiCurrency does not support bank accounts");
/*     */   }
/*     */ 
/*     */   public List<String> getBanks()
/*     */   {
/* 242 */     return new ArrayList();
/*     */   }
/*     */ 
/*     */   public boolean hasBankSupport()
/*     */   {
/* 247 */     return false;
/*     */   }
/*     */ 
/*     */   public boolean hasAccount(String playerName)
/*     */   {
/* 252 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean createPlayerAccount(String playerName)
/*     */   {
/* 257 */     return false;
/*     */   }
/*     */ 
/*     */   public int fractionalDigits()
/*     */   {
/* 262 */     return -1;
/*     */   }
/*     */ 
/*     */   public boolean hasAccount(String playerName, String worldName)
/*     */   {
/* 267 */     return hasAccount(playerName);
/*     */   }
/*     */ 
/*     */   public double getBalance(String playerName, String world)
/*     */   {
/* 272 */     return getBalance(playerName);
/*     */   }
/*     */ 
/*     */   public boolean has(String playerName, String worldName, double amount)
/*     */   {
/* 277 */     return has(playerName, amount);
/*     */   }
/*     */ 
/*     */   public EconomyResponse withdrawPlayer(String playerName, String worldName, double amount)
/*     */   {
/* 282 */     return withdrawPlayer(playerName, amount);
/*     */   }
/*     */ 
/*     */   public EconomyResponse depositPlayer(String playerName, String worldName, double amount)
/*     */   {
/* 287 */     return depositPlayer(playerName, amount);
/*     */   }
/*     */ 
/*     */   public boolean createPlayerAccount(String playerName, String worldName)
/*     */   {
/* 292 */     return createPlayerAccount(playerName);
/*     */   }
/*     */ 
/*     */   public class EconomyServerListener
/*     */     implements Listener
/*     */   {
/* 151 */     Economy_MultiCurrency economy = null;
/*     */ 
/*     */     public EconomyServerListener(Economy_MultiCurrency economy) {
/* 154 */       this.economy = economy;
/*     */     }
/*     */ 
/*     */     @EventHandler(priority=EventPriority.MONITOR)
/*     */     public void onPluginEnable(PluginEnableEvent event) {
/* 159 */       if (this.economy.economy == null) {
/* 160 */         Plugin mcur = event.getPlugin();
/*     */ 
/* 162 */         if (mcur.getDescription().getName().equals("MultiCurrency")) {
/* 163 */           this.economy.economy = ((Currency)mcur);
/* 164 */           Economy_MultiCurrency.log.info(String.format("[%s][Economy] %s hooked.", new Object[] { Economy_MultiCurrency.this.plugin.getDescription().getName(), "MultiCurrency" }));
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/*     */     @EventHandler(priority=EventPriority.MONITOR)
/*     */     public void onPluginDisable(PluginDisableEvent event) {
/* 171 */       if ((this.economy.economy != null) && 
/* 172 */         (event.getPlugin().getDescription().getName().equals("MultiCurrency"))) {
/* 173 */         this.economy.economy = null;
/* 174 */         Economy_MultiCurrency.log.info(String.format("[%s][Economy] %s unhooked.", new Object[] { Economy_MultiCurrency.this.plugin.getDescription().getName(), "MultiCurrency" }));
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\Vault.jar
 * Qualified Name:     net.milkbowl.vault.economy.plugins.Economy_MultiCurrency
 * JD-Core Version:    0.6.2
 */