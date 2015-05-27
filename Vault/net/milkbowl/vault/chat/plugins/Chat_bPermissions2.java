/*     */ package net.milkbowl.vault.chat.plugins;
/*     */ 
/*     */ import de.bananaco.bpermissions.api.ApiLayer;
/*     */ import de.bananaco.bpermissions.api.CalculableType;
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
/*     */ public class Chat_bPermissions2 extends Chat
/*     */ {
/*  35 */   private static final Logger log = Logger.getLogger("Minecraft");
/*  36 */   private final String name = "bInfo";
/*  37 */   private Plugin plugin = null;
/*  38 */   private boolean hooked = false;
/*     */ 
/*     */   public Chat_bPermissions2(Plugin plugin, Permission perms) {
/*  41 */     super(perms);
/*  42 */     this.plugin = plugin;
/*     */ 
/*  44 */     Bukkit.getServer().getPluginManager().registerEvents(new PermissionServerListener(this), plugin);
/*     */ 
/*  47 */     if (!this.hooked) {
/*  48 */       Plugin p = plugin.getServer().getPluginManager().getPlugin("bPermissions");
/*  49 */       if (p != null) {
/*  50 */         this.hooked = true;
/*  51 */         log.info(String.format("[%s][Chat] %s hooked.", new Object[] { plugin.getDescription().getName(), "bPermissions2" }));
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public String getName()
/*     */   {
/*  87 */     return "bInfo";
/*     */   }
/*     */ 
/*     */   public boolean isEnabled()
/*     */   {
/*  92 */     return this.hooked;
/*     */   }
/*     */ 
/*     */   public String getPlayerPrefix(String world, String player)
/*     */   {
/*  97 */     return ApiLayer.getValue(world, CalculableType.USER, player, "prefix");
/*     */   }
/*     */ 
/*     */   public void setPlayerPrefix(String world, String player, String prefix)
/*     */   {
/* 102 */     ApiLayer.setValue(world, CalculableType.USER, player, "prefix", prefix);
/*     */   }
/*     */ 
/*     */   public String getPlayerSuffix(String world, String player)
/*     */   {
/* 107 */     return ApiLayer.getValue(world, CalculableType.USER, player, "suffix");
/*     */   }
/*     */ 
/*     */   public void setPlayerSuffix(String world, String player, String suffix)
/*     */   {
/* 112 */     ApiLayer.setValue(world, CalculableType.USER, player, "suffix", suffix);
/*     */   }
/*     */ 
/*     */   public String getGroupPrefix(String world, String group)
/*     */   {
/* 117 */     return ApiLayer.getValue(world, CalculableType.GROUP, group, "prefix");
/*     */   }
/*     */ 
/*     */   public void setGroupPrefix(String world, String group, String prefix)
/*     */   {
/* 122 */     ApiLayer.setValue(world, CalculableType.GROUP, group, "prefix", prefix);
/*     */   }
/*     */ 
/*     */   public String getGroupSuffix(String world, String group)
/*     */   {
/* 127 */     return ApiLayer.getValue(world, CalculableType.GROUP, group, "suffix");
/*     */   }
/*     */ 
/*     */   public void setGroupSuffix(String world, String group, String suffix)
/*     */   {
/* 132 */     ApiLayer.setValue(world, CalculableType.GROUP, group, "suffix", suffix);
/*     */   }
/*     */ 
/*     */   public int getPlayerInfoInteger(String world, String player, String node, int defaultValue)
/*     */   {
/* 137 */     String s = getPlayerInfoString(world, player, node, null);
/* 138 */     if (s == null)
/* 139 */       return defaultValue;
/*     */     try
/*     */     {
/* 142 */       return Integer.valueOf(s).intValue();
/*     */     } catch (NumberFormatException e) {
/*     */     }
/* 145 */     return defaultValue;
/*     */   }
/*     */ 
/*     */   public void setPlayerInfoInteger(String world, String player, String node, int value)
/*     */   {
/* 151 */     ApiLayer.setValue(world, CalculableType.USER, player, node, String.valueOf(value));
/*     */   }
/*     */ 
/*     */   public int getGroupInfoInteger(String world, String group, String node, int defaultValue)
/*     */   {
/* 156 */     String s = getGroupInfoString(world, group, node, null);
/* 157 */     if (s == null)
/* 158 */       return defaultValue;
/*     */     try
/*     */     {
/* 161 */       return Integer.valueOf(s).intValue();
/*     */     } catch (NumberFormatException e) {
/*     */     }
/* 164 */     return defaultValue;
/*     */   }
/*     */ 
/*     */   public void setGroupInfoInteger(String world, String group, String node, int value)
/*     */   {
/* 170 */     ApiLayer.setValue(world, CalculableType.GROUP, group, node, String.valueOf(value));
/*     */   }
/*     */ 
/*     */   public double getPlayerInfoDouble(String world, String player, String node, double defaultValue)
/*     */   {
/* 175 */     String s = getPlayerInfoString(world, player, node, null);
/* 176 */     if (s == null)
/* 177 */       return defaultValue;
/*     */     try
/*     */     {
/* 180 */       return Double.valueOf(s).doubleValue();
/*     */     } catch (NumberFormatException e) {
/*     */     }
/* 183 */     return defaultValue;
/*     */   }
/*     */ 
/*     */   public void setPlayerInfoDouble(String world, String player, String node, double value)
/*     */   {
/* 189 */     ApiLayer.setValue(world, CalculableType.USER, player, node, String.valueOf(value));
/*     */   }
/*     */ 
/*     */   public double getGroupInfoDouble(String world, String group, String node, double defaultValue)
/*     */   {
/* 194 */     String s = getGroupInfoString(world, group, node, null);
/* 195 */     if (s == null)
/* 196 */       return defaultValue;
/*     */     try
/*     */     {
/* 199 */       return Double.valueOf(s).doubleValue();
/*     */     } catch (NumberFormatException e) {
/*     */     }
/* 202 */     return defaultValue;
/*     */   }
/*     */ 
/*     */   public void setGroupInfoDouble(String world, String group, String node, double value)
/*     */   {
/* 208 */     ApiLayer.setValue(world, CalculableType.GROUP, group, node, String.valueOf(value));
/*     */   }
/*     */ 
/*     */   public boolean getPlayerInfoBoolean(String world, String player, String node, boolean defaultValue)
/*     */   {
/* 213 */     String s = getPlayerInfoString(world, player, node, null);
/* 214 */     if (s == null) {
/* 215 */       return defaultValue;
/*     */     }
/* 217 */     Boolean val = Boolean.valueOf(s);
/* 218 */     return val != null ? val.booleanValue() : defaultValue;
/*     */   }
/*     */ 
/*     */   public void setPlayerInfoBoolean(String world, String player, String node, boolean value)
/*     */   {
/* 224 */     ApiLayer.setValue(world, CalculableType.USER, player, node, String.valueOf(value));
/*     */   }
/*     */ 
/*     */   public boolean getGroupInfoBoolean(String world, String group, String node, boolean defaultValue)
/*     */   {
/* 229 */     String s = getGroupInfoString(world, group, node, null);
/* 230 */     if (s == null) {
/* 231 */       return defaultValue;
/*     */     }
/* 233 */     Boolean val = Boolean.valueOf(s);
/* 234 */     return val != null ? val.booleanValue() : defaultValue;
/*     */   }
/*     */ 
/*     */   public void setGroupInfoBoolean(String world, String group, String node, boolean value)
/*     */   {
/* 240 */     ApiLayer.setValue(world, CalculableType.GROUP, group, node, String.valueOf(value));
/*     */   }
/*     */ 
/*     */   public String getPlayerInfoString(String world, String player, String node, String defaultValue)
/*     */   {
/* 245 */     String val = ApiLayer.getValue(world, CalculableType.USER, player, node);
/* 246 */     return (val == null) || (val == "BLANKWORLD") || (val == "") ? defaultValue : val;
/*     */   }
/*     */ 
/*     */   public void setPlayerInfoString(String world, String player, String node, String value)
/*     */   {
/* 251 */     ApiLayer.setValue(world, CalculableType.USER, player, node, value);
/*     */   }
/*     */ 
/*     */   public String getGroupInfoString(String world, String group, String node, String defaultValue)
/*     */   {
/* 256 */     String val = ApiLayer.getValue(world, CalculableType.GROUP, group, node);
/* 257 */     return (val == null) || (val == "BLANKWORLD") || (val == "") ? defaultValue : val;
/*     */   }
/*     */ 
/*     */   public void setGroupInfoString(String world, String group, String node, String value)
/*     */   {
/* 262 */     ApiLayer.setValue(world, CalculableType.GROUP, group, node, value);
/*     */   }
/*     */ 
/*     */   public class PermissionServerListener
/*     */     implements Listener
/*     */   {
/*  57 */     Chat_bPermissions2 chat = null;
/*     */ 
/*     */     public PermissionServerListener(Chat_bPermissions2 chat) {
/*  60 */       this.chat = chat;
/*     */     }
/*     */ 
/*     */     @EventHandler(priority=EventPriority.MONITOR)
/*     */     public void onPluginEnable(PluginEnableEvent event) {
/*  65 */       if (!Chat_bPermissions2.this.hooked) {
/*  66 */         Plugin chat = event.getPlugin();
/*  67 */         if (chat.getDescription().getName().equals("bPermissions")) {
/*  68 */           Chat_bPermissions2.this.hooked = true;
/*  69 */           Chat_bPermissions2.log.info(String.format("[%s][Chat] %s hooked.", new Object[] { Chat_bPermissions2.this.plugin.getDescription().getName(), "bPermissions2" }));
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/*     */     @EventHandler(priority=EventPriority.MONITOR)
/*     */     public void onPluginDisable(PluginDisableEvent event) {
/*  76 */       if ((Chat_bPermissions2.this.hooked) && 
/*  77 */         (event.getPlugin().getDescription().getName().equals("bPermissions"))) {
/*  78 */         Chat_bPermissions2.this.hooked = false;
/*  79 */         Chat_bPermissions2.log.info(String.format("[%s][Chat] %s un-hooked.", new Object[] { Chat_bPermissions2.this.plugin.getDescription().getName(), "bPermissions2" }));
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\Vault.jar
 * Qualified Name:     net.milkbowl.vault.chat.plugins.Chat_bPermissions2
 * JD-Core Version:    0.6.2
 */