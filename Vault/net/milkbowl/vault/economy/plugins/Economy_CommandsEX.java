/*     */ package net.milkbowl.vault.economy.plugins;
/*     */ 
/*     */ import com.github.zathrus_writer.commandsex.CommandsEX;
/*     */ import com.github.zathrus_writer.commandsex.api.economy.Economy;
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
/*     */ public class Economy_CommandsEX extends AbstractEconomy
/*     */ {
/*  23 */   private static final Logger log = Logger.getLogger("Minecraft");
/*     */ 
/*  25 */   private final String name = "CommandsEX Economy";
/*  26 */   private Plugin plugin = null;
/*  27 */   private CommandsEX economy = null;
/*     */ 
/*     */   public Economy_CommandsEX(Plugin plugin) {
/*  30 */     this.plugin = plugin;
/*  31 */     Bukkit.getServer().getPluginManager().registerEvents(new EconomyServerListener(this), plugin);
/*     */ 
/*  33 */     if (this.economy == null) {
/*  34 */       Plugin commandsex = plugin.getServer().getPluginManager().getPlugin("CommandsEX");
/*     */ 
/*  36 */       if ((commandsex != null) && (commandsex.isEnabled())) {
/*  37 */         this.economy = ((CommandsEX)commandsex);
/*  38 */         log.info(String.format("[%s][Economy] %s hooked.", new Object[] { plugin.getDescription().getName(), "CommandsEX Economy" }));
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean isEnabled()
/*     */   {
/*  75 */     if (this.economy == null) {
/*  76 */       return false;
/*     */     }
/*  78 */     return Economy.isEnabled();
/*     */   }
/*     */ 
/*     */   public String getName()
/*     */   {
/*  84 */     return "CommandsEX Economy";
/*     */   }
/*     */ 
/*     */   public boolean hasBankSupport()
/*     */   {
/*  89 */     return false;
/*     */   }
/*     */ 
/*     */   public int fractionalDigits()
/*     */   {
/*  94 */     return 2;
/*     */   }
/*     */ 
/*     */   public String format(double amount)
/*     */   {
/*  99 */     return Economy.getCurrencySymbol() + amount;
/*     */   }
/*     */ 
/*     */   public String currencyNamePlural()
/*     */   {
/* 104 */     return Economy.getCurrencyPlural();
/*     */   }
/*     */ 
/*     */   public String currencyNameSingular()
/*     */   {
/* 109 */     return Economy.getCurrencySingular();
/*     */   }
/*     */ 
/*     */   public boolean hasAccount(String playerName)
/*     */   {
/* 114 */     return Economy.hasAccount(playerName);
/*     */   }
/*     */ 
/*     */   public double getBalance(String playerName)
/*     */   {
/* 119 */     return Economy.getBalance(playerName);
/*     */   }
/*     */ 
/*     */   public boolean has(String playerName, double amount)
/*     */   {
/* 124 */     return Economy.has(playerName, amount);
/*     */   }
/*     */ 
/*     */   public EconomyResponse withdrawPlayer(String playerName, double amount)
/*     */   {
/*     */     String message;
/*     */     EconomyResponse.ResponseType rt;
/*     */     String message;
/* 132 */     if (Economy.has(playerName, amount)) {
/* 133 */       Economy.withdraw(playerName, amount);
/* 134 */       EconomyResponse.ResponseType rt = EconomyResponse.ResponseType.SUCCESS;
/* 135 */       message = null;
/*     */     } else {
/* 137 */       rt = EconomyResponse.ResponseType.FAILURE;
/* 138 */       message = "Not enough money";
/*     */     }
/*     */ 
/* 141 */     return new EconomyResponse(amount, Economy.getBalance(playerName), rt, message);
/*     */   }
/*     */ 
/*     */   public EconomyResponse depositPlayer(String playerName, double amount)
/*     */   {
/* 146 */     Economy.deposit(playerName, amount);
/* 147 */     return new EconomyResponse(amount, Economy.getBalance(playerName), EconomyResponse.ResponseType.SUCCESS, "Successfully deposited");
/*     */   }
/*     */ 
/*     */   public EconomyResponse createBank(String name, String player)
/*     */   {
/* 152 */     return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "CommandsEX Economy does not support bank accounts");
/*     */   }
/*     */ 
/*     */   public EconomyResponse deleteBank(String name)
/*     */   {
/* 157 */     return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "CommandsEX Economy does not support bank accounts");
/*     */   }
/*     */ 
/*     */   public EconomyResponse bankBalance(String name)
/*     */   {
/* 162 */     return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "CommandsEX Economy does not support bank accounts");
/*     */   }
/*     */ 
/*     */   public EconomyResponse bankHas(String name, double amount)
/*     */   {
/* 167 */     return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "CommandsEX Economy does not support bank accounts");
/*     */   }
/*     */ 
/*     */   public EconomyResponse bankWithdraw(String name, double amount)
/*     */   {
/* 172 */     return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "CommandsEX Economy does not support bank accounts");
/*     */   }
/*     */ 
/*     */   public EconomyResponse bankDeposit(String name, double amount)
/*     */   {
/* 177 */     return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "CommandsEX Economy does not support bank accounts");
/*     */   }
/*     */ 
/*     */   public EconomyResponse isBankOwner(String name, String playerName)
/*     */   {
/* 182 */     return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "CommandsEX Economy does not support bank accounts");
/*     */   }
/*     */ 
/*     */   public EconomyResponse isBankMember(String name, String playerName)
/*     */   {
/* 187 */     return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "CommandsEX Economy does not support bank accounts");
/*     */   }
/*     */ 
/*     */   public List<String> getBanks()
/*     */   {
/* 192 */     return new ArrayList();
/*     */   }
/*     */ 
/*     */   public boolean createPlayerAccount(String playerName)
/*     */   {
/* 197 */     if (Economy.hasAccount(playerName)) {
/* 198 */       return false;
/*     */     }
/* 200 */     Economy.createAccount(playerName);
/* 201 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean hasAccount(String playerName, String worldName)
/*     */   {
/* 207 */     return hasAccount(playerName);
/*     */   }
/*     */ 
/*     */   public double getBalance(String playerName, String world)
/*     */   {
/* 212 */     return getBalance(playerName);
/*     */   }
/*     */ 
/*     */   public boolean has(String playerName, String worldName, double amount)
/*     */   {
/* 217 */     return has(playerName, amount);
/*     */   }
/*     */ 
/*     */   public EconomyResponse withdrawPlayer(String playerName, String worldName, double amount)
/*     */   {
/* 222 */     return withdrawPlayer(playerName, amount);
/*     */   }
/*     */ 
/*     */   public EconomyResponse depositPlayer(String playerName, String worldName, double amount)
/*     */   {
/* 227 */     return depositPlayer(playerName, amount);
/*     */   }
/*     */ 
/*     */   public boolean createPlayerAccount(String playerName, String worldName)
/*     */   {
/* 232 */     return createPlayerAccount(playerName);
/*     */   }
/*     */ 
/*     */   public class EconomyServerListener
/*     */     implements Listener
/*     */   {
/*  44 */     Economy_CommandsEX economy = null;
/*     */ 
/*     */     public EconomyServerListener(Economy_CommandsEX economy) {
/*  47 */       this.economy = economy;
/*     */     }
/*     */ 
/*     */     @EventHandler(priority=EventPriority.MONITOR)
/*     */     public void onPluginEnable(PluginEnableEvent event) {
/*  52 */       if (this.economy.economy == null) {
/*  53 */         Plugin cex = event.getPlugin();
/*     */ 
/*  55 */         if (cex.getDescription().getName().equals("CommandsEX")) {
/*  56 */           this.economy.economy = ((CommandsEX)cex);
/*  57 */           Economy_CommandsEX.log.info(String.format("[%s][Economy] %s hooked.", new Object[] { Economy_CommandsEX.this.plugin.getDescription().getName(), "CommandsEX Economy" }));
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/*     */     @EventHandler(priority=EventPriority.MONITOR)
/*     */     public void onPluginDisable(PluginDisableEvent event) {
/*  64 */       if ((this.economy.economy != null) && 
/*  65 */         (event.getPlugin().getDescription().getName().equals("CommandsEX"))) {
/*  66 */         this.economy.economy = null;
/*  67 */         Economy_CommandsEX.log.info(String.format("[%s][Economy] %s unhooked.", new Object[] { Economy_CommandsEX.this.plugin.getDescription().getName(), "CommandsEX Economy" }));
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\Vault.jar
 * Qualified Name:     net.milkbowl.vault.economy.plugins.Economy_CommandsEX
 * JD-Core Version:    0.6.2
 */