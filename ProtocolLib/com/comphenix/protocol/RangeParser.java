/*     */ package com.comphenix.protocol;
/*     */ 
/*     */ import com.google.common.collect.ContiguousSet;
/*     */ import com.google.common.collect.DiscreteDomain;
/*     */ import com.google.common.collect.Range;
/*     */ import java.util.ArrayDeque;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Deque;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ 
/*     */ class RangeParser
/*     */ {
/*     */   public static List<Range<Integer>> getRanges(String text, Range<Integer> legalRange)
/*     */   {
/*  43 */     return getRanges(new ArrayDeque(Arrays.asList(new String[] { text })), legalRange);
/*     */   }
/*     */ 
/*     */   public static List<Range<Integer>> getRanges(Deque<String> input, Range<Integer> legalRange)
/*     */   {
/*  55 */     List tokens = tokenizeInput(input);
/*  56 */     List ranges = new ArrayList();
/*     */ 
/*  58 */     for (int i = 0; i < tokens.size(); i++)
/*     */     {
/*  60 */       String current = (String)tokens.get(i);
/*  61 */       String next = i + 1 < tokens.size() ? (String)tokens.get(i + 1) : null;
/*     */ 
/*  64 */       if ("-".equals(current))
/*  65 */         throw new IllegalArgumentException("A hyphen must appear between two numbers.");
/*     */       Range range;
/*  66 */       if ("-".equals(next)) {
/*  67 */         if (i + 2 >= tokens.size()) {
/*  68 */           throw new IllegalArgumentException("Cannot form a range without a upper limit.");
/*     */         }
/*     */ 
/*  71 */         Range range = Range.closed(Integer.valueOf(Integer.parseInt(current)), Integer.valueOf(Integer.parseInt((String)tokens.get(i + 2))));
/*  72 */         ranges.add(range);
/*     */ 
/*  75 */         i += 2;
/*     */       }
/*     */       else
/*     */       {
/*  79 */         range = Range.singleton(Integer.valueOf(Integer.parseInt(current)));
/*  80 */         ranges.add(range);
/*     */       }
/*     */ 
/*  84 */       if (!legalRange.encloses(range)) {
/*  85 */         throw new IllegalArgumentException(range + " is not in the range " + range.toString());
/*     */       }
/*     */     }
/*     */ 
/*  89 */     return simplify(ranges, ((Integer)legalRange.upperEndpoint()).intValue());
/*     */   }
/*     */ 
/*     */   private static List<Range<Integer>> simplify(List<Range<Integer>> ranges, int maximum)
/*     */   {
/*  99 */     List result = new ArrayList();
/* 100 */     boolean[] set = new boolean[maximum + 1];
/* 101 */     int start = -1;
/*     */ 
/* 104 */     for (Range range : ranges)
/* 105 */       for (i$ = ContiguousSet.create(range, DiscreteDomain.integers()).iterator(); i$.hasNext(); ) { int id = ((Integer)i$.next()).intValue();
/* 106 */         set[id] = true;
/*     */       }
/*     */     Iterator i$;
/* 111 */     for (int i = 0; i <= set.length; i++) {
/* 112 */       if ((i < set.length) && (set[i] != 0)) {
/* 113 */         if (start < 0) {
/* 114 */           start = i;
/*     */         }
/*     */       }
/* 117 */       else if (start >= 0) {
/* 118 */         result.add(Range.closed(Integer.valueOf(start), Integer.valueOf(i - 1)));
/* 119 */         start = -1;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 124 */     return result;
/*     */   }
/*     */ 
/*     */   private static List<String> tokenizeInput(Deque<String> input) {
/* 128 */     List tokens = new ArrayList();
/*     */ 
/* 131 */     while (!input.isEmpty()) {
/* 132 */       StringBuilder number = new StringBuilder();
/* 133 */       String text = (String)input.peek();
/*     */ 
/* 135 */       for (int j = 0; j < text.length(); j++) {
/* 136 */         char current = text.charAt(j);
/*     */ 
/* 138 */         if (Character.isDigit(current))
/* 139 */           number.append(current);
/* 140 */         else if (!Character.isWhitespace(current))
/*     */         {
/* 142 */           if (current == '-')
/*     */           {
/* 144 */             if (number.length() > 0) {
/* 145 */               tokens.add(number.toString());
/* 146 */               number.setLength(0);
/*     */             }
/*     */ 
/* 149 */             tokens.add(Character.toString(current));
/*     */           }
/*     */           else {
/* 152 */             return tokens;
/*     */           }
/*     */         }
/*     */       }
/*     */ 
/* 157 */       if (number.length() > 0)
/* 158 */         tokens.add(number.toString());
/* 159 */       input.poll();
/*     */     }
/*     */ 
/* 162 */     return tokens;
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.RangeParser
 * JD-Core Version:    0.6.2
 */