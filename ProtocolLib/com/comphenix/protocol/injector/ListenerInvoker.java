package com.comphenix.protocol.injector;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.injector.packet.InterceptWritePacket;

public abstract interface ListenerInvoker
{
  public abstract void invokePacketRecieving(PacketEvent paramPacketEvent);

  public abstract void invokePacketSending(PacketEvent paramPacketEvent);

  @Deprecated
  public abstract int getPacketID(Object paramObject);

  public abstract PacketType getPacketType(Object paramObject);

  public abstract InterceptWritePacket getInterceptWritePacket();

  @Deprecated
  public abstract boolean requireInputBuffer(int paramInt);

  public abstract void unregisterPacketClass(Class<?> paramClass);

  @Deprecated
  public abstract void registerPacketClass(Class<?> paramClass, int paramInt);

  @Deprecated
  public abstract Class<?> getPacketClassFromID(int paramInt, boolean paramBoolean);
}

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.injector.ListenerInvoker
 * JD-Core Version:    0.6.2
 */