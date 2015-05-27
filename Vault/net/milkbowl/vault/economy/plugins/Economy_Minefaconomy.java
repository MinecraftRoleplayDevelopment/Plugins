/*     */ package net.milkbowl.vault.economy.plugins;
/*     */ 
/*     */ import java.util.List;
/*     */ import java.util.logging.Logger;
/*     */ import me.coniin.plugins.minefaconomy.Minefaconomy;
/*     */ import me.coniin.plugins.minefaconomy.vault.VaultLayer;
/*     */ import net.milkbowl.vault.economy.AbstractEconomy;
/*     */ import net.milkbowl.vault.economy.EconomyResponse;
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
/*     */ public class Economy_Minefaconomy extends AbstractEconomy
/*     */ {
/*  19 */   private static final Logger log = Logger.getLogger("Minecraft");
/*     */ 
/*  21 */   private final String name = "Minefaconomy";
/*     */ 
/*  23 */   private Plugin plugin = null;
/*  24 */   private Minefaconomy economy = null;
/*     */ 
/*     */   public Economy_Minefaconomy(Plugin plugin) { // Byte code:
/*     */     //   0: aload_0
/*     */     //   1: invokespecial 4	net/milkbowl/vault/economy/AbstractEconomy:<init>	()V
/*     */     //   4: aload_0
/*     */     //   5: ldc 5
/*     */     //   7: putfield 6	net/milkbowl/vault/economy/plugins/Economy_Minefaconomy:name	Ljava/lang/String;
/*     */     //   10: aload_0
/*     */     //   11: aconst_null
/*     */     //   12: putfield 2	net/milkbowl/vault/economy/plugins/Economy_Minefaconomy:plugin	Lorg/bukkit/plugin/Plugin;
/*     */     //   15: aload_0
/*     */     //   16: aconst_null
/*     */     //   17: putfield 3	net/milkbowl/vault/economy/plugins/Economy_Minefaconomy:economy	Lme/coniin/plugins/minefaconomy/Minefaconomy;
/*     */     //   20: aload_0
/*     */     //   21: aload_1
/*     */     //   22: putfield 2	net/milkbowl/vault/economy/plugins/Economy_Minefaconomy:plugin	Lorg/bukkit/plugin/Plugin;
/*     */     //   25: invokestatic 7	org/bukkit/Bukkit:getServer	()Lorg/bukkit/Server;
/*     */     //   28: invokeinterface 8 1 0
/*     */     //   33: new 9	net/milkbowl/vault/economy/plugins/Economy_Minefaconomy$EconomyServerListener
/*     */     //   36: dup
/*     */     //   37: aload_0
/*     */     //   38: aload_0
/*     */     //   39: invokespecial 10	net/milkbowl/vault/economy/plugins/Economy_Minefaconomy$EconomyServerListener:<init>	(Lnet/milkbowl/vault/economy/plugins/Economy_Minefaconomy;Lnet/milkbowl/vault/economy/plugins/Economy_Minefaconomy;)V
/*     */     //   42: aload_1
/*     */     //   43: invokeinterface 11 3 0
/*     */     //   48: aconst_null
/*     */     //   49: astore_2
/*     */     //   50: aload_0
/*     */     //   51: getfield 3	net/milkbowl/vault/economy/plugins/Economy_Minefaconomy:economy	Lme/coniin/plugins/minefaconomy/Minefaconomy;
/*     */     //   54: ifnonnull +30 -> 84
/*     */     //   57: aload_1
/*     */     //   58: invokeinterface 12 1 0
/*     */     //   63: invokeinterface 8 1 0
/*     */     //   68: ldc 5
/*     */     //   70: invokeinterface 13 2 0
/*     */     //   75: astore_2
/*     */     //   76: getstatic 1	net/milkbowl/vault/economy/plugins/Economy_Minefaconomy:log	Ljava/util/logging/Logger;
/*     */     //   79: ldc 14
/*     */     //   81: invokevirtual 15	java/util/logging/Logger:info	(Ljava/lang/String;)V
/*     */     //   84: aload_2
/*     */     //   85: ifnull +58 -> 143
/*     */     //   88: aload_2
/*     */     //   89: invokeinterface 16 1 0
/*     */     //   94: ifeq +49 -> 143
/*     */     //   97: aload_0
/*     */     //   98: aload_2
/*     */     //   99: checkcast 17	me/coniin/plugins/minefaconomy/Minefaconomy
/*     */     //   102: putfield 3	net/milkbowl/vault/economy/plugins/Economy_Minefaconomy:economy	Lme/coniin/plugins/minefaconomy/Minefaconomy;
/*     */     //   105: getstatic 1	net/milkbowl/vault/economy/plugins/Economy_Minefaconomy:log	Ljava/util/logging/Logger;
/*     */     //   108: ldc 18
/*     */     //   110: iconst_2
/*     */     //   111: anewarray 19	java/lang/Object
/*     */     //   114: dup
/*     */     //   115: iconst_0
/*     */     //   116: aload_1
/*     */     //   117: invokeinterface 20 1 0
/*     */     //   122: invokevirtual 21	org/bukkit/plugin/PluginDescriptionFile:getName	()Ljava/lang/String;
/*     */     //   125: aastore
/*     */     //   126: dup
/*     */     //   127: iconst_1
/*     */     //   128: aload_0
/*     */     //   129: invokevirtual 22	java/lang/Object:getClass	()Ljava/lang/Class;
/*     */     //   132: pop
/*     */     //   133: ldc 5
/*     */     //   135: aastore
/*     */     //   136: invokestatic 23	java/lang/String:format	(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
/*     */     //   139: invokevirtual 15	java/util/logging/Logger:info	(Ljava/lang/String;)V
/*     */     //   142: return
/*     */     //   143: getstatic 1	net/milkbowl/vault/economy/plugins/Economy_Minefaconomy:log	Ljava/util/logging/Logger;
/*     */     //   146: ldc 24
/*     */     //   148: invokevirtual 15	java/util/logging/Logger:info	(Ljava/lang/String;)V
/*     */     //   151: return } 
/*  75 */   public boolean isEnabled() { return (this.economy != null) && (this.economy.isEnabled()); }
/*     */ 
/*     */ 
/*     */   public String getName()
/*     */   {
/*  80 */     return "Minefaconomy";
/*     */   }
/*     */ 
/*     */   public int fractionalDigits()
/*     */   {
/*  85 */     return Minefaconomy.vaultLayer.fractionalDigits();
/*     */   }
/*     */ 
/*     */   public String format(double amount)
/*     */   {
/*  90 */     return Minefaconomy.vaultLayer.format(amount);
/*     */   }
/*     */ 
/*     */   public String currencyNamePlural()
/*     */   {
/*  95 */     return Minefaconomy.vaultLayer.currencyNamePlural();
/*     */   }
/*     */ 
/*     */   public String currencyNameSingular()
/*     */   {
/* 100 */     return Minefaconomy.vaultLayer.currencyNameSingular();
/*     */   }
/*     */ 
/*     */   public boolean hasAccount(String playerName)
/*     */   {
/* 105 */     return Minefaconomy.vaultLayer.hasAccount(playerName);
/*     */   }
/*     */ 
/*     */   public boolean hasAccount(String playerName, String worldName)
/*     */   {
/* 110 */     return Minefaconomy.vaultLayer.hasAccount(playerName);
/*     */   }
/*     */ 
/*     */   public double getBalance(String playerName)
/*     */   {
/* 115 */     return Minefaconomy.vaultLayer.getBalance(playerName);
/*     */   }
/*     */ 
/*     */   public double getBalance(String playerName, String world)
/*     */   {
/* 120 */     return Minefaconomy.vaultLayer.getBalance(playerName);
/*     */   }
/*     */ 
/*     */   public boolean has(String playerName, double amount)
/*     */   {
/* 125 */     return Minefaconomy.vaultLayer.has(playerName, amount);
/*     */   }
/*     */ 
/*     */   public boolean has(String playerName, String worldName, double amount)
/*     */   {
/* 130 */     return Minefaconomy.vaultLayer.has(playerName, amount);
/*     */   }
/*     */ 
/*     */   public EconomyResponse withdrawPlayer(String playerName, double amount)
/*     */   {
/* 135 */     return Minefaconomy.vaultLayer.withdrawPlayer(playerName, amount);
/*     */   }
/*     */ 
/*     */   public EconomyResponse withdrawPlayer(String playerName, String worldName, double amount)
/*     */   {
/* 141 */     return Minefaconomy.vaultLayer.withdrawPlayer(playerName, amount);
/*     */   }
/*     */ 
/*     */   public EconomyResponse depositPlayer(String playerName, double amount)
/*     */   {
/* 146 */     return Minefaconomy.vaultLayer.depositPlayer(playerName, amount);
/*     */   }
/*     */ 
/*     */   public EconomyResponse depositPlayer(String playerName, String worldName, double amount)
/*     */   {
/* 152 */     return Minefaconomy.vaultLayer.depositPlayer(playerName, amount);
/*     */   }
/*     */ 
/*     */   public boolean createPlayerAccount(String playerName)
/*     */   {
/* 157 */     return Minefaconomy.vaultLayer.createPlayerAccount(playerName);
/*     */   }
/*     */ 
/*     */   public boolean createPlayerAccount(String playerName, String worldName)
/*     */   {
/* 162 */     return Minefaconomy.vaultLayer.createPlayerAccount(playerName);
/*     */   }
/*     */ 
/*     */   public boolean hasBankSupport()
/*     */   {
/* 167 */     return Minefaconomy.vaultLayer.hasBankSupport();
/*     */   }
/*     */ 
/*     */   public EconomyResponse createBank(String name, String player)
/*     */   {
/* 172 */     return Minefaconomy.vaultLayer.createBank(name, player);
/*     */   }
/*     */ 
/*     */   public EconomyResponse deleteBank(String name)
/*     */   {
/* 177 */     return Minefaconomy.vaultLayer.deleteBank(name);
/*     */   }
/*     */ 
/*     */   public EconomyResponse bankBalance(String name)
/*     */   {
/* 182 */     return Minefaconomy.vaultLayer.bankBalance(name);
/*     */   }
/*     */ 
/*     */   public EconomyResponse bankHas(String name, double amount)
/*     */   {
/* 187 */     return Minefaconomy.vaultLayer.bankHas(name, amount);
/*     */   }
/*     */ 
/*     */   public EconomyResponse bankWithdraw(String name, double amount)
/*     */   {
/* 192 */     return Minefaconomy.vaultLayer.bankWithdraw(name, amount);
/*     */   }
/*     */ 
/*     */   public EconomyResponse bankDeposit(String name, double amount)
/*     */   {
/* 197 */     return Minefaconomy.vaultLayer.bankDeposit(name, amount);
/*     */   }
/*     */ 
/*     */   public EconomyResponse isBankOwner(String name, String playerName)
/*     */   {
/* 202 */     return Minefaconomy.vaultLayer.isBankOwner(name, playerName);
/*     */   }
/*     */ 
/*     */   public EconomyResponse isBankMember(String name, String playerName)
/*     */   {
/* 207 */     return Minefaconomy.vaultLayer.isBankMember(name, playerName);
/*     */   }
/*     */ 
/*     */   public List<String> getBanks()
/*     */   {
/* 212 */     return Minefaconomy.vaultLayer.getBanks();
/*     */   }
/*     */ 
/*     */   public class EconomyServerListener
/*     */     implements Listener
/*     */   {
/*  44 */     Economy_Minefaconomy economy_minefaconomy = null;
/*     */ 
/*     */     public EconomyServerListener(Economy_Minefaconomy economy_minefaconomy) {
/*  47 */       this.economy_minefaconomy = economy_minefaconomy;
/*     */     }
/*     */ 
/*     */     @EventHandler(priority=EventPriority.MONITOR)
/*     */     public void onPluginEnable(PluginEnableEvent event) {
/*  52 */       if (this.economy_minefaconomy.economy == null) {
/*  53 */         Plugin mfc = event.getPlugin();
/*     */ 
/*  55 */         if (mfc.getDescription().getName().equals("Minefaconomy")) {
/*  56 */           this.economy_minefaconomy.economy = Economy_Minefaconomy.this.economy;
/*  57 */           Economy_Minefaconomy.log.info(String.format("[%s][Economy] %s hooked.", new Object[] { Economy_Minefaconomy.this.plugin.getDescription().getName(), "Minefaconomy" }));
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/*     */     @EventHandler(priority=EventPriority.MONITOR)
/*     */     public void onPluginDisable(PluginDisableEvent event) {
/*  64 */       if ((this.economy_minefaconomy.economy != null) && 
/*  65 */         (event.getPlugin().getDescription().getName().equals("Minefaconomy"))) {
/*  66 */         this.economy_minefaconomy.economy = null;
/*  67 */         Economy_Minefaconomy.log.info(String.format("[%s][Economy] %s unhooked.", new Object[] { Economy_Minefaconomy.this.plugin.getDescription().getName(), "Minefaconomy" }));
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\Vault.jar
 * Qualified Name:     net.milkbowl.vault.economy.plugins.Economy_Minefaconomy
 * JD-Core Version:    0.6.2
 */