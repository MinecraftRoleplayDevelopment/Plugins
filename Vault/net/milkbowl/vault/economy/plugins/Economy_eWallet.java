/*     */ package net.milkbowl.vault.economy.plugins;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.logging.Logger;
/*     */ import me.ethan.eWallet.ECO;
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
/*     */ public class Economy_eWallet extends AbstractEconomy
/*     */ {
/*  36 */   private static final Logger log = Logger.getLogger("Minecraft");
/*     */ 
/*  38 */   private final String name = "eWallet";
/*  39 */   private Plugin plugin = null;
/*  40 */   private ECO econ = null;
/*     */ 
/*     */   public Economy_eWallet(Plugin plugin) {
/*  43 */     this.plugin = plugin;
/*  44 */     Bukkit.getServer().getPluginManager().registerEvents(new EconomyServerListener(this), plugin);
/*     */ 
/*  47 */     if (this.econ == null) {
/*  48 */       Plugin econ = plugin.getServer().getPluginManager().getPlugin("eWallet");
/*  49 */       if ((econ != null) && (econ.isEnabled())) {
/*  50 */         this.econ = ((ECO)econ);
/*  51 */         log.info(String.format("[%s][Economy] %s hooked.", new Object[] { plugin.getDescription().getName(), "eWallet" }));
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean isEnabled()
/*     */   {
/*  88 */     return this.econ != null;
/*     */   }
/*     */ 
/*     */   public String getName()
/*     */   {
/*  93 */     return "eWallet";
/*     */   }
/*     */ 
/*     */   public String format(double amount)
/*     */   {
/*  98 */     amount = Math.ceil(amount);
/*  99 */     if (amount == 1.0D) {
/* 100 */       return String.format("%d %s", new Object[] { Integer.valueOf((int)amount), this.econ.singularCurrency });
/*     */     }
/* 102 */     return String.format("%d %s", new Object[] { Integer.valueOf((int)amount), this.econ.pluralCurrency });
/*     */   }
/*     */ 
/*     */   public String currencyNameSingular()
/*     */   {
/* 108 */     return this.econ.singularCurrency;
/*     */   }
/*     */ 
/*     */   public String currencyNamePlural()
/*     */   {
/* 113 */     return this.econ.pluralCurrency;
/*     */   }
/*     */ 
/*     */   public double getBalance(String playerName)
/*     */   {
/* 118 */     Integer i = this.econ.getMoney(playerName);
/* 119 */     return i == null ? 0.0D : i.intValue();
/*     */   }
/*     */ 
/*     */   public boolean has(String playerName, double amount)
/*     */   {
/* 124 */     return getBalance(playerName) >= Math.ceil(amount);
/*     */   }
/*     */ 
/*     */   public EconomyResponse withdrawPlayer(String playerName, double amount)
/*     */   {
/* 129 */     double balance = getBalance(playerName);
/* 130 */     amount = Math.ceil(amount);
/* 131 */     if (amount < 0.0D)
/* 132 */       return new EconomyResponse(0.0D, balance, EconomyResponse.ResponseType.FAILURE, "Cannot withdraw negative funds");
/* 133 */     if (balance >= amount) {
/* 134 */       double finalBalance = balance - amount;
/* 135 */       this.econ.takeMoney(playerName, Integer.valueOf((int)amount));
/* 136 */       return new EconomyResponse(amount, finalBalance, EconomyResponse.ResponseType.SUCCESS, null);
/*     */     }
/* 138 */     return new EconomyResponse(0.0D, balance, EconomyResponse.ResponseType.FAILURE, "Insufficient funds");
/*     */   }
/*     */ 
/*     */   public EconomyResponse depositPlayer(String playerName, double amount)
/*     */   {
/* 144 */     double balance = getBalance(playerName);
/* 145 */     amount = Math.ceil(amount);
/* 146 */     if (amount < 0.0D) {
/* 147 */       return new EconomyResponse(0.0D, balance, EconomyResponse.ResponseType.FAILURE, "Cannot deposit negative funds");
/*     */     }
/* 149 */     balance += amount;
/* 150 */     this.econ.giveMoney(playerName, Integer.valueOf((int)amount));
/* 151 */     return new EconomyResponse(amount, balance, EconomyResponse.ResponseType.SUCCESS, null);
/*     */   }
/*     */ 
/*     */   public EconomyResponse createBank(String name, String player)
/*     */   {
/* 157 */     return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "eWallet does not support bank accounts!");
/*     */   }
/*     */ 
/*     */   public EconomyResponse deleteBank(String name)
/*     */   {
/* 162 */     return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "eWallet does not support bank accounts!");
/*     */   }
/*     */ 
/*     */   public EconomyResponse bankHas(String name, double amount)
/*     */   {
/* 167 */     return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "eWallet does not support bank accounts!");
/*     */   }
/*     */ 
/*     */   public EconomyResponse bankWithdraw(String name, double amount)
/*     */   {
/* 172 */     return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "eWallet does not support bank accounts!");
/*     */   }
/*     */ 
/*     */   public EconomyResponse bankDeposit(String name, double amount)
/*     */   {
/* 177 */     return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "eWallet does not support bank accounts!");
/*     */   }
/*     */ 
/*     */   public EconomyResponse isBankOwner(String name, String playerName)
/*     */   {
/* 182 */     return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "eWallet does not support bank accounts!");
/*     */   }
/*     */ 
/*     */   public EconomyResponse isBankMember(String name, String playerName)
/*     */   {
/* 187 */     return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "eWallet does not support bank accounts!");
/*     */   }
/*     */ 
/*     */   public EconomyResponse bankBalance(String name)
/*     */   {
/* 192 */     return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "eWallet does not support bank accounts!");
/*     */   }
/*     */ 
/*     */   public List<String> getBanks()
/*     */   {
/* 197 */     return new ArrayList();
/*     */   }
/*     */ 
/*     */   public boolean hasBankSupport()
/*     */   {
/* 202 */     return false;
/*     */   }
/*     */ 
/*     */   public boolean hasAccount(String playerName)
/*     */   {
/* 207 */     return this.econ.hasAccount(playerName).booleanValue();
/*     */   }
/*     */ 
/*     */   public boolean createPlayerAccount(String playerName)
/*     */   {
/* 212 */     if (hasAccount(playerName)) {
/* 213 */       return false;
/*     */     }
/* 215 */     this.econ.createAccount(playerName, Integer.valueOf(0));
/* 216 */     return true;
/*     */   }
/*     */ 
/*     */   public int fractionalDigits()
/*     */   {
/* 221 */     return 0;
/*     */   }
/*     */ 
/*     */   public boolean hasAccount(String playerName, String worldName)
/*     */   {
/* 226 */     return hasAccount(playerName);
/*     */   }
/*     */ 
/*     */   public double getBalance(String playerName, String world)
/*     */   {
/* 231 */     return getBalance(playerName);
/*     */   }
/*     */ 
/*     */   public boolean has(String playerName, String worldName, double amount)
/*     */   {
/* 236 */     return has(playerName, amount);
/*     */   }
/*     */ 
/*     */   public EconomyResponse withdrawPlayer(String playerName, String worldName, double amount)
/*     */   {
/* 241 */     return withdrawPlayer(playerName, amount);
/*     */   }
/*     */ 
/*     */   public EconomyResponse depositPlayer(String playerName, String worldName, double amount)
/*     */   {
/* 246 */     return depositPlayer(playerName, amount);
/*     */   }
/*     */ 
/*     */   public boolean createPlayerAccount(String playerName, String worldName)
/*     */   {
/* 251 */     return createPlayerAccount(playerName);
/*     */   }
/*     */ 
/*     */   public class EconomyServerListener
/*     */     implements Listener
/*     */   {
/*  57 */     Economy_eWallet economy = null;
/*     */ 
/*     */     public EconomyServerListener(Economy_eWallet economy) {
/*  60 */       this.economy = economy;
/*     */     }
/*     */ 
/*     */     @EventHandler(priority=EventPriority.MONITOR)
/*     */     public void onPluginEnable(PluginEnableEvent event) {
/*  65 */       if (this.economy.econ == null) {
/*  66 */         Plugin eco = event.getPlugin();
/*     */ 
/*  68 */         if (eco.getDescription().getName().equals("eWallet")) {
/*  69 */           this.economy.econ = ((ECO)eco);
/*  70 */           Economy_eWallet.log.info(String.format("[%s][Economy] %s hooked.", new Object[] { Economy_eWallet.this.plugin.getDescription().getName(), "eWallet" }));
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/*     */     @EventHandler(priority=EventPriority.MONITOR)
/*     */     public void onPluginDisable(PluginDisableEvent event) {
/*  77 */       if ((this.economy.econ != null) && 
/*  78 */         (event.getPlugin().getDescription().getName().equals("eWallet"))) {
/*  79 */         this.economy.econ = null;
/*  80 */         Economy_eWallet.log.info(String.format("[%s][Economy] %s unhooked.", new Object[] { Economy_eWallet.this.plugin.getDescription().getName(), "eWallet" }));
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\Vault.jar
 * Qualified Name:     net.milkbowl.vault.economy.plugins.Economy_eWallet
 * JD-Core Version:    0.6.2
 */