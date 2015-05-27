/*    */ package com.comphenix.protocol.injector.server;
/*    */ 
/*    */ import com.comphenix.protocol.error.ErrorReporter;
/*    */ import org.bukkit.Server;
/*    */ 
/*    */ public class InputStreamLookupBuilder
/*    */ {
/*    */   private Server server;
/*    */   private ErrorReporter reporter;
/*    */ 
/*    */   public static InputStreamLookupBuilder newBuilder()
/*    */   {
/* 14 */     return new InputStreamLookupBuilder();
/*    */   }
/*    */ 
/*    */   public InputStreamLookupBuilder server(Server server)
/*    */   {
/* 30 */     this.server = server;
/* 31 */     return this;
/*    */   }
/*    */ 
/*    */   public InputStreamLookupBuilder reporter(ErrorReporter reporter)
/*    */   {
/* 40 */     this.reporter = reporter;
/* 41 */     return this;
/*    */   }
/*    */ 
/*    */   public AbstractInputStreamLookup build() {
/* 45 */     return new InputStreamReflectLookup(this.reporter, this.server);
/*    */   }
/*    */ }

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.injector.server.InputStreamLookupBuilder
 * JD-Core Version:    0.6.2
 */