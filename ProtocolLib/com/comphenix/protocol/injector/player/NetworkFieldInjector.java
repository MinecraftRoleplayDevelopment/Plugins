/*     */ package com.comphenix.protocol.injector.player;
/*     */ 
/*     */ import com.comphenix.protocol.concurrency.IntegerSet;
/*     */ import com.comphenix.protocol.error.ErrorReporter;
/*     */ import com.comphenix.protocol.events.ListeningWhitelist;
/*     */ import com.comphenix.protocol.events.NetworkMarker;
/*     */ import com.comphenix.protocol.events.PacketListener;
/*     */ import com.comphenix.protocol.injector.GamePhase;
/*     */ import com.comphenix.protocol.injector.ListenerInvoker;
/*     */ import com.comphenix.protocol.injector.PacketFilterManager.PlayerInjectHooks;
/*     */ import com.comphenix.protocol.reflect.FieldUtils;
/*     */ import com.comphenix.protocol.reflect.FuzzyReflection;
/*     */ import com.comphenix.protocol.reflect.StructureModifier;
/*     */ import com.comphenix.protocol.reflect.VolatileField;
/*     */ import com.comphenix.protocol.utility.MinecraftVersion;
/*     */ import com.google.common.collect.Sets;
/*     */ import java.lang.reflect.Field;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.lang.reflect.Method;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.concurrent.ConcurrentHashMap;
/*     */ import org.bukkit.entity.Player;
/*     */ 
/*     */ class NetworkFieldInjector extends PlayerInjector
/*     */ {
/*  65 */   private MinecraftVersion safeVersion = new MinecraftVersion("1.4.4");
/*     */ 
/*  68 */   private Set<Object> ignoredPackets = Sets.newSetFromMap(new ConcurrentHashMap());
/*     */ 
/*  71 */   private List<VolatileField> overridenLists = new ArrayList();
/*     */   private static Field syncField;
/*     */   private Object syncObject;
/*     */   private IntegerSet sendingFilters;
/*     */ 
/*     */   public NetworkFieldInjector(ErrorReporter reporter, Player player, ListenerInvoker manager, IntegerSet sendingFilters)
/*     */   {
/*  81 */     super(reporter, player, manager);
/*  82 */     this.sendingFilters = sendingFilters;
/*     */   }
/*     */ 
/*     */   protected boolean hasListener(int packetID)
/*     */   {
/*  87 */     return this.sendingFilters.contains(packetID);
/*     */   }
/*     */ 
/*     */   public synchronized void initialize(Object injectionSource) throws IllegalAccessException
/*     */   {
/*  92 */     super.initialize(injectionSource);
/*     */ 
/*  95 */     if (this.hasInitialized) {
/*  96 */       if (syncField == null)
/*  97 */         syncField = FuzzyReflection.fromObject(this.networkManager, true).getFieldByType("java\\.lang\\.Object");
/*  98 */       this.syncObject = FieldUtils.readField(syncField, this.networkManager, true);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void sendServerPacket(Object packet, NetworkMarker marker, boolean filtered) throws InvocationTargetException
/*     */   {
/* 104 */     if (this.networkManager != null)
/*     */       try {
/* 106 */         if (!filtered) {
/* 107 */           this.ignoredPackets.add(packet);
/*     */         }
/* 109 */         if (marker != null) {
/* 110 */           this.queuedMarkers.put(packet, marker);
/*     */         }
/*     */ 
/* 114 */         queueMethod.invoke(this.networkManager, new Object[] { packet });
/*     */       }
/*     */       catch (IllegalArgumentException e) {
/* 117 */         throw e;
/*     */       } catch (InvocationTargetException e) {
/* 119 */         throw e;
/*     */       } catch (IllegalAccessException e) {
/* 121 */         throw new IllegalStateException("Unable to access queue method.", e);
/*     */       }
/*     */     else
/* 124 */       throw new IllegalStateException("Unable to load network mananager. Cannot send packet.");
/*     */   }
/*     */ 
/*     */   public UnsupportedListener checkListener(MinecraftVersion version, PacketListener listener)
/*     */   {
/* 130 */     if ((version != null) && (version.compareTo(this.safeVersion) > 0)) {
/* 131 */       return null;
/*     */     }
/*     */ 
/* 135 */     int[] unsupported = { 51, 56 };
/*     */ 
/* 138 */     if (ListeningWhitelist.containsAny(listener.getSendingWhitelist(), unsupported)) {
/* 139 */       return new UnsupportedListener("The NETWORK_FIELD_INJECTOR hook doesn't support map chunk listeners.", unsupported);
/*     */     }
/* 141 */     return null;
/*     */   }
/*     */ 
/*     */   public void injectManager()
/*     */   {
/* 148 */     if (this.networkManager != null)
/*     */     {
/* 151 */       StructureModifier list = networkModifier.withType(List.class);
/*     */ 
/* 154 */       for (Field field : list.getFields()) {
/* 155 */         VolatileField overwriter = new VolatileField(field, this.networkManager, true);
/*     */ 
/* 158 */         List minecraftList = (List)overwriter.getOldValue();
/*     */ 
/* 160 */         synchronized (this.syncObject)
/*     */         {
/* 162 */           List hackedList = new InjectedArrayList(this, this.ignoredPackets);
/*     */ 
/* 165 */           for (Iterator i$ = minecraftList.iterator(); i$.hasNext(); ) { Object packet = i$.next();
/* 166 */             hackedList.add(packet);
/*     */           }
/*     */ 
/* 170 */           minecraftList.clear();
/* 171 */           overwriter.setValue(Collections.synchronizedList(hackedList));
/*     */         }
/*     */ 
/* 174 */         this.overridenLists.add(overwriter);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void cleanHook()
/*     */   {
/* 183 */     for (VolatileField overriden : this.overridenLists) {
/* 184 */       List minecraftList = (List)overriden.getOldValue();
/* 185 */       List hacketList = (List)overriden.getValue();
/*     */ 
/* 187 */       if (minecraftList == hacketList) {
/* 188 */         return;
/*     */       }
/*     */ 
/* 192 */       synchronized (this.syncObject)
/*     */       {
/*     */         try {
/* 195 */           for (i$ = ((List)overriden.getValue()).iterator(); i$.hasNext(); ) { Object packet = i$.next();
/* 196 */             minecraftList.add(packet);
/*     */           }
/*     */         }
/*     */         finally
/*     */         {
/*     */           Iterator i$;
/* 199 */           overriden.revertValue();
/*     */         }
/*     */       }
/*     */     }
/* 203 */     this.overridenLists.clear();
/*     */   }
/*     */ 
/*     */   public void handleDisconnect()
/*     */   {
/*     */   }
/*     */ 
/*     */   public boolean canInject(GamePhase phase)
/*     */   {
/* 214 */     return true;
/*     */   }
/*     */ 
/*     */   public PacketFilterManager.PlayerInjectHooks getHookType()
/*     */   {
/* 219 */     return PacketFilterManager.PlayerInjectHooks.NETWORK_HANDLER_FIELDS;
/*     */   }
/*     */ 
/*     */   public static abstract interface FakePacket
/*     */   {
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.injector.player.NetworkFieldInjector
 * JD-Core Version:    0.6.2
 */