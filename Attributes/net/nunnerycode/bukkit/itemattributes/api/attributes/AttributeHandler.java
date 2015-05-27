package net.nunnerycode.bukkit.itemattributes.api.attributes;

import java.util.List;
import java.util.Set;
import net.nunnerycode.bukkit.itemattributes.api.ItemAttributes;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;

public abstract interface AttributeHandler
{
  public abstract double getAttributeValueFromItemStack(ItemStack paramItemStack, Attribute paramAttribute);

  public abstract double getAttributeValueFromEntity(LivingEntity paramLivingEntity, Attribute paramAttribute);

  public abstract Set<Attribute> getAttributesPresentOnItemStack(ItemStack paramItemStack);

  public abstract boolean hasAttributeOnItemStack(ItemStack paramItemStack, Attribute paramAttribute);

  public abstract boolean hasAttributeOnEntity(LivingEntity paramLivingEntity, Attribute paramAttribute);

  public abstract ItemAttributes getPlugin();

  public abstract List<String> getAttributeStringsFromItemStack(ItemStack paramItemStack, Attribute paramAttribute);

  public abstract List<String> getAttributeStringsFromEntity(LivingEntity paramLivingEntity, Attribute paramAttribute);
}

/* Location:           D:\Github\Mechanics\ItemAttributes.jar
 * Qualified Name:     net.nunnerycode.bukkit.itemattributes.api.attributes.AttributeHandler
 * JD-Core Version:    0.6.2
 */