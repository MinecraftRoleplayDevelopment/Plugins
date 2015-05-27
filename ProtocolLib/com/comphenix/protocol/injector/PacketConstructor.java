/*     */ package com.comphenix.protocol.injector;
/*     */ 
/*     */ import com.comphenix.protocol.PacketType;
/*     */ import com.comphenix.protocol.error.RethrowErrorReporter;
/*     */ import com.comphenix.protocol.events.PacketContainer;
/*     */ import com.comphenix.protocol.injector.packet.PacketRegistry;
/*     */ import com.comphenix.protocol.reflect.FieldAccessException;
/*     */ import com.comphenix.protocol.wrappers.BukkitConverters;
/*     */ import com.google.common.collect.ImmutableList;
/*     */ import com.google.common.collect.Lists;
/*     */ import com.google.common.primitives.Primitives;
/*     */ import java.lang.reflect.Constructor;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.util.List;
/*     */ 
/*     */ public class PacketConstructor
/*     */ {
/*  45 */   public static PacketConstructor DEFAULT = new PacketConstructor(null);
/*     */   private Constructor<?> constructorMethod;
/*     */   private PacketType type;
/*     */   private List<Unwrapper> unwrappers;
/*     */   private Unwrapper[] paramUnwrapper;
/*     */ 
/*     */   private PacketConstructor(Constructor<?> constructorMethod)
/*     */   {
/*  60 */     this.constructorMethod = constructorMethod;
/*  61 */     this.unwrappers = Lists.newArrayList(new Unwrapper[] { new BukkitUnwrapper(new RethrowErrorReporter()) });
/*  62 */     this.unwrappers.addAll(BukkitConverters.getUnwrappers());
/*     */   }
/*     */ 
/*     */   private PacketConstructor(PacketType type, Constructor<?> constructorMethod, List<Unwrapper> unwrappers, Unwrapper[] paramUnwrapper) {
/*  66 */     this.type = type;
/*  67 */     this.constructorMethod = constructorMethod;
/*  68 */     this.unwrappers = unwrappers;
/*  69 */     this.paramUnwrapper = paramUnwrapper;
/*     */   }
/*     */ 
/*     */   public ImmutableList<Unwrapper> getUnwrappers() {
/*  73 */     return ImmutableList.copyOf(this.unwrappers);
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public int getPacketID()
/*     */   {
/*  84 */     return this.type.getLegacyId();
/*     */   }
/*     */ 
/*     */   public PacketType getType()
/*     */   {
/*  92 */     return this.type;
/*     */   }
/*     */ 
/*     */   public PacketConstructor withUnwrappers(List<Unwrapper> unwrappers)
/*     */   {
/* 101 */     return new PacketConstructor(this.type, this.constructorMethod, unwrappers, this.paramUnwrapper);
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public PacketConstructor withPacket(int id, Object[] values)
/*     */   {
/* 117 */     return withPacket(PacketType.findLegacy(id), values);
/*     */   }
/*     */ 
/*     */   public PacketConstructor withPacket(PacketType type, Object[] values)
/*     */   {
/* 130 */     Class[] types = new Class[values.length];
/* 131 */     Throwable lastException = null;
/* 132 */     Unwrapper[] paramUnwrapper = new Unwrapper[values.length];
/*     */ 
/* 134 */     for (int i = 0; i < types.length; i++)
/*     */     {
/* 136 */       if (values[i] != null) {
/* 137 */         types[i] = getClass(values[i]);
/*     */ 
/* 139 */         for (Unwrapper unwrapper : this.unwrappers) {
/* 140 */           Object result = null;
/*     */           try
/*     */           {
/* 143 */             result = unwrapper.unwrapItem(values[i]);
/*     */           } catch (OutOfMemoryError e) {
/* 145 */             throw e;
/*     */           } catch (ThreadDeath e) {
/* 147 */             throw e;
/*     */           } catch (Throwable e) {
/* 149 */             lastException = e;
/*     */           }
/*     */ 
/* 153 */           if (result != null) {
/* 154 */             types[i] = getClass(result);
/* 155 */             paramUnwrapper[i] = unwrapper;
/* 156 */             break;
/*     */           }
/*     */         }
/*     */       }
/*     */       else
/*     */       {
/* 162 */         types[i] = Object.class;
/*     */       }
/*     */     }
/* 165 */     Class packetType = PacketRegistry.getPacketClassFromType(type, true);
/*     */ 
/* 167 */     if (packetType == null) {
/* 168 */       throw new IllegalArgumentException("Could not find a packet by the type " + type);
/*     */     }
/*     */ 
/* 171 */     for (Constructor constructor : packetType.getConstructors()) {
/* 172 */       Class[] params = constructor.getParameterTypes();
/*     */ 
/* 174 */       if (isCompatible(types, params))
/*     */       {
/* 176 */         return new PacketConstructor(type, constructor, this.unwrappers, paramUnwrapper);
/*     */       }
/*     */     }
/* 179 */     throw new IllegalArgumentException("No suitable constructor could be found.", lastException);
/*     */   }
/*     */ 
/*     */   public PacketContainer createPacket(Object[] values)
/*     */     throws FieldAccessException
/*     */   {
/*     */     try
/*     */     {
/* 193 */       for (int i = 0; i < values.length; i++) {
/* 194 */         if (this.paramUnwrapper[i] != null) {
/* 195 */           values[i] = this.paramUnwrapper[i].unwrapItem(values[i]);
/*     */         }
/*     */       }
/*     */ 
/* 199 */       Object nmsPacket = this.constructorMethod.newInstance(values);
/* 200 */       return new PacketContainer(this.type, nmsPacket);
/*     */     }
/*     */     catch (IllegalArgumentException e) {
/* 203 */       throw e;
/*     */     } catch (InstantiationException e) {
/* 205 */       throw new FieldAccessException("Cannot construct an abstract packet.", e);
/*     */     } catch (IllegalAccessException e) {
/* 207 */       throw new FieldAccessException("Cannot construct packet due to a security limitation.", e);
/*     */     } catch (InvocationTargetException e) {
/* 209 */       throw new RuntimeException("Minecraft error.", e);
/*     */     }
/*     */   }
/*     */ 
/*     */   private static boolean isCompatible(Class<?>[] types, Class<?>[] params)
/*     */   {
/* 217 */     if (params.length == types.length) {
/* 218 */       for (int i = 0; i < params.length; i++) {
/* 219 */         Class inputType = types[i];
/* 220 */         Class paramType = params[i];
/*     */ 
/* 223 */         if ((!inputType.isPrimitive()) && (paramType.isPrimitive()))
/*     */         {
/* 225 */           paramType = Primitives.wrap(paramType);
/*     */         }
/*     */ 
/* 229 */         if (!paramType.isAssignableFrom(inputType)) {
/* 230 */           return false;
/*     */         }
/*     */       }
/*     */ 
/* 234 */       return true;
/*     */     }
/*     */ 
/* 238 */     return false;
/*     */   }
/*     */ 
/*     */   public static Class<?> getClass(Object obj)
/*     */   {
/* 247 */     if ((obj instanceof Class))
/* 248 */       return (Class)obj;
/* 249 */     return obj.getClass();
/*     */   }
/*     */ 
/*     */   public static abstract interface Unwrapper
/*     */   {
/*     */     public abstract Object unwrapItem(Object paramObject);
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.injector.PacketConstructor
 * JD-Core Version:    0.6.2
 */