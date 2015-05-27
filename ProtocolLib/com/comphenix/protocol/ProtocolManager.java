package com.comphenix.protocol;

import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketListener;
import com.comphenix.protocol.injector.PacketConstructor;
import com.comphenix.protocol.reflect.FieldAccessException;
import com.comphenix.protocol.utility.MinecraftVersion;
import com.google.common.collect.ImmutableSet;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Set;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public abstract interface ProtocolManager extends PacketStream
{
  public abstract int getProtocolVersion(Player paramPlayer);

  public abstract void sendServerPacket(Player paramPlayer, PacketContainer paramPacketContainer, boolean paramBoolean)
    throws InvocationTargetException;

  public abstract void recieveClientPacket(Player paramPlayer, PacketContainer paramPacketContainer, boolean paramBoolean)
    throws IllegalAccessException, InvocationTargetException;

  public abstract void broadcastServerPacket(PacketContainer paramPacketContainer);

  public abstract void broadcastServerPacket(PacketContainer paramPacketContainer, Entity paramEntity, boolean paramBoolean);

  public abstract void broadcastServerPacket(PacketContainer paramPacketContainer, Location paramLocation, int paramInt);

  public abstract ImmutableSet<PacketListener> getPacketListeners();

  public abstract void addPacketListener(PacketListener paramPacketListener);

  public abstract void removePacketListener(PacketListener paramPacketListener);

  public abstract void removePacketListeners(Plugin paramPlugin);

  @Deprecated
  public abstract PacketContainer createPacket(int paramInt);

  public abstract PacketContainer createPacket(PacketType paramPacketType);

  @Deprecated
  public abstract PacketContainer createPacket(int paramInt, boolean paramBoolean);

  public abstract PacketContainer createPacket(PacketType paramPacketType, boolean paramBoolean);

  @Deprecated
  public abstract PacketConstructor createPacketConstructor(int paramInt, Object[] paramArrayOfObject);

  public abstract PacketConstructor createPacketConstructor(PacketType paramPacketType, Object[] paramArrayOfObject);

  public abstract void updateEntity(Entity paramEntity, List<Player> paramList)
    throws FieldAccessException;

  public abstract Entity getEntityFromID(World paramWorld, int paramInt)
    throws FieldAccessException;

  public abstract List<Player> getEntityTrackers(Entity paramEntity)
    throws FieldAccessException;

  @Deprecated
  public abstract Set<Integer> getSendingFilters();

  public abstract Set<PacketType> getSendingFilterTypes();

  @Deprecated
  public abstract Set<Integer> getReceivingFilters();

  public abstract Set<PacketType> getReceivingFilterTypes();

  public abstract MinecraftVersion getMinecraftVersion();

  public abstract boolean isClosed();

  public abstract AsynchronousManager getAsynchronousManager();
}

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.ProtocolManager
 * JD-Core Version:    0.6.2
 */