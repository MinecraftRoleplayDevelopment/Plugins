/*     */ package com.comphenix.protocol.reflect.cloning;
/*     */ 
/*     */ import com.comphenix.protocol.reflect.ObjectWriter;
/*     */ import com.comphenix.protocol.reflect.StructureModifier;
/*     */ import com.comphenix.protocol.reflect.instances.InstanceProvider;
/*     */ import com.comphenix.protocol.reflect.instances.NotConstructableException;
/*     */ 
/*     */ public class FieldCloner
/*     */   implements Cloner
/*     */ {
/*     */   protected Cloner defaultCloner;
/*     */   protected InstanceProvider instanceProvider;
/*     */   protected ObjectWriter writer;
/*     */ 
/*     */   public FieldCloner(Cloner defaultCloner, InstanceProvider instanceProvider)
/*     */   {
/*  43 */     this.defaultCloner = defaultCloner;
/*  44 */     this.instanceProvider = instanceProvider;
/*     */ 
/*  47 */     this.writer = new ObjectWriter()
/*     */     {
/*     */       protected void transformField(StructureModifier<Object> modifierSource, StructureModifier<Object> modifierDest, int fieldIndex)
/*     */       {
/*  51 */         FieldCloner.this.defaultTransform(modifierDest, modifierDest, FieldCloner.this.getDefaultCloner(), fieldIndex);
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   protected void defaultTransform(StructureModifier<Object> modifierSource, StructureModifier<Object> modifierDest, Cloner defaultCloner, int fieldIndex)
/*     */   {
/*  66 */     Object value = modifierSource.read(fieldIndex);
/*  67 */     modifierDest.write(fieldIndex, defaultCloner.clone(value));
/*     */   }
/*     */ 
/*     */   public boolean canClone(Object source)
/*     */   {
/*  72 */     if (source == null) {
/*  73 */       return false;
/*     */     }
/*     */     try
/*     */     {
/*  77 */       return this.instanceProvider.create(source.getClass()) != null; } catch (NotConstructableException e) {
/*     */     }
/*  79 */     return false;
/*     */   }
/*     */ 
/*     */   public Object clone(Object source)
/*     */   {
/*  85 */     if (source == null) {
/*  86 */       throw new IllegalArgumentException("source cannot be NULL.");
/*     */     }
/*  88 */     Object copy = this.instanceProvider.create(source.getClass());
/*     */ 
/*  91 */     this.writer.copyTo(source, copy, source.getClass());
/*  92 */     return copy;
/*     */   }
/*     */ 
/*     */   public Cloner getDefaultCloner()
/*     */   {
/* 100 */     return this.defaultCloner;
/*     */   }
/*     */ 
/*     */   public InstanceProvider getInstanceProvider()
/*     */   {
/* 108 */     return this.instanceProvider;
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.reflect.cloning.FieldCloner
 * JD-Core Version:    0.6.2
 */