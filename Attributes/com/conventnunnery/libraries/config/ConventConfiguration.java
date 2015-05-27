package com.conventnunnery.libraries.config;

import java.io.InputStream;
import org.bukkit.configuration.file.FileConfiguration;

public abstract interface ConventConfiguration
{
  public abstract boolean load();

  public abstract boolean save();

  public abstract void setDefaults(InputStream paramInputStream);

  @Deprecated
  public abstract void saveDefaults(InputStream paramInputStream);

  public abstract boolean needToUpdate();

  public abstract boolean backup();

  public abstract String getName();

  public abstract FileConfiguration getFileConfiguration();

  public abstract String getVersion();
}

/* Location:           D:\Github\Mechanics\ItemAttributes.jar
 * Qualified Name:     com.conventnunnery.libraries.config.ConventConfiguration
 * JD-Core Version:    0.6.2
 */