/*     */ package net.milkbowl.vault.economy.plugins;
/*     */ 
/*     */ import com.earth2me.essentials.Essentials;
/*     */ import com.earth2me.essentials.api.Economy;
/*     */ import com.earth2me.essentials.api.NoLoanPermittedException;
/*     */ import com.earth2me.essentials.api.UserDoesNotExistException;
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
/*     */ public class Economy_Essentials extends AbstractEconomy
/*     */ {
/*  39 */   private static final Logger log = Logger.getLogger("Minecraft");
/*     */ 
/*  41 */   private final String name = "Essentials Economy";
/*  42 */   private Plugin plugin = null;
/*  43 */   private Essentials ess = null;
/*     */ 
/*     */   public Economy_Essentials(Plugin plugin) {
/*  46 */     this.plugin = plugin;
/*  47 */     Bukkit.getServer().getPluginManager().registerEvents(new EconomyServerListener(this), plugin);
/*     */ 
/*  50 */     if (this.ess == null) {
/*  51 */       Plugin essentials = plugin.getServer().getPluginManager().getPlugin("Essentials");
/*  52 */       if ((essentials != null) && (essentials.isEnabled())) {
/*  53 */         this.ess = ((Essentials)essentials);
/*  54 */         log.info(String.format("[%s][Economy] %s hooked.", new Object[] { plugin.getDescription().getName(), "Essentials Economy" }));
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean isEnabled()
/*     */   {
/*  61 */     if (this.ess == null) {
/*  62 */       return false;
/*     */     }
/*  64 */     return this.ess.isEnabled();
/*     */   }
/*     */ 
/*     */   public String getName()
/*     */   {
/*  70 */     return "Essentials Economy";
/*     */   }
/*     */ 
/*     */   public double getBalance(String playerName)
/*     */   {
/*     */     double balance;
/*     */     try
/*     */     {
/*  78 */       balance = Economy.getMoney(playerName);
/*     */     } catch (UserDoesNotExistException e) {
/*  80 */       createPlayerAccount(playerName);
/*  81 */       balance = 0.0D;
/*     */     }
/*     */ 
/*  84 */     return balance;
/*     */   }
/*     */ 
/*     */   public boolean createPlayerAccount(String playerName)
/*     */   {
/*  89 */     if (hasAccount(playerName)) {
/*  90 */       return false;
/*     */     }
/*  92 */     return Economy.createNPC(playerName);
/*     */   }
/*     */ 
/*     */   public EconomyResponse withdrawPlayer(String playerName, double amount)
/*     */   {
/*  97 */     if (amount < 0.0D) {
/*  98 */       return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.FAILURE, "Cannot withdraw negative funds");
/* 103 */     }
/*     */ String errorMessage = null;
/*     */     double balance;
/*     */     EconomyResponse.ResponseType type;
/*     */     try {
/* 106 */       Economy.subtract(playerName, amount);
/* 107 */       balance = Economy.getMoney(playerName);
/* 108 */       type = EconomyResponse.ResponseType.SUCCESS;
/*     */     } catch (UserDoesNotExistException e) {
/* 110 */       if (createPlayerAccount(playerName)) {
/* 111 */         return withdrawPlayer(playerName, amount);
/*     */       }
/* 113 */       amount = 0.0D;
/* 114 */       balance = 0.0D;
/* 115 */       type = EconomyResponse.ResponseType.FAILURE;
/* 116 */       errorMessage = "User does not exist";
/*     */     }
/*     */     catch (NoLoanPermittedException e) {
/*     */       try {
/* 120 */         balance = Economy.getMoney(playerName);
/* 121 */         amount = 0.0D;
/* 122 */         type = EconomyResponse.ResponseType.FAILURE;
/* 123 */         errorMessage = "Loan was not permitted";
/*     */       } catch (UserDoesNotExistException e1) {
/* 125 */         amount = 0.0D;
/* 126 */         balance = 0.0D;
/* 127 */         type = EconomyResponse.ResponseType.FAILURE;
/* 128 */         errorMessage = "User does not exist";
/*     */       }
/*     */     }
/*     */ 
/* 132 */     return new EconomyResponse(amount, balance, type, errorMessage);
/*     */   }
/*     */ 
/*     */   public EconomyResponse depositPlayer(String playerName, double amount)
/*     */   {
/* 137 */     if (amount < 0.0D) {
/* 138 */       return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.FAILURE, "Cannot desposit negative funds");
/* 143 */     }
/*     */ String errorMessage = null;
/*     */     double balance;
/*     */     EconomyResponse.ResponseType type;
/*     */     try {
/* 146 */       Economy.add(playerName, amount);
/* 147 */       balance = Economy.getMoney(playerName);
/* 148 */       type = EconomyResponse.ResponseType.SUCCESS;
/*     */     } catch (UserDoesNotExistException e) {
/* 150 */       if (createPlayerAccount(playerName)) {
/* 151 */         return depositPlayer(playerName, amount);
/*     */       }
/* 153 */       amount = 0.0D;
/* 154 */       balance = 0.0D;
/* 155 */       type = EconomyResponse.ResponseType.FAILURE;
/* 156 */       errorMessage = "User does not exist";
/*     */     }
/*     */     catch (NoLoanPermittedException e) {
/*     */       try {
/* 160 */         balance = Economy.getMoney(playerName);
/* 161 */         amount = 0.0D;
/* 162 */         type = EconomyResponse.ResponseType.FAILURE;
/* 163 */         errorMessage = "Loan was not permitted";
/*     */       } catch (UserDoesNotExistException e1) {
/* 165 */         balance = 0.0D;
/* 166 */         amount = 0.0D;
/* 167 */         type = EconomyResponse.ResponseType.FAILURE;
/* 168 */         errorMessage = "Loan was not permitted";
/*     */       }
/*     */     }
/*     */ 
/* 172 */     return new EconomyResponse(amount, balance, type, errorMessage);
/*     */   }
/*     */ 
/*     */   public String format(double amount)
/*     */   {
/* 207 */     return Economy.format(amount);
/*     */   }
/*     */ 
/*     */   public String currencyNameSingular()
/*     */   {
/* 212 */     return "";
/*     */   }
/*     */ 
/*     */   public String currencyNamePlural()
/*     */   {
/* 217 */     return "";
/*     */   }
/*     */ 
/*     */   public boolean has(String playerName, double amount)
/*     */   {
/*     */     try {
/* 223 */       return Economy.hasEnough(playerName, amount); } catch (UserDoesNotExistException e) {
/*     */     }
/* 225 */     return false;
/*     */   }
/*     */ 
/*     */   public EconomyResponse createBank(String name, String player)
/*     */   {
/* 231 */     return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Essentials Eco does not support bank accounts!");
/*     */   }
/*     */ 
/*     */   public EconomyResponse deleteBank(String name)
/*     */   {
/* 236 */     return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Essentials Eco does not support bank accounts!");
/*     */   }
/*     */ 
/*     */   public EconomyResponse bankHas(String name, double amount)
/*     */   {
/* 241 */     return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Essentials Eco does not support bank accounts!");
/*     */   }
/*     */ 
/*     */   public EconomyResponse bankWithdraw(String name, double amount)
/*     */   {
/* 246 */     return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Essentials Eco does not support bank accounts!");
/*     */   }
/*     */ 
/*     */   public EconomyResponse bankDeposit(String name, double amount)
/*     */   {
/* 251 */     return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Essentials Eco does not support bank accounts!");
/*     */   }
/*     */ 
/*     */   public EconomyResponse isBankOwner(String name, String playerName)
/*     */   {
/* 256 */     return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Essentials Eco does not support bank accounts!");
/*     */   }
/*     */ 
/*     */   public EconomyResponse isBankMember(String name, String playerName)
/*     */   {
/* 261 */     return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Essentials Eco does not support bank accounts!");
/*     */   }
/*     */ 
/*     */   public EconomyResponse bankBalance(String name)
/*     */   {
/* 266 */     return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Essentials Eco does not support bank accounts!");
/*     */   }
/*     */ 
/*     */   public List<String> getBanks()
/*     */   {
/* 271 */     return new ArrayList();
/*     */   }
/*     */ 
/*     */   public boolean hasBankSupport()
/*     */   {
/* 276 */     return false;
/*     */   }
/*     */ 
/*     */   public boolean hasAccount(String playerName)
/*     */   {
/* 281 */     return Economy.playerExists(playerName);
/*     */   }
/*     */ 
/*     */   public int fractionalDigits()
/*     */   {
/* 286 */     return -1;
/*     */   }
/*     */ 
/*     */   public boolean hasAccount(String playerName, String worldName)
/*     */   {
/* 291 */     return hasAccount(playerName);
/*     */   }
/*     */ 
/*     */   public double getBalance(String playerName, String world)
/*     */   {
/* 296 */     return getBalance(playerName);
/*     */   }
/*     */ 
/*     */   public boolean has(String playerName, String worldName, double amount)
/*     */   {
/* 301 */     return has(playerName, amount);
/*     */   }
/*     */ 
/*     */   public EconomyResponse withdrawPlayer(String playerName, String worldName, double amount)
/*     */   {
/* 306 */     return withdrawPlayer(playerName, amount);
/*     */   }
/*     */ 
/*     */   public EconomyResponse depositPlayer(String playerName, String worldName, double amount)
/*     */   {
/* 311 */     return depositPlayer(playerName, amount);
/*     */   }
/*     */ 
/*     */   public boolean createPlayerAccount(String playerName, String worldName)
/*     */   {
/* 316 */     return createPlayerAccount(playerName);
/*     */   }
/*     */ 
/*     */   public class EconomyServerListener
/*     */     implements Listener
/*     */   {
/* 176 */     Economy_Essentials economy = null;
/*     */ 
/*     */     public EconomyServerListener(Economy_Essentials economy) {
/* 179 */       this.economy = economy;
/*     */     }
/*     */ 
/*     */     @EventHandler(priority=EventPriority.MONITOR)
/*     */     public void onPluginEnable(PluginEnableEvent event) {
/* 184 */       if (this.economy.ess == null) {
/* 185 */         Plugin essentials = event.getPlugin();
/*     */ 
/* 187 */         if (essentials.getDescription().getName().equals("Essentials")) {
/* 188 */           this.economy.ess = ((Essentials)essentials);
/* 189 */           Economy_Essentials.log.info(String.format("[%s][Economy] %s hooked.", new Object[] { Economy_Essentials.this.plugin.getDescription().getName(), "Essentials Economy" }));
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/*     */     @EventHandler(priority=EventPriority.MONITOR)
/*     */     public void onPluginDisable(PluginDisableEvent event) {
/* 196 */       if ((this.economy.ess != null) && 
/* 197 */         (event.getPlugin().getDescription().getName().equals("Essentials"))) {
/* 198 */         this.economy.ess = null;
/* 199 */         Economy_Essentials.log.info(String.format("[%s][Economy] %s unhooked.", new Object[] { Economy_Essentials.this.plugin.getDescription().getName(), "Essentials Economy" }));
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\Vault.jar
 * Qualified Name:     net.milkbowl.vault.economy.plugins.Economy_Essentials
 * JD-Core Version:    0.6.2
 */