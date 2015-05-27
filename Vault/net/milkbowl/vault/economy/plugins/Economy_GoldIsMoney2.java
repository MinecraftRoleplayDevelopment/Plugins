/*     */ package net.milkbowl.vault.economy.plugins;
/*     */ 
/*     */ import com.flobi.GoldIsMoney2.GoldIsMoney;
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
/*     */ public class Economy_GoldIsMoney2 extends AbstractEconomy
/*     */ {
/*  36 */   private static final Logger log = Logger.getLogger("Minecraft");
/*     */ 
/*  38 */   private final String name = "GoldIsMoney";
/*  39 */   private Plugin plugin = null;
/*  40 */   protected GoldIsMoney economy = null;
/*     */ 
/*     */   public Economy_GoldIsMoney2(Plugin plugin) {
/*  43 */     this.plugin = plugin;
/*  44 */     Bukkit.getServer().getPluginManager().registerEvents(new EconomyServerListener(this), plugin);
/*     */ 
/*  46 */     if (this.economy == null) {
/*  47 */       Plugin ec = plugin.getServer().getPluginManager().getPlugin("GoldIsMoney");
/*     */ 
/*  49 */       if ((ec != null) && (ec.isEnabled()) && (ec.getClass().getName().equals("com.flobi.GoldIsMoney2.GoldIsMoney"))) {
/*  50 */         this.economy = ((GoldIsMoney)ec);
/*  51 */         log.info(String.format("[%s][Economy] %s hooked.", new Object[] { plugin.getDescription().getName(), "GoldIsMoney" }));
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean isEnabled()
/*     */   {
/*  58 */     if (this.economy == null) {
/*  59 */       return false;
/*     */     }
/*  61 */     return this.economy.isEnabled();
/*     */   }
/*     */ 
/*     */   public String getName()
/*     */   {
/*  67 */     return "GoldIsMoney";
/*     */   }
/*     */ 
/*     */   public boolean hasBankSupport()
/*     */   {
/*  72 */     return GoldIsMoney.hasBankSupport();
/*     */   }
/*     */ 
/*     */   public int fractionalDigits()
/*     */   {
/*  77 */     return GoldIsMoney.fractionalDigits();
/*     */   }
/*     */ 
/*     */   public String format(double amount)
/*     */   {
/*  82 */     return GoldIsMoney.format(amount);
/*     */   }
/*     */ 
/*     */   public String currencyNamePlural()
/*     */   {
/*  87 */     return GoldIsMoney.currencyNamePlural();
/*     */   }
/*     */ 
/*     */   public String currencyNameSingular()
/*     */   {
/*  92 */     return GoldIsMoney.currencyNameSingular();
/*     */   }
/*     */ 
/*     */   public boolean hasAccount(String playerName)
/*     */   {
/*  97 */     return GoldIsMoney.hasAccount(playerName);
/*     */   }
/*     */ 
/*     */   public double getBalance(String playerName)
/*     */   {
/* 102 */     return GoldIsMoney.getBalance(playerName);
/*     */   }
/*     */ 
/*     */   public boolean has(String playerName, double amount)
/*     */   {
/* 107 */     return GoldIsMoney.has(playerName, amount);
/*     */   }
/*     */ 
/*     */   public EconomyResponse withdrawPlayer(String playerName, double amount)
/*     */   {
/* 112 */     if (amount < 0.0D) {
/* 113 */       return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.FAILURE, "Cannot withdraw negative funds!");
/*     */     }
/* 115 */     if (!GoldIsMoney.hasAccount(playerName)) {
/* 116 */       return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.FAILURE, "That player does not have an account!");
/*     */     }
/* 118 */     if (!GoldIsMoney.has(playerName, amount)) {
/* 119 */       return new EconomyResponse(0.0D, GoldIsMoney.getBalance(playerName), EconomyResponse.ResponseType.FAILURE, "Insufficient funds");
/*     */     }
/* 121 */     if (!GoldIsMoney.withdrawPlayer(playerName, amount)) {
/* 122 */       return new EconomyResponse(0.0D, GoldIsMoney.getBalance(playerName), EconomyResponse.ResponseType.FAILURE, "Unable to withdraw funds!");
/*     */     }
/* 124 */     return new EconomyResponse(amount, GoldIsMoney.getBalance(playerName), EconomyResponse.ResponseType.SUCCESS, null);
/*     */   }
/*     */ 
/*     */   public EconomyResponse depositPlayer(String playerName, double amount)
/*     */   {
/* 129 */     if (amount < 0.0D) {
/* 130 */       return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.FAILURE, "Cannot desposit negative funds!");
/*     */     }
/* 132 */     if (!GoldIsMoney.hasAccount(playerName)) {
/* 133 */       return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.FAILURE, "That player does not have an account!");
/*     */     }
/* 135 */     if (!GoldIsMoney.depositPlayer(playerName, amount)) {
/* 136 */       return new EconomyResponse(0.0D, GoldIsMoney.getBalance(playerName), EconomyResponse.ResponseType.FAILURE, "Unable to deposit funds!");
/*     */     }
/* 138 */     return new EconomyResponse(amount, GoldIsMoney.getBalance(playerName), EconomyResponse.ResponseType.SUCCESS, null);
/*     */   }
/*     */ 
/*     */   public EconomyResponse createBank(String name, String player)
/*     */   {
/* 143 */     if (!GoldIsMoney.hasBankSupport()) {
/* 144 */       return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "GoldIsMoney bank support is disabled!");
/*     */     }
/* 146 */     if (!GoldIsMoney.createBank(name, player)) {
/* 147 */       return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.FAILURE, "Unable to create bank account.");
/*     */     }
/* 149 */     return new EconomyResponse(0.0D, GoldIsMoney.bankBalance(name), EconomyResponse.ResponseType.SUCCESS, "");
/*     */   }
/*     */ 
/*     */   public EconomyResponse deleteBank(String name)
/*     */   {
/* 154 */     if (!GoldIsMoney.hasBankSupport()) {
/* 155 */       return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "GoldIsMoney bank support is disabled!");
/*     */     }
/* 157 */     if (!GoldIsMoney.deleteBank(name)) {
/* 158 */       return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.FAILURE, "Unable to remove bank account.");
/*     */     }
/* 160 */     return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.SUCCESS, "");
/*     */   }
/*     */ 
/*     */   public EconomyResponse bankBalance(String name)
/*     */   {
/* 165 */     if (!GoldIsMoney.hasBankSupport()) {
/* 166 */       return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "GoldIsMoney bank support is disabled!");
/*     */     }
/* 168 */     if (!GoldIsMoney.bankExists(name)) {
/* 169 */       return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.FAILURE, "That bank does not exist!");
/*     */     }
/* 171 */     return new EconomyResponse(0.0D, GoldIsMoney.bankBalance(name), EconomyResponse.ResponseType.SUCCESS, "");
/*     */   }
/*     */ 
/*     */   public EconomyResponse bankHas(String name, double amount)
/*     */   {
/* 176 */     if (!GoldIsMoney.hasBankSupport()) {
/* 177 */       return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "GoldIsMoney bank support is disabled!");
/*     */     }
/* 179 */     if (!GoldIsMoney.bankExists(name)) {
/* 180 */       return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.FAILURE, "That bank does not exist!");
/*     */     }
/* 182 */     if (GoldIsMoney.bankHas(name, amount)) {
/* 183 */       return new EconomyResponse(0.0D, GoldIsMoney.bankBalance(name), EconomyResponse.ResponseType.FAILURE, "The bank does not have enough money!");
/*     */     }
/* 185 */     return new EconomyResponse(0.0D, GoldIsMoney.bankBalance(name), EconomyResponse.ResponseType.SUCCESS, "");
/*     */   }
/*     */ 
/*     */   public EconomyResponse bankWithdraw(String name, double amount)
/*     */   {
/* 190 */     if (!GoldIsMoney.hasBankSupport()) {
/* 191 */       return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "GoldIsMoney bank support is disabled!");
/*     */     }
/* 193 */     if (!GoldIsMoney.bankExists(name)) {
/* 194 */       return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.FAILURE, "That bank does not exist!");
/*     */     }
/* 196 */     if (!GoldIsMoney.bankHas(name, amount)) {
/* 197 */       return new EconomyResponse(0.0D, GoldIsMoney.bankBalance(name), EconomyResponse.ResponseType.FAILURE, "The bank does not have enough money!");
/*     */     }
/* 199 */     if (!GoldIsMoney.bankWithdraw(name, amount)) {
/* 200 */       return new EconomyResponse(0.0D, GoldIsMoney.bankBalance(name), EconomyResponse.ResponseType.FAILURE, "Unable to withdraw from that bank account!");
/*     */     }
/* 202 */     return new EconomyResponse(amount, GoldIsMoney.bankBalance(name), EconomyResponse.ResponseType.SUCCESS, "");
/*     */   }
/*     */ 
/*     */   public EconomyResponse bankDeposit(String name, double amount)
/*     */   {
/* 207 */     if (!GoldIsMoney.hasBankSupport()) {
/* 208 */       return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "GoldIsMoney bank support is disabled!");
/*     */     }
/* 210 */     if (!GoldIsMoney.bankExists(name)) {
/* 211 */       return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.FAILURE, "That bank does not exist!");
/*     */     }
/* 213 */     if (!GoldIsMoney.bankDeposit(name, amount)) {
/* 214 */       return new EconomyResponse(0.0D, GoldIsMoney.bankBalance(name), EconomyResponse.ResponseType.FAILURE, "Unable to deposit to that bank account!");
/*     */     }
/* 216 */     return new EconomyResponse(amount, GoldIsMoney.bankBalance(name), EconomyResponse.ResponseType.SUCCESS, "");
/*     */   }
/*     */ 
/*     */   public EconomyResponse isBankOwner(String name, String playerName)
/*     */   {
/* 221 */     if (!GoldIsMoney.hasBankSupport()) {
/* 222 */       return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "GoldIsMoney bank support is disabled!");
/*     */     }
/* 224 */     if (!GoldIsMoney.bankExists(name)) {
/* 225 */       return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.FAILURE, "That bank does not exist!");
/*     */     }
/* 227 */     if (!GoldIsMoney.isBankOwner(name, playerName)) {
/* 228 */       return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.FAILURE, "That player does not own that bank!");
/*     */     }
/* 230 */     return new EconomyResponse(0.0D, GoldIsMoney.bankBalance(name), EconomyResponse.ResponseType.SUCCESS, "");
/*     */   }
/*     */ 
/*     */   public EconomyResponse isBankMember(String name, String playerName)
/*     */   {
/* 235 */     if (!GoldIsMoney.hasBankSupport()) {
/* 236 */       return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "GoldIsMoney bank support is disabled!");
/*     */     }
/* 238 */     if (!GoldIsMoney.bankExists(name)) {
/* 239 */       return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.FAILURE, "That bank does not exist!");
/*     */     }
/* 241 */     if (!GoldIsMoney.isBankMember(name, playerName)) {
/* 242 */       return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.FAILURE, "That player is not a member of that bank!");
/*     */     }
/* 244 */     return new EconomyResponse(0.0D, GoldIsMoney.bankBalance(name), EconomyResponse.ResponseType.SUCCESS, "");
/*     */   }
/*     */ 
/*     */   public List<String> getBanks()
/*     */   {
/* 249 */     return GoldIsMoney.getBanks();
/*     */   }
/*     */ 
/*     */   public boolean createPlayerAccount(String playerName)
/*     */   {
/* 254 */     return GoldIsMoney.createPlayerAccount(playerName);
/*     */   }
/*     */ 
/*     */   public boolean hasAccount(String playerName, String worldName)
/*     */   {
/* 289 */     return hasAccount(playerName);
/*     */   }
/*     */ 
/*     */   public double getBalance(String playerName, String world)
/*     */   {
/* 294 */     return getBalance(playerName);
/*     */   }
/*     */ 
/*     */   public boolean has(String playerName, String worldName, double amount)
/*     */   {
/* 299 */     return has(playerName, amount);
/*     */   }
/*     */ 
/*     */   public EconomyResponse withdrawPlayer(String playerName, String worldName, double amount)
/*     */   {
/* 304 */     return withdrawPlayer(playerName, amount);
/*     */   }
/*     */ 
/*     */   public EconomyResponse depositPlayer(String playerName, String worldName, double amount)
/*     */   {
/* 309 */     return depositPlayer(playerName, amount);
/*     */   }
/*     */ 
/*     */   public boolean createPlayerAccount(String playerName, String worldName)
/*     */   {
/* 314 */     return createPlayerAccount(playerName);
/*     */   }
/*     */ 
/*     */   public class EconomyServerListener
/*     */     implements Listener
/*     */   {
/* 258 */     Economy_GoldIsMoney2 economy = null;
/*     */ 
/*     */     public EconomyServerListener(Economy_GoldIsMoney2 economy_GoldIsMoney2) {
/* 261 */       this.economy = economy_GoldIsMoney2;
/*     */     }
/*     */ 
/*     */     @EventHandler(priority=EventPriority.MONITOR)
/*     */     public void onPluginEnable(PluginEnableEvent event) {
/* 266 */       if (this.economy.economy == null) {
/* 267 */         Plugin ec = event.getPlugin();
/*     */ 
/* 269 */         if (ec.getClass().getName().equals("com.flobi.GoldIsMoney2.GoldIsMoney")) {
/* 270 */           this.economy.economy = ((GoldIsMoney)ec);
/* 271 */           Economy_GoldIsMoney2.log.info(String.format("[%s][Economy] %s hooked.", new Object[] { Economy_GoldIsMoney2.this.plugin.getDescription().getName(), "GoldIsMoney" }));
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/*     */     @EventHandler(priority=EventPriority.MONITOR)
/*     */     public void onPluginDisable(PluginDisableEvent event) {
/* 278 */       if ((this.economy.economy != null) && 
/* 279 */         (event.getPlugin().getDescription().getName().equals("GoldIsMoney"))) {
/* 280 */         this.economy.economy = null;
/* 281 */         Economy_GoldIsMoney2.log.info(String.format("[%s][Economy] %s unhooked.", new Object[] { Economy_GoldIsMoney2.this.plugin.getDescription().getName(), "GoldIsMoney" }));
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\Vault.jar
 * Qualified Name:     net.milkbowl.vault.economy.plugins.Economy_GoldIsMoney2
 * JD-Core Version:    0.6.2
 */