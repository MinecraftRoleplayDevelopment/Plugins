package me.coder.combatindicator;

import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;

public class R
{
  public static Q a(Main paramMain)
  {
    if (Bukkit.getPluginManager().isPluginEnabled("HolographicDisplays"))
      return new U(paramMain);
    if (Bukkit.getPluginManager().isPluginEnabled("HoloAPI"))
      return new N(paramMain);
    String[] arrayOfString = e.c;
    for (int i = 0; i < 2; i++)
    {
      String str = arrayOfString[i];
      paramMain.getLogger().severe(str);
    }
    throw new S((byte)0);
  }
}

/* Location:           D:\Github\Mechanics\CombatIndicator.jar
 * Qualified Name:     me.coder.combatindicator.R
 * JD-Core Version:    0.6.2
 */