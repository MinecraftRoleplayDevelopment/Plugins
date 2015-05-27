/*     */ package joe.crump.isort;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.util.concurrent.ConcurrentHashMap;
/*     */ import org.bukkit.ChatColor;
/*     */ import org.bukkit.Server;
/*     */ import org.bukkit.command.Command;
/*     */ import org.bukkit.command.CommandSender;
/*     */ import org.bukkit.entity.Player;
/*     */ import org.bukkit.plugin.PluginDescriptionFile;
/*     */ import org.bukkit.plugin.PluginManager;
/*     */ import org.bukkit.plugin.java.JavaPlugin;
/*     */ 
/*     */ public class InventorySort extends JavaPlugin
/*     */ {
/*  16 */   public static String AppName = "Joe's Inventory Sort";
/*     */ 
/*     */   public void onEnable()
/*     */   {
/*  21 */     JoeUtils.EnsureDirectory("plugins" + File.separator + "JoeInventorySort");
/*  22 */     JoeUtils.LoadPreferences();
/*     */ 
/*  25 */     PluginManager pm = getServer().getPluginManager();
/*  26 */     pm.registerEvents(new JoeListener(), this);
/*     */ 
/*  29 */     AppName = AppName + " " + getDescription().getVersion();
/*  30 */     JoeUtils.ConsoleMsg(ChatColor.GREEN + "Enabled " + ChatColor.LIGHT_PURPLE + AppName);
/*     */   }
/*     */ 
/*     */   public void onDisable()
/*     */   {
/*  36 */     JoeUtils.SavePreferences();
/*     */   }
/*     */ 
/*     */   public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args)
/*     */   {
/*  42 */     String command = cmd.getName();
/*     */ 
/*  44 */     if (command.equalsIgnoreCase("isort"))
/*     */     {
/*  46 */       if ((!cs.isOp()) && (cs.hasPermission("joe.invsort.noaccess")) && (!cs.hasPermission("joe.invsort.access")))
/*     */       {
/*  48 */         cs.sendMessage(ChatColor.RED + "Sorry, you don't have access to " + ChatColor.GOLD + "/isort");
/*  49 */         return true;
/*     */       }
/*     */ 
/*  53 */       if ((args.length == 0) || (!(cs instanceof Player)))
/*     */       {
/*  55 */         cs.sendMessage(JoeUtils.RainbowString("✈ " + AppName + " ✈", "bi"));
/*  56 */         if ((cs instanceof Player))
/*     */         {
/*  58 */           cs.sendMessage(ChatColor.YELLOW + "To toggle, use " + ChatColor.GOLD + "/iSort [on|off]");
/*     */         }
/*  60 */         return true;
/*     */       }
/*     */ 
/*  63 */       Player p = (Player)cs;
/*     */ 
/*  65 */       if ((args.length == 1) && (args[0].equalsIgnoreCase("on")))
/*     */       {
/*  67 */         JoeUtils.PlayerSortPreference.put(p.getName(), Boolean.valueOf(true));
/*  68 */         JoeUtils.PlayerSortPreference.remove(p.getName());
/*  69 */         cs.sendMessage(ChatColor.YELLOW + "Automatic Chest Sorting set to: " + ChatColor.LIGHT_PURPLE + ChatColor.BOLD + JoeUtils.DoesPlayerWantSorting(p));
/*  70 */         cs.sendMessage(ChatColor.GREEN + "Chests will now be sorted when you open them.");
/*  71 */         return true;
/*     */       }
/*  73 */       if ((args.length == 1) && (args[0].equalsIgnoreCase("off")))
/*     */       {
/*  75 */         JoeUtils.PlayerSortPreference.put(p.getName(), Boolean.valueOf(true));
/*  76 */         cs.sendMessage(ChatColor.YELLOW + "Automatic Chest Sorting set to: " + ChatColor.LIGHT_PURPLE + ChatColor.BOLD + JoeUtils.DoesPlayerWantSorting(p));
/*  77 */         cs.sendMessage(ChatColor.GREEN + "Ok. I see you have OCD and will handle everything yourself!");
/*  78 */         return true;
/*     */       }
/*     */ 
/*  81 */       cs.sendMessage(ChatColor.GREEN + "Automatic Chest Sorting is currently: " + ChatColor.LIGHT_PURPLE + ChatColor.BOLD + JoeUtils.DoesPlayerWantSorting(p));
/*  82 */       cs.sendMessage(ChatColor.YELLOW + "To toggle, use " + ChatColor.GOLD + "/iSort [on|off]");
/*     */ 
/*  84 */       return true;
/*     */     }
/*     */ 
/* 111 */     return false;
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\JoeInventorySort.jar
 * Qualified Name:     joe.crump.isort.InventorySort
 * JD-Core Version:    0.6.2
 */