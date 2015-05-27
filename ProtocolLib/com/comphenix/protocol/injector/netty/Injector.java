package com.comphenix.protocol.injector.netty;

import com.comphenix.protocol.PacketType.Protocol;
import com.comphenix.protocol.events.NetworkMarker;
import org.bukkit.entity.Player;

abstract interface Injector
{
  public abstract int getProtocolVersion();

  public abstract boolean inject();

  public abstract void close();

  public abstract void sendServerPacket(Object paramObject, NetworkMarker paramNetworkMarker, boolean paramBoolean);

  public abstract void recieveClientPacket(Object paramObject);

  public abstract PacketType.Protocol getCurrentProtocol();

  public abstract NetworkMarker getMarker(Object paramObject);

  public abstract void saveMarker(Object paramObject, NetworkMarker paramNetworkMarker);

  public abstract Player getPlayer();

  public abstract void setPlayer(Player paramPlayer);

  public abstract boolean isInjected();

  public abstract boolean isClosed();

  public abstract void setUpdatedPlayer(Player paramPlayer);
}

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.injector.netty.Injector
 * JD-Core Version:    0.6.2
 */