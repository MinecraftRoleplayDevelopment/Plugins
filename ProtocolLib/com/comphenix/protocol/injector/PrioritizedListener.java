/*    */ package com.comphenix.protocol.injector;
/*    */ 
/*    */ import com.comphenix.protocol.events.ListenerPriority;
/*    */ import com.google.common.base.Objects;
/*    */ import com.google.common.primitives.Ints;
/*    */ 
/*    */ public class PrioritizedListener<TListener>
/*    */   implements Comparable<PrioritizedListener<TListener>>
/*    */ {
/*    */   private TListener listener;
/*    */   private ListenerPriority priority;
/*    */ 
/*    */   public PrioritizedListener(TListener listener, ListenerPriority priority)
/*    */   {
/* 35 */     this.listener = listener;
/* 36 */     this.priority = priority;
/*    */   }
/*    */ 
/*    */   public int compareTo(PrioritizedListener<TListener> other)
/*    */   {
/* 42 */     return Ints.compare(getPriority().getSlot(), other.getPriority().getSlot());
/*    */   }
/*    */ 
/*    */   public boolean equals(Object obj)
/*    */   {
/* 53 */     if ((obj instanceof PrioritizedListener)) {
/* 54 */       PrioritizedListener other = (PrioritizedListener)obj;
/* 55 */       return Objects.equal(this.listener, other.listener);
/*    */     }
/* 57 */     return false;
/*    */   }
/*    */ 
/*    */   public int hashCode()
/*    */   {
/* 63 */     return Objects.hashCode(new Object[] { this.listener });
/*    */   }
/*    */ 
/*    */   public TListener getListener()
/*    */   {
/* 71 */     return this.listener;
/*    */   }
/*    */ 
/*    */   public ListenerPriority getPriority()
/*    */   {
/* 79 */     return this.priority;
/*    */   }
/*    */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.injector.PrioritizedListener
 * JD-Core Version:    0.6.2
 */