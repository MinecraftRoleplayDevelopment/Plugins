/*     */ package com.comphenix.protocol;
/*     */ 
/*     */ import com.comphenix.protocol.async.AsyncListenerHandler;
/*     */ import com.comphenix.protocol.error.ErrorReporter;
/*     */ import com.comphenix.protocol.error.Report;
/*     */ import com.comphenix.protocol.error.Report.ReportBuilder;
/*     */ import com.comphenix.protocol.error.ReportType;
/*     */ import com.comphenix.protocol.events.ListeningWhitelist;
/*     */ import com.comphenix.protocol.events.PacketContainer;
/*     */ import com.comphenix.protocol.injector.BukkitUnwrapper;
/*     */ import com.comphenix.protocol.injector.server.AbstractInputStreamLookup;
/*     */ import com.comphenix.protocol.injector.server.TemporaryPlayerFactory;
/*     */ import com.comphenix.protocol.injector.spigot.SpigotPacketInjector;
/*     */ import com.comphenix.protocol.reflect.FieldUtils;
/*     */ import com.comphenix.protocol.reflect.FuzzyReflection;
/*     */ import com.comphenix.protocol.reflect.MethodUtils;
/*     */ import com.comphenix.protocol.reflect.ObjectWriter;
/*     */ import com.comphenix.protocol.reflect.compiler.BackgroundCompiler;
/*     */ import com.comphenix.protocol.reflect.compiler.StructureCompiler;
/*     */ import com.comphenix.protocol.reflect.instances.CollectionGenerator;
/*     */ import com.comphenix.protocol.reflect.instances.DefaultInstances;
/*     */ import com.comphenix.protocol.reflect.instances.PrimitiveGenerator;
/*     */ import com.comphenix.protocol.utility.MinecraftReflection;
/*     */ import com.comphenix.protocol.wrappers.ChunkPosition;
/*     */ import com.comphenix.protocol.wrappers.WrappedDataWatcher;
/*     */ import com.comphenix.protocol.wrappers.WrappedWatchableObject;
/*     */ import com.comphenix.protocol.wrappers.nbt.io.NbtBinarySerializer;
/*     */ import java.lang.reflect.Field;
/*     */ import java.lang.reflect.Modifier;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ 
/*     */ class CleanupStaticMembers
/*     */ {
/*  57 */   public static final ReportType REPORT_CANNOT_RESET_FIELD = new ReportType("Unable to reset field %s: %s");
/*  58 */   public static final ReportType REPORT_CANNOT_UNLOAD_CLASS = new ReportType("Unable to unload class %s.");
/*     */   private ClassLoader loader;
/*     */   private ErrorReporter reporter;
/*     */ 
/*     */   public CleanupStaticMembers(ClassLoader loader, ErrorReporter reporter)
/*     */   {
/*  64 */     this.loader = loader;
/*  65 */     this.reporter = reporter;
/*     */   }
/*     */ 
/*     */   public void resetAll()
/*     */   {
/*  74 */     Class[] publicClasses = { AsyncListenerHandler.class, ListeningWhitelist.class, PacketContainer.class, BukkitUnwrapper.class, DefaultInstances.class, CollectionGenerator.class, PrimitiveGenerator.class, FuzzyReflection.class, MethodUtils.class, BackgroundCompiler.class, StructureCompiler.class, ObjectWriter.class, Packets.Server.class, Packets.Client.class, ChunkPosition.class, WrappedDataWatcher.class, WrappedWatchableObject.class, AbstractInputStreamLookup.class, TemporaryPlayerFactory.class, SpigotPacketInjector.class, MinecraftReflection.class, NbtBinarySerializer.class };
/*     */ 
/*  85 */     String[] internalClasses = { "com.comphenix.protocol.events.SerializedOfflinePlayer", "com.comphenix.protocol.injector.player.InjectedServerConnection", "com.comphenix.protocol.injector.player.NetworkFieldInjector", "com.comphenix.protocol.injector.player.NetworkObjectInjector", "com.comphenix.protocol.injector.player.NetworkServerInjector", "com.comphenix.protocol.injector.player.PlayerInjector", "com.comphenix.protocol.injector.EntityUtilities", "com.comphenix.protocol.injector.packet.PacketRegistry", "com.comphenix.protocol.injector.packet.PacketInjector", "com.comphenix.protocol.injector.packet.ReadPacketModifier", "com.comphenix.protocol.injector.StructureCache", "com.comphenix.protocol.reflect.compiler.BoxingHelper", "com.comphenix.protocol.reflect.compiler.MethodDescriptor", "com.comphenix.protocol.wrappers.nbt.WrappedElement" };
/*     */ 
/* 102 */     resetClasses(publicClasses);
/* 103 */     resetClasses(getClasses(this.loader, internalClasses));
/*     */   }
/*     */ 
/*     */   private void resetClasses(Class<?>[] classes)
/*     */   {
/* 108 */     for (Class clazz : classes)
/* 109 */       resetClass(clazz);
/*     */   }
/*     */ 
/*     */   private void resetClass(Class<?> clazz)
/*     */   {
/* 114 */     for (Field field : clazz.getFields()) {
/* 115 */       Class type = field.getType();
/*     */ 
/* 118 */       if ((Modifier.isStatic(field.getModifiers())) && (!type.isPrimitive()) && (!type.equals(String.class)) && (!type.equals(ReportType.class)))
/*     */       {
/*     */         try
/*     */         {
/* 123 */           setFinalStatic(field, null);
/*     */         }
/*     */         catch (IllegalAccessException e) {
/* 126 */           this.reporter.reportWarning(this, Report.newBuilder(REPORT_CANNOT_RESET_FIELD).error(e).messageParam(new Object[] { field.getName(), e.getMessage() }));
/*     */ 
/* 129 */           e.printStackTrace();
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private static void setFinalStatic(Field field, Object newValue) throws IllegalAccessException
/*     */   {
/* 137 */     int modifier = field.getModifiers();
/* 138 */     boolean isFinal = Modifier.isFinal(modifier);
/*     */ 
/* 140 */     Field modifiersField = isFinal ? FieldUtils.getField(Field.class, "modifiers", true) : null;
/*     */ 
/* 143 */     if (isFinal) {
/* 144 */       FieldUtils.writeField(modifiersField, field, Integer.valueOf(modifier & 0xFFFFFFEF), true);
/*     */     }
/*     */ 
/* 148 */     FieldUtils.writeStaticField(field, newValue, true);
/*     */ 
/* 151 */     if (isFinal)
/* 152 */       FieldUtils.writeField(modifiersField, field, Integer.valueOf(modifier), true);
/*     */   }
/*     */ 
/*     */   private Class<?>[] getClasses(ClassLoader loader, String[] names)
/*     */   {
/* 157 */     List output = new ArrayList();
/*     */ 
/* 159 */     for (String name : names) {
/*     */       try {
/* 161 */         output.add(loader.loadClass(name));
/*     */       }
/*     */       catch (ClassNotFoundException e) {
/* 164 */         this.reporter.reportWarning(this, Report.newBuilder(REPORT_CANNOT_UNLOAD_CLASS).error(e).messageParam(new Object[] { name }));
/*     */       }
/*     */     }
/*     */ 
/* 168 */     return (Class[])output.toArray(new Class[0]);
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.CleanupStaticMembers
 * JD-Core Version:    0.6.2
 */