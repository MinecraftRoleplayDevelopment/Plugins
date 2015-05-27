/*    */ package com.comphenix.protocol.injector.player;
/*    */ 
/*    */ import com.google.common.base.Joiner;
/*    */ import java.util.Arrays;
/*    */ 
/*    */ class UnsupportedListener
/*    */ {
/*    */   private String message;
/*    */   private int[] packets;
/*    */ 
/*    */   public UnsupportedListener(String message, int[] packets)
/*    */   {
/* 40 */     this.message = message;
/* 41 */     this.packets = packets;
/*    */   }
/*    */ 
/*    */   public String getMessage()
/*    */   {
/* 49 */     return this.message;
/*    */   }
/*    */ 
/*    */   public int[] getPackets()
/*    */   {
/* 57 */     return this.packets;
/*    */   }
/*    */ 
/*    */   public String toString()
/*    */   {
/* 62 */     return String.format("%s (%s)", new Object[] { this.message, Joiner.on(", ").join(Arrays.asList(new int[][] { this.packets })) });
/*    */   }
/*    */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.injector.player.UnsupportedListener
 * JD-Core Version:    0.6.2
 */