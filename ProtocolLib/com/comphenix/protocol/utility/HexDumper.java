/*     */ package com.comphenix.protocol.utility;
/*     */ 
/*     */ import com.google.common.base.Preconditions;
/*     */ import java.io.IOException;
/*     */ 
/*     */ public class HexDumper
/*     */ {
/*  13 */   private static final char[] HEX_DIGITS = "0123456789ABCDEF".toCharArray();
/*     */ 
/*  16 */   private int positionLength = 6;
/*  17 */   private char[] positionSuffix = ": ".toCharArray();
/*  18 */   private char[] delimiter = " ".toCharArray();
/*  19 */   private int groupLength = 2;
/*  20 */   private int groupCount = 24;
/*  21 */   private char[] lineDelimiter = "\n".toCharArray();
/*     */ 
/*     */   public static HexDumper defaultDumper()
/*     */   {
/*  58 */     return new HexDumper();
/*     */   }
/*     */ 
/*     */   public HexDumper lineDelimiter(String lineDelimiter)
/*     */   {
/*  67 */     this.lineDelimiter = ((String)Preconditions.checkNotNull(lineDelimiter, "lineDelimiter cannot be NULL")).toCharArray();
/*  68 */     return this;
/*     */   }
/*     */ 
/*     */   public HexDumper positionLength(int positionLength)
/*     */   {
/*  77 */     if (positionLength < 0)
/*  78 */       throw new IllegalArgumentException("positionLength cannot be less than zero.");
/*  79 */     if (positionLength > 8)
/*  80 */       throw new IllegalArgumentException("positionLength cannot be greater than eight.");
/*  81 */     this.positionLength = positionLength;
/*  82 */     return this;
/*     */   }
/*     */ 
/*     */   public HexDumper positionSuffix(String positionSuffix)
/*     */   {
/*  91 */     this.positionSuffix = ((String)Preconditions.checkNotNull(positionSuffix, "positionSuffix cannot be NULL")).toCharArray();
/*  92 */     return this;
/*     */   }
/*     */ 
/*     */   public HexDumper delimiter(String delimiter)
/*     */   {
/* 101 */     this.delimiter = ((String)Preconditions.checkNotNull(delimiter, "delimiter cannot be NULL")).toCharArray();
/* 102 */     return this;
/*     */   }
/*     */ 
/*     */   public HexDumper groupLength(int groupLength)
/*     */   {
/* 111 */     if (groupLength < 1)
/* 112 */       throw new IllegalArgumentException("groupLength cannot be less than one.");
/* 113 */     this.groupLength = groupLength;
/* 114 */     return this;
/*     */   }
/*     */ 
/*     */   public HexDumper groupCount(int groupCount)
/*     */   {
/* 125 */     if (groupCount < 1)
/* 126 */       throw new IllegalArgumentException("groupCount cannot be less than one.");
/* 127 */     this.groupCount = groupCount;
/* 128 */     return this;
/*     */   }
/*     */ 
/*     */   public void appendTo(Appendable appendable, byte[] data)
/*     */     throws IOException
/*     */   {
/* 140 */     appendTo(appendable, data, 0, data.length);
/*     */   }
/*     */ 
/*     */   public void appendTo(Appendable appendable, byte[] data, int start, int length)
/*     */     throws IOException
/*     */   {
/* 152 */     StringBuilder output = new StringBuilder();
/* 153 */     appendTo(output, data, start, length);
/* 154 */     appendable.append(output.toString());
/*     */   }
/*     */ 
/*     */   public void appendTo(StringBuilder builder, byte[] data)
/*     */   {
/* 165 */     appendTo(builder, data, 0, data.length);
/*     */   }
/*     */ 
/*     */   public void appendTo(StringBuilder builder, byte[] data, int start, int length)
/*     */   {
/* 177 */     int dataIndex = start;
/* 178 */     int dataEnd = start + length;
/* 179 */     int groupCounter = 0;
/* 180 */     int currentGroupLength = 0;
/*     */ 
/* 183 */     int value = 0;
/* 184 */     boolean highNiblet = true;
/*     */ 
/* 186 */     while ((dataIndex < dataEnd) || (!highNiblet))
/*     */     {
/* 188 */       if ((groupCounter == 0) && (currentGroupLength == 0))
/*     */       {
/* 190 */         for (int i = this.positionLength - 1; i >= 0; i--) {
/* 191 */           builder.append(HEX_DIGITS[(dataIndex >>> 4 * i & 0xF)]);
/*     */         }
/* 193 */         builder.append(this.positionSuffix);
/*     */       }
/*     */ 
/* 197 */       if (highNiblet) {
/* 198 */         value = data[(dataIndex++)] & 0xFF;
/* 199 */         builder.append(HEX_DIGITS[(value >>> 4)]);
/*     */       } else {
/* 201 */         builder.append(HEX_DIGITS[(value & 0xF)]);
/*     */       }
/* 203 */       highNiblet = !highNiblet;
/* 204 */       currentGroupLength++;
/*     */ 
/* 207 */       if (currentGroupLength >= this.groupLength) {
/* 208 */         currentGroupLength = 0;
/*     */ 
/* 211 */         groupCounter++; if (groupCounter >= this.groupCount) {
/* 212 */           builder.append(this.lineDelimiter);
/* 213 */           groupCounter = 0;
/*     */         }
/*     */         else {
/* 216 */           builder.append(this.delimiter);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public int getLineLength(int byteCount)
/*     */   {
/* 228 */     int constant = this.positionLength + this.positionSuffix.length + this.lineDelimiter.length;
/* 229 */     int groups = Math.min(2 * byteCount / this.groupLength, this.groupCount);
/*     */ 
/* 232 */     return constant + this.delimiter.length * (groups - 1) + this.groupLength * groups;
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.utility.HexDumper
 * JD-Core Version:    0.6.2
 */