/*     */ package com.comphenix.protocol.concurrency;
/*     */ 
/*     */ import com.comphenix.protocol.utility.SafeCacheBuilder;
/*     */ import com.google.common.base.Function;
/*     */ import com.google.common.cache.CacheLoader;
/*     */ import com.google.common.cache.RemovalListener;
/*     */ import com.google.common.cache.RemovalNotification;
/*     */ import com.google.common.collect.AbstractIterator;
/*     */ import com.google.common.collect.Maps;
/*     */ import com.google.common.util.concurrent.UncheckedExecutionException;
/*     */ import java.util.AbstractMap;
/*     */ import java.util.AbstractMap.SimpleEntry;
/*     */ import java.util.AbstractSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.Set;
/*     */ import java.util.concurrent.ConcurrentMap;
/*     */ import org.bukkit.Bukkit;
/*     */ import org.bukkit.entity.Player;
/*     */ 
/*     */ public class ConcurrentPlayerMap<TValue> extends AbstractMap<Player, TValue>
/*     */   implements ConcurrentMap<Player, TValue>
/*     */ {
/*  59 */   protected ConcurrentMap<Object, TValue> valueLookup = createValueMap();
/*     */ 
/*  65 */   protected ConcurrentMap<Object, Player> keyLookup = createKeyCache();
/*     */   protected final Function<Player, Object> keyMethod;
/*     */ 
/*     */   public static <T> ConcurrentPlayerMap<T> usingAddress()
/*     */   {
/*  76 */     return new ConcurrentPlayerMap(PlayerKey.ADDRESS);
/*     */   }
/*     */ 
/*     */   public static <T> ConcurrentPlayerMap<T> usingName()
/*     */   {
/*  84 */     return new ConcurrentPlayerMap(PlayerKey.NAME);
/*     */   }
/*     */ 
/*     */   public ConcurrentPlayerMap(PlayerKey standardMethod)
/*     */   {
/*  92 */     this.keyMethod = standardMethod;
/*     */   }
/*     */ 
/*     */   public ConcurrentPlayerMap(Function<Player, Object> method)
/*     */   {
/* 100 */     this.keyMethod = method;
/*     */   }
/*     */ 
/*     */   protected ConcurrentMap<Object, TValue> createValueMap()
/*     */   {
/* 110 */     return Maps.newConcurrentMap();
/*     */   }
/*     */ 
/*     */   protected ConcurrentMap<Object, Player> createKeyCache()
/*     */   {
/* 118 */     return SafeCacheBuilder.newBuilder().weakValues().removalListener(new RemovalListener()
/*     */     {
/*     */       public void onRemoval(RemovalNotification<Object, Player> removed)
/*     */       {
/* 125 */         if (removed.wasEvicted())
/* 126 */           ConcurrentPlayerMap.this.onCacheEvicted(removed.getKey());
/*     */       }
/*     */     }).build(new CacheLoader()
/*     */     {
/*     */       public Player load(Object key)
/*     */         throws Exception
/*     */       {
/* 134 */         Player player = ConcurrentPlayerMap.this.findOnlinePlayer(key);
/*     */ 
/* 136 */         if (player != null) {
/* 137 */           return player;
/*     */         }
/*     */ 
/* 140 */         throw new IllegalArgumentException("Unable to find a player associated with: " + key);
/*     */       }
/*     */     });
/*     */   }
/*     */ 
/*     */   private void onCacheEvicted(Object key)
/*     */   {
/* 152 */     Player newPlayer = findOnlinePlayer(key);
/*     */ 
/* 154 */     if (newPlayer != null)
/*     */     {
/* 156 */       this.keyLookup.put(key, newPlayer);
/*     */     }
/* 158 */     else this.valueLookup.remove(key);
/*     */   }
/*     */ 
/*     */   protected Player findOnlinePlayer(Object key)
/*     */   {
/* 168 */     for (Player player : Bukkit.getOnlinePlayers()) {
/* 169 */       if (key.equals(this.keyMethod.apply(player))) {
/* 170 */         return player;
/*     */       }
/*     */     }
/* 173 */     return null;
/*     */   }
/*     */ 
/*     */   protected Player lookupPlayer(Object key)
/*     */   {
/*     */     try
/*     */     {
/* 184 */       return (Player)this.keyLookup.get(key); } catch (UncheckedExecutionException e) {
/*     */     }
/* 186 */     return null;
/*     */   }
/*     */ 
/*     */   protected Object cachePlayerKey(Player player)
/*     */   {
/* 196 */     Object key = this.keyMethod.apply(player);
/*     */ 
/* 198 */     this.keyLookup.put(key, player);
/* 199 */     return key;
/*     */   }
/*     */ 
/*     */   public TValue put(Player key, TValue value)
/*     */   {
/* 204 */     return this.valueLookup.put(cachePlayerKey(key), value);
/*     */   }
/*     */ 
/*     */   public TValue putIfAbsent(Player key, TValue value)
/*     */   {
/* 209 */     return this.valueLookup.putIfAbsent(cachePlayerKey(key), value);
/*     */   }
/*     */ 
/*     */   public TValue replace(Player key, TValue value)
/*     */   {
/* 214 */     return this.valueLookup.replace(cachePlayerKey(key), value);
/*     */   }
/*     */ 
/*     */   public boolean replace(Player key, TValue oldValue, TValue newValue)
/*     */   {
/* 219 */     return this.valueLookup.replace(cachePlayerKey(key), oldValue, newValue);
/*     */   }
/*     */ 
/*     */   public TValue remove(Object key)
/*     */   {
/* 224 */     if ((key instanceof Player)) {
/* 225 */       Object playerKey = this.keyMethod.apply((Player)key);
/*     */ 
/* 227 */       if (playerKey != null) {
/* 228 */         Object value = this.valueLookup.remove(playerKey);
/*     */ 
/* 230 */         this.keyLookup.remove(playerKey);
/* 231 */         return value;
/*     */       }
/*     */     }
/* 234 */     return null;
/*     */   }
/*     */ 
/*     */   public boolean remove(Object key, Object value)
/*     */   {
/* 239 */     if ((key instanceof Player)) {
/* 240 */       Object playerKey = this.keyMethod.apply((Player)key);
/*     */ 
/* 242 */       if ((playerKey != null) && (this.valueLookup.remove(playerKey, value))) {
/* 243 */         this.keyLookup.remove(playerKey);
/* 244 */         return true;
/*     */       }
/*     */     }
/* 247 */     return false;
/*     */   }
/*     */ 
/*     */   public TValue get(Object key)
/*     */   {
/* 252 */     if ((key instanceof Player)) {
/* 253 */       Object playerKey = this.keyMethod.apply((Player)key);
/* 254 */       return playerKey != null ? this.valueLookup.get(playerKey) : null;
/*     */     }
/* 256 */     return null;
/*     */   }
/*     */ 
/*     */   public boolean containsKey(Object key)
/*     */   {
/* 261 */     if ((key instanceof Player)) {
/* 262 */       Object playerKey = this.keyMethod.apply((Player)key);
/* 263 */       return (playerKey != null) && (this.valueLookup.containsKey(playerKey));
/*     */     }
/* 265 */     return false;
/*     */   }
/*     */ 
/*     */   public Set<Map.Entry<Player, TValue>> entrySet()
/*     */   {
/* 270 */     return new AbstractSet()
/*     */     {
/*     */       public Iterator<Map.Entry<Player, TValue>> iterator() {
/* 273 */         return ConcurrentPlayerMap.this.entryIterator();
/*     */       }
/*     */ 
/*     */       public int size()
/*     */       {
/* 278 */         return ConcurrentPlayerMap.this.valueLookup.size();
/*     */       }
/*     */ 
/*     */       public void clear()
/*     */       {
/* 283 */         ConcurrentPlayerMap.this.valueLookup.clear();
/* 284 */         ConcurrentPlayerMap.this.keyLookup.clear();
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   private Iterator<Map.Entry<Player, TValue>> entryIterator()
/*     */   {
/* 295 */     final Iterator source = this.valueLookup.entrySet().iterator();
/* 296 */     final AbstractIterator filtered = new AbstractIterator()
/*     */     {
/*     */       protected Map.Entry<Player, TValue> computeNext()
/*     */       {
/* 300 */         while (source.hasNext()) {
/* 301 */           Map.Entry entry = (Map.Entry)source.next();
/* 302 */           Player player = ConcurrentPlayerMap.this.lookupPlayer(entry.getKey());
/*     */ 
/* 304 */           if (player == null)
/*     */           {
/* 306 */             source.remove();
/* 307 */             ConcurrentPlayerMap.this.keyLookup.remove(entry.getKey());
/*     */           } else {
/* 309 */             return new AbstractMap.SimpleEntry(player, entry.getValue());
/*     */           }
/*     */         }
/* 312 */         return (Map.Entry)endOfData();
/*     */       }
/*     */     };
/* 317 */     return new Iterator() {
/*     */       public boolean hasNext() {
/* 319 */         return filtered.hasNext();
/*     */       }
/*     */       public Map.Entry<Player, TValue> next() {
/* 322 */         return (Map.Entry)filtered.next();
/*     */       }
/*     */       public void remove() {
/* 325 */         source.remove();
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   public static abstract enum PlayerKey
/*     */     implements Function<Player, Object>
/*     */   {
/*  38 */     ADDRESS, 
/*     */ 
/*  48 */     NAME;
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.concurrency.ConcurrentPlayerMap
 * JD-Core Version:    0.6.2
 */