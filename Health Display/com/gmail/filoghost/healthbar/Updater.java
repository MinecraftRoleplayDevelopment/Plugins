/*     */ package com.gmail.filoghost.healthbar;
/*     */ 
/*     */ import java.io.BufferedInputStream;
/*     */ import java.io.BufferedOutputStream;
/*     */ import java.io.BufferedReader;
/*     */ import java.io.File;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStreamReader;
/*     */ import java.net.MalformedURLException;
/*     */ import java.net.URL;
/*     */ import java.net.URLConnection;
/*     */ import java.util.Enumeration;
/*     */ import java.util.List;
/*     */ import java.util.logging.Logger;
/*     */ import java.util.zip.ZipEntry;
/*     */ import java.util.zip.ZipFile;
/*     */ import org.bukkit.Bukkit;
/*     */ import org.bukkit.ChatColor;
/*     */ import org.bukkit.Server;
/*     */ import org.bukkit.command.CommandSender;
/*     */ import org.bukkit.command.ConsoleCommandSender;
/*     */ import org.bukkit.configuration.file.YamlConfiguration;
/*     */ import org.bukkit.configuration.file.YamlConfigurationOptions;
/*     */ import org.bukkit.entity.Player;
/*     */ import org.bukkit.plugin.Plugin;
/*     */ import org.bukkit.plugin.PluginDescriptionFile;
/*     */ import org.bukkit.scheduler.BukkitScheduler;
/*     */ import org.json.simple.JSONArray;
/*     */ import org.json.simple.JSONObject;
/*     */ import org.json.simple.JSONValue;
/*     */ 
/*     */ public class Updater
/*     */ {
/*     */   private Plugin plugin;
/*     */   private UpdateType type;
/*     */   private String versionName;
/*     */   private String versionLink;
/*     */   private String versionType;
/*     */   private String versionGameVersion;
/*     */   private boolean announce;
/*     */   private URL url;
/*     */   private File file;
/*     */   private Thread thread;
/* 172 */   private int id = -1;
/* 173 */   private String apiKey = null;
/*     */   private static final String TITLE_VALUE = "name";
/*     */   private static final String LINK_VALUE = "downloadUrl";
/*     */   private static final String TYPE_VALUE = "releaseType";
/*     */   private static final String VERSION_VALUE = "gameVersion";
/*     */   private static final String QUERY = "/servermods/files?projectIds=";
/*     */   private static final String HOST = "https://api.curseforge.com";
/* 181 */   private static final String[] NO_UPDATE_TAG = { "-DEV", "-PRE", "-SNAPSHOT" };
/*     */   private static final int BYTE_SIZE = 1024;
/*     */   private YamlConfiguration config;
/*     */   private String updateFolder;
/* 185 */   private UpdateResult result = UpdateResult.SUCCESS;
/*     */ 
/*     */   public Updater(Plugin plugin, int id, File file, UpdateType type, boolean announce)
/*     */   {
/* 257 */     this.plugin = plugin;
/* 258 */     this.type = type;
/* 259 */     this.announce = announce;
/* 260 */     this.file = file;
/* 261 */     this.id = id;
/* 262 */     this.updateFolder = plugin.getServer().getUpdateFolder();
/*     */ 
/* 264 */     File pluginFile = plugin.getDataFolder().getParentFile();
/* 265 */     File updaterFile = new File(pluginFile, "Updater");
/* 266 */     File updaterConfigFile = new File(updaterFile, "config.yml");
/*     */ 
/* 268 */     if (!updaterFile.exists()) {
/* 269 */       updaterFile.mkdir();
/*     */     }
/* 271 */     if (!updaterConfigFile.exists()) {
/*     */       try {
/* 273 */         updaterConfigFile.createNewFile();
/*     */       } catch (IOException e) {
/* 275 */         plugin.getLogger().severe("The updater could not create a configuration in " + updaterFile.getAbsolutePath());
/* 276 */         e.printStackTrace();
/*     */       }
/*     */     }
/* 279 */     this.config = YamlConfiguration.loadConfiguration(updaterConfigFile);
/*     */ 
/* 281 */     this.config.options().header("This configuration file affects all plugins using the Updater system (version 2+ - http://forums.bukkit.org/threads/96681/ )\nIf you wish to use your API key, read http://wiki.bukkit.org/ServerMods_API and place it below.\nSome updating systems will not adhere to the disabled value, but these may be turned off in their plugin's configuration.");
/*     */ 
/* 284 */     this.config.addDefault("api-key", "PUT_API_KEY_HERE");
/* 285 */     this.config.addDefault("disable", Boolean.valueOf(false));
/*     */ 
/* 287 */     if (this.config.get("api-key", null) == null) {
/* 288 */       this.config.options().copyDefaults(true);
/*     */       try {
/* 290 */         this.config.save(updaterConfigFile);
/*     */       } catch (IOException e) {
/* 292 */         plugin.getLogger().severe("The updater could not save the configuration in " + updaterFile.getAbsolutePath());
/* 293 */         e.printStackTrace();
/*     */       }
/*     */     }
/*     */ 
/* 297 */     if (this.config.getBoolean("disable")) {
/* 298 */       this.result = UpdateResult.DISABLED;
/* 299 */       return;
/*     */     }
/*     */ 
/* 302 */     String key = this.config.getString("api-key");
/* 303 */     if ((key.equalsIgnoreCase("PUT_API_KEY_HERE")) || (key.equals(""))) {
/* 304 */       key = null;
/*     */     }
/*     */ 
/* 307 */     this.apiKey = key;
/*     */     try
/*     */     {
/* 310 */       this.url = new URL("https://api.curseforge.com/servermods/files?projectIds=" + id);
/*     */     } catch (MalformedURLException e) {
/* 312 */       plugin.getLogger().severe("The project ID provided for updating, " + id + " is invalid.");
/* 313 */       this.result = UpdateResult.FAIL_BADID;
/* 314 */       e.printStackTrace();
/*     */     }
/*     */ 
/* 317 */     this.thread = new Thread(new UpdateRunnable(null));
/* 318 */     this.thread.start();
/*     */   }
/*     */ 
/*     */   public UpdateResult getResult()
/*     */   {
/* 325 */     waitForThread();
/* 326 */     return this.result;
/*     */   }
/*     */ 
/*     */   public String getLatestType()
/*     */   {
/* 333 */     waitForThread();
/* 334 */     return this.versionType;
/*     */   }
/*     */ 
/*     */   public String getLatestGameVersion()
/*     */   {
/* 341 */     waitForThread();
/* 342 */     return this.versionGameVersion;
/*     */   }
/*     */ 
/*     */   public String getLatestName()
/*     */   {
/* 349 */     waitForThread();
/* 350 */     return this.versionName;
/*     */   }
/*     */ 
/*     */   public String getLatestFileLink()
/*     */   {
/* 357 */     waitForThread();
/* 358 */     return this.versionLink;
/*     */   }
/*     */ 
/*     */   private void waitForThread()
/*     */   {
/* 366 */     if ((this.thread != null) && (this.thread.isAlive()))
/*     */       try {
/* 368 */         this.thread.join();
/*     */       } catch (InterruptedException e) {
/* 370 */         e.printStackTrace();
/*     */       }
/*     */   }
/*     */ 
/*     */   private void saveFile(File folder, String file, String u)
/*     */   {
/* 379 */     if (!folder.exists()) {
/* 380 */       folder.mkdir();
/*     */     }
/* 382 */     BufferedInputStream in = null;
/* 383 */     FileOutputStream fout = null;
/*     */     try
/*     */     {
/* 386 */       URL url = new URL(u);
/* 387 */       int fileLength = url.openConnection().getContentLength();
/* 388 */       in = new BufferedInputStream(url.openStream());
/* 389 */       fout = new FileOutputStream(folder.getAbsolutePath() + "/" + file);
/*     */ 
/* 391 */       byte[] data = new byte[1024];
/*     */ 
/* 393 */       if (this.announce) {
/* 394 */         this.plugin.getLogger().info("About to download a new update: " + this.versionName);
/*     */       }
/* 396 */       long downloaded = 0L;
/*     */       int count;
/* 397 */       while ((count = in.read(data, 0, 1024)) != -1)
/*     */       {
/*     */         int count;
/* 398 */         downloaded += count;
/* 399 */         fout.write(data, 0, count);
/* 400 */         int percent = (int)(downloaded * 100L / fileLength);
/* 401 */         if ((this.announce) && (percent % 10 == 0)) {
/* 402 */           this.plugin.getLogger().info("Downloading update: " + percent + "% of " + fileLength + " bytes.");
/*     */         }
/*     */       }
/*     */ 
/* 406 */       for (File xFile : new File(this.plugin.getDataFolder().getParent(), this.updateFolder).listFiles()) {
/* 407 */         if (xFile.getName().endsWith(".zip")) {
/* 408 */           xFile.delete();
/*     */         }
/*     */       }
/*     */ 
/* 412 */       File dFile = new File(folder.getAbsolutePath() + "/" + file);
/* 413 */       if (dFile.getName().endsWith(".zip"))
/*     */       {
/* 415 */         unzip(dFile.getCanonicalPath());
/*     */       }
/* 417 */       if (this.announce)
/* 418 */         this.plugin.getLogger().info("Finished updating.");
/*     */     }
/*     */     catch (Exception ex) {
/* 421 */       this.plugin.getLogger().warning("The auto-updater tried to download a new update, but was unsuccessful.");
/* 422 */       this.result = UpdateResult.FAIL_DOWNLOAD;
/*     */       try
/*     */       {
/* 425 */         if (in != null) {
/* 426 */           in.close();
/*     */         }
/* 428 */         if (fout != null)
/* 429 */           fout.close();
/*     */       }
/*     */       catch (Exception localException1)
/*     */       {
/*     */       }
/*     */     }
/*     */     finally
/*     */     {
/*     */       try
/*     */       {
/* 425 */         if (in != null) {
/* 426 */           in.close();
/*     */         }
/* 428 */         if (fout != null)
/* 429 */           fout.close();
/*     */       }
/*     */       catch (Exception localException2)
/*     */       {
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private void unzip(String file)
/*     */   {
/*     */     try
/*     */     {
/* 441 */       File fSourceZip = new File(file);
/* 442 */       String zipPath = file.substring(0, file.length() - 4);
/* 443 */       ZipFile zipFile = new ZipFile(fSourceZip);
/* 444 */       Enumeration e = zipFile.entries();
/*     */       int b;
/*     */       String name;
/* 445 */       while (e.hasMoreElements()) {
/* 446 */         ZipEntry entry = (ZipEntry)e.nextElement();
/* 447 */         destinationFilePath = new File(zipPath, entry.getName());
/* 448 */         destinationFilePath.getParentFile().mkdirs();
/* 449 */         if (!entry.isDirectory())
/*     */         {
/* 452 */           bis = new BufferedInputStream(zipFile.getInputStream(entry));
/*     */ 
/* 454 */           byte[] buffer = new byte[1024];
/* 455 */           FileOutputStream fos = new FileOutputStream(destinationFilePath);
/* 456 */           BufferedOutputStream bos = new BufferedOutputStream(fos, 1024);
/* 457 */           while ((b = bis.read(buffer, 0, 1024)) != -1)
/*     */           {
/*     */             int b;
/* 458 */             bos.write(buffer, 0, b);
/*     */           }
/* 460 */           bos.flush();
/* 461 */           bos.close();
/* 462 */           bis.close();
/* 463 */           name = destinationFilePath.getName();
/* 464 */           if ((name.endsWith(".jar")) && (pluginFile(name))) {
/* 465 */             destinationFilePath.renameTo(new File(this.plugin.getDataFolder().getParent(), this.updateFolder + "/" + name));
/*     */           }
/*     */ 
/* 468 */           entry = null;
/* 469 */           destinationFilePath = null;
/*     */         }
/*     */       }
/* 471 */       e = null;
/* 472 */       zipFile.close();
/* 473 */       zipFile = null;
/*     */ 
/* 476 */       BufferedInputStream bis = (b = new File(zipPath).listFiles()).length; for (File destinationFilePath = 0; destinationFilePath < bis; destinationFilePath++) { File dFile = b[destinationFilePath];
/* 477 */         if ((dFile.isDirectory()) && 
/* 478 */           (pluginFile(dFile.getName()))) {
/* 479 */           File oFile = new File(this.plugin.getDataFolder().getParent(), dFile.getName());
/* 480 */           File[] contents = oFile.listFiles();
/*     */           File[] arrayOfFile1;
/* 481 */           String str1 = (arrayOfFile1 = dFile.listFiles()).length; for (name = 0; name < str1; name++) { File cFile = arrayOfFile1[name];
/*     */ 
/* 483 */             boolean found = false;
/* 484 */             for (File xFile : contents)
/*     */             {
/* 486 */               if (xFile.getName().equals(cFile.getName())) {
/* 487 */                 found = true;
/* 488 */                 break;
/*     */               }
/*     */             }
/* 491 */             if (!found)
/*     */             {
/* 493 */               cFile.renameTo(new File(oFile.getCanonicalFile() + "/" + cFile.getName()));
/*     */             }
/*     */             else {
/* 496 */               cFile.delete();
/*     */             }
/*     */           }
/*     */         }
/*     */ 
/* 501 */         dFile.delete();
/*     */       }
/* 503 */       new File(zipPath).delete();
/* 504 */       fSourceZip.delete();
/*     */     } catch (IOException ex) {
/* 506 */       this.plugin.getLogger().warning("The auto-updater tried to unzip a new update file, but was unsuccessful.");
/* 507 */       this.result = UpdateResult.FAIL_DOWNLOAD;
/* 508 */       ex.printStackTrace();
/*     */     }
/* 510 */     new File(file).delete();
/*     */   }
/*     */ 
/*     */   private boolean pluginFile(String name)
/*     */   {
/* 517 */     for (File file : new File("plugins").listFiles()) {
/* 518 */       if (file.getName().equals(name)) {
/* 519 */         return true;
/*     */       }
/*     */     }
/* 522 */     return false;
/*     */   }
/*     */ 
/*     */   private boolean versionCheck(String title)
/*     */   {
/* 529 */     if (this.type != UpdateType.NO_VERSION_CHECK) {
/* 530 */       String version = this.plugin.getDescription().getVersion();
/* 531 */       if (title.split(" v").length == 2) {
/* 532 */         String remoteVersion = title.split(" v")[1].split(" ")[0];
/*     */ 
/* 534 */         if ((hasTag(version)) || (version.equalsIgnoreCase(remoteVersion)))
/*     */         {
/* 536 */           this.result = UpdateResult.NO_UPDATE;
/* 537 */           return false;
/*     */         }
/*     */       }
/*     */       else {
/* 541 */         String authorInfo = " (" + (String)this.plugin.getDescription().getAuthors().get(0) + ")";
/* 542 */         this.plugin.getLogger().warning("The author of this plugin" + authorInfo + " has misconfigured their Auto Update system");
/* 543 */         this.plugin.getLogger().warning("File versions should follow the format 'PluginName vVERSION'");
/* 544 */         this.plugin.getLogger().warning("Please notify the author of this error.");
/* 545 */         this.result = UpdateResult.FAIL_NOVERSION;
/* 546 */         return false;
/*     */       }
/*     */     }
/* 549 */     return true;
/*     */   }
/*     */ 
/*     */   private boolean hasTag(String version)
/*     */   {
/* 556 */     for (String string : NO_UPDATE_TAG) {
/* 557 */       if (version.contains(string)) {
/* 558 */         return true;
/*     */       }
/*     */     }
/* 561 */     return false;
/*     */   }
/*     */ 
/*     */   private boolean read() {
/*     */     try {
/* 566 */       URLConnection conn = this.url.openConnection();
/* 567 */       conn.setConnectTimeout(5000);
/*     */ 
/* 569 */       if (this.apiKey != null) {
/* 570 */         conn.addRequestProperty("X-API-Key", this.apiKey);
/*     */       }
/* 572 */       conn.addRequestProperty("User-Agent", "Updater (by Gravity)");
/*     */ 
/* 574 */       conn.setDoOutput(true);
/*     */ 
/* 576 */       BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
/* 577 */       String response = reader.readLine();
/*     */ 
/* 579 */       JSONArray array = (JSONArray)JSONValue.parse(response);
/*     */ 
/* 581 */       if (array.size() == 0) {
/* 582 */         this.plugin.getLogger().warning("The updater could not find any files for the project id " + this.id);
/* 583 */         this.result = UpdateResult.FAIL_BADID;
/* 584 */         return false;
/*     */       }
/*     */ 
/* 587 */       this.versionName = ((String)((JSONObject)array.get(array.size() - 1)).get("name"));
/* 588 */       this.versionLink = ((String)((JSONObject)array.get(array.size() - 1)).get("downloadUrl"));
/* 589 */       this.versionType = ((String)((JSONObject)array.get(array.size() - 1)).get("releaseType"));
/* 590 */       this.versionGameVersion = ((String)((JSONObject)array.get(array.size() - 1)).get("gameVersion"));
/*     */ 
/* 592 */       return true;
/*     */     } catch (IOException e) {
/* 594 */       if (e.getMessage().contains("HTTP response code: 403")) {
/* 595 */         this.plugin.getLogger().warning("dev.bukkit.org rejected the API key provided in plugins/Updater/config.yml");
/* 596 */         this.plugin.getLogger().warning("Please double-check your configuration to ensure it is correct.");
/* 597 */         this.result = UpdateResult.FAIL_APIKEY;
/*     */       } else {
/* 599 */         this.plugin.getLogger().warning("The updater could not contact dev.bukkit.org for updating.");
/* 600 */         this.plugin.getLogger().warning("If you have not recently modified your configuration and this is the first time you are seeing this message, the site may be experiencing temporary downtime.");
/* 601 */         this.result = UpdateResult.FAIL_DBO;
/*     */       }
/* 603 */       e.printStackTrace();
/* 604 */     }return false;
/*     */   }
/*     */ 
/*     */   public static enum UpdateResult
/*     */   {
/* 191 */     SUCCESS, 
/*     */ 
/* 195 */     NO_UPDATE, 
/*     */ 
/* 199 */     DISABLED, 
/*     */ 
/* 203 */     FAIL_DOWNLOAD, 
/*     */ 
/* 207 */     FAIL_DBO, 
/*     */ 
/* 211 */     FAIL_NOVERSION, 
/*     */ 
/* 215 */     FAIL_BADID, 
/*     */ 
/* 219 */     FAIL_APIKEY, 
/*     */ 
/* 223 */     UPDATE_AVAILABLE;
/*     */   }
/*     */ 
/*     */   private class UpdateRunnable
/*     */     implements Runnable
/*     */   {
/*     */     private UpdateRunnable()
/*     */     {
/*     */     }
/*     */ 
/*     */     public void run()
/*     */     {
/* 612 */       if (Updater.this.url != null)
/*     */       {
/* 614 */         if ((Updater.this.read()) && 
/* 615 */           (Updater.this.versionCheck(Updater.this.versionName)))
/* 616 */           if ((Updater.this.versionLink != null) && (Updater.this.type != Updater.UpdateType.NO_DOWNLOAD)) {
/* 617 */             String name = Updater.this.file.getName();
/*     */ 
/* 619 */             if (Updater.this.versionLink.endsWith(".zip")) {
/* 620 */               String[] split = Updater.this.versionLink.split("/");
/* 621 */               name = split[(split.length - 1)];
/*     */             }
/* 623 */             Updater.this.saveFile(new File(Updater.this.plugin.getDataFolder().getParent(), Updater.this.updateFolder), name, Updater.this.versionLink);
/*     */           } else {
/* 625 */             Updater.this.result = Updater.UpdateResult.UPDATE_AVAILABLE;
/*     */           }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public static enum UpdateType
/*     */   {
/* 233 */     DEFAULT, 
/*     */ 
/* 237 */     NO_VERSION_CHECK, 
/*     */ 
/* 241 */     NO_DOWNLOAD;
/*     */   }
/*     */ 
/*     */   public static class UpdaterHandler
/*     */   {
/*  49 */     public static boolean updateFound = false;
/*  50 */     public static String updateVersion = "unknown";
/*  51 */     public static boolean updateAlreadyDownloaded = false;
/*     */     private static Plugin plugin;
/*     */     private static int projectId;
/*     */     private static String pluginChatPrefix;
/*     */     private static File pluginFile;
/*     */     private static ChatColor primaryColor;
/*     */     private static String updateCommand;
/*     */     private static String bukkitDevSlug;
/*     */ 
/*     */     public static void setup(Plugin plugin, int projectId, String pluginChatPrefix, File pluginFile, ChatColor primaryColor, String updateCommand, String bukkitDevSlug)
/*     */     {
/*  62 */       plugin = plugin;
/*  63 */       projectId = projectId;
/*  64 */       pluginChatPrefix = pluginChatPrefix;
/*  65 */       pluginFile = pluginFile;
/*  66 */       primaryColor = primaryColor;
/*  67 */       updateCommand = updateCommand;
/*  68 */       bukkitDevSlug = bukkitDevSlug;
/*     */     }
/*     */ 
/*     */     public static void notifyIfUpdateWasFound(Player player, String updatePermission)
/*     */     {
/*  73 */       if ((updateFound) && (!updateAlreadyDownloaded) && (player.hasPermission(updatePermission)))
/*  74 */         Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
/*  75 */           public void run() { Updater.UpdaterHandler.this.sendMessage(Updater.UpdaterHandler.pluginChatPrefix + Updater.UpdaterHandler.primaryColor + "Found an update: v" + Updater.UpdaterHandler.updateVersion + "   §7(Your version: v" + Updater.UpdaterHandler.plugin.getDescription().getVersion() + ")");
/*  76 */             Updater.UpdaterHandler.this.sendMessage(Updater.UpdaterHandler.pluginChatPrefix + "§7Type \"" + Updater.UpdaterHandler.primaryColor + Updater.UpdaterHandler.updateCommand + "§7\" or download it from:");
/*  77 */             Updater.UpdaterHandler.this.sendMessage(Updater.UpdaterHandler.pluginChatPrefix + "§7dev.bukkit.org/bukkit-plugins/" + Updater.UpdaterHandler.bukkitDevSlug);
/*     */           }
/*     */         }
/*     */         , 10L);
/*     */     }
/*     */ 
/*     */     public static void startupUpdateCheck()
/*     */     {
/*  84 */       if (plugin == null) try {
/*  85 */           throw new Exception("The developer did not setup the updater correctly"); } catch (Exception continueRuntime) {
/*  86 */           continueRuntime.printStackTrace();
/*  87 */           return;
/*     */         }
/*     */ 
/*  90 */       Updater updater = new Updater(plugin, projectId, pluginFile, Updater.UpdateType.NO_DOWNLOAD, true);
/*  91 */       if (updater.getResult() == Updater.UpdateResult.UPDATE_AVAILABLE)
/*     */       {
/*  93 */         updateFound = true;
/*     */ 
/*  95 */         if (updater.getLatestName().split(" v").length == 2) {
/*  96 */           updateVersion = updater.getLatestName().split(" v")[1].split(" ")[0];
/*     */         }
/*     */ 
/*  99 */         Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
/* 100 */           public void run() { Bukkit.getConsoleSender().sendMessage(Updater.UpdaterHandler.pluginChatPrefix + Updater.UpdaterHandler.primaryColor + "Found an update: v" + Updater.UpdaterHandler.updateVersion + "   §f(Your version: v" + Updater.UpdaterHandler.plugin.getDescription().getVersion() + ")");
/* 101 */             Bukkit.getConsoleSender().sendMessage(Updater.UpdaterHandler.pluginChatPrefix + "§fType \"" + Updater.UpdaterHandler.primaryColor + Updater.UpdaterHandler.updateCommand + "§f\" or download it from:");
/* 102 */             Bukkit.getConsoleSender().sendMessage(Updater.UpdaterHandler.pluginChatPrefix + "§fdev.bukkit.org/bukkit-plugins/" + Updater.UpdaterHandler.bukkitDevSlug);
/*     */           }
/*     */         }
/*     */         , 1L);
/*     */       }
/*     */     }
/*     */ 
/*     */     public static void manuallyCheckUpdates(CommandSender sender)
/*     */     {
/* 111 */       if (plugin == null) try {
/* 112 */           throw new Exception("The developer did not setup the updater correctly"); } catch (Exception continueRuntime) {
/* 113 */           continueRuntime.printStackTrace();
/* 114 */           return;
/*     */         }
/*     */ 
/* 117 */       sender.sendMessage(pluginChatPrefix + "§7Please wait while the plugin is searching for updates. If it finds one, you will see the progress on the console.");
/* 118 */       Updater updater = new Updater(plugin, projectId, pluginFile, Updater.UpdateType.DEFAULT, true);
/*     */ 
/* 120 */       switch ($SWITCH_TABLE$com$gmail$filoghost$healthbar$Updater$UpdateResult()[updater.getResult().ordinal()]) {
/*     */       case 1:
/* 122 */         sender.sendMessage(pluginChatPrefix + "§7The update will be loaded on the next server startup.");
/* 123 */         updateAlreadyDownloaded = true;
/* 124 */         break;
/*     */       case 3:
/* 126 */         sender.sendMessage(pluginChatPrefix + "§7The updater is disabled. If you want to enable it, edit /plugins/Updater/config.yml accordingly.");
/* 127 */         break;
/*     */       case 8:
/* 129 */         sender.sendMessage(pluginChatPrefix + "§7You provided an invalid API key for the updater to use (/plugin/Updater/config.yml).");
/* 130 */         break;
/*     */       case 7:
/* 132 */         sender.sendMessage(pluginChatPrefix + "§7The project ID didn't exist. Please contact the developer.");
/* 133 */         break;
/*     */       case 5:
/* 135 */         sender.sendMessage(pluginChatPrefix + "§7The updater was unable to contact dev.bukkit.org. Please retry later.");
/* 136 */         break;
/*     */       case 4:
/* 138 */         sender.sendMessage(pluginChatPrefix + "§7The updater failed to download the update. Please download the file manually.");
/* 139 */         break;
/*     */       case 6:
/* 141 */         sender.sendMessage(pluginChatPrefix + "§7The latest file on dev.bukkit.org did not contain a version. Please manually check for updates and contact the developer.");
/* 142 */         break;
/*     */       case 2:
/* 144 */         sender.sendMessage(pluginChatPrefix + "§7The plugin is already updated.");
/* 145 */         break;
/*     */       case 9:
/* 147 */         sender.sendMessage(pluginChatPrefix + "§7The update is ready, but has not been downloaded yet.");
/* 148 */         break;
/*     */       default:
/* 150 */         sender.sendMessage(pluginChatPrefix + "§7The updater encountered an unexpected error. Check the console and retry later.");
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\HealthBar.jar
 * Qualified Name:     com.gmail.filoghost.healthbar.Updater
 * JD-Core Version:    0.6.2
 */