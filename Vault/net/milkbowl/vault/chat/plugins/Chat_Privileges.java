/*     */ package net.milkbowl.vault.chat.plugins;
/*     */ 
/*     */ import java.util.logging.Logger;
/*     */ import net.krinsoft.privileges.Privileges;
/*     */ import net.milkbowl.vault.chat.Chat;
/*     */ import net.milkbowl.vault.permission.Permission;
/*     */ import org.bukkit.Bukkit;
/*     */ import org.bukkit.Server;
/*     */ import org.bukkit.configuration.ConfigurationSection;
/*     */ import org.bukkit.event.EventHandler;
/*     */ import org.bukkit.event.EventPriority;
/*     */ import org.bukkit.event.Listener;
/*     */ import org.bukkit.event.server.PluginDisableEvent;
/*     */ import org.bukkit.event.server.PluginEnableEvent;
/*     */ import org.bukkit.plugin.Plugin;
/*     */ import org.bukkit.plugin.PluginDescriptionFile;
/*     */ import org.bukkit.plugin.PluginManager;
/*     */ 
/*     */ public class Chat_Privileges extends Chat
/*     */ {
/*     */   private static final String FRIENDLY_NAME = "Privileges - Chat";
/*     */   private static final String PLUGIN_NAME = "Privileges";
/*     */   private static final String CHAT_PREFIX_KEY = "prefix";
/*     */   private static final String CHAT_SUFFIX_KEY = "suffix";
/*     */   private Privileges privs;
/*     */   private final Plugin plugin;
/*     */ 
/*     */   public Chat_Privileges(Plugin plugin, Permission perms)
/*     */   {
/*  39 */     super(perms);
/*  40 */     this.plugin = plugin;
/*  41 */     Bukkit.getServer().getPluginManager().registerEvents(new PermissionServerListener(), plugin);
/*     */ 
/*  43 */     if (this.privs == null) {
/*  44 */       Plugin privsPlugin = plugin.getServer().getPluginManager().getPlugin("Privileges");
/*  45 */       if ((privsPlugin != null) && (privsPlugin.isEnabled())) {
/*  46 */         this.privs = ((Privileges)privsPlugin);
/*  47 */         plugin.getLogger().info(String.format("[%s][Chat] %s hooked.", new Object[] { plugin.getDescription().getName(), "Privileges - Chat" }));
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public String getName()
/*     */   {
/*  54 */     return "Privileges - Chat";
/*     */   }
/*     */ 
/*     */   public boolean isEnabled()
/*     */   {
/*  59 */     return (this.privs != null) && (this.privs.isEnabled());
/*     */   }
/*     */ 
/*     */   private String getPlayerOrGroupInfoString(String world, String player, String key, String defaultValue) {
/*  63 */     String value = getPlayerInfoString(world, player, key, null);
/*  64 */     if (value != null) return value;
/*     */ 
/*  66 */     value = getGroupInfoString(world, getPrimaryGroup(world, player), key, null);
/*  67 */     if (value != null) return value;
/*     */ 
/*  69 */     return defaultValue;
/*     */   }
/*     */ 
/*     */   private void worldCheck(String world) {
/*  73 */     if ((world != null) && (!world.isEmpty()))
/*  74 */       throw new UnsupportedOperationException("Privileges does not support multiple worlds for player/group metadata.");
/*     */   }
/*     */ 
/*     */   public String getPlayerPrefix(String world, String player)
/*     */   {
/*  80 */     return getPlayerOrGroupInfoString(world, player, "prefix", null);
/*     */   }
/*     */ 
/*     */   public void setPlayerPrefix(String world, String player, String prefix)
/*     */   {
/*  85 */     setPlayerInfoString(world, player, "prefix", prefix);
/*     */   }
/*     */ 
/*     */   public String getPlayerSuffix(String world, String player)
/*     */   {
/*  90 */     return getPlayerOrGroupInfoString(world, player, "suffix", null);
/*     */   }
/*     */ 
/*     */   public void setPlayerSuffix(String world, String player, String suffix)
/*     */   {
/*  95 */     setPlayerInfoString(world, player, "suffix", suffix);
/*     */   }
/*     */ 
/*     */   public String getGroupPrefix(String world, String group)
/*     */   {
/* 100 */     return getGroupInfoString(world, group, "prefix", null);
/*     */   }
/*     */ 
/*     */   public void setGroupPrefix(String world, String group, String prefix)
/*     */   {
/* 105 */     setGroupInfoString(world, group, "prefix", prefix);
/*     */   }
/*     */ 
/*     */   public String getGroupSuffix(String world, String group)
/*     */   {
/* 110 */     return getGroupInfoString(world, group, "suffix", null);
/*     */   }
/*     */ 
/*     */   public void setGroupSuffix(String world, String group, String suffix)
/*     */   {
/* 115 */     setGroupInfoString(world, group, "suffix", suffix);
/*     */   }
/*     */ 
/*     */   public int getPlayerInfoInteger(String world, String player, String node, int defaultValue)
/*     */   {
/* 120 */     return this.privs.getUserNode(player).getInt(node, defaultValue);
/*     */   }
/*     */ 
/*     */   public void setPlayerInfoInteger(String world, String player, String node, int value)
/*     */   {
/* 125 */     worldCheck(world);
/* 126 */     this.privs.getUserNode(player).set(node, Integer.valueOf(value));
/*     */   }
/*     */ 
/*     */   public int getGroupInfoInteger(String world, String group, String node, int defaultValue)
/*     */   {
/* 131 */     return this.privs.getGroupNode(group).getInt(node, defaultValue);
/*     */   }
/*     */ 
/*     */   public void setGroupInfoInteger(String world, String group, String node, int value)
/*     */   {
/* 136 */     worldCheck(world);
/* 137 */     this.privs.getGroupNode(group).set(node, Integer.valueOf(value));
/*     */   }
/*     */ 
/*     */   public double getPlayerInfoDouble(String world, String player, String node, double defaultValue)
/*     */   {
/* 142 */     return this.privs.getUserNode(player).getDouble(node, defaultValue);
/*     */   }
/*     */ 
/*     */   public void setPlayerInfoDouble(String world, String player, String node, double value)
/*     */   {
/* 147 */     worldCheck(world);
/* 148 */     this.privs.getUserNode(player).set(node, Double.valueOf(value));
/*     */   }
/*     */ 
/*     */   public double getGroupInfoDouble(String world, String group, String node, double defaultValue)
/*     */   {
/* 153 */     return this.privs.getGroupNode(group).getDouble(node, defaultValue);
/*     */   }
/*     */ 
/*     */   public void setGroupInfoDouble(String world, String group, String node, double value)
/*     */   {
/* 158 */     worldCheck(world);
/* 159 */     this.privs.getGroupNode(group).set(node, Double.valueOf(value));
/*     */   }
/*     */ 
/*     */   public boolean getPlayerInfoBoolean(String world, String player, String node, boolean defaultValue)
/*     */   {
/* 164 */     return this.privs.getUserNode(player).getBoolean(node, defaultValue);
/*     */   }
/*     */ 
/*     */   public void setPlayerInfoBoolean(String world, String player, String node, boolean value)
/*     */   {
/* 169 */     worldCheck(world);
/* 170 */     this.privs.getUserNode(player).set(node, Boolean.valueOf(value));
/*     */   }
/*     */ 
/*     */   public boolean getGroupInfoBoolean(String world, String group, String node, boolean defaultValue)
/*     */   {
/* 175 */     return this.privs.getGroupNode(group).getBoolean(node, defaultValue);
/*     */   }
/*     */ 
/*     */   public void setGroupInfoBoolean(String world, String group, String node, boolean value)
/*     */   {
/* 180 */     worldCheck(world);
/* 181 */     this.privs.getGroupNode(group).set(node, Boolean.valueOf(value));
/*     */   }
/*     */ 
/*     */   public String getPlayerInfoString(String world, String player, String node, String defaultValue)
/*     */   {
/* 186 */     return this.privs.getUserNode(player).getString(node, defaultValue);
/*     */   }
/*     */ 
/*     */   public void setPlayerInfoString(String world, String player, String node, String value)
/*     */   {
/* 191 */     worldCheck(world);
/* 192 */     this.privs.getUserNode(player).set(node, value);
/*     */   }
/*     */ 
/*     */   public String getGroupInfoString(String world, String group, String node, String defaultValue)
/*     */   {
/* 197 */     return this.privs.getGroupNode(group).getString(node, defaultValue);
/*     */   }
/*     */ 
/*     */   public void setGroupInfoString(String world, String group, String node, String value)
/*     */   {
/* 202 */     worldCheck(world);
/* 203 */     this.privs.getGroupNode(group).set(node, value);
/*     */   }
/*     */   public class PermissionServerListener implements Listener {
/*     */     public PermissionServerListener() {
/*     */     }
/*     */     @EventHandler(priority=EventPriority.MONITOR)
/*     */     public void onPluginEnable(PluginEnableEvent event) {
/* 210 */       if (Chat_Privileges.this.privs == null) {
/* 211 */         Plugin permChat = event.getPlugin();
/* 212 */         if (("Privileges".equals(permChat.getDescription().getName())) && 
/* 213 */           (permChat.isEnabled())) {
/* 214 */           Chat_Privileges.this.privs = ((Privileges)permChat);
/* 215 */           Chat_Privileges.this.plugin.getLogger().info(String.format("[Chat] %s hooked.", new Object[] { "Privileges - Chat" }));
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/*     */     @EventHandler(priority=EventPriority.MONITOR)
/*     */     public void onPluginDisable(PluginDisableEvent event)
/*     */     {
/* 223 */       if ((Chat_Privileges.this.privs != null) && 
/* 224 */         ("Privileges".equals(event.getPlugin().getDescription().getName()))) {
/* 225 */         Chat_Privileges.this.privs = null;
/* 226 */         Chat_Privileges.this.plugin.getLogger().info(String.format("[Chat] %s un-hooked.", new Object[] { "Privileges - Chat" }));
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\Vault.jar
 * Qualified Name:     net.milkbowl.vault.chat.plugins.Chat_Privileges
 * JD-Core Version:    0.6.2
 */