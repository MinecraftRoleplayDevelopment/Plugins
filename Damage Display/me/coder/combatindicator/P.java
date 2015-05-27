package me.coder.combatindicator;

import com.dsh105.holoapi.HoloAPI;
import com.dsh105.holoapi.api.HoloManager;
import com.dsh105.holoapi.api.Hologram;
import org.bukkit.Location;

final class P
  implements T
{
  private final Hologram a;

  public P(N paramN, Hologram paramHologram)
  {
    this.a = paramHologram;
  }

  public final void a()
  {
    HoloAPI.getManager().stopTracking(this.a);
  }

  public final void a(Location paramLocation)
  {
    this.a.move(paramLocation);
  }

  public final String toString()
  {
    return Integer.toHexString(hashCode());
  }
}

/* Location:           D:\Github\Mechanics\CombatIndicator.jar
 * Qualified Name:     me.coder.combatindicator.P
 * JD-Core Version:    0.6.2
 */