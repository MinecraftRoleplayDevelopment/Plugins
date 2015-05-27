package com.comphenix.protocol.injector.server;

import com.comphenix.protocol.events.NetworkMarker;
import java.lang.reflect.InvocationTargetException;
import java.net.Socket;
import java.net.SocketAddress;
import org.bukkit.entity.Player;

public abstract interface SocketInjector
{
  public abstract Socket getSocket()
    throws IllegalAccessException;

  public abstract SocketAddress getAddress()
    throws IllegalAccessException;

  public abstract void disconnect(String paramString)
    throws InvocationTargetException;

  public abstract void sendServerPacket(Object paramObject, NetworkMarker paramNetworkMarker, boolean paramBoolean)
    throws InvocationTargetException;

  public abstract Player getPlayer();

  public abstract Player getUpdatedPlayer();

  public abstract void transferState(SocketInjector paramSocketInjector);

  public abstract void setUpdatedPlayer(Player paramPlayer);
}

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.injector.server.SocketInjector
 * JD-Core Version:    0.6.2
 */