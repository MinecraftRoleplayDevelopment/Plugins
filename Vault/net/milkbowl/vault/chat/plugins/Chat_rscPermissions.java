/*     */ package net.milkbowl.vault.chat.plugins;
/*     */ 
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
/*     */ import ru.simsonic.rscPermissions.MainPluginClass;
/*     */ import ru.simsonic.rscPermissions.rscpAPI;
/*     */ 
/*     */ public class Chat_rscPermissions extends Chat
/*     */ {
/*  35 */   private static final Logger log = Logger.getLogger("Minecraft");
/*     */   private final Plugin vault;
/*     */   private MainPluginClass rscp;
/*     */   private rscpAPI rscpAPI;
/*     */ 
/*     */   public Chat_rscPermissions(Plugin plugin, Permission perm)
/*     */   {
/*  41 */     super(perm);
/*  42 */     this.vault = plugin;
/*  43 */     Bukkit.getServer().getPluginManager().registerEvents(new ChatServerListener(this), this.vault);
/*  44 */     if (this.rscp == null) {
/*  45 */       Plugin perms = plugin.getServer().getPluginManager().getPlugin("rscPermissions");
/*  46 */       if ((perms != null) && (perms.isEnabled())) {
/*  47 */         this.rscp = ((MainPluginClass)perms);
/*  48 */         this.rscpAPI = this.rscp.API;
/*  49 */         plugin.getLogger().info(String.format("[%s][Chat] %s hooked.", new Object[] { plugin.getDescription().getName(), "rscPermissions" }));
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public String getName()
/*     */   {
/*  87 */     return "rscPermissions";
/*     */   }
/*     */ 
/*     */   public boolean isEnabled()
/*     */   {
/*  92 */     return (this.rscpAPI != null) && (this.rscpAPI.isEnabled());
/*     */   }
/*     */ 
/*     */   public String getPlayerPrefix(String world, String player)
/*     */   {
/*  97 */     return this.rscpAPI.getPlayerPrefix(world, player);
/*     */   }
/*     */ 
/*     */   public String getPlayerSuffix(String world, String player)
/*     */   {
/* 102 */     return this.rscpAPI.getPlayerSuffix(world, player);
/*     */   }
/*     */ 
/*     */   public String getGroupPrefix(String world, String group)
/*     */   {
/* 107 */     return this.rscpAPI.getGroupPrefix(world, group);
/*     */   }
/*     */ 
/*     */   public String getGroupSuffix(String world, String group)
/*     */   {
/* 112 */     return this.rscpAPI.getGroupSuffix(world, group);
/*     */   }
/*     */ 
/*     */   public void setPlayerPrefix(String world, String player, String prefix)
/*     */   {
/* 117 */     this.rscpAPI.setPlayerPrefix(world, player, prefix);
/*     */   }
/*     */ 
/*     */   public void setPlayerSuffix(String world, String player, String suffix)
/*     */   {
/* 122 */     this.rscpAPI.setPlayerSuffix(world, player, suffix);
/*     */   }
/*     */ 
/*     */   public void setGroupPrefix(String world, String group, String prefix)
/*     */   {
/* 127 */     this.rscpAPI.setGroupPrefix(world, group, prefix);
/*     */   }
/*     */ 
/*     */   public void setGroupSuffix(String world, String group, String suffix)
/*     */   {
/* 132 */     this.rscpAPI.setGroupSuffix(world, group, suffix);
/*     */   }
/*     */ 
/*     */   public int getPlayerInfoInteger(String world, String player, String node, int defaultValue)
/*     */   {
/* 137 */     throw new UnsupportedOperationException("rscPermissions does not support info nodes");
/*     */   }
/*     */ 
/*     */   public void setPlayerInfoInteger(String world, String player, String node, int defaultValue)
/*     */   {
/* 142 */     throw new UnsupportedOperationException("rscPermissions does not support info nodes");
/*     */   }
/*     */ 
/*     */   public int getGroupInfoInteger(String world, String group, String node, int defaultValue)
/*     */   {
/* 147 */     throw new UnsupportedOperationException("rscPermissions does not support info nodes");
/*     */   }
/*     */ 
/*     */   public void setGroupInfoInteger(String world, String group, String node, int defaultValue)
/*     */   {
/* 152 */     throw new UnsupportedOperationException("rscPermissions does not support info nodes");
/*     */   }
/*     */ 
/*     */   public double getPlayerInfoDouble(String world, String player, String node, double defaultValue)
/*     */   {
/* 157 */     throw new UnsupportedOperationException("rscPermissions does not support info nodes");
/*     */   }
/*     */ 
/*     */   public void setPlayerInfoDouble(String world, String player, String node, double defaultValue)
/*     */   {
/* 162 */     throw new UnsupportedOperationException("rscPermissions does not support info nodes");
/*     */   }
/*     */ 
/*     */   public double getGroupInfoDouble(String world, String group, String node, double defaultValue)
/*     */   {
/* 167 */     throw new UnsupportedOperationException("rscPermissions does not support info nodes");
/*     */   }
/*     */ 
/*     */   public void setGroupInfoDouble(String world, String group, String node, double defaultValue)
/*     */   {
/* 172 */     throw new UnsupportedOperationException("rscPermissions does not support info nodes");
/*     */   }
/*     */ 
/*     */   public boolean getPlayerInfoBoolean(String world, String player, String node, boolean defaultValue)
/*     */   {
/* 177 */     throw new UnsupportedOperationException("rscPermissions does not support info nodes");
/*     */   }
/*     */ 
/*     */   public void setPlayerInfoBoolean(String world, String player, String node, boolean defaultValue)
/*     */   {
/* 182 */     throw new UnsupportedOperationException("rscPermissions does not support info nodes");
/*     */   }
/*     */ 
/*     */   public boolean getGroupInfoBoolean(String world, String group, String node, boolean defaultValue)
/*     */   {
/* 187 */     throw new UnsupportedOperationException("rscPermissions does not support info nodes");
/*     */   }
/*     */ 
/*     */   public void setGroupInfoBoolean(String world, String group, String node, boolean defaultValue)
/*     */   {
/* 192 */     throw new UnsupportedOperationException("rscPermissions does not support info nodes");
/*     */   }
/*     */ 
/*     */   public String getPlayerInfoString(String world, String player, String node, String defaultValue)
/*     */   {
/* 197 */     throw new UnsupportedOperationException("rscPermissions does not support info nodes");
/*     */   }
/*     */ 
/*     */   public void setPlayerInfoString(String world, String player, String node, String defaultValue)
/*     */   {
/* 202 */     throw new UnsupportedOperationException("rscPermissions does not support info nodes");
/*     */   }
/*     */ 
/*     */   public String getGroupInfoString(String world, String group, String node, String defaultValue)
/*     */   {
/* 207 */     throw new UnsupportedOperationException("rscPermissions does not support info nodes");
/*     */   }
/*     */ 
/*     */   public void setGroupInfoString(String world, String group, String node, String defaultValue)
/*     */   {
/* 212 */     throw new UnsupportedOperationException("rscPermissions does not support info nodes");
/*     */   }
/*     */ 
/*     */   private class ChatServerListener
/*     */     implements Listener
/*     */   {
/*     */     private final Chat_rscPermissions bridge;
/*     */ 
/*     */     public ChatServerListener(Chat_rscPermissions bridge)
/*     */     {
/*  58 */       this.bridge = bridge;
/*     */     }
/*     */ 
/*     */     @EventHandler(priority=EventPriority.MONITOR)
/*     */     private void onPluginEnable(PluginEnableEvent event) {
/*  63 */       if (this.bridge.rscp == null) {
/*  64 */         Plugin plugin = event.getPlugin();
/*  65 */         if (plugin.getDescription().getName().equals("rscPermissions")) {
/*  66 */           this.bridge.rscp = ((MainPluginClass)plugin);
/*  67 */           this.bridge.rscpAPI = this.bridge.rscp.API;
/*  68 */           Chat_rscPermissions.log.info(String.format("[%s][Chat] %s hooked.", new Object[] { Chat_rscPermissions.this.vault.getDescription().getName(), "rscPermissions" }));
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/*     */     @EventHandler(priority=EventPriority.MONITOR)
/*     */     public void onPluginDisable(PluginDisableEvent event) {
/*  75 */       if ((this.bridge.rscpAPI != null) && 
/*  76 */         (event.getPlugin().getDescription().getName().equals(this.bridge.rscpAPI.getName()))) {
/*  77 */         this.bridge.rscpAPI = null;
/*  78 */         this.bridge.rscp = null;
/*  79 */         Chat_rscPermissions.log.info(String.format("[%s][Chat] %s un-hooked.", new Object[] { Chat_rscPermissions.this.vault.getDescription().getName(), "rscPermissions" }));
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\Vault.jar
 * Qualified Name:     net.milkbowl.vault.chat.plugins.Chat_rscPermissions
 * JD-Core Version:    0.6.2
 */