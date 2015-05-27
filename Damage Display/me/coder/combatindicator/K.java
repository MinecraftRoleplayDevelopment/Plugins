package me.coder.combatindicator;

import java.util.List;
import java.util.Map;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;

public final class K
{
  private final J jdField_a_of_type_MeCoderCombatindicatorJ;
  private final s jdField_a_of_type_MeCoderCombatindicatorS;
  private final x jdField_a_of_type_MeCoderCombatindicatorX;
  private final double jdField_a_of_type_Double;
  private final double jdField_b_of_type_Double;
  private final double jdField_c_of_type_Double;
  private final double d;
  private final double e;
  private final double f;
  private final double g;
  private final double h;
  private final Permission jdField_a_of_type_OrgBukkitPermissionsPermission;
  private final Permission jdField_b_of_type_OrgBukkitPermissionsPermission;
  private final Permission jdField_c_of_type_OrgBukkitPermissionsPermission;
  private final boolean jdField_a_of_type_Boolean;
  private final boolean jdField_b_of_type_Boolean;
  private final boolean jdField_c_of_type_Boolean;

  public K(J paramJ, Map paramMap)
  {
    this.jdField_a_of_type_MeCoderCombatindicatorJ = paramJ;
    this.jdField_a_of_type_MeCoderCombatindicatorS = ((paramMap != null) && (paramMap.containsKey("visibleTo")) ? s.a((String)paramMap.get("visibleTo")) : s.jdField_a_of_type_MeCoderCombatindicatorS);
    this.jdField_a_of_type_MeCoderCombatindicatorX = ((paramMap != null) && (paramMap.containsKey("source")) ? y.a((List)paramMap.get("source")) : (paramMap.get("source") instanceof String) ? y.a((String)paramMap.get("source")) : null);
    this.jdField_a_of_type_Double = ((paramMap != null) && (paramMap.containsKey("valBiggerEq")) ? ((Number)paramMap.get("valBiggerEq")).doubleValue() : -1.0D);
    this.jdField_b_of_type_Double = ((paramMap != null) && (paramMap.containsKey("valSmallerEq")) ? ((Number)paramMap.get("valSmallerEq")).doubleValue() : 1.7976931348623157E+308D);
    this.jdField_c_of_type_Double = ((paramMap != null) && (paramMap.containsKey("finalValBiggerEq")) ? ((Number)paramMap.get("finalValBiggerEq")).doubleValue() : -1.0D);
    this.d = ((paramMap != null) && (paramMap.containsKey("finalValSmallerEq")) ? ((Number)paramMap.get("finalValSmallerEq")).doubleValue() : 1.7976931348623157E+308D);
    this.e = ((paramMap != null) && (paramMap.containsKey("valBigger")) ? ((Number)paramMap.get("valBigger")).doubleValue() : -1.0D);
    this.f = ((paramMap != null) && (paramMap.containsKey("valSmaller")) ? ((Number)paramMap.get("valSmaller")).doubleValue() : 1.7976931348623157E+308D);
    this.g = ((paramMap != null) && (paramMap.containsKey("finalValBigger")) ? ((Number)paramMap.get("finalValBigger")).doubleValue() : -1.0D);
    this.h = ((paramMap != null) && (paramMap.containsKey("finalValSmaller")) ? ((Number)paramMap.get("finalValSmaller")).doubleValue() : 1.7976931348623157E+308D);
    if ((paramMap != null) && (paramMap.containsKey("permission-see")))
    {
      if ((paramJ = (String)paramMap.get("permission-see")).startsWith("!"))
      {
        this.jdField_a_of_type_OrgBukkitPermissionsPermission = new Permission(paramJ.substring(1));
        this.jdField_a_of_type_Boolean = true;
      }
      else
      {
        this.jdField_a_of_type_OrgBukkitPermissionsPermission = new Permission(paramJ);
        this.jdField_a_of_type_Boolean = false;
      }
    }
    else
    {
      this.jdField_a_of_type_OrgBukkitPermissionsPermission = null;
      this.jdField_a_of_type_Boolean = false;
    }
    if ((paramMap != null) && (paramMap.containsKey("permission-attacker")))
    {
      if ((paramJ = (String)paramMap.get("permission-attacker")).startsWith("!"))
      {
        this.jdField_b_of_type_OrgBukkitPermissionsPermission = new Permission(paramJ.substring(1));
        this.jdField_b_of_type_Boolean = true;
      }
      else
      {
        this.jdField_b_of_type_OrgBukkitPermissionsPermission = new Permission(paramJ);
        this.jdField_b_of_type_Boolean = false;
      }
    }
    else
    {
      this.jdField_b_of_type_OrgBukkitPermissionsPermission = null;
      this.jdField_b_of_type_Boolean = false;
    }
    if ((paramMap != null) && (paramMap.containsKey("permission-who")))
    {
      if ((paramJ = (String)paramMap.get("permission-who")).startsWith("!"))
      {
        this.jdField_c_of_type_OrgBukkitPermissionsPermission = new Permission(paramJ.substring(1));
        this.jdField_c_of_type_Boolean = true;
      }
      else
      {
        this.jdField_c_of_type_OrgBukkitPermissionsPermission = new Permission(paramJ);
        this.jdField_c_of_type_Boolean = false;
      }
    }
    else
    {
      this.jdField_c_of_type_OrgBukkitPermissionsPermission = null;
      this.jdField_c_of_type_Boolean = false;
    }
  }

