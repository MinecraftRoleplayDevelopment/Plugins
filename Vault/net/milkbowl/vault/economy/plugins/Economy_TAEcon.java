/*     */ package net.milkbowl.vault.economy.plugins;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.logging.Logger;
/*     */ import net.milkbowl.vault.economy.AbstractEconomy;
/*     */ import net.milkbowl.vault.economy.EconomyResponse;
/*     */ import net.milkbowl.vault.economy.EconomyResponse.ResponseType;
/*     */ import net.teamalpha.taecon.TAEcon;
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
/*     */ public class Economy_TAEcon extends AbstractEconomy
/*     */ {
/*  36 */   private static final Logger log = Logger.getLogger("Minecraft");
/*     */ 
/*  38 */   private final String name = "TAEcon";
/*  39 */   private Plugin plugin = null;
/*  40 */   private TAEcon economy = null;
/*     */ 
/*     */   public Economy_TAEcon(Plugin plugin) {
/*  43 */     this.plugin = plugin;
/*  44 */     Bukkit.getServer().getPluginManager().registerEvents(new EconomyServerListener(this), plugin);
/*     */ 
/*  46 */     if (this.economy == null) {
/*  47 */       Plugin taecon = plugin.getServer().getPluginManager().getPlugin("TAEcon");
/*     */ 
/*  49 */       if ((taecon != null) && (taecon.isEnabled())) {
/*  50 */         this.economy = ((TAEcon)taecon);
/*  51 */         log.info(String.format("[%s][Economy] %s hooked.", new Object[] { plugin.getDescription().getName(), "TAEcon" }));
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean isEnabled()
/*     */   {
/*  88 */     return this.economy != null;
/*     */   }
/*     */ 
/*     */   public String getName()
/*     */   {
/*  93 */     return "TAEcon";
/*     */   }
/*     */ 
/*     */   public boolean hasBankSupport()
/*     */   {
/*  98 */     return false;
/*     */   }
/*     */ 
/*     */   public int fractionalDigits()
/*     */   {
/* 103 */     return 0;
/*     */   }
/*     */ 
/*     */   public String format(double amount)
/*     */   {
/* 108 */     amount = Math.ceil(amount);
/* 109 */     if (amount == 1.0D) {
/* 110 */       return String.format("%d %s", new Object[] { Integer.valueOf((int)amount), currencyNameSingular() });
/*     */     }
/* 112 */     return String.format("%d %s", new Object[] { Integer.valueOf((int)amount), currencyNamePlural() });
/*     */   }
/*     */ 
/*     */   public String currencyNamePlural()
/*     */   {
/* 118 */     return this.economy.getCurrencyName(Boolean.valueOf(true));
/*     */   }
/*     */ 
/*     */   public String currencyNameSingular()
/*     */   {
/* 123 */     return this.economy.getCurrencyName(Boolean.valueOf(false));
/*     */   }
/*     */ 
/*     */   public boolean hasAccount(String playerName)
/*     */   {
/* 128 */     return true;
/*     */   }
/*     */ 
/*     */   public double getBalance(String playerName)
/*     */   {
/* 133 */     return this.economy.getBalance(playerName);
/*     */   }
/*     */ 
/*     */   public boolean has(String playerName, double amount)
/*     */   {
/* 138 */     return getBalance(playerName) >= amount;
/*     */   }
/*     */ 
/*     */   public EconomyResponse withdrawPlayer(String playerName, double amount)
/*     */   {
/* 145 */     int iamount = (int)Math.ceil(amount);
/*     */     String message;
/*     */     EconomyResponse.ResponseType rt;
/*     */     String message;
/* 147 */     if (has(playerName, amount))
/*     */     {
/*     */       String message;
/* 148 */       if (this.economy.removeBalance(playerName, iamount).booleanValue()) {
/* 149 */         EconomyResponse.ResponseType rt = EconomyResponse.ResponseType.SUCCESS;
/* 150 */         message = null;
/*     */       } else {
/* 152 */         EconomyResponse.ResponseType rt = EconomyResponse.ResponseType.SUCCESS;
/* 153 */         message = "ERROR";
/*     */       }
/*     */     } else {
/* 156 */       rt = EconomyResponse.ResponseType.FAILURE;
/* 157 */       message = "Not enough money";
/*     */     }
/*     */ 
/* 160 */     return new EconomyResponse(iamount, getBalance(playerName), rt, message);
/*     */   }
/*     */ 
/*     */   public EconomyResponse depositPlayer(String playerName, double amount)
/*     */   {
/* 167 */     int iamount = (int)Math.floor(amount);
/*     */     String message;
/*     */     EconomyResponse.ResponseType rt;
/*     */     String message;
/* 169 */     if (this.economy.addBalance(playerName, iamount).booleanValue()) {
/* 170 */       EconomyResponse.ResponseType rt = EconomyResponse.ResponseType.SUCCESS;
/* 171 */       message = null;
/*     */     } else {
/* 173 */       rt = EconomyResponse.ResponseType.SUCCESS;
/* 174 */       message = "ERROR";
/*     */     }
/*     */ 
/* 177 */     return new EconomyResponse(iamount, getBalance(playerName), rt, message);
/*     */   }
/*     */ 
/*     */   public EconomyResponse createBank(String name, String player)
/*     */   {
/* 182 */     return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "TAEcon does not support bank accounts");
/*     */   }
/*     */ 
/*     */   public EconomyResponse deleteBank(String name)
/*     */   {
/* 187 */     return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "TAEcon does not support bank accounts");
/*     */   }
/*     */ 
/*     */   public EconomyResponse bankBalance(String name)
/*     */   {
/* 192 */     return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "TAEcon does not support bank accounts");
/*     */   }
/*     */ 
/*     */   public EconomyResponse bankHas(String name, double amount)
/*     */   {
/* 197 */     return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "TAEcon does not support bank accounts");
/*     */   }
/*     */ 
/*     */   public EconomyResponse bankWithdraw(String name, double amount)
/*     */   {
/* 202 */     return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "TAEcon does not support bank accounts");
/*     */   }
/*     */ 
/*     */   public EconomyResponse bankDeposit(String name, double amount)
/*     */   {
/* 207 */     return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "TAEcon does not support bank accounts");
/*     */   }
/*     */ 
/*     */   public EconomyResponse isBankOwner(String name, String playerName)
/*     */   {
/* 212 */     return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "TAEcon does not support bank accounts");
/*     */   }
/*     */ 
/*     */   public EconomyResponse isBankMember(String name, String playerName)
/*     */   {
/* 217 */     return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "TAEcon does not support bank accounts");
/*     */   }
/*     */ 
/*     */   public List<String> getBanks()
/*     */   {
/* 222 */     return new ArrayList();
/*     */   }
/*     */ 
/*     */   public boolean createPlayerAccount(String playerName)
/*     */   {
/* 227 */     return false;
/*     */   }
/*     */ 
/*     */   public boolean hasAccount(String playerName, String worldName)
/*     */   {
/* 232 */     return true;
/*     */   }
/*     */ 
/*     */   public double getBalance(String playerName, String world)
/*     */   {
/* 237 */     return getBalance(playerName);
/*     */   }
/*     */ 
/*     */   public boolean has(String playerName, String worldName, double amount)
/*     */   {
/* 242 */     return has(playerName, amount);
/*     */   }
/*     */ 
/*     */   public EconomyResponse withdrawPlayer(String playerName, String worldName, double amount)
/*     */   {
/* 247 */     return withdrawPlayer(playerName, amount);
/*     */   }
/*     */ 
/*     */   public EconomyResponse depositPlayer(String playerName, String worldName, double amount)
/*     */   {
/* 252 */     return depositPlayer(playerName, amount);
/*     */   }
/*     */ 
/*     */   public boolean createPlayerAccount(String playerName, String worldName)
/*     */   {
/* 257 */     return false;
/*     */   }
/*     */ 
/*     */   public class EconomyServerListener
/*     */     implements Listener
/*     */   {
/*  57 */     Economy_TAEcon economy = null;
/*     */ 
/*     */     public EconomyServerListener(Economy_TAEcon economy) {
/*  60 */       this.economy = economy;
/*     */     }
/*     */ 
/*     */     @EventHandler(priority=EventPriority.MONITOR)
/*     */     public void onPluginEnable(PluginEnableEvent event) {
/*  65 */       if (this.economy.economy == null) {
/*  66 */         Plugin taecon = event.getPlugin();
/*     */ 
/*  68 */         if (taecon.getDescription().getName().equals("TAEcon")) {
/*  69 */           this.economy.economy = ((TAEcon)taecon);
/*  70 */           Economy_TAEcon.log.info(String.format("[%s][Economy] %s hooked.", new Object[] { Economy_TAEcon.this.plugin.getDescription().getName(), "TAEcon" }));
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/*     */     @EventHandler(priority=EventPriority.MONITOR)
/*     */     public void onPluginDisable(PluginDisableEvent event) {
/*  77 */       if ((this.economy.economy != null) && 
/*  78 */         (event.getPlugin().getDescription().getName().equals("TAEcon"))) {
/*  79 */         this.economy.economy = null;
/*  80 */         Economy_TAEcon.log.info(String.format("[%s][Economy] %s unhooked.", new Object[] { Economy_TAEcon.this.plugin.getDescription().getName(), "TAEcon" }));
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\Vault.jar
 * Qualified Name:     net.milkbowl.vault.economy.plugins.Economy_TAEcon
 * JD-Core Version:    0.6.2
 */