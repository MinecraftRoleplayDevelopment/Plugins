package me.coder.combatindicator;

import com.dsh105.holoapi.api.visibility.Visibility;
import java.util.LinkedHashMap;
import org.bukkit.entity.Player;

final class O
  implements Visibility
{
  O(N paramN, Player[] paramArrayOfPlayer)
  {
  }

  public final boolean isVisibleTo(Player paramPlayer, String paramString)
  {
    int i = (paramString = this.a).length;
    for (int j = 0; j < i; j++)
    {
      Object localObject;
      if ((localObject = paramString[j]) == paramPlayer)
        return true;
    }
    return false;
  }

  public final String getSaveKey()
  {
    return null;
  }

  public final LinkedHashMap getDataToSave()
  {
    return null;
  }
}

/* Location:           D:\Github\Mechanics\CombatIndicator.jar
 * Qualified Name:     me.coder.combatindicator.O
 * JD-Core Version:    0.6.2
 */