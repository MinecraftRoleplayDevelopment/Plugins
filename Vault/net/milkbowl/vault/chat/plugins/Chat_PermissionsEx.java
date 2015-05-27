/*     */ package net.milkbowl.vault.chat.plugins;
/*     */ 
/*     */ import java.util.logging.Logger;
/*     */ import net.milkbowl.vault.chat.Chat;
/*     */ import net.milkbowl.vault.permission.Permission;
/*     */ import org.bukkit.Bukkit;
/*     */ import org.bukkit.OfflinePlayer;
/*     */ import org.bukkit.Server;
/*     */ import org.bukkit.event.EventHandler;
/*     */ import org.bukkit.event.EventPriority;
/*     */ import org.bukkit.event.Listener;
/*     */ import org.bukkit.event.server.PluginDisableEvent;
/*     */ import org.bukkit.event.server.PluginEnableEvent;
/*     */ import org.bukkit.plugin.Plugin;
/*     */ import org.bukkit.plugin.PluginDescriptionFile;
/*     */ import org.bukkit.plugin.PluginManager;
/*     */ import ru.tehkode.permissions.PermissionGroup;
/*     */ import ru.tehkode.permissions.PermissionManager;
/*     */ import ru.tehkode.permissions.PermissionUser;
/*     */ import ru.tehkode.permissions.bukkit.PermissionsEx;
/*     */ 
/*     */ public class Chat_PermissionsEx extends Chat
/*     */ {
/*  37 */   private static final Logger log = Logger.getLogger("Minecraft");
/*  38 */   private final String name = "PermissionsEx_Chat";
/*     */ 
/*  40 */   private Plugin plugin = null;
/*  41 */   private PermissionsEx chat = null;
/*     */ 
/*     */   public Chat_PermissionsEx(Plugin plugin, Permission perms) {
/*  44 */     super(perms);
/*  45 */     this.plugin = plugin;
/*     */ 
/*  47 */     Bukkit.getServer().getPluginManager().registerEvents(new PermissionServerListener(this), plugin);
/*     */ 
/*  50 */     if (this.chat == null) {
/*  51 */       Plugin p = plugin.getServer().getPluginManager().getPlugin("PermissionsEx");
/*  52 */       if ((p != null) && 
/*  53 */         (p.isEnabled())) {
/*  54 */         this.chat = ((PermissionsEx)p);
/*  55 */         log.info(String.format("[%s][Chat] %s hooked.", new Object[] { plugin.getDescription().getName(), "PermissionsEx_Chat" }));
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public String getName()
/*     */   {
/*  95 */     return "PermissionsEx_Chat";
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
/*     */   private PermissionUser getUser(OfflinePlayer op) {
/* 107 */     return PermissionsEx.getPermissionManager().getUser(op.getUniqueId());
/*     */   }
/*     */ 
/*     */   private PermissionUser getUser(String playerName) {
/* 111 */     return PermissionsEx.getPermissionManager().getUser(playerName);
/*     */   }
/*     */ 
/*     */   public int getPlayerInfoInteger(String world, String playerName, String node, int defaultValue)
/*     */   {
/* 116 */     return getUser(playerName).getOptionInteger(node, world, defaultValue);
/*     */   }
/*     */ 
/*     */   public double getPlayerInfoDouble(String world, String playerName, String node, double defaultValue)
/*     */   {
/* 121 */     return getUser(playerName).getOptionDouble(node, world, defaultValue);
/*     */   }
/*     */ 
/*     */   public boolean getPlayerInfoBoolean(String world, String playerName, String node, boolean defaultValue)
/*     */   {
/* 126 */     return getUser(playerName).getOptionBoolean(node, world, defaultValue);
/*     */   }
/*     */ 
/*     */   public String getPlayerInfoString(String world, String playerName, String node, String defaultValue)
/*     */   {
/* 131 */     return getUser(playerName).getOption(node, world, defaultValue);
/*     */   }
/*     */ 
/*     */   public int getPlayerInfoInteger(String world, OfflinePlayer op, String node, int defaultValue) {
/* 135 */     return getUser(op).getOptionInteger(node, world, defaultValue);
/*     */   }
/*     */ 
/*     */   public double getPlayerInfoDouble(String world, OfflinePlayer op, String node, double defaultValue) {
/* 139 */     return getUser(op).getOptionDouble(node, world, defaultValue);
/*     */   }
/*     */ 
/*     */   public boolean getPlayerInfoBoolean(String world, OfflinePlayer op, String node, boolean defaultValue) {
/* 143 */     return getUser(op).getOptionBoolean(node, world, defaultValue);
/*     */   }
/*     */ 
/*     */   public String getPlayerInfoString(String world, OfflinePlayer op, String node, String defaultValue) {
/* 147 */     return getUser(op).getOption(node, world, defaultValue);
/*     */   }
/*     */ 
/*     */   public void setPlayerInfoInteger(String world, OfflinePlayer op, String node, int value) {
/* 151 */     PermissionUser user = getUser(op);
/* 152 */     if (user != null)
/* 153 */       user.setOption(node, String.valueOf(value), world);
/*     */   }
/*     */ 
/*     */   public void setPlayerInfoDouble(String world, OfflinePlayer op, String node, double value)
/*     */   {
/* 158 */     PermissionUser user = getUser(op);
/* 159 */     if (user != null)
/* 160 */       user.setOption(node, String.valueOf(value), world);
/*     */   }
/*     */ 
/*     */   public void setPlayerInfoBoolean(String world, OfflinePlayer op, String node, boolean value)
/*     */   {
/* 165 */     PermissionUser user = getUser(op);
/* 166 */     if (user != null)
/* 167 */       user.setOption(node, String.valueOf(value), world);
/*     */   }
/*     */ 
/*     */   public void setPlayerInfoString(String world, OfflinePlayer op, String node, String value)
/*     */   {
/* 172 */     PermissionUser user = getUser(op);
/* 173 */     if (user != null)
/* 174 */       user.setOption(node, String.valueOf(value), world);
/*     */   }
/*     */ 
/*     */   public void setPlayerInfoInteger(String world, String playerName, String node, int value)
/*     */   {
/* 180 */     PermissionUser user = getUser(playerName);
/* 181 */     if (user != null)
/* 182 */       user.setOption(node, String.valueOf(value), world);
/*     */   }
/*     */ 
/*     */   public void setPlayerInfoDouble(String world, String playerName, String node, double value)
/*     */   {
/* 188 */     PermissionUser user = getUser(playerName);
/* 189 */     if (user != null)
/* 190 */       user.setOption(node, String.valueOf(value), world);
/*     */   }
/*     */ 
/*     */   public void setPlayerInfoBoolean(String world, String playerName, String node, boolean value)
/*     */   {
/* 196 */     PermissionUser user = getUser(playerName);
/* 197 */     if (user != null)
/* 198 */       user.setOption(node, String.valueOf(value), world);
/*     */   }
/*     */ 
/*     */   public void setPlayerInfoString(String world, String playerName, String node, String value)
/*     */   {
/* 204 */     PermissionUser user = getUser(playerName);
/* 205 */     if (user != null)
/* 206 */       user.setOption(node, String.valueOf(value), world);
/*     */   }
/*     */ 
/*     */   public int getGroupInfoInteger(String world, String groupName, String node, int defaultValue)
/*     */   {
/* 212 */     PermissionGroup group = PermissionsEx.getPermissionManager().getGroup(groupName);
/* 213 */     if (group == null) {
/* 214 */       return defaultValue;
/*     */     }
/* 216 */     return group.getOptionInteger(node, world, defaultValue);
/*     */   }
/*     */ 
/*     */   public void setGroupInfoInteger(String world, String groupName, String node, int value)
/*     */   {
/* 222 */     PermissionGroup group = PermissionsEx.getPermissionManager().getGroup(groupName);
/* 223 */     if (group == null) {
/* 224 */       return;
/*     */     }
/* 226 */     group.setOption(node, world, String.valueOf(value));
/*     */   }
/*     */ 
/*     */   public double getGroupInfoDouble(String world, String groupName, String node, double defaultValue)
/*     */   {
/* 232 */     PermissionGroup group = PermissionsEx.getPermissionManager().getGroup(groupName);
/* 233 */     if (group == null) {
/* 234 */       return defaultValue;
/*     */     }
/* 236 */     return group.getOptionDouble(node, world, defaultValue);
/*     */   }
/*     */ 
/*     */   public void setGroupInfoDouble(String world, String groupName, String node, double value)
/*     */   {
/* 242 */     PermissionGroup group = PermissionsEx.getPermissionManager().getGroup(groupName);
/* 243 */     if (group == null) {
/* 244 */       return;
/*     */     }
/* 246 */     group.setOption(node, world, String.valueOf(value));
/*     */   }
/*     */ 
/*     */   public boolean getGroupInfoBoolean(String world, String groupName, String node, boolean defaultValue)
/*     */   {
/* 252 */     PermissionGroup group = PermissionsEx.getPermissionManager().getGroup(groupName);
/* 253 */     if (group == null) {
/* 254 */       return defaultValue;
/*     */     }
/* 256 */     return group.getOptionBoolean(node, world, defaultValue);
/*     */   }
/*     */ 
/*     */   public void setGroupInfoBoolean(String world, String groupName, String node, boolean value)
/*     */   {
/* 262 */     PermissionGroup group = PermissionsEx.getPermissionManager().getGroup(groupName);
/* 263 */     if (group == null) {
/* 264 */       return;
/*     */     }
/* 266 */     group.setOption(node, world, String.valueOf(value));
/*     */   }
/*     */ 
/*     */   public String getGroupInfoString(String world, String groupName, String node, String defaultValue)
/*     */   {
/* 272 */     PermissionGroup group = PermissionsEx.getPermissionManager().getGroup(groupName);
/* 273 */     if (group == null) {
/* 274 */       return defaultValue;
/*     */     }
/* 276 */     return group.getOption(node, world, defaultValue);
/*     */   }
/*     */ 
/*     */   public void setGroupInfoString(String world, String groupName, String node, String value)
/*     */   {
/* 282 */     PermissionGroup group = PermissionsEx.getPermissionManager().getGroup(groupName);
/* 283 */     if (group == null) {
/* 284 */       return;
/*     */     }
/* 286 */     group.setOption(node, world, value);
/*     */   }
/*     */ 
/*     */   public String getPlayerPrefix(String world, OfflinePlayer op)
/*     */   {
/* 291 */     PermissionUser user = getUser(op);
/* 292 */     if (user != null) {
/* 293 */       return user.getPrefix(world);
/*     */     }
/* 295 */     return null;
/*     */   }
/*     */ 
/*     */   public String getPlayerSuffix(String world, OfflinePlayer op)
/*     */   {
/* 300 */     PermissionUser user = getUser(op);
/* 301 */     if (user != null) {
/* 302 */       return user.getSuffix(world);
/*     */     }
/* 304 */     return null;
/*     */   }
/*     */ 
/*     */   public void setPlayerSuffix(String world, OfflinePlayer player, String suffix)
/*     */   {
/* 309 */     PermissionUser user = getUser(player);
/* 310 */     if (user != null)
/* 311 */       user.setSuffix(suffix, world);
/*     */   }
/*     */ 
/*     */   public void setPlayerPrefix(String world, OfflinePlayer player, String prefix)
/*     */   {
/* 316 */     PermissionUser user = getUser(player);
/* 317 */     if (user != null)
/* 318 */       user.setPrefix(prefix, world);
/*     */   }
/*     */ 
/*     */   public String getPlayerPrefix(String world, String playerName)
/*     */   {
/* 324 */     PermissionUser user = getUser(playerName);
/* 325 */     if (user != null) {
/* 326 */       return user.getPrefix(world);
/*     */     }
/* 328 */     return null;
/*     */   }
/*     */ 
/*     */   public String getPlayerSuffix(String world, String playerName)
/*     */   {
/* 334 */     PermissionUser user = getUser(playerName);
/* 335 */     if (user != null) {
/* 336 */       return user.getSuffix(world);
/*     */     }
/* 338 */     return null;
/*     */   }
/*     */ 
/*     */   public void setPlayerSuffix(String world, String player, String suffix)
/*     */   {
/* 344 */     PermissionUser user = getUser(player);
/* 345 */     if (user != null)
/* 346 */       user.setSuffix(suffix, world);
/*     */   }
/*     */ 
/*     */   public void setPlayerPrefix(String world, String player, String prefix)
/*     */   {
/* 352 */     PermissionUser user = getUser(player);
/* 353 */     if (user != null)
/* 354 */       user.setPrefix(prefix, world);
/*     */   }
/*     */ 
/*     */   public String getGroupPrefix(String world, String group)
/*     */   {
/* 360 */     PermissionGroup pGroup = PermissionsEx.getPermissionManager().getGroup(group);
/* 361 */     if (group != null) {
/* 362 */       return pGroup.getPrefix(world);
/*     */     }
/* 364 */     return null;
/*     */   }
/*     */ 
/*     */   public void setGroupPrefix(String world, String group, String prefix)
/*     */   {
/* 370 */     PermissionGroup pGroup = PermissionsEx.getPermissionManager().getGroup(group);
/* 371 */     if (group != null)
/* 372 */       pGroup.setPrefix(prefix, world);
/*     */   }
/*     */ 
/*     */   public String getGroupSuffix(String world, String group)
/*     */   {
/* 379 */     PermissionGroup pGroup = PermissionsEx.getPermissionManager().getGroup(group);
/* 380 */     if (group != null) {
/* 381 */       return pGroup.getSuffix(world);
/*     */     }
/* 383 */     return null;
/*     */   }
/*     */ 
/*     */   public void setGroupSuffix(String world, String group, String suffix)
/*     */   {
/* 389 */     PermissionGroup pGroup = PermissionsEx.getPermissionManager().getGroup(group);
/* 390 */     if (group != null)
/* 391 */       pGroup.setSuffix(suffix, world);
/*     */   }
/*     */ 
/*     */   public class PermissionServerListener
/*     */     implements Listener
/*     */   {
/*  62 */     Chat_PermissionsEx chat = null;
/*     */ 
/*     */     public PermissionServerListener(Chat_PermissionsEx chat) {
/*  65 */       this.chat = chat;
/*     */     }
/*     */ 
/*     */     @EventHandler(priority=EventPriority.MONITOR)
/*     */     public void onPluginEnable(PluginEnableEvent event) {
/*  70 */       if (this.chat.chat == null) {
/*  71 */         Plugin perms = event.getPlugin();
/*     */ 
/*  73 */         if ((perms.getDescription().getName().equals("PermissionsEx")) && 
/*  74 */           (perms.isEnabled())) {
/*  75 */           this.chat.chat = ((PermissionsEx)perms);
/*  76 */           Chat_PermissionsEx.log.info(String.format("[%s][Chat] %s hooked.", new Object[] { Chat_PermissionsEx.this.plugin.getDescription().getName(), "PermissionsEx_Chat" }));
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/*     */     @EventHandler(priority=EventPriority.MONITOR)
/*     */     public void onPluginDisable(PluginDisableEvent event)
/*     */     {
/*  84 */       if ((this.chat.chat != null) && 
/*  85 */         (event.getPlugin().getDescription().getName().equals("PermissionsEx"))) {
/*  86 */         this.chat.chat = null;
/*  87 */         Chat_PermissionsEx.log.info(String.format("[%s][Chat] %s un-hooked.", new Object[] { Chat_PermissionsEx.this.plugin.getDescription().getName(), "PermissionsEx_Chat" }));
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\Vault.jar
 * Qualified Name:     net.milkbowl.vault.chat.plugins.Chat_PermissionsEx
 * JD-Core Version:    0.6.2
 */