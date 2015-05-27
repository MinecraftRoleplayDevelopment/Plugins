/*    */ package com.comphenix.protocol.wrappers.nbt;
/*    */ 
/*    */ class MemoryElement<TType>
/*    */   implements NbtBase<TType>
/*    */ {
/*    */   private String name;
/*    */   private TType value;
/*    */   private NbtType type;
/*    */ 
/*    */   public MemoryElement(String name, TType value)
/*    */   {
/*  9 */     if (name == null)
/* 10 */       throw new IllegalArgumentException("Name cannot be NULL.");
/* 11 */     if (value == null) {
/* 12 */       throw new IllegalArgumentException("Element cannot be NULL.");
/*    */     }
/* 14 */     this.name = name;
/* 15 */     this.value = value;
/* 16 */     this.type = NbtType.getTypeFromClass(value.getClass());
/*    */   }
/*    */ 
/*    */   public MemoryElement(String name, TType value, NbtType type) {
/* 20 */     if (name == null)
/* 21 */       throw new IllegalArgumentException("Name cannot be NULL.");
/* 22 */     if (type == null) {
/* 23 */       throw new IllegalArgumentException("Type cannot be NULL.");
/*    */     }
/* 25 */     this.name = name;
/* 26 */     this.value = value;
/* 27 */     this.type = type;
/*    */   }
/*    */ 
/*    */   public boolean accept(NbtVisitor visitor)
/*    */   {
/* 32 */     return visitor.visit(this);
/*    */   }
/*    */ 
/*    */   public NbtType getType()
/*    */   {
/* 37 */     return this.type;
/*    */   }
/*    */ 
/*    */   public String getName()
/*    */   {
/* 42 */     return this.name;
/*    */   }
/*    */ 
/*    */   public void setName(String name)
/*    */   {
/* 47 */     this.name = name;
/*    */   }
/*    */ 
/*    */   public TType getValue()
/*    */   {
/* 52 */     return this.value;
/*    */   }
/*    */ 
/*    */   public void setValue(TType newValue)
/*    */   {
/* 57 */     this.value = newValue;
/*    */   }
/*    */ 
/*    */   public NbtBase<TType> deepClone()
/*    */   {
/* 63 */     return new MemoryElement(this.name, this.value, this.type);
/*    */   }
/*    */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.wrappers.nbt.MemoryElement
 * JD-Core Version:    0.6.2
 */