/*     */ package com.comphenix.protocol.utility;
/*     */ 
/*     */ import com.google.common.base.Objects;
/*     */ import com.google.common.collect.ComparisonChain;
/*     */ import com.google.common.collect.Ordering;
/*     */ import java.io.Serializable;
/*     */ import java.text.SimpleDateFormat;
/*     */ import java.util.Date;
/*     */ import java.util.Locale;
/*     */ import java.util.regex.Matcher;
/*     */ import java.util.regex.Pattern;
/*     */ import org.bukkit.Bukkit;
/*     */ import org.bukkit.Server;
/*     */ 
/*     */ public class MinecraftVersion
/*     */   implements Comparable<MinecraftVersion>, Serializable
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*  45 */   private static final Pattern VERSION_PATTERN = Pattern.compile(".*\\(.*MC.\\s*([a-zA-z0-9\\-\\.]+)\\s*\\)");
/*     */ 
/*  50 */   public static final MinecraftVersion BOUNTIFUL_UPDATE = new MinecraftVersion("1.8");
/*     */ 
/*  55 */   public static final MinecraftVersion SKIN_UPDATE = new MinecraftVersion("1.7.8");
/*     */ 
/*  60 */   public static final MinecraftVersion WORLD_UPDATE = new MinecraftVersion("1.7.2");
/*     */ 
/*  65 */   public static final MinecraftVersion HORSE_UPDATE = new MinecraftVersion("1.6.1");
/*     */ 
/*  70 */   public static final MinecraftVersion REDSTONE_UPDATE = new MinecraftVersion("1.5.0");
/*     */ 
/*  75 */   public static final MinecraftVersion SCARY_UPDATE = new MinecraftVersion("1.4.2");
/*     */   private final int major;
/*     */   private final int minor;
/*     */   private final int build;
/*     */   private final String development;
/*     */   private final SnapshotVersion snapshot;
/*     */   private static MinecraftVersion currentVersion;
/*     */ 
/*     */   public MinecraftVersion(Server server)
/*     */   {
/*  92 */     this(extractVersion(server.getVersion()));
/*     */   }
/*     */ 
/*     */   public MinecraftVersion(String versionOnly)
/*     */   {
/* 100 */     this(versionOnly, true);
/*     */   }
/*     */ 
/*     */   private MinecraftVersion(String versionOnly, boolean parseSnapshot)
/*     */   {
/* 109 */     String[] section = versionOnly.split("-");
/* 110 */     SnapshotVersion snapshot = null;
/* 111 */     int[] numbers = new int[3];
/*     */     try
/*     */     {
/* 114 */       numbers = parseVersion(section[0]);
/*     */     }
/*     */     catch (NumberFormatException cause)
/*     */     {
/* 118 */       if (!parseSnapshot) {
/* 119 */         throw cause;
/*     */       }
/*     */       try
/*     */       {
/* 123 */         snapshot = new SnapshotVersion(section[0]);
/* 124 */         SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
/*     */ 
/* 126 */         MinecraftVersion latest = new MinecraftVersion("1.8.3", false);
/* 127 */         boolean newer = snapshot.getSnapshotDate().compareTo(format.parse("2015-02-20")) > 0;
/*     */ 
/* 130 */         numbers[0] = latest.getMajor();
/* 131 */         numbers[1] = (latest.getMinor() + (newer ? 1 : -1));
/* 132 */         numbers[2] = 0;
/*     */       } catch (Exception e) {
/* 134 */         throw new IllegalStateException("Cannot parse " + section[0], e);
/*     */       }
/*     */     }
/*     */ 
/* 138 */     this.major = numbers[0];
/* 139 */     this.minor = numbers[1];
/* 140 */     this.build = numbers[2];
/* 141 */     this.development = (snapshot != null ? "snapshot" : section.length > 1 ? section[1] : null);
/* 142 */     this.snapshot = snapshot;
/*     */   }
/*     */ 
/*     */   public MinecraftVersion(int major, int minor, int build)
/*     */   {
/* 152 */     this(major, minor, build, null);
/*     */   }
/*     */ 
/*     */   public MinecraftVersion(int major, int minor, int build, String development)
/*     */   {
/* 163 */     this.major = major;
/* 164 */     this.minor = minor;
/* 165 */     this.build = build;
/* 166 */     this.development = development;
/* 167 */     this.snapshot = null;
/*     */   }
/*     */ 
/*     */   private int[] parseVersion(String version) {
/* 171 */     String[] elements = version.split("\\.");
/* 172 */     int[] numbers = new int[3];
/*     */ 
/* 175 */     if (elements.length < 1) {
/* 176 */       throw new IllegalStateException("Corrupt MC version: " + version);
/*     */     }
/*     */ 
/* 179 */     for (int i = 0; i < Math.min(numbers.length, elements.length); i++)
/* 180 */       numbers[i] = Integer.parseInt(elements[i].trim());
/* 181 */     return numbers;
/*     */   }
/*     */ 
/*     */   public int getMajor()
/*     */   {
/* 189 */     return this.major;
/*     */   }
/*     */ 
/*     */   public int getMinor()
/*     */   {
/* 197 */     return this.minor;
/*     */   }
/*     */ 
/*     */   public int getBuild()
/*     */   {
/* 205 */     return this.build;
/*     */   }
/*     */ 
/*     */   public String getDevelopmentStage()
/*     */   {
/* 213 */     return this.development;
/*     */   }
/*     */ 
/*     */   public SnapshotVersion getSnapshot()
/*     */   {
/* 221 */     return this.snapshot;
/*     */   }
/*     */ 
/*     */   public boolean isSnapshot()
/*     */   {
/* 229 */     return this.snapshot != null;
/*     */   }
/*     */ 
/*     */   public String getVersion()
/*     */   {
/* 237 */     if (getDevelopmentStage() == null) {
/* 238 */       return String.format("%s.%s.%s", new Object[] { Integer.valueOf(getMajor()), Integer.valueOf(getMinor()), Integer.valueOf(getBuild()) });
/*     */     }
/* 240 */     return String.format("%s.%s.%s-%s%s", new Object[] { Integer.valueOf(getMajor()), Integer.valueOf(getMinor()), Integer.valueOf(getBuild()), getDevelopmentStage(), isSnapshot() ? this.snapshot : "" });
/*     */   }
/*     */ 
/*     */   public int compareTo(MinecraftVersion o)
/*     */   {
/* 246 */     if (o == null) {
/* 247 */       return 1;
/*     */     }
/* 249 */     return ComparisonChain.start().compare(getMajor(), o.getMajor()).compare(getMinor(), o.getMinor()).compare(getBuild(), o.getBuild()).compare(getDevelopmentStage(), o.getDevelopmentStage(), Ordering.natural().nullsLast()).compare(getSnapshot(), o.getSnapshot(), Ordering.natural().nullsFirst()).result();
/*     */   }
/*     */ 
/*     */   public boolean isAtLeast(MinecraftVersion other)
/*     */   {
/* 260 */     if (other == null) {
/* 261 */       return false;
/*     */     }
/* 263 */     return compareTo(other) >= 0;
/*     */   }
/*     */ 
/*     */   public boolean equals(Object obj)
/*     */   {
/* 268 */     if (obj == null)
/* 269 */       return false;
/* 270 */     if (obj == this) {
/* 271 */       return true;
/*     */     }
/* 273 */     if ((obj instanceof MinecraftVersion)) {
/* 274 */       MinecraftVersion other = (MinecraftVersion)obj;
/*     */ 
/* 276 */       return (getMajor() == other.getMajor()) && (getMinor() == other.getMinor()) && (getBuild() == other.getBuild()) && (Objects.equal(getDevelopmentStage(), other.getDevelopmentStage()));
/*     */     }
/*     */ 
/* 282 */     return false;
/*     */   }
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 287 */     return Objects.hashCode(new Object[] { Integer.valueOf(getMajor()), Integer.valueOf(getMinor()), Integer.valueOf(getBuild()) });
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 293 */     return String.format("(MC: %s)", new Object[] { getVersion() });
/*     */   }
/*     */ 
/*     */   public static String extractVersion(String text)
/*     */   {
/* 303 */     Matcher version = VERSION_PATTERN.matcher(text);
/*     */ 
/* 305 */     if ((version.matches()) && (version.group(1) != null)) {
/* 306 */       return version.group(1);
/*     */     }
/* 308 */     throw new IllegalStateException("Cannot parse version String '" + text + "'");
/*     */   }
/*     */ 
/*     */   public static MinecraftVersion fromServerVersion(String serverVersion)
/*     */   {
/* 318 */     return new MinecraftVersion(extractVersion(serverVersion));
/*     */   }
/*     */ 
/*     */   public static void setCurrentVersion(MinecraftVersion version)
/*     */   {
/* 324 */     currentVersion = version;
/*     */   }
/*     */ 
/*     */   public static MinecraftVersion getCurrentVersion() {
/* 328 */     if (currentVersion == null) {
/* 329 */       currentVersion = fromServerVersion(Bukkit.getVersion());
/*     */     }
/*     */ 
/* 332 */     return currentVersion;
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.utility.MinecraftVersion
 * JD-Core Version:    0.6.2
 */