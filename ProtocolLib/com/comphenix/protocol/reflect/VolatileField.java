/*     */ package com.comphenix.protocol.reflect;
/*     */ 
/*     */ import com.comphenix.protocol.ProtocolLibrary;
/*     */ import com.comphenix.protocol.reflect.accessors.Accessors;
/*     */ import com.comphenix.protocol.reflect.accessors.FieldAccessor;
/*     */ import com.google.common.base.Objects;
/*     */ import java.lang.reflect.Field;
/*     */ 
/*     */ public class VolatileField
/*     */ {
/*     */   private FieldAccessor accessor;
/*     */   private Object container;
/*     */   private Object previous;
/*     */   private Object current;
/*     */   private boolean previousLoaded;
/*     */   private boolean currentSet;
/*     */   private boolean forceAccess;
/*     */ 
/*     */   public VolatileField(Field field, Object container)
/*     */   {
/*  53 */     this.accessor = Accessors.getFieldAccessor(field);
/*  54 */     this.container = container;
/*     */   }
/*     */ 
/*     */   public VolatileField(Field field, Object container, boolean forceAccess)
/*     */   {
/*  64 */     this.accessor = Accessors.getFieldAccessor(field, true);
/*  65 */     this.container = container;
/*  66 */     this.forceAccess = forceAccess;
/*     */   }
/*     */ 
/*     */   public VolatileField(FieldAccessor accessor, Object container)
/*     */   {
/*  75 */     this.accessor = accessor;
/*  76 */     this.container = container;
/*     */   }
/*     */ 
/*     */   public Field getField()
/*     */   {
/*  84 */     return this.accessor.getField();
/*     */   }
/*     */ 
/*     */   public Object getContainer()
/*     */   {
/*  92 */     return this.container;
/*     */   }
/*     */ 
/*     */   public boolean isForceAccess()
/*     */   {
/* 100 */     return this.forceAccess;
/*     */   }
/*     */ 
/*     */   public void setForceAccess(boolean forceAccess)
/*     */   {
/* 108 */     this.forceAccess = forceAccess;
/*     */   }
/*     */ 
/*     */   public Object getValue()
/*     */   {
/* 117 */     if (!this.currentSet) {
/* 118 */       ensureLoaded();
/* 119 */       return this.previous;
/*     */     }
/* 121 */     return this.current;
/*     */   }
/*     */ 
/*     */   public Object getOldValue()
/*     */   {
/* 130 */     ensureLoaded();
/* 131 */     return this.previous;
/*     */   }
/*     */ 
/*     */   public void setValue(Object newValue)
/*     */   {
/* 140 */     ensureLoaded();
/*     */ 
/* 142 */     writeFieldValue(newValue);
/* 143 */     this.current = newValue;
/* 144 */     this.currentSet = true;
/*     */   }
/*     */ 
/*     */   public void refreshValue()
/*     */   {
/* 153 */     Object fieldValue = readFieldValue();
/*     */ 
/* 155 */     if (this.currentSet)
/*     */     {
/* 157 */       if (!Objects.equal(this.current, fieldValue)) {
/* 158 */         this.previous = readFieldValue();
/* 159 */         this.previousLoaded = true;
/* 160 */         writeFieldValue(this.current);
/*     */       }
/* 162 */     } else if (this.previousLoaded)
/*     */     {
/* 164 */       this.previous = fieldValue;
/*     */     }
/*     */   }
/*     */ 
/*     */   public void saveValue()
/*     */   {
/* 172 */     this.previous = this.current;
/* 173 */     this.currentSet = false;
/*     */   }
/*     */ 
/*     */   public void revertValue()
/*     */   {
/* 181 */     if (this.currentSet)
/* 182 */       if (getValue() == this.current) {
/* 183 */         setValue(this.previous);
/* 184 */         this.currentSet = false;
/*     */       }
/*     */       else {
/* 187 */         ProtocolLibrary.log("Unable to switch {0} to {1}. Expected {2}, but got {3}.", new Object[] { getField().toGenericString(), this.previous, this.current, getValue() });
/*     */       }
/*     */   }
/*     */ 
/*     */   public VolatileField toSynchronized()
/*     */   {
/* 198 */     return new VolatileField(Accessors.getSynchronized(this.accessor), this.container);
/*     */   }
/*     */ 
/*     */   public boolean isCurrentSet()
/*     */   {
/* 205 */     return this.currentSet;
/*     */   }
/*     */ 
/*     */   private void ensureLoaded()
/*     */   {
/* 210 */     if (!this.previousLoaded) {
/* 211 */       this.previous = readFieldValue();
/* 212 */       this.previousLoaded = true;
/*     */     }
/*     */   }
/*     */ 
/*     */   private Object readFieldValue()
/*     */   {
/* 221 */     return this.accessor.get(this.container);
/*     */   }
/*     */ 
/*     */   private void writeFieldValue(Object newValue)
/*     */   {
/* 229 */     this.accessor.set(this.container, newValue);
/*     */   }
/*     */ 
/*     */   protected void finalize() throws Throwable
/*     */   {
/* 234 */     revertValue();
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 239 */     return "VolatileField [accessor=" + this.accessor + ", container=" + this.container + ", previous=" + this.previous + ", current=" + this.current + ", previousLoaded=" + this.previousLoaded + ", currentSet=" + this.currentSet + ", forceAccess=" + this.forceAccess + "]";
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.reflect.VolatileField
 * JD-Core Version:    0.6.2
 */