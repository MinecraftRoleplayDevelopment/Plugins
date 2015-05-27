package se.ranzdo.bukkit.methodcommand;

import org.bukkit.command.CommandSender;

public abstract interface ExecutableArgument
{
  public abstract Object execute(CommandSender paramCommandSender, Arguments paramArguments)
    throws CommandError;
}

/* Location:           D:\Github\Mechanics\ItemAttributes.jar
 * Qualified Name:     se.ranzdo.bukkit.methodcommand.ExecutableArgument
 * JD-Core Version:    0.6.2
 */