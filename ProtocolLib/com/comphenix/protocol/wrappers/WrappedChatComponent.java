/*     */ package com.comphenix.protocol.wrappers;
/*     */ 
/*     */ import com.comphenix.protocol.reflect.FuzzyReflection;
/*     */ import com.comphenix.protocol.reflect.accessors.Accessors;
/*     */ import com.comphenix.protocol.reflect.accessors.ConstructorAccessor;
/*     */ import com.comphenix.protocol.reflect.accessors.MethodAccessor;
/*     */ import com.comphenix.protocol.utility.MinecraftReflection;
/*     */ import com.google.common.base.Preconditions;
/*     */ 
/*     */ public class WrappedChatComponent extends AbstractWrapper
/*     */ {
/*  17 */   private static final Class<?> SERIALIZER = MinecraftReflection.getChatSerializerClass();
/*  18 */   private static final Class<?> COMPONENT = MinecraftReflection.getIChatBaseComponentClass();
/*     */ 
/*  28 */   private static MethodAccessor SERIALIZE_COMPONENT = Accessors.getMethodAccessor(fuzzy.getMethodByParameters("serialize", String.class, new Class[] { COMPONENT }));
/*     */ 
/*  30 */   private static MethodAccessor DESERIALIZE_COMPONENT = Accessors.getMethodAccessor(fuzzy.getMethodByParameters("deserialize", COMPONENT, new Class[] { String.class }));
/*     */ 
/*  34 */   private static MethodAccessor CONSTRUCT_COMPONENT = Accessors.getMethodAccessor(MinecraftReflection.getCraftChatMessage(), "fromString", new Class[] { String.class });
/*     */ 
/*  37 */   private static ConstructorAccessor CONSTRUCT_TEXT_COMPONENT = Accessors.getConstructorAccessor(MinecraftReflection.getChatComponentTextClass(), new Class[] { String.class });
/*     */   private transient String cache;
/*     */ 
/*     */   private WrappedChatComponent(Object handle, String cache)
/*     */   {
/*  43 */     super(MinecraftReflection.getIChatBaseComponentClass());
/*  44 */     setHandle(handle);
/*  45 */     this.cache = cache;
/*     */   }
/*     */ 
/*     */   public static WrappedChatComponent fromHandle(Object handle)
/*     */   {
/*  54 */     return new WrappedChatComponent(handle, null);
/*     */   }
/*     */ 
/*     */   public static WrappedChatComponent fromJson(String json)
/*     */   {
/*  63 */     return new WrappedChatComponent(DESERIALIZE_COMPONENT.invoke(null, new Object[] { json }), json);
/*     */   }
/*     */ 
/*     */   public static WrappedChatComponent fromText(String text)
/*     */   {
/*  72 */     Preconditions.checkNotNull(text, "text cannot be NULL.");
/*  73 */     return fromHandle(CONSTRUCT_TEXT_COMPONENT.invoke(new Object[] { text }));
/*     */   }
/*     */ 
/*     */   public static WrappedChatComponent[] fromChatMessage(String message)
/*     */   {
/*  84 */     Object[] components = (Object[])CONSTRUCT_COMPONENT.invoke(null, new Object[] { message });
/*  85 */     WrappedChatComponent[] result = new WrappedChatComponent[components.length];
/*     */ 
/*  87 */     for (int i = 0; i < components.length; i++) {
/*  88 */       result[i] = fromHandle(components[i]);
/*     */     }
/*  90 */     return result;
/*     */   }
/*     */ 
/*     */   public String getJson()
/*     */   {
/* 100 */     if (this.cache == null) {
/* 101 */       this.cache = ((String)SERIALIZE_COMPONENT.invoke(null, new Object[] { this.handle }));
/*     */     }
/* 103 */     return this.cache;
/*     */   }
/*     */ 
/*     */   public void setJson(String obj)
/*     */   {
/* 111 */     this.handle = DESERIALIZE_COMPONENT.invoke(null, new Object[] { obj });
/* 112 */     this.cache = obj;
/*     */   }
/*     */ 
/*     */   public WrappedChatComponent deepClone()
/*     */   {
/* 120 */     return fromJson(getJson());
/*     */   }
/*     */ 
/*     */   public boolean equals(Object obj)
/*     */   {
/* 125 */     if (obj == this)
/* 126 */       return true;
/* 127 */     if ((obj instanceof WrappedChatComponent)) {
/* 128 */       return ((WrappedChatComponent)obj).handle.equals(this.handle);
/*     */     }
/* 130 */     return false;
/*     */   }
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 135 */     return this.handle.hashCode();
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  25 */     FuzzyReflection fuzzy = FuzzyReflection.fromClass(SERIALIZER);
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.wrappers.WrappedChatComponent
 * JD-Core Version:    0.6.2
 */