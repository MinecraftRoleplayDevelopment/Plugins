/*      */ package com.comphenix.protocol.utility;
/*      */ 
/*      */ import com.comphenix.net.sf.cglib.asm.ClassReader;
/*      */ import com.comphenix.net.sf.cglib.asm.MethodVisitor;
/*      */ import com.comphenix.protocol.PacketType;
/*      */ import com.comphenix.protocol.PacketType.Login.Client;
/*      */ import com.comphenix.protocol.PacketType.Play.Server;
/*      */ import com.comphenix.protocol.PacketType.Status.Server;
/*      */ import com.comphenix.protocol.ProtocolLibrary;
/*      */ import com.comphenix.protocol.error.ErrorReporter;
/*      */ import com.comphenix.protocol.error.Report;
/*      */ import com.comphenix.protocol.error.ReportType;
/*      */ import com.comphenix.protocol.injector.packet.PacketRegistry;
/*      */ import com.comphenix.protocol.reflect.ClassAnalyser;
/*      */ import com.comphenix.protocol.reflect.ClassAnalyser.AsmMethod;
/*      */ import com.comphenix.protocol.reflect.FuzzyReflection;
/*      */ import com.comphenix.protocol.reflect.accessors.Accessors;
/*      */ import com.comphenix.protocol.reflect.accessors.MethodAccessor;
/*      */ import com.comphenix.protocol.reflect.compiler.EmptyClassVisitor;
/*      */ import com.comphenix.protocol.reflect.compiler.EmptyMethodVisitor;
/*      */ import com.comphenix.protocol.reflect.fuzzy.AbstractFuzzyMatcher;
/*      */ import com.comphenix.protocol.reflect.fuzzy.FuzzyClassContract;
/*      */ import com.comphenix.protocol.reflect.fuzzy.FuzzyClassContract.Builder;
/*      */ import com.comphenix.protocol.reflect.fuzzy.FuzzyFieldContract;
/*      */ import com.comphenix.protocol.reflect.fuzzy.FuzzyFieldContract.Builder;
/*      */ import com.comphenix.protocol.reflect.fuzzy.FuzzyMatchers;
/*      */ import com.comphenix.protocol.reflect.fuzzy.FuzzyMethodContract;
/*      */ import com.comphenix.protocol.reflect.fuzzy.FuzzyMethodContract.Builder;
/*      */ import com.comphenix.protocol.wrappers.WrappedDataWatcher;
/*      */ import com.comphenix.protocol.wrappers.nbt.NbtFactory;
/*      */ import com.comphenix.protocol.wrappers.nbt.NbtType;
/*      */ import com.comphenix.protocol.wrappers.nbt.NbtWrapper;
/*      */ import com.google.common.collect.Maps;
/*      */ import com.mojang.authlib.GameProfile;
/*      */ import io.netty.buffer.ByteBuf;
/*      */ import java.io.DataInputStream;
/*      */ import java.io.DataOutput;
/*      */ import java.io.IOException;
/*      */ import java.lang.reflect.Array;
/*      */ import java.lang.reflect.Constructor;
/*      */ import java.lang.reflect.Field;
/*      */ import java.lang.reflect.Method;
/*      */ import java.net.InetAddress;
/*      */ import java.net.ServerSocket;
/*      */ import java.util.HashSet;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Set;
/*      */ import java.util.concurrent.ConcurrentMap;
/*      */ import java.util.logging.Level;
/*      */ import java.util.regex.Matcher;
/*      */ import java.util.regex.Pattern;
/*      */ import javax.annotation.Nonnull;
/*      */ import org.bukkit.Bukkit;
/*      */ import org.bukkit.Server;
/*      */ import org.bukkit.inventory.ItemStack;
/*      */ 
/*      */ public class MinecraftReflection
/*      */ {
/*   84 */   public static final ReportType REPORT_CANNOT_FIND_MCPC_REMAPPER = new ReportType("Cannot find MCPC remapper.");
/*   85 */   public static final ReportType REPORT_CANNOT_LOAD_CPC_REMAPPER = new ReportType("Unable to load MCPC remapper.");
/*   86 */   public static final ReportType REPORT_NON_CRAFTBUKKIT_LIBRARY_PACKAGE = new ReportType("Cannot find standard Minecraft library location. Assuming MCPC.");
/*      */   private static final String CANONICAL_REGEX = "(\\p{javaJavaIdentifierStart}\\p{javaJavaIdentifierPart}*\\.)+\\p{javaJavaIdentifierStart}\\p{javaJavaIdentifierPart}*";
/*      */ 
/*      */   @Deprecated
/*      */   public static final String MINECRAFT_OBJECT = "net\\.minecraft\\.(\\p{javaJavaIdentifierStart}\\p{javaJavaIdentifierPart}*\\.)+\\p{javaJavaIdentifierStart}\\p{javaJavaIdentifierPart}*";
/*  104 */   private static String DYNAMIC_PACKAGE_MATCHER = null;
/*      */   private static final String FORGE_ENTITY_PACKAGE = "net.minecraft.entity";
/*  114 */   private static String MINECRAFT_PREFIX_PACKAGE = "net.minecraft.server";
/*      */ 
/*  120 */   private static final Pattern PACKAGE_VERSION_MATCHER = Pattern.compile(".*\\.(v\\d+_\\d+_\\w*\\d+)");
/*      */ 
/*  122 */   private static String MINECRAFT_FULL_PACKAGE = null;
/*  123 */   private static String CRAFTBUKKIT_PACKAGE = null;
/*      */   static CachedPackage minecraftPackage;
/*      */   static CachedPackage craftbukkitPackage;
/*      */   static CachedPackage libraryPackage;
/*      */   private static Constructor<?> craftNMSConstructor;
/*      */   private static Constructor<?> craftBukkitConstructor;
/*      */   private static AbstractFuzzyMatcher<Class<?>> fuzzyMatcher;
/*      */   private static Method craftNMSMethod;
/*      */   private static Method craftBukkitNMS;
/*      */   private static Method craftBukkitOBC;
/*      */   private static boolean craftItemStackFailed;
/*      */   private static String packageVersion;
/*      */   private static Class<?> itemStackArrayClass;
/*  150 */   private static ConcurrentMap<Class<?>, MethodAccessor> getBukkitEntityCache = Maps.newConcurrentMap();
/*      */   private static ClassSource classSource;
/*      */   private static boolean initializing;
/*      */   private static Boolean cachedNetty;
/*      */ 
/*      */   public static String getMinecraftObjectRegex()
/*      */   {
/*  172 */     if (DYNAMIC_PACKAGE_MATCHER == null)
/*  173 */       getMinecraftPackage();
/*  174 */     return DYNAMIC_PACKAGE_MATCHER;
/*      */   }
/*      */ 
/*      */   public static AbstractFuzzyMatcher<Class<?>> getMinecraftObjectMatcher()
/*      */   {
/*  182 */     if (fuzzyMatcher == null)
/*  183 */       fuzzyMatcher = FuzzyMatchers.matchRegex(getMinecraftObjectRegex(), 50);
/*  184 */     return fuzzyMatcher;
/*      */   }
/*      */ 
/*      */   public static String getMinecraftPackage()
/*      */   {
/*  193 */     if (MINECRAFT_FULL_PACKAGE != null)
/*  194 */       return MINECRAFT_FULL_PACKAGE;
/*  195 */     if (initializing)
/*  196 */       throw new IllegalStateException("Already initializing minecraft package!");
/*  197 */     initializing = true;
/*      */ 
/*  199 */     Server craftServer = Bukkit.getServer();
/*      */ 
/*  202 */     if (craftServer != null) {
/*      */       try
/*      */       {
/*  205 */         Class craftClass = craftServer.getClass();
/*  206 */         CRAFTBUKKIT_PACKAGE = getPackage(craftClass.getCanonicalName());
/*      */ 
/*  209 */         Matcher packageMatcher = PACKAGE_VERSION_MATCHER.matcher(CRAFTBUKKIT_PACKAGE);
/*  210 */         if (packageMatcher.matches()) {
/*  211 */           packageVersion = packageMatcher.group(1);
/*      */         } else {
/*  213 */           MinecraftVersion version = new MinecraftVersion(craftServer);
/*      */ 
/*  216 */           if (MinecraftVersion.SCARY_UPDATE.compareTo(version) <= 0)
/*      */           {
/*  218 */             packageVersion = "v" + version.getMajor() + "_" + version.getMinor() + "_R1";
/*  219 */             ProtocolLibrary.log(Level.WARNING, "Assuming package version: " + packageVersion, new Object[0]);
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/*  224 */         handleLibigot();
/*      */ 
/*  227 */         Class craftEntity = getCraftEntityClass();
/*  228 */         Method getHandle = craftEntity.getMethod("getHandle", new Class[0]);
/*      */ 
/*  230 */         MINECRAFT_FULL_PACKAGE = getPackage(getHandle.getReturnType().getCanonicalName());
/*      */         String matcher;
/*  233 */         if (!MINECRAFT_FULL_PACKAGE.startsWith(MINECRAFT_PREFIX_PACKAGE))
/*      */         {
/*  235 */           if (MINECRAFT_FULL_PACKAGE.equals("net.minecraft.entity"))
/*      */           {
/*  237 */             MINECRAFT_FULL_PACKAGE = CachedPackage.combine(MINECRAFT_PREFIX_PACKAGE, packageVersion);
/*      */           }
/*      */           else {
/*  240 */             MINECRAFT_PREFIX_PACKAGE = MINECRAFT_FULL_PACKAGE;
/*      */           }
/*      */ 
/*  244 */           matcher = (MINECRAFT_PREFIX_PACKAGE.length() > 0 ? Pattern.quote(MINECRAFT_PREFIX_PACKAGE + ".") : "") + "(\\p{javaJavaIdentifierStart}\\p{javaJavaIdentifierPart}*\\.)+\\p{javaJavaIdentifierStart}\\p{javaJavaIdentifierPart}*";
/*      */ 
/*  249 */           setDynamicPackageMatcher("(" + matcher + ")|(" + "net\\.minecraft\\.(\\p{javaJavaIdentifierStart}\\p{javaJavaIdentifierPart}*\\.)+\\p{javaJavaIdentifierStart}\\p{javaJavaIdentifierPart}*" + ")");
/*      */         }
/*      */         else
/*      */         {
/*  253 */           setDynamicPackageMatcher("net\\.minecraft\\.(\\p{javaJavaIdentifierStart}\\p{javaJavaIdentifierPart}*\\.)+\\p{javaJavaIdentifierStart}\\p{javaJavaIdentifierPart}*");
/*      */         }
/*      */ 
/*  256 */         return MINECRAFT_FULL_PACKAGE;
/*      */       }
/*      */       catch (SecurityException e) {
/*  259 */         throw new RuntimeException("Security violation. Cannot get handle method.", e);
/*      */       } catch (NoSuchMethodException e) {
/*  261 */         throw new IllegalStateException("Cannot find getHandle() method on server. Is this a modified CraftBukkit version?", e);
/*      */       } finally {
/*  263 */         initializing = false;
/*      */       }
/*      */     }
/*      */ 
/*  267 */     initializing = false;
/*  268 */     throw new IllegalStateException("Could not find Bukkit. Is it running?");
/*      */   }
/*      */ 
/*      */   public static String getPackageVersion()
/*      */   {
/*  277 */     getMinecraftPackage();
/*  278 */     return packageVersion;
/*      */   }
/*      */ 
/*      */   private static void setDynamicPackageMatcher(String regex)
/*      */   {
/*  286 */     DYNAMIC_PACKAGE_MATCHER = regex;
/*      */ 
/*  289 */     fuzzyMatcher = null;
/*      */   }
/*      */ 
/*      */   private static void handleLibigot()
/*      */   {
/*      */     try {
/*  295 */       getCraftEntityClass();
/*      */     }
/*      */     catch (RuntimeException e) {
/*  298 */       craftbukkitPackage = null;
/*  299 */       CRAFTBUKKIT_PACKAGE = "org.bukkit.craftbukkit";
/*      */ 
/*  302 */       getCraftEntityClass();
/*      */     }
/*      */   }
/*      */ 
/*      */   public static void setMinecraftPackage(String minecraftPackage, String craftBukkitPackage)
/*      */   {
/*  312 */     MINECRAFT_FULL_PACKAGE = minecraftPackage;
/*  313 */     CRAFTBUKKIT_PACKAGE = craftBukkitPackage;
/*      */ 
/*  316 */     if (getMinecraftServerClass() == null) {
/*  317 */       throw new IllegalArgumentException("Cannot find MinecraftServer for package " + minecraftPackage);
/*      */     }
/*      */ 
/*  321 */     setDynamicPackageMatcher("net\\.minecraft\\.(\\p{javaJavaIdentifierStart}\\p{javaJavaIdentifierPart}*\\.)+\\p{javaJavaIdentifierStart}\\p{javaJavaIdentifierPart}*");
/*      */   }
/*      */ 
/*      */   public static String getCraftBukkitPackage()
/*      */   {
/*  330 */     if (CRAFTBUKKIT_PACKAGE == null)
/*  331 */       getMinecraftPackage();
/*  332 */     return CRAFTBUKKIT_PACKAGE;
/*      */   }
/*      */ 
/*      */   private static String getPackage(String fullName)
/*      */   {
/*  341 */     int index = fullName.lastIndexOf(".");
/*      */ 
/*  343 */     if (index > 0) {
/*  344 */       return fullName.substring(0, index);
/*      */     }
/*  346 */     return "";
/*      */   }
/*      */ 
/*      */   public static Object getBukkitEntity(Object nmsObject)
/*      */   {
/*  356 */     if (nmsObject == null) {
/*  357 */       return null;
/*      */     }
/*      */     try
/*      */     {
/*  361 */       Class clazz = nmsObject.getClass();
/*  362 */       MethodAccessor accessor = (MethodAccessor)getBukkitEntityCache.get(clazz);
/*      */ 
/*  364 */       if (accessor == null) {
/*  365 */         MethodAccessor created = Accessors.getMethodAccessor(clazz, "getBukkitEntity", new Class[0]);
/*  366 */         accessor = (MethodAccessor)getBukkitEntityCache.putIfAbsent(clazz, created);
/*      */ 
/*  369 */         if (accessor == null) {
/*  370 */           accessor = created;
/*      */         }
/*      */       }
/*  373 */       return accessor.invoke(nmsObject, new Object[0]);
/*      */     } catch (Exception e) {
/*  375 */       throw new IllegalArgumentException("Cannot get Bukkit entity from " + nmsObject, e);
/*      */     }
/*      */   }
/*      */ 
/*      */   public static boolean isMinecraftObject(@Nonnull Object obj)
/*      */   {
/*  385 */     if (obj == null) {
/*  386 */       return false;
/*      */     }
/*      */ 
/*  389 */     return obj.getClass().getName().startsWith(MINECRAFT_PREFIX_PACKAGE);
/*      */   }
/*      */ 
/*      */   public static boolean isMinecraftClass(@Nonnull Class<?> clazz)
/*      */   {
/*  398 */     if (clazz == null) {
/*  399 */       throw new IllegalArgumentException("clazz cannot be NULL.");
/*      */     }
/*  401 */     return getMinecraftObjectMatcher().isMatch(clazz, null);
/*      */   }
/*      */ 
/*      */   public static boolean isMinecraftObject(@Nonnull Object obj, String className)
/*      */   {
/*  411 */     if (obj == null) {
/*  412 */       return false;
/*      */     }
/*  414 */     String javaName = obj.getClass().getName();
/*  415 */     return (javaName.startsWith(MINECRAFT_PREFIX_PACKAGE)) && (javaName.endsWith(className));
/*      */   }
/*      */ 
/*      */   public static boolean isChunkPosition(Object obj)
/*      */   {
/*  424 */     Class chunkPosition = getChunkPositionClass();
/*  425 */     return (obj != null) && (chunkPosition != null) && (chunkPosition.isAssignableFrom(obj.getClass()));
/*      */   }
/*      */ 
/*      */   public static boolean isBlockPosition(Object obj)
/*      */   {
/*  434 */     return (obj != null) && (getBlockPositionClass().isAssignableFrom(obj.getClass()));
/*      */   }
/*      */ 
/*      */   public static boolean isChunkCoordIntPair(Object obj)
/*      */   {
/*  443 */     return (obj != null) && (getChunkCoordIntPair().isAssignableFrom(obj.getClass()));
/*      */   }
/*      */ 
/*      */   public static boolean isChunkCoordinates(Object obj)
/*      */   {
/*  452 */     Class chunkCoordinates = getChunkCoordinatesClass();
/*  453 */     return (obj != null) && (chunkCoordinates != null) && (chunkCoordinates.isAssignableFrom(obj.getClass()));
/*      */   }
/*      */ 
/*      */   public static boolean isPacketClass(Object obj)
/*      */   {
/*  462 */     return (obj != null) && (getPacketClass().isAssignableFrom(obj.getClass()));
/*      */   }
/*      */ 
/*      */   public static boolean isLoginHandler(Object obj)
/*      */   {
/*  471 */     return (obj != null) && (getNetLoginHandlerClass().isAssignableFrom(obj.getClass()));
/*      */   }
/*      */ 
/*      */   public static boolean isServerHandler(Object obj)
/*      */   {
/*  480 */     return (obj != null) && (getNetServerHandlerClass().isAssignableFrom(obj.getClass()));
/*      */   }
/*      */ 
/*      */   public static boolean isMinecraftEntity(Object obj)
/*      */   {
/*  489 */     return (obj != null) && (getEntityClass().isAssignableFrom(obj.getClass()));
/*      */   }
/*      */ 
/*      */   public static boolean isItemStack(Object value)
/*      */   {
/*  498 */     return (value != null) && (getItemStackClass().isAssignableFrom(value.getClass()));
/*      */   }
/*      */ 
/*      */   public static boolean isCraftPlayer(Object value)
/*      */   {
/*  507 */     return (value != null) && (getCraftPlayerClass().isAssignableFrom(value.getClass()));
/*      */   }
/*      */ 
/*      */   public static boolean isMinecraftPlayer(Object obj)
/*      */   {
/*  516 */     return (obj != null) && (getEntityPlayerClass().isAssignableFrom(obj.getClass()));
/*      */   }
/*      */ 
/*      */   public static boolean isWatchableObject(Object obj)
/*      */   {
/*  525 */     return (obj != null) && (getWatchableObjectClass().isAssignableFrom(obj.getClass()));
/*      */   }
/*      */ 
/*      */   public static boolean isDataWatcher(Object obj)
/*      */   {
/*  534 */     return (obj != null) && (getDataWatcherClass().isAssignableFrom(obj.getClass()));
/*      */   }
/*      */ 
/*      */   public static boolean isIntHashMap(Object obj)
/*      */   {
/*  543 */     return (obj != null) && (getIntHashMapClass().isAssignableFrom(obj.getClass()));
/*      */   }
/*      */ 
/*      */   public static boolean isCraftItemStack(Object obj)
/*      */   {
/*  552 */     return (obj != null) && (getCraftItemStackClass().isAssignableFrom(obj.getClass()));
/*      */   }
/*      */ 
/*      */   public static Class<?> getEntityPlayerClass()
/*      */   {
/*      */     try
/*      */     {
/*  561 */       return getMinecraftClass("EntityPlayer");
/*      */     }
/*      */     catch (RuntimeException e) {
/*      */       try {
/*  565 */         Method getHandle = FuzzyReflection.fromClass(getCraftBukkitClass("entity.CraftPlayer")).getMethodByName("getHandle");
/*      */ 
/*  570 */         return setMinecraftClass("EntityPlayer", getHandle.getReturnType());
/*      */       } catch (IllegalArgumentException e1) {
/*  572 */         throw new RuntimeException("Could not find EntityPlayer class.", e1);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public static Class<?> getEntityHumanClass()
/*      */   {
/*  583 */     return getEntityPlayerClass().getSuperclass();
/*      */   }
/*      */ 
/*      */   public static Class<?> getGameProfileClass()
/*      */   {
/*  592 */     if (!isUsingNetty())
/*  593 */       throw new IllegalStateException("GameProfile does not exist in version 1.6.4 and earlier.");
/*      */     try
/*      */     {
/*  596 */       return GameProfile.class;
/*      */     } catch (Throwable ex) {
/*  598 */       FuzzyReflection reflection = FuzzyReflection.fromClass(PacketType.Login.Client.START.getPacketClass(), true);
/*  599 */       FuzzyFieldContract contract = FuzzyFieldContract.newBuilder().banModifier(8).typeMatches(FuzzyMatchers.matchRegex("(.*)(GameProfile)", 1)).build();
/*      */ 
/*  603 */       return reflection.getField(contract).getType();
/*      */     }
/*      */   }
/*      */ 
/*      */   public static Class<?> getEntityClass()
/*      */   {
/*      */     try
/*      */     {
/*  613 */       return getMinecraftClass("Entity"); } catch (RuntimeException e) {
/*      */     }
/*  615 */     return fallbackMethodReturn("Entity", "entity.CraftEntity", "getHandle");
/*      */   }
/*      */ 
/*      */   public static Class<?> getCraftChatMessage()
/*      */   {
/*  624 */     return getCraftBukkitClass("util.CraftChatMessage");
/*      */   }
/*      */ 
/*      */   public static Class<?> getWorldServerClass()
/*      */   {
/*      */     try
/*      */     {
/*  633 */       return getMinecraftClass("WorldServer"); } catch (RuntimeException e) {
/*      */     }
/*  635 */     return fallbackMethodReturn("WorldServer", "CraftWorld", "getHandle");
/*      */   }
/*      */ 
/*      */   public static Class<?> getNmsWorldClass()
/*      */   {
/*      */     try
/*      */     {
/*  645 */       return getMinecraftClass("World"); } catch (RuntimeException e) {
/*      */     }
/*  647 */     return setMinecraftClass("World", getWorldServerClass().getSuperclass());
/*      */   }
/*      */ 
/*      */   private static Class<?> fallbackMethodReturn(String nmsClass, String craftClass, String methodName)
/*      */   {
/*  659 */     Class result = FuzzyReflection.fromClass(getCraftBukkitClass(craftClass)).getMethodByName(methodName).getReturnType();
/*      */ 
/*  663 */     return setMinecraftClass(nmsClass, result);
/*      */   }
/*      */ 
/*      */   public static Class<?> getPacketClass()
/*      */   {
/*      */     try
/*      */     {
/*  672 */       return getMinecraftClass("Packet");
/*      */     } catch (RuntimeException e) {
/*  674 */       FuzzyClassContract paketContract = null;
/*      */ 
/*  677 */       if (isUsingNetty()) {
/*  678 */         paketContract = FuzzyClassContract.newBuilder().method(FuzzyMethodContract.newBuilder().parameterDerivedOf(ByteBuf.class).returnTypeVoid()).method(FuzzyMethodContract.newBuilder().parameterDerivedOf(ByteBuf.class, 0).parameterExactType([B.class, 1).returnTypeVoid()).build();
/*      */       }
/*      */       else
/*      */       {
/*  688 */         paketContract = FuzzyClassContract.newBuilder().field(FuzzyFieldContract.newBuilder().typeDerivedOf(Map.class).requireModifier(8)).field(FuzzyFieldContract.newBuilder().typeDerivedOf(Set.class).requireModifier(8)).method(FuzzyMethodContract.newBuilder().parameterSuperOf(DataInputStream.class).returnTypeVoid()).build();
/*      */       }
/*      */ 
/*  702 */       Method selected = FuzzyReflection.fromClass(getNetServerHandlerClass()).getMethod(FuzzyMethodContract.newBuilder().parameterMatches(paketContract, 0).parameterCount(1).build());
/*      */ 
/*  710 */       Class clazz = getTopmostClass(selected.getParameterTypes()[0]);
/*  711 */       return setMinecraftClass("Packet", clazz);
/*      */     }
/*      */   }
/*      */ 
/*      */   public static Class<?> getEnumProtocolClass()
/*      */   {
/*      */     try
/*      */     {
/*  721 */       return getMinecraftClass("EnumProtocol");
/*      */     } catch (RuntimeException e) {
/*  723 */       Method protocolMethod = FuzzyReflection.fromClass(getNetworkManagerClass()).getMethod(FuzzyMethodContract.newBuilder().parameterCount(1).parameterDerivedOf(Enum.class, 0).build());
/*      */ 
/*  729 */       return setMinecraftClass("EnumProtocol", protocolMethod.getParameterTypes()[0]); }  } 
/*      */   public static Class<?> getIChatBaseComponentClass() { // Byte code:
/*      */     //   0: ldc_w 677
/*      */     //   3: invokestatic 488	com/comphenix/protocol/utility/MinecraftReflection:getMinecraftClass	(Ljava/lang/String;)Ljava/lang/Class;
/*      */     //   6: areturn
/*      */     //   7: astore_0
/*      */     //   8: ldc_w 677
/*      */     //   11: invokestatic 679	com/comphenix/protocol/utility/MinecraftReflection:getCraftChatMessage	()Ljava/lang/Class;
/*      */     //   14: ldc_w 681
/*      */     //   17: iconst_1
/*      */     //   18: anewarray 159	java/lang/Class
/*      */     //   21: dup
/*      */     //   22: iconst_0
/*      */     //   23: ldc_w 264
/*      */     //   26: aastore
/*      */     //   27: invokestatic 371	com/comphenix/protocol/reflect/accessors/Accessors:getMethodAccessor	(Ljava/lang/Class;Ljava/lang/String;[Ljava/lang/Class;)Lcom/comphenix/protocol/reflect/accessors/MethodAccessor;
/*      */     //   30: invokeinterface 684 1 0
/*      */     //   35: invokevirtual 260	java/lang/reflect/Method:getReturnType	()Ljava/lang/Class;
/*      */     //   38: invokevirtual 687	java/lang/Class:getComponentType	()Ljava/lang/Class;
/*      */     //   41: invokestatic 507	com/comphenix/protocol/utility/MinecraftReflection:setMinecraftClass	(Ljava/lang/String;Ljava/lang/Class;)Ljava/lang/Class;
/*      */     //   44: areturn
/*      */     //
/*      */     // Exception table:
/*      */     //   from	to	target	type
/*      */     //   0	6	7	java/lang/RuntimeException } 
/*  749 */   public static Class<?> getIChatBaseComponentArrayClass() { return getArrayClass(getIChatBaseComponentClass()); }
/*      */ 
/*      */ 
/*      */   public static Class<?> getChatComponentTextClass()
/*      */   {
/*      */     try
/*      */     {
/*  758 */       return getMinecraftClass("ChatComponentText");
/*      */     } catch (RuntimeException e) {
/*      */       try {
/*  761 */         Method getScoreboardDisplayName = FuzzyReflection.fromClass(getEntityClass()).getMethodByParameters("getScoreboardDisplayName", getIChatBaseComponentClass(), new Class[0]);
/*      */ 
/*  763 */         baseClass = getIChatBaseComponentClass();
/*      */ 
/*  765 */         for (ClassAnalyser.AsmMethod method : ClassAnalyser.getDefault().getMethodCalls(getScoreboardDisplayName)) {
/*  766 */           Class owner = method.getOwnerClass();
/*      */ 
/*  768 */           if ((isMinecraftClass(owner)) && (baseClass.isAssignableFrom(owner)))
/*  769 */             return setMinecraftClass("ChatComponentText", owner);
/*      */         }
/*      */       }
/*      */       catch (Exception e1)
/*      */       {
/*      */         Class baseClass;
/*  773 */         throw new IllegalStateException("Cannot find ChatComponentText class.", e);
/*      */       }
/*      */     }
/*  776 */     throw new IllegalStateException("Cannot find ChatComponentText class.");
/*      */   }
/*      */ 
/*      */   public static Class<?> getChatSerializerClass()
/*      */   {
/*      */     try
/*      */     {
/*  786 */       return getMinecraftClass("IChatBaseComponent$ChatSerializer", new String[] { "ChatSerializer" });
/*      */     }
/*      */     catch (RuntimeException e) {
/*  789 */       throw new IllegalStateException("Could not find ChatSerializer class.", e);
/*      */     }
/*      */   }
/*      */ 
/*      */   public static Class<?> getServerPingClass()
/*      */   {
/*  798 */     if (!isUsingNetty())
/*  799 */       throw new IllegalStateException("ServerPing is only supported in 1.7.2.");
/*      */     try
/*      */     {
/*  802 */       return getMinecraftClass("ServerPing");
/*      */     } catch (RuntimeException e) {
/*  804 */       Class statusServerInfo = PacketType.Status.Server.OUT_SERVER_INFO.getPacketClass();
/*      */ 
/*  807 */       AbstractFuzzyMatcher serverPingContract = FuzzyClassContract.newBuilder().field(FuzzyFieldContract.newBuilder().typeExact(String.class).build()).field(FuzzyFieldContract.newBuilder().typeDerivedOf(getIChatBaseComponentClass()).build()).build().and(getMinecraftObjectMatcher());
/*      */ 
/*  813 */       return setMinecraftClass("ServerPing", FuzzyReflection.fromClass(statusServerInfo, true).getField(FuzzyFieldContract.matchType(serverPingContract)).getType());
/*      */     }
/*      */   }
/*      */ 
/*      */   public static Class<?> getServerPingServerDataClass()
/*      */   {
/*  824 */     if (!isUsingNetty())
/*  825 */       throw new IllegalStateException("ServerPingServerData is only supported in 1.7.2.");
/*      */     try
/*      */     {
/*  828 */       return getMinecraftClass("ServerPing$ServerData", new String[] { "ServerPingServerData" });
/*      */     } catch (RuntimeException e) {
/*  830 */       Class serverPing = getServerPingClass();
/*      */ 
/*  832 */       for (Field field : FuzzyReflection.fromClass(serverPing, true).getFields()) {
/*  833 */         Class clazz = field.getType();
/*  834 */         if (clazz.getName().contains("ServerData")) {
/*  835 */           return setMinecraftClass("ServerData", clazz);
/*      */         }
/*      */       }
/*      */     }
/*  839 */     throw new IllegalStateException("Could not find ServerData class.");
/*      */   }
/*      */ 
/*      */   public static Class<?> getServerPingPlayerSampleClass()
/*      */   {
/*  848 */     if (!isUsingNetty())
/*  849 */       throw new IllegalStateException("ServerPingPlayerSample is only supported in 1.7.2.");
/*      */     try
/*      */     {
/*  852 */       return getMinecraftClass("ServerPing$ServerPingPlayerSample", new String[] { "ServerPingPlayerSample" });
/*      */     } catch (RuntimeException e) {
/*  854 */       Class serverPing = getServerPingClass();
/*      */ 
/*  857 */       AbstractFuzzyMatcher serverPlayerContract = FuzzyClassContract.newBuilder().constructor(FuzzyMethodContract.newBuilder().parameterExactArray(new Class[] { Integer.TYPE, Integer.TYPE })).field(FuzzyFieldContract.newBuilder().typeExact([Lcom.mojang.authlib.GameProfile.class)).build().and(getMinecraftObjectMatcher());
/*      */ 
/*  863 */       return setMinecraftClass("ServerPingPlayerSample", getTypeFromField(serverPing, serverPlayerContract));
/*      */     }
/*      */   }
/*      */ 
/*      */   private static Class<?> getTypeFromField(Class<?> clazz, AbstractFuzzyMatcher<Class<?>> fieldTypeMatcher)
/*      */   {
/*  874 */     FuzzyFieldContract fieldMatcher = FuzzyFieldContract.matchType(fieldTypeMatcher);
/*      */ 
/*  876 */     return FuzzyReflection.fromClass(clazz, true).getField(fieldMatcher).getType();
/*      */   }
/*      */ 
/*      */   public static boolean isUsingNetty()
/*      */   {
/*  887 */     if (cachedNetty == null) {
/*      */       try {
/*  889 */         cachedNetty = Boolean.valueOf(getEnumProtocolClass() != null);
/*      */       } catch (RuntimeException e) {
/*  891 */         cachedNetty = Boolean.valueOf(false);
/*      */       }
/*      */     }
/*  894 */     return cachedNetty.booleanValue();
/*      */   }
/*      */ 
/*      */   private static Class<?> getTopmostClass(Class<?> clazz)
/*      */   {
/*      */     while (true)
/*      */     {
/*  903 */       Class superClass = clazz.getSuperclass();
/*      */ 
/*  905 */       if ((superClass == Object.class) || (superClass == null)) {
/*  906 */         return clazz;
/*      */       }
/*  908 */       clazz = superClass;
/*      */     }
/*      */   }
/*      */ 
/*      */   public static Class<?> getMinecraftServerClass()
/*      */   {
/*      */     try
/*      */     {
/*  918 */       return getMinecraftClass("MinecraftServer");
/*      */     } catch (RuntimeException e) {
/*  920 */       useFallbackServer();
/*  921 */     }return getMinecraftClass("MinecraftServer");
/*      */   }
/*      */ 
/*      */   public static Class<?> getStatisticClass()
/*      */   {
/*  931 */     return getMinecraftClass("Statistic");
/*      */   }
/*      */ 
/*      */   public static Class<?> getStatisticListClass()
/*      */   {
/*  940 */     return getMinecraftClass("StatisticList");
/*      */   }
/*      */ 
/*      */   private static void useFallbackServer()
/*      */   {
/*  948 */     Constructor selected = FuzzyReflection.fromClass(getCraftBukkitClass("CraftServer")).getConstructor(FuzzyMethodContract.newBuilder().parameterMatches(getMinecraftObjectMatcher(), 0).parameterCount(2).build());
/*      */ 
/*  954 */     Class[] params = selected.getParameterTypes();
/*      */ 
/*  957 */     setMinecraftClass("MinecraftServer", params[0]);
/*  958 */     setMinecraftClass("ServerConfigurationManager", params[1]);
/*      */   }
/*      */ 
/*      */   public static Class<?> getPlayerListClass()
/*      */   {
/*      */     try
/*      */     {
/*  967 */       return getMinecraftClass("ServerConfigurationManager", new String[] { "PlayerList" });
/*      */     }
/*      */     catch (RuntimeException e) {
/*  970 */       useFallbackServer();
/*  971 */     }return getMinecraftClass("ServerConfigurationManager");
/*      */   }
/*      */ 
/*      */   public static Class<?> getNetLoginHandlerClass()
/*      */   {
/*      */     try
/*      */     {
/*  981 */       return getMinecraftClass("NetLoginHandler", new String[] { "PendingConnection" });
/*      */     } catch (RuntimeException e) {
/*  983 */       Method selected = FuzzyReflection.fromClass(getPlayerListClass()).getMethod(FuzzyMethodContract.newBuilder().parameterMatches(FuzzyMatchers.matchExact(getEntityPlayerClass()).inverted(), 0).parameterExactType(String.class, 1).parameterExactType(String.class, 2).build());
/*      */ 
/*  994 */       return setMinecraftClass("NetLoginHandler", selected.getParameterTypes()[0]);
/*      */     }
/*      */   }
/*      */ 
/*      */   public static Class<?> getNetServerHandlerClass()
/*      */   {
/*      */     try
/*      */     {
/* 1004 */       return getMinecraftClass("NetServerHandler", new String[] { "PlayerConnection" });
/*      */     }
/*      */     catch (RuntimeException e) {
/*      */       try {
/* 1008 */         return setMinecraftClass("NetServerHandler", FuzzyReflection.fromClass(getEntityPlayerClass()).getFieldByType("playerConnection", getNetHandlerClass()).getType());
/*      */       }
/*      */       catch (RuntimeException e1)
/*      */       {
/* 1015 */         Class playerClass = getEntityPlayerClass();
/*      */ 
/* 1017 */         FuzzyClassContract playerConnection = FuzzyClassContract.newBuilder().field(FuzzyFieldContract.newBuilder().typeExact(playerClass).build()).constructor(FuzzyMethodContract.newBuilder().parameterCount(3).parameterSuperOf(getMinecraftServerClass(), 0).parameterSuperOf(getEntityPlayerClass(), 2).build()).method(FuzzyMethodContract.newBuilder().parameterCount(1).parameterExactType(String.class).build()).build();
/*      */ 
/* 1033 */         Class fieldType = FuzzyReflection.fromClass(getEntityPlayerClass(), true).getField(FuzzyFieldContract.newBuilder().typeMatches(playerConnection).build()).getType();
/*      */ 
/* 1037 */         return setMinecraftClass("NetServerHandler", fieldType);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public static Class<?> getNetworkManagerClass()
/*      */   {
/*      */     try
/*      */     {
/* 1048 */       return getMinecraftClass("INetworkManager", new String[] { "NetworkManager" });
/*      */     } catch (RuntimeException e) {
/* 1050 */       Constructor selected = FuzzyReflection.fromClass(getNetServerHandlerClass()).getConstructor(FuzzyMethodContract.newBuilder().parameterSuperOf(getMinecraftServerClass(), 0).parameterSuperOf(getEntityPlayerClass(), 2).build());
/*      */ 
/* 1058 */       return setMinecraftClass("INetworkManager", selected.getParameterTypes()[1]);
/*      */     }
/*      */   }
/*      */ 
/*      */   public static Class<?> getNetHandlerClass()
/*      */   {
/*      */     try
/*      */     {
/* 1068 */       return getMinecraftClass("NetHandler", new String[] { "Connection" });
/*      */     } catch (RuntimeException e) {
/*      */     }
/* 1071 */     return setMinecraftClass("NetHandler", getNetLoginHandlerClass().getSuperclass());
/*      */   }
/*      */ 
/*      */   public static Class<?> getItemStackClass()
/*      */   {
/*      */     try
/*      */     {
/* 1081 */       return getMinecraftClass("ItemStack");
/*      */     } catch (RuntimeException e) {
/*      */     }
/* 1084 */     return setMinecraftClass("ItemStack", FuzzyReflection.fromClass(getCraftItemStackClass(), true).getFieldByName("handle").getType());
/*      */   }
/*      */ 
/*      */   public static Class<?> getBlockClass()
/*      */   {
/*      */     try
/*      */     {
/* 1095 */       return getMinecraftClass("Block");
/*      */     } catch (RuntimeException e) {
/* 1097 */       FuzzyReflection reflect = FuzzyReflection.fromClass(getItemStackClass());
/* 1098 */       Set candidates = new HashSet();
/*      */ 
/* 1101 */       for (Constructor constructor : reflect.getConstructors()) {
/* 1102 */         for (Class clazz : constructor.getParameterTypes()) {
/* 1103 */           if (isMinecraftClass(clazz)) {
/* 1104 */             candidates.add(clazz);
/*      */           }
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 1110 */       Method selected = reflect.getMethod(FuzzyMethodContract.newBuilder().parameterMatches(FuzzyMatchers.matchAnyOf(candidates)).returnTypeExact(Float.TYPE).build());
/*      */ 
/* 1115 */       return setMinecraftClass("Block", selected.getParameterTypes()[0]);
/*      */     }
/*      */   }
/*      */ 
/*      */   public static Class<?> getWorldTypeClass()
/*      */   {
/*      */     try
/*      */     {
/* 1125 */       return getMinecraftClass("WorldType");
/*      */     }
/*      */     catch (RuntimeException e) {
/* 1128 */       Method selected = FuzzyReflection.fromClass(getMinecraftServerClass(), true).getMethod(FuzzyMethodContract.newBuilder().parameterExactType(String.class, 0).parameterExactType(String.class, 1).parameterMatches(getMinecraftObjectMatcher()).parameterExactType(String.class, 4).parameterCount(5).build());
/*      */ 
/* 1137 */       return setMinecraftClass("WorldType", selected.getParameterTypes()[3]);
/*      */     }
/*      */   }
/*      */ 
/*      */   public static Class<?> getDataWatcherClass()
/*      */   {
/*      */     try
/*      */     {
/* 1147 */       return getMinecraftClass("DataWatcher");
/*      */     }
/*      */     catch (RuntimeException e) {
/* 1150 */       FuzzyClassContract dataWatcherContract = FuzzyClassContract.newBuilder().field(FuzzyFieldContract.newBuilder().requireModifier(8).typeDerivedOf(Map.class)).field(FuzzyFieldContract.newBuilder().banModifier(8).typeDerivedOf(Map.class)).method(FuzzyMethodContract.newBuilder().parameterExactType(Integer.TYPE).parameterExactType(Object.class).returnTypeVoid()).build();
/*      */ 
/* 1162 */       FuzzyFieldContract fieldContract = FuzzyFieldContract.newBuilder().typeMatches(dataWatcherContract).build();
/*      */ 
/* 1167 */       return setMinecraftClass("DataWatcher", FuzzyReflection.fromClass(getEntityClass(), true).getField(fieldContract).getType());
/*      */     }
/*      */   }
/*      */ 
/*      */   public static Class<?> getChunkPositionClass()
/*      */   {
/*      */     try
/*      */     {
/* 1182 */       return getMinecraftClass("ChunkPosition");
/*      */     }
/*      */     catch (RuntimeException e)
/*      */     {
/*      */     }
/*      */ 
/* 1197 */     return null;
/*      */   }
/*      */ 
/*      */   public static Class<?> getBlockPositionClass()
/*      */   {
/*      */     try
/*      */     {
/* 1207 */       return getMinecraftClass("BlockPosition");
/*      */     } catch (RuntimeException e) {
/* 1209 */       Class normalChunkGenerator = getCraftBukkitClass("generator.NormalChunkGenerator");
/*      */ 
/* 1212 */       FuzzyMethodContract selected = FuzzyMethodContract.newBuilder().banModifier(8).parameterMatches(getMinecraftObjectMatcher(), 0).parameterExactType(String.class, 1).parameterMatches(getMinecraftObjectMatcher(), 1).build();
/*      */ 
/* 1219 */       return setMinecraftClass("BlockPosition", FuzzyReflection.fromClass(normalChunkGenerator).getMethod(selected).getReturnType());
/*      */     }
/*      */   }
/*      */ 
/*      */   public static Class<?> getVec3DClass()
/*      */   {
/*      */     try
/*      */     {
/* 1230 */       return getMinecraftClass("Vec3D");
/*      */     } catch (RuntimeException e) {
/*      */     }
/* 1233 */     return null;
/*      */   }
/*      */ 
/*      */   public static Class<?> getChunkCoordinatesClass()
/*      */   {
/*      */     try
/*      */     {
/* 1243 */       return getMinecraftClass("ChunkCoordinates"); } catch (RuntimeException e) {
/*      */     }
/* 1245 */     return setMinecraftClass("ChunkCoordinates", WrappedDataWatcher.getTypeClass(6));
/*      */   }
/*      */ 
/*      */   public static Class<?> getChunkCoordIntPair()
/*      */   {
/* 1254 */     if (!isUsingNetty())
/* 1255 */       throw new IllegalArgumentException("Not supported on 1.6.4 and older.");
/*      */     try
/*      */     {
/* 1258 */       return getMinecraftClass("ChunkCoordIntPair");
/*      */     } catch (RuntimeException e) {
/* 1260 */       Class packet = PacketRegistry.getPacketClassFromType(PacketType.Play.Server.MULTI_BLOCK_CHANGE);
/*      */ 
/* 1262 */       AbstractFuzzyMatcher chunkCoordIntContract = FuzzyClassContract.newBuilder().field(FuzzyFieldContract.newBuilder().typeDerivedOf(Integer.TYPE)).field(FuzzyFieldContract.newBuilder().typeDerivedOf(Integer.TYPE)).method(FuzzyMethodContract.newBuilder().parameterExactArray(new Class[] { Integer.TYPE }).returnDerivedOf(getChunkPositionClass())).build().and(getMinecraftObjectMatcher());
/*      */ 
/* 1272 */       Field field = FuzzyReflection.fromClass(packet, true).getField(FuzzyFieldContract.matchType(chunkCoordIntContract));
/*      */ 
/* 1274 */       return setMinecraftClass("ChunkCoordIntPair", field.getType());
/*      */     }
/*      */   }
/*      */ 
/*      */   public static Class<?> getWatchableObjectClass()
/*      */   {
/*      */     try
/*      */     {
/* 1284 */       return getMinecraftClass("WatchableObject");
/*      */     } catch (RuntimeException e) {
/* 1286 */       Method selected = FuzzyReflection.fromClass(getDataWatcherClass(), true).getMethod(FuzzyMethodContract.newBuilder().requireModifier(8).parameterDerivedOf(isUsingNetty() ? getPacketDataSerializerClass() : DataOutput.class, 0).parameterMatches(getMinecraftObjectMatcher(), 1).build());
/*      */ 
/* 1294 */       return setMinecraftClass("WatchableObject", selected.getParameterTypes()[1]);
/*      */     }
/*      */   }
/*      */ 
/*      */   public static Class<?> getServerConnectionClass()
/*      */   {
/*      */     try
/*      */     {
/* 1304 */       return getMinecraftClass("ServerConnection");
/*      */     } catch (RuntimeException e) {
/* 1306 */       Method selected = null;
/* 1307 */       FuzzyClassContract.Builder serverConnectionContract = FuzzyClassContract.newBuilder().constructor(FuzzyMethodContract.newBuilder().parameterExactType(getMinecraftServerClass()).parameterCount(1));
/*      */ 
/* 1312 */       if (isUsingNetty()) {
/* 1313 */         serverConnectionContract.method(FuzzyMethodContract.newBuilder().parameterDerivedOf(InetAddress.class, 0).parameterDerivedOf(Integer.TYPE, 1).parameterCount(2));
/*      */ 
/* 1320 */         selected = FuzzyReflection.fromClass(getMinecraftServerClass()).getMethod(FuzzyMethodContract.newBuilder().requireModifier(1).returnTypeMatches(serverConnectionContract.build()).build());
/*      */       }
/*      */       else
/*      */       {
/* 1327 */         serverConnectionContract.method(FuzzyMethodContract.newBuilder().parameterExactType(getNetServerHandlerClass()));
/*      */ 
/* 1331 */         selected = FuzzyReflection.fromClass(getMinecraftServerClass()).getMethod(FuzzyMethodContract.newBuilder().requireModifier(1024).returnTypeMatches(serverConnectionContract.build()).build());
/*      */       }
/*      */ 
/* 1339 */       return setMinecraftClass("ServerConnection", selected.getReturnType());
/*      */     }
/*      */   }
/*      */ 
/*      */   public static Class<?> getNBTBaseClass()
/*      */   {
/*      */     try
/*      */     {
/* 1349 */       return getMinecraftClass("NBTBase");
/*      */     } catch (RuntimeException e) {
/* 1351 */       Class nbtBase = null;
/*      */ 
/* 1353 */       if (isUsingNetty()) {
/* 1354 */         FuzzyClassContract tagCompoundContract = FuzzyClassContract.newBuilder().field(FuzzyFieldContract.newBuilder().typeDerivedOf(Map.class)).method(FuzzyMethodContract.newBuilder().parameterDerivedOf(DataOutput.class).parameterCount(1)).build();
/*      */ 
/* 1362 */         Method selected = FuzzyReflection.fromClass(getPacketDataSerializerClass()).getMethod(FuzzyMethodContract.newBuilder().banModifier(8).parameterCount(1).parameterMatches(tagCompoundContract).returnTypeVoid().build());
/*      */ 
/* 1370 */         nbtBase = selected.getParameterTypes()[0].getSuperclass();
/*      */       }
/*      */       else {
/* 1373 */         FuzzyClassContract tagCompoundContract = FuzzyClassContract.newBuilder().constructor(FuzzyMethodContract.newBuilder().parameterExactType(String.class).parameterCount(1)).field(FuzzyFieldContract.newBuilder().typeDerivedOf(Map.class)).build();
/*      */ 
/* 1381 */         Method selected = FuzzyReflection.fromClass(getPacketClass()).getMethod(FuzzyMethodContract.newBuilder().requireModifier(8).parameterSuperOf(DataInputStream.class).parameterCount(1).returnTypeMatches(tagCompoundContract).build());
/*      */ 
/* 1389 */         nbtBase = selected.getReturnType().getSuperclass();
/*      */       }
/*      */ 
/* 1393 */       if ((nbtBase == null) || (nbtBase.equals(Object.class))) {
/* 1394 */         throw new IllegalStateException("Unable to find NBT base class: " + nbtBase);
/*      */       }
/*      */ 
/* 1398 */       return setMinecraftClass("NBTBase", nbtBase);
/*      */     }
/*      */   }
/*      */ 
/*      */   public static Class<?> getNBTReadLimiterClass()
/*      */   {
/* 1409 */     return getMinecraftClass("NBTReadLimiter");
/*      */   }
/*      */ 
/*      */   public static Class<?> getNBTCompoundClass()
/*      */   {
/*      */     try
/*      */     {
/* 1418 */       return getMinecraftClass("NBTTagCompound"); } catch (RuntimeException e) {
/*      */     }
/* 1420 */     return setMinecraftClass("NBTTagCompound", NbtFactory.ofWrapper(NbtType.TAG_COMPOUND, "Test").getHandle().getClass());
/*      */   }
/*      */ 
/*      */   public static Class<?> getEntityTrackerClass()
/*      */   {
/*      */     try
/*      */     {
/* 1433 */       return getMinecraftClass("EntityTracker");
/*      */     } catch (RuntimeException e) {
/* 1435 */       FuzzyClassContract entityTrackerContract = FuzzyClassContract.newBuilder().field(FuzzyFieldContract.newBuilder().typeDerivedOf(Set.class)).method(FuzzyMethodContract.newBuilder().parameterSuperOf(getEntityClass()).parameterCount(1).returnTypeVoid()).method(FuzzyMethodContract.newBuilder().parameterSuperOf(getEntityClass(), 0).parameterSuperOf(Integer.TYPE, 1).parameterSuperOf(Integer.TYPE, 2).parameterCount(3).returnTypeVoid()).build();
/*      */ 
/* 1450 */       Field selected = FuzzyReflection.fromClass(getWorldServerClass(), true).getField(FuzzyFieldContract.newBuilder().typeMatches(entityTrackerContract).build());
/*      */ 
/* 1457 */       return setMinecraftClass("EntityTracker", selected.getType());
/*      */     }
/*      */   }
/*      */ 
/*      */   public static Class<?> getNetworkListenThreadClass()
/*      */   {
/*      */     try
/*      */     {
/* 1469 */       return getMinecraftClass("NetworkListenThread");
/*      */     } catch (RuntimeException e) {
/* 1471 */       FuzzyClassContract networkListenContract = FuzzyClassContract.newBuilder().field(FuzzyFieldContract.newBuilder().typeDerivedOf(ServerSocket.class)).field(FuzzyFieldContract.newBuilder().typeDerivedOf(Thread.class)).field(FuzzyFieldContract.newBuilder().typeDerivedOf(List.class)).method(FuzzyMethodContract.newBuilder().parameterExactType(getNetServerHandlerClass())).build();
/*      */ 
/* 1482 */       Field selected = FuzzyReflection.fromClass(getMinecraftServerClass(), true).getField(FuzzyFieldContract.newBuilder().typeMatches(networkListenContract).build());
/*      */ 
/* 1489 */       return setMinecraftClass("NetworkListenThread", selected.getType());
/*      */     }
/*      */   }
/*      */ 
/*      */   public static Class<?> getAttributeSnapshotClass()
/*      */   {
/*      */     try
/*      */     {
/* 1501 */       return getMinecraftClass("PacketPlayOutUpdateAttributes$AttributeSnapshot", new String[] { "AttributeSnapshot" });
/*      */     } catch (RuntimeException e) {
/* 1503 */       Class packetUpdateAttributes = PacketRegistry.getPacketClassFromType(PacketType.Play.Server.UPDATE_ATTRIBUTES, true);
/* 1504 */       String packetSignature = packetUpdateAttributes.getCanonicalName().replace('.', '/');
/*      */       try
/*      */       {
/* 1508 */         ClassReader reader = new ClassReader(packetUpdateAttributes.getCanonicalName());
/*      */ 
/* 1510 */         reader.accept(new EmptyClassVisitor()
/*      */         {
/*      */           public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions)
/*      */           {
/* 1514 */             if (desc.startsWith("(Ljava/io/DataInput")) {
/* 1515 */               return new EmptyMethodVisitor()
/*      */               {
/*      */                 public void visitMethodInsn(int opcode, String owner, String name, String desc) {
/* 1518 */                   if ((opcode == 183) && (MinecraftReflection.isConstructor(name))) {
/* 1519 */                     String className = owner.replace('/', '.');
/*      */ 
/* 1522 */                     if (desc.startsWith("(L" + MinecraftReflection.1.this.val$packetSignature))
/* 1523 */                       MinecraftReflection.setMinecraftClass("AttributeSnapshot", MinecraftReflection.access$100(className));
/* 1524 */                     else if (desc.startsWith("(Ljava/util/UUID;Ljava/lang/String")) {
/* 1525 */                       MinecraftReflection.setMinecraftClass("AttributeModifier", MinecraftReflection.access$100(className));
/*      */                     }
/*      */                   }
/*      */                 }
/*      */               };
/*      */             }
/* 1531 */             return null;
/*      */           }
/*      */         }
/*      */         , 0);
/*      */       }
/*      */       catch (IOException e1)
/*      */       {
/* 1536 */         throw new RuntimeException("Unable to read the content of Packet44UpdateAttributes.", e1);
/*      */       }
/*      */     }
/*      */ 
/* 1540 */     return getMinecraftClass("AttributeSnapshot");
/*      */   }
/*      */ 
/*      */   public static Class<?> getIntHashMapClass()
/*      */   {
/*      */     try
/*      */     {
/* 1550 */       return getMinecraftClass("IntHashMap");
/*      */     } catch (RuntimeException e) {
/* 1552 */       Class parent = getEntityTrackerClass();
/*      */ 
/* 1555 */       FuzzyClassContract intHashContract = FuzzyClassContract.newBuilder().method(FuzzyMethodContract.newBuilder().parameterCount(2).parameterExactType(Integer.TYPE, 0).parameterExactType(Object.class, 1).requirePublic()).method(FuzzyMethodContract.newBuilder().parameterCount(1).parameterExactType(Integer.TYPE).returnTypeExact(Object.class).requirePublic()).field(FuzzyFieldContract.newBuilder().typeMatches(FuzzyMatchers.matchArray(FuzzyMatchers.matchAll()))).build();
/*      */ 
/* 1574 */       AbstractFuzzyMatcher intHashField = FuzzyFieldContract.newBuilder().typeMatches(getMinecraftObjectMatcher().and(intHashContract)).build();
/*      */ 
/* 1579 */       return setMinecraftClass("IntHashMap", FuzzyReflection.fromClass(parent).getField(intHashField).getType());
/*      */     }
/*      */   }
/*      */ 
/*      */   public static Class<?> getAttributeModifierClass()
/*      */   {
/*      */     try
/*      */     {
/* 1589 */       return getMinecraftClass("AttributeModifier");
/*      */     }
/*      */     catch (RuntimeException e) {
/* 1592 */       getAttributeSnapshotClass();
/* 1593 */     }return getMinecraftClass("AttributeModifier");
/*      */   }
/*      */ 
/*      */   public static Class<?> getMobEffectClass()
/*      */   {
/*      */     try
/*      */     {
/* 1603 */       return getMinecraftClass("MobEffect");
/*      */     }
/*      */     catch (RuntimeException e) {
/* 1606 */       Class packet = PacketRegistry.getPacketClassFromType(PacketType.Play.Server.ENTITY_EFFECT);
/* 1607 */       Constructor constructor = FuzzyReflection.fromClass(packet).getConstructor(FuzzyMethodContract.newBuilder().parameterCount(2).parameterExactType(Integer.TYPE, 0).parameterMatches(getMinecraftObjectMatcher(), 1).build());
/*      */ 
/* 1614 */       return setMinecraftClass("MobEffect", constructor.getParameterTypes()[1]);
/*      */     }
/*      */   }
/*      */ 
/*      */   public static Class<?> getPacketDataSerializerClass()
/*      */   {
/*      */     try
/*      */     {
/* 1624 */       return getMinecraftClass("PacketDataSerializer");
/*      */     } catch (RuntimeException e) {
/* 1626 */       Class packet = getPacketClass();
/* 1627 */       Method method = FuzzyReflection.fromClass(packet).getMethod(FuzzyMethodContract.newBuilder().parameterCount(1).parameterDerivedOf(ByteBuf.class).returnTypeVoid().build());
/*      */ 
/* 1634 */       return setMinecraftClass("PacketDataSerializer", method.getParameterTypes()[0]);
/*      */     }
/*      */   }
/*      */ 
/*      */   public static Class<?> getNbtCompressedStreamToolsClass()
/*      */   {
/*      */     try
/*      */     {
/* 1644 */       return getMinecraftClass("NBTCompressedStreamTools");
/*      */     } catch (RuntimeException e) {
/* 1646 */       Class packetSerializer = getPacketDataSerializerClass();
/*      */ 
/* 1649 */       Method writeNbt = FuzzyReflection.fromClass(packetSerializer).getMethodByParameters("writeNbt", new Class[] { getNBTCompoundClass() });
/*      */       try
/*      */       {
/* 1654 */         for (ClassAnalyser.AsmMethod method : ClassAnalyser.getDefault().getMethodCalls(writeNbt)) {
/* 1655 */           Class owner = method.getOwnerClass();
/*      */ 
/* 1657 */           if ((!packetSerializer.equals(owner)) && (isMinecraftClass(owner)))
/* 1658 */             return setMinecraftClass("NBTCompressedStreamTools", owner);
/*      */         }
/*      */       }
/*      */       catch (Exception e1) {
/* 1662 */         throw new RuntimeException("Unable to analyse class.", e1);
/*      */       }
/*      */     }
/* 1664 */     throw new IllegalArgumentException("Unable to find NBTCompressedStreamTools.");
/*      */   }
/*      */ 
/*      */   public static ByteBuf getPacketDataSerializer(ByteBuf buffer)
/*      */   {
/* 1674 */     Class packetSerializer = getPacketDataSerializerClass();
/*      */     try
/*      */     {
/* 1677 */       return (ByteBuf)packetSerializer.getConstructor(new Class[] { ByteBuf.class }).newInstance(new Object[] { buffer });
/*      */     } catch (Exception e) {
/* 1679 */       throw new RuntimeException("Cannot construct packet serializer.", e);
/*      */     }
/*      */   }
/*      */ 
/*      */   public static Class<?> getTileEntityClass()
/*      */   {
/* 1688 */     return getMinecraftClass("TileEntity");
/*      */   }
/*      */ 
/*      */   public static Class<?> getMinecraftGsonClass()
/*      */   {
/*      */     try
/*      */     {
/* 1697 */       return getClass("com.google.gson.Gson"); } catch (RuntimeException e) {
/*      */     }
/* 1699 */     return getClass("org.bukkit.craftbukkit.libs.com.google.gson.Gson");
/*      */   }
/*      */ 
/*      */   private static boolean isConstructor(String name)
/*      */   {
/* 1709 */     return "<init>".equals(name);
/*      */   }
/*      */ 
/*      */   public static Class<?> getItemStackArrayClass()
/*      */   {
/* 1717 */     if (itemStackArrayClass == null)
/* 1718 */       itemStackArrayClass = getArrayClass(getItemStackClass());
/* 1719 */     return itemStackArrayClass;
/*      */   }
/*      */ 
/*      */   public static Class<?> getArrayClass(Class<?> componentType)
/*      */   {
/* 1729 */     return Array.newInstance(componentType, 0).getClass();
/*      */   }
/*      */ 
/*      */   public static Class<?> getCraftItemStackClass()
/*      */   {
/* 1737 */     return getCraftBukkitClass("inventory.CraftItemStack");
/*      */   }
/*      */ 
/*      */   public static Class<?> getCraftPlayerClass()
/*      */   {
/* 1745 */     return getCraftBukkitClass("entity.CraftPlayer");
/*      */   }
/*      */ 
/*      */   public static Class<?> getCraftWorldClass()
/*      */   {
/* 1753 */     return getCraftBukkitClass("CraftWorld");
/*      */   }
/*      */ 
/*      */   public static Class<?> getCraftEntityClass()
/*      */   {
/* 1761 */     return getCraftBukkitClass("entity.CraftEntity");
/*      */   }
/*      */ 
/*      */   public static Class<?> getCraftMessageClass()
/*      */   {
/* 1769 */     return getCraftBukkitClass("util.CraftChatMessage");
/*      */   }
/*      */ 
/*      */   public static ItemStack getBukkitItemStack(ItemStack bukkitItemStack)
/*      */   {
/* 1779 */     if (craftBukkitNMS != null) {
/* 1780 */       return getBukkitItemByMethod(bukkitItemStack);
/*      */     }
/* 1782 */     if (craftBukkitConstructor == null) {
/*      */       try {
/* 1784 */         craftBukkitConstructor = getCraftItemStackClass().getConstructor(new Class[] { ItemStack.class });
/*      */       }
/*      */       catch (Exception e) {
/* 1787 */         if (!craftItemStackFailed) {
/* 1788 */           return getBukkitItemByMethod(bukkitItemStack);
/*      */         }
/* 1790 */         throw new RuntimeException("Cannot find CraftItemStack(org.bukkit.inventory.ItemStack).", e);
/*      */       }
/*      */     }
/*      */ 
/*      */     try
/*      */     {
/* 1796 */       return (ItemStack)craftBukkitConstructor.newInstance(new Object[] { bukkitItemStack });
/*      */     } catch (Exception e) {
/* 1798 */       throw new RuntimeException("Cannot construct CraftItemStack.", e);
/*      */     }
/*      */   }
/*      */ 
/*      */   private static ItemStack getBukkitItemByMethod(ItemStack bukkitItemStack) {
/* 1803 */     if (craftBukkitNMS == null) {
/*      */       try {
/* 1805 */         craftBukkitNMS = getCraftItemStackClass().getMethod("asNMSCopy", new Class[] { ItemStack.class });
/* 1806 */         craftBukkitOBC = getCraftItemStackClass().getMethod("asCraftMirror", new Class[] { getItemStackClass() });
/*      */       } catch (Exception e) {
/* 1808 */         craftItemStackFailed = true;
/* 1809 */         throw new RuntimeException("Cannot find CraftItemStack.asCraftCopy(org.bukkit.inventory.ItemStack).", e);
/*      */       }
/*      */     }
/*      */ 
/*      */     try
/*      */     {
/* 1815 */       Object nmsItemStack = craftBukkitNMS.invoke(null, new Object[] { bukkitItemStack });
/* 1816 */       return (ItemStack)craftBukkitOBC.invoke(null, new Object[] { nmsItemStack });
/*      */     } catch (Exception e) {
/* 1818 */       throw new RuntimeException("Cannot construct CraftItemStack.", e);
/*      */     }
/*      */   }
/*      */ 
/*      */   public static ItemStack getBukkitItemStack(Object minecraftItemStack)
/*      */   {
/* 1829 */     if (craftNMSMethod != null) {
/* 1830 */       return getBukkitItemByMethod(minecraftItemStack);
/*      */     }
/* 1832 */     if (craftNMSConstructor == null) {
/*      */       try {
/* 1834 */         craftNMSConstructor = getCraftItemStackClass().getConstructor(new Class[] { minecraftItemStack.getClass() });
/*      */       }
/*      */       catch (Exception e) {
/* 1837 */         if (!craftItemStackFailed) {
/* 1838 */           return getBukkitItemByMethod(minecraftItemStack);
/*      */         }
/* 1840 */         throw new RuntimeException("Cannot find CraftItemStack(net.minecraft.server.ItemStack).", e);
/*      */       }
/*      */     }
/*      */ 
/*      */     try
/*      */     {
/* 1846 */       return (ItemStack)craftNMSConstructor.newInstance(new Object[] { minecraftItemStack });
/*      */     } catch (Exception e) {
/* 1848 */       throw new RuntimeException("Cannot construct CraftItemStack.", e);
/*      */     }
/*      */   }
/*      */ 
/*      */   private static ItemStack getBukkitItemByMethod(Object minecraftItemStack) {
/* 1853 */     if (craftNMSMethod == null) {
/*      */       try {
/* 1855 */         craftNMSMethod = getCraftItemStackClass().getMethod("asCraftMirror", new Class[] { minecraftItemStack.getClass() });
/*      */       } catch (Exception e) {
/* 1857 */         craftItemStackFailed = true;
/* 1858 */         throw new RuntimeException("Cannot find CraftItemStack.asCraftMirror(net.minecraft.server.ItemStack).", e);
/*      */       }
/*      */     }
/*      */ 
/*      */     try
/*      */     {
/* 1864 */       return (ItemStack)craftNMSMethod.invoke(null, new Object[] { minecraftItemStack });
/*      */     } catch (Exception e) {
/* 1866 */       throw new RuntimeException("Cannot construct CraftItemStack.", e);
/*      */     }
/*      */   }
/*      */ 
/*      */   public static Object getMinecraftItemStack(ItemStack stack)
/*      */   {
/* 1885 */     if (craftBukkitNMS == null) {
/*      */       try {
/* 1887 */         craftBukkitNMS = getCraftItemStackClass().getMethod("asNMSCopy", new Class[] { ItemStack.class });
/*      */       } catch (Throwable ex) {
/* 1889 */         throw new RuntimeException("Could not find CraftItemStack.asNMSCopy.", ex);
/*      */       }
/*      */     }
/*      */     try
/*      */     {
/* 1894 */       return craftBukkitNMS.invoke(null, new Object[] { stack });
/*      */     } catch (Throwable ex) {
/* 1896 */       throw new RuntimeException("Could not obtain NMS ItemStack.", ex);
/*      */     }
/*      */   }
/*      */ 
/*      */   public static Class<?> getPlayerInfoDataClass()
/*      */   {
/* 1905 */     return getMinecraftClass("PacketPlayOutPlayerInfo$PlayerInfoData", new String[] { "PlayerInfoData" });
/*      */   }
/*      */ 
/*      */   public static boolean isPlayerInfoData(Object obj)
/*      */   {
/* 1914 */     Class clazz = getPlayerInfoDataClass();
/* 1915 */     return (clazz != null) && (obj.getClass().equals(clazz));
/*      */   }
/*      */ 
/*      */   public static Class<?> getIBlockDataClass()
/*      */   {
/* 1923 */     return getMinecraftClass("IBlockData");
/*      */   }
/*      */ 
/*      */   public static boolean isIBlockData(Object obj)
/*      */   {
/* 1932 */     Class clazz = getIBlockDataClass();
/* 1933 */     return (clazz != null) && (obj.getClass().equals(clazz));
/*      */   }
/*      */ 
/*      */   private static Class<?> getClass(String className)
/*      */   {
/*      */     try
/*      */     {
/* 1943 */       return MinecraftReflection.class.getClassLoader().loadClass(className);
/*      */     } catch (ClassNotFoundException e) {
/* 1945 */       throw new RuntimeException("Cannot find class " + className, e);
/*      */     }
/*      */   }
/*      */ 
/*      */   public static Class<?> getCraftBukkitClass(String className)
/*      */   {
/* 1956 */     if (craftbukkitPackage == null)
/* 1957 */       craftbukkitPackage = new CachedPackage(getCraftBukkitPackage(), getClassSource());
/* 1958 */     return craftbukkitPackage.getPackageClass(className);
/*      */   }
/*      */ 
/*      */   public static Class<?> getMinecraftClass(String className)
/*      */   {
/* 1968 */     if (minecraftPackage == null)
/* 1969 */       minecraftPackage = new CachedPackage(getMinecraftPackage(), getClassSource());
/* 1970 */     return minecraftPackage.getPackageClass(className);
/*      */   }
/*      */ 
/*      */   private static Class<?> setMinecraftClass(String className, Class<?> clazz)
/*      */   {
/* 1980 */     if (minecraftPackage == null)
/* 1981 */       minecraftPackage = new CachedPackage(getMinecraftPackage(), getClassSource());
/* 1982 */     minecraftPackage.setPackageClass(className, clazz);
/* 1983 */     return clazz;
/*      */   }
/*      */ 
/*      */   private static ClassSource getClassSource()
/*      */   {
/* 1991 */     ErrorReporter reporter = ProtocolLibrary.getErrorReporter();
/*      */ 
/* 1994 */     if (classSource == null)
/*      */     {
/*      */       try {
/* 1997 */         return MinecraftReflection.classSource = new RemappedClassSource().initialize();
/*      */       } catch (RemappedClassSource.RemapperUnavaibleException e) {
/* 1999 */         if (e.getReason() != RemappedClassSource.RemapperUnavaibleException.Reason.MCPC_NOT_PRESENT)
/* 2000 */           reporter.reportWarning(MinecraftReflection.class, Report.newBuilder(REPORT_CANNOT_FIND_MCPC_REMAPPER));
/*      */       } catch (Exception e) {
/* 2002 */         reporter.reportWarning(MinecraftReflection.class, Report.newBuilder(REPORT_CANNOT_LOAD_CPC_REMAPPER));
/*      */       }
/*      */ 
/* 2006 */       classSource = ClassSource.fromClassLoader();
/*      */     }
/*      */ 
/* 2009 */     return classSource; } 
/*      */   public static Class<?> getMinecraftClass(String className, String[] aliases) { // Byte code:
/*      */     //   0: aload_0
/*      */     //   1: invokestatic 488	com/comphenix/protocol/utility/MinecraftReflection:getMinecraftClass	(Ljava/lang/String;)Ljava/lang/Class;
/*      */     //   4: areturn
/*      */     //   5: astore_2
/*      */     //   6: aconst_null
/*      */     //   7: astore_3
/*      */     //   8: aload_1
/*      */     //   9: astore 4
/*      */     //   11: aload 4
/*      */     //   13: arraylength
/*      */     //   14: istore 5
/*      */     //   16: iconst_0
/*      */     //   17: istore 6
/*      */     //   19: iload 6
/*      */     //   21: iload 5
/*      */     //   23: if_icmpge +27 -> 50
/*      */     //   26: aload 4
/*      */     //   28: iload 6
/*      */     //   30: aaload
/*      */     //   31: astore 7
/*      */     //   33: aload 7
/*      */     //   35: invokestatic 488	com/comphenix/protocol/utility/MinecraftReflection:getMinecraftClass	(Ljava/lang/String;)Ljava/lang/Class;
/*      */     //   38: astore_3
/*      */     //   39: goto +11 -> 50
/*      */     //   42: astore 8
/*      */     //   44: iinc 6 1
/*      */     //   47: goto -28 -> 19
/*      */     //   50: aload_3
/*      */     //   51: ifnull +13 -> 64
/*      */     //   54: getstatic 1264	com/comphenix/protocol/utility/MinecraftReflection:minecraftPackage	Lcom/comphenix/protocol/utility/CachedPackage;
/*      */     //   57: aload_0
/*      */     //   58: aload_3
/*      */     //   59: invokevirtual 1268	com/comphenix/protocol/utility/CachedPackage:setPackageClass	(Ljava/lang/String;Ljava/lang/Class;)V
/*      */     //   62: aload_3
/*      */     //   63: areturn
/*      */     //   64: new 299	java/lang/RuntimeException
/*      */     //   67: dup
/*      */     //   68: ldc_w 1312
/*      */     //   71: iconst_2
/*      */     //   72: anewarray 4	java/lang/Object
/*      */     //   75: dup
/*      */     //   76: iconst_0
/*      */     //   77: aload_0
/*      */     //   78: aastore
/*      */     //   79: dup
/*      */     //   80: iconst_1
/*      */     //   81: ldc_w 1314
/*      */     //   84: invokestatic 1320	com/google/common/base/Joiner:on	(Ljava/lang/String;)Lcom/google/common/base/Joiner;
/*      */     //   87: aload_1
/*      */     //   88: invokevirtual 1324	com/google/common/base/Joiner:join	([Ljava/lang/Object;)Ljava/lang/String;
/*      */     //   91: aastore
/*      */     //   92: invokestatic 1328	java/lang/String:format	(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
/*      */     //   95: invokespecial 1329	java/lang/RuntimeException:<init>	(Ljava/lang/String;)V
/*      */     //   98: athrow
/*      */     //
/*      */     // Exception table:
/*      */     //   from	to	target	type
/*      */     //   0	4	5	java/lang/RuntimeException
/*      */     //   33	39	42	java/lang/RuntimeException } 
/* 2052 */   public static String getNetworkManagerName() { return getNetworkManagerClass().getSimpleName(); }
/*      */ 
/*      */ 
/*      */   public static String getNetLoginHandlerName()
/*      */   {
/* 2060 */     return getNetLoginHandlerClass().getSimpleName();
/*      */   }
/*      */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.utility.MinecraftReflection
 * JD-Core Version:    0.6.2
 */