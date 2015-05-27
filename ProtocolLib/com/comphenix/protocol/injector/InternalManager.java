package com.comphenix.protocol.injector;

import com.comphenix.protocol.ProtocolManager;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

public abstract interface InternalManager extends ProtocolManager
{
  public abstract PacketFilterManager.PlayerInjectHooks getPlayerHook();

  public abstract void setPlayerHook(PacketFilterManager.PlayerInjectHooks paramPlayerInjectHooks);

  public abstract void registerEvents(PluginManager paramPluginManager, Plugin paramPlugin);

  public abstract void close();

  public abstract boolean isDebug();

  public abstract void setDebug(boolean paramBoolean);
}

/* Location:           D:\Github\Mechanics\ProtocolLib.jar
 * Qualified Name:     com.comphenix.protocol.injector.InternalManager
 * JD-Core Version:    0.6.2
 */