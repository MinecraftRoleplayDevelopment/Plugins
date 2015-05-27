package com.comphenix.protocol.injector.packet;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerOptions;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import java.util.Set;
import org.bukkit.entity.Player;

public abstract interface PacketInjector
{
  public abstract boolean isCancelled(Object paramObject);

  public abstract void setCancelled(Object paramObject, boolean paramBoolean);

  public abstract boolean addPacketHandler(PacketType paramPacketType, Set<ListenerOptions> paramSet);

  public abstract boolean removePacketHandler(PacketType paramPacketType);

  public abstract boolean hasPacketHandler(PacketType paramPacketType);

  public abstract void inputBuffersChanged(Set<PacketType> paramSet);

  public abstract Set<PacketType> getPacketHandlers();

  public abstract PacketEvent packetRecieved(PacketContainer paramPacketContainer, Player paramPlayer, byte[] paramArrayOfByte);

  public abstract void cleanupAll();
}

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.injector.packet.PacketInjector
 * JD-Core Version:    0.6.2
 */