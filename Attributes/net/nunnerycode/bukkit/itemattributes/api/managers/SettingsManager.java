package net.nunnerycode.bukkit.itemattributes.api.managers;

import java.util.Set;
import net.nunnerycode.bukkit.itemattributes.api.ItemAttributes;
import net.nunnerycode.bukkit.itemattributes.api.attributes.Attribute;

public abstract interface SettingsManager
{
  public abstract ItemAttributes getPlugin();

  public abstract double getBasePlayerHealth();

  public abstract int getSecondsBetweenHealthUpdates();

  public abstract double getBaseCriticalRate();

  public abstract double getBaseCriticalDamage();

  public abstract double getBaseStunRate();

  public abstract int getBaseStunLength();

  public abstract double getBaseDodgeRate();

  public abstract Attribute getAttribute(String paramString);

  public abstract boolean isItemOnlyDamageSystemEnabled();

  public abstract double getItemOnlyDamageSystemBaseDamage();

  public abstract boolean addAttribute(String paramString, Attribute paramAttribute);

  public abstract boolean removeAttribute(String paramString, Attribute paramAttribute);

  public abstract Set<Attribute> getLoadedAttributes();

  public abstract boolean isPluginCompatible();
}

/* Location:           D:\Github\Mechanics\ItemAttributes.jar
 * Qualified Name:     net.nunnerycode.bukkit.itemattributes.api.managers.SettingsManager
 * JD-Core Version:    0.6.2
 */