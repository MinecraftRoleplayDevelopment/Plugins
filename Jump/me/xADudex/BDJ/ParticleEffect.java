/*     */ package me.xADudex.BDJ;
/*     */ 
/*     */ import java.lang.reflect.Constructor;
/*     */ import java.lang.reflect.Field;
/*     */ import java.lang.reflect.Method;
/*     */ import org.bukkit.Location;
/*     */ import org.bukkit.World;
/*     */ import org.bukkit.entity.Player;
/*     */ 
/*     */ public enum ParticleEffect
/*     */ {
/*  11 */   HUGE_EXPLOSION(
/*  15 */     "hugeexplosion", Environment.ANY), 
/*  16 */   LARGE_EXPLODE("largeexplode", Environment.ANY), 
/*  17 */   FIREWORK_SPARK("fireworksSpark", Environment.ANY), 
/*  18 */   TOWN_AURA("townaura", Environment.ANY), 
/*  19 */   CRIT("crit", Environment.ANY), 
/*  20 */   MAGIC_CRIT("magicCrit", Environment.ANY), 
/*  21 */   SMOKE("smoke", Environment.ANY), 
/*  22 */   MOB_SPELL("mobSpell", Environment.ANY), 
/*  23 */   MOB_SPELL_AMBIENT("mobSpellAmbient", Environment.ANY), 
/*  24 */   SPELL("spell", Environment.ANY), 
/*  25 */   INSTANT_SPELL("instantSpell", Environment.ANY), 
/*  26 */   WITCH_MAGIC("witchMagic", Environment.ANY), 
/*  27 */   NOTE("note", Environment.ANY), 
/*  28 */   PORTAL("portal", Environment.ANY), 
/*  29 */   ENCHANTMENT_TABLE("enchantmenttable", Environment.ANY), 
/*  30 */   EXPLODE("explode", Environment.ANY), 
/*  31 */   FLAME("flame", Environment.ANY), 
/*  32 */   LAVA("lava", Environment.ANY), 
/*  33 */   FOOTSTEP("footstep", Environment.ANY), 
/*  34 */   LARGE_SMOKE("largesmoke", Environment.ANY), 
/*  35 */   CLOUD("cloud", Environment.ANY), 
/*  36 */   RED_DUST("reddust", Environment.ANY), 
/*  37 */   SNOWBALL_POOF("snowballpoof", Environment.ANY), 
/*  38 */   DRIP_WATER("dripWater", Environment.ANY), 
/*  39 */   DRIP_LAVA("dripLava", Environment.ANY), 
/*  40 */   SNOW_SHOVEL("snowshovel", Environment.ANY), 
/*  41 */   SLIME("slime", Environment.ANY), 
/*  42 */   HEART("heart", Environment.ANY), 
/*  43 */   ANGRY_VILLAGER("angryVillager", Environment.ANY), 
/*  44 */   HAPPY_VILLAGER("happyVillager", Environment.ANY), 
/*  45 */   ICONCRACK(
/*  46 */     "iconcrack_%id%", Environment.UKNOWN), 
/*  47 */   BLOCK_BREAK("blockcrack_%id%_%data%", Environment.ANY), 
/*  48 */   BLOCK_DUST("blockdust_%id%_%data%", Environment.ANY), 
/*  49 */   SPLASH("splash", Environment.AIR), 
/*  50 */   BUBBLE("bubble", Environment.IN_WATER), 
/*  51 */   SUSPEND("suspended", Environment.UKNOWN), 
/*  52 */   DEPTH_SUSPEND("depthSuspend", Environment.UKNOWN);
/*     */ 
/*     */   private final String packetName;
/*     */   private final Environment environment;
/*     */   private float xStack;
/*     */   private float yStack;
/*     */   private float zStack;
/*     */   private float speed;
/*  58 */   private int _id = 1;
/*  59 */   private int _data = 0;
/*     */   private int count;
/*     */ 
/*  68 */   private ParticleEffect(String packetName, Environment environment) { this.packetName = packetName;
/*  69 */     this.environment = environment;
/*     */   }
/*     */ 
/*     */   public void setStack(float stackXAxis, float stackYAxis, float stackZAxis)
/*     */   {
/*  79 */     this.xStack = stackXAxis;
/*  80 */     this.yStack = stackYAxis;
/*  81 */     this.zStack = stackZAxis;
/*     */   }
/*     */ 
/*     */   public void setSpeedAndCount(int count, float speed) {
/*  85 */     this.speed = speed;
/*  86 */     this.count = count;
/*     */   }
/*     */ 
/*     */   public void setId(int id)
/*     */   {
/*  94 */     this._id = id;
/*     */   }
/*     */ 
/*     */   public void setData(int data)
/*     */   {
/* 102 */     this._data = data;
/*     */   }
/*     */ 
/*     */   public void animateToPlayer(Player player)
/*     */   {
/* 112 */     if (player == null)
/* 113 */       return;
/*     */     try
/*     */     {
/* 116 */       Object ep = player.getClass().getMethod("getHandle", null).invoke(player, null);
/* 117 */       Object packet = getParticle(player.getLocation(), this.xStack, this.yStack, this.zStack, this.speed, this.count);
/* 118 */       Object conn = ep.getClass().getDeclaredField("playerConnection").get(ep);
/* 119 */       conn.getClass().getMethod("sendPacket", new Class[] { packet.getClass().getSuperclass() }).invoke(conn, new Object[] { packet });
/*     */     } catch (Exception e) {
/* 121 */       e.printStackTrace();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void animateAtLocation(Location location)
/*     */   {
/* 140 */     if (location == null)
/* 141 */       return;
/*     */     try
/*     */     {
/* 144 */       for (Player p : location.getWorld().getPlayers())
/*     */       {
/* 146 */         if (p.getLocation().distanceSquared(location) < 110889.0D)
/*     */         {
/* 150 */           Object ep = p.getClass().getMethod("getHandle", null).invoke(p, null);
/* 151 */           Object packet = getParticle(location, this.xStack, this.yStack, this.zStack, this.speed, this.count);
/* 152 */           Object conn = ep.getClass().getDeclaredField("playerConnection").get(ep);
/* 153 */           conn.getClass().getMethod("sendPacket", new Class[] { packet.getClass().getInterfaces()[0] }).invoke(conn, new Object[] { packet });
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/* 162 */       e.printStackTrace();
/*     */     }
/*     */   }
/*     */ 
/*     */   public Environment getEnvironment()
/*     */   {
/* 171 */     return this.environment;
/*     */   }
/*     */ 
/*     */   private Object getParticle(Location location, float offsetX, float offsetY, float offsetZ, float speed, int count)
/*     */     throws Exception
/*     */   {
/* 210 */     Class packet = Class.forName(Main.nmsPackage + ".PacketPlayOutWorldParticles");
/* 211 */     if (Main.version.startsWith("v1.7")) {
/* 212 */       Constructor con = packet.getConstructor(new Class[] { String.class, Float.TYPE, Float.TYPE, Float.TYPE, Float.TYPE, Float.TYPE, Float.TYPE, Float.TYPE, Integer.TYPE });
/* 213 */       return con.newInstance(new Object[] { 
/* 214 */         this.packetName.replace("%id%", this._id).replace("%data%", this._data), 
/* 215 */         Float.valueOf((float)location.getX()), 
/* 216 */         Float.valueOf((float)location.getY()), 
/* 217 */         Float.valueOf((float)location.getZ()), 
/* 218 */         Float.valueOf(offsetX), 
/* 219 */         Float.valueOf(offsetY), 
/* 220 */         Float.valueOf(offsetZ), 
/* 221 */         Float.valueOf(speed), 
/* 222 */         Integer.valueOf(count) });
/*     */     }
/* 224 */     Class type = Class.forName(Main.nmsPackage + ".EnumParticle");
/* 225 */     Object _type = type.getDeclaredMethod("valueOf", new Class[] { String.class }).invoke(null, new Object[] { this.packetName.split("_")[0].toUpperCase() });
/*     */ 
/* 227 */     int[] data = null;
/* 228 */     if (this.packetName.contains("_")) {
/* 229 */       data = new int[] { this._id + (this._data << 12) };
/*     */     }
/*     */ 
/* 232 */     Constructor con = packet.getConstructor(new Class[] { type, Boolean.TYPE, Float.TYPE, Float.TYPE, Float.TYPE, Float.TYPE, Float.TYPE, Float.TYPE, Float.TYPE, Integer.TYPE, [I.class });
/* 233 */     return con.newInstance(new Object[] { 
/* 234 */       _type, 
/* 235 */       Boolean.valueOf(false), 
/* 236 */       Float.valueOf((float)location.getX()), 
/* 237 */       Float.valueOf((float)location.getY()), 
/* 238 */       Float.valueOf((float)location.getZ()), 
/* 239 */       Float.valueOf(offsetX), 
/* 240 */       Float.valueOf(offsetY), 
/* 241 */       Float.valueOf(offsetZ), 
/* 242 */       Float.valueOf(speed), 
/* 243 */       Integer.valueOf(count), 
/* 244 */       data });
/*     */   }
/*     */ 
/*     */   private static void setValue(Object instance, String fieldName, Object value)
/*     */     throws Exception
/*     */   {
/* 257 */     Field field = instance.getClass().getDeclaredField(fieldName);
/* 258 */     field.setAccessible(true);
/* 259 */     field.set(instance, value);
/*     */   }
/*     */ 
/*     */   public static ParticleEffect valueOfStringRaw(String name)
/*     */   {
/* 264 */     for (ParticleEffect pe : values()) {
/*     */       try
/*     */       {
/* 267 */         String[] args = name.split("_");
/* 268 */         if (pe == ICONCRACK) {
/* 269 */           if (args[0].equalsIgnoreCase("iconcrack")) {
/* 270 */             int id = Integer.parseInt(args[1]);
/* 271 */             pe.setId(id);
/* 272 */             return pe;
/*     */           }
/* 274 */         } else if (pe == BLOCK_BREAK) {
/* 275 */           if (args[0].equalsIgnoreCase("blockcrack")) {
/* 276 */             int id = Integer.parseInt(args[1]);
/* 277 */             pe.setId(id);
/* 278 */             int data = Integer.parseInt(args[2]);
/* 279 */             pe.setData(data);
/* 280 */             return pe;
/*     */           }
/* 282 */         } else if (pe == BLOCK_DUST) {
/* 283 */           if (args[0].equalsIgnoreCase("blockdust")) {
/* 284 */             int id = Integer.parseInt(args[1]);
/* 285 */             pe.setId(id);
/* 286 */             int data = Integer.parseInt(args[2]);
/* 287 */             pe.setData(data);
/* 288 */             return pe;
/*     */           }
/* 290 */         } else if (pe.packetName.equalsIgnoreCase(name))
/* 291 */           return pe;
/*     */       }
/*     */       catch (Exception localException)
/*     */       {
/*     */       }
/*     */     }
/* 297 */     return null;
/*     */   }
/*     */ 
/*     */   public static enum Environment
/*     */   {
/* 178 */     ANY, 
/* 179 */     AIR, 
/* 180 */     IN_WATER, 
/* 181 */     UKNOWN;
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\BetterDoubleJump.jar
 * Qualified Name:     me.xADudex.BDJ.ParticleEffect
 * JD-Core Version:    0.6.2
 */