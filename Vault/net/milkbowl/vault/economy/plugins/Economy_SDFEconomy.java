/*     */ package net.milkbowl.vault.economy.plugins;
/*     */ 
/*     */ import com.github.omwah.SDFEconomy.SDFEconomy;
/*     */ import com.github.omwah.SDFEconomy.SDFEconomyAPI;
/*     */ import java.util.List;
/*     */ import java.util.logging.Logger;
/*     */ import net.milkbowl.vault.economy.AbstractEconomy;
/*     */ import net.milkbowl.vault.economy.EconomyResponse;
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
/*     */ public class Economy_SDFEconomy extends AbstractEconomy
/*     */ {
/*  35 */   private static final Logger log = Logger.getLogger("Minecraft");
/*  36 */   private Plugin plugin = null;
/*     */ 
/*  38 */   private final String name = "SDFEconomy";
/*  39 */   private SDFEconomyAPI api = null;
/*     */ 
/*     */   public Economy_SDFEconomy(Plugin _plugin) {
/*  42 */     this.plugin = _plugin;
/*     */ 
/*  45 */     this.plugin.getServer().getPluginManager().registerEvents(new EconomyServerListener(this), this.plugin);
/*     */ 
/*  48 */     load_api();
/*     */   }
/*     */ 
/*     */   public void load_api() {
/*  52 */     SDFEconomy pluginSDF = (SDFEconomy)this.plugin.getServer().getPluginManager().getPlugin("SDFEconomy");
/*  53 */     if ((!isEnabled()) && (pluginSDF != null)) {
/*  54 */       this.api = pluginSDF.getAPI();
/*  55 */       log.info(String.format("[%s][Economy] %s hooked.", new Object[] { this.plugin.getDescription().getName(), "SDFEconomy" }));
/*     */     }
/*     */   }
/*     */ 
/*     */   public void unload_api() {
/*  60 */     SDFEconomy pluginSDF = (SDFEconomy)this.plugin.getServer().getPluginManager().getPlugin("SDFEconomy");
/*  61 */     if ((isEnabled()) && (pluginSDF != null)) {
/*  62 */       this.api = null;
/*  63 */       log.info(String.format("[%s][Economy] %s unhooked.", new Object[] { this.plugin.getDescription().getName(), "SDFEconomy" }));
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean isEnabled()
/*     */   {
/*  92 */     return this.api != null;
/*     */   }
/*     */ 
/*     */   public String getName()
/*     */   {
/*  97 */     return "SDFEconomy";
/*     */   }
/*     */ 
/*     */   public boolean hasBankSupport()
/*     */   {
/* 102 */     return this.api.hasBankSupport();
/*     */   }
/*     */ 
/*     */   public int fractionalDigits()
/*     */   {
/* 107 */     return this.api.fractionalDigits();
/*     */   }
/*     */ 
/*     */   public String format(double amount)
/*     */   {
/* 112 */     return this.api.format(amount);
/*     */   }
/*     */ 
/*     */   public String currencyNamePlural()
/*     */   {
/* 117 */     return this.api.currencyNamePlural();
/*     */   }
/*     */ 
/*     */   public String currencyNameSingular()
/*     */   {
/* 122 */     return this.api.currencyNameSingular();
/*     */   }
/*     */ 
/*     */   public boolean hasAccount(String playerName)
/*     */   {
/* 127 */     return this.api.hasAccount(playerName);
/*     */   }
/*     */ 
/*     */   public double getBalance(String playerName)
/*     */   {
/* 132 */     return this.api.getBalance(playerName);
/*     */   }
/*     */ 
/*     */   public boolean has(String playerName, double amount)
/*     */   {
/* 137 */     return this.api.has(playerName, amount);
/*     */   }
/*     */ 
/*     */   public EconomyResponse withdrawPlayer(String playerName, double amount)
/*     */   {
/* 142 */     return this.api.withdrawPlayer(playerName, amount);
/*     */   }
/*     */ 
/*     */   public EconomyResponse depositPlayer(String playerName, double amount)
/*     */   {
/* 147 */     return this.api.depositPlayer(playerName, amount);
/*     */   }
/*     */ 
/*     */   public EconomyResponse createBank(String name, String player)
/*     */   {
/* 152 */     return this.api.createBank(name, player);
/*     */   }
/*     */ 
/*     */   public EconomyResponse deleteBank(String name)
/*     */   {
/* 157 */     return this.api.deleteBank(name);
/*     */   }
/*     */ 
/*     */   public EconomyResponse bankBalance(String name)
/*     */   {
/* 162 */     return this.api.bankBalance(name);
/*     */   }
/*     */ 
/*     */   public EconomyResponse bankHas(String name, double amount)
/*     */   {
/* 167 */     return this.api.bankHas(name, amount);
/*     */   }
/*     */ 
/*     */   public EconomyResponse bankWithdraw(String name, double amount)
/*     */   {
/* 172 */     return this.api.bankWithdraw(name, amount);
/*     */   }
/*     */ 
/*     */   public EconomyResponse bankDeposit(String name, double amount)
/*     */   {
/* 177 */     return this.api.bankDeposit(name, amount);
/*     */   }
/*     */ 
/*     */   public EconomyResponse isBankOwner(String name, String playerName)
/*     */   {
/* 182 */     return this.api.isBankOwner(name, playerName);
/*     */   }
/*     */ 
/*     */   public EconomyResponse isBankMember(String name, String playerName)
/*     */   {
/* 187 */     return this.api.isBankMember(name, playerName);
/*     */   }
/*     */ 
/*     */   public List<String> getBanks()
/*     */   {
/* 192 */     return this.api.getBankNames();
/*     */   }
/*     */ 
/*     */   public boolean createPlayerAccount(String playerName)
/*     */   {
/* 197 */     return this.api.createPlayerAccount(playerName);
/*     */   }
/*     */ 
/*     */   public boolean hasAccount(String playerName, String worldName)
/*     */   {
/* 202 */     return hasAccount(playerName);
/*     */   }
/*     */ 
/*     */   public double getBalance(String playerName, String world)
/*     */   {
/* 207 */     return getBalance(playerName);
/*     */   }
/*     */ 
/*     */   public boolean has(String playerName, String worldName, double amount)
/*     */   {
/* 212 */     return has(playerName, amount);
/*     */   }
/*     */ 
/*     */   public EconomyResponse withdrawPlayer(String playerName, String worldName, double amount)
/*     */   {
/* 217 */     return withdrawPlayer(playerName, amount);
/*     */   }
/*     */ 
/*     */   public EconomyResponse depositPlayer(String playerName, String worldName, double amount)
/*     */   {
/* 222 */     return depositPlayer(playerName, amount);
/*     */   }
/*     */ 
/*     */   public boolean createPlayerAccount(String playerName, String worldName)
/*     */   {
/* 227 */     return createPlayerAccount(playerName);
/*     */   }
/*     */ 
/*     */   public class EconomyServerListener
/*     */     implements Listener
/*     */   {
/*  68 */     Economy_SDFEconomy economy = null;
/*     */ 
/*     */     public EconomyServerListener(Economy_SDFEconomy economy) {
/*  71 */       this.economy = economy;
/*     */     }
/*     */ 
/*     */     @EventHandler(priority=EventPriority.MONITOR)
/*     */     public void onPluginEnable(PluginEnableEvent event) {
/*  76 */       if (event.getPlugin().getDescription().getName().equals("SDFEconomy"))
/*  77 */         this.economy.load_api();
/*     */     }
/*     */ 
/*     */     @EventHandler(priority=EventPriority.MONITOR)
/*     */     public void onPluginDisable(PluginDisableEvent event)
/*     */     {
/*  83 */       if (event.getPlugin().getDescription().getName().equals("SDFEconomy"))
/*  84 */         this.economy.unload_api();
/*     */     }
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\Vault.jar
 * Qualified Name:     net.milkbowl.vault.economy.plugins.Economy_SDFEconomy
 * JD-Core Version:    0.6.2
 */