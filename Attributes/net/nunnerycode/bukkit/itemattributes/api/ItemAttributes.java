package net.nunnerycode.bukkit.itemattributes.api;

import com.conventnunnery.libraries.config.CommentedConventYamlConfiguration;
import net.nunnerycode.bukkit.itemattributes.api.attributes.AttributeHandler;
import net.nunnerycode.bukkit.itemattributes.api.commands.ItemAttributesCommand;
import net.nunnerycode.bukkit.itemattributes.api.listeners.CoreListener;
import net.nunnerycode.bukkit.itemattributes.api.managers.LanguageManager;
import net.nunnerycode.bukkit.itemattributes.api.managers.PermissionsManager;
import net.nunnerycode.bukkit.itemattributes.api.managers.SettingsManager;
import net.nunnerycode.bukkit.itemattributes.api.tasks.HealthUpdateTask;
import net.nunnerycode.java.libraries.cannonball.DebugPrinter;

public abstract interface ItemAttributes
{
  public abstract CoreListener getCoreListener();

  public abstract DebugPrinter getDebugPrinter();

  public abstract CommentedConventYamlConfiguration getConfigYAML();

  public abstract CommentedConventYamlConfiguration getLanguageYAML();

  public abstract CommentedConventYamlConfiguration getPermissionsYAML();

  public abstract LanguageManager getLanguageManager();

  public abstract SettingsManager getSettingsManager();

  public abstract PermissionsManager getPermissionsManager();

  public abstract HealthUpdateTask getHealthUpdateTask();

  public abstract ItemAttributesCommand getItemAttributesCommand();

  public abstract AttributeHandler getAttributeHandler();
}

/* Location:           D:\Github\Mechanics\ItemAttributes.jar
 * Qualified Name:     net.nunnerycode.bukkit.itemattributes.api.ItemAttributes
 * JD-Core Version:    0.6.2
 */