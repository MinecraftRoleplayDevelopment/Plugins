/*      */ package com.comphenix.protocol.events;
/*      */ 
/*      */ import com.comphenix.protocol.PacketType;
/*      */ import com.comphenix.protocol.PacketType.Play.Server;
/*      */ import com.comphenix.protocol.PacketType.Protocol;
/*      */ import com.comphenix.protocol.PacketType.Status.Server;
/*      */ import com.comphenix.protocol.injector.StructureCache;
/*      */ import com.comphenix.protocol.reflect.EquivalentConverter;
/*      */ import com.comphenix.protocol.reflect.FuzzyReflection;
/*      */ import com.comphenix.protocol.reflect.ObjectWriter;
/*      */ import com.comphenix.protocol.reflect.StructureModifier;
/*      */ import com.comphenix.protocol.reflect.cloning.AggregateCloner;
/*      */ import com.comphenix.protocol.reflect.cloning.AggregateCloner.Builder;
/*      */ import com.comphenix.protocol.reflect.cloning.AggregateCloner.BuilderParameters;
/*      */ import com.comphenix.protocol.reflect.cloning.BukkitCloner;
/*      */ import com.comphenix.protocol.reflect.cloning.Cloner;
/*      */ import com.comphenix.protocol.reflect.cloning.CollectionCloner;
/*      */ import com.comphenix.protocol.reflect.cloning.FieldCloner;
/*      */ import com.comphenix.protocol.reflect.cloning.ImmutableDetector;
/*      */ import com.comphenix.protocol.reflect.cloning.SerializableCloner;
/*      */ import com.comphenix.protocol.reflect.fuzzy.FuzzyMethodContract;
/*      */ import com.comphenix.protocol.reflect.fuzzy.FuzzyMethodContract.Builder;
/*      */ import com.comphenix.protocol.reflect.instances.DefaultInstances;
/*      */ import com.comphenix.protocol.reflect.instances.InstanceProvider;
/*      */ import com.comphenix.protocol.utility.MinecraftMethods;
/*      */ import com.comphenix.protocol.utility.MinecraftReflection;
/*      */ import com.comphenix.protocol.utility.StreamSerializer;
/*      */ import com.comphenix.protocol.wrappers.BlockPosition;
/*      */ import com.comphenix.protocol.wrappers.BukkitConverters;
/*      */ import com.comphenix.protocol.wrappers.ChunkCoordIntPair;
/*      */ import com.comphenix.protocol.wrappers.ChunkPosition;
/*      */ import com.comphenix.protocol.wrappers.EnumWrappers;
/*      */ import com.comphenix.protocol.wrappers.EnumWrappers.ChatVisibility;
/*      */ import com.comphenix.protocol.wrappers.EnumWrappers.ClientCommand;
/*      */ import com.comphenix.protocol.wrappers.EnumWrappers.CombatEventType;
/*      */ import com.comphenix.protocol.wrappers.EnumWrappers.Difficulty;
/*      */ import com.comphenix.protocol.wrappers.EnumWrappers.EntityUseAction;
/*      */ import com.comphenix.protocol.wrappers.EnumWrappers.NativeGameMode;
/*      */ import com.comphenix.protocol.wrappers.EnumWrappers.Particle;
/*      */ import com.comphenix.protocol.wrappers.EnumWrappers.PlayerAction;
/*      */ import com.comphenix.protocol.wrappers.EnumWrappers.PlayerDigType;
/*      */ import com.comphenix.protocol.wrappers.EnumWrappers.PlayerInfoAction;
/*      */ import com.comphenix.protocol.wrappers.EnumWrappers.ResourcePackStatus;
/*      */ import com.comphenix.protocol.wrappers.EnumWrappers.ScoreboardAction;
/*      */ import com.comphenix.protocol.wrappers.EnumWrappers.TitleAction;
/*      */ import com.comphenix.protocol.wrappers.EnumWrappers.WorldBorderAction;
/*      */ import com.comphenix.protocol.wrappers.PlayerInfoData;
/*      */ import com.comphenix.protocol.wrappers.WrappedAttribute;
/*      */ import com.comphenix.protocol.wrappers.WrappedBlockData;
/*      */ import com.comphenix.protocol.wrappers.WrappedChatComponent;
/*      */ import com.comphenix.protocol.wrappers.WrappedDataWatcher;
/*      */ import com.comphenix.protocol.wrappers.WrappedGameProfile;
/*      */ import com.comphenix.protocol.wrappers.WrappedServerPing;
/*      */ import com.comphenix.protocol.wrappers.WrappedStatistic;
/*      */ import com.comphenix.protocol.wrappers.WrappedWatchableObject;
/*      */ import com.comphenix.protocol.wrappers.nbt.NbtBase;
/*      */ import com.google.common.base.Function;
/*      */ import com.google.common.base.Preconditions;
/*      */ import com.google.common.collect.Maps;
/*      */ import com.google.common.collect.Sets;
/*      */ import com.mojang.authlib.GameProfile;
/*      */ import io.netty.buffer.ByteBuf;
/*      */ import io.netty.buffer.UnpooledByteBufAllocator;
/*      */ import java.io.DataInput;
/*      */ import java.io.DataInputStream;
/*      */ import java.io.DataOutput;
/*      */ import java.io.DataOutputStream;
/*      */ import java.io.IOException;
/*      */ import java.io.ObjectInputStream;
/*      */ import java.io.ObjectOutputStream;
/*      */ import java.io.Serializable;
/*      */ import java.lang.reflect.Array;
/*      */ import java.lang.reflect.Field;
/*      */ import java.lang.reflect.InvocationTargetException;
/*      */ import java.lang.reflect.Method;
/*      */ import java.util.Collection;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Set;
/*      */ import java.util.concurrent.ConcurrentMap;
/*      */ import javax.annotation.Nonnull;
/*      */ import javax.annotation.Nullable;
/*      */ import org.bukkit.Material;
/*      */ import org.bukkit.World;
/*      */ import org.bukkit.WorldType;
/*      */ import org.bukkit.entity.Entity;
/*      */ import org.bukkit.entity.Player;
/*      */ import org.bukkit.inventory.ItemStack;
/*      */ import org.bukkit.util.Vector;
/*      */ 
/*      */ public class PacketContainer
/*      */   implements Serializable
/*      */ {
/*      */   private static final long serialVersionUID = 3L;
/*      */   protected PacketType type;
/*      */   protected transient Object handle;
/*      */   protected transient StructureModifier<Object> structureModifier;
/*  120 */   private static ConcurrentMap<Class<?>, Method> writeMethods = Maps.newConcurrentMap();
/*  121 */   private static ConcurrentMap<Class<?>, Method> readMethods = Maps.newConcurrentMap();
/*      */ 
/*  124 */   private static final AggregateCloner DEEP_CLONER = AggregateCloner.newBuilder().instanceProvider(DefaultInstances.DEFAULT).andThen(BukkitCloner.class).andThen(ImmutableDetector.class).andThen(CollectionCloner.class).andThen(getSpecializedDeepClonerFactory()).build();
/*      */ 
/*  132 */   private static final AggregateCloner SHALLOW_CLONER = AggregateCloner.newBuilder().instanceProvider(DefaultInstances.DEFAULT).andThen(new Function()
/*      */   {
/*      */     public Cloner apply(@Nullable AggregateCloner.BuilderParameters param)
/*      */     {
/*  137 */       if (param == null) {
/*  138 */         throw new IllegalArgumentException("Cannot be NULL.");
/*      */       }
/*  140 */       return new FieldCloner(param.getAggregateCloner(), param.getInstanceProvider())
/*      */       {
/*      */       };
/*      */     }
/*      */   }).build();
/*      */ 
/*  149 */   private static final Set<PacketType> CLONING_UNSUPPORTED = Sets.newHashSet(new PacketType[] { PacketType.Play.Server.UPDATE_ATTRIBUTES, PacketType.Status.Server.OUT_SERVER_INFO });
/*      */ 
/*      */   @Deprecated
/*      */   public PacketContainer(int id)
/*      */   {
/*  160 */     this(PacketType.findLegacy(id), StructureCache.newPacket(PacketType.findLegacy(id)));
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public PacketContainer(int id, Object handle)
/*      */   {
/*  172 */     this(PacketType.findLegacy(id), handle);
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public PacketContainer(int id, Object handle, StructureModifier<Object> structure)
/*      */   {
/*  185 */     this(PacketType.findLegacy(id), handle, structure);
/*      */   }
/*      */ 
/*      */   public PacketContainer(PacketType type)
/*      */   {
/*  193 */     this(type, StructureCache.newPacket(type));
/*      */   }
/*      */ 
/*      */   public PacketContainer(PacketType type, Object handle)
/*      */   {
/*  202 */     this(type, handle, StructureCache.getStructure(type).withTarget(handle));
/*      */   }
/*      */ 
/*      */   public PacketContainer(PacketType type, Object handle, StructureModifier<Object> structure)
/*      */   {
/*  212 */     if (handle == null)
/*  213 */       throw new IllegalArgumentException("handle cannot be null.");
/*  214 */     if (type == null) {
/*  215 */       throw new IllegalArgumentException("type cannot be null.");
/*      */     }
/*  217 */     this.type = type;
/*  218 */     this.handle = handle;
/*  219 */     this.structureModifier = structure;
/*      */   }
/*      */ 
/*      */   public static PacketContainer fromPacket(Object packet)
/*      */   {
/*  228 */     PacketType type = PacketType.fromClass(packet.getClass());
/*  229 */     return new PacketContainer(type, packet);
/*      */   }
/*      */ 
/*      */   protected PacketContainer()
/*      */   {
/*      */   }
/*      */ 
/*      */   public Object getHandle()
/*      */   {
/*  243 */     return this.handle;
/*      */   }
/*      */ 
/*      */   public StructureModifier<Object> getModifier()
/*      */   {
/*  251 */     return this.structureModifier;
/*      */   }
/*      */ 
/*      */   public <T> StructureModifier<T> getSpecificModifier(Class<T> primitiveType)
/*      */   {
/*  260 */     return this.structureModifier.withType(primitiveType);
/*      */   }
/*      */ 
/*      */   public StructureModifier<Byte> getBytes()
/*      */   {
/*  268 */     return this.structureModifier.withType(Byte.TYPE);
/*      */   }
/*      */ 
/*      */   public StructureModifier<Boolean> getBooleans()
/*      */   {
/*  276 */     return this.structureModifier.withType(Boolean.TYPE);
/*      */   }
/*      */ 
/*      */   public StructureModifier<Short> getShorts()
/*      */   {
/*  284 */     return this.structureModifier.withType(Short.TYPE);
/*      */   }
/*      */ 
/*      */   public StructureModifier<Integer> getIntegers()
/*      */   {
/*  292 */     return this.structureModifier.withType(Integer.TYPE);
/*      */   }
/*      */ 
/*      */   public StructureModifier<Long> getLongs()
/*      */   {
/*  299 */     return this.structureModifier.withType(Long.TYPE);
/*      */   }
/*      */ 
/*      */   public StructureModifier<Float> getFloat()
/*      */   {
/*  307 */     return this.structureModifier.withType(Float.TYPE);
/*      */   }
/*      */ 
/*      */   public StructureModifier<Double> getDoubles()
/*      */   {
/*  315 */     return this.structureModifier.withType(Double.TYPE);
/*      */   }
/*      */ 
/*      */   public StructureModifier<String> getStrings()
/*      */   {
/*  323 */     return this.structureModifier.withType(String.class);
/*      */   }
/*      */ 
/*      */   public StructureModifier<String[]> getStringArrays()
/*      */   {
/*  331 */     return this.structureModifier.withType([Ljava.lang.String.class);
/*      */   }
/*      */ 
/*      */   public StructureModifier<byte[]> getByteArrays()
/*      */   {
/*  339 */     return this.structureModifier.withType([B.class);
/*      */   }
/*      */ 
/*      */   public StreamSerializer getByteArraySerializer()
/*      */   {
/*  347 */     return new StreamSerializer();
/*      */   }
/*      */ 
/*      */   public StructureModifier<int[]> getIntegerArrays()
/*      */   {
/*  355 */     return this.structureModifier.withType([I.class);
/*      */   }
/*      */ 
/*      */   public StructureModifier<ItemStack> getItemModifier()
/*      */   {
/*  367 */     return this.structureModifier.withType(MinecraftReflection.getItemStackClass(), BukkitConverters.getItemStackConverter());
/*      */   }
/*      */ 
/*      */   public StructureModifier<ItemStack[]> getItemArrayModifier()
/*      */   {
/*  380 */     return this.structureModifier.withType(MinecraftReflection.getItemStackArrayClass(), BukkitConverters.getIgnoreNull(new ItemStackArrayConverter(null)));
/*      */   }
/*      */ 
/*      */   public StructureModifier<Map<WrappedStatistic, Integer>> getStatisticMaps()
/*      */   {
/*  392 */     return this.structureModifier.withType(Map.class, BukkitConverters.getMapConverter(MinecraftReflection.getStatisticClass(), BukkitConverters.getWrappedStatisticConverter()));
/*      */   }
/*      */ 
/*      */   public StructureModifier<WorldType> getWorldTypeModifier()
/*      */   {
/*  409 */     return this.structureModifier.withType(MinecraftReflection.getWorldTypeClass(), BukkitConverters.getWorldTypeConverter());
/*      */   }
/*      */ 
/*      */   public StructureModifier<WrappedDataWatcher> getDataWatcherModifier()
/*      */   {
/*  420 */     return this.structureModifier.withType(MinecraftReflection.getDataWatcherClass(), BukkitConverters.getDataWatcherConverter());
/*      */   }
/*      */ 
/*      */   public StructureModifier<Entity> getEntityModifier(@Nonnull World world)
/*      */   {
/*  437 */     Preconditions.checkNotNull(world, "world cannot be NULL.");
/*      */ 
/*  439 */     return this.structureModifier.withType(Integer.TYPE, BukkitConverters.getEntityConverter(world));
/*      */   }
/*      */ 
/*      */   public StructureModifier<Entity> getEntityModifier(@Nonnull PacketEvent event)
/*      */   {
/*  455 */     Preconditions.checkNotNull(event, "event cannot be NULL.");
/*  456 */     return getEntityModifier(event.getPlayer().getWorld());
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public StructureModifier<ChunkPosition> getPositionModifier()
/*      */   {
/*  468 */     return this.structureModifier.withType(MinecraftReflection.getChunkPositionClass(), ChunkPosition.getConverter());
/*      */   }
/*      */ 
/*      */   public StructureModifier<BlockPosition> getBlockPositionModifier()
/*      */   {
/*  479 */     return this.structureModifier.withType(MinecraftReflection.getBlockPositionClass(), BlockPosition.getConverter());
/*      */   }
/*      */ 
/*      */   public StructureModifier<ChunkCoordIntPair> getChunkCoordIntPairs()
/*      */   {
/*  490 */     return this.structureModifier.withType(MinecraftReflection.getChunkCoordIntPair(), ChunkCoordIntPair.getConverter());
/*      */   }
/*      */ 
/*      */   public StructureModifier<NbtBase<?>> getNbtModifier()
/*      */   {
/*  501 */     return this.structureModifier.withType(MinecraftReflection.getNBTBaseClass(), BukkitConverters.getNbtConverter());
/*      */   }
/*      */ 
/*      */   public StructureModifier<Vector> getVectors()
/*      */   {
/*  512 */     return this.structureModifier.withType(MinecraftReflection.getVec3DClass(), BukkitConverters.getVectorConverter());
/*      */   }
/*      */ 
/*      */   public StructureModifier<List<WrappedAttribute>> getAttributeCollectionModifier()
/*      */   {
/*  526 */     return this.structureModifier.withType(Collection.class, BukkitConverters.getListConverter(MinecraftReflection.getAttributeSnapshotClass(), BukkitConverters.getWrappedAttributeConverter()));
/*      */   }
/*      */ 
/*      */   public StructureModifier<List<ChunkPosition>> getPositionCollectionModifier()
/*      */   {
/*  544 */     return this.structureModifier.withType(Collection.class, BukkitConverters.getListConverter(MinecraftReflection.getChunkPositionClass(), ChunkPosition.getConverter()));
/*      */   }
/*      */ 
/*      */   public StructureModifier<List<BlockPosition>> getBlockPositionCollectionModifier()
/*      */   {
/*  562 */     return this.structureModifier.withType(Collection.class, BukkitConverters.getListConverter(MinecraftReflection.getBlockPositionClass(), BlockPosition.getConverter()));
/*      */   }
/*      */ 
/*      */   public StructureModifier<List<WrappedWatchableObject>> getWatchableCollectionModifier()
/*      */   {
/*  579 */     return this.structureModifier.withType(Collection.class, BukkitConverters.getListConverter(MinecraftReflection.getWatchableObjectClass(), BukkitConverters.getWatchableObjectConverter()));
/*      */   }
/*      */ 
/*      */   public StructureModifier<Material> getBlocks()
/*      */   {
/*  597 */     return this.structureModifier.withType(MinecraftReflection.getBlockClass(), BukkitConverters.getBlockConverter());
/*      */   }
/*      */ 
/*      */   public StructureModifier<WrappedGameProfile> getGameProfiles()
/*      */   {
/*  610 */     return this.structureModifier.withType(GameProfile.class, BukkitConverters.getWrappedGameProfileConverter());
/*      */   }
/*      */ 
/*      */   public StructureModifier<WrappedBlockData> getBlockData()
/*      */   {
/*  623 */     return this.structureModifier.withType(MinecraftReflection.getIBlockDataClass(), BukkitConverters.getWrappedBlockDataConverter());
/*      */   }
/*      */ 
/*      */   public StructureModifier<WrappedChatComponent> getChatComponents()
/*      */   {
/*  636 */     return this.structureModifier.withType(MinecraftReflection.getIChatBaseComponentClass(), BukkitConverters.getWrappedChatComponentConverter());
/*      */   }
/*      */ 
/*      */   public StructureModifier<WrappedChatComponent[]> getChatComponentArrays()
/*      */   {
/*  649 */     return this.structureModifier.withType(MinecraftReflection.getIChatBaseComponentArrayClass(), BukkitConverters.getIgnoreNull(new WrappedChatComponentArrayConverter(null)));
/*      */   }
/*      */ 
/*      */   public StructureModifier<WrappedServerPing> getServerPings()
/*      */   {
/*  663 */     return this.structureModifier.withType(MinecraftReflection.getServerPingClass(), BukkitConverters.getWrappedServerPingConverter());
/*      */   }
/*      */ 
/*      */   public StructureModifier<List<PlayerInfoData>> getPlayerInfoDataLists()
/*      */   {
/*  676 */     return this.structureModifier.withType(Collection.class, BukkitConverters.getListConverter(MinecraftReflection.getPlayerInfoDataClass(), PlayerInfoData.getConverter()));
/*      */   }
/*      */ 
/*      */   public StructureModifier<PacketType.Protocol> getProtocols()
/*      */   {
/*  690 */     return this.structureModifier.withType(EnumWrappers.getProtocolClass(), EnumWrappers.getProtocolConverter());
/*      */   }
/*      */ 
/*      */   public StructureModifier<EnumWrappers.ClientCommand> getClientCommands()
/*      */   {
/*  700 */     return this.structureModifier.withType(EnumWrappers.getClientCommandClass(), EnumWrappers.getClientCommandConverter());
/*      */   }
/*      */ 
/*      */   public StructureModifier<EnumWrappers.ChatVisibility> getChatVisibilities()
/*      */   {
/*  710 */     return this.structureModifier.withType(EnumWrappers.getChatVisibilityClass(), EnumWrappers.getChatVisibilityConverter());
/*      */   }
/*      */ 
/*      */   public StructureModifier<EnumWrappers.Difficulty> getDifficulties()
/*      */   {
/*  720 */     return this.structureModifier.withType(EnumWrappers.getDifficultyClass(), EnumWrappers.getDifficultyConverter());
/*      */   }
/*      */ 
/*      */   public StructureModifier<EnumWrappers.EntityUseAction> getEntityUseActions()
/*      */   {
/*  730 */     return this.structureModifier.withType(EnumWrappers.getEntityUseActionClass(), EnumWrappers.getEntityUseActionConverter());
/*      */   }
/*      */ 
/*      */   public StructureModifier<EnumWrappers.NativeGameMode> getGameModes()
/*      */   {
/*  740 */     return this.structureModifier.withType(EnumWrappers.getGameModeClass(), EnumWrappers.getGameModeConverter());
/*      */   }
/*      */ 
/*      */   public StructureModifier<EnumWrappers.ResourcePackStatus> getResourcePackStatus()
/*      */   {
/*  750 */     return this.structureModifier.withType(EnumWrappers.getResourcePackStatusClass(), EnumWrappers.getResourcePackStatusConverter());
/*      */   }
/*      */ 
/*      */   public StructureModifier<EnumWrappers.PlayerInfoAction> getPlayerInfoAction()
/*      */   {
/*  760 */     return this.structureModifier.withType(EnumWrappers.getPlayerInfoActionClass(), EnumWrappers.getPlayerInfoActionConverter());
/*      */   }
/*      */ 
/*      */   public StructureModifier<EnumWrappers.TitleAction> getTitleActions()
/*      */   {
/*  770 */     return this.structureModifier.withType(EnumWrappers.getTitleActionClass(), EnumWrappers.getTitleActionConverter());
/*      */   }
/*      */ 
/*      */   public StructureModifier<EnumWrappers.WorldBorderAction> getWorldBorderActions()
/*      */   {
/*  780 */     return this.structureModifier.withType(EnumWrappers.getWorldBorderActionClass(), EnumWrappers.getWorldBorderActionConverter());
/*      */   }
/*      */ 
/*      */   public StructureModifier<EnumWrappers.CombatEventType> getCombatEvents()
/*      */   {
/*  790 */     return this.structureModifier.withType(EnumWrappers.getCombatEventTypeClass(), EnumWrappers.getCombatEventTypeConverter());
/*      */   }
/*      */ 
/*      */   public StructureModifier<EnumWrappers.PlayerDigType> getPlayerDigTypes()
/*      */   {
/*  800 */     return this.structureModifier.withType(EnumWrappers.getPlayerDigTypeClass(), EnumWrappers.getPlayerDiggingActionConverter());
/*      */   }
/*      */ 
/*      */   public StructureModifier<EnumWrappers.PlayerAction> getPlayerActions()
/*      */   {
/*  810 */     return this.structureModifier.withType(EnumWrappers.getPlayerActionClass(), EnumWrappers.getEntityActionConverter());
/*      */   }
/*      */ 
/*      */   public StructureModifier<EnumWrappers.ScoreboardAction> getScoreboardActions()
/*      */   {
/*  820 */     return this.structureModifier.withType(EnumWrappers.getScoreboardActionClass(), EnumWrappers.getUpdateScoreActionConverter());
/*      */   }
/*      */ 
/*      */   public StructureModifier<EnumWrappers.Particle> getParticles()
/*      */   {
/*  830 */     return this.structureModifier.withType(EnumWrappers.getParticleClass(), EnumWrappers.getParticleConverter());
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public int getID()
/*      */   {
/*  842 */     return this.type.getLegacyId();
/*      */   }
/*      */ 
/*      */   public PacketType getType()
/*      */   {
/*  850 */     return this.type;
/*      */   }
/*      */ 
/*      */   public PacketContainer shallowClone()
/*      */   {
/*  862 */     Object clonedPacket = SHALLOW_CLONER.clone(getHandle());
/*  863 */     return new PacketContainer(getType(), clonedPacket);
/*      */   }
/*      */ 
/*      */   public PacketContainer deepClone()
/*      */   {
/*  877 */     Object clonedPacket = null;
/*      */ 
/*  880 */     if (CLONING_UNSUPPORTED.contains(this.type))
/*  881 */       clonedPacket = ((PacketContainer)SerializableCloner.clone(this)).getHandle();
/*      */     else {
/*  883 */       clonedPacket = DEEP_CLONER.clone(getHandle());
/*      */     }
/*  885 */     return new PacketContainer(getType(), clonedPacket);
/*      */   }
/*      */ 
/*      */   private static Function<AggregateCloner.BuilderParameters, Cloner> getSpecializedDeepClonerFactory()
/*      */   {
/*  891 */     return new Function()
/*      */     {
/*      */       public Cloner apply(@Nullable AggregateCloner.BuilderParameters param) {
/*  894 */         return new FieldCloner(param.getAggregateCloner(), param.getInstanceProvider())
/*      */         {
/*      */         };
/*      */       }
/*      */     };
/*      */   }
/*      */ 
/*      */   private void writeObject(ObjectOutputStream output)
/*      */     throws IOException
/*      */   {
/*  913 */     output.defaultWriteObject();
/*      */ 
/*  916 */     output.writeBoolean(this.handle != null);
/*      */     try
/*      */     {
/*  919 */       if (MinecraftReflection.isUsingNetty()) {
/*  920 */         ByteBuf buffer = createPacketBuffer();
/*  921 */         MinecraftMethods.getPacketWriteByteBufMethod().invoke(this.handle, new Object[] { buffer });
/*      */ 
/*  923 */         output.writeInt(buffer.readableBytes());
/*  924 */         buffer.readBytes(output, buffer.readableBytes());
/*      */       }
/*      */       else
/*      */       {
/*  928 */         output.writeInt(-1);
/*  929 */         getMethodLazily(writeMethods, this.handle.getClass(), "write", DataOutput.class).invoke(this.handle, new Object[] { new DataOutputStream(output) });
/*      */       }
/*      */     }
/*      */     catch (IllegalArgumentException e)
/*      */     {
/*  934 */       throw new IOException("Minecraft packet doesn't support DataOutputStream", e);
/*      */     } catch (IllegalAccessException e) {
/*  936 */       throw new RuntimeException("Insufficient security privileges.", e);
/*      */     } catch (InvocationTargetException e) {
/*  938 */       throw new IOException("Could not serialize Minecraft packet.", e);
/*      */     }
/*      */   }
/*      */ 
/*      */   private void readObject(ObjectInputStream input) throws ClassNotFoundException, IOException
/*      */   {
/*  944 */     input.defaultReadObject();
/*      */ 
/*  947 */     this.structureModifier = StructureCache.getStructure(this.type);
/*      */ 
/*  950 */     if (input.readBoolean())
/*      */     {
/*  953 */       this.handle = StructureCache.newPacket(this.type);
/*      */       try
/*      */       {
/*  957 */         if (MinecraftReflection.isUsingNetty()) {
/*  958 */           ByteBuf buffer = createPacketBuffer();
/*  959 */           buffer.writeBytes(input, input.readInt());
/*      */ 
/*  961 */           MinecraftMethods.getPacketReadByteBufMethod().invoke(this.handle, new Object[] { buffer });
/*      */         } else {
/*  963 */           if (input.readInt() != -1) {
/*  964 */             throw new IllegalArgumentException("Cannot load a packet from 1.7.2 in 1.6.4.");
/*      */           }
/*  966 */           getMethodLazily(readMethods, this.handle.getClass(), "read", DataInput.class).invoke(this.handle, new Object[] { new DataInputStream(input) });
/*      */         }
/*      */       }
/*      */       catch (IllegalArgumentException e) {
/*  970 */         throw new IOException("Minecraft packet doesn't support DataInputStream", e);
/*      */       } catch (IllegalAccessException e) {
/*  972 */         throw new RuntimeException("Insufficient security privileges.", e);
/*      */       } catch (InvocationTargetException e) {
/*  974 */         throw new IOException("Could not deserialize Minecraft packet.", e);
/*      */       }
/*      */ 
/*  978 */       this.structureModifier = this.structureModifier.withTarget(this.handle);
/*      */     }
/*      */   }
/*      */ 
/*      */   private ByteBuf createPacketBuffer()
/*      */   {
/*  987 */     return MinecraftReflection.getPacketDataSerializer(UnpooledByteBufAllocator.DEFAULT.buffer());
/*      */   }
/*      */ 
/*      */   private Method getMethodLazily(ConcurrentMap<Class<?>, Method> lookup, Class<?> handleClass, String methodName, Class<?> parameterClass)
/*      */   {
/* 1000 */     Method method = (Method)lookup.get(handleClass);
/*      */ 
/* 1003 */     if (method == null) {
/* 1004 */       Method initialized = FuzzyReflection.fromClass(handleClass).getMethod(FuzzyMethodContract.newBuilder().parameterCount(1).parameterDerivedOf(parameterClass).returnTypeVoid().build());
/*      */ 
/* 1010 */       method = (Method)lookup.putIfAbsent(handleClass, initialized);
/*      */ 
/* 1013 */       if (method == null) {
/* 1014 */         method = initialized;
/*      */       }
/*      */     }
/*      */ 
/* 1018 */     return method;
/*      */   }
/*      */ 
/*      */   private static class WrappedChatComponentArrayConverter
/*      */     implements EquivalentConverter<WrappedChatComponent[]>
/*      */   {
/* 1063 */     final EquivalentConverter<WrappedChatComponent> componentConverter = BukkitConverters.getWrappedChatComponentConverter();
/*      */ 
/*      */     public Object getGeneric(Class<?> genericType, WrappedChatComponent[] specific)
/*      */     {
/* 1067 */       Class nmsComponent = MinecraftReflection.getIChatBaseComponentClass();
/* 1068 */       Object[] result = (Object[])Array.newInstance(nmsComponent, specific.length);
/*      */ 
/* 1071 */       for (int i = 0; i < result.length; i++) {
/* 1072 */         result[i] = this.componentConverter.getGeneric(nmsComponent, specific[i]);
/*      */       }
/* 1074 */       return result;
/*      */     }
/*      */ 
/*      */     public WrappedChatComponent[] getSpecific(Object generic)
/*      */     {
/* 1079 */       Object[] input = (Object[])generic;
/* 1080 */       WrappedChatComponent[] result = new WrappedChatComponent[input.length];
/*      */ 
/* 1083 */       for (int i = 0; i < result.length; i++) {
/* 1084 */         result[i] = ((WrappedChatComponent)this.componentConverter.getSpecific(input[i]));
/*      */       }
/* 1086 */       return result;
/*      */     }
/*      */ 
/*      */     public Class<WrappedChatComponent[]> getSpecificType()
/*      */     {
/* 1091 */       return [Lcom.comphenix.protocol.wrappers.WrappedChatComponent.class;
/*      */     }
/*      */   }
/*      */ 
/*      */   private static class ItemStackArrayConverter
/*      */     implements EquivalentConverter<ItemStack[]>
/*      */   {
/* 1026 */     final EquivalentConverter<ItemStack> stackConverter = BukkitConverters.getItemStackConverter();
/*      */ 
/*      */     public Object getGeneric(Class<?> genericType, ItemStack[] specific)
/*      */     {
/* 1030 */       Class nmsStack = MinecraftReflection.getItemStackClass();
/* 1031 */       Object[] result = (Object[])Array.newInstance(nmsStack, specific.length);
/*      */ 
/* 1034 */       for (int i = 0; i < result.length; i++) {
/* 1035 */         result[i] = this.stackConverter.getGeneric(nmsStack, specific[i]);
/*      */       }
/* 1037 */       return result;
/*      */     }
/*      */ 
/*      */     public ItemStack[] getSpecific(Object generic)
/*      */     {
/* 1042 */       Object[] input = (Object[])generic;
/* 1043 */       ItemStack[] result = new ItemStack[input.length];
/*      */ 
/* 1046 */       for (int i = 0; i < result.length; i++) {
/* 1047 */         result[i] = ((ItemStack)this.stackConverter.getSpecific(input[i]));
/*      */       }
/* 1049 */       return result;
/*      */     }
/*      */ 
/*      */     public Class<ItemStack[]> getSpecificType()
/*      */     {
/* 1054 */       return [Lorg.bukkit.inventory.ItemStack.class;
/*      */     }
/*      */   }
/*      */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.events.PacketContainer
 * JD-Core Version:    0.6.2
 */