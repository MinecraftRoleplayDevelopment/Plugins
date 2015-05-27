/*     */ package com.comphenix.protocol.reflect.compiler;
/*     */ 
/*     */ import com.comphenix.protocol.reflect.EquivalentConverter;
/*     */ import com.comphenix.protocol.reflect.FieldAccessException;
/*     */ import com.comphenix.protocol.reflect.StructureModifier;
/*     */ import com.comphenix.protocol.reflect.instances.DefaultInstances;
/*     */ import com.google.common.collect.Sets;
/*     */ import java.lang.reflect.Field;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.Set;
/*     */ 
/*     */ public abstract class CompiledStructureModifier extends StructureModifier<Object>
/*     */ {
/*     */   protected StructureCompiler compiler;
/*     */   private Set<Integer> exempted;
/*     */ 
/*     */   public CompiledStructureModifier()
/*     */   {
/*  43 */     this.customConvertHandling = true;
/*     */   }
/*     */ 
/*     */   public void setReadOnly(int fieldIndex, boolean value)
/*     */     throws FieldAccessException
/*     */   {
/*  49 */     if ((isReadOnly(fieldIndex)) && (!value)) {
/*  50 */       if (this.exempted == null)
/*  51 */         this.exempted = Sets.newHashSet();
/*  52 */       this.exempted.add(Integer.valueOf(fieldIndex));
/*     */     }
/*     */ 
/*  56 */     if ((!isReadOnly(fieldIndex)) && (value) && (
/*  57 */       (this.exempted == null) || (!this.exempted.contains(Integer.valueOf(fieldIndex))))) {
/*  58 */       throw new IllegalStateException("Cannot make compiled field " + fieldIndex + " read only.");
/*     */     }
/*     */ 
/*  62 */     super.setReadOnly(fieldIndex, value);
/*     */   }
/*     */ 
/*     */   public StructureModifier<Object> writeDefaults()
/*     */     throws FieldAccessException
/*     */   {
/*  69 */     DefaultInstances generator = DefaultInstances.DEFAULT;
/*     */ 
/*  72 */     for (Map.Entry entry : this.defaultFields.entrySet()) {
/*  73 */       Integer index = (Integer)entry.getValue();
/*  74 */       Field field = (Field)entry.getKey();
/*     */ 
/*  76 */       write(index.intValue(), generator.getDefault(field.getType()));
/*     */     }
/*     */ 
/*  79 */     return this;
/*     */   }
/*     */ 
/*     */   public final Object read(int fieldIndex) throws FieldAccessException
/*     */   {
/*  84 */     Object result = readGenerated(fieldIndex);
/*     */ 
/*  86 */     if (this.converter != null) {
/*  87 */       return this.converter.getSpecific(result);
/*     */     }
/*  89 */     return result;
/*     */   }
/*     */ 
/*     */   protected Object readReflected(int index)
/*     */     throws FieldAccessException
/*     */   {
/*  99 */     return super.read(index);
/*     */   }
/*     */ 
/*     */   protected abstract Object readGenerated(int paramInt) throws FieldAccessException;
/*     */ 
/*     */   public StructureModifier<Object> write(int index, Object value) throws FieldAccessException
/*     */   {
/* 106 */     if (this.converter != null)
/* 107 */       value = this.converter.getGeneric(getFieldType(index), value);
/* 108 */     return writeGenerated(index, value);
/*     */   }
/*     */ 
/*     */   protected void writeReflected(int index, Object value)
/*     */     throws FieldAccessException
/*     */   {
/* 118 */     super.write(index, value);
/*     */   }
/*     */ 
/*     */   protected abstract StructureModifier<Object> writeGenerated(int paramInt, Object paramObject) throws FieldAccessException;
/*     */ 
/*     */   public StructureModifier<Object> withTarget(Object target)
/*     */   {
/* 125 */     if (this.compiler != null) {
/* 126 */       return this.compiler.compile(super.withTarget(target));
/*     */     }
/* 128 */     return super.withTarget(target);
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.reflect.compiler.CompiledStructureModifier
 * JD-Core Version:    0.6.2
 */