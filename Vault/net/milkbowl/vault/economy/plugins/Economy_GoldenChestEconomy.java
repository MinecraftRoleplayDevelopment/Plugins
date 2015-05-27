/*     */ package net.milkbowl.vault.economy.plugins;
/*     */ 
/*     */ import java.util.List;
/*     */ import java.util.logging.Logger;
/*     */ import me.igwb.GoldenChest.GoldenChestEconomy;
/*     */ import me.igwb.GoldenChest.Vault.VaultConnector;
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
/*     */ public class Economy_GoldenChestEconomy extends AbstractEconomy
/*     */ {
/*  35 */   private static final Logger log = Logger.getLogger("Minecraft");
/*     */ 
/*  37 */   private final String name = "GoldenChestEconomy";
/*  38 */   private Plugin plugin = null;
/*  39 */   private GoldenChestEconomy economy = null;
/*     */ 
/*     */   public Economy_GoldenChestEconomy(Plugin plugin)
/*     */   {
/*  43 */     this.plugin = plugin;
/*  44 */     Bukkit.getServer().getPluginManager().registerEvents(new EconomyServerListener(this), plugin);
/*     */ 
/*  46 */     if (this.economy == null) {
/*  47 */       Plugin ec = plugin.getServer().getPluginManager().getPlugin("GoldenChestEconomy");
/*  48 */       if ((ec != null) && (ec.isEnabled()) && (ec.getClass().getName().equals("me.igwb.GoldenChest.GoldenChestEconomy"))) {
/*  49 */         this.economy = ((GoldenChestEconomy)ec);
/*  50 */         log.info(String.format("[%s][Economy] %s hooked.", new Object[] { plugin.getDescription().getName(), "GoldenChestEconomy" }));
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean isEnabled()
/*     */   {
/*  88 */     if (this.economy == null) {
/*  89 */       return false;
/*     */     }
/*  91 */     return this.economy.isEnabled();
/*     */   }
/*     */ 
/*     */   public String getName()
/*     */   {
/*  97 */     return "GoldenChestEconomy";
/*     */   }
/*     */ 
/*     */   public boolean hasBankSupport()
/*     */   {
/* 102 */     return false;
/*     */   }
/*     */ 
/*     */   public int fractionalDigits()
/*     */   {
/* 107 */     return this.economy.getVaultConnector().fractionalDigits();
/*     */   }
/*     */ 
/*     */   public String format(double amount)
/*     */   {
/* 112 */     return this.economy.getVaultConnector().format(amount);
/*     */   }
/*     */ 
/*     */   public String currencyNamePlural()
/*     */   {
/* 117 */     return this.economy.getVaultConnector().currencyNamePlural();
/*     */   }
/*     */ 
/*     */   public String currencyNameSingular()
/*     */   {
/* 122 */     return this.economy.getVaultConnector().currencyNameSingular();
/*     */   }
/*     */ 
/*     */   public boolean hasAccount(String playerName)
/*     */   {
/* 127 */     return this.economy.getVaultConnector().hasAccount(playerName);
/*     */   }
/*     */ 
/*     */   public boolean hasAccount(String playerName, String worldName)
/*     */   {
/* 132 */     return this.economy.getVaultConnector().hasAccount(playerName, worldName);
/*     */   }
/*     */ 
/*     */   public double getBalance(String playerName)
/*     */   {
/* 137 */     return this.economy.getVaultConnector().getBalance(playerName);
/*     */   }
/*     */ 
/*     */   public double getBalance(String playerName, String world)
/*     */   {
/* 142 */     return this.economy.getVaultConnector().getBalance(playerName, world);
/*     */   }
/*     */ 
/*     */   public boolean has(String playerName, double amount)
/*     */   {
/* 147 */     return this.economy.getVaultConnector().has(playerName, amount);
/*     */   }
/*     */ 
/*     */   public boolean has(String playerName, String worldName, double amount)
/*     */   {
/* 152 */     return this.economy.getVaultConnector().has(playerName, worldName, amount);
/*     */   }
/*     */ 
/*     */   public EconomyResponse withdrawPlayer(String playerName, double amount)
/*     */   {
/* 158 */     if (amount < 0.0D) {
/* 159 */       return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.FAILURE, "Cannot withdraw negative funds");
/*     */     }
/*     */ 
/* 162 */     if (has(playerName, amount)) {
/* 163 */       this.economy.getVaultConnector().withdrawPlayer(playerName, amount);
/* 164 */       return new EconomyResponse(amount, getBalance(playerName), EconomyResponse.ResponseType.SUCCESS, null);
/*     */     }
/* 166 */     return new EconomyResponse(0.0D, getBalance(playerName), EconomyResponse.ResponseType.FAILURE, "Insufficient funds");
/*     */   }
/*     */ 
/*     */   public EconomyResponse withdrawPlayer(String playerName, String worldName, double amount)
/*     */   {
/* 173 */     return withdrawPlayer(playerName, amount);
/*     */   }
/*     */ 
/*     */   public EconomyResponse depositPlayer(String playerName, double amount)
/*     */   {
/* 178 */     if (amount < 0.0D) {
/* 179 */       return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.FAILURE, "Cannot desposit negative funds");
/*     */     }
/*     */ 
/* 182 */     this.economy.getVaultConnector().depositPlayer(playerName, amount);
/* 183 */     return new EconomyResponse(amount, getBalance(playerName), EconomyResponse.ResponseType.SUCCESS, null);
/*     */   }
/*     */ 
/*     */   public EconomyResponse depositPlayer(String playerName, String worldName, double amount)
/*     */   {
/* 189 */     return depositPlayer(playerName, amount);
/*     */   }
/*     */ 
/*     */   public EconomyResponse createBank(String name, String player)
/*     */   {
/* 194 */     return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Banks are not supported!");
/*     */   }
/*     */ 
/*     */   public EconomyResponse deleteBank(String name)
/*     */   {
/* 199 */     return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Banks are not supported!");
/*     */   }
/*     */ 
/*     */   public EconomyResponse bankBalance(String name)
/*     */   {
/* 204 */     return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Banks are not supported!");
/*     */   }
/*     */ 
/*     */   public EconomyResponse bankHas(String name, double amount)
/*     */   {
/* 209 */     return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Banks are not supported!");
/*     */   }
/*     */ 
/*     */   public EconomyResponse bankWithdraw(String name, double amount)
/*     */   {
/* 214 */     return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Banks are not supported!");
/*     */   }
/*     */ 
/*     */   public EconomyResponse bankDeposit(String name, double amount)
/*     */   {
/* 219 */     return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Banks are not supported!");
/*     */   }
/*     */ 
/*     */   public EconomyResponse isBankOwner(String name, String playerName)
/*     */   {
/* 224 */     return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Banks are not supported!");
/*     */   }
/*     */ 
/*     */   public EconomyResponse isBankMember(String name, String playerName)
/*     */   {
/* 229 */     return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Banks are not supported!");
/*     */   }
/*     */ 
/*     */   public List<String> getBanks()
/*     */   {
/* 234 */     return null;
/*     */   }
/*     */ 
/*     */   public boolean createPlayerAccount(String playerName)
/*     */   {
/* 239 */     return this.economy.getVaultConnector().createPlayerAccount(playerName);
/*     */   }
/*     */ 
/*     */   public boolean createPlayerAccount(String playerName, String worldName)
/*     */   {
/* 244 */     return this.economy.getVaultConnector().createPlayerAccount(playerName, worldName);
/*     */   }
/*     */ 
/*     */   public class EconomyServerListener
/*     */     implements Listener
/*     */   {
/*  56 */     Economy_GoldenChestEconomy economy = null;
/*     */ 
/*     */     public EconomyServerListener(Economy_GoldenChestEconomy economy_GoldenChestEconomy) {
/*  59 */       this.economy = economy_GoldenChestEconomy;
/*     */     }
/*     */ 
/*     */     @EventHandler(priority=EventPriority.MONITOR)
/*     */     public void onPluginEnable(PluginEnableEvent event) {
/*  64 */       if (this.economy.economy == null) {
/*  65 */         Plugin ec = event.getPlugin();
/*     */ 
/*  67 */         if ((ec.getDescription().getName().equals("GoldenChestEconomy")) && (ec.getClass().getName().equals("me.igwb.GoldenChest.GoldenChestEconomy"))) {
/*  68 */           this.economy.economy = ((GoldenChestEconomy)ec);
/*  69 */           Economy_GoldenChestEconomy.log.info(String.format("[%s][Economy] %s hooked.", new Object[] { Economy_GoldenChestEconomy.this.plugin.getDescription().getName(), "GoldenChestEconomy" }));
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/*     */     @EventHandler(priority=EventPriority.MONITOR)
/*     */     public void onPluginDisable(PluginDisableEvent event) {
/*  76 */       if ((this.economy.economy != null) && 
/*  77 */         (event.getPlugin().getDescription().getName().equals("GoldenChestEconomy"))) {
/*  78 */         this.economy.economy = null;
/*  79 */         Economy_GoldenChestEconomy.log.info(String.format("[%s][Economy] %s unhooked.", new Object[] { Economy_GoldenChestEconomy.this.plugin.getDescription().getName(), "GoldenChestEconomy" }));
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\Vault.jar
 * Qualified Name:     net.milkbowl.vault.economy.plugins.Economy_GoldenChestEconomy
 * JD-Core Version:    0.6.2
 */