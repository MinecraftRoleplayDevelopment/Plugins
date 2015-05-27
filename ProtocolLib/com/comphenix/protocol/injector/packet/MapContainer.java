/*    */ package com.comphenix.protocol.injector.packet;
/*    */ 
/*    */ import com.comphenix.protocol.reflect.FieldUtils;
/*    */ import java.lang.reflect.Field;
/*    */ 
/*    */ public class MapContainer
/*    */ {
/*    */   private Field modCountField;
/*    */   private int lastModCount;
/*    */   private Object source;
/*    */   private boolean changed;
/*    */ 
/*    */   public MapContainer(Object source)
/*    */   {
/* 21 */     this.source = source;
/* 22 */     this.changed = true;
/* 23 */     this.modCountField = FieldUtils.getField(source.getClass(), "modCount", true);
/*    */   }
/*    */ 
/*    */   public boolean hasChanged()
/*    */   {
/* 32 */     checkChanged();
/* 33 */     return this.changed;
/*    */   }
/*    */ 
/*    */   public void setChanged(boolean changed)
/*    */   {
/* 41 */     this.changed = changed;
/*    */   }
/*    */ 
/*    */   protected void checkChanged()
/*    */   {
/* 48 */     if ((!this.changed) && 
/* 49 */       (getModificationCount() != this.lastModCount)) {
/* 50 */       this.lastModCount = getModificationCount();
/* 51 */       this.changed = true;
/*    */     }
/*    */   }
/*    */ 
/*    */   private int getModificationCount()
/*    */   {
/*    */     try
/*    */     {
/* 62 */       return this.modCountField != null ? this.modCountField.getInt(this.source) : this.lastModCount + 1;
/*    */     } catch (Exception e) {
/* 64 */       throw new RuntimeException("Unable to retrieve modCount.", e);
/*    */     }
/*    */   }
/*    */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.injector.packet.MapContainer
 * JD-Core Version:    0.6.2
 */