/*     */ package me.xADudex.BDJ;
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
/*     */ import java.util.logging.Level;
/*     */ import java.util.logging.Logger;
/*     */ import java.util.zip.ZipEntry;
/*     */ import java.util.zip.ZipFile;
/*     */ import org.bukkit.Server;
/*     */ import org.bukkit.configuration.file.YamlConfiguration;
/*     */ import org.bukkit.configuration.file.YamlConfigurationOptions;
/*     */ import org.bukkit.plugin.Plugin;
/*     */ import org.bukkit.plugin.PluginDescriptionFile;
/*     */ import org.bukkit.scheduler.BukkitRunnable;
/*     */ import org.json.simple.JSONArray;
/*     */ import org.json.simple.JSONObject;
/*     */ import org.json.simple.JSONValue;
/*     */ 
/*     */ public class Updater
/*     */ {
/*     */   private static final String TITLE_VALUE = "name";
/*     */   private static final String LINK_VALUE = "downloadUrl";
/*     */   private static final String TYPE_VALUE = "releaseType";
/*     */   private static final String VERSION_VALUE = "gameVersion";
/*     */   private static final String QUERY = "/servermods/files?projectIds=";
/*     */   private static final String HOST = "https://api.curseforge.com";
/*     */   private static final String USER_AGENT = "Updater (by Gravity)";
/*     */   private static final String DELIMETER = "^v|[\\s_-]v";
/*  57 */   private static final String[] NO_UPDATE_TAG = { "-DEV", "-PRE", "-SNAPSHOT", "beta" };
/*     */   private static final int BYTE_SIZE = 1024;
/*     */   private static final String API_KEY_CONFIG_KEY = "api-key";
/*     */   private static final String DISABLE_CONFIG_KEY = "disable";
/*     */   private static final String API_KEY_DEFAULT = "PUT_API_KEY_HERE";
/*     */   private static final boolean DISABLE_DEFAULT = false;
/*     */   private final Plugin plugin;
/*     */   private final UpdateType type;
/*     */   private final boolean announce;
/*     */   private final File file;
/*     */   private final File updateFolder;
/*     */   private final UpdateCallback callback;
/*  84 */   private int id = -1;
/*     */ 
/*  86 */   private String apiKey = null;
/*     */   private String versionName;
/*     */   private String versionLink;
/*     */   private String versionType;
/*     */   private String versionGameVersion;
/*     */   private URL url;
/*     */   private Thread thread;
/* 102 */   private UpdateResult result = UpdateResult.SUCCESS;
/*     */ 
/*     */   public Updater(Plugin plugin, int id, File file, UpdateType type, boolean announce)
/*     */   {
/* 192 */     this(plugin, id, file, type, null, announce);
/*     */   }
/*     */ 
/*     */   public Updater(Plugin plugin, int id, File file, UpdateType type, UpdateCallback callback)
/*     */   {
/* 205 */     this(plugin, id, file, type, callback, false);
/*     */   }
/*     */ 
/*     */   public Updater(Plugin plugin, int id, File file, UpdateType type, UpdateCallback callback, boolean announce)
/*     */   {
/* 219 */     this.plugin = plugin;
/* 220 */     this.type = type;
/* 221 */     this.announce = announce;
/* 222 */     this.file = file;
/* 223 */     this.id = id;
/* 224 */     this.updateFolder = this.plugin.getServer().getUpdateFolderFile();
/* 225 */     this.callback = callback;
/*     */ 
/* 227 */     File pluginFile = this.plugin.getDataFolder().getParentFile();
/* 228 */     File updaterFile = new File(pluginFile, "Updater");
/* 229 */     File updaterConfigFile = new File(updaterFile, "config.yml");
/*     */ 
/* 231 */     YamlConfiguration config = new YamlConfiguration();
/* 232 */     config.options().header("This configuration file affects all plugins using the Updater system (version 2+ - http://forums.bukkit.org/threads/96681/ )\nIf you wish to use your API key, read http://wiki.bukkit.org/ServerMods_API and place it below.\nSome updating systems will not adhere to the disabled value, but these may be turned off in their plugin's configuration.");
/*     */ 
/* 235 */     config.addDefault("api-key", "PUT_API_KEY_HERE");
/* 236 */     config.addDefault("disable", Boolean.valueOf(false));
/*     */ 
/* 238 */     if (!updaterFile.exists()) {
/* 239 */       fileIOOrError(updaterFile, updaterFile.mkdir(), true);
/*     */     }
/*     */ 
/* 242 */     boolean createFile = !updaterConfigFile.exists();
/*     */     try {
/* 244 */       if (createFile) {
/* 245 */         fileIOOrError(updaterConfigFile, updaterConfigFile.createNewFile(), true);
/* 246 */         config.options().copyDefaults(true);
/* 247 */         config.save(updaterConfigFile);
/*     */       } else {
/* 249 */         config.load(updaterConfigFile);
/*     */       }
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/*     */       String message;
/*     */       String message;
/* 253 */       if (createFile)
/* 254 */         message = "The updater could not create configuration at " + updaterFile.getAbsolutePath();
/*     */       else {
/* 256 */         message = "The updater could not load configuration at " + updaterFile.getAbsolutePath();
/*     */       }
/* 258 */       this.plugin.getLogger().log(Level.SEVERE, message, e);
/*     */     }
/*     */ 
/* 261 */     if (config.getBoolean("disable")) {
/* 262 */       this.result = UpdateResult.DISABLED;
/* 263 */       return;
/*     */     }
/*     */ 
/* 266 */     String key = config.getString("api-key");
/* 267 */     if (("PUT_API_KEY_HERE".equalsIgnoreCase(key)) || ("".equals(key))) {
/* 268 */       key = null;
/*     */     }
/*     */ 
/* 271 */     this.apiKey = key;
/*     */     try
/*     */     {
/* 274 */       this.url = new URL("https://api.curseforge.com/servermods/files?projectIds=" + this.id);
/*     */     } catch (MalformedURLException e) {
/* 276 */       this.plugin.getLogger().log(Level.SEVERE, "The project ID provided for updating, " + this.id + " is invalid.", e);
/* 277 */       this.result = UpdateResult.FAIL_BADID;
/*     */     }
/*     */ 
/* 280 */     if (this.result != UpdateResult.FAIL_BADID) {
/* 281 */       this.thread = new Thread(new UpdateRunnable(null));
/* 282 */       this.thread.start();
/*     */     } else {
/* 284 */       runUpdater();
/*     */     }
/*     */   }
/*     */ 
/*     */   public UpdateResult getResult()
/*     */   {
/* 295 */     waitForThread();
/* 296 */     return this.result;
/*     */   }
/*     */ 
/*     */   public ReleaseType getLatestType()
/*     */   {
/* 306 */     waitForThread();
/* 307 */     if (this.versionType != null) {
/* 308 */       for (ReleaseType type : ReleaseType.values()) {
/* 309 */         if (this.versionType.equalsIgnoreCase(type.name())) {
/* 310 */           return type;
/*     */         }
/*     */       }
/*     */     }
/* 314 */     return null;
/*     */   }
/*     */ 
/*     */   public String getLatestGameVersion()
/*     */   {
/* 323 */     waitForThread();
/* 324 */     return this.versionGameVersion;
/*     */   }
/*     */ 
/*     */   public String getLatestName()
/*     */   {
/* 333 */     waitForThread();
/* 334 */     return this.versionName;
/*     */   }
/*     */ 
/*     */   public String getLatestFileLink()
/*     */   {
/* 343 */     waitForThread();
/* 344 */     return this.versionLink;
/*     */   }
/*     */ 
/*     */   private void waitForThread()
/*     */   {
/* 352 */     if ((this.thread != null) && (this.thread.isAlive()))
/*     */       try {
/* 354 */         this.thread.join();
/*     */       } catch (InterruptedException e) {
/* 356 */         this.plugin.getLogger().log(Level.SEVERE, null, e);
/*     */       }
/*     */   }
/*     */ 
/*     */   private void saveFile(String file)
/*     */   {
/* 367 */     File folder = this.updateFolder;
/*     */ 
/* 369 */     deleteOldFiles();
/* 370 */     if (!folder.exists()) {
/* 371 */       fileIOOrError(folder, folder.mkdir(), true);
/*     */     }
/* 373 */     downloadFile();
/*     */ 
/* 376 */     File dFile = new File(folder.getAbsolutePath(), file);
/* 377 */     if (dFile.getName().endsWith(".zip"))
/*     */     {
/* 379 */       unzip(dFile.getAbsolutePath());
/*     */     }
/* 381 */     if (this.announce)
/* 382 */       this.plugin.getLogger().info("Finished updating.");
/*     */   }
/*     */ 
/*     */   private void downloadFile()
/*     */   {
/* 390 */     BufferedInputStream in = null;
/* 391 */     FileOutputStream fout = null;
/*     */     try {
/* 393 */       URL fileUrl = new URL(this.versionLink);
/* 394 */       int fileLength = fileUrl.openConnection().getContentLength();
/* 395 */       in = new BufferedInputStream(fileUrl.openStream());
/* 396 */       fout = new FileOutputStream(new File(this.updateFolder, this.file.getName()));
/*     */ 
/* 398 */       byte[] data = new byte[1024];
/*     */ 
/* 400 */       if (this.announce) {
/* 401 */         this.plugin.getLogger().info("About to download a new update: " + this.versionName);
/*     */       }
/* 403 */       long downloaded = 0L;
/*     */       int count;
/* 404 */       while ((count = in.read(data, 0, 1024)) != -1)
/*     */       {
/*     */         int count;
/* 405 */         downloaded += count;
/* 406 */         fout.write(data, 0, count);
/* 407 */         int percent = (int)(downloaded * 100L / fileLength);
/* 408 */         if ((this.announce) && (percent % 10 == 0))
/* 409 */           this.plugin.getLogger().info("Downloading update: " + percent + "% of " + fileLength + " bytes.");
/*     */       }
/*     */     }
/*     */     catch (Exception ex) {
/* 413 */       this.plugin.getLogger().log(Level.WARNING, "The auto-updater tried to download a new update, but was unsuccessful.", ex);
/* 414 */       this.result = UpdateResult.FAIL_DOWNLOAD;
/*     */       try
/*     */       {
/* 417 */         if (in != null)
/* 418 */           in.close();
/*     */       }
/*     */       catch (IOException ex) {
/* 421 */         this.plugin.getLogger().log(Level.SEVERE, null, ex);
/*     */       }
/*     */       try {
/* 424 */         if (fout != null)
/* 425 */           fout.close();
/*     */       }
/*     */       catch (IOException ex) {
/* 428 */         this.plugin.getLogger().log(Level.SEVERE, null, ex);
/*     */       }
/*     */     }
/*     */     finally
/*     */     {
/*     */       try
/*     */       {
/* 417 */         if (in != null)
/* 418 */           in.close();
/*     */       }
/*     */       catch (IOException ex) {
/* 421 */         this.plugin.getLogger().log(Level.SEVERE, null, ex);
/*     */       }
/*     */       try {
/* 424 */         if (fout != null)
/* 425 */           fout.close();
/*     */       }
/*     */       catch (IOException ex) {
/* 428 */         this.plugin.getLogger().log(Level.SEVERE, null, ex);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private void deleteOldFiles()
/*     */   {
/* 438 */     File[] list = listFilesOrError(this.updateFolder);
/* 439 */     for (File xFile : list)
/* 440 */       if (xFile.getName().endsWith(".zip"))
/* 441 */         fileIOOrError(xFile, xFile.mkdir(), true);
/*     */   }
/*     */ 
/*     */   private void unzip(String file)
/*     */   {
/* 452 */     File fSourceZip = new File(file);
/*     */     try {
/* 454 */       String zipPath = file.substring(0, file.length() - 4);
/* 455 */       ZipFile zipFile = new ZipFile(fSourceZip);
/* 456 */       Enumeration e = zipFile.entries();
/* 457 */       while (e.hasMoreElements()) {
/* 458 */         ZipEntry entry = (ZipEntry)e.nextElement();
/* 459 */         File destinationFilePath = new File(zipPath, entry.getName());
/* 460 */         fileIOOrError(destinationFilePath.getParentFile(), destinationFilePath.getParentFile().mkdirs(), true);
/* 461 */         if (!entry.isDirectory()) {
/* 462 */           BufferedInputStream bis = new BufferedInputStream(zipFile.getInputStream(entry));
/*     */ 
/* 464 */           byte[] buffer = new byte[1024];
/* 465 */           FileOutputStream fos = new FileOutputStream(destinationFilePath);
/* 466 */           BufferedOutputStream bos = new BufferedOutputStream(fos, 1024);
/*     */           int b;
/* 467 */           while ((b = bis.read(buffer, 0, 1024)) != -1)
/*     */           {
/*     */             int b;
/* 468 */             bos.write(buffer, 0, b);
/*     */           }
/* 470 */           bos.flush();
/* 471 */           bos.close();
/* 472 */           bis.close();
/* 473 */           String name = destinationFilePath.getName();
/* 474 */           if ((name.endsWith(".jar")) && (pluginExists(name))) {
/* 475 */             File output = new File(this.updateFolder, name);
/* 476 */             fileIOOrError(output, destinationFilePath.renameTo(output), true);
/*     */           }
/*     */         }
/*     */       }
/* 480 */       zipFile.close();
/*     */ 
/* 483 */       moveNewZipFiles(zipPath);
/*     */     }
/*     */     catch (IOException e) {
/* 486 */       this.plugin.getLogger().log(Level.SEVERE, "The auto-updater tried to unzip a new update file, but was unsuccessful.", e);
/* 487 */       this.result = UpdateResult.FAIL_DOWNLOAD;
/*     */     } finally {
/* 489 */       fileIOOrError(fSourceZip, fSourceZip.delete(), false);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void moveNewZipFiles(String zipPath)
/*     */   {
/* 498 */     File[] list = listFilesOrError(new File(zipPath));
/* 499 */     for (File dFile : list) {
/* 500 */       if ((dFile.isDirectory()) && (pluginExists(dFile.getName())))
/*     */       {
/* 502 */         File oFile = new File(this.plugin.getDataFolder().getParent(), dFile.getName());
/*     */ 
/* 504 */         File[] dList = listFilesOrError(dFile);
/*     */ 
/* 506 */         File[] oList = listFilesOrError(oFile);
/* 507 */         for (File cFile : dList)
/*     */         {
/* 509 */           boolean found = false;
/* 510 */           for (File xFile : oList)
/*     */           {
/* 512 */             if (xFile.getName().equals(cFile.getName())) {
/* 513 */               found = true;
/* 514 */               break;
/*     */             }
/*     */           }
/* 517 */           if (!found)
/*     */           {
/* 519 */             File output = new File(oFile, cFile.getName());
/* 520 */             fileIOOrError(output, cFile.renameTo(output), true);
/*     */           }
/*     */           else {
/* 523 */             fileIOOrError(cFile, cFile.delete(), false);
/*     */           }
/*     */         }
/*     */       }
/* 527 */       fileIOOrError(dFile, dFile.delete(), false);
/*     */     }
/* 529 */     File zip = new File(zipPath);
/* 530 */     fileIOOrError(zip, zip.delete(), false);
/*     */   }
/*     */ 
/*     */   private boolean pluginExists(String name)
/*     */   {
/* 540 */     File[] plugins = listFilesOrError(new File("plugins"));
/* 541 */     for (File file : plugins) {
/* 542 */       if (file.getName().equals(name)) {
/* 543 */         return true;
/*     */       }
/*     */     }
/* 546 */     return false;
/*     */   }
/*     */ 
/*     */   private boolean versionCheck()
/*     */   {
/* 555 */     String title = this.versionName;
/* 556 */     if (this.type != UpdateType.NO_VERSION_CHECK) {
/* 557 */       String localVersion = this.plugin.getDescription().getVersion();
/* 558 */       if (title.split("^v|[\\s_-]v").length == 2)
/*     */       {
/* 560 */         String remoteVersion = title.split("^v|[\\s_-]v")[1].split(" ")[0];
/*     */ 
/* 562 */         if ((hasTag(localVersion)) || (!shouldUpdate(localVersion, remoteVersion)))
/*     */         {
/* 564 */           this.result = UpdateResult.NO_UPDATE;
/* 565 */           return false;
/*     */         }
/*     */       }
/*     */       else {
/* 569 */         String authorInfo = " (" + (String)this.plugin.getDescription().getAuthors().get(0) + ")";
/* 570 */         this.plugin.getLogger().warning("The author of this plugin" + authorInfo + " has misconfigured their Auto Update system");
/* 571 */         this.plugin.getLogger().warning("File versions should follow the format 'PluginName vVERSION'");
/* 572 */         this.plugin.getLogger().warning("Please notify the author of this error.");
/* 573 */         this.result = UpdateResult.FAIL_NOVERSION;
/* 574 */         return false;
/*     */       }
/*     */     }
/* 577 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean shouldUpdate(String localVersion, String remoteVersion)
/*     */   {
/* 608 */     return !localVersion.equalsIgnoreCase(remoteVersion);
/*     */   }
/*     */ 
/*     */   private boolean hasTag(String version)
/*     */   {
/* 618 */     for (String string : NO_UPDATE_TAG) {
/* 619 */       if (version.contains(string)) {
/* 620 */         return true;
/*     */       }
/*     */     }
/* 623 */     return false;
/*     */   }
/*     */ 
/*     */   private boolean read()
/*     */   {
/*     */     try
/*     */     {
/* 633 */       URLConnection conn = this.url.openConnection();
/* 634 */       conn.setConnectTimeout(5000);
/*     */ 
/* 636 */       if (this.apiKey != null) {
/* 637 */         conn.addRequestProperty("X-API-Key", this.apiKey);
/*     */       }
/* 639 */       conn.addRequestProperty("User-Agent", "Updater (by Gravity)");
/*     */ 
/* 641 */       conn.setDoOutput(true);
/*     */ 
/* 643 */       BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
/* 644 */       String response = reader.readLine();
/*     */ 
/* 646 */       JSONArray array = (JSONArray)JSONValue.parse(response);
/*     */ 
/* 648 */       if (array.isEmpty()) {
/* 649 */         this.plugin.getLogger().warning("The updater could not find any files for the project id " + this.id);
/* 650 */         this.result = UpdateResult.FAIL_BADID;
/* 651 */         return false;
/*     */       }
/*     */ 
/* 654 */       JSONObject latestUpdate = (JSONObject)array.get(array.size() - 1);
/* 655 */       this.versionName = ((String)latestUpdate.get("name"));
/* 656 */       this.versionLink = ((String)latestUpdate.get("downloadUrl"));
/* 657 */       this.versionType = ((String)latestUpdate.get("releaseType"));
/* 658 */       this.versionGameVersion = ((String)latestUpdate.get("gameVersion"));
/*     */ 
/* 660 */       return true;
/*     */     } catch (IOException e) {
/* 662 */       if (e.getMessage().contains("HTTP response code: 403")) {
/* 663 */         this.plugin.getLogger().severe("dev.bukkit.org rejected the API key provided in plugins/Updater/config.yml");
/* 664 */         this.plugin.getLogger().severe("Please double-check your configuration to ensure it is correct.");
/* 665 */         this.result = UpdateResult.FAIL_APIKEY;
/*     */       } else {
/* 667 */         this.plugin.getLogger().severe("The updater could not contact dev.bukkit.org for updating.");
/* 668 */         this.plugin.getLogger().severe("If you have not recently modified your configuration and this is the first time you are seeing this message, the site may be experiencing temporary downtime.");
/* 669 */         this.result = UpdateResult.FAIL_DBO;
/*     */       }
/* 671 */       this.plugin.getLogger().log(Level.SEVERE, null, e);
/* 672 */     }return false;
/*     */   }
/*     */ 
/*     */   private void fileIOOrError(File file, boolean result, boolean create)
/*     */   {
/* 683 */     if (!result)
/* 684 */       this.plugin.getLogger().severe("The updater could not " + (create ? "create" : "delete") + " file at: " + file.getAbsolutePath());
/*     */   }
/*     */ 
/*     */   private File[] listFilesOrError(File folder)
/*     */   {
/* 689 */     File[] contents = folder.listFiles();
/* 690 */     if (contents == null) {
/* 691 */       this.plugin.getLogger().severe("The updater could not access files at: " + this.updateFolder.getAbsolutePath());
/* 692 */       return new File[0];
/*     */     }
/* 694 */     return contents;
/*     */   }
/*     */ 
/*     */   private void runUpdater()
/*     */   {
/* 718 */     if ((this.url != null) && (read()) && (versionCheck()))
/*     */     {
/* 720 */       if ((this.versionLink != null) && (this.type != UpdateType.NO_DOWNLOAD)) {
/* 721 */         String name = this.file.getName();
/*     */ 
/* 723 */         if (this.versionLink.endsWith(".zip")) {
/* 724 */           name = this.versionLink.substring(this.versionLink.lastIndexOf("/") + 1);
/*     */         }
/* 726 */         saveFile(name);
/*     */       } else {
/* 728 */         this.result = UpdateResult.UPDATE_AVAILABLE;
/*     */       }
/*     */     }
/*     */ 
/* 732 */     if (this.callback != null)
/* 733 */       new BukkitRunnable()
/*     */       {
/*     */         public void run() {
/* 736 */           Updater.this.runCallback();
/*     */         }
/*     */       }
/* 738 */       .runTask(this.plugin);
/*     */   }
/*     */ 
/*     */   private void runCallback()
/*     */   {
/* 743 */     this.callback.onFinish(this);
/*     */   }
/*     */ 
/*     */   public static enum ReleaseType
/*     */   {
/* 168 */     ALPHA, 
/*     */ 
/* 172 */     BETA, 
/*     */ 
/* 176 */     RELEASE;
/*     */   }
/*     */ 
/*     */   public static abstract interface UpdateCallback
/*     */   {
/*     */     public abstract void onFinish(Updater paramUpdater);
/*     */   }
/*     */ 
/*     */   public static enum UpdateResult
/*     */   {
/* 108 */     SUCCESS, 
/*     */ 
/* 112 */     NO_UPDATE, 
/*     */ 
/* 116 */     DISABLED, 
/*     */ 
/* 120 */     FAIL_DOWNLOAD, 
/*     */ 
/* 124 */     FAIL_DBO, 
/*     */ 
/* 128 */     FAIL_NOVERSION, 
/*     */ 
/* 132 */     FAIL_BADID, 
/*     */ 
/* 136 */     FAIL_APIKEY, 
/*     */ 
/* 140 */     UPDATE_AVAILABLE;
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
/* 713 */       Updater.this.runUpdater();
/*     */     }
/*     */   }
/*     */ 
/*     */   public static enum UpdateType
/*     */   {
/* 150 */     DEFAULT, 
/*     */ 
/* 154 */     NO_VERSION_CHECK, 
/*     */ 
/* 158 */     NO_DOWNLOAD;
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\BetterDoubleJump.jar
 * Qualified Name:     me.xADudex.BDJ.Updater
 * JD-Core Version:    0.6.2
 */