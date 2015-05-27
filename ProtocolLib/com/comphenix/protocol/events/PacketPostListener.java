package com.comphenix.protocol.events;

import org.bukkit.plugin.Plugin;

public abstract interface PacketPostListener
{
  public abstract Plugin getPlugin();

  public abstract void onPostEvent(PacketEvent paramPacketEvent);
}

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.events.PacketPostListener
 * JD-Core Version:    0.6.2
 */