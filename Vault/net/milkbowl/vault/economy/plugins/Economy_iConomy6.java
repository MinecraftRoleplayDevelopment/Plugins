/*     */ package net.milkbowl.vault.economy.plugins;
/*     */ 
/*     */ import com.iCo6.Constants.Nodes;
/*     */ import com.iCo6.iConomy;
/*     */ import com.iCo6.system.Account;
/*     */ import com.iCo6.system.Accounts;
/*     */ import com.iCo6.system.Holdings;
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
/*     */ public class Economy_iConomy6 extends AbstractEconomy
/*     */ {
/*  39 */   private static final Logger log = Logger.getLogger("Minecraft");
/*     */ 
/*  41 */   private String name = "iConomy ";
/*  42 */   private Plugin plugin = null;
/*  43 */   protected iConomy economy = null;
/*     */   private Accounts accounts;
/*     */ 
/*     */   public Economy_iConomy6(Plugin plugin)
/*     */   {
/*  47 */     this.plugin = plugin;
/*  48 */     Bukkit.getServer().getPluginManager().registerEvents(new EconomyServerListener(this), plugin);
/*  49 */     log.warning("iConomy - If you are using Flatfile storage be aware that versions 6, 7 and 8 have a CRITICAL bug which can wipe ALL iconomy data.");
/*  50 */     log.warning("if you're using Votifier, or any other plugin which handles economy data in a threaded manner your server is at risk!");
/*  51 */     log.warning("it is highly suggested to use SQL with iCo6 or to use an alternative economy plugin!");
/*     */ 
/*  53 */     if (this.economy == null) {
/*  54 */       Plugin ec = plugin.getServer().getPluginManager().getPlugin("iConomy");
/*  55 */       if ((ec != null) && (ec.isEnabled()) && (ec.getClass().getName().equals("com.iCo6.iConomy"))) {
/*  56 */         String version = ec.getDescription().getVersion().split("\\.")[0];
/*  57 */         this.name += version;
/*  58 */         this.economy = ((iConomy)ec);
/*  59 */         this.accounts = new Accounts();
/*  60 */         log.info(String.format("[%s][Economy] %s hooked.", new Object[] { plugin.getDescription().getName(), this.name }));
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean isEnabled()
/*     */   {
/*  99 */     if (this.economy == null) {
/* 100 */       return false;
/*     */     }
/* 102 */     return this.economy.isEnabled();
/*     */   }
/*     */ 
/*     */   public String getName()
/*     */   {
/* 108 */     return this.name;
/*     */   }
/*     */ 
/*     */   public String format(double amount)
/*     */   {
/* 113 */     return iConomy.format(amount);
/*     */   }
/*     */ 
/*     */   public String currencyNameSingular()
/*     */   {
/* 118 */     return (String)Constants.Nodes.Major.getStringList().get(0);
/*     */   }
/*     */ 
/*     */   public String currencyNamePlural()
/*     */   {
/* 123 */     return (String)Constants.Nodes.Major.getStringList().get(1);
/*     */   }
/*     */ 
/*     */   public double getBalance(String playerName)
/*     */   {
/* 128 */     if (this.accounts.exists(playerName)) {
/* 129 */       return this.accounts.get(playerName).getHoldings().getBalance().doubleValue();
/*     */     }
/* 131 */     return 0.0D;
/*     */   }
/*     */ 
/*     */   public EconomyResponse withdrawPlayer(String playerName, double amount)
/*     */   {
/* 137 */     if (amount < 0.0D) {
/* 138 */       return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.FAILURE, "Cannot withdraw negative funds");
/*     */     }
/*     */ 
/* 141 */     Holdings holdings = this.accounts.get(playerName).getHoldings();
/* 142 */     if (holdings.hasEnough(amount)) {
/* 143 */       holdings.subtract(amount);
/* 144 */       return new EconomyResponse(amount, holdings.getBalance().doubleValue(), EconomyResponse.ResponseType.SUCCESS, null);
/*     */     }
/* 146 */     return new EconomyResponse(0.0D, holdings.getBalance().doubleValue(), EconomyResponse.ResponseType.FAILURE, "Insufficient funds");
/*     */   }
/*     */ 
/*     */   public EconomyResponse depositPlayer(String playerName, double amount)
/*     */   {
/* 152 */     if (amount < 0.0D) {
/* 153 */       return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.FAILURE, "Cannot desposit negative funds");
/*     */     }
/*     */ 
/* 156 */     Holdings holdings = this.accounts.get(playerName).getHoldings();
/* 157 */     holdings.add(amount);
/* 158 */     return new EconomyResponse(amount, holdings.getBalance().doubleValue(), EconomyResponse.ResponseType.SUCCESS, null);
/*     */   }
/*     */ 
/*     */   public boolean has(String playerName, double amount)
/*     */   {
/* 163 */     return getBalance(playerName) >= amount;
/*     */   }
/*     */ 
/*     */   public EconomyResponse createBank(String name, String player)
/*     */   {
/* 168 */     if (this.accounts.exists(name)) {
/* 169 */       return new EconomyResponse(0.0D, this.accounts.get(name).getHoldings().getBalance().doubleValue(), EconomyResponse.ResponseType.FAILURE, "That account already exists.");
/*     */     }
/* 171 */     boolean created = this.accounts.create(name);
/* 172 */     if (created) {
/* 173 */       return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.SUCCESS, "");
/*     */     }
/* 175 */     return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.FAILURE, "There was an error creating the account");
/*     */   }
/*     */ 
/*     */   public EconomyResponse deleteBank(String name)
/*     */   {
/* 182 */     if (this.accounts.exists(name)) {
/* 183 */       this.accounts.remove(new String[] { name });
/* 184 */       return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.SUCCESS, "");
/*     */     }
/* 186 */     return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.FAILURE, "That bank account does not exist.");
/*     */   }
/*     */ 
/*     */   public EconomyResponse bankHas(String name, double amount)
/*     */   {
/* 191 */     if (has(name, amount)) {
/* 192 */       return new EconomyResponse(0.0D, amount, EconomyResponse.ResponseType.SUCCESS, "");
/*     */     }
/* 194 */     return new EconomyResponse(0.0D, this.accounts.get(name).getHoldings().getBalance().doubleValue(), EconomyResponse.ResponseType.FAILURE, "The account does not have enough!");
/*     */   }
/*     */ 
/*     */   public EconomyResponse bankWithdraw(String name, double amount)
/*     */   {
/* 200 */     if (amount < 0.0D) {
/* 201 */       return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.FAILURE, "Cannot withdraw negative funds");
/*     */     }
/*     */ 
/* 204 */     return withdrawPlayer(name, amount);
/*     */   }
/*     */ 
/*     */   public EconomyResponse bankDeposit(String name, double amount)
/*     */   {
/* 209 */     if (amount < 0.0D) {
/* 210 */       return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.FAILURE, "Cannot desposit negative funds");
/*     */     }
/*     */ 
/* 213 */     return depositPlayer(name, amount);
/*     */   }
/*     */ 
/*     */   public EconomyResponse isBankOwner(String name, String playerName)
/*     */   {
/* 218 */     return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "iConomy 6 does not support Bank owners.");
/*     */   }
/*     */ 
/*     */   public EconomyResponse isBankMember(String name, String playerName)
/*     */   {
/* 223 */     return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "iConomy 6 does not support Bank members.");
/*     */   }
/*     */ 
/*     */   public EconomyResponse bankBalance(String name)
/*     */   {
/* 228 */     if (!this.accounts.exists(name)) {
/* 229 */       return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.FAILURE, "There is no bank account with that name");
/*     */     }
/* 231 */     return new EconomyResponse(0.0D, this.accounts.get(name).getHoldings().getBalance().doubleValue(), EconomyResponse.ResponseType.SUCCESS, null);
/*     */   }
/*     */ 
/*     */   public List<String> getBanks()
/*     */   {
/* 237 */     throw new UnsupportedOperationException("iConomy does not support listing of bank accounts");
/*     */   }
/*     */ 
/*     */   public boolean hasBankSupport()
/*     */   {
/* 242 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean hasAccount(String playerName)
/*     */   {
/* 247 */     return this.accounts.exists(playerName);
/*     */   }
/*     */ 
/*     */   public boolean createPlayerAccount(String playerName)
/*     */   {
/* 252 */     if (hasAccount(playerName)) {
/* 253 */       return false;
/*     */     }
/* 255 */     return this.accounts.create(playerName);
/*     */   }
/*     */ 
/*     */   public int fractionalDigits()
/*     */   {
/* 260 */     return -1;
/*     */   }
/*     */ 
/*     */   public boolean hasAccount(String playerName, String worldName)
/*     */   {
/* 265 */     return hasAccount(playerName);
/*     */   }
/*     */ 
/*     */   public double getBalance(String playerName, String world)
/*     */   {
/* 270 */     return getBalance(playerName);
/*     */   }
/*     */ 
/*     */   public boolean has(String playerName, String worldName, double amount)
/*     */   {
/* 275 */     return has(playerName, amount);
/*     */   }
/*     */ 
/*     */   public EconomyResponse withdrawPlayer(String playerName, String worldName, double amount)
/*     */   {
/* 280 */     return withdrawPlayer(playerName, amount);
/*     */   }
/*     */ 
/*     */   public EconomyResponse depositPlayer(String playerName, String worldName, double amount)
/*     */   {
/* 285 */     return depositPlayer(playerName, amount);
/*     */   }
/*     */ 
/*     */   public boolean createPlayerAccount(String playerName, String worldName)
/*     */   {
/* 290 */     return createPlayerAccount(playerName);
/*     */   }
/*     */ 
/*     */   public class EconomyServerListener
/*     */     implements Listener
/*     */   {
/*  66 */     Economy_iConomy6 economy = null;
/*     */ 
/*     */     public EconomyServerListener(Economy_iConomy6 economy) {
/*  69 */       this.economy = economy;
/*     */     }
/*     */ 
/*     */     @EventHandler(priority=EventPriority.MONITOR)
/*     */     public void onPluginEnable(PluginEnableEvent event) {
/*  74 */       if (this.economy.economy == null) {
/*  75 */         Plugin ec = event.getPlugin();
/*  76 */         if (ec.getClass().getName().equals("com.iCo6.iConomy")) {
/*  77 */           String version = ec.getDescription().getVersion().split("\\.")[0];
/*  78 */           Economy_iConomy6.access$084(Economy_iConomy6.this, version);
/*  79 */           this.economy.economy = ((iConomy)ec);
/*  80 */           Economy_iConomy6.this.accounts = new Accounts();
/*  81 */           Economy_iConomy6.log.info(String.format("[%s][Economy] %s hooked.", new Object[] { Economy_iConomy6.this.plugin.getDescription().getName(), this.economy.name }));
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/*     */     @EventHandler(priority=EventPriority.MONITOR)
/*     */     public void onPluginDisable(PluginDisableEvent event) {
/*  88 */       if ((this.economy.economy != null) && 
/*  89 */         (event.getPlugin().getDescription().getName().equals("iConomy"))) {
/*  90 */         this.economy.economy = null;
/*  91 */         Economy_iConomy6.log.info(String.format("[%s][Economy] %s unhooked.", new Object[] { Economy_iConomy6.this.plugin.getDescription().getName(), this.economy.name }));
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\Vault.jar
 * Qualified Name:     net.milkbowl.vault.economy.plugins.Economy_iConomy6
 * JD-Core Version:    0.6.2
 */