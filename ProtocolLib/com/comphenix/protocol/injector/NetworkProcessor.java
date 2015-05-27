/*     */ package com.comphenix.protocol.injector;
/*     */ 
/*     */ import com.comphenix.protocol.ProtocolLibrary;
/*     */ import com.comphenix.protocol.ProtocolManager;
/*     */ import com.comphenix.protocol.error.ErrorReporter;
/*     */ import com.comphenix.protocol.events.NetworkMarker;
/*     */ import com.comphenix.protocol.events.PacketEvent;
/*     */ import com.comphenix.protocol.events.PacketOutputHandler;
/*     */ import com.comphenix.protocol.events.PacketPostListener;
/*     */ import com.comphenix.protocol.events.ScheduledPacket;
/*     */ import java.util.List;
/*     */ import java.util.PriorityQueue;
/*     */ 
/*     */ public class NetworkProcessor
/*     */ {
/*     */   private ErrorReporter reporter;
/*     */ 
/*     */   public NetworkProcessor(ErrorReporter reporter)
/*     */   {
/*  27 */     this.reporter = reporter;
/*     */   }
/*     */ 
/*     */   public byte[] processOutput(PacketEvent event, NetworkMarker marker, byte[] input)
/*     */   {
/*  39 */     PriorityQueue handlers = (PriorityQueue)marker.getOutputHandlers();
/*     */ 
/*  41 */     byte[] output = input;
/*     */ 
/*  44 */     while (!handlers.isEmpty()) {
/*  45 */       PacketOutputHandler handler = (PacketOutputHandler)handlers.poll();
/*     */       try
/*     */       {
/*  48 */         byte[] changed = handler.handle(event, output);
/*     */ 
/*  51 */         if (changed != null)
/*  52 */           output = changed;
/*     */         else
/*  54 */           throw new IllegalStateException("Handler cannot return a NULL array.");
/*     */       }
/*     */       catch (OutOfMemoryError e) {
/*  57 */         throw e;
/*     */       } catch (ThreadDeath e) {
/*  59 */         throw e;
/*     */       } catch (Throwable e) {
/*  61 */         this.reporter.reportMinimal(handler.getPlugin(), "PacketOutputHandler.handle()", e);
/*     */       }
/*     */     }
/*  64 */     return output;
/*     */   }
/*     */ 
/*     */   public void invokePostEvent(PacketEvent event, NetworkMarker marker)
/*     */   {
/*  72 */     if (marker == null) {
/*  73 */       return;
/*     */     }
/*  75 */     if (NetworkMarker.hasPostListeners(marker))
/*     */     {
/*  77 */       for (PacketPostListener listener : marker.getPostListeners()) {
/*     */         try {
/*  79 */           listener.onPostEvent(event);
/*     */         } catch (OutOfMemoryError e) {
/*  81 */           throw e;
/*     */         } catch (ThreadDeath e) {
/*  83 */           throw e;
/*     */         } catch (Throwable e) {
/*  85 */           this.reporter.reportMinimal(listener.getPlugin(), "SentListener.run()", e);
/*     */         }
/*     */       }
/*     */     }
/*  89 */     sendScheduledPackets(marker);
/*     */   }
/*     */ 
/*     */   private void sendScheduledPackets(NetworkMarker marker)
/*     */   {
/*  98 */     List scheduled = NetworkMarker.readScheduledPackets(marker);
/*  99 */     ProtocolManager manager = ProtocolLibrary.getProtocolManager();
/*     */ 
/* 101 */     if (scheduled != null)
/* 102 */       for (ScheduledPacket packet : scheduled)
/* 103 */         packet.schedule(manager);
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.injector.NetworkProcessor
 * JD-Core Version:    0.6.2
 */