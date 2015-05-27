/*     */ package net.milkbowl.vault.economy.plugins;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.logging.Logger;
/*     */ import me.mjolnir.mineconomy.MineConomy;
/*     */ import me.mjolnir.mineconomy.exceptions.AccountNameConflictException;
/*     */ import me.mjolnir.mineconomy.exceptions.NoAccountException;
/*     */ import me.mjolnir.mineconomy.internal.MCCom;
/*     */ import me.mjolnir.mineconomy.internal.util.MCFormat;
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
/*     */ public class Economy_MineConomy extends AbstractEconomy
/*     */ {
/*  40 */   private static final Logger log = Logger.getLogger("Minecraft");
/*     */ 
/*  42 */   private final String name = "MineConomy";
/*  43 */   private Plugin plugin = null;
/*  44 */   private MineConomy econ = null;
/*     */ 
/*     */   public Economy_MineConomy(Plugin plugin) {
/*  47 */     this.plugin = plugin;
/*  48 */     Bukkit.getServer().getPluginManager().registerEvents(new EconomyServerListener(this), plugin);
/*     */ 
/*  51 */     if (this.econ == null) {
/*  52 */       Plugin econ = plugin.getServer().getPluginManager().getPlugin("MineConomy");
/*  53 */       if ((econ != null) && (econ.isEnabled())) {
/*  54 */         this.econ = ((MineConomy)econ);
/*  55 */         log.info(String.format("[%s][Economy] %s hooked.", new Object[] { plugin.getDescription().getName(), "MineConomy" }));
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean isEnabled()
/*     */   {
/*  91 */     return this.econ != null;
/*     */   }
/*     */ 
/*     */   public String getName() {
/*  95 */     return "MineConomy";
/*     */   }
/*     */ 
/*     */   public String format(double amount) {
/*  99 */     return MCFormat.format(amount);
/*     */   }
/*     */ 
/*     */   public String currencyNameSingular() {
/* 103 */     return MCCom.getDefaultCurrency();
/*     */   }
/*     */ 
/*     */   public String currencyNamePlural() {
/* 107 */     return MCCom.getDefaultCurrency();
/*     */   }
/*     */ 
/*     */   public double getBalance(String playerName)
/*     */   {
/*     */     try {
/* 113 */       return MCCom.getExternalBalance(playerName);
/*     */     }
/*     */     catch (NoAccountException e)
/*     */     {
/* 117 */       MCCom.create(playerName);
/* 118 */     }return MCCom.getExternalBalance(playerName);
/*     */   }
/*     */ 
/*     */   public boolean has(String playerName, double amount)
/*     */   {
/*     */     try
/*     */     {
/* 125 */       return MCCom.canExternalAfford(playerName, amount);
/*     */     } catch (NoAccountException e) {
/* 127 */       MCCom.create(playerName);
/* 128 */     }return MCCom.canExternalAfford(playerName, amount);
/*     */   }
/*     */ 
/*     */   public EconomyResponse withdrawPlayer(String playerName, double amount)
/*     */   {
/*     */     double balance;
/*     */     try
/*     */     {
/* 136 */       balance = MCCom.getExternalBalance(playerName);
/*     */     } catch (NoAccountException e) {
/* 138 */       MCCom.create(playerName);
/* 139 */       balance = MCCom.getExternalBalance(playerName);
/*     */     }
/*     */ 
/* 142 */     if (amount < 0.0D) {
/* 143 */       return new EconomyResponse(0.0D, balance, EconomyResponse.ResponseType.FAILURE, "Cannot withdraw negative funds");
/*     */     }
/*     */ 
/* 146 */     if (balance >= amount) {
/* 147 */       double finalBalance = balance - amount;
/* 148 */       MCCom.setExternalBalance(playerName, finalBalance);
/* 149 */       return new EconomyResponse(amount, finalBalance, EconomyResponse.ResponseType.SUCCESS, null);
/*     */     }
/* 151 */     return new EconomyResponse(0.0D, balance, EconomyResponse.ResponseType.FAILURE, "Insufficient funds");
/*     */   }
/*     */ 
/*     */   public EconomyResponse depositPlayer(String playerName, double amount)
/*     */   {
/*     */     double balance;
/*     */     try
/*     */     {
/* 159 */       balance = MCCom.getExternalBalance(playerName);
/*     */     } catch (NoAccountException e) {
/* 161 */       MCCom.create(playerName);
/* 162 */       balance = MCCom.getExternalBalance(playerName);
/*     */     }
/* 164 */     if (amount < 0.0D) {
/* 165 */       return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.FAILURE, "Cannot deposit negative funds");
/*     */     }
/*     */ 
/* 168 */     balance += amount;
/* 169 */     MCCom.setExternalBalance(playerName, balance);
/* 170 */     return new EconomyResponse(amount, balance, EconomyResponse.ResponseType.SUCCESS, null);
/*     */   }
/*     */ 
/*     */   public EconomyResponse createBank(String name, String player)
/*     */   {
/* 176 */     return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "MineConomy does not support bank accounts!");
/*     */   }
/*     */ 
/*     */   public EconomyResponse deleteBank(String name)
/*     */   {
/* 181 */     return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "MineConomy does not support bank accounts!");
/*     */   }
/*     */ 
/*     */   public EconomyResponse bankHas(String name, double amount)
/*     */   {
/* 186 */     return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "MineConomy does not support bank accounts!");
/*     */   }
/*     */ 
/*     */   public EconomyResponse bankWithdraw(String name, double amount)
/*     */   {
/* 191 */     return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "MineConomy does not support bank accounts!");
/*     */   }
/*     */ 
/*     */   public EconomyResponse bankDeposit(String name, double amount)
/*     */   {
/* 196 */     return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "MineConomy does not support bank accounts!");
/*     */   }
/*     */ 
/*     */   public EconomyResponse isBankOwner(String name, String playerName)
/*     */   {
/* 201 */     return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "MineConomy does not support bank accounts!");
/*     */   }
/*     */ 
/*     */   public EconomyResponse isBankMember(String name, String playerName)
/*     */   {
/* 206 */     return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "MineConomy does not support bank accounts!");
/*     */   }
/*     */ 
/*     */   public EconomyResponse bankBalance(String name)
/*     */   {
/* 211 */     return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "MineConomy does not support bank accounts!");
/*     */   }
/*     */ 
/*     */   public List<String> getBanks()
/*     */   {
/* 216 */     return new ArrayList();
/*     */   }
/*     */ 
/*     */   public boolean hasBankSupport()
/*     */   {
/* 221 */     return false;
/*     */   }
/*     */ 
/*     */   public boolean hasAccount(String playerName)
/*     */   {
/* 226 */     return MCCom.exists(playerName);
/*     */   }
/*     */ 
/*     */   public boolean createPlayerAccount(String playerName) {
/*     */     try {
/* 231 */       MCCom.create(playerName);
/* 232 */       return true; } catch (AccountNameConflictException e) {
/*     */     }
/* 234 */     return false;
/*     */   }
/*     */ 
/*     */   public int fractionalDigits()
/*     */   {
/* 240 */     return 2;
/*     */   }
/*     */ 
/*     */   public boolean hasAccount(String playerName, String worldName)
/*     */   {
/* 245 */     return hasAccount(playerName);
/*     */   }
/*     */ 
/*     */   public double getBalance(String playerName, String world)
/*     */   {
/* 250 */     return getBalance(playerName);
/*     */   }
/*     */ 
/*     */   public boolean has(String playerName, String worldName, double amount)
/*     */   {
/* 255 */     return has(playerName, amount);
/*     */   }
/*     */ 
/*     */   public EconomyResponse withdrawPlayer(String playerName, String worldName, double amount)
/*     */   {
/* 260 */     return withdrawPlayer(playerName, amount);
/*     */   }
/*     */ 
/*     */   public EconomyResponse depositPlayer(String playerName, String worldName, double amount)
/*     */   {
/* 265 */     return depositPlayer(playerName, amount);
/*     */   }
/*     */ 
/*     */   public boolean createPlayerAccount(String playerName, String worldName)
/*     */   {
/* 270 */     return createPlayerAccount(playerName);
/*     */   }
/*     */ 
/*     */   public class EconomyServerListener
/*     */     implements Listener
/*     */   {
/*  61 */     Economy_MineConomy economy = null;
/*     */ 
/*     */     public EconomyServerListener(Economy_MineConomy economy) {
/*  64 */       this.economy = economy;
/*     */     }
/*     */ 
/*     */     @EventHandler(priority=EventPriority.MONITOR)
/*     */     public void onPluginEnable(PluginEnableEvent event) {
/*  69 */       if (this.economy.econ == null) {
/*  70 */         Plugin eco = event.getPlugin();
/*     */ 
/*  72 */         if (eco.getDescription().getName().equals("MineConomy")) {
/*  73 */           this.economy.econ = ((MineConomy)eco);
/*  74 */           Economy_MineConomy.log.info(String.format("[%s][Economy] %s hooked.", new Object[] { Economy_MineConomy.this.plugin.getDescription().getName(), "MineConomy" }));
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/*     */     @EventHandler(priority=EventPriority.MONITOR)
/*     */     public void onPluginDisable(PluginDisableEvent event) {
/*  81 */       if ((this.economy.econ != null) && 
/*  82 */         (event.getPlugin().getDescription().getName().equals("MineConomy"))) {
/*  83 */         this.economy.econ = null;
/*  84 */         Economy_MineConomy.log.info(String.format("[%s][Economy] %s unhooked.", new Object[] { Economy_MineConomy.this.plugin.getDescription().getName(), "MineConomy" }));
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\Vault.jar
 * Qualified Name:     net.milkbowl.vault.economy.plugins.Economy_MineConomy
 * JD-Core Version:    0.6.2
 */