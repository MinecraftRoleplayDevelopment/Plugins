package com.comphenix.protocol.events;

import org.bukkit.plugin.Plugin;

public abstract interface PacketListener
{
  public abstract void onPacketSending(PacketEvent paramPacketEvent);

  public abstract void onPacketReceiving(PacketEvent paramPacketEvent);

  public abstract ListeningWhitelist getSendingWhitelist();

  public abstract ListeningWhitelist getReceivingWhitelist();

  public abstract Plugin getPlugin();
}

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.events.PacketListener
 * JD-Core Version:    0.6.2
 */