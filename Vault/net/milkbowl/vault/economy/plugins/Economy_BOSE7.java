/*     */ package net.milkbowl.vault.economy.plugins;
/*     */ 
/*     */ import cosine.boseconomy.BOSEconomy;
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
/*     */ public class Economy_BOSE7 extends AbstractEconomy
/*     */ {
/*  36 */   private static final Logger log = Logger.getLogger("Minecraft");
/*     */ 
/*  38 */   private final String name = "BOSEconomy";
/*  39 */   private Plugin plugin = null;
/*  40 */   private BOSEconomy economy = null;
/*     */ 
/*     */   public Economy_BOSE7(Plugin plugin) {
/*  43 */     this.plugin = plugin;
/*  44 */     Bukkit.getServer().getPluginManager().registerEvents(new EconomyServerListener(this), plugin);
/*     */ 
/*  47 */     if (this.economy == null) {
/*  48 */       Plugin bose = plugin.getServer().getPluginManager().getPlugin("BOSEconomy");
/*  49 */       if ((bose != null) && (bose.isEnabled()) && (bose.getDescription().getVersion().startsWith("0.7"))) {
/*  50 */         this.economy = ((BOSEconomy)bose);
/*  51 */         log.info(String.format("[%s][Economy] %s hooked.", new Object[] { plugin.getDescription().getName(), "BOSEconomy" }));
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public String getName()
/*     */   {
/*  58 */     return "BOSEconomy";
/*     */   }
/*     */ 
/*     */   public boolean isEnabled()
/*     */   {
/*  63 */     if (this.economy == null) {
/*  64 */       return false;
/*     */     }
/*  66 */     return this.economy.isEnabled();
/*     */   }
/*     */ 
/*     */   public double getBalance(String playerName)
/*     */   {
/*  74 */     double balance = this.economy.getPlayerMoneyDouble(playerName);
/*     */ 
/*  76 */     double fBalance = balance;
/*  77 */     return fBalance;
/*     */   }
/*     */ 
/*     */   public EconomyResponse withdrawPlayer(String playerName, double amount)
/*     */   {
/*  82 */     if (amount < 0.0D) {
/*  83 */       return new EconomyResponse(0.0D, this.economy.getPlayerMoneyDouble(playerName), EconomyResponse.ResponseType.FAILURE, "Cannot withdraw negative funds");
/*     */     }
/*     */ 
/*  86 */     if (!has(playerName, amount)) {
/*  87 */       return new EconomyResponse(0.0D, this.economy.getPlayerMoneyDouble(playerName), EconomyResponse.ResponseType.FAILURE, "Insufficient funds");
/*     */     }
/*     */ 
/*  90 */     double balance = this.economy.getPlayerMoneyDouble(playerName);
/*  91 */     if (this.economy.setPlayerMoney(playerName, balance - amount, false)) {
/*  92 */       balance = this.economy.getPlayerMoneyDouble(playerName);
/*  93 */       return new EconomyResponse(amount, balance, EconomyResponse.ResponseType.SUCCESS, "");
/*     */     }
/*  95 */     return new EconomyResponse(0.0D, balance, EconomyResponse.ResponseType.FAILURE, "Error withdrawing funds");
/*     */   }
/*     */ 
/*     */   public EconomyResponse depositPlayer(String playerName, double amount)
/*     */   {
/* 101 */     if (amount < 0.0D) {
/* 102 */       return new EconomyResponse(0.0D, this.economy.getPlayerMoneyDouble(playerName), EconomyResponse.ResponseType.FAILURE, "Cannot deposit negative funds");
/*     */     }
/* 104 */     double balance = this.economy.getPlayerMoneyDouble(playerName);
/* 105 */     if (this.economy.setPlayerMoney(playerName, balance + amount, false)) {
/* 106 */       balance = this.economy.getPlayerMoneyDouble(playerName);
/* 107 */       return new EconomyResponse(amount, balance, EconomyResponse.ResponseType.SUCCESS, "");
/*     */     }
/* 109 */     return new EconomyResponse(0.0D, balance, EconomyResponse.ResponseType.FAILURE, "Error depositing funds");
/*     */   }
/*     */ 
/*     */   public String currencyNamePlural()
/*     */   {
/* 115 */     return this.economy.getMoneyNamePlural();
/*     */   }
/*     */ 
/*     */   public String currencyNameSingular()
/*     */   {
/* 120 */     return this.economy.getMoneyName();
/*     */   }
/*     */ 
/*     */   public String format(double amount)
/*     */   {
/* 155 */     return this.economy.getMoneyFormatted(amount);
/*     */   }
/*     */ 
/*     */   public EconomyResponse createBank(String name, String player)
/*     */   {
/* 160 */     boolean success = this.economy.addBankOwner(name, player, false);
/* 161 */     if (success) {
/* 162 */       return new EconomyResponse(0.0D, this.economy.getBankMoneyDouble(name), EconomyResponse.ResponseType.SUCCESS, "");
/*     */     }
/* 164 */     return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.FAILURE, "Unable to create that bank account.");
/*     */   }
/*     */ 
/*     */   public EconomyResponse deleteBank(String name)
/*     */   {
/* 169 */     boolean success = this.economy.removeBank(name);
/* 170 */     if (success) {
/* 171 */       return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.SUCCESS, "");
/*     */     }
/* 173 */     return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.FAILURE, "Unable to remove that bank account.");
/*     */   }
/*     */ 
/*     */   public EconomyResponse bankHas(String name, double amount)
/*     */   {
/* 178 */     if (!this.economy.bankExists(name)) {
/* 179 */       return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.FAILURE, "That bank does not exist!");
/*     */     }
/*     */ 
/* 182 */     double bankMoney = this.economy.getBankMoneyDouble(name);
/* 183 */     if (bankMoney < amount) {
/* 184 */       return new EconomyResponse(0.0D, bankMoney, EconomyResponse.ResponseType.FAILURE, "The bank does not have enough money!");
/*     */     }
/* 186 */     return new EconomyResponse(0.0D, bankMoney, EconomyResponse.ResponseType.SUCCESS, "");
/*     */   }
/*     */ 
/*     */   public EconomyResponse bankWithdraw(String name, double amount)
/*     */   {
/* 192 */     EconomyResponse er = bankHas(name, amount);
/* 193 */     if (!er.transactionSuccess()) {
/* 194 */       return er;
/*     */     }
/* 196 */     this.economy.addBankMoney(name, -amount, true);
/* 197 */     return new EconomyResponse(amount, this.economy.getBankMoneyDouble(name), EconomyResponse.ResponseType.SUCCESS, "");
/*     */   }
/*     */ 
/*     */   public EconomyResponse bankDeposit(String name, double amount)
/*     */   {
/* 203 */     if (!this.economy.bankExists(name)) {
/* 204 */       return new EconomyResponse(amount, 0.0D, EconomyResponse.ResponseType.FAILURE, "That bank does not exist!");
/*     */     }
/* 206 */     this.economy.addBankMoney(name, amount, true);
/* 207 */     return new EconomyResponse(amount, this.economy.getBankMoneyDouble(name), EconomyResponse.ResponseType.SUCCESS, "");
/*     */   }
/*     */ 
/*     */   public EconomyResponse isBankOwner(String name, String playerName)
/*     */   {
/* 213 */     if (!this.economy.bankExists(name))
/* 214 */       return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.FAILURE, "That bank does not exist!");
/* 215 */     if (this.economy.isBankOwner(name, playerName)) {
/* 216 */       return new EconomyResponse(0.0D, this.economy.getBankMoneyDouble(name), EconomyResponse.ResponseType.SUCCESS, "");
/*     */     }
/* 218 */     return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.FAILURE, "That player is not a bank owner!");
/*     */   }
/*     */ 
/*     */   public EconomyResponse isBankMember(String name, String playerName)
/*     */   {
/* 223 */     if (!this.economy.bankExists(name))
/* 224 */       return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.FAILURE, "That bank does not exist!");
/* 225 */     if (this.economy.isBankMember(name, playerName)) {
/* 226 */       return new EconomyResponse(0.0D, this.economy.getBankMoneyDouble(name), EconomyResponse.ResponseType.SUCCESS, "");
/*     */     }
/* 228 */     return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.FAILURE, "That player is not a bank member!");
/*     */   }
/*     */ 
/*     */   public EconomyResponse bankBalance(String name)
/*     */   {
/* 234 */     if (!this.economy.bankExists(name)) {
/* 235 */       return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.FAILURE, "That bank does not exist!");
/*     */     }
/*     */ 
/* 238 */     double bankMoney = this.economy.getBankMoneyDouble(name);
/* 239 */     return new EconomyResponse(0.0D, bankMoney, EconomyResponse.ResponseType.SUCCESS, null);
/*     */   }
/*     */ 
/*     */   public List<String> getBanks()
/*     */   {
/* 244 */     return this.economy.getBankList();
/*     */   }
/*     */ 
/*     */   public boolean has(String playerName, double amount)
/*     */   {
/* 249 */     return getBalance(playerName) >= amount;
/*     */   }
/*     */ 
/*     */   public boolean hasBankSupport()
/*     */   {
/* 254 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean hasAccount(String playerName)
/*     */   {
/* 259 */     return this.economy.playerRegistered(playerName, false);
/*     */   }
/*     */ 
/*     */   public boolean createPlayerAccount(String playerName)
/*     */   {
/* 264 */     if (this.economy.playerRegistered(playerName, false)) {
/* 265 */       return false;
/*     */     }
/* 267 */     return this.economy.registerPlayer(playerName);
/*     */   }
/*     */ 
/*     */   public int fractionalDigits()
/*     */   {
/* 272 */     return this.economy.getFractionalDigits();
/*     */   }
/*     */ 
/*     */   public boolean hasAccount(String playerName, String worldName)
/*     */   {
/* 278 */     return hasAccount(playerName);
/*     */   }
/*     */ 
/*     */   public double getBalance(String playerName, String world)
/*     */   {
/* 283 */     return getBalance(playerName);
/*     */   }
/*     */ 
/*     */   public boolean has(String playerName, String worldName, double amount)
/*     */   {
/* 288 */     return has(playerName, amount);
/*     */   }
/*     */ 
/*     */   public EconomyResponse withdrawPlayer(String playerName, String worldName, double amount)
/*     */   {
/* 293 */     return withdrawPlayer(playerName, amount);
/*     */   }
/*     */ 
/*     */   public EconomyResponse depositPlayer(String playerName, String worldName, double amount)
/*     */   {
/* 298 */     return depositPlayer(playerName, amount);
/*     */   }
/*     */ 
/*     */   public boolean createPlayerAccount(String playerName, String worldName)
/*     */   {
/* 303 */     return createPlayerAccount(playerName);
/*     */   }
/*     */ 
/*     */   public class EconomyServerListener
/*     */     implements Listener
/*     */   {
/* 124 */     Economy_BOSE7 economy = null;
/*     */ 
/*     */     public EconomyServerListener(Economy_BOSE7 economy) {
/* 127 */       this.economy = economy;
/*     */     }
/*     */ 
/*     */     @EventHandler(priority=EventPriority.MONITOR)
/*     */     public void onPluginEnable(PluginEnableEvent event) {
/* 132 */       if (this.economy.economy == null) {
/* 133 */         Plugin bose = event.getPlugin();
/*     */ 
/* 135 */         if ((bose.getDescription().getName().equals("BOSEconomy")) && (bose.getDescription().getVersion().startsWith("0.7"))) {
/* 136 */           this.economy.economy = ((BOSEconomy)bose);
/* 137 */           Economy_BOSE7.log.info(String.format("[%s][Economy] %s hooked.", new Object[] { Economy_BOSE7.this.plugin.getDescription().getName(), "BOSEconomy" }));
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/*     */     @EventHandler(priority=EventPriority.MONITOR)
/*     */     public void onPluginDisable(PluginDisableEvent event) {
/* 144 */       if ((this.economy.economy != null) && 
/* 145 */         (event.getPlugin().getDescription().getName().equals("BOSEconomy")) && (event.getPlugin().getDescription().getVersion().startsWith("0.7"))) {
/* 146 */         this.economy.economy = null;
/* 147 */         Economy_BOSE7.log.info(String.format("[%s][Economy] %s unhooked.", new Object[] { Economy_BOSE7.this.plugin.getDescription().getName(), "BOSEconomy" }));
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\Vault.jar
 * Qualified Name:     net.milkbowl.vault.economy.plugins.Economy_BOSE7
 * JD-Core Version:    0.6.2
 */