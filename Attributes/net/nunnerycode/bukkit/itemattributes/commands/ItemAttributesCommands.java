/*     */ package net.nunnerycode.bukkit.itemattributes.commands;
/*     */ 
/*     */ import java.text.DecimalFormat;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import net.nunnerycode.bukkit.itemattributes.ItemAttributesPlugin;
/*     */ import net.nunnerycode.bukkit.itemattributes.api.ItemAttributes;
/*     */ import net.nunnerycode.bukkit.itemattributes.api.attributes.Attribute;
/*     */ import net.nunnerycode.bukkit.itemattributes.api.commands.ItemAttributesCommand;
/*     */ import net.nunnerycode.bukkit.itemattributes.api.managers.LanguageManager;
/*     */ import net.nunnerycode.bukkit.itemattributes.api.managers.SettingsManager;
/*     */ import net.nunnerycode.bukkit.itemattributes.utils.ItemAttributesParseUtil;
/*     */ import org.bukkit.Bukkit;
/*     */ import org.bukkit.command.CommandSender;
/*     */ import org.bukkit.entity.Player;
/*     */ import org.bukkit.inventory.EntityEquipment;
/*     */ import org.bukkit.inventory.ItemStack;
/*     */ import org.bukkit.inventory.meta.ItemMeta;
/*     */ import se.ranzdo.bukkit.methodcommand.Arg;
/*     */ import se.ranzdo.bukkit.methodcommand.Command;
/*     */ import se.ranzdo.bukkit.methodcommand.CommandHandler;
/*     */ 
/*     */ public class ItemAttributesCommands
/*     */   implements ItemAttributesCommand
/*     */ {
/*     */   private final ItemAttributes plugin;
/*     */   private final CommandHandler commandHandler;
/*  23 */   private final DecimalFormat DF = new DecimalFormat("#.##");
/*     */ 
/*     */   public ItemAttributesCommands(ItemAttributesPlugin plugin) {
/*  26 */     this.plugin = plugin;
/*  27 */     this.commandHandler = new CommandHandler(plugin);
/*  28 */     this.commandHandler.registerCommands(this);
/*     */   }
/*     */ 
/*     */   public CommandHandler getCommandHandler()
/*     */   {
/*  33 */     return this.commandHandler;
/*     */   }
/*     */ 
/*     */   public ItemAttributes getPlugin()
/*     */   {
/*  38 */     return this.plugin;
/*     */   }
/*     */ 
/*     */   @Command(identifier="itemattributes view", description="Shows a player's ItemAttributes stats", permissions={"itemattributes.command.view"})
/*     */   public void viewSubcommand(CommandSender sender, @Arg(name="player name", def="self") String name)
/*     */   {
/*  44 */     String playerName = name;
/*  45 */     if ((!(sender instanceof Player)) && (playerName.equalsIgnoreCase("self"))) {
/*  46 */       getPlugin().getLanguageManager().sendMessage(sender, "commands.cannot-use");
/*  47 */       return;
/*     */     }
/*  49 */     if (playerName.equalsIgnoreCase("self")) {
/*  50 */       playerName = sender.getName();
/*     */     }
/*  52 */     Player player = Bukkit.getPlayer(playerName);
/*     */ 
/*  54 */     getPlugin().getLanguageManager().sendMessage(sender, "commands.view-stats-help");
/*     */ 
/*  56 */     Attribute damageAttribute = getPlugin().getSettingsManager().getAttribute("DAMAGE");
/*  57 */     Attribute meleeDamageAttribute = getPlugin().getSettingsManager().getAttribute("MELEE DAMAGE");
/*  58 */     Attribute rangedDamageAttribute = getPlugin().getSettingsManager().getAttribute("RANGED DAMAGE");
/*  59 */     Attribute criticalRateAttribute = getPlugin().getSettingsManager().getAttribute("CRITICAL RATE");
/*  60 */     Attribute criticalDamageAttribute = getPlugin().getSettingsManager().getAttribute("CRITICAL DAMAGE");
/*  61 */     Attribute stunRateAttribute = getPlugin().getSettingsManager().getAttribute("STUN RATE");
/*  62 */     Attribute stunLengthAttribute = getPlugin().getSettingsManager().getAttribute("STUN LENGTH");
/*  63 */     Attribute dodgeRateAttribute = getPlugin().getSettingsManager().getAttribute("DODGE RATE");
/*  64 */     Attribute healthAttribute = getPlugin().getSettingsManager().getAttribute("HEALTH");
/*  65 */     Attribute regenerationAttribute = getPlugin().getSettingsManager().getAttribute("REGENERATION");
/*  66 */     Attribute armorPenetrationAttribute = getPlugin().getSettingsManager().getAttribute("ARMOR PENETRATION");
/*  67 */     Attribute armorAttribute = getPlugin().getSettingsManager().getAttribute("ARMOR");
/*     */ 
/*  70 */     sendStatMessage(sender, player, healthAttribute, getPlugin().getSettingsManager().getBasePlayerHealth());
/*     */ 
/*  73 */     sendStatMessage(sender, player, damageAttribute, 0.0D);
/*     */ 
/*  75 */     sendStatMessage(sender, player, meleeDamageAttribute, 0.0D);
/*     */ 
/*  77 */     sendStatMessage(sender, player, rangedDamageAttribute, 0.0D);
/*     */ 
/*  79 */     sendStatMessage(sender, player, regenerationAttribute, 0.0D);
/*     */ 
/*  81 */     sendStatMessage(sender, player, armorAttribute, 0.0D);
/*     */ 
/*  83 */     sendPercentageStatMessage(sender, player, criticalRateAttribute, getPlugin().getSettingsManager().getBaseCriticalRate());
/*     */ 
/*  86 */     sendPercentageStatMessage(sender, player, criticalDamageAttribute, getPlugin().getSettingsManager().getBaseCriticalDamage());
/*     */ 
/*  89 */     sendStatMessage(sender, player, armorPenetrationAttribute, 0.0D);
/*     */ 
/*  91 */     sendPercentageStatMessage(sender, player, stunRateAttribute, getPlugin().getSettingsManager().getBaseStunRate());
/*     */ 
/*  94 */     sendStatMessage(sender, player, stunLengthAttribute, getPlugin().getSettingsManager().getBaseStunLength());
/*     */ 
/*  96 */     sendPercentageStatMessage(sender, player, dodgeRateAttribute, getPlugin().getSettingsManager().getBaseDodgeRate());
/*     */   }
/*     */ 
/*     */   private void sendPercentageStatMessage(CommandSender sender, Player player, Attribute attribute, double baseStat)
/*     */   {
/* 101 */     double statHelmet = ItemAttributesParseUtil.getValue(getItemStackLore(player.getEquipment().getHelmet()), attribute);
/*     */ 
/* 103 */     double statChestplate = ItemAttributesParseUtil.getValue(getItemStackLore(player.getEquipment().getChestplate()), attribute);
/*     */ 
/* 105 */     double statLeggings = ItemAttributesParseUtil.getValue(getItemStackLore(player.getEquipment().getLeggings()), attribute);
/*     */ 
/* 107 */     double statBoots = ItemAttributesParseUtil.getValue(getItemStackLore(player.getEquipment().getBoots()), attribute);
/*     */ 
/* 109 */     double statItem = ItemAttributesParseUtil.getValue(getItemStackLore(player.getEquipment().getItemInHand()), attribute);
/*     */ 
/* 111 */     double statTotal = baseStat + statHelmet + statChestplate + statLeggings + statBoots + statItem;
/* 112 */     String formatString = attribute.getFormat().replaceAll("%(?s)(.*?)%", "").trim();
/* 113 */     getPlugin().getLanguageManager().sendMessage(sender, "commands.view-stats-percentage", new String[][] { { "%statname%", formatString }, { "%totalvalue%", this.DF.format(statTotal * 100.0D) }, { "%helmet%", this.DF.format(statHelmet * 100.0D) }, { "%chestplate%", this.DF.format(statChestplate * 100.0D) }, { "%leggings%", this.DF.format(statLeggings * 100.0D) }, { "%boots%", this.DF.format(statBoots * 100.0D) }, { "%item%", this.DF.format(statItem * 100.0D) } });
/*     */   }
/*     */ 
/*     */   private void sendStatMessage(CommandSender sender, Player player, Attribute attribute, double baseStat)
/*     */   {
/* 121 */     double statHelmet = ItemAttributesParseUtil.getValue(getItemStackLore(player.getEquipment().getHelmet()), attribute);
/*     */ 
/* 123 */     double statChestplate = ItemAttributesParseUtil.getValue(getItemStackLore(player.getEquipment().getChestplate()), attribute);
/*     */ 
/* 125 */     double statLeggings = ItemAttributesParseUtil.getValue(getItemStackLore(player.getEquipment().getLeggings()), attribute);
/*     */ 
/* 127 */     double statBoots = ItemAttributesParseUtil.getValue(getItemStackLore(player.getEquipment().getBoots()), attribute);
/*     */ 
/* 129 */     double statItem = ItemAttributesParseUtil.getValue(getItemStackLore(player.getEquipment().getItemInHand()), attribute);
/*     */ 
/* 131 */     double statTotal = baseStat + statHelmet + statChestplate + statLeggings + statBoots + statItem;
/* 132 */     String formatString = attribute.getFormat().replaceAll("%(?s)(.*?)%", "").trim();
/* 133 */     getPlugin().getLanguageManager().sendMessage(sender, "commands.view-stats", new String[][] { { "%statname%", formatString }, { "%totalvalue%", this.DF.format(statTotal) }, { "%helmet%", this.DF.format(statHelmet) }, { "%chestplate%", this.DF.format(statChestplate) }, { "%leggings%", this.DF.format(statLeggings) }, { "%boots%", this.DF.format(statBoots) }, { "%item%", this.DF.format(statItem) } });
/*     */   }
/*     */ 
/*     */   private List<String> getItemStackLore(ItemStack itemStack)
/*     */   {
/* 140 */     List lore = new ArrayList();
/* 141 */     if ((itemStack != null) && (itemStack.hasItemMeta()) && (itemStack.getItemMeta().hasLore())) {
/* 142 */       lore.addAll(itemStack.getItemMeta().getLore());
/*     */     }
/* 144 */     return lore;
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\ItemAttributes.jar
 * Qualified Name:     net.nunnerycode.bukkit.itemattributes.commands.ItemAttributesCommands
 * JD-Core Version:    0.6.2
 */