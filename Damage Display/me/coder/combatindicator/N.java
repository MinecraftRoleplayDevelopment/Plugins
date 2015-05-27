package me.coder.combatindicator;

import com.dsh105.holoapi.api.HologramFactory;
import org.bukkit.Location;
import org.bukkit.entity.Player;

class N
  implements Q
{
  private final Main a;

  N(Main paramMain)
  {
    this.a = paramMain;
  }

  public final T a(Location paramLocation, String paramString, Player[] paramArrayOfPlayer)
  {
    paramArrayOfPlayer = new O(this, paramArrayOfPlayer);
    paramLocation = new HologramFactory(this.a).withLocation(paramLocation).withSimplicity(true).withVisibility(paramArrayOfPlayer).withText(paramString.split("\n")).build();
    return new P(this, paramLocation);
  }
}

/* Location:           D:\Github\Mechanics\CombatIndicator.jar
 * Qualified Name:     me.coder.combatindicator.N
 * JD-Core Version:    0.6.2
 */