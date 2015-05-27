/*     */ package net.milkbowl.vault.chat.plugins;
/*     */ 
/*     */ import com.nijiko.permissions.PermissionHandler;
/*     */ import com.nijikokun.bukkit.Permissions.Permissions;
/*     */ import java.util.logging.Logger;
/*     */ import net.milkbowl.vault.chat.Chat;
/*     */ import net.milkbowl.vault.permission.Permission;
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
/*     */ public class Chat_Permissions3 extends Chat
/*     */ {
/*  35 */   private static final Logger log = Logger.getLogger("Minecraft");
/*     */ 
/*  37 */   private String name = "Permissions 3 (Yeti) - Chat";
/*     */   private PermissionHandler perms;
/*  39 */   private Plugin plugin = null;
/*  40 */   private Permissions chat = null;
/*     */ 
/*     */   public Chat_Permissions3(Plugin plugin, Permission perms) {
/*  43 */     super(perms);
/*  44 */     this.plugin = plugin;
/*     */ 
/*  46 */     Bukkit.getServer().getPluginManager().registerEvents(new PermissionServerListener(), plugin);
/*     */ 
/*  49 */     if (this.chat == null) {
/*  50 */       Plugin p = plugin.getServer().getPluginManager().getPlugin("Permissions");
/*  51 */       if (p == null) {
/*  52 */         plugin.getServer().getPluginManager().getPlugin("vPerms");
/*  53 */         this.name = "vPerms - Chat";
/*     */       }
/*  55 */       if ((p != null) && 
/*  56 */         (p.isEnabled()) && (p.getDescription().getVersion().startsWith("3"))) {
/*  57 */         this.chat = ((Permissions)p);
/*  58 */         this.perms = this.chat.getHandler();
/*  59 */         log.info(String.format("[%s][Chat] %s hooked.", new Object[] { plugin.getDescription().getName(), this.name }));
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public String getName()
/*     */   {
/*  95 */     return this.name;
/*     */   }
/*     */ 
/*     */   public boolean isEnabled()
/*     */   {
/* 100 */     if (this.chat == null) {
/* 101 */       return false;
/*     */     }
/* 103 */     return this.chat.isEnabled();
/*     */   }
/*     */ 
/*     */   public int getPlayerInfoInteger(String world, String playerName, String node, int defaultValue)
/*     */   {
/* 108 */     Integer i = Integer.valueOf(this.perms.getPermissionInteger(world, playerName, node));
/* 109 */     return i == null ? defaultValue : i.intValue();
/*     */   }
/*     */ 
/*     */   public double getPlayerInfoDouble(String world, String playerName, String node, double defaultValue)
/*     */   {
/* 114 */     Double d = Double.valueOf(this.perms.getPermissionDouble(world, playerName, node));
/* 115 */     return d == null ? defaultValue : d.doubleValue();
/*     */   }
/*     */ 
/*     */   public boolean getPlayerInfoBoolean(String world, String playerName, String node, boolean defaultValue)
/*     */   {
/* 120 */     Boolean b = Boolean.valueOf(this.perms.getPermissionBoolean(world, playerName, node));
/* 121 */     return b == null ? defaultValue : b.booleanValue();
/*     */   }
/*     */ 
/*     */   public String getPlayerInfoString(String world, String playerName, String node, String defaultValue)
/*     */   {
/* 126 */     String s = this.perms.getPermissionString(world, playerName, node);
/* 127 */     return s == null ? defaultValue : s;
/*     */   }
/*     */ 
/*     */   public String getPlayerPrefix(String world, String playerName)
/*     */   {
/* 132 */     return getPlayerInfoString(world, playerName, "prefix", null);
/*     */   }
/*     */ 
/*     */   public String getPlayerSuffix(String world, String playerName)
/*     */   {
/* 137 */     return getPlayerInfoString(world, playerName, "suffix", null);
/*     */   }
/*     */ 
/*     */   public void setPlayerSuffix(String world, String player, String suffix)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void setPlayerPrefix(String world, String player, String prefix)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void setPlayerInfo(String world, String playerName, String node, Object value)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void setPlayerInfoInteger(String world, String playerName, String node, int value)
/*     */   {
/* 155 */     setPlayerInfo(world, playerName, node, Integer.valueOf(value));
/*     */   }
/*     */ 
/*     */   public void setPlayerInfoDouble(String world, String playerName, String node, double value)
/*     */   {
/* 160 */     setPlayerInfo(world, playerName, node, Double.valueOf(value));
/*     */   }
/*     */ 
/*     */   public void setPlayerInfoBoolean(String world, String playerName, String node, boolean value)
/*     */   {
/* 165 */     setPlayerInfo(world, playerName, node, Boolean.valueOf(value));
/*     */   }
/*     */ 
/*     */   public void setPlayerInfoString(String world, String playerName, String node, String value)
/*     */   {
/* 170 */     setPlayerInfo(world, playerName, node, value);
/*     */   }
/*     */ 
/*     */   public int getGroupInfoInteger(String world, String groupName, String node, int defaultValue)
/*     */   {
/* 175 */     int i = this.perms.getGroupPermissionInteger(world, groupName, node);
/* 176 */     return i != -1 ? i : defaultValue;
/*     */   }
/*     */ 
/*     */   public void setGroupInfo(String world, String groupName, String node, Object value)
/*     */   {
/* 181 */     this.perms.addGroupInfo(world, groupName, node, value);
/*     */   }
/*     */ 
/*     */   public void setGroupInfoInteger(String world, String groupName, String node, int value)
/*     */   {
/* 186 */     setGroupInfo(world, groupName, node, Integer.valueOf(value));
/*     */   }
/*     */ 
/*     */   public double getGroupInfoDouble(String world, String groupName, String node, double defaultValue)
/*     */   {
/* 191 */     double d = this.perms.getGroupPermissionDouble(world, groupName, node);
/* 192 */     return d != -1.0D ? d : defaultValue;
/*     */   }
/*     */ 
/*     */   public void setGroupInfoDouble(String world, String groupName, String node, double value)
/*     */   {
/* 197 */     setGroupInfo(world, groupName, node, Double.valueOf(value));
/*     */   }
/*     */ 
/*     */   public boolean getGroupInfoBoolean(String world, String groupName, String node, boolean defaultValue)
/*     */   {
/* 202 */     return this.perms.getGroupPermissionBoolean(world, groupName, node);
/*     */   }
/*     */ 
/*     */   public void setGroupInfoBoolean(String world, String groupName, String node, boolean value)
/*     */   {
/* 207 */     setGroupInfo(world, groupName, node, Boolean.valueOf(value));
/*     */   }
/*     */ 
/*     */   public String getGroupInfoString(String world, String groupName, String node, String defaultValue)
/*     */   {
/* 212 */     String s = this.perms.getGroupPermissionString(world, groupName, node);
/* 213 */     return s != null ? s : defaultValue;
/*     */   }
/*     */ 
/*     */   public void setGroupInfoString(String world, String groupName, String node, String value)
/*     */   {
/* 218 */     setGroupInfo(world, groupName, node, value);
/*     */   }
/*     */ 
/*     */   public String getGroupPrefix(String world, String group)
/*     */   {
/* 223 */     return this.perms.getGroupPrefix(world, group);
/*     */   }
/*     */ 
/*     */   public void setGroupPrefix(String world, String group, String prefix)
/*     */   {
/* 228 */     this.perms.addGroupInfo(world, group, "prefix", prefix);
/*     */   }
/*     */ 
/*     */   public String getGroupSuffix(String world, String group)
/*     */   {
/* 233 */     return this.perms.getGroupSuffix(world, group);
/*     */   }
/*     */ 
/*     */   public void setGroupSuffix(String world, String group, String suffix)
/*     */   {
/* 238 */     this.perms.addGroupInfo(world, group, "suffix", suffix);
/*     */   }
/*     */ 
/*     */   public class PermissionServerListener
/*     */     implements Listener
/*     */   {
/*     */     public PermissionServerListener()
/*     */     {
/*     */     }
/*     */ 
/*     */     @EventHandler(priority=EventPriority.MONITOR)
/*     */     public void onPluginEnable(PluginEnableEvent event)
/*     */     {
/*  69 */       if (Chat_Permissions3.this.chat == null) {
/*  70 */         Plugin permChat = event.getPlugin();
/*  71 */         if (((permChat.getDescription().getName().equals("Permissions")) || (permChat.getDescription().getName().equals("vPerms"))) && (permChat.getDescription().getVersion().startsWith("3")) && 
/*  72 */           (permChat.isEnabled())) {
/*  73 */           Chat_Permissions3.this.chat = ((Permissions)permChat);
/*  74 */           Chat_Permissions3.this.perms = Chat_Permissions3.this.chat.getHandler();
/*  75 */           Chat_Permissions3.log.info(String.format("[%s][Chat] %s hooked.", new Object[] { Chat_Permissions3.this.plugin.getDescription().getName(), Chat_Permissions3.this.name }));
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/*     */     @EventHandler(priority=EventPriority.MONITOR)
/*     */     public void onPluginDisable(PluginDisableEvent event)
/*     */     {
/*  83 */       if ((Chat_Permissions3.this.chat != null) && (
/*  84 */         (event.getPlugin().getDescription().getName().equals("Permissions")) || (event.getPlugin().getDescription().getName().equals("vPerms")))) {
/*  85 */         Chat_Permissions3.this.chat = null;
/*  86 */         Chat_Permissions3.this.perms = null;
/*  87 */         Chat_Permissions3.log.info(String.format("[%s][Chat] %s un-hooked.", new Object[] { Chat_Permissions3.this.plugin.getDescription().getName(), Chat_Permissions3.this.name }));
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\Vault.jar
 * Qualified Name:     net.milkbowl.vault.chat.plugins.Chat_Permissions3
 * JD-Core Version:    0.6.2
 */