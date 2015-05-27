/*     */ package net.milkbowl.vault.economy.plugins;
/*     */ 
/*     */ import com.greatmancode.craftconomy3.Cause;
/*     */ import com.greatmancode.craftconomy3.Common;
/*     */ import com.greatmancode.craftconomy3.account.Account;
/*     */ import com.greatmancode.craftconomy3.account.AccountACL;
/*     */ import com.greatmancode.craftconomy3.account.AccountManager;
/*     */ import com.greatmancode.craftconomy3.currency.Currency;
/*     */ import com.greatmancode.craftconomy3.currency.CurrencyManager;
/*     */ import com.greatmancode.craftconomy3.tools.interfaces.BukkitLoader;
/*     */ import com.greatmancode.craftconomy3.tools.interfaces.caller.ServerCaller;
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
/*     */ public class Economy_Craftconomy3 extends AbstractEconomy
/*     */ {
/*  40 */   private static final Logger log = Logger.getLogger("Minecraft");
/*  41 */   private final String name = "Craftconomy3";
/*  42 */   private Plugin plugin = null;
/*  43 */   protected BukkitLoader economy = null;
/*     */ 
/*     */   public Economy_Craftconomy3(Plugin plugin) {
/*  46 */     this.plugin = plugin;
/*  47 */     Bukkit.getServer().getPluginManager().registerEvents(new EconomyServerListener(this), plugin);
/*     */ 
/*  50 */     if (this.economy == null) {
/*  51 */       Plugin ec = plugin.getServer().getPluginManager().getPlugin("Craftconomy3");
/*  52 */       if ((ec != null) && (ec.isEnabled()) && (ec.getClass().getName().equals("com.greatmancode.craftconomy3.BukkitLoader"))) {
/*  53 */         this.economy = ((BukkitLoader)ec);
/*  54 */         log.info(String.format("[%s][Economy] %s hooked.", new Object[] { plugin.getDescription().getName(), "Craftconomy3" }));
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean isEnabled()
/*     */   {
/*  91 */     if (this.economy == null) {
/*  92 */       return false;
/*     */     }
/*  94 */     return this.economy.isEnabled();
/*     */   }
/*     */ 
/*     */   public String getName()
/*     */   {
/* 100 */     return "Craftconomy3";
/*     */   }
/*     */ 
/*     */   public String format(double amount)
/*     */   {
/* 105 */     return Common.getInstance().format(null, Common.getInstance().getCurrencyManager().getDefaultCurrency(), amount);
/*     */   }
/*     */ 
/*     */   public String currencyNameSingular()
/*     */   {
/* 110 */     return Common.getInstance().getCurrencyManager().getDefaultCurrency().getName();
/*     */   }
/*     */ 
/*     */   public String currencyNamePlural()
/*     */   {
/* 115 */     return Common.getInstance().getCurrencyManager().getDefaultCurrency().getPlural();
/*     */   }
/*     */ 
/*     */   public double getBalance(String playerName)
/*     */   {
/* 120 */     return getBalance(playerName, "default");
/*     */   }
/*     */ 
/*     */   public EconomyResponse withdrawPlayer(String playerName, double amount)
/*     */   {
/* 125 */     return withdrawPlayer(playerName, "default", amount);
/*     */   }
/*     */ 
/*     */   public EconomyResponse depositPlayer(String playerName, double amount)
/*     */   {
/* 130 */     return depositPlayer(playerName, "default", amount);
/*     */   }
/*     */ 
/*     */   public boolean has(String playerName, double amount)
/*     */   {
/* 135 */     return has(playerName, "default", amount);
/*     */   }
/*     */ 
/*     */   public EconomyResponse createBank(String name, String player)
/*     */   {
/* 140 */     boolean success = false;
/* 141 */     if (!Common.getInstance().getAccountManager().exist(name, true)) {
/* 142 */       Common.getInstance().getAccountManager().getAccount(name, true).getAccountACL().set(player, true, true, true, true, true);
/* 143 */       success = true;
/*     */     }
/* 145 */     if (success) {
/* 146 */       return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.SUCCESS, "");
/*     */     }
/*     */ 
/* 149 */     return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.FAILURE, "Unable to create that bank account. It already exists!");
/*     */   }
/*     */ 
/*     */   public EconomyResponse deleteBank(String name)
/*     */   {
/* 154 */     boolean success = Common.getInstance().getAccountManager().delete(name, true);
/* 155 */     if (success) {
/* 156 */       return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.SUCCESS, "");
/*     */     }
/*     */ 
/* 159 */     return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.FAILURE, "Unable to delete that bank account.");
/*     */   }
/*     */ 
/*     */   public EconomyResponse bankHas(String name, double amount)
/*     */   {
/* 165 */     if (Common.getInstance().getAccountManager().exist(name, true)) {
/* 166 */       Account account = Common.getInstance().getAccountManager().getAccount(name, true);
/* 167 */       if (account.hasEnough(amount, Common.getInstance().getServerCaller().getDefaultWorld(), Common.getInstance().getCurrencyManager().getDefaultCurrency().getName())) {
/* 168 */         return new EconomyResponse(0.0D, bankBalance(name).balance, EconomyResponse.ResponseType.SUCCESS, "");
/*     */       }
/* 170 */       return new EconomyResponse(0.0D, bankBalance(name).balance, EconomyResponse.ResponseType.FAILURE, "The bank does not have enough money!");
/*     */     }
/*     */ 
/* 173 */     return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.FAILURE, "That bank does not exist!");
/*     */   }
/*     */ 
/*     */   public EconomyResponse bankWithdraw(String name, double amount)
/*     */   {
/* 178 */     if (amount < 0.0D) {
/* 179 */       return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.FAILURE, "Cannot withdraw negative funds");
/*     */     }
/*     */ 
/* 182 */     EconomyResponse er = bankHas(name, amount);
/* 183 */     if (!er.transactionSuccess()) {
/* 184 */       return er;
/*     */     }
/* 186 */     if (Common.getInstance().getAccountManager().exist(name, true)) {
/* 187 */       return new EconomyResponse(0.0D, Common.getInstance().getAccountManager().getAccount(name, true).withdraw(amount, "default", Common.getInstance().getCurrencyManager().getDefaultBankCurrency().getName(), Cause.VAULT, null), EconomyResponse.ResponseType.SUCCESS, "");
/*     */     }
/* 189 */     return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.FAILURE, "That bank does not exist!");
/*     */   }
/*     */ 
/*     */   public EconomyResponse bankDeposit(String name, double amount)
/*     */   {
/* 195 */     if (amount < 0.0D) {
/* 196 */       return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.FAILURE, "Cannot desposit negative funds");
/*     */     }
/*     */ 
/* 199 */     if (Common.getInstance().getAccountManager().exist(name, true)) {
/* 200 */       return new EconomyResponse(0.0D, Common.getInstance().getAccountManager().getAccount(name, true).deposit(amount, "default", Common.getInstance().getCurrencyManager().getDefaultBankCurrency().getName(), Cause.VAULT, null), EconomyResponse.ResponseType.SUCCESS, "");
/*     */     }
/* 202 */     return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.FAILURE, "That bank does not exist!");
/*     */   }
/*     */ 
/*     */   public EconomyResponse isBankOwner(String name, String playerName)
/*     */   {
/* 207 */     if (Common.getInstance().getAccountManager().exist(name, true)) {
/* 208 */       if (Common.getInstance().getAccountManager().getAccount(name, true).getAccountACL().isOwner(playerName)) {
/* 209 */         return new EconomyResponse(0.0D, bankBalance(name).balance, EconomyResponse.ResponseType.SUCCESS, "");
/*     */       }
/* 211 */       return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.FAILURE, "This player is not the owner of the bank!");
/*     */     }
/* 213 */     return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.FAILURE, "That bank does not exist!");
/*     */   }
/*     */ 
/*     */   public EconomyResponse isBankMember(String name, String playerName)
/*     */   {
/* 220 */     EconomyResponse er = isBankOwner(name, playerName);
/* 221 */     if (er.transactionSuccess()) {
/* 222 */       return er;
/*     */     }
/* 224 */     if (Common.getInstance().getAccountManager().exist(name, true)) {
/* 225 */       Account account = Common.getInstance().getAccountManager().getAccount(name, true);
/* 226 */       if ((account.getAccountACL().canDeposit(playerName)) && (account.getAccountACL().canWithdraw(playerName))) {
/* 227 */         return new EconomyResponse(0.0D, bankBalance(name).balance, EconomyResponse.ResponseType.SUCCESS, "");
/*     */       }
/*     */     }
/* 230 */     return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.FAILURE, "This player is not a member of the bank!");
/*     */   }
/*     */ 
/*     */   public EconomyResponse bankBalance(String name)
/*     */   {
/* 236 */     if (Common.getInstance().getAccountManager().exist(name, true)) {
/* 237 */       return new EconomyResponse(0.0D, Common.getInstance().getAccountManager().getAccount(name, true).getBalance("default", Common.getInstance().getCurrencyManager().getDefaultBankCurrency().getName()), EconomyResponse.ResponseType.SUCCESS, "");
/*     */     }
/* 239 */     return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.FAILURE, "That bank does not exist!");
/*     */   }
/*     */ 
/*     */   public List<String> getBanks()
/*     */   {
/* 244 */     return Common.getInstance().getAccountManager().getAllAccounts(true);
/*     */   }
/*     */ 
/*     */   public boolean hasBankSupport()
/*     */   {
/* 249 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean hasAccount(String playerName)
/*     */   {
/* 254 */     return Common.getInstance().getAccountManager().exist(playerName, false);
/*     */   }
/*     */ 
/*     */   public boolean createPlayerAccount(String playerName)
/*     */   {
/* 259 */     if (Common.getInstance().getAccountManager().exist(playerName, false)) {
/* 260 */       return false;
/*     */     }
/* 262 */     Common.getInstance().getAccountManager().getAccount(playerName, false);
/* 263 */     return true;
/*     */   }
/*     */ 
/*     */   public int fractionalDigits()
/*     */   {
/* 268 */     return -1;
/*     */   }
/*     */ 
/*     */   public boolean hasAccount(String playerName, String worldName)
/*     */   {
/* 273 */     return hasAccount(playerName);
/*     */   }
/*     */ 
/*     */   public double getBalance(String playerName, String world)
/*     */   {
/* 278 */     return Common.getInstance().getAccountManager().getAccount(playerName, false).getBalance(world, Common.getInstance().getCurrencyManager().getDefaultCurrency().getName());
/*     */   }
/*     */ 
/*     */   public boolean has(String playerName, String worldName, double amount)
/*     */   {
/* 283 */     return Common.getInstance().getAccountManager().getAccount(playerName, false).hasEnough(amount, worldName, Common.getInstance().getCurrencyManager().getDefaultCurrency().getName());
/*     */   }
/*     */ 
/*     */   public EconomyResponse withdrawPlayer(String playerName, String worldName, double amount)
/*     */   {
/* 288 */     if (amount < 0.0D) {
/* 289 */       return new EconomyResponse(0.0D, getBalance(playerName, worldName), EconomyResponse.ResponseType.FAILURE, "Cannot withdraw negative funds");
/*     */     }
/*     */ 
/* 293 */     Account account = Common.getInstance().getAccountManager().getAccount(playerName, false);
/* 294 */     if (account.hasEnough(amount, worldName, Common.getInstance().getCurrencyManager().getDefaultCurrency().getName())) {
/* 295 */       double balance = account.withdraw(amount, worldName, Common.getInstance().getCurrencyManager().getDefaultCurrency().getName(), Cause.VAULT, null);
/* 296 */       return new EconomyResponse(amount, balance, EconomyResponse.ResponseType.SUCCESS, "");
/*     */     }
/* 298 */     return new EconomyResponse(0.0D, getBalance(playerName, worldName), EconomyResponse.ResponseType.FAILURE, "Insufficient funds");
/*     */   }
/*     */ 
/*     */   public EconomyResponse depositPlayer(String playerName, String worldName, double amount)
/*     */   {
/* 304 */     if (amount < 0.0D) {
/* 305 */       return new EconomyResponse(0.0D, getBalance(playerName, worldName), EconomyResponse.ResponseType.FAILURE, "Cannot desposit negative funds");
/*     */     }
/*     */ 
/* 308 */     Account account = Common.getInstance().getAccountManager().getAccount(playerName, false);
/*     */ 
/* 310 */     double balance = account.deposit(amount, worldName, Common.getInstance().getCurrencyManager().getDefaultCurrency().getName(), Cause.VAULT, null);
/* 311 */     return new EconomyResponse(amount, balance, EconomyResponse.ResponseType.SUCCESS, null);
/*     */   }
/*     */ 
/*     */   public boolean createPlayerAccount(String playerName, String worldName)
/*     */   {
/* 316 */     return createPlayerAccount(playerName);
/*     */   }
/*     */ 
/*     */   public class EconomyServerListener
/*     */     implements Listener
/*     */   {
/*  60 */     Economy_Craftconomy3 economy = null;
/*     */ 
/*     */     public EconomyServerListener(Economy_Craftconomy3 economy) {
/*  63 */       this.economy = economy;
/*     */     }
/*     */ 
/*     */     @EventHandler(priority=EventPriority.MONITOR)
/*     */     public void onPluginEnable(PluginEnableEvent event) {
/*  68 */       if (this.economy.economy == null) {
/*  69 */         Plugin ec = event.getPlugin();
/*     */ 
/*  71 */         if ((ec.getDescription().getName().equals("Craftconomy3")) && (ec.getClass().getName().equals("com.greatmancode.craftconomy3.tools.interfaces.BukkitLoader"))) {
/*  72 */           this.economy.economy = ((BukkitLoader)ec);
/*  73 */           Economy_Craftconomy3.log.info(String.format("[%s][Economy] %s hooked.", new Object[] { Economy_Craftconomy3.this.plugin.getDescription().getName(), "Craftconomy3" }));
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/*     */     @EventHandler(priority=EventPriority.MONITOR)
/*     */     public void onPluginDisable(PluginDisableEvent event) {
/*  80 */       if ((this.economy.economy != null) && 
/*  81 */         (event.getPlugin().getDescription().getName().equals("Craftconomy3"))) {
/*  82 */         this.economy.economy = null;
/*  83 */         Economy_Craftconomy3.log.info(String.format("[%s][Economy] %s unhooked.", new Object[] { Economy_Craftconomy3.this.plugin.getDescription().getName(), "Craftconomy3" }));
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\Vault.jar
 * Qualified Name:     net.milkbowl.vault.economy.plugins.Economy_Craftconomy3
 * JD-Core Version:    0.6.2
 */