package com.comphenix.protocol.events;

import org.bukkit.plugin.Plugin;

public abstract interface PacketOutputHandler
{
  public abstract ListenerPriority getPriority();

  public abstract Plugin getPlugin();

  public abstract byte[] handle(PacketEvent paramPacketEvent, byte[] paramArrayOfByte);
}

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.events.PacketOutputHandler
 * JD-Core Version:    0.6.2
 */