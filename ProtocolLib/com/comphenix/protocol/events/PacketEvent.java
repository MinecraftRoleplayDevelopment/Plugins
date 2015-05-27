/*     */ package com.comphenix.protocol.events;
/*     */ 
/*     */ import com.comphenix.protocol.Application;
/*     */ import com.comphenix.protocol.PacketType;
/*     */ import com.comphenix.protocol.ProtocolLibrary;
/*     */ import com.comphenix.protocol.async.AsyncMarker;
/*     */ import com.comphenix.protocol.error.ErrorReporter;
/*     */ import com.comphenix.protocol.error.Report;
/*     */ import com.comphenix.protocol.error.Report.ReportBuilder;
/*     */ import com.comphenix.protocol.error.ReportType;
/*     */ import com.google.common.base.Objects;
/*     */ import com.google.common.base.Preconditions;
/*     */ import com.google.common.collect.HashMultimap;
/*     */ import com.google.common.collect.Multimaps;
/*     */ import com.google.common.collect.SetMultimap;
/*     */ import java.io.IOException;
/*     */ import java.io.ObjectInputStream;
/*     */ import java.io.ObjectOutputStream;
/*     */ import java.lang.ref.WeakReference;
/*     */ import java.util.EventObject;
/*     */ import java.util.List;
/*     */ import org.bukkit.OfflinePlayer;
/*     */ import org.bukkit.entity.Player;
/*     */ import org.bukkit.event.Cancellable;
/*     */ 
/*     */ public class PacketEvent extends EventObject
/*     */   implements Cancellable
/*     */ {
/*  47 */   public static final ReportType REPORT_CHANGING_PACKET_TYPE_IS_CONFUSING = new ReportType("Plugin %s changed packet type from %s to %s in packet listener. This is confusing for other plugins! (Not an error, though!)");
/*     */ 
/*  50 */   private static final SetMultimap<PacketType, PacketType> CHANGE_WARNINGS = Multimaps.synchronizedSetMultimap(HashMultimap.create());
/*     */   private static final long serialVersionUID = -5360289379097430620L;
/*     */   private transient WeakReference<Player> playerReference;
/*     */   private transient Player offlinePlayer;
/*     */   private PacketContainer packet;
/*     */   private boolean serverPacket;
/*     */   private boolean cancel;
/*     */   private AsyncMarker asyncMarker;
/*     */   private boolean asynchronous;
/*     */   NetworkMarker networkMarker;
/*     */   private boolean readOnly;
/*     */   private boolean filtered;
/*     */ 
/*     */   public PacketEvent(Object source)
/*     */   {
/*  80 */     super(source);
/*  81 */     this.filtered = true;
/*     */   }
/*     */ 
/*     */   private PacketEvent(Object source, PacketContainer packet, Player player, boolean serverPacket) {
/*  85 */     this(source, packet, null, player, serverPacket, true);
/*     */   }
/*     */ 
/*     */   private PacketEvent(Object source, PacketContainer packet, NetworkMarker marker, Player player, boolean serverPacket, boolean filtered) {
/*  89 */     super(source);
/*  90 */     this.packet = packet;
/*  91 */     this.playerReference = new WeakReference(player);
/*  92 */     this.networkMarker = marker;
/*  93 */     this.serverPacket = serverPacket;
/*  94 */     this.filtered = filtered;
/*     */   }
/*     */ 
/*     */   private PacketEvent(PacketEvent origial, AsyncMarker asyncMarker) {
/*  98 */     super(origial.source);
/*  99 */     this.packet = origial.packet;
/* 100 */     this.playerReference = origial.playerReference;
/* 101 */     this.cancel = origial.cancel;
/* 102 */     this.serverPacket = origial.serverPacket;
/* 103 */     this.filtered = origial.filtered;
/* 104 */     this.networkMarker = origial.networkMarker;
/* 105 */     this.asyncMarker = asyncMarker;
/* 106 */     this.asynchronous = true;
/*     */   }
/*     */ 
/*     */   public static PacketEvent fromClient(Object source, PacketContainer packet, Player client)
/*     */   {
/* 117 */     return new PacketEvent(source, packet, client, false);
/*     */   }
/*     */ 
/*     */   public static PacketEvent fromClient(Object source, PacketContainer packet, NetworkMarker marker, Player client)
/*     */   {
/* 129 */     return new PacketEvent(source, packet, marker, client, false, true);
/*     */   }
/*     */ 
/*     */   public static PacketEvent fromClient(Object source, PacketContainer packet, NetworkMarker marker, Player client, boolean filtered)
/*     */   {
/* 144 */     return new PacketEvent(source, packet, marker, client, false, filtered);
/*     */   }
/*     */ 
/*     */   public static PacketEvent fromServer(Object source, PacketContainer packet, Player recipient)
/*     */   {
/* 155 */     return new PacketEvent(source, packet, recipient, true);
/*     */   }
/*     */ 
/*     */   public static PacketEvent fromServer(Object source, PacketContainer packet, NetworkMarker marker, Player recipient)
/*     */   {
/* 167 */     return new PacketEvent(source, packet, marker, recipient, true, true);
/*     */   }
/*     */ 
/*     */   public static PacketEvent fromServer(Object source, PacketContainer packet, NetworkMarker marker, Player recipient, boolean filtered)
/*     */   {
/* 182 */     return new PacketEvent(source, packet, marker, recipient, true, filtered);
/*     */   }
/*     */ 
/*     */   public static PacketEvent fromSynchronous(PacketEvent event, AsyncMarker marker)
/*     */   {
/* 192 */     return new PacketEvent(event, marker);
/*     */   }
/*     */ 
/*     */   public boolean isAsync()
/*     */   {
/* 205 */     return !Application.isPrimaryThread();
/*     */   }
/*     */ 
/*     */   public PacketContainer getPacket()
/*     */   {
/* 213 */     return this.packet;
/*     */   }
/*     */ 
/*     */   public void setPacket(PacketContainer packet)
/*     */   {
/* 221 */     if (this.readOnly)
/* 222 */       throw new IllegalStateException("The packet event is read-only.");
/* 223 */     if (packet == null) {
/* 224 */       throw new IllegalArgumentException("Cannot set packet to NULL. Use setCancelled() instead.");
/*     */     }
/*     */ 
/* 227 */     PacketType oldType = this.packet.getType();
/* 228 */     PacketType newType = packet.getType();
/* 229 */     if ((this.packet != null) && (!Objects.equal(oldType, newType)))
/*     */     {
/* 231 */       if (CHANGE_WARNINGS.put(oldType, newType)) {
/* 232 */         ProtocolLibrary.getErrorReporter().reportWarning(this, Report.newBuilder(REPORT_CHANGING_PACKET_TYPE_IS_CONFUSING).messageParam(new Object[] { oldType, newType }).build());
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 238 */     this.packet = packet;
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public int getPacketID()
/*     */   {
/* 249 */     return this.packet.getID();
/*     */   }
/*     */ 
/*     */   public PacketType getPacketType()
/*     */   {
/* 257 */     return this.packet.getType();
/*     */   }
/*     */ 
/*     */   public boolean isCancelled()
/*     */   {
/* 265 */     return this.cancel;
/*     */   }
/*     */ 
/*     */   public NetworkMarker getNetworkMarker()
/*     */   {
/* 276 */     if (this.networkMarker == null) {
/* 277 */       if (isServerPacket()) {
/* 278 */         this.networkMarker = new NetworkMarker.EmptyBufferMarker(this.serverPacket ? ConnectionSide.SERVER_SIDE : ConnectionSide.CLIENT_SIDE);
/*     */       }
/*     */       else {
/* 281 */         throw new IllegalStateException("Add the option ListenerOptions.INTERCEPT_INPUT_BUFFER to your listener.");
/*     */       }
/*     */     }
/* 284 */     return this.networkMarker;
/*     */   }
/*     */ 
/*     */   public void setNetworkMarker(NetworkMarker networkMarker)
/*     */   {
/* 294 */     this.networkMarker = ((NetworkMarker)Preconditions.checkNotNull(networkMarker, "marker cannot be NULL"));
/*     */   }
/*     */ 
/*     */   public void setCancelled(boolean cancel)
/*     */   {
/* 311 */     if (this.readOnly)
/* 312 */       throw new IllegalStateException("The packet event is read-only.");
/* 313 */     this.cancel = cancel;
/*     */   }
/*     */ 
/*     */   public Player getPlayer()
/*     */   {
/* 321 */     return (Player)this.playerReference.get();
/*     */   }
/*     */ 
/*     */   public boolean isFiltered()
/*     */   {
/* 331 */     return this.filtered;
/*     */   }
/*     */ 
/*     */   public boolean isServerPacket()
/*     */   {
/* 341 */     return this.serverPacket;
/*     */   }
/*     */ 
/*     */   public AsyncMarker getAsyncMarker()
/*     */   {
/* 354 */     return this.asyncMarker;
/*     */   }
/*     */ 
/*     */   public void setAsyncMarker(AsyncMarker asyncMarker)
/*     */   {
/* 367 */     if (isAsynchronous())
/* 368 */       throw new IllegalStateException("The marker is immutable for asynchronous events");
/* 369 */     if (this.readOnly)
/* 370 */       throw new IllegalStateException("The packet event is read-only.");
/* 371 */     this.asyncMarker = asyncMarker;
/*     */   }
/*     */ 
/*     */   public boolean isReadOnly()
/*     */   {
/* 384 */     return this.readOnly;
/*     */   }
/*     */ 
/*     */   public void setReadOnly(boolean readOnly)
/*     */   {
/* 394 */     this.readOnly = readOnly;
/*     */   }
/*     */ 
/*     */   public boolean isAsynchronous()
/*     */   {
/* 402 */     return this.asynchronous;
/*     */   }
/*     */ 
/*     */   public void schedule(ScheduledPacket scheduled)
/*     */   {
/* 412 */     getNetworkMarker().getScheduledPackets().add(scheduled);
/*     */   }
/*     */ 
/*     */   public boolean unschedule(ScheduledPacket scheduled)
/*     */   {
/* 421 */     if (this.networkMarker != null) {
/* 422 */       return this.networkMarker.getScheduledPackets().remove(scheduled);
/*     */     }
/* 424 */     return false;
/*     */   }
/*     */ 
/*     */   private void writeObject(ObjectOutputStream output) throws IOException
/*     */   {
/* 429 */     output.defaultWriteObject();
/*     */ 
/* 432 */     output.writeObject(this.playerReference.get() != null ? new SerializedOfflinePlayer((OfflinePlayer)this.playerReference.get()) : null);
/*     */   }
/*     */ 
/*     */   private void readObject(ObjectInputStream input) throws ClassNotFoundException, IOException
/*     */   {
/* 437 */     input.defaultReadObject();
/*     */ 
/* 439 */     SerializedOfflinePlayer serialized = (SerializedOfflinePlayer)input.readObject();
/*     */ 
/* 442 */     if (serialized != null)
/*     */     {
/* 444 */       this.offlinePlayer = serialized.getPlayer();
/* 445 */       this.playerReference = new WeakReference(this.offlinePlayer);
/*     */     }
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.events.PacketEvent
 * JD-Core Version:    0.6.2
 */