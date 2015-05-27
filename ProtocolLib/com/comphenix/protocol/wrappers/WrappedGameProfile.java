/*     */ package com.comphenix.protocol.wrappers;
/*     */ 
/*     */ import com.comphenix.protocol.ProtocolLibrary;
/*     */ import com.comphenix.protocol.error.ErrorReporter;
/*     */ import com.comphenix.protocol.error.PluginContext;
/*     */ import com.comphenix.protocol.error.Report;
/*     */ import com.comphenix.protocol.error.Report.ReportBuilder;
/*     */ import com.comphenix.protocol.error.ReportType;
/*     */ import com.comphenix.protocol.injector.BukkitUnwrapper;
/*     */ import com.comphenix.protocol.reflect.accessors.Accessors;
/*     */ import com.comphenix.protocol.reflect.accessors.ConstructorAccessor;
/*     */ import com.comphenix.protocol.reflect.accessors.FieldAccessor;
/*     */ import com.comphenix.protocol.reflect.accessors.MethodAccessor;
/*     */ import com.comphenix.protocol.utility.MinecraftReflection;
/*     */ import com.comphenix.protocol.wrappers.collection.ConvertedMultimap;
/*     */ import com.google.common.base.Charsets;
/*     */ import com.google.common.base.Objects;
/*     */ import com.google.common.collect.Multimap;
/*     */ import java.util.UUID;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import org.bukkit.OfflinePlayer;
/*     */ import org.bukkit.entity.Player;
/*     */ 
/*     */ public class WrappedGameProfile extends AbstractWrapper
/*     */ {
/*  29 */   public static final ReportType REPORT_INVALID_UUID = new ReportType("Plugin %s created a profile with '%s' as an UUID.");
/*     */ 
/*  31 */   private static final Class<?> GAME_PROFILE = MinecraftReflection.getGameProfileClass();
/*     */ 
/*  33 */   private static final ConstructorAccessor CREATE_STRING_STRING = Accessors.getConstructorAccessorOrNull(GAME_PROFILE, new Class[] { String.class, String.class });
/*     */ 
/*  35 */   private static final ConstructorAccessor CREATE_UUID_STRING = Accessors.getConstructorAccessorOrNull(GAME_PROFILE, new Class[] { UUID.class, String.class });
/*     */ 
/*  38 */   private static final FieldAccessor GET_UUID_STRING = Accessors.getFieldAcccessorOrNull(GAME_PROFILE, "id", String.class);
/*     */ 
/*  41 */   private static final MethodAccessor GET_ID = Accessors.getMethodAcccessorOrNull(GAME_PROFILE, "getId");
/*     */ 
/*  43 */   private static final MethodAccessor GET_NAME = Accessors.getMethodAcccessorOrNull(GAME_PROFILE, "getName");
/*     */ 
/*  45 */   private static final MethodAccessor GET_PROPERTIES = Accessors.getMethodAcccessorOrNull(GAME_PROFILE, "getProperties");
/*     */ 
/*  47 */   private static final MethodAccessor IS_COMPLETE = Accessors.getMethodAcccessorOrNull(GAME_PROFILE, "isComplete");
/*     */   private static FieldAccessor PLAYER_PROFILE;
/*     */   private static FieldAccessor OFFLINE_PROFILE;
/*     */   private Multimap<String, WrappedSignedProperty> propertyMap;
/*     */   private volatile UUID parsedUUID;
/*     */ 
/*     */   private WrappedGameProfile(Object profile)
/*     */   {
/*  62 */     super(GAME_PROFILE);
/*  63 */     setHandle(profile);
/*     */   }
/*     */ 
/*     */   public static WrappedGameProfile fromPlayer(Player player)
/*     */   {
/*  75 */     FieldAccessor accessor = PLAYER_PROFILE;
/*  76 */     if (accessor == null) {
/*  77 */       accessor = Accessors.getFieldAccessor(MinecraftReflection.getEntityHumanClass(), GAME_PROFILE, true);
/*  78 */       PLAYER_PROFILE = accessor;
/*     */     }
/*     */ 
/*  81 */     Object nmsPlayer = BukkitUnwrapper.getInstance().unwrapItem(player);
/*  82 */     return fromHandle(PLAYER_PROFILE.get(nmsPlayer));
/*     */   }
/*     */ 
/*     */   public static WrappedGameProfile fromOfflinePlayer(OfflinePlayer player)
/*     */   {
/*  94 */     FieldAccessor accessor = OFFLINE_PROFILE;
/*  95 */     if (accessor == null) {
/*  96 */       accessor = Accessors.getFieldAccessor(player.getClass(), GAME_PROFILE, true);
/*  97 */       OFFLINE_PROFILE = accessor;
/*     */     }
/*     */ 
/* 100 */     return fromHandle(OFFLINE_PROFILE.get(player));
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public WrappedGameProfile(String id, String name)
/*     */   {
/* 116 */     super(GAME_PROFILE);
/*     */ 
/* 118 */     if (CREATE_STRING_STRING != null)
/* 119 */       setHandle(CREATE_STRING_STRING.invoke(new Object[] { id, name }));
/* 120 */     else if (CREATE_UUID_STRING != null)
/* 121 */       setHandle(CREATE_UUID_STRING.invoke(new Object[] { parseUUID(id), name }));
/*     */     else
/* 123 */       throw new IllegalArgumentException("Unsupported GameProfile constructor.");
/*     */   }
/*     */ 
/*     */   public WrappedGameProfile(UUID uuid, String name)
/*     */   {
/* 136 */     super(GAME_PROFILE);
/*     */ 
/* 138 */     if (CREATE_STRING_STRING != null)
/* 139 */       setHandle(CREATE_STRING_STRING.invoke(new Object[] { uuid != null ? uuid.toString() : null, name }));
/* 140 */     else if (CREATE_UUID_STRING != null)
/* 141 */       setHandle(CREATE_UUID_STRING.invoke(new Object[] { uuid, name }));
/*     */     else
/* 143 */       throw new IllegalArgumentException("Unsupported GameProfile constructor.");
/*     */   }
/*     */ 
/*     */   public static WrappedGameProfile fromHandle(Object handle)
/*     */   {
/* 153 */     if (handle == null) {
/* 154 */       return null;
/*     */     }
/* 156 */     return new WrappedGameProfile(handle);
/*     */   }
/*     */ 
/*     */   private static UUID parseUUID(String id)
/*     */   {
/*     */     try
/*     */     {
/* 168 */       return id != null ? UUID.fromString(id) : null;
/*     */     }
/*     */     catch (IllegalArgumentException e) {
/* 171 */       ProtocolLibrary.getErrorReporter().reportWarning(WrappedGameProfile.class, Report.newBuilder(REPORT_INVALID_UUID).rateLimit(1L, TimeUnit.HOURS).messageParam(new Object[] { PluginContext.getPluginCaller(new Exception()), id }));
/*     */     }
/*     */ 
/* 175 */     return UUID.nameUUIDFromBytes(id.getBytes(Charsets.UTF_8));
/*     */   }
/*     */ 
/*     */   public UUID getUUID()
/*     */   {
/* 190 */     UUID uuid = this.parsedUUID;
/*     */ 
/* 192 */     if (uuid == null) {
/*     */       try {
/* 194 */         if (GET_UUID_STRING != null)
/* 195 */           uuid = parseUUID(getId());
/* 196 */         else if (GET_ID != null)
/* 197 */           uuid = (UUID)GET_ID.invoke(this.handle, new Object[0]);
/*     */         else {
/* 199 */           throw new IllegalStateException("Unsupported getId() method");
/*     */         }
/*     */ 
/* 203 */         this.parsedUUID = uuid;
/*     */       } catch (IllegalArgumentException e) {
/* 205 */         throw new IllegalStateException("Cannot parse ID " + getId() + " as an UUID in player profile " + getName(), e);
/*     */       }
/*     */     }
/*     */ 
/* 209 */     return uuid;
/*     */   }
/*     */ 
/*     */   public String getId()
/*     */   {
/* 222 */     if (GET_UUID_STRING != null)
/* 223 */       return (String)GET_UUID_STRING.get(this.handle);
/* 224 */     if (GET_ID != null) {
/* 225 */       UUID uuid = (UUID)GET_ID.invoke(this.handle, new Object[0]);
/* 226 */       return uuid != null ? uuid.toString() : null;
/*     */     }
/* 228 */     throw new IllegalStateException("Unsupported getId() method");
/*     */   }
/*     */ 
/*     */   public String getName()
/*     */   {
/* 238 */     if (GET_NAME != null) {
/* 239 */       return (String)GET_NAME.invoke(this.handle, new Object[0]);
/*     */     }
/* 241 */     throw new IllegalStateException("Unsupported getName() method");
/*     */   }
/*     */ 
/*     */   public Multimap<String, WrappedSignedProperty> getProperties()
/*     */   {
/* 252 */     Multimap result = this.propertyMap;
/*     */ 
/* 254 */     if (result == null) {
/* 255 */       Multimap properties = (Multimap)GET_PROPERTIES.invoke(this.handle, new Object[0]);
/* 256 */       result = new ConvertedMultimap(GuavaWrappers.getBukkitMultimap(properties))
/*     */       {
/*     */         protected Object toInner(WrappedSignedProperty outer) {
/* 259 */           return outer.handle;
/*     */         }
/*     */ 
/*     */         protected Object toInnerObject(Object outer)
/*     */         {
/* 264 */           if ((outer instanceof WrappedSignedProperty)) {
/* 265 */             return toInner((WrappedSignedProperty)outer);
/*     */           }
/* 267 */           return outer;
/*     */         }
/*     */ 
/*     */         protected WrappedSignedProperty toOuter(Object inner)
/*     */         {
/* 272 */           return WrappedSignedProperty.fromHandle(inner);
/*     */         }
/*     */       };
/* 275 */       this.propertyMap = result;
/*     */     }
/* 277 */     return result;
/*     */   }
/*     */ 
/*     */   public WrappedGameProfile withName(String name)
/*     */   {
/* 287 */     return new WrappedGameProfile(getId(), name);
/*     */   }
/*     */ 
/*     */   public WrappedGameProfile withId(String id)
/*     */   {
/* 297 */     return new WrappedGameProfile(id, getName());
/*     */   }
/*     */ 
/*     */   public boolean isComplete()
/*     */   {
/* 306 */     return ((Boolean)IS_COMPLETE.invoke(this.handle, new Object[0])).booleanValue();
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 311 */     return String.valueOf(getHandle());
/*     */   }
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 317 */     return Objects.hashCode(new Object[] { getId(), getName() });
/*     */   }
/*     */ 
/*     */   public boolean equals(Object obj)
/*     */   {
/* 322 */     if (obj == this) {
/* 323 */       return true;
/*     */     }
/* 325 */     if ((obj instanceof WrappedGameProfile)) {
/* 326 */       WrappedGameProfile other = (WrappedGameProfile)obj;
/* 327 */       return Objects.equal(getHandle(), other.getHandle());
/*     */     }
/*     */ 
/* 330 */     return false;
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.wrappers.WrappedGameProfile
 * JD-Core Version:    0.6.2
 */