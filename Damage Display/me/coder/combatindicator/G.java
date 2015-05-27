package me.coder.combatindicator;

import java.util.List;

public abstract class G
{
  public static final G a = new H();
  public static final G b = new I();

  public F a(Object paramObject)
  {
    if ((paramObject instanceof String))
      return new M((String)paramObject);
    if ((paramObject instanceof List))
      return new L((List)paramObject);
    return null;
  }
}

/* Location:           D:\Github\Mechanics\CombatIndicator.jar
 * Qualified Name:     me.coder.combatindicator.G
 * JD-Core Version:    0.6.2
 */