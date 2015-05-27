package net.nunnerycode.bukkit.itemattributes.api.commands;

import net.nunnerycode.bukkit.itemattributes.api.ItemAttributes;
import se.ranzdo.bukkit.methodcommand.CommandHandler;

public abstract interface ItemAttributesCommand
{
  public abstract CommandHandler getCommandHandler();

  public abstract ItemAttributes getPlugin();
}

/* Location:           D:\Github\Mechanics\ItemAttributes.jar
 * Qualified Name:     net.nunnerycode.bukkit.itemattributes.api.commands.ItemAttributesCommand
 * JD-Core Version:    0.6.2
 */