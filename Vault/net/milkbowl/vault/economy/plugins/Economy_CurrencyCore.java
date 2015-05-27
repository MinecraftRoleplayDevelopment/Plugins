/*     */ package net.milkbowl.vault.economy.plugins;
/*     */ 
/*     */ import is.currency.Currency;
/*     */ import is.currency.config.CurrencyConfiguration;
/*     */ import is.currency.syst.AccountContext;
/*     */ import is.currency.syst.AccountManager;
/*     */ import is.currency.syst.FormatHelper;
/*     */ import java.util.List;
/*     */ import java.util.logging.Logger;
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
/*     */ public class Economy_CurrencyCore extends AbstractEconomy
/*     */ {
/*     */   private Currency currency;
/*  39 */   private static final Logger log = Logger.getLogger("Minecraft");
/*     */   private final Plugin plugin;
/*  41 */   private final String name = "CurrencyCore";
/*     */ 
/*     */   public Economy_CurrencyCore(Plugin plugin) {
/*  44 */     this.plugin = plugin;
/*  45 */     Bukkit.getServer().getPluginManager().registerEvents(new EconomyServerListener(this), plugin);
/*     */ 
/*  48 */     if (this.currency == null) {
/*  49 */       Plugin currencyPlugin = plugin.getServer().getPluginManager().getPlugin("CurrencyCore");
/*  50 */       if ((currencyPlugin != null) && (currencyPlugin.getClass().getName().equals("is.currency.Currency"))) {
/*  51 */         this.currency = ((Currency)currencyPlugin);
/*  52 */         log.info(String.format("[%s][Economy] %s hooked.", new Object[] { plugin.getDescription().getName(), "CurrencyCore" }));
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean isEnabled()
/*     */   {
/*  90 */     return this.currency != null;
/*     */   }
/*     */ 
/*     */   public String getName()
/*     */   {
/*  95 */     return "CurrencyCore";
/*     */   }
/*     */ 
/*     */   public String format(double amount)
/*     */   {
/* 100 */     return this.currency.getFormatHelper().format(amount);
/*     */   }
/*     */ 
/*     */   public String currencyNamePlural()
/*     */   {
/* 105 */     return (String)this.currency.getCurrencyConfig().getCurrencyMajor().get(1);
/*     */   }
/*     */ 
/*     */   public String currencyNameSingular()
/*     */   {
/* 110 */     return (String)this.currency.getCurrencyConfig().getCurrencyMajor().get(0);
/*     */   }
/*     */ 
/*     */   public double getBalance(String playerName)
/*     */   {
/* 115 */     AccountContext account = this.currency.getAccountManager().getAccount(playerName);
/* 116 */     if (account == null) {
/* 117 */       return 0.0D;
/*     */     }
/*     */ 
/* 120 */     return account.getBalance();
/*     */   }
/*     */ 
/*     */   public boolean has(String playerName, double amount)
/*     */   {
/* 125 */     AccountContext account = this.currency.getAccountManager().getAccount(playerName);
/* 126 */     if (account == null) {
/* 127 */       return false;
/*     */     }
/* 129 */     return account.hasBalance(amount);
/*     */   }
/*     */ 
/*     */   public EconomyResponse withdrawPlayer(String playerName, double amount)
/*     */   {
/* 135 */     if (amount < 0.0D) {
/* 136 */       return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.FAILURE, "Cannot withdraw negative funds");
/*     */     }
/*     */ 
/* 139 */     AccountContext account = this.currency.getAccountManager().getAccount(playerName);
/* 140 */     if (account == null)
/* 141 */       return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.FAILURE, "That account does not exist");
/* 142 */     if (!account.hasBalance(amount)) {
/* 143 */       return new EconomyResponse(0.0D, account.getBalance(), EconomyResponse.ResponseType.FAILURE, "Insufficient funds");
/*     */     }
/* 145 */     account.subtractBalance(amount);
/* 146 */     return new EconomyResponse(amount, account.getBalance(), EconomyResponse.ResponseType.SUCCESS, "");
/*     */   }
/*     */ 
/*     */   public EconomyResponse depositPlayer(String playerName, double amount)
/*     */   {
/* 152 */     if (amount < 0.0D) {
/* 153 */       return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.FAILURE, "Cannot desposit negative funds");
/*     */     }
/*     */ 
/* 156 */     AccountContext account = this.currency.getAccountManager().getAccount(playerName);
/* 157 */     if (account == null) {
/* 158 */       return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.FAILURE, "That account does not exist");
/*     */     }
/* 160 */     account.addBalance(amount);
/* 161 */     return new EconomyResponse(amount, account.getBalance(), EconomyResponse.ResponseType.SUCCESS, "");
/*     */   }
/*     */ 
/*     */   public EconomyResponse createBank(String name, String player)
/*     */   {
/* 166 */     if (this.currency.getAccountManager().hasAccount(name)) {
/* 167 */       return new EconomyResponse(0.0D, this.currency.getAccountManager().getAccount(name).getBalance(), EconomyResponse.ResponseType.FAILURE, "That account already exists.");
/*     */     }
/* 169 */     this.currency.getAccountManager().createAccount(name);
/* 170 */     return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.SUCCESS, "");
/*     */   }
/*     */ 
/*     */   public EconomyResponse deleteBank(String name)
/*     */   {
/* 175 */     if (this.currency.getAccountManager().hasAccount(name)) {
/* 176 */       this.currency.getAccountManager().deleteAccount(name);
/* 177 */       return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.SUCCESS, "");
/*     */     }
/* 179 */     return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.FAILURE, "That account does not exist!");
/*     */   }
/*     */ 
/*     */   public EconomyResponse bankBalance(String name)
/*     */   {
/* 184 */     AccountContext account = this.currency.getAccountManager().getAccount(name);
/*     */ 
/* 186 */     if (account == null) {
/* 187 */       return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.FAILURE, "That account does not exists.");
/*     */     }
/* 189 */     return new EconomyResponse(0.0D, account.getBalance(), EconomyResponse.ResponseType.SUCCESS, "");
/*     */   }
/*     */ 
/*     */   public EconomyResponse bankHas(String name, double amount)
/*     */   {
/* 194 */     AccountContext account = this.currency.getAccountManager().getAccount(name);
/* 195 */     if (account == null)
/* 196 */       return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.FAILURE, "That account does not exist!");
/* 197 */     if (!account.hasBalance(amount)) {
/* 198 */       return new EconomyResponse(0.0D, account.getBalance(), EconomyResponse.ResponseType.FAILURE, "That account does not have enough!");
/*     */     }
/* 200 */     return new EconomyResponse(0.0D, account.getBalance(), EconomyResponse.ResponseType.SUCCESS, "");
/*     */   }
/*     */ 
/*     */   public EconomyResponse bankWithdraw(String name, double amount)
/*     */   {
/* 206 */     if (amount < 0.0D) {
/* 207 */       return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.FAILURE, "Cannot withdraw negative funds");
/*     */     }
/*     */ 
/* 210 */     AccountContext account = this.currency.getAccountManager().getAccount(name);
/* 211 */     if (account == null)
/* 212 */       return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.FAILURE, "That account does not exist!");
/* 213 */     if (!account.hasBalance(amount)) {
/* 214 */       return new EconomyResponse(0.0D, account.getBalance(), EconomyResponse.ResponseType.FAILURE, "That account does not have enough!");
/*     */     }
/* 216 */     account.subtractBalance(amount);
/* 217 */     return new EconomyResponse(amount, account.getBalance(), EconomyResponse.ResponseType.SUCCESS, "");
/*     */   }
/*     */ 
/*     */   public EconomyResponse bankDeposit(String name, double amount)
/*     */   {
/* 223 */     if (amount < 0.0D) {
/* 224 */       return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.FAILURE, "Cannot desposit negative funds");
/*     */     }
/*     */ 
/* 227 */     AccountContext account = this.currency.getAccountManager().getAccount(name);
/* 228 */     if (account == null) {
/* 229 */       return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.FAILURE, "That account does not exist!");
/*     */     }
/* 231 */     account.addBalance(amount);
/* 232 */     return new EconomyResponse(amount, account.getBalance(), EconomyResponse.ResponseType.SUCCESS, "");
/*     */   }
/*     */ 
/*     */   public EconomyResponse isBankOwner(String name, String playerName)
/*     */   {
/* 238 */     return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Currency does not support Bank members.");
/*     */   }
/*     */ 
/*     */   public EconomyResponse isBankMember(String name, String playerName)
/*     */   {
/* 243 */     return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Currency does not support Bank members.");
/*     */   }
/*     */ 
/*     */   public List<String> getBanks()
/*     */   {
/* 248 */     return this.currency.getAccountManager().getAccountList();
/*     */   }
/*     */ 
/*     */   public boolean hasBankSupport()
/*     */   {
/* 253 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean hasAccount(String playerName)
/*     */   {
/* 258 */     return this.currency.getAccountManager().getAccount(playerName) != null;
/*     */   }
/*     */ 
/*     */   public boolean createPlayerAccount(String playerName)
/*     */   {
/* 263 */     if (this.currency.getAccountManager().getAccount(playerName) != null) {
/* 264 */       return false;
/*     */     }
/* 266 */     this.currency.getAccountManager().createAccount(playerName);
/* 267 */     return true;
/*     */   }
/*     */ 
/*     */   public int fractionalDigits()
/*     */   {
/* 272 */     return -1;
/*     */   }
/*     */ 
/*     */   public boolean hasAccount(String playerName, String worldName)
/*     */   {
/* 277 */     return hasAccount(playerName);
/*     */   }
/*     */ 
/*     */   public double getBalance(String playerName, String world)
/*     */   {
/* 282 */     return getBalance(playerName);
/*     */   }
/*     */ 
/*     */   public boolean has(String playerName, String worldName, double amount)
/*     */   {
/* 287 */     return has(playerName, amount);
/*     */   }
/*     */ 
/*     */   public EconomyResponse withdrawPlayer(String playerName, String worldName, double amount)
/*     */   {
/* 292 */     return withdrawPlayer(playerName, amount);
/*     */   }
/*     */ 
/*     */   public EconomyResponse depositPlayer(String playerName, String worldName, double amount)
/*     */   {
/* 297 */     return depositPlayer(playerName, amount);
/*     */   }
/*     */ 
/*     */   public boolean createPlayerAccount(String playerName, String worldName)
/*     */   {
/* 302 */     return createPlayerAccount(playerName);
/*     */   }
/*     */ 
/*     */   public class EconomyServerListener
/*     */     implements Listener
/*     */   {
/*  59 */     private Economy_CurrencyCore economy = null;
/*     */ 
/*     */     public EconomyServerListener(Economy_CurrencyCore economy) {
/*  62 */       this.economy = economy;
/*     */     }
/*     */ 
/*     */     @EventHandler(priority=EventPriority.MONITOR)
/*     */     public void onPluginEnable(PluginEnableEvent event) {
/*  67 */       if (this.economy.currency == null) {
/*  68 */         Plugin currencyPlugin = event.getPlugin();
/*     */ 
/*  70 */         if ((currencyPlugin.getDescription().getName().equals("CurrencyCore")) && (currencyPlugin.getClass().getName().equals("is.currency.Currency"))) {
/*  71 */           this.economy.currency = ((Currency)currencyPlugin);
/*  72 */           Economy_CurrencyCore.log.info(String.format("[%s][Economy] %s hooked.", new Object[] { Economy_CurrencyCore.this.plugin.getDescription().getName(), this.economy.getName() }));
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/*     */     @EventHandler(priority=EventPriority.MONITOR)
/*     */     public void onPluginDisable(PluginDisableEvent event) {
/*  79 */       if ((this.economy.currency != null) && 
/*  80 */         (event.getPlugin().getDescription().getName().equals("CurrencyCore"))) {
/*  81 */         this.economy.currency = null;
/*  82 */         Economy_CurrencyCore.log.info(String.format("[%s][Economy] %s unhooked.", new Object[] { Economy_CurrencyCore.this.plugin.getDescription().getName(), this.economy.getName() }));
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\Vault.jar
 * Qualified Name:     net.milkbowl.vault.economy.plugins.Economy_CurrencyCore
 * JD-Core Version:    0.6.2
 */