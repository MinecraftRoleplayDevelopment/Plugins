package me.coder.combatindicator;

import java.util.Collection;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityDamageEvent;

class ac extends Z
{
  public final String a(Entity paramEntity)
  {
    return paramEntity.getCustomName();
  }

  protected final boolean a(Entity paramEntity)
  {
    return paramEntity.isCustomNameVisible();
  }

  protected final int a()
  {
    return Bukkit.getOnlinePlayers().size();
  }

  protected final double a(EntityDamageEvent paramEntityDamageEvent)
  {
    return paramEntityDamageEvent.getFinalDamage();
  }

  protected boolean a()
  {
    return false;
  }
}

/* Location:           D:\Github\Mechanics\CombatIndicator.jar
 * Qualified Name:     me.coder.combatindicator.ac
 * JD-Core Version:    0.6.2
 */