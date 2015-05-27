/*     */ package net.milkbowl.vault.chat.plugins;
/*     */ 
/*     */ import com.miraclem4n.mchat.api.Reader;
/*     */ import com.miraclem4n.mchat.api.Writer;
/*     */ import com.miraclem4n.mchat.types.InfoType;
/*     */ import in.mDev.MiracleM4n.mChatSuite.mChatSuite;
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
/*     */ public class Chat_mChatSuite extends Chat
/*     */ {
/*  37 */   private static final Logger log = Logger.getLogger("Minecraft");
/*  38 */   private final String name = "mChatSuite";
/*  39 */   private Plugin plugin = null;
/*  40 */   private mChatSuite mChat = null;
/*     */ 
/*     */   public Chat_mChatSuite(Plugin plugin, Permission perms) {
/*  43 */     super(perms);
/*  44 */     this.plugin = plugin;
/*     */ 
/*  46 */     Bukkit.getServer().getPluginManager().registerEvents(new PermissionServerListener(), plugin);
/*     */ 
/*  49 */     if (this.mChat == null) {
/*  50 */       Plugin chat = plugin.getServer().getPluginManager().getPlugin("mChatSuite");
/*  51 */       if ((chat != null) && (chat.isEnabled())) {
/*  52 */         this.mChat = ((mChatSuite)chat);
/*  53 */         log.info(String.format("[%s][Chat] %s hooked.", new Object[] { plugin.getDescription().getName(), "mChatSuite" }));
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public String getName()
/*     */   {
/*  84 */     return "mChatSuite";
/*     */   }
/*     */ 
/*     */   public boolean isEnabled()
/*     */   {
/*  89 */     return (this.mChat != null) && (this.mChat.isEnabled());
/*     */   }
/*     */ 
/*     */   public String getPlayerPrefix(String world, String player)
/*     */   {
/*  94 */     return Reader.getPrefix(player, InfoType.USER, world);
/*     */   }
/*     */ 
/*     */   public void setPlayerPrefix(String world, String player, String prefix)
/*     */   {
/*  99 */     setPlayerInfoValue(world, player, "prefix", prefix);
/*     */   }
/*     */ 
/*     */   public String getPlayerSuffix(String world, String player)
/*     */   {
/* 104 */     return Reader.getSuffix(player, InfoType.USER, world);
/*     */   }
/*     */ 
/*     */   public void setPlayerSuffix(String world, String player, String suffix)
/*     */   {
/* 109 */     setPlayerInfoValue(world, player, "suffix", suffix);
/*     */   }
/*     */ 
/*     */   public String getGroupPrefix(String world, String group)
/*     */   {
/* 114 */     return Reader.getPrefix(group, InfoType.GROUP, world);
/*     */   }
/*     */ 
/*     */   public void setGroupPrefix(String world, String group, String prefix)
/*     */   {
/* 119 */     setGroupInfoValue(world, group, "prefix", prefix);
/*     */   }
/*     */ 
/*     */   public String getGroupSuffix(String world, String group)
/*     */   {
/* 124 */     return Reader.getSuffix(group, InfoType.GROUP, world);
/*     */   }
/*     */ 
/*     */   public void setGroupSuffix(String world, String group, String suffix)
/*     */   {
/* 129 */     setGroupInfoValue(world, group, "suffix", suffix);
/*     */   }
/*     */ 
/*     */   public int getPlayerInfoInteger(String world, String player, String node, int defaultValue)
/*     */   {
/* 134 */     String val = getPlayerInfoValue(world, player, node);
/* 135 */     if ((val == null) || (val.equals("")))
/* 136 */       return defaultValue;
/*     */     try
/*     */     {
/* 139 */       return Integer.parseInt(val); } catch (NumberFormatException e) {
/*     */     }
/* 141 */     return defaultValue;
/*     */   }
/*     */ 
/*     */   public void setPlayerInfoInteger(String world, String player, String node, int value)
/*     */   {
/* 147 */     setPlayerInfoValue(world, player, node, Integer.valueOf(value));
/*     */   }
/*     */ 
/*     */   public int getGroupInfoInteger(String world, String group, String node, int defaultValue)
/*     */   {
/* 152 */     String val = getGroupInfoValue(world, group, node);
/* 153 */     if ((val == null) || (val.equals("")))
/* 154 */       return defaultValue;
/*     */     try
/*     */     {
/* 157 */       return Integer.parseInt(val); } catch (NumberFormatException e) {
/*     */     }
/* 159 */     return defaultValue;
/*     */   }
/*     */ 
/*     */   public void setGroupInfoInteger(String world, String group, String node, int value)
/*     */   {
/* 165 */     setGroupInfoValue(world, group, node, Integer.valueOf(value));
/*     */   }
/*     */ 
/*     */   public double getPlayerInfoDouble(String world, String player, String node, double defaultValue)
/*     */   {
/* 170 */     String val = getPlayerInfoValue(world, player, node);
/* 171 */     if ((val == null) || (val.equals("")))
/* 172 */       return defaultValue;
/*     */     try
/*     */     {
/* 175 */       return Double.parseDouble(val); } catch (NumberFormatException e) {
/*     */     }
/* 177 */     return defaultValue;
/*     */   }
/*     */ 
/*     */   public void setPlayerInfoDouble(String world, String player, String node, double value)
/*     */   {
/* 183 */     setPlayerInfoValue(world, player, node, Double.valueOf(value));
/*     */   }
/*     */ 
/*     */   public double getGroupInfoDouble(String world, String group, String node, double defaultValue)
/*     */   {
/* 188 */     String val = getGroupInfoValue(world, group, node);
/* 189 */     if ((val == null) || (val.equals("")))
/* 190 */       return defaultValue;
/*     */     try
/*     */     {
/* 193 */       return Double.parseDouble(val); } catch (NumberFormatException e) {
/*     */     }
/* 195 */     return defaultValue;
/*     */   }
/*     */ 
/*     */   public void setGroupInfoDouble(String world, String group, String node, double value)
/*     */   {
/* 201 */     setGroupInfoValue(world, group, node, Double.valueOf(value));
/*     */   }
/*     */ 
/*     */   public boolean getPlayerInfoBoolean(String world, String player, String node, boolean defaultValue)
/*     */   {
/* 206 */     String val = getPlayerInfoValue(world, player, node);
/* 207 */     if ((val == null) || (val.equals(""))) {
/* 208 */       return defaultValue;
/*     */     }
/* 210 */     return Boolean.parseBoolean(val);
/*     */   }
/*     */ 
/*     */   public void setPlayerInfoBoolean(String world, String player, String node, boolean value)
/*     */   {
/* 215 */     setPlayerInfoValue(world, player, node, Boolean.valueOf(value));
/*     */   }
/*     */ 
/*     */   public boolean getGroupInfoBoolean(String world, String group, String node, boolean defaultValue)
/*     */   {
/* 220 */     String val = getGroupInfoValue(world, group, node);
/* 221 */     if ((val == null) || (val.equals(""))) {
/* 222 */       return defaultValue;
/*     */     }
/* 224 */     return Boolean.valueOf(val).booleanValue();
/*     */   }
/*     */ 
/*     */   public void setGroupInfoBoolean(String world, String group, String node, boolean value)
/*     */   {
/* 229 */     setGroupInfoValue(world, group, node, Boolean.valueOf(value));
/*     */   }
/*     */ 
/*     */   public String getPlayerInfoString(String world, String player, String node, String defaultValue)
/*     */   {
/* 234 */     String val = getPlayerInfoValue(world, player, node);
/* 235 */     if (val == null) {
/* 236 */       return defaultValue;
/*     */     }
/* 238 */     return val;
/*     */   }
/*     */ 
/*     */   public void setPlayerInfoString(String world, String player, String node, String value)
/*     */   {
/* 244 */     setPlayerInfoValue(world, player, node, value);
/*     */   }
/*     */ 
/*     */   public String getGroupInfoString(String world, String group, String node, String defaultValue)
/*     */   {
/* 249 */     String val = getGroupInfoValue(world, group, node);
/* 250 */     if (val == null) {
/* 251 */       return defaultValue;
/*     */     }
/* 253 */     return val;
/*     */   }
/*     */ 
/*     */   public void setGroupInfoString(String world, String group, String node, String value)
/*     */   {
/* 259 */     setGroupInfoValue(world, group, node, value);
/*     */   }
/*     */ 
/*     */   private void setPlayerInfoValue(String world, String player, String node, Object value) {
/* 263 */     if (world != null)
/* 264 */       Writer.setWorldVar(player, InfoType.USER, world, node, value.toString());
/*     */     else
/* 266 */       Writer.setInfoVar(player, InfoType.USER, node, value.toString());
/*     */   }
/*     */ 
/*     */   private void setGroupInfoValue(String world, String group, String node, Object value)
/*     */   {
/* 271 */     if (world != null)
/* 272 */       Writer.setWorldVar(group, InfoType.GROUP, world, node, value);
/*     */     else
/* 274 */       Writer.setInfoVar(group, InfoType.GROUP, node, value);
/*     */   }
/*     */ 
/*     */   private String getPlayerInfoValue(String world, String player, String node) {
/* 278 */     return Reader.getInfo(player, InfoType.USER, world, node);
/*     */   }
/*     */ 
/*     */   private String getGroupInfoValue(String world, String group, String node) {
/* 282 */     return Reader.getInfo(group, InfoType.GROUP, world, node);
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
/*  62 */       if (Chat_mChatSuite.this.mChat == null) {
/*  63 */         Plugin chat = event.getPlugin();
/*  64 */         if (chat.getDescription().getName().equals("mChatSuite")) {
/*  65 */           Chat_mChatSuite.this.mChat = ((mChatSuite)chat);
/*  66 */           Chat_mChatSuite.log.info(String.format("[%s][Chat] %s hooked.", new Object[] { Chat_mChatSuite.this.plugin.getDescription().getName(), "mChatSuite" }));
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/*     */     @EventHandler(priority=EventPriority.MONITOR)
/*     */     public void onPluginDisable(PluginDisableEvent event) {
/*  73 */       if ((Chat_mChatSuite.this.mChat != null) && 
/*  74 */         (event.getPlugin().getDescription().getName().equals("mChatSuite"))) {
/*  75 */         Chat_mChatSuite.this.mChat = null;
/*  76 */         Chat_mChatSuite.log.info(String.format("[%s][Chat] %s un-hooked.", new Object[] { Chat_mChatSuite.this.plugin.getDescription().getName(), "mChatSuite" }));
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\Vault.jar
 * Qualified Name:     net.milkbowl.vault.chat.plugins.Chat_mChatSuite
 * JD-Core Version:    0.6.2
 */