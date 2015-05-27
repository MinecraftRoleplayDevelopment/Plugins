package me.coder.combatindicator;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.mcstats.Metrics;

public class Main extends JavaPlugin
{
  private static final Random jdField_a_of_type_JavaUtilRandom = new Random();
  private c jdField_a_of_type_MeCoderCombatindicatorC;
  private Q jdField_a_of_type_MeCoderCombatindicatorQ;
  private Y jdField_a_of_type_MeCoderCombatindicatorY;
  private o jdField_a_of_type_MeCoderCombatindicatorO = null;

  public void onEnable()
  {
    Z.a();
    getDataFolder().mkdirs();
    a();
    try
    {
      this.jdField_a_of_type_MeCoderCombatindicatorQ = R.a(this);
    }
    catch (S localS)
    {
      setEnabled(false);
      return;
    }
    if (this.jdField_a_of_type_MeCoderCombatindicatorC.b())
    {
      this.jdField_a_of_type_MeCoderCombatindicatorO = new y(this);
      this.jdField_a_of_type_MeCoderCombatindicatorO.a();
      this.jdField_a_of_type_MeCoderCombatindicatorO.e();
      Bukkit.getScheduler().runTaskLater(this, new h(this), 10L);
    }
    else
    {
      this.jdField_a_of_type_MeCoderCombatindicatorO = new q();
    }
    Object localObject = new g(this);
    Bukkit.getPluginManager().registerEvents((Listener)localObject, this);
    getCommand("combatindicator").setExecutor(new b(this));
    this.jdField_a_of_type_MeCoderCombatindicatorY = new Y(this);
    this.jdField_a_of_type_MeCoderCombatindicatorY.a();
    try
    {
      (localObject = new Metrics(this)).start();
    }
    catch (IOException localIOException)
    {
    }
    if (this.jdField_a_of_type_MeCoderCombatindicatorC.a())
    {
      getLogger().info(e.jdField_a_of_type_JavaLangString);
      new m(this);
    }
    else
    {
      getLogger().warning(e.b);
    }
    this.jdField_a_of_type_MeCoderCombatindicatorO.c();
  }

  public void onDisable()
  {
    this.jdField_a_of_type_MeCoderCombatindicatorC = null;
    this.jdField_a_of_type_MeCoderCombatindicatorQ = null;
    this.jdField_a_of_type_MeCoderCombatindicatorO.d();
    this.jdField_a_of_type_MeCoderCombatindicatorO = null;
  }

  public final String a()
  {
    Object localObject = null;
    this.jdField_a_of_type_MeCoderCombatindicatorC = new c(this);
    this.jdField_a_of_type_MeCoderCombatindicatorC.a();
    String str1;
    try
    {
      this.jdField_a_of_type_MeCoderCombatindicatorC.b();
    }
    catch (d locald)
    {
      String[] arrayOfString = e.jdField_a_of_type_ArrayOfJavaLangString;
      for (int i = 0; i < 4; i++)
      {
        String str2 = arrayOfString[i];
        getLogger().warning(str2.replace("%", locald.getMessage()));
      }
      str1 = locald.getMessage();
      this.jdField_a_of_type_MeCoderCombatindicatorC.c();
    }
    if (this.jdField_a_of_type_MeCoderCombatindicatorO != null)
    {
      this.jdField_a_of_type_MeCoderCombatindicatorO.e();
      if ((!(this.jdField_a_of_type_MeCoderCombatindicatorO instanceof q)) && (!this.jdField_a_of_type_MeCoderCombatindicatorC.b()))
        str1 = ChatColor.RED + "You disabled debugging, but you didn't reload/restart your server! Debug will still be enabled!" + str1;
    }
    return str1;
  }

  public final void a(double paramDouble1, double paramDouble2, LivingEntity paramLivingEntity, Entity paramEntity)
  {
    F localF = this.jdField_a_of_type_MeCoderCombatindicatorC.a();
    if (((paramEntity instanceof Projectile)) && ((((Projectile)paramEntity).getShooter() instanceof Entity)))
      paramEntity = (Entity)((Projectile)paramEntity).getShooter();
    paramEntity = paramEntity;
    a(paramLivingEntity, new i(this, localF, paramDouble1, paramDouble2, paramLivingEntity, paramEntity));
  }

  public final void a(double paramDouble, LivingEntity paramLivingEntity)
  {
    F localF = this.jdField_a_of_type_MeCoderCombatindicatorC.b();
    a(paramLivingEntity, new j(this, localF, paramDouble, paramLivingEntity));
  }

  private void a(LivingEntity paramLivingEntity, k paramk)
  {
    this.jdField_a_of_type_MeCoderCombatindicatorO.f();
    Object localObject1 = (localObject1 = paramLivingEntity.getNearbyEntities(this.jdField_a_of_type_MeCoderCombatindicatorC.a(), this.jdField_a_of_type_MeCoderCombatindicatorC.b(), this.jdField_a_of_type_MeCoderCombatindicatorC.c())).iterator();
    while (((Iterator)localObject1).hasNext())
    {
      Object localObject2;
      if (((localObject2 = (Entity)((Iterator)localObject1).next()) instanceof Player))
      {
        Object localObject4 = paramk;
        Object localObject3 = (Player)localObject2;
        LivingEntity localLivingEntity1 = paramLivingEntity;
        localObject2 = this;
        if (((localObject4 = ((k)localObject4).a((Player)localObject3)) != null) && (!((String)localObject4).equals("")))
        {
          LivingEntity localLivingEntity2 = localLivingEntity1;
          Object localObject5 = localObject2;
          localObject5 = localLivingEntity1.getEyeLocation().add(jdField_a_of_type_JavaUtilRandom.nextFloat() - 0.5F, ((localLivingEntity2 instanceof Player)) || (Z.b(localLivingEntity2) != null) ? ((Main)localObject5).jdField_a_of_type_MeCoderCombatindicatorC.b() : ((Main)localObject5).jdField_a_of_type_MeCoderCombatindicatorC.a(), jdField_a_of_type_JavaUtilRandom.nextFloat() - 0.5D);
          localObject3 = ((Main)localObject2).jdField_a_of_type_MeCoderCombatindicatorQ.a((Location)localObject5, (String)localObject4, new Player[] { localObject3 });
          new l((Main)localObject2, (T)localObject3, (Location)localObject5, localLivingEntity1, (byte)0);
          ((Main)localObject2).jdField_a_of_type_MeCoderCombatindicatorO.a((T)localObject3);
        }
      }
    }
  }

  public final o a()
  {
    return this.jdField_a_of_type_MeCoderCombatindicatorO;
  }

  public final c a()
  {
    return this.jdField_a_of_type_MeCoderCombatindicatorC;
  }
}

/* Location:           D:\Github\Mechanics\CombatIndicator.jar
 * Qualified Name:     me.coder.combatindicator.Main
 * JD-Core Version:    0.6.2
 */