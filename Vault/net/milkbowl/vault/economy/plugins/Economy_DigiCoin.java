/*     */ package net.milkbowl.vault.economy.plugins;
/*     */ 
/*     */ import co.uk.silvania.cities.digicoin.DigiCoin;
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
/*     */ public class Economy_DigiCoin extends AbstractEconomy
/*     */ {
/*  36 */   private static final Logger log = Logger.getLogger("Minecraft");
/*     */ 
/*  38 */   private final String name = "DigiCoin";
/*  39 */   private Plugin plugin = null;
/*  40 */   private DigiCoin economy = null;
/*     */ 
/*     */   public Economy_DigiCoin(Plugin plugin) {
/*  43 */     this.plugin = plugin;
/*  44 */     Bukkit.getServer().getPluginManager().registerEvents(new EconomyServerListener(this), plugin);
/*     */ 
/*  46 */     if (this.economy == null) {
/*  47 */       Plugin digicoin = plugin.getServer().getPluginManager().getPlugin("DigiCoin");
/*     */ 
/*  49 */       if ((digicoin != null) && (digicoin.isEnabled())) {
/*  50 */         this.economy = ((DigiCoin)digicoin);
/*  51 */         log.info(String.format("[%s][Economy] %s hooked.", new Object[] { plugin.getDescription().getName(), "DigiCoin" }));
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
/*  93 */     return "DigiCoin";
/*     */   }
/*     */ 
/*     */   public boolean hasBankSupport()
/*     */   {
/*  98 */     return false;
/*     */   }
/*     */ 
/*     */   public int fractionalDigits()
/*     */   {
/* 103 */     return -1;
/*     */   }
/*     */ 
/*     */   public String format(double amount)
/*     */   {
/* 108 */     if (amount == 1.0D) {
/* 109 */       return String.format("%d %s", new Object[] { Double.valueOf(amount), currencyNameSingular() });
/*     */     }
/* 111 */     return String.format("%d %s", new Object[] { Double.valueOf(amount), currencyNamePlural() });
/*     */   }
/*     */ 
/*     */   public String currencyNamePlural()
/*     */   {
/* 117 */     return "coins";
/*     */   }
/*     */ 
/*     */   public String currencyNameSingular()
/*     */   {
/* 122 */     return "coin";
/*     */   }
/*     */ 
/*     */   public boolean hasAccount(String playerName)
/*     */   {
/* 127 */     return true;
/*     */   }
/*     */ 
/*     */   public double getBalance(String playerName)
/*     */   {
/* 132 */     return this.economy.getBalance(playerName);
/*     */   }
/*     */ 
/*     */   public boolean has(String playerName, double amount)
/*     */   {
/* 137 */     return getBalance(playerName) >= amount;
/*     */   }
/*     */ 
/*     */   public EconomyResponse withdrawPlayer(String playerName, double amount)
/*     */   {
/*     */     String message;
/*     */     EconomyResponse.ResponseType rt;
/*     */     String message;
/* 145 */     if (this.economy.removeBalance(playerName, amount).booleanValue()) {
/* 146 */       EconomyResponse.ResponseType rt = EconomyResponse.ResponseType.SUCCESS;
/* 147 */       message = null;
/*     */     } else {
/* 149 */       rt = EconomyResponse.ResponseType.FAILURE;
/* 150 */       message = "Not enough money.";
/*     */     }
/*     */ 
/* 153 */     return new EconomyResponse(amount, getBalance(playerName), rt, message);
/*     */   }
/*     */ 
/*     */   public EconomyResponse depositPlayer(String playerName, double amount)
/*     */   {
/*     */     String message;
/*     */     EconomyResponse.ResponseType rt;
/*     */     String message;
/* 161 */     if (this.economy.addBalance(playerName, amount).booleanValue()) {
/* 162 */       EconomyResponse.ResponseType rt = EconomyResponse.ResponseType.SUCCESS;
/* 163 */       message = null;
/*     */     } else {
/* 165 */       rt = EconomyResponse.ResponseType.FAILURE;
/* 166 */       message = "Failed to deposit balance.";
/*     */     }
/*     */ 
/* 169 */     return new EconomyResponse(amount, getBalance(playerName), rt, message);
/*     */   }
/*     */ 
/*     */   public EconomyResponse createBank(String name, String player)
/*     */   {
/* 174 */     return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "DigiCoin does not support bank accounts");
/*     */   }
/*     */ 
/*     */   public EconomyResponse deleteBank(String name)
/*     */   {
/* 179 */     return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "DigiCoin does not support bank accounts");
/*     */   }
/*     */ 
/*     */   public EconomyResponse bankBalance(String name)
/*     */   {
/* 184 */     return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "DigiCoin does not support bank accounts");
/*     */   }
/*     */ 
/*     */   public EconomyResponse bankHas(String name, double amount)
/*     */   {
/* 189 */     return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "DigiCoin does not support bank accounts");
/*     */   }
/*     */ 
/*     */   public EconomyResponse bankWithdraw(String name, double amount)
/*     */   {
/* 194 */     return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "DigiCoin does not support bank accounts");
/*     */   }
/*     */ 
/*     */   public EconomyResponse bankDeposit(String name, double amount)
/*     */   {
/* 199 */     return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "DigiCoin does not support bank accounts");
/*     */   }
/*     */ 
/*     */   public EconomyResponse isBankOwner(String name, String playerName)
/*     */   {
/* 204 */     return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "DigiCoin does not support bank accounts");
/*     */   }
/*     */ 
/*     */   public EconomyResponse isBankMember(String name, String playerName)
/*     */   {
/* 209 */     return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "DigiCoin does not support bank accounts");
/*     */   }
/*     */ 
/*     */   public List<String> getBanks()
/*     */   {
/* 214 */     return new ArrayList();
/*     */   }
/*     */ 
/*     */   public boolean createPlayerAccount(String playerName)
/*     */   {
/* 219 */     return false;
/*     */   }
/*     */ 
/*     */   public boolean hasAccount(String playerName, String worldName)
/*     */   {
/* 224 */     return true;
/*     */   }
/*     */ 
/*     */   public double getBalance(String playerName, String world)
/*     */   {
/* 229 */     return getBalance(playerName);
/*     */   }
/*     */ 
/*     */   public boolean has(String playerName, String worldName, double amount)
/*     */   {
/* 234 */     return has(playerName, amount);
/*     */   }
/*     */ 
/*     */   public EconomyResponse withdrawPlayer(String playerName, String worldName, double amount)
/*     */   {
/* 239 */     return withdrawPlayer(playerName, amount);
/*     */   }
/*     */ 
/*     */   public EconomyResponse depositPlayer(String playerName, String worldName, double amount)
/*     */   {
/* 244 */     return depositPlayer(playerName, amount);
/*     */   }
/*     */ 
/*     */   public boolean createPlayerAccount(String playerName, String worldName)
/*     */   {
/* 249 */     return false;
/*     */   }
/*     */ 
/*     */   public class EconomyServerListener
/*     */     implements Listener
/*     */   {
/*  57 */     Economy_DigiCoin economy = null;
/*     */ 
/*     */     public EconomyServerListener(Economy_DigiCoin economy) {
/*  60 */       this.economy = economy;
/*     */     }
/*     */ 
/*     */     @EventHandler(priority=EventPriority.MONITOR)
/*     */     public void onPluginEnable(PluginEnableEvent event) {
/*  65 */       if (this.economy.economy == null) {
/*  66 */         Plugin digicoin = event.getPlugin();
/*     */ 
/*  68 */         if (digicoin.getDescription().getName().equals("DigiCoin")) {
/*  69 */           this.economy.economy = ((DigiCoin)digicoin);
/*  70 */           Economy_DigiCoin.log.info(String.format("[%s][Economy] %s hooked.", new Object[] { Economy_DigiCoin.this.plugin.getDescription().getName(), "DigiCoin" }));
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/*     */     @EventHandler(priority=EventPriority.MONITOR)
/*     */     public void onPluginDisable(PluginDisableEvent event) {
/*  77 */       if ((this.economy.economy != null) && 
/*  78 */         (event.getPlugin().getDescription().getName().equals("DigiCoin"))) {
/*  79 */         this.economy.economy = null;
/*  80 */         Economy_DigiCoin.log.info(String.format("[%s][Economy] %s unhooked.", new Object[] { Economy_DigiCoin.this.plugin.getDescription().getName(), "DigiCoin" }));
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\Vault.jar
 * Qualified Name:     net.milkbowl.vault.economy.plugins.Economy_DigiCoin
 * JD-Core Version:    0.6.2
 */