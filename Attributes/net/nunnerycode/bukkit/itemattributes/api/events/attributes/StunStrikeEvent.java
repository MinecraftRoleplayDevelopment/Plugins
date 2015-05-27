package net.nunnerycode.bukkit.itemattributes.api.events.attributes;

import org.bukkit.entity.LivingEntity;

public abstract interface StunStrikeEvent
{
  public abstract double getStunRate();

  public abstract int getStunLength();

  public abstract LivingEntity getTarget();
}

/* Location:           D:\Github\Mechanics\ItemAttributes.jar
 * Qualified Name:     net.nunnerycode.bukkit.itemattributes.api.events.attributes.StunStrikeEvent
 * JD-Core Version:    0.6.2
 */