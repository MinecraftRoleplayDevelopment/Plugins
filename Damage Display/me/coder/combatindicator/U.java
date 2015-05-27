package me.coder.combatindicator;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.gmail.filoghost.holographicdisplays.api.VisibilityManager;
import com.gmail.filoghost.holographicdisplays.object.CraftHologram;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.entity.Player;

final class U
  implements Q
{
  private final Main a;

  U(Main paramMain)
  {
    this.a = paramMain;
  }

  private boolean a()
  {
    return (this.a.a().c()) && (Z.b());
  }

  private V a(Location paramLocation, String paramString, Player[] paramArrayOfPlayer)
  {
    Object localObject1 = paramLocation;
    if (a())
      localObject1 = paramLocation.add(0.0D, 1.0D, 0.0D);
    paramLocation = new V((byte)0);
    (localObject2 = (localObject1 = HologramsAPI.createHologram(this.a, (Location)localObject1)).getVisibilityManager()).setVisibleByDefault(false);
    Object localObject5;
    for (localObject5 : paramArrayOfPlayer)
      ((VisibilityManager)localObject2).showTo((Player)localObject5);
    paramArrayOfPlayer = paramString.split("\n");
    Object localObject2 = paramArrayOfPlayer;
    paramArrayOfPlayer = (Player[])localObject1;
    paramString = this;
    if ((a()) && ((paramArrayOfPlayer instanceof CraftHologram)))
      try
      {
        CraftHologram localCraftHologram = (CraftHologram)paramArrayOfPlayer;
        localObject4 = (List)i.a(paramArrayOfPlayer, "lines");
        for (String str3 : localObject2)
          ((List)localObject4).add(new W(paramString, localCraftHologram, str3));
        localCraftHologram.refreshSingleLines();
      }
      catch (ad localad)
      {
        Object localObject4;
        for (String str2 : localObject2)
          paramArrayOfPlayer.appendTextLine(str2);
        localad.printStackTrace();
      }
    else
      for (String str1 : localObject2)
        paramArrayOfPlayer.appendTextLine(str1);
    V.a(paramLocation, (Hologram)localObject1);
    return paramLocation;
  }
}

/* Location:           D:\Github\Mechanics\CombatIndicator.jar
 * Qualified Name:     me.coder.combatindicator.U
 * JD-Core Version:    0.6.2
 */