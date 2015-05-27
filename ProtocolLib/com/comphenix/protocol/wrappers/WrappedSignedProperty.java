/*     */ package com.comphenix.protocol.wrappers;
/*     */ 
/*     */ import com.google.common.base.Objects;
/*     */ import com.google.common.base.Objects.ToStringHelper;
/*     */ import com.mojang.authlib.properties.Property;
/*     */ import java.security.PublicKey;
/*     */ 
/*     */ public class WrappedSignedProperty extends AbstractWrapper
/*     */ {
/*     */   public WrappedSignedProperty(String name, String value, String signature)
/*     */   {
/*  21 */     this(new Property(name, value, signature));
/*     */   }
/*     */ 
/*     */   private WrappedSignedProperty(Object handle)
/*     */   {
/*  29 */     super(Property.class);
/*  30 */     setHandle(handle);
/*     */   }
/*     */ 
/*     */   public static WrappedSignedProperty fromHandle(Object handle)
/*     */   {
/*  39 */     return new WrappedSignedProperty(handle);
/*     */   }
/*     */ 
/*     */   public static WrappedSignedProperty fromValues(String name, String value, String signature)
/*     */   {
/*  50 */     return new WrappedSignedProperty(name, value, signature);
/*     */   }
/*     */ 
/*     */   private Property getProfile()
/*     */   {
/*  58 */     return (Property)this.handle;
/*     */   }
/*     */ 
/*     */   public String getName()
/*     */   {
/*  66 */     return getProfile().getName();
/*     */   }
/*     */ 
/*     */   public String getSignature()
/*     */   {
/*  74 */     return getProfile().getSignature();
/*     */   }
/*     */ 
/*     */   public String getValue()
/*     */   {
/*  82 */     return getProfile().getValue();
/*     */   }
/*     */ 
/*     */   public boolean hasSignature()
/*     */   {
/*  90 */     return getProfile().hasSignature();
/*     */   }
/*     */ 
/*     */   public boolean isSignatureValid(PublicKey key)
/*     */   {
/*  99 */     return getProfile().isSignatureValid(key);
/*     */   }
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 104 */     return Objects.hashCode(new Object[] { getName(), getValue(), getSignature() });
/*     */   }
/*     */ 
/*     */   public boolean equals(Object object)
/*     */   {
/* 109 */     if ((object instanceof WrappedSignedProperty)) {
/* 110 */       if (!super.equals(object))
/* 111 */         return false;
/* 112 */       WrappedSignedProperty that = (WrappedSignedProperty)object;
/* 113 */       return (Objects.equal(getName(), that.getName())) && (Objects.equal(getValue(), that.getValue())) && (Objects.equal(getSignature(), that.getSignature()));
/*     */     }
/*     */ 
/* 117 */     return false;
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 122 */     return Objects.toStringHelper(this).add("name", getName()).add("value", getValue()).add("signature", getSignature()).toString();
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.wrappers.WrappedSignedProperty
 * JD-Core Version:    0.6.2
 */