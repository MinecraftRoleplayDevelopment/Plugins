/*     */ package com.comphenix.protocol.timing;
/*     */ 
/*     */ import com.comphenix.protocol.PacketType;
/*     */ import com.google.common.base.Charsets;
/*     */ import com.google.common.base.Strings;
/*     */ import com.google.common.collect.Sets;
/*     */ import com.google.common.io.Files;
/*     */ import java.io.BufferedWriter;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.io.Writer;
/*     */ import java.util.Date;
/*     */ import java.util.Map;
/*     */ 
/*     */ public class TimingReportGenerator
/*     */ {
/*  18 */   private static final String NEWLINE = System.getProperty("line.separator");
/*  19 */   private static final String META_STARTED = "Started: %s" + NEWLINE;
/*  20 */   private static final String META_STOPPED = "Stopped: %s (after %s seconds)" + NEWLINE;
/*  21 */   private static final String PLUGIN_HEADER = "=== PLUGIN %s ===" + NEWLINE;
/*  22 */   private static final String LISTENER_HEADER = " TYPE: %s " + NEWLINE;
/*  23 */   private static final String SEPERATION_LINE = " " + Strings.repeat("-", 139) + NEWLINE;
/*  24 */   private static final String STATISTICS_HEADER = " Protocol:      Name:                         ID:                 Count:       Min (ms):       Max (ms):       Mean (ms):      Std (ms): " + NEWLINE;
/*     */ 
/*  27 */   private static final String STATISTICS_ROW = " %-15s %-29s %-19s %-12d %-15.6f %-15.6f %-15.6f %.6f " + NEWLINE;
/*  28 */   private static final String SUM_MAIN_THREAD = " => Time on main thread: %.6f ms" + NEWLINE;
/*     */ 
/*     */   public void saveTo(File destination, TimedListenerManager manager) throws IOException {
/*  31 */     BufferedWriter writer = null;
/*  32 */     Date started = manager.getStarted();
/*  33 */     Date stopped = manager.getStopped();
/*  34 */     long seconds = Math.abs((stopped.getTime() - started.getTime()) / 1000L);
/*     */     try
/*     */     {
/*  37 */       writer = Files.newWriter(destination, Charsets.UTF_8);
/*     */ 
/*  40 */       writer.write(String.format(META_STARTED, new Object[] { started }));
/*  41 */       writer.write(String.format(META_STOPPED, new Object[] { stopped, Long.valueOf(seconds) }));
/*  42 */       writer.write(NEWLINE);
/*     */ 
/*  44 */       for (String plugin : manager.getTrackedPlugins()) {
/*  45 */         writer.write(String.format(PLUGIN_HEADER, new Object[] { plugin }));
/*     */ 
/*  47 */         for (TimedListenerManager.ListenerType type : TimedListenerManager.ListenerType.values()) {
/*  48 */           TimedTracker tracker = manager.getTracker(plugin, type);
/*     */ 
/*  51 */           if (tracker.getObservations() > 0) {
/*  52 */             writer.write(String.format(LISTENER_HEADER, new Object[] { type }));
/*     */ 
/*  54 */             writer.write(SEPERATION_LINE);
/*  55 */             saveStatistics(writer, tracker, type);
/*  56 */             writer.write(SEPERATION_LINE);
/*     */           }
/*     */         }
/*     */ 
/*  60 */         writer.write(NEWLINE);
/*     */       }
/*     */     } finally {
/*  63 */       if (writer != null)
/*  64 */         writer.flush();
/*     */     }
/*     */   }
/*     */ 
/*     */   private void saveStatistics(Writer destination, TimedTracker tracker, TimedListenerManager.ListenerType type) throws IOException
/*     */   {
/*  70 */     Map streams = tracker.getStatistics();
/*  71 */     StatisticsStream sum = new StatisticsStream();
/*  72 */     int count = 0;
/*     */ 
/*  74 */     destination.write(STATISTICS_HEADER);
/*  75 */     destination.write(SEPERATION_LINE);
/*     */ 
/*  78 */     for (PacketType key : Sets.newTreeSet(streams.keySet())) {
/*  79 */       StatisticsStream stream = (StatisticsStream)streams.get(key);
/*     */ 
/*  81 */       if ((stream != null) && (stream.getCount() > 0)) {
/*  82 */         printStatistic(destination, key, stream);
/*     */ 
/*  85 */         count++;
/*  86 */         sum = sum.add(stream);
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/*  91 */     if (count > 1) {
/*  92 */       printStatistic(destination, null, sum);
/*     */     }
/*     */ 
/*  95 */     if (type == TimedListenerManager.ListenerType.SYNC_SERVER_SIDE)
/*  96 */       destination.write(String.format(SUM_MAIN_THREAD, new Object[] { Double.valueOf(toMilli(sum.getCount() * sum.getMean())) }));
/*     */   }
/*     */ 
/*     */   private void printStatistic(Writer destination, PacketType key, StatisticsStream stream)
/*     */     throws IOException
/*     */   {
/* 103 */     destination.write(String.format(STATISTICS_ROW, new Object[] { key != null ? key.getProtocol() : "SUM", key != null ? key.name() : "-", key != null ? getPacketId(key) : "-", Integer.valueOf(stream.getCount()), Double.valueOf(toMilli(stream.getMinimum())), Double.valueOf(toMilli(stream.getMaximum())), Double.valueOf(toMilli(stream.getMean())), Double.valueOf(toMilli(stream.getStandardDeviation())) }));
/*     */   }
/*     */ 
/*     */   private String getPacketId(PacketType type)
/*     */   {
/* 116 */     return Strings.padStart(Integer.toString(type.getCurrentId()), 2, '0') + " (Legacy: " + type.getLegacyId() + ")";
/*     */   }
/*     */ 
/*     */   private double toMilli(double value)
/*     */   {
/* 125 */     return value / 1000000.0D;
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.timing.TimingReportGenerator
 * JD-Core Version:    0.6.2
 */