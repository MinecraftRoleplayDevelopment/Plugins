package se.ranzdo.bukkit.methodcommand;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({java.lang.annotation.ElementType.METHOD})
public @interface Command
{
  public abstract String description();

  public abstract String identifier();

  public abstract boolean onlyPlayers();

  public abstract String[] permissions();
}

/* Location:           D:\Github\Mechanics\ItemAttributes.jar
 * Qualified Name:     se.ranzdo.bukkit.methodcommand.Command
 * JD-Core Version:    0.6.2
 */