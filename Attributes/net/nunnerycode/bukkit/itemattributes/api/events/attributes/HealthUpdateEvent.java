package net.nunnerycode.bukkit.itemattributes.api.events.attributes;

public abstract interface HealthUpdateEvent extends LivingEntityAttributeEvent
{
  public abstract double getPreviousHealth();

  public abstract double getBaseHealth();

  public abstract double getChangeInHealth();

  public abstract void setChangeInHealth(double paramDouble);
}

/* Location:           D:\Github\Mechanics\ItemAttributes.jar
 * Qualified Name:     net.nunnerycode.bukkit.itemattributes.api.events.attributes.HealthUpdateEvent
 * JD-Core Version:    0.6.2
 */