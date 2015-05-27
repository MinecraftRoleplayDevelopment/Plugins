package me.coder.combatindicator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public final class L
  implements F
{
  private static final Random jdField_a_of_type_JavaUtilRandom = new Random();
  private final List jdField_a_of_type_JavaUtilList;

  public L(List paramList)
  {
    this.jdField_a_of_type_JavaUtilList = new ArrayList(paramList.size());
    paramList = paramList.iterator();
    while (paramList.hasNext())
    {
      Object localObject;
      if (((localObject = paramList.next()) instanceof Map))
      {
        localObject = (Map)localObject;
        this.jdField_a_of_type_JavaUtilList.add(new K(new M((String)((Map)localObject).get("format")), (Map)localObject));
      }
      else
      {
        throw new d("RestrictedFormat");
      }
    }
  }

  public final J a(double paramDouble1, double paramDouble2, LivingEntity paramLivingEntity, Entity paramEntity, Player paramPlayer)
  {
    Collections.shuffle(this.jdField_a_of_type_JavaUtilList, jdField_a_of_type_JavaUtilRandom);
    Iterator localIterator = this.jdField_a_of_type_JavaUtilList.iterator();
    while (localIterator.hasNext())
    {
      K localK;
      if (((localK = (K)localIterator.next()).a(paramDouble1, paramDouble2, paramLivingEntity, paramEntity)) && (localK.a(paramLivingEntity, paramEntity, paramPlayer)))
        return localK.a();
    }
    return E.a;
  }

  public final J a(double paramDouble, LivingEntity paramLivingEntity, Player paramPlayer)
  {
    double d = Math.min(paramDouble, paramLivingEntity.getMaxHealth() - paramLivingEntity.getHealth());
    Collections.shuffle(this.jdField_a_of_type_JavaUtilList, jdField_a_of_type_JavaUtilRandom);
    Iterator localIterator = this.jdField_a_of_type_JavaUtilList.iterator();
    while (localIterator.hasNext())
    {
      K localK;
      if (((localK = (K)localIterator.next()).a(d, paramDouble, paramLivingEntity)) && (localK.a(paramPlayer)))
        return localK.a();
    }
    return E.a;
  }
}

/* Location:           D:\Github\Mechanics\CombatIndicator.jar
 * Qualified Name:     me.coder.combatindicator.L
 * JD-Core Version:    0.6.2
 */