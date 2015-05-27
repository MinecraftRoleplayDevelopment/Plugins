/*     */ package com.comphenix.protocol;
/*     */ 
/*     */ import org.bukkit.conversations.Conversation;
/*     */ import org.bukkit.conversations.ConversationCanceller;
/*     */ import org.bukkit.conversations.ConversationContext;
/*     */ import org.bukkit.conversations.ExactMatchConversationCanceller;
/*     */ import org.bukkit.conversations.Prompt;
/*     */ import org.bukkit.conversations.StringPrompt;
/*     */ 
/*     */ class MultipleLinesPrompt extends StringPrompt
/*     */ {
/*     */   private static final String KEY = "multiple_lines_prompt";
/*     */   private static final String KEY_LAST = "multiple_lines_prompt.last_line";
/*     */   private static final String KEY_LINES = "multiple_lines_prompt.linecount";
/*     */   private final MultipleConversationCanceller endMarker;
/*     */   private final String initialPrompt;
/*     */ 
/*     */   public String removeAccumulatedInput(ConversationContext context)
/*     */   {
/*  87 */     Object result = context.getSessionData("multiple_lines_prompt");
/*     */ 
/*  89 */     if ((result instanceof StringBuilder)) {
/*  90 */       context.setSessionData("multiple_lines_prompt", null);
/*  91 */       context.setSessionData("multiple_lines_prompt.linecount", null);
/*  92 */       return ((StringBuilder)result).toString();
/*     */     }
/*  94 */     return null;
/*     */   }
/*     */ 
/*     */   public MultipleLinesPrompt(String endMarker, String initialPrompt)
/*     */   {
/* 106 */     this(new ExactMatchConversationCanceller(endMarker), initialPrompt);
/*     */   }
/*     */ 
/*     */   public MultipleLinesPrompt(ConversationCanceller endMarker, String initialPrompt)
/*     */   {
/* 117 */     this.endMarker = new MultipleWrapper(endMarker);
/* 118 */     this.initialPrompt = initialPrompt;
/*     */   }
/*     */ 
/*     */   public MultipleLinesPrompt(MultipleConversationCanceller endMarker, String initialPrompt)
/*     */   {
/* 127 */     this.endMarker = endMarker;
/* 128 */     this.initialPrompt = initialPrompt;
/*     */   }
/*     */ 
/*     */   public Prompt acceptInput(ConversationContext context, String in)
/*     */   {
/* 133 */     StringBuilder result = (StringBuilder)context.getSessionData("multiple_lines_prompt");
/* 134 */     Integer count = (Integer)context.getSessionData("multiple_lines_prompt.linecount");
/*     */ 
/* 137 */     if (result == null)
/* 138 */       context.setSessionData("multiple_lines_prompt", result = new StringBuilder());
/* 139 */     if (count == null) {
/* 140 */       count = Integer.valueOf(0);
/*     */     }
/*     */ 
/* 143 */     context.setSessionData("multiple_lines_prompt.last_line", in);
/* 144 */     context.setSessionData("multiple_lines_prompt.linecount", count = Integer.valueOf(count.intValue() + 1));
/* 145 */     result.append(in + "\n");
/*     */ 
/* 148 */     if (this.endMarker.cancelBasedOnInput(context, in, result, count.intValue())) {
/* 149 */       return Prompt.END_OF_CONVERSATION;
/*     */     }
/* 151 */     return this;
/*     */   }
/*     */ 
/*     */   public String getPromptText(ConversationContext context)
/*     */   {
/* 156 */     Object last = context.getSessionData("multiple_lines_prompt.last_line");
/*     */ 
/* 158 */     if ((last instanceof String)) {
/* 159 */       return (String)last;
/*     */     }
/* 161 */     return this.initialPrompt;
/*     */   }
/*     */ 
/*     */   private static class MultipleWrapper
/*     */     implements MultipleLinesPrompt.MultipleConversationCanceller
/*     */   {
/*     */     private ConversationCanceller canceller;
/*     */ 
/*     */     public MultipleWrapper(ConversationCanceller canceller)
/*     */     {
/*  46 */       this.canceller = canceller;
/*     */     }
/*     */ 
/*     */     public boolean cancelBasedOnInput(ConversationContext context, String currentLine)
/*     */     {
/*  51 */       return this.canceller.cancelBasedOnInput(context, currentLine);
/*     */     }
/*     */ 
/*     */     public boolean cancelBasedOnInput(ConversationContext context, String currentLine, StringBuilder lines, int lineCount)
/*     */     {
/*  57 */       return cancelBasedOnInput(context, currentLine);
/*     */     }
/*     */ 
/*     */     public void setConversation(Conversation conversation)
/*     */     {
/*  62 */       this.canceller.setConversation(conversation);
/*     */     }
/*     */ 
/*     */     public MultipleWrapper clone()
/*     */     {
/*  67 */       return new MultipleWrapper(this.canceller.clone());
/*     */     }
/*     */   }
/*     */ 
/*     */   public static abstract interface MultipleConversationCanceller extends ConversationCanceller
/*     */   {
/*     */     public abstract boolean cancelBasedOnInput(ConversationContext paramConversationContext, String paramString);
/*     */ 
/*     */     public abstract boolean cancelBasedOnInput(ConversationContext paramConversationContext, String paramString, StringBuilder paramStringBuilder, int paramInt);
/*     */   }
/*     */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.MultipleLinesPrompt
 * JD-Core Version:    0.6.2
 */