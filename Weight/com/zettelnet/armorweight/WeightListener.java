/*     */ package com.zettelnet.armorweight;
/*     */ 
/*     */ import java.util.logging.Logger;
/*     */ import org.bukkit.entity.Horse;
/*     */ import org.bukkit.entity.Player;
/*     */ import org.bukkit.event.EventHandler;
/*     */ import org.bukkit.event.EventPriority;
/*     */ import org.bukkit.event.Listener;
/*     */ import org.bukkit.event.inventory.InventoryClickEvent;
/*     */ import org.bukkit.event.player.PlayerChangedWorldEvent;
/*     */ import org.bukkit.event.player.PlayerGameModeChangeEvent;
/*     */ import org.bukkit.event.player.PlayerInteractEvent;
/*     */ import org.bukkit.event.player.PlayerJoinEvent;
/*     */ import org.bukkit.event.player.PlayerQuitEvent;
/*     */ import org.bukkit.event.player.PlayerRespawnEvent;
/*     */ import org.bukkit.event.vehicle.VehicleEnterEvent;
/*     */ import org.bukkit.event.vehicle.VehicleExitEvent;
/*     */ import org.bukkit.inventory.Inventory;
/*     */ import org.bukkit.inventory.InventoryHolder;
/*     */ import org.bukkit.plugin.Plugin;
/*     */ 
/*     */ public class WeightListener
/*     */   implements Listener
/*     */ {
/*     */   private WeightManager manager;
/*     */ 
/*     */   public WeightListener(WeightManager manager)
/*     */   {
/*  24 */     this.manager = manager;
/*     */   }
/*     */ 
/*     */   public WeightManager getManager() {
/*  28 */     return this.manager;
/*     */   }
/*     */ 
/*     */   public void setManager(WeightManager manager) {
/*  32 */     this.manager = manager;
/*     */   }
/*     */ 
/*     */   @EventHandler(priority=EventPriority.MONITOR)
/*     */   public void onPlayerJoin(PlayerJoinEvent event) {
/*  37 */     Player player = event.getPlayer();
/*  38 */     if (this.manager.isWorldEnabled(player.getWorld()))
/*  39 */       this.manager.loadPlayer(player);
/*     */   }
/*     */ 
/*     */   @EventHandler(priority=EventPriority.MONITOR)
/*     */   public void onPlayerQuit(PlayerQuitEvent event)
/*     */   {
/*  45 */     Player player = event.getPlayer();
/*  46 */     this.manager.unloadPlayer(player);
/*     */   }
/*     */ 
/*     */   @EventHandler(priority=EventPriority.MONITOR)
/*     */   public void onPlayerRespawn(PlayerRespawnEvent event) {
/*  51 */     Player player = event.getPlayer();
/*  52 */     this.manager.updateWeight(player);
/*     */   }
/*     */ 
/*     */   @EventHandler(priority=EventPriority.MONITOR)
/*     */   public void onPlayerChangedWorld(PlayerChangedWorldEvent event) {
/*  57 */     Player player = event.getPlayer();
/*  58 */     if (this.manager.isWorldEnabled(player.getWorld()))
/*  59 */       this.manager.loadPlayer(player);
/*     */     else
/*  61 */       this.manager.unloadPlayer(player);
/*     */   }
/*     */ 
/*     */   @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=false)
/*     */   public void onPlayerInteract(PlayerInteractEvent event)
/*     */   {
/*  67 */     Player player = event.getPlayer();
/*  68 */     this.manager.updateWeightLater(player);
/*     */   }
/*     */ 
/*     */   @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
/*     */   public void onInventoryClick(InventoryClickEvent event) {
/*  73 */     InventoryHolder holder = event.getInventory().getHolder();
/*  74 */     if ((holder instanceof Player)) {
/*  75 */       this.manager.updateWeightLater((Player)holder);
/*     */     }
/*  77 */     if (((holder instanceof Horse)) && (this.manager.isHorseLoaded((Horse)holder))) {
/*  78 */       Horse horse = (Horse)holder;
/*  79 */       this.manager.updateWeightLater(horse);
/*     */     }
/*     */   }
/*     */ 
/*     */   @EventHandler(priority=EventPriority.LOW, ignoreCancelled=true)
/*     */   public void onInventoryClickPortableHorsesFix(InventoryClickEvent event) {
/*  85 */     if (!this.manager.isPortableHorsesEnabled()) {
/*  86 */       return;
/*     */     }
/*  88 */     InventoryHolder holder = event.getInventory().getHolder();
/*  89 */     if ((holder instanceof Player)) {
/*  90 */       this.manager.updateEffects((Player)holder);
/*     */     }
/*  92 */     Player player = (Player)event.getWhoClicked();
/*  93 */     if ((!(holder instanceof Horse)) || (!this.manager.isHorseLoaded((Horse)holder))) {
/*  94 */       return;
/*     */     }
/*  96 */     Horse horse = (Horse)holder;
/*     */     try {
/*  98 */       if (this.manager.isKickedOffHorse(player, horse, event.getCurrentItem()))
/*  99 */         this.manager.unloadHorse(horse, null);
/*     */     }
/*     */     catch (Exception e) {
/* 102 */       this.manager.getPlugin().getLogger().warning("Failed to perform PortableHorses fix:");
/* 103 */       e.printStackTrace();
/*     */     }
/*     */   }
/*     */ 
/*     */   @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
/*     */   public void onVehicleEnter(VehicleEnterEvent event) {
/* 109 */     if ((event.getVehicle() instanceof Horse))
/* 110 */       this.manager.loadHorse((Horse)event.getVehicle(), event.getEntered());
/*     */   }
/*     */ 
/*     */   @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
/*     */   public void onVehicleExit(VehicleExitEvent event)
/*     */   {
/* 116 */     if ((event.getVehicle() instanceof Horse))
/* 117 */       this.manager.unloadHorse((Horse)event.getVehicle(), null);
/*     */   }
/*     */ 
/*     */   @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
/*     */   public void onPlayerGameModeChange(PlayerGameModeChangeEvent event)
/*     */   {
/* 123 */     Player player = event.getPlayer();
/* 124 */     this.manager.updateWeightLater(player);
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\ArmorWeight.jar
 * Qualified Name:     com.zettelnet.armorweight.WeightListener
 * JD-Core Version:    0.6.2
 */