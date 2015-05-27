package org.mcstats;

public abstract class Metrics$Plotter
{
  private final String name;

  public Metrics$Plotter()
  {
    this("Default");
  }

  public Metrics$Plotter(String paramString)
  {
    this.name = paramString;
  }

  public abstract int getValue();

  public String getColumnName()
  {
    return this.name;
  }

  public void reset()
  {
  }

  public int hashCode()
  {
    return getColumnName().hashCode();
  }

  public boolean equals(Object paramObject)
  {
    if (!(paramObject instanceof Plotter))
      return false;
    return ((paramObject = (Plotter)paramObject).name.equals(this.name)) && (paramObject.getValue() == getValue());
  }
}

/* Location:           D:\Github\Mechanics\CombatIndicator.jar
 * Qualified Name:     org.mcstats.Metrics.Plotter
 * JD-Core Version:    0.6.2
 */