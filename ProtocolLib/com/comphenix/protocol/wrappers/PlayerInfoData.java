/*     */ package com.comphenix.protocol.wrappers;
/*     */ 
/*     */ import com.comphenix.protocol.reflect.EquivalentConverter;
/*     */ import com.comphenix.protocol.reflect.StructureModifier;
/*     */ import com.comphenix.protocol.utility.MinecraftReflection;
/*     */ import com.google.common.base.Objects;
/*     */ import java.lang.reflect.Constructor;
/*     */ 
/*     */ public class PlayerInfoData
/*     */ {
/*     */   private static Constructor<?> constructor;
/*     */   protected final WrappedGameProfile profile;
/*     */   protected final int ping;
/*     */   protected final EnumWrappers.NativeGameMode gameMode;
/*     */   protected final WrappedChatComponent displayName;
/*     */ 
/*     */   public PlayerInfoData(WrappedGameProfile profile, int ping, EnumWrappers.NativeGameMode gameMode, WrappedChatComponent displayName)
/*     */   {
/*  41 */     this.ping = ping;
/*  42 */     this.gameMode = gameMode;
/*  43 */     this.profile = profile;
/*  44 */     this.displayName = displayName;
/*     */   }
/*     */ 
/*     */   public WrappedGameProfile getProfile() {
/*  48 */     return this.profile;
/*     */   }
/*     */ 
/*     */   public int getPing() {
/*  52 */     return this.ping;
/*     */   }
/*     */ 
/*     */   public EnumWrappers.NativeGameMode getGameMode() {
/*  56 */     return this.gameMode;
/*     */   }
/*     */ 
/*     */   public WrappedChatComponent getDisplayName() {
/*  60 */     return this.displayName;
/*     */   }
/*     */ 
/*     */   public static EquivalentConverter<PlayerInfoData> getConverter()
/*     */   {
/*  68 */     return new EquivalentConverter()
/*     */     {
/*     */       public Object getGeneric(Class<?> genericType, PlayerInfoData specific) {
/*  71 */         if (PlayerInfoData.constructor == null) {
/*     */           try
/*     */           {
/*  74 */             PlayerInfoData.access$002(MinecraftReflection.getPlayerInfoDataClass().getConstructor(new Class[] { MinecraftReflection.getMinecraftClass("PacketPlayOutPlayerInfo"), MinecraftReflection.getGameProfileClass(), Integer.TYPE, EnumWrappers.getGameModeClass(), MinecraftReflection.getIChatBaseComponentClass() }));
/*     */           }
/*     */           catch (Exception e)
/*     */           {
/*  82 */             throw new RuntimeException("Cannot find PlayerInfoData constructor.", e);
/*     */           }
/*     */ 
/*     */         }
/*     */ 
/*     */         try
/*     */         {
/*  91 */           return PlayerInfoData.constructor.newInstance(new Object[] { null, specific.profile.handle, Integer.valueOf(specific.ping), EnumWrappers.getGameModeConverter().getGeneric(EnumWrappers.getGameModeClass(), specific.gameMode), specific.displayName != null ? specific.displayName.handle : null });
/*     */         }
/*     */         catch (Exception e)
/*     */         {
/* 100 */           throw new RuntimeException("Failed to construct PlayerInfoData.", e);
/*     */         }
/*     */       }
/*     */ 
/*     */       public PlayerInfoData getSpecific(Object generic)
/*     */       {
/* 106 */         if (MinecraftReflection.isPlayerInfoData(generic)) {
/* 107 */           StructureModifier modifier = new StructureModifier(generic.getClass(), null, false).withTarget(generic);
/*     */ 
/* 110 */           StructureModifier gameProfiles = modifier.withType(MinecraftReflection.getGameProfileClass(), BukkitConverters.getWrappedGameProfileConverter());
/*     */ 
/* 112 */           WrappedGameProfile gameProfile = (WrappedGameProfile)gameProfiles.read(0);
/*     */ 
/* 114 */           StructureModifier ints = modifier.withType(Integer.TYPE);
/* 115 */           int ping = ((Integer)ints.read(0)).intValue();
/*     */ 
/* 117 */           StructureModifier gameModes = modifier.withType(EnumWrappers.getGameModeClass(), EnumWrappers.getGameModeConverter());
/*     */ 
/* 119 */           EnumWrappers.NativeGameMode gameMode = (EnumWrappers.NativeGameMode)gameModes.read(0);
/*     */ 
/* 121 */           StructureModifier displayNames = modifier.withType(MinecraftReflection.getIChatBaseComponentClass(), BukkitConverters.getWrappedChatComponentConverter());
/*     */ 
/* 123 */           WrappedChatComponent displayName = (WrappedChatComponent)displayNames.read(0);
/*     */ 
/* 125 */           return new PlayerInfoData(gameProfile, ping, gameMode, displayName);
/*     */         }
/*     */ 
/* 129 */         return null;
/*     */       }
/*     */ 
/*     */       public Class<PlayerInfoData> getSpecificType()
/*     */       {
/* 135 */         return PlayerInfoData.class;
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   public boolean equals(Object obj)
/*     */   {
/* 143 */     if (this == obj) return true;
/* 144 */     if (obj == null) return false;
/*     */ 
/* 147 */     if ((obj instanceof PlayerInfoData)) {
/* 148 */       PlayerInfoData other = (PlayerInfoData)obj;
/* 149 */       return (this.profile.equals(other.profile)) && (this.ping == other.ping) && (this.gameMode == other.gameMode) && (this.displayName.equals(other.displayName));
/*     */     }
/*     */ 
/* 152 */     return false;
/*     */   }
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 157 */     return Objects.hashCode(new Object[] { this.profile, Integer.valueOf(this.ping), this.gameMode, this.displayName });
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 162 */     return String.format("PlayerInfoData { profile=%s, ping=%s, gameMode=%s, displayName=%s }", new Object[] { this.profile, Integer.valueOf(this.ping), this.gameMode, this.displayName });
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.wrappers.PlayerInfoData
 * JD-Core Version:    0.6.2
 */