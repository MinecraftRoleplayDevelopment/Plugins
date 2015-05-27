package me.coder.combatindicator;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

 enum B
{
  B()
  {
    super(str, 0, (byte)0);
  }

  public final boolean a(Entity paramEntity)
  {
    return (paramEntity != null) && ((paramEntity instanceof Player));
  }
}

/* Location:           D:\Github\Mechanics\CombatIndicator.jar
 * Qualified Name:     me.coder.combatindicator.B
 * JD-Core Version:    0.6.2
 */