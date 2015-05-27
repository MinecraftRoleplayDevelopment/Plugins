package org.mcstats;

import java.io.IOException;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

class Metrics$1
  implements Runnable
{
  private boolean firstPost = true;

  Metrics$1(Metrics paramMetrics)
  {
  }

  public void run()
  {
    try
    {
      synchronized (Metrics.access$100(this.this$0))
      {
        if ((this.this$0.isOptOut()) && (Metrics.access$200(this.this$0) != null))
        {
          Metrics.access$200(this.this$0).cancel();
          Metrics.access$202(this.this$0, null);
          Iterator localIterator = Metrics.access$300(this.this$0).iterator();
          while (localIterator.hasNext())
          {
            Metrics.Graph localGraph;
            (localGraph = (Metrics.Graph)localIterator.next()).onOptOut();
          }
        }
      }
      Metrics.access$400(this.this$0, !this.firstPost);
      this.firstPost = false;
      return;
    }
    catch (IOException localIOException)
    {
      if (Metrics.access$500(this.this$0))
        Bukkit.getLogger().log(Level.INFO, "[Metrics] " + localIOException.getMessage());
    }
  }
}

/* Location:           D:\Github\Mechanics\CombatIndicator.jar
 * Qualified Name:     org.mcstats.Metrics.1
 * JD-Core Version:    0.6.2
 */