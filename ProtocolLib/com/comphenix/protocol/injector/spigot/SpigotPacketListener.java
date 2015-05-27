package com.comphenix.protocol.injector.spigot;

abstract interface SpigotPacketListener
{
  public abstract Object packetReceived(Object paramObject1, Object paramObject2, Object paramObject3);

  public abstract Object packetQueued(Object paramObject1, Object paramObject2, Object paramObject3);
}

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.injector.spigot.SpigotPacketListener
 * JD-Core Version:    0.6.2
 */