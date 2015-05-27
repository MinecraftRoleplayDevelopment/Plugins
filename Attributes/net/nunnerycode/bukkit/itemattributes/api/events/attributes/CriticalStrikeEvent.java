package net.nunnerycode.bukkit.itemattributes.api.events.attributes;

import org.bukkit.entity.LivingEntity;

public abstract interface CriticalStrikeEvent
{
  public abstract double getCriticalRate();

  public abstract double getCriticalDamage();

  public abstract LivingEntity getTarget();
}

/* Location:           D:\Github\Mechanics\ItemAttributes.jar
 * Qualified Name:     net.nunnerycode.bukkit.itemattributes.api.events.attributes.CriticalStrikeEvent
 * JD-Core Version:    0.6.2
 */