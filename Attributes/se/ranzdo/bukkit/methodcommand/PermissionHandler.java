package se.ranzdo.bukkit.methodcommand;

import org.bukkit.command.CommandSender;

public abstract interface PermissionHandler
{
  public abstract boolean hasPermission(CommandSender paramCommandSender, String[] paramArrayOfString);
}

/* Location:           D:\Github\Mechanics\ItemAttributes.jar
 * Qualified Name:     se.ranzdo.bukkit.methodcommand.PermissionHandler
 * JD-Core Version:    0.6.2
 */