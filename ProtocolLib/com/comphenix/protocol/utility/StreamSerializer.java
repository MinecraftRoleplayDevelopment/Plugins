/*     */ package com.comphenix.protocol.utility;
/*     */ 
/*     */ import com.comphenix.protocol.reflect.FuzzyReflection;
/*     */ import com.comphenix.protocol.reflect.accessors.Accessors;
/*     */ import com.comphenix.protocol.reflect.accessors.MethodAccessor;
/*     */ import com.comphenix.protocol.reflect.fuzzy.FuzzyMethodContract;
/*     */ import com.comphenix.protocol.reflect.fuzzy.FuzzyMethodContract.Builder;
/*     */ import com.comphenix.protocol.wrappers.nbt.NbtCompound;
/*     */ import com.comphenix.protocol.wrappers.nbt.NbtFactory;
/*     */ import com.comphenix.protocol.wrappers.nbt.NbtWrapper;
/*     */ import com.google.common.base.Preconditions;
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.DataInput;
/*     */ import java.io.DataInputStream;
/*     */ import java.io.DataOutput;
/*     */ import java.io.DataOutputStream;
/*     */ import java.io.IOException;
/*     */ import javax.annotation.Nonnull;
/*     */ import org.bukkit.inventory.ItemStack;
/*     */ import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;
/*     */ 
/*     */ public class StreamSerializer
/*     */ {
/*  30 */   private static final StreamSerializer DEFAULT = new StreamSerializer();
/*     */   private static MethodAccessor READ_ITEM_METHOD;
/*     */   private static MethodAccessor WRITE_ITEM_METHOD;
/*     */   private static MethodAccessor READ_NBT_METHOD;
/*     */   private static MethodAccessor WRITE_NBT_METHOD;
/*     */   private static MethodAccessor READ_STRING_METHOD;
/*     */   private static MethodAccessor WRITE_STRING_METHOD;
/*     */ 
/*     */   public static StreamSerializer getDefault()
/*     */   {
/*  47 */     return DEFAULT;
/*     */   }
/*     */ 
/*     */   public int deserializeVarInt(@Nonnull DataInputStream source)
/*     */     throws IOException
/*     */   {
/*  57 */     Preconditions.checkNotNull(source, "source cannot be NULL");
/*     */ 
/*  59 */     int result = 0;
/*  60 */     int length = 0;
/*     */     byte currentByte;
/*     */     do
/*     */     {
/*  63 */       currentByte = source.readByte();
/*  64 */       result |= (currentByte & 0x7F) << length++ * 7;
/*  65 */       if (length > 5)
/*  66 */         throw new RuntimeException("VarInt too big"); 
/*     */     }
/*  67 */     while ((currentByte & 0x80) == 128);
/*     */ 
/*  69 */     return result;
/*     */   }
/*     */ 
/*     */   public void serializeVarInt(@Nonnull DataOutputStream destination, int value)
/*     */     throws IOException
/*     */   {
/*  79 */     Preconditions.checkNotNull(destination, "source cannot be NULL");
/*     */ 
/*  81 */     while ((value & 0xFFFFFF80) != 0) {
/*  82 */       destination.writeByte(value & 0x7F | 0x80);
/*  83 */       value >>>= 7;
/*     */     }
/*  85 */     destination.writeByte(value);
/*     */   }
/*     */ 
/*     */   public ItemStack deserializeItemStack(@Nonnull DataInputStream input)
/*     */     throws IOException
/*     */   {
/*  99 */     if (input == null)
/* 100 */       throw new IllegalArgumentException("Input stream cannot be NULL.");
/* 101 */     Object nmsItem = null;
/*     */ 
/* 103 */     if (MinecraftReflection.isUsingNetty()) {
/* 104 */       if (READ_ITEM_METHOD == null) {
/* 105 */         READ_ITEM_METHOD = Accessors.getMethodAccessor(FuzzyReflection.fromClass(MinecraftReflection.getPacketDataSerializerClass(), true).getMethodByParameters("readItemStack", MinecraftReflection.getItemStackClass(), new Class[0]));
/*     */       }
/*     */ 
/* 112 */       nmsItem = READ_ITEM_METHOD.invoke(ByteBufAdapter.packetReader(input), new Object[0]);
/*     */     } else {
/* 114 */       if (READ_ITEM_METHOD == null) {
/* 115 */         READ_ITEM_METHOD = Accessors.getMethodAccessor(FuzzyReflection.fromClass(MinecraftReflection.getPacketClass()).getMethod(FuzzyMethodContract.newBuilder().parameterCount(1).parameterDerivedOf(DataInput.class).returnDerivedOf(MinecraftReflection.getItemStackClass()).build()));
/*     */       }
/*     */ 
/* 125 */       nmsItem = READ_ITEM_METHOD.invoke(null, new Object[] { input });
/*     */     }
/*     */ 
/* 129 */     if (nmsItem != null) {
/* 130 */       return MinecraftReflection.getBukkitItemStack(nmsItem);
/*     */     }
/* 132 */     return null;
/*     */   }
/*     */ 
/*     */   public NbtCompound deserializeCompound(@Nonnull DataInputStream input)
/*     */     throws IOException
/*     */   {
/* 142 */     if (input == null)
/* 143 */       throw new IllegalArgumentException("Input stream cannot be NULL.");
/* 144 */     Object nmsCompound = null;
/*     */ 
/* 147 */     if (MinecraftReflection.isUsingNetty()) {
/* 148 */       if (READ_NBT_METHOD == null) {
/* 149 */         READ_NBT_METHOD = Accessors.getMethodAccessor(FuzzyReflection.fromClass(MinecraftReflection.getPacketDataSerializerClass(), true).getMethodByParameters("readNbtCompound", MinecraftReflection.getNBTCompoundClass(), new Class[0]));
/*     */       }
/*     */ 
/* 156 */       nmsCompound = READ_NBT_METHOD.invoke(ByteBufAdapter.packetReader(input), new Object[0]);
/*     */     } else {
/* 158 */       if (READ_NBT_METHOD == null) {
/* 159 */         READ_NBT_METHOD = Accessors.getMethodAccessor(FuzzyReflection.fromClass(MinecraftReflection.getPacketClass()).getMethod(FuzzyMethodContract.newBuilder().parameterCount(1).parameterDerivedOf(DataInput.class).returnDerivedOf(MinecraftReflection.getNBTBaseClass()).build()));
/*     */       }
/*     */ 
/*     */       try
/*     */       {
/* 170 */         nmsCompound = READ_NBT_METHOD.invoke(null, new Object[] { input });
/*     */       } catch (Exception e) {
/* 172 */         throw new IOException("Cannot read item stack.", e);
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 177 */     if (nmsCompound != null) {
/* 178 */       return NbtFactory.fromNMSCompound(nmsCompound);
/*     */     }
/* 180 */     return null;
/*     */   }
/*     */ 
/*     */   public String deserializeString(@Nonnull DataInputStream input, int maximumLength)
/*     */     throws IOException
/*     */   {
/* 193 */     if (input == null)
/* 194 */       throw new IllegalArgumentException("Input stream cannot be NULL.");
/* 195 */     if (maximumLength > 32767)
/* 196 */       throw new IllegalArgumentException("Maximum length cannot exceed 32767 characters.");
/* 197 */     if (maximumLength < 0) {
/* 198 */       throw new IllegalArgumentException("Maximum length cannot be negative.");
/*     */     }
/* 200 */     if (MinecraftReflection.isUsingNetty()) {
/* 201 */       if (READ_STRING_METHOD == null) {
/* 202 */         READ_STRING_METHOD = Accessors.getMethodAccessor(FuzzyReflection.fromClass(MinecraftReflection.getPacketDataSerializerClass(), true).getMethodByParameters("readString", String.class, new Class[] { Integer.TYPE }));
/*     */       }
/*     */ 
/* 209 */       return (String)READ_STRING_METHOD.invoke(ByteBufAdapter.packetReader(input), new Object[] { Integer.valueOf(maximumLength) });
/*     */     }
/* 211 */     if (READ_STRING_METHOD == null) {
/* 212 */       READ_STRING_METHOD = Accessors.getMethodAccessor(FuzzyReflection.fromClass(MinecraftReflection.getPacketClass()).getMethod(FuzzyMethodContract.newBuilder().parameterCount(2).parameterDerivedOf(DataInput.class, 0).parameterExactType(Integer.TYPE, 1).returnTypeExact(String.class).build()));
/*     */     }
/*     */ 
/* 223 */     return (String)READ_STRING_METHOD.invoke(null, new Object[] { input, Integer.valueOf(maximumLength) });
/*     */   }
/*     */ 
/*     */   public ItemStack deserializeItemStack(@Nonnull String input)
/*     */     throws IOException
/*     */   {
/* 234 */     if (input == null)
/* 235 */       throw new IllegalArgumentException("Input text cannot be NULL.");
/* 236 */     ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(input));
/*     */ 
/* 238 */     return deserializeItemStack(new DataInputStream(inputStream));
/*     */   }
/*     */ 
/*     */   public void serializeItemStack(@Nonnull DataOutputStream output, ItemStack stack)
/*     */     throws IOException
/*     */   {
/* 254 */     if (output == null) {
/* 255 */       throw new IllegalArgumentException("Output stream cannot be NULL.");
/*     */     }
/*     */ 
/* 258 */     Object nmsItem = MinecraftReflection.getMinecraftItemStack(stack);
/*     */ 
/* 260 */     if (MinecraftReflection.isUsingNetty()) {
/* 261 */       if (WRITE_ITEM_METHOD == null) {
/* 262 */         WRITE_ITEM_METHOD = Accessors.getMethodAccessor(FuzzyReflection.fromClass(MinecraftReflection.getPacketDataSerializerClass(), true).getMethodByParameters("writeStack", new Class[] { MinecraftReflection.getItemStackClass() }));
/*     */       }
/*     */ 
/* 269 */       WRITE_ITEM_METHOD.invoke(ByteBufAdapter.packetWriter(output), new Object[] { nmsItem });
/*     */     } else {
/* 271 */       if (WRITE_ITEM_METHOD == null) {
/* 272 */         WRITE_ITEM_METHOD = Accessors.getMethodAccessor(FuzzyReflection.fromClass(MinecraftReflection.getPacketClass()).getMethod(FuzzyMethodContract.newBuilder().parameterCount(2).parameterDerivedOf(MinecraftReflection.getItemStackClass(), 0).parameterDerivedOf(DataOutput.class, 1).build()));
/*     */       }
/*     */ 
/* 281 */       WRITE_ITEM_METHOD.invoke(null, new Object[] { nmsItem, output });
/*     */     }
/*     */   }
/*     */ 
/*     */   public void serializeCompound(@Nonnull DataOutputStream output, NbtCompound compound)
/*     */     throws IOException
/*     */   {
/* 295 */     if (output == null) {
/* 296 */       throw new IllegalArgumentException("Output stream cannot be NULL.");
/*     */     }
/*     */ 
/* 299 */     Object handle = compound != null ? NbtFactory.fromBase(compound).getHandle() : null;
/*     */ 
/* 301 */     if (MinecraftReflection.isUsingNetty()) {
/* 302 */       if (WRITE_NBT_METHOD == null) {
/* 303 */         WRITE_NBT_METHOD = Accessors.getMethodAccessor(FuzzyReflection.fromClass(MinecraftReflection.getPacketDataSerializerClass(), true).getMethodByParameters("writeNbtCompound", new Class[] { MinecraftReflection.getNBTCompoundClass() }));
/*     */       }
/*     */ 
/* 310 */       WRITE_NBT_METHOD.invoke(ByteBufAdapter.packetWriter(output), new Object[] { handle });
/*     */     } else {
/* 312 */       if (WRITE_NBT_METHOD == null) {
/* 313 */         WRITE_NBT_METHOD = Accessors.getMethodAccessor(FuzzyReflection.fromClass(MinecraftReflection.getPacketClass(), true).getMethod(FuzzyMethodContract.newBuilder().parameterCount(2).parameterDerivedOf(MinecraftReflection.getNBTBaseClass(), 0).parameterDerivedOf(DataOutput.class, 1).returnTypeVoid().build()));
/*     */       }
/*     */ 
/* 324 */       WRITE_NBT_METHOD.invoke(null, new Object[] { handle, output });
/*     */     }
/*     */   }
/*     */ 
/*     */   public void serializeString(@Nonnull DataOutputStream output, String text)
/*     */     throws IOException
/*     */   {
/* 337 */     if (output == null)
/* 338 */       throw new IllegalArgumentException("output stream cannot be NULL.");
/* 339 */     if (text == null) {
/* 340 */       throw new IllegalArgumentException("text cannot be NULL.");
/*     */     }
/* 342 */     if (MinecraftReflection.isUsingNetty()) {
/* 343 */       if (WRITE_STRING_METHOD == null) {
/* 344 */         WRITE_STRING_METHOD = Accessors.getMethodAccessor(FuzzyReflection.fromClass(MinecraftReflection.getPacketDataSerializerClass(), true).getMethodByParameters("writeString", new Class[] { String.class }));
/*     */       }
/*     */ 
/* 351 */       WRITE_STRING_METHOD.invoke(ByteBufAdapter.packetWriter(output), new Object[] { text });
/*     */     } else {
/* 353 */       if (WRITE_STRING_METHOD == null) {
/* 354 */         WRITE_STRING_METHOD = Accessors.getMethodAccessor(FuzzyReflection.fromClass(MinecraftReflection.getPacketClass()).getMethod(FuzzyMethodContract.newBuilder().parameterCount(2).parameterExactType(String.class, 0).parameterDerivedOf(DataOutput.class, 1).returnTypeVoid().build()));
/*     */       }
/*     */ 
/* 365 */       WRITE_STRING_METHOD.invoke(null, new Object[] { text, output });
/*     */     }
/*     */   }
/*     */ 
/*     */   public String serializeItemStack(ItemStack stack)
/*     */     throws IOException
/*     */   {
/* 379 */     ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
/* 380 */     DataOutputStream dataOutput = new DataOutputStream(outputStream);
/*     */ 
/* 382 */     serializeItemStack(dataOutput, stack);
/*     */ 
/* 385 */     return Base64Coder.encodeLines(outputStream.toByteArray());
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.utility.StreamSerializer
 * JD-Core Version:    0.6.2
 */