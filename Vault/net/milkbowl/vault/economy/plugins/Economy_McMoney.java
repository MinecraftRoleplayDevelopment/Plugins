/*     */ package net.milkbowl.vault.economy.plugins;
/*     */ 
/*     */ import boardinggamer.mcmoney.McMoneyAPI;
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
/*     */ 
/*     */ public class Economy_McMoney extends AbstractEconomy
/*     */ {
/*  37 */   private static final Logger log = Logger.getLogger("Minecraft");
/*     */ 
/*  39 */   private final String name = "McMoney";
/*  40 */   private Plugin plugin = null;
/*  41 */   private McMoneyAPI economy = null;
/*     */ 
/*     */   public Economy_McMoney(Plugin plugin) {
/*  44 */     this.plugin = plugin;
/*  45 */     Bukkit.getServer().getPluginManager().registerEvents(new EconomyServerListener(this), plugin);
/*     */ 
/*  48 */     if (this.economy == null) {
/*  49 */       Plugin econ = plugin.getServer().getPluginManager().getPlugin("McMoney");
/*  50 */       if ((econ != null) && (econ.isEnabled())) {
/*  51 */         this.economy = McMoneyAPI.getInstance();
/*  52 */         log.info(String.format("[%s][Economy] %s hooked.", new Object[] { plugin.getDescription().getName(), "McMoney" }));
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public String getName()
/*     */   {
/*  59 */     return "McMoney";
/*     */   }
/*     */ 
/*     */   public boolean isEnabled()
/*     */   {
/*  64 */     return this.economy != null;
/*     */   }
/*     */ 
/*     */   public double getBalance(String playerName)
/*     */   {
/*  69 */     return this.economy.getMoney(playerName);
/*     */   }
/*     */ 
/*     */   public EconomyResponse withdrawPlayer(String playerName, double amount)
/*     */   {
/*  74 */     double balance = this.economy.getMoney(playerName);
/*  75 */     if (amount < 0.0D)
/*  76 */       return new EconomyResponse(0.0D, balance, EconomyResponse.ResponseType.FAILURE, "Cannot withdraw negative funds");
/*  77 */     if (balance - amount < 0.0D) {
/*  78 */       return new EconomyResponse(0.0D, balance, EconomyResponse.ResponseType.FAILURE, "Insufficient funds");
/*     */     }
/*  80 */     this.economy.removeMoney(playerName, amount);
/*  81 */     return new EconomyResponse(amount, this.economy.getMoney(playerName), EconomyResponse.ResponseType.SUCCESS, "");
/*     */   }
/*     */ 
/*     */   public EconomyResponse depositPlayer(String playerName, double amount)
/*     */   {
/*  86 */     double balance = this.economy.getMoney(playerName);
/*  87 */     if (amount < 0.0D) {
/*  88 */       return new EconomyResponse(0.0D, balance, EconomyResponse.ResponseType.FAILURE, "Cannot deposit negative funds");
/*     */     }
/*  90 */     this.economy.addMoney(playerName, amount);
/*  91 */     return new EconomyResponse(amount, this.economy.getMoney(playerName), EconomyResponse.ResponseType.SUCCESS, "");
/*     */   }
/*     */ 
/*     */   public String currencyNamePlural()
/*     */   {
/*  96 */     return this.economy.moneyNamePlural();
/*     */   }
/*     */ 
/*     */   public String currencyNameSingular()
/*     */   {
/* 101 */     return this.economy.moneyNameSingle();
/*     */   }
/*     */ 
/*     */   public String format(double amount)
/*     */   {
/* 136 */     amount = Math.ceil(amount);
/* 137 */     if (amount == 1.0D) {
/* 138 */       return String.format("%d %s", new Object[] { Integer.valueOf((int)amount), currencyNameSingular() });
/*     */     }
/* 140 */     return String.format("%d %s", new Object[] { Integer.valueOf((int)amount), currencyNamePlural() });
/*     */   }
/*     */ 
/*     */   public EconomyResponse createBank(String name, String player)
/*     */   {
/* 146 */     return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "McMoney does not support bank accounts!");
/*     */   }
/*     */ 
/*     */   public EconomyResponse deleteBank(String name)
/*     */   {
/* 151 */     return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "McMoney does not support bank accounts!");
/*     */   }
/*     */ 
/*     */   public EconomyResponse bankHas(String name, double amount)
/*     */   {
/* 156 */     return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "McMoney does not support bank accounts!");
/*     */   }
/*     */ 
/*     */   public EconomyResponse bankWithdraw(String name, double amount)
/*     */   {
/* 161 */     return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "McMoney does not support bank accounts!");
/*     */   }
/*     */ 
/*     */   public EconomyResponse bankDeposit(String name, double amount)
/*     */   {
/* 166 */     return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "McMoney does not support bank accounts!");
/*     */   }
/*     */ 
/*     */   public boolean has(String playerName, double amount)
/*     */   {
/* 171 */     return getBalance(playerName) >= amount;
/*     */   }
/*     */ 
/*     */   public EconomyResponse isBankOwner(String name, String playerName)
/*     */   {
/* 176 */     return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "McMoney does not support bank accounts!");
/*     */   }
/*     */ 
/*     */   public EconomyResponse isBankMember(String name, String playerName)
/*     */   {
/* 181 */     return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "McMoney does not support bank accounts!");
/*     */   }
/*     */ 
/*     */   public EconomyResponse bankBalance(String name)
/*     */   {
/* 186 */     return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "McMoney does not support bank accounts!");
/*     */   }
/*     */ 
/*     */   public List<String> getBanks()
/*     */   {
/* 191 */     return new ArrayList();
/*     */   }
/*     */ 
/*     */   public boolean hasBankSupport()
/*     */   {
/* 196 */     return false;
/*     */   }
/*     */ 
/*     */   public boolean hasAccount(String playerName)
/*     */   {
/* 201 */     return this.economy.playerExists(playerName);
/*     */   }
/*     */ 
/*     */   public boolean createPlayerAccount(String playerName)
/*     */   {
/* 206 */     if (!hasAccount(playerName)) {
/* 207 */       this.economy.setMoney(playerName, 0.0D);
/* 208 */       return true;
/*     */     }
/* 210 */     return false;
/*     */   }
/*     */ 
/*     */   public int fractionalDigits()
/*     */   {
/* 215 */     return -1;
/*     */   }
/*     */ 
/*     */   public boolean hasAccount(String playerName, String worldName)
/*     */   {
/* 220 */     return hasAccount(playerName);
/*     */   }
/*     */ 
/*     */   public double getBalance(String playerName, String world)
/*     */   {
/* 225 */     return getBalance(playerName);
/*     */   }
/*     */ 
/*     */   public boolean has(String playerName, String worldName, double amount)
/*     */   {
/* 230 */     return has(playerName, amount);
/*     */   }
/*     */ 
/*     */   public EconomyResponse withdrawPlayer(String playerName, String worldName, double amount)
/*     */   {
/* 235 */     return withdrawPlayer(playerName, amount);
/*     */   }
/*     */ 
/*     */   public EconomyResponse depositPlayer(String playerName, String worldName, double amount)
/*     */   {
/* 240 */     return depositPlayer(playerName, amount);
/*     */   }
/*     */ 
/*     */   public boolean createPlayerAccount(String playerName, String worldName)
/*     */   {
/* 245 */     return createPlayerAccount(playerName);
/*     */   }
/*     */ 
/*     */   public class EconomyServerListener
/*     */     implements Listener
/*     */   {
/* 105 */     Economy_McMoney economy = null;
/*     */ 
/*     */     public EconomyServerListener(Economy_McMoney economy) {
/* 108 */       this.economy = economy;
/*     */     }
/*     */ 
/*     */     @EventHandler(priority=EventPriority.MONITOR)
/*     */     public void onPluginEnable(PluginEnableEvent event) {
/* 113 */       if (this.economy.economy == null) {
/* 114 */         Plugin eco = event.getPlugin();
/*     */ 
/* 116 */         if (eco.getDescription().getName().equals("McMoney")) {
/* 117 */           this.economy.economy = McMoneyAPI.getInstance();
/* 118 */           Economy_McMoney.log.info(String.format("[%s][Economy] %s hooked.", new Object[] { Economy_McMoney.this.plugin.getDescription().getName(), "McMoney" }));
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/*     */     @EventHandler(priority=EventPriority.MONITOR)
/*     */     public void onPluginDisable(PluginDisableEvent event) {
/* 125 */       if ((this.economy.economy != null) && 
/* 126 */         (event.getPlugin().getDescription().getName().equals("McMoney"))) {
/* 127 */         this.economy.economy = null;
/* 128 */         Economy_McMoney.log.info(String.format("[%s][Economy] %s unhooked.", new Object[] { Economy_McMoney.this.plugin.getDescription().getName(), "McMoney" }));
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\Vault.jar
 * Qualified Name:     net.milkbowl.vault.economy.plugins.Economy_McMoney
 * JD-Core Version:    0.6.2
 */