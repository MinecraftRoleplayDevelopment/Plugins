/*     */ package net.milkbowl.vault.economy.plugins;
/*     */ 
/*     */ import java.util.ArrayList;
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
/*     */ import org.gestern.gringotts.Account;
/*     */ import org.gestern.gringotts.AccountHolder;
/*     */ import org.gestern.gringotts.AccountHolderFactory;
/*     */ import org.gestern.gringotts.Accounting;
/*     */ import org.gestern.gringotts.Configuration;
/*     */ import org.gestern.gringotts.Gringotts;
/*     */ 
/*     */ public class Economy_Gringotts extends AbstractEconomy
/*     */ {
/*  39 */   private static final Logger log = Logger.getLogger("Minecraft");
/*     */ 
/*  41 */   private final String name = "Gringotts";
/*  42 */   private Plugin plugin = null;
/*  43 */   private Gringotts gringotts = null;
/*     */ 
/*     */   public Economy_Gringotts(Plugin plugin) {
/*  46 */     this.plugin = plugin;
/*  47 */     Bukkit.getServer().getPluginManager().registerEvents(new EconomyServerListener(this), plugin);
/*     */ 
/*  49 */     if (this.gringotts == null) {
/*  50 */       Plugin grngts = plugin.getServer().getPluginManager().getPlugin("Gringotts");
/*  51 */       if ((grngts != null) && (grngts.isEnabled())) {
/*  52 */         this.gringotts = ((Gringotts)grngts);
/*  53 */         log.info(String.format("[%s][Economy] %s hooked.", new Object[] { plugin.getDescription().getName(), "Gringotts" }));
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean isEnabled()
/*     */   {
/*  90 */     return (this.gringotts != null) && (this.gringotts.isEnabled());
/*     */   }
/*     */ 
/*     */   public String getName()
/*     */   {
/*  95 */     return "Gringotts";
/*     */   }
/*     */ 
/*     */   public boolean hasBankSupport()
/*     */   {
/* 100 */     return false;
/*     */   }
/*     */ 
/*     */   public int fractionalDigits()
/*     */   {
/* 105 */     return 2;
/*     */   }
/*     */ 
/*     */   public String format(double amount)
/*     */   {
/* 110 */     return Double.toString(amount);
/*     */   }
/*     */ 
/*     */   public String currencyNamePlural()
/*     */   {
/* 115 */     return Configuration.config.currencyNamePlural;
/*     */   }
/*     */ 
/*     */   public String currencyNameSingular()
/*     */   {
/* 120 */     return Configuration.config.currencyNameSingular;
/*     */   }
/*     */ 
/*     */   public boolean hasAccount(String playerName)
/*     */   {
/* 125 */     AccountHolder owner = this.gringotts.accountHolderFactory.getAccount(playerName);
/* 126 */     if (owner == null) {
/* 127 */       return false;
/*     */     }
/*     */ 
/* 130 */     return this.gringotts.accounting.getAccount(owner) != null;
/*     */   }
/*     */ 
/*     */   public double getBalance(String playerName)
/*     */   {
/* 135 */     AccountHolder owner = this.gringotts.accountHolderFactory.getAccount(playerName);
/* 136 */     if (owner == null) {
/* 137 */       return 0.0D;
/*     */     }
/* 139 */     Account account = this.gringotts.accounting.getAccount(owner);
/* 140 */     return account.balance();
/*     */   }
/*     */ 
/*     */   public boolean has(String playerName, double amount)
/*     */   {
/* 145 */     return getBalance(playerName) >= amount;
/*     */   }
/*     */ 
/*     */   public EconomyResponse withdrawPlayer(String playerName, double amount)
/*     */   {
/* 151 */     if (amount < 0.0D) {
/* 152 */       return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.FAILURE, "Cannot withdraw a negative amount.");
/*     */     }
/*     */ 
/* 155 */     AccountHolder accountHolder = this.gringotts.accountHolderFactory.getAccount(playerName);
/* 156 */     if (accountHolder == null) {
/* 157 */       return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.FAILURE, playerName + " is not a valid account holder.");
/*     */     }
/*     */ 
/* 160 */     Account account = this.gringotts.accounting.getAccount(accountHolder);
/*     */ 
/* 162 */     if ((account.balance() >= amount) && (account.remove(amount)))
/*     */     {
/* 164 */       return new EconomyResponse(amount, account.balance(), EconomyResponse.ResponseType.SUCCESS, null);
/*     */     }
/*     */ 
/* 167 */     return new EconomyResponse(0.0D, account.balance(), EconomyResponse.ResponseType.FAILURE, "Insufficient funds");
/*     */   }
/*     */ 
/*     */   public EconomyResponse depositPlayer(String playerName, double amount)
/*     */   {
/* 174 */     if (amount < 0.0D) {
/* 175 */       return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.FAILURE, "Cannot desposit negative funds");
/*     */     }
/*     */ 
/* 178 */     AccountHolder accountHolder = this.gringotts.accountHolderFactory.getAccount(playerName);
/* 179 */     if (accountHolder == null) {
/* 180 */       return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.FAILURE, playerName + " is not a valid account holder.");
/*     */     }
/*     */ 
/* 183 */     Account account = this.gringotts.accounting.getAccount(accountHolder);
/*     */ 
/* 185 */     if (account.add(amount)) {
/* 186 */       return new EconomyResponse(amount, account.balance(), EconomyResponse.ResponseType.SUCCESS, null);
/*     */     }
/* 188 */     return new EconomyResponse(0.0D, account.balance(), EconomyResponse.ResponseType.FAILURE, "Not enough capacity to store that amount!");
/*     */   }
/*     */ 
/*     */   public EconomyResponse createBank(String name, String player)
/*     */   {
/* 194 */     return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Gringotts does not support bank accounts!");
/*     */   }
/*     */ 
/*     */   public EconomyResponse deleteBank(String name)
/*     */   {
/* 199 */     return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Gringotts does not support bank accounts!");
/*     */   }
/*     */ 
/*     */   public EconomyResponse bankBalance(String name)
/*     */   {
/* 204 */     return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Gringotts does not support bank accounts!");
/*     */   }
/*     */ 
/*     */   public EconomyResponse bankHas(String name, double amount)
/*     */   {
/* 209 */     return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Gringotts does not support bank accounts!");
/*     */   }
/*     */ 
/*     */   public EconomyResponse bankWithdraw(String name, double amount)
/*     */   {
/* 214 */     return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Gringotts does not support bank accounts!");
/*     */   }
/*     */ 
/*     */   public EconomyResponse bankDeposit(String name, double amount)
/*     */   {
/* 219 */     return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Gringotts does not support bank accounts!");
/*     */   }
/*     */ 
/*     */   public EconomyResponse isBankOwner(String name, String playerName)
/*     */   {
/* 224 */     return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Gringotts does not support bank accounts!");
/*     */   }
/*     */ 
/*     */   public EconomyResponse isBankMember(String name, String playerName)
/*     */   {
/* 229 */     return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Gringotts does not support bank accounts!");
/*     */   }
/*     */ 
/*     */   public List<String> getBanks()
/*     */   {
/* 234 */     return new ArrayList();
/*     */   }
/*     */ 
/*     */   public boolean createPlayerAccount(String playerName)
/*     */   {
/* 239 */     return hasAccount(playerName);
/*     */   }
/*     */ 
/*     */   public boolean hasAccount(String playerName, String worldName)
/*     */   {
/* 244 */     return hasAccount(playerName);
/*     */   }
/*     */ 
/*     */   public double getBalance(String playerName, String world)
/*     */   {
/* 249 */     return getBalance(playerName);
/*     */   }
/*     */ 
/*     */   public boolean has(String playerName, String worldName, double amount)
/*     */   {
/* 254 */     return has(playerName, amount);
/*     */   }
/*     */ 
/*     */   public EconomyResponse withdrawPlayer(String playerName, String worldName, double amount)
/*     */   {
/* 259 */     return withdrawPlayer(playerName, amount);
/*     */   }
/*     */ 
/*     */   public EconomyResponse depositPlayer(String playerName, String worldName, double amount)
/*     */   {
/* 264 */     return depositPlayer(playerName, amount);
/*     */   }
/*     */ 
/*     */   public boolean createPlayerAccount(String playerName, String worldName)
/*     */   {
/* 269 */     return createPlayerAccount(playerName);
/*     */   }
/*     */ 
/*     */   public class EconomyServerListener
/*     */     implements Listener
/*     */   {
/*  59 */     Economy_Gringotts economy = null;
/*     */ 
/*     */     public EconomyServerListener(Economy_Gringotts economy_Gringotts) {
/*  62 */       this.economy = economy_Gringotts;
/*     */     }
/*     */ 
/*     */     @EventHandler(priority=EventPriority.MONITOR)
/*     */     public void onPluginEnable(PluginEnableEvent event) {
/*  67 */       if (this.economy.gringotts == null) {
/*  68 */         Plugin grngts = event.getPlugin();
/*     */ 
/*  70 */         if (grngts.getDescription().getName().equals("Gringotts")) {
/*  71 */           this.economy.gringotts = ((Gringotts)grngts);
/*  72 */           Economy_Gringotts.log.info(String.format("[%s][Economy] %s hooked.", new Object[] { Economy_Gringotts.this.plugin.getDescription().getName(), "Gringotts" }));
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/*     */     @EventHandler(priority=EventPriority.MONITOR)
/*     */     public void onPluginDisable(PluginDisableEvent event) {
/*  79 */       if ((this.economy.gringotts != null) && 
/*  80 */         (event.getPlugin().getDescription().getName().equals("Gringotts"))) {
/*  81 */         this.economy.gringotts = null;
/*  82 */         Economy_Gringotts.log.info(String.format("[%s][Economy] %s unhooked.", new Object[] { Economy_Gringotts.this.plugin.getDescription().getName(), "Gringotts" }));
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\Vault.jar
 * Qualified Name:     net.milkbowl.vault.economy.plugins.Economy_Gringotts
 * JD-Core Version:    0.6.2
 */