/*     */ package com.comphenix.protocol.wrappers.nbt.io;
/*     */ 
/*     */ import com.comphenix.protocol.reflect.FieldAccessException;
/*     */ import com.comphenix.protocol.reflect.FuzzyReflection;
/*     */ import com.comphenix.protocol.reflect.accessors.Accessors;
/*     */ import com.comphenix.protocol.reflect.accessors.MethodAccessor;
/*     */ import com.comphenix.protocol.utility.MinecraftReflection;
/*     */ import com.comphenix.protocol.wrappers.nbt.NbtBase;
/*     */ import com.comphenix.protocol.wrappers.nbt.NbtCompound;
/*     */ import com.comphenix.protocol.wrappers.nbt.NbtFactory;
/*     */ import com.comphenix.protocol.wrappers.nbt.NbtList;
/*     */ import com.comphenix.protocol.wrappers.nbt.NbtWrapper;
/*     */ import java.io.DataInput;
/*     */ import java.io.DataOutput;
/*     */ import java.lang.reflect.Method;
/*     */ 
/*     */ public class NbtBinarySerializer
/*     */ {
/*  19 */   private static final Class<?> NBT_BASE_CLASS = MinecraftReflection.getNBTBaseClass();
/*     */   private static Method methodWrite;
/*     */   private static LoadMethod loadMethod;
/*  79 */   public static final NbtBinarySerializer DEFAULT = new NbtBinarySerializer();
/*     */ 
/*     */   public <TType> void serialize(NbtBase<TType> value, DataOutput destination)
/*     */   {
/*  87 */     if (methodWrite == null) {
/*  88 */       Class base = MinecraftReflection.getNBTBaseClass();
/*     */ 
/*  91 */       methodWrite = getUtilityClass().getMethodByParameters("writeNBT", new Class[] { base, DataOutput.class });
/*     */ 
/*  93 */       methodWrite.setAccessible(true);
/*     */     }
/*     */     try
/*     */     {
/*  97 */       methodWrite.invoke(null, new Object[] { NbtFactory.fromBase(value).getHandle(), destination });
/*     */     } catch (Exception e) {
/*  99 */       throw new FieldAccessException("Unable to write NBT " + value, e);
/*     */     }
/*     */   }
/*     */ 
/*     */   public <TType> NbtWrapper<TType> deserialize(DataInput source)
/*     */   {
/* 109 */     LoadMethod method = loadMethod;
/*     */ 
/* 111 */     if (loadMethod == null) {
/* 112 */       if (MinecraftReflection.isUsingNetty())
/*     */         try {
/* 114 */           method = new LoadMethodWorldUpdate(null);
/*     */         }
/*     */         catch (IllegalArgumentException e) {
/* 117 */           method = new LoadMethodSkinUpdate(null);
/*     */         }
/*     */       else {
/* 120 */         method = new LoadMethodNbtClass(null);
/*     */       }
/*     */ 
/* 124 */       loadMethod = method;
/*     */     }
/*     */     try
/*     */     {
/* 128 */       return NbtFactory.fromNMS(method.loadNbt(source), null);
/*     */     } catch (Exception e) {
/* 130 */       throw new FieldAccessException("Unable to read NBT from " + source, e);
/*     */     }
/*     */   }
/*     */ 
/*     */   private static MethodAccessor getNbtLoadMethod(Class<?>[] parameters) {
/* 135 */     return Accessors.getMethodAccessor(getUtilityClass().getMethodByParameters("load", NBT_BASE_CLASS, parameters), true);
/*     */   }
/*     */ 
/*     */   private static FuzzyReflection getUtilityClass() {
/* 139 */     if (MinecraftReflection.isUsingNetty()) {
/* 140 */       return FuzzyReflection.fromClass(MinecraftReflection.getNbtCompressedStreamToolsClass(), true);
/*     */     }
/* 142 */     return FuzzyReflection.fromClass(MinecraftReflection.getNBTBaseClass(), true);
/*     */   }
/*     */ 
/*     */   public NbtCompound deserializeCompound(DataInput source)
/*     */   {
/* 154 */     return (NbtCompound)deserialize(source);
/*     */   }
/*     */ 
/*     */   public <T> NbtList<T> deserializeList(DataInput source)
/*     */   {
/* 164 */     return (NbtList)deserialize(source);
/*     */   }
/*     */ 
/*     */   private static class LoadMethodSkinUpdate
/*     */     implements NbtBinarySerializer.LoadMethod
/*     */   {
/*  58 */     private Class<?> readLimitClass = MinecraftReflection.getNBTReadLimiterClass();
/*  59 */     private Object readLimiter = FuzzyReflection.fromClass(this.readLimitClass).getSingleton();
/*  60 */     private MethodAccessor accessor = NbtBinarySerializer.getNbtLoadMethod(new Class[] { DataInput.class, Integer.TYPE, this.readLimitClass });
/*     */ 
/*     */     public Object loadNbt(DataInput input)
/*     */     {
/*  64 */       return this.accessor.invoke(null, new Object[] { input, Integer.valueOf(0), this.readLimiter });
/*     */     }
/*     */   }
/*     */ 
/*     */   private static class LoadMethodWorldUpdate
/*     */     implements NbtBinarySerializer.LoadMethod
/*     */   {
/*  46 */     private MethodAccessor accessor = NbtBinarySerializer.getNbtLoadMethod(new Class[] { DataInput.class, Integer.TYPE });
/*     */ 
/*     */     public Object loadNbt(DataInput input)
/*     */     {
/*  50 */       return this.accessor.invoke(null, new Object[] { input, Integer.valueOf(0) });
/*     */     }
/*     */   }
/*     */ 
/*     */   private static class LoadMethodNbtClass
/*     */     implements NbtBinarySerializer.LoadMethod
/*     */   {
/*  34 */     private MethodAccessor accessor = NbtBinarySerializer.getNbtLoadMethod(new Class[] { DataInput.class });
/*     */ 
/*     */     public Object loadNbt(DataInput input)
/*     */     {
/*  38 */       return this.accessor.invoke(null, new Object[] { input });
/*     */     }
/*     */   }
/*     */ 
/*     */   private static abstract interface LoadMethod
/*     */   {
/*     */     public abstract Object loadNbt(DataInput paramDataInput);
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.wrappers.nbt.io.NbtBinarySerializer
 * JD-Core Version:    0.6.2
 */