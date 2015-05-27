/*     */ package net.milkbowl.vault.economy.plugins;
/*     */ 
/*     */ import ca.agnate.EconXP.EconXP;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.logging.Level;
/*     */ import java.util.logging.Logger;
/*     */ import net.milkbowl.vault.economy.AbstractEconomy;
/*     */ import net.milkbowl.vault.economy.EconomyResponse;
/*     */ import net.milkbowl.vault.economy.EconomyResponse.ResponseType;
/*     */ import org.bukkit.Bukkit;
/*     */ import org.bukkit.OfflinePlayer;
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
/*     */ public class Economy_EconXP extends AbstractEconomy
/*     */ {
/*  39 */   private static final Logger log = Logger.getLogger("Minecraft");
/*     */ 
/*  41 */   private final String name = "EconXP";
/*  42 */   private Plugin plugin = null;
/*  43 */   private EconXP econ = null;
/*     */ 
/*     */   public Economy_EconXP(Plugin plugin) {
/*  46 */     this.plugin = plugin;
/*  47 */     Bukkit.getServer().getPluginManager().registerEvents(new EconomyServerListener(this), plugin);
/*  48 */     log.log(Level.WARNING, "EconXP is an integer only economy, you may notice inconsistencies with accounts if you do not setup your other econ using plugins accordingly!");
/*     */ 
/*  50 */     if (this.econ == null) {
/*  51 */       Plugin econ = plugin.getServer().getPluginManager().getPlugin("EconXP");
/*  52 */       if ((econ != null) && (econ.isEnabled())) {
/*  53 */         this.econ = ((EconXP)econ);
/*  54 */         log.info(String.format("[%s][Economy] %s hooked.", new Object[] { plugin.getDescription().getName(), "EconXP" }));
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean isEnabled()
/*     */   {
/*  91 */     return this.econ != null;
/*     */   }
/*     */ 
/*     */   public String getName()
/*     */   {
/*  96 */     return "EconXP";
/*     */   }
/*     */ 
/*     */   public String format(double amount)
/*     */   {
/* 101 */     amount = Math.ceil(amount);
/*     */ 
/* 103 */     return String.format("%d %s", new Object[] { Integer.valueOf((int)amount), "experience" });
/*     */   }
/*     */ 
/*     */   public String currencyNamePlural()
/*     */   {
/* 108 */     return "experience";
/*     */   }
/*     */ 
/*     */   public String currencyNameSingular()
/*     */   {
/* 113 */     return "experience";
/*     */   }
/*     */ 
/*     */   public double getBalance(String playerName)
/*     */   {
/* 118 */     OfflinePlayer player = this.econ.getPlayer(playerName);
/*     */ 
/* 120 */     if (player == null) return 0.0D;
/*     */ 
/* 122 */     return this.econ.getExp(player);
/*     */   }
/*     */ 
/*     */   public boolean has(String playerName, double amount)
/*     */   {
/* 127 */     OfflinePlayer player = this.econ.getPlayer(playerName);
/*     */ 
/* 129 */     if (player == null) return false;
/*     */ 
/* 131 */     return this.econ.hasExp(player, (int)Math.ceil(amount));
/*     */   }
/*     */ 
/*     */   public EconomyResponse withdrawPlayer(String playerName, double amount)
/*     */   {
/* 136 */     OfflinePlayer player = this.econ.getPlayer(playerName);
/*     */ 
/* 138 */     if (player == null) {
/* 139 */       return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.FAILURE, "Player does not exist");
/*     */     }
/*     */ 
/* 142 */     double balance = this.econ.getExp(player);
/* 143 */     amount = Math.ceil(amount);
/*     */ 
/* 145 */     if (amount < 0.0D) {
/* 146 */       return new EconomyResponse(0.0D, balance, EconomyResponse.ResponseType.FAILURE, "Cannot withdraw negative funds");
/*     */     }
/*     */ 
/* 149 */     if (!this.econ.hasExp(player, (int)amount)) {
/* 150 */       return new EconomyResponse(0.0D, balance, EconomyResponse.ResponseType.FAILURE, "Insufficient funds");
/*     */     }
/*     */ 
/* 153 */     this.econ.removeExp(player, (int)amount);
/*     */ 
/* 155 */     double finalBalance = this.econ.getExp(player);
/*     */ 
/* 157 */     return new EconomyResponse(amount, finalBalance, EconomyResponse.ResponseType.SUCCESS, null);
/*     */   }
/*     */ 
/*     */   public EconomyResponse depositPlayer(String playerName, double amount)
/*     */   {
/* 162 */     OfflinePlayer player = this.econ.getPlayer(playerName);
/*     */ 
/* 164 */     if (player == null) {
/* 165 */       return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.FAILURE, "Player does not exist");
/*     */     }
/*     */ 
/* 168 */     double balance = this.econ.getExp(player);
/* 169 */     amount = Math.ceil(amount);
/*     */ 
/* 171 */     if (amount < 0.0D) {
/* 172 */       return new EconomyResponse(0.0D, balance, EconomyResponse.ResponseType.FAILURE, "Cannot withdraw negative funds");
/*     */     }
/*     */ 
/* 175 */     this.econ.addExp(player, (int)amount);
/* 176 */     balance = this.econ.getExp(player);
/*     */ 
/* 178 */     return new EconomyResponse(amount, balance, EconomyResponse.ResponseType.SUCCESS, null);
/*     */   }
/*     */ 
/*     */   public EconomyResponse createBank(String name, String player)
/*     */   {
/* 183 */     return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "EconXP does not support bank accounts!");
/*     */   }
/*     */ 
/*     */   public EconomyResponse deleteBank(String name)
/*     */   {
/* 188 */     return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "EconXP does not support bank accounts!");
/*     */   }
/*     */ 
/*     */   public EconomyResponse bankHas(String name, double amount)
/*     */   {
/* 193 */     return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "EconXP does not support bank accounts!");
/*     */   }
/*     */ 
/*     */   public EconomyResponse bankWithdraw(String name, double amount)
/*     */   {
/* 198 */     return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "EconXP does not support bank accounts!");
/*     */   }
/*     */ 
/*     */   public EconomyResponse bankDeposit(String name, double amount)
/*     */   {
/* 203 */     return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "EconXP does not support bank accounts!");
/*     */   }
/*     */ 
/*     */   public EconomyResponse isBankOwner(String name, String playerName)
/*     */   {
/* 208 */     return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "EconXP does not support bank accounts!");
/*     */   }
/*     */ 
/*     */   public EconomyResponse isBankMember(String name, String playerName)
/*     */   {
/* 213 */     return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "EconXP does not support bank accounts!");
/*     */   }
/*     */ 
/*     */   public EconomyResponse bankBalance(String name)
/*     */   {
/* 218 */     return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "EconXP does not support bank accounts!");
/*     */   }
/*     */ 
/*     */   public List<String> getBanks()
/*     */   {
/* 223 */     return new ArrayList();
/*     */   }
/*     */ 
/*     */   public boolean hasBankSupport()
/*     */   {
/* 228 */     return false;
/*     */   }
/*     */ 
/*     */   public boolean hasAccount(String playerName)
/*     */   {
/* 233 */     return this.econ.getPlayer(playerName) != null;
/*     */   }
/*     */ 
/*     */   public boolean createPlayerAccount(String playerName)
/*     */   {
/* 238 */     return false;
/*     */   }
/*     */ 
/*     */   public int fractionalDigits()
/*     */   {
/* 243 */     return 0;
/*     */   }
/*     */ 
/*     */   public boolean hasAccount(String playerName, String worldName)
/*     */   {
/* 249 */     return hasAccount(playerName);
/*     */   }
/*     */ 
/*     */   public double getBalance(String playerName, String world)
/*     */   {
/* 254 */     return getBalance(playerName);
/*     */   }
/*     */ 
/*     */   public boolean has(String playerName, String worldName, double amount)
/*     */   {
/* 259 */     return has(playerName, amount);
/*     */   }
/*     */ 
/*     */   public EconomyResponse withdrawPlayer(String playerName, String worldName, double amount)
/*     */   {
/* 264 */     return withdrawPlayer(playerName, amount);
/*     */   }
/*     */ 
/*     */   public EconomyResponse depositPlayer(String playerName, String worldName, double amount)
/*     */   {
/* 269 */     return depositPlayer(playerName, amount);
/*     */   }
/*     */ 
/*     */   public boolean createPlayerAccount(String playerName, String worldName)
/*     */   {
/* 274 */     return createPlayerAccount(playerName);
/*     */   }
/*     */ 
/*     */   public class EconomyServerListener
/*     */     implements Listener
/*     */   {
/*  60 */     Economy_EconXP economy = null;
/*     */ 
/*     */     public EconomyServerListener(Economy_EconXP economy) {
/*  63 */       this.economy = economy;
/*     */     }
/*     */ 
/*     */     @EventHandler(priority=EventPriority.MONITOR)
/*     */     public void onPluginEnable(PluginEnableEvent event) {
/*  68 */       if (this.economy.econ == null) {
/*  69 */         Plugin eco = event.getPlugin();
/*     */ 
/*  71 */         if (eco.getDescription().getName().equals("EconXP")) {
/*  72 */           this.economy.econ = ((EconXP)eco);
/*  73 */           Economy_EconXP.log.info(String.format("[%s][Economy] %s hooked.", new Object[] { Economy_EconXP.this.plugin.getDescription().getName(), "EconXP" }));
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/*     */     @EventHandler(priority=EventPriority.MONITOR)
/*     */     public void onPluginDisable(PluginDisableEvent event) {
/*  80 */       if ((this.economy.econ != null) && 
/*  81 */         (event.getPlugin().getDescription().getName().equals("EconXP"))) {
/*  82 */         this.economy.econ = null;
/*  83 */         Economy_EconXP.log.info(String.format("[%s][Economy] %s unhooked.", new Object[] { Economy_EconXP.this.plugin.getDescription().getName(), "EconXP" }));
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\Vault.jar
 * Qualified Name:     net.milkbowl.vault.economy.plugins.Economy_EconXP
 * JD-Core Version:    0.6.2
 */