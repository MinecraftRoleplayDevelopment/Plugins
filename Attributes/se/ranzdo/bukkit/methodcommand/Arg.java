package se.ranzdo.bukkit.methodcommand;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({java.lang.annotation.ElementType.PARAMETER})
public @interface Arg
{
  public abstract String def();

  public abstract String description();

  public abstract String name();

  public abstract String verifiers();
}

/* Location:           D:\Github\Mechanics\ItemAttributes.jar
 * Qualified Name:     se.ranzdo.bukkit.methodcommand.Arg
 * JD-Core Version:    0.6.2
 */