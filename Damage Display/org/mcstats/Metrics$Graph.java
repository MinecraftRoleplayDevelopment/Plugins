package org.mcstats;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

public class Metrics$Graph
{
  private final String name;
  private final Set plotters = new LinkedHashSet();

  private Metrics$Graph(String paramString)
  {
    this.name = paramString;
  }

  public String getName()
  {
    return this.name;
  }

  public void addPlotter(Metrics.Plotter paramPlotter)
  {
    this.plotters.add(paramPlotter);
  }

  public void removePlotter(Metrics.Plotter paramPlotter)
  {
    this.plotters.remove(paramPlotter);
  }

  public Set getPlotters()
  {
    return Collections.unmodifiableSet(this.plotters);
  }

  public int hashCode()
  {
    return this.name.hashCode();
  }

  public boolean equals(Object paramObject)
  {
    if (!(paramObject instanceof Graph))
      return false;
    return (paramObject = (Graph)paramObject).name.equals(this.name);
  }

  protected void onOptOut()
  {
  }
}

/* Location:           D:\Github\Mechanics\CombatIndicator.jar
 * Qualified Name:     org.mcstats.Metrics.Graph
 * JD-Core Version:    0.6.2
 */