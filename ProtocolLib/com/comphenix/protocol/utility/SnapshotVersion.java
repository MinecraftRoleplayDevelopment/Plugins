/*     */ package com.comphenix.protocol.utility;
/*     */ 
/*     */ import com.google.common.base.Objects;
/*     */ import com.google.common.collect.ComparisonChain;
/*     */ import java.io.Serializable;
/*     */ import java.text.ParseException;
/*     */ import java.text.SimpleDateFormat;
/*     */ import java.util.Calendar;
/*     */ import java.util.Date;
/*     */ import java.util.Locale;
/*     */ import java.util.regex.Matcher;
/*     */ import java.util.regex.Pattern;
/*     */ 
/*     */ public class SnapshotVersion
/*     */   implements Comparable<SnapshotVersion>, Serializable
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*  23 */   private static final Pattern SNAPSHOT_PATTERN = Pattern.compile("(\\d{2}w\\d{2})([a-z])");
/*     */   private final Date snapshotDate;
/*     */   private final int snapshotWeekVersion;
/*     */   private transient String rawString;
/*     */ 
/*     */   public SnapshotVersion(String version)
/*     */   {
/*  31 */     Matcher matcher = SNAPSHOT_PATTERN.matcher(version.trim());
/*     */ 
/*  33 */     if (matcher.matches())
/*     */       try {
/*  35 */         this.snapshotDate = getDateFormat().parse(matcher.group(1));
/*  36 */         this.snapshotWeekVersion = (matcher.group(2).charAt(0) - 'a');
/*  37 */         this.rawString = version;
/*     */       } catch (ParseException e) {
/*  39 */         throw new IllegalArgumentException("Date implied by snapshot version is invalid.", e);
/*     */       }
/*     */     else
/*  42 */       throw new IllegalArgumentException("Cannot parse " + version + " as a snapshot version.");
/*     */   }
/*     */ 
/*     */   private static SimpleDateFormat getDateFormat()
/*     */   {
/*  53 */     SimpleDateFormat format = new SimpleDateFormat("yy'w'ww", Locale.US);
/*  54 */     format.setLenient(false);
/*  55 */     return format;
/*     */   }
/*     */ 
/*     */   public int getSnapshotWeekVersion()
/*     */   {
/*  63 */     return this.snapshotWeekVersion;
/*     */   }
/*     */ 
/*     */   public Date getSnapshotDate()
/*     */   {
/*  71 */     return this.snapshotDate;
/*     */   }
/*     */ 
/*     */   public String getSnapshotString()
/*     */   {
/*  79 */     if (this.rawString == null)
/*     */     {
/*  81 */       Calendar current = Calendar.getInstance(Locale.US);
/*  82 */       current.setTime(this.snapshotDate);
/*  83 */       this.rawString = String.format("%02dw%02d%s", new Object[] { Integer.valueOf(current.get(1) % 100), Integer.valueOf(current.get(3)), Character.valueOf((char)(97 + this.snapshotWeekVersion)) });
/*     */     }
/*     */ 
/*  88 */     return this.rawString;
/*     */   }
/*     */ 
/*     */   public int compareTo(SnapshotVersion o)
/*     */   {
/*  93 */     if (o == null) {
/*  94 */       return 1;
/*     */     }
/*  96 */     return ComparisonChain.start().compare(this.snapshotDate, o.getSnapshotDate()).compare(this.snapshotWeekVersion, o.getSnapshotWeekVersion()).result();
/*     */   }
/*     */ 
/*     */   public boolean equals(Object obj)
/*     */   {
/* 104 */     if (obj == this)
/* 105 */       return true;
/* 106 */     if ((obj instanceof SnapshotVersion)) {
/* 107 */       SnapshotVersion other = (SnapshotVersion)obj;
/* 108 */       return (Objects.equal(this.snapshotDate, other.getSnapshotDate())) && (this.snapshotWeekVersion == other.getSnapshotWeekVersion());
/*     */     }
/*     */ 
/* 111 */     return false;
/*     */   }
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 116 */     return Objects.hashCode(new Object[] { this.snapshotDate, Integer.valueOf(this.snapshotWeekVersion) });
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 121 */     return getSnapshotString();
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.utility.SnapshotVersion
 * JD-Core Version:    0.6.2
 */