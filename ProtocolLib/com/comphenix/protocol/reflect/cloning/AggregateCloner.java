/*     */ package com.comphenix.protocol.reflect.cloning;
/*     */ 
/*     */ import com.comphenix.protocol.reflect.instances.DefaultInstances;
/*     */ import com.comphenix.protocol.reflect.instances.ExistingGenerator;
/*     */ import com.comphenix.protocol.reflect.instances.InstanceProvider;
/*     */ import com.google.common.base.Function;
/*     */ import com.google.common.collect.Lists;
/*     */ import java.lang.ref.WeakReference;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */ import javax.annotation.Nullable;
/*     */ 
/*     */ public class AggregateCloner
/*     */   implements Cloner
/*     */ {
/* 173 */   public static final AggregateCloner DEFAULT = newBuilder().instanceProvider(DefaultInstances.DEFAULT).andThen(BukkitCloner.class).andThen(ImmutableDetector.class).andThen(CollectionCloner.class).andThen(FieldCloner.class).build();
/*     */   private List<Cloner> cloners;
/*     */   private WeakReference<Object> lastObject;
/*     */   private int lastResult;
/*     */ 
/*     */   public static Builder newBuilder()
/*     */   {
/* 192 */     return new Builder();
/*     */   }
/*     */ 
/*     */   public List<Cloner> getCloners()
/*     */   {
/* 207 */     return Collections.unmodifiableList(this.cloners);
/*     */   }
/*     */ 
/*     */   private void setCloners(Iterable<? extends Cloner> cloners)
/*     */   {
/* 215 */     this.cloners = Lists.newArrayList(cloners);
/*     */   }
/*     */ 
/*     */   public boolean canClone(Object source)
/*     */   {
/* 221 */     this.lastResult = getFirstCloner(source);
/* 222 */     this.lastObject = new WeakReference(source);
/* 223 */     return (this.lastResult >= 0) && (this.lastResult < this.cloners.size());
/*     */   }
/*     */ 
/*     */   private int getFirstCloner(Object source)
/*     */   {
/* 234 */     for (int i = 0; i < this.cloners.size(); i++) {
/* 235 */       if (((Cloner)this.cloners.get(i)).canClone(source)) {
/* 236 */         return i;
/*     */       }
/*     */     }
/* 239 */     return this.cloners.size();
/*     */   }
/*     */ 
/*     */   public Object clone(Object source)
/*     */   {
/* 244 */     if (source == null)
/* 245 */       throw new IllegalAccessError("source cannot be NULL.");
/* 246 */     int index = 0;
/*     */ 
/* 249 */     if ((this.lastObject != null) && (this.lastObject.get() == source))
/* 250 */       index = this.lastResult;
/*     */     else {
/* 252 */       index = getFirstCloner(source);
/*     */     }
/*     */ 
/* 256 */     if (index < this.cloners.size()) {
/* 257 */       return ((Cloner)this.cloners.get(index)).clone(source);
/*     */     }
/*     */ 
/* 261 */     throw new IllegalArgumentException("Cannot clone " + source + ": No cloner is suitable.");
/*     */   }
/*     */ 
/*     */   public static class Builder
/*     */   {
/*  78 */     private List<Function<AggregateCloner.BuilderParameters, Cloner>> factories = Lists.newArrayList();
/*     */     private AggregateCloner.BuilderParameters parameters;
/*     */ 
/*     */     public Builder()
/*     */     {
/*  85 */       this.parameters = new AggregateCloner.BuilderParameters(null);
/*     */     }
/*     */ 
/*     */     public Builder instanceProvider(InstanceProvider provider)
/*     */     {
/*  94 */       AggregateCloner.BuilderParameters.access$102(this.parameters, provider);
/*  95 */       return this;
/*     */     }
/*     */ 
/*     */     public Builder andThen(final Class<? extends Cloner> type)
/*     */     {
/* 105 */       return andThen(new Function()
/*     */       {
/*     */         public Cloner apply(@Nullable AggregateCloner.BuilderParameters param) {
/* 108 */           Object result = AggregateCloner.BuilderParameters.access$200(param).create(type);
/*     */ 
/* 110 */           if (result == null) {
/* 111 */             throw new IllegalStateException("Constructed NULL instead of " + type);
/*     */           }
/*     */ 
/* 114 */           if (type.isAssignableFrom(result.getClass())) {
/* 115 */             return (Cloner)result;
/*     */           }
/* 117 */           throw new IllegalStateException("Constructed " + result.getClass() + " instead of " + type);
/*     */         }
/*     */       });
/*     */     }
/*     */ 
/*     */     public Builder andThen(Function<AggregateCloner.BuilderParameters, Cloner> factory)
/*     */     {
/* 128 */       this.factories.add(factory);
/* 129 */       return this;
/*     */     }
/*     */ 
/*     */     public AggregateCloner build()
/*     */     {
/* 137 */       AggregateCloner newCloner = new AggregateCloner(null);
/*     */ 
/* 140 */       Cloner paramCloner = new NullableCloner(newCloner);
/* 141 */       InstanceProvider paramProvider = AggregateCloner.BuilderParameters.access$100(this.parameters);
/*     */ 
/* 144 */       AggregateCloner.BuilderParameters.access$402(this.parameters, paramCloner);
/* 145 */       AggregateCloner.BuilderParameters.access$202(this.parameters, DefaultInstances.fromArray(new InstanceProvider[] { ExistingGenerator.fromObjectArray(new Object[] { paramCloner, paramProvider }) }));
/*     */ 
/* 150 */       List cloners = Lists.newArrayList();
/*     */ 
/* 152 */       for (int i = 0; i < this.factories.size(); i++) {
/* 153 */         Cloner cloner = (Cloner)((Function)this.factories.get(i)).apply(this.parameters);
/*     */ 
/* 156 */         if (cloner != null)
/* 157 */           cloners.add(cloner);
/*     */         else {
/* 159 */           throw new IllegalArgumentException(String.format("Cannot create cloner from %s (%s)", new Object[] { this.factories.get(i), Integer.valueOf(i) }));
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 165 */       newCloner.setCloners(cloners);
/* 166 */       return newCloner;
/*     */     }
/*     */   }
/*     */ 
/*     */   public static class BuilderParameters
/*     */   {
/*     */     private InstanceProvider instanceProvider;
/*     */     private Cloner aggregateCloner;
/*     */     private InstanceProvider typeConstructor;
/*     */ 
/*     */     public InstanceProvider getInstanceProvider()
/*     */     {
/*  60 */       return this.instanceProvider;
/*     */     }
/*     */ 
/*     */     public Cloner getAggregateCloner()
/*     */     {
/*  68 */       return this.aggregateCloner;
/*     */     }
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.reflect.cloning.AggregateCloner
 * JD-Core Version:    0.6.2
 */