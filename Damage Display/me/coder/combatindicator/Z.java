package me.coder.combatindicator;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityDamageEvent;

public abstract class Z
{
  private static final Z a = new aa((byte)0);

  protected abstract String a(Entity paramEntity);

  protected abstract boolean a(Entity paramEntity);

  protected abstract int a();

  protected abstract double a(EntityDamageEvent paramEntityDamageEvent);

  protected abstract boolean a();

  public static String b(Entity paramEntity)
  {
    return a.a(paramEntity);
  }

  public static boolean b(Entity paramEntity)
  {
    return a.a(paramEntity);
  }

  public static int b()
  {
    return a.a();
  }

  public static double b(EntityDamageEvent paramEntityDamageEvent)
  {
    return a.a(paramEntityDamageEvent);
  }

  public static boolean b()
  {
    return a.a();
  }

  public static void a()
  {
  }

  static
  {
    String str;
    if ((str = Bukkit.getBukkitVersion()).startsWith("1.8"))
    {
      if ((!str.startsWith("1.8-")) && (!str.startsWith("1.8.1")) && (!str.startsWith("1.8.2")))
      {
        a = new ab((byte)0);
        return;
      }
      a = new ac((byte)0);
      return;
    }
  }
}

/* Location:           D:\Github\Mechanics\CombatIndicator.jar
 * Qualified Name:     me.coder.combatindicator.Z
 * JD-Core Version:    0.6.2
 */