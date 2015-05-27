/*     */ package net.milkbowl.vault.chat.plugins;
/*     */ 
/*     */ import java.util.logging.Logger;
/*     */ import net.D3GN.MiracleM4n.mChat.mChat;
/*     */ import net.D3GN.MiracleM4n.mChat.mChatAPI;
/*     */ import net.milkbowl.vault.chat.Chat;
/*     */ import net.milkbowl.vault.permission.Permission;
/*     */ import org.bukkit.Bukkit;
/*     */ import org.bukkit.Server;
/*     */ import org.bukkit.entity.Player;
/*     */ import org.bukkit.event.EventHandler;
/*     */ import org.bukkit.event.EventPriority;
/*     */ import org.bukkit.event.Listener;
/*     */ import org.bukkit.event.server.PluginDisableEvent;
/*     */ import org.bukkit.event.server.PluginEnableEvent;
/*     */ import org.bukkit.plugin.Plugin;
/*     */ import org.bukkit.plugin.PluginDescriptionFile;
/*     */ import org.bukkit.plugin.PluginManager;
/*     */ 
/*     */ public class Chat_mChat extends Chat
/*     */ {
/*  34 */   private static final Logger log = Logger.getLogger("Minecraft");
/*  35 */   private final String name = "mChat";
/*  36 */   private Plugin plugin = null;
/*  37 */   private mChatAPI mChat = null;
/*     */ 
/*     */   public Chat_mChat(Plugin plugin, Permission perms) {
/*  40 */     super(perms);
/*  41 */     this.plugin = plugin;
/*     */ 
/*  43 */     Bukkit.getServer().getPluginManager().registerEvents(new PermissionServerListener(this), plugin);
/*     */ 
/*  46 */     if (this.mChat == null) {
/*  47 */       Plugin chat = plugin.getServer().getPluginManager().getPlugin("mChat");
/*  48 */       if (chat != null) {
/*  49 */         this.mChat = mChat.API;
/*  50 */         log.info(String.format("[%s][Chat] %s hooked.", new Object[] { plugin.getDescription().getName(), "mChat" }));
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public String getName()
/*     */   {
/*  86 */     return "mChat";
/*     */   }
/*     */ 
/*     */   public boolean isEnabled()
/*     */   {
/*  91 */     return this.mChat != null;
/*     */   }
/*     */ 
/*     */   public String getPlayerPrefix(String world, String player)
/*     */   {
/*  96 */     Player p = Bukkit.getServer().getPlayer(player);
/*  97 */     if (p == null) {
/*  98 */       throw new UnsupportedOperationException("mChat does not support offline player prefixes");
/*     */     }
/* 100 */     return this.mChat.getPrefix(p);
/*     */   }
/*     */ 
/*     */   public void setPlayerPrefix(String world, String player, String prefix)
/*     */   {
/* 105 */     throw new UnsupportedOperationException("mChat does not support setting info nodes");
/*     */   }
/*     */ 
/*     */   public String getPlayerSuffix(String world, String player)
/*     */   {
/* 110 */     Player p = Bukkit.getServer().getPlayer(player);
/* 111 */     if (p == null) {
/* 112 */       throw new UnsupportedOperationException("mChat does not support offline player prefixes");
/*     */     }
/* 114 */     return this.mChat.getSuffix(p);
/*     */   }
/*     */ 
/*     */   public void setPlayerSuffix(String world, String player, String suffix)
/*     */   {
/* 119 */     throw new UnsupportedOperationException("mChat does not support setting info nodes");
/*     */   }
/*     */ 
/*     */   public String getGroupPrefix(String world, String group)
/*     */   {
/* 124 */     throw new UnsupportedOperationException("mChat does not support group info nodes");
/*     */   }
/*     */ 
/*     */   public void setGroupPrefix(String world, String group, String prefix)
/*     */   {
/* 129 */     throw new UnsupportedOperationException("mChat does not support group info nodes");
/*     */   }
/*     */ 
/*     */   public String getGroupSuffix(String world, String group)
/*     */   {
/* 134 */     throw new UnsupportedOperationException("mChat does not support group info nodes");
/*     */   }
/*     */ 
/*     */   public void setGroupSuffix(String world, String group, String suffix)
/*     */   {
/* 139 */     throw new UnsupportedOperationException("mChat does not support group info nodes");
/*     */   }
/*     */ 
/*     */   public int getPlayerInfoInteger(String world, String player, String node, int defaultValue)
/*     */   {
/* 144 */     String s = getPlayerInfoString(world, player, node, null);
/* 145 */     if (s == null) {
/* 146 */       return defaultValue;
/*     */     }
/*     */     try
/*     */     {
/* 150 */       return Integer.valueOf(s).intValue(); } catch (NumberFormatException e) {
/*     */     }
/* 152 */     return defaultValue;
/*     */   }
/*     */ 
/*     */   public void setPlayerInfoInteger(String world, String player, String node, int value)
/*     */   {
/* 158 */     throw new UnsupportedOperationException("mChat does not support setting info nodes");
/*     */   }
/*     */ 
/*     */   public int getGroupInfoInteger(String world, String group, String node, int defaultValue)
/*     */   {
/* 163 */     throw new UnsupportedOperationException("mChat does not support group info nodes");
/*     */   }
/*     */ 
/*     */   public void setGroupInfoInteger(String world, String group, String node, int value)
/*     */   {
/* 168 */     throw new UnsupportedOperationException("mChat does not support group info nodes");
/*     */   }
/*     */ 
/*     */   public double getPlayerInfoDouble(String world, String player, String node, double defaultValue)
/*     */   {
/* 173 */     String s = getPlayerInfoString(world, player, node, null);
/* 174 */     if (s == null) {
/* 175 */       return defaultValue;
/*     */     }
/*     */     try
/*     */     {
/* 179 */       return Double.valueOf(s).doubleValue(); } catch (NumberFormatException e) {
/*     */     }
/* 181 */     return defaultValue;
/*     */   }
/*     */ 
/*     */   public void setPlayerInfoDouble(String world, String player, String node, double value)
/*     */   {
/* 187 */     throw new UnsupportedOperationException("mChat does not support setting info nodes");
/*     */   }
/*     */ 
/*     */   public double getGroupInfoDouble(String world, String group, String node, double defaultValue)
/*     */   {
/* 192 */     throw new UnsupportedOperationException("mChat does not support group info nodes");
/*     */   }
/*     */ 
/*     */   public void setGroupInfoDouble(String world, String group, String node, double value)
/*     */   {
/* 197 */     throw new UnsupportedOperationException("mChat does not support group info nodes");
/*     */   }
/*     */ 
/*     */   public boolean getPlayerInfoBoolean(String world, String player, String node, boolean defaultValue)
/*     */   {
/* 202 */     String s = getPlayerInfoString(world, player, node, null);
/* 203 */     if (s == null) {
/* 204 */       return defaultValue;
/*     */     }
/* 206 */     Boolean val = Boolean.valueOf(s);
/* 207 */     return val != null ? val.booleanValue() : defaultValue;
/*     */   }
/*     */ 
/*     */   public void setPlayerInfoBoolean(String world, String player, String node, boolean value)
/*     */   {
/* 213 */     throw new UnsupportedOperationException("mChat does not support setting info nodes");
/*     */   }
/*     */ 
/*     */   public boolean getGroupInfoBoolean(String world, String group, String node, boolean defaultValue)
/*     */   {
/* 218 */     throw new UnsupportedOperationException("mChat does not support group info nodes");
/*     */   }
/*     */ 
/*     */   public void setGroupInfoBoolean(String world, String group, String node, boolean value)
/*     */   {
/* 223 */     throw new UnsupportedOperationException("mChat does not support group info nodes");
/*     */   }
/*     */ 
/*     */   public String getPlayerInfoString(String world, String player, String node, String defaultValue)
/*     */   {
/* 228 */     Player p = Bukkit.getServer().getPlayer(player);
/* 229 */     if (p == null) {
/* 230 */       throw new UnsupportedOperationException("mChat does not support offline player prefixes");
/*     */     }
/* 232 */     String s = this.mChat.getInfo(p, node);
/* 233 */     return s == null ? defaultValue : s;
/*     */   }
/*     */ 
/*     */   public void setPlayerInfoString(String world, String player, String node, String value)
/*     */   {
/* 238 */     throw new UnsupportedOperationException("mChat does not support setting info nodes");
/*     */   }
/*     */ 
/*     */   public String getGroupInfoString(String world, String group, String node, String defaultValue)
/*     */   {
/* 243 */     throw new UnsupportedOperationException("mChat does not support group info nodes");
/*     */   }
/*     */ 
/*     */   public void setGroupInfoString(String world, String group, String node, String value)
/*     */   {
/* 248 */     throw new UnsupportedOperationException("mChat does not support group info nodes");
/*     */   }
/*     */ 
/*     */   public class PermissionServerListener
/*     */     implements Listener
/*     */   {
/*  56 */     Chat_mChat chat = null;
/*     */ 
/*     */     public PermissionServerListener(Chat_mChat chat) {
/*  59 */       this.chat = chat;
/*     */     }
/*     */ 
/*     */     @EventHandler(priority=EventPriority.MONITOR)
/*     */     public void onPluginEnable(PluginEnableEvent event) {
/*  64 */       if (this.chat.mChat == null) {
/*  65 */         Plugin chat = event.getPlugin();
/*  66 */         if (chat.getDescription().getName().equals("mChat")) {
/*  67 */           this.chat.mChat = mChat.API;
/*  68 */           Chat_mChat.log.info(String.format("[%s][Chat] %s hooked.", new Object[] { Chat_mChat.this.plugin.getDescription().getName(), "mChat" }));
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/*     */     @EventHandler(priority=EventPriority.MONITOR)
/*     */     public void onPluginDisable(PluginDisableEvent event) {
/*  75 */       if ((this.chat.mChat != null) && 
/*  76 */         (event.getPlugin().getDescription().getName().equals("mChat"))) {
/*  77 */         this.chat.mChat = null;
/*  78 */         Chat_mChat.log.info(String.format("[%s][Chat] %s un-hooked.", new Object[] { Chat_mChat.this.plugin.getDescription().getName(), "mChat" }));
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\Vault.jar
 * Qualified Name:     net.milkbowl.vault.chat.plugins.Chat_mChat
 * JD-Core Version:    0.6.2
 */