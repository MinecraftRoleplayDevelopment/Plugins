/*    */ package main.Chunks;
/*    */ 
/*    */ import java.util.List;
/*    */ import org.bukkit.Chunk;
/*    */ import org.bukkit.World;
/*    */ import org.bukkit.event.EventHandler;
/*    */ import org.bukkit.event.Listener;
/*    */ import org.bukkit.event.world.ChunkLoadEvent;
/*    */ 
/*    */ public class ChunkListener
/*    */   implements Listener
/*    */ {
/*    */   @EventHandler
/*    */   public void onChunkLoad(ChunkLoadEvent e)
/*    */   {
/* 12 */     if (CM.worlds.size() == 0) {
/* 13 */       return;
/*    */     }
/* 15 */     if (e.isNewChunk()) {
/* 16 */       String world = e.getWorld().getName();
/* 17 */       for (String w : CM.worlds)
/* 18 */         if (w.equalsIgnoreCase(world)) {
/* 19 */           Chunk c = e.getChunk();
/* 20 */           c.unload(false, false);
/* 21 */           break;
/*    */         }
/*    */     }
/*    */   }
/*    */ }

/* Location:           D:\Github\Mechanics\NoNewChunks.jar
 * Qualified Name:     net.PixelizedMC.NoNewChunks.ChunkListener
 * JD-Core Version:    0.6.2
 */