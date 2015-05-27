package com.comphenix.protocol;

import com.comphenix.protocol.events.NetworkMarker;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.injector.netty.WirePacket;
import java.lang.reflect.InvocationTargetException;
import org.bukkit.entity.Player;

public abstract interface PacketStream
{
  public abstract void sendServerPacket(Player paramPlayer, PacketContainer paramPacketContainer)
    throws InvocationTargetException;

  public abstract void sendServerPacket(Player paramPlayer, PacketContainer paramPacketContainer, boolean paramBoolean)
    throws InvocationTargetException;

  public abstract void sendServerPacket(Player paramPlayer, PacketContainer paramPacketContainer, NetworkMarker paramNetworkMarker, boolean paramBoolean)
    throws InvocationTargetException;

  public abstract void sendWirePacket(Player paramPlayer, int paramInt, byte[] paramArrayOfByte)
    throws InvocationTargetException;

  public abstract void sendWirePacket(Player paramPlayer, WirePacket paramWirePacket)
    throws InvocationTargetException;

  public abstract void recieveClientPacket(Player paramPlayer, PacketContainer paramPacketContainer)
    throws IllegalAccessException, InvocationTargetException;

  public abstract void recieveClientPacket(Player paramPlayer, PacketContainer paramPacketContainer, boolean paramBoolean)
    throws IllegalAccessException, InvocationTargetException;

  public abstract void recieveClientPacket(Player paramPlayer, PacketContainer paramPacketContainer, NetworkMarker paramNetworkMarker, boolean paramBoolean)
    throws IllegalAccessException, InvocationTargetException;
}

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.PacketStream
 * JD-Core Version:    0.6.2
 */