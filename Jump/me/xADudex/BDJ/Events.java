/*     */ package me.xADudex.BDJ;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.UUID;
/*     */ import org.bukkit.GameMode;
/*     */ import org.bukkit.Location;
/*     */ import org.bukkit.World;
/*     */ import org.bukkit.entity.Player;
/*     */ import org.bukkit.event.EventHandler;
/*     */ import org.bukkit.event.Listener;
/*     */ import org.bukkit.event.entity.EntityDamageEvent;
/*     */ import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
/*     */ import org.bukkit.event.player.PlayerGameModeChangeEvent;
/*     */ import org.bukkit.event.player.PlayerPortalEvent;
/*     */ import org.bukkit.event.player.PlayerQuitEvent;
/*     */ import org.bukkit.event.player.PlayerTeleportEvent;
/*     */ import org.bukkit.event.player.PlayerToggleFlightEvent;
/*     */ import org.bukkit.scheduler.BukkitRunnable;
/*     */ import org.bukkit.util.Vector;
/*     */ 
/*     */ public class Events
/*     */   implements Listener
/*     */ {
/*     */   @EventHandler
/*     */   public void onFly(PlayerToggleFlightEvent event)
/*     */   {
/*  26 */     final Player p = event.getPlayer();
/*  27 */     if ((Main.hasJumpPerm(p)) && (p.getGameMode() != GameMode.CREATIVE) && (!Main.isDisabled(p)) && (!Main.jumping.contains(p.getUniqueId()))) {
/*  28 */       if ((!Main.canJumpInside(p.getLocation())) || (!Main.payFood(p))) {
/*  29 */         event.setCancelled(true);
/*  30 */         return;
/*     */       }
/*  32 */       Vector jump = p.getLocation().getDirection();
/*  33 */       double y = jump.getY() + Main.ySpeed * Main.directionJump;
/*  34 */       jump.multiply(Main.directionJump);
/*  35 */       jump.setY(y);
/*  36 */       p.setVelocity(jump);
/*  37 */       playJumpEffect(p);
/*  38 */       Main.jumping.add(p.getUniqueId());
/*  39 */       if (Main.fallMode == Main.FallMode.RESET) {
/*  40 */         p.setFallDistance(0.0F);
/*     */       }
/*  42 */       new BukkitRunnable() {
/*  43 */         int count = Main.ticks;
/*  44 */         boolean pos = true;
/*     */ 
/*  46 */         public void run() { if ((p.getGameMode() == GameMode.CREATIVE) || (!p.isOnline()) || (!Main.jumping.contains(p.getUniqueId())) || (Main.isDisabled(p)) || 
/*  47 */             (!Main.canJumpInside(p.getLocation()))) {
/*  48 */             new BukkitRunnable() {
/*     */               public void run() {
/*  50 */                 Main.jumping.remove(this.val$p.getUniqueId());
/*     */               }
/*     */             }
/*  52 */             .runTaskLater(Main.pl, 5L);
/*  53 */             cancel();
/*  54 */             return;
/*     */           }
/*     */ 
/*  70 */           if (p.getFallDistance() > 0.0F) {
/*  71 */             this.pos = false;
/*     */           }
/*  73 */           if ((p.getFallDistance() <= 0.0F) && 
/*  74 */             (!this.pos)) {
/*  75 */             new BukkitRunnable() {
/*     */               public void run() {
/*  77 */                 Main.jumping.remove(this.val$p.getUniqueId());
/*  78 */                 this.val$p.setFlying(false);
/*  79 */                 this.val$p.setAllowFlight(true);
/*     */               }
/*     */             }
/*  81 */             .runTaskLater(Main.pl, 5L);
/*  82 */             cancel();
/*  83 */             return;
/*     */           }
/*     */ 
/*  86 */           if (this.count <= 0) {
/*  87 */             p.setFlying(false);
/*  88 */             p.setAllowFlight(false);
/*  89 */             return;
/*     */           }
/*  91 */           this.count -= 1;
/*  92 */           Vector v = p.getVelocity();
/*  93 */           v.add(p.getLocation().getDirection().multiply(Main.directionSpeed));
/*  94 */           v.setY(v.getY() * Main.ySpeed);
/*  95 */           p.setVelocity(v); }
/*     */       }
/*  97 */       .runTaskTimer(Main.pl, 0L, 1L);
/*     */     }
/*     */   }
/*     */ 
/*     */   @EventHandler
/*     */   public void onGameModeChange(PlayerGameModeChangeEvent event) {
/* 103 */     final Player p = event.getPlayer();
/* 104 */     if ((event.getNewGameMode() != GameMode.CREATIVE) && (!Main.isDisabled(p)) && (event.getNewGameMode() != GameMode.SPECTATOR)) {
/* 105 */       final boolean en = (Main.canJumpInside(p.getLocation())) && (!Main.isWorldEnabled(event.getPlayer().getWorld()));
/* 106 */       final boolean rem = (en) && (!Main.jumping.contains(p.getUniqueId()));
/* 107 */       if (rem) {
/* 108 */         new BukkitRunnable() {
/*     */           public void run() {
/* 110 */             if (rem)
/* 111 */               Main.jumping.remove(p.getUniqueId());
/*     */           }
/*     */         }
/* 114 */         .runTaskLater(Main.pl, 5L);
/* 115 */         Main.jumping.add(event.getPlayer().getUniqueId());
/*     */       }
/* 117 */       new BukkitRunnable() {
/*     */         public void run() {
/* 119 */           p.setAllowFlight(en);
/*     */         }
/*     */       }
/* 121 */       .runTaskLater(Main.pl, 1L);
/*     */     }
/*     */   }
/*     */ 
/*     */   @EventHandler
/*     */   public void onDamage(EntityDamageEvent event) {
/* 127 */     if ((event.getCause() == EntityDamageEvent.DamageCause.FALL) && 
/* 128 */       ((event.getEntity() instanceof Player))) {
/* 129 */       Player p = (Player)event.getEntity();
/* 130 */       boolean dis = Main.isDisabled(p);
/* 131 */       if ((dis) && (p.getAllowFlight())) event.setCancelled(true);
/* 132 */       if (dis) return;
/*     */ 
/* 134 */       if (Main.fallMode == Main.FallMode.NONE_GLOBAL)
/* 135 */         event.setCancelled(true);
/* 136 */       else if (Main.fallMode == Main.FallMode.NONE)
/* 137 */         event.setCancelled((Main.canJumpInside(p.getLocation())) && (Main.isWorldEnabled(p.getWorld().getName())) && (Main.hasJumpPerm(p)));
/* 138 */       else if ((Main.fallMode == Main.FallMode.RESET_NONE) && 
/* 139 */         (Main.jumping.contains(((Player)event.getEntity()).getUniqueId())))
/* 140 */         event.setCancelled(true);
/*     */     }
/*     */   }
/*     */ 
/*     */   @EventHandler
/*     */   public void onQuit(PlayerQuitEvent event)
/*     */   {
/* 149 */     UUID id = event.getPlayer().getUniqueId();
/* 150 */     Main.jumping.remove(id);
/* 151 */     Main.wasInside.remove(id);
/* 152 */     Main.disabled.remove(id);
/*     */   }
/*     */ 
/*     */   @EventHandler
/*     */   public void onTeleport(PlayerTeleportEvent event) {
/* 157 */     if ((event.getTo() != null) && (!Main.isDisabled(event.getPlayer()))) {
/* 158 */       if ((event.getPlayer().getGameMode() == GameMode.SPECTATOR) || (event.getPlayer().getGameMode() == GameMode.CREATIVE)) {
/* 159 */         event.getPlayer().setAllowFlight(true);
/* 160 */         return;
/*     */       }
/*     */ 
/* 163 */       boolean from = !Main.isWorldEnabled(event.getFrom().getWorld());
/* 164 */       boolean to = !Main.isWorldEnabled(event.getTo().getWorld());
/* 165 */       if ((!from) && (to))
/* 166 */         event.getPlayer().setAllowFlight(false);
/* 167 */       else if ((!to) && (Main.hasJumpPerm(event.getPlayer())))
/* 168 */         event.getPlayer().setAllowFlight(true);
/* 169 */       else if (((to) || (!Main.hasJumpPerm(event.getPlayer()))) && (!from))
/* 170 */         event.getPlayer().setAllowFlight(false);
/*     */       else
/* 172 */         event.getPlayer().setAllowFlight(true);
/*     */     }
/*     */   }
/*     */ 
/*     */   @EventHandler
/*     */   public void onPortal(PlayerPortalEvent event)
/*     */   {
/* 179 */     onTeleport(event);
/*     */   }
/*     */ 
/*     */   void playJumpEffect(Player p) {
/* 183 */     if (p == null) {
/* 184 */       return;
/*     */     }
/* 186 */     Location l = p.getLocation();
/* 187 */     for (CustomSound c : Main.sounds) {
/* 188 */       c.play(l);
/*     */     }
/*     */ 
/* 193 */     for (StoredEffect e : Main.effects)
/* 194 */       e.play(l);
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\BetterDoubleJump.jar
 * Qualified Name:     me.xADudex.BDJ.Events
 * JD-Core Version:    0.6.2
 */