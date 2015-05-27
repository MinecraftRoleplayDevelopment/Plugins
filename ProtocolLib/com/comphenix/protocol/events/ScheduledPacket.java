/*     */ package com.comphenix.protocol.events;
/*     */ 
/*     */ import com.comphenix.protocol.PacketStream;
/*     */ import com.comphenix.protocol.PacketType;
/*     */ import com.comphenix.protocol.PacketType.Sender;
/*     */ import com.comphenix.protocol.ProtocolLibrary;
/*     */ import com.google.common.base.Objects;
/*     */ import com.google.common.base.Objects.ToStringHelper;
/*     */ import com.google.common.base.Preconditions;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import org.bukkit.entity.Player;
/*     */ 
/*     */ public class ScheduledPacket
/*     */ {
/*     */   protected PacketContainer packet;
/*     */   protected Player target;
/*     */   protected boolean filtered;
/*     */ 
/*     */   public ScheduledPacket(PacketContainer packet, Player target, boolean filtered)
/*     */   {
/*  31 */     setPacket(packet);
/*  32 */     setTarget(target);
/*  33 */     setFiltered(filtered);
/*     */   }
/*     */ 
/*     */   public static ScheduledPacket fromSilent(PacketContainer packet, Player target)
/*     */   {
/*  43 */     return new ScheduledPacket(packet, target, false);
/*     */   }
/*     */ 
/*     */   public static ScheduledPacket fromFiltered(PacketContainer packet, Player target)
/*     */   {
/*  53 */     return new ScheduledPacket(packet, target, true);
/*     */   }
/*     */ 
/*     */   public PacketContainer getPacket()
/*     */   {
/*  61 */     return this.packet;
/*     */   }
/*     */ 
/*     */   public void setPacket(PacketContainer packet)
/*     */   {
/*  69 */     this.packet = ((PacketContainer)Preconditions.checkNotNull(packet, "packet cannot be NULL"));
/*     */   }
/*     */ 
/*     */   public Player getTarget()
/*     */   {
/*  77 */     return this.target;
/*     */   }
/*     */ 
/*     */   public void setTarget(Player target)
/*     */   {
/*  85 */     this.target = ((Player)Preconditions.checkNotNull(target, "target cannot be NULL"));
/*     */   }
/*     */ 
/*     */   public boolean isFiltered()
/*     */   {
/*  93 */     return this.filtered;
/*     */   }
/*     */ 
/*     */   public void setFiltered(boolean filtered)
/*     */   {
/* 101 */     this.filtered = filtered;
/*     */   }
/*     */ 
/*     */   public PacketType.Sender getSender()
/*     */   {
/* 109 */     return this.packet.getType().getSender();
/*     */   }
/*     */ 
/*     */   public void schedule()
/*     */   {
/* 116 */     schedule(ProtocolLibrary.getProtocolManager());
/*     */   }
/*     */ 
/*     */   public void schedule(PacketStream stream)
/*     */   {
/* 124 */     Preconditions.checkNotNull(stream, "stream cannot be NULL");
/*     */     try
/*     */     {
/* 127 */       if (getSender() == PacketType.Sender.CLIENT)
/* 128 */         stream.recieveClientPacket(getTarget(), getPacket(), isFiltered());
/*     */       else
/* 130 */         stream.sendServerPacket(getTarget(), getPacket(), isFiltered());
/*     */     }
/*     */     catch (InvocationTargetException e) {
/* 133 */       throw new RuntimeException("Cannot send packet " + this + " to " + stream);
/*     */     } catch (IllegalAccessException e) {
/* 135 */       throw new RuntimeException("Cannot send packet " + this + " to " + stream);
/*     */     }
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 141 */     return Objects.toStringHelper(this).add("packet", this.packet).add("target", this.target).add("filtered", this.filtered).toString();
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.events.ScheduledPacket
 * JD-Core Version:    0.6.2
 */