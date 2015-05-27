/*     */ package com.conventnunnery.libraries.config;
/*     */ 
/*     */ import com.google.common.io.Files;
/*     */ import java.io.File;
/*     */ import java.io.FileWriter;
/*     */ import java.io.IOException;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ 
/*     */ public class CommentedConventYamlConfiguration extends ConventYamlConfiguration
/*     */ {
/*     */   private Map<String, String> comments;
/*     */ 
/*     */   public CommentedConventYamlConfiguration(File file)
/*     */   {
/*  15 */     super(file);
/*  16 */     this.comments = new HashMap();
/*     */   }
/*     */ 
/*     */   public CommentedConventYamlConfiguration(File file, String version) {
/*  20 */     super(file, version);
/*  21 */     this.comments = new HashMap();
/*     */   }
/*     */ 
/*     */   public void save(String file) throws IOException
/*     */   {
/*  26 */     if (file == null) {
/*  27 */       throw new IllegalArgumentException("File cannot be null");
/*     */     }
/*     */ 
/*  30 */     save(new File(file));
/*     */   }
/*     */ 
/*     */   public void save(File file) throws IOException
/*     */   {
/*  35 */     if (file == null) {
/*  36 */       throw new IllegalArgumentException("File cannot be null");
/*     */     }
/*     */ 
/*  39 */     Files.createParentDirs(file);
/*     */ 
/*  41 */     String data = insertComments(saveToString());
/*     */ 
/*  43 */     FileWriter writer = new FileWriter(file);
/*     */     try
/*     */     {
/*  46 */       writer.write(data);
/*     */     } finally {
/*  48 */       writer.close();
/*     */     }
/*     */   }
/*     */ 
/*     */   private String insertComments(String yaml)
/*     */   {
/*  54 */     if (!this.comments.isEmpty())
/*     */     {
/*  56 */       String[] yamlContents = yaml.split(new StringBuilder().append("[").append(System.getProperty("line.separator")).append("]").toString());
/*     */ 
/*  60 */       StringBuilder newContents = new StringBuilder();
/*     */ 
/*  62 */       StringBuilder currentPath = new StringBuilder();
/*     */ 
/*  64 */       boolean commentedPath = false;
/*     */ 
/*  66 */       boolean node = false;
/*     */ 
/*  68 */       int depth = 0;
/*     */ 
/*  71 */       boolean firstLine = true;
/*     */ 
/*  73 */       for (String line : yamlContents) {
/*  74 */         if (firstLine) {
/*  75 */           firstLine = false;
/*  76 */           if (line.startsWith("#"));
/*     */         }
/*     */         else
/*     */         {
/*  81 */           if ((line.contains(": ")) || ((line.length() > 1) && (line.charAt(line.length() - 1) == ':')))
/*     */           {
/*  85 */             commentedPath = false;
/*     */ 
/*  87 */             node = true;
/*     */ 
/*  90 */             int index = 0;
/*  91 */             index = line.indexOf(": ");
/*  92 */             if (index < 0) {
/*  93 */               index = line.length() - 1;
/*     */             }
/*     */ 
/*  98 */             if (currentPath.toString().isEmpty()) {
/*  99 */               currentPath = new StringBuilder(line.substring(0, index));
/*     */             }
/*     */             else
/*     */             {
/* 103 */               int whiteSpace = 0;
/* 104 */               for (int n = 0; (n < line.length()) && 
/* 105 */                 (line.charAt(n) == ' '); n++)
/*     */               {
/* 106 */                 whiteSpace++;
/*     */               }
/*     */ 
/* 113 */               if (whiteSpace / 2 > depth)
/*     */               {
/* 115 */                 currentPath.append(".").append(line.substring(whiteSpace, index));
/*     */ 
/* 117 */                 depth++;
/* 118 */               } else if (whiteSpace / 2 < depth)
/*     */               {
/* 122 */                 int newDepth = whiteSpace / 2;
/* 123 */                 for (int i = 0; i < depth - newDepth; i++) {
/* 124 */                   currentPath.replace(currentPath.lastIndexOf("."), currentPath.length(), "");
/*     */                 }
/*     */ 
/* 129 */                 int lastIndex = currentPath.lastIndexOf(".");
/* 130 */                 if (lastIndex < 0)
/*     */                 {
/* 133 */                   currentPath = new StringBuilder();
/*     */                 }
/*     */                 else
/*     */                 {
/* 137 */                   currentPath.replace(currentPath.lastIndexOf("."), currentPath.length(), "").append(".");
/*     */                 }
/*     */ 
/* 142 */                 currentPath.append(line.substring(whiteSpace, index));
/*     */ 
/* 145 */                 depth = newDepth;
/*     */               }
/*     */               else
/*     */               {
/* 149 */                 int lastIndex = currentPath.lastIndexOf(".");
/* 150 */                 if (lastIndex < 0)
/*     */                 {
/* 153 */                   currentPath = new StringBuilder();
/*     */                 }
/*     */                 else
/*     */                 {
/* 157 */                   currentPath.replace(currentPath.lastIndexOf("."), currentPath.length(), "").append(".");
/*     */                 }
/*     */ 
/* 164 */                 currentPath.append(line.substring(whiteSpace, index));
/*     */               }
/*     */             }
/*     */           }
/*     */           else {
/* 169 */             node = false;
/*     */           }
/* 171 */           StringBuilder newLine = new StringBuilder(line);
/* 172 */           if (node) {
/* 173 */             String comment = null;
/* 174 */             if (!commentedPath)
/*     */             {
/* 177 */               comment = (String)this.comments.get(currentPath.toString());
/*     */             }
/* 179 */             if ((comment != null) && (!comment.isEmpty()))
/*     */             {
/* 181 */               newLine.insert(0, System.getProperty("line.separator")).insert(0, comment);
/*     */ 
/* 183 */               comment = null;
/* 184 */               commentedPath = true;
/*     */             }
/*     */           }
/* 187 */           newLine.append(System.getProperty("line.separator"));
/*     */ 
/* 189 */           newContents.append(newLine.toString());
/*     */         }
/*     */       }
/* 192 */       return newContents.toString();
/*     */     }
/* 194 */     return yaml;
/*     */   }
/*     */ 
/*     */   public void addComment(String path, String[] commentLines)
/*     */   {
/* 205 */     StringBuilder commentstring = new StringBuilder();
/* 206 */     String leadingSpaces = "";
/* 207 */     for (int n = 0; n < path.length(); n++) {
/* 208 */       if (path.charAt(n) == '.') {
/* 209 */         leadingSpaces = new StringBuilder().append(leadingSpaces).append("  ").toString();
/*     */       }
/*     */     }
/* 212 */     for (String line : commentLines) {
/* 213 */       if (!line.isEmpty()) {
/* 214 */         line = new StringBuilder().append(leadingSpaces).append("# ").append(line).toString();
/*     */       }
/* 216 */       if (commentstring.length() > 0) {
/* 217 */         commentstring.append(System.getProperty("line.separator"));
/*     */       }
/* 219 */       commentstring.append(line);
/*     */     }
/* 221 */     this.comments.put(path, commentstring.toString());
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\ItemAttributes.jar
 * Qualified Name:     com.conventnunnery.libraries.config.CommentedConventYamlConfiguration
 * JD-Core Version:    0.6.2
 */