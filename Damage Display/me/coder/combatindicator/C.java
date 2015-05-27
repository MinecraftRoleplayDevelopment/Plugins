package me.coder.combatindicator;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

 enum C
{
  C()
  {
    super(str, 1, (byte)0);
  }

  public final boolean a(Entity paramEntity)
  {
    return (paramEntity != null) && (!(paramEntity instanceof Player));
  }
}

/* Location:           D:\Github\Mechanics\CombatIndicator.jar
 * Qualified Name:     me.coder.combatindicator.C
 * JD-Core Version:    0.6.2
 */