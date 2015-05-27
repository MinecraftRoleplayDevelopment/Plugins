package me.coder.combatindicator;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;

final class aa extends Z
{
  public final String a(Entity paramEntity)
  {
    if ((paramEntity instanceof LivingEntity))
      return ((LivingEntity)paramEntity).getCustomName();
    return null;
  }

  protected final boolean a(Entity paramEntity)
  {
    if ((paramEntity instanceof LivingEntity))
      return ((LivingEntity)paramEntity).isCustomNameVisible();
    return false;
  }

  protected final int a()
  {
    try
    {
      return (localObject = (Player[])Bukkit.class.getMethod("getOnlinePlayers", new Class[0]).invoke(null, new Object[0])).length;
    }
    catch (IllegalAccessException localIllegalAccessException)
    {
      (localObject = localIllegalAccessException).printStackTrace();
    }
    catch (InvocationTargetException localInvocationTargetException)
    {
      (localObject = localInvocationTargetException).printStackTrace();
    }
    catch (NoSuchMethodException localNoSuchMethodException)
    {
      Object localObject;
      (localObject = localNoSuchMethodException).printStackTrace();
    }
    return 0;
  }

  protected final double a(EntityDamageEvent paramEntityDamageEvent)
  {
    return paramEntityDamageEvent.getDamage();
  }

  protected final boolean a()
  {
    return false;
  }
}

/* Location:           D:\Github\Mechanics\CombatIndicator.jar
 * Qualified Name:     me.coder.combatindicator.aa
 * JD-Core Version:    0.6.2
 */