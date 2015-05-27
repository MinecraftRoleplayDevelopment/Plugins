/*     */ package net.milkbowl.vault.chat.plugins;
/*     */ 
/*     */ import de.bananaco.permissions.Permissions;
/*     */ import de.bananaco.permissions.info.InfoReader;
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
/*     */ public class Chat_bPermissions extends Chat
/*     */ {
/*  35 */   private static final Logger log = Logger.getLogger("Minecraft");
/*  36 */   private final String name = "bInfo";
/*  37 */   private Plugin plugin = null;
/*     */   InfoReader chat;
/*     */ 
/*     */   public Chat_bPermissions(Plugin plugin, Permission perms)
/*     */   {
/*  41 */     super(perms);
/*  42 */     this.plugin = plugin;
/*     */ 
/*  44 */     Bukkit.getServer().getPluginManager().registerEvents(new PermissionServerListener(this), plugin);
/*     */ 
/*  47 */     if (this.chat == null) {
/*  48 */       Plugin p = plugin.getServer().getPluginManager().getPlugin("bPermissions");
/*  49 */       if (p != null) {
/*  50 */         this.chat = Permissions.getInfoReader();
/*  51 */         log.info(String.format("[%s][Chat] %s hooked.", new Object[] { plugin.getDescription().getName(), "bPermissions" }));
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public String getName()
/*     */   {
/*  86 */     return "bInfo";
/*     */   }
/*     */ 
/*     */   public boolean isEnabled()
/*     */   {
/*  91 */     return this.chat != null;
/*     */   }
/*     */ 
/*     */   public String getPlayerPrefix(String world, String player)
/*     */   {
/*  96 */     return this.chat.getPrefix(player, world);
/*     */   }
/*     */ 
/*     */   public void setPlayerPrefix(String world, String player, String prefix)
/*     */   {
/* 101 */     throw new UnsupportedOperationException("bPermissions does not support altering info nodes");
/*     */   }
/*     */ 
/*     */   public String getPlayerSuffix(String world, String player)
/*     */   {
/* 106 */     return this.chat.getSuffix(player, world);
/*     */   }
/*     */ 
/*     */   public void setPlayerSuffix(String world, String player, String suffix)
/*     */   {
/* 111 */     throw new UnsupportedOperationException("bPermissions does not support altering info nodes");
/*     */   }
/*     */ 
/*     */   public String getGroupPrefix(String world, String group)
/*     */   {
/* 116 */     return this.chat.getGroupPrefix(group, world);
/*     */   }
/*     */ 
/*     */   public void setGroupPrefix(String world, String group, String prefix)
/*     */   {
/* 121 */     throw new UnsupportedOperationException("bPermissions does not support altering info nodes");
/*     */   }
/*     */ 
/*     */   public String getGroupSuffix(String world, String group)
/*     */   {
/* 126 */     return this.chat.getGroupSuffix(group, world);
/*     */   }
/*     */ 
/*     */   public void setGroupSuffix(String world, String group, String suffix)
/*     */   {
/* 131 */     throw new UnsupportedOperationException("bPermissions does not support altering info nodes");
/*     */   }
/*     */ 
/*     */   public int getPlayerInfoInteger(String world, String player, String node, int defaultValue)
/*     */   {
/* 136 */     String s = getPlayerInfoString(world, player, node, null);
/* 137 */     if (s == null)
/* 138 */       return defaultValue;
/*     */     try
/*     */     {
/* 141 */       return Integer.valueOf(s).intValue();
/*     */     } catch (NumberFormatException e) {
/*     */     }
/* 144 */     return defaultValue;
/*     */   }
/*     */ 
/*     */   public void setPlayerInfoInteger(String world, String player, String node, int value)
/*     */   {
/* 150 */     throw new UnsupportedOperationException("bPermissions does not support altering info nodes");
/*     */   }
/*     */ 
/*     */   public int getGroupInfoInteger(String world, String group, String node, int defaultValue)
/*     */   {
/* 155 */     String s = getGroupInfoString(world, group, node, null);
/* 156 */     if (s == null)
/* 157 */       return defaultValue;
/*     */     try
/*     */     {
/* 160 */       return Integer.valueOf(s).intValue();
/*     */     } catch (NumberFormatException e) {
/*     */     }
/* 163 */     return defaultValue;
/*     */   }
/*     */ 
/*     */   public void setGroupInfoInteger(String world, String group, String node, int value)
/*     */   {
/* 169 */     throw new UnsupportedOperationException("bPermissions does not support altering info nodes");
/*     */   }
/*     */ 
/*     */   public double getPlayerInfoDouble(String world, String player, String node, double defaultValue)
/*     */   {
/* 174 */     String s = getPlayerInfoString(world, player, node, null);
/* 175 */     if (s == null)
/* 176 */       return defaultValue;
/*     */     try
/*     */     {
/* 179 */       return Double.valueOf(s).doubleValue();
/*     */     } catch (NumberFormatException e) {
/*     */     }
/* 182 */     return defaultValue;
/*     */   }
/*     */ 
/*     */   public void setPlayerInfoDouble(String world, String player, String node, double value)
/*     */   {
/* 188 */     throw new UnsupportedOperationException("bPermissions does not support altering info nodes");
/*     */   }
/*     */ 
/*     */   public double getGroupInfoDouble(String world, String group, String node, double defaultValue)
/*     */   {
/* 193 */     String s = getGroupInfoString(world, group, node, null);
/* 194 */     if (s == null)
/* 195 */       return defaultValue;
/*     */     try
/*     */     {
/* 198 */       return Double.valueOf(s).doubleValue();
/*     */     } catch (NumberFormatException e) {
/*     */     }
/* 201 */     return defaultValue;
/*     */   }
/*     */ 
/*     */   public void setGroupInfoDouble(String world, String group, String node, double value)
/*     */   {
/* 207 */     throw new UnsupportedOperationException("bPermissions does not support altering info nodes");
/*     */   }
/*     */ 
/*     */   public boolean getPlayerInfoBoolean(String world, String player, String node, boolean defaultValue)
/*     */   {
/* 212 */     String s = getPlayerInfoString(world, player, node, null);
/* 213 */     if (s == null) {
/* 214 */       return defaultValue;
/*     */     }
/* 216 */     Boolean val = Boolean.valueOf(s);
/* 217 */     return val != null ? val.booleanValue() : defaultValue;
/*     */   }
/*     */ 
/*     */   public void setPlayerInfoBoolean(String world, String player, String node, boolean value)
/*     */   {
/* 223 */     throw new UnsupportedOperationException("bPermissions does not support altering info nodes");
/*     */   }
/*     */ 
/*     */   public boolean getGroupInfoBoolean(String world, String group, String node, boolean defaultValue)
/*     */   {
/* 228 */     String s = getGroupInfoString(world, group, node, null);
/* 229 */     if (s == null) {
/* 230 */       return defaultValue;
/*     */     }
/* 232 */     Boolean val = Boolean.valueOf(s);
/* 233 */     return val != null ? val.booleanValue() : defaultValue;
/*     */   }
/*     */ 
/*     */   public void setGroupInfoBoolean(String world, String group, String node, boolean value)
/*     */   {
/* 239 */     throw new UnsupportedOperationException("bPermissions does not support altering info nodes");
/*     */   }
/*     */ 
/*     */   public String getPlayerInfoString(String world, String player, String node, String defaultValue)
/*     */   {
/* 244 */     String val = this.chat.getValue(player, world, node);
/* 245 */     return (val == null) || (val == "BLANKWORLD") ? defaultValue : val;
/*     */   }
/*     */ 
/*     */   public void setPlayerInfoString(String world, String player, String node, String value)
/*     */   {
/* 250 */     throw new UnsupportedOperationException("bPermissions does not support altering info nodes");
/*     */   }
/*     */ 
/*     */   public String getGroupInfoString(String world, String group, String node, String defaultValue)
/*     */   {
/* 255 */     String val = this.chat.getGroupValue(group, world, node);
/* 256 */     return (val == null) || (val == "BLANKWORLD") ? defaultValue : val;
/*     */   }
/*     */ 
/*     */   public void setGroupInfoString(String world, String group, String node, String value)
/*     */   {
/* 261 */     throw new UnsupportedOperationException("bPermissions does not support altering info nodes");
/*     */   }
/*     */ 
/*     */   public class PermissionServerListener
/*     */     implements Listener
/*     */   {
/*  57 */     Chat_bPermissions chat = null;
/*     */ 
/*     */     public PermissionServerListener(Chat_bPermissions chat) {
/*  60 */       this.chat = chat;
/*     */     }
/*     */ 
/*     */     @EventHandler(priority=EventPriority.MONITOR)
/*     */     public void onPluginEnable(PluginEnableEvent event) {
/*  65 */       if (this.chat.chat == null) {
/*  66 */         Plugin chat = event.getPlugin();
/*  67 */         if (chat.getDescription().getName().equals("bPermissions")) {
/*  68 */           this.chat.chat = Permissions.getInfoReader();
/*  69 */           Chat_bPermissions.log.info(String.format("[%s][Chat] %s hooked.", new Object[] { Chat_bPermissions.this.plugin.getDescription().getName(), "bPermissions" }));
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/*     */     @EventHandler(priority=EventPriority.MONITOR)
/*     */     public void onPluginDisable(PluginDisableEvent event) {
/*  76 */       if ((this.chat.chat != null) && 
/*  77 */         (event.getPlugin().getDescription().getName().equals("bPermissions"))) {
/*  78 */         this.chat.chat = null;
/*  79 */         Chat_bPermissions.log.info(String.format("[%s][Chat] %s un-hooked.", new Object[] { Chat_bPermissions.this.plugin.getDescription().getName(), "bPermissions" }));
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\Vault.jar
 * Qualified Name:     net.milkbowl.vault.chat.plugins.Chat_bPermissions
 * JD-Core Version:    0.6.2
 */