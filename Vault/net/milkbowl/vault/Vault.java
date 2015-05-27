/*     */ package net.milkbowl.vault;
/*     */ 
/*     */ import com.nijikokun.register.payment.Methods;
/*     */ import java.io.BufferedReader;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStreamReader;
/*     */ import java.lang.reflect.Constructor;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.lang.reflect.Method;
/*     */ import java.net.URL;
/*     */ import java.net.URLConnection;
/*     */ import java.util.Collection;
/*     */ import java.util.logging.Logger;
/*     */ import net.milkbowl.vault.chat.Chat;
/*     */ import net.milkbowl.vault.chat.plugins.Chat_DroxPerms;
/*     */ import net.milkbowl.vault.chat.plugins.Chat_GroupManager;
/*     */ import net.milkbowl.vault.chat.plugins.Chat_OverPermissions;
/*     */ import net.milkbowl.vault.chat.plugins.Chat_Permissions3;
/*     */ import net.milkbowl.vault.chat.plugins.Chat_PermissionsEx;
/*     */ import net.milkbowl.vault.chat.plugins.Chat_Privileges;
/*     */ import net.milkbowl.vault.chat.plugins.Chat_TotalPermissions;
/*     */ import net.milkbowl.vault.chat.plugins.Chat_bPermissions;
/*     */ import net.milkbowl.vault.chat.plugins.Chat_bPermissions2;
/*     */ import net.milkbowl.vault.chat.plugins.Chat_iChat;
/*     */ import net.milkbowl.vault.chat.plugins.Chat_mChat;
/*     */ import net.milkbowl.vault.chat.plugins.Chat_mChatSuite;
/*     */ import net.milkbowl.vault.chat.plugins.Chat_rscPermissions;
/*     */ import net.milkbowl.vault.economy.Economy;
/*     */ import net.milkbowl.vault.economy.plugins.Economy_BOSE7;
/*     */ import net.milkbowl.vault.economy.plugins.Economy_CommandsEX;
/*     */ import net.milkbowl.vault.economy.plugins.Economy_Craftconomy3;
/*     */ import net.milkbowl.vault.economy.plugins.Economy_CurrencyCore;
/*     */ import net.milkbowl.vault.economy.plugins.Economy_DigiCoin;
/*     */ import net.milkbowl.vault.economy.plugins.Economy_Dosh;
/*     */ import net.milkbowl.vault.economy.plugins.Economy_EconXP;
/*     */ import net.milkbowl.vault.economy.plugins.Economy_Essentials;
/*     */ import net.milkbowl.vault.economy.plugins.Economy_GoldIsMoney2;
/*     */ import net.milkbowl.vault.economy.plugins.Economy_GoldenChestEconomy;
/*     */ import net.milkbowl.vault.economy.plugins.Economy_Gringotts;
/*     */ import net.milkbowl.vault.economy.plugins.Economy_McMoney;
/*     */ import net.milkbowl.vault.economy.plugins.Economy_MiConomy;
/*     */ import net.milkbowl.vault.economy.plugins.Economy_MineConomy;
/*     */ import net.milkbowl.vault.economy.plugins.Economy_Minefaconomy;
/*     */ import net.milkbowl.vault.economy.plugins.Economy_MultiCurrency;
/*     */ import net.milkbowl.vault.economy.plugins.Economy_SDFEconomy;
/*     */ import net.milkbowl.vault.economy.plugins.Economy_TAEcon;
/*     */ import net.milkbowl.vault.economy.plugins.Economy_XPBank;
/*     */ import net.milkbowl.vault.economy.plugins.Economy_eWallet;
/*     */ import net.milkbowl.vault.economy.plugins.Economy_iConomy6;
/*     */ import net.milkbowl.vault.permission.plugins.Permission_DroxPerms;
/*     */ import net.milkbowl.vault.permission.plugins.Permission_GroupManager;
/*     */ import net.milkbowl.vault.permission.plugins.Permission_KPerms;
/*     */ import net.milkbowl.vault.permission.plugins.Permission_OverPermissions;
/*     */ import net.milkbowl.vault.permission.plugins.Permission_Permissions3;
/*     */ import net.milkbowl.vault.permission.plugins.Permission_PermissionsBukkit;
/*     */ import net.milkbowl.vault.permission.plugins.Permission_PermissionsEx;
/*     */ import net.milkbowl.vault.permission.plugins.Permission_Privileges;
/*     */ import net.milkbowl.vault.permission.plugins.Permission_SimplyPerms;
/*     */ import net.milkbowl.vault.permission.plugins.Permission_Starburst;
/*     */ import net.milkbowl.vault.permission.plugins.Permission_SuperPerms;
/*     */ import net.milkbowl.vault.permission.plugins.Permission_TotalPermissions;
/*     */ import net.milkbowl.vault.permission.plugins.Permission_Xperms;
/*     */ import net.milkbowl.vault.permission.plugins.Permission_bPermissions;
/*     */ import net.milkbowl.vault.permission.plugins.Permission_bPermissions2;
/*     */ import net.milkbowl.vault.permission.plugins.Permission_rscPermissions;
/*     */ import org.bukkit.Bukkit;
/*     */ import org.bukkit.OfflinePlayer;
/*     */ import org.bukkit.Server;
/*     */ import org.bukkit.command.Command;
/*     */ import org.bukkit.command.CommandSender;
/*     */ import org.bukkit.command.ConsoleCommandSender;
/*     */ import org.bukkit.command.PluginCommand;
/*     */ import org.bukkit.configuration.file.FileConfiguration;
/*     */ import org.bukkit.configuration.file.FileConfigurationOptions;
/*     */ import org.bukkit.entity.Player;
/*     */ import org.bukkit.event.EventHandler;
/*     */ import org.bukkit.event.EventPriority;
/*     */ import org.bukkit.event.Listener;
/*     */ import org.bukkit.event.player.PlayerJoinEvent;
/*     */ import org.bukkit.event.server.PluginEnableEvent;
/*     */ import org.bukkit.permissions.PermissionDefault;
/*     */ import org.bukkit.plugin.Plugin;
/*     */ import org.bukkit.plugin.PluginDescriptionFile;
/*     */ import org.bukkit.plugin.PluginManager;
/*     */ import org.bukkit.plugin.RegisteredServiceProvider;
/*     */ import org.bukkit.plugin.ServicePriority;
/*     */ import org.bukkit.plugin.ServicesManager;
/*     */ import org.bukkit.plugin.java.JavaPlugin;
/*     */ import org.bukkit.scheduler.BukkitScheduler;
/*     */ import org.json.simple.JSONArray;
/*     */ import org.json.simple.JSONObject;
/*     */ import org.json.simple.JSONValue;
/*     */ 
/*     */ public class Vault extends JavaPlugin
/*     */ {
/*     */   private static Logger log;
/*     */   private net.milkbowl.vault.permission.Permission perms;
/*     */   private String newVersionTitle;
/*     */   private double newVersion;
/*     */   private double currentVersion;
/*     */   private String currentVersionTitle;
/*     */   private ServicesManager sm;
/*     */   private Metrics metrics;
/*     */   private Vault plugin;
/*     */ 
/*     */   public Vault()
/*     */   {
/* 109 */     this.newVersionTitle = "";
/* 110 */     this.newVersion = 0.0D;
/* 111 */     this.currentVersion = 0.0D;
/* 112 */     this.currentVersionTitle = "";
/*     */   }
/*     */ 
/*     */   public void onDisable()
/*     */   {
/* 120 */     getServer().getServicesManager().unregisterAll(this);
/* 121 */     Bukkit.getScheduler().cancelTasks(this);
/*     */   }
/*     */ 
/*     */   public void onEnable()
/*     */   {
/* 126 */     this.plugin = this;
/* 127 */     log = getLogger();
/* 128 */     this.currentVersionTitle = getDescription().getVersion().split("-")[0];
/* 129 */     this.currentVersion = Double.valueOf(this.currentVersionTitle.replaceFirst("\\.", "")).doubleValue();
/* 130 */     this.sm = getServer().getServicesManager();
/*     */ 
/* 132 */     getConfig().addDefault("update-check", Boolean.valueOf(true));
/* 133 */     getConfig().options().copyDefaults(true);
/* 134 */     saveConfig();
/*     */ 
/* 136 */     loadEconomy();
/* 137 */     loadPermission();
/* 138 */     loadChat();
/*     */ 
/* 140 */     getCommand("vault-info").setExecutor(this);
/* 141 */     getCommand("vault-convert").setExecutor(this);
/* 142 */     getServer().getPluginManager().registerEvents(new VaultListener(), this);
/*     */ 
/* 145 */     getServer().getScheduler().runTask(this, new Runnable()
/*     */     {
/*     */       public void run()
/*     */       {
/* 150 */         org.bukkit.permissions.Permission perm = Vault.this.getServer().getPluginManager().getPermission("vault.update");
/* 151 */         if (perm == null)
/*     */         {
/* 153 */           perm = new org.bukkit.permissions.Permission("vault.update");
/* 154 */           perm.setDefault(PermissionDefault.OP);
/* 155 */           Vault.this.plugin.getServer().getPluginManager().addPermission(perm);
/*     */         }
/* 157 */         perm.setDescription("Allows a user or the console to check for vault updates");
/*     */ 
/* 159 */         Vault.this.getServer().getScheduler().runTaskTimerAsynchronously(Vault.this.plugin, new Runnable()
/*     */         {
/*     */           public void run()
/*     */           {
/* 163 */             if ((Vault.this.getServer().getConsoleSender().hasPermission("vault.update")) && (Vault.this.getConfig().getBoolean("update-check", true)))
/*     */               try {
/* 165 */                 Vault.log.info("Checking for Updates ... ");
/* 166 */                 Vault.this.newVersion = Vault.this.updateCheck(Vault.this.currentVersion);
/* 167 */                 if (Vault.this.newVersion > Vault.this.currentVersion) {
/* 168 */                   Vault.log.warning("Stable Version: " + Vault.this.newVersionTitle + " is out!" + " You are still running version: " + Vault.this.currentVersionTitle);
/* 169 */                   Vault.log.warning("Update at: http://dev.bukkit.org/server-mods/vault");
/* 170 */                 } else if (Vault.this.currentVersion > Vault.this.newVersion) {
/* 171 */                   Vault.log.info("Stable Version: " + Vault.this.newVersionTitle + " | Current Version: " + Vault.this.currentVersionTitle);
/*     */                 } else {
/* 173 */                   Vault.log.info("No new version available");
/*     */                 }
/*     */               }
/*     */               catch (Exception e)
/*     */               {
/*     */               }
/*     */           }
/*     */         }
/*     */         , 0L, 432000L);
/*     */       }
/*     */ 
/*     */     });
/*     */     try
/*     */     {
/* 188 */       this.metrics = new Metrics(this);
/* 189 */       this.metrics.findCustomData();
/* 190 */       this.metrics.start();
/*     */     }
/*     */     catch (IOException e) {
/*     */     }
/* 194 */     log.info(String.format("Enabled Version %s", new Object[] { getDescription().getVersion() }));
/*     */   }
/*     */ 
/*     */   private void loadChat()
/*     */   {
/* 202 */     hookChat("PermissionsEx", Chat_PermissionsEx.class, ServicePriority.Highest, new String[] { "ru.tehkode.permissions.bukkit.PermissionsEx" });
/*     */ 
/* 205 */     hookChat("mChatSuite", Chat_mChatSuite.class, ServicePriority.Highest, new String[] { "in.mDev.MiracleM4n.mChatSuite.mChatSuite" });
/*     */ 
/* 208 */     hookChat("mChat", Chat_mChat.class, ServicePriority.Highest, new String[] { "net.D3GN.MiracleM4n.mChat" });
/*     */ 
/* 211 */     hookChat("OverPermissions", Chat_OverPermissions.class, ServicePriority.Highest, new String[] { "com.overmc.overpermissions.OverPermissions" });
/*     */ 
/* 214 */     hookChat("DroxPerms", Chat_DroxPerms.class, ServicePriority.Lowest, new String[] { "de.hydrox.bukkit.DroxPerms.DroxPerms" });
/*     */ 
/* 217 */     hookChat("bPermssions2", Chat_bPermissions2.class, ServicePriority.Highest, new String[] { "de.bananaco.bpermissions.api.ApiLayer" });
/*     */ 
/* 220 */     hookChat("bPermissions", Chat_bPermissions.class, ServicePriority.Normal, new String[] { "de.bananaco.permissions.info.InfoReader" });
/*     */ 
/* 223 */     hookChat("GroupManager", Chat_GroupManager.class, ServicePriority.Normal, new String[] { "org.anjocaido.groupmanager.GroupManager" });
/*     */ 
/* 226 */     hookChat("Permissions3", Chat_Permissions3.class, ServicePriority.Normal, new String[] { "com.nijiko.permissions.ModularControl" });
/*     */ 
/* 229 */     hookChat("iChat", Chat_iChat.class, ServicePriority.Low, new String[] { "net.TheDgtl.iChat.iChat" });
/*     */ 
/* 232 */     hookChat("Privileges", Chat_Privileges.class, ServicePriority.Normal, new String[] { "net.krinsoft.privileges.Privileges" });
/*     */ 
/* 235 */     hookChat("rscPermissions", Chat_rscPermissions.class, ServicePriority.Normal, new String[] { "ru.simsonic.rscPermissions.MainPluginClass" });
/*     */ 
/* 238 */     hookChat("TotalPermissions", Chat_TotalPermissions.class, ServicePriority.Normal, new String[] { "net.ar97.totalpermissions.TotalPermissions" });
/*     */   }
/*     */ 
/*     */   private void loadEconomy()
/*     */   {
/* 246 */     hookEconomy("MiConomy", Economy_MiConomy.class, ServicePriority.Normal, new String[] { "com.gmail.bleedobsidian.miconomy.Main" });
/*     */ 
/* 249 */     hookEconomy("MineFaConomy", Economy_Minefaconomy.class, ServicePriority.Normal, new String[] { "me.coniin.plugins.minefaconomy.Minefaconomy" });
/*     */ 
/* 252 */     hookEconomy("MultiCurrency", Economy_MultiCurrency.class, ServicePriority.Normal, new String[] { "me.ashtheking.currency.Currency", "me.ashtheking.currency.CurrencyList" });
/*     */ 
/* 255 */     hookEconomy("MineConomy", Economy_MineConomy.class, ServicePriority.Normal, new String[] { "me.mjolnir.mineconomy.MineConomy" });
/*     */ 
/* 258 */     hookEconomy("McMoney", Economy_McMoney.class, ServicePriority.Normal, new String[] { "boardinggamer.mcmoney.McMoneyAPI" });
/*     */ 
/* 261 */     hookEconomy("CraftConomy3", Economy_Craftconomy3.class, ServicePriority.Normal, new String[] { "com.greatmancode.craftconomy3.tools.interfaces.BukkitLoader" });
/*     */ 
/* 264 */     hookEconomy("eWallet", Economy_eWallet.class, ServicePriority.Normal, new String[] { "me.ethan.eWallet.ECO" });
/*     */ 
/* 267 */     hookEconomy("BOSEconomy7", Economy_BOSE7.class, ServicePriority.Normal, new String[] { "cosine.boseconomy.BOSEconomy", "cosine.boseconomy.CommandHandler" });
/*     */ 
/* 270 */     hookEconomy("CurrencyCore", Economy_CurrencyCore.class, ServicePriority.Normal, new String[] { "is.currency.Currency" });
/*     */ 
/* 273 */     hookEconomy("Gringotts", Economy_Gringotts.class, ServicePriority.Normal, new String[] { "org.gestern.gringotts.Gringotts" });
/*     */ 
/* 276 */     hookEconomy("Essentials Economy", Economy_Essentials.class, ServicePriority.Low, new String[] { "com.earth2me.essentials.api.Economy", "com.earth2me.essentials.api.NoLoanPermittedException", "com.earth2me.essentials.api.UserDoesNotExistException" });
/*     */ 
/* 279 */     hookEconomy("iConomy 6", Economy_iConomy6.class, ServicePriority.High, new String[] { "com.iCo6.iConomy" });
/*     */ 
/* 282 */     hookEconomy("EconXP", Economy_EconXP.class, ServicePriority.Normal, new String[] { "ca.agnate.EconXP.EconXP" });
/*     */ 
/* 285 */     hookEconomy("GoldIsMoney2", Economy_GoldIsMoney2.class, ServicePriority.Normal, new String[] { "com.flobi.GoldIsMoney2.GoldIsMoney" });
/*     */ 
/* 288 */     hookEconomy("GoldenChestEconomy", Economy_GoldenChestEconomy.class, ServicePriority.Normal, new String[] { "me.igwb.GoldenChest.GoldenChestEconomy" });
/*     */ 
/* 291 */     hookEconomy("Dosh", Economy_Dosh.class, ServicePriority.Normal, new String[] { "com.gravypod.Dosh.Dosh" });
/*     */ 
/* 294 */     hookEconomy("CommandsEX", Economy_CommandsEX.class, ServicePriority.Normal, new String[] { "com.github.zathrus_writer.commandsex.api.EconomyAPI" });
/*     */ 
/* 297 */     hookEconomy("SDFEconomy", Economy_SDFEconomy.class, ServicePriority.Normal, new String[] { "com.github.omwah.SDFEconomy.SDFEconomy" });
/*     */ 
/* 300 */     hookEconomy("XPBank", Economy_XPBank.class, ServicePriority.Normal, new String[] { "com.gmail.mirelatrue.xpbank.XPBank" });
/*     */ 
/* 303 */     hookEconomy("TAEcon", Economy_TAEcon.class, ServicePriority.Normal, new String[] { "net.teamalpha.taecon.TAEcon" });
/*     */ 
/* 306 */     hookEconomy("DigiCoin", Economy_DigiCoin.class, ServicePriority.Normal, new String[] { "co.uk.silvania.cities.digicoin.DigiCoin" });
/*     */   }
/*     */ 
/*     */   private void loadPermission()
/*     */   {
/* 314 */     hookPermission("Starburst", Permission_Starburst.class, ServicePriority.Highest, new String[] { "com.dthielke.starburst.StarburstPlugin" });
/*     */ 
/* 317 */     hookPermission("PermissionsEx", Permission_PermissionsEx.class, ServicePriority.Highest, new String[] { "ru.tehkode.permissions.bukkit.PermissionsEx" });
/*     */ 
/* 320 */     hookPermission("OverPermissions", Permission_OverPermissions.class, ServicePriority.Highest, new String[] { "com.overmc.overpermissions.OverPermissions" });
/*     */ 
/* 323 */     hookPermission("PermissionsBukkit", Permission_PermissionsBukkit.class, ServicePriority.Normal, new String[] { "com.platymuus.bukkit.permissions.PermissionsPlugin" });
/*     */ 
/* 326 */     hookPermission("DroxPerms", Permission_DroxPerms.class, ServicePriority.High, new String[] { "de.hydrox.bukkit.DroxPerms.DroxPerms" });
/*     */ 
/* 329 */     hookPermission("SimplyPerms", Permission_SimplyPerms.class, ServicePriority.Highest, new String[] { "net.crystalyx.bukkit.simplyperms.SimplyPlugin" });
/*     */ 
/* 332 */     hookPermission("bPermissions 2", Permission_bPermissions2.class, ServicePriority.Highest, new String[] { "de.bananaco.bpermissions.api.WorldManager" });
/*     */ 
/* 335 */     hookPermission("Privileges", Permission_Privileges.class, ServicePriority.Highest, new String[] { "net.krinsoft.privileges.Privileges" });
/*     */ 
/* 338 */     hookPermission("bPermissions", Permission_bPermissions.class, ServicePriority.High, new String[] { "de.bananaco.permissions.SuperPermissionHandler" });
/*     */ 
/* 341 */     hookPermission("GroupManager", Permission_GroupManager.class, ServicePriority.High, new String[] { "org.anjocaido.groupmanager.GroupManager" });
/*     */ 
/* 344 */     hookPermission("Permissions 3 (Yeti)", Permission_Permissions3.class, ServicePriority.Normal, new String[] { "com.nijiko.permissions.ModularControl" });
/*     */ 
/* 347 */     hookPermission("Xperms", Permission_Xperms.class, ServicePriority.Low, new String[] { "com.github.sebc722.Xperms" });
/*     */ 
/* 350 */     hookPermission("TotalPermissions", Permission_TotalPermissions.class, ServicePriority.Normal, new String[] { "net.ae97.totalpermissions.TotalPermissions" });
/*     */ 
/* 353 */     hookPermission("rscPermissions", Permission_rscPermissions.class, ServicePriority.Normal, new String[] { "ru.simsonic.rscPermissions.MainPluginClass" });
/*     */ 
/* 356 */     hookPermission("KPerms", Permission_KPerms.class, ServicePriority.Normal, new String[] { "com.lightniinja.kperms.KPermsPlugin" });
/*     */ 
/* 358 */     net.milkbowl.vault.permission.Permission perms = new Permission_SuperPerms(this);
/* 359 */     this.sm.register(net.milkbowl.vault.permission.Permission.class, perms, this, ServicePriority.Lowest);
/* 360 */     log.info(String.format("[Permission] SuperPermissions loaded as backup permission system.", new Object[0]));
/*     */ 
/* 362 */     this.perms = ((net.milkbowl.vault.permission.Permission)this.sm.getRegistration(net.milkbowl.vault.permission.Permission.class).getProvider());
/*     */   }
/*     */ 
/*     */   private void hookChat(String name, Class<? extends Chat> hookClass, ServicePriority priority, String[] packages) {
/*     */     try {
/* 367 */       if (packagesExists(packages)) {
/* 368 */         Chat chat = (Chat)hookClass.getConstructor(new Class[] { Plugin.class, net.milkbowl.vault.permission.Permission.class }).newInstance(new Object[] { this, this.perms });
/* 369 */         this.sm.register(Chat.class, chat, this, priority);
/* 370 */         log.info(String.format("[Chat] %s found: %s", new Object[] { name, chat.isEnabled() ? "Loaded" : "Waiting" }));
/*     */       }
/*     */     } catch (Exception e) {
/* 373 */       log.severe(String.format("[Chat] There was an error hooking %s - check to make sure you're using a compatible version!", new Object[] { name }));
/*     */     }
/*     */   }
/*     */ 
/*     */   private void hookEconomy(String name, Class<? extends Economy> hookClass, ServicePriority priority, String[] packages) {
/*     */     try {
/* 379 */       if (packagesExists(packages)) {
/* 380 */         Economy econ = (Economy)hookClass.getConstructor(new Class[] { Plugin.class }).newInstance(new Object[] { this });
/* 381 */         this.sm.register(Economy.class, econ, this, priority);
/* 382 */         log.info(String.format("[Economy] %s found: %s", new Object[] { name, econ.isEnabled() ? "Loaded" : "Waiting" }));
/*     */       }
/*     */     } catch (Exception e) {
/* 385 */       log.severe(String.format("[Economy] There was an error hooking %s - check to make sure you're using a compatible version!", new Object[] { name }));
/*     */     }
/*     */   }
/*     */ 
/*     */   private void hookPermission(String name, Class<? extends net.milkbowl.vault.permission.Permission> hookClass, ServicePriority priority, String[] packages) {
/*     */     try {
/* 391 */       if (packagesExists(packages)) {
/* 392 */         net.milkbowl.vault.permission.Permission perms = (net.milkbowl.vault.permission.Permission)hookClass.getConstructor(new Class[] { Plugin.class }).newInstance(new Object[] { this });
/* 393 */         this.sm.register(net.milkbowl.vault.permission.Permission.class, perms, this, priority);
/* 394 */         log.info(String.format("[Permission] %s found: %s", new Object[] { name, perms.isEnabled() ? "Loaded" : "Waiting" }));
/*     */       }
/*     */     } catch (Exception e) {
/* 397 */       log.severe(String.format("[Permission] There was an error hooking %s - check to make sure you're using a compatible version!", new Object[] { name }));
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args)
/*     */   {
/* 403 */     if (!sender.hasPermission("vault.admin")) {
/* 404 */       sender.sendMessage("You do not have permission to use that command!");
/* 405 */       return true;
/*     */     }
/*     */ 
/* 408 */     if (command.getName().equalsIgnoreCase("vault-info")) {
/* 409 */       infoCommand(sender);
/* 410 */       return true;
/* 411 */     }if (command.getName().equalsIgnoreCase("vault-convert")) {
/* 412 */       convertCommand(sender, args);
/* 413 */       return true;
/*     */     }
/*     */ 
/* 416 */     sender.sendMessage("Vault Commands:");
/* 417 */     sender.sendMessage("  /vault-info - Displays information about Vault");
/* 418 */     sender.sendMessage("  /vault-convert [economy1] [economy2] - Converts from one Economy to another");
/* 419 */     return true;
/*     */   }
/*     */ 
/*     */   private void convertCommand(CommandSender sender, String[] args)
/*     */   {
/* 424 */     Collection econs = getServer().getServicesManager().getRegistrations(Economy.class);
/* 425 */     if ((econs == null) || (econs.size() < 2)) {
/* 426 */       sender.sendMessage("You must have at least 2 economies loaded to convert.");
/* 427 */       return;
/* 428 */     }if (args.length != 2) {
/* 429 */       sender.sendMessage("You must specify only the economy to convert from and the economy to convert to. (names should not contain spaces)");
/* 430 */       return;
/*     */     }
/* 432 */     Economy econ1 = null;
/* 433 */     Economy econ2 = null;
/* 434 */     String economies = "";
/* 435 */     for (RegisteredServiceProvider econ : econs) {
/* 436 */       String econName = ((Economy)econ.getProvider()).getName().replace(" ", "");
/* 437 */       if (econName.equalsIgnoreCase(args[0]))
/* 438 */         econ1 = (Economy)econ.getProvider();
/* 439 */       else if (econName.equalsIgnoreCase(args[1])) {
/* 440 */         econ2 = (Economy)econ.getProvider();
/*     */       }
/* 442 */       if (economies.length() > 0) {
/* 443 */         economies = economies + ", ";
/*     */       }
/* 445 */       economies = economies + econName;
/*     */     }
/*     */ 
/* 448 */     if (econ1 == null) {
/* 449 */       sender.sendMessage("Could not find " + args[0] + " loaded on the server, check your spelling.");
/* 450 */       sender.sendMessage("Valid economies are: " + economies);
/* 451 */       return;
/* 452 */     }if (econ2 == null) {
/* 453 */       sender.sendMessage("Could not find " + args[1] + " loaded on the server, check your spelling.");
/* 454 */       sender.sendMessage("Valid economies are: " + economies);
/* 455 */       return;
/*     */     }
/*     */ 
/* 458 */     sender.sendMessage("This may take some time to convert, expect server lag.");
/* 459 */     for (OfflinePlayer op : Bukkit.getServer().getOfflinePlayers()) {
/* 460 */       if ((econ1.hasAccount(op)) && 
/* 461 */         (!econ2.hasAccount(op)))
/*     */       {
/* 464 */         econ2.createPlayerAccount(op);
/* 465 */         double diff = econ1.getBalance(op) - econ2.getBalance(op);
/* 466 */         if (diff > 0.0D)
/* 467 */           econ2.depositPlayer(op, diff);
/* 468 */         else if (diff < 0.0D) {
/* 469 */           econ2.withdrawPlayer(op, -diff);
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 474 */     sender.sendMessage("Converson complete, please verify the data before using it.");
/*     */   }
/*     */ 
/*     */   private void infoCommand(CommandSender sender)
/*     */   {
/* 479 */     String registeredEcons = null;
/* 480 */     Collection econs = getServer().getServicesManager().getRegistrations(Economy.class);
/* 481 */     for (RegisteredServiceProvider econ : econs) {
/* 482 */       Economy e = (Economy)econ.getProvider();
/* 483 */       if (registeredEcons == null)
/* 484 */         registeredEcons = e.getName();
/*     */       else {
/* 486 */         registeredEcons = registeredEcons + ", " + e.getName();
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 491 */     String registeredPerms = null;
/* 492 */     Collection perms = getServer().getServicesManager().getRegistrations(net.milkbowl.vault.permission.Permission.class);
/* 493 */     for (RegisteredServiceProvider perm : perms) {
/* 494 */       net.milkbowl.vault.permission.Permission p = (net.milkbowl.vault.permission.Permission)perm.getProvider();
/* 495 */       if (registeredPerms == null)
/* 496 */         registeredPerms = p.getName();
/*     */       else {
/* 498 */         registeredPerms = registeredPerms + ", " + p.getName();
/*     */       }
/*     */     }
/*     */ 
/* 502 */     String registeredChats = null;
/* 503 */     Collection chats = getServer().getServicesManager().getRegistrations(Chat.class);
/* 504 */     for (RegisteredServiceProvider chat : chats) {
/* 505 */       Chat c = (Chat)chat.getProvider();
/* 506 */       if (registeredChats == null)
/* 507 */         registeredChats = c.getName();
/*     */       else {
/* 509 */         registeredChats = registeredChats + ", " + c.getName();
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 514 */     RegisteredServiceProvider rsp = getServer().getServicesManager().getRegistration(Economy.class);
/* 515 */     Economy econ = null;
/* 516 */     if (rsp != null) {
/* 517 */       econ = (Economy)rsp.getProvider();
/*     */     }
/* 519 */     net.milkbowl.vault.permission.Permission perm = null;
/* 520 */     RegisteredServiceProvider rspp = getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
/* 521 */     if (rspp != null) {
/* 522 */       perm = (net.milkbowl.vault.permission.Permission)rspp.getProvider();
/*     */     }
/* 524 */     Chat chat = null;
/* 525 */     RegisteredServiceProvider rspc = getServer().getServicesManager().getRegistration(Chat.class);
/* 526 */     if (rspc != null) {
/* 527 */       chat = (Chat)rspc.getProvider();
/*     */     }
/*     */ 
/* 530 */     sender.sendMessage(String.format("[%s] Vault v%s Information", new Object[] { getDescription().getName(), getDescription().getVersion() }));
/* 531 */     sender.sendMessage(String.format("[%s] Economy: %s [%s]", new Object[] { getDescription().getName(), econ == null ? "None" : econ.getName(), registeredEcons }));
/* 532 */     sender.sendMessage(String.format("[%s] Permission: %s [%s]", new Object[] { getDescription().getName(), perm == null ? "None" : perm.getName(), registeredPerms }));
/* 533 */     sender.sendMessage(String.format("[%s] Chat: %s [%s]", new Object[] { getDescription().getName(), chat == null ? "None" : chat.getName(), registeredChats }));
/*     */   }
/*     */ 
/*     */   private static boolean packagesExists(String[] packages)
/*     */   {
/*     */     try
/*     */     {
/* 546 */       for (String pkg : packages) {
/* 547 */         Class.forName(pkg);
/*     */       }
/* 549 */       return true; } catch (Exception e) {
/*     */     }
/* 551 */     return false;
/*     */   }
/*     */ 
/*     */   public double updateCheck(double currentVersion)
/*     */   {
/*     */     try {
/* 557 */       URL url = new URL("https://api.curseforge.com/servermods/files?projectids=33184");
/* 558 */       URLConnection conn = url.openConnection();
/* 559 */       conn.setReadTimeout(5000);
/* 560 */       conn.addRequestProperty("User-Agent", "Vault Update Checker");
/* 561 */       conn.setDoOutput(true);
/* 562 */       BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
/* 563 */       String response = reader.readLine();
/* 564 */       JSONArray array = (JSONArray)JSONValue.parse(response);
/*     */ 
/* 566 */       if (array.size() == 0) {
/* 567 */         getLogger().warning("No files found, or Feed URL is bad.");
/* 568 */         return currentVersion;
/*     */       }
/*     */ 
/* 571 */       this.newVersionTitle = ((String)((JSONObject)array.get(array.size() - 1)).get("name")).replace("Vault", "").trim();
/* 572 */       return Double.valueOf(this.newVersionTitle.replaceFirst("\\.", "").trim()).doubleValue();
/*     */     } catch (Exception e) {
/* 574 */       log.info("There was an issue attempting to check for the latest version.");
/*     */     }
/* 576 */     return currentVersion;
/*     */   }
/*     */   public class VaultListener implements Listener {
/*     */     public VaultListener() {
/*     */     }
/*     */     @EventHandler(priority=EventPriority.MONITOR)
/*     */     public void onPlayerJoin(PlayerJoinEvent event) {
/* 583 */       Player player = event.getPlayer();
/* 584 */       if (Vault.this.perms.has(player, "vault.update"))
/*     */         try {
/* 586 */           if (Vault.this.newVersion > Vault.this.currentVersion) {
/* 587 */             player.sendMessage("Vault " + Vault.this.newVersion + " is out! You are running " + Vault.this.currentVersion);
/* 588 */             player.sendMessage("Update Vault at: http://dev.bukkit.org/server-mods/vault");
/*     */           }
/*     */         }
/*     */         catch (Exception e)
/*     */         {
/*     */         }
/*     */     }
/*     */ 
/*     */     @EventHandler(priority=EventPriority.MONITOR)
/*     */     public void onPluginEnable(PluginEnableEvent event) {
/* 598 */       if (event.getPlugin().getDescription().getName().equals("Register")) if ((Vault.packagesExists(new String[] { "com.nijikokun.register.payment.Methods" })) && 
/* 599 */           (!Methods.hasMethod()))
/*     */           try {
/* 601 */             Method m = Methods.class.getMethod("addMethod", new Class[] { Methods.class });
/* 602 */             m.setAccessible(true);
/* 603 */             m.invoke(null, new Object[] { "Vault", new VaultEco() });
/* 604 */             if (!Methods.setPreferred("Vault"))
/* 605 */               Vault.log.info("Unable to hook register");
/*     */             else
/* 607 */               Vault.log.info("[Vault] - Successfully injected Vault methods into Register.");
/*     */           }
/*     */           catch (SecurityException e) {
/* 610 */             Vault.log.info("Unable to hook register");
/*     */           } catch (NoSuchMethodException e) {
/* 612 */             Vault.log.info("Unable to hook register");
/*     */           } catch (IllegalArgumentException e) {
/* 614 */             Vault.log.info("Unable to hook register");
/*     */           } catch (IllegalAccessException e) {
/* 616 */             Vault.log.info("Unable to hook register");
/*     */           } catch (InvocationTargetException e) {
/* 618 */             Vault.log.info("Unable to hook register");
/*     */           }
/*     */     }
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\Vault.jar
 * Qualified Name:     net.milkbowl.vault.Vault
 * JD-Core Version:    0.6.2
 */