package se.ranzdo.bukkit.methodcommand;

import org.bukkit.command.CommandSender;

public abstract interface ArgumentVerifier<T>
{
  public abstract void verify(CommandSender paramCommandSender, CommandArgument paramCommandArgument, String paramString1, String[] paramArrayOfString, T paramT, String paramString2)
    throws VerifyError;
}

/* Location:           D:\Github\Mechanics\ItemAttributes.jar
 * Qualified Name:     se.ranzdo.bukkit.methodcommand.ArgumentVerifier
 * JD-Core Version:    0.6.2
 */