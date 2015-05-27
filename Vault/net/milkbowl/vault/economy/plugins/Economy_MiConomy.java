/*     */ package net.milkbowl.vault.economy.plugins;
/*     */ 
/*     */ import com.gmail.bleedobsidian.miconomy.Config;
/*     */ import com.gmail.bleedobsidian.miconomy.Main;
/*     */ import com.gmail.bleedobsidian.miconomy.MiConomy;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.logging.Logger;
/*     */ import net.milkbowl.vault.economy.AbstractEconomy;
/*     */ import net.milkbowl.vault.economy.EconomyResponse;
/*     */ import net.milkbowl.vault.economy.EconomyResponse.ResponseType;
/*     */ import org.bukkit.Bukkit;
/*     */ import org.bukkit.OfflinePlayer;
/*     */ import org.bukkit.Server;
/*     */ import org.bukkit.World;
/*     */ import org.bukkit.event.EventHandler;
/*     */ import org.bukkit.event.EventPriority;
/*     */ import org.bukkit.event.Listener;
/*     */ import org.bukkit.event.server.PluginDisableEvent;
/*     */ import org.bukkit.event.server.PluginEnableEvent;
/*     */ import org.bukkit.plugin.Plugin;
/*     */ import org.bukkit.plugin.PluginDescriptionFile;
/*     */ import org.bukkit.plugin.PluginManager;
/*     */ 
/*     */ public class Economy_MiConomy extends AbstractEconomy
/*     */ {
/*  39 */   private static final Logger log = Logger.getLogger("Minecraft");
/*     */ 
/*  41 */   private final String name = "MiConomy";
/*     */   private Plugin plugin;
/*     */   private MiConomy economy;
/*     */   private Main miConomy;
/*     */ 
/*     */   public Economy_MiConomy(Plugin plugin)
/*     */   {
/*  48 */     this.plugin = plugin;
/*  49 */     Bukkit.getServer().getPluginManager().registerEvents(new EconomyServerListener(this), plugin);
/*     */ 
/*  52 */     if (this.miConomy == null) {
/*  53 */       Plugin miConomyPlugin = plugin.getServer().getPluginManager().getPlugin("MiConomy");
/*     */ 
/*  55 */       if (this.miConomy != null) {
/*  56 */         this.miConomy = ((Main)miConomyPlugin);
/*  57 */         this.economy = this.miConomy.getInstance();
/*  58 */         log.info(String.format("[%s][Economy] %s hooked.", new Object[] { plugin.getDescription().getName(), "MiConomy" }));
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean isEnabled()
/*     */   {
/*  65 */     if (this.miConomy == null) {
/*  66 */       return false;
/*     */     }
/*  68 */     return this.miConomy.isEnabled();
/*     */   }
/*     */ 
/*     */   public String getName()
/*     */   {
/*  74 */     return "MiConomy";
/*     */   }
/*     */ 
/*     */   public boolean hasBankSupport()
/*     */   {
/*  79 */     return true;
/*     */   }
/*     */ 
/*     */   public int fractionalDigits()
/*     */   {
/*  84 */     return 2;
/*     */   }
/*     */ 
/*     */   public String format(double amount)
/*     */   {
/*  89 */     return this.economy.getFormattedValue(amount);
/*     */   }
/*     */ 
/*     */   public String currencyNamePlural()
/*     */   {
/*  94 */     return this.miConomy.getPluginConfig().MoneyNamePlural;
/*     */   }
/*     */ 
/*     */   public String currencyNameSingular()
/*     */   {
/*  99 */     return this.miConomy.getPluginConfig().MoneyName;
/*     */   }
/*     */ 
/*     */   public boolean hasAccount(String playerName)
/*     */   {
/* 104 */     List worlds = this.plugin.getServer().getWorlds();
/*     */ 
/* 106 */     return hasAccount(playerName, ((World)worlds.get(0)).getName());
/*     */   }
/*     */ 
/*     */   public boolean hasAccount(String playerName, String worldName)
/*     */   {
/* 111 */     OfflinePlayer player = this.plugin.getServer().getOfflinePlayer(playerName);
/* 112 */     World world = this.plugin.getServer().getWorld(worldName);
/*     */ 
/* 114 */     return this.economy.isAccountCreated(player, world);
/*     */   }
/*     */ 
/*     */   public double getBalance(String playerName)
/*     */   {
/* 119 */     List worlds = this.plugin.getServer().getWorlds();
/*     */ 
/* 121 */     return getBalance(playerName, ((World)worlds.get(0)).getName());
/*     */   }
/*     */ 
/*     */   public double getBalance(String playerName, String worldName)
/*     */   {
/* 126 */     OfflinePlayer player = this.plugin.getServer().getOfflinePlayer(playerName);
/* 127 */     World world = this.plugin.getServer().getWorld(worldName);
/*     */ 
/* 129 */     return this.economy.getAccountBalance(player, world);
/*     */   }
/*     */ 
/*     */   public boolean has(String playerName, double amount)
/*     */   {
/* 134 */     List worlds = this.plugin.getServer().getWorlds();
/*     */ 
/* 136 */     return has(playerName, ((World)worlds.get(0)).getName(), amount);
/*     */   }
/*     */ 
/*     */   public boolean has(String playerName, String worldName, double amount)
/*     */   {
/* 141 */     OfflinePlayer player = this.plugin.getServer().getOfflinePlayer(playerName);
/* 142 */     World world = this.plugin.getServer().getWorld(worldName);
/*     */ 
/* 144 */     double playerBalance = this.economy.getAccountBalance(player, world);
/*     */ 
/* 146 */     if (playerBalance >= amount) {
/* 147 */       return true;
/*     */     }
/* 149 */     return false;
/*     */   }
/*     */ 
/*     */   public EconomyResponse withdrawPlayer(String playerName, double amount)
/*     */   {
/* 155 */     List worlds = this.plugin.getServer().getWorlds();
/*     */ 
/* 157 */     return withdrawPlayer(playerName, ((World)worlds.get(0)).getName(), amount);
/*     */   }
/*     */ 
/*     */   public EconomyResponse withdrawPlayer(String playerName, String worldName, double amount)
/*     */   {
/* 162 */     OfflinePlayer player = this.plugin.getServer().getOfflinePlayer(playerName);
/* 163 */     World world = this.plugin.getServer().getWorld(worldName);
/*     */ 
/* 165 */     double balance = this.economy.getAccountBalance(player, world);
/*     */ 
/* 167 */     if (getBalance(playerName, worldName) < amount) {
/* 168 */       return new EconomyResponse(0.0D, balance, EconomyResponse.ResponseType.FAILURE, "Insufficient funds");
/*     */     }
/* 170 */     if (this.economy.removeAccountBalance(player, amount, world)) {
/* 171 */       balance = this.economy.getAccountBalance(player, world);
/*     */ 
/* 173 */       return new EconomyResponse(amount, balance, EconomyResponse.ResponseType.SUCCESS, "");
/*     */     }
/* 175 */     return new EconomyResponse(0.0D, balance, EconomyResponse.ResponseType.FAILURE, "Failed to remove funds from account");
/*     */   }
/*     */ 
/*     */   public EconomyResponse depositPlayer(String playerName, double amount)
/*     */   {
/* 182 */     List worlds = this.plugin.getServer().getWorlds();
/*     */ 
/* 184 */     return depositPlayer(playerName, ((World)worlds.get(0)).getName(), amount);
/*     */   }
/*     */ 
/*     */   public EconomyResponse depositPlayer(String playerName, String worldName, double amount)
/*     */   {
/* 189 */     OfflinePlayer player = this.plugin.getServer().getOfflinePlayer(playerName);
/* 190 */     World world = this.plugin.getServer().getWorld(worldName);
/*     */ 
/* 192 */     double balance = this.economy.getAccountBalance(player, world);
/*     */ 
/* 194 */     if (this.economy.addAccountBalance(player, amount, world)) {
/* 195 */       balance = this.economy.getAccountBalance(player, world);
/*     */ 
/* 197 */       return new EconomyResponse(amount, balance, EconomyResponse.ResponseType.SUCCESS, "");
/*     */     }
/* 199 */     return new EconomyResponse(0.0D, balance, EconomyResponse.ResponseType.FAILURE, "Failed to add funds to account");
/*     */   }
/*     */ 
/*     */   public EconomyResponse createBank(String name, String player)
/*     */   {
/* 205 */     OfflinePlayer owner = this.plugin.getServer().getOfflinePlayer(player);
/*     */ 
/* 207 */     ArrayList owners = new ArrayList();
/* 208 */     owners.add(owner);
/*     */ 
/* 210 */     if (!this.economy.isBankCreated(name)) {
/* 211 */       this.economy.createBank(name, owners, new ArrayList(), false);
/* 212 */       return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.SUCCESS, "");
/*     */     }
/* 214 */     return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.FAILURE, "A bank with this name already exists");
/*     */   }
/*     */ 
/*     */   public EconomyResponse deleteBank(String name)
/*     */   {
/* 220 */     if (this.economy.isBankCreated(name)) {
/* 221 */       this.economy.deleteBank(name);
/* 222 */       return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.SUCCESS, "");
/*     */     }
/* 224 */     return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.FAILURE, "Bank doesn't exist");
/*     */   }
/*     */ 
/*     */   public EconomyResponse bankBalance(String name)
/*     */   {
/* 230 */     if (this.economy.isBankCreated(name)) {
/* 231 */       double balance = this.economy.getBankBalance(name);
/* 232 */       return new EconomyResponse(0.0D, balance, EconomyResponse.ResponseType.SUCCESS, "");
/*     */     }
/* 234 */     return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.FAILURE, "Bank doesn't exist");
/*     */   }
/*     */ 
/*     */   public EconomyResponse bankHas(String name, double amount)
/*     */   {
/* 240 */     if (this.economy.isBankCreated(name)) {
/* 241 */       double balance = this.economy.getBankBalance(name);
/*     */ 
/* 243 */       if (balance >= amount) {
/* 244 */         return new EconomyResponse(0.0D, balance, EconomyResponse.ResponseType.SUCCESS, "");
/*     */       }
/* 246 */       return new EconomyResponse(0.0D, balance, EconomyResponse.ResponseType.FAILURE, "The bank does not have enough money!");
/*     */     }
/*     */ 
/* 249 */     return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.FAILURE, "Bank doesn't exist");
/*     */   }
/*     */ 
/*     */   public EconomyResponse bankWithdraw(String name, double amount)
/*     */   {
/* 255 */     if (this.economy.isBankCreated(name)) {
/* 256 */       this.economy.removeBankBalance(name, amount);
/*     */ 
/* 258 */       double balance = this.economy.getBankBalance(name);
/*     */ 
/* 260 */       return new EconomyResponse(amount, balance, EconomyResponse.ResponseType.SUCCESS, "");
/*     */     }
/* 262 */     return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.FAILURE, "Bank doesn't exist");
/*     */   }
/*     */ 
/*     */   public EconomyResponse bankDeposit(String name, double amount)
/*     */   {
/* 268 */     if (this.economy.isBankCreated(name)) {
/* 269 */       this.economy.addBankBalance(name, amount);
/*     */ 
/* 271 */       double balance = this.economy.getBankBalance(name);
/*     */ 
/* 273 */       return new EconomyResponse(amount, balance, EconomyResponse.ResponseType.SUCCESS, "");
/*     */     }
/* 275 */     return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.FAILURE, "Bank doesn't exist");
/*     */   }
/*     */ 
/*     */   public EconomyResponse isBankOwner(String name, String playerName)
/*     */   {
/* 281 */     OfflinePlayer owner = this.plugin.getServer().getOfflinePlayer(playerName);
/*     */ 
/* 283 */     if (this.economy.isBankCreated(name)) {
/* 284 */       if (this.economy.isPlayerBankOwner(name, owner)) {
/* 285 */         return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.SUCCESS, "");
/*     */       }
/* 287 */       return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.FAILURE, "The player is not a bank owner");
/*     */     }
/*     */ 
/* 290 */     return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.FAILURE, "Bank doesn't exist");
/*     */   }
/*     */ 
/*     */   public EconomyResponse isBankMember(String name, String playerName)
/*     */   {
/* 296 */     OfflinePlayer owner = this.plugin.getServer().getOfflinePlayer(playerName);
/*     */ 
/* 298 */     if (this.economy.isBankCreated(name)) {
/* 299 */       if (this.economy.isPlayerBankMember(name, owner)) {
/* 300 */         return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.SUCCESS, "");
/*     */       }
/* 302 */       return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.FAILURE, "The player is not a bank member");
/*     */     }
/*     */ 
/* 305 */     return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.FAILURE, "Bank doesn't exist");
/*     */   }
/*     */ 
/*     */   public List<String> getBanks()
/*     */   {
/* 311 */     return this.economy.getBanks();
/*     */   }
/*     */ 
/*     */   public boolean createPlayerAccount(String playerName)
/*     */   {
/* 316 */     List worlds = this.plugin.getServer().getWorlds();
/*     */ 
/* 318 */     return createPlayerAccount(playerName, ((World)worlds.get(0)).getName());
/*     */   }
/*     */ 
/*     */   public boolean createPlayerAccount(String playerName, String worldName)
/*     */   {
/* 323 */     OfflinePlayer player = this.plugin.getServer().getOfflinePlayer(playerName);
/* 324 */     World world = this.plugin.getServer().getWorld(worldName);
/*     */ 
/* 326 */     if (!this.economy.isAccountCreated(player, world)) {
/* 327 */       this.economy.createAccount(player, 0.0D, world);
/*     */ 
/* 329 */       return true;
/*     */     }
/* 331 */     return false;
/*     */   }
/*     */ 
/*     */   public class EconomyServerListener implements Listener
/*     */   {
/* 336 */     Economy_MiConomy economy = null;
/*     */ 
/*     */     public EconomyServerListener(Economy_MiConomy economy) {
/* 339 */       this.economy = economy;
/*     */     }
/*     */ 
/*     */     @EventHandler(priority=EventPriority.MONITOR)
/*     */     public void onPluginEnable(PluginEnableEvent event) {
/* 344 */       if (this.economy.economy == null) {
/* 345 */         Plugin miConomyPlugin = event.getPlugin();
/*     */ 
/* 347 */         if (miConomyPlugin.getDescription().getName().equals("MiConomy")) {
/* 348 */           this.economy.miConomy = ((Main)miConomyPlugin);
/*     */ 
/* 350 */           this.economy.economy = Economy_MiConomy.this.miConomy.getInstance();
/*     */ 
/* 352 */           Economy_MiConomy.log.info(String.format("[%s][Economy] %s hooked.", new Object[] { Economy_MiConomy.this.plugin.getDescription().getName(), "MiConomy" }));
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/*     */     @EventHandler(priority=EventPriority.MONITOR)
/*     */     public void onPluginDisable(PluginDisableEvent event) {
/* 359 */       if ((this.economy.economy != null) && 
/* 360 */         (event.getPlugin().getDescription().getName().equals("MiConomy"))) {
/* 361 */         this.economy.miConomy = null;
/* 362 */         this.economy.economy = null;
/*     */ 
/* 364 */         Economy_MiConomy.log.info(String.format("[%s][Economy] %s unhooked.", new Object[] { Economy_MiConomy.this.plugin.getDescription().getName(), "MiConomy" }));
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\Vault.jar
 * Qualified Name:     net.milkbowl.vault.economy.plugins.Economy_MiConomy
 * JD-Core Version:    0.6.2
 */