  public final boolean a(double paramDouble1, double paramDouble2, LivingEntity paramLivingEntity)
  {
    return b(paramDouble2, paramDouble1, paramLivingEntity);
  }

  public final boolean a(Player paramPlayer)
  {
    return b(paramPlayer);
  }

  public final boolean a(double paramDouble1, double paramDouble2, LivingEntity paramLivingEntity, Entity paramEntity)
  {
    if (!b(paramDouble2, paramDouble1, paramLivingEntity))
      return false;
    if ((this.jdField_b_of_type_OrgBukkitPermissionsPermission != null) && (paramEntity != null) && ((paramEntity instanceof Player)) && (!this.jdField_b_of_type_Boolean) && (!((Player)paramEntity).hasPermission(this.jdField_b_of_type_OrgBukkitPermissionsPermission)))
      return false;
    if ((this.jdField_b_of_type_OrgBukkitPermissionsPermission != null) && (paramEntity != null) && ((paramEntity instanceof Player)) && (this.jdField_b_of_type_Boolean) && (((Player)paramEntity).hasPermission(this.jdField_c_of_type_OrgBukkitPermissionsPermission)))
      return false;
    return (this.jdField_a_of_type_MeCoderCombatindicatorX == null) || (this.jdField_a_of_type_MeCoderCombatindicatorX.a(paramEntity));
  }

  public final boolean a(LivingEntity paramLivingEntity, Entity paramEntity, Player paramPlayer)
  {
    if (!b(paramPlayer))
      return false;
    return this.jdField_a_of_type_MeCoderCombatindicatorS.a(paramLivingEntity, paramEntity, paramPlayer);
  }

  private boolean b(double paramDouble1, double paramDouble2, LivingEntity paramLivingEntity)
  {
    if ((this.jdField_c_of_type_OrgBukkitPermissionsPermission != null) && ((paramLivingEntity instanceof Player)) && (!this.jdField_c_of_type_Boolean) && (!((Player)paramLivingEntity).hasPermission(this.jdField_c_of_type_OrgBukkitPermissionsPermission)))
      return false;
    if ((this.jdField_c_of_type_OrgBukkitPermissionsPermission != null) && ((paramLivingEntity instanceof Player)) && (this.jdField_c_of_type_Boolean) && (((Player)paramLivingEntity).hasPermission(this.jdField_c_of_type_OrgBukkitPermissionsPermission)))
      return false;
    if ((paramDouble1 < this.jdField_a_of_type_Double) || (paramDouble1 > this.jdField_b_of_type_Double) || (paramDouble2 < this.jdField_c_of_type_Double) || (paramDouble2 > this.d))
      return false;
    return (paramDouble1 > this.e) && (paramDouble1 < this.f) && (paramDouble2 > this.g) && (paramDouble2 < this.h);
  }

  private boolean b(Player paramPlayer)
  {
    if ((this.jdField_a_of_type_OrgBukkitPermissionsPermission != null) && (!this.jdField_a_of_type_Boolean) && (!paramPlayer.hasPermission(this.jdField_a_of_type_OrgBukkitPermissionsPermission)))
      return false;
    return (this.jdField_a_of_type_OrgBukkitPermissionsPermission == null) || (!this.jdField_a_of_type_Boolean) || (!paramPlayer.hasPermission(this.jdField_a_of_type_OrgBukkitPermissionsPermission));
  }

  public final J a()
  {
    return this.jdField_a_of_type_MeCoderCombatindicatorJ;
  }
}

/* Location:           D:\Github\Mechanics\CombatIndicator.jar
 * Qualified Name:     me.coder.combatindicator.K
 * JD-Core Version:    0.6.2
 */