/*     */ package com.comphenix.protocol.events;
/*     */ 
/*     */ import com.comphenix.net.sf.cglib.proxy.Enhancer;
/*     */ import com.comphenix.net.sf.cglib.proxy.MethodInterceptor;
/*     */ import com.comphenix.net.sf.cglib.proxy.MethodProxy;
/*     */ import com.comphenix.protocol.utility.EnhancerFactory;
/*     */ import java.io.IOException;
/*     */ import java.io.ObjectInputStream;
/*     */ import java.io.ObjectOutputStream;
/*     */ import java.io.Serializable;
/*     */ import java.lang.reflect.Method;
/*     */ import java.util.Map;
/*     */ import java.util.UUID;
/*     */ import java.util.concurrent.ConcurrentHashMap;
/*     */ import org.bukkit.Bukkit;
/*     */ import org.bukkit.Location;
/*     */ import org.bukkit.OfflinePlayer;
/*     */ import org.bukkit.Server;
/*     */ import org.bukkit.World;
/*     */ import org.bukkit.entity.Player;
/*     */ 
/*     */ class SerializedOfflinePlayer
/*     */   implements OfflinePlayer, Serializable
/*     */ {
/*     */   private static final long serialVersionUID = -2728976288470282810L;
/*     */   private transient Location bedSpawnLocation;
/*     */   private String name;
/*     */   private UUID uuid;
/*     */   private long firstPlayed;
/*     */   private long lastPlayed;
/*     */   private boolean operator;
/*     */   private boolean banned;
/*     */   private boolean playedBefore;
/*     */   private boolean online;
/*     */   private boolean whitelisted;
/*  67 */   private static Map<String, Method> lookup = new ConcurrentHashMap();
/*     */ 
/*     */   public SerializedOfflinePlayer()
/*     */   {
/*     */   }
/*     */ 
/*     */   public SerializedOfflinePlayer(OfflinePlayer offline)
/*     */   {
/*  81 */     this.name = offline.getName();
/*  82 */     this.uuid = offline.getUniqueId();
/*  83 */     this.firstPlayed = offline.getFirstPlayed();
/*  84 */     this.lastPlayed = offline.getLastPlayed();
/*  85 */     this.operator = offline.isOp();
/*  86 */     this.banned = offline.isBanned();
/*  87 */     this.playedBefore = offline.hasPlayedBefore();
/*  88 */     this.online = offline.isOnline();
/*  89 */     this.whitelisted = offline.isWhitelisted();
/*     */   }
/*     */ 
/*     */   public boolean isOp()
/*     */   {
/*  94 */     return this.operator;
/*     */   }
/*     */ 
/*     */   public void setOp(boolean operator)
/*     */   {
/*  99 */     this.operator = operator;
/*     */   }
/*     */ 
/*     */   public Map<String, Object> serialize()
/*     */   {
/* 104 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   public Location getBedSpawnLocation()
/*     */   {
/* 109 */     return this.bedSpawnLocation;
/*     */   }
/*     */ 
/*     */   public long getFirstPlayed()
/*     */   {
/* 114 */     return this.firstPlayed;
/*     */   }
/*     */ 
/*     */   public long getLastPlayed()
/*     */   {
/* 119 */     return this.lastPlayed;
/*     */   }
/*     */ 
/*     */   public UUID getUniqueId()
/*     */   {
/* 124 */     return this.uuid;
/*     */   }
/*     */ 
/*     */   public String getName()
/*     */   {
/* 129 */     return this.name;
/*     */   }
/*     */ 
/*     */   public boolean hasPlayedBefore()
/*     */   {
/* 134 */     return this.playedBefore;
/*     */   }
/*     */ 
/*     */   public boolean isBanned()
/*     */   {
/* 139 */     return this.banned;
/*     */   }
/*     */ 
/*     */   public void setBanned(boolean banned)
/*     */   {
/* 144 */     this.banned = banned;
/*     */   }
/*     */ 
/*     */   public boolean isOnline()
/*     */   {
/* 149 */     return this.online;
/*     */   }
/*     */ 
/*     */   public boolean isWhitelisted()
/*     */   {
/* 154 */     return this.whitelisted;
/*     */   }
/*     */ 
/*     */   public void setWhitelisted(boolean whitelisted)
/*     */   {
/* 159 */     this.whitelisted = whitelisted;
/*     */   }
/*     */ 
/*     */   private void writeObject(ObjectOutputStream output) throws IOException {
/* 163 */     output.defaultWriteObject();
/*     */ 
/* 166 */     output.writeUTF(this.bedSpawnLocation.getWorld().getName());
/* 167 */     output.writeDouble(this.bedSpawnLocation.getX());
/* 168 */     output.writeDouble(this.bedSpawnLocation.getY());
/* 169 */     output.writeDouble(this.bedSpawnLocation.getZ());
/*     */   }
/*     */ 
/*     */   private void readObject(ObjectInputStream input) throws ClassNotFoundException, IOException {
/* 173 */     input.defaultReadObject();
/*     */ 
/* 176 */     this.bedSpawnLocation = new Location(getWorld(input.readUTF()), input.readDouble(), input.readDouble(), input.readDouble());
/*     */   }
/*     */ 
/*     */   private World getWorld(String name)
/*     */   {
/*     */     try
/*     */     {
/* 187 */       return Bukkit.getServer().getWorld(name);
/*     */     } catch (Exception e) {
/*     */     }
/* 190 */     return null;
/*     */   }
/*     */ 
/*     */   public Player getPlayer()
/*     */   {
/*     */     try
/*     */     {
/* 198 */       return Bukkit.getServer().getPlayerExact(this.name); } catch (Exception e) {
/*     */     }
/* 200 */     return getProxyPlayer();
/*     */   }
/*     */ 
/*     */   public Player getProxyPlayer()
/*     */   {
/* 213 */     if (lookup.size() == 0)
/*     */     {
/* 215 */       for (Method method : Serializable.class.getMethods()) {
/* 216 */         lookup.put(method.getName(), method);
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 221 */     Enhancer ex = EnhancerFactory.getInstance().createEnhancer();
/* 222 */     ex.setSuperclass(Player.class);
/* 223 */     ex.setCallback(new MethodInterceptor()
/*     */     {
/*     */       public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy)
/*     */         throws Throwable
/*     */       {
/* 228 */         Method offlineMethod = (Method)SerializedOfflinePlayer.lookup.get(method.getName());
/*     */ 
/* 231 */         if (offlineMethod == null) {
/* 232 */           throw new UnsupportedOperationException("The method " + method.getName() + " is not supported for offline players.");
/*     */         }
/*     */ 
/* 237 */         return offlineMethod.invoke(SerializedOfflinePlayer.this, args);
/*     */       }
/*     */     });
/* 241 */     return (Player)ex.create();
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.events.SerializedOfflinePlayer
 * JD-Core Version:    0.6.2
 */