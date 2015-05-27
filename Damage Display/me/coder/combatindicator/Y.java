package me.coder.combatindicator;

import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;

public final class Y
{
  private final Main a;

  public Y(Main paramMain)
  {
    this.a = paramMain;
  }

  public final void a()
  {
    PluginManager localPluginManager;
    if ((localPluginManager = Bukkit.getPluginManager()).isPluginEnabled("Heroes"))
      localPluginManager.registerEvents(new X(this.a), this.a);
  }
}

/* Location:           D:\Github\Mechanics\CombatIndicator.jar
 * Qualified Name:     me.coder.combatindicator.Y
 * JD-Core Version:    0.6.2
 */