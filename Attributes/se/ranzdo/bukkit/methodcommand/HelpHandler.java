package se.ranzdo.bukkit.methodcommand;

public abstract interface HelpHandler
{
  public abstract String[] getHelpMessage(RegisteredCommand paramRegisteredCommand);

  public abstract String getUsage(RegisteredCommand paramRegisteredCommand);
}

/* Location:           D:\Github\Mechanics\ItemAttributes.jar
 * Qualified Name:     se.ranzdo.bukkit.methodcommand.HelpHandler
 * JD-Core Version:    0.6.2
 */