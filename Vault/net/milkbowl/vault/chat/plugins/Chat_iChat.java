/*     */ package net.milkbowl.vault.chat.plugins;
/*     */ 
/*     */ import java.util.logging.Logger;
/*     */ import net.TheDgtl.iChat.iChat;
/*     */ import net.TheDgtl.iChat.iChatAPI;
/*     */ import net.milkbowl.vault.chat.Chat;
/*     */ import net.milkbowl.vault.permission.Permission;
/*     */ import org.bukkit.Bukkit;
/*     */ import org.bukkit.Server;
/*     */ import org.bukkit.World;
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
/*     */ public class Chat_iChat extends Chat
/*     */ {
/*  36 */   private static final Logger log = Logger.getLogger("Minecraft");
/*  37 */   private final String name = "iChat";
/*  38 */   private Plugin plugin = null;
/*  39 */   private iChatAPI iChat = null;
/*     */ 
/*     */   public Chat_iChat(Plugin plugin, Permission perms) {
/*  42 */     super(perms);
/*  43 */     this.plugin = plugin;
/*     */ 
/*  45 */     Bukkit.getServer().getPluginManager().registerEvents(new PermissionServerListener(this), plugin);
/*     */ 
/*  48 */     if (this.iChat == null) {
/*  49 */       Plugin chat = plugin.getServer().getPluginManager().getPlugin("iChat");
/*  50 */       if (chat != null) {
/*  51 */         this.iChat = ((iChat)chat).API;
/*  52 */         log.info(String.format("[%s][Chat] %s hooked.", new Object[] { plugin.getDescription().getName(), "iChat" }));
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public String getName()
/*     */   {
/*  88 */     return "iChat";
/*     */   }
/*     */ 
/*     */   public boolean isEnabled()
/*     */   {
/*  93 */     return this.iChat != null;
/*     */   }
/*     */ 
/*     */   public String getPlayerPrefix(String world, String player)
/*     */   {
/*  98 */     Player p = this.plugin.getServer().getPlayer(player);
/*  99 */     if (p == null) {
/* 100 */       throw new UnsupportedOperationException("iChat does not support offline player info nodes!");
/*     */     }
/*     */ 
/* 103 */     if (!p.getWorld().getName().equals(world)) {
/* 104 */       return null;
/*     */     }
/*     */ 
/* 107 */     return this.iChat.getPrefix(p);
/*     */   }
/*     */ 
/*     */   public void setPlayerPrefix(String world, String player, String prefix)
/*     */   {
/* 112 */     throw new UnsupportedOperationException("iChat does not support mutable info nodes!");
/*     */   }
/*     */ 
/*     */   public String getPlayerSuffix(String world, String player)
/*     */   {
/* 117 */     Player p = this.plugin.getServer().getPlayer(player);
/* 118 */     if (p == null) {
/* 119 */       throw new UnsupportedOperationException("iChat does not support offline player info nodes!");
/*     */     }
/*     */ 
/* 122 */     if (!p.getWorld().getName().equals(world)) {
/* 123 */       return null;
/*     */     }
/*     */ 
/* 126 */     return this.iChat.getSuffix(p);
/*     */   }
/*     */ 
/*     */   public void setPlayerSuffix(String world, String player, String suffix)
/*     */   {
/* 131 */     throw new UnsupportedOperationException("iChat does not support mutable info nodes!");
/*     */   }
/*     */ 
/*     */   public String getGroupPrefix(String world, String group)
/*     */   {
/* 136 */     throw new UnsupportedOperationException("iChat does not support group info nodes!");
/*     */   }
/*     */ 
/*     */   public void setGroupPrefix(String world, String group, String prefix)
/*     */   {
/* 142 */     throw new UnsupportedOperationException("iChat does not support mutable info nodes!");
/*     */   }
/*     */ 
/*     */   public String getGroupSuffix(String world, String group)
/*     */   {
/* 147 */     throw new UnsupportedOperationException("iChat does not support group info nodes!");
/*     */   }
/*     */ 
/*     */   public void setGroupSuffix(String world, String group, String suffix)
/*     */   {
/* 152 */     throw new UnsupportedOperationException("iChat does not support mutable info nodes!");
/*     */   }
/*     */ 
/*     */   public int getPlayerInfoInteger(String world, String player, String node, int defaultValue)
/*     */   {
/* 157 */     String val = getPlayerInfoString(world, player, node, null);
/* 158 */     if (val == null) {
/* 159 */       return defaultValue;
/*     */     }
/*     */ 
/* 162 */     Integer i = Integer.valueOf(defaultValue);
/*     */     try {
/* 164 */       i = Integer.valueOf(val);
/* 165 */       return i.intValue(); } catch (NumberFormatException e) {
/*     */     }
/* 167 */     return defaultValue;
/*     */   }
/*     */ 
/*     */   public void setPlayerInfoInteger(String world, String player, String node, int value)
/*     */   {
/* 173 */     throw new UnsupportedOperationException("iChat does not support mutable info nodes!");
/*     */   }
/*     */ 
/*     */   public int getGroupInfoInteger(String world, String group, String node, int defaultValue)
/*     */   {
/* 178 */     throw new UnsupportedOperationException("iChat does not support group info nodes!");
/*     */   }
/*     */ 
/*     */   public void setGroupInfoInteger(String world, String group, String node, int value)
/*     */   {
/* 183 */     throw new UnsupportedOperationException("iChat does not support mutable info nodes!");
/*     */   }
/*     */ 
/*     */   public double getPlayerInfoDouble(String world, String player, String node, double defaultValue)
/*     */   {
/* 188 */     String val = getPlayerInfoString(world, player, node, null);
/* 189 */     if (val == null) {
/* 190 */       return defaultValue;
/*     */     }
/*     */ 
/* 193 */     double d = defaultValue;
/*     */     try {
/* 195 */       return Double.valueOf(val).doubleValue();
/*     */     } catch (NumberFormatException e) {
/*     */     }
/* 198 */     return defaultValue;
/*     */   }
/*     */ 
/*     */   public void setPlayerInfoDouble(String world, String player, String node, double value)
/*     */   {
/* 204 */     throw new UnsupportedOperationException("iChat does not support mutable info nodes!");
/*     */   }
/*     */ 
/*     */   public double getGroupInfoDouble(String world, String group, String node, double defaultValue)
/*     */   {
/* 209 */     throw new UnsupportedOperationException("iChat does not support group info nodes!");
/*     */   }
/*     */ 
/*     */   public void setGroupInfoDouble(String world, String group, String node, double value)
/*     */   {
/* 214 */     throw new UnsupportedOperationException("iChat does not support mutable info nodes!");
/*     */   }
/*     */ 
/*     */   public boolean getPlayerInfoBoolean(String world, String player, String node, boolean defaultValue)
/*     */   {
/* 219 */     String val = getPlayerInfoString(world, player, node, null);
/* 220 */     if (val == null) {
/* 221 */       return defaultValue;
/*     */     }
/* 223 */     Boolean v = Boolean.valueOf(val);
/* 224 */     return v != null ? v.booleanValue() : defaultValue;
/*     */   }
/*     */ 
/*     */   public void setPlayerInfoBoolean(String world, String player, String node, boolean value)
/*     */   {
/* 230 */     throw new UnsupportedOperationException("iChat does not support mutable info nodes!");
/*     */   }
/*     */ 
/*     */   public boolean getGroupInfoBoolean(String world, String group, String node, boolean defaultValue)
/*     */   {
/* 235 */     throw new UnsupportedOperationException("iChat does not support group info nodes!");
/*     */   }
/*     */ 
/*     */   public void setGroupInfoBoolean(String world, String group, String node, boolean value)
/*     */   {
/* 240 */     throw new UnsupportedOperationException("iChat does not support mutable info nodes!");
/*     */   }
/*     */ 
/*     */   public String getPlayerInfoString(String world, String player, String node, String defaultValue)
/*     */   {
/* 245 */     Player p = this.plugin.getServer().getPlayer(player);
/* 246 */     if (p == null) {
/* 247 */       throw new UnsupportedOperationException("iChat does not support offline player info nodes!");
/*     */     }
/*     */ 
/* 250 */     if (!p.getWorld().getName().equals(world)) {
/* 251 */       return null;
/*     */     }
/*     */ 
/* 254 */     String val = this.iChat.getInfo(p, node);
/*     */ 
/* 256 */     return val != null ? val : defaultValue;
/*     */   }
/*     */ 
/*     */   public void setPlayerInfoString(String world, String player, String node, String value)
/*     */   {
/* 261 */     throw new UnsupportedOperationException("iChat does not support mutable info nodes!");
/*     */   }
/*     */ 
/*     */   public String getGroupInfoString(String world, String group, String node, String defaultValue)
/*     */   {
/* 266 */     throw new UnsupportedOperationException("iChat does not support group info nodes!");
/*     */   }
/*     */ 
/*     */   public void setGroupInfoString(String world, String group, String node, String value)
/*     */   {
/* 271 */     throw new UnsupportedOperationException("iChat does not support mutable info nodes!");
/*     */   }
/*     */ 
/*     */   public class PermissionServerListener
/*     */     implements Listener
/*     */   {
/*  58 */     Chat_iChat chat = null;
/*     */ 
/*     */     public PermissionServerListener(Chat_iChat chat) {
/*  61 */       this.chat = chat;
/*     */     }
/*     */ 
/*     */     @EventHandler(priority=EventPriority.MONITOR)
/*     */     public void onPluginEnable(PluginEnableEvent event) {
/*  66 */       if (this.chat.iChat == null) {
/*  67 */         Plugin chat = event.getPlugin();
/*  68 */         if (chat.getDescription().getName().equals("iChat")) {
/*  69 */           this.chat.iChat = ((iChat)chat).API;
/*  70 */           Chat_iChat.log.info(String.format("[%s][Chat] %s hooked.", new Object[] { Chat_iChat.this.plugin.getDescription().getName(), "iChat" }));
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/*     */     @EventHandler(priority=EventPriority.MONITOR)
/*     */     public void onPluginDisable(PluginDisableEvent event) {
/*  77 */       if ((this.chat.iChat != null) && 
/*  78 */         (event.getPlugin().getDescription().getName().equals("iChat"))) {
/*  79 */         this.chat.iChat = null;
/*  80 */         Chat_iChat.log.info(String.format("[%s][Chat] %s un-hooked.", new Object[] { Chat_iChat.this.plugin.getDescription().getName(), "iChat" }));
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\Vault.jar
 * Qualified Name:     net.milkbowl.vault.chat.plugins.Chat_iChat
 * JD-Core Version:    0.6.2
 */