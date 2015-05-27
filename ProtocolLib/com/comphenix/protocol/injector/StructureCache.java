/*     */ package com.comphenix.protocol.injector;
/*     */ 
/*     */ import com.comphenix.protocol.PacketType;
/*     */ import com.comphenix.protocol.injector.packet.PacketRegistry;
/*     */ import com.comphenix.protocol.reflect.StructureModifier;
/*     */ import com.comphenix.protocol.reflect.compiler.BackgroundCompiler;
/*     */ import com.comphenix.protocol.reflect.compiler.CompileListener;
/*     */ import com.comphenix.protocol.reflect.compiler.CompiledStructureModifier;
/*     */ import com.comphenix.protocol.reflect.instances.DefaultInstances;
/*     */ import com.comphenix.protocol.utility.MinecraftReflection;
/*     */ import java.util.HashSet;
/*     */ import java.util.Set;
/*     */ import java.util.concurrent.ConcurrentHashMap;
/*     */ import java.util.concurrent.ConcurrentMap;
/*     */ 
/*     */ public class StructureCache
/*     */ {
/*  40 */   private static ConcurrentMap<PacketType, StructureModifier<Object>> structureModifiers = new ConcurrentHashMap();
/*     */ 
/*  43 */   private static Set<PacketType> compiling = new HashSet();
/*     */ 
/*     */   @Deprecated
/*     */   public static Object newPacket(int legacyId)
/*     */   {
/*  54 */     return newPacket(PacketType.findLegacy(legacyId));
/*     */   }
/*     */ 
/*     */   public static Object newPacket(PacketType type)
/*     */   {
/*  63 */     Class clazz = PacketRegistry.getPacketClassFromType(type, true);
/*     */ 
/*  66 */     if (clazz != null)
/*     */     {
/*  68 */       Object result = DefaultInstances.DEFAULT.create(clazz);
/*     */ 
/*  70 */       if (result != null) {
/*  71 */         return result;
/*     */       }
/*     */     }
/*  74 */     throw new IllegalArgumentException("Cannot find associated packet class: " + type);
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public static StructureModifier<Object> getStructure(int legacyId)
/*     */   {
/*  86 */     return getStructure(PacketType.findLegacy(legacyId));
/*     */   }
/*     */ 
/*     */   public static StructureModifier<Object> getStructure(PacketType type)
/*     */   {
/*  96 */     return getStructure(type, true);
/*     */   }
/*     */ 
/*     */   public static StructureModifier<Object> getStructure(Class<?> packetType)
/*     */   {
/* 106 */     return getStructure(packetType, true);
/*     */   }
/*     */ 
/*     */   public static StructureModifier<Object> getStructure(Class<?> packetType, boolean compile)
/*     */   {
/* 117 */     return getStructure(PacketRegistry.getPacketType(packetType), compile);
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public static StructureModifier<Object> getStructure(int legacyId, boolean compile)
/*     */   {
/* 130 */     return getStructure(PacketType.findLegacy(legacyId), compile);
/*     */   }
/*     */ 
/*     */   public static StructureModifier<Object> getStructure(PacketType type, boolean compile)
/*     */   {
/* 140 */     StructureModifier result = (StructureModifier)structureModifiers.get(type);
/*     */ 
/* 143 */     if (result == null)
/*     */     {
/* 145 */       StructureModifier value = new StructureModifier(PacketRegistry.getPacketClassFromType(type, true), MinecraftReflection.getPacketClass(), true);
/*     */ 
/* 148 */       result = (StructureModifier)structureModifiers.putIfAbsent(type, value);
/*     */ 
/* 151 */       if (result == null) {
/* 152 */         result = value;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 157 */     if ((compile) && (!(result instanceof CompiledStructureModifier)))
/*     */     {
/* 159 */       synchronized (compiling) {
/* 160 */         BackgroundCompiler compiler = BackgroundCompiler.getInstance();
/*     */ 
/* 162 */         if ((!compiling.contains(type)) && (compiler != null)) {
/* 163 */           compiler.scheduleCompilation(result, new CompileListener()
/*     */           {
/*     */             public void onCompiled(StructureModifier<Object> compiledModifier) {
/* 166 */               StructureCache.structureModifiers.put(this.val$type, compiledModifier);
/*     */             }
/*     */           });
/* 169 */           compiling.add(type);
/*     */         }
/*     */       }
/*     */     }
/* 173 */     return result;
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.injector.StructureCache
 * JD-Core Version:    0.6.2
 */