package net.nunnerycode.bukkit.itemattributes.api.managers;

import java.util.List;
import java.util.Map;
import net.nunnerycode.bukkit.itemattributes.api.ItemAttributes;
import org.bukkit.command.CommandSender;

public abstract interface LanguageManager
{
  public abstract Map<String, String> getMessages();

  public abstract void sendMessage(CommandSender paramCommandSender, String paramString);

  public abstract String getMessage(String paramString);

  public abstract ItemAttributes getPlugin();

  public abstract void sendMessage(CommandSender paramCommandSender, String paramString, String[][] paramArrayOfString);

  public abstract String getMessage(String paramString, String[][] paramArrayOfString);

  public abstract List<String> getStringList(String paramString);

  public abstract List<String> getStringList(String paramString, String[][] paramArrayOfString);
}

/* Location:           D:\Github\Mechanics\ItemAttributes.jar
 * Qualified Name:     net.nunnerycode.bukkit.itemattributes.api.managers.LanguageManager
 * JD-Core Version:    0.6.2
 */