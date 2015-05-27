package net.nunnerycode.bukkit.itemattributes.api.managers;

import java.util.List;
import net.nunnerycode.bukkit.itemattributes.api.ItemAttributes;
import org.bukkit.entity.Player;

public abstract interface PermissionsManager
{
  public abstract ItemAttributes getPlugin();

  public abstract List<String> getPermissions();

  public abstract void addPermissions(String[] paramArrayOfString);

  public abstract void removePermissions(String[] paramArrayOfString);

  public abstract boolean hasPermission(Player paramPlayer, String paramString);
}

/* Location:           D:\Github\Mechanics\ItemAttributes.jar
 * Qualified Name:     net.nunnerycode.bukkit.itemattributes.api.managers.PermissionsManager
 * JD-Core Version:    0.6.2
 */