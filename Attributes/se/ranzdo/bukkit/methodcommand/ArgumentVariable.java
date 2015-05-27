package se.ranzdo.bukkit.methodcommand;

import org.bukkit.command.CommandSender;

public abstract interface ArgumentVariable<T>
{
  public abstract T var(CommandSender paramCommandSender, CommandArgument paramCommandArgument, String paramString)
    throws CommandError;
}

/* Location:           D:\Github\Mechanics\ItemAttributes.jar
 * Qualified Name:     se.ranzdo.bukkit.methodcommand.ArgumentVariable
 * JD-Core Version:    0.6.2
 */