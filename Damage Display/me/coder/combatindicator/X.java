package me.coder.combatindicator;

import com.herocraftonline.heroes.api.events.HeroRegainHealthEvent;
import com.herocraftonline.heroes.characters.Hero;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

public final class X extends a
{
  public X(Main paramMain)
  {
    super(paramMain);
  }

  @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
  private void a(HeroRegainHealthEvent paramHeroRegainHealthEvent)
  {
    super.a(paramHeroRegainHealthEvent, paramHeroRegainHealthEvent.getAmount(), paramHeroRegainHealthEvent.getHero().getPlayer());
  }
}

/* Location:           D:\Github\Mechanics\CombatIndicator.jar
 * Qualified Name:     me.coder.combatindicator.X
 * JD-Core Version:    0.6.2
 */