/*     */ package com.gmail.filoghost.healthbar;
/*     */ 
/*     */ import com.gmail.filoghost.healthbar.api.HealthBarAPI;
/*     */ import org.apache.commons.lang.WordUtils;
/*     */ import org.bukkit.configuration.file.FileConfiguration;
/*     */ import org.bukkit.entity.Entity;
/*     */ import org.bukkit.entity.EntityType;
/*     */ import org.bukkit.entity.LivingEntity;
/*     */ import org.bukkit.entity.Player;
/*     */ import org.bukkit.entity.Projectile;
/*     */ import org.bukkit.event.EventHandler;
/*     */ import org.bukkit.event.EventPriority;
/*     */ import org.bukkit.event.Listener;
/*     */ import org.bukkit.event.entity.EntityDamageByEntityEvent;
/*     */ import org.bukkit.event.entity.EntityDamageEvent;
/*     */ import org.bukkit.event.entity.PlayerDeathEvent;
/*     */ import org.bukkit.inventory.ItemStack;
/*     */ import org.bukkit.inventory.meta.ItemMeta;
/*     */ 
/*     */ public class DeathListener
/*     */   implements Listener
/*     */ {
/*     */   private static boolean wantDeathListener;
/*     */ 
/*     */   @EventHandler(priority=EventPriority.LOW)
/*     */   public void onPlayerDeathEvent(PlayerDeathEvent event)
/*     */   {
/*  24 */     if (!wantDeathListener) return; try
/*     */     {
/*  26 */       String deathMessage = event.getDeathMessage();
/*  27 */       String victim = event.getEntity().getName();
/*  28 */       EntityDamageEvent damageEvent = event.getEntity().getLastDamageCause();
/*  29 */       if ((damageEvent instanceof EntityDamageByEntityEvent)) {
/*  30 */         Entity damager = ((EntityDamageByEntityEvent)damageEvent).getDamager();
/*     */ 
/*  33 */         if ((deathMessage.contains("killed")) || (deathMessage.contains("slain")) || (deathMessage.contains("got finished"))) {
/*  34 */           if ((damager instanceof Player)) {
/*  35 */             String itemname = ((Player)damager).getItemInHand().getItemMeta().getDisplayName();
/*  36 */             if (itemname == null) {
/*  37 */               event.setDeathMessage(victim + " was slain by " + ((Player)damager).getName());
/*  38 */               return;
/*     */             }
/*  40 */             event.setDeathMessage(victim + " was slain by " + ((Player)damager).getName() + " using " + itemname);
/*  41 */             return;
/*     */           }
/*     */ 
/*  45 */           if ((damager instanceof LivingEntity)) {
/*  46 */             event.setDeathMessage(victim + " was slain by " + getName((LivingEntity)damager));
/*  47 */             return;
/*     */           }
/*     */         }
/*     */ 
/*  51 */         if (deathMessage.contains("blown up")) {
/*  52 */           if ((damager instanceof Player)) {
/*  53 */             event.setDeathMessage(victim + " was blown up by " + ((Player)damager).getName());
/*  54 */             return;
/*     */           }
/*  56 */           if ((damager instanceof LivingEntity)) {
/*  57 */             event.setDeathMessage(victim + " was blown up by " + getName((LivingEntity)damager));
/*  58 */             return;
/*     */           }
/*     */         }
/*     */ 
/*  62 */         if (((deathMessage.contains("shot")) || (deathMessage.contains("shooted"))) && 
/*  63 */           ((damager instanceof Projectile))) {
/*  64 */           LivingEntity shooter = ((Projectile)damager).getShooter();
/*  65 */           if ((shooter instanceof Player)) {
/*  66 */             String itemname = ((Player)shooter).getItemInHand().getItemMeta().getDisplayName();
/*  67 */             if (itemname == null) {
/*  68 */               event.setDeathMessage(victim + " was shot by " + ((Player)shooter).getName());
/*  69 */               return;
/*     */             }
/*  71 */             event.setDeathMessage(victim + " was shot by " + ((Player)shooter).getName() + " using " + itemname);
/*  72 */             return;
/*     */           }
/*     */ 
/*  75 */           if ((shooter instanceof LivingEntity)) {
/*  76 */             event.setDeathMessage(victim + " was shot by " + getName(shooter));
/*  77 */             return;
/*     */           }
/*     */ 
/*     */         }
/*     */ 
/*  82 */         if ((deathMessage.contains("fireballed")) && 
/*  83 */           ((damager instanceof Projectile))) {
/*  84 */           LivingEntity shooter = ((Projectile)damager).getShooter();
/*  85 */           if ((shooter instanceof Player)) {
/*  86 */             event.setDeathMessage(victim + " was fireballed by " + ((Player)shooter).getName());
/*  87 */             return;
/*     */           }
/*  89 */           if ((shooter instanceof LivingEntity)) {
/*  90 */             event.setDeathMessage(victim + "was fireballed by " + getName(shooter));
/*  91 */             return;
/*     */           }
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/*  97 */       if ((deathMessage.contains("high place")) || (deathMessage.contains("doomed to fall")) || (deathMessage.contains("fell off")) || (deathMessage.contains("fell out of the water"))) {
/*  98 */         event.setDeathMessage(victim + " fell from a high place");
/*  99 */         return;
/* 100 */       }if (deathMessage.contains("lava")) {
/* 101 */         event.setDeathMessage(victim + " tried to swim in lava");
/* 102 */         return;
/* 103 */       }if (deathMessage.contains("blew up")) {
/* 104 */         event.setDeathMessage(victim + " blew up");
/* 105 */         return;
/* 106 */       }if ((deathMessage.contains("burned")) || (deathMessage.contains("crisp"))) {
/* 107 */         event.setDeathMessage(victim + " was burned to death");
/* 108 */         return;
/* 109 */       }if ((deathMessage.contains("flames")) || (deathMessage.contains("fire"))) {
/* 110 */         event.setDeathMessage(victim + " went up in flames");
/* 111 */         return;
/* 112 */       }if (deathMessage.contains("drowned")) {
/* 113 */         event.setDeathMessage(victim + " drowned");
/* 114 */         return;
/* 115 */       }if ((deathMessage.contains("shooted")) || (deathMessage.contains("shot"))) {
/* 116 */         event.setDeathMessage(victim + " was shot by an arrow");
/* 117 */         return;
/* 118 */       }if (deathMessage.contains("wall")) {
/* 119 */         event.setDeathMessage(victim + " suffucated in a wall");
/* 120 */         return;
/* 121 */       }if (deathMessage.contains("starved")) {
/* 122 */         event.setDeathMessage(victim + " starved to death");
/* 123 */         return;
/* 124 */       }if (deathMessage.contains("magic")) {
/* 125 */         event.setDeathMessage(victim + " was killed by magic");
/* 126 */         return;
/* 127 */       }if (deathMessage.contains("fireball")) {
/* 128 */         event.setDeathMessage(victim + " was fireballed");
/* 129 */         return;
/* 130 */       }if ((deathMessage.contains("pricked")) || (deathMessage.contains("cactus")) || (deathMessage.contains("cacti"))) {
/* 131 */         event.setDeathMessage(victim + " was pricked to death");
/* 132 */         return;
/* 133 */       }if (deathMessage.contains("world")) {
/* 134 */         event.setDeathMessage(victim + " fell out of the world");
/* 135 */         return;
/* 136 */       }if (deathMessage.contains("squashed")) {
/* 137 */         event.setDeathMessage(victim + " was squashed by a falling anvil");
/* 138 */         return;
/* 139 */       }event.setDeathMessage(victim + " died");
/* 140 */       return;
/*     */     }
/*     */     catch (Exception e) {
/* 143 */       event.setDeathMessage(event.getEntity().getName() + " died");
/*     */     }
/*     */   }
/*     */ 
/*     */   private String getName(LivingEntity mob) {
/* 148 */     String customName = HealthBarAPI.getMobName(mob);
/* 149 */     if (customName != null) return customName;
/*     */ 
/* 151 */     if (mob.getType() == EntityType.PIG_ZOMBIE) return "Zombie Pigman";
/* 152 */     if (mob.getType() == EntityType.MUSHROOM_COW) return "Mooshroom";
/* 153 */     return WordUtils.capitalizeFully(mob.getType().toString().replace("_", " "));
/*     */   }
/*     */ 
/*     */   public static void loadConfiguration() {
/* 157 */     wantDeathListener = Main.plugin.getConfig().getBoolean(Configuration.Nodes.FIX_DEATH_MESSAGES.getNode());
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\HealthBar.jar
 * Qualified Name:     com.gmail.filoghost.healthbar.DeathListener
 * JD-Core Version:    0.6.2
 */