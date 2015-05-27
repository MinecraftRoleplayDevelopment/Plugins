package me.coder.combatindicator;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import org.bukkit.Location;

final class V
  implements T
{
  private Hologram a;

  public final void a()
  {
    this.a.delete();
  }

  public final void a(Location paramLocation)
  {
    this.a.teleport(paramLocation);
  }

  public final String toString()
  {
    return Integer.toHexString(hashCode());
  }
}

/* Location:           D:\Github\Mechanics\CombatIndicator.jar
 * Qualified Name:     me.coder.combatindicator.V
 * JD-Core Version:    0.6.2
 */