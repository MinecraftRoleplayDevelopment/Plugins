/*    */ package com.comphenix.protocol.wrappers.nbt.io;
/*    */ 
/*    */ import com.comphenix.protocol.wrappers.nbt.NbtBase;
/*    */ import com.comphenix.protocol.wrappers.nbt.NbtCompound;
/*    */ import com.comphenix.protocol.wrappers.nbt.NbtList;
/*    */ import com.comphenix.protocol.wrappers.nbt.NbtWrapper;
/*    */ import java.io.ByteArrayInputStream;
/*    */ import java.io.ByteArrayOutputStream;
/*    */ import java.io.DataInputStream;
/*    */ import java.io.DataOutputStream;
/*    */ import java.io.IOException;
/*    */ import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;
/*    */ 
/*    */ public class NbtTextSerializer
/*    */ {
/* 25 */   public static final NbtTextSerializer DEFAULT = new NbtTextSerializer();
/*    */   private NbtBinarySerializer binarySerializer;
/*    */ 
/*    */   public NbtTextSerializer()
/*    */   {
/* 30 */     this(new NbtBinarySerializer());
/*    */   }
/*    */ 
/*    */   public NbtTextSerializer(NbtBinarySerializer binary)
/*    */   {
/* 38 */     this.binarySerializer = binary;
/*    */   }
/*    */ 
/*    */   public NbtBinarySerializer getBinarySerializer()
/*    */   {
/* 46 */     return this.binarySerializer;
/*    */   }
/*    */ 
/*    */   public <TType> String serialize(NbtBase<TType> value)
/*    */   {
/* 55 */     ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
/* 56 */     DataOutputStream dataOutput = new DataOutputStream(outputStream);
/*    */ 
/* 58 */     this.binarySerializer.serialize(value, dataOutput);
/*    */ 
/* 61 */     return Base64Coder.encodeLines(outputStream.toByteArray());
/*    */   }
/*    */ 
/*    */   public <TType> NbtWrapper<TType> deserialize(String input)
/*    */     throws IOException
/*    */   {
/* 71 */     ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(input));
/*    */ 
/* 73 */     return this.binarySerializer.deserialize(new DataInputStream(inputStream));
/*    */   }
/*    */ 
/*    */   public NbtCompound deserializeCompound(String input)
/*    */     throws IOException
/*    */   {
/* 85 */     return (NbtCompound)deserialize(input);
/*    */   }
/*    */ 
/*    */   public <T> NbtList<T> deserializeList(String input)
/*    */     throws IOException
/*    */   {
/* 96 */     return (NbtList)deserialize(input);
/*    */   }
/*    */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.wrappers.nbt.io.NbtTextSerializer
 * JD-Core Version:    0.6.2
 */