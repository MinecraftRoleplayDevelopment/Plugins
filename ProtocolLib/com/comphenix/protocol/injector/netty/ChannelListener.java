package com.comphenix.protocol.injector.netty;

import com.comphenix.protocol.error.ErrorReporter;
import com.comphenix.protocol.events.NetworkMarker;
import com.comphenix.protocol.events.PacketEvent;

abstract interface ChannelListener
{
  public abstract PacketEvent onPacketSending(Injector paramInjector, Object paramObject, NetworkMarker paramNetworkMarker);

  public abstract PacketEvent onPacketReceiving(Injector paramInjector, Object paramObject, NetworkMarker paramNetworkMarker);

  public abstract boolean hasListener(Class<?> paramClass);

  public abstract boolean hasMainThreadListener(Class<?> paramClass);

  public abstract boolean includeBuffer(Class<?> paramClass);

  public abstract ErrorReporter getReporter();

  public abstract boolean isDebug();
}

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.injector.netty.ChannelListener
 * JD-Core Version:    0.6.2
 */