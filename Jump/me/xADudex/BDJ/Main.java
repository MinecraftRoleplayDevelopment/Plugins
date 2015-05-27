/*     */ package me.xADudex.BDJ;
/*     */ 
/*     */ import com.sk89q.worldedit.bukkit.BukkitUtil;
/*     */ import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
/*     */ import com.sk89q.worldguard.protection.managers.RegionManager;
/*     */ import java.io.IOException;
/*     */ import java.lang.reflect.Field;
/*     */ import java.lang.reflect.Method;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.HashMap;
/*     */ import java.util.UUID;
/*     */ import java.util.logging.Logger;
/*     */ import org.bukkit.Bukkit;
/*     */ import org.bukkit.GameMode;
/*     */ import org.bukkit.Location;
/*     */ import org.bukkit.Material;
/*     */ import org.bukkit.Sound;
/*     */ import org.bukkit.World;
/*     */ import org.bukkit.block.Block;
/*     */ import org.bukkit.block.BlockFace;
/*     */ import org.bukkit.command.CommandSender;
/*     */ import org.bukkit.command.PluginCommand;
/*     */ import org.bukkit.configuration.file.FileConfiguration;
/*     */ import org.bukkit.entity.Player;
/*     */ import org.bukkit.plugin.Plugin;
/*     */ import org.bukkit.plugin.PluginManager;
/*     */ import org.bukkit.plugin.java.JavaPlugin;
/*     */ import org.bukkit.potion.PotionEffect;
/*     */ import org.bukkit.potion.PotionEffectType;
/*     */ import org.bukkit.scheduler.BukkitRunnable;
/*     */ 
/*     */ public class Main extends JavaPlugin
/*     */ {
/*     */   private static final String adminPerm = "BetterDoubleJump.Admin";
/*     */   private static final String jumpPerm = "BetterDoubleJump.Jump";
/*     */   private static final String disablePerm = "BetterDoubleJump.Disable";
/*     */   private static final String disableOtherPerm = "BetterDoubleJump.DisableOther";
/*     */   private static final String foodBypassPerm = "BetterDoubleJump.FoodBypass";
/*  36 */   static ArrayList<UUID> disabled = new ArrayList();
/*     */ 
/*  38 */   static ArrayList<UUID> wasInside = new ArrayList();
/*     */   static Main pl;
/*  42 */   Events events = new Events();
/*     */ 
/*  44 */   static double ySpeed = 0.1D;
/*  45 */   static double directionSpeed = 0.03D;
/*  46 */   static double directionJump = 0.1D;
/*  47 */   static int ticks = 5;
/*     */ 
/*  49 */   static ArrayList<CustomSound> sounds = new ArrayList();
/*     */ 
/*  51 */   static ArrayList<StoredEffect> effects = new ArrayList();
/*     */ 
/*  53 */   static ArrayList<String> disabledWorlds = new ArrayList();
/*     */ 
/*  55 */   static ArrayList<UUID> jumping = new ArrayList();
/*     */ 
/*  57 */   static ArrayList<String> wgRegions = new ArrayList();
/*  58 */   static boolean disableWGRegions = false;
/*  59 */   static WorldGuardPlugin wgPlugin = null;
/*     */ 
/*  61 */   static HashMap<Player, Float> fallDistance = new HashMap();
/*     */ 
/*  63 */   static FallMode fallMode = FallMode.NONE;
/*     */   static String bukkitPackage;
/*     */   static String nmsPackage;
/*     */   static String version;
/*     */ 
/*     */   public void onEnable()
/*     */   {
/*  74 */     pl = this;
/*     */ 
/*  76 */     bukkitPackage = getServer().getClass().getPackage().getName();
/*  77 */     version = bukkitPackage.substring(bukkitPackage.lastIndexOf('.') + 1);
/*     */ 
/*  79 */     nmsPackage = "net.minecraft.server." + version;
/*  80 */     getLogger().info("Doing version check...");
/*  81 */     if (isVersionSupported()) {
/*  82 */       getLogger().info("Version Supported");
/*     */     } else {
/*  84 */       getLogger().warning("######################################################################################");
/*  85 */       getLogger().warning("# This version is not supported, please inform the author xGamingDudex at bukkit dev #");
/*  86 */       getLogger().warning("# by making a comment at http://dev.bukkit.org/bukkit-plugins/betterdoublejump/      #");
/*  87 */       getLogger().warning("#------------------------------------------------------------------------------------#");
/*  88 */       getLogger().warning("# This plugin will now disable...                                                    #");
/*  89 */       getLogger().warning("######################################################################################");
/*  90 */       setEnabled(false);
/*  91 */       return;
/*     */     }
/*     */ 
/*  94 */     Bukkit.getPluginManager().registerEvents(this.events, this);
/*     */ 
/*  96 */     saveDefaultConfig();
/*     */ 
/*  98 */     reloadConfigFile();
/*  99 */     getCommand("bdj").setExecutor(new CmdHandler());
/*     */ 
/* 101 */     new BukkitRunnable() {
/*     */       public void run() {
/* 103 */         for (Player p : Bukkit.getOnlinePlayers())
/*     */         {
/* 105 */           if (Main.this.getConfig().getBoolean("RunActiveCheck"))
/*     */           {
/* 107 */             if ((!Main.isDisabled(p)) && 
/* 108 */               (!Main.jumping.contains(p.getUniqueId())))
/*     */             {
/* 110 */               if ((p.getGameMode() == GameMode.ADVENTURE) || (p.getGameMode() == GameMode.SURVIVAL)) {
/* 111 */                 p.setAllowFlight((Main.canJumpInside(p.getLocation())) && (p.getFoodLevel() >= Main.this.getConfig().getInt("Food.Min")) && (Main.hasJumpPerm(p)));
/*     */               }
/*     */ 
/*     */             }
/*     */ 
/*     */           }
/* 117 */           else if ((Main.isWorldEnabled(p.getWorld())) && (Main.hasJumpPerm(p)))
/*     */           {
/* 120 */             if ((!Main.jumping.contains(p.getUniqueId())) && 
/* 121 */               (!Main.disabled.contains(p.getUniqueId())))
/*     */             {
/* 123 */               boolean wasInside = Main.wasInside.contains(p.getUniqueId());
/* 124 */               boolean isInside = Main.isInside(p.getLocation());
/* 125 */               if (Main.wgPlugin == null) {
/* 126 */                 p.setAllowFlight(true);
/*     */               }
/* 129 */               else if (wasInside) {
/* 130 */                 if (!isInside) {
/* 131 */                   p.setAllowFlight(Main.disableWGRegions);
/* 132 */                   Main.wasInside.remove(p.getUniqueId());
/*     */                 }
/*     */               }
/* 135 */               else if (isInside) {
/* 136 */                 p.setAllowFlight(!Main.disableWGRegions);
/* 137 */                 Main.wasInside.add(p.getUniqueId());
/*     */               }
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/* 143 */     .runTaskTimer(this, 0L, 5L);
/*     */ 
/* 145 */     new BukkitRunnable() {
/*     */       public void run() {
/* 147 */         for (Player p : Bukkit.getOnlinePlayers())
/* 148 */           if (p.getAllowFlight()) {
/* 149 */             float d = p.getFallDistance();
/* 150 */             if (d == 0.0F) {
/* 151 */               Float prev = (Float)Main.fallDistance.get(p);
/* 152 */               if ((prev != null) && (prev.floatValue() > 0.0F)) {
/* 153 */                 Main.this.fallDMG(p, prev.floatValue());
/*     */               }
/*     */             }
/* 156 */             Main.fallDistance.put(p, Float.valueOf(d));
/*     */           }
/*     */       }
/*     */     }
/* 159 */     .runTaskTimer(pl, 0L, 1L);
/*     */     try
/*     */     {
/* 162 */       MetricsLite metrics = new MetricsLite(this);
/* 163 */       metrics.start();
/*     */     }
/*     */     catch (IOException localIOException) {
/*     */     }
/* 167 */     new BukkitRunnable() {
/*     */       public void run() {
/* 169 */         if (!Main.pl.getConfig().getBoolean("CheckForUpdates")) return;
/* 170 */         int id = 84484;
/* 171 */         Updater updater = new Updater(Main.pl, id, Main.pl.getFile(), Updater.UpdateType.DEFAULT, false);
/* 172 */         if (updater.getResult() == Updater.UpdateResult.SUCCESS) {
/* 173 */           Main.pl.getLogger().info("Successfully updated to version '" + updater.getLatestName() + "'");
/* 174 */           Main.pl.getLogger().info("Please reload or restart the server of the update to take affect");
/* 175 */           cancel();
/*     */         }
/*     */       }
/*     */     }
/* 178 */     .runTaskTimerAsynchronously(pl, 0L, 36000L);
/*     */   }
/*     */ 
/*     */   boolean isVersionSupported()
/*     */   {
/*     */     try
/*     */     {
/* 185 */       Class cp = Class.forName(bukkitPackage + ".entity.CraftPlayer");
/* 186 */       Class returnHandle = cp.getMethod("getHandle", null).getReturnType();
/* 187 */       Class nmsPlayer = Class.forName(nmsPackage + ".EntityPlayer");
/* 188 */       if (!returnHandle.equals(nmsPlayer)) return false;
/* 189 */       Class ds = Class.forName(nmsPackage + ".DamageSource");
/* 190 */       ds.getDeclaredField("FALL");
/* 191 */       nmsPlayer.getMethod("damageEntity", new Class[] { ds, Float.TYPE });
/*     */ 
/* 206 */       return true;
/*     */     } catch (Exception e) {
/* 208 */       e.printStackTrace();
/* 209 */     }return false;
/*     */   }
/*     */ 
/*     */   void fallDMG(Player p, float d)
/*     */   {
/* 215 */     if (jumping.contains(p.getUniqueId())) return;
/* 216 */     if ((p.getLocation().getBlock().getRelative(BlockFace.DOWN).getType() == Material.WATER) || 
/* 217 */       (p.getLocation().getBlock().getType() == Material.WATER)) return;
/* 218 */     if ((p.getLocation().getBlock().getRelative(BlockFace.DOWN).getType() == Material.STATIONARY_WATER) || 
/* 219 */       (p.getLocation().getBlock().getType() == Material.STATIONARY_WATER)) return;
/* 220 */     if ((p.getGameMode() == GameMode.CREATIVE) || (p.getGameMode() == GameMode.SPECTATOR)) return;
/*     */ 
/* 222 */     float f = 0.0F;
/* 223 */     for (PotionEffect ef : p.getActivePotionEffects()) {
/* 224 */       if (ef.getType() == PotionEffectType.JUMP) {
/* 225 */         f = ef.getAmplifier() + 1;
/* 226 */         break;
/*     */       }
/*     */     }
/*     */ 
/* 230 */     d = d - 3.0F - f;
/*     */ 
/* 232 */     int dmg = (int)d;
/* 233 */     if (d > dmg) dmg++;
/*     */ 
/* 235 */     if (dmg <= 0) return; try
/*     */     {
/* 237 */       Object ep = p.getClass().getMethod("getHandle", null).invoke(p, null);
/* 238 */       Class ds = Class.forName(nmsPackage + ".DamageSource");
/* 239 */       Object dmgfall = ds.getDeclaredField("FALL").get(null);
/* 240 */       ep.getClass().getMethod("damageEntity", new Class[] { ds, Float.TYPE }).invoke(ep, new Object[] { dmgfall, Integer.valueOf(dmg) });
/*     */     } catch (Exception e) {
/* 242 */       e.printStackTrace();
/*     */     }
/*     */   }
/*     */ 
/*     */   void reloadConfigFile()
/*     */   {
/* 249 */     FileConfiguration c = getConfig();
/* 250 */     ySpeed = c.getDouble("Ymultiplier");
/* 251 */     directionSpeed = c.getDouble("DirectionMultiplier");
/* 252 */     directionJump = c.getDouble("JumpMultiplier");
/* 253 */     ticks = c.getInt("Ticks");
/*     */ 
/* 255 */     String sounds = c.getString("Sounds");
/* 256 */     String effects = c.getString("Effects");
/* 257 */     sounds.clear();
/* 258 */     effects.clear();
/*     */ 
/* 260 */     disabledWorlds.clear();
/* 261 */     disabledWorlds.addAll(Arrays.asList(c.getString("DisabledWorlds").split(",")));
/*     */ 
/* 263 */     wgRegions.clear();
/* 264 */     wgRegions.addAll(Arrays.asList(c.getString("WorldGuardRegions").split(",")));
/*     */ 
/* 266 */     disableWGRegions = c.getBoolean("DisableWGRegions");
/*     */     try
/*     */     {
/* 269 */       fallMode = FallMode.valueOf(c.getString("FallMode").toUpperCase());
/*     */     } catch (Exception e) {
/* 271 */       getLogger().warning("Unknown fall mode: " + c.getString("FallMode"));
/* 272 */       getLogger().warning("Using default mode instead (NONE)");
/* 273 */       fallMode = FallMode.NONE;
/*     */     }
/*     */ 
/* 276 */     for (String sound : sounds.split(",")) {
/*     */       try {
/* 278 */         String[] args = sound.split("-");
/* 279 */         sounds.add(new CustomSound(Sound.valueOf(args[0].toUpperCase()), Float.parseFloat(args[1]), Float.parseFloat(args[2])));
/*     */       } catch (Exception e) {
/* 281 */         getLogger().warning("Unable to parse sound: " + sound);
/*     */       }
/*     */     }
/*     */ 
/* 285 */     for (String effect : effects.split(",")) {
/*     */       try {
/* 287 */         String[] args = effect.split("-");
/* 288 */         float spread = Float.parseFloat(args[1]);
/* 289 */         int id = 0;
/* 290 */         int data = 0;
/* 291 */         if (args.length == 6) {
/* 292 */           id = Integer.parseInt(args[4]);
/* 293 */           data = Integer.parseInt(args[5]);
/*     */         }
/* 295 */         StoredEffect e = new StoredEffect(args[0].toUpperCase(), spread, spread, spread, Float.parseFloat(args[3]), Integer.parseInt(args[2]), id, data);
/*     */ 
/* 301 */         effects.add(e);
/*     */       } catch (Exception e) {
/* 303 */         getLogger().warning("Unable to parse effect: " + effect + "; " + e.getMessage());
/*     */       }
/*     */     }
/*     */ 
/* 307 */     Plugin wg = Bukkit.getPluginManager().getPlugin("WorldGuard");
/* 308 */     if ((wg != null) && (wg.isEnabled()) && ((wg instanceof WorldGuardPlugin)))
/* 309 */       wgPlugin = (WorldGuardPlugin)wg;
/*     */     else
/* 311 */       wgPlugin = null;
/*     */   }
/*     */ 
/*     */   static boolean canJumpInside(Location loc)
/*     */   {
/* 316 */     if (wgPlugin == null) return true;
/*     */ 
/* 318 */     String w = loc.getWorld().getName();
/*     */ 
/* 320 */     if (!isWorldEnabled(w)) return false;
/*     */ 
/* 322 */     return isInside(loc) ^ disableWGRegions;
/*     */   }
/*     */ 
/*     */   static boolean isInside(Location loc) {
/* 326 */     if (loc == null) return false;
/* 327 */     if (wgPlugin == null) return false;
/* 328 */     boolean inside = false;
/*     */     try {
/* 330 */       for (String r : wgPlugin.getRegionManager(loc.getWorld()).getApplicableRegionsIDs(BukkitUtil.toVector(loc))) {
/* 331 */         if (wgRegions.contains(r)) {
/* 332 */           inside = true;
/* 333 */           break;
/*     */         }
/*     */       }
/* 336 */       return inside; } catch (Exception e) {
/*     */     }
/* 338 */     return false;
/*     */   }
/*     */ 
/*     */   static boolean isWorldEnabled(World world)
/*     */   {
/* 343 */     return isWorldEnabled(world.getName());
/*     */   }
/*     */ 
/*     */   static boolean isWorldEnabled(String world) {
/* 347 */     return disabledWorlds.contains(world) == pl.getConfig().getBoolean("OnlyDisabledWorlds");
/*     */   }
/*     */ 
/*     */   static boolean hasAdminPerm(CommandSender p) {
/* 351 */     if (p == null) return false;
/* 352 */     return p.hasPermission("BetterDoubleJump.Admin");
/*     */   }
/*     */ 
/*     */   static boolean hasDisablePerm(CommandSender p) {
/* 356 */     if (p == null) return false;
/* 357 */     return (hasAdminPerm(p)) || (hasDisableOtherPerm(p)) || (p.hasPermission("BetterDoubleJump.Disable"));
/*     */   }
/*     */ 
/*     */   static boolean hasDisableOtherPerm(CommandSender p) {
/* 361 */     if (p == null) return false;
/* 362 */     return (hasAdminPerm(p)) || (p.hasPermission("BetterDoubleJump.DisableOther"));
/*     */   }
/*     */ 
/*     */   static boolean hasJumpPerm(CommandSender p) {
/* 366 */     if (p == null) return false;
/* 367 */     return (hasAdminPerm(p)) || (p.hasPermission("BetterDoubleJump.Jump"));
/*     */   }
/*     */ 
/*     */   static boolean isDisabled(Player p) {
/* 371 */     if (p == null) return false;
/* 372 */     return disabled.contains(p.getUniqueId());
/*     */   }
/*     */ 
/*     */   static boolean payFood(Player p) {
/* 376 */     if ((hasAdminPerm(p)) || (p.hasPermission("BetterDoubleJump.FoodBypass"))) {
/* 377 */       return true;
/*     */     }
/* 379 */     if (p.getFoodLevel() < pl.getConfig().getInt("Food.Min")) {
/* 380 */       return false;
/*     */     }
/* 382 */     int food = p.getFoodLevel();
/* 383 */     food -= pl.getConfig().getInt("Food.Cost");
/* 384 */     if (food < 0) food = 0;
/* 385 */     if (food > 20) food = 20;
/* 386 */     p.setFoodLevel(food);
/* 387 */     return true;
/*     */   }
/*     */ 
/*     */   static enum FallMode
/*     */   {
/*  70 */     NONE_GLOBAL, NONE, VANILLA, RESET, RESET_NONE;
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\BetterDoubleJump.jar
 * Qualified Name:     me.xADudex.BDJ.Main
 * JD-Core Version:    0.6.2
 */