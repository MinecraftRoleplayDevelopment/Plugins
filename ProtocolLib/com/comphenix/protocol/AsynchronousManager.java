package com.comphenix.protocol;

import com.comphenix.protocol.async.AsyncListenerHandler;
import com.comphenix.protocol.error.ErrorReporter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.events.PacketListener;
import java.util.Set;
import org.bukkit.plugin.Plugin;

public abstract interface AsynchronousManager
{
  public abstract AsyncListenerHandler registerAsyncHandler(PacketListener paramPacketListener);

  public abstract void unregisterAsyncHandler(AsyncListenerHandler paramAsyncListenerHandler);

  public abstract void unregisterAsyncHandler(PacketListener paramPacketListener);

  public abstract void unregisterAsyncHandlers(Plugin paramPlugin);

  @Deprecated
  public abstract Set<Integer> getSendingFilters();

  public abstract Set<PacketType> getSendingTypes();

  @Deprecated
  public abstract Set<Integer> getReceivingFilters();

  public abstract Set<PacketType> getReceivingTypes();

  public abstract boolean hasAsynchronousListeners(PacketEvent paramPacketEvent);

  public abstract PacketStream getPacketStream();

  public abstract ErrorReporter getErrorReporter();

  public abstract void cleanupAll();

  public abstract void signalPacketTransmission(PacketEvent paramPacketEvent);

  public abstract void registerTimeoutHandler(PacketListener paramPacketListener);

  public abstract void unregisterTimeoutHandler(PacketListener paramPacketListener);

  public abstract Set<PacketListener> getTimeoutHandlers();

  public abstract Set<PacketListener> getAsyncHandlers();
}

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.AsynchronousManager
 * JD-Core Version:    0.6.2
 */