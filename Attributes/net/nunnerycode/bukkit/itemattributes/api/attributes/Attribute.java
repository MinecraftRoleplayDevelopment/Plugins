package net.nunnerycode.bukkit.itemattributes.api.attributes;

import org.bukkit.Sound;

public abstract interface Attribute
{
  public abstract boolean isEnabled();

  public abstract void setEnabled(boolean paramBoolean);

  public abstract double getMaxValue();

  public abstract void setMaxValue(double paramDouble);

  public abstract boolean isPercentage();

  public abstract void setPercentage(boolean paramBoolean);

  public abstract String getFormat();

  public abstract void setFormat(String paramString);

  public abstract String getName();

  public abstract Sound getSound();

  public abstract void setSound(Sound paramSound);

  public abstract double getBaseValue();

  public abstract void setBaseValue(double paramDouble);
}

/* Location:           D:\Github\Mechanics\ItemAttributes.jar
 * Qualified Name:     net.nunnerycode.bukkit.itemattributes.api.attributes.Attribute
 * JD-Core Version:    0.6.2
 */