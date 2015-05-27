/*     */ package net.milkbowl.vault.economy.plugins;
/*     */ 
/*     */ import com.gmail.mirelatrue.xpbank.API;
/*     */ import com.gmail.mirelatrue.xpbank.Account;
/*     */ import com.gmail.mirelatrue.xpbank.GroupBank;
/*     */ import com.gmail.mirelatrue.xpbank.Memberlist;
/*     */ import com.gmail.mirelatrue.xpbank.XPBank;
/*     */ import java.util.HashMap;
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
/*     */ public class Economy_XPBank extends AbstractEconomy
/*     */ {
/*  41 */   private static final Logger log = Logger.getLogger("Minecraft");
/*     */ 
/*  44 */   private final String name = "XPBank";
/*  45 */   private Plugin plugin = null;
/*  46 */   private XPBank XPB = null;
/*  47 */   private API api = null;
/*     */ 
/*     */   public Economy_XPBank(Plugin plugin) {
/*  50 */     this.plugin = plugin;
/*  51 */     Bukkit.getServer().getPluginManager().registerEvents(new EconomyServerListener(this), plugin);
/*     */ 
/*  54 */     if (this.XPB == null) {
/*  55 */       Plugin economy = plugin.getServer().getPluginManager().getPlugin("XPBank");
/*  56 */       if ((economy != null) && (economy.isEnabled())) {
/*  57 */         this.XPB = ((XPBank)economy);
/*  58 */         this.api = this.XPB.getAPI();
/*  59 */         log.info(String.format("[%s][Economy] %s hooked.", new Object[] { plugin.getDescription().getName(), "XPBank" }));
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean isEnabled()
/*     */   {
/*  97 */     return this.XPB != null;
/*     */   }
/*     */ 
/*     */   public String getName()
/*     */   {
/* 102 */     return "XPBank";
/*     */   }
/*     */ 
/*     */   public boolean hasBankSupport()
/*     */   {
/* 107 */     return true;
/*     */   }
/*     */ 
/*     */   public int fractionalDigits()
/*     */   {
/* 112 */     return 0;
/*     */   }
/*     */ 
/*     */   public String format(double amount)
/*     */   {
/* 117 */     return String.format("%d %s", new Object[] { Integer.valueOf((int)amount), this.api.currencyName((int)amount) });
/*     */   }
/*     */ 
/*     */   public String currencyNamePlural()
/*     */   {
/* 122 */     return this.api.getMsg("CurrencyNamePlural");
/*     */   }
/*     */ 
/*     */   public String currencyNameSingular()
/*     */   {
/* 127 */     return this.api.getMsg("currencyName");
/*     */   }
/*     */ 
/*     */   public boolean hasAccount(String playerName)
/*     */   {
/* 132 */     Account account = this.api.getAccount(playerName);
/*     */ 
/* 134 */     if (account != null) {
/* 135 */       return true;
/*     */     }
/*     */ 
/* 138 */     return false;
/*     */   }
/*     */ 
/*     */   public double getBalance(String playerName)
/*     */   {
/* 143 */     Account account = this.api.getAccount(playerName);
/*     */ 
/* 145 */     return account.getBalance();
/*     */   }
/*     */ 
/*     */   public boolean has(String playerName, double amount)
/*     */   {
/* 150 */     Account account = this.api.getAccount(playerName);
/*     */ 
/* 152 */     if (account.getBalance() >= (int)amount) {
/* 153 */       return true;
/*     */     }
/*     */ 
/* 156 */     return false;
/*     */   }
/*     */ 
/*     */   public EconomyResponse withdrawPlayer(String playerName, double amount)
/*     */   {
/* 161 */     Account account = this.api.getAccount(playerName);
/*     */ 
/* 163 */     if (account == null) {
/* 164 */       return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.FAILURE, this.api.getMsg("Player doesn't exist."));
/*     */     }
/*     */ 
/* 167 */     int value = (int)amount;
/* 168 */     int balance = account.getBalance();
/*     */ 
/* 170 */     if (value < 1) {
/* 171 */       return new EconomyResponse(0.0D, balance, EconomyResponse.ResponseType.FAILURE, this.api.getMsg("LessThanZero"));
/*     */     }
/*     */ 
/* 174 */     if (value > balance) {
/* 175 */       return new EconomyResponse(0.0D, balance, EconomyResponse.ResponseType.FAILURE, String.format(this.api.getMsg("InsufficientXP"), new Object[] { this.api.currencyName(value) }));
/*     */     }
/*     */ 
/* 178 */     account.modifyBalance(-value);
/*     */ 
/* 180 */     return new EconomyResponse(value, balance - value, EconomyResponse.ResponseType.SUCCESS, null);
/*     */   }
/*     */ 
/*     */   public EconomyResponse depositPlayer(String playerName, double amount)
/*     */   {
/* 185 */     Account account = this.api.getAccount(playerName);
/*     */ 
/* 187 */     if (account == null)
/*     */     {
/* 190 */       createPlayerAccount(playerName);
/*     */     }
/*     */ 
/* 193 */     int value = (int)amount;
/* 194 */     int balance = account.getBalance();
/*     */ 
/* 196 */     if (value < 1) {
/* 197 */       return new EconomyResponse(0.0D, balance, EconomyResponse.ResponseType.FAILURE, this.api.getMsg("LessThanZero"));
/*     */     }
/*     */ 
/* 200 */     account.addTaxableIncome(value);
/*     */ 
/* 202 */     return new EconomyResponse(value, balance + value, EconomyResponse.ResponseType.SUCCESS, null);
/*     */   }
/*     */ 
/*     */   public EconomyResponse createBank(String name, String player)
/*     */   {
/* 207 */     GroupBank groupBank = this.api.getGroupBank(name);
/*     */ 
/* 209 */     if (groupBank != null) {
/* 210 */       return new EconomyResponse(0.0D, groupBank.getBalance(), EconomyResponse.ResponseType.FAILURE, String.format(this.api.getMsg("GroupBankExists"), new Object[] { name }));
/*     */     }
/*     */ 
/* 213 */     Account account = this.api.getAccount(player);
/*     */ 
/* 215 */     groupBank = this.api.createGroupBank(name, account);
/*     */ 
/* 217 */     return new EconomyResponse(0.0D, groupBank.getBalance(), EconomyResponse.ResponseType.SUCCESS, null);
/*     */   }
/*     */ 
/*     */   public EconomyResponse deleteBank(String name)
/*     */   {
/* 222 */     GroupBank groupBank = this.api.getGroupBank(name);
/*     */ 
/* 224 */     if (groupBank == null) {
/* 225 */       return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.FAILURE, this.api.getMsg("GroupBankNotExists"));
/*     */     }
/*     */ 
/* 228 */     this.api.deleteGroupBank(groupBank, String.format(this.api.getMsg("Disbanded"), new Object[] { groupBank.getName() }));
/*     */ 
/* 230 */     return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.SUCCESS, null);
/*     */   }
/*     */ 
/*     */   public EconomyResponse bankBalance(String name)
/*     */   {
/* 235 */     GroupBank groupBank = this.api.getGroupBank(name);
/*     */ 
/* 237 */     if (groupBank == null) {
/* 238 */       return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.FAILURE, this.api.getMsg("GroupBankNotExists"));
/*     */     }
/*     */ 
/* 241 */     return new EconomyResponse(0.0D, groupBank.getBalance(), EconomyResponse.ResponseType.SUCCESS, null);
/*     */   }
/*     */ 
/*     */   public EconomyResponse bankHas(String name, double amount)
/*     */   {
/* 246 */     GroupBank groupBank = this.api.getGroupBank(name);
/*     */ 
/* 248 */     if (groupBank == null) {
/* 249 */       return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.FAILURE, this.api.getMsg("GroupBankNotExists"));
/*     */     }
/*     */ 
/* 252 */     int value = (int)amount;
/* 253 */     int balance = groupBank.getBalance();
/*     */ 
/* 255 */     if (balance >= value) {
/* 256 */       return new EconomyResponse(0.0D, balance, EconomyResponse.ResponseType.SUCCESS, null);
/*     */     }
/*     */ 
/* 259 */     return new EconomyResponse(0.0D, balance, EconomyResponse.ResponseType.FAILURE, String.format(this.api.getMsg("InsufficientXP"), new Object[] { this.api.currencyName(value) }));
/*     */   }
/*     */ 
/*     */   public EconomyResponse bankWithdraw(String name, double amount)
/*     */   {
/* 264 */     GroupBank groupBank = this.api.getGroupBank(name);
/*     */ 
/* 266 */     if (groupBank == null) {
/* 267 */       return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.FAILURE, this.api.getMsg("GroupBankNotExists"));
/*     */     }
/*     */ 
/* 270 */     int value = (int)amount;
/* 271 */     int balance = groupBank.getBalance();
/*     */ 
/* 273 */     if (value < 1) {
/* 274 */       return new EconomyResponse(0.0D, balance, EconomyResponse.ResponseType.FAILURE, this.api.getMsg("LessThanZero"));
/*     */     }
/*     */ 
/* 277 */     if (value > balance) {
/* 278 */       return new EconomyResponse(0.0D, balance, EconomyResponse.ResponseType.FAILURE, String.format(this.api.getMsg("InsufficientXP"), new Object[] { this.api.currencyName(value) }));
/*     */     }
/*     */ 
/* 281 */     groupBank.modifyBalance(-value);
/*     */ 
/* 283 */     return new EconomyResponse(value, balance - value, EconomyResponse.ResponseType.SUCCESS, null);
/*     */   }
/*     */ 
/*     */   public EconomyResponse bankDeposit(String name, double amount)
/*     */   {
/* 288 */     GroupBank groupBank = this.api.getGroupBank(name);
/*     */ 
/* 290 */     if (groupBank == null) {
/* 291 */       return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.FAILURE, this.api.getMsg("GroupBankNotExists"));
/*     */     }
/*     */ 
/* 294 */     int value = (int)amount;
/* 295 */     int balance = groupBank.getBalance();
/*     */ 
/* 297 */     if (value < 1) {
/* 298 */       return new EconomyResponse(0.0D, balance, EconomyResponse.ResponseType.FAILURE, this.api.getMsg("LessThanZero"));
/*     */     }
/*     */ 
/* 301 */     groupBank.modifyBalance(value);
/*     */ 
/* 303 */     return new EconomyResponse(value, balance + value, EconomyResponse.ResponseType.SUCCESS, null);
/*     */   }
/*     */ 
/*     */   public EconomyResponse isBankOwner(String name, String playerName)
/*     */   {
/* 308 */     GroupBank groupBank = this.api.getGroupBank(name);
/*     */ 
/* 310 */     if (groupBank == null) {
/* 311 */       return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.FAILURE, this.api.getMsg("GroupBankNotExists"));
/*     */     }
/*     */ 
/* 314 */     Account account = this.api.getAccount(name);
/*     */ 
/* 316 */     if (account == null) {
/* 317 */       return new EconomyResponse(0.0D, groupBank.getBalance(), EconomyResponse.ResponseType.FAILURE, this.api.getMsg("PlayerNotExist"));
/*     */     }
/*     */ 
/* 320 */     if (groupBank.getOwner().equalsIgnoreCase(name)) {
/* 321 */       return new EconomyResponse(0.0D, groupBank.getBalance(), EconomyResponse.ResponseType.SUCCESS, null);
/*     */     }
/*     */ 
/* 324 */     return new EconomyResponse(0.0D, groupBank.getBalance(), EconomyResponse.ResponseType.FAILURE, String.format(this.api.getMsg("PlayerNotOwner"), new Object[] { account.getName(), groupBank.getName() }));
/*     */   }
/*     */ 
/*     */   public EconomyResponse isBankMember(String name, String playerName)
/*     */   {
/* 329 */     GroupBank groupBank = this.api.getGroupBank(name);
/*     */ 
/* 331 */     if (groupBank == null) {
/* 332 */       return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.FAILURE, this.api.getMsg("GroupBankNotExists"));
/*     */     }
/*     */ 
/* 335 */     Account account = this.api.getAccount(name);
/*     */ 
/* 337 */     if (account == null) {
/* 338 */       return new EconomyResponse(0.0D, groupBank.getBalance(), EconomyResponse.ResponseType.FAILURE, this.api.getMsg("PlayerNotExist"));
/*     */     }
/*     */ 
/* 341 */     if (groupBank.groupMembers.getMembers().containsKey(playerName)) {
/* 342 */       return new EconomyResponse(0.0D, groupBank.getBalance(), EconomyResponse.ResponseType.SUCCESS, null);
/*     */     }
/*     */ 
/* 345 */     return new EconomyResponse(0.0D, groupBank.getBalance(), EconomyResponse.ResponseType.FAILURE, String.format(this.api.getMsg("NotAMemberOf"), new Object[] { groupBank.getName(), account.getName() }));
/*     */   }
/*     */ 
/*     */   public List<String> getBanks()
/*     */   {
/* 350 */     return this.api.getAllGroupBanks();
/*     */   }
/*     */ 
/*     */   public boolean createPlayerAccount(String playerName)
/*     */   {
/* 355 */     this.api.createAccount(playerName);
/*     */ 
/* 357 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean hasAccount(String playerName, String worldName)
/*     */   {
/* 362 */     return hasAccount(playerName);
/*     */   }
/*     */ 
/*     */   public double getBalance(String playerName, String world)
/*     */   {
/* 367 */     return getBalance(playerName);
/*     */   }
/*     */ 
/*     */   public boolean has(String playerName, String worldName, double amount)
/*     */   {
/* 372 */     return has(playerName, amount);
/*     */   }
/*     */ 
/*     */   public EconomyResponse withdrawPlayer(String playerName, String worldName, double amount)
/*     */   {
/* 377 */     return withdrawPlayer(playerName, amount);
/*     */   }
/*     */ 
/*     */   public EconomyResponse depositPlayer(String playerName, String worldName, double amount)
/*     */   {
/* 382 */     return depositPlayer(playerName, amount);
/*     */   }
/*     */ 
/*     */   public boolean createPlayerAccount(String playerName, String worldName)
/*     */   {
/* 387 */     return createPlayerAccount(playerName);
/*     */   }
/*     */ 
/*     */   public class EconomyServerListener
/*     */     implements Listener
/*     */   {
/*  65 */     Economy_XPBank economy = null;
/*     */ 
/*     */     public EconomyServerListener(Economy_XPBank economy_XPBank) {
/*  68 */       this.economy = economy_XPBank;
/*     */     }
/*     */ 
/*     */     @EventHandler(priority=EventPriority.MONITOR)
/*     */     public void onPluginEnable(PluginEnableEvent event) {
/*  73 */       if (this.economy.XPB == null) {
/*  74 */         Plugin eco = event.getPlugin();
/*     */ 
/*  76 */         if (eco.getDescription().getName().equals("XPBank")) {
/*  77 */           this.economy.XPB = ((XPBank)eco);
/*  78 */           Economy_XPBank.this.api = Economy_XPBank.this.XPB.getAPI();
/*  79 */           Economy_XPBank.log.info(String.format("[%s][Economy] %s hooked.", new Object[] { Economy_XPBank.this.plugin.getDescription().getName(), "XPBank" }));
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/*     */     @EventHandler(priority=EventPriority.MONITOR)
/*     */     public void onPluginDisable(PluginDisableEvent event) {
/*  86 */       if ((this.economy.XPB != null) && 
/*  87 */         (event.getPlugin().getDescription().getName().equals("XPBank"))) {
/*  88 */         this.economy.XPB = null;
/*  89 */         Economy_XPBank.log.info(String.format("[%s][Economy] %s unhooked.", new Object[] { Economy_XPBank.this.plugin.getDescription().getName(), "XPBank" }));
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\Vault.jar
 * Qualified Name:     net.milkbowl.vault.economy.plugins.Economy_XPBank
 * JD-Core Version:    0.6.2
 */